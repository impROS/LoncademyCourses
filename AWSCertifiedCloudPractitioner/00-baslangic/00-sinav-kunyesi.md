# 0.0 — Sınav Künyesi ve Sınav Alma Stratejisi

> Bu bir konu dosyası değil, **oyunun kurallarını** öğrendiğin dosya. 20 dakikada oku, sonra kuruluma geç.
> Sınav mekaniğini bilmek, tek başına 3–5 soru kazandırır.

---

## 1. Rakamlar

| | |
|---|---|
| **Sınav kodu** | CLF-C02 |
| **Soru sayısı** | 65 |
| **Puanlanan soru** | 50 — kalan 15'i AWS'in denediği "pilot" sorulardır, puanına etki etmez |
| **Süre** | 90 dakika |
| **Puan aralığı** | 100–1000 (ölçeklenmiş) |
| **Geçme notu** | **700** |
| **Ücret** | 100 USD (+ ülkeye göre vergi) |
| **Geçerlilik** | 3 yıl |
| **Dil** | İngilizce, Almanca, Fransızca, İspanyolca, Japonca, Korece, Portekizce, Çince — **Türkçe yok** |
| **Ön koşul** | Yok |
| **Yeniden girme** | Kaldıysan **14 gün** bekle, ücreti tekrar öde |

### Bilmen gereken 4 mekanik detay

**1. Ölçeklenmiş puan (scaled score).** 700, "yüzde 70 doğru" demek değildir; ham doğru sayın istatistiksel olarak ölçeklenir. Pratikte hedefin **%72–75 ham doğru**. Deneme sınavlarında %80 tutturuyorsan güvendesin.

**2. Telafili (compensatory) model.** Her alandan ayrı ayrı geçmek zorunda değilsin. 4. alandan sıfır alıp diğerlerinden yüksek alarak geçebilirsin. Ama alan ağırlıkları çalışma zamanını nasıl böleceğini söyler.

**3. Yanlış cezası yok.** Boş bırakılan soru = yanlış. **Hiçbir soruyu boş bırakma.** Bilmiyorsan bile ele.

**4. 15 pilot soru gizlidir.** Sınavda "bu ne saçma soru" dediğin soru muhtemelen puanlanmıyordur. Takılma, geç.

---

## 2. Soru tipleri

Sınavda sadece iki tip soru var:

**a) Tek doğru (multiple choice)** — 4 şık, 1 doğru.
> *A company wants to reduce the time it takes to provision new servers. Which benefit of the AWS Cloud does this describe?*
> A) Agility  B) Elasticity  C) Durability  D) Fault tolerance

**b) Çoklu doğru (multiple response)** — 5–6 şık, 2 (bazen 3) doğru. **Soruda "(Choose TWO.)" açıkça yazar.**
> *Which tasks are the customer's responsibility under the shared responsibility model? (Choose TWO.)*

Kısmi puan yoktur: 2 doğrudan 1'ini bulman 0 puan demektir.

**Sınavda olmayan şeyler:** kod yazma, konsol simülasyonu, sürükle-bırak, açık uçlu cevap, hesaplama sorusu (Pricing Calculator ezberi istenmez).

---

## 3. AWS soruları nasıl okunur — anahtar kelime → servis refleksi

Cloud Practitioner soruları neredeyse her zaman bir **ihtiyaç cümlesi** kurar ve o ihtiyaca karşılık gelen servisi ister. Doğru refleks, senaryoyu anlamak değil, **tetikleyici kelimeyi yakalamaktır.**

| Soruda geçen kelime | Aklına gelmesi gereken |
|---|---|
| "who made this API call" / "audit" / "kim yaptı" | **CloudTrail** |
| "metrics", "alarm", "logs", "monitor" | **CloudWatch** |
| "is my resource configured correctly / compliant" | **AWS Config** |
| "compliance reports", "SOC / ISO / PCI belgesi" | **AWS Artifact** |
| "restrict what member accounts can do" | **Organizations + SCP** |
| "sensitive data in S3", "PII discovery" | **Macie** |
| "malicious activity / threat detection" | **GuardDuty** |
| "vulnerability / CVE scan on EC2" | **Inspector** |
| "SQL injection / block web attacks" | **WAF** |
| "DDoS" | **Shield** |
| "rotate database password automatically" | **Secrets Manager** |
| "SSL/TLS certificate" | **ACM** |
| "lowest cost, interruption tolerable" | **Spot Instances** |
| "steady usage, 1–3 year commitment" | **Savings Plans / Reserved Instances** |
| "estimate cost before building" | **Pricing Calculator** |
| "analyze past spend / forecast" | **Cost Explorer** |
| "alert when spend exceeds X" | **AWS Budgets** |
| "recommendations on cost, security, limits" | **Trusted Advisor** |
| "service outage / is AWS down" | **AWS Health Dashboard** |
| "petabytes, limited bandwidth, ship physically" | **Snowball / Snow ailesi** |
| "no servers to manage", "pay per request" | **Lambda / Fargate / serverless** |
| "cache in front of database" | **ElastiCache** |
| "data warehouse / BI queries" | **Redshift** |
| "SQL directly on S3" | **Athena** |
| "cheapest archive, 12 saat beklenebilir" | **S3 Glacier Deep Archive** |
| "reduce latency for global users" | **CloudFront** |
| "dedicated private connection to AWS" | **Direct Connect** |
| "decouple application components" | **SQS** |
| "infrastructure as code" | **CloudFormation** |

