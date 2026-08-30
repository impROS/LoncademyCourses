# Soru & Cevap — çalışırken sorduklarım

> Bu dosya kursun bir parçası değil, **senin defterin**. Konu dosyalarını okurken ya da testleri
> çözerken takıldığın ve sorduğun her şey buraya, **sorduğun hâliyle** ve ayrıntılı cevabıyla yazılır.
>
> Neden ayrı dosya: bu sorular tek bir konuya ait değil — testte 00.1 okurken sorduğun bir soru
> 3.4'ün konusuna dayanabilir. Konu dosyalarına dağıtılsalar bulunamazlar; burada **tek yerde**
> ve **tarih sırasında** dururlar.
>
> 📖 Kısa tanım arıyorsan: [`00-baslangic/02-kavram-sozlugu.md`](00-baslangic/02-kavram-sozlugu.md) ·
> ⚙️ Bir ayarı ne zaman kullanacağını arıyorsan: [`00-baslangic/03-ayar-rehberi.md`](00-baslangic/03-ayar-rehberi.md)

---

## İçindekiler

| # | Soru | İlgili konu |
|---|---|---|
| [S1](#s1--share-group-paralellik-tavanını-nasıl-kaldırıyor) | Share group paralellik tavanını nasıl kaldırıyor? | [3.4](03-consumer/3.4-share-groups.md) |
| [S2](#s2--uncleanleaderelectionenable-tam-olarak-ne-işe-yarıyor-bu-durum-ne-zaman-oluşuyor) | `unclean.leader.election.enable` tam olarak ne işe yarıyor, bu durum ne zaman oluşuyor? | [1.2](01-broker-depolama/1.2-replikasyon-isr-hw.md) |
| [S3](#s3--lingerms-tam-olarak-ne-iş-yapıyor-neyi-nasıl-çözüyor) | `linger.ms` tam olarak ne iş yapıyor, neyi nasıl çözüyor? | [2.1](02-producer/2.1-accumulator-ve-batching.md) |
| [S4](#s4--acl-ve-kotalar-da-metadata-logunda-tutulur-ne-demek-zookeeper-ne-yapıyordu-kraft-yerini-nasıl-aldı) | "ACL ve kotalar da metadata log'unda tutulur" ne demek? ZooKeeper ne yapıyordu, KRaft yerini nasıl aldı? | [1.3](01-broker-depolama/1.3-kraft-metadata.md) |
| [S5](#s5--kip-848de-opt-in-ne-demek-bu-ayar-tam-olarak-ne-yapıyor) | KIP-848'de "opt-in" ne demek, bu ayar tam olarak ne yapıyor? | [3.2](03-consumer/3.2-grup-protokolu-ve-rebalance.md) |
| [S6](#s6--isr-nedir) | ISR nedir? | [1.2](01-broker-depolama/1.2-replikasyon-isr-hw.md) |
| [S7](#s7--rf-ne-demek) | RF ne demek? (ve neden artık sette RF yok) | [1.2](01-broker-depolama/1.2-replikasyon-isr-hw.md) |
| [S8](#s8--iki-poll-arası-derken-bu-polllar-ne-oluyor) | "İki poll arası" derken bu poll'lar ne oluyor? | [3.1](03-consumer/3.1-fetch-ve-poll-dongusu.md) |

---

## S1 — Share group paralellik tavanını nasıl kaldırıyor?

> **Soru (2026-08-30, 00.1 testi · "Paralellik tavanı" sorusu):**
> *"Burada share group bu tavanı nasıl kaldırır?"*

### Kısa cevap

Klasik consumer group'ta **atama birimi partition**'dır. Share group'ta **atama birimi kayıttır**.
Tavan, "bir partition aynı anda tek bir tüketiciye atanır" kuralından geliyordu; share group o
kuralı ortadan kaldırdığı için tavan da kalkıyor.

### Neden klasik grupta tavan var

Klasik grupta koordinatör bir **atama tablosu** üretir:

```
partition 0 → consumer-A
partition 1 → consumer-B
partition 2 → consumer-C
...
```

Bu tablo bire-bir eşlemedir. Bir partition iki tüketiciye yazılamaz. Sebebi teknik bir kısıt değil,
**bilinçli bir garanti**: partition içi sıra. İki tüketici aynı partition'ı paralel okusa,
offset 5 offset 3'ten önce işlenebilirdi ve "aynı key'in olayları sıralıdır" sözü çökerdi.

Ayrıca ilerlemenin tek bir sayıyla (offset) ifade edilmesi de buna dayanır: *"bu partition'da
şuraya kadar geldim"* cümlesinin **tek bir sahibi** olmalıdır.

Sonuç: `aktif tüketici sayısı = min(consumer sayısı, partition sayısı)`. 6 partition + 12
consumer = **6'sı atıl**.

### Share group'ta ne değişiyor

Share group ilerlemeyi tek bir offset'le değil, **kayıt başına durumla** takip eder:

```
        poll()                    acknowledge(ACCEPT)
 Available ──────► Acquired ─────────────────────────► Acknowledged
     ▲                │
     │                │ RELEASE / kilit süresi doldu
     └────────────────┤
                      │ REJECT / teslim sayısı limiti aştı
                      ▼
                  Archived
```

Bu durum `__share_group_state` adlı iç topic'te, **share coordinator** tarafından tutulur.
Artık "partition kime ait" diye bir soru yok; soru şu: **"bu kayıt şu an kimin elinde?"**

Mekanizma şöyle işliyor:

1. Tüketici `poll()` çağırır. Coordinator, o partition'dan **bir grup kaydı** ona **kiralar**
   (`Acquired`) — kira süresi `group.share.record.lock.duration.ms`, varsayılan **30 saniye**.
2. Aynı anda başka bir tüketici de `poll()` çağırırsa, **aynı partition'dan farklı kayıtlar** ona
   kiralanır. İki tüketici aynı partition'dan paralel çalışır.
3. Tüketici işi bitirince `acknowledge(ACCEPT)` der; kayıt tamamlanır. `RELEASE` derse kayıt
   havuza geri döner ve **başkasına** (ya da yine kendisine) gidebilir.
4. Aynı anda uçuşta olabilecek kayıt sayısının sınırı `group.share.partition.max.record.locks`
   (varsayılan **2000**), üye sayısının sınırı `group.share.max.size` (varsayılan **200**).

Yani tavan artık partition sayısı değil, **kilit havuzu ve grup büyüklüğü**.

### Bedeli — bedava değil

| Kazandığın | Kaybettiğin |
|---|---|
| Partition'dan bağımsız worker sayısı | **Sıra garantisi yok** — hiçbir düzeyde |
| Başarısız kaydı başkasına devretme (`RELEASE`) | Kayıt başına durum tutma maliyeti |
| Zehirli kayda karşı otomatik koruma (`delivery.count.limit=5`) | Transaction/EOS zincirine **girmez** (4.1) |
| Kuyruk semantiği (gerçek iş dağıtımı) | Kafka Streams share group kullanmaz |

### Lab'da nasıl göründü

**Tek partition'lı** bir topic'i üç share consumer ile tükettik. Gerçek çıktı:

```
===== SONUÇ =====
  s3     0 kayıt aldı
  s1    27 kayıt aldı
  s2    17 kayıt aldı
  s3'ün RELEASE ettiği kayıt sayısı: 80
  toplam teslim: 44 · benzersiz kayıt: 44 / 60
```

**s1 ve s2 aynı tek partition'dan paralel tüketti** — klasik grupta bu imkânsızdı, biri atıl
kalırdı. Dağılımın eşit olmaması (27/17) bir hata değil: share group **iş dağıtır**, adaleti
garanti etmez; hızlı tüketici daha çok alır. Bir kuyruk için doğru davranış budur.

### Karar kuralı

> **Sıra önemliyse → consumer group. İş dağıtımı önemliyse → share group.**
> Share group, consumer group'un **yerine geçmez**; yanına gelir.

Somut örnek: 4 partition'lı bir topic'i 50 worker ile işleyecek ve sıra önemsizse (OCR, dış API
çağrısı, e-posta gönderimi) → share group. "Aynı müşterinin olayları sıralı işlensin" gereksinimi
varsa → consumer group.

🔗 [3.4 Share groups](03-consumer/3.4-share-groups.md) ·
[cevaplar 3.4 Soru 1](03-consumer/cevaplar.md#34-share-groups) ·
📖 [share group ↗](00-baslangic/02-kavram-sozlugu.md#share-group)

---

## S2 — `unclean.leader.election.enable` tam olarak ne işe yarıyor, bu durum ne zaman oluşuyor?

> **Soru (2026-08-30):** *"`unclean.leader.election.enable` bu tam olarak ne işe yarıyordu?
> Bu durum ne zaman oluşuyor?"*

### Kısa cevap

Bu ayar tek bir soruyu cevaplar: **"ISR boşaldı ve elimde yalnızca geride kalmış replikalar var.
Partition'ı açayım mı, kapalı mı bırakayım?"**

- `false` (varsayılan) → **kapalı bırak.** Tutarlılığı seç. Partition offline kalır, ne okunur ne
  yazılır, ama **commit edilmiş hiçbir kayıt kaybolmaz**.
- `true` → **aç.** Kullanılabilirliği seç. Geride kalmış bir replika lider olur; onda olmayan
  kayıtlar **kalıcı olarak silinir**.

Yani bu bir performans ya da kurtarma ayarı değil, **veri kaybını kabul etme anahtarıdır**.

### Bu durum ne zaman oluşur

Ön koşul: **ISR'in tamamen boşalması** — yani commit edilmiş veriye sahip olduğu bilinen hiçbir
replikanın hayatta olmaması. Tipik yollar:

| Senaryo | Nasıl gelişir |
|---|---|
| **Kademeli çöküş** | replication.factor=3, min.insync.replicas=2. Bir broker ölür (ISR=2, yazma sürer). İkincisi ölür (ISR=1, yazma durur ama okuma sürer). Üçüncüsü de ölür → ISR boş |
| **replication.factor=1 topic** | Tek replika var; o broker ölünce ISR zaten boş. Lab'da tam olarak bu üretildi |
| **Tüm rack/AZ kaybı** | `broker.rack` ayarlanmamışsa üç replika da aynı AZ'ye düşmüş olabilir; AZ giderse hepsi gider |
| **Disk kaybı** | Broker'lar ayakta ama replikaların diski bozuk/silinmiş |
| **Temiz olmayan kapanış sonrası** | Broker `kill -9` ya da güç kesintisiyle kapandıysa, ELR muhasebesi onu "verisi güvenilmez" sayar ve aday listesinden **çıkarır** (KIP-966) |

Lab çıktısı — replication.factor=1 topic'te o replikanın broker'ı durdurulunca:

```
Topic: lab-rf1  Partition: 0  Leader: 2     Replicas: 2  Isr: 2  Elr:    LastKnownElr:
Topic: lab-rf1  Partition: 1  Leader: none  Replicas: 3  Isr:    Elr: 3  LastKnownElr: 3
Topic: lab-rf1  Partition: 2  Leader: 1     Replicas: 1  Isr: 1  Elr:    LastKnownElr:
OfflinePartitionsCount = 1
```

`Leader: none` + boş `Isr:` = tam olarak bu ayarın devreye girdiği an.

### `true` yaparsan ne olur — adım adım

1. Controller, ISR dışı bir replikayı lider yapar. O replika diyelim offset **800**'e kadar
   veriye sahip; eski lider **1000**'e kadar yazmıştı.
2. **800–1000 arası kayıtlar yok olur.** Producer bunlar için çoktan `OK` almıştı.
3. Yeni yazılan kayıtlar **800'den itibaren** numaralanır. Artık aynı offset numarası **farklı
   kayıtlara** karşılık gelir — cluster'ın tarihi çatallanmıştır.
4. Offset'i 950'de olan bir consumer `OffsetOutOfRangeException` alır ve
   `auto.offset.reset`'e düşer:
   - `latest` → 800'den (aslında yeni sondan) devam eder, **sessizce atlar**
   - `earliest` → baştan okur, **duplicate seli**
   - `none` → durur ve haber verir
5. Aynı kayıtları okumuş aşağı akış sistemleriyle mutabakat **kalıcı olarak** bozulur.

### Ne yapmalı — doğru sıra

`Leader: none` gördüğünde:

1. **Kapsamı belirle:** `kafka-topics.sh --describe --unavailable-partitions`
2. **Eksik broker'ı geri getir.** Lider dönünce partition kendiliğinden açılır. Aynı anda
   metadata quorum'unu (karar için gereken salt çoğunluk) da kontrol et — controller sağlıksızsa lider seçimi zaten yapılamaz (1.3).
3. **`Elr` / `LastKnownElr` kolonlarına bak.** Kafka 4.1'den beri ELR varsayılan açıktır ve
   controller "ISR'den düşmüş **ama verisi hâlâ güvenilir**" replikaları ayrı bir listede tutar.
   ELR doluysa **veri kaybetmeden** lider seçilebilecek bir aday var demektir — unclean seçime
   gerek kalmaz.
4. Broker gerçekten kurtarılamıyorsa ve **kesintinin maliyeti veri kaybından büyükse**, unclean
   seçim son çaredir. Sonrasında etkilenen grupların offset'lerini
   `kafka-consumer-groups.sh --reset-offsets` ile bilinçli bir noktaya taşımak **operasyonun
   parçasıdır**.

### Sık yapılan hata

> 📌 Bu ayarı "partition'ı açan zararsız bir çözüm" sanmak. Partition açılır, sistem "düzelmiş"
> görünür, hiçbir alarm çalmaz — ve veri kaybı **aylar sonra** mutabakatta ortaya çıkar.
> Bu yüzden varsayılanı `false`'tur ve öyle kalmalıdır.

> **Hafıza kancası:** `unclean` kelimesi "kirli" değil, **"temiz olmayan seçim"** demek —
> yani kuralına uymayan, veri bütünlüğünü garanti etmeyen bir lider seçimi.

🔗 [1.2 §4 Lider seçimi](01-broker-depolama/1.2-replikasyon-isr-hw.md) ·
[5.3 Arıza senaryoları](05-operasyon/5.3-ariza-senaryolari.md) ·
[cevaplar 1.2 Soru 4](01-broker-depolama/cevaplar.md#12-replikasyon-isr-hw-ve-elr) ·
📖 [unclean.leader.election.enable ↗](00-baslangic/02-kavram-sozlugu.md#uncleanleaderelectionenable)

---

## S3 — `linger.ms` tam olarak ne iş yapıyor, neyi nasıl çözüyor?

> **Soru (2026-08-30):** *"`linger.ms` tam olarak ne iş yapıyor anlatır mısın? Neyi nasıl
> çözüyor, ne zaman nasıl kullanmalıyız?"*

### Kısa cevap

`linger.ms`, producer'ın bir batch'i göndermeden önce **"biraz daha kayıt gelir mi?" diye
beklediği süredir.** Varsayılan **5 ms** (Kafka 4.0 öncesi 0 idi, KIP-1030 ile değişti).

Çözdüğü problem: **kayıt başına sabit maliyet.** Her ağ isteği bir başlık, bir round-trip, broker
tarafında bir parse ve bir kuyruk girişi demektir. Tek kayıtlık isteklerle çalışırsan bu maliyeti
**her kayıt için** ödersin.

### `send()` çağrısından sonra gerçekte ne oluyor

```
uygulama thread'i                          Sender thread (arka plan)
──────────────────                         ──────────────────────────
send(record)
  ├─ serialize et
  ├─ partitioner → partition seç
  └─ RecordAccumulator'a koy ──────┐
     (partition başına batch)       │      ┌── hazır batch'leri topla
     hemen dön (Future)             └─────►│   (DOLU veya LINGER DOLDU)
                                           ├── broker'a göre grupla
                                           └── tek ProduceRequest gönder
```

Bir batch **iki koşuldan biri** olunca gönderilir:

1. Batch `batch.size`'a ulaştı (varsayılan **16 KiB**, **partition başına**)
2. `linger.ms` doldu

Ayrıca `flush()` ya da `close()` bekleyen her şeyi zorla gönderir.

> ⭐ **Kritik nüans:** `linger.ms` bir **üst sınırdır**, sabit bir gecikme değil. Trafik yoğunsa
> batch zaten `batch.size`'da dolup gider ve `linger.ms` **hiç beklemez** — gecikmeye etkisi ~0
> olur. Etki en çok **düşük ve düzensiz trafikte** görülür.

### Neyi kazandırıyor — ölçümle

**Lab 1** (200.000 kayıt × 200 bayt, 6 partition, `acks=all`):

| kurulum | kayıt/sn | kayıt/istek |
|---|---|---|
| `linger=0, batch=16K` | 156.617 | 140,3 |
| `linger=5, batch=16K` *(4.x varsayılanı)* | 201.410 | 140,6 |
| `linger=50, batch=64K` | 343.053 | 561,8 |
| `linger=50, batch=64K, lz4` | **961.538** | 1923,1 |

**Lab 2** (`kafka-producer-perf-test.sh`, 300.000 kayıt × 512 bayt) — asıl sürpriz burada:

| Yapılandırma | kayıt/sn | ort. gecikme | **p99** |
|---|---|---|---|
| `linger.ms=0, batch=16K` | 72.081 | 644 ms | **1201 ms** |
| `linger.ms=50, batch=64K, lz4` | **201.342** | **22,85 ms** | **91 ms** |

**Throughput 2,8 kat arttı ve p99 gecikme 13 kat düştü.** Sezgiye aykırı ama sebebi basit:
`linger.ms=0` ile producer her kaydı ayrı istekte göndermeye çalışır, broker'a giden istek sayısı
patlar ve **gecikme kuyruktan gelir**. Batch'leyince istek sayısı düşer, kuyruk erir.

Üç kanaldan birden kazanç sağlar:

| Kanal | Nasıl |
|---|---|
| **Ağ/CPU** | Daha az istek = daha az başlık, daha az parse, daha az kuyruk girişi |
| **Sıkıştırma** | Sıkıştırma **batch'in tamamına** uygulanır; büyük batch daha iyi sıkışır |
| **Disk** | Batch başlığı (~61 bayt) çok kayda bölünür. Lab'da 802 baytlık değer diskte **877 bayt** yer kapladı — tek kayıtlık batch'te bu ek yük %10 |

### Nasıl kullanmalı

> **Tek cümlelik kural: doygun sistemde batch'le, boş sistemde batch'leme.**

| Durum | Değer | Gerekçe |
|---|---|---|
| Toplu iş / analitik akış | **20–100** | Gecikme umursanmıyor, verim her şey |
| Genel amaçlı üretim | **5** (varsayılan) | KIP-1030'un varsayılanı yapmasının sebebi: neredeyse bedava kazanç |
| İstek-yanıt, kullanıcıyı bekleten akış | **0–5** | Ama önce sistemin doygun olmadığını **ölç** |
| Doygun sistem, yüksek p99 şikâyeti | **artır** | Gecikme kuyruktan geliyorsa batch'lemek p99'u **düşürür** |

**Yalnız ayarlanmaz — kardeşleri var:**

- **`batch.size`** ile birlikte düşünülür. `linger.ms`'i artırıp `batch.size`'ı 16 KiB'da
  bırakırsan batch zaten dolup gider, beklemenin kazancı sınırlı kalır.
- **`buffer.memory`** yetmelidir: `aktif_partition × batch.size × 2–3`.
- **`compression.type`** ile birleşince asıl sıçrama olur (lab'da lz4 ile toplam 6,1 kat).

**Doğru ayarladığını nasıl anlarsın — metrikler:**

| Metrik | Ne söyler |
|---|---|
| `batch-size-avg` | `batch.size`'ın çok altındaysa batch dolmuyor → `linger.ms` artırılabilir |
| `records-per-request-avg` | 1–2 civarındaysa **batching hiç çalışmıyor** |
| `record-queue-time-avg` | Kaydın tamponda beklediği süre — `linger.ms`'i buradan görürsün |
| `compression-rate-avg` | 0.9 üstündeyse sıkıştırma para etmiyor (belki batch'ler küçük) |

> 📌 **Sık yapılan hata 1:** "Düşük gecikme istiyorum, `linger.ms=0` yapayım" refleksi. Yoğun bir
> sistemde bu, gecikmeyi **artırır** (lab: 91 ms → 1201 ms).
>
> 📌 **Sık yapılan hata 2:** `producer.send(record).get()` yazmak. `get()` batching'i tamamen
> öldürür — `linger.ms` ne olursa olsun her batch tek kayıtlık olur.

🔗 [2.1 Accumulator ve batching](02-producer/2.1-accumulator-ve-batching.md) ·
[5.1 Performans tuning](05-operasyon/5.1-performans-tuning.md) ·
⚙️ [Ayar rehberi — producer verim](00-baslangic/03-ayar-rehberi.md) ·
📖 [linger.ms ↗](00-baslangic/02-kavram-sozlugu.md#lingerms)

---

## S4 — "ACL ve kotalar da metadata log'unda tutulur" ne demek? ZooKeeper ne yapıyordu, KRaft yerini nasıl aldı?

> **Soru (2026-08-30, 00.1 testi · KRaft sorusu):**
> *"ACL ve kotalar da metadata log'unda tutulur derken neyi kastediyor? Ve ZooKeeper önceden ne
> işe yarıyordu tam olarak, KRaft nasıl ZooKeeper'ın yerini aldı?"*

### Kısa cevap

Kafka'nın iki tür verisi vardır:

| | **Veri (data plane)** | **Metadata (control plane)** |
|---|---|---|
| Ne | Senin ürettiğin kayıtlar | Cluster'ın kendisi hakkındaki bilgi |
| Nerede | Normal topic'ler | Eskiden **ZooKeeper**, şimdi **`__cluster_metadata`** |
| Örnek | `siparisler` topic'indeki mesajlar | Hangi topic var, kaç partition, lider kim, kimin neye izni var, kimin kotası ne |

"ACL ve kotalar da metadata log'unda tutulur" cümlesi şunu söylüyor: **yetkilendirme kuralları ve
istemci kotaları da bu ikinci sınıfa aittir** ve Kafka 4.x'te artık `__cluster_metadata` adlı iç
topic'te, diğer metadata ile aynı yerde saklanırlar. Eskiden ZooKeeper'da duruyorlardı.

### Metadata'nın içinde tam olarak ne var

| Metadata kaydı | Örnek |
|---|---|
| Topic tanımları | `siparisler`, 6 partition, replication.factor=3 |
| Partition durumu | Partition 3'ün lideri broker 2, ISR = {2,1,3}, ELR = {} |
| Broker kaydı | Broker 2 canlı, şu adreslerde dinliyor, rack'i `az-a` |
| Topic/broker ayarları | `retention.ms=604800000`, `min.insync.replicas=2` |
| **ACL'ler** | `User:alice` → `Topic:siparisler` → `Write` → `ALLOW` |
| **Kotalar** | `User:alice` → `producer_byte_rate=1048576` |
| SCRAM ([parolayı düz metin taşımayan SASL mekanizması ↗](00-baslangic/02-kavram-sozlugu.md#scram)) kimlik bilgileri | Kullanıcı parolalarının hash'leri |
| Feature seviyeleri | `metadata.version=4.3-IV0`, `transaction.version=2` |
| Delegation token'lar | — |

Hepsi aynı log'a, **sıralı kayıtlar** olarak yazılır.

### ZooKeeper tam olarak ne yapıyordu

ZooKeeper, Kafka'ya ait olmayan, **ayrı kurulan ve ayrı işletilen** bir dağıtık koordinasyon
servisiydi. Kafka onu dört iş için kullanıyordu:

1. **Metadata deposu.** Yukarıdaki tablonun tamamı ZooKeeper'ın hiyerarşik düğümlerinde
   (`/brokers/topics/siparisler`, `/config/topics/...`, `/kafka-acl/...`) dururdu.
2. **Controller seçimi.** Broker'lar ZooKeeper'da `/controller` düğümünü oluşturmaya yarışırdı;
   kazanan controller olurdu. Ölünce düğüm silinir, yarış yeniden başlardı.
3. **Üyelik takibi.** Her broker `/brokers/ids/<id>` altında **ephemeral** (oturumla ölen) bir
   düğüm oluştururdu. Broker ölünce oturum düşer, düğüm kaybolur, controller haberdar olurdu.
4. **Dağıtık kilit ve bildirim.** Watcher mekanizmasıyla "bir şey değişti" bildirimleri.

**Problemleri:**

| Problem | Sonuç |
|---|---|
| İki ayrı dağıtık sistem işletmek | İki güvenlik modeli, iki izleme, iki yükseltme takvimi, iki uzmanlık |
| Controller failover'da metadata'yı ZooKeeper'dan **baştan okumak** | Büyük cluster'da **dakikalar** süren kesinti |
| Metadata değişiklikleri broker'lara **tek tek RPC ([uzak yordam çağrısı ↗](00-baslangic/02-kavram-sozlugu.md#rpc))** ile yayılırdı | Yavaş yayılım, tutarsızlık pencereleri |
| ZooKeeper'ın veri düğümü (znode) sayısı sınırlıydı | Partition sayısı tavanı |
| İki ayrı gerçeklik kaynağı | ZooKeeper ile Kafka'nın kendi durumu **ıraksayabilirdi** — split-brain kaynağı |

### KRaft yerini nasıl aldı

**Temel fikir:** Kafka zaten replike edilmiş, sıralı bir log tutmayı çok iyi biliyor. O hâlde
metadata'yı da **bir log olarak** tutalım.

> ⚠️ **KRaft ZooKeeper'ın yerine geçen ayrı bir servis değildir.** Kafka'nın **içindeki** bir
> Raft uygulamasıdır: ayrı process yok, ayrı port yok, ayrı bağımlılık yok.

Mimari:

```
    ┌──────────── controller quorum (Raft voter'ları) ────────────┐
    │  node 1 (lider = active controller)   node 2      node 3    │
    │        ▲ yazar                           ▲ fetch    ▲       │
    └────────┼──────────────────────────────────────────┼─────────┘
             │  __cluster_metadata-0  (TEK partition)
    ┌────────┴───────── observer'lar (broker'lar) ─────────────────┐
    │  metadata log'unu FETCH eder, delta uygular                  │
    └──────────────────────────────────────────────────────────────┘
```

| Rol | Kim | Ne yapar |
|---|---|---|
| **Voter (oy veren controller düğümü)** | `controller.quorum.voters`'daki node'lar | Raft oylamasına katılır, metadata yazımını onaylar |
| **Lider (active controller)** | Voter'lardan biri | Metadata **yazan tek** node: lider seçimi, ISR değişimi, topic oluşturma |
| **Observer (yalnızca okuyan, oy vermeyen düğüm)** | Broker'lar | Yalnızca **okur**: log'u fetch eder, kendi belleğinde durumu kurar |

Dört ZooKeeper işinin KRaft'taki karşılığı:

| ZooKeeper işi | KRaft'ta |
|---|---|
| Metadata deposu | `__cluster_metadata` topic'i (tek partition, replikaları voter'lar) |
| Controller seçimi | **Raft lider seçimi** — çoğunluk oyuyla |
| Üyelik takibi | Broker'lar controller'a **heartbeat** gönderir |
| Değişiklik yayılımı | Broker'lar log'u **takip eder** (fetch), delta uygular — RPC yok |

**Kazançlar:**

- **Failover saniyeler sürer**, dakikalar değil: yeni controller zaten log'u takip ediyordu,
  baştan okumasına gerek yok.
- **Tek gerçeklik kaynağı** — ıraksama imkânsız.
- **Tek binary, tek konfigürasyon, tek güvenlik modeli.**
- Partition ölçeği log'un kendisiyle sınırlı; ZooKeeper znode limitleri yok.

**Neden tek partition?** Metadata'nın **toplam sıraya** ihtiyacı var. "Topic silindi" ile "aynı
isimle topic oluşturuldu" olayları tüm node'larda **aynı sırada** uygulanmalı. Çok partition,
partition'lar arası sıra garantisi olmadığı için bunu bozardı.

**Log sonsuza kadar büyümüyor:** Controller periyodik olarak **snapshot** alır
(`00000000000000002306.snapshot`); yeni katılan bir node önce snapshot'ı yükler, sonra üstüne son
kayıtları uygular. Eski log kayıtları budanabilir.

### İki farklı "yeter sayı" kuralını karıştırma

Kafka'da iki ayrı çoğunluk kuralı var ve **aynı şey değiller**:

- **ISR** = *"lidere yetişebilenler"*. Kaç kişi olduğu **değişkendir**; biri geri kalırsa listeden
  düşer, yetişince geri girer. Yazmanın kabul edilmesi için gereken taban `min.insync.replicas`.
- **Raft'ın çoğunluğu** (İngilizce kaynaklarda *quorum*) = *"oy veren düğümlerin yarıdan fazlası"*.
  Sabit bir matematiktir: `(düğüm sayısı / 2) + 1`. 3 düğümde **2**, 5 düğümde **3**. Bu sayı
  sağlanmazsa metadata'ya **hiçbir şey yazılamaz** — pazarlık yok.

Bu, konunun en kritik ayrımı:

| | **ISR** (veri partition'ları) | **Raft çoğunluğu** (metadata) |
|---|---|---|
| Onay kuralı | ISR'deki herkes — ve **ISR daralabilir** | **Kesin çoğunluk** `(N/2)+1` |
| Uyarlanabilir mi | Evet, ISR küçülür ve yazma devam edebilir | **Hayır** — çoğunluk yoksa yazma durur |
| 3 node ile kayıp toleransı | `replication.factor − min.insync.replicas`'e bağlı | **1** |
| 5 node ile kayıp toleransı | — | **2** |

> **Hafıza kancası:** ISR *"kim yetişebiliyorsa o"*, Raft *"matematiksel çoğunluk"*.
> İkincisi pazarlık kabul etmez.

Lab'da bunun sonucunu doğrudan görürsün: 3 node'lu **combined** modda (broker + controller aynı
süreçte) iki node'u durdurmak yalnızca broker'ları değil **controller quorum'unu da** öldürür;
topic oluşturma gibi metadata işlemleri tamamen durur:

```
Cancelled createTopics request ... node 1 being disconnected
```

Bu bir bug değil, quorum matematiğidir. Üretimde **isolated mod** (ayrı controller node'ları)
bu yüzden önerilir.

### Pratik sonuç — senin için ne değişti

- `zookeeper.connect` ayarı **yok**. `--zookeeper` bayrağı **yok**.
- ZooKeeper tabanlı her runbook, her blog yazısı, her `zkCli.sh` komutu **geçersiz**.
- Quorum'u incelemek için ayrı bir komut ve **ayrı bir bootstrap bayrağı** var:
  ```bash
  kafka-metadata-quorum.sh --bootstrap-controller kafka-1:9093 describe --status
  ```
  Broker API'leri için `--bootstrap-server`, quorum komutları için `--bootstrap-controller`.
- Sürüm yükseltmesi **iki adımdır**: önce binary (rolling restart), sonra
  `kafka-features.sh upgrade` ile feature seviyesi.

🔗 [1.3 KRaft ve metadata](01-broker-depolama/1.3-kraft-metadata.md) ·
[5.4 Güvenlik (ACL, kota)](05-operasyon/5.4-guvenlik.md) ·
[cevaplar 1.3](01-broker-depolama/cevaplar.md#13-kraft-ve-metadata) ·
📖 [KRaft ↗](00-baslangic/02-kavram-sozlugu.md#kraft)

---

## S5 — KIP-848'de "opt-in" ne demek, bu ayar tam olarak ne yapıyor?

> **Soru (2026-08-30, 00.1 testi · Kafka 4.x farkları sorusu):**
> *"KIP-848 sunucuda varsayılan etkindir ama consumer'ın opt-in etmesi gerekir derken, opt-in
> dediği ne ve bu ayar tam olarak ne yapıyordu?"*

### Kısa cevap — "opt-in" ne demek

**Opt-in = "isteyerek dahil olmak".** Özellik hazır ve açık duruyor ama **kendiliğinden
başlamıyor**; kullanmak için sen açıkça talep etmelisin.

Karşıtı **opt-out**'tur: özellik kendiliğinden devreye girer, istemiyorsan kapatman gerekir.

KIP-848'de durum şu:

| Taraf | Durum |
|---|---|
| **Broker** | Yeni protokolü **destekliyor** — `group.coordinator.rebalance.protocols=classic,consumer,streams` |
| **Client (consumer)** | Varsayılan hâlâ **`classic`**. Yeni protokole geçmek için `group.protocol=consumer` **yazman gerekir** |

```java
props.put(ConsumerConfig.GROUP_PROTOCOL_CONFIG, "consumer");   // opt-in budur
```

Bu satırı yazmazsan uygulaman 4.x cluster'da da **eski protokolle** çalışmaya devam eder —
hiçbir hata almazsın, hiçbir uyarı görmezsin. Testteki "otomatik devreye girer" şıkkının
yanlış olmasının sebebi bu.

**Neden opt-in yapılmış:** Grup protokolü değişikliği, çalışan bir tüketici grubunun en hassas
parçasını değiştirir. Yükseltmenin bir yan etkisi olarak kendiliğinden devreye girseydi, dünyadaki
tüm Kafka tüketicileri bir binary yükseltmesiyle davranış değiştirirdi. Kafka bu riski **sana**
bırakıyor: hazır olduğunda, uygulama uygulama geç.

### Ayar tam olarak ne yapıyor — eski protokol

Klasik protokolde rebalance şöyle işler:

```
1. Consumer → koordinatör : JoinGroup
2. Koordinatör            : TÜM üyeler gelene kadar BEKLER        ← global barrier
3. Koordinatör            : üyelerden birini GRUP LİDERİ seçer
4. Grup lideri (BİR CONSUMER) : atamayı HESAPLAR                  ← mantık client'ta
5. Lider → koordinatör    : SyncGroup (atama tablosu)
6. Koordinatör → herkes   : herkesin kendi ataması
```

Üç sorunu var:

| Problem | Sonuç |
|---|---|
| Atamayı bir **consumer** hesaplar | Client sürümü/strateji uyuşmazlığı = grup **hiç kurulmaz** |
| **Global barrier**: herkes beklenir | Bir yavaş üye **tüm grubu** durdurur |
| Eager assignor'da herkes her şeyi bırakır | Rebalance = stop-the-world |

### Yeni protokol ne yapıyor

```
Consumer → koordinatör : ConsumerGroupHeartbeat (üyelik + mevcut atama)
Koordinatör            : atamayı KENDİSİ hesaplar (uniform/range)
Koordinatör → consumer : heartbeat cevabında "şunu bırak / şunu al"
```

| | **Klasik** | **KIP-848 (`group.protocol=consumer`)** |
|---|---|---|
| Atamayı kim yapar | Grup lideri (bir consumer) | **Broker (koordinatör)** |
| Barrier | Var — herkes beklenir | **Yok** — üye bazında ilerler |
| Yayılım | JoinGroup/SyncGroup turu | **Heartbeat üzerinden damla damla** |
| Yeni üye etkisi | Tüm grup etkilenir | Yalnızca **taşınan** partition'lar |
| Assignor nerede | Client'ta | **Broker'da** (`group.consumer.assignors`) |
| Client sürüm uyumu | Tüm grup aynı stratejide olmalı | Broker karar verdiği için sorun değil |
| `session.timeout.ms` | Client'ta | **Broker'da** (`group.consumer.session.timeout.ms`) |

Lab kanıtı — 6 partition, 3. consumer ekleniyor:

```
[c2] ATANDI      : [0, 1, 2, 3, 4, 5]   ← ilk anda tek üye tamamını aldı
[c2] GERİ ALINDI : [3, 4, 5]            ← c1 katılınca kademeli devir
=== 3. consumer ekleniyor ===
[c3] ATANDI      : [5]
[c1] ATANDI      : [3, 4]
[c2] GERİ ALINDI : [2]
[c3] ATANDI      : [2]
```

**Hiçbir noktada tüm grup durmuyor.** Değişiklikler heartbeat'lerle kademeli akıyor.

### Pratik sonuçlar

- **"Rebalance süresi" diye tek bir an yok** — **yakınsama (convergence)** var. Klasik protokolden
  taşınan "ortalama rebalance süresi" panosu yeni protokolde boşalır; bu "rebalance yok" demek
  değil, metriğin tanımı değişti demektir. Bunun yerine partition'ın sahipsiz kalma süresini ve
  lag sıçramasını ölç.
- **Client ayarları yok sayılır:** Yeni protokolde `session.timeout.ms` ve
  `heartbeat.interval.ms` broker tarafında belirlenir (lab cluster'ında 45000 / 5000).
  `max.poll.interval.ms` hâlâ client'tadır ve aynı işi yapar.
- **Geçiş kademelidir:** Broker `classic,consumer,streams` üçünü de destekler ve
  `group.consumer.migration.policy=bidirectional` (varsayılan) sayesinde bir grup iki protokol
  arasında geçiş yapabilir. Pratikte: client'ları **tek tek** `group.protocol=consumer`'a al.
- **Atama birkaç heartbeat sürebilir.** Kısa testlerde "atama neden hemen dengelenmedi?" diye
  şaşırma — protokol kademeli çalışıyor.

> 📌 **Sık yapılan hata:** Kafka 4.x'e yükseltip "artık rebalance sorunum yok" sanmak. Ayarı
> yazmadıysan hâlâ klasik protokoldesin.

🔗 [3.2 Grup protokolü ve rebalance](03-consumer/3.2-grup-protokolu-ve-rebalance.md) ·
[cevaplar 3.2](03-consumer/cevaplar.md#32-grup-protokolü-ve-rebalance) ·
📖 [KIP-848 ↗](00-baslangic/02-kavram-sozlugu.md#kip-848) ·
📖 [group.protocol ↗](00-baslangic/02-kavram-sozlugu.md#groupprotocol)

---

## S6 — ISR nedir?

> **Soru (2026-08-30):** *"ISR nedir?"*

### Kısa cevap

**ISR = In-Sync Replicas** — *"lidere yeterince yetişebilen replikalar kümesi"*. Lider de bu
kümeye dahildir.

Kafka'da bir kayıt "yazıldı" olduğunda değil, **"commit edildi"** olduğunda güvendedir. Commit'in
tanımını belirleyen şey **ISR'dir**, replika sayısı değil. Bu tek cümle, veri kaybı sorularının
%90'ının cevabıdır.

### Nasıl çalışıyor

Her partition'ın bir **lideri** ve N−1 **takipçisi (follower)** vardır.

- Tüm okuma/yazma **lidere** gider. (Tek istisna: rack-aware follower fetch, 5.5.)
- Follower'lar liderden **fetch eder** — evet, tam olarak bir consumer gibi. Ayrı bir replikasyon
  protokolü yoktur.
- Follower çektiği veriyi kendi log'una yazar ve bir sonraki fetch isteğinde "ben artık offset
  X'teyim" bilgisini lidere iletir.

Üç kavram birlikte anlam kazanır:

| Kavram | Anlamı |
|---|---|
| **LEO** (Log End Offset) | **Bu replikanın** yazdığı son kaydın bir sonraki offset'i |
| **HW** (High Watermark) | **ISR'deki tüm** replikaların ulaştığı en yüksek offset |
| **ISR** | Lidere yeterince yakın replikalar kümesi (lider dahil) |

```
lider    : [0][1][2][3][4]          LEO=5
follower1: [0][1][2][3]             LEO=4
follower2: [0][1][2]                LEO=3
                    ↑
                   HW=3   → consumer 0,1,2'yi görür; 3 ve 4'ü göremez
```

**Consumer yalnızca HW'ye kadar okuyabilir.** HW'nin ötesindeki kayıtlar diskte vardır ama
görünmezler — henüz commit edilmemişlerdir ve lider değişirse kaybolabilirler.

> **Hafıza kancası:** HW = **"herkesin gördüğü su seviyesi"**. Havuzdaki en yavaş replikanın
> seviyesi, herkesin okuyabildiği seviyedir.

### ISR'e girmenin ve düşmenin tek kuralı

Eski Kafka'da iki ayar vardı (mesaj sayısı + süre); bugün **tek kural** var:

> Bir follower, **`replica.lag.time.max.ms`** (varsayılan **30000 ms**) süresince liderin
> LEO'suna yetişemezse ISR'den düşürülür.

| Durum | Sonuç |
|---|---|
| Follower yavaş ama her fetch'te liderin sonuna değiyor | ISR'de **kalır** |
| 30 saniyedir liderin sonuna hiç değmedi | ISR'den **düşer** (shrink) |
| Düşen follower tekrar yetişti | ISR'e **geri alınır** (expand) |
| Broker tamamen öldü | ISR'den düşer, lider seçimi tetiklenir |

Dikkat: "fetch ediyor" yetmez, **"yetişiyor"** gerekir. Broker ayakta ve fetch ediyor olmasına
rağmen ISR'den düşen bir follower'ın tipik sebepleri: yavaş disk, ağ doygunluğu / yetersiz
`num.replica.fetchers` (varsayılan **1**), GC duraklamaları.

ISR daralması **normaldir**; sorun **kalıcı** olmasıdır. `UnderReplicatedPartitions` metriğinden
izlenir ve alarm **süreye** bağlanır: *"> 0, 5 dakikadır"*.

### ISR neden bu kadar önemli — dayanıklılık zinciri

`acks=all`, "tüm replikalar" demek **değildir**; **"ISR'deki tüm replikalar"** demektir.
ISR daraldıkça `all`'ın anlamı da daralır. ISR = {lider} ise `acks=all` ≡ `acks=1`.

İşte bu yüzden `min.insync.replicas` var: ISR'in daralabileceği bir **taban** koyar.

| replication.factor | min.insync.replicas | Yazmaya devam ederek tolere edilen kayıp | Yorum |
|---|---|---|---|
| 3 | 1 | 2 | ⚠️ `acks=all` olsa bile tek kopyaya yazar — **sahte güvenlik** |
| 3 | **2** | **1** | ✅ Üretim standardı |
| 3 | 3 | 0 | Tek broker bakıma alınınca yazma durur — genelde fazla katı |
| 2 | 2 | 0 | Her bakım yazmayı durdurur. replication.factor=3 kullan |

> ⚠️ **Broker varsayılanı `min.insync.replicas=1`'dir.** Kimse değiştirmezse `acks=all` yazan
> uygulaman gerçekte `acks=1` güvenliğindedir. Bu setin lab cluster'ında bilerek **2** yapıldı.

> **İki formül, ezberlenecek:**
> ```
> Yazmaya devam ederek tolere edilen kayıp = replication.factor − min.insync.replicas
> Veri kaybı olmadan tolere edilen kayıp   = replication.factor − 1   (unclean seçim kapalıyken)
> ```

### ISR'i canlı görmek

```bash
kafka-topics.sh --bootstrap-server kafka-1:19092 --describe --topic lab-isr
```
```
Topic: lab-hello  Partition: 0  Leader: 3  Replicas: 3,1,2  Isr: 3,1,2  Elr:   LastKnownElr:
```

| Kolon | Anlamı |
|---|---|
| `Replicas` | Tanımlı tüm kopyalar (replication.factor) |
| `Isr` | Şu an **senkron** olanlar |
| `Elr` | ISR'den düşmüş ama lider olmaya **uygun** olanlar (KIP-966, 4.1+) |
| `LastKnownElr` | ELR de boşaldığında son bilinen aday(lar) |

Lab'da bir broker durdurulunca canlı olarak görülür:
```
02:23:58  leader=3 isr=[3, 1, 2]        OK   offset=5
02:24:00  leader=1 isr=[1, 2]           OK   offset=6      ← lider değişti, ISR daraldı, yazma sürüyor
02:24:23  leader=1 isr=[1]  FAIL TimeoutException ...       ← ikinci broker da ölünce yazma durdu
```

> ⚠️ **Dikkat — hata mesajı yanıltıcıdır.** Broker gerçekte `NotEnoughReplicasException` döner ama
> bu hata **yeniden denenebilir** olduğu için client retry'lara girer ve `delivery.timeout.ms`
> dolunca yüzeye **`TimeoutException`** olarak çıkar. Gerçek sebep **broker log'undadır**.

### Karıştırılmaması gerekenler

| Karışan | Doğrusu |
|---|---|
| `replication.factor` ↔ `min.insync.replicas` | replication.factor = kaç kopya **var**; min.insync.replicas = yazma için kaç kopya **güncel olmalı** |
| LEO ↔ HW | LEO tek replikanın sonu; HW **ISR'in ortak asgarisi** |
| ISR ↔ Raft quorum | ISR uyarlanabilir ve daralabilir; Raft **kesin çoğunluk** ister (bkz. [S4](#s4--acl-ve-kotalar-da-metadata-logunda-tutulur-ne-demek-zookeeper-ne-yapıyordu-kraft-yerini-nasıl-aldı)) |
| ISR shrink ↔ arıza | Shrink normal olabilir; **kalıcı** shrink arızadır |
| ELR ↔ ek replika | ELR yeni kopya **oluşturmaz**; sadece "kim güvenle lider olabilir" muhasebesidir |

🔗 [1.2 Replikasyon: ISR, HW ve ELR](01-broker-depolama/1.2-replikasyon-isr-hw.md) ·
[cevaplar 1.2](01-broker-depolama/cevaplar.md#12-replikasyon-isr-hw-ve-elr) ·
📖 [ISR ↗](00-baslangic/02-kavram-sozlugu.md#isr) ·
📖 [HW ↗](00-baslangic/02-kavram-sozlugu.md#hw) ·
📖 [LEO ↗](00-baslangic/02-kavram-sozlugu.md#leo)

---

## S7 — RF ne demek?

> **Soru (2026-08-30, S2'deki "RF=3, min.insync.replicas=2" tablosu üzerine):**
> *"RF ne demek düzgünce açıklasana şunları."*
>
> **Devamı (aynı gün):** *"RF yazacağına Replication Factor yaz, boş yere kısaltmayla kafamı iyice
> karıştırma. Konuyu öğrenmeye çalışıyorum, sürekli dikkatimi dağıtarak başka yerlere bakmak
> zorunda bırakma beni."*

### Önce: haklısın, kısaltmayı kaldırdım

**"RF" benim uydurduğum kısayoldu, Kafka'nın terimi değil.** Kafka dokümantasyonunda,
`--describe` çıktısında, hiçbir ayar adında "RF" geçmez — orada her zaman `replication.factor`
yazar. Ben bu sette 110'dan fazla yerde "RF" yazmışım ve hiçbir yerde açmamışım.

İlk düzeltmem yanlıştı: sözlüğe girdi açıp bağlantı koymuştum. Ama senin dediğin gibi, **bağlantı
da dikkat dağıtıyor** — cümlenin ortasında durup başka dosyaya bakmak zorunda kalıyorsun.
Doğru çözüm kısaltmayı **hiç kullanmamak**:

| Eskiden yazdığım | Artık yazıyorum |
|---|---|
| `RF=3` | `replication.factor=3` |
| `min.ISR` / `minISR` | `min.insync.replicas` |
| `ZK` | `ZooKeeper` |
| `RF − min.ISR` | `replication.factor − min.insync.replicas` |

**224 yerde** açıldı; sette artık "RF" diye bir şey yok. Bundan sonra da kısaltma kullanmayacağım —
tek istisna, senin `--describe` çıktısında ya da ayar adında **gerçekten göreceğin** terimler
(ISR, ACL, TLS gibi). Onları kaçınmak seni gerçek dünyaya hazırlıksız bırakır, ama onlar da ilk
geçtikleri cümlede açıklanır.

### Kısa cevap

**replication.factor = `replication.factor` = bir partition'ın kaç kopyasının tutulacağı.**

`replication.factor=3` demek: her partition **3 ayrı broker'da** birebir aynı şekilde duruyor.

```
siparisler topic'i, partition 0, replication.factor=3

broker-1   [0][1][2][3][4]   ← LİDER    (tüm okuma/yazma buraya)
broker-2   [0][1][2][3][4]   ← takipçi  (liderden fetch eder)
broker-3   [0][1][2][3]      ← takipçi  (biraz geride)
```

Bir broker ölünce partition kaybolmaz; takipçilerden biri lider olur ve hayat devam eder.
**Kafka'nın dayanıklılığı tamamen buradan gelir** — diskten değil, `fsync`'ten değil,
**replikasyondan**.

### replication.factor ile min.insync.replicas farkı — asıl karışan yer

Bu ikisi **farklı iki soruyu** cevaplar ve karıştırılmaları bu konunun 1 numaralı hatasıdır:

| | **replication.factor** (`replication.factor`) | **min.insync.replicas** (`min.insync.replicas`) |
|---|---|---|
| Sorusu | Kaç kopya **var**? | Yazma için kaç kopya **güncel olmalı**? |
| Nerede ayarlanır | Topic oluşturulurken | Topic ya da broker ayarı |
| Değişir mi | Sabit (elle değiştirilir) | Sabit — ama **ISR** anlık olarak dalgalanır |
| Varsayılan | Broker `default.replication.factor` | **1** ⚠️ |
| Neyi belirler | Kaç kopya kaybını **hayatta kalarak** atlatırsın | Ne zaman yazmanın **reddedileceğini** |

Somut örnek — `replication.factor=3, min.insync.replicas=2`:

| Durum | ISR | Yazma (`acks=all`) | Okuma |
|---|---|---|---|
| Her şey normal | {1,2,3} | ✅ | ✅ |
| 1 broker öldü | {1,2} | ✅ `2 >= 2` | ✅ |
| 2 broker öldü | {1} | ❌ `NotEnoughReplicasException` | ✅ |
| 3 broker öldü | {} | ❌ | ❌ partition offline |

Dikkat: ikinci broker ölünce **veri kaybolmadı** — yazma durdu. Yazmanın durması Kafka'nın seni
**koruduğu** andır; sessiz kayıp, `min.insync.replicas=1` bırakıldığında olur.

### Ezberlenecek iki formül

```
replication.factor − min.insync.replicas  =  yazmaya devam ederek tolere edilen broker kaybı
replication.factor − 1        =  veri kaybı olmadan tolere edilen kayıp   (unclean seçim kapalıyken)
```

Kontrol edelim: `replication.factor=3, min.insync.replicas=2` → `3−2 = 1` broker kaybında yazma sürer, `3−1 = 2` broker
kaybında veri hâlâ güvende. Yukarıdaki tabloyla birebir uyuyor.

`replication.factor=5, min.insync.replicas=3` → 2 broker kaybına kadar yazma sürer, 4 broker kaybına kadar veri kaybı olmaz.

### Hangi değeri seçmeli

| replication.factor | Ne zaman | Not |
|---|---|---|
| **1** | ❌ Üretimde asla | Tek broker kaybı = partition offline + **kalıcı veri kaybı**. Ayrıca broker varsayılanı `min.insync.replicas=2`'yi miras alırsa topic **hiç yazılamaz** (5.3'te bu tuzağa düştük) |
| **2** | ❌ Genelde yanlış | min.insync.replicas=2 ile her bakım yazmayı durdurur; min.insync.replicas=1 ile de tek kopyaya yazar. İki dünyanın kötüsü |
| **3** | ✅ **Üretim standardı** | min.insync.replicas=2 ile: 1 broker kaybını kesintisiz, 2 broker kaybını veri kaybetmeden atlatır |
| **5** | Kritik/finansal veri | Daha çok tolerans, ama 5 kat disk ve 5 kat replikasyon trafiği |

**Bedeli unutma:** replication.factor=3, diskini **üçe katlar** ve broker'lar arası replikasyon trafiği üretir.
Kapasite hesabında replication.factor bir **çarpandır**:

```
disk = günlük_ham × sıkıştırma_oranı × replication.factor × retention_gün × (1 + emniyet payı)
```

### replication.factor'yi görmek ve değiştirmek

```bash
# Mevcut replication.factor'yi gör — "Replicas:" kolonundaki broker sayısı
kafka-topics.sh --bootstrap-server kafka-1:19092 --describe --topic siparisler
```
```
Topic: siparisler  Partition: 0  Leader: 3  Replicas: 3,1,2  Isr: 3,1,2  Elr:  LastKnownElr:
                                            ^^^^^^^^^^^^^^^^ replication.factor = 3
```

> ⚠️ **replication.factor sonradan `--alter` ile değiştirilemez.** Artırmak için
> `kafka-reassign-partitions.sh` ile yeni bir replika atama planı uygulanır — ve bu, mevcut
> verinin tamamının kopyalanması demektir. **`--throttle` şart**, yoksa canlı trafiği ezer (5.5).

### Karıştırılmaması gerekenler

| Karışan | Doğrusu |
|---|---|
| replication.factor ↔ min.insync.replicas | replication.factor = kaç kopya **var**; min.insync.replicas = kaç kopya **güncel olmalı** |
| replication.factor ↔ ISR | replication.factor **sabit bir sayı** (yapılandırma); ISR **anlık bir küme** (kim yetişiyor) |
| replication.factor ↔ partition sayısı | replication.factor = aynı verinin kaç kopyası; partition = veri kaç parçaya bölündü. Biri **dayanıklılık**, diğeri **paralellik** |
| `acks=all` ↔ replication.factor | `acks=all` "replication.factor kadar replika" değil, **"ISR'deki tüm replikalar"** demektir |
| replication.factor artırmak ↔ ölçekleme | replication.factor artırmak **hız kazandırmaz**; okuma da lidere gider. Dayanıklılık ayarıdır |

> **Hafıza kancası:** **Partition = veriyi böler (hız). replication.factor = veriyi çoğaltır (güvenlik).**
> İkisi dik eksenlerdir; biri diğerinin yerine geçmez.

🔗 [1.2 §3 Dayanıklılık zinciri](01-broker-depolama/1.2-replikasyon-isr-hw.md) ·
[5.5 Kapasite ve partition tasarımı](05-operasyon/5.5-kapasite-ve-partition-tasarimi.md) ·
[S6 — ISR nedir](#s6--isr-nedir) ·
📖 [replication.factor ↗](00-baslangic/02-kavram-sozlugu.md#replicationfactor) · 📖 [min.insync.replicas ↗](00-baslangic/02-kavram-sozlugu.md#mininsyncreplicas)


---

## S8 — "İki poll arası" derken bu poll'lar ne oluyor?

> **Soru (2026-08-30, 00.1 testi · rebalance sorusunun açıklaması üzerine):**
> *"Rebalance döngüsünün klasik sebebi, iki poll arasındaki işlem süresinin
> max.poll.interval.ms'i aşmasıdır: Burada iki poll arasında derken bu poll'lar ne oluyor?"*

### Kısa cevap

`poll()`, tüketici uygulamanın **senin yazdığın döngüde** çağırdığı tek metottur:
`consumer.poll(...)`. "İki poll arası" = **senin kodunun iki `poll()` çağrısı arasında geçirdiği
süre** — yani kayıtları işlemek için harcadığın zaman.

Kafka bunu ölçüyor, çünkü `poll()` çağırmayan bir tüketici **ilerlemiyor** demektir; elindeki
partition'ları boşuna tutuyordur.

### Somut olarak nerede

Tipik bir tüketici döngüsü şöyle görünür:

```java
while (calisiyor) {
    ConsumerRecords<String, String> kayitlar = consumer.poll(Duration.ofMillis(500));   // ⬅ 1. poll

    for (ConsumerRecord<String, String> k : kayitlar) {
        isle(k);                        // ⬅ ARADAKİ SÜRE BU
    }                                   //    500 kayıt × 400 ms = 200 saniye
    consumer.commitSync();
}                                       // ⬅ döngü başa döner → 2. poll
```

**İki `poll()` çağrısı arasında Kafka'nın hiçbir kontrolü yok.** O süre tamamen senin `isle()`
metodunun içinde geçiyor: veritabanına yazıyorsun, bir servise istek atıyorsun, dosya
oluşturuyorsun. Kafka bu sırada uygulamanın ne yaptığını bilemez — yalnızca "bir daha
`poll()` çağırmadı" bilgisine sahiptir.

`max.poll.interval.ms` (varsayılan **300.000 ms = 5 dakika**) tam olarak şunu söyler:

> *"İki `poll()` çağrın arasında en fazla 5 dakika geçebilir. Geçerse seni ölü sayarım."*

### `poll()` yalnızca kayıt getirmez — asıl mesele bu

`poll()` adı yanıltıcı; içeride beş iş yapar:

```
poll(timeout)
 ├─ 1) koordinasyon : gruba katıl / rebalance'a cevap ver / atamayı güncelle
 ├─ 2) heartbeat    : "yaşıyorum" sinyali (klasik protokolde arka plan thread'i tetikler)
 ├─ 3) otomatik commit : enable.auto.commit=true ise ve zamanı geldiyse offset'i yaz
 ├─ 4) fetch        : tamponda yeterli veri yoksa broker'a istek gönder
 └─ 5) kayıtları döndür : tampondan max.poll.records kadarını deserialize edip sana ver
```

İşte bu yüzden `poll()` çağırmayı bırakmak sadece "veri gelmemesi" değil, **grup üyeliğinin
kopması** demektir. Uygulaman hayatta, CPU'su normal, log'u temiz — ama Kafka için ölü.

### Karıştırma: `poll(Duration)` ≠ `max.poll.interval.ms`

Bu ikisi çok karıştırılır ve **tamamen farklı** şeylerdir:

| | `poll(Duration.ofMillis(500))` | `max.poll.interval.ms` |
|---|---|---|
| Kim belirler | Sen, her çağrıda | Yapılandırma |
| Anlamı | *"Veri yoksa en fazla 500 ms **bekle**, sonra boş dön"* | *"İki çağrın arasında en fazla 5 dakika olsun"* |
| Aşılırsa | Hiçbir şey — `poll()` boş liste döner | **Gruptan atılırsın** |

`poll()`'un argümanı bir **sabır süresidir**; `max.poll.interval.ms` bir **son tarihtir**.

### Aşarsan ne olur — adım adım

Diyelim `max.poll.records=500`, kayıt başına işleme 400 ms:

```
500 × 400 ms = 200.000 ms = 200 saniye  <  300.000 ms   ✅ güvenli
```

Ama aşağı akış sistemi yavaşladı ve kayıt başına 1 saniyeye çıktı:

```
500 × 1000 ms = 500.000 ms = 500 saniye  >  300.000 ms  ❌
```

Sırasıyla şunlar olur:

1. **5. dakikada** koordinatör "bu üye ilerlemiyor" der ve gruptan atar.
2. Partition'lar başka bir tüketiciye verilir → **rebalance**.
3. **8. dakikada** senin tüketicin işi bitirir, `commitSync()` çağırır →
   **`CommitFailedException`**: "artık o partition'lar senin değil".
4. İşlediğin 500 kayıt **commit edilmedi**; yeni sahip onları **baştan işler**.
5. Yeni sahip de aynı 500 kaydı 500 saniyede işlemeye çalışır → **o da atılır**.
6. Döngü başa döner. **Grup hiç ilerlemez**, lag sürekli büyür, aynı kayıtlar defalarca işlenir.

Belirtisi tanıdıktır: *"Consumer log'unda sürekli `Request joining group` yazıyor, lag artıyor,
ama hata yok."*

### Diğer zamanlayıcıyla karıştırma

| Ayar | Varsayılan | Sorduğu soru | Aşılırsa |
|---|---|---|---|
| `session.timeout.ms` | 45.000 ms | **"Süreç yaşıyor mu?"** | Üye atılır (process çöktü / ağ koptu) |
| `max.poll.interval.ms` | 300.000 ms | **"Süreç ilerliyor mu?"** | Üye atılır (işleme çok uzun) |

Klasik protokolde heartbeat'i **ayrı bir arka plan thread'i** gönderir. Bu yüzden işlemede
takılan bir tüketici heartbeat atmaya devam eder ve `session.timeout.ms`'e **yakalanmaz** —
onu yakalayan `max.poll.interval.ms`'tir.

### Ne yapmalı

**Doğru sıra:**

1. **`max.poll.records`'ı düşür.** İlk ve en güvenli hamle. Formül:
   ```
   max.poll.records × kayıt başına işleme süresi  <  max.poll.interval.ms
   ```
   Kayıt başına 400 ms ise teorik tavan 750; ama **en kötü** süreyi hesaba kat ve pay bırak —
   pratikte **150-200** seç.
2. **İşleme süresini kısalt.** Veritabanına tek tek değil toplu yaz, gereksiz senkron çağrıları
   kaldır, paralelleştir.
3. **Partition sayısını artırıp yatay ölçekle** (sıra riskine dikkat, 5.5).
4. **En son `max.poll.interval.ms`'i büyüt.** Çünkü büyütmek, **gerçekten çöken** bir tüketicinin
   fark edilmesini de geciktirir. 30 dakikaya çıkarırsan, ölen bir pod'un partition'ları 30
   dakika boyunca kimseye devredilmez.

**Uzun süren tek bir kayıt için özel çözüm:** Kaydı al, bir kuyruğa koy, o partition'ı
`pause()` ile duraklat — **ama `poll()` çağırmaya devam et.** Duraklatılmış partition için
`poll()` boş döner; koordinasyon ve heartbeat çalışmaya devam eder. İş bitince `resume()`.

> ⚠️ **`pause()` ile "poll etmeyi bırakmak" aynı şey değildir.** `poll()` çağırmayı bırakırsan
> yine atılırsın. Aynı sebeple `Thread.sleep()` ile yavaşlatmak da yanlıştır.

### Önceden görmenin yolu

**`last-poll-seconds-ago`** — tüketicinin kendi metriği; son `poll()` çağrısından bu yana kaç
saniye geçtiğini söyler. Eşik: `max.poll.interval.ms`'in **%70'i** (varsayılanla **210 saniye**).

Bu metrik fırtına **başlamadan önce** uyarır; `rebalance-rate-per-hour` ise ancak başladıktan
sonra yükselir. En az bilinen ama en faydalı tüketici metriğidir — uygulamandan mutlaka dışarı ver.

🔗 [3.1 Fetch ve poll döngüsü](03-consumer/3.1-fetch-ve-poll-dongusu.md) ·
[3.2 Grup protokolü ve rebalance](03-consumer/3.2-grup-protokolu-ve-rebalance.md) ·
[cevaplar 3.1 Soru 2](03-consumer/cevaplar.md#31-fetch-ve-poll-döngüsü) ·
[5.2 Metrikler](05-operasyon/5.2-metrikler-ve-izleme.md)


---

> **Yeni soru sorduğunda** bu dosyanın sonuna eklenir ve içindekiler tablosu güncellenir.
> Sorunu kısaltmadan, **sorduğun hâliyle** yazıyorum — altı ay sonra "ne sormuştum" diye
> baktığında bağlam kaybolmasın.
