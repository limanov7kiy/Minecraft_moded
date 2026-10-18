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
assert.deepStrictEqual(r, { received: 4, noNumber: 1, done: 1, open: 3, stale: 2 });
console.log('report-sheet: OK');
