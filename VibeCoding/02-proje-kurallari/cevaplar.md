# 02 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 2.1 CLAUDE.md

### Soru 1 — Bir satırın kural dosyasında yer alıp almayacağına karar verirken sorduğun tek soruyu yaz ve iki örnekle göster.

**Kısa cevap:** *"Bu satırı silsem ajan hata yapar mı?"* Hayırsa sil.

**Ayrıntı:**

| Satır | Silsen ne olur | Karar |
|---|---|---|
| "Test: `npm run test:tek -- --grep`" | Ajan yanlış komutu dener | ✅ **Kal** |
| "Değişkenlere anlamlı isim ver" | Hiçbir şey; zaten yapıyor | ❌ **Sil** |
| "Bu projede tarih alanları UTC saklanır, gösterimde çevrilir" | Yerel saat yazar, hata çıkar | ✅ **Kal** |
| "`src/` klasöründe kaynak kodlar bulunur" | Hiçbir şey; okuyunca görüyor | ❌ **Sil** |

Ölçüt **davranış değişikliği**, doğruluk değil. Doğru ama etkisiz bir cümle
yalnızca bağlam harcar.

📌 **Sık yapılan hata:** Ölçütü "bu bilgi doğru mu?" sanmak. Kural dosyasındaki
her cümle doğrudur; soru doğruluk değil, **gereklilik**.

🔗 [2.1 §2](2.1-claude-md.md)

---

### Soru 2 — "Şişme paradoksu" ne? Refleks olarak yapılan yanlış hamle ve doğrusu ne?

**Kısa cevap:** Dosya uzadıkça içindeki kurallara **daha az** uyulur. Refleks
hamle: kuralı daha uzun ve daha vurgulu yazmak — bu dosyayı büyütür ve durumu
kötüleştirir. Doğrusu: **budamak**.

**Ayrıntı:** Mekanizma basit — kural dosyası bağlama giren bir metin ve model
uzun bir metindeki tek bir satırı kaçırabilir. 40 satırlık dosyada bir kural
öne çıkar; 300 satırlıkta gürültüye karışır.

İki farklı belirti, iki farklı teşhis:

| Belirti | Teşhis | Çözüm |
|---|---|---|
| Kurala **uyulmuyor** | Dosya uzun | Buda |
| Dosyada yazan şey **soruluyor** | İfade belirsiz | Ölçülebilir yaz |

Vurgu tek bir kural için işe yarar — ama **kıt bir kaynak**: her satır
vurgulanırsa ayırt edicilik kalmaz.

📌 **Sık yapılan hata:** Uyulmayan kuralı büyük harfle ve üç kez tekrar yazmak.
Hem dosya büyür hem vurgu değersizleşir.

🔗 [2.1 §3](2.1-claude-md.md)

---

### Soru 3 — `@yol` ile içe aktarma ne kazandırır, ne kazandırmaz? Bağlam tasarrufu için nereye gidersin?

**Kısa cevap:** **Düzen** kazandırır, **bağlam tasarrufu kazandırmaz** — içe
aktarılan dosya açılışta yüklenir. Tasarruf için: **yola kapsanmış kural
dosyaları** (`2.2`) ya da **skill** (`4.1`).

**Ayrıntı:**

| Yöntem | Ne zaman yüklenir | Tasarruf |
|---|---|---|
| Ana dosyaya yazmak | Her oturum | — |
| `@yol` içe aktarma | Her oturum | ❌ Yok |
| Kapsanmış kural | Eşleşen dosya okununca | ✅ Var |
| Skill | Çağrılınca (adı hep görünür) | ✅ Var |

Yani bölme bir **bakım** kararıdır, bir **maliyet** kararı değil. İkisini
karıştırmak, "kuralları böldüm ama `/context` değişmedi" şaşkınlığını doğurur.

📌 **Sık yapılan hata:** Bir yol adından söz ederken istemeden onu içe aktarmak.
Anmak istiyorsan ters tırnak içine al: `` `@README` ``.

🔗 [2.1 §4](2.1-claude-md.md)

---

### Soru 4 — Kural dosyaları çakışırsa ne olur ve bundan çıkan sorumluluk ne?

**Kısa cevap:** Dosyalar **birikir, birbirini ezmez**; çelişen iki talimat
varsa model birini **keyfî olarak** seçebilir. Sorumluluk: **çelişki bırakmamak**.

