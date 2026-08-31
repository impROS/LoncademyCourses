/*
 * 9.1 — I/O sarmalama zinciri, konsol ve serialization.
 *
 *     java IOOrnekleri.java
 *
 * Dosyalar /tmp/ocp-io altinda olusur; program sonunda kendisi temizler.
 */
import java.io.*;
import java.nio.file.*;

public class IOOrnekleri {

    static class Ust {
        int u = 1;
        Ust() { u = 99; System.out.println("   >> Ust ctor CALISTI"); }
    }

    static class Alt extends Ust implements Serializable {
        private static final long serialVersionUID = 1L;
        int a = 5;
        transient int gecici = 7;
        static int statik = 9;
        Alt() { System.out.println("   >> Alt ctor CALISTI"); }
    }

    public static void main(String[] args) throws Exception {

        Path dizin = Path.of("/tmp/ocp-io");
        Files.createDirectories(dizin);
        Path dosya = dizin.resolve("metin.txt");
        Path ser = dizin.resolve("nesne.ser");

        // --- 01: yazma (sarmalama zinciri) ---
        try (var w = new PrintWriter(new BufferedWriter(new FileWriter(dosya.toFile())))) {
            w.println("birinci satir");
            w.printf("%s=%d%n", "sayi", 42);
        }
        System.out.println("01 yazildi -> " + dosya);

        // --- 02: okuma, readLine sonda NULL ---
        try (var r = new BufferedReader(new FileReader(dosya.toFile()))) {
            String s;
            int n = 0;
            while ((s = r.readLine()) != null) System.out.println("02 satir " + (++n) + " -> " + s);
            System.out.println("   son readLine() -> " + r.readLine() + " (null)");
        }

        // --- 03: read() int doner, sonda -1 ---
        try (var r = new FileReader(dosya.toFile())) {
            int c = r.read();
            System.out.println("03 read() -> " + c + " (int!) karakter olarak '" + (char) c + "'");
        }

        // --- 04: SERIALIZATION ---
        Alt o = new Alt();
        o.a = 50; o.gecici = 70; Alt.statik = 90;
        try (var out = new ObjectOutputStream(new FileOutputStream(ser.toFile()))) {
            out.writeObject(o);
        }
        System.out.println("04 yazilan  -> a=" + o.a + " gecici=" + o.gecici
                + " statik=" + Alt.statik + " u=" + o.u);

        Alt.statik = 111;                       // dosyadan SONRA degistirildi
        System.out.println("   deserialize basliyor (hangi ctor calisacak?):");
        Alt r2;
        try (var in = new ObjectInputStream(new FileInputStream(ser.toFile()))) {
            r2 = (Alt) in.readObject();
        }
        System.out.println("   okunan    -> a=" + r2.a
                + " gecici=" + r2.gecici + " (transient -> varsayilan)"
                + " statik=" + Alt.statik + " (dosyadan DEGIL, mevcut siniftan)"
                + " u=" + r2.u + " (Ust ctor'dan)");

        // --- 05: System.console() null olabilir ---
        System.out.println("05 System.console() -> " + System.console()
                + " (IDE/yonlendirme durumunda null olur)");

        // --- 06: NotSerializableException ---
        try (var out = new ObjectOutputStream(new ByteArrayOutputStream())) {
            out.writeObject(new Object());
        } catch (NotSerializableException e) {
            System.out.println("06 Serializable olmayan nesne -> NotSerializableException: " + e.getMessage());
        }

        // --- temizlik ---
        Files.deleteIfExists(dosya);
        Files.deleteIfExists(ser);
        Files.deleteIfExists(dizin);
        System.out.println("07 temizlendi -> " + !Files.exists(dizin));
    }
}
