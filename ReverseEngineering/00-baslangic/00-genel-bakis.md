# 00 — Reverse Engineering Nedir + Oyunun Kuralları

> **Bu dosya:** Konuya başlamadan önce sahayı tanıtır. Test yok — bu bir harita.
> **Süre:** ~15 dakika okuma

---

## Neden bu dosya

RE'ye çoğu insan "assembly ezberleyeyim" diye başlar ve boğulur. Yanlış giriş. RE bir **ezber işi değil,
bir dedektiflik işidir.** Elinde çalışan bir program var, kaynağı yok, ve sen onun ne yaptığını —
bazen nasıl kandırılacağını — anlamaya çalışıyorsun. Assembly sadece kanıtların yazıldığı dil.

Bu dosyayı okuduğunda şunu bileceksin: bu iş neden var, nasıl ölçülüyor, insanlar en çok nerede kayboluyor,
ve senin (Java bilen ama C/assembly bilmeyen biri olarak) hangi refleksleri kurman gerekiyor.

**Büyük fikir:** Reverse engineering = *çalışan bir şeyi, kaynağı olmadan, gözlemleyerek anlamak.*

---

## Reverse engineering tam olarak ne

Normal yazılım geliştirme ileriye gider: **fikir → kaynak kod → derleyici → makine kodu (binary).**

RE bu oku tersine yürür: **binary → ne yaptığını anlamak → (bazen) kaynak mantığını yeniden kurmak.**

Derleyici tek yönlü bir kıyma makinesi gibidir: değişken isimleri, yorumlar, tip bilgisi, fonksiyon
sınırlarının çoğu **kıyılıp gider.** Geriye sadece işlemcinin anlayacağı ham komutlar kalır. RE'nin işi
o kıymadan anlamlı bir tabak çıkarmak.

### Nerede kullanılır (hepsi meşru)

| Alan | Ne yapılır |
|---|---|
| **Güvenlik araştırması** | Zafiyet bulma, yama analizi, exploit geliştirme (yetkiyle) |
| **Malware analizi** | Zararlının ne yaptığını izole ortamda çözmek |
| **CTF / yarışma** | Kasıtlı bulmaca binary'lerini çözmek (bizim ana pratik alanımız) |
| **Uyumluluk (interop)** | Kapalı bir formatı/protokolü anlayıp onunla konuşan yazılım yazmak |
| **Hata ayıklama** | Kaynağı olmayan bir kütüphanenin neden çöktüğünü bulmak |

Bu kursta ağırlık **CTF ve crackme** — çünkü orada binary'ler *öğretmek için* tasarlanmış, yasal olarak
paylaşılıyor ve zararsız. Öğrendiğin refleksler diğer alanlara birebir taşınır.

---

## Bu iş nasıl "ölçülüyor" — yani ne zaman iyi olduğunu anlarsın

CTF/crackme dünyasında ölçüt nettir: **flag'i buldun mu, bulmadın mı.** Bir crackme'de doğru şifreyi
girip "Correct!" gördün mü, o seviyeyi geçtin. Ara metrik yok. Bu iyi bir şey — kendini kandıramazsın.

Beceri merdiveni kabaca şöyle:

1. **Okuyabiliyor musun?** Bir disassembly/decompile çıktısına bakıp "burada bir döngü var, şu değeri
   sayıyor" diyebiliyor musun.
2. **Yön bulabiliyor musun?** 2000 satırlık bir fonksiyonda "flag kontrolü şurada" noktasını bulabiliyor musun.
3. **Tersine çevirebiliyor musun?** "Program şifreyi şöyle hesaplıyor" deyip doğru girdiyi üretebiliyor musun.
4. **Manipüle edebiliyor musun?** Programı patch'leyip veya çalışırken kandırıp istediğini yaptırabiliyor musun.

Bu kurs seni 1'den 4'e taşır.

---

## İnsanlar en çok nerede kayboluyor (bunları baştan bil)

| Tuzak | Gerçek |
|---|---|
| **"Her komutu anlamalıyım"** | Hayır. Deneyimli tersine mühendis binary'nin %90'ını okumadan atlar, sadece kritik %10'a odaklanır. Yön bulmak, her satırı okumaktan önemli. |
| **"Sadece okursam öğrenirim"** | RE kas hafızasıdır. Ghidra'yı açıp 50 crackme'ye bakmadan "decompile okuma" oturmaz. Bu yüzden her konuda gerçek lab var. |
| **"Assembly'yi ezberleyeceğim"** | ~30 komut günlük işin %95'ini kapsar. Kalanını gördüğünde ararsın. Referans kartı ezber yerine geçer. |
| **"Takılırsam başarısızım"** | Takılmak işin kendisi. Fark: acemi 6 saat aynı yere bakar; usta 20 dakikada yaklaşımını değiştirir. |
| **Araç hayranlığı** | Ghidra/IDA/GDB araçtır, çözüm değil. Aracı tanı ama "hangi araç daha iyi" tartışmasına saplanma. |

---

## Senin avantajın ve dezavantajın (Java/backend geçmişiyle)

**Avantaj:**
- Değişken, döngü, koşul, fonksiyon, veri yapısı kavramlarını zaten biliyorsun. Assembly bunların
  *çıplak* halinden ibaret. `for` döngüsünü tanıyorsan, onun assembly karşılığını tanımak an meselesi.
- Debugger mantığına (breakpoint, step, değişken izleme) yabancı değilsin. GDB sadece daha ham.

**Dezavantaj (kurs bunları kapatacak):**
- **Pointer ve manuel bellek.** Java seni bunlardan korudu. RE'de her şey adres ve bellek. `01-temeller` bunu kuruyor.
- **Stack'in fiziksel gerçekliği.** Java'da "stack" soyut; burada gerçek adresler, gerçek register'lar.
- **Tip yok.** Binary'de bir `int` ile bir `pointer` aynı 8 byte. Anlamı sen vereceksin.

Kısacası: kavramların hazır, eksiğin **makinenin çıplak gerçekliği.** Kurs tam oradan başlıyor.

---

## Bu kursun sana kazandıracağı refleksler

Bitirdiğinde şunları otomatik yapıyor olacaksın:

- Bir binary'yi görünce önce `file`, `strings`, `checksec` çalıştırmak (tanıma refleksi).
- Ghidra'da String penceresinden "Correct/Wrong" gibi ipuçlarına gidip xref ile kontrol noktasını bulmak.
- Bir karşılaştırma (`cmp`) gördüğünde "burada bir karar veriliyor, iki dala da bakayım" demek.
- GDB'de kritik fonksiyona breakpoint koyup register'ları okuyarak "program şu an ne düşünüyor" demek.
- Takıldığında yaklaşımı değiştirmek: statik tıkandıysa dinamiğe, dinamik tıkandıysa statiğe geçmek.

---

## Nasıl ilerleyeceksin — özet

1. Sırayla oku. Her konu bir öncekine yaslanıyor.
2. Her konunun 🖥 Pratik bloğunu **gerçekten yap** — binary indir, aracı aç.
3. Teste gir, %80 tuttur. Tutmazsan zayıf başlıklara dön.
4. Kendi cheatsheet'ini biriktir (final klasöründe iskeleti var).

Bir sonraki dosya, pratiği nerede yapacağının **yasal ve etik** çerçevesi. Bunu atlamak yok — RE'de
"nerede oynanır, nerede oynanmaz" bilgisi tekniğin kendisi kadar önemli.

## Sırada ne var
➡️ [01-yasal-etik.md](01-yasal-etik.md)
