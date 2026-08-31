# 02 — Keygen Yazmak: Algoritmayı Tersine Çevirmek

> **Alan:** Crackme, keygen, patching (%12) — RE'nin en tatmin edici becerisi
> **Süre:** ~30 dakika okuma + 30 dakika pratik
> **Test:** [`02-test.html`](02-test.html) · 14 soru

---

## Neden bu konu

Keygen, doğrulama algoritmasını okuyup **geçerli anahtarları üreten** bir program yazmaktır. Bir parolayı
bulmak tek seferliktir; keygen ise "istediğin kadar geçerli anahtar" demektir — algoritmayı gerçekten
anladığının kanıtı. En sık hata: dönüşümü yaklaşık anlayıp keygen'i deneme-yanılmayla yazmak. Doğrusu:
her adımı **kesin** çıkarıp **tersini** kodlamak.

**Büyük fikir:** Doğrulama bir fonksiyondur: `dogru_mu(girdi) → evet/hayır`. Keygen bunun **tersidir**:
`uret() → dogru(girdi)`. Doğrulamayı adım adım okur, her adımı tersine çevirir ve birleştirirsin.

---

## 1. ⭐ Keygen'in üç adımı

1. **Doğrulama algoritmasını tam çıkar.** Girdi hangi dönüşümlerden geçiyor, sonuç neyle karşılaştırılıyor.
   Statik (Ghidra) + dinamik (GDB) ile kesinleştir (4.3).
2. **Her dönüşümü tersine çevir.** Karşılaştırma hedefinden başlayıp geriye doğru: "sonuç X olmalıysa,
   ondan önceki değer neydi?"
3. **Üreteci kodla.** Genelde Python: hedeften geçerli bir girdi üret, programa vererek doğrula.

**Kural:** Keygen'i "doğrulama ne yapıyorsa onun tam tersi" olarak düşün. İleri: girdi→sonuç. Geri: sonuç→girdi.

---

## 2. ⭐ Tersine çevrilebilir işlemler ve tersleri

