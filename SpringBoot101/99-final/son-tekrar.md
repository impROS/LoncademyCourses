# Son tekrar — bitirmeden önce

> Bu dosyayı genel sınavlardan **önce**, bir oturuşta oku. Amacı yeni bir şey öğretmek
> değil; öğrendiklerini **çağırma refleksi** kazandırmak. 20 dakika sürer.

---

## 1. Hazırlık kontrol listesi

Aşağıdakilerin hepsine "evet" diyemiyorsan, sınava girme — ilgili konuya dön.

**Container ve bean'ler**
- [ ] Elimdeki bir nesne için "bunu container mı kurdu, ben mi?" sorusunu cevaplayabiliyorum.
- [ ] Bir sınıfı bean yapmanın iki yolunu ve hangisinin ne zaman kullanılacağını biliyorum.
- [ ] "Bulunamadı" ile "birden fazla bulundu" hatalarının çözümlerinin **zıt** olduğunu biliyorum.
- [ ] Constructor'da neden iş yapılmayacağını anlatabiliyorum.
- [ ] Singleton bir bean'e neden durum yazılmayacağını bir senaryoyla anlatabiliyorum.

**Anotasyonlar**
- [ ] `@Service` ile `@Repository` arasındaki **teknik** farkı söyleyebiliyorum.
- [ ] `@Configuration`'ın neden alt sınıfının üretildiğini ve bunun sonucunu biliyorum.
- [ ] Bir bean'in nereden geldiğini `--debug` ile bulabiliyorum.
- [ ] Spring Boot 4'te değişen starter adlarını sayabiliyorum.

**Yapılandırma**
- [ ] Öncelik sırasını yukarıdan aşağı sayabiliyorum.
- [ ] Bir ayarı ortam değişkeni olarak doğru yazabiliyorum (tire kuralı!).
- [ ] `@Validated` olmadan kısıtların çalışmadığını biliyorum.
- [ ] Profil dosyasının **bindiğini**, yerine geçmediğini biliyorum.

**Web ve test**
- [ ] 400 / 404 / 405 / 415'i birbirinden ayırabiliyorum.
- [ ] `ProblemDetail`'de `type` ile `detail` farkını biliyorum.
- [ ] `RestClient`'in varsayılan timeout'u olmadığını biliyorum.
- [ ] Slice test ile full context testinin **farklı şeyler ölçtüğünü** anlatabiliyorum.
- [ ] `@Mock` ile `@MockitoBean` farkını biliyorum.

---

## 2. Refleks turu — soru, sonra cevap

Her soruyu okuyup **cevabını kendine söyle**, sonra satırın devamına bak.

| Gördüğün | Refleksin |
|---|---|
| `required a bean ... could not be found` | Anotasyon var mı? Paket component scan'e giriyor mu? |
| `required a single bean, but 2 were found` | `@Qualifier` (tüketende) ya da `@Primary` (üretende) |
| `form a cycle` | Ortak parçayı üçüncü sınıfa çıkar. **Ayarı açma.** |
| Uç nokta 404, sınıf bean | `@RestController` mı? Paket taranıyor mu? |
| Uç nokta 404, adres doğru, `@Controller` var | `@ResponseBody` eksik — `String` view name sanıldı |
| 405 | Adres doğru, metot yanlış — `Allow:` başlığına bak |
| 415 | İstemcinin `Content-Type` başlığı eksik |
| Ayar okunmuyor | Daha yüksek öncelikli kaynak eziyor → `/actuator/env/<anahtar>` |
| Docker'da ayar geçmiyor | Ortam değişkeni adı: nokta → `_`, tire → **silinir** |
| Profil uygulanmıyor | Açılış günlüğünün 2. satırı ne diyor? |
| Kısıt çalışmıyor | `@Validated` (ayarda) / `@Valid` (istekte) eksik |
| Test yavaş | Slice test yeter mi? İş mantığı Spring'den ayrılabilir mi? |
| Test yeşil ama şüpheliyim | Çalışan test **sayısına** bak. Sınıf adı `*Test` mi? |
| `cannot find symbol` bir Spring sınıfında | Boot 4'te paket taşınmış olabilir |
| Dış servis yavaşladı, biz de çöktük | Timeout yok |

