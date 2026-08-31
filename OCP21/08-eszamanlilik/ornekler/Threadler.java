/*
 * 8.1 — Platform ve virtual thread'ler.
 *
 *     java Threadler.java
 *
 * ONCE tahmin et: 02 numarali satir hangi thread adini yazdirir?
 */
import java.util.concurrent.*;

public class Threadler {

    public static void main(String[] args) throws Exception {

        // --- 01: klasik olusturma ---
        Thread t1 = new Thread(() -> System.out.println("01 klasik -> " + Thread.currentThread().getName()));
        t1.start();
        t1.join();

        // --- 02: start() vs run() ---
        Thread t2 = new Thread(() -> System.out.println("   calisan thread: " + Thread.currentThread().getName()));
        System.out.print("02 start vs run -> start(): ");
        t2.start();
        t2.join();
        System.out.print("                   run()  : ");
        t2.run();                       // SIRADAN metot cagrisi -> main

        // --- 03: ikinci start ---
        try {
            t2.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("03 ikinci start -> IllegalThreadStateException");
        }

        // --- 04: thread durumlari ---
        Thread bekleyen = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) { }
        });
        System.out.print("04 durumlar -> NEW=" + bekleyen.getState());
        bekleyen.start();
        Thread.sleep(50);
        System.out.print(" calisirken=" + bekleyen.getState());
        bekleyen.join();
        System.out.println(" bitince=" + bekleyen.getState());

        // --- 05: virtual thread ozellikleri ---
        Thread v = Thread.ofVirtual().name("v-1").start(() -> { });
        v.join();
        System.out.println("05 virtual ozellikleri -> isVirtual=" + v.isVirtual()
                + " isDaemon=" + v.isDaemon() + " name=" + v.getName());

        Thread vt = Thread.ofVirtual().unstarted(() -> { });
        System.out.print("   priority ayari -> ");
        vt.setPriority(10);
        System.out.print("kabul edildi ama deger=" + vt.getPriority() + " (yok sayilir)  |  ");
        try {
            vt.setDaemon(false);
        } catch (IllegalArgumentException e) {
            System.out.println("setDaemon(false) -> IllegalArgumentException");
        }

        // --- 06: platform thread builder ---
        Thread p = Thread.ofPlatform().name("islem-1").daemon().unstarted(() -> { });
        System.out.println("06 platform builder -> name=" + p.getName()
                + " isDaemon=" + p.isDaemon() + " isVirtual=" + p.isVirtual()
                + " state=" + p.getState());

        // --- 07: startVirtualThread kisayolu ---
        Thread k = Thread.startVirtualThread(() -> System.out.println("07 kisayol -> calisti"));
        k.join();

        // --- 08: interrupt ---
        Thread kesilen = new Thread(() -> {
            try { Thread.sleep(5000); }
            catch (InterruptedException e) { System.out.println("08 interrupt -> InterruptedException yakalandi"); }
        });
        kesilen.start();
        Thread.sleep(50);
        kesilen.interrupt();
        kesilen.join();

        // --- 09: OLCEK TESTI (yorumu kaldir) ---
        // olcekTesti();
    }

    static void olcekTesti() throws Exception {
        long t0 = System.currentTimeMillis();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                exec.submit(() -> { Thread.sleep(100); return null; });
            }
        }
        System.out.println("09 10.000 VIRTUAL thread -> " + (System.currentTimeMillis() - t0) + " ms");

        long t1 = System.currentTimeMillis();
        try (var exec = Executors.newFixedThreadPool(200)) {
            for (int i = 0; i < 10_000; i++) {
                exec.submit(() -> { Thread.sleep(100); return null; });
            }
        }
        System.out.println("   200 PLATFORM thread havuzu -> " + (System.currentTimeMillis() - t1) + " ms");
    }
}
