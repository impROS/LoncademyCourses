# 04 — Fonksiyon Çağrısı: System V ABI, Stack Frame, Argümanlar

> **Alan:** x86-64 assembly okuma (%26) — bölümü kapatan tutkal
> **Süre:** ~30 dakika okuma + 20 dakika pratik
> **Test:** [`04-test.html`](04-test.html) · 15 soru

---

## Neden bu konu

Programlar fonksiyonlardan oluşur; fonksiyonlar birbirini **belirli bir sözleşmeye** göre çağırır. Bu
sözleşme (System V AMD64 ABI) argümanların hangi register'da geçtiğini, dönüş değerinin nerede olduğunu,
stack frame'in nasıl kurulup yıkıldığını söyler. Bunu bilmezsen `call strcmp` gördüğünde "hangi iki string
karşılaştırılıyor" sorusunu cevaplayamazsın. En sık hata: argüman register sırasını (`rdi, rsi, rdx, rcx,
r8, r9`) bilmemek.

**Büyük fikir:** Linux x86-64'te fonksiyon çağrısının **tek bir standardı** vardır. Argümanlar sırayla
6 register'da, sonra stack'te; dönüş `rax`'te; frame `push rbp`/`mov rbp,rsp` ile kurulur. Bu kalıbı
ezberlersen her çağrıyı okursun.

---

## 1. ⭐ Argüman geçişi — 6 register kuralı

İlk 6 tamsayı/pointer argüman **sırayla** şu register'larda geçer:

| Argüman | Register | Kanca |
|---|---|---|
| 1. | `rdi` | **d**estination-ish |
| 2. | `rsi` | **s**ource-ish |
| 3. | `rdx` | |
| 4. | `rcx` | **c**ount-ish |
| 5. | `r8` | |
| 6. | `r9` | |
| 7.+ | stack | `[rsp]`, `[rsp+8]`… |

**Ezber cümlesi:** "**Di**ane's **si**lk **d**ress **c**osts **8** to **9**" → di, si, d(x), c(x), 8, 9.
(rdi, rsi, rdx, rcx, r8, r9).

- **Dönüş değeri:** `rax` (64-bit) / `eax` (32-bit).
- **Float/double argümanlar:** `xmm0`–`xmm7` (ayrı register seti).

> ⚠️ **Tuzak:** Bu sıra **Linux/System V** içindir. Windows x64 farklıdır (`rcx, rdx, r8, r9`). CTF'lerin
> çoğu Linux; ama bir Windows binary'sinde bu sırayı uygularsan argümanları yanlış okursun.

**Bunu kullanmak:** `call printf`'ten hemen önce `rdi`'ye ne konduysa o **format string**'tir; `rsi` ilk
değişkendir. `call strcmp` öncesi `rdi` ve `rsi` **karşılaştırılan iki string**'tir.

---

## 2. ⭐ Caller-saved vs callee-saved

Bir fonksiyon çağrıldığında hangi register'ların korunacağı da ABI'de yazılıdır:

| Tip | Register'lar | Kim korur |
|---|---|---|
| **Caller-saved** (volatile) | `rax, rcx, rdx, rsi, rdi, r8–r11` | Çağıran, gerekiyorsa kaydeder |
| **Callee-saved** (non-volatile) | `rbx, rbp, r12–r15`, `rsp` | Çağrılan fonksiyon korumalı |

> ⚠️ **Tuzak:** Argüman register'ları (`rdi` vb.) caller-saved'dır — bir `call`'dan sonra değerleri
> **bozulmuş** olabilir. "Argümanı rdi'ye koydum, call sonrası hâlâ orada" varsayımı yanlıştır; bu yüzden
> derleyici argümanları çoğu zaman yerel değişkene (`[rbp-...]`) kopyalar.

---

## 3. ⭐ Stack frame: prologue ve epilogue

Bir fonksiyon tipik olarak şöyle **açılır** (prologue):
```asm
push rbp            ; eski frame tabanını sakla
mov  rbp, rsp       ; yeni frame tabanı = mevcut tepe
sub  rsp, 0x20      ; yerel değişkenler için yer aç
```
ve şöyle **kapanır** (epilogue):
```asm
leave               ; = mov rsp, rbp ; pop rbp
ret                 ; dönüş adresine dön
```

`leave` iki komutun kısaltmasıdır: frame'i geri sarar (rsp'yi rbp'ye getirir, eski rbp'yi pop eder). Sonra
`ret` dönüş adresini çeker.

Frame'in yerleşimi (2. bölümdeki haritanın tamamı):

```
[rbp+0x10] ← 8.+ argümanlar (stack'te geçenler)
[rbp+0x8]  ← dönüş adresi (call koydu)
[rbp]      ← eski rbp (push rbp koydu)
[rbp-0x8]  ← 1. yerel değişken
[rbp-0x10] ← 2. yerel değişken   ← rsp buralarda
```

> ⚠️ **Tuzak:** `-O0`'da bu klasik prologue/epilogue görünür. `-O2`'de derleyici `rbp`'yi frame pointer
> olarak kullanmayabilir (`-fomit-frame-pointer`), `leave` yerine doğrudan `add rsp, N; ret` çıkabilir.
> "rbp yok" demek "frame yok" demek değildir.

---

## 4. Çağrı öncesi/sonrası okuma refleksi

Bir `call X` gördüğünde geriye doğru bak:

1. **`call`'tan hemen önceki `mov rdi, ...` / `lea rdi, ...`** → 1. argüman.
2. `rsi`, `rdx`… → sonraki argümanlar.
3. **`call`'tan sonraki ilk `mov ..., rax` / `test rax, rax`** → dönüş değeriyle ne yapıldığı.

Bu "önce argümanlar, sonra call, sonra dönüş" üçlüsü her fonksiyon çağrısını çözmenin kalıbıdır. Ghidra'nın
decompiler'ı tam olarak bunu otomatik yapıp `strcmp(input, "secret")` gibi okunur çağrılar üretir (Bölüm 3).

---

## 5. Yaygın libc çağrılarını tanımak

Argüman kuralını bilince tanıdık çağrılar okunur hale gelir:

| Çağrı | rdi | rsi | rdx | Ne yapar |
|---|---|---|---|---|
| `strcmp(a,b)` | a | b | — | eşitse rax=0 |
| `strcpy(dst,src)` | dst | src | — | kopyalar (sınırsız → overflow riski) |
| `memcpy(dst,src,n)` | dst | src | n | n byte kopyala |
| `printf(fmt,...)` | fmt | 1. arg | 2. arg | biçimli yazdır |
| `malloc(n)` | n | — | — | rax = yeni bellek (veya NULL) |

**Kanca:** `strcmp` sonrası `test eax, eax` + `jne` = "eşit değilse hata" → parola kontrolünün klasik izi.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| 1. argüman nerede | `rdi` | `rax` dönüş değeri |
| Dönüş değeri nerede | `rax`/`eax` | Argüman değil |
| Linux vs Windows argüman sırası | Linux: rdi,rsi,rdx,rcx,r8,r9 | Windows: rcx,rdx,r8,r9 |
| `leave` ne yapar | `mov rsp,rbp; pop rbp` | Tek başına dönmez, `ret` ayrı |
| Argüman register'ları call sonrası | Bozulmuş olabilir (caller-saved) | Korunmuş varsaymak yanlış |

---

## 🖥 Pratik — argümanları ve frame'i canlı izle

> **Amaç:** rdi/rsi'de argümanları görmek, prologue/epilogue'u tanımak · **Süre:** 20 dk
> **💸 Maliyet:** Yok

### Adımlar
1. strcmp çağıran program:
   ```bash
   cat > /tmp/abi.c <<'EOF'
   #include <string.h>
   #include <stdio.h>
   int kontrol(char*g){ return strcmp(g,"secret")==0; }
   int main(int c,char**v){ printf("%d\n", kontrol(v[1]?v[1]:"x")); return 0; }
   EOF
   gcc -O0 -o /tmp/abi /tmp/abi.c
   ```
2. GDB'de `strcmp` çağrısı anında argümanlara bak:
   ```bash
   gdb -q /tmp/abi
   (gdb) break strcmp
   (gdb) run deneme
   (gdb) x/s $rdi
   (gdb) x/s $rsi
   ```
   `$rdi` senin girdin (`"deneme"`), `$rsi` `"secret"` olmalı — yani karşılaştırılan iki string.
3. `kontrol` fonksiyonunun prologue'unu gör:
   ```bash
   objdump -d -M intel /tmp/abi | sed -n '/<kontrol>:/,/ret/p' | head -8
   ```
   Başta `push rbp` / `mov rbp, rsp`, sonda `leave`/`pop rbp` + `ret` olmalı.
4. Dönüş değerini gör: `strcmp` dönünce `rax`'e bak (eşitse 0):
   ```
   (gdb) finish
   (gdb) print $rax
   ```

- [ ] **Kontrol:** `$rsi` içinde `"secret"` çıktı mı? (Gizli parolayı ABI sayesinde buldun.)
- [ ] **Kaydet:** rdi,rsi,rdx,rcx,r8,r9 · dönüş=rax · strcmp'in 2. argümanı = beklenen parola ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — gizli parolayı bulma**
> `lea rsi, [rip+0x...]` sonra `call strcmp`. `rsi` neyi tutar? → **Karşılaştırılan 2. string — muhtemelen beklenen parola.**

**Kalıp 2 — argüman sayma**
> `call f`'ten önce rdi, rsi, rdx dolduruluyor. `f` kaç argüman alıyor? → **En az 3.**

**Kalıp 3 — dönüş kullanımı**
> `call malloc` sonrası `test rax, rax` + `je hata`. Ne kontrol ediliyor? → **malloc NULL döndü mü (bellek alınamadı mı).**

**Kalıp 4 — frame tanıma**
> `push rbp; mov rbp, rsp; sub rsp, 0x30`. Ne bu? → **Fonksiyon prologue'u; 48 byte yerel alan.**

---

## 60 saniyelik özet

- Argümanlar (Linux): `rdi, rsi, rdx, rcx, r8, r9`, sonra stack. Dönüş: `rax`.
- Windows sırası farklı (`rcx, rdx, r8, r9`) — CTF'de Linux varsay ama kontrol et.
- Caller-saved (`rax, rdi, rsi…`) call sonrası bozulabilir; callee-saved (`rbx, rbp, r12–r15`) korunur.
- Prologue: `push rbp; mov rbp, rsp; sub rsp, N`. Epilogue: `leave; ret`.
- Frame: `rbp+` tarafında dönüş adresi/argümanlar, `rbp-` tarafında yereller.
- `call X` çözmek: önce argüman register'larına bak, sonra `rax`'in nasıl kullanıldığına.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. 4. argüman hangi register'da geçer? Dönüş değeri nerede?
2. `push rbp; mov rbp, rsp` ne işe yarar?
3. `leave` hangi iki komuta eşdeğerdir?
4. `call strcmp` öncesi hangi register beklenen parolayı taşıyabilir?
5. Neden derleyici argümanları çoğu zaman `[rbp-...]`'e kopyalar?

---

## ✅ Test
➡️ **[04-test.html](04-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`../03-statik/01-ghidra-giris.md`](../03-statik/01-ghidra-giris.md)
