package tr.improsy.kafkalab.k11;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import tr.improsy.kafkalab.common.Lab;

import java.util.Map;
import java.util.Properties;

/**
 * 1.1 — Log segment ve index labı.
 *
 * segment.bytes'ı izin verilen en küçük değere (1 MiB) çekilmiş tek partition'lık bir topic'e
 * yeterince veri yazar ki broker segment'i "roll" etmek zorunda kalsın. Ardından log dizinine
 * bakınca birden fazla .log/.index/.timeindex üçlüsü görürsün.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k11.SegmentDemo
 */
public class SegmentDemo {

    static final String TOPIC = "lab-segment";
    static final int RECORDS = 4000;
    static final int VALUE_BYTES = 800;

    public static void main(String[] args) {
        // KIP-1030 (Kafka 4.0): segment.bytes'ın alt sınırı 1 MiB'e çıkarıldı. Daha küçüğünü
        // verirsen broker InvalidConfigurationException fırlatır — bilerek en küçük değeri kullanıyoruz.
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of(
                "segment.bytes", "1048576",       // 1 MiB — izin verilen minimum
                "segment.index.bytes", "1024",    // 1 KiB — izin verilen minimum (KIP-1030)
                "index.interval.bytes", "1024",   // her ~1 KiB'de bir index girdisi (varsayılan 4096)
                "retention.ms", "3600000"
        ));

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");   // segment boyutunu tahmin edebilelim
        p.put(ProducerConfig.LINGER_MS_CONFIG, 20);

        String value = "x".repeat(VALUE_BYTES);
        long firstOffset = -1, lastOffset = -1;

        Lab.banner(RECORDS + " kayıt yazılıyor (~" + (RECORDS * VALUE_BYTES / 1024 / 1024) + " MiB)");
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < RECORDS; i++) {
                RecordMetadata md = producer.send(
                        new ProducerRecord<>(TOPIC, "key-" + (i % 10), i + ":" + value)).get();
                if (i == 0) firstOffset = md.offset();
                lastOffset = md.offset();
            }
        } catch (Exception e) {
            throw new IllegalStateException("üretim başarısız", e);
        }

        System.out.printf("ilk offset=%d, son offset=%d%n", firstOffset, lastOffset);
        System.out.println("""

                Şimdi diske bak:
                  docker exec kafka-1 bash -c 'ls -la /var/lib/kafka/data/lab-segment-0/'
                Segment içeriğini oku:
                  docker exec kafka-1 /opt/kafka/bin/kafka-dump-log.sh \\
                    --files /var/lib/kafka/data/lab-segment-0/00000000000000000000.log --print-data-log | head -20
                Index'i oku:
                  docker exec kafka-1 /opt/kafka/bin/kafka-dump-log.sh \\
                    --files /var/lib/kafka/data/lab-segment-0/00000000000000000000.index | head -10
                """);
    }
}
