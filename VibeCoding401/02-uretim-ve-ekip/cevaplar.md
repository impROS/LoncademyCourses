# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 2.1 Güvenlik

### Soru 1 — İstem enjeksiyonunun farkı; neden yamayla kapanmıyor?

**Kısa cevap:** Klasik açıklardan farkı: **kırılan bir şey yok.** Ne arabellek
taşması ne yetki yükseltme — yalnızca metin. Model tasarlandığı gibi çalışıyor.
Yamayla kapanmıyor çünkü model, **"bu talimat mı veri mi"** ayrımını
**yapısal olarak** yapamıyor: ikisi aynı akışta, aynı biçimde geliyor.

**Ayrıntı:** Araçlar azaltıcı katmanlar ekliyor — izin sistemi, ağ
komutlarının otomatik onaylanmaması, web içeriğini ayrı bağlamda getirme,
sınıflandırıcı denetimi. **Hiçbiri tam değil**, ve bu bir eksiklik değil
problemin doğası.

📌 **Sık yapılan hata:** "Bir sürümde düzeltirler" beklemek. Bu bir hata
değil, mimari bir özellik — savunma katmanlarla kurulur.

🔗 [2.1 §1](2.1-guvenlik.md)

---

### Soru 2 — Beş kaynak; hangisi "kaynağa güvenirsem" yanılgısını çürütür?

**Kısa cevap:** Sorun kayıtları ve yorumlar · web sayfaları · bağımlılıklar ·
hata izleme kayıtları · **MCP sunucu çıktıları** (ayrıca kod dosyaları,
görseller).
Yanılgıyı çürüten: **MCP sunucu çıktıları.**

**Ayrıntı:** Sunucu senin yazdığın ve güvendiğin bir sunucu olabilir — ama
döndürdüğü kullanıcı yorumları, hata kayıtları ya da sorun açıklamaları
**başkalarının yazdığı metindir.**

Yani **sunucunun kötü niyetli olması gerekmiyor**; güvenilmez içeriği
aktarması yeter. Diğer kaynaklarda "güvenmiyorum" demek kolaydır; buradaki
ayrım daha ince ve bu yüzden daha çok kaçırılır.

📌 **Sık yapılan hata:** Riski yalnızca "sunucu kötü niyetli olabilir" diye
düşünmek ve dürüst sunucuların taşıdığı içeriği gözden kaçırmak.

🔗 [2.1 §1](2.1-guvenlik.md) · **301 · *Kendi MCP sunucun***

---

### Soru 3 — Altı katman; birincisi neden birinci?

**Kısa cevap:** **1. En az yetki · 2. İzin kuralları · 3. Kum havuzu/yalıtım ·
4. Hook ile engelleme · 5. İnsan onayı · 6. Denetim izi.**
Birincisi birinci çünkü **diğer katmanların hepsi aşılsa bile zararın üst
sınırını** belirler.

**Ayrıntı:** Diğer katmanlar saldırıyı **engellemeye** çalışır; en az yetki
başarılı bir saldırının **ne yapabileceğini** sınırlar.

| Uygulama | Etkisi |
|---|---|
| Salt okunur veritabanı kullanıcısı | Enjeksiyon başarılı olsa da veri silinmez |
| Dar kapsamlı belirteç | Tek depo, tek proje |
| **Yıkıcı aracı hiç sunmama** | Çağrılamaz — en katı biçim |
| Alt ajana yazma aracı vermeme | İnceleyici değiştiremez |

📌 **Sık yapılan hata:** Katmanları alternatif sanmak. Hepsi birlikte
çalışır; biri aşılınca diğerleri devrede kalır.

🔗 [2.1 §3](2.1-guvenlik.md)

---

### Soru 4 — "Geri dönülemez eylem" ölçütü ve dört örnek.

**Kısa cevap:** Ölçüt: ***"Yanlış olsaydı geri alabilir miydim?"***
Örnekler: **üretime dağıtım · veri silme · para hareketi · dışarıya mesaj
gönderme** (ayrıca gizli değer paylaşma).

**Ayrıntı:** Bu eylemlerin ortak yanı: sonucu **dışarıya taşar** ve geri
alınamaz. Bir dosyayı yanlış düzenlersen geri sararsın; yanlış bir e-postayı
geri alamazsın.

