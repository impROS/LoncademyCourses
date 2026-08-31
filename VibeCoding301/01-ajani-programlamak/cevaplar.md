# 01 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak.
> Her cevapta: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 1.1 Skill yazmak

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

🔗 [1.1 §1](1.1-skill-yazma.md)

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

🔗 [1.1 §2](1.1-skill-yazma.md)

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

🔗 [1.1 §5](1.1-skill-yazma.md)

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

🔗 [1.1 §5](1.1-skill-yazma.md) · **101 · *Bağlam penceresi***

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

🔗 [1.1 §3](1.1-skill-yazma.md)

---

## 1.2 Subagent

### Soru 1 — Asıl kazanç ve iki yanlış anlama.

**Kısa cevap:** Asıl kazanç **bağlam yalıtımı**.
Yanlış anlamalar: (1) "paralel hız için", (2) "her işi ona vermeli".

**Ayrıntı:** Paralellik bir **yan fayda**; asıl mesele onlarca dosyanın
yardımcının penceresinde okunup sana yalnızca özetin dönmesi.

"Her işi devret" yanlışı daha zararlı: ara sonuçlara ihtiyacın olan bir işi
devretmek, **gözünü kapatarak çalışmak** demektir.

📌 **Sık yapılan hata:** Token tasarrufu beklemek. Her yardımcı kendi
penceresini açar; toplam token bazen **artar**. Kazanç kaliteden gelir.

🔗 [1.2 §1](1.2-subagent.md)

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

🔗 [1.2 §1](1.2-subagent.md)

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

🔗 [1.2 §4](1.2-subagent.md)

---

### Soru 4 — `tools` daraltmanın somut faydası.

**Kısa cevap:** Yazma aracı verilmezse yardımcı **yanlışlıkla dosya değiştiremez**
— talimatla değil, **yapısal olarak**.

**Ayrıntı:** Bu, en az yetki ilkesinin en kolay uygulandığı yer. "İncelerken
düzeltmeye kalkma" bir ricadır; yazma aracının olmaması bir **imkânsızlıktır**.

Aynı mantık `2.2`'de de geçiyor: **sunulmayan bir yetenek kötüye kullanılamaz.**

📌 **Sık yapılan hata:** Araç listesini boş bırakıp "nasılsa incelemeye
gelmiş, yazmaz" varsaymak.

🔗 [1.2 §3](1.2-subagent.md)

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

🔗 [1.2 §5](1.2-subagent.md)

---

## 1.3 Hook

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

🔗 [1.3 §1](1.3-hooklar.md)

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

🔗 [1.3 §1](1.3-hooklar.md)

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

🔗 [1.3 §4](1.3-hooklar.md)

---

### Soru 4 — Girdi değiştirme hangi soruna kalıcı çözüm?

**Kısa cevap:** **Hacimli komut çıktılarının bağlamı doldurması** (**201 · *Hata ayıklama*** konusundaki
günlük tuzağı).

**Ayrıntı:** Her seferinde "günlüğü filtreleyerek oku" demeyi hatırlamak yerine,
bir kez yapılandırırsın: hook, komutu yalnızca başarısızlıkları gösterecek
biçimde **yeniden yazar**.

Kalıcı çözümün üstünlüğü: unutulmaz, ve **her oturumda, herkeste** çalışır —
proje ayarına yazılırsa ekip de yararlanır.

📌 **Sık yapılan hata:** Bu davranışı kural dosyasına yazmak. "Test çıktısını
filtrele" bir ricadır; hook onu **yapar**.

🔗 [1.3 §4](1.3-hooklar.md)

---

### Soru 5 — Klonlanan depoda hook riski ve iki savunma.

**Kısa cevap:** Deponun proje ayarındaki hook'lar **senin makinende kod
çalıştırabilir** — ve etkileşimsiz kipte güven onayı **sorulmaz**.
Savunmalar: (1) ayar dosyalarına **bak**, (2) **çıplak** başlat.

**Ayrıntı:** Bu, `2.3`'teki eklenti riskinin ve **401 · *Güvenlik*** konusundaki tedarik zinciri
riskinin aynı ailesi: **başkasının yazdığı yapılandırma, senin yetkilerinle
çalışıyor.**

| Savunma | Ne yapar |
|---|---|
| Ayar dosyalarını okumak | Ne çalışacağını önceden görürsün |
| Çıplak başlatma | Hook, skill, eklenti, sunucu **hiç yüklenmez** |

📌 **Sık yapılan hata:** "Kod okumadan çalıştırmam" deyip ajanı okumadan
başlatmak. Ajanı başlatmak da bir çalıştırmadır.

🔗 [1.3 §5](1.3-hooklar.md)

---
