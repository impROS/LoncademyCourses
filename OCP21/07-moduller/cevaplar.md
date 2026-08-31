# 07 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 7.1 Modül tanımlama: exports, requires ve servisler

### Soru 1 — `requires` ile `exports` arasındaki yön farkı nedir?

**Kısa cevap:** **`requires` içeri bakar (ben bağımlıyım), `exports` dışarı bakar (paketimi açarım).**

**Ayrıntı:** `requires m;` "ben `m` modülüne bağımlıyım, onu kullanacağım" demektir — yön **içeriye**. `exports p;` ise "`p` paketimi başka modüller kullanabilsin diye açarım" demektir — yön **dışarıya**. En çok karıştırılan çift budur: "bu modülü kullanacağım" → `requires`; "paketimi açayım" → `exports`. `exports p to a, b;` ile paket yalnızca belirli modüllere açılabilir (qualified export).

📌 **Sık yapılan hata:** İkisini ters kullanmak — "kullanacağım" derken `exports`, "açayım" derken `requires` yazmak.

🔗 [7.1 §2 Yönergeler (requires/exports yönü)](7.1-modul-tanimlama.md)

### Soru 2 — Spring gibi bir framework private alanlara erişecekse hangi yönerge gerekir, neden `exports` yetmez?

**Kısa cevap:** **`opens` gerekir.** `exports` yansımaya özel üye erişimi vermez.

**Ayrıntı:** `exports` yalnızca **derleme + çalışma zamanı** erişimi verir; yani dışarıdaki modül `public` API'yi görür ama yansımayla (reflection) private üyelere erişemez. Spring, Jackson, JPA gibi framework'ler private alanlara yansımayla eriştiği için paketin `opens p;` (veya `opens p to a;`) ile açılması gerekir. Tüm paketleri yansımaya açmak için `open module m { }` kullanılır — ancak `open module` içinde ayrıca `opens` yazmak derleme hatasıdır.

📌 **Sık yapılan hata:** `exports`'un yansımaya da erişim verdiğini sanmak. Framework private alanlara erişecekse `opens` şarttır.

🔗 [7.1 §2 Yönergeler / opens tuzağı](7.1-modul-tanimlama.md)

### Soru 3 — `requires transitive` ne zaman zorunludur?

**Kısa cevap:** **Bir modülün public API'si, `requires` ettiği başka bir modülün tiplerini döndürüyorsa zorunludur.**

**Ayrıntı:** `b` modülü, API'sinde `a`'nın tiplerini döndürüyorsa ve `c` de `b`'yi kullanıyorsa, `c`'nin `a`'yı görebilmesi için `b` içinde `requires transitive a;` yazılmalıdır. Düz `requires a;` yayılmaz, o yüzden `c` derlenmez. `requires transitive` bağımlılığı **kullananlara da** geçirir; böylece `c`, `a` paketini de görür.

📌 **Sık yapılan hata:** Düz `requires`'ın da bağımlılığı aşağı modüle taşıdığını sanmak. Yayılım yalnızca `transitive` ile olur.

🔗 [7.1 §2 requires transitive neden var](7.1-modul-tanimlama.md)

### Soru 4 — Servis sağlayan ve tüketen modüllerde hangi yönergeler bulunur?

**Kısa cevap:** **Sağlayıcıda `provides S with Impl;`, tüketicide `uses S;` — ikisi de olmalı.**

**Ayrıntı:** Sağlayıcı modül `provides com.ocp.api.Selamlayici with com.ocp.saglayici.TurkceSelamlayici;` yazar; tüketici modül `uses com.ocp.api.Selamlayici;` yazar ve `ServiceLoader.load(...)` ile uygulamaları bulur. `uses` yazılmazsa `ServiceLoader` hiçbir sağlayıcı bulamaz (boş döner, exception atmaz). Birden çok sağlayıcı varsa hepsi döner ama sıra garantisi yoktur.

📌 **Sık yapılan hata:** Yalnızca `provides` yazıp tüketicide `uses`'ı unutmak — o zaman `ServiceLoader` boş döner.

🔗 [7.1 §3 Servisler (uses / provides)](7.1-modul-tanimlama.md)

### Soru 5 — Servis uygulama sınıfının paketi neden `exports` edilmek zorunda değildir?

