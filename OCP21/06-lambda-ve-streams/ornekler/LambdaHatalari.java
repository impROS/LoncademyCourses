/*
 * 6.1 — DERLENMEYEN lambda ornekleri.
 *
 *     javac -d /tmp/ocp LambdaHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
import java.util.function.*;

public class LambdaHatalari {

    // HATA 1: lambda parametresi cevredeki yerel degiskeni GOLGELEYEMEZ
    // DUZELTME: lambda parametresini y yap.
    static void h1() {
        int x = 5;
        Predicate<Integer> p = x -> x > 3;
    }

    // HATA 2: yakalanan degisken effectively final olmali
    // DUZELTME: 'sayac++;' satirini sil.
    static void h2() {
        int sayac = 0;
        Runnable r = () -> System.out.println(sayac);
        sayac++;
    }

    // HATA 3: tip yazildiysa TUM parametrelerde olmali
    // DUZELTME: (int x, int y) yaz.
    static void h3() {
        BinaryOperator<Integer> f = (int x, y) -> x + y;
    }

    // HATA 4: var ile acik tip karistirilamaz
    // DUZELTME: (var x, var y) yaz.
    static void h4() {
        BinaryOperator<Integer> f = (var x, Integer y) -> x + y;
    }

    // HATA 5: blok govdede return ZORUNLU
    // DUZELTME: 'return x * 2;' yaz.
    static void h5() {
        UnaryOperator<Integer> f = x -> { x * 2; };
    }

    // HATA 6: lambda, SAM'in bildirmedigi checked exception atamaz
    // DUZELTME: RuntimeException at ya da hedefi Callable yap.
    static void h6() {
        Function<String, Integer> f = s -> { throw new java.io.IOException(); };
    }

    // HATA 7: Supplier parametre ALMAZ
    // DUZELTME: () -> "x" yaz.
    static void h7() {
        Supplier<String> s = x -> "x";
    }

    // HATA 8: void donen bir SAM'de blok govde DEGER DONDUREMEZ
    // DIKKAT: 'x -> x.length()' IFADE govdesi olarak DERLENIR (sonuc atilir);
    //         hata veren, acikca 'return' yazan blok govdedir.
    // DUZELTME: 'return' kelimesini sil.
    static void h8() {
        Consumer<String> c = x -> { return x.length(); };
    }

    public static void main(String[] args) { }
}
