/**
 * Учёт отчётов игроков в Google Таблице, привязанной к форме (Расширения → Apps Script).
 * Вставляется целиком; один раз запустить bootstrap() — он сам создаёт форму, связывает её с этой Таблицей и вызывает setup(). Личных данных и секретов здесь нет —
 * данные живут только в Таблице владельца. Инструкция — docs/report-form.md.
 *
 * Что делает:
 *  - при новом ответе присваивает номер R-001, R-002… (счётчик в свойствах документа, не зависит от удаления строк),
 *    ставит статус «новый» и дату изменения;
 *  - раз в день проверяет, что у всех строк есть номер (на случай сбоя триггера);
 *  - при правке статуса/ответственного/версии/заметки обновляет «Дату изменения»;
 *  - список статусов, подсветка зависших отчётов и лист «Сводка» (получено / обработано / висит / старше N дней);
 *  - лист «По людям» (по адресу почты из формы, без адреса — по нику): сколько отчётов и открытых, номера, последний ответ, неотвеченные;
 *  - категория «пожелание / идея»: отдельный набор статусов (идея → принята → в реализации → реализовано, а также нужно решение владельца /
 *    отклонено / отложено), отдельные счётчики на «Сводке», своё письмо-подтверждение; нумерация общая R-###;
 *  - колонки для публичной страницы (заполняются вручную, без ников; сборка — scripts/report-status.py) и для ответов авторам;
 *    строка с оранжевой подсветкой = статус сменился, а ответ автору не отправлен.
 */
var CFG = {
  STATUSES: ['новый', 'принят', 'в работе', 'исправлено', 'дубль', 'не баг', 'нужны данные', 'отложено'],
  OPEN: ['новый', 'принят', 'в работе', 'нужны данные'],   // «отложено» не краснеет: ждёт сознательно
  DONE: ['исправлено', 'дубль', 'не баг'],
  // Пожелания (категория WISH): свой набор статусов. «отложено» общий. Список в ячейке один на колонку, поэтому проверка значений в Таблице —
  // объединённый список (ALL_STATUSES); соответствие категории проверяет statusValid() при правке (мягкая пометка ячейки, без запрета).
  WISH: 'пожелание',
  WISH_CHOICE: 'пожелание / идея',   // пункт выпадающего списка формы; в Таблице начало «пожелание» опознаётся isWish_()
  WISH_STATUSES: ['идея', 'принята', 'в реализации', 'реализовано', 'нужно решение владельца', 'отклонено', 'отложено'],
  WISH_OPEN: ['идея', 'принята', 'в реализации', 'нужно решение владельца'],
  WISH_DONE: ['реализовано', 'отклонено'],
  STALE_DAYS: 7,
  EXTRA: ['№', 'Статус', 'Ответственный (поток/PR)', 'Исправлено в версии', 'Дата изменения', 'Заметка',
          'Статус изменён (дата)', 'Мод для публичной страницы', 'Описание для публичной страницы',
          'Пояснение для публичной страницы', 'Ответ автору', 'Ответ отправлен (дата)', 'Категория', 'Письмо отправлено (дата)'],
  EDIT_TRIGGERS: ['Статус', 'Ответственный (поток/PR)', 'Исправлено в версии', 'Заметка',
                  'Мод для публичной страницы', 'Описание для публичной страницы', 'Пояснение для публичной страницы', 'Категория'],
  BUG_CATEGORIES: ['краш/вылет', 'фризы/лаги', 'мод/механика', 'модели/графика', 'звук/голос', 'сервер/вход', 'другое'],
  CATEGORIES: ['краш/вылет', 'фризы/лаги', 'мод/механика', 'модели/графика', 'звук/голос', 'сервер/вход', 'другое', 'пожелание'],
  NICK: 'Ник / имя для связи',
  // Форма с «Собирать адреса эл. почты: Подтверждённые» сама добавляет колонку; название зависит от языка. Нет колонки — setup() создаёт первую.
  EMAIL_TITLES: ['Адрес электронной почты', 'Адрес эл. почты', 'Email Address'],
  MAX_ROWS: 2000,
  // Решение владельца (В43 «а»): письмо автору сразу после отправки отчёта. false — выключить письма.
  SEND_CONFIRMATION: true,
  // Решение владельца (В44 «б»): текст из «Ответ автору» уходит автору сам, без одобрения владельца. false — выключить.
  SEND_REPLIES: true,
  MAX_REPLIES_PER_RUN: 20,   // писем за один запуск таймера (раз в 15 минут)
  QUOTA_RESERVE: 10,         // запас дневной квоты MailApp под письма-подтверждения
  MAX_REPLY_CHARS: 4000,
  STATUS_URL: 'https://github.com/limanov7kiy/Minecraft_moded/blob/HEAD/docs/report-status.md'
};

CFG.ALL_STATUSES = CFG.STATUSES.concat(CFG.WISH_STATUSES.filter(function (x) { return CFG.STATUSES.indexOf(x) < 0; }));
CFG.FORM_CATEGORIES = CFG.BUG_CATEGORIES.concat([CFG.WISH_CHOICE]);   // что видит автор в форме
CFG.SHEET_CATEGORIES = CFG.CATEGORIES.concat([CFG.WISH_CHOICE]);      // что принимает список в Таблице (форма пишет «пожелание / идея»)

// Пожелание ли строка: категория «пожелание» или «пожелание / идея» (регистр и пробелы в начале не важны).
function isWish_(category) { return /^\s*пожелание/i.test(String(category || '')); }
function isOpenStatus_(status) { return CFG.OPEN.indexOf(status) >= 0 || CFG.WISH_OPEN.indexOf(status) >= 0; }
function isDoneStatus_(status) { return CFG.DONE.indexOf(status) >= 0 || CFG.WISH_DONE.indexOf(status) >= 0; }

// Подходит ли статус категории: пусто можно; у пожеланий — WISH_STATUSES, у остальных — STATUSES.
function statusValid(category, status) {
  if (!status) return true;
  return (isWish_(category) ? CFG.WISH_STATUSES : CFG.STATUSES).indexOf(status) >= 0;
}

// Номер R-### : минимум три цифры, дальше без обрезки (R-1000).
function formatNumber(n) { var s = String(n); while (s.length < 3) s = '0' + s; return 'R-' + s; }

// Зависший отчёт: открытый статус и последнее изменение старше days дней.
function isStale(status, changed, now, days) {
  if (status && !isOpenStatus_(status)) return false;   // пустой статус = ещё не разобран
  if (!changed) return true;
  return (now.getTime() - new Date(changed).getTime()) > days * 86400000;
}

