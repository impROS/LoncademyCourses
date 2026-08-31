# Vibe Coding 201 — Günlük iş akışları

> Ajanın nasıl çalıştığını biliyorsun; sıra onunla **gerçek iş yapmakta**. Bu kurs
> günlük yazılım işinin altı akışını öğretir: sıfırdan özellik çıkarmak, tanımadığın
> koda dokunmak, hata ayıklamak, test yazdırmak, diff okumak ve büyük dönüşümleri
> güvenle yapmak.
>
> Anlatım Türkçe; komutlar, dosya adları ve istem (prompt) örnekleri kendi dilinde kalır.

**Süre:** ~1 hafta · günde ~1 saat · **8 konu** · 8 test · 127 soru
**Ön koşul:** **101 — Temeller** kursu. Ayrıca `git` kullanabiliyor olmak.
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
7. **Terim tanımadıysan** [kavram sözlüğüne](00-baslangic/02-kavram-sozlugu.md) bak;
   ayar ya da karar seçerken [seçim ve ayar rehberine](00-baslangic/03-ayar-rehberi.md).
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
| [ ] | [00.1 Bu kursa başlarken](00-baslangic/01-bu-kursa-baslarken.md) | — | — |
| 📖 | [Kavram sözlüğü](00-baslangic/02-kavram-sozlugu.md) | başvuru | — |
| ⚙️ | [Seçim ve ayar rehberi](00-baslangic/03-ayar-rehberi.md) | başvuru | — |
| ❓ | [Soru & cevap defteri](soru-cevap.md) | başvuru | — |

### 01 — Kod üretmek ve değiştirmek

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Sıfırdan özellik: keşif → plan → uygulama](01-kod-yazdirmak/1.1-sifirdan-ozellik.md) | [test](01-kod-yazdirmak/1.1-test.html) | — |
| [ ] | [1.2 Var olan koda dokunmak](01-kod-yazdirmak/1.2-var-olan-koda-dokunmak.md) | [test](01-kod-yazdirmak/1.2-test.html) | — |
| [ ] | [1.3 Hata ayıklama: önce yeniden üret](01-kod-yazdirmak/1.3-hata-ayiklama.md) | [test](01-kod-yazdirmak/1.3-test.html) | — |
| 💡 | [Kendini kontrol cevapları](01-kod-yazdirmak/cevaplar.md) | cevap | — |

### 02 — Kaliteyi güvenceye almak

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 Test yazdırmak ve testle sürmek](02-kaliteyi-guvenceye-almak/2.1-test-yazdirma.md) | [test](02-kaliteyi-guvenceye-almak/2.1-test.html) | — |
| [ ] | [2.2 Kod inceleme: ajanın kodunu ve ajanla incelemek](02-kaliteyi-guvenceye-almak/2.2-kod-inceleme.md) | [test](02-kaliteyi-guvenceye-almak/2.2-test.html) | — |
| [ ] | [2.3 Git, commit ve pull request](02-kaliteyi-guvenceye-almak/2.3-git-ve-pr.md) | [test](02-kaliteyi-guvenceye-almak/2.3-test.html) | — |
| [ ] | [2.4 Büyük dönüşümler: göç, yeniden yapılandırma, toplu değişiklik](02-kaliteyi-guvenceye-almak/2.4-buyuk-donusum.md) | [test](02-kaliteyi-guvenceye-almak/2.4-test.html) | — |
| 💡 | [Kendini kontrol cevapları](02-kaliteyi-guvenceye-almak/cevaplar.md) | cevap | — |

### 99 — Final

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet — her konunun tek satırı](99-final/cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar — bitirmeden önce oku](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Genel deneme · 29 soru · 36 dk | [test](99-final/final-test.html) | — |

---

## Program

| Gün | Bölüm | Odak |
|---|---|---|
| **1** | `00` | Ön koşul denetimi, ortam, sözlük |
| **2-3** | `01` | Sıfırdan özellik, var olan kod, hata ayıklama |
| **4-6** | `02` | Test, inceleme, git ve pull request, büyük dönüşüm |
| **7** | `99` | Cheatsheet, son tekrar, genel deneme |

**Günlük tempo:** bir konu ≈ 20-25 dk okuma + 15-20 dk pratik + 8-10 dk test.
Pratiklerin hepsi **atılabilir bir projede** yapılmalı.

---

## Otomatik skor kaydı

Tarayıcı güvenlik nedeniyle diske **yazamaz**. Test sonuçlarının yukarıdaki
tabloya kendiliğinden düşmesi için küçük bir yerel süreç çalıştırman gerekiyor:

```bash
cd /yol/VibeCoding201 && SKOR_PORT=8896 node assets/skor-sunucu.js
```

Sonra testleri `http://localhost:8896/01-kod-yazdirmak/1.1-test.html` gibi açabilirsin —
ya da dosyaya çift tıklamaya devam et, ikisi de çalışır.

> ⚠️ **Port neden 8896?** Skor sunucusunun varsayılanı 8899. Başka bir kurs seti aynı
> anda açıksa portu kapar, bu sunucu hiç açılmaz ve skorların **sessizce** kaydedilmez.
> Bu dizinin dört kursuna ayrı port verildi: **101 → 8897 · 201 → 8896 ·
> 301 → 8895 · 401 → 8894.**

**Sunucu kapalıyken ne olur:** hiçbir şey kaybolmaz. Sonuç tarayıcının belleğinde
kuyruğa alınır, sunucuyu açıp herhangi bir testi yeniden açtığında gönderilir.

macOS'ta hep açık tutmak istersen: `assets/skor-sunucu.plist` dosyasındaki yolu
düzenleyip `~/Library/LaunchAgents/` altına kopyala, sonra
`launchctl load ~/Library/LaunchAgents/local.vibe201.skor.plist`.

---

## Klasör düzeni

```
VibeCoding201/
├── README.md                        ← buradasın
├── soru-cevap.md                    ← sorularının biriktiği defter
├── assets/                          ← test motoru + skor sunucusu (elle düzenleme)
├── 00-baslangic/                    ← ön koşul, ortam, sözlük, ayar rehberi
├── 01-kod-yazdirmak/                ← sıfırdan özellik, var olan kod, hata ayıklama
├── 02-kaliteyi-guvenceye-almak/     ← test, inceleme, git ve pull request, dönüşüm
└── 99-final/                        ← cheatsheet, son tekrar, genel deneme
```

Her bölüm klasöründe konunun `.md` dosyası, testinin `.html` dosyası ve bölümün
`cevaplar.md` dosyası bulunur.

---

## Bu kurs neyi öğretmez

- **Ajanın temellerini öğretmez.** Döngü, bağlam, izin kipleri ve kural dosyaları
  **101**'de; bu kurs onları bildiğini varsayar.
- **Kendi aletini yapmayı öğretmez.** Burada elle yaptığın tekrarları skill, hook ve
  alt ajana çevirmek **301**'in konusu.
- **Güvenlik, gizlilik, lisans ve ekip politikası** **401**'de.
- **Belirli bir dil ya da çerçeve öğretmez.** Örnekler kasten küçük ve tarafsız.
