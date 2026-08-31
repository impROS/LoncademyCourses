# 03 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 01 Disassembler vs decompiler; Ghidra'ya giriş

### Soru 1 — Disassembler ile decompiler arasındaki temel güvenilirlik farkı nedir?

**Kısa cevap:** **Disassembler kesindir, decompiler tahminidir.** Disassembler byte'ların birebir karşılığını verir; decompiler yapı ve tipleri yeniden kurduğu için yanılabilir.

**Ayrıntı:** Disassembler girdiyi (byte) çıktıya (assembly komutları) doğrudan çevirir — sonuç makine kodunun kesin karşılığıdır, her zaman güvenilir. Decompiler ise assembly'den C benzeri kod üretir; `if`/`while`/değişken gibi yapıları ve tipleri **yeniden inşa eder**, bu bir tahmindir. Bu yüzden decompiler'a şüpheyle güvenir, şüphelenince Listing (assembly) penceresiyle doğrularsın.

📌 **Sık yapılan hata:** Decompiler çıktısına kaynak kod gibi güvenmek. O bir tahmindir; yanlış tip tahmini mantığı ters bile gösterebilir.

🔗 [01 §1 Disassembler vs decompiler](01-ghidra-giris.md)

### Soru 2 — `uVar1` veya `local_28` gibi isimler nereden gelir?

**Kısa cevap:** **Ghidra'nın uydurduğu isimlerdir**, kaynaktaki gerçek isimler değildir.

**Ayrıntı:** Decompiler değişkenlere kendi ürettiği adları verir: `uVar1` bir geçici değişkendir (`u` = unsigned), `local_28` ise stack'teki bir yerel değişkendir (`rbp-0x28` gibi bir konumdan gelir). Bunlar aracın tahminidir; stripped binary'de kaynaktaki gerçek isimler zaten yoktur.

📌 **Sık yapılan hata:** Bu adları programın orijinal değişken isimleri sanmak. Onlar Ghidra'nın uydurmasıdır; anlamını çözünce sen yeniden adlandırırsın.

🔗 [01 §1 Disassembler vs decompiler](01-ghidra-giris.md)

### Soru 3 — Ghidra'da bir string'i kullanan fonksiyonu nasıl bulursun?

**Kısa cevap:** **String'e xref alarak**: string'e sağ tık → References → Show References to → onu kullanan fonksiyona atla.

**Ayrıntı:** Defined Strings'te ilgi çekici bir string bulursun ("Correct", "Wrong", "flag{"), üzerine sağ tıklayıp "Show References to" dersin. Xref (cross-reference) o string'e nereden atıf yapıldığını gösterir; listeden çift tıklayıp onu kullanan koda/fonksiyona atlarsın. Bu, ilk oturum reflekslerinin dördüncü adımıdır.

📌 **Sık yapılan hata:** `main`'den başlayıp satır satır aramak. String→xref seni doğrudan hedefe götürürken bütün kodu okumak zaman kaybıdır.

🔗 [01 §5 İlk oturum refleksleri](01-ghidra-giris.md)

### Soru 4 — Statik analiz hangi durumda gerçek mantığı gösteremez?

**Kısa cevap:** **Kod runtime'da üretiliyor/açılıyorsa** — packer ile paketlenmiş ya da şifrelenmiş binary'lerde statik görünüm gerçek mantığı vermez.

**Ayrıntı:** Statik analiz byte'lara bakar; ama bazı şeyler ancak çalışınca belli olur: packer/şifre ile gizlenmiş kod runtime'da açılır, runtime'da hesaplanan değerler ve kullanıcı girdisine bağlı yollar, anti-analiz numaraları. UPX ile paketlenmiş bir binary'de Ghidra anlamlı kod göstermez çünkü gerçek kod çalışırken açılır. Bu durumda dinamik analiz şarttır.

📌 **Sık yapılan hata:** "Ghidra'da kodu gördüm, çözdüm" demek. Paketlenmiş/şifreli binary'de statik görünüm yetersizdir; statik + dinamik birlikte kullanılır.

🔗 [01 §4 Statik analizin gücü ve sınırı](01-ghidra-giris.md)

### Soru 5 — Listing ve Decompiler panelleri arasındaki ilişki nedir?

**Kısa cevap:** **Senkronizedirler**: aynı kodun iki görünümü — Listing kesin assembly, Decompiler okunur C — ve biri seçilince diğeri karşılık gelen satırı vurgular.

**Ayrıntı:** Listing adres + assembly'yi (kesin gerçek), Decompiler C benzeri kodu (hızlı okuma) gösterir. İkisi senkrondur: Listing'de bir satıra tıklarsan Decompiler o C satırını vurgular ve tersi. İkisi ayrı dosyalar değil, aynı fonksiyonun iki eşzamanlı penceresidir; aralarında gidip gelmek en temel refleksindir.

📌 **Sık yapılan hata:** İki paneli ayrı/ilgisiz iki dosya sanmak. Aynı kodu iki farklı görünümle verirler ve birbirine bağlıdır.