// Формулы служебных листов. c — буквы колонок {last, num, nick, status}; sheet — имя листа ответов.
// Только для владельца: на листах есть ники. Листы не публиковать, не делиться Таблицей.
function buildQueries(sheet, c) {
  var q = "'" + sheet + "'!A2:" + c.last;
  var notDone = CFG.DONE.concat(CFG.WISH_DONE).map(function (x) { return c.status + " <> '" + x + "'"; }).join(' and ');
  return {
    byNumber: '=IFERROR(QUERY(' + q + ', "select * where ' + c.num + ' is not null order by ' + c.num + '", 0), "Нет отчётов")',
    byAuthor: '=IFERROR(QUERY(' + q + ', "select * where ' + c.num + ' is not null order by ' + c.nick + ', ' + c.num + '", 0), "Нет отчётов")',
    pending: '=IFERROR(QUERY(' + q + ', "select ' + c.nick + ', count(' + c.num + ') where ' + c.num + ' is not null and ' + c.nick +
      ' is not null and ' + notDone + ' group by ' + c.nick + ' order by count(' + c.num + ') desc label ' + c.nick +
      " 'Автор', count(" + c.num + ") 'Открытых отчётов'" + '", 0), "Нет отчётов")'
  };
}

// ---------- Форма: создаётся и связывается скриптом (bootstrap) ----------
// Вопрос «Что не работает» стал общим для багов и пожеланий; WISH_QUESTION.old — прежнее название (ищем и по нему, и по новому).
var WISH_QUESTION = {
  old: 'Что не работает',
  title: 'Что не работает / что хотите видеть',
  help: 'Проблема: что делали, что ожидали, что получилось. Пожелание: что хотели бы увидеть и зачем (от этого зависит, как мы решим)'
};
var WISH_CATEGORY_HELP = 'Что ближе всего. Идея или пожелание — «пожелание / идея»: опишите в поле ниже, что хотите увидеть и зачем';
var FORM = {
  TITLE: 'Minecraft_moded — отчёт о проблеме',
  DESCRIPTION: 'Опишите проблему. Логи и скриншоты — по желанию: лучше архив diag-Player-….zip от collect-diagnostics.bat (ник и IP в нём заменены), ' +
    'ссылкой в поле «Ссылка на файлы». Не вставляйте пароли, адрес дома, реальное имя и IP в текст. Ответы видит только хост. ' +
    'Адрес виден только хосту, не публикуется; нужен вход в Google. ' +
    'Есть идея, а не проблема? Выберите категорию «' + CFG.WISH_CHOICE + '» и напишите, что хотите увидеть и зачем.',
  CONFIRMATION: 'Спасибо! Номер отчёта и ответ пришлём в чат в течение суток; статус — на странице report-status',
  // type: text | paragraph | list | choice. Вопроса «Загрузка файла» здесь нет: FormApp не умеет его создавать (см. docs/report-form.md, ручной шаг).
  FIELDS: [
    { title: 'Ник / имя для связи', type: 'text', required: true, help: 'Как к вам обратиться. Ник виден только хосту. Отчёты без имени не принимаются' },
    { title: 'Псевдоним из диагностики', type: 'text', help: 'Player-xxxx из конца окна скрипта диагностики (по нему сопоставим архив с вами)' },
    { title: 'Версия сборки', type: 'text', required: true, help: 'Дата последнего обновления сборки или номер из Prism. Не знаете — «не знаю»' },
    { title: 'Пункт «Что нового»', type: 'list', help: 'W1…W10 из docs/whats-new-check.md', choices: ['W1', 'W2', 'W3', 'W4', 'W5', 'W6', 'W7', 'W8', 'W9', 'W10', 'другое'] },
    { title: 'Где играли', type: 'choice', required: true, choices: ['Одиночная игра', 'Сервер друзей'] },
    { title: 'Мод или предмет', type: 'text', help: 'Подсказка Jade при наведении или вкладка Mods. Не знаете — «не знаю»' },
    { title: WISH_QUESTION.title, type: 'paragraph', required: true, help: WISH_QUESTION.help },
    { title: 'Шаги, чтобы повторить', type: 'paragraph', help: '1–2–3. Не повторяется — напишите' },
    { title: 'Как часто', type: 'choice', choices: ['Один раз', 'Иногда', 'Каждый раз'] },
    { title: 'Категория', type: 'choice', help: WISH_CATEGORY_HELP, choices: null },   // choices = CFG.FORM_CATEGORIES
    { title: 'Ссылка spark', type: 'text', help: 'При лагах: в мире /spark profiler start, 2–3 минуты игры, /spark profiler stop, ссылку из чата сюда' },
    { title: 'Ссылка на файлы', type: 'text', help: 'Если файл большой: ссылка на облако «доступ по ссылке», срок — неделя' },
    { title: 'ОС, видеокарта, память для игры', type: 'text', help: 'Например: Windows 11, RTX 2060, 8192 МБ' }
  ]
};

function addFormItem_(form, f) {
  var item;
  if (f.type === 'text') item = form.addTextItem();
  else if (f.type === 'paragraph') item = form.addParagraphTextItem();
  else if (f.type === 'list') item = form.addListItem().setChoiceValues(f.choices || CFG.CATEGORIES);
  else item = form.addMultipleChoiceItem().setChoiceValues(f.choices || CFG.FORM_CATEGORIES);
  item.setTitle(f.title);
  if (f.help) item.setHelpText(f.help);
  item.setRequired(!!f.required);
  return item;
}

// Создаёт форму один раз: id в свойствах документа; повторный запуск открывает ту же форму и заново ставит настройки.
function ensureForm_() {
  var props = PropertiesService.getDocumentProperties();
  var id = props.getProperty('formId'), form = null, created = false;
  if (id) { try { form = FormApp.openById(id); } catch (err) { form = null; } }
  if (!form) {
    form = FormApp.create(FORM.TITLE);
    FORM.FIELDS.forEach(function (f) { addFormItem_(form, f); });
    // FormApp умеет только включить сбор адресов; тип «Подтверждённые» ставится вручную (лист «Начало», docs/report-form.md).
    // Только при создании: повторный запуск не должен сбрасывать то, что владелец выставил руками.
    form.setCollectEmail(true);
    props.setProperty('formId', form.getId());
    created = true;
  }
  form.setTitle(FORM.TITLE).setDescription(FORM.DESCRIPTION).setConfirmationMessage(FORM.CONFIRMATION);
  if (!created) optional_('пожелания в форме', function () { applyWish_(form); });
  form.setAllowResponseEdits(false).setLimitOneResponsePerUser(false).setProgressBar(false);
  return { form: form, created: created };
}

