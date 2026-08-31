# Cheatsheet — OCP 21 (1Z0-830)

> Tüm konuların **tek satırlık** özeti. Sınavdan önceki hafta günde bir kez baştan sona oku.
> Bir satır sana yabancı geliyorsa **o konuya geri dön**.

---

## En çok kaybettiren 15 refleks

| # | Refleks |
|---|---|
| 1 | Şıklarda `Compilation fails` varsa **önce derlemeyi** kontrol et. |
| 2 | Immutable tipte (`String`, `java.time`, wrapper) dönüş atanmadıysa **o satır etkisizdir**. |
| 3 | `Integer` cache **−128…127**; `==` orada `true`, dışında `false`. Bir taraf primitifse **unboxing**. |
| 4 | Compound assignment (`+=`, `*=`) **gizli cast** yapar → neredeyse her zaman derlenir. |
| 5 | `switch` ifadesi **exhaustive** olmalı; blok gövdede **`yield`** şart. |
| 6 | Pattern `switch`'te `case null` yoksa null → **NPE**. |
| 7 | **Metotlar nesneye, alanlar ve statikler referansa** bağlıdır. |
| 8 | `finally`'deki `return` her şeyi **ezer** ve exception'ı **yutar**. |
| 9 | Stream **terminal işlem olmadan çalışmaz**, **iki kez kullanılamaz**. |
| 10 | Boş stream'de **`allMatch` `true`**, `anyMatch` `false`. |
| 11 | `List<Integer>.remove(1)` **indeksi** siler. |
| 12 | `Files.lines`/`list`/`walk` **kapatılmalı**; `Path` diske **dokunmaz**. |
| 13 | Deserialization **constructor çalıştırmaz**; `transient` **sıfırlanır**. |
| 14 | Virtual thread'ler **her zaman daemon**, **havuzlanmaz**, `synchronized`'da **pinlenir**. |
| 15 | `orElse` argümanını **her zaman hesaplar**; `orElseGet` tembeldir. |

---

## 1 — Temel tipler, metin, tarih

| Konu | Kural |
|---|---|
| Sabit ifade | `final` + sığıyorsa `byte/short/char`'a cast'siz; **`long → int` için yok** |
| Sayısal terfi | `byte/short/char` ikili işlemde **`int`** olur |
| `char` aritmetiği | `'a' + 1` = `98` |
| Bölme | `5/0` → exception · `5.0/0` → `Infinity` · `0.0/0` → `NaN` |
| `NaN` | Kendine bile eşit **değil** |
| `Math.round(-2.5)` | **`-2`** (yukarı yuvarlar) |
| `String` havuzu | Literal ve derleme zamanı sabitleri paylaşılır; `new String` **paylaşmaz** |
| `substring(b,e)` | `b` dahil, `e` hariç; `b == length()` → `""`; fazlası exception |
| `strip` vs `trim` | `strip` Unicode boşluk temizler |
| `StringBuilder.equals` | **Override edilmemiş** (referans) |
| `sb.delete(1,99)` | Exception **atmaz**, sona kadar siler |
| Text block | Açılıştan sonra **satır sonu şart**; kapanış ayrı satırdaysa sonda `\n` **var** |
| `java.time` | Constructor **yok**, hepsi **immutable**, ay **1 tabanlı** |
| `plusMonths` | Taşan gün **ayın son gününe çekilir**, geri gelmez |
| `Period` vs `Duration` | Takvim vs kesin süre; yanlış eşleşme **runtime** `UnsupportedTemporalTypeException` |
| `Period.ofYears(1).ofMonths(2)` | **`P2M`** — static fabrikalar ezer |
| Biçim | `MM`=ay, `mm`=dakika, `HH`=0-23, `hh`=1-12, `dd`=ayın günü |

---

## 2 — Akış kontrolü

