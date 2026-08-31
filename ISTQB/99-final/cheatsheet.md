# Cheatsheet — Tek Sayfa Özet

> **Ne zaman okunur:** Her konu tekrarında ve **sınavdan önceki hafta boyunca her gün**.
> Bu dosya öğretmez, **hatırlatır**. İlk kez buradan çalışma.

---

## ⚡ Ezberlenecek sayılar

| | |
|---|---|
| Soru sayısı / süre / geçme | **40 soru · 60 dk · 26/40 (%65)** |
| Ek süre (ana dili İngilizce olmayan) | **+%25 → 75 dk** |
| Bölüm puanları (1→6) | **8 – 5 – 4 – 11 – 9 – 3** |
| Bölüm müfredat süreleri (dk) | 180 – 130 – 80 – **390** – **335** – 20 |
| K3 (uygulama) hangi bölümlerde | **Sadece 4 ve 5** |
| Three-point formülü | **E = (a + 4m + b) / 6** · **SD = (b − a) / 6** |
| n Boolean koşul → tam karar tablosu | **2ⁿ kural** (3→8, 4→16, 5→32) |
| Risk seviyesi | **olasılık × etki** |
| Kapsam formülü | **(kapsanan item / toplam item) × 100** |
| Soru başına ortalama süre | **90 saniye** |

---

## 🔴 En çok karıştırılan ikililer

| Sorudaki ifade | Doğru | Karıştırılan |
|---|---|---|
| İnsanın yanlış anlaması | **Error** | Defect |
| Koddaki/dokümandaki hata | **Defect** (fault, bug) | Failure |
| Çalışırken görünen yanlış davranış | **Failure** | Defect |
| Kusuru doğuran en derin sebep | **Root cause** (genelde süreç) | Defect |
| Şartnameye uygun mu | **Verification** | Validation |
| Kullanıcının ihtiyacı bu mu | **Validation** | Verification |
| Süreç odaklı, önleyici | **QA** | Test |
| Ürün odaklı, düzeltici | **QC** (test onun parçası) | QA |
| Aynı testi tekrar koş | **Confirmation testing** | Regression |
| Diğer testleri koş, yan etki ara | **Regression testing** | Confirmation |
| Kusuru bul | **Testing** | Debugging |
| Kusurun sebebini bulup düzelt | **Debugging** (test aktivitesi **değil**) | Testing |
| NE test edilecek | **Test analysis** → test conditions | Test design |
| NASIL test edilecek | **Test design** → test cases | Test analysis |
| Veri/ortam hazırla, koşulabilir yap | **Test implementation** | Test design |
| Nerede test ediliyor | **Test seviyesi** | Test tipi |
| Ne test ediliyor | **Test tipi** | Test seviyesi |
| Geliştirici sahasında dış kullanıcı | **Alpha** | Beta |
| Müşteri sahasında dış kullanıcı | **Beta** | Alpha |
| Yedekleme/geri yükleme testi | **OAT** | UAT |
| Toplantıyı **author** yönetir | **Walkthrough** | Inspection |
| En resmî, metrikli, giriş/çıkış kriterli | **Inspection** | Technical review |
| Anomalileri kaydeden | **Scribe** | Moderator |
| Toplantıyı yürüten | **Moderator** | Review leader |
| Kim/ne zaman/nerede | **Review leader** | Manager |
| Review yapılmasına karar veren, kaynak ayıran | **Manager** | Review leader |
| Sınır + komşu (2 değer) | **2-value BVA** | 3-value |
| Sınır−1, sınır, sınır+1 (3 değer) | **3-value BVA** | 2-value |
| Her satır çalıştı | **Statement coverage** | Branch |
| Her karar T ve F oldu | **Branch coverage** | Statement |
| Başlamaya hazır mıyız | **Entry criteria = DoR** | Exit / DoD |
| Bitirmeye hazır mıyız | **Exit criteria = DoD** | Entry / DoR |
| Projeyi bitiremeyiz | **Proje riski** (yönetim) | Ürün riski |
| Ürün bozuk çıkar | **Ürün riski** (test azaltır) | Proje riski |
| Teknik zarar | **Severity** (tester) | Priority |
| Düzeltme aciliyeti | **Priority** (iş tarafı) | Severity |
| Metrik topla | **Test monitoring** | Test control |
| Metriğe göre eylem al | **Test control** | Test monitoring |
| Düzenli yayınlanan rapor | **Test progress report** | Completion |
| Sonda, **lessons learned** içeren | **Test completion report** | Progress |
| Geçmiş **başka** projelerin oranı | **Ratio-based** | Extrapolation |
| **Mevcut** projenin erken ölçümü | **Extrapolation** | Ratio-based |
| Birim testleri, kodu sürer | **TDD** | ATDD |
| Kabul testleri, ekipçe, koddan önce | **ATDD** | TDD |
| Given/When/Then doğal dil | **BDD** | ATDD |

---

## 📜 Yedi prensip (senaryodan tanı)