// Добавляет в существующую форму пожелания: пункт «пожелание / идея» в «Категории» и новое название/подсказку общего вопроса.
// Ищет по заголовку; повторный запуск ничего не меняет. Бросает ошибку, если вопроса «Категория» нет (тогда пункт добавить руками, docs/report-form.md).
function applyWish_(form) {
  var found = { question: false, category: false }, items = form.getItems();
  items.forEach(function (it) {
    var t = it.getTitle();
    if (t === WISH_QUESTION.old || t === WISH_QUESTION.title) {
      found.question = true;
      if (t !== WISH_QUESTION.title) it.setTitle(WISH_QUESTION.title);
      it.setHelpText(WISH_QUESTION.help);
    } else if (t === 'Категория') {
      var q = null;
      try { q = it.asMultipleChoiceItem(); } catch (e1) { q = it.asListItem(); }
      var vals = q.getChoices().map(function (c) { return c.getValue(); });
      if (vals.indexOf(CFG.WISH_CHOICE) < 0) q.setChoiceValues(vals.concat([CFG.WISH_CHOICE]));
      q.setHelpText(WISH_CATEGORY_HELP);
      found.category = true;
    }
  });
  if (!found.category) throw new Error('В форме нет вопроса «Категория»: добавьте пункт «' + CFG.WISH_CHOICE + '» вручную (docs/report-form.md, «Пожелания»).');
  if (!found.question) Logger.log('addWishOption: вопрос «' + WISH_QUESTION.old + '» не найден — название и подсказку поправьте вручную');
  return found;
}

// Можно запустить отдельно (редактор Apps Script → addWishOption): только форма. Полное обновление (списки статусов, «Сводка») делает bootstrap().
function addWishOption() {
  var id = PropertiesService.getDocumentProperties().getProperty('formId');
  if (!id) throw new Error('Форма ещё не создана через bootstrap(): запустите bootstrap().');
  var res = applyWish_(FormApp.openById(id));
  Logger.log('addWishOption: готово (' + JSON.stringify(res) + ')');
}

// Какой из листов появился после связывания формы: before — id листов до, sheets — [{id, name}] после.
function pickNewSheet(before, sheets) {
  for (var i = 0; i < sheets.length; i++) if (before.indexOf(sheets[i].id) < 0) return sheets[i];
  return null;
}

function linkDestination_(form, ss) {
  var props = PropertiesService.getDocumentProperties();
  if (props.getProperty('responsesSheetId')) return;
  var before = ss.getSheets().map(function (x) { return x.getSheetId(); });
  form.setDestination(FormApp.DestinationType.SPREADSHEET, ss.getId());
  var found = null;
  for (var k = 0; k < 10 && !found; k++) {   // лист появляется не сразу
    SpreadsheetApp.flush();
    found = pickNewSheet(before, ss.getSheets().map(function (x) { return { id: x.getSheetId(), name: x.getName() }; }));
    if (!found) Utilities.sleep(1000);
  }
  if (!found) throw new Error('Лист ответов не появился. Свяжите форму вручную (вкладка «Ответы» → Связать с Таблицами → выбрать эту Таблицу) и запустите bootstrap() ещё раз.');
  props.setProperty('responsesSheetId', String(found.id));
  // Пустой «Лист1» до формы больше не нужен.
  ss.getSheets().forEach(function (x) { if (x.getSheetId() !== found.id && x.getLastRow() === 0 && x.getName() === 'Лист1' && ss.getSheets().length > 1) ss.deleteSheet(x); });
}

// Тексты листа «Начало» (строки A:B). Ссылки видны только владельцу Таблицы.
function startRows(publishedUrl, editUrl) {
  return [
    ['Ссылка на форму для друзей', publishedUrl],
    ['Редактор формы (только владельцу)', editUrl],
    ['', ''],
    ['Ручной шаг 1 (обязательно)', 'Редактор формы → вкладка «Настройки» → «Ответы» → «Собирать адреса электронной почты» → «Подтверждённые». Скрипт может только включить сбор адресов, тип выбирается руками.'],
    ['Ручной шаг 2 (по желанию)', 'Редактор формы → «+» (Добавить вопрос) → тип «Загрузка файла» → название «Файлы», «Необязательный вопрос». Скрипт не умеет создавать этот вопрос. Включает вход в Google (адрес и так собирается). Без него остаётся поле «Ссылка на файлы».'],
    ['Ручной шаг 3', 'Ответы → ⋮ → «Получать уведомления о новых ответах по электронной почте».'],
    ['Письмо автору', 'После отправки отчёта скрипт шлёт автору короткое письмо с номером R-### (только номер и ссылка на report-status, без ника и текста отчёта). При первом запуске Google спросит разрешение «Отправлять письма от вашего имени». Лимит — около 100 писем в день. Выключить: CFG.SEND_CONFIRMATION = false в Code.gs.'],
    ['Ответ автору', 'Раз в 15 минут скрипт шлёт автору текст из колонки «Ответ автору» (если он не начинается с [ЧЕРНОВИК], «Ответ отправлен (дата)» пусто и адрес корректен) и ставит дату. Не больше 20 писем за запуск. Выключить: CFG.SEND_REPLIES = false. Подробности — docs/report-form.md.'],
    ['Пожелания', 'Категория «пожелание / идея» — идеи игроков. Номера общие R-###, статусы свои: идея → принята → в реализации → реализовано, а также нужно решение владельца / отклонено / отложено. Что можно делать самим, а что — только с решением владельца: docs/report-form.md, «Пожелания».'],
    ['', ''],
    ['Что делает скрипт сам', 'Номера R-###, статусы, подсветка, листы «Сводка», «По номеру», «По автору», «По людям», «Кто что ждёт», триггеры. Повторный запуск bootstrap() форму не дублирует.'],
    ['Приватность', 'Таблица и этот лист содержат ники и адреса. Не делитесь Таблицей и не публикуйте листы. Ссылки на форму и Таблицу в git не вписываются. Публичная страница: python3 -I scripts/report-status.py <csv>.']
  ];
}

function writeStart_(ss, form) {
  var t = ss.getSheetByName('Начало') || ss.insertSheet('Начало');
  t.clear();
  var rows = startRows(form.getPublishedUrl(), form.getEditUrl());
  t.getRange(1, 1, rows.length, 2).setValues(rows);
  t.setColumnWidth(1, 260); t.setColumnWidth(2, 700);
  t.getRange(1, 2, rows.length, 1).setWrap(true);
  ss.setActiveSheet(t);
  ss.moveActiveSheet(1);
}

