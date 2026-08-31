# 01 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 01 Sayı sistemleri, bit, byte, endianness

### Soru 1 — `0x2A` onlukta kaç? `mov eax, 0x2a` hangi sayıyı yüklüyor?

**Kısa cevap:** **42.** Komut `eax`'e 42 (yani `0x2A`) yükler.

**Ayrıntı:** Hex hane hesabı: `0x2A = 2×16 + 10 = 32 + 10 = 42`. `A` = 10 olduğu
için. `mov eax, 0x2a` doğrudan bu sabiti register'a koyar; sayı `0x` önekiyle
yazıldığı için hex'tir.

📌 **Sık yapılan hata:** `0x2A`'yı "yirmi bir" ya da "iki-on" gibi okumak.
Her hex hane 16'nın kuvvetidir, 10'un değil.

🔗 [01 §2 Hex neden her yerde](01-sayilar-bellek.md)

### Soru 2 — Bellekte byte'lar `90 78 56 34` (soldan sağa). Little-endian dword değeri nedir?

**Kısa cevap:** **`0x34567890`.**

**Ayrıntı:** x86-64 little-endian'dır: en düşük adresteki byte, sayının **en
düşük değerlikli** byte'ıdır. Byte'ları adres sırasıyla okursun (`90 78 56 34`),
sayıyı kurmak için **tersine** dizersin: `34 56 78 90` → `0x34567890`.

📌 **Sık yapılan hata:** Ham byte'ları düz okuyup `0x90785634` demek. Bellekteki
sıra ile sayının sırası little-endian'da terstir.

🔗 [01 §4 Endianness](01-sayilar-bellek.md)

### Soru 3 — `0x80` bir byte olarak signed kaç? Neden negatif?

**Kısa cevap:** **−128.** En üst bit 1 olduğu için negatiftir.

**Ayrıntı:** İşaretli byte two's complement ile okunur: en üst bit (MSB) işaret
bitidir. `0x80 = 1000 0000`, MSB = 1 → negatif. Değeri: bitleri ters çevir
(`0111 1111` = 127), 1 ekle (128), başına eksi koy → −128. `0x80` işaretli
byte'ın en küçük değeridir.

📌 **Sık yapılan hata:** `0x80`'i her zaman 128 sanmak. İşaretsiz okunursa 128,
işaretli okunursa −128 — aynı byte, iki yorum.

🔗 [01 §3 İşaretli mi işaretsiz mi](01-sayilar-bellek.md)

### Soru 4 — `'z'` ile `'a'` arasında kaç değer var? `'z'`'nin hex'i tahminen ne?

**Kısa cevap:** **26 harf (25 aralık).** `'a' = 0x61`, `'z' = 0x7A`.

**Ayrıntı:** ASCII'de küçük harfler ardışıktır: `'a' = 0x61 = 97`. Alfabe 26
harf olduğundan `'z' = 0x61 + 25 = 0x7A = 122`. İki değer arasındaki fark
`0x7A − 0x61 = 0x19 = 25`; harf sayısı ikisi dahil 26.

📌 **Sık yapılan hata:** `'A'` (büyük, `0x41`) ile `'a'` (küçük, `0x61`) karışır.
Aralarında `0x20` (32) fark vardır — büyük/küçük dönüşümü bu bittir.

🔗 [01 §5 ASCII](01-sayilar-bellek.md)

### Soru 5 — `dword` kaç byte, `qword` kaç byte? `eax` hangisi?

**Kısa cevap:** **dword = 4 byte, qword = 8 byte. `eax` bir dword'dür.**

**Ayrıntı:** Assembly sözlüğü: byte 1, word 2, dword 4, qword 8 byte. `rax`
64-bit (qword), `eax` onun alt 32-biti (dword), `ax` alt 16-bit (word), `al`
alt 8-bit (byte). Hepsi aynı fiziksel register'ın farklı genişlikte görünümü.

📌 **Sık yapılan hata:** Assembly'de "word"ü işlemci kelime boyu (64 bit)
sanmak. RE'de word = 16 bit; 64-bit olan qword'dür.

🔗 [01 §1 Bit, byte ve genişlikler](01-sayilar-bellek.md)

---

## 02 Bellek modeli: stack, heap, register, adres

### Soru 1 — `push rbx`'ten sonra `rsp` büyür mü küçülür mü? Neden?

**Kısa cevap:** **Küçülür.** Stack yüksek adresten alçağa doğru büyür.

