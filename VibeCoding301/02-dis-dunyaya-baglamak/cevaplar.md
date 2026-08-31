# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 2.1 MCP ile bağlanmak

### Soru 1 — Üç taşıma türü; hangisi önerilmiyor?

**Kısa cevap:** **`stdio`** yerel süreç · **`http`** uzak (önerilen) ·
**`sse`** uzak ama **artık önerilmiyor**.

**Ayrıntı:**

| Tür | Nerede | Ne zaman |
|---|---|---|
| `stdio` | Senin makinende | Yerel araçlar, veritabanı, dosya erişimi |
| `http` | Uzak adres | Bulut servisleri |
| `sse` | Uzak adres | ⚠️ Eski belgelerde görürsen güncele taşındı mı bak |

Yerel süreç eklerken **`--` ayırıcısı şart**: onsuz sunucunun bayrakları ajanın
bayrakları sanılır.

📌 **Sık yapılan hata:** `--` unutup anlaşılmaz bir hata alınca kurulumun
bozuk olduğunu sanmak.

🔗 [2.1 §2](2.1-mcp-baglama.md)

---

### Soru 2 — Üç kapsam; hangisi paylaşılır, oraya ne yazılmaz?

**Kısa cevap:** **Yerel** (öntanımlı, yalnızca sen + bu proje) · **proje**
(depoya işlenir, **ekip görür**) · **kullanıcı** (sen + tüm projeler).
Proje kapsamına **gizli değer yazılmaz** — ortam değişkeni genişletmesi kullanılır.

**Ayrıntı:** Depoya yazılan her şey depoyu okuyabilen herkese açıktır ve git
geçmişinden silmek zordur. Bu yüzden bağlantı bilgisi `${DEGISKEN}` biçiminde
geçirilir; değerin kendisi ortamda durur.

Öntanımlının **yerel** olması güvenli bir varsayılan: bir deneme sunucusu
istemeden ekibe yayılmaz.

📌 **Sık yapılan hata:** Hızlıca çalışsın diye bağlantı dizesini `.mcp.json`
içine yazmak — ve commit'lemek.

🔗 [2.1 §2](2.1-mcp-baglama.md)

---

### Soru 3 — Araç arama ne yapar? Çok sunucunun iki maliyeti?

**Kısa cevap:** Araç arama, başta yalnızca **araç adlarını** yükler; ağır tam
tanımlar gerektiğinde çekilir.
İki maliyet: (1) **adlar birikir**, (2) benzer araçlar arasında **yanlış seçim
riski** artar.

**Ayrıntı:** Maliyet iki boyutlu — token **ve karar kalitesi**. Otuz benzer
araç adı arasından doğru olanı seçmek, üç araç arasından seçmekten zordur.

Bu yüzden en etkili tek önlem **kullanmadığın sunucuları kapatmak**: kapalı bir
sunucu ne ad ekler ne veri döndürür — hem yüzeyi hem bağlamı birlikte küçültür.

📌 **Sık yapılan hata:** "Nasılsa erteleniyor" diye çok sayıda sunucu bağlı
tutmak.

🔗 [2.1 §3](2.1-mcp-baglama.md)

---

### Soru 4 — Bağlanmanın iki riski; hangisi az anlaşılır?

**Kısa cevap:** (1) Sunucu **ajanının yetkilerini** kullanır. (2) Sunucu
**ajanın gözüne veri sokar**.
Az anlaşılan **ikincisi** — ve sunucunun kötü niyetli olması **gerekmez**.

**Ayrıntı:** Sunucunun döndürdüğü metin ajanın bağlamına girer. O metin bir hata
kaydı, bir web sayfası ya da bir sorun kaydı olabilir — hepsi **başkaları
tarafından yazılmış**. İçine talimat gizlenmişse ajan onu okur.

Yani dürüst bir sunucu, güvenilmez içeriği aktardığı için bir **istem enjeksiyonu
taşıyıcısı** olabilir.

| Önlem | Etkisi |
|---|---|
| Bilinen kaynak | Tedarik zinciri riskini düşürür |
| **Dar kapsam** | Zararı yapısal olarak sınırlar |
| İzin kuralları | Belirli araçları engeller |
| Kullanmadığını kapat | Yüzeyi ve bağlamı birlikte küçültür |

