# 02 — Bellek/Stack İnceleme, Watch, Canlı Veri Takibi

> **Alan:** Dinamik analiz (%18) — çalışan programın belleğini okumak
> **Süre:** ~30 dakika okuma + 25 dakika pratik
> **Test:** [`02-test.html`](02-test.html) · 15 soru

---

## Neden bu konu

Register'lar anlık değerleri tutar; asıl veri **bellektedir** — string'ler, tamponlar (buffer), diziler,
struct'lar. Bir crackme'nin girdiyi nasıl işlediğini görmek için o belleği canlı okuman gerekir: buffer'a
ne yazıldı, dönüşümden sonra ne oldu, karşılaştırmaya hangi değer girdi. En sık hata: bir değişkenin *ne
zaman* değiştiğini elle adım adım aramak — oysa **watchpoint** bunu otomatik yakalar.

**Büyük fikir:** Dinamik analizde bellek senin en zengin bilgi kaynağındır. `x` ile herhangi bir adresi
okur, watchpoint ile "şu değer değişince dur" dersin. Statikte "buraya bir şey yazılıyor" dediğin yeri,
dinamikte "işte tam olarak bu byte'lar yazıldı" diye görürsün.

---

## 1. ⭐ Belleği okumanın yolları (x komutunu ustalaşmak)

`x/[sayı][format][boyut] adres` — 4.1'de tanıttık, şimdi derinleştiriyoruz:

| Komut | Ne gösterir |
|---|---|
| `x/s 0x404040` | Adresteki string |
| `x/20xb $rsp` | rsp'den 20 byte, hex |
| `x/8xg $rsp` | rsp'den 8 qword (stack dökümü) |
| `x/16xw &buf` | buf'tan 16 word |
| `x/i $rip` | sıradaki komut |
| `x/10i $rip` | sıradaki 10 komut (ileriye bak) |
| `x/d $rbp-0x4` | yerel değişkeni decimal oku |

**Adres ifadeleri:** `$rsp`, `$rbp-0x4`, `&degisken`, `0x404040`, `$rax+8` — hepsi `x`'e verilebilir.
pwndbg ayrıca `telescope $rsp` ile stack'i pointer-takipli (nereyi gösterdiğini çözerek) döker.

> ⚠️ **Tuzak:** Boyut harfini karıştırmak yanlış okutur: `x/4xb` 4 **byte**, `x/4xg` 4 **qword** (32 byte).
> Endianness'i (1.1) unutma: `x/xg` ile bir qword okursan sayı doğru birleşir; `x/4xb` ile byte'lar ham
> (ters görünen) sırada gelir.

---

## 2. ⭐ Stack'i okumak

Fonksiyon çalışırken stack'i dökmek, yerel değişkenleri ve akışı görmenin yoludur:

```
(gdb) x/16xg $rsp          # tepeden 16 qword
(gdb) telescope $rsp 20    # pwndbg: pointer'ları çözerek
```

Ararken:
- **Dönüş adresi:** `.text` aralığında (kod) bir değer — `ret` buraya dönecek.
- **Kaydedilmiş rbp:** bir stack adresi (başka bir `0x7fff...`).
- **Yerel buffer'lar:** senin girdin (ASCII byte'lar) burada görünür.

pwndbg her durakta stack'i zaten gösterir; ama derine bakmak için `x`/`telescope` kullanırsın.

> ⚠️ **Tuzak:** Overflow analizinde (Bölüm 6) buffer ile dönüş adresi arasındaki **mesafeyi** stack
> dökümünden ölçersin. "Kaç byte sonra dönüş adresi başlıyor" sorusunun cevabı burada, byte sayarak bulunur.

---

## 3. ⭐ Watchpoint — "bu değer değişince dur"

Breakpoint bir **koda**, watchpoint bir **veriye** kurulur. Bir değişken/adres değiştiğinde program
otomatik durur — değişimin *nerede* olduğunu bulmanın en güçlü yolu.

| Komut | Ne yapar |
|---|---|
| `watch degisken` | değer **yazıldığında** dur |
| `watch *0x404040` | o adres değişince dur |
| `rwatch degisken` | değer **okunduğunda** dur |
| `awatch degisken` | okuma **veya** yazmada dur |

**Kullanım:** "Bu toplam değişkeni 1000'e nereden ulaşıyor?" → `watch toplam` koy, `continue` de; her
değişimde durur, `backtrace` ile hangi kod satırının değiştirdiğini görürsün.

> ⚠️ **Tuzak:** Watchpoint'ler yerel değişkenlerde **kapsam** (scope) sınırlıdır — fonksiyon bitince o
> yerel adres başka şeye ait olur. Global/heap adreslerinde daha güvenli. Ayrıca donanım watchpoint sayısı
> sınırlıdır (genelde 4); çok fazla koyarsan yavaş "software watchpoint"e düşer.

---

## 4. Backtrace ve akış takibi

Nerede olduğunu ve nereden geldiğini görmek:

| Komut | Ne gösterir |
|---|---|
| `backtrace` / `bt` | çağrı yığını (kim kimi çağırdı) |
| `frame N` / `f N` | belirli bir frame'e geç |
| `info args` | mevcut fonksiyonun argümanları (debug bilgisi varsa) |
| `info locals` | yerel değişkenler (debug bilgisi varsa) |
| `nearpc` (pwndbg) | rip çevresindeki komutlar |

`bt` özellikle bir crash'te veya watchpoint durağında "buraya hangi çağrı zinciriyle geldim" sorusunu
cevaplar. Stripped binary'de isimler yerine adresler görürsün ama zincir yine okunur.

---

## 5. Canlı veri takibi kalıbı — crackme çözümü

Bir dönüşümlü crackme'yi (girdi işlenip karşılaştırılıyor) dinamik çözme kalıbı:

1. Girdi okuma noktasına breakpoint (`break *okuma_sonrası` veya `break strcmp`).
2. Bilinen bir deneme gir (`AAAA...`).
3. Girdinin yazıldığı buffer'ı bul: `x/s &buf` veya stack'te ASCII ara.
4. Dönüşüm döngüsünü adımla (`ni`), her turda buffer'ı tekrar oku — nasıl değiştiğini gör.
5. Karşılaştırma anında (`cmp`/`strcmp`) her iki tarafı oku → **beklenen değer** ortaya çıkar.
6. Beklenen değeri üreten girdiyi hesapla (keygen mantığı, Bölüm 5) veya doğrudan gir.

Bu kalıp, "statikte anlamadım" dediğin dönüşümleri **gözünle görerek** çözmeni sağlar.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `x/4xb` vs `x/4xg` | 4 byte vs 4 qword (32 byte) | Boyut harfi kritik |
| watchpoint vs breakpoint | watch=veri, break=kod | Farklı tetikleyici |
| `watch` vs `rwatch` | yazma vs okuma | awatch=ikisi |
| `bt` ne gösterir | çağrı zinciri | anlık register değil |
| yerel değişkende watch | kapsam bitince güvensiz | global/heap daha iyi |

---

## 🖥 Pratik — dönüşümü canlı izle ve watchpoint kur

> **Amaç:** Buffer okuma + watchpoint ile bir değerin nasıl/ nerede değiştiğini görmek · **Süre:** 25 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Dönüşümlü crackme derle:
   ```bash
   cat > /tmp/watch.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char b[32]; int i, s=0;
     printf("Key: ");
     if(scanf("%31s",b)!=1) return 1;
     for(i=0;b[i];i++){ b[i] ^= 0x2a; s += b[i]; }   // her byte XOR + toplam
     if(s==0x3e8) puts("Correct!"); else puts("Wrong.");
     return 0;
   }
   EOF
   gcc -g -O0 -o /tmp/watch /tmp/watch.c
   ```
2. GDB'de aç, `main`'e gir, çalıştır, `AAAA` gir:
   ```bash
   gdb -q /tmp/watch
   (gdb) break main
   (gdb) run
   ```
   `Key:` çıkınca `AAAA` yaz.
3. `s` toplamına watchpoint koy ve akışı izle:
   ```
   (gdb) watch s
   (gdb) continue
   ```
   Her `s` değişiminde program durur. `p s` ile değeri, `bt` ile nerede olduğunu gör.
4. Buffer'ın XOR'la nasıl değiştiğini gör: döngü içinde `p b` veya `x/8xb b` ile her turda byte'ları oku.
   `'A'=0x41`, `0x41 ^ 0x2a = 0x6b` olduğunu doğrula.
5. Beklenen toplam `0x3e8 = 1000`. XOR'lu byte'ların toplamı 1000 olmalı — bu, keygen'e köprü (Bölüm 5).

- [ ] **Kontrol:** watchpoint `s` her arttığında durdu mu? `0x41 ^ 0x2a = 0x6b` dönüşümünü gördün mü?
- [ ] **Kaydet:** `watch s`=değişince dur · `bt`=çağrı zinciri · XOR 0x2a dönüşümü · hedef toplam 0x3e8 ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — buffer okuma**
> `x/s &buf` senin girdiğin `AAAA`'yı gösteriyor. Ne öğrendin? → **Girdinin yazıldığı bellek adresini; dönüşümü buradan izlerim.**

**Kalıp 2 — watchpoint**
> Bir bayrak değişkeni bir yerde 1 oluyor ama nerede bilmiyorsun. Ne yaparsın? → **`watch flag` + `continue`; değişince durup `bt` ile yeri bulurum.**

**Kalıp 3 — stack mesafesi**
> Overflow için buffer ile dönüş adresi arası kaç byte? → **Stack'i `x/xg $rsp` ile döker, buffer başından dönüş adresine kadar byte sayarım.**

**Kalıp 4 — format karışıklığı**
> `x/4xg` ile stack'e baktın, beklediğinden 4 kat fazla veri geldi. Neden? → **`g`=8 byte; 4 qword = 32 byte. `b` isteseydim `x/4xb` yazmalıydım.**

---

## 60 saniyelik özet

- `x/[n][format][boyut] adres`: `b/w/g`=1/4/8 byte, `x/d/s/i`=hex/dec/string/instr.
- Stack dökümü: `x/16xg $rsp` veya pwndbg `telescope`; dönüş adresi/rbp/buffer'ı ayırt et.
- Watchpoint veriye kurulur: `watch` (yazma), `rwatch` (okuma), `awatch` (ikisi) — değişimin yerini bulur.
- Yerel değişken watch'u kapsamla sınırlı; global/heap daha güvenli; donanım watch sayısı sınırlı (~4).
- `bt`/`frame`/`info locals` ile çağrı zincirini ve bağlamı gör.
- Crackme kalıbı: girdiyi bul → dönüşümü adımla → karşılaştırmada beklenen değeri oku.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `x/8xg $rsp` ile `x/8xb $rsp` kaç byte okur, aralarındaki fark ne?
2. Watchpoint ile breakpoint arasındaki temel fark nedir?
3. `watch` ile `rwatch` ne zaman farklı davranır?
4. `bt` bir watchpoint durağında ne söyler?
5. Yerel bir değişkene watchpoint koymanın riski nedir?

➡️ **Cevaplar:** [`cevaplar.md#02-bellekstack-inceleme-watch-canlı-veri-takibi`](cevaplar.md#02-bellekstack-inceleme-watch-canlı-veri-takibi) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[02-test.html](02-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`03-birlikte-analiz.md`](03-birlikte-analiz.md)
