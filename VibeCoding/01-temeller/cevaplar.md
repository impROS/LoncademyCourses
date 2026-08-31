# 01 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 1.1 Ajan döngüsü

### Soru 1 — Döngünün dört adımını sırayla yaz. Hangi adım bağlamı büyütür ve neden bu, sonraki turları da etkiler?

**Kısa cevap:** *bağlamı oku → araç çağır → sonucu bağlama ekle → tekrar değerlendir.*
Bağlamı büyüten adım **üçüncüsü**; büyüttüğü şey kalıcıdır, çünkü model her turda
bağlamın tamamını yeniden okur.

**Ayrıntı:** Araç sonucu bir kez eklenir, sonra oturumun sonuna kadar **her
istekte** tekrar gönderilir. Yani 2000 satırlık bir test çıktısı bir kerelik
maliyet değil, o oturumun geri kalanına yayılan **sabit bir yük**tür.

| Adım | Bağlama etkisi |
|---|---|
| Bağlamı okuma | Yok (okuma, ekleme değil) |
| Araç çağırma | Çağrının kendisi küçük |
| **Sonucun eklenmesi** | **Kalıcı büyüme** |
| Tekrar değerlendirme | Yok |

📌 **Sık yapılan hata:** Araç sonucunu "kullanılıp atılan" bir şey sanmak. Bu
sanı, geniş keşfin neden hem pahalı hem zararlı olduğunu görmeyi engeller.

🔗 [1.1 §1](1.1-ajan-dongusu.md)

---

### Soru 2 — Sen hiçbir şey yazmadan bağlamda ne var? En az beş kalem say ve hangisinin boyutunu senin kontrol ettiğini belirt.

**Kısa cevap:** Sistem talimatı, ana kural dosyası, kural dosyaları, otomatik
bellek dizini, ortam bilgisi, git durumu, MCP araç adları, skill açıklamaları.
**Boyutunu sen belirlediklerin:** kural dosyaları, skill sayısı, bağlı MCP sunucusu sayısı.

**Ayrıntı:**

| Kalem | Kim koyar | Senin kontrolünde mi |
|---|---|---|
| Sistem talimatı | Araç | ❌ |
| Ana kural dosyası | **Sen** | ✅ tamamen |
| Kural dosyaları | **Sen** | ✅ |
| Otomatik bellek dizini | Ajan | 🟡 düzenlenebilir |
| Ortam + git durumu | Araç | ❌ |
| MCP araç adları | **Sen** | ✅ hangi sunucuyu bağladığın |
| Skill açıklamaları | **Sen** | ✅ kaç skill tanımladığın |

Bu, şişmiş bir kural dosyasının neden **her isteğe binen bir vergi** olduğunu
açıklar: bir kez değil, oturum boyunca her turda ödenir.

📌 **Sık yapılan hata:** `/context` ekranını yalnızca "bağlam dolduğunda" açmak.
İlk gün açıp sabit yükü görmek, kural dosyası kararlarını değiştirir.

🔗 [1.1 §3](1.1-ajan-dongusu.md)

---

### Soru 3 — "Ajan bitti dediyse bitmiştir" neden güvenilmez? Yanlış pozitif ve yanlış negatifin birer örneğini ver.

**Kısa cevap:** Çünkü bitiş kararını **model verir**; dışarıdan bir ölçüt
koymazsan "bitmiş görünüyor" tek sinyaldir.

**Ayrıntı:**

| | Ne demek | Örnek |
|---|---|---|
| **Yanlış pozitif** | Bitmemişken bitmiş saymak | Kod yazılır, hiç çalıştırılmadan "hazır" denir |
| **Yanlış negatif** | Bitmişken devam etmek | "Bu kod tabanını iyileştir" gibi kapsamsız istekte dönüp durmak |

Yanlış pozitif çok daha yaygın ve çok daha pahalıdır, çünkü **sessizdir**:
elinde çalışan bir şey olduğunu sanırsın.

📌 **Sık yapılan hata:** Çözümü "daha iyi istem yazmak"ta aramak. İstem yardımcı
olur ama garanti vermez; garanti, ajanın çalıştırabileceği bir denetimden gelir.

