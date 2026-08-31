# 00.3 — Plan, kota ve maliyet: ne satın alınmalı

> **Alan:** Başlangıç — para kararı (konu değil, hazırlık)
> **Süre:** ~15 dakika
> **Test:** yok — ölçme alışkanlığı `6.4`'te test edilecek
> 📖 [Kavram sözlüğü](03-kavram-sozlugu.md) · ⚙️ [Seçim ve ayar rehberi](04-ayar-rehberi.md)

---

## Neden bu konu

Bu alanda en çok para, **ölçmeden verilen kararlarda** kayboluyor: ihtiyaç
duyulmayan üst abonelik, aynı anda üç araca ödeme, "en iyi promptlar" paketleri.

Karar vermek için bilmen gereken tek şey var: **maliyet nereden geliyor.**
Onu bilirsen fiyat listesi ezberlemene gerek kalmıyor — ve fiyat listeleri
zaten üç ayda bir değişiyor.

**Büyük fikir:** Faturayı model değil, **bağlamın büyüklüğü** belirler.

---

## 1. ⭐ Maliyet nereden geliyor

Ajanlar, gönderilen ve üretilen metin parçası (**token**) başına ücretlendirilir.
Kritik nokta şu: **ajan her istekte konuşmanın tamamını yeniden gönderir.**

Bu tek cümle her şeyi açıklar:

- Uzun süren oturumda yazdığın **tek satırlık soru bile** o güne kadarki tüm
  geçmişi taşır. Ucuz görünen soru ucuz değildir.
- Ajanın okuduğu her dosya, çalıştırdığı her komutun çıktısı, o oturumun geri
  kalanında **her istekte** tekrar tekrar gider.
- Bu yüzden bağlamı temizlemek yalnızca kaliteyi değil, **maliyeti** de düzeltir.

Yükü hafifleten mekanizma **istem önbelleği**: değişmeyen önek yeniden
işlenmez, önbellekten okunur ve çok daha ucuza gelir. Ama önbelleğin bir ömrü
var — uzun bir aradan sonraki ilk istek önbelleği ıskalar ve tüm geçmiş yeniden
işlenir. *"Sabah açık bıraktım, öğlen bir şey sordum, kota uçtu"* şikâyetinin
mekanizması budur.

---

## 2. İki ödeme biçimi

| | Abonelik | Kullandıkça öde |
|---|---|---|
| Ne | Aylık ücret, kotalı kullanım | Token başına fatura |
| Öngörülebilirlik | ✅ Aylık gider sabit | ❌ Kullanıma göre değişir |
| Sınır | Kota dolunca beklersin | Sınır yok — bu iyi değil, tehlikeli |
| Kime | Düzenli, kişisel kullanım | Otomasyon, ekip, değişken yük |
| Tuzağı | Az kullanıp fazla ödemek | Farkında olmadan çok harcamak |

Kişisel öğrenme için: **abonelik.** Otomasyon ve sürekli tümleştirme hattı
kuracaksan kullandıkça ödeme daha uygun olabilir — ama **harcama sınırı
koymadan başlama.**

---

## 3. Bu set için ne gerekiyor

| | Karar | Gerekçe |
|---|---|---|
| ✅ **Şart** | Bir ajan aracına erişim. Ücretsiz katman ya da giriş seviyesi abonelik yeter. | 32 konunun pratikleri küçük; ağır kullanım gerekmiyor |
| ✅ **Şart** | Terminal, Node, git | Zaten var |
| 🟡 **Belki** | Günde 2+ saat ajanla çalışıyorsan üst abonelik | Ama **ölçtükten sonra** — `6.4` bunu öğretiyor |
| ⛔ **Alma** | "Vibe coding kursu / mentorluk" paketleri | Bu setin `5.3` konusu bedava ve güncel kaynakları veriyor |
| ⛔ **Alma** | "500 en iyi prompt" listeleri, ücretli istem pazarları | İstem yazmak `1.2`'de öğretiliyor; ezber listeler eskir |
| ⛔ **Alma** | Aynı anda birden çok araç aboneliği | Önce `05` bölümünü bitir, sonra birini seç |
| ⛔ **Acele alma** | Yıllık ödeme indirimleri | Bu alanda araçlar 6 ayda yer değiştiriyor |

> 💸 Bu sette kredi kartı isteyen, bulut kaynağı açtıran **hiçbir pratik yok.**
> Ücret doğuran tek şey kendi ajan kotan.

---

## 4. Kotaya takılmadan çalışmak

Öğrenirken kotayı en çok yiyen üç davranış — ve karşılığı:

| Davranış | Neden pahalı | Yerine |
|---|---|---|
| Tek oturumda bütün gün çalışmak | Her istek tüm geçmişi taşır | İşler arasında bağlamı temizle |
| Ajana koca dosyaları okutmak | Okunan her şey oturum boyunca taşınır | Dosyayı adıyla ver, ilgili bölümü söyle |
| Basit işte en büyük modeli ve en yüksek çabayı kullanmak | Düşünme tokenı çıktı olarak faturalanır | İşe göre model ve çaba düzeyi seç |

Ölçmek için, oturum içinde:

```text
/usage
```

Bu ekran hem bu oturumun sayılarını hem — abonelikteysen — kotanın ne kadarını
kullandığını gösterir. `6.4` bunu ekip ölçeğine taşıyor.

- **Kaydet:** Bu setin ilk haftasını bitirdiğinde `/usage` ne diyor? ______
  (Karşılaştırma noktası olacak; hafta 4'te tekrar bakacaksın.)

---

## Sık karıştırılanlar

| Karıştırılan | Doğrusu | Neden diğeri değil |
|---|---|---|
| "Büyük model = pahalı, küçük model = ucuz" | Belirleyici olan **bağlamın büyüklüğü**; şişmiş bağlamla küçük model de pahalıya gelir | Model fiyatı çarpandır, çarpılan şey token sayısıdır |
| "Boşta duran oturum bir şey harcamaz" | Zamanlanmış görev, hedef denetimi ya da başka oturumdan mesaj **boştayken de** istek başlatabilir | Boşta durmak, bağlantısız olmak demek değil |
| "Kota doldu, model değiştireyim" | Oturum/haftalık kota **tüm modelleri kapsar**; model değiştirmek kurtarmaz. Modele özel sınır ise değiştirmek işe yarar | İki farklı sınır var; mesajın hangisini söylediğine bak |
| "Ücretsiz katman öğrenmeye yetmez" | Bu set için genelde yeter | Pratikler kasten küçük tutuldu |

---

## 60 saniyelik özet

- Maliyet **token** üzerinden; ajan her istekte **tüm konuşmayı** gönderir.
- Bu yüzden bağlamı temizlemek hem kaliteyi hem maliyeti düzeltir.
- **İstem önbelleği** tekrarı ucuzlatır; uzun aradan sonra ilk istek pahalıdır.
- Abonelik = öngörülebilir; kullandıkça öde = esnek ama **sınır koymadan başlama**.
- Bu set için giriş seviyesi erişim yeter; hiçbir pratik ücret doğurmuyor.
- Üst plana **ölçtükten sonra** geç. Ölçüm komutu: `/usage`.
- "Prompt paketi", "vibe coding kursu" satın alma; `5.3` bedava kaynakları veriyor.

---

## Sırada ne var
➡️ [`../01-temeller/1.1-ajan-dongusu.md`](../01-temeller/1.1-ajan-dongusu.md) —
kurulum bitti, asıl konu başlıyor.
