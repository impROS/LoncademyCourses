# Kavram sözlüğü

> **Bu dosya hatırlatma içindir, ilk öğrenme için değil.** Her terim konu
> dosyasında ilk geçtiği yerde açıklanıyor. Buraya "neydi bu ya?" dediğinde
> dönersin.
>
> Sınav **İngilizce** sorulur; bu yüzden her girdide terimin İngilizce hâli de
> yazılı. Şıklarda göreceğin biçim odur.
>
> Beş bölüm: [Platform ve araçlar](#a-platform-ve-araçlar) · [Dil temelleri](#b-dil-temelleri) ·
> [İstisnalar](#c-istisnalar) · [Koleksiyonlar ve stream'ler](#d-koleksiyonlar-ve-streamler) ·
> [Eşzamanlılık, g-ç ve yerelleştirme](#e-eşzamanlılık-g-ç-ve-yerelleştirme)

---

## A. Platform ve araçlar

#### JDK

*Java Development Kit.* Derleyici (`javac`), çalıştırıcı (`java`) ve standart
kütüphaneyi içeren geliştirme paketi. Sınav **Java SE 21** üzerinden sorulur;
daha yeni bir sürümle çalışırsan sınavda olmayan davranışları öğrenirsin.
→ [0.1 Ortam kurulumu](01-kurulum.md)

#### JVM

*Java Virtual Machine.* Derlenmiş bytecode'u çalıştıran sanal makine. Nesne
yaşam döngüsü, bellek yönetimi ve çöp toplama onun sorumluluğundadır.
→ [3.1 Sınıflar ve nesne yaşam döngüsü](../03-nesne-yonelimli/3.1-siniflar-ve-yasam-dongusu.md)

#### SE

*Standard Edition.* Java'nın masaüstü ve sunucu tarafı için standart sürümü.
Sınav kodu `1Z0-830` bu sürümün 21 numaralı hâlini ölçer.
→ [0.0 Sınav künyesi](00-sinav-kunyesi.md)

#### GC

*Garbage collector — çöp toplayıcı.* Erişilemez hâle gelmiş nesnelerin belleğini
geri alan mekanizma. Sınavın sorduğu şey **ne zaman çalıştığı değil** (garanti
yoktur), bir nesnenin ne zaman **toplanmaya uygun** hâle geldiğidir.
→ [3.1 Sınıflar ve nesne yaşam döngüsü](../03-nesne-yonelimli/3.1-siniflar-ve-yasam-dongusu.md)

#### API

*Application Programming Interface.* Bir kütüphanenin dışarıya sunduğu sınıf ve
metot kümesi. Sınavın en sık kalıbı "hangi API doğru?": şıkların hepsi derlenir
gibi görünür, biri gerçekten var olmayan bir metottur.
→ [0.0 Sınav künyesi](00-sinav-kunyesi.md)

#### IDE

Kod düzenleyici. Sınavda **tuzak kaynağıdır**: düzenleyici senin için otomatik
içe aktarma ekler ve hatayı gizler; sınavda o yardım yok.
→ [0.0 Sınav künyesi](00-sinav-kunyesi.md)

#### jar ve jlink

`jar` derlenmiş sınıfları tek arşive paketler. `jlink` ise uygulamanın ihtiyacı
olan modülleri seçip **kendi çalışma zamanını** üretir; tam JDK taşımak yerine
çok daha küçük bir paket çıkar.
→ [7.2 Derleme, jar, jlink ve modüle geçiş](../07-moduller/7.2-derleme-ve-migrasyon.md)

#### Modül

Java 9'la gelen paketleme birimi. `module-info.java` içinde `exports` ile neyi
dışarı açtığını, `requires` ile neye bağlı olduğunu bildirir.
→ [7.1 Modül tanımlama](../07-moduller/7.1-modul-tanimlama.md)

---

## B. Dil temelleri

#### Primitif ve wrapper

`int`, `double`, `boolean` gibi tipler **primitiftir**: değer taşırlar, `null`
olamazlar. `Integer`, `Double`, `Boolean` onların nesne karşılığıdır ve `null`
olabilirler.
→ [1.1 Primitifler, wrapper'lar ve operatörler](../01-temel-tipler/1.1-primitifler-ve-operatorler.md)

#### Autoboxing ve unboxing

Primitif ile wrapper arasındaki otomatik dönüşüm. `Integer` → `int` yönü
**unboxing**'dir ve wrapper `null` ise çalışma zamanında `NullPointerException`
atar — sınavın en sevdiği tuzaklardan biri.
→ [1.1 Primitifler, wrapper'lar ve operatörler](../01-temel-tipler/1.1-primitifler-ve-operatorler.md)

#### NPE

*NullPointerException.* `null` bir başvuru üzerinden alan ya da metoda erişilince
atılan çalışma zamanı istisnası. Sınavda çoğu zaman **unboxing** ya da zincirli
çağrı içinde gizlenir.
→ [1.1 Primitifler, wrapper'lar ve operatörler](../01-temel-tipler/1.1-primitifler-ve-operatorler.md)

#### Text block

Üç tırnakla (`"""`) yazılan çok satırlı metin sabiti. Girintiyi kendi kurallarına
göre kırpar; satır sonu ve boşluk davranışı sınavda sorulur.
→ [1.2 String, StringBuilder ve text block'lar](../01-temel-tipler/1.2-string-ve-stringbuilder.md)

#### var

Yerel değişkenin tipini derleyicinin çıkarması. **Yalnızca yerel değişkende**
kullanılır; alan, parametre ya da dönüş tipi olamaz ve `null` ile başlatılamaz.
→ [3.2 Metotlar, kapsam ve var](../03-nesne-yonelimli/3.2-metotlar-ve-kapsam.md)

#### Pattern matching

`instanceof` ya da `switch` içinde tipi denerken aynı anda bir değişkene bağlama.
`if (o instanceof String s)` yazınca `s` doğrudan kullanılabilir hâle gelir.
→ [2.1 if/else, switch ve pattern matching](../02-akis-kontrolu/2.1-if-switch-ve-pattern-matching.md)

#### switch ifadesi ve yield

`switch` bir **değer üretebilir**. Ok sözdizimiyle (`->`) tek satırda döner;
blok kullanırsan değeri `yield` ile vermek **zorunludur**.
→ [2.1 if/else, switch ve pattern matching](../02-akis-kontrolu/2.1-if-switch-ve-pattern-matching.md)

#### Record

Verinin taşıyıcısı olan, alanları kesin değişmez (immutable) sınıf türü.
Erişimci metotlar alan adıyla aynıdır: `n.x()` — `n.getX()` **değil**.
`equals`, `hashCode` ve `toString` derleyici tarafından üretilir.
→ [3.3 Record'lar](../03-nesne-yonelimli/3.3-recordlar.md)

#### Sealed tip

Kimlerin kendisinden türeyebileceğini `permits` ile **kısıtlayan** sınıf ya da
arayüz. Alt tip kümesi kapalı olduğu için `switch` bütün durumları kapsayabilir.
→ [3.5 Abstract sınıflar ve sealed tipler](../03-nesne-yonelimli/3.5-abstract-ve-sealed.md)

#### SAM

*Single Abstract Method.* Tek soyut metodu olan arayüz; **fonksiyonel arayüz**
budur ve lambda ile karşılanabilir. `@FunctionalInterface` işareti bunu derleyiciye
denetlettirir.
→ [3.6 Arayüzler ve fonksiyonel arayüzler](../03-nesne-yonelimli/3.6-arayuzler.md)

#### Enum

Sabit bir örnek kümesini tanımlayan tip. Kendi alanı, kurucusu ve metodu olabilir;
`switch` içinde nitelenmeden (`KIRMIZI`, `Renk.KIRMIZI` değil) yazılır.
→ [3.7 Enum'lar](../03-nesne-yonelimli/3.7-enumlar.md)

#### İç sınıf

Başka bir sınıfın içinde tanımlı sınıf. Dört çeşidi (üye, statik, yerel, isimsiz)
erişim ve `static` kurallarıyla ayrılır.
→ [3.8 İç sınıflar](../03-nesne-yonelimli/3.8-ic-siniflar.md)

---

## C. İstisnalar

#### Checked ve unchecked exception

**Checked** olanlar (`IOException` gibi) ya yakalanmak ya da `throws` ile
bildirilmek zorundadır; derleyici zorlar. **Unchecked** olanlar
(`RuntimeException` soyundan) zorunlu değildir.
→ [4.1 Exception temelleri](../04-istisnalar/4.1-exception-temelleri.md)

#### Multi-catch

Tek `catch` bloğunda birden çok istisna tipini `|` ile yakalamak. Tipler
birbirinin **alt/üst tipi olamaz** ve yakalanan değişken örtük olarak `final`'dır.
→ [4.1 Exception temelleri](../04-istisnalar/4.1-exception-temelleri.md)

#### try-with-resources

Parantez içinde açılan kaynakların blok bitince **otomatik kapatılması**. Kaynak
`AutoCloseable` olmalı, örtük olarak `final`'dır ve kapatma **açılışın tersi
sırayla** yapılır.
→ [4.2 try-with-resources ve özel exception'lar](../04-istisnalar/4.2-try-with-resources.md)

#### Suppressed exception

`try` bloğu istisna atarken kapatma da istisna atarsa, ikincisi **bastırılmış**
olarak birincinin içine eklenir; `getSuppressed()` ile okunur.
→ [4.2 try-with-resources ve özel exception'lar](../04-istisnalar/4.2-try-with-resources.md)

---

## D. Koleksiyonlar ve stream'ler

#### Generics

Tip parametresiyle çalışan sınıf ve metotlar (`List<String>`). Derleme
sonrasında tip bilgisi silinir (*type erasure*); sınav bu silinmenin sonuçlarını
sorar.
→ [5.2 List, Set, Map, Deque ve generics](../05-koleksiyonlar/5.2-collections-api.md)

#### Deque

*Double-ended queue.* İki ucundan da ekleme/çıkarma yapılabilen koleksiyon; yığın
(stack) ve kuyruk (queue) olarak da kullanılır.
→ [5.2 List, Set, Map, Deque ve generics](../05-koleksiyonlar/5.2-collections-api.md)

#### Comparable ve Comparator

`Comparable` tipin **kendi doğal sırasını** tanımlar (`compareTo`). `Comparator`
ise **dışarıdan** verilen sıralama ölçütüdür (`compare`); zincirlenebilir.
→ [5.3 Sıralama ve sequenced koleksiyonlar](../05-koleksiyonlar/5.3-siralama-ve-sequenced.md)

#### Sequenced Collections

Java 21'le gelen, **tanımlı bir sırası olan** koleksiyon arayüzleri. `getFirst`,
`getLast`, `reversed` gibi ortak metotlar getirir.
→ [5.3 Sıralama ve sequenced koleksiyonlar](../05-koleksiyonlar/5.3-siralama-ve-sequenced.md)

#### CME

*ConcurrentModificationException.* Bir koleksiyon üzerinde dolaşırken onu
değiştirince atılır. **Tek thread'de de olur** — adı yanıltıcıdır. Çözümü
`removeIf` ya da iterator'ın kendi `remove`'u.
→ [2.2 Döngüler, break ve continue](../02-akis-kontrolu/2.2-donguler.md)

#### Lambda ve method reference

Lambda, fonksiyonel arayüzü yerinde karşılayan kısa yazım (`x -> x * 2`).
Method reference (`String::length`) var olan bir metodu aynı yere takar.
→ [6.1 Lambda ve fonksiyonel arayüzler](../06-lambda-ve-streams/6.1-lambda-ve-fonksiyonel-arayuzler.md)

#### Ara ve terminal işlem

Stream'de `filter`, `map` gibi işlemler **ara** işlemdir: tembeldir, yeni bir
stream döner. `collect`, `forEach`, `reduce` gibi işlemler **terminal**dir:
akışı tüketir ve zinciri çalıştırır. Terminal işlem yoksa **hiçbir şey çalışmaz**.
→ [6.2 Stream oluşturma ve ara işlemler](../06-lambda-ve-streams/6.2-stream-ve-ara-islemler.md)

#### Optional

Değerin **olmayabileceğini** tipte belirten sarmalayıcı. `get()` yerine
`orElse`, `orElseThrow`, `ifPresent` kullanılır.
→ [6.2 Stream oluşturma ve ara işlemler](../06-lambda-ve-streams/6.2-stream-ve-ara-islemler.md)

#### Collectors

Terminal `collect` işleminin ne üreteceğini söyleyen yardımcılar: `toList`,
`toMap`, `groupingBy`, `partitioningBy`. `toMap` anahtar çakışmasında istisna
atar; birleştirme fonksiyonu vermek gerekir.
→ [6.3 Terminal işlemler ve collectors](../06-lambda-ve-streams/6.3-terminal-ve-collectors.md)

#### Paralel stream

İşi birden çok çekirdeğe dağıtan stream. Yalnızca **işlemci yoğun** ve yan
etkisiz işlerde kazandırır; g-ç bekleyen ya da paylaşılan durum değiştiren
işlerde yavaşlatır veya bozar.
→ [6.4 Primitif ve paralel stream'ler](../06-lambda-ve-streams/6.4-primitif-ve-paralel.md)

---

## E. Eşzamanlılık, g-ç ve yerelleştirme

#### Virtual thread

Java 21'in hafif thread'i. İşletim sistemi thread'ine birebir bağlı olmadığı için
binlercesi açılabilir; **bekleyen** işlerde (ağ, dosya) kazandırır.
→ [8.1 Thread'ler ve virtual thread'ler](../08-eszamanlilik/8.1-threadler-ve-virtual-threads.md)

#### ExecutorService

Thread'leri elle yönetmek yerine iş gönderilen havuz. `submit` bir `Future`
döndürür; kapatma `shutdown` ile yapılır.
→ [8.2 ExecutorService, Callable ve Future](../08-eszamanlilik/8.2-executor-service.md)

#### Callable ve Future

`Callable` değer **döndürebilen** ve istisna atabilen görevdir (`Runnable`
döndüremez). `Future` o değerin ileride alınacağı tutamaçtır; `get()` sonucu
gelene kadar bekler.
→ [8.2 ExecutorService, Callable ve Future](../08-eszamanlilik/8.2-executor-service.md)

#### Thread-safe

Aynı anda birden çok thread'den kullanıldığında bozulmayan kod. Kilitler,
`Atomic*` sınıfları ve `Concurrent*` koleksiyonları bunu sağlamanın üç yoludur.
→ [8.3 Thread-safe kod](../08-eszamanlilik/8.3-thread-safety.md)

#### Serialization

Nesnenin bayt dizisine çevrilip geri okunabilmesi. `transient` alanlar
yazılmaz; geri okunurken kurucu **çalışmaz**.
→ [9.1 I/O stream'leri ve serialization](../09-io/9.1-io-streams-ve-serialization.md)

#### NIO.2

Java 7'yle gelen dosya sistemi API'si: `Path` bir yolu **temsil eder** (dosyanın
var olması gerekmez), `Files` o yol üzerinde işlem yapar.
→ [9.2 NIO.2: Path ve Files](../09-io/9.2-nio2-path-ve-files.md)

#### Locale

Dil ve ülke birleşimi. Yazım kuralı sınavda sorulur: **dil küçük harf, ülke
BÜYÜK harf** (`tr_TR`).
→ [10.1 Locale, ResourceBundle ve biçimlendirme](../10-yerellestirme/10.1-locale-ve-bicimlendirme.md)

#### ResourceBundle

Diller arasında değişen metinleri tutan kaynak dosyası kümesi. Arama sırası
belirlidir ve tam eşleşme bulunamazsa **daha genel** olana düşer.
→ [10.1 Locale, ResourceBundle ve biçimlendirme](../10-yerellestirme/10.1-locale-ve-bicimlendirme.md)

#### DST

*Daylight saving time — yaz saati.* `LocalDate`/`ZonedDateTime` üzerinde gün
eklemek ile saniye eklemek farklı sonuç verir: takvim günü 24 saat olmayabilir.
→ [1.3 Date-Time API](../01-temel-tipler/1.3-date-time-api.md)