🔗 [1.1 §4](1.1-ajan-dongusu.md) · [1.5](1.5-dogrulama-refleksi.md)

---

### Soru 4 — Bir kuralın kesin olarak uygulanmasını istiyorsun. Kural dosyasına yazmak neden yetmez, yerine ne kullanırsın?

**Kısa cevap:** Kural dosyası **bağlamdır** — model onu okur ve genelde uyar, ama
uyması garanti değildir. Kesinlik isteyen kural **hook** olmalıdır.

**Ayrıntı:**

| Deterministik (garanti) | Olasılıksal (rica) |
|---|---|
| Hook | Kural dosyasındaki talimat |
| İzin kuralları | Skill içindeki adımlar |
| Kum havuzu sınırları | "Şunu asla yapma" cümleleri |
| Betiğin çıkış kodu | Modelin "bitti" kararı |

*"Asla `.env` düzenleme"* kural dosyasında bir ricadır; dosya yazımından önce
çalışan bir hook'ta **engellemedir**.

📌 **Sık yapılan hata:** Uyulmayan kuralı büyük harfle yazıp güçlendirmeye
çalışmak. Vurgu ihtimali artırır, garanti vermez — ve her satırı vurgularsan
hiçbiri öne çıkmaz.

🔗 [1.1 §5](1.1-ajan-dongusu.md) · [4.3](../04-kendi-aletini-yap/4.3-hooklar.md)

---

### Soru 5 — `npm test` çıktısını filtrelemeden almak neden yalnızca o turu değil, oturumun tamamını etkiler?

**Kısa cevap:** Çünkü bağlam **birikir**: giren çıktı sonraki her turda yeniden
gönderilir.

**Ayrıntı:** Sayılarla düşün. 2000 satırlık bir çıktı kabaca on binlerce token
eder. O oturumda 20 tur daha yaparsan, o çıktı **20 kez daha** gönderilir. Etkisi
üç yerde birden görünür: maliyet, hız ve bağlam doluluğu — yani sessiz bozulma.

Çözüm: çıktıyı kaynağında daralt (yalnızca başarısızlıkları göster), ya da
komutu ayrı bağlamda çalışan bir yardımcıya çalıştırt.

📌 **Sık yapılan hata:** Sorunu "çıktı uzun, okuması zor" diye görmek. Asıl sorun
okunabilirlik değil, **kalıcı yük**.

🔗 [1.1 §2](1.1-ajan-dongusu.md) · [1.3](1.3-baglam-yonetimi.md)

---

## 1.2 İstem yazma

### Soru 1 — "Testleri düzelt" isteminde dört parçadan hangileri eksik? Her biri için sonucunu yaz.

**Kısa cevap:** Dördü de zayıf; **kaynak, kısıt ve kabul ölçütü tamamen eksik**,
hedef ise belirsiz.

**Ayrıntı:**

| Parça | Durum | Sonucu |
|---|---|---|
| Hedef | Belirsiz ("düzelt" nasıl?) | Ajan hangi testin, hangi anlamda düzeleceğini tahmin eder |
| Kaynak | Yok | Test dosyalarını aramak için geniş keşif yapar, bağlam dolar |
| Kısıt | Yok | Testi **değiştirerek** geçirebilir; mock ekleyebilir |
| Kabul ölçütü | Yok | Çalıştırmadan "düzelttim" der |

Üçüncü satır en tehlikelisi: kısıt yoksa "testleri geçir" isteğinin en kısa
çözümü çoğu zaman **testi değiştirmektir**.

📌 **Sık yapılan hata:** Eksikliği yalnızca "kısa yazmışım" diye görmek. Sorun
uzunluk değil; dört parçadan üçünün hiç olmaması.

🔗 [1.2 §1](1.2-istem-yazma.md)

---

### Soru 2 — Ajanın kendi bağlamını toplamasını istemek ne zaman iyi, ne zaman kötü fikirdir?

