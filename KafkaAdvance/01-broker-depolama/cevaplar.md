# 01 · Broker ve depolama — Kendini kontrol cevapları

> Bu dosya [1.1](1.1-log-segment-ve-index.md) – [1.5](1.5-tiered-storage.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca "biliyordum" hissi gelir; bu his
> öğrenme değildir. Kâğıda yazdığın cevapla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [1.1](#11-log-segment-ve-index) · [1.2](#12-replikasyon-isr-hw-ve-elr) ·
[1.3](#13-kraft-ve-metadata) · [1.4](#14-retention-ve-compaction) · [1.5](#15-tiered-storage)

---

## 1.1 Log segment ve index

📄 Sorular: [`1.1-log-segment-ve-index.md`](1.1-log-segment-ve-index.md)

### Soru 1 — `00000000000000004711.log` ne demek, offset 4800 nasıl bulunur?

**Kısa cevap:** Dosya adı o segmentin **base offset**'idir: bu segmentteki ilk kaydın offset'i
4711'dir. Offset 4800 aranırken Kafka üç adım atar — (1) dosya adlarında ikili arama ile doğru
segmenti bulur, (2) o segmentin `.index` dosyasında ikili arama ile 4800'den küçük **en büyük**
girdiyi bulur, (3) `.log` dosyasında o bayt pozisyonundan **ileri doğru tarayarak** 4800'ü bulur.

**Ayrıntı:**

- Dosya adı 20 hane sıfır dolgulu yazılır ki **leksikografik sıralama = sayısal sıralama** olsun.
  `ls` çıktısı doğrudan offset sırasıdır; Kafka da bu diziyi ikili aramaya sokar.
- `.index` **seyrektir**: her kayıt için girdi yoktur, `index.interval.bytes` (varsayılan **4096**)
  kadar veri biriktikçe bir girdi düşer. Lab'da `index.interval.bytes=1024` ve kayıt ~877 bayt
  olduğu için **iki kayıtta bir** girdi düştü:
  ```
  offset: 2  position: 1754
  offset: 4  position: 3508
  offset: 6  position: 5262
  ```
- `.index` içindeki offsetler **göreli**dir (base offset'e göre), bu yüzden girdi 8 bayta sığar:
  4 bayt göreli offset + 4 bayt pozisyon.
- Adım 3'teki ileri tarama en fazla `index.interval.bytes` kadar sürer — yani sabit ve küçük.

> 📌 **Sık yapılan hata:** Index'i bir "sözlük" sanmak. Index bir **içindekiler sayfasıdır**:
> seni doğru sayfaya yaklaştırır, satırı taramayla bulursun.

🔗 Konu: [1.1 §1–2](1.1-log-segment-ve-index.md)

---

### Soru 2 — `segment.bytes=1GB` ama segmentler 150 MB'de dönüyor: üç sebep

**Kısa cevap:** (1) `segment.index.bytes` doluyor — özellikle **time index** (girdi 12 bayt),
(2) `segment.ms` süresi doluyor, (3) offset index doluyor (girdi 8 bayt) ya da broker seviyesindeki
`log.segment.bytes`/`log.roll.ms` topic ayarını ezmiş/farklı.

**Ayrıntı — segment dört limitten *ilk dolanla* döner:**

| Tetikleyici | Ayar | Varsayılan | Not |
|---|---|---|---|
| Segment boyutu | `segment.bytes` | 1 GiB | min 1 MiB (KIP-1030) |
| Süre | `segment.ms` | 7 gün | min 1 dakika (KIP-1030) |
| Offset index | `segment.index.bytes` | 10 MiB | girdi **8 bayt** |
| **Time index** | `segment.index.bytes` (aynı ayar!) | 10 MiB | girdi **12 bayt** → önce dolan |

**Lab'da gerçekten olan buydu:** `segment.bytes=1 MiB` verdik ama segmentler **169 kayıt /
~148 KB**'de döndü. `segment.index.bytes=1024` ve iki kayıtta bir girdi → time index
`1024/12 ≈ 85` girdide doldu ve segmenti **o** döndürdü. Aynı anda `.index` hâlâ 672 baytta,
yani dolmamıştı.

**Neden time index önce dolar?** Aynı `segment.index.bytes` bütçesi iki dosya için de geçerlidir
ama time index girdisi 12 bayt (8 bayt timestamp + 4 bayt göreli offset), offset index girdisi
8 bayttır. Yani time index **%50 daha hızlı** dolar.

> 📌 **Sık yapılan hata:** `segment.bytes`'ı büyütüp `segment.index.bytes`'ı unutmak. İkisi
> birlikte düşünülmeli; küçük kayıtlarda index limiti neredeyse her zaman önce dolar.

🔗 Konu: [1.1 §3](1.1-log-segment-ve-index.md)

---

### Soru 3 — Retention 1 saat ama 5 günlük veri diskte, topic günde 10 mesaj alıyor

**Kısa cevap:** Veri hâlâ **aktif segmentte**. Aktif segment hiçbir retention politikasıyla
silinmez; silme birimi **segment dosyasıdır**, kayıt değil. Günde 10 mesajla ne `segment.bytes`
doluyor ne de 7 günlük `segment.ms` süresi geçmiş — segment hiç dönmediği için silinecek kapalı
segment yok.

**Ayrıntı:**

- Retention thread'i `log.retention.check.interval.ms` (varsayılan **5 dakika**) periyodunda çalışır
  ve **kapalı** segmentlere bakar. Aktif segmenti hiç değerlendirmez.
- Kararı segmentin **son kaydının** zaman damgasına göre verir. Yani bir segment, içindeki en yeni
  kayıt bile eskiyene kadar durur.
- **Çözüm:** `segment.ms`'i düşürmek (min 1 dakika). Örneğin `segment.ms=3600000` (1 saat) verirsen
  segment saatte bir zorla döner ve retention devreye girebilir. Alternatif olarak
  `segment.bytes`'ı da küçültebilirsin, ama düşük trafikte zaman limiti daha etkilidir.
- **Bedeli:** Küçük segmentler = çok dosya = broker açılışında çok `mmap` = uzun başlangıç süresi.
  Aşırıya kaçma.

> 📌 **Sık yapılan hata:** Bunu "retention bug'ı" sanıp `retention.ms`'i daha da küçültmek.
> Hiçbir şey değişmez — sorun retention süresinde değil, **segmentin dönmemesinde**.

🔗 Konu: [1.1 §3](1.1-log-segment-ve-index.md) · [1.4 §2](1.4-retention-ve-compaction.md)

---

### Soru 4 — `linger.ms` 5 → 50: diskte ne değişir? (iki etki)

**Kısa cevap:**

1. **Batch'ler büyür → sıkıştırma oranı iyileşir → diskte daha az yer.** Sıkıştırma tek kayda
   değil **record batch'in tamamına** uygulanır; batch ne kadar büyükse tekrar eden alanlar
   (key önekleri, JSON anahtarları, header'lar) o kadar iyi sıkışır.
2. **Batch başlığı maliyeti amortize olur → daha az ek yük.** Bir record batch başlığı ~61 bayttır
   ve batch tek kayıt içerse bile ödenir. Lab'da 802 baytlık bir değer diskte **877 bayt** yer
   kapladı. 50 kayıtlık bir batch'te bu ~61 bayt 50'ye bölünür.

**Ayrıntı:** Aynı hareket **ağ** trafiğini de düşürür (aynı sıkıştırma), broker'ın işlediği
**request sayısını** azaltır (daha az `RequestsPerSec`, daha az CPU) ve replikasyon trafiğini
küçültür. Bedeli **gecikme**dir: bir kayıt en kötü durumda 50 ms fazladan bekler. Bu yüzden
`linger.ms` "verim ↔ gecikme" kolunun ta kendisidir.

> 📌 **Sık yapılan hata:** `linger.ms` artırmanın **her zaman** gecikmeyi artırdığını sanmak.
> Sistem doygunsa (batch'ler zaten `batch.size`'da dolup gidiyorsa) `linger.ms` hiç beklemez ve
> gecikmeye etkisi ~0 olur. Etkiyi ölçmeden karar verme (5.1).

🔗 Konu: [1.1 §4](1.1-log-segment-ve-index.md) · [2.1 Accumulator ve batching](../02-producer/2.1-accumulator-ve-batching.md)

---

### Soru 5 — TLS sonrası broker CPU'su neden artar, hangi optimizasyon kaybolur?

**Kısa cevap:** **Sıfır kopya (`sendfile`) devre dışı kalır.** TLS, veriye dokunmayı (şifrelemeyi)
gerektirir; veri artık page cache'ten doğrudan sokete aktarılamaz, önce kullanıcı alanına
kopyalanır, şifrelenir, sonra sokete yazılır. Buna bir de simetrik şifreleme maliyeti eklenir.

**Ayrıntı — normal okuma yolu (`sendfile`) vs TLS'li yol:**

| | Sıfır kopya | TLS |
|---|---|---|
| Kopya sayısı | 2 | 4 |
| JVM heap kullanımı | **Yok** | Var (GC baskısı) |
| CPU | Neredeyse yok | Şifreleme + kopyalama |

Tipik etki: aynı trafikte broker CPU'sunun **%40 → %85** seviyesine çıkması (5.4'teki örnek).
Bu bir bug değil, **mimari bir sonuçtur**.

**Aynı optimizasyonu kaybettiren diğer durumlar:**
- Eski client için **mesaj format dönüşümü** (down-conversion) — 4.x'te büyük ölçüde tarih oldu
  (KIP-896) ama karma sürümlü ortamlarda hâlâ görülebilir.
- **Uzak (tiered) segment** okuma — uzak segmentte sıfır kopya yoktur, broker indirir (1.5).

**Ne yapılır:** TLS'i kapatmak genelde seçenek değildir. Yapılabilecekler: AES-NI destekli
CPU/JVM kullanmak, cipher suite'i modern ve donanım hızlandırmalı olanla sınırlamak, broker'ları
CPU tarafında biraz büyütmek ve TLS'i **kapasite planına baştan** yazmak (5.5).

> 📌 **Sık yapılan hata:** TLS açıldıktan sonraki CPU artışını "Kafka yavaşladı" diye ayar
> arayışına girmek. Sebep bellidir; çözüm ayar değil **kapasite**dir.

🔗 Konu: [1.1 §5](1.1-log-segment-ve-index.md) · [5.4 Güvenlik](../05-operasyon/5.4-guvenlik.md)

---

## 1.2 Replikasyon: ISR, HW ve ELR

📄 Sorular: [`1.2-replikasyon-isr-hw.md`](1.2-replikasyon-isr-hw.md)

### Soru 1 — replication.factor=5, min.insync.replicas=3: kaç broker kaybına kadar yazma sürer, kaç kayba kadar veri kaybı olmaz?

**Kısa cevap:** **Yazma 2 broker kaybına kadar devam eder** (`replication.factor − min.insync.replicas = 5 − 3 = 2`).
**Veri kaybı 4 broker kaybına kadar olmaz** — commit edilmiş bir kayıt tanım gereği ISR'deki
her replikada vardır; en az bir replika hayattayken kayıt durur.

**Ayrıntı:**

- **Yazma kuralı:** `|ISR| >= min.insync.replicas` olduğu sürece `acks=all` yazmaları kabul edilir.
  5 replikadan 2'si düşerse ISR 3'tür, şart sağlanır. 3'ü düşerse ISR 2 olur, `2 < 3` → broker
  `NotEnoughReplicasException` döner ve **yazma durur**.
- **Okuma kuralı:** Yazma dursa bile **okuma devam eder**. Bu ayrımı karıştırmamak önemli:
  `min.insync.replicas` yalnızca **yazma** tarafını kısıtlar.
- **Veri kaybı kuralı:** Commit edilmiş (HW'nin altındaki) bir kayıt, o anki ISR'deki tüm
  replikalarda vardır. Yazma durduğu anda ISR'de en az 3 replika vardı; kalan 1 replika hayatta
  olduğu sürece veri diskte durur. 5'inin de kaybı (kalıcı disk kaybı) veri kaybıdır.
- **Şart:** `unclean.leader.election.enable=false` olmalı. `true` ise ISR dışı geri kalmış bir
  replika lider olabilir ve **commit edilmiş veri** silinir — o zaman yukarıdaki garanti çöker.

**Genel formüller:**
```
Yazmaya devam ederek tolere edilen kayıp = replication.factor − min.insync.replicas
Veri kaybı olmadan tolere edilen kayıp   = replication.factor − 1     (unclean kapalıyken)
```

> 📌 **Sık yapılan hata:** "Yazma durdu = veri kaybettik" sanmak. Tam tersi: yazmanın durması
> Kafka'nın seni **koruduğu** andır. Sessiz kayıp, `min.insync.replicas` düşük olduğunda olur.

🔗 Konu: [1.2 §3](1.2-replikasyon-isr-hw.md)

---

### Soru 2 — HW ile LEO arasındaki kayıtlar neden gösterilmez?

**Kısa cevap:** Çünkü o kayıtlar henüz **commit edilmemiştir** — yalnızca liderde (ya da ISR'in
bir kısmında) vardır. Lider çökerse bu kayıtlar kaybolabilir. Consumer'a gösterilseydi,
**okunmuş ama sonradan var olmayan** bir kayıt ortaya çıkardı: "phantom read".

**Ayrıntı — somut senaryo:**

```
lider    : [0][1][2][3][4]   LEO=5
follower1: [0][1][2][3]      LEO=4
follower2: [0][1][2]         LEO=3
                    ↑ HW=3
```

Consumer 3 ve 4'ü okuyabilseydi ve hemen ardından lider çökseydi, yeni lider follower2 olurdu
(LEO=3). 3 ve 4 **hiç var olmamış** olurdu. Consumer ise onları işlemiş, belki bir e-posta
göndermiş, belki bir ödeme almış olurdu. Ayrıca offset'ini 5'e commit ettiyse, yeni liderde
olmayan bir offset'e commit etmiş olur — `OffsetOutOfRange` ve `auto.offset.reset` davranışı
devreye girer.

**HW'nin tanımı:** ISR'deki **tüm** replikaların ulaştığı en yüksek offset — yani "en yavaş
senkron replikanın seviyesi". Lider bunu follower'ların fetch isteklerindeki offset bilgisinden
hesaplar ve bir sonraki fetch cevabında follower'lara bildirir. Bu yüzden HW'nin yayılması
**bir fetch turu gecikir**.

> 📌 **Sık yapılan hata:** LEO ile HW'yi karıştırmak. LEO **tek bir replikanın** sonu, HW
> **ISR'in ortak asgarisi**dir. `kafka-get-offsets.sh` sana HW'yi gösterir, LEO'yu değil.

🔗 Konu: [1.2 §1](1.2-replikasyon-isr-hw.md)

---

### Soru 3 — Follower ISR'den düştü ama broker ayakta ve fetch ediyor: üç sebep

**Kısa cevap:** Tek kural var — follower `replica.lag.time.max.ms` (**varsayılan 30 s**) süresince
liderin LEO'suna **hiç değemezse** düşer. "Fetch ediyor" yetmez, "yetişiyor" gerekir. Üç tipik sebep:

1. **Yavaş disk.** Follower çektiği veriyi kendi log'una yazamıyor; I/O kuyruğu doluyor
   (bozuk disk, komşu gürültüsü, EBS/IOPS limiti). `LogFlushRateAndTimeMs`, disk `await` metrikleri.
2. **Ağ doygunluğu ya da yetersiz fetcher.** `num.replica.fetchers` varsayılan **1**'dir; tek
   thread onlarca partition'ı çekiyorsa yetişemez. `replica.fetch.max.bytes` (1 MiB) küçük kalmış
   olabilir. Yüksek trafikli cluster'da `num.replica.fetchers`'ı artırmak klasik çözümdür.
3. **GC duraklamaları.** Follower broker'da uzun bir stop-the-world duraklaması, 30 saniyelik
   pencerede fetch'i geciktirir. Broker heap'i gereğinden büyükse (1.1) bu daha sık olur.

**Diğer olasılıklar:** Lidere ani ve büyük bir trafik artışı (follower yapısal olarak geride
kalır), broker üzerinde partition/lider dengesizliği (5.5), sıkışan bir `log.dirs` diski
(JBOD ([RAID’siz, her disk ayrı log.dirs ↗](../00-baslangic/02-kavram-sozlugu.md#jbod))'da tek disk yavaşsa yalnız oradaki partition'lar düşer).

**Nasıl teşhis edilir:** `UnderReplicatedPartitions` metriğinin **hangi broker'da** yükseldiğine
bak. Tek broker'da yükseliyorsa sorun o broker'da (disk/GC); tüm broker'larda yükseliyorsa
sorun **lider tarafında** ya da ağdadır.

> 📌 **Sık yapılan hata:** Alarmı susturmak için `replica.lag.time.max.ms`'i büyütmek. Bu, geride
> kalmış bir replikanın daha uzun süre "senkron" sayılması demektir — **veri kaybı riskini
> doğrudan artırır**. Alarmı değil sebebi düzelt.

🔗 Konu: [1.2 §2](1.2-replikasyon-isr-hw.md) · [5.2 Metrikler](../05-operasyon/5.2-metrikler-ve-izleme.md)

---

### Soru 4 — Unclean seçim oldu: consumer offset'lerine ne olur?

**Kısa cevap:** Yeni lider geride kalmış olduğu için log'un sonu **geriye kayar**. Offset'i yeni
liderin LEO'sundan büyük olan consumer'lar `OffsetOutOfRangeException` alır ve
`auto.offset.reset` ayarına göre davranır: `latest` ise **aradaki kayıtları atlar**,
`earliest` ise **baştan okuyup her şeyi yeniden işler**, `none` ise **hata fırlatıp durur**.

**Ayrıntı:**

- Diyelim eski lider offset 1000'e kadar yazmıştı ve consumer 950'yi commit etmişti. ISR dışı
  replika yalnızca 800'e kadar veriye sahipti ve lider oldu. Artık log'un sonu 800.
- Consumer 950'den okumak ister; broker "böyle bir offset yok" der.
  - `auto.offset.reset=latest` → consumer 800'den devam eder, **800–950 arasını hiç okumaz**
    (aslında o kayıtlar zaten yok) ama daha kötüsü: yeni yazılan kayıtlar 800'den itibaren
    numaralanır, yani **aynı offset'ler farklı kayıtlara** karşılık gelir.
  - `auto.offset.reset=earliest` → 0'dan başlar, **tüm veriyi yeniden işler** (duplicate seli).
- `__consumer_offsets` topic'indeki commit kaydı silinmez; sadece artık geçersiz bir offset'i
  gösterir.
- Bu yüzden unclean seçim sonrası standart operasyon adımı, etkilenen grupların offset'lerini
  `kafka-consumer-groups.sh --reset-offsets` ile **bilinçli bir noktaya** taşımaktır.

> 📌 **Sık yapılan hata:** `unclean.leader.election.enable=true` yapmayı "partition'ı açan zararsız
> bir çözüm" sanmak. Bu ayar **commit edilmiş veriyi silmeyi kabul etmektir**. Önce ISR'deki
> broker'ı ayağa kaldırmayı dene; unclean son çaredir ve sonrası temizlik ister.

🔗 Konu: [1.2 §4](1.2-replikasyon-isr-hw.md) · [3.3 Offset ve commit](../03-consumer/3.3-offset-ve-commit.md)

---

### Soru 5 — ELR'nin çözdüğü "last replica standing" problemi

**Kısa cevap:** ISR bir replikaya kadar daraldığında, o tek replika da **temiz olmayan** şekilde
çökerse geriye lider adayı kalmaz. Eski dünyada iki seçenek vardı: partition'ı **sonsuza kadar
offline** bırakmak ya da `unclean` seçimle **veri kaybetmek**. ELR, controller'ın "ISR'den düşmüş
**ama verisi hâlâ güvenilir**" replikaları ayrı bir listede takip etmesini sağlar; böylece ISR
boşalsa bile veri kaybetmeden lider seçilebilecek adaylar **bilinir**.

**Ayrıntı:**

- **Neden ISR bilgisi yetmiyordu?** ISR "şu an yetişenler" listesidir; bir replika ISR'den düştüğü
  anda controller onun ne kadar veriye sahip olduğunu unutuyordu. ELR (KIP-966) bu bilgiyi
  **kalıcı olarak** metadata'da tutar.
- **"Verisi güvenilir" ne demek?** Replika HW'ye kadar olan tüm veriye sahipti ve **temiz**
  kapandı. Bir broker temiz olmayan kapanıştan (kill -9, güç kesintisi) sonra kaydolursa
  controller onu hem ISR'den **hem ELR'den** çıkarır — çünkü diskindeki son yazmalara artık
  güvenilemez (Kafka `fsync` etmez, page cache'te veri kalmış olabilir).
- `--describe` çıktısında üç kolon görürsün:
  ```
  Topic: lab-hello  Partition: 0  Leader: 3  Replicas: 3,1,2  Isr: 3,1,2  Elr:   LastKnownElr:
  ```
  `Elr` = lider olmaya uygun ISR-dışı adaylar; `LastKnownElr` = ELR de boşaldığında son bilinenler.
- ELR, KIP-966'nın getirdiği **daha katı HW kuralıyla** birlikte anlamlıdır: HW yalnızca ISR boyutu
  `min.insync.replicas`'a eşit ya da fazlayken ilerler. Yani "commit edildi" damgası artık gerçekten
  min.insync.replicas kadar kopyada olmayı gerektirir.
- Kafka **4.0**'da deneysel, **4.1**'den beri varsayılan açık — yalnızca KRaft'ta.

> 📌 **Sık yapılan hata:** ELR'yi "fazladan bir yedek kopya" sanmak. ELR yeni bir replika
> **oluşturmaz**; sadece bir **muhasebe kaydıdır**. `min.insync.replicas=1` bırakan bir cluster'ı
> ELR kurtarmaz.

🔗 Konu: [1.2 §5](1.2-replikasyon-isr-hw.md)

---

## 1.3 KRaft ve metadata

📄 Sorular: [`1.3-kraft-metadata.md`](1.3-kraft-metadata.md)

### Soru 1 — 3 node'lu combined cluster'da 2 node çökerse ne çalışır, ne durur?

**Kısa cevap:** **Metadata yazan her şey durur** (topic oluşturma/silme, partition ekleme, ACL
değişikliği, lider seçimi, ISR güncellemesi, config değişikliği) — çünkü Raft quorum'u (karar için gereken salt çoğunluk) için
`(3/2)+1 = 2` voter (oy veren controller düğümü) gerekir, geriye 1 kaldı. **Mevcut lideri hayatta olan partition'lara veri
okuma/yazma** teknik olarak devam eder, ama `min.insync.replicas=2` ise `acks=all` yazmaları da
reddedilir.

**Ayrıntı:**

- **Combined mod** (`process.roles=broker,controller`) demek, aynı süreç hem veri hem metadata
  işi yapıyor demektir. 2 node ölünce yalnızca broker'ları değil **controller quorum'unu da**
  kaybedersin. Lab'da tam olarak bu hatayı görürsün:
  ```
  Cancelled createTopics request ... node 1 being disconnected
  ```
- **Neden bu bir bug değil:** Raft **kesin çoğunluk** ister ve pazarlık kabul etmez. ISR "kim
  yetişebiliyorsa o" diye esneyebilir; Raft esnemez. Çoğunluk yoksa yeni bir metadata kaydı
  **yazılamaz**, çünkü yazılsa split-brain riski doğar.

| | ISR (veri) | Raft quorum (metadata) |
|---|---|---|
| Onay kuralı | ISR'deki herkes (ISR daralabilir) | Kesin çoğunluk `(N/2)+1` |
| Uyarlanabilir mi | Evet | **Hayır** |
| 3 node ile kayıp toleransı | `replication.factor − min.insync.replicas`'e bağlı | **1** |
| 5 node ile kayıp toleransı | — | **2** |

- **Kalan tek node ne yapar?** Kendini follower/candidate olarak görür, sürekli seçim başlatmaya
  çalışır ama çoğunluk oluşamaz. Metadata'yı **okuyabilir** (kendi kopyası var), yazamaz.
- **Üretimde ne yapılır:** Isolated mod (3 ayrı controller + N broker) ya da 5 voter. Combined
  mod lab ve küçük cluster içindir.

> 📌 **Sık yapılan hata:** Lab'da "2 broker öldürelim, ISR davranışını görelim" derken cluster'ın
> tamamen kilitlenmesine şaşırmak. 1.2 pratiğinde bu uyarı bilerek verilmiştir.

🔗 Konu: [1.3 §3–4](1.3-kraft-metadata.md)

---

### Soru 2 — `__cluster_metadata` neden tek partition?

**Kısa cevap:** Çünkü metadata **tek ve kesin bir olay sırası** gerektirir. Raft'ın garantisi
"replike edilmiş **tek** bir log" üzerine kuruludur. Partition sayısı artsaydı, partition'lar
arasında **toplam sıra (total order)** garantisi kalmazdı ve birbirine bağlı metadata olayları
(örn. "topic silindi" ile "aynı isimle topic oluşturuldu") farklı node'larda **farklı sırada**
uygulanabilirdi — yani cluster durumu ıraksardı.

**Ayrıntı:**

- Metadata olayları birbirine bağımlıdır: bir partition'ın lider değişimi, o topic'in var olmasına
  bağlıdır; bir ACL, o kaynağın tanımlı olmasına bağlıdır. İki ayrı partition'da bunların sırası
  garanti edilemez.
- Raft protokolünün kendisi de tek bir log üzerinde tanımlıdır: log index'i = terim + sıra.
  Çok partition, çok Raft grubu demektir — bu tamamen farklı (ve çok daha karmaşık) bir tasarımdır.
- **Ölçek endişesi yersizdir**: metadata yazma hacmi veri hacminin yanında ihmal edilebilir.
  Tek partition darboğaz değildir. Büyümeyi **snapshot** mekanizması dizginler: controller
  periyodik olarak "şu andaki tüm durum" anlık görüntüsünü alır (`...snapshot` dosyası) ve
  eski log kayıtları atılabilir. Yeni katılan bir node önce snapshot'ı yükler, üstüne son
  kayıtları uygular.
- Diskte tıpkı normal bir partition gibi durur:
  ```
  /var/lib/kafka/data/__cluster_metadata-0/
  ├── 00000000000000000000.log
  ├── 00000000000000002306.snapshot
  └── leader-epoch-checkpoint
  ```

> 📌 **Sık yapılan hata:** `__cluster_metadata`'yı normal bir topic sanıp `kafka-topics.sh` ile
> yapılandırmaya çalışmak. O bir Raft log'udur; `--bootstrap-controller` ile
> `kafka-metadata-quorum.sh` üzerinden incelenir.

🔗 Konu: [1.3 §2](1.3-kraft-metadata.md)

---

### Soru 3 — Yeni controller node quorum'a katılınca metadata'yı nasıl edinir? Snapshot'ın rolü?

**Kısa cevap:** Yeni node, lider controller'dan metadata log'unu **fetch eder** — tıpkı bir
follower gibi. Ama log'un tamamını baştan okumak yerine önce en güncel **snapshot**'ı indirip
yükler, sonra snapshot'tan sonraki kayıtları uygular. Snapshot olmasaydı katılım süresi cluster'ın
tüm geçmişiyle orantılı olurdu.

**Ayrıntı — adım adım:**

1. Node açılır, `controller.quorum.voters` (ya da `kraft.version=1` ise dinamik voter kümesi)
   üzerinden lideri bulur.
2. Kendi disk durumuna bakar: hiç veri yoksa ya da çok geride kalmışsa lider ona
   **snapshot indirmesini** söyler (`FetchSnapshot` API).
3. Snapshot yüklenir → node "şu offset'e kadar tüm durumu biliyorum" noktasına gelir.
4. Oradan itibaren normal `Fetch` ile log kayıtlarını çeker, **delta** olarak uygular.
5. Yetiştiğinde oy kullanmaya başlar.

**Snapshot'ın iki işi:**
- **Katılım hızı:** Yeni/geri gelen node'un baştan okuma yükünü ortadan kaldırır.
- **Log budama:** Snapshot alındıktan sonra ondan eski log kayıtları silinebilir; metadata log'u
  sonsuza kadar büyümez.

**Broker'lar da aynı yolu izler** (onlara *observer* denir: log'u okurlar, oy vermezler) — fark, oy kullanmamalarıdır. Broker'ın kendi
belleğindeki metadata görüntüsü bu log'un uygulanmasıyla oluşur; bu yüzden KRaft'ta controller
failover'ı **saniyeler** sürer (ZooKeeper döneminde yeni controller metadata'yı ZooKeeper'dan baştan
okurdu, büyük cluster'da dakikalar).

> 📌 **Sık yapılan hata:** Snapshot'ı bir "yedek" sanmak. Snapshot bir **hızlı katılım
> mekanizmasıdır**; felaket kurtarma aracı değildir.

🔗 Konu: [1.3 §2, §6](1.3-kraft-metadata.md)

---

### Soru 4 — `metadata.version` ile binary sürümü farkı; hangisi önce yükseltilir?

**Kısa cevap:** **Binary sürümü** = node'da çalışan Kafka jar'larının sürümü (ör. 4.3.1).
**`metadata.version`** = cluster'ın anlaştığı **metadata kayıt formatı seviyesi** (ör. `4.3-IV0`).
Yükseltme sırası: **önce binary (rolling restart ile tüm node'lar), sonra feature seviyesi.**

**Ayrıntı:**

- Yeni binary, **eski** `metadata.version` ile sorunsuz çalışabilir — yeni özellikler kapalı kalır.
  Tersi mümkün değildir: eski binary, tanımadığı bir metadata kaydını **okuyamaz** ve node açılmaz.
  Bu yüzden sıra zorunludur.
- Feature seviyesi cluster geneli bir karardır ve `kafka-features.sh upgrade` ile yükseltilir.
  Gerçek lab çıktısı:
  ```
  Feature: metadata.version       SupportedMaxVersion: 4.3-IV0  FinalizedVersionLevel: 4.3-IV0
  Feature: transaction.version    SupportedMaxVersion: 2        FinalizedVersionLevel: 2
  Feature: kraft.version          SupportedMaxVersion: 1        FinalizedVersionLevel: 0
  ```
  `SupportedMaxVersion` = binary'nin desteklediği tavan; `FinalizedVersionLevel` = cluster'ın
  fiilen kullandığı seviye. İkisi arasındaki fark, "yükseltilmemiş özellikler" demektir.
- `metadata.version` diğer tüm feature'ların **tabanıdır**: ELR (`eligible.leader.replicas.version`),
  KIP-848 (`group.version`), share groups (`share.version`), TV2 (`transaction.version`) hep
  yeterli bir `metadata.version` gerektirir.
- **Downgrade genelde mümkün değildir** ve sürüme özgüdür. Üretimde `upgrade` çalıştırmadan önce
  kendi sürümünün resmî upgrade notlarını okumalısın.

**Doğru sıra, tek satırda:**
```
1) Tüm node'ları yeni binary ile rolling restart et  →  2) doğrula  →  3) kafka-features.sh upgrade
```

> 📌 **Sık yapılan hata:** Binary'yi yükseltip "yeni özellik neden yok?" diye aramak. Özellik
> **feature seviyesi** yükseltilene kadar kapalıdır — ve bu bilinçli bir tasarımdır: geri
> dönüşü olan bir aşama bırakır.

🔗 Konu: [1.3 §5](1.3-kraft-metadata.md)

---

### Soru 5 — ISR "esnek", Raft quorum "katı": ne demek?

**Kısa cevap:** ISR **uyarlanabilir bir kümedir** — üye sayısı koşullara göre küçülür/büyür ve
sistem çalışmaya devam edebilir. Raft quorum'u **sabit bir matematiksel eşiktir** — `(N/2)+1`
oy yoksa yazma **durur**, pazarlık yoktur.

**Ayrıntı — birer örnek:**

**ISR esnekliği (veri partition'ı):** replication.factor=3, min.insync.replicas=2 bir topic'te bir follower yavaşladı ve
`replica.lag.time.max.ms` süresince yetişemedi. Controller onu ISR'den çıkarır, ISR = {lider,
follower1} olur. `2 >= 2` olduğu için **yazma kesintisiz devam eder**. Follower yetiştiğinde
ISR'e geri alınır. Sistem "kaç kişi yetişiyorsa onunla" çalışır.

**Raft katılığı (metadata):** 3 voter'lı quorum'da 2 voter kayboldu. Kalan voter mükemmel
çalışıyor, diski sağlam, ağı iyi — ama **hiçbir metadata kaydı yazamaz**. Çünkü Raft'ın
güvenliği "her commit edilmiş kayıt çoğunluğun elindedir" varsayımına dayanır; çoğunluk yoksa
bu varsayım kurulamaz ve iki ayrı ağ bölümünün ikisinin de "ben lideriim" demesi (split-brain)
mümkün hâle gelirdi.

**Neden bu tasarım farkı var?** İkisinin **maliyeti** farklıdır. Veri partition'larında
kullanılabilirlik kritiktir ve `min.insync.replicas` ile taban zaten sen belirlersin. Metadata'da
ise tutarsızlık **tüm cluster'ı** bozar — orada esneklik lüks değil, risktir.

> 📌 **Sık yapılan hata:** "3 broker'ın 2'si ölse de Kafka çalışır" cümlesini KRaft'a taşımak.
> Veri tarafında `min.insync.replicas`'e bağlı olarak çalışabilir; **metadata tarafında çalışmaz**.
> Combined modda ikisi aynı süreçtir — bu yüzden combined mod üretim için önerilmez.

🔗 Konu: [1.3 §3](1.3-kraft-metadata.md) · [1.2 §2–3](1.2-replikasyon-isr-hw.md)

---

## 1.4 Retention ve compaction

📄 Sorular: [`1.4-retention-ve-compaction.md`](1.4-retention-ve-compaction.md)

### Soru 1 — "Kullanıcı profili" ve "ödeme olayları" topic'leri için `cleanup.policy`?

**Kısa cevap:**
- **Kullanıcı profili → `compact`** (gerekirse `compact,delete`). Bu bir **durum tablosudur**;
  önemli olan her `userId` için **en güncel** değerdir, geçmiş sürümler değil.
- **Ödeme olayları → `delete`.** Bu bir **olay akışıdır**; her olay ayrı ve anlamlıdır, hiçbiri
  bir diğerinin "eski sürümü" değildir. Saklama süresi hukuki/operasyonel gereksinime göre
  `retention.ms` ile belirlenir.

**Ayrıntı — karar kuralı:**

| Soru | Cevap "evet" ise |
|---|---|
| "Bu key'in yalnızca son değeri lazım mı?" | `compact` |
| "Her kayıt bağımsız bir olgu mu?" | `delete` |
| "Hem son durum lazım hem sınırsız büyümesin mi?" | `compact,delete` |

- **Profil topic'i neden `compact`:** Yeni bir tüketici (ya da bir Streams `KTable`) topic'i baştan
  okuyunca tüm kullanıcıların **güncel** hâlini elde eder. Bu "state'i log'dan yeniden kurma"
  yeteneği compaction'ın asıl değeridir. Silme, `null` value (tombstone) ile ifade edilir.
- **Ödeme topic'i neden `compact` değil:** Aynı `paymentId` için birden çok olay olabilir
  (`AUTHORIZED`, `CAPTURED`, `REFUNDED`) ve **hepsi** gereklidir. Compaction bunları
  "eski sürüm" sayıp silerdi — denetim izini kaybederdin.
- **Ödeme için `retention.ms`:** Mevzuat 10 yıl saklama istiyorsa Kafka'da 10 yıl tutmak yerine
  bir arşive (ya da tiered storage'a, 1.5) yazmak daha doğrudur. Kafka'daki retention genelde
  "en yavaş tüketicinin gecikmesi + rahat bir pay" olarak seçilir.

> 📌 **Sık yapılan hata:** Compacted bir topic'e olay akışı yazmak. Aynı key'e iki farklı olay
> yazarsan biri **sessizce kaybolur**. Compaction'ın "eski" tanımı senin iş mantığını bilmez.

🔗 Konu: [1.4 §1](1.4-retention-ve-compaction.md)

---

### Soru 2 — 10 milyon benzersiz key, her key bir kez yazıldı: compaction ne kazandırır?

**Kısa cevap:** **Hiçbir şey.** Compaction ancak bir key'in **birden fazla sürümü** varsa yer
kazandırır. Her key bir kez yazıldıysa silinecek "eski sürüm" yoktur; log aynen kalır.
Üstelik **maliyet ödersin**: cleaner thread'i sürekli tarama yapar, dedupe buffer'ı 10 milyon
key'in hash'ini tutmaya çalışır.

**Ayrıntı:**

- Cleaner, kirli kısımdaki key'lerin bir **hash haritasını** kurar
  (`log.cleaner.dedupe.buffer.size`, varsayılan **128 MiB**). Key başına ~24 bayt hesabıyla
  128 MiB kabaca 5-6 milyon key'e yeter. 10 milyon benzersiz key **tek geçişte sığmaz**;
  cleaner temizliği yarım bırakır, birden çok geçiş yapar ve CPU/IO harcar — hem de **hiçbir
  kazanç olmadan**.
- Bu senaryoda doğru politika `delete`'tir. Gerçekten "son durum" semantiği istiyorsan ama
  key'ler tekilse, sorun Kafka'da değil **veri modelinde**dir.
- Compaction'ın gerçekten kazandırdığı yer: az sayıda key + çok güncelleme. Lab'daki örnek tam
  budur — **10 key × 2000 güncelleme = 20001 kayıt → 218 kayıt**, disk ~10 MB → 180 KB.

**Ne zaman yine de mantıklı olabilir?** Key'ler bugün tekil ama **ileride güncellenecekse**
(ör. profil topic'i henüz yeni), compaction ileriye dönük doğru politikadır. O zaman
`log.cleaner.dedupe.buffer.size`'ı key sayısına göre büyütmeyi planla.

> 📌 **Sık yapılan hata:** Compaction'ı "sıkıştırma" (compression) sanmak. Compaction baytları
> küçültmez; **tekrar eden key'lerin eski sürümlerini siler**. Bayt küçültmek `compression.type`
> işidir ve ikisi tamamen ayrı dünyalardır.

🔗 Konu: [1.4 §3, §5](1.4-retention-ve-compaction.md)

---

### Soru 3 — Haftada bir çalışan batch tüketici + `delete.retention.ms=24h`: neden tehlikeli?

**Kısa cevap:** Tüketici **tombstone'ları kaçırır**. Bir key silindiğinde log'a `value=null` bir
tombstone yazılır; cleaner o key'in eski sürümlerini siler ve tombstone'u
`delete.retention.ms` (24 saat) boyunca tutar. 7 gün sonra gelen tüketici tombstone'u artık
göremez — ne silinen kaydı görür ne de silme bilgisini. Sonuç: **hayalet kayıt** —
tüketicinin elindeki kopyada o key hâlâ eski değeriyle durur, sonsuza kadar.

**Ayrıntı:**

- Tombstone'un hemen silinmemesinin **tek sebebi** budur: geride kalan tüketicilerin (ve yeniden
  kurulan Streams state store'larının) "bu key silindi" bilgisini görebilmesi.
- **Kural:** `delete.retention.ms` > (en yavaş tüketicinin maksimum gecikmesi) + güvenlik payı.
  Haftalık bir batch için `delete.retention.ms` en az **7 gün**, tercihen **10-14 gün** olmalı.
- Aynı kural `retention.ms` için de geçerlidir ama tombstone senaryosu daha sinsidir: `retention.ms`
  aşımında tüketici `OffsetOutOfRange` alır ve **gürültü çıkarır**; tombstone kaybı **sessizdir**.
- Streams tarafında aynı problem, `application.id` değiştirilip state store baştan kurulduğunda da
  ortaya çıkar (6.2).

> 📌 **Sık yapılan hata:** `delete.retention.ms`'i "tombstone ne kadar yaşasın" olarak değil
> "silinen veri ne kadar sonra gitsin" olarak okumak. Doğru soru şudur: **"En geç ne zaman
> okuyan bir tüketicim olabilir?"**

🔗 Konu: [1.4 §4](1.4-retention-ve-compaction.md)

---

### Soru 4 — 8 partition, `retention.bytes=2GB`: en kötü durumda disk?

**Kısa cevap:** **Tek broker'da değil, cluster genelinde `8 × 2 GB × replication.factor`.** replication.factor=3 ise
`8 × 2 × 3 = 48 GB`. Çünkü `retention.bytes` **partition başınadır**, topic başına değil — ve her
partition replication.factor kadar kopyalanır.

**Ayrıntı:**

- Broker başına düşen kısım, partition'ların dağılımına bağlıdır. 3 broker'a 24 replika
  (8 partition × replication.factor 3) dağıtılırsa broker başına 8 replika ≈ **16 GB**.
- **"En kötü durum" neden daha büyük olabilir:**
  - Aktif segment `retention.bytes`'a dahil sayılmaz gibi davranır — silme yalnızca kapalı
    segmentlere uygulanır, dolayısıyla gerçek kullanım limitin bir segment kadar üstüne çıkabilir.
  - Retention thread'i `log.retention.check.interval.ms` (**5 dakika**) periyodunda çalışır;
    iki tarama arasında limit aşılabilir.
  - Partition dağılımı dengesizse (5.5) bir broker diğerlerinden çok daha fazlasını taşıyabilir.
- `retention.ms` ile birlikte ayarlıysa **önce dolan kazanır**: hangisi önce sağlanırsa segment silinir.

**Kapasite planlaması için pratik formül:**
```
Disk (cluster) = partition × retention.bytes × replication.factor × (1 + emniyet payı)
Disk (broker)  = yukarıdaki / broker sayısı   (dengeli dağılım varsayımıyla)
```
Emniyet payı olarak **%30** yaygındır: reassignment sırasında geçici çift kopya, compaction
çalışma alanı ve dengesizlik için.

> 📌 **Sık yapılan hata:** `retention.bytes=2GB` yazıp "topic'im 2 GB yer kaplar" sanmak.
> Partition sayısını artırdığın gün disk kullanımın da katlanır — ve bu **sessizce** olur.

🔗 Konu: [1.4 §2](1.4-retention-ve-compaction.md) · [5.5 Kapasite](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md)

---

### Soru 5 — Compaction sonrası offset 5000–5200 arasında kayıt yok: hata mı?

**Kısa cevap:** **Hayır, tamamen normaldir.** Compaction offsetleri **korur**, yeniden
numaralandırmaz. Silinen kayıtların offsetleri log'da **boşluk (gap)** olarak kalır. Consumer bunu
sorunsuz karşılar: `poll()` basitçe bir sonraki **var olan** kaydı döner; 5000 isteyip 5201
alabilirsin.

**Ayrıntı:**

- **Neden yeniden numaralandırılmıyor?** Numaralandırılsaydı, commit edilmiş tüm consumer
  offset'leri anlamsızlaşırdı. Offset bir **kalıcı adres**tir; compaction adresleri değil
  içerikleri temizler.
- Consumer'ın gördüğü şey: `seek(5000)` → broker en yakın var olan kayıttan başlar.
  `OffsetOutOfRange` **fırlatmaz**, çünkü 5000 hâlâ log'un başlangıç ve bitiş offset'leri
  arasındadır.
- Lab'da bunun diskteki karşılığı çarpıcıydı: segment dosyaları **0 bayta** indi ama
  **dosya adları (base offset) korundu**:
  ```
  -rw-r--r-- 1 appuser appuser  0  00000000000000000000.log
  -rw-r--r-- 1 appuser appuser  0  00000000000000001984.log
  ```
- **Nerede sorun çıkarır?** "İşlenen kayıt sayısı = son offset − ilk offset" varsayan
  izleme/mutabakat kodları. Compacted topic'lerde bu eşitlik **yanlıştır**. Lag hesabı da
  aynı sebeple compacted topic'lerde olduğundan büyük görünebilir.

> 📌 **Sık yapılan hata:** Boşluğu "veri kaybı" sanıp alarm kurmak. Compacted bir topic'te offset
> sürekliliği **hiçbir zaman garanti edilmedi**; transaction'lı topic'lerde de aynı durum vardır
> (control record'lar offset tüketir, 4.1).

🔗 Konu: [1.4 §3, §5](1.4-retention-ve-compaction.md)

---

## 1.5 Tiered storage

📄 Sorular: [`1.5-tiered-storage.md`](1.5-tiered-storage.md)

### Soru 1 — `retention.ms=30 gün`, `local.retention.ms=2 gün`: 10 gün önceki kaydı okumak

**Kısa cevap:** Kayıt **uzak depodadır** (S3/GCS/HDFS). Consumer istediğinde broker o segmenti
uzaktan **indirir**, kendi belleğinde/önbelleğinde açar ve consumer'a döner. Gecikme artar çünkü
(1) **sıfır kopya devre dışıdır** — veri page cache'te değil, ağ üzerinden geliyor,
(2) nesne depolamanın ilk bayt gecikmesi (TTFB) onlarca-yüzlerce ms'dir,
(3) segment **bütün olarak** çekilir, tek kayıt için bile.

**Ayrıntı:**

- İki retention ayrı işler: `local.retention.ms` = broker diskinde kalma süresi,
  `retention.ms` = **toplam** (yerel + uzak). Yani "2 gün sıcak, 30 gün toplam" demek,
  "2–30 gün arası veri S3'te" demektir.
- Okuma yolu: `RemoteLogManager` → `__remote_log_metadata` topic'inden "bu offset hangi uzak
  segmentte?" bilgisini alır → `remote.log.reader.threads` (varsayılan **10**) havuzundan bir
  thread segmenti indirir → consumer'a döner. Uzak index'ler
  `remote.log.index.file.cache.total.size.bytes` (varsayılan **1 GiB**) ile yerelde önbelleklenir.
- **Broker kaynağı harcanır:** CPU (indirme + açma), ağ (dışarıya doğru), heap. Yani "eski veriyi
  okumak" artık **canlı trafiği etkileyen** bir iştir.

> 📌 **Sık yapılan hata:** `local.retention.ms`'i `retention.ms`'ten büyük yapmak. Anlamsızdır ve
> yapılandırma hatasıdır — yerel her zaman toplamın **alt kümesidir**.

🔗 Konu: [1.5 §1–2](1.5-tiered-storage.md)

---

### Soru 2 — Tiered storage neden compacted topic'lerde desteklenmez?

**Kısa cevap:** Çünkü compaction **segmentleri yeniden yazar**, uzak depodaki segment ise
**değiştirilemez (immutable)** kabul edilir. Cleaner bir segmenti temizlerken içindeki eski key
sürümlerini atıp dosyayı baştan oluşturur; S3'e yüklenmiş bir nesne için bu işlem "indir, temizle,
yeniden yükle, eskisini sil" demek olurdu — hem pahalı hem de tutarlılık açısından tehlikeli.

**Ayrıntı:**

- Tiered storage'ın tüm tasarımı şu varsayıma dayanır: **kapanmış bir segment bir daha değişmez.**
  `delete` politikasında bu doğrudur — segment ya durur ya tamamen silinir. `compact`'ta değildir.
- Ayrıca compaction, segmentleri **birleştirebilir** (küçülen segmentleri gruplayarak). Uzaktaki
  segment kimliklerinin bu birleşmeyi takip etmesi gerekirdi.
- Pratik sonuç: `__consumer_offsets`, Streams changelog'ları ve tüm durum tablosu topic'leri
  tiered storage'a **alınamaz**. Bunlar zaten compaction sayesinde küçük kalır — sorun da değildir.
- `compact,delete` de aynı sebeple desteklenmez.

> 📌 **Sık yapılan hata:** Tiered storage'ı cluster genelinde açıp "neden bu topic taşınmıyor?"
> diye aramak. `remote.storage.enable=true` compacted bir topic'te reddedilir; broker seviyesinde
> `remote.log.storage.system.enable=true` olması yetmez.

🔗 Konu: [1.5 §3](1.5-tiered-storage.md) · [1.4 §3](1.4-retention-ve-compaction.md)

---

### Soru 3 — 2.000 msg/sn × 2 KB, 60 gün, replication.factor=3: toplam ve yerel (6 saat) hacim

**Kısa cevap:**

```
Ham hız      = 2.000 × 2 KB          = 4.000 KB/sn ≈ 3,81 MiB/sn  (≈ 4 MB/sn)
Günlük       = 4 MB/sn × 86.400      ≈ 345,6 GB/gün
60 gün (×1)  = 345,6 × 60            ≈ 20,7 TB
60 gün (×replication.factor) = 20,7 TB × 3           ≈ 62,2 TB      ← toplam (çoğu uzakta)
Yerel 6 saat = 4 MB/sn × 21.600 × 3  ≈ 259 GB       ← broker disklerinde
```

**Ayrıntı:**

- **Sıkıştırmayı unutma.** Yukarıdaki sayılar **sıkıştırılmamış** haldir. `lz4`/`zstd` ile
  JSON benzeri veride 0,3–0,5 oranı tipiktir; 0,4 alırsan toplam ~25 TB, yerel ~104 GB olur.
  Kafka batch'i **sıkıştırılmış hâliyle** saklar, yani hem disk hem ağ bundan kazanır.
- **Uzakta replication.factor çarpanı gerekli mi?** Genelde **hayır** — nesne depolama kendi dayanıklılığını
  sağlar ve segment uzağa **bir kez** kopyalanır. Yani gerçekçi hesap:
  `uzak ≈ 20,7 TB (×1)` + `yerel ≈ 259 GB (×3)`. Yukarıdaki `×3`'lü toplam **üst sınırdır**;
  hangi modeli kullandığını sağlayıcının dokümanından doğrula.
- **Emniyet payı:** Kapasite planında yerel diske **%30** pay ekle (reassignment, compaction
  alanı, dengesizlik). 259 GB → ~340 GB planla.
- **Karşılaştırma — tiered storage olmadan:** 62,2 TB'ı broker disklerinde tutmak gerekirdi.
  6 broker ile broker başına ~10 TB. Tiered ile broker başına ~43-57 GB. Kazanç budur.

> 📌 **Sık yapılan hata:** Sadece "toplam TB"a bakmak. Asıl karar değişkeni **yerel** hacimdir —
> broker'ın disk boyutunu, açılış süresini ve reassignment süresini o belirler.

🔗 Konu: [1.5 §1](1.5-tiered-storage.md) · [5.5 Kapasite](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md)

---

### Soru 4 — Tiered storage bir yedekleme çözümü müdür?

**Kısa cevap:** **Hayır.** Bu bir **saklama katmanıdır**, yedek değil. Yanlışlıkla silinen bir
topic'in verisi uzak depodan da silinir. Bozuk yazılan bir veri uzağa da bozuk gider. Yedek,
tanım gereği **bağımsız bir kopya** ve **zamanda geri dönebilme** demektir; tiered storage'ın
ikisi de yoktur.

**Ayrıntı — neyi çözer, neyi çözmez:**

| ✅ Çözer | ❌ Çözmez |
|---|---|
| Uzun saklamanın broker maliyetini | Yanlışlıkla `--delete` edilen topic'i |
| Broker ekleme/çıkarmada taşınan veri hacmini | Bozuk/hatalı üretilmiş veriyi |
| Disk kapasitesi kaynaklı cluster büyütmeyi | Cluster'ın tamamen kaybını (bölge arızası) |
| Uzun geçmişe replay yeteneğini | Compaction'ı (desteklenmez) |

**Gerçek yedek/DR için ne kullanılır:**
- **Cluster'lar arası kopyalama:** MirrorMaker 2 ile ikinci bir cluster'a replikasyon (7.2).
  Bu da klasik anlamda "yedek" değil, **canlı ikinci kopyadır** — silme de replike olur.
- **Bağımsız arşiv:** Bir sink connector ile veriyi ayrı bir depoya (S3, data lake, DWH) yazmak.
  Kafka'daki silme buraya yansımaz.
- **Koruyucu operasyon:** `delete.topic.enable` üzerinde ACL, topic silmeyi onaya bağlamak,
  IaC ile topic tanımlarını versiyonlamak.

> 📌 **Sık yapılan hata:** "90 gün S3'te duruyor, yedeğimiz var" demek. S3'teki nesneler
> Kafka'nın **çalışma verisidir**; Kafka onları kendi retention kararlarıyla siler.

🔗 Konu: [1.5 §3–4](1.5-tiered-storage.md) · [7.2 MirrorMaker 2](../07-connect/7.2-connector-smt-ve-mm2.md)

---

### Soru 5 — Uzak segmentlerden ağır replay canlı trafiği neden etkiler? İki önlem?

**Kısa cevap:** Çünkü uzak okuma **broker'ın kaynağını** harcar: `remote.log.reader.threads`
havuzu, CPU (indirme + açma + şifre çözme), ağ bant genişliği ve heap. Sıfır kopya devre dışıdır.
Aynı broker aynı anda canlı producer/consumer trafiğine hizmet ettiği için, ağır bir replay
**producer gecikmesini ve replikasyonu** doğrudan etkiler.

**İki önlem:**

1. **Kota koy.** KIP-956 ile gelen tiered storage kotaları
   (`remote.log.manager.*.max.bytes.per.second`) uzak okuma/yazma hızını sınırlar. Ayrıca
   **client kotası** (5.4) ile replay yapan uygulamanın `consumer_byte_rate`'ini kıs — böylece
   replay yavaşlar ama cluster ayakta kalır.
2. **Replay'i izole et.** Replay'i canlı trafikten ayır:
   - Ayrı bir consumer group + düşük öncelikli, **iş saatleri dışında** çalışan bir iş,
   - ya da mümkünse **ayrı bir cluster**'a (MM2 ile kopyalanmış) yönlendir,
   - ya da rack-aware fetch ile replay'i belirli broker'lara çek.

**Üçüncü önlem (bonus):** `remote.log.reader.threads` havuzunu bilinçli boyutlandır. Büyütmek
replay'i hızlandırır ama canlı trafikten daha çok kaynak çalar; küçültmek replay'i doğal olarak
sınırlar. Bu bir performans ayarı değil, bir **izolasyon** ayarıdır.

**Nasıl fark edersin:** Replay sırasında `RemoteFetchBytesPerSec`, `RemoteReadRequestsPerSec`
yükselir; aynı anda `RequestQueueTimeMs` ve produce p99 gecikmesi tırmanır. İkisini aynı grafikte
görmek teşhisi bitirir (5.2).

> 📌 **Sık yapılan hata:** Tiered storage'ı açıp "artık istediğim kadar geçmişe dönebilirim"
> diye plansız replay başlatmak. Yetenek var, **bedeli** de var — ve bedeli canlı trafik öder.

🔗 Konu: [1.5 §2–3](1.5-tiered-storage.md) · [5.2 Metrikler](../05-operasyon/5.2-metrikler-ve-izleme.md)

---

⬅️ [Bölüme dön](1.1-log-segment-ve-index.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
