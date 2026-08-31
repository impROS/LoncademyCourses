# 01 — Lab Ortamı Kurulumu

> **Süre:** ~45 dakika
> **💸 Maliyet:** **Yok.** Bu dosyadaki her araç ücretsiz katmanda kullanılır. Ücret riski olan yerler `💸` ile işaretli.
> **Test:** Yok — bu bir kurulum dosyası.

---

## Neden bu dosya

ISTQB **teorik** bir sınav; kod yazmadan da geçilir. Ama iki teknik konu (beyaz kutu kapsam ölçümü ve
kusur yönetimi) elinle bir kez yaptığında **kalıcı** oluyor, okuduğunda olmuyor. Bu yüzden iki lab ortamı
kuracağız:

| Ortam | Ne için | Hangi konularda kullanılır |
|---|---|---|
| **Java + JUnit + JaCoCo** | Statement/branch coverage'ı **gerçekten ölçmek** | 4.3 Beyaz kutu |
| **Jira + Xray (ücretsiz plan)** | Kusur raporu, test case, izlenebilirlik | 1.4, 5.3, 5.4–5.5 |

Sadece okuyup sınava girmek istiyorsan bu dosyayı atlayabilirsin — her konu dosyasında pratiğin
"kalem kâğıt" alternatifi de var.

---

## 1. Java + JUnit + JaCoCo (zorunlu değil ama şiddetle önerilir)

### Ne gerekiyor

| | |
|---|---|
| JDK | **17 veya üstü** (21 önerilir) |
| Derleme aracı | Maven 3.9+ |
| Kapsam aracı | JaCoCo (Maven plugin, ayrı kurulum yok) |

### Adımlar

1. Terminali aç, kurulu mu bak:

