# 06 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 6.1 Lambda, fonksiyonel arayüzler ve method reference

### Soru 1 — `() -> 42`, `x -> x > 3`, `x -> System.out.print(x)`, `(a,b) -> a+b` — hangi arayüzler?

**Kısa cevap:** **`Supplier`, `Predicate`, `Consumer`, `BinaryOperator`/`BiFunction`.**

**Ayrıntı:** `() -> 42` parametre almaz, değer döner → `Supplier<Integer>`. `x -> x > 3` bir değer alıp `boolean` döner → `Predicate<T>` (sınav `Function<T,Boolean>` yerine `Predicate` bekler). `x -> System.out.print(x)` değer alır, dönmez → `Consumer<T>`. `(a,b) -> a+b` iki parametre alır; **aynı tipteyse** `BinaryOperator<T>`, genel hâli `BiFunction<T,U,R>`.

📌 **Sık yapılan hata:** `() -> 42`'yi `Consumer` sanmak (Consumer parametre alır, değer dönmez) veya `x -> x > 3`'ü sadece `Function<T,Boolean>` görüp `Predicate`'i atlamak.

🔗 [6.1 §2 Hazır fonksiyonel arayüzler](6.1-lambda-ve-fonksiyonel-arayuzler.md)

### Soru 2 — `f.andThen(g)` ile `f.compose(g)` arasındaki fark ne?

**Kısa cevap:** **`andThen` önce `f`'i sonra `g`'yi çalıştırır; `compose` önce `g`'yi sonra `f`'i.**

**Ayrıntı:** `andThen` soldan sağa akar: `f.andThen(g).apply(x)` = `g(f(x))`. `compose` sağdan sola akar: `f.compose(g).apply(x)` = `f(g(x))`. Örnek: `ekle = x->x+1`, `carp = x->x*2` için `ekle.andThen(carp).apply(5)` = `(5+1)*2 = 12`; `ekle.compose(carp).apply(5)` = `(5*2)+1 = 11`. Hafıza kancası: "*and then* = sonra; *compose* = önce."

📌 **Sık yapılan hata:** İkisini eşdeğer sanmak ya da sırayı ters kurmak. `compose` argümanı **önce** çalışır.

🔗 [6.1 §3 Default metotlar ve zincirleme](6.1-lambda-ve-fonksiyonel-arayuzler.md)

### Soru 3 — `IntFunction<String>` ve `ToIntFunction<String>` imzaları nedir?

**Kısa cevap:** **`IntFunction<String>`: `int -> String` (int alır); `ToIntFunction<String>`: `String -> int` (int döner).**

**Ayrıntı:** `IntFunction<R>` bir **`int` alır** ve `R` döner (`int -> R`). `ToIntFunction<T>` bir `T` alır ve **`int` döner** (`T -> int`). Yani ikisi birbirinin yön olarak tersidir: biri int'i tüketir, diğeri int üretir. Bu primitif sürümler kutulamayı önlemek içindir.

📌 **Sık yapılan hata:** Yönü karıştırmak. "To**Int**" adı sonucun int olduğunu söyler; `IntFunction` ise girişin int olduğunu.

🔗 [6.1 §2 Primitif sürümler](6.1-lambda-ve-fonksiyonel-arayuzler.md)

### Soru 4 — Lambda parametresine çevredeki bir yerel değişkenin adını verirsen ne olur?

**Kısa cevap:** **Derlenmez** — lambda parametresi çevredeki bir yerel değişkeni gölgeleyemez.

**Ayrıntı:** `int x = 5; Function<Integer,Integer> f = x -> x * 2;` derleme hatası verir, çünkü `x` zaten kapsamda tanımlıdır ve lambda parametresi aynı adı **gölgeleyemez** (shadowing yasak). Ayrı bir ad seçilmelidir.

📌 **Sık yapılan hata:** Bunu çalışma zamanı sorunu ya da gölgeleme yoluyla geçerli sanmak. İç bloklardaki bazı gölgelemelerin aksine, lambda parametresinde bu **derleme hatasıdır**.

🔗 [6.1 §1 Lambda sözdizimi](6.1-lambda-ve-fonksiyonel-arayuzler.md)

