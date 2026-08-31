# 00.5 — Seçim rehberi: hangi servis, hangi model, ne zaman

> **Alan:** Başvuru dosyası — baştan sona okunmaz, **karar verirken** açılır.
> **Süre:** İlk okuma ~20 dakika; sonrası ihtiyaç anında.

CLF-C02'nin soru kalıbı neredeyse hep aynıdır: bir senaryo verilir, dört servis (ya da dört
fiyat modeli) sıralanır, **"hangisi?"** diye sorulur. Bu dosya o kararın rehberidir.

[Kavram sözlüğü](03-kavram-sozlugu.md) *"bu ne demek?"* sorusunu cevaplar; bu dosya
**üç** soruyu cevaplar:

- **Ne yapar?**
- **Ne zaman seçilir?** — hangi kelimeyi görünce akla gelir
- **Nasıl düşünülür?** — seçerken hangi büyüklük hesaba katılır

> ⚠️ **Servis adı ezberleme, ayırt edici kelimeyi ezberle.** Sınavda dört şıkkın dördü de
> gerçek AWS servisidir ve dördü de "yapabilir" görünür. Soruyu çözen şey senaryodaki
> **tek bir kelimedir**: `automatically`, `least operational overhead`, `no code change`,
> `long-term`, `unpredictable`, `archive`, `on-premises`.

