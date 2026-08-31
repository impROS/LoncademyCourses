# 05 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 5.1 Alan haritası

### Soru 1 — Üç kategori ve sınırların bulanıklaşması.

**Kısa cevap:**

| Kategori | Ayırt edici özellik |
|---|---|
| **Ajan** | Dosya okuyan, komut çalıştıran, **kendi adımlarını planlayan** döngü |
| **Yapay zekâ destekli düzenleyici** | Ajan yetenekleri **gömülü** kod düzenleyici |
| **Tamamlama aracı** | Yazarken satır/blok öneren eklenti |

Bulanıklaşma: düzenleyiciler **ajan kipi ekliyor**, ajanlar **arayüz kazanıyor**.
Kategori bir etiket değil, bir **eğilim**.

**Ayrıntı:** Kategoriyi belirleyen şey döngünün kendisi, arayüzü değil. Bu
yüzden bir düzenleyicinin ajan kipi, bu setin öğrettiklerinin büyük kısmını
kullanabilir.

📌 **Sık yapılan hata:** Kategoriyi katı bir sınıflandırma sanmak ve "bu araç
hangi kutuya giriyor?" diye takılmak. Karar verirken etikete değil **eksenlere**
bakılır.

🔗 [5.1 §1](5.1-arac-haritasi.md)

---

### Soru 2 — Beş eksen; model gücü neden listede yok?

**Kısa cevap:** Örnek beşli: **bağlam yönetimi · yönlendirme katmanları ·
genişletilebilirlik · izin modeli · otomasyon** (ayrıca: doğrulama desteği,
maliyet modeli, veri işleme).
Model gücü listede yok çünkü **modeller yakınsıyor** ve çoğu araç birden çok
model sunuyor — farkı **harness** yapıyor.

**Ayrıntı:** Sekiz eksen, bu setin ilk dört bölümünde öğrenilen ihtiyaçlardan
türedi. Her biri bir bölüme karşılık geliyor:

| Eksen | Bölüm |
|---|---|
| Bağlam yönetimi | `1.3` |
| Yönlendirme katmanları | `02`, `4.3` |
| Genişletilebilirlik | `04` |
| İzin modeli | `1.4` |
| Otomasyon | `4.7` |
| Doğrulama desteği | `1.5` |
| Maliyet modeli | `6.4` |
| Veri işleme | `6.2` |

📌 **Sık yapılan hata:** En çok konuşulan ekseni en ayırt edici sanmak. Aynı
modeli kullanan iki araç çok farklı sonuç verebilir.

🔗 [5.1 §2](5.1-arac-haritasi.md)

---

### Soru 3 — Kıyaslamaların ölçmediği üç şey; "ölçüt hedef olunca" ne olur?

**Kısa cevap:** Ölçmedikleri: **senin kod tabanın · uzun oturumda bağlam
yönetimi · yönlendirme kurulumunun etkisi** (ayrıca maliyet ve ekip kullanımı).
"Ölçüt hedef hâline gelince ölçüt olmaktan çıkar": bir kıyaslamada yüksek puan,
**o kıyaslamaya benzer işlerde** iyi olmak demektir.

**Ayrıntı:** Kıyaslamalar kurulumsuz ve genel bir havuzda çalışır. Oysa bu setin
ilk dört bölümü tam olarak **kurulumun farkını** öğretiyor: kural dosyası,
hook, alt ajan, doğrulama.

Bu, kıyaslamaların değersiz olduğu anlamına gelmez — **kapsamlarının dışına
genellenmemesi** gerektiği anlamına gelir.

📌 **Sık yapılan hata:** Kıyaslama sıralamasını göç sebebi saymak. Ölçüm kendi
işinde, kendi kod tabanında yapılır (`5.2`).

🔗 [5.1 §4](5.1-arac-haritasi.md)

---

### Soru 4 — "En iyi araç hangisi?" neden eksik? Doğru soru?

**Kısa cevap:** Tek boyutlu bir sıralama, **hangi eksenin** önemli olduğunu
tarif etmiyor. Doğru soru: **"Hangi eksende farklılar ve o eksen benim işimde
önemli mi?"**

**Ayrıntı:** Otomasyon ağırlıklı çalışan biri için etkileşimsiz kip
belirleyicidir; kurumsal bir ekip için veri işleme belirleyici olabilir;
öğrenen biri için ücretsiz katmanın genişliği. Tek bir sıralama bu farkları gizler.

Bu yüzden `5.1`'in pratiği bir tablo doldurtuyor ve `5.2` o tabloyla karar
verdiriyor.