// ОДИН раз: создать пустую Таблицу → Расширения → Apps Script → вставить этот файл → запустить bootstrap() и разрешить доступ.
function bootstrap() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var r = ensureForm_();
  linkDestination_(r.form, ss);
  setupThenStart_(ss, r.form);
}

// «Начало» пишем, даже если setup() упал; повторный запуск идемпотентен. Ошибка setup() не теряется: если сбой writeStart_
// случился уже после неё, наружу уходит исходная ошибка setup(); если setup() прошёл, а writeStart_ упал — бросаем его ошибку.
function setupThenStart_(ss, form) {
  var failed = true;
  try { setup(); failed = false; }
  finally {
    try { writeStart_(ss, form); }
    catch (e2) { if (!failed) throw e2; Logger.log('bootstrap: «Начало» не записано (setup тоже упал): ' + e2); }
  }
}

// Необязательное оформление: сбой (например, лист стал «Таблицей» Google) не обрывает setup().
function optional_(name, fn) {
  try { fn(); } catch (e) { Logger.log('setup: «' + name + '» пропущено: ' + e); }
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
    var open = g.items.filter(function (x) { return !isDoneStatus_(x.status); }).length;
    g.items.sort(function (a, b) { return a.number < b.number ? -1 : a.number > b.number ? 1 : 0; });
    return { open: open, key: k, row: [g.email, g.nick, g.items.length, open,
      g.items.map(function (x) { return x.number + ' (' + (x.status || 'без статуса') + ')'; }).join(', '),
      g.lastReply || '', g.unanswered ? 'да' : 'нет'] };
  });
  out.sort(function (a, b) { return b.open - a.open || (a.key < b.key ? -1 : a.key > b.key ? 1 : 0); });
  return out.map(function (x) { return x.row; });
}

// Текст из публичной формы, который уходит на другие листы через setValues: «=…», «+…», «-…», «@…», табуляция и \r в начале
// разобрались бы как формула — ставим впереди апостроф (экранирование Таблиц).
function safeText_(v) {
  return (typeof v === 'string' && /^[=+\-@\t\r]/.test(v)) ? "'" + v : v;
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
  if (res.length) {
    // Ник и адрес приходят из публичной формы: «=IMPORTDATA(…)» в нике стал бы живой формулой. Текстовый формат до записи — значения остаются текстом.
    t.getRange(2, 1, res.length, 2).setNumberFormat('@');
    t.getRange(2, 5, res.length, 1).setNumberFormat('@');
    t.getRange(2, 1, res.length, 7).setValues(res.map(function (row) { return row.map(safeText_); }));
  }
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

// rows — массив {status, number, changed, category}. Баги и пожелания считаются раздельно: верхние числа — только баги, wishes — только пожелания.
// noNumber — по всем строкам.
function summarize(rows, now, days) {
  var r = { received: 0, noNumber: 0, done: 0, open: 0, stale: 0, postponed: 0,
            wishes: { total: 0, open: 0, implemented: 0, rejected: 0, needsOwner: 0 } };
  rows.forEach(function (x) {
    if (!x.number) r.noNumber++;
    if (isWish_(x.category)) {
      var w = r.wishes;
      w.total++;
      if (x.status === 'реализовано') w.implemented++;
      else if (x.status === 'отклонено') w.rejected++;
      else w.open++;
      if (x.status === 'нужно решение владельца') w.needsOwner++;
      return;
    }
    r.received++;
    if (CFG.DONE.indexOf(x.status) >= 0) r.done++;
    else r.open++;
    if (x.status === 'отложено') r.postponed++;
    if (isStale(x.status, x.changed, now, days)) r.stale++;
  });
  return r;
}

// Лист ответов формы: id хранится в свойствах (bootstrap); без него — первый лист, у которого в A1 «Отметка времени»/Timestamp, иначе первый.
function responsesSheet_() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var id = PropertiesService.getDocumentProperties().getProperty('responsesSheetId');
  var sheets = ss.getSheets();
  for (var i = 0; i < sheets.length; i++) if (id && String(sheets[i].getSheetId()) === id) return sheets[i];
  for (var j = 0; j < sheets.length; j++) {
    var a1 = sheets[j].getLastRow() ? String(sheets[j].getRange(1, 1).getValue()) : '';
    if (a1 === 'Отметка времени' || a1 === 'Timestamp') return sheets[j];
  }
  return sheets[0];
}

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
  if (!sh.getRange(row, cS).getValue()) {
    var catCol = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0].indexOf('Категория') + 1;
    sh.getRange(row, cS).setValue(catCol && isWish_(sh.getRange(row, catCol).getValue()) ? 'идея' : 'новый');
  }
  if (!sh.getRange(row, cD).getValue()) sh.getRange(row, cD).setValue(new Date());
}

