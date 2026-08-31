# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 2.1 SDLC, shift left ve DevOps

### Soru 1 — TDD, ATDD ve BDD'yi birer cümleyle ayır. Kim yazar, ne seviyede, hangi dilde?

**Kısa cevap:** **TDD** = geliştirici **birim** seviyesinde otomatik testler yazar (kodu sürer);
**ATDD** = ekip birlikte **kabul kriterlerini/testlerini** yazar (kabul seviyesi); **BDD** =
**Given/When/Then doğal dilinde** davranış senaryoları yazılır (ortak anlayış).

**Ayrıntı:** Üçü de "önce test, sonra kod" ailesinden. Ayıran eksen "neyi sürdüğü": TDD **kodu**
(Red→Green→Refactor, geliştirici), ATDD **kabul kriterlerini** (tester + geliştirici + iş temsilcisi
birlikte, tasarımdan önce), BDD **davranışı** (iş temsilcisiyle doğal dil senaryoları, sonra
otomatikleştirilir). Hafıza kancası: **T**DD → **T**eknik, **A**TDD → **A**cceptance, **B**DD → **B**ehavior.

📌 **Sık yapılan hata:** Üçünü "aynı şey" sanmak ya da BDD'yi teknik test yazan bir yöntem gibi görmek.
BDD'nin özü doğal dilde ortak anlayış; ATDD kabul kriteri netliği; TDD birim tasarımı.

🔗 [2.1 §3 TDD/ATDD/BDD](2.1-sdlc.md)

### Soru 2 — Shift left neden kısa vadede eforu artırır?

**Kısa cevap:** Çünkü shift left **ek çaba** ister — review, erken test yazımı, statik analiz gibi
aktiviteler öne alınır; "aynı işi daha erken yapmak" değildir.

**Ayrıntı:** Shift left, test aktivitelerini yaşam döngüsünde sola (erkene) çekmektir (Prensip 3'ün
uygulaması). Gereksinim/tasarım review'u, ATDD ile kabul kriterlerini önceden yazmak, TDD ile birim
testi, CI'da statik analiz — bunların hepsi baştan yatırım gerektirir. Bu erken yatırım, geç bulunan
kusurun katlanan maliyetini düşürür; yani kısa vadede efor **artar**, uzun vadede toplam maliyet düşer.

📌 **Sık yapılan hata:** "Shift left toplam eforu hemen azaltır" sanmak. Kısa vadede eforu artırır;
kazanç uzun vadede gelir.

🔗 [2.1 §5 Shift left (kritik nokta)](2.1-sdlc.md)

### Soru 3 — DevOps'un test açısından iki riskini say.

**Kısa cevap:** **(1)** DevOps pipeline'ının kendisi kurulmalı ve bakımı yapılmalıdır (ek maliyet);
**(2)** Test otomasyonu ek kaynak/yatırım gerektirir ve **her şeyi kapsayamaz** — keşif ve deneyim
tabanlı test hâlâ insan işidir.

**Ayrıntı:** DevOps'un faydaları (hızlı geri bildirim, otomatik kapılar, tekrarlanabilir ortam, azalan
regresyon riski) bedava değildir. Pipeline ve otomasyon altyapısının kurulumu ve sürdürülmesi kaynak ister;
üstelik otomasyon regresyonu devralsa bile keşif testi (exploratory), kullanılabilirlik ve deneyim
tabanlı test otomatikleştirilemez.

📌 **Sık yapılan hata:** "DevOps manuel testi ortadan kaldırır" demek. Otomasyon regresyonu devralır ama
keşif ve deneyim tabanlı test insan işidir.

🔗 [2.1 §4 DevOps ve test](2.1-sdlc.md)

### Soru 4 — V-model "test en sonda yapılır" demek midir? Gerekçelendir.

**Kısa cevap:** **Hayır.** V-model'de her geliştirme aşamasının karşısına bir test seviyesi konur ve
test **planlama/tasarımı** ilgili geliştirme aşamasıyla **eşzamanlı** başlar.

**Ayrıntı:** V-model sıralı bir modeldir; dinamik test yürütmesi görece geç (aşama bitince) olsa da
statik test ve test tasarımı erken başlar. Her geliştirme aşamasına karşılık gelen bir test seviyesi
vardır ve bir seviyenin analiz/tasarımı ilgili aşamayla birlikte kurulur — bu, shift left'in temelidir.
Dolayısıyla V-model bile "test yalnızca en sonda" demez.

