/*
 * 1.1 — Tip donusumu, autoboxing ve operator tuzaklari.
 *
 * ONCE TAHMIN ET, SONRA CALISTIR:
 *     java Tuzaklar.java
 *
 * Her satirin yanina kagida tahminini yaz. Yanlis ciktigin her satir,
 * sinavda kaybedecegin bir puandir.
 */
public class Tuzaklar {

    public static void main(String[] args) {

        // --- Compound assignment gizli cast ---
        byte b = 10;
        b += 300;                                  // (byte)(10 + 300)
        System.out.println("01 -> " + b);

        int i = 5;
        i /= 2;
        i *= 1.5;                                  // (int)(2 * 1.5)
        System.out.println("02 -> " + i);

        // --- Sayisal terfi ---
        System.out.println("03 -> " + ('a' + 1));
        System.out.println("04 -> " + ("" + 'a' + 1));
        System.out.println("05 -> " + ('a' + 1 + ""));

        // --- Daraltma / tasma ---
        System.out.println("06 -> " + (byte) 300);
        System.out.println("07 -> " + (Integer.MAX_VALUE + 1));
        System.out.println("08 -> " + (char) 65);

        // --- Integer cache ---
        Integer c1 = 127, c2 = 127;
        Integer c3 = 128, c4 = 128;
        System.out.println("09 -> " + (c1 == c2));
        System.out.println("10 -> " + (c3 == c4));
        System.out.println("11 -> " + c3.equals(c4));

        Integer kutulu = 1000;
        int duz = 1000;
        System.out.println("12 -> " + (kutulu == duz));   // unboxing olur

        Double d1 = 1.0, d2 = 1.0;
        System.out.println("13 -> " + (d1 == d2));        // Double'da cache yok

        // --- Operator sirasi ---
        int a = 5;
        System.out.println("14 -> " + (a++ + ++a) + " (a=" + a + ")");

        int x = 3;
        x = x++ + --x;
        System.out.println("15 -> " + x);

        // --- String birlestirme sirasi ---
        System.out.println("16 -> " + (1 + 2 + "3" + 4 + 5));

        // --- Kayan nokta ve ozel degerler ---
        System.out.println("17 -> " + (10 / 0.0));
        System.out.println("18 -> " + (0.0 / 0.0));
        System.out.println("19 -> " + (Double.NaN == Double.NaN));
        System.out.println("20 -> " + (0.1 + 0.2 == 0.3));

        // --- Math API ---
        System.out.println("21 -> " + Math.round(2.5) + " / " + Math.round(-2.5));
        System.out.println("22 -> " + (-7 % 3) + " / " + Math.floorMod(-7, 3));
        System.out.println("23 -> " + (-7 / 2) + " / " + Math.floorDiv(-7, 2));
        System.out.println("24 -> " + Math.abs(Integer.MIN_VALUE));

        // --- Wrapper parse ---
        System.out.println("25 -> " + Boolean.parseBoolean("evet"));
        System.out.println("26 -> " + Boolean.parseBoolean("TRUE"));

        // --- Ternary tip birlestirme ---
        System.out.println("27 -> " + (true ? 1 : 2.0));

        // --- Unboxing NPE (yorumu kaldir ve calistir, ne oldugunu gor) ---
        // Integer bos = null;
        // int patlar = bos;
        // System.out.println("28 -> " + patlar);
    }
}
