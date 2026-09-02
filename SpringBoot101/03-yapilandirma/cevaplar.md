# 03 · Yapılandırma — Kendini kontrol cevapları

> Bu dosya [3.1](3.1-oncelik-sirasi.md) – [3.3](3.3-profiller.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:**
[3.1](#31-ayar-nereden-geliyor-öncelik-sırası) ·
[3.2](#32-configurationproperties-ve-doğrulama) ·
[3.3](#33-profiller)

---

## 3.1 Ayar nereden geliyor: öncelik sırası

📄 Sorular: [`3.1-oncelik-sirasi.md`](3.1-oncelik-sirasi.md)

### Soru 1 — Dosyada 1000 yazıyor ama uygulama 5000 kullanıyor; sırayla hangi üç yere bakarsın?

**Kısa cevap:** Yukarıdan aşağı: **1) komut satırı argümanları**, **2) Java sistem özellikleri
(`-D`)**, **3) ortam değişkenleri**. Üçü de temizse sıradaki şüpheli profil dosyasıdır.

**Ayrıntı:**

Aramayı öncelik sırasının **tepesinden** başlat; dosyanın kendisine en son bak, çünkü dosya
zaten okunuyor — sorun onu ezen bir şey.

| Sıra | Nereye bakarsın | Somut olarak ne ararsın |
|---|---|---|
| 1 | Komut satırı | Başlatma betiğinde / `docker run` satırında `--siparis.zaman-asimi=5000` |
| 2 | Sistem özellikleri | `JAVA_OPTS`, `JAVA_TOOL_OPTIONS`, betikte `-Dsiparis.zaman-asimi=5000` |
| 3 | Ortam değişkenleri | `env` çıktısında `SIPARIS_ZAMANASIMI=5000` var mı |
| 4 | Profil dosyası | Açık profil varsa `application-<profil>.properties` içinde aynı anahtar |
| 5 | Ana dosya | Aynı klasörde ikinci bir `application.yaml` var mı |

Tahmin etmeden tek soruyla bitirmenin yolu, Actuator açıksa:

```bash
curl -s http://localhost:8080/actuator/env/siparis.zaman-asimi
```

Yanıt değeri **ve hangi kaynaktan geldiğini** birlikte verir. ⚠️ Bu uç nokta ayar
değerlerini gösterir; üretimde herkese açma.

> 📌 **Sık yapılan hata:** Aramaya `application.properties`'ten başlamak. O dosyayı beş kere
> okumak hiçbir şey öğretmez — değeri **oraya rağmen** kazanan kaynak başka yerdedir.

🔗 Konu: [3.1 §2 Öncelik sırası](3.1-oncelik-sirasi.md) · [3.1 §6 Yanlış hâlleri](3.1-oncelik-sirasi.md)

---

### Soru 2 — `siparis.magaza-adi` ortam değişkeni olarak nasıl yazılır? Yaygın yanlış yazım hangisi, ne olur?

**Kısa cevap:** `SIPARIS_MAGAZAADI`. Yaygın yanlış yazım `SIPARIS_MAGAZA_ADI`; **eşleşmez,
hata da vermez** — uygulama dosyadaki değerle açılır.

**Ayrıntı:**

Kural üç adımdır: **nokta → `_` · tire → silinir · hepsi büyük harf.**

```
siparis.magaza-adi   →   SIPARIS_MAGAZAADI
        ^      ^                ^      ^^^^^^^^
      nokta   tire          alt çizgi  tire silindi
```

Ölçüm — aynı uygulama, iki farklı değişken adı:

```
SIPARIS_MAGAZAADI=ortamdan-gelen   →   ### DEGER = ortamdan-gelen     ✅ eşleşti
SIPARIS_MAGAZA_ADI=yanlis-yazim    →   ### DEGER = dosyadan-gelen     ❌ yok sayıldı
```

İkinci satırda ne hata ne uyarı var. Spring o adı bir ayar olarak hiç tanımadığı için ortada
"tanınmayan ayar" durumu bile yoktur — bu yüzden uyaracak bir şey de yoktur.

Dosya içinde ise bağlama relaxed'tır: `siparis.magaza-adi`, `siparis.magazaAdi`,
`siparis.magaza_adi`, `siparis.MAGAZA-ADI` — dördü de aynı anahtara gider. **Katı olan
yalnızca ortam değişkeni yazımıdır.**

> 📌 **Sık yapılan hata:** Tireyi de bir ayraç sanıp alt çizgiye çevirmek. Docker ve
> Kubernetes'te en sık yapılan yapılandırma hatası budur ve tek belirtisi "yerelde çalışıyor,
> container'da çalışmıyor" cümlesidir.

🔗 Konu: [3.1 §3 Relaxed binding](3.1-oncelik-sirasi.md) · [3.3 §3 Ortam değişkeni yazımı](3.3-profiller.md)

---

### Soru 3 — Öncelik sırası neden tuzak değil özellik?

**Kısa cevap:** Çünkü **aynı jar dosyasını hiç değiştirmeden** test ve üretim ortamında farklı
ayarlarla çalıştırabilmenin yolu tam olarak budur.

**Ayrıntı:**

Sıra olmasaydı ayarı değiştirmek için yapı çıktısını değiştirmen gerekirdi — yani ortam
başına ayrı bir jar:

| Sıra olmasaydı | Sıra olduğu için |
|---|---|
| Ortam başına ayrı yapı çıktısı | Tek yapı çıktısı, üç ortam |
| Ayar değişikliği = yeniden derleme + dağıtım | Ayar değişikliği = ortam değişkenini değiştir, yeniden başlat |
| Test edilen jar ile üretime giden jar farklı | Test edilen jar ile üretime giden jar **aynı** |

Son satır asıl kazançtır: test ettiğin şeyle dağıttığın şeyin bit bit aynı olması.

"Beklemediğim değer geldi" şikâyetini üreten mekanizma ile bu kazancı üreten mekanizma
**aynı mekanizmadır**. Onu tuzak yapan sıra değil, sıranın bilinmemesidir.

> 📌 **Sık yapılan hata:** Sorunu yaşayınca "keşke sadece dosyayı okusa" diye düşünmek. O
> durumda ayarı üretimde değiştirmenin tek yolu yeniden paketlemek olurdu.

🔗 Konu: [3.1 §2 Öncelik sırası](3.1-oncelik-sirasi.md)

---

### Soru 4 — `@Value("${x}")` ile `@Value("${x:varsayilan}")` arasında eksik ayarda ne fark var?

**Kısa cevap:** Varsayılansız olan uygulamayı **açtırmaz**; varsayılanlı olan sessizce
varsayılanla açar. Zorunlu ayarlarda varsayılansız, gerçekten makul bir varsayılanı olan
ayarlarda varsayılanlı yazılır.

**Ayrıntı:**

```java
@Value("${siparis.magaza-adi}")            // ayar yoksa → açılış durur
@Value("${siparis.magaza-adi:Merkez}")     // ayar yoksa → "Merkez"
```

Varsayılansız hâlin çıktısı:

```
Could not resolve placeholder 'siparis.magaza-adi' in value "${siparis.magaza-adi}"
```

| | Varsayılansız | Varsayılanlı |
|---|---|---|
| Eksik ayarda davranış | Açılış durur | Açılır, varsayılan kullanılır |
| Hatanın çıktığı an | **Dağıtım anı** | Belki hiç; belki ilk yanlış sipariş |
| Ne zaman doğru | Veritabanı adresi, API anahtarı, mağaza adı gibi **zorunlu** ayarlar | Timeout, retry sayısı gibi makul bir varsayılanı olan ayarlar |

Karar ölçütü tek soruda: **"Bu ayar verilmemişse uygulamanın çalışması doğru mu?"** Cevap
hayırsa varsayılan yazma.

> 📌 **Sık yapılan hata:** Açılış hatasından kurtulmak için varsayılan eklemek. O hata
> çözülmüş olmaz, yalnızca **görünmez** olur — ve üretimde eksik ayarla çalışan bir uygulamaya
> dönüşür.

🔗 Konu: [3.1 §5 Değeri okumanın iki yolu](3.1-oncelik-sirasi.md) · [3.2 §3 Doğrulama](3.2-configuration-properties.md)

---

### Soru 5 — Hem `application.properties` hem `application.yaml` var. Hangisi kazanır, neden tehlikeli?

**Kısa cevap:** **`.properties` kazanır.** Tehlikesi, YAML'ı düzenleyen kişinin hiçbir hata
almadan hiçbir şey değiştirememesidir.

**Ayrıntı:**

```
src/main/resources/
├── application.properties     ← siparis.zaman-asimi=1000     ✅ kazanan
└── application.yaml           ← siparis: { zaman-asimi: 5000 }  ❌ ezilen
```

Uygulama `1000` ile çalışır. Bu senaryonun kötü olmasının sebebi değerin yanlış olması değil,
**geri bildirimin hiç olmaması**:

| Beklenen | Gerçekleşen |
|---|---|
| "Yanlış dosyayı düzenledin" uyarısı | Yok |
| Açılışta çakışma hatası | Yok |
| Değişikliğin etkisi | Yok |

Elde kalan tek sinyal "neden değişmiyor?" sorusudur ve o soru genelde saatler sürer. Sorun
ekipteki kişinin bir şeyi yanlış yapması değil; **iki dosyanın birlikte var olmasıdır.**

✅ **Doğrusu:** Birini sil. Hangisini tuttuğun ikinci sorudur — liste ve iç içe yapın varsa
YAML, küçük bir ayar kümesiyse `.properties`.

> 📌 **Sık yapılan hata:** "İkisini de tutalım, hangisi lazımsa oraya yazarız." Aynı anahtarın
> iki dosyada bulunması an meselesidir ve o andan sonra hangisinin okunduğunu kimse
> hatırlamaz.

🔗 Konu: [3.1 §4 `properties` mi `yaml` mı](3.1-oncelik-sirasi.md) · [3.1 §6 Hata 3](3.1-oncelik-sirasi.md)

---

## 3.2 @ConfigurationProperties ve doğrulama

📄 Sorular: [`3.2-configuration-properties.md`](3.2-configuration-properties.md)

### Soru 1 — Sekiz ayarı `@Value` ile okumanın üç somut derdi nedir?

**Kısa cevap:** 1) Ayar adları **metin** olarak yazılıdır, yazım hatasını derleyici yakalamaz.
2) **Doğrulama yoktur** — `zaman-asimi=-5` sorunsuz geçer. 3) Ayarlar **dağınıktır**, aynı
`siparis.*` ailesi birçok sınıfa yayılır.

