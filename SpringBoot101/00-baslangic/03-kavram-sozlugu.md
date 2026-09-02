# Kavram sözlüğü

> **Bu dosya baştan sona okunmak için değil, dönülmek için var.** Her terim konu
> dosyasında, ilk geçtiği yerde zaten açıklanıyor. Buraya "neydi bu ya?" dediğinde
> dönersin.
>
> Her girdinin sonunda o terimin **derinlemesine işlendiği konu** yazıyor. Terimi
> öğrenmek istiyorsan tanımı burada okur, konuya gidersin.
>
> Bir ayarın **değerini seçerken** buraya değil, [ayar rehberine](04-ayar-rehberi.md)
> bak: sözlük *"bu ne demek?"* sorusunu, rehber *"ne zaman ve hangi değeri?"*
> sorusunu cevaplar.

**Altı bölüm:** [Container, bean ve dependency injection](#a-container-bean-ve-dependency-injection) ·
[Spring Boot mekanizması](#b-spring-boot-mekanizması) ·
[Yapılandırma](#c-yapılandırma) ·
[Web katmanı](#d-web-katmanı) ·
[Test](#e-test) ·
[Anotasyon dizini](#f-anotasyon-dizini)

---

## A. Container, bean ve dependency injection

#### Container

Nesnelerini senin yerine kuran, bağımlılıklarını çözen, bir haritada tutan ve uygulama
kapanırken temizleyen Spring parçası. Uygulama açılırken bir kere çalışır; açılıştan sonra
yeni [bean](#bean) kurmaz.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md)

#### ApplicationContext

[Container'ın](#container) koddaki karşılığı olan arayüz. `SpringApplication.run(...)` sana
onu döndürür; `getBean`, `getBeanDefinitionCount`, `getBeansOfType` ile sorgulanabilen
sıradan bir Java nesnesidir.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md)

#### Bean

[Container'ın](#container) kurup yönettiği nesne. Özel bir Java türü değildir; onu bean
yapan tek şey container'ın onu tanımasıdır. ⚠️ `new` ile kurduğun nesne bean **değildir** —
Spring ona injection yapmaz, anotasyonlarını işlemez.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md)

#### Bean definition

[Bean'in](#bean) tarifi: hangi sınıf, hangi ad, hangi [scope](#scope), hangi bağımlılıklar.
Tarif tektir; ondan kaç nesne çıkacağı scope'a bağlıdır — bu yüzden
`getBeanDefinitionCount()` nesne değil **tarif** sayar.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md)

#### Dependency injection

Bir sınıfın ihtiyaç duyduğu nesneleri kendi içinde kurmayıp dışarıdan alması. Fikrin
kendisi constructor'dadır ve Spring'siz de geçerlidir; anotasyon yalnızca Spring'e haber
verme biçimidir.
→ [00.1](01-genel-bakis.md) · [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### Constructor injection

Bağımlılıkların constructor parametresi olarak alınması — kursun tek önerdiği biçim.
Alanı `final` yapabilirsin, Spring'siz test edebilirsin ve eksik bağımlılık **açılışta**
patlar.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### Field injection

Bağımlılığın doğrudan alana `@Autowired` ile doldurulması. Kısa yazılır ama `final`
olamaz, Spring'siz test edilemez ve [circular dependency'yi](#circular-dependency) senden
saklar.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### Service locator

Uygulama kodunun içinde `container.getBean(...)` çağırma alışkanlığı — injection'ın tersi.
Sınıfı container'a bağlar ve test edilemez hâle getirir; `getBean` yalnızca öğrenirken ve
hata ararken kullanılır.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md)

#### Component scan

Container'ın, [stereotype](#stereotype) anotasyonu taşıyan sınıfları bulma işi. ⚠️ Tarama
`@SpringBootApplication` sınıfının paketinden başlar ve **aşağı iner** — kardeş paket
taranmaz.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md) · [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### Stereotype

`@Component` ve onu [meta-annotation](#meta-annotation) olarak taşıyan aile: `@Service`,
`@Repository`, `@Controller`, `@RestController`, `@Configuration`. Component scan açısından
hepsi aynıdır; farkları ek davranışlarındadır.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### Meta-annotation

Başka bir anotasyonun üzerinde duran anotasyon. `@Service`'in üzerinde `@Component` vardır;
Spring "üzerinde `@Component` olan her anotasyonu" tarar — [stereotype](#stereotype) ailesi
böyle oluşur.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### Ambiguity

Container'ın bir bağımlılık için **birden fazla** uygun aday bulup seçemediği durum. Hata
mesajı `required a single bean, but N were found` der; çözümü [@Qualifier](#qualifier),
[@Primary](#primary) ya da [list injection](#list-injection).
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### List injection

Aynı arayüzün bütün gerçeklemelerini `List<T>` ya da `Map<String,T>` olarak birden almak.
⚠️ Hiç aday yoksa Spring **boş liste** verir ve hata vermez — bu durumu kendin kontrol
etmelisin.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### Circular dependency

İki bean'in birbirini istemesi. Spring Boot 2.6'dan beri varsayılan olarak yasaktır; bir
yapılandırma sorunu değil **tasarım sorunudur** — ortak parçayı üçüncü bir sınıfa çıkar.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### Bean lifecycle

Bir bean'in geçtiği sıra: constructor → injection → [@PostConstruct](#postconstruct) →
[proxy](#proxy)'lenme → hazır → [@PreDestroy](#predestroy). Constructor'da nesne henüz
eksiktir; hazırlık işleri `@PostConstruct`'a yazılır.
→ [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md)

#### Proxy

Gerçek nesnenin yerine geçip çağrıları ona ileten, arada iş yapan wrapper. Spring
`@Configuration` sınıflarında, [scoped proxy'de](#scoped-proxy) ve `@Transactional` gibi
anotasyonlarda proxy kullanır.
→ [2.2](../02-anotasyon-haritasi/2.2-configuration.md)

#### CGLIB

Spring'in çalışma zamanında **alt sınıf üreterek** [proxy](#proxy) yapma tekniği. Stack
trace'te `$$SpringCGLIB$$` görürsen bu odur; ⚠️ alt sınıf üretildiği için hedef sınıf
`final` **olamaz**.
→ [2.2](../02-anotasyon-haritasi/2.2-configuration.md)

#### Scope

"Bu bean definition'dan kaç nesne üretilecek ve ne kadar yaşayacak" sorusunun cevabı.
Türleri: `singleton` (varsayılan), `prototype`, `request`, `session`, `application`.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### Singleton

Varsayılan [scope](#scope): container başına bir nesne, uygulama boyunca. ⚠️ Singleton
olmak thread güvenliği **vermez** — güvenliği sağlayan şey, istekten isteğe değişen
hiçbir şeyin alanda durmamasıdır.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### Prototype

Her istendiğinde yeni nesne üreten [scope](#scope). İki sürprizi var:
[@PreDestroy](#predestroy) hiç çağrılmaz ve singleton bir bean'e bir kere inject edilirse
[scope uyumsuzluğu](#scope-mismatch) yaşarsın.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### Scope mismatch

Dar scope'lu bir bean'in geniş scope'lu bir bean'in içine sıkışıp onun ömrünü devralması.
Belirtisi: "prototype yaptım ama hep aynı nesne geliyor" ya da
`Scope 'request' is not active for the current thread`.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### Scoped proxy

[Scope uyumsuzluğunun](#scope-mismatch) çözümü: inject edilen şey gerçek nesne değil, her
çağrıda container'dan doğru nesneyi isteyen ince bir [proxy'dir](#proxy).
Yazımı `@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)`.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### ObjectProvider

[Scoped proxy'ye](#scoped-proxy) alternatif: container'dan **ihtiyaç anında** nesne isteyen
bir wrapper (`getObject()`, `getIfAvailable()`). Farkı, değişikliğin üreten tarafta değil
**tüketen** tarafta ve kodda görünür olmasıdır.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### Unchecked exception

`RuntimeException` altındaki, `throws` yazmayı zorunlu kılmayan exception'lar. `@Repository`
kütüphaneye özgü exception'ları (`SQLException` gibi) Spring'in unchecked
`DataAccessException` ailesine çevirir; kendi exception'larını da unchecked yaz.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md) · [4.2](../04-web-katmani/4.2-exception-handling.md)

#### Graceful shutdown

Uygulamanın `SIGTERM` aldıktan sonra yeni istek kabul etmeyip devam edenleri bitirerek
kapanması. Spring Boot 4'te **varsayılan** davranıştır (`server.shutdown=graceful`);
3.x'te varsayılan `immediate` idi.
→ [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md) · [ayar rehberi](04-ayar-rehberi.md#kapanış-ve-lifecycle)

#### Failure analyzer

Spring Boot'un açılış hatalarını `APPLICATION FAILED TO START` kutusuna çevirip
`Description` ve `Action` başlıklarıyla ne yapman gerektiğini yazan parçası. ⚠️ Her hata
kutuya girmez — girmediğinde stack trace'in **en üstteki** `Caused by` satırını oku.
→ [00.2](02-kurulum.md) · [2.2](../02-anotasyon-haritasi/2.2-configuration.md)

#### CommandLineRunner

Container hazır olduğunda bir kere çalışan arayüz. Açılışta bir iş yaptırmak ya da bu
kursta olduğu gibi bir denemeyi tetiklemek için kullanılır.
→ [4.3](../04-web-katmani/4.3-restclient.md)

---

## B. Spring Boot mekanizması

#### Spring Framework

Container, injection, web katmanı, transaction ve AOP'yi sağlayan çekirdek kütüphaneler
kümesi. Bu kursun sürümü **7.0.x**; öğrendiğin container ve bean bilgisi Boot sürümü
değişse de geçerliliğini korur.
→ [00.1](01-genel-bakis.md)

#### Spring Boot

[Spring Framework'ü](#spring-framework) senin yerine yapılandıran katman. Üç katkısı var:
[auto-configuration](#auto-configuration), [starter'lar](#starter) ve
[embedded server](#embedded-server). Bu kursun sürümü **4.1.x**.
→ [00.1](01-genel-bakis.md)

#### Auto-configuration

Classpath'te ne olduğuna bakıp senin yerine bean kuran mekanizma. Sihir değil, koşullu
`@Bean` metotlarından oluşan uzun bir listedir — ve her kararı
[raporda](#koşul-değerlendirme-raporu) okunabilir.
→ [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md)

#### Koşul anotasyonları

Bir `@Bean` metodunun ne zaman devreye gireceğini belirleyen aile: `@ConditionalOnClass`,
`@ConditionalOnMissingClass`, `@ConditionalOnBean`,
[@ConditionalOnMissingBean](#conditionalonmissingbean), `@ConditionalOnProperty`,
`@ConditionalOnBooleanProperty`, `@ConditionalOnWebApplication`, `@ConditionalOnResource`.
→ [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md)

#### Koşul değerlendirme raporu

`--debug` bayrağıyla açılışta basılan **CONDITIONS EVALUATION REPORT**. Dört bölümü var:
`Positive matches`, `Negative matches`, `Exclusions`, `Unconditional classes`. ⚠️ Bir şey
**çalışmıyorsa** cevap neredeyse her zaman `Negative matches` bölümünde tek satırdadır.
→ [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md) · [ayar rehberi](04-ayar-rehberi.md#varsayılanı-bilmiyorsan-sisteme-sor)

#### Starter

İçinde **sınıf olmayan**, yalnızca uyumlu bağımlılıkları listeleyen bir `pom.xml` taşıyan
paket. Tek satır yazarsın, birbiriyle test edilmiş onlarca kütüphane gelir.
→ [2.4](../02-anotasyon-haritasi/2.4-starterlar.md)

#### Modül bölünmesi

Spring Boot 4'ün devasa `spring-boot-autoconfigure` jar'ını teknoloji başına modüllere
ayırması. ⚠️ Paket adlarında `autoconfigure` kelimesi **sona** taşındı:
`org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration`.
→ [2.4](../02-anotasyon-haritasi/2.4-starterlar.md)

#### Classic starterlar

`spring-boot-starter-classic` ve `spring-boot-starter-test-classic` — eski toplu davranışı
geri getiren paketler. Adından belli: bunlar **geçiş aleti**, varış noktası değil; yeni
projede kullanma.
→ [2.4](../02-anotasyon-haritasi/2.4-starterlar.md)

#### Embedded server

Web sunucusunun uygulamanın **içinde** çalışması: `java -jar` ile başlayan sıradan bir Java
programı yazarsın, dışarıya kurulmuş bir sunucuya `.war` atmazsın. Varsayılanı Tomcat;
⚠️ Undertow Spring Boot 4'te kaldırıldı.
→ [00.1](01-genel-bakis.md)

#### Proxy modlari

`@Bean` metotlarının nasıl davrandığını belirleyen üç mod: **full mode** (`@Configuration` —
metot ikinci çağrıda container'dan alır), **proxysiz mod** (`proxyBeanMethods = false` — yeni
nesne kurar), **lite mode** (`@Component` + `@Bean` — yeni nesne kurar, ⚠️ sessiz tuzak).
→ [2.2](../02-anotasyon-haritasi/2.2-configuration.md)

#### proxyBeanMethods

`@Configuration` anotasyonunun, [CGLIB](#cglib) proxy'sinin üretilip üretilmeyeceğini
belirleyen özelliği (varsayılan `true`). ⚠️ `false` yaparsan `@Bean` metotları birbirini
çağırdığında singleton garantisi bozulur.
→ [2.2](../02-anotasyon-haritasi/2.2-configuration.md)

#### Jackson 3

Spring Boot 4'ün JSON kütüphanesi; paketi **`tools.jackson`**. Spring Boot 3'te Jackson 2
ve `com.fasterxml.jackson` idi — internetteki her örnek eski paketi kullanıyor.
→ [2.4](../02-anotasyon-haritasi/2.4-starterlar.md) · [4.1](../04-web-katmani/4.1-request-mapping.md)

#### Maven wrapper

Projeye gömülü `./mvnw` betiği; Maven'ı doğru sürümüyle kendisi indirir. Böylece projeyi
taşıdığın makinedeki Maven sürümü ne olursa olsun aynı derleme çıkar.
→ [00.2](02-kurulum.md)

---

## C. Yapılandırma

#### Öncelik sırası

Aynı ayarın birçok kaynaktan okunması ve aralarındaki kesin sıralama. Tek cümlesi:
**dışarıdan gelen, içeride yazılanı ezer** — komut satırı → sistem özellikleri → ortam
değişkenleri → profil dosyası → ana dosya → varsayılanlar.
→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md) · [ayar rehberi](04-ayar-rehberi.md#ayarı-nereye-yazarsın-öncelik-sırası)

#### Relaxed binding

Spring'in aynı ayarı birden çok yazımdan tanıması: `siparis.magaza-adi` ≡
`siparis.magazaAdi` ≡ `SIPARIS_MAGAZAADI`. ⚠️ Ortam değişkeninde kural ayrı: **nokta `_`
olur, tire silinir** — `SIPARIS_MAGAZA_ADI` eşleşmez ve hata da vermez.
→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md) · [ayar rehberi](04-ayar-rehberi.md#ortam-değişkeni-yazım-kuralı)

#### Environment

Bütün ayar kaynaklarını birleştirip tek yerden okutan Spring nesnesi
(`container.getEnvironment().getProperty("...")`). Hangi profillerin açık olduğunu da o
tutar.
→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md)

#### PropertySource

[Environment](#environment) içindeki tek bir ayar kaynağı: `commandLineArgs`,
`systemProperties`, `systemEnvironment`, `application.properties`… Listenin sırası
doğrudan [öncelik sırasıdır](#öncelik-sırası) — üstteki kazanır.
→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md)

#### Placeholder

`@Value("${siparis.magaza-adi}")` yazımındaki `${...}` ifadesi. Karşılığı yoksa ve
varsayılan verilmediyse uygulama `Could not resolve placeholder` diyerek **açılmaz** — bu
iyi bir hatadır.
→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md)

#### Prefix

`@ConfigurationProperties(prefix = "siparis")` içindeki, o sınıfa bağlanacak ayar ailesinin
adı. ⚠️ **kebab-case** yazılır: `siparis-ayarlari` çalışır, `siparisAyarlari` çalışmaz.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md)

#### Profil

"Bu ayar kümesi yalnızca şu ortamda geçerli" demenin yolu. Profil dosyası ana dosyanın
**yerine geçmez, üzerine biner** — yazmadığı anahtarlar tabandan gelmeye devam eder.
⚠️ Yanlış yazılan profil adı hata vermez, sessizce yok sayılır.
→ [3.3](../03-yapilandirma/3.3-profiller.md) · [ayar rehberi](04-ayar-rehberi.md#profiller)

#### Ayar doğrulaması

`@Validated` + kısıtlarla ayarların **açılışta** denetlenmesi. Çıktısı hatalı ayarların
hepsini birden, `Origin:` satırıyla dosya:satır:sütun bilgisiyle verir — sessiz ayar
hatalarına karşı en somut savunma.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md)

---

## D. Web katmanı

#### Endpoint

Bir HTTP adresi ve metodu ile eşlenmiş kontrolcü metodu. Parametreleri istekten doldurulur,
dönüş değeri gövdeye yazılır — iki çeviri de sessizce yanlış gidebilir, bu yüzden
[@Valid](#valid) şart.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### DTO

*Data Transfer Object* — dış dünyayla konuşmak için yazılan, genelde `record` olan sınıf.
⚠️ Yerine [entity](#entity) sınıfı döndürürsen veritabanı şeman API sözleşmesine dönüşür.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### Entity

Veritabanı tablosuna karşılık gelen sınıf. Kontrolcüden doğrudan döndürülmez; araya bir
[DTO](#dto) konur.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### ProblemDetail

[RFC 9457](#rfc-9457) hata gövdesinin Spring'deki karşılığı olan sınıf. Alanları: `type`
(sabit, makine okur), `title`, `status`, `detail` (olaya özgü, insan okur), `instance`;
`setProperty` ile kendi alanlarını da eklersin.
→ [4.2](../04-web-katmani/4.2-exception-handling.md)

#### RFC 9457

HTTP hata gövdelerinin standardı (eski adıyla RFC 7807). Hata yanıtını bir **sözleşmeye**
çevirir: istemci `type` alanına bakarak dallanır, `detail` metnine göre değil.
→ [4.2](../04-web-katmani/4.2-exception-handling.md)

#### problem+json

`application/problem+json` içerik türü — [ProblemDetail](#problemdetail) döndüren
yanıtların `Content-Type` başlığı. İstemci başlığa bakarak "bu bir hata gövdesi"
diyebilir.
→ [4.2](../04-web-katmani/4.2-exception-handling.md)

#### RestClient

Spring Framework 7'de **önerilen** HTTP istemcisi; zincirleme (fluent) arayüzü vardır ve
hata durumlarını exception'a çevirir. ⚠️ Varsayılan timeout **yoktur**; sen koymalısın.
→ [4.3](../04-web-katmani/4.3-restclient.md)

#### RestClient.Builder

[RestClient](#restclient) kuran ve auto-configuration ile hazır gelen kurucu.
⚠️ Onu **inject et**; `RestClient.builder()` ile sıfırdan kurarsan timeout ve
observability ayarlarını kaybedersin ve bunu fark etmezsin.
→ [4.3](../04-web-katmani/4.3-restclient.md)

#### WebClient

Reaktif ve akış (streaming) işleri için kullanılan HTTP istemcisi. Kaldırılmıyor; ama
bloklayan sıradan çağrılar için [RestClient](#restclient) kullanılır.
→ [4.3](../04-web-katmani/4.3-restclient.md)

#### RestTemplate

Spring'in eski HTTP istemcisi. Şu an `@Deprecated` **değil**; resmî plana göre 7.1'de
işaretlenecek, 8.0'da kaldırılacak. Yeni kodda [RestClient](#restclient) yaz.
→ [4.3](../04-web-katmani/4.3-restclient.md)

#### RestClientResponseException

`retrieve()` başarısız bir durum kodu gördüğünde attığı exception ailesinin üst sınıfı:
4xx için `HttpClientErrorException`, 5xx için `HttpServerErrorException`.
`getResponseBodyAsString()` ile karşı tarafın hata gövdesini okuyabilirsin.
→ [4.3](../04-web-katmani/4.3-restclient.md)

#### Connect timeout

TCP bağlantısı **kurulana** kadar beklenecek süre (`spring.http.clients.connect-timeout`).
Her zaman [read timeout'tan](#read-timeout) küçük olmalı: bağlantı kurulamıyorsa karşı
taraf zaten ayakta değildir.
→ [4.3](../04-web-katmani/4.3-restclient.md) · [ayar rehberi](04-ayar-rehberi.md#dışarıya-çağrı-restclient)

#### Read timeout

İlk yanıt baytı gelene kadar beklenecek süre (`spring.http.clients.read-timeout`).
⚠️ Varsayılanı yoktur — koymazsan karşı taraf donduğunda thread'in sonsuza kadar bekler.
→ [4.3](../04-web-katmani/4.3-restclient.md) · [ayar rehberi](04-ayar-rehberi.md#dışarıya-çağrı-restclient)

---

## E. Test

#### Test piramidi

Testlerin dağılım kuralı: çok **birim** testi (Spring hiç yok), orta miktarda
[slice test](#slice-test), az sayıda tam container testi. En büyük hız kazancı iş mantığını
Spring'den ayırmaktan gelir.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### Slice test

Container'ın yalnızca ilgili katmanını kuran test: `@WebMvcTest`, `@DataJpaTest`,
`@JsonTest`, `@RestClientTest`, `@JdbcTest`. Ölçüldü: `@WebMvcTest` 0,174 sn,
`@SpringBootTest` 1,083 sn. ⚠️ Slice anotasyonları birleştirilemez.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### Context cache

Spring'in aynı yapılandırmaya sahip testler için container'ı bir kere kurup yeniden
kullanması. ⚠️ `@ActiveProfiles`, `@TestPropertySource`, farklı [mock](#mock) kümesi ve
`@DirtiesContext` yeni bir container demektir.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### Mock

Gerçek nesnenin yerine geçen, davranışını senin belirlediğin test nesnesi. ⚠️
Mock'lanmamış bir metot varsayılan değer döner (`null` / `0` / `false`) — testin sessizce
yanlış şeyi ölçebilir.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### Spy

Gerçek nesneyi **saran** test nesnesi: mock'lamadığın metotlar gerçek kodu çalıştırmaya
devam eder. Spring'deki karşılığı [@MockitoSpyBean](#mockitospybean).
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### MockMvc

Web sunucusu ayağa kaldırmadan kontrolcüye istek atmayı sağlayan eski test aracı
(`perform(...).andExpect(...)`). Hâlâ çalışır; yeni kodda
[MockMvcTester](#mockmvctester) kullanılır.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### MockMvcTester

Spring Framework 7 ile gelen, AssertJ tabanlı test aracı. Kazancı üç maddede: uzun statik
`import` listesi yok, `throws Exception` yok, hata çıktısı ayrıntılı.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### Surefire kalıbı

Maven'ın hangi sınıfları test sayacağını belirleyen ad kalıbı: `*Test`, `Test*`, `*Tests`,
`*TestCase`. ⚠️ `SiparisTesti` hiçbirine uymaz — test **sessizce hiç çalışmaz**, yapı yine
de yeşil görünür.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

---

## F. Anotasyon dizini

> Anotasyonların **ne yaptığı** burada, hangisini ne zaman seçeceğin konu dosyalarında.
> Bir anotasyonu ilk kez görüyorsan önce buraya, sonra konuya bak.

### Container ve bean

#### @SpringBootApplication

Üç anotasyonun kısaltması: `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.
⚠️ Bu sınıf **en üst pakette** durmalı — [component scan](#component-scan) onun paketinden
aşağı iner.
→ [1.1](../01-container-ve-bean/1.1-container-nedir.md)

#### @ComponentScan

[Component scan'i](#component-scan) açan anotasyon. Başlangıç noktasını
`basePackages` ile ya da `@SpringBootApplication(scanBasePackages = ...)` ile
değiştirebilirsin — ama bu ihtiyaç genelde paket düzeninin bozuk olduğunun işaretidir.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### @EnableAutoConfiguration

[Auto-configuration'ı](#auto-configuration) açan anotasyon. `exclude` parametresiyle
tek tek yapılandırma sınıflarını dışlayabilirsin — son çare olarak.
→ [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md)

#### @Configuration

`@Bean` metotlarını barındıran sınıfı işaretler ve o sınıfın [CGLIB](#cglib) proxy'sini
ürettirir. ⚠️ Proxy üretildiği için sınıf `final` **olamaz** — Kotlin'de sık başa gelir.
→ [2.2](../02-anotasyon-haritasi/2.2-configuration.md)

#### @Component

Genel amaçlı [stereotype](#stereotype); diğerlerinin hepsi bunu
[meta-annotation](#meta-annotation) olarak taşır. Rolü hiçbirine uymayan yardımcı
sınıflarda kullan.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### @Service

İş kuralları taşıyan sınıflar için [stereotype](#stereotype). ⚠️ **Teknik bir etkisi
yoktur** — `@Component` ile aynı davranır; okuyan insana bilgi verir.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### @Repository

Veri erişim sınıfları için [stereotype](#stereotype). ⭐ Teknik etkisi **vardır**:
kütüphaneye özgü exception'ları Spring'in [unchecked](#unchecked-exception)
`DataAccessException` ailesine çevirir.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### @Controller

Web katmanının `@RequestMapping` metotlarını aradığı [stereotype](#stereotype). ⚠️ Dönen
`String` bir **view name** sayılır — JSON döndürmek istiyorsan
[@RestController](#restcontroller) ya da metoda [@ResponseBody](#responsebody).
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### @RestController

`@Controller` + `@ResponseBody`. JSON dönen endpoint'ler için kullanılır. ⚠️ Kontrolcüne
`@Component` yazarsan uygulama açılır ama endpoint 404 verir.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md) · [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @Bean metodu

Nesneyi senin kurup container'a verdiğin yol; bean'in **adı metodun adı**, türü metodun
dönüş türüdür. Üç durumda şart: sınıf senin değil, kurulum mantık istiyor, aynı türden
birden çok nesne lazım.
→ [1.2](../01-container-ve-bean/1.2-bean-tanimlama.md)

#### @Autowired

Injection noktasını işaretler. ⚠️ Sınıfta **tek constructor varsa yazmana gerek yok** —
Spring 4.3'ten beri gereksiz; eski örneklerde göreceksin.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### @Qualifier

[Ambiguity'de](#ambiguity) hangi bean'in isteneceğini **tüketen tarafta** ada göre seçer.
Okuyan kişi hangi bean'in geldiğini kodda görür — parametre adına güvenmekten çok daha
sağlamdır.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### @Primary

Aynı türden birden çok bean varsa "belirtilmediyse bunu kullan" der; **üreten tarafa**
yazılır ve container genelinde geçerlidir. Bir yerde kolaylık, on yerde sürpriz olabilir.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### @Order

[List injection'da](#list-injection) sırayı belirler; küçük sayı önce gelir. Yazmazsan
sıra garanti değildir.
→ [1.3](../01-container-ve-bean/1.3-dependency-injection.md)

#### @Lazy

Bean'in kurulumunu ilk kullanıma erteler. Bir iyileştirme değil **takastır**: açılış
hızını, hatayı açılışta görme güvencesiyle değişirsin.
→ [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md)

#### @Scope anotasyonu

Bean'in [scope'unu](#scope) belirler. `proxyMode` parametresiyle
[scoped proxy](#scoped-proxy) de buradan açılır:
`@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)`.
→ [1.5](../01-container-ve-bean/1.5-scope.md)

#### @PostConstruct

Injection bittikten sonra çağrılan hazırlık metodu; paketi **`jakarta.annotation`**
(`javax` değil). Kural: constructor'da atama yap, iş yapma — iş buraya gelir.
→ [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md)

#### @PreDestroy

Kapanışta çağrılan temizlik metodu; paketi **`jakarta.annotation`**. ⚠️ `kill -9`
alındığında ve [prototype](#prototype) bean'lerde **çağrılmaz** — veri güvenliği
için kullanma, yalnızca kaynak bırak.
→ [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md)

#### @ConditionalOnMissingBean

[Koşul anotasyonlarının](#koşul-anotasyonları) en önemlisi: "bu türden bean yoksa kur".
Senin tanımladığın bean'in Spring'inkini ezmesini sağlayan mekanizma budur.
⚠️ Kendi yapılandırma sınıfında kullanma — değerlendirme sırası garanti değildir.
→ [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md)

### Yapılandırma

#### @Value

Tek bir ayarı [placeholder'la](#placeholder) okur: `@Value("${siparis.magaza-adi:Merkez}")`.
Kuralı: 1–2 ayar için `@Value`, 3 ve üstü için
[@ConfigurationProperties](#configurationproperties).
→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md)

#### @ConfigurationProperties

Bir ayar ailesini tek bir nesneye bağlar; [relaxed binding](#relaxed-binding) ve tip
dönüşümü (`45s` → `Duration`) kendiliğinden çalışır. `record` ile kullanırsan ayar nesnesi
değişmez olur.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md)

#### @EnableConfigurationProperties

[@ConfigurationProperties](#configurationproperties) sınıfını tek tek listeleyerek bean
yapar. `record` ayar sınıfında `@Component` **çalışmaz** — bunu ya da
[@ConfigurationPropertiesScan](#configurationpropertiesscan) kullan.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md)

#### @ConfigurationPropertiesScan

`@ConfigurationProperties` işaretli **bütün** sınıfları tarar. Çok sayıda ayar sınıfın
varsa tek tek listelemekten kurtarır.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md)

#### @Validated

Ayar sınıfındaki kısıtları çalıştıran anotasyon. ⚠️ Yazmazsan kısıtlar orada durur ama
**hiç denetlenmez** — "doğrulama koydum sandım" hatasının kaynağı budur.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md)

#### @Valid

İki yerde kullanılır: istek gövdesinde kısıtları çalıştırır (`@Valid @RequestBody`) ve
ayar sınıfında **iç içe** nesnenin kısıtlarını açar. ⚠️ İkisinde de unutulursa doğrulama
sessizce atlanır. Kısıtlar: `@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Positive`, `@Size`,
`@Pattern`, `@Email`.
→ [3.2](../03-yapilandirma/3.2-configuration-properties.md) · [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @Profile

Bir bean'in yalnızca belirli [profillerde](#profil) kurulmasını sağlar.
İfadeleri: `"uretim"`, `"!uretim"`, `{"test","yerel"}`, `"uretim & izleme"`.
⚠️ Bir güvenlik aracı **değildir** — yanlış profil ayarı korumayı kaldırır.
→ [3.3](../03-yapilandirma/3.3-profiller.md)

### Web

#### @RequestMapping

Bir adresi kontrolcü metoduyla eşler; sınıf düzeyinde yazıldığında ortak önek olur.
Kısayolları: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`,
`@DeleteMapping`.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @PathVariable

Değeri **adresin içinden** okur: `/siparisler/{no}` → `S-100`.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @RequestParam

Değeri **sorgu dizesinden** okur: `?durum=yeni`. ⚠️ `required = false` ile ilkel tür
kullanma — `Integer` yaz ya da `defaultValue` ver.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @RequestBody

İstek gövdesindeki JSON'u bir nesneye çevirir. Doğrulama istiyorsan başına
[@Valid](#valid) yaz.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @RequestHeader

Değeri HTTP başlığından okur: `X-Izleme-No` gibi.
→ [4.1](../04-web-katmani/4.1-request-mapping.md)

#### @ResponseBody

"Bu metodun dönüşü view name değil, gövdenin kendisi" der.
[@RestController](#restcontroller) bunu her metoda kendiliğinden ekler.
→ [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

#### @ControllerAdvice

Bütün kontrolcüler için ortak exception işleme sınıfını işaretler. ⚠️ `@ResponseBody`
taşımaz — JSON hata gövdesi döndüreceksen
[@RestControllerAdvice](#restcontrolleradvice) yaz.
→ [4.2](../04-web-katmani/4.2-exception-handling.md)

#### @RestControllerAdvice

`@ControllerAdvice` + `@ResponseBody`. HTTP kodu eşlemesini **tek yerde** toplamanın
yolu; her kontrolcüde `try/catch` yazmanın alternatifi.
→ [4.2](../04-web-katmani/4.2-exception-handling.md)

#### @ExceptionHandler

Belirli bir exception türünü hangi metodun karşılayacağını söyler. Dönüş türü
[ProblemDetail](#problemdetail) olduğunda yanıt kendiliğinden
[problem+json](#problemjson) olur.
→ [4.2](../04-web-katmani/4.2-exception-handling.md)

### Test

#### @SpringBootTest

Bütün container'ı ayağa kaldıran tam container testi. ⚠️ Spring Boot 4'te MockMvc ve
`TestRestTemplate` artık **kendiliğinden gelmiyor** —
[@AutoConfigureMockMvc](#autoconfiguremockmvc) yazman gerekiyor.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### @WebMvcTest

Container'ın yalnızca web slice'ını kurar: kontrolcüler, `@ControllerAdvice`,
dönüştürücüler, filtreler. ⚠️ `@Service` ve `@Repository` sınıflarını **almaz**;
bağımlılıkları [@MockitoBean](#mockitobean) ile sen mock'larsın. Paketi
`org.springframework.boot.webmvc.test.autoconfigure`.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### @AutoConfigureMockMvc

`@SpringBootTest` ile birlikte MockMvc / [MockMvcTester](#mockmvctester) bean'lerini
devreye alır. Spring Boot 4'te **zorunlu**; yazmazsan
`No qualifying bean of type 'MockMvcTester' available` alırsın.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### @AutoConfigureTestRestTemplate

`@SpringBootTest` ile `TestRestTemplate` kullanacaksan gereken anotasyon. Sınıfın paketi de
değişti: `org.springframework.boot.resttestclient`.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### @MockitoBean

Container'daki bean'i Mockito [mock'uyla](#mock) **değiştirir**. Spring Boot 3'teki
`@MockBean`'in yerini aldı; yalnızca test sınıfının alanına yazılır. Paketi
`org.springframework.test.context.bean.override.mockito`.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### @MockitoSpyBean

Container'daki gerçek bean'i [spy ile](#spy) **sarar**: mock'lamadığın metotlar gerçek kodu
çalıştırmaya devam eder. Spring Boot 3'teki `@SpyBean`'in yerini aldı.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### @MockBean

Spring Boot 3'ün mock bean anotasyonu; 4'te **yok**, `cannot find symbol` alırsın.
Karşılığı [@MockitoBean](#mockitobean).
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### @Mock anotasyonu

Mockito'nun kendi anotasyonu: mock nesne üretir ama **Spring container'ına dokunmaz**.
Container'daki bean'i değiştirmek istiyorsan [@MockitoBean](#mockitobean) kullan.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### @ExtendWith

JUnit 5'te bir eklenti bağlar. ⚠️ Spring Boot 4'te `@Mock` ve `@Captor` için
`@ExtendWith(MockitoExtension.class)` yazman gerekiyor — `MockitoTestExecutionListener`
kaldırıldı, yazmazsan alanlar `null` kalır.
→ [5.2](../05-test-temelleri/5.2-mockitobean.md)

#### @ActiveProfiles

Testin hangi [profillerle](#profil) çalışacağını söyler. ⚠️ Her farklı profil kümesi ayrı
bir [container](#context-cache) demektir.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### @TestPropertySource

Teste özel ayar değeri verir. ⚠️ [Context cache'i](#context-cache) böler — her farklı ayar
kümesi yeni bir container kurulumu.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### @DirtiesContext

Container'ı test sonrası **attırır**. Testler arası sızıntıyı bununla çözmeye çalışma; her
kullanımı bir container kurulumu demektir, asıl sorun genelde paylaşılan durumdur.
→ [5.1](../05-test-temelleri/5.1-slice-test.md)

#### @WithMockUser

Testte bir mock kullanıcı oturumu açar. ⚠️ Spring Boot 4'te
`spring-boot-starter-security-test` bağımlılığını eklemen gerekiyor; eskiden genel test
starter'ıyla geliyordu.
→ [2.4](../02-anotasyon-haritasi/2.4-starterlar.md)

#### @Transactional

Metodun bir veritabanı transaction'ı içinde çalışmasını sağlar. Bu kursta yalnızca
[proxy](#proxy) ve [unchecked exception](#unchecked-exception) örneği olarak
geçiyor; mekanizmasının tamamı **201** kursunda.
→ [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md) · [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)

---

⚙️ Bir ayarın **değerini** seçeceksen: [Ayar rehberi](04-ayar-rehberi.md)
🏠 [Kursa dön](../README.md)