Bu yüzden bu eylemler **otomatik onaya bırakılmaz** — ne izin kipiyle, ne
otomasyonda, ne de "nasılsa doğru yapar" varsayımıyla.

📌 **Sık yapılan hata:** Ölçütü "riskli mi?" diye kurmak. Riskli ama geri
alınabilir çok iş var; belirleyici olan **geri alınabilirlik**.

🔗 [2.1 §3](2.1-guvenlik.md)

---

### Soru 5 — "Bu sorun kaydında şunu yapmam isteniyor" dendiğinde doğru refleks?

**Kısa cevap:** **"Kim istiyor?"** diye sormak. Talimatı **alıntılat, uygulatma.**

**Ayrıntı:** Sorun kaydını **herkes** yazabilir; o istek senin isteğin değil.
Doğru davranış iki adımlı:

1. Ajan metni **aynen gösterir** (yorumlamaz, uygulamaz).
2. **Sen** kimin yazdığını ve meşru olup olmadığını değerlendirirsin.

Bu, hem enjeksiyonu yakalar hem meşru istekleri kaybetmez — çünkü gerçekten
yapılması gereken bir şeyse sen onaylarsın.

📌 **Sık yapılan hata:** Ajanın dış içerikten türettiği bir isteği "iş akışının
parçası" sanıp onaylamak. Kaynağı sorgulanmayan her istek bir risk.

🔗 [2.1 §4](2.1-guvenlik.md)

---

## 2.2 Gizlilik ve veri

### Soru 1 — Ne gider, ne gitmez? En çok atlanan kalem?

**Kısa cevap:**
**Gider:** istemler · **okunan dosyalar** · **komut çıktıları** · araç ve
sunucu sonuçları · kural dosyaları, bellek, skill açıklamaları · ortam ve
depo bilgisi.
**Gitmez:** okunmayan dosyalar · çalıştırılmayan komutlar · bağlanmamış
sistemler · engellenmiş yollar.
En çok atlanan: **komut çıktıları.**

**Ayrıntı:** Dosya okumak bilinçli bir eylem gibi görünür; komut çalıştırmak
öyle görünmez. `env` çalıştırmak **tüm ortam değişkenlerini** — yani gizli
değerleri — bağlama ve oradan modele gönderir. Aynısı bir yapılandırma dökümü
ya da veritabanı sorgusu için de geçerli.

📌 **Sık yapılan hata:** Yalnızca dosya okuma yollarını engellemek. Aynı bilgi
bir komut çıktısıyla da gidebilir — `Bash(env)` gibi kurallar da gerekir.

🔗 [2.2 §1](2.2-gizlilik-ve-veri.md)

---

### Soru 2 — Saklama, eğitim ve erişim neden ayrı? Cevapları nereden?

**Kısa cevap:** Üçü **ayrı ayrı yapılandırılan** boyutlar: bir sağlayıcı
isteği kısa süre saklayıp eğitimde hiç kullanmayabilir; erişim kısıtları da
bağımsızdır. Cevapları **aracın veri işleme sayfasından** ve kurumsal
kullanımda **sözleşmeden** gelir.

**Ayrıntı:** Üç soru:

| Soru | Ne demek |
|---|---|
| Saklanıyor mu | Sağlayıcı isteği ne kadar tutuyor |
| Eğitimde kullanılıyor mu | Model geliştirmede kullanılıyor mu |
| Kim erişebiliyor | Sağlayıcı içinde erişim kısıtı |

Cevaplar **plana göre** de değişir: tüketici planı, kurumsal plan ve bulut
sağlayıcı üzerinden kullanım farklı davranır.

📌 **Sık yapılan hata:** Bir blogda okuduğu politikayı kendi planına
uyarlamak. Politika plana, sözleşmeye ve ülkeye bağlıdır.

🔗 [2.2 §2](2.2-gizlilik-ve-veri.md)

---

### Soru 3 — "Sıfır veri saklama" nedir, hangi varsayım yanlış?

**Kısa cevap:** Sağlayıcının istek içeriğini işledikten sonra **saklamadığı**
yapılandırma. Yanlış varsayım: **kendiliğinden açık olduğu.**