**Ayrıntı:** x86-64'te stack "aşağı" büyür: yeni veri daha **düşük** adrese
konur. `push rbx` önce `rsp`'yi 8 azaltır, sonra `rbx`'i o adrese yazar. `pop`
tersini yapar: okur, sonra `rsp`'yi 8 artırır.

📌 **Sık yapılan hata:** "Stack büyüyor" deyip `rsp`'nin arttığını sanmak.
Stack büyürken `rsp` **azalır**; bu ters ilişki bütün stack okumasının temelidir.

🔗 [02 §3 Stack](02-bellek-modeli.md)

### Soru 2 — `[rbp-0x18]` neyi ifade eder — yerel değişken mi argüman mı?

**Kısa cevap:** **Yerel değişken.** `rbp`'den **negatif** offset yerel
değişkenleri gösterir.

**Ayrıntı:** Stack frame'de `rbp` tabanı işaret eder. `rbp`'nin **altındaki**
(negatif offset, `rbp-0x18`) alan fonksiyonun kendi yerel değişkenleri için
ayrılmıştır. `rbp`'nin **üstü** (`rbp+0x10` gibi) dönüş adresi ve — altı argüman
register'a sığmadıysa — taşan argümanlar içindir.

📌 **Sık yapılan hata:** Her `[rbp-...]`'i argüman sanmak. System V ABI'de ilk
altı argüman register'la gelir; `[rbp-...]` genelde yereldir (derleyici argümanı
oraya kopyalamışsa da artık yerel bir kopyadır).

🔗 [02 §4 Stack frame](02-bellek-modeli.md)

### Soru 3 — Bir string'in `.rodata`'da olması onun hakkında ne söyler?

**Kısa cevap:** **Sabittir** — kaynağa gömülü, salt okunur.

**Ayrıntı:** `.rodata` (read-only data) derleme anında bilinen sabitleri tutar:
kaynaktaki `"password"` gibi literal string'ler oraya gider. Program boyu yaşar
ve yazılamaz. Bir parola string'i `.rodata`'daysa **binary'ye gömülmüş** demektir
ve doğrudan okunabilir; stack'teyse çalışma zamanında (kullanıcı girdisinden)
üretilmiştir.

📌 **Sık yapılan hata:** Nerede durduğuna bakmadan her string'i "gizli" saymak.
Bölge (`.rodata` vs stack) string'in nereden geldiğini söyler.

🔗 [02 §1 Adres uzayı bölgeleri](02-bellek-modeli.md)

### Soru 4 — `mov rax, rdi` ile `mov rax, [rdi]` arasındaki fark nedir?

**Kısa cevap:** İlki **değeri** kopyalar, ikincisi `rdi`'nin **gösterdiği
bellekten okur** (dereference).

