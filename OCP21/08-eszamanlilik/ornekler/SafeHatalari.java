/*
 * 8.3 — DERLENMEYEN thread-safety ornekleri.
 *
 *     javac -d /tmp/ocp SafeHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.concurrent.locks.*;

public class SafeHatalari {

    // HATA 1: wait() checked InterruptedException atar
    // DUZELTME: try/catch ekle ya da throws bildir.
    static void h1(Object kilit) {
        synchronized (kilit) { kilit.wait(); }
    }

    // HATA 2: yerel degisken synchronized kilidi olarak kullanilabilir ama
    //         PRIMITIF olamaz (nesne gerekir)
    // DUZELTME: Object kilit = new Object();
    static void h2() {
        int kilit = 0;
        synchronized (kilit) { }
    }

    // HATA 3: volatile YEREL degiskene uygulanamaz (yalnizca alanlara)
    // DUZELTME: alanı sinif duzeyine tasi.
    static void h3() {
        volatile int x = 0;
    }

    // HATA 4: Lock bir arayuzdur, dogrudan new edilemez
    // DUZELTME: new ReentrantLock() yaz.
    static void h4() {
        Lock l = new Lock();
    }

    // HATA 5: synchronized bir metot degil, belirtec
    // DUZELTME: void m() { synchronized (this) { } }
    static void h5() {
        synchronized();
    }

    // HATA 6: AtomicInteger'a dogrudan int atanamaz
    // DUZELTME: a.set(5) yaz.
    static void h6() {
        java.util.concurrent.atomic.AtomicInteger a = new java.util.concurrent.atomic.AtomicInteger();
        a = 5;
    }

    public static void main(String[] args) { }
}
