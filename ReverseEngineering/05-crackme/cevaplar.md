# 05 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 01 Lisans kontrolü mantığını bulmak ve kırmak

### Soru 1 — Bir crackme'nin türünü nasıl anlarsın (sabit mi dönüşümlü mü)?

**Kısa cevap:** **`strings`/xref'e bakarsın:** parola düz gömülüyse sabit parola; string yoksa ama kodda bir dönüşüm-döngüsü ve `cmp ..., <sabit>` varsa dönüşümlüdür.

**Ayrıntı:** İlk iş "hangi türle karşı karşıyayım" sorusudur. `strings` çıktısında beklenen parolayı doğrudan görüyorsan (örn. `sunshine42`) tür **sabit parola**dır — oku ve geç. Parola yoksa, girdi bir işlemden (XOR, toplam, hash) geçirilip sonucu bir sabitle karşılaştırılıyorsa (örn. bir toplama döngüsü + `cmp ..., 0x290`) tür **dönüşümlü**dür — keygen adayıdır. Diğer türler seri/anahtar, bayrak/flag ve zaman/deneme kısıtıdır; çoğu gerçek crackme bunların karışımıdır.

📌 **Sık yapılan hata:** Parolayı "tahmin" etmeye çalışmak. Doğru refleks doğrulama kodunu okuyup türü belirlemek; beklenen değer tahmin edilmez, okunur veya hesaplanır.

🔗 [01 §1 Crackme türleri](01-crackme-mantik.md)

### Soru 2 — Doğrulama noktasını bulmanın üç yolu nedir?

**Kısa cevap:** **String→xref, import(`strcmp`/`memcmp`)→xref, ve dinamik yakalama (`break strcmp`).**

**Ayrıntı:** Aradığın tek şey "Correct/Wrong" kararının verildiği karşılaştırmadır. (1) **String→xref** (statik, en hızlı): "Correct!"/"Wrong" string'ine xref alıp karar fonksiyonuna gidersin. (2) **Import→xref:** `strcmp`/`memcmp`/`strncmp` çağrılarına bakıp karşılaştırılan değerleri bulursun. (3) **Dinamik yakalama:** karşılaştırmaya (`break strcmp`) breakpoint koyup çalışırken beklenen değeri okursun. Karar noktasını bulunca üç seçeneğin olur: oku, hesapla, zorla.

📌 **Sık yapılan hata:** Sadece tek bir yola güvenmek. String yoksa xref boş çıkar; o zaman import'a ya da dinamik yakalamaya geçersin — üç yol birbirini tamamlar.

🔗 [01 §2 Doğrulama noktasını bulma](01-crackme-mantik.md)

### Soru 3 — "Correct" dalını assembly'de nasıl tanırsın?

**Kısa cevap:** **Karşılaştırmanın başarılı (eşit) olduğu koldur** — örn. `test eax, eax; jne .wrong` kalıbında, `jne` alınmadığında devam eden dal.

**Ayrıntı:** Tipik kalıp: `call strcmp` → `test eax, eax` (sonuç sıfır mı, yani eşit mi) → `jne .wrong`. Karşılaştırmanın sonucu (flag) hangi dala gidileceğini belirler; `jne` eşit **değilse** Wrong'a atlar. Dolayısıyla "Correct" dalı, atlamanın alınmadığı — eşitken düşülen — koldur ve ardından `msg_correct`/`puts` gelir.

📌 **Sık yapılan hata:** "Correct" dalını her zaman `je`'nin peşinde sanmak. Bazı crackme'ler kararı **ters** kurar (yanlışta devam, doğruda `exit`); dalların ne yaptığını okumadan varsayma.

🔗 [01 §3 "Correct" dalını tanımak](01-crackme-mantik.md)

### Soru 4 — Bir dönüşümün "tersine çevrilebilir" olması ne demek, neden önemli?

**Kısa cevap:** **Sonuçtan girdiyi geri hesaplayabilmek demektir; önemlidir çünkü mümkünse keygen yazılır, değilse patch/brute-force'a yönelirsin.**

**Ayrıntı:** Girdi bir dönüşüm zincirinden geçer: `girdi → dönüşüm 1 → dönüşüm 2 → karşılaştırma`. Bir dönüşüm tersine çevrilebilirse hedeften geriye doğru yürüyüp geçerli girdiyi üretebilirsin: `^0x2a`'nın tersi yine `^0x2a` (XOR kendi tersidir), `+k`'nın tersi `-k`'dır. Tek yönlü bir hash tersine çevrilemez; o zaman ya girdiyi brute-force edersin ya da kontrolü patch'lersin. Refleks: "Dönüşüm tersine çevrilebilir mi?" — evetse keygen, hayırsa zorla.

