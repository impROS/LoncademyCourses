# 00.3 — Ayar rehberi: hangi ayar, ne zaman, nasıl düşünülür

> **Alan:** Başvuru dosyası — baştan sona okunmaz, **ihtiyaç anında** açılır
> **İlgili:** [`02-kavram-sozlugu.md`](02-kavram-sozlugu.md) (terimlerin kısa tanımı) ·
> [`../99-final/cheatsheet.md`](../99-final/cheatsheet.md) (komutlar ve sayılar)

---

## Bu dosya nasıl kullanılır

Kavram sözlüğü *"bu ne demek?"* sorusunu cevaplar. Bu dosya **üç ayrı soruyu** cevaplar:

1. **Ne yapar?** — ayarın işlevi
2. **Ne zaman dokunulur?** — hangi belirtiyi görünce bu ayar akla gelir
3. **Nasıl düşünülür?** — değeri seçerken hangi büyüklüğü hesaba katarsın

> ⚠️ **Kural: ölçmeden ayar değiştirme.** Bu setin lab'larında iki kez ölçtük ve iki kez
> "mantıklı görünen" ayar **hiçbir şey değiştirmedi** (`max.partition.fetch.bytes` 10 katına
> çıktı, kazanç 0) ya da **zarar verdi** (`fetch.min.bytes=1MB`, %40 yavaşlama).
> Ayar değiştirmek bir hipotez testidir; önce **darboğazı** bul.

---

## 1. ⭐ Karar çerçevesi: bir ayara dokunmadan önce beş soru

| # | Soru | Neden önemli |
|---|---|---|
| 1 | **Hangi katman?** producer · broker · consumer | Yanlış katmanda ayar aramak en yaygın zaman kaybı |
| 2 | **Hangi eksen?** dayanıklılık · gecikme · verim · maliyet | Her ayar bu dörtgende bir yerden alıp bir yere verir |
| 3 | **Darboğazın orada olduğunu ölçtün mü?** | Ölçmeden değiştirmek kör atıştır |
| 4 | **Bu ayarın kardeşi var mı?** | `acks` tek başına anlamsız (`min.insync.replicas` şart); `linger.ms` tek başına yarım (`batch.size`) |
| 5 | **Geri alınabilir mi?** | Partition sayısı, `application.id`, feature seviyesi — **geri alınamaz** |

> **Hafıza kancası — üçgen:** *Dayanıklılık ↔ Gecikme ↔ Verim.* Kafka'da bedava öğle yemeği
> yoktur; sadece **kimin ödeyeceğini** seçersin: producer mı bekler, consumer mı bekler,
> yoksa disk mi şişer.

---

## 2. ⭐ Karar reçeteleri — senaryodan ayar setine

Aşağıdakiler **başlangıç noktalarıdır**, kopyala-yapıştır reçete değil. Her biri ölçülerek
kendi ortamına uyarlanmalıdır.

### Reçete A — Maksimum dayanıklılık (finansal/kritik veri)

```properties
# producer
acks=all
enable.idempotence=true                 # 4.x'te zaten varsayılan
max.in.flight.requests.per.connection=5
delivery.timeout.ms=120000
# topic / broker
replication.factor=3
min.insync.replicas=2
unclean.leader.election.enable=false    # varsayılan
# consumer
enable.auto.commit=false                # işledikten SONRA elle commit
isolation.level=read_committed          # transaction kullanılıyorsa
```
**Bedeli:** p99 gecikme artar (lab'da `acks=all`, `acks=1`'in **4 katı**), bir broker kaybında
yazma devam eder ama ikincisinde durur.

### Reçete B — Maksimum verim (toplu iş, analitik akış)

```properties
# producer
linger.ms=50
batch.size=65536
compression.type=lz4                    # ağ darboğazsa zstd
buffer.memory=67108864
# consumer
max.poll.records=2000                   # işleme süresiyle çarpımına dikkat!
fetch.min.bytes=1                       # geriden okumada artırma (lab: %40 yavaşlattı)
```
**Kazanç (lab ölçümü):** `linger=0,batch=16K` → `linger=50,batch=64K,lz4`:
**72.081 → 201.342 kayıt/sn** ve p99 **1201 ms → 91 ms**. Doygun sistemde batching
gecikmeyi de **düşürür**.

