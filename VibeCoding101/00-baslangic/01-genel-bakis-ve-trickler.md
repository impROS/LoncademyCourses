# 00.1 — Vibe coding nedir, ne değildir — ve trickler

> **Alan:** Başlangıç — oyunun kuralları
> **Süre:** ~25 dakika okuma + 10 dakika pratik
> **Test:** [`01-test.html`](01-test.html) · 14 soru
> 📖 [Kavram sözlüğü](03-kavram-sozlugu.md) · ⚙️ [Seçim ve ayar rehberi](04-ayar-rehberi.md)

---

## Neden bu konu

Bu setin geri kalanı bir şeyi varsayıyor: terimin ne anlama geldiğini ve **neyi
vaat etmediğini** biliyorsun. Çünkü "vibe coding" adı, altındaki işi yanlış tarif
ediyor — ve bu yanlış tarif, insanların en çok zaman kaybettiği yer.

Terimi ortaya atan kişi bile bir yıl sonra geri aldı. Neyi geri aldığını anlarsan,
kalan 31 konunun neden böyle sıralandığını da anlarsın.

**Büyük fikir:** Kod yazmanın maliyeti düştü; **kodun doğru olduğunu bilmenin**
maliyeti düşmedi. Bu setin tamamı ikinci maliyetle ilgilidir.

---

## 1. ⭐ Terimin hikâyesi — ve neden önemli

Şubat 2025'te Andrej Karpathy kısa bir gönderi yazdı: kendini tamamen "vibe"a
bırakıp yapay zekâya kod yazdırdığı, "bir şeyler görüyorum, bir şeyler söylüyorum,
çalıştırıyorum, kopyala-yapıştır yapıyorum ve çoğunlukla çalışıyor" dediği bir
çalışma biçimi. Terim tuttu; 2025'in Collins sözlük yılın kelimesi seçildi.

Kritik ayrıntı: **onu tek kullanımlık hafta sonu projeleri için tarif etmişti.**
Kendisi sonradan bunu "düşünülmeden atılmış bir gönderi" diye niteledi.

Nisan 2026'da aynı kişi terimi geride bıraktığını söyledi ve yerine
**ajan mühendisliği (agentic engineering)** dedi. Ayrımı şöyle kurdu:

| | Vibe coding | Ajan mühendisliği |
|---|---|---|
| Kodu kim yazıyor | Ajan | Ajan |
| Sen ne yapıyorsun | Sonuca bakıyorsun | Ajanları yönetiyor ve **denetliyorsun** |
| Denetim düzeyi | Düşük — "çalışıyorsa tamam" | Yüksek — kanıt istiyorsun |
| Kime ne kazandırır | Yeni başlayanın **tabanını** yükseltir | Profesyonelin **tavanını** yükseltir |
| Nerede işe yarar | Atılacak prototip, deneme | Bakılacak, yaşayacak yazılım |

> ⚠️ **Tuzak:** Bu setin adı "VibeCoding", çünkü herkes bu adı arıyor. Ama
> öğrettiği şey ikinci sütun. Adı popüler olan ile öğretilmesi gereken aynı
> şey değil, ve bunu baştan bilmen gerekiyor.

**Hafıza kancası:** *Vibe coding tabanı, ajan mühendisliği tavanı yükseltir.*

---

## 2. ⭐ Her şeyin çıktığı tek kısıt: bağlam dolar

Bu setteki tavsiyelerin neredeyse tamamı tek bir teknik gerçekten türüyor:

**Ajan, konuşmanın tamamını her istekte yeniden gönderir.** Yazdığın her mesaj,
okuduğu her dosya, çalıştırdığı her komutun çıktısı aynı torbada birikir. Bu
torbaya **bağlam penceresi (context window)** deniyor ve sınırlı.

Sonuç iki katmanlı:

1. **Dolunca bitmez** — sistem eskiyi özetleyip yer açar (buna *sıkıştırma*,
   İngilizcesi *compaction* deniyor). Yani oturum devam eder.
2. **Ama dolarken bozulur** — pencere doldukça model daha önce verdiğin
   talimatları "unutmaya" ve daha çok hata yapmaya başlar.

İkinci madde kritik: **başarısızlık sessizdir.** Hata mesajı almazsın; sadece
cevaplar yavaş yavaş kötüleşir. Deneyimli kullanıcıyı acemiden ayıran şeylerin
başında, "bu oturum bozuldu, temizleyeyim" refleksinin ne zaman geldiği gelir.

Ayrıntısı `1.3`'te. Şimdilik bilmen gereken: **bağlam senin en kıt kaynağın**,
para değil.

