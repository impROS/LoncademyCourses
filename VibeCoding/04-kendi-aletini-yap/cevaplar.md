# 04 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 4.1 Skill yazmak

### Soru 1 — Üç tetikleyici ve karar üçlüsündeki yeri.

**Kısa cevap:** (1) Aynı istemi **üçüncü kez** yazmak, (2) kural dosyandaki bir
bölümün **yordama** dönüşmesi, (3) ara sıra gereken **uzun başvuru**.
Karar üçlüsünde skill **"bazen"** kutusundadır.

**Ayrıntı:**

| Soru | Cevap |
|---|---|
| Her zaman mı gerekli? | Ana kural dosyası |
| Bazı dosyalarda mı? | Kapsanmış kural |
| **Bazen mi?** | **Skill** |
| İstisnasız tutmalı mı? | Hook |

Skill'in ayırt edici özelliği **talep üzerine yüklenmesi** — bu yüzden uzun bir
başvuru metni, gerekene kadar neredeyse bedavadır.

📌 **Sık yapılan hata:** "Her zaman gerekli" bir kuralı skill'e taşımak.
Çağrılmadığında yok demektir.

🔗 [4.1 §1](4.1-skill-yazma.md)

---

### Soru 2 — `description` neden en önemli alan? Kötü yazılmışsa iki belirti.

**Kısa cevap:** Çünkü **ajan onunla seçer.** İki belirti: (1) skill **hiç
çağrılmaz**, (2) skill **alakasız yerde** devreye girer.

**Ayrıntı:**

| Belirti | Sebep | Düzeltme |
|---|---|---|
| Hiç çağrılmıyor | Açıklama belirsiz, kullanım durumu yok | Tetikleyiciyi başa yaz |
| Yanlış yerde çağrılıyor | Açıklama fazla geniş | Daralt; yan etkiliyse kendiliğinden çağrılmayı kapat |

İyi açıklama kullanım durumunu **başa** koyar: *"Kullanıcı 'ne değişti', 'commit
mesajı' ya da 'diff'imi incele' dediğinde kullan."*

📌 **Sık yapılan hata:** Açıklamayı skill'in **ne yaptığını** anlatan bir cümle
sanmak. Ajanın ihtiyacı olan şey **ne zaman kullanılacağı**.

🔗 [4.1 §2](4.1-skill-yazma.md)

---

### Soru 3 — "Skill bedavadır" neden yanlış?

**Kısa cevap:** **Gövdesi** talep üzerine yüklenir (asıl kazanç), ama
**açıklaması her oturumda** bağlama girer.

**Ayrıntı:** Otuz skill, otuz açıklama demektir. İki maliyet birden artar:

| Artan | Nasıl |
|---|---|
| Bağlam | Açıklamalar birikir |
| **Yanlış seçim riski** | Benzer açıklamalar arasında ajan şaşırır |

İkincisi daha az konuşulur ama daha zararlıdır: yanlış skill çağrılması, hiç
çağrılmamasından kötüdür.

📌 **Sık yapılan hata:** Skill sayısını sınırsız artırmak. Kullanılmayanları
sil ya da kendiliğinden çağrılmayı kapat.

🔗 [4.1 §5](4.1-skill-yazma.md)

---

### Soru 4 — Kritik talimatlar neden dosyanın başına?

**Kısa cevap:** Çünkü **sıkıştırmada skill gövdesi kırpılır ve kırpma sondan
yapılır** — başı korunur.

**Ayrıntı:** Uzun bir oturumda çağrılmış skill'lerin gövdeleri yeniden eklenir,
ama bir üst sınıra göre kesilir. Dosyanın altına yazılmış bir kural sessizce
kaybolur ve sen bunu fark etmezsin.

İlk çağrıda sorun yoktur — dosya bütün olarak okunur. Sorun **sıkıştırma
sonrasında** ortaya çıkar, yani uzun oturumlarda.

📌 **Sık yapılan hata:** Skill'i klasik belge gibi yazmak — bağlam, tarihçe,
sonda kurallar. Tersine yaz: **önce kurallar**, sonra ayrıntı.

🔗 [4.1 §5](4.1-skill-yazma.md) · [1.3](../01-temeller/1.3-baglam-yonetimi.md)

---

### Soru 5 — Dağıtım skill'i için hangi alan, iki faydası?

**Kısa cevap:** `disable-model-invocation: true`.
Faydaları: (1) ajan **kendiliğinden çağıramaz**, (2) açıklaması **bağlama girmez**
— yani bedava olur.

