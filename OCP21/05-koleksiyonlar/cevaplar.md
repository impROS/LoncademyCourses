# 05 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 5.1 Diziler ve Arrays API

### Soru 1 — `int[] a, b;` ile `int a[], b;` arasındaki fark nedir?

**Kısa cevap:** **`int[] a, b;`'de ikisi de `int[]`; `int a[], b;`'de `a` bir `int[]`, `b` ise `int`.**

**Ayrıntı:** Köşeli parantezin yeri belirleyici. Parantez **tipin yanındaysa** (`int[]`) bildirimdeki tüm değişkenlere uygulanır, yani hem `a` hem `b` dizi olur. Parantez **değişkenin yanındaysa** (`a[]`) yalnızca o değişkene bağlanır; `int a[], b;`'de sadece `a` dizi, `b` düz bir `int`'tir. Aynı mantıkla `int[] g[], h;` → `g` `int[][]`, `h` `int[]` olur.

📌 **Sık yapılan hata:** `int a[], b;`'de `b`'yi de dizi sanmak. Parantez değişkenin yanındayken yalnızca ona uygulanır.

🔗 [5.1 §1 Bildirim ve oluşturma](5.1-diziler.md)

### Soru 2 — `Arrays.binarySearch` sıralanmamış bir dizide ne yapar?

**Kısa cevap:** **Sonuç tanımsızdır (undefined); exception atmaz, sessizce anlamsız bir sayı döner.**

**Ayrıntı:** `binarySearch` çalışması için dizinin **sıralı** olmasını şart koşar. Dizi sıralı değilse metot patlamaz, istisna da fırlatmaz; ikili arama mantığı bozuk veri üzerinde çalıştığı için **anlamsız/yanlış** bir değer döndürür. Şıklarda "undefined result" görürsen doğru cevap odur.

📌 **Sık yapılan hata:** Sıralanmamış dizide exception beklemek. Davranış tanımsızdır, hata fırlatılmaz.

🔗 [5.1 §3 binarySearch tuzağı](5.1-diziler.md)

### Soru 3 — `Arrays.binarySearch({10,20,30}, 25)` neden `-3` döner? Formül nedir?

**Kısa cevap:** **Formül `-(ekleme noktası) - 1`. 25, indeks 2'ye eklenirdi → `-(2) - 1 = -3`.**

**Ayrıntı:** Eleman bulunamazsa `binarySearch` negatif bir değer döner: **`-(eklenecek yer) - 1`**. `{10,20,30}` içinde 25 bulunmaz; sıralı kalması için indeks 2'ye (30'dan önce) yerleştirilirdi. Yani `-(2) - 1 = -3`. Bu değer hem "bulunamadı"yı hem de nereye eklenmesi gerektiğini kodlar.

📌 **Sık yapılan hata:** Bulunamayınca `-1` döndüğünü sanmak. Dönüş `-1` değil, `-(ekleme noktası) - 1`'dir.

🔗 [5.1 §3 binarySearch tuzağı](5.1-diziler.md)

### Soru 4 — `Arrays.asList` ile `List.of` arasındaki üç farkı say.

**Kısa cevap:** **(1) `asList` `set` ile eleman değiştirmeye izin verir ve bunu diziye yansıtır; `List.of` hiç değiştirilemez. (2) `asList` `null` eleman kabul eder; `List.of` `null`'da NPE atar. (3) İkisi de sabit boyutludur ama `asList` diziyle bağlıdır.**

**Ayrıntı:** `Arrays.asList(a)` **sabit boyutlu** ve **diziyle bağlı** bir listedir: `set(i, v)` çalışır ve **alttaki diziyi de değiştirir**, ama `add`/`remove` → `UnsupportedOperationException`. `null` elemanı kabul eder. `List.of(...)` ise tümüyle **immutable**'dır: `set`, `add`, `remove` hepsi exception atar ve `null` eleman verilirse `NullPointerException` fırlatır.

