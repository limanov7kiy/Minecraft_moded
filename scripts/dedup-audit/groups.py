import re, json, collections, os
exec(open(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'analyze.py')).read().split('if __name__')[0])

# функциональные группы: (ключ, название, regex по id-пути и англ. имени, regex исключений)
G = [
    ('rad', 'Защита от радиации', r'hazmat|anti_radiation|radiation_(suit|armor|helmet|chest|legg|boots)', r'tape|sign'),
    ('breath', 'Дыхание под водой / противогаз', r'gas_mask|respirator|scuba|rebreather|diving_(helmet|mask|suit|chest|legg|boots)|oxygen_mask|breathing', r''),
    ('backpack', 'Рюкзаки', r'backpack|rucksack|satchel', r'upgrade|tank|_sign'),
    ('sleep', 'Спальники', r'sleeping_bag|bedroll', r''),
    ('jetpack', 'Реактивные ранцы', r'jetpack|jet_suit|jetsuit', r'upgrade|thruster$'),
    ('glider', 'Планеры', r'glider', r'upgrade|reinforce'),
    ('magnet', 'Магниты', r'(^|_)magnet', r'magnetite|magnetic|upgrade|ingot|nugget|block|dust|plate|wire|raw|ore'),
    ('wrench', 'Гаечные ключи / конфигураторы', r'wrench|configurator|screwdriver', r'upgrade'),
    ('waystone', 'Телепорты / путевые камни', r'waystone|warp_(stone|scroll|plate|pad)|teleporter|teleport_(pad|stone|scroll)|return_scroll|recall', r'_sign|portal_frame'),
    ('compass', 'Компасы / трекеры', r'compass|tracker|locator', r'_sign|banner'),
    ('spyglass', 'Бинокли / подзорные трубы', r'binocular|spyglass|telescope', r''),
    ('nightvis', 'Ночное зрение', r'night_vision|nightvision', r''),
    ('grapple', 'Крюки-кошки', r'grappl|grapple', r''),
    ('parachute', 'Парашюты', r'parachute', r''),
    ('elevator', 'Лифты', r'elevator', r'_sign'),
    ('trash', 'Мусорки', r'trash_can|trashcan|(^|_)trash$|void_(chest|tank)', r''),
    ('heal', 'Аптечки / бинты', r'bandage|medkit|med_kit|first_aid|stimpack|syringe|healing_(salve|kit)', r'empty'),
    ('chunkload', 'Загрузчики чанков', r'chunk_?loader|world_anchor|chunk_anchor', r''),
    ('xp', 'Хранилища опыта', r'xp_(tank|bottle|book|storage|crystal|drain)|experience_(tank|bottle|obelisk|storage)|exp_(tank|book)', r''),
    ('solar', 'Солнечные панели / генераторы', r'solar_(panel|generator|cell)|photovoltaic', r'upgrade'),
    ('gen', 'Генераторы на топливе', r'(combustion|stirling|heat|furnace|coal|fuel|gas_burning|bio|diesel|magmatic)_generator|generator$', r'solar|upgrade|wind'),
    ('battery', 'Батареи / накопители энергии', r'battery|energy_cell|energy_cube|accumulator|capacitor_bank|power_cell', r'upgrade|empty|acid'),
    ('cable', 'Энергокабели', r'(energy|power|universal|fluxduct|copper|gold|electrum)_?(cable|conduit|wire)|^cable|cable$|wire_coil', r'redstone|dye|insulat|spool|coil_block'),
    ('pipe', 'Жидкостные трубы', r'(fluid|liquid|mechanical)_pipe|pipe$|fluid_conduit|pressurized_tube', r'smoke|pipe_wrench|bubble'),
    ('tank', 'Баки для жидкостей', r'fluid_tank|(^|_)tank$|portable_tank', r'oxygen|tank_tracks|tanker|armor|xp|experience|fuel_tank|gas|upgrade'),
    ('pump', 'Насосы', r'(^|_)pump$|mechanical_pump|electric_pump', r''),
    ('crusher', 'Дробилки', r'crusher|pulverizer|grinder|macerator|crushing_wheel|millstone', r'upgrade|hammer'),
    ('alloy', 'Плавильни сплавов', r'alloy_(smelter|furnace|kiln)|arc_furnace|blast_furnace$', r'upgrade|^minecraft'),
    ('sawmill', 'Пилорамы', r'sawmill|saw_mill|mechanical_saw|precision_sawmill', r''),
    ('efurnace', 'Электропечи', r'(electric|powered|energized|induction)_(furnace|smelter|smelting)', r'upgrade'),
    ('centrifuge', 'Центрифуги', r'centrifuge', r'upgrade'),
    ('quarry', 'Автошахтёры / карьеры', r'quarry|digital_miner|miner$|mining_(drill|rig|laser)', r'upgrade|helmet|hat|hammer'),
    ('conveyor', 'Конвейеры / предметные трубы', r'conveyor|item_pipe|item_duct|logistical_transporter|transporter$', r'upgrade'),
    ('autocraft', 'Автокрафтеры', r'auto_?crafter|crafting_(machine|station)|automatic_crafting', r''),
    ('paxel', 'Паксели / мультиинструменты', r'paxel|multitool|multi_tool|omnitool', r''),
    ('sprinkler', 'Поливалки / ускорители роста', r'sprinkler|growth_accelerator|fertilizer_(machine|spreader)', r''),
    ('spawner', 'Ловушки мобов / контроль спавна', r'mob_(grinder|crusher|masher)|spawn_(controller|repellent)|mega_torch', r''),
]
EXCL_NS = re.compile(r'^(minecraft)$')
DECOR = re.compile(r'stairs|slab|wall$|_wall_|bricks|tile|pillar|_sign|carpet|banner|pane|trim|pot$|button|pressure_plate|fence|door|_log$|planks')

def mods(lst): return {i.split(':')[0] for i in lst}
groups = {}
for key, title, pat, ex in G:
    rp, rx = re.compile(pat), (re.compile(ex) if ex else None)
    found = []
    for i, it in items.items():
        ns, path = i.split(':', 1)
        hay = path + ' ' + it['name'].lower().replace(' ', '_')
        if not rp.search(hay) or (rx and rx.search(hay)) or DECOR.search(path):
            continue
        found.append(i)
    if len(mods(found)) >= 2:
        groups[key] = (title, found)

# одинаковый путь у разных модов (снаряжение и машины, без декора)
EQUIP = re.compile(r'(helmet|chestplate|leggings|boots|sword|pickaxe|axe|shovel|hoe|paxel|hammer|shield|bow|crossbow|backpack|generator|furnace|crusher|tank|cable|pipe|battery|wrench|magnet|jetpack|glider|compass)$')
by_path = collections.defaultdict(list)
for i in items:
    ns, path = i.split(':', 1)
    if EQUIP.search(path) and not DECOR.search(path):
        by_path[path].append(i)
same = {p: v for p, v in by_path.items() if len(mods(v)) >= 2}

out = {'groups': {k: {'title': t, 'items': [info(i) for i in sorted(v)]} for k, (t, v) in groups.items()},
       'same_path': {p: [info(i) for i in sorted(v)] for p, v in sorted(same.items())}}
json.dump(out, open(D + 'groups.json', 'w'), ensure_ascii=False, indent=1)
for k, g in out['groups'].items():
    print(f"{k:10} {g['title']:40} items={len(g['items']):3} mods={len(mods([x['id'] for x in g['items']]))}")
print('same_path groups:', len(same))
