# Son tekrar — bitirmeden önce oku

> Bu dosya, kursu bitirmeden ve genel denemeye girmeden önce okunur.
> `cheatsheet.md` **bilgiyi** hatırlatır; bu dosya **kullanmayı**.

---

## 1. Bu kursun tek cümlesi

> **Her seferinde tekrarladığın talimat, aslında yazılmamış bir alettir.**

Yedi konunun tamamı bunun bir sonucu: tekrar eden işi dosyaya al, tutması
şart olanı garantiye bağla, ajanın erişimini bilinçli seç.

---

## 2. Beş temel refleks

1. **Şart mı? → hook.** Her zaman mı? → kural dosyası. Bazen mi? → **skill**.
2. **`description` ajanın seçim ölçütüdür.** Ne yaptığını değil, **ne zaman
   çağrılacağını** yaz.
3. **Alt ajana devrederken araç listesini daralt.**
4. **Bağlanmak güvenmektir.** Komut satırı aracı yetiyorsa sunucu bağlama.
5. **Otomasyon denetimi kaldırır.** Kaldırdığın denetimin yerine ne koyduğunu bil.

---

## 3. Altı kritik ayrım

| Ayrım | Yanlış taraf ne yapar |
|---|---|
| Kural dosyası **garanti değil** | Kritik kuralı ricaya bırakırsın |
| Skill ≠ **alt ajan** | Bağlam yalıtımı beklerken içerik yüklersin |
| Alt ajan ≠ **fork** | Devraldığını sanırsın, sıfırdan başlar |
| Hız ≠ **bağlam yalıtımı** | Alt ajanı yanlış gerekçeyle kullanırsın |
| MCP ≠ **komut satırı aracı** | Bedava sandığın araç listesi bağlam yer |
| Otomasyon ≠ **etkileşimli oturum** | Onay beklediğini sanırsın, sorulmaz |

---

## 4. Alet yazmadan önce — 60 saniyelik kontrol

- [ ] **Bu iş gerçekten tekrar ediyor mu?** Üç kez yaptın mı?
- [ ] **Doğru araç hangisi?** Rica mı, garanti mi, çağrılınca mı?
- [ ] **`description` ne zaman çağrılacağını söylüyor mu?**
- [ ] **Kaç tur, hangi araçlar, hangi yalıtım?**
- [ ] **Bu alet kod çalıştırıyor mu?** Kimin kodunu?

---

## 5. Kurmadan önce — güvenlik kontrolü

- [ ] Eklenti/sunucu **kimin**? Depo geçmişi ne diyor?
- [ ] **Hangi yetkileri** istiyor? En az yetki verildi mi?
- [ ] Çıktısı **ajanın gözüne veri sokuyor** mu? Dış içerik nereden geliyor?
- [ ] **Yıkıcı bir işlem** sunuyor mu? Sunmasa olur mu?

Dört soru. Kurulan her eklenti, çalışmasına izin verilen bir koddur.

---

## 6. En sık düşülen yedi tuzak

| # | Tuzak | Kurtuluş |
|---|---|---|
| 1 | Ne yaptığını anlatan `description` | Ne zaman çağrılacağını yaz |
| 2 | Şişmiş skill gövdesi | Başa koy; kırpılırken **baş korunur** |
| 3 | Hız için alt ajan | Gerekçe **bağlam yalıtımı** olmalı |
| 4 | Alt ajandan tam çıktı beklemek | Gördüklerini **kaybeder**, özet alırsın |
| 5 | Sayfalanmamış sunucu çıktısı | Bağlamı bir çağrı doldurur |
| 6 | Denetimsiz eklenti kurulumu | Dört soruyu sor |
| 7 | Otomasyonu geniş yetkiyle başlatmak | **Çıplak** başlat, kip **Manual** |

---

## 7. Denemeye girmeden

- **Cheatsheet'i bir kez tara** — 10 dakika, tablolara bak.
- **Zayıf kaldığın testleri hatırla.** `%80` altında kaldığın konuya dön.
- Deneme **24 soru, 30 dakika** — soru başına ~75 saniye. Takılırsan **geç**.
- Sorular konu testlerinin kopyası **değil**: aynı bilgi, **farklı senaryo**.

---

## 8. Kursu bitirdikten sonra

| Ne zaman | Ne yap |
|---|---|
| **Hemen** | Kendi projende bir skill **ve** bir hook kur |
| **İlk hafta** | Bir alt ajan yaz; araç listesini bilerek daralt |
| **İlk ay** | Kurulumunu taşınabilir hâle getir |
| **Sonra** | **401 — Üretim ve ekip**: bunları başkalarıyla kullan |

Yazdığın her alet bir **yetki genişletmesidir**. **401** o yetkinin
güvenlik, gizlilik, lisans, maliyet ve ekip tarafını ele alıyor.

---

## 9. Bu kursun sana vermediği şey

- **Araç karşılaştırması ve seçim ölçütleri** **401**'de.
- **Güvenlik ve gizlilik derinliği** **401**'de; burada yalnızca kurulum
  anındaki dört soru var.
- Bu kursun bir kısmı **hızla eskir**: alet arayüzleri sürüm sürüm değişiyor.
  **`⚠️` işaretli yerleri doğrula.**

Kalan tek şey aynı: **doğrulayamadığın şeyi teslim etme.**

---

## ✅ Genel deneme

➡️ **[final-test.html](final-test.html)** — 24 soru · 30 dakika

Denemeye **dinlenmiş kafayla** gir. Konu testlerinin hemen ardından çözmek,
denemenin ölçüm değerini düşürür.
