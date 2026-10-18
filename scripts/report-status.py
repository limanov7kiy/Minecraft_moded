#!/usr/bin/env python3
"""Генератор публичной страницы docs/report-status.md из CSV-выгрузки приватной Таблицы отчётов.

Запуск:  python3 -I scripts/report-status.py <csv вне репозитория>   (процесс — docs/report-form.md, раздел 8)

Репозиторий публичный. Поэтому:
  * читаются и публикуются ТОЛЬКО колонки из PUBLIC_COLUMNS; всё остальное (ник, контакт, текст автора,
    ссылки на файлы, spark) в страницу не попадает ни при каких условиях;
  * строка без публичного описания, без номера или без понятного статуса/даты пропускается (предупреждение);
  * если в публичных полях есть похожее на IP, путь Windows/Linux, URL, ссылку Диска/spark/форму, e-mail,
    псевдоним диагностики или ник автора — строка не публикуется (предупреждение);
  * предупреждения содержат только номер R-### и причину, но не само содержимое;
  * CSV внутри репозитория не принимается (чтобы его не закоммитили случайно).
"""
import argparse
import csv
import os
import re
import sys
import tempfile
from collections import Counter
from datetime import datetime
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
DEFAULT_OUT = REPO / 'docs' / 'report-status.md'

COL_NUMBER = '№'
COL_STATUS = 'Статус'
COL_DATE = 'Дата изменения'
COL_VERSION = 'Исправлено в версии'
COL_OWNER = 'Ответственный (поток/PR)'
COL_DESC = 'Описание для публичной страницы'
COL_MOD = 'Мод для публичной страницы'
COL_NOTE = 'Пояснение для публичной страницы'
COL_CAT = 'Категория'
# Только с флагом --public-names (по умолчанию выключено, решение владельца ещё не принято). Ник из формы не публикуется никогда.
COL_SHOW_NAME = 'Показывать моё имя'
COL_PUBLIC_NAME = 'Публичное имя'
CATEGORIES = ('краш/вылет', 'фризы/лаги', 'мод/механика', 'модели/графика', 'звук/голос', 'сервер/вход', 'другое')
TRUE_VALUES = ('да', 'yes', 'true', '1', '✓', 'истина')
MAX_NAME = 30
# Колонки, которые нужны только чтобы убедиться, что их содержимое не просочилось в публичные поля. Не публикуются.
GUARD_COLUMNS = ('Ник / имя для связи', 'Псевдоним из диагностики')

PUBLIC_COLUMNS = (COL_NUMBER, COL_DESC, COL_MOD, COL_STATUS, COL_DATE, COL_VERSION, COL_NOTE)
REQUIRED = (COL_NUMBER, COL_STATUS, COL_DATE, COL_DESC, COL_NOTE)

STATUSES = ('новый', 'принят', 'в работе', 'исправлено', 'не баг', 'нужны данные', 'отложено', 'дубль')
OPEN_STATUSES = ('новый', 'принят', 'в работе', 'нужны данные', 'отложено')
MAX_LEN = 400

NUMBER_RE = re.compile(r'^R-\d{3,}$')
PR_RE = re.compile(r'^(PR\s*)?#\d+$', re.I)

DANGER = [
    ('IP-адрес', re.compile(r'\b\d{1,3}(?:\.\d{1,3}){3}\b')),
    ('IPv6', re.compile(r'\b(?:[0-9a-f]{1,4}:){3,}[0-9a-f]{1,4}\b', re.I)),
    ('путь Windows', re.compile(r'\b[A-Za-z]:[\\/]')),
    ('путь Windows', re.compile(r'\\\\|\\Users\\|%\w+%', re.I)),
    ('путь Linux/macOS', re.compile(r'(?:^|[\s(])/(?:home|Users|root|tmp|var|etc)/')),
    ('URL', re.compile(r'(?:https?|ftp)://|\bwww\.', re.I)),
    ('ссылка Диска/spark/формы', re.compile(
        r'spark\.lucko|drive\.google|docs\.google|forms\.gle|forms\.google|dropbox|mega\.nz|mediafire|'
        r'cdn\.discord|discord\.gg', re.I)),
    ('e-mail', re.compile(r'\b[\w.+-]+@[\w-]+\.[\w.-]+\b')),
    ('псевдоним диагностики', re.compile(r'\bPlayer-[0-9a-f]{3,}\b|\bPlayer-xxxx\b|diag-Player', re.I)),
]


