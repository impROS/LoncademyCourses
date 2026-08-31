# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 2.1 Test yazdırmak

### Soru 1 — Testin ajan çağındaki ek değeri; önceliği nasıl değiştirir?

**Kısa cevap:** Test, **ajanın kendi döngüsünü kapatabileceği tek makine-okunur
sinyal**. Bu, test yazmanın önceliğini **kalite kaygısından önce işleyiş
kaygısına** taşır.

**Ayrıntı:**

| | Klasik değeri | Ajan çağındaki değeri |
|---|---|---|
| Kime | İnsana | **Ajana** |
| Ne zaman okunur | İnceleme, regresyon | **Her turda, döngü içinde** |
| Yoksa | Regresyon riski | **Doğrulama halkası sen olursun** |

Son satır belirleyici: testsiz bir projede ajan gözetimsiz çalışamaz — çünkü
bittiğini anlayacağı bir ölçüt yok.

📌 **Sık yapılan hata:** Test yazmayı "sonra yapılacak kalite işi" sırasında
tutmak. Ajanla çalışırken test, çalışma **altyapısıdır**.

🔗 [2.1 §1](2.1-test-yazdirma.md)

---

### Soru 2 — Dört test kusuru ve zararları.

**Kısa cevap:**

| Kusur | Zararı |
|---|---|
| **Uygulamayı aynalama** | Kod değişince test de değişir; hiçbir şeyi korumaz |
| **Aşırı sahteleme** | Gerçekte hiçbir şey çalışmıyor olabilir |
| **Zayıf iddia** | Yanlış değer de testi geçer |
| **Her zaman yeşil** | Ölçtüğü şey bilinmiyor |

**Ayrıntı:** Dördü de **yeşil bir takım** üretir — ve yeşil takım güven üretir.
Tehlike burada: kusurlu testler yalnızca işe yaramaz değil, **yanlış güven**
verir.

Karşılığı tek paragraf:

```text
Testler davranışı sınasın, uygulamayı değil. Sahte nesneyi yalnızca dış
sistem için kullan. İddialar kesin olsun. Her testi önce kırmızı gördüğünü göster.
```

📌 **Sık yapılan hata:** Test sayısını kalite göstergesi saymak. Zayıf iddialı
elli test, kesin iddialı beş testten daha az koruma sağlar ve daha çok yavaşlatır.

🔗 [2.1 §3](2.1-test-yazdirma.md)

---

### Soru 3 — Kapsam neyi ölçer/ölçmez? Ajana kapsam hedefi neden riskli, yerine ne?

**Kısa cevap:** Kapsam **hangi satırların çalıştırıldığını** ölçer; o satırların
**doğru** çalıştığını, uç durumların denendiğini ve iddiaların anlamlı olduğunu
**ölçmez**. Ajana kapsam hedefi vermek risklidir çünkü hedefe en kısa yol **çok
sayıda zayıf iddialı test**. Yerine: **davranış listesi.**

**Ayrıntı:** Bu, `1.3`'teki bastırma kalıbının test tarafındaki karşılığı:
**ölçülen şey hedef hâline gelince, ölçüyü en ucuza yükselten yol seçilir.**

Daha iyi hedef somut:

```text
Şu davranışların her biri için bir test yaz:
- Boş listede boş dizi döner
- Eklenen görev listede görünür
- Olmayan id'de 404 döner
```

📌 **Sık yapılan hata:** Kapsam oranını ekip hedefi yapmak. İnsanlarda da aynı
sonucu üretir; ajanlarda yalnızca daha hızlı üretir.

🔗 [2.1 §4](2.1-test-yazdirma.md)

---

### Soru 4 — Testsiz modülde üç adım; ikinci adımda hangi cazibeye direnilir?

**Kısa cevap:** (1) **Mevcut davranışı** sınayan testler yaz, (2) testleri
**oku**, (3) **sonra** değişikliği yap.
İkinci adımda direnilecek cazibe: **yanlış bulduğun davranışı hemen düzeltmek.**

