/*
 * 3.2 — Overload cozumleme sirasi, varargs ve pass-by-value.
 *
 *     java Overload.java
 *
 * GOREV: Her cagrida hangi overload'in secilecegini ONCE tahmin et.
 * Sonra f(long) metodunu yorum satiri yapip tekrar calistir; sira boyle ogrenilir.
 */
import java.util.*;

public class Overload {

    static void f(long x)    { System.out.println("long"); }
    static void f(Integer x) { System.out.println("Integer"); }
    static void f(int... x)  { System.out.println("varargs(" + x.length + ")"); }

    static void g(Object o)  { System.out.println("Object"); }

    static void v(String... s) { System.out.println("varargs uzunluk=" + s.length); }

    public static void main(String[] args) {

        System.out.print("01 f(5) -> ");        f(5);
        System.out.print("02 f(5L) -> ");       f(5L);
        System.out.print("03 f(Integer) -> ");  f(Integer.valueOf(5));
        System.out.print("04 f(1,2,3) -> ");    f(1, 2, 3);
        System.out.print("05 g(5) -> ");        g(5);          // int -> Integer -> Object

        // --- varargs davranislari ---
        System.out.print("06 v() -> ");                 v();
        System.out.print("07 v(\"a\",\"b\") -> ");      v("a", "b");
        System.out.print("08 v(new String[0]) -> ");    v(new String[0]);
        System.out.print("09 v((String) null) -> ");    v((String) null);
        try {
            System.out.print("10 v((String[]) null) -> ");
            v((String[]) null);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException");
        }

        // --- pass-by-value ---
        StringBuilder sb = new StringBuilder("a");
        String s = "a";
        int i = 1;
        List<String> liste = new ArrayList<>(List.of("x"));
        degistir(sb, s, i, liste);
        System.out.println("11 pass-by-value -> sb=" + sb + " s=" + s + " i=" + i + " liste=" + liste);

        // --- var kurallari (gecerli olanlar) ---
        var sayi = 5;
        var ad = "java";
        var harita = new HashMap<String, Integer>();
        harita.put(ad, sayi);
        for (var e : harita.entrySet()) {
            System.out.println("12 var -> " + e.getKey() + "=" + e.getValue());
        }
        int var = 7;          // 'var' ayrilmis kelime DEGIL
        System.out.println("13 int var = " + var);

        // --- effectively final ---
        int yakalanan = 5;
        Runnable r = () -> System.out.println("14 lambda yakaladi -> " + yakalanan);
        r.run();
        // yakalanan = 6;   <- bu satiri acarsan YUKARIDAKI lambda DERLENMEZ
    }

    static void degistir(StringBuilder sb, String s, int i, List<String> liste) {
        sb.append("!");                    // icerik degisti      -> GORULUR
        liste.add("y");                    // icerik degisti      -> GORULUR
        sb = new StringBuilder("yeni");    // referans kopyasi    -> GORULMEZ
        s += "!";                          // String immutable    -> GORULMEZ
        i++;                               // primitif kopyasi    -> GORULMEZ
    }
}
