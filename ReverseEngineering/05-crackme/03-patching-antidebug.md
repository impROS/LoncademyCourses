# 03 — Binary Patching ve Anti-Debug'a İlk Bakış

> **Alan:** Crackme, keygen, patching (%12) — kararı zorla değiştirmek
> **Süre:** ~30 dakika okuma + 30 dakika pratik
> **Test:** [`03-test.html`](03-test.html) · 15 soru

---

## Neden bu konu

Bazen algoritmayı tersine çevirmek (keygen) zor veya imkânsızdır (tek yönlü hash, karmaşık kontrol). O
zaman en pratik yol **binary'yi patch'lemek**: karar veren komutu değiştirip programı "her zaman geç"
yapmak. Ayrıca birçok crackme **anti-debug** ile GDB'yi engellemeye çalışır — bunları tanıyıp atlatman
gerekir. En sık hata: patch'i yanlış komuta uygulamak ve programı bozmak, ya da anti-debug'ın seni
yanılttığını fark etmemek.

**Büyük fikir:** Patching = kararı **kaynakta değil, makine kodunda** değiştirmek. Tek bir byte'ı
(`jne`→`je` ya da `jne`→`nop`) çevirerek "yanlış" dalını "doğru" yapabilirsin. Anti-debug ise programın
"izleniyor muyum?" diye sorması; sen o soruyu "hayır" cevaplatırsın.

---

## 1. ⭐ Patching'in üç yolu

| Yöntem | Kalıcı mı | Nasıl |
|---|---|---|
| **Dinamik (GDB)** | Hayır (tek oturum) | `set $eflags` / `set $rip` / komut atlama |
| **Statik byte patch** | Evet | Dosyada ilgili byte'ı değiştir (hex editör / `dd` / Ghidra export) |
| **Ghidra patch + export** | Evet | Listing'de "Patch Instruction" → export |

Tek seferlik geçmek yeterse dinamik; kalıcı bir "cracked" binary istiyorsan statik patch.

---

## 2. ⭐ Karar komutunu değiştirmek (patch mantığı)

Doğrulama tipik olarak (5.1):
```asm
    test eax, eax
    jne  .wrong        ; <-- karar burada
```
Programı **her zaman geçirmek** için üç seçenek:

| Patch | Etki |
|---|---|
| `jne` → `je` | Koşulu tersine çevir (yanlışta geç, doğruda takıl — dikkat!) |
| `jne` → `jmp` | Her zaman "wrong değil" yönüne? Hayır — dikkatli düşün |
| `jne` → `nop` (2 byte) | Atlamayı iptal et; akış her zaman "Correct" dalına düşer |

**En güvenli:** genelde koşullu atlamayı **nop'lamak** (yani "yanlışsa atla"yı silmek) doğru dala düşürür.
Ama her durumda dalların ne yaptığını oku — bazı kurgularda `jne`→`je` gerekir.

**Byte seviyesi:** `jne` (kısa) = `0x75`, `je` (kısa) = `0x74`. Yani tek bir byte'ı `75`→`74` yapmak
`jne`'yi `je`'ye çevirir. `nop` = `0x90`.

> ⚠️ **Tuzak:** Komut **uzunluğunu** koru. `jne rel8` 2 byte'tır; onu 2 byte'lık bir şeyle değiştir
> (`je rel8` = 2 byte, veya iki `nop`). Farklı uzunlukta bir şey yazarsan sonraki komutları kaydırıp
> binary'yi bozarsın. Ghidra "Patch Instruction" bunu senin için hizalar.

---

## 3. ⭐ Statik patch uygulamak (adım adım)

1. **Adresi/offset'i bul.** Ghidra'da (veya `objdump`) karar komutunun **dosya offset'ini** belirle.
   Ghidra Listing'de adres → dosya offset eşlemesi gösterir; `objdump -d` sanal adres verir, offset'e
   çevirmen gerekebilir.
2. **Byte'ı değiştir.** En kolayı: Ghidra'da komuta sağ tık → **Patch Instruction** → yeni komutu yaz →
   **File → Export Program → Original File** ile patch'li binary'yi kaydet.
3. Alternatif (elle): hex editör (`hexedit`, `xxd` + `sed`) ile offset'teki `75`'i `74` yap.
4. **Test et:** Patch'li binary'yi çalıştır, herhangi bir girdiyle "Correct!" al.

**objdump ile offset bulma:** `objdump -d` sanal adres verir; dosya offset'i için binary'nin yükleme
tabanını çıkar (PIE değilse genelde adres − 0x400000 gibi, ama bu değişir — Ghidra'nın offset'ini
kullanmak en güvenlisi).

