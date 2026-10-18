/**
 * Учёт отчётов игроков в Google Таблице, привязанной к форме (Расширения → Apps Script).
 * Вставляется целиком; один раз запустить setup(). Личных данных и секретов здесь нет —
 * данные живут только в Таблице владельца. Инструкция — docs/report-form.md.
 *
 * Что делает:
 *  - при новом ответе присваивает номер R-001, R-002… (счётчик в свойствах документа, не зависит от удаления строк),
 *    ставит статус «новый» и дату изменения;
 *  - раз в день проверяет, что у всех строк есть номер (на случай сбоя триггера);
 *  - при правке статуса/ответственного/версии/заметки обновляет «Дату изменения»;
 *  - список статусов, подсветка зависших отчётов и лист «Сводка» (получено / обработано / висит / старше N дней);
 *  - лист «По людям» (по адресу почты из формы, без адреса — по нику): сколько отчётов и открытых, номера, последний ответ, неотвеченные;
 *  - колонки для публичной страницы (заполняются вручную, без ников; сборка — scripts/report-status.py) и для ответов авторам;
 *    строка с оранжевой подсветкой = статус сменился, а ответ автору не отправлен.
 */
var CFG = {
  STATUSES: ['новый', 'принят', 'в работе', 'исправлено', 'дубль', 'не баг', 'нужны данные', 'отложено'],
  OPEN: ['новый', 'принят', 'в работе', 'нужны данные'],   // «отложено» не краснеет: ждёт сознательно
  DONE: ['исправлено', 'дубль', 'не баг'],
  STALE_DAYS: 7,
  EXTRA: ['№', 'Статус', 'Ответственный (поток/PR)', 'Исправлено в версии', 'Дата изменения', 'Заметка',
          'Статус изменён (дата)', 'Мод для публичной страницы', 'Описание для публичной страницы',
          'Пояснение для публичной страницы', 'Ответ автору', 'Ответ отправлен (дата)', 'Категория'],
  EDIT_TRIGGERS: ['Статус', 'Ответственный (поток/PR)', 'Исправлено в версии', 'Заметка',
                  'Мод для публичной страницы', 'Описание для публичной страницы', 'Пояснение для публичной страницы', 'Категория'],
  CATEGORIES: ['краш/вылет', 'фризы/лаги', 'мод/механика', 'модели/графика', 'звук/голос', 'сервер/вход', 'другое'],
  NICK: 'Ник / имя для связи',
  // Форма с «Собирать адреса эл. почты: Подтверждённые» сама добавляет колонку; название зависит от языка. Нет колонки — setup() создаёт первую.
  EMAIL_TITLES: ['Адрес электронной почты', 'Адрес эл. почты', 'Email Address'],
  MAX_ROWS: 2000
};

// Номер R-### : минимум три цифры, дальше без обрезки (R-1000).
function formatNumber(n) { var s = String(n); while (s.length < 3) s = '0' + s; return 'R-' + s; }

// Зависший отчёт: открытый статус и последнее изменение старше days дней.
function isStale(status, changed, now, days) {
  if (status && CFG.OPEN.indexOf(status) < 0) return false;   // пустой статус = ещё не разобран
  if (!changed) return true;
  return (now.getTime() - new Date(changed).getTime()) > days * 86400000;
}

// Формулы служебных листов. c — буквы колонок {last, num, nick, status}; sheet — имя листа ответов.
// Только для владельца: на листах есть ники. Листы не публиковать, не делиться Таблицей.
function buildQueries(sheet, c) {
  var q = "'" + sheet + "'!A2:" + c.last;
  var notDone = CFG.DONE.map(function (x) { return c.status + " <> '" + x + "'"; }).join(' and ');
  return {
    byNumber: '=QUERY(' + q + ', "select * where ' + c.num + ' is not null order by ' + c.num + '", 0)',
    byAuthor: '=QUERY(' + q + ', "select * where ' + c.num + ' is not null order by ' + c.nick + ', ' + c.num + '", 0)',
    pending: '=QUERY(' + q + ', "select ' + c.nick + ', count(' + c.num + ') where ' + c.num + ' is not null and ' + c.nick +
      ' is not null and ' + notDone + ' group by ' + c.nick + ' order by count(' + c.num + ') desc label ' + c.nick +
      " 'Автор', count(" + c.num + ") 'Открытых отчётов'" + '", 0)'
  };
}