| Senaryodaki ifade | Prensip |
|---|---|
| "Kusur bulunamadı → yazılım hatasız" | **1** Presence, not absence |
| "Tüm kombinasyonları deneyelim" | **2** Exhaustive testing impossible |
| "Testerlar gereksinimi review etsin" | **3** Early testing (shift left) |
| "%78 arıza 3 modülden" | **4** Defects cluster |
| "Aynı suite 6 aydır yeni kusur bulmuyor" | **5** Tests wear out (pesticide) |
| "Tıbbi cihazın stratejisi bloga uymaz" | **6** Context dependent |
| "Kusur yok ama kullanıcı memnun değil" | **7** Absence-of-defects fallacy |

**1 vs 7:** 1 = **kanıt** hatası ("bulamadık = yok"). 7 = **değer** hatası ("kusursuz = başarılı").
**2 vs 5:** 2 = **kapsam** imkânsız. 5 = **tekrar** köreltir.

---

## 🗂 Bölüm bölüm listeler

### Bölüm 1 — Fundamentals (8 puan)

- **7 test aktivitesi:** planning · monitoring & control · analysis · design · implementation · execution · completion
- **Testware eşlemesi:** analysis→**test conditions** · design→**test cases** · implementation→**test procedures/data/ortam** ·
  execution→**test logs + defect reports** · completion→**test completion report + arşiv**
- **İzlenebilirlik zinciri:** test basis → condition → case → procedure → result
- **İzlenebilirlik faydaları:** **impact analysis** · kapsam ölçümü · denetlenebilirlik · raporlama · iş hedefleriyle ilişki
- **İki rol:** test management (süreç, kaynak, rapor) · testing (analiz, tasarım, yürütme).
  **Ayrı test manager zorunlu değil.**
- **Bağımsızlık 4 derece:** ① yazarın kendisi ② aynı ekipten biri ③ organizasyon içi ayrı ekip ④ **organizasyon dışı**
- **Bağımsızlığın dezavantajı:** izolasyon · iletişim kopması · geliştiricide **sorumluluk kaybı** · **darboğaz** · gecikme

### Bölüm 2 — SDLC (5 puan)

- **5 test seviyesi:** component → component integration → system → system integration → **acceptance**
- **Kabul testinin amacı: güven + hazır olma** (kusur bulmak değil)
- **Kabul biçimleri:** UAT · **OAT** · sözleşmesel/regülatif · **alpha** (bizde) · **beta** (onlarda)
- **Kalite karakteristikleri:** performance efficiency · compatibility · usability · **reliability** ·
  security · maintainability · portability · functional suitability
- **DevOps riski:** pipeline/otomasyon **bakım maliyeti** · otomasyon her şeyi kapsayamaz
- **Shift left kısa vadede eforu ARTIRIR**, sonraki seviyeleri **iptal etmez**
- **Regresyon tetikleyicileri:** kusur düzeltme · **yeni özellik** · **ortam/sürüm yükseltmesi** · konfigürasyon
- **Bakım tetikleyicileri:** modification · **upgrade/migration** (+veri dönüştürme testi) ·
  **retirement** (+arşivleme ve **geri yükleme** testi)
- **Bakım kapsamı 3 faktör:** değişikliğin riski · sistemin boyutu · değişikliğin boyutu

### Bölüm 3 — Static Testing (4 puan)

- **Statik test → defect'i doğrudan bulur, debugging gerekmez**
- **Nesnesi:** okunabilir ve anlaşılabilir **her** iş ürünü (user story, sözleşme, **testware** dahil)
- **Bulamadığı:** gerçek yanıt süresi, bellek sızıntısı, yarış koşulu
- **Review süreci 5 adım:** planning → initiation → **individual review** → **communication and analysis** → fixing and reporting
- **Anomali → kusur kararı:** communication and analysis'te verilir
- **6 rol:** manager (karar+kaynak) · review leader (kim/ne zaman/nerede) · author · moderator · scribe · reviewer
- **Resmiyet:** informal < walkthrough < technical review < **inspection**
- **Hazırlık zorunlu:** technical review + inspection
- **En kritik başarı faktörü:** sonuçlar **bireysel performans değerlendirmesinde kullanılmaz**

### Bölüm 4 — Analysis & Design (11 puan) ⭐

| Teknik | Coverage item | %100 kapsam |
|---|---|---|
| **EP** | Eşdeğerlik bölümü | Her **geçerli ve geçersiz** bölümden bir değer |
| **BVA** | Sınır değeri | Her sınır (2-value: 2, 3-value: 3 değer) |
| **Karar tablosu** | **Kural (sütun)** | Her kuraldan bir test |
| **Durum geçiş** | Durum / geçiş | all states < **valid transitions** < all transitions |
| **Statement** | Çalıştırılabilir ifade | Her satır bir kez |
| **Branch** | Dal | Her karar **T ve F** |

