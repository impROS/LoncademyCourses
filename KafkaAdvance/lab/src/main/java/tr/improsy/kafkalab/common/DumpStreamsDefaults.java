package tr.improsy.kafkalab.common;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Comparator;

/** Kafka Streams varsayılanlarını kullandığın sürümden okur. */
public class DumpStreamsDefaults {
    public static void main(String[] args) throws Exception {
        var f = StreamsConfig.class.getDeclaredField("CONFIG");
        f.setAccessible(true);
        ConfigDef def = (ConfigDef) f.get(null);
        def.configKeys().values().stream()
                .sorted(Comparator.comparing(k -> k.name))
                .forEach(k -> System.out.println(k.name + " = " + k.defaultValue));
    }
}
