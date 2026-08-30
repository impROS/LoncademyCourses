package tr.improsy.kafkalab.k21;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.StringSerializer;
import tr.improsy.kafkalab.common.Lab;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 2.1 — Batching ve sıkıştırmanın throughput'a etkisini ÖLÇEN lab.
 *
 * Aynı yükü farklı (linger.ms, batch.size, compression.type) kombinasyonlarıyla gönderir ve
 * her koşum için producer'ın kendi metriklerini yazdırır:
 *   batch-size-avg           → gerçekten oluşan ortalama batch boyutu (bayt)
 *   records-per-request-avg  → istek başına kaç kayıt gitti
 *   compression-rate-avg     → sıkıştırma oranı (1.0 = sıkıştırma yok)
 *   request-latency-avg      → broker'a giden isteğin ortalama gecikmesi (ms)
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k21.BatchingBenchmark
 */
public class BatchingBenchmark {

    static final String TOPIC = "lab-batching";
    static final int RECORDS = 200_000;
    static final int VALUE_BYTES = 200;

    record Setup(String name, int lingerMs, int batchSize, String compression) {}

    public static void main(String[] args) {
        Lab.ensureTopic(TOPIC, 6, (short) 3, Map.of("retention.ms", "600000"));

        List<Setup> setups = List.of(
                new Setup("linger=0,  batch=16K, none", 0, 16384, "none"),
                new Setup("linger=5,  batch=16K, none", 5, 16384, "none"),   // Kafka 4.x varsayılanı
                new Setup("linger=50, batch=64K, none", 50, 65536, "none"),
                new Setup("linger=50, batch=64K, lz4", 50, 65536, "lz4"),
                new Setup("linger=50, batch=64K, zstd", 50, 65536, "zstd")
        );

        System.out.printf("%n%-28s %9s %11s %11s %10s %8s %9s%n",
                "kurulum", "süre(ms)", "kayıt/sn", "MB/sn", "batch(B)", "kyt/req", "sıkışt.");
        System.out.println("-".repeat(96));

        for (Setup s : setups) {
            run(s);
        }

        System.out.println("""

                Not: ilk koşum JIT/bağlantı ısınması yüzünden dezavantajlıdır; sonuçları
                ikinci kez çalıştırıp karşılaştır. Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-batching
                """);
    }

    static void run(Setup s) {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.ACKS_CONFIG, "all");
        p.put(ProducerConfig.LINGER_MS_CONFIG, s.lingerMs());
        p.put(ProducerConfig.BATCH_SIZE_CONFIG, s.batchSize());
        p.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, s.compression());

        String value = "x".repeat(VALUE_BYTES);
        AtomicLong errors = new AtomicLong();
        long t0 = System.nanoTime();

        Map<MetricName, ? extends Metric> metrics;
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < RECORDS; i++) {
                producer.send(new ProducerRecord<>(TOPIC, "k-" + (i % 1000), value),
                        (md, ex) -> { if (ex != null) errors.incrementAndGet(); });
            }
            producer.flush();
            metrics = producer.metrics();
            printRow(s, t0, metrics);
        }
        if (errors.get() > 0) System.out.println("  ! hata sayısı: " + errors.get());
    }

    static void printRow(Setup s, long t0, Map<MetricName, ? extends Metric> metrics) {
        long ms = (System.nanoTime() - t0) / 1_000_000;
        double perSec = RECORDS * 1000.0 / Math.max(ms, 1);
        double mbSec = perSec * VALUE_BYTES / 1024 / 1024;
        System.out.printf("%-28s %9d %11.0f %11.2f %10.0f %8.1f %9.3f%n",
                s.name(), ms, perSec, mbSec,
                metric(metrics, "batch-size-avg"),
                metric(metrics, "records-per-request-avg"),
                metric(metrics, "compression-rate-avg"));
    }

    /** producer-metrics grubundan tek bir metriği okur. */
    static double metric(Map<MetricName, ? extends Metric> metrics, String name) {
        return metrics.entrySet().stream()
                .filter(e -> e.getKey().name().equals(name) && e.getKey().group().equals("producer-metrics"))
                .map(e -> e.getValue().metricValue())
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .findFirst().orElse(Double.NaN);
    }
}
