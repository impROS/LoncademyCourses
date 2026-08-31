# Vibe Coding 101 — Temeller

> Kodu artık çoğunlukla sen yazmıyorsun; yazdırıyorsun ve denetliyorsun. Bu kurs o
> işin **fiziğini** öğretir: ajan nasıl çalışır, bağlam neden en kıt kaynağındır,
> belirsizliği nereden alırsın, yetkiyi nasıl ayarlarsın, ne zaman "bitti"
> dersin ve projenin kurallarını nereye yazarsın.
>
> Anlatım Türkçe; komutlar, dosya adları, ayar adları ve istem (prompt) örnekleri kendi
> dilinde kalır — çünkü ekranda onları öyle göreceksin.

**Süre:** ~1 hafta · günde ~1 saat · **12 konu** · 11 test · 176 soru
**Ön koşul:** Bir dilde kod okuyup yazabiliyor olmak. Terminal ve `git` temelleri.
**Ana araç:** Claude Code (omurga) · diğer araçlar karşılaştırmalı

> 📚 **Bu kurs dört kurstan biri.** Sıra: **101 Temeller** → **201 Günlük iş
> akışları** → **301 Kendi aletini yapmak** → **401 Üretim ve ekip.**
> Her kursun kendi icazeti var.

---

## Nasıl çalışılır

1. **Sırayla git.** Konular birbirinin üstüne biniyor.
2. **Önce oku, sonra pratiği yap, en son teste gir.** Test, konu dosyasındaki bilgiyle
   çözülebilir; dışarıdan bir şey gerekmez.
3. **Pratikleri gerçekten yap.** Bu kursun pratikleri okunacak metin değil, çalıştırılacak
   komutlardır. Ajanla çalışmak, okuyarak değil deneyerek oturuyor.
4. **Kendini kontrol sorularının cevabını önce kâğıda yaz**, sonra o bölümün
   `cevaplar.md` dosyasını aç. Göz kayarsa düşünme adımı atlanır.
5. **%80 altında kaldıysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.
6. **Aklına takılanı [`soru-cevap.md`](soru-cevap.md)'ye yaz.** Kurs boyunca büyüyen defterin.
7. **Terim tanımadıysan** [kavram sözlüğüne](00-baslangic/03-kavram-sozlugu.md) bak;
   ayar ya da karar seçerken [seçim ve ayar rehberine](00-baslangic/04-ayar-rehberi.md).
8. **Skor sunucusunu açık tut** (aşağıya bak) — test sonuçların bu tabloya kendiliğinden düşer.
9. **Bitirmeden önce `99-final/cheatsheet.md`'yi tara.** Unuttuğun yeri hemen görürsün.

> ⚠️ **Bu kursun bir tarafı hızla eskir.** Araç sürümleri, fiyatlar, model adları ve
> özellik listeleri aylık değişiyor. Her konu dosyasında **kalıcı fikir** ile
> **bugünkü ayrıntı** ayrı işaretlendi. `⚠️ Doğrulanmalı` gördüğün yerde resmî
> dokümana bak — adres orada yazıyor.

---

## İlerleme tablosu

### 00 — Başlangıç

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [00.1 Vibe coding nedir, ne değildir — ve trickler](00-baslangic/01-genel-bakis-ve-trickler.md) | [test](00-baslangic/01-test.html) | — |
| [ ] | [00.2 Ortam kurulumu ve örnek proje](00-baslangic/02-kurulum.md) | — | — |
| 📖 | [Kavram sözlüğü](00-baslangic/03-kavram-sozlugu.md) | başvuru | — |
| ⚙️ | [Seçim ve ayar rehberi](00-baslangic/04-ayar-rehberi.md) | başvuru | — |
| [ ] | [00.3 Plan, kota ve maliyet: ne satın alınmalı](00-baslangic/05-plan-ve-maliyet.md) | — | — |
| 💡 | [Kendini kontrol cevapları](00-baslangic/cevaplar.md) | cevap | — |
| ❓ | [Soru & cevap defteri](soru-cevap.md) | başvuru | — |

### 01 — Ajanla çalışmanın temelleri

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Ajan döngüsü: bir istek ne oluyor da koda dönüşüyor](01-ajanla-calismak/1.1-ajan-dongusu.md) | [test](01-ajanla-calismak/1.1-test.html) | — |
| [ ] | [1.2 İstem yazma: belirsizliği nereden alırsın](01-ajanla-calismak/1.2-istem-yazma.md) | [test](01-ajanla-calismak/1.2-test.html) | — |
| [ ] | [1.3 Bağlam penceresi: en kıt kaynağın](01-ajanla-calismak/1.3-baglam-yonetimi.md) | [test](01-ajanla-calismak/1.3-test.html) | — |
| [ ] | [1.4 İzinler, plan modu ve kum havuzu](01-ajanla-calismak/1.4-izinler-ve-plan-modu.md) | [test](01-ajanla-calismak/1.4-test.html) | — |
| [ ] | [1.5 Doğrulama refleksi: ajana kapatabileceği bir döngü ver](01-ajanla-calismak/1.5-dogrulama-refleksi.md) | [test](01-ajanla-calismak/1.5-test.html) | — |
| 💡 | [Kendini kontrol cevapları](01-ajanla-calismak/cevaplar.md) | cevap | — |

