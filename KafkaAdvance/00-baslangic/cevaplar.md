# 00 · Başlangıç — Kendini kontrol cevapları

> Bu dosya [`00-genel-bakis-ve-trickler.md`](00-genel-bakis-ve-trickler.md) sonundaki
> **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca "biliyordum" hissi gelir; bu his
> öğrenme değildir. Kâğıda yazdığın cevapla buradakini **karşılaştırmak** öğrenmedir.
> Eksik kaldığın her madde, ilgili konuya geri dönmen gereken yeri gösterir.

---

## 00.1 Genel bakış ve ileri seviye trickler

📄 Sorular: [`00-genel-bakis-ve-trickler.md`](00-genel-bakis-ve-trickler.md)

### Soru 1 — 8 partition, 3 consumer; 4. consumer eklenirse?

**Kısa cevap:** 3-3-2 (ya da 3-3-2'nin bir permütasyonu) dağılır. 4. consumer eklenince
rebalance olur ve dağılım 2-2-2-2'ye döner. Tavan 8'dir: 9. consumer'dan itibaren fazlalar
**boş oturur**.

**Ayrıntı:**

- Klasik consumer group'ta bir partition **aynı anda tek bir üyeye** atanır. Bu, sıra
  garantisinin (partition içi sıra) korunması için zorunludur — iki tüketici aynı partition'ı
  paralel okusa sıra anlamını kaybederdi.
- Varsayılan atayıcı Kafka 4.x'te `RangeAssignor` (klasik protokol) ya da
  `uniform` (KIP-848 protokolü). 8/3 bölünmesi tam olmadığı için kalan partition'lar
  ilk üyelere dağıtılır — bu yüzden **3-3-2**, eşit değil.
- 4. üye katıldığında dağılım 2-2-2-2 olur; klasik protokolde bu bir **stop-the-world**
  rebalance'tır, KIP-848 protokolünde artımlıdır (3.2).

**Sayı, ezberlenecek tek kural:** `aktif tüketici sayısı = min(consumer sayısı, partition sayısı)`.

> 📌 **Sık yapılan hata:** "Yavaşladık, consumer ekleyelim" refleksi. Partition sayısı tavanına
> ulaştıysan consumer eklemek **hiçbir şey** kazandırmaz; sadece boş JVM'ler ve daha uzun
> rebalance'lar üretir. Önce partition sayısına, sonra kayıt başına işleme süresine bak.

🔗 Derinleşmek için: [3.2 Grup protokolü ve rebalance](../03-consumer/3.2-grup-protokolu-ve-rebalance.md) ·
[5.5 Kapasite ve partition tasarımı](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md)

---

### Soru 2 — `acks=all` + replication.factor=3 + `min.insync.replicas=1`, iki broker çökerse?

**Kısa cevap:** Producer **yazmaya devam eder ve `OK` alır** — çünkü ISR tek replikaya düşse bile
`1 >= min.insync.replicas` şartı sağlanır. Veri kaybı riski **yüksektir**: kayıtlar tek kopyada
duruyor; o broker da ölürse (ya da diski bozulursa) veri **geri dönülemez şekilde** kaybolur.

**Ayrıntı:**

- `acks=all` "tüm replikalar" demek değildir; **"ISR'deki tüm replikalar"** demektir. ISR
  daraldıkça `all`'ın anlamı da daralır. ISR = {lider} ise `acks=all` ≡ `acks=1`.
- `min.insync.replicas` tam olarak bu daralmaya bir **taban** koymak için vardır. Broker
  varsayılanı **1**'dir — yani kimse değiştirmezse bu tehlikeli yapılandırma **varsayılan**dır.
- Doğrusu: `min.insync.replicas=2` (replication.factor=3 ile). O zaman ikinci broker kaybında broker yazmayı
  `NotEnoughReplicasException` ile **reddeder** — yani sessiz kayıp yerine **gürültülü hata**
  alırsın. Üretimde istediğin budur.
- Formül: `replication.factor − min.insync.replicas` = yazmaya devam ederek tolere edilen broker kaybı. Burada `3 − 1 = 2`.

> 📌 **Sık yapılan hata:** "Sessiz kayıp" burada anahtar kelime. Producer hata almadığı için
> uygulama log'unda hiçbir iz yoktur; kayıp aylar sonra mutabakatta fark edilir.

🔗 Derinleşmek için: [1.2 Replikasyon: ISR, HW ve ELR](../01-broker-depolama/1.2-replikasyon-isr-hw.md) ·
[2.3 Teslimat garantileri](../02-producer/2.3-teslimat-garantileri.md)

---

### Soru 3 — Bir mesaj ne zaman silinir? Consumer'ın okuması etkiler mi?

**Kısa cevap:** Silmeye **broker** karar verir, ölçüt **retention politikası**dır
(`cleanup.policy` + `retention.ms`/`retention.bytes`, ya da compaction'da "bu key'in daha yenisi
var mı"). Consumer'ın okumuş olmasının **hiçbir etkisi yoktur**.

**Ayrıntı:**

- Kafka bir kuyruk değil, **append-only bir log**tur. Tüketmek = okumak + kendi offset'ini
  ilerletmek. Veriye dokunmaz.
- Bu yüzden aynı topic'i N ayrı consumer group bedelsiz okuyabilir ve bir grup `seek()` ile
  geri sarabilir. Klasik kuyrukta bunların ikisi de mümkün değildir.
- Silme koşulları: `retention.ms`/`retention.bytes` dolması **ve** verinin **aktif olmayan**
  bir segmentte olması. Aktif segment hiçbir politikayla silinmez (1.1).
- `cleanup.policy=compact` ise yaş ölçüt değildir: bir kayıt ancak **aynı key'in daha yeni bir
  sürümü** varsa silinir. Bir key'in son değeri asla kaybolmaz.

> 📌 **Sık yapılan hata:** Az trafikli bir topic'te "retention 1 saat ama veri duruyor" görülür ve
> retention bozuk sanılır. Sebep neredeyse her zaman **segmentin hiç dönmemiş** olmasıdır.

🔗 Derinleşmek için: [1.4 Retention ve compaction](../01-broker-depolama/1.4-retention-ve-compaction.md)

---

### Soru 4 — "Aynı müşterinin olayları sıralı işlensin" nasıl karşılanır? Riski ne?

**Kısa cevap:** **Key = müşteri kimliği** verirsin. Aynı key aynı partition'a düşer, partition
içinde sıra garantilidir ve o partition tek bir tüketiciye atanır — yani müşteri bazında sıra
uçtan uca korunur.

**Ayrıntı — üç riski var:**

1. **Partition sayısını sonradan artırmak eşlemeyi bozar.** Varsayılan partitioner
   `hash(key) % partition_sayısı` kullanır; payda değişince aynı müşteri yeni bir partition'a
   düşer ve **eski partition'da işlenmemiş olayları varken** yeni partition'dan olay işlenmeye
   başlanır. Sıra kırılır ve bu **geri alınamaz**. (5.5)
2. **Sıcak key (hot partition).** Trafiğin %40'ı tek bir müşteriden geliyorsa o partition
   boğulur; diğerleri boşta kalır. Ölçek partition sayısıyla değil, **key dağılımıyla** sınırlanır.
3. **Producer tarafında sıra bozulması.** `enable.idempotence=false` + `retries>0` +
   `max.in.flight.requests.per.connection>1` kombinasyonu, başarısız bir batch'in yeniden
   denenmesi sırasında **sırayı bozabilir**. Kafka 4.x'te idempotence varsayılan açıktır ve
   broker sıralamayı kendisi düzeltir (2.2) — ama bunu bilerek kapatan kod hâlâ vardır.

> 📌 **Sık yapılan hata:** Sıra garantisini yalnızca Kafka'da aramak. Tüketici tarafında kayıtları
> bir thread pool'a dağıtıyorsan sırayı **sen** kırmış olursun; key bazlı sıralı işlemek için
> tüketici içinde de key→worker eşlemesi gerekir.

🔗 Derinleşmek için: [5.5 Kapasite ve partition tasarımı](../05-operasyon/5.5-kapasite-ve-partition-tasarimi.md) ·
[2.2 Idempotence ve sıralama](../02-producer/2.2-idempotence-ve-siralama.md)

---

### Soru 5 — Kafka 3.x bilgisiyle 4.x'e gelen birinin ilk üç yanlış varsayımı

**Kısa cevap (en sık üçü):**

1. **"ZooKeeper vardır / gerekir."** Kafka 4.x **KRaft-only**'dir. `zookeeper.connect` yoktur,
   `--zookeeper` bayrağı yoktur, ZooKeeper tabanlı her runbook geçersizdir. Metadata artık Kafka'nın
   kendi log'unda (`__cluster_metadata`) durur.
2. **"Rebalance = tüm grup durur (stop-the-world)."** 4.x'te `group.protocol=consumer` ile
   KIP-848 protokolü kullanılabilir: atamayı **broker** hesaplar, rebalance artımlıdır ve
   `session.timeout.ms` gibi client ayarları yerini broker tarafı grup ayarlarına bırakır.
3. **"`linger.ms` varsayılanı 0'dır."** KIP-1030 ile **5**'e çıktı. Yani hiçbir şey
   değiştirmeden 3.x'ten 4.x'e geçen bir uygulamanın p99 gecikmesi birkaç ms artabilir,
   buna karşılık batch'ler büyür ve verim artar.

**Onur listesi — diğer sık yanlışlar:**

| Eski varsayım | 4.x gerçeği |
|---|---|
| "Kafka kuyruk olarak kullanılamaz" | **Share groups** (KIP-932) 4.2'de üretime hazır (3.4) |
| "ISR'den düşmüş replika lider olabilir" | **ELR** (KIP-966) 4.1'den beri varsayılan açık (1.2) |
| "Java 8 ile client yazarım" | Client/Streams **Java 11+**, broker/Connect **Java 17+** |
| "Eski client'larım nasılsa bağlanır" | **KIP-896**: 2.1 öncesi protokol sürümleri kaldırıldı |
| "Transaction'lar zombi producer'a açık" | **TV2** (KIP-890) varsayılan; her tx'te epoch artar (4.1) |

> 📌 **Sık yapılan hata:** İnternetteki Kafka içeriğinin büyük kısmı 2.x/3.x dönemine aittir.
> Bir yazıda ZooKeeper geçiyorsa, o yazıdaki **ayar tavsiyeleri de** muhtemelen eskimiştir.

🔗 Derinleşmek için: [1.3 KRaft ve metadata](../01-broker-depolama/1.3-kraft-metadata.md) ·
[3.2 Grup protokolü ve rebalance](../03-consumer/3.2-grup-protokolu-ve-rebalance.md) ·
[3.4 Share groups](../03-consumer/3.4-share-groups.md)

---

⬅️ [Konuya dön](00-genel-bakis-ve-trickler.md) · 📖 [Kavram sözlüğü](02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](03-ayar-rehberi.md)
