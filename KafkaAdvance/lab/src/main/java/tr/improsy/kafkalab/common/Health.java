package tr.improsy.kafkalab.common;

import org.apache.kafka.clients.admin.*;
import java.util.Map;

/**
 * Kurulum doğrulaması: cluster'a bağlanır, node'ları ve controller'ı yazdırır.
 * Çalıştır:  mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.common.Health
 */
public class Health {
    public static void main(String[] args) throws Exception {
        try (Admin admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Lab.BOOTSTRAP))) {
            DescribeClusterResult c = admin.describeCluster();
            System.out.println("clusterId  : " + c.clusterId().get());
            System.out.println("controller : " + c.controller().get());
            c.nodes().get().forEach(n -> System.out.println("node       : " + n.id() + " @ " + n.host() + ":" + n.port()));
            System.out.println("topics     : " + admin.listTopics().names().get());
            System.out.println("\nOK — cluster erişilebilir.");
        }
    }
}
