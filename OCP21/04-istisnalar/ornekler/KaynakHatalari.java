/*
 * 4.2 — DERLENMEYEN try-with-resources ornekleri.
 *
 *     javac -d /tmp/ocp KaynakHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
public class KaynakHatalari {

    static class K implements AutoCloseable {
        K(String ad) { }
        @Override public void close() { }
    }

    static class Sayi { }          // AutoCloseable DEGIL

    // HATA 1: kaynak degiskeni ortuk final'dir
    // DUZELTME: atama satirini sil.
    static void h1() {
        try (K a = new K("A")) {
            a = new K("B");
        }
    }

    // HATA 2: kaynak degiskeni catch blogunda gorunmez
    // DUZELTME: println satirini sil.
    static void h2() {
        try (K a = new K("A")) {
        } catch (Exception e) {
            System.out.println(a);
        }
    }

    // HATA 3: kaynak AutoCloseable uygulamali
    // DUZELTME: Sayi'yi AutoCloseable yap ya da kaynagi K yap.
    static void h3() {
        try (Sayi s = new Sayi()) {
        }
    }

    // HATA 4: Java 9+ biciminde degisken effectively final olmali
    // DUZELTME: 'a = new K("B");' satirini sil.
    static void h4() {
        K a = new K("A");
        a = new K("B");
        try (a) {
        }
    }

    // HATA 5: klasik try tek basina olamaz (kaynak yoksa catch/finally sart)
    // DUZELTME: finally { } ekle.
    static void h5() {
        try {
            System.out.println("x");
        }
    }

    public static void main(String[] args) { }
}
