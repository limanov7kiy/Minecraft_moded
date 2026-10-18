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
console.log('report-sheet: OK');