**Ayrıntı:** Yan etkili işlerde tetikleyici sen olmalısın: dağıtım yapan, kayıt
gönderen, dış sistemi değiştiren bir skill'in ajan kararıyla çalışması
istenmez.

İkinci fayda güzel bir yan etki: madem ajan onu hiç seçmeyecek, açıklamasını
görmesine de gerek yok.

📌 **Sık yapılan hata:** Yan etkili skill'i açık bırakıp "nasılsa doğru zamanda
çağırır" varsaymak. Bir kez yanlış zamanda çağırması yeterli.

🔗 [4.1 §3](4.1-skill-yazma.md)

---

## 4.2 Subagent

### Soru 1 — Asıl kazanç ve iki yanlış anlama.

**Kısa cevap:** Asıl kazanç **bağlam yalıtımı**.
Yanlış anlamalar: (1) "paralel hız için", (2) "her işi ona vermeli".

**Ayrıntı:** Paralellik bir **yan fayda**; asıl mesele onlarca dosyanın
yardımcının penceresinde okunup sana yalnızca özetin dönmesi.

"Her işi devret" yanlışı daha zararlı: ara sonuçlara ihtiyacın olan bir işi
devretmek, **gözünü kapatarak çalışmak** demektir.

📌 **Sık yapılan hata:** Token tasarrufu beklemek. Her yardımcı kendi
penceresini açar; toplam token bazen **artar**. Kazanç kaliteden gelir.

🔗 [4.2 §1](4.2-subagent.md)

---

### Soru 2 — Ayırt edici soru ve bedeli.

**Kısa cevap:** Soru: **"Ara sonuçları bir daha kullanacak mıyım?"** Hayırsa
devret. Bedel: **yardımcının gördüklerini kaybedersin**; elinde yalnızca özet kalır.

**Ayrıntı:** Bedelin somut sonucu: yardımcı bir hata yaptıysa ya da önemli bir
gözlemi özete koymadıysa senin haberin olmaz. Bu yüzden **kritik kararları
özete dayandırma — kanıt iste.**

| Devret | Devretme |
|---|---|
| Hacimli okuma, ara sonuç gereksiz | Birlikte düşünerek ilerlediğin iş |
| Bağımsız doğrulama | Ana işin doğrudan devamı |
| Günlük/çıktı analizi | Kısa, tek dosyalık soru |

📌 **Sık yapılan hata:** Devretmeyi süre ya da dosya sayısıyla kararlaştırmak.
Ölçüt ara sonuçların değeri.

🔗 [4.2 §1](4.2-subagent.md)

---

### Soru 3 — Yardımcının bağlamında ne yok? Pratik sonucu?

**Kısa cevap:** **Ana konuşmanın geçmişi** yok (ayrıca ana oturumun otomatik
belleği ve çıktı biçemi de yok).
Pratik sonuç: görev metnini yazarken yardımcının **hiçbir şey bilmediğini** varsay.

**Ayrıntı:** Var olanlar: kendi sistem talimatı, ana ajanın yazdığı görev metni,
kural dosyaları, ön yüklenen skill'ler.

"Az önce konuştuğumuz gibi devam et" bir yardımcı için **anlamsızdır**. Görev
metni kendi kendine yeter olmalı: dosya adları, ölçütler, beklenen çıktı biçimi.

📌 **Sık yapılan hata:** Görev metnini ana konuşmanın devamı gibi yazmak ve
yardımcı alakasız bir şey üretince "anlamadı" diye düşünmek.

🔗 [4.2 §4](4.2-subagent.md)

---

### Soru 4 — `tools` daraltmanın somut faydası.

**Kısa cevap:** Yazma aracı verilmezse yardımcı **yanlışlıkla dosya değiştiremez**
— talimatla değil, **yapısal olarak**.

**Ayrıntı:** Bu, en az yetki ilkesinin en kolay uygulandığı yer. "İncelerken
düzeltmeye kalkma" bir ricadır; yazma aracının olmaması bir **imkânsızlıktır**.

Aynı mantık `4.5`'te de geçiyor: **sunulmayan bir yetenek kötüye kullanılamaz.**

📌 **Sık yapılan hata:** Araç listesini boş bırakıp "nasılsa incelemeye
gelmiş, yazmaz" varsaymak.

🔗 [4.2 §3](4.2-subagent.md)

---

### Soru 5 — Token tasarrufu doğru mu? Kesin kazanç ne?

