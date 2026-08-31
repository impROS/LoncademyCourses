# Son Tekrar — Sınavdan 24 Saat Önce

> Bu dosya **yeni bir şey öğretmez**. Amacı: hazırlığını kontrol etmek, sınav stratejisini
> tazelemek ve refleksleri son bir kez geçirmek.
> **Bu gece yeni konu çalışma.** Beyin dinlenmeye ihtiyaç duyar; yarın hız gerekecek.

---

## 1. Hazırlık kontrol listesi

### Bilgi tarafı

- [ ] 30 konu testinin **hepsini** en az bir kez çözdüm.
- [ ] Hepsinde **%80+** aldım (almadıklarımı not ettim: ______________________)
- [ ] `99-final/cheatsheet.md` dosyasını baştan sona okudum ve **yabancı satır kalmadı**.
- [ ] İki deneme sınavını da **süre tutarak** çözdüm.
- [ ] Deneme skorlarım: Deneme 1 = ____% · Deneme 2 = ____%
- [ ] Deneme sınavlarındaki **yanlışlarımın hepsinin sebebini** biliyorum.

> ⚠️ Denemelerde **%70'in altındaysan** randevunu ertelemeyi ciddi ciddi düşün.
> Gerçek sınav denemelerden **daha zordur**: soruları ilk kez görürsün ve süre baskısı vardır.

### Lojistik tarafı

- [ ] Sınav tarih ve saatimi **onay e-postasından** doğruladım.
- [ ] **Fotoğraflı, devlet tarafından verilmiş kimliğim** hazır ve üzerindeki isim Oracle hesabımla **birebir aynı**.
- [ ] Online sınavsam: **sistem testini** çalıştırdım, kamera/mikrofon çalışıyor.
- [ ] Online sınavsam: masamı **tamamen boşalttım**, odada yalnız olacağım.
- [ ] Test merkezindeysem: yolu ve süreyi kontrol ettim, **30 dakika erken** varacağım.
- [ ] Telefon, akıllı saat ve notlar **başka bir odada** olacak.
- [ ] Sınavdan hemen önce **tuvalete gideceğim** — 120 dakika **ara yok**.

---

## 2. Sınav stratejisi — 3 tur

| Tur | Süre | Ne yapılır |
|---|---|---|
| **1. tur** | ~85 dk | Her soruyu **bir kez** oku. 2 dakikada çözemiyorsan **işaretle (flag) ve geç**. |
| **2. tur** | ~25 dk | Yalnızca işaretli soruları çöz. |
| **3. tur** | ~10 dk | Boş kalan **her soruya tahmin işaretle**. Boş soru kalmasın. |

**Negatif puan yok.** Boş bırakmak = yanlış. Emin olmadığında bile **mutlaka işaretle**.

**Kısmi puan yok.** `(Choose TWO.)` sorularında bir doğru bir yanlış = **0 puan**.
Emin olduğun bir doğruyu bul, ikinciyi ona göre ara.

### Kod okuma sırası

1. **Şıklara bak.** `Compilation fails` var mı? Varsa gözün **derleme hatası** arasın.
2. **İmzalara bak:** tipler, `final`, `static`, erişim belirteçleri. Hata genelde buradadır.
3. **Satır satır izle.** Değişken değerlerini takip et.
4. **Satır numarası verilmişse** (`// line 5`) o satır neredeyse kesin sorunun merkezidir.

### Eleme taktiği

- İki şık **aynı anlama geliyorsa** ikisi de yanlıştır.
- Mutlak ifadeli şıklar (`always`, `never`, `only`) genelde yanlıştır —
  ama Java'da bazı kurallar gerçekten mutlaktır (`finally` her zaman çalışır). **Körlemesine eleme.**
- Bilmediğin bir metot adı görürsen (`Character.parseChar`, `CharStream`, `IntStream.toList`)
  **muhtemelen o metot yoktur**.

---

## 3. Son refleks turu — 5 dakika

Aşağıdakileri okurken cevabı **anında** gelmiyorsa ilgili konuya bir bak.

