# 05 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 5.1a Test planlama ve giriş/çıkış kriterleri

### Soru 1 — Üç entry ve üç exit kriteri örneği yaz.

**Kısa cevap:** **Entry** = başlamaya hazır mıyız (test ortamı hazır · kod
teslim edildi · araç ve veri mevcut). **Exit** = bitirmeye hazır mıyız
(planlanan testler koşuldu · kapsam seviyesine ulaşıldı · kalan risk kabul
edilebilir).

**Ayrıntı:** Entry criteria bir aktiviteye **başlamak için** ön koşulları
sorar: test ortamı hazır, test edilebilir kod teslim edildi, gerekli araç ve
veri mevcut, testware hazır. Exit criteria aktiviteyi **normal olarak
sonlandırmak için** koşulları sorar: planlanan testler koşuldu, tanımlı kapsam
seviyesine ulaşıldı, çözülmemiş kusur sayısı eşiğin altında, kalan risk kabul
edilebilir, fonksiyonel olmayan kalite seviyeleri yeterli.

📌 **Sık yapılan hata:** Entry ile exit örneklerini karıştırmak. Entry
**girişi** (başlamaya hazırlık), exit **çıkışı** (bitirmeye hazırlık) kontrol
eder.

🔗 [5.1a §3 Giriş ve çıkış kriterleri](5.1a-test-planlama.md)

### Soru 2 — DoR ve DoD hangi kavramların çevik karşılıklarıdır?

**Kısa cevap:** **DoR (Definition of Ready) = entry criteria** · **DoD
(Definition of Done) = exit criteria.**

**Ayrıntı:** Çevik ekipler entry/exit kriterlerini iki tanımla ifade eder.
Definition of Ready, bir story'nin sprint'e alınabilmesi için sağlanması
gereken ön koşullardır — yani **entry criteria**'nın çevik karşılığı. Definition
of Done, story'nin bitmiş sayılması için sağlanması gereken koşullardır —
**exit criteria**'nın çevik karşılığı. Hafıza kancası: **Ready = başla, Done =
bitir.**

📌 **Sık yapılan hata:** İkisini ters eşlemek. Sınav "DoD hangi kavramın
karşılığıdır?" diye çeviriyi ters vererek sorar; DoD = exit, DoR = entry.

🔗 [5.1a §3 Çevik karşılık (DoR/DoD)](5.1a-test-planlama.md)

### Soru 3 — Testerın iterasyon planlamasına dört katkısını say.

**Kısa cevap:** **User story'lerin detaylı risk analizi · testlenebilirliği
belirlemek · story'leri (test) görevlerine bölmek · test kestirimi yapmak ve
test conditions belirlemek.**

**Ayrıntı:** Iteration (sprint) planlamasında tester tek bir iterasyona
odaklanır: user story'leri detaylı risk analizinden geçirir, testlenebilirliği
saptar, story'leri görevlere (özellikle test görevlerine) böler, test
kestirimi yapar ve test edilecek test conditions'ı belirler. Release
planlamasındaki daha üst seviyeli katkıyla (backlog yazımı, test yaklaşımı)
karıştırılmamalı.

📌 **Sık yapılan hata:** Katkıyı yalnızca "kaç gün sürer" tahmini sanmak. Risk
analizi ve testlenebilirlik en az kestirim kadar önemli katkılardır.

🔗 [5.1a §2 Testerın iterasyon planlamasına katkısı](5.1a-test-planlama.md)

### Soru 4 — Bir çıkış kriteri karşılanmazsa iki geçerli seçenek nedir?

**Kısa cevap:** **(1) Teste devam et** (daha fazla test koş, kusurları düzelt)
ya da **(2) kriteri bilinçli olarak gevşet** — ve kalan riski paydaşlara açıkça
bildir.

**Ayrıntı:** Çıkış kriteri karşılanmadığında iki yol vardır. Ya test sürdürülür
(ek test, kusur düzeltme), ya da kriter gevşetilir. İkinci seçenek bir
**yönetim kararıdır**, testerın kararı değil, ve **bilinçli** olmalıdır: hangi
kriterin gevşetildiği ve doğan **kalan risk (residual risk)** paydaşlara açıkça
raporlanmalıdır.

