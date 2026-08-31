# Cheatsheet — her konunun tek satırı

> Tek sayfalık hatırlatma. Bir konuyu unuttuysan satırı oku, yetmezse
> bağlantısına git. Terim tanımadıysan
> [kavram sözlüğü](../00-baslangic/03-kavram-sozlugu.md).

---

## Konular tek satırda

### 00 — Başlangıç
| # | Tek satır |
|---|---|
| [00.1](../00-baslangic/01-genel-bakis-ve-trickler.md) | Terim atılacak projeler için tarif edildi; ajan mühendisliği ondan **denetim** kadar farklı. Kod yazmak ucuzladı, **doğru olduğunu bilmek** ucuzlamadı. |
| [00.2](../00-baslangic/02-kurulum.md) | Kurulum komutunu **resmî kaynaktan** al; örnek proje kasten küçük — gözlenecek şey ajanın davranışı. |
| [00.3](../00-baslangic/05-plan-ve-maliyet.md) | Fatura modeli değil **bağlamın büyüklüğü** belirler; üst plana **ölçtükten sonra** geç. |

### 01 — Temeller
| # | Tek satır |
|---|---|
| [1.1](../01-temeller/1.1-ajan-dongusu.md) | Ajan = **araç çağıran döngü**; araç sonucu bağlamda **kalır**; "bitti" kararını **model** verir. |
| [1.2](../01-temeller/1.2-istem-yazma.md) | İstemin dört parçası: **hedef · kaynak · kısıt · kabul ölçütü**. Eksik parça = düzeltme sayısı. |
| [1.3](../01-temeller/1.3-baglam-yonetimi.md) | Bağlam **birikir, akmaz**; **dolma görünür, bozulma sessiz**. İlgisiz işler arasında **temizle**. |
| [1.4](../01-temeller/1.4-izinler-ve-plan-modu.md) | İzin kipi **sorup sormamayı**, kum havuzu **yapabilmeyi** belirler. Plan modu belirsizlik aracı, güvenlik aracı **değil**. |
| [1.5](../01-temeller/1.5-dogrulama-refleksi.md) | **Doğrulayamadığın şeyi teslim etme.** Dört düzey: istem içi · hedef · durdurma hook'u · ikinci göz. |

### 02 — Proje kuralları
| # | Tek satır |
|---|---|
| [2.1](../02-proje-kurallari/2.1-claude-md.md) | Her isteğe binen **sabit vergi**; ölçüt *"silsem hata yapar mı?"*; **şişme paradoksu**: uzun dosya = kaybolan kural. |
| [2.2](../02-proje-kurallari/2.2-kural-dosyalari.md) | Tasarrufu **bölme değil kapsamlama** sağlar; kapsanmış kural **eşleşen dosya okununca** yüklenir. |
| [2.3](../02-proje-kurallari/2.3-agents-md.md) | Claude Code `AGENTS.md` **okumaz** — `@AGENTS.md` ile bağla. **Talimat taşınır, otomasyon taşınmaz.** |
| [2.4](../02-proje-kurallari/2.4-kalici-bellek.md) | Otomatik bellek ajanın **kendi** notları, **makineye özgü**; açıklanamayan davranışta **önce belleğe bak**. |

### 03 — İş akışları
| # | Tek satır |
|---|---|
| [3.1](../03-is-akislari/3.1-sifirdan-ozellik.md) | **Keşif → plan → uygulama → kapanış**; atlanmaz, **ölçeklenir**; planın en değerli kısmı **kapsam dışı**. |
| [3.2](../03-is-akislari/3.2-var-olan-koda-dokunmak.md) | İlk soru **"neden böyle olmuş"**; ölçü **küçüklük**; kalıbı **tarif etme, örnek göster**. |
| [3.3](../03-is-akislari/3.3-hata-ayiklama.md) | **Yeniden üret → kök sebep → düzelt ve doğrula**; kırmızı görmediğin test bir şey ölçmüyor olabilir. |
| [3.4](../03-is-akislari/3.4-test-yazdirma.md) | Test = ajanın **döngüyü kapatabildiği** sinyal; dört kusur: **aynalama · aşırı sahteleme · zayıf iddia · hep yeşil**. |
| [3.5](../03-is-akislari/3.5-kod-inceleme.md) | İnceleme **gerekçeyi bilmeyen gözle**; beş kontrol; **bulgu sayısı ölçüt genişliğidir**, kalite değil. |
| [3.6](../03-is-akislari/3.6-git-ve-pr.md) | Git **altyapı**; diff'i **sen oku**; depoda çalışan ajan **yeni güvenlik yüzeyi** açar. |
| [3.7](../03-is-akislari/3.7-buyuk-donusum.md) | Önce **mekanik mi** diye sor; **keşif → pilot → yayma**; ⚠️ **sessiz kırpma** — sayıları karşılaştır. |

