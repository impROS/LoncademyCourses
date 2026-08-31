# Cheatsheet — her konunun tek satırı

> Tek sayfalık hatırlatma. Bir konuyu unuttuysan satırı oku, yetmezse
> bağlantısına git. Terim tanımadıysan
> [kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md).

---

## Konular tek satırda

### 01 — Ajanı programlamak
| # | Tek satır |
|---|---|
| [1.1](../01-ajani-programlamak/1.1-skill-yazma.md) | **Çağrılınca yüklenir**; `description` ajanın seçim ölçütü; ⚠️ gövde kırpılırken **başı korunur**. |
| [1.2](../01-ajani-programlamak/1.2-subagent.md) | Kazanç **hız değil bağlam yalıtımı**; bedeli **gördüklerini kaybetmen**; `tools` **daralt**. |
| [1.3](../01-ajani-programlamak/1.3-hooklar.md) | **Rica değil garanti**; bağlam maliyeti ~sıfır; **çıkış kodu 2 = sert engelleme**; ⚠️ hook'lar **kod çalıştırır**. |

### 02 — Dış dünyaya bağlamak
| # | Tek satır |
|---|---|
| [2.1](../02-dis-dunyaya-baglamak/2.1-mcp-baglama.md) | Bağlanmak = **güvenmek**; komut satırı aracı varsa **daha ucuz**; sunucu ajanın **gözüne veri sokar**. |
| [2.2](../02-dis-dunyaya-baglamak/2.2-mcp-sunucusu-yazma.md) | Asıl iş **tasarım**; ⚠️ **çıktı bağlama girer** — sayfala; **yıkıcı işlemi hiç sunma**. |
| [2.3](../02-dis-dunyaya-baglamak/2.3-eklenti-ve-paylasim.md) | Tetikleyici **ikinci depo**; ⚠️ eklenti kurmak **kod çalıştırmayı kabul etmek**; dört soru sor. |
| [2.4](../02-dis-dunyaya-baglamak/2.4-otomasyon-ve-sdk.md) | Otomasyon **denetimi kaldırır**; başlangıç kipi **Manual**; **çıplak** başlat; şema kullan. |

---

## En çok karıştırılan ikililer

| Bu | Şu | Ayıran kelime |
|---|---|---|
| **Kural dosyası** | **Hook** | Rica / **garanti** |
| **Kural dosyası** | **Skill** | Her oturum / **çağrılınca** |
| **Skill** | **Subagent** | İçerik / **ayrı pencere** |
| **Subagent** | **Fork** | Sıfırdan / **devralır** |
| **MCP** | **Komut satırı aracı** | Araç listesi ekler / **eklemez** |
| **Eklenti** | **Tek skill** | İkinci depo / **tek dosya** |

---

## Ezberlenecek sayılar ve eşikler

| Değer | Ne |
|---|---|
| **2** | Hook'ta sert engelleme çıkış kodu (`0` = JSON okunur) |
| **4** | Eklenti kurulumundan önce sorulacak soru |
| **4** | Otomasyonda ayarlanacak şey: **kip · araç listesi · yalıtım · tur sınırı** |
| **%80** | Bu setin test geçme eşiği |

---

## Refleks listesi — günlük

| Ne zaman | Refleks |
|---|---|
| Kural yazarken | Şart mı? → **hook**; her zaman mı? → kural; bazen mi? → **skill** |
| Skill yazarken | `description` **ne zaman çağrılacağını** söylüyor mu? |
| Alt ajana devrederken | `tools` listesini **daralt** |
| Sunucu bağlarken | **Komut satırı aracı yetmiyor mu?** |
| Kendi sunucunu yazarken | **Çıktıyı sayfala**, yıkıcı işlemi **hiç sunma** |
| Eklenti kurarken | **Dört soruyu sor** |
| Otomasyon kurarken | **Kip · araç listesi · yalıtım · tur sınırı** |

---

## Sırada ne var
➡️ [`son-tekrar.md`](son-tekrar.md) — bitirmeden önce oku.
