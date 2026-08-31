# 03 — Lab Siteleri Rehberi: Hangi Site Ne İçin, Nasıl Başlanır

> **Bu dosya:** Pratik yapacağın tüm ücretsiz/yasal platformların haritası. Kurs boyunca bu dosyaya döneceksin.
> **Süre:** ~15 dakika okuma + hesap açma
> **💸 Maliyet:** Hepsi ücretsiz. Bazıları (isteğe bağlı) hesap ister.

---

## Neden bu dosya

RE okuyarak değil, **binary çözerek** öğrenilir. Ama "nereden binary bulacağım, hangi site ne seviyeye
uygun" sorusu acemileri erken boğar. Bu dosya sana bir menü verir: her sitenin ne işe yaradığını,
hangi konuda kullanacağını, nasıl başlayacağını söyler. Konu dosyaları buraya link verir.

**Büyük fikir:** *Doğru zorlukta bol bol binary çöz.* Fazla kolay = sıkılırsın; fazla zor = pes edersin.
Bu menü seni her konuda tam kıvamında malzemeye yönlendirir.

---

## Platform menüsü — hangi site ne için

| Site | Ne | Hesap? | Kurs bloğu | Seviye |
|---|---|---|---|---|
| ⭐ **pwn.college** | Rehberli dojo'lar, tarayıcıda VM + Ghidra | Gerekli (ücretsiz) | 02, 03, 04, 06 | Başlangıç→İleri |
| ⭐ **picoCTF / picoGym** | CMU'nun eğitim CTF'i, 7/24 pratik | Gerekli (ücretsiz) | 03, 05 | Başlangıç |
| ⭐ **crackmes.one** | Topluluk crackme arşivi, 4500+ binary | Opsiyonel | 05 | 1→6 (kendi ölçeği) |
| **ROP Emporium** | 8 kademeli exploit/ROP öğretimi | Yok | 06 | Orta |
| **Microcorruption** | Tarayıcıda MSP430 CTF, gömülü kilit | Gerekli (ücretsiz) | 04, 05 | Başlangıç→Orta |
| **exploit.education** | Phoenix/Nebula: bellek güvenliği lab'ları | Yok (VM indir) | 06 | Başlangıç→Orta |
| **OverTheWire** | Wargame'ler; özellikle **Narnia** binary temeli | Yok (SSH) | 02, 04 | Başlangıç |
| **OST2 (ost2.fyi)** | Ücretsiz derinlemesine dersler (Arch1001, RE1001) | Gerekli (ücretsiz) | 01, 02 referans | Referans |
| **Flare-On arşivi** | Mandiant'ın yıllık CTF'i, geçmiş challenge'lar | Yok (indir) | İleri/bonus | Orta→Zor |

> ⚠️ **Doğrulanmalı:** Sitelerin ücretsiz-kademe şartları, kayıt gereksinimi ve içerikleri değişebilir.
> Kayıt olurken sadece gerekeni ver; şüpheli izin isteyen bir siteye girme.

---

## 1. pwn.college ⭐ — ana rehberli platform

**Ne:** Arizona State Üniversitesi'nin açık cybersecurity "dojo"su. Tarayıcıda çalışan bir Linux VM
veriyor, içinde **Ghidra, GDB, IDA Free, radare2** hazır. Video anlatım + kademeli challenge.

