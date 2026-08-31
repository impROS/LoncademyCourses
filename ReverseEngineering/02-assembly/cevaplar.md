# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 01 Register'lar ve temel komut seti

### Soru 1 — `mov eax, 0` ile `xor eax, eax` aynı işi mi yapar? Derleyici neden ikincisini seçer?

**Kısa cevap:** **Evet, ikisi de `rax`'i tamamen sıfırlar.** Derleyici `xor eax, eax`'i **daha kısa/hızlı** olduğu için seçer.

**Ayrıntı:** `eax`'e yazmak `rax`'in üst 32 bitini de sıfırladığından her iki komut da 64-bitlik register'ı tümüyle 0 yapar. `xor eax, eax` bir XOR işlemi değil, sıfırlamanın standart **deyimi**dir; `mov rax, 0` yerine derleyici bunu kullanır çünkü kodlaması daha kısa ve daha hızlıdır.

📌 **Sık yapılan hata:** `xor eax, eax` görünce şifreleme/gizleme sanmak. Çoğu zaman sadece register'ı sıfırlıyordur.

🔗 [01 §3 Aritmetik ve mantık](01-register-komut.md)

### Soru 2 — `al`'e yazmak `rax`'in üst 56 bitini etkiler mi?

**Kısa cevap:** **Hayır.** Sadece alt 8 bit değişir; üst 56 bit korunur.

**Ayrıntı:** `al`, `rax`'in en alt 8-bitlik dilimidir. `eax`'e yazma üst 32 biti sıfırlarken, `al`'e yazma bu davranışı **göstermez**: yalnızca son byte güncellenir, üstteki 56 bit ne idiyse öyle kalır. `rax` (64) → `al` (alt 8), aradaki 56 bit dokunulmaz.

📌 **Sık yapılan hata:** `al`'i de `eax` gibi sanıp "üst kısım sıfırlandı" demek. Sadece `eax`/`e..` yazımı üstü sıfırlar, `al` sıfırlamaz.

🔗 [01 §1 Register haritası — 64/32/16/8 bit parçalar](01-register-komut.md)

### Soru 3 — `lea rax, [rdi+rdi*4]` sonucu nedir?

**Kısa cevap:** **`rax = rdi*5`** (bellek okumaz, sadece aritmetik).

**Ayrıntı:** `lea` köşeli parantezdeki adresi **hesaplar** ama belleğe dokunmaz. Formül: `rdi + rdi*4 = rdi*(1+4) = rdi*5`. Sonuç doğrudan `rax`'e yazılır; bu, `lea`'nın "hızlı çarpma" kullanımıdır.

📌 **Sık yapılan hata:** Köşeli paranteze bakıp bellekten okuma sanmak. `lea` adı görülünce erişim değil, adres/aritmetik hesabı yapılır.

🔗 [01 §4 `lea` — adres hesaplama](01-register-komut.md)

### Soru 4 — `mov rax, [rbx]` ile `lea rax, [rbx]` farkı?

**Kısa cevap:** `mov` `rbx`'in **gösterdiği bellekteki veriyi** getirir; `lea` `rbx`'in **değerini (adresi)** getirir.

**Ayrıntı:** Aynı yazılış, zıt iş. `mov rax, [rbx]` → `rbx`'in işaret ettiği adresteki 8 byte'ı okur (dereference). `lea rax, [rbx]` → hiçbir okuma yapmaz, sadece `rbx`'in içindeki sayıyı `rax`'e kopyalar. Yani `lea rax, [rbx]` pratikte `mov rax, rbx` ile aynı sonucu verir.

📌 **Sık yapılan hata:** Köşeli parantezin varlığına bakıp ikisini de "bellek erişimi" saymak. Ayrımı belirleyen komut adıdır: `mov`=veri, `lea`=adres.

🔗 [01 §4 `lea` — adres hesaplama](01-register-komut.md)

### Soru 5 — `call` komutu stack'e ne koyar, `ret` ne çeker?

**Kısa cevap:** `call` **dönüş adresini** push eder; `ret` o dönüş adresini **çeker** (pop) ve oraya atlar.

**Ayrıntı:** `call hedef` iki iş yapar: bir sonraki komutun adresini (dönüş adresi) stack'e iter, sonra `hedef`'e atlar. `ret` stack'in tepesindeki bu adresi pop edip oraya döner. Bu ikili fonksiyonların girip çıkmasını sağlar. (`jmp` ise stack'e bir şey koymaz, sadece atlar.)

📌 **Sık yapılan hata:** `call`'ın atladığı hedefi stack'e koyduğunu sanmak. Stack'e konan, dönülecek yer olan **dönüş adresi**dir.

🔗 [01 §5 `push`/`pop`/`call`/`ret`](01-register-komut.md)

---

## 02 Bellek erişimi, adresleme, LEA, flag'ler

