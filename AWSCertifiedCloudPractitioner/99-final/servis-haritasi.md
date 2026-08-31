# Servis Haritası — Tek Sayfa Cheatsheet

> **Kullanım:** Her servisin yanındaki tek cümleyi kapat, kendi kendine söyle, sonra aç ve kontrol et.
> Sınavda derinlik değil **kapsam** gerekiyor: her servis için tek cümlelik "bu neyi çözer" cevabın olsun yeter.
> ⭐ işaretliler neredeyse her sınavda çıkar.

---

## Hesaplama (Compute)

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **EC2** | Bulutta sanal sunucu; işletim sistemi üzerinde tam kontrol |
| ⭐ **Lambda** | Sunucusuz, olay tetikli kod; **maks 15 dakika**, çalıştığı kadar ödeme |
| **Elastic Beanstalk** | Kodu yükle, altyapıyı AWS kursun (PaaS) |
| **Lightsail** | Basit, **sabit aylık fiyatlı** sunucu paketi |
| **Batch** | Toplu (batch) işleri planla ve çalıştır |
| **Outposts** | AWS donanımı **senin veri merkezinde** |
| ⭐ **Auto Scaling** | Kapasiteyi talebe göre otomatik ayarla, sağlıksızı değiştir |
| ⭐ **ELB (ALB/NLB/GWLB)** | Trafiği sağlıklı hedeflere ve AZ'lere dağıt |
| **App Runner** | Konteynerden doğrudan yönetilen web servisi |

## Konteyner ve Sunucusuz

| Servis | Bu neyi çözer |
|---|---|
| **ECS** | AWS'in kendi konteyner orkestrasyonu |
| **EKS** | Yönetilen **Kubernetes** |
| **ECR** | Konteyner **imaj deposu** |
| ⭐ **Fargate** | Konteyneri **sunucu yönetmeden** çalıştır |

## Depolama (Storage)

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **S3** | Nesne depolama; sınırsız, 11 dokuz dayanıklılık, HTTP erişimi |
| ⭐ **S3 Glacier (Instant / Flexible / Deep Archive)** | Arşiv; Deep Archive **en ucuz**, erişim **saatler** |
| ⭐ **EBS** | EC2'ye takılan blok disk (**tek AZ**, kalıcı) |
| **Instance store** | Host'a bağlı **geçici** disk — instance durunca veri gider |
| ⭐ **EFS** | Çok sayıda **Linux** sunucunun paylaştığı NFS dosya sistemi |
| **FSx** | Windows (SMB/AD), Lustre (HPC), ONTAP, OpenZFS |
| **Storage Gateway** | Şirket içi uygulamaya **hibrit** bulut depolama |
| **AWS Backup** | Birçok servisin yedeğini **merkezî politikayla** yönet |
| **Elastic Disaster Recovery** | Sunucuları çoğalt, felakette hızlı devreye al |

## Veritabanı

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **RDS** | Yönetilen ilişkisel veritabanı (MySQL, PostgreSQL, Oracle, SQL Server, MariaDB, Db2) |
| **Aurora** | Bulut için tasarlanmış, MySQL/PostgreSQL uyumlu; 3 AZ'de 6 kopya |
| ⭐ **DynamoDB** | Sunucusuz NoSQL anahtar-değer; milisaniye altı |
| ⭐ **ElastiCache** | Redis/Memcached **bellek içi önbellek** |
| ⭐ **Redshift** | Petabayt ölçeğinde **veri ambarı (OLAP)** |
| **DocumentDB** | **MongoDB uyumlu** doküman veritabanı |
| **Neptune** | **Graf** veritabanı (ilişki ağları, öneri, dolandırıcılık) |
| **Keyspaces** | **Cassandra uyumlu** |
| **Timestream** | **Zaman serisi** (IoT, telemetri) |
| **DMS** | Veritabanını **çalışırken** AWS'e taşı |
| **SCT** | **Farklı motora** geçerken şemayı dönüştür |
| **RDS Proxy** | Veritabanı bağlantılarını havuzla |

