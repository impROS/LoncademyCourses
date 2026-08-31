# RE-LX64 — Linux x86-64 Reverse Engineering Çalışma Seti

> Sıfırdan (C ve assembly bilmeyen, ama Java/backend bilen biri için) başlayıp,
> ELF binary'lerini gerçek araçlarla söküp okuyabilen, crackme çözebilen, temel
> exploit yazabilen bir seviyeye çıkaran, **ölçerek ilerleyen** bir kurs.

Bu bir sohbet değil, diskte kalan ve haftalarca kullanacağın bir settir. Her konu kendi
`.md` dosyasında; her konudan sonra tarayıcıda çalışan **skorlu bir test** var (çift tıkla açılır,
internet/sunucu gerekmez). Pratikler gerçek, ücretsiz ve yasal sitelerde yapılır.

---

## Nasıl çalışılır (kurallar)

1. **Sırayla git.** Dosyalar bağımlılık sırasına dizildi. `01`'i atlayıp `04`'e geçme — çöker.
2. **Önce oku, sonra pratiği yap, en son teste gir.** Test, konuyu anladığını ölçer; kopya çekme yeri değil.
3. **Testte %80 altındaysan** sonuç ekranındaki kırmızı (zayıf) alt konulara dön, oku, **tekrar çöz.**
   Sorular ve şıklar her seferinde karışır — ezber işe yaramaz, anlamak zorundasın.
4. **Pratiği gerçekten yap.** Okuyup geçmek RE'de işe yaramaz. Binary'yi indir, aracı aç, elini kirlet.
   Her konudaki 🖥 Pratik bloğu tıklanacak yeri, girilecek komutu, beklenen çıktıyı söyler.
5. **Not defteri tut.** Her konunun sonunda "Kaydet: ____" satırları var. Kendi cheatsheet'ini biriktir.
6. **Takılınca 20 dakika kuralı.** Bir crackme'de 20 dk ilerleyemezsen writeup'a bakmak ayıp değil —
   **ama önce kendi denemeni yaz.** RE öğrenmek = takılma süresini yönetmeyi öğrenmek.

---

## Künye

| | |
|---|---|
| **Eksen** | Linux x86-64 (ELF), CTF temelli |
| **Varsayılan seviye** | Java/backend biliyor, C ve assembly yok |
| **Süre** | 8 hafta · haftada ~10 saat |
| **İçerik** | 18 konu + 4 giriş dosyası + 3 final dosyası |
| **Test** | Her konuda 12–16 soruluk tarayıcı testi + 2 genel deneme sınavı |
| **Araçlar** | Ghidra 12.x, GDB + pwndbg, pwntools, radare2 — hepsi ücretsiz/açık kaynak |
| **Platform** | Linux (yerli, VM veya WSL2). macOS'ta Docker/VM ile. |

> ⚠️ **Değişebilir bilgiler:** Araç sürümleri (Ghidra 12.1.3, Ağustos 2026), site kuralları ve
> ücretsiz-kademe şartları zamanla değişir. Kurs içinde `⚠️ Doğrulanmalı` ile işaretlenen yerleri
> resmî sayfadan teyit et.

---

## İlerleme tablosu

Kutucukları çözdükçe işaretle (dosyayı düzenle, `[ ]` → `[x]`).

### 00 — Başlangıç
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Reverse engineering nedir + oyunun kuralları](00-baslangic/00-genel-bakis.md) | — | — |
| [ ] | [Yasal & etik çerçeve — nerede pratik yapılır, nerede yapılmaz](00-baslangic/01-yasal-etik.md) | — | — |
| [ ] | [Ortam kurulumu — Linux, Ghidra, GDB+pwndbg, pwntools](00-baslangic/02-ortam-kurulumu.md) | — | — |
| [ ] | [Lab siteleri rehberi — hangi site ne için, nasıl başlanır](00-baslangic/03-lab-siteleri.md) | — | — |
| ❓ | [Soru & cevap defteri](soru-cevap.md) | başvuru | — |

