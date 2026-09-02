# 04 · Web katmanı — Kendini kontrol cevapları

> Bu dosya [4.1](4.1-request-mapping.md) – [4.3](4.3-restclient.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:**
[4.1](#41-request-mapping-ve-body-binding) ·
[4.2](#42-exception-handling-ve-problemdetail) ·
[4.3](#43-restclient-ile-dışarıya-çağrı)

---

## 4.1 Request mapping ve body binding

📄 Sorular: [`4.1-request-mapping.md`](4.1-request-mapping.md)

### Soru 1 — `@Valid` yazmayı unuttun. Uygulama hata verir mi, istek reddedilir mi, ne olur?

**Kısa cevap:** Hiçbiri. Uygulama açılır, istek **reddedilmez**, geçersiz veri sorunsuz içeri
girer ve sen `201` dönersin. Ne hata ne uyarı vardır.

**Ayrıntı:**

Aynı istek, tek satırın farkıyla iki sonuç verir — ölçüldü:

| Kontrolcü imzası | `{"urunKodu":"","adet":0}` gönderince |
|---|---|
| `olustur(@Valid @RequestBody YeniSiparis istek)` | `HTTP/1.1 400` |
| `olustur(@RequestBody YeniSiparis istek)` | `HTTP/1.1 201` — boş `urunKodu`, `adet=0` içeri girdi |

`record` aynı `record`, kısıtlar aynı yerde duruyor:

```java
public record YeniSiparis(@NotBlank String urunKodu, @Min(1) int adet) {}
```

Yani `@NotBlank` ve `@Min(1)` **birer etikettir**; onları çalıştıran şey `@Valid`'dir.
`@Valid` yoksa doğrulayıcı hiç çağrılmaz, dolayısıyla ihlal edilecek bir kısıt da yoktur.

Fark edilmemesinin sebebi tam olarak bu: hata mesajı üretecek bir mekanizma çalışmadığı
için ortada bir hata da yok. Kodu okuyan kişi kısıtları görür ve "doğrulama var" der.

**Bunu kendi kodunda 30 saniyede sınamanın yolu:** bilerek geçersiz bir gövde gönder.
`400` alıyorsan doğrulama var, `201` alıyorsan yok.

> 📌 **Sık yapılan hata:** Kısıt anotasyonunu görmeyi doğrulamanın kanıtı saymak. Bu,
> [3.2](../03-yapilandirma/3.2-configuration-properties.md)'deki `@Validated` tuzağının aynı
> ailesi: *doğrulama koyduğunu sanmak, koymamış olmak.*

🔗 Konu: [4.1 §3 Gövde bağlama ve doğrulama](4.1-request-mapping.md) · [4.1 §6 Hata 1](4.1-request-mapping.md)

---

### Soru 2 — 404 ile 405 arasındaki fark ve 405'in hangi başlığı işine yarar?

**Kısa cevap:** **404 = bu adres hiç eşleşmiyor. 405 = adres eşleşiyor, HTTP metodu yanlış.**
İşine yarayan başlık **`Allow:`** — adresin kabul ettiği metotları listeler.

**Ayrıntı:**

Ölçülen çıktı:

```
$ curl -i -X DELETE localhost:8080/siparisler/1

HTTP/1.1 405
Allow: GET
```

Kontrolcüde yalnızca `@GetMapping("/{no}")` var; adres eşleşiyor, `DELETE` eşleşmiyor.

Teşhis olarak okunuşu:

| Aldığın | Ne biliyorsun | Sıradaki hamlen |
|---|---|---|
| **404** | Adres yanlış ya da eşleme hiç kaydedilmemiş | Yolu ve sınıf düzeyindeki `@RequestMapping` önekini kontrol et |
| **405** + `Allow: GET` | Adres **doğru** | `curl`'daki `-X` değerine bak; metot yanlış |

`Allow: GET` başlığının kazandırdığı şey, aramayı yarıya indirmesi: "adres mi yanlış, metot
mu yanlış" sorusunu tek satırda kapatır. Bu başlık olmadan iki hipotezi de tek tek elemen
gerekirdi.

⚠️ 404'ü "kayıt bulunamadı" ile karıştırma. Kayıt yoksa dönen 404'ü **sen** yazarsın
([4.2](4.2-exception-handling.md)); buradaki 404 eşleme sonucudur ve kodun hiç çalışmamıştır.

> 📌 **Sık yapılan hata:** 405 alınca eşleme adresini değiştirmeye başlamak. `Allow:`
> başlığı sana adresin doğru olduğunu **zaten söyledi** — orada değiştirilecek bir şey yok.

🔗 Konu: [4.1 §4 Eşleme anotasyonları](4.1-request-mapping.md)

---

### Soru 3 — `curl` ile POST atıyorsun ve 415 alıyorsun. Sunucu kodunda mı sorun var?

**Kısa cevap:** Hayır. Sunucu kodunda sorun yok; **istemcinin `Content-Type` başlığı eksik.**
Eksik olan şey `-H 'Content-Type: application/json'`.

**Ayrıntı:**

```bash
# ❌ 415 Unsupported Media Type
curl -X POST localhost:8080/siparisler -d '{"urunKodu":"ABC-1","adet":2}'

# ✅ 201 Created
curl -X POST localhost:8080/siparisler \
  -H 'Content-Type: application/json' -d '{"urunKodu":"ABC-1","adet":2}'
```

`curl`, `-H` verilmezse gövdeyi `application/x-www-form-urlencoded` olarak gönderir. Sunucu
o türü kabul etmez ve gövdeyi **hiç okumadan** reddeder.

Kanıt yanıtın kendi başlığındadır — ölçülen `415` yanıtı şunu taşıyor:

```
Accept: application/json, application/*+json
```

Yani sunucu "ben şunları kabul ediyorum" diyor. Sen o listede olmayan bir şey gönderdin.

400 ile 415'i ayıran şey **hangi aşamada durulduğudur**:

| Kod | Aşama | Anlamı |
|---|---|---|
| **415** | `Content-Type` kontrolü | Zarfı açmadım — tür desteklenmiyor |
| **400** | Gövde okundu, doğrulandı | Açtım, içi geçersiz |

> 📌 **Sık yapılan hata:** 415 alınca kontrolcüye `consumes = "application/json"` eklemeye
> çalışmak. Bu, sunucunun zaten yaptığı kontrolü tekrar yazmaktır; sorun `curl` satırındadır.

🔗 Konu: [4.1 §6 Hata 2](4.1-request-mapping.md) · [4.1 Sık karıştırılanlar](4.1-request-mapping.md)

---

### Soru 4 — Yeni sipariş oluşturan uç nokta neden `200` değil `201` dönmeli, hangi başlık eklenmeli?

**Kısa cevap:** **`201 Created`** dönmeli ve **`Location`** başlığını eklemeli. İkisini birden
`ResponseEntity.created(...)` ayarlar.

**Ayrıntı:**

```java
@PostMapping
public ResponseEntity<Siparis> olustur(@Valid @RequestBody YeniSiparis istek) {
    var s = new Siparis("S-100", istek.urunKodu(), istek.adet());
    return ResponseEntity.created(URI.create("/siparisler/" + s.no())).body(s);
}
```

Ölçülen yanıt:

```
HTTP/1.1 201
Location: /siparisler/S-100
Content-Type: application/json
```

`200` dönmek **yanlış değil, eksiktir.** Farkı istemci tarafından gör:

| | `200 OK` | `201 Created` + `Location` |
|---|---|---|
| İstemci "kaynak oluştu mu" sorusunu | Gövdeyi ayrıştırarak çıkarır | Durum kodundan okur |
| Yeni kaynağın adresini | **Tahmin eder** (`/siparisler/` + gövdedeki `no`) | Başlıktan alır |
| Adres biçimi değişirse | İstemci kırılır | İstemci etkilenmez |

Son satır asıl kazanç: adres üretme kuralı **sunucuda** kalır. `200` döndüğün gün istemci
adresi kendi kurallarıyla kurmak zorunda kalır ve o kural senin kuralınla birlikte
değişmez.

> 📌 **Sık yapılan hata:** `201`'i döndürüp `Location` yazmamak. Yarım yapılan iş burada
> hiç yapılmamışla aynı: istemci adresi yine tahmin ediyordur.

🔗 Konu: [4.1 §3 Gövde bağlama ve doğrulama](4.1-request-mapping.md) · [4.1 §5 Dönüş türü](4.1-request-mapping.md)

---

### Soru 5 — Varlık sınıfını doğrudan döndürmenin üç zararı

**Kısa cevap:** 1) Veritabanı şeman **API sözleşmesine** dönüşür. 2) Bir alan eklediğin gün
istemcilere **habersiz** API değişikliği yaparsın. 3) JPA'da **tembel yüklenen alanlar**
serileştirme sırasında beklenmedik sorgular tetikler.

**Ayrıntı:**

```java
@GetMapping("/{no}")
public SiparisVarligi getir(@PathVariable String no) { ... }   // ← veritabanı sınıfı
```

Üç zararın nasıl görüneceği:

| Zarar | Ne zaman fark edersin | Belirtisi |
|---|---|---|
| Şema sızıntısı | İç kolon adını değiştirmek istediğinde | "Bu kolonu yeniden adlandıramayız, API'de duruyor" |
| Habersiz sözleşme değişikliği | Veritabanına yeni bir alan eklediğin gün | İstemci "yanıtta bilmediğim alan var" der ya da katı ayrıştırıcısı kırılır |
| Tembel yükleme | Yük altında, üretimde | Tek bir GET isteği onlarca sorgu üretir (**201**) |

Üçünün ortak yanı: **hiçbiri hemen çıkmaz.** Kod yazıldığı gün gayet çalışır — bu yüzden
kod incelemesinde yakalanması gerekir, testte değil.

Çözüm bir DTO (Data Transfer Object) `record`'u; 4.1'deki `Siparis` ve `YeniSiparis` tam
olarak bunlar:

```java
public record Siparis(String no, String urunKodu, int adet) {}
```

Böylece dışarı ne verdiğin **açık bir liste** olur ve veritabanı sınıfını değiştirmek
API'yi değiştirmez.

> 📌 **Sık yapılan hata:** "Alanlar zaten aynı, DTO gereksiz kopya" demek. DTO'nun işi farklı
> olmak değil, **ayrı olmak**: iki tarafın birbirinden bağımsız değişebilmesi.

🔗 Konu: [4.1 §5 Dönüş türü ne olmalı](4.1-request-mapping.md) · [4.1 §6 Hata 4](4.1-request-mapping.md)

---

## 4.2 Exception handling ve ProblemDetail

📄 Sorular: [`4.2-exception-handling.md`](4.2-exception-handling.md)

### Soru 1 — `type` ile `detail` farkı; istemci hangisine göre kod yazmalı?

**Kısa cevap:** **`type` sabittir ve makine okur; `detail` her olayda değişir ve insan okur.**
İstemci kodunu **`type`**'a göre yazmalı.

**Ayrıntı:**

Ölçülen gövde:

```json
{"detail":"Sipariş bulunamadı: yok",
 "instance":"/siparisler/yok",
 "status":404,
 "title":"Sipariş bulunamadı",
 "type":"https://loncademy.tr/hatalar/siparis-bulunamadi"}
```

Aynı hata farklı bir siparişte olduğunda hangi alan değişir:

| Alan | `S-100` için | `yok` için | Değişti mi |
|---|---|---|---|
| `type` | `.../siparis-bulunamadi` | `.../siparis-bulunamadi` | **Hayır** |
| `title` | `Sipariş bulunamadı` | `Sipariş bulunamadı` | Hayır |
| `detail` | `Sipariş bulunamadı: S-100` | `Sipariş bulunamadı: yok` | **Evet** |
| `instance` | `/siparisler/S-100` | `/siparisler/yok` | **Evet** |

Değişmeyen alana kod yazılır. İstemci `detail`'e göre dallanırsa:

```javascript
if (hata.detail.startsWith("Sipariş bulunamadı"))   // ← sen metni düzelttiğin gün kırılır
```

Sen bir gün "Sipariş bulunamadı" yerine "Böyle bir sipariş yok" yazarsın — bu bir **metin
düzeltmesidir**, sözleşme değişikliği değil. Ama istemci kırılır. Doğrusu:

```javascript
if (hata.type === "https://loncademy.tr/hatalar/siparis-bulunamadi")
```

> 📌 **Sık yapılan hata:** `type` alanını doldurmayı atlamak ("nasılsa `status` var"). O
> zaman istemcinin elinde yalnızca `404` kalır — ve `404`, "sipariş yok" ile "müşteri yok"u
> ayırmaz. `type` alanını yazmadıysan istemciyi `detail` metnine mahkûm etmişsindir.

🔗 Konu: [4.2 §1 RFC 9457 ve ProblemDetail](4.2-exception-handling.md) · [4.2 §7 Hata 3](4.2-exception-handling.md)

---

### Soru 2 — `spring.mvc.problemdetails.enabled=true` neyi çözer, neyi çözmez?

**Kısa cevap:** **Biçimi çözer, içeriği çözmez.** Yerleşik hataların gövdesini RFC 9457'ye ve
`Content-Type`'ı `application/problem+json`'a çevirir; **alan bazlı ayrıntı eklemez.**

**Ayrıntı:**

Aynı `DELETE` isteği, ayar kapalı ve açık — gerçek çıktılar:

```
# Varsayılan (false)
HTTP/1.1 405
Allow: GET

# spring.mvc.problemdetails.enabled=true
{"detail":"Method 'DELETE' is not supported.","instance":"/siparisler/1",
 "status":405,"title":"Method Not Allowed"}
```

| | Ayar kapalı (**varsayılan**) | Ayar açık |
|---|---|---|
| Yerleşik hata gövdesi | `{"timestamp","status","error","path"}` | RFC 9457 `problem+json` |
| `Content-Type` | `application/json` | `application/problem+json` |
| **Alan bazlı ayrıntı** | **Yok** | **Yok** |

Son satır sorunun cevabı. Doğrulama hatasında hangi alanın neden geçersiz olduğunu bu ayar
**hiçbir koşulda** yazmaz; onu kendi işleyicinle eklersin:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ProblemDetail gecersiz(MethodArgumentNotValidException ex) { ... p.setProperty("alanlar", alanlar); ... }
```

ve sonuç şu olur:

```json
{"detail":"İstek gövdesi geçersiz.","instance":"/siparisler","status":400,
 "title":"Doğrulama hatası",
 "alanlar":{"urunKodu":"must not be blank","adet":"must be greater than or equal to 1"}}
```

**İkisini birlikte kullan:** ayar senin yazmadığın hataları (405, 415, bozuk JSON)
standarda taşır; işleyicin de asıl bilgiyi ekler.

> 📌 **Sık yapılan hata:** Ayarı açıp "exception handling tamam" demek. Ayarı açtıktan sonra
> istemci hâlâ hangi alanın hatalı olduğunu bilmiyor — asıl şikâyet oydu.

🔗 Konu: [4.2 §4 Yerleşik hataları çevirmek](4.2-exception-handling.md) · [4.2 §3 Doğrulama hataları](4.2-exception-handling.md)

---

### Soru 3 — Kullanıcı "500 aldım" diyor. Ne kadar bilgi göstermelisin, sorunu nasıl bulursun?

**Kısa cevap:** Kullanıcıya **genel bir mesaj + izleme numarası**; ayrıntı **günlüğe**. Sorunu
kullanıcının verdiği izleme numarasını günlükte arayarak bulursun.

**Ayrıntı:**

```java
@ExceptionHandler(Exception.class)
public ProblemDetail beklenmeyen(Exception ex) {
    log.error("Beklenmeyen hata", ex);                    // ← ayrıntı GÜNLÜĞE
    ProblemDetail p = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Beklenmeyen bir hata oluştu.");              // ← istemciye GENEL mesaj
    p.setProperty("izlemeNo", MDC.get("traceId"));        // ← ikisini bağlayan numara
    return p;
}
```

Üç parçalı kural ve her parçanın sebebi:

| Nereye | Ne | Neden |
|---|---|---|
| Günlüğe | Tam stack trace, istek bağlamı | Sorunu **sen** çözeceksin, bilgi sende olmalı |
| İstemciye | Genel mesaj | Sınıf adları, kütüphane sürümleri ve dosya yolları saldırgana harita çizer |
| Yanıta | `izlemeNo` | İkisini bağlar — kullanıcının elindeki tek somut ipucu |

Destek akışı: kullanıcı "izleme numaram `abc-123`" der, sen günlükte `abc-123` ararsın, tam
stack trace'i bulursun. Yarım saatlik günlük taraması tek `grep`'e iner.

⚠️ **Stack trace'i asla döndürme.** `server.error.include-stacktrace` varsayılanı `never`;
**değiştirme.**

> 📌 **Sık yapılan hata:** Yardımcı olmak isteyip exception mesajını `detail` alanına koymak.
> `NullPointerException: Cannot invoke "SiparisVarligi.getMusteri()" because ...` cümlesi
> kullanıcıya hiçbir şey söylemez, saldırgana çok şey söyler.

🔗 Konu: [4.2 §6 Ne kadarını söylemeli](4.2-exception-handling.md)

---

### Soru 4 — `@ControllerAdvice` ile `@RestControllerAdvice` farkı; yanlışını yazarsan ne olur?

**Kısa cevap:** `@RestControllerAdvice` = `@ControllerAdvice` + `@ResponseBody`. Yanlışını
yazarsan dönen `ProblemDetail` nesnesi **view name sanılır** (gösterilecek sayfanın adı) ve
hata yanıtı yerine bir view çözme hatası alırsın.

**Ayrıntı:**

```java
@ControllerAdvice                      // ← @ResponseBody yok
public class HataIsleyici {
    @ExceptionHandler(SiparisBulunamadi.class)
    public ProblemDetail bulunamadi(SiparisBulunamadi ex) { ... }
}
```

| | `@ControllerAdvice` | `@RestControllerAdvice` |
|---|---|---|
| İşleyici bulunur mu | **Evet** | Evet |
| Dönen nesne nasıl yorumlanır | **View name** | Gövdeye yazılır (JSON) |
| İstemcinin gördüğü | View çözümleme hatası | `application/problem+json` |

Teşhisi zorlaştıran şey: işleyici **çalışıyordur**. Kesme noktası koyarsan metoda girildiğini
görürsün. Sorun dönüşün nasıl yorumlandığındadır — bu yüzden "işleyici çalışmıyor" diye
aramaya başlarsan yanlış yerdesin.

Bu, [2.1](../02-anotasyon-haritasi/2.1-stereotype.md)'deki `@Controller` / `@RestController`
tuzağının aynısıdır; aynı hata iki farklı yerde karşına çıkıyor.

> 📌 **Sık yapılan hata:** Belirtiye bakıp bir view resolver eklemeye çalışmak. Uygulaman
> view döndürmüyor; eksik olan `Rest` önekidir.

🔗 Konu: [4.2 §7 Hata 2](4.2-exception-handling.md) · [2.1 Stereotype'lar](../02-anotasyon-haritasi/2.1-stereotype.md)

---

### Soru 5 — Neden exception'lar `RuntimeException`'dan türemeli? İki sebep.

**Kısa cevap:** 1) Checked exception **her ara katmana `throws` yazdırır**. 2) `@Transactional`
checked exception'larda **geri alma (rollback) yapmaz** — sessiz bir veri sorunu.

**Ayrıntı:**

```java
public class SiparisBulunamadi extends RuntimeException {
    public SiparisBulunamadi(String no) { super("Sipariş bulunamadı: " + no); }
}
```

**Sebep 1 — bulaşıcı imza.** Exception deponun içinde atılır ama kontrolcüye kadar her metot
imzasına yazılır:

```java
// Checked olsaydı:
Siparis bul(String no) throws SiparisBulunamadi;          // depo
Siparis getir(String no) throws SiparisBulunamadi;        // servis
Siparis getir(String no) throws SiparisBulunamadi;        // kontrolcü
```

Oysa bu exception'ı **hiçbiri ele almıyor**; hepsi yalnızca taşıyor. Ele alan tek yer
`@RestControllerAdvice`.

**Sebep 2 — sessiz geri alma kaybı.** `@Transactional` varsayılan olarak yalnızca
unchecked exception'larda geri alır:

| Exception türü | `@Transactional` davranışı |
|---|---|
| `RuntimeException` ailesi (unchecked) | İşlem **geri alınır** |
| Checked `Exception` | İşlem **işlenir (commit)** — yarım veri kalır |

İkincisi ne hata verir ne uyarı; veritabanında yarım bir sipariş bulursun. Ayrıntısı
**201**'in konusu, ama tercihin sebebi burada.

Bunlara ek olarak exception'ın **alan adı taşıması** gerekir: `SiparisBulunamadi` iyi,
`NotFoundException` kötü — ikincisi alan sınıfına HTTP bilgisi sızdırır. HTTP eşlemesi tek
yerde, `@RestControllerAdvice`'ta durur.

> 📌 **Sık yapılan hata:** "Checked exception çağıranı ele almaya zorlar, daha güvenli"
> demek. Pratikte zorladığı şey ele almak değil, `throws` eklemek ya da boş `catch` yazmak.

🔗 Konu: [4.2 §5 Exception tasarımı](4.2-exception-handling.md)

---

## 4.3 RestClient ile dışarıya çağrı

📄 Sorular: [`4.3-restclient.md`](4.3-restclient.md)

### Soru 1 — Timeout koymadığın bir çağrı, karşı taraf donduğunda servisini nasıl çökertir?

**Kısa cevap:** İstek **sonsuza kadar bekler**, bekleyen her istek bir thread tutar;
havuz dolunca yeni istekler işlenemez ve **senin** servisin de cevap veremez hâle gelir —
kendi kodunda hiçbir hata olmadan.

**Ayrıntı:**

Çöküş zinciri:

| Adım | Ne olur | Senin günlüğünde |
|---|---|---|
| 1 | Karşı taraf yanıt vermiyor | Hiçbir şey |
| 2 | İstek bloklanıyor, thread tutuluyor | Hiçbir şey |
| 3 | Yeni istekler geliyor, her biri bir thread daha tutuyor | Hiçbir şey |
| 4 | Havuz tükeniyor | İstekler kuyrukta bekliyor |
| 5 | Senin servisin de yanıt veremiyor | "Yavaşlık" şikâyeti |

En tehlikeli sütun sonuncusu: **hiçbir adımda hata günlüğü yok.** Bu yüzden olay "servis
çöktü" değil "her şey yavaşladı" olarak raporlanır ve suçlu ilk sırada senin servisin
sanılır — oysa senin kodunda tek bir hata yok, yalnızca bir sınır eksik.

Ağın üç davranışı vardır: doğru cevap verir, hata verir, ya da **hiç cevap vermez**.
İlk ikisini `retrieve()` exception'a çevirir; üçüncüsüne karşı tek korumam timeout'tur.

```properties
spring.http.clients.connect-timeout=2s
spring.http.clients.read-timeout=5s
```

> 📌 **Sık yapılan hata:** "Yerelde ve testte kusursuz çalışıyor" diye timeout'u ertelemek.
> Yerelde karşı taraf hiç donmaz; bu ayarın sınandığı tek yer üretimdir.

🔗 Konu: [4.3 §4 Timeout](4.3-restclient.md) · [4.3 §6 Hata 1](4.3-restclient.md)

---

### Soru 2 — `connect-timeout` ile `read-timeout` neyi sınırlar? Hangisi küçük olmalı, neden?

**Kısa cevap:** `connect-timeout` **bağlantı kurulana** kadarki süreyi, `read-timeout` **ilk
yanıt baytı gelene** kadarki süreyi sınırlar. **`connect-timeout` daha küçük olmalı** —
bağlantı kurulamıyorsa karşı taraf ayakta değildir, beklemenin bilgi değeri yoktur.

**Ayrıntı:**

| Ayar | Neyi sınırlar | Ne öğrenirsin | Makul başlangıç |
|---|---|---|---|
| `connect-timeout` | TCP bağlantısının kurulması | Karşı taraf **erişilebilir mi** | 1–3 sn |
| `read-timeout` | İlk yanıt baytının gelmesi | Karşı taraf **ne kadar hızlı** | 3–10 sn |

Neden asimetrik: bağlantı kurulumu karşı tarafın **iş yapmasını** içermez, yalnızca ağ
erişilebilirliğini ölçer. 5 saniyede kurulamayan bir bağlantı 20 saniyede de kurulmaz —
orada beklemek boş beklemedir. Ama bağlantı kurulduysa karşı taraf ayaktadır ve gerçekten
bir iş yapıyor olabilir; ona biraz daha süre tanımak makuldür.

Ölçüm — konu dosyasındaki lab:

```properties
spring.http.clients.connect-timeout=2s
spring.http.clients.read-timeout=1s
```

3 saniye uyuyan bir uç noktaya çağrı ~1 saniyede timeout exception'ıyla
(`ResourceAccessException` ailesi) bitiyor. `read-timeout` satırını silince aynı çağrı 3
saniye bekleyip **başarıyla** dönüyor — varsayılan timeout'un olmadığının kanıtı bu.

⚠️ Timeout bir **sınırdır, çözüm değil.** "5 saniyede cevap gelmezse ne yapacağım?"
sorusunun cevabı hâlâ sende: yeniden dene, önbellekten dön ya da hata ver (**301**).

> 📌 **Sık yapılan hata:** İki değeri de yüksek tutup "güvenli tarafta kalmak". Yüksek bir
> timeout, timeout olmamasına yaklaşır; koruduğu tek şey senin kararsızlığındır.

🔗 Konu: [4.3 §4 Timeout](4.3-restclient.md)

---

### Soru 3 — `RestClient.builder()` ile inject edilen `RestClient.Builder` arasındaki fark

**Kısa cevap:** Inject edilen builder **Spring Boot'un auto-configuration ile kurduğu**
builder'dır; timeout ayarlarını ve observability eklerini **zaten taşır**. `RestClient.builder()`
sıfırdan kurar ve bunların hepsini kaybedersin — **hiçbir uyarı almadan.**

**Ayrıntı:**

```java
// ❌ Boot'un ayarlarını taşımaz
@Bean
public RestClient stokIstemcisi() {
    return RestClient.builder().baseUrl("https://stok.sirket.com").build();
}

// ✅ Builder'ı inject et
@Bean
public RestClient stokIstemcisi(RestClient.Builder kurucu) {
    return kurucu.baseUrl("https://stok.sirket.com").build();
}
```

| | `RestClient.builder()` | Inject edilen `RestClient.Builder` |
|---|---|---|
| `spring.http.clients.*` timeout'ları | **Yok** | Var |
| Observability (metrik/iz) eklentileri | **Yok** | Var |
| Derleme / açılış uyarısı | **Yok** | — |

Üçüncü satır hatayı sinsi yapan şey. Belirtisi şu cümledir: **"Timeout ayarladık ama
çalışmıyor."** Ayar dosyasında satır duruyor, istemci onu hiç görmüyor.

Teşhis yolu: ayarı bilerek 1 saniyeye indir ve 3 saniye süren bir çağrı yap. İstek 3 saniye
sonra **başarıyla** dönüyorsa ayar o istemciye ulaşmıyor demektir; builder'ın nereden
geldiğine bak.

> 📌 **Sık yapılan hata:** Ayar çalışmayınca değeri değiştirip denemeye devam etmek. Sorun
> değerde değil, ayarın o nesneye hiç ulaşmamasında.

🔗 Konu: [4.3 §5 Bean tasarımı](4.3-restclient.md) · [4.3 Kalıp 4](4.3-restclient.md)

---

### Soru 4 — Karşı taraftan 404 geldi. `retrieve().body()` ne yapar, gövdeye nasıl ulaşırsın?

**Kısa cevap:** `retrieve()` başarısız durumu **exception'a çevirir** —
`HttpClientErrorException$NotFound` atılır, `body()` hiç dönmez. Gövdeye
**`e.getResponseBodyAsString()`** ile ulaşırsın.

**Ayrıntı:**

Ölçülen çıktı:

```
### ISTISNA SINIFI = org.springframework.web.client.HttpClientErrorException$NotFound
### DURUM = 404 NOT_FOUND
### GOVDE = {"detail":"Sipariş bulunamadı: yok","instance":"/siparisler/yok",
             "status":404,"title":"Sipariş bulunamadı",
             "type":"https://loncademy.tr/hatalar/siparis-bulunamadi"}
