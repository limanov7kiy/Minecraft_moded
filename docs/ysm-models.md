# Модели игроков Yes Steve Model (YSM 2.6.5)

Мод `yes-steve-model` (сторона `both`) заменяет модель игрока. Модели лежат на сервере в
`config/yes_steve_model/custom/`; сервер сам раздаёт их всем клиентам (в зашифрованном виде),
а в одиночной игре их читает встроенный сервер из той же папки клиента. После правки моделей на сервере —
`/ysm model reload` (оператор).

macOS: YSM на Mac не работает. В облаке (виртуальная машина) клиент YSM тоже не запускается
(`docs/compat.md` К7), поэтому модели в игре проверяют игроки на своих ПК — чек-лист внизу.

## Как выбрать модель
1. В *Настройки → Управление* найти раздел **Yes Steve Model** и назначить клавиши — в сборке клавиши модов
   по умолчанию не назначены (если клавиш много — поиск по «Yes Steve» в Controlling). Нужны:
   **меню моделей** и **колесо анимаций** (позы, настройки модели).
2. Меню моделей → слева разделы-«пакеты»: *Arknights*, *Arknights: Endfield*, *Punishing: Gray Raven*,
   встроенные (Default, Wine Fox) и **Priestess** (наша). Выбрать модель и, если есть, вариант текстуры.
3. Свою модель видно в виде от третьего лица (F5); друзья видят её на сервере сразу.
4. Колесо анимаций: позы и кнопка настроек модели (у Priestess — пункт «Настройки»).
   Позу «закрепить», чтобы ходьба её не прерывала, — замок в колесе (Alt+L).

## Как раздаются модели
- **Готовые модели** (65 файлов, ~31 МБ) в наш репозиторий не копируются: в `pack/config/yes_steve_model/custom/<пакет>/`
  лежат packwiz-метафайлы `*.pw.toml` со ссылкой на файл в репозитории
  [Elaina69/Yes-Steve-Model-Repo](https://github.com/Elaina69/Yes-Steve-Model-Repo) (закреплён коммит `1749b464`,
  хэш sha256 проверяется). Клиенты получают файлы через packwiz-installer или `.mrpack` (ссылки на raw.githubusercontent.com),
  сервер — `scripts/fetch-config-files.py` (его вызывают `test-server.sh` и `export-pack.sh`).
  Обновить/перегенерировать: `scripts/ysm-models.py <клон Elaina69/Yes-Steve-Model-Repo>` (инструкция в скрипте), затем `packwiz refresh`.
- **Priestess** — папка-модель в `pack/config/yes_steve_model/custom/priestess/` (~1 МБ), собирается
  `mods-src/ysm-priestess/build.py` (см. ниже).

## Готовые модели (Elaina69/Yes-Steve-Model-Repo)
Все модели из папок `_Arknights`, `_ArknightsEndfield`, `_PunishingGrayRaven` репозитория Elaina69 (сборщик модельного
архива; авторы моделей — ниже, имена как в файлах). Решение владельца: лицензии моделей (в т.ч. NC/ND/ARR) нас не
ограничивают — сборка некоммерческая (CLAUDE.md «Назначение и лицензии»), авторство указываем.

Проверка на сервере (`scripts/test-server.sh`, 2026-10-17): сервер доходит до `Done`, YSM прочитал **65 из 66** моделей
(+ 27 встроенных) — счёт по серверному кэшу `config/yes_steve_model/cache/server`, сама YSM в лог по моделям не пишет;
непрочитанная найдена делением набора пополам. Формат `.ysm` новее, чем умеет 2.6.5, в наборе нет (самые новые — формат 26).

Не читается: `AK_Rosmontis_v0.9.ysm` (формат 26) — YSM 2.6.5 не загружает её даже одну (прогон с одним этим файлом: 0 моделей в кэше), в паре с v1.0 грузится только v1.0. **Не раздаём** (`skip` в `scripts/ysm-models.py`); остаются v1.0, v1.1, v1.3 и две старые zip-версии Rosmontis. Итого раздаётся **65** моделей.

