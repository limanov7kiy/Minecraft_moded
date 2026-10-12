#!/usr/bin/env python3
"""Аудит покрытия JEI: у каждого мода — свои типы рецептов (data/*/recipes/*.json с type не из minecraft:)
и есть ли в jar JEI-плагин (класс с @JeiPlugin / ссылка на mezz/jei/api).
Использование: jei_audit.py <папка с jar> [--md out.md] [--extra <папка с jar>]...
Папку получают так: scripts/fetch-mods.py client <папка вне репо> (+ pack/mods/*.jar копируются туда же).
Вложенные jar (META-INF/jarjar) тоже просматриваются."""
import io, json, re, sys, zipfile, tomllib, pathlib, collections

def modids(z):
    try:
        t = tomllib.loads(z.read("META-INF/mods.toml").decode("utf8", "replace"))
        return [m["modId"] for m in t.get("mods", [])]
    except Exception:
        return []

def scan(z, name, res, depth=0):
    ids = modids(z)
    r = res.setdefault(name, dict(ids=ids, types=collections.Counter(), jei=set(), jei_api=False, jei_dep=False,
                                  code_types=False, plugins=[], cats=0))
    if not r["ids"]:
        r["ids"] = ids
    try:
        t = tomllib.loads(z.read("META-INF/mods.toml").decode("utf8", "replace"))
        for deps in t.get("dependencies", {}).values():
            for d in deps:
                if d.get("modId") == "jei":
                    r["jei_dep"] = True
    except Exception:
        pass
    for n in z.namelist():
        if n.endswith(".json") and "/recipes/" in n and n.startswith("data/"):
            try:
                ty = json.loads(z.read(n)).get("type", "")
            except Exception:
                continue
            if isinstance(ty, str) and ty and not ty.startswith("minecraft:") and not ty.startswith("forge:conditional"):
                r["types"][ty] += 1
        elif n.endswith(".class"):
            b = z.read(n)
            if b"mezz/jei/api/JeiPlugin" in b or b"mezz/jei/api/IModPlugin" in b:
                r["jei"].add(n); r["plugins"].append(n)
            elif b"mezz/jei/api" in b:
                r["jei_api"] = True
            if b"mezz/jei/api/recipe/category/IRecipeCategory" in b or b"mezz/jei/api/recipe/category/AbstractRecipeCategory" in b:
                r["cats"] += 1
            if b"RecipeSerializer" in b and b"RecipeType" in b:
                r["code_types"] = True
        elif n.startswith("META-INF/jarjar/") and n.endswith(".jar") and depth < 2:
            try:
                scan(zipfile.ZipFile(io.BytesIO(z.read(n))), name, res, depth + 1)
            except Exception:
                pass

def main():
    args = sys.argv[1:]
    md = None; dirs = []
    while args:
        a = args.pop(0)
        if a == "--md": md = args.pop(0)
        elif a == "--extra": dirs.append(args.pop(0))
        else: dirs.append(a)
    res = {}
    for d in dirs:
        for j in sorted(pathlib.Path(d).glob("*.jar")):
            try:
                scan(zipfile.ZipFile(j), j.name, res)
            except Exception as e:
                print("skip", j.name, e, file=sys.stderr)
    plug_ns = set()  # modId, у которых есть JEI-плагин (он показывает и рецепты аддонов этого типа)
    for n, r in res.items():
        if r["jei"]:
            plug_ns.update(r["ids"])
    SPECIAL = re.compile(r"(special|dye|_book_recipe|conditional)")
    rows = []
    for n, r in sorted(res.items()):
        ids = r["ids"][:1]
        own = [t for t in r["types"] if t.split(":")[0] in r["ids"]]
        foreign = [t for t in r["types"] if t.split(":")[0] not in r["ids"]]
        miss = [t for t in foreign if t.split(":")[0] not in plug_ns]
        own_spec = [t for t in own if SPECIAL.search(t)]
        own_real = [t for t in own if t not in own_spec]
        if not (r["types"] or r["jei"]):
            continue
        if own_real and not r["jei"]:
            st = "ПРОБЕЛ"
        elif own_real and r["jei"] and r["cats"] == 0:
            st = "проверить"
        elif miss:
            st = "чужой тип без плагина"
        elif own_spec and not r["jei"]:
            st = "только special"
        elif r["jei"]:
            st = "OK (плагин)"
        else:
            st = "OK (чужой плагин)"
        rows.append((st, n, ",".join(r["ids"]), own_real, own_spec, foreign, miss, r["cats"], bool(r["jei"])))
    order = {"ПРОБЕЛ": 0, "проверить": 1, "чужой тип без плагина": 2, "только special": 3, "OK (чужой плагин)": 4, "OK (плагин)": 5}
    rows.sort(key=lambda x: (order[x[0]], x[1]))
    out = ["| Статус | Jar | modId | Плагин | Категорий JEI | Свои типы рецептов | Чужие типы (кто показывает) |", "|---|---|---|---|---|---|---|"]
    for st, n, ids, own_real, own_spec, foreign, miss, cats, pj in rows:
        f = ", ".join(sorted({t.split(":")[0] + (" ✗" if t in miss else "") for t in foreign}))
        o = ", ".join(t.split(":")[1] for t in own_real + own_spec)
        out.append(f"| {st} | {n} | {ids} | {'да' if pj else 'нет'} | {cats} | {o} | {f} |")
    text = "\n".join(out)
    print(text)
    cnt = collections.Counter(r[0] for r in rows)
    print("\nИтого:", dict(cnt), file=sys.stderr)
    if md:
        pathlib.Path(md).write_text(text + "\n")

main()
