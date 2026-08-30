# Cheatsheet — Kafka Advanced tek sayfada

> 📖 Tanımadığın bir kısaltma ya da KIP görürsen: [kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md)
>
> Apache Kafka **4.3.x** (KRaft-only). Tüm sayılar bu sürümün varsayılanlarıdır ve
> lab cluster'ından okunmuştur. Sınav/mülakat öncesi 20 dakikada okunur.

---

## 1. Ezberlenecek varsayılanlar

### Producer (client)
| Ayar | Değer |
|---|---|
| `acks` | **all** |
| `enable.idempotence` | **true** |
| `linger.ms` | **5** ⬅ 4.0'da 0'dan değişti (KIP-1030) |
| `batch.size` | 16384 (16 KiB) |
| `buffer.memory` | 33554432 (32 MiB) |
| `max.block.ms` | 60000 |
| `max.request.size` | 1048576 (1 MiB) |
| `max.in.flight.requests.per.connection` | **5** |
| `retries` | Integer.MAX_VALUE |
| `delivery.timeout.ms` | **120000** |
| `request.timeout.ms` | 30000 |
| `compression.type` | none |
| `transaction.timeout.ms` | 60000 |

### Consumer (client)
| Ayar | Değer |
|---|---|
| `group.protocol` | **classic** ⬅ KIP-848 için `consumer` yapılmalı |
| `auto.offset.reset` | **latest** |
| `enable.auto.commit` | true |
| `auto.commit.interval.ms` | 5000 |
| `max.poll.records` | **500** |
| `max.poll.interval.ms` | **300000 (5 dk)** |
| `session.timeout.ms` | **45000** |
| `heartbeat.interval.ms` | 3000 |
| `fetch.min.bytes` | 1 |
| `fetch.max.wait.ms` | 500 |
| `fetch.max.bytes` | 52428800 (50 MiB) |
| `max.partition.fetch.bytes` | 1048576 (1 MiB) |
| `isolation.level` | **read_uncommitted** |
| `partition.assignment.strategy` | [Range, CooperativeSticky] |

### Broker
| Ayar | Değer |
|---|---|
| `min.insync.replicas` | **1** ⬅ üretimde 2 yap |
| `unclean.leader.election.enable` | **false** |
| `replica.lag.time.max.ms` | **30000** |
| `num.replica.fetchers` | 1 · `replica.fetch.max.bytes` 1 MiB |
| `log.retention.hours` | **168 (7 gün)** · `log.retention.bytes` **-1** |
| `log.segment.bytes` | 1 GiB (**min 1 MiB**) · `log.roll.hours` 168 (**min 1 dk**) |
| `log.index.interval.bytes` | 4096 |
| `log.cleaner.min.cleanable.ratio` | 0.5 · `log.cleaner.delete.retention.ms` **86400000 (24 sa)** |
| `log.retention.check.interval.ms` | 300000 (5 dk) |
| `message.max.bytes` | 1048588 |
| `num.network.threads` 3 · `num.io.threads` 8 | — |
| `offsets.topic.num.partitions` | **50** · `offsets.retention.minutes` **10080 (7 gün)** |
| `transaction.state.log.num.partitions` | 50 · replication.factor 3 · min.insync.replicas 2 |
| `auto.leader.rebalance.enable` true · `leader.imbalance.check.interval.seconds` **300** | — |
| `num.recovery.threads.per.data.dir` | **2** (KIP-1030) |
| `log.message.timestamp.after.max.ms` | **3600000** (KIP-1030) |
| `remote.log.storage.system.enable` | **false** |

