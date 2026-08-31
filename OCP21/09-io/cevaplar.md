# 09 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 9.1 I/O stream'leri, konsol ve serialization

### Soru 1 — `FileReader` ile `FileInputStream` arasındaki fark nedir?

**Kısa cevap:** **`FileReader` karakter akışıdır (metin), `FileInputStream` byte akışıdır (ikili veri).**

**Ayrıntı:** I/O'da iki eksen vardır: byte mı karakter mi. `FileInputStream` `InputStream` ailesindendir ve byte okur — ikili (binary) veriler için uygundur. `FileReader` ise `Reader` ailesindendir ve karakter okur — metin için uygundur. İkisi de "düşük seviye" sınıflardır, yani doğrudan kaynağa (dosyaya) bağlanır.

📌 **Sık yapılan hata:** Metni byte akışıyla okumaya çalışmak. Metin için `Reader`/`Writer`, ikili veri için `InputStream`/`OutputStream` kullanılır.

🔗 [9.1 §1 Dört kutu: byte/karakter × düşük/yüksek](9.1-io-streams-ve-serialization.md)

### Soru 2 — `BufferedReader`'ı doğrudan dosya adıyla açabilir misin? Neden?

**Kısa cevap:** **Hayır — `new BufferedReader("a.txt")` derlenmez; `BufferedReader` bir `Reader` ister.**

**Ayrıntı:** `BufferedReader` yüksek seviye (sarmalayıcı) bir sınıftır; kaynağa doğrudan bağlanmaz, başka bir `Reader`'ı sarmalar. Bu yüzden dosyayı önce bir düşük seviye akışla (`FileReader`) açıp sonra sarmalarsın: `new BufferedReader(new FileReader("a.txt"))`. Zincirde kaynak içte, sarmalayıcı dıştadır; kapatırken en dıştakini kapatmak yeter, zincir kapanır.

📌 **Sık yapılan hata:** Yüksek seviye sınıfa doğrudan dosya adı vermek. Sarmalayıcı tek başına açılamaz, bir `Reader`/`InputStream` gerekir.

🔗 [9.1 §1 Dört kutu: byte/karakter × düşük/yüksek](9.1-io-streams-ve-serialization.md)

### Soru 3 — `read()` neden `int` döner?

**Kısa cevap:** **Dosya sonunu (`-1`) geçerli bir bayttan (örn. `0xFF`) ayırt edebilmek için `int` döner.**

**Ayrıntı:** `read()` bir byte/char okur ama dönüş tipi `int`'tir. Neden? Çünkü dosya sonunu belirtmek için `-1` kullanılır; eğer `byte` dönseydi geçerli `0xFF` değeri ile dosya sonu karışırdı. Bu yüzden `while ((c = r.read()) != -1)` kalıbı kullanılır. `read()` sonda `-1` döner; buna karşılık `BufferedReader.readLine()` sonda `null` döner.

📌 **Sık yapılan hata:** `read()`'in dönüşünü `char` ya da `byte`'a atamak ve `!= -1` ile karşılaştırmak (`char c; ... read() != -1`). Bu derlenmez — dönüş `int` olmalı.

🔗 [9.1 §1 Dört kutu: byte/karakter × düşük/yüksek](9.1-io-streams-ve-serialization.md)

### Soru 4 — `transient` ve `static` alanlar serileştirmede ne olur?

**Kısa cevap:** **İkisi de serileştirilmez; `transient` alan geri okunduğunda varsayılan değere (`0`, `null`, `false`) döner.**

**Ayrıntı:** `transient` alanlar bilerek serileştirme dışında bırakılır; nesne geri okunduğunda bu alanlar kaydedilmemiş olduğu için tip varsayılanına döner (`int` → `0`, referans → `null`, `boolean` → `false`). `static` alanlar da yazılmaz çünkü nesneye değil sınıfa aittir — geri okunduğunda değer dosyadan değil mevcut sınıftan gelir. Ayrıca tüm non-transient alanların da `Serializable` olması gerekir; değilse yazarken `NotSerializableException` atılır.

