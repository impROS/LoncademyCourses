# 02 — Ortam Kurulumu: Linux, Ghidra, GDB+pwndbg, pwntools

> **Bu dosya:** Çalışma ortamını sıfırdan kurar. Sonunda "çalışıyor mu?" testleri var.
> **Süre:** ~45–90 dakika (indirmelerle)
> **💸 Maliyet:** Yok — her şey ücretsiz ve açık kaynak.

---

## Neden bu dosya

RE araçlarının çoğu Linux'ta yaşıyor. Sen macOS'tasın (Darwin), o yüzden bir Linux ortamına ihtiyacın var.
Bu dosya üç yol sunar, birini seç, sonra araçları kur. Kurulumu doğru yapmazsan sonraki hiçbir konu
çalışmaz — o yüzden sonundaki doğrulama adımlarını **atlama.**

**Büyük fikir:** Amaç, içinde `gcc`, `gdb`, `objdump`, `python3` olan bir **Linux x86-64 kabuğu** ve
ayrı olarak masaüstünde çalışan **Ghidra.** Gerisi detay.

---

## 1. Linux ortamı seç (macOS'tasın)

| Seçenek | Ne zaman | Zorluk | Not |
|---|---|---|---|
| ⭐ **UTM / VirtualBox VM** | Kalıcı, temiz, önerilen | Orta | Ubuntu 22.04+ veya Kali. Malware için de bunu kullanırsın. |
| **Docker konteyner** | Hızlı başlamak istiyorsan | Kolay | Ghidra GUI'si için ekstra ayar gerekir; CLI araçları hemen çalışır. |
| **Lima / colima** | Terminal seven | Orta | Hafif Linux VM; GUI yok, CLI mükemmel. |

