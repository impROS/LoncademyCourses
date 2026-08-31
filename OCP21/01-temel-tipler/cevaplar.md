# 01 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 1.1 Primitifler, wrapper'lar, operatörler ve tip dönüşümleri

### Soru 1 — `byte b = 5; b = b * 2;` ile `b *= 2;` arasındaki fark nedir ve neden?

**Kısa cevap:** **`b = b * 2;` DERLENMEZ, `b *= 2;` derlenir.** Compound
assignment gizli bir cast yapar.

**Ayrıntı:** İkili aritmetikte `byte` **`int`'e terfi eder** (numeric
promotion), yani `b * 2` bir `int`'tir ve onu `byte`'a cast'siz atamak derleme
hatasıdır. `b *= 2;` ise eşdeğeri `b = (byte)(b * 2)` — compound assignment
operatörü **örtük cast** ekler, o yüzden derlenir (taşarsa sessizce sarar).

📌 **Sık yapılan hata:** İkisini eşdeğer sanmak. `*=` gizli cast yapar, `=` +
çarpma yapmaz.

🔗 [1.1 §1 Sayısal terfi ve compound assignment](1.1-primitifler-ve-operatorler.md)

### Soru 2 — `final int x = 100; byte y = x;` derlenir mi? `final` kaldırılırsa ne olur?

**Kısa cevap:** **`final` ile derlenir; `final` kaldırılınca derlenmez.**

**Ayrıntı:** `final int x = 100` bir **derleme zamanı sabitidir** ve 100
`byte`'a sığar (−128…127), bu yüzden cast'siz daraltma atamasına izin verilir.
`final` yoksa `x` sabit değildir; derleyici değerini garanti edemez ve `int →
byte` daraltması cast ister.

📌 **Sık yapılan hata:** `final`'ın burada "değişmezlik" için olduğunu sanmak.
Asıl işlevi değerin **derleme zamanında sabit** olmasını sağlamaktır.

🔗 [1.1 §1 Sabit ifade ataması](1.1-primitifler-ve-operatorler.md)

### Soru 3 — `Integer a = 127, b = 127, c = 128, d = 128;` — `a==b` ve `c==d` sonuçları ne, neden?

**Kısa cevap:** **`a==b` → true, `c==d` → false.**

**Ayrıntı:** `Integer` autoboxing **−128…127** aralığındaki değerleri önbellekten
(cache) verir; aynı değer aynı nesnedir, `==` true. 127 bu aralıkta olduğu için
`a` ve `b` aynı nesnedir. 128 aralık dışındadır; her biri yeni `Integer` nesnesi
olur ve `==` (referans karşılaştırması) false döner. Değer karşılaştırmak için
`equals` gerekir.

📌 **Sık yapılan hata:** `Integer` `==`'ini her zaman değer karşılaştırması
sanmak. Cache aralığı dışında `==` referans karşılaştırır.

🔗 [1.1 60 sn özet (Integer cache −128…127)](1.1-primitifler-ve-operatorler.md)

### Soru 4 — `System.out.println(1 + 2 + "3" + 4 + 5);` ne yazdırır?

**Kısa cevap:** **`3345`.**

**Ayrıntı:** `+` soldan sağa değerlendirilir. `1 + 2` henüz iki sayıdır → `3`.
Sonra `3 + "3"` bir String devreye girdiği için birleştirmedir → `"33"`.
Ondan sonrası hep String birleştirmesidir: `"33" + 4` → `"334"`, `+ 5` →
`"3345"`. String bir kez girince gerisi birleştirmedir.

📌 **Sık yapılan hata:** Baştaki `1 + 2`'yi de birleştirme sanıp `123345`
beklemek. String görülene kadar `+` aritmetiktir.

🔗 [1.1 60 sn özet (String bir kez girince birleştirme)](1.1-primitifler-ve-operatorler.md)

### Soru 5 — `10 % 0`, `10.0 % 0`, `10 / 0.0` — üçünün sonucu ne?

**Kısa cevap:** **`10 % 0` → `ArithmeticException`; `10.0 % 0` → `NaN`; `10 /
0.0` → `Infinity`.**

**Ayrıntı:** **Tamsayı** sıfıra bölme/mod → `ArithmeticException` (çalışma
zamanında atılır). **Kayan nokta** aritmetiği exception atmaz: `10.0 % 0` bir
kayan nokta işlemi olduğu için `NaN` verir, `10 / 0.0` ise `Infinity` verir
(pozitif sonsuz). Kural: en az bir operand `double`/`float` ise işlem kayan
noktadır ve patlamaz.

