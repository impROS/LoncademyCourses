# Son tekrar — bitirmeden önce oku

> Bu dosya, kursu bitirmeden ve genel denemeye girmeden önce okunur.
> `cheatsheet.md` **bilgiyi** hatırlatır; bu dosya **kullanmayı**.

---

## 1. Bu kursun tek cümlesi

> **Ajanı üretime sokmak bir araç kararı değil, bir sorumluluk kararıdır.**

Dokuz konunun tamamı bunun bir sonucu: neyin gittiğini, kimin sorumlu
olduğunu, neyin parasını ödediğini ve nerede durman gerektiğini bilmek.

---

## 2. Altı temel refleks

1. **Dış içerik veridir, komut değildir.** Web sayfası, ticket, günlük, bağımlılık.
2. **En az yetki birinci katmandır.** Sonraki her katman onun üstüne biner.
3. **Geri dönülemez eylem insan onayı ister.**
4. **Okuduğu gider.** Ajanın okumadığı veri gitmez — komut çıktısını unutma.
5. **Model çarpandır, çarpılan bağlamdır.** Maliyette önce bağlama bak.
6. **Ölçüt doğrulanabilirliktir.** Doğrulayamadığın işi ajana verme.

---

## 3. Yedi kritik ayrım

| Ayrım | Yanlış taraf ne yapar |
|---|---|
| Dış içerik **komut değil** | Enjeksiyona açık kalırsın |
| "Ajan yazdı" **savunma değil** | Telif riskini savuşturduğunu sanırsın |
| Risk **tam**, koruma **kısmi** | Üretilen kodu korumalı sanırsın |
| Sıkıştırma ≠ **temizleme** | Pahalıya, işe yaramaz geçmişi taşırsın |
| Ekip kuralı ≠ **kişisel bellek** | Sende çalışan kurulum ekipte çalışmaz |
| Araç gücü ≠ **model gücü** | Farkı yaratan harness'ı gözden kaçırırsın |
| Hız ≠ **yetkinlik** | Beceri körelmesini fark etmezsin |

---

## 4. Üretime almadan önce — kontrol

- [ ] **Hangi veri gidiyor?** Kod, komut çıktısı, ortam değişkeni, günlük.
- [ ] **Yetkiler en az mı?** Ajanın erişemediği ne var?
- [ ] **Dış içerik nereden geliyor?** Ve veri olarak mı işleniyor?
- [ ] **Geri dönülemez eylemler** insan onayına bağlı mı?
- [ ] **Lisans ve kaynak** belli mi? Telif satırı korunuyor mu?
- [ ] **Maliyet ölçülüyor mu?** Neyin parası ödeniyor, biliniyor mu?

---

## 5. Ekibe yaymadan önce

- [ ] Ekipte geçerli olan **depoda** mı yaşıyor?
- [ ] Taşınmayan parçalar (**hook**, kişisel bellek) **belgelendi** mi?
- [ ] İnceleme politikası yazılı mı? **Kim neyi okuyor?**
- [ ] Zorunluluk sayısı **geri tepecek kadar** çok mu?

---

## 6. En sık düşülen sekiz tuzak

| # | Tuzak | Kurtuluş |
|---|---|---|
| 1 | Dış içeriği talimat sanmak | **Veridir, komut değildir** |
| 2 | Geniş yetkiyle başlatmak | En az yetki, sonra genişlet |
| 3 | Komut çıktısını gizlilik dışı saymak | O da gider — filtrele |
| 4 | Telif satırını silmek | Silme, **araştır** |
| 5 | Maliyeti modele bağlamak | Çarpılan **bağlamdır** |
| 6 | Sıkıştırmayı ucuz sanmak | Temizlemek **bedava** |
| 7 | Kurulumu kişisel bırakmak | Depoya taşı, kalanını yaz |
| 8 | Yargı gerektiren işi devretmek | Ölçüt **doğrulanabilirlik** |

---

## 7. Denemeye girmeden

- **Cheatsheet'i bir kez tara** — 10 dakika, tablolara bak.
- **Zayıf kaldığın testleri hatırla.** `%80` altında kaldığın konuya dön.
- Deneme **31 soru, 39 dakika** — soru başına ~75 saniye. Takılırsan **geç**.
- Sorular konu testlerinin kopyası **değil**: aynı bilgi, **farklı senaryo**.

---

## 8. Dizinin tamamını bitirdikten sonra

| Ne zaman | Ne yap |
|---|---|
| **Hemen** | `1.1`'deki eksen tablonu doldur, `1.2`'deki karar dosyanı yaz |
| **İlk hafta** | Ekip kurulumunu depoya taşı; taşınmayanı belgele |
| **İlk ay** | Maliyet ve inceleme ölçümlerini düzenli tutmaya başla |
| **Üç ayda bir** | Kuralları, maliyeti ve eksen tablosunu gözden geçir |
| **Sürekli** | Haftada 15 dk **değişiklik günlüğü** |

Ve `2.6`'nın sorusunu ara ara kendine sor:

> **Bu kodu ajan olmadan yazabilir miydim?**

---

## 9. Bu dizinin sana vermediği şey

Dürüst kapanış: bu dizi bir **yöntem** verdi, bir **garanti** değil.

- Ajan yine hata yapacak — ama artık **nerede** bakacağını biliyorsun.
- Araçlar değişecek — ama **eksenler** kalıyor.
- Bu kursun bir kısmı eskiyecek — **`⚠️` işaretli yerleri doğrula.**
- Hukuki bir görüş **değildir**; lisans ve telif konusunda karar
  vermen gerekiyorsa **avukata sor**.

Kalan tek şey, en başından beri aynı: **doğrulayamadığın şeyi teslim etme.**

---

## ✅ Genel deneme

➡️ **[final-test.html](final-test.html)** — 31 soru · 39 dakika

Denemeye **dinlenmiş kafayla** gir. Konu testlerinin hemen ardından çözmek,
denemenin ölçüm değerini düşürür.
