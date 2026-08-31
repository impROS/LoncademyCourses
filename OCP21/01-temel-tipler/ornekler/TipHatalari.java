/*
 * 1.1 — DERLENMEYEN kod ornekleri.
 *
 *     javac -d /tmp/ocp TipHatalari.java
 *
 * Amac: derleyicinin hata mesajlarini tanimak. Sinavda "Compilation fails"
 * siki cok sik dogru cevaptir; hangi satirin neden hata verdigini gormeden
 * bu refleks olusmaz.
 *
 * GOREV: Hatalari TEKER TEKER duzelt, her duzeltmeden sonra tekrar derle.
 *        Yorumdaki "DUZELTME:" satiri nasil duzeltilecegini soyluyor.
 */
public class TipHatalari {

    public static void main(String[] args) {

        // HATA 1: possible lossy conversion from int to byte
        // Sebep: b + 300 ifadesi int'tir, byte'a cast'siz atanamaz.
        // DUZELTME: b += 300;   ya da   b = (byte)(b + 300);
        byte b = 10;
        b = b + 300;

        // HATA 2: integer number too large
        // Sebep: literal varsayilan olarak int'tir.
        // DUZELTME: sonuna L ekle.
        long buyuk = 3000000000;

        // HATA 3: incompatible types: possible lossy conversion from double to float
        // Sebep: 1.5 bir double literalidir.
        // DUZELTME: 1.5f
        float f = 1.5;

        // HATA 4: incompatible types: possible lossy conversion from long to int
        // Sebep: sabit ifade istisnasi long -> int icin gecerli DEGIL.
        // DUZELTME: int i = 10;
        int i = 10L;

        // HATA 5: possible lossy conversion from int to byte
        // Sebep: 'degisken' final degil, derleme zamani sabiti sayilmaz.
        // DUZELTME: degiskeni final yap.
        int degisken = 100;
        byte kucuk = degisken;

        // HATA 6: bad operand types / incompatible types
        // Sebep: byte + byte ifadesi int uretir.
        // DUZELTME: int toplam = x + y;
        byte x = 10, y = 20;
        byte toplam = x + y;

        // HATA 7: variable sayac might not have been initialized
        // Sebep: yerel degiskenler varsayilan deger almaz.
        // DUZELTME: int sayac = 0;
        int sayac;
        System.out.println(sayac);

        // HATA 8: incompatible types: int cannot be converted to boolean
        // Sebep: Java'da if sadece boolean kabul eder (C'nin aksine).
        // DUZELTME: if (sayac == 1) { }
        if (sayac = 1) {
        }

        System.out.println(b + " " + buyuk + " " + f + " " + i + " " + kucuk + " " + toplam);
    }
}
