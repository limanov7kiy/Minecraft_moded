#!/usr/bin/env bash
# Добавляет в сборку все моды из pack/modlist.txt (уже добавленные пропускаются).
set -u
cd "$(dirname "$0")/../pack"
PACKWIZ="${PACKWIZ:-packwiz}"
fail=0
while read -r src slug _; do
  case "$src" in ''|\#*) continue ;; esac
  dir=mods; cmd="$src"
  # rp = ресурспак с Modrinth (кладётся в resourcepacks/, side=client)
  [ "$src" = rp ] && { dir=resourcepacks; cmd=mr; }
  [ -f "$dir/$slug.pw.toml" ] && continue
  echo ">> $src $slug"
  if ! "$PACKWIZ" "$cmd" add "$slug" -y </dev/null; then echo "!! FAILED: $slug"; fail=1; fi
done < modlist.txt
exit $fail
