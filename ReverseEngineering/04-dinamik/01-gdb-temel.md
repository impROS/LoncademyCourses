# 01 — GDB + pwndbg Temelleri: Breakpoint, Adımlama, Register

> **Alan:** Dinamik analiz (%18) — programı çalışırken izlemek
> **Süre:** ~30 dakika okuma + 25 dakika pratik
> **Test:** [`01-test.html`](01-test.html) · 15 soru

---

## Neden bu konu

Statik analiz "ne yapabilir"i gösterir; dinamik analiz "şu an ne yapıyor"u gösterir. GDB, programı
durdurup register'ları, belleği, akışı **canlı** izlemeni sağlar. pwndbg eklentisi GDB'yi RE için çok daha
kullanışlı hale getirir. En sık hata: breakpoint'i yanlış yere koyup ya çok erken ya çok geç durmak, ya da
`step` (fonksiyonun içine gir) ile `next` (üstünden atla) arasındaki farkı bilmemek.

**Büyük fikir:** Dinamik analiz = programı **kontrollü** çalıştırmak. Sen durdurma noktalarını koyar,
adım adım ilerler ve her anda "değişkenlerin gerçek değeri ne" sorusunu cevaplarsın. Statikte tahmin
ettiğin şeyi dinamikte **görürsün**.

---

## 1. ⭐ pwndbg neden ve kurulum

Çıplak GDB güçlüdür ama ham. **pwndbg** (veya alternatifi GEF) her durakta register'ları, stack'i,
disassembly'yi ve akışı renkli, düzenli gösterir; RE/exploit için tasarlanmıştır.

Kurulum özeti (ayrıntı `00-baslangic/02-ortam-kurulumu.md`):
```bash
git clone https://github.com/pwndbg/pwndbg
cd pwndbg && ./setup.sh
```
Kurulunca her `gdb` açılışında pwndbg yüklenir; durunca otomatik **context** (register/stack/kod) basar.

> ⚠️ **Doğrulanmalı:** pwndbg kurulum adımları ve komut adları sürümle değişebilir — resmî repo'dan teyit et.

---

## 2. ⭐ Programı başlatmak ve durdurmak

| Komut | Ne yapar |
|---|---|
| `gdb ./prog` | prog'u GDB altında yükle (çalıştırmaz) |
| `run` / `r` | programı baştan çalıştır |
| `run arg1 arg2` | argümanlarla çalıştır |
| `start` | `main`'e breakpoint koyup çalıştır |
| `continue` / `c` | bir sonraki breakpoint'e kadar devam |
| `kill` | çalışan programı öldür |
| `quit` / `q` | GDB'den çık |

Girdi vermen gereken bir program için `run` sonrası terminale yazarsın; veya `run < input.txt` ile
dosyadan beslersin.

---

## 3. ⭐ Breakpoint — nerede durayım

Breakpoint, programı belirli bir noktada durdurur. Koyma yolları:

| Komut | Nereye koyar |
|---|---|
| `break main` / `b main` | `main` fonksiyonunun başına |
| `break *0x401136` | tam bir adrese (stripped'te böyle) |
| `break strcmp` | bir kütüphane fonksiyonuna (her çağrıda durur) |
| `break *main+42` | main'den 42 byte içeri |
| `tbreak ...` | tek seferlik breakpoint (bir kez durur, silinir) |

Yönetim: `info breakpoints` (listele), `delete N` (sil), `disable N` / `enable N`.

**Refleks:** Bir parola kontrolünü izlemek için `break strcmp` koy — program karşılaştırma anında durur,
argümanları (`$rdi`, `$rsi`) okursun (2.4). Bu, gizli parolayı bulmanın en hızlı dinamik yoludur.

> ⚠️ **Tuzak:** PIE (position independent executable) binary'lerde adresler her çalıştırmada değişir
> (ASLR). Bu yüzden sabit adrese breakpoint koymadan önce ya sembol ismi kullan (`break main`) ya da
> program yüklenince gerçek adresi öğren. pwndbg base adresi gösterir.

---

## 4. ⭐ Adımlama: step vs next vs stepi/nexti

Durduktan sonra ilerleme komutları — **fark kritik:**

| Komut | Seviye | Fonksiyon çağrısında |
|---|---|---|
| `step` / `s` | kaynak satırı | **içine girer** |
| `next` / `n` | kaynak satırı | **üstünden atlar** |
| `stepi` / `si` | tek assembly komutu | içine girer |
| `nexti` / `ni` | tek assembly komutu | `call`'ın üstünden atlar |
| `finish` | — | mevcut fonksiyondan **çıkana** kadar çalış |

RE'de genelde `si`/`ni` (komut seviyesi) kullanırsın, çünkü kaynak yok. Bir `call`'ı incelemek
istemiyorsan `ni` ile atla; içine girip görmek istiyorsan `si`.

> ⚠️ **Tuzak:** `si` ile bir `call printf`'e girersen libc'nin içinde kaybolursun — yüzlerce komut. İlgilenmediğin
> kütüphane çağrılarını `ni` ile atla, sadece kendi kodunu `si` ile izle. Yanlış tercih zaman kaybettirir.

---

## 5. ⭐ Register ve değer okuma

| Komut | Ne gösterir |
|---|---|
| `info registers` / `i r` | tüm register'lar |
| `p $rax` | rax'in değeri (decimal) |
| `p/x $rax` | rax hex olarak |
| `x/s $rdi` | rdi'nin gösterdiği string |
| `x/4xg $rsp` | rsp'den 4 adet 8-byte (giant) hex |
| `x/i $rip` | sıradaki komut (disassemble) |

`x` (examine) formatı: `x/[sayı][format][boyut] adres`. Format: `x`=hex, `d`=decimal, `s`=string,
`i`=instruction. Boyut: `b`=byte, `w`=word(4), `g`=giant(8).

`set $rax = 5` ile bir register'ı **değiştirebilirsin** — akışı kandırmanın (patching'in dinamik hali)
temeli (Bölüm 5).

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `step` vs `next` | step içine girer, next atlar | Çağrıda davranış farkı |
| `si` ile `call printf` | libc'de kaybolursun, `ni` kullan | Kendi kodun değil |
| `break strcmp` | her çağrıda durur, argüman okunur | Tek yer değil |
| PIE'de sabit adres bp | adres değişir (ASLR), sembol kullan | Sabit adres güvenilmez |
| `x/s $rdi` | rdi'nin gösterdiği string | `p $rdi` sadece sayı |

