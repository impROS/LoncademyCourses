/* CLF-C02 çalışma seti — ortak quiz motoru.
   Her test dosyası window.QUIZ nesnesini tanımlar, bu script onu çalıştırır.
   QUIZ = { id, code, title, back, backLabel, mode:'practice'|'exam',
            time: saniye|null, pass: 80, shuffle: true,
            passNote?: 'gerçek sınav 700/1000 ≈ %70',   // eşiğin yanına parantez içi not
            langNote?: 'Sorular İngilizce, açıklamalar Türkçe.',
            notes?: ['ek bilgi satırı', ...],
            questions:[{q, hint?, opts:[], a:[index...], why, topic}] }
   topic = alt konu adı; sonuç ekranındaki zayıf-alan raporu bu alana göre üretilir. */
(function () {
  var Q = window.QUIZ;
  var PASS = Q.pass || 70;
  var MODE = Q.mode || 'practice';
  var KEY = 'vibe101-quiz-' + Q.id;
  var LETTERS = 'ABCDEFGH';

  var pool = [], state = null, timerId = null, remain = 0, basladi = 0;

  function esc(s) { return String(s).replace(/[&<>]/g, function (c) { return { '&': '&amp;', '<': '&lt;', '>': '&gt;' }[c]; }); }
  function el(id) { return document.getElementById(id); }
  function shuffle(a) { for (var i = a.length - 1; i > 0; i--) { var j = Math.floor(Math.random() * (i + 1)); var t = a[i]; a[i] = a[j]; a[j] = t; } return a; }
  function same(x, y) { if (x.length !== y.length) return false; var a = x.slice().sort(), b = y.slice().sort(); for (var i = 0; i < a.length; i++) if (a[i] !== b[i]) return false; return true; }

  function store(sc) {
    try {
      var raw = JSON.parse(localStorage.getItem(KEY) || '{}');
      raw.attempts = (raw.attempts || 0) + 1;
      raw.last = sc;
      raw.best = Math.max(raw.best || 0, sc);
      localStorage.setItem(KEY, JSON.stringify(raw));
      return raw;
    } catch (e) { return { attempts: 1, last: sc, best: sc }; }
  }
  function readStore() { try { return JSON.parse(localStorage.getItem(KEY) || '{}'); } catch (e) { return {}; } }

  try { kuyrugusal(); } catch (e) {}

  /* --- soru havuzunu hazırla (şıkları da karıştır, doğru indeksleri taşı) --- */
  function build(list) {
    return list.map(function (q) {
      var idx = q.opts.map(function (_, i) { return i; });
      if (Q.shuffle !== false) shuffle(idx);
      return {
        src: q,
        q: q.q, hint: q.hint, why: q.why, topic: q.topic || 'Genel',
        opts: idx.map(function (i) { return q.opts[i]; }),
        a: q.a.map(function (i) { return idx.indexOf(i); })
      };
    });
  }

  function reset(list) {
    pool = build(list || Q.questions);
    if (Q.shuffle !== false) shuffle(pool);
    state = { i: 0, ans: pool.map(function () { return []; }), shown: pool.map(function () { return false; }), flag: pool.map(function () { return false; }), done: false };
    remain = Q.time || 0;
  }

  /* ---------------- ekranlar ---------------- */
  function intro() {
    var s = readStore();
    var bits = [];
    bits.push('<div class="card intro">');
    bits.push('<h2>' + esc(Q.title) + '</h2>');
    bits.push('<p>' + (MODE === 'exam'
      ? 'Sınav modu: süre işler, geri bildirim en sonda gelir. Gerçek sınav gibi çalış — emin olmadığını işaretle, sonra dön.'
      : 'Alıştırma modu: her cevaptan sonra doğru/yanlış ve gerekçeli açıklama gelir. Süre yok, düşünerek çöz.') + '</p>');
    bits.push('<ul>');
    bits.push('<li><b>' + pool.length + ' soru</b>' + (Q.time ? ' · <b>' + Math.round(Q.time / 60) + ' dakika</b>' : ' · süre sınırı yok') + '</li>');
    bits.push('<li>Geçme eşiği: <b>%' + PASS + '</b>' + (Q.passNote ? ' (' + esc(Q.passNote) + ')' : '') + '</li>');
    if (Q.langNote) bits.push('<li>' + esc(Q.langNote) + '</li>');
    bits.push('<li>Bazı sorularda birden fazla doğru cevap var — parantez içinde yazar.</li>');
    (Q.notes || []).forEach(function (n) { bits.push('<li>' + esc(n) + '</li>'); });
    bits.push('</ul>');
    if (s.attempts) bits.push('<p class="mono" style="font-size:.85rem">Önceki denemeler: ' + s.attempts + ' · Son: %' + s.last + ' · En iyi: %' + s.best + '</p>');
    bits.push('<div class="actions"><button class="btn" id="start">Teste başla</button>');
    if (Q.back) bits.push('<a class="back" style="margin:0 0 0 6px" href="' + Q.back + '">← ' + esc(Q.backLabel || 'Konuya dön') + '</a>');
    bits.push('</div></div>');
    el('app').innerHTML = bits.join('');
    el('start').onclick = function () { basladi = Date.now(); if (Q.time) startTimer(); question(); };
  }

  function startTimer() {
    var t = el('timer'); t.style.display = '';
    timerId = setInterval(function () {
      remain--;
      var m = Math.floor(remain / 60), s = remain % 60;
      t.textContent = (m < 10 ? '0' : '') + m + ':' + (s < 10 ? '0' : '') + s;
      if (remain <= 300) t.classList.add('warn');
      if (remain <= 0) { clearInterval(timerId); finish(); }
    }, 1000);
  }

  function progress() {
    el('bar').style.width = Math.round((state.i) / pool.length * 100) + '%';
  }

  function question() {
    var q = pool[state.i], shown = state.shown[state.i], sel = state.ans[state.i];
    var multi = q.a.length > 1;
    var b = [];
    b.push('<div class="card">');
    b.push('<div class="qmeta"><span>Soru ' + (state.i + 1) + ' / ' + pool.length + '</span><span>' + esc(q.topic) + '</span>' + (state.flag[state.i] ? '<span class="flagged">● işaretli</span>' : '') + '</div>');
    b.push('<p class="qtext">' + esc(q.q) + '</p>');
    if (multi || q.hint) b.push('<p class="qhint">' + esc(q.hint || ('Bu soruda ' + q.a.length + ' doğru cevap var.')) + '</p>');
    b.push('<div class="opts">');
    q.opts.forEach(function (o, i) {
      var cls = 'opt';
      if (shown) {
        if (q.a.indexOf(i) >= 0) cls += ' right';
        else if (sel.indexOf(i) >= 0) cls += ' wrong';
      } else if (sel.indexOf(i) >= 0) cls += ' sel';
      b.push('<button class="' + cls + '" data-i="' + i + '"' + (shown ? ' disabled' : '') + '><span class="key">' + LETTERS[i] + '</span><span>' + esc(o) + '</span></button>');
    });
    b.push('</div>');

    if (shown) {
      var ok = same(sel, q.a);
      b.push('<div class="why ' + (ok ? 'ok' : 'no') + '"><span class="verdict">' + (ok ? '✓ Doğru' : '✗ Yanlış — doğrusu: ' + q.a.map(function (i) { return LETTERS[i]; }).sort().join(', ')) + '</span>' + q.why + '</div>');
    }

    b.push('<div class="actions">');
    if (state.i > 0) b.push('<button class="btn quiet" id="prev">← Geri</button>');
    if (MODE === 'practice' && !shown) {
      b.push('<button class="btn" id="check"' + (sel.length ? '' : ' disabled') + '>Cevapla</button>');
    } else {
      b.push('<button class="btn" id="next">' + (state.i === pool.length - 1 ? 'Bitir ve sonucu gör' : 'Sonraki →') + '</button>');
    }
    if (MODE === 'exam') {
      b.push('<button class="btn quiet spacer" id="flag">' + (state.flag[state.i] ? 'İşareti kaldır' : 'İşaretle') + '</button>');
      b.push('<button class="btn ghost" id="fin">Sınavı bitir</button>');
    }
    b.push('</div></div>');

    if (MODE === 'exam') {
      var g = ['<div class="grid-nav">'];
      pool.forEach(function (_, i) {
        var c = state.ans[i].length ? 'done' : '';
        if (state.flag[i]) c = 'flag';
        if (i === state.i) c = 'cur';
        g.push('<button class="' + c + '" data-g="' + i + '">' + (i + 1) + '</button>');
      });
      g.push('</div>');
      b.push(g.join(''));
    }
    if (Q.back) b.push('<a class="back" href="' + Q.back + '">← ' + esc(Q.backLabel || 'Konuya dön') + '</a>');

    el('app').innerHTML = b.join('');
    progress();

    Array.prototype.forEach.call(document.querySelectorAll('.opt'), function (btn) {
      btn.onclick = function () {
        var i = +btn.dataset.i, cur = state.ans[state.i];
        if (multi) {
          var p = cur.indexOf(i);
          if (p >= 0) cur.splice(p, 1);
          else if (cur.length < q.a.length) cur.push(i);
        } else {
          state.ans[state.i] = [i];
        }
        question();
      };
    });
    if (el('check')) el('check').onclick = function () { state.shown[state.i] = true; question(); };
    if (el('next')) el('next').onclick = nextOrFinish;
    if (el('prev')) el('prev').onclick = function () { state.i--; question(); };
    if (el('flag')) el('flag').onclick = function () { state.flag[state.i] = !state.flag[state.i]; question(); };
    if (el('fin')) el('fin').onclick = function () { if (confirm('Sınavı bitirip sonucu görmek istediğine emin misin?')) finish(); };
    Array.prototype.forEach.call(document.querySelectorAll('[data-g]'), function (btn) {
      btn.onclick = function () { state.i = +btn.dataset.g; question(); };
    });
  }

  function nextOrFinish() {
    if (state.i === pool.length - 1) finish();
    else { state.i++; question(); }
  }


  /* --- skor sunucusuna bildir (assets/skor-sunucu.js) ------------------------
     Tarayıcı diske yazamaz; sonucu alıp .md dosyasına yazan yerel sunucuya
     gönderiyoruz. Sunucu kapalıysa sonuç kuyruğa alınır ve bir dahaki açılışta
     gönderilir — hiçbir skor kaybolmaz. */
  /* Birden çok kurs setin varsa her birine ayrı port ver: burayı değiştir ve
     sunucuyu `SKOR_PORT=8897 node assets/skor-sunucu.js` diye başlat. */
  var SKOR_PORT = 8897;
  var SKOR_URL = 'http://localhost:' + SKOR_PORT + '/skor';
  var KUYRUK = 'vibe101-skor-kuyruk';

  function kuyrugaAl(veri) {
    try {
      var k = JSON.parse(localStorage.getItem(KUYRUK) || '[]');
      k.push(veri); localStorage.setItem(KUYRUK, JSON.stringify(k.slice(-100)));
    } catch (e) {}
  }
  function gonder(veri) {
    return fetch(SKOR_URL, {
      method: 'POST', headers: { 'Content-Type': 'text/plain' },
      body: JSON.stringify(veri), keepalive: true
    }).then(function (r) { return r.ok ? r.json() : Promise.reject(new Error('http ' + r.status)); });
  }
  function kuyrugusal() {
    var k;
    try { k = JSON.parse(localStorage.getItem(KUYRUK) || '[]'); } catch (e) { return; }
    if (!k.length) return;
    var kalan = [], zincir = Promise.resolve();
    k.forEach(function (v) {
      zincir = zincir.then(function () { return gonder(v); }).catch(function () { kalan.push(v); });
    });
    zincir.then(function () {
      try { localStorage.setItem(KUYRUK, JSON.stringify(kalan)); } catch (e) {}
      if (k.length && kalan.length < k.length) console.log('[skor] ' + (k.length - kalan.length) + ' bekleyen sonuç yazıldı');
    });
  }

  function skorBildir(veri, kutuId) {
    function rozet(metin, sinif) {
      var d = el(kutuId); if (!d) return;
      d.innerHTML = '<div class="sub" style="margin-top:10px;opacity:.85">' + metin + '</div>';
    }
    gonder(veri).then(function (c) {
      rozet('📊 Skor <code>' + esc(c.dosya) + '</code> dosyasına yazıldı.');
      kuyrugusal();
    }).catch(function () {
      kuyrugaAl(veri);
      rozet('📊 Skor sunucusu kapalı — sonuç <b>kuyruğa alındı</b>. '
          + 'Yazmak için: <code>node assets/skor-sunucu.js</code> çalıştırıp bu sayfayı yenile.');
    });
  }

  function finish() {
    if (timerId) { clearInterval(timerId); timerId = null; }
    el('timer').style.display = 'none';
    state.done = true;
    var right = 0, byTopic = {}, wrong = [];
    pool.forEach(function (q, i) {
      var ok = same(state.ans[i], q.a);
      if (ok) right++; else wrong.push(i);
      var t = byTopic[q.topic] || (byTopic[q.topic] = { n: 0, ok: 0 });
      t.n++; if (ok) t.ok++;
    });
    var pct = Math.round(right / pool.length * 100);
    var rec = store(pct);
    var passed = pct >= PASS;

    var zayifListe = [];
    Object.keys(byTopic).forEach(function (t) {
      if (byTopic[t].ok / byTopic[t].n * 100 < PASS) zayifListe.push(t);
    });
    skorBildir({
      id: Q.id, code: Q.code, title: Q.title, back: Q.back, href: location.href,
      mode: MODE, pass: PASS, right: right, total: pool.length, pct: pct,
      seconds: basladi ? Math.round((Date.now() - basladi) / 1000) : null,
      weak: zayifListe
    }, 'skor-rozet');

    var b = ['<div class="card">'];
    b.push('<div class="score"><div class="pct ' + (passed ? 'pass' : 'fail') + '">%' + pct + '</div>');
    b.push('<div class="verdict">' + (passed ? 'Geçtin — bu konu oturmuş' : 'Eşiğin altında — bu konuya geri dön') + '</div>');
    b.push('<div class="sub">' + right + ' / ' + pool.length + ' doğru · eşik %' + PASS + ' · en iyi skorun %' + rec.best + '</div>');
    b.push('<div id="skor-rozet"></div></div>');

    var topics = Object.keys(byTopic).sort(function (x, y) { return (byTopic[x].ok / byTopic[x].n) - (byTopic[y].ok / byTopic[y].n); });
    b.push('<table class="brk"><tr><th>Alt konu</th><th>Doğru</th><th>Oran</th></tr>');
    var weak = [];
    topics.forEach(function (t) {
      var o = byTopic[t], r = Math.round(o.ok / o.n * 100), w = r < PASS;
      if (w) weak.push(t);
      b.push('<tr class="' + (w ? 'weak' : '') + '"><td>' + esc(t) + '</td><td class="n">' + o.ok + '/' + o.n + '</td><td class="n">%' + r + '</td></tr>');
    });
    b.push('</table>');

    b.push('<div class="advice">');
    if (weak.length) {
      b.push('<b>Ne yapmalısın:</b> Kırmızı satırlar zayıf alanların. Konu dosyasında şu başlıklara dön: <b>' + weak.map(esc).join('</b>, <b>') + '</b>. Okuduktan sonra testi tekrar çöz — sorular ve şıklar her seferinde karışır, ezber işe yaramaz.');
    } else if (passed) {
      b.push('<b>Temiz geçiş.</b> Tüm alt konularda eşiğin üstündesin. Bir sonraki konuya geçebilirsin. Bu testi bir hafta sonra tekrar çözüp kalıcılığı ölç.');
    } else {
      b.push('<b>Yaygın eksik:</b> Hiçbir alt konu tek başına çökmemiş ama toplam eşiğin altında. Konu dosyasını baştan sona bir kez daha oku, özellikle "Sık karıştırılanlar" tablosunu.');
    }
    b.push('</div>');

    if (wrong.length) {
      b.push('<h3 style="font-size:1rem;margin:0 0 12px">Yanlış cevapladıkların (' + wrong.length + ')</h3><div class="review">');
      wrong.forEach(function (i) {
        var q = pool[i];
        b.push('<div class="rev"><div class="rq">' + (i + 1) + '. ' + esc(q.q) + '</div>');
        b.push('<div class="ra">Senin cevabın: ' + (state.ans[i].length ? state.ans[i].map(function (k) { return LETTERS[k] + ') ' + q.opts[k]; }).map(esc).join(' · ') : '(boş)') + '</div>');
        b.push('<div class="rc">Doğru cevap: ' + q.a.map(function (k) { return LETTERS[k] + ') ' + q.opts[k]; }).map(esc).join(' · ') + '</div>');
        b.push('<div class="rw">' + q.why + '</div></div>');
      });
      b.push('</div>');
    }

    b.push('<div class="actions"><button class="btn" id="again">Tekrar çöz (karıştırarak)</button>');
    if (wrong.length) b.push('<button class="btn ghost" id="onlywrong">Sadece yanlışları çöz (' + wrong.length + ')</button>');
    b.push('</div>');
    if (Q.back) b.push('<a class="back" href="' + Q.back + '">← ' + esc(Q.backLabel || 'Konuya dön') + '</a>');
    b.push('</div>');
    el('app').innerHTML = b.join('');
    el('bar').style.width = '100%';

    el('again').onclick = function () { reset(); if (Q.time) { el('timer').textContent = fmt(Q.time); startTimer(); } question(); };
    if (el('onlywrong')) el('onlywrong').onclick = function () {
      var subset = wrong.map(function (i) { return pool[i].src; });
      reset(subset); question();
    };
  }

  function fmt(s) { var m = Math.floor(s / 60), r = s % 60; return (m < 10 ? '0' : '') + m + ':' + (r < 10 ? '0' : '') + r; }

  /* ---------------- kurulum ---------------- */
  document.addEventListener('DOMContentLoaded', function () {
    document.title = Q.title + ' — CLF-C02';
    document.body.innerHTML =
      '<header class="top"><div class="wrap top-inner">' +
      '<div><span class="code">' + esc(Q.code || 'CLF-C02') + '</span><h1>' + esc(Q.title) + '</h1></div>' +
      '<div class="right"><span class="best" id="bestbox"></span>' +
      '<span class="timer" id="timer" style="display:none">' + fmt(Q.time || 0) + '</span></div>' +
      '</div><div class="bar"><i id="bar"></i></div></header>' +
      '<main><div class="wrap" id="app"></div></main>';
    var s = readStore();
    if (s.best !== undefined) el('bestbox').textContent = 'en iyi %' + s.best;
    reset();
    intro();
  });
})();
