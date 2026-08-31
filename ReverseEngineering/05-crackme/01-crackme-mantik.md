# 01 — Lisans Kontrolü Mantığını Bulmak ve Kırmak

> **Alan:** Crackme, keygen, patching (%12) — öğrendiğin her şeyin buluştuğu yer
> **Süre:** ~30 dakika okuma + 30 dakika pratik
> **Test:** [`01-test.html`](01-test.html) · 15 soru
> ⚖️ **Yasal not:** Sadece **kendi** crackme'lerinde ve yasal CTF/crackme sitelerinde çalış. Başkasının
> ticari yazılımını kırmak suçtur. Bkz. `00-baslangic/01-yasal-etik.md`.

---

## Neden bu konu

Crackme, seni "doğru parolayı/lisansı bul" diye zorlayan, öğretmek için yapılmış bir bulmaca binary'sidir.
Bu konuda 1–4. bölümlerdeki her şeyi (assembly, Ghidra, GDB) tek bir amaçta birleştirirsin: **doğrulama
mantığını bul ve geç.** En sık hata: parolayı "tahmin" etmeye çalışmak — oysa doğrulama kodunu okuyup
beklenen değeri *hesaplarsın* veya kontrolü *devre dışı bırakırsın*.

**Büyük fikir:** Her lisans kontrolü bir **karar noktasıdır**: "girdi doğru mu?" → evet/hayır dalı. Senin
işin o karar noktasını bulmak ve ya doğru cevabı üretmek (keygen, 5.2) ya da kararı zorla "evet" yapmak
(patching, 5.3).

---

## 1. ⭐ Crackme türleri

| Tür | Nasıl çalışır | Saldırı yolu |
|---|---|---|
| **Sabit parola** | Girdiyi gömülü bir string'le karşılaştırır | String/xref ile parolayı oku |
| **Dönüşümlü** | Girdiyi işler (XOR, toplam, hash), sonucu karşılaştırır | Dönüşümü ters çevir (keygen) |
| **Seri/anahtar** | Kullanıcı adı + seriyi bir formülle doğrular | Formülü çıkar, keygen yaz |
| **Bayrak/flag kontrol** | Bir değişken doğruysa açılır | Kontrolü patch'le veya dinamik set et |
| **Zaman/deneme kısıtı** | Süre/deneme sayısı sınırı | İlgili kontrolü patch'le |

Çoğu gerçek crackme bunların birkaçının karışımıdır. İlk iş: **hangi türle** karşı karşıya olduğunu anlamak.

---

## 2. ⭐ Doğrulama noktasını bulma (üç yol)

Her crackme'de aradığın şey aynı: **"Correct/Wrong" kararının verildiği karşılaştırma.**

1. **String→xref (statik, en hızlı):** "Correct!"/"Wrong" string'ine xref al → karar fonksiyonu (3.3).
2. **Import→xref:** `strcmp`/`memcmp`/`strncmp` çağrılarına bak → karşılaştırılan değerler (2.4, 3.3).
3. **Dinamik yakalama:** `break strcmp` / karşılaştırmaya breakpoint → çalışırken beklenen değeri oku (4.1).

Karar noktasını bulunca üç seçeneğin olur: **oku** (sabit parola), **hesapla** (keygen), **zorla** (patch).

---

## 3. ⭐ "Correct" dalını tanımak

