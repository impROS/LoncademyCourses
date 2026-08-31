# 00.1 — Bu kursa başlarken

Bu kurs, ajanla **günlük yazılım işini** yürütmeyi öğretir: sıfırdan özellik
çıkarmak, tanımadığın koda dokunmak, hata ayıklamak, test yazdırmak, diff
okumak, commit ve pull request üretmek, büyük dönüşümleri güvenle yapmak.

Yedi konunun tamamı tek bir varsayıma dayanıyor: **ajanın nasıl çalıştığını
zaten biliyorsun.** Bu dosya o varsayımı denetler ve eksik kalan yeri kapatır.

---

## Kimin için

| Sen | Bu kurs |
|---|---|
| Bir ajanı kurdun, birkaç iş yaptırdın, ama iş büyüyünce dağılıyor | ✅ tam yerinde |
| Ajanın ürettiği kodu okuyabiliyorsun ama neyi kontrol edeceğini bilmiyorsun | ✅ tam yerinde |
| Ajanı hiç kullanmadın, terimleri tanımıyorsun | ⛔ önce **101 — Temeller** |
| Kendi skill'ini, hook'unu yazmak istiyorsun | ➡️ bu kurstan sonra **301** |

---

## Ön koşul: **101 — Temeller**

Bu kurs, dizinin ilk kursunun bittiğini varsayar. Aşağıdaki altı şeyi
okuduğunda *"evet, bunu biliyorum"* diyemiyorsan önce oraya dön — bu kurs
onların üstüne kuruluyor, tekrarlamıyor.

#### Ajan döngüsü

Ajan bir **araç çağıran döngüdür**: planlar, araç çağırır, sonucu okur, tekrar
değerlendirir. Kritik ayrıntı: **"bitti" kararını modelin kendisi verir.** Bu
yüzden bu kursun her konusunda aynı cümle geçecek — *iddia değil kanıt iste.*

#### Bağlam penceresi

Modelin tek istekte görebildiği metnin tamamı: talimat, konuşma geçmişi, okunan
dosyalar, komut çıktıları. **Birikir, akmaz.** İki belirti birbirinden farklı:
**dolma görünür** (uyarı çıkar), **bozulma sessizdir** (cevaplar kötüleşir ama
kimse söylemez). Refleks: **ilgisiz işler arasında bağlamı temizle.**

#### İstemin dört parçası

**Hedef · kaynak · kısıt · kabul ölçütü.** Eksik bırakılan her parça, sonradan
yapacağın düzeltme sayısına dönüşür. Bu kursta "iyi istem" derken kastedilen bu.

#### İzin kipi ve kum havuzu

İzin kipi ajanın **sorup sormayacağını**, kum havuzu **yapabileceğini** belirler.
İkisi ayrı şeydir. Bu kursta uzun işlerde `auto`, tanımadığın kodda `default`
kullanacaksın.

#### Doğrulama refleksi

**Doğrulayamadığın şeyi teslim etme.** Ajana kapatabileceği bir döngü ver: test,
derleme, çalıştırıp deneme. Bu kursun tamamı bunun uygulamasıdır.

#### Kural dosyası bir ricadır

`CLAUDE.md` her oturuma girer ama **garanti değildir**; ajan onu göz ardı
edebilir. Tutması şart olan şeyler için başka bir katman gerekiyor — o katman
**301**'in konusu.

---

## Ortam

Bu kursun pratikleri gerçek komutlar çalıştırır. Elinde şunlar olmalı:

| Ne | Neden | Denetleme |
|---|---|---|
| Çalışan bir ajan (Claude Code önerilir) | Bütün pratikler onun üstünden | `claude --version` |
| `git` | 2.3 konusu tamamen git üzerine | `git --version` |
| `gh` (GitHub komut satırı aracı) | 2.3'teki pull request pratiği | `gh --version` |
| Bir programlama dili ve test koşucusu | 2.1 test yazdırma pratiği | dile göre değişir |
| Küçük ve **atılabilir** bir örnek proje | Deneme yaparken bozulması sorun olmasın | — |

> ⚠️ **Kurulum komutunu buradan kopyalama.** Ajanların kurulum yolu sürümle
> değişiyor; her zaman aracın **resmî belgesinden** al. 101'in kurulum konusu
> bunu adım adım anlatıyor.

Örnek proje elinde yoksa: küçük bir görev listesi servisi (üç uç nokta, bir
veri katmanı, birkaç test) bu kursun tamamı için yeterli. Kasten küçük tutuluyor
— gözlenecek şey projenin karmaşıklığı değil, **ajanın davranışı.**

---

## Bu kursta ne yapacaksın

| Bölüm | Ne | Çıktı |
|---|---|---|
| **01 — Kod üretmek ve değiştirmek** | Sıfırdan özellik · var olan koda dokunmak · hata ayıklama | Dört aşamalı akışı kendi projende yürütmüş olacaksın |
| **02 — Kaliteyi güvenceye almak** | Test yazdırmak · kod inceleme · git ve pull request · büyük dönüşümler | Diff okuma kontrol listen ve pilot yayma alışkanlığın |

Sonunda **29 soruluk genel deneme** ve icazet var.

---

## Nasıl çalışılır

1. **Sırayla git.** 02'nin konuları 01'dekileri varsayıyor.
2. **Önce oku, sonra pratiği yap, en son teste gir.**
3. **Pratikleri gerçekten yap.** Ajanla çalışmak okuyarak değil deneyerek oturuyor.
4. **Kendini kontrol sorularının cevabını önce kâğıda yaz**, sonra bölümün
   `cevaplar.md` dosyasını aç.
5. **%80 altında kaldıysan** testin sonundaki zayıf alt konulara dön.
6. **Terim tanımadıysan** [kavram sözlüğüne](02-kavram-sozlugu.md) bak; ayar
   seçerken [seçim ve ayar rehberine](03-ayar-rehberi.md).
7. **Aklına takılanı** [`soru-cevap.md`](../soru-cevap.md)'ye yaz.

---

## Sırada ne var
➡️ [`../01-kod-yazdirmak/1.1-sifirdan-ozellik.md`](../01-kod-yazdirmak/1.1-sifirdan-ozellik.md) — keşif, plan, uygulama, kapanış.
