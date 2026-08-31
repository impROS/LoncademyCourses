# 04 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 01 GDB + pwndbg temelleri

### Soru 1 — `step` ile `next` arasındaki fark bir `call` satırında ne olur?

**Kısa cevap:** **`step` çağrının içine girer, `next` üstünden atlar.** İkisi de kaynak satırı seviyesindedir; fark tam da bir `call`'da ortaya çıkar.

**Ayrıntı:** İkisi de bir kaynak satırı ilerletir. Fark, satır bir fonksiyon çağrısı içeriyorsa görünür: `step`/`s` çağrılan fonksiyonun **içine** dalar ve orada durur; `next`/`n` fonksiyonu çalıştırıp **dönüşünde** bir sonraki satırda durur — içini görmezsin. Komut seviyesindeki karşılıkları `stepi`/`si` (gir) ve `nexti`/`ni` (`call`'ı atla).

📌 **Sık yapılan hata:** İkisini eşdeğer sanıp ilgilenmediğin bir fonksiyonun içine `step` ile dalmak — akışta kaybolursun. İncelemek istemediğin çağrıda `next`/`ni` kullan.

🔗 [01 §4 Adımlama: step vs next](01-gdb-temel.md)

### Soru 2 — `break strcmp` ne işe yarar, argümanları nasıl okursun?

**Kısa cevap:** **Her `strcmp` çağrısında durdurur;** argümanları `x/s $rdi` ve `x/s $rsi` ile okursun.

**Ayrıntı:** `break strcmp` kütüphane fonksiyonuna breakpoint koyar, yani program karşılaştırma anında durur. System V ABI'de ilk iki argüman `$rdi` ve `$rsi`'dedir; `x/s $rdi` ve `x/s $rsi` ile iki string'i okursun — biri senin girdin, biri **beklenen parola** olur. Bu, gizli parolayı bulmanın en hızlı dinamik yoludur.

📌 **Sık yapılan hata:** `p $rdi` ile sadece adres sayısını görüp "parola bu mu" diye şaşmak. String'i görmek için dereference eden `x/s` gerekir.

🔗 [01 §3 Breakpoint — nerede durayım](01-gdb-temel.md)

### Soru 3 — `x/s $rdi` ile `p $rdi` arasındaki fark nedir?

**Kısa cevap:** **`p $rdi` register'daki sayıyı** verir; **`x/s $rdi` o sayıyı adres kabul edip gösterdiği string'i** okur.

**Ayrıntı:** `p $rax`/`p $rdi` register içeriğini decimal sayı olarak basar — bir işaretçiyse sadece adresin kendisidir. `x` (examine) ise belleğe erişir: `x/s $rdi`, `$rdi`'nin gösterdiği adresten başlayıp null'a kadar byte'ları string olarak yazar. Yani `p` değeri, `x/s` o değerin işaret ettiği veriyi verir.

📌 **Sık yapılan hata:** İşaretçi tutan bir register'ı `p` ile okuyup ham adresi "değer" sanmak. İçeriğe ulaşmak için `x` ile dereference et.

🔗 [01 §5 Register ve değer okuma](01-gdb-temel.md)

### Soru 4 — PIE bir binary'de neden sabit adrese breakpoint koymak sorunludur?

**Kısa cevap:** **PIE + ASLR yüzünden adresler her çalıştırmada değişir;** sabit adres tutmaz.

**Ayrıntı:** PIE (position independent executable) binary'lerde yükleme tabanı (base) her çalıştırmada ASLR ile kayar, dolayısıyla `break *0x401136` gibi sabit bir adres bir sonraki çalıştırmada başka bir yeri gösterir. Çözüm: taban değişse de sabit kalan **sembol ismi** kullanmak (`break main`), ya da program yüklenince gerçek tabanı öğrenip (pwndbg base'i gösterir) offset'i eklemek.

📌 **Sık yapılan hata:** Bir yerde işe yarayan sabit adresi ezberleyip her çalıştırmada aynı kalacağını sanmak. PIE'de güvenilir olan semboldür, çıplak adres değil.

