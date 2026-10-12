"""Описания боссов, которых код не отдаёт (русские названия, где встретить, как призвать, фазы, экипировка).
Числа здоровья/урона/брони сюда НЕ пишем — их берёт scripts/boss_table.py из сборки. Поля: ru, en, where, summon,
phases, reward (если пусто — берётся из лут-таблицы/JSON), gear, size (если пусто — по хитбоксу), notes, verified
("нет" по умолчанию; "да (дата, кто)" — только после прохождения в игре).
Текст ниже — по документации/структуре модов, в игре не проверялся. Незаполненное поле = «не выяснено», а не «нет».
"""

D = "ванильный дроп"

INFO = {
    # --- Ваниль ---
    "minecraft:ender_dragon": {"ru": "Дракон Края", "where": "Край (The End)", "summon": "заходит в Край; повторно — 4 кристалла Края вокруг портала",
        "phases": "кристаллы лечат; пикирование, дыхание; после смерти — яйцо", "reward": "яйцо дракона, опыт", "gear": "алмаз+/незерит, лук, зелья падения",
        "notes": "У Sarkaz's Furnace и Bumblezone есть теги, где он числится «тёмным боссом»."},
    "minecraft:wither": {"ru": "Иссушитель", "where": "любое измерение (бой лучше в пещере/Незере)", "summon": "4 блока песка душ/почвы душ «Т» + 3 черепа скелета-иссушителя",
        "phases": "после 50% здоровья — броня и щит от стрел", "reward": "звезда Нижнего мира", "gear": "незерит, зачарованный меч, молоко/золотые яблоки"},
    "minecraft:warden": {"ru": "Хранитель", "where": "Древний город (Тёмные глубины Overworld)", "summon": "не призывается; пробуждается шумом/сенсорами",
        "phases": "звуковая волна сквозь стены", "gear": "незерит, обувь с Тихой поступью (Swift Sneak)", "notes": "Нет боссбара, но по силе — босс."},
    # --- Twilight Forest ---
    "twilightforest:naga": {"ru": "Нага", "where": "Измерение Twilight Forest, Двор Наги", "summon": "найти двор в Лесу сумерек",
        "gear": "железо/алмаз", "notes": "Первый босс линейки TF."},
    "twilightforest:lich": {"ru": "Лич", "where": "Twilight Forest, Башня Лича", "summon": "подняться в башню", "phases": "клоны, телепорт, щит"},
    "twilightforest:minoshroom": {"ru": "Миношрум", "where": "Twilight Forest, Лабиринт", "summon": "дойти до арены лабиринта"},
    "twilightforest:hydra": {"ru": "Гидра", "where": "Twilight Forest, Логово гидры (Огненное болото)", "phases": "три головы отращиваются заново", "size": "огромный"},
    "twilightforest:knight_phantom": {"ru": "Призрачный рыцарь", "where": "Twilight Forest, Крепость рыцарей (Тёмный лес)", "phases": "группа из нескольких рыцарей"},
    "twilightforest:ur_ghast": {"ru": "Ур-Гаст", "where": "Twilight Forest, Тёмная башня (Тёмный лес)", "size": "огромный"},
    "twilightforest:snow_queen": {"ru": "Снежная королева", "where": "Twilight Forest, Аврора-дворец (Ледник)", "phases": "ледяные кристаллы"},
    "twilightforest:alpha_yeti": {"ru": "Альфа-йети", "where": "Twilight Forest, Логово йети (Снежный лес)"},
    # --- Aether ---
    "aether:slider": {"ru": "Слайдер", "where": "Эфир, Бронзовое подземелье"},
    "aether:valkyrie_queen": {"ru": "Королева валькирий", "where": "Эфир, Серебряное подземелье"},
    "aether:sun_spirit": {"ru": "Дух Солнца", "where": "Эфир, Золотое подземелье", "phases": "огненные и ледяные атаки; ночь/день"},
    "ancient_aether:mutated_aechor_plant": {"ru": "Мутировавший эхор", "where": "Ancient Aether"},
    "deep_aether:eots_controller": {"ru": "Глаз бури (Eye of the Storm)", "where": "Deep Aether", "notes": "Состоит из контроллера и сегментов (eots_segment)."},
    "lost_aether_content:aerwhale_king": {"ru": "Король эрвалей", "where": "Эфир (Lost Aether Content)", "size": "огромный"},
    # --- Cataclysm (структура арены — по именам structure в jar; призыв — предмет Eye of … / алтарь, точное соответствие смотреть в JEI) ---
    "cataclysm:ignis": {"ru": "Игнис", "where": "Незер, структура burning_arena", "summon": "арена с Altar of Fire + Eye", "phases": "две фазы", "gear": "огнестойкость, незерит+"},
    "cataclysm:netherite_monstrosity": {"ru": "Незеритовое чудовище", "where": "Незер, структура soul_black_smith", "summon": "арена + Eye of Monstrous"},
    "cataclysm:ender_guardian": {"ru": "Страж Края", "where": "Край, структура ruined_citadel", "summon": "Altar of Void + предмет Eye of …"},
    "cataclysm:the_harbinger": {"ru": "Предвестник", "where": "Мир, структура ancient_factory", "summon": "Eye of Mech"},
    "cataclysm:the_leviathan": {"ru": "Левиафан", "where": "Океан, структура sunken_city", "summon": "Altar of Abyss + Eye of Abyss", "size": "огромный"},
    "cataclysm:ancient_remnant": {"ru": "Древний реликт", "where": "Пустыня, структура cursed_pyramid", "summon": "Eye of Desert", "size": "огромный"},
    "cataclysm:maledictus": {"ru": "Малединктус", "where": "Мир, структура frosted_prison", "summon": "Eye of Curse"},
    "cataclysm:scylla": {"ru": "Сцилла", "where": "Океан, структура acropolis", "summon": "Eye of Storm"},
    # --- Mowzie's ---
    "mowziesmobs:frostmaw": {"ru": "Фростмо", "en": "Frostmaw", "where": "снежные биомы (Frostmaw)", "size": "огромный"},
    "mowziesmobs:umvuthi": {"ru": "Умвути, Птица солнца", "where": "Деревня племени Умвутана", "summon": "поселение племени (бой стартует у трона)"},
    "mowziesmobs:sculptor": {"ru": "Тонби, Скульптор", "where": "структура-испытание Скульптора", "summon": "по найденной структуре"},
    # --- Alex's Caves / Mobs ---
    "alexscaves:luxtructosaurus": {"ru": "Люкструктозавр", "where": "Alex’s Caves", "size": "огромный"},
    "alexscaves:tremorzilla": {"ru": "Тремороцилла", "where": "Alex’s Caves (яйцо Tremorzilla)", "size": "огромный", "notes": "Не босс по боссбару — огромный моб."},
    "alexscaves:atlatitan": {"ru": "Атлатитан", "where": "Alex’s Caves", "size": "огромный"},
    "alexscaves:hullbreaker": {"ru": "Хуллбрейкер", "where": "Abyssal Chasm (Alex’s Caves)", "summon": "Abyssal Altar"},
    "alexsmobs:void_worm": {"ru": "Червь Пустоты", "where": "Alex’s Mobs, призыв предметом (Void Worm Eye)"},
    # --- Мана / магия ---
    "irons_spellbooks:dead_king": {"ru": "Король мертвецов", "where": "структура Iron’s Spellbooks (Archevoker/Dead King Dungeon)", "summon": "найти склеп-арену"},
    "irons_spellbooks:fire_boss": {"ru": "Огненный босс", "where": "структура в мире Iron’s Spellbooks"},
    "botania:doppleganger": {"ru": "Страж Гайи (Gaia Guardian)", "where": "Любой мир, на площадке «Gaia Guardian»", "summon": "ритуал Botania (Gaia Guardian)"},
    "extrabotany:gaia_3rd": {"ru": "Гайя 3-я", "where": "ExtraBotany", "summon": "ритуал ExtraBotany"},
    "ars_nouveau:wilden_boss": {"ru": "Дикий химера-босс", "en": "Wilden Chimera", "where": "Ars Nouveau, лагеря Дикарей"},
    "mna:demon_lord": {"ru": "Повелитель демонов", "where": "Mana and Artifice"},
    "mna:odin": {"ru": "Один", "where": "Mana and Artifice"},
    "mna:faerie_queen": {"ru": "Королева фей", "where": "Mana and Artifice"},
    "mna:wither_lich": {"ru": "Лич-иссушитель", "where": "Mana and Artifice"},
    "mna:council_warden": {"ru": "Страж Совета", "where": "Mana and Artifice"},
    "mna:pumpkin_king": {"ru": "Король тыкв", "where": "Mana and Artifice"},
    "arsmagicalegacy:nature_guardian": {"ru": "Страж природы", "where": "Ars Magica: Legacy"},
    "arsmagicalegacy:ender_guardian": {"ru": "Страж Края", "where": "Ars Magica: Legacy"},
    # --- Lycanites ---
    "lycanitesmobs:rahovart": {"ru": "Раховарт", "where": "Незер", "phases": "много призывов и нескольких видов атак", "size": "огромный"},
    "lycanitesmobs:asmodeus": {"ru": "Асмодей", "where": "по JSON creature — см. колонку", "size": "огромный"},
    "lycanitesmobs:amalgalich": {"ru": "Амальгалич", "size": "огромный"},
    # --- Blue Skies / Undergarden / Deeper Dark ---
    "blue_skies:summoner": {"ru": "Призыватель", "where": "Blue Skies"},
    "blue_skies:alchemist": {"ru": "Алхимик", "where": "Blue Skies"},
    "blue_skies:starlit_crusher": {"ru": "Звёздный крушитель", "where": "Blue Skies"},
    "blue_skies:arachnarch": {"ru": "Арахнарх", "where": "Blue Skies"},
    "undergarden:forgotten_guardian": {"ru": "Забытый страж", "where": "Андергарден (структура Forgotten Vault)"},
    "deeperdarker:stalker": {"ru": "Сталкер", "where": "Deeper and Darker (Бездна)"},
    "the_bumblezone:bee_queen": {"ru": "Королева пчёл", "where": "Bumblezone (Королевский сот)", "notes": "Мирный NPC с большим запасом здоровья."},
    "the_bumblezone:cosmic_crystal_entity": {"ru": "Космический кристалл", "where": "Bumblezone (Космическая арена)"},
    # --- Ice and Fire (поток 9). Драконы растут по стадиям; stats — замер взрослого (стадия 5) scripts/tests/iafcheck.js ---
    "iceandfire:fire_dragon": {"ru": "Огненный дракон", "where": "Логова драконов в мире (горы/пустоши)", "summon": "яйцо дракона из логова; взрослые встречаются в небе у логова",
        "phases": "дыхание огнём; пикирование; приручаемый — летающий скакун", "stats": (500, 18, 21), "size": "огромный", "kind": "босс",
        "gear": "алмаз/незерит, дальний бой, защита от огня",
        "notes": "Здоровье/урон/броня — взрослый (стадия 5, AgeTicks 3 000 000), у стадии 1 — 20 HP. «Dragon Health»/«Dragon Attack Damage» в config/iceandfire-common.toml."},
    "iceandfire:ice_dragon": {"ru": "Ледяной дракон", "where": "Логова драконов в заснеженных районах", "summon": "яйцо из логова",
        "phases": "ледяное дыхание, замораживание", "stats": (500, 18, 21), "size": "огромный", "kind": "босс", "gear": "огнестойкость не нужна; тёплая броня, огонь по дракону",
        "notes": "Замер как у огненного: стадия 5."},
    "iceandfire:lightning_dragon": {"ru": "Молниевый дракон", "where": "Логова драконов (Lightning)", "summon": "яйцо из логова",
        "phases": "дыхание молнией, цепной урон", "stats": (500, 18, 21), "size": "огромный", "kind": "босс", "gear": "громоотвод/диэлектрическая броня",
        "notes": "Замер как у огненного: стадия 5."},
    "iceandfire:hydra": {"ru": "Гидра (Ice and Fire)", "where": "Болота, логово гидры", "phases": "много голов, отравляющее дыхание", "size": "большой", "kind": "босс"},
    "iceandfire:cyclops": {"ru": "Циклоп", "where": "Пещеры циклопа / бродит по миру", "size": "огромный", "kind": "босс"},
    "iceandfire:sea_serpent": {"ru": "Морской змей", "where": "Океан", "size": "огромный", "kind": "босс"},
    "iceandfire:myrmex_queen": {"ru": "Королева мирмексов", "where": "Улей мирмексов (джунгли/пустыня)", "kind": "босс"},
    "iceandfire:gorgon": {"ru": "Горгона", "where": "Храм горгоны", "phases": "взгляд превращает в камень — нужно зеркало/слепота", "kind": "босс"},
    "iceandfire:troll": {"ru": "Тролль", "where": "горы, лес"},
    # --- Прочее (Arknights и свои) ---
    "gcrevival:skeleton_boss": {"ru": "Скелет-босс (Galacticraft Revival)", "where": "Лунный данж, Луна (Ad Astra)", "notes": "Из нашего порта Galacticraft Revival."},
    "botania:pink_wither": {"ru": "Розовый Иссушитель", "where": "Botania (Pink Wither)"},
    "darkermagic:summoned_warden": {"ru": "Призванный хранитель", "notes": "Призыв заклинанием, не отдельный босс."},
}

