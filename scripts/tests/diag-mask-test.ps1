# Тест маскировки collect-diagnostics.ps1 на фейковой папке (без запуска сбора).
# Запуск: pwsh -NoProfile -File scripts/tests/diag-mask-test.ps1
$ErrorActionPreference = 'Stop'
$src = Join-Path (Split-Path -Parent $PSScriptRoot) 'collect-diagnostics.ps1'
$ast = [Management.Automation.Language.Parser]::ParseFile((Resolve-Path $src), [ref]$null, [ref]$null)
$ast.FindAll({ param($n) $n -is [Management.Automation.Language.FunctionDefinitionAst] }, $true) |
    Where-Object { $_.Name -in 'Note', 'Get-Alias', 'Mask-Text', 'Mask-File', 'Mask-Stage' } |
    ForEach-Object { Invoke-Expression $_.Extent.Text }
$script:Manifest = New-Object System.Collections.Generic.List[string]
Get-Content $src | Where-Object { $_ -match '^\$script:Mask(Ext|Skip) =' } | ForEach-Object { Invoke-Expression $_ }

$fail = 0
function Check([string]$name, $got, $want) {
    if ($got -ceq $want) { Write-Host "ok   $name" } else { Write-Host "FAIL $name`n  got:  $got`n  want: $want"; $script:fail++ }
}

$alias = Get-Alias 'Vasya_Pupkin'
Check 'alias стабилен' (Get-Alias 'vasya_pupkin') $alias
Check 'alias формат' ($alias -match '^Player-[0-9a-f]{4}$') $true
$names = @('Vasya_Pupkin', 'ivan', 'DESKTOP-ABC123', 'me')
$m = { param($t) Mask-Text $t $names $alias }
Check 'ник в чате' (& $m '[Server thread/INFO]: <Vasya_Pupkin> hi') "[Server thread/INFO]: <$alias> hi"
Check 'ник без учёта регистра' (& $m 'vasya_pupkin joined the game') "$alias joined the game"
Check 'ник внутри слова не трогаем' (& $m 'Vasya_PupkinX') 'Vasya_PupkinX'
Check 'короткое имя пропущено' (& $m 'me and you') 'me and you'
Check 'путь Windows' (& $m 'C:\Users\ivan\AppData\Roaming\PrismLauncher') 'C:\Users\USER\AppData\Roaming\PrismLauncher'
Check 'путь с чужим именем' (& $m 'C:\Users\Anna Karenina\Desktop') 'C:\Users\USER\Desktop'
Check 'путь в JSON' (& $m '"C:\\Users\\ivan\\x"') '"C:\\Users\\USER\\x"'
Check 'путь через /' (& $m 'c:/users/ivan/x') 'c:/users/USER/x'
Check 'имя ПК' (& $m 'Machine name: DESKTOP-ABC123') "Machine name: $alias"
Check 'IPv4' (& $m 'Connecting to 203.0.113.45, 25565') 'Connecting to x.x.x.x, 25565'
Check 'IPv4 с портом' (& $m '/192.168.1.7:51234 lost connection') '/x.x.x.x:51234 lost connection'
Check '127.0.0.1 остаётся' (& $m 'bind 127.0.0.1:25565') 'bind 127.0.0.1:25565'
Check 'версия не IP (3 части)' (& $m 'forge 47.4.26 mc 1.20.1') 'forge 47.4.26 mc 1.20.1'
Check 'версия из 5 частей не IP' (& $m 'lib 1.2.3.4.5') 'lib 1.2.3.4.5'
Check 'MAC' (& $m 'Adapter 00-1A-2B-3C-4D-5E up') 'Adapter xx:xx:xx:xx:xx:xx up'
Check 'UUID игрока' (& $m 'UUID of player Vasya_Pupkin is 123e4567-e89b-12d3-a456-426614174000') "UUID of player $alias is xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
Check 'чужой UUID остаётся' (& $m 'entity 123e4567-e89b-12d3-a456-426614174000') 'entity 123e4567-e89b-12d3-a456-426614174000'

# Файлы: UTF-8 с кириллицей, не-UTF-8 (cp1251), пропуски, бинарное расширение
$dir = Join-Path ([IO.Path]::GetTempPath()) ('diagtest-' + [guid]::NewGuid().ToString('N'))
$dLogs = Join-Path $dir 'logs'
$fLatest = Join-Path $dLogs 'latest.log'; $fOld = Join-Path $dLogs 'old.log'; $fGz = Join-Path $dLogs 'x.gz'; $fMods = Join-Path $dir 'mods.csv'
New-Item -ItemType Directory -Path $dLogs -Force | Out-Null
[IO.File]::WriteAllText($fLatest, "Привет Vasya_Pupkin 203.0.113.45`n", (New-Object Text.UTF8Encoding($false)))
[IO.File]::WriteAllBytes($fOld, ([Text.Encoding]::GetEncoding(28591).GetBytes("Vasya_Pupkin ") + [byte[]](0xCF, 0xF0, 0xE8, 0xE2, 0xE5, 0xF2)))
[IO.File]::WriteAllText($fMods, "mod-1.2.3.4.jar,Vasya_Pupkin`n")
[IO.File]::WriteAllBytes($fGz, [Text.Encoding]::ASCII.GetBytes('Vasya_Pupkin'))
Mask-Stage $dir $names $alias
Check 'latest.log (UTF-8)' ([IO.File]::ReadAllText($fLatest).TrimEnd()) "Привет $alias x.x.x.x"
$b = [IO.File]::ReadAllBytes($fOld)
Check 'old.log (cp1251) байты' (($b | ForEach-Object { $_.ToString('x2') }) -join '') ((([Text.Encoding]::ASCII.GetBytes("$alias ") + [byte[]](0xCF, 0xF0, 0xE8, 0xE2, 0xE5, 0xF2)) | ForEach-Object { $_.ToString('x2') }) -join '')
Check 'mods.csv пропущен' ([IO.File]::ReadAllText($fMods).TrimEnd()) 'mod-1.2.3.4.jar,Vasya_Pupkin'
Check '.gz пропущен' ([IO.File]::ReadAllText($fGz)) 'Vasya_Pupkin'
Check 'манифест' ($script:Manifest[-1]) 'OK: маскировка личных данных — изменено файлов: 2 из 2'
Remove-Item -LiteralPath $dir -Recurse -Force
if ($fail) { Write-Host "$fail ПРОВАЛЕНО"; exit 1 } else { Write-Host 'ВСЕ ТЕСТЫ ПРОШЛИ' }
