# Kafka Advanced — İleri Seviye Apache Kafka Çalışma Seti

> **Baz sürüm:** Apache Kafka **4.3.x** (KRaft-only, ZooKeeper yok) · **Dil:** Java 21 · **Süre:** 6 hafta
> **Ön koşul:** Temel producer/consumer kullanmışlığın var. Bu set "Kafka nedir"i anlatmaz;
> **broker'ın içinde ne olduğunu**, neden yavaşladığını, neden veri kaybettiğini ve nasıl düzeltileceğini anlatır.

Bu bir sohbet değil, diskte duran bir kurs. Her konu kendi dosyasında, her konunun sonunda
tarayıcıda çalışan skorlu bir test var. Tüm pratikler lokalde, **ücretsiz** (Docker + Maven).

---

## Nasıl çalışılır

1. **Önce ortamı kur.** [`00-baslangic/01-ortam-kurulumu.md`](00-baslangic/01-ortam-kurulumu.md) —
   3 broker'lı KRaft cluster + `lab/` Maven projesi. Bu bitmeden konulara geçme; her konuda çalıştıracak kod var.
2. **Konu dosyasını baştan sona oku.** Atlamak yok. Tablolar ve `⚠️ Tuzak` kutuları setin en değerli kısmı.
   Bir kısaltma ya da KIP numarası ilk geçtiğinde yanında **parantez içinde kısa açıklaması** ve
   **↗ ile [kavram sözlüğüne](00-baslangic/02-kavram-sozlugu.md) bağlantı** vardır — takıldığın yerde tıkla, oku, geri dön.
3. **Pratiği gerçekten yap.** Kodu çalıştır, çıktıya bak, `Kaydet:` satırlarına gördüğün sayıyı yaz.
   Okuyup geçtiğin lab öğrenilmemiş sayılır — bu setin amacı "biliyorum" değil, "yaptım" demek.
4. **"Kendini kontrol" sorularını kâğıda yaz.** Teste girmeden önce. Yazamıyorsan konuyu anlamamışsındır.
   Yazdıktan **sonra** o bölümün `cevaplar.md` dosyasını aç ve kendi cevabınla karşılaştır —
   her soru için kısa cevap, ayrıntı, sık yapılan hata ve konuya dönüş bağlantısı var.
5. **Teste gir** (`*-test.html` dosyasına çift tıkla). Skor sunucusu çalışıyorsa sonuç,
   test biter bitmez **kendiliğinden** konu dosyasının sonundaki *Test geçmişim* tablosuna
   ve buradaki ilerleme tablosuna yazılır — aşağıya bak. **%80 altındaysan** testin sonundaki zayıf alt konu
   raporuna bak, o başlıkları tekrar oku, testi yeniden çöz.
6. **Takıldığın yeri sor.** Sorduğun her soru ayrıntılı cevabıyla birlikte
   [`soru-cevap.md`](soru-cevap.md)'ye yazılır — konu dosyalarına dağılmaz, tek yerde birikir.
7. **Sonraki konuya geç.** Sıra rastgele değil: her konu öncekine dayanıyor.
8. **Haftanın sonunda** [`99-final/cheatsheet.md`](99-final/cheatsheet.md)'in o haftaya ait satırlarını oku.
9. **Bitince** iki genel sınavı çöz. 60 soru / 90 dakika, gerçek mülakat + üretim senaryosu karışımı.

> **Kavram sözlüğü:** [`00-baslangic/02-kavram-sozlugu.md`](00-baslangic/02-kavram-sozlugu.md) —
> ISR, ELR, LSO, TV2, KRaft gibi kısaltmalar; 20 KIP'in tek satırlık açıklaması; adından ne yaptığı
> anlaşılmayan ayarlar. Baştan sona okunmak için değil, **dönmek için** var.

> **Ayar rehberi:** [`00-baslangic/03-ayar-rehberi.md`](00-baslangic/03-ayar-rehberi.md) —
> producer/consumer/broker/Streams/Connect ayarlarının tamamı; her biri için *ne yapar*,
> *ne zaman dokunulur*, *değeri seçerken nasıl düşünülür*. Ayrıca senaryodan ayar setine
> **karar reçeteleri** ve **tehlikeli ayarlar** listesi.