| Konu | Kural |
|---|---|
| `switch` selector | `byte/short/char/int` + wrapper, `String`, `enum`, (21) pattern. **`long`, `float`, `double`, `boolean` yasak** |
| `case` etiketi | **Derleme zamanı sabiti** olmalı |
| Deyim vs ifade | Deyim **düşer**; ifade **düşmez**, **exhaustive**, `yield` ister |
| Sözdizimi karıştırma | Aynı `switch`'te `:` ve `->` → **derleme hatası** |
| Dominance | Genel desen özeli **gölgeleyemez**; guard'lı desen **önce** |
| `instanceof` pattern | `&&` çalışır, `\|\|` **çalışmaz** (flow scoping) |
| Nitelikli enum etiketi | `case Renk.MAVI` → **Java 21'de geçerli** |
| Ulaşılamaz kod | `while(false)` **derlenmez**, `if(false)` **derlenir** |
| `for (...);` | Gövde **boş** |
| for-each değişkeni | **Kopya**; koleksiyon değişirse **`ConcurrentModificationException`** |
| `continue` | `for`'da `update` çalışır; `while`'da artırmayı atlarsan sonsuz döngü |
| `switch` içinde | `break` switch'ten, `continue` **döngüden** çıkar |

---

## 3 — Nesne yönelimli

| Konu | Kural |
|---|---|
| Başlatma sırası | **Statikler (bir kez, metin sırası) → örnek alan/blok → constructor** |
| Kalıtımda | Tüm statikler → üst örnek+ctor → alt örnek+ctor |
| `this()`/`super()` | **İlk deyim**, birlikte olamaz |
| Varsayılan ctor | Yalnızca **hiç ctor yoksa** üretilir |
| `public void Sinif()` | Constructor **değil**, metot |
| `final` alan | **Her** ctor'da tam **bir kez** atanmalı |
| Overload sırası | **Tam eşleşme → genişletme → kutulama → varargs** |
| `int → Integer → Long` | **Yok**; `int → Integer → Object` **var** |
| Varargs | Bir tane, **sonda**; `f()` boş dizi, `f((T[])null)` NPE |
| `var` | Yalnızca yerel/döngü/try-resource/lambda param; `var x;`, `var x = null;` **yasak** |
| Pass-by-value | **İçi** değişirse görünür, **referans** değişirse görünmez |
| Record | Alanlar `private final`, accessor **`x()`**, örnek alan/blok **yasak**, implicitly **final** |
| Compact ctor | Parantezsiz, **parametreye** atanır; ek ctor `this(...)` ile delege eder |
| Record immutability | **Shallow** — mutable bileşen için savunmacı kopya |
| Override 5 şart | Aynı imza · covariant dönüş · erişim daraltılamaz · checked exception genişletilemez · üst `final/static/private` olamaz |
| Hiding | **Statik metot** ve **alan** → referans tipine göre |
| `equals(Tip)` | **Overload**, override değil → `Set`'te bozuk davranış |
| `abstract` | Ctor'ı **olabilir**; metodun gövdesi **olamaz**; `private`/`static`/`final` olamaz |
| `sealed` alt tip | **`final` / `sealed` / `non-sealed`** şart; aynı paket/modül |
| Arayüz üyeleri | Metot `public abstract`, alan `public static final` (**değer şart**) |
| Arayüz `static` metot | **Kalıtılmaz** — `Arayuz.metot()` |
| Çakışma | **Sınıf arayüzü yener**; iki ilişkisiz `default` → **override zorunlu** (`A.super.m()`) |
| Fonksiyonel arayüz | **Tam bir** abstract metot; `Object` metotları sayılmaz |
| Enum | Ctor **örtük private**, sabit başına **bir kez**; sabitlerden sonra `;`; `values()` yeni dizi; `valueOf` bulamazsa **`IllegalArgumentException`** |
| İç sınıflar | `new Dis.Nested()` vs **`dis.new Inner()`**; local sınıf belirteç alamaz; anonim sınıf **tam bir tip**, ctor yok, sonunda `;` |
| Java 16+ | Inner ve local sınıflar **`static` üye bildirebilir** |
| `this` | Anonim sınıfta **kendisi**, lambda'da **çevreleyen sınıf** |

---

## 4 — İstisnalar

