# Ayar rehberi: hangi ayar, ne zaman, hangi değer

> **Başvuru dosyası.** Baştan sona okunmaz; *"acaba buna mı dokunmalıyım?"* dediğinde
> açılır.
>
> Sözlük *"bu ne demek?"* sorusunu cevaplar. Bu dosya üç başka soruyu cevaplar:
> **Ne yapar? · Ne zaman dokunulur? · Değeri seçerken neye bakılır?**
>
> Bir terime takıldıysan [kavram sözlüğüne](03-kavram-sozlugu.md) dön.

## Bu dosya nasıl kullanılır

Her satırda **varsayılan değer** yazıyor. Varsayılanı bilmeden "dokunayım mı?" sorusu
cevaplanamaz — bir ayarı değiştirmeden önce onun şu an ne olduğunu bil.

⚠️ **Ölçmeden ayar değiştirme.** Bu kursta iki gerçek karşı örnek var:

| Ne yapıldı | Ne beklendi | Ne oldu |
|---|---|---|
| `spring.autoconfigure.exclude` satırına Spring Boot 3 paket adı yazıldı ([2.4](../02-anotasyon-haritasi/2.4-starterlar.md)) | Auto-configuration dışlanacak | **Hiçbir şey.** Hata da uyarı da yok; satır sessizce yok sayıldı |
| `SIPARIS_MAGAZA_ADI` ortam değişkeni verildi ([3.1](../03-yapilandirma/3.1-oncelik-sirasi.md)) | Dosyadaki değer ezilecek | **Ezilmedi.** Yazım kuralına uymadığı için eşleşmedi, hata yok |

İkisinin ortak dersi: **ayarı yazmak, ayarın çalıştığı anlamına gelmez.** Değişikliğin
tuttuğunu her seferinde ölç — açılış günlüğünden, `/actuator/env`'den ya da değeri
yazdırarak.

