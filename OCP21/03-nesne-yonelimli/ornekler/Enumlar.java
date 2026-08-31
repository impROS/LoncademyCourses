/*
 * 3.7 — Enum: constructor sayisi, sabit govdesi, switch ve EnumSet/EnumMap.
 *
 *     java Enumlar.java
 */
import java.util.*;

public class Enumlar {

    enum Renk {
        KIRMIZI("#f00"), YESIL("#0f0"), MAVI("#00f");     // ; zorunlu (asagida uye var)

        private final String kod;
        Renk(String kod) {                                 // ORTUK private
            this.kod = kod;
            System.out.print("[ctor " + name() + "] ");
        }
        String kod() { return kod; }
        @Override public String toString() { return name() + kod; }   // toString override EDILEBILIR
    }

    enum Islem {
        TOPLA { public int uygula(int a, int b) { return a + b; } },
        CIKAR { public int uygula(int a, int b) { return a - b; } };

        public abstract int uygula(int a, int b);
    }

    interface Etiketli { String etiket(); }

    enum Durum implements Etiketli {                       // implements SERBEST
        ACIK, KAPALI;
        @Override public String etiket() { return "durum:" + name().toLowerCase(); }
    }

    public static void main(String[] args) {

        System.out.print("01 constructor -> ");
        Renk r = Renk.MAVI;                                // ilk kullanim: TUM sabitler kurulur
        System.out.println("| ilk kullanim sonrasi");

        System.out.println("02 hazir metotlar -> name=" + r.name() + " ordinal=" + r.ordinal()
                + " toString=" + r + " values=" + Arrays.toString(Renk.values()));

        System.out.println("03 values() her cagrida yeni dizi -> "
                + (Renk.values() == Renk.values()));

        System.out.println("04 sabit govdesi -> TOPLA=" + Islem.TOPLA.uygula(3, 4)
                + " CIKAR=" + Islem.CIKAR.uygula(3, 4)
                + " | sinif adi=" + Islem.TOPLA.getClass().getName()
                + " vs " + Islem.class.getName());

        System.out.println("05 implements -> " + Durum.ACIK.etiket());

        // --- valueOf ---
        try {
            Renk.valueOf("mavi");
        } catch (IllegalArgumentException e) {
            System.out.println("06 valueOf(\"mavi\") -> IllegalArgumentException: " + e.getMessage());
        }

        // --- switch: niteliksiz ve nitelikli etiket (Java 21) ---
        String s = switch (r) {
            case KIRMIZI -> "niteliksiz kirmizi";
            case Renk.MAVI -> "NITELIKLI mavi (Java 21)";
            case YESIL -> "niteliksiz yesil";
        };
        System.out.println("07 switch -> " + s);

        // --- == guvenlidir ---
        System.out.println("08 == karsilastirma -> " + (r == Renk.MAVI)
                + " | equals -> " + r.equals(Renk.MAVI));

        // --- EnumSet / EnumMap ---
        EnumSet<Renk> hepsi = EnumSet.allOf(Renk.class);
        EnumSet<Renk> bir = EnumSet.of(Renk.MAVI);
        EnumSet<Renk> tersi = EnumSet.complementOf(bir);
        EnumSet<Renk> aralik = EnumSet.range(Renk.KIRMIZI, Renk.YESIL);
        System.out.println("09 EnumSet -> hepsi=" + hepsi + " tersi=" + tersi + " aralik=" + aralik);

        EnumMap<Renk, Integer> m = new EnumMap<>(Renk.class);
        m.put(Renk.MAVI, 2);
        m.put(Renk.KIRMIZI, 1);
        System.out.println("10 EnumMap (ordinal sirasinda) -> " + m);
    }
}