// Колонка адреса: номер (с 1) или 0, если её нет.
function emailColIndex_(header) {
  for (var i = 0; i < CFG.EMAIL_TITLES.length; i++) { var k = header.indexOf(CFG.EMAIL_TITLES[i]); if (k >= 0) return k + 1; }
  return 0;
}

// Лист «По людям»: группировка по адресу, а если адреса нет — по нику. Только для владельца.
// rows — [{email, nick, number, status, statusChanged, replySent}]. Возвращает строки таблицы (без заголовка).
function groupPeople(rows) {
  var by = {}, order = [];
  rows.forEach(function (x) {
    if (!x.number) return;
    var key = (x.email ? String(x.email).trim().toLowerCase() : '') || ('ник:' + String(x.nick || '').trim().toLowerCase());
    if (!by[key]) { by[key] = { email: x.email ? String(x.email).trim() : '', nick: x.nick || '', items: [], lastReply: null, unanswered: false }; order.push(key); }
    var g = by[key];
    if (!g.nick && x.nick) g.nick = x.nick;
    g.items.push(x);
    if (x.replySent && (!g.lastReply || new Date(x.replySent) > new Date(g.lastReply))) g.lastReply = x.replySent;
    if (needsReply(x.number, x.statusChanged, x.replySent)) g.unanswered = true;
  });
  var out = order.map(function (k) {
    var g = by[k];
    var open = g.items.filter(function (x) { return CFG.DONE.indexOf(x.status) < 0; }).length;
    g.items.sort(function (a, b) { return a.number < b.number ? -1 : a.number > b.number ? 1 : 0; });
    return { open: open, key: k, row: [g.email, g.nick, g.items.length, open,
      g.items.map(function (x) { return x.number + ' (' + (x.status || 'без статуса') + ')'; }).join(', '),
      g.lastReply || '', g.unanswered ? 'да' : 'нет'] };
  });
  out.sort(function (a, b) { return b.open - a.open || (a.key < b.key ? -1 : a.key > b.key ? 1 : 0); });
  return out.map(function (x) { return x.row; });
}

function rebuildPeople_() {
  var ss = SpreadsheetApp.getActiveSpreadsheet(), sh = responsesSheet_();
  var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
  var last = sh.getLastRow();
  var t = ss.getSheetByName('По людям') || ss.insertSheet('По людям');
  t.clear();
  t.getRange(1, 1, 1, 7).setValues([['Адрес эл. почты', 'Ник', 'Всего отчётов', 'Открытых', 'Отчёты и статусы', 'Последний ответ отправлен', 'Есть неотвеченные']]);
  t.setFrozenRows(1);
  if (last < 2) return;
  var data = sh.getRange(2, 1, last - 1, sh.getLastColumn()).getValues();
  var col = function (title) { return h.indexOf(title); };
  var cE = emailColIndex_(h) - 1;
  var rows = data.map(function (r) {
    return { email: cE >= 0 ? r[cE] : '', nick: r[col(CFG.NICK)], number: r[col('№')], status: r[col('Статус')],
             statusChanged: r[col('Статус изменён (дата)')], replySent: r[col('Ответ отправлен (дата)')] };
  });
  var res = groupPeople(rows);
  if (res.length) t.getRange(2, 1, res.length, 7).setValues(res);
  t.autoResizeColumns(1, 7);
}

// Меню для ручного обновления (на случай правок статусов): простой триггер, без прав.
function onOpen() { SpreadsheetApp.getUi().createMenu('Отчёты').addItem('Обновить «По людям»', 'rebuildPeople_').addToUi(); }

// Нужен ли ответ автору: статус менялся (или отчёт новый), а ответ не отправлен или отправлен раньше смены статуса.
function needsReply(number, statusChanged, replySent) {
  if (!number) return false;
  if (!replySent) return true;
  if (!statusChanged) return false;
  return new Date(replySent).getTime() < new Date(statusChanged).getTime();
}

// rows — массив {status, number, changed}. Возвращает числа для сводки.
function summarize(rows, now, days) {
  var r = { received: rows.length, noNumber: 0, done: 0, open: 0, stale: 0, postponed: 0 };
  rows.forEach(function (x) {
    if (!x.number) r.noNumber++;
    if (CFG.DONE.indexOf(x.status) >= 0) r.done++;
    else r.open++;
    if (x.status === 'отложено') r.postponed++;
    if (isStale(x.status, x.changed, now, days)) r.stale++;
  });
  return r;
}

function responsesSheet_() { return SpreadsheetApp.getActiveSpreadsheet().getSheets()[0]; }

function colIndex_(sh, title) {
  var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
  var i = h.indexOf(title);
  if (i < 0) throw new Error('Нет колонки «' + title + '». Запустите setup().');
  return i + 1;
}

