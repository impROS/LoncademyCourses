# 03 — Kontrol Akışı: Jump, Cmp, Döngü, If/Switch

> **Alan:** x86-64 assembly okuma (%26) — kodun "kararlarını" okumak
> **Süre:** ~30 dakika okuma + 20 dakika pratik
> **Test:** [`03-test.html`](03-test.html) · 16 soru

---

## Neden bu konu

Bir programın **mantığı** kontrol akışında saklıdır: nerede karar veriyor, nerede döngü kuruyor, hangi
şart doğruysa "Correct!" yazıyor. Crackme çözmenin özü budur — "flag doğruysa buraya, yanlışsa şuraya"
dallanmasını bulmak. En sık hata: koşullu atlamaların **hangi flag'e** baktığını bilmemek ve `jg` (signed)
ile `ja` (unsigned) arasındaki farkı kaçırmak — bu, döngü sınırını veya karşılaştırmayı ters anlatır.

**Büyük fikir:** Assembly'de `if`, `for`, `while`, `switch` diye komut yoktur. Hepsi **`cmp` + koşullu
`jmp`** ikilisine iner. Bu ikiliyi tanırsan yüksek seviye mantığı geri kurabilirsin.

---

## 1. ⭐ Koşulsuz ve koşullu atlama

- `jmp hedef` → her zaman atla (koşulsuz). `goto` gibi.
- `je/jne/jg/...` → flag'lere bakıp **koşullu** atla.

Koşullu atlamalar 2.2'deki flag'leri okur. Bir `cmp`'ten sonra gelirler:

| Komut | Atlar eğer | Flag | İşaretli mi |
|---|---|---|---|
| `je` / `jz` | eşit / sıfır | ZF=1 | — |
| `jne` / `jnz` | eşit değil | ZF=0 | — |
| `jg` / `jnle` | büyük | ZF=0 & SF=OF | **signed** |
| `jge` | büyük veya eşit | SF=OF | **signed** |
| `jl` / `jnge` | küçük | SF≠OF | **signed** |
| `jle` | küçük veya eşit | ZF=1 veya SF≠OF | **signed** |
| `ja` | büyük (above) | CF=0 & ZF=0 | **unsigned** |
| `jb` | küçük (below) | CF=1 | **unsigned** |
| `js` | negatif | SF=1 | — |

**Ezberleme yerine kancala:** `j` + koşul harfi. `e`=equal, `n`=not, `g`=greater(signed), `l`=less(signed),
`a`=above(unsigned), `b`=below(unsigned).

> ⚠️ **Tuzak:** `jg`/`jl` **işaretli**, `ja`/`jb` **işaretsiz** karşılaştırma içindir. `0xFFFFFFFF`
> signed'da −1 (küçük), unsigned'da çok büyük. Hangi atlamanın kullanıldığı, verinin işaretli mi olduğunu
> ele verir — decompiler tip tahmininin kaynağı budur.

---

## 2. ⭐ if / else kalıbı

Yüksek seviye:
```c
if (x == 5) { A(); } else { B(); }
```
Assembly'ye çevirisi (kabaca):
```asm
    cmp  dword [rbp-0x4], 5   ; x == 5 ?
    jne  .else                ; değilse else'e atla
    call A
    jmp  .son
.else:
    call B
.son:
```

**Okuma refleksi:** Koşullu atlama genelde **ters koşulu** taşır. Kaynakta `if (x==5)` ama assembly'de
`jne` (eşit *değilse* atla) görürsün — çünkü "şart tutmuyorsa gövdeyi atla" daha verimli.

> ⚠️ **Tuzak:** `jne .else` gördüğünde "eşit değilse" diye okursun ama kaynaktaki `if` **eşitlik**
> kontrolüdür. Atlamanın koşulu, kaynak koşulunun **tersidir**. Bu ters çevirmeyi kaçırma.

---

## 3. ⭐ Döngü (for / while)

```c
for (int i=0; i<n; i++) { gövde; }
```
Tipik assembly:
```asm
    mov  dword [rbp-0x4], 0       ; i = 0
    jmp  .kontrol
.gövde:
    ...                            ; döngü gövdesi
    add  dword [rbp-0x4], 1        ; i++
.kontrol:
    cmp  dword [rbp-0x4], [rbp-0x8] ; i < n ?
    jl   .gövde                    ; küçükse tekrar gövdeye
```

**Tanıma refleksi:** **Geriye doğru atlama (yukarı bir adrese `jl`/`jne`) = döngü.** Adres numarası
düşen bir atlama görürsen orada dönen bir şey vardır.

> ⚠️ **Tuzak:** `-O2`/`-O3` döngüyü açabilir (unroll), sayaç register'da tutulabilir, koşul başa alınabilir.
> Optimize kodda "temiz for" beklemeyin; geriye atlamayı ve sayaç artışını arayın.

---

## 4. switch — atlama tablosu (jump table)

Küçük, yoğun `switch`'ler bir **jump table** ile derlenir: değeri indeks yapıp adres tablosundan
doğrudan hedefe atlar.

```asm
    cmp  eax, 5
    ja   .default          ; 0..5 dışında ise default
    jmp  [.table + rax*8]   ; tablodan case adresine atla
```

`jmp [table + reg*8]` kalıbı (dolaylı atlama, indeksli) **neredeyse her zaman bir switch**'tir. Seyrek
veya aralıklı case'lerde derleyici bunun yerine `cmp` zinciri (if/else if) üretir.

