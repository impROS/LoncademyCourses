/*
 * 6.2 — DERLENMEYEN stream / Optional ornekleri.
 *
 *     javac -d /tmp/ocp StreamHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;
import java.util.stream.*;

public class StreamHatalari {

    // HATA 1: filter Predicate bekler, deger donduren lambda uymaz
    // DUZELTME: s -> s.length() > 2
    static void h1() {
        Stream.of("a", "bb").filter(s -> s.length());
    }

    // HATA 2: map Function bekler, void lambda uymaz
    // DUZELTME: map yerine peek ya da forEach kullan.
    static void h2() {
        Stream.of("a").map(s -> System.out.println(s)).toList();
    }

    // HATA 3: IntStream.boxed() olmadan Stream<Integer> elde edilemez
    // DUZELTME: .boxed() ekle.
    static void h3() {
        List<Integer> l = IntStream.range(1, 5).toList();
    }

    // HATA 4: Stream<Integer> uzerinde sum() yoktur
    // DUZELTME: mapToInt(Integer::intValue).sum() yaz.
    static void h4() {
        int t = Stream.of(1, 2, 3).sum();
    }

    // HATA 5: Optional<String> dogrudan String'e atanamaz
    // DUZELTME: .orElse("") ekle.
    static void h5() {
        String s = Optional.of("x");
    }

    // HATA 6: ifPresent Consumer alir, deger donduremez
    // DUZELTME: v -> System.out.println(v) yaz.
    static void h6() {
        Optional.of("x").ifPresent(v -> { return v.length(); });
    }

    // HATA 7: terminal islemden sonra ara islem zincirlenemez
    // DUZELTME: toList() sonucu List'tir, uzerinde .filter yoktur; stream()'e don.
    static void h7() {
        Stream.of("a", "b").toList().filter(s -> true);
    }

    public static void main(String[] args) { }
}
