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
assert.deepStrictEqual(r, { received: 4, noNumber: 1, done: 1, open: 3, stale: 2, postponed: 0 });
// «отложено»: не красится как зависшее, считается висящим, учитывается отдельно
assert.strictEqual(c.isStale('отложено', d('2026-01-01'), now, 7), false);
assert.ok(c.CFG.STATUSES.includes('отложено'));
assert.deepStrictEqual(c.summarize([{ status: 'отложено', number: 'R-004', changed: d('2026-01-01') }], now, 7),
  { received: 1, noNumber: 0, done: 0, open: 1, stale: 0, postponed: 1 });
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
assert.ok(c.CFG.EXTRA.includes('Категория') && c.CFG.CATEGORIES.length === 7);
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
    f.items.push(it); return it; };
  f.addTextItem = () => item('text'); f.addParagraphTextItem = () => item('paragraph');
  f.addListItem = () => item('list'); f.addMultipleChoiceItem = () => item('choice');
  ['setTitle', 'setDescription', 'setConfirmationMessage', 'setAllowResponseEdits', 'setLimitOneResponsePerUser', 'setProgressBar', 'setCollectEmail']
    .forEach((m) => { f[m] = (v) => { f.calls.push([m, v]); return f; }; });
  f.getId = () => id; f.getPublishedUrl = () => 'PUB'; f.getEditUrl = () => 'EDIT';
  return f;
}
const forms = {}; let created = 0;
global.FormApp = { create: () => { created++; return (forms['f' + created] = fakeForm('f' + created)); }, openById: (id) => { if (!forms[id]) throw new Error('no form'); return forms[id]; } };
const store = {};
global.PropertiesService = { getDocumentProperties: () => ({ getProperty: (k) => store[k] || null, setProperty: (k, v) => { store[k] = v; } }) };
const r1 = c.ensureForm_();
assert.strictEqual(r1.created, true);
const it = r1.form.items;
assert.strictEqual(it.length, c.FORM.FIELDS.length);
assert.strictEqual(it.filter((x) => x.required).length, 4);   // ник, версия, где играли, что не работает
assert.deepStrictEqual(it.find((x) => x.title === 'Категория').choices, c.CFG.CATEGORIES);
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
assert.deepStrictEqual(c.pickNewSheet([1, 2], [{ id: 1 }, { id: 2 }]), null);
assert.deepStrictEqual(c.pickNewSheet([1], [{ id: 1 }, { id: 7, name: 'x' }]), { id: 7, name: 'x' });
const sr = c.startRows('PUB', 'EDIT');
assert.strictEqual(sr[0][1], 'PUB'); assert.strictEqual(sr[1][1], 'EDIT');
assert.ok(sr.some((x) => /Подтверждённые/.test(x[1])) && sr.some((x) => /Загрузка файла/.test(x[1])));
assert.strictEqual(typeof c.bootstrap, 'function');
// ---- setup() на листе-«Таблице»: createFilter бросает исключение, остальное должно дойти до конца ----
function runSetup(failFilter) {
  const triggers = []; const logs = []; const written = [];
  const head = ['Отметка времени', 'Адрес электронной почты', c.CFG.NICK].concat(c.CFG.EXTRA);
  const chain = () => new Proxy(function () {}, { get: (_, k) => (k === 'then' ? undefined : chain()), apply: () => chain() });
  const rangeOf = (r, col) => ({
    getValues: () => [head.slice()], setValue: (v) => { if (r === 1) head[col - 1] = v; return rangeOf(r, col); },
    getValue: () => head[0],
    createFilter: () => { if (failFilter) throw new Error('Нельзя применить фильтр к диапазону, который частично пересекается с таблицей'); },
    setValues: (v) => { written.push(v); }, setWrap: () => {}, setDataValidation: () => {}, setFormula: () => {}, setFormulas: () => {}, copyTo: () => {},
  });
  const sheet = {
    getLastColumn: () => head.length, getLastRow: () => 1, getName: () => 'Ответы', getSheetId: () => 1,
    getRange: (r, c) => rangeOf(r, c), getFilter: () => null, setFrozenRows: () => {}, setConditionalFormatRules: () => {},
    clear: () => {}, setColumnWidth: () => {}, autoResizeColumns: () => {}, getDataRange: () => ({ getValues: () => [head.slice()] }),
  };
  const ss = { setActiveSheet: () => {}, moveActiveSheet: () => {}, getSheets: () => [sheet], getSheetByName: () => sheet, insertSheet: () => sheet };
  global.SpreadsheetApp = { getActiveSpreadsheet: () => ss, newDataValidation: chain, newConditionalFormatRule: chain, CopyPasteType: { PASTE_VALUES: 0 } };
  const trig = (name) => { const t = { name }; const b = new Proxy(t, { get: (o, k) => (k === 'create' ? () => triggers.push(name) : () => b) }); return b; };
  global.ScriptApp = { getProjectTriggers: () => [], deleteTrigger: () => {}, newTrigger: trig };
  global.LockService = { getDocumentLock: () => ({ waitLock: () => {}, releaseLock: () => {} }) };
  global.Logger = { log: (m) => logs.push(m) };
  global.PropertiesService = { getDocumentProperties: () => ({ getProperty: () => null, setProperty: () => {} }) };
  return { triggers, logs, written, ss };
}
{
  const env = runSetup(true);
  c.setup();   // не должен бросить
  c.writeStart_(env.ss, { getPublishedUrl: () => 'PUB', getEditUrl: () => 'EDIT' });
  assert.ok(env.written.some((v) => v.some((r) => r[1] === 'PUB')), '«Начало» записано');
  assert.deepStrictEqual(env.triggers, ['onFormSubmit', 'fillMissing']);
  c.setup();   // повторный запуск (идемпотентность) не падает
  assert.ok(env.logs.some((m) => /фильтр/.test(m) && /таблиц/.test(m)), 'причина записана в Logger');
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
console.log('report-sheet: OK');