📌 **Sık yapılan hata:** Üçünü de exception atar sanmak. Yalnızca **tamsayı**
sıfıra bölme exception atar; kayan noktada `NaN`/`Infinity` çıkar.

🔗 [1.1 60 sn özet (tamsayı /0 vs kayan nokta)](1.1-primitifler-ve-operatorler.md)

### Soru 6 — `Math.round(2.5)`, `Math.round(-2.5)`, `Math.round(2.4f)` — dönüş tipleri ve değerleri?

**Kısa cevap:** **`round(2.5)` → `long` 3 · `round(-2.5)` → `long` -2 ·
`round(2.4f)` → `int` 2.**

**Ayrıntı:** `Math.round(double)` bir **`long`**, `Math.round(float)` bir
**`int`** döner — argüman tipine göre. Yuvarlama `floor(x + 0.5)` mantığındadır:
`2.5 → 3`, ama `-2.5 → -2` (çünkü `-2.5 + 0.5 = -2.0`, aşağı yuvarlanır −2).
`2.4f → 2`.

📌 **Sık yapılan hata:** `-2.5`'i `-3`'e yuvarlamak. Round pozitif yöne
(`x + 0.5`) çalışır; `-2.5` → `-2`. Ayrıca dönüş tipi argümana bağlıdır.

🔗 [1.1 60 sn özet (`Math.round(-2.5)` = -2)](1.1-primitifler-ve-operatorler.md)

## 1.2 String, StringBuilder ve text block'lar

### Soru 1 — `String s = "abc"; s.concat("d"); s.toUpperCase();` sonrası `s` nedir ve neden?

**Kısa cevap:** **Hâlâ `"abc"`.** String immutable'dır.

**Ayrıntı:** `String`'in her metodu **yeni bir String döner**, mevcut nesneyi
değiştirmez. `s.concat("d")` `"abcd"` üretir ama bir değişkene atanmadığı için
kaybolur; `s.toUpperCase()` `"ABC"` üretir, o da atılır. `s` değişkeni ilk
gösterdiği `"abc"`'yi göstermeye devam eder.

📌 **Sık yapılan hata:** String metotlarının nesneyi yerinde değiştirdiğini
sanmak. Dönüş değeri atanmazsa değişiklik kaybolur.

🔗 [1.2 §1 Immutability](1.2-string-ve-stringbuilder.md)

### Soru 2 — `"ja" + "va" == "java"` neden `true`, `e + "va" == "java"` neden `false`?

**Kısa cevap:** **`"ja" + "va"` derleme zamanı sabitidir (havuzdan),** `e + "va"`
(e bir değişkense) çalışma zamanında birleşir → yeni nesne.

**Ayrıntı:** Derleyici, tümüyle **literal** olan `"ja" + "va"`'yı derleme
anında birleştirip havuzdaki (pool) `"java"` ile aynı nesneye bağlar → `==`
true. `e` bir `String` **değişkeniyse** `e + "va"` çalışma zamanında hesaplanır
ve havuz dışında yeni bir nesne üretir → `==` false. Not: `final String e =
"ja"` yapılırsa `e` de derleme zamanı sabiti olur ve sonuç true'ya döner.

📌 **Sık yapılan hata:** Değeri aynı olan iki String'i `==` ile eşit sanmak.
Değer eşitliği `equals`; `==` referans (havuz) eşitliğidir.

🔗 [1.2 §1 String pool ve ==](1.2-string-ve-stringbuilder.md)

### Soru 3 — `"java".substring(4)`, `"java".substring(2,2)`, `"java".substring(5)` — üçünün sonucu?

**Kısa cevap:** **`substring(4)` → `""` · `substring(2,2)` → `""` ·
`substring(5)` → `StringIndexOutOfBoundsException`.**

**Ayrıntı:** `"java"` uzunluğu 4. `substring(4)` başlangıç = uzunluk, boş String
verir (geçerli). `substring(2,2)` başlangıç = bitiş, yine boş String. Ama
`substring(5)` başlangıç uzunluğu aşar (5 > 4) → çalışma zamanında
`StringIndexOutOfBoundsException`. Sınır: `beginIndex` 0…length arası olmalı.

📌 **Sık yapılan hata:** `substring(length)`'i de hata sanmak. Tam uzunlukta boş
String döner; ancak uzunluğu **aşınca** patlar.