**Kısa cevap:** **İyi:** ne arayacağını sen de tam bilmiyorsan.
**Kötü:** kapsam belirsizse — çektiği her şey bağlama girer ve orada kalır.

**Ayrıntı:**

| İyi | Kötü |
|---|---|
| "Şu komutu çalıştır, çıktısına bak" (tek komut, sınırlı çıktı) | "Projeyi bir incele" (sınırsız keşif) |
| "Bu kütüphanenin belgesini çek ve X'i nasıl yaptığına bak" | "İnternette araştır" (kapsamsız) |
| Ayrı bağlamda çalışan yardımcıya devredilmiş keşif | Ana oturumda geniş dosya taraması |

Ayırt edici: **üst sınır var mı?** Varsa güvenli, yoksa daralt ya da devret.

📌 **Sık yapılan hata:** Esnekliği bedava sanmak. Ajanın kendi bağlamını
toplaması güçlü bir yetenektir, ama faturası bağlam olarak gelir.

🔗 [1.2 §3](1.2-istem-yazma.md)

---

### Soru 3 — Büyük bir özelliğe başlarken neden önce ajanın sana soru sormasını istersin? Belirtimden sonra neden yeni oturum?

**Kısa cevap:** Çünkü belirsizlik **senin kafanda** da var. Sorgulama, düşünmediğin
uç durumları ve ödünleşimleri açığa çıkarır. Yeni oturum ise **bağlam kararıdır**:
belirtim artık diskte, tartışma turları ise yalnızca yer kaplıyor.

**Ayrıntı:** Sorgulamanın çıkardığı şeyler genelde şunlar: arayüz davranışı,
hata durumları, eşzamanlılık, göç yolu, kapsam dışı. Bunları kod yazılmadan
önce kararlaştırmak, sonradan düzeltmekten kat kat ucuz.

İyi bir belirtimin üç özelliği:
1. Dokunulacak **dosya ve arayüzleri adıyla** sayar.
2. **Kapsam dışını** açıkça yazar.
3. **Uçtan uca bir doğrulama adımıyla** biter.

📌 **Sık yapılan hata:** Belirtime uygulamanın satır satır nasıl kodlanacağını
yazdırmak. Belirtim **ne olacağını** tarif eder; nasıl yazılacağını dikte etmek
onu kırılganlaştırır ve ajanın kendi yolunu bulmasını engeller.

🔗 [1.2 §4](1.2-istem-yazma.md)

---

### Soru 4 — Belirsiz istem hangi durumda doğru araçtır? Ayırt edici soruyu yaz.

**Kısa cevap:** Ayırt edici soru: **yanlış anlaşılmanın maliyeti ne?** Ucuzsa
belirsiz sor, pahalıysa kesinleştir.

**Ayrıntı:** Belirsizliğin bilgi getirdiği üç durum:

- **Keşif:** *"bu dosyada neyi iyileştirirdin?"* — aklına gelmeyeni gösterir.
- **Varsayım sınama:** Problemi kısıtlamadan önce ajanın nasıl yorumladığını
  görmek, senin varsayımını da sınar.
- **Öğrenme:** Tanımadığın kod tabanında *"loglama nasıl çalışıyor?"* sormak,
  kıdemli bir geliştiriciye sormakla aynı şey.

Ortak nokta: üçünde de **değişiklik istenmiyor**, bilgi isteniyor. Yanlış
anlaşılmanın maliyeti bir turdan ibaret.

📌 **Sık yapılan hata:** "Her istem kesin olmalı" kuralını mutlaklaştırmak.
Kesinlik, yanlış anlaşılmanın pahalı olduğu yerde erdemdir; keşifte körlüktür.

🔗 [1.2 §5](1.2-istem-yazma.md)

---

### Soru 5 — "Kabul ölçütü koydum: testleri de yazsın dedim." Bu neden yetersiz?

**Kısa cevap:** Test **yazmak** ile test **çalıştırıp sonucu okumak** farklı
şeyler. Yalnızca yazılan test döngüyü kapatmaz.