def parse_date(s):
    s = (s or '').strip()
    for fmt in ('%Y-%m-%d', '%Y-%m-%d %H:%M:%S', '%Y-%m-%dT%H:%M:%S', '%Y-%m-%dT%H:%M:%SZ',
                '%m/%d/%Y %H:%M:%S', '%m/%d/%Y', '%d.%m.%Y %H:%M:%S', '%d.%m.%Y'):
        try:
            return datetime.strptime(s, fmt).date()
        except ValueError:
            pass
    return None


def danger_reason(value):
    for name, rx in DANGER:
        if rx.search(value):
            return name
    return None


def cell(s):
    """Один безопасный фрагмент для ячейки markdown-таблицы."""
    s = re.sub(r'\s+', ' ', s or '').strip()
    return s.replace('|', '/').replace('<', '‹').replace('>', '›').replace('`', "'")


def refuse_inside_repo(path):
    real = Path(os.path.realpath(path))
    try:
        real.relative_to(REPO)
    except ValueError:
        return
    sys.exit('Отказ: CSV лежит внутри репозитория. Положите его во временную папку вне репо (mktemp -d) — в нём личные данные.')


def load_rows(path):
    refuse_inside_repo(path)
    with open(path, newline='', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        header = reader.fieldnames or []
        missing = [c for c in REQUIRED if c not in header]
        if missing:
            sys.exit('В CSV нет колонок: %s. Запустите setup() в Code.gs и выгрузите лист ответов.' % ', '.join(missing))
        return list(reader)


def build(rows, public_names=False):
    """Возвращает (публичные записи, число всех отчётов, предупреждения). Предупреждения без содержимого полей."""
    warnings = []
    records = []
    counts = Counter()
    for i, row in enumerate(rows, start=2):
        g = lambda c: (row.get(c) or '').strip()
        number = g(COL_NUMBER)
        if not number:
            if any((v or '').strip() for v in row.values()):
                warnings.append('строка %d: нет номера R-### — не опубликована' % i)
            continue
        if not NUMBER_RE.match(number):
            warnings.append('строка %d: номер не вида R-### — не опубликована' % i)
            continue
        status = g(COL_STATUS) or 'новый'
        if status not in STATUSES:
            warnings.append('%s: неизвестный статус — не опубликована' % number)
            counts['?'] += 1
            continue
        counts[status] += 1
        desc = g(COL_DESC)
        if not desc:
            warnings.append('%s: нет публичного описания — не опубликована (заполните «%s»)' % (number, COL_DESC))
            continue
        changed = parse_date(g(COL_DATE))
        if changed is None:
            warnings.append('%s: непонятная дата изменения — не опубликована' % number)
            continue
        version = g(COL_VERSION)
        if not version and PR_RE.match(g(COL_OWNER)):
            version = g(COL_OWNER)
        cat = g(COL_CAT).lower()
        if cat not in CATEGORIES:
            if cat:
                warnings.append('%s: категория не из списка — показана как «другое»' % number)
            cat = 'другое'
        author = None
        if public_names:
            author = 'аноним'
            name = g(COL_PUBLIC_NAME)
            if g(COL_SHOW_NAME).lower() in TRUE_VALUES and name:
                nick = g(GUARD_COLUMNS[0]).lower()
                if len(name) > MAX_NAME or danger_reason(name):
                    warnings.append('%s: публичное имя не подошло — показано «аноним»' % number)
                elif nick and (name.lower() == nick or nick in name.lower()):
                    warnings.append('%s: публичное имя совпадает с ником из формы — показано «аноним»' % number)
                else:
                    author = cell(name)
        fields = {'описание': desc, 'мод': g(COL_MOD), 'версия/PR': version, 'пояснение': g(COL_NOTE)}
        bad = None
        for name, v in fields.items():
            if len(v) > MAX_LEN:
                bad = 'поле «%s» длиннее %d знаков' % (name, MAX_LEN)
                break
            r = danger_reason(v)
            if r:
                bad = 'в поле «%s» похоже на %s' % (name, r)
                break
            for gc in GUARD_COLUMNS:
                secret = g(gc)
                if len(secret) >= 3 and secret.lower() in v.lower():
                    bad = 'в поле «%s» найден ник/псевдоним автора' % name
                    break
            if bad:
                break
        if bad:
            warnings.append('%s: %s — не опубликована' % (number, bad))
            continue
        if status in ('не баг', 'отложено', 'нужны данные') and not fields['пояснение']:
            warnings.append('%s: для статуса «%s» нет публичного пояснения — опубликована без него' % (number, status))
        records.append({'author': author, 'cat': cat, 'number': number, 'desc': cell(desc), 'mod': cell(fields['мод']) or '—', 'status': status,
                        'date': changed, 'ver': cell(version) or '—', 'note': cell(fields['пояснение']) or '—'})
    return records, counts, warnings


HEADER = """# Статус отчётов о проблемах

Открытая страница: что игроки нашли, что решено, а что нет. Решение владельца (В41): «Мы работаем с ребятами открыто, пусть будет публичная страница, чтобы ребята видели процесс решения, что решено, а что нет».

Здесь **только** номер отчёта, краткое описание проблемы, категория, мод, статус, дата и номер PR или версия сборки. Ников, контактов, текстов игроков, логов и ссылок на файлы здесь нет и не будет.
Описания пишет хост или приёмщик своими словами; страница собирается скриптом `scripts/report-status.py` из приватной Таблицы (процесс — `docs/report-form.md`, раздел 8).

- Как отправить отчёт — `docs/friends.md`, раздел «Нашёл баг». Ответ с номером R-### приходит автору лично в течение суток.
- Что недавно вошло в сборку и что стоит проверить — `docs/whats-new-check.md`; исправленное по отчётам там помечено «исправлено по отчёту R-012».
- Статусы: **новый** (пришёл), **принят** (признан проблемой), **в работе** (есть ответственный или PR), **исправлено** (в «PR / сборка» — когда будет у вас),
  **не баг** (так задумано или настройка), **нужны данные** (ждём от автора), **отложено** (не сейчас, причина в пояснении), **дубль** (тот же симптом, что у другого отчёта).

"""

TABLE_HEAD = ('| № | Что за проблема | Категория | Мод | Статус | Изменено | PR / сборка | Пояснение |\n'
              '|---|---|---|---|---|---|---|---|\n')
EMPTY_EXAMPLE = ('<!-- Пример строки:\n'
                 '| R-012 | Фриз раз в полминуты при полёте над новыми чанками | фризы/лаги | The Lost Cities | в работе | 2026-10-20 | PR #96 | Ищем причину в генерации чанков |\n'
                 '-->\n')


def table(recs, example=False, authors=False):
    head = TABLE_HEAD
    if authors:
        head = head.replace('| № |', '| № | Автор |', 1).replace('|---|', '|---|---|', 1)
    if not recs:
        return head + '| — | Пока пусто |' + ' |' * (6 + authors) + '\n' + ('\n' + EMPTY_EXAMPLE if example else '')
    out = head
    for r in recs:
        out += '| %s | %s%s | %s | %s | %s | %s | %s | %s |\n' % (
            r['number'], r['author'] + ' | ' if authors else '', r['desc'], r['cat'], r['mod'], r['status'],
            r['date'].isoformat(), r['ver'], r['note'])
    return out


def grouped(recs, order, example=False, authors=False):
    """Подразделы по статусам (порядок order), внутри — по номеру R. Если записей нет — одна пустая таблица."""
    if not recs:
        return table([], example, authors)
    out = ''
    for st in order:
        part = [r for r in recs if r['status'] == st]
        if part:
            out += '### %s (%d)\n\n%s\n' % (st, len(part), table(part, authors=authors))
    return out


def breakdown(records):
    def block(title, keys, getter):
        rows = ''
        for k in keys:
            mine = [r for r in records if getter(r) == k]
            if mine:
                op = sum(1 for r in mine if r['status'] in OPEN_STATUSES)
                rows += '| %s | %d | %d |\n' % (k, op, len(mine) - op)
        return ('| %s | Открыто | Решено |\n|---|---|---|\n%s' % (title, rows)) if rows else ''
    mods = sorted({r['mod'] for r in records if r['mod'] != '—'}, key=str.lower)
    out = block('Категория', CATEGORIES, lambda r: r['cat'])
    m = block('Мод', mods, lambda r: r['mod'])
    return out + ('\n' + m if m else '')


def render(records, counts, authors=False):
    total = sum(counts.values())
    last = max((r['date'] for r in records), default=None)
    key = lambda r: int(r['number'][2:])
    records = sorted(records, key=key)
    still_open = [r for r in records if r['status'] in OPEN_STATUSES]
    solved = [r for r in records if r['status'] not in OPEN_STATUSES]
    c = lambda s: counts.get(s, 0)
    summary = ('| Всего | Открыто (новый, принят) | В работе | Исправлено | Не баг | Нужны данные | Отложено | Дубль |\n'
               '|---|---|---|---|---|---|---|---|\n'
               '| %d | %d | %d | %d | %d | %d | %d | %d |\n' % (
                   total, c('новый') + c('принят'), c('в работе'), c('исправлено'), c('не баг'),
                   c('нужны данные'), c('отложено'), c('дубль')))
    hidden = total - len(records)
    note = ''
    if hidden > 0:
        note = '\n%d из них ещё без публичного описания и в таблицах ниже пока не показаны.\n' % hidden
    bd = breakdown(records)
    return (HEADER + '## Сводка' + (' на %s' % last.isoformat() if last else '') + '\n\n' + summary + note +
            ('\n### По категориям и модам\n\n' + bd if bd else '') +
            '\n## Что ещё открыто\n\nГруппы по статусу, внутри — по номеру отчёта.\n\n' +
            grouped(still_open, ('новый', 'принят', 'в работе', 'нужны данные', 'отложено'), True, authors) +
            '\n## Что решено\n\n' + grouped(solved, ('исправлено', 'не баг', 'дубль'), False, authors))


def main(argv):
    ap = argparse.ArgumentParser(description=__doc__.split('\n')[0])
    ap.add_argument('csv', help='CSV-выгрузка Таблицы (вне репозитория)')
    ap.add_argument('--out', default=str(DEFAULT_OUT), help='куда писать (по умолчанию docs/report-status.md)')
    ap.add_argument('--public-names', action='store_true',
                    help='ВЫКЛЮЧЕНО по умолчанию: показывать «Публичное имя» тем, кто поставил «Показывать моё имя»; остальные — «аноним»')
    a = ap.parse_args(argv)
    rows = load_rows(a.csv)
    records, counts, warnings = build(rows, a.public_names)
    text = render(records, counts, a.public_names)
    out = Path(a.out)
    fd, tmp = tempfile.mkstemp(dir=str(out.parent), prefix='.report-status-', suffix='.tmp')
    with os.fdopen(fd, 'w', encoding='utf-8', newline='\n') as f:
        f.write(text)
    os.replace(tmp, out)
    for w in warnings:
        print('ПРЕДУПРЕЖДЕНИЕ: ' + w, file=sys.stderr)
    print('Опубликовано строк: %d из %d. Предупреждений: %d. Файл: %s' % (len(records), sum(counts.values()), len(warnings), out))
    print('Теперь: закоммитьте только docs/report-status.md и удалите CSV.')


if __name__ == '__main__':
    main(sys.argv[1:])
