#!/usr/bin/env python3 -I
"""Генератор справочника docs/encyclopedia/ (этап 1: карточки модов).

Источники: pack/modlist.txt, pack/mods/*.pw.toml, pack/resourcepacks/*.pw.toml, pack/mods/*.jar (mods.toml),
scripts/encyclopedia/meta-cache.json (описания Modrinth; обновляется fetch_meta.py), categories.toml.
Пересоздаёт только блоки между <!-- auto:start ... --> и <!-- auto:end ... -->; всё остальное — ручной текст.
Запуск: python3 -I scripts/encyclopedia/build.py [--check]
  --check: ничего не писать, вернуть код 1, если файлы устарели или есть непокрытые моды.
Правила редактирования — docs/encyclopedia/README-authoring.md."""
import fnmatch
import json
import re
import sys
import tomllib
import zipfile
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
OUT = ROOT / "docs" / "encyclopedia"
CHECK = "--check" in sys.argv

STUBS = ["Как играть", "Предметы/блоки", "Мобы", "Эффекты", "События",
         "Данжи/структуры (/locate)", "Чары", "Команды/гамерулы", "Конфиги"]
STUB_TEXT = "_Пока пусто — заполнит один из следующих этапов._"


def clean(s):
    return " ".join((s or "").split())


def cut(s, n):
    s = clean(s)
    return s if len(s) <= n else s[:n - 1].rstrip() + "…"


# ---------- modlist ----------
def parse_modlist():
    """-> (active {slug: (kind, section, note)}, inactive [(slug, why)], jar_notes [(glob, note)])"""
    active, inactive, jar_notes = {}, [], []
    section = ""
    last_comment = ""
    for raw in (ROOT / "pack" / "modlist.txt").read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if line.startswith("## "):
            section = line[3:].strip()
            continue
        m = re.match(r"^(mr|rp|cf)\s+(\S+)\s*(?:#\s*(.*))?$", line)
        if m:
            active[m.group(2)] = (m.group(1), section, clean(m.group(3)))
            continue
        m = re.match(r"^#\s*(jar|removed|patched|mr|cf|rp)\s+(\S+)\s*(?:#\s*(.*))?$", line)
        if m:
            inactive.append((m.group(2), m.group(1), clean(m.group(3))))
            continue
        for g in re.findall(r"pack/mods/([\w.\-*]+\.jar)", line):
            jar_notes.append((g, clean(line.lstrip("# "))))
    return active, inactive, jar_notes


# ---------- записи ----------
def side_of(t, fallback="both"):
    return t.get("side", fallback)


def jar_key(name):
    stem = name[:-4]
    return re.sub(r"[-_]?\d.*$", "", stem) or stem


def jar_version(fn, declared):
    if declared and "${" not in declared:
        return declared
    m = re.search(r"(\d[\w.+\-]*)\.jar$", fn)
    return m.group(1) if m else "?"


def load_entries(meta):
    entries = {}  # key -> dict
    for d, kind in (("pack/mods", "pw"), ("pack/resourcepacks", "rp")):
        for f in sorted((ROOT / d).glob("*.pw.toml")):
            t = tomllib.loads(f.read_text(encoding="utf-8"))
            key = f.name[:-8]
            upd = t.get("update", {})
            mr = upd.get("modrinth")
            cf = upd.get("curseforge")
            info = meta.get(mr["mod-id"], {}) if mr else {}
            link = None
            if mr:
                typ = "resourcepack" if kind == "rp" else "mod"
                link = ("Modrinth", f"https://modrinth.com/{typ}/{info.get('slug') or mr['mod-id']}")
            elif cf:
                link = ("CurseForge", f"https://www.curseforge.com/projects/{cf['project-id']}")
            entries[key] = dict(
                key=key, name=clean(t["name"]), version=info.get("version") or "?", side=side_of(t),
                link=link, desc=cut(info.get("description"), 300), desc_src="Modrinth",
                kind=kind, file=f.name, mr_slug=info.get("slug"), jar=None,
            )
    for f in sorted((ROOT / "pack" / "mods").glob("*.jar")):
        key = jar_key(f.name)
        name, ver, desc, src = key, jar_version(f.name, None), "", ""
        try:
            with zipfile.ZipFile(f) as z:
                t = tomllib.loads(z.read("META-INF/mods.toml").decode("utf-8"))
            m0 = t["mods"][0]
            name = clean(m0.get("displayName")) or key
            ver = jar_version(f.name, m0.get("version"))
            desc, src = cut(m0.get("description"), 300), "mods.toml"
        except (KeyError, ValueError, zipfile.BadZipFile):
            pass
        entries[key] = dict(
            key=key, name=name, version=ver, side="client" if "-client" in f.name else "both",
            link=None, desc=desc, desc_src=src, kind="jar", file=f.name, mr_slug=None, jar=f.name,
        )
    return entries