```bash
java -version && mvn -version
```

   Çıktıda `openjdk version "21..."` benzeri bir satır ve `Apache Maven 3.9...` görmelisin.
   Yoksa: macOS'ta `brew install openjdk@21 maven`, Windows'ta [adoptium.net](https://adoptium.net) + Maven.

2. Lab projesini oluştur:

```bash
mkdir -p ~/istqb-lab/src/{main,test}/java/istqb && cd ~/istqb-lab
```

3. `pom.xml` dosyasını oluştur:

```bash
cat > pom.xml <<'POM'
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>istqb</groupId><artifactId>lab</artifactId><version>1.0</version>
  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId><artifactId>junit-jupiter</artifactId>
      <version>5.10.2</version><scope>test</scope>
    </dependency>
  </dependencies>
  <build><plugins>
    <plugin>
      <groupId>org.jacoco</groupId><artifactId>jacoco-maven-plugin</artifactId><version>0.8.12</version>
      <executions>
        <execution><goals><goal>prepare-agent</goal></goals></execution>
        <execution><id>report</id><phase>test</phase><goals><goal>report</goal></goals></execution>
      </executions>
    </plugin>
  </plugins></build>
</project>
POM
```

4. Test edilecek sınıfı yaz (4.3'te bunun üzerinde çalışacaksın):

```bash
cat > src/main/java/istqb/Discount.java <<'JAVA'
package istqb;

public class Discount {
    public int calculate(int age, boolean member) {
        int rate = 0;
        if (age < 18) {
            rate = 20;
        }
        if (member) {
            rate = rate + 10;
        }
        return rate;
    }
}
JAVA
```

5. Boş bir test sınıfı koy:

```bash
cat > src/test/java/istqb/DiscountTest.java <<'JAVA'
package istqb;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountTest {
    @Test
    void child_not_member() {
        assertEquals(20, new Discount().calculate(10, false));
    }
}
JAVA
```

6. Çalıştır ve kapsam raporu üret:

```bash
mvn -q test && open target/site/jacoco/index.html
```

   (Windows/Linux'ta `open` yerine dosyayı elle aç: `target/site/jacoco/index.html`)

- [ ] **Kontrol:** Tarayıcıda bir tablo açılmalı. `Discount` sınıfına tıkladığında kodun satırları
      **yeşil / sarı / kırmızı** boyanmış görünmeli. Yeşil = çalıştı, kırmızı = hiç çalışmadı,
      **sarı = dallanma kısmen kapsandı** (bu sarı, 4.3'ün tam konusu).
- [ ] **Kaydet:** Tek testle çıkan `Missed Branches` sayısı: ______ (4.3'te bunu 0'a indireceksin)

### 💸 Temizlik

Bu lab **tamamen yerel**, hiçbir ücret doğurmaz. Bitince silmek istersen:

```bash
rm -rf ~/istqb-lab
```

- [ ] **Kontrol:** `ls ~/` çıktısında `ISTQB-lab` görünmüyor.

---

## 2. Jira + Xray (kusur ve test yönetimi labı)

> **💸 Maliyet uyarısı:** Jira Cloud'un **Free** planı 10 kullanıcıya kadar ücretsizdir.
> Xray ise Atlassian Marketplace'ten kurulur ve **30 günlük ücretsiz deneme** ile gelir.
> ⚠️ Deneme bitince ücretlendirme başlayabilir — **bu labı bitirir bitirmez uygulamayı kaldır.**
> Fiyatlandırma ve deneme süresi değişebilir; **kurulumdan önce Marketplace sayfasındaki fiyatı oku.**

Riski hiç almak istemiyorsan: bu bölümü atla, kusur raporu pratiğini **düz metin dosyasında** yap
(5.4 dosyasında alternatif adımlar var). Sınav açısından kayıp yok.

### Adımlar

1. <https://www.atlassian.com/software/jira/free> adresine git, ücretsiz hesap aç.
   Site adı olarak `istqb-lab-<adın>` gibi bir şey ver.
2. **Create project** → şablon olarak **Scrum** seç → proje adı `ISTQB Lab`, anahtar `ISTQB`.
3. Sol menüden **Apps → Explore more apps** → arama kutusuna `Xray Test Management` yaz.
4. **Try it free** butonuna bas. 💸 Bu noktada 30 günlük deneme başlar. Kurulum 2-3 dakika sürer.
5. Projeye dön, **Create** butonunda artık `Test`, `Test Set`, `Test Execution`, `Test Plan`
   tipleri görünmeli.

- [ ] **Kontrol:** `Create` menüsünde issue type listesinde **Test** görünüyor.
- [ ] **Kaydet:** Proje anahtarın: ______ (konu dosyalarındaki adımlarda kullanacaksın)

### 💸 Temizlik — labı bitirir bitirmez ZORUNLU

Xray denemesi 30 günün sonunda otomatik ücretlendirmeye geçebilir. Bekleme, hemen kaldır:

1. Sol alt **⚙ Settings → Apps → Manage apps**
2. Listede **Xray Test Management** satırını bul → sağdaki `⋯` → **Uninstall**
3. Onay kutusunda **Uninstall** de.
4. Jira sitesinin tamamını da silmek istersen: **⚙ Settings → System → ... → Delete site**
   ⚠️ Bu geri alınamaz. Sadece bu lab için açtığın siteyi sil, başka bir işte kullandığın siteyi **asla**.

- [ ] **Kontrol:** **Manage apps** listesinde Xray görünmüyor.
- [ ] **Kontrol:** **Billing** sayfasında aktif ücretli abonelik yok.

> ⚠️ **Basmaman gereken buton:** Xray deneme ekranındaki **"Buy now" / "Subscribe"**. Deneme bittiğinde
> gelen "Continue subscription" e-postasındaki bağlantıya da tıklama.

---

## 3. Sözlük ve müfredat (ücretsiz, mutlaka indir)

1. **Resmî müfredat PDF'i:** <https://www.istqb.org/certifications/certified-tester-foundation-level-ctfl-v4-0/>
   sayfasından syllabus v4.0.1'i indir. Bu setin kaynağı odur; anlamadığın bir terimde oraya bakabilirsin.
2. **ISTQB Glossary:** <https://glossary.istqb.org> — çevrimiçi, ücretsiz. Sınavda sorulan terimlerin
   resmî tanımı burada.

> ❌ Bu iki kaynağı **para karşılığı satan** siteler var. İkisi de ücretsiz. Ödeme sayfasına yönlendiren
> bir siteye girdiysen kapat.

---

## 60 saniyelik özet

- Java labı **tamamen yerel ve ücretsiz** — JaCoCo raporundaki **sarı satır** = kısmen kapsanan dallanma.
- Jira/Xray labı **💸 30 gün deneme** — bitirince **Manage apps → Uninstall**, unutma.
- Müfredat PDF'i ve Glossary **ücretsiz**; para isteyen siteye girme.
- Lab'lar sınavda çıkmaz; 4.3 ve 5.4'ü **kalıcı** hale getirir.

---

## Sırada ne var

➡️ [`02-kayit-ve-satin-alma.md`](02-kayit-ve-satin-alma.md)
