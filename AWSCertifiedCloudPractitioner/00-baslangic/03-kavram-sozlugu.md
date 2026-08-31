# 00.4 — Kavram sözlüğü (kısaltmalar, uyumluluk standartları, servis adları)

> **Bu dosya baştan sona okunmak için değil, dönülmek için var.**
> Konu dosyalarında bir kısaltma ilk geçtiğinde yanında kısa açıklaması ve buraya bağlantısı olacak.

**Nasıl kullanılır:** Konu dosyasında şuna benzer bir ifade görürsün:

> …bir bölge içinde birbirinden bağımsız veri merkezi kümeleri, yani **Availability Zone**
> ([bir bölge içindeki bağımsız veri merkezi ↗](03-kavram-sozlugu.md#az)) kullanılır.

Parantez içi kısa açıklama anlık cevabı verir; **↗** bağlantısı buradaki tam tanıma götürür.

> **Servislerin tam listesi burada değil.** 100'den fazla servisin tek cümlelik tanımı için
> [`99-final/servis-haritasi.md`](../99-final/servis-haritasi.md)'ye bak. Burada yalnızca
> **adından ne yaptığı anlaşılmayanlar** ve sınav dilinin kavramları var.

**İçindekiler:** [1. Kavramlar ve kısaltmalar](#1-kavramlar-ve-kısaltmalar) ·
[2. Uyumluluk standartları ve programlar](#2-uyumluluk-standartları-ve-programlar) ·
[3. Harflerden ne yaptığı anlaşılmayan servis adları](#3-harflerden-ne-yaptığı-anlaşılmayan-servis-adları)

---

## 1. Kavramlar ve kısaltmalar

<a id="agility"></a>
#### Agility
*(çeviklik)* — Bir kaynağı **ne kadar hızlı hayata geçirebildiğin**. Sunucu için 6 hafta beklemek
yerine 3 dakikada açabilmek agility'dir. ⚠️ [Elasticity](#elasticity) ile karıştırılır: agility
**başlatma hızıyla**, elasticity **talebe göre otomatik uyum** ile ilgilidir.
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="ami"></a>
#### AMI
*(Amazon Machine Image — makine görüntüsü)* — Bir sunucunun işletim sistemi, ayarları ve
kurulu yazılımlarını içeren şablon. Yeni sunucu bu şablondan **dakikalar içinde** üretilir.
→ [3.3](../03-teknoloji-servisler/3.3-compute.md)

<a id="arn"></a>
#### ARN
*(Amazon Resource Name — kaynak adı)* — Her AWS kaynağının dünya çapında benzersiz kimliği:
`arn:aws:s3:::sirket-yedek`. Yetki politikalarında "hangi kaynak" sorusu bununla yazılır.
→ [2.3](../02-guvenlik-uyumluluk/2.3-iam.md)

<a id="az"></a>
#### AZ
*(Availability Zone — erişilebilirlik bölgesi)* — Bir [Region](#region) içindeki, kendi elektriği
ve soğutması olan **bağımsız veri merkezi kümesi**. Her Region'da en az 3 tane vardır; aralarındaki
gecikme milisaniyenin altındadır. ⚠️ İki AZ'a yayılmak [yüksek erişilebilirliğin](#high-availability)
en ucuz yoludur. → [3.2](../03-teknoloji-servisler/3.2-global-altyapi.md)

<a id="byol"></a>
#### BYOL
*(Bring Your Own License — kendi lisansını getir)* — Elindeki Windows/Oracle lisansını buluta
taşıyıp yalnızca donanıma ödeme. Karşıtı **License Included**: lisans ücreti saatlik fiyatın
içindedir, ayrıca lisans satın almazsın.
→ [1.4](../01-bulut-kavramlari/1.4-bulut-ekonomisi.md)

<a id="caf"></a>
#### CAF
*(Cloud Adoption Framework — buluta geçiş çerçevesi)* — AWS'in "kurumu buluta nasıl taşırsın"
rehberi. Altı **perspektifi** var: iş tarafında *Business · People · Governance*, teknik tarafta
*Platform · Security · Operations*. ⚠️ [7R](#7r) göç **stratejisidir**, CAF **kurumsal hazırlıktır**.
→ [1.3](../01-bulut-kavramlari/1.3-migration.md)

<a id="capex"></a>
#### CapEx
*(Capital Expenditure — sermaye gideri)* — Peşin, büyük, tek seferlik yatırım: sunucu satın almak,
veri merkezi kurmak. Bulutun temel vaadi bunu [OpEx](#opex)'e çevirmektir.
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="disaster-recovery"></a>
#### Disaster recovery
*(felaket kurtarma — kısaltması DR)* — Bir bölgeyi tümden kaybetmek gibi **felaket sonrası**
sistemi geri getirme planı. Ölçütleri [RTO](#rto) ve [RPO](#rpo)'dur.
⚠️ [Yüksek erişilebilirlik](#high-availability) günlük çalışmayı, felaket kurtarma **felaketi**
konuşur. → [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="durability"></a>
#### Durability
*(dayanıklılık)* — Verinin **kaybolmama** olasılığı. S3 Standard 11 dokuz (%99,999999999)
dayanıklılık için tasarlanmıştır. ⚠️ Dayanıklılık ≠ [erişilebilirlik](#availability): veri duruyor
olabilir ama o an ulaşılamayabilir. → [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="availability"></a>
#### Availability
*(erişilebilirlik)* — Veriye ya da servise **o an ulaşabilme** olasılığı; S3 Standard için
%99,99 hedeflenir. Karşılaştır: [durability](#durability).
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="elasticity"></a>
#### Elasticity
*(elastikiyet)* — Talep artınca **otomatik büyüme**, azalınca **otomatik küçülme**.
⚠️ [Scalability](#scalability) yalnızca büyüyebilmeyi anlatır, küçülmeyi garanti etmez;
sınavda `automatically` + `scale in` ikilisini görürsen cevap elasticity'dir.
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="esl"></a>
#### ESL hakkı
*(English as a Second Language accommodation — ana dili İngilizce olmayanlar için ek süre)* —
Sınav İngilizce olduğu için **30 dakika ek süre** talep hakkı. ⚠️ Sınavı **satın almadan önce**
istenir; sonradan verilmez. → [00.1](00-sinav-kunyesi.md)

<a id="fault-tolerance"></a>
#### Fault tolerance
*(hata toleransı)* — Bir bileşen çökse bile sistemin **hiç kesintisiz** çalışmaya devam etmesi.
⚠️ [Yüksek erişilebilirlikten](#high-availability) daha güçlü ve daha pahalı bir taahhüt:
o kısa bir kesintiyi kabul eder, bu etmez. → [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="free-tier"></a>
#### Free Tier
*(ücretsiz kullanım katmanı)* — Yeni hesaplarda belirli servislerin belirli miktarlarının
ücretsiz olduğu paket. ⚠️ Sınırı aşınca **uyarı gelmez, fatura gelir** — bu yüzden bütçe alarmı
kurulur. Kapsam değişebilir; [resmî sayfadan teyit et](https://aws.amazon.com/free/).
→ [00.2](01-aws-hesabi-kurulum.md)

<a id="high-availability"></a>
#### High availability
*(yüksek erişilebilirlik)* — Sistemin **çok kısa kesintiyle** ayakta kalması; tipik kurulumu
birden çok [AZ](#az) + yük dengeleyicidir. Karşılaştır: [fault tolerance](#fault-tolerance) ·
[disaster recovery](#disaster-recovery). → [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="ia"></a>
#### IA
*(Infrequent Access — seyrek erişim)* — S3 ve EFS sınıf adlarında geçer (`S3 Standard-IA`).
Depolama ucuz, **okuma ücretlidir** ve en az 30 günlük saklama varsayılır.
→ [3.6](../03-teknoloji-servisler/3.6-storage.md)

<a id="iops"></a>
#### IOPS
*(Input/Output Operations Per Second — saniyedeki okuma/yazma işlemi)* — Diskin **hız birimi**.
Veritabanı gibi çok sayıda küçük işlem yapan yükler yüksek IOPS ister.
→ [3.6](../03-teknoloji-servisler/3.6-storage.md)

<a id="least-privilege"></a>
#### En az yetki ilkesi
*(least privilege)* — Bir kimliğe **işini görecek kadar**, bir damla fazla değil yetki vermek.
Sınavda `LEAST privilege` / `MOST secure` ifadeleri bu ilkeyi işaret eder.
→ [2.3](../02-guvenlik-uyumluluk/2.3-iam.md)

<a id="loose-coupling"></a>
#### Loose coupling
*(gevşek bağlılık)* — Bileşenlerin birbirine doğrudan değil, arada bir kuyruk ya da yük dengeleyici
üzerinden bağlanması; biri çökünce diğeri devam eder. AWS'te tipik araçları SQS, SNS ve Elastic
Load Balancing'dir. → [1.2](../01-bulut-kavramlari/1.2-well-architected.md)

<a id="mfa"></a>
#### MFA
*(Multi-Factor Authentication — çok adımlı kimlik doğrulama)* — Parolaya ek olarak telefondaki
uygulamadan gelen tek kullanımlık kod ya da fiziksel anahtar isteme. ⚠️ Hesabın **kök kullanıcısında**
açılması ilk yapılacak iştir. → [2.3](../02-guvenlik-uyumluluk/2.3-iam.md)

<a id="nacl"></a>
#### NACL
*(Network Access Control List — ağ erişim listesi)* — Bir alt ağın (subnet) önündeki, **durumsuz**
ve **reddetme kuralı yazılabilen** güvenlik filtresi. ⚠️ Karşıtı [güvenlik grubu](#security-group):
o sunucunun önünde durur, durumludur ve yalnızca izin yazılır.
→ [2.4](../02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md)

<a id="olap"></a>
#### OLAP
*(Online Analytical Processing — çevrimiçi analitik işleme)* — Milyonlarca satırı tarayıp rapor
üreten **analiz** yükü; AWS'teki karşılığı Redshift. Karşıtı [OLTP](#oltp).
→ [3.4](../03-teknoloji-servisler/3.4-veritabanlari.md)

<a id="oltp"></a>
#### OLTP
*(Online Transaction Processing — çevrimiçi işlem işleme)* — Sipariş kaydetme gibi çok sayıda
**küçük ve hızlı** işlem; AWS'teki karşılığı RDS ve Aurora. Karşıtı [OLAP](#olap).
→ [3.4](../03-teknoloji-servisler/3.4-veritabanlari.md)

<a id="opex"></a>
#### OpEx
*(Operational Expenditure — işletme gideri)* — Kullandıkça ödenen, aylık akan gider.
[CapEx](#capex)'in karşıtı ve bulutun temel finansal vaadi.
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="ou"></a>
#### OU
*(Organizational Unit — kurumsal birim)* — AWS Organizations'ta hesapları grupladığın klasör.
Politika bir OU'ya uygulanınca altındaki bütün hesaplara iner.
→ [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

<a id="region"></a>
#### Region
*(bölge)* — Dünyanın belirli bir coğrafyasındaki, birbirinden bağımsız [AZ](#az)'lardan oluşan
küme (`eu-central-1` gibi). ⚠️ Çoğu servis **Region bazlıdır**: bir bölgede açtığın kaynak
diğerinde görünmez. → [3.2](../03-teknoloji-servisler/3.2-global-altyapi.md)

<a id="right-sizing"></a>
#### Right-sizing
*(doğru boyutlandırma)* — Kullanılmayan kapasiteyi tespit edip makineyi küçültmek.
Öneriyi Compute Optimizer ve Cost Explorer verir.
→ [1.4](../01-bulut-kavramlari/1.4-bulut-ekonomisi.md)

<a id="rpo"></a>
#### RPO
*(Recovery Point Objective — kabul edilen veri kaybı)* — Felakette **en fazla ne kadarlık veriyi**
kaybetmeyi göze aldığın. "RPO 15 dakika" = en fazla son 15 dakika kaybolabilir. Karşılaştır:
[RTO](#rto). → [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="rto"></a>
#### RTO
*(Recovery Time Objective — kabul edilen kesinti süresi)* — Felaketten sonra sistemin **ne kadar
sürede** ayağa kalkması gerektiği. Karşılaştır: [RPO](#rpo).
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="scalability"></a>
#### Scalability
*(ölçeklenebilirlik)* — Yükün artmasına **büyüyerek** cevap verebilme. İki yönü var:
**scale up** (aynı makineyi büyütmek) ve **scale out** (makine sayısını artırmak; AWS'in tercihi).
⚠️ [Elasticity](#elasticity) ile karıştırılır. → [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="scp"></a>
#### SCP
*(Service Control Policy — servis kontrol politikası)* — AWS Organizations'ta bir hesabın ya da
[OU](#ou)'nun **yapabileceklerinin tavanını** çizen kural. ⚠️ Yetki **vermez**, yalnızca sınırlar:
SCP izin verse bile kimlik politikası vermediyse iş olmaz. Yönetim (management) hesabını etkilemez.
→ [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

<a id="security-group"></a>
#### Güvenlik grubu
*(security group)* — Bir sunucunun önündeki, **durumlu** (giden isteğin cevabı otomatik geçer) ve
yalnızca **izin** yazılabilen sanal güvenlik duvarı. Karşıtı [NACL](#nacl).
→ [2.4](../02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md)

<a id="sla"></a>
#### SLA
*(Service Level Agreement — hizmet seviyesi taahhüdü)* — AWS'in bir servis için verdiği yazılı
erişilebilirlik sözü; tutulmazsa fatura kredisi doğar.
→ [4.3](../04-faturalama-destek/4.3-destek-planlari.md)

<a id="sorumluluk-paylasimi"></a>
#### Paylaşılan sorumluluk modeli
*(shared responsibility model)* — Güvenliğin ikiye bölünmesi: **bulutun kendisinin güvenliği**
(donanım, tesis, altyapı) AWS'te; **bulutun içindekilerin güvenliği** (veri, yetki, ayar) müşteride.
⚠️ Sınavın en çok soru üreten tek kavramı. → [2.1](../02-guvenlik-uyumluluk/2.1-paylasilan-sorumluluk.md)

<a id="tam"></a>
#### TAM
*(Technical Account Manager — teknik hesap yöneticisi)* — Kuruma atanmış, mimarisini tanıyan AWS
çalışanı. Enterprise planında **atanmış** olarak, Enterprise On-Ramp'te **havuzdan** gelir.
→ [4.3](../04-faturalama-destek/4.3-destek-planlari.md)

<a id="tco"></a>
#### TCO
*(Total Cost of Ownership — toplam sahip olma maliyeti)* — Sadece sunucu fiyatı değil; elektrik,
soğutma, yer kirası, donanım yenileme, yedekleme ve **personel** dahil bütün maliyet.
Bulut karşılaştırmaları bunun üzerinden yapılır. → [1.4](../01-bulut-kavramlari/1.4-bulut-ekonomisi.md)

<a id="undifferentiated-heavy-lifting"></a>
#### Undifferentiated heavy lifting
*(fark yaratmayan ağır iş)* — Sunucu rafa takmak, disk değiştirmek gibi, müşteriye hiçbir değer
katmadığı hâlde zaman yiyen işler. Sınavda bu ifadeyi görürsen cevap genelde "AWS bu işi üstlenir".
→ [1.1](../01-bulut-kavramlari/1.1-bulut-faydalari.md)

<a id="7r"></a>
#### 7R
*(yedi göç stratejisi)* — Bir uygulamayı buluta taşımanın yedi yolu: **Rehost** (olduğu gibi taşı) ·
**Replatform** (küçük iyileştirmeyle taşı) · **Repurchase** (hazır ürüne geç) · **Refactor**
(yeniden yaz) · **Retire** (kapat) · **Retain** (yerinde bırak) · **Relocate** (sanal ortamı toptan taşı).
→ [1.3](../01-bulut-kavramlari/1.3-migration.md)

---

## 2. Uyumluluk standartları ve programlar

> Bunlar AWS'in **kendi** aldığı denetim belgeleridir; müşteri bunları
> **AWS Artifact** üzerinden indirir. Kimin hangi belgeye ihtiyacı olduğu sınavda
> senaryo olarak sorulur.

<a id="fedramp"></a>
#### FedRAMP
ABD federal kurumlarının bulut kullanımı için zorunlu güvenlik değerlendirme programı.
Senaryoda "ABD kamu kurumu" geçiyorsa akla bu gelir.
→ [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

<a id="gdpr"></a>
#### GDPR
*(General Data Protection Regulation — Avrupa veri koruma tüzüğü)* — Avrupa Birliği'ndeki kişisel
veriler için kurallar. Türkiye'deki karşılığı KVKK'dır. Veri **hangi Region'da durduğu**
sorusunu doğuran şey budur. → [3.2](../03-teknoloji-servisler/3.2-global-altyapi.md)

<a id="hipaa"></a>
#### HIPAA
ABD'de **sağlık verisi** mahremiyetini düzenleyen yasa. Senaryoda hasta kaydı geçiyorsa aranan
kelime budur. → [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

<a id="iso-27001"></a>
#### ISO 27001
Bilgi güvenliği yönetim sistemi standardı. Denetçi "belgenizi görebilir miyim" derse cevap:
**AWS Artifact**. → [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

<a id="pci-dss"></a>
#### PCI DSS
*(Payment Card Industry Data Security Standard — kart verisi güvenlik standardı)* — Kredi kartı
verisi işleyen herkesi bağlar. → [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

<a id="soc"></a>
#### SOC 1 / SOC 2 / SOC 3
*(Service Organization Control — hizmet kuruluşu denetim raporları)* — Bağımsız denetçinin AWS
kontrolleri hakkındaki raporu. **SOC 1** finansal raporlamaya, **SOC 2** güvenlik ve gizliliğe
odaklanır ve gizlilik anlaşmasıyla verilir; **SOC 3** herkese açık özettir.
→ [2.2](../02-guvenlik-uyumluluk/2.2-yonetisim-uyumluluk.md)

---

## 3. Harflerden ne yaptığı anlaşılmayan servis adları

> Tam liste [`99-final/servis-haritasi.md`](../99-final/servis-haritasi.md)'de.
> Burada yalnızca **adı kendini açıklamayanlar** var.

<a id="acm"></a>
#### ACM
*(AWS Certificate Manager)* — HTTPS sertifikalarını ücretsiz üretip **kendiliğinden yenileyen**
servis. → [2.4](../02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md)

<a id="alb"></a>
#### ALB / NLB
*(Application / Network Load Balancer)* — Yük dengeleyicinin iki türü. **ALB** HTTP ve HTTPS
trafiğini adres yoluna göre dağıtır; **NLB** çok yüksek hızda ve TCP/UDP seviyesinde çalışır.
→ [3.3](../03-teknoloji-servisler/3.3-compute.md)

<a id="dms"></a>
#### DMS / SCT
*(Database Migration Service / Schema Conversion Tool)* — Veritabanı göçünün iki aracı:
**SCT** şemayı hedef veritabanının diline çevirir (**önce** o çalışır), **DMS** veriyi kaynak
çalışmaya devam ederken taşır. → [1.3](../01-bulut-kavramlari/1.3-migration.md)

<a id="ebs"></a>
#### EBS
*(Elastic Block Store)* — Bir sunucuya takılan **kalıcı sanal disk**. ⚠️ Karıştırılan
**instance store** sunucunun içindeki geçici diskidir: makine durunca içindekiler silinir.
→ [3.6](../03-teknoloji-servisler/3.6-storage.md)

<a id="ec2"></a>
#### EC2
*(Elastic Compute Cloud)* — AWS'in **sanal sunucusu**. Sınavda "sunucu" denen her yerde kastedilen
budur. → [3.3](../03-teknoloji-servisler/3.3-compute.md)

<a id="ecs"></a>
#### ECS / EKS / ECR / Fargate
*(Elastic Container Service / Elastic Kubernetes Service / Elastic Container Registry)* —
Konteyner dörtlüsü: **ECS** AWS'in kendi konteyner yöneticisi, **EKS** Kubernetes'in yönetilen
hâli, **ECR** konteyner imajlarının deposu, **Fargate** ise ikisinin altında sunucu yönetmeden
çalıştırma seçeneği. → [3.3](../03-teknoloji-servisler/3.3-compute.md)

<a id="efs"></a>
#### EFS / FSx
*(Elastic File System)* — Aynı anda **birçok sunucunun bağlanabildiği** dosya sistemi.
**EFS** Linux, **FSx** Windows ve özel dosya sistemleri içindir.
→ [3.6](../03-teknoloji-servisler/3.6-storage.md)

<a id="elb"></a>
#### ELB
*(Elastic Load Balancing)* — Gelen trafiği birden çok sunucuya dağıtan servis ailesi;
türleri [ALB ve NLB](#alb). → [3.3](../03-teknoloji-servisler/3.3-compute.md)

<a id="iam"></a>
#### IAM
*(Identity and Access Management — kimlik ve erişim yönetimi)* — Kimin neye erişebileceğini
belirleyen servis. **Ücretsizdir** ve **global**dir. → [2.3](../02-guvenlik-uyumluluk/2.3-iam.md)

<a id="igw"></a>
#### IGW / NAT Gateway
*(Internet Gateway)* — **IGW** bir sanal ağı internete **iki yönlü** bağlar; **NAT Gateway** ise
özel alt ağdaki sunucuların internete **yalnızca çıkmasına** izin verir, dışarıdan gelen isteği
geçirmez. → [3.5](../03-teknoloji-servisler/3.5-networking.md)

<a id="kms"></a>
#### KMS / CloudHSM
*(Key Management Service / Hardware Security Module)* — Şifreleme anahtarlarını üreten ve saklayan
servisler. **KMS** yönetilen ve paylaşımlı donanım kullanır; **CloudHSM** yalnızca sana ayrılmış
fiziksel cihaz verir ve anahtara **AWS bile erişemez**.
→ [2.4](../02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md)

<a id="rds"></a>
#### RDS
*(Relational Database Service)* — Yönetilen ilişkisel veritabanı: yedekleme, yama ve yük devretme
AWS'te. → [3.4](../03-teknoloji-servisler/3.4-veritabanlari.md)

<a id="s3"></a>
#### S3
*(Simple Storage Service)* — **Nesne** deposu: dosyayı bir kovaya (bucket) atarsın, adresi olur.
İşletim sistemine disk olarak takılmaz. → [3.6](../03-teknoloji-servisler/3.6-storage.md)

<a id="ses"></a>
#### SES
*(Simple Email Service)* — Müşteriye **e-posta** gönderme servisi. ⚠️ [SNS](#sns) bildirim dağıtır,
SES pazarlama/işlem e-postası yollar. → [3.8](../03-teknoloji-servisler/3.8-diger-servisler.md)

<a id="sns"></a>
#### SNS
*(Simple Notification Service)* — Bir mesajı **aynı anda birçok aboneye** iten bildirim servisi.
Karşılaştır: [SQS](#sqs). → [3.8](../03-teknoloji-servisler/3.8-diger-servisler.md)

<a id="sqs"></a>
#### SQS
*(Simple Queue Service)* — Mesajları **kuyrukta bekleten**, alıcı hazır olunca çektiği servis.
⚠️ [SNS](#sns) iter ve saklamaz, SQS bekletir ve çekilir.
→ [3.8](../03-teknoloji-servisler/3.8-diger-servisler.md)

<a id="vpc"></a>
#### VPC
*(Virtual Private Cloud — sanal özel bulut)* — AWS içinde sana ayrılmış, adres aralığını kendin
belirlediğin **özel ağ**. → [3.5](../03-teknoloji-servisler/3.5-networking.md)

<a id="waf"></a>
#### WAF / Shield
*(Web Application Firewall)* — **WAF** web isteklerinin içeriğine bakıp zararlı olanı (SQL enjeksiyonu
gibi) eler; **Shield** hacim tabanlı saldırıya (DDoS) karşı korur ve **Standard sürümü herkes için
ücretsiz açıktır**. → [2.4](../02-guvenlik-uyumluluk/2.4-guvenlik-servisleri.md)

---

⬅️ [Başlangıç klasörüne dön](00-sinav-kunyesi.md) · ⚙️ [Seçim rehberi](04-secim-rehberi.md) ·
🗺 [Servis haritası](../99-final/servis-haritasi.md)
