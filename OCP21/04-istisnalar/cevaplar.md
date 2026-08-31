# 04 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 4.1 Exception temelleri: try/catch/finally ve multi-catch

### Soru 1 — Checked ve unchecked ayrımını hangi sınıf belirler? `NumberFormatException` hangisi?

**Kısa cevap:** **Ayrımı `RuntimeException` belirler.** `Exception` ve altları checked'tir, **`RuntimeException` (ve `Error`) hariç**; `NumberFormatException` **unchecked**'tir.

**Ayrıntı:** `Throwable` altında `Exception` **checked**'tir; ancak `RuntimeException` ve altındaki her şey ile `Error` ve altları **unchecked**'tir. Checked'i derleyici zorlar (**yakala ya da bildir**), unchecked'i takip etmez. `NumberFormatException`, `IllegalArgumentException`'ın alt sınıfıdır, o da `RuntimeException` altındadır; dolayısıyla **unchecked**'tir ve `Integer.parseInt("abc")` için `try/catch` **zorunlu değildir**.

📌 **Sık yapılan hata:** `NumberFormatException`'ı checked sanıp `Integer.parseInt`'i mecburen yakalamak. Adı "Exception" ile bitse de `RuntimeException` altındadır, unchecked'tir.

🔗 [4.1 §1 Hiyerarşi (checked vs unchecked)](4.1-exception-temelleri.md)

### Soru 2 — `finally` hangi tek durumda çalışmaz?

**Kısa cevap:** **Yalnızca `System.exit()` çağrıldığında `finally` çalışmaz.**

**Ayrıntı:** `finally` **her zaman** çalışır — `return`, `break`, `continue` ya da exception fark etmez; `catch` bloğu exception atsa bile `finally` yine çalışır, sonra exception yayılır. Tek istisna, blok içinde `System.exit()` çağrılmasıdır; o zaman JVM sonlanır ve `finally` çalışmaz.

📌 **Sık yapılan hata:** Exception yakalanmadığında `finally`'nin atlanacağını sanmak. Yakalansa da yakalanmasa da çalışır; onu yalnızca `System.exit()` atlatır.

🔗 [4.1 §3 try / catch / finally akışı](4.1-exception-temelleri.md)

### Soru 3 — `try { return 1; } finally { return 2; }` ne döner? Neden?

**Kısa cevap:** **`2` döner.** `finally` içindeki `return` önceki `return`'ü ezer.

