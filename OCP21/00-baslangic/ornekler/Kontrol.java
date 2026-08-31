public class Kontrol {
    sealed interface Sekil permits Daire, Kare {}
    record Daire(double r) implements Sekil {}
    record Kare(double kenar) implements Sekil {}

    static String anlat(Sekil s) {
        return switch (s) {
            case Daire d when d.r() > 10 -> "büyük daire";
            case Daire d                 -> "daire, r=" + d.r();
            case Kare(double k)          -> "kare, kenar=" + k;
        };
    }

    public static void main(String[] args) {
        System.out.println("Java sürümü: " + Runtime.version().feature());
        System.out.println(anlat(new Daire(3)));
        System.out.println(anlat(new Daire(42)));
        System.out.println(anlat(new Kare(5)));
    }
}
