# 04 · EOS ve transaction — Kendini kontrol cevapları

> Bu dosya [4.1](4.1-transactions-internals.md) ve [4.2](4.2-outbox-ve-idempotent-tuketici.md)
> konularının sonundaki **"Kendini kontrol"** sorularının ayrıntılı cevaplarını içerir.

> ⚠️ **Önce kendin yaz, sonra buraya bak.** Cevabı okuyunca gelen "biliyordum" hissi öğrenme
> değildir; kâğıdaki cevabınla buradakini **karşılaştırmak** öğrenmedir.

**İçindekiler:** [4.1](#41-transactions-internals) · [4.2](#42-outbox-ve-idempotent-tüketici)

---

## 4.1 Transactions internals

📄 Sorular: [`4.1-transactions-internals.md`](4.1-transactions-internals.md)

### Soru 1 — Abort edilen kayıtlar diskte ne olur? İki `isolation.level` ne görür?

**Kısa cevap:** Abort edilen kayıtlar **diskte aynen durur** — silinmezler. Fark, okuyucuda:
- **`read_uncommitted`** (varsayılan) onları **görür**.
- **`read_committed`** onları **atlar** — broker `.txnindex` dosyasındaki abort aralıklarına
  bakarak filtreler.

**Ayrıntı — lab kanıtı.** 3 kayıt commit, 3 kayıt abort, 1 kayıt daha commit edildi:
```
===== isolation.level = read_uncommitted =====
  offset=0   COMMIT-0
  offset=1   COMMIT-1
  offset=2   COMMIT-2
  offset=4   ABORT-0        ⬅ abort edilmiş kayıtlar GÖRÜNÜYOR
  offset=5   ABORT-1
  offset=6   ABORT-2
  offset=8   COMMIT-3
  toplam 7 kayıt · log sonu offset=10

===== isolation.level = read_committed =====
  offset=0   COMMIT-0 ... offset=8   COMMIT-3
  toplam 4 kayıt · log sonu offset=10
```

Diskteki hâli (`kafka-dump-log.sh`):
```
| offset: 3  endTxnMarker: COMMIT  coordinatorEpoch: 0
| offset: 7  endTxnMarker: ABORT   coordinatorEpoch: 0
| offset: 9  endTxnMarker: COMMIT  coordinatorEpoch: 0
```

**Beş gözlem:**
1. Abort kayıtları offset 4–6'da **duruyor**; silme yok, atlama var.
2. Offset 3, 7, 9'daki **boşluklar control record'lardır** — consumer'a hiç verilmez ama
   offset tüketirler. `LAG` hesabında bu yüzden küçük sapmalar görürsün.
3. **`endTxnMarker: ABORT`** — abort da bir kayıttır, "hiçbir şey olmadı" değil.
4. `producerEpoch` her transaction'da **artıyor** (4 → 5 → 6 → 7): **TV2 (KIP-890)** davranışı.
5. Partition dizininde **`.txnindex`** dosyası oluştu; `read_committed` okuyucu abort
   aralıklarını oradan öğrenir.

> 📌 **Sık yapılan hata (lab'da yaşandı):** `flush()` çağırmadan `abortTransaction()` yapmak.
> O zaman batch'ler broker'a **hiç gitmez** ve log'da abort kaydı görünmez. Gerçek bir çöküşte
> batch'ler gönderilmiş olacağı için log'a yazılırlar.

🔗 Konu: [4.1 §2](4.1-transactions-internals.md)

---

### Soru 2 — `initTransactions()` üç işi

**Kısa cevap:**
1. **Transaction coordinator'ı bulur** — `hash(transactional.id) % transaction.state.log.num.partitions`
   (**50**) partition'ının lideri.
2. **Producer epoch'unu artırır** → aynı `transactional.id`'yi kullanan **eski oturumlar
   fenced olur** (zombie fencing).
3. **Yarım kalmış transaction'ları abort eder** — önceki oturumdan kalan açık transaction varsa
   temizlenir.

**Ayrıntı:**

- **Zombie fencing tam olarak burada olur.** Aynı `transactional.id` ile yeni bir producer
  başladığı anda, eskisi — hâlâ çalışıyor olsa bile — bir sonraki yazımında
  **`ProducerFencedException`** alır. Kubernetes'te "eski pod tam ölmedi, yenisi ayağa kalktı"
  senaryosunun çözümü budur.
- **Neden yarım transaction'lar temizlenmeli:** Açık bir transaction, o partition'daki tüm
  `read_committed` tüketicileri **LSO'da bekletir** (bkz. Soru 4). Temizlenmezse yeni oturum
  başlasa bile tüketiciler ilerleyemez.
- `initTransactions()` **yalnızca bir kez**, producer ömrünün başında çağrılır. Her transaction
  için değil.
- Bu çağrı `transaction.timeout.ms` (client varsayılanı **60 s**) ve broker'daki
  `transaction.max.timeout.ms` (900 s) ile de ilişkilidir: istenen timeout üst sınırı aşarsa
  reddedilir.

> 📌 **Sık yapılan hata:** `initTransactions()`'ı her batch'te çağırmak. Gereksiz epoch artışı
> ve coordinator turu üretir; ayrıca kendi kendini fence'leyen kod desenlerine yol açabilir.

🔗 Konu: [4.1 §1](4.1-transactions-internals.md)

---

### Soru 3 — Kafka→PostgreSQL akışında EOS neden çalışmaz? Ne yaparsın?

**Kısa cevap:** Çünkü Kafka transaction'ı **yalnızca Kafka'yı** kapsar: ürettiğin kayıtlar ve
tükettiğin offset'ler atomik olur. PostgreSQL'e yaptığın yazma bu transaction'ın **dışındadır**
ve Kafka onu geri alamaz. İki ayrı sistem, tek atomik blok yok — bu klasik **çift yazma (dual
write)** problemidir.

**Ne yaparsın — üç yol (tercih sırasıyla):**

**Yol A — Doğal idempotency.** İşlemi zaten tekrar edilebilir yaz:
`INSERT ... ON CONFLICT DO UPDATE` (upsert), `UPDATE bakiye = 500` (mutlak),
`SET durum = 'ONAYLANDI'`. Ek mekanizma gerekmez, en ucuz çözümdür.

**Yol B — Tekilleştirme tablosu.** İş verisi + `processed_messages(message_id PK)` kaydı **aynı
DB transaction'ında** yazılır. Duplicate gelirse `UNIQUE` ihlali → rollback → iş tekrarlanmaz.

**Yol C — Offset'i hedef sistemde tutmak.** Offset'i iş verisiyle aynı transaction'da sakla,
Kafka'ya commit etme:
```
BEGIN
  iş verisini yaz
  UPDATE kafka_offsets SET offset = ? WHERE topic = ? AND partition = ?
COMMIT
-- Açılışta: onPartitionsAssigned → tablodan offset oku → consumer.seek(...)
```
**Efektif exactly-once**'a en yakın yaklaşım budur: **tek karar noktası** vardır. Bedeli,
`kafka-consumer-groups --describe`'ın artık gerçeği göstermemesidir.

**Ayrıntı — EOS'un kapsamı:**

| Senaryo | EOS var mı |
|---|---|
| Kafka → işle → Kafka (+offset) | ✅ Atomik |
| Kafka → Streams → Kafka | ✅ (`exactly_once_v2`, 6.4) |
| Kafka → **veritabanı** | ❌ |
| Kafka → **HTTP API** | ❌ (çağrı geri alınamaz) |
| Kafka → e-posta / SMS | ❌ (ve asla olmayacak) |

> 📌 **Sık yapılan hata:** `@Transactional` bir **veritabanı** transaction'ıdır; Kafka onun içinde
> değildir. "Spring hallediyor" varsayımı bu problemin en yaygın sebebidir.

🔗 Konu: [4.1 §4](4.1-transactions-internals.md) · [4.2 §1, §3](4.2-outbox-ve-idempotent-tuketici.md)

---

### Soru 4 — `read_committed` tüketici neden broker sağlıklıyken bekler?

**Kısa cevap:** **LSO (Last Stable Offset)** yüzünden. `read_committed` bir tüketici, HW'ye kadar
değil **LSO'ya kadar** okuyabilir — yani **açık (henüz commit/abort edilmemiş) ilk
transaction'ın başlangıcına** kadar. O partition'da uzun süren açık bir transaction varsa, ondan
sonraki tüm kayıtlar commit edilmiş olsa bile **görünmez**.

**Ayrıntı:**

- **Neden böyle:** Tüketici, o açık transaction'ın commit mi abort mu edileceğini bilmiyor. Sonraki
  kayıtları verirse ve transaction abort edilirse, sırayı ve tutarlılığı bozar. Kafka bekletmeyi
  seçer.
- **Belirti:** "Consumer lag var ama broker sağlıklı, rebalance yok, CPU normal." Klasik ve sinsi
  bir vakadır — çünkü hiçbir metrik hata göstermez.
- **Sebepleri:**
  - Uzun süren bir transaction (büyük batch, yavaş aşağı akış, hatalı kodda unutulmuş commit).
  - Çökmüş bir transactional producer: coordinator `transaction.timeout.ms` (**60 s**) dolana
    kadar abort etmez.
  - Bir instance'ın fence'lenmesi gerekirken hâlâ açık transaction tutması.
- **Nasıl doğrularsın:**
  ```bash
  # LSO ile HW arasında fark var mı?
  kafka-get-offsets.sh --bootstrap-server kafka-1:19092 --topic T --time -1   # HW
  # __transaction_state'te açık transaction'lar
  kafka-transactions.sh --bootstrap-server kafka-1:19092 list
  kafka-transactions.sh --bootstrap-server kafka-1:19092 describe --transactional-id X
  ```
  Aynı topic'i `read_uncommitted` ile okuyup kayıtların **geldiğini** görmek de teşhisi bitirir.
- **Çözüm:** `transaction.timeout.ms`'i işleme süresine göre makul tut (çok uzun tutma), transaction
  granülaritesini küçült (batch başına bir transaction), ve gerekirse
  `kafka-transactions.sh --abort` ile takılan transaction'ı sonlandır.

> 📌 **Sık yapılan hata:** Bu vakada consumer ayarlarını (fetch, max.poll) kurcalamak. Sorun
> tüketicide değil, **partition'da açık duran bir transaction'da**dır.

🔗 Konu: [4.1 §5](4.1-transactions-internals.md) · [5.3 Arıza senaryoları](../05-operasyon/5.3-ariza-senaryolari.md)

---

### Soru 5 — İki instance aynı `transactional.id`'yi kullanırsa?

**Kısa cevap:** Birbirlerini sürekli **fence**'lerler. Her `initTransactions()` çağrısı epoch'u
artırdığı için, ikinci instance açıldığı anda birincisi **`ProducerFencedException`** alır ve
yazamaz. Birincisi yeniden başlayıp `initTransactions()` çağırırsa bu kez ikincisi fence'lenir —
sonsuz bir "ping-pong". Sonuç: **hiçbiri istikrarlı çalışamaz**, sürekli hata döngüsü.

**Ayrıntı:**

- `ProducerFencedException` **ölümcül** bir hatadır: o producer nesnesi bir daha kullanılamaz.
  Doğru davranış `producer.close()` çağırıp hatayı yukarı fırlatmaktır — asla `abortTransaction()`
  deneyip devam etmek değil.
  ```java
  } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
      producer.close();      // ÖLÜMCÜL — bu producer bir daha kullanılamaz
      throw e;
  } catch (KafkaException e) {
      producer.abortTransaction();   // geçici — geri al, batch'i yeniden dene
  }
  ```
- **Çözüm: `transactional.id` benzersiz ve kararlı olmalı.** İki gereksinim birden:
  - **Benzersiz** — her instance kendine ait bir id kullanmalı.
  - **Kararlı** — restart'ta **aynı** id ile dönmeli; yoksa zombie fencing çalışmaz ve restart
    sonrası duplicate koruması kaybolur.
  ```java
  pp.put(TRANSACTIONAL_ID_CONFIG, "odeme-isleyici-" + instanceId);   // KALICI ve BENZERSİZ
  ```
- **Kubernetes'te doğru kaynak:** StatefulSet pod ordinal'i (`worker-0`, `worker-1`). Deployment
  pod adları rastgeledir — `group.instance.id`'de olduğu gibi (3.2) burada da StatefulSet
  doğru araçtır.
- **UUID kullanmak neden yanlış:** Her restart yeni bir `transactional.id` üretir. Fencing
  çalışmaz (eski id hâlâ açık transaction tutuyor olabilir) ve `transactional.id.expiration.ms`
  (7 gün) dolana kadar coordinator'da çöp birikir.

> 📌 **Sık yapılan hata:** `transactional.id`'yi uygulama adına eşitlemek
> (`"odeme-servisi"`) ve 3 replika çalıştırmak. Üç instance sürekli birbirini fence'ler ve
> uygulama hiç ilerlemez — ama hata mesajı bunu doğrudan söylemez.

🔗 Konu: [4.1 §1, §3](4.1-transactions-internals.md) · [3.2 §5](../03-consumer/3.2-grup-protokolu-ve-rebalance.md)

---

## 4.2 Outbox ve idempotent tüketici

📄 Sorular: [`4.2-outbox-ve-idempotent-tuketici.md`](4.2-outbox-ve-idempotent-tuketici.md)

### Soru 1 — Çift yazma probleminin iki çöküş senaryosu

**Kısa cevap:**

```java
// ❌ BOZUK: iki ayrı sistem, tek atomik blok YOK
@Transactional
public void siparisOlustur(Siparis s) {
    siparisRepository.save(s);              // 1) DB'ye yaz
    kafkaTemplate.send("siparisler", s);    // 2) Kafka'ya yaz  ← burada çökersen?
}
```

| Çöküş anı | Sonuç |
|---|---|
| **DB commit'ten sonra, Kafka send'den önce** | DB'de sipariş **var**, Kafka'da **yok** → aşağı akış sistemleri (stok, faturalama, bildirim) siparişi hiç görmez. Müşteri "siparişim nerede?" der; DB'de duruyor gözükür. **Sessiz veri kaybı.** |
| **Kafka send'den sonra, DB commit'ten önce** (ya da rollback) | Kafka'da olay **var**, DB'de sipariş **yok** → **hayalet sipariş**: stok düşülür, fatura kesilir, bildirim gider ama sipariş kaydı yoktur. Mutabakat tutmaz. |

**Ayrıntı:**

- İkinci senaryo daha sinsidir: `@Transactional` rollback olsa bile **Kafka'ya gitmiş mesaj geri
  alınamaz**. Kafka'da "geri al" diye bir işlem yoktur (transaction kullanmıyorsan).
- Üçüncü bir varyant: `kafkaTemplate.send()` asenkrondur — DB commit olur, `send()` çağrısı döner,
  ama arka planda teslim **başarısız olur** ve callback'te sessizce loglanır (2.3). Sonuç birinci
  senaryonun aynısıdır.
- **Bu problem sıralama değiştirilerek çözülmez.** Hangi sırayla yazarsan yaz, ikisinin arasında
  bir çöküş penceresi vardır.
- **Çözüm:** İki yazma yerine **tek** yazma — outbox deseni.

> 📌 **Sık yapılan hata:** "`send()`'i `@Transactional`'ın içine alırsam Spring halleder" sanmak.
> `@Transactional` bir **veritabanı** transaction'ıdır; Kafka onun kapsamında değildir.

🔗 Konu: [4.2 §1](4.2-outbox-ve-idempotent-tuketici.md)

---

### Soru 2 — Outbox neden exactly-once değil at-least-once verir?

**Kısa cevap:** Çünkü **relay** adımı atomik değildir. Relay mesajı Kafka'ya yazar ve sonra
outbox satırını "yayınlandı" olarak işaretler. Bu iki adım arasında çökerse, mesaj Kafka'ya
gitmiştir ama işaretlenmemiştir — bir sonraki turda **tekrar** gönderilir.

**Ayrıntı:**

- Outbox'ın çözdüğü şey **çift yazma**dır: uygulama artık tek bir sisteme (DB'ye) yazar, iş verisi
  ve olay **aynı transaction'da** commit olur:
  ```java
  @Transactional
  public void siparisOlustur(Siparis s) {
      siparisRepository.save(s);
      outboxRepository.save(new OutboxEvent(
              UUID.randomUUID(), s.getMusteriId(), "siparisler", toJson(s)));
  }   // ikisi ya birlikte commit olur ya birlikte rollback
  ```
- Çözmediği şey **relay'in kendisidir** — orada yine "iki sistem, tek atomik blok yok" durumu
  vardır, sadece bir katman aşağı taşınmıştır. Fark şu: bu kez **kayıp değil, tekrar** olur.
  Tekrar edilebilir; kayıp edilemez.
- CDC (Debezium) kullanmak da bunu değiştirmez: CDC de bir offset tutar ve o offset'i ilerletmeden
  önce çökebilir.
- **Bu yüzden kural:** *Outbox **her zaman** tüketici tarafında idempotency ile eşleşmelidir.*
  Outbox tek başına yarım bir çözümdür.

**Relay iki şekilde yapılır:**

| | CDC (Debezium vb.) | Poller (zamanlanmış sorgu) |
|---|---|---|
| Nasıl | DB'nin **WAL**'ini okur | `SELECT ... WHERE published_at IS NULL` |
| Gecikme | Milisaniye | Poll aralığı kadar |
| DB yükü | Neredeyse yok | Sorgu + güncelleme |
| Sıra | WAL sırası korunur | Sorgu sırasına bağlı |
| Ne zaman | Yüksek hacim, düşük gecikme | Küçük/orta hacim, sade mimari |

**Sessiz ama kritik ayrıntı:** `aggregate_id`'yi Kafka key'i yaparsan aynı toplulaştırmanın
olayları aynı partition'a gider ve **sıralı** kalır (2.1).

> 📌 **Sık yapılan hata:** Outbox kurup "artık exactly-once'ız" demek ve tüketici tarafında hiçbir
> tekilleştirme yapmamak. Duplicate ilk relay çöküşünde gelir.

🔗 Konu: [4.2 §2](4.2-outbox-ve-idempotent-tuketici.md)

---

### Soru 3 — `bakiye = bakiye + 10` ile `bakiye = 500` arasındaki idempotency farkı

**Kısa cevap:** `bakiye = 500` **mutlak** bir yazmadır: kaç kez uygulanırsa uygulansın sonuç
aynıdır (500). `bakiye = bakiye + 10` **artımlıdır**: her uygulama sonucu değiştirir. İki kez
işlenirse bakiye 20 artar. Birincisi **doğal olarak idempotent**, ikincisi değildir.

**Ayrıntı:**

| Doğal idempotent | Değil |
|---|---|
| `UPDATE hesap SET bakiye = 500` (mutlak) | `UPDATE hesap SET bakiye = bakiye + 10` (artımlı) |
| `INSERT ... ON CONFLICT DO UPDATE` (upsert) | Düz `INSERT` |
| `PUT /kaynak/42` | `POST /kaynak` |
| `SET durum = 'ONAYLANDI'` | `durum++` |

> **Hafıza kancası:** **Mutlak yazma idempotenttir, artımlı yazma değildir.** Tasarımda mümkünse
> olayı "yeni durum" olarak taşı, "delta" olarak değil.

**Bu bir veri modeli kararıdır, bir Kafka kararı değil.** Olayı şöyle tasarlarsan:
```json
{"hesapId": 42, "islem": "PARA_YATIRMA", "tutar": 10}      ← delta, idempotent DEĞİL
```
tüketicinin tekilleştirme yapması **zorunlu** olur. Şöyle tasarlarsan:
```json
{"hesapId": 42, "yeniBakiye": 500, "versiyon": 17}          ← mutlak, doğal idempotent
```
tekrar işlense bile sonuç değişmez (hatta `versiyon` ile eski olayları da eleyebilirsin).

**Ne zaman delta kaçınılmazdır:** Muhasebe/defter (ledger) sistemlerinde her hareket ayrı bir
kayıttır ve toplam onlardan hesaplanır. Orada çözüm mutlak yazma değil, **hareketin kendisini
tekilleştirmektir** — `INSERT INTO hareketler(hareket_id, ...)` ile `hareket_id` üzerinde
`UNIQUE` kısıtı. Duplicate `INSERT` ihlal verir, transaction rollback olur, toplam bozulmaz.

**Lab kanıtı:** 100 benzersiz iş, 30'u iki kez gönderildi (130 kayıt):
```
NAİF       toplam bakiye = 1300   ✗ BOZUK      (artımlı yazma + tekilleştirme yok)
IDEMPOTENT toplam bakiye = 1000   ✓            (message-id ile tekilleştirme)
```
Naif tüketici **%30 fazla** para yazdı.

> 📌 **Sık yapılan hata:** Idempotency'yi yalnızca bir tüketici sorunu sanmak. Çoğu zaman en ucuz
> çözüm, **olayın şeklini** değiştirmektir — delta yerine durum taşımak.

🔗 Konu: [4.2 §3–4](4.2-outbox-ve-idempotent-tuketici.md)

---

### Soru 4 — `message_id = topic-partition-offset` hangi senaryoda kırılır?

**Kısa cevap:** Kayıt **yeniden yazıldığında** — yani offset değiştiğinde. En yaygın üç senaryo:
1. **MirrorMaker 2 ile kopyalama (7.2):** Hedef cluster'da aynı kayıt **farklı offset** alır.
   DR'a geçtiğinde tüm `message_id`'ler değişir ve tekilleştirme tablon işe yaramaz — her şey
   yeniden işlenir.
2. **Replay / yeniden üretim:** Bir hata sonrası kayıtları yeni bir topic'e (ya da aynı topic'e
   yeniden) basarsan offsetler baştan başlar.