**Ayrıntı:**

| Dert | Somut sonucu | `@ConfigurationProperties` ne yapar |
|---|---|---|
| Ad metin olarak yazılı | Yazım hatası çalışma zamanında patlar ya da varsayılan sessizce devreye girer | Ad, tek bir tipin bileşeni olur; hata tek yerde |
| Doğrulama yok | Negatif timeout, boş mağaza adı fark edilmez | `@Validated` + kısıtlar, açılışta patlatır |
| Ayarlar dağınık | Tam `siparis.*` listesini kimse bilmez | Nesne, ayarların tek doğrusu ve tek belgesi olur |

Somut hâli — sekiz `@Value` parametresi taşıyan constructor:

```java
public SiparisServisi(
        @Value("${siparis.magaza-adi}") String magazaAdi,
        @Value("${siparis.zaman-asimi:3000}") int zamanAsimi,
        @Value("${siparis.bekleme:30s}") Duration bekleme,
        ...
        SiparisDeposu depo) { ... }
```

Buradaki `"${siparis.zaman-asimi:3000}"` metninde bir harf yanlış olsa — örneğin
`zaman-asmi` — kod derlenir, uygulama açılır ve timeout sessizce 3000 olur.

> 📌 **Sık yapılan hata:** Sekiz `@Value`'yu tek bir yapılandırma sınıfında toplayıp sorunu
> çözdüğünü sanmak. Dağınıklık azalır ama **adlar hâlâ metindir ve doğrulama noktası yoktur**
> — üç dertten yalnızca biri çözülmüştür.