# ---------- упоминания в docs ----------
def doc_texts():
    res = {}
    for p in sorted((ROOT / "docs").rglob("*.md")):
        rel = p.relative_to(ROOT / "docs")
        if rel.parts[0] == "encyclopedia":
            continue
        res[str(rel)] = p.read_text(encoding="utf-8", errors="replace")
    return res


def terms_for(e, aliases, skip):
    terms = [(a, True) for a in aliases.get(e["key"], [])]  # (термин, чувствителен к регистру)
    if e["key"] in skip:
        return terms
    name = re.sub(r"^\[[^\]]*\]\s*", "", e["name"])
    cands = {name, re.split(r"\s*(?::| - | \(|'n )\s*", name)[0]}
    if e["mr_slug"]:
        cands.add(e["mr_slug"])
    for c in cands:
        c = c.strip()
        if len(c) >= 8 or (len(c) >= 5 and re.search(r"[\s\-_']", c)):
            terms.append((c, False))
        elif len(c) >= 4:
            terms.append((c, True))
    return terms


def doc_links(e, texts, aliases, skip):
    pats = []
    for t, cs in terms_for(e, aliases, skip):
        pats.append(re.compile(r"(?<![\w])" + re.escape(t) + r"(?![\w])", 0 if cs else re.I))
    hits = []
    for rel, txt in texts.items():
        n = sum(len(p.findall(txt)) for p in pats)
        if n:
            hits.append((n, rel))
    hits.sort(key=lambda x: (-x[0], x[1]))
    return hits[:6]


# ---------- markdown ----------
def block(tag, body):
    return f"<!-- auto:start {tag} -->\n{body}\n<!-- auto:end {tag} -->"


BLOCK_RE = r"<!-- auto:start {tag} -->\n.*?\n<!-- auto:end {tag} -->"


def replace_block(text, tag, body):
    pat = re.compile(BLOCK_RE.format(tag=re.escape(tag)), re.S)
    return pat.sub(lambda m: block(tag, body), text, count=1)


def card_body(e, modlist, texts, aliases, skip):
    ml = modlist.get(e["key"])
    lines = [f'<a id="{e["key"]}"></a>']
    src = []
    if e["link"]:
        src.append(f"[{e['link'][0]}]({e['link'][1]})")
    if e["jar"]:
        src.append(f"jar в репозитории: `pack/mods/{e['jar']}`")
    lines.append(f"- **Версия:** {e['version']} · **Сторона:** {e['side']}"
                 + (" · **Источник:** " + "; ".join(src) if src else ""))
    desc = e["desc"] or (ml[2] if ml and ml[2] else "") or "_Описание не найдено._"
    lines.append(f"- **Что это:** {desc}" + (f" _(источник: {e['desc_src']})_" if e["desc"] and e["desc_src"] else ""))
    note = ml[2] if ml and ml[2] and ml[2] != desc else ""
    if note:
        lines.append(f"- **В сборке:** {cut(note, 400)}")
    elif not ml:
        lines.append("- **В сборке:** подтянут как зависимость или связка (в modlist.txt отдельной строкой не указан).")
    hits = doc_links(e, texts, aliases, skip)
    if hits:
        lines.append("- **В наших docs:** " + ", ".join(f"[{rel}](../{rel}) ({n})" for n, rel in hits))
    return "\n".join(lines)


def new_card(e, body):
    parts = [f"## {e['name']}", "", block(f"card:{e['key']}", body), ""]
    for s in STUBS:
        parts += [f"### {s}", "", STUB_TEXT, ""]
    return "\n".join(parts)


CARD_HEAD = re.compile(r"^## (.+)\n\n<!-- auto:start card:(\S+) -->", re.M)


def gh_anchor(title):
    a = re.sub(r"[^\w\- ]", "", title.lower()).replace(" ", "-")
    return a


