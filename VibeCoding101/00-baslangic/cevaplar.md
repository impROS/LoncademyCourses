# 00 — Kendini kontrol cevapları

> Önce kâğıda kendi cevabını yaz, sonra buraya bak. Göz kayarsa düşünme adımı
> atlanır ve konu öğrenilmiş gibi görünür.
>
> Her cevapta dört parça var: **Kısa cevap · Ayrıntı · 📌 Sık yapılan hata · 🔗 Konu**

---

## 00.1 Vibe coding nedir

### Soru 1 — Karpathy'nin terimi geri alma sebebi neydi ve yerine ne koydu? İkisi arasındaki fark hangi tek kelimeyle özetlenir?

**Kısa cevap:** Terimi **atılacak projeler** için tarif etmişti; insanlar onu
üretim yazılımı için kullanmaya başladı. Yerine **ajan mühendisliği** dedi.
Farkı özetleyen kelime: **denetim**.

**Ayrıntı:**

| | Vibe coding (Şubat 2025) | Ajan mühendisliği (Nisan 2026) |
|---|---|---|
| Tarif edildiği kullanım | Hafta sonu projesi, prototip | Bakılacak, yaşayacak yazılım |
| Sen ne yaparsın | Sonuca bakarsın | Ajanları yönetir ve **denetlersin** |
| Kanıt beklentisi | Yok — "çalışıyorsa tamam" | Var — test, çıktı, gösterilen sonuç |
| Kime ne kazandırır | Yeni başlayanın **tabanı** | Profesyonelin **tavanı** |

Kendi ifadesiyle: kodu artık %99 sen yazmıyorsun, **yönlendiriyor ve
denetliyorsun** — ve bunun bir uzmanlığı var.

📌 **Sık yapılan hata:** "Karpathy vibe coding'in işe yaramadığını söyledi"
sanmak. Söylediği bu değil; **ajanla çalışmanın varsayılan hâline geldiğini,
ama daha yüksek denetimle eşleşmesi gerektiğini** söyledi.

🔗 [00.1 §1](01-genel-bakis-ve-trickler.md)

---

### Soru 2 — "Bağlam dolduğunda oturum biter" cümlesi neden yanlış? Gerçekte ne olur ve bunun neden fark edilmesi zordur?

**Kısa cevap:** Oturum bitmez; sistem eski konuşmayı **özetleyip** yer açar
(sıkıştırma). Fark edilmesi zordur çünkü **hata mesajı yoktur** — yalnızca
cevapların kalitesi düşer.

**Ayrıntı:** İki ayrı olgu var, karıştırılıyor:

1. **Sınır** — pencere dolunca özetleme devreye girer, çalışmaya devam edersin.
   Bu görünür ve yönetilebilir.
2. **Bozulma** — pencere dolarken, sınıra varmadan çok önce, model daha önce
   verdiğin talimatları kaçırmaya ve daha çok hata yapmaya başlar. Bu
   **görünmez**.

Görünmezliğin sebebi: başarısızlık bir istisna değil, **derece kaybı**. Program
çökseydi fark ederdin; cevap biraz daha kötüleştiğinde fark etmezsin. Belirtiler
dolaylıdır: aynı şeyi ikinci kez söylemek zorunda kalmak, ajanın az önce
düzelttiği hatayı tekrar yapması, kural dosyandaki bir maddeyi görmezden gelmesi.

📌 **Sık yapılan hata:** Kaliteyi modelin "kötü günü" sanmak ve aynı oturumda
daha sert istemlerle zorlamak. Bu bağlamı daha da doldurur; sorunu büyütür.

🔗 [00.1 §2](01-genel-bakis-ve-trickler.md) · ayrıntısı
[1.3](../01-ajanla-calismak/1.3-baglam-yonetimi.md)

---

### Soru 3 — Ajana doğrulama vermenin, "kodu sonra ben kontrol ederim" demekten farkı ne? İki farklı sonucu say.

**Kısa cevap:** Doğrulama verirsen **döngü ajanda kapanır**; vermezsen
doğrulama halkası sen olursun.