**Neden bizim için ideal:** Kurulumla uğraşmadan, tarayıcıda, rehberli pratik. Bizim ilgilendiğimiz
modüller (dojo'lar → modüller):
- **Computing 101** → "The Stack", "Assembly Assortment", "Debugging Refresher" (01–02 konularımızla birebir)
- **Program Security** → **"Reverse Engineering"** (36 challenge), "Program Security" (03–05 için)
- **Intro to Cybersecurity** → "Reverse Engineering" (39 challenge)

**Nasıl başlanır:**
1. `pwn.college` → kayıt ol (ücretsiz), e-postanı doğrula.
2. "Program Security" dojo'suna gir → "Reverse Engineering" modülü.
3. İlk challenge'ı aç → "Start" ile tarayıcı VM'i başlat → Ghidra/terminal orada.
4. Flag'i bulunca challenge sayfasına yapıştır, yeşil onay al.

- [ ] **Kaydet:** Kullanıcı adın (ilerlemen buna bağlı): ______

---

## 2. picoCTF / picoGym ⭐ — başlangıç CTF'i

**Ne:** Carnegie Mellon'un eğitim CTF'i. **picoGym** = geçmiş soruların 7/24 açık pratik havuzu.
%100 ücretsiz, paywall yok.

**Neden:** Reverse Engineering kategorisi başlangıç için mükemmel; her soru bir mini-ders. Zorluk
filtresi var (Easy/Medium/Hard).

**Nasıl başlanır:**
1. `picoctf.org` → hesap aç → "picoGym Practice" (veya "Practice").
2. Kategori filtresi: **Reverse Engineering**, zorluk: **Easy**.
3. Klasik başlangıç soruları: `GDB Baby Step` serisi, `vault-door` serisi, `ARMssembly`, `Transformation`.
4. Binary'yi indir, çöz, flag'i (`picoCTF{...}`) gir.

- [ ] **Kaydet:** İlk çözdüğün RE sorusunun adı: ______

---

## 3. crackmes.one ⭐ — crackme cenneti

**Ne:** İnsanların *çözmen için* yüklediği 4500+ crackme. Her birinin zorluk (1–6) ve kalite puanı,
platform etiketi (Unix/Windows), dil bilgisi var. Writeup'lar da paylaşılıyor.

**Neden:** 05 (crackme/keygen) bloğunun ana malzemesi. Sınırsız, çeşitli, gerçek pratik.

**Nasıl başlanır:**
1. `crackmes.one` → "Search" → filtre: **Platform: Unix/Linux**, **Difficulty: 1**, **Language: C/C++**.
2. Bir crackme indir. **Arşiv şifresi her zaman `crackmes.one`** (zip açarken sorar).
3. **Önce izole ortamda çalış** (VM'in içinde). Binary'yi `file` ile tanı, sonra analiz et.
4. Çözünce: yazarın istediği buysa keygen yaz; sadece "doğru şifreyi buldum" da bir başlangıç.

**Kurallar (siteden):**
- Amaç şifreyi bulmak değil, **nasıl** bulduğunu anlamak. İyi çözüm = keygen + yöntem yazısı.
- **Patch'leme** genelde geçersiz sayılır (yazar aksini söylemedikçe) — algoritmayı anla, binary'yi bozma.
- Oyun hilesi / ticari yazılım kırma **yasak** — site sadece öğretim crackme'leri barındırır.

- [ ] **Kaydet:** İlk kırdığın crackme'nin adı ve zorluğu: ______

---

## 4. ROP Emporium — exploit/ROP öğretimi

**Ne:** Tek konuya odaklı 8 kademeli challenge: **ret2win → split → callme → write4 → badchars →
fluff → pivot → ret2csu.** Her biri 32-bit ve 64-bit sürümlü. Bizim ilgimiz **x86-64.**

**Neden:** 06 (exploit köprüsü) bloğunun ana lab'ı. Buffer overflow'dan ROP'a geçişi adım adım öğretir.

**Nasıl başlanır:**
1. `ropemporium.com` → "Beginners' guide" oku (arka planı verir).
2. **ret2win** challenge sayfası → 64-bit binary indir.
3. Kurstaki 06/02'de bunu pwntools ile çözeceğiz.

---

## 5. Microcorruption — tarayıcıda gömülü CTF

**Ne:** Tarayıcıda çalışan bir MSP430 (gömülü işlemci) debugger'ı ve kademeli kilit-kırma CTF'i.
Kurulum yok, her şey tarayıcıda. Seviyeler şehir adlı: Tutorial → New Orleans → Sydney → ...

**Neden:** Farklı bir mimari (MSP430) görmek, disassembly + debugger refleksini eğlenceli şekilde
pekiştirmek için. x86 dışında bir dünya olduğunu gösterir.

**Nasıl başlanır:**
1. `microcorruption.com` → hesap aç → **Tutorial** seviyesi (arayüzü öğretir).
2. **New Orleans**'a geç — ilk gerçek bulmaca.
3. Sağda disassembly, altta debugger, canlı bellek. Kilidi açan girdiyi bul.

---

## 6. exploit.education & OverTheWire — bellek güvenliği ve wargame

**exploit.education:** Phoenix ve Nebula VM'leri — bellek güvenliği zafiyetlerini kademeli öğretir.
VM indirip çalıştırırsın. 06 bloğuna destek.

**OverTheWire:** SSH ile bağlanılan wargame'ler. **Narnia** serisi basit binary exploitation/RE için iyi
başlangıç. `overthewire.org/wargames/narnia` → SSH bilgileri sayfada.

**Nasıl başlanır (OverTheWire Narnia):**
```bash
ssh narnia0@narnia.labs.overthewire.org -p 2226
# parola sayfada yazılı: narnia0
```

---

## 7. OST2 — derinlemesine ücretsiz dersler (referans)

**Ne:** OpenSecurityTraining2 — üniversite düzeyinde, ücretsiz, video+slayt dersler. Bizim için:
- **Arch1001: x86-64 Assembly** — 01–02 konularımızın derin referansı.
- **RE1001 / Debuggers serisi** — takıldığında derinleşmek için.

**Nasıl:** `ost2.fyi` → kayıt ol → ilgili kursu aç. Bu kursta *referans* olarak kullan — bir konuda
daha çok derinlik istersen buraya git. ⚠️ Doğrulanmalı: kurs kodları/isimleri güncellenebilir.

---

## Nasıl seçmeli — pratik akışı

Her konu dosyası sana **hangi siteyi, hangi challenge'ı** söyleyecek. Ama genel refleks:

1. **Kavramı yeni öğrendiysen** → pwn.college veya picoGym'de o kavramın kolay sorusu.
2. **Pekiştirmek istiyorsan** → crackmes.one'da zorluk 1–2 aynı türden birkaç crackme.
3. **Zorlanmak istiyorsan** → zorluğu bir kademe artır, veya ROP Emporium / Microcorruption sıradaki seviye.
4. **Tıkandıysan** → 20 dk kendi denemeni yaz, sonra o challenge'ın writeup'ına bak, öğren, benzerini writeup'sız çöz.

---

## Özet — cebine koy

- **Rehberli + tarayıcı VM:** pwn.college (ana platform, Ghidra hazır).
- **Başlangıç CTF:** picoGym (RE / Easy).
- **Sınırsız crackme:** crackmes.one (arşiv şifresi `crackmes.one`, patch'leme geçersiz).
- **Exploit/ROP:** ROP Emporium (ret2win'den başla), exploit.education, OverTheWire Narnia.
- **Farklı mimari, eğlence:** Microcorruption (tarayıcıda MSP430).
- **Derin referans:** OST2 Arch1001.
- **Kural:** Doğru zorlukta bol pratik; tıkanınca 20 dk sonra writeup.

Artık ortam ve saha hazır. Gerçek öğrenme başlıyor: makine seviyesi temeller.

## Sırada ne var
➡️ [../01-temeller/01-sayilar-bellek.md](../01-temeller/01-sayilar-bellek.md)
