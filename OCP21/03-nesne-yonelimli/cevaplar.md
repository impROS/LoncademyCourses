# 03 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 3.1 Sınıflar, Nesne Yaşam Döngüsü ve Başlatma Sırası

### Soru 1 — Statik blok, örnek blok ve constructor hangi sırayla çalışır? Kalıtım varsa sıra nasıl değişir?

**Kısa cevap:** **Statikler (bir kez, metin sırasına göre) → örnek alanları/initializer blokları (her `new`'de, metin sırasına göre) → constructor gövdesi.**

**Ayrıntı:** Tek sınıfta önce statik alanlar ve statik bloklar sınıf ilk yüklendiğinde **bir kez** metin sırasıyla çalışır; sonra her `new`'de örnek alanları ve örnek initializer'ları metin sırasıyla; en son constructor gövdesi. Kalıtımda: **tüm statikler** (üst sonra alt) → üst sınıfın örnek initializer'ları + constructor gövdesi → alt sınıfın örnek initializer'ları + constructor gövdesi. Yani üst sınıf tamamen biter, sonra alt sınıf.

📌 **Sık yapılan hata:** Constructor gövdesini alan/blok atamalarından önce sanmak. Constructor **en son** çalışır; alanlar ve initializer'lar ondan önce hazırlanır.

🔗 [3.1 §3 Başlatma sırası](3.1-siniflar-ve-yasam-dongusu.md)

### Soru 2 — `public void Sinif() {}` yazarsan `new Sinif()` ne yapar?

**Kısa cevap:** **O metot çalışmaz; `Sinif()` bir constructor değil, `Sinif` adında normal bir metottur.**

**Ayrıntı:** Constructor'ın dönüş tipi olmaz. `void` yazıldığı anda bu artık constructor değil, sıradan bir metottur (derlenir ama sessizce çalışmaz). `new Sinif()` çağrısı derleyicinin ürettiği **varsayılan parametresiz constructor'ı** çalıştırır (başka constructor yazılmadığından üretilir) ve `Sinif()` metodunun gövdesindeki kod hiç yürümez.

📌 **Sık yapılan hata:** `void`'li ismi sınıf adıyla aynı olduğu için constructor sanmak. Dönüş tipi (`void` dahil) varsa constructor olmaz.

🔗 [3.1 §2 Constructor kuralları](3.1-siniflar-ve-yasam-dongusu.md)

### Soru 3 — Üst sınıfta yalnızca parametreli constructor varsa alt sınıfta ne yapman gerekir?

**Kısa cevap:** **Alt sınıfın constructor'ında ilk deyim olarak açıkça `super(...)` ile üst sınıfın parametreli constructor'ını çağırmalısın.**

**Ayrıntı:** Derleyici, açık bir `super(...)`/`this(...)` yazmadığın her constructor'a otomatik `super();` ekler. Üst sınıfta parametresiz constructor yoksa bu otomatik `super();` çağrısı hedefini bulamaz ve **derleme hatası** verir. Çözüm: alt constructor'da ilk deyim olarak uygun argümanlarla `super(...)` yaz.

📌 **Sık yapılan hata:** Üst sınıfta parametreli constructor olunca da parametresiz `super()`'in var sanmak. Bir constructor yazıldığı an varsayılan parametresiz constructor **eklenmez**.

🔗 [3.1 §2 Constructor kuralları](3.1-siniflar-ve-yasam-dongusu.md)

### Soru 4 — `final` bir alanı nerelerde atayabilirsin? Hiç atamazsan ne olur?

**Kısa cevap:** **Bildirimde, örnek initializer bloğunda veya HER constructor'da (tam olarak bir kez) atanabilir; hiç atanmazsa derleme hatası olur.**

**Ayrıntı:** `final` bir alan bildirimde (`final int x = 5;`), örnek initializer bloğunda ya da her constructor'da atanabilir — ama toplamda tam bir kez. Bir constructor'da atanıp diğerinde atanmazsa, ya da iki kez atanırsa derlenmez. Hiç atanmazsa **varsayılan değer kurtarmaz**, yine derleme hatası olur. `static final` alan aynı kuralı statik blokta karşılar.

📌 **Sık yapılan hata:** `final` alanın varsayılan değerle (0/null) yetineceğini sanmak. `final` alan mutlaka açıkça atanmalıdır.

🔗 [3.1 §4 final alanlar](3.1-siniflar-ve-yasam-dongusu.md)

### Soru 5 — Bir nesnenin GC'ye uygun hâle geldiğini nasıl anlarsın, `System.gc()` ne garanti eder?

**Kısa cevap:** **Nesne, hiçbir canlı thread'den erişilemez hâle geldiğinde GC'ye uygundur; `System.gc()` hiçbir şey garanti etmez, yalnızca öneridir.**

**Ayrıntı:** Bir nesne, kök erişilebilirlikten koptuğunda (hiçbir canlı referans onu göstermediğinde) GC'ye **uygun (eligible)** olur. Referansı `null` yapmak nesneyi hemen silmez, sadece uygun hâle getirir — üstelik başka bir referans hâlâ gösteriyorsa uygun bile olmaz. `System.gc()` çöp toplamayı yalnızca **önerir**; JVM yok sayabilir. `finalize()`'ın çağrılacağı da garanti değildir (deprecated).

📌 **Sık yapılan hata:** `null` atamanın veya `System.gc()`'nin nesneyi anında sildiğini sanmak. Toplama zamanı JVM'e bağlıdır, garantisi yoktur.

🔗 [3.1 §5 Nesne yaşam döngüsü ve GC](3.1-siniflar-ve-yasam-dongusu.md)

### Soru 6 — Üst sınıf constructor'ı override edilmiş bir metodu çağırırsa neden alt sınıf alanı `0` görünür?

**Kısa cevap:** **Çünkü üst sınıfın constructor'ı, alt sınıfın örnek alanları henüz başlatılmadan önce çalışır; dinamik bağlama override edilmiş metodu çağırır ama alan hâlâ varsayılan değerindedir.**

**Ayrıntı:** Başlatma sırasında önce üst sınıfın constructor'ı biter, sonra alt sınıfın alan initializer'ları çalışır. Üst constructor içinden çağrılan metot override edildiği için **alt sınıf sürümü** (nesne tipine göre) çalışır; ama bu an alt sınıfın `int x = 5;` ataması henüz yapılmamıştır, dolayısıyla `x` varsayılan `0` görünür.

📌 **Sık yapılan hata:** Alanın atamasının constructor çağrılmadan önce yapıldığını sanmak. Alt sınıf alanları ancak üst constructor bittikten sonra başlatılır.

🔗 [3.1 §3 Başlatma sırası (kalıtım tuzağı)](3.1-siniflar-ve-yasam-dongusu.md)

## 3.2 Metotlar, Overloading, Varargs, `var` ve Immutability

### Soru 1 — `void f(long)`, `void f(Integer)`, `void f(int...)` varken `f(5)` hangisini çağırır? Neden?

**Kısa cevap:** **`f(long)` çağrılır.** Genişletme (widening), kutulamadan da varargs'tan da önce gelir.

**Ayrıntı:** Overload çözümlemesi turlu ilerler: (1) tam eşleşme, (2) genişletme, (3) kutulama, (4) varargs — ve geri dönmez. `5` bir `int`'tir; tam eşleşen `f(int)` yoktur. İkinci turda `int → long` genişletmesi `f(long)`'a uyar ve seçim orada biter. `f(Integer)` (kutulama) ve `f(int...)` (varargs) daha sonraki turlar olduğu için hiç değerlendirilmez.

📌 **Sık yapılan hata:** `f(Integer)` daha "özel" göründüğü için seçileceğini sanmak. Genişletme kutulamayı her zaman yener.

🔗 [3.2 §1 Overload çözümleme](3.2-metotlar-ve-kapsam.md)

### Soru 2 — `void g(Long)` varken `g(5)` neden derlenmez ama `g(5L)` derlenir?

**Kısa cevap:** **`g(5)` derlenmez çünkü `int → Integer → Long` zinciri yoktur; `g(5L)` derlenir çünkü `long → Long` tek adım kutulamadır.**

**Ayrıntı:** `5` bir `int`'tir. Kutulama onu ancak `Integer` yapar; kutuladıktan **sonra** başka bir wrapper tipine (`Long`) genişletme yoktur, o yüzden `g(Long)`'a uymaz ve derleme hatası olur. `5L` bir `long`'tur; `long → Long` doğrudan autoboxing olduğu için `g(Long)`'a uyar. (Kutuladıktan sonra sadece üst tipe gitmek serbesttir: `int → Integer → Object` geçerlidir.)

📌 **Sık yapılan hata:** `int`'in `Long`'a kutulanabileceğini sanmak. Kutulama tipi sabittir: `int` yalnızca `Integer` olur.

🔗 [3.2 §1 Kutulama zinciri](3.2-metotlar-ve-kapsam.md)

### Soru 3 — `f(null)` çağrısı varargs metotta ne yapar?

**Kısa cevap:** **`null` doğrudan dizi olarak geçer; dizi `null` olur ve gövdede `s.length` gibi bir erişimde runtime `NullPointerException` atılır.**

**Ayrıntı:** `f(String... s)` metoduna `f(null)` verildiğinde derleyici `null`'ı boş dizi değil, doğrudan `String[]` referansı olarak yorumlar; böylece `s` **null dizi** olur (boş dizi değil). `s.length` çağrılınca çalışma zamanında NPE atılır. Boş dizi istiyorsan `f()` yazarsın (length 0); tek elemanlı null istiyorsan `f((String) null)` yazarsın (length 1).

📌 **Sık yapılan hata:** `f(null)`'ın boş dizi ya da length 0 vereceğini sanmak. Doğrudan `null` dizi geçer, erişimde patlar.

🔗 [3.2 §2 Varargs](3.2-metotlar-ve-kapsam.md)

### Soru 4 — `var`'ın kullanılamayacağı dört yeri say.

**Kısa cevap:** **Alan (field), metot parametresi, dönüş tipi ve `catch` parametresi.** (Ayrıca initializer'sız `var x;` ve `var x = null;` de olmaz.)

**Ayrıntı:** `var` yalnızca **yerel değişken** (initializer'lı), **for/for-each değişkeni**, **try-with-resources kaynağı** ve **lambda parametresinde** geçerlidir. Alan, metot parametresi, dönüş tipi ve `catch` parametresinde kullanılamaz. Ayrıca `var x;` (initializer yok), `var x = null;` (tip çıkarılamaz), `var a = 1, b = 2;` (çoklu bildirim) ve `var f = () -> {};` (hedef tip yok) derlenmez. Not: `var` ayrılmış kelime değildir; `int var = 5;` derlenir.

📌 **Sık yapılan hata:** `var`'ı metot parametresi veya alan olarak kullanmaya çalışmak. `var` yalnızca yerel bağlamda çalışır.

🔗 [3.2 §5 var kuralları](3.2-metotlar-ve-kapsam.md)

### Soru 5 — Bir metoda `List` verip içine `add` yaparsan dışarıda görülür mü? Parametreye yeni liste atarsan?

**Kısa cevap:** **İçine `add` yaparsan görülür (nesnenin içeriği değişti); parametreye yeni liste atarsan görülmez (yalnızca referansın kopyası yeniden bağlandı).**

**Ayrıntı:** Java her zaman **değer ile geçirir**; nesnelerde geçen değer referansın kopyasıdır. Metot içinde `list.add(...)` çağırırsan gerçek nesneyi değiştirdiğin için değişiklik dışarıda görülür. Ama `list = new ArrayList<>()` yazarsan sadece yerel kopyayı yeni bir nesneye bağlarsın; dışarıdaki orijinal referans etkilenmez. Kural: **içini değiştirirsen görülür, referansı değiştirirsen görülmez.**

📌 **Sık yapılan hata:** Parametreye yeni nesne atamanın dışarıyı değiştireceğini sanmak (Java'yı pass-by-reference sanmak). Referansın yalnızca kopyası geçer.

🔗 [3.2 §4 Değer ile geçiş](3.2-metotlar-ve-kapsam.md)

### Soru 6 — Immutable bir sınıfta savunmacı kopya niye şart?

**Kısa cevap:** **Aksi hâlde çağıran, verdiği (veya aldığı) değiştirilebilir nesneyi sonradan değiştirerek immutable sınıfın içini bozabilir.**

**Ayrıntı:** Immutable tarifi: `final` sınıf + `private final` alanlar + setter yok. Ama bir alan `List`, `Date` gibi değiştirilebilir bir nesneyse, constructor'da referansı doğrudan saklamak yetmez: çağıran elindeki aynı listeyi sonradan değiştirebilir. Bu yüzden constructor'da gelen nesnenin **kopyası** alınır (`new ArrayList<>(...)`) ve getter'da da **kopya döndürülür** (`List.copyOf(...)`). Böylece dış dünya iç duruma erişemez ve nesne gerçekten değişmez kalır.

📌 **Sık yapılan hata:** Alanı `private final` yapmayı immutability için yeterli sanmak. `final` yalnızca referansı bağlar, gösterdiği nesnenin içeriğini dondurmaz.

🔗 [3.2 §6 Immutability](3.2-metotlar-ve-kapsam.md)

## 3.3 Record'lar

### Soru 1 — `record Nokta(int x, int y) {}` derleyicinin ürettiği beş şeyi say.

**Kısa cevap:** **`private final` alanlar (`x`, `y`) · canonical constructor · bileşen adlı accessor'lar (`x()`, `y()`) · `equals`/`hashCode` · `toString`.**

**Ayrıntı:** Tek satırlık record bildirimi şunları üretir: her bileşen için `private final` alan; tüm bileşenleri alan `public Nokta(int x, int y)` canonical constructor; bileşen adının aynısı olan accessor'lar (`x()`, `y()` — `getX()` değil); tüm bileşenlere göre değer eşitliği yapan `equals` ve uyumlu `hashCode`; ve `Nokta[x=1, y=2]` biçiminde `toString`.

📌 **Sık yapılan hata:** Accessor'ın `getX()` olacağını sanmak. Record accessor'ı bileşenle aynı addır: `x()`.

🔗 [3.3 §1 Derleyici ne üretir](3.3-recordlar.md)

### Soru 2 — Compact constructor'da `this.x = x;` neden derlenmez, doğrusu nedir?

**Kısa cevap:** **Compact constructor'da alana atama yasaktır; yalnızca parametre değişkenini değiştirirsin — doğrusu `x = ...;` (ör. `x = Math.max(x, 0);`). Alanları blok sonunda derleyici otomatik atar.**

**Ayrıntı:** Compact constructor parantezsizdir (`public Nokta {`) ve içinde yalnızca doğrulama + parametre değişkenlerinin normalleştirmesi yapılır. `this.x = ...` yazmak derleme hatasıdır; bunun yerine parametreye atarsın (`x = x * 2;`). Blok bittiğinde derleyici parametrelerin son değerlerini alanlara **otomatik** kopyalar. Alanlara açık atama sadece **canonical** constructor'ın tam parametre listesiyle yazılmış biçiminde zorunludur.

📌 **Sık yapılan hata:** Compact constructor'da alanı `this.x = ...` ile atamaya çalışmak. Yalnızca parametreye atanır; alan atamasını derleyici yapar.

🔗 [3.3 §3 Constructor biçimleri](3.3-recordlar.md)

### Soru 3 — Bir record'a ek constructor yazarken ilk deyim ne olmalı?

**Kısa cevap:** **İlk deyim `this(...)` ile canonical constructor'a delege etmek olmalı.**

**Ayrıntı:** Ek (overloaded) constructor doğrudan alanlara atayamaz (`this.x = ...` yazamaz); ilk deyim olarak `this(...)` çağrısıyla canonical constructor'ı çalıştırmak zorundadır. Böylece tüm alan atamaları tek noktada, canonical constructor'da yapılır. Örn. `public Aralik(int ust) { this(0, ust); }`.

📌 **Sık yapılan hata:** Ek constructor'da alanları doğrudan atamaya çalışmak. Canonical'a delege şarttır.

🔗 [3.3 §3 Ek constructor](3.3-recordlar.md)

### Soru 4 — Record'a örnek alan ekleyebilir misin? Statik alan?

**Kısa cevap:** **Örnek alan ekleyemezsin (derleme hatası); statik alan ekleyebilirsin.**

**Ayrıntı:** Record'un tüm örnek alanları yalnızca bileşenlerden gelir; ek bir `private int z;` gibi örnek alan eklemek yasaktır. Örnek initializer bloğu (`{ ... }`) de yasaktır. Buna karşın **statik** alan, statik metot, statik initializer ve örnek metot eklemek serbesttir. Ayrıca record `implements` edebilir ama `extends` edemez (implicitly final).

📌 **Sık yapılan hata:** Örnek alan ile statik alanı aynı kefeye koymak. Örnek alan yasak, statik alan serbesttir.

🔗 [3.3 §2 Yasaklar ve serbestlikler](3.3-recordlar.md)

### Soru 5 — `record Kutu(List<String> l) {}` neden tam immutable değil, nasıl düzeltirsin?

**Kısa cevap:** **Record shallow (yüzeysel) immutable'dır; bileşen değiştirilebilir bir `List` olduğundan dışarıdaki liste sonradan değiştirilince record da değişir. Düzeltme: compact constructor'da savunmacı kopya al — `Kutu { l = List.copyOf(l); }`.**

**Ayrıntı:** Record alanları `final`'dır ama bu yalnızca referansı sabitler; gösterilen `List`'in içeriği dışarıdan değiştirilebilir. Örneğin record'a verilen listeye sonradan `add` yapılırsa record'un `toString`'i de değişir. Gerçek immutability için compact constructor'da `l = List.copyOf(l);` ile değiştirilemez bir kopya alınır.

📌 **Sık yapılan hata:** Record'un otomatik olarak tümüyle immutable olduğunu sanmak. Değiştirilebilir bileşenler için savunmacı kopya gerekir.

🔗 [3.3 §5 equals, hashCode ve immutability sınırı](3.3-recordlar.md)

### Soru 6 — Record neden `extends` edemez?

**Kısa cevap:** **Record implicitly `final`'dır ve zaten `java.lang.Record`'u genişletir; Java'da tek kalıtım olduğu için başka bir sınıfı `extends` edemez.**

**Ayrıntı:** Her record örtük olarak `java.lang.Record` sınıfını genişletir ve implicitly final olduğundan kendisi de genişletilemez. Bir sınıf yalnızca tek bir üst sınıftan türeyebildiği için record ayrıca başka bir sınıfı `extends` edemez. Ancak arayüz `implements` etmek serbesttir.

📌 **Sık yapılan hata:** Record'un normal bir sınıf gibi `extends` edebileceğini sanmak. Üst sınıfı sabittir (`Record`), yalnızca `implements` yapabilir.

🔗 [3.3 §2 Yasaklar ve serbestlikler](3.3-recordlar.md)

## 3.4 Kalıtım, Override, Polimorfizm ve Cast

### Soru 1 — `Ust u = new Alt();` için alan, statik metot ve örnek metot erişiminde hangi sürüm çalışır?

**Kısa cevap:** **Alan → üst sınıfın (referans tipi) · statik metot → üst sınıfın (referans tipi) · örnek metot → alt sınıfın (nesne tipi).**

**Ayrıntı:** Tek cümlelik kural: **metotlar nesneye, alanlar ve statik metotlar referansa bağlıdır.** `u.ad` referans tipi `Ust` olduğu için üst sınıfın alanını verir (alanlar override edilmez, gizlenir/hiding). `u.s()` statik metot da referans tipine göre çözülür → üst sınıfın statik sürümü (hiding). `u.i()` örnek metot ise dinamik bağlama ile nesnenin gerçek tipi `Alt`'a göre çalışır → alt sınıf sürümü.

📌 **Sık yapılan hata:** Alanların da metotlar gibi dinamik bağlanacağını sanmak. Alan ve statik metot referans tipine, yalnızca örnek metot nesne tipine bağlıdır.

🔗 [3.4 §3 Override mi, hiding mi, overload mu](3.4-kalitim-ve-polimorfizm.md)

### Soru 2 — Override'ın beş şartını say.

**Kısa cevap:** **(1) Aynı imza · (2) dönüş tipi aynı veya alt tip (covariant) · (3) erişim daraltılamaz · (4) checked exception genişletilemez · (5) üst metot `final`/`static`/`private` olmamalı.**

**Ayrıntı:** İmza (ad + parametre tipleri) farklıysa override değil overload olur. Dönüş tipi covariant olmalı — üst tip döndürmek derlenmez. Erişim daraltılamaz (`public → protected` hata); ama genişletilebilir. Alt metot yeni/daha geniş **checked** exception fırlatamaz (unchecked serbest). Üst metot `final`/`static`/`private` ise override edilemez (statikte hiding, private'ta yeni metot olur).

📌 **Sık yapılan hata:** `@Override` annotation'ının override için zorunlu olduğunu sanmak. Annotation isteğe bağlıdır; override imzayla belirlenir, annotation sadece derleme zamanında denetletir.

🔗 [3.4 §2 Override kuralları](3.4-kalitim-ve-polimorfizm.md)

### Soru 3 — Alt sınıfta dönüş tipini `Object`'ten `String`'e değiştirmek geçerli mi? Tersi?

**Kısa cevap:** **`Object → String` geçerli (covariant, `String` alt tip); tersi (`String → Object`) geçersiz (derleme hatası).**

**Ayrıntı:** Override'da dönüş tipi ancak aynı ya da **alt tip** olabilir (covariant return). Üst sınıf `Object` döndürüyorsa alt sınıfın `String` döndürmesi geçerlidir çünkü `String`, `Object`'in alt tipidir. Ama üst sınıf `String` döndürüyorken alt sınıfın `Object` (üst tip) döndürmesi kuralı ihlal eder ve derlenmez.

📌 **Sık yapılan hata:** Covariant kuralın her iki yönde çalıştığını sanmak. Yalnızca daha dar (alt) tipe daraltmak serbesttir; genişletmek yasaktır.

🔗 [3.4 §2 Override kuralları (covariant)](3.4-kalitim-ve-polimorfizm.md)

### Soru 4 — Alt sınıf yeni bir checked exception fırlatabilir mi? Unchecked?

**Kısa cevap:** **Yeni/daha geniş checked exception fırlatamaz (derleme hatası); unchecked (runtime) exception serbesttir.**

**Ayrıntı:** Override eden metot, üst metodun bildirdiğinden daha geniş veya yeni bir **checked** exception fırlatamaz — aynısını, alt tipini veya daha azını fırlatabilir. **Unchecked** (RuntimeException türevleri) için böyle bir kısıt yoktur; alt sınıf istediği unchecked exception'ı fırlatabilir.

📌 **Sık yapılan hata:** Kısıtın tüm exception'ları kapsadığını sanmak. Kural yalnızca checked exception'lar içindir; unchecked serbesttir.

🔗 [3.4 §2 Override kuralları (exception)](3.4-kalitim-ve-polimorfizm.md)

### Soru 5 — `equals(Nokta n)` yazmanın somut zararı ne?

**Kısa cevap:** **Bu `Object.equals`'ı override etmez, overload eder; `HashSet`/`HashMap` `equals(Object)` sürümünü çağırdığı için beklediğin değer eşitliği çalışmaz ve aynı değerli nesneler ayrı elemanlar olur.**

**Ayrıntı:** `equals`'ın imzası tam olarak `equals(Object)` olmalıdır. `equals(Nokta n)` parametresi `Nokta` olduğu için farklı imzalıdır → override değil overload. Koleksiyonlar (`HashSet`, `HashMap`) elemanları `Object` referansı üzerinden karşılaştırdığından senin overload'un değil, kalıtımdan gelen `Object.equals` (referans karşılaştırması) çalışır; sonuç olarak eşit değerli iki nesne kümede ayrı ayrı tutulur. `@Override` yazmış olsaydın derleyici hatayı yakalardı.

📌 **Sık yapılan hata:** Parametreyi somut tip yaparak `equals`'ı doğru override ettiğini sanmak. Parametre `Object` olmalı; aksi hâlde sadece overload olur.

🔗 [3.4 §5 Object metotları (equals/hashCode)](3.4-kalitim-ve-polimorfizm.md)

### Soru 6 — Hangi durumda cast derleme hatası, hangi durumda `ClassCastException` verir?

**Kısa cevap:** **Tipler hiç ilişkili değilse derleme hatası (`inconvertible types`); tipler ilişkili ama nesne gerçekte hedef tip değilse çalışma zamanında `ClassCastException`.**

**Ayrıntı:** Birbiriyle hiçbir kalıtım/arayüz ilişkisi olmayan tipler arasında cast (`(Integer) "abc"`) derleyici tarafından reddedilir. İlişki varsa (örneğin `Object o = "metin"; (Integer) o`) cast derlenir ama nesnenin gerçek tipi hedefe uymadığından çalışma zamanında `ClassCastException` atılır. `null` cast'i her zaman geçerlidir ve exception atmaz; `null instanceof X` ise her zaman `false` döner.

📌 **Sık yapılan hata:** İlişkili tiplerdeki hatalı cast'i de derleme hatası sanmak. İlişki varsa derlenir, uyumsuzluk runtime'da `ClassCastException` olur.

🔗 [3.4 §4 Polimorfizm ve cast](3.4-kalitim-ve-polimorfizm.md)

## 3.5 Abstract sınıflar ve sealed tipler

### Soru 1 — Abstract sınıfın constructor'ı olabilir mi, ne zaman çalışır?

**Kısa cevap:** **Evet, olabilir.** `new` ile üretilemese de alt sınıf onu `super(...)` ile çağırır ve o an çalışır.

**Ayrıntı:** Abstract sınıf doğrudan `new` edilemez, ama constructor tanımlamak serbesttir; hatta alan, statik üye ve initializer da bulundurabilir. Somut bir alt sınıf üretildiğinde alt sınıfın constructor'ı `super(...)` üzerinden abstract sınıfın constructor'ını çalıştırır. Kalıp örneğinde `class B extends A {}` için `new B()` yapıldığında `A`'nın constructor'ı çalışır ve `A` yazdırılır.

📌 **Sık yapılan hata:** "Abstract sınıf `new` edilemiyorsa constructor'ı da olamaz" sanmak. Constructor vardır; sadece alt sınıf üzerinden çalışır.

🔗 [3.5 §1 Abstract sınıflar](3.5-abstract-ve-sealed.md)

### Soru 2 — `abstract void m() { }` neden derlenmez?

**Kısa cevap:** **Abstract metodun gövdesi olamaz;** boş `{ }` bile derleme hatasıdır.

**Ayrıntı:** Abstract metot yalnızca imza bildirir ve `;` ile biter; gövde yazılamaz (*abstract methods cannot have a body*). Boş süslü parantez bile bir gövde sayıldığı için hata verir. Ayrıca abstract metot `private`, `static` veya `final` de olamaz — çünkü uygulanabilir/polimorfik olması gerekir.

📌 **Sık yapılan hata:** Boş `{ }`'yi "gövde yok" sanmak. Boş gövde de gövdedir; abstract metot noktalı virgülle bitmelidir.

🔗 [3.5 §1 Abstract sınıflar](3.5-abstract-ve-sealed.md)

### Soru 3 — Bir `sealed` tipin alt tipi hangi üç belirteçten birini almak zorunda? Hiçbirini yazmazsan?

**Kısa cevap:** **`final`, `sealed` veya `non-sealed`.** Hiçbirini yazmazsan **derlenmez**.

**Ayrıntı:** İzin verilen her alt tip zinciri nasıl kapattığını bildirmek zorundadır: `final` zinciri burada bitirir, `sealed` kontrollü (kendi `permits`'iyle) devam ettirir, `non-sealed` zinciri serbestçe yeniden açar. Üçünden hiçbiri yazılmazsa *sealed, non-sealed or final modifiers expected* hatası alınır. Not: `record` alt tip zaten implicitly final olduğu için ek belirteç istemez.

📌 **Sık yapılan hata:** Alt tipe belirteç yazmayı unutmak. `sealed` hiyerarşide belirteçsiz alt tip her zaman derleme hatasıdır (record hariç, o zaten final).

🔗 [3.5 §2 Sealed tipler](3.5-abstract-ve-sealed.md)

### Soru 4 — `permits` ne zaman yazılmak zorunda değildir?

**Kısa cevap:** **Alt tipler `sealed` tiple aynı dosyadaysa** `permits` yazılmayabilir.

**Ayrıntı:** Alt tipler aynı kaynak dosyada tanımlanmışsa derleyici izin verilenleri kendisi görebildiği için `permits` listesini yazmak zorunlu değildir. Alt tipler ayrı dosyalardaysa `permits` **zorunludur**. Her hâlde izin verilen alt tipler aynı pakette (adsız modülde) ya da aynı modülde olmak zorundadır.

📌 **Sık yapılan hata:** `permits`'i her zaman zorunlu sanmak. Aynı dosyada opsiyoneldir; ayrı dosyada şarttır.

🔗 [3.5 §2 Sealed tipler](3.5-abstract-ve-sealed.md)

### Soru 5 — Sealed hiyerarşide `switch` yazarken neden `default` koymamalısın?

**Kısa cevap:** **Hiyerarşiye yeni alt tip eklenince `switch`'in derlenmemesini (uyarı vermesini) istediğin için.** `default` bu güvenceyi yok eder.

**Ayrıntı:** Derleyici sealed tipin tüm alt tiplerini bildiği için `switch` exhaustive sayılır ve `default` gerekmez. `default` koyarsan yeni eklenen alt tip sessizce oraya düşer ve derleyici seni uyaramaz — yani sessizce yanlış çalışır. `default` yazmamak, eksik dalı derleme zamanında yakalatan bilinçli bir tercihtir.

📌 **Sık yapılan hata:** Güvenli olsun diye `default` eklemek. Sealed'da tersine, `default` yeni alt tip uyarısını bastırdığı için risklidir.

🔗 [3.5 §3 Exhaustive switch](3.5-abstract-ve-sealed.md)

### Soru 6 — `record` bir sealed arayüzü uygularken neden ek belirteç gerekmez?

**Kısa cevap:** **`record` zaten implicitly final olduğu için** `final`/`sealed`/`non-sealed` yazmaya gerek yoktur.

**Ayrıntı:** Sealed kuralı her alt tipin zinciri nasıl kapattığını bildirmesini ister; `record` örtük olarak `final` olduğundan zincir onda zaten biter. Bu yüzden `record A() implements S {}` ek belirteç olmadan derlenir. Aynı gereklilik normal sınıflar için geçerlidir: onlarda üç belirteçten biri açıkça yazılmalıdır.

📌 **Sık yapılan hata:** Record alt tipe de `final` yazmayı zorunlu sanmak veya belirteç yok diye derlenmez sanmak. Record kendiliğinden final olduğu için belirteç gereksizdir.

🔗 [3.5 §2 Sealed tipler](3.5-abstract-ve-sealed.md)

## 3.6 Arayüzler ve fonksiyonel arayüzler

### Soru 1 — Arayüzdeki bir alanın örtük belirteçleri nedir, değer atamazsan ne olur?

**Kısa cevap:** **Örtük `public static final`.** Değer atamazsan **derlenmez**.

**Ayrıntı:** Arayüzdeki her alan otomatik `public static final` olur; yani bir sabittir. `final` olduğu için bildirimde değer atanması zorunludur. `int SABIT2;` gibi değersiz bir alan derlenmez. Buna karşılık `int SABIT = 5;` aslında `public static final int SABIT = 5;` demektir.

📌 **Sık yapılan hata:** Arayüz alanını normal örnek alanı gibi değersiz bırakmak. Sabittir, değer şarttır.

🔗 [3.6 §1 Örtük belirteçler](3.6-arayuzler.md)

### Soru 2 — Arayüzün `static` metodunu uygulayan sınıf üzerinden çağırabilir misin?

**Kısa cevap:** **Hayır.** Arayüzün `static` metodu kalıtılmaz; yalnızca `Arayuz.metot()` ile çağrılır.

**Ayrıntı:** Sınıf kalıtımının aksine arayüzlerin statik metotları uygulayan sınıfa geçmez. `Arac.bos()` geçerlidir ama `Araba.bos()` ya da `new Araba().bos()` **derlenmez**. Bu, sınıf statiklerinin kalıtıldığı davranıştan ayrılan, sınavın özellikle sorduğu bir noktadır.

📌 **Sık yapılan hata:** Statik arayüz metodunu uygulayan sınıf adıyla çağırmak. Yalnızca arayüzün kendi adı geçerlidir.

🔗 [3.6 §2 Dört metot tipi](3.6-arayuzler.md)

### Soru 3 — Aynı `default` metodu iki arayüzden gelirse ne yapman gerekir, sözdizimi nedir?

**Kısa cevap:** **Sınıf metodu override etmek zorundadır;** istediğini `A.super.m()` ile seçersin.

**Ayrıntı:** İki bağımsız arayüz aynı imzada `default` metot verirse sınıf bunları çözemez ve *inherits unrelated defaults* hatasıyla derlenmez. Sınıf `m()`'i override edip içinde `A.super.m()` yazarak hangi arayüzün sürümünü istediğini belirtmelidir. `A.super.m()` sözdizimi yalnızca **doğrudan uygulanan** arayüzler için geçerlidir.

📌 **Sık yapılan hata:** Çakışmayı görmezden gelmek ya da `A.m()` yazmak. Çözüm override + `A.super.m()`'dir.

🔗 [3.6 §3 Çakışma kuralları](3.6-arayuzler.md)

### Soru 4 — Sınıf metodu ile arayüz `default`'u çakışırsa hangisi kazanır?

**Kısa cevap:** **Sınıf metodu kazanır.** Sınıf her zaman arayüz `default`'unu yener.

**Ayrıntı:** Bir sınıf (üst sınıf dahil) somut bir metot sağlıyorsa ve uygulanan arayüz aynı imzada `default` veriyorsa, çağrıda sınıfın metodu çalışır. `class Alt extends Ust implements I` örneğinde `Ust`'un `m()`'i çalışır ve `"Ust"` yazılır. "Sınıf, arayüzü yener" kuralıdır.

📌 **Sık yapılan hata:** Arayüz `default`'unun daha yeni/spesifik olduğunu düşünmek. Sınıf-arayüz çakışmasında her zaman sınıf önceliklidir.

🔗 [3.6 §3 Çakışma kuralları](3.6-arayuzler.md)

### Soru 5 — `Comparator` neden iki abstract metot bildirmesine rağmen fonksiyonel arayüzdür?

**Kısa cevap:** **`equals` `Object`'ten geldiği için sayılmaz;** geriye tek abstract metot (`compare`) kalır.

**Ayrıntı:** Fonksiyonel arayüz "tam olarak bir abstract metot" ister; ancak `Object`'in public metotlarının (`equals`, `hashCode`, `toString`) yeniden bildirimi bu sayıma katılmaz. `Comparator` `compare` ve `equals` bildirir gibi görünse de `equals` sayılmadığından geriye tek soyut metot kalır ve arayüz lambda hedefi olabilir.

📌 **Sık yapılan hata:** `Object` metot bildirimlerini de soyut metot saymak. Bunlar SAM sayımına girmez.

🔗 [3.6 §4 Fonksiyonel arayüz](3.6-arayuzler.md)

### Soru 6 — Uygulayan sınıfta metodu `public` yazmayı unutursan ne olur, neden?

**Kısa cevap:** **Derlenmez.** Arayüz metodu örtük `public`'tir; sınıf onu daha dar erişimle uygulayamaz.

**Ayrıntı:** Arayüzdeki abstract metot `public abstract`'tır. Uygulayan sınıfta metodu `public` belirtmeden (paket-özel) yazmak erişimi daraltmak demektir ve "override sırasında erişim daraltılamaz" kuralını ihlal eder — derleme hatası. `class C implements I { void m() {} }` bu yüzden derlenmez.

📌 **Sık yapılan hata:** Uygulayan metotta `public` yazmayı unutmak. Erişim daraltılamayacağı için zorunludur.

🔗 [3.6 §1 Örtük belirteçler](3.6-arayuzler.md)

## 3.7 Enum'lar

### Soru 1 — Üç sabiti olan bir enum'un constructor'ı kaç kez ve ne zaman çalışır?

**Kısa cevap:** **Üç kez** — sınıf ilk yüklendiğinde, **her sabit için bir kez**.

**Ayrıntı:** Enum constructor'ı örtük `private`'tır ve enum sınıfı ilk kez yüklendiğinde tüm sabitler oluşturulur. Üç sabit varsa constructor üç kez çalışır; bu, `main` içinde tek bir sabit kullanılsa bile böyledir. Constructor'a bir `println` konursa üç satır yazılır ve bu çıktı ilk enum kullanımından önce (yükleme anında) üretilir.

📌 **Sık yapılan hata:** Constructor'ın yalnızca kullanılan sabit için çalıştığını sanmak. Sınıf yüklenirken tüm sabitler için çalışır.

🔗 [3.7 §2 Alan, constructor ve metot](3.7-enumlar.md)

### Soru 2 — Sabit listesinden sonra noktalı virgül ne zaman zorunludur?

**Kısa cevap:** **Sabitlerden sonra başka üye (alan, constructor, metot) varsa** noktalı virgül zorunludur.

**Ayrıntı:** Enum yalnızca sabitlerden oluşuyorsa `;` gerekmez. Ama sabit listesinin ardından alan, constructor ya da metot geliyorsa sabit listesi `;` ile kapatılmalıdır. `enum E { A, B  void m() {} }` gibi `;` unutulan bir enum derlenmez. Ayrıca sabitler gövdenin **ilk** elemanı olmak zorundadır.

📌 **Sık yapılan hata:** Metot eklerken sabit listesinden sonra `;` koymayı unutmak. Ek üye varsa noktalı virgül şarttır.

🔗 [3.7 §2 Alan, constructor ve metot](3.7-enumlar.md)

### Soru 3 — `valueOf` bulamazsa ne döner?

**Kısa cevap:** **Hiçbir şey dönmez — `IllegalArgumentException` atar.** `null` dönmez.

**Ayrıntı:** `valueOf(String)` verilen ada tam olarak (büyük/küçük harf duyarlı) uyan sabiti döndürür; eşleşme yoksa `IllegalArgumentException` fırlatır. `Renk.valueOf("mavi")` ve `Renk.valueOf("MOR")` bu istisnayı atar, çünkü ilkinin harf durumu yanlış, ikincisi hiç yok. Sabit `MAVI` ise doğru yazımla `Renk.valueOf("MAVI")` çalışır.

📌 **Sık yapılan hata:** `valueOf`'un bulamayınca `null` döndüğünü sanmak. Exception atar; ayrıca harf durumu birebir uymalıdır.

🔗 [3.7 §1 Temel yapı ve hazır metotlar](3.7-enumlar.md)

### Soru 4 — `ordinal()` kullanmanın riski nedir?

**Kısa cevap:** **Değeri bildirim sırasına bağlıdır;** sabitleri yeniden sıralayınca değişir, bu yüzden kırılgandır.

**Ayrıntı:** `ordinal()` sabitin 0 tabanlı bildirim sırasını verir. Kodda sabitlerin sırası değiştirilirse aynı sabitin ordinal değeri değişir. Bu değeri kalıcı bir yere (örneğin veritabanına) yazmak klasik bir hatadır: sıra değişince eski kayıtlar yanlış sabite karşılık gelir. Sınav bunu "hangi ifade kırılgandır" biçiminde sorar.

📌 **Sık yapılan hata:** `ordinal()`'ı sabit/kalıcı bir kimlik gibi kullanmak. Bildirim sırasına bağlı olduğu için güvenilmezdir.

🔗 [3.7 §1 Temel yapı ve hazır metotlar](3.7-enumlar.md)

### Soru 5 — Enum'a `abstract` metot koyarsan her sabit ne yapmak zorundadır?

**Kısa cevap:** **Her sabit o metoda kendi gövdesini vermek (sabit başına gövde) zorundadır.**

**Ayrıntı:** Enum'da `public abstract int uygula(...)` gibi bir soyut metot bildirirsen, her sabit kendi süslü parantezli gövdesiyle bu metodu uygulamak zorundadır (`TOPLA { public int uygula(...) {...} }`). Gövdeli sabitler aslında anonim alt sınıflardır; `TOPLA.getClass()` `Islem$1` döner. Enum'un kendisi `abstract` olamaz ama bu mekanizma aynı etkiyi verir.

📌 **Sık yapılan hata:** Soyut metodu bildirip bazı sabitlere gövde vermemek. Abstract metot varsa istisnasız her sabit uygulamalıdır.

🔗 [3.7 §3 Sabit başına gövde](3.7-enumlar.md)

### Soru 6 — `EnumSet` nasıl üretilir, neden `new` kullanılmaz?

**Kısa cevap:** **Fabrika metotlarıyla** üretilir (`of`, `allOf`, `noneOf`, `range`, `complementOf`); public constructor'ı yoktur, `new EnumSet<>()` **derlenmez**.

**Ayrıntı:** `EnumSet` bit vektörü tabanlı, enum'a özel hızlı bir koleksiyondur ve `new` ile örneklenmez; `EnumSet.allOf(Renk.class)` gibi statik fabrika metotları kullanılır. Buna karşılık `EnumMap`'in constructor'ı vardır ama bir `Class` nesnesi ister: `new EnumMap<>(Renk.class)`. İkisi de `ordinal` sırasında dolaşır ve `null` anahtar kabul etmez.

📌 **Sık yapılan hata:** `new EnumSet<>()` yazmak. Constructor yoktur; fabrika metodu kullanılır (`EnumMap` ise `new` + `Class` ister).

🔗 [3.7 §5 EnumSet ve EnumMap](3.7-enumlar.md)

## 3.8 İç sınıflar

### Soru 1 — `static nested` ile `inner` arasındaki tek temel fark nedir, sözdizimine nasıl yansır?

**Kısa cevap:** **Inner dış sınıfın örneğine bağlıdır, static nested değildir.** Bu fark üretim sözdizimini değiştirir: `new Dis.Nested()` vs `dis.new Inner()`.

**Ayrıntı:** Static nested sınıf dış örneğe referans taşımaz; dış örnek olmadan `new Dis.Nested()` ile üretilir ve yalnızca dış sınıfın statik üyelerine erişir. Inner sınıf ise dış örneğe gizli bir referans taşır; üretmek için önce bir dış örnek gerekir: `Dis d = new Dis(); d.new Inner();`. Inner, dış sınıfın private dahil her üyesine erişir.

📌 **Sık yapılan hata:** İki türü aynı sözdizimiyle üretmeye çalışmak. Static nested dış örnek istemez, inner ister.

🔗 [3.8 §2 static nested vs inner](3.8-ic-siniflar.md)

### Soru 2 — `new Dis.Inner()` neden derlenmez, doğrusu ne?

**Kısa cevap:** **Inner sınıf dış örnek gerektirir;** doğrusu `dis.new Inner()` (veya `new Dis().new Inner()`).

**Ayrıntı:** Inner sınıf static olmadığı için üretiminde mevcut bir dış örneğe bağlanmalıdır. Bu yüzden `new` anahtar kelimesi noktadan **sonra** gelir: `d.new Inner()`. `new Dis.Inner()` yazımı derlenmez, çünkü dış örnek belirtilmemiştir. Sınavın çok sorduğu bir sözdizimi tuzağıdır.

📌 **Sık yapılan hata:** `new Dis.Inner()` yazmak (static nested'la karıştırmak). Inner'da `new` noktadan sonra gelir.

🔗 [3.8 §2 static nested vs inner](3.8-ic-siniflar.md)

### Soru 3 — `Dis.this.x` ile `this.x` arasındaki fark ne?

**Kısa cevap:** **`this.x` iç sınıfın alanı, `Dis.this.x` dış sınıfın alanıdır.**

**Ayrıntı:** İç sınıf içinde aynı adlı alan gölgelemesi olduğunda `this.x` içinde bulunulan iç sınıfın alanını, `Dis.this.x` ise çevreleyen dış sınıfın alanını gösterir. Metot içinde ayrıca yerel bir `x` varsa, niteliksiz `x` yerel değişkeni ifade eder. Örnekteki çıktı sırasıyla yerel (3), iç sınıf (2), dış sınıf (1) olur.

📌 **Sık yapılan hata:** `this.x`'in dış sınıfın alanına eriştiğini sanmak. Dış alan için `Dis.this.x` gerekir.

🔗 [3.8 §2 Gölgeleme ve Dis.this](3.8-ic-siniflar.md)

### Soru 4 — Local sınıf hangi belirteçleri alamaz?

**Kısa cevap:** **Erişim belirteçlerini** (`public`/`private`/`protected`) alamaz; ayrıca `static` bir sınıf olarak işaretlenemez.

**Ayrıntı:** Local sınıf bir metot (blok) içinde bildirilir ve kapsamı o bloktur; bu yüzden erişim belirteci alamaz. `final` ve `abstract` yazılabilir. Yakaladığı yerel değişkenler effectively final olmak zorundadır. Java 16'dan beri local sınıf `static` **üye** bildirebilir (bu, sınıfın kendisinin static olması demek değildir).

📌 **Sık yapılan hata:** Local sınıfa `public`/`private` yazmak. Erişim belirteci alamaz.

🔗 [3.8 §3 Local sınıflar](3.8-ic-siniflar.md)

### Soru 5 — Anonim sınıfın constructor'ı neden olamaz, üst sınıfa argümanı nasıl verirsin?

**Kısa cevap:** **Adı olmadığı için constructor tanımlanamaz;** üst sınıf argümanları `new Tip(arg) { ... }` sözdizimiyle verilir.

**Ayrıntı:** Anonim sınıfın adı yoktur (`Dis$1`, `Dis$2`), constructor bir sınıf adı gerektirdiği için tanımlanamaz. Üst sınıfın constructor argümanları `new` çağrısında parantez içinde verilir; kurulum kodu için initializer bloğu `{ ... }` kullanılır. Anonim sınıf tam olarak bir tipi (sınıf **veya** arayüz) genişletir/uygular ve bir ifade olduğu için sonuna `;` konur.

📌 **Sık yapılan hata:** Anonim sınıfa constructor yazmaya çalışmak veya sonundaki `;`'yi unutmak. Constructor yoktur; argüman `new Tip(arg){}` ile verilir.

🔗 [3.8 §4 Anonim sınıflar](3.8-ic-siniflar.md)

### Soru 6 — Anonim sınıf ile lambda arasında `this` açısından ne fark var?

**Kısa cevap:** **Anonim sınıfta `this` anonim sınıfın kendisini, lambda'da `this` çevreleyen sınıfı gösterir.**

**Ayrıntı:** Anonim sınıf gerçek bir sınıf olduğu için içindeki `this` o anonim sınıf örneğini işaret eder. Lambda ise yeni bir kapsam yaratmaz; içindeki `this` lambda'yı çevreleyen sınıfın örneğidir. Bu, lambda'nın anonim sınıfın kısaltması **olmadığını** kanıtlayan en önemli davranış farkıdır (pratikte farklı sınıf adları yazdırarak görülür).

📌 **Sık yapılan hata:** Lambda'yı anonim sınıfın birebir kısayolu sanmak. `this` bağlamları farklıdır.

🔗 [3.8 §4 Anonim sınıflar](3.8-ic-siniflar.md)