**Ayrıntı:** Dondurma aşamasında ajanın "düzeltme" yapmasına izin verirsen,
ortaya çıkan testler kodun **bugün yaptığını** değil, ajanın **doğru sandığını**
yansıtır — ve güvenlik ağın sahte olur.

Bugünkü davranış hatalı bile olsa önce yazılı hâle gelmeli. Aksi hâlde
değişikliğinin neyi bozduğunu bilemezsin: bozulan şey zaten yanlış yazılmış bir
testtir.

Hataları **not al**, ayrı bir iş olarak ele al.

📌 **Sık yapılan hata:** İstemde "testleri yaz" deyip davranış dondurma
niyetini söylememek. Ajan iyileştirmeye çalışır ve ağ delinir.

🔗 [2.1 §5](2.1-test-yazdirma.md)

---

### Soru 5 — Bir testin bir şey ölçtüğünü nasıl kanıtlarsın? İki yol.

**Kısa cevap:** (1) Testi **kırmızıyken** gör (kırmızı-yeşil sırası).
(2) **Kodu bilerek boz**, test kırılsın.

**Ayrıntı:** İkinci yol daha güçlüdür çünkü **her an** uygulanabilir — testler
çoktan yazılmış olsa bile.

```bash
# davranışı boz
sed -i '' 's|return gorevler;|return [];|' sunucu.js
npm test        # kırılmalı
git restore sunucu.js
```

Kırılmıyorsa o test o davranışı korumuyor. Bu, kapsam raporunun asla
veremeyeceği bir bilgi: kapsam satırın **çalıştığını** söyler, testin o satırın
**doğruluğunu denetlediğini** söylemez.

📌 **Sık yapılan hata:** Bu denetimi hiç yapmamak ve yeşil takıma güvenmek.
Yılda bir kez rastgele birkaç testi bozarak sınamak bile çok şey öğretir.

🔗 [2.1 §3](2.1-test-yazdirma.md)

---

## 2.2 Kod inceleme

### Soru 1 — Ajan kodunda daha sık üç hata; neden inceleme sıkılaşmalı?

**Kısa cevap:** **Uydurulmuş arayüz · kapsam taşması · bastırılmış hata**
(ayrıca: ıskalanmış kalıp, zayıf test, erken soyutlama).
İnceleme sıkılaşmalı çünkü ajan kodunda **yüzey kalitesi yüksektir** ve düzgün
görünen kod **daha az dikkatle okunur**.

**Ayrıntı:** İnsan kodunda dağınık biçim genelde aceleyi işaret eder — bir
ipucudur. Ajan kodunda bu ipucu **yoktur**: biçim her zaman tutarlı, adlandırma
her zaman düzgün. Dolayısıyla "iyi görünüyor" bilgi taşımaz.

📌 **Sık yapılan hata:** Yüzey kalitesini derinlik göstergesi saymak ve
incelemeyi hızlandırmak.

🔗 [2.2 §1](2.2-kod-inceleme.md)

---

### Soru 2 — Ajan diff'inde açılacak beş kontrol.

**Kısa cevap:**
1. **Uydurulmuş arayüz** var mı — çağrılan işlev gerçekten var mı, imzası doğru mu?
2. **Sessizce değişen davranış** var mı — varsayılan, sıra, dönüş tipi?
3. **Kapsam dışına** çıkılmış mı — istemediğin dosyalar diff'te mi?
4. **Hata bastırılmış** mı — `1.3`'teki altı kalıp?
5. **Testler bir şey ölçüyor** mu — `2.1`'deki dört kusur?

**Ayrıntı:** Beşi de ajanın **sık** hata yaptığı yerlere odaklanır. Adlandırma,
biçim ve yorum kalitesi bilerek listede yok — sınırlı inceleme dikkatini oraya
harcamak israftır.

İkinci kontrol en zor olanıdır: hata üretmeyen ama davranışı kaydıran
değişiklikler derlenir, testleri bile geçebilir.

