#!/usr/bin/env python3
"""Модели Azur Lane для Yes Steve Model из сборника nekohalawrence/FREE-YSM-AUTHOR (ветка main, папки по авторам)
→ packwiz-метафайлы pack/config/yes_steve_model/custom/azur_lane_ysmauthor/<файл>.pw.toml
(url на raw.githubusercontent.com с закреплённым коммитом + sha256; сами модели в репозиторий не копируются).
Берутся все папки AL_* (и «圣路易斯»), кроме BA_* (Blue Archive); файлы, уже раздаваемые другими пакетами
(тот же sha256 в любом pack/config/yes_steve_model/custom/*/*.pw.toml), и дубли внутри сборника пропускаются.
Имена файлов приводятся к ASCII: <номер автора>_<латиница из имени>[_<хэш>] — иначе в сборнике много одинаковых
(AL_柴郡_Cheshire.ysm у трёх авторов) и YSM берёт id модели из имени файла.
Использование: ysm-models-author.py <клон FREE-YSM-AUTHOR, main, с файлами AL> [список_исключений.txt]
Клон: GIT_LFS_SKIP_SMUDGE=1 git clone --no-checkout --filter=blob:none -b main https://github.com/nekohalawrence/FREE-YSM-AUTHOR
      затем git checkout HEAD -- <пути *.ysm/*.zip из папок AL_*>
Исключения — по одному имени метафайла (без .pw.toml) в строке: модели, которые YSM 2.6.5 не читает (docs/ysm-models.md)."""
import hashlib, json, pathlib, re, subprocess, sys, tomllib, urllib.parse

src = pathlib.Path(sys.argv[1])
skip = set()
if len(sys.argv) > 2:
    skip = {l.strip() for l in open(sys.argv[2], encoding="utf-8") if l.strip() and not l.startswith("#")}
root = pathlib.Path(__file__).resolve().parent.parent
custom = root / "pack" / "config" / "yes_steve_model" / "custom"
out = custom / "azur_lane_ysmauthor"
commit = subprocess.check_output(["git", "-C", str(src), "rev-parse", "HEAD"], text=True).strip()
have = set()
for f in custom.glob("*/*.pw.toml"):
    if f.parent != out:
        have.add(tomllib.loads(f.read_text())["download"]["hash"])
out.mkdir(parents=True, exist_ok=True)
for old in out.glob("*.pw.toml"):
    old.unlink()
(out / "ysm-pack.json").write_text(json.dumps({
    "name": "Azur Lane (FREE-YSM-AUTHOR)",
    "description": "Модели Azur Lane из сборника github.com/nekohalawrence/FREE-YSM-AUTHOR; часть может быть 18+ (так пишет автор сборника). Авторы — в карточке модели и docs/ysm-models.md",
    "lang": {"en_us": {"name": "Azur Lane (FREE-YSM-AUTHOR)",
                       "description": "From github.com/nekohalawrence/FREE-YSM-AUTHOR; some models may be 18+"}},
}, ensure_ascii=False, indent=2) + "\n")

paths = subprocess.check_output(["git", "-C", str(src), "-c", "core.quotepath=off", "ls-tree", "-r", "--name-only", "HEAD"],
                                text=True).splitlines()
rows, seen, used = [], set(), set()
for p in sorted(paths):
    parts = p.split("/")
    if len(parts) < 3 or not p.endswith((".ysm", ".zip")):
        continue
    d = parts[1]
    if d.startswith("BA_") or not (d.startswith("AL_") or "圣路易斯" in d or "碧蓝航线" in p):
        continue
    f = src / p
    if not f.exists():
        sys.exit(f"нет файла (не выкачан): {p}")
    data = f.read_bytes()
    sha = hashlib.sha256(data).hexdigest()
    if sha in have or sha in seen:
        continue
    seen.add(sha)
    ext = f.suffix
    author = parts[0].split("-")[0]
    asc = lambda t: re.sub(r"_+", "_", re.sub(r"[^A-Za-z0-9._-]+", "_", t.encode("ascii", "ignore").decode())).strip("_.")
    stem = asc(f.stem)
    if not re.search(r"[A-Za-z]{3}", stem.replace("AL", "")):  # в имени файла нет латиницы — добавляем папку
        stem = asc(d) + ("_" + stem if stem else "")
    name = f"{author}_{stem}"
    if "非公开" in f.stem:  # «закрытая» версия (на деле — 18+), «全年龄» — для всех возрастов: помечаем в имени файла
        name += "_18plus"
    elif "全年龄" in f.stem:
        name += "_allages"
    if re.search("nsfw", f.stem, re.I) and "nsfw" not in name.lower():
        name += "_18plus"
    if name + ext in used:
        name += "_" + sha[:6]
    fn = name + ext
    used.add(fn)
    if fn in skip or name in skip:
        continue
    url = "https://raw.githubusercontent.com/nekohalawrence/FREE-YSM-AUTHOR/%s/%s" % (commit, urllib.parse.quote(p))
    (out / (fn + ".pw.toml")).write_text(
        f'name = "{name}"\nfilename = "{fn}"\nside = "both"\n\n'
        f'[download]\nurl = "{url}"\nhash-format = "sha256"\nhash = "{sha}"\n')
    rows.append((fn, len(data), p))
print(f"{len(rows)} моделей, {sum(r[1] for r in rows)/2**20:.1f} МБ, коммит {commit[:10]}")
pathlib.Path("/tmp/ysm-author-index.tsv").write_text("".join(f"{a}\t{b}\t{c}\n" for a, b, c in rows))   # fn, размер, путь в сборнике — для docs