// Формулы «Сводки», «По номеру», «По автору», «Кто что ждёт». Идемпотентно и дёшево: листы не очищаются, формулы просто перезаписываются
// (перезапись заставляет Таблицу пересчитать их; сами по себе они после записи формы показывали старые значения).
// Колонки ищутся по заголовкам, диапазон QUERY — по текущему числу колонок листа ответов (после ручного вопроса «Файлы» их стало больше).
function writeFormulas_() {
  var ss = SpreadsheetApp.getActiveSpreadsheet(), sh = responsesSheet_();
  var cS = colIndex_(sh, 'Статус'), cN = colIndex_(sh, '№'), cNick = colIndex_(sh, CFG.NICK);
  var sL = letter_(cS), nL = letter_(cN), dL = letter_(colIndex_(sh, 'Дата изменения')), catL = letter_(colIndex_(sh, 'Категория'));
  var chL = letter_(colIndex_(sh, 'Статус изменён (дата)')), rL = letter_(colIndex_(sh, 'Ответ отправлен (дата)'));
  var sum = ss.getSheetByName('Сводка') || ss.insertSheet('Сводка');
  var q = "'" + sh.getName() + "'!";
  var sR = q + sL + '2:' + sL, dR = q + dL + '2:' + dL, nR = q + nL + '2:' + nL, aR = q + 'A2:A', catR = q + catL + '2:' + catL;
  var bug = ',' + catR + ',"<>' + CFG.WISH + '*"', wish = ',' + catR + ',"' + CFG.WISH + '*"';   // условия «только баги» / «только пожелания» для COUNTIFS
  var staleSum = CFG.OPEN.map(function (s) { return 'COUNTIFS(' + sR + ',"' + s + '",' + dR + ',"<"&(TODAY()-' + CFG.STALE_DAYS + ')' + bug + ')'; }).join('+');
  var cL = q + chL + '2:' + chL, pR = q + rL + '2:' + rL;
  // Баги и пожелания считаются раздельно (пожелания — строки с категорией «пожелание…»). Подписи — значениями (иначе «=Получено» → #NAME?), формулы — отдельно.
  var sumRows = [
    ['Баги: получено', '=COUNTA(' + aR + ')-B6'],
    ['Баги: обработано (исправлено, дубль, не баг)', '=' + CFG.DONE.map(function (s) { return 'COUNTIFS(' + sR + ',"' + s + '"' + bug + ')'; }).join('+')],
    ['Баги: висит', '=B1-B2'],
    ['Баги: висят дольше ' + CFG.STALE_DAYS + ' дней', '=' + staleSum],
    ['Баги: отложено', '=COUNTIFS(' + sR + ',"отложено"' + bug + ')'],
    ['Пожелания: всего', '=COUNTIF(' + catR + ',"' + CFG.WISH + '*")'],
    ['Пожелания: открыто (не реализовано и не отклонено)', '=B6-B8-B9'],
    ['Пожелания: реализовано', '=COUNTIFS(' + sR + ',"реализовано"' + wish + ')'],
    ['Пожелания: отклонено', '=COUNTIFS(' + sR + ',"отклонено"' + wish + ')'],
    ['Пожелания: ждут решения владельца', '=COUNTIFS(' + sR + ',"нужно решение владельца"' + wish + ')'],
    ['Без номера (должно быть 0)', '=COUNTA(' + aR + ')-COUNTA(' + nR + ')'],
    ['Ждут ответа автору (баги и пожелания)', '=COUNTIFS(' + nR + ',"<>",' + pR + ',"")+SUMPRODUCT((' + nR + '<>"")*(' + pR + '<>"")*(' + pR + '<' + cL + '))'],
    ['На дату', '=TODAY()']
  ];

  sum.getRange(1, 1, sumRows.length, 1).setValues(sumRows.map(function (x) { return [x[0]]; }));   // подписи — значениями (иначе «=Получено» → #NAME?)
  sum.getRange(1, 2, sumRows.length, 1).setFormulas(sumRows.map(function (x) { return [x[1]]; }));
  sum.autoResizeColumns(1, 2);

  // Служебные листы (заголовки копируются из листа ответов). Содержат ники — только владельцу.
  var lastCol = sh.getLastColumn();
  var qs = buildQueries(sh.getName(), { last: letter_(lastCol), num: nL, nick: letter_(cNick), status: sL });
  [['По номеру', qs.byNumber], ['По автору', qs.byAuthor]].forEach(function (x) {
    var t = ss.getSheetByName(x[0]) || ss.insertSheet(x[0]);
    sh.getRange(1, 1, 1, lastCol).copyTo(t.getRange(1, 1), SpreadsheetApp.CopyPasteType.PASTE_VALUES, false);
    t.getRange(2, 1).setFormula(x[1]);
    t.setFrozenRows(1);
  });
  var who = ss.getSheetByName('Кто что ждёт') || ss.insertSheet('Кто что ждёт');
  who.getRange(1, 1).setFormula(qs.pending);
  who.autoResizeColumns(1, 2);
  SpreadsheetApp.flush();
}

// Пересчёт не должен ломать главное (номер уже выставлен): сбой пишем в журнал.
function refreshFormulas_() {
  try { writeFormulas_(); } catch (e) { Logger.log('writeFormulas_: ' + e); }
}

