# 08 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 8.1 Thread'ler ve virtual thread'ler

### Soru 1 — `t.run()` ile `t.start()` arasındaki fark nedir, `run()` hangi thread'de çalışır?

**Kısa cevap:** **`start()` yeni bir thread açar; `run()` doğrudan çağrılırsa mevcut thread'de (çağıran thread'de) çalışır.**

**Ayrıntı:** `t.start()` JVM'e yeni bir thread açtırır ve `run()` o yeni thread'de çalışır (`Thread-0` gibi). `t.run()` ise sıradan bir metot çağrısıdır: yeni thread açılmaz, kod çağıran thread'in içinde yürür. `main` içinden çağırırsan `Thread.currentThread().getName()` **`main`** yazdırır — sınavın en sık kurduğu tuzak budur.

📌 **Sık yapılan hata:** `run()`'ın da yeni thread açtığını sanmak. Yeni thread yalnızca `start()` ile açılır.

🔗 [8.1 §1 start() vs run()](8.1-threadler-ve-virtual-threads.md)

### Soru 2 — Bir thread'i iki kez başlatmaya çalışırsan ne olur?

**Kısa cevap:** **İkinci `start()` çağrısı `IllegalThreadStateException` atar.**

**Ayrıntı:** Bir thread yeniden başlatılamaz. Bir kez `start()` edilip çalışmaya başlamış (veya bitmiş) bir thread'e ikinci kez `start()` çağırmak çalışma zamanında `IllegalThreadStateException` fırlatır. Aynı işi tekrar yapmak istiyorsan yeni bir `Thread` nesnesi oluşturman gerekir.

📌 **Sık yapılan hata:** Aynı `Thread` nesnesini yeniden kullanabileceğini sanmak. Thread tek kullanımlıktır.

🔗 [8.1 §1 start() vs run()](8.1-threadler-ve-virtual-threads.md)

### Soru 3 — Virtual thread'in daemon durumu nedir, değiştirebilir misin?

**Kısa cevap:** **Virtual thread her zaman daemon'dır (`isDaemon()` → `true`) ve değiştirilemez.**

**Ayrıntı:** Virtual thread'ler her zaman daemon olarak çalışır; `isDaemon()` daima `true` döner. `setDaemon(false)` çağırmak **`IllegalArgumentException`** atar. Ayrıca öncelik (priority) de yok sayılır, hep `5` döner. Platform thread'de ise varsayılan `daemon = false`'tur ve `setDaemon` `start()`'tan önce çağrılabilir.

📌 **Sık yapılan hata:** Virtual thread'i `setDaemon(false)` ile normal (non-daemon) yapabileceğini sanmak. Bu exception atar.

