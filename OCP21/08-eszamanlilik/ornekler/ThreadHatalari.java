/*
 * 8.1 — DERLENMEYEN thread ornekleri.
 *
 *     javac -d /tmp/ocp ThreadHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.concurrent.*;

public class ThreadHatalari {

    // HATA 1: Thread.sleep CHECKED InterruptedException atar
    // DUZELTME: try/catch ekle ya da 'throws InterruptedException' bildir.
    static void h1() {
        Thread.sleep(100);
    }

    // HATA 2: join() de checked InterruptedException atar
    // DUZELTME: imzaya throws InterruptedException ekle.
    static void h2(Thread t) {
        t.join();
    }

    // HATA 3: Runnable.run() checked exception atamaz (SAM bildirmez)
    // DUZELTME: try/catch ile sar.
    static void h3() {
        Runnable r = () -> Thread.sleep(100);
    }

    // HATA 4: Thread constructor'i Callable KABUL ETMEZ
    // DUZELTME: Runnable ver ya da ExecutorService kullan (8.2).
    static void h4() {
        Callable<Integer> c = () -> 42;
        Thread t = new Thread(c);
    }

    // HATA 5: ofVirtual() bir Thread degil, Thread.Builder doner
    // DUZELTME: .unstarted(() -> {}) ya da .start(() -> {}) ekle.
    static void h5() {
        Thread t = Thread.ofVirtual();
    }

    // HATA 6: Thread sinifinda 'startVirtual' diye bir metot yoktur
    // DUZELTME: Thread.startVirtualThread(...) yaz.
    static void h6() {
        Thread.startVirtual(() -> { });
    }

    public static void main(String[] args) { }
}