🔗 Konu: [3.2 Neden bu konu](3.2-configuration-properties.md) · [3.1 §5](3.1-oncelik-sirasi.md)

---

### Soru 2 — `record` ayar sınıfına `@Component` yazdın. Ne olur, doğrusu nedir?

**Kısa cevap:** **Çalışmaz.** `record` için Spring constructor bağlaması yapar ve bu
`@Component` yoluyla devreye girmez. Doğrusu `@EnableConfigurationProperties(...)` ya da
`@ConfigurationPropertiesScan`.

**Ayrıntı:**

```java
@Component                                       // ← record ile çalışmaz
@ConfigurationProperties(prefix = "siparis")
public record SiparisAyarlari(String magazaAdi) {}
```

İki doğru yol:

```java
@SpringBootApplication
@EnableConfigurationProperties(SiparisAyarlari.class)   // Yol A — sınıfı tek tek kaydeder
public class SiparisServisiUygulamasi { ... }
```

```java
@SpringBootApplication
@ConfigurationPropertiesScan                            // Yol B — işaretli tüm sınıfları tarar
public class SiparisServisiUygulamasi { ... }
```

| Kaç ayar sınıfın var | Tercih |
|---|---|
| Bir–iki | Yol A: kayıt listesi belge gibi okunur |
| Çok | Yol B: yeni sınıf ekleyen kişinin kayıt yazmayı unutması imkânsızlaşır |

