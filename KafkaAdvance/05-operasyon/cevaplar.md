# 05 · Operasyon — Kendini kontrol cevapları

> Bu dosya [5.1](5.1-performans-tuning.md) – [5.5](5.5-kapasite-ve-partition-tasarimi.md)
> konularının sonundaki **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [5.1](#51-performans-tuning) · [5.2](#52-metrikler-ve-izleme) ·
[5.3](#53-arıza-senaryoları) · [5.4](#54-güvenlik) · [5.5](#55-kapasite-ve-partition-tasarımı)

---

## 5.1 Performans tuning

📄 Sorular: [`5.1-performans-tuning.md`](5.1-performans-tuning.md)

### Soru 1 — `linger.ms` 0 → 50: gecikmeyi ne zaman düşürür, ne zaman artırır?

**Kısa cevap:**
- **Düşürür — sistem doygunken.** `linger.ms=0` ile producer her kaydı ayrı istekte göndermeye
  çalışır; broker'a giden istek sayısı patlar, istekler broker kuyruğunda bekler ve **gecikme
  kuyruktan gelir**. Batch'leyince istek sayısı düşer, kuyruk erir, gecikme düşer.
- **Artırır — sistem boşken.** Trafik düşükse batch dolmaz; `linger.ms` süresi dolana kadar
  beklenir ve gecikmeye **doğrudan 50 ms eklenir**.

**Lab kanıtı** (`kafka-producer-perf-test.sh`, 300.000 kayıt × 512 bayt, 6 partition, replication.factor=3,
`acks=all`):

| Yapılandırma | kayıt/sn | MB/sn | ort. gecikme | **p99** |
|---|---|---|---|---|
| `linger.ms=0, batch=16K` | 72.081 | 35,20 | 644 ms | **1201 ms** |
| `linger.ms=50, batch=64K, lz4` | **201.342** | **98,31** | **22,85 ms** | **91 ms** |

**Throughput 2,8 kat arttı ve p99 gecikme 13 kat düştü.** Sezgiye aykırı ama sebebi basit:
küçük ve çok sayıda istek broker kuyruğunu doldurur; gecikmenin kaynağı ağ değil **kuyruktur**.

> **Kural, tek cümlede:** *Doygun sistemde batch'le, boş sistemde batch'leme.*

**Ayrıntı — nasıl karar verirsin:** `batch-size-avg` metriğine bak. `batch.size`'a yakınsa sistem
doygundur ve `linger.ms` zaten beklemiyordur; artırmanın gecikme maliyeti ~0'dır.
`records-per-request-avg` 1-2 civarındaysa sistem boştur ve `linger.ms` doğrudan gecikme ekler.

> 📌 **Sık yapılan hata:** "Düşük gecikme istiyorum, `linger.ms=0` yapayım" refleksi. Yoğun bir
> sistemde bu, gecikmeyi **13 kat artırabilir**.

🔗 Konu: [5.1 §2–3](5.1-performans-tuning.md) · [2.1 §2](../02-producer/2.1-accumulator-ve-batching.md)

---

### Soru 2 — Ortalama 20 ms, p99 900 ms: ne anlama gelir, neye bakarsın?

**Kısa cevap:** İsteklerin %99'u 900 ms'nin altında ama **binde 1'i çok kötü**. Ortalama ile p99
arasındaki 45 kat fark, sistemde **düzensiz bir bekleme kaynağı** olduğunu söyler — sürekli bir
yavaşlık değil, ara ara gelen bir duraklama. Kullanıcı deneyimini belirleyen p99'dur; ortalama
bu sorunu **gizler**.

**Neye bakarsın — sırayla:**

1. **GC duraklamaları.** Producer/broker JVM'inde uzun stop-the-world duraklamaları klasik
   p99 kaynağıdır. GC loglarına ve duraklama süresi dağılımına bak.
2. **`acks=all` + yavaş follower.** Replikasyon, **en yavaş ISR üyesini beklemektir**. Broker'da
   `TotalTimeMs`'i bileşenlerine ayır: `RemoteTimeMs` yüksekse sorun **follower'lardadır**
   (5.2). 2.3'teki ölçümde `acks=all` p99'u diğerlerinin 4 katıydı — bu **beklenen** davranıştır.
3. **Broker istek kuyruğu.** `RequestQueueTimeMs` yüksek, `RequestHandlerAvgIdlePercent` düşükse
   broker doygundur → `num.io.threads`.
4. **Retry'lar.** Bir batch başarısız olup yeniden denendiğinde o kaydın gecikmesi katlanır.
   `record-retry-rate` ve `record-error-rate` metriklerine bak.
5. **Metadata yenileme / lider değişimi.** Lider seçimi sırasında o partition'a yazan kayıtlar
   saniyeler bekleyebilir. `UnderReplicatedPartitions` ve broker log'u.
6. **Disk `fsync`/page cache flush dalgalanması.** `LogFlushRateAndTimeMs`.

**Ayrıntı — ölçüm yöntemi de sorgulanmalı:** Ölçüm **sabit hızda** mı yapıldı? Sınırsız hızda
ölçtüğün "gecikme" büyük ölçüde **kendi kuyruğunda bekleme**dir ve p99 yapay olarak şişer (2.3).
Ayrıca ısınma turu atlandıysa ilk isteklerin JIT ([JVM’in çalışma anı derlemesi ↗](../00-baslangic/02-kavram-sozlugu.md#jit)) + bağlantı kurulum maliyeti p99'a girer.

> 📌 **Sık yapılan hata:** Ortalamaya bakıp "iyi görünüyor" demek. Kafka'da karar **her zaman**
> p99 (ve mümkünse p99.9) ile verilir.

🔗 Konu: [5.1 §1, §6](5.1-performans-tuning.md) · [5.2 §3](5.2-metrikler-ve-izleme.md)

---

### Soru 3 — Consumer lag artıyor: Kafka ayarlarına dokunmadan önce hangi iki şeyi ölçersin?

**Kısa cevap:**

1. **Kayıt başına işleme süresi.** Tüketim darboğazı neredeyse her zaman **senin işleme
   kodundadır**, Kafka'da değil. Lab'da ham tüketim **655.141 kayıt/sn** ölçüldü — üretimden
   3 kat hızlı. Uygulaman saniyede 500 kayıt işliyorsa sorun fetch ayarlarında değildir.
2. **Lag'in partition başına dağılımı.** Toplam lag 50.000 olabilir ama gerçek şu olabilir:
   ```
   p0: 12   p1: 8   p2: 49.960   p3: 15   p4: 5     →  p2 BOĞULMUŞ
   ```
   Tek bir sıcak partition varsa çözüm ayar değil **key tasarımıdır** (5.5).

**Ayrıntı — bu iki ölçüm neyi eler:**

| Ölçüm sonucu | Sonuç |
|---|---|
| İşleme süresi yüksek, lag dengeli | Uygulama darboğazı → paralellik, batch DB yazımı, I/O optimizasyonu |
| İşleme hızlı, tek partition'da lag | **Key skew** → key tasarımı (5.5) |
| İşleme hızlı, lag her partition'da | Gerçek kapasite sorunu → partition + consumer artır |
| Lag var ama iş görülmüyor | Rebalance fırtınası (3.2) ya da açık transaction/LSO (4.1) |

**Üçüncü ölçüm (bonus):** `last-poll-seconds-ago` ve `rebalance-rate-per-hour`. Grup sürekli
rebalance oluyorsa lag artışının sebebi işleme hızı değil, **hiç ilerleyememektir**.

**Dördüncü:** Lag'i **zaman cinsinden** ifade et. "50.000 kayıt geride" anlamsızdır;
"12 dakika geride" karar verdirir.

> 📌 **Sık yapılan hata:** Lag görünce doğrudan `fetch.min.bytes`/`max.partition.fetch.bytes`
> kurcalamak. 3.1'de ölçtük: `max.partition.fetch.bytes`'ı 10 katına çıkarmak **hiçbir şey
> değiştirmedi** — darboğaz orada değildi.

🔗 Konu: [5.1 §1, §4](5.1-performans-tuning.md) · [5.2 §2](5.2-metrikler-ve-izleme.md)

---

### Soru 4 — Broker'a 32 GB heap vermenin iki olumsuz sonucu

**Kısa cevap:**

1. **Page cache'i çalarsın.** Kafka veriyi **heap'te tutmaz**; okuma performansı işletim
   sisteminin **page cache**'inden gelir (1.1). RAM'i heap'e verirsen, sıcak veri için kalan
   önbellek küçülür ve okumalar diske iner. Yani "daha çok bellek verdim" hareketi
   **okumaları yavaşlatır**.
2. **GC duraklamaları uzar.** Büyük heap = uzun stop-the-world duraklamaları. Bu duraklamalar
   sırasında broker heartbeat/fetch işleyemez; **ISR'den düşme** (1.2), controller quorum'undan (karar için gereken salt çoğunluk)
   kopma (combined modda, 1.3) ve p99 gecikme sıçramaları gelir.

**Ayrıntı:**

- **Doğru büyüklük:** **6 GB civarı** çoğu üretim broker'ı için yeterlidir. Geri kalan RAM'i
  işletim sistemine bırak — o page cache olarak kullanır ve Kafka'nın `sendfile` yolu oradan
  beslenir.
- Heap'te ne durur? İstek tamponları, index mmap referansları, metadata, bağlantı durumu. Kayıt
  verisi durmaz — batch'ler broker'da **açılmadan** diske yazılır (1.1).
- **Üçüncü olumsuz sonuç:** Sorunun *tanısını* zorlaştırır. Büyük heap ile OOM yerine uzun GC
  görürsün; belirti "ara ara ISR daralıyor"a döner ve kök sebebe ulaşmak zorlaşır.
- İstisna: Çok yüksek partition sayısı ve çok sayıda bağlantı olan cluster'larda heap ihtiyacı
  artar — ama çözüm 32 GB değil, ölçüp kademeli artırmaktır.

> 📌 **Sık yapılan hata:** Bu, Kafka'da en yaygın "iyileştirme" hatasıdır. 1.1'deki kalıp aynen
> geçerli: *"Broker'a 48 GB heap verdik ama hızlanmadı, GC duraklamaları başladı."*

🔗 Konu: [5.1 §5](5.1-performans-tuning.md) · [1.1 §5](../01-broker-depolama/1.1-log-segment-ve-index.md)

---

### Soru 5 — Bir benchmark sonucunu değerlendirirken sorulacak beş soru

**Kısa cevap:**

1. **Tek değişken mi değişti?** Aynı anda iki ayar değiştirildiyse hangisinin işe yaradığı
   bilinemez.
2. **Isınma turu atıldı mı?** İlk koşum JIT derlemesi ve bağlantı kurulumu yüzünden
   dezavantajlıdır (2.1'de bunu gördük).
3. **p50 mi p99 mu raporlanıyor?** Ortalama gecikme kullanıcı deneyimini anlatmaz. Karar
   **p99** ile verilir.
4. **Sabit hızda mı ölçüldü?** Sınırsız hızda ölçülen "gecikme" büyük ölçüde **kuyrukta
   beklemedir** (2.3) ve karşılaştırmayı anlamsızlaştırır.
5. **Gerçek veriyle mi ölçüldü?** `"x".repeat(200)` %96 sıkışır; gerçek JSON %50-80 (2.1).
   Sıkıştırma oranı yanlışsa throughput sonucu da yanlıştır.

**Altıncı soru (en çok atlanan):** **Üretim taklit edildi mi?** replication.factor, `min.insync.replicas`,
partition sayısı, mesaj boyutu, `acks` ve TLS durumu üretimdekiyle **aynı** olmalı. replication.factor=1,
`acks=1`, TLS kapalı bir lab ölçümü üretim için hiçbir şey söylemez.

**Yedinci:** Ölçüm **nerede** yapıldı? Lokal Docker cluster'ında ağ gecikmesi ~0'dır;
`fetch.min.bytes` ve `linger.ms` gibi ayarların gerçek etkisi orada görünmez. Bu setteki tüm
ölçümlerde bu uyarı bilerek tekrarlanır.

> 📌 **Sık yapılan hata:** Bir blog yazısındaki benchmark sayısını kendi ortamına taşımak.
> Yöntem taşınır, **sayı taşınmaz**.

🔗 Konu: [5.1 §6](5.1-performans-tuning.md)

---

## 5.2 Metrikler ve izleme

📄 Sorular: [`5.2-metrikler-ve-izleme.md`](5.2-metrikler-ve-izleme.md)

### Soru 1 — `UnderReplicatedPartitions` vs `UnderMinIsrPartitionCount`: fark ve aciliyet

**Kısa cevap:**
- **`UnderReplicatedPartitions` > 0:** ISR eksik — bir ya da daha fazla replika geride kaldı.
  Yazma **devam ediyor**, veri kaybı riski **arttı**. Aciliyet: **orta** (süreklilik önemli).
- **`UnderMinIsrPartitionCount` > 0:** ISR, `min.insync.replicas`'ın **altına düştü**.
  `acks=all` yazmaları **reddediliyor** — üretim durmuş durumda. Aciliyet: **yüksek, sayfa
  uyandırır**.

**Aciliyet sıralaması (yüksekten düşüğe):**

| Metrik | Anlamı | Aciliyet |
|---|---|---|
| `OfflinePartitionsCount > 0` | Lider yok — veri **erişilemez** | 🔴 En yüksek |
| `ActiveControllerCount` toplamı ≠ 1 | Controller yok ya da split-brain | 🔴 En yüksek |
| `UnderMinIsrPartitionCount > 0` | `acks=all` **yazamıyor** | 🔴 Yüksek |
| `AtMinIsrPartitionCount > 0` | Sınırda; bir kayıp daha yazmayı durdurur | 🟠 Uyarı |
| `UnderReplicatedPartitions > 0` | Kopya eksik ama çalışıyor | 🟠 Orta (süreklilik) |

**Ayrıntı:**

- **Sıralama neden böyle:** `UnderReplicated`, dayanıklılık **rezervinin** azalmasıdır — hâlâ
  çalışıyorsun. `UnderMinIsr`, **iş durmuş** demektir. Offline ise okuma bile yok.
- `UnderReplicated` **anlık** olarak yükselip düşebilir; bu normaldir (broker restart'ı, kısa
  bir GC). Alarm **süreye** bağlanmalı: *"> 0, 5 dakikadır"*.
- İkisi arasındaki ilişki: replication.factor=3 + min.insync.replicas=2 ile bir replika düşerse `UnderReplicated=1`,
  `UnderMinIsr=0` (2 >= 2). İkinci replika da düşerse `UnderMinIsr=1` olur ve yazma durur.

> 📌 **Sık yapılan hata:** Yalnızca `UnderReplicatedPartitions`'a alarm kurup
> `UnderMinIsrPartitionCount`'u atlamak. İlki gürültülüdür ve alarm yorgunluğu yaratır;
> **asıl uyandıran** ikincisi olmalıdır.

🔗 Konu: [5.2 §1](5.2-metrikler-ve-izleme.md) · [1.2 §3](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

---

### Soru 2 — Toplam lag 50.000: neden tek başına bir şey ifade etmez?

**Kısa cevap:** Çünkü toplam (ve ortalama) **dağılımı gizler**. 50.000 lag beş partition'a eşit
dağılmış olabilir (her biri 10.000 — dengeli bir gecikme) ya da tek bir partition'da toplanmış
olabilir:
```
p0: 12   p1: 8   p2: 49.960   p3: 15   p4: 5     →  p2 BOĞULMUŞ
```
İkinci durumda dört partition sağlıklı, biri tamamen tıkanmış. Toplam ikisinde de aynı.

**Ne izlersin:**

| Ne | Neden |
|---|---|
| **Partition başına maksimum lag** | Tek bir sıcak/tıkalı partition'ı yakalar |
| **Lag'in türevi (artıyor mu?)** | Sabit 10.000 lag, **artan** 500 lag'den iyidir |
| **Zaman cinsinden lag** | "50.000 kayıt" anlamsız; **"12 dakika geride"** karar verdirir |
| Consumer instance başına lag | Hangi pod takılmış |
| `records-lag-max` (client) | Uygulamanın kendi en kötü partition'ı |

**Ayrıntı — "zaman cinsinden lag" nasıl hesaplanır:** Partition'ın son kaydının timestamp'i ile
consumer'ın bulunduğu offset'in timestamp'i arasındaki fark. Kafka bunu doğrudan vermez;
`offsetsForTimes()` ya da lag exporter'ları hesaplar. Aynı 50.000 kayıt, yoğun bir topic'te
2 saniye, seyrek bir topic'te 2 saat olabilir.

**Bonus tuzak:** Compacted topic'lerde ve transaction'lı topic'lerde lag **olduğundan büyük**
görünür — compaction boşlukları (1.4) ve control record'lar (4.1) offset tüketir ama consumer'a
kayıt olarak gelmez.

> 📌 **Sık yapılan hata:** "Lag > 1000" gibi sabit eşikli bir alarm kurmak. İyi alarm şudur:
> *"Partition başına maksimum lag 10 dakikadır artıyor."*

🔗 Konu: [5.2 §2, §5](5.2-metrikler-ve-izleme.md)

---

### Soru 3 — `ActiveControllerCount` üç broker'da 0, 1, 0: sağlıklı mı?

**Kısa cevap:** **Evet, tamamen sağlıklı.** Bu metrik **broker başına** okunur ve controller
olmayan node'larda **0** döner. Sağlık kuralı **cluster genelindeki toplamın 1 olmasıdır**.
0+1+0 = 1 → doğru.

**Ayrıntı:**

| Toplam | Anlamı |
|---|---|
| **1** | ✅ Sağlıklı — tek aktif controller |
| **0** | 🔴 Controller yok — quorum kaybı (1.3), metadata işlemleri durur |
| **>1** | 🔴 Split-brain — iki node kendini controller sanıyor |

- Lab'da `kafka-1` için okuduğumuz değer **0**'dı ve bu normaldir — o an controller lideri başka
  bir node'du:
  ```
  kafka.controller:...,name=ActiveControllerCount:Value               0   ← bu broker controller DEĞİL
  ```
- **Alarm nasıl kurulur:** Metrikleri broker'lar üzerinden **toplayıp** (`sum`) 1'e eşit
  olmadığında alarm ver. Tek broker üzerinden alarm kurarsan, controller her taşındığında yanlış
  alarm üretirsin.
- **KRaft ile ilişkisi:** Aktif controller = Raft quorum'unun **lideri**. Ölen bir controller
  lideri geri geldiğinde liderlik **ona geri dönmez** — Raft'ta "preferred leader" kavramı yoktur
  (1.3). Yani hangi node'un 1 gösterdiği zaman içinde değişir; bu da normaldir.

> 📌 **Sık yapılan hata:** Bir broker'ın panosuna bakıp "controller yok!" diye alarm kurmak.
> Bu metriğin doğru okunuşu **cluster toplamıdır**.

🔗 Konu: [5.2 §1](5.2-metrikler-ve-izleme.md) · [1.3 §6](../01-broker-depolama/1.3-kraft-metadata.md)

---

### Soru 4 — Produce gecikmesi `RemoteTimeMs`'ten geliyor: nereye bakarsın?

**Kısa cevap:** **Follower'lara.** `RemoteTimeMs`, `acks=all` yazmalarında **replikasyonun
tamamlanmasını bekleme** süresidir. Yüksekse sorun producer'da ya da liderin diskinde değil,
**takipçi replikalarda**dır.

**Nereye bakarsın — sırayla:**

1. **`UnderReplicatedPartitions` / `ReplicaFetcherManager MaxLag`** — hangi broker geride?
2. **O broker'ın diski.** `LogFlushRateAndTimeMs`, işletim sistemi tarafında `await`/IOPS.
   Yavaş ya da doygun disk en yaygın sebeptir (1.2).
3. **`num.replica.fetchers`** (varsayılan **1**). Tek fetcher thread'i onlarca partition'ı
   çekiyorsa yetişemez. Yüksek trafikli cluster'da artırmak klasik çözümdür.
4. **Ağ.** Broker'lar arası bant genişliği doygun mu? Rack/AZ'ler arası trafik mi?
5. **GC.** Follower broker'da uzun duraklamalar fetch'i geciktirir.
6. **`min.insync.replicas` ve ISR boyutu.** ISR'de yavaş bir üye varsa `acks=all` **onu
   bekler** — HW en yavaş replikanın seviyesidir (1.2).

**Ayrıntı — `TotalTimeMs`'in bileşenleri teşhisi tek başına bitirir:**

| Bileşen | Yüksekse |
|---|---|
| `RequestQueueTimeMs` | İstek kuyruğu dolu → `num.io.threads` |
| `LocalTimeMs` | **Liderin** diske yazması yavaş |
| `RemoteTimeMs` | **Replikasyon bekleniyor** → follower'lar |
| `ResponseQueueTimeMs` / `ResponseSendTimeMs` | Ağ / cevap gönderimi |

> ⭐ **Teşhis kısayolu:** `acks=all` yazma gecikmesinin büyük kısmı `RemoteTimeMs` ise sorun
> **producer'da değil follower'lardadır.** Producer ayarlarını kurcalamayı bırak.

> 📌 **Sık yapılan hata:** Yüksek produce gecikmesi görünce `linger.ms`/`batch.size` ile
> oynamak. Gecikmenin **hangi aşamada** oluştuğunu ayırmadan ayar değiştirmek kör atıştır.

🔗 Konu: [5.2 §3](5.2-metrikler-ve-izleme.md) · [1.2 §1–2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

---

### Soru 5 — Rebalance fırtınasını önceden haber verecek metrik ve eşiği

**Kısa cevap:** **`last-poll-seconds-ago`** (consumer client metriği). Eşik:
**`max.poll.interval.ms`'in %70'i** — varsayılan 300 s ile **210 saniye**. Bu metrik, consumer
henüz atılmadan, `max.poll.interval.ms`'e **yaklaştığını** gösterir. Yani fırtına **olmadan
önce** uyarır.

**Ayrıntı:**

- Diğer rebalance metrikleri (`rebalance-rate-per-hour`, `rebalance-latency-avg`) fırtına
  **başladıktan sonra** yükselir — teşhis için iyi, **önleme** için geç.
- `last-poll-seconds-ago` yükseliyorsa sebep şudur:
  `max.poll.records × kayıt başına işleme süresi` `max.poll.interval.ms`'e yaklaşıyor (3.1).
  Çözüm de oradadır: `max.poll.records`'ı düşür.
- **İkinci erken uyarı metriği:** `rebalance-rate-per-hour`. Eşik: **saatte 10'dan fazla**
  rebalance. Bu, işleme süresinden değil pod restart döngüsü/OOMKilled ([konteyner bellek limitini aştı ↗](../00-baslangic/02-kavram-sozlugu.md#oom))/GC'den gelen fırtınaları
  yakalar.
- **Üçüncü:** `commit-latency-avg` yükselişi — coordinator sorunlarının erken işareti.

**Alarm nasıl yazılır (iyi ve kötü):**

| ❌ Kötü | ✅ İyi |
|---|---|
| "Rebalance oldu" | "Saatte 10'dan fazla rebalance" |
| "Lag > 1000" | "`last-poll-seconds-ago` > 210, 2 dakikadır" |

> **Hafıza kancası:** Kafka'da neredeyse hiçbir **tekil olay** alarm değildir; **süreklilik**
> alarmdır. ISR bir kez daralır ve düzelir — normaldir. 5 dakika daralmış kalırsa anormaldir.

> 📌 **Sık yapılan hata:** `last-poll-seconds-ago`'yu hiç dışa vurmamak. En az bilinen ama en
> faydalı consumer metriğidir; uygulamandan mutlaka dışarı ver.

🔗 Konu: [5.2 §2, §5–6](5.2-metrikler-ve-izleme.md) · [3.2 §6](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

---

## 5.3 Arıza senaryoları

📄 Sorular: [`5.3-ariza-senaryolari.md`](5.3-ariza-senaryolari.md)

### Soru 1 — `Leader: none` gördün: üç adım? Hangisini asla ilk yapmazsın?

**Kısa cevap — üç adım:**

1. **Kapsamı belirle.** Hangi partition'lar etkilenmiş, replikaları nerede:
   ```bash
   kafka-topics.sh --bootstrap-server kafka-1:19092 --describe --unavailable-partitions
   ```
2. **Eksik broker'ı geri getir.** Lider dönünce partition **kendiliğinden** açılır. Metadata
   quorum'unu da kontrol et (`kafka-metadata-quorum.sh describe --status`) — controller
   sağlıklı değilse lider seçimi zaten yapılamaz (1.3).
3. **Geri gelmiyorsa veriyi değerlendir.** `Elr` / `LastKnownElr` kolonlarına bak: ELR doluysa
   controller güvenli bir aday biliyordur. Broker'ın diski kurtarılabilir mi? Kurtarılamıyorsa
   ancak o zaman son çare konuşulur.

**Asla ilk yapmayacağın şey: `unclean.leader.election.enable=true`.** Bu ayar bir "kurtarma"
değil, **commit edilmiş veriyi silmeyi kabul etmektir** (1.2). Geride kalmış bir replika lider
olur, log'un sonu geriye kayar, consumer'lar `OffsetOutOfRange` alır ve
`auto.offset.reset`'e düşer.

**Ayrıntı — gerçek lab çıktısı** (replication.factor=1 topic, o replikanın broker'ı durduruldu):
```
Topic: lab-rf1  Partition: 1  Leader: none  Replicas: 3  Isr:    Elr: 3  LastKnownElr: 3
OfflinePartitionsCount = 1
```
`Leader: none`, `Isr:` boş, ama `Elr: 3` — ELR "veri hâlâ 3'te güvenli" diyor.

**Kalıcı düzeltme:** replication.factor=1 topic bırakma. Ayrıca lab'da yakalanan ikinci hata:
**replication.factor=1 topic broker varsayılanı `min.insync.replicas=2`'yi miras aldı.** Bu topic
**sağlıklıyken bile** `acks=all` ile yazılamaz — `replication.factor < min.insync.replicas` **kalıcı bir
yapılandırma hatasıdır**.

> 📌 **Sık yapılan hata:** Panikle unclean seçimi açmak. Partition açılır, sistem "düzelmiş"
> görünür ve veri kaybı **aylar sonra** mutabakatta ortaya çıkar.

🔗 Konu: [5.3 §2](5.3-ariza-senaryolari.md) · [1.2 §4](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

---

### Soru 2 — Disk %97: kurtarma adımları

**Kısa cevap — sırayla:**

1. **Log dosyalarını elle SİLME.** Broker'ın durumu bozulur, veri kaybı ve açılamayan broker
   riski doğar. (Bu adım "ne yapmayacağın" olduğu için birincidir.)
2. **En büyük topic'lerde retention'ı düşür:**
   ```bash
   kafka-configs.sh --bootstrap-server kafka-1:19092 --entity-type topics --entity-name buyuk-topic \
     --alter --add-config retention.ms=3600000,segment.ms=60000
   ```
   `segment.ms`'i de düşürmek kritiktir: **aktif segment silinmez** (1.1), segmentlerin dönmesi
   gerekir ki silinebilsinler.
3. **Bekle.** Retention thread'i `log.retention.check.interval.ms` (**5 dakika**) periyodunda
   çalışır. Anında olmaz.
4. **Yetmezse: gereksiz topic'leri sil** ya da belirli offset öncesini buda:
   ```bash
   echo '{"partitions":[{"topic":"buyuk-topic","partition":0,"offset":1000000}],"version":1}' > /tmp/d.json
   kafka-delete-records.sh --bootstrap-server kafka-1:19092 --offset-json-file /tmp/d.json
   ```
5. **Kalıcı çözüm:** Disk alarmını **%75**'te kur (dolan disk broker'ı durdurur), kapasite
   planını gözden geçir (5.5), uzun saklama için tiered storage'ı değerlendir (1.5).

**Ayrıntı:** Belirti genelde broker'ın açılmaması ya da kapanmasıdır; log'da:
```
Error while writing meta.properties file /var/lib/kafka/data: java.io.IOException: No space left on device
```
Bu setin kendi ortamı kurulurken de tam olarak bu hatayla karşılaşıldı — ve çözüm **kullanıcıya
sorulup onaylanan** sınırlı bir temizlikle sağlandı, körlemesine silmeyle değil.

**Broker hiç açılmıyorsa:** Önce disk üzerinde **Kafka'ya ait olmayan** alanı temizle (eski log
dosyaları, docker build cache, geçici dosyalar). Broker'ı ayağa kaldırmak, retention ayarını
uygulayabilmenin ön şartıdır.

> 📌 **Sık yapılan hata:** `rm` ile en eski segment dosyalarını silmek. Broker açılışta index ve
> log tutarlılığını doğrular; elle silinen dosyalar `.snapshot` ve `leader-epoch-checkpoint`
> ile tutarsızlaşır.

🔗 Konu: [5.3 §3](5.3-ariza-senaryolari.md) · [1.4 §2](../01-broker-depolama/1.4-retention-ve-compaction.md)

---

### Soru 3 — Grup `PreparingRebalance`'ta takılı: ilk bakılacak iki ayar

**Kısa cevap:**

1. **`max.poll.interval.ms`** — ve onunla birlikte `max.poll.records`. En yaygın sebep budur:
   bir üye işlemede takılıyor, `max.poll.interval.ms`'i aşıyor, atılıyor, rebalance başlıyor;
   yeni üye aynı batch'i alıyor, o da takılıyor. Grup **sonsuz** `PreparingRebalance`'ta kalıyor.
2. **`session.timeout.ms`** — üyeler GC/CPU limiti/ağ yüzünden heartbeat kaçırıyorsa. Klasik
   protokolde client'ta, yeni protokolde broker'da (`group.consumer.session.timeout.ms`).

**Ayrıntı — teşhis:**
```bash
kafka-consumer-groups.sh --bootstrap-server kafka-1:19092 --describe --group g --state
# STATE: PreparingRebalance / CompletingRebalance'ta takılıysa → fırtına
```

| Sebep | Kanıt | Çözüm |
|---|---|---|
| İşleme çok uzun | `last-poll-seconds-ago` yüksek, `CommitFailedException` | `max.poll.records` düşür (3.1) |
| Pod restart döngüsü | K8s event'leri, OOMKilled | Bellek limiti; `group.instance.id` (3.2) |
| GC duraklaması | GC logları, `session.timeout.ms` aşımı | Heap ayarı |
| Eager assignor + rolling deploy | Tüm atamalar geri alınıyor | `CooperativeStickyAssignor` ya da `group.protocol=consumer` |
| **Assignor uyuşmazlığı** | Grup **hiç kurulmuyor** | Tüm client'larda aynı strateji |

**Son satır özel bir vakadır:** Klasik protokolde üyeler ortak bir assignor üzerinde
anlaşamazsa grup hiç oluşmaz ve `PreparingRebalance`'ta sonsuza kadar kalır. Belirti aynı,
sebep tamamen farklı — bu yüzden **üyelerin client sürümlerini ve
`partition.assignment.strategy` değerlerini** de kontrol et.

> 📌 **Sık yapılan hata:** Doğrudan `session.timeout.ms`'i büyütmek. Sebep işleme süresiyse
> hiçbir şey değişmez; sadece gerçek arıza tespiti yavaşlar.

🔗 Konu: [5.3 §4](5.3-ariza-senaryolari.md) · [3.2 §3, §6](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 4 — `read_committed` tüketici ilerlemiyor, broker sağlıklı, rebalance yok

**Kısa cevap — hipotez:** O partition'da **açık (commit/abort edilmemiş) bir transaction** var
ve tüketici **LSO'da** (Last Stable Offset) bekliyor. `read_committed` bir tüketici, açık ilk
transaction'ın başlangıcının ötesini **göremez** — sonraki kayıtlar commit edilmiş olsa bile.

**Nasıl doğrularsın:**

1. **Aynı topic'i `read_uncommitted` ile oku.** Kayıtlar geliyorsa hipotez doğrulandı:
   veri var, `read_committed` görmüyor.
   ```bash
   kafka-console-consumer.sh --bootstrap-server kafka-1:19092 --topic T \
     --partition 0 --offset <lag_baslangici> --max-messages 5
   ```
2. **Açık transaction'ları listele:**
   ```bash
   kafka-transactions.sh --bootstrap-server kafka-1:19092 list
   kafka-transactions.sh --bootstrap-server kafka-1:19092 describe --transactional-id X
   ```
   `Ongoing` durumda ve uzun süredir açık bir transaction ararsın.
3. **Log'u doğrudan incele:** `kafka-dump-log.sh --files ... --print-data-log` çıktısında
   `isTransactional: true` batch'lerden sonra `endTxnMarker` **gelmemişse** transaction açıktır
   (4.1).

**Ayrıntı — sebepler:**
- Uzun süren bir transaction (büyük batch, yavaş aşağı akış, kodda unutulmuş commit).
- Çökmüş bir transactional producer: coordinator `transaction.timeout.ms` (**60 s**) dolana
  kadar otomatik abort etmez.
- Fence'lenmesi gereken bir instance'ın hâlâ açık transaction tutması.

**Çözüm:** Timeout'un dolmasını bekle; acilse `kafka-transactions.sh --abort` ile sonlandır.
Kalıcı çözüm: transaction granülaritesini küçült (batch başına bir transaction) ve
`transaction.timeout.ms`'i gereksiz büyük tutma.

**Alternatif hipotezler (elemek için):** Zehirli kayıt (`SerializationException`, `position`
sabit, 2.4) ve partition'ın hiç atanmamış olması (`--describe` çıktısında `CONSUMER-ID: -`,
consumer sayısı > partition sayısı).

> 📌 **Sık yapılan hata:** Bu vakada fetch/poll ayarlarını kurcalamak. Sorun tüketicide değil,
> **partition'da açık duran bir transaction'da**dır — ve hiçbir metrik hata göstermez.

🔗 Konu: [5.3 §5](5.3-ariza-senaryolari.md) · [4.1 §5](../04-eos-transaction/4.1-transactions-internals.md)

---

### Soru 5 — replication.factor=1 + `min.insync.replicas=2` + `acks=all`: ne olur, neden?

**Kısa cevap:** Yazma **her zaman reddedilir** — `NotEnoughReplicasException`. Çünkü ISR en fazla
1 olabilir (tek replika var) ve `1 < 2` şartı **hiçbir zaman** sağlanamaz. Cluster tamamen
sağlıklıyken bile bu topic'e `acks=all` ile yazılamaz.

**Ayrıntı:**

- Bu **kalıcı bir yapılandırma hatasıdır**, geçici bir arıza değil. Kural:
  **`min.insync.replicas` her zaman `replication.factor`'dan küçük olmalıdır** (eşit olması bile
  riskli: replication.factor=2 + min.insync.replicas=2 ile her bakım yazmayı durdurur).
- **Nasıl oluşur:** Topic replication.factor=1 olarak oluşturulur (test, hızlı bir deneme, otomasyon hatası) ve
  broker varsayılanı `min.insync.replicas=2`'yi **miras alır**. Kimse topic seviyesinde ayar
  yapmadığı için hata görünmez — ta ki `acks=all` bir producer yazana kadar.
- Bu setin lab cluster'ında `min.insync.replicas=2` bilerek broker varsayılanı yapılmıştır ve
  5.3 pratiğinde bu tuzak **gerçekten yakalanmıştır**.
- **Client'ın gördüğü hata** yine `TimeoutException` olabilir: `NotEnoughReplicasException`
  yeniden denenebilir bir hatadır, retry'lar gerçek sebebi maskeler (1.2). Broker log'una bakmak
  şarttır.
- **Düzeltme:** Ya topic'in replication.factor'sini artır (`kafka-reassign-partitions.sh` ile replika ekle),
  ya da o topic için `min.insync.replicas=1` ayarla — ama ikincisi replication.factor=1 olduğu için zaten hiçbir
  dayanıklılık garantisi vermez. **Doğru çözüm replication.factor=3'tür.**

**Kontrol komutu:**
```bash
kafka-topics.sh --bootstrap-server kafka-1:19092 --describe | \
  grep -E "ReplicationFactor: 1|min.insync.replicas"
```

> 📌 **Sık yapılan hata:** replication.factor=1 topic'leri "geçici, test için" diye bırakmak. Üretim
> cluster'ında replication.factor=1 bir topic hem bu hatayı hem de kalıcı offline riski (Soru 1) taşır.

🔗 Konu: [5.3 §2](5.3-ariza-senaryolari.md) · [1.2 §3](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

---

## 5.4 Güvenlik

📄 Sorular: [`5.4-guvenlik.md`](5.4-guvenlik.md)

### Soru 1 — Bir tüketicinin ihtiyaç duyduğu tüm ACL'ler; en sık unutulan?

**Kısa cevap — minimum set:**
- **`Topic:Read`** — kayıtları okumak için
- **`Topic:Describe`** — metadata (partition listesi, offset'ler) almak için
- **`Group:Read`** — ⭐ **en sık unutulan** — consumer group'a katılmak ve offset commit etmek için

**Ayrıntı — lab kanıtı.** alice'e topic ACL'leri verildi ama grup ACL'i verilmedi:
```
org.apache.kafka.common.errors.GroupAuthorizationException: Not authorized to access group: g-alice
```
Hata **`TopicAuthorizationException` değil `GroupAuthorizationException`**'dır. Bu ayrımı bilmek
teşhis süresini dakikalara indirir: hata mesajı sana hangi kaynağın eksik olduğunu **doğrudan**
söylüyor.

**Senaryoya göre ek ACL'ler:**

| Ek ihtiyaç | Gereken ACL |
|---|---|
| Transaction'lı tüketim-üretim (EOS, 4.1) | `TransactionalId:Write` + `TransactionalId:Describe` |
| Kendi topic'ini oluşturan uygulama | `Topic:Create` ya da `Cluster:Create` |
| Idempotent producer (4.x'te varsayılan açık) | `Cluster:IdempotentWrite` (yeni sürümlerde `Topic:Write` yeterli olabilir — sürümünü doğrula) |
| Offset sıfırlama / grup silme (operasyon) | `Group:Delete`, `Group:Describe` |
| Topic ayarı okuma | `Topic:DescribeConfigs` |

**Bir üreticinin minimum seti:** `Topic:Write` + `Topic:Describe` (transactional ise ayrıca
`TransactionalId:Write`).

**ACL verme örneği:**
```bash
kafka-acls.sh --bootstrap-server ... --command-config admin.properties \
  --add --allow-principal User:alice \
  --operation Read --operation Describe --topic guvenli-topic
kafka-acls.sh --bootstrap-server ... --command-config admin.properties \
  --add --allow-principal User:alice --operation Read --group g-alice
```

> 📌 **Sık yapılan hata:** ACL'leri yalnızca topic düzeyinde düşünmek. Kafka'da **Group** ve
> **TransactionalId** de birer kaynak tipidir.

🔗 Konu: [5.4 §2–3](5.4-guvenlik.md)

---

### Soru 2 — `SASL_PLAINTEXT` + `PLAIN` neden üretim için uygun değil?

**Kısa cevap:** Çünkü ikisi bir arada **parolayı ağda düz metin taşır**. `SASL_PLAINTEXT`
şifreleme sağlamaz (adındaki "PLAINTEXT" taşıma katmanını anlatır) ve `PLAIN` mekanizması
kullanıcı adı/parolayı olduğu gibi gönderir. Ağı dinleyen biri kimlik bilgilerini **doğrudan**
okur.

**Ayrıntı — iki boyut ayrı ayrı:**

| | Kimlik doğrulama | Şifreleme |
|---|---|---|
| `PLAINTEXT` | Yok | Yok |
| `SSL` | mTLS ile (opsiyonel) | **Var** |
| `SASL_PLAINTEXT` | **Var** | **Yok** ⚠️ |
| `SASL_SSL` | **Var** | **Var** ✅ Üretim standardı |

- **Sadece protokol değil, mekanizma da önemli.** `SASL_PLAINTEXT` + `SCRAM-SHA-512` daha
  iyidir (parola ağda düz gitmez, challenge-response kullanılır) ama **veri hâlâ şifresizdir** —
  mesaj içerikleri ve offset bilgileri ağda açıktır.
- **Üretimde doğru kombinasyon:** `SASL_SSL` + `SCRAM-SHA-512` (vanilla Kafka'da önerilen) ya da
  `SASL_SSL` + `OAUTHBEARER` (merkezî kimlik) ya da mTLS.
- **`PLAIN`'in ikinci sorunu:** Parolalar JAAS yapılandırmasında **düz metin** durur — dosya
  sisteminde, konteyner imajında, Kubernetes ConfigMap'inde. SCRAM ([parolayı düz metin taşımayan SASL mekanizması ↗](../00-baslangic/02-kavram-sozlugu.md#scram))'de parolalar hash'li olarak
  cluster metadata'sında saklanır ve `kafka-configs.sh` ile yönetilir.
- **Bu setteki lab neden `SASL_PLAINTEXT` + `PLAIN` kullanıyor?** Sertifika üretmeden ACL
  davranışını göstermek için — ve bu, konu içinde **açıkça** uyarı olarak belirtilmiştir.

> 📌 **Sık yapılan hata:** "İç ağdayız, şifrelemeye gerek yok" varsayımı. İç ağ da dinlenebilir;
> ayrıca çoğu uyum standardı (PCI-DSS, KVKK/GDPR yorumları) hareket hâlindeki veri için
> şifreleme bekler.

🔗 Konu: [5.4 §1](5.4-guvenlik.md)

---

### Soru 3 — TLS sonrası broker CPU'su neden artar? Bu bir hata mı?

**Kısa cevap:** **Hayır, hata değil — beklenen ve mimari bir sonuçtur.** TLS açıldığında
**sıfır kopya (`sendfile`) devre dışı kalır**: veri artık page cache'ten doğrudan sokete
aktarılamaz, önce kullanıcı alanına kopyalanır, şifrelenir, sonra gönderilir. Buna simetrik
şifreleme maliyeti eklenir.

**Ayrıntı:**

| | Sıfır kopya | TLS |
|---|---|---|
| Kopya sayısı | 2 | 4 |
| JVM heap kullanımı | **Yok** | Var (GC baskısı) |
| CPU | Neredeyse yok | Şifreleme + kopyalama |

Tipik etki: aynı trafikte broker CPU'sunun **%40 → %85** seviyesine çıkması.

**Ne yapılır:**
- **Kapasite planına dahil et** (5.5). TLS'i sonradan açmak bir "ayar değişikliği" değil,
  bir **kapasite olayıdır**.
- AES-NI destekli CPU ve modern bir JVM kullan; cipher suite'i donanım hızlandırmalı olanlarla
  sınırla.
- TLS'i kapatmak genelde seçenek değildir ve olmamalıdır.

**Aynı optimizasyonu kaybettiren diğer durumlar:** eski client için mesaj format dönüşümü
(4.x'te büyük ölçüde tarih oldu, KIP-896) ve **uzak (tiered) segment** okuma (1.5).

**Sertifika tarafında bir hatırlatma:** Keystore/truststore dosyaları **dinamik olarak**
yeniden yüklenebilir (`kafka-configs.sh` ile broker config güncelleyerek) — süresi dolan
sertifika için broker restart'ı gerekmez. En sık yaşanan TLS kesintisi **"sertifika süresi
doldu"**dur; rotasyonu süre dolmadan planla.

> 📌 **Sık yapılan hata:** TLS sonrası CPU artışını ayar arayışıyla "çözmeye" çalışmak. Sebep
> bellidir; çözüm ayar değil **kapasite**dir.

🔗 Konu: [5.4 §4](5.4-guvenlik.md) · [1.1 §5](../01-broker-depolama/1.1-log-segment-ve-index.md)

---

### Soru 4 — `allow.everyone.if.no.acl.found=true` ne yapar, neden tehlikeli?

**Kısa cevap:** *"Bir kaynak için hiç ACL tanımlanmamışsa, o kaynağa **herkes** erişebilir"*
demektir. Yani yetkilendirmeyi **pratikte kapatır** — yalnızca ACL'i **olan** kaynaklar korunur,
geri kalan her şey serbesttir. Varsayılanı **false**'tur.

**Neden tehlikeli:**

1. **Yeni topic'ler otomatik olarak korumasız doğar.** Bir uygulama yeni bir topic oluşturur,
   kimse ACL yazmayı hatırlamaz — o topic herkese açıktır. Güvenlik açığı **sessizce büyür**.
2. **Geçiş dönemi için açılır, kapatılması unutulur.** En yaygın kullanım şudur: ACL'ler henüz
   yazılmamışken sistemi çalışır tutmak için `true` yapılır, "sonra kapatırız" denir ve
   kapatılmaz. Bu, üretimdeki en yaygın Kafka güvenlik açığıdır.
3. **Test edilmesi zordur.** Sistem çalışıyor göründüğü için eksik ACL'ler fark edilmez; ayarı
   kapattığın gün **onlarca uygulama birden** kırılır — ve o an geri açma baskısı doğar.

**Doğru geçiş yöntemi:**
1. `authorizer.class.name` ayarlanır ama `allow.everyone.if.no.acl.found=false` bırakılır.
2. Önce **tüm** uygulamalar için ACL'ler yazılır (denetim log'larından hangi principal neye
   eriştiği çıkarılabilir).
3. Doğrulama yapılır, sonra üretime alınır.

**İlgili tuzak — `super.users`:** Bu listedeki principal'lar ACL kontrolünü **tamamen atlar**.
Operasyon hesapları için makul, **uygulamalar için asla**. Bir uygulamayı `super.users`'a
eklemek, o uygulama için yetkilendirmeyi kapatmaktır.

> 📌 **Sık yapılan hata:** Bu ayarı "ACL'ler yavaş yavaş eklenecek" gerekçesiyle açık bırakmak.
> Bir yetkilendirme sistemi, yalnızca **varsayılanı reddetmek** olduğunda anlamlıdır.

🔗 Konu: [5.4 §2](5.4-guvenlik.md)

---

### Soru 5 — Uygulaman hata almadan yavaşladı: güvenlik tarafında hangi ihtimal?

**Kısa cevap:** **Kota (quota) throttle'ı (reddetmeden yavaşlatma).** Kafka kotayı **reddederek değil geciktirerek**
uygular — istemci hiçbir hata almaz, sadece yavaşlar. Bu yüzden "hata yok ama yavaşladık"
vakalarının sinsi bir sebebidir.

**Ayrıntı — kota tipleri:**

| Kota | Ayar | Etki |
|---|---|---|
| Üretim bant genişliği | `producer_byte_rate` | Aşan istemci throttle edilir |
| Tüketim bant genişliği | `consumer_byte_rate` | Aynı |
| İstek oranı | `request_percentage` | CPU zamanı payı |
| Controller mutasyonu | `controller_mutation_rate` | Topic oluşturma/silme hızı |

**Nasıl doğrularsın:**
```bash
# Bu kullanıcı/client için tanımlı kota var mı?
kafka-configs.sh --bootstrap-server ... --describe --entity-type users --entity-name alice
kafka-configs.sh --bootstrap-server ... --describe --entity-type clients --entity-name my-app
```
Ve throttle metriklerine bak: `kafka.server:type=Produce,client-id=...` altındaki
`throttle-time-avg` / `throttle-time-max` sıfırdan büyükse **kesin teşhis**tir. Client
tarafında da `produce-throttle-time-avg` / `fetch-throttle-time-avg` metrikleri vardır.

**Neden bu tasarım:** Kota reddetseydi uygulamalar hata döngüsüne girer ve retry fırtınası
yaratırdı. Geciktirmek, doğal bir backpressure üretir — ama **görünürlüğü** senin metrik kurmana
bağlıdır.

**İkinci güvenlik kaynaklı yavaşlama ihtimali:** TLS'in devreye alınmış olması (Soru 3) —
sıfır kopya kaybı broker CPU'sunu artırır ve gecikme yükselir. Üçüncüsü: mTLS handshake
maliyetinin bağlantı sık kurulup kapatılan bir istemcide birikmesi.

> 📌 **Sık yapılan hata:** Kota tanımlayıp throttle metriklerini panoya koymamak. Kota
> "uygulandığını söylemeyen" tek Kafka mekanizmasıdır.

🔗 Konu: [5.4 §5](5.4-guvenlik.md)

---

## 5.5 Kapasite ve partition tasarımı

📄 Sorular: [`5.5-kapasite-ve-partition-tasarimi.md`](5.5-kapasite-ve-partition-tasarimi.md)

### Soru 1 — 8.000 msg/sn × 500 bayt, sıkıştırma 0,4, replication.factor=3, 10 gün: disk?

**Kısa cevap:**

```
Günlük ham    = 8.000 × 500 B × 86.400      = 345,6 GB/gün
Sıkıştırılmış = 345,6 × 0,4                 = 138,2 GB/gün
Replikasyonla = 138,2 × 3                   = 414,7 GB/gün
10 gün        = 414,7 × 10                  = 4,15 TB
+%30 pay      = 4,15 × 1,3                  ≈ 5,4 TB
```

**Cluster genelinde ≈ 5,4 TB.** 3 broker'a dengeli dağılırsa broker başına **~1,8 TB**;
disk **%75**'i geçmemeli, yani broker başına en az **~2,4 TB** disk planlanmalı.

**Ayrıntı — formülün parçaları:**

| Adım | Neden |
|---|---|
| `× 86.400` | Saniyeden güne |
| `× sıkıştırma oranı` | Kafka batch'i **sıkıştırılmış** saklar (1.1) |
| `× replication.factor` | Her partition replication.factor kadar kopyalanır |
| `× retention_gün` | Saklama süresi |
| `× 1,3` | Segment döngüsü, compaction çalışma alanı, geçici dosyalar, dengesizlik |

**Unutulmaması gerekenler:**
- **`retention.bytes` partition başınadır** (1.4). Boyut sınırı koyacaksan
  `partition_sayısı × retention.bytes × replication.factor` hesapla.
- **Disk %75 kuralı:** Dolan disk broker'ı **durdurur** (5.3). %100'e göre planlama.
- **Reassignment payı:** Partition taşıma sırasında hedef broker'da geçici olarak **çift kopya**
  bulunur; %30 payın bir kısmı bunun içindir.
- **Sıkıştırma oranını varsayma, ölç.** 0,4 makul bir JSON tahminidir; `"x".repeat(200)` gibi
  yapay veri 0,04 verir ve seni yanıltır (2.1).

**Uzun saklama gerekiyorsa:** 10 gün yerine 90 gün istenirse hesap ~48 TB'a çıkar. O noktada
**tiered storage** (1.5) değerlendirilmelidir.

> 📌 **Sık yapılan hata:** replication.factor çarpanını unutmak. Tek kopya hesabı yapıp diski üçte bir
> boyutlandırmak, kapasite planlamasının en klasik hatasıdır.

🔗 Konu: [5.5 §3](5.5-kapasite-ve-partition-tasarimi.md)

---

### Soru 2 — Partition 10 → 20: key kullanan bir topic'te ne bozulur, neden geri alınamaz?

**Kısa cevap:** **Key → partition eşlemesi bozulur ve sıra garantisi kırılır.** Varsayılan
partitioner `murmur2(key) % N` kullanır; payda 10'dan 20'ye çıkınca aynı key başka bir
partition'a düşer. O andan itibaren aynı key'in eski olayları bir partition'da, yeni olayları
başka bir partition'da olur ve ikisi **paralel** tüketilir.

**Somut örnek:**
```
murmur2("musteri-42") % 10 = 7    →  eski olaylar partition 7'de
murmur2("musteri-42") % 20 = 17   →  yeni olaylar partition 17'de
```
Partition 7'de müşterinin işlenmemiş `SIPARIS_OLUSTURULDU` olayı beklerken, partition 17'yi
okuyan tüketici yeni gelen `SIPARIS_IPTAL`'i hemen işler. **İptal, oluşturmadan önce işlenir.**

**Neden geri alınamaz:**
1. **Partition sayısı azaltılamaz.** Kafka'da böyle bir işlem yoktur.
2. Azaltılabilse bile **eski veri eski partition'larda** kalırdı; hangi kaydın nereye ait olduğu
   bilgisi geri kurulamaz.
3. Tek çıkış yolu: **yeni bir topic** oluşturup veriyi yeniden anahtarlayarak taşımak ve
   tüketicileri kesintiyle geçirmek — yani bir migrasyon projesi.

**Zorunlu artırma nasıl yapılır (risk en aza indirilerek):**
1. Üretimi durdur.
2. Tüketicilerin lag'ini **0**'a indir — böylece eski partition'larda bekleyen olay kalmaz.
3. Partition sayısını artır.
4. Üretimi başlat.

**Doğru çözüm:** Partition sayısını **baştan cömert** seç (2-3× güvenlik payı, 5.5 §1) — çünkü
artırmanın bedeli budur, azaltmanın imkânı yoktur.

> 📌 **Sık yapılan hata:** Partition artırmayı "kesintisiz bir ölçekleme işlemi" sanmak. Komut
> anında döner, hiçbir uyarı vermez; kırılan şey **veriyi işleyen uygulamanın varsayımıdır**.

🔗 Konu: [5.5 §1–2](5.5-kapasite-ve-partition-tasarimi.md) · [2.1 §3](../02-producer/2.1-accumulator-ve-batching.md)

---

### Soru 3 — Bir broker'ın trafiği diğerlerinin 3 katı: iki sebep ve teşhis

**Kısa cevap:**

1. **Lider dengesizliği.** Partition liderleri o broker'da toplanmış. Tüm okuma/yazma lidere
   gittiği için trafik oraya yığılır. Genelde bir broker restart'ından sonra, liderlikler geri
   dağıtılmadan önce görülür.
2. **Sıcak partition (key skew).** Trafiğin büyük kısmı tek bir key'e (ya da az sayıda key'e)
   gidiyor; o partition'ın lideri olan broker boğulur. Diğerleri boşta bekler.

**Teşhis:**

| Sebep | Nasıl doğrularsın | Çözüm |
|---|---|---|
| Lider dengesizliği | `kafka-topics.sh --describe` çıktısında `Leader:` kolonunu say — liderler bir broker'da mı toplanmış? | `kafka-leader-election.sh --election-type preferred --all-topic-partitions` |
| Sıcak partition | **Partition başına** `BytesInPerSec` (5.2); ya da `kafka-get-offsets.sh` ile partition başına offset **artış hızını** karşılaştır | Key tasarımı: bileşik key, salt, özel partitioner, sıcak müşteriye ayrı topic |
| Partition sayısı dengesiz | `--describe` çıktısında broker başına replika sayısı | `kafka-reassign-partitions.sh` (throttle ile!) |

**Ayrıntı:**

- **Lider dengesizliği genelde kendiliğinden düzelir:** `auto.leader.rebalance.enable=true`
  (varsayılan) ile controller `leader.imbalance.check.interval.seconds` (**300**) periyodunda
  liderliği tercih edilen replikalara geri taşır. Bu yüzden bir broker restart'ından
  **5 dakika sonra** liderlikler yerine oturur (1.2). Oturmuyorsa elle tetikle.
- **Sıcak partition'ı broker eklemek çözmez.** Sorun kapasitede değil **key dağılımındadır**.
  Yeni broker eklersen o partition yine tek bir broker'da olur.
- Üçüncü olasılık: **TLS + sıkıştırma yeniden işleme** ya da o broker'da çalışan başka bir
  yük (combined modda controller lideri olması, 1.3).

**Sıcak key çözümleri:**

| Çözüm | Nasıl | Sıra etkisi |
|---|---|---|
| Bileşik key | `ulke + "-" + musteri_id` | Sıra **müşteri düzeyinde** korunur ✅ |
| Salt (tuz) | `key + "-" + (i % 10)` | Sırayı **bozar** ⚠️ |
| Özel partitioner | `partitioner.class` | İş kuralına göre |
| Ayrı topic | Büyük müşteriler için | Sıra korunur |

> 📌 **Sık yapılan hata:** Dengesizliği görünce broker eklemek. Önce **hangi tür** dengesizlik
> olduğunu ayır: lider mi, veri mi, key mi?

🔗 Konu: [5.5 §2, §4](5.5-kapasite-ve-partition-tasarimi.md) · [5.3 §5](5.3-ariza-senaryolari.md)

---

### Soru 4 — Reassignment'ta `--throttle` ve `--verify` neden zorunlu?

**Kısa cevap:**
- **`--throttle`:** Yeniden dağıtım, taşınan her partition'ın **tüm verisini** hedef broker'a
  kopyalar. Sınırlanmazsa bu replikasyon trafiği **tüm ağ ve disk bant genişliğini yer** ve
  canlı üretim/tüketimi durdurur.
- **`--verify`:** Throttle'ı **kaldıran adım budur**. Çalıştırmazsan cluster kalıcı olarak
  kısıtlı (yavaş) kalır — normal replikasyon da throttle'a takılır ve ISR daralmaya başlar.

**Ayrıntı — gerçek lab çıktısı:**
```
Save this to use as the --reassignment-json-file option during rollback
Warning: You must run --verify periodically, until the reassignment completes, to ensure the throttle is removed.
The inter-broker throttle limit was set to 10485760 B/s
Successfully started partition reassignments for lab-reassign-0,lab-reassign-1,lab-reassign-2

# --verify sonrası:
Reassignment of partition lab-reassign-0 is completed.
Clearing broker-level throttles on brokers 1,2,3
Clearing topic-level throttles on topic lab-reassign
```
Araç uyarıyı **açıkça** veriyor: *"You must run --verify ... to ensure the throttle is removed."*

**Doğru akış:**
```bash
# 1) Plan üret (uygulamaz)
kafka-reassign-partitions.sh ... --topics-to-move-json-file /tmp/topics.json \
  --broker-list "1,2,3" --generate
# 2) MEVCUT dağılımı sakla — geri alma planın odur
# 3) Uygula, throttle ile
kafka-reassign-partitions.sh ... --reassignment-json-file /tmp/plan.json \
  --execute --throttle 10485760
# 4) Tamamlanana kadar periyodik doğrula (throttle'ı bu kaldırır)
kafka-reassign-partitions.sh ... --reassignment-json-file /tmp/plan.json --verify
```

**Throttle değeri nasıl seçilir:** Broker'lar arası mevcut bant genişliğinin **%20-30'u** iyi bir
başlangıçtır. Çok düşük seçersen taşıma günlerce sürer ve o süre boyunca cluster geçici çift
kopya taşır (disk!). Çok yüksek seçersen canlı trafiği ezersin.

**Üçüncü zorunluluk (bonus):** `--generate` çıktısındaki **mevcut** dağılımı sakla. Taşıma
sırasında bir sorun çıkarsa geri alma planın odur.

> 📌 **Sık yapılan hata:** Taşıma "tamamlandı" göründüğü için `--verify` çalıştırmamak.
> Cluster haftalarca throttle altında kalır ve kimse sebebi bulamaz.

🔗 Konu: [5.5 §5](5.5-kapasite-ve-partition-tasarimi.md)

---

### Soru 5 — `broker.rack` ile `client.rack` farkı; ikisi ne kazandırır?

**Kısa cevap:**
- **`broker.rack`** (broker ayarı): Broker'ın hangi rack/AZ'de olduğunu söyler. Kafka replikaları
  **farklı rack'lere dağıtır** → bir AZ'nin tamamen kaybı partition'ı offline bırakmaz.
  **Dayanıklılık** kazandırır.
- **`client.rack`** (consumer ayarı): Tüketicinin hangi rack/AZ'de olduğunu söyler. Consumer
  **kendi bölgesindeki follower'dan** okuyabilir (rack-aware follower fetch) → AZ'ler arası veri
  transfer maliyeti ve gecikme düşer. **Maliyet ve gecikme** kazandırır.

**Ayrıntı:**

| | `broker.rack` | `client.rack` |
|---|---|---|
| Nerede | Broker config | Consumer config |
| Ne yapar | Replika **yerleşimi** | Okuma **yeri** |
| Kazanç | AZ kaybına dayanıklılık | AZ'ler arası trafik maliyeti ↓, gecikme ↓ |
| Ön şart | — | Broker'larda `broker.rack` tanımlı olmalı |

- **`broker.rack` bulut ortamında ilk yapılacak ayardır.** replication.factor=3 ve 3 AZ ile her partition'ın bir
  kopyası her AZ'de bulunur; bir AZ komple giderse ISR 2'ye düşer ve `min.insync.replicas=2` ile yazma
  **devam eder**.
- **`client.rack` bir istisnadır:** Normalde tüm okuma/yazma **lidere** gider (1.2). Rack-aware
  follower fetch bunun tek istisnasıdır — ve yalnızca **okuma** için geçerlidir; yazma her zaman
  lidere gider.
- **`client.rack`'in ekonomik değeri büyüktür:** Bulut sağlayıcıları AZ'ler arası trafiği
  ücretlendirir. Yüksek hacimli bir topic'te tüketicinin kendi AZ'sinden okuması aylık faturayı
  ciddi düşürür.
- **Küçük bir uyarı:** Follower'dan okurken consumer, liderden bir fetch turu kadar **geride**
  olabilir — HW yayılımı bir tur gecikir. Sıkı gerçek zamanlılık gerektiren akışlarda ölç.

> 📌 **Sık yapılan hata:** `client.rack`'i `broker.rack` tanımlamadan ayarlamak. Broker'lar rack
> bilgisi taşımıyorsa Kafka eşleştirme yapamaz ve consumer yine liderden okur — hiçbir kazanç
> olmaz, ama kimse fark etmez.

🔗 Konu: [5.5 §4](5.5-kapasite-ve-partition-tasarimi.md) · [1.2 §1](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

---

⬅️ [Bölüme dön](5.1-performans-tuning.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