- **%100 branch ⟹ %100 statement. TERSİ DEĞİL.**
- **%100 valid transitions ⟹ %100 all states. TERSİ DEĞİL.**
- **Dal sayısı ≠ test case sayısı** — bir test birden fazla dalı kapsar
- **Geçersiz bölümler teker teker** test edilir (**fault masking**)
- **BVA ön koşulu:** bölüm **sıralanabilir** olmalı
- "between X and Y" → sınır X ve Y. "greater than X, less than Y" → sınır **X+1 ve Y−1**
- **Beyaz kutu bulamaz:** hiç yazılmamış özellik. **%100 kapsam ≠ doğruluk**
- **Deneyim tabanlı:** error guessing (**fault attack**) · **exploratory** (charter + time-box, **SBTM**) ·
  checklist-based (**tekrarda etkinliği azalır**). Ortak zayıflık: **kapsam ölçülemez**
- **3C:** Card · Conversation · **Confirmation (kabul kriterleri)**
- **INVEST:** Independent · Negotiable · Valuable · Estimable · Small · **Testable**
- **ATDD:** koddan önce, ekipçe, **önce pozitif** test case'ler. Asıl değer: **specification workshop'ta kusur önleme**

### Bölüm 5 — Managing (9 puan) ⭐

- **Entry = DoR** (başla) · **Exit = DoD** (bitir)
- **Çıkış kriteri örnekleri:** planlanan testler koşuldu · **kapsam hedefine ulaşıldı** ·
  açık kusur eşiğin altında · **kalan risk kabul edilebilir**
- **Zaman bitmesi çıkış kriteri DEĞİLDİR** — kalan risk raporlanır
- **4 kestirim tekniği:** ratio (geçmiş projeler) · extrapolation (mevcut proje) ·
  **Wideband Delphi** (planning poker) · **three-point**
- **3 önceliklendirme:** risk-based · coverage-based · requirements-based.
  **Test bağımlılıkları** sırayı bozabilir
- **Test piramidi:** aşağı indikçe **daha çok, daha hızlı, daha ucuz, daha ince**.
  Ters piramit = **anti-pattern**
- **Quadrantlar:** Q1 **birim** (teknik/destek/otomatik) · Q2 **fonksiyonel-user story** (iş/destek) ·
  Q3 **keşif-kullanılabilirlik-UAT** (iş/eleştiri/**manuel**) · Q4 **performans-güvenlik** (teknik/eleştiri/**araç**)
- **Risk analizi = identification + assessment.** **Risk kontrolü = mitigation + monitoring**
- **Risk yanıtları:** mitigate · accept · transfer · contingency plan
- **Monitoring** ölçer · **control** eylem alır · **completion** kapatır (**proje iptalinde de**)
- **Progress report:** düzenli, dönem durumu + sapmalar + sonraki plan
- **Completion report:** sonda + **kalan risk değerlendirmesi** + **lessons learned**
- **CM:** benzersiz tanımlama · sürümleme · değişiklik izleme · ilişkiler · referans verilebilirlik.
  **Testware de CM'e tabidir**
- **Severity** = teknik zarar (tester) · **Priority** = aciliyet (iş tarafı). **Dört kombinasyon da mümkün**
- **Kusur raporunda OLMAYAN:** kök neden · düzeltme talimatı · **kişi suçlama**
- **Kusur raporunda ZORUNLU:** **yeniden üretme adımları** · **beklenen ve gerçek sonuç** ·
  **test nesnesi + ortam** · severity · priority · durum

### Bölüm 6 — Tools (3 puan)

- Araç desteği **sadece yürütme otomasyonu değil**: yönetim, CM, CI/CD, statik analiz, **kapsam ölçümü**, fonksiyonel olmayan test
- **Riskler:** **gerçekçi olmayan beklentiler** · **bakım eforunun hafife alınması** ·
  araca aşırı güven · sürüm kontrolü ihmali · birlikte çalışabilirlik ihmali ·
  **satıcı riski / açık kaynak projesinin durması** · belirsiz sorumluluk
- **Otomasyon tester ihtiyacını ortadan kaldırmaz**
- **Kötü test setini otomatikleştirmek onu iyileştirmez**

---

## 🎯 Sınav refleksleri

1. `always` · `never` · `only` · `all` · `100%` · `guarantees` · `eliminates` → **şüphelen**
2. `BEST` · `MOST` · `PRIMARY` → birden fazla doğru var, **en uygunu** seç
3. `NOT` · `LEAST` · `WEAKEST` → yön kelimesinin **altını çiz**
4. `(Choose TWO.)` → **kısmi puan yok**, ikisini de işaretle
5. `FULL decision table` mı `collapsed` mı? `2-value` mı `3-value` mı? → **kelime kelime oku**
6. Boş bırakma — **yanlış cezası yok**
7. 90 saniyeden fazla harcama, **işaretle geç**
8. Cevabı **somut bir sebep** olmadan değiştirme

---

➡️ Sınavdan 24 saat önce: [`son-tekrar.md`](son-tekrar.md)
➡️ Deneme sınavları: [`deneme-1.html`](deneme-1.html) · [`deneme-2.html`](deneme-2.html)