`@Component` normal **sınıflarda** çalışır — kafa karışıklığının kaynağı budur. Ayrım
`record` olup olmamasıdır, `@ConfigurationProperties` olup olmaması değil.

> 📌 **Sık yapılan hata:** "Bende `@Component` ile çalışmıştı" deyip sorunu başka yerde
> aramak. Çalıştığı yerde sınıf `record` değildi.

🔗 Konu: [3.2 §2 Bean'i devreye almanın iki yolu](3.2-configuration-properties.md) · [3.2 §7 Hata 1](3.2-configuration-properties.md)

---

### Soru 3 — `@Min(100)` yazdın ama `zaman-asimi=5` geçiyor. İki olası sebep?

**Kısa cevap:** 1) Sınıfın başında **`@Validated` yok**. 2) Projede
**`spring-boot-starter-validation` bağımlılığı yok**. (İç içe bir nesnedeki kısıttan söz
ediyorsan üçüncüsü: dış alanda **`@Valid` yok**.)

**Ayrıntı:**

Kısıt yazmak doğrulamayı **açmaz**. Eksik parçalar:

| Eksik olan | Belirti | Düzeltme |
|---|---|---|
| `@Validated` | Kısıt kodda durur, hiç denetlenmez | Sınıfın başına `@Validated` |
| `spring-boot-starter-validation` | Denetleyecek altyapı yok | `pom.xml`'e bağımlılığı ekle |
| İç içe alanda `@Valid` | Dış kısıtlar çalışır, iç kısıtlar atlanır | `@Valid Yeniden yenidenDeneme` |

Ölçülen hâli — `@Validated` silinip ayarlar bozuk bırakıldığında:

```
Uygulama açıldı. zamanAsimi=5 değeriyle. Hiçbir uyarı yok.
```

`@Validated` geri konduğunda aynı ayarla:

```
Binding to target tr.loncademy.siparis.SiparisAyarlari failed:

    Property: siparis.zamanAsimi
    Value: "5"
    Origin: class path resource [application.properties] - 2:21
    Reason: must be greater than or equal to 100
```

Aradaki tek fark bir satırlık anotasyon.

> 📌 **Sık yapılan hata:** Kısıtı ekleyip "artık korunuyoruz" demek ve hiç denememek. **Bir
> kısıt eklediğinde onu bilerek boz ve gerçekten patladığını gör** — bu üç eksikliğin üçünü
> de otuz saniyede yakalar.

🔗 Konu: [3.2 §3 Doğrulama](3.2-configuration-properties.md) · [3.2 §7 Hata 2](3.2-configuration-properties.md)

---

### Soru 4 — Doğrulama hatasındaki `Origin:` satırı ne işe yarar?

**Kısa cevap:** Bozuk değerin **hangi dosyada, hangi satır ve sütunda** yazdığını söyler —
büyük bir projede ayarın kaynağını bulmanın en hızlı yolu.

**Ayrıntı:**

```
    Property: siparis.zamanAsimi
    Value: "5"
    Origin: class path resource [application.properties] - 2:21
    Reason: must be greater than or equal to 100
```

Dört satır dört ayrı soruyu cevaplar:

| Satır | Cevapladığı soru |
|---|---|
| `Property:` | Hangi ayar bozuk (bağlanmış, camelCase adıyla) |
| `Value:` | Ne yazıyor — `"5"` |
| `Origin:` | **Nerede yazıyor** — `application.properties`, 2. satır, 21. sütun |
| `Reason:` | Neden kabul edilmedi — `must be greater than or equal to 100` |

