/*
 * 3.4 — DERLENMEYEN override ornekleri.
 *
 *     javac -d /tmp/ocp KalitimHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.io.IOException;

public class KalitimHatalari {

    static class A {
        public void m() { }
        public String f() { return ""; }
        public final void kilitli() { }
        public void h() throws IOException { }
        private void gizli() { }
    }

    // HATA 1: erisim daraltilamaz (public -> paket ozel)
    // DUZELTME: public void m() yaz.
    static class B1 extends A {
        void m() { }
    }

    // HATA 2: donus tipi genisletilemez (String -> Object)
    // DUZELTME: public String f() yaz.
    static class B2 extends A {
        public Object f() { return null; }
    }

    // HATA 3: final metot override edilemez
    // DUZELTME: metodu tamamen sil.
    static class B3 extends A {
        public void kilitli() { }
    }

    // HATA 4: yeni/daha genis checked exception eklenemez
    // DUZELTME: throws Exception yerine throws IOException ya da hicbir sey yaz.
    static class B4 extends A {
        public void h() throws Exception { }
    }

    // HATA 5: @Override yazildi ama ust sinifta boyle bir metot yok
    // (gizli() private, kalitilmaz)
    // DUZELTME: @Override anotasyonunu sil.
    static class B5 extends A {
        @Override void gizli() { }
    }

    // HATA 6: super.super diye bir sozdizimi yoktur
    // DUZELTME: satiri sil.
    static class B6 extends B1 {
        void deneme() { super.super.m(); }
    }

    // HATA 7: iliskisiz tipler arasinda cast
    // DUZELTME: Object uzerinden gec: (String)(Object) i
    static void cast() {
        Integer i = 5;
        String s = (String) i;
    }

    public static void main(String[] args) { }
}