### Soru 1 — `[rbx+rsi*8]` muhtemelen hangi tip diziye erişiyor? Neden 8?

**Kısa cevap:** **pointer/`long` dizisi.** Ölçek 8, eleman boyutunun 8 byte olduğunu gösterir.

**Ayrıntı:** Adresleme formülü `[taban + indeks*ölçek + offset]`; burada `rbx` taban, `rsi` indeks, `*8` ölçek. Ölçek her zaman eleman **boyutunu** yansıtır: `*1`=char, `*4`=int, `*8`=pointer/`long`. 8 byte'lık elemanlar 64-bitlik değerler olduğundan bu bir pointer veya `long` dizisidir.

📌 **Sık yapılan hata:** `*8`'i `*4` ile karıştırıp `int` dizisi demek. `*4`=int, `*8`=pointer/long; ölçek eleman tipini ele verir.

🔗 [02 §1 Genel adresleme formülü](02-bellek-flag.md)

### Soru 2 — `lea rax, [rbp-0x30]` ile `mov rax, [rbp-0x30]` sonuçları nasıl farklı?

**Kısa cevap:** `lea` `rbp-0x30` **adresini** (bir pointer) verir; `mov` o adresteki **veriyi** okur.

**Ayrıntı:** `lea rax, [rbp-0x30]` belleğe dokunmadan `rbp-0x30` sayısını `rax`'e yazar — yani yerel bir tampon/buffer'ın adresini üretir (genelde `scanf`/`strcpy` öncesi argüman hazırlama). `mov rax, [rbp-0x30]` ise o yerel değişkenin **içindeki değeri** `rax`'e getirir.

📌 **Sık yapılan hata:** İkisini de bellek okuması sanmak. `lea` adres üretir (okumaz), `mov [ ]` veriyi okur.

