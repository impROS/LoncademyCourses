# 00.2 — Ortam kurulumu ve örnek proje

> **Alan:** Başlangıç — kurulum (konu değil, hazırlık)
> **Süre:** ~30 dakika
> **Test:** yok — bu dosyanın testi, sonundaki kontrol listesinin geçmesidir
> 📖 [Kavram sözlüğü](03-kavram-sozlugu.md) · ⚙️ [Seçim ve ayar rehberi](04-ayar-rehberi.md)

---

## Neden bu konu

Setin geri kalanındaki her pratik **aynı örnek proje** üzerinde çalışır. Bu
dosyayı bitirdiğinde elinde çalışan bir ajan ve üzerinde deneme yapabileceğin
küçük, tarafsız bir kod tabanı olacak.

Örnek projeyi kendi işinden ayrı tutuyoruz. Sebebi basit: bu sette hook
yazacaksın, izin ayarlarıyla oynayacaksın, ajanı geri alınması gereken şeyler
yapmaya iteceksin. Bunları gerçek işinin üstünde denemek, öğrenmeye kaygı
katar — ve kaygı en kötü öğretmendir.

---

## 1. Ne gerekiyor

| Gereken | Neden | Kontrol |
|---|---|---|
| **Node.js 20+** | Hem örnek proje hem setin skor sunucusu için | `node --version` |
| **git** | `03` bölümünün tamamı git üstünde | `git --version` |
| **Bir ajan aracı** | Setin omurgası Claude Code | aşağıda |
| **Terminal** | Ajanların çoğu terminalde yaşıyor | zaten var |

İsteğe bağlı ama işi kolaylaştırır:

- **`gh`** (GitHub komut satırı aracı) — **201 · *Git ve pull request*** konusundaki pull request pratiği için.
  Ajanlar `gh`'yi bilir; kurulu değilse aynı işi yapmak için yetkisiz istek
  atarlar ve hız sınırına takılırlar.
- **`jq`** — **301 · *Hook*** ve **301 · *Otomasyon ve SDK*** konusunda hook ve betik çıktısı ayrıştırmak için.

---

## 2. Ajanı kur

Bu set Claude Code üzerinden anlatıyor. Başka bir araç kullanıyorsan `05`
bölümüne bakıp karşılığını bul — kavramların hepsi taşınır, komutlar değişir.

### Adımlar

1. Kurulum yönergesini **resmî kaynaktan** al:
   <https://code.claude.com/docs/en/quickstart>
   ⚠️ Kurulum komutunu buraya yazmıyorum bilerek: paket adları ve kurulum yolu
   değişiyor, buraya yazılan komut altı ay sonra yanlış olur.
2. Kurulduğunu doğrula:
   ```bash
   claude --version
   ```
3. Oturum aç ve ilk oturumu başlat:
   ```bash
   claude
   ```
4. İçerideyken sağlık denetimini çalıştır:
   ```text
   /doctor
   ```

- [ ] **Kontrol:** `claude --version` bir sürüm numarası yazdırıyor.
- [ ] **Kontrol:** `/doctor` kırmızı bir sorun bildirmiyor.
- [ ] **Kaydet:** Sürüm numaran: ______ (bir sorun yaşarsan bu lazım olacak)

> 💸 **Maliyet:** Kurulumun kendisi ücretsiz. Kullanım, planına göre kotandan
> ya da kullandıkça ödemeden düşer. Hangi planın sana uygun olduğu
> [`05-plan-ve-maliyet.md`](05-plan-ve-maliyet.md) içinde — **oraya bakmadan
> üst plana geçme.**

---

## 3. 🖥 Pratik — örnek projeyi kur

> **Amaç:** Setin tamamında kullanılacak `gorev-api` projesini oluşturmak
> **Süre:** 15 dk · **💸 Maliyet:** Yok

Bu, kasten küçük ve sıkıcı bir proje: birkaç uç noktası olan bir görev listesi
servisi. Küçük olması önemli — ajanın davranışını gözlemleyeceksin, projeyi değil.

### Adımlar

1. Projeyi oluştur ve git deposu yap:
   ```bash
   mkdir -p ~/gorev-api && cd ~/gorev-api
   git init
   npm init -y
   ```

2. `package.json` içindeki `scripts` bölümünü şununla değiştir:
   ```json
   "scripts": {
     "start": "node sunucu.js",
     "test": "node --test"
   }
   ```

3. `sunucu.js` dosyasını oluştur — bağımlılık yok, yalnızca Node'un kendisi:
   ```js
   const http = require('node:http');

   /** Bellekte duran görev listesi. Kalıcılık yok: bu bir öğrenme projesi. */
   const gorevler = [{ id: 1, baslik: 'ilk görev', bitti: false }];
   let sonrakiId = 2;

   const sunucu = http.createServer((istek, cevap) => {
     cevap.setHeader('Content-Type', 'application/json; charset=utf-8');

     if (istek.method === 'GET' && istek.url === '/gorevler') {
       return cevap.end(JSON.stringify(gorevler));
     }

     if (istek.method === 'POST' && istek.url === '/gorevler') {
       let govde = '';
       istek.on('data', (p) => { govde += p; });
       istek.on('end', () => {
         const { baslik } = JSON.parse(govde);
         const gorev = { id: sonrakiId++, baslik, bitti: false };
         gorevler.push(gorev);
         cevap.statusCode = 201;
         cevap.end(JSON.stringify(gorev));
       });
       return;
     }

     cevap.statusCode = 404;
     cevap.end(JSON.stringify({ hata: 'bulunamadı' }));
   });

   if (require.main === module) sunucu.listen(3000, () => console.log('3000'));
   module.exports = { sunucu, gorevler };
   ```