### Soru 5 — `String::length` ile `s::length` hangi arayüzlere uyar?

**Kısa cevap:** **`String::length` → `Function<String,Integer>`; `s::length` → `Supplier<Integer>`.**

**Ayrıntı:** `String::length` "rastgele nesnenin metodu" biçimidir: nesneyi **parametre olarak alır** (`s -> s.length()`), yani `Function<String,Integer>`. `s::length` ise "belirli bir nesnenin metodu" biçimidir: nesne (`s`) zaten bellidir, parametre almaz (`() -> s.length()`), yani `Supplier<Integer>`.

📌 **Sık yapılan hata:** İkisini aynı tip sanmak. Nesnenin tip adı mı (`String`) yoksa örnek mi (`s`) yazıldığına göre biçim ve arayüz değişir.

🔗 [6.1 §4 Method reference — dört biçim](6.1-lambda-ve-fonksiyonel-arayuzler.md)

### Soru 6 — Lambda gövdesinde `IOException` atabilir misin? Koşulu ne?

**Kısa cevap:** **Yalnızca hedef arayüzün soyut metodu o checked exception'ı bildiriyorsa atabilirsin.**

**Ayrıntı:** Lambda gövdesi, uyguladığı SAM'in **bildirmediği** bir checked exception atamaz. `Function<String,Integer> f = s -> { throw new IOException(); };` derlenmez çünkü `apply` checked exception bildirmez. Ama `Callable<Integer> c = () -> { throw new IOException(); };` derlenir çünkü `call()` `throws Exception` içerir. Unchecked (RuntimeException) her zaman atılabilir.

📌 **Sık yapılan hata:** Her lambda'da checked exception atılabileceğini sanmak. Koşul, hedef arayüzün metodunun o exception'ı bildirmesidir.

🔗 [6.1 §5 Lambda ve exception](6.1-lambda-ve-fonksiyonel-arayuzler.md)

## 6.2 Stream oluşturma, ara işlemler ve Optional

### Soru 1 — Terminal işlem olmadan `peek` çalışır mı? Neden?

**Kısa cevap:** **Hayır, hiçbir çıktı üretmez.** Stream tembeldir.

**Ayrıntı:** Ara işlemler (`peek` dahil) **tembeldir**: terminal işlem çağrılana kadar hiçbir eleman boru hattından geçmez. `Stream.of("a","b").peek(System.out::println);` tek başına hiçbir şey yazdırmaz. Stream bir veri yapısı değil, bir **işlem tarifidir**; tarif ancak bir terminal işlem (örn. `forEach`, `count`, `collect`) ile yürür.

📌 **Sık yapılan hata:** `peek`'in gözlem amaçlı olduğu için hemen çalıştığını sanmak. Terminal olmadan hiçbir ara işlem yürümez.

🔗 [6.2 §2 Tembellik ve tek kullanımlık olma](6.2-stream-ve-ara-islemler.md)

### Soru 2 — Aynı stream'i iki kez kullanırsan ne olur?

**Kısa cevap:** **Çalışma zamanında `IllegalStateException`** ("stream has already been operated upon or closed").

**Ayrıntı:** Bir stream **tek kullanımlıktır**. İlk terminal işlem stream'i tüketir; ikinci bir terminal (ya da ara) işlem çağrılırsa `IllegalStateException` atılır. Örnek: `Stream<String> s = Stream.of("a","b"); s.count(); s.count();` → ikinci `count()` patlar. Çözüm: kaynaktan yeniden `stream()` çağırmaktır.

📌 **Sık yapılan hata:** Stream'i koleksiyon gibi tekrar tekrar kullanılabilir sanmak. Her kullanım için yeni stream gerekir.

🔗 [6.2 §2 Tembellik ve tek kullanımlık olma](6.2-stream-ve-ara-islemler.md)

### Soru 3 — `Stream.iterate(1, x -> x + 1).sorted().limit(5)` ne yapar?

**Kısa cevap:** **Asla bitmez** — program sonsuz döngüye girer.

