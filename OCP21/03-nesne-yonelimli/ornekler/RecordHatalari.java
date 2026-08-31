/*
 * 3.3 — DERLENMEYEN record ornekleri.
 *
 *     javac -d /tmp/ocp RecordHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;

public class RecordHatalari {

    // HATA 1: record'a ornek alan eklenemez
    // DUZELTME: 'static' ekle ya da alani sil.
    record R1(int x) {
        private int y;
    }

    // HATA 2: record'a ornek initializer blogu eklenemez
    // DUZELTME: blogu sil, dogrulamayi compact constructor'a tasi.
    record R2(int x) {
        { System.out.println("ornek blok"); }
    }

    // HATA 3: compact constructor'da alana atanamaz
    // DUZELTME: 'this.x = x * 2;' yerine 'x = x * 2;' yaz.
    record R3(int x) {
        R3 { this.x = x * 2; }
    }

    // HATA 4: ek constructor canonical'a delege etmeli
    // DUZELTME: govdeyi 'this(x, 0);' yap.
    record R4(int x, int y) {
        R4(int x) { this.x = x; this.y = 0; }
    }

    // HATA 5: record baska bir sinifi extends edemez
    // DUZELTME: 'extends ArrayList<String>' kismini sil.
    record R5(int x) extends ArrayList<String> { }

    // HATA 6: accessor adi bilesen adiyla aynidir
    // DUZELTME: getX() yerine x() cagir.
    static void kullan() {
        R1 r = new R1(5);
        System.out.println(r.getX());
    }

    // HATA 7: record'un alanlari private'dir, disaridan erisilemez
    // DUZELTME: r.x() yaz.
    static void kullan2() {
        R1 r = new R1(5);
        System.out.println(r.x);
    }

    public static void main(String[] args) { }
}
