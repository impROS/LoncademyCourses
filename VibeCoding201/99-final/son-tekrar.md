# Son tekrar — bitirmeden önce oku

> Bu dosya, kursu bitirmeden ve genel denemeye girmeden önce okunur.
> `cheatsheet.md` **bilgiyi** hatırlatır; bu dosya **kullanmayı**.

---

## 1. Bu kursun tek cümlesi

> **Ajanla çalışmanın hızı istemde değil, döngünün kapanmasındadır.**

Yedi konunun tamamı aynı şeyi söylüyor: ajana ölçebileceği bir hedef ver,
çıktısını kendin oku, iddiayı kanıtla değiştir.

---

## 2. Beş temel refleks

1. **Önce keşif, sonra plan, sonra kod.** Sıra atlanmaz, ölçeklenir.
2. **Var olan koda dokunmadan önce "neden böyle olmuş?" diye sor.**
3. **Hatayı önce yeniden üret.** Üretemediğin hatayı düzeltemezsin.
4. **Kırmızıyı gör.** Hiç kırmızı olmamış test bir şey ölçmüyor olabilir.
5. **Diff'i sen oku.** Beş kontrol, her seferinde.

---

## 3. Altı kritik ayrım

| Ayrım | Yanlış taraf ne yapar |
|---|---|
| Testi çalıştırmak ≠ **yazmak** | Döngü kapanmaz, iddia alırsın |
| Denetimi geçmek ≠ **çözmek** | Bastırılmış hatayı düzeltilmiş sanırsın |
| Bulgu sayısı ≠ **kalite** | Aşırı mühendisliğe gidersin |
| Kontrol noktası ≠ **git** | Oturum kapanınca geri dönüş noktasız kalırsın |
| Mekanik dönüşüm ≠ **kararlı dönüşüm** | Yargı gereken işi betiğe bırakırsın |
| Yeşil test ≠ **doğru kod** | Aynalanmış testle kendi hatanı onaylarsın |

---

## 4. Bir işe başlamadan önce — 60 saniyelik kontrol

- [ ] **Keşif yapıldı mı?** Dosyalar, kalıplar, komşu kod okundu mu?
- [ ] **Plan yazılı mı?** Ve **kapsam dışı** belirtildi mi?
- [ ] **Doğrulama ne olacak?** Ajan neyi çalıştırıp okuyacak?
- [ ] **Geri dönüş noktası var mı?** Dal açıldı mı, ağaç temiz mi?
- [ ] **Bu iş mekanik mi, yargı mı gerektiriyor?**

---

## 5. İş biterken — teslim kontrolü

- [ ] Doğrulama **çalıştırıldı** ve çıktısı **gösterildi** mi?
- [ ] Diff'i **sen okudun** mu? (**beş kontrol**)
- [ ] Kapsam dışına çıkılmış mı?
- [ ] Hata bastırılmış mı? (**altı kalıp**)
- [ ] Testler gerçekten bir şey **ölçüyor** mu? (**dört kusur**)
- [ ] Toplu değişiklikte **sayılar karşılaştırıldı** mı?
- [ ] Commit notu ve kaynak belli mi?

---

## 6. En sık düşülen yedi tuzak

| # | Tuzak | Kurtuluş |
|---|---|---|
| 1 | Keşifsiz kod | Önce oku, sonra yazdır |
| 2 | Kapsam kaçması | Planda kapsam dışını yaz |
| 3 | Yeniden üretilmemiş hata | Önce kırmızıyı üret |
| 4 | Kırmızı görülmemiş test | Sırayı dayat |
| 5 | Aynalanmış test | Testi koddan **önce** yazdır |
| 6 | Okunmamış diff | Beş kontrolü uygula |
| 7 | Sessiz kırpma | Sayıları karşılaştır |

---

## 7. Denemeye girmeden

- **Cheatsheet'i bir kez tara** — 10 dakika, tablolara bak.
- **Zayıf kaldığın testleri hatırla.** `%80` altında kaldığın konuya dön.
- Deneme **29 soru, 36 dakika** — soru başına ~74 saniye. Takılırsan **geç**.
- Sorular konu testlerinin kopyası **değil**: aynı bilgi, **farklı senaryo**.

---

## 8. Kursu bitirdikten sonra

| Ne zaman | Ne yap |
|---|---|
| **Hemen** | Kendi projende bir işi baştan sona bu sırayla yürüt |
| **İlk hafta** | Bir hatayı "yeniden üret → kök sebep → doğrula" ile çöz |
| **Sonra** | **301 — Kendi aletini yapmak**: bu akışları otomatikleştir |

Bu kursta tekrar tekrar elle yaptığın şeyler — doğrulama komutunu hatırlatmak,
diff kontrolünü uygulamak, kapsamı daraltmak — **301**'de skill, hook ve alt
ajana dönüşüyor.

---

## 9. Bu kursun sana vermediği şey

- **Kendi aletini yapmayı** öğretmez; onu **301** yapıyor.
- **Güvenlik, gizlilik, lisans ve ekip politikası** **401**'de.
- Bu kursun bir kısmı eskiyecek — **`⚠️` işaretli yerleri doğrula.**

Kalan tek şey aynı: **doğrulayamadığın şeyi teslim etme.**

---

## ✅ Genel deneme

➡️ **[final-test.html](final-test.html)** — 29 soru · 36 dakika

Denemeye **dinlenmiş kafayla** gir. Konu testlerinin hemen ardından çözmek,
denemenin ölçüm değerini düşürür.
