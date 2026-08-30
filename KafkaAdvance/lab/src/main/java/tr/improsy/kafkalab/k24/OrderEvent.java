package tr.improsy.kafkalab.k24;

/**
 * 2.4 — Şema evrimi örneği için olay modeli.
 *
 * v1: orderId, amountCents
 * v2: + currency (varsayılan "TRY")
 * v3: + customerId (varsayılanı YOK — bilerek: uyumsuz değişikliği göstermek için)
 */
public record OrderEvent(long orderId, long amountCents, String currency, String customerId) {

    public static final String DEFAULT_CURRENCY = "TRY";

    public static OrderEvent v1(long orderId, long amountCents) {
        return new OrderEvent(orderId, amountCents, DEFAULT_CURRENCY, null);
    }

    public static OrderEvent v2(long orderId, long amountCents, String currency) {
        return new OrderEvent(orderId, amountCents, currency, null);
    }

    public static OrderEvent v3(long orderId, long amountCents, String currency, String customerId) {
        return new OrderEvent(orderId, amountCents, currency, customerId);
    }
}