📌 **Sık yapılan hata:** Her dönüşümü tersine çevrilebilir sanmak. Hash gibi tek yönlü adımlarda keygen matematiği çökeriz; orada strateji patch veya brute-force olmalı.

🔗 [01 §4 Girdi akışını haritalamak](01-crackme-mantik.md)

### Soru 5 — Oku/hesapla/zorla stratejileri arasında nasıl seçim yaparsın?

**Kısa cevap:** **En ucuzdan başla:** parola gömülüyse **oku**; dönüşüm tersine çevrilebilirse **hesapla** (keygen); karar tek dala bağlıysa **zorla** (patch); sadece bir kez geçmek yetiyorsa GDB'de `set`.

**Ayrıntı:** Karar ağacı maliyet sırasıyla ilerler. Önce **oku** (en ucuz): parola `.rodata`'da düz gömülüyse `strings`/xref yeter. Olmuyorsa **hesapla** (en zarif): dönüşüm tersine çevrilebiliyorsa keygen yazarsın. O da zorsa (tek yönlü hash, karmaşık kontrol) **zorla** (en kaba ama etkili): `jne→je` veya nop ile kararı kalıcı patch'lersin. Yalnız tek seferlik geçmen yetiyorsa dinamik `set` en hızlısıdır. Anti-debug varsa önce onu atlatır, sonra saldırırsın.

📌 **Sık yapılan hata:** Tek seferlik geçmek yeterken kalıcı byte patch'e girişmek (ya da tersi). Amaç kalıcılığı belirler: kalıcı "cracked" kopya istiyorsan statik patch, bir oturum yetiyorsa GDB `set`.

🔗 [01 §5 Üç saldırı stratejisinin seçimi](01-crackme-mantik.md)

---

## 02 Keygen yazmak: algoritmayı tersine çevirmek

### Soru 1 — Keygen ile "parola bulma" arasındaki fark nedir?

**Kısa cevap:** **Parola bulma tek seferliktir; keygen istediğin kadar geçerli anahtar *üreten* bir programdır.**

**Ayrıntı:** Doğrulama bir fonksiyondur: `dogru_mu(girdi) → evet/hayır`. Keygen bunun tersidir: `uret() → dogru(girdi)`. Bir parolayı bulmak sadece o örneği çözer; keygen ise algoritmayı gerçekten anlayıp geçerli anahtarları sistematik olarak üretir — algoritmayı anladığının kanıtıdır. Doğrulamayı adım adım okur, her adımı tersine çevirir ve birleştirirsin.

📌 **Sık yapılan hata:** Dönüşümü yaklaşık anlayıp keygen'i deneme-yanılmayla yazmak. Doğrusu her adımı **kesin** çıkarıp tam tersini kodlamaktır.

