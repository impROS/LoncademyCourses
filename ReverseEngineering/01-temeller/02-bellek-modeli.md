# 02 — Bellek Modeli: Stack, Heap, Register, Adres

> **Alan:** Makine seviyesi temeller (%16) — assembly'i okumadan önceki zihinsel harita
> **Süre:** ~25 dakika okuma + 15 dakika pratik
> **Test:** [`02-test.html`](02-test.html) · 14 soru

---

## Neden bu konu

Bir programı sökerken ekranda sürekli `[rbp-0x8]`, `[rsp]`, `mov rdi, rax` gibi ifadeler görürsün.
Bunlar **verinin nerede durduğunu** söyler: register'da mı, stack'te mi, heap'te mi? Bu haritayı
bilmezsen "yerel değişken" ile "global değişken"i, "geçici hesap" ile "kalıcı veri"yi ayıramazsın.
En sık hata: stack'in **yukarı mı aşağı mı** büyüdüğünü bilmemek ve `rbp-0x8`'in ne olduğunu ters
anlamak.

**Büyük fikir:** Bir programın belleği tek düz bir byte dizisidir (adres 0'dan yukarı). Farklı
"bölgeler" (stack, heap, kod, veri) bu dizinin farklı parçalarıdır ve **her birinin farklı ömrü ve
kuralı** vardır.

---

## 1. ⭐ Adres uzayı — tek büyük byte dizisi

Bir process çalışırken kendi **sanal adres uzayına** sahiptir: 0'dan başlayıp yukarı giden byte'lar.
Her byte'ın bir **adresi** vardır (x86-64'te 64-bit, pratikte 48-bit kullanılır).

Kabaca bölgeler (düşük adresten yükseğe):

| Bölge | Ne tutar | Ömrü | Yazılabilir? |
|---|---|---|---|
| **.text** (kod) | Makine komutları | Program boyu | Hayır (çalıştırılır) |
| **.rodata** | Sabitler, string'ler | Program boyu | Hayır |
| **.data / .bss** | Global/static değişkenler | Program boyu | Evet |
| **heap** | `malloc` ile alınan bellek | Sen serbest bırakana kadar | Evet |
| **stack** | Yerel değişkenler, dönüş adresleri | Fonksiyon boyu | Evet |

> ⚠️ **Tuzak:** String bir `.rodata`'daysa sabittir (kaynakta `"password"` gibi gömülü). Stack'teyse
> çalışma zamanında üretilmiştir (kullanıcıdan gelen girdi gibi). Nerede olduğu, nereden geldiğini söyler.

---

## 2. ⭐ Register — CPU'nun elindeki değişkenler