## Ağ ve İçerik Dağıtımı

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **VPC** | İzole sanal ağ (subnet, route table, IGW, NAT) |
| ⭐ **CloudFront** | CDN — içeriği edge'de önbellekle, gecikmeyi düşür |
| ⭐ **Route 53** | DNS + domain kaydı + health check + yönlendirme politikaları |
| **API Gateway** | Sunucusuz REST/HTTP/WebSocket API katmanı |
| ⭐ **Direct Connect** | AWS'e **özel fiziksel hat** (internet kullanmaz) |
| ⭐ **Site-to-Site VPN** | İnternet üzerinden **şifreli tünel** |
| **Transit Gateway** | Çok sayıda VPC + şirket içi ağ için **merkezî hub** |
| **PrivateLink / VPC endpoint** | Servise **internete çıkmadan** erişim |
| **Global Accelerator** | Statik anycast IP + TCP/UDP hızlandırma + bölgesel failover |
| **VPC Peering** | İki VPC'yi bire bir bağla |
| **VPC Flow Logs** | Ağ trafiği meta verisini kaydet |

## Güvenlik ve Kimlik

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **IAM** | Kullanıcı, grup, **rol**, politika — kim neye erişir (**ücretsiz ve global**) |
| ⭐ **IAM Identity Center** | Çalışanlar için **tek oturumla çok hesaba** erişim |
| ⭐ **KMS** | Şifreleme anahtarı yönetimi (varsayılan tercih) |
| **CloudHSM** | **Adanmış, tek kiracılı** donanım HSM |
| ⭐ **GuardDuty** | **Anormal/kötü niyetli davranış** tespiti |
| ⭐ **Inspector** | EC2/konteyner/Lambda **zafiyet (CVE) taraması** |
| ⭐ **Macie** | **S3'te hassas veri (PII)** keşfi |
| **Detective** | Güvenlik bulgusunun **kök nedenini** araştır |
| **Security Hub** | Tüm güvenlik bulgularını **tek panelde** topla |
| ⭐ **WAF** | SQL injection, XSS, bot — **uygulama katmanı** filtresi |
| ⭐ **Shield** | DDoS koruması — **Standard ücretsiz**, Advanced ücretli |
| **Firewall Manager** | WAF/Shield/SG kurallarını **çok hesapta merkezî** yönet |
| ⭐ **Artifact** | AWS'in **uyumluluk raporları** (SOC, ISO, PCI) |
| **ACM** | SSL/TLS sertifikası sağla ve **otomatik yenile** |
| ⭐ **Secrets Manager** | Parola/anahtar sakla + **otomatik rotasyon** |
| **Cognito** | **Uygulamanın son kullanıcıları** için kayıt/giriş |
| **Directory Service** | Yönetilen **Active Directory** |
| **RAM** | Kaynakları hesaplar arasında paylaş |
| **Audit Manager** | Denetim **kanıtı toplamayı** otomatikleştir |

## Yönetim ve Yönetişim

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **CloudWatch** | Metrik, log, alarm — **sistem nasıl çalışıyor** |
| ⭐ **CloudTrail** | API çağrısı denetimi — **kim ne yaptı** |
| ⭐ **Config** | Konfigürasyon geçmişi ve **uyumluluk** — **kurala uygun mu** |
| ⭐ **Organizations** | Çok hesap + **consolidated billing** + **SCP** + OU |
| ⭐ **Trusted Advisor** | Hesabı tara, **5 kategoride** öneri ver |
| **Control Tower** | Çok hesaplı **landing zone**'u otomatik kur |
| ⭐ **CloudFormation** | Altyapıyı **kod (şablon)** olarak tanımla |
| **Systems Manager** | Patch Manager, **Session Manager**, Parameter Store, Run Command |
| **Service Catalog** | Kurumun **onaylı ürün kataloğu** |
| **Service Quotas** | Servis **limitlerini gör ve artır** |
| **Compute Optimizer** | ML ile **doğru instance boyutu** öner |
| **License Manager** | Yazılım **lisans takibi** |
| ⭐ **Health Dashboard** | AWS kesintileri + **hesabına özel** olaylar |
| **Well-Architected Tool** | Soru-cevap temelli **mimari öz-değerlendirme** |

