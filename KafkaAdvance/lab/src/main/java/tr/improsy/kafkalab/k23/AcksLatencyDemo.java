package tr.improsy.kafkalab.k23;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import tr.improsy.kafkalab.common.Lab;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 2.3 — acks seviyesinin gecikmeye ve dayanıklılığa etkisini ölçer.
 *
 * Her acks seviyesi için aynı sayıda kayıt gönderir ve kayıt başına uçtan uca onay süresini
 * (send çağrısından callback'e) toplayıp p50/p95/p99 olarak raporlar.
 *
 * Not: acks=0 idempotence ile uyumsuzdur; o koşumda enable.idempotence kapatılır —
 * yani ölçüm aynı zamanda "acks'i düşürmek başka garantileri de kapatır" dersini verir.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k23.AcksLatencyDemo
 */
public class AcksLatencyDemo {

    static final String TOPIC = "lab-acks";
    static final int RECORDS = 6_000;
    static final int VALUE_BYTES = 500;
    /** Hedef gönderim hızı (kayıt/sn). Sabit hız olmadan ölçülen "gecikme" büyük ölçüde
     *  kuyrukta bekleme olur ve acks karşılaştırması anlamsızlaşır. */
    static final int TARGET_RATE = 2_000;

    public static void main(String[] args) throws Exception {
        Lab.ensureTopic(TOPIC, 3, (short) 3, Map.of(
                "min.insync.replicas", "2",
                "retention.ms", "600000"));

        System.out.printf("%n%-10s %10s %10s %10s %10s %12s%n",
                "acks", "süre(ms)", "p50(µs)", "p95(µs)", "p99(µs)", "kayıt/sn");
        System.out.println("(sabit " + TARGET_RATE + " kayıt/sn hızında " + RECORDS + " kayıt)");
        System.out.println("-".repeat(70));

        for (String acks : List.of("0", "1", "all")) {
            run(acks);
        }

        System.out.println("""

                Yorum:
                  acks=0   : onay beklenmez — ölçülen süre yalnızca tampona koyma süresidir, teslimat değil
                  acks=1   : yalnızca lider yazdı; lider çökerse replike olmamış kayıt kaybolur
                  acks=all : ISR'deki tüm replikalar yazdı (min.insync.replicas=2 şartıyla)

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-acks
                """);
    }

    static void run(String acks) throws Exception {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.ACKS_CONFIG, acks);
        p.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        p.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        // acks=0/1 idempotence ile uyumsuzdur (2.2) — bilerek kapatıyoruz.
        p.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, acks.equals("all"));

        String value = "x".repeat(VALUE_BYTES);
        long[] micros = new long[RECORDS];
        CountDownLatch done = new CountDownLatch(RECORDS);
        long t0 = System.nanoTime();

        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            long start = System.nanoTime();
            for (int i = 0; i < RECORDS; i++) {
                final int idx = i;
                // sabit hız: i. kayıt en erken start + i/TARGET_RATE saniyede gönderilir
                long dueNs = start + (long) (i * 1_000_000_000.0 / TARGET_RATE);
                long waitNs = dueNs - System.nanoTime();
                if (waitNs > 0) {
                    try { Thread.sleep(waitNs / 1_000_000, (int) (waitNs % 1_000_000)); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                long sent = System.nanoTime();
                producer.send(new ProducerRecord<>(TOPIC, "k-" + (i % 100), value), (md, ex) -> {
                    micros[idx] = (System.nanoTime() - sent) / 1000;
                    done.countDown();
                });
            }
            producer.flush();
            done.await();
        }

        long ms = (System.nanoTime() - t0) / 1_000_000;
        Arrays.sort(micros);
        System.out.printf("%-10s %10d %10d %10d %10d %12.0f%n",
                acks, ms, pct(micros, 50), pct(micros, 95), pct(micros, 99),
                RECORDS * 1000.0 / Math.max(ms, 1));
    }

    static long pct(long[] sorted, int p) {
        return sorted[Math.min(sorted.length - 1, (int) Math.ceil(sorted.length * p / 100.0) - 1)];
    }
}
