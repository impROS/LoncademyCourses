package tr.improsy.kafkalab.k24;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;

/**
 * 2.4 — Şema evrimi labı.
 *
 * Aynı topic'e v1, v2 ve v3 sürümleriyle yazar; sonra iki farklı tüketiciyle okur:
 *   - TOLERANSLI tüketici : hepsini okur (eksik alan → varsayılan, fazla alan → atlanır)
 *   - KATI tüketici (v1)  : v2/v3 kayıtlarında SerializationException ile durur
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k24.SchemaEvolutionDemo
 */
public class SchemaEvolutionDemo {

    static final String TOPIC = "lab-schema";

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of("retention.ms", "600000"));

        Lab.banner("Üç sürümle yazılıyor");
        write(1, OrderEvent.v1(1001, 25_000));
        write(2, OrderEvent.v2(1002, 30_000, "EUR"));
        write(3, OrderEvent.v3(1003, 45_000, "USD", "cust-42"));

        Lab.banner("TOLERANSLI tüketici (v3'ü bilir)");
        read(new OrderCodec.TolerantOrderDeserializer(3));

        Lab.banner("KATI tüketici (yalnızca v1 kabul eder)");
        read(new OrderCodec.StrictOrderDeserializer(1));

        System.out.println("""

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-schema
                """);
    }

    static void write(int version, OrderEvent event) {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        try (Producer<Long, OrderEvent> producer =
                     new KafkaProducer<>(p, new LongSerializer(), new OrderCodec.OrderSerializer(version))) {
            producer.send(new ProducerRecord<>(TOPIC, event.orderId(), event));
            producer.flush();
            System.out.printf("  v%d yazıldı: %s%n", version, event);
        }
    }

    static void read(org.apache.kafka.common.serialization.Deserializer<OrderEvent> deserializer) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "schema-demo-" + UUID.randomUUID());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        TopicPartition tp = new TopicPartition(TOPIC, 0);
        try (Consumer<Long, OrderEvent> consumer =
                     new KafkaConsumer<>(p, new LongDeserializer(), deserializer)) {
            consumer.assign(List.of(tp));
            consumer.seekToBeginning(List.of(tp));
            int read = 0;
            for (int attempt = 0; attempt < 5 && read < 3; attempt++) {
                try {
                    for (ConsumerRecord<Long, OrderEvent> r : consumer.poll(Duration.ofSeconds(2))) {
                        System.out.printf("  offset=%d → %s%n", r.offset(), r.value());
                        read++;
                    }
                } catch (SerializationException e) {
                    // Deserializer hatası: consumer'ın pozisyonu ilerlemez → aynı kayıtta sonsuza kadar
                    // takılırsın. Bilerek gösteriyoruz; çözümü konu dosyasında.
                    System.out.println("  ✗ HATA: " + e.getMessage());
                    System.out.println("    → position=" + consumer.position(tp)
                            + " (ilerlemedi; zehirli kayıt tüketiciyi kilitler)");
                    break;
                }
            }
        }
    }
}
