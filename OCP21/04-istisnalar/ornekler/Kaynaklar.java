/*
 * 4.2 — try-with-resources: kapanma sirasi, kapanma zamani ve bastirilan exception'lar.
 *
 *     java Kaynaklar.java
 *
 * ONCE cikti sirasini tahmin et.
 */
public class Kaynaklar {

    static class Kaynak implements AutoCloseable {
        private final String ad;
        Kaynak(String ad) { this.ad = ad; System.out.println("   acildi " + ad); }
        @Override public void close() { System.out.println("   kapandi " + ad); }
    }

    static class PatlayanKaynak implements AutoCloseable {
        @Override public void close() { throw new IllegalStateException("kapatma hatasi"); }
    }

    public static void main(String[] args) {

        // --- 01: kapanma TERS sirayla, catch ve finally'den ONCE ---
        System.out.println("01 sira:");
        try (Kaynak a = new Kaynak("A"); Kaynak b = new Kaynak("B")) {
            System.out.println("   govde");
        } catch (Exception e) {
            System.out.println("   catch");
        } finally {
            System.out.println("   finally");
        }

        // --- 02: govde exception atarsa da once kapanir ---
        System.out.println("02 exception ile:");
        try (Kaynak a = new Kaynak("A")) {
            throw new RuntimeException("govde hatasi");
        } catch (RuntimeException e) {
            System.out.println("   catch: " + e.getMessage());
        } finally {
            System.out.println("   finally");
        }

        // --- 03: bastirilan exception ---
        System.out.println("03 bastirilan:");
        try (PatlayanKaynak k = new PatlayanKaynak()) {
            throw new RuntimeException("govde kazanir");
        } catch (Exception e) {
            System.out.println("   disari cikan : " + e.getMessage());
            for (Throwable t : e.getSuppressed()) {
                System.out.println("   bastirilan   : " + t.getMessage());
            }
        }

        // --- 04: yalnizca close() atarsa o yayilir ---
        System.out.println("04 sadece close atar:");
        try (PatlayanKaynak k = new PatlayanKaynak()) {
            System.out.println("   govde sorunsuz");
        } catch (Exception e) {
            System.out.println("   disari cikan : " + e.getMessage()
                    + " | bastirilan sayisi: " + e.getSuppressed().length);
        }

        // --- 05: KLASIK finally ile ayni senaryo -> govdenin exception'i KAYBOLUR ---
        System.out.println("05 klasik finally (govde hatasi kaybolur):");
        try {
            PatlayanKaynak k = new PatlayanKaynak();
            try {
                throw new RuntimeException("govde kazanmali ama kaybolacak");
            } finally {
                k.close();
            }
        } catch (Exception e) {
            System.out.println("   disari cikan : " + e.getMessage()
                    + " | bastirilan sayisi: " + e.getSuppressed().length);
        }

        // --- 06: Java 9+ effectively final degisken dogrudan kullanilabilir ---
        System.out.println("06 Java 9+ bicimi:");
        Kaynak mevcut = new Kaynak("C");
        try (mevcut) {
            System.out.println("   govde");
        }

        // --- 07: ozel exception ve super(mesaj) ---
        System.out.println("07 ozel exception:");
        System.out.println("   super(mesaj) VAR -> " + new MesajliException("merhaba").getMessage());
        System.out.println("   super(mesaj) YOK -> " + new MesajsizException("merhaba").getMessage());
    }

    static class MesajliException extends RuntimeException {
        MesajliException(String m) { super(m); }
    }

    static class MesajsizException extends RuntimeException {
        MesajsizException(String m) { }          // super(m) CAGRILMADI
    }
}
