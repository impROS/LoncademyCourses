/*
 * 9.2 — DERLENMEYEN NIO.2 ornekleri.
 *
 *     javac -d /tmp/ocp YolHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class YolHatalari {

    // HATA 1: Files metotlari checked IOException atar
    // DUZELTME: throws IOException ekle.
    static void h1() {
        Files.readString(Path.of("a.txt"));
    }

    // HATA 2: Path bir arayuzdur, new edilemez
    // DUZELTME: Path.of("a.txt") yaz.
    static void h2() {
        Path p = new Path("a.txt");
    }

    // HATA 3: Files.lines Stream<String> doner, List<String> degil
    // DUZELTME: List<String> l = Files.readAllLines(...)  ya da  .toList() ekle.
    static void h3() throws Exception {
        List<String> l = Files.lines(Path.of("a.txt"));
    }

    // HATA 4: Files.list Stream<Path> doner, Stream<String> degil
    // DUZELTME: Stream<Path> yaz.
    static void h4() throws Exception {
        Stream<String> s = Files.list(Path.of("."));
    }

    // HATA 5: getNameCount int doner, Path degil
    // DUZELTME: int n = ... yaz.
    static void h5() {
        Path n = Path.of("/a/b").getNameCount();
    }

    // HATA 6: Files.size long doner
    // DUZELTME: long s = ... yaz.
    static void h6() throws Exception {
        int s = Files.size(Path.of("a.txt"));
    }

    public static void main(String[] args) { }
}
