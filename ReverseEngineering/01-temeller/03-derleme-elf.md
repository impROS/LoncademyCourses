# 03 — Kaynaktan Binary'ye: Derleme, Linkleme, ELF

> **Alan:** Makine seviyesi temeller (%16) — analiz edeceğin nesnenin anatomisi
> **Süre:** ~25 dakika okuma + 20 dakika pratik
> **Test:** [`03-test.html`](03-test.html) · 15 soru

---

## Neden bu konu

RE'de elindeki nesne bir **ELF binary**'sidir. Onu açmadan önce nasıl oluştuğunu bilmelisin: hangi
aşamada isimler kayboldu, string'ler nereye gitti, "stripped" ne demek, statik/dinamik linkleme neyi
değiştirir. En sık hata: her binary'de fonksiyon isimlerinin duracağını sanmak — oysa çoğu CTF binary'si
**stripped**'tir ve `main`'i bile elle bulman gerekir.

**Büyük fikir:** Derleyici zinciri tek yönlü bir hattır: **kaynak → önişlem → derleme → assembly →
makine kodu → linkleme → ELF.** Her aşamada bilgi kaybolur. RE, bu hattın çıktısından geriye bilgi
kurtarma sanatıdır.

---

## 1. ⭐ Derleme hattı — hangi aşamada ne olur

| Aşama | Girdi → Çıktı | Ne olur | RE'ye etkisi |
|---|---|---|---|
| **Önişlem** (preprocess) | `.c` → genişletilmiş `.c` | `#include`, `#define` açılır | Makrolar kaybolur |
| **Derleme** (compile) | `.c` → `.s` (assembly) | C mantığı assembly'ye çevrilir | Yorum, çoğu isim gider |
| **Assemble** | `.s` → `.o` (object) | Assembly makine koduna | İnsan-okunur metin biter |
| **Linkleme** (link) | `.o` + kütüphaneler → ELF | Parçalar birleşir, adresler bağlanır | Çalıştırılabilir dosya doğar |

`gcc prog.c -o prog` bu dört adımı tek komutta yapar. Ara adımları görmek için:
`gcc -S prog.c` (assembly üretir), `gcc -c prog.c` (object üretir).

> ⚠️ **Tuzak:** Optimizasyon seviyesi (`-O0`…`-O3`) çıktıyı **kökten** değiştirir. `-O0` (optimizasyonsuz)
> okunması en kolay, kaynağa en yakın haldir — CTF binary'leri çoğu zaman `-O2`/`-O3` ile derlenir ve
> döngüler açılmış, değişkenler yok olmuş olabilir. Kendi denemelerinde `-O0 -g` kullan.

---

## 2. ⭐ ELF nedir — Linux'un binary formatı

