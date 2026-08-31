# 02 — Decompile Okuma, Tip/İsim Düzeltme, Veri Akışı

> **Alan:** Statik analiz (%20) — Ghidra çıktısını okunur hale getirmek
> **Süre:** ~30 dakika okuma + 25 dakika pratik
> **Test:** [`02-test.html`](02-test.html) · 15 soru

---

## Neden bu konu

Ghidra sana C benzeri kod verir ama ilk halinde çirkindir: `uVar1`, `param_1`, `(char)(iVar2 + 0x30)`,
`undefined8`. Bu çorbayı okunur bir mantığa çevirmek RE'nin günlük işidir. En sık hata: bu çıktıyı olduğu
gibi okumaya çalışıp boğulmak. Doğrusu: **isimleri değiştirerek** (rename) ve **tipleri düzelterek** kodu
adım adım kendi diline çevirmek — Ghidra bunu senin için hatırlar ve tüm çıktıyı günceller.

**Büyük fikir:** Decompile çıktısı bir **taslaktır**, senin düzenlemenle netleşir. Bir değişkene doğru
isim verdiğinde ("`local_18` → `password_len`") kod birden anlam kazanır. RE, bu isimlendirme sürecidir.

---

## 1. ⭐ Ghidra'nın uydurma isimlerini çözmek

| Ghidra gösterir | Anlamı | Ne yaparsın |
|---|---|---|
| `param_1, param_2` | Fonksiyon argümanları (rdi, rsi...) | Anlayınca yeniden adlandır |
| `local_18, local_2c` | Yerel değişkenler (`rbp-0x18` vb.) | Rolünü bul, adlandır |
| `uVar1, iVar2` | Geçici değişkenler (u=unsigned, i=int) | Takip et, adlandır |
| `FUN_00101169` | İsimsiz fonksiyon (adresten) | Ne yaptığını çöz, adlandır |
| `DAT_00104010` | İsimsiz global veri | Xref'le anla |

**Rename kısayolu:** Bir değişkene/fonksiyona tıkla, **`L`** tuşu (veya sağ tık → Rename). Yeni isim
tüm çıktıda otomatik güncellenir. Bu senin en sık kullanacağın komuttur.

> ⚠️ **Tuzak:** `param_1` isimlendirmesi register sırasına göredir: `param_1`=rdi, `param_2`=rsi… Yani
> Ghidra argüman eşlemesini ABI'den (2.4) yapar. İsim uydurmadır ama sıra gerçektir.

---

## 2. ⭐ Tip düzeltme — okunabilirliğin anahtarı

Ghidra tipleri de tahmin eder ve sık yanılır. `undefined8` (bilinmeyen 8 byte), `undefined4` gibi tipler
görürsün. Doğru tipi vermek kodu netleştirir:

- Bir değişken bir string tutuyorsa → tipini `char *` yap.
- Bir sayaç/uzunluk ise → `int` veya `size_t`.
- Bir struct pointer ise → uygun struct tipini uygula.

**Tip değiştirme:** Değişkene tıkla, sağ tık → **Retype Variable** (kısayol `Ctrl+L`). Örneğin
`undefined8 uVar1` bir string'se `char *`'e çevirince Ghidra `*(char *)(uVar1+1)` gibi ifadeleri
`uVar1[1]` gibi okunur hale getirir.

> ⚠️ **Tuzak:** Yanlış tip mantığı **ters** gösterebilir. `int` sanılan bir değer aslında `unsigned`sa
> bir karşılaştırma decompiler'da `< 0` görünürken gerçekte hiç negatif olmaz. Şüphede Listing'deki
> atlama komutuna bak (`jl` signed mı `jb` unsigned mı — 2.3).

---

## 3. ⭐ Veri akışını takip etmek

RE'nin özü "bu değer nereden geldi, nereye gidiyor" sorusudur. Ghidra bunu kolaylaştırır:

- Bir değişkene tıkla → aynı değişkenin tüm kullanımları vurgulanır.
- **Sağ tık → References** ile bir değişkenin/adresin nereden okunduğunu/yazıldığını görürsün.
- Bir kullanıcı girdisi (`scanf`, `read`, `fgets`) hangi değişkene gidiyor, o değişken hangi
  karşılaştırmaya sokuluyor — bu zinciri izlersin.

**Crackme çözme kalıbı:** Girdi → dönüşüm(ler) → karşılaştırma. Karşılaştırmanın **diğer tarafı**
beklenen değeri verir (ya sabit ya hesaplanmış).

---

## 4. Yaygın decompile kalıplarını tanımak

| Decompile görünümü | Gerçek anlamı |
|---|---|
| `if (iVar1 == 0)` (strcmp sonrası) | İki string eşit (strcmp==0) |
| `*(char *)(buf + i)` | `buf[i]` — dizi elemanı |
| `(int)cVar1 + -0x30` | Karakteri sayıya çevirme (`c - '0'`) |
| `uVar1 = uVar1 * 0x21 + ...` | Bir hash/karma hesabı |
| `for (i=0; i < len; i++)` | String üzerinde döngü |
| `CONCAT44`, `SUB84` | Ghidra'nın byte birleştirme/kesme yardımcıları (tip tahmini artığı) |

`CONCAT`/`SUB` gibi tuhaf fonksiyonlar Ghidra'nın **tip belirsizliğinden** gelir; genelde doğru tip
verince kaybolurlar. Onları "gerçek fonksiyon" sanma.

---

## 5. Yorum ekleme ve iz bırakma