🔗 [01 §3 Breakpoint (PIE tuzağı)](01-gdb-temel.md)

### Soru 5 — `si` ile bir `call printf`'e girersen ne olur, doğrusu ne?

**Kısa cevap:** **libc'nin içinde kaybolursun** — yüzlerce komut; doğrusu `ni` ile üstünden atlamak.

**Ayrıntı:** `si` (stepi) bir `call`'ın içine dalar. `call printf` gibi bir kütüphane çağrısına girersen kendi kodundan çıkıp libc'nin onlarca/yüzlerce komutluk iç dünyasına düşersin — ilgilenmediğin, zaman kaybettiren bir yer. İlgilenmediğin kütüphane çağrılarını `ni` ile atlar, sadece kendi kodunu `si` ile izlersin.

📌 **Sık yapılan hata:** Her çağrıya `si` ile dalıp libc/başka fonksiyonların içinde yolunu kaybetmek. Kural: kendi kodun `si`, dış çağrı `ni`.

🔗 [01 §4 Adımlama (si/ni seçimi)](01-gdb-temel.md)

---

## 02 Bellek/Stack inceleme, watch, canlı veri takibi

### Soru 1 — `x/8xg $rsp` ile `x/8xb $rsp` kaç byte okur, aralarındaki fark ne?

**Kısa cevap:** **`x/8xg` = 8 qword = 64 byte; `x/8xb` = 8 byte.** Fark, boyut harfindedir (`g`=8 byte, `b`=1 byte).

**Ayrıntı:** `x/[sayı][format][boyut]` biçiminde son harf birim boyudur: `b`=byte(1), `w`=word(4), `g`=giant/qword(8). Yani `x/8xg` sekiz adet 8-byte'lık qword (64 byte, tipik stack dökümü), `x/8xb` sekiz adet tek byte okur. Sayı aynı (8) olsa da okunan bellek 8 kat farklıdır. Ek olarak `xg` bir qword'ü tek sayı hâlinde doğru birleştirir; `xb` byte'ları ham (ters görünen) sırada verir.

📌 **Sık yapılan hata:** Boyut harfini atlamak/karıştırmak ve `g` ile beklediğinden kat kat fazla veri gelince şaşmak. İstediğin genişliğe göre `b`/`w`/`g` seç.

🔗 [02 §1 Belleği okumanın yolları](02-bellek-inceleme.md)

### Soru 2 — Watchpoint ile breakpoint arasındaki temel fark nedir?

**Kısa cevap:** **Breakpoint bir koda, watchpoint bir veriye kurulur.** Biri "şu satıra gelince", diğeri "şu değer değişince" durdurur.

**Ayrıntı:** Breakpoint belirli bir adrese/fonksiyona konur; akış oraya ulaşınca durur. Watchpoint ise bir değişkene/adrese kurulur ve o veri değiştiğinde (ya da okunduğunda) program **otomatik** durur — kodun neresinde olursa olsun. Bu yüzden watchpoint, "bu değer nerede değişiyor" sorusunu bulmanın en güçlü yoludur; breakpoint gibi yeri önceden bilmeni gerektirmez.

📌 **Sık yapılan hata:** Bir değişkenin ne zaman değiştiğini breakpoint'lerle elle adım adım aramak. Bunun otomatik aracı watchpoint'tir.

🔗 [02 §3 Watchpoint](02-bellek-inceleme.md)

### Soru 3 — `watch` ile `rwatch` ne zaman farklı davranır?

**Kısa cevap:** **`watch` değere yazılınca, `rwatch` değer okununca** durur. Fark, erişim türündedir.

**Ayrıntı:** `watch degisken` yalnızca değişkene **yazma** olduğunda tetiklenir; `rwatch degisken` değişken **okunduğunda** durur; `awatch` ise okuma **veya** yazma — her iki durumda da durur. Yani sadece okunup hiç değiştirilmeyen bir değeri `watch` yakalamaz, `rwatch` yakalar.