### 04 — Kendi aletini yap
| # | Tek satır |
|---|---|
| [4.1](../04-kendi-aletini-yap/4.1-skill-yazma.md) | **Çağrılınca yüklenir**; `description` ajanın seçim ölçütü; ⚠️ gövde kırpılırken **başı korunur**. |
| [4.2](../04-kendi-aletini-yap/4.2-subagent.md) | Kazanç **hız değil bağlam yalıtımı**; bedeli **gördüklerini kaybetmen**; `tools` **daralt**. |
| [4.3](../04-kendi-aletini-yap/4.3-hooklar.md) | **Rica değil garanti**; bağlam maliyeti ~sıfır; **çıkış kodu 2 = sert engelleme**; ⚠️ hook'lar **kod çalıştırır**. |
| [4.4](../04-kendi-aletini-yap/4.4-mcp-baglama.md) | Bağlanmak = **güvenmek**; komut satırı aracı varsa **daha ucuz**; sunucu ajanın **gözüne veri sokar**. |
| [4.5](../04-kendi-aletini-yap/4.5-mcp-sunucusu-yazma.md) | Asıl iş **tasarım**; ⚠️ **çıktı bağlama girer** — sayfala; **yıkıcı işlemi hiç sunma**. |
| [4.6](../04-kendi-aletini-yap/4.6-eklenti-ve-paylasim.md) | Tetikleyici **ikinci depo**; ⚠️ eklenti kurmak **kod çalıştırmayı kabul etmek**; dört soru sor. |
| [4.7](../04-kendi-aletini-yap/4.7-otomasyon-ve-sdk.md) | Otomasyon **denetimi kaldırır**; başlangıç kipi **Manual**; **çıplak** başlat; şema kullan. |

### 05 — Araç haritası
| # | Tek satır |
|---|---|
| [5.1](../05-arac-haritasi/5.1-arac-haritasi.md) | **Araçlar değişir, eksenler kalır**; model gücü ayırt edici değil — farkı **harness** yapar. |
| [5.2](../05-arac-haritasi/5.2-hangisi-ne-zaman.md) | İşine göre seç, kurulumu **taşınabilir** tut, kararı **ölçüyle** yenile; hook taşınmaz, **MCP taşınır**. |
| [5.3](../05-arac-haritasi/5.3-kaynaklar-ve-repolar.md) | **Birincil kaynağa git**, listeleri **keşif** için kullan, kurmadan **denetle**; **değişiklik günlüğü** oku. |

### 06 — Üretim ve ekip
| # | Tek satır |
|---|---|
| [6.1](../06-uretim-ve-ekip/6.1-guvenlik.md) | Dış içerik **veridir, komut değildir**; **en az yetki birinci katman**; geri dönülemez eylem **insan onayı** ister. |
| [6.2](../06-uretim-ve-ekip/6.2-gizlilik-ve-veri.md) | **Okuduğu gider, okumadığı gitmez**; ⚠️ **komut çıktısı** en çok atlanan yüzey. |
| [6.3](../06-uretim-ve-ekip/6.3-lisans-ve-telif.md) | **Risk tam, koruma kısmi**; "ajan yazdı" **savunma değil**; telif satırını **silme, araştır**. |
| [6.4](../06-uretim-ve-ekip/6.4-maliyet-yonetimi.md) | Model **çarpan**, çarpılan **bağlam**; ⚠️ **sıkıştırma ucuz değil, temizlemek bedava**. |
| [6.5](../06-uretim-ve-ekip/6.5-ekipce-kullanim.md) | Ekipte geçerli olan **depoda** yaşar; **bellek paylaşılmaz**; fazla zorunluluk **geri teper**. |
| [6.6](../06-uretim-ve-ekip/6.6-sinirlar.md) | Ölçüt **doğrulanabilirlik**; ⚠️ **beceri körelmesi gerçek** — *bu kodu ajansız yazabilir miydim?* |