🔗 [1.2 §2 String metotları / sınır davranışları](1.2-string-ve-stringbuilder.md)

### Soru 4 — `StringBuilder`'da `equals` neden değer karşılaştırmaz, doğru kullanım ne?

**Kısa cevap:** **`StringBuilder` `equals`'i override etmez** (Object'in referans
karşılaştırması çalışır); değer için `sb.toString().equals(...)` kullanılır.

**Ayrıntı:** `String.equals` içeriği karşılaştırır ama `StringBuilder` bunu
override etmediği için iki farklı `StringBuilder` aynı içerikte bile
`equals` false döner (referans eşitliği). İçeriği karşılaştırmak için önce
`toString()` ile String'e çevir, sonra `equals` kullan.

📌 **Sık yapılan hata:** İki `StringBuilder`'ı `equals` ile içerikçe
karşılaştırmak. `String`'e çevirmeden içerik eşitliği yapılamaz.

🔗 [1.2 §3 StringBuilder](1.2-string-ve-stringbuilder.md)

### Soru 5 — Text block'ta kapanış `"""` ayrı satırda olursa ne değişir?

**Kısa cevap:** **Kapanış `"""` ayrı satırdaysa metin sonuna bir satır sonu
(`\n`) eklenir;** aynı satırdaysa eklenmez.

**Ayrıntı:** Text block'ta son içerik satırından sonra kapanış `"""` yeni bir
satıra konursa, metin bir **trailing newline** ile biter. Kapanış son içerik
satırının hemen ardına (aynı satıra) yazılırsa o satır sonu olmaz. Girinti de
kapanış `"""`'nin konumuna göre kırpılır (incidental whitespace).

📌 **Sık yapılan hata:** Kapanış `"""`'nin konumunu önemsiz sanmak. Ayrı satır
= sonda `\n`, aynı satır = yok; sınav bu farkı sorar.

🔗 [1.2 §4 Text block girinti kuralları](1.2-string-ve-stringbuilder.md)

### Soru 6 — `sb.delete(1, 100)` ile `s.substring(1, 100)` arasındaki davranış farkı ne?

**Kısa cevap:** **`StringBuilder.delete` bitiş indexini uzunlukla sınırlar (patlamaz);
`String.substring(1,100)` aşınca `StringIndexOutOfBoundsException` atar.**

**Ayrıntı:** `delete(start, end)` `end` uzunluğu aşarsa onu uzunluğa çeker ve
sonuna kadar siler — hata vermez. `substring(begin, end)` ise `end` uzunluğu
aşınca istisna atar. Aynı "aralık aşımı" iki sınıfta farklı ele alınır.

📌 **Sık yapılan hata:** İki sınıfın sınır davranışını aynı sanmak.
`StringBuilder` toleranslı, `String` katıdır.

🔗 [1.2 §3 StringBuilder / sınır davranışları](1.2-string-ve-stringbuilder.md)

## 1.3 Date-Time API

### Soru 1 — `LocalDate.of(2026,1,31).plusMonths(1).plusMonths(1)` sonucu nedir, neden 31 Mart değil?

**Kısa cevap:** **2026-03-31.** İki ayrı `plusMonths(1)` her adımda ayın son
geçerli gününe **düzeltir**, ama zincir tekrar 31'e dönemez.

**Ayrıntı:** `2026-01-31` + 1 ay → Şubat'ta 31 yok, en yakın geçerli gün
`2026-02-28`. Ondan + 1 ay → `2026-03-28`. Yani sonuç **2026-03-28**, 31 Mart
değil: ara adımda gün 28'e düşürülür ve bir sonraki adım 28'den ilerler.
(Doğrudan `plusMonths(2)` de aynı 28'i verirdi; tarih işlemleri geri gün
kazandırmaz.)

📌 **Sık yapılan hata:** Ayın gününü ekleme boyunca "31" olarak korunur sanmak.
Geçersiz gün en yakın geçerli güne düşürülür ve bu düşüş kalıcıdır.

🔗 [1.3 Takvim tabanlı işlemler](1.3-date-time-api.md)

### Soru 2 — `LocalDate` nesnesine `Duration` eklersen ne olur — derleme hatası mı, runtime hatası mı?

**Kısa cevap:** **Runtime hatası** (`UnsupportedTemporalTypeException`). Derlenir
ama çalışırken patlar.