**Kısa cevap:** **Garanti değil** — her yardımcı kendi penceresini açar, toplam
token artabilir. **Kesin kazanç: ana bağlamın temiz kalması.**

**Ayrıntı:** Beş yardımcı, beş ayrı giriş/çıkış maliyeti demektir. Ama ana
pencere temiz kaldığı için oradaki **her tur** daha ucuz ve daha kaliteli olur.

Yani hesap şöyle: yardımcıların maliyeti bir kereliktir; kirlenmiş ana bağlamın
maliyeti **oturumun geri kalanına yayılır**.

📌 **Sık yapılan hata:** Kararı yalnızca token üzerinden vermek. Asıl ölçüt
bağlam kalitesi.

🔗 [4.2 §5](4.2-subagent.md)

---

## 4.3 Hook

### Soru 1 — Hook'u kural dosyasından ayıran üç özellik; hangisi belirleyici?

**Kısa cevap:** (1) Olayında **kesin çalışır**, (2) eylemi **engelleyebilir**,
(3) bağlam maliyeti **~sıfır**.
Belirleyici olan: **garanti** (ilk ikisi).

**Ayrıntı:**

| | Hook | Kural dosyası |
|---|---|---|
| Çalışma | **Kesin** | Model okur, genelde uyar |
| Engelleme | ✅ | ❌ |
| Bağlam | ~sıfır | Yer tutar |
| Yaşadığı yer | Yapılandırma | Metin |

Üçüncü satır sıkça atlanır ve hook'u **cazip** kılan şeydir: talimatı bağlamın
dışına çıkarırsın.

📌 **Sık yapılan hata:** Hook'u "daha resmî kural" sanmak. Farklı bir **katman**:
biri yönlendirir, diğeri zorlar.

🔗 [4.3 §1](4.3-hooklar.md)

---

### Soru 2 — "Hook bağlam harcar" doğru mu?

**Kısa cevap:** **Hayır** — yapılandırma bağlamın dışındadır. Yalnızca hook bir
**çıktı döndürürse** o çıktı bağlama girer.

**Ayrıntı:** Bu, hook'un en büyük gizli avantajı: kural dosyasına yazacağın bir
talimatı hook'a taşımak, hem garanti hem **bağlam tasarrufu** getirir.

Ama tersi de mümkün: her çalıştığında lint çıktısını olduğu gibi geri veren bir
hook, bağlamı hızla doldurur. **Sessiz çalış**, yalnızca ajanın gerçekten
bilmesi gerekeni yaz.

📌 **Sık yapılan hata:** Hook'u gürültülü yazıp "hook koydum, bağlam doluyor"
diye şaşırmak.

🔗 [4.3 §1](4.3-hooklar.md)

---

### Soru 3 — Çıkış kodları; sert engelleme hangisi?

**Kısa cevap:** **`2` = sert engelleme** (geçersiz kılınamaz). `0` = başarılı,
standart çıktıdaki JSON okunur. Diğerleri = **engellemeyen hata**.

**Ayrıntı:** Bu ayrımı bilmemek, "hook yazdım ama engellemiyor" durumunun en
sık sebebi — betik `exit 1` ile çıkıyordur.

Çıkış kodu `0` olduğunda hook'un yapabildikleri:

| JSON ile | Ne olur |
|---|---|
| İzin kararı | İzin ver / reddet |
| Ek bağlam | Ajana metin gönder |
| **Girdiyi değiştir** | Çağrılacak aracın girdisini yeniden yaz |

📌 **Sık yapılan hata:** Engellemeyi standart hataya mesaj yazarak yapmaya
çalışmak. Mesaj gerekli ama **çıkış kodu belirleyici**.

🔗 [4.3 §4](4.3-hooklar.md)

---

### Soru 4 — Girdi değiştirme hangi soruna kalıcı çözüm?

**Kısa cevap:** **Hacimli komut çıktılarının bağlamı doldurması** (`3.3`'teki
günlük tuzağı).

**Ayrıntı:** Her seferinde "günlüğü filtreleyerek oku" demeyi hatırlamak yerine,
bir kez yapılandırırsın: hook, komutu yalnızca başarısızlıkları gösterecek
biçimde **yeniden yazar**.

Kalıcı çözümün üstünlüğü: unutulmaz, ve **her oturumda, herkeste** çalışır —
proje ayarına yazılırsa ekip de yararlanır.

