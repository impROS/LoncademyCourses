package tr.improsy.kafkalab.k62;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.*;
import tr.improsy.kafkalab.common.Lab;

import java.io.File;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.Comparator;

/**
 * 6.2 — State store, changelog ve restore labı.
 *
 * 1) Sayaç topolojisi çalıştırır, state store'u interaktif sorgu ile okur.
 * 2) Diskteki RocksDB dizinini gösterir.
 * 3) State dizinini SİLER ve uygulamayı yeniden başlatır → changelog'dan RESTORE'u ölçer.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k62.StateStoreDemo
 */
public class StateStoreDemo {

    static final String IN = "lab-tiklamalar";
    static final String STORE = "tiklama-sayaci";
    static final String APP = "lab-state-demo";
    static final String STATE_DIR = System.getProperty("java.io.tmpdir") + "/kafka-streams-6-2";
    static final int KEYS = 50;
    static final int EVENTS = 5000;

    public static void main(String[] args) {
        // Lab'ı tekrar tekrar çalıştırdığında sayıların anlamlı kalması için sıfırdan başlıyoruz.
        cleanSlate();
        Lab.ensureTopic(IN, 4, (short) 3, Map.of("retention.ms", "600000"));

        Lab.banner("1. ÇALIŞTIRMA — state sıfırdan kuruluyor");
        seed();
        runAndReport(false);

        Lab.banner("2. ÇALIŞTIRMA — state dizini SİLİNDİ, changelog'dan restore");
        deleteStateDir();
        runAndReport(true);

        System.out.println("""

                Changelog topic'ini incele:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --describe --topic %s-%s-changelog

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-streams-application-reset.sh \\
                    --bootstrap-server kafka-1:19092 --application-id %s --input-topics %s
                """.formatted(APP, STORE, APP, IN));
    }

    static void runAndReport(boolean expectRestore) {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(IN, Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(STORE)
                        .withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()));

        Properties p = new Properties();
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, APP);
        p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.STATE_DIR_CONFIG, STATE_DIR);
        p.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        p.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 0);

        KafkaStreams streams = new KafkaStreams(builder.build(), p);

        // Restore'u ÖLÇ: kaç kayıt, ne kadar sürede geri yüklendi
        final java.util.concurrent.atomic.AtomicLong restored = new java.util.concurrent.atomic.AtomicLong();
        final java.util.concurrent.atomic.AtomicLong restoreMs = new java.util.concurrent.atomic.AtomicLong();
        streams.setGlobalStateRestoreListener(new org.apache.kafka.streams.processor.StateRestoreListener() {
            long start;
            @Override public void onRestoreStart(org.apache.kafka.common.TopicPartition tp,
                                                String store, long from, long to) {
                start = System.currentTimeMillis();
                System.out.printf("  restore başladı: %s  offset %d → %d%n", tp, from, to);
            }
            @Override public void onBatchRestored(org.apache.kafka.common.TopicPartition tp,
                                                  String store, long batchEnd, long numRestored) { }
            @Override public void onRestoreEnd(org.apache.kafka.common.TopicPartition tp,
                                               String store, long totalRestored) {
                restored.addAndGet(totalRestored);
                restoreMs.addAndGet(System.currentTimeMillis() - start);
                System.out.printf("  restore bitti : %s  %d kayıt%n", tp, totalRestored);
            }
        });

        streams.start();
        waitRunning(streams);
        Lab.sleep(8000);

        // Interaktif sorgu: state store'u DOĞRUDAN oku (aşağı akış topic'i olmadan)
        try {
            ReadOnlyKeyValueStore<String, Long> store = streams.store(
                    StoreQueryParameters.fromNameAndType(STORE, QueryableStoreTypes.keyValueStore()));
            long total = 0; int keys = 0;
            try (KeyValueIterator<String, Long> it = store.all()) {
                while (it.hasNext()) { total += it.next().value; keys++; }
            }
            System.out.printf("  interaktif sorgu: %d key, toplam %d olay%n", keys, total);
            System.out.println("  örnek: key-0 = " + store.get("key-0")
                    + "  (beklenen: " + (EVENTS / KEYS) + ")");
        } catch (Exception e) {
            System.out.println("  sorgu yapılamadı: " + e.getMessage());
        }

        showStateDir();
        streams.close(Duration.ofSeconds(15));
        System.out.printf("  >>> TOPLAM RESTORE: %d kayıt, ~%d ms (%s)%n",
                restored.get(), restoreMs.get(),
                expectRestore ? "state silinmişti" : "ilk çalıştırma");
    }

    static void waitRunning(KafkaStreams streams) {
        for (int i = 0; i < 240 && streams.state() != KafkaStreams.State.RUNNING; i++) Lab.sleep(500);
    }

    /** Önceki koşumlardan kalan topic, iç topic ve consumer group'u siler. */
    static void cleanSlate() {
        try (org.apache.kafka.clients.admin.Admin admin = org.apache.kafka.clients.admin.Admin.create(
                Map.of(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP))) {
            admin.deleteConsumerGroups(List.of(APP)).all().get();
        } catch (Exception ignored) { }
        Lab.deleteTopics(IN, APP + "-" + STORE + "-changelog");
        deleteStateDir();
        Lab.sleep(4000);
    }

    static void showStateDir() {
        File dir = new File(STATE_DIR);
        if (!dir.exists()) { System.out.println("  state dizini yok"); return; }
        try (var s = Files.walk(dir.toPath())) {
            long bytes = s.filter(Files::isRegularFile).mapToLong(f -> f.toFile().length()).sum();
            System.out.printf("  state dizini: %s  (%.1f KB)%n", STATE_DIR, bytes / 1024.0);
        } catch (Exception ignored) { }
    }

    static void deleteStateDir() {
        try (var s = Files.walk(Path.of(STATE_DIR))) {
            s.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            System.out.println("  state dizini silindi: " + STATE_DIR);
        } catch (Exception e) {
            System.out.println("  state dizini silinemedi: " + e.getMessage());
        }
    }

    static void seed() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.LINGER_MS_CONFIG, 20);
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < EVENTS; i++)
                producer.send(new ProducerRecord<>(IN, "key-" + (i % KEYS), "tiklama-" + i));
        }
        System.out.println("  " + EVENTS + " olay yazıldı (" + KEYS + " key)");
    }
}
