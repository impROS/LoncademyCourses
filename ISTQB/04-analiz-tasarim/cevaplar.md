# 04 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 4.1 Test tekniklerine genel bakış

### Soru 1 — Üç teknik ailesini yaz ve her birinin dayandığı kaynağı belirt.

**Kısa cevap:** **Black-box → spesifikasyon (belirtilmiş davranış) · White-box → iç
yapı/kod · Experience-based → testerın bilgi ve deneyimi.**

**Ayrıntı:** Aileler **dayandıkları kaynağa** göre ayrılır. Black-box (specification-based)
test nesnesinin belirtilmiş davranışına bakar, iç yapıya bakmaz; kod bilgisi gerekmez ve
kod yazılmadan uygulanabilir. White-box (structure-based) kodun iç yapısına — satır, dal,
akış — bakar, **kod bilgisi gerektirir**. Experience-based ise testerın sezgisine dayanır
ve her zaman uygulanabilir. Ayrıca 4.5'te dördüncü bir başlık var: **collaboration-based**,
ama o kusur bulmaz, **önler**.

📌 **Sık yapılan hata:** Black-box'ı "kara kutu" diye kodla, white-box'ı spesifikasyonla
karıştırmak. Kaynağı hatırla: black = spesifikasyon, white = kod.

🔗 [4.1 §1 Üç teknik ailesi](4.1-teknikler-genel.md)

### Soru 2 — %100 branch coverage'ın bulamayacağı bir kusur tipi yaz.

**Kısa cevap:** **Spesifikasyonda tanımlı ama kodda hiç yazılmamış bir özellik**
(eksik/uygulanmamış gereksinim).

**Ayrıntı:** White-box kapsamı yalnızca **var olan kodu** ölçer. Olmayan kodun kapsamı
ölçülemez; dolayısıyla %100 branch coverage bile "bu özellik hiç yazılmamış" kusurunu
göremez. Bu, beyaz kutunun en büyük **kör noktasıdır** ve tam bu yüzden black-box ile
white-box birbirinin alternatifi değil, **tamamlayıcısıdır**.

📌 **Sık yapılan hata:** "%100 kapsam = her kusur bulunur" sanmak. Kapsam yalnızca yazılanı
ölçer; eksik özelliği göremez.

🔗 [4.1 §1 Beyaz kutunun kör noktası (tuzak)](4.1-teknikler-genel.md)

### Soru 3 — Deneyim tabanlı teknikler neden tek başına yeterli değildir?

**Kısa cevap:** Çünkü **kapsamları ölçülemez** ve sonuçları **tekrarlanabilir değildir**
(sistematik değil, testere bağlı).

**Ayrıntı:** Black-box ve white-box teknikleri sayısal kapsam üretir (partition, sınır, dal
sayısı); deneyim tabanlıda böyle bir ölçü yoktur — kaç şeyin test edildiğini yüzdeyle
söyleyemezsin ve aynı testerın iki oturumu aynı sonucu vermez. Bu yüzden bu teknikler
sistematik tekniklerin **yerine değil, yanında** kullanılır. "Exploratory testing can
replace systematic techniques" ifadesi **yanlıştır**.

📌 **Sık yapılan hata:** Deneyim tabanlıyı "gereksiz" ya da "tek başına yeterli" uçlarından
biriyle okumak. Değerlidir ama **tamamlayıcıdır**.

🔗 [4.1 §1 Experience-based (tuzak)](4.1-teknikler-genel.md)

### Soru 4 — Teknik seçimini etkileyen beş faktör say.

**Kısa cevap:** **Regülasyon/yasal standart · risk seviyesi ve tipi · tester bilgi ve
becerisi · mevcut dokümantasyon · zaman ve bütçe** (ayrıca sistem tipi/karmaşıklığı,
sözleşme, araçlar, yaşam döngüsü, beklenen kusur tipi).

