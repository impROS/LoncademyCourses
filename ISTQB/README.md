# ISTQB CTFL v4.0 — Sıfırdan Sertifikaya Çalışma Seti

Bu klasör bir sohbet çıktısı değil, **4 haftalık bir kurs**. Konu dosyalarını sırayla okuyup her konunun
sonundaki testi çözersen, sınava girmek için başka bir kaynağa ihtiyacın kalmaz.

> **Hedef sınav:** ISTQB® Certified Tester Foundation Level, **sürüm 4.0** (güncel müfredat metni: v4.0.1, 15.09.2024)
> **Format:** 40 soru · 60 dakika · **26/40 doğru = geçer (%65)**
> Ana dili İngilizce olmayan adaylar İngilizce sınava girerse **+%25 ek süre (75 dk)** hakkı vardır.

---

## Nasıl çalışılır

1. **Sırayla git.** Konular birbirine bağlı. 4. bölümdeki teknikler 1. bölümdeki terimleri kullanır.
2. **Her konuyu bir oturumda bitir.** Dosyayı oku → pratiği yap → testi çöz. Yarım bırakma.
3. **Testi konuyu okumadan çözme.** Test, konu dosyasındaki bilgiyle çözülecek şekilde yazıldı.
   Dışarıdan bilgi gerekmiyor; çözemiyorsan konuyu tam okumamışsındır.
4. **%80 altındaysan geri dön.** Test sonunda "zayıf alan" raporu çıkar. O alt başlığı tekrar oku, testi tekrar çöz.
   Motor soruları ve şıkları her seferinde karıştırır, ezberleyemezsin.
5. **Kâğıda yaz.** Her konunun sonunda "Kendini kontrol" soruları var. Kafadan geçirmek sayılmaz, yaz.
6. **Sınav dili İngilizce.** Anlatım Türkçe ama testlerdeki sorular İngilizce — gerçek sınavda göreceğin
   cümle kalıplarına alışman için. Terimlerin İngilizce karşılığını ezberle, Türkçe çevirisini değil.
7. **Son hafta yeni konu açma.** 4. hafta sadece tekrar + deneme sınavı.

---

## Sınav künyesi

| | |
|---|---|
| Sertifika | ISTQB® CTFL — Certified Tester Foundation Level |
| Müfredat sürümü | v4.0 (metin v4.0.1) |
| Soru sayısı | 40 çoktan seçmeli |
| Süre | 60 dakika (+15 dk ek süre hakkı, ana dili İngilizce olmayanlar için) |
| Geçme notu | **26 / 40 (%65)** |
| Ön koşul | Yok |
| Geçerlilik | Süresiz (yenileme gerekmez) |
| Ücret | Ülkeye ve sınav sağlayıcısına göre değişir — ⚠️ **kayıt öncesi teyit et** |

Detay ve strateji: [`00-baslangic/00-sinav-kunyesi.md`](00-baslangic/00-sinav-kunyesi.md)

---

## İlerleme tablosu

Her konuyu bitirdiğinde kutucuğu işaretle. Test skorunu yanına yaz.

### 00 — Başlangıç

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Sınav künyesi, oyunun kuralları ve trickler](00-baslangic/00-sinav-kunyesi.md) | — | — |
| [ ] | [Ortam kurulumu — Jira/Xray + Java/JUnit lab ortamı](00-baslangic/01-kurulum.md) | — | — |
| [ ] | [Kayıt ve satın alma — ne alınmalı, ne alınmamalı](00-baslangic/02-kayit-ve-satin-alma.md) | — | — |

### 01 — Fundamentals of Testing *(8/40 puan · 180 dk)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Test nedir? Hedefler ve debugging](01-temeller/1.1-test-nedir.md) | [test](01-temeller/1.1-test.html) | — |
| [ ] | [1.2 Test neden gerekli? Hata, kusur, arıza](01-temeller/1.2-test-neden-gerekli.md) | [test](01-temeller/1.2-test.html) | — |
| [ ] | [1.3 Yedi test prensibi](01-temeller/1.3-test-prensipleri.md) | [test](01-temeller/1.3-test.html) | — |
| [ ] | [1.4 Test aktiviteleri, testware ve roller](01-temeller/1.4-test-aktiviteleri.md) | [test](01-temeller/1.4-test.html) | — |
| [ ] | [1.5 Temel beceriler ve bağımsızlık](01-temeller/1.5-beceriler.md) | [test](01-temeller/1.5-test.html) | — |