**Kısa cevap:** **Servis mekanizması erişimi kendisi sağlar; `provides` yeter.**

**Ayrıntı:** Uygulama (implementation) sınıfına dışarıdan doğrudan `import` ile ulaşılmaz — ona yalnızca `ServiceLoader` üzerinden, servis arayüzü tipiyle erişilir. Bu yüzden uygulama sınıfının paketinin `exports` edilmesine gerek yoktur; `provides S with Impl;` yönergesi erişimi kendi içinde halleder. Uygulama sınıfının **parametresiz `public` constructor**'ı (veya `public static provider()` metodu) olması gerekir.

📌 **Sık yapılan hata:** Uygulama sınıfının paketini de `exports` etmeye çalışmak; gereksizdir, `provides` yeterlidir.

🔗 [7.1 §3 Servisler / uygulama paketi exports](7.1-modul-tanimlama.md)

### Soru 6 — Automatic modül nedir, adı nereden gelir?

**Kısa cevap:** **`module-info.java`'sı olmayan ama modül yolundaki bir jar'dır; adı jar adından veya `Automatic-Module-Name` manifest girdisinden gelir.**

**Ayrıntı:** Automatic (otomatik) modül, `module-info.java` içermeyen ama classpath yerine `--module-path` üzerinde bulunan bir jar'dır. Adı öncelikle manifest'teki `Automatic-Module-Name` girdisinden, yoksa jar dosyasının adından türetilir. Automatic modül **her şeyi exports eder** ve **tüm modülleri requires eder**. Named modüller classpath'teki (unnamed) kodu göremediği için, henüz modülleşmemiş kütüphaneleri köprülemek için automatic modüller kullanılır.

📌 **Sık yapılan hata:** Automatic modülü, `module-info.java` içeren named modülle karıştırmak. Automatic modülün `module-info.java`'sı yoktur ama yine de modül yolundadır.

🔗 [7.1 §4 Modül türleri (automatic)](7.1-modul-tanimlama.md)

## 7.2 Derleme, jar, jlink ve modüle geçiş

### Soru 1 — `-p` ve `-m` bayrakları neyin kısaltması, `-m`'nin değeri hangi biçimde yazılır?

**Kısa cevap:** **`-p` = `--module-path`, `-m` = `--module`; `-m`'nin değeri `modul/SinifTamAdi` biçimindedir (eğik çizgi ile).**

**Ayrıntı:** `-p` modül yolunu (`--module-path`) belirtir. `-m` (`--module`) ise çalıştırılacak modülü ve ana sınıfı `modulAdi/AnaSinifTamAdi` biçiminde alır — arada **eğik çizgi**, nokta değil. Örn: `java -p out -m com.ocp.uygulama/com.ocp.uygulama.Main`. Jar'da `Main-Class` tanımlıysa yalnızca `java -p jars -m com.ocp.uygulama` yeter.

📌 **Sık yapılan hata:** `-m` değerini `com.ocp.uygulama.Main` gibi tek parça yazmak. Modül ile sınıf arasında eğik çizgi olmalıdır; aksi hâlde çalışmaz.

🔗 [7.2 §1 java bayrakları (-p / -m)](7.2-derleme-ve-migrasyon.md)

### Soru 2 — Bir jar'ın hangi modülleri gerektirdiğini nasıl öğrenirsin?

**Kısa cevap:** **`jar --describe-module --file ...` (kısa hâli `jar -d`) ile.**

**Ayrıntı:** `jar --describe-module --file jars/x.jar` bir jar'ın modül yönergelerini gösterir: `requires` satırları, `exports`, `provides ... with ...` gibi. Örneğin çıktıda `requires com.ocp.api`, `requires java.base mandated` görürsün. `--list` (`-t`) ise yalnızca jar içeriğini (dosyaları) listeler, yönergeleri değil. Çalışırken bir modülün yönergelerini görmek için `java --describe-module com.ocp.uygulama` da kullanılabilir.

📌 **Sık yapılan hata:** `jar --list`'in modül yönergelerini gösterdiğini sanmak; o sadece içeriği listeler. Yönergeler için `--describe-module` gerekir.

🔗 [7.2 §1 jar / describe-module](7.2-derleme-ve-migrasyon.md)

### Soru 3 — `jlink` ne üretir ve hangi durumda çalışmaz?

