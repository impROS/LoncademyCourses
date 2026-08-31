# 0.1 — AWS Hesabı Açma, Güvenlik ve Bütçe Alarmı (PRATİK)

> **Bu dosya baştan sona uygulanır, okunmaz.** Süre: ~60–75 dakika.
> Sonunda: çalışan bir AWS hesabın, MFA korumalı bir root kullanıcın, günlük işlerde kullanacağın
> bir yönetici kullanıcın ve **para harcarsan seni uyaran bir alarmın** olacak.

---

## 💸 Önce para meselesi — bunu okumadan devam etme

AWS hesabı açmak ücretsizdir, ama **kullandığın kaynak ücretlidir.** Bu setteki tüm pratikler
Free Tier / ücretsiz krediler içinde kalacak şekilde tasarlandı, ama korumasız bırakırsan fatura gelir.

**Sürpriz fatura üreten klasik 6 kaynak** (hepsi bu setin pratiklerinde YOK, ama bilerek dur):

| Kaynak | Yaklaşık aylık maliyet | Neden yakalanırsın |
|---|---|---|
| NAT Gateway | ~32 USD | VPC sihirbazı bazen otomatik kurar, silmeyi unutursun |
| Load Balancer (ALB/NLB) | ~16 USD | Deneme amaçlı kurup silmezsin |
| Boşta duran Elastic IP | ~3,6 USD | EC2'yi silersin, IP kalır |
| Silinmemiş EBS volume / snapshot | ~0,08 USD/GB | Instance silinir, disk kalır |
| Free Tier dışı RDS instance | 15 USD+ | `db.t3.micro` yerine büyük tip seçersin |
| Route 53 Hosted Zone | 0,50 USD | Test için oluşturup unutursun |

**Altın kural: Açtığın her kaynağı, işin bitince aynı gün sil.** Bu setteki her pratiğin sonunda
**"Temizlik"** adımı var — atlarsan para ödersin.

---

## Adım 1 — Hazırlık: neye ihtiyacın var

- [ ] Daha önce AWS hesabı açmamış bir **e-posta adresi** (Free Tier kişi başı bir kez)
- [ ] **Telefon numarası** (SMS/sesli doğrulama gelir)
- [ ] **Kredi veya banka kartı** — zorunlu. AWS **1 USD (veya ~1 TL karşılığı) doğrulama provizyonu** çeker, 3–5 günde iade eder. Sanal kart kabul edilmeyebilir; mümkünse gerçek kart kullan.
- [ ] Kimlik bilgilerin (fatura adresi)
- [ ] Telefonunda bir **authenticator uygulaması**: Google Authenticator, Microsoft Authenticator, Authy veya 1Password/iCloud Keychain (MFA için)

---

## Adım 2 — Hesabı aç

1. `https://portal.aws.amazon.com/billing/signup` adresine git (veya `aws.amazon.com` → **Create an AWS Account**).
2. **Root user email address** → kişisel e-postan. **AWS account name** → örneğin `ridvan-personal`.
3. E-postana gelen 6 haneli kodu gir.
4. **Root kullanıcı parolası** oluştur. Uzun ve benzersiz olsun, parola yöneticine kaydet. Bu parola hesabının tamamının anahtarı.
5. **Account type:** `Personal` seç (şirket adına açmıyorsan). Ad, adres, telefon gir.
6. **Kart bilgisi** gir. Kart doğrulaması yapılır (~1 USD provizyon).
7. **Telefon doğrulaması:** SMS veya arama ile gelen kodu gir.
8. **Support plan:** **Basic support — Free** seç. (Developer/Business planları aylık ücretlidir, sana gerekmiyor. 4.3 konusunda bu planları öğreneceksin.)
9. **"Complete sign up"** → hesap birkaç dakika içinde aktifleşir; onay e-postası gelir.

> **Free Tier hakkında:** AWS ücretsiz kullanım şartlarını 2025'te değiştirdi. Yeni hesaplarda
> kayıt sırasında bir **plan seçimi** (kredi tabanlı ücretsiz plan veya standart ücretli plan) ve
> **belirli süreli kredi** görebilirsin. Ekranda ne yazıyorsa güncel olan odur —
> **kayıt sırasında çıkan şartları oku ve not al**, hangi servislerin ne kadar ücretsiz olduğunu bilerek ilerle.
> Kredin bittiğinde ya da süre dolduğunda ücretlendirme başlar; bu yüzden Adım 5'teki bütçe alarmı **zorunlu**.

- [ ] **Kontrol:** `https://console.aws.amazon.com` adresinden root e-postanla giriş yapabiliyorsun.

---

## Adım 3 — Root kullanıcıya MFA tak (ATLAMA)

Root kullanıcı hesabındaki **her şeyi** yapabilir: hesabı kapatabilir, faturayı değiştirebilir, tüm veriyi silebilir.
Parolası çalınırsa hesabın gider. Bu yüzden ilk iş MFA.

