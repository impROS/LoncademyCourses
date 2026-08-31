# 02 — Bellek Erişimi, Adresleme, LEA, Flag'ler

> **Alan:** x86-64 assembly okuma (%26)
> **Süre:** ~30 dakika okuma + 20 dakika pratik
> **Test:** [`02-test.html`](02-test.html) · 15 soru

---

## Neden bu konu

Bir programın verisi bellekte durur; assembly ona **adresleme kalıpları** ile erişir. `[rbp-0x8]`,
`[rax+rcx*4]`, `[rdi+0x10]` gibi ifadeler bir dizinin elemanına mı, bir struct alanına mı, yoksa bir
yerel değişkene mi baktığını söyler. Ayrıca **flag register** — CPU'nun "sonuç sıfır mıydı, negatif miydi"
hafızası — koşullu atlamaların temelidir. En sık hata: `[rax+rcx*4]` kalıbını görüp bunun bir **dizi
indeksi** olduğunu fark etmemek.

**Büyük fikir:** x86'da tek bir adresleme formülü vardır: `[taban + indeks*ölçek + offset]`. Bütün karmaşık
bellek erişimleri bu formülün özel halleridir. Flag'ler ise her aritmetik/karşılaştırma sonrası sessizce
güncellenen "durum lambaları"dır.

---

## 1. ⭐ Genel adresleme formülü

x86-64'te bir bellek operandı en fazla şu parçalardan oluşur:

```
[ taban + indeks * ölçek + offset ]
   base    index   scale   disp
```

- **taban (base):** bir register (örn. `rbp`, `rax`)
- **indeks (index):** bir register (örn. `rcx`)
- **ölçek (scale):** 1, 2, 4 veya 8 (eleman boyutu)
- **offset (disp):** sabit sayı (örn. `-0x8`)