**Ayrıntı:** Yükleme sırası kurumsaldan yerele doğrudur ve daha spesifik olan
genelde ağır basar — ama bu bir **eğilimdir**, garanti edilmiş bir öncelik
kuralı değil. Bu yüzden davranış tutarsız görünür: "bazen şöyle, bazen böyle".

Düzenli bakım işi: kural dosyalarını ve alt dizinlerdekileri arada bir gözden
geçirip eskiyeni ve çelişeni temizlemek. Büyük tek depolarda başka ekiplerin
dosyalarını dışlayan bir ayar da var.

📌 **Sık yapılan hata:** Tutarsız davranışı modelin kararsızlığına yormak.
Önce iki dosyada çelişen talimat olup olmadığına bak.

🔗 [2.1 §4](2.1-claude-md.md)

---

### Soru 5 — `/init` çıktısını olduğu gibi bırakmak neden kötü fikirdir?

**Kısa cevap:** Çünkü `/init` **koddan çıkarılabilecekleri** yazar — dizin
yapısı, bağımlılıklar, mimari özet. Bunlar tam olarak "silsem hata yapar mı?"
testini geçemeyen içerik.

**Ayrıntı:** `/init` iyi bir **başlangıç noktasıdır**, bitmiş bir dosya değil.
Yaptığı iş: kod tabanını okuyup gördüklerini yazmak. Ama ajan zaten kod tabanını
okuyabiliyor — o bilgiyi her oturumda taşımanın değeri yok.

Asıl değer, ajanın **kendi başına bulamayacağı** şeylerde:
- Neden bu mimari seçilmiş (koddan görünmez)
- Hangi tuzaklara düşülmüş (koddan görünmez)
- Ekip görgü kuralları (koddan görünmez)
- Ortam tuhaflıkları (koddan görünmez)

Budamayı kendin yapmak zorunda değilsin: `/doctor` denetimi, koddan çıkarılabilir
içerik için kesme önerileri getirir.

📌 **Sık yapılan hata:** 300 satırlık `/init` çıktısını "proje iyi belgelenmiş"
işareti saymak. Doğrusu: budanmamış bir taslak.

🔗 [2.1 §5](2.1-claude-md.md)

---

## 2.2 Kural dosyaları

### Soru 1 — Beş parçaya böldün ama `paths` yazmadın. Maliyet ne olur, ne kazandın?

**Kısa cevap:** Maliyet **aynı** — hepsi her oturumda yüklenir. Kazandığın şey
yalnızca **düzen**.

**Ayrıntı:** `paths` olmayan kural dosyası, ana kural dosyasıyla birebir aynı
davranır: açılışta yüklenir, her istekte gider. Bölmek yine de değersiz değil —
ekip ayrı ayrı bakım yapabilir, dosyalar konu konu ayrılır, çakışma kolay görülür.

Ama **maliyet kararı ile bakım kararını karıştırma**: tasarrufu sağlayan şey
bölme değil, **kapsamlama**.

📌 **Sık yapılan hata:** Bölmeden sonra `/context`'e bakıp değişiklik görmeyince
mekanizmanın çalışmadığını sanmak. Çalışıyor — sen tasarruf getiren kısmı
kullanmadın.

🔗 [2.2 §1](2.2-kural-dosyalari.md)

---

### Soru 2 — Kapsanmış kural tam olarak hangi anda yüklenir? "Neden görmedi?" sorusunun cevabı ne?

**Kısa cevap:** Ajan **desene eşleşen bir dosyayı okuduğunda**. En yaygın cevap:
**ajan o dosyaya hiç dokunmadı**, dolayısıyla kural hiç yüklenmedi.

**Ayrıntı:** Tetikleyici dosya okumasıdır — her araç çağrısı değil. Bu bilinçli
bir tasarım: her çağrıda denetim yapılsaydı kapsamın maliyet avantajı kalmazdı.

Sonuçları:

| Durum | Kural yüklenir mi |
|---|---|
| Ajan `src/api/x.ts` okudu, kural `src/api/**` | ✅ |
| Ajan yalnızca `README.md` okudu | ❌ |
| Ajan dosyayı yazdı ama hiç okumadı | 🟡 Duruma göre — güvenme |
| Sıkıştırma oldu, dosya bir daha okunmadı | ❌ |

📌 **Sık yapılan hata:** Kuralı yazıp `/context`'te görmeyince biçim hatası
aramak. Önce "tetikleyici gerçekleşti mi?" diye sor.