**Hafıza kancası:** `jmp [tablo + rax*8]` = switch/jump table. Art arda `cmp`+`je` zinciri = if/else if.

---

## 5. Kısa devre ve karşılaştırma zincirleri

`if (a && b)` ve `if (a || b)` **kısa devre** ile çevrilir:
- `&&`: ilk şart yanlışsa ikinciyi hiç kontrol etme (erken atla).
- `||`: ilk şart doğruysa ikinciyi atla (erken gövdeye gir).

Yani art arda gelen `cmp`+`j..` çiftleri bir **bileşik koşul** olabilir. Her `cmp`'i ayrı sanma; birlikte
bir mantıksal ifade oluştururlar. Crackme'lerde "parola uzunluğu doğru **ve** ilk karakter 'A' **ve** …"
tarzı zincirler böyle görünür.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `jg` vs `ja` | jg signed, ja unsigned | İkisi de "büyükse atla" ama işaret farkı |
| `je` hangi flag | ZF=1 | SF sign içindir |
| Geriye atlama (`jl` yukarı) | Döngü | İleri atlama genelde if/else |
| `jmp [tablo+rax*8]` | switch/jump table | if/else if `cmp` zinciridir |
| `jne .else` kaynak koşulu | `if (eşit)` — atlama ters koşul | "eşit değil" diye okumak yanlış |

---

## 🖥 Pratik — if, döngü ve switch'i tanı

> **Amaç:** cmp+jump ikilisini, döngüdeki geri atlamayı ve jump table'ı gözle ayırt etmek · **Süre:** 20 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Üç kalıbı içeren program:
   ```bash
   cat > /tmp/ctrl.c <<'EOF'
   #include <stdio.h>
   int f(int x){
     int s=0;
     for(int i=0;i<x;i++) s+=i;      // döngü
     if(s>10) s=100; else s=0;        // if/else
     switch(x){ case 1:return 11;case 2:return 22;case 3:return 33;default:return s;}
   }
   int main(int c,char**v){ return f(c); }
   EOF
   gcc -O0 -o /tmp/ctrl /tmp/ctrl.c
   ```
2. `f`'i sök:
   ```bash
   objdump -d -M intel /tmp/ctrl | sed -n '/<f>:/,/ret/p'
   ```
3. **Bul ve işaretle:**
   - Döngü: bir `cmp` + `jl`/`jge`, sonrasında **daha küçük bir adrese** geri `jmp`/`jl`.
   - if/else: bir `cmp` + `jle`/`jg`, iki ayrı kola ayrılma.
   - switch: ya `jmp [... rax*8]` (jump table) ya da art arda `cmp eax, 1` / `cmp eax, 2` zinciri.
4. Karşılaştır: aynı programı `-O2` ile derleyip farkı gör:
   ```bash
   gcc -O2 -o /tmp/ctrl2 /tmp/ctrl.c
   objdump -d -M intel /tmp/ctrl2 | sed -n '/<f>:/,/ret/p' | head -40
   ```
   Döngünün nasıl değiştiğini (belki sayaç register'da, belki açılmış) gözlemle.

- [ ] **Kontrol:** Geriye atlayan bir döngü ve en az bir `cmp`+koşullu atlama çifti bulabildin mi?
- [ ] **Kaydet:** geri atlama=döngü · `jne` ters koşul · `jg/ja`=signed/unsigned · `jmp [t+r*8]`=switch ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — parola dalı**
> `cmp eax, edx` + `jne .yanlis`. Ne oluyor? → **İki değer eşit değilse "yanlış" koluna; eşitse devam (doğru parola kalıbı).**

**Kalıp 2 — döngü tanıma**
> Adres 0x1180'de `jl 0x1150` (yukarı). Ne bu? → **Bir döngü (geriye atlama).**

**Kalıp 3 — signed/unsigned ipucu**
> Sınır kontrolü `ja .hata` ile yapılıyor. Değişken hakkında ne öğrenirsin? → **İşaretsiz (unsigned) karşılaştırılıyor; muhtemelen boyut/uzunluk.**

**Kalıp 4 — switch**
> `jmp qword [rax*8 + 0x4020]`. Bu yapı? → **Bir switch/jump table.**

---

## 60 saniyelik özet

- Assembly'de if/for/while/switch yok; hepsi `cmp`/`test` + koşullu `jmp`.
- `je`=ZF (eşit), `jg/jl`=signed, `ja/jb`=unsigned. İşaret farkı kritik.
- Koşullu atlama genelde kaynak koşulunun **tersini** taşır (`if(==)` → `jne`).
- Geriye (küçük adrese) atlama = döngü işareti.
- `jmp [tablo+reg*8]` = switch/jump table; `cmp` zinciri = if/else if.
- `&&`/`||` kısa devre ile art arda `cmp`+`j..` çiftlerine dönüşür.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `je` hangi flag'e bakar? `jne`?
2. `jg` ile `ja` arasındaki fark ne, hangisi işaretli?
3. Kaynakta `if (x==5)` iken assembly'de neden `jne` görürsün?
4. Bir döngüyü disassembly'de nasıl tanırsın?
5. `jmp [rax*8 + 0x4020]` sana ne söyler?

➡️ **Cevaplar:** [`cevaplar.md#03-kontrol-akışı-jump-cmp-döngü-ifswitch`](cevaplar.md#03-kontrol-akışı-jump-cmp-döngü-ifswitch) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[03-test.html](03-test.html)** — 16 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`04-fonksiyon-abi.md`](04-fonksiyon-abi.md)