📌 **Sık yapılan hata:** Bu davranışı kural dosyasına yazmak. "Test çıktısını
filtrele" bir ricadır; hook onu **yapar**.

🔗 [4.3 §4](4.3-hooklar.md)

---

### Soru 5 — Klonlanan depoda hook riski ve iki savunma.

**Kısa cevap:** Deponun proje ayarındaki hook'lar **senin makinende kod
çalıştırabilir** — ve etkileşimsiz kipte güven onayı **sorulmaz**.
Savunmalar: (1) ayar dosyalarına **bak**, (2) **çıplak** başlat.

**Ayrıntı:** Bu, `4.6`'daki eklenti riskinin ve `6.1`'deki tedarik zinciri
riskinin aynı ailesi: **başkasının yazdığı yapılandırma, senin yetkilerinle
çalışıyor.**

| Savunma | Ne yapar |
|---|---|
| Ayar dosyalarını okumak | Ne çalışacağını önceden görürsün |
| Çıplak başlatma | Hook, skill, eklenti, sunucu **hiç yüklenmez** |

📌 **Sık yapılan hata:** "Kod okumadan çalıştırmam" deyip ajanı okumadan
başlatmak. Ajanı başlatmak da bir çalıştırmadır.

🔗 [4.3 §5](4.3-hooklar.md)

---

## 4.4 MCP ile bağlanmak

### Soru 1 — Üç taşıma türü; hangisi önerilmiyor?

**Kısa cevap:** **`stdio`** yerel süreç · **`http`** uzak (önerilen) ·
**`sse`** uzak ama **artık önerilmiyor**.

**Ayrıntı:**

| Tür | Nerede | Ne zaman |
|---|---|---|
| `stdio` | Senin makinende | Yerel araçlar, veritabanı, dosya erişimi |
| `http` | Uzak adres | Bulut servisleri |
| `sse` | Uzak adres | ⚠️ Eski belgelerde görürsen güncele taşındı mı bak |

Yerel süreç eklerken **`--` ayırıcısı şart**: onsuz sunucunun bayrakları ajanın
bayrakları sanılır.

📌 **Sık yapılan hata:** `--` unutup anlaşılmaz bir hata alınca kurulumun
bozuk olduğunu sanmak.

🔗 [4.4 §2](4.4-mcp-baglama.md)

---

### Soru 2 — Üç kapsam; hangisi paylaşılır, oraya ne yazılmaz?

**Kısa cevap:** **Yerel** (öntanımlı, yalnızca sen + bu proje) · **proje**
(depoya işlenir, **ekip görür**) · **kullanıcı** (sen + tüm projeler).
Proje kapsamına **gizli değer yazılmaz** — ortam değişkeni genişletmesi kullanılır.

**Ayrıntı:** Depoya yazılan her şey depoyu okuyabilen herkese açıktır ve git
geçmişinden silmek zordur. Bu yüzden bağlantı bilgisi `${DEGISKEN}` biçiminde
geçirilir; değerin kendisi ortamda durur.

Öntanımlının **yerel** olması güvenli bir varsayılan: bir deneme sunucusu
istemeden ekibe yayılmaz.

📌 **Sık yapılan hata:** Hızlıca çalışsın diye bağlantı dizesini `.mcp.json`
içine yazmak — ve commit'lemek.

🔗 [4.4 §2](4.4-mcp-baglama.md)

---

### Soru 3 — Araç arama ne yapar? Çok sunucunun iki maliyeti?

**Kısa cevap:** Araç arama, başta yalnızca **araç adlarını** yükler; ağır tam
tanımlar gerektiğinde çekilir.
İki maliyet: (1) **adlar birikir**, (2) benzer araçlar arasında **yanlış seçim
riski** artar.

**Ayrıntı:** Maliyet iki boyutlu — token **ve karar kalitesi**. Otuz benzer
araç adı arasından doğru olanı seçmek, üç araç arasından seçmekten zordur.

Bu yüzden en etkili tek önlem **kullanmadığın sunucuları kapatmak**: kapalı bir
sunucu ne ad ekler ne veri döndürür — hem yüzeyi hem bağlamı birlikte küçültür.

📌 **Sık yapılan hata:** "Nasılsa erteleniyor" diye çok sayıda sunucu bağlı
tutmak.

🔗 [4.4 §3](4.4-mcp-baglama.md)

---

### Soru 4 — Bağlanmanın iki riski; hangisi az anlaşılır?