### Reçete C — Düşük gecikme (istek-yanıt, kullanıcıyı bekleten akış)

```properties
# producer
linger.ms=0                             # ⚠ yalnızca sistem DOYGUN DEĞİLSE doğru
batch.size=16384
compression.type=none                   # CPU'yu gecikmeye çevirme
# consumer
fetch.min.bytes=1
fetch.max.wait.ms=100
max.poll.records=100
```
> ⚠️ **En sık hata:** Yoğun bir sistemde `linger.ms=0` yapmak. İstek sayısı patlar, istekler
> broker kuyruğunda bekler ve gecikme **artar**. Kural: *doygun sistemde batch'le, boş sistemde
> batch'leme.*

### Reçete D — Kubernetes'te kesintisiz tüketici

```properties
group.instance.id=worker-<pod-ordinal>  # StatefulSet ordinal'i; UUID DEĞİL
group.protocol=consumer                 # KIP-848; yoksa CooperativeStickyAssignor
max.poll.records=<işleme süresine göre>
enable.auto.commit=false
```
Ve dağıtım tarafında: StatefulSet + `maxUnavailable: 1` + `terminationGracePeriodSeconds` >
işleme süresi + `consumer.close()` ile düzgün kapanış.

### Reçete E — Streams üretim tabanı

```properties
state.dir=/var/lib/streams/<uygulama>   # /tmp DEĞİL
num.standby.replicas=1                  # büyük store'larda
processing.guarantee=at_least_once      # EOS bilinçli bir karar olmalı
errors.dead.letter.queue.topic.name=<uygulama>-dlq
deserialization.exception.handler=...LogAndContinueExceptionHandler
processing.exception.handler=...LogAndContinueProcessingExceptionHandler
replication.factor=3
```

---

## 3. Producer ayarları