**Ayrıntı:** Kabul ölçütü, ajanın **çalıştırıp okuyabileceği** bir sinyal
üretmelidir. Test yazmak yalnızca daha fazla koddur; o kod çalıştırılmadıkça
hatayı yakalamaz. İstemde eksik olan beş kelime: *"çalıştır ve çıktıyı göster."*

Bir adım ötesi: *"önce hatayı gösteren başarısız bir test yaz"* demek. Böylece
testin gerçekten bir şey ölçtüğü de kanıtlanır — her zaman geçen bir test,
hiçbir şey ölçmeyen bir testtir.

📌 **Sık yapılan hata:** "Testleri de yaz" dedikten sonra yeşil çıktıyı görüp
rahatlamak. Testin **başta kırmızı** olduğunu görmediysen, neyi ölçtüğünü
bilmiyorsun.

🔗 [1.2 §2.4](1.2-istem-yazma.md) · [1.5](1.5-dogrulama-refleksi.md)

---

## 1.3 Bağlam penceresi

### Soru 1 — "Dolma" ile "bozulma" farkı. Hangisi görünür, hangisinin çözümü sende?

**Kısa cevap:** **Dolma** görünür ve otomatik yönetilir (sıkıştırma).
**Bozulma** görünmez ve çözümü sende: **temizlemek**.

**Ayrıntı:**

| | Dolma | Bozulma |
|---|---|---|
| Ne | Sınıra yaklaşmak | Doldukça kalitenin düşmesi |
| Ne zaman | Sınıra yakın | **Sınırdan çok önce** |
| Belirti | Sıkıştırma bildirimi | Talimat kaçırma, tekrarlanan hata |
| Görünür mü | ✅ | ❌ |
| Kim çözer | Araç | **Sen** |

Bozulmanın görünmezliği bir eşik meselesi değil, bir **derece kaybı** meselesi:
program çökmez, cevap biraz daha kötüleşir.

📌 **Sık yapılan hata:** Kaliteyi modelin "kötü günü" sanıp aynı oturumda daha
sert istemlerle zorlamak. Bu bağlamı daha da doldurur — sorunu büyütür.

🔗 [1.3 §2](1.3-baglam-yonetimi.md)

---

### Soru 2 — Sıkıştırmadan sonra hangi üç şey geri yüklenir, hangi bir şey kalıcı kaybolur? Çıkan kural ne?

**Kısa cevap:** **Geri yüklenir:** ana kural dosyası ve kapsanmamış kurallar ·
otomatik bellek · plan modunda yazılan plan.
**Kalıcı kaybolur:** yalnızca sohbette söylediklerin.
**Kural:** *kalıcı olması gereken talimatı sohbette bırakma, dosyaya yaz.*

**Ayrıntı:**

| Ne | Sonrası |
|---|---|
| Ana kural dosyası, kapsanmamış kurallar | Diskten yeniden yüklenir |
| Otomatik bellek | Yeniden yüklenir |
| Plan | Yeniden yüklenir |
| Yola kapsanmış kurallar | Eşleşen dosya tekrar okununca döner |
| Okunan dosyalar | **Yalnızca son birkaçı** geri okunur |
| Skill gövdeleri | Yeniden eklenir ama **kırpılabilir** (başı korunur) |
| **Sohbette söylenenler** | **Özete karışır, ayrıntı gider** |

📌 **Sık yapılan hata:** Sıkıştırma sonrası ajanın "unutmasına" şaşırmak.
Unutmuyor — o bilgi hiçbir zaman diske yazılmamıştı.

🔗 [1.3 §3](1.3-baglam-yonetimi.md)

---

### Soru 3 — Keşif isteği bağlamı neden bu kadar hızlı doldurur ve nasıl önlersin?

**Kısa cevap:** Çünkü okunan **her dosya** bağlama girer ve orada kalır; kapsamsız
keşifte okunacak dosya sayısının üst sınırı yoktur. Önleme: **kapsamı daralt**
ya da **ayrı bağlama devret**.

**Ayrıntı:** İki çözümün farkı önemli:

