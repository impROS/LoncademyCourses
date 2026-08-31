# 00 — Sınav Künyesi, Oyunun Kuralları ve Trickler

> **Süre:** ~25 dakika okuma
> **Test:** Yok — bu dosya konu değil, **strateji**. Ama en az iki kez oku: şimdi ve sınavdan bir hafta önce.

---

## Neden bu dosya

ISTQB CTFL'de insanlar konuyu bilmedikleri için değil, **sınavın nasıl soru sorduğunu bilmedikleri için**
kaybeder. 40 soruluk bir sınavda 26 doğru gerekiyor; yani **14 hakkın var**. Ama bu 14 hak, dikkatsizlikle
kolayca yenir. Bu dosya konuyu değil, **sınavı** öğretir.

**Büyük fikir:** ISTQB sınavı bilgiyi değil, **terim disiplinini** ölçer. Günlük hayatta "bug" dediğin şeye
sınav "defect" der ve "failure" ile karıştırıp karıştırmadığına bakar.

---

## 1. Künye — ezberlenecek sayılar

| | |
|---|---|
| Soru sayısı | **40** |
| Süre | **60 dakika** (soru başına ortalama 90 saniye) |
| Geçme notu | **26 / 40 = %65** |
| Ek süre | Ana dili İngilizce olmayan aday İngilizce sınava girerse **+%25 → 75 dakika** |
| Ön koşul | **Yok** — deneyim, eğitim, önceki sertifika aranmaz |
| Geçerlilik | **Süresiz** — yenileme yok, aidat yok |
| Soru tipi | Çoktan seçmeli. Çoğu tek doğru; bir kısmı "Choose TWO" |
| Puanlama | Her soru **1 puan**. K3 soruları da 1 puan — daha fazla değil |
| Yanlış cezası | **Yok.** Boş bırakmak = yanlış. Her soruyu işaretle |

> ⚠️ **Tuzak:** Bazı kaynaklar "K3 soruları 2-3 puan" der. CTFL v4.0'da bu **doğru değil**;
> toplam 40 soru, 40 puan, her soru 1 puan.

---

## 2. Bölüm ağırlıkları — nereye ne kadar çalışacaksın ⭐

| Bölüm | Konu | Puan | Müfredat süresi | En yüksek K seviyesi |
|---|---|---|---|---|
| 1 | Fundamentals of Testing | **8** | 180 dk | K2 |
| 2 | Testing Throughout the SDLC | **5** | 130 dk | K2 |
| 3 | Static Testing | **4** | 80 dk | K2 |
| 4 | **Test Analysis and Design** | **11** | 390 dk | **K3** |
| 5 | **Managing the Test Activities** | **9** | 335 dk | **K3** |
| 6 | Test Tools | **3** | 20 dk | K2 |
| | **Toplam** | **40** | 1135 dk | |

**Okuma:** 4. ve 5. bölüm birlikte **20/40 puan** — sınavın yarısı. Zamanın yarısını oraya ayır.
6. bölüm 3 puan ve 20 dakikalık müfredat — bir oturumda biter, ama 3 puanı da bedava vermek yerine oku.

**Hafıza kancası:** puanlar sırayla **8 – 5 – 4 – 11 – 9 – 3**. "Sekiz beş dört, on bir dokuz üç."

---

## 3. K seviyeleri — soru tipini bunlar belirler

Her öğrenme hedefinin bir bilişsel seviyesi var. Sınav sorusunun **şekli** bu seviyeden çıkar.

| Seviye | Fiil | Soru neye benzer | Örnek |
|---|---|---|---|
| **K1** | Hatırla (recall) | Tanım, liste, terim eşleme | *"Which of the following is a test level?"* |
| **K2** | Anla (understand) | Neden/sonuç, ayırt etme, sınıflandırma | *"Why is early testing valuable?"* |
| **K3** | Uygula (apply) | **Sen hesaplarsın.** Verilen veriyle teknik uygularsın | *"How many test cases are needed for 100% branch coverage?"* |

> ⚠️ **K3 sadece 4. ve 5. bölümdedir.** Yani "hesap yapacağın" sorular şu konulardan gelir:
> eşdeğerlik bölümleme, sınır değer analizi, karar tablosu, durum geçiş, statement/branch coverage,
> risk seviyesi hesabı, kestirim. Diğer bölümlerde kalem kâğıt gerekmez.

**Sonuç:** Sınavda kalem kâğıt kullanacaksan, **10-12 soruda** kullanacaksın. Onlara zaman ayır.

---

## 4. Soru okuma refleksleri — puanı burada kazanırsın ⭐

### 4.1 Mutlak ifadeli şıklar genelde yanlıştır

`always` · `never` · `only` · `all` · `100%` · `guarantees` · `eliminates`

ISTQB'nin doğaya bakışı olasılıksaldır: test **kusur olduğunu gösterir, olmadığını gösteremez**.
Bu yüzden "testing guarantees the software is defect-free" gibi bir şık **her zaman** çeldiricidir.

> **Ama dikkat:** Yedi prensibin kendisi mutlak ifade içerir ("Exhaustive testing is impossible",
> "Testing shows the presence, not the absence of defects"). Prensip metnindeki mutlaklık doğrudur.

### 4.2 "BEST", "MOST", "PRIMARY" görürsen dört şık da doğru olabilir

Bu kelimeler varsa soru "hangisi doğru" değil, **"hangisi en uygun"** demektir. Şıkların ikisi teknik
olarak doğru olur; senden **en iyisini** seçmen istenir. Genelde ayrım şurada olur:
- Soru bir **aşama** soruyorsa → o aşamaya en erken/en uygun olan
- Soru bir **rol** soruyorsa → o rolün tanımlı sorumluluğu (moderator/facilitator gibi)
- Soru bir **amaç** soruyorsa → müfredatın o konudaki birincil amacı

