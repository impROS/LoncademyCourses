# 00 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 00 Sınav künyesi, formatı ve trickler

### Soru 1 — Şıklarda `Compilation fails` gördüğünde ilk yapman gereken ne?

**Kısa cevap:** **Önce kodun derlenip derlenmediğini kontrol et, çıktıyı sonra düşün.**

**Ayrıntı:** Bu sınavda soru "ne yazdırır" gibi görünür ama gerçekte "bu kod
derleniyor mu" diye sorar; `Compilation fails` şıkkı sık sık doğru cevaptır.
Bu yüzden refleks 1: gözün önce derleme hatası arasın — imzalara (tipler,
`final`, `static`, erişim belirteçleri) bak, çünkü hata genelde orada saklıdır.
Kod derleniyorsa ancak o zaman çıktıyı izlemeye geç. Not: karmaşık kod ≠ hatalı
kod; sadece **gerçek bir kural ihlali** bulursan bu şıkkı seç.

📌 **Sık yapılan hata:** Doğrudan çıktıyı hesaplamaya girişip derleme hatasını
kaçırmak; kodu karmaşık görünce panikleyip hatasız kodu "derlenmez" işaretlemek.

🔗 [00 §5 Kod okuma tekniği](00-sinav-kunyesi.md)

### Soru 2 — `(Choose TWO.)` sorusunda bir doğru bir yanlış işaretlersen kaç puan alırsın?

**Kısa cevap:** **0 puan.** Kısmi puan yoktur.

**Ayrıntı:** Bu sınavda kısmi puan yok; "Choose TWO"da iki şıkkın da doğru
olması gerekir. Bir doğru bir yanlış işaretlemek soruyu tamamen kaybettirir.
Negatif puan da yoktur, o yüzden boş bırakmak da yanlışla aynıdır — her soruyu
işaretle. Çoklu seçimlerde önce emin olduğun bir doğruyu bul, ikinciyi ona göre
ara.

📌 **Sık yapılan hata:** Emin olduğun tek doğruyla yetinip ikinci şıkkı rastgele
seçmek — yanlış ikinci şık tüm puanı sıfırlar.

🔗 [00 §1 Sınav formatı (kısmi puan yok)](00-sinav-kunyesi.md)

### Soru 3 — `String s = "a"; s.concat("b");` sonrası `s` nedir? Neden?

**Kısa cevap:** **Hâlâ `"a"`.** String immutable'dır ve dönüş değeri atanmamıştır.

**Ayrıntı:** `concat` mevcut String'i değiştirmez, yeni bir String (`"ab"`)
döndürür; ama bu dönüş değeri bir değişkene atanmadığı için kaybolur. `s` ilk
gösterdiği `"a"`'yı göstermeye devam eder. Kural (Kalıp E): immutable bir tipte
metot çağırıp dönüş değerini atamazsan o satır etkisizdir — String, tüm
Date-Time tipleri, wrapper'lar ve `List.of()` hepsi immutable'dır.

📌 **Sık yapılan hata:** String metotlarının nesneyi yerinde değiştirdiğini
sanıp `s`'yi `"ab"` beklemek.

🔗 [00 §2 Kalıp E — değişti mi değişmedi mi](00-sinav-kunyesi.md)

### Soru 4 — Sınavda kaç doğru cevapla geçersin ve bu yüzde kaça denk gelir?

**Kısa cevap:** **34 doğru → %68.** (⚠️ resmî sayfadan teyit edilmeli.)

**Ayrıntı:** 50 soru üzerinden geçme notu %68'dir ve bu 34 doğru cevaba karşılık
gelir; süre 120 dakika (soru başına ~2.4 dakika). Bu değerler kurs boyunca
`⚠️ Doğrulanmalı` işaretlidir — Oracle'ın resmî sınav sayfasından teyit
edilmelidir. Negatif puan olmadığı için hedef, hiçbir soruyu boş bırakmadan
en az 34 doğruya ulaşmaktır.

📌 **Sık yapılan hata:** Bu sayıları kesin bilgi sayıp resmî sayfadan teyit
etmemek; sınav parametreleri sürümle değişebilir.

🔗 [00 §1 Sınav formatı (geçme notu)](00-sinav-kunyesi.md)

### Soru 5 — Java 17'den 21'e eklenen ve sınavda kesin sorulacak dört konu hangileri?

**Kısa cevap:** **Virtual threads, sequenced collections, record patterns ve
pattern matching for switch.**

**Ayrıntı:** Sınavın "yeni" tarafı ve hazırlananların en zayıf olduğu yer bu
dört konudur: virtual threads (8.1), sequenced collections (5.3), record
patterns ve pattern matching for `switch` (2.1, 3.3). Alan ağırlıkları resmî
yayımlanmadığından bunlara ekstra dikkat et — kesinlikle çıkarlar.

