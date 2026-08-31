# 00 — Sınav Künyesi, Formatı ve Trickler

> **Süre:** ~25 dakika okuma
> **Test:** Yok — bu dosya bilgi değil, **strateji** dosyası. Sınavdan bir hafta önce tekrar oku.

---

## Neden bu konu

1Z0-830 bir "Java biliyor musun" sınavı değil. **"Derleyicinin ne yapacağını biliyor musun"** sınavı.
Günlük işte 10 yıl Java yazmış biri hazırlanmadan girerse kalır — çünkü sınav senin hiç yazmadığın kodu sorar:
kasten karışık, kasten sınırda, kasten IDE'nin senin için düzelttiği şeyler.

**Büyük fikir:** Bu sınavda sorular "bu kod ne yazdırır?" diye görünür ama gerçekte
**"bu kod derleniyor mu?"** diye sorulur. Şıkların içinde neredeyse her zaman
`Compilation fails` ve `An exception is thrown at runtime` vardır ve **bunlar çok sık doğru cevaptır.**

---

## 1. ⭐ Sınav formatı

| | |
|---|---|
| Soru sayısı | 50 · ⚠️ Doğrulanmalı |
| Süre | 120 dakika (soru başına **2.4 dakika**) · ⚠️ Doğrulanmalı |
| Geçme notu | %68 → **34 doğru** · ⚠️ Doğrulanmalı |
| Soru tipi | Çoktan seçmeli. Tek doğru veya `(Choose TWO/THREE.)` |
| Negatif puan | **Yok.** Boş bırakmak = yanlış. Her soruyu işaretle. |
| Kısmi puan | **Yok.** "Choose TWO"da bir doğru bir yanlış = 0 puan. |
| Geri dönme | Var. Soruları işaretleyip (flag) sonra dönebilirsin. |
| IDE / dokümantasyon | **Yok.** Kâğıt/kalem de yok (online proctored'da beyaz tahta verilir). |

> ⚠️ **Tuzak:** 2.4 dakika, 30 satırlık bir kod bloğunu okuyup derlenip derlenmediğine karar vermek için
> kısa bir süre. Zaman yönetimi bu sınavda **bilgi kadar** belirleyicidir. Aşağıdaki strateji bölümüne bak.

---

## 2. ⭐ Sınavın soru üretme kalıpları

Sınav 50 farklı konu sormaz — **8-10 tuzak kalıbını 50 farklı kıyafetle** sorar. Kalıpları tanı:

### Kalıp A — "Derlenir mi?"
Kod mantıklı görünür ama bir yerde derleme hatası vardır. En sık kaynaklar:

| Derleme hatası kaynağı | Nerede |
|---|---|
| `final` değişkene ikinci atama | her yerde |
| Lambda içinde effectively-final olmayan yerel değişken kullanımı | 6.1 |
| `int` bekleyen yere `long` atama (dar dönüşüm) | 1.1 |
| Checked exception yakalanmıyor / `throws` yok | 4.1 |
| Ulaşılamayan `catch` bloğu (alt sınıf üst sınıftan sonra) | 4.1 |
| `switch` expression'da tüm durumlar kapsanmamış (exhaustive değil) | 2.1 |
| `sealed` hiyerarşide `permits` dışında alt sınıf | 3.5 |
| Interface `private` metodun `abstract` olması | 3.6 |
| Local class'ta `static` üye (Java 16'dan sonra izinli — dikkat!) | 3.8 |

### Kalıp B — "Runtime'da patlar mı?"
Derlenir ama çalışırken exception atar:
`NullPointerException` (autoboxing/unboxing!), `ArrayIndexOutOfBoundsException`,
`ConcurrentModificationException`, `ArithmeticException` (`5/0` ama `5.0/0` değil!),
`UnsupportedOperationException` (`List.of()` immutable!), `ClassCastException`.

