/*
 * 3.6 — DERLENMEYEN arayuz ornekleri.
 *
 *     javac -d /tmp/ocp ArayuzHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
public class ArayuzHatalari {

    interface I1 {
        // HATA 1: arayuz alanlari public static final'dir, deger ZORUNLU
        // DUZELTME: int SABIT = 5;
        int SABIT;
    }

    interface I2 {
        // HATA 2: arayuz metotlari ortuk public'tir, protected yazilamaz
        // DUZELTME: protected'i sil.
        protected void m();
    }

    interface I3 {
        // HATA 3: private arayuz metodunun GOVDESI zorunludur
        // DUZELTME: { } ekle.
        private void p();
    }

    interface I4 { void m(); }
    // HATA 4: uygulayan sinif metodu public yazmali (erisim daraltilamaz)
    // DUZELTME: public ekle.
    static class C4 implements I4 { void m() { } }

    interface I5 { static void s() { } }
    // HATA 5: arayuzun static metodu KALITILMAZ
    // DUZELTME: I5.s() yaz.
    static class C5 implements I5 { void cagir() { s(); } }

    interface A6 { default String m() { return "A"; } }
    interface B6 { default String m() { return "B"; } }
    // HATA 6: iki bagimsiz arayuzden ayni default -> override ZORUNLU
    // DUZELTME: public String m() { return A6.super.m(); } ekle.
    static class C6 implements A6, B6 { }

    // HATA 7: iki abstract metot varsa @FunctionalInterface derlenmez
    // DUZELTME: birini default yap ya da anotasyonu sil.
    @FunctionalInterface
    interface I7 { void a(); void b(); }

    // HATA 8: arayuz somut sinif genisletemez
    // DUZELTME: extends kismini sil.
    interface I8 extends String { }

    public static void main(String[] args) { }
}