function letter_(n) { var s = ''; while (n > 0) { var m = (n - 1) % 26; s = String.fromCharCode(65 + m) + s; n = Math.floor((n - 1) / 26); } return s; }

function nextNumber_() {
  var p = PropertiesService.getDocumentProperties();
  var n = Number(p.getProperty('lastReportNumber') || 0) + 1;
  p.setProperty('lastReportNumber', String(n));
  return n;
}

function stamp_(sh, row) {
  var cN = colIndex_(sh, '№'), cS = colIndex_(sh, 'Статус'), cD = colIndex_(sh, 'Дата изменения'), cC = colIndex_(sh, 'Статус изменён (дата)');
  if (!sh.getRange(row, cC).getValue()) sh.getRange(row, cC).setValue(new Date());
  if (!sh.getRange(row, cN).getValue()) sh.getRange(row, cN).setValue(formatNumber(nextNumber_()));
  if (!sh.getRange(row, cS).getValue()) sh.getRange(row, cS).setValue('новый');
  if (!sh.getRange(row, cD).getValue()) sh.getRange(row, cD).setValue(new Date());
}

// Установленный триггер «при отправке формы».
function onFormSubmit(e) {
  var lock = LockService.getDocumentLock();
  lock.waitLock(30000);
  try { stamp_(responsesSheet_(), e.range.getRow()); rebuildPeople_(); } finally { lock.releaseLock(); }
}

// Ежедневная сверка: у каждой строки с отметкой времени есть номер и статус.
function fillMissing() {
  var lock = LockService.getDocumentLock();
  lock.waitLock(30000);
  try {
    var sh = responsesSheet_();
    var last = sh.getLastRow();
    for (var r = 2; r <= last; r++) {
      if (sh.getRange(r, 1).getValue()) stamp_(sh, r);
    }
    rebuildPeople_();
  } finally { lock.releaseLock(); }
}

// Простой триггер: правка рабочих колонок обновляет «Дату изменения».
function onEdit(e) {
  var sh = e.range.getSheet();
  if (sh.getIndex() !== 1 || e.range.getRow() < 2) return;
  var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
  var cols = CFG.EDIT_TRIGGERS.map(function (t) { return h.indexOf(t) + 1; });
  if (cols.indexOf(e.range.getColumn()) < 0) return;
  var now = new Date();
  sh.getRange(e.range.getRow(), h.indexOf('Дата изменения') + 1).setValue(now);
  if (e.range.getColumn() === h.indexOf('Статус') + 1) sh.getRange(e.range.getRow(), h.indexOf('Статус изменён (дата)') + 1).setValue(now);
}

