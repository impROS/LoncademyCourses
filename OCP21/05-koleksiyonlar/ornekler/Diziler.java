/*
 * 5.1 — Dizi bildirimleri, Arrays API tuzaklari ve kovaryans.
 *
 *     java Diziler.java
 */
import java.util.*;

public class Diziler {

    public static void main(String[] args) {

        // --- 01: bildirim varyasyonlari ---
        int[] a1, a2;          // IKISI de dizi
        int e[], f;            // e dizi, f INT
        a1 = new int[]{1, 2};
        a2 = new int[]{3};
        e = new int[]{4, 5, 6};
        f = 7;                 // f = new int[3];  -> DERLENMEZ
        System.out.println("01 bildirim -> a1.length=" + a1.length + " e.length=" + e.length
                + " f=" + f + " (f bir dizi DEGIL)");

        // --- 02: varsayilan degerler ---
        int[] sifirlar = new int[3];
        String[] nulllar = new String[2];
        boolean[] falselar = new boolean[2];
        System.out.println("02 varsayilan -> " + Arrays.toString(sifirlar)
                + " " + Arrays.toString(nulllar) + " " + Arrays.toString(falselar));

        // --- 03: cok boyutlu ve duzensiz ---
        int[][] duzenli = new int[2][3];
        int[][] duzensiz = new int[2][];
        duzensiz[0] = new int[]{1, 2};
        duzensiz[1] = new int[]{3, 4, 5};
        System.out.println("03 cok boyutlu -> duzenli=" + Arrays.deepToString(duzenli)
                + " duzensiz=" + Arrays.deepToString(duzensiz));
        System.out.println("   toString HAM -> " + Arrays.toString(duzensiz));

        // --- 04: sort void doner, yerinde siralar ---
        int[] s = {3, 1, 2};
        Arrays.sort(s);                       // int[] r = Arrays.sort(s);  -> DERLENMEZ
        System.out.println("04 sort -> " + Arrays.toString(s));

        // --- 05: binarySearch ---
        int[] sirali = {10, 20, 30, 40};
        int[] sirasiz = {30, 10, 20, 40};
        System.out.println("05 binarySearch -> bulundu=" + Arrays.binarySearch(sirali, 30)
                + " bulunamadi=" + Arrays.binarySearch(sirali, 25)
                + " SIRASIZ 30 aranıyor (index 0 da VAR ama)=" + Arrays.binarySearch(sirasiz, 30));

        // --- 06: copyOf / copyOfRange / fill / compare / mismatch ---
        int[] k = {1, 2, 3};
        System.out.println("06 copyOf(5)=" + Arrays.toString(Arrays.copyOf(k, 5))
                + " copyOf(2)=" + Arrays.toString(Arrays.copyOf(k, 2))
                + " copyOfRange(1,3)=" + Arrays.toString(Arrays.copyOfRange(k, 1, 3)));
        int[] dolu = new int[3];
        Arrays.fill(dolu, 9);
        System.out.println("   fill=" + Arrays.toString(dolu)
                + " compare({1,2},{1,3})=" + Arrays.compare(new int[]{1, 2}, new int[]{1, 3})
                + " mismatch({1,2},{1,3})=" + Arrays.mismatch(new int[]{1, 2}, new int[]{1, 3})
                + " mismatch(esit)=" + Arrays.mismatch(new int[]{1, 2}, new int[]{1, 2}));

        // --- 07: Arrays.asList diziyle BAGLI ---
        String[] dizi = {"a", "b"};
        List<String> liste = Arrays.asList(dizi);
        liste.set(0, "z");
        System.out.println("07 asList -> liste=" + liste + " dizi=" + Arrays.toString(dizi)
                + " (dizi de degisti)");
        try {
            liste.add("c");
        } catch (UnsupportedOperationException ex) {
            System.out.println("   liste.add -> UnsupportedOperationException (sabit boyut)");
        }

        // --- 08: kovaryans ve ArrayStoreException ---
        Object[] o = new String[2];
        o[0] = "metin";
        try {
            o[1] = 42;
        } catch (ArrayStoreException ex) {
            System.out.println("08 ArrayStoreException -> " + ex.getMessage());
        }

        // --- 09: equals vs deepEquals ---
        int[][] x = {{1, 2}}, y = {{1, 2}};
        System.out.println("09 equals=" + Arrays.equals(x, y) + " deepEquals=" + Arrays.deepEquals(x, y));

        // --- 10: negatif boyut ---
        try {
            int boyut = -1;
            int[] hata = new int[boyut];
        } catch (NegativeArraySizeException ex) {
            System.out.println("10 NegativeArraySizeException -> " + ex.getMessage());
        }
    }
}
