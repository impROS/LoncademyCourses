/*
 * 2.2 — DERLENMEYEN dongu ornekleri.
 *
 *     javac -d /tmp/ocp DonguHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
public class DonguHatalari {

    // HATA 1: unreachable statement — govde asla calisamaz
    // DUZELTME: false yerine bir degisken kullan ya da if(false) yap.
    static void h1() {
        while (false) {
            System.out.println("asla");
        }
    }

    // HATA 2: unreachable statement — while(true) sonrasi kod
    // DUZELTME: dongunun icine break ekle.
    static void h2() {
        while (true) {
            System.out.println("sonsuz");
        }
        System.out.println("buraya gelinmez");
    }

    // HATA 3: for basliginda tek tip bildirilebilir
    // DUZELTME: ikisini de int yap.
    static void h3() {
        for (int a = 0, long b = 0; a < 3; a++) {
        }
    }

    // HATA 4: dongu degiskeni dongu disinda gorunmez
    // DUZELTME: int k'yi dongunun disinda bildir.
    static void h4() {
        for (int k = 0; k < 3; k++) { }
        System.out.println(k);
    }

    // HATA 5: var olmayan etikete atlama
    // DUZELTME: dis dongunun ustune 'disari:' etiketini koy.
    static void h5() {
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                break disari;
            }
        }
    }

    public static void main(String[] args) { }
}