📌 **Sık yapılan hata:** Riski yalnızca "sunucu kötü niyetli olabilir" diye
düşünmek ve dürüst sunucuların taşıdığı içeriği gözden kaçırmak.

🔗 [2.1 §5](2.1-mcp-baglama.md)

---

### Soru 5 — Komut satırı aracı mı MCP mi?

**Kısa cevap:** **Komut satırı aracı varsa onu tercih et** — bağlam açısından
daha ucuzdur, hiçbir araç listesi eklemez. MCP'yi, komut satırı aracı olmayan
ya da yetersiz kaldığı yerlerde kullan.

**Ayrıntı:** Ajan `gh`, `aws`, `gcloud` gibi araçları zaten bilir ve doğrudan
komut çalıştırır — araç adı, şema, sunucu bağlantısı gerekmez.

MCP'nin üstün olduğu yerler: yapılandırılmış veri gerektiğinde, kimlik
doğrulamanın sunucu tarafında yönetilmesi gerektiğinde, ve komut satırı aracı
hiç olmadığında.

📌 **Sık yapılan hata:** "MCP daha modern" diye komut satırı aracı olan bir
sistem için sunucu bağlamak.

🔗 [2.1 §3](2.1-mcp-baglama.md)

---

## 2.2 Kendi MCP sunucun

### Soru 1 — Üç alternatif ve üç koşul.

**Kısa cevap:**
Alternatifler: **komut satırı aracı · skill · hazır sunucu.**
Koşullar (üçü birden): sistem **kapalı** + erişim **tekrarlanıyor** +
yapılandırılmış veri işi **belirgin biçimde** iyileştiriyor.

**Ayrıntı:** Üçü birden doğru değilse muhtemelen bir betik yeter. Sunucu yazmak
**bakım yükü** demektir: sürüm, hata, güvenlik, belge.

📌 **Sık yapılan hata:** Ajanın bir sistemi bilmemesini sunucu gerekçesi saymak.
Bilgi boşluğunu bir skill ya da belge bağlantısı kapatabilir.

🔗 [2.2 §1](2.2-mcp-sunucusu-yazma.md)

---

### Soru 2 — Altı tasarım ilkesi; en çok atlanan?

**Kısa cevap:** **Dar kapsam · açıklayıcı tarif · küçük çıktı · anlamlı hata ·
güvenli varsayılan · girdi doğrulama.**
En çok atlanan: **küçük çıktı** — atlanınca tek çağrı **bağlamı zehirleyebilir**.

**Ayrıntı:** MCP aracının döndürdüğü her şey ajanın bağlamına girer ve orada
**kalır**. Tüm tabloyu döndüren bir araç, tek çağrıda oturumu kullanılamaz hâle
getirir.

Bu yüzden **sayfalama ve alan seçimi bir tercih değil, zorunluluktur** — ve
sunucuyu bağlamadan önce **en büyük olası çıktıyı ölçmek** gerekir.

📌 **Sık yapılan hata:** Ortalama çıktıyı ölçüp "küçük" diye geçmek. Riski
belirleyen **en kötü durum**.

🔗 [2.2 §2](2.2-mcp-sunucusu-yazma.md)

---

### Soru 3 — Ajan aracı neye bakarak seçer? Hangi kuralın aynısı?

**Kısa cevap:** **Adına ve açıklamasına.** Bu, `1.1`'deki skill `description`
kuralının aynısı.

**Ayrıntı:** İki mekanizma da aynı ilkeye dayanıyor: **seçim, metne bakılarak
yapılır.** Dolayısıyla:

| Kötü | İyi |
|---|---|
| `veri_yonet` | `siparis_getir` |
| "Veri işlemleri" | "Sipariş kimliğiyle tek siparişin durumunu döner" |

Ortak kural: **ne yaptığını değil, ne zaman kullanılacağını** yaz.

📌 **Sık yapılan hata:** Araç adını iç mimarine göre koymak (`db_query_v2`).
Ajan senin mimarini bilmiyor; işlevi bilmesi gerekiyor.

🔗 [2.2 §2](2.2-mcp-sunucusu-yazma.md)

---

