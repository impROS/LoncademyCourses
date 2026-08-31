# Kavram sözlüğü

> **Bu dosya hatırlatma içindir, ilk öğrenme için değil.** Her terim konu dosyasında,
> ilk geçtiği cümlenin **içinde** açıklanır. Buraya "neydi bu ya?" dediğinde dönersin.
>
> Üç bölüm: [Ajanla çalışmanın kavramları](#a-ajanla-çalışmanın-kavramları) ·
> [Güvenlik, gizlilik ve hukuk](#b-güvenlik-gizlilik-ve-hukuk) ·
> [Araç ve ölçüt adları](#c-araç-ve-ölçüt-adları)

---

<a id="a-ajanla-çalışmanın-kavramları"></a>
## A. Ajanla çalışmanın kavramları

<a id="ajan"></a>
#### Ajan

Kendi adımlarını planlayıp araç çağıran program. Sohbet botundan farkı: dosya okur,
komut çalıştırır, sonucu görür ve buna göre bir sonraki adıma karar verir.
→ [1.1 Ajan döngüsü](../01-temeller/1.1-ajan-dongusu.md)

<a id="ajan-döngüsü"></a>
#### Ajan döngüsü

*plan → araç çağır → sonucu oku → tekrar değerlendir* çevrimi. İş bittiğine karar
verilene kadar döner. "Bitti" kararını **modelin kendisi** verir — bu setin
tamamındaki doğrulama vurgusu buradan gelir.
→ [1.1](../01-temeller/1.1-ajan-dongusu.md)

<a id="bağlam-penceresi"></a>
#### Bağlam penceresi

Modelin tek istekte görebildiği metnin tamamı: sistem talimatı, konuşma geçmişi,
okunan dosyalar, komut çıktıları. Sınırlıdır ve **doldukça performans bozulur.**
Bu setin en kıt kaynağı.
→ [1.3](../01-temeller/1.3-baglam-yonetimi.md)

<a id="token"></a>
#### Token

Metnin modele giden en küçük parçası; kabaca bir kelimenin bir bölümü. Hem bağlam
sınırı hem faturalama token üzerinden sayılır.
→ [6.4](../06-uretim-ve-ekip/6.4-maliyet-yonetimi.md)

<a id="sıkıştırma"></a>
#### Sıkıştırma

İngilizcesi *compaction*. Bağlam dolmaya yaklaşınca eski konuşmanın özetle
değiştirilmesi. Oturum devam eder ama **ayrıntı kaybolur**; neyin hayatta kaldığı
bilinmesi gereken bir şeydir.
→ [1.3](../01-temeller/1.3-baglam-yonetimi.md)

<a id="istem-önbelleği"></a>
#### İstem önbelleği

İngilizcesi *prompt caching*. Her istekte tekrar gönderilen aynı önekin (sistem
talimatı, geçmiş) yeniden işlenmeyip önbellekten okunması. Ucuzdur — ama önbellek
ömrü dolduğunda ya da geçmiş değiştiğinde yeniden işlenir; uzun molalardan sonraki
ilk istek pahalıdır.
→ [6.4](../06-uretim-ve-ekip/6.4-maliyet-yonetimi.md)

<a id="düşünme-bütçesi"></a>
#### Düşünme bütçesi

İngilizcesi *extended thinking* / *effort*. Modelin cevaptan önce kendi kendine
yürüttüğü akıl yürütmeye ayrılan pay. Zor planlamada işe yarar, basit işlerde
sadece maliyettir; seviyesi ayarlanabilir.
→ [00.4 Ayar rehberi](04-ayar-rehberi.md#düşünme-ve-model)

<a id="kontrol-noktası"></a>
#### Kontrol noktası

İngilizcesi *checkpoint*. Her istem gönderildiğinde alınan anlık görüntü; konuşmayı
ve dosyaları o ana geri sarabilirsin.
⚠️ **Yalnızca ajanın dosya düzenleme araçlarıyla yaptığı değişiklikleri kapsar**;
kabuk komutlarıyla olanları kapsamaz. Git'in yerine geçmez.
→ [3.2](../03-is-akislari/3.2-var-olan-koda-dokunmak.md)

<a id="plan-modu"></a>
#### Plan modu

Ajanın okuyup plan çıkardığı ama **hiçbir şeyi değiştirmediği** kip. Planı
onaylayınca uygulamaya geçer.
→ [1.4](../01-temeller/1.4-izinler-ve-plan-modu.md)

<a id="izin-kipi"></a>
#### İzin kipi

İngilizcesi *permission mode*. Ajanın hangi eylemleri sormadan yapabileceğini
belirleyen taban ayar. Türleri ve ne zaman hangisi: [ayar rehberi](04-ayar-rehberi.md#izin-kipleri).
→ [1.4](../01-temeller/1.4-izinler-ve-plan-modu.md)

<a id="kum-havuzu"></a>
#### Kum havuzu

İngilizcesi *sandbox*. Çalıştırılan komutların dosya sistemi ve ağ erişiminin
işletim sistemi düzeyinde kısıtlanması. İzin kipinden farkı: izin kipi **sorup
sormamayı**, kum havuzu **yapabileceğini** belirler.
→ [1.4](../01-temeller/1.4-izinler-ve-plan-modu.md)

<a id="etkileşimsiz-kip"></a>
#### Etkileşimsiz kip

İngilizcesi *headless* / *non-interactive*. Ajanı tek komutla, soru sormadan
çalıştırma; betiklerin ve sürekli tümleştirme hatlarının kullandığı biçim.
→ [4.7](../04-kendi-aletini-yap/4.7-otomasyon-ve-sdk.md)

<a id="sürekli-tümleştirme"></a>
#### Sürekli tümleştirme

İngilizcesi *continuous integration*, kısaltması **CI**. Her değişiklikte
testleri ve denetimleri otomatik çalıştıran hat. Ajan açısından önemi: orada
**soracak kimse yoktur**, dolayısıyla izinler ve araç listesi önceden yazılır.
→ [4.7](../04-kendi-aletini-yap/4.7-otomasyon-ve-sdk.md)

<a id="geliştirme-kiti"></a>
#### Geliştirme kiti

İngilizcesi *SDK*. Aynı ajan döngüsünü kendi programının içinde çalıştırmanı
sağlayan kütüphane. Komut satırından farkı **akış denetimi**: araç onaylarına,
oturuma ve kanca noktalarına kodla müdahale edebilirsin.
→ [4.7](../04-kendi-aletini-yap/4.7-otomasyon-ve-sdk.md)

<a id="subagent"></a>
#### Subagent

**Kendi ayrı bağlam penceresinde** çalışan yardımcı ajan. Onlarca dosya okur ama
ana konuşmaya yalnızca özeti döner. Bağlam korumanın ana aracı.
→ [4.2](../04-kendi-aletini-yap/4.2-subagent.md)

<a id="fork"></a>
#### Fork

Sıfırdan başlamak yerine **o ana kadarki konuşmanın tamamını devralan** subagent
türü. Bağlamı zaten paylaştığı için önbelleği de paylaşır, ucuzdur.
→ [4.2](../04-kendi-aletini-yap/4.2-subagent.md)

<a id="skill"></a>
#### Skill

Talimat, bilgi ya da çok adımlı iş akışı içeren markdown dosyası. **Yalnızca
kullanıldığında** bağlama yüklenir; yazılı yordamı dosyaya almanın yolu.
→ [4.1](../04-kendi-aletini-yap/4.1-skill-yazma.md)

<a id="hook"></a>
#### Hook

Belirli bir yaşam döngüsü olayında (dosya düzenlendikten sonra, komut
çalıştırılmadan önce…) **kesin olarak** çalışan betik, istek ya da denetim.
Kural dosyasındaki talimat bir ricadır; hook garantidir.
→ [4.3](../04-kendi-aletini-yap/4.3-hooklar.md)

<a id="mcp"></a>
#### MCP

*Model Context Protocol* — ajanları dış sistemlere (veritabanı, sorun takip,
tarayıcı) bağlayan açık protokol. Adı ekranda böyle geçer; kısaltması öğrenilmeli.
Sürüm damgası tarihtir (ör. `2026-07-28`).
→ [4.4](../04-kendi-aletini-yap/4.4-mcp-baglama.md)

<a id="stdio-ve-http-taşıma"></a>
#### stdio ve http taşıma

Bir MCP sunucusuna nasıl bağlanıldığı. `stdio` = makinende yerel bir süreç olarak
çalışır; `http` = uzak bir adrese bağlanır. Üçüncüsü `sse` artık önerilmiyor.
→ [4.4](../04-kendi-aletini-yap/4.4-mcp-baglama.md)

<a id="eklenti"></a>
#### Eklenti

İngilizcesi *plugin*. Skill, hook, subagent ve MCP sunucularını **tek kurulabilir
paket** hâline getirme biçimi. Aynı kurulumu ikinci bir depoda istediğinde açılır.
→ [4.6](../04-kendi-aletini-yap/4.6-eklenti-ve-paylasim.md)

<a id="kural-dosyaları"></a>
#### Kural dosyaları

`.claude/rules/` altındaki markdown dosyaları. Ana kural dosyasından farkı:
**yola göre kapsanabilir** — yalnızca eşleşen dosyalarla çalışılırken yüklenir.
→ [2.2](../02-proje-kurallari/2.2-kural-dosyalari.md)

<a id="claudemd"></a>
#### CLAUDE.md

Claude Code'un **her oturum başında** okuduğu proje talimatı dosyası. Uzunsa
uyulmaz: hedef 200 satır altı.
→ [2.1](../02-proje-kurallari/2.1-claude-md.md)

<a id="agentsmd"></a>
#### AGENTS.md

Araçtan bağımsız, açık standart talimat dosyası. Ağustos 2025'te belirtim oldu,
Aralık 2025'te Linux Foundation çatısındaki Agentic AI Foundation'a devredildi.
Yirmiden fazla araç okur.
→ [2.3](../02-proje-kurallari/2.3-agents-md.md)

<a id="otomatik-bellek"></a>
#### Otomatik bellek

Ajanın **kendi kendine** tuttuğu, oturumlar arası taşınan notlar. Sen yazmazsın;
düzeltmelerinden ve tercihlerinden çıkarır. Kural dosyasından farkı: yazarı sen değilsin.
→ [2.4](../02-proje-kurallari/2.4-kalici-bellek.md)

<a id="çıktı-biçemi"></a>
#### Çıktı biçemi

İngilizcesi *output style*. Ajanın rolünü kökten değiştiren, sistem talimatına
giren dosya. En ağır basan yönlendirme biçimi — ve o yüzden en az kullanılması gereken.
→ [00.4 Ayar rehberi](04-ayar-rehberi.md#yönlendirme-katmanları)

<a id="worktree"></a>
#### worktree

Aynı deponun ikinci bir çalışma kopyası. Git'in kendi özelliği; paralel ajan
oturumlarının birbirinin dosyalarını ezmemesi için kullanılır.
→ [3.7](../03-is-akislari/3.7-buyuk-donusum.md)

<a id="kod-zekâsı"></a>
#### Kod zekâsı

İngilizcesi *code intelligence*, altındaki teknoloji **LSP** (*Language Server
Protocol* — düzenleyicilerin dile özgü bilgi aldığı protokol). Ajan "bu sembol
nerede tanımlı?" sorusunu metin arayarak değil, dile sorarak cevaplar; daha az
dosya okur.
→ [3.2](../03-is-akislari/3.2-var-olan-koda-dokunmak.md)

<a id="araç-arama"></a>
#### Araç arama

İngilizcesi *tool search*. Bağlı MCP sunucularının **tam tanımlarını** baştan
yüklemeyip gerektiğinde çekme davranışı. Çok sunucu bağlıyken bağlamı kurtarır.
→ [4.4](../04-kendi-aletini-yap/4.4-mcp-baglama.md)

---

<a id="b-güvenlik-gizlilik-ve-hukuk"></a>
## B. Güvenlik, gizlilik ve hukuk

<a id="istem-enjeksiyonu"></a>
#### İstem enjeksiyonu

İngilizcesi *prompt injection*. Saldırganın, ajanın **okuyacağı** bir yere
(sorun kaydı, web sayfası, bağımlılık dosyası, hata izleme kaydı) talimat
yerleştirip ajanı kandırması. Ajanın gördüğü her dış içerik **veridir, komut
değildir** — ama model bunu kendiliğinden ayırt etmez.
→ [6.1](../06-uretim-ve-ekip/6.1-guvenlik.md)

<a id="tedarik-zinciri-saldırısı"></a>
#### Tedarik zinciri saldırısı

Kodun kendisine değil, **kodun bağlı olduğu şeye** yapılan saldırı: zehirli bir
paket, kötü niyetli bir MCP sunucusu, sahte bir eklenti. Ajan çağında yüzey
büyüdü, çünkü ajan bunların hepsini kendi yetkisiyle çalıştırır.
→ [6.1](../06-uretim-ve-ekip/6.1-guvenlik.md)

<a id="en-az-yetki"></a>
#### En az yetki

İngilizcesi *least privilege*. Bir sürece **işini yapmaya yetecek kadar** yetki
verme ilkesi. Ajanlarda karşılığı: izin kuralları, salt-okunur belirteçler,
kapsamı daraltılmış MCP sunucuları.
→ [6.1](../06-uretim-ve-ekip/6.1-guvenlik.md)

<a id="sıfır-veri-saklama"></a>
#### Sıfır veri saklama

İngilizcesi *zero data retention*, kısaltması **ZDR**. Sağlayıcının istek
içeriğini işledikten sonra saklamadığı yapılandırma. Kurumsal sözleşmelerde
geçer; kendiliğinden açık değildir.
→ [6.2](../06-uretim-ve-ekip/6.2-gizlilik-ve-veri.md)

<a id="copyleft"></a>
#### Copyleft

Türetilmiş çalışmanın da aynı lisansla dağıtılmasını zorunlu kılan lisans ailesi
(**GPL** — *GNU General Public License* — en bilineni). Ajan çıktısı böyle bir
kaynaktan parça üretirse yükümlülük **fark edilmeden** bulaşabilir.
→ [6.3](../06-uretim-ve-ekip/6.3-lisans-ve-telif.md)

<a id="insan-yazarlığı"></a>
#### İnsan yazarlığı

Telif korumasının insan yaratıcılığı gerektirmesi ilkesi. Yalnızca istem yazmak
genelde bu eşiği karşılamaz; tamamen makine üretimi kod telifle korunmayabilir.
⚠️ Ülkeye göre değişir, hukuki tavsiye değildir.
→ [6.3](../06-uretim-ve-ekip/6.3-lisans-ve-telif.md)

---

<a id="c-araç-ve-ölçüt-adları"></a>
## C. Araç ve ölçüt adları

Adından ne olduğu anlaşılmayanlar. Ayrıntılı karşılaştırma: [5.1](../05-arac-haritasi/5.1-arac-haritasi.md).

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
> aracın kendi belgesine bak; `5.2` kararı nasıl vereceğini öğretiyor,
> hangisinin kazandığını değil.
