# 00.1 — Genel bakış ve ileri seviye trickler

> **Alan:** Başlangıç — oyunun kuralları
> **Süre:** ~25 dakika okuma
> **Test:** [`00-test.html`](00-test.html) · 14 soru

---

## Neden bu konu

Kafka'yı "biliyorum" diyenlerin çoğu `send()` ve `poll()` biliyor. Üretimde sorun çıktığında lazım olan
bilgi orada değil: **veri neden kayboldu**, **tüketici neden duruyor**, **latency neden p99'da patlıyor**,
**neden aynı mesaj iki kez işlendi**. Bunların hepsinin cevabı Kafka'nın *tasarım tercihlerinde* yatıyor.

Bu dosya, kalan 26 konunun üstüne oturduğu zihinsel modeli kurar. Buradaki 6 ilkeyi kavradıysan
sonraki konular "ezberlenecek ayar listesi" olmaktan çıkıp "tahmin edilebilir sonuçlar" haline gelir.

> **Büyük fikir:** Kafka bir kuyruk değil, **replike edilmiş, sadece sona eklenen (append-only) bir dosyadır.**
> Anlamadığın her davranışın cevabı, "bu bir dosya olsaydı ne olurdu?" sorusundadır.

---

## 1. ⭐ Kafka bir mesaj kuyruğu değil, bir log'dur

Klasik kuyrukta (RabbitMQ, ActiveMQ, SQS) mesaj **tüketilince silinir**. Kafka'da tüketmek okumaktır;
mesaj yerinde durur, **retention** bitince silinir. Bu tek fark, davranışların yarısını açıklar.

| | **Klasik kuyruk** | **Kafka log'u** |
|---|---|---|
| Tüketim | Mesajı kuyruktan çıkarır (destructive) | Sadece okur; offset ilerletir |
| Aynı mesajı 2 uygulama okusun | Fanout/exchange kurmak gerekir | 2 ayrı consumer group yeter, bedava |
| Geri sarma | Genelde imkânsız | `seek()` ile saniyeler içinde |
| Sıralama | Kuyruk başına | **Partition başına** |
| Silme | Consumer siler | Broker, retention politikasıyla siler |
| Paralellik sınırı | Consumer sayısı | **Partition sayısı** |

> ⚠️ **Tuzak:** "Kuyruk dolmuş, tüketicileri artıralım" refleksi Kafka'da işe yaramaz.
> Partition sayısı kadar aktif tüketicin olabilir; 4 partition'lık topic'e 10 consumer koyarsan
> **6'sı boş oturur.** (3.2'de göreceğiz.)

