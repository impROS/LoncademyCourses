/*
 * 2.1 — DERLENMEYEN switch ornekleri.
 *
 *     javac -d /tmp/ocp SwitchHatalari.java
 *
 * GOREV: Hatalari TEKER TEKER duzelt, her duzeltmeden sonra tekrar derle.
 *        Amac hata mesajlarini degil, bu DURUMLARI tanimak.
 */
public class SwitchHatalari {

    // HATA 1: selector tipi long olamaz
    // DUZELTME: parametreyi int yap.
    static void h1(long x) {
        switch (x) {
            case 1: break;
            default: break;
        }
    }

    // HATA 2: case etiketi derleme zamani sabiti olmali
    // DUZELTME: 'esik' degiskenini final yap.
    static void h2(int x) {
        int esik = 5;
        switch (x) {
            case esik: break;
            default: break;
        }
    }

    // HATA 3: switch IFADESI tum girdileri kapsamali (default yok)
    // DUZELTME: default dali ekle.
    static String h3(Object o) {
        return switch (o) {
            case Integer i -> "sayi";
            case String s -> "metin";
        };
    }

    // HATA 4: blok kullanildi ama yield yok
    // DUZELTME: son satiri 'yield 0;' yap.
    static int h4(int x) {
        return switch (x) {
            case 1 -> 10;
            default -> {
                System.out.println("varsayilan");
            }
        };
    }

    // HATA 5: dominance — genel desen ozel deseni golgeliyor
    // DUZELTME: CharSequence dalini String dalinin ALTINA tasi.
    static String h5(Object o) {
        return switch (o) {
            case CharSequence c -> "cs";
            case String s -> "str";
            default -> "?";
        };
    }

    // HATA 6: iki sozdizimi ayni switch icinde karistirilamaz
    // DUZELTME: hepsini -> biciminde yaz.
    static void h6(int x) {
        switch (x) {
            case 1 -> System.out.println("bir");
            case 2: System.out.println("iki"); break;
        }
    }

    // HATA 7: instanceof pattern degiskeni || ile garanti degil
    // DUZELTME: || yerine && kullan.
    static boolean h7(Object o) {
        return o instanceof String s || s.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println("bu dosya derlenmez");
    }
}