| Soru | Cevap |
|---|---|
| `byte b = 10; b = b + 300;` | **Derlenmez** (`b += 300` derlenir → 54) |
| `Integer a=128, b=128; a==b` | **`false`** |
| `s.concat("x");` sonrası `s` | **Değişmez** |
| `LocalDate.of(2026,1,31).plusMonths(1)` | **`2026-02-28`** |
| `switch` ifadesi `default`'suz, selector `Object` | **Derlenmez** |
| Pattern `switch`'e `null` gelirse | **NPE** (`case null` yoksa) |
| `Ust u = new Alt(); u.alan` | **Üst** sınıfın alanı |
| `equals(Nokta n)` | **Overload**, override değil |
| `sealed` alt tipte belirteç yok | **Derlenmez** |
| Record'a örnek alan eklemek | **Derlenmez** |
| `try { return 1; } finally { return 2; }` | **`2`** |
| İki kaynağın kapanma sırası | **Ters** (son açılan önce) |
| `List<Integer> l; l.remove(1);` | **İndeksi** siler |
| `Set.of("a","a")` | **`IllegalArgumentException`** |
| `TreeSet.addFirst("z")` | **`UnsupportedOperationException`** |
| Boş stream + `allMatch` | **`true`** |
| `Stream.of(1,2).reduce(0, Integer::sum)` dönüş tipi | **`Integer`**, `Optional` değil |
| `IntStream.range(1,4).toList()` | **Derlenmez** — `boxed()` gerekir |
| `Optional.of("a").orElse(pahali())` | `pahali()` **çalışır** |
| `exports` vs `opens` | Derleme erişimi vs **yansıma** |
| `java -m` değeri | **`modul/SinifTamAdi`** |
| `t.run()` | **Aynı thread** |
| Virtual thread `setDaemon(false)` | **`IllegalArgumentException`** |
| Görev exception attı, `get()` çağrılmadı | **Hiçbir şey görünmez** |
| `volatile int sayac; sayac++;` | **Thread-safe değil** |
| `Path.of("/a/b").resolve("/x/y")` | **`/x/y`** |
| Deserialization'da ctor | **Çalışmaz** (ilk non-serializable üstünki çalışır) |
| Bundle'da anahtar yok | Daha **genele** bakılır, hepsinde yoksa **`MissingResourceException`** |

---

## 4. Sınav günü — saat saat

| Ne zaman | Ne yap |
|---|---|
| **Akşam** | Cheatsheet'i bir kez oku. **Yeni konu çalışma.** Erken yat. |
| **Sabah** | Normal kahvaltı. Ağır yemek yeme. |
| **T−60 dk** | Refleks turunu (bölüm 3) bir kez geçir. Sonra **kapat**. |
| **T−30 dk** | Merkeze var / check-in'e başla. Tuvalete git. |
| **T−5 dk** | Derin nefes. İlk soruyu görmeden panik yapma — ilk 3 soru genelde en zor hissettirir. |
| **Sınav** | 3 turlu stratejiyi uygula. Saate **her 10 soruda bir** bak. |
| **Bitince** | Sonuç ekranda çıkar. Geçtiysen tebrikler; geçmediysen **rapordaki zayıf alanları not et**. |

---

## 5. Sınav sırasında panik anları için

| Durum | Ne yap |
|---|---|
| Hiç görmediğim bir API | Şıkları ele: var olmayan metot adı **genelde yanlıştır**. Sonra tahmin et ve **geç**. |
| Kod çok uzun | Önce şıklara bak: `Compilation fails` varsa hata ara, yoksa yalnızca **ilgili satırları** izle. |
| Zaman azalıyor | İşaretli soruları bırak, **boşları doldur**. Tahmin > boş. |
| İki şık arasında kaldım | Mutlak ifadeliyi ele, kalanı seç. **Geri dönüp değiştirme** — ilk sezgi genelde doğrudur. |
| Kafam durdu | 10 saniye gözlerini kapat, nefes al. Sonraki soruya geç; geri dönersin. |

---

## 6. Kaldıysan

Bu bir felaket değil, bir **veri noktasıdır**.

1. **Sonuç raporunu sakla** — alan bazlı performansını gösterir.
2. Zayıf çıkan alanların konu dosyalarına dön, testlerini **%90 olana kadar** çöz.
3. Enthuware mock testlerinden 3-4 tane daha çöz.
4. **14 gün** bekleme süresi var (⚠️ politikayı teyit et) ve yeni voucher gerekir.
5. İkinci denemede geçme oranı belirgin biçimde yüksektir — çünkü artık sınavın **dilini** biliyorsun.

---

## Başarılar

Buraya kadar geldiysen 30 konu, 442 soruluk test ve iki deneme sınavı çözmüşsün.
Sınav senden bilmediğin bir şey istemeyecek — yalnızca **dikkatini** isteyecek.

➡️ [Deneme Sınavı 1](deneme-1.html) · [Deneme Sınavı 2](deneme-2.html) · [Cheatsheet](cheatsheet.md)
