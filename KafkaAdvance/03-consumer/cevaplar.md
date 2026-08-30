# 03 · Consumer — Kendini kontrol cevapları

> Bu dosya [3.1](3.1-fetch-ve-poll-dongusu.md) – [3.4](3.4-share-groups.md) konularının sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [3.1](#31-fetch-ve-poll-döngüsü) · [3.2](#32-grup-protokolü-ve-rebalance) ·
[3.3](#33-offset-ve-commit) · [3.4](#34-share-groups)

---

## 3.1 Fetch ve poll döngüsü

📄 Sorular: [`3.1-fetch-ve-poll-dongusu.md`](3.1-fetch-ve-poll-dongusu.md)

### Soru 1 — Fetch 50.000 kayıt getirdi, `max.poll.records=500`: kaç `poll()` ağa çıkar?

**Kısa cevap:** **Hiçbiri** — ya da daha doğrusu, o 50.000 kaydı tüketen 100 `poll()` çağrısının
**hiçbiri o veri için ağa çıkmaz**. Kayıtlar consumer'ın yerel tamponundadır; `poll()` oradan
500'er 500'er servis eder. Ağa çıkan istek, tampon tükenmeye yaklaştığında **arka planda,
öngörülü (prefetch)** olarak gider.

**Ayrıntı:**

- Bu, konunun en sık karıştırılan ayrımıdır:
  - `fetch.*` ayarları = **ağdan ne kadar veri gelsin**
  - `max.poll.records` = **elime kaç kayıt gelsin**
- `poll()` beş iş yapar: koordinasyon → heartbeat → (varsa) otomatik commit → **gerekiyorsa**
  fetch → tampondan `max.poll.records` kadar kaydı deserialize edip döndürme. Dördüncü adım
  koşulludur.
- Pratik sonuç: **`poll()` süresi ağ gecikmesini ölçmez.** Bir benchmark'ta "poll 0,2 ms sürdü"
  görmen ağın hızlı olduğu anlamına gelmez; tampondan okuduğu anlamına gelir.
- Lab ölçümünde bu net görülüyor (200.000 kayıt × 300 bayt, 3 partition):

| kurulum | süre (ms) | poll sayısı | kayıt/poll | kayıt/sn |
|---|---|---|---|---|
| varsayılan | 656 | 400 | 500,0 | 304.878 |
| `max.poll.records=10` | 931 | **20.000** | 10,0 | 214.823 |
| `max.poll.records=5000` | 581 | **40** | 5000,0 | **344.234** |

  `max.poll.records=10` fetch sayısını **değiştirmedi**; sadece 20.000 kez döngü döndürdü ve
  throughput'u %30 düşürdü.

> 📌 **Sık yapılan hata:** `max.poll.records`'ı düşürmenin "ağ trafiğini azaltacağını" sanmak.
> Azaltmaz — döngü sayısını artırır, kayıt başına sabit maliyeti öne çıkarır.

🔗 Konu: [3.1 §1–2](3.1-fetch-ve-poll-dongusu.md)

---

### Soru 2 — Kayıt başına 400 ms işleme: `max.poll.records` en fazla kaç?

**Kısa cevap:** Formül:
```
max.poll.records × kayıt_başına_süre  <  max.poll.interval.ms
max.poll.records × 400 ms             <  300.000 ms
max.poll.records                      <  750
```
Güvenlik payıyla — kayıt süresinin ortalama değil **en kötü** hâlini ve GC/ağ duraklamalarını
hesaba katarak — **150-200** civarı seçilir. Ben pratikte **%25 kural**ı öneririm: teorik tavanın
dörtte biri, yani **~180**.

**Ayrıntı:**

- **Neden 750 değil?** Çünkü 400 ms bir **ortalamadır**. Aşağı akış sistemi (DB, API) yavaşladığı
  gün kayıt başına 2 saniye olabilir; 750 kayıt × 2 s = 25 dakika ≫ 5 dakika. Sonuç:
  **rebalance döngüsü** — consumer atılır, batch'in commit'i `CommitFailedException` alır, aynı
  kayıtlar yeniden işlenir, döngü baştan başlar. Grup hiç ilerlemez.
- **`max.poll.interval.ms`'i büyütmek neden ikinci tercih?** Onu büyütmek **gerçekten ölen** bir
  consumer'ın fark edilmesini de geciktirir. 30 dakikaya çıkarırsan, çöken bir pod'un
  partition'ları 30 dakika boyunca kimseye devredilmez.
- **Doğru sıra:**
  1. `max.poll.records`'ı düşür (ilk ve en güvenli hamle).
  2. İşleme süresini kısalt (batch DB yazımı, paralel I/O, gereksiz senkron çağrıları kaldır).
  3. Partition sayısını artırıp yatay ölçekle (5.5'teki sıra riskine dikkat).
  4. **En son** `max.poll.interval.ms`'i büyüt.
- Uzun süren tek bir kayıt için alternatif: kaydı **kabul et, kuyruğa al**, `pause()` ile o
  partition'ı duraklat ve `poll()` çağırmaya devam et (bkz. Soru 4).

> 📌 **Sık yapılan hata:** Hesabı ortalama süreyle yapıp pay bırakmamak. Üretimde
> `max.poll.interval.ms` aşımı **en sık görülen consumer arızasıdır**.

🔗 Konu: [3.1 §3](3.1-fetch-ve-poll-dongusu.md) · [3.2 §3](3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 3 — `fetch.min.bytes` nerede kazandırır, nerede zarar verir? Lab sonucu?

**Kısa cevap:**
- **Kazandırır:** **Seyrek trafikte.** Broker cevap vermeden önce en az N bayt biriktirir; boş
  ya da 3 kayıtlık fetch turları ortadan kalkar. Broker CPU'su, ağ round-trip sayısı ve consumer
  döngü sayısı düşer.
- **Zarar verir:** **Veri zaten hazırken** — geriden okuma, log'u baştan tarama, yoğun trafik.
  Orada asgari bayt şartı hiçbir şey biriktirmez, yalnızca **bekleme** ekler.

**Lab sonucu:** 200.000 kayıt × 300 bayt, dolu bir topic'i baştan okuma:

| kurulum | süre (ms) | kayıt/sn |
|---|---|---|
| varsayılan (`fetch.min.bytes=1`) | 656 | 304.878 |
| **`fetch.min.bytes=1MB`** | **1109** | **180.343** |

**%40 yavaşlama.** Sebep: veri zaten hazırdı; broker her fetch'te 1 MiB birikmesini ya da
`fetch.max.wait.ms` (500 ms) dolmasını bekledi.

**Ayrıntı — doğru okuma:** `fetch.min.bytes` bir **verim** ayarı değil, bir **verimlilik** ayarıdır.
İkilisi `fetch.max.wait.ms`'tir (varsayılan **500 ms**): asgari bayt dolmazsa en fazla bu kadar
beklenir. Yani `fetch.min.bytes` artırmanın gecikme maliyeti en kötü durumda
`fetch.max.wait.ms` kadardır.

**Ne zaman gerçekten açarsın:** Yüzlerce consumer'ın seyrek yazılan topic'leri dinlediği bir
cluster'da broker CPU'sunun büyük kısmı boş fetch isteklerini karşılamaya gidiyorsa. Orada
`fetch.min.bytes=10000` + `fetch.max.wait.ms=500` broker'ı ciddi rahatlatır.

> 📌 **Sık yapılan hata:** Lab'ın üçüncü dersi buydu: `max.partition.fetch.bytes`'ı 1 MiB'den
> 10 MiB'e çıkarmak **hiç kazandırmadı** (582 ms vs 581 ms) — çünkü darboğaz bayt değildi.
> **Bir ayarı büyütmeden önce darboğazın orada olduğunu ölç.**

🔗 Konu: [3.1 §2](3.1-fetch-ve-poll-dongusu.md) · [5.1 Performans tuning](../05-operasyon/5.1-performans-tuning.md)

---

### Soru 4 — `pause()` çağırdıktan sonra `poll()` etmeyi bırakırsan?

**Kısa cevap:** Grup seni **ölü sayar**. `max.poll.interval.ms` (varsayılan 5 dk) dolunca üye
gruptan atılır, partition'ların başkasına verilir ve o ana kadar yaptığın işin commit'i
`CommitFailedException` ile reddedilir. `pause()` ile "poll etmeyi bırakmak" **aynı şey değildir**.

**Ayrıntı:**

- `poll()` yalnızca kayıt getirmez; **koordinasyonu yürütür**: gruba katılma/ayrılma, rebalance
  cevapları, (klasik protokolde arka plan heartbeat'inin tetiklenmesi), otomatik commit.
  Çağırmayı bırakmak bu mekanizmayı durdurur.
- Doğru kalıp:
  ```java
  consumer.pause(Set.of(tp));                    // o partition'dan kayıt gelmesin
  consumer.poll(Duration.ofMillis(100));         // ama POLL ETMEYE DEVAM ET — kayıt döndürmez
  // ... aşağı akış düzelince
  consumer.resume(Set.of(tp));
  ```
- `pause()` edilmiş bir partition için `poll()` **boş döner** ve bu tamamen normaldir. Heartbeat
  ve koordinasyon çalışmaya devam eder.
- **Neden `Thread.sleep()` değil:** `sleep` poll döngüsünü durdurur ve yukarıdaki hataya yol açar.
  Consumer'ı yavaşlatmanın doğru yolu **her zaman** `pause`/`resume`'dur.
- Yeni protokolde (KIP-848) heartbeat de `poll()` üzerinden akar; poll etmemek orada daha da
  doğrudan bir ölüm sinyalidir.

> 📌 **Sık yapılan hata:** Aşağı akış (DB/API) yavaşladığında `Thread.sleep(5000)` koyup sorunu
> "çözmek". Bu, backpressure'ı rebalance fırtınasına çevirir.

🔗 Konu: [3.1 §4](3.1-fetch-ve-poll-dongusu.md)

---

### Soru 5 — `assign()` kullanan bir consumer'a ikinci instance eklersen?

**Kısa cevap:** **Hiçbir koordinasyon olmaz** — iki instance da atadıkları partition'ları
**paralel olarak, baştan sona** okur. Aynı kayıtlar iki kez işlenir. Kafka bunu engellemez,
çünkü `assign()` grup üyeliği kurmaz: rebalance yok, sahiplik yok, çakışma denetimi yok.

**Ayrıntı:**

| | `subscribe()` | `assign()` |
|---|---|---|
| Grup üyeliği | **Var** — rebalance, sahiplik | **Yok** |
| Partition ataması | Koordinatör yapar | **Sen** yaparsın |
| İkinci instance | Otomatik dağılır | **Aynı işi tekrarlar** |
| Offset commit | Grup offset'i | `group.id` verirsen mümkün, ama koruma yok |

- `group.id` versen bile durum değişmez: offset'ler `__consumer_offsets`'e yazılabilir ama iki
  instance **birbirinin offset'ini ezer** — her ikisi de son commit ettiği yerden devam eder,
  aralarında hiçbir bölüşme olmaz.
- **Ne zaman `assign()` doğrudur:** Belirli bir partition'ı incelemek, araç yazmak, deterministik
  test/benchmark yapmak (bu setteki lab'ların çoğu bu yüzden `assign()` kullanır — ölçüm sırasında
  rebalance gürültüsü istemiyoruz), ya da offset'i tamamen dışarıda (DB'de) yöneten bir tasarım.
- **Üretim kodunda varsayılan tercihin `subscribe()` olmalı.**

> 📌 **Sık yapılan hata:** `assign()` kullanan bir uygulamayı Kubernetes'te `replicas: 3` ile
> ölçeklemek. Ölçeklenmiş olmaz — **üç kat duplicate** üretmiş olursun ve bu, aylarca fark
> edilmeyebilir.

🔗 Konu: [3.1 §5](3.1-fetch-ve-poll-dongusu.md)

---

## 3.2 Grup protokolü ve rebalance

📄 Sorular: [`3.2-grup-protokolu-ve-rebalance.md`](3.2-grup-protokolu-ve-rebalance.md)

### Soru 1 — 6 dakika süren işleme, süreç yaşıyor: hangi zamanlayıcı devreye girer?

**Kısa cevap:** **`max.poll.interval.ms`** (varsayılan **300.000 ms = 5 dakika**) devreye girer;
üye gruptan atılır. `session.timeout.ms` (45 s) devreye **girmez**, çünkü klasik protokolde
heartbeat'i **ayrı bir arka plan thread'i** gönderir — işleme takılsa bile heartbeat akmaya
devam eder.

**Ayrıntı — üç zamanlayıcının ayrımı:**

| Ayar | Varsayılan | Neyi ölçer | Aşılırsa |
|---|---|---|---|
| `heartbeat.interval.ms` | 3000 | Heartbeat sıklığı | — |
| `session.timeout.ms` | **45000** | Heartbeat gelmezse ölü sayma | **Süreç/ağ öldü** |
| `max.poll.interval.ms` | **300000** | İki `poll()` arası süre | **Süreç ilerlemiyor** |

> **Ayrım tek cümlede:** `session.timeout.ms` *"süreç yaşıyor mu?"*, `max.poll.interval.ms`
> *"süreç ilerliyor mu?"* sorusunu sorar.

**Ne olur, sırayla:** 5. dakikada koordinatör üyeyi atar → rebalance başlar → partition'lar
başkasına verilir → 6. dakikada işleme biten consumer commit etmeye çalışır →
**`CommitFailedException`** (generation eskimiş) → aynı kayıtlar yeni sahibi tarafından yeniden
işlenir → o da 6 dakika sürer → döngü.

**Çözüm sırası:** `max.poll.records`'ı düşür → işleme süresini kısalt → gerekirse partition
artır → **en son** `max.poll.interval.ms`'i büyüt.

**Yeni protokolde (KIP-848) ne değişir:** `session.timeout.ms` ve `heartbeat.interval.ms`
**broker tarafında** belirlenir (`group.consumer.session.timeout.ms`), client ayarları yok
sayılır. `max.poll.interval.ms` hâlâ client tarafındadır ve aynı işi yapar.

> 📌 **Sık yapılan hata:** Belirtiye bakıp `session.timeout.ms`'i büyütmek. Yanlış zamanlayıcıyı
> ayarlamış olursun; hiçbir şey değişmez.

🔗 Konu: [3.2 §3](3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 2 — Klasik protokolde atamayı kim hesaplar? İki dezavantajı?

**Kısa cevap:** Atamayı **grup lideri** hesaplar — ve grup lideri bir **broker değil, bir
consumer'dır**. Koordinatör yalnızca üye listesini ona iletir ve sonucu dağıtır.

**İki dezavantajı:**

1. **Atama mantığı client'ta çalışır → sürüm/strateji uyuşmazlığı grubu çökertir.** Grup
   üyelerinin ortak bir assignor üzerinde anlaşması gerekir. Bir pod eski, biri yeni bir strateji
   listesiyle gelirse grup **hiç oluşmaz**. Kütüphane yükseltmesi bir dağıtık sistem olayına döner.
2. **Global barrier: bir yavaş üye tüm grubu durdurur.** Koordinatör tüm üyelerin JoinGroup'a
   gelmesini bekler (rebalance timeout'a kadar). Eager assignor ile bu süre boyunca **herkes her
   şeyi bırakmıştır** — grup tamamen durur (stop-the-world).

**Ayrıntı — akış:**
```
1. Consumer → koordinatör : JoinGroup
2. Koordinatör            : tüm üyeler gelene kadar BEKLER   ← barrier
3. Koordinatör            : üyelerden birini GRUP LİDERİ seçer
4. Grup lideri (consumer) : atamayı HESAPLAR                 ← client'ta mantık
5. Lider → koordinatör    : SyncGroup (atama tablosu)
6. Koordinatör → herkes   : herkesin kendi ataması
```

**Üçüncü dezavantaj (bonus):** Her rebalance yeni bir **generation** üretir; eski generation ile
yapılan commit'ler reddedilir → yeniden işleme.

**Kısmi çözüm:** `CooperativeStickyAssignor` barrier'ı kaldırmaz ama "herkes her şeyi bırakır"
kısmını çözer. Lab kanıtı — 3. consumer eklendiğinde:
```
[c2] GERİ ALINDI : [5]     ← YALNIZCA taşınacak olanlar
[c1] GERİ ALINDI : [4]
[c3] ATANDI      : [4, 5]
```
Eager bir assignor'da `[1,3,5]` ve `[0,2,4]`'ün **tamamı** geri alınırdı.

**Tam çözüm:** KIP-848 — atamayı **broker** yapar, barrier yoktur.

> 📌 **Sık yapılan hata:** Grup liderini partition lideriyle karıştırmak. Grup lideri bir
> **consumer**, partition lideri bir **broker**'dır; hiçbir ilgileri yoktur.

🔗 Konu: [3.2 §2](3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 3 — KIP-848'de "rebalance süresi" ölçmek neden zor? Yerine ne ölçersin?

**Kısa cevap:** Çünkü artık **tek bir "rebalance anı" yok**. Değişiklikler heartbeat'lerle
**damla damla** yayılır; üye bazında ilerler, global bir başlangıç/bitiş noktası bulunmaz.
Ölçülecek şey rebalance süresi değil, **yakınsama (convergence)** ve onun iş üzerindeki etkisidir.

**Ne ölçersin:**

1. **Yakınsama süresi:** İlk üyelik değişikliğinden, tüm partition'ların **kararlı** olarak
   atandığı ana kadar geçen süre. Grubun `STATE` alanını ve atama tablosunu periyodik
   örnekleyerek çıkarılır.
2. **Partition başına "sahipsiz kalma" süresi:** Asıl önemli metrik budur — bir partition kaç
   saniye hiç kimse tarafından tüketilmedi?
3. **Lag sıçraması:** Üyelik değiştiğinde `records-lag-max` ne kadar yükseldi, ne kadar sürede
   normale döndü? İşe etkiyi doğrudan bu gösterir.
4. **`rebalance-rate-per-hour` / `failed-rebalance-rate-per-hour`** (client metrikleri):
   Süreyi değil **sıklığı** ölçer. Fırtına teşhisi için asıl işe yarayan budur.

**Ayrıntı — lab kanıtı:** Aynı senaryo, yeni protokolle:
```
[c2] ATANDI      : [0, 1, 2, 3, 4, 5]   ← ilk anda tek üye tamamını aldı
[c2] GERİ ALINDI : [3, 4, 5]            ← c1 katılınca kademeli devir
=== 3. consumer ekleniyor ===
[c3] ATANDI      : [5]
[c1] ATANDI      : [3, 4]
[c2] GERİ ALINDI : [2]
[c3] ATANDI      : [2]
```
Hiçbir noktada tüm grup durmuyor; atama birkaç heartbeat sürüyor. Kısa testlerde "atama neden
hemen dengelenmedi?" diye şaşırma — protokol **kademeli** çalışıyor.

> 📌 **Sık yapılan hata:** Klasik protokolden alınan "ortalama rebalance süresi" dashboard'unu
> yeni protokole taşıyıp grafiğin boşalmasına bakarak "rebalance yok" sonucuna varmak.
> Metriğin tanımı değişti, olay değil.

🔗 Konu: [3.2 §4](3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 4 — 20 pod'lu tüketiciyi kesintisiz güncellemek: hangi üç ayar?

**Kısa cevap:**

1. **`group.instance.id`** (static membership) — pod ordinal'i gibi **kararlı** bir değer.
   Restart eden pod "ayrılmış" sayılmaz; `session.timeout.ms` içinde dönerse aynı partition'larını
   geri alır ve **rebalance hiç tetiklenmez**.
2. **`group.protocol=consumer`** (KIP-848) — atama broker'da, barrier yok, değişiklik yalnızca
   taşınan partition'ları etkiler. (Bu mümkün değilse ikinci en iyisi:
   `partition.assignment.strategy=CooperativeStickyAssignor`.)
3. **`session.timeout.ms`** — pod'un yeniden başlama süresinden **büyük** olmalı ki static
   membership işe yarasın. Yeni protokolde bu broker tarafında
   (`group.consumer.session.timeout.ms`, lab cluster'ında **45000**, min 45000 / max 60000)
   ayarlanır; client değeri yok sayılır.

**Ayrıntı — kod ve dağıtım:**
```java
props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "worker-" + podOrdinal);  // "worker-3"
props.put(ConsumerConfig.GROUP_PROTOCOL_CONFIG, "consumer");
```
- Kubernetes'te **StatefulSet** kullan: pod adı (`worker-0`, `worker-1`, …) kararlıdır ve
  ordinal'i doğrudan `group.instance.id` yapabilirsin. Deployment'ta pod adları rastgeledir.
- **Rolling update stratejisi:** `maxUnavailable: 1` ile tek tek değiştir. Static membership
  sayesinde her pod'un inip kalkması rebalance üretmez.
- **Düzgün kapanış şart:** `consumer.close()` çağrılmalı ki üye gruptan temiz ayrılsın; ayrıca
  `terminationGracePeriodSeconds` işleme süresinden uzun olmalı.

**Bedeli:** Static membership ile **gerçekten ölen** bir üye de `session.timeout.ms` boyunca
yerini tutar — yani gerçek arıza tespiti yavaşlar. Bu bilinçli bir takastır.

> 📌 **Sık yapılan hata:** `group.instance.id`'yi UUID gibi **rastgele** üretmek. O zaman hiçbir
> işe yaramaz; her restart yeni bir üye gibi görünür. Değer **kararlı** olmalıdır.

🔗 Konu: [3.2 §5–6](3.2-grup-protokolu-ve-rebalance.md)

---

### Soru 5 — Bir broker çöktüğünde neden sadece bazı consumer group'lar etkilenir?

**Kısa cevap:** Çünkü her grubun **koordinatörü** farklı bir broker olabilir. Koordinatör şöyle
belirlenir:
```
coordinator = __consumer_offsets partition'ı ( hash(group.id) % 50 ) → o partition'ın LİDERİ
```
Çöken broker yalnızca **kendisinin lideri olduğu** offset partition'larına denk gelen grupların
koordinatörüdür. Diğer grupların koordinatörü başka broker'dadır ve hiç etkilenmezler.

**Ayrıntı:**

- `offsets.topic.num.partitions` varsayılan **50**'dir. 50 partition 3 broker'a dağılmıştır;
  yani her broker ~17 offset partition'ının lideridir ve o partition'lara hash'lenen grupların
  koordinatörüdür.
- Koordinatör kaybolduğunda o gruplar geçici olarak `COORDINATOR_NOT_AVAILABLE` /
  `NOT_COORDINATOR` alır, yeni lider seçilene kadar commit edemez, sonra yeni koordinatörü bulup
  devam ederler. Genelde saniyeler sürer.
- **Kritik ayrım:** Koordinatör, grubun **tükettiği topic'in** liderleriyle ilgisizdir. Bir grup,
  çöken broker'ın lider olduğu partition'ları tüketiyor olabilir ama koordinatörü başka
  broker'dadır — o zaman *okuma* etkilenir, *koordinasyon* etkilenmez. Tersi de mümkündür.
- Nasıl görürsün:
  ```
  $ kafka-consumer-groups.sh --describe --state --group rebalance-demo-consumer
  GROUP                   COORDINATOR (ID)     ASSIGNMENT-STRATEGY  STATE   #MEMBERS
  rebalance-demo-consumer kafka-2:19092  (2)   uniform              Empty   0
  ```

> 📌 **Sık yapılan hata:** "Broker 2 çöktü ama A grubu etkilendi, B etkilenmedi — tutarsızlık var"
> demek. Tutarsızlık yok; koordinatör ataması **grup adının hash'ine** bağlı, deterministik bir
> dağılımdır.

🔗 Konu: [3.2 §1](3.2-grup-protokolu-ve-rebalance.md)

---

## 3.3 Offset ve commit

📄 Sorular: [`3.3-offset-ve-commit.md`](3.3-offset-ve-commit.md)

### Soru 1 — 0–9 arası kayıtları işledin: commit edeceğin değer?

**Kısa cevap:** **10.** Commit edilen offset "işlediğim son kayıt" değil, **"bir sonraki okunacak
offset"**tir. Yani `son_işlenen_offset + 1`.

**Ayrıntı:**

```java
consumer.commitSync(Map.of(tp, new OffsetAndMetadata(record.offset() + 1)));
//                                                                    ^^^ +1
```
- `+1` unutulursa ne olur? Her yeniden başlatmada **son kayıt tekrar işlenir**. Tek kayıtlık,
  sessiz, sürekli bir duplicate kaynağı. Kimse fark etmez çünkü sistem "çalışıyor" görünür.
- `commitSync()` argümansız çağrılırsa (yaygın kullanım) Kafka bu hesabı **kendisi** yapar:
  son `poll()`'un döndürdüğü kayıtların bir fazlasını commit eder. Elle offset verdiğinde
  `+1` senin sorumluluğundur.
- Aynı mantık `seek()` için de geçerlidir: zehirli kaydı atlamak için `seek(tp, offset + 1)`.
- Offset'ler nerede tutulur:

| | Detay |
|---|---|
| Topic | `__consumer_offsets` |
| Partition sayısı | `offsets.topic.num.partitions` — **50** |
| Politika | `compact` — key başına son değer korunur |
| Key | `(group.id, topic, partition)` |
| Saklama | `offsets.retention.minutes` — **10080 (7 gün)** |

> 📌 **Sık yapılan hata:** `+1`'i unutmak. Kod çalışır, testler geçer, üretimde her restart'ta
> bir kayıt tekrar işlenir.

🔗 Konu: [3.3 §1](3.3-offset-ve-commit.md)

---

### Soru 2 — Otomatik commit hangi durumda veri kaybettirir? Somut kod deseni.

**Kısa cevap:** İşi **poll döngüsünün dışına** attığında. Otomatik commit `poll()` çağrısı içinde
yapılır ve **bir önceki poll'un döndürdüğü tüm kayıtları** commit eder — o kayıtların gerçekten
işlenip işlenmediğine bakmadan. İşi bir thread pool'a ya da kuyruğa devrediyorsan Kafka
"işlendi" sanır; süreç çökerse o kayıtlar **hiç işlenmemiş olarak kaybolur**.

**Kaybettiren desen:**
```java
// ❌ VERİ KAYBETTİREN DESEN
props.put(ENABLE_AUTO_COMMIT_CONFIG, true);       // varsayılan!
ExecutorService pool = Executors.newFixedThreadPool(8);

while (running) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
    for (ConsumerRecord<String, String> r : records) {
        pool.submit(() -> process(r));            // işi DEVRET, hemen dön
    }
    // döngü başa döner → poll() → 5 sn geçtiyse HEPSİ commit edilir
    // pool'daki işler henüz bitmemiş olabilir. Süreç şimdi çökerse: KAYIP.
}
```

**Güvenli desen:**
```java
// ✅ İşleme poll döngüsünde ve SENKRON
props.put(ENABLE_AUTO_COMMIT_CONFIG, false);
while (running) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
    for (ConsumerRecord<String, String> r : records) {
        process(r);                                // ÖNCE İŞLE
    }
    consumer.commitAsync();                        // SONRA commit et
}
```

**Ayrıntı:**
- Otomatik commit ayarları: `enable.auto.commit=true` (varsayılan),
  `auto.commit.interval.ms=5000` (varsayılan).
- **Kural:** İşleme `poll()` döngüsünün içinde ve senkronsa, otomatik commit at-least-once'a
  yakın davranır (çöküşte son 0–5 saniyelik iş **yeniden** işlenir — duplicate, kayıp değil).
  İşi başka bir thread'e/kuyruğa atıyorsan otomatik commit **veri kaybettirir**.
- İkinci kayıp senaryosu: kayıtları işlemeden `continue` edip bir sonraki `poll()`'a girmek
  (ör. bir filtreleme hatası, bir `return`).

> 📌 **Sık yapılan hata:** "Paralel işleyelim, hızlansın" diye kayıtları thread pool'a atmak ve
> `enable.auto.commit`'i varsayılan (açık) bırakmak. Bu, üretimdeki sessiz veri kaybının en
> yaygın consumer tarafı sebebidir.

🔗 Konu: [3.3 §2](3.3-offset-ve-commit.md)

---

### Soru 3 — `commitAsync()` neden retry yapmaz? Hangi hatayı önler?

**Kısa cevap:** Retry yapsaydı, **geç gelen bir commit daha yenisini ezebilirdi**. Asenkron
commit'ler sırasız tamamlanabilir: offset 100 için yapılan commit başarısız olup yeniden
denenirken, offset 200 için yapılan commit çoktan başarılı olmuş olabilir. Gecikmiş 100
commit'i başarılı olursa grup offset'i **200'den 100'e geri düşer** — ve süreç çökerse
100–200 arası **yeniden işlenir**.

**Ayrıntı:**

| | `commitSync()` | `commitAsync()` |
|---|---|---|
| Bloklar mı | Evet | Hayır |
| Retry | **Otomatik** (timeout'a kadar) | **Yok** |
| Hız | Yavaş | Hızlı |
| Kullanım | Kapanış, rebalance öncesi, kritik nokta | Normal döngü |

- `commitSync()` neden retry yapabiliyor? Çünkü **bloklar** — bir sonraki commit'e sıra gelmeden
  bu commit tamamlanır. Sıra dışılık ihtimali yoktur.
- **Üretim kalıbı — ikisi birlikte:**
  ```java
  try {
      while (running) {
          ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
          for (ConsumerRecord<String, String> r : records) process(r);
          consumer.commitAsync();               // hızlı yol
      }
  } catch (WakeupException e) {
      // kapatma sinyali — normal
  } finally {
      try { consumer.commitSync(); }            // son bir kez, garantili
      finally { consumer.close(); }             // gruptan düzgün ayrıl
  }
  ```
- `finally`'deki `commitSync()` şu deliği kapatır: sadece `commitAsync()` kullanan bir döngüde
  **son** commit başarısız olursa fark etmezsin ve yeniden başlatmada o batch tekrar işlenir.
- `commitAsync(callback)` ile hatayı **görebilirsin** — ve görmelisin: en azından bir metrik
  artır, log yaz.

> 📌 **Sık yapılan hata:** `commitAsync()`'i callback'siz kullanmak. Hatalar tamamen görünmez
> olur; sistemin at-least-once garantisi sessizce zayıflar.

🔗 Konu: [3.3 §3](3.3-offset-ve-commit.md)

---

### Soru 4 — `onPartitionsRevoked` ile `onPartitionsLost` neden farklı davranmalı?

**Kısa cevap:**
- **`onPartitionsRevoked`**: Partition **henüz senin** — düzenli bir devir teslim yapılıyor.
  Burada **commit etmelisin**; son işlediğin yeri kaydetmenin son şansı.
- **`onPartitionsLost`**: Partition **zaten kaybedildi** — üye gruptan atıldı, sahibi artık
  başkası. Burada **commit etmemelisin**; yalnızca yerel durumu temizlemelisin.

**Ayrıntı:**

```java
@Override public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    consumer.commitSync(currentOffsets);      // ✅ son şans
}
@Override public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
    // gerekirse dış kaynaktan offset yükle ve seek() yap
}
@Override public void onPartitionsLost(Collection<TopicPartition> partitions) {
    cleanupLocalState(partitions);            // ✅ sadece temizlik
    // ❌ commitSync() burada CommitFailedException üretir
}
```

**Neden commit etmemelisin — iki sebep:**
1. **Teknik olarak başarısız olur.** Generation eskimiştir; `CommitFailedException` alırsın ve
   rebalance callback'i içinde fırlayan bir istisna daha karmaşık hatalara yol açar.
2. **Doğru olsaydı bile tehlikeli olurdu.** Partition'ın yeni sahibi çoktan okumaya başlamış
   olabilir. Senin eski offset'ini yazman, onun ilerlemesini **geri sarardı**.

**`onPartitionsLost` ne zaman çağrılır:** Cooperative protokolde, üye `max.poll.interval.ms`
aşımı ya da `session.timeout.ms` ile gruptan **atıldığında**. Yani "ben ayrılmadım, atıldım"
durumu. Varsayılan implementasyonu `onPartitionsRevoked`'u çağırır — bu yüzden **açıkça
override etmek** önemlidir.

> 📌 **Sık yapılan hata:** Sadece `onPartitionsRevoked`'u yazıp `onPartitionsLost`'u varsayılana
> bırakmak. Atılma senaryosunda commit denemesi yapılır ve gereksiz hata gürültüsü üretilir.

🔗 Konu: [3.3 §5](3.3-offset-ve-commit.md)

---

### Soru 5 — Bir tüketici 10 gün durdu: açıldığında ne olabilir?

**Kısa cevap — iki ihtimal:**

1. **Offset'leri silinmiştir.** `offsets.retention.minutes` varsayılanı **10080 dakika = 7
   gün**dür. 10 gün commit yapmayan bir grubun offset'leri `__consumer_offsets`'ten temizlenir.
   Consumer artık `auto.offset.reset`'e düşer: `latest` ise **aradaki tüm veriyi atlar** (sessiz
   veri kaybı), `earliest` ise **her şeyi baştan işler** (duplicate seli), `none` ise
   `NoOffsetForPartitionException` fırlatıp durur.

2. **Offset'i duruyor ama veri silinmiştir.** `retention.ms` (varsayılan 7 gün) dolduğu için
   commit edilmiş offset artık log'un **başlangıcından önce** kalır. Consumer
   `OffsetOutOfRangeException` alır ve yine `auto.offset.reset`'e düşer — bu kez `earliest`
   davranışı "log'un mevcut başından oku" olur, yani **aradaki veri gerçekten kayıptır**.

**Ayrıntı — üçüncü ihtimal:** İkisi de olmamıştır (retention uzun tutulmuş, grup başka bir
instance ile commit etmeye devam etmiş) ve consumer **10 günlük lag** ile açılır. Bu da bir
sorundur: geriden okuma tiered storage'da pahalıdır (1.5), fetch'ler büyük olur, aşağı akış
sistemleri ani yükle karşılaşır.

**Nasıl önlenir:**
- Uzun duracak gruplar için `offsets.retention.minutes`'ı topic/cluster seviyesinde artır.
- `auto.offset.reset` seçimini **bilinçli** yap: kritik verilerde `none` + açık bir alarm,
  `latest`'in sessiz atlamasından iyidir.
- Tüketici uzun süre duracaksa, dönüşten önce grubun offset durumunu
  `kafka-consumer-groups.sh --describe` ile **kontrol et**; gerekiyorsa `--reset-offsets
  --to-datetime` ile bilinçli bir noktaya taşı (grup durmuşken, `--execute` ile).

> 📌 **Sık yapılan hata:** `auto.offset.reset=latest`'i "güvenli varsayılan" sanmak. En sessiz
> veri kaybı yolu budur: hiçbir hata çıkmaz, sadece bir hafta veri atlanır.

🔗 Konu: [3.3 §1, §7](3.3-offset-ve-commit.md) · [1.4 Retention](../01-broker-depolama/1.4-retention-ve-compaction.md)

---

## 3.4 Share groups

📄 Sorular: [`3.4-share-groups.md`](3.4-share-groups.md)

### Soru 1 — 4 partition, 50 worker, sıra önemsiz: hangi model?

**Kısa cevap:** **Share group.** Klasik consumer group'ta paralellik tavanı partition sayısıdır:
4 partition → 4 aktif tüketici, **46 worker boş oturur**. Share group'ta atama birimi
**partition değil kayıt** olduğu için 50 worker aynı 4 partition'dan paralel çalışabilir.

**Ayrıntı:**

| | Consumer group | Share group |
|---|---|---|
| Atama birimi | **Partition** | **Kayıt** |
| Tüketici tavanı | Partition sayısı | `group.share.max.size` (**200**) |
| Sıra garantisi | Partition içinde var | **Yok** |
| Başarısız kaydı devretme | Yok | **Var** (RELEASE) |
| Zehirli kayda karşı koruma | Yok | `delivery.count.limit` (**5**) sonrası arşivleme |

- **Alternatif çözüm neden kötü:** "Partition'ı 50'ye çıkaralım" demek 50 partition'lık bir
  topic'in maliyetini (metadata, açık dosya, replikasyon, lider dengesi) üstlenmek ve yine de
  kayıt bazlı yeniden dağıtım kazanamamak demektir. Ayrıca key kullanıyorsan sıra kırılır (2.1).
- **Bu senaryonun tipik örneği:** Uzun süren, birbirinden bağımsız işler — OCR, dış API çağrısı,
  görüntü işleme, e-posta gönderimi. İşleme süresi uzun, sıra önemsiz, worker sayısı iş yüküne
  göre değişken.
- **Ek kazanç:** Bir worker yavaş ya da hatalıysa aldığı kayıt `RELEASE` ile başkasına gider;
  klasik grupta o partition'ın **tamamı** beklerdi.

> 📌 **Sık yapılan hata:** Share group'u consumer group'un yerine geçen bir şey sanmak. **Yanına**
> gelir. Sıra, transaction ve Kafka Streams hâlâ consumer group üzerine kuruludur.

🔗 Konu: [3.4 §1, §6](3.4-share-groups.md)

---

### Soru 2 — RELEASE ile REJECT farkı; hangisi ne zaman?

**Kısa cevap:**
- **`RELEASE` = "şu an işleyemedim"** → kayıt **yeniden teslim edilir** (sana ya da başkasına).
  **Geçici** hatalar için: aşağı akış servisi 503 döndü, DB bağlantısı koptu, rate limit'e takıldı.
- **`REJECT` = "bu kayıt işlenemez"** → kayıt **arşivlenir**, bir daha **hiç** teslim edilmez.
  **Kalıcı** hatalar için: kayıt bozuk, şema tanınmıyor, iş kuralı gereği geçersiz.

**Ayrıntı — kaydın yaşam döngüsü:**
```
        poll()                    acknowledge(ACCEPT)
 Available ──────► Acquired ─────────────────────────► Acknowledged (bitti)
     ▲                │
     │                │ acknowledge(RELEASE) ya da kilit süresi doldu
     └────────────────┤
                      │ acknowledge(REJECT) ya da teslim sayısı limiti aştı
                      ▼
                  Archived  (bir daha teslim edilmez)
```

- **REJECT geri dönüşü olmayan bir karardır.** Kaydı arşivlemeden önce onu bir **DLQ topic'ine
  yazmalısın** — yoksa veriyi tamamen kaybedersin. Doğru desen: önce DLQ'ya yaz, sonra REJECT.
- **RELEASE'in bedeli:** Kayıt tekrar tekrar dolaşır ve her turda kaynak harcar.
  `delivery.count.limit` (**5**) bunu sınırlar: 5. denemeden sonra kayıt otomatik **arşivlenir**.
- **Kod:**
  ```java
  try {
      process(r);
      consumer.acknowledge(r, AcknowledgeType.ACCEPT);
  } catch (TransientException e) {
      consumer.acknowledge(r, AcknowledgeType.RELEASE);   // başkası denesin
  } catch (PermanentException e) {
      dlqProducer.send(toDlq(r));                          // ÖNCE DLQ
      consumer.acknowledge(r, AcknowledgeType.REJECT);     // sonra arşivle
  }
  ```
- **Üçüncü seçenek:** Kafka 4.2 ile gelen **`RENEW`** — uzun süren bir işlemede kilidi uzatır.
  "İşliyorum, henüz bitmedi, kimseye verme" demenin yoludur.

> 📌 **Sık yapılan hata:** Her hatada RELEASE etmek. Kalıcı bir hata 5 kez dolaşır, 5 kez kaynak
> yakar ve sonunda **sessizce** arşivlenir. Hatanın geçici mi kalıcı mı olduğunu ayırt etmek
> uygulamanın işidir.

🔗 Konu: [3.4 §2–3, §5](3.4-share-groups.md)

---

### Soru 3 — Bir kayıt 5 kez teslim edilip hiç ACCEPT edilmezse?

**Kısa cevap:** **Arşivlenir** (`Archived`) — bir daha hiç teslim edilmez. Sınırı
`group.share.delivery.count.limit` belirler, varsayılan **5**. Bu, share group'un zehirli kayda
karşı **yerleşik güvenlik ağıdır**.

**Ayrıntı:**

- Klasik consumer group'ta böyle bir ağ **yoktur**: zehirli bir kayıt tüketiciyi sonsuza kadar
  kilitler (2.4'teki `position=1` örneği). Share group bu problemi protokol seviyesinde çözer.
- Teslim sayacı yalnızca RELEASE ile artmaz; **kilit süresi dolması** da bir teslim sayılır.
  `group.share.record.lock.duration.ms` (varsayılan **30 s**) içinde onaylanmayan kayıt otomatik
  olarak yeniden teslim edilebilir hâle gelir ve sayaç artar. Yani çöken bir worker'ın elindeki
  kayıtlar 30 saniye sonra başkasına gider.
- **Kritik operasyonel sonuç:** Arşivleme **sessizdir**. Kayıt kaybolmaz (log'da durur) ama
  hiçbir tüketiciye gitmez. Bunu izlemezsen "işlenmemiş ama kimsenin fark etmediği" bir kayıt
  havuzu oluşur.
- **Ne yapmalı:** Uygulama tarafında teslim sayısını takip et
  (`ConsumerRecord.deliveryCount()`) ve limite yaklaşan kayıtları **kendin DLQ'ya yaz**:
  ```java
  if (r.deliveryCount().orElse(0) >= 4) {          // limitten bir önce
      dlqProducer.send(toDlq(r));
      consumer.acknowledge(r, AcknowledgeType.REJECT);
  }
  ```

> 📌 **Sık yapılan hata:** `delivery.count.limit`'e güvenip DLQ kurmamak. Limit seni sonsuz
> döngüden korur, **veri kaybından korumaz**.

🔗 Konu: [3.4 §2–3](3.4-share-groups.md)

---

### Soru 4 — Lab'da s3, 60 kayıtlık topic'te neden 80 kez RELEASE etti?

**Kısa cevap:** Çünkü **aynı kayıt birden çok kez teslim edildi**. s3 aldığı her kaydı RELEASE
ediyordu; RELEASE edilen kayıt `Available` durumuna döner ve yeniden dağıtılır — **s3'ün kendisi
dahil** herhangi bir tüketiciye. Aynı kaydın s3'e ikinci, üçüncü kez düşmesiyle RELEASE sayısı
kayıt sayısını aştı.

**Lab çıktısı:**
```
===== SONUÇ =====
  s3     0 kayıt aldı
  s1    27 kayıt aldı
  s2    17 kayıt aldı
  s3'ün RELEASE ettiği kayıt sayısı: 80
  toplam teslim: 44 · benzersiz kayıt: 44 / 60
```

**Ayrıntı — dört gözlem:**

1. **`80 > 60`:** RELEASE sayısı toplam kayıt sayısından fazla. Tek açıklaması tekrar teslimdir.
2. **`delivery.count.limit=5` olmasaydı bu sonsuza kadar sürerdi.** Limit sayesinde her kayıt en
   fazla 5 tur dolaşıp arşivlenir.
3. **"s3 0 kayıt aldı" satırı**, "işlediği kayıt sayısı"nı gösteriyor — s3 hiçbir kaydı ACCEPT
   etmedi, hepsini RELEASE etti. Aldığı kayıt sayısı 0 değil, **80**'di.
4. **Dağılım eşit değil (27 / 17).** Share group **iş dağıtır**, adaleti garanti etmez; hızlı
   tüketici daha çok alır. Bir kuyruk için bu **doğru** davranıştır.

**Bonus gözlem — `44/60`:** Kesildiğinde 16 kayıt hâlâ uçuştaydı (acquired ama onaylanmamış).
Share group'ta `LAG` sıfır görünse bile "tamamlandı" demek değildir; ilerleme =
**onaylanmış kayıt sayısı**dır.

> 📌 **Sık yapılan hata:** RELEASE'i "bir sonraki tüketiciye gönder" sanmak. RELEASE kaydı
> havuza geri koyar; kime gideceğinin garantisi yoktur — **sana geri gelebilir**.

🔗 Konu: [3.4 §4](3.4-share-groups.md)

---

### Soru 5 — Share group'a geçmeden önce sorulacak üç soru

**Kısa cevap:**

1. **"Sıra gerçekten önemsiz mi?"** Share group'ta **hiçbir sıra garantisi yoktur** — aynı key'in
   iki olayı farklı worker'lara, farklı zamanlarda gidebilir. "Aynı müşterinin olayları sıralı
   işlensin" gereksinimin varsa share group **yanlış araçtır**.
2. **"Transaction / exactly-once zincirim var mı?"** Kafka transaction'ları consumer group üzerine
   kuruludur (`sendOffsetsToTransaction`, 4.1). Share group bu zincire **girmez**. Kafka Streams
   de share group kullanmaz. EOS gerekiyorsa consumer group'ta kal.
3. **"Kalıcı hatalar için DLQ'm var mı?"** `delivery.count.limit` (5) sonrası kayıt **sessizce
   arşivlenir**. DLQ kurmadan geçersen, işlenemeyen kayıtlar hiçbir iz bırakmadan kaybolur.

**Ek üç soru (pratikte de sorulur):**

4. **"Sürümüm yeterli mi?"** KIP-932 Kafka **4.1'de önizleme**, **4.2'de üretime hazır**dır.
   Bu sürüm eşleşmesi klasik bir sınav sorusudur.
5. **"Başlangıç noktası davranışını biliyor muyum?"** Share group varsayılan olarak **log
   sonundan** başlar. Lab'da bu tam olarak yaşandı: tüketiciler başlatılmadan önce yazılan 60
   kaydı **hiç görmediler**. Klasik gruptaki `auto.offset.reset=earliest` refleksin burada aynı
   şekilde çalışmaz.
6. **"Throughput profilim uygun mu?"** Share group kayıt başına **durum tutar**
   (`__share_group_state`). Çok yüksek throughput + çok kısa işleme senaryosunda bu ek maliyet
   kazancı yer; orada klasik consumer group daha verimlidir.

> 📌 **Sık yapılan hata:** Share group'u "daha modern olduğu için" tercih etmek. Yeni değil,
> **farklı**dır. Karar kuralı tek cümle: *sıra önemliyse consumer group, iş dağıtımı önemliyse
> share group.*

🔗 Konu: [3.4 §1, §4, §6](3.4-share-groups.md)

---

⬅️ [Bölüme dön](3.1-fetch-ve-poll-dongusu.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