📌 **Sık yapılan hata:** Diff'i baştan sona okumaya çalışmak. Beş soruyla
taramak, satır satır okumaktan hem hızlı hem etkili.

🔗 [2.2 §1](2.2-kod-inceleme.md)

---

### Soru 3 — İnceleyici yönergesinde hangi üç şey? Sonuncusu yazılmazsa?

**Kısa cevap:** **Neyi · neye karşı · neyin bulgu sayıldığı.**
Sonuncusu yazılmazsa gelen uzun liste seni **aşırı mühendisliğe** sürükler.

**Ayrıntı:** "Neye karşı" özellikle önemli: plana mı, gereksinim listesine mi,
yoksa genel iyi uygulamalara mı bakılacağı sonucu tamamen değiştirir.

Filtresiz uygulanan bulguların tipik ürünü: gereksiz soyutlama katmanları,
olmayacak durumlara karşı savunmacı kod, gerçekleşemeyecek senaryolar için
testler, okunabilirliği düşüren erken genelleme.

Filtreyi **isteğin içine** kur, sonradan uygulama — böylece süzme işi listeyi
okumadan önce yapılmış olur.

📌 **Sık yapılan hata:** "Bulabildiğin her sorunu listele" demek. Alacağın
liste, kodun kalitesini değil ölçütün genişliğini yansıtır.

🔗 [2.2 §2](2.2-kod-inceleme.md)

---

### Soru 4 — "Aynı model, o yüzden tarafsız değil" doğru mu? Belirleyici ne?

**Kısa cevap:** **Hayır.** Belirleyici olan farklı **model** değil, farklı
**bağlam**.

**Ayrıntı:** Fark **bilgiseldir**, teknik değil:

| | Yazan bağlam | Taze bağlam |
|---|---|---|
| Ne görür | Değişiklik **+ onu üreten akıl yürütme** | Yalnızca değişiklik ve ölçüt |
| Riski | Kendi gerekçesini doğrular | — |

Aynı model, temiz bir pencerede yalnızca diff'i ve ölçütü görür — dolayısıyla
sonucu bağımsız değerlendirir. Farklı model bir çeşitlilik katabilir ama gerekli
koşul değildir.

📌 **Sık yapılan hata:** Aynı oturumda "bir de sen kontrol et" demek. Aynı
bağlam, aynı körlük.

🔗 [2.2 §3](2.2-kod-inceleme.md)

---

### Soru 5 — Ajanın prensipte inceleyemeyeceği üç alan.

**Kısa cevap:** **Niyet** ("bu gerçekten istediğimiz şey mi?") · **kabul
edilebilir risk** (bu sistemde neyin bozulması göze alınır) · **öncelik** (şimdi
mi, sonra mı). Ayrıca: kurumsal bağlam ve insan etkisi.

**Ayrıntı:** Ortak sebep tek: **bilgi ajanın erişiminde değil.** Ürün hedefleri,
iş kısıtları, sözleşmeler, takvim, ekip kapasitesi — hiçbiri kodda yazılı değil.

Bu "ajan yeterince iyi değil" demek değil. Ayrımı yapmazsan iki hatadan birine
düşersin:

| Hata | Sonucu |
|---|---|
| Ajanın yapabildiğini elle yapmak | Gereksiz iş |
| **Ajanın yapamayacağını ona bırakmak** | Kritik kararın otomatiğe kaçması |

📌 **Sık yapılan hata:** İnceleyicinin "onay" vermesini ürün onayı saymak.
İnceleyici kodun doğruluğuna bakar, kararın doğruluğuna değil.

🔗 [2.2 §4](2.2-kod-inceleme.md)

---

## 2.3 Git ve pull request

### Soru 1 — Beş git alışkanlığı; hangisi "yaptığını" gösterir?

**Kısa cevap:** **Dal aç · temiz ağaçla başla · küçük ve sık commit · diff'i
sen oku · gizli değeri asla commit'leme.**
"Yaptığını" gösteren: **diff'i sen okumak**.

