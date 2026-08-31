/*
 * 3.5 — DERLENMEYEN abstract / sealed ornekleri.
 *
 *     javac -d /tmp/ocp SealedHatalari.java
 *
 * GOREV: Hatalari teker teker duzelt, her seferinde tekrar derle.
 */
public class SealedHatalari {

    // HATA 1: abstract metodun govdesi olamaz
    // DUZELTME: govdeyi sil, noktali virgulle bitir.
    static abstract class A1 { abstract void m() { } }

    // HATA 2: abstract + final celiskidir
    // DUZELTME: final'i sil.
    static abstract final class A2 { }

    // HATA 3: abstract metot private olamaz
    // DUZELTME: private'i sil.
    static abstract class A3 { abstract private void m(); }

    // HATA 4: abstract metot static olamaz
    // DUZELTME: static'i sil.
    static abstract class A4 { abstract static void m(); }

    // HATA 5: somut alt sinif tum abstract metotlari uygulamali
    // DUZELTME: ses() metodunu ekle.
    static abstract class Hayvan { abstract String ses(); }
    static class Kedi extends Hayvan { }

    // HATA 6: sealed alt tipi final / sealed / non-sealed olmali
    // DUZELTME: 'final' ekle.
    sealed interface S1 permits B1 { }
    static class B1 implements S1 { }

    // HATA 7: permits listesinde olmayan tip uygulayamaz
    // DUZELTME: permits listesine B3'u ekle.
    sealed interface S2 permits B2 { }
    static final class B2 implements S2 { }
    static final class B3 implements S2 { }

    // HATA 8: sealed + final celiskidir
    // DUZELTME: final'i sil.
    sealed final class S3 { }

    public static void main(String[] args) { }
}
