# 01 — Register'lar ve Temel Komut Seti

> **Alan:** x86-64 assembly okuma (%26) — bu bölümün en yoğun ve en kritik alanı
> **Süre:** ~30 dakika okuma + 20 dakika pratik
> **Test:** [`01-test.html`](01-test.html) · 16 soru

---

## Neden bu konu

Assembly okumak RE'nin merkezidir. Ama korkutucu görünmesinin sebebi genelde yanlış: aslında **çok az
sayıda komut** işlerin %90'ını yapar. `mov`, `add`, `sub`, `cmp`, `jmp`, `call`, `push`, `pop` — bunları
tanıyorsan çoğu binary'yi okuyabilirsin. En sık hata: her satırı ezberlemeye çalışmak. Doğrusu: birkaç
kalıbı tanımak ve gerisini o kalıplara oturtmak.

**Büyük fikir:** Assembly, register'lar (CPU'nun kutuları) üzerinde yapılan **basit tek işlemler**dizisidir.
Karmaşık görünen her şey, bu basit işlemlerin üst üste binmesidir.

---

## 1. ⭐ Register haritası — 64/32/16/8 bit parçalar

x86-64'te 16 genel amaçlı register var. Her biri farklı boyutlarda **aynı fiziksel** register'a bakar:

| 64-bit | 32-bit | 16-bit | 8-bit (düşük) | Tipik rolü |
|---|---|---|---|---|
| `rax` | `eax` | `ax` | `al` | Dönüş değeri, hesap |
| `rbx` | `ebx` | `bx` | `bl` | Genel (korunan) |
| `rcx` | `ecx` | `cx` | `cl` | Sayaç, 4. argüman |
| `rdx` | `edx` | `dx` | `dl` | 3. argüman, hesap |
| `rsi` | `esi` | `si` | `sil` | 2. argüman, kaynak |
| `rdi` | `edi` | `di` | `dil` | 1. argüman, hedef |
| `rbp` | `ebp` | `bp` | `bpl` | Frame tabanı |
| `rsp` | `esp` | `sp` | `spl` | Stack tepesi |
| `r8`–`r15` | `r8d`… | `r8w`… | `r8b`… | Ek argüman/genel |

**Kritik kural:** `eax`'e yazmak `rax`'in **üst 32 bitini sıfırlar**. Ama `al`'e yazmak `rax`'in
üstünü **korur** (sadece alt 8 bit değişir). Bu, "aynı register'a farklı boyutta yazma"nın en sık tuzağıdır.

> ⚠️ **Tuzak:** `mov eax, 5` → `rax` tamamen 5 olur (üst 32 bit sıfırlandı). `mov al, 5` → `rax`'in sadece
> son byte'ı 5 olur, üstü ne idiyse öyle kalır. Decompiler'ın "neden değer bozuldu" dediğin anları buradan gelir.

---

## 2. ⭐ `mov` ailesi — veri taşıma

`mov hedef, kaynak` = "kaynağı hedefe kopyala" (soldan sağa okumak yerine **sağdan sola**: kaynak → hedef).

| Komut | Anlamı |
|---|---|
| `mov rax, rbx` | rbx'i rax'e kopyala (register → register) |
| `mov rax, 5` | 5 sabitini rax'e (immediate → register) |
| `mov rax, [rbx]` | rbx'in gösterdiği bellekteki veriyi rax'e (memory → register) |
| `mov [rbx], rax` | rax'i rbx'in gösterdiği belleğe (register → memory) |
| `movzx eax, byte [rbx]` | 1 byte oku, üstünü **sıfırla** genişlet (zero-extend) |
| `movsx eax, byte [rbx]` | 1 byte oku, **işaret** koruyarak genişlet (sign-extend) |

> ⚠️ **Tuzak:** `mov [rbx], [rax]` **yoktur** — x86'da bellekten belleğe doğrudan `mov` olmaz. Önce
> register'a alınır. İki köşeli parantez bir arada görürsen yanlış okuyorsundur.

**Hafıza kancası:** Intel sözdiziminde her zaman **hedef önce, kaynak sonra**. AT&T'de tersi (`%` ve `$`'lı).
Bu kurs Intel sözdizimi kullanır (Ghidra/pwndbg varsayılanı).

---

## 3. ⭐ Aritmetik ve mantık

| Komut | Yaptığı | Not |
|---|---|---|
| `add rax, rbx` | `rax += rbx` | |
| `sub rax, rbx` | `rax -= rbx` | flag'leri de günceller |
| `inc rax` / `dec rax` | `+1` / `−1` | |
| `imul rax, rbx` | işaretli çarpma | |
| `and/or/xor rax, rbx` | bit işlemleri | |
| `xor rax, rax` | **rax'i sıfırla** | En yaygın "sıfırlama" kalıbı |
| `shl/shr rax, 3` | sola/sağa kaydır | `shl n` = ×2ⁿ, `shr n` = ÷2ⁿ |
| `neg rax` | işaret çevir (−rax) | |

> ⚠️ **Tuzak:** `xor rax, rax` bir XOR değil, bir **deyim**dir: register'ı sıfırlamanın kısa/hızlı yolu.
> `mov rax, 0` yerine derleyici bunu kullanır. "XOR görünce şifreleme sanma" — çoğu zaman sadece sıfırlama.

---

## 4. `lea` — adres hesaplama (aritmetik hilesi)

`lea rax, [rbx+rcx*4+8]` = "bu adresi **hesapla**, ama bellekten okuma — sonucu rax'e yaz".