**Kısa cevap:** (1) Sunucu **ajanının yetkilerini** kullanır. (2) Sunucu
**ajanın gözüne veri sokar**.
Az anlaşılan **ikincisi** — ve sunucunun kötü niyetli olması **gerekmez**.

**Ayrıntı:** Sunucunun döndürdüğü metin ajanın bağlamına girer. O metin bir hata
kaydı, bir web sayfası ya da bir sorun kaydı olabilir — hepsi **başkaları
tarafından yazılmış**. İçine talimat gizlenmişse ajan onu okur.

Yani dürüst bir sunucu, güvenilmez içeriği aktardığı için bir **istem enjeksiyonu
taşıyıcısı** olabilir.

| Önlem | Etkisi |
|---|---|
| Bilinen kaynak | Tedarik zinciri riskini düşürür |
| **Dar kapsam** | Zararı yapısal olarak sınırlar |
| İzin kuralları | Belirli araçları engeller |
| Kullanmadığını kapat | Yüzeyi ve bağlamı birlikte küçültür |

📌 **Sık yapılan hata:** Riski yalnızca "sunucu kötü niyetli olabilir" diye
düşünmek ve dürüst sunucuların taşıdığı içeriği gözden kaçırmak.

🔗 [4.4 §5](4.4-mcp-baglama.md)

---

### Soru 5 — Komut satırı aracı mı MCP mi?

**Kısa cevap:** **Komut satırı aracı varsa onu tercih et** — bağlam açısından
daha ucuzdur, hiçbir araç listesi eklemez. MCP'yi, komut satırı aracı olmayan
ya da yetersiz kaldığı yerlerde kullan.

**Ayrıntı:** Ajan `gh`, `aws`, `gcloud` gibi araçları zaten bilir ve doğrudan
komut çalıştırır — araç adı, şema, sunucu bağlantısı gerekmez.

MCP'nin üstün olduğu yerler: yapılandırılmış veri gerektiğinde, kimlik
doğrulamanın sunucu tarafında yönetilmesi gerektiğinde, ve komut satırı aracı
hiç olmadığında.

📌 **Sık yapılan hata:** "MCP daha modern" diye komut satırı aracı olan bir
sistem için sunucu bağlamak.

🔗 [4.4 §3](4.4-mcp-baglama.md)

---

## 4.5 Kendi MCP sunucun

### Soru 1 — Üç alternatif ve üç koşul.

**Kısa cevap:**
Alternatifler: **komut satırı aracı · skill · hazır sunucu.**
Koşullar (üçü birden): sistem **kapalı** + erişim **tekrarlanıyor** +
yapılandırılmış veri işi **belirgin biçimde** iyileştiriyor.

**Ayrıntı:** Üçü birden doğru değilse muhtemelen bir betik yeter. Sunucu yazmak
**bakım yükü** demektir: sürüm, hata, güvenlik, belge.

📌 **Sık yapılan hata:** Ajanın bir sistemi bilmemesini sunucu gerekçesi saymak.
Bilgi boşluğunu bir skill ya da belge bağlantısı kapatabilir.

🔗 [4.5 §1](4.5-mcp-sunucusu-yazma.md)

---

### Soru 2 — Altı tasarım ilkesi; en çok atlanan?

**Kısa cevap:** **Dar kapsam · açıklayıcı tarif · küçük çıktı · anlamlı hata ·
güvenli varsayılan · girdi doğrulama.**
En çok atlanan: **küçük çıktı** — atlanınca tek çağrı **bağlamı zehirleyebilir**.

**Ayrıntı:** MCP aracının döndürdüğü her şey ajanın bağlamına girer ve orada
**kalır**. Tüm tabloyu döndüren bir araç, tek çağrıda oturumu kullanılamaz hâle
getirir.

Bu yüzden **sayfalama ve alan seçimi bir tercih değil, zorunluluktur** — ve
sunucuyu bağlamadan önce **en büyük olası çıktıyı ölçmek** gerekir.

📌 **Sık yapılan hata:** Ortalama çıktıyı ölçüp "küçük" diye geçmek. Riski
belirleyen **en kötü durum**.

🔗 [4.5 §2](4.5-mcp-sunucusu-yazma.md)

---

### Soru 3 — Ajan aracı neye bakarak seçer? Hangi kuralın aynısı?

**Kısa cevap:** **Adına ve açıklamasına.** Bu, `4.1`'deki skill `description`
kuralının aynısı.

