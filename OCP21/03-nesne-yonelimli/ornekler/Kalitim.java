/*
 * 3.4 — Override / hiding / overload ve cast davranislari.
 *
 *     java Kalitim.java
 *
 * 01, 02, 03 satirlari sinavin en klasik sorusudur: ONCE tahmin et.
 */
import java.util.*;

public class Kalitim {

    static class Ust {
        String ad = "ust";
        static String s() { return "ust-static"; }
        String i()        { return "ust-instance"; }
        Object f()        { return "ust-f"; }
    }

    static class Alt extends Ust {
        String ad = "alt";
        static String s() { return "alt-static"; }
        @Override String i() { return "alt-instance"; }
        @Override String f() { return "alt-f"; }      // covariant donus: Object -> String
        String sadeceAlt()   { return "sadece-alt"; }
    }

    // equals OVERLOAD edilmis (Object degil, Nokta parametresi) -> tuzak
    static class Nokta {
        int x;
        Nokta(int x) { this.x = x; }
        public boolean equals(Nokta n) { return n != null && n.x == x; }
        @Override public int hashCode() { return x; }
    }

    // dogru yazim
    static class Nokta2 {
        int x;
        Nokta2(int x) { this.x = x; }
        @Override public boolean equals(Object o) { return o instanceof Nokta2 n && n.x == x; }
        @Override public int hashCode() { return x; }
    }

    public static void main(String[] args) {

        Ust u = new Alt();

        System.out.println("01 alan   -> " + u.ad);        // referans tipi
        System.out.println("02 statik -> " + u.s());       // referans tipi
        System.out.println("03 ornek  -> " + u.i());       // NESNE tipi
        System.out.println("   covariant -> " + u.f());

        // --- referans tipi neyi cagirabilecegini belirler ---
        // u.sadeceAlt();                                  // DERLENMEZ
        System.out.println("04 cast sonrasi -> " + ((Alt) u).sadeceAlt());

        // --- yanlis cast: RUNTIME hatasi ---
        Object o = Integer.valueOf(5);
        try {
            String s = (String) o;
            System.out.println("05 cast -> " + s);
        } catch (ClassCastException e) {
            System.out.println("05 ClassCastException -> " + e.getMessage().split(" \\(")[0]);
        }

        // --- null davranislari ---
        System.out.println("06 null cast -> [" + (String) null + "]"
                + " | null instanceof String -> " + (null instanceof String));

        // --- equals OVERLOAD tuzagi ---
        Set<Nokta> set1 = new HashSet<>();
        set1.add(new Nokta(1));
        set1.add(new Nokta(1));
        System.out.println("07 equals overload -> set boyutu = " + set1.size() + " (beklenen 1)");

        Set<Nokta2> set2 = new HashSet<>();
        set2.add(new Nokta2(1));
        set2.add(new Nokta2(1));
        System.out.println("08 equals override -> set boyutu = " + set2.size());

        // --- HashSet Object parametreli equals'i cagirir ---
        System.out.println("09 dogrudan cagri -> " + new Nokta(1).equals(new Nokta(1))
                + " | Object referansiyla -> " + new Nokta(1).equals((Object) new Nokta(1)));
    }
}
