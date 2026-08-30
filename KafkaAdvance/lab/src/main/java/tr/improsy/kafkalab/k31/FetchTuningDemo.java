package tr.improsy.kafkalab.k31;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;

/**
 * 3.1 — Fetch ayarlarının tüketim hızına etkisini ölçer.
 *
 * Topic'i bir kez doldurur, sonra aynı veriyi farklı fetch yapılandırmalarıyla baştan okur.
 * Her koşumda: toplam süre, poll başına ortalama kayıt, kayıt/sn.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k31.FetchTuningDemo
 */
public class FetchTuningDemo {

    static final String TOPIC = "lab-fetch";
    static final int RECORDS = 200_000;
    static final int VALUE_BYTES = 300;

    record Setup(String name, int fetchMinBytes, int fetchMaxWaitMs, int maxPollRecords, int maxPartitionFetchBytes) {}

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 3, (short) 3, Map.of("retention.ms", "1800000"));
        fillOnce();

        List<Setup> setups = List.of(
                //                              fetch.min  fetch.max.wait  max.poll.records  max.partition.fetch.bytes
                new Setup("varsayılan",              1,           500,            500,            1_048_576),
                new Setup("max.poll.records=10",     1,           500,             10,            1_048_576),
                new Setup("max.poll.records=5000",   1,           500,           5000,            1_048_576),
                new Setup("fetch.min=1MB",   1_048_576,           500,            500,            1_048_576),
                new Setup("partition.fetch=10MB",    1,           500,           5000,           10_485_760)
        );

        System.out.printf("%n%-26s %10s %12s %14s %12s%n",
                "kurulum", "süre(ms)", "poll sayısı", "kayıt/poll", "kayıt/sn");
        System.out.println("-".repeat(80));
        for (Setup s : setups) consume(s);

        System.out.println("""

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-fetch
                """);
    }

    /** Topic zaten doluysa tekrar yazmaz. */
    static void fillOnce() {
        Properties cp = new Properties();
        cp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        cp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cp.put(ConsumerConfig.GROUP_ID_CONFIG, "fill-check");
        long existing = 0;
        List<TopicPartition> tps = new ArrayList<>();
        for (int i = 0; i < 3; i++) tps.add(new TopicPartition(TOPIC, i));
        try (Consumer<String, String> c = new KafkaConsumer<>(cp)) {
            for (long v : c.endOffsets(tps).values()) existing += v;
        }
        if (existing >= RECORDS) {
            System.out.println("[lab] topic zaten dolu (" + existing + " kayıt), yazma atlandı");
            return;
        }

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        p.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536);
        p.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        String value = "x".repeat(VALUE_BYTES);
        Lab.banner(RECORDS + " kayıt yazılıyor (bir kez)");
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < RECORDS; i++)
                producer.send(new ProducerRecord<>(TOPIC, "k-" + (i % 1000), value));
        }
    }

    static void consume(Setup s) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "fetch-demo-" + UUID.randomUUID());
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        p.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, s.fetchMinBytes());
        p.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, s.fetchMaxWaitMs());
        p.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, s.maxPollRecords());
        p.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, s.maxPartitionFetchBytes());

        List<TopicPartition> tps = new ArrayList<>();
        for (int i = 0; i < 3; i++) tps.add(new TopicPartition(TOPIC, i));

        long consumed = 0, polls = 0;
        long t0 = System.nanoTime();
        try (Consumer<String, String> consumer = new KafkaConsumer<>(p)) {
            consumer.assign(tps);
            consumer.seekToBeginning(tps);
            long target = consumer.endOffsets(tps).values().stream().mapToLong(Long::longValue).sum();
            int empty = 0;
            while (consumed < target && empty < 3) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                polls++;
                if (records.isEmpty()) { empty++; continue; }
                empty = 0;
                consumed += records.count();
            }
        }
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("%-26s %10d %12d %14.1f %12.0f%n",
                s.name(), ms, polls, (double) consumed / Math.max(polls, 1),
                consumed * 1000.0 / Math.max(ms, 1));
    }
}