**Ayrıntı — iki sonuç:**

1. **Hata ne zaman yakalanır.** Ajanın çalıştırabildiği bir denetim varsa hata
   üretildiği anda yakalanır ve ajan kendisi düzeltir. Denetim yoksa hata,
   **senin bakmanı** bekler — yani en iyi ihtimalle dakikalar, gerçekte
   çoğunlukla üretimde ortaya çıkar.
2. **Gözetimsiz çalışabilir mi.** Denetim varsa başında durmadığın bir iş doğru
   bitebilir. Yoksa her iş senin dikkat süren kadar uzayabilir; ajan "bitmiş
   göründüğünde" durur ve bu tek sinyal yanıltıcıdır.

Denetim olabilecek şeyler: test takımı, derleme çıkış kodu, tip denetleyici,
çıktıyı beklenen sonuçla karşılaştıran betik, ekran görüntüsü karşılaştırması.

📌 **Sık yapılan hata:** "Testleri de yaz" demeyi doğrulama sanmak. Test
**yazdırmak** yetmez; ajanın onu **çalıştırıp sonucu görmesi** gerekir. İstemde
"çalıştır ve sonucu göster" yoksa döngü kapanmaz.

🔗 [00.1 §3.1](01-genel-bakis-ve-trickler.md) ·
[1.5](../01-ajanla-calismak/1.5-dogrulama-refleksi.md)

---

### Soru 4 — Hangi durumda plan modu kullanmamak doğrudur? Ölçütü tek cümleyle yaz.

**Kısa cevap:** **Değişikliği tek cümleyle tarif edebiliyorsan** plan yapma.

**Ayrıntı:** Plan modunun bir bedeli var — ekstra tur, ekstra token, ekstra
bekleme. Bu bedel ancak belirsizlik varken karşılığını verir.

| Plan yap | Plan yapma |
|---|---|
| Yaklaşımdan emin değilsin | Yazım hatası düzeltmesi |
| Değişiklik birden çok dosyaya dokunuyor | Tek satır log ekleme |
| Dokunacağın kodu tanımıyorsun | Değişken adı değiştirme |
| Geri alması pahalı bir karar var | Sonucu bariz, küçük değişiklik |

📌 **Sık yapılan hata:** Plan modunu güvenlik önlemi sanmak. Plan modu
**belirsizliği** azaltır, **yetkiyi** kısıtlamaz — o izin kipinin ve kum
havuzunun işi.

🔗 [00.1 §3.4](01-genel-bakis-ve-trickler.md) ·
[1.4](../01-ajanla-calismak/1.4-izinler-ve-plan-modu.md)

---

### Soru 5 — Aynı hatayı üçüncü kez düzeltmek üzeresin. Neden durman gerekiyor ve bunun yerine ne yapmalısın?

**Kısa cevap:** Bağlam **başarısız denemelerle** doldu; üçüncü düzeltme de büyük
olasılıkla tutmaz. Doğrusu: bağlamı temizle ve öğrendiklerini içeren **daha iyi
bir ilk istem** yaz.

**Ayrıntı:** İki düzeltme sonrası oturumun içinde artık şunlar var: yanlış
yaklaşım, onun kısmi düzeltmesi, ikinci yanlış yaklaşım, senin sinirli
tekrarların. Bunların hepsi her istekte tekrar gönderiliyor ve modelin dikkatini
**işe yaramadığı kanıtlanmış yollara** çekiyor.

Temiz oturum + iyi istem, uzun oturum + birikmiş düzeltmelerden neredeyse her
zaman daha iyi sonuç verir. "İyi istem" derken: iki denemede öğrendiğin şeyi
baştan söylemek — *"X yaklaşımını deneme, çünkü Y; Z dosyasındaki kalıbı izle."*

📌 **Sık yapılan hata:** Temizlemeyi "ilerlemeyi çöpe atmak" sanmak. Kod diskte
duruyor; atılan şey yalnızca **işe yaramamış konuşma**.

🔗 [00.1 §3.3 ve §4](01-genel-bakis-ve-trickler.md)
