package tr.improsy.kafkalab.common;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Tüm lab örneklerinin ortak yardımcıları.
 *
 * lab/docker/docker-compose.yml ile ayağa kalkan 3 broker'lı KRaft cluster'a host'tan bağlanır.
 * Bootstrap adresini değiştirmek istersen:  -Dkafka.bootstrap=host:port
 */
public final class Lab {

    public static final String BOOTSTRAP =
            System.getProperty("kafka.bootstrap", "localhost:29092,localhost:39092,localhost:49092");

    private Lab() {}

    /** Topic'i oluşturur; zaten varsa sessizce geçer. */
    public static void ensureTopic(String name, int partitions, short rf, Map<String, String> configs) {
        try (Admin admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP))) {
            NewTopic t = new NewTopic(name, partitions, rf);
            if (configs != null && !configs.isEmpty()) t.configs(configs);
            admin.createTopics(List.of(t)).all().get();
            System.out.printf("[lab] topic hazır: %s (p=%d, rf=%d, cfg=%s)%n", name, partitions, rf, configs);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                System.out.printf("[lab] topic zaten var: %s%n", name);
            } else {
                throw new IllegalStateException("topic oluşturulamadı: " + name, e.getCause());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    public static void ensureTopic(String name, int partitions, short rf) {
        ensureTopic(name, partitions, rf, Map.of());
    }

    /** Test topic'ini siler — lab sonundaki "Temizlik" adımı için. */
    public static void deleteTopics(String... names) {
        try (Admin admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP))) {
            admin.deleteTopics(Collections.singletonList(names)).all().get();
            System.out.println("[lab] silindi: " + String.join(", ", names));
        } catch (Exception e) {
            System.out.println("[lab] silme atlandı (" + e.getMessage() + ")");
        }
    }

    /** Konsola okunaklı ayraç. */
    public static void banner(String s) {
        System.out.println("\n===== " + s + " =====");
    }

    public static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
