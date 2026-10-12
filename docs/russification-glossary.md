# Глоссарий русификации (единые термины для всех пачек)

Общее: item — предмет, block — блок, mob — моб, spawner — спавнер, dimension — измерение, loot — добыча, tier — уровень,
cooldown — перезарядка, durability — прочность, hunger — голод.
Магия: mana — мана (не «магическая энергия»), spell — заклинание, spellbook — книга заклинаний, scroll — свиток, wand — жезл,
staff — посох, rune — руна, glyph — глиф, ritual — ритуал, familiar — фамильяр, ward — оберег, altar — алтарь, tome — фолиант,
source (Ars Nouveau) — Источник, will (Blood Magic) — воля, essence — эссенция, aura — аура, sigil — сигил, curse — проклятие,
school — школа (магии), summon — призыв, bound — связанный, arcane — тайная магия/чародейский (по контексту), Eldritch — потусторонний,
Fire/Ice/Lightning/Nature/Ender/Holy/Blood Spells — огонь/лёд/молния/природа/эндер/свет/кровь.
Техника: machine — механизм, generator — генератор, conveyor — конвейер, gear — шестерня, cogwheel — зубчатое колесо, shaft — вал,
stress (Create) — нагрузка, rotation speed — скорость вращения, ponder — осмысление (Create), blueprint — чертёж, wrench — ключ,
energy/FE — энергия (FE), tank — бак, pipe — труба, cable — кабель, upgrade — улучшение, circuit — плата/схема.
Космос: oxygen — кислород, space suit — скафандр, rocket — ракета, launch pad — стартовая площадка, planet — планета,
orbit — орбита, gravity — гравитация, fuel — топливо, tier N rocket — ракета N уровня, station — станция, lander — посадочный модуль.
Оружие: ammo — боеприпасы, magazine — магазин, round — патрон, attachment — модификация/обвес, scope — прицел, recoil — отдача,
cannon — пушка, shell — снаряд, barrel — ствол, firing rate — скорострельность.
Имена собственные (Create, Botania, Mekanism и т.п.), названия модов, `Iron's Spells`, `Ars Nouveau` — не переводить.
Arknights: оставлять транслит-имена операторов, Originium — Оригиниум, Operator — оператор, Infected — заражённый, Oripathy — орипатия.


**Названия модов не переводить (решение владельца 2026-10-12).** Название мода (displayName в mods.toml: Create, Botania, Blood Magic, Alex's Caves…)
остаётся английским везде: вкладки творческого режима (`itemGroup.*`), заголовки экранов/книг/достижений-корней, `menu_title`. Допустим перевод хвоста
(«Cataclysm: Предметы»). Предметы, блоки, существа и описания переводим. Для рас/классов/подклассов Arknights (Origins) и навыков Puffish по решению владельца
переводятся только описания, названия — английские (`scripts/lang_keep_en_names.py`). Автоматически: `scripts/lang_keep_en_modnames.py`; проверка — `lang_validate.py lang`.

**Originium → «ориджиниум»** (решение владельца 2026-10-12): все падежи и производные — ориджиниум, ориджиниумный, ориджиниума…; не «оригиниум»/«оригинум»/«ориджинум».
Автоисправление: `scripts/lang_term_fix.py` (запускать после lang_merge.py); `lang_validate.py` предупреждает о старых написаниях.
