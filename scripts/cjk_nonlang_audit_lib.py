"""Общие функции для cjk_nonlang_audit.py и cjk_literals_harvest.py: строки из .class (constant pool) и из NBT."""
import gzip, re, struct
CJK_STRICT = re.compile(r"[\u3040-\u30ff\u3400-\u4dbf\u4e00-\u9fff\uac00-\ud7af]")


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




def _walk_nbt(b, i, t, out):
    """Минимальный разбор NBT: собирает все строковые значения. Возвращает новую позицию."""
    if t == 8:
        n = struct.unpack(">H", b[i:i + 2])[0]; out.append(b[i + 2:i + 2 + n].decode("utf-8", "ignore")); return i + 2 + n
    if t in (1,): return i + 1
    if t == 2: return i + 2
    if t in (3, 5): return i + 4
    if t in (4, 6): return i + 8
    if t == 7: n = struct.unpack(">i", b[i:i + 4])[0]; return i + 4 + n
    if t == 11: n = struct.unpack(">i", b[i:i + 4])[0]; return i + 4 + 4 * n
    if t == 12: n = struct.unpack(">i", b[i:i + 4])[0]; return i + 4 + 8 * n
    if t == 9:
        et = b[i]; n = struct.unpack(">i", b[i + 1:i + 5])[0]; i += 5
        for _ in range(n): i = _walk_nbt(b, i, et, out)
        return i
    if t == 10:
        while True:
            tt = b[i]; i += 1
            if tt == 0: return i
            ln = struct.unpack(">H", b[i:i + 2])[0]; i += 2 + ln
            i = _walk_nbt(b, i, tt, out)
    raise ValueError("bad nbt")


def nbt_json_strings(data):
    """Все строки NBT; значения-JSON-компоненты ({"text":...}, [...]) разворачиваются до отдельных text."""
    import json
    try: raw = gzip.decompress(data)
    except Exception: raw = data
    out = []
    try:
        t = raw[0]; ln = struct.unpack(">H", raw[1:3])[0]; _walk_nbt(raw, 3 + ln, t, out)
    except Exception:
        return [m.decode("utf-8", "ignore") for m in re.findall(rb"(?:[\xe3-\xed][\x80-\xbf]{2})+", raw)]
    res = []
    def jwalk(o):
        if isinstance(o, str): res.append(o)
        elif isinstance(o, dict):
            for k, v in o.items():
                if k in ("text", "extra", "with", "translate") or isinstance(v, (list, dict)): jwalk(v)
        elif isinstance(o, list):
            for v in o: jwalk(v)
    for s in out:
        if s.startswith(("{", "[")):
            try: jwalk(json.loads(s)); continue
            except Exception: pass
        res.append(s)
    return res
