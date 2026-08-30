package tr.improsy.kafkalab.k64;

import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.errors.*;
import org.apache.kafka.streams.kstream.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Instant;
import java.util.Properties;

/**
 * 6.4 — Streams hata yönetimi labı (TopologyTestDriver ile, cluster gerekmez).
 *
 * Üç hata sınıfını ve üç ayrı handler'ı gösterir:
 *   1) DESERIALIZATION  → deserialization.exception.handler  (LogAndFail / LogAndContinue)
 *   2) PROCESSING       → processing.exception.handler       (KIP-1033)
 *   3) PRODUCTION       → production.exception.handler
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k64.ErrorHandlingDemo
 */
public class ErrorHandlingDemo {

    static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

    public static void main(String[] args) {
        deserializationFail();
        deserializationContinue();
        processingFail();
        processingContinue();
    }

    /** Long bekleyen bir topolojiye bozuk bayt gönderir. */
    static Topology longSumTopology() {
        StreamsBuilder b = new StreamsBuilder();
        b.stream("in", Consumed.with(Serdes.String(), Serdes.Long()))
                .peek((k, v) -> System.out.printf("    işlendi: %s=%d%n", k, v))
                .to("out", Produced.with(Serdes.String(), Serdes.Long()));
        return b.build();
    }

    /** İçinde bilerek hata fırlatan bir processor olan topoloji. */
    static Topology throwingTopology() {
        StreamsBuilder b = new StreamsBuilder();
        b.stream("in", Consumed.with(Serdes.String(), Serdes.String()))
                .mapValues(v -> {
                    if (v.startsWith("BOZUK")) throw new IllegalStateException("işlenemez kayıt: " + v);
                    return v.toUpperCase();
                })
                .peek((k, v) -> System.out.printf("    işlendi: %s=%s%n", k, v))
                .to("out", Produced.with(Serdes.String(), Serdes.String()));
        return b.build();
    }

    static Properties props(String key, Object value) {
        Properties p = new Properties();
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, "error-demo");
        p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");
        p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        if (key != null) p.put(key, value);
        return p;
    }

    static void deserializationFail() {
        Lab.banner("1a) DESERIALIZATION — LogAndFail (VARSAYILAN)");
        try (TopologyTestDriver d = new TopologyTestDriver(longSumTopology(), props(null, null), T0)) {
            var in = d.createInputTopic("in", Serdes.String().serializer(), Serdes.ByteArray().serializer());
            in.pipeInput("a", new byte[]{1, 2, 3});   // Long değil → deserialize hatası
            System.out.println("  (buraya ulaşılmamalı)");
        } catch (Exception e) {
            System.out.println("  ✗ uygulama DURDU: " + e.getClass().getSimpleName()
                    + " → " + rootMessage(e));
        }
    }

    static void deserializationContinue() {
        Lab.banner("1b) DESERIALIZATION — LogAndContinue");
        Properties p = props(StreamsConfig.DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                LogAndContinueExceptionHandler.class.getName());
        try (TopologyTestDriver d = new TopologyTestDriver(longSumTopology(), p, T0)) {
            var raw = d.createInputTopic("in", Serdes.String().serializer(), Serdes.ByteArray().serializer());
            raw.pipeInput("a", new byte[]{1, 2, 3});                       // atlanır
            raw.pipeInput("b", Serdes.Long().serializer().serialize("in", 42L));   // işlenir
            System.out.println("  ✓ uygulama ÇALIŞMAYA DEVAM ETTİ (bozuk kayıt atlandı)");
        }
    }

    static void processingFail() {
        Lab.banner("2a) PROCESSING — LogAndFail (VARSAYILAN)");
        try (TopologyTestDriver d = new TopologyTestDriver(throwingTopology(), props(null, null), T0)) {
            var in = d.createInputTopic("in", Serdes.String().serializer(), Serdes.String().serializer());
            in.pipeInput("a", "iyi-kayit");
            in.pipeInput("b", "BOZUK-kayit");
            System.out.println("  (buraya ulaşılmamalı)");
        } catch (Exception e) {
            System.out.println("  ✗ uygulama DURDU: " + e.getClass().getSimpleName()
                    + " → " + rootMessage(e));
        }
    }

    static void processingContinue() {
        Lab.banner("2b) PROCESSING — LogAndContinue (KIP-1033)");
        Properties p = props(StreamsConfig.PROCESSING_EXCEPTION_HANDLER_CLASS_CONFIG,
                LogAndContinueProcessingExceptionHandler.class.getName());
        try (TopologyTestDriver d = new TopologyTestDriver(throwingTopology(), p, T0)) {
            var in = d.createInputTopic("in", Serdes.String().serializer(), Serdes.String().serializer());
            in.pipeInput("a", "iyi-kayit");
            in.pipeInput("b", "BOZUK-kayit");     // atlanır
            in.pipeInput("c", "yine-iyi");
            System.out.println("  ✓ uygulama ÇALIŞMAYA DEVAM ETTİ");
        }
    }

    static String rootMessage(Throwable t) {
        while (t.getCause() != null) t = t.getCause();
        return t.getMessage();
    }
}