📌 **Sık yapılan hata:** "Sürümü çıkaramayız" diyerek kararı üstlenmek. Testerın
işi kararı vermek değil, **riski görünür kılmaktır**.

🔗 [5.1a §3 Çıkış kriterlerinin karşılanmaması](5.1a-test-planlama.md)

### Soru 5 — "Süre doldu, test bitti" ifadesindeki hata nedir?

**Kısa cevap:** **Zaman/bütçe bitmesi bir çıkış kriteri değildir.** Buna "test
tükendi" denir, "test tamamlandı" denmez.

**Ayrıntı:** Testin normal bitişi çıkış kriterlerinin (planlanan testler
koşuldu, kapsam sağlandı, kalan risk kabul edilebilir) karşılanmasıyla olur.
Süre dolduğu için testi durdurmak bu kriterleri karşılamaz; test hedefine
ulaşılmadan kesilmiştir. Bu durumda **kalan risk** mutlaka değerlendirilip
paydaşlara raporlanmalıdır.

📌 **Sık yapılan hata:** "Zaman doldu" ile "test tamamlandı"yı eş saymak. Zaman
bitmesi çıkış kriteri değildir; kalan risk raporlanmalıdır.

🔗 [5.1a §3 Çıkış kriteri tuzağı](5.1a-test-planlama.md)

## 5.1b Kestirim, önceliklendirme, test piramidi ve quadrant

### Soru 1 — a=5, m=8, b=17 için E ve SD nedir?

**Kısa cevap:** **E = 9, SD = 2** → **9 ± 2.**

**Ayrıntı:** Three-point estimation formülü **E = (a + 4m + b) / 6** ve
**SD = (b − a) / 6**. Hesap:

```
E  = (5 + 4×8 + 17) / 6 = (5 + 32 + 17) / 6 = 54 / 6 = 9
SD = (17 − 5) / 6 = 12 / 6 = 2
```

→ Sonuç **9 ± 2**. Dikkat: `m` mutlaka **4 ile** çarpılır, payda **6**'dır.

📌 **Sık yapılan hata:** `m`'yi 4 ile çarpmayı unutup düz ortalama almak:
(5+8+17)/3 = 10 verirdi — **yanlış**. Payda da 4 veya 3 değil, **6**'dır.

🔗 [5.1b §1 Three-point estimation](5.1b-kestirim-piramit.md)

### Soru 2 — Ratio-based ile extrapolation arasındaki farkı tek cümlede yaz.

**Kısa cevap:** **Ratio/metrics-based geçmiş projelerin** verisinden oran
çıkarır; **extrapolation mevcut projenin** erken ölçümlerinden ileriye tahmin
yapar.

