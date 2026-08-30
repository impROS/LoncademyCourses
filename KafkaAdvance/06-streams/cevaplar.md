# 06 · Kafka Streams — Kendini kontrol cevapları

> Bu dosya [6.1](6.1-topoloji-ve-task-modeli.md) – [6.4](6.4-eos-hata-ve-test.md) konularının
> sonundaki **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [6.1](#61-topoloji-ve-task-modeli) · [6.2](#62-state-store-ve-changelog) ·
[6.3](#63-join-ve-windowing) · [6.4](#64-eos-hata-yönetimi-ve-test)

---

## 6.1 Topoloji ve task modeli

📄 Sorular: [`6.1-topoloji-ve-task-modeli.md`](6.1-topoloji-ve-task-modeli.md)

### Soru 1 — 3 partition + `groupBy`: kaç task üretir?

**Kısa cevap:** **6 task.** `groupBy` key'i değiştirir ve ardından key'e bağlı bir işlem
(aggregate/reduce/count) gelir; Streams araya bir **repartition topic'i** koyar ve topoloji
**iki alt topolojiye** bölünür. Task sayısı:
```
task sayısı = Σ (her alt topoloji için, kaynak topic'lerindeki max partition sayısı)
            = 3 (alt topoloji 0) + 3 (alt topoloji 1) = 6
```
Task id'leri `0_0, 0_1, 0_2` ve `1_0, 1_1, 1_2` olur.

**Ayrıntı — lab kanıtı** (4 partition ile çalıştırıldı, 2 alt topoloji → **8 task**):
```
Sub-topology: 0
  Source: KSTREAM-SOURCE-0000000000 (topics: [lab-siparisler])
  Processor: KSTREAM-KEY-SELECT-0000000002          ← YENİDEN ANAHTARLAMA
  Sink: musteri-toplam-store-repartition-sink (topic: musteri-toplam-store-repartition)
Sub-topology: 1
  Source: musteri-toplam-store-repartition-source (topics: [musteri-toplam-store-repartition])
  Processor: KSTREAM-REDUCE-0000000004 (stores: [musteri-toplam-store])   ← STATEFUL
  Sink: KSTREAM-SINK-0000000009 (topic: lab-musteri-toplam)
```
```
thread: ...-StreamThread-2   aktif task 1_3, 1_1, 0_3, 0_1
thread: ...-StreamThread-1   aktif task 1_0, 1_2, 0_2
```

**Kurallar:**

| Kural | Açıklama |
|---|---|
| Task **atomiktir** | Bölünmez; bir thread'e atanır |
| `taskId = altTopolojiIndeksi_partition` | `1_3` = alt topoloji 1, partition 3 |
| Task sayısı **paralellik tavanıdır** | Fazla thread/instance atıl kalır |

> 📌 **Sık yapılan hata:** Task sayısını partition sayısına eşit sanmak. Stateless bir topolojide
> öyledir; `groupBy`/`join` girdiğinde alt topoloji sayısıyla **çarpılır**.

🔗 Konu: [6.1 §1–2](6.1-topoloji-ve-task-modeli.md)

---

### Soru 2 — Alt topoloji sınırı ne zaman oluşur? Neden topic gerekir?

**Kısa cevap:** Sınır, **key değiştiğinde** (`selectKey`, `map`, `groupBy`) ve ardından **key'e
bağlı** bir işlem (aggregate, reduce, count, join) geldiğinde oluşur. Topic gerekir çünkü aynı
key'in tüm kayıtlarının **aynı task'a** düşmesi gerekir — ve bunu sağlamanın tek yolu veriyi
yeni key'e göre Kafka üzerinden **yeniden dağıtmaktır**.

**Ayrıntı:**

- Bir task yalnızca **kendi partition'ını** görür. `selectKey` sonrası aynı yeni key'e sahip
  kayıtlar farklı partition'larda (yani farklı task'larda) olabilir. Toplama yapmak için hepsinin
  bir araya gelmesi şart.
- Streams bunu kendi belleğinde yapamaz (task'lar farklı JVM'lerde olabilir), bu yüzden veriyi
  bir **repartition topic'ine** yazar ve yeniden okur. `murmur2(yeniKey) % N` ile doğru partition'a
  düşer.
- **Sadece key değişimi yetmez.** `selectKey` yapıp sonra sadece `mapValues`/`filter`/`to`
  yaparsan repartition **oluşmaz** — key'e bağlı bir işlem yoksa yeniden dağıtıma gerek yoktur.
  Streams bunu tembel değerlendirir.
- Sınırı topoloji açıklamasında görürsün: iki alt topoloji arasında bir `...-repartition` topic'i.
- **Bedeli:** Ekstra bir yazma + okuma turu, ekstra disk, ekstra gecikme. Bu yüzden gereksiz
  `selectKey` çağrılarından kaçınılır.
- **Kaçınma yolu:** Veriyi **üretim tarafında** doğru key ile yazmak. Producer zaten doğru key
  veriyorsa Streams'te yeniden anahtarlamaya gerek kalmaz.

> 📌 **Sık yapılan hata:** Repartition topic'ini "Streams'in gereksiz bir iç ayrıntısı" sanmak.
> O topic, dağıtık bir sistemde key bazlı toplamanın **tek doğru yoludur**.

🔗 Konu: [6.1 §1, §4](6.1-topoloji-ve-task-modeli.md)

---

### Soru 3 — Repartition ve changelog topic'lerinin `cleanup.policy`'leri neden farklı?

**Kısa cevap:** İkisi **farklı işler** yapar:
- **Repartition = geçici taşıma.** `cleanup.policy=delete`, `retention.ms=-1`. Kayıt işlenince
  artık gereksizdir; Streams onu **kendisi budar** (İngilizce kaynaklarda *purge*).
- **Changelog = kalıcı durum yedeği.** `cleanup.policy=compact`. Key başına **son değer** asla
  kaybolmamalı — çünkü state store'un gerçek kaynağı odur.

**Ayrıntı — lab çıktısı:**
```
lab-topology-demo-musteri-toplam-store-repartition
  Configs: cleanup.policy=delete, retention.ms=-1, segment.bytes=52428800
lab-topology-demo-musteri-toplam-store-changelog
  Configs: cleanup.policy=compact
```

- **`retention.ms=-1` neden şaşırtıcı ama doğru:** Repartition topic'i **zamanla** temizlenmez.
  Zaman bazlı silme, henüz işlenmemiş bir kaydı silebilirdi. Onun yerine Streams, işlediği
  kayıtları **açıkça budar**. Sonuç: kontrol Streams'te, zamanlayıcıda değil.
- **Yan etki:** Durdurulmuş bir Streams uygulamasının repartition topic'i **büyümeye devam
  edebilir** (üretici hâlâ yazıyorsa) ve kimse budamaz. Kullanılmayan uygulamaların iç
  topic'lerini temizlemek operasyonel bir görevdir.
- **Changelog neden compact:** Restore sırasında geçmişin tamamı değil, **her key'in son durumu**
  gerekir. Compaction bunu garanti eder ve restore'u dramatik biçimde hızlandırır (bkz. 6.2 Soru 1).
- **İstisna — windowed store changelog'ları `compact,delete`'tir:** Pencereler zamanla
  anlamsızlaşır, bu yüzden hem key bazlı temizlik hem yaş bazlı silme uygulanır.

> 📌 **Sık yapılan hata:** İç topic'leri elle silmek. Çalışan bir uygulamayı bozar. Temizlik için
> **`kafka-streams-application-reset.sh`** kullanılır.

🔗 Konu: [6.1 §4](6.1-topoloji-ve-task-modeli.md) · [1.4 §1](../01-broker-depolama/1.4-retention-ve-compaction.md)

---

### Soru 4 — `num.stream.threads=8` ama 4 task var: ne olur, ne yaparsın?

**Kısa cevap:** **4 thread çalışır, 4'ü boş oturur.** Task, atomik iş birimidir; iki thread aynı
task'ı paylaşamaz. Thread artırmak **task sayısını artırmaz** — tıpkı partition'dan fazla
consumer koymanın işe yaramaması gibi (3.2).

**Ne yaparsın:**

| Durum | Yapılacak |
|---|---|
| CPU doygun, boş task yok | **Yeni instance** başlat (yatay ölçekleme) |
| Tek makinede boş CPU var, task var | `num.stream.threads` artır |
| **Task sayısı tavana ulaştı** | **Giriş topic'inin partition sayısını artır** — 5.5'teki uyarılarla |
| Tek kayıt işleme süresi uzun | Önce **kodu** optimize et; paralellik son çare |

**Ayrıntı:**

- Task sayısı formülü: `Σ (alt topoloji başına max partition sayısı)`. 4 task varsa ya giriş
  topic'i 4 partition'lıdır ve topoloji tek alt topolojidir, ya da 2 partition × 2 alt topolojidir.
- **Partition artırmanın bedelini unutma:** Key kullanan bir topic'te partition artırmak
  key→partition eşlemesini bozar ve sıra garantisini kırar (5.5). Streams'te bu daha da
  tehlikelidir çünkü **state store'lar** partition'a bağlıdır — mevcut durum yeni partition
  dağılımıyla eşleşmez. Pratikte bu, uygulamayı **sıfırdan başlatmak** (reset) anlamına gelir.
- Boş thread'ler tamamen zararsız da değildir: her StreamThread'in **kendi consumer'ı** vardır,
  yani grup üyeliği ve rebalance maliyeti taşır.
- **Doğru başlangıç:** `num.stream.threads` = makinedeki kullanılabilir çekirdek sayısı civarı,
  ama **task sayısını aşmayacak** şekilde.

> 📌 **Sık yapılan hata:** Yavaşlık görünce `num.stream.threads`'i artırmak. Önce
> **task sayısı tavanına ulaşıp ulaşmadığını** ölç.

🔗 Konu: [6.1 §2–3](6.1-topoloji-ve-task-modeli.md)

---

### Soru 5 — `application.id`'yi değiştirirsen ne olur?

**Kısa cevap:** **Yepyeni bir uygulama** başlatmış olursun. `application.id`, Streams'in
**consumer group id'sidir** — değiştirince:
1. Offset'ler sıfırdan başlar (`auto.offset.reset`'e göre baştan ya da sondan okur),
2. **Yeni iç topic'ler** oluşur (`<yeni-app-id>-<store>-changelog`, `-repartition`),
3. Tüm state store'lar **sıfırdan** kurulur — eski changelog'daki durum kullanılmaz.

