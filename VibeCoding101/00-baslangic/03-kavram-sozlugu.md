# Kavram sözlüğü

> **Bu dosya hatırlatma içindir, ilk öğrenme için değil.** Her terim konu dosyasında,
> ilk geçtiği cümlenin **içinde** açıklanır. Buraya "neydi bu ya?" dediğinde dönersin.
>
> Bir terimin derinlemesine işlendiği yer başka bir kursa düşüyorsa satırın sonunda
> o kursun numarası yazıyor — o kursu almadan da tanım burada tam olarak duruyor.
>
> 3 bölüm: [Ajanla çalışmanın kavramları](#a-ajanla-çalışmanın-kavramları) ·
> [Güvenlik, gizlilik ve hukuk](#b-güvenlik-gizlilik-ve-hukuk) ·
> [Araç ve ölçüt adları](#c-araç-ve-ölçüt-adları)

---

## A. Ajanla çalışmanın kavramları

#### Ajan

Kendi adımlarını planlayıp araç çağıran program. Sohbet botundan farkı: dosya okur,
komut çalıştırır, sonucu görür ve buna göre bir sonraki adıma karar verir.
→ [1.1 Ajan döngüsü](../01-ajanla-calismak/1.1-ajan-dongusu.md)

#### Ajan döngüsü

*plan → araç çağır → sonucu oku → tekrar değerlendir* çevrimi. İş bittiğine karar
verilene kadar döner. "Bitti" kararını **modelin kendisi** verir — bu setin
tamamındaki doğrulama vurgusu buradan gelir.
→ [1.1](../01-ajanla-calismak/1.1-ajan-dongusu.md)

#### Bağlam penceresi

Modelin tek istekte görebildiği metnin tamamı: sistem talimatı, konuşma geçmişi,
okunan dosyalar, komut çıktıları. Sınırlıdır ve **doldukça performans bozulur.**
Bu setin en kıt kaynağı.
→ [1.3](../01-ajanla-calismak/1.3-baglam-yonetimi.md)

#### Token

Metnin modele giden en küçük parçası; kabaca bir kelimenin bir bölümü. Hem bağlam
sınırı hem faturalama token üzerinden sayılır.
→ **401 · *Maliyet***

#### Sıkıştırma

İngilizcesi *compaction*. Bağlam dolmaya yaklaşınca eski konuşmanın özetle
değiştirilmesi. Oturum devam eder ama **ayrıntı kaybolur**; neyin hayatta kaldığı
bilinmesi gereken bir şeydir.
→ [1.3](../01-ajanla-calismak/1.3-baglam-yonetimi.md)

#### İstem önbelleği

İngilizcesi *prompt caching*. Her istekte tekrar gönderilen aynı önekin (sistem
talimatı, geçmiş) yeniden işlenmeyip önbellekten okunması. Ucuzdur — ama önbellek
ömrü dolduğunda ya da geçmiş değiştiğinde yeniden işlenir; uzun molalardan sonraki
ilk istek pahalıdır.
→ **401 · *Maliyet***

#### Düşünme bütçesi

İngilizcesi *extended thinking* / *effort*. Modelin cevaptan önce kendi kendine
yürüttüğü akıl yürütmeye ayrılan pay. Zor planlamada işe yarar, basit işlerde
sadece maliyettir; seviyesi ayarlanabilir.
→ [00.4 Ayar rehberi](04-ayar-rehberi.md#düşünme-ve-model)

#### Kontrol noktası

İngilizcesi *checkpoint*. Her istem gönderildiğinde alınan anlık görüntü; konuşmayı
ve dosyaları o ana geri sarabilirsin.
⚠️ **Yalnızca ajanın dosya düzenleme araçlarıyla yaptığı değişiklikleri kapsar**;
kabuk komutlarıyla olanları kapsamaz. Git'in yerine geçmez.
→ **201 · *Var olan koda dokunmak***

#### Plan modu

Ajanın okuyup plan çıkardığı ama **hiçbir şeyi değiştirmediği** kip. Planı
onaylayınca uygulamaya geçer.
→ [1.4](../01-ajanla-calismak/1.4-izinler-ve-plan-modu.md)

#### İzin kipi

İngilizcesi *permission mode*. Ajanın hangi eylemleri sormadan yapabileceğini
belirleyen taban ayar. Türleri ve ne zaman hangisi: [ayar rehberi](04-ayar-rehberi.md#izin-kipleri).
→ [1.4](../01-ajanla-calismak/1.4-izinler-ve-plan-modu.md)

#### Kum havuzu

İngilizcesi *sandbox*. Çalıştırılan komutların dosya sistemi ve ağ erişiminin
işletim sistemi düzeyinde kısıtlanması. İzin kipinden farkı: izin kipi **sorup
sormamayı**, kum havuzu **yapabileceğini** belirler.
→ [1.4](../01-ajanla-calismak/1.4-izinler-ve-plan-modu.md)

#### Etkileşimsiz kip

İngilizcesi *headless* / *non-interactive*. Ajanı tek komutla, soru sormadan
çalıştırma; betiklerin ve sürekli tümleştirme hatlarının kullandığı biçim.
→ **301 · *Otomasyon ve SDK***

#### Sürekli tümleştirme

İngilizcesi *continuous integration*, kısaltması **CI**. Her değişiklikte
testleri ve denetimleri otomatik çalıştıran hat. Ajan açısından önemi: orada
**soracak kimse yoktur**, dolayısıyla izinler ve araç listesi önceden yazılır.
→ **301 · *Otomasyon ve SDK***

#### Geliştirme kiti

İngilizcesi *SDK*. Aynı ajan döngüsünü kendi programının içinde çalıştırmanı
sağlayan kütüphane. Komut satırından farkı **akış denetimi**: araç onaylarına,
oturuma ve kanca noktalarına kodla müdahale edebilirsin.
→ **301 · *Otomasyon ve SDK***

#### Subagent

**Kendi ayrı bağlam penceresinde** çalışan yardımcı ajan. Onlarca dosya okur ama
ana konuşmaya yalnızca özeti döner. Bağlam korumanın ana aracı.
→ **301 · *Subagent***

#### Skill

Talimat, bilgi ya da çok adımlı iş akışı içeren markdown dosyası. **Yalnızca
kullanıldığında** bağlama yüklenir; yazılı yordamı dosyaya almanın yolu.
→ **301 · *Skill yazmak***

#### Hook

Belirli bir yaşam döngüsü olayında (dosya düzenlendikten sonra, komut
çalıştırılmadan önce…) **kesin olarak** çalışan betik, istek ya da denetim.
Kural dosyasındaki talimat bir ricadır; hook garantidir.
→ **301 · *Hook***

#### MCP

*Model Context Protocol* — ajanları dış sistemlere (veritabanı, sorun takip,
tarayıcı) bağlayan açık protokol. Adı ekranda böyle geçer; kısaltması öğrenilmeli.
Sürüm damgası tarihtir (ör. `2026-07-28`).
→ **301 · *MCP ile bağlanmak***

#### Eklenti

İngilizcesi *plugin*. Skill, hook, subagent ve MCP sunucularını **tek kurulabilir
paket** hâline getirme biçimi. Aynı kurulumu ikinci bir depoda istediğinde açılır.
→ **301 · *Eklenti ve paylaşım***

#### Kural dosyaları

`.claude/rules/` altındaki markdown dosyaları. Ana kural dosyasından farkı:
**yola göre kapsanabilir** — yalnızca eşleşen dosyalarla çalışılırken yüklenir.
→ [2.2](../02-projeye-kural-yazmak/2.2-kural-dosyalari.md)

#### CLAUDE.md

Claude Code'un **her oturum başında** okuduğu proje talimatı dosyası. Uzunsa
uyulmaz: hedef 200 satır altı.
→ [2.1](../02-projeye-kural-yazmak/2.1-claude-md.md)

#### AGENTS.md

Araçtan bağımsız, açık standart talimat dosyası. Ağustos 2025'te belirtim oldu,
Aralık 2025'te Linux Foundation çatısındaki Agentic AI Foundation'a devredildi.
Yirmiden fazla araç okur.
→ [2.3](../02-projeye-kural-yazmak/2.3-agents-md.md)

#### Otomatik bellek

Ajanın **kendi kendine** tuttuğu, oturumlar arası taşınan notlar. Sen yazmazsın;
düzeltmelerinden ve tercihlerinden çıkarır. Kural dosyasından farkı: yazarı sen değilsin.
→ [2.4](../02-projeye-kural-yazmak/2.4-kalici-bellek.md)

#### Çıktı biçemi

İngilizcesi *output style*. Ajanın rolünü kökten değiştiren, sistem talimatına
giren dosya. En ağır basan yönlendirme biçimi — ve o yüzden en az kullanılması gereken.
→ [00.4 Ayar rehberi](04-ayar-rehberi.md#yönlendirme-katmanları)

#### worktree

Aynı deponun ikinci bir çalışma kopyası. Git'in kendi özelliği; paralel ajan
oturumlarının birbirinin dosyalarını ezmemesi için kullanılır.
→ **201 · *Büyük dönüşümler***

#### Kod zekâsı

İngilizcesi *code intelligence*, altındaki teknoloji **LSP** (*Language Server
Protocol* — düzenleyicilerin dile özgü bilgi aldığı protokol). Ajan "bu sembol
nerede tanımlı?" sorusunu metin arayarak değil, dile sorarak cevaplar; daha az
dosya okur.
→ **201 · *Var olan koda dokunmak***

---

## B. Güvenlik, gizlilik ve hukuk

#### İstem enjeksiyonu

İngilizcesi *prompt injection*. Saldırganın, ajanın **okuyacağı** bir yere
(sorun kaydı, web sayfası, bağımlılık dosyası, hata izleme kaydı) talimat
yerleştirip ajanı kandırması. Ajanın gördüğü her dış içerik **veridir, komut
değildir** — ama model bunu kendiliğinden ayırt etmez.
→ **401 · *Güvenlik***

#### En az yetki

İngilizcesi *least privilege*. Bir sürece **işini yapmaya yetecek kadar** yetki
verme ilkesi. Ajanlarda karşılığı: izin kuralları, salt-okunur belirteçler,
kapsamı daraltılmış MCP sunucuları.
→ **401 · *Güvenlik***

---

## C. Araç ve ölçüt adları

Adından ne olduğu anlaşılmayanlar. Ayrıntılı karşılaştırma: **401 · *Alan haritası***.

| Ad | Ne | Türü |
|---|---|---|
| **Claude Code** | Anthropic'in terminal tabanlı ajanı; bu setin omurgası | Ajan |
| **Codex** | OpenAI'nin kodlama ajanı; terminal ve bulut | Ajan |
| **Cursor** | Ajan yetenekleri gömülü kod düzenleyici | Yapay zekâ destekli düzenleyici |
| **GitHub Copilot** | Düzenleyici içi tamamlama + ajan kipi | Karma |
| **Windsurf** | Ajan yetenekli düzenleyici; paralel ajan iddiası | Düzenleyici |
| **Gemini CLI** | Google'ın terminal ajanı; geniş ücretsiz katman | Ajan |
| **Aider** | Açık kaynak terminal ajanı, git odaklı | Ajan |
| **Cline** | Açık kaynak düzenleyici eklentisi ajanı | Ajan |
| **Zed** | Ajan tümleşik kod düzenleyici | Düzenleyici |
| **SWE-bench** | Gerçek depo sorunlarını çözme başarısını ölçen kıyas | Ölçüt |
| **Terminal-Bench** | Terminal işlerinde başarıyı ölçen kıyas | Ölçüt |

> ⚠️ Bu tablodaki ürün özellikleri **aylık değişiyor.** Karar vermeden önce
> aracın kendi belgesine bak; **401 · *Hangisi ne zaman*** kararı nasıl vereceğini öğretiyor,
> hangisinin kazandığını değil.
