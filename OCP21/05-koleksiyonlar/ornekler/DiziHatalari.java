/*
 * 5.1 — DERLENMEYEN dizi ornekleri.
 *
 *     javac -d /tmp/ocp DiziHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.*;

public class DiziHatalari {

    // HATA 1: dizide length bir ALANDIR, metot degil
    // DUZELTME: a.length yaz.
    static int h1() {
        int[] a = {1, 2, 3};
        return a.length();
    }

    // HATA 2: {1,2,3} kisayolu yalnizca BILDIRIMDE gecerli
    // DUZELTME: d = new int[]{1, 2, 3};
    static void h2() {
        int[] d;
        d = {1, 2, 3};
    }

    // HATA 3: parantez degiskenin yanindaysa yalnizca ona uygulanir
    // (b bir int'tir, dizi degil)
    // DUZELTME: int[] a, b;  yaz.
    static void h3() {
        int a[], b;
        b = new int[3];
    }

    // HATA 4: Arrays.sort void doner
    // DUZELTME: Arrays.sort(a); ayri satirda cagir, sonra a'yi dondur.
    static int[] h4() {
        int[] a = {3, 1, 2};
        return Arrays.sort(a);
    }

    // HATA 5: en az ilk boyut verilmeli
    // DUZELTME: new int[2][]
    static void h5() {
        int[][] m = new int[][];
    }

    // HATA 6: dizi tipi ile eleman tipi uyusmali
    // DUZELTME: String[] s = {"a", "b"};
    static void h6() {
        String[] s = {1, 2};
    }

    // HATA 7: List<Object> = List<String> generics'te YASAK (diziden farkli)
    // DUZELTME: List<String> l = new ArrayList<String>();
    static void h7() {
        List<Object> l = new ArrayList<String>();
    }

    public static void main(String[] args) { }
}
