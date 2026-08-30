package tr.improsy.kafkalab.k32;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 3.2 — Rebalance labı: klasik protokol ile KIP-848 protokolünü yan yana gösterir.
 *
 * 6 partition'lı bir topic'i 2 consumer ile tüketmeye başlar, ortada 3. consumer ekler,
 * sonra birini kapatır. Her atama değişikliği zaman damgasıyla yazdırılır.
 *
 * Çalıştır (klasik):
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k32.RebalanceDemo -Dprotocol=classic
 * Çalıştır (KIP-848):
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k32.RebalanceDemo -Dprotocol=consumer
 */
public class RebalanceDemo {

    static final String TOPIC = "lab-rebalance";
    static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) throws Exception {
        String protocol = System.getProperty("protocol", "classic");
        String group = "rebalance-demo-" + protocol;

        Lab.ensureTopic(TOPIC, 6, (short) 3, Map.of("retention.ms", "600000"));
        seed(600);

        Lab.banner("group.protocol = " + protocol + "  ·  grup = " + group);
        ExecutorService pool = Executors.newCachedThreadPool();
        List<Worker> workers = new ArrayList<>();

        for (int i = 1; i <= 2; i++) workers.add(start(pool, group, protocol, "c" + i));
        Lab.sleep(8000);

        log("=== 3. consumer ekleniyor ===");
        workers.add(start(pool, group, protocol, "c3"));
        Lab.sleep(8000);

        log("=== c1 kapatılıyor ===");
        workers.get(0).stop();
        Lab.sleep(8000);

        log("=== bitiriliyor ===");
        running.set(false);
        workers.forEach(Worker::stop);
        pool.shutdown();
        pool.awaitTermination(20, TimeUnit.SECONDS);

        System.out.println("""

                Grubu incele:
                  docker exec kafka-1 /opt/kafka/bin/kafka-consumer-groups.sh \\
                    --bootstrap-server kafka-1:19092 --describe --group %s

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --delete --topic lab-rebalance
                """.formatted(group));
    }

    static void log(String s) {
        System.out.printf("%s  %s%n", LocalTime.now().format(TF), s);
    }

    static Worker start(ExecutorService pool, String group, String protocol, String name) {
        Worker w = new Worker(group, protocol, name);
        pool.submit(w);
        return w;
    }

    /** Tek bir consumer'ı temsil eder; atama değişikliklerini yazdırır. */
    static class Worker implements Runnable {
        final String group, protocol, name;
        final AtomicBoolean alive = new AtomicBoolean(true);

        Worker(String group, String protocol, String name) {
            this.group = group; this.protocol = protocol; this.name = name;
        }

        void stop() { alive.set(false); }

        @Override public void run() {
            Properties p = new Properties();
            p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
            p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            p.put(ConsumerConfig.GROUP_ID_CONFIG, group);
            p.put(ConsumerConfig.CLIENT_ID_CONFIG, name);
            p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            p.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
            p.put(ConsumerConfig.GROUP_PROTOCOL_CONFIG, protocol);
            if (protocol.equals("classic")) {
                // Klasik protokolde atama stratejisi client'ta seçilir; işbirlikçi olanı kullanıyoruz.
                p.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                        CooperativeStickyAssignor.class.getName());
            }

            try (Consumer<String, String> consumer = new KafkaConsumer<>(p)) {
                consumer.subscribe(List.of(TOPIC), new ConsumerRebalanceListener() {
                    @Override public void onPartitionsRevoked(Collection<TopicPartition> parts) {
                        log("  [" + name + "] GERİ ALINDI : " + ids(parts));
                    }
                    @Override public void onPartitionsAssigned(Collection<TopicPartition> parts) {
                        log("  [" + name + "] ATANDI      : " + ids(parts));
                    }
                    @Override public void onPartitionsLost(Collection<TopicPartition> parts) {
                        log("  [" + name + "] KAYBEDİLDİ  : " + ids(parts));
                    }
                });
                while (alive.get() && running.get()) {
                    consumer.poll(Duration.ofMillis(300));
                }
            } catch (org.apache.kafka.common.errors.WakeupException | IllegalStateException ignored) {
                // kapanış sırasında normal
            }
            log("  [" + name + "] kapandı");
        }

        static String ids(Collection<TopicPartition> parts) {
            List<Integer> ids = new ArrayList<>(parts.stream().map(TopicPartition::partition).sorted().toList());
            return ids.isEmpty() ? "[]" : ids.toString();
        }
    }

    /** Topic'e biraz veri koyar ki tüketiciler boş dönmesin. */
    static void seed(int n) {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < n; i++)
                producer.send(new ProducerRecord<>(TOPIC, "k-" + i, "v-" + i));
        }
    }
}