📌 **Sık yapılan hata:** Bir değerin nereden okunduğunu ararken `watch` koyup hiç durmaması. Okuma erişimini izlemek için `rwatch` (veya ikisi için `awatch`) gerekir.

🔗 [02 §3 Watchpoint](02-bellek-inceleme.md)

### Soru 4 — `bt` bir watchpoint durağında ne söyler?

**Kısa cevap:** **Çağrı zincirini** — bu değişimin hangi çağrı yoluyla, hangi kod satırından geldiğini.

**Ayrıntı:** Watchpoint izlenen değer değişince durur; `backtrace`/`bt` o anki çağrı yığınını (kim kimi çağırdı) döker. Böylece "değer değişti ama nerede" sorusunun cevabını — değişimi yapan fonksiyonu ve ona ulaşan zinciri — görürsün. Stripped binary'de isimler yerine adresler çıkar ama zincir yine okunur.

📌 **Sık yapılan hata:** `bt`'yi anlık register/değer dökümü sanmak. `bt` değer değil, **çağrı zincirini** gösterir; değeri `p degisken` ile okursun.

🔗 [02 §4 Backtrace ve akış takibi](02-bellek-inceleme.md)

### Soru 5 — Yerel bir değişkene watchpoint koymanın riski nedir?

**Kısa cevap:** **Kapsam (scope) sınırı:** fonksiyon bitince o yerel adres başka bir şeye ait olur, watchpoint yanıltıcı hâle gelir.

**Ayrıntı:** Yerel değişken stack'te fonksiyonun frame'inde yaşar. Fonksiyon dönünce o adres serbest kalır ve başka bir çağrının verisiyle dolar; watchpoint hâlâ o adresi izlediği için alakasız değişimlerde durabilir. Bu yüzden yerel değişken watch'u güvenilmezdir — global/heap adreslerinde daha güvenlidir. Ayrıca donanım watchpoint sayısı sınırlıdır (genelde ~4).

📌 **Sık yapılan hata:** Yerel bir değişkene watch koyup fonksiyon bittikten sonraki durakları da "o değişken değişti" diye yorumlamak. Kalıcı izleme için global/heap adresi seç.

🔗 [02 §3 Watchpoint (kapsam tuzağı)](02-bellek-inceleme.md)

---

## 03 Statik + dinamik birlikte: bir binary'yi baştan sona çözmek

### Soru 1 — "Bu string'i kim kullanıyor?" sorusunu hangi araçla, nasıl cevaplarsın?

**Kısa cevap:** **Statik (Ghidra) ile — xref (çapraz referans) alarak.** String'e xref, onu kullanan kodu gösterir.

**Ayrıntı:** "Kim kullanıyor" tüm kod yollarını gören bir sorudur; en iyi araç statiktir. Ghidra'da ilgili string'e xref alırsın, bu seni string'i kullanan fonksiyona (çoğu zaman doğrulama fonksiyonuna) götürür. Dinamik analiz anlık değer içindir; bir string'in tüm kullanıcılarını (hiç çalışmayan dallar dahil) statik gösterir.

📌 **Sık yapılan hata:** Bu tür "kim/nerede kullanıyor" sorusunu GDB'de çalıştırarak aramaya çalışmak. Referans haritası statiğin işidir; dinamik yalnızca o an çalışan yolu görür.

🔗 [03 §1 İş bölümü: hangi soruyu hangi araç](03-birlikte-analiz.md)

### Soru 2 — Ghidra'daki bir adrese GDB'de breakpoint koyunca tutmadı. İlk şüphen ne?

**Kısa cevap:** **PIE/ASLR.** Ghidra'nın gösterdiği adres yükleme tabanına göredir; GDB'de gerçek base farklıdır.

**Ayrıntı:** "Ghidra'daki adrese breakpoint koydum ama tutmadı" şikayeti neredeyse her zaman PIE/ASLR kaynaklıdır. Ghidra genelde `0x100000` tabanlı gösterir, çalışan programın base'i ise ASLR ile başka bir yerdedir; sabit adres yanlış yeri işaret eder. Çözüm: sembol kullan (`break main`), veya base'i öğrenip (`info proc mappings`/pwndbg) `base + offset` hesapla, ya da `break $rebase(0x1169)`.

