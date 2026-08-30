package tr.improsy.kafkalab.k22;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringSerializer;
import tr.improsy.kafkalab.common.Lab;

import java.util.Map;
import java.util.Properties;

/**
 * 2.2 — Idempotent producer labı.
 *
 * 1) Idempotence ile uyumsuz ayarların client tarafında nasıl reddedildiğini gösterir.
 * 2) İki ayrı producer oturumu açıp aynı partition'a yazar; dump-log çıktısında
 *    producerId'nin değiştiğini ve sequence'ın sıfırdan başladığını görürsün.
 *    Ders: idempotence garantisi OTURUM BAŞINADIR — transactional.id olmadan
 *    uygulama restart'ı duplicate'e karşı korumaz.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k22.IdempotenceDemo
 */
public class IdempotenceDemo {

    static final String TOPIC = "lab-idem";

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of("retention.ms", "600000"));

        Lab.banner("1) Idempotence ile uyumsuz ayarlar");
        tryBuild("max.in.flight = 6 (idempotence açık)", base(p -> {
            p.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 6);
        }));
        tryBuild("acks = 1 (idempotence açık)", base(p -> {
            p.put(ProducerConfig.ACKS_CONFIG, "1");
        }));
        tryBuild("retries = 0 (idempotence açık)", base(p -> {
            p.put(ProducerConfig.RETRIES_CONFIG, 0);
        }));
        tryBuild("varsayılan idempotent yapılandırma", base(p -> {}));

        Lab.banner("2) İki ayrı producer oturumu");
        sendBatch("oturum-A");
        sendBatch("oturum-B");

        System.out.println("""

                Şimdi log'a bak — iki farklı producerId ve her birinde sequence 0'dan başlıyor:
                  docker exec kafka-1 /opt/kafka/bin/kafka-dump-log.sh \\
                    --files /var/lib/kafka/data/lab-idem-0/00000000000000000000.log \\
                    --print-data-log | grep -E "producerId|payload" | head -20

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-idem
                """);
    }

    /** Idempotent varsayılanlarla başlayan bir yapılandırma üretir. */
    static Properties base(java.util.function.Consumer<Properties> tweak) {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        tweak.accept(p);
        return p;
    }

    /** Producer'ı kurmayı dener; reddedilirse hatayı yazdırır. */
    static void tryBuild(String label, Properties p) {
        try (Producer<String, String> ignored = new KafkaProducer<>(p)) {
            System.out.printf("  %-42s → KABUL%n", label);
        } catch (KafkaException e) {
            System.out.printf("  %-42s → RED: %s%n", label, e.getMessage());
        }
    }

    /** Aynı partition'a 3 kayıt yazar. */
    static void sendBatch(String session) {
        try (Producer<String, String> producer = new KafkaProducer<>(base(p -> {}))) {
            for (int i = 0; i < 3; i++) {
                producer.send(new ProducerRecord<>(TOPIC, "k", session + "-kayit-" + i));
            }
            producer.flush();
            System.out.println("  " + session + ": 3 kayıt yazıldı");
        }
    }
}
