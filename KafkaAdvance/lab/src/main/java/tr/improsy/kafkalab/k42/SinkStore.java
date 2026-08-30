package tr.improsy.kafkalab.k42;

import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * 4.2 — Bir ilişkisel veritabanının yerine geçen, minimal ve THREAD-SAFE bir "hedef sistem".
 *
 * Gerçek hayatta bunun karşılığı:
 *   balances        → bakiye tablosu
 *   processedIds    → UNIQUE(message_id) kısıtı olan idempotency tablosu
 *   offsets         → kafka_offsets tablosu (topic, partition, offset)
 * applyAtomically(...) ise TEK bir DB transaction'ıdır.
 */
public class SinkStore {

    private final Map<String, Long> balances = new HashMap<>();
    private final Set<String> processedIds = new HashSet<>();
    private final Map<TopicPartition, Long> offsets = new HashMap<>();

    /** Naif uygulama: tekilleştirme yok. Duplicate gelirse bakiye iki kez artar. */
    public synchronized void applyNaive(String account, long amount) {
        balances.merge(account, amount, Long::sum);
    }

    /**
     * Idempotent uygulama: iş verisi + idempotency kaydı + offset AYNI atomik blokta.
     * @return kayıt gerçekten uygulandıysa true, duplicate olduğu için atlandıysa false
     */
    public synchronized boolean applyAtomically(String messageId, String account, long amount,
                                                TopicPartition tp, long nextOffset) {
        if (!processedIds.add(messageId)) {         // UNIQUE kısıtının karşılığı
            offsets.put(tp, nextOffset);            // offset yine ilerlemeli, iş tekrar edilmemeli
            return false;
        }
        balances.merge(account, amount, Long::sum);
        offsets.put(tp, nextOffset);
        return true;
    }

    /** Uygulama açılışında offset'i buradan okur ve consumer.seek(...) yapar. */
    public synchronized Long offsetOf(TopicPartition tp) {
        return offsets.get(tp);
    }

    public synchronized long balance(String account) {
        return balances.getOrDefault(account, 0L);
    }

    public synchronized long totalBalance() {
        return balances.values().stream().mapToLong(Long::longValue).sum();
    }

    public synchronized int processedCount() {
        return processedIds.size();
    }

    public synchronized Map<String, Long> snapshot() {
        return new TreeMap<>(balances);
    }
}