---

## 3. Trickler — ilk günden işe yarayan altı refleks

Bunlar süsleme değil; deneyimli kullanıcıların davranışıyla acemininki
arasındaki farkın büyük kısmı bu altı maddede.

### 3.1 ⭐ Ajana kapatabileceği bir döngü ver

Ajan, **iş bitmiş göründüğünde** durur. Elinde çalıştırabileceği bir denetim
yoksa "bitmiş görünmek" tek sinyaldir — ve doğrulama işi sana kalır: her hata
senin fark etmeni bekler.

Ona geçti/kaldı üreten bir şey ver — test takımı, derleme, tip denetimi, çıktıyı
beklenen sonuçla karşılaştıran bir betik, ekran görüntüsü — döngü kendi kapanır.
Ajan işi yapar, denetimi çalıştırır, sonucu okur, geçene kadar döner.

```text
✗  "e-posta doğrulayan bir fonksiyon yaz"
✓  "validateEmail yaz. Örnek durumlar: a@b.com true, 'invalid' false,
    'user@.com' false. Yazdıktan sonra testleri çalıştır."
```

### 3.2 Kanıt iste, iddia değil

"Yaptım, çalışıyor" bir iddiadır. **Testin çıktısını, çalıştırdığın komutu ve
dönen sonucu göster** demek kanıt ister. Kanıt okumak, doğrulamayı kendin
tekrar yapmaktan hızlıdır — ve izlemediğin oturumlarda tek seçenektir.

### 3.3 İki düzeltmeden sonra düzeltme, temizle

Aynı konuda ajanı ikiden fazla düzelttiysen, bağlam başarısız denemelerle
kirlendi. Üçüncü düzeltme genelde işe yaramaz. Doğrusu: oturumu temizle ve
**öğrendiklerini içeren daha iyi bir ilk istem** yaz. Temiz bir oturum + iyi
istem, uzun bir oturum + birikmiş düzeltmelerden neredeyse her zaman iyidir.

### 3.4 Önce keşif, sonra plan, sonra kod

Kapsamı belirsiz işlerde doğrudan kod yazdırmak, **yanlış problemi çözen** kod
üretir. Sırayı ayır: ajan önce okusun ve anlatsın, sonra plan çıkarsın, sen planı
düzelt, ondan sonra yazsın.

> ⚠️ Bunun bir maliyeti var: küçük ve kapsamı net işlerde (yazım hatası, tek
> satır log ekleme, değişken adı değiştirme) planlama sadece zaman kaybettirir.
> Ölçüt basit: **değişikliği tek cümleyle tarif edebiliyorsan plan yapma.**

### 3.5 Belirsizliği sen kapat

Ajan niyetini okuyamaz. "Testleri düzelt" ile "auth modülündeki oturum zaman
aşımı testini düzelt, mock kullanma" arasındaki fark, alacağın düzeltme sayısıdır.
Dosyayı adıyla söyle, kısıtı yaz, örnek göster.

### 3.6 Ölçmediğin şeyi bilmiyorsun

"Ajan bu işi hızlandırdı mı?" sorusunun cevabı histen gelmez. Ne kadar
harcandığını, kaç turda bittiğini, kaç kez geri alındığını görebiliyorsan
karar verebilirsin. **401 · *Maliyet*** bunu öğretiyor.

---

## 4. En sık kaybedilen beş yer

Bunlar tek tek tanınabilir kalıplardır; adını bilirsen erken fark edersin.

| Kalıp | Belirtisi | Çözümü |
|---|---|---|
| **Çorba oturum** | Bir işe başladın, arada alakasız bir şey sordun, sonra geri döndün | İşler arasında bağlamı temizle |
| **Düzelt-düzelt-düzelt** | Aynı hatayı üçüncü kez düzeltiyorsun | İki denemeden sonra temizle, yeni istem yaz |
| **Şişmiş kural dosyası** | Yazdığın kurallara uyulmuyor | Kural dosyası çok uzun; kısalt (`2.1`) |
| **Güven-ama-doğrulama boşluğu** | Kod makul görünüyor ama uç durumlarda kırılıyor | Doğrulama olmadan teslim etme |
| **Sonsuz keşif** | "Şunu bir araştır" dedin, yüzlerce dosya okundu | Keşfi daralt ya da ayrı bağlama gönder (**301 · *Subagent***) |

---

## 🖥 Pratik — refleksi kendi üstünde ölç