3. **Topic yeniden oluşturma / partition sayısı değişimi:** Aynı iş kaydı farklı bir
   `partition-offset` çiftine düşer.

**Ayrıntı — `message_id` kaynakları:**

| Kaynak | Artı | Eksi |
|---|---|---|
| **İş anahtarı** (`siparis_id`, `odeme_id`) | En doğrusu; iş anlamı taşır, taşınmaya dayanıklı | Her olayda mevcut olmayabilir |
| Producer'ın ürettiği UUID (header) | Genel, kolay | Producer'ın **aynı** id ile yeniden göndermesi şart |
| `topic-partition-offset` | Her zaman var, ek iş yok | **Replay/mirror'da değişir** |

- Dördüncü sinsi senaryo: **transaction'lı topic'lerde control record'lar offset tüketir** (4.1).
  Offset numaraları veri kayıtlarıyla birebir örtüşmez; bu tekilliği bozmaz ama
  "offset = kayıt sayısı" varsayan yan hesapları bozar.
- **Ne zaman yine de kullanılabilir:** Tek cluster, replay yapılmayan, retention'ı kısa, DR'ı
  olmayan bir sistemde `topic-partition-offset` pratik ve ucuzdur. Ama bu varsayımların **hepsi**
  bir gün değişir.
- **Doğru tercih:** İş anahtarı varsa onu kullan. Yoksa producer'da **üretim anında** bir UUID
  üret ve header'a koy — kritik nokta, retry ve yeniden gönderimde **aynı UUID'nin** korunmasıdır.

