# Kavram sözlüğü

> **Bu dosya hatırlatma içindir, ilk öğrenme için değil.** Her terim konu dosyasında,
> ilk geçtiği yerde açıklanıyor. Buraya "neydi bu ya?" dediğinde dönersin.
>
> ISTQB sınavı **İngilizce terimlerle** soru sorar; bu yüzden her girdide terimin
> İngilizcesi de yazılı. Sınavda göreceğin biçim odur.
>
> Beş bölüm: [Sınav ve müfredat](#a-sınav-ve-müfredat) · [Test temelleri](#b-test-temelleri) ·
> [Seviyeler ve tipler](#c-seviyeler-ve-tipler) · [Teknikler](#d-teknikler) ·
> [Test yönetimi](#e-test-yönetimi)

---

## A. Sınav ve müfredat

#### ISTQB

*International Software Testing Qualifications Board.* Yazılım testi
sertifikalarını tanımlayan uluslararası kuruluş. Müfredatı (syllabus) o yayımlar,
sınavı ülke temsilcileri yapar.
→ [00 Sınav künyesi](00-sinav-kunyesi.md)

#### CTFL

*Certified Tester Foundation Level.* Bu setin hazırladığı giriş seviyesi sertifika.
40 soru, 60 dakika, geçme eşiği 26/40 (%65).
→ [00 Sınav künyesi](00-sinav-kunyesi.md)

#### K seviyeleri (K1, K2, K3)

Müfredattaki her öğrenme hedefinin **bilişsel derinliği**. **K1** hatırlama
(tanımı bilir), **K2** anlama (karşılaştırır, açıklar), **K3** uygulama (verilen
duruma tekniği uygular). Sınavda K3 soruları hesap ya da tasarım ister ama
puanı diğerleriyle aynıdır — zaman tuzağı buradadır.
→ [00 Sınav künyesi](00-sinav-kunyesi.md)

---

## B. Test temelleri

#### Hata, kusur, arıza

*Error, defect, failure.* Zincir tek yönlüdür: **insan hata yapar** (error) →
koda **kusur** girer (defect) → çalışırken **arıza** olarak görünür (failure).
Her kusur arızaya dönüşmez; bazıları hiç tetiklenmez.
→ [1.2 Test neden gerekli](../01-temeller/1.2-test-neden-gerekli.md)

#### QA

*Quality Assurance — kalite güvence.* **Sürece** odaklanır: doğru süreç
uygulanırsa kusur daha az oluşur. Testten farkı budur; test ürüne bakar.
→ [1.2 Test neden gerekli](../01-temeller/1.2-test-neden-gerekli.md)

#### QC

*Quality Control — kalite kontrol.* **Ürüne** odaklanır: üretilen şeyde kusur
var mı? Test, kalite kontrolün bir parçasıdır.
→ [1.2 Test neden gerekli](../01-temeller/1.2-test-neden-gerekli.md)

#### SDLC

*Software Development Lifecycle — yazılım geliştirme yaşam döngüsü.* Sınavda
sorulan şey modellerin kendisi değil, **modelin test üzerindeki etkisi**:
sıralı modelde test sonda, yinelemeli modelde her yinelemede.
→ [2.1 SDLC, shift left ve DevOps](../02-yasam-dongusu/2.1-sdlc.md)

#### Shift left

Test aktivitelerini yaşam döngüsünde **erkene çekmek**. Kusuru gereksinim
aşamasında yakalamak, üretimde yakalamaktan kat kat ucuzdur.
→ [2.1 SDLC, shift left ve DevOps](../02-yasam-dongusu/2.1-sdlc.md)

#### CI/CD

*Continuous integration / continuous delivery — sürekli tümleştirme / sürekli
teslim.* Her değişikliğin otomatik derlenip test edildiği, sonra otomatik
paketlenip yayına hazırlandığı hat. Otomatik regresyon testinin yaşadığı yer.
→ [2.1 SDLC, shift left ve DevOps](../02-yasam-dongusu/2.1-sdlc.md)

#### Testware

Test sırasında üretilen **her türlü iş ürünü**: test planı, test durumu, test
verisi, test ortamı betikleri, kusur raporu, test raporu. Kodun kendisi değildir.
→ [1.4 Test aktiviteleri](../01-temeller/1.4-test-aktiviteleri.md)

#### İzlenebilirlik

*Traceability.* Gereksinimden test durumuna, oradan kusura kadar uzanan bağ.
"Bu gereksinim test edildi mi?" ve "bu testi neden yazdık?" sorularının cevabı
buradan gelir.
→ [1.4 Test aktiviteleri](../01-temeller/1.4-test-aktiviteleri.md)

#### Bağımsızlık

*Independence of testing.* Testi yazanın koda ne kadar uzak olduğu. Bağımsızlık
arttıkça **farklı kusur tipleri** bulunur ama ürün bilgisi azalır; ikisi arasında
bir denge kurulur.
→ [1.5 Temel beceriler ve bağımsızlık](../01-temeller/1.5-beceriler.md)

---

## C. Seviyeler ve tipler

#### UAT

*User acceptance testing — kullanıcı kabul testi.* Son kullanıcının, sistemin
**kendi işini yapmasına yettiğini** doğruladığı kabul testi.
→ [2.2 Test seviyeleri ve tipleri](../02-yasam-dongusu/2.2-seviyeler-ve-tipler.md)

#### OAT

*Operational acceptance testing — operasyonel kabul testi.* Operasyon ekibinin
baktığı kabul testi: yedekleme, geri yükleme, kurulum, felaket kurtarma,
güvenlik. UAT iş sürecine bakar, OAT **işletmeye**.
→ [2.2 Test seviyeleri ve tipleri](../02-yasam-dongusu/2.2-seviyeler-ve-tipler.md)

#### Regresyon testi

Yapılan bir değişikliğin, **daha önce çalışan** bir şeyi bozup bozmadığını
ölçen test. Doğrulama testiyle karıştırılır: doğrulama testi düzeltmenin
işe yaradığını, regresyon testi başka bir şeyi bozmadığını gösterir.
→ [2.3 Bakım testi](../02-yasam-dongusu/2.3-bakim-testi.md)

#### Etki analizi

*Impact analysis.* Bir değişikliğin sistemin **hangi bölümlerini** etkilediğini
belirleyip regresyon testinin kapsamını buna göre seçmek.
→ [2.3 Bakım testi](../02-yasam-dongusu/2.3-bakim-testi.md)

---

## D. Teknikler

#### EP

*Equivalence partitioning — eşdeğerlik bölümleme.* Girdi alanını, üyeleri **aynı
davranışı göstermesi beklenen** sınıflara bölmek ve her sınıftan bir değer
seçmek. Amaç: test sayısını düşürüp kapsamı korumak.
→ [4.2a EP ve BVA](../04-analiz-tasarim/4.2a-ep-bva.md)

#### BVA

*Boundary value analysis — sınır değer analizi.* Eşdeğerlik sınıflarının
**sınırlarını** test etmek; kusurlar oralarda kümelenir. EP'nin üzerine kurulur,
onun yerine geçmez.
→ [4.2a EP ve BVA](../04-analiz-tasarim/4.2a-ep-bva.md)

#### Karar tablosu

*Decision table testing.* Koşul birleşimlerinin ve her birleşime karşılık gelen
eylemin tabloya dökülmesi. **Kuralların birbiriyle etkileştiği** durumlarda
kullanılır.
→ [4.2b Karar tablosu ve durum geçişi](../04-analiz-tasarim/4.2b-karar-durum.md)

#### Durum geçiş testi

*State transition testing.* Sistemin durumları ve aralarındaki geçişler üzerinden
test tasarımı. **Geçmişe bağlı** davranışlarda (oturum, sipariş durumu) uygundur.
→ [4.2b Karar tablosu ve durum geçişi](../04-analiz-tasarim/4.2b-karar-durum.md)

#### Statement coverage

*İfade kapsamı.* Kodun **her satırının** en az bir kez çalıştırılma oranı.
→ [4.3 Beyaz kutu teknikleri](../04-analiz-tasarim/4.3-beyaz-kutu.md)

#### Branch coverage

*Dal kapsamı.* Her kararın hem **doğru** hem **yanlış** sonucunun alınma oranı.
Statement coverage'ı **içerir**: %100 branch, %100 statement demektir — tersi
doğru değildir.
→ [4.3 Beyaz kutu teknikleri](../04-analiz-tasarim/4.3-beyaz-kutu.md)

#### Keşif testi

*Exploratory testing.* Test tasarımı, çalıştırması ve öğrenmenin **eşzamanlı**
yürüdüğü deneyim tabanlı yaklaşım. Genelde bir görev tanımıyla (charter)
zamanlanır.
→ [4.4 Deneyim tabanlı teknikler](../04-analiz-tasarim/4.4-deneyim-tabanli.md)

#### Hata tahmini

*Error guessing.* Testçinin geçmiş deneyimine dayanarak "burada kusur olabilir"
dediği yerleri hedeflemesi.
→ [4.4 Deneyim tabanlı teknikler](../04-analiz-tasarim/4.4-deneyim-tabanli.md)

#### TDD

*Test-driven development.* Önce **başarısız** test yazılır, sonra onu geçiren en
az kod, sonra düzenleme. Testi **geliştirici** yazar, hedef tasarımdır.
→ [4.5 İşbirliği tabanlı yaklaşımlar](../04-analiz-tasarim/4.5-isbirligi.md)

#### ATDD

*Acceptance test-driven development.* Kabul testleri, geliştirme **başlamadan
önce** iş, geliştirme ve test tarafının birlikte yazdığı testlerdir. Amaç kusur
bulmak değil, kusurun **oluşmasını önlemek**.
→ [4.5 İşbirliği tabanlı yaklaşımlar](../04-analiz-tasarim/4.5-isbirligi.md)

#### BDD

*Behaviour-driven development.* Davranışın *Given / When / Then* kalıbıyla,
herkesin okuyabildiği bir dille yazılması.
→ [4.5 İşbirliği tabanlı yaklaşımlar](../04-analiz-tasarim/4.5-isbirligi.md)

#### INVEST

İyi bir kullanıcı hikâyesinin altı ölçütü: *Independent · Negotiable · Valuable ·
Estimable · Small · Testable.* Sınavda **3C ile karıştırılır**; INVEST hikâyenin
**niteliğini** ölçer.
→ [4.5 İşbirliği tabanlı yaklaşımlar](../04-analiz-tasarim/4.5-isbirligi.md)

#### 3C

Kullanıcı hikâyesinin üç parçası: **Card** (ortam/kısa tanım) · **Conversation**
(ortak anlayışın kurulduğu konuşma) · **Confirmation** (kabul kriterleri).
INVEST nitelik ölçer, 3C **yapıyı** tarif eder.
→ [4.5 İşbirliği tabanlı yaklaşımlar](../04-analiz-tasarim/4.5-isbirligi.md)

#### DoR ve DoD

*Definition of Ready* bir işin **başlanabilir** sayılması için gerekenler;
*Definition of Done* **bitmiş** sayılması için gerekenler. İkisi giriş/çıkış
kriterlerinin çevik karşılığıdır.
→ [5.1a Test planlama](../05-yonetim/5.1a-test-planlama.md)

---

## E. Test yönetimi

#### Giriş ve çıkış kriterleri

*Entry / exit criteria.* Bir test aktivitesine **başlamak** ve onu **bitmiş
saymak** için sağlanması gereken koşullar. Çevik takımlarda DoR ve DoD adını alır.
→ [5.1a Test planlama](../05-yonetim/5.1a-test-planlama.md)

#### Test piramidi

Alt katmanda çok sayıda hızlı birim testi, üstte az sayıda yavaş uçtan uca test
öngören denge. Ters çevrilirse hat yavaşlar ve kırılgan olur.
→ [5.1b Kestirim, piramit ve quadrant](../05-yonetim/5.1b-kestirim-piramit.md)

#### Test quadrantları

Testleri iki eksende ayıran dörtlü: **teknolojiye/iş tarafına** dönük ×
**ekibi destekleyen/ürünü eleştiren**. **Q1** birim ve bileşen testleri,
**Q2** işlevsel testler ve örnekler, **Q3** keşif ve kullanılabilirlik testi,
**Q4** performans, yük, stres ve güvenlik testleri.
→ [5.1b Kestirim, piramit ve quadrant](../05-yonetim/5.1b-kestirim-piramit.md)

#### Üç noktalı kestirim

*Three-point estimation.* İyimser (a), en olası (m) ve kötümser (b) tahminden
beklenen değeri hesaplama yöntemi: **E = (a + 4m + b) / 6**, standart sapma
**SD = (b − a) / 6**.
→ [5.1b Kestirim, piramit ve quadrant](../05-yonetim/5.1b-kestirim-piramit.md)

#### Risk

Olumsuz bir olayın **olasılığı** ile **etkisinin** birleşimi. Ürün riski
ürünün kendisine, proje riski projenin yürütülmesine ilişkindir. Test önceliği
risk seviyesinden çıkar.
→ [5.2 Risk yönetimi](../05-yonetim/5.2-risk-yonetimi.md)

#### CM

*Configuration management — konfigürasyon yönetimi.* Testware'in ve test
nesnesinin sürümlenip **birbirine bağlanması**; hangi testin hangi sürümde
çalıştığının bilinmesi. Olmadan test sonucu tekrarlanamaz.
→ [5.4 Konfigürasyon ve kusur yönetimi](../05-yonetim/5.4-konfigurasyon-kusur.md)

#### Kusur raporu

*Defect report.* Bir arızanın kaydı: yeniden üretme adımları, beklenen ve
gözlenen sonuç, ortam, önem ve öncelik. Amacı suçlamak değil, **düzeltilebilir
kılmak**.
→ [5.4 Konfigürasyon ve kusur yönetimi](../05-yonetim/5.4-konfigurasyon-kusur.md)

#### Önem ve öncelik

*Severity / priority.* Önem arızanın **teknik etkisi**, öncelik **ne kadar acil
düzeltileceği**. Yüksek önem düşük öncelikli olabilir (nadir bir yolda çökme),
tersi de olur (her ekranda görünen yazım hatası).
→ [5.4 Konfigürasyon ve kusur yönetimi](../05-yonetim/5.4-konfigurasyon-kusur.md)
