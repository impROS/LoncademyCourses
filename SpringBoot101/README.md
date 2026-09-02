# Spring Boot 101 — Çekirdek: Spring nasıl çalışır

> Çoğu kişi Spring Boot'u ezberleyerek kullanır: "buraya `@Service` yazılır, çalışır."
> Sonra bir gün çalışmaz — ve elde tutunacak hiçbir şey kalmaz. Bu kurs ezber vermez,
> **mekanizma** verir: nesneleri kim kuruyor, o anotasyon aslında ne yapıyor, Spring
> senin yazmadığın yapılandırmayı nereden buluyor ve bunu nasıl kendi gözünle görürsün.
>
> Anlatım Türkçe; kod, anotasyon adları, ayar anahtarları ve hata mesajları kendi dilinde
> kalır — ekranda onları öyle göreceksin.

**Sürüm:** Spring Boot **4.1.1** (Ağustos 2026) · Spring Framework 7 · Java 21
**Süre:** ~3 hafta · haftada 4–6 saat · **19 konu** · 18 test
**Ön koşul:** Java bilmek — sınıf, arayüz, `interface`, generics, lambda. Spring bilgisi **gerekmez**.
**Laboratuvar:** Kendi elinle kuracağın tek bir Maven projesi, kurs boyunca büyüyor.

> ⚠️ **Neden 4.1 ve neden bu önemli:** Spring Boot 4.0 (Kasım 2025) yıllardır görülen en
> büyük kırılmayı getirdi — Jackson 2 → 3, modül bölünmesi, test anotasyonlarının
> değişmesi. İşteki projen büyük ihtimalle hâlâ Spring Boot 3.x'tir. Bu yüzden her konuda
> **"Spring Boot 3.x'te bu şöyleydi"** kutusu var: yeni doğruyu öğrenirsin, eski kodu da
> tanırsın.

---

## 📚 Bu kurs dört kurstan biri

| Kurs | Ne öğretir | Giriş şartı |
|---|---|---|
| **101 Çekirdek** *(buradasın)* | Container, bean, dependency injection, anotasyonlar, yapılandırma, web, test temelleri | Java bilmek |
| **201 Veri ve transaction** | Hibernate mekaniği, `@Transactional`'ın içi, kilitleme, ileri test | 101 finalinden %80 |
| **301 Dayanıklılık ve desenler** | AOP, tasarım desenleri, retry, outbox pattern, cache, async | 201 finalinden %80 |
| **401 Mikroservis ve üretim** | Servis sınırları, event-driven mimari, observability, güvenlik, dağıtım | 301 finalinden %80 |

**Bu kurs şunları öğretmez** — unutulduğu için değil, sırası gelmediği için:
transaction yönetimi (**201**), AOP ve kendi aspect'in (**301**), mikroservisler ve
Kafka (**401**). Burada onların üstüne bina edilecek zemini atıyoruz.

---

## Nasıl çalışılır

1. **Sırayla git.** Her konu bir öncekinin üstüne biniyor; 2.2'yi 1.2 olmadan anlamazsın.
2. **Önce oku, sonra pratiği yap, en son teste gir.** Test, konu dosyasındaki bilgiyle
   çözülür; dışarıdan bir şey gerekmez.
3. **Pratikleri gerçekten yaz.** Bu kursun pratikleri okunacak metin değil, çalıştırılacak
   koddur. Özellikle "yanlış hâli" kutularını **kendi elinle bozup** hatayı gör — o hata
   mesajını bir kez gördüysen, üretimde tanırsın.
4. **Kendini kontrol sorularının cevabını önce kâğıda yaz**, sonra o bölümün
   `cevaplar.md` dosyasını aç. Göz kayarsa düşünme adımı atlanır.
5. **%80 altında kaldıysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.
6. **Aklına takılanı [`soru-cevap.md`](soru-cevap.md)'ye yaz.** Kurs boyunca büyüyen defterin.
7. **Terim tanımadıysan** [kavram sözlüğüne](00-baslangic/03-kavram-sozlugu.md) bak;
   bir ayarın değerini seçerken [ayar rehberine](00-baslangic/04-ayar-rehberi.md).
8. **Skor sunucusunu açık tut** (aşağıda) — test sonuçların bu tabloya kendiliğinden düşer.
9. **Bitirmeden önce [`99-final/cheatsheet.md`](99-final/cheatsheet.md)'yi tara.**

---

## İlerleme tablosu