🔗 [02 §2 LEA'yı derinleştir — üç kullanımı](02-bellek-flag.md)

### Soru 3 — `cmp rax, 0` sonrası ZF=1 ise ne çıkarım yaparsın?

**Kısa cevap:** **`rax == 0`.**

**Ayrıntı:** `cmp a, b` aslında `a - b` hesaplar, sonucu atar, flag'leri kurar. `cmp rax, 0` → `rax - 0 = rax`. ZF (Zero Flag) = 1 sonucun sıfır olduğunu, yani `rax - 0 = 0` → `rax = 0` olduğunu söyler. ZF eşitliğin/sıfırlığın lambasıdır.

📌 **Sık yapılan hata:** ZF ile SF'yi karıştırmak. ZF=1 "sonuç sıfır/eşit" demektir; negatiflik için bakılan flag SF'dir.

🔗 [02 §3 Flag register (RFLAGS)](02-bellek-flag.md)

### Soru 4 — `test rax, rax` ne için kullanılır?

**Kısa cevap:** **`rax` sıfır mı** diye kontrol etmek için (NULL/0 kontrolü).

**Ayrıntı:** `test a, b` aslında `a AND b` hesaplar, sonucu atar, flag kurar. `test rax, rax` → `rax AND rax = rax`; `rax` sıfırsa ZF=1 olur. Bu, "malloc NULL mı döndü", "string sonu mu", "pointer 0 mı" gibi sıfır kontrollerinin standart kalıbıdır ve genelde arkasından `je`/`jne` gelir.

📌 **Sık yapılan hata:** `test rax, rax`'i iki farklı değer karşılaştırması sanmak. Aynı register'la yapıldığından tek amacı "sıfır mı" sorusudur.

🔗 [02 §4 `cmp` ve `test` — flag üretenler](02-bellek-flag.md)

### Soru 5 — `mov dword [rax], 1` neden `dword ptr` belirtir?

**Kısa cevap:** Sabit (immediate) yazılırken assembly **kaç byte** yazacağını bilemez; `dword` bunu 4 byte olarak belirtir.

**Ayrıntı:** Hedef bir register olsaydı genişlik ondan bellidir (`al`=byte, `eax`=dword). Ama `[rax]` bellek operandı ve kaynak sadece `1` sabiti olduğunda genişlik belirsizdir — `mov [rax], 1` kaç byte'a (1/2/4/8) yazacağını söylemez. `dword ptr` bunu 4 byte olarak netleştirir.

📌 **Sık yapılan hata:** Belirteci gereksiz sanmak. Register operandında gerek yoktur, ama belleğe **sabit** yazarken genişlik belirteci şarttır.

🔗 [02 §5 Bellek genişliği belirteçleri](02-bellek-flag.md)

---

## 03 Kontrol akışı: jump, cmp, döngü, if/switch

### Soru 1 — `je` hangi flag'e bakar? `jne`?

**Kısa cevap:** `je` **ZF=1** ise atlar (eşit); `jne` **ZF=0** ise atlar (eşit değil).

**Ayrıntı:** İkisi de bir `cmp`/`test` sonrası ZF (Zero Flag) okur. `cmp a, b` sonrası eşitlikte sonuç sıfır olur ve ZF=1 → `je`/`jz` atlar. Eşitsizlikte ZF=0 → `jne`/`jnz` atlar. İşaret (signed/unsigned) bu ikisi için önemsizdir, salt eşitlik/sıfırlık bakılır.

📌 **Sık yapılan hata:** `je`'yi SF (sign) ile ilişkilendirmek. `je` yalnızca ZF'ye bakar; SF negatiflik içindir.

🔗 [03 §1 Koşulsuz ve koşullu atlama](03-kontrol-akisi.md)

### Soru 2 — `jg` ile `ja` arasındaki fark ne, hangisi işaretli?

**Kısa cevap:** İkisi de "büyükse atla" der ama `jg` **işaretli (signed)**, `ja` **işaretsiz (unsigned)** karşılaştırma içindir.

**Ayrıntı:** `jg` (greater) SF/OF/ZF flag'lerini işaretli mantıkla okur; `ja` (above) CF/ZF'yi işaretsiz mantıkla okur. Fark kritiktir: `0xFFFFFFFF` signed'da −1 (küçük), unsigned'da çok büyük bir sayıdır. Kodda hangisinin kullanıldığı, karşılaştırılan verinin işaretli mi işaretsiz mi olduğunu ele verir (decompiler tip tahmininin kaynağı).

📌 **Sık yapılan hata:** İkisini eşdeğer görüp işaret farkını atlamak. `jg/jl` signed, `ja/jb` unsigned — aynı sayı iki atlama için farklı sonuç verir.

🔗 [03 §1 Koşulsuz ve koşullu atlama](03-kontrol-akisi.md)

### Soru 3 — Kaynakta `if (x==5)` iken assembly'de neden `jne` görürsün?

**Kısa cevap:** Koşullu atlama genelde kaynak koşulunun **tersini** taşır: "şart tutmuyorsa gövdeyi atla" daha verimlidir.

**Ayrıntı:** `if (x==5) { ... }` derlenirken `cmp` ile `x` 5'e kıyaslanır, sonra `jne .else` konur — yani "eşit **değilse** else'e atla". Şart tuttuğunda (eşitse) atlama yapılmaz ve gövde doğal akışta çalışır. Bu yüzden kaynaktaki eşitlik kontrolü, assembly'de eşitsizlik atlaması olarak görünür.

📌 **Sık yapılan hata:** `jne`'yi görüp kaynağı da "eşit değilse" diye okumak. Atlamanın koşulu, kaynak koşulunun tersidir; bu ters çevirmeyi kaçırma.

🔗 [03 §2 if / else kalıbı](03-kontrol-akisi.md)

### Soru 4 — Bir döngüyü disassembly'de nasıl tanırsın?

**Kısa cevap:** **Geriye (daha küçük/düşük bir adrese) doğru yapılan atlamadan.** Geri atlama = döngü.

**Ayrıntı:** Döngü, gövdeyi tekrar çalıştırmak için akışı yukarı taşımak zorundadır. Tipik `for`/`while` kalıbında sonda bir `cmp` (`i < n`) ve arkasından **düşen bir adrese** `jl`/`jne` gibi koşullu atlama bulunur; adres numarası azalan bir atlama görürsen orada dönen bir yapı vardır. (İleri atlama ise genelde if/else'tir.)

📌 **Sık yapılan hata:** `-O2`/`-O3`'te "temiz for" beklemek. Optimize kodda döngü açılmış (unroll) ya da sayaç register'da olabilir; yine de geriye atlamayı ve sayaç artışını ararsın.

🔗 [03 §3 Döngü (for / while)](03-kontrol-akisi.md)

### Soru 5 — `jmp [rax*8 + 0x4020]` sana ne söyler?

**Kısa cevap:** Bu bir **switch / jump table**'dır (dolaylı, indeksli atlama).

**Ayrıntı:** `jmp [tablo + reg*8]` kalıbı, değeri indeks yapıp bir adres tablosundan (`0x4020` taban) doğrudan case hedefine atlar; `*8` her tablo girişinin 8 byte'lık bir adres olmasındandır. Bu yapı neredeyse her zaman küçük/yoğun bir `switch`'tir. Seyrek/aralıklı case'lerde derleyici bunun yerine art arda `cmp`+`je` zinciri (if/else if) üretir.

📌 **Sık yapılan hata:** Bu dolaylı atlamayı sıradan bir `jmp` sanmak. `[tablo+reg*8]` biçimi switch işaretidir; `cmp` zinciriyle karıştırma.

🔗 [03 §4 switch — atlama tablosu (jump table)](03-kontrol-akisi.md)

---

## 04 Fonksiyon çağrısı: System V ABI, stack frame, argümanlar

### Soru 1 — 4. argüman hangi register'da geçer? Dönüş değeri nerede?

**Kısa cevap:** 4. argüman **`rcx`**; dönüş değeri **`rax`** (32-bit için `eax`).

**Ayrıntı:** System V AMD64 ABI'de ilk 6 tamsayı/pointer argüman sırayla `rdi, rsi, rdx, rcx, r8, r9`'da geçer — sıradaki 4. eleman `rcx`'tir. 7. ve sonrası stack'e konur. Fonksiyonun döndürdüğü değer `rax`'te taşınır.

📌 **Sık yapılan hata:** Argüman sırasını Windows'la karıştırmak. Windows x64 farklıdır (`rcx, rdx, r8, r9`); bu sıra Linux/System V içindir.

🔗 [04 §1 Argüman geçişi — 6 register kuralı](04-fonksiyon-abi.md)

### Soru 2 — `push rbp; mov rbp, rsp` ne işe yarar?

**Kısa cevap:** Fonksiyon **prologue'u**: eski frame tabanını saklar ve yeni frame tabanını kurar.

**Ayrıntı:** `push rbp` çağıranın frame taban işaretçisini (eski `rbp`) stack'e kaydeder; `mov rbp, rsp` yeni frame'in tabanını mevcut stack tepesine ayarlar. Böylece fonksiyon boyunca yerel değişkenler `[rbp-...]`, argüman/dönüş adresi `[rbp+...]` ile sabit offset'lerden okunabilir. Çoğu zaman ardından `sub rsp, N` ile yerel değişkenlere yer açılır.

📌 **Sık yapılan hata:** `-O2`'de `rbp` frame pointer olarak kullanılmayabilir (`-fomit-frame-pointer`). "`rbp` yok" demek "frame yok" demek değildir.

🔗 [04 §3 Stack frame: prologue ve epilogue](04-fonksiyon-abi.md)

### Soru 3 — `leave` hangi iki komuta eşdeğerdir?

**Kısa cevap:** **`mov rsp, rbp; pop rbp`**.

**Ayrıntı:** `leave` fonksiyon epilogue'unda frame'i geri sarar: önce `mov rsp, rbp` ile stack tepesini frame tabanına getirir (yerel alanı serbest bırakır), sonra `pop rbp` ile çağıranın eski `rbp`'sini geri yükler. `leave` tek başına dönmez; hemen ardından gelen `ret` dönüş adresini çeker.

📌 **Sık yapılan hata:** `leave`'in dönüşü de yaptığını sanmak. Dönüş ayrı bir `ret` ile olur; `leave` yalnızca frame'i toplar.

🔗 [04 §3 Stack frame: prologue ve epilogue](04-fonksiyon-abi.md)

### Soru 4 — `call strcmp` öncesi hangi register beklenen parolayı taşıyabilir?

**Kısa cevap:** **`rsi`** (2. argüman).

**Ayrıntı:** `strcmp(a, b)` çağrısında 1. argüman `rdi`, 2. argüman `rsi`'dir. Bir parola kontrolünde `rdi` genelde kullanıcı girdisi, `rsi` ise karşılaştırılan sabit string — yani **beklenen parola** olur. `call strcmp` öncesi `rsi`'ye ne konduğuna (örn. `lea rsi, [rip+0x...]`) bakarak gizli parolayı doğrudan bulabilirsin.

📌 **Sık yapılan hata:** İki argümanı ters okumak. `rdi` 1., `rsi` 2. argümandır; beklenen parola sabit olan ikinci argümandadır.

🔗 [04 §5 Yaygın libc çağrılarını tanımak](04-fonksiyon-abi.md)

### Soru 5 — Neden derleyici argümanları çoğu zaman `[rbp-...]`'e kopyalar?

**Kısa cevap:** Argüman register'ları **caller-saved**'dır; bir `call`'dan sonra değerleri **bozulmuş** olabilir. Kopyalamak onları güvene alır.

**Ayrıntı:** `rdi, rsi, rdx, rcx, r8–r11` gibi register'lar volatile (caller-saved) sayılır; fonksiyon içinde başka bir `call` yapıldığında bu register'lar korunmayabilir. Derleyici argümanları fonksiyon başında yerel değişkenlere (`[rbp-...]`) kopyalayarak sonraki çağrılardan sonra da erişilebilir kılar. Bu yüzden `[rbp-...]` alanları genelde argümanların yerel birer kopyasıdır.

📌 **Sık yapılan hata:** "Argümanı `rdi`'ye koydum, `call` sonrası hâlâ orada" varsaymak. Argüman register'ları caller-saved olduğundan çağrı sonrası korunmuş kabul edilemez.

🔗 [04 §2 Caller-saved vs callee-saved](04-fonksiyon-abi.md)