| Doğrulama işlemi | Tersi (keygen'de) |
|---|---|
| `x ^ k` (XOR) | `x ^ k` (aynı) |
| `x + k` | `x - k` |
| `x - k` | `x + k` |
| `x << n` (kaydırma) | `x >> n` (dikkat: bit kaybı olabilir) |
| `x * k` (tek sayı k) | `x * k⁻¹ mod 2ⁿ` (modüler ters) |
| byte permütasyonu | ters permütasyon |
| tablo araması (S-box) | ters tablo |

> ⚠️ **Tuzak:** Her işlem tersine çevrilemez. `x & 0xF0` (bit maskeleme) alt bitleri **yok eder** — geri
> getirilemez. Bölme, mod, tek yönlü hash de kayıplıdır. Kayıplı bir adım varsa ya o adım belirleyici
> değildir (atlanabilir) ya da o değeri brute-force etmen gerekir.

---

## 3. ⭐ Toplama tabanlı doğrulamayı çözmek (klasik)

En yaygın crackme kalıbı: "karakterlerin (belki dönüştürülmüş) toplamı = hedef". Örnek:

```c
for(i=0;b[i];i++) s += (b[i] ^ 0x2a);
if(s == 0x3e8) // 1000
```

Bu **tek denklemli** bir sistemdir: `Σ(b[i]^0x2a) = 1000`. Sonsuz çözümü var; sen **bir** tanesini üret:

- n karakter seç (örn. uzunluk şartı varsa ona uy).
- İlk n−1 karakteri sabitle (örn. hepsi `'A'`).
- Son karakteri denklemi tamamlayacak şekilde hesapla: `son = (1000 − Σöncekiler) ^ 0x2a`.
- `son`'un geçerli aralıkta (yazdırılabilir ASCII, `0x20`–`0x7e`) olduğunu kontrol et; değilse başka
  karakterleri ayarla.

**Refleks:** Toplam/XOR gibi **doğrusal** kısıtlarda "hepsini sabitle, birini serbest bırak" tekniği işe yarar.

---

## 4. Kısıtları birleştirmek (çoklu koşul)

Gerçek crackme'ler birden çok kısıt koyar:
- **Uzunluk:** `strlen(b) == 8`
- **Karakter sınıfı:** hepsi rakam / harf
- **Toplam/checksum:** `s == hedef`
- **Konum bazlı:** `b[0] == 'K'`, `b[3] ^ b[4] == 0x10`

Keygen tüm kısıtları **aynı anda** sağlamalı. Strateji:
1. En katı kısıtları önce sabitle (sabit konumlar, uzunluk).
2. Serbest kalan karakterleri toplam/checksum'ı tutturacak şekilde ayarla.
3. Üret, programa ver, "Correct!" görene kadar ayarla.

> ⚠️ **Tuzak:** Bir kısıtı unutmak keygen'i sessizce bozar. Ghidra'da **tüm** karar zincirini (her `&&`,
> her `cmp`) listele; keygen'de her birine bir satır karşılık gelsin.

---

## 5. Keygen'i doğrulamak ve otomatikleştirmek

Keygen yazınca **kanıtla:**
```bash
python3 keygen.py | ./crackme     # ürettiğini doğrudan besle
```
"Correct!" görmüyorsan bir kısıtı kaçırmışsındır — geri dön, algoritmayı yeniden oku.

**İleri seviye:** Doğrusal çözüm yoksa (karmaşık kısıt), küçük arama uzayını **brute-force** et:
```python
import itertools, string
for combo in itertools.product(string.ascii_letters, repeat=4):
    k = ''.join(combo)
    if dogrula(k):   # doğrulama mantığını Python'da yeniden yaz
        print(k); break
```
Brute-force sadece arama uzayı küçükse (birkaç karakter) pratiktir; büyük uzayda keygen matematiği şart.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| Keygen nedir | Geçerli anahtar *üreten* program | Tek parola bulmak değil |
| XOR'un tersi | Aynı XOR | Çıkarma değil |
| `& 0xF0` tersine çevrilir mi | Hayır, bit kaybı | Kayıplı işlem |
| Toplam kısıtı çözümü | Birini serbest bırak, hesapla | Hepsini brute-force gereksiz |
| Çoklu kısıt | Hepsini aynı anda sağla | Birini atlamak keygen'i bozar |

---

## 🖥 Pratik — gerçek bir keygen yaz

> **Amaç:** Çok kısıtlı bir doğrulamayı tersine çevirip çalışan keygen üretmek · **Süre:** 30 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Çok kısıtlı bir crackme derle:
   ```bash
   cat > /tmp/kg.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char b[32]; int i,s=0;
     printf("Serial: ");
     if(scanf("%31s",b)!=1) return 1;
     if(strlen(b)!=8){ puts("Wrong."); return 0; }     // kısıt 1: uzunluk 8
     if(b[0]!='K'){ puts("Wrong."); return 0; }         // kısıt 2: ilk karakter 'K'
     for(i=0;i<8;i++) s += (b[i] ^ 0x2a);               // dönüşüm: XOR + toplam
     if(s==0x2bc) puts("Correct!"); else puts("Wrong."); // kısıt 3: toplam 0x2bc=700
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/kg /tmp/kg.c
   ```
2. Ghidra/objdump ile **üç kısıtı** çıkar (uzunluk 8, `b[0]=='K'`, XOR'lu toplam `0x2bc=700`):
   ```bash
   objdump -d -M intel /tmp/kg | sed -n '/<main>:/,/ret/p' | grep -E "cmp|add|xor" | head
   ```
3. Keygen'i yaz — kısıtları sırayla uygula:
   ```bash
   cat > /tmp/keygen.py <<'EOF'
   K = 0x2a
   target = 0x2bc              # 700
   n = 8
   s = ['K'] + ['A']*(n-2) + ['?']   # b[0]='K', ilk 7 sabit, son serbest
   # ilk 7 karakterin XOR'lu toplamı
   partial = sum(ord(c) ^ K for c in s[:7])
   last_x = target - partial   # son karakterin XOR'lu değeri
   last = last_x ^ K           # gerçek karakter
   if 0x20 <= last <= 0x7e:
       s[7] = chr(last)
       serial = ''.join(s)
       assert len(serial)==8 and serial[0]=='K'
       print(serial)
   else:
       # son karakter aralık dışı: bir 'A'yı değiştirip tekrar dene
       print("AYAR GEREK: ara karakterleri değiştir")
   EOF
   python3 /tmp/keygen.py
   ```
4. **Kanıtla:**
   ```bash
   python3 /tmp/keygen.py | /tmp/kg
   ```
   `Correct!` görmelisin. (Görmüyorsan `last` aralık dışına düşmüştür; keygen'de bir `'A'`'yı `'a'` yapıp
   toplamı yeniden dengele — bu, "kısıtı birleştirme" pratiğidir.)

- [ ] **Kontrol:** Keygen'in ürettiği seri programda "Correct!" verdi mi? Üç kısıdın üçünü de sağladın mı?
- [ ] **Kaydet:** keygen = doğrulamanın tersi · birini serbest bırak-hesapla · tüm kısıtları aynı anda sağla ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — ters işlem**
> Doğrulama `key[i] = input[i] + i` yapıyor, sonucu sabitle karşılaştırıyor. Keygen ne yapar? → **`input[i] = sabit[i] - i` ile geri hesaplar.**

**Kalıp 2 — serbest bırakma**
> Toplam 700 olmalı, 8 karakter. Nasıl üretirsin? → **7'sini sabitle, 8.'yi 700−toplam olacak şekilde hesapla.**

**Kalıp 3 — kayıplı işlem**
> Doğrulamada `x & 0xF0` var. Keygen için sorun? → **Alt 4 bit yok olur; tersine çevrilemez, o biti brute-force veya kısıtsız bırak.**

**Kalıp 4 — çoklu kısıt kaçırma**
> Keygen "Correct" vermiyor ama toplam doğru. Neden? → **Başka bir kısıt (uzunluk/konum) sağlanmıyor; algoritmayı yeniden oku.**

---

## 60 saniyelik özet

- Keygen = doğrulamanın tersi: `uret() → dogru(girdi)`. Geçerli anahtar *üretir*.
- Üç adım: algoritmayı tam çıkar → her dönüşümü tersine çevir → üreteci kodla (Python).
- Tersine çevrilebilir: XOR (aynı), + ↔ −, permütasyon, modüler çarpım. Kayıplı: `& mask`, bölme, hash.
- Doğrusal kısıt (toplam/XOR): n−1 karakteri sabitle, birini denklemi tutturacak şekilde hesapla.
- Çoklu kısıtı **aynı anda** sağla; Ghidra'da tüm karar zincirini listele, her birine keygen satırı.
- Kanıtla: `python3 keygen.py | ./crackme` → "Correct!". Küçük uzayda brute-force alternatif.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. Keygen ile "parola bulma" arasındaki fark nedir?
2. `x + 7` dönüşümünün keygen'deki tersi nedir? `x ^ 0x2a`'nınki?
3. Toplam kısıtını tek serbest karakterle nasıl çözersin?
4. Hangi işlemler tersine çevrilemez ve o zaman ne yaparsın?
5. Keygen "Correct" vermiyorsa ilk kontrol edeceğin şey nedir?

➡️ **Cevaplar:** [`cevaplar.md#02-keygen-yazmak-algoritmayı-tersine-çevirmek`](cevaplar.md#02-keygen-yazmak-algoritmayı-tersine-çevirmek) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[02-test.html](02-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`03-patching-antidebug.md`](03-patching-antidebug.md)
