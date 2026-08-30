package tr.improsy.kafkalab.k61;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.util.*;

/**
 * 6.1 — Topoloji, task ve thread modeli labı.
 *
 * Topoloji: siparisler → filtrele → (yeniden anahtarla) → müşteri başına toplam → çıktı
 * Amaç: topology.describe() çıktısında ALT TOPOLOJİ (sub-topology) sınırlarını ve
 * repartition topic'ini görmek; sonra çalıştırıp task/thread davranışını gözlemlemek.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k61.TopologyDemo
 */
public class TopologyDemo {

    static final String IN = "lab-siparisler";
    static final String OUT = "lab-musteri-toplam";

    public static void main(String[] args) {
        Lab.ensureTopic(IN, 4, (short) 3, Map.of("retention.ms", "600000"));
        Lab.ensureTopic(OUT, 4, (short) 3, Map.of("retention.ms", "600000"));

        StreamsBuilder builder = new StreamsBuilder();

        // Kaynak: siparis olayları  key=siparisId, value="musteriId:tutar"
        KStream<String, String> siparisler = builder.stream(IN,
                Consumed.with(Serdes.String(), Serdes.String()));

        siparisler
                .filter((k, v) -> v != null && v.contains(":"))                 // stateless
                .selectKey((k, v) -> v.split(":")[0])                           // YENİDEN ANAHTARLAMA
                .mapValues(v -> Long.parseLong(v.split(":")[1]))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))       // repartition tetikler
                .reduce(Long::sum, Materialized.as("musteri-toplam-store"))     // STATEFUL
                .toStream()
                .to(OUT, Produced.with(Serdes.String(), Serdes.Long()));

        Topology topology = builder.build();

        Lab.banner("TOPOLOJİ");
        System.out.println(topology.describe());

        Properties p = new Properties();
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, "lab-topology-demo");
        p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, Integer.getInteger("threads", 2));
        p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.STATE_DIR_CONFIG, System.getProperty("java.io.tmpdir") + "/kafka-streams-lab");
        p.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        KafkaStreams streams = new KafkaStreams(topology, p);
        streams.setStateListener((newState, oldState) ->
                System.out.println("  [durum] " + oldState + " → " + newState));

        streams.start();
        Lab.sleep(6000);
        seed();
        Lab.sleep(8000);

        Lab.banner("ÇALIŞAN TASK'LAR");
        streams.metadataForLocalThreads().forEach(t -> {
            System.out.println("  thread: " + t.threadName() + "  durum: " + t.threadState());
            t.activeTasks().forEach(task ->
                    System.out.println("      aktif task " + task.taskId() + " ← " + task.topicPartitions()));
            t.standbyTasks().forEach(task ->
                    System.out.println("      standby task " + task.taskId() + " ← " + task.topicPartitions()));
        });

        streams.close(Duration.ofSeconds(10));
        System.out.println("""

                Oluşan iç topic'leri gör (repartition + changelog):
                  docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh \\
                    --bootstrap-server kafka-1:19092 --list | grep lab-topology-demo

                Temizlik:
                  docker exec kafka-1 /opt/kafka/bin/kafka-streams-application-reset.sh \\
                    --bootstrap-server kafka-1:19092 --application-id lab-topology-demo \\
                    --input-topics lab-siparisler
                """);
    }

    static void seed() {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (Producer<String, String> producer = new KafkaProducer<>(p)) {
            for (int i = 0; i < 200; i++)
                producer.send(new ProducerRecord<>(IN, "siparis-" + i, "musteri-" + (i % 5) + ":" + (i * 10)));
        }
        System.out.println("  200 sipariş yazıldı");
    }
}