🔗 [8.1 §3 Virtual thread'ler](8.1-threadler-ve-virtual-threads.md)

### Soru 4 — Virtual thread'ler neden havuzlanmaz?

**Kısa cevap:** **Çok ucuz oldukları için havuzlamak anlamsızdır; her görev için yenisi açılır.**

**Ayrıntı:** Platform thread'i işletim sisteminin thread'idir, ~1 MB yığın kaplar ve pahalıdır — bu yüzden `ExecutorService` ile havuzlanır. Virtual thread ise JVM'in yönettiği hafif bir thread'tir; milyonlarcası açılabilir. Havuzun amacı pahalı kaynağı yeniden kullanmaktır, oysa virtual thread ucuz olduğu için havuzlamanın kazancı yoktur. Java 21'de görev başına bir virtual thread açan `Executors.newVirtualThreadPerTaskExecutor()` kullanılır.

📌 **Sık yapılan hata:** Virtual thread'leri de fixed pool içinde havuzlamaya çalışmak. Görev başına bir tane açmak doğru modeldir.

🔗 [8.1 §3 Virtual thread'ler](8.1-threadler-ve-virtual-threads.md)

### Soru 5 — Pinning nedir ve nasıl kaçınılır?

**Kısa cevap:** **Virtual thread bir `synchronized` blok içindeyken bloklanırsa taşıyıcı (carrier) thread'e çivilenir; kaçınmak için `synchronized` yerine `ReentrantLock` kullanılır.**

**Ayrıntı:** Normalde virtual thread bloklayıcı bir işleme (I/O, `sleep`) girince JVM onu taşıyıcı platform thread'inden söker (unmount) ve taşıyıcıyı başka virtual thread'e verir. Ama `synchronized` blok içinde bloklanırsa virtual thread taşıyıcıya **pinned** olur, taşıyıcı serbest kalmaz ve ölçek kaybolur. Bu yüzden virtual thread kullanan kodda `synchronized` yerine `ReentrantLock` tercih edilir.

📌 **Sık yapılan hata:** `synchronized`'ın virtual thread ile de sorunsuz ölçekleneceğini sanmak. Bloklama anında pinning oluşur.

🔗 [8.1 §3 Virtual thread'ler / Pinning](8.1-threadler-ve-virtual-threads.md)

### Soru 6 — `sleep` ile `wait` arasındaki kilit davranışı farkı nedir?

**Kısa cevap:** **`sleep` kilitleri bırakmaz; `wait()` çağrıldığı kilidi bırakır.**

**Ayrıntı:** `Thread.sleep(n)` thread'i `TIMED_WAITING` durumuna sokar ama tuttuğu hiçbir kilidi bırakmaz — checked `InterruptedException` atar. `wait()` ise tutulan monitör kilidini bırakır ve başka thread'lerin o kilidi almasına izin verir (üretici-tüketici deseninin temeli). İkisi de eşzamanlılıkta sık karıştırılır.

📌 **Sık yapılan hata:** `sleep`'in de kilidi bıraktığını sanmak. Kilidi yalnızca `wait()` bırakır.

🔗 [8.1 §2 Thread durumları](8.1-threadler-ve-virtual-threads.md)

## 8.2 ExecutorService, Callable ve Future

### Soru 1 — `execute` ile `submit` arasındaki fark ne, `submit(Runnable).get()` ne döner?

**Kısa cevap:** **`execute(Runnable)` `void` döner (sonuç izlenemez); `submit` bir `Future` döner ve `submit(Runnable).get()` → `null`.**

**Ayrıntı:** `execute` yalnızca `Runnable` alır ve `void` döner — görevin sonucunu ya da bitişini izleyemezsin. `submit` ise `Future` döner: `Runnable` verirsen `Future<?>` alırsın ve `Runnable` değer üretmediği için `get()` **`null`** döner; `Callable<T>` verirsen `Future<T>` alırsın ve `get()` gerçek değeri döner.

📌 **Sık yapılan hata:** `submit(Runnable).get()`'in görevin döndürdüğü bir şey vereceğini sanmak. Runnable değer üretmez, `null` döner.

🔗 [8.2 §2 Görev gönderme](8.2-executor-service.md)

### Soru 2 — Bir görev exception atarsa bunu nerede ve hangi tipte görürsün? Asıl hataya nasıl ulaşırsın?

**Kısa cevap:** **`get()` çağrıldığında `ExecutionException` olarak görürsün; asıl hataya `getCause()` ile ulaşırsın.**

**Ayrıntı:** Görevin fırlattığı exception `submit` sırasında görünmez; ancak `f.get()` çağrılınca ortaya çıkar ve checked `ExecutionException` içine sarılmış gelir. Gerçek exception `e.getCause()`'tadır (`getMessage()` sarmalayana aittir). `get()` ayrıca `InterruptedException`, süreli sürümde `TimeoutException`, iptal edilmiş görevde `CancellationException` atabilir.

📌 **Sık yapılan hata:** Hatayı `submit` satırında beklemek ya da `getMessage()`'a bakmak. Hata `get()`'te çıkar, asıl neden `getCause()`'tadır.

🔗 [8.2 §3 Future / Exception nasıl gelir](8.2-executor-service.md)

### Soru 3 — `get()` hiç çağrılmazsa görevin hatası ne olur?

**Kısa cevap:** **Hata sessizce kaybolur — hiçbir çıktı olmaz.**

**Ayrıntı:** Görevin exception'ı yalnızca `get()` çağrıldığında yüzeye çıkar. `get()` hiç çağrılmazsa `ExecutionException` da hiç oluşmaz ve hata görünmeden yutulur. Sınav bu durumu "ne yazdırır?" diye sorup **hiçbir çıktı olmadığını** bekler.

📌 **Sık yapılan hata:** Görev patlarsa konsola otomatik bir hata basılacağını sanmak. `get()` çağrılmazsa hiçbir şey görünmez.

🔗 [8.2 §3 Future / Exception nasıl gelir](8.2-executor-service.md)

### Soru 4 — `invokeAll` ile `invokeAny` dönüş tipleri nasıl farklı?

**Kısa cevap:** **`invokeAll` tüm görevler bitince `List<Future<T>>` döner; `invokeAny` ilk başarılı sonucun değerini (Future değil, `T`) döner.**

**Ayrıntı:** `invokeAll(Collection<Callable<T>>)` verilen tüm görevleri çalıştırır, **hepsi bitene kadar bloklar** ve her göreve karşılık bir `Future` içeren liste döner. `invokeAny` ise görevlerden **ilk başarıyla tamamlananın** doğrudan değerini döner — bir `Future` değil, sonucun kendisidir.

📌 **Sık yapılan hata:** `invokeAny`'nin de `Future` döndüğünü sanmak. O, değerin kendisini döner.

🔗 [8.2 §2 Görev gönderme](8.2-executor-service.md)

### Soru 5 — `shutdown()`, `shutdownNow()`, `awaitTermination()` sırasıyla ne yapar?

**Kısa cevap:** **`shutdown()` yeni görev almaz ama kuyruktakileri çalıştırır ve bloklamaz; `shutdownNow()` çalışanları kesmeye çalışır ve bekleyenleri `List<Runnable>` olarak döner; `awaitTermination(n, unit)` bitişi belirtilen süre kadar bekler.**

**Ayrıntı:** `shutdown()` executor'ı kapanışa alır — sonrasında `submit` çağrısı `RejectedExecutionException` (unchecked) atar — fakat mevcut/kuyruktaki görevleri tamamlar ve **beklemez**. `shutdownNow()` çalışan görevleri kesmeye çalışır ve henüz başlamamış görevleri liste olarak geri verir. `awaitTermination` ise verilen süre içinde tüm görevler biterse `true` döner. "Görevler bitti mi" sorusuna `isTerminated`, "shutdown çağrıldı mı" sorusuna `isShutdown` cevap verir.

📌 **Sık yapılan hata:** `shutdown()`'ın görevlerin bitmesini beklediğini sanmak. Beklemek için `awaitTermination` (veya `close()`) gerekir.

🔗 [8.2 §4 Kapatma](8.2-executor-service.md)

### Soru 6 — `scheduleAtFixedRate` ile `scheduleWithFixedDelay` arasındaki fark nedir?

**Kısa cevap:** **`scheduleAtFixedRate` ardışık çalıştırmaların başlangıçları arasını sabit tutar; `scheduleWithFixedDelay` bir çalıştırmanın bitişi ile bir sonrakinin başlangıcı arasını sabit tutar.**

**Ayrıntı:** `scheduleAtFixedRate` aralığı **başlangıçtan başlangıca** ölçer; görev uzun sürerse sıradaki çalıştırmalar birikir/gecikir. `scheduleWithFixedDelay` ise aralığı **öncekinin bitişinden** başlatır, yani görev uzadıkça toplam aralık kayar. İkisi de zamanlanmış görev fabrikalarıyla (`newScheduledThreadPool`) kullanılır.

📌 **Sık yapılan hata:** İkisini eşdeğer sanmak. `FixedRate` başlangıçları, `FixedDelay` bitiş-başlangıç arasını sabitler.

🔗 [8.2 §5 Zamanlanmış görevler](8.2-executor-service.md)

## 8.3 Thread-safe kod: kilitler, atomic ve concurrent koleksiyonlar

### Soru 1 — `volatile` neyi çözer, neyi çözmez? `volatile int sayac; sayac++;` güvenli mi?

**Kısa cevap:** **`volatile` görünürlüğü çözer, atomikliği çözmez; `volatile int sayac; sayac++;` thread-safe değildir.**

**Ayrıntı:** Eşzamanlılıkta iki ayrı sorun vardır: görünürlük (bir thread'in yazdığını diğeri görüyor mu) ve atomiklik (işlem bölünmeden bitiyor mu). `volatile` yalnızca görünürlüğü sağlar. `sayac++` aslında üç işlemdir (oku-artır-yaz) ve `volatile` bunları atomik yapmaz — birden çok thread arttırırsa sonuç eksik çıkar. Atomiklik için `synchronized`, `ReentrantLock` veya `AtomicInteger` gerekir.

📌 **Sık yapılan hata:** `volatile`'ın bir sayacı thread-safe yapacağını sanmak. Sadece görünürlük verir, `++` yine yarışır.

🔗 [8.3 §3 volatile ve Atomic](8.3-thread-safety.md)

### Soru 2 — `static synchronized` bir metot hangi nesneyi kilitler?

**Kısa cevap:** **Sınıf nesnesini — `SinifAdi.class`'ı kilitler.**

**Ayrıntı:** Örnek (`instance`) `synchronized` metot `this` üzerinde kilitlenir; `static synchronized` metot ise nesneye değil, sınıfa ait `Sinif.class` monitörü üzerinde kilitlenir. Bu yüzden statik ve örnek senkronize metotlar farklı kilitler kullanır ve birbirini engellemez.

📌 **Sık yapılan hata:** `static synchronized`'ın da `this`'i kilitlediğini sanmak. Statikte kilit `Sinif.class`'tır.

🔗 [8.3 §2 synchronized](8.3-thread-safety.md)

### Soru 3 — `wait()` çağırmadan önce ne yapmış olmalısın, yapmazsan ne olur?

**Kısa cevap:** **İlgili nesnenin kilidini tutuyor (`synchronized` blok içinde) olmalısın; tutmuyorsan `IllegalMonitorStateException` atılır.**

**Ayrıntı:** `wait()`, `notify()` ve `notifyAll()` yalnızca o nesnenin monitör kilidi tutulurken çağrılabilir. Kilit tutulmadan çağrılırsa çalışma zamanında `IllegalMonitorStateException` fırlar. Ayrıca `wait()` bir `while` döngüsü içinde çağrılmalıdır (sahte uyanmalara / spurious wakeup karşı); `if` yeterli değildir. `wait()` çağrıldığında kilit bırakılır.

📌 **Sık yapılan hata:** `wait()`'i `synchronized` dışında çağırmak veya `while` yerine `if` kullanmak. İkisi de klasik sınav tuzağıdır.

🔗 [8.3 §2 wait / notify / notifyAll](8.3-thread-safety.md)

### Soru 4 — `wait` ile `sleep` arasındaki kilit farkı ne?

**Kısa cevap:** **`wait()` kilidi bırakır; `sleep()` kilidi bırakmaz.**

**Ayrıntı:** `wait()` çağrıldığında thread tuttuğu monitör kilidini serbest bırakır ve başka thread'lerin ilerlemesine izin verir; `notify`/`notifyAll` ile uyandırılır. `sleep(n)` ise thread'i belli süre bekletir ama tuttuğu hiçbir kilidi bırakmaz. Bu ayrım üretici-tüketici senaryolarında kritiktir.

📌 **Sık yapılan hata:** `sleep`'in de kilidi bıraktığını sanmak. Kilidi yalnızca `wait` bırakır.

🔗 [8.3 §2 wait / notify / notifyAll](8.3-thread-safety.md)

### Soru 5 — `getAndIncrement` ile `incrementAndGet` arasındaki fark ne?

**Kısa cevap:** **`getAndIncrement` eski (artırmadan önceki) değeri, `incrementAndGet` yeni (artırılmış) değeri döner.**

**Ayrıntı:** `AtomicInteger`'da `getAndIncrement()` `i++` gibidir — önce mevcut değeri döner, sonra artırır. `incrementAndGet()` `++i` gibidir — önce artırır, sonra yeni değeri döner. Örneğin `new AtomicInteger(5)` üzerinde `getAndIncrement()` `5` döner ve alan `6` olur; sonraki `get()` `6` verir.

📌 **Sık yapılan hata:** İkisini aynı sanmak. `get...` eski, `...Get` yeni değeri döndürür — `i++` / `++i` mantığı.

🔗 [8.3 §3 volatile ve Atomic](8.3-thread-safety.md)

### Soru 6 — `CopyOnWriteArrayList` neden `ConcurrentModificationException` atmaz?

**Kısa cevap:** **Her yazmada içeriğin yeni bir kopyasını oluşturduğu için iterator eski kopya üzerinde gezer; iterasyon sırasında yapılan değişiklik o gezmeyi bozmaz.**

**Ayrıntı:** `CopyOnWriteArrayList` her ekleme/değiştirmede diziyi kopyalar; başlatılmış bir iterator, iterasyon başındaki eski kopyayı dolaşır. Bu yüzden iterasyon sırasında `add` yapsan bile `ConcurrentModificationException` atılmaz — ancak yeni eklenen eleman o iterasyonda **görünmez**. Okumanın çok, yazmanın az olduğu senaryolar için uygundur. (Sıradan `ArrayList` aynı durumda CME atar.)

📌 **Sık yapılan hata:** Yeni eklenen elemanın aynı iterasyonda görüneceğini sanmak. Iterator eski kopyayı gezer, yeni eleman görünmez.

🔗 [8.3 §5 Concurrent koleksiyonlar](8.3-thread-safety.md)