**Kısa cevap:** **Yalnızca gereken modülleri içeren, JRE gerektirmeyen özel bir çalışma zamanı üretir; automatic modüllerle çalışmaz.**

**Ayrıntı:** `jlink`, uygulamanın ihtiyaç duyduğu modülleri toplayıp bağımsız (JDK olmadan çalışan) bir runtime üretir; bu tam JDK'ya göre çok daha küçüktür. `--launcher ad=modul` ile çalıştırılabilir bir betik de üretir. **Şart:** kullanılan tüm modüller adlandırılmış (named) olmalıdır — `jlink` **automatic modül kabul etmez**. Classpath'ten gelen jar'lar önce gerçek modül hâline getirilmelidir.

📌 **Sık yapılan hata:** Bağımlılıklardan biri automatic modülken `jlink`'in yine de çalışacağını sanmak. Automatic modül varsa `jlink` çalışmaz.

🔗 [7.2 §1 jlink (automatic modül kısıtı)](7.2-derleme-ve-migrasyon.md)

### Soru 4 — Bottom-up ve top-down migrasyon arasındaki fark ne, neden bottom-up tercih edilir?

**Kısa cevap:** **Bottom-up önce en alttaki bağımlılıkları modülleştirir; top-down önce uygulamayı modülleştirip bağımlılıkları automatic bırakır. Bottom-up tercih edilir çünkü named modül classpath'teki kodu okuyamaz.**

**Ayrıntı:** **Bottom-up (aşağıdan yukarı):** bağımlılık ağacının en altındaki modülleştirilir, sonra yukarı çıkılır — tüm bağımlılıklar modüle çevrilebiliyorsa tercih edilen yoldur. **Top-down (yukarıdan aşağı):** önce uygulama modülleştirilir, henüz modül olmayan kütüphaneler automatic modül olarak bırakılır. Bottom-up doğal sıradır çünkü **adlandırılmış bir modül, classpath'teki (unnamed) kodu okuyamaz**; o yüzden alttan başlamak daha az sürtünme yaratır.

📌 **Sık yapılan hata:** Top-down'ı her zaman doğru sanmak. Top-down, kütüphaneler henüz modül değilken (automatic bırakmak gerektiğinde) kullanılır; kural olarak bottom-up tercih edilir.

🔗 [7.2 §2 Migrasyon stratejileri](7.2-derleme-ve-migrasyon.md)

### Soru 5 — Split package nedir, neden yasaktır?

**Kısa cevap:** **Aynı paket adının birden çok modülde bulunmasıdır; modül sistemi bunu reddeder.**

**Ayrıntı:** Split package, aynı paketin (örn. `com.util`) iki farklı modülde yer almasıdır. Modül sistemi bir paketin tek bir modüle ait olmasını şart koştuğu için bunu **başlatma anında `java.lang.LayerInstantiationException`** veya derleme hatasıyla reddeder. Eski kütüphanelerde sık görülür ve migrasyonun en zorlu adımlarındandır; çözümü paketleri yeniden adlandırmak veya ilgili jar'ları birleştirmektir.

📌 **Sık yapılan hata:** Aynı paketi iki modülde bulundurmanın çalışacağını sanmak. Modül sistemi buna izin vermez.

🔗 [7.2 §2 Split package sorunu](7.2-derleme-ve-migrasyon.md)

### Soru 6 — `public` bir sınıfa başka modülden erişilemiyor olması nasıl mümkün?

**Kısa cevap:** **Sınıf `public` olsa da paketi `exports` edilmemişse, ona yalnızca kendi modülü erişebilir.**

**Ayrıntı:** Java 9'dan beri `public` tek başına "her yerden erişilebilir" demek değildir. `public` + paket `exports` edilmemiş → yalnızca kendi modülü erişir. `public` + `exports p to m` → yalnızca `m` erişir. `public` + `exports p` → herkes erişir. Buna "public ama exported değil" durumu denir ve modül sisteminin özüdür: erişim artık hem erişim belirleyicisine hem de paketin dışa açılıp açılmadığına bağlıdır.

📌 **Sık yapılan hata:** `public`'in tek başına dış modüllerden erişim garantilediğini sanmak. Paket `exports` edilmemişse `public` sınıf dışarıdan görünmez.

🔗 [7.2 §3 Erişim kontrolünün yeni katmanı](7.2-derleme-ve-migrasyon.md)
