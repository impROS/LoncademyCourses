#!/usr/bin/env node
/* Skor sunucusu — test bitince sonucu otomatik olarak konu dosyasına yazar.
 *
 * Neden gerekli: Tarayıcı, güvenlik nedeniyle diske dosya yazamaz. Test sonucunun
 * .md dosyasına düşmesi için sonucu alıp yazacak küçük bir yerel süreç şart.
 *
 * Çalıştır:  node assets/skor-sunucu.js
 * Testleri:  http://localhost:8899/<bolum>/<kod>-test.html  (önerilen)
 *            ya da dosyaya çift tıklayarak (file://) — o da çalışır.
 *
 * Bağımlılık yok, sadece Node stdlib. Yalnızca 127.0.0.1'e bağlanır.
 */
'use strict';
const http = require('http');
const fs   = require('fs');
const path = require('path');
const url  = require('url');

const ROOT = path.resolve(__dirname, '..');
/* Bu kursun kendi portu. 8899 sabit bırakılırsa aynı anda açık başka bir kurs
 * setiyle çakışır: sunucu EADDRINUSE ile hiç açılmaz ve kullanıcı skorların
 * neden yazılmadığını anlayamaz. Süreç 3'te kursa özel portla değiştirilir. */
const PORT = Number(process.env.SKOR_PORT || 8888);

const MIME = { '.html':'text/html; charset=utf-8', '.js':'text/javascript; charset=utf-8',
  '.css':'text/css; charset=utf-8', '.md':'text/plain; charset=utf-8', '.json':'application/json' };

const BAS = '<!-- skor:baslangic -->';
const BIT = '<!-- skor:bitis -->';

function damga(d) {
  const p = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`;
}
function sure(sn) {
  if (!sn && sn !== 0) return '—';
  sn = Math.round(sn);
  return sn < 60 ? `${sn} sn` : `${Math.floor(sn/60)} dk ${p2(sn%60)} sn`;
  function p2(n){ return String(n).padStart(2,'0'); }
}

/* --- hangi .md dosyasına yazılacak --- */
function hedefDosya(s) {
  if (/^final/i.test(s.id || '')) return path.join(ROOT, '99-final', 'sinav-gecmisi.md');
  let u = null;
  try { u = new URL(s.href); } catch (e) { /* göreli yol; aşağıda ele alınıyor */ }
  let dizin;
  if (u && (u.protocol === 'http:' || u.protocol === 'https:')) {
    // Test bu sunucudan açıldı. Adresteki yol KURS KÖKÜNE göredir, dosya
    // sistemine göre değil: '/01-bolum/1.1-test.html'. Dosya yoluymuş gibi
    // değerlendirilirse kökün dışında görünür ve sonuç yanlışlıkla
    // "başka bir kursa ait" diye reddedilir — yani sunucunun kendi başlangıç
    // mesajında önerdiği açılış biçimi hiç çalışmaz.
    dizin = path.join(ROOT, path.dirname(decodeURIComponent(u.pathname)));
  } else {
    const yol = u ? decodeURIComponent(u.pathname) : String(s.href || '');
    dizin = path.dirname(yol);
    // file:// ile açılmış ve BAŞKA bir kursun içindeyse: bu sonuç bize ait değil.
    // Sessizce kendi kökümüze eklersek anlamsız bir 'dosya yok' hatası çıkar
    // ve kullanıcı neden yazılmadığını anlayamaz.
    if (path.isAbsolute(dizin) && !dizin.startsWith(ROOT)) {
      throw new Error('bu sonuç başka bir kursa ait (' + dizin + '); o kursun skor '
        + 'sunucusunu kendi portunda başlat — bkz. assets/quiz.js içindeki SKOR_PORT');
    }
    if (!path.isAbsolute(dizin)) dizin = path.join(ROOT, dizin);
  }
  const hedef = path.resolve(dizin, s.back || '');
  if (!hedef.startsWith(ROOT) || path.extname(hedef) !== '.md') throw new Error('geçersiz hedef: ' + hedef);
  return hedef;
}

/* --- konu dosyasına satırı yaz --- */
function skoruYaz(dosya, s) {
  if (!fs.existsSync(dosya)) {
    fs.writeFileSync(dosya, `# ${s.code || 'Sınav'} — skor geçmişi\n\n> Bu dosya **otomatik** yazılır (\`assets/skor-sunucu.js\`).\n\n`);
  }
  let t = fs.readFileSync(dosya, 'utf8');

  if (t.indexOf(BAS) === -1) {
    const blok =
`\n---\n\n## 📊 Test geçmişim\n\n` +
`> Bu tablo test bittiğinde **otomatik** doldurulur (\`assets/skor-sunucu.js\` çalışıyorsa).\n` +
`> En yeni deneme en üstte. Elle düzenlersen bir sonraki yazımda korunur; yalnızca yeni satır eklenir.\n\n` +
`${BAS}\n| Tarih | Skor | Yüzde | Süre | Zayıf alanlar |\n|---|---|---|---|---|\n${BIT}\n`;
    const nerede = t.indexOf('\n## Sırada ne var');
    t = nerede === -1 ? t.replace(/\s*$/, '\n') + blok : t.slice(0, nerede) + blok + t.slice(nerede);
  }

  const zayif = (s.weak && s.weak.length) ? s.weak.join(', ') : '—';
  const satir = `| ${damga(new Date())} | ${s.right}/${s.total} | **%${s.pct}** ${s.pct >= (s.pass || 80) ? '✅' : '⚠️'} | ${sure(s.seconds)} | ${zayif} |`;

  const i = t.indexOf(BAS), j = t.indexOf(BIT);
  const govde = t.slice(i + BAS.length, j);
  const satirlar = govde.split('\n').filter(x => x.trim());
  const basliklar = satirlar.slice(0, 2);                 // | Tarih |... ve |---|
  const eskiler   = satirlar.slice(2);
  const yeni = [BAS, ...basliklar, satir, ...eskiler, BIT].join('\n');
  t = t.slice(0, i) + yeni + t.slice(j + BIT.length);

  fs.writeFileSync(dosya, t);
  return dosya;
}

