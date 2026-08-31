/*
 * 9.2 — Path hesaplari ve Files islemleri.
 *
 *     java Yollar.java
 *
 * ONCE 02-04 bloklarindaki sonuclari KAGIDA YAZ. Bu konu tamamen kural tabanlidir.
 */
import java.nio.file.*;
import java.util.stream.*;

public class Yollar {

    public static void main(String[] args) throws Exception {

        // --- 01: bilesenler ---
        Path p = Path.of("/ev/kullanici/belge/rapor.txt");
        System.out.println("01 bilesenler:");
        System.out.println("   getFileName   = " + p.getFileName());
        System.out.println("   getParent     = " + p.getParent());
        System.out.println("   getRoot       = " + p.getRoot());
        System.out.println("   getNameCount  = " + p.getNameCount() + "  (kok SAYILMAZ)");
        System.out.println("   getName(0)    = " + p.getName(0));
        System.out.println("   subpath(1,3)  = " + p.subpath(1, 3) + "  (bitis HARIC)");
        System.out.println("   goreli yolun koku = " + Path.of("belge/rapor.txt").getRoot());

        // --- 02: resolve ---
        System.out.println("02 resolve:");
        System.out.println("   /a/b + c/d        = " + Path.of("/a/b").resolve("c/d"));
        System.out.println("   /a/b + /x/y       = " + Path.of("/a/b").resolve("/x/y")
                + "   <- MUTLAK arguman soldakini YOK SAYAR");
        System.out.println("   resolveSibling    = " + Path.of("/a/b/c.txt").resolveSibling("d.txt"));

        // --- 03: relativize ---
        System.out.println("03 relativize:");
        System.out.println("   /a/b -> /a/b/c/d  = " + Path.of("/a/b").relativize(Path.of("/a/b/c/d")));
        System.out.println("   /a/b/c/d -> /a/b  = " + Path.of("/a/b/c/d").relativize(Path.of("/a/b")));
        try {
            Path.of("/a").relativize(Path.of("b"));
        } catch (IllegalArgumentException e) {
            System.out.println("   mutlak + goreli    -> IllegalArgumentException");
        }

        // --- 04: normalize ve equals ---
        System.out.println("04 normalize:");
        System.out.println("   /a/b/../c/./d normalize = " + Path.of("/a/b/../c/./d").normalize());
        System.out.println("   equals normalize eder mi = "
                + Path.of("/a/b/../c").equals(Path.of("/a/c")) + "  (HAYIR)");
        System.out.println("   normalize sonrasi        = "
                + Path.of("/a/b/../c").normalize().equals(Path.of("/a/c")));

        // --- 05: Path diske BAKMAZ ---
        Path yok = Path.of("/kesinlikle/olmayan/yol.txt");
        System.out.println("05 Path diske bakmaz -> getFileName=" + yok.getFileName()
                + " | Files.exists=" + Files.exists(yok));

        // --- 06: Files islemleri ---
        Path dizin = Path.of("/tmp/ocp-nio/ic/derin");
        Files.createDirectories(dizin);                     // ARA dizinleri de olusturur
        Path dosya = dizin.resolve("a.txt");
        Files.writeString(dosya, "birinci\nikinci\n\nucuncu\n");
        System.out.println("06 Files:");
        System.out.println("   size            = " + Files.size(dosya) + " byte");
        System.out.println("   readString      = " + Files.readString(dosya).replace("\n", "\\n"));
        System.out.println("   readAllLines    = " + Files.readAllLines(dosya));

        try (Stream<String> satirlar = Files.lines(dosya)) {   // KAPATILMALI
            System.out.println("   lines (bos olmayan) = " + satirlar.filter(s -> !s.isBlank()).toList());
        }

        try {
            Files.delete(Path.of("/tmp/ocp-nio/yok.txt"));
        } catch (NoSuchFileException e) {
            System.out.println("   delete(yok)     -> NoSuchFileException");
        }
        System.out.println("   deleteIfExists  -> " + Files.deleteIfExists(Path.of("/tmp/ocp-nio/yok.txt")));

        Path kopya = dizin.resolve("b.txt");
        Files.copy(dosya, kopya);
        try {
            Files.copy(dosya, kopya);
        } catch (FileAlreadyExistsException e) {
            System.out.println("   copy(hedef var) -> FileAlreadyExistsException");
        }
        Files.copy(dosya, kopya, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("   REPLACE_EXISTING ile -> basarili");

        // --- 07: list vs walk ---
        try (Stream<Path> s = Files.list(Path.of("/tmp/ocp-nio"))) {
            System.out.println("07 list (bir seviye) = " + s.map(Path::getFileName).toList());
        }
        try (Stream<Path> s = Files.walk(Path.of("/tmp/ocp-nio"))) {
            System.out.println("   walk (tum agac)   = " + s.count() + " oge");
        }

        // --- temizlik ---
        try (Stream<Path> s = Files.walk(Path.of("/tmp/ocp-nio"))) {
            s.sorted((x, y) -> y.getNameCount() - x.getNameCount())
             .forEach(x -> { try { Files.deleteIfExists(x); } catch (Exception e) { } });
        }
        System.out.println("08 temizlendi -> " + !Files.exists(Path.of("/tmp/ocp-nio")));
    }
}