---

## 3. Beş kritik ayrım

Sınavda en çok bu beşi karıştırırlar. Her birini **kendi cümlenle** yaz:

1. **`@Service` vs `@Repository`** — hangisinin teknik etkisi var, o etki ne?
2. **0 aday vs 2+ aday** — iki hata mesajı, iki zıt çözüm.
3. **Singleton vs prototype** — ve prototype'ı singleton içine koyunca ne olur?
4. **`@Configuration` vs `@Component` + `@Bean`** — ölçüm neydi?
5. **`@Mock` vs `@MockitoBean`** — hangisi container'a dokunur?

---

## 4. Sınav stratejisi

- **Sorunun ne sorduğunu bul.** Çoğu soru senaryo; asıl soru genelde son cümlede.
- **Mutlak ifadeli şıklara dikkat:** "her zaman", "asla", "yalnızca", "%100". Gerçek
  hayatta istisnası olan bir kural, mutlak yazıldığında genelde yanlış şıktır.
- **"Hata verir" mi, "sessizce çalışmaz" mı?** Bu kursta en çok ayrılan yer burası.
  [Cheatsheet'teki sessiz hatalar tablosunu](cheatsheet.md) hatırla.
- **Çoklu doğru sorularda kaç tane seçeceğin yazılıdır.** Sayıyı tutturmadan geçme.
- **Emin olmadığın soruyu işaretle, geç.** Sonraki soru bazen öncekini hatırlatır.
- Testler karıştırmalı; şık **harflerini** ezberleme, **fikri** ezberle.

---

## 5. Bu kursun bitiminde ne biliyorsun

Şunları yapabiliyor olmalısın:

- Boş bir Spring Boot 4 projesi kurmak ve neyin nereden geldiğini açıklamak.
- Bir bean'i iki farklı yolla tanımlamak ve doğru yolu seçmek.
- Dependency injection hatalarının üçünü de tanıyıp çözmek.
- Bir ayarın hangi kaynaktan geldiğini **kanıtlamak**.
- Ayarları bir nesneye bind edip açılışta doğrulatmak.
- Uçtan uca bir REST uç noktası yazmak ve hatalarını standarda uygun döndürmek.
- Dış bir servisi timeout ile çağırmak.
- Aynı uç nokta için hem slice hem full context testi yazmak ve farkı açıklamak.

---

## 6. Sırada ne var — 201

Bu kurs container'ı ve çerçeveyi öğretti. **201 Veri ve transaction** şunları açıyor:

- Hibernate'in persistence context'i ve dirty checking
- `@Transactional`'ın proxy mekaniği — ve **neden bazen hiç çalışmadığı**
  (1.4'te bıraktığımız ipucu burada tamamlanıyor)
- Propagation, isolation, locking
- N+1 sorgu sorunu
- Testcontainers ile gerçek veritabanına karşı test
- İleri birim test: mock'lamanın sınırı, mutation testing

**Giriş şartı:** Bu kursun genel sınavlarından **%80**.

---

## Son bir şey

Bu kursta en çok tekrarladığım cümle şuydu:

> **Bean olmak, var olmak değildir.**

İkinci en çok tekrarladığım da şu olsun:

> **Hata vermeyen yanlış, hata verenden pahalıdır.**

Cheatsheet'teki sessiz hatalar tablosunu ara ara aç. Bir gün birinin haftasını
kurtarırsın — büyük ihtimalle kendi haftanı.

---

➡️ **Genel sınav 1:** [`genel-sinav-1.html`](genel-sinav-1.html)
➡️ **Genel sınav 2:** [`genel-sinav-2.html`](genel-sinav-2.html)
➡️ **Cheatsheet:** [`cheatsheet.md`](cheatsheet.md)
