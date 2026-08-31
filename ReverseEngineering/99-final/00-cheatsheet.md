# Cheatsheet — RE-LX64 Her Şey Tek Sayfada

> **Bu dosya:** Her konunun tek satırlık özeti + en çok karıştırılan ikililer + ezberlenecek sayılar +
> komut kartları. Bir CTF/sınav öncesi buraya bak; takıldığında ilgili konu dosyasına dön.

---

## Konu konu tek satır

**01 · Temeller**
- **Sayılar:** `0x10=16`, `0xFF=255`(u)/−1(s), `0x100=256`; word=16, dword=32, qword=64 bit; x86-64 **little-endian**.
- **Bellek:** stack aşağı büyür (`push`→rsp azalır); yereller `rbp-0x…`, dönüş adresi/argümanlar `rbp+`; `[x]`=dereference.
- **Derleme/ELF:** kaynak→önişlem→derle→assemble→link→ELF; magic `7f 45 4c 46`; stripped=kendi isimlerin gitti; ilk refleks `file`/`checksec`/`strings`.

**02 · Assembly**
- **Register/komut:** `rax/eax/ax/al` aynı yer; `eax`'e yazma üstü sıfırlar, `al`'e yazma korur; Intel: `mov hedef,kaynak`; `xor r,r`=sıfırla.
- **Bellek/flag:** `[base+idx*ölçek+off]` (`*4`=int,`*8`=ptr); `lea`=adres/aritmetik (bellek okumaz); ZF=eşit/sıfır, SF=negatif; `cmp`/`test` sadece flag.
- **Kontrol akışı:** if/for/switch → `cmp`+koşullu `jmp`; `je`=ZF, `jg/jl`=signed, `ja/jb`=unsigned; geri atlama=döngü; `jmp [t+r*8]`=switch.
- **Fonksiyon ABI:** argümanlar `rdi,rsi,rdx,rcx,r8,r9`, dönüş `rax`; prologue `push rbp;mov rbp,rsp;sub rsp,N`, epilogue `leave;ret`.

**03 · Statik (Ghidra)**
- **Giriş:** disassembler=kesin, decompiler=tahmin; akış import→analyze→Symbol Tree→Decompiler↔Listing; packed'i statik göremez.
- **Decompile okuma:** `L`=rename, `Ctrl+L`=retype; `param_N`=argüman, `undefined8`=bilinmeyen tip, `CONCAT/SUB`=tip artığı; girdi→dönüşüm→karşılaştırma.
- **String/xref:** `strings`→ipucu, "Correct" xref→doğrulama; sabitler algoritma ele verir (`0x9e3779b9`=TEA, `0x67452301`=MD5); import `ptrace`=anti-debug.

**04 · Dinamik (GDB)**
- **Temel:** `break main/*addr/strcmp`, `run`, `si`(gir)/`ni`(atla), `finish`; PIE'de sembol kullan; `x/s $rdi` string, `set $rax=..` değiştir.
- **Bellek/stack:** `x/[n][fmt][boyut]` (b/w/g=1/4/8); `watch`(yaz)/`rwatch`(oku); `bt`=çağrı zinciri; stack'te dönüş adresi=.text değeri.
- **Birlikte:** statik=harita("ne"), dinamik=keşif("şu an değer"); akış kimlik→harita→hipotez→doğrula→çöz→kanıtla; takılınca araç değiştir.

**05 · Crackme/keygen/patch**
- **Mantık:** türler sabit/dönüşümlü/seri/flag; doğrulama noktası string/import xref veya break strcmp; strateji oku→hesapla→zorla.
- **Keygen:** doğrulamanın tersi; XOR↔XOR, +↔−; doğrusal kısıtta birini serbest bırak; tüm kısıtları aynı anda sağla; `python3 keygen.py | ./cm`.
- **Patch/anti-debug:** `jne`=0x75,`je`=0x74,`nop`=0x90; uzunluğu koru; Ghidra Patch Instruction+Export; anti-debug=karar (ptrace/TracerPid), bul&patch'le.

**06 · Exploit**
- **Overflow:** sınırsız `gets`/`scanf("%s")`→komşu stack ezilir→dönüş adresi; offset=cyclic ile ölç; `p64` little-endian; checksec=koruma.
- **pwntools/ret2win:** `ELF()`+`process/remote`+`recvuntil`+`sendline(b'A'*offset+p64(win))`; `elf.symbols['win']`; movaps çökmesi→fazladan `ret`.

---

## En çok karıştırılan ikililer

