package tr.improsy.kafkalab.k33;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;

/**
 * 3.3 — Commit sırasının teslimat semantiğine etkisini KANITLAR.
 *
 * Senaryo: 30 kayıt yazılır. Tüketici 10 kayıt çeker, 5'ini işler ve "çöker".
 * Sonra aynı group.id ile yeni bir tüketici başlar ve kalanı işler.
 *
 *   ÖNCE COMMIT  → çöküşte işlenmeyen kayıtlar bir daha görünmez  → KAYIP (at-most-once)
 *   SONRA COMMIT → çöküşte işlenenler tekrar görünür              → DUPLICATE (at-least-once)
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k33.CommitStrategyDemo
 */
public class CommitStrategyDemo {

    static final int TOTAL = 30;
    static final int FETCH = 10;
    static final int CRASH_AFTER = 5;

    public static void main(String[] args) {
        scenario("ÖNCE COMMIT (at-most-once)", true);
        scenario("SONRA COMMIT (at-least-once)", false);
    }

    static void scenario(String title, boolean commitFirst) {
        String topic = "lab-commit-" + (commitFirst ? "before" : "after");
        String group = "commit-demo-" + (commitFirst ? "before" : "after");
        Lab.ensureTopic(topic, 1, (short) 3, Map.of("retention.ms", "600000"));
        seed(topic);

        Lab.banner(title);
        Set<Integer> processed = new TreeSet<>();
        List<Integer> processOrder = new ArrayList<>();

        // --- 1. oturum: 10 kayıt çeker, 5'ini işler ve "çöker" ---
        try (Consumer<String, String> c = consumer(group)) {
            c.subscribe(List.of(topic));
            ConsumerRecords<String, String> records = pollUntilData(c);
            List<ConsumerRecord<String, String>> list = new ArrayList<>();
            records.forEach(list::add);

            if (commitFirst) {
                c.commitSync();                                  // ÖNCE commit — tamamı işlendi sayıldı
                System.out.println("  commit edildi (işlemeden önce), " + list.size() + " kayıt için");
            }
            for (int i = 0; i < Math.min(CRASH_AFTER, list.size()); i++) {
                int v = Integer.parseInt(list.get(i).value());
                processed.add(v); processOrder.add(v);
            }
            System.out.println("  işlendi: " + processOrder + "  → ÇÖKÜŞ (kalanı işlemeden çık)");
            // SONRA-commit senaryosunda commit HİÇ yapılmadı: çöküş öncesi commit yok.
        }

        // --- 2. oturum: aynı grup, kaldığı yerden ---
        List<Integer> secondRun = new ArrayList<>();
        try (Consumer<String, String> c = consumer(group)) {
            c.subscribe(List.of(topic));
            int empty = 0;
            while (empty < 3) {
                ConsumerRecords<String, String> records = c.poll(Duration.ofSeconds(2));
                if (records.isEmpty()) { empty++; continue; }
                empty = 0;
                for (ConsumerRecord<String, String> r : records) {
                    int v = Integer.parseInt(r.value());
                    secondRun.add(v); processed.add(v);
                }
                c.commitSync();
            }
        }

        System.out.println("  2. oturumda işlenen: " + summarize(secondRun));
        List<Integer> missing = new ArrayList<>();
        for (int i = 0; i < TOTAL; i++) if (!processed.contains(i)) missing.add(i);
        List<Integer> duplicated = new ArrayList<>();
        for (int v : processOrder) if (secondRun.contains(v)) duplicated.add(v);

        System.out.println("  → HİÇ İŞLENMEYEN (kayıp) : " + (missing.isEmpty() ? "yok" : missing));
        System.out.println("  → İKİ KEZ İŞLENEN        : " + (duplicated.isEmpty() ? "yok" : duplicated));
    }

    static Consumer<String, String> consumer(String group) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);   // commit'i biz yönetiyoruz
        p.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, FETCH);
        return new KafkaConsumer<>(p);
    }

    static ConsumerRecords<String, String> pollUntilData(Consumer<String, String> c) {
        for (int i = 0; i < 20; i++) {
            ConsumerRecords<String, String> r = c.poll(Duration.ofSeconds(1));
            if (!r.isEmpty()) return r;
        }
        throw new IllegalStateException("veri gelmedi");
    }

    /** Topic'i her koşumda sıfırdan doldurmak yerine, boşsa doldurur. */
    static void seed(String topic) {
        Properties cp = new Properties();
        cp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        cp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        cp.put(ConsumerConfig.GROUP_ID_CONFIG, "seed-check");
        TopicPartition tp = new TopicPartition(topic, 0);
        try (Consumer<String, String> c = new KafkaConsumer<>(cp)) {
            if (c.endOffsets(List.of(tp)).get(tp) >= TOTAL) return;
        }
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < TOTAL; i++)
                producer.send(new ProducerRecord<>(topic, "k", String.valueOf(i)));
        }
    }

    static String summarize(List<Integer> v) {
        if (v.isEmpty()) return "[]";
        return v.size() + " kayıt, ilk=" + v.get(0) + " son=" + v.get(v.size() - 1);
    }
}
