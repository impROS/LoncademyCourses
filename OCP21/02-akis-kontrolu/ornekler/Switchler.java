/*
 * 2.1 — switch deyimi, switch ifadesi, instanceof pattern ve switch pattern matching.
 *
 *     java Switchler.java
 *
 * ONCE TAHMIN ET. Ozellikle 01 (fall-through) ve 06 (null) satirlari sinav sorusudur.
 */
public class Switchler {

    sealed interface Sekil permits Daire, Kare, Ucgen {}
    record Daire(double r) implements Sekil {}
    record Kare(double kenar) implements Sekil {}
    record Ucgen(double taban, double yukseklik) implements Sekil {}

    record Nokta(int x, int y) {}
    record Cizgi(Nokta bas, Nokta son) {}

    public static void main(String[] args) {

        // --- 01: switch DEYIMI, fall-through ---
        System.out.print("01 fall-through -> ");
        int x = 2;
        switch (x) {
            case 1: System.out.print("bir ");
            case 2: System.out.print("iki ");
            case 3: System.out.print("uc ");
            default: System.out.print("varsayilan");
        }
        System.out.println();

        // --- 02: default sonda olmak ZORUNDA degil ---
        System.out.print("02 default ortada -> ");
        switch (9) {
            case 1: System.out.print("bir "); break;
            default: System.out.print("varsayilan "); break;
            case 2: System.out.print("iki "); break;
        }
        System.out.println();

        // --- 03: switch IFADESI, coklu etiket ---
        int gun = 7;
        String ad = switch (gun) {
            case 1, 7 -> "hafta sonu";
            case 2, 3, 4, 5, 6 -> "hafta ici";
            default -> "gecersiz";
        };
        System.out.println("03 switch ifadesi -> " + ad);

        // --- 04: blok + yield ---
        String harf = "C";
        int puan = switch (harf) {
            case "A" -> 4;
            case "B" -> 3;
            default -> {
                int hesap = harf.length();
                yield hesap;              // blok kullanildiysa yield ZORUNLU
            }
        };
        System.out.println("04 yield -> " + puan);

        // --- 05: pattern matching for switch ---
        System.out.println("05 pattern -> " + anlat(42));
        System.out.println("   pattern -> " + anlat(500));
        System.out.println("   pattern -> " + anlat("merhaba"));
        System.out.println("   pattern -> " + anlat(3.14));

        // --- 06: null davranisi ---
        System.out.println("06 null (case null VAR) -> " + anlat(null));
        try {
            System.out.println("   null (case null YOK) -> " + anlatNullsuz(null));
        } catch (NullPointerException e) {
            System.out.println("   null (case null YOK) -> NullPointerException!");
        }

        // --- 07: sealed hiyerarsi, default GEREKMEZ ---
        System.out.println("07 sealed -> " + alan(new Daire(2)) + " / " + alan(new Kare(3))
                + " / " + alan(new Ucgen(4, 5)));

        // --- 08: record pattern, ic ice ayristirma ---
        System.out.println("08 record pattern -> " + tarif(new Cizgi(new Nokta(1, 2), new Nokta(3, 4))));
        System.out.println("   record pattern -> " + tarif(new Nokta(5, 5)));
        System.out.println("   record pattern -> " + tarif(new Nokta(1, 9)));

        // --- 09: instanceof pattern, flow scoping ---
        Object o = "java";
        if (o instanceof String s && s.length() > 2) {
            System.out.println("09 instanceof pattern -> uzunluk " + s.length());
        }
        System.out.println("10 negatif dal -> " + uzunluk("merhaba") + " / " + uzunluk(42));

        // --- 11: Integer selector, unboxing ---
        Integer kutulu = 2;
        switch (kutulu) {
            case 2 -> System.out.println("11 Integer selector -> iki");
            default -> System.out.println("11 Integer selector -> baska");
        }
    }

    static String anlat(Object o) {
        return switch (o) {
            case null -> "null geldi";
            case Integer i when i > 100 -> "buyuk sayi: " + i;   // guard'li ONCE
            case Integer i -> "sayi: " + i;
            case String s -> "metin uzunlugu: " + s.length();
            default -> "bilinmeyen tip: " + o.getClass().getSimpleName();
        };
    }

    static String anlatNullsuz(Object o) {
        return switch (o) {
            case Integer i -> "sayi";
            case String s -> "metin";
            default -> "bilinmeyen";        // default null'i YAKALAMAZ
        };
    }

    // sealed hiyerarsi tam kapsandigi icin default GEREKMEZ
    static double alan(Sekil s) {
        return switch (s) {
            case Daire d -> Math.PI * d.r() * d.r();
            case Kare k -> k.kenar() * k.kenar();
            case Ucgen(double taban, double yuk) -> taban * yuk / 2;
        };
    }

    static String tarif(Object o) {
        return switch (o) {
            case Cizgi(Nokta(var x1, var y1), Nokta p2) -> "cizgi (" + x1 + "," + y1 + ") -> " + p2;
            case Nokta(int x, int y) when x == y -> "kosegen nokta " + x;
            case Nokta n -> "nokta " + n;
            default -> "?";
        };
    }

    static String uzunluk(Object o) {
        if (!(o instanceof String s)) {
            return "String degil";
        }
        return "uzunluk " + s.length();     // s burada kesinlesti
    }
}