**Ayrıntı:** İki mekanizma da aynı ilkeye dayanıyor: **seçim, metne bakılarak
yapılır.** Dolayısıyla:

| Kötü | İyi |
|---|---|
| `veri_yonet` | `siparis_getir` |
| "Veri işlemleri" | "Sipariş kimliğiyle tek siparişin durumunu döner" |

Ortak kural: **ne yaptığını değil, ne zaman kullanılacağını** yaz.

📌 **Sık yapılan hata:** Araç adını iç mimarine göre koymak (`db_query_v2`).
Ajan senin mimarini bilmiyor; işlevi bilmesi gerekiyor.

🔗 [4.5 §2](4.5-mcp-sunucusu-yazma.md)

---

### Soru 4 — "Kendi sunucum güvenlidir" neden yanlış? En güçlü savunma?

**Kısa cevap:** Yazarlık güvenlik sağlamaz — sunucu **sen güvenli yaparsan**
güvenlidir. En güçlü tek savunma: **yıkıcı işlemleri hiç sunmamak.**

**Ayrıntı:** Riskler ve karşılıkları:

| Risk | Karşılık |
|---|---|
| Beklenmedik girdi | Şemayla doğrula, sınır koy |
| Yıkıcı aracın çağrılması | **Hiç sunma**, ya da onay iste |
| Dış metin taşıma | Sınırla, etiketle, gereksizi dönme |
| Kimlik sızıntısı | Ortam değişkeni, en az yetkili hesap |
| Aşırı yetki | Salt okunur bağlantı, dar kapsam |

**Sunulmayan bir aracı hiçbir enjeksiyon çağıramaz** — diğer önlemler var olan
bir yeteneğin kötüye kullanımını sınırlamaya çalışır; sunmamak yeteneği
ortadan kaldırır.

📌 **Sık yapılan hata:** "Yazma aracını koyayım, izin kuralıyla sınırlarım"
demek. Katman iyi ama zayıf; en iyisi aracın hiç olmaması.

🔗 [4.5 §4](4.5-mcp-sunucusu-yazma.md)

---

### Soru 5 — Dış metin döndürme riski ve üç hafifletme.