> ⚠️ **Apple Silicon (M1/M2/M3) uyarısı:** Senin makinen ARM olabilir. x86-64 binary'leri çalıştırmak/analiz
> etmek için ya **x86 emülasyonlu bir VM** (UTM'de "Emulate" modu, yavaş ama çalışır) ya da statik analiz
> (Ghidra x86-64 binary'yi ARM makinede de okur — çünkü *çalıştırmıyor*, sadece *okuyor*). Kurs boyunca
> statik analiz her yerde çalışır; dinamik analiz (GDB ile *çalıştırma*) için x86-64 ortamı gerekir.
> **En sağlam yol:** x86-64 emülasyonlu Ubuntu VM. Yavaşlığı öğrenirken sorun olmaz.

### Önerilen: Ubuntu 22.04+ VM (UTM veya VirtualBox)
1. UTM (`mac.getutm.app`, ücretsiz) veya VirtualBox indir.
2. Ubuntu Desktop 22.04 LTS veya 24.04 LTS ISO'su indir.
3. Yeni VM: en az **4 GB RAM, 40 GB disk, 2 CPU.** Apple Silicon'da mimari olarak **x86_64/AMD64** seç (emülasyon).
4. Kurulumu tamamla, VM içinde bir terminal aç.

- [ ] **Kontrol:** VM içinde `uname -m` yaz → `x86_64` görmelisin (ARM VM'de `aarch64` çıkar — o zaman
  dinamik analiz için ayrı x86 ortamı gerekir).

---

## 2. Temel geliştirme + analiz araçları (Ubuntu/Debian tabanlı)

VM/konteyner içinde terminal aç ve şunu çalıştır:

```bash
sudo apt update
sudo apt install -y build-essential gdb binutils file xxd python3 python3-pip git ltrace strace radare2
```

Bu paketler ne veriyor:

| Paket | Ne için |
|---|---|
| `build-essential` | `gcc` derleyici — kendi test binary'lerini derleyeceksin |
| `gdb` | Dinamik analiz debugger'ı |
| `binutils` | `objdump`, `readelf`, `nm`, `strings` — statik inceleme |
| `file`, `xxd` | Dosya tanıma, hex dökümü |
| `radare2` | Alternatif RE framework (CLI) |
| `ltrace`, `strace` | Kütüphane/sistem çağrısı izleme |

- [ ] **Kontrol:** `gcc --version && gdb --version && objdump --version` → üçü de sürüm yazdırmalı.

---

## 3. pwndbg (GDB'yi RE için kullanışlı hale getiren eklenti) ⭐

Çıplak GDB acemi dostu değil. **pwndbg** onu ciddi şekilde iyileştirir: register'ları, stack'i,
disassembly'yi her adımda renkli gösterir.

```bash
cd ~
git clone https://github.com/pwndbg/pwndbg
cd pwndbg
./setup.sh
```

- [ ] **Kontrol:** `gdb` yaz, açılışta `pwndbg>` prompt'unu görmelisin (düz `(gdb)` değil).
- [ ] **Kaydet:** pwndbg kurulduğunda çıkan sürüm/uyarı varsa not et: ______

> Alternatif: **GEF** (`github.com/hugsy/gef`) veya **pwndbg** — ikisi de iyi. Bu kurs pwndbg komutlarını kullanır.

---

## 4. pwntools (exploit ve otomasyon kütüphanesi)

06 (exploit) bloğunda ve keygen otomasyonunda kullanacaksın.

```bash
python3 -m pip install --user pwntools
```

- [ ] **Kontrol:** `python3 -c "import pwn; print(pwn.__version__)"` → bir sürüm numarası yazdırmalı.

> Hata alırsan: `pip install --upgrade pip` deneyip tekrar dene. Bazı sistemlerde `pip3` gerekir.

---

## 5. Ghidra (statik analiz / decompiler) ⭐

NSA'in açık kaynak decompiler'ı. GUI uygulaması, JDK gerektirir.

1. **JDK kur** (Ghidra 12.x için JDK 21 önerilir — ⚠️ Doğrulanmalı: Ghidra'nın indirme sayfasındaki
   güncel JDK şartına bak):
   ```bash
   sudo apt install -y openjdk-21-jdk
   ```
2. **Ghidra'yı indir:** `github.com/NationalSecurityAgency/ghidra` → Releases → en güncel `ghidra_*.zip`
   (bu yazının hazırlandığı sırada **12.1.3**, Ağustos 2026).
3. Zip'i aç, klasöre gir, çalıştır:
   ```bash
   cd ~/ghidra_*/    # açtığın klasör
   ./ghidraRun
   ```

- [ ] **Kontrol:** Ghidra açılış ekranı (CodeBrowser projesi oluşturma penceresi) gelmeli.
- [ ] **Kaydet:** İndirdiğin Ghidra sürümü: ______ · JDK sürümü: ______

> ⚠️ **Doğrulanmalı:** Ghidra sürümü ve JDK şartı değişir. Releases sayfasındaki "What's New" dosyası
> gerekli JDK'yı yazar. JDK sürümü uyuşmazsa Ghidra açılmaz — hata mesajı hangi JDK'yı istediğini söyler.

---

## 6. İlk test binary'ni derle (her şey çalışıyor mu?)

Küçük bir C programı yazıp derleyelim — hem araçları test eder hem ilk analiz malzemen olur.

```bash
mkdir -p ~/re-lab && cd ~/re-lab
cat > merhaba.c <<'EOF'
#include <stdio.h>
#include <string.h>
int main() {
    char sifre[32];
    printf("Sifre: ");
    scanf("%31s", sifre);
    if (strcmp(sifre, "acmelabs2026") == 0)
        printf("Dogru!\n");
    else
        printf("Yanlis.\n");
    return 0;
}
EOF
gcc -no-pie -fno-stack-protector -o merhaba merhaba.c
```

(`-no-pie -fno-stack-protector`: analizi kolaylaştıran bayraklar; ileride ne olduklarını göreceksin.)

- [ ] **Kontrol 1 — çalışıyor mu:** `./merhaba` çalıştır, `acmelabs2026` yaz → `Dogru!` görmelisin.
- [ ] **Kontrol 2 — tanıma:** `file merhaba` → `ELF 64-bit LSB executable, x86-64` görmelisin.
- [ ] **Kontrol 3 — string:** `strings merhaba | grep -i sifre` → `Sifre: ` çıkmalı. Ayrıca `acmelabs2026`
  de `strings merhaba | grep acme` ile görünür — **ilk zaafiyet dersin:** şifre binary içinde açık duruyor!
- [ ] **Kontrol 4 — disassembly:** `objdump -d -M intel merhaba | grep -A2 strcmp` → `strcmp` çağrısını görmelisin.

Bu dört kontrol geçtiyse ortamın hazır. Tebrikler — az önce ilk statik analizini yaptın.

---

## 7. macOS'ta doğrudan çalışmak isteyenler için not

macOS'ta `lldb`, `otool`, ve Homebrew ile `radare2`/`ghidra` var. Ama:
- macOS binary'leri **Mach-O** formatı, bu kurs **ELF** (Linux) üzerine. Kavramlar aynı, detaylar farklı.
- Kursun dinamik analiz kısmı Linux/GDB varsayıyor.

**Öneri:** Öğrenirken Linux VM'de kal — internetteki writeup'ların ve lab'ların %95'i Linux/ELF. macOS'a
özel RE'ye sonra, temel oturduktan sonra bak.

---

## Sık takılınan yerler

| Belirti | Çözüm |
|---|---|
| Ghidra açılmıyor, JDK hatası | Hata mesajındaki JDK sürümünü kur; `java -version` ile doğrula |
| `gdb` açılışta `pwndbg>` göstermiyor | `~/.gdbinit` içinde pwndbg satırı var mı bak; setup.sh tekrar çalıştır |
| Apple Silicon'da binary "cannot execute" | ARM VM'desin; x86-64 emülasyonlu VM kur veya sadece statik analiz yap |
| `pip install pwntools` çöküyor | `sudo apt install python3-dev`, sonra tekrar dene |

---

## Özet — cebine koy

- Hedef: içinde `gcc/gdb/objdump/python3` olan **x86-64 Linux kabuğu** + masaüstünde **Ghidra**.
- macOS'ta yol: **Ubuntu VM** (Apple Silicon'da x86-64 emülasyonlu).
- Kur: `build-essential gdb binutils file radare2` → **pwndbg** → **pwntools** → **Ghidra + JDK**.
- Doğrulama: kendi `merhaba` binary'ni derle, `file`/`strings`/`objdump` ile incele.
- Değişebilir: Ghidra sürümü, gerekli JDK. Resmî sayfadan teyit et.

## Sırada ne var
➡️ [03-lab-siteleri.md](03-lab-siteleri.md)
