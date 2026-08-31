# OCP 21 — Oracle Certified Professional: Java SE 21 Developer (1Z0-830)

Sıfırdan sertifikaya kadar götüren, **tek başına çalışılacak** bir kurs. Sohbet değil; disk üstünde duran,
haftalarca açıp kapatacağın bir set.

**Kime göre ayarlandı:** Java yazan ama bazı konuları unutmuş bir geliştirici. Değişken nedir anlatılmıyor;
**`int i = 'a' + 1;` neden derlenir, `Integer a = 127, b = 127; a == b` neden `true` ama 128'de `false`** anlatılıyor.
Sınav zaten bunları soruyor.

---

## Sınav künyesi

| | |
|---|---|
| **Sınav kodu** | 1Z0-830 |
| **Ünvan** | Oracle Certified Professional: Java SE 21 Developer |
| **Soru** | 50 çoktan seçmeli · ⚠️ Doğrulanmalı |
| **Süre** | 120 dakika · ⚠️ Doğrulanmalı |
| **Geçme notu** | %68 (≈ 34/50 doğru) · ⚠️ Doğrulanmalı |
| **Ücret** | ~245 USD (ülkeye göre değişir) · ⚠️ Doğrulanmalı |
| **Önkoşul** | Yok |
| **Geçerlilik** | Süresiz (Oracle Java sertifikaları expire olmaz) |

> ⚠️ Soru sayısı, süre, geçme notu ve ücret Oracle tarafından değiştirilebilir.
> **Randevu almadan önce** [resmî sınav sayfasından](https://education.oracle.com/java-se-21-developer-professional/pexam_1Z0-830)
> teyit et. Detaylar: [`00-baslangic/00-sinav-kunyesi.md`](00-baslangic/00-sinav-kunyesi.md)

---

> **Terim tanımadıysan** [kavram sözlüğüne](00-baslangic/03-kavram-sozlugu.md) bak —
> 43 terim, her biri sınavda göreceğin İngilizce karşılığıyla.

---

## Nasıl çalışılır

1. **Önce `00-baslangic/` klasörünü bitir.** Sınavın nasıl düşündüğünü öğrenmeden konuya girme —
   aynı bilgiyle %10 daha fazla puan alırsın.
2. **Sırayla git.** Konular birbirine bağlı: pattern matching'i (2.1) bilmeden sealed types (3.5) eksik kalır.
3. **Bir konuyu okurken kod dosyalarını aç ve çalıştır.** Her konu klasöründe `ornekler/` var.
   Kodu okuyup "herhalde şunu yazar" deme — **çalıştır**, sonra tahminini kontrol et. Sınavda kaybettiren şey
   tam olarak bu: kafandaki Java ile gerçek Java arasındaki fark.
4. **Testi konuyu bitirir bitirmez çöz.** Ertesi güne bırakma; amaç hafıza değil, anlamayı ölçmek.
5. **%80 altındaysan tekrar et.** Test bitince "zayıf alanlar" raporu çıkar — o alt başlıkları dosyadan
   tekrar oku, testi **karıştırılmış** olarak yeniden çöz.
6. **Her hafta sonunda önceki haftanın testlerini tekrar çöz.** Skorlar tarayıcıda saklanır.
7. **Son iki hafta deneme sınavı haftasıdır.** `99-final/` içindeki denemeleri **süre tutarak, tek oturumda**,
   telefonsuz çöz. 45 dakikada bitirdiysen dikkatli okumamışsındır.

---

## İlerleme tablosu

Bitirdikçe kutucuğu doldur. (`- [ ]` → `- [x]`)

### 00 — Başlangıç
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Sınav künyesi, formatı ve trickler](00-baslangic/00-sinav-kunyesi.md) | — | — |
| [ ] | [Ortam kurulumu ve çalışma projesi](00-baslangic/01-kurulum.md) | — | — |
| [ ] | [Kayıt, satın alma ve sınav günü](00-baslangic/02-kayit-ve-satin-alma.md) | — | — |
| 📖 | [Kavram sözlüğü](00-baslangic/03-kavram-sozlugu.md) | başvuru | — |
| ❓ | [Soru & cevap defteri](soru-cevap.md) | başvuru | — |

