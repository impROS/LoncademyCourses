/*
 * 5.2 — DERLENMEYEN koleksiyon / generics ornekleri.
 *
 *     javac -d /tmp/ocp GenerikHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;

public class GenerikHatalari {

    // HATA 1: generics KOVARYANT DEGILDIR (dizilerin aksine)
    // DUZELTME: List<String> l = new ArrayList<String>();
    static void h1() {
        List<Object> l = new ArrayList<String>();
    }

    // HATA 2: ? extends listesine eleman EKLENEMEZ (null haric)
    // DUZELTME: List<Integer> l = new ArrayList<>(); yaz.
    static void h2() {
        List<? extends Number> l = new ArrayList<Integer>();
        l.add(5);
    }

    // HATA 3: ? super listesinden yalnizca Object olarak okunur
    // DUZELTME: Object o = l.get(0);
    static void h3() {
        List<? super Integer> l = new ArrayList<Number>();
        Integer o = l.get(0);
    }

    // HATA 4: tip silme nedeniyle parametreli instanceof yasak
    // DUZELTME: l instanceof List<?> yaz.
    static boolean h4(Object l) {
        return l instanceof List<String>;
    }

    // HATA 5: generic tipten dizi olusturulamaz
    // DUZELTME: (T[]) new Object[10] cast'i kullan.
    static <T> void h5() {
        T[] dizi = new T[10];
    }

    // HATA 6: Map.of en fazla 10 cift alir
    // DUZELTME: Map.ofEntries(...) kullan.
    static void h6() {
        Map<String, Integer> m = Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5,
                                        "f", 6, "g", 7, "h", 8, "i", 9, "j", 10, "k", 11);
    }

    // HATA 7: sinirli tip parametresi ihlali
    // DUZELTME: Kutu<Integer> yaz.
    static class Kutu<T extends Number> { }
    static void h7() {
        Kutu<String> k = new Kutu<>();
    }

    public static void main(String[] args) { }
}