**Neden dikkatli olmak gerekir:**

- **Eski iç topic'ler ortada kalır.** `<eski-app-id>-*-changelog` ve `-repartition` topic'leri
  silinmez; disk yer kaplamaya devam eder ve kimse temizlemez.
- **Veri kaybı ya da duplicate seli.** `auto.offset.reset=latest` ise aradaki tüm veri
  **atlanır**; `earliest` ise her şey **yeniden işlenir** ve aşağı akışa duplicate akar.
- **Durum kaybolur.** Aylarca birikmiş bir aggregate, tek bir yapılandırma satırıyla sıfırlanır.
- Bu yüzden `application.id` **kalıcı bir kimliktir** — uygulama adı gibi düşünülmeli, sürüm
  numarası gibi değil.

**Ne zaman bilerek değiştirilir:** Topoloji **uyumsuz** biçimde değiştiğinde (yeni bir
alt topoloji, farklı store yapısı) — o zaman zaten eski state kullanılamaz. Ama doğru yol
genelde `application.id` değiştirmek değil, **`kafka-streams-application-reset.sh`** ile
kontrollü sıfırlamadır: bu araç offset'leri sıfırlar, iç topic'leri temizler ve neyi sildiğini
sana söyler.

**İlgili ayrım:** `application.id` ≠ `client.id`. İkincisi yalnızca izleme/loglama için bir
etikettir; değiştirmek hiçbir davranışı etkilemez.

> 📌 **Sık yapılan hata:** "Uygulamayı temiz başlatmak için" `application.id`'ye sürüm eki eklemek
> (`odeme-akisi-v2`). Her dağıtımda yeni bir grup ve yeni iç topic seti üretmiş olursun.

🔗 Konu: [6.1 §3–4](6.1-topoloji-ve-task-modeli.md) · [3.3 §1](../03-consumer/3.3-offset-ve-commit.md)

---

## 6.2 State store ve changelog

📄 Sorular: [`6.2-state-store-ve-changelog.md`](6.2-state-store-ve-changelog.md)