**Ayrıntı:** Diğer dördü koşulları hazırlar; diff okuma **tek gözlem aracıdır**.
Ajanın özeti bir iddiadır — genelde doğru, ama doğru olduğunu diff'ten bilirsin.

| Alışkanlık | Ne sağlar |
|---|---|
| Dal | Ana dal korunur, deneme ucuzlar |
| Temiz başlangıç | Kaynak netleşir |
| Küçük commit | Geri alma noktası, inceleme birimi |
| **Diff okuma** | **Gözlem** |
| Gizli değer kuralı | Sızıntı önlenir |

📌 **Sık yapılan hata:** Ajanın değişiklik özetini okuyup diff'i atlamak. Özet,
kaçırdığı şeyi de kaçırır.

🔗 [2.3 §1](2.3-git-ve-pr.md)

---

### Soru 2 — Kontrol noktası ve git kapsam farkı; örnek.

**Kısa cevap:** Kontrol noktası **oturum içi** ve yalnızca **ajanın dosya
düzenleme araçlarını** kapsar; git **kalıcı** ve **her değişikliği** kapsar.
Örnek: ajan `npm install` çalıştırdıysa geri sarma bağımlılıkları geri getirmez.

**Ayrıntı:** Sınırın pratik sonuçları:

| Değişiklik | Geri sarma alır mı |
|---|---|
| Ajan bir dosyayı düzenledi | ✅ |
| Ajan `sed` ile dosya değiştirdi | ❌ |
| Ajan paket kurdu | ❌ |
| Bir betik veri dosyası üretti | ❌ |

İkisi birlikte kullanılır: kontrol noktası hızlı ve oturum içi bir kolaylık,
git kalıcı ve eksiksiz kayıt.

📌 **Sık yapılan hata:** Kontrol noktasına güvenip dal açmamak — ve kabuk
kaynaklı bir değişiklikte geri dönüş noktası bulamamak.

🔗 [2.3 §1](2.3-git-ve-pr.md)

---

### Soru 3 — Pull request açıklamasının beş parçası; hangisi diff'ten okunamaz, hangisi soruyu önler?

**Kısa cevap:** **Ne değişti · neden · nasıl doğrulandı · nereye bakılmalı ·
kapsam dışı.**
Diff'ten okunamayan: **neden**. "Bunu neden yapmamış?" sorusunu önleyen:
**kapsam dışı**.

**Ayrıntı:**

| Parça | İnceleyene ne kazandırır |
|---|---|
| Ne değişti | Diff'i okumadan önce çerçeve |
| **Neden** | Diff'in **asla** veremeyeceği bilgi |
| Nasıl doğrulandı | Aynı işi tekrarlamaz |
| Nereye bakılmalı | Dikkatini yönlendirir |
| **Kapsam dışı** | Eksik mi bilinçli mi belirsizliğini kaldırır |

Kapsam dışı, plan aşamasında da aynı işi görüyordu (`1.1`) — orada kapsam
kaçmasını, burada yanlış anlaşılmayı önler.

📌 **Sık yapılan hata:** Açıklamayı diff'in özeti gibi yazmak. Diff zaten
orada; açıklamanın işi **diffte olmayanı** söylemek.

🔗 [2.3 §3](2.3-git-ve-pr.md)

---

### Soru 4 — Depoda çalışan ajanın güvenlik yüzeyi ve dört denetim.

**Kısa cevap:** Yüzey: **sorun kaydı ve yorum metinlerini herkes yazabilir, ve
ajan bunları okur** — yani istem enjeksiyonu kapısı.
Dört denetim: **yazma yetkisi şartı · bot denetimi · dar araç listesi · tur sınırı**.

**Ayrıntı:** Dışarıdan yazılabilen ve ajan tarafından okunan her metin bir
saldırı yüzeyidir. Açık bir depoda sorun kaydı açmak herkese açıktır.

