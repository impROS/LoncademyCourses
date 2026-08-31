/*
 * 5.3 — Comparator zinciri ve Java 21 Sequenced Collections.
 *
 *     java Siralama.java
 */
import java.util.*;

public class Siralama {

    record Kisi(String ad, int yas) {}

    public static void main(String[] args) {

        List<Kisi> kisiler = new ArrayList<>(List.of(
                new Kisi("Ali", 30), new Kisi("Veli", 25),
                new Kisi("Ayse", 30), new Kisi("Zeynep", 25)));

        // --- 01: dogal olmayan tipte comparator ---
        List<Kisi> a = new ArrayList<>(kisiler);
        a.sort(Comparator.comparingInt(Kisi::yas).thenComparing(Kisi::ad));
        System.out.println("01 yas, sonra ad          -> " + adlar(a));

        // --- 02 / 03: reversed() KONUMU sonucu degistirir ---
        List<Kisi> b = new ArrayList<>(kisiler);
        b.sort(Comparator.comparingInt(Kisi::yas).thenComparing(Kisi::ad).reversed());
        System.out.println("02 (yas,ad).reversed()    -> " + adlar(b));

        List<Kisi> c = new ArrayList<>(kisiler);
        c.sort(Comparator.comparingInt(Kisi::yas).reversed().thenComparing(Kisi::ad));
        System.out.println("03 yas.reversed(), ad     -> " + adlar(c));

        // --- 04: nullsFirst / nullsLast ---
        List<String> nullu = new ArrayList<>(Arrays.asList("b", null, "a"));
        nullu.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("04 nullsFirst             -> " + nullu);

        // --- 05: Sequenced Collections temel metotlari ---
        SequencedCollection<Integer> sc = new ArrayList<>(List.of(1, 2, 3));
        sc.addFirst(0);
        sc.addLast(4);
        System.out.println("05 SequencedCollection    -> " + sc
                + " getFirst=" + sc.getFirst() + " getLast=" + sc.getLast()
                + " reversed=" + sc.reversed());

        // --- 06: reversed() bir GORUNUMDUR ---
        List<Integer> kaynak = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> ters = kaynak.reversed();
        System.out.print("06 reversed gorunum       -> once " + ters);
        kaynak.add(4);
        System.out.println("  |  kaynaga 4 eklendi -> " + ters);

        // --- 07: TreeSet Sequenced'tir ama konumlu ekleme YAPAMAZ ---
        SequencedSet<String> ts = new TreeSet<>(List.of("b", "a", "c"));
        System.out.println("07 TreeSet                -> " + ts + " reversed=" + ts.reversed()
                + " getFirst=" + ts.getFirst());
        try {
            ts.addFirst("z");
        } catch (UnsupportedOperationException e) {
            System.out.println("   TreeSet.addFirst       -> UnsupportedOperationException");
        }

        // --- 08: LinkedHashSet konumlu ekleyebilir ---
        SequencedSet<String> lhs = new LinkedHashSet<>(List.of("b", "c"));
        lhs.addFirst("a");
        System.out.println("08 LinkedHashSet          -> " + lhs);

        // --- 09: SequencedMap ---
        SequencedMap<String, Integer> m = new LinkedHashMap<>();
        m.put("b", 2); m.put("c", 3);
        m.putFirst("a", 1);
        System.out.println("09 SequencedMap           -> " + m
                + " firstEntry=" + m.firstEntry() + " lastEntry=" + m.lastEntry()
                + " reversed=" + m.reversed());
        System.out.println("   pollFirstEntry         -> " + m.pollFirstEntry() + " kalan=" + m);

        // --- 10: bos koleksiyon davranislari ---
        try {
            new ArrayList<String>().getFirst();
        } catch (NoSuchElementException e) {
            System.out.println("10 bos getFirst()         -> NoSuchElementException");
        }
        System.out.println("   bos firstEntry()       -> " + new LinkedHashMap<String, Integer>().firstEntry());

        // --- 11: HashSet / HashMap Sequenced DEGIL ---
        System.out.println("11 HashSet instanceof SequencedCollection -> "
                + (new HashSet<String>() instanceof SequencedCollection));
        System.out.println("   HashMap instanceof SequencedMap        -> "
                + (new HashMap<String, Integer>() instanceof SequencedMap));
        // new HashSet<String>().getFirst();   // DERLENMEZ
    }

    static List<String> adlar(List<Kisi> l) {
        return l.stream().map(k -> k.ad() + "/" + k.yas()).toList();
    }
}