### 02 — Testing Throughout the SDLC *(5/40 puan · 130 dk)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 SDLC, shift left ve DevOps](02-yasam-dongusu/2.1-sdlc.md) | [test](02-yasam-dongusu/2.1-test.html) | — |
| [ ] | [2.2 Test seviyeleri ve test tipleri](02-yasam-dongusu/2.2-seviyeler-ve-tipler.md) | [test](02-yasam-dongusu/2.2-test.html) | — |
| [ ] | [2.3 Bakım testi](02-yasam-dongusu/2.3-bakim-testi.md) | [test](02-yasam-dongusu/2.3-test.html) | — |

### 03 — Static Testing *(4/40 puan · 80 dk)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [3.1 Statik test temelleri](03-statik-test/3.1-statik-test-temelleri.md) | [test](03-statik-test/3.1-test.html) | — |
| [ ] | [3.2 Geri bildirim ve gözden geçirme süreci](03-statik-test/3.2-gozden-gecirme.md) | [test](03-statik-test/3.2-test.html) | — |

### 04 — Test Analysis and Design *(11/40 puan · 390 dk ⭐ en ağır bölüm)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [4.1 Test tekniklerine genel bakış](04-analiz-tasarim/4.1-teknikler-genel.md) | [test](04-analiz-tasarim/4.1-test.html) | — |
| [ ] | [4.2a Eşdeğerlik bölümleme ve sınır değer analizi ⭐](04-analiz-tasarim/4.2a-ep-bva.md) | [test](04-analiz-tasarim/4.2a-test.html) | — |
| [ ] | [4.2b Karar tablosu ve durum geçiş testi ⭐](04-analiz-tasarim/4.2b-karar-durum.md) | [test](04-analiz-tasarim/4.2b-test.html) | — |
| [ ] | [4.3 Beyaz kutu teknikleri ⭐](04-analiz-tasarim/4.3-beyaz-kutu.md) | [test](04-analiz-tasarim/4.3-test.html) | — |
| [ ] | [4.4 Deneyim tabanlı teknikler](04-analiz-tasarim/4.4-deneyim-tabanli.md) | [test](04-analiz-tasarim/4.4-test.html) | — |
| [ ] | [4.5 İşbirliği tabanlı yaklaşımlar (ATDD)](04-analiz-tasarim/4.5-isbirligi.md) | [test](04-analiz-tasarim/4.5-test.html) | — |

### 05 — Managing the Test Activities *(9/40 puan · 335 dk ⭐)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [5.1a Test planlama ve giriş/çıkış kriterleri](05-yonetim/5.1a-test-planlama.md) | [test](05-yonetim/5.1a-test.html) | — |
| [ ] | [5.1b Kestirim, önceliklendirme, piramit ve quadrant ⭐](05-yonetim/5.1b-kestirim-piramit.md) | [test](05-yonetim/5.1b-test.html) | — |
| [ ] | [5.2 Risk yönetimi ⭐](05-yonetim/5.2-risk-yonetimi.md) | [test](05-yonetim/5.2-test.html) | — |
| [ ] | [5.3 İzleme, kontrol, tamamlama ve raporlama](05-yonetim/5.3-izleme-kontrol.md) | [test](05-yonetim/5.3-test.html) | — |
| [ ] | [5.4–5.5 Konfigürasyon yönetimi ve kusur yönetimi](05-yonetim/5.4-konfigurasyon-kusur.md) | [test](05-yonetim/5.4-test.html) | — |

### 06 — Test Tools *(3/40 puan · 20 dk)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [6.1–6.2 Araç desteği ve otomasyonun riskleri](06-araclar/6.1-araclar.md) | [test](06-araclar/6.1-test.html) | — |

