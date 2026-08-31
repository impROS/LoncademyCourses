/*
 * 8.2 — DERLENMEYEN ExecutorService ornekleri.
 *
 *     javac -d /tmp/ocp ExecutorHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;
import java.util.concurrent.*;

public class ExecutorHatalari {

    // HATA 1: execute() void doner
    // DUZELTME: submit() kullan.
    static void h1(ExecutorService e) {
        Future<?> f = e.execute(() -> { });
    }

    // HATA 2: Future.get() checked exception atar
    // DUZELTME: try/catch ekle ya da throws bildir.
    static int h2(Future<Integer> f) {
        return f.get();
    }

    // HATA 3: invokeAny Future degil, DEGERIN kendisini doner
    // DUZELTME: Integer r = ... yaz.
    static void h3(ExecutorService e) throws Exception {
        Future<Integer> r = e.invokeAny(List.of(() -> 1));
    }

    // HATA 4: invokeAll List<Future<T>> doner, List<T> degil
    // DUZELTME: List<Future<Integer>> yaz.
    static void h4(ExecutorService e) throws Exception {
        List<Integer> r = e.invokeAll(List.of(() -> 1));
    }

    // HATA 5: shutdownNow List<Runnable> doner
    // DUZELTME: List<Runnable> kalanlar = ... yaz.
    static void h5(ExecutorService e) {
        List<Future<?>> kalanlar = e.shutdownNow();
    }

    // HATA 6: Runnable checked exception atamaz
    // DUZELTME: Callable kullan (submit overload'i secilir).
    static void h6(ExecutorService e) {
        Runnable r = () -> { throw new Exception("x"); };
        e.submit(r);
    }

    // HATA 7: ScheduledExecutorService gerekir, ExecutorService'te schedule yoktur
    // DUZELTME: Executors.newScheduledThreadPool(1) kullan.
    static void h7(ExecutorService e) {
        e.schedule(() -> { }, 1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) { }
}
