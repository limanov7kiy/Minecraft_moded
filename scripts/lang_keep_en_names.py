#!/usr/bin/env python3
"""Решение владельца (2026-10-12): у рас/классов/подклассов Arknights (Origins) и деревьев навыков Puffish переводим только
ОПИСАНИЯ, названия остаются английскими. Скрипт пишет в наш пак (pack/resourcepacks-src/russian-moded) английское значение
для ключей-названий — наш пак стоит выше MPLOC/RTF+, поэтому они тоже не «перетягивают» названия на русский.
Ключи: origin.<ns>.*.name и power.<ns>.*.name из lang модов Arknights (расы, классы, Originium Arts) + «title»-ключи
деревьев навыков Puffish (skill.*, profession.*) из их data-JSON. Использование: lang_keep_en_names.py <папка jar> [<пак>]"""
import json, pathlib, re, sys, zipfile
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from lang_audit import LANG_RE, load_json  # noqa: E402
jars = pathlib.Path(sys.argv[1])
pack = pathlib.Path(sys.argv[2] if len(sys.argv) > 2 else pathlib.Path(__file__).resolve().parent.parent / "pack/resourcepacks-src/russian-moded")
MODS = ("arknights_races", "arknights_skills", "arknights_originiumarts")
total = 0
for prefix in MODS:
    for j in jars.glob(prefix + "*.jar"):
        z = zipfile.ZipFile(j); en = {}; where = {}
        for n in z.namelist():
            m = LANG_RE.match(n)
            if m and m.group(2).lower() == "en_us" and m.group(3) == "json":
                for k, v in (load_json(z.read(n)) or {}).items():
                    en[k] = v; where[k] = m.group(1)
        keys = {k for k in en if re.match(r"(origin|power)\..+\.name$", k)}
        for n in z.namelist():
            if n.startswith("data/") and "/puffish_skills/" in n and n.endswith(".json"):
                s = z.read(n).decode("utf-8", "replace")
                keys |= {k for k in re.findall(r'"title"\s*:\s*\{\s*"translate"\s*:\s*"([^"]+)"', s) if k in en}
        by_ns = {}
        for k in keys:
            by_ns.setdefault(where[k], {})[k] = en[k]
        for ns, d in by_ns.items():
            f = pack / "assets" / ns / "lang" / "ru_ru.json"
            cur = json.loads(f.read_text("utf-8")) if f.exists() else {}
            cur.update(d)
            f.parent.mkdir(parents=True, exist_ok=True)
            f.write_text(json.dumps(dict(sorted(cur.items())), ensure_ascii=False, indent=2) + "\n", "utf-8")
            total += len(d)
        print(f"{j.name}: {sum(len(d) for d in by_ns.values())} названий оставлено английскими")
print("итого", total)