**Ayrıntı:** Köşeli parantez "buradaki bellek" demektir. `mov rax, rdi` →
`rax = rdi` (register'daki sayı, örn. bir adres). `mov rax, [rdi]` → `rax`,
`rdi`'nin adres olarak gösterdiği yerdeki 8 byte. `rdi` bir işaretçiyse birincisi
işaretçiyi, ikincisi işaret edilen değeri getirir.

📌 **Sık yapılan hata:** Parantezi görmezden gelmek. `[...]` bir bellek erişimidir;
onsuz komut yalnızca register-register kopyalamadır.

🔗 [02 §2 Register ve bellek erişimi](02-bellek-modeli.md)

### Soru 5 — `sub rsp, 0x20` fonksiyonun başında ne yapıyor?

**Kısa cevap:** **Yerel değişkenler için stack'te yer açıyor** — 32 byte.

**Ayrıntı:** Stack aşağı büyüdüğü için `rsp`'yi azaltmak (`sub rsp, 0x20`) 32
byte'lık boş alanı fonksiyona ayırır. Yerel değişkenler bu alana `[rsp+...]` ya
da `[rbp-...]` ile yazılır. Fonksiyon dönerken bu alan geri verilir (`add rsp,
0x20` ya da `leave`).

📌 **Sık yapılan hata:** Bunu bir hesap işlemi sanmak. Fonksiyon girişindeki
`sub rsp, sabit`, prolog'un parçasıdır — mantık değil, yer ayırma.

🔗 [02 §4 Stack frame](02-bellek-modeli.md)

---

## 03 Kaynaktan binary'ye: derleme, linkleme, ELF

### Soru 1 — `gcc -S prog.c` ne üretir? `gcc -c` ne üretir?

**Kısa cevap:** `-S` **assembly** (`prog.s`), `-c` **object** dosyası (`prog.o`).

**Ayrıntı:** Derleme hattı: kaynak → önişlem → **derleme** → assemble →
**linkleme**. `-S` hattı derleme adımında durdurur ve insan-okunur assembly
verir. `-c` bir adım ileri gider: assemble edilmiş makine kodunu içeren object
dosyası üretir ama **linklemez**, yani tek başına çalışmaz.

📌 **Sık yapılan hata:** `-c` ile çalıştırılabilir dosya beklemek. Object henüz
linklenmemiştir; çalıştırılabilir dosya linkleme sonrası doğar.

🔗 [03 §1 Derleme hattı](03-derleme-elf.md)

### Soru 2 — ELF magic byte'ları nelerdir? Bir dosyanın ELF olduğunu nasıl anlarsın?

**Kısa cevap:** İlk dört byte **`7f 45 4c 46`** (`\x7fELF`). `file prog` bunu okur.

**Ayrıntı:** Her ELF dosyası bu dört sihirli byte'la başlar: `0x7f` sonra `'E'`
`'L'` `'F'` ASCII'si. `file prog` komutu bu magic'e bakıp dosyanın ELF olduğunu,
32/64-bit'liğini, statik/dinamikliğini ve stripped olup olmadığını söyler.

📌 **Sık yapılan hata:** Dosya uzantısına güvenmek. Linux'ta çalıştırılabilir
dosyanın uzantısı yoktur; türü **magic byte** belirler, ad değil.

🔗 [03 §2 ELF nedir](03-derleme-elf.md)

### Soru 3 — Statik ve dinamik linkleme arasındaki temel fark nedir? `ldd` ile nasıl ayırırsın?

**Kısa cevap:** Dinamik kütüphaneyi **ödünç alır** (ayrı `.so`), statik **içine
gömer**. `ldd prog` dinamikte bağımlılık listeler, statikte "not a dynamic
executable" der.

**Ayrıntı:** Dinamik binary küçüktür; `printf` gibi çağrılar çalışırken libc'den
PLT/GOT üzerinden bulunur. Statik binary büyüktür; libc kodu içine kopyalanmıştır.
`ldd prog` dinamik bağımlılıkları gösterir — statikte gösterecek bir şey yoktur.

📌 **Sık yapılan hata:** Statik + stripped binary'de `strcmp` gibi fonksiyonları
isimli aramak. Statikte libc kod yığınına gömülüdür ve isimsiz gelebilir;
başlangıçta dinamik binary'lerle çalış.

🔗 [03 §3 Statik vs dinamik linkleme](03-derleme-elf.md)

### Soru 4 — "Stripped" bir binary neyi kaybeder, neyi korur?

**Kısa cevap:** **Kendi** fonksiyon isimlerini kaybeder; **import edilen** dış
isimleri (örn. `printf`) korur.

**Ayrıntı:** `strip` sembol tablosunu (`.symtab`) siler; `main`, `check_password`
gibi kendi isimlerin gider, yerine `FUN_00401136` gibi adres-tabanlı adlar kalır.
Ama dinamik sembol tablosu (`.dynsym`) linkleme için gerekli olduğundan **kısmen
kalır**: dışarıdan çağrılan `printf` gibi import isimleri hâlâ görünür.

📌 **Sık yapılan hata:** Stripped binary'de hiçbir isim yok sanmak. Import edilen
libc isimleri durur — o çağrılar en iyi başlangıç ipuçlarındır.

🔗 [03 §4 Stripped ve semboller](03-derleme-elf.md)

### Soru 5 — Neden `-O0` binary'si RE için `-O3`'ten kolaydır?

**Kısa cevap:** `-O0` kaynağa **birebir yakındır**; `-O3` döngüleri açar,
değişkenleri yok eder, kodu yeniden düzenler.

**Ayrıntı:** Optimizasyon seviyesi çıktıyı kökten değiştirir. `-O0`
optimizasyonsuzdur: her değişken stack'te durur, her satır tanınır sırayla
çevrilir. `-O2`/`-O3` agresiftir: döngüler açılabilir (unrolling), değişkenler
yalnızca register'da yaşayabilir, işlemler yeniden sıralanır — kaynakla
disassembly arasındaki bağ zayıflar. CTF binary'leri çoğu zaman `-O2`/`-O3`
iledir; kendi denemelerinde `-O0 -g` kullan.

📌 **Sık yapılan hata:** Kendi test binary'ni optimizasyonla derleyip "decompile
neden bu kadar karışık" diye şaşırmak. Öğrenirken `-O0 -g`, gerçek hedefte ne
verildiyse o.

🔗 [03 §1 Derleme hattı (optimizasyon tuzağı)](03-derleme-elf.md)
