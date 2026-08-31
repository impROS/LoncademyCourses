# 3. alan · Bulut Teknolojisi ve Servisleri — Kendini kontrol cevapları

> Bu dosya [3.1](3.1-dagitim-isletim.md) – [3.8](3.8-diger-servisler.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [3.1](#31-dağıtım-ve-işletim-yöntemleri) · [3.2](#32-global-altyapı) ·
[3.3](#33-hesaplama) · [3.4](#34-veritabanları) · [3.5](#35-ağ) · [3.6](#36-depolama) ·
[3.7](#37-yapay-zekâ-ve-analitik) · [3.8](#38-diğer-servisler)

---

## 3.1 Dağıtım ve işletim yöntemleri

📄 Sorular: [`3.1-dagitim-isletim.md`](3.1-dagitim-isletim.md)

### Soru 1 — CloudFormation ile Elastic Beanstalk arasındaki temel fark

**Kısa cevap:** CloudFormation'a **altyapı şablonu** verirsin, ne isteyeceğine sen karar verirsin;
Elastic Beanstalk'a **uygulama kodu** verirsin, altyapıyı o seçer.

**Ayrıntı:**

| | CloudFormation | Elastic Beanstalk |
|---|---|---|
| Girdi | JSON/YAML şablon | Uygulama paketi (JAR, ZIP, konteyner imajı) |
| Kim karar verir | **Sen**, her kaynağı tanımlarsın | **AWS**, uygun altyapıyı kurar |
| Kapsam | Her tür altyapı | Web uygulaması / API |
| Ücret | Ücretsiz (kaynaklara ödersin) | Ücretsiz (kaynaklara ödersin) |
| Tetikleyici | `infrastructure as code`, `template`, `repeatable` | `just deploy my code`, `PaaS` |

CloudFormation'ın oluşturduğu kaynak grubuna **stack** denir; stack silinince içindeki her şey
gider — lab temizliği için birebirdir.

> 📌 **Sık yapılan hata:** Beanstalk'ı "sunucusuz" sanmak. Beanstalk'ın açtığı EC2'ler **senin
> hesabında görünür** ve onların ücretini ödersin; ücretsiz olan Beanstalk katmanıdır.

🔗 Konu: [3.1 §2–3](3.1-dagitim-isletim.md)

---

### Soru 2 — CLI, SDK ve API arasındaki ilişki

**Kısa cevap:** Altta tek bir **HTTPS API** vardır; **CLI** onu komut satırından, **SDK** ise
programlama dilinden çağırır. Üçü de aynı kapıya gider.

**Ayrıntı:**

| Yol | Ne | Ne zaman |
|---|---|---|
| Konsol | Tarayıcıdaki arayüz | Öğrenirken, tek seferlik işlerde |
| CLI | Komut satırı (`aws s3 ls`) | Betikleme, tekrarlı işler |
| SDK | Dil kütüphanesi | Uygulama içinden çağrı |
| API | Altındaki HTTPS arayüzü | Doğrudan çağrı |

Bunun pratik sonucu üç tanedir: hepsi **IAM** ile yetkilendirilir, hepsi **CloudTrail**'e loglanır,
ve birinde yapılabilen her şey ötekinde de yapılabilir.

> 📌 **Sık yapılan hata:** "Konsoldan yapılan iş loglanmaz" sanmak. Konsol da API'yi çağırır;
> CloudTrail hepsini görür.

🔗 Konu: [3.1 §1](3.1-dagitim-isletim.md)

---

### Soru 3 — VPN ile Direct Connect'i iki cümleyle ayır

**Kısa cevap:** **Site-to-Site VPN** internet üzerinden şifreli tünel kurar; dakikalar içinde
hazırdır ama hızı internete bağlıdır. **Direct Connect** AWS'e giden **fiziksel özel hattır**;
tutarlı bant genişliği verir ama kurulumu haftalar sürer ve pahalıdır.

**Ayrıntı:**

| | Site-to-Site VPN | Direct Connect |
|---|---|---|
| Yol | İnternet (şifreli tünel) | **Özel fiziksel hat** |
| Kurulum | Dakikalar | **Haftalar** |
| Maliyet | Düşük | Yüksek |
| Bant genişliği | İnternete bağlı, dalgalı | **Tutarlı** |
| Tetikleyici | `encrypted over the internet`, `quickly` | `dedicated`, `consistent bandwidth`, `not over the public internet` |

> 📌 **Sık yapılan hata:** "Şifreli" kelimesini görüp Direct Connect demek. Direct Connect
> kendiliğinden şifreli **değildir**; gerekiyorsa üzerine VPN kurulur.

🔗 Konu: [3.1 §5](3.1-dagitim-isletim.md)

---

### Soru 4 — "Trafik public internete çıkmasın" hangi çözümü işaret eder

**Kısa cevap:** **VPC Endpoint / PrivateLink** (AWS servisine VPC içinden erişim) ya da şirket
ağından geliniyorsa **Direct Connect**.

**Ayrıntı:** Cevabı belirleyen şey trafiğin **nereden nereye** gittiğidir:

| Nereden nereye | Cevap |
|---|---|
| VPC içinden S3/DynamoDB'ye | **Gateway VPC Endpoint** (ücretsiz) |
| VPC içinden diğer AWS servislerine ya da başka VPC'deki servise | **PrivateLink / Interface Endpoint** |
| Şirket veri merkezinden AWS'e | **Direct Connect** |

Üçünün ortak yanı, trafiği **AWS omurgasında** tutup halka açık internete hiç çıkarmamalarıdır.

> 📌 **Sık yapılan hata:** Bu ifadeye otomatik olarak VPN demek. VPN internet **üzerinden** gider —
> şifrelidir ama yine de halka açık internettedir.

🔗 Konu: [3.1 §5](3.1-dagitim-isletim.md)

---

### Soru 5 — Outposts ne zaman kullanılır

**Kısa cevap:** AWS servislerinin **senin kendi veri merkezinde** çalışması gerektiğinde: veri
yerinde kalmak zorundaysa, yerel sistemlere çok düşük gecikmeyle bağlanmak gerekiyorsa.

**Ayrıntı:** Outposts, AWS'in fiziksel donanımını sana kurar; aynı API'ler, aynı servisler
(EC2, EBS, RDS, ECS/EKS) yerinde çalışır.

| İhtiyaç | Servis |
|---|---|
| AWS servisleri **kendi veri merkezimde** çalışsın | **Outposts** |
| Büyük şehre yakın, tek haneli milisaniye gecikme | **Local Zones** |
| 5G operatör ağının içinde ultra düşük gecikme | **Wavelength** |
| Bağlantısız sahada veri toplama/işleme | **Snow ailesi** |

> 📌 **Sık yapılan hata:** "Düşük gecikme" görür görmez Outposts demek. Outposts'un ayırt edici
> şartı **kendi veri merkezinde olmak**tır; yalnız gecikme sorunuysa Local Zones ya da CloudFront
> daha ucuzdur.

🔗 Konu: [3.1 §4](3.1-dagitim-isletim.md)

---

## 3.2 Global altyapı

📄 Sorular: [`3.2-global-altyapi.md`](3.2-global-altyapi.md)

### Soru 1 — Region, AZ ve Edge Location'ı birer cümleyle tanımla

**Kısa cevap:** **Region** coğrafi bir bölgedir · **Availability Zone** o bölge içindeki bağımsız
veri merkezi kümesidir · **Edge Location** içeriğin kullanıcıya yakın önbelleklendiği noktadır.

**Ayrıntı:**

| | Region | Availability Zone | Edge Location |
|---|---|---|---|
| Sayı | Onlarca | Region başına **en az 3** | **Yüzlerce** |
| Amaç | Coğrafi seçim, mevzuat | **Yüksek erişilebilirlik** | **Düşük gecikme** |
| Örnek | `eu-central-1` | `eu-central-1a` | CloudFront noktaları |

> 📌 **Sık yapılan hata:** Edge Location'ı "küçük Region" sanmak. Orada sunucu çalıştıramazsın;
> yalnızca önbellek ve DNS vardır.

🔗 Konu: [3.2 §1](3.2-global-altyapi.md) · 📖 [AZ](../00-baslangic/03-kavram-sozlugu.md#az)

---

### Soru 2 — Bir Region'da en az kaç AZ vardır, neden birden fazlası kullanılır

**Kısa cevap:** **En az 3.** Birden fazlası, **bir veri merkezi çökse bile uygulamanın ayakta
kalması** için kullanılır.

**Ayrıntı:** AZ'ler birbirinden fiziksel olarak ayrıdır — ayrı elektrik, ayrı soğutma, sel ve
yangın riski ayrıştırılmış — ama aralarında yüksek hızlı, düşük gecikmeli özel bağlantı vardır.
Bu ikisi birlikte, "hem bağımsız hem birlikte çalışabilir" demektir.

Tek AZ'de tek sunucu = **tek arıza noktası**. İki AZ'de iki sunucu + yük dengeleyici =
yüksek erişilebilirlik.

> 📌 **Sık yapılan hata:** Sınavda `highly available` görünce "yedek alalım" demek. Yedek felaket
> kurtarmadır; yüksek erişilebilirliğin cevabı neredeyse her zaman
> **"deploy across multiple Availability Zones"**dır.

🔗 Konu: [3.2 §1, §4](3.2-global-altyapi.md)

---

### Soru 3 — Region seçiminin dört kriteri

**Kısa cevap:** **(1)** Uyumluluk / veri egemenliği · **(2)** kullanıcıya yakınlık (gecikme) ·
**(3)** servisin o Region'da bulunması · **(4)** fiyat.

**Ayrıntı:** Sıralama tesadüf değil: mevzuat tartışmaya kapalıdır, bu yüzden çakışma olduğunda
**birinci kriter kazanır**. Yeni servisler önce belirli Region'lara gelir; aynı servisin fiyatı
Region'a göre değişir.

> 📌 **Sık yapılan hata:** Soruda yasal zorunluluk geçerken fiyat ya da gecikme şıkkını seçmek.
> `must remain within the country`, `data residency` ifadeleri **uyumluluk** cevabını zorunlu kılar.

🔗 Konu: [3.2 §2](3.2-global-altyapi.md)

---

### Soru 4 — IAM ve S3'ten hangisi global, S3'ün püf noktası ne

**Kısa cevap:** **IAM globaldir**, S3 **Region bazlıdır**. Püf nokta: **kova adları dünya çapında
benzersizdir ama her kova belirli bir Region'da yaşar.**

**Ayrıntı:**

| Global | Region bazlı |
|---|---|
| IAM, Route 53, CloudFront, Organizations, Shield | EC2, EBS, VPC, **S3**, RDS, DynamoDB, Lambda |

S3'ün konsolu bütün kovaları tek listede gösterdiği için global sanılır. Benzersiz olan **isimdir**;
verinin kendisi seçtiğin Region'da durur ve veri egemenliği soruları bu ayrım üzerine kurulur.

> 📌 **Sık yapılan hata:** "S3 global, o hâlde veri her yerde" demek. Bu, mevzuat sorularında
> doğrudan yanlış cevaba götürür.

🔗 Konu: [3.2 §3](3.2-global-altyapi.md)

---

### Soru 5 — CloudFront ile Global Accelerator'ı ne zaman seçersin

**Kısa cevap:** **Önbelleklenebilir içerik** (web sayfası, görsel, video) → **CloudFront**.
**Önbelleklenemeyen TCP/UDP trafiği** ve sabit IP ihtiyacı → **Global Accelerator**.

**Ayrıntı:**

| | CloudFront | Global Accelerator |
|---|---|---|
| Ne yapar | İçeriği uçta **önbellekler** | Kullanıcıyı AWS omurgasına en yakın noktadan alır, en iyi Region'a yönlendirir |
| Protokol | HTTP/HTTPS | **TCP/UDP** dahil |
| IP | — | **Statik anycast IP** |
| Tipik senaryo | Web sitesi, medya dağıtımı | Çevrimiçi oyun, sesli görüşme, hızlı bölgesel yük devretme |

> 📌 **Sık yapılan hata:** "Global kullanıcı" ifadesini görüp ikisini eşit saymak. Ayırt edici
> soru: **içerik önbelleklenebilir mi?** Önbelleklenemiyorsa CloudFront'un katacağı fazla bir şey yoktur.

🔗 Konu: [3.2 §5](3.2-global-altyapi.md)

---

## 3.3 Hesaplama

📄 Sorular: [`3.3-compute.md`](3.3-compute.md)

### Soru 1 — Beş instance ailesi ve kullanım alanları

**Kısa cevap:**

| Aile | Ne için | Örnek |
|---|---|---|
| **General purpose** | Web sunucusu, küçük veritabanı, geliştirme/test | `t3`, `m5` |
| **Compute optimized** | Toplu işlem, oyun sunucusu, bilimsel modelleme | `c5`, `c7g` |
| **Memory optimized** | Büyük veritabanları, bellek içi analiz | `r5`, `x2` |
| **Storage optimized** | Veri ambarı, yüksek [IOPS](../00-baslangic/03-kavram-sozlugu.md#iops) veritabanı | `i3`, `d3` |
| **Accelerated computing** | Makine öğrenmesi eğitimi, grafik ve video işleme | `p4`, `g5` |

**Ayrıntı:** Harf ipucu: **C** = CPU · **R** = RAM · **I/D/H** = disk · **P/G** = grafik işlemci ·
**M/T** = genel amaçlı. Sonundaki `g` (`m7g`, `c7g`) **Graviton** işlemcidir: daha iyi
fiyat/performans ve **daha düşük enerji tüketimi** — sürdürülebilirlik sorularında da geçer.

> 📌 **Sık yapılan hata:** "Veritabanı" görür görmez storage optimized demek. Büyük veritabanları
> genelde **bellek** ister; storage optimized diskin **kendisi** darboğazsa seçilir.

🔗 Konu: [3.3 §1](3.3-compute.md)

---

### Soru 2 — Stop ile terminate farkı ve ücret etkisi

**Kısa cevap:** **Stop** makineyi kapatır, EBS diski ve verisi **kalır**; işlem ücreti durur,
**disk ücreti devam eder**. **Terminate** makineyi ve (varsayılan olarak) kök diski **siler**;
her şey durur ve **geri alınamaz**.

**Ayrıntı:**

| İşlem | Ne olur | Ücret |
|---|---|---|
| Stop | Kapanır, EBS kalır | İşlem durur, **EBS devam eder** |
| Terminate | Silinir, kök disk de silinir | Her şey durur |
| Reboot | Yeniden başlar, adres ve disk korunur | Devam eder |
| Hibernate | Bellek diske yazılır, kaldığı yerden devam | EBS devam eder |

> 📌 **Sık yapılan hata:** "Durdurdum, artık para ödemiyorum" demek. Disk duruyorsa parası işler —
> lab sonrası temizlikte instance'ı **terminate** etmek gerekir.

🔗 Konu: [3.3 §1](3.3-compute.md)

---

### Soru 3 — Lambda'nın maksimum süresi ve ücretlendirme mantığı

**Kısa cevap:** Tek çalışma en fazla **15 dakika** sürebilir. Ücret **istek sayısı + çalışma
süresi × ayrılan bellek** üzerinden hesaplanır; çalışmıyorken **ücret yoktur**.

**Ayrıntı:** Bu iki gerçek, Lambda'nın nerede doğru nerede yanlış olduğunu belirler:

| Uygun | Uygun değil |
|---|---|
| Olay tetikli, kısa süren işler | 15 dakikadan uzun süren işler |
| Aralıklı, öngörülemeyen yük | Kesintisiz çalışan servis (EC2 daha ucuz olur) |
| Bir dosya yüklenince tetiklenen işleme | Özel işletim sistemi gerektiren yükler |

15 dakika yetmiyorsa sıradaki adres **ECS/Fargate** ya da **Batch**'tir.

> 📌 **Sık yapılan hata:** Lambda'yı "her zaman en ucuz" sanmak. Sürekli çalışan bir yükte
> Lambda EC2'den **pahalıya** gelir; ucuzluğu **boşta ücret ödememesinden** gelir.

🔗 Konu: [3.3 §3](3.3-compute.md)

---

### Soru 4 — ECS, EKS, ECR ve Fargate'i birer cümleyle ayır

**Kısa cevap:** **ECS** AWS'in kendi konteyner orkestrasyonu · **EKS** yönetilen Kubernetes ·
**ECR** konteyner imajlarının deposu · **Fargate** ikisinin altında sunucu yönetmeden çalıştırma
motoru.

**Ayrıntı:** Ayırt edici soru ikilidir:

1. **Kubernetes kelimesi geçiyor mu?** Geçiyorsa **EKS**, geçmiyorsa **ECS**.
2. **"Sunucu yönetmek istemiyorum" var mı?** Varsa çalıştırma tipi **Fargate**.

| | EC2 çalıştırma tipi | Fargate çalıştırma tipi |
|---|---|---|
| Sunucuyu kim yönetir | **Sen** | **AWS** |
| Ücret | Çalışan EC2'ler | Konteynerin kullandığı işlemci/bellek |
| Kontrol | Yüksek | Düşük, işletim yükü yok |

ECR bu üçlüden farklıdır: orkestrasyon yapmaz, yalnızca **imajları saklar**.

> 📌 **Sık yapılan hata:** Fargate'i ECS/EKS'e alternatif sanmak. Fargate bir **çalıştırma
> tipidir**; yine ECS ya da EKS kullanırsın.

🔗 Konu: [3.3 §4](3.3-compute.md) · 📖 [ECS/EKS/ECR](../00-baslangic/03-kavram-sozlugu.md#ecs)

---

### Soru 5 — ALB ile NLB'yi ne zaman seçersin

**Kısa cevap:** HTTP/HTTPS trafiği ve adres yoluna göre yönlendirme gerekiyorsa **ALB**;
çok yüksek hız, TCP/UDP ya da **sabit IP** gerekiyorsa **NLB**.

**Ayrıntı:**

| | ALB | NLB |
|---|---|---|
| Katman | Uygulama (HTTP/HTTPS) | Taşıma (TCP/UDP/TLS) |
| Yönlendirme | Adres yolu ve alan adına göre | Bağlantı seviyesinde |
| Hız | Yüksek | **Aşırı yüksek**, çok düşük gecikme |
| Sabit IP | Yok | **Var** |
| Tipik | Web uygulaması, mikroservis, konteyner | Oyun, sesli görüşme, IP'nin sabit olması gereken sistemler |

Üçüncü tür olan **Gateway Load Balancer** üçüncü parti güvenlik cihazlarını (güvenlik duvarı,
saldırı tespit sistemi) devreye almak içindir.

> 📌 **Sık yapılan hata:** "Performans" kelimesini görüp otomatik NLB demek. Yönlendirme kuralı,
> alan adı ya da HTTPS sonlandırma isteniyorsa cevap ALB'dir.

🔗 Konu: [3.3 §2](3.3-compute.md) · 📖 [ALB / NLB](../00-baslangic/03-kavram-sozlugu.md#alb)

---

## 3.4 Veritabanları

📄 Sorular: [`3.4-veritabanlari.md`](3.4-veritabanlari.md)

### Soru 1 — Multi-AZ ile read replica arasındaki üç fark

**Kısa cevap:** **(1)** Multi-AZ dayanıklılık, read replica performans içindir. **(2)** Multi-AZ
eşzamanlı, read replica eşzamansız kopyalar. **(3)** Multi-AZ'deki yedeğe **okuma yapılmaz**,
read replica okuma trafiği alır.

**Ayrıntı:**

| | Multi-AZ | Read replica |
|---|---|---|
| Amaç | Yüksek erişilebilirlik | Okuma performansı |
| Kopyalama | **Eşzamanlı** | **Eşzamansız** |
| Okuma | Hayır | **Evet** |
| Arıza | **Otomatik yük devretme** | Elle terfi ettirilir |
| Region | Aynı Region, farklı AZ | **Başka Region'a da** kurulabilir |

Ezber cümlesi: **Multi-AZ = ayakta kal. Read replica = hızlan.**

> 📌 **Sık yapılan hata:** "Multi-AZ açtım, okuma da hızlanır" sanmak. Yedek yalnızca bekler;
> okuma yükünü hiç almaz.

🔗 Konu: [3.4 §2](3.4-veritabanlari.md)

---

### Soru 2 — RDS ile EC2 üzerine kurulmuş veritabanı arasındaki sorumluluk farkı

**Kısa cevap:** RDS'te işletim sistemi yaması, veritabanı motoru yaması, yedekleme ve yük
devretme **AWS'te**; EC2'ye kendin kurduğunda hepsi **sende**.

**Ayrıntı:**

| | EC2 üzerine kendin kur | RDS |
|---|---|---|
| İşletim sistemi yaması | Sen | AWS |
| Veritabanı motoru yaması | Sen | AWS |
| Yedekleme | Kendin kurarsın | Otomatik + zaman noktasına dönüş |
| Yüksek erişilebilirlik | Kendin kurarsın | Multi-AZ tek tıkla |
| İşletim sistemine erişim | **Var** | Yok |

EC2 doğru cevap olur — ama yalnızca soru **işletim sistemi seviyesinde erişim** ya da RDS'in
desteklemediği bir sürüm/eklenti istiyorsa.

> 📌 **Sık yapılan hata:** `LEAST operational overhead` sorusunda "EC2'ye kurarım, kontrolüm olur"
> demek. O ifade her zaman yönetilen servisi işaret eder.

🔗 Konu: [3.4 §1](3.4-veritabanlari.md)

---

### Soru 3 — DynamoDB'yi üç kelimeyle tanımla

**Kısa cevap:** **Sunucusuz · anahtar-değer · milisaniye altı.**

**Ayrıntı:** Üçünün her biri sınavda ayrı bir tetikleyicidir:

| Kelime | Sınavdaki karşılığı |
|---|---|
| Sunucusuz | `no servers to manage`, `fully managed NoSQL` |
| Anahtar-değer | `key-value`, `document`, `flexible schema` |
| Milisaniye altı | `single-digit millisecond at any scale` |

Ek özellikler: **Global Tables** (çok Region'lı çoklu yazma) ve **DAX** (mikrosaniye seviyesinde
önbellek). Karmaşık birleştirme (JOIN) ve ilişkisel sorgular için **uygun değildir**.

> 📌 **Sık yapılan hata:** Ölçek büyüyünce "ilişkisel veritabanı yetmez, DynamoDB'ye geçelim"
> refleksi. Sorguların ilişkiselse DynamoDB doğru cevap değildir.

🔗 Konu: [3.4 §3](3.4-veritabanlari.md)

---

### Soru 4 — Redshift ile RDS farkı (OLAP / OLTP)

**Kısa cevap:** **RDS = [OLTP](../00-baslangic/03-kavram-sozlugu.md#oltp)** — çok sayıda küçük ve
hızlı işlem. **Redshift = [OLAP](../00-baslangic/03-kavram-sozlugu.md#olap)** — az sayıda, büyük
ve karmaşık analiz sorgusu.

**Ayrıntı:**

| | RDS / Aurora | Redshift |
|---|---|---|
| Yük tipi | Sipariş kaydet, kullanıcı girişi | Rapor üret, milyonlarca satır tara |
| Sorgu sayısı | Çok, küçük | Az, büyük |
| Veri düzeni | Satır bazlı | **Sütun bazlı** |
| Tetikleyici | `transactional`, `application database` | `data warehouse`, `business intelligence`, `petabyte` |

> 📌 **Sık yapılan hata:** "SQL yazıyorsam RDS'tir" demek. Redshift de SQL konuşur; ayrım
> **sorgunun şeklindedir**, dilinde değil.

🔗 Konu: [3.4 §4](3.4-veritabanlari.md)

---

### Soru 5 — Neptune, DocumentDB ve Keyspaces hangi uyumluluğu sağlar

**Kısa cevap:** **Neptune** graf veritabanıdır (ilişki ağları) · **DocumentDB** MongoDB uyumludur ·
**Keyspaces** Apache Cassandra uyumludur.

**Ayrıntı:**

| Servis | Tür | Tetikleyici |
|---|---|---|
| Neptune | Graf | `graph`, `social network`, `fraud detection`, `recommendation` |
| DocumentDB | Doküman | `MongoDB compatible` |
| Keyspaces | Geniş sütun | `Cassandra compatible` |
| Timestream | Zaman serisi | `IoT sensor data over time` |
| MemoryDB | Kalıcı bellek içi | `durable in-memory database` |

İkisinin adı doğrudan uyumluluk vaadi taşır; Neptune ise uyumluluk değil **veri modeli**
üzerinden sorulur.

> 📌 **Sık yapılan hata:** "İlişki" kelimesini görüp ilişkisel veritabanı demek. Sosyal ağ,
> dolandırıcılık tespiti gibi **ilişki ağı** senaryolarının cevabı Neptune'dür.

🔗 Konu: [3.4 §4](3.4-veritabanlari.md)

---

## 3.5 Ağ

📄 Sorular: [`3.5-networking.md`](3.5-networking.md)

### Soru 1 — Bir subnet'i public yapan şey nedir

**Kısa cevap:** Adı değil, **yönlendirme tablosu**: içinde
[Internet Gateway](../00-baslangic/03-kavram-sozlugu.md#igw)'e giden bir yol varsa o subnet
public'tir.

**Ayrıntı:**

| | Public subnet | Private subnet |
|---|---|---|
| Yönlendirme tablosu | Internet Gateway'e yol **var** | Yol yok |
| İçinde ne olur | Yük dengeleyici, web sunucusu, atlama sunucusu | Uygulama sunucusu, veritabanı |
| İnternete çıkış | Doğrudan | **NAT Gateway** üzerinden, yalnız giden yönde |

Tipik üç katmanlı mimari:
`İnternet → IGW → public subnet (yük dengeleyici) → private subnet (uygulama) → private subnet (veritabanı)`

> 📌 **Sık yapılan hata:** Subnet'e "public-subnet" adı verip iş bitti sanmak. Ad hiçbir şey
> değiştirmez; belirleyici olan yönlendirme tablosudur.

🔗 Konu: [3.5 §1](3.5-networking.md)

---

### Soru 2 — NAT Gateway ile Internet Gateway farkı

**Kısa cevap:** **Internet Gateway çift yönlüdür** — VPC'yi internete bağlar, dışarıdan da
gelinebilir. **NAT Gateway tek yönlüdür** — özel subnet'teki sunucuların internete **çıkmasını**
sağlar, içeri girilmesine izin vermez.

**Ayrıntı:**

| | Internet Gateway | NAT Gateway |
|---|---|---|
| Yön | Çift yönlü | **Yalnız giden** |
| Nerede durur | VPC'ye bağlıdır | Public subnet'te çalışır |
| Ne için | Web sunucusuna dışarıdan erişim | Yama indirme, dış API çağırma |
| Ücret | Yok | **Saatlik + veri işleme** 💸 |

⚠️ NAT Gateway sessiz bir fatura kalemidir: kullanılmasa bile saati işler. Yalnızca S3 ve
DynamoDB'ye çıkılıyorsa **VPC Endpoint** ücretsiz alternatiftir.

> 📌 **Sık yapılan hata:** "NAT Gateway koydum, veritabanıma dışarıdan bağlanırım" sanmak.
> NAT gelen bağlantı kabul etmez.

🔗 Konu: [3.5 §1](3.5-networking.md)

---

### Soru 3 — Route 53'ün üç ana işlevi

**Kısa cevap:** **(1)** Alan adı kaydı · **(2)** DNS yönlendirmesi · **(3)** sağlık kontrolü ve
arızalı hedeften uzaklaştırma.

**Ayrıntı:** Sınavda asıl ayrım yönlendirme politikalarında yapılır:

| Politika | Ne zaman |
|---|---|
| Simple | Tek hedef |
| Weighted | Trafiği yüzdeyle bölme (kademeli geçiş, A/B testi) |
| **Latency-based** | `route users to the Region with the lowest latency` |
| Failover | Birincil arızalanınca yedeğe geç |
| **Geolocation** | `route European users to the EU site` — konuma göre |
| Multivalue answer | Birden çok sağlıklı adres döndür |

Adındaki 53, DNS'in çalıştığı port numarasından gelir.

> 📌 **Sık yapılan hata:** Latency-based ile geolocation'ı karıştırmak. Biri **hız**, öteki
> **konum/mevzuat** için; Avrupalı kullanıcıyı yasal sebeple Avrupa'ya yönlendirmek geolocation'dır.

🔗 Konu: [3.5 §2](3.5-networking.md)

---

### Soru 4 — CloudFront'un üç faydası

**Kısa cevap:** **(1)** Gecikmeyi düşürür · **(2)** kaynak sunucunun yükünü azaltır ·
**(3)** veri çıkış maliyetini düşürür.

**Ayrıntı:** Üçüncüsü en çok atlanandır: içerik uçtan servis edilince kaynak Region'dan internete
çıkan veri azalır, çıkış ücreti düşer.

Ek yetenekler: Shield Standard, WAF ve ACM sertifikalarıyla entegre çalışır; kaynak olarak S3,
ALB, EC2 ya da **AWS dışı** bir sunucu kullanılabilir; statik **ve** dinamik içeriği hızlandırır;
kodun uçta çalışması için Lambda@Edge ve CloudFront Functions vardır.

> 📌 **Sık yapılan hata:** CloudFront'u yalnızca "statik dosya sunucusu" sanmak. Dinamik içeriği de
> hızlandırır ve önünde durduğu uygulamayı korumaya da yarar.

🔗 Konu: [3.5 §3](3.5-networking.md)

---

### Soru 5 — İki VPC'yi bağlamak ile 100 VPC'yi bağlamak

**Kısa cevap:** İki VPC için **VPC Peering**; çok sayıda VPC ve şube için **Transit Gateway**.

**Ayrıntı:** Peering **geçişli değildir**: A–B ve B–C bağlıyken A, C'yi göremez. Bu yüzden n
VPC'yi tam bağlamak `n×(n−1)/2` bağlantı gerektirir — 100 VPC için 4.950 eşleşme demektir.
Transit Gateway merkezî bir hub kurar; her VPC yalnızca hub'a bağlanır.

| | VPC Peering | Transit Gateway |
|---|---|---|
| Topoloji | Bire bir | **Yıldız (hub)** |
| Geçişlilik | **Yok** | Var |
| Ölçek | Az sayıda VPC | Onlarca–yüzlerce VPC + şirket içi ağ |

> 📌 **Sık yapılan hata:** Peering'i geçişli sanmak. "Ortadaki VPC üzerinden geçerim" yaklaşımı
> çalışmaz.

🔗 Konu: [3.5 §1, §4](3.5-networking.md)

---

## 3.6 Depolama

📄 Sorular: [`3.6-storage.md`](3.6-storage.md)

### Soru 1 — Nesne, blok ve dosya depolamayı ayır ve servisini yaz

**Kısa cevap:** **Nesne → S3** (dosyayı at, adresi olsun) · **Blok → EBS** (sunucuya takılan disk) ·
**Dosya → EFS/FSx** (aynı anda çok sunucunun bağlandığı paylaşım).

**Ayrıntı:**

| | Nesne (S3) | Blok (EBS) | Dosya (EFS/FSx) |
|---|---|---|---|
| Erişim | HTTP/HTTPS API | Bir sunucuya bağlanır | **Aynı anda birçok** sunucu |
| Değiştirme | Nesne **tümüyle** yeniden yazılır | Blok blok | Dosya bazlı |
| Tipik kullanım | Yedek, medya, veri gölü, statik site | İşletim sistemi ve veritabanı diski | Paylaşılan içerik, ortak dosya alanı |

Hafıza kancası: **S3** internetteki dev dosya kutusu · **EBS** tek sunucuya takılan disk ·
**EFS** ağ paylaşımı.

> 📌 **Sık yapılan hata:** S3'ü sunucuya disk olarak takmayı düşünmek. S3 dosya sistemi değildir;
> "birden çok sunucu aynı klasörü görsün" isteğinin cevabı EFS'tir.

🔗 Konu: [3.6 §1](3.6-storage.md)

---

### Soru 2 — Yedi S3 sınıfını erişim sıklığı sırasına diz

**Kısa cevap:** Standard → Intelligent-Tiering → Standard-IA → One Zone-IA →
Glacier Instant Retrieval → Glacier Flexible Retrieval → **Glacier Deep Archive**.

**Ayrıntı:**

| Sınıf | AZ | Asgari saklama | Erişim süresi |
|---|---|---|---|
| Standard | ≥3 | Yok | Anında |
| Intelligent-Tiering | ≥3 | Yok | Anında |
| Standard-IA | ≥3 | 30 gün | Anında |
| One Zone-IA | **1** | 30 gün | Anında |
| Glacier Instant Retrieval | ≥3 | 90 gün | Milisaniye |
| Glacier Flexible Retrieval | ≥3 | 90 gün | Dakikalar–saatler |
| Glacier Deep Archive | ≥3 | 180 gün | **Saatler (≈12)** |

> 📌 **Sık yapılan hata:** "En ucuz" görünce Deep Archive demek. Soruda
> `must be available within milliseconds` varsa Deep Archive **yanlıştır** — erişim süresi
> kabul edilebilir mi diye kontrol et.

🔗 Konu: [3.6 §2](3.6-storage.md) · ⚙️ [Seçim rehberi §4](../00-baslangic/04-secim-rehberi.md#4-depolama-seçimi)

---

### Soru 3 — Erişim deseni bilinmiyorsa hangi sınıf, neden

**Kısa cevap:** **S3 Intelligent-Tiering** — katmanı **kendisi** değiştirir, yanlış tahminin
bedelini sen ödemezsin.

**Ayrıntı:** Diğer sınıflarda bedel tahmin hatasına bağlıdır:

| Yanlış seçim | Bedeli |
|---|---|
| Seyrek okunan veri Standard'da | Gereksiz yüksek depolama ücreti |
| Sık okunan veri Standard-IA'da | Okuma ücretleri depolama tasarrufunu yer |
| Erken silinen IA nesnesi | 30 günlük asgari süre yine faturalanır |

Intelligent-Tiering'in karşılığında nesne başına küçük bir izleme ücreti vardır; belirsizlik
karşısında bu ücret hemen her zaman ucuz kalır.

> 📌 **Sık yapılan hata:** Belirsizlikte "ortalama bir sınıf seçeyim" diye Standard-IA'ya gitmek.
> Sınav `access pattern is unknown` dediğinde tek doğru cevap Intelligent-Tiering'dir.

🔗 Konu: [3.6 §2](3.6-storage.md)

---

### Soru 4 — EBS ile instance store arasındaki kritik fark

**Kısa cevap:** **EBS kalıcıdır**, makine dursa da silinse de yaşayabilir. **Instance store
geçicidir**: makine durdurulunca ya da sonlandırılınca **içindeki veri gider**.

**Ayrıntı:**

| | EBS | Instance store |
|---|---|---|
| Kalıcılık | Kalıcı | **Geçici** |
| Nerede | Ağ üzerinden bağlı | Host makinenin **içinde** |
| Hız | Yüksek | **Çok yüksek** |
| Kullanım | Genel amaç, veritabanı | Geçici önbellek, ara çalışma alanı |

EBS ek olarak **anlık görüntü** (snapshot) alabilir; bunlar S3'te artımlı saklanır ve başka
AZ ya da Region'a taşınabilir.

> 📌 **Sık yapılan hata:** Instance store'un hızına bakıp veritabanı koymak. Makine bir kez
> durdurulduğunda veri geri gelmez.

🔗 Konu: [3.6 §3](3.6-storage.md) · 📖 [EBS](../00-baslangic/03-kavram-sozlugu.md#ebs)

---

### Soru 5 — Versioning ve lifecycle birlikte hangi problemi çözer

**Kısa cevap:** **Yanlışlıkla silme/üzerine yazmaya karşı koruma** ile **eski sürümlerin
maliyetini** aynı anda çözer: versiyonlama her sürümü saklar, yaşam döngüsü kuralı eski
sürümleri ucuz sınıfa indirir ya da bir süre sonra siler.

**Ayrıntı:** İkisi ayrı ayrı eksiktir:

| Yalnız versiyonlama | Yalnız yaşam döngüsü |
|---|---|
| Hiçbir sürüm silinmez → **depolama sonsuza kadar büyür** | Yanlışlıkla silinen veri **geri gelmez** |

Birlikte kurulan tipik kural: güncel sürüm Standard'da kalsın, 30 günden eski sürümler
Standard-IA'ya insin, 365 günden eski sürümler silinsin.

> 📌 **Sık yapılan hata:** Versiyonlamayı açıp faturayı unutmak. Silinen nesneler bile
> "silme işareti" altında durmaya devam eder ve depolama ücreti işler.

🔗 Konu: [3.6 §2](3.6-storage.md)

---

## 3.7 Yapay zekâ ve analitik

📄 Sorular: [`3.7-ai-ml-analitik.md`](3.7-ai-ml-analitik.md)

### Soru 1 — Rekognition, Textract ve Comprehend'i birer cümleyle ayır

**Kısa cevap:** **Rekognition görür** (görüntü/videoda nesne, yüz) · **Textract okur** (taranmış
belgeden metin, tablo, form alanı) · **Comprehend anlar** (metinden duygu, varlık, konu).

**Ayrıntı:**

| Servis | Girdi | Çıktı |
|---|---|---|
| Rekognition | Görüntü, video | Nesne, yüz, metin, uygunsuz içerik |
| Textract | Taranmış **belge** | Metin, tablo, form alanları |
| Comprehend | Düz **metin** | Duygu, varlık, anahtar ifade, dil |

Ayırt edici soru: elimde **görüntü mü, belge mi, metin mi?**

> 📌 **Sık yapılan hata:** Faturadan tutar çıkarma senaryosuna Rekognition demek. Rekognition
> görüntüdeki yazıyı görür ama **form ve tablo yapısını** çıkaran Textract'tır.

🔗 Konu: [3.7 §1](3.7-ai-ml-analitik.md)

---

### Soru 2 — Transcribe ile Polly'nin yönü

**Kısa cevap:** **Transcribe: ses → metin.** **Polly: metin → ses.**

**Ayrıntı:** Hafıza kancası ikisinin adında saklı: *transcript* döküm demektir, yani sesi yazıya
geçirir; *Polly* ise papağan gibi **konuşur**.

| Senaryo | Servis |
|---|---|
| Çağrı merkezi kaydının dökümü, altyazı üretimi | Transcribe |
| Sesli haber okuma, telefon menüsü anonsu | Polly |
| Metni başka dile çevirme | Translate |
| Sohbet botu | Lex |

> 📌 **Sık yapılan hata:** İkisini "ses servisi" diye tek kefeye koymak. Sınav yönü doğrudan
> sorar; yönü karıştırmak doğrudan yanlış cevaptır.

🔗 Konu: [3.7 §1](3.7-ai-ml-analitik.md)

---

### Soru 3 — Athena ile Redshift arasındaki üç fark

**Kısa cevap:** **(1)** Athena veriyi **S3'te olduğu yerde** sorgular, Redshift kümesine
**yüklenir**. **(2)** Athena sunucusuzdur, Redshift küme yönetir. **(3)** Athena **taranan veri**
başına, Redshift **çalışma süresi** başına ücretlenir.

**Ayrıntı:**

| | Athena | Redshift |
|---|---|---|
| Veri nerede | S3'te | Kümeye yüklenir |
| Altyapı | Sunucusuz | Küme (sunucusuz seçeneği de var) |
| Ücret | Taranan veri | Küme süresi |
| Ne zaman | Ara sıra, geçici sorgular | Sürekli, ağır analitik iş yükü |

> 📌 **Sık yapılan hata:** "SQL var, öyleyse veri ambarı" demek. Sorgular **seyrekse** Athena
> hem daha ucuz hem daha az işletim yüklüdür.

🔗 Konu: [3.7 §3](3.7-ai-ml-analitik.md)

---

### Soru 4 — Data lake ile data warehouse farkı, hangi servisler

**Kısa cevap:** **Data lake** ham veriyi şemasını önceden tanımlamadan saklar — merkezi **S3**'tür.
**Data warehouse** temizlenmiş, yapılandırılmış veriyi tutar — **Redshift**'tir.

**Ayrıntı:**

| | Data lake | Data warehouse |
|---|---|---|
| Veri | Ham, her formatta | Temizlenmiş, yapılandırılmış |
| Şema | Okuma anında | Yazma anında |
| Servis | **S3** | **Redshift** |

Veri gölünün üzerinde çalışan takım: **Glue** (katalog ve dönüştürme), **Athena** (SQL),
**EMR** (büyük veri çatıları), **QuickSight** (görselleştirme), **SageMaker** (model),
**Lake Formation** (kurulum ve izinler).

> 📌 **Sık yapılan hata:** Veri gölünü bir servis sanmak. Veri gölü bir **yaklaşımdır**;
> AWS'teki karşılığı S3 üzerine kurulmuş bir servis kümesidir.

🔗 Konu: [3.7 §4](3.7-ai-ml-analitik.md)

---

### Soru 5 — Hazır AI servisi ile SageMaker'ı ne zaman seçersin

**Kısa cevap:** Hazır bir yetenek işini görüyorsa **AI servisi** (Rekognition, Comprehend…);
**kendi verinle kendi modelini** eğiteceksen **SageMaker AI**; hazır temel modelle üretken yapay
zekâ uygulaması geliştireceksen **Bedrock**.

**Ayrıntı:**

| Soru | Cevap |
|---|---|
| "Fotoğraftaki yüzleri tanı" | Rekognition |
| "Kendi verimizle özel bir tahmin modeli eğiteceğiz" | SageMaker AI |
| "Hazır büyük dil modeliyle uygulama yazacağız" | Bedrock |
| "AWS hakkında soru soran asistan" | Amazon Q |

Karar ölçütü: **model eğitmek gerekiyor mu?** Gerekmiyorsa hazır servis her zaman daha az
işletim yüklüdür.

> 📌 **Sık yapılan hata:** "Makine öğrenmesi" kelimesini görüp otomatik SageMaker demek. Sınav
> genelde hazır servisin yettiği senaryoyu kurar.

🔗 Konu: [3.7 §2](3.7-ai-ml-analitik.md)

---

## 3.8 Diğer servisler

📄 Sorular: [`3.8-diger-servisler.md`](3.8-diger-servisler.md)

### Soru 1 — SQS ile SNS arasındaki üç fark

**Kısa cevap:** **(1)** SQS kuyruktur, mesajı **bir** tüketici işler; SNS yayındır, mesaj **tüm
abonelere** gider. **(2)** SQS'te tüketici **çeker**, SNS **iter**. **(3)** SQS mesajı **saklar**
(varsayılan 4, en fazla 14 gün), SNS saklamaz.

**Ayrıntı:**

| | SQS | SNS |
|---|---|---|
| Model | Kuyruk, çekme | Yayın, itme |
| Alıcı | Bir mesaj → bir tüketici | Bir mesaj → tüm aboneler |
| Saklama | Var | **Yok** |
| Kullanım | Sipariş kuyruğu, yük tamponu | Bildirim, alarm, dağıtım |

Ezber cümlesi: **SQS bekletir, SNS dağıtır.**

> 📌 **Sık yapılan hata:** "Abone o an ayakta değilse SNS tekrar dener, sorun olmaz" sanmak.
> SNS mesajı saklamaz; kaybolmaması gerekiyorsa araya **SQS** konur.

🔗 Konu: [3.8 §1](3.8-diger-servisler.md) · 📖 [SQS](../00-baslangic/03-kavram-sozlugu.md#sqs)

---

### Soru 2 — Fan-out kalıbı nedir, hangi iki servisle kurulur

**Kısa cevap:** Bir olayın **birden çok bağımsız tüketiciye** dağıtılmasıdır; kurulumu
**SNS → birden çok SQS kuyruğu** şeklindedir.

**Ayrıntı:** İki servisin zayıf yanı birbirini kapatır:

| Servis | Zayıf yanı | Kalıptaki rolü |
|---|---|---|
| SNS | Mesajı saklamaz | Dağıtımı yapar |
| SQS | Bir mesajı tek tüketici işler | Her tüketici için mesajı **saklar** |

Sonuç: bir sipariş olayı aynı anda faturalama, kargo ve analitik kuyruklarına düşer; biri
çalışmıyorsa mesajı kendi kuyruğunda bekler.

> 📌 **Sık yapılan hata:** Fan-out'u tek başına SNS ile kurmak. Tüketicilerden biri o an
> ayakta değilse o mesaj kaybolur.

🔗 Konu: [3.8 §1](3.8-diger-servisler.md)

---

### Soru 3 — Systems Manager'ın dört bileşeni ve işleri

**Kısa cevap:** **Patch Manager** (yama dağıtımı) · **Session Manager** (anahtarsız sunucu
bağlantısı) · **Parameter Store** (yapılandırma ve sır saklama) · **Run Command** (filoda toplu
komut çalıştırma). Envanter yönetimi de bu ailededir.

**Ayrıntı:** Ortak amaç, sunucu filosunu **tek yerden ve elle bağlanmadan** işletmektir.

| Bileşen | Çözdüğü problem |
|---|---|
| Patch Manager | "100 sunucuya yamayı tek tek nasıl geçerim?" |
| Session Manager | "SSH anahtarı ve açık port olmadan nasıl bağlanırım?" |
| Parameter Store | "Yapılandırmayı koda gömmeden nerede tutarım?" |
| Run Command | "Aynı komutu tüm filoda nasıl çalıştırırım?" |

> 📌 **Sık yapılan hata:** Parameter Store ile Secrets Manager'ı eşitlemek. **Otomatik
> döndürme** (rotation) isteniyorsa cevap Secrets Manager'dır; Parameter Store'un standart
> parametreleri ücretsizdir ama kendiliğinden döndürmez.

🔗 Konu: [3.8 §2](3.8-diger-servisler.md) · [2.4 §5](../02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md)

---

### Soru 4 — EC2'ye SSH anahtarı olmadan nasıl bağlanılır

**Kısa cevap:** **Systems Manager Session Manager** ile. Anahtar da gerekmez, 22 numaralı portu
açmak da.

**Ayrıntı:** Güvenlik kazancı üç katmanlıdır:

| Klasik SSH | Session Manager |
|---|---|
| Anahtar dosyası dağıtılır ve saklanır | Anahtar yok |
| Gelen port açılır | **Açık port yok** |
| Oturum kaydı ayrıca kurulur | Oturumlar CloudTrail'e ve isteğe bağlı olarak S3'e **kaydedilir** |

Bağlantı, sunucudaki ajanın **dışarı doğru** kurduğu bağlantı üzerinden yürür; bu yüzden gelen
kural gerekmez.

> 📌 **Sık yapılan hata:** Atlama sunucusunu (bastion) doğru cevap sanmak. Bastion çalışır ama
> yönetilecek bir sunucu daha ve yine açık bir port demektir; `MOST secure` sorusunun cevabı
> Session Manager'dır.

🔗 Konu: [3.8 §2](3.8-diger-servisler.md)

---

### Soru 5 — SES ile SNS'i ne zaman ayırt edersin

**Kısa cevap:** Alıcı **müşteriyse ve gerçek bir e-posta** gönderilecekse **SES**; alıcı
sistem/operatörse ve bir **bildirim/alarm** dağıtılacaksa **SNS**.

**Ayrıntı:**

| Senaryo | Servis |
|---|---|
| Sipariş onayı, şifre sıfırlama, pazarlama bülteni | **SES** |
| CloudWatch alarmı ekibe düşsün | **SNS** |
| Bir olay hem e-postaya hem Lambda'ya hem kuyruğa gitsin | **SNS** |
| Çok kanallı müşteri kampanyası (SMS + e-posta + anlık bildirim) | **Pinpoint** |

SNS de e-posta gönderebilir; ama biçimlendirilmiş, ölçeklenen, teslim edilebilirliği yönetilen
müşteri e-postası SES'in işidir.

> 📌 **Sık yapılan hata:** "SNS e-posta atabiliyor, öyleyse pazarlama e-postası da atarım" demek.
> SNS'in e-postası bildirim içindir.

🔗 Konu: [3.8 §4](3.8-diger-servisler.md) · 📖 [SES](../00-baslangic/03-kavram-sozlugu.md#ses)

---

⬅️ [Bölüme dön](3.1-dagitim-isletim.md) · 📖 [Kavram sözlüğü](../00-baslangic/03-kavram-sozlugu.md) ·
⚙️ [Seçim rehberi](../00-baslangic/04-secim-rehberi.md)
