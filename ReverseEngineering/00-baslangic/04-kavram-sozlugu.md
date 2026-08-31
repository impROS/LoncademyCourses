# Kavram sözlüğü

> **Bu dosya hatırlatma içindir, ilk öğrenme için değil.** Her terim konu
> dosyasında ilk geçtiği yerde açıklanıyor. Buraya "neydi bu ya?" dediğinde
> dönersin.
>
> Terimlerin İngilizcesi de yazılı: araç çıktısında, belgelerde ve CTF
> yazılarında onları İngilizce göreceksin.
>
> Beş bölüm: [Makine ve bellek](#a-makine-ve-bellek) · [Binary ve derleme](#b-binary-ve-derleme) ·
> [Assembly](#c-assembly) · [Araçlar](#d-araçlar) · [Analiz ve pratik](#e-analiz-ve-pratik)

---

## A. Makine ve bellek

#### Endianness

Çok baytlı bir sayının bellekte hangi sırayla durduğu. x86-64 **little-endian**'dır:
en düşük değerlikli bayt en düşük adreste. `0x41424344` bellekte `44 43 42 41`
görünür — hex dökümü okurken en sık yapılan hata budur.
→ [1.1 Sayı sistemleri, bit, byte, endianness](../01-temeller/01-sayilar-bellek.md)

#### ASCII

Baytların karaktere karşılık geldiği tablo. `41 42 43 44` baytları hem
`"ABCD"` metni hem `0x44434241` sayısı olarak okunabilir; hangisi olduğuna
**bağlam** karar verir.
→ [1.1 Sayı sistemleri, bit, byte, endianness](../01-temeller/01-sayilar-bellek.md)

#### Register

İşlemcinin içindeki, bellekten çok daha hızlı olan az sayıda saklama yeri.
x86-64'te `rax`, `rbx`, `rdi`, `rsi`, `rsp`, `rbp` gibi 64 bitlik register'lar
vardır; `eax` aynı register'ın alt 32 bitidir.
→ [2.1 Register'lar ve temel komut seti](../02-assembly/01-register-komut.md)

#### Stack

Fonksiyon çağrılarının yerel değişkenlerini ve dönüş adreslerini tuttuğu,
**yüksek adresten alçağa doğru büyüyen** bellek bölgesi. `rsp` tepesini gösterir.
Buffer overflow'un yaşadığı yer.
→ [1.2 Bellek modeli](../01-temeller/02-bellek-modeli.md)

#### Heap

Çalışma zamanında `malloc` gibi çağrılarla ayrılan, ömrü fonksiyondan bağımsız
bellek bölgesi.
→ [1.2 Bellek modeli](../01-temeller/02-bellek-modeli.md)

#### Stack frame

Tek bir fonksiyon çağrısının stack üzerindeki bloğu: dönüş adresi, kaydedilen
`rbp`, yerel değişkenler. Fonksiyonun nerede başlayıp bittiğini bundan okursun.
→ [2.4 Fonksiyon çağrısı ve System V ABI](../02-assembly/04-fonksiyon-abi.md)

#### ABI

*Application Binary Interface.* Derlenmiş kodun uyduğu sözleşme: argümanlar hangi
register'larla geçer, dönüş değeri nerede durur, stack nasıl hizalanır. Linux
x86-64'te **System V ABI** geçerlidir; ilk altı tamsayı argüman sırasıyla
`rdi, rsi, rdx, rcx, r8, r9`.
→ [2.4 Fonksiyon çağrısı ve System V ABI](../02-assembly/04-fonksiyon-abi.md)

#### ASLR

*Address Space Layout Randomization.* Her çalıştırmada bellek bölgelerinin farklı
adreslere yerleştirilmesi. Bu yüzden **sabit adrese breakpoint koymak** güvenilmez;
sembol adı kullan.
→ [4.1 GDB + pwndbg temelleri](../04-dinamik/01-gdb-temel.md)

---

## B. Binary ve derleme

#### ELF

*Executable and Linkable Format.* Linux'un çalıştırılabilir dosya biçimi.
`file prog` çıktısında `ELF 64-bit LSB executable, x86-64` görürsün.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### Optimizasyon seviyesi

Derleyicinin ne kadar agresif iyileştirme yaptığı: `-O0` optimizasyonsuz ve
kaynağa en yakın hâl, `-O2`/`-O3` hızlı ama okunması zor. **Aynı kaynak, farklı
seviyede tanınmayacak kadar farklı** assembly üretir. CTF binary'leri genelde
`-O2`/`-O3` ile derlenir.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### Sembol ve stripping

Sembol, fonksiyon ve değişken adlarının binary'de saklanmış hâli. **Stripped**
bir binary'de bu adlar silinmiştir; Ghidra `FUN_00401136` gibi üretilmiş adlar
gösterir ve isimlendirme işi sana kalır.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### PLT ve GOT

*Procedure Linkage Table / Global Offset Table.* `printf` gibi dış kütüphane
çağrılarının çalışma zamanında gerçek adrese bağlandığı iki tablo. Disassembly'de
`call printf@plt` görürsün.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### NX

*No-eXecute.* Stack gibi veri bölgelerinin **çalıştırılamaz** işaretlenmesi.
Klasik "stack'e shellcode yaz ve atla" saldırısını kapatır; ROP bu yüzden doğdu.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### PIE

*Position Independent Executable.* Programın her çalıştırmada farklı taban
adrese yüklenebilmesi. ASLR'yi kod bölümü için de geçerli kılar.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### RELRO

*RELocation Read-Only.* GOT tablosunun yazmaya kapatılması; GOT üzerine yazma
saldırılarını kısıtlar.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### Canary

Dönüş adresinin hemen önüne konan rastgele değer. Fonksiyon dönerken değişmişse
program kendini durdurur — stack taşmasını **fark etme** mekanizmasıdır.
→ [6.1 Buffer overflow'u RE gözüyle görmek](../06-exploit/01-overflow-gozu.md)

---

## C. Assembly

#### Flag register

Son işlemin sonucunu özetleyen bit'ler. En çok kullanılanlar: **ZF** (Zero Flag —
sonuç sıfır, yani eşitlik), **SF** (Sign Flag — sonuç negatif), **OF** (Overflow
Flag — işaretli taşma), **CF** (Carry Flag — işaretsiz taşma). `cmp` bunları
kurar, `je`/`jne` bunlara bakar.
→ [2.2 Bellek erişimi, adresleme, LEA, flag'ler](../02-assembly/02-bellek-flag.md)

#### LEA

*Load Effective Address.* Adresi **hesaplar ama okumaz**. `mov` bellekten değeri
getirir, `lea` yalnızca adresi verir; derleyiciler bunu çarpma-toplama kısayolu
olarak da kullanır.
→ [2.2 Bellek erişimi, adresleme, LEA, flag'ler](../02-assembly/02-bellek-flag.md)

#### XOR deyimi

`xor rax, rax` bir şifreleme değil, **register'ı sıfırlamanın** kısa ve hızlı
yoludur. Disassembly'de gördüğünde "burada XOR şifreleme var" diye okuma.
→ [2.1 Register'lar ve temel komut seti](../02-assembly/01-register-komut.md)

#### Koşullu atlama

`cmp`/`test` ile kurulan flag'lere bakıp dallanan komutlar: `je` (eşitse),
`jne` (eşit değilse), `jg`/`jl` (işaretli büyük/küçük), `ja`/`jb` (işaretsiz).
Kaynaktaki `if` ve döngüler bu ikiliden okunur.
→ [2.3 Kontrol akışı](../02-assembly/03-kontrol-akisi.md)

---

## D. Araçlar

#### Disassembler

Makine kodunu **assembly**'ye çeviren araç. Bire bir karşılıktır, yorum katmaz.
→ [3.1 Disassembler vs decompiler](../03-statik/01-ghidra-giris.md)

#### Decompiler

Makine kodundan **C benzeri sözde kaynak** üreten araç. Okuması kolaydır ama
**tahmin içerir**: tipleri ve isimleri uydurur, bazen yanlış çıkarır. Şüphede
kaldığında assembly'ye dön.
→ [3.1 Disassembler vs decompiler](../03-statik/01-ghidra-giris.md)

#### Ghidra

Açık kaynak tersine mühendislik paketi; disassembler ve decompiler'ı birlikte
sunar. Grafik arayüzlüdür ve çalışmak için Java çalışma ortamı ister.
→ [3.1 Disassembler vs decompiler](../03-statik/01-ghidra-giris.md)

#### IDA

Ticari disassembler/decompiler. Ghidra'nın en yaygın alternatifi; bu set
Ghidra üzerinden ilerliyor ama kavramlar aynı.
→ [3.1 Disassembler vs decompiler](../03-statik/01-ghidra-giris.md)

#### GDB

*GNU Debugger.* Programı **çalışırken** durdurup register ve belleği inceleten
araç. Dinamik analizin omurgası.
→ [4.1 GDB + pwndbg temelleri](../04-dinamik/01-gdb-temel.md)

#### pwndbg

GDB'ye takılan eklenti: register'ları, stack'i ve disassembly'yi her durakta
okunur biçimde gösterir. Çıplak GDB'ye göre çok daha az komut yazdırır.
→ [4.1 GDB + pwndbg temelleri](../04-dinamik/01-gdb-temel.md)

#### Breakpoint

Programın belirli bir noktada durdurulmasını sağlayan işaret. ASLR yüzünden
sabit adres yerine **sembol adına** koymak daha güvenilirdir (`break main`).
→ [4.1 GDB + pwndbg temelleri](../04-dinamik/01-gdb-temel.md)

#### checksec

Bir binary'de hangi koruma katmanlarının açık olduğunu (NX, PIE, canary, RELRO)
tek bakışta gösteren araç.
→ [1.3 Derleme, linkleme, ELF](../01-temeller/03-derleme-elf.md)

#### Xref

*Cross-reference — çapraz başvuru.* Bir string'in, sabitin ya da fonksiyonun
**nereden kullanıldığı**. Büyük bir binary'de yön bulmanın en hızlı yolu:
ilginç string'i bul, xref'ine git.
→ [3.3 String, xref, sabit avı](../03-statik/03-string-xref.md)

#### pwntools

Exploit yazmak için Python kütüphanesi: süreci başlatır, girdi gönderir, çıktıyı
okur, adresleri paketler.
→ [6.2 pwntools + ret2win](../06-exploit/02-pwntools-ret2win.md)

#### VM

*Virtual machine — sanal makine.* Ana makinenden yalıtılmış işletim sistemi.
Bilinmeyen ya da zararlı binary'ler **yalnızca** burada çalıştırılır.
→ [0.2 Ortam kurulumu](02-ortam-kurulumu.md)

---

## E. Analiz ve pratik

#### RE

*Reverse engineering — tersine mühendislik.* Kaynak koda erişmeden, çalıştırılabilir
dosyadan davranışı ve mantığı çıkarma işi. Ezber değil, **ölçüm ve çıkarım** işidir.
→ [0.0 Reverse engineering nedir](00-genel-bakis.md)

#### Statik analiz

Programı **çalıştırmadan** incelemek: disassembly okumak, string aramak, xref
takip etmek. Riski düşüktür, ama çalışma zamanında hesaplanan değerleri göremezsin.
→ [3.1 Disassembler vs decompiler](../03-statik/01-ghidra-giris.md)

#### Dinamik analiz

Programı **çalıştırarak** incelemek: breakpoint koymak, register ve belleği
canlı okumak. Gerçek değerleri görürsün, ama programı çalıştırmış olursun —
bilinmeyen binary'de yalıtım şart.
→ [4.1 GDB + pwndbg temelleri](../04-dinamik/01-gdb-temel.md)

#### CTF

*Capture The Flag.* Kasıtlı olarak hazırlanmış bulmaca binary'lerinin çözüldüğü
yarışma biçimi. Bu setin ana pratik alanı; hedef genelde gizlenmiş bir metin
parçasını ("flag") bulmaktır.
→ [0.3 Lab siteleri rehberi](03-lab-siteleri.md)

#### Crackme

Lisans/parola kontrolünü tersine çevirmek üzere yazılmış eğitim binary'si.
Yasal olarak **serbest** olmasının sebebi budur: kırılmak için üretilmiştir.
→ [5.1 Lisans kontrolü mantığını bulmak](../05-crackme/01-crackme-mantik.md)

#### Keygen

Programın doğru kabul ettiği anahtarı **üreten** kod. Kontrolü atlamaktan farkı:
algoritmayı gerçekten anlamış olmayı gerektirir.
→ [5.2 Keygen yazmak](../05-crackme/02-keygen.md)

#### Patching

Binary'nin baytlarını doğrudan değiştirerek davranışını değiştirmek — örneğin bir
koşullu atlamayı koşulsuz yapmak. Hızlıdır ama **algoritmayı öğretmez**.
→ [5.3 Binary patching ve anti-debug](../05-crackme/03-patching-antidebug.md)

#### Anti-debug

Programın hata ayıklayıcı altında çalıştığını fark edip davranışını değiştirmesi.
Dinamik analizi zorlaştırmak için konur.
→ [5.3 Binary patching ve anti-debug](../05-crackme/03-patching-antidebug.md)

#### Buffer overflow

Bir tampona kapasitesinden fazla veri yazılması. Stack üzerinde olursa dönüş
adresinin üzerine yazılabilir ve akış saldırganın seçtiği yere gider.
→ [6.1 Buffer overflow'u RE gözüyle görmek](../06-exploit/01-overflow-gozu.md)

#### ret2win

Dönüş adresini, binary'de zaten bulunan "kazandın" fonksiyonuna yönlendiren en
basit exploit biçimi. İlk kontrollü exploit alıştırması olarak kullanılır.
→ [6.2 pwntools + ret2win](../06-exploit/02-pwntools-ret2win.md)

#### ROP

*Return-Oriented Programming.* NX yüzünden kendi kodunu çalıştıramadığında,
binary'de **zaten var olan** küçük komut parçalarını zincirleyerek istediğini
yaptırma tekniği.
→ [6.1 Buffer overflow'u RE gözüyle görmek](../06-exploit/01-overflow-gozu.md)