> **Soru & cevap defteri:** [`soru-cevap.md`](soru-cevap.md) — çalışırken sorduğun soruların
> sorduğun hâliyle ve ayrıntılı cevabıyla tutulduğu dosya. Bir soru birden çok konuya
> dayanabildiği için konu dosyalarına değil **tek bir yere** yazılır.

> **Cevaplar:** Her bölüm klasöründe bir `cevaplar.md` var —
> o bölümdeki tüm "Kendini kontrol" sorularının ayrıntılı çözümü. Konu dosyasından ayrı
> tutuldu ki **önce kendin düşün**, sonra bak.

> **Not alma:** Konu dosyalarına kendi notunu eklemekten çekinme; bu dosyalar senin.
> `Kaydet: ______` satırları özellikle boş bırakıldı.

---

## İlerleme tablosu

Bitirdiğin kutuyu işaretle, test skorunu yaz.

### 00 — Başlangıç
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [x] | [00.1 Genel bakış ve ileri seviye trickler](00-baslangic/00-genel-bakis-ve-trickler.md) | [test](00-baslangic/00-test.html) | %100 (14/14) |
| [ ] | [00.2 Ortam kurulumu (cluster + Maven)](00-baslangic/01-ortam-kurulumu.md) | — | — |
| 📖 | [00.3 **Kavram sözlüğü** — kısaltmalar, KIP'ler, ayarlar](00-baslangic/02-kavram-sozlugu.md) | başvuru | — |
| ⚙️ | [00.4 **Ayar rehberi** — hangi ayar, ne zaman, nasıl düşünülür](00-baslangic/03-ayar-rehberi.md) | başvuru | — |
| 💡 | [Kendini kontrol cevapları](00-baslangic/cevaplar.md) | cevap | — |
| ❓ | [**Soru & cevap defteri** — çalışırken sorduklarım](soru-cevap.md) | başvuru | — |

### 01 — Broker & Depolama Internals *(Hafta 1)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [x] | [1.1 Log segment, index ve sıfır kopya okuma](01-broker-depolama/1.1-log-segment-ve-index.md) | [test](01-broker-depolama/1.1-test.html) | %100 (14/14) |
| [ ] | [1.2 Replikasyon: ISR, High Watermark, ELR](01-broker-depolama/1.2-replikasyon-isr-hw.md) | [test](01-broker-depolama/1.2-test.html) | ___ |
| [ ] | [1.3 KRaft: metadata quorum ve controller](01-broker-depolama/1.3-kraft-metadata.md) | [test](01-broker-depolama/1.3-test.html) | ___ |
| [ ] | [1.4 Retention, compaction ve tombstone](01-broker-depolama/1.4-retention-ve-compaction.md) | [test](01-broker-depolama/1.4-test.html) | ___ |
| [ ] | [1.5 Tiered storage](01-broker-depolama/1.5-tiered-storage.md) | [test](01-broker-depolama/1.5-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](01-broker-depolama/cevaplar.md) | cevap | — |

### 02 — Producer Internals *(Hafta 2)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [2.1 RecordAccumulator, batching ve partitioner](02-producer/2.1-accumulator-ve-batching.md) | [test](02-producer/2.1-test.html) | ___ |
| [ ] | [2.2 Idempotence ve sıralama garantisi](02-producer/2.2-idempotence-ve-siralama.md) | [test](02-producer/2.2-test.html) | ___ |
| [ ] | [2.3 Teslimat garantileri: acks, min.insync, retry](02-producer/2.3-teslimat-garantileri.md) | [test](02-producer/2.3-test.html) | ___ |
| [ ] | [2.4 Serialization ve şema evrimi](02-producer/2.4-serialization-ve-sema.md) | [test](02-producer/2.4-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](02-producer/cevaplar.md) | cevap | — |

### 03 — Consumer Internals *(Hafta 2–3)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [3.1 Fetch/poll döngüsü ve akış kontrolü](03-consumer/3.1-fetch-ve-poll-dongusu.md) | [test](03-consumer/3.1-test.html) | ___ |
| [ ] | [3.2 Grup protokolü ve rebalance (KIP-848)](03-consumer/3.2-grup-protokolu-ve-rebalance.md) | [test](03-consumer/3.2-test.html) | ___ |
| [ ] | [3.3 Offset yönetimi ve commit stratejileri](03-consumer/3.3-offset-ve-commit.md) | [test](03-consumer/3.3-test.html) | ___ |
| [ ] | [3.4 Share groups (Queues for Kafka)](03-consumer/3.4-share-groups.md) | [test](03-consumer/3.4-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](03-consumer/cevaplar.md) | cevap | — |

### 04 — Transaction & Exactly-Once *(Hafta 3)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [4.1 Transactions internals ve EOS](04-eos-transaction/4.1-transactions-internals.md) | [test](04-eos-transaction/4.1-test.html) | ___ |
| [ ] | [4.2 Outbox ve idempotent tüketici desenleri](04-eos-transaction/4.2-outbox-ve-idempotent-tuketici.md) | [test](04-eos-transaction/4.2-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](04-eos-transaction/cevaplar.md) | cevap | — |

### 05 — Operasyon *(Hafta 4)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [5.1 Performans tuning: throughput ve gecikme](05-operasyon/5.1-performans-tuning.md) | [test](05-operasyon/5.1-test.html) | ___ |
| [ ] | [5.2 Metrikler ve izleme](05-operasyon/5.2-metrikler-ve-izleme.md) | [test](05-operasyon/5.2-test.html) | ___ |
| [ ] | [5.3 Arıza senaryoları ve troubleshooting](05-operasyon/5.3-ariza-senaryolari.md) | [test](05-operasyon/5.3-test.html) | ___ |
| [ ] | [5.4 Güvenlik: SASL ([kimlik doğrulama çerçevesi ↗](00-baslangic/02-kavram-sozlugu.md#sasl)), TLS, ACL, kota](05-operasyon/5.4-guvenlik.md) | [test](05-operasyon/5.4-test.html) | ___ |
| [ ] | [5.5 Kapasite planlama ve partition tasarımı](05-operasyon/5.5-kapasite-ve-partition-tasarimi.md) | [test](05-operasyon/5.5-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](05-operasyon/cevaplar.md) | cevap | — |

### 06 — Kafka Streams *(Hafta 5)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [6.1 Topoloji, task ve thread modeli](06-streams/6.1-topoloji-ve-task-modeli.md) | [test](06-streams/6.1-test.html) | ___ |
| [ ] | [6.2 State store, changelog ve standby](06-streams/6.2-state-store-ve-changelog.md) | [test](06-streams/6.2-test.html) | ___ |
| [ ] | [6.3 Join, windowing ve zaman semantiği](06-streams/6.3-join-ve-windowing.md) | [test](06-streams/6.3-test.html) | ___ |
| [ ] | [6.4 Streams'te EOS, hata yönetimi ve test](06-streams/6.4-eos-hata-ve-test.md) | [test](06-streams/6.4-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](06-streams/cevaplar.md) | cevap | — |

### 07 — Connect & Replikasyon *(Hafta 6)*
| ✔ | Konu | Test | Skor |
|---|---|---|---|
| [ ] | [7.1 Connect mimarisi ve offset yönetimi](07-connect/7.1-connect-mimarisi.md) | [test](07-connect/7.1-test.html) | ___ |
| [ ] | [7.2 Connector/SMT geliştirme ve MirrorMaker 2](07-connect/7.2-connector-smt-ve-mm2.md) | [test](07-connect/7.2-test.html) | ___ |
| 💡 | [Kendini kontrol cevapları](07-connect/cevaplar.md) | cevap | — |

### 99 — Final *(Hafta 6 sonu)*
| ✔ | Dosya | |
|---|---|---|
| [ ] | [Cheatsheet — tüm set tek sayfada](99-final/cheatsheet.md) | — |
| [ ] | [Son tekrar — mülakat/sunum öncesi 24 saat](99-final/son-tekrar.md) | — |
| [ ] | Genel Sınav 1 | [60 soru / 90 dk](99-final/genel-sinav-1.html) · Skor: ___ |
| [ ] | Genel Sınav 2 | [60 soru / 90 dk](99-final/genel-sinav-2.html) · Skor: ___ |

---

## 6 haftalık program

| Hafta | Konular | Haftalık yük | Hafta sonu hedefi |
|---|---|---|---|
| 1 | 00.1, 00.2, 1.1 → 1.5 | ~5 sa | Broker'ın diskte ne yaptığını çizebilmek |
| 2 | 2.1 → 2.4, 3.1 | ~5 sa | Bir producer'ı throughput için tune edebilmek |
| 3 | 3.2 → 3.4, 4.1, 4.2 | ~5 sa | Rebalance fırtınasını teşhis edip durdurabilmek |
| 4 | 5.1 → 5.5 | ~5 sa | Bir arıza senaryosunu metriklerden okuyabilmek |
| 5 | 6.1 → 6.4 | ~5 sa | Stateful bir Streams uygulaması yazıp resetleyebilmek |
| 6 | 7.1, 7.2, cheatsheet, 2 genel sınav | ~5 sa | Uçtan uca veri hattı tasarlayabilmek |

**Geride kaldıysan** şu sırayla feda et: 1.5 (tiered storage) → 3.4 (share groups) → 7.2.
**Asla feda etme:** 1.2, 2.3, 3.2, 4.1, 5.3. Bu beşi bu setin omurgası.

---

## Ne kurmalı / ne kurmamalı

| ✅ Kur | ❌ Kurma |
|---|---|
| Docker Desktop (veya colima/podman) — cluster bunun üstünde döner | Confluent Platform full stack — bu set **Apache Kafka** üzerinden ilerler, gereksiz 8 GB RAM yer |
| JDK 21 (broker 17+, client 11+ ister) | JDK 8 — Kafka 4.x client/Streams'te desteklenmiyor |
| Maven 3.9+ | Ayrı bir Kafka GUI — bu setteki her gözlem CLI ve JMX ([JVM metrik arayüzü ↗](00-baslangic/02-kavram-sozlugu.md#jmx)) ile yapılıyor; GUI aradaki katmanı gizler |
| `apache/kafka:4.3.1` imajı | Eski `wurstmeister/kafka`, `bitnami/kafka` — env değişkenleri farklı, bu setin komutları tutmaz |

**💸 Maliyet:** Bu setin tamamı **lokal ve ücretsizdir.** Bulut hesabı, lisans, ücretli tier gerekmez.
Tek maliyetin disk (~2 GB imaj + veri) ve RAM (~3 GB, 3 broker).

---

## Klasör ağacı

---

## 📊 Otomatik skor kaydı

Test bittiği anda sonuç (tarih, skor, süre, zayıf alanlar) **kendiliğinden** iki yere yazılır:
konu dosyasının sonundaki *Test geçmişim* tablosuna ve yukarıdaki ilerleme tablosunun *Skor*
hücresine. Elle bir şey yazman gerekmez.

**Neden küçük bir sunucu gerekiyor:** Tarayıcı, güvenlik nedeniyle diske dosya **yazamaz** —
bu bir ayar değil, tarayıcının temel kuralı. Bu yüzden sonucu alıp `.md` dosyasına yazan
minik bir yerel süreç gerekiyor. Bağımlılığı yok, sadece Node:

```bash
node assets/skor-sunucu.js
```

Sonra testleri ister çift tıklayarak aç, ister tarayıcıda `http://localhost:8899/...` adresinden.
İkisi de çalışır.

| Durum | Ne olur |
|---|---|
| Sunucu **açık** | Test biter → sonuç anında `.md`'ye yazılır, sonuç ekranında "yazıldı" notu çıkar |
| Sunucu **kapalı** | Sonuç tarayıcıda **kuyruğa alınır**; sunucuyu açıp herhangi bir testi yenilediğinde bekleyenlerin hepsi yazılır. **Hiçbir skor kaybolmaz** |

**Hep açık olsun istersen (macOS, isteğe bağlı):**

```bash
sed "s|KURS_KOKU|$PWD|g" assets/skor-sunucu.plist > ~/Library/LaunchAgents/local.kafkaadv.skor.plist
launchctl load ~/Library/LaunchAgents/local.kafkaadv.skor.plist
```

Böylece bilgisayar açıldığında sunucu da açılır ve bir daha hiç düşünmezsin.
Kaldırmak için `launchctl unload` + dosyayı sil.

> **Tablolar senin dosyalarında durur.** Elle satır ekleyebilir, not düşebilirsin;
> sunucu yalnızca `<!-- skor:baslangic -->` … `<!-- skor:bitis -->` arasına **yeni satır ekler**,
> var olanları silmez.

```
KafkaAdvance/
├── README.md                    ← buradasın
├── soru-cevap.md                ← çalışırken sorduğun sorular + ayrıntılı cevapları
├── assets/                      ← quiz motoru + skor sunucusu (dokunma)
│   └── skor-sunucu.js           ← test sonucunu .md dosyalarına yazan yerel sunucu
├── lab/                         ← tek Maven projesi: tüm Java örnekleri burada
│   ├── pom.xml
│   ├── docker/docker-compose.yml        ← 3 broker'lı KRaft cluster (ana lab)
│   ├── docker/docker-compose-mm2.yml    ← 7.2 için DR cluster + MirrorMaker 2
│   ├── docker/mm2.properties            ← MM2 replikasyon yapılandırması
│   ├── connect/*.properties             ← 07 Connect labı yapılandırmaları
│   ├── src/main/java/tr/improsy/kafkalab/   ← konu kodlarına göre paketler (k11, k21, …)
│   └── src/test/java/…/SiparisTopolojisiTest.java   ← 6.4 Streams testleri
├── 00-baslangic/                ← genel bakış, ortam kurulumu
│   ├── 02-kavram-sozlugu.md     ← terim/KIP/ayar sözlüğü (tıklanabilir çapalar)
│   └── 03-ayar-rehberi.md       ← ayar yönetimi: ne yapar, ne zaman, nasıl düşünülür
├── 01-… / 07-connect/           ← konu dosyaları + testleri
│   └── cevaplar.md              ← o bölümün "Kendini kontrol" cevapları (her klasörde)
└── 99-final/                    ← cheatsheet, son tekrar, 2 genel sınav
```

---

## Sorun çıkarsa

| Belirti | Çözüm |
|---|---|
| Broker başlamıyor, log'da `No space left on device` | `docker system df` → `docker builder prune -af`; gerekirse `docker compose -f lab/docker/docker-compose.yml down -v` |
| `The log dir ... is already offline` | Cluster'ı **sıfırla**: `down -v` sonra `up -d` (lab verisi gider, sorun değil) |
| `Connection to node N ... could not be established` | Yanlış listener: container içinden `kafka-1:19092`, host'tan `localhost:29092` ([00.2](00-baslangic/01-ortam-kurulumu.md)) |
| `Topic 'x' not present in metadata` | `auto.create.topics.enable=false`; topic'i önce oluştur |
| Lab çalışıyor ama topic'ler birikti | `docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-1:19092 --list \| grep lab-` ile bak, sil |

---

## Zamanla değişebilecek bilgiler

Bu set **Ağustos 2026** itibarıyla Apache Kafka **4.3.1**'e göre yazıldı. Şunlar sürümle değişir,
üretime almadan önce kendi sürümünün resmî dokümanından teyit et:

- **Varsayılan config değerleri** (özellikle 4.0'da KIP-1030 ile değişenler: `linger.ms=5` gibi)
- **Share groups** (KIP-932) — 4.2'de üretime hazır ilan edildi, API'si hâlâ gelişiyor
- **Streams rebalance protokolü** (KIP-1071) — 4.2'de sınırlı özellikle GA
- **Deprecation'lar** — `streams-scala` ve klasik rebalance protokolü 5.0 hedefli uyarı alıyor

Resmî kaynaklar: <https://kafka.apache.org/documentation/> · <https://kafka.apache.org/blog/releases/>
