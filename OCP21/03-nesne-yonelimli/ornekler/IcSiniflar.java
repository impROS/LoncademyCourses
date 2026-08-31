/*
 * 3.8 — Dort ic sinif turu, golgeleme ve anonim sinif / lambda 'this' farki.
 *
 *     java IcSiniflar.java
 */
public class IcSiniflar {

    private int x = 1;
    private static int sx = 100;

    // --- static nested: dis ORNEK gerekmez ---
    static class Nested {
        int oku() { return sx; }          // yalnizca statik uyelere erisir
        static String bilgi() { return "static nested"; }
    }

    // --- inner: dis ORNEGE bagli ---
    class Inner {
        int x = 2;
        static int SABIT = 7;             // Java 16+ : inner sinifta static uye SERBEST
        int topla() { return x + IcSiniflar.this.x + sx; }
        void golgeleme() {
            int x = 3;
            System.out.println("03 golgeleme -> " + x + " " + this.x + " " + IcSiniflar.this.x);
        }
    }

    // --- local sinif ---
    void localOrnek() {
        int yakalanan = 5;                // effectively final olmali
        class Yerel {
            static String TIP = "local";  // Java 16+ : local sinifta static uye SERBEST
            void yaz() { System.out.println("04 local -> yakalanan=" + yakalanan
                    + " tip=" + TIP + " disAlan=" + x); }
        }
        new Yerel().yaz();
        // yakalanan = 6;   <- bu satiri acarsan YUKARISI DERLENMEZ
    }

    interface Selam { String ver(); }

    void thisFarki() {
        // anonim sinif: 'this' ANONIM SINIFI gosterir
        Selam anonim = new Selam() {
            @Override public String ver() { return this.getClass().getSimpleName(); }
        };
        // lambda: 'this' CEVRELEYEN SINIFI gosterir
        Selam lambda = () -> this.getClass().getSimpleName();

        System.out.println("06 this farki -> anonim=[" + anonim.ver() + "]"
                + " (bos string = isimsiz sinif) | lambda=[" + lambda.ver() + "]");
    }

    public static void main(String[] args) {

        System.out.println("01 static nested -> " + new Nested().oku() + " " + Nested.bilgi());

        IcSiniflar dis = new IcSiniflar();
        IcSiniflar.Inner ic = dis.new Inner();        // dis.new Ic()  <- sozdizimi
        System.out.println("02 inner -> topla=" + ic.topla() + " SABIT=" + Inner.SABIT);
        // IcSiniflar.Inner hata = new IcSiniflar.Inner();   // DERLENMEZ

        ic.golgeleme();
        dis.localOrnek();

        // --- anonim sinif: ust sinif constructor argumani new Tip(arg) { } ile verilir ---
        Object o = new Object() {
            @Override public String toString() { return "anonim nesne"; }
        };                                            // NOKTALI VIRGUL zorunlu
        System.out.println("05 anonim -> " + o + " | sinif adi=" + o.getClass().getName());

        dis.thisFarki();
    }
}