| Kalıp | Ne demek |
|---|---|
| `[rbp-0x8]` | Yerel değişken (taban+offset) |
| `[rax]` | rax'in gösterdiği tek eleman |
| `[rax+rcx*4]` | `int` dizisi: `rax` taban, `rcx` indeks, 4 = `int` boyutu |
| `[rax+rcx*8]` | pointer/`long` dizisi (8 byte'lık elemanlar) |
| `[rdi+0x10]` | struct alanı: `rdi` struct'ın başı, `+0x10` alan offset'i |

**Hafıza kancası:** `*4` görürsen aklına **`int` dizisi** gelsin; `*8` → pointer/`long` dizisi; sabit
`+offset` tek başına → struct alanı veya yerel değişken.

> ⚠️ **Tuzak:** Ölçek her zaman eleman **boyutunu** yansıtır. `char` dizisinde ölçek 1, `int`'te 4,
> `long`/pointer'da 8. Ölçeği okuyarak eleman tipini tahmin edebilirsin.

---

## 2. ⭐ LEA'yı derinleştir — üç kullanımı

`lea` ("Load Effective Address") aynı adresleme formülünü kullanır ama **belleğe dokunmaz**, sadece
hesaplanan adresi (sayıyı) register'a yazar. Üç tipik kullanımı:

| Kullanım | Örnek | Sonuç |
|---|---|---|
| Gerçek adres alma | `lea rax, [rbp-0x20]` | Yerel bir tampon(buffer)ın adresi rax'e (pointer üretme) |
| Hızlı çarpma | `lea rax, [rdi+rdi*4]` | `rax = rdi*5` (bellek yok) |
| Toplama+kaydırma | `lea rax, [rdi+rsi*2+3]` | `rax = rdi + rsi*2 + 3` |

`lea rax, [rbp-0x20]` kalıbını gördüğünde genelde bir **buffer/dizi/string'in adresi** bir fonksiyona
argüman olarak hazırlanıyordur (örn. `scanf`, `strcpy` öncesi).

> ⚠️ **Tuzak:** `lea rax, [rbx]` ile `mov rax, [rbx]` aynı yazılışa rağmen zıt işler yapar: `lea` **adresi**,
> `mov` **veriyi** verir. Köşeli parantez sizi kandırmasın; komut adına bakın.

---

## 3. ⭐ Flag register (RFLAGS) — durum lambaları

Aritmetik/karşılaştırma komutları sonucu **atmasa bile** flag'leri günceller. Önemli olanlar:

| Flag | Adı | 1 olduğunda anlamı |
|---|---|---|
| **ZF** | Zero Flag | Sonuç **sıfır** (eşitlik!) |
| **SF** | Sign Flag | Sonuç **negatif** (MSB=1) |
| **CF** | Carry Flag | İşaretsiz taşma/borç |
| **OF** | Overflow Flag | İşaretli taşma |

Bunlar tek başına anlamlı değil; **koşullu atlamalar** (2.3) onları okur. Örneğin `cmp rax, rbx` sonrası:
- ZF=1 → `rax == rbx`
- ZF=0 → eşit değil

> ⚠️ **Tuzak:** `cmp` ve `test` **hiçbir register'ı değiştirmez** — sadece flag kurar. "cmp bir şey
> hesaplıyor" sanma; o sadece sonraki `je`/`jne`/`jg` için zemin hazırlar.

---

## 4. `cmp` ve `test` — flag üretenler

| Komut | Aslında yaptığı | Kullanımı |
|---|---|---|
| `cmp a, b` | `a - b` hesapla, sonucu **at**, flag'leri kur | İki değeri karşılaştır |
| `test a, b` | `a AND b` hesapla, sonucu at, flag kur | Genelde `test rax, rax` (rax sıfır mı?) |

`test rax, rax` → rax sıfırsa ZF=1. Bu, "rax null/0 mı?" kontrolünün standart kalıbıdır (örn. `malloc`
sonucu NULL mu, string sonu mu).

**Hafıza kancası:** `cmp` = "iki şeyi kıyasla", `test rax, rax` = "rax sıfır mı?".

---

## 5. Bellek genişliği belirteçleri

Bellek operandının kaç byte olduğunu belirsizse assembly açıkça yazar:

| Belirteç | Byte | Örnek |
|---|---|---|
| `byte ptr` | 1 | `mov byte [rax], 0x41` |
| `word ptr` | 2 | `mov word [rax], 0x4142` |
| `dword ptr` | 4 | `mov dword [rax], 0x1` |
| `qword ptr` | 8 | `mov qword [rax], 0x0` |

Register operandı varsa genişlik ondan bellidir (`al`=byte, `eax`=dword). Sabit yazılırken belirteç şart,
çünkü `mov [rax], 1` kaç byte yazacağını bilemez.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `[rax+rcx*4]` | int dizisi elemanı | `*8` olsa pointer/long olurdu |
| `lea` vs `mov` `[rbx]` | lea=adres, mov=veri | Aynı yazılış, zıt iş |
| `cmp`/`test` register'ı değiştirir mi | Hayır, sadece flag | Sonuç atılır |
| ZF=1 ne demek | Sonuç sıfır / eşit | SF negatiflik içindir |
| `test rax, rax` | rax sıfır mı kontrolü | İki farklı değer kıyaslama değil |

---

## 🖥 Pratik — dizi erişimini ve flag'i gör

> **Amaç:** `[base+index*scale]` kalıbını gerçek kodda görmek, cmp/flag akışını izlemek · **Süre:** 20 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Dizi kullanan bir program derle:
   ```bash
   cat > /tmp/arr.c <<'EOF'
   int topla(int *a, int n){ int s=0; for(int i=0;i<n;i++) s+=a[i]; return s; }
   int main(){ int v[3]={10,20,30}; return topla(v,3); }
   EOF
   gcc -O0 -o /tmp/arr /tmp/arr.c
   ```
2. `topla`'yı Intel sözdiziminde sök:
   ```bash
   objdump -d -M intel /tmp/arr | sed -n '/<topla>:/,/ret/p'
   ```
3. Çıktıda **ara**:
   - `[rax+rcx*4]` veya benzeri (`*4` = int elemanı) → `a[i]` erişimi
   - `cmp` + `jl`/`jge` → döngü koşulu (`i < n`)
   - `add` → `s += ...`
4. GDB'de flag'i canlı gör:
   ```bash
   gdb -q /tmp/arr
   (gdb) break topla
   (gdb) run
   (gdb) info registers eflags
   ```
   `next` ile ilerledikçe `eflags` içinde `ZF`, `SF` bayraklarının değiştiğini gözlemle.

- [ ] **Kontrol:** `*4` içeren bir bellek erişimi ve döngü `cmp`'ini bulabildin mi?
- [ ] **Kaydet:** `[base+idx*4]`=int dizisi · `lea`=adres · `test r,r`=sıfır mı · ZF=eşitlik ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — dizi indeksleme**
> `mov eax, [rdx+rcx*4]`. Ne oluyor? → **Bir `int` dizisinin `rcx`. elemanı okunuyor (taban `rdx`).**

**Kalıp 2 — buffer adresi**
> `lea rdi, [rbp-0x40]` hemen `call gets` öncesi. Ne oluyor? → **Yerel bir buffer'ın adresi 1. argüman olarak hazırlanıyor (overflow adayı, Bölüm 6).**

**Kalıp 3 — null kontrolü**
> `test rax, rax` + `je hata`. Ne kontrol ediliyor? → **rax sıfır (NULL) mı; sıfırsa `hata`'ya atla.**

**Kalıp 4 — struct alanı**
> `mov eax, [rdi+0x8]`. Ne olabilir? → **`rdi` bir struct pointer; `+0x8` offset'indeki alan okunuyor.**

---

## 60 saniyelik özet

- Adresleme: `[taban + indeks*ölçek + offset]`. Tüm bellek erişimleri bunun özel hali.
- Ölçek eleman boyutu: `*1`=char, `*4`=int, `*8`=pointer/long dizisi.
- `lea` = adresi/aritmetiği hesapla, belleğe dokunma; `mov [ ]` = veriyi oku/yaz.
- Flag'ler: ZF (sıfır/eşit), SF (negatif), CF/OF (taşma). Aritmetik onları sessizce kurar.
- `cmp a,b` = `a-b` sonucu at, flag kur; `test rax,rax` = rax sıfır mı.
- `byte/word/dword/qword ptr` bellek genişliğini belirtir (sabit yazarken şart).

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `[rbx+rsi*8]` muhtemelen hangi tip diziye erişiyor? Neden 8?
2. `lea rax, [rbp-0x30]` ile `mov rax, [rbp-0x30]` sonuçları nasıl farklı?
3. `cmp rax, 0` sonrası ZF=1 ise ne çıkarım yaparsın?
4. `test rax, rax` ne için kullanılır?
5. `mov dword [rax], 1` neden `dword ptr` belirtir?

---

## ✅ Test
➡️ **[02-test.html](02-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`03-kontrol-akisi.md`](03-kontrol-akisi.md)
