"""Тест генератора страницы статусов на вымышленном CSV: python3 -I scripts/tests/report-status-test.py
Все данные ниже выдуманы (адреса из диапазона документации RFC 5737)."""
import csv
import subprocess
import sys
import tempfile
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent.parent
GEN = REPO / 'scripts' / 'report-status.py'
H = ['Отметка времени', 'Ник / имя для связи', 'Псевдоним из диагностики', 'Что не работает', 'Ссылка spark', 'Ссылка на файлы',
     '№', 'Статус', 'Ответственный (поток/PR)', 'Исправлено в версии', 'Дата изменения',
     'Мод для публичной страницы', 'Описание для публичной страницы', 'Пояснение для публичной страницы', 'Категория',
     'Адрес электронной почты']
SECRET_NICK = 'ВымышленныйНикТест'
SECRET_TEXT = 'ТекстАвтораСекрет'
SECRET_LINK = 'spark.lucko.me/SECRETSPARK1'


def row(num, status, desc, note='', mod='Мод Х', ver='', owner='', date='2026-10-17 10:00:00', nick=SECRET_NICK, diag='Player-ab12', cat='краш/вылет', email=''):
    d = dict.fromkeys(H, '')
    d.update({'Отметка времени': '10/10/2026 12:00:00', 'Ник / имя для связи': nick, 'Псевдоним из диагностики': diag,
              'Что не работает': SECRET_TEXT, 'Ссылка spark': 'https://' + SECRET_LINK, 'Ссылка на файлы': 'https://drive.google.com/x',
              '№': num, 'Статус': status, 'Ответственный (поток/PR)': owner, 'Исправлено в версии': ver, 'Дата изменения': date,
              'Мод для публичной страницы': mod, 'Описание для публичной страницы': desc, 'Пояснение для публичной страницы': note,
              'Категория': cat, 'Адрес электронной почты': email})
    return d


ROWS = [
    row('R-001', 'исправлено', 'Фриз при полёте', 'Обновитесь', ver='сборка от 2026-10-18', cat='фризы/лаги',),
    row('R-002', 'новый', 'Пропадает предмет в сундуке', cat='мод/механика'),
    row('R-003', 'в работе', 'Краш при входе', owner='#97', date='10/16/2026 09:00:00'),
    row('R-004', 'не баг', 'Нет звука рации', 'Так задумано: рация включается клавишей'),
    row('R-005', 'отложено', 'Редкий мерцающий блок', 'Не сейчас, мало данных'),
    row('R-006', 'нужны данные', 'Чёрный экран', 'Нужен лог последнего запуска'),
    row('R-007', 'дубль', 'Фриз при полёте (повтор)', 'Дубль R-001'),
    row('R-008', 'принят', ''),                                              # нет описания
    row('R-009', 'принят', 'Лаги у 198.51.100.7'),                           # IP
    row('R-010', 'принят', 'Не открывается C:\\Users\\Ivan\\mods'),          # путь
    row('R-011', 'принят', 'Смотрите профиль', 'https://' + SECRET_LINK),    # spark URL
    row('R-012', 'принят', 'Лагает у ' + SECRET_NICK.lower()),               # ник автора
    row('R-013', 'принят', 'Ошибка в Player-ab12'),                          # псевдоним
    row('R-014', 'принят', 'Файл тут: https://drive.google.com/abc'),        # Диск
    row('R-015', 'принят', 'Строка | с трубой\nи переносом'),                # экранирование
    row('R-016', 'принят', 'Вася нашёл краш', nick='Вася, tg @vasya_p'),     # слово из многословного контакта
    row('R-017', 'принят', 'У аккаунта vasya_p вылет', nick='Вася, tg @vasya_p'),
    row('R-018', 'принят', 'Пишет @someone про лаги'),                       # @handle
    row('R-019', 'принят', 'Подробности [тут](mailto:a@b.example)'),         # markdown mailto
    row('R-020', 'принят', 'Смотри [это](//evil.example/x)'),                # markdown без http
    row('R-021', 'принят', 'Дата в чужом формате', date='18 Oct 2026'),
    row('R-022', 'принят', 'У masha такой же вылет', email='masha.k@example.com'),   # локальная часть адреса
    row('R-023', 'принят', 'Почта masha.k@example.com'),                     # любой e-mail
    row('R-024', 'принят', 'Ссылка t.me/someone в чате'),
    row('R-025', 'принят', 'Нормальное описание без личного', email='masha.k@example.com', nick='Маша К'),
    row('R-026', 'принят', 'Маше такой же вылет', nick='Маша К'),            # падеж ника
    row('R-027', 'принят', 'Пишите на masha&#64;mail.example'),              # HTML-сущность вместо @
    row('R-028', 'принят', 'Лагает так же, как у ЧужойНик'),                 # ник другого автора
    row('R-029', 'принят', 'Другое нормальное описание', nick='ЧужойНик'),
    row('', 'принят', 'Без номера'),
]