`Origin:` özellikle 3.1'in sorusunu çözer: aynı anahtar profil dosyasında, ana dosyada ve
ortam değişkeninde olabilir; bu satır hangisinin kazandığını **tahmin ettirmeden** söyler.
Kaynak bir ortam değişkeniyse `Origin:` onu gösterir, dosyayı değil.

Ayrıca çıktı **hataların hepsini birden** listeler — tek tek düzeltip yeniden çalıştırmazsın.

> 📌 **Sık yapılan hata:** `Origin:` satırını kısıtın Java kaynağındaki yeri sanmak. Kısıtın
> yerini zaten biliyorsun; bilmediğin şey **değerin** nereden geldiğidir.

🔗 Konu: [3.2 §3 Doğrulama](3.2-configuration-properties.md) · [3.1 §2 Öncelik sırası](3.1-oncelik-sirasi.md)

---

### Soru 5 — "Yanlış ayarla açılmamak" neden kayıp değil kazanç?

**Kısa cevap:** Çünkü seçenek "açılmamak" ile "açılmak" değil; **"açılışta patlamak" ile
"üretimde yanlış davranmak"**. Doğrulama, sessiz hatayı gürültülü hataya çevirir.

**Ayrıntı:**

Aynı bozuk ayarın iki dünyada gördüğü karşılık:

| | `@Validated` yok | `@Validated` var |
|---|---|---|
| Uygulama | Açılır | **Açılmaz** |
| Hata ne zaman fark edilir | Belki günler sonra, yanlış davranışın sonucundan | Dağıtımın ilk saniyesinde |
| Fark edildiğinde bilinen | "Bir şeyler yanlış" | Ayar adı, değeri, dosya ve satırı, sebebi |
| Etkilenen | Gerçek siparişler | Kimse |
| Geri alma maliyeti | Veri düzeltme, iletişim, güven | Bir satır düzelt, yeniden başlat |

`zaman-asimi=5` ile açılan bir uygulama hata vermez — sadece 5 milisaniyede vazgeçer ve
sipariş kaybeder. Kaybın nereden geldiğini bulmak, açılışta çıkan `Reason:` satırını okumaktan
kat kat pahalıdır.

Açılışın durması aynı zamanda **dağıtım hattının** işine yarar: sağlık kontrolü geçmez, yeni
sürüm devreye alınmaz, eski sürüm ayakta kalır.

> 📌 **Sık yapılan hata:** Dağıtım sırasında açılış patlayınca `@Validated`'ı kaldırmak.
> Bu, alarmı çalıştığı için sökmektir — ayar hâlâ bozuktur, artık sadece kimse bilmez.

🔗 Konu: [3.2 §3 Doğrulama](3.2-configuration-properties.md) · [3.1 Neden bu konu](3.1-oncelik-sirasi.md)

---

## 3.3 Profiller

📄 Sorular: [`3.3-profiller.md`](3.3-profiller.md)

### Soru 1 — Tabanda 4, profilde 2 ayar var. Profil açıkken kaç ayar okunur, değerler nereden gelir?

**Kısa cevap:** **Dört ayar** okunur. İkisi profil dosyasından, ikisi tabandan gelir —
**profil dosyası tabanın üzerine biner, yerine geçmez.**

**Ayrıntı:**

`application.properties` (taban):

```properties
siparis.magaza-adi=varsayilan-magaza
siparis.zaman-asimi=3000
siparis.bekleme=45s
siparis.sunucular[0]=a.sirket.com
```

`application-uretim.properties` (yalnızca fark):

```properties
siparis.magaza-adi=uretim-magazasi
siparis.zaman-asimi=1000
```

Ölçülen çıktı, `uretim` profili açıkken:

```
### AYAR = SiparisAyarlari[magazaAdi=uretim-magazasi, zamanAsimi=1000,
                           bekleme=PT45S, sunucular=[a.sirket.com]]
```

| Ayar | Değer | Nereden |
|---|---|---|
| `magazaAdi` | `uretim-magazasi` | Profil dosyası (ezdi) |
| `zamanAsimi` | `1000` | Profil dosyası (ezdi) |
| `bekleme` | `PT45S` | **Taban** (profilde yok, değişmedi) |
| `sunucular` | `[a.sirket.com]` | **Taban** (profilde yok, değişmedi) |

