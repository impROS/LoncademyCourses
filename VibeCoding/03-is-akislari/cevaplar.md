# 03 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 3.1 Sıfırdan özellik

### Soru 1 — Dört aşamayı say. "Atlama, ölçekle" ne demek?

**Kısa cevap:** **Keşif → plan → uygulama → kapanış.** Ölçekleme: küçük işte
keşif iki cümle, plan üç satır olabilir — ama **sıfır olmaz**.
Ölçüt: *değişikliği tek cümleyle tarif edebiliyorsan aşamaları birleştir.*

**Ayrıntı:** Ölçüt **belirsizliktir**, büyüklük değil. Küçük bir değişiklik bile
tanımadığın bir kodda belirsizse aşamalar korunur; büyük ama tekrarlayan bir iş
ise az planla yürüyebilir.

📌 **Sık yapılan hata:** Aşamaları bir tören sanıp her işe tam boy uygulamak.
Yazım hatası düzeltmek için plan çıkarmak, planın itibarını düşürür.

🔗 [3.1 §1](3.1-sifirdan-ozellik.md)

---

### Soru 2 — Neden "kendi cümlelerinle anlat"? Hata görürsen ne yaparsın?

**Kısa cevap:** Anlatı, ajanın **zihin modelini görünür kılar**. Hata görürsen
**hemen** düzeltirsin — çünkü o hata plana, plandan koda taşınır.

**Ayrıntı:** Hatanın yakalandığı aşama, düzeltme maliyetini belirler:

| Aşama | Düzeltme maliyeti |
|---|---|
| Anlatı | Bir cümle |
| Plan | Bir madde |
| Kod | Yeniden yazım |
| Üretim | Olay müdahalesi |

Bu yüzden keşif aşamasının çıktısı kod değil **metin** olmalı: metni okumak
ucuz, diff okumak pahalı.

📌 **Sık yapılan hata:** Anlatıyı göz gezdirip geçmek. Anlatı okunmuyorsa
keşif aşaması yapılmamış demektir — yalnızca zaman harcanmıştır.

🔗 [3.1 §2](3.1-sifirdan-ozellik.md)

---

### Soru 3 — Planın beş parçası. Hangisi en çok atlanır, belirtisi ne?

**Kısa cevap:** Dosyalar adıyla · yeni dosyaların gerekçesi · **kapsam dışı** ·
uç durumlar ve hata davranışı · **uçtan uca doğrulama adımı**.
En çok atlanan: **kapsam dışı**. Belirtisi: *"bir uç nokta istedim, altı dosya
değişti."*

**Ayrıntı:** Kapsam dışının değeri iki yönlü:

1. **Uygulamada** kapsam kaçmasını önler.
2. **İncelemede** "bunu neden yapmamış?" sorusunu önler (`3.6`).

Yazması en kolay, atlanması en kolay parça bu. Plan geldiğinde ilk bakacağın
yer orası olsun; boşsa **sen doldur**.

📌 **Sık yapılan hata:** Kapsam dışını "bariz" sayıp yazmamak. Ajan için bariz
diye bir şey yok; yazılmayan sınır yok sayılır.

🔗 [3.1 §3](3.1-sifirdan-ozellik.md)

---

### Soru 4 — Planı neden dosyaya yazdırıp temiz oturumda uygularsın?

**Kısa cevap:** (1) **Dayanıklılık** — dosyadaki plan sıkıştırmadan sağ çıkar,
sohbetteki çıkmaz. (2) **Temiz başlangıç** — tartışma turları bağlamı doldurmuş
durumda; uygulama temiz pencerede daha iyi gider.

**Ayrıntı:** İki gerekçe farklı problemleri çözüyor:

| Gerekçe | Çözdüğü problem |
|---|---|
| Dayanıklılık | Uzun oturumda planın kaybolması |
| Temiz başlangıç | Tartışma birikintisinin uygulamayı bozması |

Ek fayda: dosyadaki plan **incelenebilir** — sen okur, düzeltir, ekibinle
paylaşırsın. Sohbetteki plan bunların hiçbirine uygun değil.

📌 **Sık yapılan hata:** Planı dosyaya yazdırıp aynı oturumda uygulamak. Yarım
kazanç: dayanıklılık var, temiz başlangıç yok.

🔗 [3.1 §3](3.1-sifirdan-ozellik.md) · [1.3](../01-temeller/1.3-baglam-yonetimi.md)

---

### Soru 5 — Üç durma işareti; geri almanın iki yolu ve kritik sınır.

