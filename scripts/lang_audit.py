#!/usr/bin/env python3
"""Аудит русификации: assets/<ns>/lang/ru_ru.json против en_us.json по каждому jar.
Использование: lang_audit.py <папка с jar> [<папка с jar> ...] [--md out.md] [--json out.json]
Считает ключи en/ru, покрытие, «не переведённые» (нет в ru) и «совпадающие с en» (ru == en, вероятно не переведено),
определяет мод без en_us (основной язык другой), Patchouli-книги (по языкам), Modonomicon, квесты внутри jar.
Jar внутри jar (META-INF/jarjar) тоже просматриваются."""
import io, json, pathlib, re, sys, zipfile, collections

LANG_RE = re.compile(r"^assets/([^/]+)/lang/([^/]+)\.(json|lang)$", re.I)
PATCH_RE = re.compile(r"^(assets|data)/([^/]+)/patchouli_books/([^/]+)/([a-z]{2}_[a-z]{2})/.+\.json$", re.I)
MODON_RE = re.compile(r"^data/([^/]+)/modonomicon/books/([^/]+)/")
GUIDE_RE = re.compile(r"^assets/([^/]+)/(guideme|guides|ponder|lexicon|lexica|manual)/", re.I)
QUEST_RE = re.compile(r"(ftbquests|heracles|questbook|quests)/", re.I)


def load_json(data: bytes):
    t = data.decode("utf-8-sig", "replace")
    t = re.sub(r"^\s*//.*$", "", t, flags=re.M)          # // комментарии
    t = re.sub(r",(\s*[}\]])", r"\1", t)                  # хвостовые запятые
    try:
        d = json.loads(t, strict=False)
    except Exception:
        try:
            d = json.loads(re.sub(r"/\*.*?\*/", "", t, flags=re.S), strict=False)
        except Exception:
            return None
    return {k: v for k, v in d.items() if isinstance(v, str)} if isinstance(d, dict) else None


def load_lang(data: bytes):
    out = {}
    for line in data.decode("utf-8", "replace").splitlines():
        if "=" in line and not line.lstrip().startswith("#"):
            k, v = line.split("=", 1)
            out[k.strip()] = v.strip()
    return out


def has_letters(s: str) -> bool:
    return re.search(r"[A-Za-zА-Яа-я一-鿿]", re.sub(r"%\w|\$\{?\w+\}?|§.", "", s)) is not None


def scan_zip(z, name, res, depth=0):
    langs = collections.defaultdict(dict)   # (ns, code) -> {key: val}
    bad = []
    for n in z.namelist():
        m = LANG_RE.match(n)
        if m:
            ns, code, ext = m.group(1), m.group(2).lower(), m.group(3).lower()
            raw = z.read(n)
            d = load_json(raw) if ext == "json" else load_lang(raw)
            if d is None:
                bad.append(n); continue
            langs[(ns, code)].update(d)
            continue
        m = PATCH_RE.match(n)
        if m:
            res["patchouli"][(m.group(3), m.group(4).lower())] += 1
            res["patchouli_chars"][(m.group(3), m.group(4).lower())] += z.getinfo(n).file_size
            continue
        if MODON_RE.match(n):
            res["modonomicon"] += 1
        elif GUIDE_RE.match(n):
            res["guides"].add(GUIDE_RE.match(n).group(2))
        elif QUEST_RE.search(n) and n.endswith((".snbt", ".json")):
            res["quests"] += 1
        elif n.endswith(".jar") and n.startswith("META-INF/jarjar/") and depth < 2:
            try:
                scan_zip(zipfile.ZipFile(io.BytesIO(z.read(n))), n, res, depth + 1)
            except zipfile.BadZipFile:
                pass
    for (ns, code), d in langs.items():
        res["langs"][ns][code] = d
    res["bad"] += bad


