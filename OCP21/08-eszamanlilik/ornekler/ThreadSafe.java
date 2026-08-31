/*
 * 8.3 — Yaris durumu, atomic, kilitler ve concurrent koleksiyonlar.
 *
 *     java ThreadSafe.java
 *
 * DIKKAT: 01 satirini gormek icin programi BIRKAC KEZ calistir; sayi her seferinde degisir.
 */
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

public class ThreadSafe {

    static int guvensiz = 0;
    static volatile int volatil = 0;              // volatile ATOMIKLIK VERMEZ
    static final AtomicInteger atomik = new AtomicInteger();
    static int kilitli = 0;
    static final Object KILIT = new Object();

    static synchronized void artirSync() { kilitli++; }

    public static void main(String[] args) throws Exception {

        // --- 01: yaris durumu ---
        try (var e = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < 10_000; i++) {
                e.submit(() -> {
                    guvensiz++;
                    volatil++;
                    atomik.incrementAndGet();
                    artirSync();
                });
            }
        }
        System.out.println("01 yaris durumu (beklenen 10000):");
        System.out.println("   guvensiz int : " + guvensiz);
        System.out.println("   volatile int : " + volatil + "  <- volatile de KURTARMAZ");
        System.out.println("   AtomicInteger: " + atomik.get());
        System.out.println("   synchronized : " + kilitli);

        // --- 02: atomic metotlarin donusleri ---
        AtomicInteger a = new AtomicInteger(5);
        System.out.println("02 atomic -> getAndIncrement=" + a.getAndIncrement()
                + " (sonra " + a.get() + ") incrementAndGet=" + a.incrementAndGet()
                + " compareAndSet(7,100)=" + a.compareAndSet(7, 100)
                + " updateAndGet(x*2)=" + a.updateAndGet(x -> x * 2));

        // --- 03: kilitsiz wait ---
        try {
            new Object().wait();
        } catch (IllegalMonitorStateException e) {
            System.out.println("03 kilitsiz wait -> IllegalMonitorStateException");
        }

        // --- 04: CopyOnWriteArrayList CME ATMAZ ---
        List<String> cow = new CopyOnWriteArrayList<>(List.of("a", "b"));
        for (String s : cow) cow.add("x");
        System.out.println("04 CopyOnWriteArrayList -> " + cow + " (CME YOK, iterator eski kopyayi gezdi)");

        List<String> normal = new ArrayList<>(List.of("a", "b"));
        try {
            for (String s : normal) normal.add("x");
        } catch (ConcurrentModificationException e) {
            System.out.println("   ArrayList ayni islem -> ConcurrentModificationException");
        }

        // --- 05: ConcurrentHashMap null kabul etmez ---
        try {
            new ConcurrentHashMap<String, String>().put(null, "x");
        } catch (NullPointerException e) {
            System.out.println("05 ConcurrentHashMap null anahtar -> NullPointerException");
        }
        System.out.println("   HashMap null anahtar -> kabul eder: " + new HashMap<String, String>() {{ put(null, "x"); }});

        // --- 06: ReentrantLock ---
        Lock kilit = new ReentrantLock();
        kilit.lock();
        try { System.out.println("06 ReentrantLock -> kritik bolge"); }
        finally { kilit.unlock(); }
        System.out.println("   tryLock=" + kilit.tryLock());
        kilit.unlock();
        try {
            kilit.unlock();                       // kilit alinmadan unlock
        } catch (IllegalMonitorStateException e) {
            System.out.println("   kilitsiz unlock -> IllegalMonitorStateException");
        }

        // --- 07: BlockingQueue ---
        BlockingQueue<Integer> bq = new ArrayBlockingQueue<>(1);
        bq.put(1);
        System.out.println("07 BlockingQueue -> offer(dolu)=" + bq.offer(2)
                + " offer(timeout)=" + bq.offer(2, 50, TimeUnit.MILLISECONDS)
                + " take=" + bq.take() + " poll(bos)=" + bq.poll());

        // --- 08: wait/notify dogru kullanim ---
        Thread bekleyen = new Thread(() -> {
            synchronized (KILIT) {
                try { KILIT.wait(500); } catch (InterruptedException e) { }
                System.out.println("08 wait/notify -> uyandi");
            }
        });
        bekleyen.start();
        Thread.sleep(100);
        synchronized (KILIT) { KILIT.notifyAll(); }
        bekleyen.join();
    }
}