# Не боссы: призывы заклинаний, катсцены, сегменты — исключаем из таблицы.
EXCLUDE = {
    "cataclysm_spellbooks:summoned_clawdian", "cataclysm_spellbooks:summoned_amethyst_crab", "cataclysm_spellbooks:summoned_koboldiator",
    "cataclysm_spellbooks:summoned_aptrgangr", "cataclysm_spellbooks:summoned_coralssus", "cataclysm_spellbooks:summoned_prowler",
    "darkermagic:summoned_warden", "irons_spellbooks:dead_king_corpse", "deep_aether:eots_segment", "gtbcs_geomancy_plus:seismic_ride",
    "alexsmobs:grizzly_bear", "occultism:marid", "occultism:afrit", "occultism:possessed_warden", "occultism:possessed_elder_guardian",
    "cinematiccataclysm:ancient_remnant_cutscene", "cinematiccataclysm:ignis_cutscene", "cinematiccataclysm:maledictus_cutscene",
    "cinematiccataclysm:leviathan_cutscene", "cinematiccataclysm:scylla_cutscene", "cinematiccataclysm:harbinger_cutscene",
    "cinematiccataclysm:netherite_monstrosity_cutscene", "cinematiccataclysm:ender_guardian_cutscene",
    "minecraft:ravager", "minecraft:piglin_brute", "superbwarfare:steel_coil",
    "mna:tfpd",  # лошадка-питомец «Pig Dragon»?
}
SIZE_NOTE = ""
