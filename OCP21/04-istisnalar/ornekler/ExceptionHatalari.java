/*
 * 4.1 — DERLENMEYEN exception ornekleri.
 *
 *     javac -d /tmp/ocp ExceptionHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.io.*;
import java.sql.SQLException;

public class ExceptionHatalari {

    // HATA 1: checked exception yakalanmadi ve bildirilmedi
    // DUZELTME: imzaya 'throws IOException' ekle.
    static void h1() {
        throw new IOException();
    }

    // HATA 2: try tek basina olamaz
    // DUZELTME: finally { } ekle.
    static void h2() {
        try { System.out.println("x"); }
    }

    // HATA 3: ust sinif catch'i alt siniftan ONCE gelemez
    // DUZELTME: iki catch blogunun yerini degistir.
    static void h3() {
        try { new FileReader("a.txt"); }
        catch (Exception e) { }
        catch (IOException e) { }
    }

    // HATA 4: govdede atilamayacak checked exception yakalanamaz
    // DUZELTME: catch tipini Exception yap ya da blogu sil.
    static void h4() {
        try { System.out.println("x"); }
        catch (SQLException e) { }
    }

    // HATA 5: multi-catch tipleri akraba olamaz
    // DUZELTME: sadece IOException yaz.
    static void h5() {
        try { new FileReader("a.txt"); }
        catch (FileNotFoundException | IOException e) { }
    }

    // HATA 6: multi-catch degiskeni ortuk final'dir
    // DUZELTME: atama satirini sil.
    static void h6() {
        try { atar(); }
        catch (IOException | SQLException e) { e = new IOException(); }
    }

    // HATA 7: alt sinif yeni/daha genis checked exception bildiremez (3.4)
    // DUZELTME: throws Exception -> throws IOException
    static class A { void m() throws IOException { } }
    static class B extends A { void m() throws Exception { } }

    static void atar() throws IOException, SQLException { }

    public static void main(String[] args) { }
}