### 3.1 Dayanıklılık

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| [`acks`](02-kavram-sozlugu.md#acks) | `all` | Kaç kopyanın onayı beklenir | Neredeyse hiç | `all` = **ISR'deki tümü**. `min.insync.replicas` olmadan anlamı yok. `1` iki dünyanın kötüsü |
| `enable.idempotence` | `true` | Retry kaynaklı duplicate'i önler | Kapatma | Kapatırsan `acks`/`retries` uyumsuzluğu **başlangıçta** hata verir |
| `max.in.flight.requests.per.connection` | 5 | Cevabı beklenmeden uçan istek | 5'te bırak | Idempotence açıkken **5'e kadar sıra korunur**. `>5` client'ı başlatmaz |
| `transactional.id` | — | Producer'ın **kalıcı** kimliği | EOS gerekiyorsa | **Benzersiz + kararlı** olmalı; iki instance aynı id'yi kullanırsa birbirini fence'ler |
| `transaction.timeout.ms` | 60000 | Transaction bu sürede bitmezse abort | Uzun batch işlerinde | Büyütmek `read_committed` tüketicileri **LSO'da bekletir** |

### 3.2 Zaman bütçesi

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| [`delivery.timeout.ms`](02-kavram-sozlugu.md#deliverytimeoutms) | 120000 | Kaydın **toplam** teslimat bütçesi | Hata hızını iş gereksinimine bağlarken | **Asıl sınır budur**, `retries` değil. `≥ linger.ms + request.timeout.ms` |
| `request.timeout.ms` | 30000 | **Tek** isteğin cevap bekleme süresi | Yüksek gecikmeli WAN ([veri merkezleri arası ağ ↗](02-kavram-sozlugu.md#wan))'da | Broker'ın en kötü cevap süresinden büyük olmalı |
| `max.block.ms` | 60000 | Tampon dolu/metadata yokken `send()` bloklama süresi | Fail-fast isterken **düşür** | Büyütmek semptomu gizler: uygulama **sessizce donar** |
| `retries` | `Integer.MAX_VALUE` | Deneme sayısı | Pratikte **hiç** | Anlamsızdır; süre bütçesi önce dolar. `retries=0` idempotence'ı kırar |
| `retry.backoff.ms` | 100 | Denemeler arası bekleme (üstel artışlı) | Nadiren | Lider seçimi ~saniyeler sürer; çok küçük değer gereksiz yük |

### 3.3 Verim ve bellek

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| [`linger.ms`](02-kavram-sozlugu.md#lingerms) | **5** (4.0 öncesi 0) | Batch dolsun diye beklenen süre | Verim/gecikme dengesini kurarken | Doygun sistemde **gecikmeyi de düşürür**; boş sistemde doğrudan ekler |
| [`batch.size`](02-kavram-sozlugu.md#batchsize) | 16384 | **Partition başına** batch bayt tavanı | `batch-size-avg` tavanı vuruyorsa | `linger.ms` ile **birlikte** ayarlanır. Bellek: `aktif_partition × batch.size` |
| `compression.type` | `none` | Batch'in tamamını sıkıştırır | Ağ/disk darboğazsa | `lz4` = CPU dostu, `zstd` = bayt dostu. `compression-rate-avg > 0.9` ise **kapat** |
| `buffer.memory` | 33554432 (32 MiB) | Toplam tampon | Çok partition'a yazıyorsan | `aktif_partition × batch.size × 2–3`. Dolunca **backpressure** başlar |
| `max.request.size` | 1048576 (1 MiB) | Tek isteğin tavanı | Büyük mesaj varsa | **Üç yerde birden**: producer + broker `message.max.bytes` + topic `max.message.bytes` |
| `partitioner.class` | (yok) | Key→partition kararı | Sıcak key varsa | Varsayılan: key varsa `murmur2 % N`, yoksa **sticky** |
| `partitioner.ignore.keys` | `false` | Key olsa bile sticky davran | Sıra gerekmiyorsa | **Sıra garantisini feda eder** — bilinçli bir karar olmalı |

> ⭐ **Producer'da üç grup ayar vardır:** *dayanıklılık*, *zaman bütçesi*, *verim*.
> Bir sorunu çözerken önce **hangi gruba dokunduğunu** söyle.

---

## 4. Consumer ayarları

### 4.1 Fetch ve poll

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| [`max.poll.records`](02-kavram-sozlugu.md#maxpollrecords) | 500 | Tek `poll()`'un döndüreceği kayıt | **İşleme yavaşsa ilk dokunacağın ayar** | `max.poll.records × işleme_süresi < max.poll.interval.ms`, **%25 pay bırak** |
| [`max.poll.interval.ms`](02-kavram-sozlugu.md#maxpollintervalms) | 300000 | İki `poll()` arası izin verilen süre | En **son** çare | Büyütmek gerçekten ölen consumer'ın tespitini de geciktirir |
| `fetch.min.bytes` | 1 | Broker cevap vermeden önce biriktireceği veri | **Seyrek trafikte** broker'ı rahatlatmak için | Geriden okumada **zarar verir** (lab: %40 yavaşlama) |
| `fetch.max.wait.ms` | 500 | `fetch.min.bytes` dolmazsa azami bekleme | `fetch.min.bytes` ile birlikte | `fetch.min.bytes` artırmanın gecikme maliyeti en kötü bu kadardır |
| `fetch.max.bytes` | 52428800 (50 MiB) | Tek fetch cevabının **toplam** tavanı | Nadiren | Bellek: consumer bu kadarını tutabilmeli |
| `max.partition.fetch.bytes` | 1048576 (1 MiB) | **Partition başına** cevap tavanı | Darboğaz ağ ise | Lab'da 10× artırmak **hiçbir şey değiştirmedi** — önce ölç |
| `auto.offset.reset` | `latest` | Offset yokken/geçersizken ne yapılır | **Bilinçli seçilmeli** | `latest` = sessizce atlar (en sinsi kayıp), `earliest` = duplicate seli, `none` = dur ve haber ver |

### 4.2 Grup ve rebalance

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| [`group.protocol`](02-kavram-sozlugu.md#groupprotocol) | `classic` | Klasik mi KIP-848 mi | Rolling deploy'da durma yaşıyorsan | `consumer` = broker tarafı atama, barrier yok. Client'ta **opt-in** |
| [`group.instance.id`](02-kavram-sozlugu.md#groupinstanceid) | (yok) | Üyeye **kalıcı kimlik** (static membership) | Pod restart'ları rebalance üretiyorsa | **Kararlı** olmalı (pod ordinal'i). UUID **işe yaramaz** |
| [`session.timeout.ms`](02-kavram-sozlugu.md#sessiontimeoutms) | 45000 | Heartbeat gelmezse ölü sayma süresi | Static membership ile birlikte | "Süreç **yaşıyor** mu". Yeni protokolde **broker** belirler |
| `heartbeat.interval.ms` | 3000 | Heartbeat sıklığı | Nadiren | `session.timeout.ms`'in ~1/3'ü |
| `partition.assignment.strategy` | `[Range, CooperativeSticky]` | Klasik protokolde atama | Eager davranış görüyorsan | Tüm üyelerde **aynı** olmalı; yoksa grup hiç kurulmaz |

### 4.3 Commit ve izolasyon

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `enable.auto.commit` | `true` | `poll()` içinde otomatik commit | İşi **poll döngüsü dışına** atıyorsan **kapat** | Açıkken ve iş kuyruğa atılıyorsa **veri kaybettirir** |
| `auto.commit.interval.ms` | 5000 | Otomatik commit periyodu | Nadiren | Çöküşte bu kadarlık iş **yeniden** işlenir |
| [`isolation.level`](02-kavram-sozlugu.md#isolationlevel) | `read_uncommitted` | Abort edilenler görünsün mü | Transaction'lı topic okuyorsan **`read_committed`** | `read_committed` seni **LSO'da bekletebilir** |

> ⭐ **Consumer'ın en kritik denklemi:**
> `max.poll.records × kayıt_başına_işleme_süresi < max.poll.interval.ms`
> Üretimdeki 1 numaralı consumer arızası bu eşitsizliğin bozulmasıdır.

---

## 5. Broker ve topic ayarları

### 5.1 Replikasyon ve dayanıklılık

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `replication.factor` (topic) | broker `default.replication.factor` | Kaç kopya tutulur | **Her zaman 3** | `replication.factor − min.insync.replicas` = yazmaya devam ederek tolere edilen kayıp |
| [`min.insync.replicas`](02-kavram-sozlugu.md#mininsyncreplicas) | **1** ⚠️ | `acks=all` için gereken asgari güncel kopya | **Mutlaka 2 yap** (replication.factor=3 ile) | Varsayılan 1 bırakılırsa `acks=all` **sahte güvenliktir** |
| [`unclean.leader.election.enable`](02-kavram-sozlugu.md#uncleanleaderelectionenable) | `false` | ISR dışı replika lider olabilsin mi | **Asla** (son çare) | `true` = commit edilmiş **veriyi silmeyi kabul etmek** |
| [`replica.lag.time.max.ms`](02-kavram-sozlugu.md#replicalagtimemaxms) | 30000 | ISR'de kalma süre kriteri | **Alarmı susturmak için dokunma** | Büyütmek geride kalmış replikayı "senkron" sayar → **risk artar** |
| `num.replica.fetchers` | 1 | Replikasyon fetcher thread sayısı | ISR sürekli daralıyorsa | Yüksek trafikli cluster'da klasik çözüm |
| `replica.fetch.max.bytes` | 1048576 | Fetch başına replikasyon baytı | Büyük mesajlarda | `message.max.bytes`'tan küçük olmamalı |
| `broker.rack` | (yok) | Broker'ın rack/AZ'si | **Bulutta ilk yapılacak ayar** | Replikalar farklı AZ'lere dağılır → AZ kaybına dayanıklılık |
| `auto.leader.rebalance.enable` | `true` | Liderliği tercih edilene geri taşı | Nadiren | `leader.imbalance.check.interval.seconds` (300) — restart'tan 5 dk sonra oturur |

### 5.2 Depolama, retention, compaction

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| [`cleanup.policy`](02-kavram-sozlugu.md#cleanuppolicy) | `delete` | Silme politikası | Durum tablosu ise `compact` | `delete` = **zaman**, `compact` = **kimlik (key)** |
| `retention.ms` | 604800000 (7 gün) | Yaş sınırı | Tüketici gecikmesine göre | "En yavaş tüketicinin gecikmesi + rahat bir pay" |
| `retention.bytes` | -1 | **Partition başına** boyut sınırı | Disk sınırı koyarken | `partition × retention.bytes × replication.factor` = gerçek disk |
| [`segment.bytes`](02-kavram-sozlugu.md#segmentms--segmentbytes) | 1 GiB (min 1 MiB) | Segment dosya boyutu | Nadiren | Küçültmek = çok dosya = **yavaş broker açılışı** |
| `segment.ms` | 7 gün (min 1 dk) | Segmentin zorla dönme süresi | **Az trafikli topic'te retention çalışmıyorsa** | Aktif segment silinmez; dönmeden silinemez |
| `segment.index.bytes` | 10 MiB | Index dosya tavanı | `segment.bytes` değiştirirken **birlikte** | **Time index 12 bayt/girdi** — çoğu zaman ilk dolan budur |
| `index.interval.bytes` | 4096 | Index girdi sıklığı | Nadiren | Küçültmek: index büyür, tarama kısalır |
| `min.cleanable.dirty.ratio` | 0.5 | Compaction eşiği | Compaction geç kalıyorsa | Düşürmek = daha sık temizlik = daha çok CPU/IO |
| `delete.retention.ms` | 86400000 (24 saat) | Tombstone ömrü | **Yavaş tüketicin varsa artır** | En yavaş tüketicinin gecikmesinden **büyük** olmalı |
| `min.compaction.lag.ms` / `max.compaction.lag.ms` | 0 / sınırsız (min 60 s) | Temizlik zaman sınırları | Uyum gereksinimi varsa | `max.compaction.lag.ms` "en geç ne zaman silinsin" |
| `log.cleaner.dedupe.buffer.size` | 128 MiB | Cleaner'ın key hash haritası | **Çok benzersiz key'li** compacted topic'te | Yetersizse temizlik **yarım kalır**, disk düşmez |
| `log.retention.check.interval.ms` | 300000 (5 dk) | Retention tarama periyodu | Nadiren | Silme **anında** olmaz; bu periyodu bekler |
| `message.max.bytes` (broker) / `max.message.bytes` (topic) | ~1 MiB | Kabul edilen azami kayıt | Büyük mesaj varsa | Producer `max.request.size` ile **birlikte** |

### 5.3 Tiered storage

| Ayar | Varsayılan | Ne yapar | Nasıl düşünürsün |
|---|---|---|---|
| `remote.log.storage.system.enable` (broker) | `false` | Ana anahtar | Kapalıysa topic ayarı reddedilir |
| `remote.storage.enable` (topic) | `false` | Topic'i tiered yapar | `cleanup.policy=compact` topic'lerde **desteklenmez** |
| `local.retention.ms` / `local.retention.bytes` | -2 (= `retention`) | **Yerel** saklama | Her zaman toplamın **alt kümesi** |
| `remote.log.reader.threads` | 10 | Uzaktan okuma paralelliği | Bu bir **izolasyon** ayarıdır; büyütmek replay'i hızlandırır ama canlı trafikten çalar |
| `remote.log.manager.copier.thread.pool.size` | 10 | Yükleme paralelliği | Yükleme geride kalıyorsa |

### 5.4 Kaynak ve doygunluk

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| **JVM heap** | — | Broker belleği | **Küçük tut: ~6 GB** | Kafka veriyi heap'te tutmaz; RAM **page cache'in** |
| `num.io.threads` | 8 | İstek işleyici thread'leri | `RequestHandlerAvgIdlePercent < 0.3` ise | Disk/istek doygunluğu |
| `num.network.threads` | 3 | Ağ thread'leri | `NetworkProcessorAvgIdlePercent < 0.3` ise | Ağ doygunluğu |
| `queued.max.requests` | 500 | İstek kuyruğu | Nadiren | Büyütmek gecikmeyi gizler, çözmez |
| `socket.send/receive.buffer.bytes` | ~100 KiB | Soket tamponları | Yüksek gecikmeli WAN'da | BDP (bant genişliği × gecikme) hesabı |
| `log.dirs` | — | Veri dizin(ler)i | JBOD ([RAID’siz, her disk ayrı log.dirs ↗](02-kavram-sozlugu.md#jbod)) kurarken | Birden çok disk = birden çok bağımsız arıza alanı |

### 5.5 KRaft ve metadata

| Ayar | Varsayılan | Ne yapar | Nasıl düşünürsün |
|---|---|---|---|
| `process.roles` | — | `broker`, `controller` ya da ikisi | Combined **üretim için önerilmez**: broker sorunu quorum'u (karar için gereken salt çoğunluk) düşürür |
| `controller.quorum.voters` | — | Statik voter (oy veren controller düğümü) listesi | Voter sayısı **tek** olmalı: 3 (kayıp toleransı 1) ya da 5 (2) |
| `metadata.version` (feature) | sürüme göre | Metadata kayıt formatı | **Önce binary, sonra feature.** Geri alınamayabilir |
| `kraft.version` (feature) | 0 | 1 = dinamik voter kümesi (KIP-853) | 0'da controller eklemek tüm node'ların restart'ını ister |

### 5.6 Güvenlik ve kota

| Ayar | Varsayılan | Ne yapar | Nasıl düşünürsün |
|---|---|---|---|
| `listener.security.protocol.map` | — | Listener → protokol | Üretim: **`SASL_SSL`**. `SASL_PLAINTEXT` parolayı **düz metin** taşır |
| `authorizer.class.name` | (yok) | Yetkilendirme | KRaft'ta `StandardAuthorizer` |
| `allow.everyone.if.no.acl.found` | `false` | ACL'siz kaynağa herkes erişsin mi | `true` = yetkilendirmeyi **pratikte kapatmak**. Açılır, kapatılması unutulur |
| `super.users` | (boş) | ACL kontrolünü atlayanlar | Operasyon hesapları için; **uygulamalar için asla** |
| `producer_byte_rate` / `consumer_byte_rate` | (yok) | Bant genişliği kotası | Kafka **reddetmez, geciktirir** — throttle (reddetmeden yavaşlatma) metriğini panoya koy |
| `request_percentage` | (yok) | CPU zamanı payı | Gürültülü komşuyu susturur |

---

## 6. Kafka Streams ayarları

| Ayar | Varsayılan | Ne yapar | Ne zaman dokunursun | Nasıl düşünürsün |
|---|---|---|---|---|
| `application.id` | — | **Consumer group id'si** ve iç topic öneki | **Değiştirme** | Değiştirmek = yeni uygulama: offset sıfır, yeni iç topic'ler, durum kaybı |
| [`state.dir`](02-kavram-sozlugu.md#statedir) | `/tmp/kafka-streams` ⚠️ | Yerel durum dizini | **Üretimde mutlaka değiştir** | `/tmp` temizlenirse **her restart tam restore** |
| `num.stream.threads` | 1 | Instance başına thread | CPU boşsa ve **task varsa** | Tavan **task sayısı**; fazlası atıl oturur |
| [`num.standby.replicas`](02-kavram-sozlugu.md#numstandbyreplicas) | 0 | Yedek durum kopyası | Durum büyük + kesinti toleransı düşükse | **Hız** içindir, dayanıklılık için değil (o changelog'da). Disk 2× |
| [`processing.guarantee`](02-kavram-sozlugu.md#processingguarantee) | `at_least_once` | Garanti seviyesi | EOS gerekiyorsa `exactly_once_v2` | EOS'ta `commit.interval.ms` **100**'e düşer; aşağı akış `read_committed` olmalı |
| `commit.interval.ms` | 30000 (EOS'ta 100) | Offset + state commit periyodu | EOS'ta maliyeti düşürmek için | EOS'ta bu aynı zamanda **aşağı akışın görünürlük gecikmesidir** |
| `statestore.cache.max.bytes` | 10 MiB | Ara sonuçları birleştiren önbellek | **Testte 0 yap** | Açıkken ara çıktılar gelmez — "testte çıktı yok"un 1 numaralı sebebi |
| `default.timestamp.extractor` | `FailOnInvalidTimestamp` | Olay zamanı kaynağı | Zaman damgası bozuksa | Varsayılan uygulamayı **durdurur**; bu bilinçlidir |
| `deserialization.exception.handler` | `LogAndFail` | Okuma hatası politikası | `LogAndContinue` + **DLQ** | Tek başına `LogAndContinue` = **sessiz veri kaybı** |
| `processing.exception.handler` | `LogAndFail` | İşleme hatası (KIP-1033) | Aynı | Katmanı hata mesajından ayırt et |
| `errors.dead.letter.queue.topic.name` | `null` | Yerleşik DLQ (KIP-1034) | **Üretimde her zaman** | Streams'teki tek en değerli üretim ayarı. Topic'i sen oluştur, **izle** |
| `replication.factor` (Streams iç topic'leri) | -1 (broker varsayılanı) | Changelog/repartition replication.factor'si | Üretimde **≥ 3** | Durum yedeğinin dayanıklılığı buna bağlı |
| `probing.rebalance.interval.ms` | 600000 | Warm-up replikası yakınsama kontrolü | Ölçekleme yavaşsa | Task, durum hazır olana kadar devredilmez |

> ⚠️ **RocksDB belleği JVM heap'inin dışındadır.** Konteyner limitini `-Xmx`'e göre değil
> `heap + off-heap + %20` olarak belirle; aksi hâlde **OOMKilled ([konteyner bellek limitini aştı ↗](02-kavram-sozlugu.md#oom))** alırsın (6.2).

---

## 7. Kafka Connect ayarları

### 7.1 Worker

| Ayar | Varsayılan | Ne yapar | Nasıl düşünürsün |
|---|---|---|---|
| [`plugin.path`](02-kavram-sozlugu.md#pluginpath) | (yok) | Eklenti dizini | **Kafka'nın `libs` dizinini gösterme** — yavaş tarama + sessiz classloader çakışması |
| `key.converter` / `value.converter` | — | İç model ↔ bayt | `JsonConverter` + `schemas.enable=true` mesajları **şişirir** |
| `config.storage.topic` | — | Connector yapılandırmaları | **1 partition zorunlu** (toplam sıra) |
| `offset.storage.topic` | — | **Source** offsetleri | 25 partition tipik |
| `status.storage.topic` | — | Durum | 5 partition tipik |
| `offset.flush.interval.ms` | 60000 | Source offset yazma periyodu | Küçültmek duplicate penceresini daraltır, yükü artırır |

### 7.2 Connector

| Ayar | Varsayılan | Ne yapar | Nasıl düşünürsün |
|---|---|---|---|
| [`tasks.max`](02-kavram-sozlugu.md#tasksmax) | 1 | Task **üst sınırı** | Gerçek sayıyı connector belirler; `/status`'tan **doğrula** |
| [`errors.tolerance`](02-kavram-sozlugu.md#errorstolerance) | `none` | Hatalı kayıt atlansın mı | `all` + **DLQ** birlikte kullanılır |
| `errors.deadletterqueue.topic.name` | — | Sink DLQ | Üretim reçetesi: `tolerance=all` + DLQ + `context.headers.enable=true` |
| `errors.retry.timeout` | 0 | Yeniden deneme süresi | -1 = sınırsız (dikkat) |
| `errors.log.include.messages` | `false` | Log'a kayıt içeriği | ⚠️ **PII riski** |
| `transforms` / `predicates` | — | SMT zinciri | SMT **durumsuz ve hızlı** olmalı; dış çağrı yapma |

### 7.3 MirrorMaker 2

| Ayar | Varsayılan | Ne yapar | Nasıl düşünürsün |
|---|---|---|---|
| `<k>-><h>.topics` | — | Replike edilecek topic regex'i | Fazla geniş regex iç topic'leri de kopyalayabilir |
| `<k>-><h>.groups` | — | Offset çevrimi yapılacak gruplar | `sync.group.offsets.enabled=true` olmadan **yazılmaz** |
| `sync.group.offsets.interval.seconds` | 60 | Offset yazma periyodu | RPO'nun bir bileşeni |
| `refresh.groups.interval.seconds` | **600 (10 dk)** ⚠️ | Grup listesi tarama periyodu | Lab'da yakalanan tuzak: grup hedefte **hiç görünmedi**; 10 sn'ye çekince belirdi |
| `refresh.topics.interval.seconds` | 600 | Topic listesi tarama | Yeni topic'in replike edilmesi bu kadar gecikebilir |
| `replication.policy.class` | `DefaultReplicationPolicy` | Hedef topic adlandırma | `Identity...` ön eki kaldırır ama **döngü korumasını da** kaldırır |

---

## 8. ⚠️ Tehlikeli ayarlar — dokunmadan önce iki kez düşün

| Ayar | Neden tehlikeli | Doğru refleks |
|---|---|---|
| `unclean.leader.election.enable=true` | **Commit edilmiş veriyi silmeyi kabul etmek** | Önce broker'ı geri getir |
| `min.insync.replicas=1` | `acks=all`'ı **sahte güvenliğe** çevirir | replication.factor=3 ile **2** |
| `allow.everyone.if.no.acl.found=true` | Yetkilendirmeyi pratikte kapatır | Önce ACL'leri yaz, sonra authorizer'ı aç |
| `replica.lag.time.max.ms` ↑ | Alarmı susturur, **riski büyütür** | Sebebi (disk/ağ/GC) düzelt |
| `max.poll.interval.ms` ↑ | Ölen consumer'ın tespitini geciktirir | Önce `max.poll.records` ↓ |
| `max.block.ms` ↑ | Uygulama **sessizce donar** | Broker tarafına bak |
| `retries=0` | Geçici hatayı kalıcıya çevirir + idempotence'ı kırar | `delivery.timeout.ms` ↓ |
| Partition sayısı ↑ | **Geri alınamaz**; key→partition eşlemesi bozulur | Baştan cömert seç |
| `application.id` değişimi | Yeni uygulama: offset sıfır, durum kaybı | `kafka-streams-application-reset.sh` |
| Broker heap ↑ (32 GB) | Page cache'i çalar, GC duraklamaları uzar | ~6 GB'da bırak |
| `--throttle`'sız reassignment | Canlı trafiği **ezer** | `--throttle` + `--verify` |

---

## 9. Ayarı nereye yazarsın? Öncelik sırası

Kafka'da aynı ayar birden çok yerde tanımlanabilir. **Dar olan kazanır:**

```
topic ayarı  >  dinamik broker ayarı (per-broker)  >  dinamik cluster ayarı  >  statik broker ayarı (server.properties)
```

```bash
# Topic ayarı (en dar, en tercih edilen)
kafka-configs.sh --bootstrap-server kafka-1:19092 --entity-type topics --entity-name T \
  --alter --add-config retention.ms=3600000

# Dinamik broker ayarı (restart gerektirmez)
kafka-configs.sh --bootstrap-server kafka-1:19092 --entity-type brokers --entity-name 1 \
  --alter --add-config num.io.threads=16

# Cluster geneli dinamik (entity-default)
kafka-configs.sh --bootstrap-server kafka-1:19092 --entity-type brokers --entity-default \
  --alter --add-config log.retention.ms=259200000

# Hangi ayar nereden geliyor? (kaynağı da gösterir)
kafka-configs.sh --bootstrap-server kafka-1:19092 --entity-type topics --entity-name T --describe --all
```

> ⭐ **`--describe --all` çıktısındaki `sensitive`/`source` kolonlarına bak:** bir ayarın
> `DEFAULT_CONFIG` mi `DYNAMIC_TOPIC_CONFIG` mi olduğunu görmek, "neden bu değer?" sorusunu
> saniyeler içinde bitirir.

**Client ayarları** uygulamanın kendi yapılandırmasındadır ve broker'dan **ezilemez** — bu yüzden
`acks`, `linger.ms`, `max.poll.records` gibi ayarlar operasyon ekibinin değil **uygulama
ekibinin** sorumluluğundadır. Cluster tarafında yapabileceğin tek şey **kota** koymaktır.

---

## 10. Varsayılanı bilmiyorsan: cluster'a sor

Bu setin lab'ında bunun için hazır araçlar var:

```bash
# Broker'ın gerçek etkin ayarları
docker exec kafka-1 /opt/kafka/bin/kafka-configs.sh --bootstrap-server kafka-1:19092 \
  --entity-type brokers --entity-name 1 --describe --all | sort

# Client (producer/consumer) varsayılanları — koddan
cd lab && mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.common.DumpDefaults

# Streams varsayılanları
cd lab && mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.common.DumpStreamsDefaults
```

> **Bu alışkanlığı edin:** İnternetteki Kafka içeriğinin büyük kısmı 2.x/3.x dönemine aittir ve
> **varsayılanlar değişti** (`linger.ms` 0 → 5, `acks` 1 → all, `enable.idempotence` false → true).
> Bir sayıyı ezberlemek yerine **kendi cluster'ına sormayı** ezberle.

---

⬅️ [Başlangıca dön](00-genel-bakis-ve-trickler.md) · 📖 [Kavram sözlüğü](02-kavram-sozlugu.md) ·
📋 [Cheatsheet](../99-final/cheatsheet.md)