```

Üçüncü satır bu kursun en güzel kapanışı: gelen gövde,
[4.2](4.2-exception-handling.md)'de **kendi yazdığın** `ProblemDetail`. Hata sözleşmesi yazmanın
karşılığı tam olarak bu — çağıran taraf `type` alanına bakıp dallanabiliyor.

Exception hiyerarşisi ve yakalama:

| Durum | Exception | Üst sınıf |
|---|---|---|
| 4xx | `HttpClientErrorException` (`$NotFound` gibi alt sınıflar) | `RestClientResponseException` |
| 5xx | `HttpServerErrorException` | `RestClientResponseException` |

Üç seçeneğin var:

```java
// 1) Yakala ve gövdeyi oku
catch (RestClientResponseException e) { e.getResponseBodyAsString(); }

// 2) Kendi exception'ına çevir
.onStatus(HttpStatusCode::is4xxClientError, (istek, yanit) -> { throw new StokBulunamadi(kod); })

// 3) Durumu kendin oku — exception hiç atılmasın istiyorsan
ResponseEntity<StokBilgisi> yanit = ... .retrieve().toEntity(StokBilgisi.class);
```

> 📌 **Sık yapılan hata:** `catch (HttpServerErrorException e)` yazıp 404'ün neden
> yakalanmadığını anlamamak. 404 bir **istemci** hatasıdır; sınıf ailesini karıştırırsan
> exception hiç yakalanmaz.

🔗 Konu: [4.3 §3 Hata gelince ne oluyor](4.3-restclient.md)

---

### Soru 5 — 4xx ile 5xx'e neden farklı tepki vermelisin? Birer örnek.

**Kısa cevap:** **4xx senin isteğin hatalı, 5xx karşı taraf bozuk** — biri düzeltilebilir bir
durumdur, diğeri geçici bir arıza. Örnek: stok servisinden **404** → "bu ürün yok", akışa
devam edebilirsin. Ödeme servisinden **500** → "bilmiyorum", siparişi **almamalısın**.

**Ayrıntı:**

| | 4xx | 5xx |
|---|---|---|
| Ne söyler | İsteğin hatalıydı ya da kaynak yok | Karşı taraf isteği işleyemedi |
| Tekrar denemek | Anlamsız — aynı istek aynı sonucu verir | Anlamlı olabilir (geçici arıza) |
| İş kararı | Bilgi olarak kullanılabilir ("ürün yok") | Bilgi **değildir** ("bilmiyorum") |
| Örnek | Stok `404` → ürün katalogda yok | Ödeme `500` → ödeme alındı mı belli değil |

Ayrımı yok eden kod:

```java
try {
    return istemci.get().uri("/stoklar/{kod}", kod).retrieve().body(StokBilgisi.class);
} catch (Exception e) {
    return null;                     // ← "stok yok" ile "servis çökmüş" aynı şey oldu
}
```

Çağıran taraf `null` görür ve "stok yok" der. Oysa belki stok servisi çökmüştür ve stok
gayet vardır — ya da tersi, ödeme servisi çökmüşken sipariş almaya devam edersin. **En
pahalı hatalar, bilgi eksikliğini bilgi sanmaktan doğar.**

Doğrusu ayrımı korumak:

```java
.onStatus(HttpStatusCode::is4xxClientError, (istek, yanit) -> { throw new StokBulunamadi(kod); })
.onStatus(HttpStatusCode::is5xxServerError, (istek, yanit) -> { throw new StokServisiErisilemez(kod); })
```

İki farklı exception, iki farklı iş kararı.

> 📌 **Sık yapılan hata:** `catch (Exception e)` ile hepsini tek sepete koymak. Timeout
> (`ResourceAccessException`) da o sepete düşer — ve o, 5xx'ten bile daha belirsizdir:
> istek karşı tarafa ulaştı mı, işlendi mi, bilmiyorsun.

🔗 Konu: [4.3 §3 Hata gelince ne oluyor](4.3-restclient.md) · [4.3 §6 Hata 3](4.3-restclient.md)

---

⬅️ [Bölüme dön](4.1-request-mapping.md) · 📄 [4.2](4.2-exception-handling.md) · 📄 [4.3](4.3-restclient.md) · ➡️ [5. bölüm](../05-test-temelleri/5.1-slice-test.md)
