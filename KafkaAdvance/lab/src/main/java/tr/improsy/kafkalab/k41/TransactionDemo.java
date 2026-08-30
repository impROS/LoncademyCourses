package tr.improsy.kafkalab.k41;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;

/**
 * 4.1 — Transaction ve isolation.level labı.
 *
 * 1) Bir transaction commit edilir, bir transaction ABORT edilir.
 * 2) Aynı topic iki farklı isolation.level ile okunur:
 *      read_uncommitted → abort edilmiş kayıtlar da GÖRÜNÜR
 *      read_committed   → yalnızca commit edilenler görünür
 * 3) Log'da control record'ları (commit/abort marker) gösterir.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k41.TransactionDemo
 */
public class TransactionDemo {

    static final String TOPIC = "lab-tx";

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of(
                "min.insync.replicas", "2", "retention.ms", "600000"));

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // transactional.id KALICI bir kimliktir: restart'ta coordinator eski durumu bulur (2.2)
        p.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "lab-tx-producer-1");

        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            producer.initTransactions();     // coordinator'ı bul, epoch al, yarım tx'leri temizle

            Lab.banner("1) COMMIT edilen transaction");
            producer.beginTransaction();
            for (int i = 0; i < 3; i++)
                producer.send(new ProducerRecord<>(TOPIC, "k", "COMMIT-" + i));
            producer.commitTransaction();
            System.out.println("  3 kayıt commit edildi");

            Lab.banner("2) ABORT edilen transaction");
            producer.beginTransaction();
            for (int i = 0; i < 3; i++)
                producer.send(new ProducerRecord<>(TOPIC, "k", "ABORT-" + i));
            // flush() olmadan abort edersek batch'ler hiç gönderilmez ve log'a yazılmazlar.
            // Gerçek bir çöküş senaryosunu taklit etmek için önce broker'a gönderiyoruz.
            producer.flush();
            producer.abortTransaction();
            System.out.println("  3 kayıt abort edildi (yine de LOG'A YAZILDI)");

            Lab.banner("3) Tekrar COMMIT");
            producer.beginTransaction();
            producer.send(new ProducerRecord<>(TOPIC, "k", "COMMIT-3"));
            producer.commitTransaction();
            System.out.println("  1 kayıt commit edildi");
        }

        read("read_uncommitted");
        read("read_committed");

        System.out.println("""

                Log'daki control record'ları gör (isControl: true satırları):
                  docker exec kafka-1 /opt/kafka/bin/kafka-dump-log.sh \\
                    --files /var/lib/kafka/data/lab-tx-0/00000000000000000000.log \\
                    --print-data-log | grep -E "isTransactional|endTxnMarker"

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-tx
                """);
    }

    static void read(String isolation) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "tx-reader-" + UUID.randomUUID());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        p.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolation);

        TopicPartition tp = new TopicPartition(TOPIC, 0);
        Lab.banner("OKUMA · isolation.level = " + isolation);
        try (Consumer<String, String> c = new KafkaConsumer<>(p)) {
            c.assign(List.of(tp));
            c.seekToBeginning(List.of(tp));
            int empty = 0, n = 0;
            while (empty < 3) {
                ConsumerRecords<String, String> records = c.poll(Duration.ofSeconds(2));
                if (records.isEmpty()) { empty++; continue; }
                empty = 0;
                for (ConsumerRecord<String, String> r : records) {
                    System.out.printf("  offset=%-3d %s%n", r.offset(), r.value());
                    n++;
                }
            }
            System.out.println("  toplam " + n + " kayıt · log sonu offset=" + c.endOffsets(List.of(tp)).get(tp));
        }
    }
}