Register, işlemcinin **içindeki** çok hızlı, isimli kutulardır. Bellekte değil, CPU'da. RE'de en çok
bunları okursun (ayrıntı 2.1'de). Şimdilik rol dağılımı:

| Register | Tipik rolü |
|---|---|
| `rax` | Dönüş değeri, geçici hesap |
| `rdi, rsi, rdx, rcx, r8, r9` | Fonksiyon argümanları (sırayla 1–6) |
| `rsp` | **Stack pointer** — stack'in tepesi |
| `rbp` | **Base pointer** — mevcut stack frame'in tabanı |
| `rip` | **Instruction pointer** — sıradaki komutun adresi |

> ⚠️ **Tuzak:** `rsp` ve `rbp` sadece "iki register" değil; onlar stack'i *adresleyen* özel işaretçilerdir.
> `[rbp-0x8]` = "rbp'nin 8 byte gerisindeki bellek". Register'ın **kendisi** değil, gösterdiği bellek.

---

## 3. ⭐ Stack — aşağı doğru büyüyen defter

Stack, fonksiyon çağrılarının ve yerel değişkenlerin yaşadığı yerdir. İki temel gerçek:

1. **Stack aşağı büyür.** `push` yapınca `rsp` **azalır** (düşük adrese gider). Bu ters gibi gelir
   ama x86-64 böyledir.
2. **LIFO** (Last In First Out): en son `push`'lanan ilk `pop`'lanır.

`push rax` kabaca: `rsp -= 8; [rsp] = rax`.
`pop rax` kabaca: `rax = [rsp]; rsp += 8`.

Bir fonksiyon çalışırken **stack frame** kurar:

```
yüksek adres
  ...            ← çağıranın verileri
  dönüş adresi   ← call bunu buraya koydu (rip nereye dönecek)
  eski rbp       ← push rbp
  [rbp-0x8]      ← 1. yerel değişken
  [rbp-0x10]     ← 2. yerel değişken
  ...
düşük adres      ← rsp (tepe)
```

**Yerel değişkenler `rbp`'nin gerisinde** (`rbp-0x…`), **fonksiyon argümanları ve dönüş adresi ileride**
(`rbp+0x…`). Bu ayrım overflow'da hayati (6. bölümde göreceksin).

> ⚠️ **Tuzak:** "Stack büyüyor" = `rsp` küçülüyor. Bir `sub rsp, 0x20` görürsen fonksiyon kendine
> 32 byte'lık yerel alan açtı demektir — stack'i *büyüttü*.

---

## 4. Heap — elle yönetilen uzun ömürlü bellek

Heap, `malloc`/`new` ile istenen ve `free`/`delete` ile bırakılan bellektir.

| | Stack | Heap |
|---|---|---|
| Kim yönetir | Derleyici (otomatik) | Programcı (elle) |
| Ömür | Fonksiyon bitince gider | `free` edilene kadar |
| Hız | Çok hızlı | Daha yavaş |
| Büyüme yönü | Aşağı | Yukarı (kabaca) |
| Tipik hata | Overflow, dangling frame | Use-after-free, leak, double-free |

RE'de bir işaretçinin heap'i mi stack'i mi gösterdiğini adres aralığından ve nasıl alındığından
(bir `malloc` çağrısı sonrası mı) anlarsın.

---

## 5. Pointer — adres tutan değer

**Pointer** (işaretçi) = başka bir byte'ın adresini tutan değer. `rdi` bir string'in adresini
tutuyorsa, `rdi` pointer'dır; `[rdi]` o adresteki byte'tır.

- `rax` = `0x7fffffffe4c0` → bu bir **adres** (muhtemelen stack).
- `[rax]` → o adresteki **veri**.
- `mov rbx, [rax]` → adresteki veriyi `rbx`'e al ("dereference").
- `lea rbx, [rax+0x8]` → adresin **kendisini** hesapla, veriyi okuma (2.2'de detay).

**Hafıza kancası:** Köşeli parantez `[...]` = "bu adresteki bellek". Parantezsiz = değerin/adresin kendisi.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| Stack hangi yöne büyür | Aşağı (rsp azalır) | Sezgi "yukarı" der, x86-64 aksi |
| `[rbp-0x8]` nedir | Yerel değişken (bellekte) | rbp register'ının kendisi değil |
| `rax` vs `[rax]` | Değer vs o adresteki bellek | Parantez dereference demek |
| Yerel değişkeni kim temizler | Derleyici (stack, otomatik) | Heap'i programcı `free` eder |
| `push`'ta rsp ne olur | Azalır | Stack aşağı büyür |

---

## 🖥 Pratik — stack'i canlı gör

> **Amaç:** rsp'nin push ile azaldığını ve yerel değişkenlerin rbp altında olduğunu gözlemlemek · **Süre:** 15 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Küçük bir program derle (C bilmesen de kopyala-yapıştır yeter):
   ```bash
   cat > /tmp/stk.c <<'EOF'
   #include <stdio.h>
   int topla(int a, int b){ int s = a + b; return s; }
   int main(){ printf("%d\n", topla(2,3)); return 0; }
   EOF
   gcc -g -O0 -o /tmp/stk /tmp/stk.c
   ```
2. GDB'de aç, `topla`'ya breakpoint koy, çalıştır:
   ```bash
   gdb -q /tmp/stk
   (gdb) break topla
   (gdb) run
   ```
3. Register'lara bak — `rsp` ve `rbp` adreslerini oku:
   ```
   (gdb) info registers rsp rbp
   ```
   `rbp` `rsp`'den büyük (yüksek) olmalı — stack aşağı büyüdüğü için tepe (rsp) daha düşük adreste.
4. Fonksiyondan devam et, `s` değişkeninin nerede olduğuna bak:
   ```
   (gdb) next
   (gdb) next
   (gdb) print &s
   ```
   `&s` adresi `rbp`'den **küçük** olmalı (yani `rbp - küçük bir sayı`).

- [ ] **Kontrol:** `rbp > rsp` ve `&s < rbp` gördün mü? (Bu, "yerel değişken rbp altında" demek.)
- [ ] **Kaydet:** Stack aşağı büyür · yerel değişken = `rbp-0x…` · argümanlar register'da geldi ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — bölge tanıma**
> Bir string `.rodata`'da gömülü. Bu string kullanıcı girdisi olabilir mi? → **Hayır, sabit; kaynağa gömülmüş.**

**Kalıp 2 — stack yönü**
> `sub rsp, 0x30` bir fonksiyon başında. Ne yapıyor? → **48 byte yerel alan açıyor (stack'i büyütüyor).**

**Kalıp 3 — pointer vs değer**
> `mov rax, [rdi]` ile `mov rax, rdi` farkı? → **İlki rdi'nin gösterdiği bellekteki veri, ikincisi rdi'nin (adresin) kendisi.**

**Kalıp 4 — yerel mi argüman mı**
> `[rbp-0x4]` ve `[rbp+0x10]`. Hangisi yerel değişken? → **`rbp-0x4` yerel; `rbp+` çağıran tarafında.**

---

## 60 saniyelik özet

- Bellek = 0'dan yukarı tek byte dizisi; bölgeler: kod/.text, .rodata, .data, heap, stack.
- Register CPU içinde; `rsp`=stack tepesi, `rbp`=frame tabanı, `rip`=sıradaki komut.
- Stack **aşağı** büyür: `push` → `rsp` azalır; LIFO.
- Yerel değişkenler `rbp-0x…`; dönüş adresi ve argümanlar `rbp+` tarafında.
- Heap elle yönetilir (`malloc`/`free`); stack otomatik.
- `[x]` = x adresindeki bellek (dereference); `x` = değerin/adresin kendisi.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `push rbx`'ten sonra `rsp` büyür mü küçülür mü? Neden?
2. `[rbp-0x18]` neyi ifade eder — yerel değişken mi argüman mı?
3. Bir string'in `.rodata`'da olması onun hakkında ne söyler?
4. `mov rax, rdi` ile `mov rax, [rdi]` arasındaki fark nedir?
5. `sub rsp, 0x20` fonksiyonun başında ne yapıyor?

---

## ✅ Test
➡️ **[02-test.html](02-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`03-derleme-elf.md`](03-derleme-elf.md)