📌 **Sık yapılan hata:** Kendi ihtiyacını tanımlamadan karşılaştırma yapmak.
O zaman karşılaştırma bir popülerlik yarışına dönüşür.

🔗 [5.1 §2](5.1-arac-haritasi.md)

---

### Soru 5 — Aynı modeli kullanan iki araç neden farklı sonuç verir?

**Kısa cevap:** Çünkü **harness** farklı: bağlamı nasıl doldurduğu, ne zaman
sıkıştırdığı, hangi kuralları yüklediği, testi çalıştırıp sonucu okuyup
okumadığı.

**Ayrıntı:** Harness, modelin **etrafındaki her şey**. Somut örnekler:

| Fark | Sonucu |
|---|---|
| Biri keşfi ayrı bağlama devrediyor | Ana bağlam temiz kalır, kalite korunur |
| Biri kural dosyasını sıkıştırma sonrası yeniden yüklüyor | Talimatlar kaybolmaz |
| Biri komut çıktısını filtreleyebiliyor | Bağlam zehirlenmez |
| Biri garanti veren bir katman sunuyor | Kritik kurallar tutar |

Bu setin ilk dört bölümü tam olarak bu farkların nasıl yönetileceğini öğretiyor
— ve bu yüzden **araç değişse de taşınır**.

📌 **Sık yapılan hata:** "Aynı model, aynı sonuç" varsaymak ve farkı ölçmeden
model seçimine odaklanmak.

🔗 [5.1 §2](5.1-arac-haritasi.md)

---

## 5.2 Hangisi ne zaman

### Soru 1 — Karar tablosu tek başına neden yetmez?

**Kısa cevap:** Çünkü tablo bir **başlangıç noktası**. Yanına `5.1`'de
doldurduğun **eksen tablosu** ve **kendi işindeki en önemli iki eksen** gerekir.

**Ayrıntı:** Aynı satırdaki iki kişi farklı eksenlere öncelik veriyorsa farklı
araçlarda karar kılabilir. Örnek: "öğreniyorum, tek başıma" satırındaki iki
kişiden biri için ücretsiz katman belirleyiciyken, diğeri için genişletme
katmanının derinliği belirleyici olabilir.

📌 **Sık yapılan hata:** Tabloyu bir tavsiye listesi gibi okuyup ilk satırı
uygulamak. Tablo soruyu daraltır, cevabı vermez.

🔗 [5.2 §1](5.2-hangisi-ne-zaman.md)

---

### Soru 2 — Çoklu aracın iki mantıklı durumu, üç maliyeti, bir yasağı.

**Kısa cevap:**
**Mantıklı:** (1) farklı işler için farklı araçlar (düzenleyicide tamamlama,
terminalde ajan), (2) bir araç belirli bir eksende belirgin biçimde iyiyse.
**Maliyetler:** kurulum ikiye katlanır · alışkanlık bölünür · abonelik ikiye katlanır.
**Yasak:** ⚠️ **aynı işi iki araçla yapmak.**

**Ayrıntı:** Ayrım **işe göre** olduğunda çoklu araç iyi bir kalıptır; keyfe
göre olduğunda kafa karışıklığı üretir — hangisinde ne yaptığını takip edemezsin.

Kurulum maliyeti gerçek: `2.3`'teki kural geçerli — **talimat taşınır,
otomasyon taşınmaz.** Yani her araç için ayrı hook, ayrı alt ajan, ayrı bakım.

📌 **Sık yapılan hata:** Üçüncü bir aracı "denemek için" sürekli açık tutmak.
Deneme bir **ölçüm** olmalı, bir alışkanlık değil.

🔗 [5.2 §2](5.2-hangisi-ne-zaman.md)

---

### Soru 3 — Taşınan üç şey, taşınmayan üç şey, en küçümsenen maliyet.

**Kısa cevap:**
**Taşınır:** bu setin kavramları · talimat dosyaları · **MCP sunucuları**.
**Taşınmaz:** hook'lar · alt ajan tanımları · **kas hafızası**.
En çok küçümsenen: **kas hafızası**.

**Ayrıntı:**

| Katman | Taşınır mı |
|---|---|
| Kavramlar (bağlam, doğrulama, izin, iş akışları) | ✅ Tamamen |
| Talimat dosyaları | ✅ Büyük ölçüde |
| Kapsanmış kurallar | 🟡 Fikir taşınır, biçim taşınmaz |
| Skill / yordamlar | 🟡 Metin taşınır, çağırma biçimi değişir |
| Hook, alt ajan | ❌ |
| **MCP sunucuları** | ✅ **Ortak protokol** |
| Kas hafızası | ❌ |