**Ayrıntı:** Bu bir yapılandırma ve genelde kurumsal sözleşmelerde konuşulur —
öntanımlı davranış değildir. "Biz zaten sıfır veri saklamadayız" varsayımı
doğrulanmadan yapılırsa yanlış bir güvence üretir.

Yanında bilinmesi gereken kavramlar: **veri yerleşimi** (isteğin hangi
coğrafyada işlendiği), **kurumsal sözleşme** (tüketici koşullarından farklı
taahhütler), **denetim belgeleri** (bağımsız denetim raporları).

📌 **Sık yapılan hata:** Kavramı doğru bilip **durumu** doğrulamamak. Tanımı
bilmek yetmez; sizin yapılandırmanızda açık mı, ona bak.

🔗 [2.2 §2](2.2-gizlilik-ve-veri.md)

---

### Soru 4 — Uzaktan bağlanmak ile bulut oturumu farkı.

**Kısa cevap:** **Uzaktan denetimde** kod ve dosya erişimi **yerelde kalır**;
yalnızca oturum trafiği geçer. **Bulut oturumunda** depo uzak bir ortama
**kopyalanır** ve orada çalışır.

**Ayrıntı:** "Kod nerede çalışıyor" sorusu, "model isteği nereye gidiyor"
sorusundan **ayrıdır**. Dört biçim:

| Biçim | Kod nerede |
|---|---|
| Yerel | Senin makinende |
| Bulut oturumu | Sağlayıcının yalıtılmış ortamında |
| Kendi altyapın | Sizin sunucularınızda |
| Bulut sağlayıcı üzerinden | Yerel; model isteği sizin bulut hesabınızdan |

Kurumsal değerlendirmede bu ayrım belirleyicidir — telefondan bağlanmak
kodun buluta gittiği anlamına gelmez.

📌 **Sık yapılan hata:** Arayüzün uzak olmasını, yürütmenin uzak olması
sanmak.

🔗 [2.2 §3](2.2-gizlilik-ve-veri.md)

---

### Soru 5 — Kişisel verinin beş kaynağı; en pratik önlem?

**Kısa cevap:** **Test verileri** (üretimden kopyalanmışsa) · **günlükler ve
hata izleme kayıtları** · **veritabanı dökümleri ve göç dosyaları** ·
**ekran görüntüleri** · **sorun kayıtlarındaki müşteri metinleri**.
En pratik önlem: **anonimleştirilmiş örnek veriyle çalışmak**, gerekirse
alanları maskelemek.

**Ayrıntı:** Bu önlem teknik önlemden **önce** gelir: hiç okutulmayan veri
hiçbir risk taşımaz. Şifreli veri okutmak işe yaramaz — ajan onu çözemez, ya
da çözerse koruma zaten kalkar.

⚠️ Kişisel veri düzenlemeleri ülkeye ve sektöre göre değişir; kurumsal
kullanımda **uyum tarafına danış**. Bu set hukuki tavsiye vermez.

📌 **Sık yapılan hata:** "Bizim kodumuzda kişisel veri yok" demek. Test
verilerine ve günlüklere bakmadan bu cümle kurulamaz.

🔗 [2.2 §5](2.2-gizlilik-ve-veri.md)

---

## 2.3 Lisans ve telif

### Soru 1 — Sorumluluk asimetrisi; pratik sonucu ne, ne değil?

**Kısa cevap:** Kodun **ne yaptığından tam sorumlusun**; **nasıl yaptığı**
üzerindeki mülkiyetin **sınırlı olabilir**. Yani: **risk tam, koruma kısmi.**
Pratik sonucu **"ajan kullanma" değil**: incelemeyi ciddiye almak ve üretilen
kodu **kendi kodun gibi sahiplenmek**.

**Ayrıntı:** "Ajan yazdı" bir savunma değildir — ajan hukuki bir taraf değil.
Bir aracı kullanmak, çıktısının sorumluluğunu ortadan kaldırmaz; derleyicinin
ürettiği kod için de aynısı geçerli.

Bu yüzden **201 · *Kod inceleme*** konusundaki inceleme, ajan çağında **daha az değil daha çok** önem
kazanıyor.

📌 **Sık yapılan hata:** Asimetriyi bir "ajan kullanmama" gerekçesi sanmak.
Gerekçe değil; **çalışma biçimi** gerekçesi.

🔗 [2.3 §1](2.3-lisans-ve-telif.md)