🔗 [02 §1 Keygen'in üç adımı](02-keygen.md)

### Soru 2 — `x + 7` dönüşümünün keygen'deki tersi nedir? `x ^ 0x2a`'nınki?

**Kısa cevap:** **`x + 7`'nin tersi `x - 7`; `x ^ 0x2a`'nın tersi yine `x ^ 0x2a`.**

**Ayrıntı:** Keygen'de karşılaştırma hedefinden geriye yürürsün. Toplama ile çıkarma birbirinin tersidir: doğrulama `x + 7` yapıyorsa, geçerli girdiyi bulmak için sonuçtan `- 7` yaparsın. XOR ise kendi tersidir: aynı anahtarla ikinci kez XOR'lamak orijinali geri verir, dolayısıyla `x ^ 0x2a`'nın tersi ayrı bir işlem değil, yine `x ^ 0x2a`'dır.

📌 **Sık yapılan hata:** XOR'un tersini çıkarma sanmak. XOR'un tersi aynı XOR'dur; ayrı bir "geri alma" işlemi aramak yanlış yola götürür.

🔗 [02 §2 Tersine çevrilebilir işlemler ve tersleri](02-keygen.md)

### Soru 3 — Toplam kısıtını tek serbest karakterle nasıl çözersin?

**Kısa cevap:** **n−1 karakteri sabitler, sonuncuyu denklemi tamamlayacak şekilde hesaplarsın:** `son = (hedef − Σöncekiler)` (dönüşüm varsa tersini de uygula).

**Ayrıntı:** `Σ(dönüşüm(b[i])) = hedef` tek denklemli bir sistemdir, sonsuz çözümü vardır; sen bir tanesini üretirsin. İlk n−1 karakteri sabitle (örn. hepsi `'A'`), son karakteri denklemi tutturacak biçimde hesapla. Örneğin dönüşüm `b[i] ^ 0x2a` ve hedef `1000` ise: `son = (1000 − Σöncekiler) ^ 0x2a`. Sonra `son`'un yazdırılabilir ASCII aralığında (`0x20`–`0x7e`) olduğunu kontrol et; aralık dışıysa ara karakterlerden birini değiştirip toplamı yeniden dengele.

📌 **Sık yapılan hata:** Hesaplanan son karakterin geçerli aralıkta olup olmadığını kontrol etmemek. Değer `0x7e`'yi aşarsa (ör. taşarsa) seri geçersizdir; bir ara karakteri ayarlayıp dengelemek gerekir.

🔗 [02 §3 Toplama tabanlı doğrulamayı çözmek](02-keygen.md)

### Soru 4 — Hangi işlemler tersine çevrilemez ve o zaman ne yaparsın?

**Kısa cevap:** **Bit kaybı olan/kayıplı işlemler:** `x & 0xF0` (maskeleme), bölme, mod, tek yönlü hash. O zaman ya o adım belirleyici değilse atlarsın ya da o değeri **brute-force** edersin.

**Ayrıntı:** `x & 0xF0` alt 4 biti tamamen yok eder — hangi girdi olduğu geri getirilemez. Bölme, mod ve hash de bilgi kaybettiği için tersine çevrilemez. Kayıplı bir adımla karşılaşınca iki çıkış vardır: (1) o adım sonucu belirlemiyorsa (serbest bırakılabiliyorsa) atlanır; (2) belirliyorsa, o değer için küçük arama uzayını brute-force edersin. Arama uzayı büyükse keygen matematiği şarttır, brute-force pratik değildir.

📌 **Sık yapılan hata:** Kayıplı bir işlemi tersine çevirmeye çalışıp keygen'i zorlamak. `& mask`/bölme/hash geri alınamaz; onları brute-force et ya da o biti kısıtsız bırak.

🔗 [02 §2 Tersine çevrilebilir işlemler ve tersleri (tuzak)](02-keygen.md)

### Soru 5 — Keygen "Correct" vermiyorsa ilk kontrol edeceğin şey nedir?

**Kısa cevap:** **Kaçırdığın bir kısıt vardır** — algoritmayı yeniden okuyup tüm karar zincirini (uzunluk, konum, checksum) listelersin.

**Ayrıntı:** Gerçek crackme'ler birden çok kısıt koyar: uzunluk (`strlen==8`), karakter sınıfı, toplam/checksum, konum bazlı (`b[0]=='K'`). Keygen hepsini **aynı anda** sağlamalıdır; birini unutmak keygen'i sessizce bozar — toplam doğru olsa bile "Correct" gelmez. İlk refleks: Ghidra'da tüm karar zincirini (her `&&`, her `cmp`) çıkar ve keygen'de her kısıta bir satır karşılık geldiğinden emin ol. `python3 keygen.py | ./crackme` ile kanıtla.

📌 **Sık yapılan hata:** Sadece toplam/checksum'a odaklanıp uzunluk veya konum kısıtını atlamak. Toplam tutuyor ama "Wrong" görüyorsan neredeyse her zaman başka bir kısıt sağlanmıyordur.

🔗 [02 §4 Kısıtları birleştirmek (çoklu koşul)](02-keygen.md)

---

## 03 Binary patching ve anti-debug'a ilk bakış

### Soru 1 — `jne`'yi `je`'ye çevirmek için hangi byte'ı nasıl değiştirirsin?

**Kısa cevap:** **`jne` (kısa) = `0x75`, `je` (kısa) = `0x74`; tek byte'ı `75`→`74` yaparsın.**

**Ayrıntı:** Kısa koşullu atlamalar tek byte opcode'dur: `jne rel8` = `0x75`, `je rel8` = `0x74`. Karar komutunun dosya offset'indeki `75` byte'ını `74` yaparak koşulu tersine çevirirsin; böylece eşitlik durumunda alınan dal değişir. (Karşılaştırma için `nop` = `0x90`.)

📌 **Sık yapılan hata:** `0x74` ile `0x75`'i karıştırmak. `je`=`0x74`, `jne`=`0x75`; yanlış byte'ı yazmak kararı istediğinin tersine çevirir.

🔗 [03 §2 Karar komutunu değiştirmek](03-patching-antidebug.md)

### Soru 2 — Patch'te neden komut uzunluğunu korumak zorundasın?

**Kısa cevap:** **Farklı uzunlukta byte yazarsan sonraki komutları kaydırır ve binary'yi bozarsın.**

**Ayrıntı:** Makine kodu bitişik komutlardan oluşur; her komutun sabit bir byte uzunluğu vardır. `jne rel8` 2 byte'tır, onu yine 2 byte'lık bir şeyle değiştirmelisin: `je rel8` (2 byte) ya da iki `nop` (2×`0x90`). Daha kısa/uzun bir şey yazarsan ardından gelen tüm komutların hizası kayar ve program çöker. Ghidra'nın "Patch Instruction" özelliği bu hizalamayı senin için yapar.

📌 **Sık yapılan hata:** Atlamayı tek bir `nop` (1 byte) ile iptal etmeye çalışıp 2 byte'lık `jne`'nin yerini yarım bırakmak. Uzunluğu koru: 2 byte'lık atlama → 2 byte'lık patch.

🔗 [03 §2 Karar komutunu değiştirmek (tuzak)](03-patching-antidebug.md)

### Soru 3 — Dinamik (GDB) ve statik byte patch arasında ne zaman hangisini seçersin?

**Kısa cevap:** **Bir kez geçmek yetiyorsa ve kaynak binary'yi bozmak istemiyorsan dinamik (GDB `set`); kalıcı "cracked" kopya istiyorsan statik byte patch.**

**Ayrıntı:** Dinamik patch tek oturumluktur: GDB'de `set $eflags`/`set $rip`/register değiştirerek çalışırken kararı geçirirsin, dosya değişmez. Statik byte patch kalıcıdır: dosyadaki byte'ı (hex editör, `dd` veya Ghidra "Patch Instruction" + Export) değiştirir, artık her çalıştırmada geçen bir binary elde edersin. Seçim amaca bağlıdır — tek seferlik analiz mi, dağıtılabilir kalıcı kopya mı.

📌 **Sık yapılan hata:** Sadece bir kez geçmek yeterken statik patch'e girişip binary'yi riske atmak (ya da kalıcı kopya gerekirken GDB `set`'in oturum bitince kaybolduğunu unutmak). Amaç kalıcılığı belirler.

🔗 [03 §1 Patching'in üç yolu](03-patching-antidebug.md)

### Soru 4 — ptrace tabanlı anti-debug nasıl çalışır ve nasıl atlatılır?

**Kısa cevap:** **Program `ptrace(PTRACE_TRACEME)` ile kendini trace eder; bir debugger zaten bağlıysa bu ikinci trace başarısız olur ve program "izleniyorum" der. Atlatma: çağrının dönüşünü patch'lersin veya GDB'de `set` ile 0 döndürürsün.**

**Ayrıntı:** Bir process aynı anda yalnızca bir kez trace edilebilir. Program başta `ptrace(PTRACE_TRACEME,0,0,0)` çağırır; debugger yoksa başarılı olur, GDB gibi bir debugger varsa `-1` döner ve "Debugger detected!" yolu tetiklenir. Anti-debug özünde bir **karar**dır ("izleniyor muyum? → evet ise çık"). Diğer kararlar gibi import'ta `ptrace`'e xref alıp bulur, dönüşünü/kararını patch'ler ya da GDB'de sonucu `set` edersin.

📌 **Sık yapılan hata:** Anti-debug'ı "sihir" sanıp atlanamaz görmek. O da patch'lenebilir bir karardır; asıl kontrole geçmeden önce anti-debug'ı atlatmak refleks olmalı.

🔗 [03 §4 Anti-debug teknikleri ve atlatma](03-patching-antidebug.md)

### Soru 5 — Program GDB altında ve dışında farklı davranıyorsa ne düşünürsün?

**Kısa cevap:** **Anti-debug'dan şüphelenirsin** — program "izleniyor muyum?" diye kontrol ediyor ve GDB altında farklı bir yola sapıyordur.

**Ayrıntı:** Bir program debugger altında ve dışında **farklı** davranıyorsa, bu neredeyse her zaman bir anti-debug kontrolünün (ptrace, `/proc/self/status` TracerPid, rdtsc zamanlaması, `0xCC`/`int3` arama) tetiklendiği anlamına gelir. Anti-debug seni yanlış yöne de itebilir: debugger tespit edilince kasten "Correct!" yazıp aslında yanlış bir yola sapmak gibi. Refleks: davranış farkını gördüğünde anti-debug'ı bul ve önce onu patch'le/`set` ile geç, sonra asıl kontrolü analiz et.

📌 **Sık yapılan hata:** GDB altındaki farklı davranışı "debugger'ım bozuk" sanıp anti-debug'ı gözden kaçırmak. Davranış farkının kendisi anti-debug'ın en güçlü işaretidir.

🔗 [03 §4 Anti-debug teknikleri ve atlatma (tuzak)](03-patching-antidebug.md)
