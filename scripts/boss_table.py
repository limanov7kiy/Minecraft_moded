#!/usr/bin/env python3
"""Таблица боссов сборки → docs/bosses.md и docs/bosses.csv.

Откуда берутся данные (ничего «из памяти» в числа не попадает):
  1. jar сборки (scripts/fetch-mods.py server|client <папка>, плюс pack/mods/*.jar):
     теги forge:bosses / c:bosses / <мод>:bosses, классы с ServerBossEvent/BossEvent, названия из lang;
  2. выгрузка живых существ scripts/tests/bossdump.js (строки BOSSDUMP в logs/latest.log тестового сервера):
     здоровье, урон, броня, размер хитбокса — после всех конфигов и патчей сборки;
  3. scripts/boss_info.py — описания, которые код не отдаёт (русские названия, где встретить, как призвать,
     награда, экипировка, пометка «проверен в игре»). Их пишут руками по docs мода/лоттейблам.

Использование:
  scripts/boss_table.py --mods <папка с jar> --dump <latest.log> [--out docs]
Босс, которого нет в boss_info.py, попадает в таблицу с пометкой «не описан» — это сигнал дописать запись.
Правило проекта: добавил мод с боссами — перезапусти скрипт и закоммить docs/bosses.md/csv в том же PR.
"""
import argparse, csv, glob, json, os, re, sys, zipfile
from collections import defaultdict

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
from boss_info import INFO, EXCLUDE, SIZE_NOTE  # noqa: E402

HP_BIG = 150          # существа с таким здоровьем и выше попадают в кандидаты, даже без боссбара
HP_HUGE_H = 6.0       # высота хитбокса, с которой существо считается «огромным»


def read_jars(mods_dir):
    tags, bossbar_classes, lang = defaultdict(set), set(), {}
    loot, lyc = {}, {}
    jars = sorted(glob.glob(os.path.join(mods_dir, "*.jar")))
    pending = []
    for j in jars:
        try:
            z = zipfile.ZipFile(j)
        except zipfile.BadZipFile:
            continue
        for n in z.namelist():
            m = re.match(r"data/([^/]+)/tags/entity_types/(.+)\.json$", n)
            if m and re.search(r"boss", m.group(2)):
                ns, name = m.group(1), m.group(2)
                try:
                    vals = json.loads(z.read(n).decode("utf-8-sig")).get("values", [])
                except ValueError:
                    continue
                pending.append((f"{ns}:{name}", vals, ns))
            elif re.match(r"data/[^/]+/loot_tables/entit(ies|y)/[^/]+\.json$", n):
                try:
                    items = []
                    walk_loot(json.loads(z.read(n).decode("utf-8-sig")), items)
                    loot[f"{n.split('/')[1]}:{n.rsplit('/', 1)[1][:-5]}"] = items
                except ValueError:
                    pass
            elif re.match(r"common/lycanitesmobs/creatures/[^/]+\.json$", n):
                try:
                    d = json.loads(z.read(n).decode("utf-8-sig"))
                    if d.get("boss") or "boss" in d.get("groups", []):
                        lyc["lycanitesmobs:" + d["name"]] = d
                except ValueError:
                    pass
            elif n.endswith(".class"):
                b = z.read(n)
                if b"ServerBossEvent" in b or b"world/BossEvent" in b:
                    bossbar_classes.add(n[:-6].replace("/", "."))
            elif re.match(r"assets/[^/]+/lang/en_us\.json$", n):
                try:
                    for k, v in json.loads(z.read(n).decode("utf-8-sig")).items():
                        if k.startswith("entity."):
                            lang[k] = v
                except ValueError:
                    pass
    direct = defaultdict(set)
    for tag, vals, ns in pending:
        for v in vals:
            v = v.get("id", "") if isinstance(v, dict) else v
            if v.startswith("#") or ":" not in v:
                continue
            direct[tag].add(v)
    refs = {}
    for tag, vals, ns in pending:
        for v in vals:
            v = v.get("id", "") if isinstance(v, dict) else v
            if v.startswith("#"):
                refs.setdefault(tag, []).append(v[1:])
    for tag, rs in refs.items():          # раскрываем вложенные теги (один уровень достаточно)
        for r in rs:
            direct[tag] |= direct.get(r, set())
    return direct, bossbar_classes, lang, loot, lyc


def walk_loot(node, out):
    if isinstance(node, dict):
        if node.get("type") == "minecraft:item" and "name" in node:
            out.append(node["name"])
        for v in node.values():
            walk_loot(v, out)
    elif isinstance(node, list):
        for v in node:
            walk_loot(v, out)


