# 01 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 1.1 Test nedir? Hedefler ve debugging

### Soru 1 — Test yürütme dışında dört test aktivitesi say.

**Kısa cevap:** **Planlama, analiz, tasarım, izleme/kontrol** (ve raporlama).

**Ayrıntı:** ISTQB'de test bir **süreçtir**, tek bir aktivite değil. Test
yürütme (execution) yalnızca yedi aktiviteden biridir; yanında planlama
(planning), izleme ve kontrol (monitoring & control), analiz (analysis),
tasarım (design), gerçekleştirme (implementation) ve tamamlama (completion)
vardır. Soru "Choose TWO" derse `test planning` gibi bir şık da doğrudur.

📌 **Sık yapılan hata:** Testi test yürütmeye indirgemek. Yürütme testin
görünen kısmı ama sadece bir parçasıdır.

🔗 [1.1 §1 Test nedir?](1.1-test-nedir.md)

### Soru 2 — Statik test neden debugging gerektirmez?

**Kısa cevap:** **Statik test kusuru doğrudan bulur** (arıza üretmeden), oysa
debugging bir **arızanın** sebebini aramaktır.

**Ayrıntı:** Debugging zinciri "arıza gördüm → sebebini bulayım → düzelteyim"
üzerine kuruludur; başlangıç noktası bir failure'dır. Statik test kodu
çalıştırmaz, arıza üretmez; kusuru belgede/kodda **doğrudan** görür. Ortada
tetiklenmiş bir arıza olmadığı için "sebebini bul" adımı da yoktur.

📌 **Sık yapılan hata:** Debugging'i testin parçası sanmak. Test kusuru
**bulur**, debugging kusuru **düzeltir** — ayrı işlerdir.

🔗 [1.1 Büyük fikir + §1 statik/dinamik](1.1-test-nedir.md)

### Soru 3 — "Are we building the right product?" hangisi — verification mı validation mı?

**Kısa cevap:** **Validation.**

**Ayrıntı:** İki soru ayrılır: *"Are we building the product **right**?"* =
**verification** (şartnameye uygun mu?), *"Are we building the **right**
product?"* = **validation** (kullanıcının istediği bu muydu?). Referansları da
farklıdır: verification şartnameye, validation kullanıcı ihtiyacına bakar.

📌 **Sık yapılan hata:** İkisini `right` kelimesinin yerine bakmadan
karıştırmak. "right product" (doğru ürün) = validation; "product right" (ürünü
doğru) = verification.

🔗 [1.1 §2 verification/validation](1.1-test-nedir.md)

### Soru 4 — Debugging'in üç adımı nedir, sırasıyla?

**Kısa cevap:** **Kusuru yeniden üret (reproduce) → teşhis et/kök nedeni bul
(diagnose) → düzelt (fix).**

**Ayrıntı:** Debugging bir arızayla başlar: önce arıza güvenilir biçimde
yeniden üretilir, sonra onu üreten kusur bulunur (teşhis), sonra kusur
düzeltilir. Düzeltmenin işe yaradığını göstermek için ardından **doğrulama
testi** (confirmation testing) yapılır — ama bu artık testtir, debugging değil.

📌 **Sık yapılan hata:** Doğrulama testini debugging'in adımı sanmak.
Düzeltmeyi doğrulamak bir **test** aktivitesidir.

🔗 [1.1 §3 Debugging](1.1-test-nedir.md)

### Soru 5 — Bir test hedefi ifadesini nasıl anlarsın ki çeldiricidir? Hangi kelimeleri ararsın?

**Kısa cevap:** **`prove`, `guarantee`, `ensure defect-free`, `correct`** gibi
kesinlik iddiaları çeldiricidir.

**Ayrıntı:** Testin hedefi doğruluğu **kanıtlamak değil**, güven vermek
(confidence) ve riski düşürmektir (1.3'ün 1. prensibi: test kusurun varlığını
gösterir, yokluğunu değil). Bu yüzden "prove the software is correct",
"guarantee", "ensure defect-free" içeren şıklar yanlıştır; doğru ifadeler
"provide confidence" ve "reduce risk"tir.