🔗 [2.2 §2](2.2-kural-dosyalari.md)

---

### Soru 3 — Üç talimatı doğru yere yerleştir: test komutu · göç dosyaları · `.env` yasağı.

**Kısa cevap:**

| Talimat | Yer | Gerekçe |
|---|---|---|
| "Test komutu `npm test`" | **Ana kural dosyası** | Her oturumda gerekli |
| "Göç dosyaları yalnızca eklenir" | **Kapsanmış kural** (`migrations/**`) | Yalnızca oraya dokununca anlamlı |
| "`.env` asla düzenlenmesin" | **Hook** | Kesinlik gerekiyor |

**Ayrıntı:** Karar sorusu üçlü — ve bir dördüncüsü var:

| Soru | Cevap |
|---|---|
| Her zaman mı gerekli? | Ana kural dosyası |
| Bazı dosyalarda mı? | Kapsanmış kural |
| Bazen mi? | Skill |
| **İstisnasız tutmalı mı?** | **Hook** |

Üçüncü talimatın kural dosyasına yazılması yaygın ama yetersiz bir çözümdür:
bir gün yine de düzenlenir, ve o gün geldiğinde gizli anahtarın kaybolmuş olur.

📌 **Sık yapılan hata:** Güvenlik kısıtlarını kural dosyasına yazıp iş bitti
sanmak. Güvenlik ricayla kurulmaz.

🔗 [2.2 §3](2.2-kural-dosyalari.md)

---

### Soru 4 — Uzun oturumda kapsanmış kural neden uygulanmayı bırakabilir, ne yaparsın?

**Kısa cevap:** Çünkü kapsanmış kurallar mesaj geçmişine girer ve **sıkıştırmada
özetlenir**; geri gelmeleri için eşleşen dosyanın **tekrar okunması** gerekir.
Kritikse: `paths` ön bilgisini kaldır ya da kuralı ana dosyaya taşı.

**Ayrıntı:**

| Kural türü | Sıkıştırma sonrası |
|---|---|
| Kapsanmamış | Diskten **koşulsuz** yeniden yüklenir |
| Kapsanmış | Yalnızca tetikleyici tekrarlanırsa |

Bu, `1.3`'teki tablonun pratik sonucu. Kararı **riske göre** ver: bir biçim
tercihinin uzun oturumda kaybolması önemsiz; bir güvenlik kısıtının kaybolması
değil. İkincisini zaten hook'a taşımış olman gerekir.

📌 **Sık yapılan hata:** Kapsamlamayı her kurala refleks olarak uygulamak.
Kapsam bir **maliyet optimizasyonudur** ve bedeli dayanıklılıktır.

🔗 [2.2 §5](2.2-kural-dosyalari.md)

---

### Soru 5 — Kullanıcı ve proje kuralları çakışırsa hangisi önceliklidir, neden?

**Kısa cevap:** **Proje kuralları.** Kullanıcı kuralları önce yüklenir, proje
kuralları sonra — sonra yüklenen daha yakın okunur.

**Ayrıntı:** Genel ilke tutarlı: **dar kapsam geniş kapsamı geçer.** Kullanıcı
kuralları tüm projelerinde geçerlidir; proje kuralları yalnızca bu depoda. Daha
özgül olan, daha genel olanı gölgeler.

Yine de bu bir öncelik **eğilimi**, katı bir üzerine yazma değil — ikisi de
bağlamda durur. Bu yüzden gerçek çözüm çelişkiyi hiç yaratmamak: kişisel
tercihlerini projeye özgü kararlarla çakışmayacak biçimde yaz.

📌 **Sık yapılan hata:** Kişisel bir tercihi kullanıcı düzeyine yazıp her
projede geçerli olmasını beklemek — sonra bir projede tersi bir kural olduğunda
şaşırmak.

🔗 [2.2 §4](2.2-kural-dosyalari.md)

---

## 2.3 AGENTS.md

### Soru 1 — AGENTS.md var ama Claude uymuyor. Sebep ve iki çözüm; hangisi Windows'ta sorun çıkarır?

**Kısa cevap:** **Claude Code `AGENTS.md` okumaz, `CLAUDE.md` okur.**
Çözüm 1: `CLAUDE.md` içine `@AGENTS.md` yaz. Çözüm 2: sembolik bağ kur.
**Windows'ta sorun çıkaran: sembolik bağ** (yönetici yetkisi ya da geliştirici
kipi ister).

**Ayrıntı:**