## Maliyet Yönetimi

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **Pricing Calculator** | **Önden** maliyet tahmini |
| ⭐ **Cost Explorer** | **Geçmiş** harcama analizi + tahmin |
| ⭐ **Budgets** | Eşik aşılınca **uyarı** |
| **Cost and Usage Report (CUR)** | **En ayrıntılı** fatura verisi (S3 + Athena) |
| **Cost Anomaly Detection** | Harcama **sıçramasını** ML ile yakala |
| ⭐ **Marketplace** | Üçüncü parti yazılım, **AWS faturasına dahil** |

## Göç ve Transfer

| Servis | Bu neyi çözer |
|---|---|
| **Application Discovery Service** | Şirket içi sunucuları **envanterle** |
| **Application Migration Service (MGN)** | Sunucuları **lift-and-shift** ile taşı |
| **Migration Hub** | Göç projelerini **tek panelden izle** |
| **Migration Evaluator** | Göçün **iş gerekçesini/tasarrufunu** çıkar |
| **DMS / SCT** | Veritabanı taşı / şema dönüştür |
| ⭐ **Snow ailesi** | **İnternet yetmiyorsa** veriyi fiziksel taşı |
| **DataSync** | İnternet üzerinden **otomatik veri senkronu** |
| **Transfer Family** | **SFTP/FTPS/FTP** ile S3/EFS'e aktarım |

## Analitik

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **Athena** | **S3'teki veriye SQL** (sunucusuz, taranan veri kadar) |
| **Glue** | Sunucusuz **ETL** + veri kataloğu |
| **EMR** | Yönetilen **Spark/Hadoop** kümesi |
| ⭐ **Kinesis** | **Gerçek zamanlı akış** verisi |
| **Data Firehose** | Akış verisini S3/Redshift/OpenSearch'e teslim et |
| ⭐ **QuickSight** | **BI panoları** ve görselleştirme |
| **OpenSearch Service** | Arama ve **log analizi** |
| **Lake Formation** | **Data lake** kur ve izinleri yönet |
| **Data Exchange** | Üçüncü parti veri setlerine abone ol |

