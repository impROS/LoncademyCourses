package tr.improsy.kafkalab.common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Comparator;

/**
 * Client (producer/consumer) varsayılanlarını kullandığın kafka-clients sürümünden okur.
 * Konu dosyalarındaki tabloların kaynağı budur; sürüm değişince buradan doğrula.
 *
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.common.DumpDefaults
 */
public class DumpDefaults {
    public static void main(String[] args) throws Exception {
        dump("PRODUCER", configDefOf(ProducerConfig.class));
        dump("CONSUMER", configDefOf(ConsumerConfig.class));
    }

    static ConfigDef configDefOf(Class<?> cls) throws Exception {
        var f = cls.getDeclaredField("CONFIG");
        f.setAccessible(true);
        return (ConfigDef) f.get(null);
    }

    static void dump(String title, ConfigDef def) {
        System.out.println("===== " + title + " =====");
        def.configKeys().values().stream()
                .sorted(Comparator.comparing(k -> k.name))
                .forEach(k -> System.out.println(k.name + " = " + k.defaultValue));
    }
}