Son iki satır bu konunun tamamıdır: profil dosyasının **yazmadığı** anahtarlar tabandan
gelmeye devam eder.

> 📌 **Sık yapılan hata:** Kaybolacaklar diye profil dosyasına ayarların tamamını kopyalamak.
> O an çalışır; tabana yeni bir ayar eklendiği gün ortamlar sessizce ayrışır. **Profil
> dosyasına yalnızca farkı yaz.**

🔗 Konu: [3.3 §1 Profil dosyaları](3.3-profiller.md) · [3.2 §1 `record` ile bağlama](3.2-configuration-properties.md)

---

### Soru 2 — Profil adını yanlış yazdın. Ne olur, nasıl fark edersin?

**Kısa cevap:** **Hiçbir hata çıkmaz.** Spring `application-<yanlisAd>.properties` dosyasını
arar, bulamaz, sessizce geçer; uygulama taban ayarlarıyla açılır. Fark etmenin yolu **açılış
günlüğünün ikinci satırını okumaktır.**

**Ayrıntı:**

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=uretmi
```

Sonuç: hata yok, ama `magazaAdi=varsayilan-magaza` — profil hiç uygulanmadı.

Günlük satırı iki farklı arızayı **birbirinden ayırır**:

| Günlükte gördüğün | Teşhis | Düzeltme |
|---|---|---|
| `No active profile set, falling back to 1 default profile: "default"` | Profil değeri uygulamaya **hiç ulaşmadı** | Değişken/argüman adını kontrol et (`SPRING_PROFILES_ACTIVE`) |
| `The following 1 profile is active: "uretmi"` | Değer ulaştı, **dosya karşılığı yok** | Profil adının yazımını düzelt |

İki savunma birlikte kurulur:

1. **Her dağıtımda** bu satırı oku — beklediğin profil adı yazıyor mu?
2. Kritik ortamlarda [3.2](3.2-configuration-properties.md)'deki ayar doğrulamasını kullan:
   profil uygulanmadığında zorunlu bir ayar eksik kalır ve uygulama **açılmaz**. Sessiz hata,
   gürültülü hataya dönüşür.

> 📌 **Sık yapılan hata:** Dağıtım betiğine bakıp "profil verilmiş, sorun başka yerde" demek.
> Betik **niyeti** gösterir; günlük **sonucu**. Yanlış yazılmış bir değişken adında betik
> kusursuz görünür.

🔗 Konu: [3.3 §6 Hangi profillerin açık olduğunu görmek](3.3-profiller.md) · [3.3 §7 Hata 1](3.3-profiller.md)

---

### Soru 3 — `spring.profiles.active=uretim` satırını `application.properties`'e yazmak neden kötü fikir?

**Kısa cevap:** Uygulamayı üretim profiline **varsayılan olarak kilitlemiş** olursun: ortam
bilgisi kaynak koda sızar, yapı çıktısı ortama bağlanır ve dışarıdan ezmek isteyen herkesin
bu satırın varlığını bilmesi gerekir.

**Ayrıntı:**

Üç somut sonuç:

| Sonuç | Neye benziyor |
|---|---|
| Yerelde çalıştıran herkes üretim profiliyle açılır | Geliştirici farkında olmadan üretim ayarlarıyla çalışır |
| Ortam bilgisi kaynak kodda | Yeni ortam eklemek dosya değiştirmek, derlemek, dağıtmak demek |
| Ezmek mümkün ama görünmez | `--spring.profiles.active=test` işe yarar, ama neyi ezdiğini bilmeyen kişi şaşırır |

Bu, konunun başındaki `if (System.getenv("ORTAM").equals("uretim"))` kodunun daha zararsız
görünen hâlidir: derdi aynı — **ortam bilgisi yapı çıktısının içinde.**

✅ **Doğrusu:** Profili dışarıdan ver.

```bash
--spring.profiles.active=uretim          # dağıtım betiği
SPRING_PROFILES_ACTIVE=uretim            # Docker / Kubernetes
```

**Tek istisna:** yalnızca geliştirme kolaylığı için `yerel` gibi zararsız bir varsayılan
yazmak. Kaynak kodda bir **üretim** ortamının adını görüyorsan dur ve düşün.

> 📌 **Sık yapılan hata:** "Nasılsa üretimde ortam değişkeniyle eziyoruz" diyerek satırı
> bırakmak. O ezme bir gün unutulur ya da adı yanlış yazılır — ve o gün varsayılanın üretim
> olması, sessiz hatayı en kötü yöne çevirir.

🔗 Konu: [3.3 §2 Profil nasıl açılır](3.3-profiller.md) · [3.3 §7 Hata 3](3.3-profiller.md)

---

### Soru 4 — `--spring.profiles.active=uretim,izleme` ve iki dosyada da `siparis.zaman-asimi` var. Hangisi kazanır?

**Kısa cevap:** **`izleme`** — çakışmada **sondaki profil** kazanır.

**Ayrıntı:**

Profiller yazdığın sırada, soldan sağa üst üste bindirilir; en sağdaki en üstte kalır.

| Komut | Kazanan | Neden |
|---|---|---|
| `--spring.profiles.active=uretim,izleme` | `application-izleme.properties` | `izleme` sonda |
| `--spring.profiles.active=izleme,uretim` | `application-uretim.properties` | `uretim` sonda |

Yalnızca **çakışan** anahtar için geçerlidir. Çakışmayan anahtarlarda birikme sürer: her iki
profil dosyasının kendine ait ayarları da okunur, ve hiçbirinin yazmadığı anahtarlar tabandan
gelir (Soru 1).

Pratikte kural şudur: **daha özel olan profili sona yaz.** `uretim,izleme` sıralaması,
"izleme profili üretim ayarlarının üstüne ince ayar yapar" demektir. Sırayı ters çevirmek
sessizce başka bir uygulama üretir.

> 📌 **Sık yapılan hata:** Sırayı önemsiz sanıp iki profil adını alfabetik ya da rastgele
> yazmak. Kazanan alfabetik sıra değil, **senin yazdığın sıradır.**

🔗 Konu: [3.3 §2 Profil nasıl açılır](3.3-profiller.md) · [3.1 §2 Öncelik sırası](3.1-oncelik-sirasi.md)

---

### Soru 5 — Bir uç noktayı üretimde kapatmak için `@Profile("!uretim")` yeterli bir güvenlik önlemi midir?

**Kısa cevap:** **Hayır.** `@Profile` bean seçim aracıdır; korumanın tamamı "profil doğru
ayarlanmış olsun" varsayımına dayanır ve yanlış profil adı **hata bile vermez**.

**Ayrıntı:**

Zincir tek bir halkadan kırılır:

```
--spring.profiles.active=uretmi        ← yazım hatası, hata yok
        ↓
