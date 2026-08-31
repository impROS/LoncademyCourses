/*
 * 3.2 — DERLENMEYEN 'var' ve overload ornekleri.
 *
 *     javac -d /tmp/ocp MetotHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;

public class MetotHatalari {

    // HATA 1: var alan (field) olamaz
    // DUZELTME: int sayac = 0;
    var sayac = 0;

    // HATA 2: var metot parametresi olamaz
    // DUZELTME: void m(String s)
    void m(var s) { }

    // HATA 3: var donus tipi olamaz
    // DUZELTME: String n()
    var n() { return "x"; }

    void yerel() {
        // HATA 4: initializer yok, tip cikarilamaz
        // DUZELTME: var a = 0;
        var a;

        // HATA 5: null'dan tip cikarilamaz
        // DUZELTME: String b = null;
        var b = null;

        // HATA 6: dizi kisayolu var ile kullanilamaz
        // DUZELTME: var c = new int[]{1, 2, 3};
        var c = {1, 2, 3};

        // HATA 7: var degiskenin tipi sonradan degismez
        // DUZELTME: son satiri sil.
        var d = 5;
        d = "metin";
    }

    // HATA 8: int -> Integer -> Long zinciri YOK
    // DUZELTME: g(5L) yaz.
    static void g(Long x) { }
    static void cagir() { g(5); }

    // HATA 9: yalnizca donus tipi farkli iki metot ayni imzadir
    // DUZELTME: birinin parametre tipini degistir.
    int h(int x) { return 0; }
    String h(int x) { return ""; }

    public static void main(String[] args) { }
}