📌 **Sık yapılan hata:** Yıllardır Java yazıyor olmaya güvenip 17→21 arası
eklenen bu yenilikleri atlamak; sınav tam da bu güncel konuları kovalar.

🔗 [00 §4 Java 17→21 yenilikleri](00-sinav-kunyesi.md)

## 01 Ortam kurulumu ve çalışma projesi

### Soru 1 — `java -version` 23 gösteriyorsa ne yaparsın, hangi iki satırı yazarsın?

**Kısa cevap:** **JDK 21'i aktif et:** `JAVA_HOME`'u 21'e ayarla, sonra `PATH`'e
ekle.

**Ayrıntı:** Sınav Java SE 21 üzerinden sorulur; 22/23/24 ile çalışırsan yanlış
davranış öğrenirsin. Önce `/usr/libexec/java_home -V` ile 21 sürümlü JDK'nın
yolunu bul, sonra terminalinde şu iki satırı yaz:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```
Kalıcı olması için bunları `~/.zshrc` sonuna ekle. Sonra `java -version`
çıktısında `21.x` görmelisin.

📌 **Sık yapılan hata:** "En yeni JDK daha iyidir" diye 23'te kalmak;
`mvn -version`'ın da 21 gösterdiğini varsaymak — Maven kendi `JAVA_HOME`'unu
kullanır.

🔗 [01 §1 JDK 21 doğrulaması](01-kurulum.md)

### Soru 2 — Bir kodun derlenip derlenmediğini test etmek için hangi komutu kullanırsın, neden jshell olmaz?

**Kısa cevap:** **`javac Dosya.java`** kullan. jshell derlenirlik testi için
uygun değildir çünkü kuralları gevşetir.

**Ayrıntı:** `javac` kaynağı gerçek derleyiciyle derler ve derleme hatalarını
aynen gösterir. `jshell` ise bazı kuralları gevşetir: `main` metodu istemez ve
checked exception'ları otomatik sarar. Bu yüzden jshell'de "çalıştı" demek
kodun gerçekten derlendiği anlamına gelmez. Araç bölüşümü: ifade değeri için
`jshell`, derlenirlik için `javac`, program çıktısı için `java Dosya.java`.

📌 **Sık yapılan hata:** jshell'de bir ifadenin sorunsuz koşmasını "derlenir"
kanıtı saymak; sınavda checked exception veya `main` zorunluluğu yüzünden aynı
kod derlenmeyebilir.

🔗 [01 §4 jshell vs javac](01-kurulum.md)

### Soru 3 — `byte b = 10; b = b + 300;` neden derlenmez ama `b += 300;` neden derlenir?

**Kısa cevap:** **`b + 300` bir `int`'tir** ve `byte`'a cast'siz atanamaz;
`b += 300` ise compound assignment'ın **gizli cast**'i sayesinde derlenir.

**Ayrıntı:** İkili aritmetikte `byte` operand `int`'e terfi eder (numeric
promotion), yani `b + 300` `int` üretir ve onu `byte b`'ye cast'siz atamak
"possible lossy conversion from int to byte" derleme hatası verir. `b += 300`
ise eşdeğeri `b = (byte)(b + 300)` — compound operatör örtük cast ekler, o yüzden
derlenir (ve taşarsa sessizce sararak `54` yazdırır). Sınavın en sevdiği
tuzaklardan biridir.

📌 **Sık yapılan hata:** `b = b + 300` ile `b += 300`'ü eşdeğer sanmak; `+=`
gizli bir cast içerir, düz atama içermez.

🔗 [01 §3 Derleme hatasını görmeyi öğren](01-kurulum.md)

### Soru 4 — IDE'nin sınava hazırlıkta üç zararı ne?

**Kısa cevap:** **Hatayı senin yerine bulur, import'ları otomatik ekler, API
adını otomatik tamamlar.**

**Ayrıntı:** IDE kırmızı altı çizgiyle derleme hatasını gösterir — ama sınavda
hata gösterilmez, sen bulacaksın. `import`'ları otomatik ekler — sınavda eksik
import bir cevap şıkkıdır. Otomatik tamamlama API adını hatırlatır — sınavda API
adlarını ezbere bileceksin (var olmayan metot = derleme hatası). Bu yüzden konu
dosyalarını okurken IDE, örnekleri çalıştırırken terminal kullan; tahminini
yazmadan önce IDE uyarılarına bakma.

📌 **Sık yapılan hata:** IDE'nin yeşil ışığına güvenip kendi derleme-hatası
sezgisini geliştirmemek; sınavda o yardım olmayınca hazırlıksız kalmak.

🔗 [01 §5 IDE kullanacaksan](01-kurulum.md)

## 02 Kayıt, satın alma ve sınav günü

### Soru 1 — Voucher'ı hangi koşul sağlandıktan sonra alacaksın?

**Kısa cevap:** **8. haftada, deneme sınavında %70+ (hedef %75-80) aldıktan
sonra**, sadece Oracle'dan.

**Ayrıntı:** Hafta 1-7 boyunca hiçbir şey satın alma, sadece çalış. Hafta 8
başında `99-final/deneme-1.html` çöz; %70+ aldıysan Enthuware mock testini al ve
2-3 deneme çöz. Denemelerde %70+ (ideali %75-80) tutuyorsan voucher'ı al ve
randevunu 3-5 gün sonrasına kur. Voucher iadesi olmadığı için erken alım sadece
parayı riske atar, hazırlığı hızlandırmaz.

📌 **Sık yapılan hata:** Denemede %65 alıp "sınavda toparlarım" demek; gerçek
sınav denemelerden daha zordur (ilk görüş + süre baskısı).

🔗 [02 §1 Ne zaman satın alınır](02-kayit-ve-satin-alma.md)

### Soru 2 — Dumps kullanmanın iki somut riski ne?

**Kısa cevap:** **(1) Sertifika iptali ve tekrar sınava girme yasağı, (2) eski
sınav sürümüne ait yanlış içerik.**

**Ayrıntı:** Exam dumps siteleri ("gerçek sorular" iddiasıyla) Oracle politika
ihlalidir; tespit edilirse sertifikan iptal edilir ve tekrar sınava girmen
yasaklanır. Ayrıca bu içerikler çoğunlukla sınavın eski sürümüne aittir ve
yanlıştır — yani hem risklidir hem de öğrettiği şey hatalıdır. Meşru ve yeterli
yol: Oracle voucher + Enthuware mock testleri.

📌 **Sık yapılan hata:** Dumps'ı "kısa yol" sanmak; hem sertifikayı hem de doğru
bilgiyi kaybettirir.

🔗 [02 §2 Buradan ALMA — dumps](02-kayit-ve-satin-alma.md)

### Soru 3 — Online sınavda randevudan önce yapılması gereken teknik adım ne?

**Kısa cevap:** **Pearson VUE sistem testini randevudan en az bir gün önce
çalıştırmak** (kamera, mikrofon, ekran paylaşımı kontrolü).

**Ayrıntı:** Online proctored sınavda sistem kontrolü önceden yapılmalıdır;
randevudan en az bir gün önce Pearson VUE sistem testini çalıştır. Sınav
başladıktan sonra internet/kamera sorunu çıkarsa süre işlemeye devam eder ve
sınav iptal olabilir. Ayrıca oda tamamen boş, tek başına olmalısın; kâğıt/kalem
yasak, yerine dijital beyaz tahta verilir.

📌 **Sık yapılan hata:** Sistem testini sınav gününe bırakmak; son anda çıkan
teknik sorun süreyi ve randevuyu yakar.

🔗 [02 §3 Online proctored (sistem testi)](02-kayit-ve-satin-alma.md)

### Soru 4 — Kimlikteki isim farklıysa ne olur?

**Kısa cevap:** **Sınava alınmazsın ve ücret yanar.**

**Ayrıntı:** Kimlikteki isim, Oracle hesabındaki isimle birebir aynı olmalıdır;
farklıysa sınava alınmazsın ve ödediğin ücret iade edilmez. Kimlik devlet
tarafından verilmiş, fotoğraflı ve Latin harfli olmalı (pasaport en güvenlisi).
Bu yüzden kaydı yaparken adını kimliğindeki gibi yaz.

📌 **Sık yapılan hata:** Oracle hesabını kimliktekinden farklı bir isim/yazımla
oluşturmak; küçük bir farklılık bile girişi engeller.

🔗 [02 §4 Sınav günü kuralları — kimlik](02-kayit-ve-satin-alma.md)

### Soru 5 — Kaldığın sınava en erken ne zaman tekrar girebilirsin?

**Kısa cevap:** **14 gün sonra** (⚠️ politikayı teyit et).

**Ayrıntı:** Aynı sınava tekrar girmek için 14 gün beklemen gerekir ve yeni bir
voucher alman gerekir — ilk ödeme iade edilmez. Sonuç raporu alan bazlı
performansını gösterir; bunu sakla, çünkü hangi konulara döneceğini söyler.
Strateji: zayıf alanların konu dosyalarına dön, testlerini %90 olana kadar çöz,
sonra tekrar dene.

📌 **Sık yapılan hata:** Kalınca hemen tekrar denemeye çalışmak ya da sonuç
raporunu atmak; rapor, ikinci denemenin yol haritasıdır.

🔗 [02 §5 Kaldıysan ne olur](02-kayit-ve-satin-alma.md)
