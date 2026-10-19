// Проверка чистых функций Code.gs: node -e "require('module')._extensions['.gs']=require('module')._extensions['.js']; require('./scripts/tests/report-sheet-test.js')"
const assert = require('assert');
const c = require('../report-sheet/Code.gs');
assert.strictEqual(c.formatNumber(1), 'R-001');
assert.strictEqual(c.formatNumber(12), 'R-012');
assert.strictEqual(c.formatNumber(999), 'R-999');
assert.strictEqual(c.formatNumber(1000), 'R-1000');
assert.strictEqual(c.letter_(1), 'A');
assert.strictEqual(c.letter_(26), 'Z');
assert.strictEqual(c.letter_(27), 'AA');
assert.strictEqual(c.letter_(53), 'BA');
const now = new Date('2026-10-18T12:00:00Z');
const d = (s) => new Date(s);
assert.strictEqual(c.isStale('новый', d('2026-10-01'), now, 7), true);
assert.strictEqual(c.isStale('новый', d('2026-10-15'), now, 7), false);
assert.strictEqual(c.isStale('в работе', null, now, 7), true);
assert.strictEqual(c.isStale('исправлено', d('2026-01-01'), now, 7), false);
assert.strictEqual(c.isStale('дубль', null, now, 7), false);
const r = c.summarize([
  { status: 'новый', number: 'R-001', changed: d('2026-10-01') },
  { status: 'исправлено', number: 'R-002', changed: d('2026-10-02') },
  { status: 'нужны данные', number: 'R-003', changed: d('2026-10-17') },
  { status: '', number: '', changed: null },
], now, 7);
const noWishes = { total: 0, open: 0, implemented: 0, rejected: 0, needsOwner: 0 };
assert.deepStrictEqual(r, { received: 4, noNumber: 1, done: 1, open: 3, stale: 2, postponed: 0, wishes: noWishes });
// «отложено»: не красится как зависшее, считается висящим, учитывается отдельно
assert.strictEqual(c.isStale('отложено', d('2026-01-01'), now, 7), false);
assert.ok(c.CFG.STATUSES.includes('отложено'));
assert.deepStrictEqual(c.summarize([{ status: 'отложено', number: 'R-004', changed: d('2026-01-01') }], now, 7),
  { received: 1, noNumber: 0, done: 0, open: 1, stale: 0, postponed: 1, wishes: noWishes });
// пожелания: считаются отдельно и не смешиваются с багами; «пожелание / идея» из формы опознаётся
const sw = c.summarize([
  { status: 'новый', number: 'R-001', changed: d('2026-10-01'), category: 'краш/вылет' },
  { status: 'идея', number: 'R-002', changed: d('2026-10-01'), category: 'пожелание / идея' },
  { status: 'реализовано', number: 'R-003', changed: d('2026-10-01'), category: 'пожелание' },
  { status: 'отклонено', number: 'R-004', changed: d('2026-10-01'), category: ' Пожелание' },
  { status: 'нужно решение владельца', number: 'R-005', changed: d('2026-10-17'), category: 'пожелание' },
  { status: 'отложено', number: 'R-006', changed: d('2026-10-01'), category: 'пожелание' },
  { status: 'идея', number: '', changed: null, category: 'пожелание' },
], now, 7);
assert.deepStrictEqual(sw, { received: 1, noNumber: 1, done: 0, open: 1, stale: 1, postponed: 0,
  wishes: { total: 6, open: 4, implemented: 1, rejected: 1, needsOwner: 1 } });
assert.strictEqual(c.isStale('идея', d('2026-10-01'), now, 7), true, 'идея зависает как открытый статус');
assert.strictEqual(c.isStale('реализовано', d('2026-01-01'), now, 7), false);
assert.strictEqual(c.isStale('отклонено', null, now, 7), false);
assert.strictEqual(c.isStale('нужно решение владельца', d('2026-10-17'), now, 7), false);
// статусы по категории
assert.deepStrictEqual(c.CFG.WISH_STATUSES, ['идея', 'принята', 'в реализации', 'реализовано', 'нужно решение владельца', 'отклонено', 'отложено']);
['идея', 'принята', 'в реализации', 'реализовано', 'нужно решение владельца', 'отклонено', 'отложено', ''].forEach((x) => assert.ok(c.statusValid('пожелание / идея', x), x));
['новый', 'принят', 'в работе', 'исправлено', 'не баг', 'дубль'].forEach((x) => assert.ok(!c.statusValid('пожелание', x), 'пожелание: ' + x));
['новый', 'исправлено', 'отложено', 'нужны данные'].forEach((x) => assert.ok(c.statusValid('краш/вылет', x) && c.statusValid('', x), x));
['идея', 'реализовано', 'отклонено', 'в реализации'].forEach((x) => assert.ok(!c.statusValid('краш/вылет', x), 'баг: ' + x));
assert.ok(c.CFG.ALL_STATUSES.length === 14 && new Set(c.CFG.ALL_STATUSES).size === 14, 'объединённый список без повторов');
assert.ok(c.CFG.WISH_STATUSES.every((x) => c.CFG.ALL_STATUSES.includes(x)) && c.CFG.STATUSES.every((x) => c.CFG.ALL_STATUSES.includes(x)));
// ответ автору: нет номера — не нужен; не отправлен — нужен; отправлен раньше смены статуса — нужен; позже — нет
assert.strictEqual(c.needsReply('', null, null), false);
assert.strictEqual(c.needsReply('R-001', d('2026-10-10'), null), true);
assert.strictEqual(c.needsReply('R-001', d('2026-10-10'), d('2026-10-09')), true);
assert.strictEqual(c.needsReply('R-001', d('2026-10-10'), d('2026-10-11')), false);
assert.strictEqual(c.needsReply('R-001', null, d('2026-10-11')), false);
// новые колонки на месте
['Описание для публичной страницы', 'Пояснение для публичной страницы', 'Ответ автору', 'Ответ отправлен (дата)']
  .forEach((t) => assert.ok(c.CFG.EXTRA.includes(t), t));