### 99 — Final

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet — tek sayfa özet](99-final/cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar — sınavdan 24 saat önce oku](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Deneme Sınavı 1 · 40 soru / 60 dk | [test](99-final/deneme-1.html) | — |
| [ ] | Deneme Sınavı 2 · 40 soru / 60 dk | [test](99-final/deneme-2.html) | — |

---

## 4 haftalık program

Haftada ~6 saat. Hafta içi 2 gün + hafta sonu 1 gün gibi bölebilirsin.

| Hafta | Konular | Süre | Hedef |
|---|---|---|---|
| **1** | 00-baslangic tamamı + 1.1 → 1.5 | ~5 sa | Terminoloji oturmalı. Hata/kusur/arıza ayrımını uykuda söyleyebilmelisin. |
| **2** | 2.1 → 2.3, 3.1 → 3.2, 6.1 | ~5 sa | Seviye/tip matrisi ve review tipleri. Hafif bölümleri erken bitir. |
| **3** | 4.1 → 4.5 (⭐ en ağırı) | ~7 sa | Teknikleri **uygulayabilmelisin** — bu bölüm K3, ezber yetmez. |
| **4** | 5.1a → 5.4, cheatsheet, 2 deneme sınavı | ~7 sa | İki denemede de 32+/40. Altındaysan sınav tarihini ertele. |

> 4. bölüm sınavın en ağır bölümü (11/40 puan) ve tek başına 390 dakikalık müfredat süresi var.
> Programda 3. haftanın tamamını ona ayırdık — sıkışırsan **4. bölümden kısma**, 2. ve 6. bölümden kıs.

---

## Ne satın alınmalı, ne alınmamalı

**Al:**
- **Sınav voucher'ı** — kendi ülkenin ISTQB üye kurulundan ya da onun yetkilendirdiği sınav sağlayıcısından.
  Türkiye için TTB (Turkish Testing Board) ve uluslararası sağlayıcılar (GASQ, Brightest, iSQI) seçenek.
  ⚠️ Fiyat, sınav dili ve online-proctored imkânı sağlayıcıya göre değişir — kayıt öncesi teyit et.

**Alma:**
- ❌ **Ücretli "resmî syllabus" veya "glossary" satan siteler.** İkisi de ISTQB'nin kendi sitesinde
  **ücretsiz ve PDF**. Para isteyen üçüncü taraf sitelere girme.
- ❌ **Akredite eğitim (~birkaç bin TL/EUR)** — CTFL için **zorunlu değil**, kendi kendine hazırlanıp
  girebilirsin. Bu setin var oluş sebebi bu.
- ❌ **"Gerçek sınav soruları" / "exam dump" satan siteler.** Hem ISTQB etik kurallarını ihlal eder
  (sertifikan iptal edilebilir) hem de sorular eski v3.1 müfredatından olduğu için seni yanlış çalıştırır.

Ayrıntı: [`00-baslangic/02-kayit-ve-satin-alma.md`](00-baslangic/02-kayit-ve-satin-alma.md)

---

## Klasör ağacı

```
ISTQB/
├── README.md                 ← buradasın
├── assets/                   ← quiz.css · quiz.js · validate.js (test motoru, elleme)
├── 00-baslangic/             ← sınav künyesi · kurulum · kayıt
├── 01-temeller/              ← Bölüm 1 (8 puan)
├── 02-yasam-dongusu/         ← Bölüm 2 (5 puan)
├── 03-statik-test/           ← Bölüm 3 (4 puan)
├── 04-analiz-tasarim/        ← Bölüm 4 (11 puan) ⭐
├── 05-yonetim/               ← Bölüm 5 (9 puan) ⭐
├── 06-araclar/               ← Bölüm 6 (3 puan)
└── 99-final/                 ← cheatsheet · son tekrar · 2 deneme sınavı
```

Testler tek dosyalık HTML. **Çift tıkla, tarayıcıda açılır.** Sunucu, internet, kurulum gerekmez.
Skorlar tarayıcının localStorage'ında tutulur — aynı tarayıcıyla devam edersen geçmiş denemelerin kalır.

---

## Değişebilecek bilgiler

Aşağıdakiler zamanla değişir; **sınav randevusu almadan önce resmî sayfadan teyit et:**
sınav ücreti · online proctoring imkânı · sınav dili seçenekleri · ek süre başvuru usulü ·
müfredat sürümü (şu an v4.0; ISTQB yeni sürüm çıkarırsa soru dağılımı değişebilir).

Resmî kaynak: <https://www.istqb.org/certifications/certified-tester-foundation-level-ctfl-v4-0/>
