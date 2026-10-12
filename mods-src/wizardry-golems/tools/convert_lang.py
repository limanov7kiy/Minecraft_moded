#!/usr/bin/env python3
"""Переводы Wizardry Golems 1.12.2 (.lang, © WinDanesz, GPL-3.0) -> json-ключи порта 1.20.1.

python3 tools/convert_lang.py <папка lang оригинала> src/main/resources/assets/wizardrygolems/lang
"""
import json, re, sys
from pathlib import Path

NS = "wizardrygolems"


def convert_key(k):
    k = k.replace("\\:", ":")
    if m := re.fullmatch(rf"spell\.{NS}:(.+)", k):
        return f"spell.{NS}.{m[1]}"
    if m := re.fullmatch(rf"item\.{NS}:([a-z_]+)\.name", k):
        return f"item.{NS}.{m[1]}"
    if m := re.fullmatch(rf"item\.{NS}:([a-z_]+)\.(.+)", k):
        return f"item.{NS}.{m[1]}.{m[2]}"
    if m := re.fullmatch(rf"tile\.{NS}:([a-z_]+)\.name", k):
        return f"block.{NS}.{m[1]}"
    # миньоны-големы -> имена голем-типов Extra Golems (`entity.<ns>.golem.<id>`)
    if m := re.fullmatch(rf"entity\.{NS}:golem_([a-z_]+)_minion\.name", k):
        return f"entity.{NS}.golem.{m[1]}"
    return None


def main(src, dst):
    dst = Path(dst)
    dst.mkdir(parents=True, exist_ok=True)
    for f in sorted(Path(src).glob("*.lang")):
        out = {}
        for line in f.read_text(encoding="utf-8").splitlines():
            if not line or line.startswith("#") or "=" not in line:
                continue
            k, v = line.split("=", 1)
            nk = convert_key(k.strip())
            if nk:
                # \n в .lang — перевод строки; %% — литеральный процент в String.format (в json одинаково)
                out[nk] = v.strip().replace("\\n", "\n")
        (dst / (f.stem.lower() + ".json")).write_text(
            json.dumps(out, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f.name, "->", len(out))


if __name__ == "__main__":
    main(*sys.argv[1:3])
