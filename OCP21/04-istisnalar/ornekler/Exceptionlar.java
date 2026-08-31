/*
 * 4.1 — finally akisi, yakalama sirasi, multi-catch ve exception zinciri.
 *
 *     java Exceptionlar.java
 *
 * ONCE her metodun donus degerini tahmin et.
 */
import java.io.*;
import java.sql.SQLException;

public class Exceptionlar {

    // finally'deki return, try'daki return'u EZER
    static int finallyReturn() {
        try { return 1; }
        finally { return 2; }
    }

    // try'daki return degeri HEMEN hesaplanir; finally gec kalir
    static int degerDegisimi() {
        int x = 1;
        try { return x; }
        finally { x = 99; }
    }

    // finally'deki return exception'i YUTAR
    static int exceptionYutar() {
        try { throw new IllegalStateException("bu exception kaybolacak"); }
        finally { return 42; }
    }

    // finally exception'i yutmaz (return yok)
    static int exceptionGecer() {
        try { throw new IllegalStateException("bu exception disari cikar"); }
        finally { System.out.print("[finally calisti] "); }
    }

    public static void main(String[] args) {

        System.out.println("01 finally return -> " + finallyReturn());
        System.out.println("02 deger degisimi -> " + degerDegisimi());
        System.out.println("03 exception yutar -> " + exceptionYutar() + " (exception kayboldu)");

        System.out.print("04 exception gecer -> ");
        try {
            exceptionGecer();
        } catch (IllegalStateException e) {
            System.out.println("yakalandi: " + e.getMessage());
        }

        // --- multi-catch: degisken ORTUK final ---
        System.out.print("05 multi-catch -> ");
        try {
            atar(2);
        } catch (IOException | SQLException e) {
            // e = new IOException();   // DERLENMEZ: multi-catch degiskeni final
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // --- yakalama sirasi: ALT SINIF once ---
        System.out.print("06 yakalama sirasi -> ");
        try {
            ioAtar();
        } catch (FileNotFoundException e) {          // alt sinif ONCE
            System.out.println("FileNotFoundException yakalandi");
        } catch (IOException e) {                    // ust sinif SONRA
            System.out.println("IOException yakalandi");
        }

        // --- exception zinciri ---
        System.out.print("07 zincir -> ");
        try {
            try { throw new SQLException("db kapali"); }
            catch (SQLException e) { throw new RuntimeException("kayit okunamadi", e); }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage() + " <- sebep: " + e.getCause().getMessage());
        }

        // --- throw null -> NPE ---
        System.out.print("08 throw null -> ");
        try { throw null; }
        catch (NullPointerException e) { System.out.println("NullPointerException"); }

        // --- unchecked: try/catch ZORUNLU DEGIL ---
        System.out.print("09 NumberFormatException (unchecked) -> ");
        try { Integer.parseInt("abc"); }
        catch (NumberFormatException e) { System.out.println(e.getMessage()); }

        // --- 10: System.exit -> finally CALISMAZ (yorumu kaldirip dene) ---
        // try {
        //     System.out.println("10 System.exit oncesi");
        //     System.exit(0);
        // } finally {
        //     System.out.println("10 bu satir ASLA yazilmaz");
        // }
    }

    static void ioAtar() throws IOException {
        throw new FileNotFoundException("dosya yok");
    }

    static void atar(int n) throws IOException, SQLException {
        if (n == 1) throw new IOException("io hatasi");
        throw new SQLException("sql hatasi");
    }
}
