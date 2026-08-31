# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 2.1 if/else, switch ve pattern matching

### Soru 1 — `switch` deyimi ile `switch` ifadesi arasındaki dört farkı say.

**Kısa cevap:** **Deyim değer üretmez, fall-through yapar, `break` ister, exhaustive olmak zorunda değildir; ifade değer üretir, düşmez, `break` istemez, exhaustive olmalıdır.**

**Ayrıntı:** Deyim (`case X:`) bir şey *yapar*; `break` yoksa sonraki `case`'e düşer ve `default` isteğe bağlıdır. İfade (`case X ->`) bir değer *üretir*, düşme yoktur, `break` yerine gerektiğinde `yield` kullanılır ve tüm olası girdileri kapsamak (exhaustive) zorundadır. Ayrıca ifade bir atama deyimi olduğu için sonunda **noktalı virgül** ister; iki sözdizimi (`:` ve `->`) aynı `switch` içinde karıştırılamaz.

📌 **Sık yapılan hata:** İfade biçiminde `default` (ya da tüm enum sabitleri) olmadan derlenebileceğini sanmak — kapsama zorunludur.

🔗 [2.1 §3 switch ifadesi (expression)](2.1-if-switch-ve-pattern-matching.md)

### Soru 2 — `long` bir değişkeni `switch` selector'ü yapabilir misin? Ya `Integer`?

**Kısa cevap:** **`long` olmaz (derleme hatası); `Integer` olur (unbox edilir).**

**Ayrıntı:** İzin verilen selector tipleri `byte`, `short`, `char`, `int` ve bunların wrapper'ları, `String`, `enum` ve Java 21'de pattern label kullanılırsa herhangi bir referans tipidir. `long`, `float`, `double`, `boolean` **yasaktır**. `Integer` geçerlidir; otomatik unbox edilir, ama değer `null` ise çalışma zamanında **NPE** atar.

📌 **Sık yapılan hata:** `long`'un desteklendiğini sanmak — `int` çalışır ama `long` çalışmaz, en çok şaşırtan kural budur.

🔗 [2.1 §2 switch deyimi — izin verilen selector tipleri](2.1-if-switch-ve-pattern-matching.md)

### Soru 3 — `case Object o ->` satırını en üste yazarsan ne olur, neden?

**Kısa cevap:** **Derlenmez — dominance ihlali.** Altındaki tüm `case`'ler ulaşılamaz olur.

**Ayrıntı:** Pattern switch'te genel bir desen, daha özel bir deseni **gölgeleyemez**. `case Object o` her nesneyle eşleştiği için ondan sonra gelen `case String s` gibi daha özel desenler asla çalışamaz; derleyici bunu "dominated by a preceding case label" olarak reddeder. Kural: özelden genele sırala, genel deseni **sona** koy. Aynı mantıkla, guard'sız bir desen (`case Integer i`) guard'lı aynı desenden (`case Integer i when ...`) **önce** yazılırsa guard'lı olan ulaşılamaz olur.

