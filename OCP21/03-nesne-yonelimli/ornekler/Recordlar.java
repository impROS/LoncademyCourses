/*
 * 3.3 — Record'larin urettikleri, compact constructor ve shallow immutability.
 *
 *     java Recordlar.java
 */
import java.util.*;

public class Recordlar {

    record Nokta(int x, int y) { }

    record Aralik(int alt, int ust) {
        // compact constructor: parantez YOK, alana atama YOK
        Aralik {
            if (alt > ust) throw new IllegalArgumentException("alt > ust: " + alt + ">" + ust);
            alt = Math.max(alt, 0);        // PARAMETREYI degistiriyoruz
        }
        // ek constructor: canonical'a delege ZORUNLU
        Aralik(int ust) { this(0, ust); }

        static final String ETIKET = "aralik";     // statik alan SERBEST
        int uzunluk() { return ust - alt; }        // ornek metot SERBEST
    }

    record Kutu(List<String> icerik) { }

    record GuvenliKutu(List<String> icerik) {
        GuvenliKutu { icerik = List.copyOf(icerik); }   // savunmaci kopya
    }

    record Kutu2<T>(T deger) { }                   // generic record

    public static void main(String[] args) {

        // --- 01/02: uretilen uyeler ---
        var n = new Nokta(1, 2);
        System.out.println("01 accessor -> " + n.x() + " " + n.y());
        System.out.println("02 toString -> " + n);
        System.out.println("03 equals/hashCode -> " + n.equals(new Nokta(1, 2))
                + " " + (n.hashCode() == new Nokta(1, 2).hashCode()));

        // --- 04: compact constructor ve ek constructor ---
        System.out.println("04 compact -> " + new Aralik(-5, 10)
                + " | ek ctor -> " + new Aralik(7)
                + " | uzunluk=" + new Aralik(2, 9).uzunluk());
        try {
            new Aralik(10, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("   dogrulama -> IllegalArgumentException: " + e.getMessage());
        }

        // --- 05: SHALLOW IMMUTABLE ---
        var liste = new ArrayList<String>(List.of("a"));
        var k = new Kutu(liste);
        System.out.println("05 shallow immutable -> once: " + k);
        liste.add("b");
        System.out.println("   disaridaki liste degisti -> simdi: " + k);

        var liste2 = new ArrayList<String>(List.of("a"));
        var g = new GuvenliKutu(liste2);
        liste2.add("b");
        System.out.println("06 savunmaci kopya -> " + g + " (etkilenmedi)");

        // --- 07: generic record ---
        System.out.println("07 generic -> " + new Kutu2<String>("merhaba").deger());

        // --- 08: record pattern ---
        System.out.println("08 record pattern -> " + tarif(new Nokta(3, 3)));
        System.out.println("   record pattern -> " + tarif(new Nokta(3, 9)));
        System.out.println("   record pattern -> " + tarif("baska"));
    }

    static String tarif(Object o) {
        return switch (o) {
            case Nokta(int x, int y) when x == y -> "kosegen " + x;
            case Nokta(int x, int y) -> "nokta " + x + "," + y;
            default -> "bilinmeyen";
        };
    }
}
