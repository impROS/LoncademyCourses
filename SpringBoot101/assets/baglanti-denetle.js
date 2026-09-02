// Kurs klasöründeki tüm markdown bağlantılarını denetler:
//  - dosya bağlantısı var olan bir dosyayı mı gösteriyor
//  - #çapa bağlantısı hedef dosyadaki bir BAŞLIĞIN GFM slug'ıyla eşleşiyor mu
// Kullanım: node baglanti-denetle.js <kurs-kökü>
const fs = require('fs'), path = require('path');
const kok = path.resolve(process.argv[2] || '.');

function slug(baslik) {
  return baslik
    .toLowerCase()
    .replace(/`/g, '')
    .replace(/[^\p{L}\p{N}\s-]/gu, '')   // noktalama sil, Türkçe harfleri KORU
    .trim()
    .replace(/\s+/g, '-');
}

function mdDosyalari(d, liste = []) {
  for (const ad of fs.readdirSync(d)) {
    const p = path.join(d, ad);
    if (fs.statSync(p).isDirectory()) { if (ad !== 'assets') mdDosyalari(p, liste); }
    else if (ad.endsWith('.md')) liste.push(p);
  }
  return liste;
}

const basliklar = new Map();   // dosya yolu -> Set<slug>
let uyari = 0;
for (const f of mdDosyalari(kok)) {
  const s = new Set();
  let kodBlogu = false;
  fs.readFileSync(f, 'utf8').split('\n').forEach((satir, i) => {
    if (/^\s*```/.test(satir)) { kodBlogu = !kodBlogu; return; }
    if (kodBlogu) return;
    const m = satir.match(/^(#{1,6})\s+(.*?)\s*$/);
    if (!m) return;
    const sl = slug(m[2]);
    // ⚠️ Türkçe 'İ' tuzağı: "İ".toLowerCase() → "i" + U+0307 (görünmez birleşen nokta).
    // Slug göze normal 'i' gibi görünür ama farklı bir dizedir; elle yazılan bağlantı tutmaz.
    if (/̇/.test(sl)) {
      console.log(`⚠ ${path.relative(kok, f)}:${i + 1}  başlıkta 'İ' var → slug görünmez U+0307 içeriyor: "${m[2]}"`);
      uyari++;
    }
    s.add(sl);
  });
  basliklar.set(f, s);
}

let hata = 0, kontrol = 0;
for (const f of mdDosyalari(kok)) {
  const metin = fs.readFileSync(f, 'utf8');
  const satirlar = metin.split('\n');
  let kodBlogu = false;
  satirlar.forEach((satir, i) => {
    if (/^\s*```/.test(satir)) { kodBlogu = !kodBlogu; return; }
    if (kodBlogu) return;
    const re = /\[[^\]]*\]\(([^)\s]+)\)/g;
    let m;
    while ((m = re.exec(satir)) !== null) {
      const hedef = m[1];
      if (/^(https?:|mailto:)/.test(hedef)) continue;
      kontrol++;
      const [yol, capa] = hedef.split('#');
      const mutlak = yol ? path.resolve(path.dirname(f), yol) : f;
      if (yol && !fs.existsSync(mutlak)) {
        console.log(`✗ ${path.relative(kok, f)}:${i + 1}  dosya yok → ${hedef}`);
        hata++; continue;
      }
      if (capa) {
        if (!mutlak.endsWith('.md')) continue;
        const küme = basliklar.get(mutlak);
        if (!küme) { console.log(`✗ ${path.relative(kok, f)}:${i + 1}  hedef okunamadı → ${hedef}`); hata++; continue; }
        if (!küme.has(capa)) {
          console.log(`✗ ${path.relative(kok, f)}:${i + 1}  çapa yok → ${hedef}`);
          hata++;
        }
      }
    }
  });
}
console.log(`\n${kontrol} bağlantı denetlendi, ${hata} kırık, ${uyari} riskli başlık.`);
process.exit(hata ? 1 : 0);
