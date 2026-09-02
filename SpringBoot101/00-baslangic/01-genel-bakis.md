# 00.1 — Spring Boot ne yapıyor, ve nerede kaybediliyor

> **Alan:** Başlangıç — kursun geri kalanının üstüne kurulacağı zemin
> **Süre:** ~25 dakika okuma
> **Test:** [`01-test.html`](01-test.html) · 14 soru

---

## Neden bu konu

Spring Boot'u kullanan çoğu kişi onu **ezberler**. "Sınıfın başına `@Service` yazılır,
alanın başına `@Autowired` yazılır, çalışır." Bu bilgi bir yere kadar götürür. Sonra bir
gün şu üç cümleden biriyle karşılaşırsın ve elde tutunacak hiçbir şey kalmaz:

- `Parameter 0 of constructor required a bean of type '...' that could not be found.`
- `The dependencies of some of the beans form a cycle.`
- Ya da en kötüsü: hata yok, ama **ayar okunmamış** — kod üretimde varsayılan değerle çalışıyor.

Bu üç durumun ortak noktası şu: hepsi, senin yazmadığın bir kodun senin nesnelerini
kurmasıyla ilgili. Anlaşılmadığı sürece hepsi büyü gibi görünür.

**Bu kursun büyük fikri:** Spring Boot'un yaptığı iki şey var — **nesneleri senin yerine
kurup birbirine bağlamak** ve **sen istemeden makul varsayılanları devreye sokmak.**
Hepsi bu. Geri kalan her şey bu iki cümlenin ayrıntısı.

---

## 1. Dert: `new` ile bağlanan kod ⭐

Spring'i anlamanın tek yolu, o olmasaydı ne yazacağını görmek. Bir sipariş servisi yaz:

```java
public class SiparisServisi {
    private final StokIstemcisi stok = new StokIstemcisi("https://stok.sirket.com");
    private final OdemeIstemcisi odeme = new OdemeIstemcisi("https://odeme.sirket.com");

    public void siparisAl(Siparis s) { /* ... */ }
}
```

Çalışır. Peki şimdi şu dört şeyi yap:

| İstek | `new` ile ne olur |
|---|---|
| Test ederken gerçek ödeme servisine gitme | Olmuyor — adres sınıfın içine gömülü, değiştiremezsin |
| Test ortamında farklı adres kullan | Kodu değiştirip yeniden derlemen gerekir |
| `StokIstemcisi`'ni 40 sınıf kullanıyor, hepsi kendi kopyasını kuruyor | 40 connection pool açılır |
| `StokIstemcisi`'ne bir timeout ayarı ekle | 40 dosyayı da düzenlersin |

Dert şu: **nesneyi kullanan sınıf, aynı zamanda onu kurmakla da yükümlü.** İki ayrı iş
tek yere yığılmış.

### En sade hâl: kurma işini dışarı çıkar

```java
public class SiparisServisi {
    private final StokIstemcisi stok;
    private final OdemeIstemcisi odeme;

    public SiparisServisi(StokIstemcisi stok, OdemeIstemcisi odeme) {   // ← dışarıdan al
        this.stok = stok;
        this.odeme = odeme;
    }
}
```

Sınıf artık "bana lazım olan şunlar" diyor, "onları şöyle kuracağım" demiyor. Bu kadarı
bile testi çözer: teste **mock** (gerçeğinin yerine geçen, test için kurulmuş taklit
nesne) bir `StokIstemcisi` verirsin.

Ama bir soru kaldı: **bu nesneleri artık kim kuruyor?** Birinin bir yerde `new
SiparisServisi(new StokIstemcisi(...), new OdemeIstemcisi(...))` yazması gerekiyor. Uygulama
büyüdükçe o "birinin bir yerde" kısmı yüzlerce satırlık bir kurulum dosyasına dönüşür.

**İşte Spring'in devraldığı iş tam olarak bu.** Sen "bana şu lazım" dersin, kurma ve
bağlama işini o yapar. Buna **dependency injection** (bağımlılığın sınıfın içinde
kurulmayıp dışarıdan verilmesi) denir.

> 📌 **Sık yapılan hata:** Dependency injection'ı "anotasyon yazma tekniği" sanmak.
> Anotasyon sadece Spring'e haber verme biçimi. Fikrin kendisi, yukarıdaki
> **constructor**'dır (nesneyi kuran metot) — Spring olmadan da geçerli bir tasarımdır.

---

## 2. Container: nesneleri tutan yer

Spring, uygulaman açılırken bir **container** (nesneleri kuran, tutan ve birbirine bağlayan
yapı) kurar. Container, kurduğu nesneleri bir haritada tutar. Container'ın içindeki her
nesneye **bean** denir.