4. Bir de test dosyası — `sunucu.test.js`:
   ```js
   const test = require('node:test');
   const assert = require('node:assert');
   const { gorevler } = require('./sunucu.js');

   test('başlangıçta bir görev var', () => {
     assert.strictEqual(gorevler.length, 1);
   });
   ```

5. Çalıştığını doğrula:
   ```bash
   npm test
   ```

6. İlk commit'i at:
   ```bash
   git add -A && git commit -m "gorev-api iskeleti"
   ```

- [ ] **Kontrol:** `npm test` "1 passed" benzeri bir çıktı veriyor.
- [ ] **Kontrol:** `git log --oneline` bir commit gösteriyor.

> ⚠️ **Bu kodda bilerek bırakılmış eksikler var:** girdi doğrulaması yok, hata
> yakalama yok, bozuk gövde gelince çöküyor. `03` bölümünde bunları ajanla
> düzelteceksin. Şimdi düzeltme.

---

## 4. İlk oturum — neyin çalıştığını gör

Proje klasöründeyken ajanı başlat:

```bash
cd ~/gorev-api && claude
```

Sırayla şunları dene ve **ne olduğuna dikkat et**:

| Ne yazacaksın | Ne göreceksin | Neden önemli |
|---|---|---|
| `bu proje ne yapıyor?` | Dosyaları okuyup özetler | Ajan **okuyabiliyor** |
| `/context` | Bağlamın neyle dolu olduğu | `1.3`'ün konusu, şimdi bak yeter |
| `npm test çalıştır ve sonucu göster` | Komut çalıştırma izni ister | İzin sistemi (`1.4`) |
| `/cost` ya da `/usage` | Bu oturumun sayıları | **401 · *Maliyet*** konusunun konusu |

- [ ] **Kontrol:** Ajan dosyaları okuyup projeyi doğru tarif etti.
- [ ] **Kaydet:** `/context` ekranında henüz hiçbir şey yapmadan kaç token
      dolu görünüyor? ______ (bu sayı `1.3`'te anlam kazanacak)

---

## 5. Kurulumu bitirdiğini nasıl anlarsın

Aşağıdakilerin hepsi doğruysa `01` bölümüne geçebilirsin:

- [ ] `claude --version` çalışıyor, `/doctor` temiz
- [ ] `~/gorev-api` var, `npm test` geçiyor, bir git commit'i var
- [ ] Ajan proje klasöründe başlatıldığında dosyaları okuyabiliyor
- [ ] `/context` ve `/usage` ekranlarını **görmüş** oldun (anlamak şart değil)
- [ ] Bu setin skor sunucusunu bir kez başlattın:
      `SKOR_PORT=8897 node assets/skor-sunucu.js`

> ⚠️ Skor sunucusunu unutma. Testleri çözerken kapalıysa sonuçlar kaybolmaz —
> kuyruğa alınır — ama tabloyu boş görmek moral bozar.

---

## Sık karıştırılanlar

| Karıştırılan | Doğrusu | Neden diğeri değil |
|---|---|---|
| "Ajanı kurmak = kullanıma hazır" | Kurulum kolay kısım; **kurulumun ayarı** (`00.4`) işin yarısı | Varsayılan ayarlar güvenli tarafta durur, verimli tarafta değil |
| "Örnek proje küçük, gerçekçi değil" | Kasten küçük: **ajanın davranışını** gözlemliyorsun | Büyük projede ajanın ne yaptığını göremezsin, proje karmaşası öne çıkar |
| "Kurulum komutunu ezberlemeliyim" | Komut değişir; **resmî kaynağa gitme alışkanlığı** kalıcıdır | Buraya yazılan komut altı ay sonra yanlış olur |

---

## 60 saniyelik özet

- Gerekenler: Node 20+, git, bir ajan aracı, terminal. İsteğe bağlı: `gh`, `jq`.
- Kurulum komutunu **resmî kaynaktan** al; buraya yazılanlar eskir.
- `claude --version` ve `/doctor` kurulumun doğrulaması.
- Örnek proje `~/gorev-api`: bağımlılıksız, iki uç noktalı, **bilerek eksikli**.
- Eksikleri şimdi düzeltme — `03` bölümünün malzemesi onlar.
- Skor sunucusu bu sette **8897** portunda.

---

## Sırada ne var
➡️ [`03-kavram-sozlugu.md`](03-kavram-sozlugu.md) ve
[`04-ayar-rehberi.md`](04-ayar-rehberi.md) başvuru dosyaları — okumak zorunda
değilsin, konular içinde yönlendirileceksin.
Doğrudan devam: [`05-plan-ve-maliyet.md`](05-plan-ve-maliyet.md)