**Ayrıntı:** `try` bloğu `return 1` ile çıkmaya hazırlanır, ama `finally` **her zaman** çalışır ve içindeki `return 2` önceki dönüş değerini (ve varsa exception'ı) **ezer**. Bu yüzden metot `2` döndürür. Not: `try`'daki dönüş değeri `return`'de hesaplanır, ama `finally`'de yeni bir `return` varsa o galip gelir.

📌 **Sık yapılan hata:** İlk `return 1`'i galip sanmak. `finally`'deki `return` her şeyin önüne geçer.

🔗 [4.1 §3 finally içinde return](4.1-exception-temelleri.md)

### Soru 4 — `catch (Exception e)` bloğunu `catch (IOException e)`'den önce yazarsan ne olur?

**Kısa cevap:** **Derlenmez** (compilation fails): "exception has already been caught".

**Ayrıntı:** `catch` blokları **alt sınıftan üst sınıfa** doğru sıralanmalıdır. `Exception`, `IOException`'ın üst sınıfı olduğu için önce yazılırsa `IOException` catch'i **ulaşılamaz kod** olur; derleyici hata verir. Doğru sıra: önce `catch (IOException e)`, sonra `catch (Exception e)`.

📌 **Sık yapılan hata:** Geniş `catch`'i başa koymak. Üst sınıf önce gelirse alt sınıf catch'ine hiç ulaşılamaz ve derleme başarısız olur.

🔗 [4.1 §3 Yakalama sırası](4.1-exception-temelleri.md)

### Soru 5 — Multi-catch'te `IOException | FileNotFoundException` neden geçersiz?

**Kısa cevap:** **Tipler akraba (subclass ilişkili) olduğu için derlenmez.**

**Ayrıntı:** Multi-catch'te alternatif tipler **birbirinin alt/üst sınıfı olamaz**. `FileNotFoundException`, `IOException`'ın alt sınıfıdır; zaten `IOException` yakalayınca alt sınıfı da yakalanır, dolayısıyla ikisini `|` ile listelemek gereksiz ve yasaktır — "alternatives in a multi-catch statement cannot be related by subclassing". İlgisiz tipler (örn. `IOException | SQLException`) ise geçerlidir.

📌 **Sık yapılan hata:** Alt ve üst sınıfı `|` ile birlikte yazmak. Multi-catch yalnızca **akraba olmayan** tipleri kabul eder.

🔗 [4.1 §4 Multi-catch](4.1-exception-temelleri.md)

### Soru 6 — `try { System.out.println("x"); } catch (IOException e) {}` neden derlenmez?

**Kısa cevap:** **Gövde `IOException` atmadığı için derlenmez:** "exception is never thrown in body".

**Ayrıntı:** Bir metodun gövdesinde **atılması mümkün olmayan** bir checked exception'ı yakalamak derleme hatasıdır. `System.out.println` hiçbir checked exception atmaz, dolayısıyla `IOException`'ı yakalayan `catch` erişilemezdir. Tek istisna: `Exception` veya `RuntimeException` yakalamak **her zaman geçerlidir** (bunlar atılamayacak checked kuralına takılmaz).

📌 **Sık yapılan hata:** Her checked tipi güvenle yakalayabileceğini sanmak. Gövde o checked'i atmıyorsa yalnızca `Exception`/`RuntimeException` yazılabilir; belirli bir checked tip derlenmez.

🔗 [4.1 §2 Handle-or-declare kuralı](4.1-exception-temelleri.md)

## 4.2 try-with-resources ve özel exception'lar

### Soru 1 — İki kaynak açtın; hangi sırayla kapanırlar ve `catch`'e göre ne zaman?

**Kısa cevap:** **Ters sırayla** (son açılan önce) ve **`catch`'ten önce** (aynı zamanda `finally`'den de önce) kapanırlar.

**Ayrıntı:** Kaynaklar bildirim sırasına göre açılır, `try` gövdesi çalışır, sonra kaynaklar **ters sırayla** kapatılır; bu kapatma `catch` ve `finally` bloklarından **önce** gerçekleşir. Örneğin `try (var a = ...; var b = ...)` için çıktı sırası: gövde, `kapandi B`, `kapandi A`, sonra `catch` (varsa), en sonda `finally`.

📌 **Sık yapılan hata:** Kaynakların bildirim sırasında ya da `finally`'den sonra kapandığını sanmak. Kapanış terstir ve `catch`/`finally`'nin önündedir.

🔗 [4.2 §2 Çalışma sırası](4.2-try-with-resources.md)

### Soru 2 — `catch` bloğunda kaynak değişkenini kullanabilir misin? Neden?

**Kısa cevap:** **Hayır — derlenmez.** Kaynak değişkeni `catch`'te **kapsam dışıdır** (ve zaten kapatılmıştır).

**Ayrıntı:** try-with-resources'ta bildirilen kaynak değişkeni yalnızca `try` gövdesinin kapsamındadır. `catch` veya `finally` bloğuna gelindiğinde değişken hem **kapsam dışıdır** hem de **çoktan kapatılmıştır**, bu yüzden orada kullanmak derleme hatasıdır ("cannot find symbol").

📌 **Sık yapılan hata:** `catch (Exception e) { System.out.println(r); }` yazmak. `r` kaynağı orada görünmez; kod derlenmez.

🔗 [4.2 §2 Kaynak kapsamı](4.2-try-with-resources.md)

### Soru 3 — Gövde ve `close()` aynı anda exception atarsa hangisi dışarı çıkar, diğerine nasıl ulaşırsın?

**Kısa cevap:** **Gövdenin exception'ı yayılır;** `close()`'unki **bastırılır** ve `getSuppressed()` ile ulaşılır.

