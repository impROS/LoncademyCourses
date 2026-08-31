/*
 * 6.2 — Tembellik, dikey akis, ara islemler ve Optional.
 *
 *     java Streamler.java
 */
import java.util.*;
import java.util.stream.*;

public class Streamler {

    static int mapSayaci = 0;

    static String pahali() { System.out.print("[hesaplandi] "); return "yedek"; }

    public static void main(String[] args) {

        // --- 01: TEMBELLIK — terminal islem yok, hicbir sey calismaz ---
        System.out.print("01 tembellik -> ");
        Stream.of("a", "b").peek(s -> System.out.print("PEEK" + s));
        System.out.println("(yukarida hicbir cikti olmamali)");

        // --- 02: stream TEK KULLANIMLIK ---
        Stream<String> s = Stream.of("a", "b");
        s.count();
        try {
            s.count();
        } catch (IllegalStateException e) {
            System.out.println("02 tekrar kullanim -> IllegalStateException");
        }

        // --- 03: DIKEY akis ---
        System.out.print("03 dikey akis -> ");
        Stream.of("a", "b")
              .peek(x -> System.out.print("peek:" + x + " "))
              .map(x -> { System.out.print("map:" + x + " "); return x; })
              .forEach(x -> System.out.print("son:" + x + "  "));
        System.out.println();

        // --- 04: kisa devre — limit(2) yuzunden map az calisir ---
        mapSayaci = 0;
        List<Integer> ilkIki = Stream.iterate(1, x -> x + 1)
                                     .map(x -> { mapSayaci++; return x * 10; })
                                     .limit(2)
                                     .toList();
        System.out.println("04 kisa devre -> " + ilkIki + " | map calisma sayisi=" + mapSayaci);

        // --- 05: filter vs takeWhile vs dropWhile ---
        List<Integer> veri = List.of(1, 2, 3, 1, 2);
        System.out.println("05 filter=" + veri.stream().filter(x -> x < 3).toList()
                + " takeWhile=" + veri.stream().takeWhile(x -> x < 3).toList()
                + " dropWhile=" + veri.stream().dropWhile(x -> x < 3).toList());

        // --- 06: map vs flatMap ---
        List<List<String>> ic = List.of(List.of("a", "b"), List.of("c"));
        System.out.println("06 map=" + ic.stream().map(List::size).toList()
                + " flatMap=" + ic.stream().flatMap(List::stream).toList());

        // --- 07: range vs rangeClosed, distinct, skip, sorted ---
        System.out.println("07 range=" + IntStream.range(1, 5).boxed().toList()
                + " rangeClosed=" + IntStream.rangeClosed(1, 5).boxed().toList()
                + " distinct=" + Stream.of(1, 1, 2).distinct().toList()
                + " skip=" + Stream.of(1, 2, 3).skip(1).toList()
                + " sorted=" + Stream.of(3, 1, 2).sorted().toList());

        // --- 08: orElse vs orElseGet ---
        System.out.print("08 orElse -> ");
        System.out.print(Optional.of("var").orElse(pahali()) + "   |   orElseGet -> ");
        System.out.println(Optional.of("var").orElseGet(Streamler::pahali));

        // --- 09: Optional temelleri ---
        System.out.println("09 ofNullable(null)=" + Optional.ofNullable(null)
                + " isEmpty=" + Optional.empty().isEmpty()
                + " map(null uretir)=" + Optional.of("x").map(v -> (String) null)
                + " filter=" + Optional.of("abc").filter(v -> v.length() > 5));
        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("   Optional.of(null) -> NullPointerException");
        }
        try {
            Optional.empty().get();
        } catch (NoSuchElementException e) {
            System.out.println("   bos Optional.get() -> NoSuchElementException: " + e.getMessage());
        }

        // --- 10: sonsuz stream + limit dogru sirada ---
        System.out.println("10 sonsuz+limit -> "
                + Stream.iterate(1, x -> x + 1).limit(5).sorted(Comparator.reverseOrder()).toList());
        // Stream.iterate(1, x -> x + 1).sorted().limit(5)  <- ASLA BITMEZ

        // --- 11: uc argumanli iterate (Java 9+) SONLUDUR ---
        System.out.println("11 sonlu iterate -> "
                + Stream.iterate(1, x -> x < 10, x -> x * 2).toList());
    }
}