def audit_jar(path):
    res = {"langs": collections.defaultdict(dict), "patchouli": collections.Counter(),
           "patchouli_chars": collections.Counter(), "modonomicon": 0, "guides": set(), "quests": 0, "bad": []}
    try:
        with zipfile.ZipFile(path) as z:
            scan_zip(z, path.name, res)
    except zipfile.BadZipFile:
        return None
    row = {"jar": path.name, "en": 0, "ru": 0, "ru_in_en": 0, "same": 0, "missing": 0, "namespaces": [],
           "other_langs": [], "main": "", "bad_files": res["bad"]}
    other = collections.Counter()
    for ns, by in res["langs"].items():
        en = by.get("en_us"); ru = by.get("ru_ru")
        if en is None:
            # основной язык не английский (или только en_gb и т.п.)
            for code, d in by.items():
                other[code] += len(d)
            if ru:
                row["ru"] += len(ru)
            continue
        row["namespaces"].append(ns)
        row["en"] += len(en)
        if ru:
            row["ru"] += len(ru)
            inn = [k for k in en if k in ru]
            row["ru_in_en"] += len(inn)
            row["same"] += sum(1 for k in inn if ru[k] == en[k] and has_letters(en[k]) and len(en[k]) > 3)
            row["missing"] += len(en) - len(inn)
        else:
            row["missing"] += len(en)
        for code in by:
            if code not in ("en_us", "ru_ru"):
                other[code] += 0
    row["other_langs"] = sorted({c for ns in res["langs"].values() for c in ns} - {"en_us", "ru_ru"})
    if row["en"] == 0 and other:
        row["main"] = max(other, key=other.get)
    row["unt"] = row["missing"] + row["same"]
    row["cover"] = round(100 * (row["en"] - row["unt"]) / row["en"], 1) if row["en"] else None
    pat = {}
    for (book, code), n in res["patchouli"].items():
        pat.setdefault(book, {})[code] = (n, res["patchouli_chars"][(book, code)])
    row["patchouli"] = {b: {"en_files": v.get("en_us", (0, 0))[0], "en_chars": v.get("en_us", (0, 0))[1],
                            "ru_files": v.get("ru_ru", (0, 0))[0]} for b, v in pat.items()}
    row["modonomicon_files"] = res["modonomicon"]
    row["guides"] = sorted(res["guides"])
    row["quests"] = res["quests"]
    return row


def main():
    args = sys.argv[1:]
    md = js = None
    if "--md" in args:
        i = args.index("--md"); md = args[i + 1]; del args[i:i + 2]
    if "--json" in args:
        i = args.index("--json"); js = args[i + 1]; del args[i:i + 2]
    rows = []
    for d in args:
        for p in sorted(pathlib.Path(d).glob("*.jar")):
            r = audit_jar(p)
            if r:
                rows.append(r)
    if js:
        pathlib.Path(js).write_text(json.dumps(rows, ensure_ascii=False, indent=1))
    lines = ["| Мод (jar) | en | ru | покрытие % | не переведено | прим. |", "|---|---:|---:|---:|---:|---|"]
    for r in sorted(rows, key=lambda r: -r["unt"]):
        note = []
        if r["main"]:
            note.append(f"нет en_us, основной: {r['main']}")
        if r["en"] == 0 and not r["main"]:
            note.append("нет lang")
        if r["patchouli"]:
            note.append("patchouli")
        if r["modonomicon_files"]:
            note.append("modonomicon")
        lines.append(f"| {r['jar']} | {r['en']} | {r['ru']} | {r['cover'] if r['cover'] is not None else '—'} | {r['unt']} | {', '.join(note)} |")
    tot_en = sum(r["en"] for r in rows); tot_unt = sum(r["unt"] for r in rows)
    lines.append(f"\nИтого: jar {len(rows)}, en-ключей {tot_en}, не переведено {tot_unt} "
                 f"({round(100 * (tot_en - tot_unt) / tot_en, 1) if tot_en else 0}% покрытие)")
    out = "\n".join(lines)
    if md:
        pathlib.Path(md).write_text(out + "\n")
    else:
        print(out)


if __name__ == "__main__":
    main()