1. Konsolda sağ üstte hesap adına tıkla → **Security credentials**.
2. **Multi-factor authentication (MFA)** bölümünde → **Assign MFA device**.
3. **Device name:** `root-phone`. Tip olarak **Authenticator app** seç.
4. QR kodu authenticator uygulamanla okut.
5. Uygulamanın ürettiği **ardışık iki kodu** sırayla gir → **Add MFA**.

- [ ] **Kontrol:** Çıkış yap, tekrar root ile giriş yap — parola sonrası 6 haneli kod sorulmalı.

> **Kural:** Bu andan sonra root kullanıcıyı **sadece** şu işler için kullanacaksın:
> hesap kapatma, fatura/ödeme bilgisi değiştirme, destek planı değiştirme, root erişim anahtarı yönetimi,
> bazı S3/SCP kilitlerini açma. Günlük iş için asla. (Bu, sınavda **çıkan** bir konudur — 2.3 IAM.)

---

## Adım 4 — Faturalama verilerini IAM kullanıcılarına aç

Varsayılan olarak root dışındaki kullanıcılar fatura ekranını göremez. Bütçe alarmı kurabilmek için bunu aç.

1. Hâlâ **root** ile girişteyken: sağ üst → **Account**.
2. Sayfada **IAM user and role access to Billing Information** bölümünü bul → **Edit** → **Activate IAM Access** → **Update**.

- [ ] **Kontrol:** Kutucuk işaretli görünüyor.

---

## Adım 5 — 💸 Bütçe alarmı kur (EN ÖNEMLİ ADIM)

Bu adım seni sürpriz faturadan koruyacak tek şey. **Şimdi yap.**

1. Konsolda arama çubuğuna `Billing and Cost Management` yaz → aç.
2. Sol menü → **Budgets** → **Create budget**.
3. **Budget type:** `Cost budget - Recommended` → Next.
4. **Budget name:** `aylik-guvenlik-limiti`
5. **Period:** `Monthly` · **Budget renewal type:** `Recurring budget`
6. **Budgeting method:** `Fixed` · **Budget amount:** `5` (USD). Bu setteki pratiklerde 5 doları geçmemelisin.
7. **Next** → **Add an alert threshold**:
   - Threshold: `50` **% of budgeted amount** → e-postan
   - **Add alert threshold** ile ikinci eşik: `80` %
   - Üçüncü eşik: `100` %
8. Her eşik için **Email recipients** kısmına kendi e-postanı yaz.
9. **Next** → **Create budget**.

Ek olarak, **gerçek zamanlı** bir uyarı daha kur (Budgets günde bir kez değerlendirir):

10. Sol menüde **Billing preferences** → **Alert preferences** → **AWS Free Tier alerts** ve
    **Invoice delivery / PDF invoices** e-postalarını aç.

- [ ] **Kontrol:** Budgets listesinde bütçen görünüyor, 3 eşik tanımlı.
- [ ] **Kontrol:** E-postana AWS'ten bütçe onay maili geldi (bazen gelmez, sorun değil).

> **Alışkanlık:** Haftada bir kez **Billing → Bills** ekranına bak. 30 saniye sürer, yüzlerce dolar kurtarır.

---

## Adım 6 — Günlük kullanacağın yönetici kullanıcıyı oluştur

Root'u kilitledik; artık günlük iş için bir IAM kullanıcısı lazım.

1. Konsol araması → `IAM` → **Users** → **Create user**.
2. **User name:** `ridvan-admin`
3. ✅ **Provide user access to the AWS Management Console** işaretle.
   - **I want to create an IAM user** seç.
   - **Custom password** ver, "Users must create a new password at next sign-in" tikini kaldır.
4. **Next** → **Permissions options:** `Attach policies directly` → **AdministratorAccess** politikasını seç.
5. **Next** → **Create user**.
6. Açılan ekrandaki **Console sign-in URL**'i (`https://<hesap-id>.signin.aws.amazon.com/console`) ve kullanıcı adı/parolayı kaydet.

Şimdi bu kullanıcıya da MFA tak:

7. **IAM → Users → ridvan-admin → Security credentials → Assign MFA device** → authenticator ile aynı adımlar.

- [ ] **Kontrol:** Root'tan çık, sign-in URL'i ile `ridvan-admin` olarak gir. MFA kodu sorulmalı.
- [ ] **Bundan sonra tüm pratikleri bu kullanıcıyla yapacaksın.**

> Sınav notu: Gerçek dünyada AWS artık **IAM Identity Center** (eski adı AWS SSO) öneriyor, tekil IAM kullanıcısı yerine.
> Öğrenme hesabında IAM kullanıcısı yeterli; ama sınavda "birden fazla hesap için merkezi kimlik" sorusunun cevabı
> **IAM Identity Center**'dır. (2.3'te göreceksin.)

---

## Adım 7 — Region'ını seç ve sabitle

1. Sağ üstteki region seçicisine tıkla.
2. **Europe (Frankfurt) `eu-central-1`** veya **Europe (Ireland) `eu-west-1`** seç.
   - Frankfurt: Türkiye'ye en düşük gecikme.
   - Ireland: en çok servisin en erken geldiği Avrupa region'ı, biraz daha ucuz.
