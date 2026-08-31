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

### 01 — Ajanla çalışmanın temelleri
| # | Tek satır |
|---|---|
| [1.1](../01-ajanla-calismak/1.1-ajan-dongusu.md) | Ajan = **araç çağıran döngü**; araç sonucu bağlamda **kalır**; "bitti" kararını **model** verir. |
| [1.2](../01-ajanla-calismak/1.2-istem-yazma.md) | İstemin dört parçası: **hedef · kaynak · kısıt · kabul ölçütü**. Eksik parça = düzeltme sayısı. |
| [1.3](../01-ajanla-calismak/1.3-baglam-yonetimi.md) | Bağlam **birikir, akmaz**; **dolma görünür, bozulma sessiz**. İlgisiz işler arasında **temizle**. |
| [1.4](../01-ajanla-calismak/1.4-izinler-ve-plan-modu.md) | İzin kipi **sorup sormamayı**, kum havuzu **yapabilmeyi** belirler. Plan modu belirsizlik aracı, güvenlik aracı **değil**. |
| [1.5](../01-ajanla-calismak/1.5-dogrulama-refleksi.md) | **Doğrulayamadığın şeyi teslim etme.** Dört düzey: istem içi · hedef · durdurma hook'u · ikinci göz. |

### 02 — Projeye kural yazmak
| # | Tek satır |
|---|---|
| [2.1](../02-projeye-kural-yazmak/2.1-claude-md.md) | Her isteğe binen **sabit vergi**; ölçüt *"silsem hata yapar mı?"*; **şişme paradoksu**: uzun dosya = kaybolan kural. |
| [2.2](../02-projeye-kural-yazmak/2.2-kural-dosyalari.md) | Tasarrufu **bölme değil kapsamlama** sağlar; kapsanmış kural **eşleşen dosya okununca** yüklenir. |
| [2.3](../02-projeye-kural-yazmak/2.3-agents-md.md) | Claude Code `AGENTS.md` **okumaz** — `@AGENTS.md` ile bağla. **Talimat taşınır, otomasyon taşınmaz.** |
| [2.4](../02-projeye-kural-yazmak/2.4-kalici-bellek.md) | Otomatik bellek ajanın **kendi** notları, **makineye özgü**; açıklanamayan davranışta **önce belleğe bak**. |

---

## En çok karıştırılan ikililer

| Bu | Şu | Ayıran kelime |
|---|---|---|
| **Ajan** | **Yapay zekâ destekli düzenleyici** | Döngü / **gömülü yetenek** |
| **İzin kipi** | **Kum havuzu** | Sormak / **yapabilmek** |
| **Plan modu** | İzin kipi | Belirsizlik / **yetki** |
| **Sıkıştırma** | **Temizleme** | Özetler / **siler** (ve: pahalı / **bedava**) |
| **Dolma** | **Bozulma** | Görünür / **sessiz** |
| **Kapsanmış kural** | Kapsamsız kural | Eşleşince / **her zaman** (sıkıştırmada: **kaybolur / döner**) |
| **Kural dosyası** | **Otomatik bellek** | Sen yazarsın / **ajan yazar** |
| **Otomatik bellek** | **Oturum sürdürme** | Çıkarım / **konuşma** |

---

## Ezberlenecek sayılar ve eşikler

| Değer | Ne |
|---|---|
| **200 satır** | Kural dosyası üst hedefi |
| **2 düzeltme** | Sonrasında bağlamı temizle |
| **4** | İstemin parçası (hedef · kaynak · kısıt · kabul ölçütü) |
| **4** | Doğrulama düzeyi (istem içi · hedef · durdurma hook'u · ikinci göz) |
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
| Kural yazarken | Silsem hata yapar mı? Hayırsa **yazma** |
| Açıklanamayan davranışta | **Önce otomatik belleğe bak** |

---

## Sırada ne var
➡️ [`son-tekrar.md`](son-tekrar.md) — bitirmeden önce oku.
