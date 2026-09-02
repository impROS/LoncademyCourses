# 00 · Başlangıç — Kendini kontrol cevapları

> Bu dosya [00.1](01-genel-bakis.md) konusunun sonundaki **"Kendini kontrol"** sorularının
> ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [00.1](#001-spring-boot-ne-yapıyor-ve-nerede-kaybediliyor)

---

## 00.1 Spring Boot ne yapıyor, ve nerede kaybediliyor

📄 Sorular: [`01-genel-bakis.md`](01-genel-bakis.md)

### Soru 1 — `new` ile kurduğun bir nesnenin `@Autowired` alanı neden dolmaz?

**Kısa cevap:** Çünkü injection'ı yapan **container**'dır (nesneleri kuran ve birbirine
bağlayan Spring parçası), ve container yalnızca **kendi kurduğu** nesnelere injection yapar.
`new` ile kurduğun nesne bir bean değildir; container'ın haritasında kaydı yoktur.

**Ayrıntı:**

Anotasyon sihirli bir işaret değil; birinin onu **okuyup** karşılığında iş yapması gerekir.
O okuyucu container'dır ve yalnızca kurulum hattından geçen nesnelere bakar:

| Nesne nasıl doğdu | Container onu tanır mı | `@Autowired` alan |
|---|---|---|
| `@Service` sınıfı, tarama bulmuş | ✅ Evet | Dolar |
| `@Bean` metodu döndürmüş | ✅ Evet | Dolar |
| `new SiparisServisi(...)` yazmışsın | ❌ Hayır | **`null` kalır** |
| `@Service` sınıfından `new` ile kurulmuş ikinci nesne | ❌ Hayır | `null` kalır |

Son satır en can alıcı olanı: **anotasyon sınıfa yazılır, ama bean'lik nesneye aittir.**
Sınıfın başında `@Service` yazması, o sınıftan senin elle kurduğun nesneyi bean yapmaz.

Ölçmek istersen — sınıftan `@Service`'i sil ve container'ı sorgula:

```
No qualifying bean of type 'tr.loncademy.siparis.SiparisServisi' available
```

Sınıf hâlâ yerinde ve derleniyor. Container onu tanımıyor. **Bean olmak, var olmak değildir.**

Aynı sebep, ileride şu üç şikâyetin de cevabıdır: `@Transactional` çalışmıyor (201),
`@Cacheable` yok sayılıyor (301), ayar okunmuyor. Hepsi tek soruya iner: **bu nesneyi kim
kurdu?**

> 📌 **Sık yapılan hata:** `@Autowired` alanı `null` gelince anotasyona ya da paket
> yapısına bakmak. Önce **nesnenin doğum yerine** bak: kodda o nesne için bir `new` var mı?
> Varsa arayışın orada biter.

🔗 Konu: [00.1 §2 Container](01-genel-bakis.md) · [1.1 Container: nesneleri kim kuruyor](../01-container-ve-bean/1.1-container-nedir.md)

---

### Soru 2 — Spring Framework ile Spring Boot farkını tek cümlede nasıl anlatırsın?

**Kısa cevap:** *"Spring Framework işi yapan çekirdektir; Spring Boot onu senin yerine
yapılandıran, üstünde duran ayrı bir katmandır."*

**Ayrıntı:**

| | **Spring Framework** | **Spring Boot** |
|---|---|---|
| Ne yapar | Container, dependency injection, web katmanı, transaction, AOP | Framework'ü yapılandırır |
| Olmasaydı | Hiçbir şey olmazdı | Aynı uygulamayı ~300 satır yapılandırma ile yazardın |
| Sürüm | 7.0.x | 4.1.x |

Cümleyi doğrulayan en hızlı kanıt **sürüm numaralarıdır**: aynı ürün olsalardı iki farklı
sürüm serisi olmazdı. Boot 4.1.1 çalıştıran bir uygulamanın içindeki Framework 7.0.x'tir.

Boot'un kattığı **üç** şey, tek tek sayılabilir: auto-configuration, starter'lar,
embedded server. Bu listede **dependency injection ve `ApplicationContext` yoktur** — onlar
Framework'e aittir.

Bunun sana pratik faydası şu: bu kursta öğrendiğin container ve bean bilgisi Framework
bilgisidir, Boot sürümü değişse de geçerliliğini korur. Değişen, Boot'un yaptığı varsayılan
tercihlerdir.

> 📌 **Sık yapılan hata:** "Boot yeni Spring" demek. Boot bir şey **icat etmez**, var olanı
> senin yerine ayarlar. Bu cümleyi kurarsan mülakatta da, hata ayıklarken de doğru yere
> bakarsın.

🔗 Konu: [00.1 §3 Spring ≠ Spring Boot](01-genel-bakis.md)

---

### Soru 3 — Boş bir Spring Boot web uygulamasında Tomcat'i kim, neye bakarak başlatıyor?

**Kısa cevap:** **Auto-configuration** başlatıyor; **classpath**'e (uygulamanın gördüğü
kütüphanelerin listesi) bakarak karar veriyor. Web starter'ı orada olduğu için "demek ki
web uygulaması yazıyor" sonucuna varıyor.

**Ayrıntı:**

8 satır Java kodu yazılmış bir uygulamanın gerçek açılış çıktısı:

```
 :: Spring Boot ::                (v4.1.1)

... Starting SiparisServisiUygulamasi using Java 23.0.2 with PID 67147
... o.s.boot.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
... o.apache.catalina.core.StandardEngine : Starting Servlet engine: [Apache Tomcat/11.0.24]
... o.s.boot.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
... Started SiparisServisiUygulamasi in 0.635 seconds (process running for 0.789)
```

Kararın zinciri:

```
pom.xml'e web starter'ı eklendi
   ↓
Tomcat sınıfları classpath'e girdi
   ↓
@EnableAutoConfiguration koşulları değerlendirdi: "Tomcat var mı? Var."
   ↓
Sunucu bean'leri kuruldu, 8080 dinlemeye başladı
```

Kararı veren **`@SpringBootApplication` değildir** — o anotasyon yalnızca
auto-configuration'ı *açar*. Web bağımlılığı olmayan bir projede aynı anotasyonla hiçbir
sunucu açılmaz. Aynı mekanizma yazmadığın 404 gövdesini de üretir:

```
{"timestamp":"2026-09-02T21:07:28.889Z","status":404,"error":"Not Found","path":"/yok"}
```

Bunu da varsayılan hata işleyicisi yazdı — o da auto-configuration'ın kurduğu bir bean.

> 📌 **Sık yapılan hata:** "Sihir" deyip orada durmak. Sihir değil, bir dizi `if` — sadece
> `if`'ler senin dosyalarında değil, kütüphanenin içinde. Hangi kararın **neden** alındığını
> satır satır görmeyi 2.3'te öğreneceksin.

🔗 Konu: [00.1 §4 Auto-configuration'ın bedeli](01-genel-bakis.md)

---

### Soru 4 — `@MockBean` yazan bir blog yazısı hangi sürüm içindir, nereden anladın?

**Kısa cevap:** **Spring Boot 3.x** (ya da daha eskisi) için. Anladın çünkü `@MockBean`
4.x'te yok; yerini **`@MockitoBean`** aldı ve o kod 4.1.1'de **derlenmez**.

**Ayrıntı:**

Anotasyon adları sürüm parmak izidir. Bir örneğe bakarken tarih arama — şu işaretlere bak:

| Örnekte bunu görürsen | Yazı şu sürüm içindir | 4.1.1'deki karşılığı |
|---|---|---|
| `@MockBean` | 3.x | `@MockitoBean` |
| `import javax.annotation.PostConstruct` | 2.x | `jakarta.annotation.PostConstruct` |
| `com.fasterxml.jackson...` | 3.x | `tools.jackson...` (Jackson 3) |
| `@SpringBootTest` + doğrudan `MockMvc` | 3.x | `@AutoConfigureMockMvc` eklenir |
| Undertow yapılandırması | 3.x | Sunucu kaldırıldı |

Kopyalarsan ne olur: sınıf classpath'te olmadığı için **derleme hatası** alırsın. Bu
aslında iyi haber — sessizce yanlış çalışmaktan iyidir. Tehlikeli olan durum, yanlış
paketten benzer adlı bir sınıfın içeri alınıp kodun derlenmesi, ama anotasyonun hiç
çalışmamasıdır; `javax`/`jakarta` karışıklığında tam olarak bu olur.

> 📌 **Sık yapılan hata:** "Spring Boot Spring Boot'tur" deyip kopyalamak. Bir örneğe
> bakarken **önce sürümüne bak**; sürüm yazmıyorsa yukarıdaki tablodan çıkar.

🔗 Konu: [00.1 §5 Spring Boot 4 ne değiştirdi](01-genel-bakis.md)

---

### Soru 5 — "Dependency injection Spring'e özgü bir tekniktir" cümlesi neden yanlış?

**Kısa cevap:** Çünkü fikrin kendisi **constructor'dır** ve Spring'siz de geçerli bir
tasarımdır. Anotasyon, o fikri Spring'e bildirme biçiminden ibarettir.

**Ayrıntı:**

Aynı sınıfın iki hâli — ikisinde de dependency injection var, birinde Spring yok:

```java
// Spring yok, anotasyon yok — ama bu dependency injection'dır
public class SiparisServisi {
    private final StokIstemcisi stok;
    private final OdemeIstemcisi odeme;

    public SiparisServisi(StokIstemcisi stok, OdemeIstemcisi odeme) {
        this.stok = stok;
        this.odeme = odeme;
    }
}
```

```java
// Spring var — TEK fark, nesneleri kimin kurduğu
@Service
public class SiparisServisi {
    private final StokIstemcisi stok;
    private final OdemeIstemcisi odeme;

    public SiparisServisi(StokIstemcisi stok, OdemeIstemcisi odeme) { ... }
}
```

Gövde birebir aynı. Dependency injection zaten birinci örnekte var: sınıf "bana lazım
olanlar şunlar" diyor, "onları şöyle kuracağım" demiyor. `@Service` satırının eklediği tek
şey, **kurma ve bağlama işini** container'ın devralması.

İki ayrımı ayrı tut:

| | Ne | Spring gerekir mi |
|---|---|---|
| **Dependency injection** | Bağımlılığı içeride kurma, dışarıdan al | ❌ Hayır |
| **Container (IoC container)** | O nesneleri kuran, bağlayan, tutan altyapı | ✅ Evet (ya da benzeri bir araç) |

Testi bile Spring çözmez, constructor çözer: `new SiparisServisi(sahteStok, sahteOdeme)`
yazabilmenin sebebi anotasyon değil, imzadır.

> 📌 **Sık yapılan hata:** Dependency injection'ı "anotasyon yazma tekniği" sanmak. Sınama
> yöntemi basit: **anotasyonu sil, tasarım ayakta kalıyor mu?** Kalıyorsa fikir zaten
> senindi. Constructor'ı sil — geriye hiçbir şey kalmaz.

🔗 Konu: [00.1 §1 Dert: `new` ile bağlanan kod](01-genel-bakis.md) · [1.3 Dependency injection](../01-container-ve-bean/1.3-dependency-injection.md)

---

⬅️ [Konuya dön](01-genel-bakis.md) · ✅ [00.1 Testi](01-test.html) · ➡️ [Kurulum](02-kurulum.md)
