# 02 · Producer — Kendini kontrol cevapları

> Bu dosya [2.1](2.1-accumulator-ve-batching.md) – [2.4](2.4-serialization-ve-sema.md) konularının
> sonundaki **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [2.1](#21-accumulator-ve-batching) · [2.2](#22-idempotence-ve-sıralama) ·
[2.3](#23-teslimat-garantileri) · [2.4](#24-serialization-ve-şema)

---

## 2.1 Accumulator ve batching

📄 Sorular: [`2.1-accumulator-ve-batching.md`](2.1-accumulator-ve-batching.md)

### Soru 1 — `producer.send(record).get()` throughput'u neden yıkar?

**Kısa cevap:** `get()` `Future`'ı bloklar; uygulama thread'i **broker cevabını bekler**. Bu,
batching'i tamamen öldürür — her batch tek kayıtlık olur, her kayıt için ayrı bir round-trip
ödenir. Throughput `1 / RTT` ile sınırlanır. Alternatifi **callback'li asenkron gönderim**tir.

**Ayrıntı:**

- `send()` normalde asenkrondur: kaydı `RecordAccumulator`'a koyar ve **hemen döner**. Sender
  thread'i arka planda batch'leri toplayıp gönderir. `get()` bu mimariyi devre dışı bırakır.
- Sayısal etki: 1 ms RTT ([gidiş-dönüş süresi ↗](../00-baslangic/02-kavram-sozlugu.md#rtt)) ile `get()`'li kod **saniyede ~1000 kayıt** yapabilir. Aynı cluster'da
  asenkron + batching ile lab'da **961.538 kayıt/sn** ölçüldü. Fark üç mertebedir.
- Doğrusu:
  ```java
  producer.send(record, (metadata, exception) -> {
      if (exception != null) { deadLetterStore.save(record, exception); }
  });
  ```
- **"Ama gönderildiğinden emin olmam lazım"** ihtiyacı `get()` ile değil şöyle karşılanır:
  toplu işin sonunda **bir kez** `producer.flush()` çağır, callback'lerde hata sayacını kontrol et.
  Böylece hem batching hem kesinlik olur.
- Callback'in **Sender thread'inde** çalıştığını unutma: içinde bloklayan iş yaparsan tüm
  producer'ı durdurursun.

> 📌 **Sık yapılan hata:** `get()`'i "hata yakalamak için" kullanmak. Hataları callback zaten
> verir; `get()` sadece bekletir. Tek meşru kullanımı, gerçekten kayıt-kayıt senkron akış
> gereken (çok nadir) bir senaryodur.

🔗 Konu: [2.1 §1](2.1-accumulator-ve-batching.md) · [2.3 §4](2.3-teslimat-garantileri.md)

---

### Soru 2 — 200 partition, `batch.size=64KB`: minimum `buffer.memory`?

**Kısa cevap:** Teorik tavan `200 × 64 KiB = 12,8 MiB`'dır — **ama bu asgari değil, tek turluk
tavandır**. Sender thread'i gönderirken yeni kayıtlar gelmeye devam ettiği için pratikte bunun
**2-3 katını** istersin: ~**32-48 MiB**. Varsayılan `buffer.memory` 32 MiB'dır, yani bu senaryoda
sınırda kalır.

**Ayrıntı:**

- **Kritik nokta:** `batch.size` **partition başınadır**, istek başına değil. Producer her aktif
  partition için ayrı bir batch kuyruğu tutar. 200 partition'a yazıyorsan 200 batch birikebilir.
- Tampon dolduğunda ne olur? `send()` `max.block.ms` (varsayılan **60 s**) kadar **bekler**,
  sonra `TimeoutException` fırlatır. Yani belirti "producer yavaşladı" ya da "uygulama dondu"dur.
  Bu Kafka'nın **backpressure** mekanizmasıdır — bir hata değil, bir sinyal.
- Neden 2-3 kat? Bir batch gönderilirken (uçuşta) belleği hâlâ tutulur; cevap gelene kadar
  serbest kalmaz. `max.in.flight=5` ile aynı anda 5 istek uçuşta olabilir.
- Formül:
  ```
  buffer.memory ≥ aktif_partition × batch.size × emniyet_katsayısı (2–3)
  ```
- **Gerçekten 200 partition'a mı yazıyorsun?** Key dağılımın dar ise aktif partition sayısı çok
  daha az olabilir; ölçmek için `buffer-available-bytes` metriğini izle.

> 📌 **Sık yapılan hata:** `buffer.memory` yerine `max.block.ms`'i büyütmek. Bu, semptomu
> gizler: uygulama hata almaz ama **sessizce donar**. Doğru refleks önce broker tarafına bakmak,
> sonra tamponu boyutlandırmaktır.

🔗 Konu: [2.1 §1, §4](2.1-accumulator-ve-batching.md)

---

### Soru 3 — `linger.ms=100`: iki olumlu, bir olumsuz sonuç

**Kısa cevap:**

**Olumlu 1 — Verim artar.** Batch'ler dolar; istek başına kayıt sayısı yükselir, broker'a giden
istek sayısı düşer. Lab'da `linger=0 → 5` geçişi tek başına **%29** kazandırdı; `linger=50` +
`batch=64K` ise **2,2 kat**.

**Olumlu 2 — Sıkıştırma oranı iyileşir → ağ ve disk maliyeti düşer.** Sıkıştırma record batch'in
**tamamına** uygulanır; büyük batch daha iyi sıkışır. Lab'da lz4 ile toplam **6,1 kat** kazanç
alındı çünkü ağa giden bayt 23 kat azaldı.

**Olumsuz — Gecikme artar.** Her kayıt en kötü durumda 100 ms fazladan bekler. Düşük gecikme
gerektiren senaryolarda (istek-yanıt, kullanıcıyı bekleten akışlar) bu kabul edilemez.

**Ayrıntı — önemli nüans:** `linger.ms` **her zaman** gecikme eklemez. Sistem doygunsa batch'ler
zaten `batch.size`'a ulaşıp gidiyordur; `linger.ms` hiç beklemez ve gecikmeye etkisi ~0 olur.
Etki en çok **düşük ve düzensiz trafikte** görülür — orada batch dolmaz, süre dolar.

**Bonus olumlu:** Broker CPU'su düşer (daha az request parse), replikasyon trafiği küçülür,
`RequestsPerSec` metriği rahatlar.

> 📌 **Sık yapılan hata:** `linger.ms`'i tek başına artırıp `batch.size`'ı 16 KiB'da bırakmak.
> Batch zaten 16 KiB'da kapanıyorsa beklemenin kazancı sınırlıdır. İkisi **birlikte** ayarlanır.

🔗 Konu: [2.1 §2](2.1-accumulator-ve-batching.md) · [5.1 Performans tuning](../05-operasyon/5.1-performans-tuning.md)

---

### Soru 4 — `compression-rate-avg` 0.95: ne demek, ne yaparsın?

**Kısa cevap:** Sıkıştırma **neredeyse hiç işe yaramıyor** — 100 bayt veri 95 bayta iniyor.
Buna karşılık producer CPU'sunu ve gecikmeyi ödüyorsun. İki olası sebep var: (1) veri zaten
sıkıştırılmış/rastgele (resim, video, şifreli, base64'lenmiş binary), (2) **batch'ler çok küçük**
ve sıkıştırıcı tekrar bulacak yeterli veri göremiyor.

**Ayrıntı — ne yaparsın, sırayla:**

1. **Önce batch boyutuna bak.** `batch-size-avg` ve `records-per-request-avg` metriklerini oku.
   `records-per-request-avg` 1-2 civarındaysa sorun sıkıştırmada değil **batching'de**dir;
   `linger.ms` ve `batch.size`'ı artır, oranı yeniden ölç.
2. **Veri tipini sorgula.** İçerik zaten sıkıştırılmışsa (JPEG, PDF, gzip'lenmiş payload) hiçbir
   codec kazandıramaz. Bu durumda `compression.type=none` yap — **CPU'yu boşuna harcama**.
3. **Codec değiştirmeyi dene.** `lz4` hızlıdır ama en iyi oranı vermez; `zstd` daha iyi sıkıştırır
   (lab'da 0.020 vs 0.043) ama daha yavaştır (526K vs 961K kayıt/sn). Veri metin ağırlıklıysa
   zstd'yi ölç.
4. **Veri modelini sorgula.** Base64'lenmiş binary alanlar hem %33 şişirir hem sıkışmaz;
   binary'yi binary olarak taşı (Avro/Protobuf, 2.4).

**Metriğin okunuşu:** `compression-rate-avg` = sıkıştırılmış / sıkıştırılmamış. 1.0 = hiç
sıkışmadı; 0.04 = 25 kat küçüldü. **Küçük iyidir.**

> 📌 **Sık yapılan hata:** Sıkıştırmayı açıp bir daha bakmamak. Sıkıştırma bedava değildir —
> ölçmediğin sürece CPU'yu boşa yakıyor olabilirsin.

🔗 Konu: [2.1 §2, §5](2.1-accumulator-ve-batching.md)

---

### Soru 5 — Key'i olan bir topic'te partition sayısını artırmanın etkisi (müşteri örneğiyle)

**Kısa cevap:** Varsayılan partitioner `murmur2(key) % N` kullanır. **N değişince** aynı key
başka bir partition'a düşer. O andan itibaren aynı müşterinin eski olayları bir partition'da,
yeni olayları başka bir partition'da olur — ve iki partition **paralel** tüketildiği için
**sıra garantisi kırılır**.

**Somut örnek:**

`musteri-42` müşterisi için 10 partition'lı bir topic'te:
```
murmur2("musteri-42") % 10 = 7   →  tüm olayları partition 7'de, sıralı
```
Partition sayısı 20'ye çıkarılıyor:
```
murmur2("musteri-42") % 20 = 17  →  yeni olaylar partition 17'ye gidiyor
```
Şu an partition 7'de müşterinin **işlenmemiş** `SIPARIS_OLUSTURULDU` olayı bekliyor olabilir.
Partition 17'yi okuyan tüketici ise yeni gelen `SIPARIS_IPTAL` olayını **hemen** işler. Sonuç:
iptal, oluşturmadan **önce** işlenir. Uygulama "olmayan siparişi iptal etme" hatası verir ya da
daha kötüsü, sessizce tutarsız bir duruma düşer.

**Neden geri alınamaz:** Partition sayısını **azaltamazsın**. Eski veri eski partition'larında
kalır; yeni veri yeni dağılıma gider. Tek çıkış yolu: **yeni bir topic** oluşturup veriyi
yeniden anahtarlayarak taşımak (ve tüketicileri kesintiyle geçirmek).

**Ne yapılmalı:**
- Partition sayısını **baştan** cömert seç (5.5'teki kapasite formülü).
- Artırma zorunluysa: üretimi durdur → tüketicilerin lag'ini **0**'a indir → partition'ı artır →
  üretimi başlat. Böylece "eski partition'da bekleyen olay" kalmaz.
- Ya da anahtar bazlı sıraya gerçekten ihtiyacın yoksa (`partitioner.ignore.keys=true` senaryosu)
  bu problem hiç doğmaz.

> 📌 **Sık yapılan hata:** Partition artırmayı "kesintisiz bir ölçekleme işlemi" sanmak. Kafka
> komutu anında döner ve hiçbir uyarı vermez; kırılan şey **veriyi işleyen uygulamanın
> varsayımıdır**.

🔗 Konu: [2.1 §3](2.1-accumulator-ve-batching.md) · [5.5 Kapasite ve partition tasarımı](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md)

---

## 2.2 Idempotence ve sıralama

📄 Sorular: [`2.2-idempotence-ve-siralama.md`](2.2-idempotence-ve-siralama.md)

### Soru 1 — Broker `sequence = son + 3` olan bir batch aldı: ne yapar?

**Kısa cevap:** Reddeder ve **`OutOfOrderSequenceException`** döner. Çünkü arada **iki batch
eksiktir** — broker o boşluğun içeriğini bilmediği için batch'i kabul etse log'da sessiz bir
delik oluşurdu.

**Ayrıntı — broker'ın üç kararı:**

| Broker'ın gördüğü | Kararı |
|---|---|
| `sequence == son + 1` | Kabul et, yaz |
| `sequence <= son` | **Duplicate** — sessizce at, başarı dön |
| `sequence > son + 1` | **Boşluk** → `OutOfOrderSequenceException` |

**Neden bu davranış doğrudur:** Idempotence'ın sözü iki parçalıdır — "duplicate yok" **ve**
"sıra korunur". Boşluklu bir batch'i kabul etmek ikinci sözü bozardı ve daha kötüsü, eksik
batch'ler asla gelmezse **veri kaybını sessizleştirirdi**. Kafka burada bilinçli olarak
"gürültülü hata"yı seçer.

**Bu hata ne anlama gelir:** `OutOfOrderSequenceException` **ölümcül** bir sinyaldir — producer
o noktadan sonra kullanılamaz, yeniden oluşturulması gerekir. Ama asıl önemlisi **sebebini
araştırmaktır**:
- Broker producer state'ini düşürmüş olabilir (uzun boşluk, `transactional.id.expiration.ms`,
  retention ile `.snapshot` kaybı) → `UnknownProducerIdException` da eşlik edebilir.
- Gerçekten veri kaybı yaşanmış olabilir (unclean lider seçimi sonrası log truncate, 1.2).
- Aynı `transactional.id`'yi iki instance kullanıyor olabilir (4.1).

> 📌 **Sık yapılan hata:** Bu hatayı yakalayıp yeni bir producer oluşturarak "çözmek". Producer
> yenilenir ama **neden boşluk oluştuğu** araştırılmazsa, sessiz veri kaybını üstünü örtmüş
> olursun.

🔗 Konu: [2.2 §1, §5](2.2-idempotence-ve-siralama.md)

---

### Soru 2 — `enable.idempotence=true` + `acks=1`: ne olur, neden başlangıçta reddedilir?

**Kısa cevap:** Client **hiç başlamaz** — `ConfigException` fırlatır:
*"Must set acks to all in order to use the idempotent producer."* Reddin başlangıçta olmasının
sebebi, bu kombinasyonun **mantıksal olarak imkânsız** olmasıdır: idempotence'ın garantisi
`acks=1` ile sağlanamaz, dolayısıyla çalışma anında "kısmen çalışan" bir hâl yoktur.

**Ayrıntı:**

- **Neden `acks=all` şart?** Idempotence, broker'ın `(PID, partition)` için tuttuğu sequence
  durumuna dayanır. `acks=1` ile lider çökerse, yeni lider o sequence durumunu **görmemiş**
  olabilir (kayıtlar ona replike olmamıştı). Producer retry ederse yeni lider aynı sequence'ı
  "yeni" sanar ve **duplicate yazar**. Yani `acks=1` + idempotence = yalan bir garanti.
- Aynı sebeple **`retries=0`** de reddedilir (*"Must set retries to non-zero"*) — retry olmadan
  idempotence'ın koruyacağı bir şey yoktur — ve **`max.in.flight > 5`** reddedilir
  (*"must be set to at most 5"*).
- Gerçek lab çıktısı:
  ```
  max.in.flight = 6 (idempotence açık)  → RED: ... must be set to at most 5.
  acks = 1 (idempotence açık)           → RED: Must set acks to all ...
  retries = 0 (idempotence açık)        → RED: Must set retries to non-zero ...
  varsayılan idempotent yapılandırma    → KABUL
  ```

**Neden başlangıçta, çalışma anında değil — tasarım gerekçesi:** Yanlış yapılandırmanın bedeli
**sessiz veri sorunu**dur. Kafka bunu "fail fast" ile görünür kılar: uygulaman ilk saniyede
açılmaz, üretimde saatler sonra duplicate keşfetmezsin.

> 📌 **Sık yapılan hata:** 3.x'ten 4.x'e geçerken eski kodda kalmış `acks=1`/`retries=0`
> satırları. Kafka 3.0'dan beri `enable.idempotence` **varsayılan açık** olduğu için uygulama
> yükseltmeden sonra **hiç başlamaz**. Klasik geçiş hatasıdır; çözüm eski satırları silmektir.

🔗 Konu: [2.2 §3](2.2-idempotence-ve-siralama.md)

---

### Soru 3 — Cevap gelmeden çöktü, restart edip aynı kaydı gönderdi: duplicate olur mu?

**Kısa cevap:** **Evet, olur.** Idempotence **oturum başınadır**. Yeni süreç broker'dan **yeni
bir PID** alır; broker'ın "bunu daha önce gördüm" diyebilmesi için gereken bağ kopar. Aynı içerik,
farklı PID → broker için **iki ayrı kayıt**.

**Ayrıntı — lab kanıtı:** İki ayrı producer oturumu aynı partition'a yazdı, log dump'ı:
```
baseOffset: 0  baseSequence: 0  producerId: 5005   ← oturum A
| offset: 0  sequence: 0  payload: oturum-A-kayit-0
baseOffset: 3  baseSequence: 0  producerId: 4004   ← oturum B (FARKLI PID, sequence yine 0!)
| offset: 3  sequence: 0  payload: oturum-B-kayit-0
```
Dikkat: oturum B'nin sequence'ı yine **0**'dan başlıyor. Broker bunu duplicate saymaz, çünkü
tekillik anahtarı `(PID, partition, sequence)` üçlüsüdür ve PID farklı.

**Ne çözer:**

| İhtiyacın | Çözüm |
|---|---|
| Tek oturum içinde retry duplicate'i olmasın | `enable.idempotence=true` (varsayılan) — yeter |
| **Restart'a dayanıklı** tekilleştirme | `transactional.id` ile transaction (4.1) — PID kalıcı hâle gelir |
| Kafka → dış sistem tekilliği | Tüketicide idempotent yazma / outbox (4.2) |

`transactional.id` verdiğinde producer `initTransactions()` çağrısında **aynı** `transactional.id`
için **aynı PID'yi** geri alır (epoch artırılmış olarak) — işte restart'a dayanıklılık buradan gelir.

> 📌 **Sık yapılan hata:** "Idempotence açık, artık duplicate olmaz" cümlesi. Doğrusu: *"Tek bir
> producer oturumunda, tek bir partition için, **retry kaynaklı** duplicate olmaz."* Restart,
> uygulama seviyesinde tekrar `send()` etmek ve tüketici tarafı bu garantinin **dışındadır**.

🔗 Konu: [2.2 §2, §4](2.2-idempotence-ve-siralama.md) · [4.1 Transactions](../04-eos-transaction/4.1-transactions-internals.md)

---

### Soru 4 — `max.in.flight=5`, idempotence açık, 3. batch başarısız: sıra nasıl korunur?

**Kısa cevap:** Broker sequence numaralarına bakar. 3. batch başarısız olup 4. ve 5. batch'ler
ulaştığında, broker onları `sequence > son + 1` gördüğü için **kabul etmez** —
`OutOfOrderSequence` sinyaliyle geri çevirir. Client 3'ü yeniden gönderir, ardından 4 ve 5'i de
yeniden gönderir. Log'a yazılan sıra **her zaman** sequence sırasıdır.

**Ayrıntı:**

- **Neden tam olarak 5?** Broker `(PID, partition)` için son **5 batch'in** durumunu bellekte
  tutar. Daha fazla uçuşta istek olsaydı, sıra dışı gelen bir batch'i doğru yere yerleştirmek
  için gereken bilgi elde olmazdı. Bu yüzden `max.in.flight > 5` yapılandırması **başlangıçta
  reddedilir**.
- Client tarafında da bir mekanizma var: idempotence açıkken producer, bir partition için
  başarısız olan batch'ten **sonraki** batch'leri yeniden sıraya sokar ve doğru sırada gönderir.
  Yani düzeltme iki taraflıdır.
- **Bu neden önemli:** Kafka 3.0 **öncesinde** yaygın tavsiye "sıra istiyorsan
  `max.in.flight=1` yap" idi. Bu tavsiye bugün **geçersizdir** ve throughput'u gereksiz yere
  beşte birine düşürür.

| | Idempotence kapalı | Idempotence açık |
|---|---|---|
| `max.in.flight=1` | Sıra korunur, verim düşük | Sıra korunur |
| `max.in.flight=5` | ⚠️ Retry olursa sıra bozulabilir | ✅ Sıra korunur |
| `max.in.flight>5` | Sıra bozulabilir | ❌ Client başlamaz |

> 📌 **Sık yapılan hata:** Eski blog yazılarındaki `max.in.flight=1` tavsiyesini 4.x'e taşımak.
> Kaynağın sürümüne bak.

🔗 Konu: [2.2 §3](2.2-idempotence-ve-siralama.md)

---

### Soru 5 — `retries=Integer.MAX_VALUE`: sonsuza kadar mı denenir?

**Kısa cevap:** **Hayır.** Asıl sınır **`delivery.timeout.ms`**'tir (varsayılan **120.000 ms =
2 dakika**). Bu süre dolduğunda kayıt, kaç deneme kaldığından bağımsız olarak **kalıcı hata**
sayılır ve callback'e `TimeoutException` gelir.

**Ayrıntı — zaman bütçesi hiyerarşisi:**
```
delivery.timeout.ms (120 s)  ≥  linger.ms (5 ms) + request.timeout.ms (30 s)
```
- `request.timeout.ms` (**30 s**) = **tek bir isteğin** cevap bekleme süresi.
- `delivery.timeout.ms` (**120 s**) = kaydın `send()` çağrısından itibaren **toplam** teslimat
  bütçesi: tamponda bekleme + tüm retry'lar + tüm istek süreleri dahil.
- `retries` (varsayılan `Integer.MAX_VALUE`) pratikte **anlamsızdır**: süre bütçesi önce dolar.
- Ayrıca `retry.backoff.ms` (varsayılan 100 ms, üstel artışlı) denemeler arasına bekleme koyar,
  yani 2 dakikada sonsuz sayıda deneme zaten yapılamaz.

**Doğru refleks:** "Retry sayısını artıralım" değil, **"teslimat süresini konuşalım"**. İş
gereksinimin "bir kayıt en fazla 10 saniye gecikebilir" ise `delivery.timeout.ms=10000` yap ve
hatayı erken al. "Broker 5 dakika bakımda olabilir, kaybetmek istemiyorum" ise
`delivery.timeout.ms=360000` yap — ama o zaman `buffer.memory`'nin 6 dakikalık üretimi tutabildiğinden
emin ol.

**`retries=0` neden yanlış:** Hem idempotence ile uyumsuzdur (client başlamaz) hem de **geçici
hataları kalıcı hataya çevirir** — lider seçimi sırasındaki 200 ms'lik bir kesintide bile kayıt
düşer.

> 📌 **Sık yapılan hata:** `delivery.timeout.ms`'i `request.timeout.ms`'ten küçük yapmak.
> Client bunu başlangıçta reddeder — ama daha ince hata, `delivery.timeout.ms`'i büyütüp
> `buffer.memory`'yi büyütmeyi unutmaktır: tampon dolar, `send()` bloklar.

🔗 Konu: [2.2 §5](2.2-idempotence-ve-siralama.md)

---

## 2.3 Teslimat garantileri

📄 Sorular: [`2.3-teslimat-garantileri.md`](2.3-teslimat-garantileri.md)

### Soru 1 — `acks=all`, replication.factor=3, min.insync.replicas=2: bir broker bakımda? İki broker bakımda?

**Kısa cevap:**
- **Bir broker bakımda:** ISR 2'ye düşer, `2 >= 2` → **yazma devam eder**. Bu zaten replication.factor=3 +
  min.insync.replicas=2'nin varlık sebebidir: bir broker kaybını **kesintisiz** tolere etmek.
- **İki broker bakımda:** ISR 1'e düşer, `1 < 2` → broker yazmayı **`NotEnoughReplicasException`
  ile reddeder**. Okuma devam eder, yazma durur.

**Ayrıntı — üç ince nokta:**

1. **Client'ın gördüğü hata farklıdır.** `NotEnoughReplicasException` **yeniden denenebilir** bir
   hatadır; client retry'lara girer ve `delivery.timeout.ms` dolunca yüzeye **`TimeoutException`**
   olarak çıkar. Gerçek sebebi görmek için **broker log'una** bakmalısın. Lab'da bu tam olarak
   gözlendi:
   ```
   02:24:23  leader=1 isr=[1]  FAIL TimeoutException: Expiring 1 record(s) for lab-isr-0 ...
   ```
   Retry'ı kapatıp (`-Dretries=0`) çalıştırınca ham hata görünür:
   ```
   02:28:27  leader=1 isr=[1, 3]  FAIL NotEnoughReplicasException: Messages are rejected ...
   ```
2. **Bakım penceresi planlaması:** replication.factor=3 + min.insync.replicas=2 ile **aynı anda yalnızca bir broker**
   bakıma alınabilir. Rolling restart'ta bir sonraki broker'a geçmeden önce ISR'in tam
   genişlediğini (`UnderReplicatedPartitions = 0`) doğrulaman gerekir.
3. **Veri kaybı yok.** Yazmanın durması bir arıza gibi görünür ama Kafka'nın seni **koruduğu**
   andır: tek kopyaya yazıp "başarılı" demek yerine reddediyor.

> 📌 **Sık yapılan hata:** Bakım sırasında yazma durunca paniğe kapılıp `min.insync.replicas`'ı
> 1'e düşürmek. Bu, tam da en kırılgan anda güvenliği kapatmaktır.

🔗 Konu: [2.3 §1](2.3-teslimat-garantileri.md) · [1.2 §3](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

---

### Soru 2 — Callback'e gelen `TimeoutException`'ı yeniden `send()` etmek doğru mu?

**Kısa cevap:** **Genelde hayır — en azından naif biçimde hayır.** Callback'e ulaşan hata
**kalıcıdır**: client zaten `delivery.timeout.ms` boyunca denemiş ve pes etmiştir. Aynı anda
yeniden göndermek iki risk taşır: **sıra bozulması** ve **duplicate**.

**Ayrıntı — üç risk:**

1. **Sıra bozulur.** Kayıt N başarısız olup yeniden gönderilirken N+1, N+2 çoktan yazılmıştır.
   Yeniden gönderilen N artık **onların arkasına** düşer. Aynı key için sıra kırılmış olur.
2. **Duplicate olabilir.** `TimeoutException`, kaydın **yazılmadığı** anlamına gelmez —
   "cevabını alamadım" anlamına gelir. Broker kaydı yazmış ama cevap kaybolmuş olabilir.
   Idempotence bunu **aynı oturum içinde** çözer; ama `delivery.timeout.ms` dolduktan sonraki
   uygulama seviyesi yeniden gönderim **yeni bir kayıt** olarak gider.
3. **Sonsuz döngü ve thread tıkanması.** Callback **Sender thread'inde** çalışır. İçinden
   `send()` çağırmak, üstelik hata devam ediyorsa, producer'ı kendi kuyruğunda boğar.

**Doğrusu:**
```java
producer.send(record, (metadata, exception) -> {
    if (exception == null) return;
    deadLetterStore.save(record, exception);            // 1) kalıcı bir yere yaz
    metrics.counter("producer.failed").increment();     // 2) alarm üretebilir hâle getir
    if (exception instanceof AuthorizationException) {  // 3) sınıfına göre karar
        healthIndicator.down("kafka-acl");
    }
});
```
Yeniden gönderim **ayrı bir süreçte**, sıra ve tekillik kuralları bilinçli seçilerek yapılır.
Sıra kritikse yeniden gönderim genelde **mümkün değildir** — o zaman uygulamayı durdurmak
(fail-fast) daha doğrudur.

> 📌 **Sık yapılan hata:** Callback'te sadece `log.error(...)` yazmak. Bu, **veriyi kaybetmenin
> en yaygın yolu**dur: kayıt gider, kimse fark etmez, log dosyasında bir satır kalır.

🔗 Konu: [2.3 §4](2.3-teslimat-garantileri.md)

---

### Soru 3 — `acks=0` ile ölçülen p99 neden yanıltıcı?

**Kısa cevap:** `acks=0`'da callback **broker onayı beklemeden** tetiklenir. Ölçtüğün şey
teslimat süresi değil, "kaydı tampona koyma + sokete yazma" süresidir. Yani `acks=0`'ın gecikme
sayısı **teslimatı hiç ölçmez**.

**Ayrıntı — lab ölçümü (6.000 kayıt × 500 bayt, sabit 2.000 kayıt/sn, 3 partition, replication.factor=3,
min.insync.replicas=2, `linger.ms=5`):**

| acks | p50 (µs) | p95 (µs) | **p99 (µs)** |
|---|---|---|---|
| `0` | 3.332 | 5.912 | 6.712 |
| `1` | 3.875 | 6.438 | 7.205 |
| `all` | 4.317 | 7.156 | **29.291** |

Üç ders:
1. **Medyanda dayanıklılık neredeyse bedava:** `acks=0` → `all` p50 farkı ~1 ms.
2. **Bedel kuyruktadır:** p99'da `all`, diğerlerinin **4 katı**. Replikasyon = **en yavaş ISR
   üyesini beklemek**; kuyruk gecikmesi oradan gelir.
3. **`acks=0`'ın sayısı karşılaştırılamaz** — farklı bir şey ölçüyor.

**Daha derin sorun:** `acks=0` ile kaybedilen kayıtlar ölçüme **hiç girmez**. Yani "gecikmesi
düşük" görünen yapılandırma, aslında bazı kayıtları teslim edemediği için hızlı görünüyor
olabilir. Kayıp, gecikme dağılımını **iyileştirerek** kendini gizler.

**Doğru ölçüm nasıl yapılır:** Uçtan uca ölç — producer'da `send()` anındaki zaman damgasını
kayda koy, consumer'da işlendiği anda farkı hesapla. Ve mutlaka **sabit hızda** ölç: hız
sınırlamazsan "gecikme" büyük ölçüde kuyrukta bekleme olur ve karşılaştırma anlamsızlaşır.

> 📌 **Sık yapılan hata:** Benchmark'ta `acks=0` kullanıp "Kafka çok hızlı" sonucuna varmak.
> Ölçülen şey Kafka değil, yerel tampona yazma hızıdır.

🔗 Konu: [2.3 §2](2.3-teslimat-garantileri.md) · [5.1 Performans tuning](../05-operasyon/5.1-performans-tuning.md)

---

### Soru 4 — Producer'da hangi üç hata **senkron** fırlatılır?

**Kısa cevap:**
1. **`SerializationException`** — key/value serileştirilemedi.
2. **`RecordTooLargeException`** — kayıt `max.request.size` (1 MiB) sınırını aşıyor.
3. **`TimeoutException` (tampon dolu)** — `buffer.memory` dolu, `max.block.ms` (60 s) boyunca yer
   açılmadı. Aynı yolda **metadata alınamaması** da senkron `TimeoutException` verir.

Bunları yakalamazsan **uygulama thread'inde işlenmemiş istisna** olur: iş akışın kesilir, batch
işi yarıda kalır, bir web isteği 500 döner — ve callback'e **hiçbir şey gelmez**, çünkü kayıt
accumulator'a hiç girmemiştir. Yani sadece callback'e bakan bir hata stratejisi bu üç sınıfı
**tamamen kaçırır**.

**Ayrıntı:**

- Sebep mimaridir: bu üç kontrol `send()` çağrısının **uygulama thread'inde** yürüyen kısmında
  yapılır (serialize → partitioner → accumulator'a koy). Ondan sonrası Sender thread'inin işidir
  ve oradaki hatalar **callback'e** gelir.
- Ayrıca `AuthenticationException` ve `IllegalStateException` (kapatılmış producer'a `send()`)
  gibi durumlar da senkron yüzeye çıkabilir.
- Doğru desen — **iki ayrı hata yolu**:
  ```java
  try {
      producer.send(record, (md, ex) -> { if (ex != null) handleAsyncFailure(record, ex); });
  } catch (SerializationException | RecordTooLargeException e) {
      // kalıcı: kaydı düzelt ya da DLQ'ya al, tekrar deneme
      deadLetterStore.save(record, e);
  } catch (TimeoutException e) {
      // tampon dolu: backpressure. Yavaşla, alarm üret.
      metrics.counter("producer.buffer.full").increment();
      throw e;
  }
  ```

> 📌 **Sık yapılan hata:** Yalnızca callback yazıp `send()`'i `try` bloğuna almamak. Şema
> değişikliği ya da beklenmedik büyük bir mesaj geldiği gün uygulama beklenmedik yerden patlar.

🔗 Konu: [2.3 §4](2.3-teslimat-garantileri.md) · [2.4 §1](2.4-serialization-ve-sema.md)

---

### Soru 5 — "Kafka veri kaybetti" şikâyeti: sırayla bakılacak beş yer

**Kısa cevap ve sıra:**

1. **Producer gerçekten başarı aldı mı?** Callback'te hata var mıydı, hata **loglanıp geçildi
   mi**? `record-error-rate` metriği sıfır mıydı? Kayıpların çoğu burada, "log yazıp geçen"
   callback'te olur.
2. **Dayanıklılık zinciri.** `acks` + `min.insync.replicas` vs `replication.factor` +
   `unclean.leader.election.enable`. Klasik tuzak: `acks=all` ama `min.insync.replicas=1`
   (1.2). Ya da `acks=1`.
3. **Broker tarafında olay var mıydı?** Lider seçimi, unclean seçim, ISR daralması, disk arızası.
   `UnderMinIsrPartitionCount`, `UncleanLeaderElectionsPerSec` metrikleri ve broker log'u.
4. **Retention/compaction veriyi silmiş olabilir mi?** `retention.ms`/`retention.bytes` tüketicinin
   gecikmesinden kısa mı? Compacted topic'te aynı key'e ikinci bir olay mı yazıldı? Tombstone
   `delete.retention.ms` içinde mi kaçırıldı? (1.4)
5. **Consumer kaybetmiş olabilir mi?** İşlemeden **önce** commit ediliyorsa (`enable.auto.commit`
   ile uzun işleme birleşince) veri "işlendi" sanılıp atlanır. Rebalance sırasında yarım kalan
   işler, `auto.offset.reset=latest` ile boş gruptaki ilk okuma. (3.3)

**Ayrıntı — teşhis refleksi:** Önce **katmanı** seç (producer / broker / consumer), sonra ayara
in. Pratik bir eleme sorusu: *"Kayıp kayıtlar Kafka'ya hiç ulaşmadı mı, ulaştı ve silindi mi,
yoksa ulaştı ve okunmadı mı?"* Bunu ayırt etmek için ilgili offset aralığını
`kafka-console-consumer.sh --from-beginning --partition N --offset X` ile **doğrudan oku**;
kayıt orada duruyorsa sorun consumer tarafındadır ve arama alanı üçte bire iner.

> 📌 **Sık yapılan hata:** İncelemeye consumer'dan başlamak. İstatistiksel olarak en sık sebep
> **callback'te yutulan producer hatası** ve **`min.insync.replicas=1`**'dir.

🔗 Konu: [2.3 §3–4](2.3-teslimat-garantileri.md) · [5.3 Arıza senaryoları](../05-operasyon/5.3-ariza-senaryolari.md)

---

## 2.4 Serialization ve şema

📄 Sorular: [`2.4-serialization-ve-sema.md`](2.4-serialization-ve-sema.md)

### Soru 1 — Varsayılanı olmayan zorunlu alan eklemek: hangi uyumluluk kırılır, dağıtım sırası?

**Kısa cevap:** **BACKWARD uyumluluk kırılır** (Schema Registry'nin varsayılan kuralı budur, yani
kayıt reddedilir). FORWARD uyumluluk korunur. Dolayısıyla dağıtım sırası **önce consumer, sonra
producer** olmalıdır.

**Ayrıntı:**

| Değişiklik | BACKWARD | FORWARD |
|---|---|---|
| **Varsayılanı olan** alan eklemek | ✅ | ✅ |
| Varsayılanı **olmayan** alan eklemek | ❌ | ✅ |
| **Varsayılanı olan** alanı silmek | ✅ | ✅ |
| Varsayılanı olmayan alanı silmek | ✅ | ❌ |
| Alan **tipini** değiştirmek | ❌ | ❌ |
| Alan **adını** değiştirmek | ❌ (alias yoksa) | ❌ |

- **Neden BACKWARD kırılır:** BACKWARD = "yeni şemayla yazılan veriyi **eski** okuyucu okuyabilir
  mi?". Eski okuyucu yeni alanı tanımaz — bunu atlayabilir. Sorun tersinde: eski okuyucu, yeni
  şemanın *zorunlu* alanını doldurmak zorunda değildir ama **yeni şemayla yazılmış** kaydı eski
  şemaya göre çözmeye çalıştığında varsayılan bulamaz. Avro'da bir alanın varsayılanı yoksa,
  o alanı içermeyen bir okuma şeması ile **çözümleme yapılamaz**.
- **Doğru dağıtım sırası (FORWARD senaryosu):** Önce **tüm consumer'lar** yeni şemayı bilecek
  şekilde güncellenir (yeni alanı okuyabilirler), *sonra* producer yeni şemaya geçer.
- **Daha iyi çözüm:** Alanı **varsayılanla** ekle (`"default": null` ile nullable yap). O zaman
  hem BACKWARD hem FORWARD sağlanır, dağıtım sırası **serbest** olur. Zorunluluk uygulama
  seviyesinde doğrulanır.

> ⭐ **Tek cümlelik kural:** *Yeni alanlar her zaman varsayılanla eklenir; alan tipi ve adı asla
> değişmez.* Bu iki kurala uyarsan uyumluluk sorunlarının %90'ı hiç doğmaz.

> 📌 **Sık yapılan hata:** Uyumluluk kuralını `NONE` yapıp sorunu "çözmek". Registry susar,
> problem **üretime taşınır**.

🔗 Konu: [2.4 §4](2.4-serialization-ve-sema.md)

---

### Soru 2 — Deserializer hatası consumer'ı neden kilitler? Offset neden ilerlemez?

**Kısa cevap:** Deserializer **`poll()` içinde**, kayıtlar uygulamaya verilmeden **önce** çalışır.
Bir kayıt çözülemezse `poll()` `SerializationException` fırlatır ve **hiçbir kayıt dönmez** —
dolayısıyla uygulama işleyip commit edecek bir şey bulamaz, consumer'ın konumu (`position`)
o zehirli kaydın **üzerinde takılı kalır**. Sonraki `poll()` aynı kayda çarpar. Sonsuz döngü.

**Ayrıntı — lab kanıtı:**
```
offset=0 → OrderEvent[orderId=1001, ...]                  ← v1 okundu
✗ HATA: Error deserializing VALUE for partition lab-schema-0 at offset 1.
        If needed, please seek past the record to continue consumption.
  → position=1 (ilerlemedi; zehirli kayıt tüketiciyi kilitler)
```
`position=1`. Consumer **ilerlemedi**. Kafka'nın hata mesajı bile çözümü söylüyor:
*"seek past the record"*.

**Neden Kafka otomatik atlamıyor?** Çünkü atlamak **veri kaybıdır** ve bu kararı yalnızca uygulama
verebilir. Belki o kayıt kritiktir ve akış durmalıdır; belki atlanabilir. Kafka sessizce
seçim yapmaz.

**Çözüm stratejileri:**

| Strateji | Nasıl | Ne zaman |
|---|---|---|
| Toleranslı deserializer | Bilinmeyen alan/sürümü yut, varsayılan doldur | ✅ İlk savunma hattı |
| Sarmalayıcı deserializer | `try/catch` içine al, hatada `null`/"hatalı kayıt" nesnesi dön | ✅ En esnek |
| DLQ'ya taşı + `seek` | Offset'i ölü mektup topic'ine yaz, `consumer.seek(tp, offset+1)` | ✅ Üretim standardı |
| `ByteArrayDeserializer` + elle çözümleme | Deserialization'ı poll döngüsünün dışına al | ✅ Tam kontrol |
| `catch (Exception) { return null; }` | Sessizce yut | ❌ Veriyi görünmez kaybedersin |

**Spring Kafka kullanıyorsan:** `ErrorHandlingDeserializer` sarmalayıcı stratejisini uygular;
hatayı kayıt başlığına koyar ve `DefaultErrorHandler` + `DeadLetterPublishingRecoverer` ile
DLQ'ya yönlendirir.

> 📌 **Sık yapılan hata:** Zehirli kaydı üretimde `--reset-offsets` ile elle atlamak ve kök
> sebebi araştırmamak. Aynı üretici bir daha bozuk kayıt yazacaktır.

🔗 Konu: [2.4 §1, §5–6](2.4-serialization-ve-sema.md)

---

### Soru 3 — Toleranslı deserializer'ı katı olandan ayıran iki davranış

**Kısa cevap:**

1. **Bilmediği alanları/kuyruğu atlar** — tanımadığı sürüm numarası ya da gövdenin sonundaki
   fazla baytlar karşısında patlamaz, okuyabildiği kadarını okur.
2. **Eksik alanları varsayılanla doldurur** — eski sürümle yazılmış bir kayıtta olmayan alan için
   hata vermek yerine tanımlı bir varsayılan (`null`, `"TRY"`, `0`) koyar.

**Ayrıntı — lab kanıtı.** Aynı topic'e v1, v2, v3 yazıldı ve iki tüketiciyle okundu.

**Toleranslı tüketici** hepsini okudu:
```
offset=0 → OrderEvent[orderId=1001, amountCents=25000, currency=TRY,  customerId=null]     ← v1, currency VARSAYILAN
offset=1 → OrderEvent[orderId=1002, amountCents=30000, currency=EUR,  customerId=null]     ← v2
offset=2 → OrderEvent[orderId=1003, amountCents=45000, currency=USD,  customerId=cust-42]  ← v3
```

**Katı tüketici** ilk yeni sürümde durdu:
```
offset=0 → OrderEvent[orderId=1001, ...]
✗ HATA: Error deserializing VALUE ... at offset 1.
  → position=1 (ilerlemedi)
```

**Üçüncü davranış (bonus):** Toleranslı deserializer, çözemediği kaydı **çağırana bildirir**
(null ya da "hatalı kayıt" nesnesi döner) — yutmaz. Bu ayrım kritiktir:
`catch (Exception) { return null; }` **toleranslı değil, körlüktür** — veriyi görünmez şekilde
kaybedersin. Toleranslı olmak, hatayı **görünür ama öldürücü olmayan** hâle getirmektir.

> 📌 **Sık yapılan hata:** Toleransı sınırsız sanmak. Toleranslı deserializer **şema evrimini**
> hoş görür; tamamen bozuk baytları (yanlış topic'e yazılmış veri, kesik mesaj) hoş göremez.
> Onlar için DLQ + `seek` yolu gerekir.

🔗 Konu: [2.4 §5–6](2.4-serialization-ve-sema.md)

---

### Soru 4 — `BACKWARD` ile `BACKWARD_TRANSITIVE` farkı; hangisi ne zaman?

**Kısa cevap:** `BACKWARD` yeni şemayı yalnızca **bir önceki** sürümle karşılaştırır.
`BACKWARD_TRANSITIVE` **tüm geçmiş sürümlerle** karşılaştırır. Uzun ömürlü topic'lerde
(retention uzun, eski veri hâlâ okunabilir) `_TRANSITIVE` doğrudur.

**Ayrıntı — neden fark eder:**

Diyelim `v1 → v2 → v3` evrimi var ve her adım bir önceki ile uyumlu:
- v2, v1'in `email` alanını sildi (varsayılanı vardı → BACKWARD ✅)
- v3, `email` alanını **farklı tiple** geri ekledi (v2 ile karşılaştırıldığında "yeni alan" →
  varsayılanı varsa BACKWARD ✅)

`BACKWARD` kuralı ikisine de izin verir. Ama topic'te **hâlâ v1 ile yazılmış kayıtlar duruyorsa**,
v3 okuyucusu onları okuyamaz — `email` alanının tipi uyuşmaz. `BACKWARD_TRANSITIVE` bu adımı
**baştan reddederdi**.

**Seçim kuralı:**

| Durum | Seçim |
|---|---|
| Retention kısa (ör. 7 gün) ve tüm eski veri tüketilmiş | `BACKWARD` yeterli |
| Uzun retention, compacted topic, tiered storage, replay ihtimali | **`BACKWARD_TRANSITIVE`** |
| Yeni bir okuyucu topic'i **baştan** okuyabilir mi? Evet ise | **`BACKWARD_TRANSITIVE`** |
| Dağıtım sırasını serbest bırakmak istiyorsan | `FULL` / `FULL_TRANSITIVE` |

**Pratik tavsiye:** Compacted bir durum tablosu topic'inde (`__consumer_offsets` benzeri, profil
tabloları) `_TRANSITIVE` neredeyse her zaman doğrudur — çünkü orada "en eski kayıt" hiç
silinmeyebilir.

> 📌 **Sık yapılan hata:** Varsayılan `BACKWARD` ile yıllarca ilerleyip, bir gün topic'i baştan
> okuyan yeni bir tüketici eklendiğinde çözülemeyen kayıtlarla karşılaşmak. O noktada geriye
> dönüş yoktur; şema geçmişi zaten oluşmuştur.

🔗 Konu: [2.4 §4](2.4-serialization-ve-sema.md)

---

### Soru 5 — Şemasız JSON'da alan tipi değişikliği neden aylar sonra fark edilir?

**Kısa cevap:** Çünkü **hiçbir yerde kontrol yoktur**. Producer istediği JSON'u yazar, broker
baytları sorgulamaz, consumer ise değişikliği ancak o alanı **gerçekten kullandığı kod yolu
çalıştığında** fark eder. O kod yolu nadir bir dalsa (hata durumu, aylık rapor, belirli bir
müşteri tipi), fark etme süresi aylara çıkar.

**Ayrıntı — neden bu kadar sinsi:**

1. **Broker hiçbir doğrulama yapmaz.** Kafka için değer sadece bayt dizisidir. Tip değişikliği
   Kafka katmanında **görünmez**.
2. **JSON okuyucuları sessizce esner.** `"amount": 100` → `"amount": "100"` değişimi çoğu JSON
   kütüphanesinde otomatik dönüşümle geçer; ya da `null` olur ve alan varsayılana düşer.
   Uygulama patlamaz — **yanlış hesaplar**.
3. **Etki eski veride görünmez.** Yalnızca değişiklikten sonraki kayıtlar etkilenir; eski veriyle
   yapılan testler geçmeye devam eder.
4. **Sahibi belirsizdir.** Producer takımı "biz sadece bir alan tipini düzelttik" der; tüketen
   takım o şemayı hiç görmemiştir. Şemasız dünyada **sözleşme yoktur**, dolayısıyla ihlal de
   yoktur.
5. **Fark ediliş biçimi genelde mutabakattır:** ay sonu raporu tutmaz, toplam yanlış çıkar,
   geriye doğru araştırma başlar.

**Çözüm:** Şema kayıtlı bir format (Avro/Protobuf/JSON Schema) + Registry + uyumluluk kuralı.
O zaman tip değiştiren producer **kaydı yayınlayamaz** — hata, üretimden **önce** ve **doğru
takımda** çıkar.

**Ara çözüm (Registry yoksa):** Wire format'ın başına bir **sürüm baytı** koy (bu setteki lab
codec'inin yaptığı gibi: `0x00 | version (1B) | gövde`) ve tüketicide bilinmeyen sürümü açıkça
reddet/DLQ'ya al. Merkezî yönetim olmaz ama **sessiz bozulma** biter.

> 📌 **Sık yapılan hata:** "Küçük ekibiz, şemaya gerek yok" demek. Şemasız JSON en hızlı
> başlangıç, en pahalı devamdır. Ekip büyüdüğünde geriye dönük şema eklemek, üretimdeki tüm
> geçmiş veriyi kapsayacağı için çok daha zordur.

🔗 Konu: [2.4 §2–3](2.4-serialization-ve-sema.md)

---

⬅️ [Bölüme dön](2.1-accumulator-ve-batching.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