📌 **Sık yapılan hata:** Genel deseni (ya da guard'sız deseni) önce yazıp sonra özelleştirmeye çalışmak — sıralama ters olmalıdır.

🔗 [2.1 §5 switch pattern matching — dominance](2.1-if-switch-ve-pattern-matching.md)

### Soru 4 — Pattern switch'e `null` gelirse ne olur; nasıl güvenli hâle getirirsin?

**Kısa cevap:** **`case null` yazmadıysan `null` → NPE atar;** güvenli hâle getirmek için `case null ->` (veya `case null, default ->`) eklersin.

**Ayrıntı:** Klasik `switch` gibi pattern `switch` de selector `null` olduğunda varsayılan olarak **NullPointerException** atar. `default` null'ı yakalamaz. Açıkça `case null ->` yazarsan null'ı yakalarsın; istersen `case null, default ->` ile null'ı default davranışına bağlayabilirsin. Not: record pattern null ile eşleşmez, o yüzden null'ı ayrıca ele almak gerekir.

📌 **Sık yapılan hata:** `default` dalının null'ı da yakaladığını sanmak — yakalamaz, yalnızca `case null` yakalar.

🔗 [2.1 §5 switch pattern matching — null](2.1-if-switch-ve-pattern-matching.md)

### Soru 5 — `if (o instanceof String s || s.isEmpty())` neden derlenmez?

**Kısa cevap:** **`||` kısa devresinde `s`'in atandığı garanti değildir**, bu yüzden `s` sağ tarafta kapsamda değildir.

**Ayrıntı:** Pattern değişkeni `s` yalnızca derleyicinin "kesinlikle `String`" diyebildiği yerde geçerlidir (flow scoping). `&&`'de sol taraf `true` ise sağ tarafa geçilir, dolayısıyla `o instanceof String s && s.length() > 2` çalışır. Ama `||`'de sol taraf `false` olduğunda da sağ tarafa geçilir; o durumda `s` atanmamıştır, garanti edilemez ve derleyici reddeder.

📌 **Sık yapılan hata:** `&&` ve `||`'yi eşdeğer sanmak — pattern değişkeni `&&` ile taşınır, `||` ile taşınmaz.

🔗 [2.1 §4 instanceof pattern matching — flow scoping](2.1-if-switch-ve-pattern-matching.md)

### Soru 6 — `->` biçiminde blok kullanırsan değeri nasıl döndürürsün?

**Kısa cevap:** **`yield` ile.** `return` kullanılamaz.

**Ayrıntı:** `case X -> { ... }` şeklinde blok kullandığında blok bir değer üretmek zorundadır ve bunu `yield deger;` ile yapar. Blok kullanıp `yield` yazmazsan "switch rule completes without providing a value" hatasıyla **derlenmez**. `switch` ifadesi içinde `return` kullanmak da yasaktır; değer döndürmenin tek yolu `yield`'dir. Tek satırlık ok (`case X -> deger;`) zaten örtük olarak o değeri üretir, orada `yield` gerekmez.

📌 **Sık yapılan hata:** Blok içinde `return` yazmak ya da `yield`'i unutmak — ikisi de derleme hatası verir.

🔗 [2.1 §3 switch ifadesi — yield](2.1-if-switch-ve-pattern-matching.md)

## 2.2 Döngüler, break ve continue

### Soru 1 — `while (false) { }` neden derlenmez ama `if (false) { }` derlenir?

**Kısa cevap:** **`while (false)` gövdesi ulaşılamaz kod olduğu için derlenmez;** `if (false)` bilinçli bir istisnayla derlenir.

**Ayrıntı:** Java'da ulaşılamayan kod bir derleme hatasıdır. Derleyici `while (false)`'un gövdesine asla girilemeyeceğini gördüğü için gövdeyi ulaşılamaz sayar ve reddeder. `if (false)` ise özellikle **koşullu derleme** (conditional compilation) amacıyla dilde tanınan bir istisnadır; derleyici gövdeyi ulaşılamaz saymaz ve kodu kabul eder. Hafıza kancası: "`if (false)` affedilir, `while (false)` affedilmez."

📌 **Sık yapılan hata:** İkisini aynı kurala tabi sanmak — istisna yalnızca `if`'e tanınır, `while`/`for`'a değil.

🔗 [2.2 §2 Ulaşılamayan kod = derleme hatası](2.2-donguler.md)

### Soru 2 — `do { } while (cond);` gövdesi en az kaç kez çalışır ve sonunda ne zorunludur?

**Kısa cevap:** **En az 1 kez çalışır;** sonunda **noktalı virgül** zorunludur.

**Ayrıntı:** `do-while` koşulu gövdeden *sonra* kontrol eder, bu yüzden koşul en baştan `false` olsa bile gövde bir kez çalışır. Örneğin `int i = 10; do { print(i); } while (i < 5);` → `10` yazdırır. `while (false)`'un aksine `do { } while (false);` derlenir çünkü gövde gerçekten çalışır. Yapının sonundaki `;` sözdizimsel olarak zorunludur.

📌 **Sık yapılan hata:** Sonundaki noktalı virgülü unutmak ya da koşul `false` olduğunda gövdenin hiç çalışmayacağını sanmak.

🔗 [2.2 §1 Dört döngü biçimi](2.2-donguler.md)

### Soru 3 — for-each ile bir listeden eleman silmenin iki güvenli yolu nedir?

**Kısa cevap:** **`Collection.removeIf(...)`** ve **`Iterator.remove()`**.

**Ayrıntı:** for-each döngüsü içinde koleksiyonu doğrudan `add`/`remove` ile değiştirmek çalışma zamanında **`ConcurrentModificationException`** atar (bu, adına rağmen thread ile ilgili değildir; tek thread'de de olur). Güvenli iki yol: koşula uyanları toplu silen `liste.removeIf(s -> ...)`, ya da elle bir `Iterator` üzerinden dolaşıp `it.remove()` çağırmak. İkisi de yapının iç sayaç durumuyla tutarlı çalışır.

📌 **Sık yapılan hata:** `ConcurrentModificationException`'ı bir thread/eşzamanlılık sorunu sanmak — tek thread'de for-each içinde `remove` çağırınca da olur.

🔗 [2.2 §3 Enhanced for (for-each)](2.2-donguler.md)

### Soru 4 — `for` içindeki `continue` ile `while` içindeki `continue` arasında ne fark var?

**Kısa cevap:** **`for`'da `continue` sonrası `update` bölümü yine çalışır; `while`'da doğrudan koşula gidilir**, artırmayı gövdede atlarsan sonsuz döngü olur.

**Ayrıntı:** `continue` içinde bulunduğu turu atlar. `for` döngüsünde bu atlama başlıktaki `update` (örn. `i++`) bölümünü çalıştırdıktan sonra koşula döner; bu yüzden sayaç ilerlemeye devam eder. `while` döngüsünde ise `continue` doğrudan koşula gider; artırma satırı gövdenin içinde ve `continue`'dan sonra ise atlanır, sayaç ilerlemez ve **sonsuz döngü** oluşur. Örnek: `while (i < 5) { if (i == 2) continue; i++; }` → `i` 2'de takılır.

📌 **Sık yapılan hata:** `while` içinde `continue` kullanırken artırmayı `continue`'dan önce yapmayı unutup sonsuz döngüye düşmek.

🔗 [2.2 §5 Döngü sayımı](2.2-donguler.md)

### Soru 5 — `break outer;` ile `continue outer;` arasındaki fark ne?

**Kısa cevap:** **`break outer;` etiketli döngüden tamamen çıkar; `continue outer;` etiketli döngünün bir sonraki turuna geçer.**

**Ayrıntı:** İç içe döngülerde etiket, döngünün hemen üstüne `outer:` biçiminde konur. `break outer;` o etiketli (dış) döngüyü ve içindekileri sonlandırıp döngüden dışarı çıkar. `continue outer;` ise dış döngüyü bitirmez; onun sonraki iterasyonuna atlar (iç döngünün kalan turlarını atlayarak). Var olmayan bir etiket kullanmak derleme hatasıdır. Not: `continue` yalnızca döngüye uygulanabilir, `break` bir bloğa da uygulanabilir.

📌 **Sık yapılan hata:** `continue outer;`'ın da tüm döngüden çıktığını sanmak — o yalnızca dış döngünün sonraki turuna geçer.

🔗 [2.2 §4 break, continue ve etiketler](2.2-donguler.md)

### Soru 6 — `for (int i = 0; i < 3; i++);` kaç kez döner ve altındaki satır kaç kez çalışır?

**Kısa cevap:** **Döngü 3 kez döner (gövdesi boş); altındaki satır 1 kez çalışır.**

**Ayrıntı:** Başlığın hemen ardındaki noktalı virgül gövdeyi **boşaltır**; döngü `i = 0,1,2` için üç kez döner ama her turda hiçbir şey yapmaz. Altında yer alan blok/satır döngüye ait değildir, döngü bittikten sonra **bir kez** çalışır — girinti yanıltıcı olsa da bağlayıcı olan noktalı virgüldür. Ayrıca `i` döngü başlığında bildirildiği için döngü dışında görünmez.

📌 **Sık yapılan hata:** Girintiye bakıp alttaki satırı döngü gövdesi sanmak; `;` gövdeyi bitirir, o satır döngüden bağımsızdır.

🔗 [2.2 §1 for başlığı kuralları](2.2-donguler.md)