| | Daraltma | Devretme |
|---|---|---|
| Nasıl | Dosya/dizin adı ver, soruyu kesinleştir | Ayrı bağlamda çalışan yardımcıya ver |
| Kazanç | Daha az okuma | Okuma **başka pencerede** kalır |
| Ne zaman | Nereye bakılacağını biliyorsan | Gerçekten geniş arama gerekiyorsa |

Yani "geniş arama gerekiyor" bahane değil: gerekiyorsa devret, ana bağlama
yalnızca özet gelsin.

📌 **Sık yapılan hata:** Keşfi daraltmayı "ajanı kısıtlamak" sanmak. Kısıtlanan
şey okuma değil, **senin penceren**.

🔗 [1.3 §4.3](1.3-baglam-yonetimi.md) · [4.2](../04-kendi-aletini-yap/4.2-subagent.md)

---

### Soru 4 — Sıkıştırma ile temizleme farkı. İlgisiz işe geçerken hangisi doğrudur, neden?

**Kısa cevap:** Sıkıştırma **özetler**, temizleme **siler**. İlgisiz işe geçerken
doğru olan **temizlemektir**, çünkü eski işin özeti bile yeni işe yaramaz.

**Ayrıntı:**

| | Sıkıştırma | Temizleme |
|---|---|---|
| Geçmişe ne olur | Özete dönüşür | Silinir |
| Ne zaman | Aynı iş sürüyor, yer bitti | **İş değişti** |
| Kalan yük | Özet kadar | Sıfır |
| Maliyeti | Özetleme isteği büyük bir istektir | **Bedava** |

Son satır çoğu kişinin bilmediği ayrıntı: sıkıştırma, özetlediği konuşmayı
okuduğu için **kendisi pahalı bir istektir**. Temizleme hiçbir şeye mal olmaz.

📌 **Sık yapılan hata:** Temizlemeyi "ilerlemeyi çöpe atmak" sanmak. Kod diskte
ve git'te duruyor; atılan şey yalnızca konuşma.

🔗 [1.3 §4.1](1.3-baglam-yonetimi.md)

---

### Soru 5 — "1 milyon tokenlık pencerem var, bağlam yönetimine gerek yok." İki hata nerede?

**Kısa cevap:** (1) Bozulma ortadan kalkmaz, **ertelenir** — çünkü bozulma
doluluk **oranıyla** ilgilidir, mutlak sayıyla değil. (2) Dolan geniş pencere
**daha pahalıdır**: her istek o yükü taşır.

**Ayrıntı:** Geniş pencere gerçek bir kazanım — büyük dosyalarla çalışmayı ve
uzun oturumları mümkün kılar. Ama iki şeyi değiştirmez:

- Model yine **her turda tamamını** okur; doluluk arttıkça dikkat dağılır.
- Her istek yine **tüm bağlamı** taşır; büyük pencere = büyük fatura.

Yani geniş pencere, bağlam yönetimini gereksiz kılmaz — **daha değerli** kılar,
çünkü yanlış yönetildiğinde maliyeti de büyür.

📌 **Sık yapılan hata:** Pencere büyüklüğünü bir "sorun çözüldü" işareti sanmak.
Doğrusu: sorunu erteleyen ve pahalılaştıran bir kaynak artışı.

🔗 [1.3 §5](1.3-baglam-yonetimi.md)

---

## 1.4 İzinler ve plan modu

### Soru 1 — İzin kipi ile kum havuzunun sorusunu yaz. Hangisi onayla aşılabilir?

**Kısa cevap:** İzin kipi: *"bu eylemden önce sorayım mı?"* Kum havuzu: *"bu eylem
neye erişebilsin?"* **Kip onayla aşılabilir; kum havuzu aşılamaz** — onu işletim
sistemi zorlar.

**Ayrıntı:**

| | İzin kipi | Kum havuzu |
|---|---|---|
| Belirlediği | Onay akışı | Erişim sınırı |
| Zorlayan | Ajan yazılımı | **İşletim sistemi** |
| Sen aşabilir misin | ✅ Onay vererek | ❌ Hayır |
| Yanlış ayarın sonucu | Soru yorgunluğu ya da körlemesine onay | Ajan çalışamaz ya da fazla erişir |