**Ayrıntı:** Hem gövde hem `close()` exception atarsa, dışarı **gövdeninki** çıkar; `close()`'un attığı exception ona **suppressed** olarak eklenir. Yakalanan exception'ın `getSuppressed()` metodu bir `Throwable[]` döndürür ve bastırılanları kapanış sırasında verir (hiç yoksa **boş dizi** döner, null değil). Klasik `finally` ile kapatsaydın gövdenin exception'ı **tamamen kaybolurdu** — try-with-resources'ın var oluş sebebi budur.

📌 **Sık yapılan hata:** `close()`'un hatasının gövdedekini ezdiğini sanmak. Tersine gövdeninki kazanır; `close()`'unki bastırılır ve `getSuppressed()` ile okunur.

🔗 [4.2 §3 Bastırılmış (suppressed) exception'lar](4.2-try-with-resources.md)

### Soru 4 — `AutoCloseable` ile `Closeable` arasındaki fark ne?

**Kısa cevap:** **`Closeable`, `AutoCloseable`'ın alt arayüzüdür;** `close()` imzaları farklıdır: `AutoCloseable.close()` `throws Exception`, `Closeable.close()` daha dar `throws IOException`.

**Ayrıntı:** try-with-resources herhangi bir **`AutoCloseable`** ile çalışır. `Closeable` onun bir alt arayüzüdür ve `close()` metodu daha **dar** bir exception (`IOException`) bildirir; `AutoCloseable.close()` ise en genel `Exception`'ı bildirir. Yani her `Closeable` bir `AutoCloseable`'dır ama tersi değildir.

📌 **Sık yapılan hata:** İkisini eşdeğer sanmak ya da yalnızca `Closeable`'ın try-with-resources'ta kullanılabildiğini düşünmek. Kaynak olmak için `AutoCloseable` yeterlidir; `Closeable` yalnızca daha dar `close()` imzalı özel bir hâlidir.

🔗 [4.2 §1 Sözdizimi ve AutoCloseable](4.2-try-with-resources.md)

### Soru 5 — Java 9'da try-with-resources'a ne eklendi, koşulu ne?

**Kısa cevap:** **Zaten var olan bir değişkeni doğrudan `try (r)` biçiminde kullanma;** koşul, değişkenin **effectively final** (veya `final`) olmasıdır.

**Ayrıntı:** Java 9 öncesi kaynak, `try` parantezi içinde **bildirilmek** zorundaydı. Java 9+ ile önceden tanımlanmış bir değişken doğrudan `try (r) { ... }` biçiminde yazılabilir; ancak bu değişken **effectively final** olmalıdır — yani ilk atamadan sonra değeri değiştirilmemelidir. Kaynak değişkenleri try-with-resources içinde her hâlükârda örtük `final` gibi davranır.

📌 **Sık yapılan hata:** Herhangi bir değişkeni `try (r)` içine koyabileceğini sanmak. Değişken effectively final değilse (sonradan yeniden atanmışsa) derlenmez.

🔗 [4.2 §1 Java 9+ effectively final kaynak](4.2-try-with-resources.md)

### Soru 6 — Özel exception'da `super(mesaj)` yazmazsan ne olur?

**Kısa cevap:** **`getMessage()` `null` döner.**

**Ayrıntı:** Özel bir exception sınıfının constructor'ında `super(mesaj)` çağrılmazsa mesaj üst sınıfa hiç iletilmez ve `getMessage()` **`null`** döndürür. Sınav bunu genelde "ne yazdırır?" diye sorar; cevap `null`'dur. Mesajı taşımak için constructor'da en az `super(String)` çağrılmalı; exception zinciri için `super(String, Throwable)` kullanılır.

📌 **Sık yapılan hata:** Constructor'a `String` parametresi almanın mesajı otomatik ayarladığını sanmak. Parametreyi açıkça `super(mesaj)` ile iletmezsen `getMessage()` `null` kalır.

🔗 [4.2 §4 Özel exception yazma](4.2-try-with-resources.md)
