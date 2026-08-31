# Son Tekrar — Bir CTF/Sınav Öncesi 24 Saat

> **Bu dosya:** Bitişten/CTF'ten önceki son gün okunacak. Yeni konu öğrenme zamanı değil; **var olanı
> keskinleştirme** zamanı. Hazırlık kontrol listesi + strateji + refleks turu.

---

## Bu son gün ne yapmalı, ne yapmamalı

**Yap:**
- Cheatsheet'i (`00-cheatsheet.md`) bir kez baştan sona oku.
- Ortamının çalıştığını **doğrula** (Ghidra açılıyor, GDB+pwndbg yükleniyor, pwntools import ediliyor).
- İki genel deneme sınavını çöz; %80 altı kalan alt konulara **sadece o dosyaları** aç, 20 dk gözden geçir.
- Bir kolay crackme'yi baştan sona çöz (kas hafızası için).

**Yapma:**
- Gece boyu yeni bir teknik öğrenmeye çalışma (ROP'a ilk kez bu gece bakma).
- Bilmediğin araca kurulum saati harcama.
- Panikle her konuyu yeniden okuma — cheatsheet + zayıf alanlar yeter.

---

## Ortam kontrol listesi (15 dakika)

Sınav/CTF başlamadan bunların **çalıştığını gör:**

- [ ] `file`, `strings`, `objdump`, `nm` komutları var (`objdump --version`).
- [ ] `checksec` çalışıyor (yoksa pwntools ile gelir: `pwn checksec prog`).
- [ ] Ghidra açılıyor, bir binary import edip analyze edebiliyorsun.
- [ ] `gdb` açılıyor ve **pwndbg** yükleniyor (durunca renkli context basıyor).
- [ ] `python3 -c "import pwn"` hata vermiyor.
- [ ] Bir test binary'si derleyebiliyorsun (`gcc` var).
- [ ] İzole ortam (VM/konteyner) hazır — güvenilmeyen binary'yi ana makinede çalıştırma.

> Herhangi biri çalışmıyorsa **şimdi** düzelt; yarışma sırasında kurulumla uğraşmak zaman kaybıdır.

---

## Strateji — bir challenge'a oturunca

1. **Kimlik (30 sn):** `file` + `checksec` + `strings | less`. Ne tür binary, ne koruma, ilk ipuçları.
2. **Kolay kazancı ara (2 dk):** `strings` içinde düz flag/parola var mı? Bazen ödül orada.
3. **Haritalama (Ghidra):** analyze → `main`/`entry` → ilginç string'e xref → doğrulama fonksiyonu.
4. **Hipotez kur:** "Girdi şu döngüde işlenip şununla karşılaştırılıyor gibi."
5. **Doğrula (GDB):** doğru noktaya break, gerçek değerleri oku.
6. **Çöz:** oku/hesapla(keygen)/zorla(patch)/exploit — en ucuzdan başla.
7. **Kanıtla:** çalıştır, flag'i gör.

**Zaman yönetimi:** Bir challenge'da **20 dakika** ilerleyemezsen: not al, başka challenge'a geç, sonra dön.
Takılma süresini yönetmek CTF'in gerçek becerisidir (00-genel-bakis'te söylendiği gibi).

---

## Puan/öncelik stratejisi (CTF için)

- **Önce kolay ve yüksek puanlıları tara.** Çoğu CTF'te puan zorlukla artar; ama bazı "kolay" başlıklar
  çok değerli olabilir. Başlıkları hızlıca gez, en ucuz kazancı seç.
- **Düşük puanlı RE/pwn ile ısın.** İlk çözüm moral ve ivme verir.
- **Bir challenge'a saplanma.** İki-üç challenge'ı paralel açık tut; birinde tıkanınca diğerine geç.
- **Kısmi ilerlemeyi kaydet.** Ghidra projesini kaydet, notlarını tut; geri döndüğünde sıfırdan başlama.

---

## Refleks turu — hızlı hatırlatmalar

Bu soruları **kâğıda bakmadan** anında cevaplayabilmelisin. Takıldığında ilgili konuya dön.

**Sayılar/bellek:**
- `0x10` kaç? → 16. `0xFF` signed? → −1. x86-64 endianness? → little.
- Stack hangi yöne büyür? → aşağı (push→rsp azalır). Yerel değişken nerede? → `rbp-0x…`.

**Assembly:**
- `mov hedef, kaynak` yönü? → kaynaktan hedefe (Intel). `xor eax,eax`? → sıfırla.
- `je` hangi flag? → ZF=1. `jg` vs `ja`? → signed vs unsigned. Geri atlama? → döngü.
- 1. argüman? → rdi. Dönüş değeri? → rax. `lea` bellek okur mu? → hayır (adres).

**Araçlar:**
- Ghidra rename/retype? → `L` / `Ctrl+L`. String'i kim kullanıyor? → xref (References to).
- GDB parola yakalama? → `break strcmp` + `x/s $rdi`/`$rsi`. `si` vs `ni`? → gir vs atla.
- Offset ölçme? → cyclic. `p64`? → adresi little-endian paketle.

**Saldırı:**
- Doğrulama noktası bulma? → string/import xref veya break strcmp.
- XOR'un tersi? → aynı XOR. `jne`→her zaman geç? → nop'la (dalları oku).
- ret2win payload? → `b'A'*offset + p64(win)`. movaps çökmesi? → fazladan `ret`.

---

## Son 2 saat planı

| Süre | Ne yap |
|---|---|
| İlk 30 dk | Cheatsheet'i baştan sona oku |
| Sonraki 45 dk | Genel sınav 1'i çöz, zayıf alanları not al |
| Sonraki 30 dk | Zayıf 2-3 konu dosyasını hızlı gözden geçir |
| Son 15 dk | Ortam kontrol listesi + bir kolay crackme çöz (ısınma) |

Sonra **dur.** Dinlen. Yorgun beyin RE'de işe yaramaz; RE dikkat ve sabır işidir.

---

## Motivasyon — RE'de "iyi" ne demek

Unutma: RE bir **ezber** değil **dedektiflik** işidir (00-genel-bakis). İyi olmak, her komutu ezberlemek
değil; **doğru soruyu doğru araçla sormak** ve **takılma süreni yönetmek**tir. Bir challenge'ı çözemezsen
başarısız değilsin — writeup'a bakıp öğrenmek de ilerlemedir. Ölçüt nettir: flag'i buldun mu. Bulamadıysan
yarın bir tanesini daha çözersin.

**Başarılar. Elini kirlet, sabırlı ol, aracı doğru seç.**

---

➡️ Cheatsheet: [`00-cheatsheet.md`](00-cheatsheet.md)
➡️ Deneme sınavları: [`genel-sinav-1.html`](genel-sinav-1.html) · [`genel-sinav-2.html`](genel-sinav-2.html)