**Kısa cevap:** (1) aynı konuda **ikiden fazla düzeltme**, (2) **plandan sapma**
büyüdü, (3) değişiklik **anlamadığın yere yayıldı**.
Geri alma: **kontrol noktası** ve **git**. Kritik sınır: kontrol noktaları
**kabuk komutlarıyla yapılan değişiklikleri kapsamaz**.

**Ayrıntı:** Üç işaretin ortak noktası: devam etmenin maliyeti, yeniden
başlamanın maliyetini geçmiş durumda. Sezgi buna direnir ("neredeyse
bitiyordu") ama sayılar direnmez.

| Yol | Kapsam | Kalıcılık |
|---|---|---|
| Kontrol noktası | Ajanın dosya araçları | Oturum içi |
| Git | **Her şey** | Kalıcı |

📌 **Sık yapılan hata:** Kontrol noktasına güvenip git dalı açmamak. Ajan bir
paket kurduysa ya da bir betik çalıştırdıysa geri sarma onu geri getirmez.

🔗 [3.1 §5](3.1-sifirdan-ozellik.md)

---

## 3.2 Var olan koda dokunmak

### Soru 1 — İlk soru ne, cevabı hangi dört kaynaktan gelir?

**Kısa cevap:** İlk soru **"neden böyle olmuş?"** — "nasıl daha iyi olur" değil.
Kaynaklar: **git geçmişi · commit mesajları · testler · sorun kayıtları**.

**Ayrıntı:**

| Kaynak | Ne söyler |
|---|---|
| Git geçmişi | Değişiklik **ne zaman** yapıldı, hangi değişiklikle birlikte |
| Commit mesajı | Yazarın **gerekçesi** |
| Testler | Kodun **korumak zorunda olduğu** davranış |
| Sorun kayıtları | Hangi **belirtiye** çözüm |

Ortak nokta: dördü de **kodun dışında**. Ajan yalnızca koda bakarsa bu bilgilerin
hiçbirine ulaşmaz ve "gereksiz" gördüğünü siler.

📌 **Sık yapılan hata:** Ajanın "bu satır gereksiz" değerlendirmesini bir
gözlem sanmak. O bir **tahmin**; gerekçe kodda yazmıyor.

🔗 [3.2 §1](3.2-var-olan-koda-dokunmak.md)

---

### Soru 2 — "Kod ne yapması gerektiğini söyler" neden yanlış? Çelişkide çıkarım?

**Kısa cevap:** **Kod ne yaptığını söyler; testler ne yapması gerektiğini.**
Çelişki varsa **hata koddadır**.

**Ayrıntı:** Test bir **niyet beyanıdır**: "bu davranış korunmalı." Kod ise o
niyeti gerçekleştirme denemesidir. Denemenin beyandan sapması, beyanın
yanlışlığını değil denemenin başarısızlığını gösterir.

Bu, "test geçsin diye testi değiştirme" kuralının teorik temeli. Testi
değiştirmek, çelişkiyi **niyeti bozarak** çözmektir.

⚠️ İstisna: test **gerçekten yanlış** olabilir. O zaman yapılacak şey testi
sessizce düzeltmek değil, **neden yanlış olduğunu açıklamak** — istemdeki
"testi değiştireceksen önce nedenini açıkla" şartının sebebi bu.

📌 **Sık yapılan hata:** Kodu "gerçeğin kaynağı" saymak. Kod, gerçeğin
**bugünkü hâli**; niyet başka yerde yazılı.

🔗 [3.2 §1](3.2-var-olan-koda-dokunmak.md)

---

### Soru 3 — Küçük diff'in dört faydası; istem cümlesi ve fikirleri neden yasaklamaz?

**Kısa cevap:** **İncelemesi kolay · hata izole · geri alması ucuz · her satırın
gerekçesi belli.**
Cümle: *"En küçük değişiklikle çöz. İlgisiz iyileştirme, yeniden adlandırma ve
biçim düzeltmesi yapma — gerekiyorsa ayrı bir öneri olarak söyle."*
Yasaklamaz çünkü **ayırır**: fikri alır, işi ayrı yürütürsün.

**Ayrıntı:** Ajan kod tabanında gerçek sorunlar görebilir; o bilgiyi çöpe atmak
israftır. "Ayrıca söyle" ifadesi iki şeyi birden sağlar: mevcut diff küçük kalır
**ve** bilgi korunur.

📌 **Sık yapılan hata:** "İyileştirme yapma" deyip noktalamak. Ajan gördüğünü
söylemez, sen de bilmezsin — ve o sorun orada durmaya devam eder.

🔗 [3.2 §2](3.2-var-olan-koda-dokunmak.md)

---

### Soru 4 — Kalıbı izletmenin en etkili yolu ve neden?

**Kısa cevap:** **Kalıbı uygulayan somut bir dosyayı örnek göstermek.**
Çünkü model, **gördüğü örneğe benzetmekte** tarif edilen soyut kurala uymaktan
çok daha iyidir.

**Ayrıntı:** Bir örnek dosya adı, yirmi satırlık biçim tarifinden daha çok iş
görür — ve kural dosyanı da şişirmez (`2.1`).

İyi bir örnek gösterme:

```text
Yeni uç noktayı eklerken sunucu.js'deki POST /gorevler işleyicisini
örnek al: aynı hata biçimini, aynı durum kodlarını, aynı adlandırmayı kullan.
```

Tipli dillerde kod zekâsı bunu güçlendirir: ajan sembolleri metin arayarak değil
**dile sorarak** bulur — hem daha doğru hem daha az dosya okur.

📌 **Sık yapılan hata:** Kalıbı kural dosyasında uzun uzun tarif etmek. Hem
dosyayı şişirir hem örnek kadar iyi çalışmaz.

🔗 [3.2 §3](3.2-var-olan-koda-dokunmak.md)

---

### Soru 5 — Temizlik tuzağının üç biçimi ve riskleri.

**Kısa cevap:**

| Biçim | Riski |
|---|---|
| **Yeniden adlandırma** | Dışarıdan kullanan kod kırılır |
| **Erken soyutlama** | İki kullanım aynı sebeple aynı değildi; birbirine kilitlenir |
| **Kaldırma** | Yansımayla ya da yapılandırmayla çağrılıyor olabilir |

**Ayrıntı:** Üçünün ortak kusuru: **statik bakış**. Ajan kodu okur, dışarıdan
kim nasıl kullanıyor göremez. Üçüncüsü en tehlikelisi çünkü statik arama dinamik
çağrıları hiç göstermez.

Erken soyutlamada ince nokta: tekrar her zaman kusur değildir. Bugün aynı görünen
iki kod parçası farklı sebeplerle var olabilir ve yarın ayrı yönlere evrilir.

📌 **Sık yapılan hata:** Bu davranışları "ajanın fazla hevesli olması" diye
görüp önemsememek. Üçü de üretimde gerçek kesintiye yol açabilir.

🔗 [3.2 §5](3.2-var-olan-koda-dokunmak.md)

---

## 3.3 Hata ayıklama

### Soru 1 — Üç adım; birinci atlanırsa hangi iki kanıt kaybolur?

**Kısa cevap:** **Yeniden üret → kök sebebi bul → düzelt ve doğrula.**
Kaybolan kanıtlar: (1) **hatanın gerçekten var olduğu**, (2) **düzeltmenin
gerçekten işe yaradığı**.

**Ayrıntı:** Başarısız test iki yönlü bir ölçüm aracıdır:

| An | Ne kanıtlar |
|---|---|
| Kırmızıyken | Hata var, ve **bu test onu görüyor** |
| Yeşile dönünce | Düzeltme **bu hatayı** çözdü |

İkisinden biri gözlenmezse elde kalan şey bir **umut**. Ve "belirti kayboldu"
gözlemi ikisinin yerini tutmaz — bastırma da belirtiyi kaybettirir.

📌 **Sık yapılan hata:** Testi düzeltmeden sonra yazmak. O test artık yalnızca
mevcut davranışı dondurur; hatayı yakalayıp yakalamadığı bilinmez.

🔗 [3.3 §1](3.3-hata-ayiklama.md)

---

### Soru 2 — "Test ilk çalıştırmada geçti" neden uyarı? İstemde nasıl önlenir?

**Kısa cevap:** **Hiç kırmızı olmamış bir test, bir şey ölçtüğünü kanıtlamamıştır.**
Önleme: sırayı dayat — *"önce başarısız testi yaz ve kırmızı olduğunu göster."*

**Ayrıntı:** Ajan çoğu zaman testi ve düzeltmeyi birlikte yazmak ister; bu
verimli görünür ama ölçüm aracını kalibre etmeden kullanmak demektir.

Kesin doğrulama yolu (`3.4`'te de geçiyor): **kodu bilerek boz, test kırılsın.**
Kırılmıyorsa o test o davranışı korumuyor.

📌 **Sık yapılan hata:** Yeşil çıktıyı bir başarı işareti sanmak. Yeşil, yalnızca
"yazdığım testler geçti" demek — testlerin bir şey ölçtüğü ayrı bir iddia.

🔗 [3.3 §1](3.3-hata-ayiklama.md) · [3.4](3.4-test-yazdirma.md)

---

### Soru 3 — Üç bastırma kalıbı; en sinsi hangisi ve neden?

**Kısa cevap:** Örnek üçlü: **boş yakalama · testi atlama · varsayılan değerle
örtme.** En sinsi: **varsayılanla örtme.**

**Ayrıntı:** Altı kalıbın tamamı: boş yakalama · geniş yakalama · testi gevşetme ·
testi atlama · denetimi kapatma · **varsayılanla örtme**.

Sonuncusunun sinsiliği: diğerleri bir **iz bırakır** — atlama işareti, boş
blok, bastırma yorumu. Varsayılanla örtme ise (`?? 0` gibi) tamamen normal
görünür ve **yanlış veriyi doğru gibi** sisteme akıtır. Ne bir hata mesajı ne
bir işaret kalır.

📌 **Sık yapılan hata:** Bunları ajanın kötü niyeti sanmak. Verilen hedef
"denetim geçsin"di; hedefi doğru koymak **senin** işin.

🔗 [3.3 §3](3.3-hata-ayiklama.md)

---

### Soru 4 — Ham günlük okutmanın iki zararı ve doğrusu.

**Kısa cevap:** (1) Onbinlerce satır bağlama girer, (2) **her turda tekrar
gönderilir** — oturumun tamamını yavaşlatır ve pahalılaştırır.
Doğrusu: **filtrelenmiş komut ver** (`grep`, son N satır, yalnızca hata düzeyi).

**Ayrıntı:** Zarar birikimlidir: bir kez giren büyük çıktı sabit bir yük olur
(`1.3`). Ayrıca bağlam doluluğu arttıkça **sessiz bozulma** başlar — yani
hata ayıklamanın tam ortasında modelin dikkati dağılır.

İyi bir istem:

```text
gunluk.txt içindeki son 200 satırda ERROR geçen satırları göster —
tüm dosyayı okuma.
```

`4.3`'te bunu otomatikleştiren hook'u yazacaksın: komut çalışmadan önce
çıktısını filtreleyen bir denetim.

📌 **Sık yapılan hata:** "Ajan zaten ilgili kısmı bulur" diye düşünmek. Bulur —
ama bulmak için **hepsini okumuş** olur, ve okuduğu bağlamda kalır.

🔗 [3.3 §4](3.3-hata-ayiklama.md)

---

### Soru 5 — "Üçüncü teori" işareti; önceki denemeleri nasıl taşırsın?

**Kısa cevap:** İki teori denenip tutmadıysa **bağlam başarısız yaklaşımlarla
doldu**; üçüncüyü aynı oturumda denemek genelde işe yaramaz. Taşıma yolu:
her denemeyi **tek satırda** — ne denendi, neden tutmadı.

**Ayrıntı:** Değerli olan bilgi, o bilgiyi üreten konuşma değil:

```text
Bu hatada iki yaklaşım denedik, ikisi de tutmadı:
1) İstek gövdesinin boş olması — değil, gövde geliyor.
2) Ayrıştırma hatası yakalanmıyor — yakalanıyor ama üst katmanda kayboluyor.
Şimdi üçüncü bir açıdan bak: yanıt yaşam döngüsü ve hata yayılımı.
```

Üç satır, sayfalarca turun yerini tutar — ve elemeleri **kazanç olarak** taşır.

📌 **Sık yapılan hata:** Temizlemeyi "baştan başlamak" sanıp direnmek. Baştan
başlamıyorsun; **öğrendiklerinle** başlıyorsun.

🔗 [3.3 §5](3.3-hata-ayiklama.md)

---

## 3.4 Test yazdırmak

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

🔗 [3.4 §1](3.4-test-yazdirma.md)

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

🔗 [3.4 §3](3.4-test-yazdirma.md)

---

### Soru 3 — Kapsam neyi ölçer/ölçmez? Ajana kapsam hedefi neden riskli, yerine ne?

**Kısa cevap:** Kapsam **hangi satırların çalıştırıldığını** ölçer; o satırların
**doğru** çalıştığını, uç durumların denendiğini ve iddiaların anlamlı olduğunu
**ölçmez**. Ajana kapsam hedefi vermek risklidir çünkü hedefe en kısa yol **çok
sayıda zayıf iddialı test**. Yerine: **davranış listesi.**

**Ayrıntı:** Bu, `3.3`'teki bastırma kalıbının test tarafındaki karşılığı:
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

🔗 [3.4 §4](3.4-test-yazdirma.md)

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

🔗 [3.4 §5](3.4-test-yazdirma.md)

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

🔗 [3.4 §3](3.4-test-yazdirma.md)

---

## 3.5 Kod inceleme

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

🔗 [3.5 §1](3.5-kod-inceleme.md)

---

### Soru 2 — Ajan diff'inde açılacak beş kontrol.

**Kısa cevap:**
1. **Uydurulmuş arayüz** var mı — çağrılan işlev gerçekten var mı, imzası doğru mu?
2. **Sessizce değişen davranış** var mı — varsayılan, sıra, dönüş tipi?
3. **Kapsam dışına** çıkılmış mı — istemediğin dosyalar diff'te mi?
4. **Hata bastırılmış** mı — `3.3`'teki altı kalıp?
5. **Testler bir şey ölçüyor** mu — `3.4`'teki dört kusur?

**Ayrıntı:** Beşi de ajanın **sık** hata yaptığı yerlere odaklanır. Adlandırma,
biçim ve yorum kalitesi bilerek listede yok — sınırlı inceleme dikkatini oraya
harcamak israftır.

İkinci kontrol en zor olanıdır: hata üretmeyen ama davranışı kaydıran
değişiklikler derlenir, testleri bile geçebilir.

📌 **Sık yapılan hata:** Diff'i baştan sona okumaya çalışmak. Beş soruyla
taramak, satır satır okumaktan hem hızlı hem etkili.

🔗 [3.5 §1](3.5-kod-inceleme.md)

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

🔗 [3.5 §2](3.5-kod-inceleme.md)

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

🔗 [3.5 §3](3.5-kod-inceleme.md)

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

🔗 [3.5 §4](3.5-kod-inceleme.md)

---

## 3.6 Git ve pull request

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

🔗 [3.6 §1](3.6-git-ve-pr.md)

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

🔗 [3.6 §1](3.6-git-ve-pr.md)

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

Kapsam dışı, plan aşamasında da aynı işi görüyordu (`3.1`) — orada kapsam
kaçmasını, burada yanlış anlaşılmayı önler.

📌 **Sık yapılan hata:** Açıklamayı diff'in özeti gibi yazmak. Diff zaten
orada; açıklamanın işi **diffte olmayanı** söylemek.

🔗 [3.6 §3](3.6-git-ve-pr.md)

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
bağımlılıklardan gelen içerik hâlâ geçerli — `6.1`.

🔗 [3.6 §4](3.6-git-ve-pr.md)

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

🔗 [3.6 §4](3.6-git-ve-pr.md)

---

## 3.7 Büyük dönüşümler

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

🔗 [3.7 §1](3.7-buyuk-donusum.md)

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

🔗 [3.7 §2](3.7-buyuk-donusum.md)

---

### Soru 3 — Kabuk döngüsünde üç önemli ayrıntı ve gerekçeleri.

**Kısa cevap:**

| Ayrıntı | Gerekçe |
|---|---|
| **Her çağrı temiz bağlamla** | Bir dosyanın hatası ve karmaşası diğerine bulaşmaz |
| **Dar araç listesi** | Gözetimsiz çalışmada soracak kimse yok (`1.4`) |
| **Çıktı günlüğe** | Hangi dosyanın başarısız olduğunu sonra okursun |

**Ayrıntı:** Birinci madde kaliteyi **sabit** tutar: tek oturumda ilerlenirse
yüzüncü dosya, doksan dokuz dosyalık birikmiş bağlamla işlenir — sessiz
bozulmanın tam koşulu.

Üçüncü madde sessiz kırpmaya karşı **kanıt** üretir: günlük olmadan "hepsi
tamam" iddiasını doğrulayamazsın.

📌 **Sık yapılan hata:** Döngüyü kurup çıktıyı ekrana akıtmak. İş bitince
kaydırıp bakılmaz; dosyaya yaz.

🔗 [3.7 §3](3.7-buyuk-donusum.md)

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

🔗 [3.7 §4](3.7-buyuk-donusum.md)

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

İki sayı tutmuyorsa iş bitmemiştir — ajan ne derse desin. Bu, `1.5`'teki
"iddia değil kanıt" kuralının toplu iş ölçeğindeki hâli.

📌 **Sık yapılan hata:** "Tamamlandı" özetini okuyup kapatmak. Toplu işte
tamamlanma bir **sayıdır**, bir cümle değil.

🔗 [3.7 §5](3.7-buyuk-donusum.md)
