# 01 — Disassembler vs Decompiler; Ghidra'ya Giriş

> **Alan:** Statik analiz (%20) — kodu çalıştırmadan okuma
> **Süre:** ~25 dakika okuma + 25 dakika pratik
> **Test:** [`01-test.html`](01-test.html) · 14 soru
> ⚠️ **Doğrulanmalı:** Ghidra sürümü (12.x, Ağustos 2026) ve menü adları zamanla değişebilir — resmî sürümden teyit et.

---

## Neden bu konu

Şimdiye kadar assembly'i elle okudun. Gerçek işlerde bunu bir **araç** yapar: disassembler ham komutları,
**decompiler** ise onları C benzeri okunur koda çevirir. Ghidra bu ikisini birden verir ve ücretsizdir.
Bu konuda aracın nasıl kurulduğunu, bir binary'nin nasıl açıldığını ve iki görünümün (assembly ↔ C)
nasıl birlikte okunacağını öğrenirsin. En sık hata: decompiler çıktısına **kaynak kodu** gibi güvenmek —
o bir *tahmindir*, hata yapabilir.

**Büyük fikir:** Statik analiz = programı **çalıştırmadan**, sadece byte'larına bakarak anlamak. Ghidra
sana iki eşzamanlı pencere verir: solda ham gerçek (assembly), sağda okunur tahmin (decompile). İkisini
birlikte okursun.

---

## 1. ⭐ Disassembler vs decompiler