### Soru 1 — 10 milyon olay, 100 bin benzersiz key: restore'da kaç kayıt okunur?

**Kısa cevap:** **~100 bin** — 10 milyon değil. Changelog `cleanup.policy=compact`'tır: her key
için yalnızca **son değer** saklanır. Restore, durumun **anlık görüntüsünü** okur, geçmişin
tamamını değil.

**Ayrıntı — lab kanıtı** (5000 olay, 50 key):
```
===== 1. ÇALIŞTIRMA — state sıfırdan kuruluyor =====
  5000 olay yazıldı (50 key)
  >>> TOPLAM RESTORE: 0 kayıt, ~409 ms (ilk çalıştırma)

===== 2. ÇALIŞTIRMA — state dizini SİLİNDİ, changelog'dan restore =====
  interaktif sorgu: 50 key, toplam 5000 olay
  >>> TOPLAM RESTORE: 50 kayıt, ~27 ms (state silinmişti)
```
**5000 olay → 50 kayıt restore.** Durum tamamen geri geldi; diski silmek veri kaybettirmedi.

**Dört ders:**
1. **Changelog gerçek kaynaktır**, yerel disk bir önbellektir.
2. Restore edilen kayıt sayısı **benzersiz key sayısı** kadardır.
3. İlk çalıştırmada restore 0 kayıttı ama yine de ~409 ms sürdü — boş store'lar için de changelog
   konumu kontrol edilir.
4. **Restore süresi durumun boyutuyla orantılıdır.** 50 kayıt 27 ms; **100 bin kayıt saniyeler**,
   **50 milyon kayıt dakikalar**.

**Küçük bir nüans:** "~100 bin" bir alt sınırdır. Compaction **anlık değildir** (1.4): aktif
segment ve kirli kısım temizlenmemiş olabilir, dolayısıyla gerçek sayı 100 binin biraz üzerinde
çıkar. Ama mertebe 10 milyon değil **100 bin**dir.

> 📌 **Sık yapılan hata:** Restore süresini olay sayısıyla tahmin etmek. Ölçek **key sayısıdır**.

🔗 Konu: [6.2 §2–3](6.2-state-store-ve-changelog.md)

---

### Soru 2 — `state.dir` neden üretimde mutlaka değiştirilmeli?

**Kısa cevap:** Varsayılanı **`/tmp/kafka-streams`**'tir. Birçok sistem `/tmp`'i yeniden
başlatmada (ya da periyodik olarak) **temizler**. Sonuç: her restart'ta yerel durum kaybolur ve
uygulama **tam restore** yapar — büyük store'larda dakikalarca açılamama.

**Ayrıntı:**