/* --- README ilerleme tablosundaki skor hücresini güncelle --- */
function readmeGuncelle(s) {
  const rm = path.join(ROOT, 'README.md');
  if (!fs.existsSync(rm)) return null;
  let t = fs.readFileSync(rm, 'utf8');
  let testYolu;
  try { testYolu = path.relative(ROOT, decodeURIComponent(new URL(s.href).pathname)); }
  catch (e) { return null; }
  if (path.isAbsolute(testYolu) || testYolu.startsWith('..')) {
    const m = String(s.href).match(/([^/\\]+[/\\][^/\\]+\.html)$/);
    if (!m) return null; testYolu = m[1].replace(/\\/g, '/');
  }
  const satirlar = t.split('\n');
  let degisti = false;
  for (let i = 0; i < satirlar.length; i++) {
    if (satirlar[i].indexOf(testYolu) === -1 || satirlar[i][0] !== '|') continue;
    const h = satirlar[i].split('|');
    if (h.length < 3) continue;
    h[h.length - 2] = ` %${s.pct} (${s.right}/${s.total}) `;          // son dolu hücre = Skor
    if (s.pct >= (s.pass || 80)) h[1] = h[1].replace('[ ]', '[x]');
    satirlar[i] = h.join('|'); degisti = true; break;
  }
  if (!degisti) return null;
  fs.writeFileSync(rm, satirlar.join('\n'));
  return rm;
}

/* --- başlangıç mesajında gösterilecek örnek test yolu ---
 * Sabit yazılmaz: her kursun klasör adları farklı ve var olmayan bir yol
 * göstermek kullanıcıyı 404'e yollar. Kursun ilk testini bulup onu yazar. */
function ornekTest() {
  const bolumler = fs.readdirSync(ROOT, { withFileTypes: true })
    .filter(d => d.isDirectory() && /^[0-9]/.test(d.name))
    .map(d => d.name).sort();
  for (const b of bolumler) {
    const test = fs.readdirSync(path.join(ROOT, b)).sort()
      .find(f => f.endsWith('-test.html'));
    if (test) return `${b}/${test}`;
  }
  return '<bolum>/<kod>-test.html';
}

/* --- HTTP --- */
const sunucu = http.createServer((req, res) => {
  const cors = { 'Access-Control-Allow-Origin': '*', 'Access-Control-Allow-Headers': '*',
                 'Access-Control-Allow-Methods': 'POST, GET, OPTIONS' };
  if (req.method === 'OPTIONS') { res.writeHead(204, cors); return res.end(); }

  if (req.method === 'POST' && req.url.startsWith('/skor')) {
    let g = '';
    req.on('data', c => { g += c; if (g.length > 64 * 1024) req.destroy(); });
    req.on('end', () => {
      try {
        const s = JSON.parse(g);
        const yazilan = skoruYaz(hedefDosya(s), s);
        const rm = readmeGuncelle(s);
        console.log(`✓ ${damga(new Date())}  ${s.code || s.id}  ${s.right}/${s.total} (%${s.pct})`
                  + `  → ${path.relative(ROOT, yazilan)}${rm ? ' + README.md' : ''}`);
        res.writeHead(200, Object.assign({ 'Content-Type': 'application/json' }, cors));
        res.end(JSON.stringify({ ok: true, dosya: path.relative(ROOT, yazilan) }));
      } catch (e) {
        console.error('✗ yazılamadı:', e.message);
        res.writeHead(400, Object.assign({ 'Content-Type': 'application/json' }, cors));
        res.end(JSON.stringify({ ok: false, hata: e.message }));
      }
    });
    return;
  }

  if (req.method === 'GET' && req.url.startsWith('/durum')) {
    res.writeHead(200, Object.assign({ 'Content-Type': 'application/json' }, cors));
    return res.end(JSON.stringify({ ok: true, kok: ROOT }));
  }

  // statik sunum
  let p = decodeURIComponent(url.parse(req.url).pathname);
  if (p === '/') p = '/README.md';
  const dosya = path.resolve(ROOT, '.' + p);
  if (!dosya.startsWith(ROOT) || !fs.existsSync(dosya) || fs.statSync(dosya).isDirectory()) {
    res.writeHead(404, cors); return res.end('yok');
  }
  res.writeHead(200, Object.assign({ 'Content-Type': MIME[path.extname(dosya)] || 'application/octet-stream' }, cors));
  fs.createReadStream(dosya).pipe(res);
});

sunucu.listen(PORT, '127.0.0.1', () => {
  console.log(`\n  📊 Skor sunucusu çalışıyor — http://localhost:${PORT}`);
  console.log(`     kök: ${ROOT}`);
  console.log(`     Testleri buradan aç:  http://localhost:${PORT}/${ornekTest()}`);
  console.log(`     (dosyaya çift tıklayarak açmak da çalışır)`);
  console.log(`     Durdurmak için Ctrl+C\n`);
});