### Kafka Streams
| Ayar | Değer |
|---|---|
| `processing.guarantee` | **at_least_once** |
| `commit.interval.ms` | **30000** (EOS'ta **100**) |
| `num.stream.threads` | 1 · `num.standby.replicas` **0** |
| `statestore.cache.max.bytes` | 10485760 |
| `state.dir` | **/tmp/kafka-streams** ⬅ üretimde değiştir |
| `deserialization.exception.handler` | **LogAndFail** |
| `processing.exception.handler` | **LogAndFail** (KIP-1033) |
| `errors.dead.letter.queue.topic.name` | **null** (KIP-1034) |
| `replication.factor` | **-1** (broker varsayılanı) |
| `task.timeout.ms` 300000 · `probing.rebalance.interval.ms` 600000 | — |

### Share group (KIP-932)
| Ayar | Değer |
|---|---|
| `group.share.record.lock.duration.ms` | **30000** (max 60000) |
| `group.share.delivery.count.limit` | **5** |
| `group.share.partition.max.record.locks` | 2000 |
| `group.share.max.size` | 200 · `assignors` simple |

---

## 2. Konu konu tek satırlık özet

| # | Konu | Tek cümle |
|---|---|---|
| 1.1 | Log segment | Partition = segment dosyaları; roll dört limitten **ilk dolanla** (genelde **time index**); **aktif segment silinmez**; sıfır kopya **TLS'te kaybolur** |
| 1.2 | Replikasyon | `acks=all` = **ISR'deki tümü**; **replication.factor − min.insync.replicas = tolere edilen kayıp**; ELR 4.1'den beri varsayılan |
| 1.3 | KRaft | Metadata **tek partition'lık log**; **kesin çoğunluk** (3→1, 5→2); yükseltme = binary + **feature seviyesi** |
| 1.4 | Retention | `delete`=zaman, `compact`=key; compaction **"son değer kaybolmaz"** der, "1 kayıt kalır" demez; `retention.bytes` **partition başına** |
| 1.5 | Tiered storage | `local.retention` ⊂ `retention`; uzak okumada **sıfır kopya yok**; **compacted topic'te çalışmaz** |
| 2.1 | Batching | `send()` **asenkron**; batch **dolunca VEYA linger dolunca**; `batch.size` **partition başına**; sticky partitioning |
| 2.2 | Idempotence | `(PID, partition, sequence)`; **oturum başına**; `max.in.flight ≤ 5` ile sıra korunur; restart → **yeni PID** |
| 2.3 | Teslimat | Zincir: `acks` → `min.insync` → `delivery.timeout` → **callback'teki kodun**; p99'da bedel |
| 2.4 | Şema | Baytın başına **kimlik** koy; **BACKWARD** = yeni veri ↔ eski okuyucu; **zehirli kayıt offset'i ilerletmez** |
| 3.1 | Fetch/poll | `fetch.*` = **ağdan bayt**, `max.poll.records` = **elime kayıt**; `records × süre < max.poll.interval` |
| 3.2 | Rebalance | Koordinatör = `hash(group)%50`; **session=yaşıyor mu, max.poll.interval=ilerliyor mu**; KIP-848 **client'ta opt-in** |
| 3.3 | Offset | **Önce commit = kayıp, sonra commit = tekrar**; commit değeri `offset+1`; offsetler **7 gün** |
| 3.4 | Share group | Kayıt başına dağıtım; **sıra yok**; `delivery.count.limit=5` zehirli kayda karşı ağ; **4.2'de üretime hazır** |
| 4.1 | Transactions | Abort **silmez, filtreler**; control record offset tüketir; TV2 her tx'te **epoch artırır**; açık tx **LSO'yu bloklar** |
| 4.2 | Outbox/idempotent | Dual write'ı **tek DB transaction'ına** indir; **mutlak yazma idempotenttir**; duplicate'te bile **offset ilerlet** |
| 5.1 | Tuning | Darboğazı **ölç**; doygun sistemde batching **gecikmeyi düşürür**; heap küçük, page cache büyük |
| 5.2 | Metrikler | **UnderMinIsr > UnderReplicated** aciliyeti; lag **partition başına ve zaman cinsinden**; `last-poll-seconds-ago` erken uyarı |
| 5.3 | Arıza | Katman → belirti → kanıt; **disk dolunca log silme**, retention düşür; `TimeoutException` çoğu zaman **maskedir** |
| 5.4 | Güvenlik | Kimlik ≠ yetki; tüketici **Group:Read**'i unutur; TLS **sıfır kopyayı öldürür**; kota **geciktirir** |
| 5.5 | Kapasite | `max(kapasite, paralellik, broker)`; partition **azaltılamaz**, artırmak **sırayı kırar**; reassignment `--throttle` + `--verify` |
| 6.1 | Topoloji | **Task = alt topoloji × partition**; repartition **delete**, changelog **compact**; `application.id` = group.id |
| 6.2 | State store | Gerçek kaynak **changelog** (compact → restore **key başına son değer**); `state.dir` **/tmp** olmasın |
| 6.3 | Windowing | **Stream time**; veri gelmezse pencere kapanmaz; hopping **çoğaltır**; stream-table **asimetrik** |
| 6.4 | EOS/hata | Üç katman handler; **LogAndContinue + DLQ**; `exactly_once_v2`; testte **cache=0** |
| 7.1 | Connect | Connector=config, task=iş; **source offset = dış konum, sink = Kafka offset**; `RUNNING` ≠ sağlıklı |
| 7.2 | SMT/MM2 | SMT **durumsuz ve hızlı**; MM2 **üç connector**, ön ek **döngü koruması**, **at-least-once**; offset çevrimi **muhafazakâr** (geri yuvarlar) |

---

## 3. En çok karıştırılan ikililer

| A | B | Ayıran cümle |
|---|---|---|
| `replication.factor` | `min.insync.replicas` | replication.factor kaç kopya var; min.insync.replicas kaç **güncel** kopya şart |
| `acks=all` | "tüm replikalar" | `all` = **ISR'deki** tümü |
| `session.timeout.ms` | `max.poll.interval.ms` | **Yaşıyor mu** / **ilerliyor mu** |
| `fetch.max.bytes` | `max.partition.fetch.bytes` | Cevabın **tamamı** / **partition başına** |
| `fetch.*` | `max.poll.records` | **Ağdan bayt** / **elime kayıt** |
| Compaction | Compression | Key başına son değer / bayt sıkıştırma |
| Repartition topic | Changelog topic | `delete`, taşıma / `compact`, durum yedeği |
| Idempotence | Transaction | Retry duplicate'i / **atomiklik** |
| PID | `transactional.id` | Oturumluk, otomatik / **kalıcı, senin verdiğin** |
| `onPartitionsRevoked` | `onPartitionsLost` | **Commit et** / **commit etme** |
| Source offset | Sink offset | Dış sistem konumu / Kafka offset'i |
| UnderReplicated | UnderMinIsr | Kopya eksik / **yazma durdu** |
| `broker.rack` | `client.rack` | Replika yerleşimi / **okuma yeri** |
| Grup lideri | Partition lideri | Bir **consumer** / bir **broker** |
| Koordinatör | Controller | Grup için / **cluster metadata** için |
| Stream-stream join | Stream-table join | Pencere **zorunlu** / pencere **yok**, asimetrik |
| Tumbling | Hopping | Çakışmaz / **N pencereye çoğaltır** |
| `exactly_once` | `exactly_once_v2` | Task başına producer / **instance başına** |
| ELR | Ek replika | **Muhasebe kaydı** / veri kopyası değil |

---

## 4. Refleks tablosu — şikâyet → ilk üç bakış

| Şikâyet | Bak |
|---|---|
| "Mesaj kayboldu" | `acks` · `min.insync.replicas` · **producer callback** · commit sırası |
| "İki kez işlendi" | Commit sırası · idempotence · rebalance logu |
| "Rebalance durmuyor" | `max.poll.records × işleme süresi` · `group.instance.id` · pod restart |
| "Consumer ilerlemiyor, rebalance yok" | **Zehirli kayıt** · **açık transaction (LSO)** · aşağı akış yavaşlığı |
| "Producer yavaş" | `send().get()` var mı · `records-per-request-avg` · `buffer-available-bytes` |
| "Disk doldu" | `retention.ms/bytes` · **`segment.ms`** (aktif segment) · compaction geride mi |
| "Bir broker CPU yiyor" | **Lider dengesizliği** · sıcak key · TLS |
| "Partition offline" | Eksik broker'ı **geri getir** (unclean son çare) |
| "Metadata işlemleri durdu" | **Controller quorum** çoğunluğu |
| "Streams açılmıyor" | **Restore** (state.dir kalıcı mı, standby var mı) |
| "Streams sonuç üretmiyor" | **Stream time** ilerliyor mu · `suppress` · cache |
| "Connector RUNNING ama veri yok" | **Task listesi** · topic offsetleri · **`plugin.path`** |

---

## 5. Kritik komutlar

```bash
# Sağlık
kafka-metadata-quorum.sh --bootstrap-controller kafka-1:9093 describe --status
kafka-topics.sh --bootstrap-server kafka-1:19092 --describe --under-replicated-partitions
kafka-topics.sh --bootstrap-server kafka-1:19092 --describe --unavailable-partitions

# Gruplar
kafka-consumer-groups.sh --bootstrap-server kafka-1:19092 --describe --all-groups
kafka-consumer-groups.sh --bootstrap-server kafka-1:19092 --group g --reset-offsets \
  --to-datetime 2026-08-01T00:00:00.000 --topic t --execute        # grup BOŞ olmalı
kafka-share-groups.sh --bootstrap-server kafka-1:19092 --describe --group sg

# Yapılandırma
kafka-configs.sh --bootstrap-server kafka-1:19092 --entity-type topics --entity-name t \
  --alter --add-config retention.ms=3600000,segment.ms=60000
kafka-features.sh --bootstrap-server kafka-1:19092 describe

# Log incelemesi
kafka-dump-log.sh --files /var/lib/kafka/data/t-0/00000000000000000000.log --print-data-log

# Dengeleme / taşıma
kafka-leader-election.sh --bootstrap-server kafka-1:19092 --election-type preferred --all-topic-partitions
kafka-reassign-partitions.sh --bootstrap-server kafka-1:19092 \
  --reassignment-json-file plan.json --execute --throttle 10485760
kafka-reassign-partitions.sh --bootstrap-server kafka-1:19092 \
  --reassignment-json-file plan.json --verify        # THROTTLE'I KALDIRAN ADIM

# Streams
kafka-streams-application-reset.sh --bootstrap-server kafka-1:19092 \
  --application-id app --input-topics t             # + yerel state.dir'i SİL

# Ölçüm (4.2+: --command-property / --num-records)
kafka-producer-perf-test.sh --topic t --num-records 300000 --record-size 512 --throughput -1 \
  --command-property bootstrap.servers=kafka-1:19092 --command-property acks=all
```

---

## 6. Ezberlenecek formüller

```
replication.factor − min.insync.replicas          = yazmaya devam ederek tolere edilen broker kaybı
max.poll.records × işleme_süresi  < max.poll.interval.ms          (güvenlik payıyla)
partition_sayısı                  = max(kapasite, paralellik, broker_sayısı) × pay
raft quorum                       = (N/2)+1        → 3 voter: 1 kayıp, 5 voter: 2 kayıp
depolama                          = msg/sn × boyut × 86400 × sıkıştırma × replication.factor × gün + %30
hopping pencere çoğaltma          = pencere_boyu / adım
coordinator                       = hash(group.id) % offsets.topic.num.partitions (50)
commit edilecek offset            = son_islenen_offset + 1
```

---

## 7. Kafka 4.x'te değişenler (sürüm eşleştirme)

| Sürüm | Değişiklik |
|---|---|
| **4.0** (Mar 2025) | ZooKeeper **kaldırıldı** · KIP-848 **GA** (client opt-in) · KIP-896 eski protokoller kaldırıldı · Java 11 (client) / 17 (broker) · KIP-1030 varsayılanlar (**`linger.ms=5`**) · **TV2** varsayılan · KIP-932 erken erişim · ELR deneysel |
| **4.1** (Eyl 2025) | Share groups **önizleme** · Streams rebalance protokolü (KIP-1071) erken erişim · **ELR varsayılan açık** · `Consumer.close(CloseOptions)` |
| **4.2** (Şub 2026) | Share groups **üretime hazır** (+`RENEW`) · Streams rebalance protokolü GA (sınırlı) · **Streams DLQ** · KIP-1147 **CLI parametreleri** · KIP-1100 metrik adları · Java 25 |
| **4.3** (May 2026) | Broker cordoning (KIP-1066) · tiered storage iyileştirmeleri · state store header desteği · `streams-scala` **deprecated** · klasik rebalance protokolüne uyarı |

---

## 8. Üretim kontrol listesi (tek bakış)

| Katman | Kontrol |
|---|---|
| Topic | replication.factor **3** · `min.insync.replicas` **2** · `unclean.leader.election.enable=false` |
| Producer | `acks=all` · idempotence · `delivery.timeout.ms` · **callback'te DLQ + metrik** |
| Consumer | `enable.auto.commit=false` · işle-sonra-commit · `onPartitionsRevoked` commit · `group.instance.id` |
| Protokol | `group.protocol=consumer` (KIP-848) |
| Streams | Kalıcı `state.dir` · standby · **DLQ** · uncaught handler · `replication.factor` |
| Connect | `errors.tolerance=all` + DLQ · doğru **`plugin.path`** · sink lag izleme |
| Güvenlik | `SASL_SSL` · `StandardAuthorizer` · **Group:Read** · `allow.everyone...=false` · kota |
| İzleme | ActiveControllerCount(=1) · OfflinePartitions(0) · UnderMinIsr(0) · **partition başına lag** |
| Kapasite | Disk < **%75** · `broker.rack` · n-1 bakım payı |

---

➡️ **[son-tekrar.md](son-tekrar.md)** — sınavdan/mülakattan 24 saat önce
➡️ **[genel-sinav-1.html](genel-sinav-1.html)** · **[genel-sinav-2.html](genel-sinav-2.html)**