```
uygulama açılır
   ↓
container kurulur (ApplicationContext)
   ↓
"hangi sınıflar bean olacak?" → taranır ve bulunur
   ↓
her bean için: bağımlılıkları bul → onları önce kur → sonra bunu kur
   ↓
container hazır → uygulama isteği karşılamaya başlar
```

İki terim, ikisi de kursun tamamında geçecek:

| Terim | Ne demek |
|---|---|
| **Container** | Nesneleri kuran, tutan ve birbirine bağlayan Spring parçası. Kodda karşılığı `ApplicationContext`. |
| **Bean** | Container'ın kurup yönettiği nesne. Sen `new` demediysen ve container kurduysa, o bir bean'dir. |

⚠️ Bean, özel bir Java türü **değildir**. Sıradan bir nesnedir; onu bean yapan tek şey,
container'ın onu tanıyor olmasıdır. Aynı sınıftan `new` ile kurduğun nesne bean
**değildir** ve Spring'in ona injection yapması, ayar okuması, transaction açması
beklenmez.

Bu ayrımı şimdi kaydet: **201'de** transaction çalışmadığında ilk bakacağın yer,
"acaba bu nesne gerçekten bean mi?" sorusudur.

---

## 3. Spring ≠ Spring Boot ⭐

Bu ikisi sürekli karıştırılıyor. Fark şu:

| | **Spring Framework** | **Spring Boot** |
|---|---|---|
| Ne | Container, dependency injection, web katmanı, transaction, AOP — asıl işi yapan kütüphaneler | Spring Framework'ü **senin yerine yapılandıran** katman |
| Olmasaydı | Hiçbir şey olmazdı, çekirdek bu | Aynı uygulamayı yazardın, ama ~300 satır XML/Java yapılandırma ile |
| Sürüm | 7.0.x | 4.1.x |

Spring Boot'un getirdiği üç şey:

1. **Auto-configuration** (senin yazmadığın yapılandırmayı kendiliğinden kuran mekanizma) —
   classpath'te (uygulamanın gördüğü kütüphanelerin listesi) Tomcat varsa bir web sunucusu
   kurar; H2 varsa bir veri kaynağı (DataSource) kurar. Sen istemeden.
2. **Starter'lar** — `spring-boot-starter-webmvc` yazarsın, uyumlu sürümleriyle 30 kütüphane gelir.
3. **Embedded server** (uygulamanın kendi jar'ının içinde gelen sunucu) — uygulaman
   `java -jar` ile çalışan sıradan bir Java programıdır; dışarıya kurulmuş bir sunucuya
   `.war` atmazsın.

> 📌 **Sık yapılan hata:** "Spring Boot yeni Spring" sanmak. Spring Boot, Spring'in
> **üstünde** duran ince bir katman. Bu kursta öğrendiğin container ve bean bilgisi Spring
> Framework bilgisidir — Boot sürümü değişse de geçerliliğini korur.

---

## 4. Auto-configuration'ın bedeli

Auto-configuration işi kolaylaştırır ve **aynı sebeple** kafa karıştırır: kodda
görmediğin şeyler oluyor.

Az önce boş bir Spring Boot 4.1.1 uygulaması çalıştırdım. Yazdığım Java kodu tam 8 satır.
Çıktı bu:

```
 :: Spring Boot ::                (v4.1.1)

... Starting SiparisServisiUygulamasi using Java 23.0.2 with PID 67147
... No active profile set, falling back to 1 default profile: "default"
... o.s.boot.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
... o.apache.catalina.core.StandardEngine : Starting Servlet engine: [Apache Tomcat/11.0.24]
... Root WebApplicationContext: initialization completed in 306 ms
... o.s.boot.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
... Started SiparisServisiUygulamasi in 0.635 seconds (process running for 0.789)
```

8 satır kod yazdım, ortada bir Tomcat 11 sunucusu var ve 8080 portunu dinliyor. Tomcat'i
ben istemedim; `spring-boot-starter-webmvc` bağımlılığı classpath'te olduğu için Spring Boot
"demek ki web uygulaması yazıyor" diye karar verdi.

Aynı uygulamaya var olmayan bir adrese istek attım:

```
HTTP/1.1 404
Content-Type: application/json

{"timestamp":"2026-09-02T21:07:28.889Z","status":404,"error":"Not Found","path":"/yok"}
```

Bu JSON'u da ben yazmadım. Varsayılan hata işleyicisi üretti.

**Buradaki risk:** Bir gün bu varsayılanlardan biri istediğin gibi olmayacak ve kodda
arayacak bir yer bulamayacaksın. Bu yüzden **2.3'te** auto-configuration'ı teşhis
etmeyi öğreneceğiz: hangi kararın alındığını, **neden** alındığını ve hangisinin
alınmadığını satır satır görebilirsin.

> ⚠️ **Tuzak:** "Auto-configuration sihir" dersen orada durursun. Sihir değil — bir
> dizi `if` cümlesi. Sadece `if`'ler senin dosyalarında değil, kütüphanenin içinde.

---

## 5. Spring Boot 4 ne değiştirdi ⭐

Spring Boot 4.0 Kasım 2025'te çıktı ve 3.x'ten geçiş, yıllardır görülen en zahmetli geçiş.
Bunu şimdi bilmelisin, çünkü internette bulacağın örneklerin çoğu hâlâ 3.x.

| Konu | Spring Boot 3.x | Spring Boot 4.x |
|---|---|---|
| En düşük Java | 17 | 17 (Java 25 birinci sınıf destekli) |
| JSON kütüphanesi | Jackson 2 (`com.fasterxml.jackson`) | **Jackson 3** (`tools.jackson`) |
| Test mock'u | `@MockBean` | **`@MockitoBean`** |
| `@SpringBootTest` + MockMvc | Kendiliğinden gelirdi | **`@AutoConfigureMockMvc` yazman gerekir** |
| Undertow sunucusu | Vardı | **Kaldırıldı** |
| Paket yapısı | Büyük jar'lar | Modüllere bölündü (`spring-boot-starter-flyway` gibi ayrı starter'lar) |
| `null` güvenliği | `org.springframework.lang` anotasyonları | **JSpecify** |