**Ayrıntı:** Sınav "hangi tekniği seçersin" değil, "**seçimi ne belirler**" diye sorar.
Faktörler bir arada işler: regülasyon belirli bir coverage seviyesi zorunlu kılabilir,
yüksek risk daha güçlü teknik ister, kod bilmeyen ekip white-box uygulayamaz, spesifikasyon
yoksa black-box zorlaşır (keşfe kayar), araç yoksa branch coverage ölçülemez.

📌 **Sık yapılan hata:** Faktörleri "testerın keyfi" sanmak. Seçim bağlama bağlıdır ve
**birden fazla teknik birlikte** kullanılır — tek teknik yetmez.

🔗 [4.1 §2 Teknik seçimini etkileyen faktörler](4.1-teknikler-genel.md)

### Soru 5 — Kapsam formülünü yaz ve bir örnekle doldur.

**Kısa cevap:** **Kapsam % = (uygulanan coverage item / toplam coverage item) × 100.**

**Ayrıntı:** Coverage item tekniğe göre değişir (eşdeğerlik sınıfı, sınır değer, karar
tablosu kuralı, kod satırı, dal). Örnek: 5 eşdeğerlik sınıfından 4'ünü test ettiysen EP
kapsamı = (4/5) × 100 = **%80**. Kritik nokta: "%100 kapsam" tek başına anlamsızdır —
**hangi coverage item'a göre** olduğu belirtilmelidir (%100 statement mı, %100 branch mi,
%100 partition mı?).

📌 **Sık yapılan hata:** "%100 kapsam" demeyi yeterli sanmak. Coverage item tipi
söylenmeden ifade eksiktir; sınav bunu ayırt etmeni bekler.

🔗 [4.1 §3 Test condition ve coverage](4.1-teknikler-genel.md)

## 4.2a Eşdeğerlik Bölümleme ve Sınır Değer Analizi ⭐

### Soru 1 — "Miktar 5 ile 50 arasında olmalı" için bölümleri, sınırları, 2-value ve 3-value BVA değerlerini yaz.

**Kısa cevap:** **3 bölüm** (<5 invalid, 5–50 valid, >50 invalid), **sınırlar 5 ve 50**,
**2-value: 4, 5, 50, 51**, **3-value: 4, 5, 6, 49, 50, 51**.

**Ayrıntı:** "5 ile 50 arasında" → sınırlar **dahil** (between). Bölümler: `≤4 (invalid)` |
`5–50 (valid)` | `≥51 (invalid)`. Sınırlar geçerli bölümün kenarları: **5** ve **50**.
2-value = her sınır + komşu bölümdeki en yakın değer: sınır 5 → {4, 5}, sınır 50 → {50, 51}
→ **4, 5, 50, 51**. 3-value = sınır−1, sınır, sınır+1: sınır 5 → {4, 5, 6}, sınır 50 →
{49, 50, 51} → **4, 5, 6, 49, 50, 51** (çakışma yok, 6 benzersiz değer).

📌 **Sık yapılan hata:** Sınırın karşı tarafındaki komşuyu unutup 2-value'yu sadece {5, 50}
sanmak. 2-value sınır başına **iki** değer alır — sınır + öbür taraftaki komşu.

🔗 [4.2a §2 İki varyant](4.2a-ep-bva.md)

### Soru 2 — Neden geçersiz bölümler teker teker test edilir?

**Kısa cevap:** **Fault masking** — bir test case'de iki geçersiz değer birleşirse,
ilki hatayı tetikleyip diğerinin etkisini **maskeleyebilir**.

**Ayrıntı:** İlk geçersiz girdi bir hata mesajı/red üretirse, sistem ikinci geçersiz girdiyi
hiç değerlendirmeyebilir; o geçersiz bölümün gerçekten reddedilip reddedilmediğini
göremezsin. Bu yüzden her geçersiz bölüm **ayrı** test case ile denenir. Buna karşılık
**geçerli** bölümler bir testte birleştirilebilir — maskeleme riski yoktur.

📌 **Sık yapılan hata:** Bütün geçersiz değerleri tek testte toplayıp test sayısını
azaltmaya çalışmak. Geçerlileri birleştir, geçersizleri **ayır**.