| A | B | Ayıran cümle |
|---|---|---|
| `0x10` | 10 (onluk) | `0x`=hex; `0x10=16` |
| `0xFF` unsigned (255) | signed (−1) | En üst bit 1 → two's complement negatif |
| `lea [x]` | `mov [x]` | lea=adres, mov=veri |
| `eax`'e yazma (üst sıfırlanır) | `al`'e yazma (üst korunur) | 32-bit yazma üstü siler |
| `jg` (signed) | `ja` (unsigned) | l/g=signed, a/b=unsigned |
| `je` (ZF) | `js` (SF) | eşitlik ZF, negatiflik SF |
| section | segment | section=analiz, segment=çalıştırma |
| statik link | dinamik link | statik=içine gömülü, dinamik=ayrı .so |
| disassembler | decompiler | kesin vs tahmini |
| breakpoint | watchpoint | kod vs veri |
| `si` (gir) | `ni` (atla) | call davranışı |
| keygen | parola bulma | üreteç vs tek değer |
| `p64` | `u64` | paketle vs çöz |
| `jne→je` | `jne→nop` | koşul ters çevir vs iptal |
| ret2win | ret2libc | var olan fonksiyon vs libc |

---

## Ezberlenecek sayılar

| Değer | Anlam |
|---|---|
| `0x41`='A', `0x61`='a', `0x30`='0', `0x20`=boşluk, `0x00`=NUL | ASCII kancaları |
| `0xFF`=255/−1, `0x7F`=127, `0x80`=−128 | byte sınırları |
| word=16, dword=32, qword=64 bit | genişlikler |
| `jne`=0x75, `je`=0x74, `nop`=0x90, `int3`=0xCC | patch byte'ları |
| `rdi,rsi,rdx,rcx,r8,r9` → dönüş `rax` | argüman/dönüş (Linux) |
| ELF magic `7f 45 4c 46` | dosya imzası |
| `0x9e3779b9`=TEA delta, `0x67452301`=MD5 init, `0x539`=1337 | sabit parmak izleri |
| `x` boyut: b=1, w=4, g=8 | GDB examine |

---

## Komut kartları

**Terminal — kimlik:**
```bash
file prog          # 32/64-bit, statik/dinamik, stripped
checksec prog      # NX, PIE, canary, RELRO
strings -n 6 prog  # okunur metinler
objdump -d -M intel prog | sed -n '/<main>:/,/ret/p'   # Intel disassembly
nm prog            # semboller (stripped'te boş)
```

**GDB — çekirdek:**
```
break main / break *0xADDR / break strcmp
run   /  run < input
si (gir) · ni (atla) · finish · continue
x/s $rdi · x/8xg $rsp · x/i $rip
info registers · p/x $rax · set $rax=0
watch var · bt · telescope $rsp   (pwndbg)
cyclic 100 · cyclic -l <deger>    (pwndbg, offset)
```

**Ghidra — akış:**
```
New Project → Import File → Analyze (Yes)
Symbol Tree → Functions → main (yoksa entry)
Decompiler ↔ Listing (senkronize)
L = rename · Ctrl+L = retype · ; = yorum
sağ tık → References → Show References to (xref)
Search → For Scalars (sabit avı)
Patch Instruction → Export Program → Original File
```

**pwntools — ret2win iskeleti:**
```python
from pwn import *
context.binary = elf = ELF('./vuln')
p = process('./vuln')            # veya remote('host', port)
p.recvuntil(b'Input: ')
payload = b'A'*offset + p64(elf.symbols['win'])
p.sendline(payload); p.interactive()
```

---

## Genel refleks sırası (yeni bir binary)

1. `file` + `checksec` + `strings` → kimlik ve ilk ipuçları.
2. Ghidra: analyze → `main`/`entry` → ilginç string'e **xref** → doğrulama fonksiyonu.
3. Decompile'ı `rename`/`retype` ile okunur yap → girdi→dönüşüm→karşılaştırma zincirini çıkar.
4. Belirsizse GDB: doğru noktaya `break` → gerçek değerleri oku → hipotezi doğrula.
5. Çöz: **oku** (gömülü değer) / **hesapla** (keygen) / **zorla** (patch) / **exploit** (overflow).
6. **Kanıtla:** çalıştır, "Correct!"/flag gör. 20 dk takılırsan yaklaşımı değiştir.

---

➡️ Sınavdan 24 saat önce: [`01-son-tekrar.md`](01-son-tekrar.md)
➡️ Kendini sına: [`genel-sinav-1.html`](genel-sinav-1.html) · [`genel-sinav-2.html`](genel-sinav-2.html)