- Kubernetes'te aynı problem `emptyDir` ile yaşanır: pod yeniden başlatıldığında disk sıfırlanır.
  Doğru çözüm **kalıcı disk (PVC — [Kubernetes kalıcı disk talebi ↗](../00-baslangic/02-kavram-sozlugu.md#pvc)) + StatefulSet**'tir.
- Bunu `group.instance.id` (static membership, 3.2) ile birleştirirsen hem **rebalance** hem
  **restore** ortadan kalkar — pod aynı kimlikle döner, aynı task'ları alır, yerel durumu zaten
  yerindedir.
- **İkinci sebep:** `/tmp` genelde küçük ve paylaşımlıdır. Büyük bir RocksDB store'u `/tmp`'i
  doldurup **tüm makineyi** etkileyebilir.
- **Üçüncü sebep:** Aynı makinede birden çok Streams uygulaması varsa `state.dir` çakışması
  karışıklık yaratır. Uygulama başına ayrı, açıkça belirtilmiş bir yol tercih edilir.
- Kafka 4.3 ile `state.cleanup.dir.max.age.ms` (KIP-1259) geldi: eski/artık kullanılmayan state
  dizinleri otomatik temizlenir. Bu, `state.dir`'i doğru yere koymanın **yerine geçmez**.

```properties
state.dir=/var/lib/streams/odeme-akisi
```

> 📌 **Sık yapılan hata:** Bu ayarı "önemsiz bir yol ayarı" sanmak. Streams'te **en sık üretim
> hatasıdır** ve belirtisi "uygulama açılmıyor / çok yavaş açılıyor"dur.

🔗 Konu: [6.2 §6](6.2-state-store-ve-changelog.md) · [3.2 §5](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 3 — `num.standby.replicas=1` neyi hızlandırır, neyi hızlandırmaz?

**Kısa cevap:**
- **Hızlandırır: failover.** Bir instance kaybolduğunda, standby zaten güncel bir durum kopyası
  tuttuğu için yeni sahip **anında devralır** — sıfırdan restore etmez. Dakikalar yerine saniyeler.
- **Hızlandırmaz: normal işleme.** Standby aktif işleme yapmaz; throughput'a, gecikmeye ve
  sorgulara **hiçbir katkısı yoktur**. Ayrıca dayanıklılığı da artırmaz — dayanıklılık zaten
  changelog'dadır.

**Ayrıntı:**

| | `num.standby.replicas=0` | `num.standby.replicas=1` |
|---|---|---|
| Instance kaybında | Yeni sahip **sıfırdan restore** eder | Standby **anında devralır** |
| Disk maliyeti | 1× | **2×** |
| Ağ maliyeti | — | Standby sürekli changelog okur |
| Failover süresi | Dakikalar olabilir | Saniyeler |
| Throughput | — | **Değişmez** |

- **Ne zaman kullanılır:** Durum büyükse (GB'lar) ve kesinti toleransın düşükse. Küçük
  store'larda maliyeti kazancından fazladır.
- **İlgili mekanizma — warm-up replikaları:** `probing.rebalance.interval.ms` ile Streams,
  ölçekleme sırasında yeni instance'a task'ı **hemen** vermez; önce durumu hazırlar (warm-up),
  yakınsayınca devreder. Böylece ölçekleme de uzun duraklamalar üretmez.
- **Standby yerine ilk düşünülecek şey:** Kalıcı disk (PVC) + `group.instance.id`. Pod aynı
  diskle geri dönüyorsa restore zaten gerekmez ve standby'a gerek kalmaz.

> 📌 **Sık yapılan hata:** Standby'ı bir **yedekleme** sanmak. Veri kaybını standby önlemez;
> onu changelog önler. Standby yalnızca **hız** içindir.

🔗 Konu: [6.2 §4](6.2-state-store-ve-changelog.md)

---

### Soru 4 — Interactive query bir key'i bulamadı ama veri var: üç sebep

**Kısa cevap:**

1. **Key başka bir instance'ta.** Interactive query yalnızca **yerel task'ların** verisini görür.
   Aradığın key başka bir partition'a düşüyorsa, onu barındıran instance'a sormalısın:
   `streams.queryMetadataForKey(...)` ile doğru instance'ı bul, HTTP ile sor.
2. **Uygulama henüz `RUNNING` değil.** `STARTING`/`REBALANCING` durumunda store erişilemez.
   Lab'da tam olarak bu hata alındı:
   ```
   Cannot get state store ... because the stream thread is STARTING, not RUNNING
   ```
3. **Serde/key uyuşmazlığı.** Sorguladığın key'in serileştirilmiş hâli store'daki key ile
   birebir aynı olmalı. Farklı bir serde ya da farklı bir tip (ör. `Long` vs `String`) sessizce
   `null` döndürür.

**Ek sebepler:**

4. **Windowed store'u key-value store gibi sorgulamak.** Windowed store'da key
   `Windowed<K>`'dir; `keyValueStore` tipiyle sorgularsan bulamazsın —
   `QueryableStoreTypes.windowStore()` gerekir.
5. **Kayıt henüz store'a yazılmamış.** Önbellek (`statestore.cache.max.bytes`) ara güncellemeleri
   birleştirir; test/geliştirme sırasında `0` verilmezse beklediğin değer henüz görünmeyebilir.
6. **Standby'dan sorgulamak.** Varsayılan olarak yalnızca **aktif** task'lar sorgulanır;
   standby'dan okumak açıkça istenmelidir (ve o veri bir miktar geride olabilir).

**Doğru desen:**
```java
if (streams.state() != KafkaStreams.State.RUNNING) { /* bekle / 503 dön */ }
KeyQueryMetadata md = streams.queryMetadataForKey(STORE, key, serde.serializer());
if (md.activeHost().equals(benimHost)) { store.get(key); }
else { /* md.activeHost()'a HTTP ile sor */ }
```
Ve `KeyValueIterator`'ı **mutlaka kapat** — aksi hâlde RocksDB kaynak sızdırır.

> 📌 **Sık yapılan hata:** Tek instance'lı geliştirme ortamında yazılan sorgu kodunu, çok
> instance'lı üretime taşımak. Orada key'lerin çoğu **başka bir pod'dadır**.

🔗 Konu: [6.2 §5](6.2-state-store-ve-changelog.md)

---

### Soru 5 — Konteyner OOMKilled ama heap düşük: neden?

**Kısa cevap:** **RocksDB belleği JVM heap'inin dışındadır (off-heap).** Block cache, write
buffer'lar, index ve filter blokları, memtable'lar — hepsi native bellektir. Konteyner bellek
limiti **heap + off-heap + JVM overhead**'i kapsar; sadece `-Xmx`'e bakarak limit belirlersen
kernel OOM ([bellek tükenmesi ↗](../00-baslangic/02-kavram-sozlugu.md#oom)) killer devreye girer.

**Ayrıntı — konteyner belleğinin bileşenleri:**

| Bileşen | Nerede |
|---|---|
| JVM heap (`-Xmx`) | Heap |
| **RocksDB block cache + write buffer** | **Off-heap** ⚠️ |
| RocksDB index/filter blokları | Off-heap |
| JVM metaspace, code cache, thread stack'ler | Off-heap |
| Direct byte buffer'lar (ağ) | Off-heap |

- **Ölçek:** Her **task × store** için ayrı bir RocksDB örneği açılır. 8 task × 3 store = 24
  RocksDB örneği; her birinin kendi write buffer'ı ve block cache'i varsa off-heap bellek
  hızla GB'lara çıkar.
- **Çözüm:**
  1. `RocksDBConfigSetter` ile block cache ve write buffer'ları **açıkça sınırla** — ve mümkünse
     tüm store'lar için **paylaşımlı** bir block cache tanımla.
  2. Konteyner limitini `-Xmx + off-heap tahmini + %20 pay` olarak belirle.
  3. Task/store sayısını gözden geçir — gereksiz store'lar var mı?
- **İkinci olası sebep:** Çok sayıda **dosya tanıtıcısı** ve mmap. Her RocksDB örneği çok sayıda
  dosya açar; bu doğrudan OOM değil ama bellek baskısını artırır ve `ulimit` sorunlarına yol
  açar.
- **Belirti nasıl ayırt edilir:** Heap dump temiz, GC logları normal, ama `container_memory_rss`
  sürekli tırmanıyor → off-heap.

> 📌 **Sık yapılan hata:** Konteyner limitini `-Xmx`'in %20 fazlası olarak ayarlamak. Streams
> için bu neredeyse her zaman yetersizdir; RocksDB'yi hesaba katmadan konteyner boyutlandırmak
> en sık görülen Streams dağıtım hatasıdır.

🔗 Konu: [6.2 §6](6.2-state-store-ve-changelog.md)

---

## 6.3 Join ve windowing

📄 Sorular: [`6.3-join-ve-windowing.md`](6.3-join-ve-windowing.md)

### Soru 1 — 60 sn boy, 10 sn adımlı hopping: bir kayıt kaç pencereye düşer? Maliyeti?

**Kısa cevap:** **6 pencereye** (`boy / adım = 60 / 10 = 6`). Maliyeti: state store boyutu,
changelog trafiği ve çıktı sayısı **6 katına** çıkar — tek bir kayıt altı ayrı pencereyi
günceller, altı ayrı changelog kaydı yazar ve (suppress yoksa) altı ayrı çıktı üretir.

**Ayrıntı:**
```
HOPPING WINDOW — 10 sn boy, 5 sn adım   (boy/adım = 2)
  [a] 00:00:00..00:00:10 → 1
  [a] 00:00:05..00:00:15 → 1      ⬅ TEK kayıt, İKİ pencere
```

- **Genel formül:** `pencere_sayısı = ceil(boy / adım)`. 60 sn boy + **1 sn** adım = her kayıt
  **60 pencerede** — store ve changelog trafiği 60 katına çıkar. Bu, Streams'te sessizce
  kaynak yakan en klasik tasarım hatasıdır.
- **Nerede birikir:**
  - **RocksDB window store**: aynı key için 6 ayrı pencere girdisi.
  - **Changelog**: her güncelleme yazılır → 6× ağ ve disk.
  - **Restore süresi**: store büyüdüğü için uzar (6.2).
  - **Aşağı akış**: `suppress` yoksa 6× çıktı kaydı.
- **Alternatif — sliding window:** `SlidingWindows.ofTimeDifferenceWithNoGrace(...)` yalnızca
  **kayıt olan** noktalarda pencere üretir; hopping'in boş pencere üretme israfını ortadan
  kaldırır. "Son 60 saniyedeki toplam" ihtiyacında genelde daha doğru araçtır.
- **Adımı seçerken sor:** *"Sonucu gerçekten her `adım` saniyede bir güncellemem gerekiyor mu?"*
  Çoğu zaman cevap hayırdır ve adım büyütülebilir.

> 📌 **Sık yapılan hata:** "Daha sık güncelleme, daha iyi" diye adımı küçültmek. Maliyet
> `boy/adım` ile **doğrusal** artar ve fark edilmesi uzun sürer.

🔗 Konu: [6.3 §2](6.3-join-ve-windowing.md)

---

### Soru 2 — Pencereli aggregate hiç çıktı üretmiyor: üç sebep

**Kısa cevap:**

1. **Stream time ilerlemiyor.** Pencereler **duvar saatiyle değil, stream time ile** kapanır —
   yani o task'ın gördüğü **en büyük event time** ile. Partition'a yeni veri gelmezse pencere
   kapanmaz ve sonuç üretilmez. *"Test ortamında sonuç gelmiyor"* şikâyetinin **1 numaralı
   sebebi** budur.
2. **`suppress(untilWindowCloses)` kullanılıyor ve pencere kapanmıyor.** Nihai sonuç ancak stream
   time pencere sonunu + grace'i geçince yayılır. Düşük trafikli topic'lerde bu **hiç olmayabilir**.
3. **Önbellek ara sonuçları yutuyor.** `statestore.cache.max.bytes` (varsayılan 10 MiB) aynı
   key'e ardışık güncellemeleri birleştirir. Testte `0` verilmezse beklediğin çıktılar gelmez.

**Ek sebepler:**

4. **Timestamp extractor yanlış.** Varsayılan `FailOnInvalidTimestamp`'tır; kayıtların
   zaman damgası geçersizse uygulama **durur**. `WallclockTimestampExtractor` seçilmişse
   event time yerine işleme zamanı kullanılır ve pencereler beklediğinden farklı düşer.
5. **Kayıtlar grace dışında kalıyor** — geç gelen kayıtlar sessizce atılır; `dropped-records`
   metriği artar ama başka hiçbir belirti olmaz.
6. **Key `null`.** `groupByKey` ile null key'li kayıtlar atlanır.

**Teşhis sırası:** Önce `dropped-records` metriğine bak → sonra topolojiye bir `peek()` koyup
kayıtların gerçekten geldiğini doğrula → sonra `statestore.cache.max.bytes=0` ile çalıştır →
sonra timestamp extractor'ı kontrol et.

> 📌 **Sık yapılan hata:** Testte veri gönderip beklemek. `TopologyTestDriver` ile
> **timestamp'leri sen verirsin** ve stream time'ı kontrol edersin — pencereleri kapatmak için
> ileri bir timestamp'li bir kayıt daha göndermen gerekir (6.4).

🔗 Konu: [6.3 §1, §3, §5](6.3-join-ve-windowing.md)

---

### Soru 3 — Session window çıktısında `null` value ne demek? Aşağı akışta ne yapmalı?

**Kısa cevap:** `null` bir **tombstone**'dur — "bu oturum penceresi artık yok" demektir. İki olay
birleşip tek bir oturum oluşturduğunda, eski (küçük) pencere **silinir** ve yenisi yazılır.
Aşağı akış kodun bu `null` value'ları **açıkça ele almalıdır**: ilgili kaydı kendi durumundan
**silmelidir**, yoksa hayalet oturumlar birikir.

**Ayrıntı — lab çıktısı:**
```
SESSION WINDOW — 10 sn boşluk
  [a] 00:00:00..00:00:00 → 1        (ilk olay, tek noktalık oturum)
  [a] 00:00:00..00:00:00 → null     ⬅ TOMBSTONE: eski oturum siliniyor
  [a] 00:00:00..00:00:05 → 2        ⬅ birleştirilmiş yeni oturum
  [a] 00:00:40..00:00:40 → 1        (35 sn boşluk > 10 sn → YENİ oturum)
```

- **Neden böyle çalışır:** Oturum penceresinin **kimliği** `(key, başlangıç, bitiş)` üçlüsüdür.
  Yeni bir olay araya girip iki oturumu birleştirince, eski pencerelerin kimliği geçersiz olur.
  Kafka bunu silme = tombstone ile ifade eder (1.4'teki compaction tombstone'u ile aynı fikir).
- **Aşağı akışta ne yapılmalı:**
  ```java
  .toStream()
  .foreach((windowedKey, value) -> {
      if (value == null) { store.delete(windowedKey);  }   // oturum silindi
      else               { store.put(windowedKey, value); }
  });
  ```
- **Bir sink connector'a yazıyorsan** (7.1): `null` value bir **DELETE** olarak yorumlanmalıdır.
  Çoğu sink connector bunu destekler ama açıkça yapılandırılması gerekebilir.
- **`null`'ı filtrelemek en kötü seçenektir:** `filter((k, v) -> v != null)` yazarsan silme
  bilgisi kaybolur ve aşağı akışta **çift sayım** olur (hem eski hem yeni oturum durur).

> 📌 **Sık yapılan hata:** `null` çıktısını bir bug sanıp filtrelemek. O bir hata değil,
> **protokolün bir parçasıdır**.

🔗 Konu: [6.3 §2](6.3-join-ve-windowing.md) · [1.4 §4](../01-broker-depolama/1.4-retention-ve-compaction.md)

---

### Soru 4 — Stream-table join'de açılışta ilk kayıtlar neden eşleşmez? İki çözüm

**Kısa cevap:** Çünkü **tablo henüz dolmamıştır**. Stream-table join **asimetriktir**: sonuç
yalnızca **akış** tarafında kayıt geldiğinde üretilir ve o anda tablonun **o anki hâli**
kullanılır. Uygulama açılışında KTable'ın changelog'dan/topic'ten yüklenmesi zaman alır; bu
sırada gelen akış kayıtları **sessizce eşleşmez**.

**Ayrıntı — lab çıktısı:**
```
STREAM-TABLE JOIN
  (S-1 için çıktı yok)          ⬅ tabloda m1 henüz YOK
  [m1] S-2 → Ayşe               ⬅ tablo güncellendi, eşleşti
  [m1] S-3 → Ayşe Yılmaz        ⬅ tablonun GÜNCEL değeri kullanıldı
```
Dikkat: tablo sonradan güncellenince **geçmiş kayıtlar yeniden hesaplanmaz**. S-1 sonsuza kadar
eşleşmemiş kalır.

**İki çözüm:**

1. **`leftJoin` kullan ve `null` durumunu açıkça ele al.** Eşleşme olmadığında kaydı düşürmek
   yerine, "zenginleştirilememiş" olarak işaretle ve bir DLQ'ya ya da yeniden deneme akışına
   yönlendir. En azından **kayıp görünür** olur.
   ```java
   stream.leftJoin(table, (siparis, musteri) ->
       musteri != null ? zenginlestir(siparis, musteri) : eksik(siparis));
   ```
2. **`GlobalKTable` kullan.** GlobalKTable her instance'ta **tam kopya** tutulur ve uygulama
   `RUNNING` olmadan **tamamen yüklenir**. Ayrıca co-partitioning şartını da kaldırır. Bedeli:
   tablo her instance'ta bellekte/diskte durur — yalnızca **küçük referans tabloları** için uygun.

**Üçüncü yaklaşım:** Akış tarafını **geciktirmek** — tablo yüklenene kadar akışı işlememek. Bu,
Streams'te doğrudan desteklenmez; pratikte uygulamayı tablo hazır olduktan sonra trafiğe açmak
(readiness probe) şeklinde çözülür.

**İlgili şart — co-partitioning:** KStream-KTable join'de iki taraf **co-partitioned** (aynı partition sayısı, aynı anahtarlama, aynı key tipi) olmalıdır:
aynı partition sayısı, aynı partitioning stratejisi, aynı key tipi/serde. Sağlanmazsa Streams ya
repartition ekler ya da **hata verir**. GlobalKTable bu şartı kaldırır.

> 📌 **Sık yapılan hata:** `join` (inner) kullanıp eşleşmeyen kayıtların sessizce düşmesine izin
> vermek. Uygulama açılışında bu, **gerçek veri kaybıdır** ve hiçbir metrikte görünmez.

🔗 Konu: [6.3 §4](6.3-join-ve-windowing.md)

---

### Soru 5 — Grace period'ı büyütmenin iki olumlu, iki olumsuz sonucu

**Kısa cevap:**

**Olumlu 1 — Daha doğru sonuç.** Geç gelen kayıtlar pencereye dahil edilir ve pencere güncellenir.
Ağ gecikmesi, mobil istemcilerin çevrimdışı kalması, upstream retry'ları gibi sebeplerle geciken
veriyi kaybetmezsin.

**Olumlu 2 — Daha az sessiz veri kaybı.** Grace dışında kalan kayıtlar **sessizce atılır**
(`dropped-records` metriği artar, başka belirti yok). Grace'i gerçek gecikme dağılımına göre
seçmek bu kaybı azaltır.

**Olumsuz 1 — Store ve bellek büyür.** Pencereler daha uzun süre bellekte/diskte tutulur.
Grace'i 10 sn'den 10 dk'ya çıkarmak, aynı anda açık tutulan pencere sayısını ciddi artırır.

**Olumsuz 2 — Nihai sonuç gecikir.** `suppress(untilWindowCloses)` kullanıyorsan pencere
`stream_time > pencere_sonu + grace` olana kadar kapanmaz. Grace ne kadar büyükse nihai sonuç
o kadar geç gelir.

**Ayrıntı — lab çıktısı:**
```
GRACE PERIOD — 10 sn pencere + 5 sn tolerans
  [a] 00:00:00..00:00:10 → 1        (t=1)
  [a] 00:00:10..00:00:20 → 1        (t=13 → stream time 13)
  --- geç kayıt (t=2), grace içinde ---
  [a] 00:00:00..00:00:10 → 2        ⬅ KABUL edildi, pencere güncellendi
  --- stream time 30'a taşındı, sonra geç kayıt (t=4) ---
  [a] 00:00:30..00:00:40 → 1        ⬅ t=4 için ÇIKTI YOK: atıldı
```

| Kural | |
|---|---|
| Pencere **kapanır** | `stream_time > pencere_sonu + grace` |
| Grace içinde geç kayıt | Pencereyi **günceller**, yeni sonuç yayılır |
| Grace sonrası kayıt | **Sessizce atılır** (`dropped-records`) |
| `ofSizeWithNoGrace(...)` | Grace = 0 — geç kayıt kabul edilmez |

**Nasıl seçilir:** `dropped-records` metriğini izle ve gerçek gecikme dağılımını ölç. Grace'i
"olabildiğince büyük" değil, **p99 gecikmeyi kapsayacak kadar** seç.

> 📌 **Sık yapılan hata:** Grace'i sıfır bırakmak (`ofSizeWithNoGrace`) ve geç kayıtların
> atıldığını hiç fark etmemek. Kafka 3.0'dan beri grace'i **açıkça** belirtmeni isteyen API'ler
> bu yüzden vardır.

🔗 Konu: [6.3 §3, §5](6.3-join-ve-windowing.md)

---

## 6.4 EOS, hata yönetimi ve test

📄 Sorular: [`6.4-eos-hata-ve-test.md`](6.4-eos-hata-ve-test.md)

### Soru 1 — Üç hata katmanı ve varsayılan handler'ları

**Kısa cevap:**

| Katman | Ayar | Varsayılan |
|---|---|---|
| **Deserialization** (okuyamadım) | `deserialization.exception.handler` | `LogAndFailExceptionHandler` |
| **Processing** (işleyemedim, KIP-1033) | `processing.exception.handler` | `LogAndFailProcessingExceptionHandler` |
| **Production** (yazamadım) | `production.exception.handler` | `DefaultProductionExceptionHandler` |

Dördüncü bir katman daha var: **yakalanmamış istisnalar** —
`streams.setUncaughtExceptionHandler(...)`; varsayılan davranış thread'in kapanmasıdır.

**Ayrıntı — lab kanıtı, dört senaryo:**
```
===== 1a) DESERIALIZATION — LogAndFail (VARSAYILAN) =====
  ✗ uygulama DURDU: StreamsException → Size of data received by LongDeserializer is not 8
===== 1b) DESERIALIZATION — LogAndContinue =====
    işlendi: b=42
  ✓ uygulama ÇALIŞMAYA DEVAM ETTİ (bozuk kayıt atlandı)
===== 2a) PROCESSING — LogAndFail (VARSAYILAN) =====
  ✗ uygulama DURDU: StreamsException → işlenemez kayıt: BOZUK-kayit
===== 2b) PROCESSING — LogAndContinue (KIP-1033) =====
    işlendi: a=IYI-KAYIT
    işlendi: c=YINE-IYI
  ✓ uygulama ÇALIŞMAYA DEVAM ETTİ
```

**Neden varsayılanlar "fail"?** Çünkü sessizce devam etmek **veri kaybıdır** ve Kafka bu kararı
sana bırakır. Varsayılan davranış "dur ve haber ver"dir; devam etmeyi **bilinçli olarak**
seçmelisin — ve seçtiğinde DLQ kurmalısın (Soru 2).

> 📌 **Sık yapılan hata:** Yanlış katmana ayar yapmak. Deserialization hatası için
> `processing.exception.handler` ayarlamak hiçbir işe yaramaz; katmanı hata mesajından ayırt et.

🔗 Konu: [6.4 §1](6.4-eos-hata-ve-test.md)

---

### Soru 2 — `LogAndContinue`'yu DLQ olmadan kullanmanın riski

**Kısa cevap:** Bozuk kayıt **sessizce kaybolur**. Yalnızca loga bir satır yazılır ve kimse
bakmaz. Bu, 2.3'teki "producer callback'inde log yazıp geçmek" hatasının Streams'teki tam
karşılığıdır: uygulama sağlıklı görünür, metrikler temizdir, **veri gider**.

**Çözüm — KIP-1034 ile gelen yerleşik DLQ:**
```properties
errors.dead.letter.queue.topic.name=siparis-dlq
deserialization.exception.handler=org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
processing.exception.handler=org.apache.kafka.streams.errors.LogAndContinueProcessingExceptionHandler
```

| Özellik | Değer |
|---|---|
| Varsayılan | `errors.dead.letter.queue.topic.name = null` (**kapalı**) |
| Ne yazılır | Kaydın **alt topoloji girişindeki hâli**: orijinal key, value, header'lar |
| Hangi handler'lar | Yerleşik tüm exception handler'lar |
| Topic | **Sen oluşturmalısın** (auto-create kapalıysa) |

**Ayrıntı:**

- `LogAndContinue` + DLQ, *"durmasın ama kaybolmasın"* dengesini kurar. Bu, **Streams'teki tek
  en değerli üretim ayarıdır**.
- **DLQ topic'ini izlemeyi unutma.** Dolan ama kimsenin bakmadığı bir DLQ, kayıp verinin daha
  düzenli bir çöp kutusundan başka bir şey değildir. En azından "DLQ'ya kayıt düştü" alarmı kur.
- **İkinci risk:** Bozuk kayıtlar bir **desen** oluşturabilir (bir upstream sürümü hatalı yazıyor).
  DLQ olmadan bu deseni göremezsin; kaç kayıt, hangi key'ler, ne zamandan beri — hiçbiri bilinmez.
- **Üçüncü risk:** `LogAndContinue` ile ilerleyen bir uygulama, hatalı kayıtları atlayarak
  **yanlış bir aggregate** üretebilir. Toplam tutmaz ve sebebi aylar sonra aranır.

> 📌 **Sık yapılan hata:** Uygulama üretimde durdu diye panikle `LogAndContinue`'ya geçmek ve
> DLQ'yu "sonra ekleriz" demek. O "sonra" gelmez.

🔗 Konu: [6.4 §1–2](6.4-eos-hata-ve-test.md)

---

### Soru 3 — `exactly_once_v2` ile `at_least_once` neden farklı `commit.interval.ms`?

**Kısa cevap:** `at_least_once`'ta varsayılan **30000 ms**, `exactly_once_v2`'de **100 ms**.
Sebep: EOS'ta commit aralığı aynı zamanda **transaction sınırıdır**, ve bu sınır aşağı akıştaki
`read_committed` tüketicilerin veriyi **ne zaman görebileceğini** belirler. 30 saniyelik bir
transaction, tüm aşağı akışa 30 saniyelik bir gecikme eklerdi.

**Ayrıntı:**

| | `at_least_once` | `exactly_once_v2` |
|---|---|---|
| Garanti | Tekrar mümkün | Kafka içi **atomik** |
| `commit.interval.ms` | **30000** | **100** |
| Commit ne demek | Offset kaydetmek | **Transaction'ı kapatmak** |
| Gecikme etkisi | Yeniden işlenecek iş miktarı | **Aşağı akışın görünürlük gecikmesi** |
| Maliyet | Düşük | Transaction başına ek yazma + koordinasyon |

- **`at_least_once`'ta commit aralığı bir "kayıp/tekrar penceresidir":** Çökersen son commit'ten
  sonraki iş yeniden yapılır. 30 saniye makul bir denge — daha sık commit, daha çok offset yazma
  maliyeti demektir.
- **EOS'ta commit aralığı bir "görünürlük penceresidir":** Transaction kapanana kadar çıktı
  kayıtları `read_committed` tüketicilere **görünmez** (LSO, 4.1). Bu yüzden varsayılan
  agresif biçimde kısa tutulur.
- **Takas:** 100 ms ile saniyede 10 transaction açılıp kapanır; her biri coordinator kaydı ve
  control record yazar. Yani EOS'un maliyetinin önemli bir kısmı buradan gelir. Gecikme
  toleransın yüksekse `commit.interval.ms`'i artırarak bu maliyeti düşürebilirsin — ama aşağı
  akış gecikmesini kabul ederek.
- **Nasıl çalışır:** Streams her commit aralığında bir transaction açar; **çıktı kayıtları,
  changelog yazımları ve offset commit'i tek bir transaction'da** yapılır. Bu yüzden state store
  ile çıktı topic'i hiçbir zaman ıraksamaz.

> ⭐ **`exactly_once_v2`, eski `exactly_once`'ın yerini aldı:** v2, task başına ayrı producer
> yerine **instance başına tek producer** kullanır — çok daha az kaynak, çok daha iyi ölçekleme.
> Eski değer artık kullanılmamalıdır.

> 📌 **Sık yapılan hata:** EOS'a geçip aşağı akış tüketicilerini `read_committed` yapmamak.
> O zaman abort edilmiş kayıtlar da okunur ve EOS zinciri kırılır (4.1).

🔗 Konu: [6.4 §4](6.4-eos-hata-ve-test.md) · [4.1 §5](../04-eos-transaction/4.1-transactions-internals.md)

---

### Soru 4 — TopologyTestDriver testinde beklenen çıktı gelmiyor: ilk kontrol?

**Kısa cevap:** **`statestore.cache.max.bytes` (eski adıyla `cache.max.bytes.buffering`) sıfır
mı?** Streams önbelleği aynı key'e ardışık güncellemeleri **birleştirir** ve aşağı akışa her ara
sonucu göndermez. Testte önbellek açıksa beklediğin ara çıktılar **gelmez**.

```java
p.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);   // ara sonuçları da gör
```

**Ayrıntı — çalışan test kurulumu** (`mvn -q test -Dtest=SiparisTopolojisiTest` → **5 test,
0 hata**):
```java
@BeforeEach void setUp() {
    Properties p = new Properties();
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-app");
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:9092");   // kullanılmaz
    p.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);     // ⬅ KRİTİK
    driver = new TopologyTestDriver(topoloji(), p, T0);
    girdi = driver.createInputTopic("siparisler", ...)
    cikti = driver.createOutputTopic("musteri-toplam", ...);
}

@Test void toplamHesaplanir() {
    girdi.pipeInput("musteri-1", 100L, T0);
    girdi.pipeInput("musteri-1", 250L, T0.plusSeconds(1));
    assertEquals(List.of(new KeyValue<>("musteri-1", 100L),
                         new KeyValue<>("musteri-1", 350L)), cikti.readKeyValuesToList());

```
Önbellek açık olsaydı yalnızca `("musteri-1", 350L)` gelirdi ve test kırılırdı.

**İkinci kontrol — stream time.** Pencereli bir topolojide çıktı, stream time pencereyi
kapatana kadar gelmez. `pipeInput`'a **ileri bir timestamp** veren bir kayıt daha göndererek
stream time'ı ilerletmen gerekir (6.3 Soru 2).

**Üçüncü kontrol — serde uyumu.** `createInputTopic`/`createOutputTopic`'e verdiğin serde'ler
topolojinin beklediğiyle aynı mı?

**TopologyTestDriver'ın yetenekleri:**

| Yetenek | API |
|---|---|
| Girdi ver | `TestInputTopic.pipeInput(key, value, timestamp)` |
| Çıktı oku | `readKeyValue()` / `readKeyValuesToList()` / `isEmpty()` |
| **State store'u sorgula** | `driver.getKeyValueStore(...)`, `getWindowStore(...)` |
| **Zamanı ilerlet** | `pipeInput` timestamp'i (event time) · `advanceWallClockTime(...)` (punctuator) |

> 📌 **Sık yapılan hata:** Testte broker/Testcontainers'a geçmek. Önce
> `STATESTORE_CACHE_MAX_BYTES_CONFIG=0`'ı dene — "testte çıktı yok" şikâyetinin en sık sebebi budur.

🔗 Konu: [6.4 §5](6.4-eos-hata-ve-test.md) · [6.1 §5](6.1-topoloji-ve-task-modeli.md)

---

### Soru 5 — `REPLACE_THREAD` ne zaman tehlikeli? Nasıl güvenli hâle getirilir?

**Kısa cevap:** **Kalıcı bir hatada tehlikelidir** — thread ölür, yenisi başlar, aynı kayda
çarpar, yine ölür. **Sonsuz thread değiştirme döngüsü** oluşur: uygulama "çalışıyor" görünür
(süreç ayakta, health check yeşil) ama **hiç ilerlemez**, ve her turda CPU/log yakar.

**Nasıl güvenli hâle getirilir:**

1. **Hata sınıfına bak.** Yalnızca gerçekten geçici olduğunu bildiğin hatalarda `REPLACE_THREAD`
   dön:
   ```java
   streams.setUncaughtExceptionHandler(exception -> {
       if (exception instanceof TransientDbException) {
           return StreamThreadExceptionResponse.REPLACE_THREAD;
       }
       return StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
   });
   ```
2. **Deneme sayacı ekle.** Belirli bir zaman penceresinde N'den fazla replace olduysa
   `SHUTDOWN_CLIENT`'a düş:
   ```java
   if (replaceCount.incrementAndGet() > 5) return StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
   ```
3. **Metrik ve alarm.** Thread değiştirme sayısını dışa vur; sessiz döngüyü görünür kıl.
4. **Kök sebebi çözecek katmanı kullan.** Bozuk kayıt kaynaklı hatalar için doğru araç
   `REPLACE_THREAD` değil, **exception handler + DLQ**'dur (Soru 2).

**Üç seçeneğin karşılaştırması:**

| Yanıt | Ne yapar | Ne zaman |
|---|---|---|
| `REPLACE_THREAD` | Ölen thread'i yenisiyle değiştirir | Gerçekten **geçici** hatalar (ağ, DB) |
| `SHUTDOWN_CLIENT` | **Bu instance'ı** kapatır (diğerleri devralır) | Yerel/örneğe özgü sorun |
| `SHUTDOWN_APPLICATION` | Gruptaki **tüm instance'ları** kapatır | Veri bozulması, kod hatası |

> 📌 **Sık yapılan hata:** `REPLACE_THREAD`'i "uygulama durmasın" refleksiyle koşulsuz döndürmek.
> Bu, durmayı engellemez — **durduğunu fark etmeni** engeller.

🔗 Konu: [6.4 §3](6.4-eos-hata-ve-test.md)

---

⬅️ [Bölüme dön](6.1-topoloji-ve-task-modeli.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