---

### Soru 2 — "İnsan yazarlığı" nedir? İstem neden yetmez, `03` ne sağlar?

**Kısa cevap:** Telif korumasının **insan yaratıcılığı** gerektirmesi ilkesi.
Bir çalışma, **insanın ifadesel unsurlar üzerinde yaratıcı denetim kurduğu
ölçüde** korunabilir. Yalnızca istem yazmak genelde bu denetimi sağlamaz —
ifadesel seçimler modele bırakılmıştır.
`03`'teki akış (plan, inceleme, düzeltme, test) **insan denetimini zaten kurar.**

**Ayrıntı:** Kaliteli çalışma biçimi ile telif tarafı burada örtüşüyor: kodu
okumak, planlamak, düzeltmek ve sınamak, ifadesel unsurlar üzerinde gerçek
bir denetim demektir.

⚠️ Bu **garanti değildir** ve lisans bulaşması riskini de kaldırmaz — o ayrı
bir konu. Ayrıca çerçeve ülkeye göre değişir ve gelişmeye devam ediyor.

📌 **Sık yapılan hata:** İstem yazmayı yaratıcı katkı saymak. Denetim
**ifadesel unsurlarda** aranıyor.

🔗 [2.3 §2](2.3-lisans-ve-telif.md)

---

### Soru 3 — Bulaşmanın dört sinyali; telif satırı bulunca ne yapılmaz?

**Kısa cevap:** Sinyaller: **tanınmış algoritmanın birebir üretilmesi ·
projeye yabancı biçimde alışılmadık derecede eksiksiz kod · kalmış telif
satırı/yorum/tuhaf ad · belirli bir kütüphanenin iç yapısının taklidi.**
Telif satırı bulunca **silinmez** — **araştırılır.**

**Ayrıntı:** İzi silmek riski kaldırmaz, yalnızca **gizler** — ve sonradan
bulunması hâlinde durumu daha kötü hâle getirir.

Doğru davranış üç adımlı: **araştır · incelemede işaretle · kararı bilerek ver.**
Kurumsal ortamda bu adımlara lisans tarama araçları eklenir.

📌 **Sık yapılan hata:** Temizlik refleksiyle telif satırını silmek. Ajan da
bunu kendiliğinden yapabilir — istemde açıkça yasakla.

🔗 [2.3 §3](2.3-lisans-ve-telif.md)

---

### Soru 4 — "Ajan yazdı" neden savunma değil?

**Kısa cevap:** Çünkü **ajan hukuki bir taraf değil.** Sorumluluk sende ya da
kurumunda. Dağıttığın kodun lisans uyumluluğundan da sen sorumlusun.

**Ayrıntı:** Bu ilke iki yerde birden geçerli:

| Konu | Sorumluluk |
|---|---|
| Kodun **davranışı** (hata, zarar) | Sende |
| Kodun **lisans uyumluluğu** | Sende |

Bir aracı kullanmak sorumluluğu aktarmaz. Aksine, aracın hızlı üretmesi
inceleme yükünü artırır — çünkü daha çok kod, aynı dikkatle denetlenmeli.

📌 **Sık yapılan hata:** Sağlayıcı taahhütlerini sınırsız bir koruma sanmak.
Kapsam ve koşullar **sözleşmeye bağlı** — oku, varsayma.

🔗 [2.3 §1 ve §4](2.3-lisans-ve-telif.md)

---

### Soru 5 — Kaynağın belli olmasının üç faydası.

**Kısa cevap:**
1. **İnceleme dikkati** — inceleyen kişi **201 · *Kod inceleme*** konusundaki beş kontrolü açar (**201 · *Git ve pull request***).
2. **Dürüstlük** — kaynağı gizlemek sonradan güven kaybettirir (**201 · *Git ve pull request***).
3. **Sonradan denetlenebilirlik** — bir lisans sorusu çıktığında hangi kodun
   nasıl üretildiğini bilmek (`2.3`).

**Ayrıntı:** Üçüncüsü en somut faydayı verir: bir sorun çıktığında
araştırmanın kapsamını **daraltır**. "Hangi kod ajanla üretildi?" sorusunun
cevabı yoksa, tüm kod tabanını incelemek zorunda kalırsın.