**ELF** (Executable and Linkable Format), Linux'ta çalıştırılabilir dosyaların, kütüphanelerin ve
object dosyalarının formatıdır (Windows'ta karşılığı PE). Bir ELF şu parçalardan oluşur:

| Parça | Ne tutar |
|---|---|
| **ELF header** | Sihirli byte'lar (`7f 45 4c 46` = `\x7fELF`), 32/64-bit, giriş noktası (entry point) |
| **Program headers** | Çalıştırma anında bellek nasıl haritalanacak (segment'ler) |
| **Section'lar** | `.text`, `.rodata`, `.data`, `.bss`, `.symtab` … (analiz için) |
| **Symbol table** | Fonksiyon/değişken isimleri (stripped değilse) |

İlk 4 byte her ELF'te aynıdır: `7f 45 4c 46`. Bir dosyanın ELF olduğunu bu **magic**'ten anlarsın.

**Section vs segment:** Section'lar **linker/analiz** içindir (`.text`, `.data`…). Segment'ler **çalıştırma**
içindir (kernel dosyayı belleğe segment'lerle yükler). Aynı byte'lara iki farklı bakış.

> ⚠️ **Tuzak:** Section header'ları **silinebilir** (stripped) ama program bellekte yine çalışır — çünkü
> çalışma segment'lerle olur, section'larla değil. Yani "section yok" ≠ "çalışmıyor".

---

## 3. ⭐ Statik vs dinamik linkleme

Programın kullandığı kütüphane kodu (örn. `printf`) iki türlü bağlanır:

| | **Dinamik** (varsayılan) | **Statik** |
|---|---|---|
| Kütüphane kodu | Ayrı `.so` dosyasında, çalışırken yüklenir | Binary'nin **içine** kopyalanır |
| Dosya boyutu | Küçük | Büyük |
| `printf` gibi çağrılar | PLT/GOT üzerinden dışa gider | Binary içinde |
| RE'ye etkisi | libc fonksiyonları isimli görünür | libc fonksiyonları binary'ye karışır, isimsiz olabilir |
| Nasıl anlarsın | `ldd prog` bağımlılık listeler | `ldd`: "not a dynamic executable" |

**Hafıza kancası:** Dinamik = "kütüphaneyi *ödünç al*", statik = "kütüphaneyi *içine göm*".

> ⚠️ **Tuzak:** Statik + stripped bir binary'de `strcmp` gibi libc fonksiyonları isimsiz, kod yığınına
> gömülü gelir. Bunları tanımak (FLIRT/signature) ileri bir beceridir; başlangıçta dinamik binary'lerle çalış.

---

## 4. ⭐ Stripped ve semboller

**Sembol** = bir adrese verilen isim (`main`, `check_password`, `counter`). Symbol table'da tutulur.

- **Not stripped:** Fonksiyon isimleri durur. Ghidra/objdump `main`, `check` gibi isimler gösterir. Kolay.
- **Stripped:** `strip prog` ile semboller silinmiştir. Sadece `FUN_00401136` gibi adres-tabanlı isimler
  kalır. `main`'i bile giriş noktasından takip ederek bulman gerekir.

Nasıl anlarsın: `file prog` çıktısında "stripped" veya "not stripped" yazar. `nm prog` sembolleri listeler
(stripped'te "no symbols" der).

> ⚠️ **Tuzak:** Dinamik sembol tablosu (`.dynsym`) strip'lense bile **kısmen kalır** — çünkü dışa açık
> semboller linkleme için gerekir. Yani stripped bir binary'de bile `printf` gibi *import edilen* isimler
> görünebilir; senin *kendi* fonksiyonların kaybolur.

---

## 5. RE'de ilk 4 komut — binary'yi tanıma refleksi

Bir binary'yi ilk açtığında sırayla şunları çalıştır (bunlar senin "kimlik kartı" komutların):

| Komut | Ne söyler |
|---|---|
| `file prog` | ELF mi, 32/64-bit mi, statik/dinamik mi, stripped mi |
| `checksec prog` | Koruma katmanları (NX, PIE, canary, RELRO — Bölüm 6) |
| `strings prog` | İçindeki okunur metinler (flag ipuçları, mesajlar) |
| `nm prog` / `objdump -d prog` | Semboller / disassembly |

Bu dörtlü, hiçbir aracı açmadan binary hakkında %50 fikir verir.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| ELF magic byte'ları | `7f 45 4c 46` (`\x7fELF`) | PE `MZ` ile karışır (o Windows) |
| Section vs segment | Section=analiz, segment=çalıştırma | İkisi aynı sanılır |
| Statik linkleme | Kütüphane binary içine gömülür | Dinamik = ayrı .so |
| Stripped ne siler | Kendi sembollerini (isimlerini) | Kod hâlâ çalışır, sadece isimsiz |
| `-O0` vs `-O3` | O0 okunur/kaynağa yakın | O3 optimize, okuması zor |

---

## 🖥 Pratik — bir binary'nin kimliğini çıkar

> **Amaç:** file/strings/nm/objdump ile bir ELF'i tanımak, stripped etkisini görmek · **Süre:** 20 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Derle (biri normal, biri stripped):
   ```bash
   cat > /tmp/hi.c <<'EOF'
   #include <stdio.h>
   int gizli(){ return 42; }
   int main(){ printf("merhaba RE\n"); return gizli(); }
   EOF
   gcc -O0 -o /tmp/hi /tmp/hi.c
   cp /tmp/hi /tmp/hi_stripped && strip /tmp/hi_stripped
   ```
2. Kimlik kartı:
   ```bash
   file /tmp/hi
   file /tmp/hi_stripped
   ```
   İlkinde "not stripped", ikincisinde "stripped" görmelisin.
3. String'ler — mesajı bul:
   ```bash
   strings /tmp/hi | grep merhaba
   ```
   Beklenen: `merhaba RE`.
4. Semboller — `gizli` fonksiyonu duruyor mu:
   ```bash
   nm /tmp/hi | grep gizli
   nm /tmp/hi_stripped 2>&1 | head -1
   ```
   İlki `gizli`'yi listeler; ikincisi "no symbols" der.
5. ELF magic'i kendi gözünle gör:
   ```bash
   xxd /tmp/hi | head -1
   ```
   İlk byte'lar `7f45 4c46` (`.ELF`) olmalı.

- [ ] **Kontrol:** stripped kopyada `nm` "no symbols" dedi mi? Magic `7f 45 4c 46` çıktı mı?
- [ ] **Kaydet:** `file`+`strings`+`nm` = ilk refleks · stripped = kendi isimlerim gitti · magic `\x7fELF` ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — dosya tanıma**
> `file` çıktısı: "ELF 64-bit ... dynamically linked ... stripped". Ne beklersin? → **64-bit, libc ayrı yüklenir, kendi fonksiyon isimlerin yok.**

**Kalıp 2 — magic**
> Bir dosyanın ilk byte'ları `7f 45 4c 46`. Bu nedir? → **Bir ELF dosyası.**

**Kalıp 3 — optimizasyon**
> Kaynakta basit bir `for` döngüsü var ama disassembly'de döngü yok, kod düzleşmiş. Neden? → **Derleyici `-O2`/`-O3` ile döngüyü açtı/optimize etti.**

**Kalıp 4 — statik/dinamik**
> `ldd prog` "not a dynamic executable" diyor. Ne anlama gelir? → **Statik linklenmiş; libc binary'nin içinde.**

---

## 60 saniyelik özet

- Hat: kaynak → önişlem → derleme → assemble → link → ELF. Her adım bilgi siler.
- ELF = Linux binary formatı; magic `7f 45 4c 46`; header + segment'ler + section'lar.
- Section = analiz için, segment = çalıştırma için (aynı byte'lar, iki bakış).
- Dinamik link = libc ayrı `.so`; statik = içine gömülü. `ldd` ile ayırt et.
- Stripped = kendi sembollerin silinmiş; kod çalışır ama isimsiz. `file`/`nm` ile anla.
- İlk refleks dörtlüsü: `file`, `checksec`, `strings`, `objdump/nm`.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `gcc -S prog.c` ne üretir? `gcc -c` ne üretir?
2. ELF magic byte'ları nelerdir? Bir dosyanın ELF olduğunu nasıl anlarsın?
3. Statik ve dinamik linkleme arasındaki temel fark nedir? `ldd` ile nasıl ayırırsın?
4. "Stripped" bir binary neyi kaybeder, neyi korur?
5. Neden `-O0` binary'si RE için `-O3`'ten kolaydır?

---

## ✅ Test
➡️ **[03-test.html](03-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`../02-assembly/01-register-komut.md`](../02-assembly/01-register-komut.md)