### Soru 4 — "Kendi sunucum güvenlidir" neden yanlış? En güçlü savunma?

**Kısa cevap:** Yazarlık güvenlik sağlamaz — sunucu **sen güvenli yaparsan**
güvenlidir. En güçlü tek savunma: **yıkıcı işlemleri hiç sunmamak.**

**Ayrıntı:** Riskler ve karşılıkları:

| Risk | Karşılık |
|---|---|
| Beklenmedik girdi | Şemayla doğrula, sınır koy |
| Yıkıcı aracın çağrılması | **Hiç sunma**, ya da onay iste |
| Dış metin taşıma | Sınırla, etiketle, gereksizi dönme |
| Kimlik sızıntısı | Ortam değişkeni, en az yetkili hesap |
| Aşırı yetki | Salt okunur bağlantı, dar kapsam |

**Sunulmayan bir aracı hiçbir enjeksiyon çağıramaz** — diğer önlemler var olan
bir yeteneğin kötüye kullanımını sınırlamaya çalışır; sunmamak yeteneği
ortadan kaldırır.

📌 **Sık yapılan hata:** "Yazma aracını koyayım, izin kuralıyla sınırlarım"
demek. Katman iyi ama zayıf; en iyisi aracın hiç olmaması.

🔗 [2.2 §4](2.2-mcp-sunucusu-yazma.md)

---

### Soru 5 — Dış metin döndürme riski ve üç hafifletme.