**İçindekiler:** [1. Karar çerçevesi](#1-karar-çerçevesi--seçmeden-önce-beş-soru) ·
[2. Karar reçeteleri](#2-karar-reçeteleri--senaryodan-servis-setine) ·
[3. Hesaplama](#3-hesaplama-seçimi) · [4. Depolama](#4-depolama-seçimi) ·
[5. Veritabanı](#5-veritabanı-seçimi) · [6. Ağ ve bağlantı](#6-ağ-ve-bağlantı-seçimi) ·
[7. Fiyat modeli](#7-fiyat-modeli-seçimi) · [8. Destek planı](#8-destek-planı-seçimi) ·
[9. Güvenlik](#9-güvenlik-servisi-seçimi) · [10. Tehlikeli seçimler](#10--tehlikeli-seçimler--geri-dönüşü-pahalı-olanlar) ·
[11. Anahtar kelime sözlüğü](#11-anahtar-kelime--servis-tablosu)

---

## 1. Karar çerçevesi — seçmeden önce beş soru

| # | Soru | Neden belirleyici |
|---|---|---|
| 1 | **Sorumluluk kimde olsun?** Sunucuyu ben mi yöneteyim, AWS mi? | `least operational overhead` ifadesi görülürse cevap **her zaman** yönetilen servistir: EC2 değil Lambda, kendi kurduğun MySQL değil RDS |
| 2 | **Yük ne kadar öngörülebilir?** Sabit mi, dalgalı mı, kesintiye dayanır mı? | Sabit → Reserved / Savings Plans · Dalgalı → On-Demand + Auto Scaling · Kesintiye dayanır → Spot |
| 3 | **Veri ne sıklıkta okunacak?** Günde mi, yılda mı, hiç mi? | S3 sınıfını bu belirler; yanlış sınıf ya pahalı ya erişilemez olur |
| 4 | **Kod değişecek mi?** | `no application changes` → Rehost/Replatform, `refactor` yasak; `serverless` → kod değişir |
| 5 | **Nerede durmak zorunda?** Belirli bir ülke, kendi veri merkezin, uçta? | Yasal zorunluluk Region'ı, `on-premises` şartı Outposts'u, `low latency to users` CloudFront'u işaret eder |

⚠️ **Sınav refleksi:** "en ucuz" ile "en az işletim yükü" **çakışırsa**, soruda hangisi
yazılıysa o kazanır. `MOST cost-effective` ile `LEAST operational overhead` farklı cevaplar üretir.

---

## 2. Karar reçeteleri — senaryodan servis setine

### Reçete A — "Web uygulaması, trafiği gün içinde dalgalı, kesinti istemiyoruz"

```
EC2 Auto Scaling group  (en az 2 AZ)
+ Application Load Balancer
+ RDS Multi-AZ            (veritabanı yük devretmesi)
+ S3                      (statik dosyalar)
+ CloudFront              (kullanıcıya yakın önbellek)
```

**Bedeli:** İki AZ = iki kat sunucu maliyeti. Multi-AZ, tek AZ'a göre yaklaşık **iki katı**
fiyatlıdır — okuma performansı **artmaz**, yalnızca dayanıklılık artar.

### Reçete B — "Ayda birkaç kez çalışan iş, sunucu yönetmek istemiyoruz"

```
Lambda  +  EventBridge (zamanlayıcı)  +  DynamoDB  +  S3
```

**Bedeli:** İstek başına ödeme çok ucuz ama tek çalışma **15 dakikayı** aşamaz.
Uzun süren iş için Lambda yanlış cevaptır — ECS/Fargate ya da Batch'e bak.

### Reçete C — "Yerinde (on-premises) veri merkezini buluta taşıyoruz, kod değişmesin"

```
1) Migration Evaluator / Application Discovery Service   → envanter
2) SCT                                                    → şema çevirisi (önce bu)
3) DMS                                                    → veri taşıma (kaynak çalışırken)
4) Application Migration Service                          → sunucuların rehost'u
5) 200 TB'ın üstü ya da hat dar ise: Snowball / Snowmobile
```

**Bedeli:** Rehost hızlıdır ama bulut avantajını (elastikiyet, yönetilen servis) getirmez;
kazanç sonraki adımda, replatform'da gelir.

### Reçete D — "Maliyet kontrolden çıktı, önce ne yapmalı?"

```
1) Cost Explorer        → nereye gidiyor? (geçmiş)
2) Compute Optimizer    → hangi makine büyük geldi? (right-sizing)
3) Budgets + alarm      → bir daha sürpriz olmasın (gelecek)
4) Savings Plans        → sabit kalan yük için taahhüt
5) S3 Lifecycle         → eski veriyi ucuz sınıfa indir
```

**Bedeli:** Savings Plans **1 veya 3 yıllık taahhüttür**; kullanmasan da ödersin.
Önce right-sizing yap, **sonra** taahhüde gir — yanlış boyutu üç yıl kilitlemek en pahalı hatadır.

### Reçete E — "Yeni hesap açtık, ilk gün ne yapılır?"

```
1) Kök kullanıcıda MFA aç, access key varsa sil
2) Günlük iş için IAM kullanıcısı / IAM Identity Center kimliği aç
3) Budgets ile bütçe alarmı kur   (ilk gün, istisnasız)
4) CloudTrail'i aç                (kim ne yaptı kaydı)
5) Faturalandırma uyarılarını e-postaya bağla
```

**Bedeli:** Yok — hepsi ücretsizdir. Atlanırsa bedeli ilk sürpriz fatura ödetir.

---

## 3. Hesaplama seçimi

| Seçenek | İlk refleks | Ne yapar | Ne zaman seçilir | Nasıl düşünülür |
|---|---|---|---|---|
| **EC2** | ✅ varsayılan | Sanal sunucu; işletim sistemi sende | `full control`, `custom OS`, `licensed software` | En çok kontrol, en çok sorumluluk. Yama, ölçek, yedek **senin** |
| **Lambda** | | Kod parçasını olay tetikleyince çalıştırır | `serverless`, `event-driven`, `no servers to manage` | Tek çalışma **15 dk** sınırı. Sürekli çalışan yükte EC2'den pahalıdır |
| **ECS / EKS** | | Konteyner yönetir | `containers`, `Docker`, `Kubernetes` | Kubernetes kelimesi geçiyorsa **EKS**, geçmiyorsa ECS |
| **Fargate** | | ECS/EKS'i sunucusuz çalıştırır | `containers` + `without managing servers` | Konteyner + "sunucu yönetmeyeyim" ikilisi görünce |
| **Elastic Beanstalk** | | Kodu yükle, altyapıyı o kursun | `just upload my code`, `quickly deploy` | Altyapı yine senin hesabında görünür ve **ücretsizdir**; altındaki EC2'ye ödersin |
| **Batch** | | Toplu, uzun süren işleri sıraya koyar | `batch`, `thousands of jobs` | Lambda'nın 15 dakikası yetmiyorsa buraya bak |
| **Outposts** | | AWS donanımını **senin** veri merkezine koyar | `must stay on-premises`, `data residency` | Yasal/gecikme zorunluluğu yoksa gereksiz pahalıdır |
| **Lightsail** | | Basit, sabit fiyatlı hazır sunucu | `simple`, `predictable price`, `small website` | Ölçeklenme ihtiyacı doğduğunda EC2'ye taşınır |

**Yük dengeleyici:** HTTP/HTTPS ve adres yoluna göre dağıtım → **ALB**. Aşırı yüksek hız,
TCP/UDP, sabit IP → **NLB**.

---

## 4. Depolama seçimi

Önce **tür**, sonra **sınıf**:

| Tür | Servis | Ayırt edici kelime |
|---|---|---|
| Nesne (dosyayı at, adresi olsun) | **S3** | `objects`, `static website`, `unlimited` |
| Blok (sunucuya takılan disk) | **EBS** | `attach to an EC2 instance`, `boot volume` |
| Dosya (aynı anda çok sunucu bağlanır) | **EFS** (Linux) · **FSx** (Windows) | `shared file system`, `multiple instances` |
| Geçici (makine durunca silinir) | **Instance store** | `temporary`, `ephemeral`, `cache` |
| Arşiv (yılda bir bakılır) | **S3 Glacier** ailesi | `archive`, `long-term retention`, `compliance` |

### S3 sınıfları — erişim sıklığına göre

| Sınıf | İlk refleks | Ne zaman | Tuzağı |
|---|---|---|---|
| **Standard** | ✅ varsayılan | Sık okunan veri | En pahalı depolama, en ucuz okuma |
| **Intelligent-Tiering** | | **Erişim deseni bilinmiyorsa** | Nesne başına küçük izleme ücreti; belirsizlikte yine de en doğru cevap |
| **Standard-IA** | | Ayda bir okunan, ama hemen lazım olan | En az **30 gün** saklama varsayılır; erken silersen yine 30 gün ödersin |
| **One Zone-IA** | | Yeniden üretilebilir veri (küçük resimler) | Tek [AZ](03-kavram-sozlugu.md#az)'da durur; o AZ giderse veri gider |
| **Glacier Instant Retrieval** | | Çeyrekte bir okunan arşiv, **anında** lazım | 90 gün asgari saklama |
| **Glacier Flexible Retrieval** | | Yılda bir-iki, dakikalar/saatler beklenebilir | 90 gün asgari saklama |
| **Glacier Deep Archive** | | 7–10 yıl saklanacak yasal kayıt | **12 saate kadar** bekleme, 180 gün asgari saklama |

**Nasıl düşünülür:** Aylık okuma sayısı ile depolama ücreti çarpışır. Ayda bir okunuyorsa IA
kazanır; haftada bir okunuyorsa Standard kazanır — IA'nın okuma ücreti farkı yer.
Bilmiyorsan **Intelligent-Tiering**, çünkü yanlış tahminin bedelini o üstlenir.

---

## 5. Veritabanı seçimi

| İhtiyaç | Servis | Ayırt edici kelime |
|---|---|---|
| İlişkisel, klasik uygulama ([OLTP](03-kavram-sozlugu.md#oltp)) | **RDS** | `MySQL`, `PostgreSQL`, `relational` |
| İlişkisel + yüksek performans, AWS'e özgü | **Aurora** | `5x MySQL performance`, `cloud-native` |
| Anahtar-değer, milisaniyenin altı, sınırsız ölçek | **DynamoDB** | `NoSQL`, `key-value`, `single-digit millisecond`, `serverless` |
| Analitik ambar ([OLAP](03-kavram-sozlugu.md#olap)) | **Redshift** | `data warehouse`, `petabyte`, `complex queries` |
| Önbellek | **ElastiCache** | `cache`, `Redis`, `Memcached`, `reduce database load` |
| Grafik (ilişki ağı) | **Neptune** | `graph`, `social network`, `fraud detection` |
| MongoDB uyumlu belge | **DocumentDB** | `MongoDB-compatible` |
| Cassandra uyumlu | **Keyspaces** | `Cassandra-compatible` |
| Zaman serisi | **Timestream** | `IoT sensor data over time` |
| S3'teki dosyaya SQL sorusu | **Athena** | `query data in S3`, `no infrastructure`, `pay per query` |

**Multi-AZ mi, read replica mı?** İkisi farklı problemi çözer ve sınavın klasik tuzağıdır:

| | **Multi-AZ** | **Read replica** |
|---|---|---|
| Amaç | Dayanıklılık (yük devretme) | Performans (okuma yükünü dağıtma) |
| Kopyalama | Eşzamanlı (synchronous) | Eşzamansız (asynchronous) |
| Okuma yapılır mı | **Hayır** | Evet |
| Bölgeler arası olur mu | Aynı Region | **Başka Region'a da** kurulabilir |

---

## 6. Ağ ve bağlantı seçimi

| Senaryo | Cevap | Nasıl düşünülür |
|---|---|---|
| Ofisi buluta bağla, **hızlı ve ucuz** kurulsun | **Site-to-Site VPN** | İnternet üzerinden şifreli tünel; dakikalar içinde kurulur, hız internete bağlıdır |
| Ofisi buluta bağla, **trafik internete çıkmasın**, hız garantili olsun | **Direct Connect** | Fiziksel özel hat. Kurulumu **haftalar** sürer, pahalıdır. `dedicated`, `consistent bandwidth`, `not over the public internet` kelimeleri bunu işaret eder |
| İki VPC'yi bağla | **VPC Peering** | İkiden fazlada bağlantı sayısı patlar (n×(n−1)/2) |
| Çok sayıda VPC ve şubeyi bağla | **Transit Gateway** | Yıldız topolojisi; onlarca ağda tek merkez |
| Özel alt ağdaki sunucu internete **çıksın**, dışarıdan gelinemesin | **NAT Gateway** | Saatlik **ve** veri başına ücretlidir — sessiz fatura kaynağıdır |
| VPC'den S3'e giden trafik internete çıkmasın | **VPC Endpoint** | Ücretsiz gateway endpoint S3 ve DynamoDB için vardır |
| Kullanıcıya yakın statik/dinamik içerik | **CloudFront** | Uçta önbellek; `latency`, `global users`, `cache` |
| Sabit IP + anlık yük devretme, önbellek yok | **Global Accelerator** | `TCP/UDP`, `non-HTTP`, `static IP` |
| Alan adı yönetimi ve yönlendirme | **Route 53** | Kayıt (register), DNS ve sağlık kontrolü üçlüsü |

---

## 7. Fiyat modeli seçimi

| Model | İndirim | Taahhüt | Ne zaman | Riski |
|---|---|---|---|---|
| **On-Demand** | — | Yok | Öngörülemeyen, kısa süreli, yeni yük | En pahalı saatlik ücret |
| **Savings Plans** | %72'ye kadar | 1 veya 3 yıl, **$/saat** taahhüdü | Sabit kalan taban yük; esneklik istiyorsan | Kullanmasan da ödersin |
| **Reserved Instances** | %72'ye kadar | 1 veya 3 yıl, **belirli makine tipi** | Makine tipi kesin belliyse | Tip değişirse indirim kaçar |
| **Spot** | %90'a kadar | Yok | Kesintiye dayanan iş: toplu işleme, test, render | **İki dakika** ihbarla elinden alınır |
| **Dedicated Host** | — | Opsiyonel | Fiziksel sunucu şartı olan lisanslar ([BYOL](03-kavram-sozlugu.md#byol)) | En pahalısı |

**Nasıl düşünülür:** Taahhüt ettiğin miktar, yükün **hiç düşmediği taban** kadar olmalı.
Tepe noktasına göre taahhüt verirsen boşta kalan kapasiteye üç yıl ödersin.

⚠️ **Spot yasak listesi:** veritabanı, durum tutan (stateful) uygulama, tek kopyalı üretim servisi.
Soruda `cannot be interrupted` geçiyorsa Spot **yanlış cevaptır**.

**Veri transferi:** Buluta **giriş ücretsizdir**; çıkış (internete) ücretlidir. Aynı AZ içi
özel adres trafiği ücretsiz, AZ'lar arası ve Region'lar arası ücretlidir.
Sınavın klasik tuzağı: "hangi yön ücretsiz?" → **içeri**.

---

## 8. Destek planı seçimi

| Plan | Ayırt edici özellik | Ne zaman seçilir |
|---|---|---|
| **Basic** | Ücretsiz; yalnızca fatura ve hesap desteği, teknik destek yok | Deneme hesabı |
| **Developer** | E-posta, mesai saatleri, tek kişi soru sorabilir | Geliştirme/test ortamı |
| **Business** | 7/24 telefon ve sohbet, **Trusted Advisor'ın tüm kontrolleri**, üçüncü parti yazılım desteği | **Üretim ortamı olan herkes için taban** |
| **Enterprise On-Ramp** | Havuzdan [TAM](03-kavram-sozlugu.md#tam), kritik arızada **30 dakika** yanıt | Üretim var ama tam Enterprise pahalı geliyorsa |
| **Enterprise** | **Atanmış** TAM, kritik arızada **15 dakika** yanıt | İşin durursa para kaybeden kurum |

⚠️ Sınavın en sık sorusu: **Trusted Advisor'ın bütün kontrolleri Business'tan itibaren açılır.**
Basic ve Developer yalnızca temel kontrolleri görür.

---

## 9. Güvenlik servisi seçimi

| Senaryo | Cevap | Neden diğeri değil |
|---|---|---|
| Hesapta şüpheli davranış var mı? | **GuardDuty** | Tehdit **tespiti**; log okur, açık taramaz |
| Sunucumda/konteynerimde bilinen açık var mı? | **Inspector** | Zafiyet **taraması** yapar |
| S3'te farkında olmadan kişisel veri mi duruyor? | **Macie** | Yalnız S3, yalnız hassas veri keşfi |
| Olayın kökünü araştırmak istiyorum | **Detective** | Bulguyu **araştırır**, kendisi bulmaz |
| Bütün güvenlik bulgularını tek ekranda topla | **Security Hub** | Toplayıcıdır, kendisi tespit etmez |
| Web isteklerinden zararlı olanı ele | **WAF** | İçeriğe bakar (SQL enjeksiyonu, XSS) |
| Hacimsel saldırıya karşı koruma | **Shield** | Standard sürümü herkese **ücretsiz** açıktır |
| Belirli bir IP adresini engelle | **NACL** ya da WAF | [Güvenlik grubunda](03-kavram-sozlugu.md#security-group) **reddetme kuralı yazılamaz** |
| Anahtarı AWS bile göremesin | **CloudHSM** | KMS paylaşımlı donanım kullanır |
| Kim ne yaptı? | **CloudTrail** | CloudWatch **metrik**, Config **yapılandırma** izler |
| Kaynak ayarı kurala uygun mu? | **Config** | Uyum durumunu zaman içinde tutar |
| AWS'in denetim belgesini indir | **Artifact** | Kendi belgeni değil, **AWS'in** belgesini verir |

---

## 10. ⚠️ Tehlikeli seçimler — geri dönüşü pahalı olanlar

| Seçim | Neden tehlikeli | Doğru refleks |
|---|---|---|
| Kök kullanıcı için access key üretmek | Sızarsa hesabın tamamı gider; kısıtlanamaz | Üretme; varsa **sil**. Günlük iş IAM kimliğiyle yapılır |
| Kök kullanıcıyla günlük çalışmak | Yaptığı her iş sınırsızdır | MFA aç, kilitle, yılda birkaç kez kullan |
| S3 kovasını herkese açmak | Veri sızıntılarının bir numaralı sebebi | Block Public Access açık kalsın; paylaşım için imzalı adres |
| Right-sizing yapmadan 3 yıllık taahhüt | Yanlış boyutu üç yıl ödersin | Önce Compute Optimizer, sonra Savings Plans |
| Üretim veritabanını Spot'a koymak | İki dakika ihbarla makine elinden alınır | Spot yalnızca kesintiye dayanan iş için |
| Instance store'daki veriye güvenmek | Makine **durdurulunca** içindekiler silinir | Kalıcı veri EBS ya da S3'te |
| Terminate ile stop'u karıştırmak | Terminate **geri alınamaz** | Kök EBS diskini korumak istiyorsan stop |
| NAT Gateway'i unutmak | Saatlik + veri ücreti; kullanılmasa da işler | Gerekmiyorsa sil; VPC Endpoint alternatifine bak |
| Yanlış Region'da kaynak açmak | Fatura görünene kadar fark edilmez, yasal sorun doğurabilir | Konsolun sağ üstündeki Region'ı **her oturumda** kontrol et |
| Bütçe alarmı kurmadan lab yapmak | Free Tier aşımı sessizdir | İlk gün Budgets — [Reçete E](#reçete-e--yeni-hesap-açtık-ilk-gün-ne-yapılır) |

---

## 11. Anahtar kelime → servis tablosu

Sınavda soruyu çözen tek kelimeler. Bu tabloyu sınav öncesi son 24 saatte tekrar oku.

| Soruda geçen | Cevap yönü |
|---|---|
| `least operational overhead`, `fully managed` | Yönetilen/sunucusuz servis (Lambda, RDS, Fargate) |
| `automatically scale in and out` | Auto Scaling / elastikiyet |
| `no application code changes` | Rehost ya da Replatform, **Refactor değil** |
| `pay only for what you use`, `per request` | Lambda, S3, DynamoDB on-demand |
| `long-term archive`, `7 years`, `compliance` | S3 Glacier Deep Archive |
| `access pattern is unknown` | S3 Intelligent-Tiering |
| `single-digit millisecond` | DynamoDB |
| `data warehouse`, `complex analytical queries` | Redshift |
| `query data directly in S3` | Athena |
| `not over the public internet`, `dedicated connection` | Direct Connect (ya da VPC Endpoint) |
| `must remain on-premises`, `data residency` | Outposts |
| `global users`, `reduce latency`, `cache at the edge` | CloudFront |
| `decouple`, `buffer`, `process later` | SQS |
| `notify multiple subscribers at once` | SNS |
| `who made this API call?` | CloudTrail |
| `is this resource configured correctly?` | Config |
| `download AWS audit reports` | Artifact |
| `15-minute response`, `designated TAM` | Enterprise destek planı |
| `all Trusted Advisor checks` | Business planı ve üstü |
| `interruption is acceptable` | Spot |
| `steady-state usage for 1 or 3 years` | Savings Plans / Reserved Instances |

---

⬅️ [Başlangıç klasörüne dön](00-sinav-kunyesi.md) · 📖 [Kavram sözlüğü](03-kavram-sozlugu.md) ·
🗺 [Servis haritası](../99-final/servis-haritasi.md)
