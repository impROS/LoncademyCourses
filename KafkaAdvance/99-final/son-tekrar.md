# Son tekrar — 24 saat kala

> 📖 Tanımadığın bir kısaltma ya da KIP görürsen: [kavram sözlüğü](../00-baslangic/02-kavram-sozlugu.md)
>
> Bunu bir mülakattan, sunumdan, sertifika sınavından ya da üretime çıkıştan **24 saat önce** oku.
> Yeni bir şey öğrenme zamanı değil; **hatırlama ve refleks** zamanı.

---

## 1. Bugün ne yap, ne yapma

| ✅ Yap | ❌ Yapma |
|---|---|
| [`cheatsheet.md`](cheatsheet.md)'i baştan sona bir kez oku (20 dk) | Yeni bir konuya başlama |
| İki genel sınavı çöz, **yanlışlarını** konu dosyalarından tara | Testleri ezberlemek için tekrar tekrar çözme |
| Aşağıdaki 10 soruyu **sesli** cevapla | Gece geç saate kadar çalışma |
| Lab cluster'ını bir kez ayağa kaldır, üç komut çalıştır | Yeni bir lab kurmaya kalkışma |
| Zayıf iki alanını seç, sadece onların "60 saniyelik özet"ini oku | Her konuyu baştan okumaya çalışma |

---

## 2. ⭐ Sesli anlatma turu — 10 soru

Her birini **kâğıda bakmadan, yüksek sesle** anlat. Takıldığın yer, bugün tekrar edeceğin yerdir.

1. Bir kayıt `producer.send()`'den diske ulaşana kadar hangi aşamalardan geçer?
   *(2.1 → 1.1 → 1.2)*
2. `acks=all`, `replication.factor=3`, `min.insync.replicas=2` bir topic'te iki broker çökerse ne olur?
   Üçüncüsü de çökerse? *(1.2)*
3. Bir consumer group'ta rebalance neden olur, KIP-848 ile ne değişti? *(3.2)*
4. Offset commit'ini işlemenin öncesine almak neyi değiştirir? *(3.3)*
5. Kafka'nın exactly-once garantisi tam olarak neyi kapsar, neyi kapsamaz? *(4.1, 4.2)*
6. Compaction'ın garantisi nedir? "Her key'den bir kayıt kalır" neden yanlış? *(1.4)*
7. Kafka Streams'te bir topolojinin task sayısı nasıl belirlenir? Ölçekleme tavanı nedir? *(6.1)*
8. Stateful bir Streams uygulaması yeni bir makinede başlarken ne olur, neden dakikalar sürebilir? *(6.2)*
9. Partition sayısını artırmak neden geri dönülemez bir karardır? *(2.1, 5.5)*
10. "Connector RUNNING ama veri akmıyor" vakasında hangi üç şeye bakarsın? *(7.1)*

---

## 3. ⭐ Rakam turu — 60 saniyede

Bunları düşünmeden söyleyebilmelisin:

| Soru | Cevap |
|---|---|
| `linger.ms` varsayılanı (4.x) | **5** |
| `max.poll.records` / `max.poll.interval.ms` | **500** / **300000 (5 dk)** |
| `session.timeout.ms` / `heartbeat.interval.ms` | **45000** / **3000** |
| `delivery.timeout.ms` | **120000** |
| `max.in.flight` üst sınırı (idempotence açık) | **5** |
| Retention varsayılanı | **7 gün** |
| `delete.retention.ms` (tombstone) | **24 saat** |
| `offsets.retention.minutes` | **10080 (7 gün)** |
| `replica.lag.time.max.ms` | **30000** |
| `__consumer_offsets` partition sayısı | **50** |
| Raft quorum: 3 / 5 voter kayıp toleransı | **1** / **2** |
| Streams `commit.interval.ms` (ALO / EOS) | **30000** / **100** |
| Share group `delivery.count.limit` / lock süresi | **5** / **30 s** |
| `min.cleanable.dirty.ratio` | **0.5** |
| Segment `bytes` / `ms` alt sınırları (KIP-1030) | **1 MiB** / **1 dk** |

---

## 4. ⭐ Bir cümlelik gerçekler

Bunları aynen hatırla; çoğu sorunun cevabı bunlardan biridir:

- `acks=all` **ISR'deki** tüm replikalar demektir, "tüm replikalar" değil.
- **replication.factor − min.insync.replicas** = yazmaya devam ederek tolere edilen broker kaybı.
- **Aktif segment asla silinmez** — "retention çalışmıyor"un bir numaralı sebebi.
- Compaction garantisi: **"son değer kaybolmaz"**, "tek kayıt kalır" değil.
- **Önce commit = kayıp, sonra commit = tekrar.** Arada üçüncü seçenek yok.
- Idempotence **oturum başınadır**; restart yeni PID demektir.
- **`session.timeout` = yaşıyor mu, `max.poll.interval` = ilerliyor mu.**
- KIP-848 broker'da varsayılan destekli, **client'ta opt-in** (`group.protocol=consumer`).
- Abort edilen kayıtlar **silinmez, filtrelenir**; `read_uncommitted` ile hâlâ görünürler.
- Kafka'nın EOS'u **yalnızca Kafka içidir**.
- **Partition sayısı azaltılamaz**; artırmak `hash(key)%N`'i bozar ve sıra garantisini kırar.
- **TLS sıfır kopyayı iptal eder** — CPU artışı beklenendir.
- Kotalar **reddetmez, geciktirir**.
- Tüketici ACL'lerinde en sık unutulan **`Group:Read`**'tir.
- Streams'te **task = alt topoloji × partition**; thread artırmak task sayısını değiştirmez.
- Streams'te gerçek durum kaynağı **changelog**'dur; yerel disk bir önbellektir.
- Pencereler **stream time** ile kapanır; veri gelmezse sonuç gelmez.
- Connect'te **sink connector bir consumer group'tur**.
- MM2 **at-least-once**'tır; failover'da duplicate normaldir.