📌 **Sık yapılan hata:** Adresi yanlış okudum sanıp Ghidra'yı tekrar tekrar kontrol etmek. Adres doğru; sorun taban kayması (PIE), sembol/rebase kullan.

🔗 [03 §3 Adres eşleme: Ghidra ↔ GDB](03-birlikte-analiz.md)

### Soru 3 — Statik ve dinamik analizi tek cümlede nasıl özetlersin?

**Kısa cevap:** **Statik "harita" ("ne yapıyor"), dinamik "keşif gezisi" ("şu an değeri ne");** ikisi dönüşümlü kullanılır.

**Ayrıntı:** Statikle nereye gideceğini planlar, tüm kod yollarını ve genel işleyişi görürsün; dinamikle o an gerçekte ne olduğunu — değişkenlerin gerçek değerini, dönüşümün çıktısını — gözlemlersin. Haritayla yolu çizer, gezide ne olduğunu doğrularsın; biri olmadan diğeri eksik kalır.

📌 **Sık yapılan hata:** Tek araçta inatlaşmak — statikte anlaşılmayan dönüşümü saatlerce okumak ya da GDB'de körlemesine adımlamak. Doğrusu takılınca araç değiştirmek.

🔗 [03 §Büyük fikir / §1 İş bölümü](03-birlikte-analiz.md)

### Soru 4 — Bir dönüşümü statikte çözemediğinde ne yaparsın?

**Kısa cevap:** **GDB'ye geç:** girdiyi ver, dönüşümü adımla, belleği gözünle oku.

**Ayrıntı:** Statikte karmaşık görünen (örn. bir XOR/aritmetik döngü) dönüşümü saatlerce çözmeye çalışmak yerine dinamiğe geçersin: bilinen bir girdi verir (`AAAA...`), döngüyü `ni` ile adımlar, her turda buffer'ı tekrar okuyup dönüşümün girdiyi neye çevirdiğini birebir görürsün. Statikte 2 saat sürecek şey GDB'de dakikalar alır (20 dakika kuralı: takılınca yaklaşımı/aracı değiştir).

📌 **Sık yapılan hata:** Aynı statik ekranda 20 dakikadan fazla inatlaşmak. Kafan karıştığı an dinamiğe geçip değeri "gözünle görmek" çok daha hızlıdır.

🔗 [03 §4 Ne zaman araç değiştirmeli](03-birlikte-analiz.md)

### Soru 5 — Standart çözüm akışının altı adımını sırala.

**Kısa cevap:** **Kimlik → Harita → Hipotez → Doğrula → Çöz → Kanıtla.** Bir daire değil, her turda hedefe yaklaşan bir spiral.

**Ayrıntı:** (1) **Kimlik:** `file`/`checksec`/`strings` ile 32/64-bit, koruma, ipucu string'ler. (2) **Harita:** Ghidra'da import+analyze, `main`/`entry` bul, string'e xref al, decompile'ı okunur yap. (3) **Hipotez:** statikten bir tahmin kur ("girdi şu döngüde işlenip `0x...` ile karşılaştırılıyor"). (4) **Doğrula:** Ghidra'nın gösterdiği adrese/fonksiyona GDB'de breakpoint koy, gerçek değerleri oku, hipotezi test et. (5) **Çöz:** beklenen değeri doğrudan gir, hesaplanıyorsa ters çevir (keygen) veya patch'le. (6) **Kanıtla:** bulduğun girdiyle çalıştır, "Correct!" gör.

📌 **Sık yapılan hata:** Adımları atlayıp doğrudan GDB'de adımlamaya başlamak — nereye breakpoint koyacağını bilmeden. Önce kimlik ve harita, sonra hipotez, sonra doğrulama.

🔗 [03 §2 Standart çözüm akışı (recipe)](03-birlikte-analiz.md)
