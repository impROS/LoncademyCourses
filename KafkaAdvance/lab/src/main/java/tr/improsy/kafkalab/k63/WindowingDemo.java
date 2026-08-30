package tr.improsy.kafkalab.k63;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import tr.improsy.kafkalab.common.Lab;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

/**
 * 6.3 — Pencereleme ve join labı. TopologyTestDriver ile ÇALIŞIR: cluster gerekmez,
 * zamanı biz kontrol ederiz — pencere davranışını deterministik olarak gözlemleriz.
 *
 * Çalıştır:
 *   mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k63.WindowingDemo
 */
public class WindowingDemo {

    static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

    public static void main(String[] args) {
        tumbling();
        hopping();
        session();
        grace();
        streamStreamJoin();
        streamTableJoin();
    }

    static Properties props() {
        Properties p = new Properties();
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, "windowing-demo");
        p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");   // TTD'de kullanılmaz
        p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // Ara sonuçları görebilmek için önbelleği KAPATIYORUZ (6.1)
        p.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        return p;
    }

    /** TUMBLING: bitişik, çakışmayan pencereler. */
    static void tumbling() {
        Lab.banner("TUMBLING WINDOW — 10 sn, çakışma yok");
        StreamsBuilder b = new StreamsBuilder();
        b.stream("in", Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
                .count()
                .toStream()
                .foreach((k, v) -> System.out.printf("  [%s] %s..%s → %d%n",
                        k.key(), k.window().startTime(), k.window().endTime(), v));

        try (TopologyTestDriver driver = new TopologyTestDriver(b.build(), props(), T0)) {
            var in = driver.createInputTopic("in", Serdes.String().serializer(), Serdes.String().serializer());
            in.pipeInput("a", "1", T0);                       // pencere 0-10
            in.pipeInput("a", "2", T0.plusSeconds(3));        // pencere 0-10
            in.pipeInput("a", "3", T0.plusSeconds(12));       // pencere 10-20
        }
    }

    /** HOPPING: çakışan pencereler — bir kayıt birden çok pencereye düşer. */
    static void hopping() {
        Lab.banner("HOPPING WINDOW — 10 sn boy, 5 sn adım (çakışıyor)");
        StreamsBuilder b = new StreamsBuilder();
        b.stream("in", Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10))
                        .advanceBy(Duration.ofSeconds(5)))
                .count()
                .toStream()
                .foreach((k, v) -> System.out.printf("  [%s] %s..%s → %d%n",
                        k.key(), k.window().startTime(), k.window().endTime(), v));

        try (TopologyTestDriver driver = new TopologyTestDriver(b.build(), props(), T0)) {
            var in = driver.createInputTopic("in", Serdes.String().serializer(), Serdes.String().serializer());
            in.pipeInput("a", "1", T0.plusSeconds(7));   // hem 0-10 hem 5-15 penceresine düşer
        }
    }

    /** SESSION: aktivite boşluğuna göre büyüyen pencereler. */
    static void session() {
        Lab.banner("SESSION WINDOW — 10 sn boşluk (inactivity gap)");
        StreamsBuilder b = new StreamsBuilder();
        b.stream("in", Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(Duration.ofSeconds(10)))
                .count()
                .toStream()
                .foreach((k, v) -> System.out.printf("  [%s] %s..%s → %s%n",
                        k.key(), k.window().startTime(), k.window().endTime(), v));

        try (TopologyTestDriver driver = new TopologyTestDriver(b.build(), props(), T0)) {
            var in = driver.createInputTopic("in", Serdes.String().serializer(), Serdes.String().serializer());
            in.pipeInput("a", "1", T0);
            in.pipeInput("a", "2", T0.plusSeconds(5));    // aynı oturum (5 sn < 10 sn)
            in.pipeInput("a", "3", T0.plusSeconds(40));   // YENİ oturum (35 sn > 10 sn)
        }
    }

    /** GRACE: geç gelen kayıt penceresi hâlâ güncelleyebilir mi? */
    static void grace() {
        Lab.banner("GRACE PERIOD — 10 sn pencere + 5 sn tolerans");
        StreamsBuilder b = new StreamsBuilder();
        b.stream("in", Consumed.with(Serdes.String(), Serdes.String()))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofSeconds(10), Duration.ofSeconds(5)))
                .count()
                .toStream()
                .foreach((k, v) -> System.out.printf("  [%s] %s..%s → %d%n",
                        k.key(), k.window().startTime(), k.window().endTime(), v));

        try (TopologyTestDriver driver = new TopologyTestDriver(b.build(), props(), T0)) {
            var in = driver.createInputTopic("in", Serdes.String().serializer(), Serdes.String().serializer());
            in.pipeInput("a", "1", T0.plusSeconds(1));    // pencere 0-10
            in.pipeInput("a", "2", T0.plusSeconds(13));   // akış zamanı 13 → pencere kapandı ama grace sürüyor
            System.out.println("  --- şimdi GEÇ bir kayıt (t=2) gönderiyoruz, grace içinde ---");
            in.pipeInput("a", "3", T0.plusSeconds(2));    // KABUL edilmeli
            System.out.println("  --- akış zamanını 30'a taşıyıp yine geç kayıt (t=4) ---");
            in.pipeInput("a", "4", T0.plusSeconds(30));
            in.pipeInput("a", "5", T0.plusSeconds(4));    // grace doldu → ATILIR (çıktı yok)
        }
    }

    /** STREAM-STREAM JOIN: iki akış, pencere ZORUNLU. */
    static void streamStreamJoin() {
        Lab.banner("STREAM-STREAM JOIN — 10 sn pencere");
        StreamsBuilder b = new StreamsBuilder();
        KStream<String, String> siparis = b.stream("siparis", Consumed.with(Serdes.String(), Serdes.String()));
        KStream<String, String> odeme = b.stream("odeme", Consumed.with(Serdes.String(), Serdes.String()));
        siparis.join(odeme,
                        (s, o) -> "siparis=" + s + " + odeme=" + o,
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(10)),
                        StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String()))
                .foreach((k, v) -> System.out.printf("  [%s] %s%n", k, v));

        try (TopologyTestDriver driver = new TopologyTestDriver(b.build(), props(), T0)) {
            var s = driver.createInputTopic("siparis", Serdes.String().serializer(), Serdes.String().serializer());
            var o = driver.createInputTopic("odeme", Serdes.String().serializer(), Serdes.String().serializer());
            s.pipeInput("m1", "S-100", T0);
            o.pipeInput("m1", "O-100", T0.plusSeconds(4));    // 4 sn < 10 sn → EŞLEŞİR
            s.pipeInput("m2", "S-200", T0);
            o.pipeInput("m2", "O-200", T0.plusSeconds(30));   // 30 sn > 10 sn → eşleşmez
        }
    }

    /** STREAM-TABLE JOIN: pencere YOK, tablo o anki hâliyle sorgulanır. */
    static void streamTableJoin() {
        Lab.banner("STREAM-TABLE JOIN — pencere yok, tablonun O ANKİ hâli");
        StreamsBuilder b = new StreamsBuilder();
        KTable<String, String> musteri = b.table("musteri", Consumed.with(Serdes.String(), Serdes.String()));
        b.stream("siparis2", Consumed.with(Serdes.String(), Serdes.String()))
                .join(musteri, (siparis, ad) -> siparis + " → " + ad)
                .foreach((k, v) -> System.out.printf("  [%s] %s%n", k, v));

        try (TopologyTestDriver driver = new TopologyTestDriver(b.build(), props(), T0)) {
            var m = driver.createInputTopic("musteri", Serdes.String().serializer(), Serdes.String().serializer());
            var s = driver.createInputTopic("siparis2", Serdes.String().serializer(), Serdes.String().serializer());
            s.pipeInput("m1", "S-1", T0);                      // tabloda m1 YOK → eşleşmez (çıktı yok)
            m.pipeInput("m1", "Ayşe", T0.plusSeconds(1));
            s.pipeInput("m1", "S-2", T0.plusSeconds(2));       // artık eşleşir
            m.pipeInput("m1", "Ayşe Yılmaz", T0.plusSeconds(3));
            s.pipeInput("m1", "S-3", T0.plusSeconds(4));       // GÜNCEL değerle eşleşir
        }
    }
}
