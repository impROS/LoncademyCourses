# 01 · Container ve bean'ler — Kendini kontrol cevapları

> Bu dosya [1.1](1.1-container-nedir.md) – [1.5](1.5-scope.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:**
[1.1](#11-container-nesneleri-kim-kuruyor) ·
[1.2](#12-bean-tanımlamanın-iki-yolu) ·
[1.3](#13-dependency-injection-ve-belirsizlik) ·
[1.4](#14-bean-lifecycle) ·
[1.5](#15-scope-ve-scoped-proxy)

---

## 1.1 Container: nesneleri kim kuruyor

📄 Sorular: [`1.1-container-nedir.md`](1.1-container-nedir.md)

### Soru 1 — `getBeanDefinitionCount()` 180 döndürdü. Container'da 180 nesne var mı?

**Kısa cevap:** **Hayır.** 180 **tanım** var. Metodun adı zaten bunu söylüyor:
`getBeanDefinition Count`. Nesne sayısı en az 180'dir, ama üstü açıktır.

**Ayrıntı:**

| | **Bean definition** | **Bean** |
|---|---|---|
| Ne | Tarif: hangi sınıf, hangi ad, hangi scope, hangi bağımlılıklar | Tarife göre kurulmuş nesne |
| Ne zaman oluşur | Açılışta, taramada | Tanım okunup nesne kurulduğunda |
| Kaç tane | Her tanım **bir** | Scope'una göre **bir ya da çok** |

Tarif tektir; ondan kaç tabak çıkacağı scope ayarına bağlıdır. Somut olarak, 180 tanımın
içinde:

- **Singleton (`singleton`) tanımlar** → tanım başına 1 nesne.
- **Prototype tanımlar** → istendiği kadar nesne; container onları saymaz, referans bile tutmaz (1.5).
- **`@Lazy` tanımlar** → sayılır ama **henüz kurulmamış** olabilir; bu durumda nesnesi *yok*.

Yani sayı iki yönde birden yanıltır: bazı tanımların karşılığı sıfır nesnedir, bazılarının
onlarca.

Kendi sınıfını ekleyip ölçtüğünde sayının **tam 1 arttığını** görürsün — bir sınıf, bir
tarif. Proxy'lenme sayıyı değiştirmez, çünkü proxy yeni bir tarif değil, aynı tarifin
nesnesinin sarmalanmış hâlidir.

> 📌 **Sık yapılan hata:** Bu sayıyı bir "bellek göstergesi" gibi okumak. Ölçtüğün şey
> container'ın **kaç tarif tanıdığı**; bellekte kaç nesne olduğu ayrı bir sorudur.

🔗 Konu: [1.1 §3 "Bean definition" ile "bean" farkı](1.1-container-nedir.md) · [1.5 Scope](1.5-scope.md)

---

### Soru 2 — Ana sınıf `tr.sirket.app`, servis `tr.sirket.servis`. Neden açılmıyor, iki çözüm nedir?

**Kısa cevap:** `@ComponentScan` **ana sınıfın paketinden başlar ve yalnızca aşağı iner**;
`servis` kardeş pakettir, hiç taranmaz. İki çözüm: (1) ana sınıfı ortak üst pakete
(`tr.sirket`) taşı, (2) taranacak paketleri açıkça yaz.

**Ayrıntı:**

Aldığın hata:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in tr.sirket.web.SiparisKontrolcusu required a
bean of type 'tr.sirket.servis.SiparisServisi' that could not be found.
```

Taramanın gördüğü ağaç:

```
tr.sirket
├── app
│   └── SiparisServisiUygulamasi.java   ← @SpringBootApplication burada, tarama BURADAN başlar
├── servis
│   └── SiparisServisi.java             ← ❌ kardeş paket, tarama hiç uğramaz
└── web
    └── SiparisKontrolcusu.java         ← ❌ o da taranmaz
```

**Çözüm A — ana sınıfı yukarı taşı (tercih edilen):**

```java
package tr.sirket;                      // ← app'ten tr.sirket'e çıktı

@SpringBootApplication
public class SiparisServisiUygulamasi { ... }
```

Artık `servis` ve `web` ikisi de altında kalır. Bu yüzden gelenek şudur: **ana sınıf kök
pakette durur.**

**Çözüm B — taranacak paketleri açıkça yaz:**

```java
@SpringBootApplication(scanBasePackages = "tr.sirket")
public class SiparisServisiUygulamasi { ... }
```

Çalışır, ama listeyi elle bakımda tutarsın; yeni bir üst paket eklendiğinde burayı da
güncellemen gerekir. Çözüm A'yı bir kere yaparsın, çözüm B'yi her seferinde.

> 📌 **Sık yapılan hata:** Hata mesajını okuyup servis sınıfına `@Autowired` eklemeye
> çalışmak. Sınıfı bean yapan şey `@Service`/`@Component`'tir; `@Autowired` bean'e bir şey
> **vermek** içindir. Mesaj sebebi de söylemez: "bulamadım" der, "çünkü o paketi taramıyorum"
> demez.

🔗 Konu: [1.1 §6 `@SpringBootApplication` ne açıyor](1.1-container-nedir.md)

---

### Soru 3 — `@Autowired` alanın `null`. Sorabileceğin **ilk** soru nedir?

**Kısa cevap:** **"Bu nesneyi kim kurdu — container mı, ben mi?"**

**Ayrıntı:**

Bu tek soru, teşhis ağacının tamamını ikiye böler:

```
Alan null geldi
   ↓
"Bu nesneyi kim kurdu?"
   ├── BEN kurdum (kodda bir new var)
   │      → İş burada biter. Container tanımadığı nesneye injection yapmaz.
   │        Nesneyi bean yap ya da bağımlılığı constructor'dan al.
   │
   └── CONTAINER kurdu (bean)
          → O zaman sıradaki sorular: sınıf taranan pakette mi? (1.1 §6)
            aynı türden iki aday mı var? (1.3)   bean gerçekten tanımlı mı?
```

Sırayı ters çevirirsen — önce paket yapısına, sonra anotasyonlara bakarsan — kodda bir
satırlık `new` durduğu hâlde yarım saat paket adı incelersin.

Aynı soru sonraki kurslarda da ilk sorudur:

| Şikâyet | İlk soru | Nerede |
|---|---|---|
| Alan `null` | Bu nesneyi kim kurdu? | 1.1 |
| `@Transactional` çalışmıyor | Bu nesneyi kim kurdu? | 201 |
| `@Cacheable` yok sayılıyor | Bu nesneyi kim kurdu? | 301 |

> 📌 **Sık yapılan hata:** Soruyu "anotasyonu doğru mu yazdım?" diye kurmak. Anotasyon doğru
> olabilir ve yine de hiçbir işe yaramaz — çünkü onu okuyacak olan container, o nesneden habersizdir.

🔗 Konu: [1.1 §2 `ApplicationContext`](1.1-container-nedir.md) · [1.2 §4 Yanlış hâlleri](1.2-bean-tanimlama.md)

---

### Soru 4 — Hataların açılışta patlaması neden avantajdır? İstisna hangi iki bean türüdür?

**Kısa cevap:** Çünkü hata **dağıtım anında** görünür, üretimde gece yarısı değil. İstisna
iki tür: **`@Lazy` işaretli** bean'ler ve **prototype scope'lu** bean'ler.

**Ayrıntı:**

Zamanlama farkının bedeli:

| Hata ne zaman çıkarsa | Kim görür | Maliyeti |
|---|---|---|
| Açılışta (dağıtımda) | Dağıtımı yapan kişi, o an ekranda | Dağıtım durur, geri alınır — dakikalar |
| İlk kullanımda (üretimde) | Müşteri | Kesinti, çağrı, gece yarısı — saatler |

Eksik bir bağımlılığın varsa uygulama **hiç ayağa kalkmaz**. Bu bir kusur gibi görünür; oysa
tasarım tercihidir: yapılandırma sorununu, ondan zarar görecek kişi ile aran açıkken bulmak.

**İki istisna ve sebepleri:**

| Tür | Neden açılışta doğrulanmaz | Hata ne zaman çıkar |
|---|---|---|
| `@Lazy` | Kurulum bilinçli olarak ilk isteğe ertelenmiştir | Bean ilk istendiğinde (1.4) |
| Prototype scope | Açılışta hiç kurulmaz; her istendiğinde yenisi üretilir | İlk `getBean` / ilk injection (1.5) |

Bunu gözünle görmek istersen: bir sınıfa `@Lazy` ekle ve çalıştır — constructor'daki
yazdırma satırı **hiç çıkmaz**, çünkü nesne kurulmamıştır bile.

> 📌 **Sık yapılan hata:** "Uygulama açıldı, demek ki her şey yolunda" demek. Açılış yalnızca
> **erken kurulan** bean'leri doğrular. Bu yüzden `@Lazy`'yi "açılış hızlandırma" diye her
> yere yazmak, hataları üretime taşımaktır.

🔗 Konu: [1.1 §4 Container ne zaman ve kaç kere çalışır](1.1-container-nedir.md) · [1.4 §5 `@Lazy`](1.4-bean-lifecycle.md)

---

### Soru 5 — Üretim kodunda `container.getBean(SiparisServisi.class)` neden kötü bir fikir?

**Kısa cevap:** Buna **service locator** denir; dependency injection'ın
tersidir. Sınıf container'ı tanımak zorunda kalır ve test edilemez hâle gelir.

**Ayrıntı:**

Bağımlılığın **yönü** değişiyor — asıl mesele bu:

```java
// Injection: sınıf container'dan habersiz
@Service
public class RaporServisi {
    private final SiparisServisi siparis;
    public RaporServisi(SiparisServisi siparis) { this.siparis = siparis; }
}

// Service locator: sınıf container'ı TANIMAK ZORUNDA
@Service
public class RaporServisi {
    private final ApplicationContext container;
    public RaporServisi(ApplicationContext container) { this.container = container; }

    public void rapor() {
        var siparis = container.getBean(SiparisServisi.class);   // ← bağımlılık imzada görünmüyor
    }
}
```

Somut bedeller:

| Bedel | Neden |
|---|---|
| Test | Birinciyi `new RaporServisi(mock)` ile kurarsın; ikincisi için **container** kurman gerekir |
| Okunabilirlik | Birincide bağımlılık imzada yazılı; ikincide metotların içine dağılmış |
| Hata zamanı | Birincide eksik bean **açılışta** patlar; ikincide `getBean` çağrıldığı anda |
| Taşınabilirlik | İkinci sınıf Spring olmadan hiçbir yerde çalışmaz |

**Peki `getBean` ne zaman doğru?** Öğrenirken ve **hata ararken**: container'da ne var, kaç tane
var, adı ne — bunlara bakmak için. Bu kursun pratiklerinde tam olarak bu amaçla kullanıyoruz.

> 📌 **Sık yapılan hata:** `getBean`'i "injection'ın kısa yazılışı" sanmak. Kısaltmıyor,
> **ters çeviriyor.** Ayrım şu: injection'da bean'i sana container getirir; `getBean`'de sen container'ı
> aramaya gidersin. Aynı ayrım 1.5'te `ObjectProvider` ile geri gelecek — orada bağımlılık
> tek bir türe daralır, container'ın tamamına değil.

🔗 Konu: [1.1 §2 `ApplicationContext`](1.1-container-nedir.md) · [1.5 §5 `ObjectProvider`](1.5-scope.md)

---

## 1.2 Bean tanımlamanın iki yolu

📄 Sorular: [`1.2-bean-tanimlama.md`](1.2-bean-tanimlama.md)

### Soru 1 — `RestClient` sınıfına neden `@Service` yazamıyorsun? Alternatif nedir?

**Kısa cevap:** Çünkü **kaynak kodu sende değil** — sınıf Spring'in kendi jar'ında.
Alternatif: `@Configuration` sınıfında bir **`@Bean` metodu**.

**Ayrıntı:**

`@Component` ailesi sınıfın **başına** yazılır; yazamadığın bir dosyanın başına da
yazamazsın. Çözüm, nesneyi sen kurup container'a teslim etmektir:

```java
package tr.loncademy.siparis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class IstemciYapilandirmasi {

    @Bean
    public RestClient stokIstemcisi() {
        return RestClient.builder()
                .baseUrl("https://stok.sirket.com")
                .build();
    }
}
```

`@Component` "bu sınıfı bul ve kur" der; `@Bean` "bu nesneyi **ben kurdum**, al sakla" der.

Çalışmayan alternatifler ve nedenleri:

| Deneme | Neden olmaz |
|---|---|
| Jar'ı açıp sınıfa `@Component` eklemek | Bağımlılık her güncellemede geri gelir; sürdürülebilir değil |
| `@ComponentScan`'e kütüphane paketini eklemek | Tarama yalnızca **işaretli** sınıfları toplar; işaretsiz sınıf yine bean olmaz |
| `RestClient`'i genişletip alt sınıfa `@Service` yazmak | Kurucu/builder düzeni buna uygun değil; olsa bile gereksiz bir sınıf üretirsin |

> 📌 **Sık yapılan hata:** Bunu bir kısıtlama sanmak. `@Bean` yalnızca bir çare değil, üç
> durumun **doğru aracıdır**: sınıf senin değil · kurulum mantık istiyor · aynı türden çok
> nesne lazım.

🔗 Konu: [1.2 §2 Yol B — `@Bean` metodu](1.2-bean-tanimlama.md)

---

### Soru 2 — `@Bean public DataSource veriKaynagi()` metodunun ürettiği bean'in adı nedir?

**Kısa cevap:** **`veriKaynagi`** — yani **metodun adı**. Türü ise `DataSource`, yani
**dönüş türü**.

**Ayrıntı:**

`@Bean` metodunun imzası bean'in kimliğini tamamen belirler:

```java
@Bean
public DataSource veriKaynagi() { ... }
//     ▲ tür        ▲ ad
```

İki yolun kural farkı, en sık karıştırılan yerdir:

| | Bean adı nereden gelir | Örnek |
|---|---|---|
| `@Component` ailesi | Sınıf adı, ilk harf küçük | `SiparisServisi` → `siparisServisi` |
| `@Bean` metodu | **Metot adı** | `veriKaynagi()` → `veriKaynagi` |

Dolayısıyla döndürülen sınıfın adı (`DataSource`, ya da içeride kurduğun somut sınıf ne ise)
bean adı **değildir**. Metodun adını değiştirirsen bean adı da değişir — ve ada göre
injection yapan varsa kırılır.

Kanıtlamak istersen container'dan listele:

```java
container.getBeansOfType(RestClient.class)
   .forEach((ad, nesne) -> System.out.println(ad + " -> " + nesne));
```

```
stokIstemcisi -> org.springframework.web.client.DefaultRestClient@...
odemeIstemcisi -> org.springframework.web.client.DefaultRestClient@...
```

Adlar birebir metot adları.

> 📌 **Sık yapılan hata:** `@Bean` metoduna `olustur()`, `create()`, `build()` gibi genel bir
> ad vermek. O ad bean'in adıdır: `@Qualifier("olustur")` yazmak zorunda kalırsın ve ikinci
> bir yapılandırma sınıfında aynı adı kullanınca açılış patlar. **Metodu, üreteceği bean'in
> adıyla adlandır.**

🔗 Konu: [1.2 §2 Yol B](1.2-bean-tanimlama.md) · [1.3 §3 Belirsizlik](1.3-dependency-injection.md)

---

### Soru 3 — İki `@Configuration` sınıfında da `@Bean public RestClient istemci()` var. Ne olur, iki çözüm nedir?

**Kısa cevap:** **Açılış patlar** — bean adları container genelinde benzersizdir. İki çözüm:
(1) metot adlarını ayır, (2) bean'lerden birini kaldır/birleştir. Yapılmayacak şey:
`allow-bean-definition-overriding` açmak.

**Ayrıntı:**

Gerçek hata:

```
The bean 'istemci', defined in class path resource [DigerYapilandirma.class], could not
be registered. A bean with that name has already been defined in class path resource
[Yapilandirma.class] and overriding is disabled.
```

Hata **kayıt adımında** çıkar (1.1 §5, adım 3) — daha hiçbir nesne kurulmadan. Container iki
tarifi aynı isimle dosyalayamaz.

**Çözüm 1 — adları ayır (neredeyse her zaman doğru olan):**

```java
@Bean public RestClient stokIstemcisi()  { ... }
@Bean public RestClient odemeIstemcisi() { ... }
```

İki bean, aynı tür, farklı ad. Bu tamamen meşrudur — **aynı tür serbest, aynı ad yasak.**
(Bunun injection'da yarattığı belirsizliği 1.3'te `@Qualifier` ile çözersin.)

**Çözüm 2 — gerçekten iki tane gerekiyor mu, sor:**

Çoğu zaman iki yapılandırma sınıfı aynı işi kopyalamıştır. Biri silinir ya da ikisi tek
metotta birleşir. Bu, adı değiştirmekten daha iyi bir sonuçtur: bean sayısı azalır.

**Yapılmayacak olan:**

```properties
spring.main.allow-bean-definition-overriding=true    # ← açma
```

Hata mesajının sonundaki `overriding is disabled` bunu davet ediyor gibi görünür. Bu ayar
Spring Boot 2.1'den beri kapalıdır ve kapalı olması bilinçlidir: **hatayı çözmez, onu
sessiz bir "hangisi kazandı?" bilmecesine çevirir.** Kazananı sınıfların yüklenme sırası
belirler; o sıra da bir gün senden habersiz değişir.

> 📌 **Sık yapılan hata:** Buna `@Primary` ile çözüm aramak. `@Primary` aynı **türden**
> çoklu bean sorununu çözer (1.3), aynı **addan** değil — zaten ikinci tanım hiç
> kaydedilemediği için sıra ona gelmez.

🔗 Konu: [1.2 §4 Yanlış hâlleri, Hata 2](1.2-bean-tanimlama.md) · [1.1 §5 Açılış adımları](1.1-container-nedir.md)

---

### Soru 4 — `@Bean` metodunu `@Service` sınıfına koydun. Hata alır mısın? Almazsan tehlike nedir?

**Kısa cevap:** **Hata almazsın** — tehlike de tam olarak budur. Metot `@Configuration`
dışında olduğu için **"lite mode" (lite mode)** çalışır: metodu her çağıran yeni bir nesne
alır, **singleton garantisi kaybolur.**

**Ayrıntı:**

```java
@Service                                   // ← @Configuration değil
public class SiparisServisi {

    @Bean
    public RestClient stokIstemcisi() {    // bean üretir, ama lite mode'da
        return RestClient.builder().build();
    }
}
```

Uygulama açılır, container'da bir `stokIstemcisi` bean'i de görürsün. Fark, metodun **kod
içinden** çağrıldığı anda ortaya çıkar:

| Metot nerede | Container içeriden çağrıları yakalar mı | Sonuç |
|---|---|---|
| `@Configuration` sınıfında | ✅ Evet | Her çağrı **aynı** bean'i döndürür |
| `@Component`/`@Service` sınıfında | ❌ Hayır | Her çağrı **yeni nesne** üretir |

Belirtisi şu şikâyettir: *"bean'im her seferinde yeni nesne dönüyor"* — ya da daha kötüsü,
hiç şikâyet yoktur ve uygulaman sessizce beş tane connection pool açar.

Bu bir **sessiz hatadır**, ve sessiz hata gürültülü hatadan pahalıdır: hata mesajı
almadığın için arayacak bir yerin de yoktur.

✅ **Doğrusu:** `@Bean` metotları `@Configuration` sınıfında durur. Mekanizmanın kendisi —
container'ın `@Configuration` sınıflarını neden ve nasıl sarmaladığı — [2.2](../02-anotasyon-haritasi/2.2-configuration.md)'de.

> 📌 **Sık yapılan hata:** "Çalışıyor, demek ki doğru" demek. Bu konudaki hataların çoğu
> çalışır; ayırt edici soru "çalışıyor mu?" değil, **"kaç nesne kuruldu?"**dur. Ölç:
> `hashCode()` yazdır, ya da constructor'a bir `System.out.println` koy ve kaç kere
> yazıldığını say.

🔗 Konu: [1.2 §4 Yanlış hâlleri, Hata 1](1.2-bean-tanimlama.md) · [2.2 `@Configuration`](../02-anotasyon-haritasi/2.2-configuration.md)

---

### Soru 5 — Kendi `SiparisServisi` sınıfını `@Bean` metoduyla tanımlamak neden tercih edilmez?

**Kısa cevap:** Çalışır — ama kurulum bilgisini sınıftan **uzağa** taşır. Sınıfa bakan kişi
onun bean olup olmadığını göremez. Kural: **kendi sınıfın → `@Component` ailesi; başkasının
sınıfı → `@Bean`.**

**Ayrıntı:**

İki yazımın aynı sonucu ürettiğine dikkat et; fark yalnızca **bilginin nerede durduğu**:

```java
// A) Kendi sınıfın için doğru olan
@Service
public class SiparisServisi { ... }

// B) Aynı sonucu veren, ama bilgiyi uzağa taşıyan
@Configuration
public class ServisYapilandirmasi {
    @Bean
    public SiparisServisi siparisServisi(StokIstemcisi stok) {
        return new SiparisServisi(stok);
    }
}
```

B'nin bedelleri:

| Bedel | Somut sonucu |
|---|---|
| Görünürlük | `SiparisServisi.java`'yı açan kişi bunun bean olduğunu **anlayamaz** |
| İki dosyada bakım | Yeni bir bağımlılık eklediğinde constructor'u **ve** `@Bean` metodunu güncellersin |
| Bean adı sürprizi | Ad artık sınıftan değil, metot adından gelir; metot adını değiştiren biri bean adını da değiştirmiş olur |
| Yapılandırma şişer | 40 servis, 40 `@Bean` metodu — 1.1'deki elle kurulum dosyasına geri dönersin |

Son satır asıl ironidir: `@Bean` metotlarıyla kendi sınıflarını tanımlarsan, container'ın seni
kurtardığı manuel kurulum dosyasını elinle geri yazmış olursun.

> 📌 **Sık yapılan hata:** Kuralı "hangisi daha güçlü" diye sormak. İkisi de aynı şeyi
> üretir; seçim ölçütü **bilginin nerede okunacağıdır.** Sınıfa bakınca bean olduğunu
> göremiyorsan, senden sonraki kişi de göremeyecek.

🔗 Konu: [1.2 §3 İkisinin karşılaştırması](1.2-bean-tanimlama.md)

---

## 1.3 Dependency injection ve belirsizlik

📄 Sorular: [`1.3-dependency-injection.md`](1.3-dependency-injection.md)

### Soru 1 — `could not be found` ile `but 3 were found` çözümleri neden birbirinin tersidir?

**Kısa cevap:** Birincisinde aday sayısı **0**'dır — bean **eklersin**. İkincisinde aday
sayısı **3**'tür — aralarından **seçersin**. Biri arzı artırır, diğeri talebi daraltır.

**Ayrıntı:**

Container türe göre arar ve bulduğu aday sayısına göre dallanır:

```
1. Parametrenin TÜRÜNE uyan bean'leri bul
        ↓
   ┌─── 0 aday  → HATA: "required a bean of type ... could not be found"
   ├─── 1 aday  → onu kullan ✅
   └─── 2+ aday → @Primary → @Qualifier → parametre adı → yine olmazsa
                  HATA: "required a single bean, but N were found"
```

İki hatanın karşılaştırması:

| | `could not be found` | `but 3 were found` |
|---|---|---|
| Aday sayısı | 0 | 3 |
| Gerçek sebep | Sınıf bean değil, ya da taranmayan pakette (1.1) | Aynı türden birden çok geçerli bean var |
| Çözüm | Anotasyon ekle · paketi taramaya al · `@Bean` metodu yaz | `@Qualifier` · `@Primary` · `List<T>` ile hepsini al |
| Yanlış yön | Buraya `@Qualifier` yazmak hiçbir işe yaramaz — seçilecek bir şey yok | Buraya yeni bean eklemek durumu **kötüleştirir**: 4 aday olur |

Son satır, soruyu asıl anlamlı kılan yer: hatayı yanlış tanırsan, uyguladığın çözüm sorunu
büyütür.

`but N were found` hatasının bir hediyesi daha var — Spring çözümleri mesajın içine yazar:

```
Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple
beans, or using @Qualifier to identify the bean that should be consumed
```

> 📌 **Sık yapılan hata:** İki mesajı gözle ayırt etmeden çözüm aramak. Ayırt edici tek
> kelime **sayıdır**: "could not be found" = yok, "but N were found" = çok. Mesajın ilk
> satırındaki sayıyı oku, sonra çözüme geç.

🔗 Konu: [1.3 §2 Container nasıl eşleştiriyor](1.3-dependency-injection.md) · [1.1 §6 Tarama](1.1-container-nedir.md)

---

### Soru 2 — `@Primary` ile `@Qualifier` farkı nedir, hangisi nereye yazılır?

**Kısa cevap:** **`@Primary` üretende** (bean definition'ın yanında), **`@Qualifier` tüketende**
(injection parametresinde). Biri container genelinde varsayılan ilan eder, diğeri tek bir noktada
seçim yapar.

**Ayrıntı:**

```java
// @Primary — ÜRETEN taraf: "belirtilmediyse beni kullan"
@Bean
@Primary
public RestClient stokIstemcisi() { ... }

@Bean
public RestClient odemeIstemcisi() { ... }
```

```java
// @Qualifier — TÜKETEN taraf: "ben özellikle şunu istiyorum"
public OdemeServisi(@Qualifier("odemeIstemcisi") RestClient istemci) {
    this.istemci = istemci;
}
```

| | `@Primary` | `@Qualifier` |
|---|---|---|
| Nereye yazılır | Bean definition'a (`@Bean` metodu / `@Component` sınıfı) | Injection parametresine |
| Etki alanı | **Container geneli** — bütün injection noktaları | **Tek nokta** — yalnızca o parametre |
| Kaç kere yazarsın | Bir kere | Her özel durum için ayrı |
| Okunabilirlik | Tüketiciye bakınca hangi bean'in geldiği **görünmez** | Kodda **yazılı** |
| Eşleştirme sırası | Önce değerlendirilir | `@Primary` yoksa devreye girer |

**Hangisini ne zaman:**

| Durum | Seçim |
|---|---|
| İki adaydan biri "normal", diğeri özel durum | `@Primary` (normal olana) + istisnalara `@Qualifier` |
| Her tüketici farklı olanı istiyor | Sadece `@Qualifier` |
| Adayların hepsi lazım | `List<T>` injection'ı |
| Hızlı çözüm arıyorum | Parametre adı — **yapma**, kırılgan (Soru 5) |

`@Primary`'yi dikkatli kullan: **bir yerde kolaylık, on yerde sürpriz** olabilir. Yeni bir
geliştirici `RestClient` inject ettiğinde, neden ödeme değil de stok istemcisinin geldiğini
kodda hiçbir yerde göremez.

> 📌 **Sık yapılan hata:** İkisinin yerini takas etmek — tüketiciye `@Primary` yazmak. Hiçbir
> etkisi olmaz ve hata da vermez; sen çözdüğünü sanırken aynı belirsizlik hatası devam eder.
> Sınama sorusu: **"kim seçiyor?"** `@Primary`'de üretici, `@Qualifier`'da tüketici.

🔗 Konu: [1.3 §3 Belirsizlik: birden çok aday](1.3-dependency-injection.md)

---

### Soru 3 — `List<OdemeYontemi>` boş geldi ve hata almadın. İki olası sebep nedir?

**Kısa cevap:** (1) O arayüzü gerçekleyen **hiç bean yok**; (2) gerçeklemeler var ama
**taranmayan bir pakette**, yani container onları hiç görmedi. Her iki durumda da container **boş liste**
inject eder, hata vermez.

**Ayrıntı:**

`List<T>` injection'ı, tek bir bean istemekten farklı davranır — asıl tuzak budur:

| İstek | Hiç aday yoksa |
|---|---|
| `OdemeYontemi yontem` | ❌ Açılış patlar: `required a bean of type ... could not be found` |
| `List<OdemeYontemi> yontemler` | ⚠️ **Boş liste** gelir, uygulama açılır, hata yok |
| `Optional<OdemeYontemi> yontem` | ⚠️ `Optional.empty()` gelir, hata yok |

Teşhis — sayıyı yazdır, tahmin etme:

```java
@Service
public class OdemeServisi {
    public OdemeServisi(List<OdemeYontemi> yontemler) {
        System.out.println(">>> bulunan ödeme yöntemi: " + yontemler.size());
        yontemler.forEach(y -> System.out.println("    " + y.getClass().getSimpleName()));
    }
}
```

Beklenen çıktı:

```
>>> bulunan ödeme yöntemi: 3
    KrediKarti
    Havale
    Kapida
```

`0` görüyorsan sırayla kontrol et: (a) gerçekleme sınıflarında `@Component` var mı,
(b) sınıflar ana sınıfın paketinin **altında** mı (1.1 §6).

Ve savunmayı sen yazacaksın, çünkü container yazmayacak:

```java
if (yontemler.isEmpty()) {
    throw new IllegalStateException("hiç ödeme yöntemi bean'i bulunamadı");
}
```

> 📌 **Sık yapılan hata:** Boş listeyi `@Order` eksikliğine bağlamak. `@Order` yalnızca
> **sırayı** belirler, üyeliği değil — hiç yazmasan da elemanlar gelir, sadece sıraları
> garanti değildir. Boş liste her zaman "aday yok" demektir.

🔗 Konu: [1.3 §4 Liste ve harita injection'ı](1.3-dependency-injection.md)

---

### Soru 4 — Field injection döngüsel bağımlılığı neden saklar? Bu neden kötüdür?

**Kısa cevap:** Çünkü nesne **önce yarım kurulur, sonra alanlar doldurulur** — döngü bu
boşluktan sızıp sessizce çalışabilir. Kötüdür, çünkü ortada bir **tasarım sorunu** vardır ve
onu görmeni sağlayacak tek sinyal susturulmuştur.

**Ayrıntı:**

İki kurulum biçiminin döngü karşısındaki davranışı:

```
YAPICI METOT:  A'yı kurmak için B lazım → B'yi kurmak için A lazım → çıkmaz
               ⇒ container ilerleyemez, AÇILIŞTA PATLAR

ALAN:          A'yı boş kur → B'yi boş kur → A'nın alanına B'yi yaz → B'nin alanına A'yı yaz
               ⇒ kurulum tamamlanır, UYGULAMA AÇILIR
```

Constructorla aldığın gerçek çıktı:

```
The dependencies of some of the beans in the application context form a cycle:

┌─────┐
|  AServisi defined in file [.../AServisi.class]
↑     ↓
|  BServisi defined in file [.../BServisi.class]
└─────┘
```

**Neden kötü:** Döngü bir yapılandırma sorunu değil, **tasarım sorunudur** — A ile B
birbirinin işini yapıyor demektir. Field injection uygulamayı açtırarak bu bulguyu senden
saklar; sorun kaybolmaz, yalnızca faturası ertelenir (sıralamaya duyarlı, yarım kurulmuş
nesnelerle çalışan kırılgan bir kod tabanı).

Üç gerçek çözüm:

| Çözüm | Ne zaman |
|---|---|
| **Ortak parçayı üçüncü bir sınıfa çıkar** | En sık doğru cevap. A ve B'nin paylaştığı iş `CServisi` olur, ikisi de ona bağlanır. |
| **Event kullan** | A, B'yi çağırmak yerine event yayınlar; B dinler. Bağı koparır. |
| **Sınırı yeniden çiz** | A ve B aslında tek sınıf olmalıydı, ya da sınır yanlış yerden geçmiş. |

Ve bir kaçış yolu — hata mesajının kendisi de "son çare" diyerek gösteriyor:

```properties
spring.main.allow-circular-references=true    # ← açma
```

Spring Boot 2.6'dan beri döngüler varsayılan olarak yasak. Bu bir kısıtlama değil, **teşhis**.

> 📌 **Sık yapılan hata:** Field injection'ı "daha esnek" sanmak, çünkü döngüde şikâyet
> etmiyor. Şikâyet etmemesi bir özellik değil, kaybedilmiş bir uyarıdır. **Constructor
> injection'ının sana verdiği asıl hediye budur.**

🔗 Konu: [1.3 §6 Döngüsel bağımlılık](1.3-dependency-injection.md) · [1.3 §1 Üç injection biçimi](1.3-dependency-injection.md)

---

### Soru 5 — Parametre adına güvenmek neden kırılgandır? Hata mesajının hangi satırı haber verir?

**Kısa cevap:** Çünkü Java parametre adlarını derlenmiş `.class` dosyasına **varsayılan
olarak yazmaz**; bu bilgi ancak derleyici **`-parameters`** bayrağıyla çalışırsa kalır.
Haber veren satır:

```
This may be due to missing parameter name information

Ensure that your compiler is configured to use the '-parameters' flag.
```

**Ayrıntı:**

Kırılgan çözüm şudur — parametre adını bean adıyla eşitlemek:

```java
public StokServisi(RestClient stokIstemcisi) { ... }    // parametre adı = bean adı
```

Bu, eşleştirme akışının **4. ve son** adımıdır: tür → `@Primary` → `@Qualifier` → **parametre
adı**. Yani en zayıf ölçüte dayanıyorsun, üstelik o ölçüt kodda değil, **derleme
yapılandırmasında** duruyor.

Kırılganlık zinciri:

```
Kod hiç değişmedi
   ↓
Derleme düzeni değişti (yeni bir yapı aracı, elden yazılmış javac, farklı bir eklenti)
   ↓
-parameters bayrağı kapandı
   ↓
.class dosyasında parametre adı yok, adlar arg0/arg1 oldu
   ↓
Injection bozuldu — ve git geçmişinde açıklayacak tek satır yok
```

Spring Boot'un Maven yapılandırması bu bayrağı senin için açar; bu yüzden çoğu projede
çalışır ve insanlar buna güvenmeye başlar. Ama garanti eden şey senin kodun değil,
**derleyici ayarındır.**

Dayanıklı hâli, tek kelime fark:

```java
public StokServisi(@Qualifier("stokIstemcisi") RestClient istemci) { ... }
```

Artık seçim `.class` içindeki ad bilgisine değil, kodda yazılı bir metne bağlı — üstelik
okuyan kişi hangi bean'in geldiğini doğrudan görüyor.

> 📌 **Sık yapılan hata:** "Bizde çalışıyor" diye bunu kalıcı çözüm saymak. Bu hatanın
> belirtisi *"kodu değiştirmedik ama injection bozuldu"*dur ve teşhisi zordur, çünkü
> aramaya kod tabanından başlarsın — oysa değişiklik derleme ayarındadır. **Ezberleme, tanı:
> parametre adına güvenme, `@Qualifier` yaz.**

🔗 Konu: [1.3 §3 Çözüm C](1.3-dependency-injection.md) · [1.3 §2 Eşleştirme sırası](1.3-dependency-injection.md)

---

## 1.4 Bean lifecycle

📄 Sorular: [`1.4-bean-lifecycle.md`](1.4-bean-lifecycle.md)

### Soru 1 — Connection pool açma kodunu `@PostConstruct`'a koymanın üç sebebi nedir?

**Kısa cevap:** (1) Açılış dış sistemin hızına bağlanmaz, (2) sınıf Spring'siz test
edilebilir kalır, (3) hata mesajı yanıltıcı olmaz — exception "bean oluşturulamadı" diye
sarılıp asıl sebebi gizlemez.

**Ayrıntı:**

Yanlış hâli — constructor'da iş yapmak:

```java
public StokServisi(RestClient istemci) {
    this.istemci = istemci;
    this.onbellek = istemci.get().uri("/stoklar").retrieve().body(Map.class);  // ← ağ çağrısı
}
```

Doğrusu — kurulumu ikiye ayır:

```java
public StokServisi(RestClient istemci) {      // 1. AŞAMA: sadece ata
    this.istemci = istemci;
}

@PostConstruct
public void hazirla() {                       // 2. AŞAMA: asıl iş
    this.onbellek = istemci.get().uri("/stoklar").retrieve().body(Map.class);
}
```

Üç sebep, tek tabloda:

| # | Dert | Constructor'da | `@PostConstruct`'ta |
|---|---|---|---|
| 1 | Açılış süresi | Stok servisi yavaşsa uygulaman **açılmaz** | Aynı gecikme olur ama nesne kurulumu tamamlanmıştır; hata kendi adıyla çıkar |
| 2 | Test | `new StokServisi(mock)` bile **ağa çıkar** | Nesneyi kurabilirsin; `hazirla()`'yı çağırmazsan ağ yok |
| 3 | Hata mesajı | Exception "bean oluşturulamadı" diye sarılır, asıl sebep kaybolur | Exception kendi stack trace'ini korur |

Dördüncü ve daha derin bir sebep de var: constructor lifecycle'ın **1. aşamasıdır** ve
orada nesne dünyası henüz eksiktir — field injection bitmemiş, `@Value` değerleri
okunmamış, proxy'lenme hiç yapılmamış olabilir.

**Kural tek cümle: constructor'da atama yap, iş yapma.**

> 📌 **Sık yapılan hata:** İşi constructor'da bırakıp sınıfa `@Lazy` eklemek. Açılış
> gerçekten hızlanır, ama aynı ağ çağrısı bu sefer **ilk istekte** — belki üretimde — patlar
> ve sınıf hâlâ test edilemez. Erteleme, ayırma değildir.

🔗 Konu: [1.4 §1 Dert: constructor'da iş yapmak](1.4-bean-lifecycle.md) · [1.4 §2 Sıra](1.4-bean-lifecycle.md)

---

### Soru 2 — `@PreDestroy`'un çalışmadığı üç durumu say.

**Kısa cevap:** (1) `kill -9` / **SIGKILL**, (2) **prototype scope'lu** bean'ler,
(3) elektrik kesilmesi / Docker container'ının zorla durdurulması.

**Ayrıntı:**

| Durum | `@PreDestroy` çalışır mı | Neden |
|---|---|---|
| `Ctrl+C` / `SIGTERM` | ✅ Evet | JVM kapanış kancası devreye girer |
| `System.exit()` | ✅ Evet | Kapanış kancası yine çalışır |
| **`kill -9` / SIGKILL** | ❌ Hayır | İşletim sistemi süreci **anında** öldürür; çalıştıracak kod kalmaz |
| **Prototype scope** | ❌ Hayır | Container prototype nesneye referans tutmaz, öldüğünü bilemez (1.5) |
| **Elektrik / zorla durdurma** | ❌ Hayır | Süreç hiç haber almadan biter |

İlk ikisinin sezgiye aykırı olduğuna dikkat: `System.exit()` "sert kapanış" gibi görünür ama
`@PreDestroy` **çalışır**; buna karşılık prototype bean graceful shutdown yapan bir uygulamada bile
temizlenmez.

Kendi gözünle görmek istersen (1.4 pratiği): uygulamayı çalıştır, `Ctrl+C` yap —
`3) @PreDestroy — kapanıyorum` satırını görürsün. Sonra tekrar başlat ve başka bir
terminalde:

```bash
pkill -9 -f spring-boot:run
```

Satır **çıkmaz.** Bu farkı bir kez gördüysen, "kapanışta hallederim" fikrine bir daha
güvenmezsin.

Kubernetes gibi ortamlarda süreç önce SIGTERM, belirli bir süre sonra SIGKILL alır. O sürede
işini bitirebilmesi için:

```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

`graceful`: yeni istek kabul etmez, devam edenleri bitirir, sonra kapanır.

> 📌 **Sık yapılan hata:** `graceful` kapanışı bir garanti sanmak. O yalnızca **düzgün bir
> kapanış sinyali geldiğinde** iş görür. SIGKILL ile pazarlık edemezsin — sinyalin tanımı
> budur.

🔗 Konu: [1.4 §4 Kapanış](1.4-bean-lifecycle.md) · [1.5 §3 Prototype sürprizleri](1.5-scope.md)

---

### Soru 3 — "Kapanırken bekleyen kayıtları veritabanına yazarım" fikri neden yanlıştır?

**Kısa cevap:** Çünkü kapanışın çalışacağı **garanti değildir**. `kill -9` yediğin gün o
kayıtlar yok olur — ve hiçbir yerde iz kalmaz. `@PreDestroy` **kaynak bırakmak** içindir,
veri güvenliği için değil.

**Ayrıntı:**

Yanlış hâli:

```java
@PreDestroy
public void kapat() {
    bekleyenKayitlar.forEach(depo::kaydet);   // ← kill -9 gelirse hepsi kaybolur
}
```

Bu tasarımın örtük varsayımı şudur: *"süreç her zaman graceful shutdown ile kapanır."* Gerçekte kapanmadığı
durumlar sıradan:

| Olay | Ne kadar sık | Sonuç |
|---|---|---|
| Kubernetes `terminationGracePeriod` doldu → SIGKILL | Her yavaş kapanışta | Kayıp |
| İşletim sistemi OOM killer'ı süreci öldürdü | Bellek baskısı altında | Kayıp |
| Sunucu/container donanım düzeyinde gitti | Nadir ama olur | Kayıp |
| Operatör `kill -9` yazdı | Uygulama takıldığında ilk refleks | Kayıp |

Dahası: kaybın **belirtisi yoktur.** Uygulama zaten ölmüştür; kimse "şu 40 kayıt yazılamadı"
diye bir günlük satırı görmez.

✅ **Doğrusu — iki kural:**

1. **Veriyi geldiği anda kalıcı hâle getir.** Bekleyen kayıt biriktirmen gerekiyorsa,
   biriktirdiğin yer bellek değil dayanıklı bir yer olsun (veritabanı, kuyruk).
2. **`@PreDestroy`'da yalnızca kaynak bırak:** bağlantı kapat, dosya kapat, thread
   havuzunu durdur. Bunlar kaçırılırsa bedeli sızıntıdır — veri kaybı değil.

Ayrım tek cümlede: `@PreDestroy` **temizlik** kancasıdır, **taahhüt** noktası değil.

> 📌 **Sık yapılan hata:** `server.shutdown=graceful` ekleyip sorunu çözdüğünü sanmak.
> `graceful` devam eden **istekleri** bitirir; senin bellekteki listenin yazılacağını garanti
> etmez ve SIGKILL karşısında hiçbir şey yapmaz.

🔗 Konu: [1.4 §4 Kapanış](1.4-bean-lifecycle.md) · [1.4 §6 Hata 3](1.4-bean-lifecycle.md)

---

### Soru 4 — `@Lazy` bir bean eklediğinde neyi kazanır, neyi kaybedersin?

**Kısa cevap:** **Kazandığın:** açılış süresi kısalır (o bean açılışta hiç kurulmaz).
**Kaybettiğin:** hatayı açılışta görme güvencesi — hata artık bean ilk istendiğinde, belki
üretimde çıkar. Bu bir **takastır**, iyileştirme değil.

**Ayrıntı:**

| | Varsayılan (erken) | `@Lazy` (tembel) |
|---|---|---|
| Ne zaman kurulur | Açılışta | İlk istendiğinde |
| Açılış süresi | Uzar | Kısalır |
| Hata ne zaman çıkar | ✅ **Açılışta** — dağıtım anında | ⚠️ İlk kullanımda — belki üretimde saat 3'te |
| "Uygulama açıldı" ne anlama gelir | Bu bean doğrulandı | Bu bean hakkında **hiçbir şey bilmiyorsun** |

Ölçmek istersen: bir sınıfa `@Lazy` ekle ve çalıştır — constructor'daki ve
`@PostConstruct`'taki yazdırma satırları **hiç çıkmaz.** Nesne kurulmadı bile, çünkü kimse
onu istemedi. Erteleme kısmi değildir.

**Ne zaman bilinçli olarak takas edersin:** ağır ve **nadiren** kullanılan bir bean için —
örneğin ayda bir çalışan bir rapor motoru. Kazanç gerçek, kayıp da küçüktür.

**Ne zaman etmezsin:** "açılış yavaş" diye her yere yazmak. O zaman bütün hataları üretime
taşımış olursun ve elinde açılışın ne doğruladığına dair hiçbir bilgi kalmaz.

```
Ezberle: varsayılan erken kurulum, ve bu iyidir.
Tanı, yeter: @Lazy'nin var olduğunu.
Girme: spring.main.lazy-initialization=true ile hepsini birden tembelleştirmek
       — geliştirme sırasında açılışı hızlandırmak için kullanılır, üretimde değil.
```

> 📌 **Sık yapılan hata:** `@Lazy`'yi bellek iyileştirmesi sanmak. Kurulmayan bean gerçekten
> bellek tutmaz, ama singleton bean'ler zaten tanım başına bir tanedir; asıl kazanç bellekte
> değil **açılış süresinde**, asıl bedel de **hatanın zamanında**. Soruyu hep şöyle sor: *ne
> kazandım değil, neyi verdim?*

🔗 Konu: [1.4 §5 `@Lazy`](1.4-bean-lifecycle.md) · [1.1 §4 Açılışta patlamak](1.1-container-nedir.md)

---

### Soru 5 — `javax.annotation.PostConstruct` yazan bir örnek gördün. Ne yaparsın ve neden?

**Kısa cevap:** `import`'u **`jakarta.annotation.PostConstruct`** ile değiştiririm. Çünkü
Spring Boot 3'ten beri Jakarta EE'ye geçildi; `javax.annotation` paketi 4.1.1'in sınıf
yolunda **yoktur**.

**Ayrıntı:**

Kopyalarsan aldığın hata:

```
package javax.annotation does not exist
```

Doğrusu:

```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
```

Bu, aynı zamanda örneğin **yaşını** söyleyen bir işarettir: `javax` yazan bir yazı Spring
Boot 2.x döneminden kalmadır, yani ondaki başka bilgiler de eskimiş olabilir.

**Asıl tehlike derleme hatası değil, onun çıkmadığı durum:** projede başka bir kütüphane
üzerinden `javax.annotation` classpath'e girmişse kod **derlenir**, ama Spring o anotasyonu
tanımaz — `@PostConstruct` metodun **hiç çağrılmaz** ve hata da almazsın. Hazırlık kodun
sessizce atlanır.

Bu yüzden değişikliği yaptıktan sonra **doğrula**, varsaymakla yetinme:

```java
@PostConstruct
public void hazirla() {
    System.out.println("2) @PostConstruct — artık hazırım");
}
```

Açılış günlüğünde bu satırı görmüyorsan, anotasyon çalışmıyor demektir.

Aynı sınıfın kontrol listesi:

| Kural | Ayrıntı |
|---|---|
| Paket | `jakarta.annotation` |
| Dönüş türü | `void` |
| Parametre | Yok |
| Kaç tane | Birden çok yazılabilir ama **sırası garanti değildir** — bir tane yaz |

> 📌 **Sık yapılan hata:** IDE'nin önerdiği ilk `import`'u seçmek. İki paket de listede
> görünebilir ve yanlış olanı seçtiğinde derleme hatası **almazsın** — sadece anotasyon
> çalışmaz. `import` satırını gözle doğrula.

🔗 Konu: [1.4 §3 `@PostConstruct` ve `@PreDestroy`](1.4-bean-lifecycle.md) · [1.4 §6 Hata 1](1.4-bean-lifecycle.md)

---

## 1.5 Scope ve scoped proxy

📄 Sorular: [`1.5-scope.md`](1.5-scope.md)

### Soru 1 — Singleton bir servise `private String suAnkiKullanici` alanı eklemek neden geliştirmede sorun çıkarmaz da üretimde çıkarır?

**Kısa cevap:** Çünkü hata **eşzamanlılık** gerektirir. Geliştirmede tek kullanıcı vardır;
alan hep senin değerini tutar. Üretimde yüz kullanıcı **aynı nesnenin aynı alanına** yazar.

**Ayrıntı:**

Container'da `SiparisServisi`'nden **tek bir nesne** vardır (varsayılan scope `singleton`). İki
istek aynı anda geldiğinde:

```
zaman  Ayşe'nin thread'i                Mehmet'in thread'i            suAnkiKullanici
────────────────────────────────────────────────────────────────────────────────────
 t1    suAnkiKullanici = "ayse"                                       "ayse"
 t2                                     suAnkiKullanici = "mehmet"    "mehmet"
 t3    depo.kaydet(s, "mehmet") ← ❌                                  "mehmet"
```

Ayşe'nin siparişi Mehmet'in adına kaydedildi. **Hata mesajı yok, günlükte iz yok.**

Ortamlar arasındaki fark tam olarak şu:

| | Geliştirme | Üretim |
|---|---|---|
| Eşzamanlı istek | 1 | Onlarca–yüzlerce |
| `t1` ile `t3` arasına başkası girer mi | Hayır | Sürekli |
| Belirti | Yok — kod "çalışıyor" | Ara ara yanlış veri; tekrar üretilemez |

Bu yüzden testler geçer, kod gözden geçirmeden çıkar, ve arıza haftalar sonra "bazen
oluyor" diye bildirilir.

**Kural:** Singleton bir bean'in alanları **değişmez** olmalı — constructor'da atanan `final`
bağımlılıklar ve ayarlar. İstekten isteğe değişen hiçbir şey alanda durmaz:

```java
@Service
public class SiparisServisi {
    public void siparisAl(String kullanici, Siparis s) {   // ← alan yok, parametre var
        depo.kaydet(s, kullanici);
    }
}
```

> 📌 **Sık yapılan hata:** "Yük testinde çıkar" diye güvenmek. Çıkmayabilir de — zamanlamaya
> bağlıdır. Bu hata sınıfı test edilerek değil, **tasarımla** engellenir: singleton bean'in
> değişebilen alanı olmaz.

🔗 Konu: [1.5 §1 Dert: singleton bean'e durum yazmak](1.5-scope.md) · [1.1 §4 Container çalışma aşaması](1.1-container-nedir.md)

---

### Soru 2 — Bu soruna `synchronized` eklemek neden yeterli değildir?

**Kısa cevap:** Çünkü `synchronized` veriyi **bozulmaktan** korur, **karışmaktan** korumaz.
Ayşe yine Mehmet'in değerini okuyabilir — sadece sırayla okur.

**Ayrıntı:**

İki ayrı sorunu ayır:

| Sorun | Ne demek | `synchronized` çözer mi |
|---|---|---|
| **Veri bozulması** | İki thread aynı anda yazarken yapı tutarsız kalır (yarım güncellenmiş `HashMap` gibi) | ✅ Evet |
| **Veri karışması** | Ayşe'nin isteği Mehmet'in değerini okur | ❌ **Hayır** |

Kilitli hâlde ne olur:

```
t1  Ayşe kilidi alır → suAnkiKullanici = "ayse" → uzun işlem başlar → kilidi bırakır
t2  Mehmet kilidi alır → suAnkiKullanici = "mehmet" → kilidi bırakır
t3  Ayşe kilidi alır → depo.kaydet(s, suAnkiKullanici) → "mehmet" ← ❌ hâlâ yanlış
```

Alan bir tane olduğu sürece, kilit yalnızca **sırayı** düzenler; kimin değerinin orada
durduğunu düzeltmez. Üstelik kilit üretime bir de yavaşlama ekler: bütün istekler tek tek
geçmeye başlar.

Aynı yanlış refleksin diğer sürümleri:

| Deneme | Neden çözmez |
|---|---|
| Alanı `volatile` yapmak | Görünürlüğü düzeltir, paylaşımı değil |
| `AtomicReference` kullanmak | Atomik yazma sağlar; alan yine tek |
| Metodu `synchronized` yapmak | Yukarıdaki tablo — üstelik verimi düşürür |
| `ThreadLocal` kullanmak | İşe yarar ama temizlenmezse havuzdaki thread'de **sızar**; asıl çözüm değil |

**Çözüm kilit değil, alanı hiç kullanmamaktır:** durumu parametrede taşı. Gerçekten istek
bazlı bir bağlam gerekiyorsa doğru araç `request` scope'u + scoped proxy'dir (Soru 5).

> 📌 **Sık yapılan hata:** Konuyu "thread safety" başlığı altına koyup kilit aramak.
> Doğru başlık **paylaşılan durum**. Ayrıca "singleton bean thread-safe'tir" cümlesi de
> yanlıştır: singleton'lık nesne sayısıyla ilgilidir; güvenliği sağlayan şey **alansızlıktır**.

🔗 Konu: [1.5 §1 Dert: singleton bean'e durum yazmak](1.5-scope.md)

---

### Soru 3 — Prototype bir bean'i singleton servise inject ettin, hep aynı nesne geliyor. Sebebi ve iki çözümü nedir?

**Kısa cevap:** Sebep **scope uyumsuzluğu (scope mismatch)**: singleton servis **bir kere**
kurulur, o anda aldığı prototype nesneyi ömrü boyunca tutar. İki çözüm: **scoped proxy**
(`proxyMode`) ya da **`ObjectProvider`**.

**Ayrıntı:**

```java
@Service                                     // singleton — BİR KERE kurulur
public class RaporServisi {
    private final RaporUreticisi uretici;    // prototype

    public RaporServisi(RaporUreticisi uretici) {
        this.uretici = uretici;              // ← injection BİR KERE olur
    }
}
```

Prototype olmasının hiçbir faydası kalmaz. Kural şu: **dar scope'lu bir bean, geniş scope'lu
bir bean'in içine sıkıştığında geniş olanın ömrünü devralır.**

Ölçüm (1.5 pratiği): singleton servisten üç kere `kimlik()` çağırırsan üç kez **aynı** `hashCode`
görürsün — oysa doğrudan `container.getBean(PrototypeOrnek.class)` çağırsan üç **farklı** sayı
gelir. Fark, kaç kere **istendiğindedir**.

**Çözüm A — scoped proxy (üreten tarafta):**

```java
@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RaporUreticisi { ... }
```

Inject edilen artık gerçek nesne değil, ince bir proxy'dir: her metot çağrısında container'a
gider, o an için doğru nesneyi ister, çağrıyı ona iletir. Pratikte üç farklı `hashCode`
görmeye başlarsın.

**Çözüm B — `ObjectProvider` (tüketen tarafta):**

```java
@Service
public class RaporServisi {
    private final ObjectProvider<RaporUreticisi> saglayici;

    public RaporServisi(ObjectProvider<RaporUreticisi> saglayici) {
        this.saglayici = saglayici;
    }

    public void rapor() {
        RaporUreticisi uretici = saglayici.getObject();   // ← her çağrıda YENİ
        uretici.uret();
    }
}
```

Hangisini seçersin:

| | Scoped proxy | `ObjectProvider` |
|---|---|---|
| Kod nerede değişir | **Üreten** tarafta (`@Scope`) | **Tüketen** tarafta |
| Görünürlük | Gizli — bakan kişi proxy olduğunu anlamaz | Açık — kodda `getObject()` görünür |
| Sınıf kısıtı | `final` olamaz (`TARGET_CLASS` alt sınıf üretir) | Yok |
| Ne zaman | Var olan tüketici kodunu değiştirmeden çözmek | Yeni kod yazarken, açıklık istiyorsan |

> 📌 **Sık yapılan hata:** prototype'ı "her kullanımda yeni" diye okumak. Doğrusu **"her
> istendiğinde yeni"** — ve singleton bir bean onu yalnızca **bir kere** ister. Cümledeki bu tek
> kelime, hatanın tamamını açıklar.

🔗 Konu: [1.5 §3 Prototype'ın iki sürprizi](1.5-scope.md) · [1.5 §4 Scoped proxy](1.5-scope.md) · [1.5 §5 `ObjectProvider`](1.5-scope.md)

---

### Soru 4 — `@PreDestroy`'un çağrılmadığı scope hangisidir ve neden?

**Kısa cevap:** **`prototype`.** Çünkü container prototype bean'i kurar ve **unutur** — ona referans
tutmaz, dolayısıyla ne zaman öldüğünü bilemez ve temizlik metodunu çağıramaz.

**Ayrıntı:**

Container'ın iki scope karşısındaki davranışı:

| | `singleton` | `prototype` |
|---|---|---|
| Container nesneyi tutar mı | ✅ Evet, haritasında | ❌ Hayır, teslim eder ve unutur |
| Kaç nesne olduğunu bilir mi | ✅ Bir tane | ❌ Bilmez |
| Kapanışta ne yapar | `@PreDestroy` çağırır | Çağıracağı bir liste **yoktur** |

Yani bu bir eksiklik değil, tanımın doğal sonucu: container sayısını bilmediği nesnelerin
kapanışını yönetemez.

Belirtisi sessizdir — kaynak birikir:

```java
@Component
@Scope("prototype")
public class GeciciDosya {
    @PreDestroy public void sil() { dosya.delete(); }   // ← çağrılmaz, dosyalar birikir
}
```

✅ **Doğrusu: temizliği kullanan taraf yapar.** Prototype bean kaynak tutuyorsa
`AutoCloseable` gerçekleyip `try-with-resources` ile kullanılır:

```java
try (var dosya = saglayici.getObject()) {
    dosya.yaz(veri);
}   // ← kapanış burada, senin kontrolünde
```

Bu, [1.4](1.4-bean-lifecycle.md)'teki listenin de bir satırıdır: `@PreDestroy` `kill -9`'da
çalışmaz, **ve** prototype bean'lerde çalışmaz. İkisinin ortak noktası, kapanışı yönetenin
container olmamasıdır.

> 📌 **Sık yapılan hata:** "Uygulama kapanırken hepsi toplu olarak temizlenir" diye
> düşünmek. Container o nesnelerin listesini hiç tutmadığı için toplayacağı bir şey yoktur.
> **Prototype = kur ve unut.**

🔗 Konu: [1.5 §3 Sürpriz 1](1.5-scope.md) · [1.4 §4 Kapanış](1.4-bean-lifecycle.md)

---

### Soru 5 — `Scope 'request' is not active for the current thread` hatasının tam çözümünü kodla yaz.

**Kısa cevap:** Dar scope'lu bean'e **scoped proxy** ekle:

```java
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
```

**Ayrıntı:**

Hatayı üreten kod — singleton servisin içine proxy'siz `request` bean'i:

```java
@Component
@Scope("request")                       // ← proxyMode yok
public class IstekBaglami { ... }

@Service
public class SiparisServisi {
    public SiparisServisi(IstekBaglami baglam) { ... }    // singleton içine dar scope
}
```

Gerçek çıktı:

```
Error creating bean with name 'siparisServisi': Scope 'request' is not active for the
current thread; consider defining a scoped proxy for this bean if you intend to refer to
it from a singleton
```

Sebep zamanlamadır: `SiparisServisi` **açılışta** kurulur; o anda ortada bir HTTP isteği
yoktur, dolayısıyla `request` scope'u etkin değildir. Container inject edecek gerçek bir nesne
bulamaz.

Çözümün tamamı:

```java
package tr.loncademy.siparis;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class IstekBaglami {
    private String izlemeNumarasi;

    public String getIzlemeNumarasi() { return izlemeNumarasi; }
    public void setIzlemeNumarasi(String izlemeNumarasi) { this.izlemeNumarasi = izlemeNumarasi; }
}
```

`SiparisServisi` **hiç değişmez** — proxy'nin kazandırdığı şey budur. Inject edilen artık ince
bir proxy'dir; her metot çağrısında container'a gidip o isteğe ait doğru nesneyi bulur.

`proxyMode` seçenekleri:

| Değer | Ne yapar |
|---|---|
| `ScopedProxyMode.NO` *(varsayılan)* | Proxy yok — bu hatayı alırsın |
| `ScopedProxyMode.TARGET_CLASS` | Sınıfın alt sınıfı üretilir (CGLIB) — **çoğu durumda bunu kullan** |
| `ScopedProxyMode.INTERFACES` | Arayüz üzerinden proxy — sınıfa yalnızca arayüzüyle erişiliyorsa |

⚠️ `TARGET_CLASS` alt sınıf ürettiği için sınıf `final` **olamaz** ve parametresiz bir yapıcı
metoda ihtiyaç duyabilir. Sınıfın `final` olması gerekiyorsa `ObjectProvider`'a geç.

> 📌 **Sık yapılan hata:** Hatayı `Optional<IstekBaglami>` ile sarmalayıp bastırmak. Uygulama
> açılır ama bağlamı **hiç alamazsın** — sorun bean'in yokluğu değil, kurulum anında istek
> bağlamının olmamasıdır. Hata mesajı çözümü zaten söylüyor: *consider defining a scoped
> proxy*. Spring'in hata metinlerini sonuna kadar oku.

🔗 Konu: [1.5 §4 Scoped proxy](1.5-scope.md) · [1.5 §6 Hata 2](1.5-scope.md)

---

⬅️ [Bölüme dön](1.1-container-nedir.md) · 📄 [00.1 Genel bakış](../00-baslangic/01-genel-bakis.md) · ➡️ [2.1 Stereotype'lar](../02-anotasyon-haritasi/2.1-stereotype.md)
