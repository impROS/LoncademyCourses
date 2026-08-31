# 1. alan · Bulut Kavramları — Kendini kontrol cevapları

> Bu dosya [1.1](1.1-bulut-faydalari.md) – [1.4](1.4-bulut-ekonomisi.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [1.1](#11-aws-bulutunun-faydaları) · [1.2](#12-well-architected-framework) ·
[1.3](#13-buluta-göç-stratejileri) · [1.4](#14-bulut-ekonomisi)

---

## 1.1 AWS bulutunun faydaları

📄 Sorular: [`1.1-bulut-faydalari.md`](1.1-bulut-faydalari.md)

### Soru 1 — Elasticity ile scalability arasındaki tek cümlelik fark

**Kısa cevap:** Scalability **büyüyebilmektir**; elasticity **talebe göre kendiliğinden büyüyüp
küçülmektir**. Elasticity'nin ayırt edici yarısı **küçülme**dir.

**Ayrıntı:**

| | Scalability | Elasticity |
|---|---|---|
| Yön | Yalnız büyüme garantili | Büyüme **ve** küçülme |
| Tetikleyici | Genelde elle ya da planlı | **Otomatik**, talebe tepkiyle |
| Sınavdaki kelimesi | `handle growth`, `scale out` | `automatically`, `scale in and out`, `when traffic drops` |
| AWS aracı | Daha büyük instance, daha çok instance | Auto Scaling |

> 📌 **Sık yapılan hata:** "Otomatik" kelimesini görüp scalability demek. Ölçüt şudur:
> senaryoda **kapasite geri veriliyor mu?** Veriliyorsa elasticity.

🔗 Konu: [1.1 §2](1.1-bulut-faydalari.md) · 📖 [elasticity](../00-baslangic/03-kavram-sozlugu.md#elasticity)

---

### Soru 2 — High availability ile fault tolerance farkı, hangisi pahalı

**Kısa cevap:** Yüksek erişilebilirlik **kısa bir kesintiyi kabul eder** ve hızla toparlanır;
hata toleransı **hiç kesinti kabul etmez**. Hata toleransı **daha pahalıdır**.

**Ayrıntı:**

| | High availability | Fault tolerance |
|---|---|---|
| Kesinti | Saniyeler/dakikalar olabilir | **Sıfır** |
| Tipik kurulum | 2+ [AZ](../00-baslangic/03-kavram-sozlugu.md#az) + yük dengeleyici | Tam yedekli, çalışır hâlde ikinci sistem |
| Maliyet | Orta | **Yüksek** — kapasitenin bir kısmı hiç kullanılmadan bekler |
| Sınav kelimesi | `minimal downtime`, `highly available` | `NO interruption`, `continues to operate` |

Pahalı olmasının sebebi doğrudan: hiç kesinti istemiyorsan yedek bileşenin **hazır ve çalışır**
durumda beklemesi gerekir; boşta duran kapasitenin parasını da ödersin.

> 📌 **Sık yapılan hata:** İkisini eşanlamlı saymak. Soruda `no interruption` yazıyorsa cevap
> her zaman fault tolerance'tır, "iki AZ'a yaydık" şıkkı ne kadar doğru görünürse görünsün.

🔗 Konu: [1.1 §3](1.1-bulut-faydalari.md) ·
📖 [fault tolerance](../00-baslangic/03-kavram-sozlugu.md#fault-tolerance)

---

### Soru 3 — S3 için 11 dokuz hangi kavramı ifade eder

**Kısa cevap:** **Dayanıklılığı** (durability) — verinin kaybolmama olasılığını. Erişilebilirliği
(availability) değil.

**Ayrıntı:**

| Ölçü | Değer | Cümlesi |
|---|---|---|
| Durability | %99,999999999 (11 dokuz) | "Veri **kaybolmaz**" |
| Availability | %99,99 (S3 Standard) | "Veriye **o an ulaşılır**" |

Ayrım şu cümleyle kalıcı olur: **veri duruyor olabilir ama o an ulaşılamayabilir.** 11 dokuz,
10 milyon nesnenin 10.000 yılda birinin kaybolması ölçeğinde bir taahhüttür.

> 📌 **Sık yapılan hata:** 11 dokuzu "uptime" sanmak. Sınav bu ikisini bilerek yan yana şık yapar.

🔗 Konu: [1.1 §3](1.1-bulut-faydalari.md) ·
📖 [durability](../00-baslangic/03-kavram-sozlugu.md#durability)

---

### Soru 4 — Scale up ile scale out farkı, AWS hangisini tercih eder

**Kısa cevap:** **Scale up** aynı makineyi büyütmek (dikey), **scale out** makine sayısını
artırmaktır (yatay). AWS **scale out**'u önerir.

**Ayrıntı:**

| | Scale up (dikey) | Scale out (yatay) |
|---|---|---|
| Ne yapılır | `t3.small` → `m5.4xlarge` | 1 sunucu → 5 sunucu |
| Sınırı | En büyük instance boyutu | Pratikte yok |
| Kesinti | Genelde **yeniden başlatma** gerekir | Gerekmez |
| Arıza dayanıklılığı | Tek makine = tek arıza noktası | Yedeklilik doğal gelir |

AWS'in tercih sebebi iki katmanlı: yatay ölçekleme hem **tavan tanımaz** hem de tek arıza
noktasını (SPOF) kendiliğinden ortadan kaldırır.

> 📌 **Sık yapılan hata:** "instance type değiştirdik" senaryosunda elasticity demek.
> Tip değişimi **scale up**'tır; elasticity otomatik ekleme-çıkarmadır.

🔗 Konu: [1.1 §2](1.1-bulut-faydalari.md) ·
📖 [scalability](../00-baslangic/03-kavram-sozlugu.md#scalability)

---

### Soru 5 — "Undifferentiated heavy lifting" ne demek

**Kısa cevap:** Müşteriye **hiçbir rekabet avantajı katmayan**, ama zaman ve para yiyen altyapı
işleri: sunucu rafa takmak, disk değiştirmek, jeneratör bakımı, yama geçmek.

**Ayrıntı:** İfadenin işaret ettiği şey bir maliyet değil, bir **fırsat maliyetidir**. Kendi veri
merkezinde 10 kişilik ekibin 7'si donanımla uğraşıyorsa, o 7 kişi ürüne çalışmıyordur.
Bulutun vaadi bu işi AWS'e devredip aynı ekibi ürüne yöneltmektir.

Sınavda bu ifade neredeyse her zaman aynı cevaba götürür: **AWS bu işi üstlenir, müşteri
uygulamasına odaklanır.**

> 📌 **Sık yapılan hata:** İfadeyi "ağır iş yükü / yüksek trafik" sanmak. `Heavy lifting` burada
> **teknik yük** değil, **fark yaratmayan emek** anlamındadır.

🔗 Konu: [1.1 §1](1.1-bulut-faydalari.md) ·
📖 [undifferentiated heavy lifting](../00-baslangic/03-kavram-sozlugu.md#undifferentiated-heavy-lifting)

---

## 1.2 Well-Architected Framework

📄 Sorular: [`1.2-well-architected.md`](1.2-well-architected.md)

### Soru 1 — Altı sütunu sırayla say

**Kısa cevap:** Operational Excellence · Security · Reliability · Performance Efficiency ·
Cost Optimization · **Sustainability**.

**Ayrıntı:**

| Sütun | Tek cümlelik sorusu |
|---|---|
| Operational Excellence | Sistemi çalıştırmayı ve izlemeyi iyi yapıyor muyum? |
| Security | Veriyi ve sistemleri kimden, nasıl koruyorum? |
| Reliability | Bozulunca kendini toparlayabiliyor mu? |
| Performance Efficiency | Kaynağı verimli mi, doğru araçla mı kullanıyorum? |
| Cost Optimization | Gereksiz para ödüyor muyum? |
| Sustainability | Çevresel etkiyi azaltıyor muyum? |

> 📌 **Sık yapılan hata:** "Beş sütun" demek. Sustainability 2021'de eklendi ve **CLF-C02
> kapsamındadır**; şıklarda görürsen atma.

🔗 Konu: [1.2 §1](1.2-well-architected.md)

---

### Soru 2 — Reliability ile Performance Efficiency farkı

**Kısa cevap:** Reliability **"çökünce toparlanıyor mu?"**, Performance Efficiency
**"kaynağı verimli mi kullanıyorum?"** sorusudur.

**Ayrıntı:**

| | Reliability | Performance Efficiency |
|---|---|---|
| Derdi | Arızadan dönmek | Doğru aracı verimli kullanmak |
| Örnek | Multi-AZ, otomatik yedek, yük devretme | Doğru instance tipi, CloudFront, önbellek |
| Anahtar kelime | `recover`, `failure`, `backup`, `resiliency` | `efficient`, `latency`, `right resource type` |

> 📌 **Sık yapılan hata:** "Sistem hızlı olsun" isteğini Reliability sanmak. Hız
> Performance Efficiency'dir; Reliability hızla değil **ayakta kalmakla** ilgilenir.

🔗 Konu: [1.2 §1](1.2-well-architected.md)

---

### Soru 3 — Loose coupling nedir, hangi 3 AWS servisiyle sağlanır

**Kısa cevap:** Bileşenlerin birbirine **doğrudan değil**, aradaki bir tampon üzerinden bağlanması.
AWS'teki tipik üçlü: **SQS** (kuyruk), **SNS** (bildirim) ve **Elastic Load Balancing**.
Aynı amaçla EventBridge ve API Gateway de sayılır.

**Ayrıntı:** Sıkı bağlılıkta (tight coupling) A sunucusu doğrudan B'nin adresine bağlanır;
B çökünce A da çöker. Araya kuyruk konduğunda B çöktüğünde mesajlar **kuyrukta birikir**, B
dönünce işlenir — A hiç etkilenmez.

| Tampon | Ne yapar |
|---|---|
| SQS | Mesajı bekletir, alıcı hazır olunca çeker |
| SNS | Bir mesajı aynı anda çok aboneye iter |
| Elastic Load Balancing | İstemciyi belirli bir sunucuya değil, **havuza** bağlar |

> 📌 **Sık yapılan hata:** Yük dengeleyiciyi yalnızca performans aracı sanmak. İstemciyi tek bir
> sunucunun adresinden kurtardığı için **gevşek bağlılık** aracıdır da.

🔗 Konu: [1.2 §3c](1.2-well-architected.md) ·
📖 [loose coupling](../00-baslangic/03-kavram-sozlugu.md#loose-coupling)

---

### Soru 4 — Well-Architected Tool ile Trusted Advisor farkı

**Kısa cevap:** Well-Architected Tool **senin cevaplarına** dayanan bir öz-değerlendirme
anketidir; Trusted Advisor **hesabını otomatik tarar** ve öneri üretir.

**Ayrıntı:**

| | Well-Architected Tool | Trusted Advisor |
|---|---|---|
| Girdi | Senin cevapladığın sorular | Hesabındaki gerçek kaynaklar |
| Çıktı | Risk raporu (yüksek/orta riskli konular) | 5 kategoride öneri: maliyet, performans, güvenlik, hata toleransı, servis limitleri |
| Kaynak tarar mı | **Hayır** | **Evet** |
| Ücret | Ücretsiz | Tüm kontroller **Business planından** itibaren |

> 📌 **Sık yapılan hata:** "Otomatik kontrol/öneri" isteyen soruya Well-Architected Tool demek.
> O araç hiçbir şey taramaz; otomatik tarama Trusted Advisor, yapılandırma uyumu ise Config'dir.

🔗 Konu: [1.2 §4](1.2-well-architected.md) · [4.3](../04-faturalama-destek/4.3-destek-planlari.md)

---

### Soru 5 — "Servers should be disposable" ne anlama gelir, neyi gerektirir

**Kısa cevap:** Sunucu tamir edilecek bir evcil hayvan değil, **silinip yenisi kurulacak sarf
malzemesidir**. Bunun ön şartı uygulamanın **durum tutmamasıdır** (stateless).

**Ayrıntı:** Bir sunucuyu her an silebilmek için üzerinde **kaybedilecek bir şey olmamalıdır**.
Bu yüzden oturum bilgisi, yüklenen dosyalar ve günlükler sunucunun diskinde değil dışarıda tutulur:

| Durum | Nerede tutulur |
|---|---|
| Oturum (session) | ElastiCache ya da DynamoDB |
| Kullanıcı dosyaları | S3 |
| Günlük (log) | CloudWatch Logs |

Mimari adı **immutable infrastructure**: bozulan makineye müdahale edilmez, [AMI](../00-baslangic/03-kavram-sozlugu.md#ami)'den
yenisi başlatılır.

> 📌 **Sık yapılan hata:** Prensibi yalnızca "yedekli olsun" diye okumak. Asıl gereklilik
> **stateless** olmaktır; durum sunucunun diskindeyse o sunucu atılamaz.

🔗 Konu: [1.2 §3e](1.2-well-architected.md)

---

## 1.3 Buluta göç stratejileri

📄 Sorular: [`1.3-migration.md`](1.3-migration.md)

### Soru 1 — 7R'yi say ve her birine bir örnek ver

**Kısa cevap:**

| Strateji | Örnek |
|---|---|
| **Rehost** | Windows sunucuları olduğu gibi EC2'ye taşımak |
| **Replatform** | Kendi kurduğun MySQL'i Amazon RDS'e almak, kod aynı kalır |
| **Repurchase** | Kendi müşteri yönetim yazılımını bırakıp hazır bir aboneliğe geçmek |
| **Refactor** | Monoliti Lambda + DynamoDB ile mikroservislere bölmek |
| **Retire** | Üç yıldır kimsenin açmadığı uygulamayı kapatmak |
| **Retain** | Mevzuat gereği yerinde kalması gereken sistemi taşımamak |
| **Relocate** | VMware ortamını olduğu gibi AWS üzerine taşımak |

**Ayrıntı:** Efor sırası küçükten büyüğe: Retire ≈ Retain < Relocate < Rehost < Replatform <
Repurchase < **Refactor**. Bulut faydası da aynı yönde artar — Refactor en pahalı ve en kazançlı olandır.

> 📌 **Sık yapılan hata:** Repurchase ile Refactor'ı karıştırmak. Repurchase'ta **sen yazmazsın**,
> hazır ürüne geçersin; Refactor'da **sen yeniden yazarsın**.

🔗 Konu: [1.3 §1](1.3-migration.md) · 📖 [7R](../00-baslangic/03-kavram-sozlugu.md#7r)

---

### Soru 2 — Rehost ile Replatform arasındaki tek fark

**Kısa cevap:** **Değişiklik olup olmaması.** Rehost'ta hiçbir şey değişmez; Replatform'da
mimariye dokunmadan **küçük bir iyileştirme** yapılır (tipik olarak yönetilen bir servise geçiş).

**Ayrıntı:**

| Senaryo cümlesi | Cevap |
|---|---|
| `no changes to the application` | Rehost |
| `moved the database to Amazon RDS, application code unchanged` | Replatform |
| `rewrote as microservices` | Refactor |

Ayırt edici kelime **"kod"** değil, **"herhangi bir değişiklik"**tir: veritabanını RDS'e almak
kodu değiştirmese bile platformu değiştirdiği için Replatform'dur.

> 📌 **Sık yapılan hata:** "Kod değişmedi" görünce otomatik Rehost demek. Kod değişmeden de
> platform değişebilir.

🔗 Konu: [1.3 §1](1.3-migration.md)

---

### Soru 3 — CAF'ın 6 perspektifini iş/teknik olarak ikiye ayır

**Kısa cevap:** İş tarafı: **Business · People · Governance**.
Teknik taraf: **Platform · Security · Operations**.

**Ayrıntı:**

| Taraf | Perspektif | Odağı |
|---|---|---|
| İş | Business | Bulut yatırımının iş sonuçlarıyla hizalanması |
| İş | People | Kültür, yetkinlik, eğitim, rol değişimi |
| İş | Governance | Risk, uyum, portföy ve maliyet yönetimi |
| Teknik | Platform | Hedef mimari, altyapı, yeni iş yükleri |
| Teknik | Security | Gizlilik, bütünlük, erişilebilirlik, uyumluluk |
| Teknik | Operations | Servislerin işletilmesi, izleme, olay yönetimi |

Aşamalar ayrıca sorulabilir: **Envision → Align → Launch → Scale**.

> 📌 **Sık yapılan hata:** [CAF](../00-baslangic/03-kavram-sozlugu.md#caf) ile
> [7R](../00-baslangic/03-kavram-sozlugu.md#7r)'yi karıştırmak. 7R **bir uygulamayı** nasıl
> taşıyacağını, CAF **kurumun** buna nasıl hazırlanacağını anlatır.

🔗 Konu: [1.3 §2](1.3-migration.md)

---

### Soru 4 — Oracle → Aurora göçünde hangi iki araç, hangisi önce

**Kısa cevap:** Önce **Schema Conversion Tool (SCT)** şemayı çevirir, sonra
**Database Migration Service (DMS)** veriyi taşır.

**Ayrıntı:** Sıra mantıklıdır: hedefte tablo yoksa veri yazılacak yer yoktur.

| Durum | Gereken araç |
|---|---|
| Aynı motor (MySQL → RDS MySQL) | Yalnız **DMS** |
| Farklı motor (Oracle → Aurora PostgreSQL) | Önce **SCT**, sonra **DMS** |

DMS'in ayırt edici özelliği, kaynak veritabanı **çalışmaya devam ederken** taşıma yapabilmesi ve
kesintiyi en aza indirmesidir.

> 📌 **Sık yapılan hata:** Aynı motora taşımada da SCT aramak. Şema zaten uyumluysa çevirecek
> bir şey yoktur.

🔗 Konu: [1.3 §3](1.3-migration.md) · 📖 [DMS / SCT](../00-baslangic/03-kavram-sozlugu.md#dms)

---

### Soru 5 — 200 TB veriyi 1 Gbps hattan göndermek yerine ne kullanırsın

**Kısa cevap:** **AWS Snowball Edge** — veriyi fiziksel cihazla kargolamak.

**Ayrıntı:** Hesabı yapmak cevabı kesinleştirir. 1 Gbps hat, hattı **tamamen** bu işe ayırsan
bile saatte yaklaşım olarak 0,45 TB taşır:

```
200 TB ÷ (1 Gbps ≈ 0,125 GB/s) ≈ 1.600.000 saniye ≈ 18,5 gün   (hat %100 boş varsayımıyla)
```

Gerçek hayatta hattın tamamı boş olmaz; süre haftalara/aylara çıkar ve o sürede şirketin
internet bağlantısı da tıkanır. Cihazı kargoyla göndermek günler alır.

**Sınav refleksleri:**

| Senaryo | Cevap |
|---|---|
| `petabytes`, `limited bandwidth`, `would take months` | Snowball Edge |
| Uzak/sahada küçük veri | Snowcone |
| Bağlantısız sahada **işleme** de gerekiyor | Snowball Edge Compute Optimized |
| **Sürekli, otomatik** çevrimiçi kopyalama | DataSync (Snow **değil**) |
| Kalıcı özel hat isteniyor | Direct Connect (Snow **değil**) |

> 📌 **Sık yapılan hata:** "Büyük veri" görür görmez Snow demek. Soruda **süreklilik** varsa
> (`ongoing`, `daily sync`) cevap DataSync ya da Direct Connect'tir; Snow **tek seferlik** taşımadır.

🔗 Konu: [1.3 §4](1.3-migration.md)

---

## 1.4 Bulut ekonomisi

📄 Sorular: [`1.4-bulut-ekonomisi.md`](1.4-bulut-ekonomisi.md)

### Soru 1 — TCO'nun içinde sunucu fiyatı dışında hangi 5 kalem var

**Kısa cevap:** **Elektrik ve soğutma · veri merkezi alanı/kira · ağ donanımı ve kablolama ·
yedek donanım stoku · sistem yöneticisi işgücü.** (Fiziksel güvenlik ve yazılım lisansları da sayılır.)

**Ayrıntı:**

| Kalem | Şirket içinde | AWS'te |
|---|---|---|
| Elektrik + soğutma | Sürekli, büyük kalem | Yok |
| Alan / rack kirası | Kira, tadilat | Yok |
| Ağ donanımı | Alım + bakım | Yok |
| Yedek donanım | Atıl stok | Yok |
| Sistem yöneticisi işgücü | Yama, izleme, donanım değişimi | Büyük ölçüde azalır |

AWS'in [TCO](../00-baslangic/03-kavram-sozlugu.md#tco) argümanının tamamı bu "görünmeyen"
kalemler üzerine kuruludur.

> 📌 **Sık yapılan hata:** Karşılaştırmayı yalnızca sunucu etiket fiyatı üzerinden yapmak.
> Sınav bu tuzağı doğrudan sorar.

🔗 Konu: [1.4 §2](1.4-bulut-ekonomisi.md)

---

### Soru 2 — Right-sizing nedir, hangi iki servis önerir

**Kısa cevap:** Her iş yükünü **ihtiyacı kadar** kaynakla çalıştırmak. Öneriyi
**AWS Compute Optimizer** ve **Trusted Advisor** verir.

**Ayrıntı:** Döngü dört adımdır: **ölç** (CloudWatch) → **öneri al** (Compute Optimizer /
Trusted Advisor) → **küçült** → **tekrarla**.

| Servis | Ne verir |
|---|---|
| Compute Optimizer | Makine öğrenmesine dayalı **instance tipi** önerisi |
| Trusted Advisor | Düşük kullanımlı (`underutilized`) instance uyarısı |

Sınav refleksi: `instances are running at 5% CPU` → right-sizing + Compute Optimizer.

> 📌 **Sık yapılan hata:** Right-sizing'i tek seferlik bir temizlik sanmak. Yük değiştikçe doğru
> boyut da değişir; sürekli bir alışkanlıktır.

🔗 Konu: [1.4 §3](1.4-bulut-ekonomisi.md) ·
📖 [right-sizing](../00-baslangic/03-kavram-sozlugu.md#right-sizing)

---

### Soru 3 — "LEAST operational overhead" görünce hangi tip şıkkı seçersin

**Kısa cevap:** **En yönetilen / sunucusuz** olanı. Sıralama: Lambda > Fargate > RDS/yönetilen
servis > EC2 üzerinde kendi kurduğun yazılım.

**Ayrıntı:**

| Yaklaşım | İşletim yükü kimde |
|---|---|
| EC2 üzerinde kendi MySQL'in | Yama, yedek, yük devretme, izleme = **sende** |
| Amazon RDS | Hepsi = **AWS'te** |
| Lambda | Sunucu diye bir şey yok = **hiç kimsede** |

⚠️ Bu ifade sınavda **en ucuz** anlamına gelmez. `MOST cost-effective` ile
`LEAST operational overhead` farklı şıklara götürebilir; soruda hangisi yazılıysa o kazanır.

> 📌 **Sık yapılan hata:** İkisini eşitlemek. Lambda sürekli çalışan bir yükte EC2'den
> **pahalı** olabilir; yine de "en az işletim yükü" sorusunun cevabıdır.

🔗 Konu: [1.4 §4](1.4-bulut-ekonomisi.md)

---

### Soru 4 — BYOL ile License Included farkı

**Kısa cevap:** **BYOL**'de elindeki lisansı buluta taşırsın ve yalnızca donanıma ödersin;
**License Included**'da lisans ücreti saatlik AWS fiyatının içindedir.

**Ayrıntı:**

| | BYOL | License Included |
|---|---|---|
| Lisans kimde | Sende (taşınabilir olmalı) | AWS'te |
| Ne zaman avantajlı | Lisansı zaten satın almışsan | Yeni lisans almak istemiyorsan, kısa/değişken kullanımda |
| Ek not | Bazı lisanslar **Dedicated Host** gerektirir (fiziksel çekirdek bazlı lisanslama) | Ek kurulum yok |

Lisans kullanımını takip eden servis **AWS License Manager**'dır
(`track software license usage and stay compliant`).

> 📌 **Sık yapılan hata:** BYOL'u "daha ucuz" diye ezberlemek. Lisans taşınabilir değilse ya da
> Dedicated Host gerekiyorsa BYOL **daha pahalı** çıkabilir.

🔗 Konu: [1.4 §5](1.4-bulut-ekonomisi.md) · 📖 [BYOL](../00-baslangic/03-kavram-sozlugu.md#byol)

---

### Soru 5 — Pricing Calculator ile Cost Explorer farkı

**Kısa cevap:** Pricing Calculator **geleceği tahmin eder** (henüz kurmadığın mimarinin aylık
maliyeti); Cost Explorer **geçmişi gösterir** (gerçekten ne harcadın).

**Ayrıntı:**

| Araç | Zaman ekseni | Sorusu |
|---|---|---|
| Pricing Calculator | Gelecek | "Bu mimariyi kursam ayda ne öderim?" |
| Cost Explorer | Geçmiş | "Geçen ay para nereye gitti?" |
| Budgets | Gelecek + alarm | "Sınırı aşarsam haber ver" |
| Migration Evaluator | Gelecek (göç) | "Buluta taşınırsak ne kazanırız?" |

> 📌 **Sık yapılan hata:** "Tahmin" kelimesini görüp Cost Explorer demek. Cost Explorer tahmin
> **çizgisi** çizebilir ama girdisi **geçmiş kullanımdır**; hiç kurulmamış bir mimariyi
> hesaplayamaz.

🔗 Konu: [1.4 §2](1.4-bulut-ekonomisi.md) · [4.2](../04-faturalama-destek/4.2-maliyet-yonetimi.md)

---

⬅️ [Bölüme dön](1.1-bulut-faydalari.md) · 📖 [Kavram sözlüğü](../00-baslangic/03-kavram-sozlugu.md) ·
⚙️ [Seçim rehberi](../00-baslangic/04-secim-rehberi.md)