3. Bu setteki tüm pratiklerde **aynı region'da kal.** Kaynaklarını başka region'da unutmak, sürpriz faturanın 1 numaralı sebebidir.

- [ ] **Kontrol:** Region seçicide seçtiğin bölge yazıyor. Bir yere not et: `Çalışma region'ım: ____________`

> Sınav notu: **IAM, Route 53, CloudFront, WAF (global), Organizations** global servislerdir — region seçicide "Global" görünür.
> EC2, S3 bucket, VPC, RDS gibi çoğu servis **region'a bağlıdır.** (3.2'de detaylı.)

---

## Adım 8 — İlk kaynağını aç ve sil (ısınma turu)

Amaç: konsola alışmak ve "aç–doğrula–sil" refleksini kurmak.

### 8a. Bir S3 bucket oluştur
1. Konsol araması → `S3` → **Create bucket**.
2. **Bucket name:** `clfc02-deneme-<isminden-birsey>-<rastgele-sayi>` (dünya genelinde benzersiz olmak zorunda).
3. **Region:** seçtiğin bölge. Diğer ayarlara dokunma — **Block all public access açık kalsın.**
4. **Create bucket**.
5. Bucket'a gir → **Upload** → küçük bir dosya (örneğin bir `.txt`) yükle.

- [ ] **Kontrol:** Dosya listede görünüyor, üzerine tıklayınca **Object URL** var. O URL'i tarayıcıda aç → **AccessDenied** almalısın. ✅ Bu doğru davranış: bucket varsayılan olarak gizlidir.

### 8b. Temizlik (💸 atlamak yasak)
6. Bucket içindeki dosyaları seç → **Delete** → `permanently delete` yaz → sil.
7. Bucket listesinde bucket'ı seç → **Delete** → adını yaz → sil.

- [ ] **Kontrol:** S3'te hiç bucket kalmadı.

---

## Adım 9 — CloudShell'i tanı (CLI pratiği için)

Bilgisayarına hiçbir şey kurmadan AWS CLI kullanabilirsin.

1. Konsolda sağ üstteki **terminal ikonuna** (CloudShell) tıkla. Bir dakika içinde bir Linux kabuğu açılır.
2. Şu komutları çalıştır:

```bash
aws sts get-caller-identity
```
Kim olduğunu (hesap ID, kullanıcı ARN'i) döner. **ARN'ine bak: `arn:aws:iam::<hesap-id>:user/ridvan-admin` yazmalı.**

```bash
aws s3 ls
```
Bucket listeni döner (şu an boş olmalı).

```bash
aws ec2 describe-regions --output table --query "Regions[].RegionName"
```
Tüm region isimlerini tablo olarak döker. **Kaç tane olduğunu say — global altyapı konusunda (3.2) işine yarayacak.**

- [ ] **Kontrol:** Üç komut da hata vermeden çalıştı.

> CloudShell ücretsizdir (ayda 1 GB depolama dahil). Kullanmadığında kendini kapatır.

---

## Adım 10 — Güvenlik kontrol listesi (hesabın sağlıklı mı?)

1. Konsol araması → `IAM` → sol üstte **Dashboard**.
2. **Security recommendations** kutusuna bak. Şunların hepsi yeşil/temiz olmalı:

- [ ] Root user has MFA ✅
- [ ] Root user has **no access keys** (varsa sil — root access key asla oluşturma)
- [ ] IAM kullanıcın var ve MFA'lı
- [ ] Bütçe alarmın kurulu
- [ ] Hiç açık kaynağın yok (EC2 yok, S3 yok, RDS yok)

---

## Kalıcı alışkanlıklar (bu setin sonuna kadar uygula)

| Ne zaman | Ne yap |
|---|---|
| Her pratiğin sonunda | "Temizlik" adımını uygula, kaynağı sil |
| Her hafta | Billing → Bills ekranına bak (30 sn) |
| Kaynak açmadan önce | Doğru region'da mısın kontrol et |
| Konsolu kapatırken | EC2 → Instances ekranında "running" var mı bak |
| Şüphelendiğinde | Billing → **Cost Explorer** → son 7 gün, servise göre grupla |

---

## 60 saniyelik özet

- Root = hesabın tamamı → **MFA tak, günlük kullanma.**
- Günlük iş = **IAM kullanıcısı** (MFA'lı, AdministratorAccess).
- **Bütçe alarmı kurulmadan hiçbir şey açma.**
- Bir region seç, orada kal.
- Aç → doğrula → **sil.**

---

## Sırada ne var

➡️ [`02-kayit-ve-satin-alma.md`](02-kayit-ve-satin-alma.md) — sınav kaydı, ESL ek süresi, ne satın almalısın.
Ardından ilk konu: [`../01-bulut-kavramlari/1.1-bulut-faydalari.md`](../01-bulut-kavramlari/1.1-bulut-faydalari.md)