Doğrulama kodunda aradığın kalıp (2.3, 2.4'ten):

```asm
    call strcmp            ; veya bir hesaplama
    test eax, eax          ; sonuç sıfır mı (eşit mi)
    jne  .wrong            ; eşit değilse -> Wrong
    ; buradan sonrası Correct dalı
    lea  rdi, [rip+msg_correct]
    call puts
```

**İki kritik gözlem:**
- Karşılaştırmanın **sonucu** (`test`/`cmp` sonrası flag) hangi dala gideceğini belirler.
- O dalı **tersine çevirmek** (jne→je) veya **atlamayı iptal etmek** (nop) programı "her zaman Correct"
  yapar — bu patching'in özü (5.3).

> ⚠️ **Tuzak:** Bazı crackme'ler kararı **ters** verir: yanlışta devam, doğruda `exit`. Ya da birden çok
> karar noktası (uzunluk + içerik + checksum) vardır. Tek bir `jne`'yi nop'lamak yetmeyebilir; tüm karar
> zincirini haritala.

---

## 4. Girdi akışını haritalamak

Beklenen değeri hesaplaman gerekiyorsa (keygen), girdinin **tam yolculuğunu** çıkarmalısın:

```
girdi (scanf/read)  →  dönüşüm 1 (örn. her byte ^0x2a)  →  dönüşüm 2 (örn. toplam)  →  karşılaştırma (==0x3e8)
```

Bu zinciri Ghidra'da (statik) çıkar, GDB'de (dinamik) doğrula (4.3'teki birlikte-analiz). Her dönüşümü
tersine çevirebilir formda yaz:
- `^0x2a` → tersi yine `^0x2a` (XOR kendi tersidir).
- `+k` → tersi `-k`.
- `hash` → tersi zor olabilir; o zaman brute-force veya patch'e yönel.

**Refleks:** Dönüşüm **tersine çevrilebilir** mi? Evetse keygen. Değilse (tek yönlü hash) ya girdiyi
brute-force et ya kontrolü patch'le.

---

## 5. Üç saldırı stratejisinin seçimi

| Durum | En iyi strateji | Bölüm |
|---|---|---|
| Parola düz gömülü | Oku (strings/xref) | 3.3 |
| Dönüşüm tersine çevrilebilir | Keygen yaz | 5.2 |
| Karar tek bir dala bağlı | Binary patch (jne→je / nop) | 5.3 |
| Çalışırken tek seferlik geçmek yeter | GDB'de `set` ile register/flag değiştir | 4.1 |
| Anti-debug var | Önce onu atlat, sonra saldır | 5.3 |

**Karar ağacı:** Önce **oku** (en ucuz). Olmuyorsa **hesapla** (keygen, en zarif). O da zorsa **zorla**
(patch, en kaba ama etkili). Sadece bir kez geçmen yetiyorsa dinamik `set` en hızlısı.

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| Doğrulama noktası nasıl bulunur | String/import xref veya break strcmp | Parolayı tahmin etmek değil |
| "Correct" dalı | Karşılaştırma başarılı olan kol | Her zaman `je` değil, ters olabilir |
| Keygen ne zaman | Dönüşüm tersine çevrilebilirse | Hash'te zor |
| Patch ne zaman | Kararı kalıcı zorlamak için | Tek seferlikse GDB set yeter |
| XOR'un tersi | Yine aynı XOR | Ayrı bir işlem değil |

---

## 🖥 Pratik — bir crackme'yi analiz edip stratejini seç

> **Amaç:** Doğrulama noktasını bulup türünü tanımak ve doğru saldırıyı seçmek · **Süre:** 30 dk
> **💸 Maliyet:** Yok

### Adımlar
1. İki farklı türde crackme derle:
   ```bash
   # A: sabit parola
   cat > /tmp/cA.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){char b[32];printf("Pw: ");if(scanf("%31s",b)!=1)return 1;
   puts(strcmp(b,"sunshine42")==0?"Correct!":"Wrong.");return 0;}
   EOF
   gcc -O0 -o /tmp/cA /tmp/cA.c
   # B: dönüşümlü
   cat > /tmp/cB.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   int main(){char b[32];int i,s=0;printf("Key: ");if(scanf("%31s",b)!=1)return 1;
   for(i=0;b[i];i++)s+=b[i];puts(s==0x290?"Correct!":"Wrong.");return 0;}
   EOF
   gcc -O0 -o /tmp/cB /tmp/cB.c
   ```
2. **A'yı çöz (oku):**
   ```bash
   strings /tmp/cA | grep -iv correct | head
   ```
   `sunshine42`'yi doğrudan gör. Doğrula: `echo sunshine42 | /tmp/cA`.
3. **B'yi analiz et (hesapla gerekebilir):**
   ```bash
   strings /tmp/cB       # parola yok — dönüşümlü demek
   objdump -d -M intel /tmp/cB | sed -n '/<main>:/,/ret/p' | grep -E "add|cmp"
   ```
   Karşılaştırma `0x290 = 656`. Yani karakterlerin ASCII toplamı 656 olmalı.
4. **B için strateji seç:** Toplam tersine çevrilebilir (basit) → keygen yaklaşımı. Hızlı bir çözüm:
   ```bash
   python3 -c "print('A'*8 + chr(656-8*65))"   # 8 'A' (65) + toplamı tamamlayan karakter
   ```
   Çıkan string'i dene: `echo '<string>' | /tmp/cB`. (656−520=136, 136>127 olduğundan bu ilk deneme
   taşabilir; birkaç karakteri ayarla — bu, 5.2 keygen konusuna köprü.)
5. **Karar noktasını Ghidra'da gör (bonus):** B'yi Ghidra'da aç, `if (s == 0x290)` karşılaştırmasını ve
   iki dalı (Correct/Wrong) bul. Bunu 5.3'te patch'leyeceğiz.

- [ ] **Kontrol:** A'yı okuyarak, B'nin hedef toplamını (0x290) bularak iki farklı türü ayırt ettin mi?
- [ ] **Kaydet:** türler: sabit/dönüşümlü/seri/flag · strateji: oku→hesapla→zorla · XOR/+ tersine çevrilebilir ______

*(Ücret doğuran adım yok. Sadece kendi crackme'lerinde çalış.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — tür tanıma**
> `strings`'te parola yok, kodda bir toplama döngüsü ve `cmp ..., 0x290` var. Tür? → **Dönüşümlü; keygen adayı.**

**Kalıp 2 — karar noktası**
> `call strcmp` + `test eax,eax` + `jne wrong`. "Correct" dalı hangisi? → **jne alınmadığında (eşitken) devam eden kol.**

**Kalıp 3 — strateji seçimi**
> Dönüşüm tek yönlü bir hash (SHA). Ne yaparsın? → **Keygen zor; kontrolü patch'lerim veya girdiyi brute-force ederim.**

**Kalıp 4 — çoklu kontrol**
> Tek `jne`'yi nop'ladın ama hâlâ "Wrong". Neden? → **Birden çok karar noktası var (uzunluk+içerik+checksum); zincirin tamamını haritalamalıyım.**

---

## 60 saniyelik özet

- Crackme türleri: sabit parola, dönüşümlü, seri/anahtar, bayrak, zaman/deneme kısıtı.
- Doğrulama noktasını bul: string→xref, import(strcmp)→xref, veya `break strcmp` (dinamik).
- "Correct" dalı = karşılaştırmanın başarılı olduğu kol; ama ters kurgu ve çoklu kontrol olabilir.
- Girdi akışını haritala: girdi→dönüşüm(ler)→karşılaştırma. Dönüşüm tersine çevrilebilir mi?
- Strateji sırası: oku (en ucuz) → hesapla/keygen (en zarif) → patch (en kaba) → dinamik set (tek seferlik).
- Sadece yasal/kendi crackme'lerinde çalış.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. Bir crackme'nin türünü nasıl anlarsın (sabit mi dönüşümlü mü)?
2. Doğrulama noktasını bulmanın üç yolu nedir?
3. "Correct" dalını assembly'de nasıl tanırsın?
4. Bir dönüşümün "tersine çevrilebilir" olması ne demek, neden önemli?
5. Oku/hesapla/zorla stratejileri arasında nasıl seçim yaparsın?

---

## ✅ Test
➡️ **[01-test.html](01-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`02-keygen.md`](02-keygen.md)
