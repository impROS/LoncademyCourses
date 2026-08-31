/*
 * 6.3 — Terminal islemler, reduce bicimleri ve Collectors.
 *
 *     java Collectorlar.java
 */
import java.util.*;
import java.util.stream.*;

public class Collectorlar {

    public static void main(String[] args) {

        List<String> l = List.of("elma", "armut", "ayva", "kiraz", "incir");

        // --- 01: BOS stream'de eslestirme ---
        System.out.println("01 bos stream -> allMatch=" + Stream.of().allMatch(x -> false)
                + " noneMatch=" + Stream.of().noneMatch(x -> true)
                + " anyMatch=" + Stream.of().anyMatch(x -> true));

        // --- 02: reduce uc bicim ---
        int r1 = Stream.of(1, 2, 3).reduce(0, Integer::sum);
        Optional<Integer> r2 = Stream.of(1, 2, 3).reduce(Integer::sum);
        Optional<Integer> r3 = Stream.<Integer>of().reduce(Integer::sum);
        int r4 = Stream.of("a", "bb").reduce(0, (n, s) -> n + s.length(), Integer::sum);
        System.out.println("02 reduce -> id+acc=" + r1 + " acc=" + r2 + " bos=" + r3 + " uclu=" + r4);

        // --- 03: toList degistirilemez ---
        List<String> tl = Stream.of("a").toList();
        try { tl.add("b"); }
        catch (UnsupportedOperationException e) { System.out.println("03 stream.toList().add -> UnsupportedOperationException"); }
        List<String> cl = Stream.of("a").collect(Collectors.toList());
        cl.add("b");
        System.out.println("   collect(toList()).add -> calisti: " + cl);

        // --- 04: temel toplayicilar ---
        System.out.println("04 joining=" + l.stream().collect(Collectors.joining(", ", "[", "]")));
        System.out.println("   counting=" + l.stream().collect(Collectors.counting())
                + " (tip: " + l.stream().collect(Collectors.counting()).getClass().getSimpleName() + ")");
        System.out.println("   summing=" + l.stream().collect(Collectors.summingInt(String::length))
                + " averaging=" + l.stream().collect(Collectors.averagingInt(String::length))
                + " summarizing=" + l.stream().collect(Collectors.summarizingInt(String::length)));

        // --- 05: toMap CAKISMA ---
        try {
            l.stream().collect(Collectors.toMap(s -> s.charAt(0), s -> s));
        } catch (IllegalStateException e) {
            System.out.println("05 toMap cakisma -> IllegalStateException: " + e.getMessage());
        }
        Map<Character, String> cozum = l.stream()
                .collect(Collectors.toMap(s -> s.charAt(0), s -> s, (a, b) -> a + "|" + b));
        System.out.println("   merge fonksiyonuyla -> " + new TreeMap<>(cozum));

        // --- 06: groupingBy ---
        System.out.println("06 groupingBy -> " + new TreeMap<>(
                l.stream().collect(Collectors.groupingBy(String::length))));
        System.out.println("   downstream counting -> " + new TreeMap<>(
                l.stream().collect(Collectors.groupingBy(String::length, Collectors.counting()))));
        System.out.println("   map tipi + toSet -> " +
                l.stream().collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.toSet())));

        // --- 07: partitioningBy HER ZAMAN iki anahtar doner ---
        Map<Boolean, List<String>> p = l.stream().collect(Collectors.partitioningBy(s -> s.length() > 4));
        System.out.println("07 partitioningBy -> " + p);
        Map<Boolean, List<String>> bosTaraf = l.stream()
                .collect(Collectors.partitioningBy(s -> s.length() > 100));
        System.out.println("   hicbiri saglamiyor -> " + bosTaraf + " get(true)=" + bosTaraf.get(true));
        Map<Integer, List<String>> g = l.stream().collect(Collectors.groupingBy(String::length));
        System.out.println("   groupingBy olmayan anahtar -> " + g.get(99));

        // --- 08: mapping ve filtering ---
        System.out.println("08 mapping -> " + new TreeMap<>(l.stream().collect(
                Collectors.groupingBy(String::length, Collectors.mapping(s -> s.charAt(0), Collectors.toList())))));
        System.out.println("   filtering -> " + new TreeMap<>(l.stream().collect(
                Collectors.groupingBy(String::length, Collectors.filtering(s -> s.startsWith("a"), Collectors.toList())))));

        // --- 09: min/max/findFirst Optional doner ---
        System.out.println("09 max=" + l.stream().max(Comparator.comparingInt(String::length))
                + " findFirst=" + l.stream().findFirst()
                + " count=" + l.stream().count());
    }
}