## Yapay Zekâ / Makine Öğrenmesi

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **Rekognition** | **Görüntü/video**: nesne, yüz, uygunsuz içerik |
| ⭐ **Textract** | **Taranmış belgeden** metin, tablo, form |
| ⭐ **Comprehend** | **Metnin anlamı**: duygu, varlık, anahtar ifade |
| ⭐ **Transcribe** | **Ses → metin** |
| ⭐ **Polly** | **Metin → ses** |
| **Translate** | Diller arası çeviri |
| **Lex** | Sohbet botu (Alexa'nın motoru) |
| **Kendra** | Kurumsal akıllı arama |
| **Personalize** | Öneri motoru |
| **Fraud Detector** | Dolandırıcılık tespiti |
| **Forecast** | Zaman serisi tahmini |
| ⭐ **SageMaker AI** | **Kendi modelini** eğit ve yayınla |
| **Bedrock** | **Temel modellerle** üretken yapay zekâ |
| **Amazon Q** | AWS'in üretken yapay zekâ **asistanı** |

## Entegrasyon ve Geliştirici Araçları

| Servis | Bu neyi çözer |
|---|---|
| ⭐ **SQS** | **Kuyruk** — mesajı **sakla**, tüketici çeksin (loose coupling) |
| ⭐ **SNS** | **Pub/sub** — bir mesajı **tüm abonelere** gönder |
| **EventBridge** | Olayları **kurala göre** hedefe yönlendir (+ zamanlanmış tetikleme) |
| **Step Functions** | Çok adımlı **iş akışı** (durum makinesi) |
| **CodeBuild / CodePipeline / CodeDeploy** | Derleme / **CI-CD boru hattı** / dağıtım |
| **X-Ray** | Mikroservislerde isteği **uçtan uca izle** |
| **Amplify** | Web/mobil **ön yüz** geliştirme ve barındırma |
| **CDK** | Altyapıyı **programlama diliyle** yaz |
| **Cloud9** | Tarayıcıda bulut IDE |

## Son Kullanıcı ve İş Uygulamaları

| Servis | Bu neyi çözer |
|---|---|
| **WorkSpaces** | Yönetilen **sanal masaüstü** |
| **AppStream 2.0** | **Tek uygulamayı** tarayıcıya akıt |
| **WorkSpaces Secure Browser** | Yönetilen, izole **güvenli tarayıcı** |
| **Connect** | Bulut **çağrı merkezi** |
| **SES** | **İşlemsel/toplu e-posta** gönderimi |
| **Pinpoint** | Çok kanallı müşteri kampanyaları |
| **IoT Core** | IoT **cihazlarını** bağla ve yönet |
| **AWS Support** | Destek planları (Basic → Enterprise) |

---

## ⭐⭐ En sık karıştırılan 12 ikili/üçlü — sınavdan önce son kez oku

| Karışan | Ayırt edici |
|---|---|
| **CloudTrail / CloudWatch / Config** | kim ne yaptı / sistem nasıl / kurala uygun mu |
| **GuardDuty / Inspector / Macie / Detective** | davranış / zafiyet / S3'te PII / kök neden |
| **WAF / Shield** | uygulama saldırısı (SQLi, XSS) / DDoS |
| **Security Group / NACL** | instance+stateful+sadece allow / subnet+stateless+allow&deny |
| **KMS / CloudHSM** | varsayılan, çok kiracılı / adanmış, tek kiracılı |
| **Multi-AZ / Read Replica** | ayakta kal (senkron, failover) / hızlan (asenkron, okuma) |
| **SQS / SNS** | bekletir (kuyruk, pull) / dağıtır (pub-sub, push) |
| **Pricing Calculator / Cost Explorer / Budgets** | önce / sonra / uyarı |
| **Reserved / Savings Plans / Spot** | belirli tip taahhüdü / esnek $ taahhüdü / %90 ucuz ama kesilir |
| **CloudFront / Global Accelerator** | önbelleklenebilir HTTP içerik / TCP-UDP + statik IP |
| **Artifact / Audit Manager / Config** | AWS'in raporları / senin kanıtın / senin uyumluluğun |
| **Identity Center / Cognito / Directory Service** | çalışanlar / uygulama müşterileri / Active Directory |
| **Rehost / Replatform / Refactor** | değişiklik yok / küçük dokunuş / yeniden yaz |
| **Region / AZ / Edge Location** | coğrafi bölge / izole veri merkezi (HA) / önbellek noktası (gecikme) |

---

## Ezberlenmesi gereken sayılar

| Ne | Değer |
|---|---|
| Sınav | 65 soru / 90 dakika / geçme **700** / 100 USD / 3 yıl |
| Region başına AZ | **En az 3** |
| S3 Standard dayanıklılık | **%99,999999999 (11 dokuz)** |
| S3 tek nesne maksimum | **5 TB** |
| S3 IA minimum saklama | **30 gün** |
| S3 Glacier minimum saklama | **90 gün** |
| S3 Deep Archive minimum saklama | **180 gün** |
| Lambda maksimum çalışma süresi | **15 dakika** |
| SQS mesaj saklama | Varsayılan **4 gün**, maksimum **14 gün** |
| Spot geri alma uyarısı | **2 dakika** |
| Spot indirimi | **%90'a kadar** |
| Savings Plans / RI indirimi | **%72'ye kadar** |
| Business Support — production down | **< 1 saat** |
| Enterprise On-Ramp — business-critical down | **< 30 dakika** |
| Enterprise — business-critical down | **< 15 dakika** |
| Trusted Advisor kategori sayısı | **5** |
| Well-Architected sütun sayısı | **6** |
| CAF perspektif sayısı | **6** |
| Göç stratejisi sayısı | **7 (7R)** |
| Sınavda kaldıysan bekleme | **14 gün** |

---

➡️ Sırada: [`son-tekrar.md`](son-tekrar.md) · sonra [`final-sinav-1.html`](final-sinav-1.html)
