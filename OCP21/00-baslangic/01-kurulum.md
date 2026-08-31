# 01 — Ortam Kurulumu ve Çalışma Projesi

> **Süre:** ~20 dakika
> **💸 Maliyet:** Yok. Bu dosyadaki hiçbir adım ücret doğurmaz.
> **Test:** Yok — kurulum dosyası. Kontrol listesini tamamla, geç.

---

## Neden bu konu

Bu kursun her konusunda `ornekler/` klasöründe **çalıştırılabilir kod** var. Sınavda kaybettiren şey,
kafandaki Java ile gerçek Java arasındaki farktır ve bu farkı ancak kodu **çalıştırarak** görürsün.
"Herhalde şunu yazar" demek, sınavda da aynı şekilde yanılman demektir.

**Büyük fikir:** Kurulum tek amaç için var — bir `.java` dosyasını **tek komutla** çalıştırabilmek.
IDE, build tool, proje yapısı gerekmiyor. Java 11'den beri `java Dosya.java` doğrudan çalışır.

---

## 1. ⭐ JDK 21 doğrulaması

Sınav **Java SE 21** üzerinden sorulur. Daha yeni bir JDK ile çalışırsan Java 22/23/24 davranışlarını
öğrenirsin ve sınavda yanlış cevap verirsin.

```bash
java -version
```

Çıktıda **`21.x`** görmelisin. Görmüyorsan:

```bash
/usr/libexec/java_home -V
```

Sistemindeki JDK'ları listeler. 21 sürümlü bir satır varsa onun yolunu al ve terminalinde ayarla:

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

Bunu kalıcı yapmak için `~/.zshrc` dosyasının sonuna ekle.

> ⚠️ **Tuzak:** `mvn -version` çıktısındaki Java sürümü, `java -version` çıktısından **farklı olabilir**.
> Maven kendi `JAVA_HOME`'unu kullanır. Bu kurs Maven gerektirmiyor — ama kullanacaksan ikisinin de 21
> olduğundan emin ol.

| Komut | Ne yapar | Ne zaman |
|---|---|---|
| `java Dosya.java` | Tek dosyayı derlemeden çalıştırır | Bu kursun **varsayılan** yöntemi |
| `javac Dosya.java && java Dosya` | Ayrı derleme + çalıştırma | **Derleme hatası** görmek istediğinde |
| `jshell` | Etkileşimli Java kabuğu | Tek satırlık denemeler için |
| `javap -c Sinif.class` | Bytecode'a bakar | "Derleyici ne yaptı?" merakında |

---

## 2. Çalışma klasörü ve ilk doğrulama

Kursun kökünde bir deneme dosyası oluştur ve çalıştır:

```bash
cd ~/IdeaProjects/impROS/OCP21
mkdir -p 00-baslangic/ornekler
cat > 00-baslangic/ornekler/Kontrol.java <<'JAVA'
public class Kontrol {
    sealed interface Sekil permits Daire, Kare {}
    record Daire(double r) implements Sekil {}
    record Kare(double kenar) implements Sekil {}

    static String anlat(Sekil s) {
        return switch (s) {
            case Daire d when d.r() > 10 -> "büyük daire";
            case Daire d                 -> "daire, r=" + d.r();
            case Kare(double k)          -> "kare, kenar=" + k;
        };
    }

    public static void main(String[] args) {
        System.out.println("Java sürümü: " + Runtime.version().feature());
        System.out.println(anlat(new Daire(3)));
        System.out.println(anlat(new Daire(42)));
        System.out.println(anlat(new Kare(5)));
    }
}
JAVA
java 00-baslangic/ornekler/Kontrol.java
```

**Beklenen çıktı:**
```
Java sürümü: 21
daire, r=3.0
büyük daire
kare, kenar=5.0
```

- [ ] **Kontrol:** Yukarıdaki üç satırı aynen gördün mü?
- [ ] **Kaydet:** `Runtime.version().feature()` çıktısı: ______

Bu tek dosya, sınavın **yeni** tarafının üçünü birden kullanıyor:
`sealed` arayüz (3.5), `record` (3.3), pattern matching for `switch` + `when` guard + record pattern (2.1).
Çalıştıysa ortamın hazır.

> ⚠️ **Hata alırsan:** `illegal reference to restricted type` veya `preview feature` mesajı görüyorsan
> JDK sürümün 21'in altında demektir. 1. adıma dön.

---

## 3. Derleme hatasını görmeyi öğren

Sınav soruları büyük ölçüde **derleme hatası** üzerine kurulu. Derleyicinin mesajını okumayı alışkanlık edin.

