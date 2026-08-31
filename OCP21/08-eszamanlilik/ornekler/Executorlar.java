/*
 * 8.2 — ExecutorService, Callable/Runnable, Future ve kapatma.
 *
 *     java Executorlar.java
 */
import java.util.*;
import java.util.concurrent.*;

public class Executorlar {

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newFixedThreadPool(2);

        // --- 01: Callable deger doner ---
        Future<Integer> f1 = exec.submit(() -> 42);
        System.out.println("01 Callable -> get=" + f1.get() + " isDone=" + f1.isDone());

        // --- 02: Runnable get() NULL doner ---
        Future<?> f2 = exec.submit(() -> System.out.print("02 Runnable -> calisti, get="));
        System.out.println(f2.get());

        // --- 03: SESSIZ hata (get cagrilmiyor) ---
        exec.submit(() -> { throw new IllegalStateException("bu hata KAYBOLACAK"); });
        Thread.sleep(100);
        System.out.println("03 sessiz hata -> yukarida hicbir hata mesaji YOK");

        // --- 04: get() ile hata ortaya cikar ---
        Future<Integer> f4 = exec.submit(() -> { throw new IllegalStateException("gorev patladi"); });
        try {
            f4.get();
        } catch (ExecutionException e) {
            System.out.println("04 get() -> ExecutionException | getCause=" + e.getCause());
        }

        // --- 05: get(timeout) ---
        Future<Integer> f5 = exec.submit(() -> { Thread.sleep(2000); return 1; });
        try {
            f5.get(200, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("05 get(timeout) -> TimeoutException");
        }
        f5.cancel(true);
        System.out.println("   cancel sonrasi isCancelled=" + f5.isCancelled() + " isDone=" + f5.isDone());

        // --- 07: invokeAll vs invokeAny ---
        List<Callable<Integer>> gorevler = List.of(() -> 1, () -> 2, () -> 3);
        List<Future<Integer>> hepsi = exec.invokeAll(gorevler);
        System.out.print("07 invokeAll -> liste doner, hepsi bitti mi="
                + hepsi.stream().allMatch(Future::isDone) + " degerler=");
        for (Future<Integer> f : hepsi) System.out.print(f.get() + " ");
        System.out.println("| invokeAny -> tek DEGER doner = " + exec.invokeAny(gorevler));

        // --- 06: kapatma ---
        exec.shutdown();
        System.out.println("06 kapatma -> isShutdown=" + exec.isShutdown()
                + " isTerminated=" + exec.isTerminated());
        boolean bitti = exec.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("   awaitTermination=" + bitti + " isTerminated=" + exec.isTerminated());

        try {
            exec.submit(() -> 1);
        } catch (RejectedExecutionException e) {
            System.out.println("   kapaliya submit -> RejectedExecutionException");
        }

        // --- 08: try-with-resources (Java 19+) ---
        System.out.print("08 AutoCloseable -> ");
        try (var e2 = Executors.newVirtualThreadPerTaskExecutor()) {
            e2.submit(() -> { Thread.sleep(100); System.out.print("gorev bitti, "); return null; });
        }
        System.out.println("close() gorevi BEKLEDI");

        // --- 09: zamanlanmis gorev ---
        try (var s = Executors.newScheduledThreadPool(1)) {
            ScheduledFuture<String> sf = s.schedule(() -> "09 schedule -> 300ms sonra calisti", 300, TimeUnit.MILLISECONDS);
            System.out.println(sf.get());
        }
    }
}
