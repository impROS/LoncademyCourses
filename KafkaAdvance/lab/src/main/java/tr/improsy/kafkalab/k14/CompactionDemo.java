package tr.improsy.kafkalab.k14;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;

/**
 * 1.4 — Log compaction ve tombstone labı.
 *
 * 10 key'e binlerce güncelleme yazar, sonra bir key için tombstone (value=null) gönderir.
 * Cleaner çalıştıktan sonra log'u yeniden okuyup her key için kaç kayıt kaldığını gösterir.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k14.CompactionDemo
 */
public class CompactionDemo {

    static final String TOPIC = "lab-compact";
    static final int KEYS = 10;
    static final int UPDATES_PER_KEY = 2000;
    static final int VALUE_BYTES = 500;

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of(
                "cleanup.policy", "compact",
                "segment.bytes", "1048576",            // 1 MiB — minimum; segment sık dönsün
                "min.cleanable.dirty.ratio", "0.01",   // cleaner'ı neredeyse her fırsatta çalıştır
                "min.compaction.lag.ms", "0",
                "delete.retention.ms", "100",          // tombstone'lar hızlı yok olsun
                "segment.ms", "60000"                  // minimum 1 dakika (KIP-1030)
        ));

        Properties pp = new Properties();
        pp.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        pp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        pp.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        pp.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");

        String filler = "v".repeat(VALUE_BYTES);
        Lab.banner((KEYS * UPDATES_PER_KEY) + " güncelleme yazılıyor (" + KEYS + " key)");
        try (Producer<String, String> producer = new KafkaProducer<>(pp)) {
            for (int i = 0; i < UPDATES_PER_KEY; i++) {
                for (int k = 0; k < KEYS; k++) {
                    producer.send(new ProducerRecord<>(TOPIC, "key-" + k, "sürüm-" + i + "-" + filler));
                }
            }
            producer.flush();
            // key-0 için tombstone: value = null → "bu key silindi"
            producer.send(new ProducerRecord<String, String>(TOPIC, "key-0", null));
            producer.flush();
            System.out.println("tombstone yazıldı: key-0 → null");
        }

        report("COMPACTION ÖNCESİ");

        int waitSec = Integer.getInteger("waitSec", 90);
        System.out.println("\nCleaner'ın çalışması bekleniyor (" + waitSec + " sn)...");
        System.out.println("Bu sırada başka terminalde segmentleri izleyebilirsin:");
        System.out.println("  watch -n5 \"docker exec kafka-1 ls -1 /var/lib/kafka/data/lab-compact-0/\"");
        Lab.sleep(waitSec * 1000L);

        report("COMPACTION SONRASI");
        System.out.println("\nTemizlik:  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh "
                + "--bootstrap-server kafka-1:19092 --delete --topic " + TOPIC);
    }

    /** Topic'i baştan okur; key başına kaç kayıt kaldığını ve toplamı yazdırır. */
    static void report(String title) {
        Properties cp = new Properties();
        cp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        cp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cp.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        cp.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        cp.put(ConsumerConfig.GROUP_ID_CONFIG, "compaction-report-" + UUID.randomUUID());

        Map<String, Integer> perKey = new TreeMap<>();
        int total = 0, tombstones = 0;
        TopicPartition tp = new TopicPartition(TOPIC, 0);

        try (Consumer<String, String> consumer = new KafkaConsumer<>(cp)) {
            consumer.assign(List.of(tp));
            consumer.seekToBeginning(List.of(tp));
            long end = consumer.endOffsets(List.of(tp)).get(tp);
            long beginning = consumer.beginningOffsets(List.of(tp)).get(tp);

            int emptyPolls = 0;
            while (consumer.position(tp) < end && emptyPolls < 5) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                if (records.isEmpty()) { emptyPolls++; continue; }
                emptyPolls = 0;
                for (ConsumerRecord<String, String> r : records) {
                    total++;
                    perKey.merge(r.key(), 1, Integer::sum);
                    if (r.value() == null) tombstones++;
                }
            }

            Lab.banner(title);
            System.out.printf("log başlangıç offset=%d, son offset=%d, okunan kayıt=%d, tombstone=%d%n",
                    beginning, end, total, tombstones);
            perKey.forEach((k, v) -> System.out.printf("  %-8s %d kayıt%n", k, v));
        }
    }
}