### 02 — Projeye kural yazmak

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 CLAUDE.md: her oturuma giren bağlam](02-projeye-kural-yazmak/2.1-claude-md.md) | [test](02-projeye-kural-yazmak/2.1-test.html) | — |
| [ ] | [2.2 Kural dosyaları ve yola göre kapsam](02-projeye-kural-yazmak/2.2-kural-dosyalari.md) | [test](02-projeye-kural-yazmak/2.2-test.html) | — |
| [ ] | [2.3 AGENTS.md ve araçlar arası taşınabilirlik](02-projeye-kural-yazmak/2.3-agents-md.md) | [test](02-projeye-kural-yazmak/2.3-test.html) | — |
| [ ] | [2.4 Kalıcı bellek: oturumlar arasında ne taşınır](02-projeye-kural-yazmak/2.4-kalici-bellek.md) | [test](02-projeye-kural-yazmak/2.4-test.html) | — |
| 💡 | [Kendini kontrol cevapları](02-projeye-kural-yazmak/cevaplar.md) | cevap | — |

### 99 — Final

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet — her konunun tek satırı](99-final/cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar — bitirmeden önce oku](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Genel deneme · 36 soru · 45 dk | [test](99-final/final-test.html) | — |

---

## Program

| Gün | Bölüm | Odak |
|---|---|---|
| **1** | `00` | Oyunun kuralları, kurulum, plan ve maliyet |
| **2-3** | `01` · 1.1-1.3 | Ajan döngüsü, istem, bağlam |
| **4** | `01` · 1.4-1.5 | Yetki ve doğrulama |
| **5-6** | `02` | Kural dosyaları, AGENTS.md, kalıcı bellek |
| **7** | `99` | Cheatsheet, son tekrar, genel deneme |

**Günlük tempo:** bir konu ≈ 20-25 dk okuma + 15-20 dk pratik + 8-10 dk test.
`⭐` işaretli başlıkları atlama.

---

## Otomatik skor kaydı

Tarayıcı güvenlik nedeniyle diske **yazamaz**. Test sonuçlarının yukarıdaki
tabloya kendiliğinden düşmesi için küçük bir yerel süreç çalıştırman gerekiyor:

```bash
cd /yol/VibeCoding101 && SKOR_PORT=8897 node assets/skor-sunucu.js
```

Sonra testleri `http://localhost:8897/01-ajanla-calismak/1.1-test.html` gibi açabilirsin —
ya da dosyaya çift tıklamaya devam et, ikisi de çalışır.

> ⚠️ **Port neden 8897?** Skor sunucusunun varsayılanı 8899. Başka bir kurs seti aynı
> anda açıksa portu kapar, bu sunucu hiç açılmaz ve skorların **sessizce** kaydedilmez.
> Bu dizinin dört kursuna ayrı port verildi: **101 → 8897 · 201 → 8896 ·
> 301 → 8895 · 401 → 8894.**

**Sunucu kapalıyken ne olur:** hiçbir şey kaybolmaz. Sonuç tarayıcının belleğinde
kuyruğa alınır, sunucuyu açıp herhangi bir testi yeniden açtığında gönderilir.

macOS'ta hep açık tutmak istersen: `assets/skor-sunucu.plist` dosyasındaki yolu
düzenleyip `~/Library/LaunchAgents/` altına kopyala, sonra
`launchctl load ~/Library/LaunchAgents/local.vibe101.skor.plist`.

---

## Klasör düzeni

```
VibeCoding101/
├── README.md                    ← buradasın
├── soru-cevap.md                ← sorularının biriktiği defter
├── assets/                      ← test motoru + skor sunucusu (elle düzenleme)
├── 00-baslangic/                ← genel bakış · kurulum · sözlük · ayar rehberi · maliyet
├── 01-ajanla-calismak/          ← ajan döngüsü, istem, bağlam, izin, doğrulama
├── 02-projeye-kural-yazmak/     ← CLAUDE.md, kural dosyaları, AGENTS.md, bellek
└── 99-final/                    ← cheatsheet, son tekrar, genel deneme
```

Her bölüm klasöründe konunun `.md` dosyası, testinin `.html` dosyası ve bölümün
`cevaplar.md` dosyası bulunur.

---

## Bu kurs neyi öğretmez

Dürüst olmak, sonradan hayal kırıklığı yaşamaktan iyidir:

- **Kod yazmayı öğretmez.** Ajanın ürettiğini okuyup yanlışını görebilmen gerekiyor;
  bu kurs o beceriyi varsayar, kazandırmaz.
- **Günlük iş akışlarını öğretmez.** Sıfırdan özellik, hata ayıklama, kod inceleme ve
  toplu dönüşüm **201**'in konusu.
- **Kendi aletini yapmayı öğretmez.** Skill, alt ajan, hook ve MCP **301**'de.
- **Güvenlik, gizlilik, lisans ve ekip tarafını öğretmez.** Onlar **401**'de.
- **Bugünün fiyat listesi değildir.** Sayılar örnektir; kaynağı her seferinde yazılı.
