/*
 * 10.1 — ResourceBundle arama sirasi, Locale ve bicimlendirme.
 *
 *     javac -d . Yerellestirme.java && java -cp . Yerellestirme
 *
 * DENEY: Mesaj_tr.properties icindeki 'selam' satirini sil ve tekrar calistir.
 */
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Yerellestirme {

    public static void main(String[] args) throws Exception {

        Locale.setDefault(Locale.US);                 // varsayilani sabitle (tekrarlanabilirlik)
        Locale tr = Locale.of("tr", "TR");
        Locale us = Locale.US;

        // --- 01: ARAMA SIRASI ---
        ResourceBundle b = ResourceBundle.getBundle("Mesaj", tr);
        System.out.println("01 arama sirasi (Locale=tr_TR):");
        System.out.println("   selam             = " + b.getString("selam") + "   <- Mesaj_tr.properties");
        System.out.println("   veda              = " + b.getString("veda") + " <- Mesaj_tr_TR.properties");
        System.out.println("   sadece.varsayilan = " + b.getString("sadece.varsayilan")
                + " <- Mesaj.properties");
        try {
            b.getString("olmayan.anahtar");
        } catch (MissingResourceException e) {
            System.out.println("   olmayan.anahtar   -> MissingResourceException (UNCHECKED)");
        }

        // --- 02: Locale bicimleri ---
        System.out.println("02 Locale:");
        System.out.println("   getLanguage=" + tr.getLanguage() + " getCountry=" + tr.getCountry());
        System.out.println("   toString=" + tr + "   toLanguageTag=" + tr.toLanguageTag());
        System.out.println("   forLanguageTag(\"tr-TR\") = " + Locale.forLanguageTag("tr-TR"));

        // --- 03: SAYI BICIMLERI ---
        double sayi = 1234567.891;
        System.out.println("03 sayi bicimleri (" + sayi + "):");
        System.out.println("   tr = " + NumberFormat.getInstance(tr).format(sayi)
                + "   <- nokta BINLIK, virgul ONDALIK");
        System.out.println("   us = " + NumberFormat.getInstance(us).format(sayi)
                + "   <- tam TERSI");
        System.out.println("   para   tr=" + NumberFormat.getCurrencyInstance(tr).format(1234.5)
                + "  us=" + NumberFormat.getCurrencyInstance(us).format(1234.5));
        System.out.println("   yuzde  tr=" + NumberFormat.getPercentInstance(tr).format(0.25)
                + "  us=" + NumberFormat.getPercentInstance(us).format(0.25));
        System.out.println("   compact us=" + NumberFormat.getCompactNumberInstance(us, NumberFormat.Style.SHORT).format(1234567));

        // --- 04: parse Number doner ve ParseException atar ---
        Number n = NumberFormat.getInstance(tr).parse("1.234,5");
        System.out.println("04 parse(\"1.234,5\", tr) = " + n + "  (tip: " + n.getClass().getSimpleName() + ")");
        try {
            NumberFormat.getInstance(us).parse("abc");
        } catch (ParseException e) {
            System.out.println("   parse(\"abc\") -> ParseException (CHECKED)");
        }

        // --- 05: tarih yerellestirme ---
        LocalDate d = LocalDate.of(2026, 8, 23);
        System.out.println("05 tarih:");
        for (FormatStyle st : FormatStyle.values()) {
            System.out.println("   " + st + " us=" + d.format(DateTimeFormatter.ofLocalizedDate(st).withLocale(us))
                    + " | tr=" + d.format(DateTimeFormatter.ofLocalizedDate(st).withLocale(tr)));
        }
        System.out.println("   ofPattern tr = " + d.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", tr)));

        // --- 06: LocalDate + saatli formatter -> exception ---
        try {
            d.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(us));
        } catch (java.time.temporal.UnsupportedTemporalTypeException e) {
            System.out.println("06 LocalDate + saatli formatter -> UnsupportedTemporalTypeException");
        }

        // --- 07: String.format locale ile ---
        System.out.println("07 String.format -> us=" + String.format(us, "%,.2f", 1234.5)
                + "  tr=" + String.format(tr, "%,.2f", 1234.5));
    }
}