📌 **Sık yapılan hata:** `transient` bir alanın kaydettiğin değerle geri geleceğini sanmak. Kaydedilmez, varsayılan değere döner.

🔗 [9.1 §3 Serialization](9.1-io-streams-ve-serialization.md)

### Soru 5 — Deserialization sırasında hangi constructor çalışır, hangisi çalışmaz?

**Kısa cevap:** **Serileştirilen sınıfın kendi constructor'ı ve alan initializer'ları çalışmaz; ilk `Serializable` olmayan üst sınıfın parametresiz constructor'ı çalışır.**

**Ayrıntı:** Nesne geri okunurken alan değerleri dosyadan gelir, bu yüzden sınıfın constructor'ı ve alan başlangıç değerleri (initializer) çalışmaz. Ancak kalıtım zincirinde **ilk `Serializable` olmayan üst sınıfın** parametresiz constructor'ı çalışır. Bu yüzden o üst sınıfın parametresiz bir constructor'ı olmak zorundadır; yoksa `InvalidClassException` alırsın. `transient` alanlar da varsayılan değerine döner.

📌 **Sık yapılan hata:** Deserialization'ın nesneyi normal `new` gibi kurup constructor çalıştırdığını sanmak. Serileştirilen sınıfın ctor'ı çalışmaz.