| | **Disassembler** | **Decompiler** |
|---|---|---|
| Girdi → Çıktı | byte → assembly komutları | assembly → C benzeri kod |
| Doğruluk | **Kesin** (byte'ların birebir karşılığı) | **Tahmini** (yapı/tip yeniden kurulur) |
| Okunabilirlik | Düşük (satır satır makine) | Yüksek (`if`, `while`, değişken) |
| Ne zaman güven | Her zaman | Şüpheyle; assembly ile doğrula |
| Örnek araç | objdump, Ghidra listing | Ghidra decompiler, IDA Hex-Rays |

> ⚠️ **Tuzak:** Decompiler `uVar1`, `local_18`, `(int)` gibi **uydurma isimler ve tip cast'ları** üretir.
> Bunlar kaynaktaki gerçek isimler değil, aracın tahminidir. Yanlış tip tahmini mantığı ters gösterebilir;
> şüphelenince listing (assembly) penceresine bak.

---

## 2. ⭐ Ghidra nedir, neden bu

Ghidra, NSA'in açık kaynak yaptığı bir RE platformudur. Ücretsiz, çok mimarili, güçlü bir decompiler'ı var.
IDA Free'ye kıyasla decompiler'ı ücretsiz sürümde de gelir — başlangıç için ideal.

**Kurulum özeti** (ayrıntı: `00-baslangic/02-ortam-kurulumu.md`):
- JDK gerektirir (Ghidra 12.x için uygun bir JDK sürümü — ⚠️ resmî README'den teyit et).
- `github.com/NationalSecurityAgency/ghidra` sürüm sayfasından indir, aç, `ghidraRun` ile başlat.

**Temel iş akışı:**
1. **Yeni proje** oluştur (File → New Project).
2. Binary'yi **import et** (File → Import File).
3. Çift tıkla → CodeBrowser açılır.
4. İlk açılışta **auto-analyze** öner: "Yes" de (fonksiyonları, string'leri, xref'leri bulur).

---

## 3. ⭐ CodeBrowser penceresinin haritası

Ghidra CodeBrowser'da işine yarayacak paneller:

| Panel | Ne gösterir | Ne için |
|---|---|---|
| **Listing** | Adres + assembly (disassembly) | Kesin gerçek; adres/xref |
| **Decompiler** | C benzeri kod | Hızlı okuma |
| **Symbol Tree** | Fonksiyonlar, imports, exports | `main`/fonksiyona atlama |
| **Defined Strings** | Bulunan string'ler | İpucu/flag avı (3.3) |
| **Function Graph** | Kontrol akış grafiği | Dallanmayı görselleştirme |

Listing ve Decompiler **senkronizedir:** Listing'de bir satıra tıklarsan Decompiler o C satırını
vurgular ve tersi. İkisi arasında gidip gelmek en temel refleksindir.

> ⚠️ **Tuzak:** Stripped binary'de Symbol Tree'de `main` yazmaz; `entry` veya `FUN_...` görürsün. `main`'i
> bulmak için `entry`'den `__libc_start_main`'e verilen ilk argümanı takip edersin (3.2'de).

---

## 4. Statik analizin gücü ve sınırı

**Güçlü yanı:** Programı çalıştırmadan tüm kod yollarını görebilirsin — çalışırken tetiklenmeyen dallar
dahil. Zararlı bir binary'yi bile güvenle (çalıştırmadan) inceleyebilirsin.

**Sınırı:** Bazı şeyler ancak **çalışınca** belli olur:
- Şifre çözme/paketleme (packer) ile gizlenmiş kod (runtime'da açılır).
- Runtime'da hesaplanan değerler, kullanıcı girdisine bağlı yollar.
- Anti-analiz numaraları.

Bu yüzden statik + dinamik **birlikte** kullanılır (Bölüm 4). Statikle haritayı çıkarır, dinamikle
şüpheli noktaları doğrularsın.

> ⚠️ **Tuzak:** "Ghidra'da kodu gördüm, çözdüm" demek her zaman yetmez. Paketlenmiş/şifreli bir binary'de
> statik görünüm gerçek mantığı göstermez; orada dinamik şart.

---

## 5. İlk oturum refleksleri

Bir binary'yi Ghidra'da ilk açtığında sıralı reflekslerin:

1. **Auto-analyze** → Yes.
2. **Symbol Tree → Functions** → `main` var mı? Yoksa `entry`'den başla.
3. **Defined Strings**'e göz at → tanıdık/ipucu string var mı ("Correct", "Wrong", "flag{").
4. İlgili string'e **sağ tık → References → Show References to** → onu kullanan fonksiyona atla (xref).
5. O fonksiyonu **Decompiler**'da oku, şüpheli yerde **Listing**'e bak.

Bu beş adım, çoğu basit crackme'yi araç açıldıktan 5 dakika sonra çözülebilir hale getirir.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| Disassembler vs decompiler | Diss=kesin assembly, Decomp=tahmini C | Decompiler'a körü körüne güvenme |
| `uVar1`, `local_18` | Ghidra'nın uydurduğu isimler | Kaynaktaki gerçek isim değil |
| Statik analizin sınırı | Packed/runtime kod görünmez | "Her şeyi görürüm" yanlış |
| Listing vs Decompiler | Senkronize iki görünüm | Ayrı dosyalar değil |
| Stripped'te main | `entry`/`FUN_...` olarak görünür | İsimle aramak işe yaramaz |

---

## 🖥 Pratik — ilk binary'ni Ghidra'da aç ve oku

> **Amaç:** Import → analyze → decompiler ↔ listing akışını yaşamak · **Süre:** 25 dk
> **💸 Maliyet:** Yok (Ghidra ücretsiz)

### Adımlar
1. Basit bir crackme derle:
   ```bash
   cat > /tmp/cm.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){
     char buf[32];
     printf("Parola: ");
     if(scanf("%31s",buf)!=1) return 1;
     if(strcmp(buf,"opensesame")==0) printf("Correct!\n");
     else printf("Wrong.\n");
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/cm /tmp/cm.c
   ```
2. Ghidra'yı başlat (`./ghidraRun`). **File → New Project** → non-shared, bir isim ver.
3. **File → Import File** → `/tmp/cm` seç. Import sonrası çift tıkla, CodeBrowser açılır.
4. "Analyze now?" → **Yes** (varsayılan seçenekler yeterli).
5. **Symbol Tree → Functions → `main`** çift tıkla. Sağda Decompiler'da C benzeri kodu oku.
6. Decompiler'da `strcmp(buf,"opensesame")` benzeri bir satır ara. Parola **doğrudan görünmeli.**
7. `strcmp` satırına tıkla, solda **Listing**'de karşılık gelen assembly'yi (`lea rsi, ...`, `call strcmp`) gör.

- [ ] **Kontrol:** Decompiler'da `"opensesame"` string'ini gördün mü? Terminalde `echo opensesame | /tmp/cm` "Correct!" verdi mi?
- [ ] **Kaydet:** Ghidra akışı: import→analyze→Symbol Tree→Decompiler ↔ Listing · parola Ghidra'da görünür ______

*(Ücret doğuran adım yok.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — araç ayrımı**
> Decompiler `iVar1 = FUN_00101169(param_1);` gösteriyor. `FUN_...` ve `iVar1` nedir? → **Ghidra'nın uydurduğu fonksiyon/değişken isimleri; gerçek isimler stripped'te yok.**

**Kalıp 2 — güven sınırı**
> Bir binary UPX ile paketlenmiş. Ghidra'da anlamlı kod yok. Neden? → **Gerçek kod runtime'da açılıyor; statik görünüm yetersiz, dinamik/unpack gerekir.**

**Kalıp 3 — xref ile yön bulma**
> "Correct!" string'i var. Nereye bakarsın? → **String'e xref (references) alıp onu yazan fonksiyona atlarım.**

**Kalıp 4 — iki görünüm**
> Decompiler'daki bir tip cast şüpheli. Ne yaparsın? → **Listing'de gerçek assembly'ye bakıp doğrularım.**

---

## 60 saniyelik özet

- Disassembler = kesin assembly; decompiler = tahmini C. Decompiler'a şüpheyle güven.
- Ghidra ücretsiz, decompiler'ı dahil; JDK ister, GitHub'dan indirilir.
- Akış: New Project → Import → Analyze (Yes) → CodeBrowser.
- Paneller: Listing (kesin), Decompiler (okunur), Symbol Tree, Defined Strings — Listing↔Decompiler senkronize.
- Statik: tüm yolları çalıştırmadan görür ama packed/runtime kodu göremez → dinamikle tamamla.
- İlk refleks: analyze → main/entry → strings → xref → decompiler oku.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. Disassembler ile decompiler arasındaki temel güvenilirlik farkı nedir?
2. `uVar1` veya `local_28` gibi isimler nereden gelir?
3. Ghidra'da bir string'i kullanan fonksiyonu nasıl bulursun?
4. Statik analiz hangi durumda gerçek mantığı gösteremez?
5. Listing ve Decompiler panelleri arasındaki ilişki nedir?

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`02-decompile-okuma.md`](02-decompile-okuma.md)