| Yöntem | Avantajı | Dezavantajı |
|---|---|---|
| `@AGENTS.md` içe aktarma | Altına **araca özgü ek** yazılabilir | — |
| Sembolik bağ | Tek satır, dosya birebir aynı | Windows'ta ek yetki; araca özgü ek yeri yok |

Doğrulama ikisinde de aynı: `/context` çalıştır, bellek dosyalarında `CLAUDE.md`
göründüğünü gör. Bu adımı atlama — bağlamanın sessizce çalışmaması mümkün.

📌 **Sık yapılan hata:** Standardın yaygınlığını evrensellik sanmak. Yirmiden
fazla araç okuyor olması, kullandığın aracın okuduğu anlamına gelmiyor.

🔗 [2.3 §2](2.3-agents-md.md)

---

### Soru 2 — İç içe talimat dosyalarında hangisi geçerlidir? Üstünde ne vardır?

**Kısa cevap:** **Düzenlenen dosyaya en yakın** olan. Hepsinin üstünde
**kullanıcının o anki isteği** vardır.

**Ayrıntı:** Belirtimin kendi ifadesi de bu yönde. Mantığı açık: tek depoda
farklı alt projeler farklı kurallara ihtiyaç duyar; kök dosya her zaman
kazansaydı alt proje ayrımı anlamsız olurdu.

Sıralama:

```
kullanıcının anlık isteği     ← en güçlü
  └─ en yakın AGENTS.md
      └─ üst dizindeki AGENTS.md
          └─ kök AGENTS.md
```

Son satır önemli: **sohbette söylediğin şey yazılı kuralı geçer.** Bu bir
esneklik ama aynı zamanda bir risk — `1.3`'te gördüğün gibi sohbette söylenen
sıkıştırmadan sağ çıkmaz.

📌 **Sık yapılan hata:** Kök dosyanın "resmî" olduğunu, alt dosyaların yalnızca
ek yaptığını sanmak. Çakışmada en yakın olan kazanır.

🔗 [2.3 §1](2.3-agents-md.md)

---

### Soru 3 — İçe aktarmak ile `@AGENTS.md` bağlantısı kurmak arasındaki fark? Hangisi eskir?

**Kısa cevap:** İçe aktarma **tek seferlik bir kopyadır** ve kaynak değişince
**eskir**. `@AGENTS.md` bağlantısı her oturumda kaynağı okur, eskimez.

**Ayrıntı:** Klasik kopya-bağlantı ayrımı:

| | İçe aktarma | Bağlantı |
|---|---|---|
| Ne yapar | İçeriği bir kez kopyalar | Her oturumda kaynağı okur |
| Kaynak değişirse | ❌ Kopya eski kalır | ✅ Otomatik yansır |
| Ne zaman uygun | Başka araçtan **taşınırken** | Sürekli **ortak kullanımda** |
| Taşıdığı | Talimat + bağlantı tanımları + komut + alt ajan + skill | Yalnızca metin |

İçe aktarma daha çok şey taşır (otomasyon dâhil) ama dondurur; bağlantı yalnızca
metin taşır ama canlı tutar. Geçiş yaparken içe aktarma, ortak yaşarken bağlantı.

📌 **Sık yapılan hata:** İçe aktarmayı kurup sonra kaynak dosyayı güncellemeye
devam etmek — ve değişikliklerin neden yansımadığını anlamamak.

🔗 [2.3 §4](2.3-agents-md.md)

---

### Soru 4 — Üç araç kullanan ekipte ne ortak dosyaya, ne araca özgü dosyaya? Hangi katman hiç taşınmaz?

**Kısa cevap:** **Ortak dosyaya:** komutlar, kod biçimi, test yönergeleri,
mimari kararlar, güvenlik notları. **Araca özgü dosyaya:** yalnızca o araca
bağlama satırı ve o araca özgü davranış. **Hiç taşınmayan katman:** hook,
skill ve alt ajan tanımları — yani **otomasyon**.

**Ayrıntı:**

| Katman | Taşınabilir |
|---|---|
| Ortak kurallar | ✅ Tamamen |
| Araca bağlama | Her araçta bir satır |
| Araca özgü ek | ❌ Doğası gereği |
| Kapsanmış kurallar | 🟡 Fikir taşınır, biçim taşınmaz |
| Hook, skill, alt ajan | ❌ Taşınmaz |

