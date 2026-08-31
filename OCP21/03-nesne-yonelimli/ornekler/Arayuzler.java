/*
 * 3.6 — Arayuzlerde dort metot tipi, statik kalitilmama ve cakisma cozumu.
 *
 *     java Arayuzler.java
 *
 * DENEY: Cakisma sinifindan public String m() metodunu sil, derlemeyi dene.
 */
public class Arayuzler {

    interface Arac {
        int TEKER = 4;                                    // public static final

        String ad();                                      // public abstract

        default String tanit() { return "Arac: " + ad() + kod(); }   // default

        static Arac bos() { return () -> "yok"; }         // static -> KALITILMAZ

        private String kod() { return "#" + hash(); }     // private (Java 9+)

        private static int hash() { return 42; }          // private static
    }

    static class Araba implements Arac {
        @Override public String ad() { return "araba"; }  // public ZORUNLU
    }

    // --- sinif, arayuz default'unu YENER ---
    interface I { default String m() { return "I"; } }
    static class Ust { public String m() { return "Ust"; } }
    static class Alt extends Ust implements I { }

    // --- iki bagimsiz arayuz ayni default'u verirse: OVERRIDE ZORUNLU ---
    interface A { default String m() { return "A"; } }
    interface B { default String m() { return "B"; } }
    static class Cakisma implements A, B {
        @Override public String m() { return A.super.m() + "+" + B.super.m(); }
    }

    // --- daha spesifik arayuz kazanir ---
    interface X { default String m() { return "X"; } }
    interface Y extends X { default String m() { return "Y"; } }
    static class Spesifik implements X, Y { }

    // --- fonksiyonel arayuz: Object metotlari SAYILMAZ ---
    @FunctionalInterface
    interface Karsilastir<T> {
        int karsilastir(T a, T b);                        // tek abstract
        boolean equals(Object o);                         // Object'ten -> sayilmaz
        default Karsilastir<T> ters() { return (a, b) -> karsilastir(b, a); }
    }

    public static void main(String[] args) {

        System.out.println("01 default -> " + new Araba().tanit() + " | sabit TEKER=" + Arac.TEKER);

        System.out.println("02 static -> Arac.bos().ad() = " + Arac.bos().ad());
        // System.out.println(Araba.bos());   // DERLENMEZ: arayuz statikleri kalitilmaz

        System.out.println("03 sinif kazanir -> " + new Alt().m());

        System.out.println("04 cakisma cozumu -> " + new Cakisma().m());

        System.out.println("05 spesifik kazanir -> " + new Spesifik().m());

        Karsilastir<Integer> k = (a, b) -> a - b;
        System.out.println("06 fonksiyonel arayuz -> " + k.karsilastir(3, 7)
                + " | ters -> " + k.ters().karsilastir(3, 7));
    }
}