"uretim" profili açık DEĞİL
        ↓
@Profile("!uretim") koşulu SAĞLANIR
        ↓
Uç nokta üretimde AÇIK
```

Aynı sonuç şu üç yoldan da gelir: profil değişkeni hiç geçmemiştir, adı yanlış yazılmıştır,
ya da biri dağıtımda profili kaldırmıştır. Üçünde de sessiz.

| | `@Profile` ile "kapatmak" | Güvenlik katmanıyla kapatmak |
|---|---|---|
| Neye dayanır | Yapılandırmanın doğru olmasına | Kimlik ve yetkiye |
| Yanlış yapılandırmada | Koruma **kalkar** | Koruma durur |
| Yanlış yapılandırma fark edilir mi | Hayır, sessiz | — |

`@Profile`'ın doğru işi, aynı arayüzün ortama göre farklı **uygulamasını** seçmektir: üretimde
gerçek SMS gönderen bean, diğer ortamlarda ekrana yazan bean. "Bunu kimse görmesin" işi
güvenlik katmanının işidir (**401**).

> 📌 **Sık yapılan hata:** Yapılandırmayla kapatılan bir şeyin kapalı kaldığını varsaymak.
> Yapılandırmayla kapatılan şey, yapılandırmayla açılır — üstelik kimseye haber vermeden.

🔗 Konu: [3.3 §4 Profile bağlı bean](3.3-profiller.md) · [3.3 §7 Hata 1](3.3-profiller.md)

---

⬅️ [Bölüme dön](3.1-oncelik-sirasi.md) · 📄 [3.2](3.2-configuration-properties.md) · 📄 [3.3](3.3-profiller.md)
