/*
 * 6.3 — DERLENMEYEN terminal islem / Collectors ornekleri.
 *
 *     javac -d /tmp/ocp CollectorHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;
import java.util.stream.*;

public class CollectorHatalari {

    // HATA 1: identity'li reduce Optional DEGIL, T doner
    // DUZELTME: Integer o = ... yaz ya da identity'yi kaldir.
    static void h1() {
        Optional<Integer> o = Stream.of(1, 2).reduce(0, Integer::sum);
    }

    // HATA 2: identity'siz reduce Optional doner
    // DUZELTME: Optional<Integer> o = ... yaz.
    static void h2() {
        int t = Stream.of(1, 2).reduce(Integer::sum);
    }

    // HATA 3: count() long doner
    // DUZELTME: long c = ... yaz.
    static void h3() {
        int c = (int) 0;
        c = Stream.of(1, 2).count();
    }

    // HATA 4: max() Optional doner
    // DUZELTME: Optional<Integer> m = ... yaz.
    static void h4() {
        Integer m = Stream.of(1, 2).max(Comparator.naturalOrder());
    }

    // HATA 5: counting() Long doner, Integer'a atanamaz
    // DUZELTME: Long c = ... yaz.
    static void h5() {
        Integer c = Stream.of("a").collect(Collectors.counting());
    }

    // HATA 6: partitioningBy anahtari Boolean'dir
    // DUZELTME: Map<Boolean, List<String>> yaz.
    static void h6() {
        Map<String, List<String>> m = Stream.of("a")
                .collect(Collectors.partitioningBy(s -> s.isEmpty()));
    }

    // HATA 7: groupingBy varsayilan olarak List uretir, Set degil
    // DUZELTME: downstream olarak Collectors.toSet() ekle.
    static void h7() {
        Map<Integer, Set<String>> m = Stream.of("a")
                .collect(Collectors.groupingBy(String::length));
    }

    public static void main(String[] args) { }
}
