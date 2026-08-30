# 07 · Kafka Connect — Kendini kontrol cevapları

> Bu dosya [7.1](7.1-connect-mimarisi.md) ve [7.2](7.2-connector-smt-ve-mm2.md) konularının
> sonundaki **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [7.1](#71-connect-mimarisi) · [7.2](#72-connector-smt-ve-mm2)

---

## 7.1 Connect mimarisi

📄 Sorular: [`7.1-connect-mimarisi.md`](7.1-connect-mimarisi.md)

### Soru 1 — Source ve sink offsetleri nerede, **neyin** offset'i olarak saklanır?

**Kısa cevap:**

| | **Source connector** | **Sink connector** |
|---|---|---|
| Neyin offset'i | **Dış sistemdeki konum** (dosya baytı, DB satır id'si, WAL LSN ([veritabanı WAL konum numarası ↗](../00-baslangic/02-kavram-sozlugu.md#lsn)), API cursor'ı) | **Kafka offset'i** |
| Nerede saklanır | `offset.storage.topic` (distributed) ya da `offset.storage.file.filename` (standalone) | **`__consumer_offsets`** — normal bir consumer group! |
| Grup adı | — | `connect-<connector-adı>` |

**Ayrıntı:**

- **Bu ayrım Connect'in en çok karıştırılan yeridir.** Source connector Kafka'ya **yazar**, yani
  Kafka offset'i onun için anlamsızdır; hatırlaması gereken şey **dış sistemde nerede kaldığıdır**.
  Sink connector Kafka'dan **okur**, yani sıradan bir consumer'dır.
- **Sink connector bir consumer group'tur** ve bu, çok pratik bir sonuç doğurur:
  ```bash
  kafka-consumer-groups.sh --describe --group connect-dosya-hedef
  ```
  ile lag'ini görebilirsin. Rebalance, `max.poll.interval.ms`, static membership — 3.2'deki
  her şey geçerlidir.
- **Source offset'i bir anahtar-değer çiftidir:** `sourcePartition` ("iş biriminin kimliği") ve
  `sourceOffset` ("nerede kaldım"):
  ```java
  new SourceRecord(
      Map.of("dosya", filename),        // sourcePartition
      Map.of("konum", byteOffset),      // sourceOffset
      topic, null, Schema.STRING_SCHEMA, line);
  ```
- **Lab kanıtı:** Dosyaya `satir-4` eklenip connector yeniden çalıştırıldığında topic offset'i
  **3 → 4** oldu; ilk üç satır **tekrar yazılmadı**. Offset dosyası (`/tmp/connect.offsets`)
  dış sistemdeki konumu hatırladı.
- REST API üzerinden ikisini de okuyabilirsin: `GET /connectors/<ad>/offsets`.

> 📌 **Sık yapılan hata:** Bir source connector'ın "başa dönmesini" `kafka-consumer-groups.sh
> --reset-offsets` ile denemek. O offset orada değildir; source offset'i `offset.storage.topic`'te
> ya da dosyadadır ve `DELETE /connectors/<ad>/offsets` ile sıfırlanır.

🔗 Konu: [7.1 §3](7.1-connect-mimarisi.md)

---

### Soru 2 — `tasks.max=10` ama tek task oluştu: neden?

**Kısa cevap:** `tasks.max` bir **üst sınırdır**, bir hedef değil. Kaç task üretileceğine
**connector'ın kendisi** karar verir ve bu, işin doğal bölünebilirliğine bağlıdır.

**Ayrıntı — connector tipine göre:**

| Connector | Gerçek task sayısı | Neden |
|---|---|---|
| `FileStreamSourceConnector` | **Her zaman 1** | Bir dosya bölünemez |
| JDBC source | Tablo (ya da sorgu) başına 1 | Her tablo bağımsız bir iş birimi |
| Sink connector'lar | En fazla **partition sayısı** | Bir partition tek bir task'a atanır (consumer group kuralı, 3.2) |
| Debezium | Genelde 1 (tek WAL akışı) | Sıra korunmalı |

- **Sink tarafında ikinci sınır:** `tasks.max=10` versen ama topic 3 partition'lıysa **3 task**
  oluşur; 7 tanesi zaten oluşmaz. Bu, "partition'dan fazla consumer" kuralının aynısıdır.
- **Kontrol:** `GET /connectors/<ad>/status` çıktısındaki `tasks` dizisine bak — kaç task var,
  hangi worker'da:
  ```json
  {"name":"dosya-kaynak","connector":{"state":"RUNNING",...},
   "tasks":[{"id":0,"state":"RUNNING","worker_id":"172.20.0.4:8083",...}],"type":"source"}
  ```
- **Kafka 4.x'te bir ekleme:** Bazı connector'lar `tasks.max` aşımını artık uyarı/hata olarak
  bildirir (`tasks.max.enforce`); yani "10 istedim 1 aldım" durumu daha görünür hâle geldi.

> 📌 **Sık yapılan hata:** `tasks.max`'ı büyütüp paralelliğin arttığını varsaymak. Gerçek task
> sayısını **her zaman `/status`'tan doğrula**.

🔗 Konu: [7.1 §1](7.1-connect-mimarisi.md)

---

### Soru 3 — Bir sink connector'ın lag'ini nasıl ölçersin? Hangi grup adı?

**Kısa cevap:** Sink connector **normal bir consumer group** kullanır; grup adı
**`connect-<connector-adı>`**'dır. Yani:
```bash
kafka-consumer-groups.sh --bootstrap-server kafka-1:19092 \
  --describe --group connect-dosya-hedef
```
Çıktı sana partition başına `CURRENT-OFFSET`, `LOG-END-OFFSET` ve `LAG` verir — sıradan bir
tüketicide olduğu gibi.

**Ayrıntı:**

- Bu, Connect'i izlemenin **en pratik yoludur** ve çoğu ekip bilmez. Connect'in kendi
  metriklerine (`kafka.connect:type=sink-task-metrics`) gitmeden, mevcut lag izleme altyapını
  aynen kullanabilirsin.
- **Partition başına lag'e bak**, toplama değil (5.2). Tek bir partition tıkanmışsa toplam iyi
  görünür.
- **Source connector için aynı yol yoktur.** Source'un "lag"i dış sistemdeki gecikmedir
  (ör. DB'deki son satır ile connector'ın okuduğu satır arasındaki fark) ve bunu connector'ın
  kendi metrikleri ya da dış sistemin metrikleriyle ölçersin.
- Connect'in kendi metriklerinden faydalı olanlar: `sink-record-read-rate`,
  `sink-record-send-rate`, `put-batch-avg-time-ms`, `offset-commit-avg-time-ms`,
  `deadletterqueue-produce-requests`.

> 📌 **Sık yapılan hata:** Sink connector'ı "kara kutu" sanıp yalnızca `/status` çıktısına
> bakmak. `RUNNING` olması **veri aktığı anlamına gelmez** (bkz. Soru 4).

🔗 Konu: [7.1 §3, §6](7.1-connect-mimarisi.md)

---

### Soru 4 — Connector `RUNNING` ama veri akmıyor: sırayla üç şey

**Kısa cevap:**

1. **Task listesi.** `GET /connectors/<ad>/status` çıktısındaki `tasks` dizisi **boş mu**?
   Connector RUNNING olup hiç task üretmemiş olabilir. Task varsa durumu `RUNNING` mi,
   `FAILED` mi? `FAILED` ise `trace` alanı hatayı verir.
2. **Offsetler ilerliyor mu?** Sink için `kafka-consumer-groups.sh --describe --group
   connect-<ad>`; source için hedef topic'in `kafka-get-offsets.sh` ile offset'i. **Sayı
   artmıyorsa** veri gerçekten akmıyordur.
3. **Worker log'u ve `plugin.path`.** Log'da sessiz bir classloader/plugin sorunu olabilir.

**Ayrıntı — bu setin hazırlanışında yaşanan gerçek arıza:**
```properties
plugin.path=/opt/kafka/libs        # ❌ TÜM libs dizini
```
**Sonuç:** Worker başlangıcı **2 dakikadan uzun sürdü** (her jar taranıyor), connector `RUNNING`
göründü, task `RUNNING` göründü — ama **hiç kayıt üretilmedi** ve log'da hata yoktu.
```properties
plugin.path=/opt/kafka/libs/connect-file-4.3.1.jar    # ✅ yalnızca gerçek plugin
```
**Sonuç:** Başlangıç **15 saniye**, veri anında aktı.

> ⭐ **Ders:** `plugin.path` **plugin dizinlerini/jar'larını** göstermelidir, Kafka'nın kendi
> `libs` dizinini değil. Classpath'te zaten olan sınıfları plugin olarak da yüklemek
> **sessiz classloader çakışmaları** üretir. Üretimde ayrı bir `/opt/connectors` dizini kullan.

**Kontrol edilecek diğer şeyler:**
- Kaynak gerçekten veri üretiyor mu? (Dosya boş mu, DB'de yeni satır var mı?)
- Source offset'i "her şeyi okudum" diyor olabilir — dosya değişmediyse yeni kayıt yoktur.
- SMT zinciri kayıtları **filtreliyor** olabilir (`Filter` + predicate).
- ACL/yetki sorunu (5.4) — ama bu genelde log'da görünür.

> 📌 **Sık yapılan hata:** `state: RUNNING` gördükten sonra aramayı bırakmak. **Her zaman
> `/status` çıktısındaki `tasks` dizisine ve topic offsetlerine birlikte bak.**

🔗 Konu: [7.1 §6–7](7.1-connect-mimarisi.md)

---

### Soru 5 — `config.storage.topic` neden tek partition olmak zorunda?

**Kısa cevap:** Çünkü connector yapılandırmalarının **toplam sıraya (total order)** ihtiyacı
vardır. Bir connector'ın config'i art arda güncellenirse, tüm worker'ların bu güncellemeleri
**aynı sırada** görmesi şarttır. Birden çok partition olsaydı partition'lar arası sıra garanti
edilemez ve worker'lar farklı yapılandırmalara yakınsayabilirdi.

**Ayrıntı:**

- Bu, `__cluster_metadata`'nın tek partition olmasıyla **aynı sebeptir** (1.3): dağıtık bir
  sistemde ortak bir gerçeklik kurmanın yolu tek bir sıralı log'dur.
- Connect'in üç iç topic'i ve neden farklı yapılandırıldıkları:

| Topic | Politika | Partition | İçerik |
|---|---|---|---|
| `config.storage.topic` | `compact` | **1 (zorunlu)** | Connector yapılandırmaları |
| `offset.storage.topic` | `compact` | 25 (tipik) | **Source** connector offsetleri |
| `status.storage.topic` | `compact` | 5 (tipik) | Connector/task durumları |

- **Neden diğerleri çok partition olabilir:** Offset'ler ve durumlar **birbirinden bağımsızdır** —
  connector A'nın offset'i ile connector B'ninki arasında bir sıra ilişkisi yoktur. Config'te ise
  "önce sil sonra oluştur" gibi bağımlı işlemler vardır.
- Üçü de `compact`'tır: her key için son değer korunur (1.4). Bir connector silinince tombstone
  yazılır.
- **Ölçek endişesi yersizdir:** Config yazma hacmi ihmal edilebilir. Tek partition darboğaz
  değildir.
- **Operasyonel not:** Bu topic'leri **elle oluşturuyorsan** partition sayısını ve
  `cleanup.policy=compact`'ı doğru vermelisin; yanlış oluşturulmuş bir `config.storage.topic`
  Connect cluster'ında açıklanması zor tutarsızlıklar üretir.

> 📌 **Sık yapılan hata:** Üç iç topic'i "performans için" çok partition'lı oluşturmak.
> `config.storage.topic` için bu **doğrudan bir hatadır**.

🔗 Konu: [7.1 §2](7.1-connect-mimarisi.md) · [1.3 §2](../01-broker-depolama/1.3-kraft-metadata.md)

---

## 7.2 Connector, SMT ve MM2

📄 Sorular: [`7.2-connector-smt-ve-mm2.md`](7.2-connector-smt-ve-mm2.md)

### Soru 1 — SMT içinde veritabanı sorgusu neden kötü fikir? Alternatifi?

**Kısa cevap:** Çünkü SMT **her kayıt için** çalışır ve **senkron**dur. Kayıt başına bir DB
çağrısı, hattı o çağrının gecikmesiyle sınırlar: 5 ms'lik bir sorgu bile throughput'u saniyede
~200 kayda düşürür. Ayrıca DB yavaşlarsa/kesilirse tüm connector durur.

**Alternatifi:** **Kafka Streams + GlobalKTable ile zenginleştirme.** Referans veriyi bir topic'e
al (CDC ya da periyodik dump), Streams'te `GlobalKTable` olarak yükle ve join yap. Böylece
arama **bellekten/RocksDB'den** yapılır, ağ çağrısı olmaz.

**Ayrıntı — SMT'nin sınırları:**

| İşlem | SMT uygun mu |
|---|---|
| Alan maskeleme / silme / yeniden adlandırma | ✅ |
| Header ekleme, topic yönlendirme (`RegexRouter`) | ✅ |
| Tip dönüşümü (`Cast`), zaman formatı | ✅ |
| Filtreleme (`Filter` + predicate) | ✅ |
| **Başka bir kayıtla birleştirme (join)** | ❌ Streams |
| **Toplama / sayma / pencereleme** | ❌ Streams |
| **Dış servis çağrısı** | ❌ |

**Üç kural — SMT yazarken:**
1. **Durumsuz ol.** SMT örnekleri task başına oluşturulur; kayıtlar arası durum tutma.
2. **Hızlı ol.** Her kayıt için çalışır; ağ/disk çağrısı yapma.
3. **Dokunamıyorsan kaydı olduğu gibi döndür** (Soru 2).

**İkinci alternatif (daha basit senaryolar için):** Zenginleştirmeyi **kaynakta** yap — JDBC
source connector'ında sorguyu bir `JOIN` içerecek şekilde yaz. Veri Kafka'ya zaten zenginleşmiş
gelir.

> 📌 **Sık yapılan hata:** "Küçük bir lookup, cache'lerim" diyerek SMT'ye DB çağrısı koymak.
> Cache tutmak da kuralı bozar: SMT **durumsuz** olmalıdır ve task başına ayrı örnek
> oluşturulduğu için cache tutarlılığı garanti edilemez.

🔗 Konu: [7.2 §1–2](7.2-connector-smt-ve-mm2.md)

---

### Soru 2 — SMT'nin `apply()` metodu tanımadığı bir tiple karşılaşırsa?

**Kısa cevap:** **Kaydı olduğu gibi döndürmelidir** — istisna fırlatmamalıdır. Beklenmedik tipte
istisna fırlatmak, `errors.tolerance` devrede değilse **tüm hattı durdurur**.

**Ayrıntı — lab SMT'sinden doğru desen:**
```java
@Override public R apply(R record) {
    Object value = operatingValue(record);
    if (!(value instanceof String s)) return record;      // ⬅ dokunamıyorsan DOKUNMA
    String masked = maskDigits(s);
    return masked.equals(s) ? record : newRecord(record, masked);
}
```
İki savunma var: (1) tip uymuyorsa kayıt aynen döner, (2) değişiklik olmadıysa **yeni nesne bile
oluşturulmaz**.

**Neden istisna fırlatmak kötü:**
- SMT zinciri connector task'ının **sıcak yolundadır**; bir istisna task'ı `FAILED` yapar ve
  veri akışı durur.
- Aynı kayıt yeniden denenirse aynı istisna gelir — sonsuz döngü ya da kalıcı `FAILED`.
- Tek bir beklenmedik kayıt, **tüm topic'in** akışını durdurur (2.4'teki zehirli kayıt
  probleminin Connect'teki karşılığı).

**Ne zaman istisna fırlatmak doğru olur:** Yalnızca **yapılandırma** hatalarında ve yalnızca
`configure()` içinde — "bu SMT yanlış yapılandırılmış" demek için. Çalışma anında kayıt bazlı
istisna, `errors.tolerance=all` + DLQ ile birlikte bilinçli olarak seçilmiş bir stratejinin
parçası olmalıdır:
```properties
errors.tolerance=all
errors.deadletterqueue.topic.name=connect-dlq
errors.deadletterqueue.context.headers.enable=true
```

**Alternatif — predicate kullan.** Kaydın SMT'ye hiç girmemesini istiyorsan, `apply()` içinde
kontrol etmek yerine bir predicate ile filtrele:
```properties
predicates=sadeceBu
predicates.sadeceBu.type=org.apache.kafka.connect.predicates.TopicNameMatches
predicates.sadeceBu.pattern=lab-smt-.*
transforms.maskele.predicate=sadeceBu
```

> 📌 **Sık yapılan hata:** `throw new DataException("beklenmedik tip")` yazmak. Doğru davranış
> "anlamadığım kaydı bozmam ve durdurmam"dır.

🔗 Konu: [7.2 §2](7.2-connector-smt-ve-mm2.md) · [7.1 §5](7.1-connect-mimarisi.md)

---

### Soru 3 — MM2'nin üç connector'ı ve görevleri

**Kısa cevap:**

| Connector | Görevi |
|---|---|
| **`MirrorSourceConnector`** | Topic **verisini** ve topic **yapılandırmasını** kaynaktan hedefe kopyalar |
| **`MirrorCheckpointConnector`** | **Consumer group offsetlerini** çevirip hedefe yazar |
| **`MirrorHeartbeatConnector`** | Bağlantı canlılığını ölçen `heartbeats` topic'i üretir |

**Ayrıntı:**

- **MM2 ayrı bir uygulama değildir** — Kafka Connect üzerine kurulmuş üç connector'dır. Bu
  yüzden 7.1'deki tüm işletim modeli geçerlidir: worker, task, REST API, iç topic'ler,
  `errors.*` ayarları.
- **`MirrorSourceConnector`** aynı zamanda `offset-syncs` topic'ini besler: "kaynaktaki offset X,
  hedefte offset Y'ye karşılık geliyor" eşleşmelerini yazar. Checkpoint connector'ın çevrimi buna
  dayanır.
- **`MirrorCheckpointConnector`**, `sync.group.offsets.enabled=true` ise hedefte grup
  offsetlerini **fiilen yazar** — böylece failover sonrası tüketiciler yaklaşık doğru noktadan
  devam eder.
- **`MirrorHeartbeatConnector`** çoğu zaman göz ardı edilir ama DR izlemesinin temelidir:
  heartbeat topic'indeki gecikmeye bakarak replikasyon gecikmesini (RPO'ya yakınlığı) ölçebilirsin.
- Lab'da hedef cluster'da oluşan topic'ler bunu doğruluyor:
  ```
  heartbeats                        ⬅ MirrorHeartbeatConnector
  kaynak.checkpoints.internal       ⬅ MirrorCheckpointConnector
  kaynak.heartbeats
  kaynak.lab-mm-siparis             ⬅ REPLİKE EDİLEN TOPIC (ön ekli!)
  mm2-configs.kaynak.internal       ⬅ Connect'in kendi iç topic'leri (7.1)
  mm2-offsets.kaynak.internal
  mm2-status.kaynak.internal
  ```

> 📌 **Sık yapılan hata:** Yalnızca `MirrorSourceConnector`'ı çalıştırıp "DR kuruldu" demek.
> Veri gider ama **offsetler gitmez**; failover'da tüm tüketiciler baştan ya da sondan başlar.

🔗 Konu: [7.2 §4](7.2-connector-smt-ve-mm2.md)

---

### Soru 4 — Hedefte topic adının `kaynak.siparisler` olmasının sebebi ve faydası?

**Kısa cevap:** Varsayılan `DefaultReplicationPolicy`, hedefte topic adının başına **kaynak
cluster'ın takma adını** ekler. Sebebi ve asıl faydası: **döngüsel replikasyonu engellemek**
(A→B→A). Ön ek olmasaydı, B'deki `siparisler` topic'i A'ya geri kopyalanır ve sonsuz bir döngü
oluşurdu.

**İkinci fayda:** Hedef cluster'da **verinin kaynağı isminden belli olur**. Birden çok
cluster'dan replikasyon alan bir hedefte (cluster birleştirme, merkezî analitik) `istanbul.siparisler`
ve `ankara.siparisler` yan yana durabilir ve karışmaz.

**Üçüncü fayda:** Hedefin **kendi** yerel topic'leriyle çakışma olmaz. DR cluster'ı bir gün aktif
hâle gelirse, replike edilmiş veri ile yerel veri ayırt edilebilir kalır.

**Ayrıntı:**

- **`IdentityReplicationPolicy`** (ön ek yok) kolaycı görünür: uygulamalar topic adını
  değiştirmeden failover edebilir. Ama **döngü koruması kaybolur** — yalnızca **tek yönlü, tek
  atlamalı** senaryolarda ve bilinçli olarak kullanılmalıdır.
- Ön ek, failover sırasında bir maliyet doğurur: tüketicilerin `kaynak.siparisler` topic'ini
  okuyacak şekilde yapılandırılması gerekir. Bu genelde bir regex subscribe
  (`Pattern.compile(".*siparisler")`) ile çözülür.
- Lab'da partition dağılımının da korunduğu doğrulandı:
  ```
  hedef : kaynak.lab-mm-siparis:0:0   :1:50   :2:0
  kaynak: lab-mm-siparis:0:0          :1:50   :2:0
  ```
  Aynı kayıt hedefte de **aynı partition'da** — key bazlı sıra korunur.

> 📌 **Sık yapılan hata:** Ön ekten kurtulmak için `IdentityReplicationPolicy`'yi düşünmeden
> açmak. Çift yönlü (aktif-aktif) bir kurulumda bu, cluster'ları veriyle boğar.

🔗 Konu: [7.2 §4](7.2-connector-smt-ve-mm2.md)

---

### Soru 5 — MM2 ile DR kuran bir ekibe iki uyarı

**Kısa cevap:**

**Uyarı 1 — MM2 at-least-once ve asenkrondur; "sıfır veri kaybı" vaat etmez.** Replikasyon
gecikmesi kadar bir RPO penceresi her zaman vardır. Failover sonrası **duplicate işleme
normaldir** — tüketicilerin **idempotent** olması zorunludur (4.2). MM2 senkron replikasyon
değildir ve olamaz.

**Uyarı 2 — Offsetler çevrilir, aynı değildir; ve çevrim muhafazakârdır.** Failover sonrası
tüketiciler **kaldıkları yerin biraz gerisinden** devam eder (bkz. Soru 6). Bunu bir hata sanıp
"düzeltmeye" çalışmak veri kaybına yol açar.

**Ek uyarılar (pratikte de söylenir):**

3. **Failover kararı otomatik değildir.** MM2 sana bir DR kopyası verir; "ne zaman geçilecek"
   kararını ve DNS/yapılandırma değişikliğini **sen** yaparsın. RTO'nun büyük kısmı bu insan
   kararında geçer — tatbikat yapın.
4. **Geri dönüş (failback) planı da yapın.** İstanbul geri geldiğinde veri Ankara'dadır. Ters
   yönde bir MM2 kurulumu ve ön ek politikası önceden düşünülmelidir.
5. **MM2 worker'larını hedef tarafta çalıştırın** (uzak okuma, yerel yazma) — ağ kopmalarında
   daha dayanıklıdır.
6. **`refresh.groups.interval.seconds` varsayılanı 600'dür (10 dakika).** Lab'da yakalanan gerçek
   tuzak: ilk denemede hedefte grup hiç görünmedi; 10 saniyeye çekince belirdi. "Offset çevrimi
   çalışmıyor" derken **önce bu aralığı** kontrol edin.
7. **MM2'yi broker'ın içinde çalıştırmayın.** Kendi Connect worker'larını başlatır; broker'la aynı
   konteynerde çalıştırmak belleği ve diski tüketip **broker'ı çökertebilir** — bu setin
   hazırlanışında bir kez yaşandı.

**MM2 ne çözer / ne çözmez:**

| Çözer | Çözmez |
|---|---|
| Felaket kurtarma (aktif-pasif) | Senkron replikasyon / sıfır RPO |
| Veri merkezi / bulut göçü | Exactly-once cross-cluster |
| Coğrafi yakınlık için veri kopyası | Otomatik failover kararı |
| Cluster birleştirme/ayırma | Aynı offsetleri korumak |

> 📌 **Sık yapılan hata:** MM2'yi bir **yedekleme** sanmak. Kaynakta silinen topic hedefte de
> silinebilir; yanlış yazılan veri aynen replike olur. Yedek ≠ canlı ikinci kopya (1.5).

🔗 Konu: [7.2 §4](7.2-connector-smt-ve-mm2.md)

---

### Soru 6 — Kaynakta offset 180, hedefte 102: hata mı? Neden bu yönde yuvarlanıyor?

**Kısa cevap:** **Hata değil, tasarım.** Offset çevrimi **muhafazakârdır**: MM2 emin olmadığı
yerde **ileri değil geri** yuvarlar. Çünkü ileri yuvarlamak **veri kaybı**, geri yuvarlamak
**tekrar işleme** demektir — ve tekrar işlemeyi idempotent bir tüketici emebilir, veri kaybını
hiçbir şey geri getiremez.

**Ayrıntı — gerçek lab sonucu:**

| | kaynak offset | hedefte **çevrilmiş** offset |
|---|---|---|
| partition 0 | 180 | **102** ⬅ geride |
| partition 1 | 50 | **50** ⬅ birebir |

**Neden offsetler eşleşmiyor:** İki cluster **bağımsız log'lardır**. Hedefteki kayıtlar
MM2 çalışmaya başladığı andan itibaren numaralanır; kaynaktaki offset numaraları oraya taşınmaz.
Ayrıca kaynakta retention ile silinmiş eski kayıtlar hedefe hiç gitmemiş olabilir, ya da
transaction control record'ları (4.1) iki tarafta farklı sayıda offset tüketmiş olabilir.

**Çevrim nasıl yapılır:** `MirrorSourceConnector`, `offset-syncs` topic'ine periyodik olarak
"kaynak offset X ↔ hedef offset Y" eşleşmeleri yazar. `MirrorCheckpointConnector` bir grup
offset'ini çevirirken **elindeki en yakın sync noktasını** kullanır. İki sync noktası arasındaki
bir offset için kesin karşılık bilinmez — bu yüzden **bir önceki bilinen noktaya** yuvarlanır.
Partition 1'de birebir çıkması, o offset'in tam bir sync noktasına denk gelmesindendir.

**Pratik sonuç:** Failover sonrası bir miktar kaydın **yeniden işlenmesi normaldir**. Bu, MM2'nin
en önemli tasarım kararıdır ve tüketicilerin idempotent olmasını **zorunlu** kılar (4.2).

**Ne yapılmamalı:** Hedefteki offset'i elle "düzeltip" 180 yapmak. Hedefteki 180. offset, kaynağın
180. offset'iyle **aynı kayıt değildir**; bunu yaparsan gerçekten veri atlarsın.

> 📌 **Sık yapılan hata:** DR tatbikatında bu farkı görüp "MM2 bozuk" sonucuna varmak. Fark
> **beklenen** davranıştır; ölçülmesi gereken şey farkın **büyüklüğü** (kaç kayıt tekrar
> işlenecek) ve bunun iş açısından kabul edilebilirliğidir.

🔗 Konu: [7.2 §4](7.2-connector-smt-ve-mm2.md) · [4.2 §3](../04-eos-transaction/4.2-outbox-ve-idempotent-tuketici.md)

---

⬅️ [Bölüme dön](7.1-connect-mimarisi.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
