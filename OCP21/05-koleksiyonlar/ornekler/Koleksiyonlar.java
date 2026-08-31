/*
 * 5.2 — Koleksiyon tuzaklari: remove overload, null politikalari, Queue metotlari, wildcard.
 *
 *     java Koleksiyonlar.java
 */
import java.util.*;

public class Koleksiyonlar {

    public static void main(String[] args) {

        // --- 01: fabrika metotlari immutable ---
        List<String> sabit = List.of("a", "b");
        try { sabit.add("c"); }
        catch (UnsupportedOperationException e) { System.out.println("01 List.of.add -> UnsupportedOperationException"); }
        try { List.of("a", null); }
        catch (NullPointerException e) { System.out.println("   List.of(null) -> NullPointerException"); }
        try { Set.of("a", "a"); }
        catch (IllegalArgumentException e) { System.out.println("   Set.of tekrarli -> IllegalArgumentException: " + e.getMessage()); }
        System.out.println("   new HashSet<>(tekrarli) -> " + new HashSet<>(List.of("a", "b")).size()
                + " (sessizce tekilllestirir)");

        // --- 02: List.remove OVERLOAD TUZAGI ---
        List<Integer> l1 = new ArrayList<>(List.of(10, 20, 30));
        l1.remove(1);                                   // INDEKS
        List<Integer> l2 = new ArrayList<>(List.of(10, 20, 30));
        l2.remove(Integer.valueOf(1));                  // NESNE: deger 1 aranir, YOK
        System.out.println("02 remove tuzagi -> remove(1)=" + l1
                + "   remove(Integer.valueOf(1))=" + l2 + " (degismedi!)");

        // --- 03: Map metotlari ---
        Map<String, Integer> m = new HashMap<>();
        System.out.println("03 put ilk -> " + m.put("a", 1) + " (eski deger yok -> null)");
        System.out.println("   put ikinci -> " + m.put("a", 2) + " (ESKI degeri doner)");
        System.out.println("   getOrDefault -> " + m.getOrDefault("yok", -1));
        m.putIfAbsent("a", 99);
        m.putIfAbsent("b", 5);
        System.out.println("   putIfAbsent -> " + m);
        m.merge("a", 10, Integer::sum);
        m.merge("c", 7, Integer::sum);
        System.out.println("   merge -> " + m);
        m.computeIfAbsent("d", k -> 100);
        m.computeIfPresent("b", (k, v) -> null);        // null donunce SILER
        System.out.println("   compute -> " + m);

        // --- 04: siralama garantileri ---
        System.out.println("04 HashSet -> " + new HashSet<>(List.of("c", "a", "b")));
        System.out.println("   LinkedHashSet -> " + new LinkedHashSet<>(List.of("c", "a", "b")));
        System.out.println("   TreeSet -> " + new TreeSet<>(List.of("c", "a", "b")));

        // --- 05: null politikasi ---
        Set<String> hs = new HashSet<>(); hs.add(null);
        System.out.println("05 null politikasi -> HashSet null KABUL: " + hs);
        for (var giris : Map.of("TreeSet", (Runnable) () -> new TreeSet<String>().add(null),
                                "ArrayDeque", () -> new ArrayDeque<String>().add(null)).entrySet()) {
            try { giris.getValue().run(); System.out.println("   " + giris.getKey() + " -> kabul etti?!"); }
            catch (NullPointerException e) { System.out.println("   " + giris.getKey() + " -> NullPointerException"); }
        }

        // --- 06: Queue metotlari — exception mi null mu ---
        Queue<String> q = new ArrayDeque<>();
        System.out.println("06 bos kuyrukta poll -> " + q.poll() + " | peek -> " + q.peek());
        try { q.remove(); }
        catch (NoSuchElementException e) { System.out.println("   remove() -> NoSuchElementException"); }
        try { q.element(); }
        catch (NoSuchElementException e) { System.out.println("   element() -> NoSuchElementException"); }

        // --- 07: Deque yigin (stack) olarak ---
        Deque<Integer> yigin = new ArrayDeque<>();
        yigin.push(1); yigin.push(2); yigin.push(3);
        System.out.println("07 stack -> peek=" + yigin.peek() + " pop=" + yigin.pop() + " kalan=" + yigin);

        // --- 08: PriorityQueue sadece BAS elemani garanti eder ---
        Queue<Integer> pq = new PriorityQueue<>();
        for (int v : new int[]{5, 1, 4, 2, 3}) pq.offer(v);
        System.out.println("08 PriorityQueue toString (SIRALI DEGIL) -> " + pq);
        StringBuilder sirali = new StringBuilder();
        while (!pq.isEmpty()) sirali.append(pq.poll()).append(" ");
        System.out.println("   poll ile cikarinca -> " + sirali.toString().trim());

        // --- 09: wildcard ---
        List<? extends Number> okunur = List.of(1, 2, 3);
        Number n = okunur.get(0);                        // OK
        // okunur.add(4);                                // DERLENMEZ
        List<? super Integer> yazilir = new ArrayList<Number>();
        yazilir.add(4);                                  // OK
        Object o = yazilir.get(0);                       // yalnizca Object
        System.out.println("09 wildcard -> okunur ilk=" + n + " yazilir=" + yazilir + " okundu=" + o);

        // --- 10: tip silme ---
        System.out.println("10 tip silme -> " + (new ArrayList<String>().getClass()
                == new ArrayList<Integer>().getClass()));

        // --- 11: entrySet bir GORUNUMDUR ---
        Map<String, Integer> gorunum = new HashMap<>(Map.of("a", 1, "b", 2));
        gorunum.keySet().remove("a");
        System.out.println("11 keySet gorunumu -> " + gorunum);
    }
}