**Kısa cevap:** Risk: sunucun bir **istem enjeksiyonu taşıyıcısı** olur —
başkalarının yazdığı metin ajanın bağlamına girer.
Hafifletmeler: (1) dış metni **sınırla**, (2) **etiketle** ("bu içerik kullanıcı
tarafından yazılmıştır"), (3) gereksiz alanları **hiç döndürme**.

**Ayrıntı:** Kritik nokta: sunucunun kötü niyetli olması **gerekmiyor**. Dürüst
bir sunucu, kullanıcı yorumlarını ya da sorun açıklamalarını aktardığı için
saldırı taşıyıcısı olabilir.

Üçüncü hafifletme en etkilisi ve en kolayı: bir alan gerekmiyorsa döndürme.
Dönmeyen metin enjeksiyon taşıyamaz.

📌 **Sık yapılan hata:** Kaynak nesneyi olduğu gibi döndürmek. Hem bağlam
maliyeti hem enjeksiyon yüzeyi üretir.

🔗 [2.2 §4](2.2-mcp-sunucusu-yazma.md)

---

## 2.3 Eklenti ve paylaşım

### Soru 1 — Paketleme tetikleyicisi ve erken paketlemenin zararı.

**Kısa cevap:** Tetikleyici: **ikinci depo aynı kurulumu istiyor.**
Erken paketlemenin zararı: kurulum **kararlı hâle gelmeden** sürüm yönetimi
yükü kazanç getirmeden gelir.

**Ayrıntı:**

| Durum | Ne yap |
|---|---|
| Tek depo, kişisel | `.claude/` içinde bırak |
| Tüm projelerinde aynı | Kullanıcı düzeyine taşı |
| **İkinci depo** | **Paketle** |
| Ekip | Paketle + dağıt |
| Dışarıya açacaksın | Paketle + yayımla |

📌 **Sık yapılan hata:** Paketlemeyi bir "düzen" işi sanmak. Çözdüğü sorun
düzen değil, **kopyaların ayrışması**.

🔗 [2.3 §1](2.3-eklenti-ve-paylasim.md)

---

### Soru 2 — Eklentiye ne konur, ne konmaz?

**Kısa cevap:** **Konur:** skill · alt ajan tanımı · hook · MCP sunucu tanımı.
**Konmaz:** kural dosyaları.
Gerekçe: eklenti **davranış** taşır, **proje bilgisi** değil.

**Ayrıntı:** Kural dosyaları projenin komutlarını, mimarisini ve geleneklerini
anlatır. Başka bir depoda bunlar **yanlış** olur — "test komutu `npm test`"
bilgisi, başka bir projede zararlıdır.

Ayrım şu soruyla yapılır: *bu, ajanın **nasıl çalıştığıyla** mı ilgili, yoksa
**bu projeyle** mi?*

📌 **Sık yapılan hata:** Kural dosyasını da eklentiye koyup her projeye aynı
proje bilgisini dağıtmak.

🔗 [2.3 §2](2.3-eklenti-ve-paylasim.md)

---

### Soru 3 — Ad çakışması nerede çözülmüş, nerede değil?

**Kısa cevap:** **Eklentilerde çözülmüş** — eklenti skill'leri eklenti adıyla
nitelenir. **Yerel `.claude/` tanımlarında çözülmemiş** — aynı adlı iki tanım
sessiz bir üzerine yazmaya yol açar.

**Ayrıntı:** Yereldeki sorunun sinsiliği **sessizlik**: hangi tanımın
kazandığı öncelik sırasına bağlıdır ve bir uyarı almazsın. Belirti dolaylı
gelir: "iki yerde aynı adlı skill vardı, biri kayboldu."

Pratik alışkanlık: yerel tanımlarda **ayırt edici ad** kullan; kısa ve genel
adlardan (`test`, `deploy`, `review`) kaçın.

📌 **Sık yapılan hata:** Eklentideki korumayı yerel tanımlarda da var sanmak.

🔗 [2.3 §2](2.3-eklenti-ve-paylasim.md)

---

### Soru 4 — "Eklenti kurmak dosya kopyalamaktır" neden yanlış? Dört soru?

**Kısa cevap:** Yanlış çünkü **kod çalıştırmayı kabul etmektir**: hook'lar
makinende çalışır, MCP sunucuları senin yetkilerinle konuşur, skill'ler ajanının
davranışını değiştirir.

Dört soru:
1. **Hangi hook'ları var, ne yapıyorlar?**
2. **Hangi MCP sunucularını bağlıyor?**
3. **Ne kadar yetki istiyor?**
4. **Gizli değer istiyor mu?** (Ayrıca: kim yazdı, sürdürülüyor mu?)

**Ayrıntı:** Bir eklenti üç yüzeyi birden açar: **yürütme** (hook), **erişim**
(sunucu), **davranış** (skill). Bu yüzden kurulum kararı, bir bağımlılık
eklemekle aynı ciddiyette ele alınmalıdır.

📌 **Sık yapılan hata:** Adı popüler bir araca benzeyen paketi resmî sanmak.
Yazım hatası yakalayan sahteler klasik ve hâlâ etkili.

🔗 [2.3 §4](2.3-eklenti-ve-paylasim.md)

---

### Soru 5 — "Sürprizi olmasın" ilkesi ve üç uygulaması.

**Kısa cevap:** Kuran kişi, **kurmadan önce ne olacağını bilmeli.**
Üç uygulama: (1) hangi hook'ların hangi olaylarda çalıştığını **belgele**,
(2) **gizli değer isteme**, (3) **yıkıcı işlem sunma**.

**Ayrıntı:** Ölçüt basit ve kişisel: **sen başkasının eklentisini kurarken ne
bilmek isterdin?** Onu yaz.

İyi bir eklenti belgesinde bulunması gerekenler: ne yaptığı, **hangi hook'lar
hangi olaylarda**, **ne istemediği** (gizli değer, ağ erişimi), ve **hangi
yetkilerle** çalıştığı.

Ek olarak: **küçük ve tek konulu** tut, **sürüm ver** — kırıcı değişiklik ayırt
edilebilir olsun.

📌 **Sık yapılan hata:** Kendi eklentini belgelemeyi gereksiz sanmak, çünkü sen
zaten biliyorsun. Altı ay sonraki sen de bir başkasıdır.

🔗 [2.3 §5](2.3-eklenti-ve-paylasim.md)

---

## 2.4 Otomasyon ve SDK

### Soru 1 — "Denetimi kaldırır" ne demek? Pratik sonucu?

**Kısa cevap:** Ajan aynı ajandır; değişen tek şey **senin orada olmaman.**
Yerelde sana sorulan izin soruları, diff okumaların ve "dur" deme imkânın yok.
Pratik sonuç: **kısıtlar önceden ve açıkça yazılır.**

**Ayrıntı:** Yerelde güvenliğin bir kısmı **etkileşimden** geliyordu. Onu
kaldırınca yerine üç katman koyarsın: **izin kipi · dar araç listesi · yalıtım.**

📌 **Sık yapılan hata:** Yerelde iyi çalışan bir kurulumu olduğu gibi
otomasyona taşımak ve aynı güvenliği beklemek.

🔗 [2.4 §1](2.4-otomasyon-ve-sdk.md)

---

### Soru 2 — Etkileşimsiz kipte başlangıç izin kipi ve belirtisi.

**Kısa cevap:** **Manual.** Belirti: *"betik hiçbir şey yapmadan bitiyor"* —
izin verilmemiş ve soracak kimse yok.

**Ayrıntı:** Bu, otomasyonda en sık takılınan yer ve teşhisi kolay: çıktı
boş ya da "izin gerekiyor" benzeri bir sonuç dönüyorsa kip ve araç listesi
verilmemiştir.

Çözüm açık bir kip ve dar bir liste:

```bash
claude -p "..." --permission-mode dontAsk --allowedTools "Read,Edit,Bash(npm test)"
```

📌 **Sık yapılan hata:** Belirtiyi model ya da ağ sorunu sanıp orada aramak.

🔗 [2.4 §1](2.4-otomasyon-ve-sdk.md)

---

### Soru 3 — Çıplak başlatmanın iki gerekçesi.

**Kısa cevap:** (1) **Kalite/yeniden üretilebilirlik** — sonuç makineden
bağımsız olur. (2) **Güvenlik** — güvenmediğin bir depodaki hook ve sunucu
tanımları çalışmaz.

**Ayrıntı:** Öntanımlı etkileşimsiz çalıştırma, etkileşimli oturumun yüklediği
**her şeyi** yükler: hook, skill, eklenti, MCP sunucusu, bellek, kural dosyaları.

| | Öntanımlı | Çıplak |
|---|---|---|
| Yükleme | Hepsi | Hiçbiri |
| Sonuç | Makineye bağlı | **Her yerde aynı** |
| Yabancı depo riski | Var | **Yok** |

Gerekeni açıkça geçirirsin: ek sistem talimatı, ayarlar, sunucu yapılandırması.

📌 **Sık yapılan hata:** "`-p` zaten temiz bir ortam" varsaymak. Tam tersi.

🔗 [2.4 §3](2.4-otomasyon-ve-sdk.md)

---

### Soru 4 — Şema kullanmanın gerekçesi.

**Kısa cevap:** **Model çıktı biçimini değiştirir; şema değiştirmez.** Metin
ayrıştıran betik bir gün sessizce bozulur.

**Ayrıntı:** Şema verildiğinde sonuç, o şemaya uyan **yapılandırılmış bir
nesne** olarak döner — ayrıştırmaya gerek kalmaz.

Yapılandırılmış çıktının ek faydası: JSON biçimi oturum kimliği ve
**kullanım/maliyet** bilgisini de getirir, yani çalıştırma başına maliyeti
izleyebilirsin (**401 · *Maliyet***).

📌 **Sık yapılan hata:** Düzenli ifadeyle çıktı ayrıştıran betikler yazmak ve
model güncellenince neden bozulduğunu anlamamak.

🔗 [2.4 §2](2.4-otomasyon-ve-sdk.md)

---

### Soru 5 — Üç kısıt katmanı ve neyi engellediği.

**Kısa cevap:**

| Katman | Ne engeller |
|---|---|
| **İzin kipi** (`dontAsk`) | İzin verilmeyen **her şeyi** reddeder |
| **Dar araç listesi** | Yapılabilecek eylem **yüzeyini** daraltır |
| **Yalıtım** (kapsayıcı/sanal makine/çalışma kopyası) | Zararın **yayılmasını** çevreler |

**Ayrıntı:** Üçü **katmanlıdır**: biri aşılsa diğerleri devrede kalır. Buna
ayrıca **tur sınırı ve zaman aşımı** eklenir — bunlar zararı değil, **maliyeti
ve süreyi** sınırlar.

📌 **Sık yapılan hata:** Yalnızca yalıtıma güvenmek ("kapsayıcıda çalışıyor,
sorun yok"). Kapsayıcı içindeki kimlik bilgileri ve ağ erişimi hâlâ gerçektir.

🔗 [2.4 §4](2.4-otomasyon-ve-sdk.md)