**Ayrıntı:** `LocalDate` yalnızca tarih tutar, saat/saniye kavramı yoktur.
`Duration` saniye tabanlıdır. `date.plus(duration)` derlenir çünkü `plus`
genel bir `TemporalAmount` alır; ama çalışma zamanında `LocalDate` saniye
birimini desteklemediği için istisna atar. `LocalDate` ile **`Period`**
kullanılmalıdır.

📌 **Sık yapılan hata:** Bunu derleme hatası sanmak. İmza uyduğu için derlenir;
uyumsuzluk **çalışma zamanında** ortaya çıkar.

🔗 [1.3 Period (takvim) vs Duration (saniye)](1.3-date-time-api.md)

### Soru 3 — `Period.ofYears(1).ofMonths(6)` neyi yazdırır, doğru yazım nedir?

**Kısa cevap:** **`P6M` yazdırır** (yılı kaybeder). Doğrusu
`Period.of(1, 6, 0)`.

**Ayrıntı:** `ofYears` ve `ofMonths` **statik** metotlardır; zincirlenince
ikincisi birincinin sonucunu **yok sayar** ve sıfırdan yeni bir `Period` üretir.
`Period.ofYears(1).ofMonths(6)` aslında `Period.ofMonths(6)` demektir → `P6M`.
Bir yıl altı ay için `Period.of(1, 6, 0)` yazılır.

📌 **Sık yapılan hata:** Statik fabrika metotlarını zincirleyip biriktiklerini
sanmak. Her biri bağımsız üretir; zincir öncekini ezer.

🔗 [1.3 Period oluşturma](1.3-date-time-api.md)

### Soru 4 — `Duration.ofDays(1)` neden `PT24H` yazdırır?

**Kısa cevap:** **`Duration` zaman (saniye) tabanlıdır;** bir gün onun için
kesin **24 saattir**, `PT24H` biçiminde gösterilir.

**Ayrıntı:** ISO-8601 biçiminde `P` bir period, `T`'den sonrası zamandır.
`Duration` takvim değil süre tuttuğu için "1 gün"ü gün olarak değil, saniye
cinsinden 24 saat olarak taşır → `PT24H`. `Period.ofDays(1)` ise takvim günü
olduğu için `P1D` gösterir.

📌 **Sık yapılan hata:** `Duration.ofDays(1)`'in `P1D` yazdırmasını beklemek.
`Duration` günü saate çevirir (`PT24H`); `P1D` `Period`'un biçimidir.

🔗 [1.3 Duration biçimi (PT...)](1.3-date-time-api.md)

### Soru 5 — `LocalDateTime`'ı `Instant`'a çevirmek için hangi bilgi eksiktir?

**Kısa cevap:** **Saat dilimi (zone/offset).**

**Ayrıntı:** `LocalDateTime` tarih + saat tutar ama **dilim yoktur**; `Instant`
ise UTC'de kesin bir andır. İkisi arasındaki boşluk zaman dilimidir: `ldt.
atZone(ZoneId.of(...))` ya da `ldt.toInstant(ZoneOffset...)` ile dilim/offset
verilmeden dönüşüm yapılamaz — çünkü aynı yerel saat farklı dilimlerde farklı
bir ana karşılık gelir.

📌 **Sık yapılan hata:** `LocalDateTime.toInstant()`'ı argümansız çağırmaya
çalışmak. Dilim bilgisi verilmeden yerel saat bir ana bağlanamaz.

🔗 [1.3 LocalDateTime vs Instant](1.3-date-time-api.md)

### Soru 6 — DST geçiş gününde `Period.ofDays(1)` ile `Duration.ofDays(1)` neden farklı sonuç verir?

**Kısa cevap:** **`Period` bir takvim günü ekler** (saat farkına bakmaz);
**`Duration` tam 24 saat ekler** — DST gününde gün 23 ya da 25 saat olabildiği
için sonuçlar ayrışır.

**Ayrıntı:** Yaz saati geçişinde bir takvim günü 24 saat değildir. `Period.
ofDays(1)` "yarın aynı saat" der (takvim), `Duration.ofDays(1)` "tam 86400
saniye sonra" der (saat). 23 saatlik geçiş gününde `Duration` ekleme, takvim
gününü bir saat aşar/eksiltir; ikisi farklı yerel saat verir.

📌 **Sık yapılan hata:** İkisini her zaman eşit sanmak. Normal günde aynı,
**DST geçiş gününde farklı** sonuç verirler — tam olarak sınavın sorduğu ayrım.

🔗 [1.3 Period vs Duration (DST)](1.3-date-time-api.md)
