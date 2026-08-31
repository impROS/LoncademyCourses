# 01 — Sayı Sistemleri, Bit, Byte, Endianness

> **Alan:** Makine seviyesi temeller (%16) — bu bölümün ilk ve en temel taşı
> **Süre:** ~25 dakika okuma + 15 dakika pratik
> **Test:** [`01-test.html`](01-test.html) · 14 soru

---

## Neden bu konu

RE'de ekranda gördüğün her şey sayıdır: `0x401136`, `mov eax, 0x2a`, `48 89 e5`. Bunları
"okuyamıyorsan" disassembly çıktısı senin için anlamsız bir gürültüdür. Bu konu, o gürültüyü
**dile** çevirir. En sık yapılan hata: onaltılık (hex) ile onluğu karıştırmak ve `0x10`'u "on"
sanmak — oysa `0x10 = 16`. İkinci en sık hata: byte'ların bellekte **hangi sırayla** durduğunu
(endianness) bilmemek, `41 42 43 44` görüp değeri ters okumak.

**Büyük fikir:** Bilgisayarın tek bildiği şey **bit dizileri**dir. Hex, ikilik (binary) ve onluk
aynı sayının farklı *gösterimleri*dir — sen hangi gözlükle bakacağını seçersin.

---

## 1. ⭐ Bit, byte ve neden hep 8'in katı

- **Bit** = 0 veya 1. Tek bir anahtar.
- **Byte** = 8 bit. Belleğin en küçük *adreslenebilir* birimi. Tek bir bit'in adresi yoktur; byte'ın vardır.
- 1 byte 0–255 arası (veya işaretliyse −128…+127) bir değer tutar.

RE'de gördüğün genişlikler hep byte katıdır:

| İsim | Bit | Byte | x86-64 register örneği | C tipi (kabaca) |
|---|---|---|---|---|
| byte | 8 | 1 | `al` | `char` |
| word | 16 | 2 | `ax` | `short` |
| dword | 32 | 4 | `eax` | `int` |
| qword | 64 | 8 | `rax` | `long` / pointer |

> ⚠️ **Tuzak:** Assembly'de "word" = **16 bit**, "dword" = 32 bit, "qword" = 64 bit. Günlük dilde
> "word" işlemci kelime boyu (64 bit) sanılır. RE'de Intel/AT&T sözlüğündeki 16-bit anlamı geçerlidir.

---

## 2. ⭐ Hex neden her yerde

Bir byte = 8 bit = tam olarak **iki hex hanesi**. Bu yüzden RE hex konuşur: hizalı, kısa, byte sınırı belli.

| Gösterim | Örnek | Nasıl tanırsın |
|---|---|---|
| İkilik (binary) | `0b01000001` | `0b` öneki veya sadece 0/1 |
| Onluk (decimal) | `65` | önek yok |
| Onaltılık (hex) | `0x41` | `0x` öneki (veya sonda `h`: `41h`) |

Üçü de **aynı sayı: 65**. Ezberlenecek köprüler:

| Onluk | Hex | İkilik | Not |
|---|---|---|---|
| 10 | `0x0A` | `1010` | "on" ≠ `0x10` |
| 15 | `0x0F` | `1111` | tek hex hanenin tavanı |
| 16 | `0x10` | `1 0000` | hex "onda bir basamak atlar" |
| 255 | `0xFF` | `1111 1111` | bir byte'ın tavanı |
| 256 | `0x100` | `1 0000 0000` | bir byte taştı, ikinci byte başladı |
| 4096 | `0x1000` | — | sık görülen sayfa/hizalama sınırı |

**Hafıza kancası:** Her hex hane 4 bit ("nibble"). `0xFF` iki nibble = iki `1111` = 8 bit dolu.

> ⚠️ **Tuzak:** `0x10` on **değil** on-altı. Adresler ve offset'ler hep hex — `+0x10` demek "16 byte ileri".

---

## 3. İşaretli mi, işaretsiz mi (two's complement)

Aynı 8 bit iki farklı okunabilir. `0xFF`:
- **İşaretsiz (unsigned):** 255
- **İşaretli (signed):** −1

Negatifler **two's complement** ile tutulur: en üst bit (MSB) 1 ise sayı negatiftir. Kuralı:
"bitleri ters çevir, 1 ekle" negatifini verir.

| Byte (hex) | İşaretsiz | İşaretli |
|---|---|---|
| `0x00` | 0 | 0 |
| `0x7F` | 127 | +127 |
| `0x80` | 128 | −128 |
| `0xFF` | 255 | −1 |