### 00 — Başlangıç

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [00.1 Spring Boot ne yapıyor — ve nerede kaybediliyor](00-baslangic/01-genel-bakis.md) | [test](00-baslangic/01-test.html) | — |
| [ ] | [00.2 Ortam kurulumu ve laboratuvar projesi](00-baslangic/02-kurulum.md) | — | — |
| 📖 | [Kavram sözlüğü](00-baslangic/03-kavram-sozlugu.md) | başvuru | — |
| ⚙️ | [Ayar rehberi](00-baslangic/04-ayar-rehberi.md) | başvuru | — |
| 💡 | [Kendini kontrol cevapları](00-baslangic/cevaplar.md) | cevap | — |
| ❓ | [Soru & cevap defteri](soru-cevap.md) | başvuru | — |

### 01 — Container ve bean'ler

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Container: nesneleri kim kuruyor](01-container-ve-bean/1.1-container-nedir.md) | [test](01-container-ve-bean/1.1-test.html) | — |
| [ ] | [1.2 Bean tanımlamanın iki yolu](01-container-ve-bean/1.2-bean-tanimlama.md) | [test](01-container-ve-bean/1.2-test.html) | — |
| [ ] | [1.3 Dependency injection ve belirsizlik](01-container-ve-bean/1.3-dependency-injection.md) | [test](01-container-ve-bean/1.3-test.html) | — |
| [ ] | [1.4 Bean lifecycle](01-container-ve-bean/1.4-bean-lifecycle.md) | [test](01-container-ve-bean/1.4-test.html) | — |
| [ ] | [1.5 Scope ve scoped proxy](01-container-ve-bean/1.5-scope.md) | [test](01-container-ve-bean/1.5-test.html) | — |
| 💡 | [Kendini kontrol cevapları](01-container-ve-bean/cevaplar.md) | cevap | — |

