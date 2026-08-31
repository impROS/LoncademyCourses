# Cheatsheet — her konunun tek satırı

> Tek sayfalık hatırlatma. Bir konuyu unuttuysan satırı oku, yetmezse
> bağlantısına git. Terim tanımadıysan
> [kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md).

---

## Konular tek satırda

### 01 — Kod üretmek ve değiştirmek
| # | Tek satır |
|---|---|
| [1.1](../01-kod-yazdirmak/1.1-sifirdan-ozellik.md) | **Keşif → plan → uygulama → kapanış**; atlanmaz, **ölçeklenir**; planın en değerli kısmı **kapsam dışı**. |
| [1.2](../01-kod-yazdirmak/1.2-var-olan-koda-dokunmak.md) | İlk soru **"neden böyle olmuş"**; ölçü **küçüklük**; kalıbı **tarif etme, örnek göster**. |
| [1.3](../01-kod-yazdirmak/1.3-hata-ayiklama.md) | **Yeniden üret → kök sebep → düzelt ve doğrula**; kırmızı görmediğin test bir şey ölçmüyor olabilir. |

### 02 — Kaliteyi güvenceye almak
| # | Tek satır |
|---|---|
| [2.1](../02-kaliteyi-guvenceye-almak/2.1-test-yazdirma.md) | Test = ajanın **döngüyü kapatabildiği** sinyal; dört kusur: **aynalama · aşırı sahteleme · zayıf iddia · hep yeşil**. |
| [2.2](../02-kaliteyi-guvenceye-almak/2.2-kod-inceleme.md) | İnceleme **gerekçeyi bilmeyen gözle**; beş kontrol; **bulgu sayısı ölçüt genişliğidir**, kalite değil. |
| [2.3](../02-kaliteyi-guvenceye-almak/2.3-git-ve-pr.md) | Git **altyapı**; diff'i **sen oku**; depoda çalışan ajan **yeni güvenlik yüzeyi** açar. |
| [2.4](../02-kaliteyi-guvenceye-almak/2.4-buyuk-donusum.md) | Önce **mekanik mi** diye sor; **keşif → pilot → yayma**; ⚠️ **sessiz kırpma** — sayıları karşılaştır. |

---

## En çok karıştırılan ikililer

| Bu | Şu | Ayıran kelime |
|---|---|---|
| **Kod** | **Testler** | Ne yapıyor / **ne yapması gerekiyor** |
| Testi **çalıştırmak** | Testi **yazmak** | İddia / **kapanan döngü** |
| **Denetimi geçmek** | **Çözmek** | Susturulmuş / **düzeltilmiş** |
| **Bulgu sayısı** | **Kalite** | Ölçüt genişliği / **kod iyiliği** |
| **Kontrol noktası** | **Git** | Oturum içi, dosya araçları / **kalıcı, her şey** |
| **Mekanik dönüşüm** | **Kararlı dönüşüm** | Betik yeter / **yargı gerekir** |

---

## Ezberlenecek sayılar ve eşikler

| Değer | Ne |
|---|---|
| **5** | Ajan diff'inde açılacak kontrol sayısı |
| **6** | Hata bastırma kalıbı |
| **4** | Test kusuru (aynalama · aşırı sahteleme · zayıf iddia · hep yeşil) |
| **2-3 dosya** | Toplu dönüşümde pilot boyutu |
| **%80** | Bu setin test geçme eşiği |

---

## Refleks listesi — günlük

| Ne zaman | Refleks |
|---|---|
| İşe başlarken | **Önce keşif, sonra plan, sonra kod** |
| Var olan koda dokunurken | **"Neden böyle olmuş?"** diye sor |
| Hata gelince | **Önce yeniden üret** |
| Test yazdırırken | **Kırmızıyı gör**, sonra yeşile geç |
| "Bitti" duyunca | **Kanıt iste**: ne çalıştırdın, ne döndü |
| Diff okurken | **Beş kontrol** |
| Toplu değişiklikte | **Sayıları karşılaştır** — sessiz kırpma |

---

## Sırada ne var
➡️ [`son-tekrar.md`](son-tekrar.md) — bitirmeden önce oku.
