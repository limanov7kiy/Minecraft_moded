#!/usr/bin/env python3
"""Готовые модели Yes Steve Model из репозитория Elaina69/Yes-Steve-Model-Repo → packwiz-метафайлы
pack/config/yes_steve_model/custom/<пакет>/<файл>.pw.toml (url на raw.githubusercontent.com с закреплённым коммитом).
Сами .ysm/.zip в наш репозиторий не копируются: packwiz-installer и .mrpack качают их по ссылке,
сервер — scripts/fetch-config-files.py (test-server.sh, export-pack.sh).
Использование: ysm-models.py <клон Elaina69/Yes-Steve-Model-Repo>
Клон: GIT_LFS_SKIP_SMUDGE=1 git clone --filter=blob:none --no-checkout https://github.com/Elaina69/Yes-Steve-Model-Repo
      git sparse-checkout set --no-cone /_Arknights/ /_ArknightsEndfield/ /_PunishingGrayRaven/ && git checkout
Список моделей и авторов — docs/ysm-models.md."""
import hashlib, json, pathlib, subprocess, sys, urllib.parse

src = pathlib.Path(sys.argv[1])
root = pathlib.Path(__file__).resolve().parent.parent
custom = root / "pack" / "config" / "yes_steve_model" / "custom"
commit = subprocess.check_output(["git", "-C", str(src), "rev-parse", "HEAD"], text=True).strip()
# папка репозитория Elaina69 → папка-«модельный пакет» YSM (в меню моделей — отдельный раздел)
packs = {
    "_Arknights": ("arknights", "Arknights", "Модели операторов Arknights"),
    "_ArknightsEndfield": ("arknights_endfield", "Arknights: Endfield", "Модели Arknights: Endfield"),
    "_PunishingGrayRaven": ("punishing_gray_raven", "Punishing: Gray Raven", "Модели Punishing: Gray Raven"),
}
# не открываются в YSM 2.6.5 (лог сервера) — не раздаём; причины — docs/ysm-models.md
skip = set()

n = 0
for folder, (dst, title, desc) in packs.items():
    out = custom / dst
    out.mkdir(parents=True, exist_ok=True)
    for old in out.glob("*.pw.toml"):
        old.unlink()
    (out / "ysm-pack.json").write_text(json.dumps({
        "name": title,
        "description": f"{desc}. Источник: github.com/Elaina69/Yes-Steve-Model-Repo, авторы — в карточке модели и docs/ysm-models.md",
        "lang": {"en_us": {"name": title, "description": f"From github.com/Elaina69/Yes-Steve-Model-Repo ({folder})"}},
    }, ensure_ascii=False, indent=2) + "\n")
    for f in sorted((src / folder).iterdir()):
        if f.suffix not in (".ysm", ".zip") or f.name in skip:
            continue
        url = "https://raw.githubusercontent.com/Elaina69/Yes-Steve-Model-Repo/%s/%s" % (
            commit, urllib.parse.quote(f"{folder}/{f.name}"))
        sha = hashlib.sha256(f.read_bytes()).hexdigest()
        (out / (f.name + ".pw.toml")).write_text(
            f'name = "{f.stem}"\nfilename = "{f.name}"\nside = "both"\n\n'
            f'[download]\nurl = "{url}"\nhash-format = "sha256"\nhash = "{sha}"\n')
        n += 1
print(f"{n} моделей, коммит {commit[:10]}")
