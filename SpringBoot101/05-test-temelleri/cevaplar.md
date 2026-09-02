# 05 · Test temelleri — Kendini kontrol cevapları

> Bu dosya [5.1](5.1-slice-test.md) – [5.2](5.2-mockitobean.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:**
[5.1](#51-test-piramidi-ve-slice-testler) ·
[5.2](#52-mockitobean-ve-spring-boot-4-test-değişiklikleri)

---

## 5.1 Test piramidi ve slice test'ler

📄 Sorular: [`5.1-slice-test.md`](5.1-slice-test.md)

### Soru 1 — Aynı uç noktanın slice test'i ile tam testi neden farklı cevaplar döndürdü?

**Kısa cevap:** Çünkü **container'larındaki `SiparisServisi` farklıydı.** Slice test'te
servis `@MockitoBean` ile mock'landı (`MOCK` döndü), tam testte container'da gerçek servis
vardı (`GERCEK-A-1` döndü).

**Ayrıntı:**

Aynı uç nokta (`/siparisler/durum/A-1`), iki test — ölçülen sonuçlar:

```
Tests run: 1 ... Time elapsed: 0.174 s -- in SliceTest      (@WebMvcTest)
Tests run: 1 ... Time elapsed: 1.083 s -- in FullContextTest        (@SpringBootTest)
```

| | `SliceTest` | `FullContextTest` |
|---|---|---|
| Anotasyon | `@WebMvcTest(SiparisKontrolcusu.class)` | `@SpringBootTest` + `@AutoConfigureMockMvc` |
| Container'da ne var | Web katmanı + belirtilen kontrolcü | **Her şey** |
| `SiparisServisi` | `@MockitoBean` ile **mock** | **Gerçek** |
| Yanıt | `MOCK` | `GERCEK-A-1` |
| Süre | **0,174 sn** | 1,083 sn (~6,5 kat) |

Kritik nokta: **yanıtı uç nokta değil, container'daki bean belirliyor.** İkisi de aynı HTTP
isteğini atıyor, ama biri mock ile, diğeri gerçekle konuşuyor.

Bu bir "tutarsızlık" değil, **iki farklı soru**:

| Test | Sorduğu soru |
|---|---|
| Slice | Kontrolcü, servisten gelen cevabı doğru biçimde HTTP'ye çeviriyor mu? |
| Tam | Bütün parçalar birbirine bağlandığında çalışıyor mu? |

İkisi de gerekli — ama **aynı oranda değil**: çok birim, orta slice, az tam container.

> 📌 **Sık yapılan hata:** Farklı çıktıyı görüp "slice test gerçeği ölçmüyor, atalım"
> demek. Slice test'in işi gerçeği ölçmek değil; **sınırdaki çeviriyi** ölçmek.

🔗 Konu: [5.1 §1 Ölçüm](5.1-slice-test.md) · [5.1 §2 Test piramidi](5.1-slice-test.md)

---

### Soru 2 — Testlerin yavaş. Aracı değiştirmeden önce hangi tasarım sorusunu sormalısın?

**Kısa cevap:** **"İş mantığım gerçekten Spring'e bağımlı mı?"** Yavaş test paketinin en sık
çözümü test aracını değiştirmek değil, mantığı Spring'den ayırıp `new` ile test etmektir.

**Ayrıntı:**

Somut örnek — yanlış hâli:

```java
@SpringBootTest
class SiparisHesaplayiciTest {
    @Autowired SiparisHesaplayici hesaplayici;

    @Test
    void indirim() {
        assertThat(hesaplayici.indirimliTutar(10_000, 10)).isEqualTo(9_000);
    }
}
```

Saf bir çarpma işlemi için bütün container ayağa kalkıyor. Doğrusu:

```java
class SiparisHesaplayiciTest {
    @Test
    void indirim_yuzde_on() {
        var h = new SiparisHesaplayici();                 // sadece new
        assertThat(h.indirimliTutar(10_000, 10)).isEqualTo(9_000);
    }
}
```

Maliyet tablosu:

| Katman | Test başına | Kaç tane olmalı |
|---|---|---|
| Birim (Spring yok) | Milisaniye | Yüzlerce |
| Slice (`@WebMvcTest`) | ~0,2 sn | Onlarca |
| Tam container (`@SpringBootTest`) | ~1 sn+ | Birkaç tane |

Hesap: 300 testin hepsi tam container testiyse ~5 dakika; piramide oturtulduğunda aynı
kapsam saniyelere iner.

Ve neden önemli: **çalıştırılmayan test, olmayan testtir.** 11 dakikalık bir paket kimse
tarafından çalıştırılmaz; yani bu bir hız değil, testin **var olup olmadığı** sorunudur.

> 📌 **Sık yapılan hata:** Sorunu paralel çalıştırma ya da daha güçlü CI makinesiyle
> çözmeye çalışmak. İkisi de 300 gereksiz container kurulumunu ortadan kaldırmaz, yalnızca
> aynı anda yapar.

🔗 Konu: [5.1 §2 Test piramidi](5.1-slice-test.md) · [5.1 §7 Hata 1](5.1-slice-test.md)

---

### Soru 3 — `@WebMvcTest` neden `@Service` sınıflarını container'a almıyor? Bu bir eksiklik mi?

**Kısa cevap:** **Eksiklik değil, tasarım.** Slice yalnızca web katmanını kurar; kontrolcünün
bağımlılıklarını **sen** mock'larsın, böylece test yalnızca web katmanının davranışını ölçer.

**Ayrıntı:**

`@WebMvcTest` container'a ne koyar, ne koymaz:

| Container'a giren | Container'a **girmeyen** |
|---|---|
| Kontrolcüler (`@Controller` ailesi) | `@Service` |
| `@ControllerAdvice` | `@Repository` |
| Dönüştürücüler, filtreler | Diğer `@Component`'ler |

"Eksiklik" olmadığının kanıtı, alternatifin ne olduğuna bakmak: `@Service` de container'a
girseydi, o servis kendi bağımlılıklarını isterdi, o bağımlılıklar veri kaynağını isterdi —
ve elinde `@SpringBootTest` olurdu. Yani slice diye bir şey kalmazdı.

Bağımlılığı mock'lamak testi zayıflatmaz; **sınırı belirler**:

```java
@WebMvcTest(SiparisKontrolcusu.class)
class SliceTest {
    @Autowired MockMvcTester mvc;
    @MockitoBean SiparisServisi servis;      // sınırın dışı mock'lanır

    @Test
    void mock_servis_ile_calisir() {
        given(servis.durum("A-1")).willReturn("MOCK");
        assertThat(mvc.get().uri("/siparisler/durum/A-1"))
                .hasStatusOk().hasBodyTextEqualTo("MOCK");
    }
}
```

Bu test şunu ölçüyor: adres eşleşiyor mu, parametre bağlanıyor mu, dönen değer doğru
biçimde HTTP'ye yazılıyor mu. Servisin **içinde** ne olduğu bu testin sorusu değil.

⚠️ İlgili tuzak: kontrolcüne `@Component` yazarsan `@WebMvcTest` onu **bulmaz** — slice
yalnızca `@Controller` ailesini tarar ([2.1](../02-anotasyon-haritasi/2.1-stereotype.md)).

> 📌 **Sık yapılan hata:** "Bean bulunamadı" hatasını `@SpringBootTest`'e geçerek susturmak.
> Hata gerçekten kaybolur — birlikte slice test'in bütün kazancı da kaybolur.

🔗 Konu: [5.1 §3 Slice anotasyonları](5.1-slice-test.md) · [5.1 §4 Slice test yazmak](5.1-slice-test.md)

---

### Soru 4 — Test sınıfına `SiparisTesti` adını verdin. Ne olur, nasıl fark edersin?

**Kısa cevap:** Test **hiç çalışmaz** ve yapı `BUILD SUCCESS` der. Fark etmenin yolu renge
değil, **çalışan test sayısına** (`Tests run:`) bakmaktır.

**Ayrıntı:**

Maven Surefire yalnızca şu kalıplara uyan sınıfları çalıştırır:

```
*Test      Test*      *Tests      *TestCase
```

`SiparisTesti` hiçbirine uymaz — Türkçedeki iyelik eki `i`, kalıbı bozar. Çıktı:

```
[INFO] --- surefire:3.5.6:test ---
[INFO] BUILD SUCCESS
```

**Test hiç çalışmadı, ama derleme başarılı.** Ne hata, ne uyarı, ne "skipped" satırı.

Lab'da ölçülen fark:

| Sınıf adı | Çıktı |
|---|---|
| `SliceTest` + `FullContextTest` | `Tests run: 2` |
| `SliceTesti` + `FullContextTest` | `Tests run: 1` — biri sessizce yok |

Fark etme yolları, en güvenilirden başlayarak:

| Yol | Ne yaparsın |
|---|---|
| **Sayıya bakmak** | `Tests run:` değerini beklediğinle karşılaştır |
| Kasten kırmak | Yeni testte `assertThat(1).isEqualTo(2)` yaz; kırmızı olmuyorsa çalışmıyordur |
| Coverage raporu | Yazdığın sınıfın hiç kapsanmadığını görürsün |

Metot adları Türkçe olabilir (`mock_servis_ile_calisir`), **sınıf adı** kalıba uymak
zorunda.

> 📌 **Sık yapılan hata:** Yeşil yapıyı testin geçtiğinin kanıtı saymak. Bu, kursun en sinsi
> hatası: **başarısız olmayan, hiç çalışmayan test.** Yeşil renk "testler geçti" demek
> değil, "kırmızı olan bir şey yok" demektir.

🔗 Konu: [5.1 §7 Hata 2](5.1-slice-test.md)

---

### Soru 5 — Boot 3 için yazılmış bir `@SpringBootTest` örneğini 4'e taşırken hangi satırı eklemelisin?

**Kısa cevap:** **`@AutoConfigureMockMvc`.** Boot 4'te `@SpringBootTest` MockMvc'yi
kendiliğinden getirmiyor.

**Ayrıntı:**

Eklemezsen ölçülen hata:

```
No qualifying bean of type 'MockMvcTester' available
```

Doğrusu:

```java
@SpringBootTest
@AutoConfigureMockMvc                 // ← Spring Boot 4'te ZORUNLU
class FullContextTest {
    @Autowired MockMvcTester mvc;
    ...
}
```

Bu, tek başına duran bir değişiklik değil; aynı mantığın bir ailesi var:

| Spring Boot 3 | Spring Boot 4 |
|---|---|
| `@SpringBootTest` → MockMvc hazır | **`@AutoConfigureMockMvc` gerekir** |
| `@SpringBootTest` → `TestRestTemplate` hazır | **`@AutoConfigureTestRestTemplate` gerekir** |
| `org.springframework.boot.test.web.client.TestRestTemplate` | **`org.springframework.boot.resttestclient.TestRestTemplate`** |
| `org.springframework.boot.test.autoconfigure.web.servlet.*` | **`org.springframework.boot.webmvc.test.autoconfigure.*`** |

Son satır ayrı bir hata üretir ve karıştırılır: `@WebMvcTest` **ve** `@AutoConfigureMockMvc`
artık `org.springframework.boot.webmvc.test.autoconfigure` paketinde. Eski `import`
`package ... does not exist` der. İnternetteki her örnek eski paketi yazıyor.

İki hatayı ayırt et:

| Hata mesajı | Eksik olan |
|---|---|
| `No qualifying bean of type 'MockMvcTester'` | `@AutoConfigureMockMvc` satırı |
| `package ... does not exist` | `import` satırındaki paket adı |

> 📌 **Sık yapılan hata:** Hatayı görünce `MockMvcTester`'ı bırakıp eski `MockMvc` yazımına
> dönmek. Eksik olan sınıf değil, tek bir anotasyon satırı.

🔗 Konu: [5.1 §5 Tam container testi](5.1-slice-test.md) · [5.1 §7 Hata 3](5.1-slice-test.md) · [5.2 §2 Geçiş tablosu](5.2-mockitobean.md)

---

## 5.2 @MockitoBean ve Spring Boot 4 test değişiklikleri

📄 Sorular: [`5.2-mockitobean.md`](5.2-mockitobean.md)

### Soru 1 — `@Mock` ile `@MockitoBean` farkı; yanlışını kullanınca ne olur?

**Kısa cevap:** **`@MockitoBean` mock'u Spring container'ına bean olarak koyar; `@Mock`
container'a dokunmaz**, yalnızca alana bir mock atar. Yanlışını (`@Mock`) yazarsan gerçek
bean devrede kalır ve **testin sessizce yanlış şeyi ölçer.**

**Ayrıntı:**

Lab'da ölçülen davranış — aynı test, iki anotasyon:

| Alan tanımı | Uç noktanın döndürdüğü |
|---|---|
| `@Mock SiparisServisi mockServis;` | `GERCEK-A-1` — **gerçek servis çalıştı** |
| `@MockitoBean SiparisServisi servis;` + `given(...)` | `MOCK` |

Yanlış hâli:

```java
@SpringBootTest
class SiparisTest {
    @Mock SiparisServisi servis;             // ← container'a KOYMAZ
    @Autowired SiparisKontrolcusu kontrolcu; // gerçek servisi alır
}
```

Test **geçer**. Mock'a verdiğin davranış hiç kullanılmaz, kontrolcü gerçek servisi çağırır.
Yani mock'ladığını sanırsın, mock'lamamışsındır — [4.1](../04-web-katmani/4.1-request-mapping.md)'deki
`@Valid` tuzağının test tarafındaki kardeşi.

Hangisi ne zaman:

| Anotasyon | Ne yapar | Nerede kullanılır |
|---|---|---|
| `@MockitoBean` | Container'daki bean'i mock ile **değiştirir** | Spring testi (slice ya da tam container) |
| `@MockitoSpyBean` | Gerçek bean'i **sarar** | Bazı metotları izlemek/değiştirmek |
| `@Mock` | Container'dan bağımsız mock | Spring'siz saf birim testi (+ `@ExtendWith(MockitoExtension.class)`) |

⚠️ `@MockitoBean` yalnızca **test sınıfının alanına** yazılır; `@Configuration` sınıfına
yazamazsın — Boot 3'te mümkündü, artık değil.

> 📌 **Sık yapılan hata:** "Mock'ladım ama gerçek kod çalışıyor" şikâyetinde koda bakmak.
> Cevap neredeyse her zaman aynı: `@Mock` yazılmış, `@MockitoBean` olmalıydı.

🔗 Konu: [5.2 §1 @MockitoBean ne yapıyor](5.2-mockitobean.md) · [5.2 §6 Hata 2](5.2-mockitobean.md)

---

### Soru 2 — `given(...)` yazmadığın bir metot ne döner? Bu neden tehlikeli?

**Kısa cevap:** **Varsayılan değer:** nesnelerde `null`, `int`'te `0`, `boolean`'da `false`.
Tehlikeli çünkü **test geçmeye devam eder** — ölçtüğü şey yanlış olduğu hâlde.

**Ayrıntı:**

Lab'da ölçülen durum:

```java
@Test
void mocklanmamis_metot() {
    // given(servis.durum("A-1")).willReturn("MOCK");     ← yorum satırı
    assertThat(mvc.get().uri("/siparisler/durum/A-1")).hasStatusOk();
}
```

Sonuç: **test geçti.** Uç nokta `null` döndürdü, ama test yalnızca durum kodunu kontrol
ettiği için bunu görmedi.

| Dönüş türü | Mock'lanmamış metodun döndürdüğü |
|---|---|
| Nesne (`String`, `Siparis`, `List`…) | `null` |
| `int`, `long`, `double` | `0` |
| `boolean` | `false` |

Tehlikenin iki katmanı var:

1. **Sessizlik:** Mockito uyarı vermez; mock'lanmamış çağrı meşru bir kullanımdır.
2. **Yanlış güven:** Yeşil test, "bu uç nokta çalışıyor" diye okunur. Oysa test yalnızca
   "200 dönüyor" diyor; **ne döndürdüğünü** kimse ölçmüyor.

Korunma yolları:

| Yol | Ne sağlar |
|---|---|
| Kullandığın her metoda `given(...)` yaz | Mock'un ne döneceği açık olur |
| Beklentiyi gövdeye kadar yaz (`hasBodyTextEqualTo`) | Yalnızca durum kodu ölçülmez |
| Testi bilerek kır (beklentiyi değiştir) | Testin gerçekten bir şey ölçtüğünü doğrularsın |

> 📌 **Sık yapılan hata:** Yalnızca `hasStatusOk()` yazan testler biriktirmek. Bu testler
> uç noktanın **var olduğunu** doğrular, **doğru çalıştığını** değil.

🔗 Konu: [5.2 §6 Hata 3](5.2-mockitobean.md) · [5.2 §1 @MockitoBean ne yapıyor](5.2-mockitobean.md)

---

### Soru 3 — Bir testte 5 tane `@MockitoBean` var. Hangi iki soruyu sormalısın?

**Kısa cevap:** 1) **"Yanlış katmanda mı test ediyorum?"** — bir slice test bu işi görür
müydü? 2) **"Test edilen sınıfın bağımlılıkları fazla mı?"** — constructor uzuyorsa tasarım
konuşuyordur.

**Ayrıntı:**

```java
@SpringBootTest
class SiparisAkisiTest {
    @MockitoBean SiparisServisi servis;
    @MockitoBean StokServisi stok;
    @MockitoBean OdemeServisi odeme;
    @MockitoBean BildirimServisi bildirim;      // ← her şey mock
}
```

Bu test **hiçbir şey ölçmüyor.** Bütün gerçek davranış mock'landığı için geriye yalnızca
senin mock'lara verdiğin cevapların doğru sırayla döndüğünü doğrulamak kaldı — yani testin,
kodun değil **kendi varsayımlarının** testi.

İki sorunun cevabına göre ne yaparsın:

| Cevap | Ne yaparsın |
|---|---|
| Yanlış katmandayım | Tam container testini bırak, slice test'e (ya da saf birim testine) in |
| Sınıfın bağımlılıkları fazla | Sınıfı böl — [1.3](../01-container-ve-bean/1.3-dependency-injection.md)'teki "constructor uzuyorsa tasarım konuşuyor" işareti |

Sınırı çizen kural:

| Mock'la | Mock'lama |
|---|---|
| Dış HTTP servisleri | Test ettiğin sınıfın kendisi |
| Ödeme, e-posta, SMS gibi yan etkili servisler | Saf hesaplama yapan sınıflar |
| Yavaş ya da kırılgan bağımlılıklar | Değeri olan iş mantığı |

Bir de görünmeyen maliyet: her farklı `@MockitoBean` kümesi **ayrı bir container** demektir
([5.1](5.1-slice-test.md)). Yani fazla mock'lamak yalnızca ölçümü zayıflatmaz, test
paketini de yavaşlatır.

> 📌 **Sık yapılan hata:** Mock sayısını bir performans konusu sanmak. Asıl mesele ölçüm:
> beşinci mock'u eklediğinde testin neyi doğruladığını bir cümleyle söyleyemiyorsan,
> muhtemelen hiçbir şeyi doğrulamıyordur.

🔗 Konu: [5.2 §4 Mock'lamanın sınırı](5.2-mockitobean.md)

---

### Soru 4 — Boot 3 test sınıfını 4'e taşıyorsun. Hangi beş şeyi değiştirmen gerekebilir?

**Kısa cevap:** 1) `@MockBean` → `@MockitoBean` (ve `@SpyBean` → `@MockitoSpyBean`).
2) `@SpringBootTest`'e `@AutoConfigureMockMvc` / `@AutoConfigureTestRestTemplate` ekle.
3) Taşınan paketleri düzelt. 4) `@Mock`/`@Captor` için `@ExtendWith(MockitoExtension.class)`
ekle. 5) Teknolojiye özel test starter'ını bağımlılıklara ekle.