Bu, **her satıra etiket** demek değil. Ekip düzeyinde bir **iz** yeterli:
commit notu, pull request etiketi ya da ajan kimliğiyle işlenmiş değişiklikler.

⚠️ Açık kaynağa katkı yapıyorsan ayrıca **projenin kendi kuralına** bak.

📌 **Sık yapılan hata:** İzi "gereksiz bürokrasi" sayıp atlamak — ve bir soru
çıktığında hiçbir şey bulamamak.

🔗 [2.3 §5](2.3-lisans-ve-telif.md)

---

## 2.4 Maliyet

### Soru 1 — "Büyük model pahalıdır" neden eksik?

**Kısa cevap:** Çünkü model seçimi bir **çarpandır**; çarpılan şey **bağlamın
büyüklüğüdür**. Şişmiş bağlamla küçük model de pahalıya gelir.

**Ayrıntı:** Belirleyici etkenler sırayla:

| Etken | Etkisi |
|---|---|
| **Bağlamın büyüklüğü** | En büyük; her istek tümünü taşır |
| Önbellek isabeti | Değişmeyen önek ucuza okunur |
| Düşünme bütçesi | Çıktı tokenı olarak faturalanır |
| Model seçimi | **Çarpan** |
| Alt ajan sayısı | Her biri ayrı pencere |
| Otomasyon sıklığı | Her çalıştırma bir maliyet |

📌 **Sık yapılan hata:** Maliyet sorununu model seçimiyle çözmeye çalışmak ve
bağlam alışkanlıklarına hiç dokunmamak.

🔗 [2.4 §1](2.4-maliyet-yonetimi.md)

---

### Soru 2 — Boştaki oturum neden harcar? Üç sebep.

**Kısa cevap:** **Zamanlanmış görevler** (aralığı gelince çalışır) ·
**oturumlar arası mesajlar** (yeni tur başlatır) · **hedef denetimleri**
(arka plan işi beklerken denetim turu). Ayrıca **alt ajanlar** çalıştıkları
sürece tüketir.

**Ayrıntı:** Ortak mekanizma: her tur **tüm bağlamı** taşır. Yani boşta duran
ama bağlamı büyük bir oturumda, sen klavyeye dokunmadan tetiklenen bir tur
bile belirgin bir maliyet üretir.

**Boşta olmak, bağlantısız olmak demek değil.**

📌 **Sık yapılan hata:** Uzun süren bir oturumu "nasılsa kullanmıyorum" diye
açık bırakmak. Kapatmak ya da temizlemek bedavadır.

🔗 [2.4 §2](2.4-maliyet-yonetimi.md)

---

### Soru 3 — Sıkıştırma ile temizleme maliyet farkı.

**Kısa cevap:** **Sıkıştırma ucuz değildir** — özetlediği konuşmayı okur, yani
kendisi büyük bir istektir. **Temizlemek bedavadır.**

**Ayrıntı:** Karar tablosu:

| Durum | Doğru hamle | Maliyeti |
|---|---|---|
| İş sürüyor, yer bitti | Yönlendirmeli sıkıştırma | Büyük bir istek |
| **İş değişti** | **Temizle** | **Sıfır** |

Yani temiz bir başlangıç istiyorsan sıkıştırmayı bekleme — temizle. Bu hem
maliyet hem kalite açısından doğru (**101 · *Bağlam penceresi***).

📌 **Sık yapılan hata:** Sıkıştırmayı "ucuz bir temizlik" sanmak ve ilgisiz
bir işe geçerken onu kullanmak. İki hata birden: pahalı **ve** işe yaramaz
geçmişi taşıyor.

🔗 [2.4 §2](2.4-maliyet-yonetimi.md)

---

### Soru 4 — En yüksek etkili üç strateji; listede olmayan hangisi?

**Kısa cevap:** **1. İlgisiz işler arasında temizle · 2. Okumayı ve hacimli
çıktıyı devret/filtrele · 3. İşe göre model ve çaba düzeyi seç.**
Listede **olmayan**: **"daha az kullan".**

**Ayrıntı:** Üçünün ortak yanı: **aynı işi daha az bağlamla** yapmayı sağlıyorlar.

"Daha az kullan" listede yok çünkü kullanımı kısmak, aracın değerinden
vazgeçmektir — çözüm değil. Amaç maliyeti düşürmek değil, **maliyet/değer
oranını** iyileştirmek.

