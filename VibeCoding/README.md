# Vibe Coding — Yapay zekâ ajanlarıyla yazılım geliştirme

> Kodu artık çoğunlukla sen yazmıyorsun; yazdırıyorsun ve denetliyorsun. Bu set, o işi
> **şansa bırakmadan** yapmayı öğretir: ajan nasıl çalışır, bağlam neden en kıt kaynağındır,
> hangi kuralı nereye yazarsın, kendi aletini nasıl yaparsın, ve üretimde neyin ters
> gidebileceğini nasıl önceden görürsün.
>
> Anlatım Türkçe; komutlar, dosya adları, ayar adları ve istem (prompt) örnekleri kendi
> dilinde kalır — çünkü ekranda onları öyle göreceksin.

**Süre:** 4 hafta · günde ~1 saat · **32 konu** · 35 test · 582 soru
**Ön koşul:** Bir dilde kod okuyup yazabiliyor olmak. Terminal ve `git` temelleri.
**Ana araç:** Claude Code (omurga) · diğer araçlar `05` bölümünde karşılaştırmalı

---

## Nasıl çalışılır

1. **Sırayla git.** Konular birbirinin üstüne biniyor. `03` bölümündeki iş akışları,
   `01`'deki bağlam yönetimini bildiğini varsayar.
2. **Önce oku, sonra pratiği yap, en son teste gir.** Test, konu dosyasındaki bilgiyle
   çözülebilir; dışarıdan bir şey gerekmez.
3. **Pratikleri gerçekten yap.** Bu setin pratikleri okunacak metin değil, çalıştırılacak
   komutlardır. Ajanla çalışmak, okuyarak değil deneyerek oturuyor.
4. **Kendini kontrol sorularının cevabını önce kâğıda yaz**, sonra o bölümün
   `cevaplar.md` dosyasını aç. Göz kayarsa düşünme adımı atlanır.
5. **%80 altında kaldıysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.
6. **Aklına takılanı [`soru-cevap.md`](soru-cevap.md)'ye yaz.** Kurs boyunca büyüyen defterin.
7. **Terim tanımadıysan** [kavram sözlüğüne](00-baslangic/03-kavram-sozlugu.md) bak;
   ayar ya da karar seçerken [seçim ve ayar rehberi](00-baslangic/04-ayar-rehberi.md).
8. **Skor sunucusunu açık tut** (aşağıya bak) — test sonuçların bu tabloya kendiliğinden düşer.
9. **Hafta sonunda `99-final/cheatsheet.md`'yi tara.** Unuttuğun yeri hemen görürsün.

> ⚠️ **Bu setin bir tarafı hızla eskir.** Araç sürümleri, fiyatlar, model adları ve
> özellik listeleri aylık değişiyor. Her konu dosyasında **kalıcı fikir** ile
> **bugünkü ayrıntı** ayrı işaretlendi. `⚠️ Doğrulanmalı` gördüğün yerde resmî
> dokümana bak — adres orada yazıyor.

---

## İlerleme tablosu

### 00 — Başlangıç *(konu değil, kurulum · ~3 saat)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [00.1 Vibe coding nedir, ne değildir — ve trickler](00-baslangic/01-genel-bakis-ve-trickler.md) | [test](00-baslangic/01-test.html) | — |
| [ ] | [00.2 Ortam kurulumu ve örnek proje](00-baslangic/02-kurulum.md) | — | — |
| 📖 | [Kavram sözlüğü](00-baslangic/03-kavram-sozlugu.md) | başvuru | — |
| ⚙️ | [Seçim ve ayar rehberi](00-baslangic/04-ayar-rehberi.md) | başvuru | — |
| [ ] | [00.3 Plan, kota ve maliyet — ne satın alınmalı](00-baslangic/05-plan-ve-maliyet.md) | — | — |
| 💡 | [Kendini kontrol cevapları](00-baslangic/cevaplar.md) | cevap | — |
| ❓ | [Soru & cevap defteri](soru-cevap.md) | başvuru | — |