// Подтверждённый адрес: простой email без пробелов/переводов строк/запятых (MailApp принимает список через запятую), ≤ 254,
// первый символ — буква, цифра, «_» или «.» (не = + - @).
function isValidEmail_(v) {
  return typeof v === 'string' && v.length <= 254 && /^[A-Za-z0-9_.][A-Za-z0-9._%+'-]*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)+$/.test(v);
}

// Тема и текст фиксированные: только номер R-###. Ник, текст отчёта и прочее в письмо не попадают.
function confirmationMail_(number, wish) {
  if (wish) return {
    subject: 'Идея ' + number + ' принята — Minecraft_moded',
    body: 'Спасибо за идею! Номер ' + number + ', статус — на странице report-status: ' + CFG.STATUS_URL + ' Хост ответит в чате.'
  };
  return {
    subject: 'Отчёт ' + number + ' принят — Minecraft_moded',
    body: 'Спасибо! Ваш отчёт принят, номер ' + number + '. Статус — на странице report-status: ' + CFG.STATUS_URL + ' Хост ответит в чате.'
  };
}

function noteMailFail_(sh, row, reason) {
  var cNote = colIndex_(sh, 'Заметка'), cell = sh.getRange(row, cNote), old = cell.getValue();
  cell.setValue((old ? old + '; ' : '') + 'Письмо не отправлено: ' + reason);
}

// Только из onFormSubmit (fillMissing писем не шлёт). Сбой не должен ронять триггер: причина — в «Заметку» (без адреса) и в Logger.
function sendConfirmation_(sh, row) {
  if (!CFG.SEND_CONFIRMATION) return;
  try {
    var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
    var cMail = h.indexOf('Письмо отправлено (дата)') + 1;
    if (!cMail) throw new Error('нет колонки «Письмо отправлено (дата)» (запустите setup())');
    if (sh.getRange(row, cMail).getValue()) return;
    var cE = emailColIndex_(h);
    var addr = cE ? sh.getRange(row, cE).getValue() : '';
    var number = String(sh.getRange(row, colIndex_(sh, '№')).getValue());
    if (!isValidEmail_(addr)) { noteMailFail_(sh, row, 'нет корректного адреса'); return; }
    if (!/^R-\d+$/.test(number)) { noteMailFail_(sh, row, 'нет номера'); return; }
    if (MailApp.getRemainingDailyQuota() <= 0) { noteMailFail_(sh, row, 'исчерпан дневной лимит писем'); return; }
    var cCat = h.indexOf('Категория') + 1;
    var m = confirmationMail_(number, !!cCat && isWish_(sh.getRange(row, cCat).getValue()));
    MailApp.sendEmail(addr, m.subject, m.body);
    sh.getRange(row, cMail).setValue(new Date());
  } catch (err) {
    Logger.log('sendConfirmation_: ' + err);
    try { noteMailFail_(sh, row, 'ошибка отправки (подробности в журнале выполнений)'); } catch (e2) { Logger.log('sendConfirmation_: заметка не записана: ' + e2); }
  }
}

// Тема только с номером R-###; тело — текст ответа (plain text, ≤ MAX_REPLY_CHARS) и ссылка на статус. Ника и адреса нет.
function replyMail_(number, text) {
  return {
    subject: 'Ответ по отчёту ' + number + ' — Minecraft_moded',
    body: String(text).substring(0, CFG.MAX_REPLY_CHARS) + '\n\nСтатус: ' + CFG.STATUS_URL
  };
}

// Черновик, если в начале (первые 40 знаков) есть «чернов…» в любом виде: «[ЧЕРНОВИК]», «[ черновик ]», «ЧЕРНОВИК:», с неразрывным пробелом,
// невидимыми символами и латинскими двойниками букв (e, o, p, c, x, a, k…). Ошибка здесь — в сторону «не отправлять».
var LATIN_LOOKALIKES_ = { a: 'а', b: 'в', c: 'с', e: 'е', h: 'н', k: 'к', m: 'м', o: 'о', p: 'р', t: 'т', x: 'х', y: 'у' };
function isDraftReply_(text) {
  var t = String(text).replace(/[\u00ad\u200b-\u200f\u2060\ufeff]/g, '').toLowerCase().substring(0, 40);
  t = t.replace(/[a-z]/g, function (ch) { return LATIN_LOOKALIKES_[ch] || ch; });
  return t.indexOf('чернов') >= 0 || /draft/i.test(String(text).substring(0, 40));
}

// Причина в «Заметку» без адреса; одну и ту же причину не дописываем при каждом запуске таймера.
function noteReplyFail_(sh, row, reason) {
  var cell = sh.getRange(row, colIndex_(sh, 'Заметка')), old = String(cell.getValue() || ''), msg = 'Ответ не отправлен: ' + reason;
  if (old.indexOf(msg) >= 0) return;
  cell.setValue((old ? old + '; ' : '') + msg);
}

// ---- проверка текста ответа перед отправкой (порт защит scripts/report-status.py) ----
var W_ = '\\p{L}\\p{N}_';
var COMMON_WORDS_ = ['gmail', 'mail', 'yandex', 'example', 'telegram', 'discord', 'steam', 'email', 'tg', 'com'];
var GUARD_TITLES_ = ['Контакт', 'Псевдоним из диагностики'];
var ALLOWED_URL_PREFIXES_ = ['github.com/limanov7kiy/Minecraft_moded'];
var DANGER_ = [
  ['IP-адрес', /\b\d{1,3}(?:\.\d{1,3}){3}\b/],
  ['IPv6', /\b(?:[0-9a-f]{1,4}:){3,}[0-9a-f]{1,4}\b/i],
  ['путь Windows', /\b[A-Za-z]:[\\/]/],
  ['путь Windows', /\\\\|\\Users\\|%\w+%/i],
  ['путь Linux/macOS', /(?:^|[\s(])\/(?:home|Users|root|tmp|var|etc)\//],
  ['ссылка Диска/spark/формы', /spark\.lucko|drive\.google|docs\.google|forms\.gle|forms\.google|dropbox|mega\.nz|mediafire|cdn\.discord|discord\.gg/i],
  ['упоминание @', /@[\p{L}\p{N}_]/u],
  ['ссылка на профиль/мессенджер', /t\.me\/|telegram\.(?:me|org)|discord\.com\/users|steamcommunity|vk\.com|wa\.me\//i],
  ['HTML-сущность', /&#?[\p{L}\p{N}_]+;/u],
  ['e-mail', /\b[\w.+-]+@[\w-]+\.[\w.-]+\b/],
  ['markdown-ссылка', /\]\(/],
  ['псевдоним диагностики', /\bPlayer-[0-9a-f]{3,}\b|\bPlayer-xxxx\b|diag-Player/i]
];

// Слова, которых не должно быть в публичном тексте: ники, контакты, адреса (токены от 3 знаков; из адреса — целиком, локальная часть и слова).
function guardTokens_(values) {
  var out = {};
  values.forEach(function (v) {
    v = String(v || '').trim().toLowerCase();
    if (!v) return;
    var pieces = [v].concat(v.split(/[\s,;@\/]+/));
    pieces.slice().forEach(function (part) { pieces = pieces.concat(part.split(new RegExp('[^' + W_ + ']+', 'u'))); });
    pieces.forEach(function (t) { if (t.length >= 3) out[t] = true; });
  });
  return out;
}

// Токены всех авторов (от 4 знаков, без почтовых доменов) минус токены адресата: его имя в обращении допустимо.
function foreignTokens_(rowsValues, ownIdx) {
  var own = {}, other = {};
  rowsValues.forEach(function (vals, i) {
    var t = guardTokens_(vals);
    Object.keys(t).forEach(function (k) { (ownIdx[i] ? own : other)[k] = true; });
  });
  return Object.keys(other).filter(function (t) { return t.length >= 4 && COMMON_WORDS_.indexOf(t) < 0 && !own[t]; });
}

function hasToken_(text, tokens) {
  var low = String(text).toLowerCase();
  var words = {}, wordList = low.split(new RegExp('[^' + W_ + ']+', 'u'));
  wordList.forEach(function (w) { if (w) words[w] = true; });
  for (var i = 0; i < tokens.length; i++) {
    var t = tokens[i];
    if (new RegExp('[^' + W_ + ']', 'u').test(t)) {   // токен с пунктуацией (адрес целиком) — поиск подстроки по границам слов
      if (new RegExp('(?<![' + W_ + '])' + t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + '(?![' + W_ + '])', 'u').test(low)) return true;
      continue;
    }
    if (words[t]) return true;
    if (t.length >= 4 && /[а-яё]/.test(t)) {   // падежи: основа без последней буквы + до двух букв
      var stem = t.slice(0, -1);
      for (var w = 0; w < wordList.length; w++) { var x = wordList[w]; if (x.length >= stem.length && x.length <= stem.length + 2 && x.indexOf(stem) === 0) return true; }
    }
  }
  return false;
}

// Причина запрета (без цитаты) или ''. Разрешённые URL — CFG.STATUS_URL и репозиторий.
// Какие признаки нельзя «разрезать» переносом строки или пробелом (как в report-status.py): проверяем и на тексте без пробелов вообще.
var JOINABLE_ = ['ссылка Диска/spark/формы', 'упоминание @', 'ссылка на профиль/мессенджер', 'e-mail', 'markdown-ссылка'];

// Посторонняя ссылка в тексте: любая, кроме CFG.STATUS_URL и репозитория проекта.
function badUrl_(t) {
  var urls = t.match(/(?:https?|ftp):\/\/[^\s<>"')\]]+|\bwww\.[^\s<>"')\]]+/gi) || [];
  for (var i = 0; i < urls.length; i++) {
    var u = urls[i], bare = u.replace(/^(?:https?|ftp):\/\//i, '').replace(/^www\./i, '');
    var ok = u.indexOf(CFG.STATUS_URL) === 0 || ALLOWED_URL_PREFIXES_.some(function (a) {
      var b = bare.toLowerCase(), pre = a.toLowerCase();
      return /^https:\/\//i.test(u) && b.indexOf(pre) === 0 && /^[\/?#.,;:!)]?$|^[\/?#]/.test(b.substring(pre.length));
    });
    if (!ok) return true;
  }
  return false;
}

// Причина запрета (без цитаты) или ''. Разрешённые URL — CFG.STATUS_URL и репозиторий. Текст проверяется как есть, с переносами,
// заменёнными пробелом, и (для адресов и ссылок) без пробелов: «masha@\nexample.com» или «ht\ntps://…» не проходят.
function replyTextProblem_(text, tokens) {
  var t = String(text), spaced = t.replace(/\s+/g, ' '), joined = t.replace(/\s+/g, '');
  if (badUrl_(t) || badUrl_(joined)) return 'в тексте посторонняя ссылка';
  for (var j = 0; j < DANGER_.length; j++) {
    var rx = DANGER_[j][1];
    if (rx.test(t) || rx.test(spaced) || (JOINABLE_.indexOf(DANGER_[j][0]) >= 0 && rx.test(joined))) return 'в тексте похоже на ' + DANGER_[j][0];
  }
  if (hasToken_(t, tokens)) return 'в тексте найден ник/контакт/адрес другого автора';
  return '';
}

// Триггер по времени (sendReplies, раз в 15 минут). Ответ уходит один раз: после отправки ставится «Ответ отправлен (дата)»;
// правка текста потом письма не повторяет — чтобы ответить снова, очистите эту ячейку вручную.
function sendReplies_() {
  if (!CFG.SEND_REPLIES) return;
  var lock = LockService.getDocumentLock();
  try { lock.waitLock(30000); } catch (e) { Logger.log('sendReplies_: занято, пропуск запуска'); return; }
  try {
    var sh = responsesSheet_();
    var last = sh.getLastRow();
    if (last < 2) return;
    var limit = Math.min(CFG.MAX_REPLIES_PER_RUN, MailApp.getRemainingDailyQuota() - CFG.QUOTA_RESERVE);
    if (limit <= 0) { Logger.log('sendReplies_: квота писем почти исчерпана'); return; }
    var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
    var cReply = h.indexOf('Ответ автору') + 1, cSent = h.indexOf('Ответ отправлен (дата)') + 1, cNum = h.indexOf('№') + 1, cE = emailColIndex_(h);
    if (!cReply || !cSent || !cNum || !cE) { Logger.log('sendReplies_: нет нужных колонок (запустите setup())'); return; }
    var data = sh.getRange(2, 1, last - 1, h.length).getValues();
    var guardCols = [h.indexOf(CFG.NICK)].concat(CFG.EMAIL_TITLES.map(function (x) { return h.indexOf(x); }), GUARD_TITLES_.map(function (x) { return h.indexOf(x); })).filter(function (x) { return x >= 0; });
    var guardVals = null;   // лениво: нужны только если есть что слать
    var sentCount = 0;
    for (var i = 0; i < data.length && sentCount < limit; i++) {
      var row = i + 2, text = data[i][cReply - 1];
      if (!text || !String(text).trim() || isDraftReply_(text) || data[i][cSent - 1]) continue;
      try {
        var addr = data[i][cE - 1], number = String(data[i][cNum - 1]);
        if (!isValidEmail_(addr)) { noteReplyFail_(sh, row, 'нет корректного адреса'); continue; }
        if (!/^R-\d+$/.test(number)) { noteReplyFail_(sh, row, 'нет номера'); continue; }
        if (!guardVals) guardVals = data.map(function (r) { return guardCols.map(function (c) { return r[c]; }); });
        var me = String(addr).toLowerCase();
        var ownIdx = data.map(function (r, k) { return k === i || String(r[cE - 1]).toLowerCase() === me; });
        var problem = replyTextProblem_(text, foreignTokens_(guardVals, ownIdx));
        if (problem) { noteReplyFail_(sh, row, problem); continue; }
        var m = replyMail_(number, text);
        // Дату ставим ДО отправки: если запись после отправки не удалась бы, следующий запуск прислал бы письмо второй раз.
        // При сбое отправки дату снимаем, чтобы повторить на следующем запуске.
        sh.getRange(row, cSent).setValue(new Date());
        try {
          MailApp.sendEmail(addr, m.subject, m.body);
        } catch (sendErr) {
          sh.getRange(row, cSent).setValue('');
          throw sendErr;
        }
        sentCount++;
      } catch (err) {
        Logger.log('sendReplies_: строка ' + row + ': ' + err);
        try { noteReplyFail_(sh, row, 'ошибка отправки (подробности в журнале выполнений)'); } catch (e2) { Logger.log('sendReplies_: заметка не записана: ' + e2); }
      }
    }
  } finally { lock.releaseLock(); }
}
function sendReplies() { sendReplies_(); }

// Установленный триггер «при отправке формы».
function onFormSubmit(e) {
  var lock = LockService.getDocumentLock();
  lock.waitLock(30000);
  try {
    var sh = responsesSheet_(), row = e.range.getRow();
    stamp_(sh, row);
    sendConfirmation_(sh, row);   // письмо с номером — сразу после нумерации, до листов-зеркал: их сбой не должен лишать автора письма
    rebuildPeople_(); refreshFormulas_();
  } finally { lock.releaseLock(); }
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
    refreshFormulas_();
  } finally { lock.releaseLock(); }
}

// Простой триггер: правка рабочих колонок обновляет «Дату изменения».
function onEdit(e) {
  var sh = e.range.getSheet();
  if (sh.getSheetId() !== responsesSheet_().getSheetId() || e.range.getRow() < 2) return;
  var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
  var cols = CFG.EDIT_TRIGGERS.map(function (t) { return h.indexOf(t) + 1; });
  if (cols.indexOf(e.range.getColumn()) < 0) return;
  var now = new Date();
  sh.getRange(e.range.getRow(), h.indexOf('Дата изменения') + 1).setValue(now);
  if (e.range.getColumn() === h.indexOf('Статус') + 1) sh.getRange(e.range.getRow(), h.indexOf('Статус изменён (дата)') + 1).setValue(now);
  // Список статусов в Таблице общий, поэтому «реализовано» у бага или «исправлено» у пожелания она пропустит: помечаем ячейку примечанием (не запрещаем).
  var cStat = h.indexOf('Статус') + 1, cCat = h.indexOf('Категория') + 1;
  if (cStat && cCat && (e.range.getColumn() === cStat || e.range.getColumn() === cCat)) {
    var row = e.range.getRow(), status = sh.getRange(row, cStat).getValue(), cat = sh.getRange(row, cCat).getValue();
    sh.getRange(row, cStat).setNote(statusValid(cat, status) ? '' : 'Статус «' + status + '» не подходит категории «' + cat + '». ' +
      (isWish_(cat) ? 'Для пожеланий: ' + CFG.WISH_STATUSES.join(', ') : 'Для багов: ' + CFG.STATUSES.join(', ')));
  }
}

// Один раз: колонки, списки, подсветка, сводка, триггеры.
function setup() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  // Формулы ниже написаны с запятой-разделителем (en_US); в русской локали (разделитель «;») они дают #ERROR!. Не optional_: до записи формул.
  ss.setSpreadsheetLocale('en_US');
  Logger.log('setup: локаль Таблицы сменена на en_US (разделитель аргументов в формулах — запятая)');
  var sh = responsesSheet_();
  var h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0];
  CFG.EXTRA.forEach(function (t) {
    if (h.indexOf(t) < 0) { sh.getRange(1, sh.getLastColumn() + 1).setValue(t); h = sh.getRange(1, 1, 1, sh.getLastColumn()).getValues()[0]; }
  });
  if (!emailColIndex_(h)) sh.getRange(1, sh.getLastColumn() + 1).setValue(CFG.EMAIL_TITLES[1]);
  var cS = colIndex_(sh, 'Статус'), cD = colIndex_(sh, 'Дата изменения'), cN = colIndex_(sh, '№');

  // Главное — сначала: триггеры. Остальное (оформление) необязательно.
  ScriptApp.getProjectTriggers().forEach(function (t) { ScriptApp.deleteTrigger(t); });
  ScriptApp.newTrigger('onFormSubmit').forSpreadsheet(ss).onFormSubmit().create();
  ScriptApp.newTrigger('fillMissing').timeBased().everyDays(1).atHour(9).create();
  ScriptApp.newTrigger('sendReplies').timeBased().everyMinutes(15).create();

  var sL = letter_(cS), dL = letter_(cD), chL = letter_(colIndex_(sh, 'Статус изменён (дата)')), rL = letter_(colIndex_(sh, 'Ответ отправлен (дата)')), nL = letter_(cN);
  optional_('закрепление строки', function () { sh.setFrozenRows(1); });
  optional_('список статусов', function () {
  var rule = SpreadsheetApp.newDataValidation().requireValueInList(CFG.ALL_STATUSES, true).setAllowInvalid(false).build();
  sh.getRange(2, cS, CFG.MAX_ROWS, 1).setDataValidation(rule);
  });

  optional_('подсветка', function () {
  var rng = sh.getRange(2, 1, CFG.MAX_ROWS, sh.getLastColumn());
  // Оранжевым: есть номер, а ответ автору не отправлен или отправлен раньше последней смены статуса.
  var reply = SpreadsheetApp.newConditionalFormatRule()
    .whenFormulaSatisfied('=AND($' + nL + '2<>"",OR($' + rL + '2="",AND($' + chL + '2<>"",$' + rL + '2<$' + chL + '2)))')
    .setBackground('#fce5cd').setRanges([rng]).build();
  var openCond = 'OR($' + sL + '2="",' + CFG.OPEN.concat(CFG.WISH_OPEN).map(function (s) { return '$' + sL + '2="' + s + '"'; }).join(',') + ')';
  var stale = SpreadsheetApp.newConditionalFormatRule()
    .whenFormulaSatisfied('=AND($A2<>"",' + openCond + ',OR($' + dL + '2="",TODAY()-$' + dL + '2>' + CFG.STALE_DAYS + '))')
    .setBackground('#f4c7c3').setRanges([rng]).build();
  var done = SpreadsheetApp.newConditionalFormatRule()
    .whenFormulaSatisfied('=OR(' + CFG.DONE.concat(CFG.WISH_DONE).map(function (s) { return '$' + sL + '2="' + s + '"'; }).join(',') + ')')
    .setBackground('#d9ead3').setRanges([rng]).build();
  sh.setConditionalFormatRules([reply, stale, done]);
  });

  var cCat = colIndex_(sh, 'Категория');
  writeFormulas_();
  rebuildPeople_();

  optional_('список категорий', function () {
    sh.getRange(2, cCat, CFG.MAX_ROWS, 1).setDataValidation(
      SpreadsheetApp.newDataValidation().requireValueInList(CFG.SHEET_CATEGORIES, true).setAllowInvalid(true).build());
  });
  // Лист ответов — «Таблица» Google: createFilter на нём падает. Фильтр необязателен; вручную: Данные → Создать фильтр.
  optional_('фильтр', function () {
    if (!sh.getFilter()) sh.getRange(1, 1, Math.max(sh.getLastRow(), 2), sh.getLastColumn()).createFilter();
  });
  fillMissing();
}

if (typeof module !== 'undefined') { module.exports = { applyWish_: applyWish_, addWishOption: addWishOption, isWish_: isWish_, statusValid: statusValid, onEdit: onEdit, WISH_QUESTION: WISH_QUESTION, setupThenStart_: setupThenStart_, formatNumber: formatNumber, isStale: isStale, needsReply: needsReply, FORM: FORM, ensureForm_: ensureForm_, pickNewSheet: pickNewSheet, startRows: startRows, safeText_: safeText_, isValidEmail_: isValidEmail_, confirmationMail_: confirmationMail_, sendReplies_: sendReplies_, replyTextProblem_: replyTextProblem_, replyMail_: replyMail_, writeFormulas_: writeFormulas_, onFormSubmit: onFormSubmit, fillMissing: fillMissing, rebuildPeople_: rebuildPeople_, setup: setup, writeStart_: writeStart_, addFormItem_: addFormItem_, bootstrap: bootstrap, buildQueries: buildQueries, groupPeople: groupPeople, emailColIndex_: emailColIndex_, summarize: summarize, letter_: letter_, CFG: CFG }; }
