package tr.improsy.kafkalab.k12;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import tr.improsy.kafkalab.common.Lab;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 1.2 — ISR daralmasını ve min.insync.replicas'ın etkisini canlı gösterir.
 *
 * Program 2 saniyede bir acks=all ile tek kayıt yazar ve her seferinde ISR'yi yazdırır.
 * Sen başka bir terminalden broker öldürdükçe ISR'nin daraldığını, 2 broker kalmayınca da
 * NotEnoughReplicasException alındığını görürsün.
 *
 *   docker stop kafka-3      → ISR 3→2, yazma devam eder
 *   docker stop kafka-2      → ISR 2→1, yazma REDDEDİLİR (min.insync.replicas=2)
 *   docker start kafka-2     → ISR geri genişler, yazma devam eder
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k12.IsrDemo
 */
public class IsrDemo {

    static final String TOPIC = "lab-isr";
    static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) throws Exception {
        Lab.ensureTopic(TOPIC, 1, (short) 3, Map.of("min.insync.replicas", "2"));

        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.ACKS_CONFIG, "all");
        // Hatayı hemen görebilmek için: normalde retry'lar hatayı 2 dakika saklar.
        p.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 6000);
        p.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        p.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
        // -Dretries=0 ile çalıştırırsan hatanın ham hâlini (NotEnoughReplicasException) görürsün.
        // Varsayılanda retry açık olduğu için hata TimeoutException'a dönüşür — konuda anlatılıyor.
        p.put(ProducerConfig.RETRIES_CONFIG, Integer.getInteger("retries", 3));

        int iterations = Integer.getInteger("iterations", 30);

        try (Admin admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP));
             Producer<String, String> producer = new KafkaProducer<>(p)) {

            Lab.banner("acks=all, min.insync.replicas=2 — başka terminalden broker öldür");

            for (int i = 0; i < iterations; i++) {
                String isr = describeIsr(admin);
                String result;
                try {
                    RecordMetadata md = producer.send(new ProducerRecord<>(TOPIC, "k", "v" + i)).get();
                    result = "OK   offset=" + md.offset();
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    result = "FAIL " + cause.getClass().getSimpleName() + ": " + cause.getMessage();
                }
                System.out.printf("%s  %-28s  %s%n", LocalTime.now().format(TF), isr, result);
                Lab.sleep(2000);
            }
        }
        System.out.println("\nBitti. Öldürdüğün broker'ları geri başlatmayı unutma: docker start kafka-2 kafka-3");
    }

    /** Partition 0'ın lider / replika / ISR / ELR durumunu tek satırda döndürür. */
    static String describeIsr(Admin admin) {
        try {
            TopicDescription d = admin.describeTopics(List.of(TOPIC)).allTopicNames().get().get(TOPIC);
            TopicPartitionInfo info = d.partitions().get(0);
            return "leader=" + (info.leader() == null ? "-" : info.leader().id())
                    + " isr=" + info.isr().stream().map(n -> String.valueOf(n.id())).toList();
        } catch (Exception e) {
            return "isr=? (" + e.getClass().getSimpleName() + ")";
        }
    }
}
