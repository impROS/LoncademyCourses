# Vibe Coding 301 — Kendi aletini yapmak

> Aynı talimatı üçüncü kez yazdığında, aslında yazılmamış bir aletin var demektir.
> Bu kurs o aleti yapmayı öğretir: skill, ayrı bağlamda çalışan yardımcı, garanti
> veren hook, dış sistem bağlantısı, kendi sunucun, taşınabilir kurulum ve insansız
> çalışan otomasyon.
>
> Anlatım Türkçe; komutlar, ayar adları ve yapılandırma örnekleri kendi dilinde kalır.

**Süre:** ~1 hafta · günde ~1 saat · **8 konu** · 8 test · 122 soru
**Ön koşul:** **101 — Temeller** ve **201 — Günlük iş akışları**. Node.js ya da Python temeli.
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

### 01 — Ajanı programlamak

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Skill yazmak: tekrarlayan işi dosyaya almak](01-ajani-programlamak/1.1-skill-yazma.md) | [test](01-ajani-programlamak/1.1-test.html) | — |
| [ ] | [1.2 Subagent: ayrı bağlamda çalışan yardımcı](01-ajani-programlamak/1.2-subagent.md) | [test](01-ajani-programlamak/1.2-test.html) | — |
| [ ] | [1.3 Hook: rica değil, garanti](01-ajani-programlamak/1.3-hooklar.md) | [test](01-ajani-programlamak/1.3-test.html) | — |
| 💡 | [Kendini kontrol cevapları](01-ajani-programlamak/cevaplar.md) | cevap | — |

### 02 — Dış dünyaya bağlamak

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 MCP: ajanı dış sistemlere bağlamak](02-dis-dunyaya-baglamak/2.1-mcp-baglama.md) | [test](02-dis-dunyaya-baglamak/2.1-test.html) | — |
| [ ] | [2.2 Kendi MCP sunucunu yazmak](02-dis-dunyaya-baglamak/2.2-mcp-sunucusu-yazma.md) | [test](02-dis-dunyaya-baglamak/2.2-test.html) | — |
| [ ] | [2.3 Eklenti ve paylaşım: kurulumunu taşınabilir yapmak](02-dis-dunyaya-baglamak/2.3-eklenti-ve-paylasim.md) | [test](02-dis-dunyaya-baglamak/2.3-test.html) | — |
| [ ] | [2.4 Otomasyon: kabuktan, CI'dan ve SDK'dan ajan çalıştırmak](02-dis-dunyaya-baglamak/2.4-otomasyon-ve-sdk.md) | [test](02-dis-dunyaya-baglamak/2.4-test.html) | — |
| 💡 | [Kendini kontrol cevapları](02-dis-dunyaya-baglamak/cevaplar.md) | cevap | — |

### 99 — Final

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet — her konunun tek satırı](99-final/cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar — bitirmeden önce oku](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Genel deneme · 24 soru · 30 dk | [test](99-final/final-test.html) | — |

---

## Program

| Gün | Bölüm | Odak |
|---|---|---|
| **1** | `00` | Ön koşul denetimi, ortam, sözlük |
| **2-3** | `01` | Skill, alt ajan, hook |
| **4-6** | `02` | MCP bağlama, kendi sunucun, eklenti, otomasyon |
| **7** | `99` | Cheatsheet, son tekrar, genel deneme |

**Günlük tempo:** bir konu ≈ 20-25 dk okuma + 20-25 dk pratik + 8-10 dk test.
Bu kursun pratikleri **kod çalıştırır** — atılabilir bir projede yap.

---

## Otomatik skor kaydı

Tarayıcı güvenlik nedeniyle diske **yazamaz**. Test sonuçlarının yukarıdaki
tabloya kendiliğinden düşmesi için küçük bir yerel süreç çalıştırman gerekiyor:

```bash
cd /yol/VibeCoding301 && SKOR_PORT=8895 node assets/skor-sunucu.js
```

Sonra testleri `http://localhost:8895/01-ajani-programlamak/1.1-test.html` gibi açabilirsin —
ya da dosyaya çift tıklamaya devam et, ikisi de çalışır.

> ⚠️ **Port neden 8895?** Skor sunucusunun varsayılanı 8899. Başka bir kurs seti aynı
> anda açıksa portu kapar, bu sunucu hiç açılmaz ve skorların **sessizce** kaydedilmez.
> Bu dizinin dört kursuna ayrı port verildi: **101 → 8897 · 201 → 8896 ·
> 301 → 8895 · 401 → 8894.**

**Sunucu kapalıyken ne olur:** hiçbir şey kaybolmaz. Sonuç tarayıcının belleğinde
kuyruğa alınır, sunucuyu açıp herhangi bir testi yeniden açtığında gönderilir.

macOS'ta hep açık tutmak istersen: `assets/skor-sunucu.plist` dosyasındaki yolu
düzenleyip `~/Library/LaunchAgents/` altına kopyala, sonra
`launchctl load ~/Library/LaunchAgents/local.vibe301.skor.plist`.

---

## Klasör düzeni

```
VibeCoding301/
├── README.md                        ← buradasın
├── soru-cevap.md                    ← sorularının biriktiği defter
├── assets/                          ← test motoru + skor sunucusu (elle düzenleme)
├── 00-baslangic/                    ← ön koşul, ortam, sözlük, ayar rehberi
├── 01-ajani-programlamak/           ← skill, alt ajan, hook
├── 02-dis-dunyaya-baglamak/         ← MCP, kendi sunucun, eklenti, otomasyon
└── 99-final/                        ← cheatsheet, son tekrar, genel deneme
```

Her bölüm klasöründe konunun `.md` dosyası, testinin `.html` dosyası ve bölümün
`cevaplar.md` dosyası bulunur.

---

## Bu kurs neyi öğretmez

- **Ajanın temellerini ve günlük akışları öğretmez.** Onlar **101** ve **201**'de.
- **Araç karşılaştırması yapmaz.** Hangi aracın ne yaptığı ve nasıl seçileceği **401**'de.
- **Güvenlik derinliği vermez.** Burada yalnızca alet kurarken sorulacak dört soru var;
  istem enjeksiyonu, tedarik zinciri ve gizlilik **401**'in konusu.
- **En hızlı eskiyen kurs budur.** Alet arayüzleri sürüm sürüm değişiyor;
  `⚠️` işaretli her yerde resmî belgeye bak.