def page_cards(text):
    return [(m.group(2), m.group(1)) for m in CARD_HEAD.finditer(text)]


def head_text(cat_id, cat):
    return f"# {cat['title']}\n\n{cat['desc']}\n\n"


def build_page(cat_id, cat, ents, ctx):
    path = OUT / f"{cat_id}.md"
    text = path.read_text(encoding="utf-8") if path.exists() else None
    if text is None:
        text = (head_text(cat_id, cat) + block("toc", "") + "\n\n## Общее по категории\n\n"
                "_Обзор категории — вручную, следующие этапы._\n\n---\n\n<!-- cards:start -->\n<!-- cards:end -->\n")
    existing = {k for k, _ in page_cards(text)}
    for e in sorted(ents, key=lambda x: x["name"].lower()):
        body = card_body(e, ctx["modlist"], ctx["texts"], ctx["aliases"], ctx["skip"])
        if e["key"] in existing:
            text = replace_block(text, f"card:{e['key']}", body)
        elif e["key"] not in ctx["placed_elsewhere"].get(cat_id, set()):
            text = text.replace("<!-- cards:end -->", new_card(e, body) + "\n<!-- cards:end -->", 1)
    cards = page_cards(text)
    toc = [f"Модов в категории: **{len(cards)}**.", ""]
    for key, title in sorted(cards, key=lambda x: x[1].lower()):
        toc.append(f"- [{title}](#{gh_anchor(title)})")
    extra = cat.get("extra", [])
    if extra:
        toc += ["", "**См. также:** " + "; ".join(extra)]
    toc += ["", "Вернуться: [оглавление справочника](INDEX.md)."]
    text = replace_block(text, "toc", "\n".join(toc))
    return path, text