🔗 [4.2a §1 Kritik kural (fault masking)](4.2a-ep-bva.md)

### Soru 3 — BVA hangi ön koşulu gerektirir? Uygulanamayacağı bir örnek ver.

**Kısa cevap:** Bölüm **sıralanabilir (ordered)** olmalı — sayı, tarih, saat. Örnek:
**"Renk = kırmızı/yeşil/mavi"** gibi sıralanamayan bir bölümde BVA **uygulanamaz**.

**Ayrıntı:** BVA "kenar" kavramına dayanır; bir bölümün en küçük ve en büyük değeri olması
gerekir. Değerler sıralanamıyorsa (kategorik: renk, şehir, ödeme tipi) "sınır" diye bir şey
yoktur, dolayısıyla BVA anlamsızdır. Böyle bir girdi için EP uygundur ama BVA değil.

📌 **Sık yapılan hata:** Her girdiye BVA uygulanabileceğini sanmak. Kategorik/sıralanamayan
girdilerde yalnızca EP çalışır.

🔗 [4.2a §2 BVA ön koşulu](4.2a-ep-bva.md)

### Soru 4 — "Tutar 100'den büyük, 500'den küçük olmalı" — geçerli bölümün sınırları nedir?

**Kısa cevap:** **101 ve 499** (100 ve 500 değil!).

**Ayrıntı:** "büyük" ve "küçük" → sınırlar **dahil değil** (greater than / less than). Tam
sayı varsayarsak geçerli bölüm **101 ≤ tutar ≤ 499** olur; kenarlar 101 ve 499'dur. Eğer
ifade "100 ile 500 arasında" (between) olsaydı sınırlar 100 ve 500 olurdu. Farkı belirleyen
tek şey ifadenin kelimeleridir.

📌 **Sık yapılan hata:** 100 ve 500'ü sınır sanmak. "greater than / less than" sınırı
dışarıda bırakır; ifadeyi **kelime kelime oku**.

🔗 [4.2a §4 Örnek C (dahil olmayan sınır)](4.2a-ep-bva.md)

### Soru 5 — 0–5 ve 6–20 bitişik bölümlerinde 2-value BVA kaç benzersiz değer üretir?

**Kısa cevap:** **6 benzersiz değer: −1, 0, 5, 6, 20, 21.**

**Ayrıntı:** Sınırlar: 0, 5 (ilk bölüm) ve 6, 20 (ikinci bölüm). 2-value her sınır + komşu:
0 → {−1, 0}, 5 → {5, 6}, 6 → {5, 6}, 20 → {20, 21}. Bitişik bölümlerde **5 ve 6 birbirinin
komşusu** olduğu için 5→{5,6} ile 6→{5,6} **aynı çifti** verir — çakışır. Benzersizleri
say: −1, 0, 5, 6, 20, 21 → **6 değer**. Kör körüne 4 sınır × 2 = 8 dersen fazla sayarsın.

📌 **Sık yapılan hata:** Bitişik bölümlerde sınır değerlerinin çakıştığını görmeyip fazladan
saymak. Benzersiz değerleri listele, çarpma yapma.

🔗 [4.2a §4 Örnek B (bitişik bölümler)](4.2a-ep-bva.md)

## 4.2b Karar Tablosu ve Durum Geçiş Testi ⭐

### Soru 1 — 5 bağımsız Boolean koşul için tam karar tablosunda kaç kural vardır?

**Kısa cevap:** **32 kural (2⁵).**

**Ayrıntı:** n adet bağımsız Boolean (T/F) koşulun tam tablosu **2ⁿ** kural içerir; her kural
koşulların bir T/F kombinasyonudur. 5 koşul → 2⁵ = **32**. Hafıza: 2→4, 3→8, 4→16, 5→32.
Bu, **tam (full)** tablo sayısıdır; collapse edilirse daha az olabilir.

