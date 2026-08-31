# AWS Certified Cloud Practitioner (CLF-C02) — Sıfırdan Sertifikaya

> CLF-C02 sınavını **gerçekten öğrenerek** geçmek için hazırlanmış bir çalışma seti. Resmî sınav
> kılavuzundaki 4 alan ve 19 görev başlığının her biri ayrı bir dosya; her konunun sonunda
> tarayıcıda çalışan, skorlayan ve zayıf alt konularını söyleyen bir test var.

- **Sorular İngilizce** (sınav dili İngilizce, Türkçe seçeneği yok), **açıklamalar Türkçe**.
- Her konuda **gerçek AWS konsolunda yapılacak pratik** var — tıklanacak yer, girilecek değer,
  doğrulama ve **kaynağı silme** adımıyla.
- Ücret çıkarabilecek her adım `💸` ile işaretlidir.

---

## Nasıl çalışılır (kuralları bozma)

1. **Önce `00-baslangic/` klasörünü bitir.** Hesabın açık, bütçe alarmın kurulu, çok adımlı
   kimlik doğrulaman takılı olmadan konu çalışmaya başlama.
2. **Skor sunucusunu bir kez çalıştır** (`SKOR_PORT=8898 node assets/skor-sunucu.js`) — test skorların
   kendiliğinden bu dosyaya ve konu dosyalarına yazılsın. Ayrıntı: [Otomatik skor kaydı](#otomatik-skor-kaydı).
3. Konu dosyasını **baştan sona oku** — atlamadan. Ortalama 40–60 dakika sürer. Bir kısaltma ilk
   geçtiğinde yanında **parantez içinde kısa açıklaması** ve **↗ ile
   [kavram sözlüğüne](00-baslangic/03-kavram-sozlugu.md) bağlantısı** vardır.
4. Dosyadaki **pratiği yap**. Okumak %20, yapmak %80 kalıcılık demek. `💸` gördüğün her pratikte
   sonundaki **Temizlik** adımını atlama.
5. **"Kendini kontrol" sorularını kâğıda yaz.** Teste girmeden önce. Yazdıktan **sonra** o bölümün
   `cevaplar.md` dosyasını aç ve kendi cevabınla karşılaştır — her soru için kısa cevap, ayrıntı,
   sık yapılan hata ve konuya dönüş bağlantısı var.
6. Konu sonundaki **testi çöz**. Test dosyasına çift tıkla, tarayıcıda açılır.
7. **%80'in altındaysan bir sonraki konuya geçme.** Test sana hangi alt konuda zayıf olduğunu
   söyler; o başlığa dön, oku, testi tekrar çöz. Sorular ve şıklar her seferinde karışır,
   ezber çalışmaz.
8. **Takıldığın yeri sor.** Sorduğun her soru ayrıntılı cevabıyla birlikte
   [`soru-cevap.md`](soru-cevap.md)'ye yazılır — konu dosyalarına dağılmaz, tek yerde birikir.
9. Tüm konular bitince `99-final/` klasöründeki **65 soruluk deneme sınavlarına** geç.

> **Testi nasıl açarım?** Finder'da `.html` dosyasına çift tıkla — internet gerekmez.
> Terminal'den: `open 01-bulut-kavramlari/1.1-test.html`

> **Kavram sözlüğü:** [`00-baslangic/03-kavram-sozlugu.md`](00-baslangic/03-kavram-sozlugu.md) —
> kısaltmalar, uyumluluk standartları ve adından ne yaptığı anlaşılmayan servis adları.
> Baştan sona okunmak için değil, **dönmek için** var.

> **Seçim rehberi:** [`00-baslangic/04-secim-rehberi.md`](00-baslangic/04-secim-rehberi.md) —
> sınavın soru kalıbı hep aynıdır: *"bu senaryoda hangisi?"*. Bu dosya hesaplama, depolama,
> veritabanı, ağ, fiyat modeli, destek planı ve güvenlik seçimlerini **ne zaman ve nasıl**
> yapacağını anlatır; ayrıca **anahtar kelime → servis** tablosu içerir.

---

## Sınav künyesi (özet)

| | |
|---|---|
| Sınav kodu | **CLF-C02** |
| Soru | 65 (50'si puanlanır, 15'i deneme sorusudur) |
| Süre | 90 dakika (+30 dk ana dili İngilizce olmayanlar için ek süre — **satın almadan önce** talep et) |
| Geçme | **700 / 1000** (≈ %70) |
| Ücret | **100 USD** + ülke vergisi |
| Geçerlilik | 3 yıl |
| Yanlış cezası | **Yok** — boş bırakma, her soruyu işaretle |

⚠️ Ücret, süre ve politika değişebilir — randevu almadan önce
[resmî sayfadan teyit et](https://aws.amazon.com/certification/certified-cloud-practitioner/).
Detay: [`00-baslangic/00-sinav-kunyesi.md`](00-baslangic/00-sinav-kunyesi.md)

## Alan ağırlıkları — zamanını buna göre dağıt

| Alan | Ağırlık | Konu sayısı | Klasör |
|---|---|---|---|
| Bulut Kavramları | %24 | 4 | `01-bulut-kavramlari/` |
| Güvenlik ve Uyumluluk | %30 | 4 | `02-guvenlik-uyumluluk/` |
| Bulut Teknolojisi ve Servisleri | %34 | 8 | `03-teknoloji-servisler/` |
| Faturalama, Fiyatlandırma ve Destek | %12 | 3 | `04-faturalama-destek/` |

**İkinci ve üçüncü alan birlikte sınavın %64'ü.** Zaman sıkışırsa buradan kısma.

---

## İlerleme tablosu

Bitirdiğin kutuyu işaretle; skor hücresini skor sunucusu senin yerine doldurur.

### 00 — Başlangıç *(konu değil, kurulum · ~2 saat)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [00.1 Sınav künyesi ve strateji](00-baslangic/00-sinav-kunyesi.md) | — | — |
| [ ] | [00.2 AWS hesabı açma + güvenlik + bütçe alarmı](00-baslangic/01-aws-hesabi-kurulum.md) 💸 | — | — |
| [ ] | [00.3 Ne satın almalıyım? Kayıt ve sınav günü](00-baslangic/02-kayit-ve-satin-alma.md) | — | — |
| 📖 | [00.4 **Kavram sözlüğü** — kısaltmalar, standartlar, servis adları](00-baslangic/03-kavram-sozlugu.md) | başvuru | — |
| ⚙️ | [00.5 **Seçim rehberi** — hangi servis, hangi model, ne zaman](00-baslangic/04-secim-rehberi.md) | başvuru | — |
| ❓ | [**Soru & cevap defteri** — çalışırken sorduklarım](soru-cevap.md) | başvuru | — |

### 01 — Bulut Kavramları *(%24 · Hafta 1)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 AWS bulutunun faydaları](01-bulut-kavramlari/1.1-bulut-faydalari.md) | [test](01-bulut-kavramlari/1.1-test.html) | ___ |
| [ ] | [1.2 Well-Architected Framework](01-bulut-kavramlari/1.2-well-architected.md) | [test](01-bulut-kavramlari/1.2-test.html) | ___ |
| [ ] | [1.3 Buluta göç stratejileri](01-bulut-kavramlari/1.3-migration.md) | [test](01-bulut-kavramlari/1.3-test.html) | ___ |
| [ ] | [1.4 Bulut ekonomisi](01-bulut-kavramlari/1.4-bulut-ekonomisi.md) | [test](01-bulut-kavramlari/1.4-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](01-bulut-kavramlari/cevaplar.md) | cevap | — |

### 02 — Güvenlik ve Uyumluluk *(%30 · Hafta 2)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 Paylaşılan sorumluluk modeli](02-guvenlik-uyumluluk/2.1-paylasilan-sorumluluk.md) | [test](02-guvenlik-uyumluluk/2.1-test.html) | ___ |
| [ ] | [2.2 Yönetişim ve uyumluluk](02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md) | [test](02-guvenlik-uyumluluk/2.2-test.html) | ___ |
| [ ] | [2.3 Kimlik ve erişim yönetimi](02-guvenlik-uyumluluk/2.3-iam.md) | [test](02-guvenlik-uyumluluk/2.3-test.html) | ___ |
| [ ] | [2.4 Güvenlik servisleri](02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md) | [test](02-guvenlik-uyumluluk/2.4-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](02-guvenlik-uyumluluk/cevaplar.md) | cevap | — |

### 03 — Bulut Teknolojisi ve Servisleri *(%34 · Hafta 3)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [3.1 Dağıtım ve işletim yöntemleri](03-teknoloji-servisler/3.1-dagitim-isletim.md) | [test](03-teknoloji-servisler/3.1-test.html) | ___ |
| [ ] | [3.2 Global altyapı](03-teknoloji-servisler/3.2-global-altyapi.md) | [test](03-teknoloji-servisler/3.2-test.html) | ___ |
| [ ] | [3.3 Hesaplama (compute)](03-teknoloji-servisler/3.3-compute.md) | [test](03-teknoloji-servisler/3.3-test.html) | ___ |
| [ ] | [3.4 Veritabanları](03-teknoloji-servisler/3.4-veritabanlari.md) | [test](03-teknoloji-servisler/3.4-test.html) | ___ |
| [ ] | [3.5 Ağ (networking)](03-teknoloji-servisler/3.5-networking.md) | [test](03-teknoloji-servisler/3.5-test.html) | ___ |
| [ ] | [3.6 Depolama (storage)](03-teknoloji-servisler/3.6-storage.md) | [test](03-teknoloji-servisler/3.6-test.html) | ___ |
| [ ] | [3.7 Yapay zekâ ve analitik](03-teknoloji-servisler/3.7-ai-ml-analitik.md) | [test](03-teknoloji-servisler/3.7-test.html) | ___ |
| [ ] | [3.8 Diğer kapsam içi servisler](03-teknoloji-servisler/3.8-diger-servisler.md) | [test](03-teknoloji-servisler/3.8-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](03-teknoloji-servisler/cevaplar.md) | cevap | — |

### 04 — Faturalama, Fiyatlandırma ve Destek *(%12 · Hafta 4)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [4.1 Fiyatlandırma modelleri](04-faturalama-destek/4.1-fiyatlandirma.md) | [test](04-faturalama-destek/4.1-test.html) | ___ |
| [ ] | [4.2 Bütçe ve maliyet yönetimi](04-faturalama-destek/4.2-maliyet-yonetimi.md) | [test](04-faturalama-destek/4.2-test.html) | ___ |
| [ ] | [4.3 Destek planları ve kaynaklar](04-faturalama-destek/4.3-destek-planlari.md) | [test](04-faturalama-destek/4.3-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](04-faturalama-destek/cevaplar.md) | cevap | — |

### 99 — Final *(Hafta 4 sonu)*
| ✔ | Dosya | |
|---|---|---|
| [ ] | [Servis haritası — 100+ servis, tek cümlelik tanımlar](99-final/servis-haritasi.md) | — |
| [ ] | [Son tekrar — sınavdan 24 saat önce](99-final/son-tekrar.md) | — |
| [ ] | Deneme Sınavı 1 | [65 soru / 90 dk](99-final/final-sinav-1.html) · Skor: ___ |
| [ ] | Deneme Sınavı 2 | [65 soru / 90 dk](99-final/final-sinav-2.html) · Skor: ___ |
| [ ] | Her iki denemede de %80+ → **randevunu al** | |

---

## Otomatik skor kaydı

Test bittiği anda sonuç (tarih, skor, süre, zayıf alanlar) **kendiliğinden** iki yere yazılır:
konu dosyasının sonundaki *Test geçmişim* tablosuna ve yukarıdaki ilerleme tablosunun *Skor*
hücresine. Elle bir şey yazman gerekmez.

**Neden küçük bir sunucu gerekiyor:** Tarayıcı, güvenlik nedeniyle diske dosya **yazamaz** —
bu bir ayar değil, tarayıcının temel kuralı. Bu yüzden sonucu alıp `.md` dosyasına yazan
minik bir yerel süreç gerekiyor. Bağımlılığı yok, sadece Node:

```bash
SKOR_PORT=8898 node assets/skor-sunucu.js
```

Sonra testleri ister çift tıklayarak aç, ister tarayıcıda `http://localhost:8898/...` adresinden.
İkisi de çalışır.

> **Neden 8898?** Varsayılan port 8899'dur; KafkaAdvance seti onu kullanıyor. İki kursun sunucusu
> aynı anda açık olabilsin diye bu sete 8898 verildi — `assets/quiz.js` içindeki `SKOR_PORT`
> değeri de aynı. Tek kurs çalıştırıyorsan ikisini de 8899'a çevirebilirsin.
> Yanlış porttaki bir sunucuya sonuç giderse **sessizce kaybolmaz**: sunucu "bu sonuç başka bir
> kursa ait" diye açıkça reddeder.

| Durum | Ne olur |
|---|---|
| Sunucu **açık** | Test biter → sonuç anında `.md`'ye yazılır, sonuç ekranında "yazıldı" notu çıkar |
| Sunucu **kapalı** | Sonuç tarayıcıda **kuyruğa alınır**; sunucuyu açıp herhangi bir testi yenilediğinde bekleyenlerin hepsi yazılır. **Hiçbir skor kaybolmaz** |

**Hep açık olsun istersen (macOS, isteğe bağlı):**

```bash
sed "s|KURS_KOKU|$PWD|g" assets/skor-sunucu.plist > ~/Library/LaunchAgents/local.clfc02.skor.plist
launchctl load ~/Library/LaunchAgents/local.clfc02.skor.plist
```

Portu kalıcı yapmak istersen `plist` içindeki `EnvironmentVariables` bölümüne `SKOR_PORT` ekle.

Kaldırmak için `launchctl unload` + dosyayı sil.

> **Tablolar senin dosyalarında durur.** Elle satır ekleyebilir, not düşebilirsin;
> sunucu yalnızca `<!-- skor:baslangic -->` … `<!-- skor:bitis -->` arasına **yeni satır ekler**,
> var olanları silmez.

---

## 4 haftalık program (günde 1–1,5 saat)

| Hafta | Konular | Hafta sonu kontrolü |
|---|---|---|
| **1** | Başlangıç klasörü + 1.1–1.4 + 3.2 Global altyapı | "Elasticity ile scalability farkı ne?" sorusunu kâğıda bakmadan cevapla |
| **2** | 2.1–2.4'ün tamamı | Dört testin dördünde de %80+ |
| **3** | 3.1, 3.3–3.8 | Servis haritasından rastgele 20 servisi tek cümlede tanımla |
| **4** | 4.1–4.3 + 2 deneme sınavı + zayıf konu tekrarı | İki denemede de %80+ |

Acelen varsa (yazılım geliştiriyorsan) 2 haftaya sıkıştırılabilir: günde 3 saat, güvenlik ve
teknoloji alanlarına ağırlık ver.

---

## Ne satın almam gerekiyor?

| Kalem | Zorunlu mu | Fiyat |
|---|---|---|
| Sınav ücreti (Pearson VUE) | **Evet** | 100 USD + vergi |
| AWS hesabı | **Evet** (ücretsiz açılır) | 0 $ — ücretsiz kullanım sınırları içinde kal, bütçe alarmı kur |
| Bu çalışma seti | — | 0 $ |
| AWS Skill Builder — Cloud Practitioner Essentials | Hayır, ama faydalı | Ücretsiz |
| Üçüncü parti deneme sınavı seti | Hayır | ~15–20 USD |

**Tavsiye:** Bu seti bitir, iki deneme sınavını çöz. Hâlâ %80'in altındaysan tek bir ücretli
deneme seti al (ikisini birden alma). Sınav sorularını sızdıran sitelerden uzak dur — AWS
sertifikasyon anlaşmasını ihlal eder ve sertifikan iptal edilir.

Detay: [`00-baslangic/02-kayit-ve-satin-alma.md`](00-baslangic/02-kayit-ve-satin-alma.md)

---

## Klasör yapısı

```
AWSCertifiedCloudPractitioner/
├── README.md                  ← buradasın
├── soru-cevap.md              ← çalışırken sorduğun sorular + ayrıntılı cevapları
├── assets/                    ← testlerin ortak stili, motoru ve skor sunucusu (elleme)
├── 00-baslangic/              ← kurulum, kayıt, kavram sözlüğü, seçim rehberi
├── 01-bulut-kavramlari/       ← 4 konu + 4 test + cevaplar
├── 02-guvenlik-uyumluluk/     ← 4 konu + 4 test + cevaplar
├── 03-teknoloji-servisler/    ← 8 konu + 8 test + cevaplar
├── 04-faturalama-destek/      ← 3 konu + 3 test + cevaplar
└── 99-final/                  ← servis haritası, son tekrar, 2 deneme sınavı
```

Test skorları ayrıca tarayıcının yerel deposunda da tutulur — aynı tarayıcıda açtığın sürece
"en iyi skorun" testin başında görünür.
