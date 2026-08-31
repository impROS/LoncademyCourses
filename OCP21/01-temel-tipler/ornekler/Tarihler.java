/*
 * 1.3 — java.time: immutability, Period/Duration ayrimi, ay tasmasi ve DST.
 *
 *     java Tarihler.java
 *
 * ONCE TAHMIN ET. Ozellikle 03, 08 ve 12 numarali satirlar sinav sorusudur.
 */
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Tarihler {

    public static void main(String[] args) {

        // --- Immutability ---
        LocalDate d = LocalDate.of(2026, 1, 1);
        d.plusDays(5);
        System.out.println("01 immutability -> " + d);
        System.out.println("02 atanmis      -> " + d.plusDays(5));

        // --- Ay tasmasi: gun son gune cekilir ve GERI DONMEZ ---
        LocalDate zincir = LocalDate.of(2026, 1, 31).plusMonths(1).plusMonths(1);
        System.out.println("03 zincir -> " + zincir);
        System.out.println("04 tek adim -> " + LocalDate.of(2026, 1, 31).plusMonths(2));

        // --- Ay 1 tabanli, Month enum ---
        LocalDate ay = LocalDate.of(2026, Month.AUGUST, 23);
        System.out.println("05 getMonth=" + ay.getMonth() + " getMonthValue=" + ay.getMonthValue());
        System.out.println("06 gun -> " + ay.getDayOfWeek() + " / yilin gunu " + ay.getDayOfYear());

        // --- Period ve Duration toString ---
        System.out.println("07 Period.of(1,2,3) -> " + Period.of(1, 2, 3));
        System.out.println("08 Period.ofYears(1).ofMonths(2).ofDays(3) -> "
                + Period.ofYears(1).ofMonths(2).ofDays(3));
        System.out.println("09 Duration.ofDays(1)=" + Duration.ofDays(1)
                + " ofHours(26)=" + Duration.ofHours(26)
                + " ofMinutes(90)=" + Duration.ofMinutes(90));

        // --- Aralik hesaplama ---
        LocalDate a = LocalDate.of(2026, 1, 1), b = LocalDate.of(2026, 3, 15);
        System.out.println("10 Period.between -> " + Period.between(a, b));
        System.out.println("11 ChronoUnit.DAYS.between -> " + ChronoUnit.DAYS.between(a, b));

        // --- DST: Period vs Duration ---
        ZoneId ny = ZoneId.of("America/New_York");
        ZonedDateTime once = ZonedDateTime.of(2026, 3, 7, 12, 0, 0, 0, ny);  // gecisten onceki gun
        System.out.println("12 DST baslangic -> " + once);
        System.out.println("   Period.ofDays(1)   -> " + once.plus(Period.ofDays(1)));
        System.out.println("   Duration.ofDays(1) -> " + once.plus(Duration.ofDays(1)));

        // --- Tipler arasi gecis ---
        LocalDateTime ldt = d.atTime(9, 30);
        System.out.println("13 atTime -> " + ldt + " | atStartOfDay -> " + d.atStartOfDay());
        System.out.println("14 atZone -> " + ldt.atZone(ZoneId.of("Europe/Istanbul")));
        System.out.println("15 toInstant -> " + ldt.atZone(ZoneId.of("Europe/Istanbul")).toInstant());

        // --- Bicimlendirme ---
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("16 format -> " + d.format(f) + " | " + f.format(d));
        System.out.println("17 parse  -> " + LocalDate.parse("23/08/2026", f));

        // --- PATLAYAN ORNEKLER ---
        // Teker teker yorumu kaldir, calistir, exception tipini oku, sonra geri koy.

        // System.out.println(LocalDate.of(2026, 2, 30));             // DateTimeException
        // System.out.println(d.plus(Duration.ofDays(1)));            // UnsupportedTemporalTypeException
        // System.out.println(LocalTime.of(10, 0).plus(Period.ofDays(1)));  // UnsupportedTemporalTypeException
        // System.out.println(Duration.of(1, ChronoUnit.MONTHS));     // UnsupportedTemporalTypeException
        // System.out.println(Instant.now().plus(Period.ofDays(1)));  // UnsupportedTemporalTypeException
    }
}
