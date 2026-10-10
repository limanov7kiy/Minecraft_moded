#!/usr/bin/env bash
# Lycanites Mobs 0.1.0-alpha (1.20.1) ссылается в lycanitesmobs.mixins.json на lycanitesmobs.refmap.json, но не кладёт
# его в jar — миксины к ванильным классам в настоящей игре не применяются. Этот маленький jar подкладывает refmap
# (имена из официальных маппингов → SRG), не трогая сам мод (120 МБ). Имена взяты из маппингов 1.20.1 (Forge 47).
set -eu
here="$(cd "$(dirname "$0")" && pwd)"
jar --create --file "$here/../../pack/mods/lycanites-refmap-fix-1.0.0.jar" --manifest "$here/src/META-INF/MANIFEST.MF" -C "$here/src" lycanitesmobs.refmap.json
echo "built pack/mods/lycanites-refmap-fix-1.0.0.jar"
