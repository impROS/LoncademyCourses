package tr.improsy.kafkalab.k34;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 3.4 — Share group (KIP-932) labı.
 *
 * TEK partition'lı bir topic'i ÜÇ share consumer ile tüketir. Klasik consumer group'ta
 * bu imkânsızdır (1 partition = 1 aktif consumer); share group'ta üçü de kayıt alır.
 *
 * Ayrıca bir tüketici kayıtları RELEASE eder (işleyemedim) → kayıt başka bir tüketiciye
 * yeniden teslim edilir.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k34.ShareGroupDemo
 */
public class ShareGroupDemo {

    static final String TOPIC = "lab-share";
    static final String GROUP = "share-demo";
    static final int RECORDS = 60;

    public static void main(String[] args) throws Exception {
        // TEK partition — klasik grupta tavan 1 consumer olurdu.
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of("retention.ms", "600000"));

        Lab.banner("3 share consumer, 1 partition");
        AtomicBoolean running = new AtomicBoolean(true);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Map<String, List<String>> received = new ConcurrentHashMap<>();
        List<String> released = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= 3; i++) {
            final String name = "s" + i;
            // s3 aldığı kayıtların hepsini RELEASE eder: "işleyemedim, başkası alsın"
            final boolean releaseAll = i == 3;
            received.put(name, Collections.synchronizedList(new ArrayList<>()));
            pool.submit(() -> consume(name, releaseAll, running, received.get(name), released));
        }

        // Share group varsayılan başlangıç noktası log SONUDUR: tüketiciler bağlandıktan
        // SONRA veri üretiyoruz, yoksa hiçbir şey görmezler. (Konu dosyasındaki tuzak.)
        Lab.sleep(4000);
        // Kayıtları YAVAŞ üretiyoruz: tek seferde basarsak ilk tüketici hepsini kapar ve
        // paylaşımı göremeyiz. Gerçek kuyruk davranışı sürekli akışta ortaya çıkar.
        new Thread(ShareGroupDemo::seedSlowly).start();
        Lab.sleep(14000);
        running.set(false);
        pool.shutdown();
        pool.awaitTermination(20, TimeUnit.SECONDS);

        Lab.banner("SONUÇ");
        received.forEach((k, v) -> System.out.printf("  %-4s %3d kayıt aldı%n", k, v.size()));
        System.out.println("  s3'ün RELEASE ettiği kayıt sayısı: " + released.size());
        Set<String> unique = new TreeSet<>();
        received.values().forEach(unique::addAll);
        System.out.println("  toplam teslim: "
                + received.values().stream().mapToInt(List::size).sum()
                + " · benzersiz kayıt: " + unique.size() + " / " + RECORDS);

        System.out.println("""

                Share grubunu incele:
                  docker exec kafka-1 /opt/kafka/bin/kafka-share-groups.sh \\
                    --bootstrap-server kafka-1:19092 --describe --group share-demo

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-share
                """);
    }

    static void consume(String name, boolean releaseAll, AtomicBoolean running,
                        List<String> mine, List<String> released) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        p.put(ConsumerConfig.CLIENT_ID_CONFIG, name);
        // Kayıt başına acknowledge yapabilmek için explicit mod:
        p.put("share.acknowledgement.mode", "explicit");

        try (ShareConsumer<String, String> consumer = new KafkaShareConsumer<>(p)) {
            consumer.subscribe(List.of(TOPIC));
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> r : records) {
                    if (releaseAll) {
                        // RELEASE: "işleyemedim" → kayıt gruptaki başka bir tüketiciye yeniden teslim edilir
                        consumer.acknowledge(r, AcknowledgeType.RELEASE);
                        released.add(r.value());
                    } else {
                        mine.add(r.value());
                        consumer.acknowledge(r, AcknowledgeType.ACCEPT);
                    }
                }
                if (!records.isEmpty()) consumer.commitSync();
            }
        } catch (Exception e) {
            System.out.println("  [" + name + "] hata: " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    /** Kayıtları 100 ms aralıkla üretir. */
    static void seedSlowly() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < RECORDS; i++) {
                producer.send(new ProducerRecord<>(TOPIC, null, "m-" + i));
                producer.flush();
                Lab.sleep(150);
            }
        }
    }

    static void seed() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < RECORDS; i++)
                producer.send(new ProducerRecord<>(TOPIC, null, "m-" + i));
        }
    }
}