- `mov rax, [rbx+8]` → rbx+8 adresindeki **veriyi** al.
- `lea rax, [rbx+8]` → sadece **rbx+8 sayısını** hesapla, rax'e koy.

`lea` çoğu zaman bellek erişimi değil, **hızlı aritmetik** için kullanılır: `lea rax, [rdi+rdi*2]` =
`rax = rdi*3`. Bunu 2.2'de derinleştireceğiz; şimdilik "lea = adres/aritmetik, [ ]'ye rağmen bellek okumaz"
kancasını tak.

---

## 5. ⭐ `push`/`pop`/`call`/`ret` — akış komutları

| Komut | Yaptığı |
|---|---|
| `push rax` | `rsp -= 8; [rsp] = rax` (stack'e it) |
| `pop rax` | `rax = [rsp]; rsp += 8` (stack'ten çek) |
| `call hedef` | dönüş adresini push et + hedefe atla |
| `ret` | stack'ten dönüş adresini pop et + oraya atla |
| `jmp hedef` | koşulsuz atla |
| `nop` | hiçbir şey yapma (dolgu/hizalama) |

Bunlar 2.3 (kontrol akışı) ve 2.4 (fonksiyon çağrısı) için köprü. `call`/`ret` ikilisi fonksiyonların
girip çıkmasını sağlar; overflow'da `ret`'in çektiği adresi ezmek her şeyin özüdür (Bölüm 6).

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `mov eax, 5` rax'e etkisi | rax tamamen 5 (üst 32 bit sıfır) | `al`'e yazsa üst korunurdu |
| `xor rax, rax` | rax'i sıfırlar (deyim) | Şifreleme değil |
| `lea rax, [rbx]` | rbx'in **değerini** rax'e (adres) | `mov` olsa bellekteki veriyi alırdı |
| `mov hedef, kaynak` yönü | kaynak → hedef | Soldan sağa okumak yanlış |
| `call` stack'e ne koyar | dönüş adresi | jmp koymaz |

---

## 🖥 Pratik — komutları objdump'ta tanı

> **Amaç:** Gerçek disassembly'de mov/add/xor/call kalıplarını gözle görmek · **Süre:** 20 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Küçük fonksiyon derle:
   ```bash
   cat > /tmp/asm.c <<'EOF'
   int hesap(int a, int b){ int t = a*3 + b; return t; }
   int main(){ return hesap(4,5); }
   EOF
   gcc -O0 -o /tmp/asm /tmp/asm.c
   ```
2. Sadece Intel sözdizimiyle `hesap`'ı sök:
   ```bash
   objdump -d -M intel /tmp/asm | sed -n '/<hesap>:/,/ret/p'
   ```
3. Çıktıda şunları **ara ve işaretle**:
   - `push rbp` / `mov rbp, rsp` → fonksiyon prologue (frame kurulumu, 2.4)
   - `mov ... [rbp-...]` → argümanların yerele kopyalanması
   - `imul` veya `lea` → çarpma (`a*3`)
   - `add` → toplama (`+ b`)
   - `pop rbp` / `ret` → epilogue + dönüş
4. `hesap` bir XOR ile başlıyor mu diye başka bir örnek: `xor eax, eax` kalıbını gör:
   ```bash
   objdump -d -M intel /tmp/asm | grep -E "xor|imul|lea|add" | head
   ```

- [ ] **Kontrol:** `imul`/`lea` (çarpma) ve `add` (toplama) satırlarını bulabildin mi?
- [ ] **Kaydet:** Intel: hedef önce · `xor r,r`=sıfırla · `lea`=adres/aritmetik · call=dönüş adresi push ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — sıfırlama deyimi**
> `xor eax, eax` görüyorsun. Ne yapar? → **eax'i (ve rax'in tümünü) sıfırlar.**

**Kalıp 2 — mov yönü**
> `mov [rbp-0x4], edi`. Ne oluyor? → **1. argüman (edi) yerel bir değişkene (`rbp-0x4`) kaydediliyor.**

**Kalıp 3 — lea vs mov**
> `lea rax, [rbx+rbx*2]`. Sonuç? → **rax = rbx*3 (bellek okumaz, aritmetik).**

**Kalıp 4 — register boyutu**
> `mov al, 0x41` bir döngüde. Neyi değiştirir? → **Sadece rax'in alt byte'ı; üst kısım korunur.**

---

## 60 saniyelik özet

- 16 genel register; her biri 64/32/16/8-bit dilimlerde aynı fiziksel yere bakar (`rax/eax/ax/al`).
- `eax`'e yazma üst 32 biti **sıfırlar**; `al`'e yazma üstü korur.
- Intel sözdizimi: `komut hedef, kaynak` (kaynak → hedef).
- `mov` kopyalar; bellekten belleğe doğrudan `mov` **yok**.
- `xor r, r` = sıfırlama deyimi (şifreleme değil). `lea` = adres/aritmetik, bellek okumaz.
- `push/pop` stack; `call` dönüş adresi push eder, `ret` onu çeker.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `mov eax, 0` ile `xor eax, eax` aynı işi mi yapar? Derleyici neden ikincisini seçer?
2. `al`'e yazmak `rax`'in üst 56 bitini etkiler mi?
3. `lea rax, [rdi+rdi*4]` sonucu nedir?
4. `mov rax, [rbx]` ile `lea rax, [rbx]` farkı?
5. `call` komutu stack'e ne koyar, `ret` ne çeker?

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 16 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`02-bellek-flag.md`](02-bellek-flag.md)