### 02 — Anotasyon haritası

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 Stereotype'lar: `@Service` gerçekten ne yapar](02-anotasyon-haritasi/2.1-stereotype.md) | [test](02-anotasyon-haritasi/2.1-test.html) | — |
| [ ] | [2.2 `@Configuration` ve gizli proxy](02-anotasyon-haritasi/2.2-configuration.md) | [test](02-anotasyon-haritasi/2.2-test.html) | — |
| [ ] | [2.3 Auto-configuration'ı teşhis etmek](02-anotasyon-haritasi/2.3-auto-configuration.md) | [test](02-anotasyon-haritasi/2.3-test.html) | — |
| [ ] | [2.4 Starter'lar ve Spring Boot 4 modül bölünmesi](02-anotasyon-haritasi/2.4-starterlar.md) | [test](02-anotasyon-haritasi/2.4-test.html) | — |
| 💡 | [Kendini kontrol cevapları](02-anotasyon-haritasi/cevaplar.md) | cevap | — |

### 03 — Yapılandırma

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [3.1 Ayar nereden geliyor: öncelik sırası](03-yapilandirma/3.1-oncelik-sirasi.md) | [test](03-yapilandirma/3.1-test.html) | — |
| [ ] | [3.2 `@ConfigurationProperties` ve doğrulama](03-yapilandirma/3.2-configuration-properties.md) | [test](03-yapilandirma/3.2-test.html) | — |
| [ ] | [3.3 Profiller](03-yapilandirma/3.3-profiller.md) | [test](03-yapilandirma/3.3-test.html) | — |
| 💡 | [Kendini kontrol cevapları](03-yapilandirma/cevaplar.md) | cevap | — |

### 04 — Web katmanı

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [4.1 Request mapping ve body binding](04-web-katmani/4.1-request-mapping.md) | [test](04-web-katmani/4.1-test.html) | — |
| [ ] | [4.2 Exception handling ve `ProblemDetail`](04-web-katmani/4.2-exception-handling.md) | [test](04-web-katmani/4.2-test.html) | — |
| [ ] | [4.3 `RestClient` ile dışarıya çağrı](04-web-katmani/4.3-restclient.md) | [test](04-web-katmani/4.3-test.html) | — |
| 💡 | [Kendini kontrol cevapları](04-web-katmani/cevaplar.md) | cevap | — |

### 05 — Test temelleri

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [5.1 Test piramidi ve slice test'ler](05-test-temelleri/5.1-slice-test.md) | [test](05-test-temelleri/5.1-test.html) | — |
| [ ] | [5.2 `@MockitoBean` ve Spring Boot 4 test değişiklikleri](05-test-temelleri/5.2-mockitobean.md) | [test](05-test-temelleri/5.2-test.html) | — |
| 💡 | [Kendini kontrol cevapları](05-test-temelleri/cevaplar.md) | cevap | — |

### 99 — Final

| ✔ | Dosya | Test | Skor |
|---|---|---|---|
| 📄 | [Cheatsheet](99-final/cheatsheet.md) | başvuru | — |
| 📄 | [Son tekrar](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Genel deneme sınavı 1 | [sınav](99-final/genel-sinav-1.html) | — |
| [ ] | Genel deneme sınavı 2 | [sınav](99-final/genel-sinav-2.html) | — |

---

## Haftalık program

| Hafta | Konular | Hedef |
|---|---|---|
| **1** | 00.1 → 1.5 | Container'ı anladın: bir nesnenin nereden geldiğini ve ne zaman kurulduğunu söyleyebiliyorsun |
| **2** | 2.1 → 3.3 | Anotasyonu okuyunca ne yapacağını biliyorsun; bir ayarın nereden geldiğini kanıtlayabiliyorsun |
| **3** | 4.1 → 5.2 + final | Uçtan uca bir uç nokta (endpoint) yazıp testini kurabiliyorsun |

Haftada 4–6 saat: ~3 saat okuma + ~2 saat laboratuvar.

---

## Ne satın alınmalı

**Hiçbir şey.** Bu kursun tamamı ücretsiz araçlarla yapılır:

| Araç | Durum |
|---|---|
| JDK 21 (Temurin / Corretto / JBR) | Ücretsiz |
| Maven 3.9+ | Ücretsiz |
| IntelliJ IDEA **Community** | Ücretsiz — bu kursun tamamı Community ile yapılabilir |
| H2 gömülü veritabanı | Ücretsiz, kurulum gerektirmez |

💸 **Ücret çıkmaz.** Bulut hesabı, lisans, abonelik yok. Her şey kendi makinende çalışır.

> IntelliJ **Ultimate**'ın Spring desteği (bean grafiği, uç nokta listesi) rahattır ama
> **gerekmez** — kursta her şeyi Actuator ve günlük (log) çıktısıyla görüyoruz, çünkü
> üretimde elinde IDE olmayacak.

---

## Otomatik skor kaydı

Testi bitirdiğinde skorun bu README'deki tabloya ve konu dosyasının altına
**kendiliğinden** yazılsın istiyorsan küçük bir yerel sunucu çalıştırman gerekiyor.

**Neden gerekiyor:** Tarayıcı, güvenlik nedeniyle diskteki dosyalara **yazamaz**. Test
sayfası skoru hesaplar ama kaydedemez. Araya giren bu küçük süreç, tarayıcının
gönderdiği sonucu alıp dosyaya yazar. Tek seferlik bir komut:

```bash
node ~/IdeaProjects/impROS/LoncademyCourses/SpringBoot101/assets/skor-sunucu.js
```

- Bu kurs **8888** portunu kullanır (diğer Loncademy kursları başka portlarda — çakışmaz).
- **Sunucu kapalıyken de test çözebilirsin:** sonuç tarayıcıda saklanır, sunucuyu bir
  sonraki açışında kendiliğinden gönderilir. Hiçbir skor kaybolmaz.
- Sürekli açık kalsın istersen: `assets/skor-sunucu.plist` dosyasını
  `~/Library/LaunchAgents/` altına kopyala ve `launchctl load` ile yükle.

---

## Klasör ağacı

```
SpringBoot101/
├── README.md                    ← buradasın
├── soru-cevap.md                ← sorduğun sorular + cevapları
├── assets/                      ← test motoru ve skor sunucusu (elleme)
├── 00-baslangic/                ← genel bakış · kurulum · sözlük · ayar rehberi
├── 01-container-ve-bean/        ← 5 konu: container, bean, dependency injection, lifecycle, scope
├── 02-anotasyon-haritasi/       ← 4 konu: stereotype'lar, @Configuration, auto-configuration, starter
├── 03-yapilandirma/             ← 3 konu: öncelik, @ConfigurationProperties, profiller
├── 04-web-katmani/              ← 3 konu: request mapping, exception handling, RestClient
├── 05-test-temelleri/           ← 2 konu: slice test'ler, @MockitoBean
└── 99-final/                    ← cheatsheet · son tekrar · 2 genel sınav
```

---

➡️ **Başla:** [00.1 Spring Boot ne yapıyor — ve nerede kaybediliyor](00-baslangic/01-genel-bakis.md)