> ⚠️ **Tuzak:** Yanlış offset'e yazmak programı çökertir. Her zaman **yedek al** (`cp prog prog.bak`) ve
> patch sonrası `file`/çalıştırma ile sağlamlığı kontrol et.

---

## 4. ⭐ Anti-debug teknikleri ve atlatma

Crackme'ler GDB'yi engellemek için numaralar kullanır. En yaygınları:

| Teknik | Nasıl çalışır | Atlatma |
|---|---|---|
| **ptrace(PTRACE_TRACEME)** | Kendini trace eder; debugger varsa ikinci trace başarısız → tespit | Çağrının dönüşünü patch'le / GDB'de `set` ile 0 döndür |
| **/proc/self/status TracerPid** | Dosyada TracerPid≠0 ise debugger var | İlgili karşılaştırmayı patch'le |
| **Zamanlama (rdtsc)** | İki nokta arası çok uzun sürdüyse debug var | Kontrolü nop'la |
| **Breakpoint tespiti (0xCC arama)** | Kod içinde `int3` (0xCC) byte'ı arar | Yazılım yerine donanım breakpoint kullan |

**Genel atlatma refleksi:** Anti-debug bir **karar**dır ("izleniyor muyum? → evet ise çık"). Onu da diğer
kararlar gibi bul (import'ta `ptrace`, string'de `/proc/self/status`) ve **patch'le** ya da GDB'de sonucu
`set` ile değiştir.

> ⚠️ **Tuzak:** Anti-debug seni **yanlış yöne** de itebilir: debugger tespit edilince "Correct!" yazıp
> aslında yanlış bir yola sapmak gibi. Bu yüzden program davranışı GDB altında ve dışında **farklıysa**
> anti-debug'dan şüphelen.

---

## 5. Hangi yöntemi seçmeli

| Durum | En iyi yöntem |
|---|---|
| Bir kez geçmek yeter, kaynak binary'yi bozma | Dinamik (GDB `set`) |
| Kalıcı "cracked" kopya | Statik byte patch (Ghidra export) |
| Algoritma tersine çevrilebilir | Keygen (5.2) — patch yerine daha zarif |
| Anti-debug engelliyor | Önce anti-debug'ı patch'le/atlat, sonra asıl kontrolü |

**Etik hatırlatma:** Bu teknikler yalnızca **kendi crackme'lerinde ve yasal CTF'lerde** kullanılır.
Ticari yazılım patch'lemek/dağıtmak suçtur (bkz. `00-baslangic/01-yasal-etik.md`).

---

## Sık karıştırılanlar — tek tabloda

| Soruda/pratikte geçen | Doğru cevap | Neden diğeri değil |
|---|---|---|
| `jne` byte'ı | `0x75` (je = `0x74`) | Karıştırılır |
| `nop` byte'ı | `0x90` | — |
| Patch'te komut uzunluğu | Korunmalı | Farklı uzunluk binary'yi bozar |
| Dinamik vs statik patch | GDB tek oturum, byte kalıcı | Amaç belirler |
| Anti-debug | Bir karar (izleniyor muyum) | Sihir değil, patch'lenebilir |

---

## 🖥 Pratik — bir kararı patch'le ve anti-debug atlat

> **Amaç:** Koşullu atlamayı patch'leyip programı her zaman geçirmek; basit anti-debug'ı tanımak · **Süre:** 30 dk
> **💸 Maliyet:** Yok

### Adımlar
1. Anti-debug'lı bir crackme derle:
   ```bash
   cat > /tmp/pat.c <<'EOF'
   #include <stdio.h>
   #include <string.h>
   #include <sys/ptrace.h>
   int main(){
     if(ptrace(PTRACE_TRACEME,0,0,0)==-1){ puts("Debugger detected!"); return 1; } // anti-debug
     char b[32]; printf("Pw: ");
     if(scanf("%31s",b)!=1) return 1;
     if(strcmp(b,"verysecret")==0) puts("Correct!"); else puts("Wrong.");  // karar
     return 0;
   }
   EOF
   gcc -O0 -o /tmp/pat /tmp/pat.c
   cp /tmp/pat /tmp/pat.bak    # her zaman yedek
   ```
2. **Anti-debug'ı gör:** GDB'de çalıştırınca "Debugger detected!" çıkar (ptrace ikinci kez başarısız):
   ```bash
   gdb -q /tmp/pat -ex run -ex quit <<< "test"
   ```
   Bu, anti-debug'ın etkisidir.