| Konu | Kural |
|---|---|
| Checked | `Exception` ve altı, **`RuntimeException` hariç** |
| `NumberFormatException` | **Unchecked** (`IllegalArgumentException` altı) |
| `try` tek başına | ❌ `catch` veya `finally` şart (**try-with-resources hariç**) |
| Atılamayan checked'i yakalama | **Derleme hatası** (`Exception`/`RuntimeException` hariç) |
| `catch` sırası | **Alt sınıf önce** |
| Multi-catch | Tipler **akraba olamaz**; değişken **örtük final** |
| `finally` | **Her zaman** çalışır — `System.exit()` hariç |
| `finally`'de `return` | Her şeyi **ezer**, exception'ı **yutar** |
| try-with-resources | **Ters sırayla**, **`catch`'ten önce** kapanır; kaynak `catch`'te **görünmez** |
| Bastırma | Gövde kazanır, `close()` **suppressed** |
| `throw null;` | Derlenir → **NPE** |
| Özel exception | `super(mesaj)` yazmazsan `getMessage()` **null** |

---

## 5 — Diziler ve koleksiyonlar

| Konu | Kural |
|---|---|
| `int e[], f;` | `e` dizi, `f` **int** |
| Dizide uzunluk | **`length` alanı** (String'de `length()`, koleksiyonda `size()`) |
| `binarySearch` | Sıralı ister; değilse **tanımsız**. Bulamazsa **`-(ekleme) - 1`** |
| `Arrays.sort` | **`void`**, yerinde |
| `Arrays.asList` | **Sabit boyut**, diziyle **bağlı**: `set` diziyi değiştirir, `add` patlar |
| Dizi kovaryansı | `Object[] o = new String[2]` derlenir → **`ArrayStoreException`** |
| Generics | **Kovaryant değil**; `instanceof List<String>` **yasak** (erasure) |
| Null kabul etmeyenler | `TreeSet`, `TreeMap` (anahtar), `ArrayDeque`, `PriorityQueue` |
| Fabrika metotları | Immutable, **null yasak**; `Set.of`/`Map.of` tekrarda **`IllegalArgumentException`**; `Map.of` ≤ **10 çift** |
| `List<Integer>.remove(1)` | **İndeksi** siler |
| `map.put` | **Eski değeri** döner |
| Queue | Exception: `add/remove/element` · özel değer: **`offer/poll/peek`** |
| `PriorityQueue.toString` | **Sıralı değil**; yalnızca baş garantili |
| Wildcard | `? extends` **eklenemez**, `? super` **eklenir** (PECS) |
| `reversed()` konumu | Kendinden **önceki her şeyi** ters çevirir |
| Sequenced (Java 21) | `getFirst/getLast/addFirst/addLast/removeFirst/removeLast/reversed` |
| Kimde var | `List`, `Deque`, `LinkedHashSet`, `TreeSet`, `LinkedHashMap`, `TreeMap`. **`HashSet`/`HashMap` yok** |
| `TreeSet.addFirst` | **`UnsupportedOperationException`** |
| Boş koleksiyon | `getFirst()` → **`NoSuchElementException`**; `firstEntry()` → **`null`** |

---

## 6 — Lambda ve streams

| Konu | Kural |
|---|---|
| Arayüzler | `Supplier` verir · `Consumer` alır · `Predicate` sorar · `Function` dönüştürür |
| `IntFunction<R>` | **int alır** · `ToIntFunction<T>` **int döner** |
| `andThen` / `compose` | **Önce this** / **önce argüman** |
| Method reference | `String::length` → `Function` · `"abc"::length` → `Supplier` |
| Lambda kısıtları | Parametre adı çevredeki değişkeni **gölgeleyemez**; yakalanan **effectively final** |
| Tembellik | Terminal işlem yoksa **hiçbir şey** çalışmaz |
| Tek kullanımlık | İkinci kullanım → **`IllegalStateException`** |
| Sonsuz kaynak | `generate` ve 2 argümanlı `iterate`; **`sorted` asla bitmez** |
| `filter` vs `takeWhile` | Tümünü tarar vs **ilk ihlalde durur** |
| `range` / `rangeClosed` | Üst sınır hariç / dahil |
| Boş stream | `allMatch`/`noneMatch` **`true`**, `anyMatch` `false` |
| `reduce` | `(id,acc)` → `T` · `(acc)` → **`Optional`** · üçlü → paralel/farklı tip |
| `stream.toList()` | **Değiştirilemez**, null **kabul eder** |
| `toMap` | Çakışan anahtar → **`IllegalStateException`**; null değer → NPE |
| `groupingBy` | Varsayılan `HashMap<K, List<T>>` |
| `partitioningBy` | **Her zaman** `true` ve `false` anahtarı (boş listeyle) |
| `counting()` | **`Long`** |
| Primitif stream | Yalnızca `Int`/`Long`/`Double`. `boxed()`, `mapToObj`, `mapToInt` |
| `average()` | **`OptionalDouble`**; `sum()` boş stream'de **`0`** |
| `OptionalInt` | **`getAsInt()`**; `Optional<Integer>` ile **ilişkisiz** |
| `"abc".chars()` | **`IntStream`** |
| Paralel | `forEach` **sırasız**, `findAny` hızlı; yanlış identity → **yanlış sonuç**; paylaşılan koleksiyona `add` **güvensiz** |
| `Optional.of(null)` | **NPE** · `orElse` argümanı **her zaman** hesaplanır |

---

## 7 — Modüller

| Konu | Kural |
|---|---|
| Yön | **`requires` içeri**, **`exports` dışarı** |
| Yansıma | **`opens`** gerekir; `exports` yetmez |
| `requires transitive` | Bağımlılığı **kullananlara da** geçirir |
| `requires static` | Derlemede zorunlu, çalışmada isteğe bağlı |
| Servis | Sağlayıcı `provides ... with ...` · tüketici **`uses`** · uygulama paketi **`exports` gerekmez** |
| `uses` yoksa | `ServiceLoader` **boş döner**, hata vermez |
| Modül adı | **Tire yasak**; `java.base` otomatik |
| Automatic modül | Modül yolundaki `module-info`'suz jar — **her şeyi exports** eder |
| Unnamed modül | Classpath; named modüller **okuyamaz** |
| Komutlar | `-p` = `--module-path` · `-m` = **`modul/SinifTamAdi`** |
| Derleme | `javac -d out --module-source-path src ...` |
| `jar --describe-module` | Yönergeleri gösterir |
| `jlink` | Özel runtime; **automatic modülle çalışmaz** |
| Split package | **Yasak** — bir paket tek modülde |
| `public` | Paket **`exports`** edilmediyse dışarıdan erişilemez |

---

## 8 — Eşzamanlılık

| Konu | Kural |
|---|---|
| `run()` doğrudan | **Aynı thread'de** çalışır |
| İkinci `start()` | **`IllegalThreadStateException`** |
| `sleep` | **Checked** `InterruptedException`; kilitleri **bırakmaz** |
| Durumlar | `NEW`/`RUNNABLE`/`BLOCKED`/`WAITING`/`TIMED_WAITING`/`TERMINATED` |
| Virtual thread | **Her zaman daemon**; `setDaemon(false)` → **`IllegalArgumentException`**; öncelik **yok sayılır**; **havuzlanmaz** |
| Pinning | `synchronized` içinde bloklanma → taşıyıcıya çivilenir → **`ReentrantLock`** |
| `execute` / `submit` | `void` / **`Future`**; `submit(Runnable).get()` → **`null`** |
| Görev hatası | **`get()`'te** `ExecutionException`, asıl hata **`getCause()`** |
| `get()` çağrılmazsa | Hata **kaybolur** |
| `invokeAll` / `invokeAny` | Tümü bitince `List<Future>` / **ilk başarılı değer** |
| `shutdown` | **Beklemez**; `awaitTermination` bekler |
| Kapalıya `submit` | **`RejectedExecutionException`** |
| `close()` (19+) | `shutdown` + **bekler** |
| `volatile` | **Görünürlük**, atomiklik **değil** |
| `static synchronized` | **`Sinif.class`** kilidi |
| `wait`/`notify` | Kilit tutulmalı, yoksa **`IllegalMonitorStateException`**; `while` döngüsünde |
| `getAndIncrement` | **Eski** değer |
| `unlock` | **`finally`**'de; kilitsiz `unlock` → `IllegalMonitorStateException` |
| `ConcurrentHashMap` | **Null yasak** |
| `CopyOnWriteArrayList` | Iterasyonda **CME atmaz** |
| `BlockingQueue` | `put`/`take` **bloklar** · `offer`/`poll` özel değer · `add`/`remove` **atar** |

---

## 9 — I/O

| Konu | Kural |
|---|---|
| Aileler | Byte: `InputStream`/`OutputStream` · Karakter: `Reader`/`Writer` |
| Köprü | `InputStreamReader`, `OutputStreamWriter` |
| Sarmalayıcı | Tek başına açılamaz; en **dıştakini** kapatmak yeter |
| `read()` | **`int`**, sonda **`-1`**; `readLine()` sonda **`null`** |
| `PrintWriter` | **Exception atmaz** → `checkError()` |
| `System.console()` | **`null` dönebilir** |
| `Serializable` | Marker; uygulamayan → **`NotSerializableException`** |
| `transient` / `static` | **Serileştirilmez**; `transient` geri okunduğunda **varsayılan** |
| Deserialization | Ctor **çalışmaz**; **ilk non-serializable üstün** parametresiz ctor'ı çalışır |
| `readObject` | **Checked** `ClassNotFoundException` + `IOException` |
| `Path` | **Metin işlemi** — diske dokunmaz (`toRealPath` hariç) |
| `getNameCount` | Kökü **saymaz** |
| `resolve` | Argüman **mutlaksa** soldakini yok sayar |
| `relativize` | İkisi de **aynı türden** olmalı; değilse `IllegalArgumentException` |
| `equals` | **Normalize etmez** |
| `Files.delete` | Yoksa **`NoSuchFileException`**; `deleteIfExists` **`false`** |
| `createDirectories` | Ara dizinleri **oluşturur**, varsa hata vermez |
| `copy`/`move` | Hedef varsa **`FileAlreadyExistsException`** → `REPLACE_EXISTING` |
| `lines`/`list`/`walk` | **Kapatılmalı**; `readAllLines` gerekmez |

---

## 10 — Yerelleştirme

| Konu | Kural |
|---|---|
| `Locale` | **Dil küçük**, **ülke BÜYÜK** |
| Biçimler | `toString()` → `tr_TR` · `toLanguageTag()` → **`tr-TR`** |
| Bundle adı | **`TemelAd_dil_ULKE.properties`** |
| Arama | Özelden **genele**; en sonda default bundle |
| Bulunamazsa | **`MissingResourceException`** (unchecked) |
| Türkçe sayı | **Nokta binlik, virgül ondalık** |
| `NumberFormat.parse` | **`Number`** döner, **checked `ParseException`** |
| `FormatStyle` | `SHORT` → `MEDIUM` → `LONG` → `FULL` |
| `LocalDate` + saatli formatter | **`UnsupportedTemporalTypeException`** |

---

## Ezberlenecek sayılar

| Sayı | Ne |
|---|---|
| **−128 … 127** | Wrapper cache aralığı |
| **1 tabanlı** | `java.time` ay numarası |
| **0 tabanlı** | `enum.ordinal()`, dizi indeksi, `getName(i)` |
| **10** | `Map.of` maksimum çift sayısı |
| **3** | Primitif stream sayısı (Int/Long/Double) |
| **5** | Override'ın şart sayısı |
| **4** | İç sınıf türü sayısı |
| **50 / 120 / %68** | Sınav: soru / dakika / geçme notu ⚠️ **teyit et** |

---

## Sırada ne var

➡️ [`son-tekrar.md`](son-tekrar.md) — sınavdan 24 saat önce okunacak dosya.
➡️ [`deneme-1.html`](deneme-1.html) · [`deneme-2.html`](deneme-2.html)
