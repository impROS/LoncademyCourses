package tr.improsy.kafkalab.k64;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 6.4 — Streams topolojisini CLUSTER OLMADAN test etmenin referans örneği.
 *
 * Çalıştır:
 *   mvn -q test -Dtest=SiparisTopolojisiTest
 */
class SiparisTopolojisiTest {

    static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

    TopologyTestDriver driver;
    TestInputTopic<String, Long> girdi;
    TestOutputTopic<String, Long> cikti;

    /** Test edilen topoloji: müşteri başına 1 dakikalık pencerede toplam tutar. */
    static Topology topoloji() {
        StreamsBuilder b = new StreamsBuilder();
        b.stream("siparisler", Consumed.with(Serdes.String(), Serdes.Long()))
                .filter((k, v) -> v != null && v > 0)                    // negatif/boş kayıtları ele
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .reduce(Long::sum, Materialized.<String, Long, KeyValueStore<org.apache.kafka.common.utils.Bytes, byte[]>>as("toplam")
                        .withKeySerde(Serdes.String()).withValueSerde(Serdes.Long()))
                .toStream()
                .to("musteri-toplam", Produced.with(Serdes.String(), Serdes.Long()));
        return b.build();
    }

    @BeforeEach
    void setUp() {
        Properties p = new Properties();
        p.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-app");
        p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");
        p.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);   // ara sonuçları da görelim
        driver = new TopologyTestDriver(topoloji(), p, T0);
        girdi = driver.createInputTopic("siparisler",
                Serdes.String().serializer(), Serdes.Long().serializer());
        cikti = driver.createOutputTopic("musteri-toplam",
                Serdes.String().deserializer(), Serdes.Long().deserializer());
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

    @Test
    @DisplayName("aynı müşterinin siparişleri toplanır")
    void toplamHesaplanir() {
        girdi.pipeInput("musteri-1", 100L, T0);
        girdi.pipeInput("musteri-1", 250L, T0.plusSeconds(1));

        assertEquals(List.of(
                new KeyValue<>("musteri-1", 100L),
                new KeyValue<>("musteri-1", 350L)), cikti.readKeyValuesToList());
    }

    @Test
    @DisplayName("farklı müşteriler birbirini etkilemez")
    void musterilerAyri() {
        girdi.pipeInput("musteri-1", 100L, T0);
        girdi.pipeInput("musteri-2", 700L, T0.plusSeconds(1));

        assertEquals(100L, cikti.readKeyValue().value);
        assertEquals(700L, cikti.readKeyValue().value);
        assertTrue(cikti.isEmpty());
    }

    @Test
    @DisplayName("sıfır ve negatif tutarlar elenir")
    void gecersizKayitlarElenir() {
        girdi.pipeInput("musteri-1", 0L, T0);
        girdi.pipeInput("musteri-1", -50L, T0.plusSeconds(1));

        assertTrue(cikti.isEmpty(), "geçersiz kayıtlar çıktı üretmemeliydi");
    }

    @Test
    @DisplayName("state store doğrudan sorgulanabilir")
    void storeSorgulanir() {
        girdi.pipeInput("musteri-1", 100L, T0);
        girdi.pipeInput("musteri-1", 50L, T0.plusSeconds(1));

        KeyValueStore<String, Long> store = driver.getKeyValueStore("toplam");
        assertEquals(150L, store.get("musteri-1"));
        assertNull(store.get("olmayan-musteri"));
    }

    @Test
    @DisplayName("zaman ileri sarılabilir — duvar saati beklenmez")
    void zamanKontrolu() {
        girdi.pipeInput("musteri-1", 10L, T0);
        driver.advanceWallClockTime(Duration.ofMinutes(5));   // punctuator'lar için
        girdi.pipeInput("musteri-1", 20L, T0.plusSeconds(300));

        assertEquals(30L, driver.<String, Long>getKeyValueStore("toplam").get("musteri-1"));
    }
}
