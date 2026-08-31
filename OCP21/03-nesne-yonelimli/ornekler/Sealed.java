/*
 * 3.5 — Abstract siniflar, sealed hiyerarsi ve exhaustive switch.
 *
 *     java Sealed.java
 *
 * DENEY 1: alan(Sekil) icinden 'case Ucgen u ->' satirini sil, tekrar calistir.
 * DENEY 2: permits listesine yeni bir tip ekle; alan() metodunun bozuldugunu gor.
 */
public class Sealed {

    // --- sealed hiyerarsi: alt tipler ayni dosyada oldugu icin permits ZORUNLU DEGIL,
    //     ama acikca yazmak okunabilirligi artirir.
    sealed interface Sekil permits Daire, Kare, Ucgen { }

    record Daire(double r) implements Sekil { }          // record: implicitly final
    record Kare(double kenar) implements Sekil { }        // record: implicitly final
    non-sealed static class Ucgen implements Sekil {      // zinciri yeniden acar
        final double taban, yukseklik;
        Ucgen(double t, double y) { taban = t; yukseklik = y; }
        double alan() { return taban * yukseklik / 2; }
    }

    // non-sealed oldugu icin bunu herkes genisletebilir
    static class DikUcgen extends Ucgen {
        DikUcgen(double t, double y) { super(t, y); }
        @Override double alan() { return super.alan(); }
        String tanit() { return "dik ucgen"; }
    }

    // --- abstract sinif: constructor'i VAR, new edilemez ---
    static abstract class Hayvan {
        private final String ad;
        Hayvan(String ad) { this.ad = ad; System.out.print("[Hayvan ctor] "); }
        abstract String ses();                       // govde YOK
        String tanit() { return ad + " -> " + ses(); }
    }

    static class Kedi extends Hayvan {
        Kedi() { super("kedi"); }
        @Override String ses() { return "miyav"; }
    }

    // abstract alt sinif: uygulamak ZORUNDA DEGIL, sorumlulugu asagi devreder
    static abstract class Kus extends Hayvan {
        Kus(String ad) { super(ad); }
    }

    static class Serce extends Kus {
        Serce() { super("serce"); }
        @Override String ses() { return "cik cik"; }
    }

    public static void main(String[] args) {

        System.out.println("01 record alt tip -> " + new Daire(2) + " " + new Kare(3));

        System.out.println("02 non-sealed genisletme -> " + new DikUcgen(4, 5).tanit()
                + " alan=" + new DikUcgen(4, 5).alan());

        // --- exhaustive switch: default GEREKMEZ ---
        Sekil[] sekiller = { new Daire(1), new Kare(2), new Ucgen(3, 4) };
        System.out.print("03 exhaustive switch ->");
        for (Sekil s : sekiller) {
            System.out.printf(" %.2f", alan(s));
        }
        System.out.println();

        // --- abstract sinif davranisi ---
        System.out.print("04 abstract ctor -> ");
        System.out.println(new Kedi().tanit());
        System.out.print("05 abstract alt sinif -> ");
        System.out.println(new Serce().tanit());

        // new Hayvan("x");   // DERLENMEZ: abstract sinif new edilemez
    }

    // sealed sayesinde default YOK
    static double alan(Sekil s) {
        return switch (s) {
            case Daire d -> Math.PI * d.r() * d.r();
            case Kare k -> k.kenar() * k.kenar();
            case Ucgen u -> u.alan();
        };
    }
}