MCP'nin taşınabilir olması, o tarafa yapılan yatırımı özellikle değerli kılıyor.

📌 **Sık yapılan hata:** Kas hafızasını hesaba katmamak. Ölçülmesi zor olduğu
için görünmez, ama haftalarca verim kaybettirir.

🔗 [5.2 §3](5.2-hangisi-ne-zaman.md)

---

### Soru 4 — "Kritik doğrulamayı hatta da tut" neden?

**Kısa cevap:** Çünkü **hook'lar taşınmaz.** Testlerin geçmesini zorunlu kılan
bir kapı yalnızca hook'ta yaşıyorsa, araç değiştiğinde o güvence **sessizce**
ortadan kalkar.

**Ayrıntı:** Sürekli tümleştirme hattındaki aynı denetim, araçtan bağımsız bir
**taban** sağlar. İki katman birlikte çalışır:

| Katman | Ne sağlar |
|---|---|
| Hook | **Hızlı geri bildirim** — daha kod yazılırken |
| Hat | **Araçtan bağımsız güvence** — göç günü kaybolmaz |

Bu, `4.3`'teki hook coşkusuna karşı dengeleyici bir uyarı: hook harika bir
araç, ama tek dayanak olmamalı.

📌 **Sık yapılan hata:** Bir güvenceyi hook'a taşıyıp hattan kaldırmak
("artık gerek yok"). Göç gününe kadar doğru görünür.

🔗 [5.2 §3](5.2-hangisi-ne-zaman.md)

---

### Soru 5 — Geçerli ve geçersiz yenileme sebepleri; "ölçmek" ne demek?

**Kısa cevap:**

| Geçerli | Geçersiz |
|---|---|
| İşinin **niteliği değişti** | Yeni sürüm duyurusu |
| Mevcut araç **bir eksende tıkanıyor** | Sosyal ağdaki kıyaslama |
| **Ölçtün** ve fark buldun | Merak |
| Kurumsal kısıt değişti | Arayüz daha güzel |

**Ölçmek:** aynı türden birkaç görevi iki araçla yapıp **tur sayısı, düzeltme
sayısı, geçen süre ve maliyeti** kaydetmek. Beş görev, iki araç, dört sayı.

**Ayrıntı:** Geçerli sebeplerin ortak yanı: **ihtiyacında bir değişikliği**
gösteriyorlar. Geçersizlerin ortak yanı: araç hakkında bir şey söylüyorlar ama
**senin işin hakkında** hiçbir şey söylemiyorlar.

⚠️ **Göç maliyeti bir kerelik değil** — kurulum, ekip, öğrenme haftalara yayılır.
Belirgin bir kazanç yoksa **kalmak** doğru karardır.

📌 **Sık yapılan hata:** "Bir hafta deneyeyim" deyip ölçüm tutmamak. Sonunda
elinde bir his kalır, karşılaştırılabilir bir sayı değil.

🔗 [5.2 §4](5.2-hangisi-ne-zaman.md)

---

## 5.3 Kaynaklar ve repolar

### Soru 1 — İki halka farkı; hangisi karar için?

**Kısa cevap:** **Birinci halka** (birincil kaynaklar) **nasıl çalıştığını**
öğretir — **karar için** kullanılır. **İkinci halka** (derleme listeleri,
kayıtlar) **ne var olduğunu** gösterir — **keşif için**.

**Ayrıntı:**

| Halka | İçerik | Kullanımı |
|---|---|---|
| Birinci | Aracın belgeleri, **değişiklik günlüğü**, MCP belirtimi, `AGENTS.md`, güvenlik sayfaları | Karar, doğrulama |
| İkinci | Derleme listeleri, eklenti/skill kayıtları, sunucu dizinleri | Keşif |

Blog ve videolar birincil kaynağın **yorumudur**: eskir, kaynak güncellenir.

📌 **Sık yapılan hata:** Bir listede gördüğü şeyi doğrudan kurmak. Liste keşfi
tamamlar, kararı **başlatır**.

🔗 [5.3 §1 ve §2](5.3-kaynaklar-ve-repolar.md)

---

### Soru 2 — Değişiklik günlüğü neden haber takibinden değerli?

**Kısa cevap:** Çünkü seni doğrudan etkileyen küçük değişiklikler — bir bayrağın
eklenmesi, bir varsayılanın değişmesi, bir davranışın düzeltilmesi — **haberlerde
yer almaz**, günlükte yer alır.