📌 **Sık yapılan hata:** `asList`'i tamamen immutable sanmak. Boyutu sabittir ama `set` ile elemanları (ve altındaki diziyi) değiştirebilirsin.

🔗 [5.1 §3 `Arrays.asList` tuzağı](5.1-diziler.md)

### Soru 5 — `Object[] o = new String[2];` neden derlenir, ne zaman patlar?

**Kısa cevap:** **Diziler kovaryant olduğu için derlenir (`String[]` bir `Object[]`'tir); yanlış tipte bir eleman yazmaya çalışınca çalışma zamanında `ArrayStoreException` atar.**

**Ayrıntı:** Diziler **kovaryanttır**: `String[]`, `Object[]` yerine geçebilir, bu yüzden atama derleme zamanında geçer. Ancak gerçek dizi hâlâ bir `String[]`'tir. `o[0] = "metin"` sorunsuz; `o[1] = 42` (bir `Integer`) yazmaya çalışınca dizinin gerçek tipiyle uyuşmaz ve çalışma zamanında **`ArrayStoreException`** fırlar. Generics'te karşılığı yoktur: `List<Object> l = new ArrayList<String>();` **derlenmez**.

📌 **Sık yapılan hata:** Bunu derleme hatası sanmak. Kovaryans sayesinde derlenir; hata **çalışma zamanında** ortaya çıkar.

🔗 [5.1 §4 Kovaryans ve ArrayStoreException](5.1-diziler.md)

### Soru 6 — Çok boyutlu bir diziyi yazdırmak için hangi metot gerekir?

**Kısa cevap:** **`Arrays.deepToString(a)`.**

**Ayrıntı:** `Arrays.toString(a)` yalnızca tek boyutlu diziler içindir; çok boyutlu dizide iç diziler **ham** görünür (ör. `[I@1b6d3586`). İç içe dizileri okunur biçimde yazdırmak için **`Arrays.deepToString`** kullanılır. Aynı şekilde eşitlik karşılaştırması da çok boyutluda `equals` yerine **`deepEquals`** ile yapılır.

📌 **Sık yapılan hata:** Çok boyutlu diziye `Arrays.toString` ya da düz `toString` uygulamak. İç diziler ham adres olarak çıkar; `deepToString` gerekir.

🔗 [5.1 §3 Arrays yardımcı sınıfı](5.1-diziler.md)

## 5.2 List, Set, Map, Deque ve Generics

### Soru 1 — `List<Integer> l` üzerinde `l.remove(1)` ne yapar, değeri silmek için ne yazarsın?

**Kısa cevap:** **`remove(1)` `int` overload'una gider ve 1. indeksteki elemanı siler; değere göre silmek için `l.remove(Integer.valueOf(1))` (ya da `l.remove((Integer) 1)`) yazılır.**

**Ayrıntı:** `List` iki `remove` bildirir: `remove(int index)` ve `remove(Object o)`. `l.remove(1)` çağrısında `1` bir `int` literalidir; overload çözümlemesinde **tam eşleşme (int) kutulamayı yener**, bu yüzden `remove(int index)` seçilir ve indeks silinir. Değeri (nesneyi) silmek istersen argümanı açıkça `Integer`'a kutulaman gerekir: `Integer.valueOf(1)` veya `(Integer) 1`.

📌 **Sık yapılan hata:** `List<Integer>.remove(1)`'in 1 değerini sildiğini sanmak. Aslında 1. indeksi siler.

🔗 [5.2 §3 List.remove overload tuzağı](5.2-collections-api.md)

### Soru 2 — Hangi dört koleksiyon `null` kabul etmez?

**Kısa cevap:** **`TreeSet`, `TreeMap` (anahtar), `ArrayDeque` ve `PriorityQueue` — `null` verilince `NullPointerException`.**

**Ayrıntı:** Bu dört tip `null`'ı reddeder: `TreeSet` ve `TreeMap` anahtarı sıralama için karşılaştırma yapar (`null` karşılaştırılamaz), `ArrayDeque` ve `PriorityQueue` de `null` kabul etmez → hepsi **NPE** atar. Buna karşılık `HashSet` ve `HashMap` `null`'ı sorunsuz kabul eder (`HashSet` bir `null`, `HashMap` bir `null` anahtar ve çok sayıda `null` değer).

📌 **Sık yapılan hata:** Tüm `Set`/`Map`'lerin `null` aldığını sanmak. `Tree*`, `ArrayDeque` ve `PriorityQueue` atar; `Hash*` kabul eder.

🔗 [5.2 §1 Hiyerarşi ve uygulamalar](5.2-collections-api.md)

### Soru 3 — `Set.of("a","a")` ne olur? `new HashSet<>(List.of("a","a"))` ne olur?

**Kısa cevap:** **`Set.of("a","a")` → `IllegalArgumentException` (tekrarlı eleman); `new HashSet<>(...)` tekrarı sessizce yutar ve tek elemanlı bir küme kurar.**

**Ayrıntı:** Fabrika metodu `Set.of`, argümanlarda **tekrar** bulursa `IllegalArgumentException` fırlatır — immutable küme tekrarı hata sayar. `HashSet` yapıcısı ise küme semantiğine göre çalışır: aynı elemanı ikinci kez eklemeyi **sessizce yok sayar** ve sonuçta yalnızca `"a"` içeren bir küme oluşur, exception atmaz.

📌 **Sık yapılan hata:** İkisinin de aynı davrandığını sanmak. `Set.of` tekrarda patlar; `new HashSet<>` yutar.

🔗 [5.2 §2 Fabrika metotları](5.2-collections-api.md)

### Soru 4 — Boş bir kuyrukta `poll()` ile `remove()` arasındaki fark ne?

**Kısa cevap:** **Boş kuyrukta `poll()` → `null` döner; `remove()` → `NoSuchElementException` atar.**

**Ayrıntı:** Queue metotları iki aileye ayrılır. **Özel değer** dönenler `null`/`false` verir: `offer` → `false`, `poll` → `null`, `peek` → `null`. **Exception atanlar**: `add` → `IllegalStateException`, `remove()` → `NoSuchElementException`, `element()` → `NoSuchElementException`. Hafıza kancası: "`o` ile başlayanlar (`offer`, `poll`, `peek`) naziktir, exception atmaz."

📌 **Sık yapılan hata:** `poll()` ile `remove()`'u eş sanmak. Biri `null` döner, öteki boş kuyrukta patlar.

🔗 [5.2 §4 Queue / Deque metotları](5.2-collections-api.md)

### Soru 5 — `List<? extends Number>` listesine neden eleman ekleyemezsin?

**Kısa cevap:** **Gerçek tip `Number`'ın bilinmeyen bir alt tipi olabileceği için derleyici hangi tipin güvenli olduğunu bilemez; eklenebilen tek şey `null`'dır.**

**Ayrıntı:** `List<? extends Number>`, "`Number` ya da onun bir alt tipi olan bir liste" demektir — ama tam olarak hangisi bilinmez (`List<Integer>` de olabilir, `List<Double>` de). Derleyici, koyacağın elemanın altdaki gerçek tiple uyumlu olduğunu garanti edemeyeceği için `add`'i reddeder (yalnızca `null` eklenebilir). Bu, PECS kuralının **Producer Extends** yanıdır: `extends` ile açtığın koleksiyondan yalnızca **okursun**. Yazmak istersen `? super` kullanılır.

📌 **Sık yapılan hata:** `? extends Number` listesine `Integer`/`Double` eklenebileceğini sanmak. Okuma için `extends`, yazma için `super`.

🔗 [5.2 §5 Generics ve wildcard'lar](5.2-collections-api.md)

### Soru 6 — `map.put` ne döner, `merge` ne zaman anahtarı siler?

**Kısa cevap:** **`put` **eski değeri** döner (anahtar yoksa `null`); `merge`'te birleştirme fonksiyonu `null` dönerse anahtar silinir.**

**Ayrıntı:** `put(k, v)` anahtar zaten varsa **önceki değeri** döndürür, yoksa `null` döner (yeni değeri değil). `merge(k, v, fn)` ise: anahtar yoksa `v` yazar; varsa `fn(eskiDeğer, v)` sonucunu yazar — ancak bu fonksiyonun sonucu **`null`** ise anahtar map'ten **silinir**. (`computeIfPresent` de fonksiyon `null` dönerse anahtarı siler.)

📌 **Sık yapılan hata:** `put`'un yeni değeri döndürdüğünü sanmak. Eski değeri döner; `merge` ise `null` sonuçta anahtarı siler.

🔗 [5.2 §4 Map metotları](5.2-collections-api.md)

## 5.3 Sıralama ve Sequenced Collections

### Soru 1 — `comparing(A).thenComparing(B).reversed()` hangi sırayı üretir?

**Kısa cevap:** **Önce A'ya, eşitse B'ye göre sıralayan zincirin **tamamı** ters çevrilir.**

**Ayrıntı:** `reversed()` yalnızca son adımı değil, **kendinden önceki tüm zinciri** ters çevirir. Yani `comparing(A).thenComparing(B)` ile "önce A, eşitse B" ölçütü kurulur; `reversed()` bunun bütününü tersine döndürür. Bunu `comparing(A).reversed().thenComparing(B)` ile karıştırma: orada yalnızca A ölçütü ters, B düz kalır. Sınav bu iki zinciri yan yana koyup farklı sonuç bekler.

📌 **Sık yapılan hata:** `reversed()`'ün sadece son `thenComparing` adımını çevirdiğini sanmak. O ana kadarki zincirin hepsini çevirir.

🔗 [5.3 §2 Zincir sırası tuzağı](5.3-siralama-ve-sequenced.md)

### Soru 2 — `Comparable` uygulamayan bir tipi `TreeSet`'e eklersen ne olur?

**Kısa cevap:** **Çalışma zamanında `ClassCastException` atar.**

**Ayrıntı:** `TreeSet` elemanları sıralı tutar; bunu yapabilmek için elemanların ya `Comparable` uygulaması ya da kümenin bir `Comparator` ile kurulmuş olması gerekir. İkisi de yoksa, ekleme sırasında karşılaştırma yapılamaz ve **`ClassCastException`** (runtime) fırlar. Kod **derlenir** — hata çalışma zamanında ortaya çıkar.

📌 **Sık yapılan hata:** Bunu derleme hatası sanmak. Derlenir; `Comparable` eksikliği çalışma zamanında `ClassCastException` verir.

🔗 [5.3 §1 Comparable — doğal sıralama](5.3-siralama-ve-sequenced.md)

### Soru 3 — Java 21'de eklenen üç arayüz hangileri ve hangi koleksiyonlar bunları uygular?

**Kısa cevap:** **`SequencedCollection`, `SequencedSet`, `SequencedMap`. `List`, `Deque`, `LinkedHashSet`, `TreeSet`, `LinkedHashMap`, `TreeMap` uygular; `HashSet` ve `HashMap` uygulamaz.**

**Ayrıntı:** JEP 431 ile gelen üç arayüz: `SequencedCollection<E>` (Collection'ı genişletir), `SequencedSet<E>` (SequencedCollection + Set) ve `SequencedMap<K,V>` (Map'i genişletir). Uygulayanlar: `List` (`ArrayList`, `LinkedList`) ve `Deque` (`ArrayDeque`, `LinkedList`) → `SequencedCollection`; `LinkedHashSet` ve `TreeSet`/`SortedSet` → `SequencedSet`; `LinkedHashMap` ve `TreeMap`/`SortedMap` → `SequencedMap`. **`HashSet` ve `HashMap`** sıra kavramı olmadığı için uygulamaz.

📌 **Sık yapılan hata:** `HashSet`/`HashMap`'in de bu arayüzleri uyguladığını sanmak. Sırasız oldukları için uygulamazlar.

🔗 [5.3 §3 Sequenced Collections](5.3-siralama-ve-sequenced.md)

### Soru 4 — `HashMap` neden `SequencedMap` değildir?

**Kısa cevap:** **`HashMap`'in tanımlı bir eleman sırası yoktur; Sequenced arayüzleri baş/son kavramı gerektirir.**

**Ayrıntı:** `SequencedMap`, `firstEntry`/`lastEntry`, `putFirst`/`putLast`, `pollFirstEntry`/`pollLastEntry` gibi "baş" ve "son" uçlarını varsayan metotlar sunar. `HashMap` anahtarları hash düzenine göre tutar ve **sıra garantisi vermez**; dolayısıyla "ilk" veya "son" eleman anlamlı değildir, bu yüzden `SequencedMap` olamaz. Sıra isteyen kullanıcı `LinkedHashMap` (ekleme sırası) veya `TreeMap` (anahtara göre sıralı) seçer — ikisi de `SequencedMap`'tir.

📌 **Sık yapılan hata:** Her `Map`'in Sequenced olduğunu sanmak. Yalnızca sıra garantisi olan map'ler (`LinkedHashMap`, `TreeMap`) `SequencedMap`'tir.

🔗 [5.3 §3 Kim uyguluyor, kim uygulamıyor](5.3-siralama-ve-sequenced.md)

### Soru 5 — `TreeSet.addFirst("z")` ne yapar, neden?

**Kısa cevap:** **Çalışma zamanında `UnsupportedOperationException` atar; çünkü `TreeSet`'te sırayı karşılaştırıcı belirler, konumlu ekleme yapılamaz.**

**Ayrıntı:** `TreeSet` bir `SequencedSet`'tir, yani `getFirst`/`getLast`/`reversed` gibi metotları vardır. Ama sırası **karşılaştırma sonucu** (doğal sıralama veya `Comparator`) tarafından belirlenir; bir elemanı zorla başa/sona koyamazsın. Bu yüzden `addFirst`, `addLast` (ve `TreeMap`'te `putFirst`, `putLast`) → **`UnsupportedOperationException`**. Kod derlenir; hata çalışma zamanında çıkar.

📌 **Sık yapılan hata:** `Sequenced` olmayı "konumlu ekleme yapabilir" sanmak. `TreeSet`/`TreeMap` konumlu eklemeyi reddeder; sırayı sen dayatamazsın.

🔗 [5.3 §3 Sequenced Collections (tuzak)](5.3-siralama-ve-sequenced.md)

### Soru 6 — Boş bir listede `getFirst()` ile boş bir map'te `firstEntry()` arasındaki fark ne?

**Kısa cevap:** **Boş listede `getFirst()` → `NoSuchElementException` atar; boş map'te `firstEntry()` → `null` döner.**

**Ayrıntı:** `SequencedCollection.getFirst()`/`getLast()` koleksiyon boşsa **`NoSuchElementException`** fırlatır. Buna karşılık `SequencedMap.firstEntry()`/`lastEntry()` boş map'te exception atmaz, **`null`** döndürür. İki API'nin boş durum davranışı bilinçli olarak farklıdır ve sınav tam bu ayrımı sorar.

📌 **Sık yapılan hata:** İkisinin de boşta aynı davrandığını sanmak. `getFirst()` patlar, `firstEntry()` `null` döner.

🔗 [5.3 §3 SequencedMap metotları](5.3-siralama-ve-sequenced.md)