İkisi bağımsızdır ve **birlikte** kullanılır: kum havuzu açıkken soru sayısı
düşer, çünkü sınır zaten dışarıdan kurulmuştur.

📌 **Sık yapılan hata:** İkisini tek bir "güvenlik ayarı" sanmak — sonra
"onaylıyorum ama neye eriştiğini bilmiyorum" durumuna düşmek.

🔗 [1.4 §1](1.4-izinler-ve-plan-modu.md)

---

### Soru 2 — `bypassPermissions` kullanan bir ekipte `.env` okumasını engelleyen kural işler mi?

**Kısa cevap:** **Evet.** Engelleme kuralları **her kipte** geçerlidir.

**Ayrıntı:** En üst kipte anlamını yitiren şey izin **verme** kurallarıdır —
zaten her şey serbest olduğu için ek bir izne gerek kalmaz. Engelleme ise
bağımsız bir katmandır ve kip yükseldikçe **daha da değerli** hâle gelir:
serbestliğin içindeki tek sabit sınırdır.

Hiçbir kipin otomatik onaylamadığı başka şeyler de var: açıkça "sor" işaretli
araçlar, kullanıcı etkileşimi gerektiren araçlar, kritik yollardaki silmeler.

📌 **Sık yapılan hata:** "En üst kip = kural yok" sanmak. Doğrusu: en üst kip =
**izin verme** kuralları anlamsız, **engelleme** kuralları yürürlükte.

🔗 [1.4 §2](1.4-izinler-ve-plan-modu.md)

---

### Soru 3 — Plan modunu "güvenlik önlemi" saymak neden hatalı? Gerçek güvenlik nereden gelir?

**Kısa cevap:** Plan modu **belirsizlik** aracıdır — ne yapılacağını netleştirir.
Güvenlik, **izin kipi** ve **kum havuzundan** gelir; yani ne yapılabileceğinden.

**Ayrıntı:** Plan modu değişiklikleri plan onaylanana kadar durdurur, bu gerçek
bir kısıttır — ama tasarım amacı bu değildir ve yetki modelinin yerine geçmez.
İki farklı soruyu ayır:

| Soru | Aracı |
|---|---|
| **Ne** yapılacak? | Plan modu |
| **Ne** yapılabilir? | İzin kipi + izin kuralları + kum havuzu |

Plan modundayken de ajan okuma yapar, komut çalıştırabilir (yapılandırmaya göre)
ve dış içerik görebilir — yani istem enjeksiyonu yüzeyi kapanmaz.

📌 **Sık yapılan hata:** "Plan modundayım, bir şey bozamaz" diyerek izin
kurallarını hiç kurmamak.

🔗 [1.4 §3](1.4-izinler-ve-plan-modu.md)

---

### Soru 4 — `Bash(git diff *)` ile `Bash(git diff*)` farkı ve önemi.

**Kısa cevap:** **Boşluk anlamlıdır.** Boşluklu biçim yalnızca `git diff ` ile
başlayanları kapsar; boşluksuz biçim `git diff-index` gibi **başka komutları da**
içine alır.

**Ayrıntı:**

| Desen | Eşleşir | Eşleşmez |
|---|---|---|
| `Bash(git diff *)` | `git diff HEAD`, `git diff --stat` | `git diff-index` |
| `Bash(git diff*)` | yukarıdakiler **+ `git diff-index`, `git difftool`** | — |
| `Bash(*)` | ⛔ her şey | — |

Önemi: izin listesi bir **güvenlik sınırıdır**. Kazara genişleyen bir desen,
onayladığını sandığın kümeden fazlasını serbest bırakır. Son satır ise izin
sistemini kapatmanın uzun yoludur.

📌 **Sık yapılan hata:** Desenleri "yaklaşık eşleşme" gibi düşünmek. Eşleşme
karakter karakterdir; yıldız yalnızca kaldığı yerden sonrasını serbest bırakır.

🔗 [1.4 §4](1.4-izinler-ve-plan-modu.md)