Uzun bir fonksiyonu çözerken bulgularını **yorum olarak** işaretle (Listing'de `;` tuşu, decompiler'da
sağ tık → Set Comment). Böylece 20 dakika sonra geri döndüğünde "burada uzunluk kontrolü var" notun durur.

Ghidra proje halinde her şeyi (isimler, tipler, yorumlar) kaydeder. Aynı binary'ye ertesi gün döndüğünde
tüm ilerlemen orada olur — bu yüzden proje-tabanlıdır.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `param_1` neye karşılık | rdi (1. argüman) | Rastgele değil, ABI sırası |
| `undefined8` | Ghidra'nın bilmediği 8 byte tip | Gerçek bir C tipi değil |
| `CONCAT44` | Tip tahmini artığı yardımcı | Programın gerçek fonksiyonu değil |
| Rename ne yapar | İsmi her yerde günceller, mantığı değiştirmez | Kodu bozmaz |
| Karşılaştırmanın diğer tarafı | Beklenen değer | Girdi tarafı değil |

---

## 🖥 Pratik — çirkin decompile'ı okunur hale getir

> **Amaç:** rename + retype ile bir fonksiyonu netleştirmek, veri akışını izlemek · **Süre:** 25 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Basit bir dönüşümlü crackme derle (stripped ki isimler uydurma gelsin):
   ```bash
   cat > /tmp/tr.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char b[32]; int i,s=0;
     printf("Key: ");
     if(scanf("%31s",b)!=1) return 1;
     for(i=0;b[i];i++) s += b[i];
     if(s==1000) printf("Correct!\n"); else printf("Wrong.\n");
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/tr /tmp/tr.c && strip /tmp/tr
   ```
2. Ghidra'da import et, analyze et, `entry`'den `main`'i bul (3.1 refleksleri). `main`'i Decompiler'da aç.
3. Çıktıda döngü ve toplam değişkenini bul. `local_...` (toplam) değişkenine tıkla, **`L`** ile
   `toplam` adını ver. Sayaç `i`'yi `i` yap. Buffer'ı `giris` yap.
4. `if (local_.. == 1000)` benzeri satırı bul — **beklenen toplam 1000.** Yani karakterlerin ASCII
   toplamı 1000 olan herhangi bir string geçer.
5. Veri akışını izle: `giris` değişkenine tıkla — `scanf`'ten geldiği ve döngüde okunduğu vurgulanır.
6. Bir doğrulama: toplamı 1000 yapan bir girdi bul ve dene. (Örn. çok sayıda yüksek-ASCII karakter;
   ya da Python'la üret: `python3 -c "print('z'*8+chr(1000-8*122))" ` yerine kolayca kendi denemeni yap.)

- [ ] **Kontrol:** Beklenen değeri (`== 1000`) decompiler'da bulup rename ile kodu okunur yaptın mı?
- [ ] **Kaydet:** `L`=rename, `Ctrl+L`=retype · girdi→dönüşüm→karşılaştırma · beklenen değer=diğer taraf ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — isim çözme**
> Decompiler `FUN_00101200(param_1)` gösteriyor. İçini okuyunca string uzunluğu sayıyor. Ne yaparsın? → **Fonksiyonu `strlen_benzeri`, param_1'i `str` olarak yeniden adlandırırım.**

**Kalıp 2 — beklenen değeri bulma**
> `if (iVar1 == 0x1a4)` bir karşılaştırma (iVar1 girdiden hesaplanıyor). Ne öğrenirsin? → **Hedef değer 0x1a4=420; girdi bunu üretmeli.**

**Kalıp 3 — tuhaf fonksiyon**
> Decompile'da `CONCAT44(0, uVar1)` var. Bu nedir? → **Ghidra'nın tip artığı; gerçek bir çağrı değil, doğru tiple kaybolur.**

**Kalıp 4 — char aritmetiği**
> `(int)cVar1 + -0x30`. Ne yapıyor? → **Rakam karakterini sayısal değerine çeviriyor (`c - '0'`).**

---

## 60 saniyelik özet

- Decompile çıktısı taslak; `rename` (`L`) ve `retype` (`Ctrl+L`) ile netleştirilir.
- `param_N` = N. argüman (ABI sırası); `local_N`/`uVar`/`iVar` yereller/geçiciler; `FUN_`/`DAT_` isimsizler.
- Tip düzeltmek okunabilirliğin anahtarı; `undefined8` gerçek tip değil, doğru tiple değiştir.
- Veri akışı: girdi → dönüşüm → karşılaştırma; beklenen değer karşılaştırmanın diğer tarafında.
- `CONCAT`/`SUB` gibi yardımcılar tip artığıdır, gerçek fonksiyon değil.
- Yorum/isim/tip proje halinde kaydedilir; ertesi gün ilerleme durur.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `param_2` hangi register'a ve kaçıncı argümana karşılık gelir?
2. `undefined8` gerçek bir C tipi midir? Ne yaparsın?
3. Bir crackme'de "beklenen değer"i decompile'da nerede ararsın?
4. `CONCAT44` gibi bir ifade gördüğünde ne düşünürsün?
5. Rename işlemi programın çalışmasını değiştirir mi?

➡️ **Cevaplar:** [`cevaplar.md#02-decompile-okuma-tipisim-düzeltme-veri-akışı`](cevaplar.md#02-decompile-okuma-tipisim-düzeltme-veri-akışı) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[02-test.html](02-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`03-string-xref.md`](03-string-xref.md)