Bu kursta **her konuda** böyle bir kutu göreceksin. Sebebi tek: işteki projen büyük
ihtimalle 3.x. Yeni doğruyu öğrenirken eskiyi de tanı ki, iki kod tabanı arasında gidip
gelirken şaşırma.

> 📌 **Sık yapılan hata:** İnternette bulduğun bir örneği "Spring Boot Spring Boot'tur"
> diyerek kopyalamak. `@MockBean` yazan bir örnek Spring Boot 4'te **derlenmez**. Bir
> örneğe bakarken önce sürümüne bak.

---

## 6. Bu kursta nerede kaybedilir

Konuyu öğrenenlerin en çok takıldığı beş yer. Şimdiden tanı:

| # | Kaybedilen yer | Ne zaman anlatılıyor |
|---|---|---|
| 1 | Aynı türden iki bean olunca container hangisini seçeceğini bilemez | [1.3](../01-container-ve-bean/1.3-dependency-injection.md) |
| 2 | Constructor'da bağımlılığı kullanmaya çalışmak (henüz kurulmamıştır) | [1.4](../01-container-ve-bean/1.4-bean-lifecycle.md) |
| 3 | Singleton bir bean'e istek bazlı bir bean inject etmek | [1.5](../01-container-ve-bean/1.5-scope.md) |
| 4 | `@Configuration` sınıfında `@Bean` metodunu elle çağırmak | [2.2](../02-anotasyon-haritasi/2.2-configuration.md) |
| 5 | Ayarın okunmadığını fark etmemek — hata yok, varsayılan çalışıyor | [3.1](../03-yapilandirma/3.1-oncelik-sirasi.md) |

Bu beşi bilen biri, Spring Boot'ta karşılaşılan sorunların çoğunu ilk yarım saatte çözer.

---

## Sık karıştırılanlar — tek tabloda

| Karışan | Doğrusu | Neden diğeri değil |
|---|---|---|
| Spring / Spring Boot | Spring = çekirdek, Boot = onu yapılandıran katman | Boot, Spring'in yeni sürümü değil; üstünde duran ayrı bir proje |
| Bean / nesne | Bean = **container'ın** kurduğu nesne | `new` ile kurduğun nesne de bir nesnedir ama bean değildir; Spring ona dokunmaz |
| Dependency injection / anotasyon | Fikir constructor'dadır | Anotasyon sadece Spring'e haber verme biçimi; fikir Spring'siz de geçerli |
| Auto-configuration / varsayılan ayar | Auto-configuration **bean kurar** | Varsayılan ayar sadece bir değerdir; auto-configuration karar verip nesne üretir |
| `@SpringBootApplication` / `main` metodu | Anotasyon taramayı ve auto-configuration'ı açar | `main` sadece başlangıç noktasıdır, tek başına hiçbir şey yapılandırmaz |

---

## 🖥 Pratik — Spring Boot'un kaç bean kurduğunu gör

> **Amaç:** "Container" sözünün somut karşılığını sayı olarak görmek · **Süre:** 10 dk
> **💸 Maliyet:** Yok — her şey kendi makinende
> **Ön koşul:** [00.2 Kurulum](02-kurulum.md)'u yaptıysan projen hazırdır. Yapmadıysan
> önce oraya git, sonra buraya dön.

### Adımlar