3. **Karar komutunu bul:** `strcmp` sonrası `jne`'yi bul:
   ```bash
   objdump -d -M intel /tmp/pat | grep -nE "strcmp|jne|test" | head
   ```
   `strcmp`'ten sonraki `test`/`jne` karar noktasıdır.
4. **Dinamik patch (kalıcı olmayan):** GDB'de karşılaştırmadan sonra flag'i zorla. Örneğin `strcmp`'e
   breakpoint koy, `finish`, sonra `set $eax = 0` (eşitmiş gibi), `continue` → "Correct!".
   ```bash
   gdb -q /tmp/pat
   (gdb) break strcmp
   (gdb) run
   ...
   (gdb) finish
   (gdb) set $rax = 0
   (gdb) continue
   ```
   (Not: anti-debug GDB altında "Debugger detected" verebilir; o kararı da `set` ile geçmen gerekebilir —
   bu, "önce anti-debug'ı atlat" refleksinin pratiği.)
5. **Statik patch (kalıcı):** Ghidra'da `/tmp/pat`'ı aç, `strcmp` sonrası `jne`'yi bul, sağ tık →
   **Patch Instruction** → `nop`'la (veya `je` yap). **File → Export Program → Original File** ile
   `/tmp/pat_cracked` kaydet. Sonra:
   ```bash
   chmod +x /tmp/pat_cracked
   echo herhangi_bir_sey | /tmp/pat_cracked   # her girdiyle Correct beklenir
   ```

- [ ] **Kontrol:** Kararı patch'leyerek (dinamik veya statik) yanlış girdiyle "Correct!" alabildin mi?
- [ ] **Kaydet:** jne=0x75 je=0x74 nop=0x90 · uzunluğu koru · yedek al · anti-debug=ptrace kararı ______

### 💸 Temizlik
Bu pratikte ücret yok; ama ürettiğin binary'leri temizle:
```bash
rm -f /tmp/pat /tmp/pat.bak /tmp/pat_cracked /tmp/pat.c
```
- [ ] **Kontrol:** `/tmp`'te bu pratiğe ait dosya kalmadı.

*(Yalnızca kendi crackme'lerinde çalış — bkz. yasal/etik dosyası.)*

---

## Sınavda/pratikte nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — byte patch**
> `jne`'yi `je` yapmak için hangi byte'ı ne yaparsın? → **`0x75`'i `0x74`'e çeviririm (tek byte).**

**Kalıp 2 — nop'lama**
> Karar `test eax,eax; jne wrong`. Her zaman Correct için? → **`jne`'yi `nop nop` (2×0x90) yaparım; atlama iptal olur, akış Correct dalına düşer.**

**Kalıp 3 — anti-debug tanıma**
> Import'ta `ptrace`, GDB altında "Debugger detected". Ne bu? → **ptrace tabanlı anti-debug; kararını patch'ler veya GDB'de dönüşünü set ederim.**

**Kalıp 4 — uzunluk hatası**
> Patch sonrası program çöktü. Olası hata? → **Komut uzunluğunu bozdum; farklı boyutta byte yazıp sonrakileri kaydırdım.**

---

## 60 saniyelik özet

- Patch = kararı makine kodunda değiştirmek: dinamik (GDB `set`, tek oturum) veya statik (byte, kalıcı).
- Karar komutu: `jne`(0x75)↔`je`(0x74) çevir, ya da `nop`(0x90) ile atlamayı iptal et → Correct dalına düş.
- Komut **uzunluğunu koru**; Ghidra "Patch Instruction" + Export en güvenli yol. Her zaman yedek al.
- Anti-debug bir karardır ("izleniyor muyum"): ptrace, /proc TracerPid, rdtsc zamanlama, 0xCC arama.
- Atlatma: import/string'den anti-debug'ı bul, kararını patch'le veya GDB'de sonucu `set` et.
- GDB altında/dışında farklı davranış = anti-debug şüphesi. Yalnızca yasal/kendi crackme'lerinde.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. `jne`'yi `je`'ye çevirmek için hangi byte'ı nasıl değiştirirsin?
2. Patch'te neden komut uzunluğunu korumak zorundasın?
3. Dinamik (GDB) ve statik byte patch arasında ne zaman hangisini seçersin?
4. ptrace tabanlı anti-debug nasıl çalışır ve nasıl atlatılır?
5. Program GDB altında ve dışında farklı davranıyorsa ne düşünürsün?

---

## ✅ Test
➡️ **[03-test.html](03-test.html)** — 15 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

## Sırada ne var
➡️ [`../06-exploit/01-overflow-gozu.md`](../06-exploit/01-overflow-gozu.md)