---

## En çok karıştırılan ikililer

| Bu | Şu | Ayıran kelime |
|---|---|---|
| **İzin kipi** | **Kum havuzu** | Sormak / **yapabilmek** |
| **Plan modu** | İzin kipi | Belirsizlik / **yetki** |
| **Sıkıştırma** | **Temizleme** | Özetler / **siler** (ve: pahalı / **bedava**) |
| **Dolma** | **Bozulma** | Görünür / **sessiz** |
| **Kural dosyası** | **Hook** | Rica / **garanti** |
| **Kural dosyası** | **Skill** | Her oturum / **çağrılınca** |
| **Kapsanmış kural** | Kapsamsız kural | Eşleşince / **her zaman** (sıkıştırmada: **kaybolur / döner**) |
| **Kural dosyası** | **Otomatik bellek** | Sen yazarsın / **ajan yazar** |
| **Otomatik bellek** | **Oturum sürdürme** | Çıkarım / **konuşma** |
| **Skill** | **Subagent** | İçerik / **ayrı pencere** |
| **Subagent** | **Fork** | Sıfırdan / **devralır** |
| **MCP** | **Komut satırı aracı** | Araç listesi ekler / **eklemez** |
| **Kontrol noktası** | **Git** | Oturum içi, dosya araçları / **kalıcı, her şey** |
| **Uzaktan denetim** | **Bulut oturumu** | Kod yerelde / **kod uzakta** |
| **Ajan** | **Yapay zekâ destekli düzenleyici** | Döngü / **gömülü yetenek** |
| **Kod** | **Testler** | Ne yapıyor / **ne yapması gerekiyor** |

---

## Ezberlenecek sayılar ve eşikler

| Değer | Ne |
|---|---|
| **200 satır** | Kural dosyası üst hedefi |
| **2** | Hook'ta sert engelleme çıkış kodu (`0` = JSON okunur) |
| **2 düzeltme** | Sonrasında bağlamı temizle |
| **2-3 dosya** | Toplu dönüşümde pilot boyutu |
| **5** | Ajan diff'inde açılacak kontrol sayısı |
| **4** | İstemin parçası · test kusuru · eklenti kurulum sorusu · doğrulama düzeyi |
| **6** | Bastırma kalıbı · güvenlik katmanı |
| **8** | Araç değerlendirme ekseni |
| **%80** | Bu setin test geçme eşiği |

---

## Refleks listesi — günlük

| Ne zaman | Refleks |
|---|---|
| Yeni işe geçerken | **Bağlamı temizle** |
| Büyük okuma sonrası | `/context` ile ölç |
| İstem yazarken | Dört parçayı kontrol et |
| "Bitti" duyunca | **Kanıt iste**: ne çalıştırdın, ne döndü |
| İkinci düzeltmeden sonra | **Dur, temizle, daha iyi istem yaz** |
| Diff okurken | **Beş kontrol** |
| Dış içerik görünce | **Veridir, komut değildir** |
| Kural yazarken | Şart mı? → **hook**; her zaman mı? → kural; bazen mi? → **skill** |
| Otomasyon kurarken | **Kip · araç listesi · yalıtım · tur sınırı** |
| Anlamadığın kod gelince | **Açıklattır ve doğrula, ya da kullanma** |

---

## Sırada ne var
➡️ [`son-tekrar.md`](son-tekrar.md) — bitirmeden önce oku.