📌 **Sık yapılan hata:** 5 × 2 = 10 gibi çarpım yapmak. Kural sayısı **üstel** büyür: 2ⁿ.

🔗 [4.2b §1 Sayı kuralı](4.2b-karar-durum.md)

### Soru 2 — Bir koşul 4 değer, diğeri 3 değer alıyorsa kaç kural olur?

**Kısa cevap:** **12 kural (4 × 3).**

**Ayrıntı:** Koşullar Boolean değilse 2ⁿ formülü geçmez; her koşulun **değer sayısını
çarparsın**. 4 değerli bir koşul × 3 değerli bir koşul = **12** kombinasyon = 12 kural.
2ⁿ yalnızca tüm koşullar iki değerli (T/F) olduğunda geçerlidir.

📌 **Sık yapılan hata:** Her koşulu iki değerli varsayıp 2² = 4 demek. Değer sayısını
say ve çarp: 4 × 3 = 12.

🔗 [4.2b §1 Tuzak (Boolean olmayan koşul)](4.2b-karar-durum.md)

### Soru 3 — Karar tablosunda `–` ne demektir ve ne işe yarar?

**Kısa cevap:** **Don't care** — o koşul, o kural için **sonucu etkilemez**; kuralları
birleştirip (collapse) tablo boyutunu **küçültmeye** yarar.

**Ayrıntı:** T belirli bir değerdir (koşul doğru), F yanlış; `–` ise "bu koşulun T ya da F
olması sonucu değiştirmiyor" demektir. Sonucu etkilemeyen koşullar `–` ile işaretlenip
birden fazla kural tek bir collapsed kurala indirgenir — böylece test case sayısı azalır.
Ama her tablo collapse edilemez: her koşul sonucu etkiliyorsa `–` çıkmaz.

📌 **Sık yapılan hata:** `–`'yi "F" veya "test edilmedi" sanmak. Anlamı "**fark etmez**"tir
ve kural birleştirmeyi mümkün kılar.

🔗 [4.2b §1 Gösterim sembolleri / collapsed](4.2b-karar-durum.md)

### Soru 4 — State diagram ile state table arasındaki en önemli fark nedir?

**Kısa cevap:** **Diyagram yalnızca geçerli geçişleri gösterir; state table geçersiz
(imkânsız) geçişleri de** gösterir — tüm durum-olay kombinasyonlarını.

**Ayrıntı:** State transition diagram daireler (durum) ve oklardan (geçiş) oluşur ve sadece
tanımlı, **geçerli** geçişleri çizer. State table ise satır = durum, sütun = olay olacak
şekilde **her hücreyi** doldurur; geçerli geçiş olmayan hücreler geçersiz/imkânsız geçiş
olarak görünür. Geçersiz geçişleri test etmek (all transitions coverage) istiyorsan
**tabloya** bakman gerekir — diyagram onları göstermez.

📌 **Sık yapılan hata:** İkisini eş kabul etmek. Geçersiz geçiş sorusu geldiğinde cevap
**state table**'dır, diyagram değil.

🔗 [4.2b §2 İki gösterim (kritik ayrım)](4.2b-karar-durum.md)

### Soru 5 — %100 all states coverage sağlandı. Hangi kapsam garanti değildir? Neden?

**Kısa cevap:** **%100 valid transitions (geçiş) coverage garanti değildir.** Çünkü her
durumu ziyaret etmek, her **geçişi** kullandığın anlamına gelmez.

**Ayrıntı:** İlişki tek yönlüdür: **%100 valid transitions ⟹ %100 all states** (her geçişi
koşarsan zaten her duruma uğrarsın), ama **tersi doğru değildir**. Her durumu bir kez
ziyaret eden kısa bir dizi, o durumlar arası geçişlerin çoğunu atlayabilir — örneğin 3
durumlu bir sistemde tüm durumları gezip 5 geçişin yalnızca 3'ünü (%60) kapsayabilirsin.
All states **en zayıf** kapsam seviyesidir.