| Denetim | Neyi engeller |
|---|---|
| Yazma yetkisi | Rastgele birinin ajanı çalıştırması |
| Bot denetimi | Yetkisiz otomasyon **ve sonsuz döngü** |
| Dar araç listesi | Başarılı bir enjeksiyonun zarar yüzeyi |
| Tur sınırı | Kaçak çalışmanın sınırsız uzaması |

Dördü katmanlıdır: birincisi çoğu saldırıyı, üçüncüsü geçenlerin zararını sınırlar.

📌 **Sık yapılan hata:** "Depomuz özel, sorun yok" demek. İç tehdit ve
bağımlılıklardan gelen içerik hâlâ geçerli — **401 · *Güvenlik***.

🔗 [2.3 §4](2.3-git-ve-pr.md)

---

### Soru 5 — Ajan commit'lerinde CI çalışmaması neden arıza değil?

**Kısa cevap:** Otomasyonun **kendi ürettiği olayla yeniden tetiklenmesini**
önleyen bilinçli bir davranıştır — sonsuz döngü koruması.

**Ayrıntı:** Zincir şöyle olurdu: ajan commit atar → iş akışı tetiklenir →
ajan yine çalışır → yine commit atar → sonsuza kadar. Öntanımlı davranış bu
zinciri keser.

Pratik değeri: "testler ajanın commit'inde neden çalışmadı?" sorusunun cevabını
**yapılandırma hatası aramadan** bilirsin. Gerçekten çalışmasını istiyorsan,
kimlik doğrulama biçimini değiştirmek gerekir — ve o zaman döngü riskini kendin
yönetirsin.

📌 **Sık yapılan hata:** Bunu bir arıza sanıp iş akışı dosyasında saatlerce
hata aramak.

🔗 [2.3 §4](2.3-git-ve-pr.md)

---

## 2.4 Büyük dönüşümler

### Soru 1 — Ayırt edici soru ve mekanik işte ajanın risk yönü.

**Kısa cevap:** Soru: **"Bu değişikliği bir düzenli ifadeyle güvenle yapabilir
miyim?"** Evetse ajan kullanma.
Risk yönü: 200 dosyanın 197'sini doğru yapıp **3'ünde "iyileştirme"** yapabilir
ve bunu fark etmezsin.

**Ayrıntı:** Sorun ortalama başarı değil, **dağılımın kuyruğu**. Bir kod
dönüştürücü ya hepsinde doğru çalışır ya hiçbirinde — hatası **görünür**.
Ajan çoğunda doğru, birkaçında yaratıcı davranır — hatası **görünmez**.

| | Deterministik araç | Ajan |
|---|---|---|
| Kapsama | %100 ya da hata | Çoğunlukla |
| Sapma | Yok | **Var, sessiz** |
| Hız | Saniyeler | Dakikalar-saatler |
| Maliyet | ~0 | Token |

📌 **Sık yapılan hata:** "Elimde ajan var, her şeyi onunla yapayım" refleksi.
Doğru araç sorusu her işte yeniden sorulur.

🔗 [2.4 §1](2.4-buyuk-donusum.md)

---

### Soru 2 — Üç aşama; pilotu atlamak neden çarpan hata üretir?

**Kısa cevap:** **Keşif → pilot → yayma.**
Pilotu atlamak çarpan hata üretir çünkü **istemdeki tek bir eksik, dosya sayısı
kadar tekrarlanır**: iki dosyada iki küçük düzeltme, iki yüz dosyada iki yüz hata.

**Ayrıntı:** Pilot, istemi düzeltmenin **en ucuz** olduğu andır. Sonrasında her
düzeltme, yapılmış işi geri almayı da gerektirir.

| Aşama | Çıktısı | Ölçütü |
|---|---|---|
| Keşif | Dosyaya yazılmış liste | Sayı biliniyor |
| **Pilot** | 2-3 dosyada diff | **Diff'i beğendin** |
| Yayma | Kalanı + rapor | Sayılar tutuyor |

📌 **Sık yapılan hata:** Pilotu yapıp diff'i **okumamak**. Pilotun tek amacı
o diff'i okumak; okunmuyorsa aşama yapılmamıştır.

