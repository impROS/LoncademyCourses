package tr.improsy.kafkalab.k24;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 2.4 — Elle yazılmış, SÜRÜM BAYTLI bir codec.
 *
 * Wire format (Confluent Schema Registry'nin yaptığının sadeleştirilmiş hâli):
 *
 *   [0]      magic byte  = 0x00
 *   [1]      schemaVersion (1, 2, 3...)
 *   [2..]    gövde (sürüme göre değişir)
 *
 * Gerçek Schema Registry'de 1. bayttan sonra 4 baytlık bir SCHEMA ID gelir ve şemanın kendisi
 * registry'de durur. Buradaki fark ölçek/dayanıklılıktır; fikir aynıdır:
 * "veriyi okumadan önce hangi şemayla yazıldığını bil".
 */
public final class OrderCodec {

    public static final byte MAGIC = 0x00;

    /** Belirli bir sürümle yazan serializer. */
    public static class OrderSerializer implements Serializer<OrderEvent> {
        private final int version;

        public OrderSerializer(int version) { this.version = version; }

        @Override
        public byte[] serialize(String topic, OrderEvent e) {
            if (e == null) return null;                       // tombstone (1.4)
            byte[] currency = e.currency().getBytes(StandardCharsets.UTF_8);
            byte[] customer = e.customerId() == null
                    ? new byte[0] : e.customerId().getBytes(StandardCharsets.UTF_8);

            int size = 2 + 8 + 8;                             // magic + version + orderId + amount
            if (version >= 2) size += 4 + currency.length;
            if (version >= 3) size += 4 + customer.length;

            ByteBuffer b = ByteBuffer.allocate(size);
            b.put(MAGIC).put((byte) version).putLong(e.orderId()).putLong(e.amountCents());
            if (version >= 2) b.putInt(currency.length).put(currency);
            if (version >= 3) b.putInt(customer.length).put(customer);
            return b.array();
        }
    }

    /**
     * En yeni sürümü bilen, TOLERANSLI deserializer.
     * - Eski sürüm gelirse eksik alanları VARSAYILANLA doldurur  → geriye dönük uyumluluk
     * - Yeni sürüm gelirse tanımadığı kuyruğu ATLAR              → ileriye dönük uyumluluk
     */
    public static class TolerantOrderDeserializer implements Deserializer<OrderEvent> {
        private final int knownVersion;

        public TolerantOrderDeserializer() { this(3); }
        public TolerantOrderDeserializer(int knownVersion) { this.knownVersion = knownVersion; }

        @Override
        public OrderEvent deserialize(String topic, byte[] data) {
            if (data == null) return null;
            ByteBuffer b = ByteBuffer.wrap(data);
            byte magic = b.get();
            if (magic != MAGIC) throw new SerializationException("beklenmeyen magic byte: " + magic);
            int version = b.get();

            long orderId = b.getLong();
            long amount = b.getLong();
            String currency = OrderEvent.DEFAULT_CURRENCY;   // v1 için VARSAYILAN
            String customerId = null;

            if (version >= 2 && b.remaining() >= 4) currency = readString(b);
            if (version >= 3 && b.remaining() >= 4) customerId = readString(b);
            // version > knownVersion ise: kalan baytlar okunmadan bırakılır (ileriye dönük uyumluluk)

            return new OrderEvent(orderId, amount, currency, customerId);
        }
    }

    /** KATI deserializer: tanımadığı sürümü reddeder. Gerçek hayatta en sık kırılan tüketici tipi. */
    public static class StrictOrderDeserializer implements Deserializer<OrderEvent> {
        private final int expectedVersion;

        public StrictOrderDeserializer(int expectedVersion) { this.expectedVersion = expectedVersion; }

        @Override
        public OrderEvent deserialize(String topic, byte[] data) {
            if (data == null) return null;
            ByteBuffer b = ByteBuffer.wrap(data);
            b.get();                                   // magic
            int version = b.get();
            if (version != expectedVersion) {
                throw new SerializationException(
                        "şema sürümü uyumsuz: beklenen v" + expectedVersion + ", gelen v" + version);
            }
            long orderId = b.getLong();
            long amount = b.getLong();
            String currency = version >= 2 ? readString(b) : OrderEvent.DEFAULT_CURRENCY;
            String customerId = version >= 3 ? readString(b) : null;
            return new OrderEvent(orderId, amount, currency, customerId);
        }
    }

    static String readString(ByteBuffer b) {
        int len = b.getInt();
        byte[] bytes = new byte[len];
        b.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private OrderCodec() {}
}
