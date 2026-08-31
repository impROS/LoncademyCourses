/*
 * 2.2 — Dongu sayimi, etiketli break/continue, for-each davranisi.
 *
 *     java Donguler.java
 */
import java.util.*;

public class Donguler {

    public static void main(String[] args) {

        // --- 01: do-while govdeyi en az bir kez calistirir ---
        int i = 10;
        System.out.print("01 do-while -> ");
        do { System.out.print(i + " "); } while (i < 5);
        System.out.println();

        // --- 02: continue ile sayim ---
        int sayac = 0;
        for (int k = 0; k < 5; k++) {
            if (k % 2 == 0) continue;
            sayac++;
        }
        System.out.println("02 continue sayaci -> " + sayac);

        // --- 03: bos govde tuzagi ---
        int adet = 0;
        for (int k = 0; k < 3; k++);        // noktali virgul: govde BOS
        adet++;                              // dongu disinda, 1 kez
        System.out.println("03 bos govde -> adet=" + adet);

        // --- 04: etiketli break ve continue ---
        System.out.print("04 etiketli -> ");
        disari:
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                if (b == 1) continue disari;
                if (a == 2) break disari;
                System.out.print(a + "" + b + " ");
            }
        }
        System.out.println();

        // --- 05: for-each degiskeni bir KOPYADIR ---
        int[] dizi = {1, 2, 3};
        for (int x : dizi) { x = 99; }
        System.out.println("05 for-each kopya -> " + Arrays.toString(dizi));

        // --- 06: ConcurrentModificationException (tek thread!) ---
        List<String> liste = new ArrayList<>(List.of("a", "b", "c"));
        try {
            for (String s : liste) {
                if (s.equals("a")) liste.remove(s);
            }
            System.out.println("06 CME -> exception gelmedi: " + liste);
        } catch (ConcurrentModificationException e) {
            System.out.println("06 CME -> ConcurrentModificationException");
        }

        // --- 07: guvenli silme ---
        List<String> liste2 = new ArrayList<>(List.of("a", "b", "c"));
        liste2.removeIf(s -> s.equals("a"));
        System.out.println("07 removeIf -> " + liste2);

        List<String> liste3 = new ArrayList<>(List.of("a", "b", "c"));
        Iterator<String> it = liste3.iterator();
        while (it.hasNext()) { if (it.next().equals("b")) it.remove(); }
        System.out.println("08 Iterator.remove -> " + liste3);

        // --- 09: switch icinde break ve continue farki ---
        System.out.print("09 switch icinde -> ");
        for (int k = 0; k < 4; k++) {
            switch (k) {
                case 1: break;              // sadece switch'ten cikar
                case 2: continue;           // DONGUNUN sonraki turuna
                default: break;
            }
            System.out.print(k + " ");
        }
        System.out.println();

        // --- 10: while + continue = sonsuz dongu riski (guvenli versiyon) ---
        int j = 0, tur = 0;
        while (j < 5) {
            if (j == 2) { j++; continue; }   // artirmayi UNUTURSAN sonsuz dongu
            j++; tur++;
        }
        System.out.println("10 while continue -> tur=" + tur);
    }
}