> 📌 **Sık yapılan hata:** DR tatbikatında (MM2 ile ikinci cluster'a geçiş) tekilleştirmenin
> çöktüğünü keşfetmek. O an, en kötü an.

🔗 Konu: [4.2 §3](4.2-outbox-ve-idempotent-tuketici.md) · [7.2 MirrorMaker 2](../07-connect/7.2-connector-smt-ve-mm2.md)

---

### Soru 5 — Duplicate tespit ettiğinde offset'i ilerletmezsen?

**Kısa cevap:** Tüketici o kayda **takılır** ve sonsuza kadar aynı duplicate'i işlemeye çalışır.
Her turda "bunu zaten işledim" der, iş yapmaz, **ama offset de ilerlemediği için** bir sonraki
`poll()` yine aynı kaydı getirir. Grup hiç ilerlemez, lag sürekli büyür.

**Ayrıntı — lab kodundaki kritik satır**
([`SinkStore.java`](../lab/src/main/java/tr/improsy/kafkalab/k42/SinkStore.java)):
```java
if (!processedIds.add(messageId)) {   // UNIQUE(message_id) kısıtının karşılığı
    offsets.put(tp, nextOffset);      // ⬅ duplicate OLSA BİLE offset İLERLEMELİ
    return false;                     // iş tekrarlanmaz
}
```

- **Neden bu kadar kolay atlanır:** Kod okununca mantıklı görünür — "duplicate, hiçbir şey yapma,
  `return`". Ama "hiçbir şey yapmamak" offset'i de ilerletmemek demektir.
- Belirti sinsidir: hata yok, istisna yok, CPU normal. Sadece **lag artıyor** ve aynı
  `message-id` log'da tekrar tekrar görünüyor.
- Bu, offset'i **dış sistemde** tutan tasarımlarda (Yol C) daha da kritiktir: Kafka'ya commit
  etmiyorsan, ilerlemeyi kaydeden tek yer o tablodur.
- Doğru zihinsel model: **offset "işledim" değil, "buradan devam et" demektir** (3.3). Kaydı
  atlamak da bir ilerlemedir.
- Aynı prensip zehirli kayıtta da geçerlidir (2.4): kaydı DLQ'ya yazdıktan sonra
  `consumer.seek(tp, offset + 1)` ile **ilerlemek** zorundasın.

> 📌 **Sık yapılan hata:** Duplicate ve zehirli kayıt dallarında `continue`/`return` yazıp offset
> güncellemesini atlamak. İki farklı konuda (4.2 ve 2.4) aynı hata; ikisinin de belirtisi
> "tüketici duruyor ama hata yok"tur.

🔗 Konu: [4.2 §4](4.2-outbox-ve-idempotent-tuketici.md) · [3.3 §1](../03-consumer/3.3-offset-ve-commit.md)

---

⬅️ [Bölüme dön](4.1-transactions-internals.md) · 📖 [Kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md) · ⚙️ [Ayar rehberi](../00-baslangic/03-ayar-rehberi.md)
