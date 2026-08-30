package tr.improsy.kafkalab.k42;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * 4.2 — Idempotent tüketici labı.
 *
 * Kafka → dış sistem (burada SinkStore) akışında EOS YOKTUR. Duplicate kaçınılmazdır.
 * Bu lab iki tüketiciyi karşılaştırır:
 *   NAİF        : her kaydı uygular → duplicate'ler bakiyeyi bozar
 *   IDEMPOTENT  : message-id ile tekilleştirir → duplicate'ler emilir
 *
 * Ayrıca offset'i hedef sistemde (iş verisiyle aynı atomik blokta) tutar.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k42.IdempotentConsumerDemo
 */
public class IdempotentConsumerDemo {

    static final String TOPIC = "lab-idempotent-sink";
    static final int UNIQUE = 100;      // benzersiz iş sayısı
    static final int DUPLICATES = 30;   // tekrar gönderilen iş sayısı
    static final long AMOUNT = 10;

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of("retention.ms", "600000"));
        int written = seed();

        Lab.banner("Yazılan kayıt: " + written + " (benzersiz iş: " + UNIQUE + ")");
        System.out.println("  Beklenen doğru toplam bakiye: " + (UNIQUE * AMOUNT));

        SinkStore naive = new SinkStore();
        SinkStore idempotent = new SinkStore();
        consume(naive, idempotent);

        Lab.banner("SONUÇ");
        System.out.printf("  NAİF       toplam bakiye = %-6d %s%n",
                naive.totalBalance(), naive.totalBalance() == UNIQUE * AMOUNT ? "✓" : "✗ BOZUK");
        System.out.printf("  IDEMPOTENT toplam bakiye = %-6d %s  (işlenen benzersiz iş: %d)%n",
                idempotent.totalBalance(),
                idempotent.totalBalance() == UNIQUE * AMOUNT ? "✓" : "✗ BOZUK",
                idempotent.processedCount());
        System.out.println("  Idempotent store'un kaydettiği offset: "
                + idempotent.offsetOf(new TopicPartition(TOPIC, 0)));

        System.out.println("""

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-idempotent-sink
                """);
    }

    /** Her kayda benzersiz bir message-id header'ı koyar; bazı kayıtları BİLEREK iki kez yazar. */
    static int seed() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        int written = 0;
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < UNIQUE; i++) {
                send(producer, i); written++;
                // İlk DUPLICATES kadar işi ikinci kez gönder — "at-least-once" gerçeği
                if (i < DUPLICATES) { send(producer, i); written++; }
            }
        }
        return written;
    }

    static void send(Producer<String, String> producer, int i) {
        String account = "acct-" + (i % 10);
        ProducerRecord<String, String> record =
                new ProducerRecord<>(TOPIC, account, String.valueOf(AMOUNT));
        // message-id: iş kimliği. Aynı iş iki kez gönderilse de id AYNI kalır.
        record.headers().add(new RecordHeader("message-id",
                ("job-" + i).getBytes(StandardCharsets.UTF_8)));
        producer.send(record);
    }

    static void consume(SinkStore naive, SinkStore idempotent) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "idempotent-demo-" + UUID.randomUUID());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        TopicPartition tp = new TopicPartition(TOPIC, 0);
        try (Consumer<String, String> consumer = new KafkaConsumer<>(p)) {
            consumer.assign(List.of(tp));
            // Offset'i KAFKA'dan değil HEDEF SİSTEMDEN oku (varsa)
            Long stored = idempotent.offsetOf(tp);
            if (stored != null) consumer.seek(tp, stored); else consumer.seekToBeginning(List.of(tp));

            int empty = 0;
            while (empty < 3) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                if (records.isEmpty()) { empty++; continue; }
                empty = 0;
                for (ConsumerRecord<String, String> r : records) {
                    String account = r.key();
                    long amount = Long.parseLong(r.value());
                    naive.applyNaive(account, amount);
                    idempotent.applyAtomically(messageId(r), account, amount, tp, r.offset() + 1);
                }
            }
        }
    }

    static String messageId(ConsumerRecord<String, String> r) {
        Header h = r.headers().lastHeader("message-id");
        // Header yoksa geri düşüş: topic-partition-offset de tekil bir kimliktir
        return h != null ? new String(h.value(), StandardCharsets.UTF_8)
                : r.topic() + "-" + r.partition() + "-" + r.offset();
    }
}
