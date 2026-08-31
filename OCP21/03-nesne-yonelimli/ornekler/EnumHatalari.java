/*
 * 3.7 — DERLENMEYEN enum ornekleri.
 *
 *     javac -d /tmp/ocp EnumHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;

public class EnumHatalari {

    // HATA 1: sabitlerden sonra baska uye varsa noktali virgul ZORUNLU
    // DUZELTME: 'B' den sonra ; koy.
    enum E1 {
        A, B
        void m() { }
    }

    // HATA 2: enum constructor'i ortuk private'dir
    // DUZELTME: public'i sil.
    enum E2 {
        A;
        public E2() { }
    }

    // HATA 3: enum new ile uretilemez
    // DUZELTME: E3.A yaz.
    enum E3 { A }
    static E3 uret() { return new E3(); }

    // HATA 4: enum baska bir sinifi extends edemez
    // DUZELTME: extends kismini sil.
    enum E4 extends ArrayList<String> { A }

    // HATA 5: abstract metot bildirildiyse HER sabit govde vermeli
    // DUZELTME: B sabitine de govde ekle.
    enum E5 {
        A { public int f() { return 1; } },
        B;
        public abstract int f();
    }

    // HATA 6: name() final'dir, override edilemez
    // DUZELTME: metodu sil (toString override edilebilir).
    enum E6 {
        A;
        public String name() { return "x"; }
    }

    // HATA 7: EnumSet'in public constructor'i yoktur
    // DUZELTME: EnumSet.noneOf(E3.class) yaz.
    static Set<E3> kume() { return new EnumSet<E3>(); }

    public static void main(String[] args) { }
}