**Ayrıntı:**

| # | Boot 3 | Boot 4 | Yazmazsan gördüğün |
|---|---|---|---|
| 1 | `@MockBean` / `@SpyBean` | `@MockitoBean` / `@MockitoSpyBean` | `cannot find symbol: class MockBean` |
| 2 | `@SpringBootTest` yeter | `@AutoConfigureMockMvc`, `@AutoConfigureTestRestTemplate` | `No qualifying bean of type 'MockMvcTester' available` |
| 3 | `org.springframework.boot.test.autoconfigure.web.servlet.*` | `org.springframework.boot.webmvc.test.autoconfigure.*` | `package ... does not exist` |
| 3 | `org.springframework.boot.test.web.client.TestRestTemplate` | `org.springframework.boot.resttestclient.TestRestTemplate` | `package ... does not exist` |
| 4 | `MockitoTestExecutionListener` `@Mock`'u işlerdi | `@ExtendWith(MockitoExtension.class)` gerekiyor | Alanlar `null`, hata `NullPointerException` |
| 5 | Tek `spring-boot-starter-test` | `spring-boot-starter-<teknoloji>-test` (ör. `-security-test`) | `@WithMockUser` bulunamıyor |

`@MockitoBean` ve `@MockitoSpyBean`'in paketi: `org.springframework.test.context.bean.override.mockito`
— dikkat, artık `boot` altında değil.