**Gerçekçi beklenti:** talimat taşınır, otomasyon taşınmaz. Ekip kararını buna
göre ver ve otomasyonun tek araca bağlı olduğunu **açıkça yaz** — yoksa başka
araç kullanan biri kurulumun yarısının çalışmadığını sonradan keşfeder.

📌 **Sık yapılan hata:** "Her şeyi taşınabilir yapalım" hedefi koymak. Mümkün
değil; enerjiyi metni tekleştirmeye harca.

🔗 [2.3 §5](2.3-agents-md.md)

---

### Soru 5 — Cursor'ın `globs` alanı hangi kavramın karşılığıdır?

**Kısa cevap:** **Yola göre kapsamlanmış kural dosyalarının** (`2.2`).

**Ayrıntı:** Aynı fikir, farklı adlar. Cursor'ın `.mdc` ön bilgisindeki üç alan,
bu setteki üç mekanizmaya karşılık gelir:

| Cursor alanı | Bu setteki karşılığı |
|---|---|
| `alwaysApply: true` | Ana kural dosyası (her oturum) |
| `globs: [...]` | **Kapsanmış kural** (`paths`) |
| `description: ...` | Skill (ajan gerekli görürse çağırır) |

Bunu görmek pratik bir kazanç sağlar: bir araçta öğrendiğin kapsam mantığını
diğerine taşıyabilirsin. Kaybolan şey biçim, kalan şey **fikir**.

Ek not: Cursor tarafında da "her zaman uygulanan kuralları kısa tut" önerisi var
— gerekçe aynı, her isteğe binen sabit maliyet. Şişme paradoksu araca özgü değil.

📌 **Sık yapılan hata:** Her aracın mekanizmasını sıfırdan öğrenmeye çalışmak.
Adlar farklı, altta üç soru aynı: her zaman mı, bazı dosyalarda mı, bazen mi?

🔗 [2.3 §3](2.3-agents-md.md)

---

## 2.4 Kalıcı bellek

### Soru 1 — Kural dosyası ile otomatik bellek arasındaki üç fark ve ortak nokta.

**Kısa cevap:** **Yazar** (sen / ajan) · **içerik** (talimat / çıkarım) ·
**risk** (şişerse uyulmaz / yanlış çıkarım sessizce kalıcılaşır).
**Ortak nokta:** ikisi de **bağlamdır, zorlama değil**.

**Ayrıntı:**

| | Kural dosyası | Otomatik bellek |
|---|---|---|
| Yazar | Sen | Ajan |
| İçerik | Talimat, standart | Öğrendikleri, tercihlerin |
| Kapsam | Proje/kullanıcı/kurum | **Depo başına, makineye özgü** |
| Paylaşım | ✅ Depoya işlenir | ❌ Paylaşılmaz |
| Denetim | Zaten senin elinde | **Bilinçli olarak yapman gerekir** |
| Risk | Şişme | Yanlış çıkarım |

Ortak nokta pratikte önemli: bir eylemin **her koşulda** engellenmesi
gerekiyorsa ikisi de yetmez — hook gerekir.

📌 **Sık yapılan hata:** Belleği "ajanın öğrenmesi" diye romantikleştirip hiç
denetlememek. Öğrenme, yanlış öğrenmeyi de içerir.

🔗 [2.4 §1](2.4-kalici-bellek.md)

---

### Soru 2 — Ajan hangi bilgileri kasten belleğe yazmaz ve neden?

**Kısa cevap:** **Koddan çıkarılabilecek her şeyi** (mimari, dosya yolları,
hata düzeltmeleri) ve **kural dosyalarında zaten yazanları**. Gerekçe: bellek,
koddan okunamayacak bilgiye ayrılmıştır.

**Ayrıntı:** İki gerekçe var ve ikisi de sağlam:

1. **Tekrar değersiz.** Ajan mimariyi her oturumda okuyarak bulabiliyor; not
   almak yalnızca yer harcar.
2. **Eskiyen bilgi zararlıdır.** Kod değişir, notlar değişmez. "Şu fonksiyon
   şurada" notu, dosya taşındığında ajanı **yanlış yöne** yollar.

İkinci madde daha önemli: bellek yalnızca yer harcamakla kalmaz, yanlış olduğunda
**aktif olarak zarar verir**. Bu yüzden kapsam dar tutulmuş.

📌 **Sık yapılan hata:** "Ajan projeyi ezberlese daha hızlı olur" diye düşünmek.
Ezberlenen şey eskir; okunan şey güncel.

