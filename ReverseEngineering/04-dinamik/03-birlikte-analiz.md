# 03 — Statik + Dinamik Birlikte: Bir Binary'yi Baştan Sona Çözmek

> **Alan:** Dinamik analiz (%18) — iki yöntemi tek akışta birleştirmek
> **Süre:** ~30 dakika okuma + 30 dakika pratik
> **Test:** [`03-test.html`](03-test.html) · 14 soru

---

## Neden bu konu

Şimdiye kadar araçları ayrı öğrendin: Ghidra (statik) ve GDB (dinamik). Gerçek analiz **ikisini
dönüşümlü** kullanır: Ghidra'da haritayı çıkar, GDB'de şüpheli noktayı doğrula, tekrar Ghidra'ya dön.
Bu konu, o dansı tek bir akışta gösterir. En sık hata: tek araçta takılıp kalmak — statikte anlamadığın
dönüşümü saatlerce okumak yerine GDB'de 2 dakikada görebilirsin; ya da GDB'de körlemesine adımlamak
yerine Ghidra'dan hangi adrese breakpoint koyacağını öğrenebilirsin.

**Büyük fikir:** Statik "haritadır", dinamik "keşif gezisidir". Haritayla nereye gideceğini planlarsın,
gezide gerçekte ne olduğunu görürsün. İkisi olmadan biri eksik kalır.

---

## 1. ⭐ İş bölümü: hangi soruyu hangi araç cevaplar

| Soru | En iyi araç |
|---|---|
| Program genel olarak ne yapıyor? | **Statik** (Ghidra decompile) |
| Bu string'i kim kullanıyor? | **Statik** (xref) |
| Bu değişkenin **gerçek** değeri şu an ne? | **Dinamik** (GDB) |
| Bu dönüşüm girdiyi neye çeviriyor? | **Dinamik** (adımla, belleği oku) |
| Bu dal ne zaman/hangi koşulda alınıyor? | **Dinamik** (breakpoint + register) |
| Şifreli/packed kod açıldıktan sonra ne? | **Dinamik** (açılınca dump) |
| Tüm kod yolları (çalışmayanlar dahil) | **Statik** (hepsini görür) |

**Refleks:** "Ne yapıyor?" → statik. "Şu an değeri ne?" → dinamik. Takılınca araç değiştir.

---

## 2. ⭐ Standart çözüm akışı (recipe)

Yeni bir crackme'yi baştan sona çözme adımları:

1. **Kimlik (statik/terminal):** `file`, `checksec`, `strings`. 32/64-bit, koruma, ipucu string'ler.
2. **Harita (Ghidra):** import + analyze. `main`/`entry` bul. İlginç string'e xref al → doğrulama
   fonksiyonuna atla. Decompile'ı rename/retype ile okunur yap.
3. **Hipotez:** "Girdi şu döngüde işleniyor, sonra `0x1a4` ile karşılaştırılıyor gibi." Statikten bir
   **tahmin** kur.
4. **Doğrula (GDB):** Ghidra'nın gösterdiği adrese/fonksiyona breakpoint koy. Çalıştır, gerçek değerleri
   oku. Hipotezini test et.
5. **Çöz:** Beklenen değeri buldun mu doğrudan gir; hesaplanıyorsa ters çevir (keygen) veya patch'le (Bölüm 5).
6. **Kanıtla:** Bulduğun girdiyle programı çalıştır, "Correct!" gör.

Bu döngü (harita → hipotez → doğrula) bir daire değil, **spiraldir**: her turda hedefe yaklaşırsın.

---

## 3. ⭐ Adres eşleme: Ghidra ↔ GDB (PIE tuzağı)

Ghidra'da bir adres görürsün (örn. `0x00101169`), GDB'de breakpoint koymak istersin. İki durum:

- **PIE değil (sabit yükleme):** Ghidra adresi = GDB adresi. `break *0x401169` çalışır.
- **PIE (ASLR):** Ghidra genelde `0x100000` tabanlı gösterir; GDB'de gerçek base farklıdır. Çözüm:
  - Sembol kullan: `break main` (base'den bağımsız).
  - Ya da fonksiyon offset'i: Ghidra'da `main` `0x1169`'da, base'i öğren (`pwndbg` gösterir veya
    `info proc mappings`), gerçek adres = base + offset.
  - pwndbg'de `break $rebase(0x1169)` bazı sürümlerde offset'i base'e ekler. ⚠️ Doğrulanmalı: komut
    sürüme göre değişebilir.

> ⚠️ **Tuzak:** "Ghidra'daki adrese breakpoint koydum ama tutmadı" şikayeti neredeyse her zaman PIE/ASLR
> kaynaklıdır. Sabit adres yerine sembol veya rebase kullan.

---

## 4. Ne zaman araç değiştirmeli (20 dakika kuralının uygulaması)

| Durum | Ne yap |
|---|---|
| Statikte dönüşüm çok karmaşık, kafan karıştı | GDB'ye geç, girdiyi ver, adımla, gözünle gör |
| GDB'de nereye breakpoint koyacağını bilmiyorsun | Ghidra'ya dön, xref'le hedefi bul |
| Kod hiç anlamlı görünmüyor (statik) | Packed olabilir; çalıştırıp bellekten dump al |
| Aynı yerde 20 dk takıldın | Not al, yaklaşımı değiştir, gerekirse writeup'a bak |

Amaç tek araçta inatlaşmak değil, **hangi sorunun hangi araçla hızlı çözüldüğünü** öğrenmektir.

---

## 5. Küçük bir tam örnek (zihinsel prova)

Bir crackme: `strings`'te parola yok. Ghidra'da `main`:
- Girdi `scanf` ile `local_38`'e alınıyor.
- Bir döngü her karakteri `^ 0x2a` yapıyor, bir `local_c` toplamına ekliyor.
- `if (local_c == 0x3e8)` → "Correct!".

Statik hipotez: "XOR'lu byte'ların toplamı 1000 olmalı." GDB'de doğrula:
- `break` XOR döngüsünün sonrasına, `AAAA` gir, `p local_c` oku → gerçekten toplanıyor mu?
- Beklenen `0x3e8 = 1000`. Şimdi bu bir **keygen** problemi: toplamı 1000 yapan bir string üret (Bölüm 5).

Böylece statik "ne" sorusunu, dinamik "gerçekten öyle mi" sorusunu, keygen "nasıl üretirim" sorusunu
cevaplar. Üçü birlikte tam çözümdür.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| "Ne yapıyor?" sorusu | Statik (Ghidra) | Dinamik anlık değer içindir |
| "Şu anki değer ne?" | Dinamik (GDB) | Statik tahmin eder |
| Ghidra adresi GDB'de tutmadı | PIE/ASLR; sembol/rebase kullan | Adres yanlış değil |
| Packed kod | Dinamikte açılınca görülür | Statik yetersiz |
| Çözüm akışı | Harita→hipotez→doğrula (spiral) | Tek araç yetmez |

---

## 🖥 Pratik — bir crackme'yi iki araçla baştan sona çöz

> **Amaç:** Ghidra ile haritala, GDB ile doğrula, çözümü kanıtla · **Süre:** 30 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Bir crackme derle (PIE dahil, adres eşlemeyi de yaşa):
   ```bash
   cat > /tmp/full.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char b[32]; int i, s=0;
     printf("License: ");
     if(scanf("%31s",b)!=1) return 1;
     for(i=0;b[i];i++) s += (b[i] ^ 0x2a);
     if(strlen(b)==8 && s==0x3e8) puts("Correct!"); else puts("Wrong.");
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/full /tmp/full.c   # PIE varsayılan açık
   ```
2. **Statik (terminal + Ghidra):**
   ```bash
   file /tmp/full          # PIE mi?
   strings /tmp/full | grep -Ei "correct|license"
   ```
   Ghidra'da import/analyze, `main`'i oku. İki koşulu bul: **uzunluk 8** ve **XOR toplamı 0x3e8**.
3. **Dinamik (GDB) — hipotezi doğrula:**
   ```bash
   gdb -q /tmp/full
   (gdb) break strlen        # veya XOR döngüsü sonrası bir noktaya
   (gdb) run
   ```
   `AAAAAAAA` (8 karakter) gir. Toplam değişkenini izle: `AAAAAAAA` → her `'A'^0x2a = 0x6b = 107`,
   8×107 = 856 = `0x358`. Hedef `0x3e8 = 1000`. Fark 144; demek bazı karakterleri değiştirmen gerek.
4. **Çöz (keygen mantığı — Bölüm 5'e köprü):** 8 karakter, XOR'lu toplam 1000. Örneğin çoğu `'A'`,
   birkaçını yükselt. Python ile bir çözüm ara:
   ```bash
   python3 - <<'EOF'
   # 8 karakter, sum((c ^ 0x2a)) == 1000 olan bir string bul
   target=1000; n=8
   base=[ord('A')]*n
   # ilk 7'yi 'A', son karakteri ayarla
   s=sum(c ^ 0x2a for c in base[:7])
   last = (target - s) ^ 0x2a
   if 33 <= last < 127:
       print(''.join(chr(x) for x in base[:7]) + chr(last))
   else:
       print("ilk denemeyle olmadı, birkaç karakteri daha değiştir")
   EOF
   ```
5. **Kanıtla:** Çıkan string'i programa ver:
   ```bash
   echo '<ürettiğin_string>' | /tmp/full
   ```
   `Correct!` görmelisin. (Uzunluk 8 ve toplam 1000 sağlanmalı.)

- [ ] **Kontrol:** Statik hipotezini (uzunluk 8 + toplam 1000) GDB'de doğrulayıp bir çözüm ürettin mi?
- [ ] **Kaydet:** akış: file/strings→Ghidra harita→GDB doğrula→keygen→kanıt · PIE'de sembol breakpoint ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — araç seçimi**
> "Bu XOR döngüsü girdiyi neye çeviriyor?" sorusu için hangi araç? → **Dinamik (GDB): girdi ver, adımla, belleği oku.**

**Kalıp 2 — adres eşleme**
> Ghidra `0x1169` diyor, `break *0x1169` tutmadı. Neden ve çözüm? → **PIE/ASLR; `break main` (sembol) veya base+offset kullan.**

**Kalıp 3 — iki koşul**
> "Correct!" için hem uzunluk hem toplam şartı var. Nasıl bulursun? → **Statikte `&&` zincirini görürüm, dinamikte her iki koşulu ayrı ayrı doğrularım.**

**Kalıp 4 — takılma yönetimi**
> Statikte bir dönüşümü 20 dk çözemedin. Ne yaparsın? → **GDB'ye geçip gerçek değerleri gözlemlerim; yaklaşımı değiştiririm.**

---

## 60 saniyelik özet

- Statik = harita ("ne yapıyor"), dinamik = keşif ("şu an değeri ne"). İkisi dönüşümlü kullanılır.
- Akış: kimlik (file/strings) → harita (Ghidra xref/decompile) → hipotez → doğrula (GDB) → çöz → kanıtla.
- "Ne yapıyor?" statik; "gerçek değer?" dinamik. Takılınca araç değiştir (20 dk kuralı).
- Adres eşleme: PIE değilse Ghidra=GDB adresi; PIE'de sembol/`$rebase`/base+offset kullan.
- Packed/şifreli kod ancak dinamikte (açılınca) görülür.
- Çözüm bir spiral: her tur harita→hipotez→doğrula ile hedefe yaklaşır.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. "Bu string'i kim kullanıyor?" sorusunu hangi araçla, nasıl cevaplarsın?
2. Ghidra'daki bir adrese GDB'de breakpoint koyunca tutmadı. İlk şüphen ne?
3. Statik ve dinamik analizi tek cümlede nasıl özetlersin?
4. Bir dönüşümü statikte çözemediğinde ne yaparsın?
5. Standart çözüm akışının altı adımını sırala.

➡️ **Cevaplar:** [`cevaplar.md#03-statik--dinamik-birlikte-bir-binaryyi-baştan-sona-çözmek`](cevaplar.md#03-statik--dinamik-birlikte-bir-binaryyi-baştan-sona-çözmek) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[03-test.html](03-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`../05-crackme/01-crackme-mantik.md`](../05-crackme/01-crackme-mantik.md)
