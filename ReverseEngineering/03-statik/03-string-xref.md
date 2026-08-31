# 03 — String, Xref, Sabit Avı ile Hızlı Yön Bulma

> **Alan:** Statik analiz (%20) — 2000 satırda "işin döndüğü yeri" bulma
> **Süre:** ~25 dakika okuma + 20 dakika pratik
> **Test:** [`03-test.html`](03-test.html) · 14 soru

---

## Neden bu konu

Büyük bir binary'de her fonksiyonu okumak zaman kaybıdır. Uzmanlar **kestirme** kullanır: string'lerden,
çapraz referanslardan (xref) ve tanıdık sabitlerden başlayıp doğrudan ilgili koda atlar. "Correct!"
string'ini kim yazıyor? İşte doğrulama fonksiyonu orada. En sık hata: `main`'den başlayıp satır satır
ilerlemek — oysa string→xref ile 30 saniyede hedefe gidebilirsin.

**Büyük fikir:** Kod büyüktür ama **ilginç kısmı küçüktür.** String'ler, sabitler ve xref'ler seni doğrudan
o küçük kısma götüren işaret levhalarıdır. RE'de hız, nereye *bakmayacağını* bilmektir.

---

## 1. ⭐ String avı — en ucuz ipucu

Bir binary'nin string'leri niyetini ele verir. Aranacaklar:

| String türü | Örnek | Ne anlatır |
|---|---|---|
| Sonuç mesajları | `"Correct!"`, `"Access denied"`, `"Wrong password"` | Doğrulama dalları |
| Format string | `"%s"`, `"Enter key: "` | Girdi/çıktı noktaları |
| Doğrudan flag | `"flag{...}"`, `"CTF{...}"` | Bazen ödül düz metin! |
| Dosya/yol | `/etc/passwd`, `config.dat` | Ne okuyor/yazıyor |
| Hata/debug | `"assert failed"`, fonksiyon adları | İç yapı ipuçları |

**Terminalde:** `strings -n 6 prog` (en az 6 karakterlik dizileri göster). Ghidra'da: **Defined Strings**
penceresi.

> ⚠️ **Tuzak:** String'i düz `strings` bulamıyorsa **şifreli/parçalı** olabilir (runtime'da kurulur).
> `"C","o","r","r"...` gibi tek tek byte'lar veya XOR'lu veri görürsen string çalışma zamanında üretiliyordur —
> statikte görünmez, dinamikte (Bölüm 4) yakalanır.

---

## 2. ⭐ Xref (cross-reference) — "bunu kim kullanıyor"

**Xref**, bir adrese/string'e/fonksiyona **nereden** referans verildiğini gösterir. RE'nin en güçlü
kestirmesidir.

İki yön:
- **References to** (buraya kim atıf yapıyor): "Correct!" string'ini kim yazdırıyor? → doğrulama fonksiyonu.
- **References from** (buradan nereye): bir fonksiyon hangi başka fonksiyonları/verileri kullanıyor?

**Ghidra'da:** İlgili öğeye sağ tık → **References → Show References to**. Veya öğeye tıklayıp **`Ctrl+Shift+F`**.
Xref listesinden çift tıkla, o koda atlarsın.

**Klasik çözüm zinciri:**
```
"Correct!" string  →  xref to  →  onu yazdıran fonksiyon  →  hemen üstündeki karşılaştırma  →  beklenen değer
```

> ⚠️ **Tuzak:** Bir string'e **birden çok** xref olabilir (aynı mesaj birkaç yerde). Hepsine bak; doğru
> dal genelde bir `if`/karşılaştırmanın hemen ardından gelenidir.

---

## 3. ⭐ Sabit (magic number) avı

Programcılar tanınabilir sabitler kullanır; bunlar algoritmayı ele verir:

| Sabit | İma |
|---|---|
| `0x5f3759df` | Ünlü "fast inverse sqrt" — matematiksel numara |
| `0x9e3779b9` | Golden ratio; TEA/XXTEA şifreleme |
| `0x67452301`, `0xefcdab89` | MD5/SHA init sabitleri → hashing |
| `0xdeadbeef`, `0xcafebabe` | Dolgu/sihirli işaret değerleri |
| `0x21` (33), `0x1f`… | Basit hash çarpanları (djb2 = 33) |
| `0x10, 0x20, 0x40` | Boyut/hizalama |