**Kafka 4.2 ile bir istisna geldi:** *share groups* (KIP-932 — [Kafka için kuyruklar / share group ↗](02-kavram-sozlugu.md#kip-932)) partition sayısından bağımsız,
mesaj başına onaylama yapan gerçek bir kuyruk modeli sunuyor. (3.4'te işleyeceğiz.)

---

## 2. ⭐ Sıralama garantisi partition'ın içindedir, topic'in değil

Kafka **topic seviyesinde sıra garanti etmez.** Garanti şudur: *aynı partition'a yazılan kayıtlar,
yazıldıkları sırada okunur.* Aynı key → aynı partition olduğu için pratikte kural şu olur:

> **Aynı key'in olayları sıralıdır. Farklı key'lerin olayları arasında hiçbir sıra garantisi yoktur.**

Bu yüzden ileri seviyede ilk tasarım sorusu her zaman aynıdır: **"Bu veri hangi anahtarla bölünmeli?"**
Yanlış key seçimi geri dönülemez borçtur — partition sayısını sonradan artırdığında
mevcut key→partition eşlemesi bozulur ve sıra garantisi kırılır. (5.5'te detayı var.)

---

## 3. ⭐ Her garanti bir maliyettir: üçgeni tanı

İleri seviye Kafka, ayar isimleri ezberlemek değil, **bu üçgende nerede durduğunu bilerek karar vermektir.**

```
              Dayanıklılık (durability)
                      /\
                     /  \
                    /    \
      Gecikme ---- /______\ ---- Verim (throughput)
```

| İstediğin | Bedeli | Kilit ayarlar |
|---|---|---|
| Veri asla kaybolmasın | Yazma gecikmesi artar, kullanılabilirlik düşer | `acks=all`, `min.insync.replicas=2`, `unclean.leader.election.enable=false` |
| Düşük gecikme | Batch küçülür, CPU/ağ verimi düşer | `linger.ms=0`, `batch.size` ([partition başına batch bayt tavanı ↗](02-kavram-sozlugu.md#batchsize)) küçük, `fetch.min.bytes=1` |
| Yüksek verim | p99 gecikme artar | `linger.ms` ([batch dolsun diye beklenen süre ↗](02-kavram-sozlugu.md#lingerms)) ↑, `batch.size` ↑, `compression.type=lz4/zstd` |
| Exactly-once | ~%3-20 verim kaybı + operasyonel karmaşıklık | `transactional.id` ([producer’ın kalıcı kimliği ↗](02-kavram-sozlugu.md#transactionalid)), `isolation.level=read_committed` |

> **Hafıza kancası:** Kafka'da bedava öğle yemeği yok; sadece **kimin ödediğini seçersin** —
> producer mı bekler, consumer mı bekler, yoksa disk mi şişer.

---

## 4. Kafka 4.x'te dünya değişti — eski bilgini güncelle

Elindeki Kafka bilgisi 3.x veya daha eskiyse, aşağıdakiler artık yanlış:

| Eskiden | Kafka 4.x'te |
|---|---|
| ZooKeeper cluster gerekir | **Yok.** KRaft ([Kafka’nın kendi Raft metadata katmanı ↗](02-kavram-sozlugu.md#kraft)) tek gerçeklik; metadata Kafka'nın kendi log'unda (1.3) |
| Rebalance = stop-the-world, tüm grup durur | **KIP-848 ([broker tarafı yeni consumer grup protokolü ↗](02-kavram-sozlugu.md#kip-848))**: broker'ın yönettiği artımlı rebalance; `group.protocol=consumer` ile açılır (3.2) |
| Kafka kuyruk olarak kullanılamaz | **Share groups** 4.2'de üretime hazır (3.4) |
| `linger.ms` varsayılanı 0 | **5** — KIP-1030 ([Kafka 4.0 varsayılan değer değişiklikleri ↗](02-kavram-sozlugu.md#kip-1030)) varsayılanları değiştirdi (2.1) |
| Java 8 ile client yazılır | Client/Streams **Java 11+**, broker/Connect **Java 17+** |
| Transaction'lar zombi producer'a açık | **TV2 (KIP-890 — [transaction protokolü TV2 ↗](02-kavram-sozlugu.md#kip-890))** varsayılan; her transaction'da epoch artıyor (4.1) |
| ISR ([lidere senkron replikalar kümesi ↗](02-kavram-sozlugu.md#isr))'den düşen replica lider olabilir (veri kaybı) | **ELR ([ISR dışı ama lider olmaya uygun replikalar ↗](02-kavram-sozlugu.md#elr))** (KIP-966 — [ELR — güvenli lider adayları ↗](02-kavram-sozlugu.md#kip-966)) 4.1'den beri varsayılan açık (1.2) |
| Eski client'lar hep bağlanır | **KIP-896 ([eski protokol sürümlerinin kaldırılması ↗](02-kavram-sozlugu.md#kip-896))**: 2.1'den eski protokol sürümleri kaldırıldı |

> ⚠️ **Tuzak:** İnternetteki Kafka içeriğinin büyük kısmı 2.x/3.x dönemine ait. Bir blog yazısında
> ZooKeeper görüyorsan, o yazıdaki ayar tavsiyeleri de muhtemelen eskimiştir. **Sürümü kontrol et.**

---

## 5. İleri seviyede insanlar tam olarak nerede kaybediyor

Bu setin varlık sebebi bu liste. Her satır, bu sette bir konuya karşılık geliyor.

| Kayıp noktası | Belirti | Nerede çözülüyor |
|---|---|---|
| `acks=1` ile "yeterince güvenli" sanmak | Lider çöktüğünde sessiz veri kaybı | [2.3](../02-producer/2.3-teslimat-garantileri.md) |
| `min.insync.replicas=1` bırakmak | `acks=all` yazsan bile tek kopya yeter, kayıp | [1.2](../01-broker-depolama/1.2-replikasyon-isr-hw.md) |
| Consumer'da uzun işlem + kısa `max.poll.interval.ms` ([iki poll arası izin verilen süre ↗](02-kavram-sozlugu.md#maxpollintervalms)) | Sonsuz rebalance döngüsü, grup hiç ilerlemez | [3.2](../03-consumer/3.2-grup-protokolu-ve-rebalance.md) |
| İşlemeden önce offset commit | Çöküşte **veri kaybı** (at-most-once) | [3.3](../03-consumer/3.3-offset-ve-commit.md) |
| Partition sayısını sonradan artırmak | Key→partition eşlemesi bozulur, sıra kırılır | [5.5](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md) |
| Consumer lag'i tek sayı sanmak | Ortalama iyi görünür, tek partition boğulur | [5.2](../05-operasyon/5.2-metrikler-ve-izleme.md) |
| Compaction'ı "sıkıştırma" sanmak | Silinmesini beklediğin veri durur, beklemediğin gider | [1.4](../01-broker-depolama/1.4-retention-ve-compaction.md) |
| Streams'te state store'u stateless sanmak | Instance taşınınca dakikalarca restore, "uygulama açılmıyor" | [6.2](../06-streams/6.2-state-store-ve-changelog.md) |
| EOS ([exactly-once semantics — Kafka içi atomiklik ↗](02-kavram-sozlugu.md#eos))'u "her yerde exactly-once" sanmak | Kafka→dış sistem çağrısı hâlâ at-least-once | [4.1](../04-eos-transaction/4.1-transactions-internals.md) |
| `retries` sıfırlamak / sonsuz yapmak | Ya sessiz kayıp ya sonsuz takılma | [2.2](../02-producer/2.2-idempotence-ve-siralama.md) |

---

## 6. Refleksler — soru sorulduğunda ilk bakacağın yer

Mülakatta da, üretim arızasında da işini gören kısayollar:

| Soru/şikâyet | İlk bakacağın 3 şey |
|---|---|
| "Mesaj kayboldu" | `acks` ([kaç kopyanın onayı beklenir ↗](02-kavram-sozlugu.md#acks)) · `min.insync.replicas` ([acks=all için gereken asgari güncel kopya ↗](02-kavram-sozlugu.md#mininsyncreplicas)) vs `replication.factor` · `unclean.leader.election.enable` ([ISR dışı replika lider olabilsin mi ↗](02-kavram-sozlugu.md#uncleanleaderelectionenable)) |
| "Aynı mesaj iki kez işlendi" | Commit sırası (işlem öncesi/sonrası) · rebalance logu · `enable.idempotence` |
| "Tüketici geride kaldı" | Partition başına lag dağılımı · `max.poll.records` ([tek poll’un döndüreceği kayıt sayısı ↗](02-kavram-sozlugu.md#maxpollrecords)) × işlem süresi · fetch ayarları |
| "Rebalance durmuyor" | `max.poll.interval.ms` · `session.timeout.ms` ([heartbeat gelmezse ölü sayma süresi ↗](02-kavram-sozlugu.md#sessiontimeoutms)) · pod restart/OOM ([bellek tükenmesi ↗](02-kavram-sozlugu.md#oom)) · `group.instance.id` ([üyeye kalıcı kimlik — static membership ↗](02-kavram-sozlugu.md#groupinstanceid)) |
| "Producer yavaş" | `batch.size`/`linger.ms` · `compression.type` · `max.in.flight` · broker `request` metrikleri |
| "Disk doldu" | `retention.ms/bytes` · `cleanup.policy` ([delete mi compact mi ↗](02-kavram-sozlugu.md#cleanuppolicy)) · compaction geri kalmış mı · `log.dirs` dengesi |
| "Bir broker CPU'yu yiyor" | Partition/leader dengesizliği · sıcak key · TLS + compression yeniden sıkıştırma |

> **Hafıza kancası — 3 katman kuralı:** Bir Kafka sorunu her zaman üç yerden birindedir:
> **producer tarafı** (yazma/batch/retry), **broker tarafı** (replikasyon/disk/lider),
> **consumer tarafı** (fetch/commit/rebalance). Teşhise başlarken önce **katmanı** seç, sonra ayarı.

---

## Sık karıştırılanlar — tek tabloda

| Karışan | Doğrusu | Neden diğeri değil |
|---|---|---|
| `replication.factor` ↔ `min.insync.replicas` | replication.factor = kaç kopya tutulur; min.insync.replicas = `acks=all` yazması için kaç kopyanın **güncel** olması gerekir | replication.factor=3 olsa da min.insync.replicas=1 ise tek kopyaya yazıp "başarılı" dönebilir |
| "Kafka mesajı siler" ↔ "consumer siler" | **Broker** retention'a göre siler | Consumer offset ilerletir, veriyi silmez |
| Topic sırası ↔ partition sırası | Sıra yalnızca **partition** içinde | Topic seviyesinde sıra hiçbir zaman garanti edilmedi |
| `acks=all` ↔ exactly-once | `acks=all` = kayıpsız; EOS = tekrarsız | İkisi farklı sorun; `acks=all` duplicate'i engellemez |
| Consumer group ↔ share group ([kayıt başına dağıtım — Kafka’nın kuyruk modu ↗](02-kavram-sozlugu.md#share-group)) | Group: partition başına 1 tüketici; Share group: mesaj başına onay | Share group 4.2'den itibaren üretime hazır; klasik gruba alternatif, yerine geçmez |
| Compaction ↔ compression | Compaction = key başına son kaydı tut; compression = baytları sıkıştır | İkisi ayrı ayar (`cleanup.policy` ↔ `compression.type`) |
| Lag ↔ gecikme (latency) | Lag = kaç kayıt geride; latency = bir kaydın uçtan uca süresi | Lag 0 olup latency yüksek olabilir (ör. büyük `linger.ms`) |

---

## 🖥 Pratik — cluster'ı tanı, ilk gözlemi al

> **Amaç:** Ortamın çalıştığını doğrulamak ve bu setin komut kalıbına alışmak · **Süre:** 10 dk
> **💸 Maliyet:** Yok — her şey lokal.

Önce ortamı kurmadıysan [`01-ortam-kurulumu.md`](01-ortam-kurulumu.md)'e git, sonra buraya dön.

### Adımlar

1. Cluster'ın metadata quorum'unu (karar için gereken salt çoğunluk) gör:
   ```bash
   docker exec kafka-1 /opt/kafka/bin/kafka-metadata-quorum.sh --bootstrap-controller kafka-1:9093 describe --status
   ```
2. Çıktıdaki `LeaderId` değerine bak — bu **controller lideri**. `CurrentVoters` üç node göstermeli.
3. Bir topic oluştur ve dağılımına bak:
   ```bash
   docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-1:19092 \
     --create --topic lab-hello --partitions 3 --replication-factor 3
   docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-1:19092 \
     --describe --topic lab-hello
   ```
4. Çıktıyı oku. Şu satırı göreceksin:
   ```
   Topic: lab-hello  Partition: 0  Leader: 3  Replicas: 3,1,2  Isr: 3,1,2  Elr:   LastKnownElr:
   ```
   `Leader` her partition için farklı — Kafka liderliği broker'lara **yayar**, tek broker'a yığmaz.
   `Elr` kolonu Kafka 4.x'in yeni Eligible Leader Replicas bilgisidir (1.2'de işleyeceğiz).

- [ ] **Kontrol:** 3 partition'ın lideri en az iki farklı broker'a dağılmış olmalı.
- [ ] **Kaydet:** Controller lideri hangi node id? ______  ·  Partition 0'ın lideri? ______

### 💸 Temizlik
5. Bu topic'i sonraki konularda kullanmayacağız:
   ```bash
   docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-1:19092 --delete --topic lab-hello
   ```
- [ ] **Kontrol:** `--list` çıktısında `lab-hello` görünmemeli.

---

## Nasıl karşına çıkar — örnek kalıplar

**Kalıp 1 — Ölçeklendirme tuzağı**
> *"Topic'imizde 6 partition var, 12 consumer'lı bir grup çalıştırıyoruz ama throughput artmadı. Neden?"*
> → **6 consumer atıl.** Paralellik tavanı partition sayısıdır; önce partition artırılmalı (sıra etkisine dikkat).

**Kalıp 2 — Sessiz veri kaybı**
> *"`acks=all` kullanıyoruz ama bir broker çöktüğünde kayıt kaybettik."*
> → **`min.insync.replicas=1`** kalmış. `acks=all`, "ISR'deki herkes" demektir; ISR tek kişiyse garanti tektir.

**Kalıp 3 — Sürüm farkı**
> *"Rebalance sırasında tüm grup duruyor, bunu nasıl engelleriz?"*
> → 4.x'te **`group.protocol=consumer`** (KIP-848) ile artımlı, broker yönetimli rebalance'a geç.

**Kalıp 4 — Yanlış araç**
> *"Her mesajı ayrı ayrı onaylamamız ve başarısızları yeniden dağıtmamız gerekiyor."*
> → Klasik consumer group bunu yapmaz; **share group** (4.2+) tam bu senaryo için var.

**Kalıp 5 — Model hatası**
> *"Mesaj tüketildikten sonra Kafka'dan siliniyor mu?"*
> → Hayır. Silme **retention**'a bağlıdır; tüketim sadece offset ilerletir. İki grup aynı veriyi okuyabilir.

---

## 60 saniyelik özet

- Kafka **append-only, replike bir log**tur; kuyruk değildir. Tüketmek silmez, retention siler.
- **Sıra garantisi partition içindedir.** Aynı key → aynı partition → sıralı.
- **Paralellik tavanı = partition sayısı** (klasik consumer group'ta).
- Her garantinin bedeli var: dayanıklılık ↔ gecikme ↔ verim üçgeninde karar veriyorsun.
- Kafka 4.x: **ZooKeeper yok**, KRaft var; **KIP-848** rebalance; **share groups**; **ELR**; **TV2 ([Transaction Version 2 — her tx’te epoch artışı ↗](02-kavram-sozlugu.md#tv2))**;
  `linger.ms` varsayılanı **5**; client Java 11+, broker Java 17+.
- Veri kaybı sorularının cevabı neredeyse hep `acks` + `min.insync.replicas` + `unclean.leader.election` üçlüsünde.
- Duplicate sorularının cevabı neredeyse hep **commit sırası** ve **idempotence ([retry kaynaklı duplicate’i önleme ↗](02-kavram-sozlugu.md#idempotence-idempotent-producer))**'ta.
- Teşhise başlarken **katmanı seç**: producer / broker / consumer.

---

## Kendini kontrol (teste girmeden, kâğıda yaz)

1. 8 partition'lı bir topic'e 3 consumer'lı bir grup bağlanıyor. Kaç partition kaç consumer'a düşer,
   4. consumer eklenirse ne olur?
2. `acks=all` + `replication.factor=3` + `min.insync.replicas=1` yapılandırmasında iki broker çökerse
   producer ne davranır, veri kaybı riski nedir?
3. Bir mesajın Kafka'da ne zaman silineceğini belirleyen şey nedir? Consumer'ın onu okumuş olması etkiler mi?
4. "Aynı müşterinin olayları sıralı işlensin" gereksinimini Kafka'da nasıl karşılarsın? Riskin ne?
5. Kafka 3.x bilgisiyle 4.x cluster'a gelen birinin yapacağı ilk üç yanlış varsayım nedir?

➡️ **Cevaplar:** [`cevaplar.md`](cevaplar.md) — önce kâğıda kendi cevabını yaz, sonra aç.

---

## ✅ Test
➡️ **[00-test.html](00-test.html)** — 14 soru
**%80 altındaysan** testin sonundaki zayıf alt konulara dön, oku, tekrar çöz.

---

## 📊 Test geçmişim

> Bu tablo test bittiğinde **otomatik** doldurulur (`assets/skor-sunucu.js` çalışıyorsa).
> En yeni deneme en üstte. Elle düzenlersen bir sonraki yazımda korunur; yalnızca yeni satır eklenir.

<!-- skor:baslangic -->
| Tarih | Skor | Yüzde | Süre | Zayıf alanlar |
|---|---|---|---|---|
| 2026-08-30 14:17 | 14/14 | **%100** ✅ | 2 dk 37 sn | — |
<!-- skor:bitis -->

## Sırada ne var
➡️ [`01-ortam-kurulumu.md`](01-ortam-kurulumu.md) — 3 broker'lı cluster ve Maven lab projesi
➡️ [`02-kavram-sozlugu.md`](02-kavram-sozlugu.md) — kısaltmalar, KIP'ler ve ayarların sözlüğü
➡️ [`03-ayar-rehberi.md`](03-ayar-rehberi.md) — hangi ayar, ne zaman, değeri seçerken nasıl düşünülür