🔗 [2.4 §2](2.4-buyuk-donusum.md)

---

### Soru 3 — Kabuk döngüsünde üç önemli ayrıntı ve gerekçeleri.

**Kısa cevap:**

| Ayrıntı | Gerekçe |
|---|---|
| **Her çağrı temiz bağlamla** | Bir dosyanın hatası ve karmaşası diğerine bulaşmaz |
| **Dar araç listesi** | Gözetimsiz çalışmada soracak kimse yok (**101 · *İzinler ve plan modu***) |
| **Çıktı günlüğe** | Hangi dosyanın başarısız olduğunu sonra okursun |

**Ayrıntı:** Birinci madde kaliteyi **sabit** tutar: tek oturumda ilerlenirse
yüzüncü dosya, doksan dokuz dosyalık birikmiş bağlamla işlenir — sessiz
bozulmanın tam koşulu.

Üçüncü madde sessiz kırpmaya karşı **kanıt** üretir: günlük olmadan "hepsi
tamam" iddiasını doğrulayamazsın.

📌 **Sık yapılan hata:** Döngüyü kurup çıktıyı ekrana akıtmak. İş bitince
kaydırıp bakılmaz; dosyaya yaz.

🔗 [2.4 §3](2.4-buyuk-donusum.md)

---

### Soru 4 — Ölçekte doğrulama nasıl değişir? Beş katman; hangisi "doğru yapıldı" der?

**Kısa cevap:** Tek dosyada diff okursun; iki yüz dosyada **okuyamazsın** —
doğrulama da otomatikleşmeli.
Katmanlar: **derleme/tip denetimi · test takımı · desen taraması · diff
istatistiği · örneklem incelemesi.**
"Doğru yapıldı" diyen: **örneklem incelemesi.**

**Ayrıntı:** Otomatik katmanlar **"bozulmadı"** der; dönüşümün istenen biçimde
uygulandığını söylemezler. Testler geçebilir ve dönüşüm yine de yanlış
uygulanmış olabilir.

| Katman | Cevapladığı soru |
|---|---|
| Derleme | Yapısal bozulma var mı |
| Test | Davranış bozuldu mu |
| Desen taraması | Eski kalıp kaldı mı |
| Diff istatistiği | Beklenmedik büyüklükte değişen var mı |
| **Örneklem** | **Doğru yapıldı mı** |

📌 **Sık yapılan hata:** Örneklemi atlamak — çünkü otomatik katmanlar yeşil ve
insan gözü yavaş. Beş dosya okumak yirmi dakika sürer; yanlış dönüşümü
üretimde bulmak günler.

🔗 [2.4 §4](2.4-buyuk-donusum.md)

---

### Soru 5 — Sessiz kırpmanın dört biçimi ve iki savunma.

**Kısa cevap:** Biçimler: **ilk N dosyada durmak · hata veren dosyaları sessizce
atlamak · "benzer" dosyaları eşleştirememek · zaman aşımına uğrayanları
raporlamamak.**
Savunmalar: **(istem)** her dosya için tek satır rapor ve atlama sebebi zorunlu ·
**(süreç)** beklenen ve gerçekleşen sayıları kendin karşılaştır.

**Ayrıntı:** İkinci savunma birincisinden güçlüdür çünkü **ajanın raporuna
bağlı değildir**:

```bash
wc -l < dosyalar.txt          # olması gereken
grep -c "TAMAM" gunluk.txt    # olan
```

İki sayı tutmuyorsa iş bitmemiştir — ajan ne derse desin. Bu, **101 · *Doğrulama refleksi*** konusundaki
"iddia değil kanıt" kuralının toplu iş ölçeğindeki hâli.

📌 **Sık yapılan hata:** "Tamamlandı" özetini okuyup kapatmak. Toplu işte
tamamlanma bir **sayıdır**, bir cümle değil.

🔗 [2.4 §5](2.4-buyuk-donusum.md)
