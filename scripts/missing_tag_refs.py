#!/usr/bin/env python3
"""Теги со ссылками на отсутствующие предметы/биомы → pack/kubejs/server_scripts/missing_mod_refs.js.

Ванилла выбрасывает такой тег целиком («Couldn't load tag X as it is missing following references:»,
дальше по ссылке в строке). Скрипт читает эти ошибки из лога сервера и дописывает ссылки в список
удалений (уже известные сохраняются). Лог нужен от запуска, где ошибки есть: после обновления модов
прогнать scripts/test-server.sh и запустить этот скрипт на logs/latest.log.

Использование: missing_tag_refs.py <latest.log> [папка mods тестового сервера]
С папкой mods реестр тега (item, entity_type, worldgen/biome…) берётся по пути файла тега в jar;
без неё — по догадке (biome → worldgen/biome, иначе item).
"""
import glob, json, pathlib, re, sys, zipfile

OUT = pathlib.Path(__file__).resolve().parent.parent / "pack/kubejs/server_scripts/missing_mod_refs.js"
REF = re.compile(r"(#?[a-z0-9_.-]+:[a-z0-9_./-]+) \(from [^)]*\)")
HEAD = re.compile(r"Couldn't load tag ([a-z0-9_.-]+:[a-z0-9_./-]+) as it is missing following references: (.*)")

MODS = pathlib.Path(sys.argv[2]) if len(sys.argv) > 2 else None
_tagdirs = None

def tagdirs():
    """data/<ns>/tags/<реестр>/<путь>.json во всех jar → {(ns:путь): {реестр}}"""
    global _tagdirs
    if _tagdirs is None:
        _tagdirs = {}
        for j in glob.glob(str(MODS / "*.jar")):
            for n in zipfile.ZipFile(j).namelist():
                m = re.match(r"data/([^/]+)/tags/(.+?)/(.+)\.json$", n)
                if m and m.group(2) in ("item", "items", "block", "blocks", "fluid", "fluids", "entity_type", "entity_types",
                                        "worldgen/biome", "worldgen/structure", "damage_type", "enchantment", "mob_effect"):
                    reg = {"items": "item", "blocks": "block", "fluids": "fluid", "entity_types": "entity_type"}.get(m.group(2), m.group(2))
                    _tagdirs.setdefault(f"{m.group(1)}:{m.group(3)}", set()).add(reg)
    return _tagdirs

def kind(tag, refs):
    if MODS:
        regs = tagdirs().get(tag, set())
        if len(regs) == 1:
            return regs.pop()
    return "worldgen/biome" if any("biome" in x for x in [tag, *refs]) else "item"

known = {}
if OUT.exists():
    m = re.search(r"const REFS = (\{.*?\n\})", OUT.read_text(), re.S)
    if m:
        known = json.loads(m.group(1))

lines = pathlib.Path(sys.argv[1]).read_text(errors="replace").splitlines()
for i, line in enumerate(lines):
    m = HEAD.search(line)
    if not m:
        continue
    tag, refs = m.group(1), REF.findall(m.group(2))
    for nxt in lines[i + 1:]:
        if not nxt.startswith("\t"):
            break
        refs += REF.findall(nxt)
    group = known.setdefault(kind(tag, refs), {})
    group[tag] = sorted(set(group.get(tag, [])) | set(refs))

body = json.dumps({k: dict(sorted(v.items())) for k, v in sorted(known.items())}, indent=2)
OUT.write_text(f"""// Теги модов ссылаются на предметы/биомы модов, которых в сборке нет (или которые мод сам не
// регистрирует, как мороженое Twilight Delight). Ванилла тогда выбрасывает тег ЦЕЛИКОМ
// («Couldn't load tag ... missing following references»): пропадали сладости Farmer's Delight, сахара
// Diet, желе свитов Aether Redux и т.п. Убираем только эти ссылки — тег грузится с остальным.
// Список генерирует scripts/missing_tag_refs.py из лога сервера (не править руками).
// Отключённые рецепты для отсутствующих модов — pack/kubejs/data/*/recipes (условие forge:false).
const REFS = {body}

""" + "".join(f"""
ServerEvents.tags('{t}', event => {{
  const tags = REFS['{t}']
  for (let tag in tags) event.remove(tag, tags[tag])
}})
""" for t in sorted(known)))
print(f"{OUT.name}: {sum(len(v) for g in known.values() for v in g.values())} ссылок в "
      f"{sum(len(g) for g in known.values())} тегах")
