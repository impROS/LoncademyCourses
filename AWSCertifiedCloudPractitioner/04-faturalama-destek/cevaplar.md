# 4. alan · Faturalama, Fiyatlandırma ve Destek — Kendini kontrol cevapları

> Bu dosya [4.1](4.1-fiyatlandirma.md) – [4.3](4.3-destek-planlari.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [4.1](#41-fiyatlandırma-modelleri) · [4.2](#42-bütçe-ve-maliyet-yönetimi) ·
[4.3](#43-destek-planları)

---

## 4.1 Fiyatlandırma modelleri

📄 Sorular: [`4.1-fiyatlandirma.md`](4.1-fiyatlandirma.md)

### Soru 1 — Beş EC2 fiyat modelini indirim ve risk sırasına göre yaz

**Kısa cevap:**

| Model | İndirim | Taahhüt | Kesinti riski |
|---|---|---|---|
| **Spot** | %90'a kadar | Yok | **Var** — 2 dakika ihbar |
| **Reserved Instances** | %72'ye kadar | 1 veya 3 yıl, belirli tip | Yok |
| **Savings Plans** | %72'ye kadar | 1 veya 3 yıl, saatlik dolar | Yok |
| **On-Demand** | Yok (referans) | Yok | Yok |
| **Dedicated Host** | En pahalısı | İsteğe bağlı | Yok |

**Ayrıntı:** Sıralamanın mantığı tek cümlede: **ne kadar çok söz verirsen o kadar ucuz, ne kadar
çok riske girersen o kadar ucuz.** Spot'un indirimi taahhütten değil, **kesintiyi kabul etmekten**
gelir.

> 📌 **Sık yapılan hata:** Dedicated Host'u "rezervasyon" sanıp indirimli saymak. Fiziksel sunucu
> ayırmak en pahalı seçenektir; sebebi indirim değil **lisans ve mevzuat** zorunluluğudur.

🔗 Konu: [4.1 §2](4.1-fiyatlandirma.md) ·
⚙️ [Seçim rehberi §7](../00-baslangic/04-secim-rehberi.md#7-fiyat-modeli-seçimi)

---

### Soru 2 — Reserved Instances ile Savings Plans arasındaki temel fark

**Kısa cevap:** **Esneklik.** Reserved Instances belirli bir **instance tipine/Region'a** bağlıdır;
Savings Plans **saatlik dolar taahhüdüdür** — parayı taahhüt edersin, neye harcadığın esnektir.

**Ayrıntı:**

| | Reserved Instances | Savings Plans |
|---|---|---|
| Taahhüt | Belirli tip/Region | **Saatlik $ tutarı** |
| Esneklik | Düşük (Convertible biraz daha esnek) | **Yüksek** |
| Kapsam | EC2 (ve bazı servisler) | Compute planı **Fargate ve Lambda**'yı da kapsar |
| Kapasite rezervasyonu | Bölgesel RI ile mümkün | Yok |

Sınav ipucu: soruda `flexibility` geçiyorsa **Savings Plans**; `specific instance type`
ya da `capacity reservation` geçiyorsa **Reserved Instance**.

> 📌 **Sık yapılan hata:** Savings Plans'ın kapasite garantisi verdiğini sanmak. Vermez;
> kapasite garantisi için **Capacity Reservation** ayrıca alınır ve o indirim sağlamaz.

🔗 Konu: [4.1 §2](4.1-fiyatlandirma.md)

---

### Soru 3 — Spot hangi iş yükleri için uygundur, hangileri için değildir

**Kısa cevap:** **Uygun:** toplu işleme, veri analizi, sürekli tümleştirme işleri, görüntü
işleme, durum tutmayan işçiler. **Uygun değil:** veritabanı, durum tutan uygulama, kesinti kabul
etmeyen üretim servisi.

**Ayrıntı:** Ayrım tek bir soruya iner: **makine iki dakika sonra elinden alınırsa iş kaybolur mu?**
Kaybolmuyorsa Spot uygundur.

Spot, AWS'in **atıl kapasitesidir**; AWS kapasiteye ihtiyaç duyduğunda iki dakika uyarı verip
geri alır. Bu yüzden yeniden başlatılabilen, parçalara bölünmüş işler için biçilmiş kaftandır.

> 📌 **Sık yapılan hata:** Soruda `cannot be interrupted` yazarken indirim oranına bakıp Spot
> seçmek. O ifade Spot'u doğrudan eler.

🔗 Konu: [4.1 §2](4.1-fiyatlandirma.md)

---

### Soru 4 — Veri transferinde hangi yön ücretsizdir

**Kısa cevap:** **İçeri giriş** (internetten AWS'e) genelde ücretsizdir; **dışarı çıkış**
(AWS'ten internete) ücretlidir.

**Ayrıntı:**

| Yön | Ücret |
|---|---|
| İnternetten AWS'e (giriş) | Genelde **ücretsiz** |
| AWS'ten internete (çıkış) | **Ücretli** |
| Aynı AZ içinde özel adresle | Genelde ücretsiz |
| AZ'ler arası | Ücretli |
| Region'lar arası | Ücretli |

Bunun mimari sonucu vardır: CloudFront kullanmak yalnızca gecikmeyi değil **çıkış maliyetini de**
düşürür, çünkü içerik uçtan servis edilir.

> 📌 **Sık yapılan hata:** "Veri transferi ücretlidir" diye tek cümlede özetlemek. Sınav yönü
> doğrudan sorar ve giriş/çıkış ayrımını bilmeyen yanlış cevabı seçer.

🔗 Konu: [4.1 §1](4.1-fiyatlandirma.md)

---

### Soru 5 — Durdurulmuş bir EC2 için neye ödeme yaparsın

**Kısa cevap:** **Bağlı EBS diskine** (ve varsa boşta duran Elastic IP adresine). İşlem gücü
ücreti durur.

**Ayrıntı:**

| Kalem | Instance durdurulduğunda |
|---|---|
| İşlem (compute) saati | **Durur** |
| Bağlı EBS diski | **Devam eder** |
| EBS anlık görüntüleri | Devam eder |
| Boşta duran Elastic IP | **Ücretlenir** |

Bu yüzden lab temizliğinde "durdurmak" yetmez: instance **sonlandırılır** ve artık gerekmeyen
diskler ile Elastic IP adresleri de silinir.

> 📌 **Sık yapılan hata:** "Kapattım, sayaç durdu" varsaymak. Kapalı bir makinenin diski
> aylarca sessizce fatura üretebilir.

🔗 Konu: [4.1 §3](4.1-fiyatlandirma.md) · [3.3 §1](../03-teknoloji-servisler/3.3-compute.md)

---

## 4.2 Bütçe ve maliyet yönetimi

📄 Sorular: [`4.2-maliyet-yonetimi.md`](4.2-maliyet-yonetimi.md)

### Soru 1 — Pricing Calculator, Cost Explorer ve Budgets'ı zaman ekseninde ayır

**Kısa cevap:** **Calculator = önce** (henüz hiçbir şey yokken tahmin) · **Cost Explorer = sonra**
(gerçekleşeni analiz) · **Budgets = uyarı** (eşik aşılınca haber ver).

**Ayrıntı:**

| | Pricing Calculator | Cost Explorer | Budgets |
|---|---|---|---|
| Zaman | Gelecek | Geçmiş (+ tahmin) | Eşik anı |
| Tipik soru | `estimate cost before deploying` | `which service cost the most last month` | `alert me when spend exceeds 500 USD` |
| Ücret | Ücretsiz | Temel kullanımı ücretsiz | İlk iki bütçe ücretsiz |

Cost Explorer ayrıca 12 aya kadar ileri tahmin çizer ve right-sizing / Savings Plans önerisi verir.
Budgets ise yalnız uyarmakla kalmaz: **Budgets Actions** ile eşik aşılınca otomatik aksiyon
(politika uygulama, instance durdurma) alabilir.

> 📌 **Sık yapılan hata:** Cost Explorer'ın tahmin çizgisine bakıp onu planlama aracı sanmak.
> Tahminin girdisi **geçmiş kullanımdır**; hiç kurulmamış bir mimariyi hesaplayamaz.

🔗 Konu: [4.2 §1](4.2-maliyet-yonetimi.md)

---

### Soru 2 — En ayrıntılı fatura verisini hangi araç verir, nereye yazar

**Kısa cevap:** **Cost and Usage Report (CUR)** — saatlik ve kaynak seviyesinde veri üretir,
**S3'e** yazar.

**Ayrıntı:** Oradan Athena ile sorgulanır, QuickSight ile görselleştirilir. Ayrımı kaçırmamak için:

| İhtiyaç | Araç |
|---|---|
| En ayrıntılı, saatlik, kaynak seviyesinde veri | **Cost and Usage Report** |
| Görsel analiz, gruplama, öneri | Cost Explorer |
| Aylık faturanın servis kırılımı | Billing konsolu |
| Olağandışı harcama sıçraması uyarısı | **Cost Anomaly Detection** |

> 📌 **Sık yapılan hata:** `most granular` ifadesine Cost Explorer demek. Cost Explorer analiz
> arayüzüdür; ham veri CUR'dadır.

🔗 Konu: [4.2 §2](4.2-maliyet-yonetimi.md)

---

### Soru 3 — Cost allocation tag'ler neden sadece etiket yapıştırmakla çalışmaz

**Kısa cevap:** Çünkü etiketin faturalama raporlarında görünmesi için **Billing konsolunda
etkinleştirilmesi** gerekir — ve etkinleştirme **geriye dönük çalışmaz**.

**Ayrıntı:** İki adım vardır ve ikincisi sık atlanır:

1. Kaynağa etiket yapıştır (`Ortam = Production`).
2. Billing konsolunda o etiket anahtarını **maliyet dağıtım etiketi olarak etkinleştir**.

İkinci adım yapılmadan geçen ay için rapor üretilemez; etiket sonradan etkinleştirilse bile
geçmiş dönem geriye dönük etiketlenmez. Pratik sonuç: **etiketlemeye erken başla.**

> 📌 **Sık yapılan hata:** Etiketleri koyup ay sonunda rapor beklemek. Rapor boş gelir ve o ay
> geri kazanılamaz.

🔗 Konu: [4.2 §3](4.2-maliyet-yonetimi.md)

---

### Soru 4 — Consolidated billing'in iki finansal faydası

**Kısa cevap:** **(1)** Hacim indirimleri birleşir — tüm hesapların kullanımı toplanır ve kademeli
indirimlere daha hızlı ulaşılır. **(2)** Savings Plans ve Reserved Instances indirimleri
**hesaplar arasında paylaşılır**.

**Ayrıntı:** Buna ek olarak tek fatura gelir, hesap bazında harcama yine ayrı ayrı görülebilir ve
**birleşik faturalama ücretsizdir**.

| Fayda | Neden önemli |
|---|---|
| Hacim indirimlerinin birleşmesi | 5 hesabın 100 GB'ı ayrı ayrı değil, 500 GB olarak fiyatlanır |
| Rezervasyon paylaşımı | Bir hesabın kullanmadığı rezervasyon başka hesaba yarar |
| Tek fatura | Muhasebe yükü azalır |

> 📌 **Sık yapılan hata:** "Tek fatura gelmesi" faydasını finansal fayda sanmak. Sınav
> **indirim** tarafını sorar: hacim indirimi ve rezervasyon paylaşımı.

🔗 Konu: [4.2 §4](4.2-maliyet-yonetimi.md)

---

### Soru 5 — Harcama sıçramasını otomatik yakalamak için hangi servis

**Kısa cevap:** **AWS Cost Anomaly Detection** — makine öğrenmesiyle olağandışı harcamayı tespit
edip uyarır.

**Ayrıntı:** Budgets'tan farkı, **eşiği senin belirlemene gerek olmamasıdır**:

| | Budgets | Cost Anomaly Detection |
|---|---|---|
| Tetikleyici | Senin yazdığın eşik | Öğrenilmiş normalden **sapma** |
| Yakaladığı | "500 doları aştım" | "Bu servis normalde böyle harcamazdı" |
| Ne zaman | Sınır bilinen durumlar | Sürpriz sıçramalar |

İkisi birlikte kullanılır: bütçe tavanı için Budgets, beklenmedik davranış için Anomaly Detection.

> 📌 **Sık yapılan hata:** Her uyarı senaryosuna Budgets demek. Soruda `unexpected`,
> `unusual spike`, `automatically detect` geçiyorsa cevap Cost Anomaly Detection'dır.

🔗 Konu: [4.2 §2](4.2-maliyet-yonetimi.md)

---

## 4.3 Destek planları

📄 Sorular: [`4.3-destek-planlari.md`](4.3-destek-planlari.md)

### Soru 1 — Beş destek planını fiyat sırasına diz, ayırt edici özellikleriyle

**Kısa cevap:**

| Plan | Ayırt edici özellik |
|---|---|
| **Basic** | Ücretsiz; teknik destek **yok**, yalnız hesap ve fatura desteği |
| **Developer** | E-posta, mesai saatleri, **tek kişi** vaka açabilir |
| **Business** | 7/24 telefon ve sohbet, **Trusted Advisor'ın tüm kontrolleri**, üçüncü parti yazılım desteği |
| **Enterprise On-Ramp** | **Havuzdan** teknik hesap yöneticisi, kritik arızada **30 dakika** |
| **Enterprise** | **Atanmış** teknik hesap yöneticisi, kritik arızada **15 dakika** |

**Ayrıntı:** Basic ücretsiz olmasına rağmen boş değildir: hesap/fatura desteği, Trusted Advisor'ın
temel kontrolleri, AWS Health Dashboard ve dokümantasyon erişimi içerir.

> 📌 **Sık yapılan hata:** Developer'ı "küçük üretim ortamı için yeterli" saymak. Developer'da
> 7/24 destek ve üretim arızası yanıt süresi **yoktur**; üretimin tabanı Business'tır.

🔗 Konu: [4.3 §1](4.3-destek-planlari.md)

---

### Soru 2 — TAM hangi planlarda var, aralarındaki fark

**Kısa cevap:** **Enterprise On-Ramp** ve **Enterprise** planlarında. Farkı: On-Ramp'te
**havuzdan** gelir, Enterprise'da **sana atanmıştır**.

**Ayrıntı:** [Teknik hesap yöneticisi](../00-baslangic/03-kavram-sozlugu.md#tam), kurumun
mimarisini tanıyan ve proaktif rehberlik veren AWS çalışanıdır.

| | Enterprise On-Ramp | Enterprise |
|---|---|---|
| Teknik hesap yöneticisi | Havuzdan | **Atanmış** |
| Kritik arıza yanıtı | 30 dakika | **15 dakika** |

Sınav ifadesi neredeyse hep aynıdır: `designated technical account manager` → **Enterprise**.

> 📌 **Sık yapılan hata:** Business planında teknik hesap yöneticisi olduğunu sanmak. Business'ta
> yoktur.

🔗 Konu: [4.3 §1](4.3-destek-planlari.md)

---

### Soru 3 — Trusted Advisor'ın tüm kontrolleri hangi plandan itibaren açılır

**Kısa cevap:** **Business** planından itibaren (Business, Enterprise On-Ramp, Enterprise).

**Ayrıntı:** Basic ve Developer yalnızca **temel** kontrolleri görür — genelde temel güvenlik
kontrolleri ve servis limitleri. Beş kategorinin tamamı Business'tan itibaren açılır:

| Kategori | Örnek kontrol |
|---|---|
| Cost Optimization | Düşük kullanımlı EC2, boşta duran yük dengeleyici |
| Performance | Aşırı kullanılan instance |
| Security | Kök kullanıcıda MFA yok, herkese açık S3 kovası |
| Fault Tolerance | Yedeklenmemiş disk, tek AZ'de çalışan ölçek grubu |
| Service Limits | Limitin %80'ini aşan kullanım |

> 📌 **Sık yapılan hata:** "Trusted Advisor ücretsizdir" diye tamamının ücretsiz olduğunu sanmak.
> Servis herkese açıktır ama **kapsamı plana bağlıdır**.

🔗 Konu: [4.3 §2](4.3-destek-planlari.md)

---

### Soru 4 — Business ve Enterprise için en kritik yanıt süreleri

**Kısa cevap:** **Business:** üretim sistemi çöktüğünde **1 saat**. **Enterprise:** iş açısından
kritik sistem çöktüğünde **15 dakika**.

**Ayrıntı:**

| Önem derecesi | Developer | Business | Enterprise On-Ramp | Enterprise |
|---|---|---|---|---|
| Genel soru | < 24 saat | < 24 saat | < 24 saat | < 24 saat |
| Sistem etkilenmiş | < 12 saat | < 12 saat | < 12 saat | < 12 saat |
| Üretim sistemi etkilenmiş | — | **< 4 saat** | < 4 saat | < 4 saat |
| Üretim sistemi çökmüş | — | **< 1 saat** | < 1 saat | < 1 saat |
| İş açısından kritik sistem çökmüş | — | — | **< 30 dakika** | **< 15 dakika** |

Ezberlemenin kolay yolu üç sayıdır: **Business 1 saat · On-Ramp 30 dakika · Enterprise 15 dakika.**

> 📌 **Sık yapılan hata:** "Üretim çökmüş" ile "iş açısından kritik sistem çökmüş" derecelerini
> aynı saymak. İkincisi yalnızca Enterprise planlarında vardır.

🔗 Konu: [4.3 §1](4.3-destek-planlari.md)

---

### Soru 5 — AWS Professional Services ile AWS Partner Network farkı

**Kısa cevap:** **Professional Services** AWS'in **kendi** danışmanlık ekibidir;
**Partner Network** AWS'in **onayladığı üçüncü parti** firmalar ve yazılım üreticileridir.

**Ayrıntı:**

| | Professional Services | Partner Network |
|---|---|---|
| Kim | AWS çalışanları | Bağımsız firmalar |
| İki türü | — | **Consulting Partners** (hizmet), **Technology Partners** (yazılım) |
| Ne zaman | AWS'in doğrudan katılımı isteniyorsa | Yerel/sektörel uzmanlık, uzun soluklu iş |

Aynı ailedeki diğerleri: **AWS Managed Services** ortamı senin adına işletir, **AWS IQ** küçük
işler için sertifikalı uzmanla eşleştirir, **AWS Activate** girişimlere kredi ve destek verir.

> 📌 **Sık yapılan hata:** İkisini destek planlarıyla karıştırmak. Destek planı **arıza** içindir;
> bu ikisi **proje ve danışmanlık** içindir.

🔗 Konu: [4.3 §3](4.3-destek-planlari.md)

---

⬅️ [Bölüme dön](4.1-fiyatlandirma.md) · 📖 [Kavram sözlüğü](../00-baslangic/03-kavram-sozlugu.md) ·
⚙️ [Seçim rehberi](../00-baslangic/04-secim-rehberi.md)