### 01 — Temel Tipler, Metin ve Tarih/Saat
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Primitifler, wrapper'lar, operatörler ve tip dönüşümleri](01-temel-tipler/1.1-primitifler-ve-operatorler.md) | [test](01-temel-tipler/1.1-test.html) | — |
| [ ] | [1.2 String, StringBuilder ve text block'lar](01-temel-tipler/1.2-string-ve-stringbuilder.md) | [test](01-temel-tipler/1.2-test.html) | — |
| [ ] | [1.3 Date-Time API](01-temel-tipler/1.3-date-time-api.md) | [test](01-temel-tipler/1.3-test.html) | — |

### 02 — Akış Kontrolü
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 if/else, switch ve pattern matching](02-akis-kontrolu/2.1-if-switch-ve-pattern-matching.md) | [test](02-akis-kontrolu/2.1-test.html) | — |
| [ ] | [2.2 Döngüler, break ve continue](02-akis-kontrolu/2.2-donguler.md) | [test](02-akis-kontrolu/2.2-test.html) | — |

### 03 — Nesne Yönelimli Java
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [3.1 Sınıflar, nesne yaşam döngüsü ve başlatma sırası](03-nesne-yonelimli/3.1-siniflar-ve-yasam-dongusu.md) | [test](03-nesne-yonelimli/3.1-test.html) | — |
| [ ] | [3.2 Metotlar, kapsam, `var` ve immutability](03-nesne-yonelimli/3.2-metotlar-ve-kapsam.md) | [test](03-nesne-yonelimli/3.2-test.html) | — |
| [ ] | [3.3 Record'lar](03-nesne-yonelimli/3.3-recordlar.md) | [test](03-nesne-yonelimli/3.3-test.html) | — |
| [ ] | [3.4 Kalıtım, override ve polimorfizm](03-nesne-yonelimli/3.4-kalitim-ve-polimorfizm.md) | [test](03-nesne-yonelimli/3.4-test.html) | — |
| [ ] | [3.5 Abstract sınıflar ve sealed tipler](03-nesne-yonelimli/3.5-abstract-ve-sealed.md) | [test](03-nesne-yonelimli/3.5-test.html) | — |
| [ ] | [3.6 Arayüzler ve fonksiyonel arayüzler](03-nesne-yonelimli/3.6-arayuzler.md) | [test](03-nesne-yonelimli/3.6-test.html) | — |
| [ ] | [3.7 Enum'lar](03-nesne-yonelimli/3.7-enumlar.md) | [test](03-nesne-yonelimli/3.7-test.html) | — |
| [ ] | [3.8 İç sınıflar (nested classes)](03-nesne-yonelimli/3.8-ic-siniflar.md) | [test](03-nesne-yonelimli/3.8-test.html) | — |

### 04 — İstisnalar
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [4.1 Exception temelleri, try/catch/finally, multi-catch](04-istisnalar/4.1-exception-temelleri.md) | [test](04-istisnalar/4.1-test.html) | — |
| [ ] | [4.2 try-with-resources ve custom exception'lar](04-istisnalar/4.2-try-with-resources.md) | [test](04-istisnalar/4.2-test.html) | — |

### 05 — Diziler ve Koleksiyonlar
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [5.1 Diziler ve `Arrays` API](05-koleksiyonlar/5.1-diziler.md) | [test](05-koleksiyonlar/5.1-test.html) | — |
| [ ] | [5.2 List, Set, Map, Deque ve generics](05-koleksiyonlar/5.2-collections-api.md) | [test](05-koleksiyonlar/5.2-test.html) | — |
| [ ] | [5.3 Sıralama ve Sequenced Collections (Java 21)](05-koleksiyonlar/5.3-siralama-ve-sequenced.md) | [test](05-koleksiyonlar/5.3-test.html) | — |

### 06 — Lambda ve Streams
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [6.1 Lambda, fonksiyonel arayüzler ve method reference](06-lambda-ve-streams/6.1-lambda-ve-fonksiyonel-arayuzler.md) | [test](06-lambda-ve-streams/6.1-test.html) | — |
| [ ] | [6.2 Stream oluşturma, ara işlemler ve Optional](06-lambda-ve-streams/6.2-stream-ve-ara-islemler.md) | [test](06-lambda-ve-streams/6.2-test.html) | — |
| [ ] | [6.3 Terminal işlemler, reduction ve Collectors](06-lambda-ve-streams/6.3-terminal-ve-collectors.md) | [test](06-lambda-ve-streams/6.3-test.html) | — |
| [ ] | [6.4 Primitif ve paralel stream'ler](06-lambda-ve-streams/6.4-primitif-ve-paralel.md) | [test](06-lambda-ve-streams/6.4-test.html) | — |

### 07 — Modüller ve Paketleme
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [7.1 Modül tanımlama, exports/requires, servisler](07-moduller/7.1-modul-tanimlama.md) | [test](07-moduller/7.1-test.html) | — |
| [ ] | [7.2 Derleme, jar, jlink ve modüle geçiş](07-moduller/7.2-derleme-ve-migrasyon.md) | [test](07-moduller/7.2-test.html) | — |

### 08 — Eşzamanlılık
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [8.1 Thread'ler ve virtual thread'ler](08-eszamanlilik/8.1-threadler-ve-virtual-threads.md) | [test](08-eszamanlilik/8.1-test.html) | — |
| [ ] | [8.2 ExecutorService, Callable ve Future](08-eszamanlilik/8.2-executor-service.md) | [test](08-eszamanlilik/8.2-test.html) | — |
| [ ] | [8.3 Thread-safe kod: kilitler, atomic ve concurrent koleksiyonlar](08-eszamanlilik/8.3-thread-safety.md) | [test](08-eszamanlilik/8.3-test.html) | — |

### 09 — Java I/O
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [9.1 I/O stream'leri, konsol ve serialization](09-io/9.1-io-streams-ve-serialization.md) | [test](09-io/9.1-test.html) | — |
| [ ] | [9.2 NIO.2: Path ve Files](09-io/9.2-nio2-path-ve-files.md) | [test](09-io/9.2-test.html) | — |

### 10 — Yerelleştirme
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [10.1 Locale, ResourceBundle ve biçimlendirme](10-yerellestirme/10.1-locale-ve-bicimlendirme.md) | [test](10-yerellestirme/10.1-test.html) | — |

### 99 — Final
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet](99-final/cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar (sınavdan 24 saat önce)](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Deneme Sınavı 1 · 50 soru · 120 dk | [test](99-final/deneme-1.html) | — |
| [ ] | Deneme Sınavı 2 · 50 soru · 120 dk | [test](99-final/deneme-2.html) | — |

---

## 8 haftalık program

Haftada ~6-8 saat varsayılmıştır. Hafta içi iki akşam okuma+test, hafta sonu bir blok pratik.

| Hafta | Konular | Odak |
|---|---|---|
| **1** | `00-baslangic` tamamı + 1.1, 1.2 | Ortamı kur, sınav mantığını öğren. Tip dönüşümü ve String tuzakları — burası en çok "kolay sanıp kaybedilen" yer. |
| **2** | 1.3, 2.1, 2.2 | Date-Time API'de immutability refleksi. `switch` pattern matching Java 21'in yeni sorusu, ezberleme — kuralını çıkar. |
| **3** | 3.1, 3.2, 3.3, 3.4 | Başlatma sırası (initializer order) ve overload çözümleme. Kâğıda çıktı tahmini yaz, sonra çalıştır. |
| **4** | 3.5, 3.6, 3.7, 3.8 | sealed + record + pattern matching üçlüsü birlikte sorulur. 3.5 bitince 2.1'e geri dön. |
| **5** | 4.1, 4.2, 5.1, 5.2, 5.3 | try-with-resources kapanma sırası; Sequenced Collections Java 21'e özgü, kesin çıkar. |
| **6** | 6.1, 6.2, 6.3, 6.4 | Stream'ler en yoğun alan. Her gün en az bir tam pipeline yaz. `Collectors` imzalarını ezberle. |
| **7** | 7.1, 7.2, 8.1, 8.2, 8.3 | Modüller ve virtual thread'ler. Bunlar günlük işte yazmadığın konular — pratik yapmadan geçme. |
| **8** | 9.1, 9.2, 10.1 + `99-final` | Cheatsheet + Deneme 1 (hafta ortası) → zayıf konulara dön → Deneme 2 (sınavdan 2-3 gün önce). |

**Sınav randevusunu 8. haftanın sonuna al.** Deneme 1'de %70+ alamadıysan randevuyu 1-2 hafta ertele —
ücret iadesi yok, erteleme ücretsiz (⚠️ politikayı teyit et).

---

## Ne satın alınmalı / alınmamalı

| | |
|---|---|
| ✅ **Sınav voucher'ı** — sadece Oracle üzerinden. Üçüncü parti "indirimli voucher" satan sitelerden alma. |
| ✅ **Enthuware 1Z0-830 mock testleri** (~10 USD) — sertifikaya hazırlananların standardı. Gerçek sınav zorluğuna en yakın kaynak. Bu kursu bitirdikten sonra al. |
| ✅ **Sybex / Boyarsky-Selikoff "OCP Java SE 21 Developer Complete Study Guide"** — derinleşmek istersen. Zorunlu değil. |
| ❌ **"Exam dumps" / "gerçek sorular" satan siteler** — Oracle politika ihlali, sertifikan iptal edilir. Ayrıca içerikleri genelde eski sınav sürümüne ait ve yanlış. |
| ❌ **Pahalı video kursları** — bu sette olmayan bir şey vermiyorlar. Parayı mock teste harca. |
| ❌ **Sınav sigortası / retake paketi** — ilk denemede geçecek kadar hazırlanmak daha ucuz. |

---

## Otomatik skor kaydı

Tarayıcı güvenlik nedeniyle diske **yazamaz**. Test sonuçlarının ilerleme
tablosuna kendiliğinden düşmesi için küçük bir yerel süreç çalıştırman gerekiyor:

```bash
cd /yol/OCP21 && node assets/skor-sunucu.js
```

Sonra testleri `http://localhost:8891/01-temel-tipler/1.1-test.html` gibi açabilirsin —
ya da dosyaya çift tıklamaya devam et, ikisi de çalışır.

> ⚠️ **Port neden 8891?** Skor sunucusunun genel varsayılanı 8899. Aynı anda
> başka bir kurs seti açıksa portu kapar, bu sunucu hiç açılmaz ve skorların
> **sessizce** kaydedilmez. Bu depodaki her kursa ayrı port verildi.

**Sunucu kapalıyken ne olur:** hiçbir şey kaybolmaz. Sonuç tarayıcının belleğinde
kuyruğa alınır, sunucuyu açıp herhangi bir testi yeniden açtığında gönderilir.

macOS'ta hep açık tutmak istersen: `assets/skor-sunucu.plist` dosyasındaki yolu
düzenleyip `~/Library/LaunchAgents/` altına kopyala, sonra
`launchctl load ~/Library/LaunchAgents/local.ocp21.skor.plist`.

---

## Klasör yapısı

```
OCP21/
├── README.md               ← buradasın
├── assets/                 ← test motoru (elleme)
├── 00-baslangic/           ← sınav künyesi · kurulum · kayıt
├── 01-temel-tipler/ … 10-yerellestirme/
│   ├── <kod>-<konu>.md     ← konu anlatımı
│   ├── <kod>-test.html     ← çift tıkla, tarayıcıda çöz
│   └── ornekler/           ← çalıştırılabilir .java dosyaları
└── 99-final/               ← cheatsheet · son tekrar · 2 deneme sınavı
```

**Testler internet gerektirmez.** `.html` dosyasına çift tıkla, tarayıcıda açılır. Skorlar tarayıcının
`localStorage`'ında saklanır — aynı tarayıcıyı kullanmaya devam et.

---

## Setin iki kuralı

**1. `*Hatalari.java` dosyaları kasten derlenmez.**
Her konu klasöründe bir tane var. İçlerindeki her hata bir sınav sorusudur; yorumlarda **neden**
hata verdiği ve **nasıl** düzeltileceği yazıyor. Görevin hataları **teker teker** düzeltip her
seferinde yeniden derlemek. Derleyici bazı hataları ancak öncekiler temizlenince gösterir —
bu normaldir, "hepsi çıkmadı" diye şaşırma.

**2. Diğer tüm `.java` dosyaları çalışır ve çıktıları doğrulanmıştır.**
Bu setteki her örnek JDK 21 ile **gerçekten çalıştırılıp** çıktısı kontrol edilmiştir. Konu
dosyalarında yazan çıktılar tahmin değil, **ölçüm**. Sende farklı bir sonuç çıkıyorsa önce
`java -version` kontrol et.

> **Test motoru hakkında:** `assets/quiz.js` ve `quiz.css` bu kursa özel olarak, soru metninde
> **Java kod bloğu** gösterebilecek şekilde küçük bir düzenleme aldı. Bu dosyalara dokunma —
> testlerin tamamı onlara bağlı.
