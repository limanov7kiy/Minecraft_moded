#!/usr/bin/env python3
"""Иероглифы/кана/хангыль ВНЕ lang-файлов: data/** и assets/** JSON, .mcfunction, .nbt (gzip), .snbt, patchouli/другие тексты, и строковые константы в .class.
Использование: cjk_nonlang_audit.py <папка с jar> [--out отчёт.json]. Печатает по модам: где (категория) и сколько строк, примеры."""
import collections, gzip, io, json, pathlib, re, struct, sys, zipfile
CJK = re.compile(r"[぀-ヿ㐀-䶿一-鿿가-힯＀-￯]+")
CJK_STRICT = re.compile(r"[぀-ヿ㐀-䶿一-鿿가-힯]")


def class_strings(b):
    """Строковые константы (CONSTANT_Utf8) из .class — разбор constant pool."""
    out = []
    if b[:4] != b"\xca\xfe\xba\xbe":
        return out
    n = struct.unpack(">H", b[8:10])[0]; i = 10; k = 1
    while k < n:
        t = b[i]
        if t == 1:
            ln = struct.unpack(">H", b[i + 1:i + 3])[0]; out.append(b[i + 3:i + 3 + ln]); i += 3 + ln
        elif t in (3, 4, 9, 10, 11, 12, 17, 18): i += 5
        elif t in (5, 6): i += 9; k += 1
        elif t in (7, 8, 16, 19, 20): i += 3
        elif t == 15: i += 4
        else: break
        k += 1
    res = []
    for s in out:
        try: res.append(s.decode("utf-8"))   # modified UTF-8: для CJK совпадает с обычным
        except UnicodeDecodeError: pass
    return res


def nbt_strings(b):
    try: raw = gzip.decompress(b)
    except Exception: raw = b
    return [m.decode("utf-8", "ignore") for m in re.findall(rb"(?:[\xe3-\xe9\xea-\xed][\x80-\xbf]{2}|[\xef][\xbc-\xbf][\x80-\xbf])+", raw)]


rep = collections.defaultdict(lambda: collections.defaultdict(list))
jars = pathlib.Path(sys.argv[1]).glob("*.jar")
for j in sorted(jars):
    try: z = zipfile.ZipFile(j)
    except zipfile.BadZipFile: continue
    for n in z.namelist():
        if "/lang/" in n or n.endswith("/") : continue
        ext = n.rsplit(".", 1)[-1].lower()
        try:
            if ext in ("json", "mcfunction", "snbt", "txt", "md", "toml", "mcmeta", "properties", "cfg"):
                s = z.read(n).decode("utf-8", "ignore")
                found = CJK.findall(s)
                found = [f for f in found if CJK_STRICT.search(f)]
                if found: rep[j.name][("data/assets " + ext)].append((n, found[:2], len(found)))
            elif ext == "nbt":
                f = [x for x in nbt_strings(z.read(n)) if CJK_STRICT.search(x)]
                if f: rep[j.name]["nbt"].append((n, f[:2], len(f)))
            elif ext == "class":
                f = [x for x in class_strings(z.read(n)) if CJK_STRICT.search(x)]
                if f: rep[j.name]["class"].append((n, f[:3], len(f)))
        except Exception:
            pass
out = {}
for jar, cats in sorted(rep.items()):
    tot = sum(c for v in cats.values() for _, _, c in v)
    print(f"{jar}: {tot} строк; " + ", ".join(f"{c}: {len(v)} файлов/{sum(x for _, _, x in v)}" for c, v in cats.items()))
    out[jar] = {c: [(n, ex, k) for n, ex, k in v] for c, v in cats.items()}
if "--out" in sys.argv:
    pathlib.Path(sys.argv[sys.argv.index("--out") + 1]).write_text(json.dumps(out, ensure_ascii=False, indent=1))