📌 **Sık yapılan hata:** Maliyet kaygısıyla kullanımı kısmak ve sonuçta hem
tasarruf etmemek hem verimden kaybetmek.

🔗 [2.4 §3](2.4-maliyet-yonetimi.md)

---

### Soru 5 — `/usage` ekranında üç şey; neden yalnızca maliyet yetmez?

**Kısa cevap:** **1. Önbellek isabeti** (oturum çok mu bölünüyor) ·
**2. Dağılım** (hangi skill, alt ajan, eklenti, bağlantı ne tüketiyor) ·
**3. Davranış işaretleri** (uzun bağlam, önbellek ıskası).
Yalnızca maliyet yetmez çünkü **"ucuz ama işe yaramayan"** bir kurulumu iyi
sanabilirsin.

**Ayrıntı:** `1.2`'deki dörtlü burada da geçerli: **tur sayısı · düzeltme
sayısı · geçen süre · maliyet.** Daha pahalı ama üç kat az düzeltme gerektiren
bir kurulum, toplamda daha ucuz olabilir.

⚠️ Ekranda gösterilen maliyet bir **tahmindir** — yerel olarak hesaplanır;
kesin rakam faturadadır.

📌 **Sık yapılan hata:** Tek boyutlu optimizasyon. Ölçülen şey hedef hâline
gelir ve yanlış yere götürür.

🔗 [2.4 §4](2.4-maliyet-yonetimi.md)

---

## 2.5 Ekipçe kullanım

### Soru 1 — Paylaşılan ve paylaşılmayan katmanlar.

**Kısa cevap:**
**Paylaşılır:** talimat dosyaları (kurallar, komutlar, mimari) · kapsanmış
kural dosyaları · ortak izin ve hook yapılandırması · ortak skill ve
yardımcılar · MCP sunucu tanımları (**gizli değer hariç**).
**Paylaşılmaz:** kişisel kısayollar ve tercihler · kişisel proje notları ·
⚠️ **otomatik bellek**.
"Hiçbir zaman paylaşılmayan": **otomatik bellek.**

**Ayrıntı:** Ölçüt basit: *ekipte geçerli olması gereken her şey **depoda**
yaşamalı.* Anlatılan unutulur, makinede kalan paylaşılmaz — depodaki kalır.

📌 **Sık yapılan hata:** Kurulumu ekibe anlatıp "artık biliyorlar" saymak.
Yeni katılan biri o anlatımı hiç duymayacak.

🔗 [2.5 §1](2.5-ekipce-kullanim.md)

---

### Soru 2 — "Bende çalışıyor" üçlü kontrolü.

**Kısa cevap:** (1) **Depodaki kurallar** aynı mı, (2) **yerel ayar** farkı var
mı, (3) **bellek** farkı var mı.
Neredeyse her zaman farklı olan: **bellek.**

**Ayrıntı:** İlk ikisi git ya da dosya karşılaştırmasıyla hızlıca doğrulanır;
üçüncüsü ise tanım gereği farklıdır — ajanın senden öğrendiği tercihler takım
arkadaşında yoktur.

Bu, "bende çalışıyor" durumunun **ajan çağındaki yeni sebebi**. Eskiden sebep
yerel yapılandırma ya da kurulu bir araçtı; şimdi ajanın kendi notları da aynı
sonucu doğuruyor.

📌 **Sık yapılan hata:** Farkı araç sürümünde ya da modelde aramak. Önce üçlü
kontrolü uygula.

🔗 [2.5 §1](2.5-ekipce-kullanim.md)

---

### Soru 3 — Zorunluluk ölçütü ve fazlasının sonucu.

**Kısa cevap:** Ölçüt: ***Bu, bir kişinin yanlış ayarıyla **başkasını** riske
atar mı?*** Evetse zorunlu; hayırsa bırak.
Fazla zorunluluğun sonucu: insanlar aracı **kişisel yapılandırmalarıyla**
kullanır ve **görünürlük kaybolur**.

**Ayrıntı:**

| Zorunlu kılınması mantıklı | Bırakılması mantıklı |
|---|---|
| Engelleme kuralları (hassas yollar) | Model tercihi |
| İzin kipi tavanı | Kısayollar, arayüz |
| Kurulabilecek eklenti kısıtı | Kişisel skill'ler |
| Kimlik doğrulama kısıtı | Çalışma alışkanlıkları |