🔗 [01 §3 CodeBrowser penceresinin haritası](01-ghidra-giris.md)

---

## 02 Decompile okuma, tip/isim düzeltme, veri akışı

### Soru 1 — `param_2` hangi register'a ve kaçıncı argümana karşılık gelir?

**Kısa cevap:** **`rsi`'ye ve 2. argümana.** `param_N` isimlendirmesi ABI register sırasına göredir.

**Ayrıntı:** Ghidra argümanları çağrı sırasına göre adlandırır: `param_1` = rdi (1. argüman), `param_2` = rsi (2. argüman) ve devam eder. İsim uydurmadır ama sıra gerçektir — Ghidra bu eşlemeyi ABI'den yapar.

📌 **Sık yapılan hata:** `param_N` numaralarını rastgele/anlamsız sanmak. Numara ABI'deki argüman sırasını verir; `param_2` kesinlikle 2. argüman = rsi'dir.

🔗 [02 §1 Ghidra'nın uydurma isimlerini çözmek](02-decompile-okuma.md)

### Soru 2 — `undefined8` gerçek bir C tipi midir? Ne yaparsın?

**Kısa cevap:** **Hayır**, gerçek bir C tipi değildir — Ghidra'nın "bilmediği 8 byte" için koyduğu yer tutucudur. **Doğru tipi verirsin** (retype).

**Ayrıntı:** `undefined8` (8 byte) ve `undefined4` gibi tipler Ghidra'nın tip tahmini yapamadığı yerlerdir. Değişkenin rolünü çözer, uygun tipi verirsin: string tutuyorsa `char *`, sayaç/uzunluksa `int`/`size_t`, struct pointer ise uygun struct tipi. Değişkene sağ tık → Retype Variable (`Ctrl+L`). Doğru tip verince Ghidra ifadeleri okunur hale getirir (örn. `*(char *)(uVar1+1)` → `uVar1[1]`).

📌 **Sık yapılan hata:** `undefined8`'i olduğu gibi bırakıp çirkin çıktıda boğulmak. Tip düzeltmek okunabilirliğin anahtarıdır.

🔗 [02 §2 Tip düzeltme — okunabilirliğin anahtarı](02-decompile-okuma.md)

### Soru 3 — Bir crackme'de "beklenen değer"i decompile'da nerede ararsın?

**Kısa cevap:** **Karşılaştırmanın diğer tarafında** — girdi→dönüşüm→karşılaştırma zincirinde, karşılaştırmanın girdi olmayan tarafında.

**Ayrıntı:** RE'nin kalıbı girdi → dönüşüm(ler) → karşılaştırmadır. Kullanıcı girdisi (`scanf`/`read`/`fgets`) bir değişkene gider, o değişken dönüşümlerden geçip bir karşılaştırmaya sokulur. Beklenen değer, karşılaştırmanın **diğer** (girdi olmayan) tarafındadır — ya doğrudan bir sabit (`if (iVar1 == 0x1a4)` → 420) ya da hesaplanmış bir değerdir.

📌 **Sık yapılan hata:** Karşılaştırmanın girdi tarafına bakmak. Beklenen değer daima karşı taraftadır; girdi taraf senin kontrol ettiğindir.

🔗 [02 §3 Veri akışını takip etmek](02-decompile-okuma.md)

### Soru 4 — `CONCAT44` gibi bir ifade gördüğünde ne düşünürsün?

**Kısa cevap:** **Ghidra'nın tip artığıdır** — byte birleştirme/kesme yardımcısı, programın gerçek fonksiyonu değil.

**Ayrıntı:** `CONCAT44`, `SUB84` gibi tuhaf fonksiyonlar Ghidra'nın **tip belirsizliğinden** doğar; byte'ları birleştirir/keser. Programın çağırdığı gerçek fonksiyonlar değildir. Genelde ilgili değişkene doğru tipi verince kaybolurlar.

📌 **Sık yapılan hata:** Bunları programın gerçek bir çağrısı/fonksiyonu sanmak. Tip artığıdır; doğru tiple yok olur.

🔗 [02 §4 Yaygın decompile kalıplarını tanımak](02-decompile-okuma.md)

### Soru 5 — Rename işlemi programın çalışmasını değiştirir mi?

**Kısa cevap:** **Hayır.** Rename sadece ismi her yerde günceller; mantığı ve programın çalışmasını değiştirmez.

**Ayrıntı:** Bir değişkene/fonksiyona `L` (veya sağ tık → Rename) ile yeni isim verdiğinde Ghidra bu ismi tüm çıktıda otomatik günceller — bu tamamen bir okunabilirlik işlemidir. Kodu bozmaz, çalışmayı etkilemez; sadece senin anlamanı kolaylaştırır. Verdiğin isimler (ve tipler, yorumlar) proje halinde kaydedilir, ertesi gün ilerlemen durur.

📌 **Sık yapılan hata:** Yeniden adlandırmanın binary'yi ya da mantığı değiştireceğinden çekinmek. Rename salt görünümü etkiler; programı değil, senin okumanı değiştirir.

🔗 [02 §1 Ghidra'nın uydurma isimlerini çözmek](02-decompile-okuma.md)

---

## 03 String, xref, sabit avı ile hızlı yön bulma

### Soru 1 — "References to" ile "References from" arasındaki fark nedir?

**Kısa cevap:** **"References to" = bu öğeyi kim kullanıyor** (buraya kim atıf yapıyor); **"References from" = bu öğe nereye/neye başvuruyor** (buradan nereye).

**Ayrıntı:** İki ters yön: "References to" bir string/adres/fonksiyona **nereden** referans verildiğini gösterir — örn. "Correct!" string'ini kim yazdırıyor → doğrulama fonksiyonu. "References from" ise bir fonksiyonun hangi başka fonksiyon/verileri kullandığını gösterir. Doğrulama noktasına gitmek için genelde "References to" kullanırsın.

📌 **Sık yapılan hata:** İki yönü karıştırmak. "to" seni öğeyi kullanan yere götürür; "from" tersidir.

🔗 [03 §2 Xref (cross-reference)](03-string-xref.md)

### Soru 2 — `strings` bir parolayı bulamıyorsa bu ne anlama gelebilir?

**Kısa cevap:** **String şifreli veya runtime'da byte byte kuruluyordur**; statik metin taraması onu yakalayamaz.

**Ayrıntı:** Düz `strings` sadece binary'ye gömülü hazır metinleri görür. Parola görünmüyorsa çalışma zamanında üretiliyor olabilir: `'C','o','r','r'...` gibi tek tek byte atamaları (`mov byte [rbp-x], 'A'`) ya da XOR'lu veri. Bu durumda string statikte yoktur, dinamik analizde (program çalışırken) yakalanır. "Yok" demek yanlış olabilir.

📌 **Sık yapılan hata:** `strings` bulamayınca "parola yok" sonucuna varmak. Şifreli/parçalı string'ler runtime'da kurulur; dinamikte ortaya çıkar.

🔗 [03 §1 String avı — en ucuz ipucu](03-string-xref.md)

### Soru 3 — `0x9e3779b9` sabiti sana ne ima eder?

**Kısa cevap:** **Golden ratio sabiti** — TEA/XXTEA gibi bir şifreleme algoritması kullanılıyor olabilir.

**Ayrıntı:** Programcılar tanınabilir sabitler (magic number) kullanır ve bunlar algoritmayı ele verir. `0x9e3779b9` golden ratio'dur ve TEA/XXTEA şifrelemesinde geçer — rastgele bir sayı değildir. Bir sabiti tanımazsan Google'da "0x... constant" diye ararsın; çoğu bilinen algoritmanın parmak izi vardır.

📌 **Sık yapılan hata:** Böyle bir sabiti rastgele/anlamsız sanmak. "Tuhaf, büyük, tekrarlayan" sabitler algoritma parmak izidir; `0x9e3779b9` TEA'ya işaret eder.

🔗 [03 §3 Sabit (magic number) avı](03-string-xref.md)

### Soru 4 — Imports'ta `strcmp` görmek neden faydalı bir ipucudur?

**Kısa cevap:** **String karşılaştırması yapıldığını gösterir** — genelde bir parola/anahtar kontrolü. `strcmp`'e xref alıp tüm kontrolleri bir çırpıda bulabilirsin.

**Ayrıntı:** Bir binary'nin import ettiği fonksiyonlar ne yaptığını ele verir. `strcmp`/`strncmp` string karşılaştırma → parola kontrolü demektir (`memcmp` bayt karşılaştırma → key/hash doğrulama). Symbol Tree → Imports'tan `strcmp`'i bulup ona xref alarak programdaki tüm parola kontrol noktalarına doğrudan atlarsın.

📌 **Sık yapılan hata:** Import listesini programın kendi fonksiyonları sanmak. Bunlar dışarıdan kullanılan API'lerdir; hangi işin yapıldığını (parola, dosya, anti-debug) ele verirler.

🔗 [03 §4 Import/çağrı avı](03-string-xref.md)

### Soru 5 — Yeni bir crackme'de neden `main`'i baştan okumak yerine string→xref kullanırsın?

**Kısa cevap:** **İlginç kod küçüktür**; string→xref seni 30 saniyede doğrulama noktasına götürürken `main`'i satır satır okumak zaman kaybıdır.

**Ayrıntı:** Kod büyüktür ama işin döndüğü kısım küçüktür. String'ler, sabitler ve xref'ler seni doğrudan o küçük kısma götüren işaret levhalarıdır. Örneğin "Correct!" string'ine xref alıp onu yazdıran doğrulama fonksiyonuna, oradan hemen üstündeki karşılaştırmaya ve beklenen değere gidersin. RE'de hız, nereye *bakmayacağını* bilmektir.

📌 **Sık yapılan hata:** `main`'den başlayıp satır satır ilerlemek. String→xref kestirmesi varken tüm kodu okumak gereksizdir; "her şeyi oku" yerine "doğru yere bak".

🔗 [03 §5 Hepsini birleştiren yön bulma stratejisi](03-string-xref.md)
