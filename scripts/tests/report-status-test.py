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
    row('R-030', 'нужны данные', 'Зависание ПК', 'Совет 1: выдайте 4–6 ГБ памяти.\nСовет 2: включите «Режим слабого ПК» (Настройки → Параметры видео).\n\nСовет 3: закройте лишние программы | и браузер.'),
    row('R-031', 'нужны данные', 'Очень длинное пояснение', ' '.join('Это совет номер %d, он достаточно длинный для проверки.' % i for i in range(60))),
    row('R-032', 'нужны данные', 'Ник в хвосте длинного пояснения', ' '.join(['Обычный совет.'] * 120) + ' Пишет ' + SECRET_NICK.lower()),   # ник за пределом лимита — всё равно ловится
    row('R-033', 'нужны данные', 'Адрес разрезан переносом', 'Пишите masha\n@example.com или \nvasya@\nmail.example'),
    row('R-034', 'нужны данные', 'Ссылка разрезана переносом', 'Смотрите ht\ntps://evil.example/x'),
    row('R-035', 'нужны данные', 'IP за границей обрезки', ' '.join(['Обычный совет.'] * 130) + ' Сервер 203.0.113.9 тормозит.'),
    row('', 'принят', 'Без номера'),
    # ---- пожелания (категория «пожелание / идея» из формы или «пожелание») ----
    row('R-036', 'идея', 'Хочется тёмную тему в меню модов', mod='Интерфейс', cat='пожелание / идея'),
    row('R-037', 'принята', 'Подсказки к рецептам в Jade', cat='пожелание'),
    row('R-038', 'нужно решение владельца', 'Добавить нового босса в Нижний мир', 'Влияет на баланс: решение за владельцем', cat='пожелание'),
    row('R-039', 'в реализации', 'Русификация книги заклинаний', owner='#120', cat='пожелание'),
    row('R-040', 'реализовано', 'Модель плаща для Priestess', ver='сборка от 2026-10-20', cat='пожелание'),
    row('R-041', 'отклонено', 'Убрать всех мобов из Нижнего мира', 'Ломает баланс, не берём', cat='пожелание'),
    row('R-042', 'отложено', 'Миникарта в стиле пергамента', 'Ждём обновления мода', cat='пожелание'),
    row('R-043', 'отложено', 'Пожелание без пояснения', cat='пожелание'),                   # нет комментария → предупреждение, но опубликовано
    row('R-044', 'идея', '', cat='пожелание'),                                                # нет описания
    row('R-045', 'идея', 'Идея от ' + SECRET_NICK.lower(), cat='пожелание'),                 # ник автора
    row('R-046', 'идея', 'Смотри https://drive.google.com/idea', cat='пожелание'),            # ссылка
    row('R-047', 'исправлено', 'Баговый статус у пожелания', cat='пожелание'),               # статус бага у пожелания
    row('R-048', 'реализовано', 'Статус пожелания у бага', cat='краш/вылет'),                # статус пожелания у бага
    row('R-049', '', 'Пустой статус у пожелания', cat='пожелание / идея'),                   # пусто → «идея»
    row('R-050', 'идея', 'Пишите на masha.k@example.com', cat='пожелание', email='masha.k@example.com'),
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
        for bad in ('R-009', 'R-010', 'R-011', 'R-012', 'R-013', 'R-014', 'R-016', 'R-017', 'R-018', 'R-019', 'R-020', 'R-021', 'R-022', 'R-023', 'R-024', 'R-026', 'R-027', 'R-028', 'R-033', 'R-034', 'R-035'):
            assert '| %s |' % bad not in text, 'опубликована: ' + bad
            assert bad in err, 'нет предупреждения: ' + bad
        assert 'R-021: непонятная дата' in err and 'Вася' not in err
        assert '| R-025 |' in text and '| R-029 |' in text
        assert 'R-008' in err and 'R-008 |' not in text
        assert 'Без номера' not in text and 'нет номера' in err
        # разделы: открытое и решённое
        bug_text, wish_text = text.split('## Пожелания')
        open_part, solved_part = bug_text.split('## Что ещё открыто')[1].split('## Что решено')
        for n in ('R-002', 'R-003', 'R-005', 'R-006', 'R-015'):
            assert '| %s |' % n in open_part, n
        for n in ('R-001', 'R-004', 'R-007'):
            assert '| %s |' % n in solved_part, n
        assert '| R-003 | Краш при входе | краш/вылет | Мод Х | в работе | 2026-10-16 | #97 | — |' in open_part
        assert 'Строка / с трубой и переносом' in text
        # длинное пояснение с несколькими советами: целиком, строки через <br>, экранирование прежнее
        assert ('| R-030 | Зависание ПК | краш/вылет | Мод Х | нужны данные | 2026-10-17 | — | Совет 1: выдайте 4–6 ГБ памяти.<br>'
                'Совет 2: включите «Режим слабого ПК» (Настройки → Параметры видео).<br>'
                'Совет 3: закройте лишние программы / и браузер. |') in open_part
        r31 = [l for l in open_part.splitlines() if l.startswith('| R-031 |')][0]
        n31 = r31.split(' | ')[-1].rstrip(' |')
        assert n31.endswith('.…') and len(n31) <= 1500 and 'номер 59' not in n31 and 'номер 0,' in n31, n31[-60:]
        assert 'R-031: пояснение длиннее 1500' in err
        assert '| R-032 |' not in text and 'R-032' in err and SECRET_NICK.lower() not in err
        # сводка: все 15 пронумерованных (R-001…R-015) считаются
        # баги не смешиваются с пожеланиями: R-047 (статус бага у пожелания) и R-048 (статус пожелания у бага) в счёт не входят, R-048 — «?»
        assert '| 36 | 23 | 1 | 1 | 1 | 7 | 1 | 1 |' in text, [l for l in text.splitlines() if l.startswith('| 3')]
        for n in range(36, 51):
            assert '| R-0%d |' % n not in bug_text, 'пожелание в багах: R-0%d' % n
        # группы по статусам и сводка по категориям/модам
        for h in ('### новый (', '### в работе (', '### отложено (', '### исправлено (', '### дубль ('):
            assert h in text, h
        assert text.index('### новый') < text.index('### принят') < text.index('### в работе')
        assert '| фризы/лаги | 0 | 1 |' in text and '| Мод Х |' in text
        # ---- раздел «Пожелания» ----
        assert '| Всего | Открыто (идея, принята) | В реализации | Нужно решение владельца | Реализовано | Отклонено | Отложено |' in wish_text
        # всего 14 (13 со статусами пожеланий + R-047 с неверным статусом); «идея»/«принята»: R-036, 037, 044, 045, 046, 049, 050 = 7
        assert '| 14 | 7 | 1 | 1 | 1 | 1 | 2 |' in wish_text, [l for l in wish_text.splitlines() if l.startswith('| ')][:4]
        w_open, w_solved = wish_text.split('### Открытые пожелания')[1].split('### Реализовано и отклонено')
        for n in ('R-036', 'R-037', 'R-038', 'R-039', 'R-042', 'R-043', 'R-049'):
            assert '| %s |' % n in w_open, n
        for n in ('R-040', 'R-041'):
            assert '| %s |' % n in w_solved, n
        assert '| R-036 | Хочется тёмную тему в меню модов | Интерфейс | идея | 2026-10-17 | — | — |' in w_open
        assert '| R-039 | Русификация книги заклинаний | Мод Х | в реализации | 2026-10-17 | #120 | — |' in w_open
        assert '| R-040 | Модель плаща для Priestess | Мод Х | реализовано | 2026-10-17 | сборка от 2026-10-20 | — |' in w_solved
        assert '#### идея (' in w_open and '#### нужно решение владельца (' in w_open and '#### в реализации (' in w_open
        assert w_open.index('#### идея') < w_open.index('#### принята') < w_open.index('#### нужно решение владельца') < w_open.index('#### в реализации')
        assert '| Комментарий |' in wish_text and '| Категория |' not in wish_text, 'у пожеланий свои колонки без категории'
        for n in ('R-044', 'R-045', 'R-046', 'R-047', 'R-048', 'R-050'):
            assert '| %s |' % n not in text, 'опубликована: ' + n
        assert 'R-047: статус не подходит пожеланию' in err and 'R-048: статус не подходит багу' in err
        assert 'R-044: нет публичного описания' in err and 'R-045' in err and 'R-046' in err and 'R-050' in err
        assert 'R-043: для статуса «отложено» нет публичного пояснения' in err
        assert 'R-049' not in err and 'R-036' not in err
        assert 'пожеланий: 9 из 14' in p.stdout, p.stdout
        # в разделе багов пожеланий нет ни в категориях, ни в модах
        assert '| пожелание |' not in bug_text and '| Интерфейс |' not in bug_text
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
