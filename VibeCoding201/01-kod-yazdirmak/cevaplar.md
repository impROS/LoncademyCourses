# 01 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 1.1 Sıfırdan özellik

### Soru 1 — Dört aşamayı say. "Atlama, ölçekle" ne demek?

**Kısa cevap:** **Keşif → plan → uygulama → kapanış.** Ölçekleme: küçük işte
keşif iki cümle, plan üç satır olabilir — ama **sıfır olmaz**.
Ölçüt: *değişikliği tek cümleyle tarif edebiliyorsan aşamaları birleştir.*

**Ayrıntı:** Ölçüt **belirsizliktir**, büyüklük değil. Küçük bir değişiklik bile
tanımadığın bir kodda belirsizse aşamalar korunur; büyük ama tekrarlayan bir iş
ise az planla yürüyebilir.

📌 **Sık yapılan hata:** Aşamaları bir tören sanıp her işe tam boy uygulamak.
Yazım hatası düzeltmek için plan çıkarmak, planın itibarını düşürür.

🔗 [1.1 §1](1.1-sifirdan-ozellik.md)

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

🔗 [1.1 §2](1.1-sifirdan-ozellik.md)

---

### Soru 3 — Planın beş parçası. Hangisi en çok atlanır, belirtisi ne?

**Kısa cevap:** Dosyalar adıyla · yeni dosyaların gerekçesi · **kapsam dışı** ·
uç durumlar ve hata davranışı · **uçtan uca doğrulama adımı**.
En çok atlanan: **kapsam dışı**. Belirtisi: *"bir uç nokta istedim, altı dosya
değişti."*

**Ayrıntı:** Kapsam dışının değeri iki yönlü:

1. **Uygulamada** kapsam kaçmasını önler.
2. **İncelemede** "bunu neden yapmamış?" sorusunu önler (`2.3`).

Yazması en kolay, atlanması en kolay parça bu. Plan geldiğinde ilk bakacağın
yer orası olsun; boşsa **sen doldur**.

📌 **Sık yapılan hata:** Kapsam dışını "bariz" sayıp yazmamak. Ajan için bariz
diye bir şey yok; yazılmayan sınır yok sayılır.

🔗 [1.1 §3](1.1-sifirdan-ozellik.md)

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

🔗 [1.1 §3](1.1-sifirdan-ozellik.md) · **101 · *Bağlam penceresi***

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

🔗 [1.1 §5](1.1-sifirdan-ozellik.md)

---

## 1.2 Var olan koda dokunmak

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

🔗 [1.2 §1](1.2-var-olan-koda-dokunmak.md)

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

🔗 [1.2 §1](1.2-var-olan-koda-dokunmak.md)

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

🔗 [1.2 §2](1.2-var-olan-koda-dokunmak.md)

---

### Soru 4 — Kalıbı izletmenin en etkili yolu ve neden?

**Kısa cevap:** **Kalıbı uygulayan somut bir dosyayı örnek göstermek.**
Çünkü model, **gördüğü örneğe benzetmekte** tarif edilen soyut kurala uymaktan
çok daha iyidir.

**Ayrıntı:** Bir örnek dosya adı, yirmi satırlık biçim tarifinden daha çok iş
görür — ve kural dosyanı da şişirmez (**101 · *CLAUDE.md***).

İyi bir örnek gösterme:

```text
Yeni uç noktayı eklerken sunucu.js'deki POST /gorevler işleyicisini
örnek al: aynı hata biçimini, aynı durum kodlarını, aynı adlandırmayı kullan.
```

Tipli dillerde kod zekâsı bunu güçlendirir: ajan sembolleri metin arayarak değil
**dile sorarak** bulur — hem daha doğru hem daha az dosya okur.

📌 **Sık yapılan hata:** Kalıbı kural dosyasında uzun uzun tarif etmek. Hem
dosyayı şişirir hem örnek kadar iyi çalışmaz.

🔗 [1.2 §3](1.2-var-olan-koda-dokunmak.md)

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

🔗 [1.2 §5](1.2-var-olan-koda-dokunmak.md)

---

## 1.3 Hata ayıklama

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

🔗 [1.3 §1](1.3-hata-ayiklama.md)

---

### Soru 2 — "Test ilk çalıştırmada geçti" neden uyarı? İstemde nasıl önlenir?

**Kısa cevap:** **Hiç kırmızı olmamış bir test, bir şey ölçtüğünü kanıtlamamıştır.**
Önleme: sırayı dayat — *"önce başarısız testi yaz ve kırmızı olduğunu göster."*

**Ayrıntı:** Ajan çoğu zaman testi ve düzeltmeyi birlikte yazmak ister; bu
verimli görünür ama ölçüm aracını kalibre etmeden kullanmak demektir.

Kesin doğrulama yolu (`2.1`'de de geçiyor): **kodu bilerek boz, test kırılsın.**
Kırılmıyorsa o test o davranışı korumuyor.

📌 **Sık yapılan hata:** Yeşil çıktıyı bir başarı işareti sanmak. Yeşil, yalnızca
"yazdığım testler geçti" demek — testlerin bir şey ölçtüğü ayrı bir iddia.

🔗 [1.3 §1](1.3-hata-ayiklama.md) · [2.1](../02-kaliteyi-guvenceye-almak/2.1-test-yazdirma.md)

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

🔗 [1.3 §3](1.3-hata-ayiklama.md)

---

### Soru 4 — Ham günlük okutmanın iki zararı ve doğrusu.

**Kısa cevap:** (1) Onbinlerce satır bağlama girer, (2) **her turda tekrar
gönderilir** — oturumun tamamını yavaşlatır ve pahalılaştırır.
Doğrusu: **filtrelenmiş komut ver** (`grep`, son N satır, yalnızca hata düzeyi).

**Ayrıntı:** Zarar birikimlidir: bir kez giren büyük çıktı sabit bir yük olur
(**101 · *Bağlam penceresi***). Ayrıca bağlam doluluğu arttıkça **sessiz bozulma** başlar — yani
hata ayıklamanın tam ortasında modelin dikkati dağılır.

İyi bir istem:

```text
gunluk.txt içindeki son 200 satırda ERROR geçen satırları göster —
tüm dosyayı okuma.
```

**301 · *Hook*** konusunda bunu otomatikleştiren hook'u yazacaksın: komut çalışmadan önce
çıktısını filtreleyen bir denetim.

📌 **Sık yapılan hata:** "Ajan zaten ilgili kısmı bulur" diye düşünmek. Bulur —
ama bulmak için **hepsini okumuş** olur, ve okuduğu bağlamda kalır.

🔗 [1.3 §4](1.3-hata-ayiklama.md)

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

🔗 [1.3 §5](1.3-hata-ayiklama.md)

---
