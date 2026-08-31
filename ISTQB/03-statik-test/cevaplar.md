# 03 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 3.1 Statik Test Temelleri

### Soru 1 — Statik test edilebilecek beş iş ürünü say. En az biri testware olsun.

**Kısa cevap:** **User story (kabul kriteriyle) · gereksinim spesifikasyonu ·
tasarım spesifikasyonu · kaynak kod · testware (test case / test planı /
otomatik test script'i).**

**Ayrıntı:** Statik testin nesnesi "okunabilir ve anlaşılabilir olan her iş
ürünüdür": gereksinimler, epic/user story/kabul kriteri, mimari ve tasarım
spesifikasyonları, kod, testware, kullanıcı kılavuzu, sözleşme, hatta BPMN gibi
modeller. **Testware'in kendisi de statik test edilebilir** — bir test case'i
review etmek statik testtir.

📌 **Sık yapılan hata:** "Static testing can only be applied to source code"
sanmak. En değerli uygulaması gereksinim ve user story review'udur; testware
dahil her okunabilir ürün statik test edilebilir.

🔗 [3.1 §1 Statik testle incelenebilen iş ürünleri](3.1-statik-test-temelleri.md)

### Soru 2 — Statik test neden debugging gerektirmez?

**Kısa cevap:** Çünkü statik test **kusuru (defect) doğrudan** gösterir; ortada
sebebi aranacak bir **arıza (failure) yoktur.**

**Ayrıntı:** Dinamik testte önce bir failure gözlersin, sonra debugging ile onu
üreten kusuru ararsın. Statik test kodu çalıştırmaz, arıza üretmez; kusuru
belgede/kodda doğrudan görür. Sebep zaten görünür olduğu için "belirtiden sebebe
git" adımı — yani debugging — gerekmez.

📌 **Sık yapılan hata:** Statik testin de "bulup sonra sebep araması gerektiğini"
sanmak. Statik testte belirti değil, doğrudan kusur görülür.

🔗 [3.1 §3 Statik ve dinamik testin farkları](3.1-statik-test-temelleri.md)

### Soru 3 — Statik testle bulunması kolay, dinamik testle çok zor üç kusur tipi yaz.

**Kısa cevap:** **Belirsiz/çelişkili gereksinim · ölü (erişilemeyen) kod ·
tanımsız değişken** (ayrıca yüksek coupling, standart ihlali, yanlış arayüz
spesifikasyonu, izlenebilirlik boşluğu).

**Ayrıntı:** Bu kusurlar çalıştırılarak bulunamaz: belirsiz bir gereksinimin
çalıştırılacak bir davranışı yoktur, ölü kod hiç çalışmadığı için dinamik testte
görünmez, tanımsız değişken statik analiz aracının alanıdır. Statik test bunları
kodu çalıştırmadan doğrudan görür.

📌 **Sık yapılan hata:** Her kusurun eninde sonunda dinamik testle çıkacağını
sanmak. Bu kusur tiplerinin çoğu dinamik testle çok zor ya da hiç bulunamaz.

🔗 [3.1 §2 Statik testin daha iyi bulduğu kusur tipleri](3.1-statik-test-temelleri.md)

### Soru 4 — Statik test performans gereksinimiyle ilgili ne yapabilir, ne yapamaz?

**Kısa cevap:** Performans **gereksinimini inceleyebilir** ("ölçülebilir mi,
belirsiz mi?"), ama **gerçek yanıt süresini ölçemez.**

**Ayrıntı:** Statik test dış çalışma davranışını (yanıt süresi, bellek, yarış
koşulu) ölçemez. Yapabildiği, gereksinimin kalitesini değerlendirmektir —
"hızlı yüklenmeli" gibi ölçülemez bir ifadeyi belirsizlik/test edilemezlik
olarak işaretler. Gerçek performansı ölçmek dinamik testin işidir.

📌 **Sık yapılan hata:** "Static testing can measure the actual response time"
şıkkını doğru sanmak. Statik test gereksinimi inceler, davranışı ölçmez.

🔗 [3.1 §3 Statik/dinamik farkları (tuzak)](3.1-statik-test-temelleri.md)

### Soru 5 — Manuel review'ın statik analiz aracına göre üstünlüğü nedir?

**Kısa cevap:** Review **anlamı, mantığı ve iş kuralını** değerlendirebilir —
"yanlış iş kuralı"nı bulur; araç bunu göremez.

**Ayrıntı:** Statik analiz aracı standart ihlali, ölü kod, karmaşıklık metriği
ve bilinen güvenlik desenlerini hızla bulur ama **anlamı** göremez; kod temiz
görünüp mantığı yanlışsa aracı geçer. İnsan review'ı belirsizliği, çelişkiyi ve
yanlış iş kuralını yakalar — aracın kör olduğu yer tam burasıdır. (Buna karşılık
araç 200 bin satırı insanın taramayacağı hızda tarar.)

📌 **Sık yapılan hata:** Aracı "her kusuru bulur" sanmak. Araç anlamı göremez;
yanlış iş kuralını yalnızca manuel review yakalar.

🔗 [3.1 §4 İki statik test tekniği](3.1-statik-test-temelleri.md)

## 3.2 Geri Bildirim ve Gözden Geçirme Süreci

### Soru 1 — Review sürecinin beş aktivitesini sırayla yaz.

**Kısa cevap:** **Planning → review initiation → individual review →
communication and analysis → fixing and reporting.**

**Ayrıntı:** Planning kapsamı, rolleri ve çıkış kriterlerini belirler;
initiation iş ürününü dağıtıp herkesin hazır olduğundan emin olur; individual
review'da katılımcılar bireysel inceler ve **anomali** kaydeder; communication
and analysis'te anomalilerin kusur olup olmadığına karar verilir; fixing and
reporting'de kusurlar düzeltilir, defect report üretilir ve çıkış kriterine göre
review raporlanır.

📌 **Sık yapılan hata:** Review'ı "bir toplantı" sanmak. Review bir **süreçtir**;
toplantı yalnızca bir adımdır ve bazı tiplerde hiç yoktur.

🔗 [3.2 §2 Review süreci — beş aktivite](3.2-gozden-gecirme.md)

### Soru 2 — Altı review rolünü yaz ve her birinin tek cümlelik sorumluluğunu ekle.

**Kısa cevap:** **Manager** (review'a karar verir, kaynak ayırır) · **review
leader** (kim katılır, ne zaman/nerede olur) · **author** (ürünü yazar ve
düzeltir) · **moderator** (toplantıyı yönetir, arabuluculuk yapar) · **scribe**
(anomalileri ve kararları kaydeder) · **reviewer** (ürünü inceler, anomali
raporlar).

**Ayrıntı:** Bir kişi birden fazla rol üstlenebilir (küçük ekipte moderator +
review leader aynı kişi); her rol her review tipinde bulunmaz. **Author asla
moderator olmamalıdır** — kendi ürününü savunma eğilimi tartışmayı bozar.
Manager kaynağı ayıran, review leader lojistiği ayarlayan kişidir; ikisi
karıştırılır.

📌 **Sık yapılan hata:** Manager ile review leader'ı karıştırmak. Manager karar
verip kaynak ayırır; leader kimin, ne zaman, nerede toplanacağını organize eder.

🔗 [3.2 §3 Review rolleri ve sorumlulukları](3.2-gozden-gecirme.md)

### Soru 3 — Dört review tipini resmiyet sırasına diz. Hangisini author yönetir?

**Kısa cevap:** **Informal review < walkthrough < technical review <
inspection.** Toplantıyı **author** yönetir → **walkthrough** (tek istisna).

**Ayrıntı:** Informal review en az resmî, dokümante edilmeyebilir; walkthrough
ortak anlayış kurar, hazırlık isteğe bağlı, **author yönetir**; technical review
teknik karar alır, bireysel hazırlık zorunlu, moderator yönetir; inspection en
resmî, giriş/çıkış kriterli, zorunlu hazırlık, metrik toplanır. Inspection ve
technical review'da author **yönetmez**.

📌 **Sık yapılan hata:** Walkthrough'u da moderator yönetiyor sanmak. Bu tek
istisna — walkthrough'u author yönetir — birçok soruyu tek başına çözer.

🔗 [3.2 §4 Review tipleri — resmiyet sırası](3.2-gozden-gecirme.md)

### Soru 4 — Inspection'ı technical review'dan ayıran iki şey nedir?

**Kısa cevap:** Inspection **en resmî** tiptir: **tanımlı giriş/çıkış kriterleri
ve metrik toplama + süreç iyileştirme** hedefi vardır; technical review'da bunlar
yoktur.

**Ayrıntı:** İkisi de moderator tarafından yönetilir ve bireysel hazırlık
zorunludur; bu yüzden karıştırılırlar. Fark: inspection maksimum anomali bulmayı
hedefler, resmî toplantı yapar, giriş ve çıkış kriterleri tanımlıdır, bulunan
anomali sayısı ve harcanan efor gibi **metrikler toplanır** ve çıktısı **süreç
iyileştirmedir**. Technical review ise teknik kararlar almaya ve teknik problem
çözmeye odaklanır.

📌 **Sık yapılan hata:** İkisini sadece "hazırlık zorunlu" diye eşitlemek. Ayırt
edici, inspection'ın metrik toplaması ve süreç iyileştirmeyi hedeflemesidir.

🔗 [3.2 §4 Review tipleri — resmiyet sırası](3.2-gozden-gecirme.md)

### Soru 5 — Neden review sonuçları performans değerlendirmesinde kullanılmamalı?

**Kısa cevap:** Çünkü **güven ortamını yok eder**: insanlar kusur bulmayı
bırakır, savunmaya geçer ve review ölür.

**Ayrıntı:** Bu, en kritik başarı faktörüdür. Review'ın amacı iş ürününü
iyileştirmektir, kişiyi değerlendirmek değil. Sonuçlar bireysel performans
değerlendirmesinde kullanılırsa katılımcılar açık davranmayı bırakır — author
savunmaya geçer, reviewer kusur bildirmekten çekinir — ve review'ın kusur bulma
değeri çöker. Sağlıklı review, sonucun bireyi değerlendirmek için
kullanılmayacağı güven ortamında olur.

📌 **Sık yapılan hata:** Bu maddeyi "isteğe bağlı bir nezaket" sanmak. Sınavın en
sık sorduğu başarı faktörüdür ve doğrudan review'ın işe yaramasının koşuludur.

🔗 [3.2 §5 Review'ın başarı faktörleri](3.2-gozden-gecirme.md)
