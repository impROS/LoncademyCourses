# 00.1 — Bu kursa başlarken

Bu kurs, ajanı **kendi işine göre programlamayı** öğretir: tekrarlayan işi
skill'e almak, ayrı bağlamda çalışan yardımcılar kurmak, tutması şart olanı
hook'la garantiye bağlamak, dış sistemleri MCP ile bağlamak, kendi sunucunu
yazmak, kurulumunu paketleyip taşımak ve ajanı insansız çalıştırmak.

Buradan sonrası **araç yapımıdır**. Araç yapmak, ajanın yetkisini genişletmek
demek — o yüzden her konuda güvenlik tarafı da var.

---

## Kimin için

| Sen | Bu kurs |
|---|---|
| Aynı talimatı üçüncü kez yazarken *"bunu bir yere kaydetsem"* diyorsun | ✅ tam yerinde |
| Bir kuralın **tutmadığını** gördün ve garanti istiyorsun | ✅ tam yerinde |
| Ajanı günlük işte akıcı kullanamıyorsun | ⛔ önce **201 — Günlük iş akışları** |
| Terimleri tanımıyorsun | ⛔ önce **101 — Temeller** |

---

## Ön koşul: **101** ve **201**

Bu kurs dizinin üçüncü kursu. Aşağıdaki beş şeye *"evet, biliyorum"*
diyemiyorsan önce geri dön; bu kurs bunların üstüne kuruluyor.

#### Bağlam en kıt kaynağın

Bağlam **birikir, akmaz**; **dolma görünür, bozulma sessizdir.** Bu kursun en
önemli kararlarının çoğu — alt ajan neden var, MCP sunucusu neden pahalı,
sunucu çıktısı neden sayfalanır — doğrudan bu tek gerçeğin sonucu.

#### Kural dosyası bir ricadır

`CLAUDE.md` ve yola kapsanmış kural dosyaları her oturuma girer ama ajan onları
**göz ardı edebilir.** Bu kursun 1.3 konusu tam olarak bu boşluğu dolduruyor:
hook bir rica değil, **garantidir**.

#### İzin kipi ve kum havuzu ayrı şeylerdir

İzin kipi **sorup sormamayı**, kum havuzu **yapabilmeyi** belirler. Otomasyon
kurarken ikisini birlikte ayarlayacaksın; birini ayarlayıp diğerini unutmak
bu kursun en pahalı hatası.

#### Doğrulama refleksi

**Doğrulayamadığın şeyi teslim etme.** Yazdığın her alet bu refleksi ya
güçlendirir (durdurma hook'u) ya da zayıflatır (denetimi kaldıran otomasyon).
Hangisini yaptığını bilerek yaz.

#### İş akışının sırası

**Keşif → plan → uygulama → kapanış.** Alet yazarken de aynı sıra geçerli:
önce hangi işin tekrar ettiğini ölç, sonra hangi katmana yazacağına karar ver.

---

## Ortam

| Ne | Neden | Denetleme |
|---|---|---|
| Çalışan bir ajan (Claude Code önerilir) | Bütün pratikler onun üstünden | `claude --version` |
| `git` | Eklenti ve paylaşım pratiği depo üzerinden | `git --version` |
| `jq` | Hook ve betik çıktısı ayrıştırmak için | `jq --version` |
| Node.js ya da Python | Kendi MCP sunucunu yazarken | `node -v` / `python3 -V` |
| Küçük ve **atılabilir** bir örnek proje | Hook denerken bozulması sorun olmasın | — |

> ⚠️ **Bu kursun en hızlı eskiyen kursu bu.** Alet arayüzleri — hook olay
> adları, ayar anahtarları, komut bayrakları — sürüm sürüm değişiyor.
> `⚠️ Doğrulanmalı` gördüğün her yerde resmî belgeye bak; ayar adı çalışmıyorsa
> **uydurma**, `claude --help` ve resmî ayar başvurusuna bak.

> ⚠️ **Bu kursta çalıştıracağın şeylerin çoğu koddur.** Hook bir kabuk komutu
> çalıştırır; eklenti kurmak, birinin yazdığı kodun makinende çalışmasını kabul
> etmektir. Pratikleri **atılabilir bir projede** yap.

---

## Bu kursta ne yapacaksın

| Bölüm | Ne | Çıktı |
|---|---|---|
| **01 — Ajanı programlamak** | Skill · alt ajan · hook | Çalışan bir skill ve engelleyen bir hook |
| **02 — Dış dünyaya bağlamak** | MCP bağlama · kendi sunucun · eklenti ve paylaşım · otomasyon | Kendi yazdığın bir sunucu ve taşınabilir bir kurulum |

Sonunda **24 soruluk genel deneme** ve icazet var.

---

## Nasıl çalışılır

1. **Sırayla git.** 02'nin konuları 01'dekileri varsayıyor.
2. **Önce oku, sonra pratiği yap, en son teste gir.**
3. **Yazdığın her aleti gerçekten çalıştır.** Çalışmayan hook, yazılmamış
   hook'tan daha tehlikelidir — koruduğunu sanırsın.
4. **Kendini kontrol sorularının cevabını önce kâğıda yaz.**
5. **%80 altında kaldıysan** testin sonundaki zayıf alt konulara dön.
6. **Terim tanımadıysan** [kavram sözlüğüne](02-kavram-sozlugu.md) bak; ayar
   seçerken [seçim ve ayar rehberine](03-ayar-rehberi.md).
7. **Aklına takılanı** [`soru-cevap.md`](../soru-cevap.md)'ye yaz.

---

## Sırada ne var
➡️ [`../01-ajani-programlamak/1.1-skill-yazma.md`](../01-ajani-programlamak/1.1-skill-yazma.md) — tekrarlayan işi dosyaya almak.