🔗 [9.1 §3 Deserialization'da constructor çalışmaz](9.1-io-streams-ve-serialization.md)

### Soru 6 — `System.console()` her zaman bir nesne döner mi?

**Kısa cevap:** **Hayır — `System.console()` `null` dönebilir (IDE'den ya da yönlendirilmiş girdiyle çalıştırıldığında genelde `null`'dır).**

**Ayrıntı:** `System.console()` bir `Console` nesnesi döndürebilir ama ortam elverişli değilse (IDE içinde, girdi/çıktı yönlendirilmişse) `null` döner. Bu yüzden kullanmadan önce `null` kontrolü yapılmalıdır. `Console` üzerinde `readLine(...)` ve ekranda görünmeyen `readPassword(...)` gibi metotlar vardır. Sınav "her zaman bir `Console` döner" şıkkını koyar; bu yanlıştır.

📌 **Sık yapılan hata:** `System.console()`'un daima geçerli bir nesne döndüğünü varsayıp `null` kontrolü yapmamak.

🔗 [9.1 §2 Konsol](9.1-io-streams-ve-serialization.md)

## 9.2 NIO.2: Path ve Files

### Soru 1 — `Path.of("/a/b").resolve("/x/y")` ne döner, neden?

**Kısa cevap:** **`/x/y` döner; çünkü `resolve`'a verilen argüman mutlak (absolute) bir yolsa soldaki yol tamamen yok sayılır.**

**Ayrıntı:** `resolve` genelde birleştirme yapar: göreli bir yol verilirse soldakine eklenir (`/a/b` + `c/d` → `/a/b/c/d`). Ancak argüman **mutlak** bir yolsa (`/x/y`) birleştirme olmaz, soldaki taban tamamen atılır ve argümanın kendisi döner. Kardeş dosya için `resolveSibling` kullanılır.

📌 **Sık yapılan hata:** Mutlak argümanın da soldakine ekleneceğini sanmak. Mutlak argüman verilince sol yol yok sayılır.

🔗 [9.2 §2 resolve, relativize, normalize](9.2-nio2-path-ve-files.md)

### Soru 2 — `Path.of("/a/b/c/d").relativize(Path.of("/a/b"))` ne döner?

**Kısa cevap:** **`../..` döner.**

**Ayrıntı:** `relativize` "bu yoldan şu yola nasıl giderim" sorusunu yanıtlar. `/a/b/c/d`'den `/a/b`'ye ulaşmak için iki seviye yukarı çıkmak gerekir, bu yüzden sonuç `../..`'dir. Tersi yönde (`/a/b` → `/a/b/c/d`) sonuç `c/d` olurdu. İki yolun da aynı türden (ikisi mutlak veya ikisi göreli) olması şarttır.

📌 **Sık yapılan hata:** Geriye gitmenin `relativize` ile ifade edilemeyeceğini sanmak. Geriye gidiş `..` ile üretilir.

🔗 [9.2 §2 resolve, relativize, normalize](9.2-nio2-path-ve-files.md)

### Soru 3 — `Path.of("/a/b/c").getNameCount()` kaçtır, kök sayılır mı?

**Kısa cevap:** **`3`'tür; kök (`/`) sayılmaz.**

**Ayrıntı:** `getNameCount()` yoldaki isim öğelerini sayar ama kökü dâhil etmez. `/a/b/c` için öğeler `a`, `b`, `c` → `3`. Aynı şekilde `/a/b` için `2`, göreli `a/b` için de `2` olur. `getName(0)` da her zaman kökten sonraki ilk öğedir (`a`).

📌 **Sık yapılan hata:** Kökü de bir öğe sanıp sayıyı bir fazla hesaplamak. Kök sayıya dâhil değildir.

🔗 [9.2 §1 Path oluşturma ve bileşenleri](9.2-nio2-path-ve-files.md)

### Soru 4 — `Path.of("/a/b/../c").equals(Path.of("/a/c"))` neden `false`?

**Kısa cevap:** **`Path.equals` sözdizimsel (metinsel) karşılaştırır ve normalize etmez; `/a/b/../c` metni `/a/c`'den farklı olduğu için `false` döner.**

**Ayrıntı:** `equals` yolları anlamsal olarak değil, metinsel olarak karşılaştırır. `/a/b/../c` normalize edilince `/a/c`'ye eşittir ama `equals` bu sadeleştirmeyi yapmaz. Anlamsal eşitlik için önce `normalize()` (`.` ve `..` temizler), gerekirse `toRealPath()` kullanılır.

📌 **Sık yapılan hata:** `equals`'ın `..`/`.` sadeleştirmesi yaparak eşitlik göreceğini sanmak. Önce `normalize()` gerekir.

🔗 [9.2 §2 resolve, relativize, normalize](9.2-nio2-path-ve-files.md)

### Soru 5 — `Files.delete` ile `Files.deleteIfExists` arasındaki fark ne?

**Kısa cevap:** **`Files.delete` dosya yoksa `NoSuchFileException` atar; `Files.deleteIfExists` yoksa `false` döner, exception atmaz.**

**Ayrıntı:** `Files.delete(p)` silmeyi dener; dosya yoksa `NoSuchFileException`, dizin doluysa `DirectoryNotEmptyException` atar. `Files.deleteIfExists(p)` ise dosya yoksa sessizce `false` döner (varsa siler ve `true` döner), bu yüzden "varsa sil" senaryolarında exception yakalamaya gerek kalmaz.

📌 **Sık yapılan hata:** `Files.delete`'in yok olan dosyada sessizce geçeceğini sanmak. Exception atar; sessiz davranış için `deleteIfExists` gerekir.

🔗 [9.2 §3 Files — disk işlemleri](9.2-nio2-path-ve-files.md)

### Soru 6 — `Files.lines` ile `Files.readAllLines` arasındaki iki fark ne?

**Kısa cevap:** **`Files.lines` tembel bir `Stream<String>` döner ve try-with-resources ile kapatılmalıdır; `Files.readAllLines` tüm dosyayı belleğe alıp `List<String>` döner ve kapatma gerektirmez.**

**Ayrıntı:** `Files.lines(p)` satırları tembel (lazy) okur, açık bir dosya tanıtıcısı tutar ve bu yüzden mutlaka `try (Stream<String> s = Files.lines(p))` gibi kapatılmalıdır — aynı kural `Files.list` ve `Files.walk` için de geçerlidir. `Files.readAllLines(p)` ise tüm satırları belleğe alıp `List<String>` döner; kapatma gerekmez ama büyük dosyada bellek maliyeti yüksektir.

📌 **Sık yapılan hata:** `Files.lines`'ı kapatmadan kullanmak. Açık dosya tanıtıcısı tuttuğu için try-with-resources şart.

🔗 [9.2 §3 Files — disk işlemleri](9.2-nio2-path-ve-files.md)
