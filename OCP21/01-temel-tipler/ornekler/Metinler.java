/*
 * 1.2 — String immutability, havuz (pool), StringBuilder ve text block kurallari.
 *
 *     java Metinler.java
 *
 * ONCE TAHMIN ET. Ozellikle TB satirlarindaki uzunluklara dikkat:
 * text block'un sonunda \n olup olmadigini sayi ele verir.
 */
public class Metinler {

    public static void main(String[] args) {

        // --- Immutability ---
        String s = "abc";
        s.concat("def");
        s.toUpperCase();
        System.out.println("01 immutability -> " + s);

        // --- Havuz (pool) ---
        String a = "java";
        String b = "java";
        String c = new String("java");
        String d = "ja" + "va";          // derleme zamani sabiti
        String e = "ja";
        String f = e + "va";             // calisma zamani birlestirme
        System.out.println("02 havuz: " + (a == b) + " " + (a == d) + " " + (a == c));
        System.out.println("03 runtime birlestirme: " + (a == f) + " " + (a == f.intern()));

        final String fe = "ja";
        String g = fe + "va";            // fe final -> derleme zamani sabiti
        System.out.println("04 final degisken: " + (a == g));

        // --- substring sinirlari ---
        String t = "abcdef";
        System.out.println("05 substring(2)    -> " + t.substring(2));
        System.out.println("06 substring(2,4)  -> " + t.substring(2, 4));
        System.out.println("07 substring(2,2)  -> [" + t.substring(2, 2) + "]");
        System.out.println("08 substring(6)    -> [" + t.substring(6) + "]");
        // System.out.println("09 substring(7)  -> " + t.substring(7));  // StringIndexOutOfBounds

        // --- Bulunamayan arama ---
        System.out.println("10 indexOf(\"yok\") -> " + t.indexOf("yok"));

        // --- strip / trim / isBlank ---
        String bosluklu = "\u2000java\u2000";   // U+2000: trim temizleyemez, strip temizler
        System.out.println("11 trim  uzunluk -> " + bosluklu.trim().length() + " (orijinal " + bosluklu.length() + ")");
        System.out.println("12 strip uzunluk -> " + bosluklu.strip().length());
        System.out.println("13 isBlank -> " + "   ".isBlank() + " isEmpty -> " + "   ".isEmpty());

        // --- StringBuilder mutable davranisi ---
        StringBuilder sb = new StringBuilder("abcdef");
        sb.delete(1, 3);
        sb.insert(1, "XY");
        sb.reverse();
        System.out.println("14 stringbuilder -> " + sb);

        StringBuilder tasan = new StringBuilder("abc");
        tasan.delete(1, 99);                     // exception YOK
        System.out.println("15 delete(1,99) -> [" + tasan + "]");

        StringBuilder sb1 = new StringBuilder("ab");
        StringBuilder sb2 = new StringBuilder("ab");
        System.out.println("16 sb.equals(sb2) -> " + sb1.equals(sb2));
        System.out.println("17 toString().equals -> " + sb1.toString().equals(sb2.toString()));

        // --- Zincirleme ---
        StringBuilder z = new StringBuilder("abc");
        z.append("de").reverse().delete(0, 2);
        System.out.println("18 zincir -> " + z);

        // --- Text block'lar ---
        String tb1 = """
                bir
                iki""";                 // kapanis son satirda -> sonda \n YOK
        String tb2 = """
                bir
                iki
                """;                    // kapanis ayri satirda -> sonda \n VAR
        String tb3 = """
                uzun bir \
                satir""";               // \ satirlari birlestirir
        String tb4 = """
                deger:\s
                """;                    // \s sondaki boslugu korur

        System.out.println("19 TB1 uzunluk=" + tb1.length() + " -> " + tb1.replace("\n", "\\n"));
        System.out.println("20 TB2 uzunluk=" + tb2.length() + " -> " + tb2.replace("\n", "\\n"));
        System.out.println("21 TB3 uzunluk=" + tb3.length() + " -> " + tb3);
        System.out.println("22 TB4 uzunluk=" + tb4.length() + " -> [" + tb4.replace("\n", "\\n") + "]");

        // --- Bicimlendirme ---
        System.out.println("23 formatted -> " + "%s-%d".formatted("x", 5));
        System.out.println("24 chars() ilk deger -> " + "abc".chars().findFirst().getAsInt() + " (int, char degil)");
        System.out.println("25 valueOf(null obj) -> " + String.valueOf((Object) null));
    }
}