🔗 [2.4 §2](2.4-kalici-bellek.md)

---

### Soru 3 — "Bende çalışıyor ama arkadaşımda çalışmıyor." Bellek kaynaklı olması nasıl mümkün, çözüm ne?

**Kısa cevap:** Bellek **makineye özgüdür** ve **depoya işlenmez** — senin
makinendeki notlar onun makinesinde yok. Çözüm: ekipçe geçerli olması gereken
her şeyi **kural dosyasına** yaz.

**Ayrıntı:** Bu, "bende çalışıyor" durumunun ajan çağındaki yeni biçimi. Eskiden
sebep yerel yapılandırma ya da kurulu bir araçtı; şimdi **ajanın senin
oturumlarından çıkardığı notlar** da aynı sonucu doğuruyor.

Tanı yolu basit ve kesin: `git status` bellek yüzünden bir değişiklik göstermez,
çünkü bellek deponun içinde değil. Yani depoyu klonlayan kimse o davranışı almaz.

Kontrol listesi — davranış farkı görünce sırayla bak:
1. Kural dosyaları aynı mı? (git'te, yani aynı olmalı)
2. Yerel ayar dosyaları farklı mı? (git yok sayıyor olabilir)
3. **Bellek farklı mı?** (`/memory` — kesinlikle farklı)

📌 **Sık yapılan hata:** Ekip kuralını ajana sohbette söyleyip "artık biliyor"
sanmak. Biliyor — **yalnızca senin makinende**.

🔗 [2.4 §4](2.4-kalici-bellek.md)

---

### Soru 4 — Tek seferlik isteğin kalıcı tercihe dönüşmesi hangi risk? Nasıl fark eder ve düzeltirsin?

**Kısa cevap:** **Yanlış çıkarımın sessizce kalıcılaşması.** Fark etme yolu:
`/memory` açıp okumak. Düzeltme: yanlış notu **silmek** — dosyalar düz markdown.

**Ayrıntı:** Riskin sinsiliği "sessiz" kelimesinde. Olan şey:

1. Bir kere "bu sefer testleri atla" dersin.
2. Ajan bunu bir tercih olarak kaydeder.
3. Haftalar sonra testleri atlamaya devam eder.
4. Sen bunu bir **kalite düşüşü** olarak yaşarsın, bir **ayar** olarak değil.

Üçüncü ve dördüncü adım arasında hiçbir bildirim yok. Bu yüzden kural basit:
**açıklanamayan bir davranışta önce belleğe bak.**

Önleyici alışkanlık: tek seferlik isteklere "bu sefer", "yalnızca şimdi" gibi
ifadeler eklemek işe yarayabilir — ama garanti değil. Asıl güvence düzenli denetim.

📌 **Sık yapılan hata:** Bellek denetimini hiç yapmamak. Çoğu kullanıcı `/memory`
komutunu bir kez bile açmadan aylarca çalışır.

🔗 [2.4 §4](2.4-kalici-bellek.md)

---

### Soru 5 — Bellek ile oturum sürdürme farkı. Uzun aradan sonra sürdürmenin maliyet dezavantajı?

**Kısa cevap:** Bellek **çıkarılmış notları** taşır (depo kapsamlı, kalıcı);
sürdürme **konuşmanın kendisini** taşır (tek oturum). Uzun aradan sonra sürdürmek,
**istem önbelleğini ıskalar** ve tüm geçmiş yeniden işlenir — ilk istek pahalıdır.

**Ayrıntı:**

| | Otomatik bellek | Oturum sürdürme |
|---|---|---|
| Taşıdığı | Süzülmüş notlar | Tam konuşma geçmişi |
| Kapsam | Depo | Tek oturum |
| Tetikleyen | Kendiliğinden | Senin komutun |
| Maliyeti | Küçük ve sabit | **Geçmiş kadar** |

Pratik alışkanlık: uzun süren bir işi kendi oturumunda tut ve adlandır — dal
gibi kullan. Ama **iş bittiyse sürdürme**, yeni oturum aç. Sürdürmenin değeri
devam eden bir bağlamı korumaktır; bitmiş bir işin geçmişini taşımak yalnızca
maliyet.

📌 **Sık yapılan hata:** Her sabah dünkü oturumu sürdürmeyi alışkanlık hâline
getirmek. Bir haftanın sonunda her istek beş günlük geçmişi taşır.

🔗 [2.4 §5](2.4-kalici-bellek.md)