---

### Soru 5 — Etkileşimsiz kipte hangi güvenlik davranışı kaybolur ve buna karşı ne yaparsın?

**Kısa cevap:** **Güven onayı sorulmaz.** Depodaki ayar dosyaları, hook'lar ve
dış bağlantı tanımları sorulmadan devreye girebilir. Karşılığı: **çıplak
başlatma** ve **dar izin listesi**.

**Ayrıntı:** Etkileşimsiz kipte soracak kimse yoktur, dolayısıyla ilk kez
çalıştırılan bir depoda güven diyaloğu görünmez. Bu, klonlanan bir depodaki
yapılandırmanın senin makinende sessizce çalışması demektir.

Karşı önlemler:

| Önlem | Ne yapar |
|---|---|
| Çıplak başlatma bayrağı | Hook, skill, eklenti, bağlantı tanımı ve bellek yüklenmez |
| `dontAsk` kipi | İzin verilmeyen her şey reddedilir |
| Açık ve dar araç listesi | Yalnızca gereken araçlar |
| Yalıtılmış çalıştırma | Zarar yüzeyi konteynerle sınırlanır |

📌 **Sık yapılan hata:** Yerelde güvenli çalışan bir kurulumu olduğu gibi
otomasyona taşımak. Yereldeki güvenliğin bir kısmı **sana sorulan sorulardan**
geliyordu; orada o sorular yok.

🔗 [1.4 §5](1.4-izinler-ve-plan-modu.md) · [4.7](../04-kendi-aletini-yap/4.7-otomasyon-ve-sdk.md)

---

## 1.5 Doğrulama refleksi

### Soru 1 — Dört sıkılık düzeyini sırala. Hangisi kurulumsuz işe yarar, hangisi gözetimsiz çalışmayı mümkün kılar?

**Kısa cevap:** **1.** istem içi ölçüt → **2.** oturum hedefi → **3.** durdurma
hook'u → **4.** ikinci göz.
**Kurulumsuz:** birinci. **Gözetimsiz çalışmayı mümkün kılan:** üçüncü ve dördüncü.

**Ayrıntı:**

| Düzey | Kesinlik | Kurulum | Ne zaman |
|---|---|---|---|
| İstem içi | Düşük | Yok | Her gün |
| Oturum hedefi | Orta | Az | Uzun, çok adımlı işler |
| Durdurma hook'u | **Yüksek** | Orta | Gözetimsiz çalışma |
| İkinci göz | Yüksek, farklı açıdan | Az | Teslim öncesi |

Düzey yükseldikçe **senin dikkatine bağımlılık azalır** — kazanç budur, kesinlik
tek başına değil.

📌 **Sık yapılan hata:** Her işe dördünü birden kurmak. Düzey, işin riskine göre
seçilir; küçük bir düzeltme için hook yazmak israftır.

🔗 [1.5 §2](1.5-dogrulama-refleksi.md)

---

### Soru 2 — "Bütün testler geçiyor" neden "çalışıyor" demek değildir? Örnek ver.

**Kısa cevap:** Çünkü testler yalnızca **yazıldıkları şeyi** ölçer. Örnek:
tüm birim testleri geçer ama uygulama açılışta bir yapılandırma değeri eksik
olduğu için hiç ayağa kalkmaz.

**Ayrıntı:** Testlerin kaçırdığı klasik alanlar:

- Uygulama başlatma ve yapılandırma yükleme
- Bileşenler arası gerçek tümleştirme (testte sahtelenmiş olabilir)
- Ortam farkları (yol, saat dilimi, dosya izinleri)
- Arayüzün gerçekten göründüğü hâl

Bu yüzden **çalıştırıp deneme** testin yerine değil, **üstüne** gelir. Birçok
araçta uygulamayı gerçekten ayağa kaldıran hazır bir doğrulama akışı vardır.

📌 **Sık yapılan hata:** Yeşil test çıktısını teslim kanıtı saymak. Yeşil test,
"yazdığım testler geçti" demektir — daha fazlası değil.

