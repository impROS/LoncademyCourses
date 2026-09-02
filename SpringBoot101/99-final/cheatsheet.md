# Cheatsheet — Spring Boot 101 tek sayfada

> Bu dosya öğretmez, **hatırlatır**. Konuları bitirdikten sonra tara; unuttuğun yeri
> hemen görürsün. Terim tanımadıysan
> [kavram sözlüğüne](../00-baslangic/03-kavram-sozlugu.md), bir ayarın değerini seçerken
> [ayar rehberine](../00-baslangic/04-ayar-rehberi.md) bak.

**Hedef sürüm:** Spring Boot 4.1.1 · Spring Framework 7.0 · Java 21

---

## Her konu tek cümlede

| # | Konu | Aklında kalacak tek cümle |
|---|---|---|
| 00.1 | [Genel bakış](../00-baslangic/01-genel-bakis.md) | Spring Boot iki şey yapar: nesneleri kurup bağlar, makul varsayılanları devreye sokar. |
| 00.2 | [Kurulum](../00-baslangic/02-kurulum.md) | Sürümleri üst proje yönetir — bağımlılığa `<version>` yazma. |
| 1.1 | [Container](../01-container-ve-bean/1.1-container-nedir.md) | **Bean olmak, var olmak değildir.** Component scan ana sınıfın paketinden aşağı iner. |
| 1.2 | [Bean tanımlama](../01-container-ve-bean/1.2-bean-tanimlama.md) | Kendi sınıfın → `@Component` ailesi. Başkasının sınıfı → `@Bean` metodu. |
| 1.3 | [Dependency injection](../01-container-ve-bean/1.3-dependency-injection.md) | Container **önce türe** bakar. 0 aday = "bulunamadı", 2+ aday = "seçilemedi". |
| 1.4 | [Bean lifecycle](../01-container-ve-bean/1.4-bean-lifecycle.md) | **Constructor'da atama yap, iş yapma.** İş `@PostConstruct`'ta. |
| 1.5 | [Scope](../01-container-ve-bean/1.5-scope.md) | Singleton bean'in alanına istekten isteğe değişen hiçbir şey yazma. |
| 2.1 | [Stereotype'lar](../02-anotasyon-haritasi/2.1-stereotype.md) | `@Service`'in teknik etkisi **yok**, `@Repository`'nin **var** (exception translation). |
| 2.2 | [`@Configuration`](../02-anotasyon-haritasi/2.2-configuration.md) | `@Configuration` sınıfının alt sınıfı üretilir; `@Bean` çağrısı container'dan gelir. |
| 2.3 | [Auto-configuration](../02-anotasyon-haritasi/2.3-auto-configuration.md) | Sihir değil, koşullu `@Bean` listesi. `--debug` ile oku. |
| 2.4 | [Starter'lar](../02-anotasyon-haritasi/2.4-starterlar.md) | `spring-boot-starter-web` → **`-webmvc`**; test starter'ı teknoloji başına. |
| 3.1 | [Öncelik sırası](../03-yapilandirma/3.1-oncelik-sirasi.md) | **Dışarıdan gelen, içeride yazılanı ezer.** |
| 3.2 | [`@ConfigurationProperties`](../03-yapilandirma/3.2-configuration-properties.md) | `@Validated` yoksa kısıtlar **sessizce** yok sayılır. |
| 3.3 | [Profiller](../03-yapilandirma/3.3-profiller.md) | Profil dosyası ana dosyanın **üzerine biner**, yerine geçmez. |
| 4.1 | [Request mapping](../04-web-katmani/4.1-request-mapping.md) | `@Valid` olmadan kısıtlar çalışmaz. 404 ≠ 405 ≠ 415. |
| 4.2 | [Exception handling](../04-web-katmani/4.2-exception-handling.md) | Hata yanıtı bir **sözleşmedir**: `type` makineye, `detail` insana. |
| 4.3 | [`RestClient`](../04-web-katmani/4.3-restclient.md) | **Varsayılan timeout yoktur.** Sen koyacaksın. |
| 5.1 | [Slice test](../05-test-temelleri/5.1-slice-test.md) | Yavaş test paketi = çalıştırılmayan test paketi. |
| 5.2 | [`@MockitoBean`](../05-test-temelleri/5.2-mockitobean.md) | `@Mock` container'a dokunmaz; `@MockitoBean` dokunur. |

---

## En çok karıştırılan ikililer ⭐

| A | B | Ayıran kelime |
|---|---|---|
| `@Component` | `@Bean` | **Sınıfa** / `@Configuration` içindeki **metoda** |
| `@Service` | `@Repository` | Teknik etki **yok** / **var** (exception translation) |
| `@Controller` | `@RestController` | Dönen `String` = **view name** / **gövde** |
| `@Qualifier` | `@Primary` | **Tüketende** seçer / **üretende** varsayılan ilan eder |
| "could not be found" | "required a single bean, but N were found" | **0 aday** / **2+ aday** — çözümleri zıt |
| Constructor | `@PostConstruct` | Nesne **eksik** / **tam** |
| `singleton` | `prototype` | Container başına 1 / **her istendiğinde** yeni |
| `@Configuration` | `@Configuration(proxyBeanMethods=false)` | Ölçüldü: `1,1` / `6,7` |
| `@Configuration` | `@Component` + `@Bean` | Proxy **var** / **yok** (sessiz tuzak) |
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` | Eski (kullanımdan kaldırıldı) / **yeni** |
| 404 | 405 | Adres yok / **metot** yanlış (+ `Allow:` başlığı) |
| 400 | 415 | Gövde geçersiz / `Content-Type` desteklenmiyor (gövdeye bakılmadı) |
| `type` | `detail` | **Sabit**, makine okur / olaya özgü, insan okur |
| `.body()` | `.toEntity()` | Yalnız gövde / gövde + **durum** + başlıklar |
| `@Mock` | `@MockitoBean` | Container'a **dokunmaz** / **dokunur** |
| `@WebMvcTest` | `@SpringBootTest` | 0,174 sn, servis **mock** / 1,083 sn, servis **gerçek** |
| `spring.profiles` | `spring.config.activate.on-profile` | **Ölü** (2.4'ten beri) / çalışan |

---

## Ezberlenecek sayılar ve değerler

| Ne | Değer |
|---|---|
| Spring Boot / Framework | **4.1.1** / **7.0.x** |
| En düşük Java | **17** (kursta 21) |
| Varsayılan port | **8080** |
| `server.shutdown` | **`graceful`** (Boot 3'te `immediate` idi) |
| `spring.lifecycle.timeout-per-shutdown-phase` | **30s** |
| `spring.mvc.problemdetails.enabled` | **false** |
| `RestClient` timeout | **Yok** — sen koyacaksın |
| Ölçülen slice / full test | **0,174 sn** / **1,083 sn** |
| Boot 4.1.1 ile gelen | JUnit Jupiter **6.0.3** · Mockito **5.23.0** · AssertJ **3.27.7** · Tomcat **11.0.24** |
| Testte geçerli sınıf adı | `*Test`, `Test*`, `*Tests`, `*TestCase` |

---

## Hata mesajı → sebep

| Mesaj | Sebep | Konu |
|---|---|---|
| `required a bean of type '...' that could not be found` | Bean yok ya da component scan dışındaki pakette | 1.1, 2.1 |
| `required a single bean, but N were found` | Belirsizlik — `@Qualifier`/`@Primary` | 1.3 |
| `The dependencies ... form a cycle` | Tasarım sorunu — ortak parçayı ayır | 1.3 |
| `A bean with that name has already been defined` | Ad çakışması | 1.2 |
| `@Configuration class '...' may not be final` | Proxy üretilemiyor (kutu **yok**, düz stack trace) | 2.2 |
| `Failed to configure a DataSource` | JDBC classpath'te, adres yok | 2.3 |
| `Could not resolve placeholder '...'` | `@Value` var, ayar ve varsayılan yok | 3.1 |
| `Binding to target ... failed` + `Origin:` | Ayar doğrulaması — dosya:satır:sütun verir | 3.2 |
| `415` + `Accept:` başlığı | `Content-Type` eksik | 4.1 |
| `405` + `Allow:` başlığı | HTTP metodu yanlış | 4.1 |
| `HttpClientErrorException$NotFound` | Dış servis 404 döndü | 4.3 |
| `No qualifying bean of type 'MockMvcTester'` | `@AutoConfigureMockMvc` eksik (Boot 4) | 5.1 |
| `cannot find symbol: class MockBean` | Boot 3 örneği — `@MockitoBean` yaz | 5.2 |
| `package javax.annotation does not exist` | `jakarta.annotation` olacak | 1.4 |
| **Hata yok ama yanlış çalışıyor** | Aşağıdaki sessiz hatalar listesine bak | — |

---

## ⚠️ Sessiz hatalar — hiçbiri hata vermez

Bu listeyi ayrı tut. Hepsi çalışır görünür, hiçbiri uyarmaz:

| Yaptığın | Olan |
|---|---|
| Controller'a `@Component` yazmak | Uç nokta 404 verir, günlükte iz yok |
| `@Bean` metodunu `@Component` sınıfına koymak | Singleton garantisi kaybolur (`lite:3,4`) |
| `@Repository` yerine `@Component` | Ham `SQLException` yukarı çıkar |
| Ortam değişkenini `SIPARIS_MAGAZA_ADI` yazmak | Ayar yok sayılır (tire **silinir**: `SIPARIS_MAGAZAADI`) |
| Profil adını yanlış yazmak | Profil hiç uygulanmaz |
| `@Validated` yazmadan kısıt koymak | Kısıtlar denetlenmez |
| İç içe nesnede `@Valid` unutmak | İç kısıtlar atlanır |
| `@Valid @RequestBody` yerine sadece `@RequestBody` | Geçersiz veri içeri girer |
| Eski paket adıyla `spring.autoconfigure.exclude` | Dışlama tutmaz |
| Aynı klasörde `.properties` + `.yaml` | `.properties` kazanır |
| Test sınıfına `...Testi` adı vermek | Test **hiç çalışmaz**, yapı yeşil kalır |
| Stub'lanmamış mock metodu | `null`/`0`/`false` döner, test yanlış şeyi ölçer |
| `RestClient`'e timeout koymamak | Karşı taraf donunca senin servisin de donar |
| Exception'ı yutup `null` dönmek | "Yok" ile "çökmüş" aynı şeye dönüşür |

---

## Sık kullanılan komutlar

```bash
./mvnw spring-boot:run                                  # çalıştır
./mvnw test                                             # testler
./mvnw dependency:tree                                  # bağımlılık ağacı
./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug   # koşul raporu
SPRING_PROFILES_ACTIVE=uretim ./mvnw spring-boot:run    # profille çalıştır
lsof -i :8080                                           # portu kim tutuyor
```

Koşul raporunda arama:

```bash
sed -n '/Negative matches:/,/Exclusions:/p' acilis.log   # NEDEN devreye girmedi
```

---

## Anotasyon hızlı bakış

```java
@SpringBootApplication      // = @Configuration + @ComponentScan + @EnableAutoConfiguration
@Component @Service @Repository @Controller @RestController    // stereotype'lar
@Configuration @Bean                                           // elle bean
@Qualifier("ad") @Primary @Lazy @Order(1) @Scope("prototype")  // seçim ve scope
@PostConstruct @PreDestroy                                     // jakarta.annotation
@ConfigurationProperties(prefix="...") @Validated @EnableConfigurationProperties
@Profile("uretim") @Profile("!uretim")
@RequestMapping @GetMapping @PostMapping @PathVariable @RequestParam @RequestBody @Valid
@RestControllerAdvice @ExceptionHandler
@WebMvcTest @SpringBootTest @AutoConfigureMockMvc @MockitoBean @MockitoSpyBean
```

---

➡️ **Sonraki adım:** [`son-tekrar.md`](son-tekrar.md) — sınavdan önce okunacak dosya.
➡️ **Sonra:** [Genel sınav 1](genel-sinav-1.html) · [Genel sınav 2](genel-sinav-2.html)
