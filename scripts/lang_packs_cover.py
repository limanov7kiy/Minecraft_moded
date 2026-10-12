#!/usr/bin/env python3
"""Сколько непереведённых строк сборки закрывает каждый русификатор (ресурспак/zip).
Использование: lang_packs_cover.py <папка с jar модов> <pack.zip> [<pack.zip> ...] [--json out.json]
Для каждого ns берёт en_us из jar, ru_ru из jar и из пака; считает ключи en, которых нет в ru мода, но есть в паке
(и пак не просто копия en). Выводит по каждому паку: pack_format, закрыто ключей, затронутые ns."""
import json, pathlib, sys, zipfile, collections
sys.path.insert(0, str(pathlib.Path(__file__).parent))
from lang_audit import LANG_RE, load_json, load_lang, has_letters  # noqa: E402


def read_langs(z):
    out = collections.defaultdict(dict)
    for n in z.namelist():
        m = LANG_RE.match(n)
        if m:
            raw = z.read(n)
            d = load_json(raw) if m.group(3).lower() == "json" else load_lang(raw)
            if d:
                out[(m.group(1), m.group(2).lower())].update(d)
    return out


def main():
    args = sys.argv[1:]
    js = None
    if "--json" in args:
        i = args.index("--json"); js = args[i + 1]; del args[i:i + 2]
    jars = sorted(pathlib.Path(args[0]).glob("*.jar"))
    en, ru = collections.defaultdict(dict), collections.defaultdict(dict)
    for j in jars:
        try:
            with zipfile.ZipFile(j) as z:
                for (ns, code), d in read_langs(z).items():
                    if code == "en_us": en[ns].update(d)
                    elif code == "ru_ru": ru[ns].update(d)
        except zipfile.BadZipFile:
            pass
    need = {ns: {k for k, v in d.items() if k not in ru[ns] or (ru[ns][k] == v and has_letters(v) and len(v) > 3)}
            for ns, d in en.items()}
    total = sum(len(v) for v in need.values())
    res = []
    for p in args[1:]:
        try:
            z = zipfile.ZipFile(p)
        except zipfile.BadZipFile:
            continue
        fmt = None
        if "pack.mcmeta" in z.namelist():
            try: fmt = json.loads(z.read("pack.mcmeta").decode("utf-8-sig"))["pack"]["pack_format"]
            except Exception: pass
        langs = read_langs(z)
        cov = collections.Counter()
        for (ns, code), d in langs.items():
            if code != "ru_ru" or ns not in need:
                continue
            cov[ns] = sum(1 for k in need[ns] if k in d and d[k] != en[ns].get(k))
        res.append({"pack": pathlib.Path(p).name, "pack_format": fmt, "covers": sum(cov.values()),
                    "ns": len(cov), "top": cov.most_common(12), "all": dict(cov)})
    res.sort(key=lambda r: -r["covers"])
    print(f"Нужно перевести (ns с en_us): {total} ключей, {len(need)} ns")
    for r in res:
        print(f"{r['pack']:55} format={r['pack_format']} закрывает {r['covers']:6} ключей в {r['ns']} ns; топ: {r['top'][:6]}")
    if js:
        pathlib.Path(js).write_text(json.dumps({"total": total, "need": {k: len(v) for k, v in need.items() if v}, "packs": res},
                                               ensure_ascii=False, indent=1))


if __name__ == "__main__":
    main()
