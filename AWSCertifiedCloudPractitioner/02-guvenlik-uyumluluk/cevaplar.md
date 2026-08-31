# 2. alan · Güvenlik ve Uyumluluk — Kendini kontrol cevapları

> Bu dosya [2.1](2.1-paylasilan-sorumluluk.md) – [2.4](2.4-guvenlik-servisleri.md) konularının
> sonundaki **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [2.1](#21-paylaşılan-sorumluluk-modeli) · [2.2](#22-yönetişim-ve-uyumluluk) ·
[2.3](#23-iam-ve-erişim-yönetimi) · [2.4](#24-güvenlik-servisleri)

---

## 2.1 Paylaşılan sorumluluk modeli

📄 Sorular: [`2.1-paylasilan-sorumluluk.md`](2.1-paylasilan-sorumluluk.md)

### Soru 1 — "Security OF the cloud" ile "security IN the cloud" farkı

**Kısa cevap:** **OF** = bulutun **kendisinin** güvenliği, AWS'in işi (bina, donanım, hipervizör,
global altyapı). **IN** = bulutun **içine koyduklarının** güvenliği, müşterinin işi (veri, yetki,
ağ kuralları, konuk işletim sistemi, uygulama).

**Ayrıntı:** Ayrımın en pratik cümlesi: **fişten aşağısı AWS, fişten yukarısı sen.**

| Katman | Kim |
|---|---|
| Veri merkezi binası, kartlı geçiş, kamera | AWS |
| Sunucu, disk, ağ cihazı, kablo | AWS |
| Hipervizör, global altyapı | AWS |
| Veri, sınıflandırma, saklama süresi | Müşteri |
| IAM kullanıcı/rol/politika, MFA | Müşteri |
| Güvenlik grubu ve NACL **kuralları** | Müşteri |
| EC2'deki konuk işletim sistemi yaması | Müşteri |

> 📌 **Sık yapılan hata:** Modeli sabit bir çizgi sanmak. Çizgi **servise göre kayar** — Soru 2'ye bak.

🔗 Konu: [2.1 §1](2.1-paylasilan-sorumluluk.md) ·
📖 [paylaşılan sorumluluk](../00-baslangic/03-kavram-sozlugu.md#sorumluluk-paylasimi)

---

### Soru 2 — EC2, RDS ve Lambda için işletim sistemi yamasını kim yapar

**Kısa cevap:** **EC2 → müşteri. RDS → AWS. Lambda → AWS** (çalışma ortamının tamamı).

**Ayrıntı:**

| Servis | Model | İşletim sistemi yaması | Müşteride kalan |
|---|---|---|---|
| EC2 | IaaS | **Müşteri** | Konuk işletim sistemi, güvenlik grubu kuralları, uygulama, veri |
| RDS | Yönetilen | **AWS** (işletim sistemi + veritabanı motoru) | Veritabanı kullanıcıları, şema, veri, şifreleme seçimi, yedek politikası |
| Lambda | Sunucusuz | **AWS** | Yalnızca **fonksiyon kodu**, IAM rolü, ortam değişkenleri, veri |

Kural tek cümleyle: **ne kadar yönetilen servis kullanırsan o kadar az sorumluluğun olur.**

> 📌 **Sık yapılan hata:** "Veritabanı benim, yamayı ben yaparım" diye düşünmek. RDS'te motorun
> yaması AWS'tedir; EC2 üzerine **kendin** kurduğun MySQL'de ise sende.

🔗 Konu: [2.1 §2](2.1-paylasilan-sorumluluk.md)

---

### Soru 3 — Hangi servis olursa olsun müşteride kalan 3 şey

**Kısa cevap:** **(1)** verinin kendisi, **(2)** kimin neye erişeceği kararı (IAM), **(3)** istemci
tarafı şifreleme ve kendi anahtarlarının yönetimi.

**Ayrıntı:** Bu üçünün ortak yanı, hepsinin **karar** olmasıdır. AWS altyapıyı sunar, kararı vermez:
şifreleme mekanizmasını sağlar ama **açma kararını** sen verirsin; yetki sistemini kurar ama
**kime yetki verileceğini** sen yazarsın.

Sınav refleksi: soruda `regardless of which AWS service is used` geçiyorsa cevap bu üçünden biridir.

> 📌 **Sık yapılan hata:** "S3 artık varsayılan şifreliyor, demek ki şifreleme AWS'te" demek.
> Mekanizma AWS'te, **hangi modeli kullanacağın ve anahtar yönetimi** sende.

🔗 Konu: [2.1 §3](2.1-paylasilan-sorumluluk.md)

---

### Soru 4 — Patch management neden "shared control"

**Kısa cevap:** Çünkü **iki tarafta da** yama işi vardır: AWS altyapıyı ve yönetilen servisleri
yamalar, müşteri EC2'deki konuk işletim sistemini ve kendi uygulamasını yamalar.

**Ayrıntı:** Paylaşılan kontroller, "aynı işin iki tarafta ayrı ayrı yapılması" demektir:

| Kontrol | AWS tarafı | Müşteri tarafı |
|---|---|---|
| Patch management | Altyapı + yönetilen servisler | Konuk işletim sistemi + uygulama |
| Configuration management | Altyapı cihazları | Kendi kaynakları (güvenlik grubu, S3 ayarı, IAM) |
| Awareness & training | Kendi çalışanları | Kendi çalışanları |

> 📌 **Sık yapılan hata:** "Paylaşılan" ifadesini "birlikte yapılıyor" diye okumak. Birlikte değil,
> **ayrı ayrı ve farklı katmanlarda** yapılıyor.

🔗 Konu: [2.1 §1](2.1-paylasilan-sorumluluk.md)

---

### Soru 5 — AWS'in uyumluluk raporlarını nereden alırsın

**Kısa cevap:** **AWS Artifact** — ücretsiz, konsoldan self-servis.

**Ayrıntı:** Artifact'te [SOC](../00-baslangic/03-kavram-sozlugu.md#soc) 1/2/3,
[ISO 27001](../00-baslangic/03-kavram-sozlugu.md#iso-27001),
[PCI DSS](../00-baslangic/03-kavram-sozlugu.md#pci-dss),
[FedRAMP](../00-baslangic/03-kavram-sozlugu.md#fedramp) raporları ve HIPAA BAA, GDPR DPA gibi
sözleşmeler bulunur.

⚠️ Ayrımı kaçırma:

| Soru | Cevap |
|---|---|
| **AWS'in** kontrolleri belgeli mi? | **Artifact** |
| **Senin** kaynakların kurallara uyuyor mu? | **AWS Config** |
| Denetim için kanıt toplamayı otomatikleştir | **Audit Manager** |

> 📌 **Sık yapılan hata:** Artifact'i "kendi uyumluluk raporum" sanmak. Artifact **AWS'in**
> belgelerini verir.

🔗 Konu: [2.1 §6](2.1-paylasilan-sorumluluk.md) · [2.2 §4](2.2-yonetisim-uyumluluk.md)

---

## 2.2 Yönetişim ve uyumluluk

📄 Sorular: [`2.2-yonetisim-uyumluluk.md`](2.2-yonetisim-uyumluluk.md)

### Soru 1 — CloudTrail, CloudWatch ve Config'i birer cümleyle ayır

**Kısa cevap:** **CloudTrail: kim ne yaptı.** **CloudWatch: sistem nasıl çalışıyor.**
**Config: kaynak ne durumda, kurala uygun mu.**

**Ayrıntı:**

| | CloudTrail | CloudWatch | Config |
|---|---|---|---|
| Kaydettiği | API çağrıları (kim, ne zaman, hangi adresten) | Metrik, log, alarm | Kaynak yapılandırması ve değişim geçmişi |
| Tipik soru | "Bu kovayı kim sildi?" | "CPU %80'i geçince haber ver" | "Bütün diskler şifreli mi?" |
| Benzetme | Güvenlik kamerası | Nabız ölçer | Envanter denetçisi |

⚠️ İki pratik not: CloudTrail son **90 günü** konsolda ücretsiz tutar, daha uzunu için S3'e
yazan bir trail açılır. **Config ücretlidir** — öğrenme hesabında açma.

> 📌 **Sık yapılan hata:** "Log" kelimesini görüp CloudTrail demek. Uygulama logları
> **CloudWatch Logs**'a gider; CloudTrail yalnızca **API çağrılarını** tutar.

🔗 Konu: [2.2 §1](2.2-yonetisim-uyumluluk.md)

---

### Soru 2 — SCP izin verir mi, yönetim hesabını etkiler mi

**Kısa cevap:** **İzin vermez, tavan koyar.** Ve **yönetim (management) hesabını etkilemez.**

**Ayrıntı:** Gerçek yetki iki kümenin kesişimidir:

```
Gerçek yetki = SCP tavanı  ∩  IAM izinleri
```

IAM'de `AdministratorAccess` olsa bile SCP yasaklıyorsa iş yapılamaz; tersine SCP izin verse bile
IAM'de izin yoksa yine yapılamaz. Bu yüzden [SCP](../00-baslangic/03-kavram-sozlugu.md#scp)'ye
"korkuluk" (guardrail) denir.

> 📌 **Sık yapılan hata:** SCP'yi yetki **verme** aracı sanmak. Bir kullanıcıya yetkiyi her zaman
> IAM politikası verir; SCP yalnızca üst sınırı çizer.

🔗 Konu: [2.2 §2](2.2-yonetisim-uyumluluk.md)

---

### Soru 3 — Organizations ile Control Tower farkı

**Kısa cevap:** **Organizations** hesapları gruplayan temel servistir; **Control Tower** onun
üstüne kurulu, çok hesaplı ortamı (landing zone) **otomatik kuran** yönetişim katmanıdır.

**Ayrıntı:**

| | Organizations | Control Tower |
|---|---|---|
| Ne | Hesap grubu + birleşik faturalama + SCP | Hazır kurulum ve denetim katmanı |
| Kurulum | Elle yapılandırırsın | **Landing zone'u otomatik kurar** |
| Ek yetenek | — | Guardrail kütüphanesi, Account Factory, uyumluluk panosu |
| Sınav ipucu | `consolidated billing`, `SCP` | `set up a multi-account environment quickly`, `landing zone`, `guardrails` |

Control Tower arka planda Organizations, IAM Identity Center, CloudTrail, Config ve S3'ü senin
yerine yapılandırır.

> 📌 **Sık yapılan hata:** İkisini alternatif sanmak. Control Tower Organizations'ı **kullanır**;
> yerine geçmez.

🔗 Konu: [2.2 §2–3](2.2-yonetisim-uyumluluk.md)

---

### Soru 4 — Denetçi AWS'in ISO 27001 belgesini istedi, nereye gidersin

**Kısa cevap:** **AWS Artifact.** Konsoldan ücretsiz indirilir; gizlilik sözleşmesi kabulü istenir.

**Ayrıntı:** Senaryo cümlesindeki iki kelime cevabı belirler: belge **AWS'in** ve **denetim
raporu**. Ayrımı kaçırmamak için:

| İstenen | Yer |
|---|---|
| AWS'in ISO/SOC/PCI raporu | **Artifact** |
| Kendi kaynaklarının uyum durumu | **Config** |
| Denetim kanıtı toplamayı otomatikleştirme | **Audit Manager** |
| Bulguların tek panelde toplanması | **Security Hub** |

> 📌 **Sık yapılan hata:** "Compliance" kelimesini görüp Config demek. Config **senin**
> kaynaklarına bakar, AWS'in belgesini vermez.

🔗 Konu: [2.2 §4](2.2-yonetisim-uyumluluk.md)

---

### Soru 5 — Müşteri AWS üzerinde DDoS testi yapabilir mi

**Kısa cevap:** **Hayır — ön izin olmadan yapamaz.** Sıradan penetrasyon testi (listelenen
servislerde, kendi kaynaklarında) ön izin gerektirmez; ama **DoS/DDoS** simülasyonu için AWS'e
başvurmak gerekir.

**Ayrıntı:**

| Test | İzin |
|---|---|
| Kendi EC2/RDS/Lambda/API Gateway kaynaklarına penetrasyon testi | Ön izin **gerekmez** |
| DoS / DDoS simülasyonu | **AWS'e başvuru gerekir** |
| Port/protokol/istek seli (flooding) | **Yasak** |
| Başka müşterilerin kaynakları | **Yasak** |
| AWS'in kendi altyapısı | **Yasak** |

Mantığı basit: hacimsel saldırı testi **komşu müşterileri** de etkiler; sınır orada çizilir.

> 📌 **Sık yapılan hata:** "Penetrasyon testi tamamen yasak" ya da "tamamen serbest" diye
> ezberlemek. Doğru cevap ikisinin arasında ve **DoS ayrımı** üzerinden kuruludur.

🔗 Konu: [2.2 §5](2.2-yonetisim-uyumluluk.md)

---

## 2.3 IAM ve erişim yönetimi

📄 Sorular: [`2.3-iam.md`](2.3-iam.md)

### Soru 1 — Sadece kök kullanıcının yapabildiği 3 iş

**Kısa cevap:** **Hesabı kapatmak · destek planını değiştirmek · ödeme/fatura bilgisini
değiştirmek.** (Hesap adı ve e-posta değişikliği, S3'te MFA Delete yapılandırması, kendini
kilitleyen IAM yöneticisini kurtarmak da bu listededir.)

**Ayrıntı:** Ortak yan: hepsi **hesabın kendisine** dair, kaynaklara dair değil. Bu yüzden
IAM politikasıyla devredilemezler.

Tam liste sınavda dönüşümlü sorulur: hesabı kapatma · hesap ayarları · ödeme yöntemi · destek
planı · IAM izinlerini geri yükleme · S3 MFA Delete · tüm erişimi reddeden hatalı bir kova
politikasını düzeltme · Reserved Instance Marketplace satıcılığı · GovCloud hesabı açma.

> 📌 **Sık yapılan hata:** "EC2 silmek" gibi güçlü ama sıradan işleri listeye koymak.
> Bunları uygun yetkili bir IAM kimliği de yapar.

🔗 Konu: [2.3 §1](2.3-iam.md)

---

### Soru 2 — EC2'deki uygulamaya S3 erişimi vermenin en güvenli yolu

**Kısa cevap:** **EC2 instance'a bir IAM rolü atamak.** Access key'i makineye kopyalamak yanlış
cevaptır.

**Ayrıntı:** Rolün üstünlüğü tek bir teknik ayrıntıdan gelir: rolün **kalıcı kimlik bilgisi yoktur**.
Uygulama çalışırken AWS STS'ten **süreli** kimlik alır ve bu kimlik kendiliğinden yenilenir.

| | Access key kopyalamak | Rol atamak |
|---|---|---|
| Diskte kalıcı sır | **Var** | Yok |
| Elle döndürme (rotation) | Gerekir | Gerekmez |
| Sızarsa | Süresiz geçerli | Kısa sürede geçersiz |
| Koda gömülme riski | Yüksek | Yok |

> 📌 **Sık yapılan hata:** "Access key'i ortam değişkenine koyarım, güvenlidir" demek.
> Sınav `MOST secure` dediğinde cevap **her zaman** roldür.

🔗 Konu: [2.3 §2](2.3-iam.md)

---

### Soru 3 — Allow ve Deny çakışırsa, hiçbir politika bahsetmiyorsa

**Kısa cevap:** Çakışmada **Deny kazanır**. Hiçbir politika bahsetmiyorsa istek **reddedilir**
(örtük ret).

**Ayrıntı:** Değerlendirme sırası üç adımdır:

```
1) Açık DENY var mı?   → varsa REDDEDİLİR (her şeyi ezer)
2) Açık ALLOW var mı?  → varsa İZİN VERİLİR
3) Hiçbiri yoksa       → örtük ret → REDDEDİLİR
```

Ezber cümlesi: **varsayılan reddetmedir; açık izin açar, açık ret her şeyi kapatır.**
Örnek: kullanıcıda `AdministratorAccess` olsa bile bir politika `s3:DeleteBucket` için `Deny`
diyorsa kova silinemez.

> 📌 **Sık yapılan hata:** "Yönetici yetkisi varsa her şeyi yapar" demek. Açık ret yönetici
> yetkisini de ezer.

🔗 Konu: [2.3 §3](2.3-iam.md) ·
📖 [en az yetki](../00-baslangic/03-kavram-sozlugu.md#least-privilege)

---

### Soru 4 — IAM Identity Center ile Cognito'yu bir cümleyle ayır

**Kısa cevap:** **Identity Center çalışanlar içindir** (birden çok AWS hesabına tek oturumla
erişim); **Cognito uygulamanın son kullanıcıları içindir** (müşterilerin kayıt/giriş sistemi).

**Ayrıntı:**

| Servis | Kimin için | İşi |
|---|---|---|
| IAM Identity Center | Çalışanlar | Tek oturumla birçok hesaba erişim; harici kimlik sağlayıcı bağlanabilir |
| AWS Directory Service | Şirketin Active Directory ortamı | AWS'te yönetilen Active Directory ya da şirket içine bağlantı |
| Amazon Cognito | Uygulamanın müşterileri | Mobil/web uygulaması için kayıt-giriş, kullanıcı havuzu, sosyal federasyon |

Ayırt edici soru: **kim giriş yapıyor — senin ekibin mi, senin müşterin mi?**

> 📌 **Sık yapılan hata:** "Single sign-on" kelimesini görüp her ikisine de Identity Center demek.
> Kullanıcı **son kullanıcıysa** cevap Cognito'dur.

🔗 Konu: [2.3 §5](2.3-iam.md)

---

### Soru 5 — Rolün access key'i var mıdır, kimlik bilgisini nereden alır

**Kısa cevap:** **Yoktur.** Rol üstlenildiğinde kimlik bilgisi **AWS STS**'ten gelir ve
**süreli**dir.

**Ayrıntı:** Rol bir kimlik değil, **geçici olarak üstlenilen bir kılıktır**. Parolası yoktur,
kalıcı anahtarı yoktur, doğrudan "giriş yapılamaz". Süre dolunca kimlik geçersizleşir ve
yeniden alınır — bu yüzden sızıntının ömrü kısadır.

| | IAM kullanıcı | IAM rol |
|---|---|---|
| Parola | Olabilir | Yok |
| Kalıcı access key | Olabilir | **Yok** |
| Kimlik kaynağı | Kalıcı olarak tanımlı | **STS**, süreli |
| Kim üstlenir | — | EC2, Lambda, başka hesap, federe kullanıcı |

> 📌 **Sık yapılan hata:** Rolü "izin paketi" sanıp gruba benzetmek. Grup kullanıcı **koleksiyonudur**
> ve giriş yapılamaz; rol **üstlenilir** ve geçici kimlik üretir.

🔗 Konu: [2.3 §2](2.3-iam.md)

---

## 2.4 Güvenlik servisleri

📄 Sorular: [`2.4-guvenlik-servisleri.md`](2.4-guvenlik-servisleri.md)

### Soru 1 — GuardDuty, Inspector, Macie ve Detective'i birer cümleyle ayır

**Kısa cevap:** **GuardDuty** kötü niyetli davranışı tespit eder · **Inspector** yazılım
zafiyeti tarar · **Macie** S3'teki hassas veriyi bulur · **Detective** bir bulgunun kök nedenini
araştırır.

**Ayrıntı:**

| Servis | Neye bakar | Tetikleyici ifade |
|---|---|---|
| GuardDuty | CloudTrail, VPC akış logları, DNS logları (ajan kurmaz) | `threat detection`, `unusual API activity`, `crypto mining` |
| Inspector | EC2, konteyner imajları, Lambda | `vulnerability scan`, `CVE`, `missing patches` |
| Macie | S3 nesneleri | `sensitive data`, `PII in S3` |
| Detective | GuardDuty bulguları + loglar | `investigate the root cause` |

Hafıza kancası: **GuardDuty bekçi köpeği · Inspector müfettiş · Macie veri dedektifi ·
Detective soruşturmacı.** Hepsinin bulgusunu tek panelde toplayan ise **Security Hub**'dır.

> 📌 **Sık yapılan hata:** GuardDuty'yi zafiyet tarayıcısı sanmak. GuardDuty **davranışa** bakar,
> açık taramaz; açık taraması Inspector'ındır.

🔗 Konu: [2.4 §2](2.4-guvenlik-servisleri.md)

---

### Soru 2 — Güvenlik grubu ile NACL arasındaki 4 fark

**Kısa cevap:** **(1)** Güvenlik grubu instance, NACL subnet seviyesinde çalışır. **(2)** Güvenlik
grubu durumlu, NACL durumsuzdur. **(3)** Güvenlik grubunda yalnız izin, NACL'de izin **ve** ret
yazılır. **(4)** Güvenlik grubunda kurallar birlikte, NACL'de **numara sırasına göre** değerlendirilir.

**Ayrıntı:**

| | Güvenlik grubu | NACL |
|---|---|---|
| Seviye | Instance / ağ arayüzü | Subnet |
| Durum takibi | **Durumlu** — gelen izinliyse cevabı otomatik çıkar | **Durumsuz** — gidiş ve dönüş için ayrı kural |
| Kural tipi | Yalnız **izin** | **İzin ve ret** |
| Değerlendirme | Tüm kurallar birlikte | Numara sırası, ilk eşleşen kazanır |
| Varsayılan | Gelen kapalı, giden açık | Varsayılan NACL her şeye açık |
| Kaç tane | Bir kaynağa birden çok | Bir subnet'e **tek** |

Hafıza kancası: güvenlik grubu **kapıdaki görevli** (kişiyi tanır, çıkarken sormaz);
NACL **sitenin bariyeri** (giriş-çıkış ayrı, kara liste tutabilir).

> 📌 **Sık yapılan hata:** Durumsuzluğu atlamak. NACL'de yalnız gelen kuralı yazıp dönüş kuralını
> unutmak, "kural doğru ama bağlantı kurulmuyor" tablosunun bir numaralı sebebidir.

🔗 Konu: [2.4 §4](2.4-guvenlik-servisleri.md) ·
📖 [NACL](../00-baslangic/03-kavram-sozlugu.md#nacl)

---

### Soru 3 — Bir IP adresini engellemek: hangisi, neden

**Kısa cevap:** **NACL** — çünkü [güvenlik grubunda](../00-baslangic/03-kavram-sozlugu.md#security-group)
**ret kuralı yazılamaz**. (Web isteği katmanında engellemek gerekiyorsa **WAF** de kullanılır.)

**Ayrıntı:** Güvenlik grubu yalnızca "kimler girebilir" listesidir; "şu giremez" diye bir satır
yazamazsın. Bir adresi dışarıda tutmanın yolu ya subnet önündeki NACL'de ret kuralı yazmak ya da
uygulama katmanında WAF kuralı koymaktır.

| Katman | Araç |
|---|---|
| Subnet (ağ) | **NACL** — ret kuralı yazılabilir |
| Web isteği (uygulama) | **WAF** — adres, ülke, istek hızı kuralı |
| Hacimsel saldırı | **Shield** |

> 📌 **Sık yapılan hata:** "Güvenlik grubundan o IP'yi çıkarırım" demek. Zaten listede olmayan
> bir adres için yapacak bir şey yoktur; engelleme **açık ret** gerektirir.

🔗 Konu: [2.4 §3–4](2.4-guvenlik-servisleri.md)

---

### Soru 4 — KMS yerine CloudHSM'i ne zaman seçersin

**Kısa cevap:** Mevzuat **adanmış (tek kiracılı) donanım** şart koşuyorsa ya da anahtara
**AWS'in bile erişememesi** gerekiyorsa.

**Ayrıntı:**

| | KMS | CloudHSM |
|---|---|---|
| Donanım | Çok kiracılı, yönetilen | **Sana ayrılmış fiziksel cihaz** |
| Anahtarı kim yönetir | AWS'le birlikte | **Yalnız sen** |
| AWS erişebilir mi | Servis olarak evet | **Hayır** |
| Yönetim yükü | Düşük | Yüksek |
| Fiyat | Düşük | **Yüksek** |

Varsayılan tercih **KMS**'tir. Sınavda `dedicated hardware security module`, `single-tenant`,
`exclusive control of keys` ifadeleri görünce CloudHSM'e geç.

> 📌 **Sık yapılan hata:** "Daha güvenli olan hep CloudHSM" diye seçmek. Soruda adanmış donanım
> ya da tam kontrol şartı yoksa doğru cevap KMS'tir.

🔗 Konu: [2.4 §1](2.4-guvenlik-servisleri.md) ·
📖 [KMS / CloudHSM](../00-baslangic/03-kavram-sozlugu.md#kms)

---

### Soru 5 — Shield Standard ücretli mi, kim için açık

**Kısa cevap:** **Ücretsizdir ve bütün AWS müşterileri için otomatik olarak açıktır.**
Etkinleştirmeye gerek yoktur.

**Ayrıntı:**

| | Shield Standard | Shield Advanced |
|---|---|---|
| Ücret | **Ücretsiz** | Aylık taahhütlü, ücretli |
| Kapsam | Yaygın ağ/taşıma katmanı saldırıları | Gelişmiş saldırılar |
| Ek hizmet | — | 7/24 müdahale ekibi, **maliyet koruması** |
| Açma | **Otomatik** | Abonelik gerekir |

⚠️ Ayrımı kaçırma: hacimsel saldırı → **Shield**; SQL enjeksiyonu, siteler arası betik çalıştırma,
kötü bot → **WAF**.

> 📌 **Sık yapılan hata:** "DDoS koruması için ne satın almalıyım?" sorusuna otomatik olarak
> Shield Advanced demek. Temel koruma zaten ücretsiz açıktır; soru **gelişmiş** koruma
> ya da müdahale ekibi istiyorsa Advanced'a geç.

🔗 Konu: [2.4 §3](2.4-guvenlik-servisleri.md) ·
📖 [WAF / Shield](../00-baslangic/03-kavram-sozlugu.md#waf)

---

⬅️ [Bölüme dön](2.1-paylasilan-sorumluluk.md) ·
📖 [Kavram sözlüğü](../00-baslangic/03-kavram-sozlugu.md) ·
⚙️ [Seçim rehberi](../00-baslangic/04-secim-rehberi.md)
