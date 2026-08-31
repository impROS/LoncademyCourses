# Seçim ve ayar rehberi

> Sözlük *"bu ne demek?"* diye sorar. Bu dosya üç başka soruyu cevaplar:
> **Ne yapar? · Ne zaman dokunulur? · Değeri seçerken neye bakılır?**
>
> Buraya bir ayarı **kullanmadan önce** değil, *"acaba buna mı dokunmalıyım?"*
> dediğinde gelirsin. Bu kursun dokunduğu ayarlar burada; dizinin diğer
> kurslarında başka ayarlar var.

---

## Reçeteler — senaryodan ayara

Ezberlenecek liste yerine beş hazır kurulum. Kendi durumunu en çok hangisi
anlatıyorsa oradan başla.

### A. "Kendi makinemde, kendi projemde, denetleyerek çalışıyorum"

| Ayar | Değer | Neden |
|---|---|---|
| İzin kipi | `default` (Manual) ya da `auto` | Manual her şeyi sorar; `auto` sınıflandırıcıya sorar |
| Kum havuzu | Açık | Sorulan soru sayısını düşürür, sınırı korur |
| Kural dosyası | `CLAUDE.md`, 200 satır altı | Her oturuma giriyor |
| Doğrulama | Test komutu kural dosyasında yazılı | Ajan kendi döngüsünü kapatabilsin |

### B. "Tanımadığım bir kod tabanına dokunuyorum"

| Ayar | Değer | Neden |
|---|---|---|
| Başlangıç | Plan modu | Önce oku ve anlat, sonra değiştir |
| İzin kipi | `default` | Her dosya yazımını gör |
| Ek | Kod zekâsı eklentisi (dilin varsa) | Ajan grep yerine sembole gitsin, daha az dosya okusun |

### C. "Uzun ve tekrarlayan bir iş, başında durmayacağım"

| Ayar | Değer | Neden |
|---|---|---|
| İzin kipi | `auto` | Kesintisiz ilerlesin, riskli olan yine engellensin |
| Durdurma denetimi | `Stop` hook'u ile test zorunlu | "Bitti" demesi yetmesin, testi geçsin |
| İzolasyon | worktree ya da kapsayıcı | Yanlış giderse ana kopya etkilenmesin |

---

## İzin kipleri

Ajanın **sormadan** ne yapabileceğini belirleyen taban ayar. Kum havuzuyla
karıştırma: bu *sorup sormamayı*, kum havuzu *yapabilmeyi* belirler.

| Kip | Sormadan ne yapar | Ne zaman | Varsayılan mı |
|---|---|---|---|
| `default` (**Manual**) | Yalnızca okuma | Hassas iş, tanımadığın kod | Bazı planlarda evet |
| `acceptEdits` | Okuma + dosya yazma + `mkdir`/`mv`/`cp` gibi komutlar | Kodu zaten inceliyorken hızlı yineleme | — |
| `plan` | Okuma; değişikliği plan onaylanana kadar engeller | Keşif aşaması | — |
| `auto` | Neredeyse her şey; ayrı bir sınıflandırıcı model riskli olanı engeller | Uzun işler, soru yorgunluğu | Ücretli planlarda evet |
| `dontAsk` | Yalnızca önceden izin verilenler | Kilitli otomasyon, CI | — |
| `bypassPermissions` | Her şey | **Yalnızca yalıtılmış kapsayıcı/sanal makine** | — |

Oturum içinde `Shift+Tab` ile geçilir; başlangıç kipi `--permission-mode` bayrağı
ya da ayar dosyasındaki `permissions.defaultMode` ile verilir.

> ⚠️ **Hiçbir kip her şeyi otomatik onaylamaz.** Açıkça "sor" kuralı konmuş
> araçlar, kullanıcı etkileşimi gerektiren araçlar ve kritik yollardaki silme
> işlemleri `bypassPermissions` dâhil her kipte sorar. Buna güven, ama tek
> savunman olmasın.

---

## Bağlam ayarları

| Araç | Ne yapar | Ne zaman |
|---|---|---|
| Bağlamı temizleme | Konuşmayı tamamen sıfırlar | **İlgisiz işler arasında, her seferinde** |
| Yönlendirmeli sıkıştırma | Neyin korunacağını sen söylersin | Uzun bir işin ortasında yer bitince |
| Geri sarma | Konuşmayı ve dosyaları önceki noktaya alır | Riskli bir denemeden sonra |
| Yan soru | Cevabı bağlama girmez | "Şunu bir sorayım" anları |
| Subagent'a devretme | Okuma başka pencerede kalır | Keşif, günlük analizi, araştırma |

Ölçmek için: bağlam kullanımını gösteren durum satırını aç ve `/context` komutunu
alışkanlık hâline getir. Ayrıntısı **101 · *Bağlam penceresi***.

---

## ⚠️ Tehlikeli ayarlar

Bunlar yanlış kullanıldığında **geri alınamaz** ya da güvenliği kaldırır.

| Ayar | Tehlikesi | Yalnızca şu durumda |
|---|---|---|
| `bypassPermissions` / izinleri atlama | Ajan her şeyi sormadan yapar; kötü niyetli içerik gördüyse onu da yapar | Yalıtılmış kapsayıcı ya da sanal makine, kök olmayan kullanıcı |
| Geniş izin kuralları (`Bash(*)`) | "Bir kez izin ver" alışkanlığı tüm kabuk erişimine dönüşür | Hiçbir zaman; komutu adıyla izinle |
| Doğrulanmamış MCP sunucusu | Sunucu ajanın gözüne veri sokar; istem enjeksiyonunun ana kapısı | Kaynağına güveniyorsan ve kapsamı darsa |
| Kural dosyasına gizli anahtar yazmak | Depoya işlenir, herkese gider | Asla — ortam değişkeni kullan |
| Ajan belirtecine geniş yetki (yazma, tüm depolar) | Enjeksiyon başarılı olursa zarar yüzeyi bu yetki kadardır | En az yetki, salt-okunur mümkünse |
| Otomatik onaylı hat + dış girdi | Sorun kaydına yazılan metin ajanı yönetebilir | Girdiyi güvenilmez say, yetkiyi daralt |

---

## Varsayılanı bilmiyorsan sisteme sor

Bu rehberdeki değerler örnektir; **doğrusu makinendekidir.** Ezberleme, sor:

```bash
claude --help                 # bayrakların tam listesi
```

Oturum içinde:

```text
/doctor        # kurulum sağlığı ve öneriler
/context       # bağlamı ne dolduruyor
/permissions   # yürürlükteki izin kuralları
/hooks         # tanımlı hook'lar
/mcp           # bağlı MCP sunucuları ve durumları
/usage         # kullanım ve maliyet
/config        # ayarlar
```

> ⚠️ **Bu dosyadaki ayar adları değişebilir.** Bir ayar adı çalışmıyorsa
> uydurma — `claude --help` ve resmî ayar başvurusuna bak:
> <https://code.claude.com/docs/en/settings-reference>
