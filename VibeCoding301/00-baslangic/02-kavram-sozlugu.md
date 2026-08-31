# Kavram sözlüğü

> **Bu dosya hatırlatma içindir, ilk öğrenme için değil.** Her terim konu dosyasında,
> ilk geçtiği cümlenin **içinde** açıklanır. Buraya "neydi bu ya?" dediğinde dönersin.
>
> Bir terimin derinlemesine işlendiği yer başka bir kursa düşüyorsa satırın sonunda
> o kursun numarası yazıyor — o kursu almadan da tanım burada tam olarak duruyor.
>
> 2 bölüm: [Ajanla çalışmanın kavramları](#a-ajanla-çalışmanın-kavramları) ·
> [Güvenlik, gizlilik ve hukuk](#b-güvenlik-gizlilik-ve-hukuk)

---

## A. Ajanla çalışmanın kavramları

#### Ajan

Kendi adımlarını planlayıp araç çağıran program. Sohbet botundan farkı: dosya okur,
komut çalıştırır, sonucu görür ve buna göre bir sonraki adıma karar verir.
→ **101 · *Ajan döngüsü***

#### Ajan döngüsü

*plan → araç çağır → sonucu oku → tekrar değerlendir* çevrimi. İş bittiğine karar
verilene kadar döner. "Bitti" kararını **modelin kendisi** verir — bu setin
tamamındaki doğrulama vurgusu buradan gelir.
→ **101 · *Ajan döngüsü***

#### Bağlam penceresi

Modelin tek istekte görebildiği metnin tamamı: sistem talimatı, konuşma geçmişi,
okunan dosyalar, komut çıktıları. Sınırlıdır ve **doldukça performans bozulur.**
Bu setin en kıt kaynağı.
→ **101 · *Bağlam penceresi***

#### Token

Metnin modele giden en küçük parçası; kabaca bir kelimenin bir bölümü. Hem bağlam
sınırı hem faturalama token üzerinden sayılır.
→ **401 · *Maliyet***

#### Sıkıştırma

İngilizcesi *compaction*. Bağlam dolmaya yaklaşınca eski konuşmanın özetle
değiştirilmesi. Oturum devam eder ama **ayrıntı kaybolur**; neyin hayatta kaldığı
bilinmesi gereken bir şeydir.
→ **101 · *Bağlam penceresi***

#### Plan modu

Ajanın okuyup plan çıkardığı ama **hiçbir şeyi değiştirmediği** kip. Planı
onaylayınca uygulamaya geçer.
→ **101 · *İzinler ve plan modu***

#### İzin kipi

İngilizcesi *permission mode*. Ajanın hangi eylemleri sormadan yapabileceğini
belirleyen taban ayar. Türleri ve ne zaman hangisi: [ayar rehberi](03-ayar-rehberi.md#izin-kipleri).
→ **101 · *İzinler ve plan modu***

#### Kum havuzu

İngilizcesi *sandbox*. Çalıştırılan komutların dosya sistemi ve ağ erişiminin
işletim sistemi düzeyinde kısıtlanması. İzin kipinden farkı: izin kipi **sorup
sormamayı**, kum havuzu **yapabileceğini** belirler.
→ **101 · *İzinler ve plan modu***

#### Etkileşimsiz kip

İngilizcesi *headless* / *non-interactive*. Ajanı tek komutla, soru sormadan
çalıştırma; betiklerin ve sürekli tümleştirme hatlarının kullandığı biçim.
→ [2.4](../02-dis-dunyaya-baglamak/2.4-otomasyon-ve-sdk.md)

#### Sürekli tümleştirme

İngilizcesi *continuous integration*, kısaltması **CI**. Her değişiklikte
testleri ve denetimleri otomatik çalıştıran hat. Ajan açısından önemi: orada
**soracak kimse yoktur**, dolayısıyla izinler ve araç listesi önceden yazılır.
→ [2.4](../02-dis-dunyaya-baglamak/2.4-otomasyon-ve-sdk.md)

#### Geliştirme kiti

İngilizcesi *SDK*. Aynı ajan döngüsünü kendi programının içinde çalıştırmanı
sağlayan kütüphane. Komut satırından farkı **akış denetimi**: araç onaylarına,
oturuma ve kanca noktalarına kodla müdahale edebilirsin.
→ [2.4](../02-dis-dunyaya-baglamak/2.4-otomasyon-ve-sdk.md)

#### Subagent

**Kendi ayrı bağlam penceresinde** çalışan yardımcı ajan. Onlarca dosya okur ama
ana konuşmaya yalnızca özeti döner. Bağlam korumanın ana aracı.
→ [1.2](../01-ajani-programlamak/1.2-subagent.md)

#### Fork

Sıfırdan başlamak yerine **o ana kadarki konuşmanın tamamını devralan** subagent
türü. Bağlamı zaten paylaştığı için önbelleği de paylaşır, ucuzdur.
→ [1.2](../01-ajani-programlamak/1.2-subagent.md)

#### Skill

Talimat, bilgi ya da çok adımlı iş akışı içeren markdown dosyası. **Yalnızca
kullanıldığında** bağlama yüklenir; yazılı yordamı dosyaya almanın yolu.
→ [1.1](../01-ajani-programlamak/1.1-skill-yazma.md)

#### Hook

Belirli bir yaşam döngüsü olayında (dosya düzenlendikten sonra, komut
çalıştırılmadan önce…) **kesin olarak** çalışan betik, istek ya da denetim.
Kural dosyasındaki talimat bir ricadır; hook garantidir.
→ [1.3](../01-ajani-programlamak/1.3-hooklar.md)

#### MCP

*Model Context Protocol* — ajanları dış sistemlere (veritabanı, sorun takip,
tarayıcı) bağlayan açık protokol. Adı ekranda böyle geçer; kısaltması öğrenilmeli.
Sürüm damgası tarihtir (ör. `2026-07-28`).
→ [2.1](../02-dis-dunyaya-baglamak/2.1-mcp-baglama.md)

#### stdio ve http taşıma

Bir MCP sunucusuna nasıl bağlanıldığı. `stdio` = makinende yerel bir süreç olarak
çalışır; `http` = uzak bir adrese bağlanır. Üçüncüsü `sse` artık önerilmiyor.
→ [2.1](../02-dis-dunyaya-baglamak/2.1-mcp-baglama.md)

#### Eklenti

İngilizcesi *plugin*. Skill, hook, subagent ve MCP sunucularını **tek kurulabilir
paket** hâline getirme biçimi. Aynı kurulumu ikinci bir depoda istediğinde açılır.
→ [2.3](../02-dis-dunyaya-baglamak/2.3-eklenti-ve-paylasim.md)

#### Kural dosyaları

`.claude/rules/` altındaki markdown dosyaları. Ana kural dosyasından farkı:
**yola göre kapsanabilir** — yalnızca eşleşen dosyalarla çalışılırken yüklenir.
→ **101 · *Kural dosyaları***

#### CLAUDE.md

Claude Code'un **her oturum başında** okuduğu proje talimatı dosyası. Uzunsa
uyulmaz: hedef 200 satır altı.
→ **101 · *CLAUDE.md***

#### AGENTS.md

Araçtan bağımsız, açık standart talimat dosyası. Ağustos 2025'te belirtim oldu,
Aralık 2025'te Linux Foundation çatısındaki Agentic AI Foundation'a devredildi.
Yirmiden fazla araç okur.
→ **101 · *AGENTS.md***

#### Çıktı biçemi

İngilizcesi *output style*. Ajanın rolünü kökten değiştiren, sistem talimatına
giren dosya. En ağır basan yönlendirme biçimi — ve o yüzden en az kullanılması gereken.
→ [00.4 Ayar rehberi](03-ayar-rehberi.md#yönlendirme-katmanları)

#### worktree

Aynı deponun ikinci bir çalışma kopyası. Git'in kendi özelliği; paralel ajan
oturumlarının birbirinin dosyalarını ezmemesi için kullanılır.
→ **201 · *Büyük dönüşümler***

#### Araç arama

İngilizcesi *tool search*. Bağlı MCP sunucularının **tam tanımlarını** baştan
yüklemeyip gerektiğinde çekme davranışı. Çok sunucu bağlıyken bağlamı kurtarır.
→ [2.1](../02-dis-dunyaya-baglamak/2.1-mcp-baglama.md)

---

---

## B. Güvenlik, gizlilik ve hukuk

#### İstem enjeksiyonu

İngilizcesi *prompt injection*. Saldırganın, ajanın **okuyacağı** bir yere
(sorun kaydı, web sayfası, bağımlılık dosyası, hata izleme kaydı) talimat
yerleştirip ajanı kandırması. Ajanın gördüğü her dış içerik **veridir, komut
değildir** — ama model bunu kendiliğinden ayırt etmez.
→ **401 · *Güvenlik***

#### Tedarik zinciri saldırısı

Kodun kendisine değil, **kodun bağlı olduğu şeye** yapılan saldırı: zehirli bir
paket, kötü niyetli bir MCP sunucusu, sahte bir eklenti. Ajan çağında yüzey
büyüdü, çünkü ajan bunların hepsini kendi yetkisiyle çalıştırır.
→ **401 · *Güvenlik***

#### En az yetki

İngilizcesi *least privilege*. Bir sürece **işini yapmaya yetecek kadar** yetki
verme ilkesi. Ajanlarda karşılığı: izin kuralları, salt-okunur belirteçler,
kapsamı daraltılmış MCP sunucuları.
→ **401 · *Güvenlik***
