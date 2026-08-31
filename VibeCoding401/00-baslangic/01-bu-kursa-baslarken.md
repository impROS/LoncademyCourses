# 00.1 — Bu kursa başlarken

Bu kurs, ajanı **başkalarının da etkilendiği yerde** kullanmayı öğretir: hangi
aracın ne yaptığını ölçüyle karşılaştırmak, istem enjeksiyonuna ve tedarik
zinciri saldırısına karşı katman kurmak, hangi verinin nereye gittiğini bilmek,
üretilen kodun lisans ve telif tarafını yönetmek, maliyeti ölçmek, ekipçe ortak
bir kurulum kurmak ve **nerede durulacağına** karar vermek.

Dizinin son kursu. Diğer üçü *"nasıl yapılır"* sorusunu cevapladı; bu kurs
*"yapmalı mıyım, hangi koşulla?"* sorusunu cevaplıyor.

---

## Kimin için

| Sen | Bu kurs |
|---|---|
| Ajanı kendi işinde kullanıyorsun, şimdi ekip ya da üretim söz konusu | ✅ tam yerinde |
| Güvenlik, gizlilik ya da lisans tarafında sorulacak soruları bilmiyorsun | ✅ tam yerinde |
| Hangi aracı seçeceğine karar vermen gerekiyor | ✅ tam yerinde |
| Ajanla günlük iş yapamıyorsun | ⛔ önce **101** ve **201** |
| Skill, hook, MCP kelimelerini tanımıyorsun | ⛔ önce **301** |

---

## Ön koşul: **101**, **201** ve **301**

Aşağıdaki beş şeye *"evet, biliyorum"* diyemiyorsan önce geri dön.

#### Ajan bir araç çağıran döngüdür

Planlar, araç çağırır, sonucu okur, tekrar değerlendirir; **"bitti" kararını
model verir.** Bu kursun güvenlik bölümü tamamen bu gerçeğin sonucu: döngünün
içine giren her metin ajanın davranışını etkileyebilir.

#### Bağlam birikir

Okunan her dosya, çalıştırılan her komutun çıktısı bağlamda **kalır**. Gizlilik
bölümünün tek cümlesi bu: **okuduğu gider, okumadığı gitmez.** Maliyet
bölümünün tek cümlesi de bunun ikizi: **model çarpandır, çarpılan bağlamdır.**

#### Kural rica, hook garantidir

Kural dosyası ajanın göz ardı edebileceği bir talimattır; hook bir olayda
çalışan koddur ve **engelleyebilir.** Ekip politikası kurarken hangisinin
nereye yazılacağı bu ayrıma dayanıyor.

#### İzin kipi, kum havuzu ve en az yetki

İzin kipi **sorup sormamayı**, kum havuzu **yapabilmeyi** belirler; en az yetki
ise ikisinin de arkasındaki ilkedir. Güvenlik katmanlarının birincisi budur.

#### Diff'i sen okursun

**İnceleme gerekçeyi bilmeyen gözle yapılır**; beş kontrol her seferinde
uygulanır. Ekipçe kullanım bölümündeki inceleme politikası bunun kurumsallaşmış
hâli.

---

## Ortam

Bu kursun pratiklerinin çoğu **kod yazmaz, karar verdirir**: tablo doldurmak,
politika yazmak, ölçüm almak. Yine de elinde şunlar olmalı:

| Ne | Neden | Denetleme |
|---|---|---|
| Çalışan bir ajan (Claude Code önerilir) | Ölçüm ve ayar pratikleri | `claude --version` |
| `git` | Ekip kurulumu depo üzerinden paylaşılıyor | `git --version` |
| Bir ekip deposu ya da benzeri | Ekipçe kullanım pratiği | — |

> ⚠️ **Bu kurstaki fiyatlar, limitler ve ürün özellikleri örnektir.** Araçların
> fiyatlandırması ve veri politikaları aylık değişiyor. `⚠️ Doğrulanmalı`
> gördüğün her yerde **sağlayıcının kendi sayfasına** bak.

> ⚠️ **Bu kurs hukuki görüş değildir.** Lisans ve telif bölümü, sorulması
> gereken soruları ve bilinen riskleri anlatır; bir karar vermen gerekiyorsa
> **avukata sor**.

---

## Bu kursta ne yapacaksın

| Bölüm | Ne | Çıktı |
|---|---|---|
| **01 — Araç haritası** | Alan haritası · hangisi ne zaman · kaynaklar ve repolar | Kendi doldurduğun eksen tablosu ve karar dosyası |
| **02 — Üretim ve ekip** | Güvenlik · gizlilik · lisans · maliyet · ekip · sınırlar | Ekip politikan, ölçüm dosyan ve durma ölçütün |

Sonunda **31 soruluk genel deneme** ve icazet var.

---

## Nasıl çalışılır

1. **Sırayla git.** 02'nin konuları 01'deki eksenleri varsayıyor.
2. **Önce oku, sonra pratiği yap, en son teste gir.**
3. **Tabloları gerçekten doldur.** Bu kursun pratikleri okunacak metin değil,
   sonradan kullanacağın **karar belgeleri**.
4. **Kendini kontrol sorularının cevabını önce kâğıda yaz.**
5. **%80 altında kaldıysan** testin sonundaki zayıf alt konulara dön.
6. **Terim tanımadıysan** [kavram sözlüğüne](02-kavram-sozlugu.md) bak; ayar
   seçerken [seçim ve ayar rehberine](03-ayar-rehberi.md).
7. **Aklına takılanı** [`soru-cevap.md`](../soru-cevap.md)'ye yaz.

---

## Sırada ne var
➡️ [`../01-arac-haritasi/1.1-arac-haritasi.md`](../01-arac-haritasi/1.1-arac-haritasi.md) — hangi araç ne yapıyor.