### 01 — Makine seviyesi temeller *(%16)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Sayı sistemleri, bit, byte, endianness](01-temeller/01-sayilar-bellek.md) | [test](01-temeller/01-test.html) | — |
| [ ] | [Bellek modeli: stack, heap, register, adres](01-temeller/02-bellek-modeli.md) | [test](01-temeller/02-test.html) | — |
| [ ] | [Kaynaktan binary'ye: derleme, linkleme, ELF](01-temeller/03-derleme-elf.md) | [test](01-temeller/03-test.html) | — |

### 02 — x86-64 assembly okuma *(%26)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Register'lar ve temel komut seti](02-assembly/01-register-komut.md) | [test](02-assembly/01-test.html) | — |
| [ ] | [Bellek erişimi, adresleme, LEA, flag'ler](02-assembly/02-bellek-flag.md) | [test](02-assembly/02-test.html) | — |
| [ ] | [Kontrol akışı: jump, cmp, döngü, if/switch](02-assembly/03-kontrol-akisi.md) | [test](02-assembly/03-test.html) | — |
| [ ] | [Fonksiyon çağrısı: System V ABI, stack frame, argümanlar](02-assembly/04-fonksiyon-abi.md) | [test](02-assembly/04-test.html) | — |

### 03 — Statik analiz (Ghidra) *(%20)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Disassembler vs decompiler; Ghidra'ya giriş](03-statik/01-ghidra-giris.md) | [test](03-statik/01-test.html) | — |
| [ ] | [Decompile okuma, tip/isim düzeltme, veri akışı](03-statik/02-decompile-okuma.md) | [test](03-statik/02-test.html) | — |
| [ ] | [String, xref, sabit avı ile hızlı yön bulma](03-statik/03-string-xref.md) | [test](03-statik/03-test.html) | — |

### 04 — Dinamik analiz (GDB / pwndbg) *(%18)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [GDB + pwndbg temelleri: breakpoint, adımlama, register](04-dinamik/01-gdb-temel.md) | [test](04-dinamik/01-test.html) | — |
| [ ] | [Bellek/stack inceleme, watch, canlı veri takibi](04-dinamik/02-bellek-inceleme.md) | [test](04-dinamik/02-test.html) | — |
| [ ] | [Statik + dinamik birlikte: bir binary'yi baştan sona çözmek](04-dinamik/03-birlikte-analiz.md) | [test](04-dinamik/03-test.html) | — |

### 05 — Crackme, keygen, patching *(%12)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Lisans kontrolü mantığını bulmak ve kırmak](05-crackme/01-crackme-mantik.md) | [test](05-crackme/01-test.html) | — |
| [ ] | [Keygen yazmak: algoritmayı tersine çevirmek](05-crackme/02-keygen.md) | [test](05-crackme/02-test.html) | — |
| [ ] | [Binary patching ve anti-debug'a ilk bakış](05-crackme/03-patching-antidebug.md) | [test](05-crackme/03-test.html) | — |

### 06 — RE'den exploit'e köprü *(%8)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Buffer overflow'u RE gözüyle görmek](06-exploit/01-overflow-gozu.md) | [test](06-exploit/01-test.html) | — |
| [ ] | [pwntools + ret2win: ilk kontrollü exploit](06-exploit/02-pwntools-ret2win.md) | [test](06-exploit/02-test.html) | — |

### 99 — Final
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [Cheatsheet — her şey tek sayfada](99-final/00-cheatsheet.md) | başvuru | — |
| [ ] | [Son tekrar — bir CTF/sınav öncesi 24 saat](99-final/01-son-tekrar.md) | başvuru | — |
| [ ] | Genel deneme sınavı 1 | [test](99-final/genel-sinav-1.html) | — |

---

## Haftalık program

| Hafta | Odak | Dosyalar | Hedef |
|---|---|---|---|
| 1 | Kurulum + temeller | 00 tümü, 01/01–01/02 | Ortam çalışıyor, ilk `objdump`/`gdb` |
| 2 | Temelleri bitir + assembly başla | 01/03, 02/01–02/02 | ELF'i tanıyor, register/komut okuyor |
| 3 | Assembly'i bitir | 02/03–02/04 | Bir fonksiyonu elle takip edebiliyor |
| 4 | Statik analiz | 03/01–03/03 | Ghidra'da bir crackme decompile ediyor |
| 5 | Dinamik analiz | 04/01–04/02 | GDB'de breakpoint + bellek okuyor |
| 6 | Birlikte analiz + crackme | 04/03, 05/01 | İlk crackme'yi kırıyor |
| 7 | Keygen + patching | 05/02–05/03 | Keygen yazıyor / binary patch'liyor |
| 8 | Exploit köprüsü + final | 06/01–06/02, 99 tümü | ret2win çözüyor, deneme sınavları %80+ |

Yoğun (4 hafta) gidiyorsan haftaları ikişerle birleştir. Rahat (12 hafta) gidiyorsan her hafta bir konu + bol lab.

---

## Ne kurmalı / ne kurmamalı

**Kur (hepsi ücretsiz):**
- **Linux** (Ubuntu 22.04+ / Kali) — yerli, VirtualBox VM, veya Windows'ta WSL2. Ayrıntı: `00-baslangic/02-ortam-kurulumu.md`.
- **Ghidra** — NSA'in açık kaynak decompiler'ı. `github.com/NationalSecurityAgency/ghidra` (JDK gerektirir).
- **GDB + pwndbg**, **pwntools**, **radare2** — hepsi paket yöneticisinden.

**Kurma / para verme:**
- **IDA Pro lisansı alma.** IDA Free yeterli; kurs Ghidra üzerinden gider. Profesyonel olduğunda düşünürsün.
- **Ücretli "RE kursu" satın alma.** Bu set + linkteki ücretsiz platformlar zaten fazlasıyla yeter.
- **Binfmt/malware örnekleriyle çıplak makinede oynama.** Bu kurs Linux CTF binary'leriyle çalışır (zararsız),
  ama merakla indirdiğin her şeyi ana makinende çalıştırma. İzole VM kuralı `00-baslangic/01-yasal-etik.md`'de.

---

## Otomatik skor kaydı

Tarayıcı güvenlik nedeniyle diske **yazamaz**. Test sonuçlarının ilerleme
tablosuna kendiliğinden düşmesi için küçük bir yerel süreç çalıştırman gerekiyor:

```bash
cd /yol/ReverseEngineering && node assets/skor-sunucu.js
```

Sonra testleri `http://localhost:8890/01-temeller/01-test.html` gibi açabilirsin —
ya da dosyaya çift tıklamaya devam et, ikisi de çalışır.

> ⚠️ **Port neden 8890?** Skor sunucusunun genel varsayılanı 8899. Aynı anda
> başka bir kurs seti açıksa portu kapar, bu sunucu hiç açılmaz ve skorların
> **sessizce** kaydedilmez. Bu depodaki her kursa ayrı port verildi.

**Sunucu kapalıyken ne olur:** hiçbir şey kaybolmaz. Sonuç tarayıcının belleğinde
kuyruğa alınır, sunucuyu açıp herhangi bir testi yeniden açtığında gönderilir.

macOS'ta hep açık tutmak istersen: `assets/skor-sunucu.plist` dosyasındaki yolu
düzenleyip `~/Library/LaunchAgents/` altına kopyala, sonra
`launchctl load ~/Library/LaunchAgents/local.relx64.skor.plist`.

---

## Klasör ağacı

```
kurs/
├── README.md                       ← buradasın
├── assets/                         ← quiz.css · quiz.js · validate.js (elleme)
├── 00-baslangic/                   ← genel bakış · yasal/etik · kurulum · lab rehberi
├── 01-temeller/ … 06-exploit/      ← her konu: <kod>-<ad>.md + <kod>-test.html
└── 99-final/                       ← cheatsheet · son tekrar · 2 genel sınav
```

Testleri doğrulamak istersen (motor sağlam mı):

```bash
node kurs/assets/validate.js
```

Başla → [00-baslangic/00-genel-bakis.md](00-baslangic/00-genel-bakis.md)