**Ayrıntı:** İki argümanlı `Stream.iterate(1, x -> x + 1)` **sonsuz** bir stream üretir. `sorted()` sıralamak için **tüm** elemanları görmek zorundadır; sonsuz kaynakta bu asla tamamlanmaz, dolayısıyla `limit(5)`'e hiç ulaşılamaz. `limit`, `sorted`'dan **önce** gelmeliydi: `...limit(5).sorted()` çalışırdı.

📌 **Sık yapılan hata:** `limit(5)`'in en sonda olduğu için akışı 5 elemanla sınırlayacağını sanmak. `sorted` kısa devre yapmaz; sonsuz kaynakta önce `limit` gelmeli.

🔗 [6.2 §3 Ara işlemler (intermediate)](6.2-stream-ve-ara-islemler.md)

### Soru 4 — `filter` ile `takeWhile` arasındaki fark ne? `[1,2,3,1,2]` için sonuçlar?

**Kısa cevap:** **`filter` tüm elemanları tarar; `takeWhile` koşul ilk bozulunca durur.** `filter(x<3)` → `[1,2,1,2]`; `takeWhile(x<3)` → `[1,2]`.

**Ayrıntı:** `filter(Predicate)` her elemanı sınar ve koşulu sağlayanların **hepsini** geçirir; `[1,2,3,1,2]` üzerinde 3'ü eler ama sonraki `1,2`'yi de geçirir → `[1,2,1,2]`. `takeWhile(Predicate)` koşul **ilk kez bozulduğunda** akışı durdurur; ilk 3'te durur → `[1,2]`. (`dropWhile` ise ilk ihlalden itibaren alır → `[3,1,2]`.)

📌 **Sık yapılan hata:** `takeWhile`'ı `filter` gibi tüm listeyi tarayan bir eleyici sanmak. `takeWhile` ilk ihlalde durur, sonrasına hiç bakmaz.

🔗 [6.2 §3 filter vs takeWhile](6.2-stream-ve-ara-islemler.md)

### Soru 5 — `orElse` ile `orElseGet` arasındaki performans farkı nereden gelir?

**Kısa cevap:** **`orElse`'in argümanı Optional dolu olsa bile her zaman değerlendirilir; `orElseGet`'in supplier'ı yalnızca gerekince çalışır.**

**Ayrıntı:** `orElse(v)` bir **değer** alır; bu değer (örn. bir metot çağrısı) Optional dolu olsa bile hesaplanır, sadece sonucu kullanılmaz. `Optional.of("var").orElse(pahali())` → `pahali()` **boşuna çalışır**. `orElseGet(Supplier)` bir tembel supplier alır; yalnızca Optional boşsa çalıştırılır: `Optional.of("var").orElseGet(() -> pahali())` → `pahali()` **hiç çalışmaz**. Pahalı bir yedek değer için `orElseGet` kullanılmalıdır.

📌 **Sık yapılan hata:** `orElse`'in argümanının yalnızca boşta çalıştığını sanmak. Argüman her durumda değerlendirilir; fark tembelliktedir.

🔗 [6.2 §4 orElse vs orElseGet](6.2-stream-ve-ara-islemler.md)

### Soru 6 — `Optional.of(null)` ile `Optional.ofNullable(null)` arasındaki fark?

**Kısa cevap:** **`Optional.of(null)` → `NullPointerException`; `Optional.ofNullable(null)` → güvenli, boş Optional.**

**Ayrıntı:** `Optional.of(x)` `x`'in `null` olmayacağını varsayar; `null` verilirse çalışma zamanında **NPE** atar. `Optional.ofNullable(x)` `null`'a toleranslıdır: değer `null` ise `Optional.empty()` döner, değilse dolu Optional. Değerin `null` olabileceği durumlarda `ofNullable` kullanılır.

📌 **Sık yapılan hata:** `Optional.of(null)`'ı boş Optional üretir sanmak. O NPE atar; boş Optional için `ofNullable` ya da `empty()` gerekir.

🔗 [6.2 §4 Optional](6.2-stream-ve-ara-islemler.md)

## 6.3 Terminal işlemler, reduction ve Collectors

### Soru 1 — Boş bir stream'de `allMatch`, `anyMatch`, `noneMatch` ne döner? Neden?

**Kısa cevap:** **`allMatch` → `true`, `noneMatch` → `true`, `anyMatch` → `false`.**