Gizli kullanımın en kötü yanı: ne ölçebilirsin ne denetleyebilirsin.

📌 **Sık yapılan hata:** Güvenliği "her şeyi kilitlemek" sanmak. Kilitlenmiş
kurulum kullanımı ortadan kaldırmaz, **görünmez** kılar.

🔗 [2.5 §2](2.5-ekipce-kullanim.md)

---

### Soru 4 — İnceleme politikasının üç kararı ve dengeli kural.

**Kısa cevap:** Üç karar: **(1) ajan üretimi kod nasıl işaretlenir,
(2) ne düzeyde inceleme gerekir, (3) ne otomatik onaya bırakılmaz.**
Dengeli kural: ***ajan üretimi kod, insan üretimi kodla aynı incelemeden geçer
— artı **201 · *Kod inceleme*** konusundaki beş kontrol.***

**Ayrıntı:** İki uçtan da kaçınılır:

| Aşırı gevşek | Aşırı sıkı |
|---|---|
| "Testler geçti, birleştir" | Her satırın elle yeniden yazılması |
| Doğrulanmamış kod üretime gider | Aracın değeri kaybolur, gizli kullanım başlar |

Üçüncü karar (`2.1`'e bağlanır): geri dönülemez ve dışa dönük eylemler asla
otomatik onaya bırakılmaz.

📌 **Sık yapılan hata:** Politikayı yazmayıp "herkes zaten dikkatli" varsaymak.
Yazılmayan politika, kişiden kişiye değişen bir uygulamaya dönüşür.

🔗 [2.5 §3](2.5-ekipce-kullanim.md)

---

### Soru 5 — Yaygınlaştırma sırası; bozmanın sonucu; pilotun çıktısı ne değil?

**Kısa cevap:** Sıra: **pilot → ölçüm → kurulum → politika → açılış → üç aylık
gözden geçirme.**
Sırayı bozmanın sonucu: **herkes kendi kurulumunu icat eder** ve bunları
sonradan birleştirmek baştan yapmaktan zordur.
Pilotun çıktısı **araç kararı değildir** — kurulum, politika ve ölçüm verisidir.

**Ayrıntı:** Araç kararı `1.2`'nin konusu ve farklı bir çerçeveyle verilir:
eksen tablosu, taşınma maliyeti, ölçüm. Pilotun işi, **seçilmiş araçla nasıl
çalışılacağını** çıkarmaktır.

Altıncı adım da atlanmamalı: üç ayda bir kurallar, maliyet ve eksen tablosu
gözden geçirilir — bu alan o hızda değişiyor.

📌 **Sık yapılan hata:** Heyecanla önce herkese açmak. Sonra ortaya çıkan
dağınıklığı toplamak, baştan düzenli kurmaktan kat kat pahalı.

🔗 [2.5 §4](2.5-ekipce-kullanim.md)

---

## 2.6 Sınırlar

### Soru 1 — Tek ölçüt ve dört doğrulama sınıfı.

**Kısa cevap:** Ölçüt: **çıktıyı doğrulayabiliyor musun?**

| Doğrulama | Ajan ne kadar işe yarar |
|---|---|
| Otomatik ve hızlı (test, derleme, tip denetimi) | ✅ **Çok** |
| Elle ama mümkün (kod okuma, çalıştırıp deneme) | ✅ İyi — **dikkatin kadar** |
| Uzman bilgisi gerekiyor, sende yok | ⚠️ **Riskli** |
| Pratikte imkânsız | ⛔ **Kullanma** |

**Ayrıntı:** Üçüncü satır en çok kaçırılanı: **kodu değerlendiremiyorsan,
üretmiş olman bir şey ifade etmez.** Elinde okunabilir ama sınanmamış bir metin
olur.

📌 **Sık yapılan hata:** Ölçütü karmaşıklık sanmak. Karmaşık ama test
edilebilir bir iş, basit ama doğrulanamayan bir işten çok daha uygundur.

🔗 [2.6 §1](2.6-sinirlar.md)

---

### Soru 2 — İki zayıf alanın ortak sebebi.

**Kısa cevap:** **Yazılmamış alan kısıtları** ve **ürün/öncelik kararları**.
Ortak sebep: **ajan yalnızca görebildiğiyle çalışır** — bu bilgiler bağlamında
yok.

**Ayrıntı:** Bir kural kimsenin kafasındaysa ya da bir öncelik toplantıda
konuşulduysa, o bilgi ajanın erişiminde değildir. Bu bir **yetenek** sınırı
değil, bir **bilgi** sınırı.

Ve kısmen çözülebilir: yazılmamış kuralları yazmak (`02`), kararları yazılı
hâle getirmek. Çözülemeyen kısım, doğası gereği insanda kalan yargılar
(kabul edilebilir risk, öncelik, insan etkisi — **201 · *Kod inceleme***).

📌 **Sık yapılan hata:** Bu boşluğu daha güçlü modelle kapatmaya çalışmak.
Model gücü, olmayan bilgiyi üretmez.

🔗 [2.6 §3](2.6-sinirlar.md)

---

### Soru 3 — Dört "kullanma" durumu; beşincisi neden tercih?

**Kısa cevap:** **(1) Çıktıyı değerlendiremiyorsan · (2) doğrulama pratikte
imkânsızsa · (3) mekanik bir işse · (4) veri/uyum kısıtı varsa.**
Beşincisi — **öğrenmek istediğin konu** — bir sınır değil **tercih**, çünkü
teknik bir engel yok: iş biter ama öğrenme olmayabilir.

**Ayrıntı:** İlk dördü bir risk ya da uygunsuzluk barındırır; beşincisi bir
**hedef seçimidir**. Ajan öğrenmede çok yararlı da olabilir (açıklama, keşif,
soru sorma) — belirleyici olan senin o an ne istediğin.

📌 **Sık yapılan hata:** Mekanik işleri de ajana vermek. Deterministik araç
daha kesin, hızlı ve ucuzdur — ve **sessiz sapma** riski taşımaz (**201 · *Büyük dönüşümler***).

🔗 [2.6 §4](2.6-sinirlar.md)

---

### Soru 4 — Beceri körelmesinin mekanizması.

**Kısa cevap:** Doğrulama zinciri **senin okuma ve değerlendirme becerine**
dayanıyor. Ama o beceri **kullanılarak** korunuyor — ve ajan tam olarak o
kullanımı azaltıyor.

**Ayrıntı:** Gözlemlenebilir sıra:

1. Kodu yazmayı bırakırsın; okuma devam eder.
2. Bir süre sonra **okuma yüzeyselleşir** (göz gezdirme).
3. İnceleme bir **onaya** dönüşür.
4. Doğrulama zinciri, farkına varmadan **kopar**.

Karşılığı yasak değil **denge**: zaman zaman kendin yaz · satır satır oku ·
anlamadığın kodu açıklattır ve **doğrula** · yeni bir alanı önce kendin öğren ·
ara sıra ajansız çalış.

📌 **Sık yapılan hata:** Riski "abartı" sayıp hiç denge kurmamak — ve bir gün
kendi kod tabanında yabancı hissetmek.

🔗 [2.6 §5](2.6-sinirlar.md)

---

### Soru 5 — Körelmeyi ölçen soru; cevap hayırsa ne demek?

**Kısa cevap:** ***Bu kodu ajan olmadan yazabilir miydim?***
Cevap uzun süredir hayırsa, o alanda **denetleme yetkinliğini kaybediyorsun**
demektir.

**Ayrıntı:** Sorunun gücü, üretim becerisi ile denetim becerisini **birlikte**
ölçmesinde. Yazamadığın bir kodu bir süre sonra **değerlendiremez** hâle
gelirsin — ve setin tüm güvencesi o değerlendirmeye dayanıyor.

Bu, **101 · *Vibe coding nedir*** konusundaki ayrımın son hâli: vibe coding **tabanı** yükseltir, ajan
mühendisliği **tavanı**. **Tabanda kalırsan tavanı hiç göremezsin.**

📌 **Sık yapılan hata:** Soruyu iş çıktısıyla karıştırmak. Süre, test durumu
ve satır sayısı işin çıktısını ölçer; bu soru **senin yetkinliğini** ölçer.

🔗 [2.6 §5](2.6-sinirlar.md)