Dördüncü satır en sinsi olanı: hata mesajı `NullPointerException`'dır ve **sebebi
göstermez**. `@Mock` alanının neden `null` kaldığını arayana kadar zaman gider.

Bu kursta gördüğün sürümler (Spring Boot 4.1.1 ile gelenler): **JUnit Jupiter 6.0.3**,
**Mockito 5.23.0**, **AssertJ 3.27.7**.

> 📌 **Sık yapılan hata:** Anotasyon adlarını düzeltip `import` satırlarını eski paketlerde
> bırakmak. `cannot find symbol` gider, `package does not exist` gelir — ve iki hata farklı
> göründüğü için ilkini çözdüğünü sanırsın.

🔗 Konu: [5.2 §2 Spring Boot 4 geçiş tablosu](5.2-mockitobean.md) · [5.1 §5 Tam container testi](5.1-slice-test.md)

---

### Soru 5 — `verify` çağrılarını çoğaltmanın zararı nedir?

**Kısa cevap:** Test, kodun **davranışını** değil **yapısını** dondurur. İç düzenleme
(refactor) yaptığında davranış aynı kalsa bile testler kırmızıya döner — ve o testler artık
seni koruyan değil, engelleyen bir yük olur.

**Ayrıntı:**

Doğru `verify` — sınırdaki yan etkiyi doğrular:

```java
@Test
void siparis_alinca_stok_dusulur() {
    servis.siparisAl(new Siparis("S-1", "ABC", 2));

    verify(stok).dus("ABC", 2);              // çağrıldı mı, doğru argümanlarla mı
}
```

Aşırıya kaçmış hâli, her ara çağrıyı da doğrular: `verify(depo).bul(...)`,
`verify(donusturucu).cevir(...)`, `verify(gunlukcu).yaz(...)`. Bunların hiçbiri **dışarıdan
gözlenebilir bir davranış** değil; hepsi iç uygulama ayrıntısı.

Sonuç:

| | Davranışı doğrulayan test | Her çağrıyı `verify` eden test |
|---|---|---|
| İç düzenleme yapınca | Yeşil kalır | **Kırmızıya döner** |
| Gerçek bir hata girince | Kırmızıya döner | Kırmızıya döner |
| Sana söylediği | "Davranış bozuldu" | "Kod değişti" — bunu zaten biliyorsun |

İkinci sütunun bedeli şudur: 40 test kırıldığında ekip iki şeyden birini yapar — ya
düzenlemeden vazgeçer (kod donar), ya da testleri gözden geçirmeden toplu olarak günceller
(testler anlamını yitirir). İkisi de kayıp.

Kural:

| Doğrula | Doğrulama |
|---|---|
| Yan etkinin gerçekleştiğini (`verify`) | Her metot çağrısını tek tek |
| Dönen değerin doğruluğunu | Mock'un kendi davranışını |
| Sınırdaki sözleşmeyi | İç uygulama ayrıntısını |

> 📌 **Sık yapılan hata:** `verify` sayısını test kalitesinin ölçüsü sanmak. İyi test **ne
> olduğunu** doğrular, **nasıl yapıldığını** değil.

🔗 Konu: [5.2 §5 Ne doğrulanmalı](5.2-mockitobean.md)

---

⬅️ [Bölüme dön](5.1-slice-test.md) · 📄 [5.2](5.2-mockitobean.md) · 📖 [4. bölüm](../04-web-katmani/4.1-request-mapping.md)