> ⚠️ **Tuzak:** Decompiler bir değeri `4294967295` (unsigned) mi yoksa `-1` (signed) mi gösteriyor —
> ikisi de aynı `0xFFFFFFFF`. Karşılaştırmanın `jg` (signed) mi `ja` (unsigned) mi olduğu buna bağlıdır
> (bunu 2.3'te göreceksin). Yanlış yorumlarsan döngü sınırını ters anlarsın.

---

## 4. ⭐ Endianness — byte'lar bellekte hangi sırada

Çok baytlı bir sayının byte'ları belleğe iki türlü dizilebilir:

- **Little-endian:** en düşük değerli byte önce (düşük adreste). **x86-64 böyledir.**
- **Big-endian:** en yüksek değerli byte önce. Ağ protokolleri ("network byte order") ve bazı işlemciler.

`0x41424344` (dword) little-endian bellekte:

| Adres | +0 | +1 | +2 | +3 |
|---|---|---|---|---|
| Byte | `44` | `43` | `42` | `41` |

Yani `gdb` veya hex editörde ham byte'ları soldan sağa okursan **`44 43 42 41`** görürsün — sayı ise
`0x41424344`. Ters gibi durması normaldir.

> ⚠️ **Tuzak:** CTF'de "flag'i stack'te buldum" deyip byte'ları düz okuyunca string ters çıkar.
> `41 42 43 44` ham byte'lar aslında ASCII olarak `"ABCD"` — ama bir **sayı** olarak okunursa
> `0x44434241`. String mi sayı mı olduğunu bağlam söyler.

**Hafıza kancası:** Little-endian = "**küçük** uç **önce**". Intel = little. Ağ = big.

---

## 5. ASCII — sayıdan karaktere

Karakterler de sayıdır. `0x41` = `'A'`, `0x61` = `'a'`, `0x30` = `'0'`.

| Aralık | Anlam |
|---|---|
| `0x30`–`0x39` | `'0'`–`'9'` (rakamlar) |
| `0x41`–`0x5A` | `'A'`–`'Z'` |
| `0x61`–`0x7A` | `'a'`–`'z'` |
| `0x00` | string sonu (`NUL`) — C string'ler bununla biter |
| `0x20` | boşluk |

**Kancalar:** `'A' = 0x41 = 65`. `'a'` ile `'A'` arası tam `0x20` (32) — küçük/büyük harf farkı bir bit
(`0x20`). `'0' = 0x30`, yani `'0'`'dan çıkarınca rakamın sayısal değeri gelir.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `0x10` kaç eder | 16 | Onluk "10" ile karıştırılır; hex'te 16 |
| `0xFF` işaretli byte olarak | −1 | 255 sadece *unsigned* okuma |
| `word` assembly'de kaç bit | 16 | İşlemci "kelime"si (64) ile karışır |
| Bellekte `44 43 42 41` byte'ları, dword değeri | `0x41424344` | Little-endian; düz okumak yanlış |
| `'A'` karakterinin değeri | `0x41` (65) | `0x61` küçük `'a'` |

---

## 🖥 Pratik — sayıları elinle çevir ve endianness'i gör

> **Amaç:** Hex/onluk çevirisini ve little-endian'ı somut görmek · **Süre:** 15 dk
> **💸 Maliyet:** Yok (yerel terminal)

### Adımlar
1. Terminali aç. Python ile hızlı çevirici:
   ```bash
   python3 -c "print(0x41, 0xFF, 0x10)"
   ```
   Beklenen: `65 255 16`.
2. Ters yön (onluktan hex'e) ve binary:
   ```bash
   python3 -c "print(hex(65), bin(65))"
   ```
   Beklenen: `0x41 0b1000001`.
3. İşaretli okuma — `0xFF` bir byte olarak −1 mi:
   ```bash
   python3 -c "import struct; print(struct.unpack('b', b'\xff')[0])"
   ```
   Beklenen: `-1`. (`'b'` = signed byte, `'B'` olsaydı 255.)
4. Endianness'i gör. `0x41424344` sayısını little-endian byte'lara aç:
   ```bash
   python3 -c "import struct; print(struct.pack('<I', 0x41424344))"
   ```
   Beklenen: `b'DCBA'` — yani byte'lar `44 43 42 41`, ASCII `D C B A`. `<` little, `I` = 4-byte unsigned.
   `>I` yaparsan `b'ABCD'` görürsün (big-endian).

- [ ] **Kontrol:** 3. adımda `-1`, 4. adımda `b'DCBA'` gördün mü?
- [ ] **Kaydet:** `0x41='A'`, `0xFF=−1 (signed)`, `x86-64 = little-endian` ______

*(Ücret doğuran adım yok; temizlik gerekmez.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — hex offset okuma**
> Disassembly'de `mov eax, [rbp-0x10]` görüyorsun. `rbp`'den kaç byte geride? → **16 byte** (`0x10`).

**Kalıp 2 — endianness**
> GDB'de `x/4xb $rsp` çıktısı `0x44 0x43 0x42 0x41`. Buradaki dword değeri? → **`0x41424344`**.

**Kalıp 3 — signed/unsigned**
> Bir byte karşılaştırması `0xFF`'i `-1` gösteriyor. Değer aslında hangi unsigned sayı? → **255**.

**Kalıp 4 — ASCII sabiti**
> `cmp al, 0x41` bir karakter karşılaştırması. Hangi karaktere bakıyor? → **`'A'`**.

---

## 60 saniyelik özet

- 1 byte = 8 bit = 2 hex hane; değer aralığı 0–255 (veya −128…+127).
- `0x` = hex. `0x10 = 16`, `0xFF = 255`, `0x100 = 256`.
- Aynı byte işaretli/işaretsiz farklı okunur: `0xFF` = 255 veya −1 (two's complement).
- x86-64 **little-endian**: düşük byte düşük adreste; ham byte'lar ters görünür.
- `word=16`, `dword=32`, `qword=64` bit (assembly sözlüğü).
- ASCII: `'A'=0x41`, `'a'=0x61`, `'0'=0x30`, string sonu `0x00`.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `0x2A` onlukta kaç? `mov eax, 0x2a` hangi sayıyı yüklüyor?
2. Bellekte byte'lar `90 78 56 34` (soldan sağa). Little-endian dword değeri nedir?
3. `0x80` bir byte olarak signed kaç? Neden negatif?
4. `'z'` ile `'a'` arasında kaç değer var? `'z'`'nin hex'i tahminen ne?
5. `dword` kaç byte, `qword` kaç byte? `eax` hangisi?

➡️ **Cevaplar:** [`cevaplar.md#01-sayı-sistemleri-bit-byte-endianness`](cevaplar.md#01-sayı-sistemleri-bit-byte-endianness) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`02-bellek-modeli.md`](02-bellek-modeli.md)
