# 00.2 — Ortam kurulumu: 3 broker'lı KRaft cluster + Maven lab projesi

> **Alan:** Başlangıç — bir kez yapılır, 6 hafta kullanılır
> **Süre:** ~25 dakika
> **Test:** Yok (kurulum dosyası) — doğrulama adımları aşağıda

---

## Neden bu konu

Bu setteki her konunun bir lab'ı var ve labların hepsi **gerçek bir cluster** ister. Tek broker'lı bir
kurulum bu setin yarısını öğretemez: replikasyon, ISR ([lidere senkron replikalar kümesi ↗](02-kavram-sozlugu.md#isr)) daralması, lider seçimi, rebalance, ELR ([ISR dışı ama lider olmaya uygun replikalar ↗](02-kavram-sozlugu.md#elr)) —
hepsi **en az 3 broker** gerektirir. Bir kez düzgün kur, sonra hiç uğraşma.

> **Büyük fikir:** Bu cluster senin deney tahtan. Bozmaktan korkma — `down -v` ile 30 saniyede
> sıfırdan kurulur. Bu sette **broker öldürmen istenecek**; korkmadan yapabilmen için lokal.

---

## 1. Ön koşullar

| Araç | Sürüm | Doğrulama komutu | Not |
|---|---|---|---|
| Docker | 20.10+ (Compose v2) | `docker compose version` | Docker Desktop, colima ya da podman-compose |
| JDK | **21** önerilir (17+ zorunlu) | `java -version` | Kafka 4.x: client/Streams **11+**, broker/Connect **17+** |
| Maven | 3.9+ | `mvn -version` | IntelliJ'in gömülü Maven'ı da olur |

**Kaynak ihtiyacı:** ~3 GB RAM (3 broker), ~2 GB disk. Docker Desktop'a en az **4 GB** bellek ver.

> ⚠️ **Tuzak — disk:** Docker diski dolarsa broker **sessizce** başlamaz ya da çalışırken çöker;
> log'da `No space left on device` ya da
> `The log dir /var/lib/kafka/data is already offline due to a previous IO exception` görürsün.
> Bu setin labları toplamda birkaç GB veri üretir; **haftada bir kontrol et**:
> ```bash
> docker system df                 # ne kadar yer kullanılıyor
> docker builder prune -af         # build önbelleğini temizle (güvenli)
> docker compose -f lab/docker/docker-compose.yml down -v   # cluster'ı SIFIRLA (lab verisi gider)
> ```
> Broker log dizini bir kez "offline" olduysa tek çıkış yolu `down -v` ile sıfırlamaktır.

---

## 2. Cluster'ı ayağa kaldır

Proje kökünde (`KafkaAdvance/`):

```bash
docker compose -f lab/docker/docker-compose.yml up -d
```

İlk çalıştırmada `apache/kafka:4.3.1` imajını indirir (~450 MB). Ardından:

```bash
docker compose -f lab/docker/docker-compose.yml ps
```

Üç container da `Up` olmalı: `kafka-1`, `kafka-2`, `kafka-3`.

### Ne kurduk?

| Özellik | Değer | Neden böyle |
|---|---|---|
| Mod | KRaft ([Kafka’nın kendi Raft metadata katmanı ↗](02-kavram-sozlugu.md#kraft)) **combined** (broker+controller aynı süreçte) | Lab için yeterli; üretimde controller'ları ayırırsın (1.3) |
| Node sayısı | 3 | Quorum (karar için gereken salt çoğunluk) için tek sayı; 1 node kaybını tolere eder |
| `default.replication.factor` | 3 | Her topic varsayılan 3 kopya |
| `min.insync.replicas` ([acks=all için gereken asgari güncel kopya ↗](02-kavram-sozlugu.md#mininsyncreplicas)) | 2 | `acks=all` için en az 2 güncel kopya şartı |
| İç topic'ler (`__consumer_offsets` vb.) | replication.factor=3, min.insync.replicas=2 | Gerçekçi; tek kopya olsa replikasyon labları çalışmaz |
| `auto.create.topics.enable` | **false** | Yazım hatasıyla kazara topic oluşmasın — üretim ayarı da budur |
| `num.partitions` | 3 | Varsayılan |
| JMX ([JVM metrik arayüzü ↗](02-kavram-sozlugu.md#jmx)) | 9999 (host: 19991/29991/39991) | 5.2'de metrik okuyacağız |

---

## 3. ⭐ İki farklı bootstrap adresi — en sık takılınan yer

Cluster'da iki listener var ve **hangisini kullanacağın nereden bağlandığına bağlı**:

| Nereden bağlanıyorsun | Listener | Adres |
|---|---|---|
| **Host makinen** (Java kodu, IDE) | `HOST` | `localhost:29092,localhost:39092,localhost:49092` |
| **Container içi** (`docker exec` ile CLI) | `INTERNAL` | `kafka-1:19092` (veya kafka-2/kafka-3) |

**Neden?** Broker istemciye önce metadata döner; metadata'da `advertised.listeners` yazar.
Container içinden `localhost:9092`'ye bağlanırsan broker sana `localhost:29092` adresini söyler —
o adres container'ın içinde **yoktur**, ve istemci sonsuza kadar bağlanmayı dener:

```
WARN [AdminClient] Connection to node 1 (localhost/127.0.0.1:29092) could not be established.
```

> ⚠️ **Tuzak:** Bu hata "broker kapalı" gibi görünür ama broker çalışıyordur. **Bağlantı hatasında
> önce `advertised.listeners`'a bak** — Kafka kurulumlarındaki 1 numaralı sorun budur, Kubernetes'te de aynısı.

**Bu setin kuralı:** CLI komutları `--bootstrap-server kafka-1:19092`, Java kodu `localhost:29092,...`.

Kolaylık için host'ta bir alias tanımla (opsiyonel):

```bash
alias kcli='docker exec -i kafka-1 /opt/kafka/bin'
# kullanım:  $(echo kcli)/kafka-topics.sh --bootstrap-server kafka-1:19092 --list
```

---

## 4. Cluster'ı doğrula

```bash
docker exec kafka-1 /opt/kafka/bin/kafka-metadata-quorum.sh \
  --bootstrap-controller kafka-1:9093 describe --status
```

Beklenen (id'ler ve sayılar sende farklı olabilir):

```
ClusterId:              4L6g3nShT-eMCtK--X86sw
LeaderId:               2
LeaderEpoch:            1
HighWatermark:          56
MaxFollowerLag:         0
CurrentVoters:          [{"id": 1, ...}, {"id": 2, ...}, {"id": 3, ...}]
CurrentObservers:       []
```

- [ ] **Kontrol:** `CurrentVoters` **üç** node içeriyor, `MaxFollowerLag` küçük (0-2 civarı).

---

## 5. Maven lab projesini derle ve bağlan

```bash
cd lab
mvn -q compile
mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.common.Health
```

Beklenen çıktı:

```
clusterId  : 4L6g3nShT-eMCtK--X86sw
controller : localhost:49092 (id: 3 rack: null isFenced: false)
node       : 1 @ localhost:29092
node       : 2 @ localhost:39092
node       : 3 @ localhost:49092
topics     : []

OK — cluster erişilebilir.
```

- [ ] **Kontrol:** "OK — cluster erişilebilir." satırını gördün.
- [ ] **Kaydet:** Controller hangi node? ______

### Proje düzeni

```
lab/
├── pom.xml                       kafka-clients 4.3.1, kafka-streams 4.3.1, slf4j-simple, junit5
├── docker/docker-compose.yml     3 broker'lı KRaft cluster (ANA lab ortamı)
├── docker/docker-compose-mm2.yml 7.2 labı: DR cluster + MirrorMaker 2 (ayrı, opsiyonel)
├── docker/mm2.properties         MM2 replikasyon yapılandırması
├── connect/                      07 Connect labı için worker/connector yapılandırmaları
└── src/main/java/tr/improsy/kafkalab/
    ├── common/Lab.java           ortak yardımcılar: BOOTSTRAP, ensureTopic, deleteTopics, banner
    ├── common/Health.java        kurulum doğrulaması
    ├── k11/ k12/ k14/            01 Broker & Depolama (1.3 ve 1.5 CLI ile çalışılır)
    ├── k21/ … k24/               02 Producer örnekleri
    ├── k31/ … k34/               03 Consumer örnekleri
    ├── k41/ k42/                 04 Transaction & EOS
    ├── k61/ … k64/               06 Streams örnekleri
    └── k72/                      07 Connect (kendi yazdığımız SMT)

    05 Operasyon konularının lab'ları Java değil, CLI + JMX araçlarıyla yapılır.
```

**Örnek çalıştırma kalıbı** (bu sette hep aynı):

```bash
cd lab && mvn -q compile exec:java -Dexec.mainClass=tr.improsy.kafkalab.k11.SegmentDemo
```

> **İpucu:** IntelliJ'de `lab/pom.xml`'i açıp her `main` metodunu doğrudan çalıştırabilirsin;
> `exec:java` sadece terminalden kullananlar için.

---

## 6. Günlük kullanım komutları

| Ne yapmak istiyorsun | Komut |
|---|---|
| Başlat | `docker compose -f lab/docker/docker-compose.yml up -d` |
| Durdur (**veri kalır**) | `docker compose -f lab/docker/docker-compose.yml stop` |
| Kaldır (veri kalır) | `docker compose -f lab/docker/docker-compose.yml down` |
| **Sıfırla** (veri gider) | `docker compose -f lab/docker/docker-compose.yml down -v` |
| Tek broker'ı öldür (lab için) | `docker stop kafka-2` |
| Geri getir | `docker start kafka-2` |
| Log izle | `docker compose -f lab/docker/docker-compose.yml logs -f kafka-1` |
| **7.2 DR cluster'ı** başlat | `docker compose -f lab/docker/docker-compose-mm2.yml up -d` |
| **7.2 DR cluster'ı** kaldır | `docker compose -f lab/docker/docker-compose-mm2.yml down -v` |
| Topic listesi | `docker exec kafka-1 /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-1:19092 --list` |

> ⚠️ **Tuzak:** `down -v` **volume'ları siler** — cluster id dahil her şey gider ve topic'lerin
> yok olur. Sadece "temiz sayfa" istediğinde kullan. Ders arasında `stop` yeterli.

---

## 7. Sık karşılaşılan kurulum hataları

| Belirti | Sebep | Çözüm |
|---|---|---|
| `Connection to node N ... could not be established` | Yanlış listener (madde 3) | Container içinden `kafka-1:19092`, host'tan `localhost:29092` |
| Container sürekli restart, log'da `No space left on device` | Docker diski dolu | `docker system df`, `docker builder prune -af` |
| `Topic 'x' not present in metadata after 60000 ms` | `auto.create.topics.enable=false` ve topic yok | Topic'i önce oluştur (`Lab.ensureTopic(...)` bunu yapıyor) |
| `UnsupportedVersionException` | Client < 2.1 (KIP-896 — [eski protokol sürümlerinin kaldırılması ↗](02-kavram-sozlugu.md#kip-896)) | `kafka-clients` 4.x kullan |
| Port çakışması (29092 dolu) | Başka bir Kafka çalışıyor | `docker ps` ile bul, durdur; ya da compose'daki host portlarını değiştir |
| `mvn` "release version 21 not supported" | JDK 17 ya da altı | JDK 21 kur veya `pom.xml`'de `maven.compiler.release`'i 17 yap |
| SLF4J `NOP logger` uyarısı | slf4j-api/slf4j-simple sürüm uyuşmazlığı | `pom.xml`'de `slf4j-api` sabitlendi; `mvn -U clean compile` |

---

## 60 saniyelik özet

- 3 broker, KRaft combined mod, ZooKeeper yok. Cluster id sabit, replication.factor=3, min.insync.replicas=2.
- **Host'tan** `localhost:29092,39092,49092` · **container içinden** `kafka-1:19092`.
- Bağlantı hatasının 1 numaralı sebebi `advertised.listeners` — broker kapalı sanma.
- `mvn -q compile exec:java -Dexec.mainClass=...` bu setin standart çalıştırma kalıbı.
- `stop` = ara ver, `down -v` = sıfırla (veri gider).
- `auto.create.topics.enable=false` — topic'ler bilerek oluşturulur, kazara değil.

---

## Sırada ne var
➡️ [`02-kavram-sozlugu.md`](02-kavram-sozlugu.md) — **kavram sözlüğü.** Baştan sona okuman gerekmez;
bir kez göz gezdir, sonra konu dosyalarındaki **↗** bağlantılarıyla buraya dönersin.

➡️ Sonra: [`../01-broker-depolama/1.1-log-segment-ve-index.md`](../01-broker-depolama/1.1-log-segment-ve-index.md) —
Kafka diskte tam olarak ne yazıyor?