| Пакет | Файл | Имя в модели | Авторы (из файла) | Лицензия (из файла) | Формат |
|---|---|---|---|---|---|
| Arknights | `AK_Chen.ysm` | 陈 | 日月明567 (模型) | All Rights Reserved | .ysm, формат 15 |
| Arknights | ~~`AK_Rosmontis_v0.9.ysm`~~ (не читается, не раздаём) | Rosmontis | 平行Rosmontis (ALL) | CC BY-NC-ND 4.0 | .ysm, формат 26 |
| Arknights | `AK_Rosmontis_v1.0.ysm` | Rosmontis | 平行Rosmontis (ALL) | CC BY-NC-ND 4.0 | .ysm, формат 26 |
| Arknights | `AK_Sora_v1.0_Fllz.ysm` | Sora_Default | Fllz (Model Author) | All Rights Reserved | .ysm, формат 15 |
| Arknights | `Ak_Angelina.ysm` | 安洁莉娜_free | 映白L (作者) | CC BY-NC-SA 4.0 | .ysm, формат 15 |
| Arknights | `Ak_Aurora.ysm` | 极光 | 映白L (作者) | CC BY-NC-SA 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Cannot_Goodenough.ysm` | 坎诺特 · 古德因纳夫 | 布萊德 · 基德 (模型) | CC 4.0 NC-BY-SA | .ysm, формат 15 |
| Arknights | `Ak_Eblana.ysm` | 爱布拉娜 | MicroCraft (动画和物理参考模仿), BradKidd (模型) | CC 4.0 NC-BY-SA | .ysm, формат 15 |
| Arknights | `Ak_Eblana_1.21-fix-2.ysm` | 爱布拉娜 | MicroCraft (动画和物理参考模仿), BradKidd (模型) | CC 4.0 NC-BY-SA | .ysm, формат 15 |
| Arknights | `Ak_Eblana_1.21-fix.ysm` | 爱布拉娜 | MicroCraft (动画和物理参考模仿), BradKidd (模型) | CC 4.0 NC-BY-SA | .ysm, формат 15 |
| Arknights | `Ak_Fiammetta.ysm` | Default | 哥斯拉 (模型), 端木一动不动 (动画), 甜粽子 (动画), 星屑海螺 (动画) | CC 0 | .ysm, формат 9 |
| Arknights | `Ak_Hoolheyak.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Irene_1.ysm` | 艾丽妮 | 烛火的次元宇宙 (模型), 烛火的次元宇宙 (动画) | CC BY-NC-SA 4.0 | .ysm, формат 15 |
| Arknights | `Ak_Jessica(Defender)_v1.0.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Kaltsit.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Kirara.ysm` | 绮良_free | 映白L (作者) | CC BY-NC-SA 4.0 | .ysm, формат 15 |
| Arknights | `Ak_Kirara_v1.01.ysm` | 绮良_free | 映白L (作者) | CC BY-NC-SA 4.0 | .ysm, формат 15 |
| Arknights | `Ak_LaPluma_v1.0.ysm` | 羽毛笔 | 失踪的李元 (模型) | CC BY-NC-SA 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Magallan.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_May.ysm` | 梅 | 烛火的次元宇宙 (模型) | CC BY-NC-SA 4.0 | .ysm, формат 15 |
| Arknights | `Ak_Mudrock(AmbienceSynesthesia)_0.ysm` | 黑曜石_泥岩 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Mudrock(AmbienceSynesthesia)_1.ysm` | 黑曜石_泥岩 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Mudrock(Coral-Coast)_0.ysm` | 静谧午夜_泥岩 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Mudrock(Coral-Coast)_1.ysm` | 静谧午夜_泥岩 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Mudrock_0.ysm` | 泥岩_原皮肤 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Mudrock_1.1.1.ysm` | 泥岩_砾瓦 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Mudrock_1.5.2.ysm` | 泥岩_砾瓦aa | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 15 |
| Arknights | `Ak_Myrtle.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Nearl(Horse-form).ysm` | 临光 | 烈鸟比百 (人物模型) | CC 0 | .ysm, формат 9 |
| Arknights | `Ak_Nian.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Pepe.ysm` | 佩佩 | 寒叔 (模型) | CC BY-NC-SA 4.0 | .ysm, формат 4 |
| Arknights | `Ak_Projekt-Red(Coral-Coast)_1.ysm` | 红_轻风 LB01 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (材质), 嗯嗯owo (模型) | All Rights Reserved | .ysm, формат 9 |
| Arknights | `Ak_Ptilopsis.ysm` | 白面鸮 - 免费版 | 映白L (作者) | CC BY-NC-SA 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Rosmontis_v1.1.ysm` | 迷迭香 | 平行Rosmontis (迷迭香) | CC 0 | .ysm, формат 9 |
| Arknights | `Ak_Rosmontis_v1.3.ysm` | 迷迭香 | 平行Rosmontis (迷迭香) | CC 0 | .ysm, формат 9 |
| Arknights | `Ak_Saga.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v1 |
| Arknights | `Ak_Shamare(WitchFeast)_v1.0_Fllz.ysm` | Shamare | 哥斯拉 (模型), 端木一动不动 (动画), 甜粽子 (动画), 星屑海螺 (动画), Fllz (Model author) | CC BY | .ysm, формат 15 |
| Arknights | `Ak_Shu.ysm` | 黍 | 烈鸟比百（模型） | All Rights Reserved | .ysm, формат 1 |
| Arknights | `Ak_Sideroca(Coral-Coast)_2.ysm` | 轻风LB03_铸铁 | 嗯嗯owo (模型), 嗯嗯owo (动画), 嗯嗯owo (动画), 嗯嗯owo (动画) | CC BY-NC-ND 4.0 | .ysm, формат 9 |
| Arknights | `Ak_Skadi(Sleepwear).ysm` | 斯卡蒂_free | 映白L (作者) | All Rights Reserved | .ysm, формат 15 |
| Arknights | `Ak_Skadi.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Soldier.ysm` | 整合运动士兵 | 空气避难所 | All Rights Reserved | .ysm, формат 15 |
| Arknights | `Ak_Specter(Speciallist)_v1.0.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Suzuran.ysm` | 铃兰 - 免费版 | 映白L | CC BY-NC-SA 4.0 | .ysm, формат 1 |
| Arknights | `Ak_Texas(Epoque).ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Texas(Speciallist)_v1.0.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Vigna(Coral-Coast).ysm` | 红豆—悠然假日 | 烛火的次元宇宙 (模型), 烛火的次元宇宙 (动画) | CC BY-NC-SA 4.0 | .ysm, формат 15 |
| Arknights | `Ak_Virtuosa.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Waiifu.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights: Endfield | `Endfield_Rossi_v1.1.ysm` | 终末地 | — (в файле не указаны; карточка модели в меню YSM) | -- | .ysm, формат 26 |
| Punishing: Gray Raven | `PGR_Adolf2.ysm` | Adolf 2.0 | NekuroMC | CC BY-SA 4.0 | .ysm, формат 1 |
| Punishing: Gray Raven | `PGR_Lamia.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Punishing: Gray Raven | `PGR_Lucia_1.4.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Punishing: Gray Raven | `PGR_Lucia_1.ysm` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .ysm, старый бинарный v2 |
| Arknights | `Ak_Bitey.zip` | 阿咬 | 映白L (作者) | CC BY-NC-SA 4.0 | .zip (папка, spec 2) |
| Arknights | `Ak_Chen(Tourist-C).zip` | 陈 | 一个普通的创作者 | CC BY-SA 4.0 | .zip (папка, spec 2) |
| Arknights | `Ak_Eyjafjalla(Medic)_v1.0.zip` | Default | 妖梦 (模型) | CC 0 | .zip (папка, spec 2) |
| Arknights | `Ak_Irene.zip` | 艾丽妮 | 桦 (114514) | CC BY-NC-AS4.0 | .zip (папка, spec 2) |
| Arknights | `Ak_Jessica.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Kaltsit_0.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Lappland.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Penance_0.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Penance_1.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Rosmontis_0.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Rosmontis_1.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
| Arknights | `Ak_Typhon.zip` |  | — (в файле не указаны; карточка модели в меню YSM) | — | .zip (формат YSM 1.x) |
## Priestess (Arknights) — своя модель
Авторство: Minecraft_moded (поток 32, Claude) — геометрия, текстура 128×128, осколок Originium, формы предметов,
глаза, плащ, позы, логика. Скелет (имена и иерархия костей) и базовые анимации — встроенная модель YSM «default»
(CC 0; 哥斯拉 — модель, 端木一动不动, 甜粽子, 星屑海螺 — анимации, 蓝玫瑰 — текстура/модель, 艾雷克亚 — анимации Iron's Spells),
поэтому работают все состояния: стоит, ходьба, бег, присед, плавание, полёт и элитры, сон, езда (лошадь, свинья, лодка),
лестницы, удары, использование и удержание предметов по категориям, анимации Iron's Spells и TACZ, смерть.
Персонаж — Arknights (Hypergryph). Ориентир по качеству — Ak_Shu (зашифрован, только как ориентир).

Внешний вид (по 5 референсам владельца): длинные прямые тёмные волосы до груди с тёмно-синими концами, ровная
чёлка-химэ, боковые пряди; чёрный ободок с белым кружевом; серо-фиолетовые глаза с фиолетовыми ромбами-зрачками;
белый свитер в рубчик с высоким воротом, чёрный бант-лента, манжеты-рюши; полупрозрачный белый плащ с чёрной отделкой
на плечах и по краю, асимметричный подол с острыми клиньями (левая пола длиннее); подсумки на поясе (слева маленькие,
справа большая), свисающие ремешки; бейдж на клипсе; чёрная плиссированная юбка с фиолетовой оборкой; ремень с
ромбовой пряжкой; тёмно-фиолетовые колготки; белые туфли с чёрным бантом и ремешком-крестом; над головой — светящийся
красно-белый ромб с глитч-обрывками.

Превью (свой рендер, не скриншот из игры — клиент YSM в облаке не запускается; `mods-src/ysm-priestess/previews.py`):
![виды](img/ysm-priestess/01_views.png)
![состояния](img/ysm-priestess/02_states.png)
![формы осколка](img/ysm-priestess/03_forms.png)
![глаза](img/ysm-priestess/04_eyes.png)
![позы колеса](img/ysm-priestess/05_poses.png)

Механики:
- **Осколок Originium** всегда парит рядом (орбита вокруг игрока, покачивание, вращение), пока руки пусты.
- **Осколок принимает форму предмета** в руке (обе руки): меч, топор, кирка, лопата, мотыга, лук (тетива отходит при натяжении),
  арбалет, щит, копьё/трезубец, посох-жезл, фолиант (книга заклинаний), еда/зелье, «прочее» (гроздь кристаллов).
  Ванильный предмет при этом не рисуется (локатор руки масштаб 0) — **сам предмет работает как обычно**: скрывается только
  отрисовка, удар/выстрел/каст/еда идут по обычной механике игры. Первое лицо — обычный предмет (руки свитера).
  Категории — теги `mm:ysm_form/<форма>` (`pack/kubejs/server_scripts/ysm_priestess_forms.js`: ванильные и forge-теги
  + посохи/жезлы/палочки Iron's Spells, Cataclysm Spellbooks, Wizardry, Mana and Artifice, Ars Nouveau, Botania и др. по имени,
  книги заклинаний, всё съедобное и зелья). Без скрипта (чужой сервер) модель узнаёт оружие и инструменты по ванильным тегам.
- **Глаза-ромбы** (`ysmGlow…` — светятся в темноте и с шейдерами) вспыхивают — крупнее, белая обводка, яркая сердцевина —
  при атаке, ломании блока (`ysm.swinging`), использовании предмета (`q.is_using_item`), сообщении в чат (этап 3), с плавным затуханием.
- **Колесо → «Настройки»**: «Глаза светятся всегда», «Обычные предметы» (показывать ванильный предмет вместо формы осколка),
  «Нимб», «Осколок рядом». Настройки запоминаются (`v.roaming.*`).
- **Позы колеса**: «записи» (планшет с жёлтым стикером и карандаш), «осколок на ладони», «реверанс», «глитч»,
  плюс подменю «Ещё позы (default)» — 8 поз встроенной модели (танцы, жесты).
- **Плащ, волосы, ремешки** — «физика» по скорости, вертикали и повороту (как волосы default).

Сборка: `python3 mods-src/ysm-priestess/build.py` (качает jar YSM по ссылке из `pack/mods/yes-steve-model.pw.toml` в
`mods-src/ysm-priestess/build/`, берёт оттуда скелет/анимации default и пишет модель в `pack/config/.../priestess/`), затем
`packwiz refresh`. Превью без игры: `mods-src/ysm-priestess/ysmrender.py` (свой рендер Bedrock-геометрии с анимациями
и molang; соглашения координат сверены на модели default).

## Глаза при сообщении в чат (этап 3)
`pack/kubejs/server_scripts/ysm_chat_glow.js`: на сообщение игрока сервер выполняет
`/ysm molang execute <игрок> v.chat_glow=1`, через 2,5 с — `v.chat_glow=0` (новое сообщение продлевает). Модель гасит
свечение плавно. Команда YSM работает в сетевой игре и рассылает выполнение клиентам; на других моделях переменная
ни на что не влияет. Видят ли вспышку другие игроки, в облаке проверить нельзя (клиент YSM под ВМ не работает) — пункт чек-листа ниже; если не видно — запасной вариант через клиентский мод mm-perf (согласие владельца есть).

## Что проверить в игре (на ПК, вдвоём)
- [ ] Меню моделей: видны пакеты Arknights / Endfield / PGR и Priestess; модель выбирается, друг её видит.
- [ ] Priestess: осколок кружит рядом; меч/кирка/лук/посох Iron's/палочка Wizardry/книга Ars в руке → форма осколка, ванильного предмета не видно;
      лук стреляет, кирка копает, посох кастует; в первом лице — обычный предмет.
- [ ] Удар/копание/еда → глаза вспыхивают и гаснут; колесо → «Настройки» → «Глаза светятся всегда».
- [ ] Написать в чат → глаза вспыхивают на ~2,5 с, и это видит второй игрок.
- [ ] Позы колеса, «Ещё позы (default)»; плащ не протыкается ногами при беге (если протыкается — сообщить).
- [ ] Шейдеры (Oculus): глаза, нимб, осколок светятся.
