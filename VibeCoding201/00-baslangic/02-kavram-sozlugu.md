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

#### Kontrol noktası

İngilizcesi *checkpoint*. Her istem gönderildiğinde alınan anlık görüntü; konuşmayı
ve dosyaları o ana geri sarabilirsin.
⚠️ **Yalnızca ajanın dosya düzenleme araçlarıyla yaptığı değişiklikleri kapsar**;
kabuk komutlarıyla olanları kapsamaz. Git'in yerine geçmez.
→ [1.2](../01-kod-yazdirmak/1.2-var-olan-koda-dokunmak.md)

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
→ **301 · *Otomasyon ve SDK***

#### Sürekli tümleştirme

İngilizcesi *continuous integration*, kısaltması **CI**. Her değişiklikte
testleri ve denetimleri otomatik çalıştıran hat. Ajan açısından önemi: orada
**soracak kimse yoktur**, dolayısıyla izinler ve araç listesi önceden yazılır.
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
→ **101 · *Kural dosyaları***

#### CLAUDE.md

Claude Code'un **her oturum başında** okuduğu proje talimatı dosyası. Uzunsa
uyulmaz: hedef 200 satır altı.
→ **101 · *CLAUDE.md***

#### worktree

Aynı deponun ikinci bir çalışma kopyası. Git'in kendi özelliği; paralel ajan
oturumlarının birbirinin dosyalarını ezmemesi için kullanılır.
→ [2.4](../02-kaliteyi-guvenceye-almak/2.4-buyuk-donusum.md)

#### Kod zekâsı

İngilizcesi *code intelligence*, altındaki teknoloji **LSP** (*Language Server
Protocol* — düzenleyicilerin dile özgü bilgi aldığı protokol). Ajan "bu sembol
nerede tanımlı?" sorusunu metin arayarak değil, dile sorarak cevaplar; daha az
dosya okur.
→ [1.2](../01-kod-yazdirmak/1.2-var-olan-koda-dokunmak.md)

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