📌 **Sık yapılan hata:** İki yönü de doğru sanmak. Yalnızca transitions → states yönü
garantilidir; states → transitions **değildir**.

🔗 [4.2b §2 Üç kapsam seviyesi (kritik ilişkiler)](4.2b-karar-durum.md)

## 4.3 Beyaz Kutu Test Teknikleri ⭐

### Soru 1 — Kod için %100 statement ve %100 branch coverage kaç test case gerektirir? (iki bağımsız if)

**Kısa cevap:** **Statement için 1 test, branch için 2 test.**

**Ayrıntı:** Kod: `if (a>0){x=1;}` · `if (b>0){x=x+1;}` · `return x`. **Statement:** her
satırın çalışması için tek bir test (a>0 **ve** b>0, örn. a=1, b=1) her ifadeyi çalıştırır
→ **1 test case**. **Branch:** 2 karar × 2 sonuç = 4 dal; ama bir test birden fazla dalı
kapsayabilir. T1 (a=1, b=1) iki TRUE dalını, T2 (a=0, b=0) iki FALSE dalını kapsar →
4/4 dal, **2 test case**.

📌 **Sık yapılan hata:** "4 dal var → 4 test" demek. **Dal sayısı ≠ test case sayısı**;
bir test birden çok dalı aynı anda kapsar.

🔗 [4.3 §2 Çözümlü örnek B](4.3-beyaz-kutu.md)

### Soru 2 — %100 statement coverage sağlandı. %100 branch coverage garanti mi? Neden?

**Kısa cevap:** **Hayır, garanti değildir.**

**Ayrıntı:** İlişki tek yönlüdür: **%100 branch ⟹ %100 statement** (her dalı koşarsan
içindeki satırlar da çalışır), ama **tersi geçerli değildir**. `else`'siz bir `if`'te,
koşulun TRUE olduğu tek bir test tüm satırları çalıştırıp %100 statement verir; ama `if`'in
atlandığı **FALSE dalı** hiç denenmemiş olur → branch %100 değildir. Branch daha güçlü,
statement'ı **içerir**.

📌 **Sık yapılan hata:** İki kapsamı eş sanmak veya ilişkiyi ters kurmak. Branch statement'ı
yutar; statement branch'i garanti etmez.

🔗 [4.3 §3 İki kapsamın ilişkisi](4.3-beyaz-kutu.md)

### Soru 3 — `else`'i olmayan bir `if` kaç dala sahiptir?

**Kısa cevap:** **İki dal** — TRUE dalı ve `if`'in atlandığı FALSE dalı.

**Ayrıntı:** `else` bloğu olmasa bile karar iki sonuç üretir: koşul doğruysa gövde çalışır
(TRUE dalı), yanlışsa gövde atlanır (FALSE dalı). FALSE dalı **kod satırı içermez** ama yine
de bir daldır ve branch coverage için test edilmelidir. Statement coverage bu dalı hiç
görmez, çünkü orada sayılacak satır yoktur.

📌 **Sık yapılan hata:** "else yoksa tek dal var" sanmak. Karar her zaman **iki** dallıdır;
FALSE dalı satırsız da olsa branch için gereklidir.