> **Amaç:** 3.1'in farkını kendi gözünle görmek · **Süre:** 10 dk
> **💸 Maliyet:** Yok (yalnızca kendi ajan kotandan birkaç istek)

Bu pratik kurulumdan önce yapılabilir; ajanın hangisi olduğu fark etmez.

### Adımlar

1. Boş bir klasör aç: `mkdir ~/vibe-deneme && cd ~/vibe-deneme`
2. Ajanını başlat ve **doğrulamasız** istemi ver:
   ```text
   Bir Türkçe metindeki kelime sayısını sayan bir fonksiyon yaz.
   ```
3. Çıkan kodu okumadan **not al**: kaç satır, hangi uç durumları düşünmüş?
4. Bağlamı temizle (yeni oturum başlat) ve **doğrulamalı** istemi ver:
   ```text
   kelimeSay(metin) fonksiyonunu yaz. Beklenen sonuçlar:
   "merhaba dünya" → 2 ; "" → 0 ; "  çok   boşluk  " → 2 ;
   "bir-iki üç" → 2. Testleri de yaz ve çalıştır, geçtiğini göster.
   ```
5. İkisini karşılaştır.

- [ ] **Kontrol:** İkinci denemede ajan testleri **çalıştırdı** ve sonucu gösterdi mi?
- [ ] **Kaydet:** Birinci denemenin kaçırdığı uç durum sayısı: ______

### Temizlik
6. `rm -rf ~/vibe-deneme` — bu klasör bir daha gerekmeyecek.
- [ ] **Kontrol:** `ls ~/vibe-deneme` "No such file or directory" diyor.

---

## Nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — Terimin karıştırılması**
> *"Vibe coding yapıyoruz, kimse kodu okumuyor, üretime çıkıyor."*
> → Bu vibe coding'in **tarif edildiği kullanım değil**; tek kullanımlık
> prototip için tarif edilmişti. Üretime çıkan kod denetim ister.

**Kalıp 2 — Sessiz bozulma**
> *"Sabah harika çalışıyordu, öğleden sonra saçmalamaya başladı."*
> → Model değişmedi; **bağlam doldu**. Temizle.

**Kalıp 3 — Doğrulanmamış teslim**
> *"Ajan 'tamamdır, çalışıyor' dedi ama üretimde patladı."*
> → Ajan "bitmiş göründüğünde" durur. Kanıt istenmediyse iddia alırsın.

**Kalıp 4 — Yanlış problem**
> *"İstediğimi yapmadı, tamamen başka bir şey yazdı."*
> → Belirsizlik kapatılmamış. Dosya adı, kısıt ve kabul ölçütü verilmemiş.

---

## 60 saniyelik özet

- **Vibe coding**: Şubat 2025, Karpathy, **atılacak projeler** için tarif edildi.
- Aynı kişi Nisan 2026'da terimi bıraktı; yerine **ajan mühendisliği** dedi.
- Ayrım: vibe coding **tabanı**, ajan mühendisliği **tavanı** yükseltir.
- Her şeyin çıktığı kısıt: **bağlam penceresi dolar ve dolarken performans bozulur.**
- Bozulma **sessizdir** — hata mesajı değil, kalite düşüşü olarak gelir.
- Altı refleks: doğrulama ver · kanıt iste · iki düzeltmeden sonra temizle ·
  keşif-plan-kod · belirsizliği sen kapat · ölç.
- Beş kaybetme kalıbı: çorba oturum, düzelt-düzelt, şişmiş kural dosyası,
  doğrulama boşluğu, sonsuz keşif.
- Kod yazmak ucuzladı; **kodun doğru olduğunu bilmek** ucuzlamadı.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. Karpathy'nin terimi geri alma sebebi neydi ve yerine ne koydu? İkisi
   arasındaki fark hangi tek kelimeyle özetlenir?
2. "Bağlam dolduğunda oturum biter" cümlesi neden yanlış? Gerçekte ne olur ve
   bunun neden fark edilmesi zordur?
3. Ajana doğrulama vermenin, "kodu sonra ben kontrol ederim" demekten farkı ne?
   İki farklı sonucu say.
4. Hangi durumda plan modu kullanmamak doğrudur? Ölçütü tek cümleyle yaz.
5. Aynı hatayı üçüncü kez düzeltmek üzeresin. Neden durman gerekiyor ve
   bunun yerine ne yapmalısın?

➡️ **Cevaplar:** [`cevaplar.md#001-vibe-coding-nedir`](cevaplar.md#001-vibe-coding-nedir) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`02-kurulum.md`](02-kurulum.md) — ortamı kur, örnek projeyi oluştur.