**Ayrıntı:** Boş stream'de eşleştirme "vacuous truth" (boş doğruluk) mantığıyla çalışır. `allMatch`: "aksini gösteren eleman yok" → `true`. `noneMatch`: "koşulu sağlayan eleman yok" → `true`. `anyMatch`: "koşulu sağlayan en az bir eleman var mı" → hiç eleman olmadığından `false`. Sınav özellikle `allMatch=true`'yu sık sorar.

📌 **Sık yapılan hata:** Boş stream'de `allMatch`'in `false` döneceğini sanmak. Eleman olmadığından koşul boş yere doğrudur → `true`.

🔗 [6.3 §1 Boş stream'de eşleştirme](6.3-terminal-ve-collectors.md)

### Soru 2 — `reduce`'un üç biçimi ve dönüş tipleri neler?

**Kısa cevap:** **`reduce(id, acc)` → `T`; `reduce(acc)` → `Optional<T>`; `reduce(id, acc, combiner)` → `U`.**

**Ayrıntı:** (1) `reduce(identity, accumulator)` bir identity'yle başlar, boş olsa bile identity'yi döndürebildiği için `T` döner (Optional değil). (2) `reduce(accumulator)` identity yoktur; stream boş olabileceğinden `Optional<T>` döner. (3) `reduce(identity, accumulator, combiner)` sonuç tipi eleman tipinden **farklı** olabildiğinde ve/veya **paralel** çalıştırmada kullanılır, `U` döner. Identity, birleştirme için **etkisiz eleman** olmalıdır (toplamda `0`, çarpımda `1`, metinde `""`).

📌 **Sık yapılan hata:** `reduce(id, acc)`'in `Optional` döndüğünü sanmak. Identity'li biçim doğrudan `T` döner; `Optional<Integer> o = Stream.of(1,2).reduce(0, Integer::sum);` derlenmez.

🔗 [6.3 §2 reduce — üç biçim](6.3-terminal-ve-collectors.md)

### Soru 3 — `toMap` iki eleman aynı anahtarı üretirse ne olur, nasıl çözersin?

**Kısa cevap:** **`IllegalStateException: Duplicate key` atılır;** üç argümanlı `toMap(keyFn, valFn, mergeFn)` ile çözülür.

**Ayrıntı:** İki argümanlı `Collectors.toMap(keyFn, valFn)` çakışan bir anahtar bulursa çalışma zamanında `IllegalStateException` atar. Çözüm, çakışmayı çözen bir **merge fonksiyonu** eklemektir: `toMap(keyFn, valFn, (a, b) -> a + b)` gibi. Ayrıca `toMap`'te **değer `null` olamaz** → NPE.

📌 **Sık yapılan hata:** `toMap`'in çakışan anahtarda son değeri sessizce yazacağını sanmak. Merge fonksiyonu olmadan istisna atar.

🔗 [6.3 §3 Collectors / toMap tuzağı](6.3-terminal-ve-collectors.md)

### Soru 4 — `stream.toList()` ile `collect(Collectors.toList())` arasındaki iki fark ne?

**Kısa cevap:** **`stream.toList()` değiştirilemez bir liste döner (Java 16+); `collect(Collectors.toList())` genelde değiştirilebilir bir `ArrayList` döner (Java 8+).**

**Ayrıntı:** İki fark: (1) **Değiştirilebilirlik** — `stream.toList()` üzerinde `add` yaparsan `UnsupportedOperationException` alırsın; `collect(Collectors.toList())` sonucu genelde değiştirilebilir. (2) **Sürüm** — `toList()` Java 16+, `collect(toList())` Java 8+. İkisi de `null` eleman kabul eder. (Ayrı bir seçenek olan `collect(toUnmodifiableList())` ise değiştirilemez ve **`null` kabul etmez**.)

📌 **Sık yapılan hata:** `stream.toList()` sonucuna eleman eklenebileceğini sanmak. O değiştirilemezdir; `add` çalışma zamanında patlar.

🔗 [6.3 §1 toList() vs collect(toList())](6.3-terminal-ve-collectors.md)

### Soru 5 — `groupingBy` ile `partitioningBy` arasındaki iki fark ne?

**Kısa cevap:** **`groupingBy` anahtarları `Function` sonucudur ve boş grup için anahtar hiç oluşmaz; `partitioningBy` yalnızca `true`/`false` anahtarları üretir ve boş olsa bile her iki anahtar da vardır.**

**Ayrıntı:** (1) **Anahtar kümesi** — `groupingBy(fn)` fonksiyonun ürettiği istediğin kadar anahtar üretir (varsayılan `HashMap<K, List<T>>`); `partitioningBy(pred)` her zaman tam iki anahtar döner: `true` ve `false`. (2) **Boş sonuç** — `groupingBy`'da hiç eleman düşmeyen bir anahtar map'te **hiç yer almaz** (`get` → `null`); `partitioningBy`'da hiçbir eleman koşulu sağlamasa bile `map.get(true)` **boş liste** döner, `null` değil. İkisi de downstream toplayıcı alabilir.

📌 **Sık yapılan hata:** `partitioningBy` boş tarafında `null` beklemek. Orada boş liste vardır; `null` dönen `groupingBy`'ın olmayan anahtarıdır.

🔗 [6.3 §3 groupingBy ve partitioningBy](6.3-terminal-ve-collectors.md)

### Soru 6 — `counting()` hangi tipi döner?

**Kısa cevap:** **`Long`.**

**Ayrıntı:** `Collectors.counting()` bir downstream toplayıcı olarak eleman sayısını **`Long`** olarak döner (örn. `groupingBy(fn, counting())` → `Map<K, Long>`). Terminal `count()` işleminin `long` (primitif) döndürmesiyle karıştırılmamalıdır; `counting()` wrapper `Long` döner ve `Integer` değildir.

📌 **Sık yapılan hata:** `counting()`'in `Integer` döndüğünü sanmak. `Long` döner.

🔗 [6.3 §3 Temel toplayıcılar](6.3-terminal-ve-collectors.md)

## 6.4 Primitif stream'ler ve paralel stream'ler

### Soru 1 — `IntStream`'i `List<Integer>`'a çevirmenin doğru yolu nedir?

**Kısa cevap:** **`boxed().toList()`** ile — önce `boxed()` ile `Stream<Integer>`'a çevir.

**Ayrıntı:** `IntStream` üzerinde doğrudan `toList()` **yoktur**; `IntStream.range(1, 4).toList()` derlenmez. Önce `boxed()` ile `Stream<Integer>`'a geçmek gerekir: `IntStream.range(1, 4).boxed().toList()` → `[1, 2, 3]`. Alternatif olarak `mapToObj(...)` ile de nesne stream'ine geçilebilir.

📌 **Sık yapılan hata:** `IntStream.toList()`'in var olduğunu sanmak. Primitif stream'de `toList()` yoktur; `boxed()` şart.

🔗 [6.4 §1 Dönüşümler](6.4-primitif-ve-paralel.md)

### Soru 2 — `IntStream.of().sum()` ve `IntStream.of().average()` ne döner?

**Kısa cevap:** **`sum()` → `0`; `average()` → boş `OptionalDouble`.**

**Ayrıntı:** Boş bir primitif stream'de `sum()` doğrudan primitif `0` döner (Optional değil), çünkü toplamın etkisiz elemanı sıfırdır. `average()` ise ortalaması tanımsız olduğundan **`OptionalDouble`** döner ve boş stream'de `OptionalDouble.empty()`'dir. Dolu stream'de örn. `IntStream.of(1,2,3).average()` → `OptionalDouble[2.0]`.

📌 **Sık yapılan hata:** `average()`'ın doğrudan `double` döndüğünü sanmak (`double d = IntStream.of(1,2).average();` derlenmez) ya da `sum()`'ın Optional döndüğünü sanmak. `sum` → primitif `0`, `average` → `OptionalDouble`.

🔗 [6.4 §2 Primitif stream'e özel terminaller](6.4-primitif-ve-paralel.md)

### Soru 3 — `OptionalInt`'ten değeri nasıl alırsın, `Optional<Integer>`'dan farkı ne?

**Kısa cevap:** **`getAsInt()` ile alırsın;** `OptionalInt` ile `Optional<Integer>` **ilişkisiz** (birbirine atanamaz) tiplerdir.

**Ayrıntı:** `OptionalInt`'te `get()` **yoktur**; değer `getAsInt()` ile alınır (benzer şekilde `OptionalLong.getAsLong()`, `OptionalDouble.getAsDouble()`). `Optional<T>`'de ise `get()`/`orElse(v)` kullanılır. Ayrıca `OptionalInt`, `Optional<Integer>`'ın alt tipi **değildir** — bunlar birbiriyle ilişkisiz sınıflardır ve birbirine atanamaz.

📌 **Sık yapılan hata:** `OptionalInt.get()` çağırmak (yoktur) veya `OptionalInt`'i `Optional<Integer>`'a atamaya çalışmak. `getAsInt()` kullan; tipler ilişkisizdir.

🔗 [6.4 §2 Primitif stream'e özel terminaller](6.4-primitif-ve-paralel.md)

### Soru 4 — `"abc".chars().forEach(System.out::print)` ne yazdırır, neden?

**Kısa cevap:** **`979899`** — karakterler `int` (kod noktası) olarak akar.

**Ayrıntı:** `"abc".chars()` bir **`IntStream`** döner (`Stream<Character>` değil). Elemanlar karakterlerin int değerleridir: `a`=97, `b`=98, `c`=99. `System.out::print` bir `int` alıp sayısal değerini basar, dolayısıyla çıktı `979899` olur. Karakter olarak basmak isteseydin `mapToObj(c -> (char) c)` gibi bir dönüşüm gerekirdi.

📌 **Sık yapılan hata:** `chars()`'ın `abc` yazdıracağını sanmak. `chars()` `IntStream` üretir; karakterler int koduyla basılır → `979899`.

🔗 [6.4 §1 Üç primitif stream](6.4-primitif-ve-paralel.md)

### Soru 5 — Paralel stream'de `findFirst` ile `findAny` arasındaki fark ne?

**Kısa cevap:** **`findFirst` karşılaşma sırasındaki ilk elemanı garanti eder (paralelde yavaşlar); `findAny` herhangi birini döner (paralelde daha hızlı).**

**Ayrıntı:** `findFirst` paralel çalıştırmada bile **karşılaşma sırasını (encounter order)** korumak zorundadır, bu da parçalar arasında koordinasyon gerektirir ve performansı düşürür. `findAny` sıra garantisi vermez; herhangi bir parçanın bulduğu ilk elemanı dönebildiği için **paralelde tercih edilir** ve daha hızlıdır. Sıralı stream'de ikisi de genelde aynı elemanı döndürür.

📌 **Sık yapılan hata:** Paralelde `findFirst`'ün de rastgele döndüğünü sanmak. `findFirst` sırayı korur (bu yüzden yavaştır); rastgele/hızlı olan `findAny`'dir.

🔗 [6.4 §3 Paralelde ne değişir](6.4-primitif-ve-paralel.md)

### Soru 6 — `reduce`'un identity'si neden "etkisiz eleman" olmak zorunda?

**Kısa cevap:** **Paralelde her parça identity'yle başlar; identity etkisiz eleman değilse her parçaya fazladan katkı girer ve sonuç bozulur.**

**Ayrıntı:** Paralel `reduce`'ta stream parçalara bölünür, her parça kendi hesabına identity ile başlar, sonra combiner ile birleştirilir. Identity **etkisiz eleman** olmalıdır (toplamda `0`, çarpımda `1`, metin birleştirmede `""`) ki her parçaya eklenmesi sonucu değiştirmesin. Örn. `Stream.of("a","b","c").parallel().reduce("X", String::concat)` yanlış identity (`"X"`) yüzünden `"XaXbXc"` gibi tahmin edilemez sonuç verir. En sinsi yanı: aynı kod **sıralı çalışmada doğru, paralelde yanlış** sonuç üretir.

📌 **Sık yapılan hata:** Herhangi bir başlangıç değerini identity olarak vermek. Etkisiz eleman değilse sıralıda doğru görünüp paralelde bozulur.

🔗 [6.4 §3 Doğruluk tuzakları](6.4-primitif-ve-paralel.md)
