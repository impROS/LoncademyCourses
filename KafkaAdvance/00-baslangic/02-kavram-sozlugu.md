# 00.3 — Kavram sözlüğü (kısaltmalar, KIP'ler, ayarlar)

> **Bu dosya baştan sona okunmak için değil, dönülmek için var.**
> Konu dosyalarında bir kısaltma ya da KIP numarası ilk geçtiğinde yanında kısa bir açıklama ve
> buraya bir bağlantı bulacaksın. Takıldığın yerde tıkla, oku, geri dön.

**Nasıl kullanılır:** Her terim burada **kendi başlığı** altında tanımlı; konu dosyasındaki
`↗` bağlantısı doğrudan o başlığa atlar (IDE'lerin markdown önizlemesi dahil).
Konu dosyasında şuna benzer bir ifade görürsün:

> …ISR dışı replikalar için **ELR** ([ISR dışı ama lider olmaya uygun replikalar ↗](#elr)) devreye girer.

Parantez içindeki kısa açıklama sana anlık cevabı verir; **↗** bağlantısı buradaki tam tanıma
götürür. Her tanımın sonunda **hangi konuda derinlemesine işlendiği** yazar
(örnek: → [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)).

---

## 1. Kısaltmalar ve temel kavramlar

<a id="backpressure"></a>
#### Backpressure
— Aşağı akış yetişemediğinde üst akışın **bilerek yavaşlatılması**. Kafka producer'da
tampon dolunca `send()` bloklar; bu bir hata değil, tasarlanmış bir fren mekanizmasıdır.
→ [2.1](../02-producer/2.1-accumulator-ve-batching.md)

<a id="changelog"></a>
#### Changelog
— Kafka Streams'te bir state store'un **kalıcı yedeği** olan, `compact` politikalı iç topic.
Yerel disk kaybolsa da durum buradan geri yüklenir; **gerçek veri kaynağı budur**.
→ [6.2](../06-streams/6.2-state-store-ve-changelog.md)

<a id="co-partitioning"></a>
#### Co-partitioning
— İki topic'in join edilebilmesi için **aynı partition sayısına ve aynı
partitioning stratejisine** sahip olması şartı. Aynı key iki tarafta da aynı task'a düşmelidir.
→ [6.3](../06-streams/6.3-join-ve-windowing.md)

<a id="controller"></a>
#### Controller (active controller)
— Cluster metadata'sını **yazan** tek node; KRaft quorum'unun
lideridir. Topic oluşturma, lider seçimi, ISR değişimi onun kararıdır. *Consumer group
[coordinator](#coordinator-group-coordinator)'ından farklıdır.* → [1.3](../01-broker-depolama/1.3-kraft-metadata.md)

<a id="coordinator"></a>
#### Coordinator (group coordinator)
— Bir consumer group'un üyeliğini ve offsetlerini yöneten
**broker**. `hash(group.id) % 50` ile seçilen `__consumer_offsets` partition'ının lideridir.
Transaction'lar için ayrıca *transaction coordinator* vardır. → [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

<a id="converter"></a>
#### Converter
— Kafka Connect'te **bayt ↔ Connect iç veri modeli** dönüşümünü yapan bileşen
(`StringConverter`, `JsonConverter`, `AvroConverter`). Serializer/deserializer'ın Connect'teki karşılığı.
→ [7.1](../07-connect/7.1-connect-mimarisi.md)

<a id="dlq"></a>
#### DLQ
*(Dead Letter Queue — ölü mektup kuyruğu)* — İşlenemeyen kayıtların atıldığı ayrı topic.
"Durma ama kaybetme" dengesini kurar; kaydı sessizce atmak yerine incelenebilir bir yere yazar.
→ [6.4](../06-streams/6.4-eos-hata-ve-test.md), [7.1](../07-connect/7.1-connect-mimarisi.md)

<a id="dsl"></a>
#### DSL
*(Domain Specific Language — alana özgü dil)* — Kafka Streams'in yüksek seviyeli API'si
(`filter`, `map`, `groupBy`, `join`). Altındaki düşük seviyeli API **Processor API**'dir.
DSL yazdığın kod, arka planda bir **topoloji** üretir.
→ [6.1](../06-streams/6.1-topoloji-ve-task-modeli.md)

<a id="elr"></a>
#### ELR
*(Eligible Leader Replicas — lider olmaya uygun replikalar)* — ISR'den düşmüş **ama verisi
hâlâ güvenilir** replikaların listesi. ISR boşaldığında veri kaybetmeden lider seçilebilmesini sağlar.
Yeni bir kopya **oluşturmaz**, sadece "kim güvenle lider olabilir" muhasebesidir.
Kafka 4.1'den beri varsayılan açık (yalnızca KRaft). → [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md) · [KIP-966](#kip-966)

<a id="eos"></a>
#### EOS
*(Exactly-Once Semantics — tam bir kez semantiği)* — Kafka'dan okuyup Kafka'ya yazan bir
işlemde **yazma ve offset commit'inin atomik** olması. **Yalnızca Kafka içinde geçerlidir**;
veritabanı/API çağrıları kapsam dışıdır. → [4.1](../04-eos-transaction/4.1-transactions-internals.md)

<a id="grace-period"></a>
#### Grace period
— Kafka Streams'te bir pencerenin **kapandıktan sonra** geç gelen kayıtları hâlâ
kabul ettiği tolerans süresi. Süre dolduktan sonra gelen kayıtlar **sessizce atılır**.
→ [6.3](../06-streams/6.3-join-ve-windowing.md)

<a id="hw"></a>
#### HW
*(High Watermark — yüksek su seviyesi)* — [ISR](#isr)'deki **tüm** replikaların ulaştığı en
yüksek offset. Consumer yalnızca HW'nin altını okuyabilir; üstü henüz "commit edilmemiş" sayılır.
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

<a id="idempotence"></a>
#### Idempotence (idempotent producer)
— Aynı batch'in ağ yeniden denemesi (retry) yüzünden iki kez
yazılmasını engelleyen mekanizma. `(producerId, partition, sequence)` üçlüsüne dayanır ve
**yalnızca tek producer oturumu** içinde geçerlidir. → [2.2](../02-producer/2.2-idempotence-ve-siralama.md)

<a id="isr"></a>
#### ISR
*(In-Sync Replicas — senkron replikalar)* — Lidere yeterince yakın olan replikalar kümesi
(lider dahil). `acks=all` "tüm replikalar" değil **"ISR'deki tüm replikalar"** demektir.
Üyelik kriteri tek: `replica.lag.time.max.ms` (30 s) içinde liderin sonuna değmek.
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

<a id="jbod"></a>
#### JBOD
*(Just a Bunch Of Disks — “sadece bir yığın disk”)* — RAID kullanmadan, her diski **ayrı bir
`log.dirs` girdisi** olarak Kafka'ya vermek. Avantajı: RAID katmanının yazma maliyeti yok.
Bedeli: bir disk bozulunca yalnız oradaki partition'lar etkilenir — replikasyon onları kurtarır.
→ [5.1](../05-operasyon/5.1-performans-tuning.md)

<a id="jit"></a>
#### JIT
*(Just-In-Time derleme)* — JVM'in sık çalışan kodu makine koduna çevirmesi. İlk saniyelerde kod
**yavaştır**; bu yüzden her ölçümde **ısınma turu** atılır, yoksa ilk koşum haksız yere kötü çıkar.
→ [5.1](../05-operasyon/5.1-performans-tuning.md)



<a id="jmx"></a>
#### JMX
*(Java Management Extensions)* — JVM uygulamalarının metriklerini dışarı verdiği standart
arayüz. Kafka broker metriklerinin tamamı buradan okunur (`JmxTool`, ya da üretimde
Prometheus **JMX Exporter** java agent'ı). → [5.2](../05-operasyon/5.2-metrikler-ve-izleme.md)

<a id="kraft"></a>
#### KRaft
*(Kafka Raft)* — Kafka'nın **kendi içindeki** Raft uygulaması; cluster metadata'sını
`__cluster_metadata` adlı tek partition'lık log'da tutar. Kafka 4.0 ile ZooKeeper tamamen kaldırıldı.
Ayrı bir servis **değildir**. → [1.3](../01-broker-depolama/1.3-kraft-metadata.md)

<a id="leo"></a>
#### LEO
*(Log End Offset)* — **Tek bir replikanın** yazdığı son kaydın bir sonraki offset'i.
[HW](#hw) ise ISR'in ortak asgarisidir. → [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

<a id="lsn"></a>
#### LSN
*(Log Sequence Number)* — Veritabanının kendi write-ahead log'undaki konum numarası. CDC tabanlı
bir source connector "nerede kaldım" bilgisini bununla saklar — Kafka offset'i değil, **dış
sistemin offset'i**. → [7.1](../07-connect/7.1-connect-mimarisi.md)

<a id="lso"></a>
#### LSO
*(Last Stable Offset — son kararlı offset)* — `read_committed` okuyucuların okuyabildiği
tavan; en eski **açık transaction**'ın başlangıcıdır. Uzun süren bir transaction LSO'yu dondurur ve
tüketicileri bekletir. → [4.1](../04-eos-transaction/4.1-transactions-internals.md)



<a id="oom"></a>
#### OOM / OOMKilled
*(Out Of Memory)* — Bellek tükenmesi. **OOMKilled**, konteynerin bellek limitini aştığı için
çekirdek tarafından öldürülmesidir. Streams'te klasik sebep: **RocksDB belleği heap dışıdır**,
konteyner limiti `-Xmx`'e göre belirlenirse aşılır.
→ [6.2](../06-streams/6.2-state-store-ve-changelog.md)

<a id="page-cache"></a>
#### Page cache
— İşletim sisteminin dosya önbelleği. Kafka **kendi önbelleğini tutmaz**, page
cache'i kullanır; bu yüzden broker JVM heap'i küçük (~6 GB), RAM'in kalanı page cache'e bırakılır.
→ [1.1](../01-broker-depolama/1.1-log-segment-ve-index.md)

<a id="pid"></a>
#### PID
*(Producer ID)* — Broker'ın idempotent producer'a verdiği **oturumluk** kimlik.
Uygulama yeniden başlayınca **yeni bir PID** alınır; kalıcı kimlik istiyorsan
[`transactional.id`](#transactionalid) gerekir. → [2.2](../02-producer/2.2-idempotence-ve-siralama.md)

<a id="predicate"></a>
#### Predicate
— Kafka Connect'te bir [SMT](#smt)'nin **koşullu** çalışmasını sağlayan kural
(`TopicNameMatches`, `HasHeaderKey`, `RecordIsTombstone`). → [7.2](../07-connect/7.2-connector-smt-ve-mm2.md)

<a id="pvc"></a>
#### PVC
*(PersistentVolumeClaim — Kubernetes kalıcı disk talebi)* — Pod yeniden başladığında **silinmeyen**
disk. Streams'te `state.dir` bunun üzerinde olmalıdır; `emptyDir` ile her restart **tam restore**
demektir. → [6.2](../06-streams/6.2-state-store-ve-changelog.md)

<a id="quorum"></a>
#### Quorum (çoğunluk)
— KRaft'ta metadata yazabilmek için gereken **kesin çoğunluk**: `(N/2)+1`.
3 voter → 1 kayıp, 5 voter → 2 kayıp tolere edilir. [ISR](#isr)'den farkı: quorum **pazarlık kabul etmez**.
→ [1.3](../01-broker-depolama/1.3-kraft-metadata.md)

<a id="rebalance"></a>
#### Rebalance
— Consumer group üyeleri değiştiğinde partition'ların yeniden dağıtılması.
*Eager* rebalance'ta herkes her şeyi bırakır; *cooperative* (artımlı) rebalance'ta yalnızca
taşınacaklar geri alınır. → [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

<a id="repartition"></a>
#### Repartition topic
— Kafka Streams'te key değiştiğinde veriyi yeni key'e göre dağıtmak için
otomatik oluşturulan **geçici** iç topic (`delete` politikalı). [Changelog](#changelog) ile
karıştırma: o `compact`'tır ve durum yedeğidir. → [6.1](../06-streams/6.1-topoloji-ve-task-modeli.md)


<a id="rpc"></a>
#### RPC
*(Remote Procedure Call — uzak yordam çağrısı)* — Bir sunucunun başka bir sunucudaki işlevi ağ
üzerinden çağırması. ZooKeeper döneminde metadata değişiklikleri broker'lara **tek tek RPC ile**
yayılırdı; KRaft'ta broker'lar log'u kendileri **fetch eder**.
→ [1.3](../01-broker-depolama/1.3-kraft-metadata.md)

<a id="rpo-rto"></a>
#### RPO / RTO
— *Recovery Point Objective*: felaket anında kabul edilen **veri kaybı penceresi**.
*Recovery Time Objective*: kabul edilen **kesinti süresi**. MirrorMaker 2 asenkron olduğu için
RPO sıfır **değildir**. → [7.2](../07-connect/7.2-connector-smt-ve-mm2.md)

<a id="rtt"></a>
#### RTT
*(Round-Trip Time — gidiş-dönüş süresi)* — Bir isteğin gidip cevabının dönmesi için geçen süre.
`send().get()` gibi senkron bir kalıpta throughput tavanı `1 / RTT`'dir — 1 ms RTT ile saniyede
~1000 kayıt. → [2.1](../02-producer/2.1-accumulator-ve-batching.md)

<a id="sasl"></a>
#### SASL
*(Simple Authentication and Security Layer)* — Kimlik doğrulama **çerçevesi**; hangi yöntemin
kullanılacağını *mekanizma* belirler ([SCRAM](#scram), `PLAIN`, `GSSAPI`/Kerberos, `OAUTHBEARER`).
Taşıma güvenliğinden **ayrıdır**: `SASL_PLAINTEXT` kimliği doğrular ama **şifrelemez**;
üretim standardı **`SASL_SSL`**'dir. → [5.4](../05-operasyon/5.4-guvenlik.md)

<a id="scram"></a>
#### SCRAM
*(Salted Challenge Response Authentication Mechanism)* — Parolanın ağda **düz metin gitmediği**
SASL mekanizması; parolalar hash'li olarak cluster metadata'sında saklanır ve `kafka-configs.sh`
ile yönetilir. Vanilla Kafka'da önerilen seçimdir (`SCRAM-SHA-512`).
→ [5.4](../05-operasyon/5.4-guvenlik.md)

<a id="share-group"></a>
#### Share group
— Kafka'nın **kuyruk modu**: partition yerine **kayıt** başına dağıtım ve onay.
Tüketici sayısı partition sayısıyla sınırlı değildir, ama **sıra garantisi yoktur**.
Kafka 4.2'de üretime hazır ilan edildi. → [3.4](../03-consumer/3.4-share-groups.md) · [KIP-932](#kip-932)

<a id="sifir-kopya"></a>
#### Sıfır kopya
*(zero-copy, `sendfile`)* — Veriyi çekirdek alanından **doğrudan sokete** aktarma;
JVM heap'ine kopyalanmaz. **TLS açıkken devre dışı kalır** — TLS sonrası CPU artışının sebebi budur.
→ [1.1](../01-broker-depolama/1.1-log-segment-ve-index.md)

<a id="smt"></a>
#### SMT
*(Single Message Transform — tekil mesaj dönüşümü)* — Kafka Connect'te kayıt başına çalışan,
**durumsuz ve hafif** dönüşüm (maskeleme, header ekleme, topic yönlendirme). Join/agregasyon
gerekiyorsa yanlış araçtır — orası Kafka Streams'in işidir. → [7.2](../07-connect/7.2-connector-smt-ve-mm2.md)

<a id="standby"></a>
#### Standby replica
— Kafka Streams'te bir task'ın durumunun **başka bir instance'ta da** canlı
tutulması. Failover süresini dakikalardan saniyelere indirir; bedeli 2× disk ve sürekli changelog okuma.
→ [6.2](../06-streams/6.2-state-store-ve-changelog.md)

<a id="sticky-partitioning"></a>
#### Sticky partitioning
— Key'i olmayan kayıtlarda producer'ın **batch dolana kadar aynı
partition'a** yazması. Eski round-robin her batch'i tek kayıtlık bırakıyordu; sticky ile batch'ler dolar.
→ [2.1](../02-producer/2.1-accumulator-ve-batching.md)

<a id="stream-time"></a>
#### Stream time
— Kafka Streams'te "şimdi"nin tanımı: o task'ın gördüğü **en büyük olay zamanı**.
Duvar saati değildir; **veri akmazsa zaman durur** ve pencereler kapanmaz.
→ [6.3](../06-streams/6.3-join-ve-windowing.md)

<a id="task"></a>
#### Task
 · *Streams:* Kafka Streams'te paralelliğin **atomik birimi**: `alt topoloji × partition`.
Task sayısı ölçekleme tavanıdır; thread eklemek task sayısını artırmaz.
→ [6.1](../06-streams/6.1-topoloji-ve-task-modeli.md)
*Connect:* Connector'ın ürettiği, asıl veriyi taşıyan çalışan birim.
→ [7.1](../07-connect/7.1-connect-mimarisi.md)

<a id="tombstone"></a>
#### Tombstone (mezar taşı)
— `value == null` olan kayıt; compacted bir topic'te **"bu key silindi"**
anlamına gelir. Eski sürümleri temizletir, kendisi `delete.retention.ms` (24 saat) kadar yaşar.
→ [1.4](../01-broker-depolama/1.4-retention-ve-compaction.md)

<a id="transactional-id"></a>
#### `transactional.id`
— Producer'ın **kalıcı** kimliği (sen verirsin). Yeniden başlatmada
coordinator eski oturumu bulur, epoch'u artırır ve zombi producer'ı **fence**'ler.
[PID](#pid)'den farkı: PID oturumluk ve otomatiktir. → [4.1](../04-eos-transaction/4.1-transactions-internals.md)

<a id="ttl"></a>
#### TTL
*(Time To Live — yaşam süresi)* — Bir kaydın ne kadar süre saklanacağı. Tekilleştirme tablosunda
kritik: TTL, olası duplicate penceresinden **kısa olmamalıdır**, yoksa eski bir duplicate
"yeni" sanılır. → [4.2](../04-eos-transaction/4.2-outbox-ve-idempotent-tuketici.md)

<a id="tv2"></a>
#### TV2
*(Transaction Version 2)* — Transaction protokolünün ikinci nesli. Coordinator **her
transaction'ın sonunda producer epoch'unu artırır**; böylece zombi producer bir sonraki
transaction'a mesaj "sızdıramaz". Kafka 4.0'dan beri varsayılan.
→ [4.1](../04-eos-transaction/4.1-transactions-internals.md) · [KIP-890](#kip-890)

<a id="wan"></a>
#### WAN
*(Wide Area Network — geniş alan ağı)* — Veri merkezleri/bölgeler arası ağ. Yüksek gecikmesi
nedeniyle soket tamponları (`socket.send/receive.buffer.bytes`) ve `request.timeout.ms` burada
ayarlanır. → [5.5](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md)

<a id="worker"></a>
#### Worker (Connect)
— Connect task'larını çalıştıran JVM süreci. *Connector* bir **yapılandırma**,
*task* çalışan iş birimi, *worker* onları barındıran süreçtir.
→ [7.1](../07-connect/7.1-connect-mimarisi.md)

---

## 2. KIP'ler

> **KIP** = *Kafka Improvement Proposal*. Kafka'ya eklenen her önemli özellik önce numaralı bir
> öneri olarak yazılır, tartışılır, oylanır. Bir özelliğin **hangi sürümde ve neden** geldiğini
> anlamak için KIP numarası en kısa yoldur. Tamamı: <https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Improvement+Proposals>

<a id="kip-405"></a>
#### KIP-405
**Tiered storage** · Eski log segmentlerini nesne depolamaya (S3/GCS/HDFS) taşır;
saklama ihtiyacını hesaplama ihtiyacından ayırır. 3.6 erken erişim, **3.9 üretime hazır**.
→ [1.5](../01-broker-depolama/1.5-tiered-storage.md)

<a id="kip-750"></a>
#### KIP-750
**Java 8 desteğinin kaldırılması** · Kafka 4.0'dan itibaren client/Streams **Java 11+**,
broker/Connect **Java 17+** ister. → [00.1](00-genel-bakis-ve-trickler.md)

<a id="kip-794"></a>
#### KIP-794
**Adaptif partitioner** · Producer'ın **daha hızlı cevap veren broker'lara** daha çok
yazması (`partitioner.adaptive.partitioning.enable`, varsayılan açık).
→ [2.1](../02-producer/2.1-accumulator-ve-batching.md)

<a id="kip-848"></a>
#### KIP-848
**Yeni consumer grup protokolü** · Partition atamasını consumer'dan alıp **broker'a**
verir; "herkes durur, yeniden dağıtılır" barrier'ını kaldırır, değişiklikler heartbeat'lerle
kademeli akar. Kafka 4.0'da GA; **broker'da varsayılan destekli ama client'ta opt-in**
(`group.protocol=consumer`). → [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

<a id="kip-853"></a>
#### KIP-853
**Dinamik KRaft quorum'u** · Controller'ların çalışırken eklenip çıkarılabilmesi
(`kraft.version=1`). Statik `controller.quorum.voters` listesine mahkûm kalmayı bitirir.
→ [1.3](../01-broker-depolama/1.3-kraft-metadata.md)

<a id="kip-890"></a>
#### KIP-890
**Transactions server-side defense** · [TV2](#tv2)'yi getirir: her transaction sonunda
epoch artar, böylece geç kalmış bir producer'ın mesajı bir sonraki transaction'a **sızamaz**.
Kafka 4.0'dan beri varsayılan. → [4.1](../04-eos-transaction/4.1-transactions-internals.md)

<a id="kip-896"></a>
#### KIP-896
**Eski protokol sürümlerinin kaldırılması** · Kafka 4.0 ile 2.1'den eski client/broker
protokolleri desteklenmiyor. Çok eski client'lar bağlanamaz. → [00.1](00-genel-bakis-ve-trickler.md)

<a id="kip-932"></a>
#### KIP-932
**Queues for Kafka ([share group](#share-group))** · Kayıt başına dağıtım ve onay
(ACCEPT/RELEASE/REJECT), partition sayısından bağımsız tüketici sayısı. 4.0 erken erişim,
4.1 önizleme, **4.2 üretime hazır**. → [3.4](../03-consumer/3.4-share-groups.md)

<a id="kip-950"></a>
#### KIP-950
**Tiered storage disablement** · Tiered storage'ın topic bazında **kapatılabilmesi** (3.9).
→ [1.5](../01-broker-depolama/1.5-tiered-storage.md)

<a id="kip-956"></a>
#### KIP-956
**Tiered storage kotaları** · Uzak depoya yükleme/indirme hızına üst sınır koyar; ağır
replay'in canlı trafiği ezmesini engeller (3.9). → [1.5](../01-broker-depolama/1.5-tiered-storage.md)

<a id="kip-966"></a>
#### KIP-966
**[ELR](#elr) (Eligible Leader Replicas)** · "Last replica standing" problemini çözer:
ISR boşalsa bile verisi güvenilir lider adaylarını bilir. 4.0 deneysel, **4.1'den beri varsayılan açık**.
Ayrıca HW'nin ancak ISR ≥ `min.insync.replicas` iken ilerlemesini şart koşar.
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

<a id="kip-1005"></a>
#### KIP-1005
**Tiered offset metrikleri** · `EarliestLocalOffset` ve `TieredOffset`'i dışa vurur (3.9).
→ [1.5](../01-broker-depolama/1.5-tiered-storage.md)

<a id="kip-1030"></a>
#### KIP-1030
**Varsayılan değerlerin ve sınırların değiştirilmesi** · Kafka 4.0 ile gelen ayar
değişiklikleri paketi. **En çok hissedileni: `linger.ms` 0 → 5.** Ayrıca
`num.recovery.threads.per.data.dir` 1 → 2, `log.message.timestamp.after.max.ms` sınırsız → 1 saat,
ve saçma küçük değerlere alt sınır: `segment.bytes` **min 1 MiB**, `segment.ms` **min 1 dakika**,
`segment.index.bytes` min 1 KiB. → [2.1](../02-producer/2.1-accumulator-ve-batching.md), [1.1](../01-broker-depolama/1.1-log-segment-ve-index.md)

<a id="kip-1033"></a>
#### KIP-1033
**Streams processing exception handler** · Deserialization ve production arasındaki
boşluğu doldurur: **kendi processor kodunda** fırlayan istisnalar için ayrı bir handler
(`processing.exception.handler`). → [6.4](../06-streams/6.4-eos-hata-ve-test.md)

<a id="kip-1034"></a>
#### KIP-1034
**Kafka Streams'te [DLQ](#dlq)** · Tek bir ayarla
(`errors.dead.letter.queue.topic.name`) yerleşik hata handler'larının bozuk kayıtları ölü mektup
topic'ine yazması. → [6.4](../06-streams/6.4-eos-hata-ve-test.md)

<a id="kip-1066"></a>
#### KIP-1066
**Broker/log dizini cordoning** · Ölçek küçültme ve bakım için broker'ların
"yeni partition alma" durumundan çıkarılabilmesi (`cordoned.log.dirs`, 4.3).
→ [00.1](00-genel-bakis-ve-trickler.md)

<a id="kip-1071"></a>
#### KIP-1071
**Streams rebalance protokolü** · Streams task atamasını [KIP-848](#kip-848) üzerine
kurup broker tarafına taşır. 4.1 erken erişim, 4.2'de sınırlı özellikle GA.
→ [6.1](../06-streams/6.1-topoloji-ve-task-modeli.md)

<a id="kip-1100"></a>
#### KIP-1100
**Metrik adlandırmasının düzeltilmesi** · Metrikleri `kafka.COMPONENT` konvansiyonuna
uydurur; bazı eski client metrik adları deprecate edildi (4.2). **Yükseltmede panolar kırılabilir.**
→ [5.2](../05-operasyon/5.2-metrikler-ve-izleme.md)

<a id="kip-1147"></a>
#### KIP-1147
**CLI argümanlarının standartlaştırılması** · `--producer-props` → `--command-property`,
`--messages` → `--num-records` gibi değişiklikler (4.2). Eskiler çalışır ama uyarı verir.
→ [5.1](../05-operasyon/5.1-performans-tuning.md)

<a id="kip-1259"></a>
#### KIP-1259
**Streams state dizini temizliği** · Kullanılmayan eski state dizinlerinin otomatik
silinmesi (`state.cleanup.dir.max.age.ms`, 4.3). → [6.2](../06-streams/6.2-state-store-ve-changelog.md)

---

## 3. Sık geçen ayarlar

> Tam varsayılan listesi için [`99-final/cheatsheet.md`](../99-final/cheatsheet.md) bölüm 1.
> Burada yalnızca **adından ne yaptığı anlaşılmayanlar** var.

### Producer

<a id="linger-ms"></a>
#### `linger.ms`
*(varsayılan **5**)* — Producer'ın, batch'i doldurmak için göndermeden önce
**bekleyeceği süre**. 0 ise kayıt gelir gelmez gönderilir (küçük batch, çok istek); büyütmek
batch'leri doldurur, throughput'u ve sıkıştırma oranını artırır, karşılığında **gecikme ekler**.
⚠️ Doygun bir sistemde batch'lemek gecikmeyi **düşürebilir** (broker kuyruğu erir).
Kafka 4.0'da 0'dan 5'e çıkarıldı ([KIP-1030](#kip-1030)).
→ [2.1](../02-producer/2.1-accumulator-ve-batching.md), [5.1](../05-operasyon/5.1-performans-tuning.md)

<a id="batch-size"></a>
#### `batch.size`
*(16 KiB)* — Tek bir batch'in bayt tavanı. **Partition başınadır**: 100 partition'a
yazan producer 100 ayrı batch tutar. → [2.1](../02-producer/2.1-accumulator-ve-batching.md)

<a id="acks"></a>
#### `acks`
*(all)* — Yazmanın "başarılı" sayılması için kaç kopyanın onayı beklenir.
`0` hiç, `1` yalnız lider, **`all` [ISR](#isr)'deki tümü**. → [2.3](../02-producer/2.3-teslimat-garantileri.md)

<a id="delivery-timeout-ms"></a>
#### `delivery.timeout.ms`
*(120 s)* — Bir kaydın tampona girmesinden nihai sonuca kadar tanınan
**toplam süre**. `retries` varsayılanı sınırsıza yakın olduğu için **asıl retry sınırı budur**.
→ [2.2](../02-producer/2.2-idempotence-ve-siralama.md)

<a id="max-in-flight"></a>
#### `max.in.flight.requests.per.connection`
*(5)* — Cevabı beklenmeden yola çıkabilecek istek sayısı.
[Idempotence](#idempotence-idempotent-producer) açıkken 5'e kadar **sıra korunur**; "sıra için 1 yap" tavsiyesi 3.0
öncesine aittir. → [2.2](../02-producer/2.2-idempotence-ve-siralama.md)

### Consumer

<a id="max-poll-records"></a>
#### `max.poll.records`
*(500)* — Tek `poll()` çağrısının döndüreceği **kayıt sayısı**.
`fetch.*` ayarlarıyla karıştırma: onlar **ağdan kaç bayt** geleceğini belirler.
→ [3.1](../03-consumer/3.1-fetch-ve-poll-dongusu.md)

<a id="max-poll-interval-ms"></a>
#### `max.poll.interval.ms`
*(5 dk)* — İki `poll()` arasında izin verilen en uzun süre.
Aşılırsa üye gruptan atılır → [rebalance](#rebalance). *"Süreç **ilerliyor mu**"* sorusudur.
→ [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

<a id="session-timeout-ms"></a>
#### `session.timeout.ms`
*(45 s)* — Heartbeat gelmezse üyenin ölü sayılma süresi.
*"Süreç **yaşıyor mu**"* sorusudur; yukarıdakiyle karıştırılır.
→ [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

<a id="isolation-level"></a>
#### `isolation.level`
*(read_uncommitted)* — `read_committed` yapılırsa abort edilmiş
transaction kayıtları **filtrelenir**. [EOS](#eos) zincirinin okuyan ucu budur.
→ [4.1](../04-eos-transaction/4.1-transactions-internals.md)

<a id="group-protocol"></a>
#### `group.protocol`
*(classic)* — `consumer` yapılırsa [KIP-848](#kip-848) protokolü kullanılır.
**Broker destekliyor olsa bile client'ta açıkça verilmelidir.**
→ [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

<a id="group-instance-id"></a>
#### `group.instance.id`
— Üyeye **kalıcı kimlik** verir (static membership); restart'ta rebalance
tetiklenmez. Değer **kararlı** olmalı (pod ordinal'i gibi), her açılışta üretilen UUID işe yaramaz.
→ [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

### Broker / topic

<a id="replicationfactor"></a>
#### `replication.factor`
— Bir partition'ın kaç kopyasının tutulacağı; topic oluşturulurken verilir. `replication.factor=3`
= her partition üç ayrı broker'da. Kaç kopya **var** olduğunu söyler; kaçının **güncel** olması
gerektiğini `min.insync.replicas` söyler. Üretim standardı 3'tür ve sonradan `--alter` ile
değiştirilemez (`kafka-reassign-partitions.sh` gerekir).
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md) · [5.5](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md)

<a id="min-insync-replicas"></a>
#### `min.insync.replicas`
*(broker varsayılanı **1**)* — `acks=all` yazmalarının kabul edilmesi için
[ISR](#isr)'de bulunması gereken asgari replika sayısı. **Değiştirilmezse `acks=all` sahte güvenliktir.**
Kural: `replication.factor − min.insync.replicas` = yazmaya devam ederek tolere edilen broker kaybı.
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

<a id="unclean-leader-election"></a>
#### `unclean.leader.election.enable`
*(false)* — `true` yapılırsa ISR dışı, **geride kalmış** bir
replika lider olabilir → partition açılır ama **commit edilmiş veri kaybolabilir**.
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

<a id="cleanup-policy"></a>
#### `cleanup.policy`
*(delete)* — `delete` **zamana/boyuta** göre siler; `compact` her key'in
**son değerini** korur; `compact,delete` ikisini birden uygular.
→ [1.4](../01-broker-depolama/1.4-retention-ve-compaction.md)

<a id="segment-ms"></a>
#### `segment.ms` / `segment.bytes`
*(7 gün / 1 GiB)* — Aktif segmentin ne zaman kapatılacağı.
**Aktif segment silinmez**; az trafikli topic'te "retention çalışmıyor" şikâyetinin çözümü
`segment.ms`'i düşürmektir (min 1 dakika, [KIP-1030](#kip-1030)).
→ [1.1](../01-broker-depolama/1.1-log-segment-ve-index.md), [1.4](../01-broker-depolama/1.4-retention-ve-compaction.md)

<a id="replica-lag-time-max-ms"></a>
#### `replica.lag.time.max.ms`
*(30 s)* — Bir follower'ın [ISR](#isr)'de kalabilmesi için liderin
sonuna değmesi gereken azami süre. Büyütmek **alarmı susturur, riski büyütür**.
→ [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md)

### Streams

<a id="processing-guarantee"></a>
#### `processing.guarantee`
*(at_least_once)* — `exactly_once_v2` yapılırsa çıktı + changelog +
offset **tek transaction'da** yazılır. `commit.interval.ms` varsayılanı da 30 s'den **100 ms**'e düşer.
→ [6.4](../06-streams/6.4-eos-hata-ve-test.md)

<a id="state-dir"></a>
#### `state.dir`
*(/tmp/kafka-streams)* — Yerel RocksDB durumunun tutulduğu dizin.
⚠️ **Üretimde mutlaka kalıcı bir yola alınmalı**; `/tmp` temizlenirse her restart tam restore olur.
→ [6.2](../06-streams/6.2-state-store-ve-changelog.md)

<a id="num-standby-replicas"></a>
#### `num.standby.replicas`
*(0)* — Kaç [standby](#standby-replica) kopya tutulacağı. Dayanıklılık zaten
changelog'dadır; bu ayar **failover hızı** içindir. → [6.2](../06-streams/6.2-state-store-ve-changelog.md)

### Connect

<a id="plugin-path"></a>
#### `plugin.path`
— Connector/SMT jar'larının bulunduğu **üst dizin**. ⚠️ Kafka'nın kendi `libs`
dizinini göstermek yavaş tarama ve **sessiz classloader çakışmaları** üretir (task RUNNING görünür
ama veri akmaz). → [7.1](../07-connect/7.1-connect-mimarisi.md)

<a id="errors-tolerance"></a>
#### `errors.tolerance`
*(none)* — `all` yapılırsa hatalı kayıtlar atlanır. **Tek başına kullanma**:
`errors.deadletterqueue.topic.name` ile eşleştir, yoksa kayıtlar sessizce kaybolur.
→ [7.1](../07-connect/7.1-connect-mimarisi.md)

<a id="tasks-max"></a>
#### `tasks.max`
— Bir connector'ın üretebileceği task sayısının **üst sınırı** (hedefi değil).
FileStreamSource her zaman 1 üretir; sink'ler partition sayısına kadar çıkar.
→ [7.1](../07-connect/7.1-connect-mimarisi.md)

---

## Sırada ne var
➡️ [`03-ayar-rehberi.md`](03-ayar-rehberi.md) — burada tanımını okuduğun ayarın **ne zaman ve nasıl** seçileceği
➡️ [`../01-broker-depolama/1.1-log-segment-ve-index.md`](../01-broker-depolama/1.1-log-segment-ve-index.md)