📌 **Sık yapılan hata:** Kesinlik bildiren şıkkı "güçlü göründüğü" için doğru
sanmak. Testte kesinlik iddiası neredeyse her zaman yanlış şıkkın işaretidir.

🔗 [1.1 §2 Test hedefleri (tuzak)](1.1-test-nedir.md)

## 1.2 Test neden gerekli? Hata, kusur, arıza

### Soru 1 — Bir defect'in var olup hiçbir failure'a yol açmaması nasıl mümkün? Bir örnek yaz.

**Kısa cevap:** **Kusurlu kod hiç çalıştırılmazsa ya da o koşul hiç oluşmazsa**
arıza görünmez.

**Ayrıntı:** Zincirde her defect failure üretmez. Örnek: `age >= 18` yerine
`age > 18` yazılmış bir kontrol, sisteme hiç 18 yaşında kullanıcı girmediği
sürece arıza üretmez — kusur oradadır ama uykudadır. Yalnızca o koşul
tetiklendiğinde (tam 18 yaşında kullanıcı) arızaya dönüşür.

📌 **Sık yapılan hata:** "Kod kusurluysa mutlaka arıza verir" sanmak.
Tetiklenmeyen kusur arıza üretmez.

🔗 [1.2 §1 Error–Defect–Failure zinciri](1.2-test-neden-gerekli.md)

### Soru 2 — Defect olmadan failure olabilir mi? İki sebep say.

**Kısa cevap:** **Evet — çevresel koşullar** arıza üretebilir. Örn. radyasyon,
elektromanyetik alan, güç dalgalanması, donanım arızası, kirlilik.

