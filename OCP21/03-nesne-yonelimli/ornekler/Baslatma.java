/*
 * 3.1 — Baslatma sirasi: statik / ornek / constructor ve kalitim.
 *
 *     java Baslatma.java
 *
 * ONCE cikti sirasini KAGIDA YAZ. Bu alistirmanin tamami o tahminde.
 */
public class Baslatma {

    public static void main(String[] args) {
        System.out.println("--- tek sinif: ilk new ---");
        new Sira();
        System.out.println("\n--- tek sinif: ikinci new (statikler TEKRAR calismaz) ---");
        new Sira();

        System.out.println("\n\n--- kalitim sirasi ---");
        new Alt();

        System.out.println("\n\n--- ust constructor override edilmis metodu cagirirsa ---");
        new AltAlan();

        System.out.println("\n--- sahte constructor ---");
        new SahteCtor();          // hicbir sey yazmaz
        System.out.println("(yukarida hicbir cikti olmamali)");
    }
}

class Sira {
    static { System.out.print("1 "); }
    static int a = yaz("2 ");
    { System.out.print("3 "); }
    int b = yaz("4 ");

    Sira() { System.out.print("5 "); }

    static int yaz(String s) { System.out.print(s); return 0; }
}

class Ust {
    static { System.out.print("[ust-statik] "); }
    { System.out.print("[ust-ornek] "); }
    Ust() { System.out.print("[ust-ctor] "); }
}

class Alt extends Ust {
    static { System.out.print("[alt-statik] "); }
    { System.out.print("[alt-ornek] "); }
    Alt() { System.out.print("[alt-ctor] "); }
}

class UstCagiran {
    UstCagiran() { yaz(); }
    void yaz() { System.out.print("ust-yaz "); }
}

class AltAlan extends UstCagiran {
    int x = 5;
    @Override void yaz() { System.out.print("alt-yaz x=" + x + " "); }
    AltAlan() { System.out.print("| ctor sonrasi x=" + x); }
}

class SahteCtor {
    // DIKKAT: donus tipi var -> bu bir CONSTRUCTOR DEGIL, normal bir metot.
    public void SahteCtor() { System.out.print("bu asla calismaz"); }
}