### 01 — Ajanla çalışmanın temelleri *(Hafta 1)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [1.1 Ajan döngüsü: bir istek ne oluyor da koda dönüşüyor](01-temeller/1.1-ajan-dongusu.md) | [test](01-temeller/1.1-test.html) | — |
| [ ] | [1.2 İstem yazma: belirsizliği nereden alırsın](01-temeller/1.2-istem-yazma.md) | [test](01-temeller/1.2-test.html) | — |
| [ ] | [1.3 Bağlam penceresi — en kıt kaynağın](01-temeller/1.3-baglam-yonetimi.md) | [test](01-temeller/1.3-test.html) | — |
| [ ] | [1.4 İzinler, plan modu ve kum havuzu](01-temeller/1.4-izinler-ve-plan-modu.md) | [test](01-temeller/1.4-test.html) | — |
| [ ] | [1.5 Doğrulama refleksi: ajana kapatabileceği bir döngü ver](01-temeller/1.5-dogrulama-refleksi.md) | [test](01-temeller/1.5-test.html) | — |
| 💡 | [Kendini kontrol cevapları](01-temeller/cevaplar.md) | cevap | — |

### 02 — Projeye kural yazmak *(Hafta 2)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 CLAUDE.md: her oturuma giren bağlam](02-proje-kurallari/2.1-claude-md.md) | [test](02-proje-kurallari/2.1-test.html) | — |
| [ ] | [2.2 Kural dosyaları ve yola göre kapsam](02-proje-kurallari/2.2-kural-dosyalari.md) | [test](02-proje-kurallari/2.2-test.html) | — |
| [ ] | [2.3 AGENTS.md ve araçlar arası taşınabilirlik](02-proje-kurallari/2.3-agents-md.md) | [test](02-proje-kurallari/2.3-test.html) | — |
| [ ] | [2.4 Kalıcı bellek: oturumlar arasında ne taşınır](02-proje-kurallari/2.4-kalici-bellek.md) | [test](02-proje-kurallari/2.4-test.html) | — |
| 💡 | [Kendini kontrol cevapları](02-proje-kurallari/cevaplar.md) | cevap | — |

### 03 — Günlük iş akışları *(Hafta 2)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [3.1 Sıfırdan özellik: keşif → plan → uygulama](03-is-akislari/3.1-sifirdan-ozellik.md) | [test](03-is-akislari/3.1-test.html) | — |
| [ ] | [3.2 Var olan koda dokunmak](03-is-akislari/3.2-var-olan-koda-dokunmak.md) | [test](03-is-akislari/3.2-test.html) | — |
| [ ] | [3.3 Hata ayıklama: önce yeniden üret](03-is-akislari/3.3-hata-ayiklama.md) | [test](03-is-akislari/3.3-test.html) | — |
| [ ] | [3.4 Test yazdırmak ve testle sürmek](03-is-akislari/3.4-test-yazdirma.md) | [test](03-is-akislari/3.4-test.html) | — |
| [ ] | [3.5 Kod inceleme: ajanın kodunu ve ajanla incelemek](03-is-akislari/3.5-kod-inceleme.md) | [test](03-is-akislari/3.5-test.html) | — |
| [ ] | [3.6 Git, commit ve pull request](03-is-akislari/3.6-git-ve-pr.md) | [test](03-is-akislari/3.6-test.html) | — |
| [ ] | [3.7 Büyük dönüşümler: göç, yeniden yapılandırma, toplu değişiklik](03-is-akislari/3.7-buyuk-donusum.md) | [test](03-is-akislari/3.7-test.html) | — |
| 💡 | [Kendini kontrol cevapları](03-is-akislari/cevaplar.md) | cevap | — |

### 04 — Kendi aletini yapmak *(Hafta 3)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [4.1 Skill yazmak: tekrarlayan işi dosyaya almak](04-kendi-aletini-yap/4.1-skill-yazma.md) | [test](04-kendi-aletini-yap/4.1-test.html) | — |
| [ ] | [4.2 Subagent: ayrı bağlamda çalışan yardımcı](04-kendi-aletini-yap/4.2-subagent.md) | [test](04-kendi-aletini-yap/4.2-test.html) | — |
| [ ] | [4.3 Hook: rica değil, garanti](04-kendi-aletini-yap/4.3-hooklar.md) | [test](04-kendi-aletini-yap/4.3-test.html) | — |
| [ ] | [4.4 MCP: ajanı dış sistemlere bağlamak](04-kendi-aletini-yap/4.4-mcp-baglama.md) | [test](04-kendi-aletini-yap/4.4-test.html) | — |
| [ ] | [4.5 Kendi MCP sunucunu yazmak](04-kendi-aletini-yap/4.5-mcp-sunucusu-yazma.md) | [test](04-kendi-aletini-yap/4.5-test.html) | — |
| [ ] | [4.6 Eklenti ve paylaşım: kurulumunu taşınabilir yapmak](04-kendi-aletini-yap/4.6-eklenti-ve-paylasim.md) | [test](04-kendi-aletini-yap/4.6-test.html) | — |
| [ ] | [4.7 Otomasyon: kabuktan, CI'dan ve SDK'dan ajan çalıştırmak](04-kendi-aletini-yap/4.7-otomasyon-ve-sdk.md) | [test](04-kendi-aletini-yap/4.7-test.html) | — |
| 💡 | [Kendini kontrol cevapları](04-kendi-aletini-yap/cevaplar.md) | cevap | — |