**Ayrıntı:** Her failure bir defect'ten gelmez. Kod kusursuz olsa bile dış
etkenler (radyasyon bir bit'i çevirir, güç dalgalanması hesabı bozar) çalışma
anında arıza üretebilir. Bu madde sınavda özellikle `A failure is always caused
by a defect` ifadesini **yanlış** yapan noktadır.

📌 **Sık yapılan hata:** Arızayı her zaman koda bağlamak. Çevresel arızalar
kodda kusur olmadan da oluşur.

🔗 [1.2 §1 Zincirin üç özelliği (2. madde)](1.2-test-neden-gerekli.md)

### Soru 3 — "Testing is a form of QA" ifadesindeki hata nedir?

**Kısa cevap:** Test **QC**'nin (kalite kontrol, ürün odaklı) parçasıdır; **QA**
(süreç odaklı, önleyici) ayrı bir şeydir.

**Ayrıntı:** Hiyerarşi: QA süreç odaklı ve önleyicidir; QC ürün odaklı ve
düzelticidir; **test QC'nin içindedir**. "Test bir QA biçimidir" demek bu
hiyerarşiyi bozar — test ürüne bakar (QC), QA ise sürecin doğru izlendiğini
güvenceye alır.

📌 **Sık yapılan hata:** QA ile QC'yi eş anlamlı kullanmak. QA = süreç
(önleyici), QC = ürün (düzeltici), test QC'nin bir parçası.

🔗 [1.2 §4 Test ile Quality Assurance](1.2-test-neden-gerekli.md)

### Soru 4 — Root cause ile defect arasındaki fark nedir? Kendi cümlenle yaz.

**Kısa cevap:** **Defect** iş ürününün içindeki kusurdur (kod satırı); **root
cause** o kusuru doğuran en derindeki sebeptir (genelde bir süreç/iletişim
eksikliği).

**Ayrıntı:** Zinciri geriye sürersin: arıza → kusurlu kod satırı (defect) →
geliştiricinin yanlış anlaması (error) → **kök neden:** iş kuralı dokümanının
belirsiz olması ve review edilmemesi. Defect'i düzeltmek anlık, kök nedeni
düzeltmek **önleyicidir** — aynı sınıftan gelecek kusurları engeller.

📌 **Sık yapılan hata:** Kök nedeni kod satırı sanmak. Kod satırı defect'tir;
kök neden genelde bir süreç/iletişim/eğitim eksikliğidir.

🔗 [1.2 §2 Root cause](1.2-test-neden-gerekli.md)

### Soru 5 — Kusur maliyeti neden üretimde en yüksektir? Üç maliyet kalemi say.

**Kısa cevap:** Üretimde bulunan kusur **yeniden geliştirme + yeniden test +
itibar (ve olası yasal) maliyeti** getirir.

**Ayrıntı:** "Erken test = ucuz test" ilkesi: gereksinim aşamasında yakalanan
kusur neredeyse bedava düzeltilir. Üretime kaçan kusur ise düzeltmenin
kendisinin ötesinde masraf üretir — kodun yeniden geliştirilmesi, yeniden test
edilmesi, kullanıcı nezdinde itibar kaybı, regüle sektörde yasal sorumluluk.
Kusur ne kadar geç bulunursa maliyeti o kadar katlanır.

📌 **Sık yapılan hata:** Maliyeti yalnızca "düzeltme eforu" sanmak. Geç kusurun
asıl bedeli yeniden test, itibar ve yasal kalemlerdir.

🔗 [1.2 §3 Testin katkıları (erken test)](1.2-test-neden-gerekli.md)

## 1.3 Yedi test prensibi

### Soru 1 — Yedi prensibi sırayla, tek cümleyle yaz. Kâğıda bakmadan.

**Kısa cevap:** **(1)** Test kusurun varlığını gösterir, yokluğunu değil ·
**(2)** Kapsamlı (exhaustive) test imkânsızdır · **(3)** Erken test kazandırır
(shift left) · **(4)** Kusurlar kümelenir (defect clustering) · **(5)** Testler
yıpranır (pesticide paradox) · **(6)** Test bağlama bağlıdır · **(7)** Kusur
yokluğu yanılgısı (absence-of-defects fallacy).

**Ayrıntı:** Zincir mantığı: bulursam vardır (1) → hepsini deneyemem (2) → erken
başlarım (3) → çoğu şurada (4) → aynı testler yıpranır (5) → bağlama göre
değişir (6) → kusursuz da işe yaramayabilir (7).

📌 **Sık yapılan hata:** Prensipleri ezberleyip senaryoya bağlayamamak. Sınav
prensibi ismiyle değil, **durum anlatarak** sorar.

🔗 [1.3 Yedi prensip](1.3-test-prensipleri.md)

### Soru 2 — Prensip 1 ile prensip 7'nin farkını iki cümlede açıkla.

**Kısa cevap:** **Prensip 1 kanıtla** ilgilidir (test kusur bulamamak
"kusursuz" demek değildir), **prensip 7 değerle** ilgilidir (kusursuz bir ürün
bile kullanıcı ihtiyacını karşılamıyorsa işe yaramaz).

**Ayrıntı:** "Zero defects found → product is fine" **prensip 1**'i ihlal eder
(kanıt iddiası). "All defects fixed but users unhappy" **prensip 7**'yi
gösterir (verification yetmedi, validation gerekliydi). Biri "kusur bulamadım"
iddiasının sınırı, diğeri "kusursuz = değerli" yanılgısının sınırıdır.

📌 **Sık yapılan hata:** İkisini de "test işe yaramaz" diye okumak. Prensip 1
kanıt sınırı, prensip 7 kullanıcı değeri hakkındadır.

🔗 [1.3 Prensipleri hızlı ayırt etme tablosu](1.3-test-prensipleri.md)

### Soru 3 — Prensip 2 hangi iki bölümdeki (4 ve 5) uygulamanın gerekçesidir?

**Kısa cevap:** Kapsamlı test imkânsız olduğu için **test tekniklerini** (Bölüm
4) ve **risk tabanlı önceliklendirmeyi** (Bölüm 5) kullanırız.

**Ayrıntı:** Her girdiyi deneyemeyeceğimize göre eforu akıllıca dağıtmak gerekir:
Bölüm 4'ün teknikleri (eşdeğerlik bölümleme, sınır değer…) test sayısını
düşürüp kapsamı korur; Bölüm 5'in risk analizi ve önceliklendirmesi eforu en
riskli yere yönlendirir. İkisi de "hepsini test edemem" gerçeğinin sonucudur.

📌 **Sık yapılan hata:** Prensip 2'yi soyut bir uyarı sanmak. Aslında tüm test
tasarımı ve önceliklendirmenin **gerekçesidir**.

🔗 [1.3 Prensip 2 (exhaustive test)](1.3-test-prensipleri.md)

### Soru 4 — "Pesticide paradox" regresyon testinin işe yaramadığını mı söyler? Cevabını gerekçelendir.

**Kısa cevap:** **Hayır.** Aynı testlerin **tekrar tekrar aynı kusurları**
bulduktan sonra yeni kusur bulamamaya başlamasını söyler; çözüm testleri
**gözden geçirip yenilemektir**, regresyonu bırakmak değil.

**Ayrıntı:** Pesticide paradox, sabit bir test setinin zamanla "yıprandığını" —
bulacağını bulup daha fazlasını bulamadığını — anlatır. Bu, regresyon testinin
gereksiz olduğu anlamına gelmez; regresyon başka bir işi yapar (değişikliğin
eski çalışanı bozmadığını gösterir). Prensip yalnızca test setinin düzenli
**güncellenmesi** gerektiğini söyler.

📌 **Sık yapılan hata:** "Testler yıpranıyorsa regresyonu bırak" sonucuna
varmak. Prensip testleri **yenilemeyi** ister, terk etmeyi değil.

🔗 [1.3 Prensip 5 (pesticide paradox, ince nokta)](1.3-test-prensipleri.md)

### Soru 5 — Prensip 4 bir test yöneticisinin hangi kararını değiştirir?

**Kısa cevap:** **Eforu nereye yoğunlaştıracağı** kararını: kusurlar kümelendiği
için test yöneticisi kaynağı **en çok kusur beklenen/riskli modüllere** ayırır.

**Ayrıntı:** Defect clustering — az sayıda modül kusurların çoğunu barındırır.
Bunu bilen test yöneticisi eforu eşit dağıtmaz; geçmişte sorunlu, karmaşık ya
da yeni değişmiş modüllere daha çok test ayırır. Bu, risk tabanlı testin (Bölüm
5) çıkış noktalarından biridir.

📌 **Sık yapılan hata:** Test eforunu her modüle eşit dağıtmak. Prensip 4 eforun
**kümelenmeye göre** dağıtılmasını söyler.

🔗 [1.3 Prensip 4 (defect clustering)](1.3-test-prensipleri.md)

## 1.4 Test aktiviteleri, testware ve roller

### Soru 1 — Yedi test aktivitesini sırayla yaz ve her birinin ürettiği ana testware'i yanına ekle.

**Kısa cevap:** **Planning → test plan · Monitoring & control → progress report/
kontrol kararları · Analysis → test conditions · Design → test cases · Implementation
→ test procedures/suites/data · Execution → results/logs/defect reports ·
Completion → completion report.**

**Ayrıntı:** Sıra ve çıktı birlikte sorulur. Analiz "neyi" test edeceğini
belirler (test conditions), tasarım "nasıl" (test cases), gerçekleştirme
koşulabilir hale getirir (procedures, data, ortam), yürütme sonuçları ve kusur
raporlarını üretir, tamamlama kapanış raporuyla testware'i arşivler.

📌 **Sık yapılan hata:** Analiz ile tasarımın çıktısını karıştırmak. Analiz →
test conditions (ne), tasarım → test cases (nasıl).

🔗 [1.4 §1 Yedi aktivite ve testware](1.4-test-aktiviteleri.md)

### Soru 2 — Test analysis ile test design farkını tek cümlede yaz.

**Kısa cevap:** **Analiz "NEYİ" test edeceğini belirler** (test conditions),
**tasarım "NASIL" test edeceğini** (test cases).

**Ayrıntı:** Hafıza kancası: analiz**A** = **NE**, desig**N** = **N**asıl.
Ek ince nokta: test analizi aynı zamanda **statik test** yapar — test
tabanındaki belirsizlik, eksiklik ve çelişkileri bulur, bu yüzden çıktısında
kusur raporları da olur.

📌 **Sık yapılan hata:** Analizi yalnızca "okuma", tasarımı "yazma" sanmak.
Analizin kendisi test conditions üretir ve statik kusur da bulur.

🔗 [1.4 §1 Test analysis vs test design](1.4-test-aktiviteleri.md)

### Soru 3 — Test data hangi aktivitede *gereksinimi belirlenir*, hangi aktivitede *hazırlanır*?

**Kısa cevap:** **Gereksinimi test design'da belirlenir, hazırlanması test
implementation'da** yapılır.

**Ayrıntı:** Tasarım test case'i yazarken hangi test verisine ihtiyaç olduğunu
(test data requirements) ortaya koyar; gerçekleştirme (implementation) o veriyi
gerçekten üretir, test ortamını kurar ve testi koşulabilir hale getirir.

📌 **Sık yapılan hata:** Veri gereksinimi ile verinin hazırlanmasını tek
aktiviteye koymak. Biri tasarımda tanımlanır, öteki gerçekleştirmede üretilir.

🔗 [1.4 §1 Design ve implementation çıktıları](1.4-test-aktiviteleri.md)

### Soru 4 — İzlenebilirliğin dört faydasını say.

**Kısa cevap:** **Kapsam ölçme · etki analizi · denetlenebilirlik (audit) ·
ilerleme/durum raporlama.**

**Ayrıntı:** Test tabanı ile test öğeleri arasındaki izlenebilirlik: hangi
gereksinimin test edildiğini gösterir (kapsam), bir değişikliğin hangi testleri
etkilediğini gösterir (etki analizi), regüle ortamda denetim için kanıt sağlar,
ve "ne kadarı test edildi/geçti" durumunu raporlamayı mümkün kılar.

📌 **Sık yapılan hata:** İzlenebilirliği yalnızca "kapsam" ile eşitlemek.
Etki analizi, denetim ve raporlama da onun faydalarıdır.

🔗 [1.4 İzlenebilirlik](1.4-test-aktiviteleri.md)

### Soru 5 — Çevik bir ekipte test manager yoksa test yönetimi işleri ne olur?

**Kısa cevap:** **Ortadan kalkmaz; ekibe (developer, PO, tester) dağıtılır.**

**Ayrıntı:** Test yönetimi bir **rol** değil, bir dizi **iştir** (planlama,
izleme, kontrol). Ayrı bir test manager olmadığında bu işler kaybolmaz;
whole-team yaklaşımında ekip üyeleri arasında paylaşılır — planlamayı ekip
birlikte yapar, izlemeyi herkes taşır.

📌 **Sık yapılan hata:** "Test manager yoksa test yönetimi de yoktur" sanmak.
İşler role değil, ekibe bağlıdır ve dağıtılır.

🔗 [1.4 §2 Roller (whole team)](1.4-test-aktiviteleri.md)

## 1.5 Temel beceriler, whole team yaklaşımı ve bağımsızlık

### Soru 1 — Bağımsızlığın dört derecesini sırayla yaz ve her birine bir örnek ver.

**Kısa cevap:** **(1)** Geliştirici kendi kodunu test eder · **(2)** Aynı
ekipteki başka bir kişi test eder · **(3)** Organizasyon içi ayrı test ekibi ·
**(4)** Organizasyon dışı (dış ekip, sertifikasyon kuruluşu, taşeron).

**Ayrıntı:** Bağımsızlık açık/kapalı değil, bir **derece** meselesidir ve sınav
**sırayı** sorar. Derece yükseldikçe farklı kusur tipleri bulunur ama ürün
bilgisi azalır. Derece 4 güvenlik/regülasyon gerektiren sistemlerde kullanılır.

📌 **Sık yapılan hata:** "Aynı ekipteki başka tester" ile "organizasyon içi ayrı
ekip"i karıştırmak. İkincisi (derece 3) ekipten **ayrı** bir gruptur; aynı
ekipteki başka kişi derece 2'dir.

🔗 [1.5 Bağımsızlık dereceleri](1.5-beceriler.md)

### Soru 2 — Yüksek bağımsızlığın üç dezavantajını say.

**Kısa cevap:** **Ekipten kopukluk (iletişim/işbirliği azalır) · ürün bilgisi
eksikliği · darboğaz ve gecikme** (geliştirici testçiden uzaklaşır, sorumluluğu
"onlar test eder" diye devreder).

**Ayrıntı:** Bağımsızlık farklı kusurları bulmayı sağlar ama bedeli vardır: ayrı
ekip ürünü daha az tanır, geri bildirim döngüsü yavaşlar, ve geliştiriciler
kaliteyi "test ekibinin işi" görüp kendi sorumluluğunu bırakabilir.

📌 **Sık yapılan hata:** Bağımsızlığı her zaman "daha iyi" sanmak. Yüksek
bağımsızlık kusur çeşitliliği kazandırırken bilgi ve hız kaybettirir.

🔗 [1.5 Bağımsızlığın avantaj/dezavantajı](1.5-beceriler.md)

### Soru 3 — Whole team approach neden her bağlamda uygun değildir?

**Kısa cevap:** Çünkü **test bağlama bağlıdır** (prensip 6): güvenlik/regülasyon
gerektiren sistemler **yüksek derecede bağımsızlık** ister, whole team ise
düşük bağımsızlık demektir.

**Ayrıntı:** Whole team'de herkes kaliteden sorumludur ve testçi ekibin
içindedir — bu işbirliğini artırır ama bağımsızlığı düşürür. Kritik alanlarda
(tıbbi, havacılık) bağımsız doğrulama zorunlu olabilir; orada whole team tek
başına yetmez.

📌 **Sık yapılan hata:** Whole team'i her yerde en iyi yaklaşım sanmak. Bağlam
(risk, regülasyon) bağımsızlık derecesini belirler.

🔗 [1.5 Whole team ile bağımsızlık](1.5-beceriler.md)

### Soru 4 — Kusur raporunda kaçınman gereken üç ifade tipini yaz.

**Kısa cevap:** **Suçlayıcı/kişisel ifadeler · duygusal-öznel yorumlar · belirsiz/
kanıtsız iddialar.** Rapor kişiyi değil, **gözlemlenen davranışı** anlatmalı.

**Ayrıntı:** İyi kusur raporu nesneldir: yeniden üretme adımları, beklenen ve
gözlenen sonuç. Kaçınılması gerekenler — "geliştirici yine batırmış" (suçlayıcı),
"berbat bir ekran" (duygusal), "bazen çalışmıyor" (belirsiz). Amaç düzeltilebilir
kılmaktır, suçlamak değil.

📌 **Sık yapılan hata:** Raporu bir şikâyet gibi yazmak. Kusur raporu kişiye
değil, tekrarlanabilir davranışa odaklanır.

🔗 [1.5 Kişilerarası beceriler / kusur raporu](1.5-beceriler.md)

### Soru 5 — Teknik bilgi dışında bir testerın hangi üç becerisi müfredatta sayılıyor?

**Kısa cevap:** **İletişim · analitik/eleştirel düşünme · alan (domain) bilgisi**
(ayrıca dikkat/titizlik ve takım çalışması).

**Ayrıntı:** Müfredat testçiliğin yalnızca teknik olmadığını vurgular: bulguları
net aktarabilmek (iletişim), bir durumu sorgulayıp varsayımları test edebilmek
(eleştirel düşünme), ve test edilen işi anlamak (domain bilgisi) sayılan temel
becerilerdendir.

📌 **Sık yapılan hata:** Testçiliği salt teknik beceri sanmak. İletişim ve
eleştirel düşünme müfredatta açıkça sayılır.

🔗 [1.5 Temel beceriler](1.5-beceriler.md)