---

## 🖥 Pratik — parolayı GDB ile canlı yakala

> **Amaç:** break + run + register okuma ile gizli değeri yakalamak · **Süre:** 25 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Bir crackme derle (bu sefer parolayı düz string yakalamayacağız, strcmp'te yakalayacağız):
   ```bash
   cat > /tmp/dbg.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char b[32];
     printf("Pass: ");
     if(scanf("%31s",b)!=1) return 1;
     if(strcmp(b,"g1zl1_p4r0la")==0) puts("Correct!");
     else puts("Wrong.");
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/dbg /tmp/dbg.c
   ```
   (Beklenen parola: `g1zl1_p4r0la`.)
2. GDB'de aç, `strcmp`'e breakpoint koy, çalıştır:
   ```bash
   gdb -q /tmp/dbg
   (gdb) break strcmp
   (gdb) run
   ```
   `Pass:` çıkınca bir deneme yaz (örn. `deneme`) ve Enter.
3. Program `strcmp`'te durur. Argümanları oku:
   ```
   (gdb) x/s $rdi
   (gdb) x/s $rsi
   ```
   Biri senin girdin, biri **beklenen parola** olmalı.
4. Doğrula — bulduğun parolayı gir:
   ```bash
   echo 'g1zl1_p4r0la' | /tmp/dbg
   ```
   `Correct!` görmelisin.
5. Bonus — adımlama farkını hisset: `run` ile tekrar başlat, `break main`, sonra birkaç `ni` at ve
   `p $rip`/`x/i $rip` ile nerede olduğunu izle. Bir `call`'a gelince önce `ni` (atla), sonra deneme
   olarak `si` (gir) yapıp farkı gör.

- [ ] **Kontrol:** `x/s $rsi` (veya `$rdi`) beklenen parolayı gösterdi mi? Parola "Correct!" verdi mi?
- [ ] **Kaydet:** `break strcmp`+`x/s $rdi/$rsi` = parola yakalama · `si`=gir `ni`=atla · `x/FMT adres` ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — parola yakalama**
> `break strcmp` + `run` sonrası `x/s $rsi` sabit bir string veriyor. Bu ne? → **Beklenen parola/key.**

**Kalıp 2 — adımlama seçimi**
> Bir `call rand` görüyorsun, içi umurunda değil. Hangi komut? → **`ni` (üstünden atla), `si` değil.**

**Kalıp 3 — PIE adresi**
> `break *0x1169` koydun ama program başka yerde. Neden? → **PIE/ASLR; gerçek adres yükleme tabanına göre kaydı. Sembol veya offset+base kullan.**

**Kalıp 4 — değer değiştirme**
> Bir kontrolü geçmek için `set $rax = 0` yaptın. Ne yapmış oldun? → **Karşılaştırma sonucunu canlı değiştirdim; akışı 'doğru' dalına zorladım (dinamik patch).**

---

## 60 saniyelik özet

- pwndbg = RE için düzenli GDB; her durakta register/stack/kod context'i basar.
- Başlat/durdur: `run`, `start`, `continue`, `break`. Girdi: `run < input`.
- Breakpoint: `break main` (sembol), `break *adres`, `break strcmp` (her çağrı). PIE'de sembol kullan.
- Adımlama: `step`/`next` (kaynak, içeri/atla), `stepi`/`nexti` (komut), `finish` (fonksiyondan çık).
- Kendi kodunu `si` ile izle, libc çağrılarını `ni` ile atla.
- Oku: `p/x $rax`, `x/s $rdi`, `x/4xg $rsp`. Değiştir: `set $rax=..`.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `step` ile `next` arasındaki fark bir `call` satırında ne olur?
2. `break strcmp` ne işe yarar, argümanları nasıl okursun?
3. `x/s $rdi` ile `p $rdi` arasındaki fark nedir?
4. PIE bir binary'de neden sabit adrese breakpoint koymak sorunludur?
5. `si` ile bir `call printf`'e girersen ne olur, doğrusu ne?

➡️ **Cevaplar:** [`cevaplar.md#01-gdb--pwndbg-temelleri`](cevaplar.md#01-gdb--pwndbg-temelleri) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`02-bellek-inceleme.md`](02-bellek-inceleme.md)