Bu tabloyu ezberleme — konuları çalışırken zaten oturacak. Ama sınavdan bir gün önce buraya tekrar bak.

### Tuzak kelimeler

| İfade | Anlamı |
|---|---|
| **MOST cost-effective** | En ucuz olanı seç, "en iyi çözümü" değil |
| **LEAST operational overhead** | Yönetilen/serverless servisi seç (Lambda, Fargate, RDS, Aurora Serverless) |
| **MOST secure** | Rol, MFA, şifreleme, least privilege içeren şıkkı seç |
| **highly available** | Multi-AZ düşün |
| **fault tolerant** | Yedeklilik + otomatik kurtarma |
| **globally / worldwide users** | CloudFront, Route 53, Global Accelerator |
| **without managing servers** | Serverless (Lambda, Fargate, DynamoDB, S3, SQS) |
| **AWS-managed** vs **customer-managed** | Paylaşılan sorumluluk sorusu geliyor demektir |

---

## 4. Elemeye dayalı çözüm — üç filtre

Bir soruda takıldığında sırasıyla şu üç filtreyi uygula:

**Filtre 1 — Yanlış kategori.** Şıklardan biri tamamen alakasız bir kategoriden mi? (Depolama sorusuna Rekognition şıkkı gibi.) Sil. Genelde 4 şıktan 2'si böyle gider.

**Filtre 2 — Aşırı/eksik çözüm.** "Sadece bir dosya paylaşacağım" diyen soruda "Direct Connect kur" şıkkı fazla ağırdır. AWS her zaman **en basit ve en yönetilen** çözümü doğru sayar.

**Filtre 3 — Mutlak ifadeler.** "always", "never", "all", "only", "guarantees 100%" içeren şıklar genelde yanlıştır. AWS dokümantasyonu mutlak konuşmaz.

**Kalan iki şık arasında kaldıysan:** sorudaki BÜYÜK HARFLE yazılmış kelimeye (MOST, LEAST, BEST) dön. Cevap oradadır.

---

## 5. Sınav günü zaman yönetimi

- 65 soru / 90 dakika = **soru başına 83 saniye**. Gerçekte çoğu soru 30 saniyede biter.
- **İlk tur (~50 dk):** Bildiklerini hızlıca cevapla. Bir soruda 60 saniyeden fazla harcama — **bir şık işaretle**, "Mark for review" kutusunu tikle, geç.
- **İkinci tur (~25 dk):** İşaretlediklerine dön. Şimdi zaman baskısı yok, sakin düşün.
- **Son 10 dk:** Boş kalan var mı kontrol et. Yoksa bitir.
- **İlk içgüdünü değiştirme** — okuma hatası yaptığını fark etmediysen. İstatistiksel olarak değiştirilen cevapların çoğu doğrudan yanlışa gider.

**ESL +30 dakika:** Ana dili İngilizce olmayanlar 120 dakika alabiliyor. **Sınavı satın almadan önce** AWS Certification hesabından talep etmen gerekiyor — sonradan eklenmiyor. İngilizce okuma hızına güvenmiyorsan mutlaka al, bedava.

---

## 6. Ne zaman sınava girmeye hazırsın?

Şu üç şart birden sağlanmadan randevu alma:

1. Bu setteki **19 konu testinin hepsinde %80+** (ilk denemede değil, tekrar sonrası da olur).
2. `99-final/` klasöründeki **iki deneme sınavında da %80+**, süreyi aşmadan.
3. Servis haritasındaki servislerin **%90'ını tek cümlede tanımlayabiliyorsun.**

Not: Deneme sınavları gerçek sınavdan biraz zordur (özellikle Tutorials Dojo). %75 alıyorsan da geçersin ama marj bırak.

---

## 7. 60 saniyelik özet

- 65 soru / 90 dk / geçme 700 / ücret 100 USD / 3 yıl geçerli / Türkçe yok / +30 dk ESL hakkı var (önceden talep).
- Yanlış cezası yok → **boş bırakma.**
- Telafili puanlama → tek alandan kalınmaz.
- Anahtar kelime yakala, mutlak ifadeli şıkları ele, BÜYÜK HARFLİ kelimeye göre karar ver.
- Takılınca işaretle geç, ikinci turda dön.

---

## Sırada ne var

➡️ [`01-aws-hesabi-kurulum.md`](01-aws-hesabi-kurulum.md) — hesabını aç, güvenliğini kur, **bütçe alarmını kur** (bu adımı atlama).