### Kalıp C — "Sırayla ne olur?"
Başlatma sırası, `finally` bloğu, try-with-resources kapanma sırası, statik blok sırası.
**Bunlar ezberlenir, çıkarılmaz.** (3.1 ve 4.2'de tablolar var.)

### Kalıp D — "Hangi API doğru?"
Şıklarda gerçekte var olmayan metot isimleri olur: `String.reverse()`, `List.addAll(int)`,
`Files.readLine()`. **Var olmayan metot = derleme hatası.** API isimlerini bilmek şart.

### Kalıp E — "Değişti mi değişmedi mi?"
Immutable tipler üzerinde metot çağırıp dönüş değerini atamamak:
```java
String s = "abc"; s.toUpperCase(); System.out.println(s);   // abc
LocalDate d = LocalDate.of(2026,1,1); d.plusDays(5);        // değişmez
```
**String, tüm Date-Time tipleri, wrapper'lar, `List.of()` — hepsi immutable.**

---

## 3. ⭐ Sık kaybedilen 12 refleks

Bunları şimdi oku, sınavdan önce tekrar oku. Her biri sana ortalama 1 soru kazandırır.

| # | Refleks |
|---|---|
| 1 | Şıklarda `Compilation fails` varsa **önce derlemeyi kontrol et**, çıktıyı sonra düşün. |
| 2 | `Integer`/`Long` karşılaştırmasında `==` görürsen **cache aralığı** (-128..127) aklına gelsin. |
| 3 | Bir metot çağrısının **dönüş değeri atanmıyorsa** ve tip immutable ise, o satır etkisizdir. |
| 4 | `List.of()`, `Map.of()`, `Arrays.asList()` → değiştirme denemesi = `UnsupportedOperationException`. |
| 5 | Tamsayı bölmesi: `5/2 == 2`. Bir taraf `double` olmadan ondalık yok. |
| 6 | `char` aritmetikte `int`'e terfi eder; `System.out.println('a' + 1)` → `98`. |
| 7 | Compound assignment (`+=`, `*=`) **gizli cast** içerir: `byte b = 10; b += 300;` derlenir. |
| 8 | `finally` bloğundaki `return` her şeyi ezer. |
| 9 | `switch` expression'ın **her dalı değer üretmeli**; `yield` gerektiren blok dallarını kontrol et. |
| 10 | Stream **terminal işlem olmadan çalışmaz** (lazy). Terminal yoksa hiçbir çıktı yok. |
| 11 | Bir stream **iki kez kullanılamaz** → `IllegalStateException`. |
| 12 | `var` sadece **yerel değişkende**; alan, parametre, dönüş tipi olamaz; `var x = null;` derlenmez. |

---

## 4. Alan dağılımı (tahmini)

> ⚠️ Oracle 1Z0-830 için **resmî yüzde ağırlık yayımlamıyor** — sadece hedef başlıklarını listeliyor.
> Aşağıdaki dağılım, hedef sayısına ve konu genişliğine dayanan **tahmindir**. Deneme sınavları da bu
> dağılımı kullanır. Ağırlıklara değil, **hiçbir alanı boş bırakmamaya** oyna: 50 soruda her alandan soru var.

| Alan | Konular | Tahmini soru |
|---|---|---|
| Nesne yönelimli Java | 3.1–3.8 | ~12 |
| Streams ve lambda | 6.1–6.4 | ~8 |
| Diziler ve koleksiyonlar | 5.1–5.3 | ~6 |
| Temel tipler, metin, tarih/saat | 1.1–1.3 | ~6 |
| Eşzamanlılık | 8.1–8.3 | ~5 |
| İstisnalar | 4.1–4.2 | ~4 |
| Akış kontrolü | 2.1–2.2 | ~3 |
| I/O | 9.1–9.2 | ~3 |
| Modüller ve paketleme | 7.1–7.2 | ~2 |
| Yerelleştirme | 10.1 | ~1 |

**Java 17→21 arasında eklenen konular kesinlikle sorulur:** virtual threads (8.1),
sequenced collections (5.3), record patterns ve pattern matching for switch (2.1, 3.3).
Bunlar sınavın "yeni" tarafı ve hazırlananların en zayıf olduğu yer.

---

## 5. ⭐ Sınav günü stratejisi

### Zaman bütçesi
| Aşama | Süre | Ne yapılır |
|---|---|---|
| 1. tur | ~85 dk | Her soruyu **bir kez** oku. 2 dakikada çözemiyorsan işaretle (flag) ve geç. |
| 2. tur | ~25 dk | İşaretli soruları çöz. |
| 3. tur | ~10 dk | Boş kalan her şeye tahmin işaretle. Kontrol et: boş soru kalmasın. |

### Kod okuma tekniği
1. **Önce şıklara bak.** `Compilation fails` var mı? Varsa gözün derleme hatası arasın.
2. **Sonra imzalara bak:** tipler, `final`, `static`, erişim belirteçleri. Hata genelde burada saklıdır.
3. **Sonra satır satır izle.** Değişken değerlerini beyaz tahtaya/zihinde takip et.
4. **Satır numarası verilmişse** (`// line 5`) o satır neredeyse kesin sorunun merkezidir.

### Eleme taktiği
- İki şık **aynı anlama geliyorsa** ikisi de yanlıştır.
- Mutlak ifadeli şıklar (`always`, `never`, `only`, `all`) genelde yanlıştır — ama Java'da bazı kurallar
  gerçekten mutlaktır (`finally` her zaman çalışır — `System.exit()` hariç). Körlemesine eleme.
- `(Choose TWO.)` sorularında **emin olduğun bir doğruyu bul**, ikinciyi ona göre ara.

> ⚠️ **Tuzak:** Sınav bir soruda `Compilation fails` şıkkını çekici hale getirmek için kodu karmaşık yazar.
> Karmaşık ≠ hatalı. Sadece **gerçek bir kural ihlali** bulursan bu şıkkı seç.

---

## 6. Ne satın alınmalı, neye para verme

Ayrıntılı liste [README](../README.md#ne-satın-alınmalı--alınmamalı)'de.
Kısası: **voucher (Oracle) + Enthuware mock testleri**. Dumps siteleri sertifikanı iptal ettirir.

---

## 60 saniyelik özet

- 50 soru · 120 dk · %68 (34 doğru) · negatif puan yok · kısmi puan yok. ⚠️ Teyit et.
- Soru "ne yazdırır" der, aslında **"derleniyor mu"** diye sorar.
- `Compilation fails` ve `An exception is thrown` şıkları **sık doğrudur**.
- Immutable tipte dönüş değeri atanmıyorsa o satır etkisizdir.
- `Integer` cache: -128..127 arası `==` `true`, dışı `false`.
- Java 17→21 yenilikleri (virtual threads, sequenced collections, record/switch pattern) kesin çıkar.
- Soru başına 2 dakika; çözemediğini işaretle geç, **hiçbir soruyu boş bırakma**.
- Resmî ağırlık yok; her alandan soru gelir, hiçbirini atlama.

---

## Kendini kontrol

1. Şıklarda `Compilation fails` gördüğünde ilk yapman gereken ne?
2. `(Choose TWO.)` sorusunda bir doğru bir yanlış işaretlersen kaç puan alırsın?
3. `String s = "a"; s.concat("b");` sonrası `s` nedir? Neden?
4. Sınavda kaç doğru cevapla geçersin ve bu yüzde kaça denk gelir?
5. Java 17'den 21'e eklenen ve sınavda kesin sorulacak dört konu hangileri?

➡️ **Cevaplar:** [`cevaplar.md#00-sınav-künyesi-formatı-ve-trickler`](cevaplar.md#00-sınav-künyesi-formatı-ve-trickler) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## Sırada ne var
➡️ [`01-kurulum.md`](01-kurulum.md) — çalışma ortamını kur, örnekleri çalıştırabildiğini doğrula.