def read_dump(path):
    rows = {}
    for line in open(path, encoding="utf-8", errors="replace"):
        i = line.find("BOSSDUMP {")
        if i >= 0:
            try:
                r = json.loads(line[i + 9:])
                rows[r["id"]] = r
            except ValueError:
                pass
    return rows


def en_name(eid, lang):
    ns, name = eid.split(":", 1)
    return lang.get(f"entity.{ns}.{name}") or lang.get(f"entity.{ns}.{name.replace('/', '.')}") or name.replace("_", " ").title()


def num(x):
    if x is None:
        return None
    return int(x) if float(x).is_integer() else x


def size_class(r):
    if not r:
        return "не извлечено"
    h, w = r.get("h") or 0, r.get("w") or 0
    if h >= HP_HUGE_H or w >= 4:
        return "огромный"
    if h >= 3 or w >= 2:
        return "большой"
    return "обычный"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--mods", required=True)
    ap.add_argument("--dump", required=True)
    ap.add_argument("--out", default=os.path.join(HERE, "..", "docs"))
    a = ap.parse_args()
    tags, bar_classes, lang, loot, lyc = read_jars(a.mods)
    dump = read_dump(a.dump)
    if not dump:
        sys.exit("в логе нет строк BOSSDUMP — запусти тестовый сервер с scripts/tests/bossdump.js")

    reason = defaultdict(set)
    for tag, ids in tags.items():
        for eid in ids:
            reason[eid].add(f"тег {tag}")
    for eid, r in dump.items():
        if r.get("ns") == "lycanitesmobs" or eid.startswith("lycanitesmobs:"):
            continue          # у Lycanites боссбар в базовом классе у всех; боссы определяются JSON (ниже)
        if r.get("bar") and (r.get("hp") or 0) >= 40:
            reason[eid].add("боссбар в классе")
        if (r.get("hp") or 0) >= HP_BIG:
            reason[eid].add(f"здоровье ≥ {HP_BIG}")
    for eid in lyc:
        reason[eid].add("Lycanites: boss=true в creatures/*.json")
    for eid in INFO:
        reason[eid].add("записан в boss_info")
    ids = [e for e in reason if e not in EXCLUDE and (e in dump or e in INFO)]

    out = []
    for eid in ids:
        r = dump.get(eid)
        info = INFO.get(eid, {})
        hp, dmg, armor = (r or {}).get("hp"), (r or {}).get("dmg"), (r or {}).get("armor")
        L = lyc.get(eid)
        src = "атрибут"
        if "stats" in info:     # существо создаётся «малым» (драконы растут) — берём замер взрослого из тестов, см. notes
            hp, dmg, armor = info["stats"]
        if L:                   # Lycanites задаёт характеристики в JSON при создании, атрибут по умолчанию — 20
            hp, dmg, armor, src = L.get("health"), L.get("damage"), L.get("armor"), "creatures/*.json"
        reward = info.get("reward", "")
        if not reward:
            items = [d["item"] for d in (L or {}).get("drops", []) if "item" in d] or loot.get(eid, [])
            seen = []
            for i in items:
                if i not in seen:
                    seen.append(i)
            reward = ", ".join(seen[:6]) + (" …" if len(seen) > 6 else "") + (" (из лута)" if seen else "")
        where = info.get("where", "")
        if not where and L:
            sp = L.get("spawning", {})
            where = ", ".join(sp.get("dimensionIds", [])) or ""
        h, w = (r or {}).get("h"), (r or {}).get("w")
        if L:
            h, w = L.get("height", h), L.get("width", w)
        rr = {"h": h, "w": w} if h is not None else None
        out.append({
            "id": eid,
            "mod": eid.split(":")[0],
            "ru": info.get("ru", ""),
            "en": info.get("en") or en_name(eid, lang),
            "where": where,
            "hp": num(hp) if hp is not None else "≈/не извлечено",
            "dmg": num(dmg) if dmg is not None else "≈/не извлечено",
            "armor": num(armor) if armor is not None else "≈/не извлечено",
            "phases": info.get("phases", ""),
            "summon": info.get("summon", ""),
            "reward": reward,
            "gear": info.get("gear", ""),
            "size": info.get("size") or size_class(rr),
            "size_hw": f"{h}×{w}" if rr else "",
            "notes": (info.get("notes", "") + (f" Числа из {src}." if src != "атрибут" else "")).strip(),
            "verified": info.get("verified", "нет"),
            "kind": info.get("kind") or ("босс" if any(t.startswith(("тег", "боссбар", "Lycanites")) for t in reason[eid]) else "сильный моб"),
            "signals": "; ".join(sorted(reason[eid])),
            "described": bool(info),
        })
    out.sort(key=lambda x: (x["mod"], -(x["hp"] if isinstance(x["hp"], (int, float)) else -1)))

    os.makedirs(a.out, exist_ok=True)
    cols = ["id", "mod", "ru", "en", "where", "hp", "dmg", "armor", "phases", "summon", "reward", "gear",
            "size", "size_hw", "kind", "notes", "verified", "signals"]
    with open(os.path.join(a.out, "bosses.csv"), "w", encoding="utf-8", newline="") as f:
        w = csv.writer(f)
        w.writerow(cols)
        for x in out:
            w.writerow([x[c] for c in cols])

    def cell(s):
        return str(s).replace("|", "/").replace("\n", " ")

    lines = ["# Таблица боссов сборки", "",
             "Сгенерировано `scripts/boss_table.py` (источники — в его шапке); руками правятся только записи в "
             "`scripts/boss_info.py`. **Добавил мод с боссами — перезапусти скрипт и закоммить `docs/bosses.md` и "
             "`docs/bosses.csv` в том же PR** (правило в `docs/roadmap.md`).", "",
             "Числа (здоровье / урон / броня) — атрибуты существа в запущенной сборке, после конфигов и патчей "
             "(`scripts/tests/bossdump.js`). «≈/не извлечено» — число не удалось получить. Урон — базовый атрибут "
             "атаки; у многих боссов основной урон идёт заклинаниями и особыми атаками, их множители в атрибут "
             "не входят. Размер: огромный — хитбокс от 6 блоков в высоту или 4 в ширину; большой — от 3 / 2.",
             "",
             "Учёт баланса (`docs/compat.md`): броня считается по Apothic (`a/(a+броня)`), защита-зачарование "
             "по ваниле — поздняя игра примерно вдвое тяжелее, чем рассчитывали авторы боссов; Arknights Weapon "
             "в оригинале удваивал здоровье мобов при загрузке чанка — в сборке порт без этого.", ""]
    nd = [x for x in out if not x["described"]]
    lines += [f"Всего в таблице: **{len(out)}**; без записи в `boss_info.py` (нужно описать): **{len(nd)}**.", ""]
    by_mod = defaultdict(list)
    for x in out:
        if x["described"]:
            by_mod[x["mod"]].append(x)
    head = ("| Босс | Где встретить | ❤ HP | Урон | Броня | Размер | Фазы / способности | Как призвать / найти | "
            "Награда | Экипировка | Тип | Заметки | Проверен в игре |")
    sep = "|" + "---|" * 13
    for mod in sorted(by_mod):
        lines += [f"## {mod}", "", head, sep]
        for x in by_mod[mod]:
            name = f"**{x['ru']}** / {x['en']}"
            size = x["size"] + (f" ({x['size_hw']})" if x["size_hw"] else "")
            lines.append("| " + " | ".join(cell(v) for v in [
                name, x["where"] or "—", x["hp"], x["dmg"], x["armor"], size, x["phases"] or "—",
                x["summon"] or "—", x["reward"] or "—", x["gear"] or "—", x["kind"], x["notes"], x["verified"]]) + " |")
        lines.append("")
    if nd:
        lines += ["## Найдены автоматически, ещё не описаны", "",
                  "Только числа из сборки и сигнал, по которому босс найден; описание (где встретить, как призвать) "
                  "дописывается в `scripts/boss_info.py` по мере прохождения. Полные колонки — в `bosses.csv`.", "",
                  "| ID | Тип | HP | Урон | Броня | Размер | Сигнал |", "|---|---|---|---|---|---|---|"]
        for x in nd:
            lines.append("| " + " | ".join(cell(v) for v in [
                f"`{x['id']}` ({x['en']})", x["kind"], x["hp"], x["dmg"], x["armor"],
                x["size"] + (f" ({x['size_hw']})" if x["size_hw"] else ""), x["signals"]]) + " |")
        lines.append("")
    if SIZE_NOTE:
        lines += [SIZE_NOTE, ""]
    with open(os.path.join(a.out, "bosses.md"), "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"боссов: {len(out)}, не описано: {len(nd)}")
    if os.environ.get("VERBOSE"):
        for x in nd:
            print("  не описан:", x["id"], "|", x["signals"], "| hp", x["hp"], "dmg", x["dmg"], x["size_hw"])


if __name__ == "__main__":
    main()