1. `SiparisServisiUygulamasi.java` dosyasını aç.
2. `main` metodunun içini şöyle değiştir:

   ```java
   public static void main(String[] args) {
       var container = SpringApplication.run(SiparisServisiUygulamasi.class, args);
       System.out.println(">>> Container'daki bean sayısı: " + container.getBeanDefinitionCount());
   }
   ```

   `SpringApplication.run(...)` sana container'ı geri döndürür — çoğu örnekte bu dönüş
   değeri kullanılmadığı için fark edilmez.

3. Çalıştır:

   ```bash
   ./mvnw spring-boot:run
   ```

- [ ] **Kontrol:** Açılış günlüğünün altında `>>> Container'daki bean sayısı: ...` satırını göreceksin.
- [ ] **Kaydet:** Sayı kaç çıktı? ______

Yazdığın sınıf sayısı **bir**. Container'daki bean sayısı ise yüzün üstünde. Aradaki farkın
tamamı auto-configuration'dır: Tomcat, JSON dönüştürücü, hata işleyici, istek eşleyici…

4. Şimdi bir de listele — `getBeanDefinitionCount()` satırının altına ekle:

   ```java
   java.util.Arrays.stream(container.getBeanDefinitionNames())
       .filter(ad -> ad.toLowerCase().contains("tomcat"))
       .forEach(System.out::println);
   ```

- [ ] **Kontrol:** Adında `tomcat` geçen bean'ler listelenir. Bunların hiçbirini sen yazmadın.

### Temizlik

Ücret doğuran bir şey yok, silinecek kaynak yok. İstersen eklediğin iki satırı sil;
bırakırsan da bir zararı olmaz. `Ctrl+C` ile uygulamayı durdur.

---

## Nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — "Bu nesne neden `null`?"**
> Bir alanı `new` ile kurulmuş bir nesnede `@Autowired` ile doldurmaya çalışıyorsun.
> → Container o nesneyi tanımıyor. Injection yalnızca **bean**'lere yapılır.

**Kalıp 2 — "Ayarı yazdım ama okumuyor."**
> Değeri doğru yazdın ama başka bir kaynak (ortam değişkeni, komut satırı) onu eziyor.
> → 3.1'deki öncelik sırasına bakılır.

**Kalıp 3 — "Bu Tomcat nereden geldi?"**
> `spring-boot-starter-webmvc` bağımlılığından. Sen istemedin, auto-configuration karar verdi.

**Kalıp 4 — "İnternetteki örnek derlenmiyor."**
> Örnek Spring Boot 3.x için yazılmış; `@MockBean` gibi bir anotasyon 4.x'te yok.

**Kalıp 5 — "Test yavaş."**
> `@SpringBootTest` bütün container'ı ayağa kaldırıyor. Slice test yeter mi diye bakılır (5.1).

---

## 60 saniyelik özet

- Spring'in yaptığı iki şey: **nesneleri kurup bağlamak** + **makul varsayılanları devreye sokmak.**
- **Container** = nesneleri kuran ve tutan yapı (`ApplicationContext`). **Bean** = container'ın kurduğu nesne.
- Bean, özel bir tür değil. `new` ile kurduğun nesne bean değildir; Spring ona dokunmaz.
- **Spring Framework** çekirdektir (7.0.x), **Spring Boot** onu yapılandıran katmandır (4.1.x).
- Spring Boot'un üç katkısı: auto-configuration, starter'lar, embedded server.
- Auto-configuration bir `if` dizisidir, sihir değil — ve teşhis edilebilir (2.3).
- Spring Boot 4'te dikkat: Jackson 3, `@MockitoBean`, `@SpringBootTest` artık MockMvc getirmez, Undertow yok.
- 8 satır kodla ayağa kalkan uygulamada container'da yüzlerce bean vardır.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `new SiparisServisi(...)` ile kurduğun bir nesnenin `@Autowired` alanı neden dolmaz?
2. Spring Framework ile Spring Boot arasındaki farkı tek cümlede nasıl anlatırsın?
3. Boş bir Spring Boot web uygulamasında Tomcat'i kim, neye bakarak başlatıyor?
4. `@MockBean` yazan bir blog yazısına rastladın. Bu yazı hangi sürüm için yazılmıştır, nereden anladın?
5. "Dependency injection Spring'e özgü bir tekniktir" cümlesi neden yanlış?

➡️ **Cevaplar:** [`cevaplar.md#001-spring-boot-ne-yapıyor-ve-nerede-kaybediliyor`](cevaplar.md#001-spring-boot-ne-yapıyor-ve-nerede-kaybediliyor) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

<!-- skor:baslangic -->
<!-- skor:bitis -->

## Sırada ne var
➡️ [`02-kurulum.md`](02-kurulum.md) — laboratuvar projesini kuruyoruz.