def run(args, expect_ok=True):
    p = subprocess.run([sys.executable, '-I', str(GEN)] + args, capture_output=True, text=True)
    if expect_ok and p.returncode != 0:
        raise AssertionError(p.stderr)
    return p


def main():
    with tempfile.TemporaryDirectory() as tmp:
        t = Path(tmp)
        src, out = t / 'in.csv', t / 'out.md'
        with open(src, 'w', newline='', encoding='utf-8') as f:
            w = csv.DictWriter(f, H)
            w.writeheader()
            w.writerows(ROWS)
        p = run([str(src), '--out', str(out)])
        text = out.read_text(encoding='utf-8')
        err = p.stderr + p.stdout
        # никаких секретов ни в странице, ни в предупреждениях
        for secret in (SECRET_NICK, SECRET_NICK.lower(), SECRET_TEXT, 'vasya_p', 'masha.k', 'example.com', 'someone', 'ЧужойНик', 'Маше', 'SECRETSPARK1', '198.51.100.7', 'C:\\Users', 'Player-ab12', 'drive.google'):
            assert secret not in text, 'в странице: ' + secret
            assert secret not in err, 'в предупреждениях: ' + secret
        for ok in ('R-001', 'R-002', 'R-003', 'R-004', 'R-005', 'R-006', 'R-007', 'R-015'):
            assert '| %s |' % ok not in text.split('## Что ещё открыто')[0], ok
        for bad in ('R-009', 'R-010', 'R-011', 'R-012', 'R-013', 'R-014', 'R-016', 'R-017', 'R-018', 'R-019', 'R-020', 'R-021', 'R-022', 'R-023', 'R-024', 'R-026', 'R-027', 'R-028'):
            assert '| %s |' % bad not in text, 'опубликована: ' + bad
            assert bad in err, 'нет предупреждения: ' + bad
        assert 'R-021: непонятная дата' in err and 'Вася' not in err
        assert '| R-025 |' in text and '| R-029 |' in text
        assert 'R-008' in err and 'R-008 |' not in text
        assert 'Без номера' not in text and 'нет номера' in err
        # разделы: открытое и решённое
        open_part, solved_part = text.split('## Что ещё открыто')[1].split('## Что решено')
        for n in ('R-002', 'R-003', 'R-005', 'R-006', 'R-015'):
            assert '| %s |' % n in open_part, n
        for n in ('R-001', 'R-004', 'R-007'):
            assert '| %s |' % n in solved_part, n
        assert '| R-003 | Краш при входе | краш/вылет | Мод Х | в работе | 2026-10-16 | #97 | — |' in open_part
        assert 'Строка / с трубой и переносом' in text
        # сводка: все 15 пронумерованных (R-001…R-015) считаются
        assert '| 29 | 23 | 1 | 1 | 1 | 1 | 1 | 1 |' in text, [l for l in text.splitlines() if l.startswith('| 2')]
        # группы по статусам и сводка по категориям/модам
        for h in ('### новый (', '### в работе (', '### отложено (', '### исправлено (', '### дубль ('):
            assert h in text, h
        assert text.index('### новый') < text.index('### принят') < text.index('### в работе')
        assert '| фризы/лаги | 0 | 1 |' in text and '| Мод Х |' in text
                # CSV внутри репозитория не принимается
        inside = REPO / 'scripts' / 'tests' / '_inside_test.csv'
        inside.write_text('x', encoding='utf-8')
        try:
            q = run([str(inside), '--out', str(out)], expect_ok=False)
            assert q.returncode != 0 and 'внутри репозитория' in q.stderr + q.stdout
        finally:
            inside.unlink()
        # нет обязательной колонки
        bad = t / 'bad.csv'
        bad.write_text('a,b\n1,2\n', encoding='utf-8')
        q = run([str(bad), '--out', str(out)], expect_ok=False)
        assert q.returncode != 0 and 'нет колонок' in q.stderr + q.stdout
        # пустая выгрузка → пустая таблица с примером
        emp = t / 'emp.csv'
        with open(emp, 'w', newline='', encoding='utf-8') as f:
            csv.DictWriter(f, H).writeheader()
        run([str(emp), '--out', str(out)])
        assert 'Пока пусто' in out.read_text(encoding='utf-8') and '<!-- Пример строки' in out.read_text(encoding='utf-8')
    print('report-status: OK')


main()