```bash
cat > /tmp/Hata.java <<'JAVA'
public class Hata {
    public static void main(String[] args) {
        byte b = 10;
        b = b + 300;        // bu satır derlenmez
        System.out.println(b);
    }
}
JAVA
javac -d /tmp/out /tmp/Hata.java
```

**Beklenen çıktı:**
```
/tmp/Hata.java:4: error: incompatible types: possible lossy conversion from int to byte
```

Şimdi satırı `b += 300;` yap ve tekrar derle — **derlenir** ve `54` yazdırır.
Sebebi compound assignment'ın gizli cast'i (1.1'de detaylı işlenecek). Bu, sınavın en sevdiği tuzaklardan biri.

- [ ] **Kontrol:** İlk halde hata mesajını, ikinci halde `54` çıktısını gördün mü?

### 💸 Temizlik
```bash
rm -rf /tmp/Hata.java /tmp/out
```
- [ ] **Kontrol:** `ls /tmp/Hata.java` → `No such file or directory`

---

## 4. jshell — hızlı deneme kabuğu

Tek satırlık "acaba?" soruları için IDE açma. `jshell` yaz, dene, `/exit` ile çık.

```
jshell> Integer a = 127, b = 127; a == b
$1 ==> true

jshell> Integer c = 128, d = 128; c == d
$2 ==> false

jshell> 'a' + 1
$3 ==> 98

jshell> 5 / 2
$4 ==> 2
```

> ⚠️ **Tuzak:** `jshell` bazı kuralları gevşetir (örneğin `main` metodu istemez, checked exception'ları
> otomatik sarar). **Derlenip derlenmediğini jshell ile test etme** — onun için `javac` kullan.

| Amaç | Araç |
|---|---|
| "Bu ifade ne değer üretir?" | `jshell` |
| "Bu kod derleniyor mu?" | `javac` |
| "Program ne yazdırır?" | `java Dosya.java` |

---

## 5. IDE kullanacaksan

IntelliJ IDEA kullanıyorsan `OCP21` klasörünü aç ve **Project SDK'yı 21'e** ayarla
(`File → Project Structure → Project SDK`). Ama sınava çalışırken IDE'nin iki zararı var:

| IDE yardımı | Sınavda karşılığı |
|---|---|
| Kırmızı altı çizgiyle hatayı gösterir | Sınavda hata **gösterilmez**, sen bulacaksın |
| `import`'ları otomatik ekler | Sınavda eksik import bir cevap şıkkıdır |
| Otomatik tamamlama API adını hatırlatır | Sınavda API adını **ezbere** bileceksin |

**Öneri:** Konu dosyalarını okurken IDE, örnekleri çalıştırırken **terminal** kullan.
Kod tahminlerini yazmadan önce IDE'nin uyarılarına bakma.

---

## Sık karıştırılanlar

| Durum | Doğrusu | Neden diğeri değil |
|---|---|---|
| `java Dosya.java` vs `javac` | Tek dosya çalıştırmada `java Dosya.java` yeter | `javac` `.class` üretir; derleme hatası görmek istemiyorsan gereksiz adım |
| `java -version` vs `mvn -version` | İkisi farklı JDK gösterebilir | Maven kendi `JAVA_HOME`'unu kullanır |
| jshell'de derlendi = derlenir | **Hayır** | jshell kuralları gevşetir; checked exception ve `main` zorunluluğu yoktur |
| JDK 21 vs "en yeni JDK" | Sınav **21** | 22+ davranışları farklı; yanlış öğrenirsin |

---

## 60 saniyelik özet

- `java -version` → **21** olmalı. Değilse `JAVA_HOME` ayarla.
- Örnekleri çalıştırma yöntemi: `java Dosya.java` (tek komut, derleme yok).
- Derleme hatası görmek istiyorsan: `javac Dosya.java`.
- Hızlı ifade denemesi: `jshell`. Ama **derlenirlik testi için kullanma**.
- IDE hatayı senin yerine buluyor — sınavda bulmayacak. Terminalde çalış.
- `Kontrol.java` çalışıyorsa sealed + record + pattern matching desteğin var demektir.

---

## Kendini kontrol

1. `java -version` 23 gösteriyorsa ne yaparsın, hangi iki satırı yazarsın?
2. Bir kodun derlenip derlenmediğini test etmek için hangi komutu kullanırsın, neden jshell olmaz?
3. `byte b = 10; b = b + 300;` neden derlenmez ama `b += 300;` neden derlenir?
4. IDE'nin sınava hazırlıkta üç zararı ne?

---

## Sırada ne var
➡️ [`02-kayit-ve-satin-alma.md`](02-kayit-ve-satin-alma.md) — sınav kaydı, ücret ve sınav günü kuralları.
