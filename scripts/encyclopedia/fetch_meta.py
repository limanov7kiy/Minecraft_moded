#!/usr/bin/env python3 -I
"""Обновляет scripts/encyclopedia/meta-cache.json: описание, slug и номер версии с Modrinth
для всех .pw.toml сборки (публичные данные Modrinth API). build.py работает по этому кэшу офлайн.
Запуск: python3 -I scripts/encyclopedia/fetch_meta.py"""
import json, sys, tomllib, urllib.parse, urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CACHE = Path(__file__).with_name("meta-cache.json")
API = "https://api.modrinth.com/v2"


def get(path, ids):
    url = f"{API}/{path}?ids={urllib.parse.quote(json.dumps(ids))}"
    req = urllib.request.Request(url, headers={"User-Agent": "Minecraft_moded-encyclopedia/1.0"})
    with urllib.request.urlopen(req, timeout=60) as r:
        return json.load(r)


def main():
    pairs = {}  # project id -> version id
    for d in ("pack/mods", "pack/resourcepacks"):
        for f in sorted((ROOT / d).glob("*.pw.toml")):
            m = tomllib.loads(f.read_text(encoding="utf-8")).get("update", {}).get("modrinth")
            if m:
                pairs[m["mod-id"]] = m["version"]
    cache = json.loads(CACHE.read_text(encoding="utf-8")) if CACHE.exists() else {}
    pids, vids = list(pairs), list(pairs.values())
    projects, versions = {}, {}
    for i in range(0, len(pids), 50):
        for p in get("projects", pids[i:i + 50]):
            projects[p["id"]] = p
        for v in get("versions", vids[i:i + 50]):
            versions[v["id"]] = v
    for pid, vid in pairs.items():
        p, v = projects.get(pid), versions.get(vid)
        if not p:
            print("нет на Modrinth:", pid, file=sys.stderr)
            continue
        cache[pid] = {
            "title": p["title"], "slug": p["slug"], "description": p["description"],
            "project_type": p["project_type"],
            "version": v["version_number"] if v else None,
        }
    CACHE.write_text(json.dumps(cache, ensure_ascii=False, indent=1, sort_keys=True) + "\n", encoding="utf-8")
    print(f"в кэше {len(cache)} проектов")


main()
