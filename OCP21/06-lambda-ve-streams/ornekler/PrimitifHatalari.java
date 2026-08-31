/*
 * 6.4 — DERLENMEYEN primitif stream ornekleri.
 *
 *     javac -d /tmp/ocp PrimitifHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;
import java.util.stream.*;

public class PrimitifHatalari {

    // HATA 1: IntStream'de toList() yoktur
    // DUZELTME: .boxed().toList() yaz.
    static void h1() {
        List<Integer> l = IntStream.range(1, 4).toList();
    }

    // HATA 2: Stream<Integer> uzerinde sum() yoktur
    // DUZELTME: .mapToInt(Integer::intValue).sum() yaz.
    static void h2() {
        int t = Stream.of(1, 2, 3).sum();
    }

    // HATA 3: average() OptionalDouble doner
    // DUZELTME: OptionalDouble d = ... yaz ya da .getAsDouble() ekle.
    static void h3() {
        double d = IntStream.of(1, 2).average();
    }

    // HATA 4: OptionalInt'te get() yoktur
    // DUZELTME: getAsInt() yaz.
    static void h4() {
        int m = IntStream.of(1, 2).max().get();
    }

    // HATA 5: OptionalInt ile Optional<Integer> iliskisiz tiplerdir
    // DUZELTME: OptionalInt o = ... yaz.
    static void h5() {
        Optional<Integer> o = IntStream.of(1).max();
    }

    // HATA 6: chars() IntStream doner, Stream<Character> degil
    // DUZELTME: .mapToObj(c -> (char) c) ekle.
    static void h6() {
        Stream<Character> s = "abc".chars();
    }

    // HATA 7: CharStream diye bir tip yoktur
    // DUZELTME: IntStream yaz.
    static void h7() {
        // CharStream cs = "abc".chars();
        var cs = CharStream.of('a');
    }

    public static void main(String[] args) { }
}