🔗 [1.5 §1](1.5-dogrulama-refleksi.md)

---

### Soru 3 — Ajanın "denetimi geçmek" için başvurabileceği üç hileyi say ve istemde nasıl kapatırsın?

**Kısa cevap:** (1) testi değiştirmek/atlamak, (2) hatayı sessizce yutmak,
(3) denetimi kapatmak (tip denetimini, uyarıyı bastırmak). Kapatma cümlesi:
*"Kök sebebi düzelt, hatayı bastırma; testi değiştireceksen önce nedenini açıkla."*

**Ayrıntı:**

| Hile | Nasıl görünür | Neden cazip |
|---|---|---|
| Testi atlama | Atlama işareti, silinen doğrulama | Sinyali susturur, en kısa yol |
| Hatayı yutma | Boş yakalama bloğu | Program çökmez, "çalışıyor" görünür |
| Denetimi kapatma | Bastırma yorumu, gevşetilen ayar | Uyarı listesi temizlenir |

Ortak nokta: üçü de **sinyali** düzeltir, **davranışı** değil. İstemde yasak
davranışı adıyla anmak ve gerekçe şartı koymak, kolay çıkışı kapatır.

📌 **Sık yapılan hata:** Bunu ajanın "kötü niyeti" sanmak. Değil — verilen hedef
"denetim geçsin"di ve o hedefe en kısa yoldan gitti. Hedefi doğru koymak senin işin.

🔗 [1.5 §4](1.5-dogrulama-refleksi.md) · [4.3](../04-kendi-aletini-yap/4.3-hooklar.md)

---

### Soru 4 — Kodu yazan oturum neden tarafsız inceleyemez? İkinci gözün farkı ne?

**Kısa cevap:** Çünkü **kendi gerekçesini biliyor** ve değişikliği o gerekçenin
ışığında değerlendirir. İkinci göz **taze bir bağlamda** çalışır: yalnızca
değişikliği ve ölçütü görür, onu üreten akıl yürütmeyi görmez.

**Ayrıntı:** Fark teknik değil, **bilgisel**. Aynı model, aynı araç olabilir —
belirleyici olan neyi gördüğü. Yazan bağlam "bu satırı şu yüzden böyle yazdım"ı
biliyor; o gerekçe, kusuru gizleyen şeyin ta kendisi olabilir.

İyi bir inceleme isteği üç şey söyler: **neyi** inceleyeceğini, **neye karşı**
inceleyeceğini (plan, gereksinim listesi), ve **neyin bulgu sayıldığını**.

📌 **Sık yapılan hata:** İncelemeyi aynı oturumda "bir de sen kontrol et" diye
istemek. Aynı bağlam, aynı körlük.

🔗 [1.5 §5](1.5-dogrulama-refleksi.md) · [3.5](../03-is-akislari/3.5-kod-inceleme.md)

---

### Soru 5 — İnceleyicinin bulgularını olduğu gibi uygulamak neden kötü sonuç verir? Filtreyi nasıl kurarsın?

**Kısa cevap:** Çünkü **eksik aramaya gönderilen inceleyici, iş sağlam olsa bile
eksik bulur** — ondan istenen budur. Filtre: *yalnızca doğruluğu ya da yazılı
gereksinimleri etkileyen bulgular; gerisi isteğe bağlı.*

**Ayrıntı:** Filtresiz uygulamanın tipik sonuçları:

- Gereksiz soyutlama katmanları
- Olmayacak durumlara karşı savunmacı kod
- Gerçekleşemeyecek senaryolar için testler
- Okunabilirliği düşüren erken genelleme

Filtreyi **isteğin içine** kur, sonradan uygulama: *"Biçim tercihlerini değil,
doğruluğu etkileyen eksikleri raporla."* Böylece süzme işi listeyi okumadan önce
yapılmış olur.

📌 **Sık yapılan hata:** Bulgu sayısını kalite göstergesi sanmak. Çok bulgu,
kötü kod değil, **geniş tanımlı bulgu ölçütü** demek olabilir.

🔗 [1.5 §5](1.5-dogrulama-refleksi.md)