// Один раз: колонки, списки, подсветка, сводка, триггеры.
function setup() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sh = responsesSheet_();
  var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
  CFG.EXTRA.forEach(function (t) {
    if (h.indexOf(t) < 0) { sh.getRange(1, sh.getLastColumn() + 1).setValue(t); h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0]; }
  });
  if (!emailColIndex_(h)) sh.getRange(1, sh.getLastColumn() + 1).setValue(CFG.EMAIL_TITLES[1]);
  sh.setFrozenRows(1);
  var cS = colIndex_(sh, 'Статус'), cD = colIndex_(sh, 'Дата изменения'), cN = colIndex_(sh, '№');

  var rule = SpreadsheetApp.newDataValidation().requireValueInList(CFG.STATUSES, true).setAllowInvalid(false).build();
  sh.getRange(2, cS, CFG.MAX_ROWS, 1).setDataValidation(rule);

  var rng = sh.getRange(2, 1, CFG.MAX_ROWS, sh.getLastColumn());
  var sL = letter_(cS), dL = letter_(cD);
  var cC = colIndex_(sh, 'Статус изменён (дата)'), cR = colIndex_(sh, 'Ответ отправлен (дата)');
  var chL = letter_(cC), rL = letter_(cR), nL = letter_(cN);
  // Оранжевым: есть номер, а ответ автору не отправлен или отправлен раньше последней смены статуса.
  var reply = SpreadsheetApp.newConditionalFormatRule()
    .whenFormulaSatisfied('=AND($' + nL + '2<>"",OR($' + rL + '2="",AND($' + chL + '2<>"",$' + rL + '2<$' + chL + '2)))')
    .setBackground('#fce5cd').setRanges([rng]).build();
  var openCond = 'OR($' + sL + '2="",' + CFG.OPEN.map(function (s) { return '$' + sL + '2="' + s + '"'; }).join(',') + ')';
  var stale = SpreadsheetApp.newConditionalFormatRule()
    .whenFormulaSatisfied('=AND($A2<>"",' + openCond + ',OR($' + dL + '2="",TODAY()-$' + dL + '2>' + CFG.STALE_DAYS + '))')
    .setBackground('#f4c7c3').setRanges([rng]).build();
  var done = SpreadsheetApp.newConditionalFormatRule()
    .whenFormulaSatisfied('=OR($' + sL + '2="исправлено",$' + sL + '2="дубль",$' + sL + '2="не баг")')
    .setBackground('#d9ead3').setRanges([rng]).build();
  sh.setConditionalFormatRules([reply, stale, done]);

  var sum = ss.getSheetByName('Сводка') || ss.insertSheet('Сводка');
  sum.clear();
  var q = "'" + sh.getName() + "'!";
  var sR = q + sL + '2:' + sL, dR = q + dL + '2:' + dL, nR = q + letter_(cN) + '2:' + letter_(cN), aR = q + 'A2:A';
  var staleSum = CFG.OPEN.map(function (s) { return 'COUNTIFS(' + sR + ',"' + s + '",' + dR + ',"<"&(TODAY()-' + CFG.STALE_DAYS + '))'; }).join('+');
  var cL = q + chL + '2:' + chL, pR = q + rL + '2:' + rL;
  sum.getRange(1, 1, 8, 2).setFormulas([
    ['Получено', '=COUNTA(' + aR + ')'],
    ['Обработано (исправлено, дубль, не баг)', '=COUNTIF(' + sR + ',"исправлено")+COUNTIF(' + sR + ',"дубль")+COUNTIF(' + sR + ',"не баг")'],
    ['Висит', '=B1-B2'],
    ['Висит дольше ' + CFG.STALE_DAYS + ' дней', '=' + staleSum],
    ['Без номера (должно быть 0)', '=B1-COUNTA(' + nR + ')'],
    ['Отложено', '=COUNTIF(' + sR + ',"отложено")'],
    ['Ждут ответа автору', '=COUNTIFS(' + nR + ',"<>",' + pR + ',"")+SUMPRODUCT((' + nR + '<>"")*(' + pR + '<>"")*(' + pR + '<' + cL + '))'],
    ['На дату', '=TODAY()']
  ]);
  sum.autoResizeColumns(1, 2);

  // Служебные листы (формулы; заголовки копируются из листа ответов). Содержат ники — только владельцу.
  var cNick = colIndex_(sh, CFG.NICK), cCat = colIndex_(sh, 'Категория');
  var qs = buildQueries(sh.getName(), { last: letter_(sh.getLastColumn()), num: nL, nick: letter_(cNick), status: sL });
  [['По номеру', qs.byNumber], ['По автору', qs.byAuthor]].forEach(function (x) {
    var t = ss.getSheetByName(x[0]) || ss.insertSheet(x[0]);
    t.clear();
    sh.getRange(1, 1, 1, sh.getLastColumn()).copyTo(t.getRange(1, 1), SpreadsheetApp.CopyPasteType.PASTE_VALUES, false);
    t.getRange(2, 1).setFormula(x[1]);
    t.setFrozenRows(1);
  });
  var who = ss.getSheetByName('Кто что ждёт') || ss.insertSheet('Кто что ждёт');
  who.clear();
  who.getRange(1, 1).setFormula(qs.pending);
  who.autoResizeColumns(1, 2);
  rebuildPeople_();

  sh.getRange(2, cCat, CFG.MAX_ROWS, 1).setDataValidation(
    SpreadsheetApp.newDataValidation().requireValueInList(CFG.CATEGORIES, true).setAllowInvalid(true).build());
  if (!sh.getFilter()) sh.getRange(1, 1, Math.max(sh.getLastRow(), 2), sh.getLastColumn()).createFilter();

  ScriptApp.getProjectTriggers().forEach(function (t) { ScriptApp.deleteTrigger(t); });
  ScriptApp.newTrigger('onFormSubmit').forSpreadsheet(ss).onFormSubmit().create();
  ScriptApp.newTrigger('fillMissing').timeBased().everyDays(1).atHour(9).create();
  fillMissing();
}

if (typeof module !== 'undefined') { module.exports = { formatNumber: formatNumber, isStale: isStale, needsReply: needsReply, buildQueries: buildQueries, groupPeople: groupPeople, emailColIndex_: emailColIndex_, summarize: summarize, letter_: letter_, CFG: CFG }; }
