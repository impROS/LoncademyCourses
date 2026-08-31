/*
 * 5.3 — DERLENMEYEN siralama / Sequenced ornekleri.
 *
 *     javac -d /tmp/ocp SequencedHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;

public class SequencedHatalari {

    // HATA 1: HashSet SequencedCollection DEGILDIR
    // DUZELTME: LinkedHashSet kullan.
    static void h1() {
        Set<String> s = new HashSet<>();
        s.getFirst();
    }

    // HATA 2: HashMap SequencedMap DEGILDIR
    // DUZELTME: LinkedHashMap kullan.
    static void h2() {
        Map<String, Integer> m = new HashMap<>();
        m.firstEntry();
    }

    // HATA 3: primitif dizide Comparator kullanilamaz
    // DUZELTME: Integer[] kullan.
    static void h3() {
        int[] a = {3, 1, 2};
        Arrays.sort(a, Comparator.naturalOrder());
    }

    // HATA 4: list.sort void doner
    // DUZELTME: sort'u ayri satirda cagir, sonra l'yi dondur.
    static List<String> h4() {
        List<String> l = new ArrayList<>(List.of("b", "a"));
        return l.sort(Comparator.naturalOrder());
    }

    // HATA 5: Comparable olmayan tip Collections.sort ile siralanamaz
    // DUZELTME: comparator veren asiri yuklemeyi kullan.
    static void h5() {
        List<Object> l = new ArrayList<>();
        Collections.sort(l);
    }

    // HATA 6: compareTo TEK parametre alir (compare iki alir)
    // DUZELTME: public int compareTo(K o) yaz.
    static class K implements Comparable<K> {
        public int compareTo(K a, K b) { return 0; }
    }

    public static void main(String[] args) { }
}
