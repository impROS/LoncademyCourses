package tr.improsy.kafkalab.k72;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 7.2 — Kendi yazdığımız SMT (Single Message Transform).
 *
 * Değer içindeki TC kimlik / kart benzeri uzun rakam dizilerini maskeler.
 * Gerçek hayatta bu, PII'yi topic'e yazmadan önce temizlemenin en ucuz yoludur.
 *
 * Kullanımı (connector properties):
 *   transforms=maskele
 *   transforms.maskele.type=tr.improsy.kafkalab.k72.MaskeleSmt$Value
 *   transforms.maskele.min.digits=10
 *   transforms.maskele.mask.char=*
 */
public abstract class MaskeleSmt<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String MIN_DIGITS = "min.digits";
    public static final String MASK_CHAR = "mask.char";

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(MIN_DIGITS, ConfigDef.Type.INT, 10, ConfigDef.Importance.MEDIUM,
                    "Bu uzunluk ve üstündeki rakam dizileri maskelenir")
            .define(MASK_CHAR, ConfigDef.Type.STRING, "*", ConfigDef.Importance.LOW,
                    "Maskeleme karakteri");

    private Pattern pattern;
    private String maskChar;

    @Override
    public void configure(Map<String, ?> configs) {
        Object minDigitsRaw = configs.get(MIN_DIGITS);
        int minDigits = minDigitsRaw == null ? 10 : Integer.parseInt(String.valueOf(minDigitsRaw));
        Object maskRaw = configs.get(MASK_CHAR);
        maskChar = maskRaw == null ? "*" : String.valueOf(maskRaw);
        pattern = Pattern.compile("\\d{" + minDigits + ",}");
    }

    /** Maskelenecek alanı alt sınıflar belirler (Key ya da Value). */
    protected abstract Object operatingValue(R record);

    protected abstract R newRecord(R record, Object updatedValue);

    @Override
    public R apply(R record) {
        Object value = operatingValue(record);
        if (!(value instanceof String s)) return record;      // yalnızca düz metinle çalışır
        var matcher = pattern.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, maskChar.repeat(matcher.group().length()));
        }
        matcher.appendTail(sb);
        String masked = sb.toString();
        return masked.equals(s) ? record : newRecord(record, masked);
    }

    @Override public ConfigDef config() { return CONFIG_DEF; }

    @Override public void close() { }

    /** Kaydın VALUE'sunu maskeler. */
    public static class Value<R extends ConnectRecord<R>> extends MaskeleSmt<R> {
        @Override protected Object operatingValue(R record) { return record.value(); }
        @Override protected R newRecord(R record, Object updated) {
            return record.newRecord(record.topic(), record.kafkaPartition(),
                    record.keySchema(), record.key(),
                    record.valueSchema(), updated, record.timestamp(), record.headers());
        }
    }

    /** Kaydın KEY'ini maskeler. */
    public static class Key<R extends ConnectRecord<R>> extends MaskeleSmt<R> {
        @Override protected Object operatingValue(R record) { return record.key(); }
        @Override protected R newRecord(R record, Object updated) {
            return record.newRecord(record.topic(), record.kafkaPartition(),
                    record.keySchema(), updated,
                    record.valueSchema(), record.value(), record.timestamp(), record.headers());
        }
    }
}
