# 02 · Anotasyon haritası — Kendini kontrol cevapları

> Bu dosya [2.1](2.1-stereotype.md) – [2.4](2.4-starterlar.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:**
[2.1](#21-stereotypelar-service-gerçekten-ne-yapar) ·
[2.2](#22-configuration-ve-gizli-proxy) ·
[2.3](#23-auto-configurationı-teşhis-etmek) ·
[2.4](#24-starterlar-ve-spring-boot-4-modül-bölünmesi)

---

## 2.1 Stereotype'lar: @Service gerçekten ne yapar

📄 Sorular: [`2.1-stereotype.md`](2.1-stereotype.md)

### Soru 1 — "@Service ile @Component arasındaki fark nedir?" sorusuna iki cümlelik tam cevabı yaz

**Kısa cevap:** "`@Service`, üzerinde `@Component` taşıyan bir meta-annotation'dır; component
scan ikisini de aynı şekilde bulur. Çalışma zamanında **hiçbir teknik farkı yoktur** —
farkı okuyan insana ve `@WebMvcTest` gibi stereotype'a bakan araçlara verdiği bilgidir."

**Ayrıntı:**

Görüşmede eksik kalan taraf, cevabın ikinci yarısıdır: "hangi stereotype davranış
değiştirir?" Tablo bunu bir bakışta veriyor:

| Anotasyon | Bean yapar | Çalışma zamanı ek davranışı |
|---|---|---|
| `@Component` | ✅ | Yok |
| `@Service` | ✅ | **Yok** |
| `@Repository` | ✅ | Exception translation |
| `@Controller` | ✅ | Web katmanı bu sınıfta `@RequestMapping` arar |
| `@RestController` | ✅ | `@Controller` + `@ResponseBody` |
| `@Configuration` | ✅ | `@Bean` metotları için CGLIB proxy'si (2.2) |

Yani `@Service` → `@Component` değişimi çalışma zamanında **ölçülebilir hiçbir şeyi
değiştirmez**: bean yine oluşur, yine singleton'dır, injection aynı çalışır.

> 📌 **Sık yapılan hata:** "İş mantığı için `@Service`, veri erişimi için `@Repository`"
> deyip durmak. Bu cümle doğrudur ama sorunun yarısını cevaplar; sorulan asıl şey
> **davranış farkı olup olmadığıdır.** "Teknik farkı yok" demeden cevap tamamlanmaz.

🔗 Konu: [2.1 §1–§2](2.1-stereotype.md) · [2.2 proxy](2.2-configuration.md)

---

### Soru 2 — `@Repository`'nin yaptığı çeviriyi bir örnekle anlat. Bu çeviri neden 201'deki transaction konusunu ilgilendiriyor?

**Kısa cevap:** `@Repository`, sınıfın etrafına bir wrapper koyar ve kütüphaneye özgü
exception'ları `org.springframework.dao.DataAccessException` ailesine çevirir. Transaction'ı
ilgilendirmesinin sebebi **checked/unchecked ayrımı**: `@Transactional` checked
exception'larda geri alma yapmaz, çevrilen exception'ların hepsi **unchecked**'tir.

**Ayrıntı:**

Aynı kısıt ihlali, iki anotasyonda iki farklı exception olarak servis katmanına ulaşır:

| Depo sınıfının anotasyonu | Servise ulaşan exception | Checked mi | `@Transactional` geri alır mı |
|---|---|---|---|
| `@Component` | `java.sql.SQLException` | ✅ checked | ❌ **hayır** |
| `@Repository` | `DataIntegrityViolationException` | ❌ unchecked | ✅ evet |

Çeviri zinciri:

```
SQLException (JDBC)                     ┐
ConstraintViolationException (Hibernate)├──→ DataIntegrityViolationException
EntityNotFoundException (JPA)           ┘    (org.springframework.dao)
```

Sonucun pratikteki karşılığı: `@Repository` yazmayı unuttuğun bir depoda, hata mesajı
değişmez ama **veri geri alınmaz**. Yani anotasyon eksikliği kendini bir exception olarak
değil, **bozuk veri** olarak gösterir.

> 📌 **Sık yapılan hata:** Çeviriyi "daha okunaklı exception" sanmak. Asıl kazanç servis
> katmanının hangi veri erişim kütüphanesini kullandığını bilmek zorunda kalmaması —
> ve geri alma davranışının değişmemesi.

🔗 Konu: [2.1 §2](2.1-stereotype.md)

---

### Soru 3 — Kontrolcüne `@Component` yazdın. Uygulama açılıyor mu, uç nokta çalışıyor mu, günlükte uyarı var mı?

**Kısa cevap:** Açılıyor · uç nokta çalışmıyor (404) · günlükte uyarı **yok**.

**Ayrıntı:**

Pratikteki üç kutucuğun cevabı sırasıyla:

| Soru | Cevap | Sebep |
|---|---|---|
| Uygulama açılıyor mu | ✅ evet | `@Component` de bir stereotype'tır; sınıf bean oldu |
| Uç nokta çalışıyor mu | ❌ hayır | Web katmanı `@RequestMapping` metotlarını yalnızca `@Controller` ailesinde arar |
| Günlükte uyarı var mı | ❌ yok | Spring "eşlenmemiş kontrolcü" diye bir uyarı basmaz |

Ölçüm:

```
$ curl -i http://localhost:8080/siparisler
HTTP/1.1 404
{"timestamp":"...","status":404,"error":"Not Found","path":"/siparisler"}
```

Üç cevabın birleşimi **sessiz hatanın tanımıdır**: hata yok, uyarı yok, sadece iş görmüyor.
Doğrusu `@RestController`.

> 📌 **Sık yapılan hata:** "Bean oluştuysa çalışıyordur" varsayımı. Bean olmak yetmez,
> **doğru türden bean olmak gerekir.** Kontrolcü bean listesinde görünür ama uç nokta
> listesinde görünmez.

🔗 Konu: [2.1 §5 Hata 1](2.1-stereotype.md)

---

### Soru 4 — Uç noktan 404 veriyor. Anotasyon dışında bakman gereken ikinci sebep nedir?

**Kısa cevap:** **Sınıfın taranmayan bir pakette olması.** Tarama
`@SpringBootApplication` sınıfının paketinden başlar ve yalnızca aşağı iner; kardeş paket
taranmaz.

**Ayrıntı:**

Pratiğin 5. adımında kontrolcüyü `tr.loncademy.siparis.web`'den `tr.loncademy.web`'e
taşıdığında anotasyon doğru olduğu hâlde yine 404 aldın:

```
tr.loncademy
├── siparis            ← @SpringBootApplication burada, tarama kökü burası
│   └── web            ✅ taranır
└── web                ❌ kardeş paket — taranmaz
```

404'ün üç ayrı sebebini ayırt etme sırası:

| Sıra | Bakılacak yer | Belirti aynıysa nasıl ayrılır |
|---|---|---|
| 1 | Sınıfın anotasyonu `@Controller` ailesinden mi | `@RestController` yapıp dene |
| 2 | Sınıf, ana sınıfın paketinin **altında** mı | Sınıfı doğru pakete taşıyıp dene |
| 3 | Adres/HTTP metodu gerçekten eşleşiyor mu | Yanlış metotta 404 değil 405 alırsın |

> 📌 **Sık yapılan hata:** Paketi doğrulamadan `scanBasePackages` eklemek. Bu satır
> genelde **çözüm değil belirtidir** — paket düzeni bozuk demektir. Önce ana sınıfın
> nerede durduğuna bak.

🔗 Konu: [2.1 §4](2.1-stereotype.md)

---

### Soru 5 — İki 404'ün (`@Component` ile ve `@ResponseBody`'siz `@Controller` ile) sebebi nasıl farklı, hangisini nasıl ayırt edersin?

**Kısa cevap:** `@Component`'te sınıfa **hiç bakılmadı**, uç nokta eşlenmedi. `@Controller`'da
uç nokta eşlendi, metot çalıştı, ama dönen `String` **view name** sayıldı ve o görünüm
bulunamadı. Ayırt etme yolu: metoda `@ResponseBody` ekle — ikincisi 200 döner, birincisi
hâlâ 404 verir.

**Ayrıntı:**

| | `@Component` ile | `@Controller` ile (`@ResponseBody` yok) |
|---|---|---|
| Sınıf bean mi | ✅ evet | ✅ evet |
| Uç nokta eşlendi mi | ❌ hayır — sınıfa hiç bakılmadı | ✅ evet |
| Metot çalıştı mı | ❌ hayır | ✅ evet |
| 404'ün sebebi | Böyle bir adres yok | `"liste"` adlı **view** bulunamadı |
| `@ResponseBody` eklenince | Değişmez, yine **404** | **200**, gövdede `liste` |

Ayırt eden hamle:

```java
@GetMapping("/siparisler")
@ResponseBody                          // ← "bu bir view name değil, gövdenin kendisi"
public String hepsi() { return "liste"; }
```

Ve `@RestController`'ın yaptığı iş tam olarak budur: `@Controller` + `@ResponseBody`
birleşimi.

> 📌 **Sık yapılan hata:** Aynı HTTP kodunu aynı sebep sanmak. 404 bir **belirti**, sebep
> değil; teşhis alışkanlığın "eşleme oldu mu?" sorusunu ayrıca sormalı. `@ResponseBody`
> burada bir düzeltme değil, bir **ölçüm aletidir**: cevabı değiştiriyorsa sınıf
> okunuyordu demektir.

🔗 Konu: [2.1 Pratik adım 4](2.1-stereotype.md) · [2.1 §5](2.1-stereotype.md)

---

## 2.2 @Configuration ve gizli proxy

📄 Sorular: [`2.2-configuration.md`](2.2-configuration.md)

### Soru 1 — Full mode'da `kaynak()` metodunu iki kere çağırınca neden aynı nesne geliyor? Mekanizmayı anlat

**Kısa cevap:** Spring, `@Configuration` sınıfının çalışma zamanında bir **CGLIB alt
sınıfını** üretir; bu alt sınıf her `@Bean` metodunu ezer ve gövdeyi çalıştırmadan önce
container'da o bean var mı diye bakar. İlk çağrıda gövde çalışır ve sonuç container'a konur,
sonraki her çağrı container'dan döner.

**Ayrıntı:**

Ezilen metodun kavramsal karşılığı:

```java
@Override
public Kaynak kaynak() {
    if (containerDaVar("kaynak")) return containerDanAl("kaynak"); // ← 2. çağrı buraya düşer
    Kaynak k = super.kaynak();                                     // ← senin gövden, 1 kere
    containerAKoy("kaynak", k);
    return k;
}
```

Ölçülen çıktı bunu üç modte yan yana gösteriyor:

```
### full:1,1
### proxysiz:6,7
### lite:3,4
### tuketici1 sınıfı: tr.loncademy.siparis.ProxyDenemesi$FullMode$$SpringCGLIB$$0
### tuketici2 sınıfı: tr.loncademy.siparis.ProxyDenemesi$ProxysizMode
```

`1,1` = tek nesne (proxy devrede) · `6,7` ve `3,4` = iki ayrı `new Kaynak()`.
Kanıt son iki satırda: full mode'daki sınıf adında `$$SpringCGLIB$$0` eki var, proxysiz mod'da yok.

> 📌 **Sık yapılan hata:** Bunu "Spring metot sonuçlarını önbelleğe alıyor" diye
> özetlemek. Ezme yalnızca **`@Bean` işaretli örnek metotlar** için geçerlidir; aynı
> sınıftaki sıradan bir metodun gövdesi her çağrıda çalışır, `static @Bean` de proxy'den
> geçmez.

🔗 Konu: [2.2 §1–§2](2.2-configuration.md)

---

### Soru 2 — `proxyBeanMethods = false` yazmanın güvenli olduğu iki şartı say

**Kısa cevap:** (1) O sınıftaki `@Bean` metotları **birbirini çağırmıyor**, ve (2) açılış
süresi **ölçülebilir biçimde** önemli (çok sayıda yapılandırma sınıfı olan büyük uygulama).
İkisi birden doğru değilse düz `@Configuration` yaz.

**Ayrıntı:**

| Şart | Sağlanmazsa ne olur | Nasıl anlarsın |
|---|---|---|
| Metotlar birbirini çağırmıyor | Her çağrı yeni nesne kurar; singleton'lık sessizce kaybolur | Sınıfta bir `@Bean` metodunun gövdesinde başka bir `@Bean` metodunun adı geçiyor mu, bak |
| Açılış süresi ölçülebilir biçimde önemli | Hiçbir şey kazanmadan risk alırsın | Açılış süresini ölçmeden bunu iddia etme |

Kazanç ve bedelin karşılaştırması:

| | Full mode | Proxysiz mod (`proxyBeanMethods = false`) |
|---|---|---|
| `@Bean` metodunu çağırmak | Container'dan alır (`1,1`) | Yeni nesne kurar (`6,7`) |
| Açılış maliyeti | Alt sınıf üretimi — küçük ama var | Yok |
| Sınıf kısıtı | `final` olamaz | Yok |

`final` olabilmek bir **şart değil, sonuçtur** — proxy üretilmediği için sınıf final
olabilir.

> 📌 **Sık yapılan hata:** İkinci şartı atlayıp yalnızca birinciye bakmak. "Metotlar
> birbirini çağırmıyor, o hâlde `false` yazayım" cümlesi eksiktir: kazanacağın şey
> ölçülemiyorsa yazmaya değmez.

🔗 Konu: [2.2 §3](2.2-configuration.md)

---

### Soru 3 — `@Bean` metodunu `@Service` sınıfına koydun. Hata alır mısın? Ne kaybedersin?

**Kısa cevap:** **Hata almazsın.** Kod derlenir, uygulama açılır, bean bile üretilir.
Kaybettiğin şey **singleton garantisidir**: o metodu sınıf içinden çağıran her yer yeni bir
nesne alır.

**Ayrıntı:**

```java
@Service                              // ← @Configuration değil: lite mode
public class SiparisServisi {
    @Bean
    public ObjectMapper ozelMapper() { return new ObjectMapper(); }
}
```

| | Ne bekliyorsun | Ne oluyor |
|---|---|---|
| Derleme | ✅ geçer | ✅ geçer |
| Açılış | ✅ açılır | ✅ açılır |
| Bean üretimi | ✅ üretilir | ✅ üretilir |
| Metodu çağırınca | Container'dan gelmesini | **Yeni nesne** kuruluyor (ölçüm: `lite:3,4`) |
| Uyarı | Bir uyarı bekliyorsun | **Hiçbir uyarı yok** |

`ObjectMapper` gibi kurulumu pahalı bir nesnede bu, sessiz bir bellek ve başarım sorunudur;
"`ObjectMapper`'ım her yerde farklı davranıyor" kalıbının kaynağı budur.

> 📌 **Sık yapılan hata:** `@Service`'in de bir `@Component` olmasından yola çıkıp proxy'nin
> de üretileceğini varsaymak. Tarama açısından doğru, proxy açısından yanlış: **alt sınıf
> yalnızca `@Configuration` için üretilir.** Kural: `@Bean` metotları `@Configuration`
> sınıflarında durur, istisnası yok.

🔗 Konu: [2.2 §4](2.2-configuration.md)

---

### Soru 4 — `@Configuration class '...' may not be final` hatasının iki farklı çözümü nedir?

**Kısa cevap:** (1) `final` değiştiricisini kaldır. (2) Metotlar birbirini çağırmıyorsa
`@Configuration(proxyBeanMethods = false)` yaz — proxy üretilmediği için sınıf `final`
kalabilir.

**Ayrıntı:**

Hata tam olarak şöyle gelir:

```
BeanDefinitionParsingException: Configuration problem:
@Configuration class 'Yapilandirma' may not be final. Remove the final modifier to continue.
Offending resource: class path resource [tr/loncademy/siparis/Yapilandirma.class]
```

| Çözüm | Ne kazanırsın | Ne kaybedersin |
|---|---|---|
| `final`'ı kaldır | Proxy üretilir, singleton'lık korunur | Sınıf artık final değil (pratikte bedeli yok) |
| `proxyBeanMethods = false` | Sınıf final kalır, açılışta alt sınıf üretilmez | `@Bean` metotları birbirini çağırıyorsa **singleton'lık bozulur** |

Ve hatayı ararken bilmen gereken ikinci şey: bu hatanın **`APPLICATION FAILED TO START`
kutusu yoktur**, düz bir stack trace olarak gelir. Kutu yoksa stack trace'in **en üstteki**
`Caused by` satırını oku.

> 📌 **Sık yapılan hata:** Hatayı `@Configuration` yerine `@Service` yazarak susturmak.
> Mesaj gerçekten kaybolur — çünkü artık lite mode'dasin — ama singleton'lığı sessizce feda
> etmiş olursun. Kotlin'de bu hata daha sık başa gelir: sınıflar varsayılan olarak
> `final`'dır (`open` yaz ya da all-open eklentisini kullan).

🔗 Konu: [2.2 §5 Hata 1](2.2-configuration.md)

---

### Soru 5 — Spring Boot'un kendi auto-configuration sınıfları `proxyBeanMethods = false` kullanıyor. Sen de kullanmalı mısın? Neden?

**Kısa cevap:** Hayır — varsayılanın düz `@Configuration` olsun. Boot'un şartları
seninkinden farklı: yüzlerce configuration sınıfı var ve hiçbiri diğerinin `@Bean` metodunu
çağırmıyor.

**Ayrıntı:**

| | Spring Boot'un auto-configuration'ları | Senin configuration sınıfın |
|---|---|---|
| Sınıf sayısı | Yüzlerce | Genelde birkaç tane |
| `@Bean` metotları birbirini çağırıyor mu | Hayır (tasarım gereği) | **Sıklıkla evet** (ortak ayar nesnesi vb.) |
| Proxy üretiminden kaçınmanın kazancı | Ölçülebilir açılış süresi | Ölçülemeyecek kadar küçük |
| `false` yazmanın riski | Yok | **Singleton'lık sessizce bozulur** |

Somut örnek: aynı `ayarlar()` bean'ini iki `RestClient`'te kullanan sınıfa
`proxyBeanMethods = false` yazarsan iki ayrı `HttpAyarlari` nesnesi kurulur ve hiçbir hata
almazsın — "Kalıp 2: aynı ayar nesnesinden iki tane kurulmuş" tam olarak budur.

> 📌 **Sık yapılan hata:** Framework kaynak kodunu uygulama kodu için örnek almak.
> Kaynak koda bakarken sorulacak soru "ne yazmışlar?" değil, **"hangi soruna cevap
> veriyor ve o sorun bende var mı?"**

🔗 Konu: [2.2 §3](2.2-configuration.md)

---

## 2.3 Auto-configuration'ı teşhis etmek

📄 Sorular: [`2.3-auto-configuration.md`](2.3-auto-configuration.md)

### Soru 1 — Kendi `ObjectMapper` bean'ini tanımladığında Spring'inki neden devreye girmiyor? Mekanizmanın adı nedir?

**Kısa cevap:** Mekanizmanın adı **`@ConditionalOnMissingBean`**. Spring'in `ObjectMapper`
bean'i "bu türden bean yoksa kur" koşulunu taşır; sen tanımlayınca koşul sağlanmaz ve o
bean **hiç kurulmaz**.

**Ayrıntı:**

Sıra garantilidir, yarış yoktur:

```
1. Component scan + senin @Bean tanımların kaydedilir
2. Auto-configuration DEĞERLENDİRİLİR      ← koşullar burada bakılır
3. @ConditionalOnMissingBean: "zaten var" → Spring'inki elenir
```

Pratikte kendi gözünle gördüğün kanıt:

```
>>> BENİM ObjectMapper'ım kuruldu
```

ve `--debug` raporunda Spring'in bean'i artık **Negative matches** altında,
`@ConditionalOnMissingBean ... found beans` benzeri bir satırla listeleniyor.

> 📌 **Sık yapılan hata:** Bunu "bean definition'ın üzerine yazma (overriding)" sanmak. Üzerine
> yazma farklı bir olaydır ve çakışma üretir; burada **çakışma bile oluşmaz**, rakip bean
> hiç doğmaz. Ayrıca Spring Boot 4'te `ObjectMapper` Jackson 3'ten gelir:
> `import tools.jackson.databind.ObjectMapper;`

🔗 Konu: [2.3 §1](2.3-auto-configuration.md) · [2.3 Pratik adım 4](2.3-auto-configuration.md)

---

### Soru 2 — Bir özellik çalışmıyor. Raporun hangi bölümüne bakarsın ve neden diğerine değil?

**Kısa cevap:** **Negative matches.** Çünkü çalışmayan şeyin sebebi orada **tek satırda**
yazılıdır; Positive matches yalnızca devreye girenleri sayar ve orada olmayan bir şeyi
aramak zaman kaybıdır.

**Ayrıntı:**

| Bölüm | Ne yazar | Ne zaman bakarsın |
|---|---|---|
| Positive matches | Devreye girenler ve hangi koşul yüzünden | "Bu bean nereden geldi?" |
| **Negative matches** | Devreye **girmeyenler** ve **neden** | ⭐ "Neden çalışmıyor?" |
| Exclusions | **Yalnızca elle dışladıkların** | Dışlama tuttu mu |
| Unconditional classes | Koşulsuz olanlar | Nadiren |

Negative matches'ta okuyacağın satır zaten cevabı veriyor:

```
AopAutoConfiguration.AspectJAutoProxyingConfiguration:
   Did not match:
      - @ConditionalOnClass did not find required class 'org.aspectj.weaver.Advice'
```

Sebep üç kalıptan biridir: `did not find required class ...` (bağımlılık eksik) ·
`did not find any beans ...` (beklenen bean yok) · `did not find property ...` (ayar yok).

> 📌 **Sık yapılan hata:** Exclusions bölümünü "devreye girmeyenlerin listesi" sanmak.
> Orada yalnızca **senin elle yazdığın dışlamalar** vardır; koşulu tutmayanlar Negative
> matches'tadır.

🔗 Konu: [2.3 §2](2.3-auto-configuration.md)

---

### Soru 3 — `--debug` bayrağı olmadan aynı bilgiyi nasıl alırdın? (İpucu: alamazsın — neden?)

**Kısa cevap:** Alamazsın. **CONDITIONS EVALUATION REPORT bir günlük satırı değildir;**
açılış sırasında üretilen ayrı bir çıktıdır ve onu bastıran tek şey `--debug` bayrağıdır.
Günlük seviyesini yükseltmek ekranı doldurur ama raporu getirmez.

**Ayrıntı:**

| Ne denersin | Sonuç |
|---|---|
| `logging.level.root=DEBUG` | Çerçevenin DEBUG satırları akar; **rapor yok** |
| Uygulamayı normal çalıştırıp günlüğü okumak | Koşulların sonucu hiç basılmaz |
| `--debug` | ⭐ Rapor basılır — Positive / Negative / Exclusions / Unconditional |

Doğru komutlar:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug
java -jar hedef/uygulama.jar --debug
```

`--debug` günlük seviyesini de değiştirir, ama **asıl işi raporu basmaktır** — bu ikisini
ayırt etmek, "DEBUG açtım ama rapor yok" şaşkınlığını bitirir.

> 📌 **Sık yapılan hata:** Raporu tarayarak okumak. Boş bir uygulamada bile ~61 koşul
> değerlendirmesi var; dosyaya yönlendirip **aramak** gerekir:
> ```bash
> ./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug > acilis.log 2>&1
> sed -n '/Negative matches:/,/Exclusions:/p' acilis.log | less
> ```

🔗 Konu: [2.3 §2–§3](2.3-auto-configuration.md)

---

### Soru 4 — `@SpringBootApplication(exclude = ...)` kullanmanın riski nedir, hangi iki yolu önce denemelisin?

**Kısa cevap:** Risk: bir yapılandırma sınıfı genelde **birden çok bean** kurar; dışladığında
hepsini birden kaybedersin ve eksiği aylar sonra fark edersin. Önce denenecek iki yol:
**(1) kendi bean'ini tanımla**, (2) **ilgili ayarla kapat**.

**Ayrıntı:**

| Sıra | Yol | Etki alanı | Ne zaman |
|---|---|---|---|
| 1 | Kendi `@Bean`'ini tanımla | Yalnızca o bean | ⭐ Çoğu durumda doğru cevap |
| 2 | Ayarla kapat (`spring.jpa.open-in-view=false`) | Yalnızca o davranış | Rapordaki `@ConditionalOnProperty` satırı ayarın adını söyler |
| 3 | `exclude` / `spring.autoconfigure.exclude` | **Sınıfın kurduğu her şey** | Son çare |

İkinci risk: liste büyüdükçe asıl sorun gizlenir.

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
                                 HibernateJpaAutoConfiguration.class,
                                 JpaRepositoriesAutoConfiguration.class})
```

Böyle bir liste, `pom.xml`'de kullanmadığın bir starter durduğunun işaretidir. Bağımlılığı
kaldır, dışlamaya gerek kalmasın.

> 📌 **Sık yapılan hata:** Dışlamayı "temiz çözüm" sanmak. Kaba bir alettir: hedeflediğin
> bean'in yanında haberin olmayan bean'leri de götürür. Bir de Spring Boot 4'te
> `spring.autoconfigure.exclude` satırındaki **yanlış paket adı hata vermez** (2.4) —
> dışlamanın tuttuğunu raporun Exclusions bölümünden doğrula.

🔗 Konu: [2.3 §4](2.3-auto-configuration.md) · [2.3 §5 Hata 3](2.3-auto-configuration.md)

---

### Soru 5 — `Failed to configure a DataSource: 'url' attribute is not specified` hatası tam olarak neyi anlatıyor ve üç çözümü nedir?

**Kısa cevap:** JDBC classpath'te olduğu için auto-configuration bir `DataSource`
kurmayı **denedi**, ne adres ne de gömülü veritabanı bulabildi. Üç çözüm:
`spring.datasource.url` ver · gömülü veritabanı (H2/HSQL/Derby) ekle · o
auto-configuration'ı dışla.

**Ayrıntı:**

Mesajın kendisi teşhisi zaten yazıyor:

```
Description:
Failed to configure a DataSource: 'url' attribute is not specified and no embedded
datasource could be configured.

Reason: Failed to determine a suitable driver class

Action:
Consider the following:
	If you want an embedded database (H2, HSQL or Derby), please put it on the classpath.
	...
```

| Çözüm | Ne zaman doğru |
|---|---|
| `spring.datasource.url` (+ kullanıcı/parola) | Gerçek bir veritabanına bağlanacaksan |
| Gömülü veritabanı bağımlılığı | Geliştirme/test aşamasında |
| Auto-configuration'ı dışla | Veritabanını hiç kullanmıyorsan — ama asıl doğrusu **bağımlılığı kaldırmak** |

Bu hata, "sihir" duygusunun bittiği yerin tipik örneğidir: Spring bozuk değil, classpath'te
JDBC gördüğü için üzerine düşeni yapmış.

> 📌 **Sık yapılan hata:** `Action` bölümünü okumadan aramaya gitmek. Bu hata kutulu gelen
> (`APPLICATION FAILED TO START`) hatalardandır ve kutunun içinde hem sebep hem seçenekler
> yazılıdır.

🔗 Konu: [2.3 §5 Hata 2](2.3-auto-configuration.md)

---

## 2.4 Starter'lar ve Spring Boot 4 modül bölünmesi

📄 Sorular: [`2.4-starterlar.md`](2.4-starterlar.md)

### Soru 1 — Bir starter jar'ını açsan içinde ne bulursun, ne bulamazsın?

**Kısa cevap:** **Bulursun:** başka bağımlılıkları listeleyen bir `pom.xml`.
**Bulamazsın:** tek bir sınıf bile — ne kod, ne auto-configuration.

**Ayrıntı:**

`spring-boot-starter-webmvc` yazdığında gelen ağaç:

```
spring-boot-starter-webmvc
├── spring-boot-starter          (çekirdek: container, günlükleme, yapılandırma)
├── spring-boot-starter-json     (Jackson 3 — groupId: tools.jackson)
├── spring-boot-starter-tomcat   (embedded server)
├── spring-web
└── spring-webmvc
```

| Starter yoksa | Starter varsa |
|---|---|
| 15 bağımlılığı elle yazarsın | 1 satır |
| Sürümleri elle uyumlu tutarsın | Üst proje halleder |
| Biri güncellenince diğerini kırar | Uyumlu küme test edilerek yayınlanır |

Kendi projende ölçmenin yolu:

```bash
./mvnw dependency:tree | head -40
./mvnw dependency:list | grep ":compile" | wc -l
```

> 📌 **Sık yapılan hata:** Auto-configuration sınıflarını starter'ın içinde sanmak.
> O sınıflar starter'ın **çektiği** modüllerdedir (örneğin `spring-boot-jdbc`); starter
> yalnızca listeyi tutar.

🔗 Konu: [2.4 §1](2.4-starterlar.md)

---

### Soru 2 — `spring-boot-starter-web` yazan bir Spring Boot 4 projesi çalışır mı? Bu neden iyi haber ve neden tuzak?

**Kısa cevap:** **Çalışır.** Eski ad kaldırılmadı, yalnızca "kullanımdan kaldırıldı"
(deprecated) olarak işaretlendi. İyi haber: yükseltme projeni kırmaz. Tuzak: hiçbir şey
kırılmadığı için ad değişikliğini **fark etmezsin**.

**Ayrıntı:**

Eski starter'ın kendi tanımı zaten söylüyor:

> *"Starter for building web, including RESTful, applications using Spring MVC. Uses Tomcat
> as the default embedded container **(deprecated in favor of spring-boot-starter-webmvc)**"*

| | Eski ad (`-web`) | Yeni ad (`-webmvc`) |
|---|---|---|
| Uygulama açılır mı | ✅ | ✅ |
| Tomcat gelir mi | ✅ | ✅ |
| Hata/uyarı | Yok | Yok |
| Durumu | Kullanımdan kaldırıldı | Güncel |

Kendin doğrula:

```bash
./mvnw dependency:tree -Dincludes=org.springframework.boot:spring-boot-starter-web
```

Değişen diğer adlar: `spring-boot-starter-aop` → **`spring-boot-starter-aspectj`**,
`spring-boot-starter-oauth2-client` → `spring-boot-starter-security-oauth2-client`.

> 📌 **Sık yapılan hata:** "Çalışıyorsa doğrudur" varsayımı. Bu konudaki değişikliklerin
> çoğu **sessizdir**; derleyici de çalışma zamanı da sana bir şey söylemez. `pom.xml`'i
> gözle denetlemek tek yoldur.

🔗 Konu: [2.4 §3](2.4-starterlar.md) · [2.4 Pratik adım 3](2.4-starterlar.md)

---

### Soru 3 — Spring Boot 4'te `spring-boot-starter-test` yazmana neden gerek yok?

**Kısa cevap:** Çünkü test starter'ları artık teknoloji başına ayrıldı
(`spring-boot-starter-<teknoloji>-test`) ve **hepsi `spring-boot-starter-test`'i geçişli
(transitive) olarak getiriyor.**

**Ayrıntı:**

Resmî kural iki cümle: *"All test 'starter' POMs are named
`spring-boot-starter-<technology>-test`."* ve *"Given that all test 'starter' POMs bring
`spring-boot-starter-test` transitively, you don't need to define this starter anymore."*

```xml
<artifactId>spring-boot-starter-webmvc-test</artifactId>       <!-- kontrolcü testleri -->
<artifactId>spring-boot-starter-data-jpa-test</artifactId>     <!-- depo testleri -->
<artifactId>spring-boot-starter-security-test</artifactId>     <!-- @WithMockUser -->
```

| Spring Boot 3 | Spring Boot 4 |
|---|---|
| Tek `spring-boot-starter-test` her şeyi getirirdi | Test ettiğin **her teknoloji** için ayrı starter |
| `@WithMockUser` genel test starter'ından gelirdi | **`spring-boot-starter-security-test`** ister |

En sık başa gelen: `@WithMockUser` ya da `@WithUserDetails` satırında `cannot find symbol`
alırsın; eksik olan `spring-boot-starter-security-test`'tir (uygulama bağımlılığı olan
`spring-boot-starter-security` değil).

> 📌 **Sık yapılan hata:** Genel test starter'ını "her ihtimale karşı" ayrıca yazmak.
> Zararsızdır ama gereksizdir — ve `pom.xml`'e bakan birine projenin hâlâ 3.x düzeninde
> olduğu izlenimini verir.

🔗 Konu: [2.4 §3](2.4-starterlar.md)

---

### Soru 4 — `spring.autoconfigure.exclude` satırına eski paket adını yazdın. Ne olur ve bunu nasıl fark edersin?

**Kısa cevap:** **Hiçbir şey olmaz.** O paketteki sınıf Spring Boot 4'te yok; satır sessizce
yok sayılır, dışlama tutmaz ve bean kurulmaya devam eder. Fark etme yolu: `--debug`
raporunun **Exclusions** bölümüne bakmak — dışlaman orada görünmüyorsa tutmamıştır.

**Ayrıntı:**

```properties
# ❌ Spring Boot 3 adı — sessizce yok sayılır
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

# ✅ Spring Boot 4 adı
spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
```

Fark yalnızca kelime sırasında: `autoconfigure` **öne değil, sona** taşındı.

| Yanlış paket adı nereye yazılırsa | Sonuç |
|---|---|
| Java `import` satırına | ✅ **Derleme hatası** — hemen fark edersin |
| Ayar dosyasına (`.properties`) | ❌ **Sessizlik** — düz metin, derleyici bakmaz |

Pratikte bunu bilerek ürettin: satırı ekledin, uygulama açıldı, ne hata ne uyarı çıktı —
ve `DataSource` yine kurulmaya çalışıldı.

> 📌 **Sık yapılan hata:** Ayarı yazdıktan sonra doğrulamadan devam etmek. Dışlama yazan
> herkesin refleksi şu olmalı: `--debug` → **Exclusions** bölümünde adımı görüyor muyum?

🔗 Konu: [2.4 §6 Hata 3](2.4-starterlar.md) · [2.3 §2](2.3-auto-configuration.md)

---

### Soru 5 — Elindeki bir `pom.xml`'in Spring Boot 3 için mi 4 için mi yazıldığını hangi iki satıra bakarak anlarsın?

**Kısa cevap:** (1) Web starter'ının adı: `spring-boot-starter-web` → 3.x,
`spring-boot-starter-webmvc` → 4.x. (2) Test starter'ı: tek başına
`spring-boot-starter-test` → 3.x, `spring-boot-starter-<teknoloji>-test` → 4.x.

**Ayrıntı:**

| Satır | 3.x işareti | 4.x işareti |
|---|---|---|
| Web | `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| Test | Tek `spring-boot-starter-test` | `spring-boot-starter-webmvc-test`, `...-data-jpa-test` |
| AOP | `spring-boot-starter-aop` | `spring-boot-starter-aspectj` |
| Jackson (dependency:tree'de) | `com.fasterxml.jackson` | `tools.jackson` |

Ayırt etmeyen satırlar da var — bunlara bakıp karar verme:

| Satır | Neden ayırt etmez |
|---|---|
| `spring-boot-maven-plugin` | İki sürümde de var |
| Tomcat starter'ının görünmemesi | İki sürümde de web starter'ının içinden geçişli gelir |

`pom.xml` yanıltıcıysa bağımlılık ağacına in: Jackson'ın groupId'si `tools.jackson` ise
Jackson 3, yani Spring Boot 4.

> 📌 **Sık yapılan hata:** `<version>` etiketine bakıp karar vermek. Starter'a elle sürüm
> yazmak zaten hatadır (üst projenin uyumlu sürümünü ezer) ve çoğu doğru `pom.xml`'de o
> satır **hiç yoktur** — bakılacak yer starter adlarıdır.

🔗 Konu: [2.4 §3](2.4-starterlar.md) · [2.4 §6 Hata 1](2.4-starterlar.md)

---

⬅️ [Bölüme dön](2.1-stereotype.md) · ➡️ [Sonraki bölüm](../03-yapilandirma/3.1-oncelik-sirasi.md)