---

## 5. ⭐ Tuzak soruları — en sık düşülen 8 hata

| Soru böyle gelir | Refleks cevabın |
|---|---|
| "Kafka mesajı tüketilince siler mi?" | **Hayır** — retention siler, tüketim offset ilerletir |
| "Sıra garantisi topic düzeyinde mi?" | **Hayır** — **partition** içinde |
| "Idempotence exactly-once verir mi?" | **Hayır** — retry duplicate'ini önler, oturum içinde |
| "acks=all veri kaybını tamamen engeller mi?" | **Hayır** — `min.insync.replicas` olmadan eksik |
| "Compaction her key'den tek kayıt bırakır mı?" | **Hayır** — son değer korunur, fazlası kalabilir |
| "4.x'te KIP-848 otomatik açık mı?" | **Hayır** — client `group.protocol=consumer` demeli |
| "Streams'te thread artırınca hızlanır mı?" | **Task sayısına kadar** — sonra atıl kalır |
| "Connector RUNNING ise sağlıklı mı?" | **Hayır** — task listesine ve offsetlere bak |

---

## 6. Sınav/mülakat stratejisi

**Çoktan seçmeli sınavda:**
1. Önce **mutlak ifadeleri** ele: `always`, `never`, `only`, "tamamen", "her zaman" içeren şıklar
   genelde yanlıştır (bu setin testlerinde bilerek kullanıldı).
2. Soruda **sürüm** geçiyorsa (3.x mi 4.x mi) cevabı ona göre seç — en sık tuzak budur.
3. İki şık doğru görünüyorsa **daha spesifik** olanı seç.
4. "Choose TWO" görürsen **iki** işaretle; emin olmadığın soruyu işaretleyip geç, sona bırak.
5. Hesap soruları (depolama, partition) **birim** hatası içerir: KB/MB/GB, saniye/gün,
   partition başına / topic başına, replikasyon çarpanı.

**Mülakatta:**
1. Soruyu **katmana indir**: "Bu producer tarafı mı, broker mı, consumer mı?"
2. Cevabı **takasla** ver: "X yaparsın ama bedeli Y'dir." Bu, ileri seviye sinyalidir.
3. Bilmediğini **söyle** ve nasıl bulacağını anlat: "Şu metriğe bakar, şu komutu çalıştırırdım."
4. Sayı verirken **kaynağını** söyle: "Varsayılan 5 dakika ama biz 2'ye çekmiştik çünkü…"
5. "Nasıl teşhis edersin?" soruları **en değerlisidir** — 5.3'ün akışını kullan:
   **katman → belirti → kanıt.**

---

## 7. Son 30 dakika — hızlı tarama listesi

Sırayla bak, her birinde takılırsan ilgili dosyaya 2 dakika dön:

- [ ] Dayanıklılık zinciri: `acks` → `min.insync` → `unclean` → callback → commit sırası
- [ ] Üç zamanlayıcı: heartbeat / session / max.poll.interval
- [ ] Rebalance formülü ve KIP-848 farkı
- [ ] Transaction: PID vs `transactional.id`, control record, LSO
- [ ] Compaction vs delete vs `compact,delete`; tombstone ömrü
- [ ] Streams: task hesabı, changelog restore, stream time
- [ ] Connect: connector/task/worker, source vs sink offset
- [ ] Metrik dörtlüsü: ActiveController / OfflinePartitions / UnderReplicated / UnderMinIsr
- [ ] Kapasite formülü ve partition kararının geri dönülemezliği
- [ ] 4.0 → 4.3 arası sürüm eşleştirmesi

---

## 8. Yarın

- Sınav/mülakat sabahı **yeni bilgi okuma**. Sadece [`cheatsheet.md`](cheatsheet.md)'in
  **1. ve 4. bölümlerine** (varsayılanlar + karıştırılan ikililer) göz at.
- Bir soruya takılırsan **katmana indir**: producer / broker / consumer.
- Emin olmadığın yerde **takası söyle** — "şunu kazanırsın, şunu kaybedersin" cevabı,
  ezberlenmiş bir tanımdan her zaman daha iyidir.

**Başarılar.** 🎯

---

➡️ [`genel-sinav-1.html`](genel-sinav-1.html) · [`genel-sinav-2.html`](genel-sinav-2.html)
➡️ [`cheatsheet.md`](cheatsheet.md)