def main():
    meta = json.loads((HERE / "meta-cache.json").read_text(encoding="utf-8"))
    cfg = tomllib.loads((HERE / "categories.toml").read_text(encoding="utf-8"))
    cats, members = cfg["categories"], cfg["members"]
    aliases, skip = cfg.get("doc_aliases", {}), set(cfg.get("doc_skip", {}).get("keys", []))
    active, inactive, jar_notes = parse_modlist()
    entries = load_entries(meta)

    # заметки к jar из комментариев modlist
    for e in entries.values():
        if e["key"] in cfg.get("descriptions", {}):
            e["desc"], e["desc_src"] = cfg["descriptions"][e["key"]], "docs сборки"
        if e["jar"]:
            for g, note in jar_notes:
                if fnmatch.fnmatch(e["jar"], g):
                    e.setdefault("note", note)
    # modlist по ключам карточек
    by_mr = {e["mr_slug"]: k for k, e in entries.items() if e["mr_slug"]}
    modlist = {}
    uncovered = []
    for slug, val in active.items():
        k = slug if slug in entries else by_mr.get(slug)
        if k:
            modlist[k] = val
        else:
            uncovered.append(slug)
    for slug, how, note in inactive:
        if how == "jar":
            norm = re.sub(r"[-_]", "", slug)
            for k, e in entries.items():
                if e["jar"] and re.sub(r"[-_]", "", k) == norm:
                    modlist[k] = ("jar", "", note or e.get("note", ""))
    for k, e in entries.items():
        if e["jar"] and k not in modlist:
            modlist[k] = ("jar", "", e.get("note", ""))

    # категории
    cat_of = {}
    for cid, keys in members.items():
        if cid not in cats:
            sys.exit(f"categories.toml: нет категории {cid}")
        for k in keys:
            if k in cat_of and cat_of[k] != cid:
                sys.exit(f"categories.toml: {k} в двух категориях")
            cat_of[k] = cid
    nocat = sorted(k for k in entries if k not in cat_of)
    unknown_members = sorted(k for k in cat_of if k not in entries)

    # где карточки уже лежат (ручной перенос между страницами не затираем)
    existing_pages = {}
    for cid in cats:
        p = OUT / f"{cid}.md"
        if p.exists():
            for k, _ in page_cards(p.read_text(encoding="utf-8")):
                existing_pages[k] = cid
    placed_elsewhere = {}
    for k, cid in existing_pages.items():
        if k in cat_of and cat_of[k] != cid:
            placed_elsewhere.setdefault(cat_of[k], set()).add(k)

    ctx = dict(modlist=modlist, texts=doc_texts(), aliases=aliases, skip=skip, placed_elsewhere=placed_elsewhere)
    pages = {}
    for cid, cat in cats.items():
        ents = [entries[k] for k in entries if cat_of.get(k) == cid and existing_pages.get(k, cid) == cid]
        path, text = build_page(cid, cat, ents, ctx)
        pages[path] = text

    # INDEX
    index = build_index(cats, pages)
    ipath = OUT / "INDEX.md"
    if ipath.exists() and "<!-- auto:start cats -->" in ipath.read_text(encoding="utf-8"):
        itext = ipath.read_text(encoding="utf-8")
        for tag, body in index.items():
            itext = replace_block(itext, tag, body)
    else:
        itext = ("# Справочник-энциклопедия сборки\n\n"
                 "Справочник по модам сборки **Minecraft 1.20.1 + Forge 47.4.26**: карточка каждого мода "
                 "(версия, сторона, что делает, ссылки) и разделы «Как играть», «Предметы/блоки», «Мобы» и др. "
                 "Карточки сгенерированы `scripts/encyclopedia/build.py`; остальное пишется вручную "
                 "([как редактировать](README-authoring.md)). Статус: **этап 1 — каркас и карточки**; "
                 "разделы «Как играть» и т. п. пока пусты.\n\n## Категории\n\n"
                 + block("cats", index["cats"]) + "\n\n## Статьи\n\n"
                 + block("articles", index["articles"]) + "\n\n## Все моды по алфавиту\n\n"
                 + block("alpha", index["alpha"]) + "\n")
    pages[ipath] = itext

    # отчёт
    total = len(entries)
    covered_ml = len(active) - len(uncovered)
    stale = sorted(k for k in existing_pages if k not in entries)
    changed = [p for p, t in pages.items() if not p.exists() or p.read_text(encoding="utf-8") != t]
    if not CHECK:
        OUT.mkdir(parents=True, exist_ok=True)
        for p, t in pages.items():
            p.write_text(t, encoding="utf-8")
    print(f"Карточек: {total} (pw.toml/ресурспаки: {sum(e['kind'] != 'jar' for e in entries.values())}, "
          f"jar: {sum(e['kind'] == 'jar' for e in entries.values())})")
    print(f"Строк modlist (активных): {len(active)}; с карточкой: {covered_ml}; без: {len(uncovered)}")
    for s in uncovered:
        print("  НЕ ПОКРЫТ (modlist):", s)
    print(f"Отключено/закомментировано в modlist (карточек нет, в сборке отсутствуют): {len([i for i in inactive if i[0] not in entries and i[1] != 'jar'])}")
    not_in_ml = sorted(k for k in entries if k not in modlist)
    print(f"Зависимости/связки вне modlist (карточка есть): {len(not_in_ml)}")
    for cid, cat in cats.items():
        print(f"  {cat['title']}: {sum(1 for k in cat_of if cat_of[k] == cid and k in entries)}")
    bad = False
    for label, lst in (("БЕЗ КАТЕГОРИИ", nocat), ("в categories.toml нет такого мода", unknown_members),
                       ("карточка есть, мода в сборке нет", stale)):
        for k in lst:
            print(f"  {label}: {k}")
            bad = bad or label != "карточка есть, мода в сборке нет"
    bad = bad or bool(uncovered)
    if CHECK:
        for p in changed:
            print("  устарел:", p.relative_to(ROOT))
        bad = bad or bool(changed)
    print("ОК" if not bad else "ЕСТЬ ПРОБЛЕМЫ")
    sys.exit(1 if bad else 0)


def build_index(cats, pages):
    rows = ["| Категория | Модов | О чём |", "|---|---|---|"]
    alpha = []
    for cid, cat in cats.items():
        text = pages[OUT / f"{cid}.md"]
        cards = page_cards(text)
        rows.append(f"| [{cat['title']}]({cid}.md) | {len(cards)} | {cat['desc']} |")
        for key, title in cards:
            alpha.append((title, f"[{title}]({cid}.md#{key}) — {cat['title']}"))
    articles = []
    for cid, cat in cats.items():
        for x in cat.get("extra", []):
            articles.append(f"- {cat['title']}: {x}")
    alpha.sort(key=lambda x: x[0].lower())
    return dict(cats="\n".join(rows), articles="\n".join(articles) or "_Пока нет._",
                alpha="\n".join(f"- {a}" for _, a in alpha))


main()