Bir değeri `⚠️ Doğrulanmalı` gördüysen: o varsayılanı kendi sürümünde teyit etmeden
kararına dayanak yapma. Nasıl soracağın
[aşağıda](#varsayılanı-bilmiyorsan-sisteme-sor).

---

## 1. Karar çerçevesi

Bir ayara dokunmadan önce beş soru. Sırayla sor; ilk "evet" seni doğru yere götürür.

| # | Soru | Evet ise |
|---|---|---|
| 1 | Bunu **kod** çözebilir mi? | Ayarla uğraşma. Kendi bean'ini tanımla — auto-configuration [@ConditionalOnMissingBean](03-kavram-sozlugu.md#conditionalonmissingbean) sayesinde geri çekilir |
| 2 | Belirtiyi **ölçtün** mü, yoksa tahmin mi ediyorsun? | Önce ölç: `--debug` raporu, açılış günlüğü, `/actuator/env` |
| 3 | Bu ayar bir **hatayı çözüyor mu, gizliyor mu**? | Gizliyorsa [tehlikeli ayarlar](#tehlikeli-ayarlar-dokunmadan-önce-iki-kez-düşün) tablosuna bak; muhtemelen tasarımı düzeltmelisin |
| 4 | Ortamdan ortama **değişiyor** mu? | Ayar dosyasına değil, [dışarıya](#ayarı-nereye-yazarsın-öncelik-sırası) yaz |
| 5 | Yanlış değer verirsen **sessiz mi kalır**? | Ayarını [doğrulamayla](../03-yapilandirma/3.2-configuration-properties.md) koru; sessiz ayar üretimde en pahalı hatadır |

---

## 2. Karar reçeteleri — senaryodan ayar setine

### Reçete A — "Dış servise çağrı yapıyorum"

Bu kursun en çok işine yarayacak reçetesi. `RestClient`'ın **varsayılan timeout'u
yoktur**; koymazsan karşı taraf donduğunda senin servisin de durur.

```properties
spring.http.clients.connect-timeout=2s
spring.http.clients.read-timeout=5s
```

**Bedeli:** Yavaş ama sağlıklı yanıtlar da kesilir. `read-timeout`'u karşı tarafın
gerçek yanıt süresini ölçtükten sonra seç — tahminle değil.
📌 Ayrıca `RestClient.Builder`'ı **inject et**; `RestClient.builder()` ile sıfırdan
kurarsan bu ayarlar taşınmaz. → [4.3](../04-web-katmani/4.3-restclient.md)

### Reçete B — "Aynı jar'ı üç ortamda çalıştıracağım"

```properties
# application.properties — ortak taban, ortam adı GEÇMEZ
siparis.magaza-adi=varsayilan-magaza
siparis.zaman-asimi=3000
```

```properties
# application-uretim.properties — YALNIZCA fark
siparis.magaza-adi=uretim-magazasi
siparis.zaman-asimi=1000
```

```bash
SPRING_PROFILES_ACTIVE=uretim java -jar siparis-servisi.jar
```

**Bedeli:** Profil adı yanlış yazılırsa uygulama sessizce varsayılan ayarlarla açılır.
Her dağıtımda açılış günlüğündeki `The following 1 profile is active:` satırını doğrula.
→ [3.3](../03-yapilandirma/3.3-profiller.md)

### Reçete C — "Konteynerde üretime alıyorum"

```properties
server.port=8080
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
server.error.include-stacktrace=never
```

Dördü de zaten varsayılan — bu blok bir **niyet beyanıdır**: ekipten biri `immediate`
yazmak isterse tartışma dosyada olur. Profili ve gizli değerleri dışarıdan ver:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: uretim
```

**Bedeli:** Yok. `spring.lifecycle.timeout-per-shutdown-phase` değerini yükseltirsen
Kubernetes'in `terminationGracePeriodSeconds` süresinden **küçük** tut, yoksa fark etmez
— süreç `SIGKILL` yer.

### Reçete D — "API'nin hata yanıtlarını düzeltiyorum"

```properties
spring.mvc.problemdetails.enabled=true
```

Ve kendi `@RestControllerAdvice` sınıfını yaz.

**Bedeli:** Ayar **biçimi** düzeltir, **içeriği** değil — hangi alanın neden geçersiz
olduğunu yine sen yazacaksın. Yanıt gövdesinin biçimi değiştiği için istemcileri haberdar
et. → [4.2](../04-web-katmani/4.2-exception-handling.md)

### Reçete E — "Bir bean nereden geliyor / neden kurulmuyor?"

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug > acilis.log 2>&1
sed -n '/Negative matches:/,/Exclusions:/p' acilis.log | less
```

**Bedeli:** Yok — `--debug` hiçbir davranışı değiştirmez, yalnızca raporu bastırır.
Üretimde açık bırakma; günlük hacmi büyür. → [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md)

### Reçete F — "Geliştirirken açılış hızlansın"

```properties
spring.main.lazy-initialization=true
```

**Bedeli:** Ağır. Bean hataları artık açılışta değil **ilk kullanımda** çıkar — yani
üretimde gördüğün güvenceyi kaybedersin. Yalnızca kendi makinende, yalnızca geliştirme
profilinde kullan; asla ortak `application.properties`'e yazma.

---

## 3. Tam liste — gruplara ayrılmış

### Sunucu ve HTTP yanıtları

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `server.port` | **8080** | Embedded server'ın dinlediği port | `Port 8080 was already in use` aldın ya da aynı makinede iki uygulama çalışacak | `0` yazarsan rastgele boş port seçilir — testlerde işe yarar. Kalıcı değeri dışarıdan ver, koda gömme |
| `server.error.include-stacktrace` | **never** | Hata yanıtına stack trace'in eklenip eklenmeyeceği | ⚠️ **Dokunma.** Stack trace sınıf adlarını, kütüphane sürümlerini ve dosya yollarını dışarı sızdırır | Ayrıntı **günlüğe**, genel mesaj istemciye, ikisini bağlayan izleme numarası yanıta |
| `spring.mvc.problemdetails.enabled` | **false** | Spring'in yerleşik hatalarını (404, 405, 415) [RFC 9457](03-kavram-sozlugu.md#rfc-9457) biçimine çevirir | Hata gövdelerini tek bir sözleşmeye oturtuyorsan | Biçimi düzeltir, içeriği değil. Alan bazlı ayrıntı için `@RestControllerAdvice` şart; ikisini birlikte kullan |

→ [4.1](../04-web-katmani/4.1-request-mapping.md) · [4.2](../04-web-katmani/4.2-exception-handling.md)

### Kapanış ve lifecycle

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `server.shutdown` | **graceful** ⭐ | `SIGTERM` gelince yeni istek almayı kesip devam edenleri bitirir | Neredeyse hiç. ⚠️ Spring Boot **3.x'te varsayılan `immediate` idi** — eski projelerde bu satırı elle yazılmış görürsen artık gereksiz, ama zararsız | `immediate` yazmak, devam eden isteklerin ortasından kesilmesi demektir. Bunu istiyorsan sebebini yazılı bırak |
| `spring.lifecycle.timeout-per-shutdown-phase` | **30s** | Graceful shutdown'da her aşamanın bekleyeceği üst sınır | En uzun isteğin 30 saniyeyi aşıyorsa yükselt; hızlı kapanış istiyorsan düşür | Orkestratörün öldürme süresinden **küçük** olmalı. Kubernetes'te `terminationGracePeriodSeconds` (varsayılanı 30s) bu değerden büyük olmalı, yoksa süreç zaten `SIGKILL` yer |
| `spring.main.lazy-initialization` | **false** | Bütün bean'lerin kurulumunu ilk kullanıma erteler | Yalnızca kendi makinende, açılış süresini kısaltmak için | Bir iyileştirme değil **takas**: açılış hızını, hatayı dağıtım anında görme güvencesiyle değişirsin. Üretimde kullanma |

📌 `@PreDestroy`, `kill -9` alındığında ve prototype bean'lerde **çalışmaz** — hiçbir ayar
bunu değiştirmez. → [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md)

### Dışarıya çağrı: RestClient

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `spring.http.clients.connect-timeout` | ⚠️ **Yok** | TCP bağlantısı kurulana kadar beklenecek süre | **Her projede, ilk günden.** Dış servise çağrı yapıyorsan bu satır yazılmamışsa eksiktir | 1–3 sn. Bağlantı kurulamıyorsa karşı taraf ayakta değildir; uzun beklemenin karşılığı yok |
| `spring.http.clients.read-timeout` | ⚠️ **Yok** | İlk yanıt baytı gelene kadar beklenecek süre | **Her projede.** Kursun en tehlikeli eksiği bu | 3–10 sn. Karşı tarafın gerçek yanıt süresini ölç, üstüne pay koy. `connect` her zaman `read`'ten küçük olmalı |

⚠️ **Bu iki satırın olmaması, ayarın "makul bir varsayılanı var" demek değildir — hiç
sınır yok demektir.** Karşı taraf donarsa isteğin sonsuza kadar bekler, thread
havuzun tükenir ve senin servisin de kendi kodunda hiçbir hata olmadan cevap veremez hâle
gelir.

📌 Timeout bir **sınırdır, çözüm değil**. "5 saniyede cevap gelmezse ne yapacağım?"
sorusunun cevabı hâlâ sende. → [4.3](../04-web-katmani/4.3-restclient.md)

### Container davranışı

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `spring.main.allow-bean-definition-overriding` | **false** | Aynı ada sahip ikinci bean definition'ın birincisini ezmesine izin verir | ⚠️ **Açma.** `A bean with that name has already been defined` hatasının doğru çözümü adı değiştirmektir | Ayar hatayı çözmez; onu "hangisi kazandı?" bilmecesine çevirir. Bilmecenin cevabı classpath sırasına bağlıdır ve yükseltmede değişir |
| `spring.main.allow-circular-references` | **false** | İki bean'in birbirini istemesine izin verir | ⚠️ **Açma.** Spring Boot 2.6'dan beri kapalı ve hata mesajı bunu "son çare" diye anıyor | Döngü bir yapılandırma değil **tasarım** sorunu: ortak parçayı üçüncü sınıfa çıkar, event kullan ya da sınırı yeniden çiz |
| `spring.autoconfigure.exclude` | **boş** | Adı verilen auto-configuration sınıflarını devre dışı bırakır | Bir yapılandırma sınıfını kod ya da ayarla susturamıyorsan — **son çare** | ⚠️ **Yanlış paket adı sessizce yok sayılır.** Spring Boot 4'te `autoconfigure` paket adının **sonundadır**: `org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration`. Liste uzuyorsa asıl sorun `pom.xml`'dedir |
| `spring.aop.auto` | **true** | AOP proxy altyapısını açar | Neredeyse hiç | Değer, kursun kendi `--debug` raporundaki `@ConditionalOnBooleanProperty (spring.aop.auto=true) matched` satırından okundu |
| `spring.aop.proxy-target-class` | **true** | Proxy'nin arayüz yerine [CGLIB](03-kavram-sozlugu.md#cglib) alt sınıfıyla üretilmesi | Neredeyse hiç | `false` yaparsan yalnızca arayüz üzerinden proxy üretilir ve arayüzü olmayan sınıflar proxy'lenemez |
| `proxyBeanMethods` *(anotasyon özelliği)* | **true** | `@Configuration` sınıfının CGLIB proxy'sinin üretilip üretilmeyeceği | Sınıftaki `@Bean` metotları birbirini **çağırmıyorsa** ve açılış süresi ölçülebilir biçimde önemliyse | İkisi birden doğru değilse dokunma: `false` iken metotlar birbirini çağırdığında singleton garantisi sessizce bozulur |

→ [1.2](../01-container-ve-bean/1.2-bean-tanimlama.md) · [1.3](../01-container-ve-bean/1.3-dependency-injection.md) · [2.2](../02-anotasyon-haritasi/2.2-configuration.md) · [2.3](../02-anotasyon-haritasi/2.3-auto-configuration.md)

### Profiller

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `spring.profiles.active` | **boş** — günlükte `falling back to 1 default profile: "default"` | Hangi profillerin açık olduğunu belirler | Her ortam ayrımında | ⚠️ **Dışarıdan ver**, `application.properties`'e yazma: kaynak koda yazdığın ortam adı dağıtımı kilitler. Çokluda virgülle ayır; çakışmada **sondaki kazanır** |
| `spring.config.activate.on-profile` | **yok** *(yazmazsan blok her zaman geçerli)* | Tek bir dosya içindeki `---` bloğunu belirli profile bağlar | YAML'da profilleri tek dosyada tutuyorsan | ⚠️ Eski adı `spring.profiles`; Spring Boot 2.4'ten beri **çalışmıyor ve sessizce yok sayılıyor**. İnternetteki eski örneklerin çoğu eski adı kullanıyor |

⚠️ **Yanlış yazılmış profil adı hata vermez.** `--spring.profiles.active=uretmi` yazarsan
Spring `application-uretmi.properties` arar, bulamaz, sessizce geçer ve uygulama varsayılan
ayarlarla açılır. Tek savunman açılış günlüğünün ikinci satırıdır.
→ [3.3](../03-yapilandirma/3.3-profiller.md)

### Bağlama, JSON ve thread'ler

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `spring.threads.virtual.enabled` | **false** | İstekleri Java 21 virtual thread'lerinde çalıştırır | Çok sayıda **bekleyen** (ağ/veritabanı) isteğin varsa | Java 21+ ister. CPU yoğun işlerde kazanç vermez; bekleme yoğun işlerde thread havuzunu darboğaz olmaktan çıkarır. Açmadan önce ve sonra ölç |
| `spring.jackson.*` | Jackson 3'ün kendi varsayılanları · tek tek anahtarlar için ⚠️ **Doğrulanmalı** | JSON serileştirme davranışını ayarlar (tarih biçimi, bilinmeyen alan davranışı, `null` alanların atılması…) | API sözleşmen belirli bir JSON biçimi gerektiriyorsa | ⚠️ Spring Boot 4'te Jackson **3**: paket `tools.jackson`, `com.fasterxml.jackson` değil. Karmaşık ihtiyaçta ayar yerine kendi `ObjectMapper` bean'ini tanımla — Spring'inki geri çekilir |
| `spring.datasource.url` | **yok** | Veri kaynağının adresi | JDBC classpath'teyse ve `Failed to configure a DataSource` alıyorsan | Üç seçenek: adresi ver, gömülü bir veritabanı ekle, ya da o auto-configuration'ı dışla. Veritabanı konusu **201** kursunda |
| `spring.jpa.open-in-view` | ⚠️ **Doğrulanmalı** | Görünüm katmanına kadar açık kalan bir persistence context tutar | Bu kursta yalnızca "ayarla kapatma" örneği olarak geçiyor | Kararı **201**'de ver; oraya kadar dokunma |
| Kendi ayarların (`siparis.*` gibi) | Sen belirlersin | Uygulamana özgü değerler | Her zaman | 1–2 ayar → `@Value`. 3 ve üstü → `@ConfigurationProperties` + `@Validated`. Öneki **kebab-case** yaz |

→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md) · [3.2](../03-yapilandirma/3.2-configuration-properties.md)

### Derleme ve çalıştırma bayrakları

Bunlar `application.properties`'e yazılmaz; komut satırında ya da `pom.xml`'de durur.

| Bayrak / ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `--debug` | kapalı | Açılışta **CONDITIONS EVALUATION REPORT** bastırır | "Bu bean nereden geldi / neden kurulmadı?" sorusunda | Davranışı değiştirmez, yalnızca yazdırır. Çıktıyı dosyaya al ve `Negative matches` bölümünde ara — cevap orada tek satırdır |
| `-parameters` *(derleyici bayrağı)* | Spring Boot'un Maven yapılandırmasında **açık** | Parametre adlarını `.class` dosyasına yazar | Kapatma. Başka bir derleme düzenine geçiyorsan açık olduğunu doğrula | Kapalıyken parametre adına dayanan injection **kod değişmeden** bozulur. Kalıcı çözüm bayrağa güvenmek değil, `@Qualifier` yazmaktır |
| `<java.version>` *(pom.xml)* | Projede **21** | Derleyicinin hedef Java sürümü | Sürüm değiştiriyorsan | ⚠️ Terminaldeki Java ile IDE'nin Java'sı **aynı** olmalı; farklıysa aynı kod iki yerde iki farklı davranır |
| `-Dspring-boot.run.arguments=...` | — | `./mvnw spring-boot:run` çalışırken uygulamaya komut satırı argümanı geçirir | Profil ya da ayar denemesi yaparken | Komut satırı argümanı **en yüksek öncelikli** kaynaktır; dosyadaki her şeyi ezer |

⚠️ Bağımlılıklara **`<version>` yazma.** `spring-boot-starter-parent` uyumlu sürümleri
yönetir; elle yazdığın sürüm ya bulunamaz ya da daha kötüsü bulunur ve uyumsuzluk aylar
sonra `NoSuchMethodError` olarak çıkar. → [00.2](02-kurulum.md)

---

## Ortam değişkeni yazım kuralı

Ayar adını ortam değişkenine çevirirken **iki ayrı işlem** yapılır:

```
siparis.magaza-adi   →   SIPARIS_MAGAZAADI
        ↑      ↑
     nokta    tire
      ↓        ↓
  alt çizgi  SİLİNİR
```

| Ayar adı | Doğru ortam değişkeni | Sık yapılan yanlış |
|---|---|---|
| `siparis.magaza-adi` | `SIPARIS_MAGAZAADI` | ❌ `SIPARIS_MAGAZA_ADI` |
| `spring.profiles.active` | `SPRING_PROFILES_ACTIVE` | — (tire yok, sorun çıkmaz) |
| `server.error.include-stacktrace` | `SERVER_ERROR_INCLUDESTACKTRACE` | ❌ `SERVER_ERROR_INCLUDE_STACKTRACE` |
| `spring.http.clients.read-timeout` | `SPRING_HTTP_CLIENTS_READTIMEOUT` | ❌ `SPRING_HTTP_CLIENTS_READ_TIMEOUT` |

⚠️ **Yanlış yazılan ortam değişkeni hata vermez** — eşleşmez, dosyadaki değer kalır ve
kimse fark etmez. Docker ve Kubernetes'te en sık yapılan yapılandırma hatası budur.
Tirenin silindiğini bir kez yaşayan bir daha unutmaz; yaşamadan öğrenmek daha ucuz.

→ [relaxed binding](03-kavram-sozlugu.md#relaxed-binding) · [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md)

---

## Tehlikeli ayarlar: dokunmadan önce iki kez düşün

| Ayar | Neden tehlikeli | Doğru refleks |
|---|---|---|
| `spring.http.clients.read-timeout` **yazılmamış olması** | Varsayılan yok. Karşı taraf donduğunda thread'lerin birikir; senin servisin de kendi kodunda hiçbir hata olmadan çöker | İlk günden yaz. Bu tablodaki tek "eksikliği tehlikeli" satır bu |
| `spring.main.allow-bean-definition-overriding=true` | Hatayı çözmez, sessiz bir "hangisi kazandı?" bilmecesine çevirir; kazanan classpath sırasına bağlıdır | Bean adını değiştir |
| `spring.main.allow-circular-references=true` | Tasarım sorununu gizler ve erteler; kurulum sırası artık öngörülemez | Ortak parçayı üçüncü bir sınıfa çıkar |
| `server.error.include-stacktrace` (`never` dışına çıkmak) | Sınıf adların, kütüphane sürümlerin ve dosya yolların saldırgana harita çizer | Ayrıntı günlüğe, izleme numarası yanıta |
| `spring.autoconfigure.exclude` | **Yanlış paket adı sessizce hiçbir şey yapmaz**; doğru yazsan bile bir sınıf birden çok bean kurar, hepsini birden kaybedersin | Önce kendi bean'ini tanımla, sonra ayarla kapat; dışlama son çare |
| `spring.main.lazy-initialization=true` | Bean hatalarını açılıştan ilk kullanıma taşır — yani üretime | Yalnızca kendi makinende |
| `spring.profiles.active` (ayar dosyasına yazmak) | Uygulamayı bir ortama kilitler; dışarıdan ezmek isteyen herkesin bunu bilmesi gerekir | Profili dışarıdan ver |
| `spring.profiles` *(eski anahtar)* | Spring Boot 2.4'ten beri **sessizce çalışmıyor** | `spring.config.activate.on-profile` yaz |
| `@Profile("!uretim")` ile uç nokta gizlemek | Ayar değil, ama aynı aileden: yanlış profil korumayı kaldırır | Güvenlik, güvenlik katmanıyla yapılır (**401**) |

📌 Bu tablodaki ayarların ortak özelliği: **hepsi bir hatayı çözmek yerine susturuyor.**
Bir ayarın açıklaması "şu hatayı ortadan kaldırır" diye başlıyorsa, önce o hatanın neden
çıktığını anla.

---

## Ayarı nereye yazarsın: öncelik sırası

Aynı ayar birden çok yerde tanımlıysa **dışarıdaki içerideki ezer.**

```
1. Komut satırı argümanları      --siparis.magaza-adi=x     ← hepsini ezer
2. Java sistem özellikleri       -Dsiparis.magaza-adi=x
3. Ortam değişkenleri            SIPARIS_MAGAZAADI=x
4. Profil dosyası                application-uretim.properties
5. Ana ayar dosyası              application.properties / .yaml
6. @PropertySource               elle eklenen dosyalar
7. Kod içi varsayılanlar         SpringApplication.setDefaultProperties  ← herkes ezer
```

Bu bir tuzak değil **özelliktir**: aynı jar'ı hiç değiştirmeden test ve üretimde farklı
ayarlarla çalıştırmanın yolu budur.

Hangi ayar nereye:

| Ayarın türü | Nereye yaz | Neden |
|---|---|---|
| Her ortamda aynı olan (`server.shutdown`, timeout'lar) | `application.properties` | Depoya girsin, ekip görsün |
| Ortamdan ortama değişen (adres, mağaza adı) | Profil dosyası | Farkı tek yerde tut, tamamını kopyalama |
| Hangi ortamda olduğun (`spring.profiles.active`) | **Ortam değişkeni** | Kaynak kodda ortam adı görünmemeli |
| Gizli değerler (parola, anahtar) | Ortam değişkeni ya da gizli anahtar yönetimi | Depoya asla girmez |
| Tek seferlik deneme | Komut satırı argümanı | En yüksek öncelik, dosyayı kirletmez |

⚠️ **Aynı klasörde hem `application.properties` hem `application.yaml` tutma.** İkisi de
varsa `.properties` kazanır ve YAML'ı düzenleyen kişi "neden değişmiyor?" diye saatler
harcar. Birini sil.

⚠️ **Profil dosyasına ayarların tamamını kopyalama.** Profil dosyası ana dosyanın üzerine
**biner**; yalnızca farkı yaz. Tamamını kopyalarsan tabana eklenen yeni bir ayar profil
dosyalarına eklenmediğinde sessiz bir tutarsızlık doğar.

→ [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md) · [3.3](../03-yapilandirma/3.3-profiller.md)

---

## Varsayılanı bilmiyorsan: sisteme sor

Bu dosyadaki değerler Spring Boot **4.1.1** içindir. Doğrusu her zaman **senin
makinendekidir**. Ezberleme, sor:

```bash
# Hangi auto-configuration neden devreye girdi/girmedi
./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug > acilis.log 2>&1
sed -n '/Negative matches:/,/Exclusions:/p' acilis.log | less

# Bir ayarın gerçek değeri ve HANGİ KAYNAKTAN geldiği
curl -s http://localhost:8080/actuator/env/siparis.magaza-adi

# Hangi profiller açık
curl -s http://localhost:8080/actuator/env | grep activeProfiles

# Starter gerçekte ne getiriyor
./mvnw dependency:tree | head -40
```

Koddan sormak istersen:

```java
var ort = container.getEnvironment();
System.out.println(ort.getProperty("siparis.magaza-adi"));

((org.springframework.core.env.AbstractEnvironment) ort).getPropertySources()
    .forEach(k -> System.out.println(k.getName()));      // liste = öncelik sırası
```

Açılış günlüğünün **ikinci satırı** hangi profillerin açık olduğunu her zaman yazar —
bir sorunu araştırırken ilk oraya bak:

```
INFO ... : The following 1 profile is active: "uretim"
```

⚠️ **`/actuator/env` ayar değerlerini gösterir; üretimde herkese açma.** Actuator'ün
güvenliği **401** kursunun konusu.

⚠️ **Bu dosyadaki bir ayar adı çalışmıyorsa uydurma.** Ad değişmiş olabilir — `--debug`
raporundaki `@ConditionalOnProperty` satırları ayarın **gerçek adını** yazar; ayrıca
`spring-boot-configuration-processor` bağımlılığını eklersen IDE ayar adlarını tamamlar ve
yazım hatasını yazarken gösterir.

---

📖 Bir terime takıldıysan: [Kavram sözlüğü](03-kavram-sozlugu.md)
🏠 [Kursa dön](../README.md)