**Ayrıntı:** "Bir davranış değişmiş, haberim olmadı" durumunun panzehiri bu.
Haftada 15 dakikalık bir alışkanlık yeter ve şunları kazandırır:

- Yeni bir bayrağı ya da kipi zamanında öğrenirsin.
- Değişen bir varsayılanın etkisini önceden görürsün.
- Bu setteki bir bilginin eskidiğini fark edersin.

📌 **Sık yapılan hata:** Güncel kalmayı sosyal medya takibine indirgemek. Orada
duyurular var, davranış ayrıntıları yok.

🔗 [5.3 §1 ve §4](5.3-kaynaklar-ve-repolar.md)

---

### Soru 3 — Beş değerlendirme sorusu; kaçında takılırsa okumaya değmez?

**Kısa cevap:**
1. **Ne zaman yazılmış?**
2. **Bir şey satıyor mu?**
3. **Ölçüm var mı?**
4. **Sınırlarını söylüyor mu?**
5. **Kaynağı gösteriyor mu?**
**Üçünde** takılıyorsa okumaya değmez.

**Ayrıntı:** Kötü işaretler: tarih yok ya da eski ve güncellenmemiş · sonunda
kurs/paket satışı · yalnızca iddia, sayı yok · her şeyi çözdüğünü ima etme ·
belgeye ya da koda bağlantı yok.

Özellikle kaçınılacaklar: "en iyi 500 prompt" listeleri, "bu ayarla 10 kat
hızlan" başlıkları, ücretli istem pazarları.

📌 **Sık yapılan hata:** Popülerliği kalite ölçütü saymak. Beş sorunun
hiçbirinde okunma sayısı yok.

🔗 [5.3 §3](5.3-kaynaklar-ve-repolar.md)

---

### Soru 4 — "Listede var" neden güvenlik onayı değil? Kurmadan önce ne yaparsın?

**Kısa cevap:** Çünkü listeye girmek bir **inceleme sürecinden geçmek** anlamına
gelmez. Kurmadan önce `4.6`'daki **dört soruyu** sorarsın:
**hangi hook'lar · hangi MCP sunucuları · ne yetki · gizli değer istiyor mu**
(ayrıca: kim yazdı, sürdürülüyor mu).

**Ayrıntı:** 2026'da eklenti ve skill dağıtım yüzeyinde kötü niyetli paket
kampanyaları raporlandı — üretkenlik aracı ya da kodlama yardımcısı kılığında
bilgi çalan paketler. Kalıp tanıdık: **paket yöneticilerindeki tedarik zinciri
riski, ajan eklentilerinde daha yüksek etkiyle geri döndü.**

Etki daha yüksek çünkü eklenti yalnızca kod değil, **ajanının davranışını** da
değiştirir.

⚠️ Ayrıca: **adı popüler bir araca çok benzeyen** paketlere dikkat.

📌 **Sık yapılan hata:** Yıldız sayısına bakmak. Popülerlik ölçer, güvenlik değil.

🔗 [5.3 §2](5.3-kaynaklar-ve-repolar.md) · [4.6](../04-kendi-aletini-yap/4.6-eklenti-ve-paylasim.md)

---

### Soru 5 — Bu setin hızlı/yavaş eskiyen kısımları; `⚠️` ne demek?

**Kısa cevap:**
**Hızlı eskir:** araç adları ve özellikleri (`5.1`) · komut ve bayrak biçimleri
(`4.7`) · sürüm damgaları (`4.4`) · fiyat ve plan bilgileri (`00.3`).
**Yavaş eskir:** bağlam yönetimi (`1.3`) · doğrulama refleksi (`1.5`) · iş
akışları (`03`) · güvenlik ilkeleri (`6.1`).
`⚠️` işareti: **kaynağına gitmen gereken yer.**

**Ayrıntı:** Ayrımın mantığı: somut ve **dışarıya bağlı** olan her şey hızlı
eskir; modelin ve aracın **doğasından türeyen** ilkeler yavaş eskir.

Bu ayrım bir okuma stratejisi de veriyor: bu seti altı ay sonra tekrar
açtığında, `⚠️` işaretli yerleri doğrula, geri kalanına güven.

📌 **Sık yapılan hata:** Eskimiş bir komutu görüp setin tamamına güvenmemek —
ya da tersine, hiç doğrulamadan her şeyi uygulamak. İşaretler tam olarak bu
ikisinin arasını gösteriyor.

🔗 [5.3 §5](5.3-kaynaklar-ve-repolar.md)