// служебные листы: сортировки и «кто что ждёт»
const q = c.buildQueries('Ответы', { last: 'Z', num: 'F', nick: 'B', status: 'G' });
assert.ok(q.byNumber.includes('order by F') && !q.byNumber.includes('order by B'));
assert.ok(q.byAuthor.includes('order by B, F'));
assert.ok(q.pending.includes("G <> 'исправлено'") && q.pending.includes("G <> 'не баг'") && q.pending.includes('group by B'));
assert.ok(q.pending.includes("G <> 'реализовано'") && q.pending.includes("G <> 'отклонено'"), 'закрытые пожелания не считаются открытыми');
assert.ok(c.CFG.EXTRA.includes('Категория') && c.CFG.CATEGORIES.length === 8 && c.CFG.CATEGORIES.includes('пожелание'));
assert.strictEqual(c.CFG.BUG_CATEGORIES.length, 7);
// «По людям»: группировка по адресу (без регистра), без адреса — по нику; сначала у кого больше открытых
const d2 = (x) => new Date(x);
const people = c.groupPeople([
  { email: 'Masha@example.com', nick: 'Маша', number: 'R-002', status: 'новый', statusChanged: d2('2026-10-10'), replySent: null },
  { email: 'masha@example.com', nick: 'Маша К', number: 'R-001', status: 'исправлено', statusChanged: d2('2026-10-09'), replySent: d2('2026-10-12') },
  { email: '', nick: 'Петя', number: 'R-003', status: 'исправлено', statusChanged: d2('2026-10-09'), replySent: d2('2026-10-10') },
  { email: '', nick: 'петя', number: 'R-004', status: 'не баг', statusChanged: d2('2026-10-09'), replySent: d2('2026-10-11') },
  { email: '', nick: 'Вова', number: '', status: '', statusChanged: null, replySent: null },
]);
assert.strictEqual(people.length, 2);
assert.deepStrictEqual(people[0].slice(0, 5), ['Masha@example.com', 'Маша', 2, 1, 'R-001 (исправлено), R-002 (новый)']);
assert.strictEqual(people[0][6], 'да');
assert.deepStrictEqual(people[1].slice(2, 4).concat(people[1][6]), [2, 0, 'нет']);
assert.strictEqual(+people[1][5], +d2('2026-10-11'));
assert.strictEqual(c.emailColIndex_(['a', 'Адрес электронной почты']), 2);
assert.strictEqual(c.emailColIndex_(['a']), 0);
// ---- форма и bootstrap с заглушками (в настоящем Google не запускалось) ----
function fakeForm(id) {
  const f = { id, items: [], calls: [], dest: null };
  const item = (kind) => { const it = { kind, title: '', help: '', required: false, choices: null };
    it.setTitle = (t) => { it.title = t; return it; }; it.setHelpText = (t) => { it.help = t; return it; };
    it.setRequired = (r) => { it.required = r; return it; }; it.setChoiceValues = (v) => { it.choices = v; return it; };
    it.getTitle = () => it.title; it.asMultipleChoiceItem = () => { if (kind !== 'choice') throw new Error('not choice'); return it; };
    it.asListItem = () => { if (kind !== 'list') throw new Error('not list'); return it; };
    it.getChoices = () => (it.choices || []).map((v) => ({ getValue: () => v }));
    f.items.push(it); return it; };
  f.addTextItem = () => item('text'); f.addParagraphTextItem = () => item('paragraph');
  f.addListItem = () => item('list'); f.addMultipleChoiceItem = () => item('choice');
  ['setTitle', 'setDescription', 'setConfirmationMessage', 'setAllowResponseEdits', 'setLimitOneResponsePerUser', 'setProgressBar', 'setCollectEmail']
    .forEach((m) => { f[m] = (v) => { f.calls.push([m, v]); return f; }; });
  f.getItems = () => f.items.slice(); f.getId = () => id; f.getPublishedUrl = () => 'PUB'; f.getEditUrl = () => 'EDIT';
  return f;
}
const forms = {}; let created = 0;
global.Logger = { log: () => {} };
global.FormApp = { create: () => { created++; return (forms['f' + created] = fakeForm('f' + created)); }, openById: (id) => { if (!forms[id]) throw new Error('no form'); return forms[id]; } };
const store = {};
global.PropertiesService = { getDocumentProperties: () => ({ getProperty: (k) => store[k] || null, setProperty: (k, v) => { store[k] = v; } }) };
const r1 = c.ensureForm_();
assert.strictEqual(r1.created, true);
const it = r1.form.items;
assert.strictEqual(it.length, c.FORM.FIELDS.length);
assert.strictEqual(it.filter((x) => x.required).length, 5);   // ник, версия, где играли, что не работает, категория
assert.deepStrictEqual(it.find((x) => x.title === 'Категория').choices, c.CFG.BUG_CATEGORIES.concat(['пожелание / идея']));
assert.ok(it.find((x) => x.title === 'Категория').required, 'категория обязательна в новой форме');
assert.ok(it.some((x) => x.title === 'Что не работает / что хотите видеть' && x.required), 'общий вопрос переименован');
assert.ok(!it.some((x) => x.title === 'Что не работает'));
assert.ok(it.find((x) => x.title === 'Категория').help.includes('что хотите увидеть и зачем'));
assert.ok(it.every((x) => x.kind !== 'file'), 'вопроса «Загрузка файла» нет (FormApp не умеет)');
const call = (f, m) => f.calls.filter((x) => x[0] === m).map((x) => x[1]);
assert.deepStrictEqual(call(r1.form, 'setConfirmationMessage'), ['Спасибо! Номер отчёта и ответ пришлём в чат в течение суток; статус — на странице report-status']);
assert.ok(call(r1.form, 'setDescription')[0].includes('Адрес виден только хосту, не публикуется; нужен вход в Google.'));
assert.deepStrictEqual(call(r1.form, 'setCollectEmail'), [true]);
// повторный запуск: та же форма, второй не создаётся, сбор адресов заново не трогается
const r2 = c.ensureForm_();
assert.strictEqual(r2.created, false); assert.strictEqual(created, 1); assert.strictEqual(r2.form.items.length, it.length);
assert.deepStrictEqual(call(r2.form, 'setCollectEmail'), [true]);
// форма пропала → создаётся новая
delete forms.f1; assert.strictEqual(c.ensureForm_().created, true);
// ---- пожелания в уже существующей форме (старая форма: прежнее название вопроса, 7 категорий) ----
{
  const old = fakeForm('old');
  c.addFormItem_(old, { title: 'Что не работает', type: 'paragraph', required: true, help: 'старое' });
  c.addFormItem_(old, { title: 'Категория', type: 'choice', choices: c.CFG.BUG_CATEGORIES });
  c.addFormItem_(old, { title: 'Версия сборки', type: 'text', required: true });
  c.addFormItem_(old, { title: 'Мод или предмет', type: 'text' });
  assert.strictEqual(old.items.find((x) => x.title === 'Категория').required, false);
  const res = c.applyWish_(old);
  assert.deepStrictEqual(res, { question: true, category: true });
  const cat = old.items.find((x) => x.title === 'Категория');
  assert.deepStrictEqual(cat.choices, c.CFG.BUG_CATEGORIES.concat(['пожелание / идея']));
  const q1 = old.items.find((x) => x.title === c.WISH_QUESTION.title);
  assert.ok(q1 && q1.required && q1.help.includes('что хотели бы увидеть и зачем'), 'переименован, обязательность сохранена');
  assert.strictEqual(cat.required, true, 'категория стала обязательной в существующей форме');
  assert.strictEqual(old.items.find((x) => x.title === 'Мод или предмет').required, false, 'прочие вопросы не тронуты');
  assert.strictEqual(old.items.length, 4, 'вопросов не прибавилось');
  const snap = JSON.stringify(old.items);
  c.applyWish_(old); c.applyWish_(old);
  assert.strictEqual(JSON.stringify(old.items), snap, 'повторный запуск идемпотентен');
  assert.strictEqual(cat.choices.filter((x) => x === 'пожелание / идея').length, 1, 'пункт не дублируется');
  // вопроса «Категория» нет → понятная ошибка
  const noCat = fakeForm('nocat'); c.addFormItem_(noCat, { title: 'Версия сборки', type: 'text' });
  assert.throws(() => c.applyWish_(noCat), /нет вопроса «Категория»/);
  // категория — выпадающий список, а не «один из списка»
  const lst = fakeForm('lst'); c.addFormItem_(lst, { title: 'Категория', type: 'list', choices: ['другое'] });
  c.applyWish_(lst); assert.deepStrictEqual(lst.items[0].choices, ['другое', 'пожелание / идея']);
  // addWishOption() берёт форму по formId; без bootstrap — ошибка
  forms.old = old; store.formId = 'old';
  c.addWishOption();
  delete store.formId;
  assert.throws(() => c.addWishOption(), /bootstrap/);
  // ensureForm_ на существующей форме сам добавляет пункт (повторный bootstrap)
  const old2 = fakeForm('old2'); forms.old2 = old2; store.formId = 'old2';
  c.addFormItem_(old2, { title: 'Категория', type: 'choice', choices: c.CFG.BUG_CATEGORIES });
  const re = c.ensureForm_();
  assert.strictEqual(re.created, false);
  assert.ok(old2.items[0].choices.includes('пожелание / идея'), 'bootstrap добавляет пункт в существующую форму');
  delete store.formId;
}
assert.deepStrictEqual(c.pickNewSheet([1, 2], [{ id: 1 }, { id: 2 }]), null);
assert.deepStrictEqual(c.pickNewSheet([1], [{ id: 1 }, { id: 7, name: 'x' }]), { id: 7, name: 'x' });
const sr = c.startRows('PUB', 'EDIT');
assert.strictEqual(sr[0][1], 'PUB'); assert.strictEqual(sr[1][1], 'EDIT');
assert.ok(sr.some((x) => /Подтверждённые/.test(x[1])) && sr.some((x) => /Загрузка файла/.test(x[1])));
assert.ok(sr.some((x) => x[0] === 'Пожелания' && /решение владельца/.test(x[1])));
assert.strictEqual(typeof c.bootstrap, 'function');
// ---- setup() на листе-«Таблице»: createFilter бросает исключение, остальное должно дойти до конца ----
function runSetup(failFilter) {
  const triggers = []; const logs = []; const written = []; const order = []; const formulas = [];
  const head = ['Отметка времени', 'Адрес электронной почты', c.CFG.NICK].concat(c.CFG.EXTRA);
  const chain = () => new Proxy(function () {}, { get: (_, k) => (k === 'then' ? undefined : chain()), apply: () => chain() });
  const rangeOf = (r, col) => ({
    setFormulas: (v) => { order.push('setFormulas'); formulas.push({ col, v }); },
    getValues: () => [head.slice()], setValue: (v) => { if (r === 1) head[col - 1] = v; return rangeOf(r, col); },
    getValue: () => head[0],
    createFilter: () => { if (failFilter) throw new Error('Нельзя применить фильтр к диапазону, который частично пересекается с таблицей'); },
    setValues: (v) => { written.push(v); order.push('setValues'); if (col === 1 && v.length === 13) formulas.push({ labels: v }); }, setWrap: () => {}, setDataValidation: () => {}, setFormula: (f) => { order.push('setFormula'); formulas.push({ single: f }); }, copyTo: () => {},
  });
  const sheet = {
    getLastColumn: () => head.length, getLastRow: () => 1, getName: () => 'Ответы', getSheetId: () => 1,
    getRange: (r, c) => rangeOf(r, c), getFilter: () => null, setFrozenRows: () => {}, setConditionalFormatRules: () => {},
    clear: () => {}, setColumnWidth: () => {}, autoResizeColumns: () => {}, getDataRange: () => ({ getValues: () => [head.slice()] }),
  };
  const ss = { setSpreadsheetLocale: (l) => { order.push('locale:' + l); }, setActiveSheet: () => {}, moveActiveSheet: () => {}, getSheets: () => [sheet], getSheetByName: () => sheet, insertSheet: () => sheet };
  global.SpreadsheetApp = { getActiveSpreadsheet: () => ss, newDataValidation: chain, newConditionalFormatRule: chain, CopyPasteType: { PASTE_VALUES: 0 }, flush: () => { order.push('flush'); } };
  const trig = (name) => { const t = { name }; const b = new Proxy(t, { get: (o, k) => (k === 'create' ? () => triggers.push(name) : () => b) }); return b; };
  global.ScriptApp = { getProjectTriggers: () => [], deleteTrigger: () => {}, newTrigger: trig };
  global.LockService = { getDocumentLock: () => ({ waitLock: () => {}, releaseLock: () => {} }) };
  global.Logger = { log: (m) => logs.push(m) };
  global.PropertiesService = { getDocumentProperties: () => ({ getProperty: () => null, setProperty: () => {} }) };
  return { triggers, logs, written, ss, order, formulas, head };
}
{
  const env = runSetup(true);
  c.setup();   // не должен бросить
  c.writeStart_(env.ss, { getPublishedUrl: () => 'PUB', getEditUrl: () => 'EDIT' });
  assert.ok(env.written.some((v) => v.some((r) => r[1] === 'PUB')), '«Начало» записано');
  assert.deepStrictEqual(env.triggers, ['onFormSubmit', 'fillMissing', 'sendReplies']);
  c.setup();   // повторный запуск (идемпотентность) не падает
  assert.ok(env.logs.some((m) => /фильтр/.test(m) && /таблиц/.test(m)), 'причина записана в Logger');
}
// ---- локаль, подписи «Сводки», IFERROR вокруг QUERY ----
{
  const env = runSetup(false);
  c.setup();
  const iLoc = env.order.indexOf('locale:en_US');
  assert.ok(iLoc >= 0, 'локаль en_US установлена');
  assert.ok(env.order.slice(0, iLoc).every((x) => !/^setFormula/.test(x)), 'локаль — до первой записи формул');
  assert.ok(env.logs.some((m) => /en_US/.test(m)), 'смена локали записана в Logger');
  const labels = env.formulas.find((x) => x.labels).labels.map((r) => r[0]);
  assert.strictEqual(labels.length, 13);
  assert.ok(labels.filter((t) => /^Баги/.test(t)).length === 5 && labels.filter((t) => /^Пожелания/.test(t)).length === 5, 'отдельные счётчики багов и пожеланий');
  assert.ok(labels.includes('Пожелания: всего') && labels.includes('Пожелания: реализовано') && labels.some((t) => /^Пожелания: открыто/.test(t)));
  assert.ok(labels.every((t) => !String(t).startsWith('=')), 'подписи — значения, не формулы');
  const colA = env.formulas.filter((x) => x.col === 1 && x.v);
  assert.strictEqual(colA.length, 0, 'в колонку A формулы не пишутся');
  const colB = env.formulas.find((x) => x.col === 2);
  const f = colB.v.map((r) => r[0]);
  const catL = c.letter_(env.head.indexOf('Категория') + 1);
  assert.ok(f[0].includes('-B6'), 'баги = все − пожелания');
  assert.ok([1, 3, 4].every((i) => f[i].includes(',"<>пожелание*"')) && f[2] === '=B1-B2', 'формулы багов исключают пожелания');
  assert.ok(f[5].includes(catL + '2:' + catL + ',"пожелание*"'), 'пожелания всего — по категории');
  assert.ok(f[7].includes('"реализовано"') && f[7].includes('"пожелание*"') && f[6] === '=B6-B8-B9' && f[8].includes('"отклонено"'));
  assert.ok(f.slice(5, 10).every((x) => !x.includes('<>пожелание')), 'формулы пожеланий не смешаны с багами');
  assert.ok(colB && colB.v.length === 13 && colB.v.every((r) => r.length === 1 && r[0].startsWith('=')), 'колонка B — формулы');
  const singles = env.formulas.filter((x) => x.single).map((x) => x.single);
  assert.ok(singles.length >= 3 && singles.every((f) => /^=IFERROR\(QUERY\(/.test(f) && /, "Нет отчётов"\)$/.test(f)), 'QUERY обёрнут в IFERROR');
}
['byNumber', 'byAuthor', 'pending'].forEach((k) => assert.ok(/^=IFERROR\(QUERY\(/.test(q[k]) && q[k].endsWith('0), "Нет отчётов")'), k));
// ---- пересчёт: writeFormulas_ из onFormSubmit/fillMissing, диапазон QUERY растёт с числом колонок ----
{
  const env = runSetup(false);
  c.setup();
  const queryOf = () => env.formulas.filter((x) => x.single).map((x) => x.single);
  const before = queryOf();
  assert.ok(before.every((f) => f.includes('!A2:' + c.letter_(env.head.length))), 'диапазон = число колонок');
  env.order.length = 0; env.formulas.length = 0;
  c.onFormSubmit({ range: { getRow: () => 2 } });
  assert.ok(env.order.includes('setFormulas') && env.order.filter((x) => x === 'setFormula').length === 3, 'onFormSubmit перезаписывает формулы');
  assert.strictEqual(env.order[env.order.length - 1], 'flush', 'flush в конце');
  env.order.length = 0; env.formulas.length = 0;
  c.fillMissing();
  assert.ok(env.order.includes('setFormulas') && env.order[env.order.length - 1] === 'flush', 'fillMissing тоже');
  // ручной вопрос «Файлы» → 28 колонок: диапазон пересобирается
  while (env.head.length < 28) env.head.push('Файлы ' + env.head.length);
  env.formulas.length = 0;
  c.onFormSubmit({ range: { getRow: () => 2 } });
  const after = queryOf();
  assert.strictEqual(after.length, 3);
  assert.ok(after.every((f) => f.includes('!A2:AB')), 'при 28 колонках диапазон A2:AB');
  // идемпотентность: повторный вызов даёт те же формулы
  env.formulas.length = 0; c.writeFormulas_();
  assert.deepStrictEqual(queryOf(), after);
  // сбой пересчёта не ломает onFormSubmit
  const bad = runSetup(false); global.SpreadsheetApp.flush = () => { throw new Error('сбой пересчёта'); };
  c.onFormSubmit({ range: { getRow: () => 2 } });
  assert.ok(bad.logs.some((m) => /writeFormulas_/.test(m)), 'сбой записан в Logger');
}
// ---- bootstrap: сбой setup() не маскируется сбоем writeStart_, а сбой writeStart_ после удачного setup() не глотается ----
{
  const form = { getPublishedUrl: () => 'PUB', getEditUrl: () => 'EDIT' };
  let env = runSetup(false);
  const boom = new Error('setup упал по-настоящему');
  global.ScriptApp.getProjectTriggers = () => { throw boom; };          // setup() падает на первом же шаге
  env.ss.getSheetByName = () => { throw new Error('и «Начало» тоже не записалось'); };
  assert.throws(() => c.setupThenStart_(env.ss, form), (e) => e === boom, 'наружу — исходная ошибка setup()');
  assert.ok(env.logs.some((m) => /Начало/.test(m)), 'вторичный сбой записан в Logger');
  env = runSetup(false);                                                 // setup() проходит, writeStart_ падает
  env.ss.getSheetByName = () => { throw new Error('нет доступа к листу'); };
  assert.throws(() => c.setupThenStart_(env.ss, form), /нет доступа к листу/, 'сбой «Начала» после удачного setup() не глотается');
  env = runSetup(false);                                                 // всё хорошо
  c.setupThenStart_(env.ss, form);
  assert.ok(env.written.some((v) => v.some((r) => r[1] === 'PUB')), '«Начало» записано');
}
// ---- защита от формул в тексте из формы: «По людям» ----
['=IMPORTDATA("x")', '+1', '-1', '@a', '\tx', '\rx'].forEach((v) => assert.strictEqual(c.safeText_(v), "'" + v, JSON.stringify(v)));
['Маша', 'R-001 (новый)', '', 'a=b'].forEach((v) => assert.strictEqual(c.safeText_(v), v));
assert.strictEqual(c.safeText_(3), 3);
{
  const head = ['Отметка времени', 'Адрес электронной почты', c.CFG.NICK, '№', 'Статус', 'Статус изменён (дата)', 'Ответ отправлен (дата)'];
  const data = [
    [new Date('2026-10-01'), '=IMPORTDATA("x")', '+1', 'R-001', 'новый', new Date('2026-10-02'), ''],
    [new Date('2026-10-01'), 'a@b.c', '@a', 'R-002', 'новый', new Date('2026-10-02'), ''],
  ];
  const written = []; const formats = [];
  const sheet = {
    getLastColumn: () => head.length, getLastRow: () => 3, getName: () => 'Ответы', getSheetId: () => 1,
    getRange: (r, col, nr) => ({ getValue: () => 'Timestamp', getValues: () => (r === 1 ? [head] : data), setValues: (v) => { written.push(v); }, setNumberFormat: (f) => { formats.push(f); } }),
    clear: () => {}, setFrozenRows: () => {}, autoResizeColumns: () => {},
  };
  const ss = { getSheetByName: () => sheet, insertSheet: () => sheet, getSheets: () => [sheet] };
  global.SpreadsheetApp = { getActiveSpreadsheet: () => ss };
  global.PropertiesService = { getDocumentProperties: () => ({ getProperty: () => null }) };
  c.rebuildPeople_();
  const body = written[written.length - 1];
  const cells = [].concat.apply([], body).filter((x) => typeof x === 'string');
  assert.ok(cells.includes("'=IMPORTDATA(\"x\")") && cells.includes("'+1") && cells.includes("'@a"), 'опасные значения ушли с апострофом');
  assert.ok(!cells.some((x) => /^[=+\-@\t\r]/.test(x)), 'нет строк, начинающихся с формульного символа');
  assert.ok(formats.includes('@'), 'формат «текст» тоже стоит');
}
// ---- письмо автору после отправки отчёта (MailApp) ----
{
  const head = ['Timestamp', 'Адрес электронной почты', c.CFG.NICK].concat(c.CFG.EXTRA);
  const ci = (t) => head.indexOf(t);
  const grid = [head.slice()];
  const addRow = (email) => { const r = head.map(() => ''); r[0] = new Date('2026-10-18'); r[1] = email; r[2] = 'Ник=секрет'; grid.push(r); return grid.length; };
  const sheet = {
    getLastColumn: () => head.length, getLastRow: () => grid.length, getName: () => 'Ответы', getSheetId: () => 1,
    getRange: (r, col, nr, nc) => { nr = nr || 1; nc = nc || 1; return {
      getValue: () => (grid[r - 1] || [])[col - 1] === undefined ? '' : grid[r - 1][col - 1],
      setValue: (v) => { grid[r - 1][col - 1] = v; },
      getValues: () => { const o = []; for (let i = 0; i < nr; i++) { const row = []; for (let j = 0; j < nc; j++) row.push((grid[r - 1 + i] || [])[col - 1 + j] === undefined ? '' : grid[r - 1 + i][col - 1 + j]); o.push(row); } return o; },
      setValues: () => {}, setNumberFormat: () => {}, setFormula: () => {}, setFormulas: () => {}, copyTo: () => {}, }; },
    clear: () => {}, setFrozenRows: () => {}, autoResizeColumns: () => {},
  };
  const ss = { getSheetByName: () => sheet, insertSheet: () => sheet, getSheets: () => [sheet] };
  const props = {};
  global.PropertiesService = { getDocumentProperties: () => ({ getProperty: (k) => props[k] || null, setProperty: (k, v) => { props[k] = v; } }) };
  global.LockService = { getDocumentLock: () => ({ waitLock: () => {}, releaseLock: () => {} }) };
  global.Logger = { log: () => {} };
  let quota = 100; let failSend = false; const sent = [];
  global.MailApp = { getRemainingDailyQuota: () => quota, sendEmail: (to, subj, body) => { if (failSend) throw new Error('Invalid email: ' + to); sent.push({ to, subj, body }); } };
  global.SpreadsheetApp = { getActiveSpreadsheet: () => ss, flush: () => {}, CopyPasteType: { PASTE_VALUES: 0 } };
  const submit = (row) => c.onFormSubmit({ range: { getRow: () => row } });

  // валидатор адреса
  ['a@b.co', 'x.y_z+t@mail.example.com'].forEach((v) => assert.ok(c.isValidEmail_(v), v));
  ['', 'a b@c.co', 'a@b.co\n', 'a@b', '=a@b.co', '+a@b.co', '-a@b.co', '@a.co', 'a@b.co,c@d.co', 'a@b.co\r', '<a@b.co>', 'a'.repeat(250) + '@b.co', null].forEach((v) => assert.ok(!c.isValidEmail_(v), JSON.stringify(v)));

  const r1 = addRow('friend@example.com');
  submit(r1);
  assert.strictEqual(sent.length, 1, 'письмо ушло один раз');
  assert.strictEqual(sent[0].to, 'friend@example.com');
  assert.strictEqual(sent[0].subj, 'Отчёт R-001 принят — Minecraft_moded');
  assert.ok(sent[0].body.includes('номер R-001') && sent[0].body.includes('report-status') && sent[0].body.includes(c.CFG.STATUS_URL));
  assert.ok(!/секрет|friend@/.test(sent[0].subj + sent[0].body), 'ни ника, ни адреса в письме нет');
  assert.ok(grid[r1 - 1][ci('Письмо отправлено (дата)')] instanceof Date, 'дата отправки записана');
  submit(r1); c.fillMissing();
  assert.strictEqual(sent.length, 1, 'повторно (колонка заполнена) и из fillMissing не шлётся');

  const r2 = addRow('нет адреса');   // плохой адрес
  submit(r2);
  assert.strictEqual(sent.length, 1, 'при плохом адресе не уходит');
  assert.ok(/Письмо не отправлено: нет корректного адреса/.test(grid[r2 - 1][ci('Заметка')]), 'причина в «Заметке»');
  assert.ok(!/нет адреса/.test(grid[r2 - 1][ci('Заметка')]));

  const r3 = addRow('=cmd@example.com');   // начинается с «=»
  submit(r3);
  assert.strictEqual(sent.length, 1, 'адрес с «=» не принимается');

  // fillMissing сам писем не шлёт, даже для строки без письма
  const r4 = addRow('later@example.com');
  c.fillMissing();
  assert.strictEqual(sent.length, 1, 'fillMissing писем не шлёт');
  assert.ok(grid[r4 - 1][ci('№')], 'но номер он присвоил');

  // пожелание: другое письмо («Спасибо за идею!»), статус «идея», нумерация общая
  const rw = addRow('wisher@example.com'); grid[rw - 1][ci('Категория')] = 'пожелание / идея';
  submit(rw);
  assert.strictEqual(sent.length, 2, 'письмо за идею ушло');
  assert.strictEqual(sent[1].to, 'wisher@example.com');
  assert.ok(/^Идея R-\d{3} принята/.test(sent[1].subj), sent[1].subj);
  assert.ok(sent[1].body.startsWith('Спасибо за идею! Номер R-') && sent[1].body.includes('статус — на странице report-status: ' + c.CFG.STATUS_URL), sent[1].body);
  assert.ok(!/секрет|wisher@/.test(sent[1].subj + sent[1].body), 'ни ника, ни адреса');
  assert.strictEqual(grid[rw - 1][ci('Статус')], 'идея', 'начальный статус пожелания — «идея»');
  assert.ok(/^R-\d{3}$/.test(grid[rw - 1][ci('№')]), 'номер R-### общий');
  assert.strictEqual(grid[r1 - 1][ci('Статус')], 'новый', 'у бага — «новый»');
  sent.length = 1;
  assert.deepStrictEqual(c.confirmationMail_('R-007', true), { subject: 'Идея R-007 принята — Minecraft_moded', body: 'Спасибо за идею! Номер R-007, статус — на странице report-status: ' + c.CFG.STATUS_URL + ' Хост ответит в чате.' });
  assert.ok(c.confirmationMail_('R-007').subject.startsWith('Отчёт R-007'));

  quota = 0; const r5 = addRow('quota@example.com'); submit(r5);
  assert.strictEqual(sent.length, 1, 'квота исчерпана — не шлётся');
  assert.ok(/лимит/.test(grid[r5 - 1][ci('Заметка')]));
  quota = 100;

  failSend = true; const r6 = addRow('boom@example.com');
  submit(r6);   // не должен бросить
  assert.ok(grid[r6 - 1][ci('№')], 'номер присвоен, несмотря на сбой отправки');
  assert.ok(/ошибка отправки/.test(grid[r6 - 1][ci('Заметка')]) && !/boom@/.test(grid[r6 - 1][ci('Заметка')]), 'причина в «Заметке», без адреса');
  assert.ok(!grid[r6 - 1][ci('Письмо отправлено (дата)')], 'при сбое дата отправки не ставится');
  failSend = false;

  c.CFG.SEND_CONFIRMATION = false;   // выключатель
  const r7 = addRow('off@example.com'); submit(r7);
  assert.strictEqual(sent.length, 1, 'при SEND_CONFIRMATION=false не шлётся');
  c.CFG.SEND_CONFIRMATION = true;
}
// ---- автоответ автору (sendReplies_, решение владельца В44 «б») ----
{
  const head = ['Timestamp', 'Адрес электронной почты', c.CFG.NICK].concat(c.CFG.EXTRA);
  const ci = (t) => head.indexOf(t);
  const grid = [head.slice()];
  const addRow = (email, reply, sentDate, num) => { const r = head.map(() => ''); r[0] = new Date('2026-10-18'); r[1] = email; r[2] = 'Ник=секрет'; r[ci('№')] = num || 'R-0' + (10 + grid.length); r[ci('Ответ автору')] = reply; r[ci('Ответ отправлен (дата)')] = sentDate || ''; grid.push(r); return grid.length; };
  const cell = (r, col) => (grid[r - 1] || [])[col - 1] === undefined ? '' : grid[r - 1][col - 1];
  const sheet = {
    getLastColumn: () => head.length, getLastRow: () => grid.length, getName: () => 'Ответы', getSheetId: () => 1,
    getRange: (r, col, nr, nc) => { nr = nr || 1; nc = nc || 1; return {
      getValue: () => cell(r, col), setValue: (v) => { grid[r - 1][col - 1] = v; },
      getValues: () => { const o = []; for (let i = 0; i < nr; i++) { const row = []; for (let j = 0; j < nc; j++) row.push(cell(r + i, col + j)); o.push(row); } return o; }, }; },
  };
  const ss = { getSheetByName: () => sheet, insertSheet: () => sheet, getSheets: () => [sheet] };
  global.PropertiesService = { getDocumentProperties: () => ({ getProperty: () => null, setProperty: () => {} }) };
  global.LockService = { getDocumentLock: () => ({ waitLock: () => {}, releaseLock: () => {} }) };
  const logs = []; global.Logger = { log: (m) => logs.push(m) };
  global.SpreadsheetApp = { getActiveSpreadsheet: () => ss };
  let quota = 100; const failTo = new Set(); const sent = [];
  global.MailApp = { getRemainingDailyQuota: () => quota, sendEmail: (...a) => { assert.strictEqual(a.length, 3, 'только адрес, тема, тело: без cc/bcc/вложений/replyTo'); if (failTo.has(a[0])) throw new Error('fail ' + a[0]); sent.push({ to: a[0], subj: a[1], body: a[2] }); } };
  const stampCol = ci('Ответ отправлен (дата)') + 1;   // номер колонки с 1

  const r1 = addRow('friend@example.com', 'Спасибо, исправим в 1.2.');
  c.sendReplies_();
  assert.strictEqual(sent.length, 1);
  assert.strictEqual(sent[0].to, 'friend@example.com');
  assert.strictEqual(sent[0].subj, 'Ответ по отчёту R-011 — Minecraft_moded', 'тема: только номер');
  assert.ok(!/секрет|friend@|исправим/.test(sent[0].subj), 'пользовательского текста в теме нет');
  assert.strictEqual(sent[0].body, 'Спасибо, исправим в 1.2.\n\nСтатус: ' + c.CFG.STATUS_URL, 'тело: ответ, пустая строка, ссылка');
  assert.ok(cell(r1, stampCol) instanceof Date, 'дата отправки записана');
  c.sendReplies_(); c.sendReplies_();
  assert.strictEqual(sent.length, 1, 'повторно не шлётся');
  grid[r1 - 1][ci('Ответ автору')] = 'Новый текст';
  c.sendReplies_();
  assert.strictEqual(sent.length, 1, 'правка текста после отправки не шлёт повторно');
  grid[r1 - 1][stampCol - 1] = '';
  c.sendReplies_();
  assert.strictEqual(sent.length, 2, 'после ручной очистки даты — шлётся снова');
  assert.ok(sent[1].body.startsWith('Новый текст'));

  // без отправки
  const before = sent.length;
  addRow('a@example.com', '[ЧЕРНОВИК] нужен ответ владельца');
  addRow('b@example.com', '   [черновик] с пробелами и регистром');
  addRow('b2@example.com', '[ ЧЕРНОВИК ] пробелы внутри скобок');
  addRow('b3@example.com', 'ЧЕРНОВИК: без скобок');
  addRow('b4@example.com', '\u00a0\u200b[\u0427EPHOBИК] неразрывный пробел, невидимый символ и латинские E, P, H, O');
  addRow('b5@example.com', 'DRAFT: english');
  addRow('c@example.com', '');
  addRow('d@example.com', '   ');
  addRow('e@example.com', 'уже ушло', new Date('2026-10-17'));
  const bad = addRow('нет адреса', 'текст');
  c.sendReplies_();
  assert.strictEqual(sent.length, before, 'черновик (все варианты)/пусто/дата/плохой адрес — без отправки');
  assert.ok(/Ответ не отправлен: нет корректного адреса/.test(cell(bad, ci('Заметка') + 1)));
  c.sendReplies_();
  assert.strictEqual(cell(bad, ci('Заметка') + 1).split('нет корректного адреса').length, 2, 'заметка не дублируется при повторных запусках');
  c.CFG.SEND_REPLIES = false;
  addRow('off@example.com', 'выключено');
  c.sendReplies_();
  assert.strictEqual(sent.length, before, 'выключатель SEND_REPLIES=false');
  c.CFG.SEND_REPLIES = true;
  grid.length = 1; sent.length = 0;

  // сбой отправки: дата снята (повторится), других строк не касается; сбой после отправки не даёт дубль
  {
    const okRow = addRow('ok1@example.com', 'ответ ок');
    const failRow = addRow('fail@example.com', 'ответ сбой');
    const okRow2 = addRow('ok2@example.com', 'ответ ок 2');
    const realSend = global.MailApp.sendEmail;
    global.MailApp.sendEmail = (to, s2, b2) => { if (to === 'fail@example.com') throw new Error('Service invoked too many times'); return realSend(to, s2, b2); };
    const n0 = sent.length;
    c.sendReplies_();
    assert.strictEqual(sent.length - n0, 2, 'сбой одной строки не роняет остальные');
    assert.ok(!cell(failRow, stampCol), 'при сбое отправки дата снята: повторится на следующем запуске');
    assert.ok(cell(okRow, stampCol) instanceof Date && cell(okRow2, stampCol) instanceof Date);
    assert.ok(!/fail@/.test(cell(failRow, ci('Заметка') + 1)), 'адреса в «Заметке» нет');
    global.MailApp.sendEmail = realSend;
    c.sendReplies_();
    assert.strictEqual(sent.length - n0, 3, 'повтор на следующем запуске после сбоя');
    c.sendReplies_();
    assert.strictEqual(sent.length - n0, 3, 'и дальше не повторяется');
    grid.length = 1; sent.length = 0;
  }

  // лимит за запуск
  for (let i = 0; i < 25; i++) addRow('u' + i + '@example.com', 'ответ ' + i);
  c.sendReplies_();
  assert.strictEqual(sent.length, c.CFG.MAX_REPLIES_PER_RUN, 'не больше MAX_REPLIES_PER_RUN за запуск');
  c.sendReplies_();
  assert.strictEqual(sent.length, 25, 'остальные уходят в следующем запуске');
  assert.strictEqual(new Set(sent.map((x) => x.to)).size, 25);
  grid.length = 1; sent.length = 0;

  // квота с запасом
  for (let i = 0; i < 8; i++) addRow('q' + i + '@example.com', 'ответ');
  quota = 15; c.sendReplies_();
  assert.strictEqual(sent.length, 5, 'квота 15 − запас 10 = 5 писем');
  quota = 10; c.sendReplies_();
  assert.strictEqual(sent.length, 5, 'квота ≤ запаса — не шлём');
  quota = 100; grid.length = 1; sent.length = 0;

  // сбой одной строки
  const f1 = addRow('ok1@example.com', 'раз'), f2 = addRow('boom@example.com', 'два'), f3 = addRow('ok3@example.com', 'три');
  failTo.add('boom@example.com');
  c.sendReplies_();
  assert.deepStrictEqual(sent.map((x) => x.to), ['ok1@example.com', 'ok3@example.com'], 'остальные строки продолжаются');
  assert.ok(cell(f1, stampCol) instanceof Date && cell(f3, stampCol) instanceof Date && !cell(f2, stampCol), 'у упавшей строки даты нет');
  assert.ok(/Ответ не отправлен: ошибка отправки/.test(cell(f2, ci('Заметка') + 1)) && !/boom@/.test(cell(f2, ci('Заметка') + 1)), 'заметка без адреса');
  assert.ok(logs.some((l) => /строка \d+/.test(l)), 'и в Logger');
  failTo.clear();

  // обрезка
  grid.length = 1; sent.length = 0;
  addRow('long@example.com', 'x'.repeat(5000));
  c.sendReplies_();
  assert.strictEqual(sent[0].body, 'x'.repeat(4000) + '\n\nСтатус: ' + c.CFG.STATUS_URL);

  grid.length = 1; sent.length = 0;
  // защита текста
  grid.length = 1; sent.length = 0;
  const nickCol = ci(c.CFG.NICK);
  const mk = (email, text, nick) => { const r = addRow(email, text); grid[r - 1][nickCol] = nick || 'Ник=секрет'; return r; };
  mk('maria@example.com', '', 'Мария Иванова');
  mk('petr@example.com', '', 'Пётр_Вымышленный');
  const bads = {
    ник: 'Так же, как у Пётр_Вымышленный', падеж: 'Это видел Марии друг', адрес: 'Пишите petr@example.com',
    ip: 'Сервер 198.51.100.7', win: 'Файл C:\\Users\\Ivan\\x', linux: 'См. /home/user/mods', at: 'Спросите @someone', url: 'Смотри https://evil.example/x',
    www: 'Смотри www.evil.example', drive: 'drive.google.com/abc', ent: 'Вот &#64; тут', diag: 'Игрок Player-ab12',
    urlprefix: 'https://github.com/limanov7kiy/Minecraft_moded_evil/x', http: 'http://github.com/limanov7kiy/Minecraft_moded/x',
    cutmail: 'Пишите masha\n@example.net', cutmail2: 'Пишите vasya@\nmail.example', cuturl: 'Смотри ht\ntps://evil.example/x', md: 'Подробнее [тут](x)', email: 'Пишите anna@mail.example',
  };
  const rows = {};
  Object.keys(bads).forEach((k) => { rows[k] = addRow('to' + Object.keys(rows).length + '@example.org', bads[k]); });
  c.sendReplies_();
  assert.strictEqual(sent.length, 0, 'ни одно из запрещённых писем не ушло: ' + sent.map((x) => x.body));
  Object.keys(rows).forEach((k) => {
    const n = cell(rows[k], ci('Заметка') + 1);
    assert.ok(/^Ответ не отправлен: в тексте/.test(n), k + ': ' + n);
    assert.ok(!cell(rows[k], stampCol) && !/198\.51|Вымышл|evil|petr@|someone|Ivan/.test(n), k + ': без цитаты, дата пуста');
  });
  // разрешено: имя самого адресата, URL статуса и репозитория
  const good1 = mk('masha@example.com', 'Здравствуйте, Маша! Статус: ' + c.CFG.STATUS_URL + ' и https://github.com/limanov7kiy/Minecraft_moded/pull/106 .', 'Маша');
  const good2 = addRow('ivan@example.com', 'Привет, Иван. Версия 1.20.1, Forge 47.4.26, отдайте игре 4–6 ГБ.');
  grid[good2 - 1][nickCol] = 'Иван';
  c.sendReplies_();
  assert.deepStrictEqual(sent.map((x) => x.to), ['masha@example.com', 'ivan@example.com'], 'имя адресата, разрешённые URL и версии проходят');
  assert.ok(cell(good1, stampCol) instanceof Date);
}
// ---- onEdit: статус не по категории → примечание на ячейке, «Дата изменения» обновляется ----
{
  const head = ['Timestamp', '№', 'Статус', 'Категория', 'Дата изменения', 'Статус изменён (дата)'];
  const grid = [head.slice(), ['x', 'R-001', 'исправлено', 'пожелание / идея', '', ''], ['x', 'R-002', 'идея', 'пожелание', '', ''], ['x', 'R-003', 'исправлено', 'краш/вылет', '', '']];
  const notes = {};
  const sheet = {
    getSheetId: () => 1, getLastColumn: () => head.length, getLastRow: () => grid.length,
    getRange: (r, col, nr, nc) => ({ getValues: () => [grid[r - 1].slice(col - 1, col - 1 + (nc || 1))], getValue: () => grid[r - 1][col - 1],
      setValue: (v) => { grid[r - 1][col - 1] = v; }, setNote: (n) => { notes[r + ':' + col] = n; } }),
  };
  global.SpreadsheetApp = { getActiveSpreadsheet: () => ({ getSheets: () => [sheet] }) };
  global.PropertiesService = { getDocumentProperties: () => ({ getProperty: () => '1' }) };
  const edit = (row, col) => c.onEdit({ range: { getSheet: () => sheet, getRow: () => row, getColumn: () => col } });
  edit(2, 3); edit(3, 3); edit(4, 3);
  assert.ok(/не подходит категории/.test(notes['2:3']) && /Для пожеланий/.test(notes['2:3']), 'исправлено у пожелания → примечание');
  assert.strictEqual(notes['3:3'], '', 'идея у пожелания — примечания нет');
  assert.strictEqual(notes['4:3'], '', 'исправлено у бага — примечания нет');
  grid[3][3] = 'пожелание'; edit(4, 4);
  assert.ok(/Для пожеланий/.test(notes['4:3']), 'смена категории тоже перепроверяет статус');
  assert.ok(grid[1][4] instanceof Date && grid[1][5] instanceof Date, 'дата изменения обновлена');
}
console.log('report-sheet: OK');
