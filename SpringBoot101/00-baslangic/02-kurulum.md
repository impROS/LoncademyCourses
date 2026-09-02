# 00.2 — Ortam kurulumu ve laboratuvar projesi

> **Alan:** Başlangıç — bu kursun tüm pratikleri bu projede yapılacak
> **Süre:** ~30 dakika
> **Test:** Yok — bu bir kurulum dosyası. Doğrulaması, sonundaki kontrol listesi.

---

## Neden bu konu

Bu kursun pratikleri okunacak metin değil, **çalıştırılacak kod**. Bir kere düzgün kuralım,
19 konu boyunca aynı projeye ekleyerek gidelim. Sonunda elinde okuduğun her konunun
çalışan bir karşılığı olacak — kurs bitince silmeyeceğin bir referans projesi.

💸 **Maliyet: yok.** Bulut hesabı, lisans, abonelik gerekmez. Her şey kendi makinende çalışır.

---

## 1. Java: hangi sürüm ve neden ⭐

Spring Boot 4.1 **en az Java 17** ister ve Java 26'ya kadar destekler. Biz **Java 21**
kullanacağız. Sebebi keyfî değil:

| Sürüm | Durum | Bu kurs için |
|---|---|---|
| 17 | Spring Boot 4'ün alt sınırı, uzun dönem destekli | Çalışır, ama `record` desen eşlemesi gibi rahatlıklar eksik |
| **21** | Uzun dönem destekli, virtual thread'ler (JVM'in kendi yönettiği hafif eşzamanlılık birimi) kararlı | ✅ **Bunu kullan** |
| 25 / 26 | Yeni, Spring Boot 4 birinci sınıf destekliyor | Çalışır; kursun hiçbir yerinde gerekmiyor |

Kontrol et:

```bash
java -version
```

`21` ile başlayan bir sürüm görmelisin. Görmüyorsan kur:

```bash
brew install --cask temurin@21
```

macOS'ta birden çok Java kuruluysa hangilerinin olduğunu şöyle görürsün:

```bash
/usr/libexec/java_home -V
```

Kabuğunu 21'e sabitlemek için (bu satırı `~/.zshrc` dosyanın sonuna ekle):

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

> ⚠️ **Tuzak:** IntelliJ'in kendi Java'sı ile terminalindeki Java **farklı olabilir.**
> Terminalde 21, IntelliJ'de 17 çalışıyorsa aynı kod iki yerde iki farklı davranır ve
> saatlerini yersin. IntelliJ'de: **File → Project Structure → Project SDK** — orada da 21 yaz.

---

## 2. Maven

```bash
mvn -v
```

`3.9` veya üstü olmalı. Yoksa:

```bash
brew install maven
```

Projemizde ayrıca **Maven wrapper** olacak — `./mvnw`. Bu, projeye
gömülü küçük bir betik; Maven'ı doğru sürümüyle kendisi indirir. Bu yüzden projeyi başka
bir makineye taşıdığında oradaki Maven sürümü ne olursa olsun aynı derleme çıkar.
Kursta komutları `./mvnw` ile yazacağım.

---

## 3. Projeyi oluştur

İki yol var. **Birincisini kullan**, ikincisi ne olduğunu anlaman için.

### Yol A — `start.spring.io` (önerilen)

