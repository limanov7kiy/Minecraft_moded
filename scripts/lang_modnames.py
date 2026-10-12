"""Общее для lang_keep_en_modnames.py и lang_validate.py: названия модов (displayName из mods.toml) и правила,
какие lang-ключи считаются «названием мода» (их не переводим — решение владельца 2026-10-12)."""
import io, re, tomllib, zipfile

TOMLS = ("META-INF/mods.toml", "META-INF/neoforge.mods.toml")
SKIP = re.compile(r"^(item|block|entity|effect|enchantment|fluid|biome|dimension|potion|attribute|advancement|death|subtitles|painting|trim|tag|container|"
                  r"gui|message|tooltip|command|key|stat)\.")
TAB = re.compile(r"^(itemgroup|item_group|creativetab|creative_tab)[._]", re.I)          # вкладки творческого режима
NAMEKEY = re.compile(r"(itemgroup|item_group|creativetab|creative_tab|menu_title|modname|mod_name|\.root\.title|\.title$|^mod\.|^category\.|config)", re.I)


def norm(s):
    return re.sub(r"§.", "", s).strip()


def collect_names(z, names, depth=0):
    for t in TOMLS:
        if t in z.namelist():
            try:
                d = tomllib.loads(z.read(t).decode("utf-8", "replace"))
            except Exception:
                continue
            for m in d.get("mods", []):
                n = m.get("displayName")
                if isinstance(n, str) and len(n) >= 4 and "${" not in n:
                    names[n.casefold()] = n
    if depth < 2:
        for n in z.namelist():
            if n.startswith("META-INF/jarjar/") and n.endswith(".jar"):
                try:
                    collect_names(zipfile.ZipFile(io.BytesIO(z.read(n))), names, depth + 1)
                except zipfile.BadZipFile:
                    pass


def classify(key, en_value, names):
    """None — не название мода; 'exact' — значение = названию мода; 'tab' — вкладка с названием мода внутри."""
    if not isinstance(en_value, str) or SKIP.match(key):
        return None
    cf = norm(en_value).casefold()
    if cf in names and NAMEKEY.search(key):
        return "exact"
    if TAB.search(key) and any(n in cf for n in names):
        return "tab"
    return None