🔗 [4.3 §3 Özel durum (else'siz if)](4.3-beyaz-kutu.md)

### Soru 4 — %100 branch coverage'ın bulamayacağı iki kusur tipi yaz.

**Kısa cevap:** **(1)** Spesifikasyonda olup **kodda hiç yazılmamış** özellik ·
**(2)** Kod çalışıyor ama **yanlış hesaplıyor** (doğruluk kusuru) — kapsam doğruluğu
göstermez.

**Ayrıntı:** Branch coverage yalnızca **var olan dalların çalıştırıldığını** ölçer.
Uygulanmamış bir gereksinim için ölçülecek dal yoktur → görülemez. Ayrıca yanlış formül
yazılmışsa (örn. +10 yerine +5) tüm dallar çalışır ve beklenen sonuçlar da yanlış yazılmışsa
testler geçer; %100 kapsam yine sağlanır. Kapsam bir **ölçüdür**, kalite/doğruluk garantisi
değildir.

📌 **Sık yapılan hata:** "%100 branch = kod doğru ve tam" sanmak. Kapsam ne eksik özelliği
ne de yanlış mantığı garanti eder.

🔗 [4.3 §4 Beyaz kutunun sınırları](4.3-beyaz-kutu.md)

### Soru 5 — Neden "dal sayısı = test case sayısı" demek yanlıştır?

**Kısa cevap:** Çünkü **bir test case birden fazla dalı** aynı anda kapsayabilir.

**Ayrıntı:** İki bağımsız `if`'te 4 dal vardır ama a>0 **ve** b>0 olan tek bir test iki TRUE
dalını birden kapsar; a≤0 **ve** b≤0 olan ikinci test iki FALSE dalını kapsar → 4 dal, 2
test. Bu yüzden gereken test sayısını **saymayla** bulursun, dal sayısını çarparak değil.
(İç içe yapıda ise bir dalı kapatınca içteki karar hiç çalışmayabilir; o da ayrı sayım
ister — yine ezber değil, sayma.)

📌 **Sık yapılan hata:** Dal sayısını test case sayısıyla eşitlemek. Say, çarpma —
kapsama göre değişir.

🔗 [4.3 §2 Sık yapılan hata (dal ≠ test)](4.3-beyaz-kutu.md)

## 4.4 Deneyim Tabanlı Test Teknikleri

### Soru 1 — Keşif testini "rastgele test"ten ayıran üç şey nedir?

**Kısa cevap:** **(1)** Bir **test charter**'a göre yapılır (kapsam + amaç önceden belli) ·
**(2)** **Time-boxed** — belirli süreli oturumlar · **(3)** Bulgular **kaydedilir**.

**Ayrıntı:** Exploratory testing plansız tıklama değildir; tasarım, yürütme ve değerlendirme
eşzamanlı ilerler ama bir **charter** (ne, neden, nasıl) çerçevesinde ve **zaman kutulu**
(tipik 60–120 dk) oturumlarda yapılır, yapılanlar ve bulgular yazılır. Yapılandırılmış hali
**SBTM**'dir (session-based test management). Ayrıca kalite testerın deneyimine bağlıdır —
herkesin eşit yapabileceği bir iş değildir.

📌 **Sık yapılan hata:** Keşif testini "serbest gezinti/rastgele" sanmak. Charter + time-box
+ kayıt onu sistematikleştirir.

🔗 [4.4 §2 Exploratory testing](4.4-deneyim-tabanli.md)

### Soru 2 — Fault attack nedir ve hangi tekniği yapılandırır?

**Kısa cevap:** Olası **hata, kusur ve arızaların listesini** yapıp o listeyi hedefleyen
testler tasarlamak; **error guessing**'i (hata tahmini) yapılandırır.

**Ayrıntı:** Fault attack (defect attack), sezgiye dayalı error guessing'i sistematik hale
getirir: liste geçmiş kusur verisinden, testerların bilgisinden veya genel kusur
taksonomilerinden çıkarılır, sonra her maddeyi hedefleyen test yazılır. Tipik hedefler:
boş girdi, null, sıfıra bölme, çok uzun metin, özel karakter, sınır değerler, çift tıklama,
geri butonu.

📌 **Sık yapılan hata:** Fault attack'i checklist-based ile karıştırmak. Fault attack
error guessing'in yapılandırılmış halidir, genel bir kontrol listesi değil.

🔗 [4.4 §1 Error guessing (fault attack)](4.4-deneyim-tabanli.md)

### Soru 3 — Checklist-based testing'in iki dezavantajını yaz.

**Kısa cevap:** **(1)** Tekrarlayan kullanımda **etkinliği azalır** (pesticide paradox) ·
**(2)** Liste **bakım ister**; güncellenmezse eskir. (Ayrıca maddelerin detay seviyesi
değişince yorum farkı doğar.)

**Ayrıntı:** Sabit bir checklist zamanla aynı kusurları bulur, yeni kusur bulamaz olur —
bu doğrudan 1.3'teki **pesticide paradox**'tur; çözüm listeyi düzenli güncellemektir. Ayrıca
maddeler yüksek seviyeli olduğu için farklı testerlar farklı yorumlayabilir. Yine de
checklist sistematik kapsamı artırır ve unutmayı önler — avantajları da vardır.

📌 **Sık yapılan hata:** Checklist'i "bir kez yaz, hep kullan" sanmak. Güncellenmeyen liste
etkinliğini yitirir.

🔗 [4.4 §3 Checklist-based (bakım uyarısı)](4.4-deneyim-tabanli.md)

### Soru 4 — Deneyim tabanlı tekniklerin ortak zayıflığı nedir?

**Kısa cevap:** **Kapsam sistematik olarak ölçülemez** ve sonuçlar tekrarlanabilir değildir.

**Ayrıntı:** Error guessing, exploratory ve checklist-based — üçünde de kaç eşdeğerlik
sınıfının, kaç dalın, kaç kuralın test edildiğini yüzdeyle söyleyemezsin. Bu yüzden bu
teknikler kara kutu ve beyaz kutu tekniklerinin **yerine değil, yanında** kullanılır;
onların bıraktığı boşlukları tararlar ama tek başına yeterlilik iddia edemezler.

📌 **Sık yapılan hata:** Ortak zayıflığı "her tekniğin ayrı bir kusuru var" diye dağıtmak.
Hepsini birleştiren tek nokta: **ölçülemeyen kapsam**.

🔗 [4.4 §4 Üç tekniğin karşılaştırması](4.4-deneyim-tabanli.md)

### Soru 5 — Hangi iki durumda keşif testi özellikle uygundur?

**Kısa cevap:** **(1)** Spesifikasyon az veya hiç yokken · **(2)** Zaman baskısı varken
(hızlı geri bildirim gerektiğinde).

**Ayrıntı:** Spesifikasyon yoksa sistematik türetme (EP, BVA, karar tablosu) yapamazsın —
keşif testi sistemi öğrenirken test eder. Zaman baskısı varsa charter'lı kısa oturumlar
hızlı değer üretir. Ayrıca yeni/tanınmayan bir sistemi öğrenmek ve diğer teknikleri
tamamlamak için de uygundur.

📌 **Sık yapılan hata:** Keşif testini "spesifikasyon varken de en iyi seçenek" sanmak.
Asıl gücü **doküman yokluğu ve zaman baskısı** durumlarındadır.

🔗 [4.4 §2 Ne zaman özellikle uygundur](4.4-deneyim-tabanli.md)

## 4.5 İşbirliği Tabanlı Test Yaklaşımları (ATDD)

### Soru 1 — 3C'nin üç bileşenini yaz. Hangisi kabul kriterlerini içerir?

**Kısa cevap:** **Card · Conversation · Confirmation.** Kabul kriterlerini
**Confirmation** (teyit) içerir.

**Ayrıntı:** **Card** story'nin fiziksel/dijital ortamıdır (kart, Jira issue).
**Conversation** yazılımın nasıl kullanılacağının açıklanmasıdır — değerin çoğu buradadır,
ortak anlayış burada kurulur. **Confirmation** ise story'nin tamamlandığını gösteren
**kabul kriterleridir**. Hafıza: kart yazar, konuşma anlaşır, teyit ölçer.

📌 **Sık yapılan hata:** Conversation ile Confirmation'ı karıştırmak. Konuşma anlayış kurar;
kabul kriterleri **Confirmation**'dadır.

🔗 [4.5 §1 3C (tuzak)](4.5-isbirligi.md)

### Soru 2 — INVEST'in altı harfini aç. Tester hangi ikisini özellikle sorgular?

**Kısa cevap:** **I**ndependent · **N**egotiable · **V**aluable · **E**stimable · **S**mall
· **T**estable. Tester özellikle **Testable** ve **Estimable**'ı sorgular.

**Ayrıntı:** İyi bir user story bağımsız, pazarlığa açık, değerli, kestirilebilir, küçük
(bir iterasyona sığar) ve test edilebilir olmalıdır. Testerın "**Bunu nasıl test ederim?**"
sorusu belirsiz bir story'yi anında ortaya çıkarır (Testable); kapsam net değilse efor
tahmini de yapılamaz (Estimable). Ölçülemeyen kabul kriteri Testable'ı ihlal eder.

📌 **Sık yapılan hata:** INVEST'i 3C ile karıştırmak. 3C story'nin **bileşenleri**, INVEST
story'nin **kalite özellikleridir**.

🔗 [4.5 §1 INVEST](4.5-isbirligi.md)

### Soru 3 — ATDD ile TDD arasındaki üç farkı yaz.

**Kısa cevap:** **Seviye** (ATDD = kabul, TDD = birim) · **kim yazar** (ATDD = ekip birlikte,
TDD = geliştirici) · **dil** (ATDD = doğal dil kabul testi, TDD = kod/birim testi).

**Ayrıntı:** TDD birim seviyesinde çalışır: geliştirici önce birim testini yazar, kodu ona
göre kurar — sürdüğü şey kod tasarımıdır. ATDD kabul seviyesindedir: iş temsilcisi,
geliştirici ve tester **birlikte**, kabul kriterlerinden **doğal dilde** test case'ler
üretir — sürdüğü şey kabul kriterleridir. İkisi de test-öncelikli (test-first) yaklaşımdır.

📌 **Sık yapılan hata:** İkisini "test önce yazılır" diye eş görmek. Seviye, yazar ve dil
onları ayırır.

🔗 [4.5 §3 ATDD ile TDD ve BDD farkı](4.5-isbirligi.md)

### Soru 4 — ATDD'de neden önce pozitif test case'ler yazılır?

**Kısa cevap:** Çünkü **happy path** — doğru olay sırası, hata/istisna olmadan beklenen
davranış — önce doğrulanır; **negatif ve fonksiyonel olmayan** test case'ler sonra eklenir.

**Ayrıntı:** Pozitif test case'ler kabul kriterlerinin temel/doğru akışını yansıtır ve
paydaşların "sistem ne yapmalı" beklentisini ortaya koyar; ekip önce bu ortak anlayışı
sabitler. Ardından "ya kayıt yoksa?", "ya yetkisizse?", "ya sınırda?" gibi negatif ve
fonksiyonel olmayan durumlar eklenir — bunlar için EP, BVA, karar tablosu gibi 4.2–4.4
teknikleri kullanılabilir.

📌 **Sık yapılan hata:** ATDD'nin teknikleri "dışladığını" sanmak. Tam tersine happy
path'ten sonra o teknikleri **kullanarak** negatif/sınır testleri türetir.

🔗 [4.5 §3 Test case'lerin özellikleri](4.5-isbirligi.md)

### Soru 5 — Specification workshop'ın asıl faydası nedir?

**Kısa cevap:** **User story ve kabul kriterlerindeki kusurların koddan önce bulunup
önlenmesi** (statik test değeri).

**Ayrıntı:** ATDD'nin asıl değeri test case üretmek değil, ekibin (iş temsilcisi +
geliştirici + tester) bir araya gelip story ve kriterleri tartıştığı workshop'ta
**belirsizlikleri, eksiklikleri ve çelişkileri** ortaya çıkarmasıdır. Orada çözülen bir
belirsizlik hiç koda dönüşmez — kusur **oluşmadan önlenir**. Bu, prensip 3 (early testing)
ve shift left'in en uç uygulamasıdır.

📌 **Sık yapılan hata:** ATDD'yi "koddan önce test yazma tekniği" diye özetleyip asıl
değerini kaçırmak. Asıl değer **kusur önlemedir**, test üretmek değil.

🔗 [4.5 §3 ATDD (kritik nokta)](4.5-isbirligi.md)