**Ayrıntı:** İkisinin de kaynağı geçmiş veridir ama kaynağın **hangi proje**
olduğu farklıdır. Ratio-based, benzer geçmiş projelerin verisinden ("test
eforu geliştirmenin %35'iydi") oran çıkarıp yeni projeye uygular.
Extrapolation, aynı ve devam eden projedeki ilk ölçümleri (örn. ilk 3 sprint'in
hızı) kullanarak kalanı hesaplar.

📌 **Sık yapılan hata:** İkisini "geçmiş veriye dayanır" diye eşitlemek. Ayrım
**geçmiş başka proje** (ratio) mi, **mevcut projenin erken ölçümü**
(extrapolation) mi olduğudur.

🔗 [5.1b §1 Kestirim teknikleri](5.1b-kestirim-piramit.md)

### Soru 3 — Üç önceliklendirme stratejisini yaz. Hangisi en yaygındır?

**Kısa cevap:** **Risk-based · coverage-based · requirements-based.** En yaygın
ve genelde en iyi seçim **risk-based**'dir.

**Ayrıntı:** Risk-based en yüksek riskli testleri önce koşar. Coverage-based en
yüksek kapsamı sağlayan testleri önce koşar. Requirements-based en yüksek
öncelikli gereksinimleri kapsayan testleri önce koşar. En yaygını risk-based
önceliklendirmedir. Kritik kısıt: ideal sıra **test bağımlılıklarıyla**
bozulabilir — bu yüzden "en riskli test her zaman ilk koşulur" **yanlıştır**.

📌 **Sık yapılan hata:** Coverage-based ile requirements-based'i karıştırmak.
Coverage kapsama, requirements paydaşın gereksinim önceliğine bakar.

🔗 [5.1b §2 Test case önceliklendirme](5.1b-kestirim-piramit.md)

### Soru 4 — Test piramidinde alt katmanın dört özelliğini say.

**Kısa cevap:** **Çok sayıda · hızlı · ucuz · ince granülerlik** (izole birim/
bileşen testleri).

**Ayrıntı:** Piramidin alt katmanı (unit/component) çok sayıda, hızlı koşan,
ucuz ve ince granülerlikte (izole, küçük kapsamlı) testlerden oluşur; bol
yazılır, sık koşulur, hızlı geri bildirim verir. Yukarı çıkıldıkça testler
azalır, yavaşlar, pahalılaşır ve kabalaşır (E2E/UI). Hafıza kancası: **aşağı
indikçe daha çok, daha hızlı, daha ucuz, daha ince.**

📌 **Sık yapılan hata:** Çok UI testi, az birim testi olan **ters piramidi (ice
cream cone)** normal sanmak. O bir anti-pattern'dir; alt katman kalabalık olur.

🔗 [5.1b §3 Test piramidi](5.1b-kestirim-piramit.md)

### Soru 5 — Keşif testi hangi quadrant'ta? Ya performans testi? İkisinin ortak ekseni ne?

**Kısa cevap:** **Keşif testi Q3**, **performans testi Q4**. Ortak eksenleri:
ikisi de **ürünü eleştiren (product-critiquing)** taraftadır.

**Ayrıntı:** Q3 iş tarafına dönük ve ürünü eleştiren, **manuel** testlerdir:
keşif testi, kullanılabilirlik testi, UAT. Q4 teknolojiye dönük ve ürünü
eleştiren, **araç gerektiren** testlerdir: performans, yük, stres, güvenlik.
İkisi dikey eksende farklıdır (iş ↔ teknoloji) ama yatay eksende aynıdır —
her ikisi de **ürünü eleştirir**.

📌 **Sık yapılan hata:** Q3'ün otomatik olabileceğini sanmak. Q3 **manueldir**;
Q4 ise araçlarla/otomatik yürütülür.

🔗 [5.1b §4 Testing quadrants](5.1b-kestirim-piramit.md)

## 5.2 Risk yönetimi

### Soru 1 — Üç proje riski ve üç ürün riski örneği yaz.

**Kısa cevap:** **Proje riski:** test ortamı zamanında hazır olmaz · tedarikçi
teslimatta gecikir · personelde beceri eksikliği. **Ürün riski:** hesaplama
yanlış sonuç verir · sistem veri kaybına yol açar · güvenlik/performans
gereksinimi karşılanmaz.

**Ayrıntı:** Proje riski **projenin yönetimi ve kontrolüyle** ilgilidir
(organizasyonel, iletişim, tedarikçi, teknik-kapsam) ve **yönetim** tarafından
ele alınır. Ürün riski **iş ürününün kalitesiyle** ilgilidir (yanlış hesap,
veri kaybı/bozulması, arayüzün yanlış bilgi göstermesi, fonksiyonel/fonksiyonel
olmayan gereksinimin karşılanmaması) ve **test** tarafından doğrudan azaltılır.

📌 **Sık yapılan hata:** "Test ortamı zamanında hazır olmayabilir"i ürün riski
sanmak. Bu bir **proje riskidir** — ürünün kalitesiyle değil, projenin
yürütülmesiyle ilgilidir.

🔗 [5.2 §2 Proje riski ve ürün riski](5.2-risk-yonetimi.md)

### Soru 2 — Olasılığı 5, etkisi 1 olan bir riskle; olasılığı 2, etkisi 5 olan bir riski karşılaştır.

**Kısa cevap:** Risk seviyesi = **olasılık × etki**. İlki 5×1 = **5**, ikincisi
2×5 = **10** → **ikinci risk daha yüksektir**, düşük olasılığına rağmen.

**Ayrıntı:** Risk seviyesi iki çarpanın çarpımıdır. Sık oluşan (olasılık 5) ama
etkisi önemsiz (etki 1) bir sorun düşük risklidir (5). Nadir oluşan (olasılık 2)
ama etkisi ağır (etki 5) bir sorun daha yüksek risklidir (10) ve daha çok test
eforu hak eder. **Yüksek olasılık tek başına yüksek risk anlamına gelmez** —
etki de gerekir.

📌 **Sık yapılan hata:** Sadece olasılığa bakıp ilk riski daha önemli sanmak.
İki çarpan da hesaba katılır; etki düşükse risk de düşüktür.

🔗 [5.2 §1 Risk seviyesi = olasılık × etki](5.2-risk-yonetimi.md)

### Soru 3 — Ürün risk analizinin iki adımını ve risk kontrolünün iki adımını yaz.

**Kısa cevap:** **Analiz = risk identification + risk assessment.** **Kontrol =
risk mitigation + risk monitoring.**

**Ayrıntı:** Ürün risk **analizi** iki adımdır: identification (mümkün olduğunca
çok riski listelemek) ve assessment (riskleri kategorize etmek, olasılık/etki
belirlemek, risk seviyesini hesaplamak, önceliklendirmek, nasıl ele
alınacağını önermek). Ürün risk **kontrolü** de iki adımdır: mitigation
(önerilen tedbirleri hayata geçirmek — teknik seçimi, kapsam, bağımsızlık,
review, önceliklendirme) ve monitoring (tedbirlerin işe yarayıp yaramadığını
kontrol etmek, yeni riskleri saptamak, seviyeleri güncellemek, kalan riski
izlemek).

📌 **Sık yapılan hata:** Identification ile assessment'ı karıştırmak.
Identification **listeler**, assessment **değerlendirir ve hesaplar**.

🔗 [5.2 §3–4 Risk analizi ve kontrolü](5.2-risk-yonetimi.md)

### Soru 4 — Yüksek risk seviyesi test yaklaşımını hangi üç şekilde değiştirir?

**Kısa cevap:** **Daha erken test · daha güçlü/titiz teknik · daha bağımsız
tester** (ve daha derin kapsam).

**Ayrıntı:** Yüksek riskli alanlarda test yaklaşımı yoğunlaşır: test daha erken
başlatılır (shift left), daha güçlü teknikler uygulanır (örn. %100 branch
coverage), daha yüksek bağımsızlık derecesindeki tester'lara test yaptırılır ve
kapsam derinleştirilir. Düşük riskli alanlarda ise daha az efor, daha hafif
teknik uygulanır; gerekirse alan hiç test edilmez (kabul edilen risk).

📌 **Sık yapılan hata:** Yüksek riski sadece "daha çok test" diye özetlemek.
Değişen şey ayrıca **tekniğin gücü, testerın bağımsızlığı ve testin
zamanlamasıdır**.

🔗 [5.2 §3 Ürün risk analizi (risk yanıtı)](5.2-risk-yonetimi.md)

### Soru 5 — Residual risk nedir ve ne yapılmalıdır?

**Kısa cevap:** **Residual risk = testten sonra kalan risktir.** Test riski
sıfırlayamaz; kalan risk **paydaşlara raporlanmalıdır**.

**Ayrıntı:** Test, risk seviyesini azaltır ama asla sıfırlamaz — çünkü kapsamlı
test imkânsızdır (prensip 2). Tüm planlı testler koşulduktan sonra bile bir
miktar risk kalır; buna residual risk denir. Bu, "kalan risk kabul edilebilir"
çıkış kriteriyle (5.1a) doğrudan bağlantılıdır ve paydaşlara açıkça
bildirilmesi, onların bilinçli karar vermesini sağlar.

📌 **Sık yapılan hata:** "Test tüm riskleri ortadan kaldırır" sanmak. Test riski
azaltır, sıfırlamaz; kalan risk raporlanır.

🔗 [5.2 §4 Ürün risk kontrolü (residual risk)](5.2-risk-yonetimi.md)

## 5.3 Test izleme, kontrol, tamamlama ve raporlama

### Soru 1 — Monitoring, control ve completion'ı birer cümleyle ayır.

**Kısa cevap:** **Monitoring bilgi toplar** (neredeyiz?), **control düzeltici
eylem alır** (ne yapmalıyız?), **completion aktiviteyi kapatır** (bitti mi, ne
öğrendik?).

**Ayrıntı:** Test monitoring, test aktiviteleri hakkında sürekli metrik/bilgi
toplar ve test progress report üretir. Test control, toplanan bilgiye dayanarak
müdahale eder — yeniden önceliklendirme, kapsam/program değişikliği, kaynak
ekleme, kriter gözden geçirme. Test completion, kilometre taşında veya sonda
aktiviteyi kapatır: kusur raporlarını kapatır, completion report yazar,
testware'i arşivler, öğrenilen dersleri toplar.

📌 **Sık yapılan hata:** Monitoring ile control'ü karıştırmak. Metriği
**toplamak** monitoring, metriğe göre **eylem almak** control'dür.

🔗 [5.3 §1 Monitoring, control, completion](5.3-izleme-kontrol.md)

### Soru 2 — Test control'ün dört tipik eylemini say.

**Kısa cevap:** **Test önceliklerini yeniden düzenlemek · program/kapsamı
değiştirmek · kaynak eklemek/kaydırmak · bir giriş/çıkış kriterini gözden
geçirmek** (ve bitiş tahminini güncellemek).

**Ayrıntı:** Test control, monitoring'in ortaya koyduğu sapmaya karşı verilen
düzeltici kararlardır: yeni bir risk çıkınca testleri yeniden
önceliklendirmek, test programını veya kapsamını değiştirmek, kaynak eklemek
veya kaydırmak, bir entry/exit kriterini gözden geçirmek, testin ne zaman
biteceğine dair tahmini güncellemek. Hepsi **karar ve müdahale**dir, ölçüm
değil.

📌 **Sık yapılan hata:** Ölçme/raporlama eylemlerini (coverage ölçmek, FAIL
sayısını yazmak) control sanmak. Onlar **monitoring**'dir.

🔗 [5.3 §1 Test control'ün tipik eylemleri](5.3-izleme-kontrol.md)

### Soru 3 — Progress report ile completion report arasındaki iki içerik farkını yaz.

**Kısa cevap:** **Completion report'ta olup progress'te olmayan iki bölüm: (1)
kalan risk değerlendirmesi ve (2) öğrenilen dersler (lessons learned).**

**Ayrıntı:** Test progress report düzenli aralıklarla, test devam ederken
üretilir; içeriği dönem durumu, plandan sapmalar, engelleyiciler, sonraki dönem
planı ve metriklerdir. Test completion report sonda (test seviyesi/iterasyon/
proje bitince) üretilir ve ek olarak **kalite ve kalan risk değerlendirmesi**
ile **öğrenilen dersler/iyileştirme önerilerini** içerir. Lessons learned
yalnızca completion report'ta bulunur.

📌 **Sık yapılan hata:** Lessons learned'ı progress report'ta aramak. Öğrenilen
dersler yalnızca **completion report**'ta yer alır.

🔗 [5.3 §3 İki rapor](5.3-izleme-kontrol.md)

### Soru 4 — Beş metrik kategorisi say ve her birine bir örnek ver.

**Kısa cevap:** **Test ilerleme** (koşulan/geçen test sayısı) · **ürün
kalitesi** (MTBF, yanıt süresi) · **kusur** (bulunan/düzeltilen kusur sayısı) ·
**risk** (kalan risk seviyesi) · **kapsam** (gereksinim/kod kapsamı).

**Ayrıntı:** Müfredat metrikleri kategorilere ayırır: proje ilerleme (görev
tamamlanma, kaynak kullanımı), test ilerleme (test case hazırlama/koşulan test
sayısı), ürün kalitesi (kullanılabilirlik, yanıt süresi, MTBF), kusur (kusur
yoğunluğu, tespit oranı), risk (kalan risk seviyesi), kapsam (gereksinim
kapsamı, statement/branch coverage) ve maliyet (test maliyeti, kalite
maliyeti). Metrikler tek başına değil, **birlikte** okunur.

📌 **Sık yapılan hata:** MTBF'yi kusur metriği ya da kod kapsamını kusur
metriği sanmak. MTBF **ürün kalitesi**, branch coverage **kapsam** metriğidir.

🔗 [5.3 §2 Test metrikleri](5.3-izleme-kontrol.md)

### Soru 5 — Proje iptal edilirse test completion aktiviteleri yapılır mı? Neden?

**Kısa cevap:** **Evet, yapılır.** Proje iptali de bir tamamlama senaryosudur;
öğrenilen dersler çıkarılır ve testware arşivlenir.

**Ayrıntı:** Test completion yalnızca başarıyla biten projelerde değil, bir
kilometre taşında (sürüm/iterasyon/test seviyesi tamamlanınca), bakım sürümü
bitince ve **proje iptal edildiğinde** de yapılır. Amaç değeri korumaktır: açık
kusur raporlarını kapatmak (ya da değişiklik talebi açmak), completion report
yazmak, testware'i yeniden kullanım için arşivlemek, ortamı kapatmak/devretmek
ve **öğrenilen dersleri** toplamak. İptal edilen projeden bile öğrenilecek ders
ve tekrar kullanılacak testware vardır.

📌 **Sık yapılan hata:** İptali "hiçbir şey yapılmaz" sanmak. İptal de bir
completion tetikleyicisidir; arşivleme ve dersler yine toplanır.

🔗 [5.3 §1 Test completion ne zaman yapılır](5.3-izleme-kontrol.md)

## 5.4–5.5 Konfigürasyon yönetimi ve kusur yönetimi

### Soru 1 — Konfigürasyon yönetiminin testte sağladığı beş şeyi say.

**Kısa cevap:** **Benzersiz tanımlama · sürümleme · değişiklik izleme · öğeler
arası ilişki (izlenebilirlik) · referans verilebilirlik.**

**Ayrıntı:** Configuration management (CM), test nesnesinin ve testware'in
bütünlüğünü proje boyunca tanımlar ve korur. Sağladıkları: tüm konfigürasyon
öğeleri benzersiz tanımlanır, öğeler sürümlenir (hangi sürümü test ettiğini
bilirsin), değişiklikler izlenir (ne değişti, kim, ne zaman), öğeler arası
ilişkiler korunur (test case ↔ gereksinim ↔ kod izlenebilirliği) ve referans
verilebilirlik sağlanır ("build 1.4.2" dediğinde herkes aynı şeyi anlar). CM
sadece kaynak koduna değil, **testware'e de** uygulanır.

📌 **Sık yapılan hata:** CM'i yalnızca kaynak kodu için sanmak. Test case'ler,
test verisi, otomasyon script'leri ve test planı da CM'e tabidir.

🔗 [5.4 §1 Konfigürasyon yönetimi](5.4-konfigurasyon-kusur.md)

### Soru 2 — Severity ile priority farkını yaz ve her birine "ters kombinasyon" örneği ver.

**Kısa cevap:** **Severity = kusurun teknik zararı** (genelde tester belirler) ·
**priority = düzeltme aciliyeti** (genelde iş tarafı belirler). Ters örnekler:
nadir kullanılan modül çöküyor = **yüksek severity, düşük priority**; ana
sayfada şirket adı yanlış = **düşük severity, yüksek priority**.

**Ayrıntı:** Severity kusurun sisteme verdiği teknik hasarı ölçer ve dayanağı
teknik etkidir; genelde tester atar. Priority kusurun ne kadar acil
düzeltilmesi gerektiğini ölçer, dayanağı iş değeri/kullanıcı etkisi/takvimdir;
genelde ürün sahibi atar. İki eksen bağımsız olduğu için dört kombinasyonun
hepsi mümkündür — yılda bir kullanılan raporun çökmesi teknik olarak ağırdır
(yüksek severity) ama Mart ayında acil değildir (düşük priority); şirket adının
yanlış yazımı teknik zarar vermez (düşük severity) ama itibar nedeniyle hemen
düzeltilmelidir (yüksek priority).

📌 **Sık yapılan hata:** "Yüksek severity her zaman yüksek priority demektir"
sanmak — **yanlış**. İki eksen bağımsızdır.

🔗 [5.4 §2 Severity ve priority](5.4-konfigurasyon-kusur.md)

### Soru 3 — Kusur raporunun sekiz zorunlu alanını yaz.

**Kısa cevap:** **Tanımlayıcı · başlık/özet · tarih ve yazar · test nesnesi ve
ortam · yeniden üretme adımları · beklenen ve gerçek sonuç · severity ve
priority · durum (status)** (ayrıca referanslar, küresel etkiler, değişiklik
geçmişi).

**Ayrıntı:** Kusur raporunun tipik içeriği: benzersiz tanımlayıcı, başlık ve
kısa özet, tarih/yazar ve rolü, **test nesnesinin (konfigürasyon öğesi) ve
ortamın** tanımı, kusurun gözlendiği yaşam döngüsü fazı, **kusurun tarifi**
(yeniden üretme adımları, beklenen ve gerçek sonuç, log/ekran görüntüleri),
**severity** (kapsam/etki), **priority** (aciliyet), raporun **durumu**,
sonuçlar/öneriler, küresel etkiler, değişiklik geçmişi ve referanslar. Raporun
kalbi yeniden üretme adımları ile beklenen/gerçek sonuçtur.

📌 **Sık yapılan hata:** Ortam/build bilgisini atlamak. Test nesnesi ve ortam
olmadan geliştirici kusuru **yeniden üretemez** — bu zorunlu bir alandır.

🔗 [5.4 §3 Kusur raporunun tipik içeriği](5.4-konfigurasyon-kusur.md)

### Soru 4 — Kusur raporunda olmaması gereken üç şey nedir?

**Kısa cevap:** **Kusurun kök nedeni · düzeltmenin nasıl yapılacağı · kusuru
kimin ürettiği/suçlusu.**

**Ayrıntı:** Kök neden debugging'in sonucudur, rapor yazıldığı andaki bilgi
değildir. Düzeltmenin nasıl yapılacağı geliştiricinin işidir, raporcunun
tahmini değil. Kusuru kimin ürettiği ise 1.5'teki iletişim kuralının ihlalidir
— rapor kişiyi değil, gözlemlenen davranışı anlatmalıdır. Rapor nesnel,
tekrarlanabilir ve düzeltilebilir kılmayı hedefler; suçlamayı değil.

📌 **Sık yapılan hata:** Kök neden tahminini veya çözüm önerisini rapora
eklemek. Bunlar debugging/geliştirme işidir; rapor gözlemi belgeler.

🔗 [5.4 §3 Kusur raporu tuzağı](5.4-konfigurasyon-kusur.md)

### Soru 5 — Statik testte kusur raporu yazılır mı? Neden?

**Kısa cevap:** **Evet.** Review ve statik analiz kusuru **doğrudan** bulur;
"kusur raporları yalnızca test yürütmede yazılır" ifadesi **yanlıştır**.

**Ayrıntı:** Kusur raporu hem dinamik hem statik testte yazılır. Dinamik testte
bir arıza gözlendiğinde (2.2) rapor açılır. Statik testte ise review veya statik
analiz sırasında kusur arıza üretmeden doğrudan bulunduğunda (3.1) rapor açılır.
Dolayısıyla kusur raporlaması yalnızca yürütmeye özgü değildir.

📌 **Sık yapılan hata:** "Defect reports are only created during test execution"
demek — **yanlış**. Statik testte de kusur raporu yazılır.

🔗 [5.4 §3 Kusur raporunun yazıldığı durumlar](5.4-konfigurasyon-kusur.md)