1. Tarayıcıda [start.spring.io](https://start.spring.io) aç.
2. Şunları seç:

   | Alan | Değer |
   |---|---|
   | Project | **Maven** |
   | Language | **Java** |
   | Spring Boot | **4.1.1** |
   | Group | `tr.loncademy` |
   | Artifact | `siparis-servisi` |
   | Name | `siparis-servisi` |
   | Package name | `tr.loncademy.siparis` |
   | Packaging | **Jar** |
   | Java | **21** |

3. **ADD DEPENDENCIES** düğmesine bas, şunları ekle:
   - **Spring Web**
   - **Validation**
   - **Spring Boot Actuator**

4. **GENERATE** → inen zip'i `~/IdeaProjects/` altına aç.

5. **İnen `pom.xml`'i aç ve bağımlılık listesine bak.** Üç şey seçtin ama altı bağımlılık
   göreceksin:

   ```xml
   <artifactId>spring-boot-starter-webmvc</artifactId>
   <artifactId>spring-boot-starter-validation</artifactId>
   <artifactId>spring-boot-starter-actuator</artifactId>

   <artifactId>spring-boot-starter-webmvc-test</artifactId>       <!-- test -->
   <artifactId>spring-boot-starter-validation-test</artifactId>   <!-- test -->
   <artifactId>spring-boot-starter-actuator-test</artifactId>     <!-- test -->
   ```

   ⚠️ **İki şey dikkatini çekmeli** — ikisi de Spring Boot 4'ün getirdiği değişiklik:

   - Web starter'ının adı artık `spring-boot-starter-web` **değil**,
     `spring-boot-starter-webmvc`. Eskisi hâlâ çalışıyor ama kendi tanımında şöyle yazıyor:
     *"deprecated in favor of spring-boot-starter-webmvc"*. Yani internetteki her örnek
     eski adı kullanıyor; sen yenisini kullan.
   - Tek bir `spring-boot-starter-test` yok; **her teknoloji için ayrı bir test
     starter'ı** var. Bunun sebebini [2.4](../02-anotasyon-haritasi/2.4-starterlar.md)'te
     açıyoruz: Spring Boot 4 devasa jar'ları küçük modüllere böldü.

> 📌 **Dikkat çeken bir ayrıntı:** Sürüm listesinde **Spring Boot 3.x yok.** Sadece 4.0.8,
> 4.1.1 ve geliştirme sürümleri var. Yani yeni bir proje başlatan herkes artık 4.x
> alıyor — internetteki 3.x örnekleriyle arandaki mesafe her gün açılıyor. Bu kursun
> 4.1'i temel almasının sebebi bu.

### Yol B — elle (ne indiğini anlamak için)

`start.spring.io` sana aslında şu dosyayı üretiyor. Kendin de yazabilirsin:

```bash
mkdir -p ~/IdeaProjects/siparis-servisi/src/main/java/tr/loncademy/siparis
mkdir -p ~/IdeaProjects/siparis-servisi/src/main/resources
cd ~/IdeaProjects/siparis-servisi
```

`pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
    <relativePath/>
  </parent>

  <groupId>tr.loncademy</groupId>
  <artifactId>siparis-servisi</artifactId>
  <version>0.0.1-SNAPSHOT</version>

  <properties>
    <java.version>21</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webmvc-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

`src/main/java/tr/loncademy/siparis/SiparisServisiUygulamasi.java`:

```java
package tr.loncademy.siparis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SiparisServisiUygulamasi {
    public static void main(String[] args) {
        SpringApplication.run(SiparisServisiUygulamasi.class, args);
    }
}
```

Wrapper'ı ekle:

```bash
mvn wrapper:wrapper
```

### `pom.xml`'de dikkat edilecek üç yer

| Satır | Ne işe yarıyor |
|---|---|
| `<parent>` → `spring-boot-starter-parent` | Bütün Spring kütüphanelerinin **birbiriyle uyumlu sürümlerini** belirler. Bu yüzden bağımlılıklara sürüm yazmıyorsun. |
| `<java.version>21</java.version>` | Derleyiciye hedef sürümü söyler |
| `spring-boot-maven-plugin` | `./mvnw spring-boot:run` komutunu ve çalıştırılabilir jar üretimini sağlar |

> ⚠️ **Bağımlılıklara sürüm yazma.** `<version>` eklersen üst projedeki uyumlu sürümü
> ezersin ve çalışma zamanında `NoSuchMethodError` gibi anlamsız hatalar alırsın. Sürümü
> yalnızca Spring dışı bir kütüphane için ve gerçekten gerekiyorsa yaz.

---

## 4. İlk çalıştırma

```bash
cd ~/IdeaProjects/siparis-servisi
./mvnw spring-boot:run
```

İlk çalıştırma birkaç dakika sürer (bağımlılıklar iniyor). Şunu görmelisin:

```
 :: Spring Boot ::                (v4.1.1)

... Starting SiparisServisiUygulamasi using Java 21...
... Tomcat initialized with port 8080 (http)
... Starting Servlet engine: [Apache Tomcat/11.0.24]
... Tomcat started on port 8080 (http) with context path '/'
... Started SiparisServisiUygulamasi in 0.635 seconds
```

Başka bir terminalde:

```bash
curl -i http://localhost:8080/yok
```

Beklenen:

```
HTTP/1.1 404
Content-Type: application/json

{"timestamp":"...","status":404,"error":"Not Found","path":"/yok"}
```

- [ ] **Kontrol:** `Started SiparisServisiUygulamasi` satırını gördün.
- [ ] **Kontrol:** `curl` sana JSON bir 404 döndürdü.
- [ ] **Kaydet:** Açılış kaç saniye sürdü? ______ (Kurs boyunca bu sayının nasıl değiştiğine bakacağız.)

Durdurmak için çalıştığı terminalde `Ctrl+C`.

---

## 5. Yanlış hâlleri: şimdi bilerek boz

Bu üç hatayı **şimdi** kendi elinle üret. Üretimde karşına çıktığında tanıyacaksın.

### ❌ Hata 1 — port dolu

Uygulama çalışırken **ikinci bir terminalde** aynı komutu tekrar çalıştır:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Web server failed to start. Port 8080 was already in use.

Action:

Identify and stop the process that's listening on port 8080 or configure this
application to listen on another port.
```

✅ **Doğrusu:** Bu kutuyu Spring Boot'un **failure analyzer**'ı (tanıdığı bir hatayı
okunur bir metne çeviren parça) yazdı. Stack trace yerine ne yapman gerektiğini
söylüyor. Spring Boot'ta bir hata alırsan önce bu kutuya bak — çoğu zaman çözüm orada
yazılı.

Portu kim tutuyor:

```bash
lsof -i :8080
```

### ❌ Hata 2 — sınıf yanlış pakette

`SiparisServisiUygulamasi.java` dosyasını `tr/loncademy/siparis/` yerine
`tr/loncademy/` altına taşı, `package` satırını da `tr.loncademy` yap. Sonra bir alt
pakete bir `@RestController` koy — bulunmadığını göreceksin.

✅ **Doğrusu:** `@SpringBootApplication` sınıfı **en üst pakette** durur; Spring yalnızca
o paketi ve **altındakileri** tarar. Bunun sebebini [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)'de
açıyoruz. Şimdilik kural: ana sınıf en yukarıda, geri kalan her şey onun altında.

### ❌ Hata 3 — bağımlılığa elle sürüm yazmak

`spring-boot-starter-webmvc` bağımlılığına `<version>3.5.3</version>` ekle ve çalıştır:

```
Could not resolve dependencies ...
spring-boot-starter-webmvc:jar:3.5.3 was not found
```

Sebebi öğretici: **o sürüm hiç yok.** `spring-boot-starter-webmvc` diye bir yapı taşı
yalnızca 4.x'te var; 3.5.3'te adı `spring-boot-starter-web` idi. Yani elle yazdığın sürüm,
üst projenin senin için çözdüğü uyumluluğu bozdu.

⚠️ Daha tehlikeli hâli, sürümün **var olduğu** durumdur (ör. `4.0.0`): o zaman derleme
geçer, uygulama açılır, ve uyumsuzluk aylar sonra `NoSuchMethodError` gibi sebebi
görünmeyen bir hatayla çıkar.

✅ **Doğrusu:** Sürümü üst proje yönetir. `<version>` satırını sil.

---

## 6. IntelliJ ayarları (isteğe bağlı ama tavsiye)

| Ayar | Yer | Neden |
|---|---|---|
| Project SDK = 21 | File → Project Structure → Project | Terminal ile IDE aynı Java'yı kullansın |
| Annotation processing açık | Settings → Build → Compiler → Annotation Processors | `@ConfigurationProperties` ipuçları için (3.2'de kullanacağız) |
| Encoding = UTF-8 | Settings → Editor → File Encodings | Türkçe karakter bozulmasın |

**Community sürümü yeterli.** Ultimate'ın Spring araçları rahattır ama bu kursta
gerekmiyor — her şeyi Actuator ve günlük çıktısıyla göreceğiz, çünkü üretimde elinde
IDE olmayacak.

---

## Kurulum kontrol listesi

Aşağıdakilerin hepsi ✅ olmadan 1.1'e geçme:

- [ ] `java -version` → 21
- [ ] `mvn -v` → 3.9+
- [ ] `~/IdeaProjects/siparis-servisi` klasörü var, içinde `pom.xml` ve `mvnw` var
- [ ] `./mvnw spring-boot:run` → `Started SiparisServisiUygulamasi` yazdı
- [ ] `curl -i http://localhost:8080/yok` → JSON 404 döndü
- [ ] Port dolu hatasını bilerek bir kez ürettim ve failure analyzer kutusunu gördüm
- [ ] `Ctrl+C` ile durdurmayı denedim

---

## 60 saniyelik özet

- Java **21**, Maven **3.9+**, proje `~/IdeaProjects/siparis-servisi`.
- `start.spring.io` artık Spring Boot 3.x sunmuyor — yeni projeler 4.x.
- `spring-boot-starter-parent` bütün sürümleri yönetir; bağımlılıklara **`<version>` yazma**.
- `./mvnw` wrapper'ı, Maven sürümünü projeye sabitler.
- `@SpringBootApplication` sınıfı **en üst pakette** durur; tarama oradan aşağı iner.
- Spring Boot hata verdiğinde `APPLICATION FAILED TO START` kutusunu oku — çözüm çoğu zaman orada.
- Terminal ile IDE'nin Java sürümü **aynı** olmalı.

---

## Sırada ne var
➡️ [`../01-container-ve-bean/1.1-container-nedir.md`](../01-container-ve-bean/1.1-container-nedir.md) — container'ın içine bakıyoruz.