**Kısa cevap:** Risk: sunucun bir **istem enjeksiyonu taşıyıcısı** olur —
başkalarının yazdığı metin ajanın bağlamına girer.
Hafifletmeler: (1) dış metni **sınırla**, (2) **etiketle** ("bu içerik kullanıcı
tarafından yazılmıştır"), (3) gereksiz alanları **hiç döndürme**.

**Ayrıntı:** Kritik nokta: sunucunun kötü niyetli olması **gerekmiyor**. Dürüst
bir sunucu, kullanıcı yorumlarını ya da sorun açıklamalarını aktardığı için
saldırı taşıyıcısı olabilir.

Üçüncü hafifletme en etkilisi ve en kolayı: bir alan gerekmiyorsa döndürme.
Dönmeyen metin enjeksiyon taşıyamaz.

📌 **Sık yapılan hata:** Kaynak nesneyi olduğu gibi döndürmek. Hem bağlam
maliyeti hem enjeksiyon yüzeyi üretir.

🔗 [4.5 §4](4.5-mcp-sunucusu-yazma.md)

---

## 4.6 Eklenti ve paylaşım

### Soru 1 — Paketleme tetikleyicisi ve erken paketlemenin zararı.

**Kısa cevap:** Tetikleyici: **ikinci depo aynı kurulumu istiyor.**
Erken paketlemenin zararı: kurulum **kararlı hâle gelmeden** sürüm yönetimi
yükü kazanç getirmeden gelir.

**Ayrıntı:**

| Durum | Ne yap |
|---|---|
| Tek depo, kişisel | `.claude/` içinde bırak |
| Tüm projelerinde aynı | Kullanıcı düzeyine taşı |
| **İkinci depo** | **Paketle** |
| Ekip | Paketle + dağıt |
| Dışarıya açacaksın | Paketle + yayımla |

📌 **Sık yapılan hata:** Paketlemeyi bir "düzen" işi sanmak. Çözdüğü sorun
düzen değil, **kopyaların ayrışması**.

🔗 [4.6 §1](4.6-eklenti-ve-paylasim.md)

---

### Soru 2 — Eklentiye ne konur, ne konmaz?

**Kısa cevap:** **Konur:** skill · alt ajan tanımı · hook · MCP sunucu tanımı.
**Konmaz:** kural dosyaları.
Gerekçe: eklenti **davranış** taşır, **proje bilgisi** değil.

**Ayrıntı:** Kural dosyaları projenin komutlarını, mimarisini ve geleneklerini
anlatır. Başka bir depoda bunlar **yanlış** olur — "test komutu `npm test`"
bilgisi, başka bir projede zararlıdır.

Ayrım şu soruyla yapılır: *bu, ajanın **nasıl çalıştığıyla** mı ilgili, yoksa
**bu projeyle** mi?*

📌 **Sık yapılan hata:** Kural dosyasını da eklentiye koyup her projeye aynı
proje bilgisini dağıtmak.

🔗 [4.6 §2](4.6-eklenti-ve-paylasim.md)

---

### Soru 3 — Ad çakışması nerede çözülmüş, nerede değil?

**Kısa cevap:** **Eklentilerde çözülmüş** — eklenti skill'leri eklenti adıyla
nitelenir. **Yerel `.claude/` tanımlarında çözülmemiş** — aynı adlı iki tanım
sessiz bir üzerine yazmaya yol açar.

**Ayrıntı:** Yereldeki sorunun sinsiliği **sessizlik**: hangi tanımın
kazandığı öncelik sırasına bağlıdır ve bir uyarı almazsın. Belirti dolaylı
gelir: "iki yerde aynı adlı skill vardı, biri kayboldu."

Pratik alışkanlık: yerel tanımlarda **ayırt edici ad** kullan; kısa ve genel
adlardan (`test`, `deploy`, `review`) kaçın.

📌 **Sık yapılan hata:** Eklentideki korumayı yerel tanımlarda da var sanmak.

🔗 [4.6 §2](4.6-eklenti-ve-paylasim.md)

---

### Soru 4 — "Eklenti kurmak dosya kopyalamaktır" neden yanlış? Dört soru?

**Kısa cevap:** Yanlış çünkü **kod çalıştırmayı kabul etmektir**: hook'lar
makinende çalışır, MCP sunucuları senin yetkilerinle konuşur, skill'ler ajanının
davranışını değiştirir.

Dört soru:
1. **Hangi hook'ları var, ne yapıyorlar?**
2. **Hangi MCP sunucularını bağlıyor?**
3. **Ne kadar yetki istiyor?**
4. **Gizli değer istiyor mu?** (Ayrıca: kim yazdı, sürdürülüyor mu?)

**Ayrıntı:** Bir eklenti üç yüzeyi birden açar: **yürütme** (hook), **erişim**
(sunucu), **davranış** (skill). Bu yüzden kurulum kararı, bir bağımlılık
eklemekle aynı ciddiyette ele alınmalıdır.

📌 **Sık yapılan hata:** Adı popüler bir araca benzeyen paketi resmî sanmak.
Yazım hatası yakalayan sahteler klasik ve hâlâ etkili.

🔗 [4.6 §4](4.6-eklenti-ve-paylasim.md)

---

### Soru 5 — "Sürprizi olmasın" ilkesi ve üç uygulaması.

**Kısa cevap:** Kuran kişi, **kurmadan önce ne olacağını bilmeli.**
Üç uygulama: (1) hangi hook'ların hangi olaylarda çalıştığını **belgele**,
(2) **gizli değer isteme**, (3) **yıkıcı işlem sunma**.

**Ayrıntı:** Ölçüt basit ve kişisel: **sen başkasının eklentisini kurarken ne
bilmek isterdin?** Onu yaz.

İyi bir eklenti belgesinde bulunması gerekenler: ne yaptığı, **hangi hook'lar
hangi olaylarda**, **ne istemediği** (gizli değer, ağ erişimi), ve **hangi
yetkilerle** çalıştığı.

Ek olarak: **küçük ve tek konulu** tut, **sürüm ver** — kırıcı değişiklik ayırt
edilebilir olsun.

📌 **Sık yapılan hata:** Kendi eklentini belgelemeyi gereksiz sanmak, çünkü sen
zaten biliyorsun. Altı ay sonraki sen de bir başkasıdır.

🔗 [4.6 §5](4.6-eklenti-ve-paylasim.md)

---

## 4.7 Otomasyon ve SDK

### Soru 1 — "Denetimi kaldırır" ne demek? Pratik sonucu?

**Kısa cevap:** Ajan aynı ajandır; değişen tek şey **senin orada olmaman.**
Yerelde sana sorulan izin soruları, diff okumaların ve "dur" deme imkânın yok.
Pratik sonuç: **kısıtlar önceden ve açıkça yazılır.**

**Ayrıntı:** Yerelde güvenliğin bir kısmı **etkileşimden** geliyordu. Onu
kaldırınca yerine üç katman koyarsın: **izin kipi · dar araç listesi · yalıtım.**

📌 **Sık yapılan hata:** Yerelde iyi çalışan bir kurulumu olduğu gibi
otomasyona taşımak ve aynı güvenliği beklemek.

🔗 [4.7 §1](4.7-otomasyon-ve-sdk.md)

---

### Soru 2 — Etkileşimsiz kipte başlangıç izin kipi ve belirtisi.

**Kısa cevap:** **Manual.** Belirti: *"betik hiçbir şey yapmadan bitiyor"* —
izin verilmemiş ve soracak kimse yok.

**Ayrıntı:** Bu, otomasyonda en sık takılınan yer ve teşhisi kolay: çıktı
boş ya da "izin gerekiyor" benzeri bir sonuç dönüyorsa kip ve araç listesi
verilmemiştir.

Çözüm açık bir kip ve dar bir liste:

```bash
claude -p "..." --permission-mode dontAsk --allowedTools "Read,Edit,Bash(npm test)"
```

📌 **Sık yapılan hata:** Belirtiyi model ya da ağ sorunu sanıp orada aramak.

🔗 [4.7 §1](4.7-otomasyon-ve-sdk.md)

---

### Soru 3 — Çıplak başlatmanın iki gerekçesi.

**Kısa cevap:** (1) **Kalite/yeniden üretilebilirlik** — sonuç makineden
bağımsız olur. (2) **Güvenlik** — güvenmediğin bir depodaki hook ve sunucu
tanımları çalışmaz.

**Ayrıntı:** Öntanımlı etkileşimsiz çalıştırma, etkileşimli oturumun yüklediği
**her şeyi** yükler: hook, skill, eklenti, MCP sunucusu, bellek, kural dosyaları.

| | Öntanımlı | Çıplak |
|---|---|---|
| Yükleme | Hepsi | Hiçbiri |
| Sonuç | Makineye bağlı | **Her yerde aynı** |
| Yabancı depo riski | Var | **Yok** |

Gerekeni açıkça geçirirsin: ek sistem talimatı, ayarlar, sunucu yapılandırması.

📌 **Sık yapılan hata:** "`-p` zaten temiz bir ortam" varsaymak. Tam tersi.

🔗 [4.7 §3](4.7-otomasyon-ve-sdk.md)

---

### Soru 4 — Şema kullanmanın gerekçesi.

**Kısa cevap:** **Model çıktı biçimini değiştirir; şema değiştirmez.** Metin
ayrıştıran betik bir gün sessizce bozulur.

**Ayrıntı:** Şema verildiğinde sonuç, o şemaya uyan **yapılandırılmış bir
nesne** olarak döner — ayrıştırmaya gerek kalmaz.

Yapılandırılmış çıktının ek faydası: JSON biçimi oturum kimliği ve
**kullanım/maliyet** bilgisini de getirir, yani çalıştırma başına maliyeti
izleyebilirsin (`6.4`).

📌 **Sık yapılan hata:** Düzenli ifadeyle çıktı ayrıştıran betikler yazmak ve
model güncellenince neden bozulduğunu anlamamak.

🔗 [4.7 §2](4.7-otomasyon-ve-sdk.md)

---

### Soru 5 — Üç kısıt katmanı ve neyi engellediği.

**Kısa cevap:**

| Katman | Ne engeller |
|---|---|
| **İzin kipi** (`dontAsk`) | İzin verilmeyen **her şeyi** reddeder |
| **Dar araç listesi** | Yapılabilecek eylem **yüzeyini** daraltır |
| **Yalıtım** (kapsayıcı/sanal makine/çalışma kopyası) | Zararın **yayılmasını** çevreler |

**Ayrıntı:** Üçü **katmanlıdır**: biri aşılsa diğerleri devrede kalır. Buna
ayrıca **tur sınırı ve zaman aşımı** eklenir — bunlar zararı değil, **maliyeti
ve süreyi** sınırlar.

📌 **Sık yapılan hata:** Yalnızca yalıtıma güvenmek ("kapsayıcıda çalışıyor,
sorun yok"). Kapsayıcı içindeki kimlik bilgileri ve ağ erişimi hâlâ gerçektir.

🔗 [4.7 §4](4.7-otomasyon-ve-sdk.md)