### 4.3 "(Choose TWO.)" yazıyorsa ikisini de işaretle

Bir tanesini işaretlersen **puan yok** — kısmi puan verilmez. Emin olduğun biri varsa, diğerini
kalan üç şıktan eleyerek bul.

### 4.4 Şıklardaki terimi soruda geçen terimle karıştırma

En sık kaybedilen puan burada:

| Soruda geçen | Doğru terim | Karıştırılan |
|---|---|---|
| "The developer made a mistake" | **error** (insan) | defect |
| "The code contains a wrong line" | **defect / fault / bug** (kodda) | failure |
| "The system crashed in production" | **failure** (çalışırken görünen) | defect |
| "We check the fix works" | **confirmation testing** | regression testing |
| "We check nothing else broke" | **regression testing** | confirmation testing |
| "Does it meet the user's needs?" | **validation** | verification |
| "Does it meet the specification?" | **verification** | validation |

Bu tablonun tamamı [`../99-final/cheatsheet.md`](../99-final/cheatsheet.md) içinde. Sınavdan önce son okuyacağın şey bu.

### 4.5 Soruyu iki kez oku, "NOT" ara

`Which of the following is NOT ...` soruları var. Hızlı okuyup doğru olanı işaretlersen yanarsın.
**Refleks:** soruda büyük harfli bir kelime (NOT, BEST, TWO, LEAST) varsa altını çiz.

---

## 5. Zaman yönetimi — 60 dakikada 40 soru

| Dakika | Ne yapıyorsun |
|---|---|
| 0–35 | **Birinci tur.** Bildiklerini cevapla. Takıldığın soruyu **işaretle ve geç** — 90 saniyeden fazla harcama. |
| 35–50 | **İkinci tur.** İşaretlediklerine dön. K3 hesap soruları burada çözülür. |
| 50–58 | **Üçüncü tur.** Boş kalan varsa **tahmin et** — yanlış cezası yok. |
| 58–60 | Tüm soruların işaretli olduğunu doğrula. Gönder. |

> ⚠️ **Tuzak:** İlk 10 sorunun 3'ü zor gelirse panikleme; sorular zorluk sırasına göre dizilmez.
> Zor soru kümesi rastgeledir.

**Kural:** Bir sorunun cevabını değiştirmeyi düşünüyorsan, **somut bir sebep** bulmadan değiştirme.
"İçime sinmedi" sebep değil. "Soruda BEST yazıyormuş, gözden kaçırmışım" sebeptir.

---

## 6. Nerede puan kaybediliyor — sıklık sırasıyla

1. **Terim karışıklığı** (error/defect/failure, verification/validation) — Bölüm 1
2. **Test seviyesi ile test tipini karıştırmak** — "performance testing" bir **tip**tir, seviye değil — Bölüm 2
3. **Sınır değer sayısını yanlış saymak** (2-değerli mi 3-değerli mi) — Bölüm 4 ⭐
4. **Statement ile branch coverage'ı karıştırmak** — %100 branch, %100 statement'ı garanti eder; tersi değil — Bölüm 4 ⭐
5. **Review tiplerinin resmiyet derecesini karıştırmak** (walkthrough ↔ technical review ↔ inspection) — Bölüm 3
6. **Risk = olasılık × etki** formülünde etkiyi unutmak — Bölüm 5
7. **Entry criteria ile exit criteria / DoR ile DoD** — Bölüm 5

Bu yedi başlığı sınavdan bir gün önce tekrar okursan, ortalama 4-5 soru kurtarırsın.

---

## 7. Sınav günü pratik notları

- **Online proctored sınava** gireceksen: masanda hiçbir şey olmayacak. Kalem kâğıt genelde **yasaktır**,
  onun yerine ekrandaki not alanı verilir. ⚠️ Sağlayıcının kurallarını **kayıt sırasında** oku —
  bazı sağlayıcılar beyaz tahta izni verir, bazıları vermez.
- **Test merkezinde** gireceksen: kimlik zorunlu, isim kayıtla birebir aynı olmalı.
- **Ek süre** istiyorsan bunu **kayıt sırasında** talep etmen gerekir; sınav başladıktan sonra verilmez.
- Sonuç genelde sınav biter bitmez ekranda çıkar; sertifika birkaç iş günü içinde e-posta ile gelir.

⚠️ Bu maddelerin hepsi sağlayıcıya göre değişir. **Randevu almadan önce sağlayıcının kendi sayfasından teyit et.**

---

## 60 saniyelik özet

- 40 soru · 60 dk · **26 doğru geçer** · her soru 1 puan · yanlış cezası yok → **boş bırakma**.
- Puan dağılımı: **8 – 5 – 4 – 11 – 9 – 3**. Bölüm 4 ve 5 = sınavın yarısı.
- **K3 (uygulama) sadece Bölüm 4 ve 5'te.** Hesap soruları oradan gelir.
- `always/never/only/all/100%` içeren şık → şüphelen.
- `BEST/MOST/PRIMARY` → birden fazla doğru var, en uygunu seç.
- `(Choose TWO.)` → kısmi puan yok, ikisini de işaretle.
- Soru başına **90 saniye**; takılırsan işaretle geç.
- En çok kaybedilen puan: **error / defect / failure** ayrımı.

---

## Sırada ne var

➡️ [`01-kurulum.md`](01-kurulum.md) — lab ortamını kur (Jira/Xray + JUnit).
Sadece okuyup sınava girmek istiyorsan doğrudan [`../01-temeller/1.1-test-nedir.md`](../01-temeller/1.1-test-nedir.md) ile başlayabilirsin.
