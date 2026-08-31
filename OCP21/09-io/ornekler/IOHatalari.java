/*
 * 9.1 — DERLENMEYEN I/O ornekleri.
 *
 *     javac -d /tmp/ocp IOHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.io.*;

public class IOHatalari {

    // HATA 1: BufferedReader dosya adi kabul etmez, bir Reader ister
    // DUZELTME: new BufferedReader(new FileReader("a.txt"))
    static void h1() throws IOException {
        BufferedReader r = new BufferedReader("a.txt");
    }

    // HATA 2: read() int doner, char'a dogrudan atanamaz
    // DUZELTME: int c; while ((c = r.read()) != -1)
    static void h2(Reader r) throws IOException {
        char c;
        while ((c = r.read()) != -1) { }
    }

    // HATA 3: I/O metotlari checked IOException atar
    // DUZELTME: try/catch ekle ya da throws IOException bildir.
    static void h3() {
        new FileReader("a.txt");
    }

    // HATA 4: readObject checked ClassNotFoundException da atar
    // DUZELTME: throws listesine ClassNotFoundException ekle.
    static Object h4(ObjectInputStream in) throws IOException {
        return in.readObject();
    }

    // HATA 5: byte akisina karakter yazicisi zincirlenemez
    // DUZELTME: new OutputStreamWriter(new FileOutputStream("a.txt")) kullan.
    static void h5() throws IOException {
        Writer w = new BufferedWriter(new FileOutputStream("a.txt"));
    }

    // HATA 6: Serializable bir arayuzdur, new edilemez
    // DUZELTME: kendi sinifinla implements Serializable yap.
    static void h6() {
        Serializable s = new Serializable();
    }

    public static void main(String[] args) { }
}
