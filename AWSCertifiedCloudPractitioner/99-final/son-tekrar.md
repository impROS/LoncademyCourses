# Sınavdan 24 Saat Önce — Son Tekrar

> **Bu dosya sınavdan bir gün önce okunur.** Yeni konu öğrenme zamanı değil; **hatırlatma ve strateji** zamanı.
> Süre: 45 dakika. Sonra kapat, dinlen.

---

## 1. Hazır mısın? (dürüst kontrol listesi)

- [ ] 19 konu testinin **hepsinde %80+** aldım
- [ ] `final-sinav-1.html` ve `final-sinav-2.html`'de **%80+** aldım (süreyi aşmadan)
- [ ] [`servis-haritasi.md`](servis-haritasi.md)'ndeki ⭐ servislerin **%90'ını** tek cümlede tanımlayabiliyorum
- [ ] "En sık karıştırılan 12 ikili" tablosunu kapalı gözle söyleyebiliyorum
- [ ] Ezberlenmesi gereken sayıları biliyorum

**Üçünden biri eksikse:** randevunu ertele veya bugünü o alana ayır. Sınav ücreti 100 USD, ertelemek bedava.

---

## 2. Sınav taktiği — üç kural

**Kural 1: Anahtar kelimeyi yakala.**
Soruyu tam anlamaya çalışma; **tetikleyici kelimeyi** bul. "who deleted" → CloudTrail. "compliant" → Config.
"automatically scale in" → elasticity. "15 minutes limit" → Lambda.

**Kural 2: Mutlak ifadeli şıkları ele.**
`always`, `never`, `all`, `only`, `guarantees 100%`, `must` içeren şıklar genelde **yanlıştır.**
AWS dokümantasyonu mutlak konuşmaz.

**Kural 3: BÜYÜK HARFLİ kelimeye göre karar ver.**
- **MOST cost-effective** → en ucuz olanı seç
- **LEAST operational overhead** → en yönetilen/serverless olanı seç
- **MOST secure** → rol, MFA, şifreleme, least privilege içereni seç
- **LOWEST-cost plan that meets the requirement** → gereksinimi karşılayan **en ucuz** planı seç (daha pahalısını değil)

**Zaman:** İlk turda bir soruya **60 saniyeden fazla harcama.** Bir şık işaretle, **Mark for review** yap, geç.
İkinci turda dön. **Hiçbir soruyu boş bırakma** — yanlış cezası yok.

---

## 3. Beş dakikalık refleks turu

Aşağıdakileri **okur okumaz** cevabı gelmeli. Gelmiyorsa o konuya 15 dakika ayır.