📌 **Sık yapılan hata:** V-model'i "önce hep geliştir, sonra hep test et" diye okumak. Test planlama ve
tasarımı ilgili geliştirme aşamasıyla eşzamanlı yürür.

🔗 [2.1 §1 V-model'in özelliği](2.1-sdlc.md)

### Soru 5 — Retrospektif ile review arasındaki fark ne olabilir?

**Kısa cevap:** **Retrospective süreci** değerlendirir (neyin iyi/kötü gittiği, neyi değiştireceğiz —
çıktısı action item'lar); **review ise ürünü/iş ürününü** değerlendirir (kusur bulmak, kaliteyi ölçmek).

**Ayrıntı:** Retrospektif bir iterasyon/sürüm/proje sonunda ekibin toplanıp süreç iyileştirmesi
konuştuğu toplantıdır; hedefi test etkinliğini/kalitesini, ekip bağını ve test tabanının kalitesini
artırmaktır ve çıktısı **eyleme dönüştürülebilir iyileştirme maddeleridir**. Review ise bir iş ürününü
(gereksinim, tasarım, kod) gözden geçirip değerlendirir. Kısaca: retrospektif → süreç, review → ürün.

📌 **Sık yapılan hata:** İkisini karıştırmak. "İterasyon sonunda süreci iyileştirme toplantısı" =
retrospective; ürünü/dokümanı değerlendiren = review.

🔗 [2.1 §6 Retrospektifler](2.1-sdlc.md)

## 2.2 Test Seviyeleri ve Test Tipleri

### Soru 1 — Beş test seviyesini sırayla yaz ve her birine bir test tabanı örneği ver.

**Kısa cevap:** **Component (unit)** — detaylı tasarım/kod · **Component integration** — yazılım
tasarımı/mimari/sequence diagram · **System** — sistem gereksinimleri/risk analizi · **System
integration** — sistem/arayüz spesifikasyonları/API tanımları · **Acceptance** — iş
süreçleri/kullanıcı gereksinimleri/sözleşme/regülasyon.

**Ayrıntı:** Sıra soruda kritik. Her seviye kendi test nesnesi, test tabanı, aradığı kusur ve
sorumluluğuyla ayrılır: component tek bileşene bakar (geliştirici), component integration bileşenler
arası arayüzlere, system uçtan uca sistemin tamamına (bağımsız tester), system integration sistemin dış
sistemlerle arayüzüne, acceptance ise kullanım amacına uygunluğa (kullanıcı/müşteri).

📌 **Sık yapılan hata:** Component integration ile system integration'ı karıştırmak. İlki **iç**
bileşenler arası arayüz, ikincisi sistemin **dış** sistem/servislerle arayüzüdür.

🔗 [2.2 §1 Test seviyeleri](2.2-seviyeler-ve-tipler.md)

### Soru 2 — "Security testing" bir seviye mi tip mi? Hangi seviyelerde yapılabilir?

**Kısa cevap:** Bir **tiptir** (fonksiyonel olmayan, ISO 25010'daki *security* karakteristiği); **her
seviyede** yapılabilir ve **mümkün olduğunca erken** yapılmalıdır.

**Ayrıntı:** Seviye = NEREDE, tip = NE — ikisi dik eksenlerdir. Güvenlik "neyi ölçtüğünü" anlattığı için
tiptir. Fonksiyonel olmayan test yalnızca sistem seviyesinde yapılmaz; component'ten acceptance'a kadar
her seviyede yapılabilir. Geç bulunan fonksiyonel olmayan kusur mimarî değişiklik gerektirebileceği için
pahalıdır; bu da erken yapmanın (shift left) güçlü gerekçesidir.

📌 **Sık yapılan hata:** "Non-functional/security testing sadece sistem seviyesinde yapılır" sanmak.
Her seviyede ve olabildiğince erken yapılır.

🔗 [2.2 §2 Test tipleri (tuzak)](2.2-seviyeler-ve-tipler.md)

### Soru 3 — Alpha ile beta testinin farkı nedir?

**Kısa cevap:** **Alpha** dış kullanıcılarca **geliştirici sahasında** (bizde), **beta** dış
kullanıcılarca **müşteri sahasında/kendi ortamlarında** (onlarda) yapılır.

**Ayrıntı:** İkisi de kabul testinin biçimleridir ve dış kullanıcı geri bildirimi toplamayı amaçlar.
Alpha erken, kontrollü bir ortamda (geliştirici sahasında) gerçek kullanım geri bildirimi verir; beta
gerçek ortamda geniş geri bildirim sağlar. Hafıza kancası: **A**lpha = **A**t our place, **B**eta =
**B**ack at theirs.

📌 **Sık yapılan hata:** İkisinin yerini karıştırmak. "Geliştirici sahasında" → alpha, "kendi
ortamlarında/müşteri sahasında" → beta.

🔗 [2.2 §1 Kabul testinin biçimleri](2.2-seviyeler-ve-tipler.md)

### Soru 4 — Kabul testinin birincil amacı nedir, sistem testinden farkı ne?

**Kısa cevap:** Kabul testinin birincil amacı **kusur bulmak değil**; sisteme **güven oluşturmak** ve
**kullanıma hazır olduğunu göstermektir.** Sistem testi ise uçtan uca fonksiyonel + fonksiyonel olmayan
**kusur bulmaya** odaklıdır.

**Ayrıntı:** Kabul testi kullanım amacına uygunluğa bakar; test tabanı iş süreçleri, kullanıcı
gereksinimleri, sözleşme ve regülasyondur; genelde kullanıcı/müşteri yapar. Sistem testi ise sistemin
tamamının davranışını sistem gereksinimlerine göre değerlendirir ve bağımsız tester tarafından yapılır.
"To find as many defects as possible" çeldiricisi kabul testine değil, **sistem testine** aittir.

📌 **Sık yapılan hata:** Kabul testinin amacını "olabildiğince çok kusur bulmak" sanmak. O sistem
testinin amacıdır; kabul testi güven ve hazır olma gösterir.

🔗 [2.2 §1 Kabul testi (kritik nokta)](2.2-seviyeler-ve-tipler.md)

### Soru 5 — Regresyon testi kapsamı nasıl belirlenir? Hangi Bölüm 1 kavramı işe yarar?

**Kısa cevap:** Kapsamı **impact analysis** (etki analizi) belirler; bunun için 1.4'teki
**izlenebilirlik** (traceability) işe yarar.

**Ayrıntı:** Bir değişiklikten sonra hangi alanların etkilendiğini ve dolayısıyla hangi testlerin
koşulacağını etki analizi saptar. Değişiklik ↔ test bağlantısı izlenebilirlikle kurulduğu için, iyi
izlenebilirlik regresyon kapsamını daraltıp odaklar. İzlenebilirlik yoksa etki alanı bilinemez ve
regresyon "her şeyi test etmeye" dönüşür. Ayrıca pesticide paradox nedeniyle regresyon suite'i de
düzenli yenilenmelidir.

📌 **Sık yapılan hata:** Regresyon kapsamını rastgele/tümünü koşarak belirlemek. Kapsam impact analysis
ile seçilir; izlenebilirlik bunu ucuzlatır.

🔗 [2.2 §3 Confirmation ve regression](2.2-seviyeler-ve-tipler.md)

## 2.3 Bakım Testi

### Soru 1 — Bakım testinin üç tetikleyici grubunu ve her birine bir örnek yaz.

**Kısa cevap:** **Modification (değişiklik)** — örn. Java 17 → 21 geçişi (düzeltme, yeni özellik,
ortam/COTS yükseltmesi, yama); **Upgrade/migration (yükseltme/göç)** — örn. on-prem → bulut göçü;
**Retirement (emeklilik)** — örn. sistemin kullanımdan kaldırılması.

**Ayrıntı:** Bir sistem üretime alındıktan sonra ömrü boyunca değişir; bu değişikliklerin testi bakım
testidir. Modification grubu planlı geliştirme, düzeltici/acil değişiklik, işletim ortamı değişikliği ve
COTS yükseltmelerini kapsar. Migration'da yalnızca yeni ortamda çalışmayı değil **taşınan verinin
doğruluğunu** da test edersin (conversion testi). Retirement'ta ise **arşivleme ve geri yükleme**
prosedürleri test edilir.

📌 **Sık yapılan hata:** Migration'da yalnızca "yeni ortamda çalışıyor mu"yu test etmek. Taşınan verinin
doğruluğu (data conversion) da test edilmelidir.

🔗 [2.3 §2 Bakım testini tetikleyenler](2.3-bakim-testi.md)

### Soru 2 — Bir sistem emekliye ayrılırken ne test edilir? Neden?

**Kısa cevap:** **Veri arşivleme** ve **veri geri yükleme (restore) prosedürleri** test edilir — çünkü
sistem kapansa da veriye uzun süreli **saklama (retention)** gereksinimi nedeniyle sonradan
erişilebilmesi gerekir.

**Ayrıntı:** Retirement, bakım testinin bir tetikleyicisidir. Uzun süreli veri saklama zorunluluğu varsa,
arşivlenen verinin ileride **geri yüklenebildiğini** kanıtlaman gerekir. "Sistem kapanıyorsa test
gerekmez" ifadesi bu yüzden yanlıştır: emeklilik de test edilir.

📌 **Sık yapılan hata:** "Sistem emekliye ayrılıyorsa test gerekmez" sanmak. Arşivleme ve geri yükleme
prosedürleri test edilmelidir.

🔗 [2.3 §2 Retirement (özel durum)](2.3-bakim-testi.md)

### Soru 3 — Etki analizinin üç faydasını say.

**Kısa cevap:** **(1)** Değişikliğin **istenmeyen sonuçlarını** (nerede kırılabileceğini) belirlemek;
**(2)** **Regresyon testinin kapsamını** belirlemek; **(3)** Değişikliğin **yapılıp yapılmayacağına**
karar vermeyi desteklemek.

**Ayrıntı:** Impact analysis, bir değişikliğin sistemin hangi kısımlarını etkilediğini ve sonucunda
hangi testlerin koşulacağını saptar. Böylece yan etkiler önceden görülür, regresyon eforu doğru yere
odaklanır ve etkisi çok genişse değişiklik ertelenebilir. Kapsamı belirleyen üç faktör: değişikliğin
riski, sistemin boyutu ve değişikliğin boyutu.

📌 **Sık yapılan hata:** Etki analizini yalnızca "regresyon kapsamı seçmek" ile eşitlemek. İstenmeyen
sonuçları görme ve değişiklik kararını destekleme de faydalarıdır.

🔗 [2.3 §4 Etki analizi](2.3-bakim-testi.md)

### Soru 4 — İzlenebilirlik olmadan etki analizi neden pahalılaşır?

**Kısa cevap:** Çünkü izlenebilirlik yoksa **değişiklik ↔ test bağlantısı** kurulamaz; neyin
etkilendiği bilinemez ve bakım testi **her şeyi test etmeye** dönüşür.

**Ayrıntı:** Etki analizini zorlaştıran etkenlerden ikisi güncel olmayan/eksik spesifikasyonlar ve
izlenebilirlik yokluğudur (diğerleri: değişikliği yapanların alan bilgisi eksikliği, yazılımın değişime
hazırlanmamış/sıkı bağlı olması). İyi izlenebilirlik + güncel spesifikasyon = ucuz etki analizi; bunlar
yoksa etki alanı kestirilemediği için tüm sistemi test etme maliyeti doğar.

📌 **Sık yapılan hata:** İzlenebilirliği yalnızca kapsam raporlaması için önemli sanmak. Bakım testinde
etki analizini ucuzlatan asıl şeydir.

🔗 [2.3 §4 Etki analizini zorlaştıranlar](2.3-bakim-testi.md)

### Soru 5 — Bakım testi bir test seviyesi midir? Cevabını gerekçelendir.

**Kısa cevap:** **Hayır.** Bakım testi bir test seviyesi de bir test tipi de değildir; bir
**bağlamdır** — içinde her seviye ve her tip test yapılabilir.

**Ayrıntı:** Bakım testi, canlıdaki bir sisteme yapılan değişikliğin testidir ve iki soruyu birden
sorar: değişiklik doğru çalışıyor mu (confirmation + yeni test) ve çalışan yerleri bozdu mu (regression).
Bu bir "ne zaman/hangi katman" (seviye) ya da "neyi ölçüyor" (tip) meselesi değil, testin yapıldığı
**durumdur**; bu bağlam içinde component'ten acceptance'a her seviye ve her tip devreye girebilir.

📌 **Sık yapılan hata:** "Maintenance testing bir test seviyesidir" demek. O bir bağlamdır; içinde her
seviye ve tip yapılabilir.

🔗 [2.3 §1 Bakım testi nedir (tuzak)](2.3-bakim-testi.md)