### 05 — Araç haritası *(Hafta 4)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [5.1 Alan haritası: hangi araç ne yapıyor](05-arac-haritasi/5.1-arac-haritasi.md) | [test](05-arac-haritasi/5.1-test.html) | — |
| [ ] | [5.2 Hangisi ne zaman: karar tablosu ve taşınma maliyeti](05-arac-haritasi/5.2-hangisi-ne-zaman.md) | [test](05-arac-haritasi/5.2-test.html) | — |
| [ ] | [5.3 Bilmen gereken kaynaklar ve repolar](05-arac-haritasi/5.3-kaynaklar-ve-repolar.md) | [test](05-arac-haritasi/5.3-test.html) | — |
| 💡 | [Kendini kontrol cevapları](05-arac-haritasi/cevaplar.md) | cevap | — |

### 06 — Üretim ve ekip *(Hafta 4)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [6.1 İstem enjeksiyonu ve tedarik zinciri](06-uretim-ve-ekip/6.1-guvenlik.md) | [test](06-uretim-ve-ekip/6.1-test.html) | — |
| [ ] | [6.2 Gizlilik: ne gönderiliyor, nerede duruyor](06-uretim-ve-ekip/6.2-gizlilik-ve-veri.md) | [test](06-uretim-ve-ekip/6.2-test.html) | — |
| [ ] | [6.3 Lisans ve telif: kimin kodu, kimin sorumluluğu](06-uretim-ve-ekip/6.3-lisans-ve-telif.md) | [test](06-uretim-ve-ekip/6.3-test.html) | — |
| [ ] | [6.4 Maliyet: neyin parası ödeniyor](06-uretim-ve-ekip/6.4-maliyet-yonetimi.md) | [test](06-uretim-ve-ekip/6.4-test.html) | — |
| [ ] | [6.5 Ekipçe kullanım: ortak kurulum ve inceleme politikası](06-uretim-ve-ekip/6.5-ekipce-kullanim.md) | [test](06-uretim-ve-ekip/6.5-test.html) | — |
| [ ] | [6.6 Sınırlar: nerede kullanılmaz](06-uretim-ve-ekip/6.6-sinirlar.md) | [test](06-uretim-ve-ekip/6.6-test.html) | — |
| 💡 | [Kendini kontrol cevapları](06-uretim-ve-ekip/cevaplar.md) | cevap | — |

### 99 — Final *(Hafta 4 sonu)*

| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet — her konunun tek satırı](99-final/cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar — bitirmeden önce oku](99-final/son-tekrar.md) | başvuru | — |
| [ ] | Genel deneme 1 · 57 soru · 70 dk | [test](99-final/final-1-test.html) | — |
| [ ] | Genel deneme 2 · 63 soru · 75 dk | [test](99-final/final-2-test.html) | — |

---

## Haftalık program

| Hafta | Bölüm | Odak | Bitince yapabiliyor olacaksın |
|---|---|---|---|
| **1** | `00` + `01` | Ajan nasıl çalışıyor | Ajanı denetleyerek kullanmak; bağlamı yönetmek; izin modunu bilerek seçmek |
| **2** | `02` + `03` | Kurallar ve günlük iş | Projene kural yazmak; özellik, hata ve inceleme akışlarını yürütmek |
| **3** | `04` | Kendi aletini yapmak | Skill, subagent, hook, MCP yazmak; ajanı CI'ya sokmak |
| **4** | `05` + `06` + final | Manzara ve sorumluluk | Araç seçmek; güvenlik, gizlilik, lisans ve maliyeti yönetmek |

**Günlük tempo:** bir konu ≈ 20-25 dk okuma + 15-20 dk pratik + 8-10 dk test.
Hafta 3 en yoğunu; sıkışırsan `4.5` ve `4.6`'yı hafta 4'e taşı, `⭐` işaretli başlıkları atlama.

---

## Ne satın alınmalı, ne alınmamalı

Ayrıntısı [`00-baslangic/05-plan-ve-maliyet.md`](00-baslangic/05-plan-ve-maliyet.md) içinde.
Özet karar:

| | Karar |
|---|---|
| ✅ **Bu set için gerekli** | Bir ajan aracına erişim (ücretsiz katman ya da giriş seviyesi abonelik) ve bir terminal. Başka hiçbir şey. |
| ✅ **Muhtemelen değer** | Günde 2+ saat ajanla çalışıyorsan üst abonelik. Kararı **ölçtükten sonra** ver — `6.4` bunu öğretiyor. |
| ⛔ **Alma** | "Vibe coding kursu" satan paketler, "en iyi 500 prompt" listeleri, ücretli prompt pazar yerleri. Bu setin `05.3` konusu bedava ve güncel kaynakları veriyor. |
| ⛔ **Aceleyle alma** | Aynı anda birden çok araç aboneliği. Önce `05` bölümünü bitir, sonra bir tanesini seç. |

> 💸 Bu sette **ücret doğuran tek şey** kullandığın ajanın kendi kotasıdır. Bulut kaynağı
> açtıran, kredi kartı isteyen bir pratik yok. Yine de ücretli bir planda çalışıyorsan
> `6.4`'teki ölçüm alışkanlıklarını baştan edin.

---

## Otomatik skor kaydı

Bir testi bitirdiğinde sonuç (tarih, skor, yüzde, süre, zayıf alanlar) **elle yazmadan**
konu dosyasının altına ve yukarıdaki ilerleme tablosuna düşer.

**Neden bir komut gerekiyor:** Tarayıcı güvenlik nedeniyle diske dosya yazamaz. Sonucu
alıp `.md` dosyasına yazacak küçük bir yerel süreç şart. Tek seferlik:

```bash
cd /yol/VibeCoding && SKOR_PORT=8897 node assets/skor-sunucu.js
```

Sonra testleri `http://localhost:8897/01-temeller/1.1-test.html` gibi açabilirsin —
ya da dosyaya çift tıklamaya devam et, ikisi de çalışır.

> ⚠️ **Port neden 8897?** Skor sunucusunun varsayılanı 8899. Başka bir kurs seti aynı
> anda açıksa portu kapar, bu sunucu hiç açılmaz ve skorların **sessizce** kaydedilmez.
> Bu sete 8897 ayrıldı. Başka bir sette çalışıyorsan onun kendi portunu kullan.

**Sunucu kapalıyken ne olur:** hiçbir şey kaybolmaz. Sonuç tarayıcının belleğinde
kuyruğa alınır, sunucuyu açıp herhangi bir testi yeniden açtığında gönderilir.

macOS'ta hep açık tutmak istersen: `assets/skor-sunucu.plist` dosyasındaki yolu
düzenleyip `~/Library/LaunchAgents/` altına kopyala, sonra
`launchctl load ~/Library/LaunchAgents/local.vibe.skor.plist`.

---

## Klasör düzeni

```
VibeCoding/
├── README.md                    ← buradasın
├── soru-cevap.md                ← sorularının biriktiği defter
├── assets/                      ← test motoru + skor sunucusu (elle düzenleme)
├── 00-baslangic/                ← genel bakış · kurulum · sözlük · ayar rehberi · maliyet
├── 01-temeller/                 ← ajan döngüsü, istem, bağlam, izin, doğrulama
├── 02-proje-kurallari/          ← CLAUDE.md, kural dosyaları, AGENTS.md, bellek
├── 03-is-akislari/              ← özellik, bakım, hata, test, inceleme, git, dönüşüm
├── 04-kendi-aletini-yap/        ← skill, subagent, hook, MCP, eklenti, otomasyon
├── 05-arac-haritasi/            ← alan haritası, karar tablosu, kaynaklar
├── 06-uretim-ve-ekip/           ← güvenlik, gizlilik, lisans, maliyet, ekip, sınırlar
└── 99-final/                    ← cheatsheet, son tekrar, 2 genel deneme
```

Her bölüm klasöründe konunun `.md` dosyası, testinin `.html` dosyası ve bölümün
`cevaplar.md` dosyası bulunur.

---

## Bu set neyi öğretmez

Dürüst olmak, sonradan hayal kırıklığı yaşamaktan iyidir:

- **Kod yazmayı öğretmez.** Ajanın ürettiğini okuyup yanlışını görebilmen gerekiyor;
  bu set o beceriyi varsayar, kazandırmaz.
- **Belirli bir dil ya da çerçeve öğretmez.** Örnekler kasten küçük ve tarafsız.
- **"Hiç kod bilmeden ürün çıkarma" vaat etmez.** `6.6` bunun nerede tutup nerede
  tutmadığını ölçüyle anlatıyor.
- **Bugünün fiyat listesi değildir.** Sayılar örnektir; kaynağı her seferinde yazılı.
