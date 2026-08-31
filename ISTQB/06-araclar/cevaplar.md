# 06 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 6.1–6.2 Araç desteği ve test otomasyonunun riskleri

### Soru 1 — Test araçlarının desteklediği altı alan say.

**Kısa cevap:** **Test yönetimi · konfigürasyon yönetimi · CI/CD · statik test · test yürütme · fonksiyonel olmayan test** (ayrıca gereksinim yönetimi, kusur yönetimi, kapsam ölçümü, DevOps, simülatör/emülatör).

**Ayrıntı:** Araçlar test sürecinin **her yerini** destekler, sadece yürütmeyi değil. Test yönetimi (TestRail/Xray), konfigürasyon yönetimi (Git), sürekli entegrasyon (Jenkins, GitHub Actions), statik test (linter, statik analiz), test yürütme (JUnit, Selenium), fonksiyonel olmayan test (JMeter), kapsam ölçümü (JaCoCo) ve daha fazlası — hepsi araç desteğinin kapsamındadır.

📌 **Sık yapılan hata:** Araç desteğini **sadece test yürütme otomasyonu** sanmak. "Which can be supported by tools" sorusunda statik analiz, test yönetimi ve kapsam ölçümü de doğru cevaptır.

🔗 [6.1 §1 Araçların desteklediği alanlar](6.1-araclar.md)

### Soru 2 — Otomasyonun dört faydasını ve altı riskini yaz.

**Kısa cevap:** **Faydalar:** tekrarlayan manuel işten tasarruf · tutarlılık/tekrarlanabilirlik · insan hatasının önlenmesi · daha hızlı geri bildirim. **Riskler:** gerçekçi olmayan beklentiler · zaman/maliyet/efor hafife alınması · bakım eforunun hafife alınması · araca aşırı güven · satıcı riski · açık kaynak projesinin durması.

**Ayrıntı:** Araç bir **çarpandır, çözüm değil**. Faydalar iyi bir süreci hızlandırır: makine regresyonu insan yerine tutarlı koşar, kapsam nesnel ölçülür, CI'da her commit sonrası hızlı geri bildirim gelir. Riskler ise çoğunlukla **hafife alınan efor** etrafında toplanır — kurulum, tanıtım ve özellikle **bakım** sanılandan pahalıdır; satıcı desteği kesebilir, açık kaynak proje durabilir.

📌 **Sık yapılan hata:** "Tekrarlayan manuel işi azaltır" ifadesini risk sanmak — bu bir **faydadır**. Sınav faydalardan çok riskleri sorar.

🔗 [6.1 §2–§3 Faydalar ve riskler](6.1-araclar.md)

### Soru 3 — Otomasyon tester ihtiyacını ortadan kaldırır mı? Gerekçelendir.

**Kısa cevap:** **Hayır.** Otomasyon tester ihtiyacını **ortadan kaldırmaz**; testerın işini tekrarlayan yürütmeden **tasarım, analiz ve keşif testine** kaydırır.

**Ayrıntı:** Otomasyon her şeyi kapsayamaz — keşif (exploratory) ve kullanılabilirlik testi **insan yargısı** ister. Makine testleri koşarken tester serbest kalır ve daha ilgi çekici, düşünce gerektiren işlere odaklanır. Yani rol değişir, ihtiyaç kalkmaz. Aksini iddia etmek "araca aşırı güven" riskine düşmektir.

📌 **Sık yapılan hata:** "Otomasyon tester ihtiyacını ortadan kaldırır" şıkkını doğru sanmak. İşi **kaydırır**, silmez.

🔗 [6.1 §3 Kritik cümle — otomasyonun sınırı](6.1-araclar.md)

### Soru 4 — Hangi test tipleri otomasyona kötü adaydır? İki örnek ver.

**Kısa cevap:** **Kullanılabilirlik (usability) testi** ve **tasarımı sık değişen/kararsız arayüzün testi** (ayrıca keşif testi).

**Ayrıntı:** Kullanılabilirlik **insan yargısı** gerektirir — makine "bu ekran kullanışlı mı" diyemez. Kararsız bir arayüzü otomatikleştirmek ise her değişiklikte kırılan testler, yani sürekli **bakım maliyeti** demektir; fayda maliyeti aşar. İyi aday tam tersidir: **tekrarlayan ve kararlı** testler (regresyon) ve manuel üretilemeyecek yükler (2000 eşzamanlı kullanıcıda performans).

📌 **Sık yapılan hata:** Her hafta değişen bir ekranın testine "yüksek uygunluk" demek. Bu tam olarak **bakım eforunun hafife alınması** riskidir.

🔗 [6.1 Pratik B — otomasyon kararı](6.1-araclar.md)

### Soru 5 — Statik analiz aracının bulamayacağı bir kusur tipi yaz.

**Kısa cevap:** **Yanlış bir iş kuralı** (temiz kodla yazılmış mantık/anlam hatası) — araç **anlamı göremez**.

**Ayrıntı:** Statik analiz kodlama standardı ihlallerini, ölü kodu, kullanılmayan değişkenleri, karmaşıklık metriklerini ve bilinen güvenlik desenlerini bulur. Ancak yanlış bir iş kuralı tertemiz, standarda uygun kodla yazılmışsa araç **sessiz kalır**; niyeti ve anlamı değerlendiremez. Bu tür kusurları ancak **insan review'ı** yakalar.

📌 **Sık yapılan hata:** Statik analizin "her kusuru" bulacağını sanmak. Sözdizimini ve deseni görür, **anlamı** göremez.

🔗 [6.1 §4 Statik analiz aracının sınırı](6.1-araclar.md)
