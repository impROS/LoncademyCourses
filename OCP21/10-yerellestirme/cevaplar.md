# 10 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 10.1 Locale, ResourceBundle ve biçimlendirme

### Soru 1 — `Locale` yazımında dil ve ülke kodları hangi harf düzenindedir?

**Kısa cevap:** **Dil kodu küçük harf, ülke kodu BÜYÜK harf.**

**Ayrıntı:** Dil kodu küçük harfle yazılır (`tr`, `en`, `fr`) ve zorunludur. Ülke kodu BÜYÜK harfle yazılır (`TR`, `US`) ve isteğe bağlıdır. Yani `Locale.of("tr", "TR")` doğru, `Locale.of("TR", "tr")` yanlış anlamlıdır. Yalnızca dil (`Locale.of("tr")`) geçerlidir ama yalnızca ülke anlamsızdır — dil zorunludur.

📌 **Sık yapılan hata:** `new Locale("TR", "tr")` yazmak; bu **derlenir ve çalışır** ama yanlış anlamlıdır — Java kodları düzeltmez. Sınav "hangisi doğru yazım" diye sorar.

🔗 [10.1 §1 Locale (dil küçük, ülke BÜYÜK)](10.1-locale-ve-bicimlendirme.md)

### Soru 2 — `ResourceBundle` bir anahtarı seçilen bundle'da bulamazsa ne yapar?

**Kısa cevap:** **Daha genel (üst) bundle'lara doğru aramaya devam eder; en sonda default bundle'a bakar.**

**Ayrıntı:** Bundle **seçimi** ile **anahtar araması** farklı şeylerdir. Bir bundle seçildikten sonra istenen anahtar o dosyada yoksa, arama daha genel bundle'lara doğru ilerler: `Mesaj_tr_TR` → `Mesaj_tr` → (varsayılan locale bundle'ları) → `Mesaj` (default bundle). Bu yüzden `Mesaj_tr_TR.properties` yalnızca **farklı olan** anahtarları içerebilir; ortak anahtar default bundle'dan gelir. Örneğin anahtar yalnızca default bundle'da varsa oradaki değer döner.

📌 **Sık yapılan hata:** Seçilen bundle'da anahtar yoksa hemen `MissingResourceException` atılacağını sanmak. Önce daha genel bundle'lara bakılır.

🔗 [10.1 §2 ResourceBundle ve arama sırası](10.1-locale-ve-bicimlendirme.md)

### Soru 3 — Anahtar hiçbir bundle'da yoksa ne olur, bu checked mi unchecked mi?

**Kısa cevap:** **`MissingResourceException` atılır ve bu unchecked'tır.**

**Ayrıntı:** Anahtar hiçbir bundle dosyasında bulunamazsa (veya hiç bundle dosyası yoksa), tüm arama sırası tükendikten sonra `MissingResourceException` atılır. Bu istisna **unchecked**'tır — yani yakalanması zorunlu değildir, derleyici seni buna zorlamaz. `NumberFormat.parse`'ın attığı `ParseException`'dan (checked) bu yönüyle ayrılır.

📌 **Sık yapılan hata:** `MissingResourceException`'ı checked sanıp yakalanması gerektiğini düşünmek. Unchecked'tır.

🔗 [10.1 §2 Arama sırası (MissingResourceException)](10.1-locale-ve-bicimlendirme.md)

### Soru 4 — Türkçe locale'de `1234567.891` nasıl görünür?

**Kısa cevap:** **`1.234.567,891`** — nokta binlik, virgül ondalık ayırıcı.

**Ayrıntı:** Türkçe locale'de binlik ayırıcı **nokta**, ondalık ayırıcı **virgüldür** — İngilizcenin tam tersi. `NumberFormat.getInstance(tr)` ile `1234567.891` → `1.234.567,891` olur; aynı sayı `Locale.US`'te `1,234,567.891` olur. Para için `tr_TR`'de `₺1.234,50`, yüzde için `%25` biçimleri geçerlidir.

📌 **Sık yapılan hata:** Türkçe ve İngilizce ayırıcıları karıştırmak. Türkçe'de nokta = binlik, virgül = ondalık.

🔗 [10.1 §3 NumberFormat (Türkçe ayırıcılar)](10.1-locale-ve-bicimlendirme.md)

### Soru 5 — `NumberFormat.parse` hangi tipi döner ve hangi exception'ı atar?

**Kısa cevap:** **`Number` döner ve checked `ParseException` atar.**

**Ayrıntı:** `parse` metodu `int` veya `double` değil, `Number` döner — bu somut olarak `Integer`, `Long` ya da `Double` olabilir. Ayrıca **checked `ParseException`** atar; bu yüzden çağrı yakalanmalı (try/catch) veya bildirilmelidir. Baştan itibaren ayrıştırabildiği kadarını alır; metnin sonunda geçersiz kısım kalırsa her zaman exception atmayabilir. Örn. `NumberFormat.getInstance(tr).parse("1.234,5")` → `1234.5`.

📌 **Sık yapılan hata:** Dönüşü doğrudan `int`/`double` sanmak veya `ParseException`'ı unchecked sanıp yakalamamak. Dönüş `Number`, exception checked'tır.

🔗 [10.1 §3 parse — metinden sayıya](10.1-locale-ve-bicimlendirme.md)

### Soru 6 — `toString()` ile `toLanguageTag()` çıktıları nasıl farklıdır?

**Kısa cevap:** **`toString()` → `tr_TR` (alt çizgi); `toLanguageTag()` → `tr-TR` (tire).**

**Ayrıntı:** `Locale.toString()` dil ve ülkeyi **alt çizgiyle** birleştirir: `tr_TR`. `toLanguageTag()` ise BCP 47 etiketi üretir ve **tire** kullanır: `tr-TR`. Aynı ayrım kurma tarafında da vardır: `Locale.forLanguageTag("tr-TR")` tire ile etiket alır. `getLanguage()` `tr`, `getCountry()` `TR` döner.

📌 **Sık yapılan hata:** İki metodun çıktısını aynı sanmak. `toString` alt çizgi, `toLanguageTag` tire kullanır.

🔗 [10.1 §1 Locale (toString vs toLanguageTag)](10.1-locale-ve-bicimlendirme.md)
