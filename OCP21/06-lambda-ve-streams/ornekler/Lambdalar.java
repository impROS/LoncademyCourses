/*
 * 6.1 — Fonksiyonel arayuzler, andThen/compose ve method reference.
 *
 *     java Lambdalar.java
 *
 * GOREV: Her lambda'nin hangi arayuze atandigini ONCE kagida yaz.
 */
import java.util.*;
import java.util.function.*;

public class Lambdalar {

    public static void main(String[] args) throws Exception {

        // --- 01: dort temel arayuz ---
        Supplier<String> verir = () -> "merhaba";
        Consumer<String> alir = s -> System.out.print("[" + s + "] ");
        Predicate<String> sorar = s -> s.length() > 3;
        Function<String, Integer> donusturur = s -> s.length();

        System.out.print("01 dort arayuz -> " + verir.get() + " ");
        alir.accept("konsol");
        System.out.println("test=" + sorar.test("java") + " apply=" + donusturur.apply("java"));

        // --- 02: UnaryOperator / BinaryOperator ---
        UnaryOperator<String> buyut = String::toUpperCase;
        BinaryOperator<Integer> topla = Integer::sum;
        System.out.println("02 operator -> " + buyut.apply("java") + " " + topla.apply(3, 4));

        // --- 03: primitif surumler ---
        IntFunction<String> intAlir = i -> "sayi:" + i;        // int ALIR
        ToIntFunction<String> intDoner = s -> s.length();      // int DONER
        IntUnaryOperator ikiKat = i -> i * 2;
        IntPredicate ciftMi = i -> i % 2 == 0;
        System.out.println("03 primitif -> " + intAlir.apply(7) + " " + intDoner.applyAsInt("java")
                + " " + ikiKat.applyAsInt(5) + " " + ciftMi.test(4));

        // --- 04: andThen vs compose ---
        Function<Integer, Integer> ekle = x -> x + 1;
        Function<Integer, Integer> carp = x -> x * 2;
        System.out.println("04 andThen vs compose -> andThen=" + ekle.andThen(carp).apply(5)
                + " compose=" + ekle.compose(carp).apply(5));

        // --- 05: Predicate zinciri ---
        Predicate<String> bosDegil = s -> !s.isEmpty();
        Predicate<String> uzun = s -> s.length() > 5;
        System.out.println("05 predicate -> and=" + bosDegil.and(uzun).test("java")
                + " or=" + bosDegil.or(uzun).test("java")
                + " negate=" + uzun.negate().test("java")
                + " not=" + Predicate.not(uzun).test("java"));

        // --- 06: method reference dort bicim ---
        Function<String, Integer> mrStatic = Integer::parseInt;          // static
        Consumer<String> mrNesne = System.out::println;                  // belirli nesne
        Function<String, Integer> mrRastgele = String::length;           // rastgele nesne
        Supplier<List<String>> mrCtor = ArrayList::new;                  // constructor

        String metin = "abcdef";
        Supplier<Integer> mrBelirliNesne = metin::length;                // BELIRLI nesne

        System.out.println("06 method reference -> static=" + mrStatic.apply("42")
                + " rastgele(String::length)=" + mrRastgele.apply("java")
                + " belirli(metin::length)=" + mrBelirliNesne.get()
                + " ctor=" + mrCtor.get());
        System.out.print("   System.out::println -> ");
        mrNesne.accept("yazildi");

        // --- 07: rastgele nesne + arguman ---
        BiFunction<String, String, Boolean> baslarMi = String::startsWith;
        System.out.println("07 iki argumanli mr -> " + baslarMi.apply("javascript", "java"));

        // --- 08: Function.identity ---
        System.out.println("08 identity -> " + Function.<String>identity().apply("aynen"));

        // --- 09: Consumer.andThen ---
        Consumer<String> c1 = s -> System.out.print("bir:" + s + " ");
        Consumer<String> c2 = s -> System.out.print("iki:" + s);
        System.out.print("09 consumer zinciri -> ");
        c1.andThen(c2).accept("x");
        System.out.println();

        // --- 10: Callable checked exception BILDIRIR ---
        java.util.concurrent.Callable<Integer> cal = () -> {
            if (false) throw new java.io.IOException();   // Callable.call() throws Exception
            return 1;
        };
        System.out.println("10 Callable -> " + cal.call());
        // Function<String,Integer> f = s -> { throw new java.io.IOException(); };  // DERLENMEZ
    }
}