| Soru | Cevap |
|---|---|
| EC2'de OS yamasını kim yapar? | **Müşteri** |
| RDS'te DB motorunu kim yamalar? | **AWS** |
| Kim bu API'yi çağırdı? | **CloudTrail** |
| Bu kaynak kurala uygun mu? | **Config** |
| CPU %80'i geçince uyar | **CloudWatch** |
| Explicit deny + allow çakıştı | **Deny kazanır** |
| Hiçbir politika bahsetmiyor | **Implicit deny → reddedilir** |
| EC2'den S3'e en güvenli erişim | **IAM rolü** |
| Hesabı kapatmak | **Root user** |
| Yüksek erişilebilirlik | **Birden çok AZ** |
| Region'da en az kaç AZ? | **3** |
| S3'te 11 dokuz nedir? | **Durability** |
| Erişim deseni bilinmiyor | **S3 Intelligent-Tiering** |
| En ucuz arşiv, saatler beklenebilir | **Glacier Deep Archive** |
| Yanlışlıkla silmeye karşı | **S3 Versioning** |
| Kesintiye dayanıklı, en ucuz compute | **Spot** |
| Durdurulmuş EC2'de ne ödenir? | **EBS** |
| Veri girişi ücretli mi? | **Hayır, çıkış ücretli** |
| Lambda maksimum süre | **15 dakika** |
| Konteyneri sunucusuz çalıştır | **Fargate** |
| Yönetilen Kubernetes | **EKS** |
| Bir mesaj → çok abone | **SNS** |
| Mesajı sakla, tüketici çeksin | **SQS** |
| Belirli IP'yi engelle | **NACL** (SG deny yazamaz) |
| SQL injection'ı engelle | **WAF** |
| Ücretsiz DDoS koruması | **Shield Standard** |
| S3'te kişisel veri bul | **Macie** |
| EC2'de CVE tara | **Inspector** |
| Anormal davranış tespiti | **GuardDuty** |
| SOC 2 raporu nereden? | **Artifact** |
| Üye hesapları kısıtla | **SCP (Organizations)** |
| Landing zone'u otomatik kur | **Control Tower** |
| Mimariyi kod olarak tanımla | **CloudFormation** |
| Kodu ver, altyapıyı AWS kursun | **Elastic Beanstalk** |
| Özel hat, internet kullanmaz | **Direct Connect** |
| İnternet üzerinden şifreli tünel | **Site-to-Site VPN** |
| Trafik public internete çıkmasın | **PrivateLink / VPC endpoint** |
| Private subnet'ten yama indir | **NAT Gateway** |
| Global kullanıcılar için içerik önbelleği | **CloudFront** |
| Önden maliyet tahmini | **Pricing Calculator** |
| Geçmiş harcama analizi | **Cost Explorer** |
| Eşik aşınca uyarı | **Budgets** |
| Maliyeti departmana bağla | **Cost allocation tags** |
| Tek fatura + hacim indirimi | **Consolidated billing** |
| Atanmış TAM | **Enterprise** |
| Tüm Trusted Advisor kontrolleri | **Business ve üzeri** |
| Production down yanıt süresi (Business) | **< 1 saat** |
| Business-critical down (Enterprise) | **< 15 dakika** |
| Sesten metne | **Transcribe** |
| Metinden sese | **Polly** |
| Taranmış belgeden veri | **Textract** |
| Metnin duygusu | **Comprehend** |
| S3'e SQL sorgusu | **Athena** |
| Petabayt veri ambarı | **Redshift** |
| Multi-AZ ne sağlar? | **Failover / erişilebilirlik** |
| Read replica ne sağlar? | **Okuma performansı** |
| İnternet yetmiyor, 200 TB | **Snowball Edge** |
| Kendi veri merkezinde AWS | **Outposts** |

---

## 4. Well-Architected altı sütun (kapalı gözle say)

**O**perational Excellence · **S**ecurity · **R**eliability · **P**erformance Efficiency ·
**C**ost Optimization · **S**ustainability

**Sütun OLMAYAN tuzaklar:** Scalability, Availability, Agility, Elasticity, Automation

## 5. CAF altı perspektif

**İş tarafı:** Business · People · Governance
**Teknik taraf:** Platform · Security · Operations

## 6. 7R göç stratejisi

**Rehost** (olduğu gibi) · **Replatform** (küçük dokunuş) · **Repurchase** (SaaS al) ·
**Refactor** (yeniden yaz, en pahalı) · **Retire** (kapat) · **Retain** (bırak) · **Relocate** (VMware)

---

## 7. Sınav sabahı

- [ ] Kimlik hazır — adı sınav kaydındaki adla **birebir aynı**
- [ ] Randevu saati ve zaman dilimi kontrol edildi
- [ ] OnVUE ise: system test yapıldı, VPN kapalı, masa **tamamen** boş, oda boş
- [ ] **30 dakika önce** check-in yapılabildiğini biliyorsun
- [ ] Bu dosyayı bir kez okudun — **yeni konu çalışmadın**
- [ ] Uyudun

---

## 8. Sınav biterken

- Boş soru kalmadığını kontrol et.
- Emin olmadıklarını **değiştirme** — okuma hatası yaptığını fark etmediysen ilk içgüdün genelde doğrudur.
- Bitir. Sonuç genelde **ekranda hemen** görünür.

**Başarılar. Hazırsan bu sınav zor değil — ama hazır olmadan girme.**

---

➡️ Deneme sınavları: [`final-sinav-1.html`](final-sinav-1.html) · [`final-sinav-2.html`](final-sinav-2.html)
