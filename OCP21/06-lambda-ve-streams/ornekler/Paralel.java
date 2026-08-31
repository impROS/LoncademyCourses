/*
 * 6.4 — Primitif stream donusumleri ve paralel stream tuzaklari.
 *
 *     java Paralel.java
 *
 * DIKKAT: 05 ve 06 numarali satirlar HER CALISTIRMADA farkli sonuc verebilir.
 */
import java.util.*;
import java.util.stream.*;

public class Paralel {

    public static void main(String[] args) {

        // --- 01: tip donusumleri ---
        List<Integer> boxed = IntStream.range(1, 4).boxed().toList();
        List<String> obj = IntStream.range(1, 4).mapToObj(i -> "#" + i).toList();
        int toplam = Stream.of("a", "bb").mapToInt(String::length).sum();
        System.out.println("01 donusum -> boxed=" + boxed + " mapToObj=" + obj + " mapToInt.sum=" + toplam);

        // --- 02: primitif terminaller ---
        System.out.println("02 terminaller -> sum=" + IntStream.of(1, 2, 3).sum()
                + " average=" + IntStream.of(1, 2, 3).average()
                + " max=" + IntStream.of(1, 2, 3).max()
                + " getAsInt=" + IntStream.of(1, 2, 3).max().getAsInt());

        // --- 03: BOS primitif stream ---
        System.out.println("03 bos -> sum=" + IntStream.of().sum()
                + " average=" + IntStream.of().average()
                + " max=" + IntStream.of().max()
                + " count=" + IntStream.of().count());

        // --- 04: chars() IntStream doner ---
        System.out.print("04 chars() -> ");
        "abc".chars().forEach(System.out::print);
        System.out.print("  |  mapToObj ile: ");
        "abc".chars().mapToObj(c -> (char) c).forEach(System.out::print);
        System.out.println();

        // --- 05: YANLIS identity — sirali ve paralel FARKLI ---
        String sirali = Stream.of("a", "b", "c").reduce("X", String::concat);
        String paralel = Stream.of("a", "b", "c").parallel().reduce("X", String::concat);
        System.out.println("05 yanlis identity -> sirali=" + sirali + "  paralel=" + paralel);
        String dogru = Stream.of("a", "b", "c").parallel().reduce("", String::concat);
        System.out.println("   dogru identity (\"\") -> " + dogru);

        // --- 06: GUVENSIZ biriktirme ---
        List<Integer> guvensiz = new ArrayList<>();
        try {
            IntStream.range(0, 10_000).parallel().forEach(guvensiz::add);
            System.out.println("06 guvensiz biriktirme -> beklenen 10000, gelen " + guvensiz.size()
                    + (guvensiz.size() == 10_000 ? " (bu sefer sansliydik)" : " <- VERI KAYBI"));
        } catch (Exception e) {
            System.out.println("06 guvensiz biriktirme -> " + e.getClass().getSimpleName());
        }
        List<Integer> guvenli = IntStream.range(0, 10_000).parallel().boxed().toList();
        System.out.println("   guvenli (toList) -> " + guvenli.size());

        // --- 07: forEach vs forEachOrdered ---
        System.out.print("07 paralel forEach        -> ");
        IntStream.range(0, 10).parallel().forEach(i -> System.out.print(i + " "));
        System.out.print("\n   paralel forEachOrdered -> ");
        IntStream.range(0, 10).parallel().forEachOrdered(i -> System.out.print(i + " "));
        System.out.println();

        // --- 08: isParallel / sequential ---
        Stream<Integer> s = Stream.of(1, 2).parallel();
        System.out.println("08 isParallel=" + s.isParallel()
                + " | sequential sonrasi=" + Stream.of(1, 2).parallel().sequential().isParallel());

        // --- 09: summaryStatistics ---
        System.out.println("09 stats -> " + IntStream.of(4, 8, 1).summaryStatistics());

        // --- 10: asDoubleStream / asLongStream ---
        System.out.println("10 asDoubleStream -> " + IntStream.of(1, 2).asDoubleStream().boxed().toList()
                + " asLongStream -> " + IntStream.of(1, 2).asLongStream().boxed().toList());
    }
}
