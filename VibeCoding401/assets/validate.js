// Tüm test dosyalarını doğrular: window.QUIZ parse edilebiliyor mu, cevap indeksleri geçerli mi,
// zorunlu alanlar var mı, konu dosyası mevcut mu.
const fs = require('fs'), path = require('path');
const root = path.join(__dirname, '..');
let files = [];
for (const d of fs.readdirSync(root)) {
  const p = path.join(root, d);
  if (!fs.statSync(p).isDirectory() || d === 'assets') continue;
  for (const f of fs.readdirSync(p)) if (f.endsWith('.html')) files.push(path.join(p, f));
}
let bad = 0, totalQ = 0;
for (const f of files.sort()) {
  const h = fs.readFileSync(f, 'utf8');
  const m = h.match(/window\.QUIZ\s*=\s*([\s\S]*?);\s*<\/script>/);
  const rel = path.relative(root, f);
  if (!m) { console.log('✗ ' + rel + ' : QUIZ bulunamadı'); bad++; continue; }
  let Q;
  try { Q = eval('(' + m[1] + ')'); } catch (e) { console.log('✗ ' + rel + ' : JS hatası — ' + e.message); bad++; continue; }
  const errs = [];
  if (!Q.id || !Q.title) errs.push('id/title eksik');
  Q.questions.forEach((q, i) => {
    const n = i + 1;
    if (!q.q || !q.opts || !q.a || !q.why || !q.topic) errs.push('S' + n + ': zorunlu alan eksik');
    if (q.opts && new Set(q.opts).size !== q.opts.length) errs.push('S' + n + ': yinelenen şık');
    if (q.a && q.opts && !q.a.every(x => Number.isInteger(x) && x >= 0 && x < q.opts.length)) errs.push('S' + n + ': geçersiz cevap indeksi');
    if (q.a && new Set(q.a).size !== q.a.length) errs.push('S' + n + ': yinelenen cevap indeksi');
    // Kaç şık işaretleneceği soru metninde YAZILI olmalı. Anlatım Türkçe olan
    // setlerde "(İki tanesini seç.)" doğaldır; sınav dili İngilizce olanlarda
    // "(Choose TWO.)". İkisi de kabul edilir — biri zorunlu.
    const sayiIsareti = /Choose (TWO|THREE)/i.test(q.q) || /tanesini seç/i.test(q.q);
    if (q.a && q.a.length > 1 && !sayiIsareti) errs.push('S' + n + ': çoklu cevap ama kaç tane seçileceği yazmıyor — "(Choose TWO.)" ya da "(İki tanesini seç.)" ekle');
    if (q.a && q.a.length === 1 && sayiIsareti) errs.push('S' + n + ': çoklu seçim işareti var ama tek cevap');
  });
  if (Q.back && !fs.existsSync(path.join(path.dirname(f), Q.back))) errs.push('back linki kırık: ' + Q.back);
  totalQ += Q.questions.length;
  if (errs.length) { console.log('✗ ' + rel + '\n    ' + errs.join('\n    ')); bad++; }
  else console.log('✓ ' + rel + ' (' + Q.questions.length + ' soru)');
}
console.log('\n' + files.length + ' test dosyası, ' + totalQ + ' soru, ' + bad + ' hatalı.');
process.exit(bad ? 1 : 0);