Bir sabiti tanımazsan **ara** (Google'da "0x... constant"). Çoğu bilinen algoritmanın parmak izi vardır.

**Ghidra'da sabit arama:** **Search → For Scalars** ile belirli bir sayının geçtiği yerleri bulursun.

> ⚠️ **Tuzak:** Her sabit anlamlı değildir; `0x0`, `0x1`, küçük offset'ler gürültüdür. "Tuhaf, büyük,
> tekrarlayan" sabitler ilginçtir. `0x539` (=1337) gibi CTF şakaları da sık geçer.

---

## 4. Import/çağrı avı — hangi API'ler kullanılıyor

Bir binary'nin **import ettiği fonksiyonlar** (dinamik semboller) ne yaptığını ele verir:

| Import | İma |
|---|---|
| `strcmp`, `strncmp` | String karşılaştırma → parola kontrolü |
| `memcmp` | Bayt karşılaştırma → key/hash doğrulama |
| `system`, `execve` | Komut çalıştırma → tehlikeli/ilginç |
| `ptrace` | Anti-debug (Bölüm 5) |
| `fopen`, `read` | Dosya işlemleri |
| `rand`, `srand` | Rastgelelik → keygen ilgi alanı |

**Ghidra'da:** Symbol Tree → **Imports**. `strcmp`'e xref alıp tüm parola kontrollerini bir çırpıda bulabilirsin.

---

## 5. Hepsini birleştiren yön bulma stratejisi

Yeni bir crackme açtığında sıralı strateji:

1. `strings` / Defined Strings → ilginç mesaj var mı? ("Correct", "flag{")
2. O mesaja **xref** → doğrulama fonksiyonuna atla.
3. Fonksiyonda **import çağrılarına** bak (`strcmp`, `memcmp`) → karşılaştırılan değeri oku.
4. Tuhaf **sabitler** varsa algoritmayı tanı (hash mı, XOR mu).
5. Hâlâ belirsizse → dinamik analize geç (Bölüm 4).

Bu strateji "her şeyi oku" yerine "doğru yere bak" refleksidir; hız buradan gelir.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| "References to" ne bulur | Bu öğeyi kimin kullandığı | "from" tersidir |
| `strings` string bulamıyor | Şifreli/runtime'da kurulan string | "Yok" demek yanlış olabilir |
| `0x9e3779b9` | Golden ratio (TEA vb.) | Rastgele sayı değil |
| Import listesi | Kullanılan dış API'ler | Kendi fonksiyonları değil |
| `0x539` | 1337 (CTF şakası) | Anlamsız değil |

---

## 🖥 Pratik — string→xref ile crackme'yi 5 dakikada çöz

> **Amaç:** String avı + xref + import ile doğrulama noktasını hızlı bulmak · **Süre:** 20 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Bir crackme derle:
   ```bash
   cat > /tmp/xr.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char b[32];
     printf("Enter flag: ");
     if(scanf("%31s",b)!=1) return 1;
     if(strcmp(b,"RE{x0r_1s_fun}")==0) printf("Correct!\n");
     else printf("Nope.\n");
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/xr /tmp/xr.c
   ```
2. Önce en ucuz yol — terminalde string avı:
   ```bash
   strings -n 5 /tmp/xr | grep -Ei "correct|flag|enter|RE\{"
   ```
   `RE{x0r_1s_fun}` doğrudan görünebilir (bu crackme kolay; amaç yöntemi öğrenmek).
3. Ghidra'da doğrula: Import et, analyze, **Defined Strings** penceresini aç, `"Correct!"` ara.
4. `"Correct!"`'a sağ tık → **References → Show References to** → onu yazdıran koda atla.
5. O kodun hemen üstünde `strcmp(b, "RE{x0r_1s_fun}")` çağrısını gör — **beklenen değer** budur.
6. Doğrula:
   ```bash
   echo 'RE{x0r_1s_fun}' | /tmp/xr
   ```
   `Correct!` görmelisin.

- [ ] **Kontrol:** String→xref ile doğrulama noktasına `main`'i baştan okumadan ulaştın mı?
- [ ] **Kaydet:** strings→xref→import zinciri · "Correct" xref = doğrulama · sabitleri Google'la ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — xref yönü**
> "Access Denied" string'ine "References to" alıyorsun. Ne bulursun? → **Bu mesajı yazdıran (yani reddeden) dalın kodu.**

**Kalıp 2 — gizli string**
> `strings` çıktısında parola yok ama Ghidra'da tek tek `mov byte [rbp-x], 'A'` atamaları var. Neden? → **String runtime'da byte byte kuruluyor; statik metin taraması onu yakalayamaz.**

**Kalıp 3 — sabit tanıma**
> Kodda `0x67452301` var. Ne düşünürsün? → **MD5 init sabiti — muhtemelen hashing yapılıyor.**

**Kalıp 4 — import ipucu**
> Imports'ta `ptrace` var. Ne ima eder? → **Anti-debug; program hata ayıklandığını tespit etmeye çalışıyor olabilir (Bölüm 5).**

---

## 60 saniyelik özet

- İlginç kod küçüktür; string/xref/sabit onu bulmanın kestirmesidir.
- String avı en ucuz ipucu: "Correct/Wrong/flag{" ve format string'leri ara (`strings -n 6`, Defined Strings).
- Xref "References to" = bu öğeyi kim kullanıyor; doğrulama fonksiyonuna atlamanın yolu.
- Tanıdık sabitler algoritmayı ele verir (`0x9e3779b9` TEA, `0x67452301` MD5, `0x539`=1337).
- Import listesi kullanılan API'leri verir (`strcmp`→parola, `ptrace`→anti-debug).
- Strateji: strings → xref → import → sabit → (gerekirse) dinamik.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. "References to" ile "References from" arasındaki fark nedir?
2. `strings` bir paroloyu bulamıyorsa bu ne anlama gelebilir?
3. `0x9e3779b9` sabiti sana ne ima eder?
4. Imports'ta `strcmp` görmek neden faydalı bir ipucudur?
5. Yeni bir crackme'de neden `main`'i baştan okumak yerine string→xref kullanırsın?

➡️ **Cevaplar:** [`cevaplar.md#03-string-xref-sabit-avı-ile-hızlı-yön-bulma`](cevaplar.md#03-string-xref-sabit-avı-ile-hızlı-yön-bulma) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[03-test.html](03-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`../04-dinamik/01-gdb-temel.md`](../04-dinamik/01-gdb-temel.md)
