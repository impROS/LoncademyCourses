# 01 — Yasal & Etik Çerçeve: Nerede Pratik Yapılır, Nerede Yapılmaz

> **Bu dosya:** RE'nin sınırlarını çizer. Test yok ama **en önemli dosyalardan biri.**
> **Süre:** ~15 dakika okuma

---

## Neden bu dosya

Reverse engineering yasal bir beceridir — ama neyi tersine çevirdiğin ve ne için yaptığın işi
yasal ya da yasadışı yapar. Aynı teknik, bir CTF binary'sinde masumdur; başkasının lisanslı
yazılımını kırarken suçtur. Bu ayrımı baştan öğren ki yeteneğini yanlışlıkla suça çevirme.

**Büyük fikir:** *Teknik nötrdür; niyet ve yetki onu yasal ya da yasadışı yapar.* RE öğrenirken
her zaman **sana pratik için açıkça sunulmuş** materyalle çalış.

> ⚠️ Bu dosya hukuki tavsiye değildir, bir yön haritasıdır. Ciddi bir durumda avukata danış.
> Yasalar ülkeye göre değişir ve zamanla güncellenir (`⚠️ Doğrulanmalı`).

---

## 1. Yeşil bölge — gönül rahatlığıyla pratik yapabileceğin yerler

Bunların hepsi **öğretmek için** yapılmış, indirmen ve çözmen için açıkça sunulmuş materyaldir:

| Kaynak | Ne | Neden yasal |
|---|---|---|
| **CTF binary'leri** (picoCTF, pwn.college) | Kasıtlı bulmaca programları | Site seni çözmen için davet ediyor |
| **crackmes.one** | Yazarların paylaştığı crackme'ler | Yazar "çöz ve öğren" diye koymuş |
| **ROP Emporium, exploit.education** | Exploit öğretim binary'leri | Eğitim amaçlı, açık lisans |
| **Kendi yazdığın program** | Kendi derlediğin C kodu | Senin malın, ne istersen yap |
| **Açık kaynak yazılım** | Kaynağı zaten açık | Kısıtlama yok |
| **Flare-On geçmiş challenge'ları** | Mandiant'ın yıllık CTF arşivi | Herkese açık, indirilir (arşiv şifresi: `flare`) |

**Kural:** Bu kursun tüm pratikleri yeşil bölgededir. Buradan çıkma.

---

## 2. Sarı bölge — "duruma göre", dikkatli ol

| Durum | Dikkat |
|---|---|
| **Ücretli yazılımın deneme sürümü** | Sırf incelemek eğitim olabilir; ama lisans/korumayı kırmak (crack) EULA ihlali ve çoğu yerde suç. İncele, kırma. |
| **Uyumluluk için tersine mühendislik** | Türkiye'de FSEK m.38 ve AB direktifleri, *ara işlerlik (interoperability)* için belirli sınırlarda decompile'a izin verir. Ama sınırları dar: rakip ürün üretmek/ticari kopya yasak. |
| **Güvenlik araştırması** | ABD'de DMCA §1201'in "iyi niyetli güvenlik araştırması" istisnası var ama **yetki/izin** şartıyla. Yetkin yoksa girme. |
| **Malware örnekleri** | İncelemek yasal olabilir ama **asla ana makinende çalıştırma** — izole VM şart (aşağıda). |

**Kural:** Sarı bölgeye bu kurs seni sokmaz. İleride kendi başına girersen, önce yetki/izin ve yerel yasayı netleştir.

---

## 3. Kırmızı bölge — yapma

- Başkasının **lisanslı yazılımını kırıp** dağıtmak, "crack" üretmek.
- **DRM/kopya korumasını** kırıp içerik dağıtmak.
- **Yetkin olmayan** bir sistemi/uygulamayı zafiyet için tersine çevirip saldırmak.
- Oyun **hilesi (cheat)** yazıp yaymak — crackmes.one bile bunu yasaklar.
- **Gerçek malware'i** paylaşmak veya yaymak.

Bunlar sadece "kurs kuralı" değil; çoğu ülkede telif ihlali, bilişim suçu veya her ikisi.

---

## 4. Malware / şüpheli binary için izolasyon kuralı (ileride lazım olacak)

Bu kurs Linux CTF binary'leriyle çalışır — bunlar zararsızdır. Ama merakla bir yerden bir şey
indirirsen, **kesinlikle şu kurallara uy:**

1. **İzole VM kullan.** VirtualBox/VMware'de ayrı bir sanal makine. Ana makinenle paylaşılan klasör yok.
2. **Ağı kapat.** VM'in network'ünü "host-only" veya tamamen kapalı yap. Malware dışarı konuşamasın.
3. **Snapshot al.** İncelemeden önce temiz snapshot; iş bitince geri dön.
4. **Şüpheliyi ana makinede ÇİFT TIKLAMA.** Analiz araçlarıyla *incele*, körlemesine *çalıştırma*.

> ⚠️ **Basılmaması gereken buton:** Bilmediğin bir çalıştırılabilir dosyayı ana işletim sistemin üzerinde
> çift tıklayıp çalıştırmak. Şüpheyle indirdiğin her şey, aksi kanıtlanana kadar zararlıdır.

---

## 5. CTF etiği — küçük ama önemli kurallar

- **Flag'leri paylaşma.** Aktif bir yarışmada çözümü/flag'i başkasına vermek kural ihlali.
- **Writeup'ı yarışma bitince yayınla.** Çoğu CTF, etkinlik sürerken çözüm paylaşımını yasaklar.
- **Platforma saldırma.** Challenge'ı çöz; challenge'ı *barındıran* altyapıya saldırmak yasak ve etik dışı.
- **Kaynağını belirt.** Bir writeup'tan öğrendiysen, kendi yazında ona atıf ver.

---

## 6. Türkiye özelinde tek cümlelik özet

FSEK m.38, bilgisayar programlarının **kişisel kullanım ve uyumluluk (interoperability) amacıyla**
incelenmesine/geri derlenmesine sınırlı izin verir; **ticari kopya veya rakip ürün üretmek yasaktır.**
CTF/crackme pratiği bu tartışmanın tamamen dışında — çünkü materyal sana çözmen için açıkça sunuluyor.
⚠️ Doğrulanmalı: Somut bir durum için güncel mevzuata/avukata bak.

---

## Özet — cebine koy

- **Yeşil bölgede kal:** CTF, crackmes.one, eğitim binary'leri, kendi kodun. Bu kursun tamamı burada.
- **Teknik nötr, niyet belirleyici.** Aynı beceri masum ya da suç olabilir; farkı *ne* ve *neden* yapar.
- **Şüpheli binary = izole VM + kapalı ağ + snapshot.** Ana makinede asla çalıştırma.
- **CTF'te:** flag paylaşma, writeup'ı etkinlik bitince yayınla, altyapıya dokunma.

Bir sonraki dosya: ortamı kuruyoruz. Linux, Ghidra, GDB, pwntools — hepsi ücretsiz.

## Sırada ne var
➡️ [02-ortam-kurulumu.md](02-ortam-kurulumu.md)
