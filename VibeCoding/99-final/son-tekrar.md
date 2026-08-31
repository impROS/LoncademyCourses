# Son tekrar — bitirmeden önce oku

> Bu dosya, seti bitirmeden ve genel denemelere girmeden önce okunur.
> `cheatsheet.md` **bilgiyi** hatırlatır; bu dosya **kullanmayı**.

---

## 1. Bu setin tek cümlesi

> **Kod yazmanın maliyeti düştü; kodun doğru olduğunu bilmenin maliyeti düşmedi.**

Otuz iki konunun tamamı bu cümlenin bir sonucu. Kaybolduğunda buraya dön.

---

## 2. Beş temel refleks

Diğer her şeyi unutsan bunlar kalsın:

1. **İlgisiz işler arasında bağlamı temizle.** Tek alışkanlıkla en büyük fark.
2. **Ajana kapatabileceği bir döngü ver.** Test, derleme, çalıştırıp deneme.
3. **İddia değil kanıt iste.** *"Ne çalıştırdın, ne döndü?"*
4. **Belirsizliği sen kapat.** Hedef · kaynak · kısıt · kabul ölçütü.
5. **Tutması şartsa hook yaz.** Kural dosyası bir ricadır.

---

## 3. Sekiz kritik ayrım

Yanlış tarafta durursan iş bozulur:

| Ayrım | Yanlış taraf ne yapar |
|---|---|
| Bozulma **sessizdir** | "Model bugün kötü" dersin, oturumu temizlemezsin |
| Plan modu **yetki aracı değil** | Güvende sanırsın, izin kurallarını kurmazsın |
| Kural dosyası **garanti değil** | Kritik kuralı ricaya bırakırsın |
| Testi çalıştırmak ≠ **yazmak** | Döngü kapanmaz, iddia alırsın |
| Denetimi geçmek ≠ **çözmek** | Bastırılmış hatayı düzeltilmiş sanırsın |
| Bulgu sayısı ≠ **kalite** | Aşırı mühendisliğe gidersin |
| Sıkıştırma ≠ **temizleme** | Pahalıya, işe yaramaz geçmişi taşırsın |
| Dış içerik **komut değil** | Enjeksiyona açık kalırsın |

---

## 4. Bir işe başlamadan önce — 60 saniyelik kontrol

- [ ] **Bağlam temiz mi?** Önceki işten kalan var mı?
- [ ] **Belirsizlik kapandı mı?** Dört parça yazılı mı?
- [ ] **Doğrulama ne olacak?** Ajan neyi çalıştırıp okuyacak?
- [ ] **Kapsam dışı ne?** Neye dokunulmayacak?
- [ ] **Geri dönüş noktası var mı?** Dal açıldı mı, ağaç temiz mi?
- [ ] **Bu iş ajana uygun mu?** Doğrulanabilir mi, mekanik mi?

Altı soru, bir dakika. Sonraki yarım saati belirler.

---

## 5. İş biterken — teslim kontrolü

- [ ] Doğrulama **çalıştırıldı** ve çıktısı **gösterildi** mi?
- [ ] Diff'i **sen okudun** mu? (**beş kontrol**)
- [ ] Kapsam dışına çıkılmış mı?
- [ ] Hata bastırılmış mı? (altı kalıp)
- [ ] Testler gerçekten bir şey **ölçüyor** mu?
- [ ] Geri dönülemez bir eylem varsa **sen mi onayladın**?
- [ ] Kaynağın belli mi? (commit notu / etiket)

---

## 6. En sık düşülen on tuzak

| # | Tuzak | Kurtuluş |
|---|---|---|
| 1 | Çorba oturum | İşler arasında temizle |
| 2 | Üçüncü kez aynı düzeltme | İkiden sonra temizle, yeni istem |
| 3 | Şişmiş kural dosyası | Buda; ölçüt "silsem hata yapar mı" |
| 4 | Doğrulamasız teslim | Kabul ölçütünü isteme yaz |
| 5 | Sonsuz keşif | Daralt ya da devret |
| 6 | Kırmızı görülmemiş test | Sırayı dayat |
| 7 | Kapsam kaçması | Planda kapsam dışını yaz |
| 8 | Ham günlük okutma | Filtrele; hook ile kalıcı çöz |
| 9 | Sessiz kırpma | Sayıları karşılaştır |
| 10 | Denetimsiz eklenti kurulumu | Dört soruyu sor |

---

## 7. Deneme sınavına girmeden

- **Cheatsheet'i bir kez tara** — 10 dakika, tablolara bak.
- **Kendi kaydettiğin sayılara bak**: pratiklerde doldurduğun boşluklar.
- **Zayıf kaldığın testleri hatırla.** `%80` altında kaldığın konuya dön.
- Denemeler **57 ve 63 soru**, 70 ve 75 dakika — soru başına ~70 saniye. Takılırsan **geç**.
- Sorular konu testlerinin kopyası **değil**: aynı bilgi, **farklı senaryo**.

---

## 8. Seti bitirdikten sonra

| Ne zaman | Ne yap |
|---|---|
| **Hemen** | `5.1`'deki eksen tablonu doldur, `5.2`'deki karar dosyanı yaz |
| **İlk hafta** | Kendi projende bir kural dosyası + bir hook kur |
| **İlk ay** | Bir skill ve bir alt ajan yaz; ölçüm dosyanı doldurmaya başla |
| **Üç ayda bir** | Kuralları, maliyeti ve eksen tablosunu gözden geçir |
| **Sürekli** | Haftada 15 dk **değişiklik günlüğü** |

Ve `6.6`'nın sorusunu ara ara kendine sor:

> **Bu kodu ajan olmadan yazabilir miydim?**

---

## 9. Bu setin sana vermediği şey

Dürüst kapanış: bu set bir **yöntem** verdi, bir **garanti** değil.

- Ajan yine hata yapacak — ama artık **nerede** bakacağını biliyorsun.
- Araçlar değişecek — ama **eksenler** kalıyor.
- Bu setin bir kısmı eskiyecek — **`⚠️` işaretli yerleri doğrula.**

Kalan tek şey, en başından beri aynı: **doğrulayamadığın şeyi teslim etme.**

---

## ✅ Genel denemeler

➡️ **[final-1-test.html](final-1-test.html)** — 57 soru · 70 dakika
➡️ **[final-2-test.html](final-2-test.html)** — 63 soru · 75 dakika

İkisini **farklı günlerde** çöz. Aynı gün ikisini çözmek, ikinci denemenin
ölçüm değerini düşürür.
