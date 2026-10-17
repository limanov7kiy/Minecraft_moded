# Сбор диагностики для разбора фризов Minecraft_moded: один zip на рабочий стол.
# Запуск: двойной клик по collect-diagnostics.bat (или powershell -ExecutionPolicy Bypass -File collect-diagnostics.ps1).
# Совместимо с Windows PowerShell 5.1. Файл сохранён в UTF-8 с BOM — не пересохраняйте без BOM.
# Не собирает: accounts.json, токены, пароли, папки лаунчера с аккаунтами. Подробности — docs/diagnostics.md.
param([string]$GameDir = '')

$ErrorActionPreference = 'Continue'
try { [Console]::OutputEncoding = [Text.Encoding]::UTF8 } catch {}

$script:Manifest = New-Object System.Collections.Generic.List[string]
$script:Stage = $null
# Имена, которые не копируем никогда (аккаунты, токены, пароли).
$script:Forbidden = 'account|token|password|passwd|secret|credential|auth|session'
$MaxLogBytes = 30MB   # у слишком больших логов берём хвост

function Note([string]$s) { $script:Manifest.Add($s) | Out-Null }

function Pause-Key {
    Write-Host ''
    Write-Host 'Нажмите любую клавишу, чтобы закрыть окно...'
    try { $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown') } catch { try { Read-Host | Out-Null } catch {} }
}

# Копирование с совместным доступом: работает, даже когда файл держит запущенная игра.
function Copy-Shared([string]$src, [string]$dst, [long]$max) {
    $dir = Split-Path -Parent $dst
    if (-not (Test-Path -LiteralPath $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    $share = [IO.FileShare]::ReadWrite -bor [IO.FileShare]::Delete
    $in = [IO.File]::Open($src, [IO.FileMode]::Open, [IO.FileAccess]::Read, $share)
    try {
        $out = [IO.File]::Create($dst)
        try {
            $trunc = $false
            if ($max -gt 0 -and $in.Length -gt $max) { $in.Seek(-$max, [IO.SeekOrigin]::End) | Out-Null; $trunc = $true }
            $in.CopyTo($out)
        } finally { $out.Dispose() }
        return $trunc
    } finally { $in.Dispose() }
}

function Get-Sha1([string]$p) {
    $share = [IO.FileShare]::ReadWrite -bor [IO.FileShare]::Delete
    $fs = [IO.File]::Open($p, [IO.FileMode]::Open, [IO.FileAccess]::Read, $share)
    try {
        $sha = [Security.Cryptography.SHA1]::Create()
        return (($sha.ComputeHash($fs) | ForEach-Object { $_.ToString('x2') }) -join '')
    } finally { $fs.Dispose() }
}

# Добавить один файл в набор. $rel — путь внутри zip.
function Add-File([string]$src, [string]$rel, [long]$max = 0) {
    if (-not $src -or -not (Test-Path -LiteralPath $src -PathType Leaf)) { Note "ПРОПУЩЕНО (нет файла): $rel"; return $false }
    if ((Split-Path -Leaf $src) -match $script:Forbidden) { Note "ПРОПУЩЕНО (чувствительное имя): $rel"; return $false }
    try {
        $t = Copy-Shared $src (Join-Path $script:Stage $rel) $max
        $len = (Get-Item -LiteralPath $src).Length
        if ($t) { Note ("OK (только последние {0} МБ из {1} МБ): {2}" -f [int]($max / 1MB), [int]($len / 1MB), $rel) }
        else { Note ("OK ({0} КБ): {1}" -f [int]($len / 1KB), $rel) }
        return $true
    } catch {
        Note "ОШИБКА: $rel — $($_.Exception.Message)"
        return $false
    }
}

# Добавить папку целиком (рекурсивно).
function Add-Tree([string]$srcDir, [string]$relBase) {
    if (-not (Test-Path -LiteralPath $srcDir -PathType Container)) { Note "ПРОПУЩЕНО (нет папки): $relBase"; return }
    $prefix = $srcDir.TrimEnd('\')
    Get-ChildItem -LiteralPath $srcDir -Recurse -File -Force -ErrorAction SilentlyContinue | ForEach-Object {
        $sub = $_.FullName.Substring($prefix.Length).TrimStart('\')
        Add-File $_.FullName (Join-Path $relBase $sub) | Out-Null
    }
}

# Добавить из папки всё, что подходит под маски имён (файлы и папки).
function Add-Glob([string]$dir, [string[]]$patterns, [string]$relBase) {
    if (-not (Test-Path -LiteralPath $dir -PathType Container)) { Note "ПРОПУЩЕНО (нет папки): $relBase"; return }
    foreach ($pat in $patterns) {
        $found = @(Get-ChildItem -LiteralPath $dir -Filter $pat -Force -ErrorAction SilentlyContinue)
        if ($found.Count -eq 0) { Note "ПРОПУЩЕНО (нет): $relBase\$pat"; continue }
        foreach ($f in $found) {
            if ($f.PSIsContainer) { Add-Tree $f.FullName (Join-Path $relBase $f.Name) }
            else { Add-File $f.FullName (Join-Path $relBase $f.Name) | Out-Null }
        }
    }
}

# ---------- Поиск папки игры ----------
function Resolve-Candidate([string]$path) {
    if (-not $path -or -not (Test-Path -LiteralPath $path -PathType Container)) { return $null }
    $path = (Resolve-Path -LiteralPath $path).ProviderPath.TrimEnd('\')
    $inst = $null; $game = $null
    if (Test-Path -LiteralPath (Join-Path $path 'instance.cfg')) {
        # корень инстанса Prism: игра в minecraft или .minecraft
        $inst = $path
        foreach ($n in '.minecraft', 'minecraft') {
            $c = Join-Path $path $n
            if (Test-Path -LiteralPath $c -PathType Container) { $game = $c; break }
        }
        if (-not $game) { return $null }
    } elseif ((Test-Path -LiteralPath (Join-Path $path 'mods')) -or (Test-Path -LiteralPath (Join-Path $path 'options.txt')) -or (Test-Path -LiteralPath (Join-Path $path 'logs'))) {
        $game = $path
        $parent = Split-Path -Parent $path
        if ($parent -and (Test-Path -LiteralPath (Join-Path $parent 'instance.cfg'))) { $inst = $parent }
    } else { return $null }
    $log = Join-Path $game 'logs\latest.log'
    $stamp = [datetime]::MinValue
    if (Test-Path -LiteralPath $log) { $stamp = (Get-Item -LiteralPath $log).LastWriteTime }
    return [pscustomobject]@{ Game = $game; Inst = $inst; Name = (Split-Path -Leaf $(if ($inst) { $inst } else { $game })); Stamp = $stamp }
}

function Find-GameDir {
    # 1) явный параметр
    if ($GameDir) { $r = Resolve-Candidate $GameDir; if ($r) { return $r }; Write-Host "Папка из параметра не похожа на папку игры: $GameDir" }
    # 2) запуск из папки инстанса / .minecraft (рядом со скриптом или текущая)
    $here = @()
    if ($PSScriptRoot) { $here += $PSScriptRoot; $here += (Split-Path -Parent $PSScriptRoot) }
    $here += (Get-Location).ProviderPath
    foreach ($h in $here) { $r = Resolve-Candidate $h; if ($r) { Write-Host "Найдена папка игры рядом со скриптом: $($r.Game)"; return $r } }
    # 3) стандартные места лаунчеров
    $found = New-Object System.Collections.Generic.List[object]
    $roots = @()
    if ($env:APPDATA) {
        $roots += (Join-Path $env:APPDATA 'PrismLauncher\instances')
        $roots += (Join-Path $env:APPDATA 'ModrinthApp\profiles')
    }
    foreach ($root in $roots) {
        if (-not (Test-Path -LiteralPath $root)) { continue }
        Get-ChildItem -LiteralPath $root -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike '_*' } | ForEach-Object {
            $r = Resolve-Candidate $_.FullName
            if ($r) { $found.Add($r) | Out-Null }
        }
    }
    if ($env:APPDATA) { $r = Resolve-Candidate (Join-Path $env:APPDATA '.minecraft'); if ($r) { $found.Add($r) | Out-Null } }
    $list = @($found | Sort-Object Stamp -Descending)
    if ($list.Count -eq 1) { Write-Host "Найден один экземпляр: $($list[0].Name)"; return $list[0] }
    while ($true) {
        if ($list.Count -gt 0) {
            Write-Host 'Найдены экземпляры (сверху — где играли последним):'
            for ($i = 0; $i -lt $list.Count; $i++) {
                $s = if ($list[$i].Stamp -gt [datetime]::MinValue) { $list[$i].Stamp.ToString('yyyy-MM-dd HH:mm') } else { 'логов ещё нет' }
                Write-Host ("  {0}) {1}   [{2}]" -f ($i + 1), $list[$i].Name, $s)
            }
            Write-Host '  0) ввести путь вручную'
            $a = (Read-Host 'Номер').Trim()
            if ($a -match '^\d+$' -and [int]$a -ge 1 -and [int]$a -le $list.Count) { return $list[[int]$a - 1] }
            if ($a -ne '0') { Write-Host 'Не понял номер, ещё раз.'; continue }
        } else { Write-Host 'Экземпляры автоматически не найдены.' }
        $p = (Read-Host 'Путь к папке экземпляра или к .minecraft (можно перетащить папку в окно)').Trim().Trim('"')
        $r = Resolve-Candidate $p
        if ($r) { return $r }
        Write-Host 'Это не похоже на папку игры (нет mods / logs / options.txt / instance.cfg). Попробуйте ещё раз.'
    }
}

# Прочитать key=value из cfg (без секретов не фильтруем — читаем только нужные ключи вызывающим кодом).
function Read-Cfg([string]$path) {
    $h = @{}
    if ($path -and (Test-Path -LiteralPath $path)) {
        $tmp = Join-Path $env:TEMP ("diagcfg-" + [guid]::NewGuid().ToString('N'))
        try {
            Copy-Shared $path $tmp 0 | Out-Null
            Get-Content -LiteralPath $tmp -Encoding UTF8 | ForEach-Object {
                $i = $_.IndexOf('=')
                if ($i -gt 0) { $h[$_.Substring(0, $i).Trim()] = $_.Substring($i + 1).Trim() }
            }
        } catch {} finally { Remove-Item -LiteralPath $tmp -Force -ErrorAction SilentlyContinue }
    }
    return $h
}

# ======================= Основной ход =======================
try {
    Write-Host '=== Сбор диагностики Minecraft_moded ===' -ForegroundColor Cyan
    Write-Host ''
    $sel = Find-GameDir
    $game = $sel.Game; $inst = $sel.Inst
    Write-Host ''
    Write-Host "Папка игры: $game"
    if ($inst) { Write-Host "Экземпляр Prism: $inst" }

    $nick = (Read-Host 'Ваш ник в игре (Enter — взять имя компьютера)').Trim()
    if (-not $nick) { $nick = $env:COMPUTERNAME }
    $nick = ($nick -replace '[\\/:*?"<>|\s]', '_')
    if (-not $nick) { $nick = 'player' }

    Write-Host ''
    Write-Host 'ВНИМАНИЕ: в логах есть ваш ник и адрес сервера (IP). Пароли, токены и данные аккаунтов' -ForegroundColor Yellow
    Write-Host 'НЕ собираются. Zip вы сможете открыть и посмотреть до отправки.' -ForegroundColor Yellow
    $ok = (Read-Host 'Продолжить? [Enter — да, N — отмена]').Trim()
    if ($ok -match '^(n|no|н|нет)$') { Write-Host 'Отменено.'; Pause-Key; exit 0 }

    $dxdiag = $false
    $d = (Read-Host 'Снять ещё отчёт dxdiag (до минуты, полезен для видеокарты)? [Enter — да, N — нет]').Trim()
    if ($d -notmatch '^(n|no|н|нет)$') { $dxdiag = $true }

    $script:Stage = Join-Path $env:TEMP ("diag-" + [guid]::NewGuid().ToString('N'))
    New-Item -ItemType Directory -Path $script:Stage -Force | Out-Null

    $running = @(Get-Process -Name javaw, java -ErrorAction SilentlyContinue)
    if ($running.Count -gt 0) { Note "Игра/Java запущена (процессов: $($running.Count)) — логи копируются через временную копию." }
    else { Note 'Игра не запущена: latest.log относится к последнему запуску.' }

    Write-Host ''
    Write-Host 'Копирую файлы...'

    # --- логи ---
    $logs = Join-Path $game 'logs'
    Add-File (Join-Path $logs 'latest.log') 'logs\latest.log' $MaxLogBytes | Out-Null
    Add-File (Join-Path $logs 'debug.log') 'logs\debug.log' $MaxLogBytes | Out-Null
    $gz = @(Get-ChildItem -LiteralPath $logs -Filter '*.log.gz' -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 3)
    if ($gz.Count -eq 0) { Note 'ПРОПУЩЕНО (нет): logs\*.log.gz' }
    foreach ($f in $gz) { Add-File $f.FullName ("logs\" + $f.Name) | Out-Null }

    # --- крэши ---
    $cr = Join-Path $game 'crash-reports'
    $crashes = @(Get-ChildItem -LiteralPath $cr -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 3)
    if ($crashes.Count -eq 0) { Note 'ПРОПУЩЕНО (нет): crash-reports\* (крэшей не было)' }
    foreach ($f in $crashes) { Add-File $f.FullName ("crash-reports\" + $f.Name) | Out-Null }
    $hs = @()
    foreach ($dir in $game, $inst) {
        if ($dir) { $hs += @(Get-ChildItem -LiteralPath $dir -Filter 'hs_err_pid*.log' -File -ErrorAction SilentlyContinue) }
    }
    $hs = @($hs | Sort-Object LastWriteTime -Descending | Select-Object -First 3)
    if ($hs.Count -eq 0) { Note 'ПРОПУЩЕНО (нет): hs_err_pid*.log (вылетов JVM не было)' }
    foreach ($f in $hs) { Add-File $f.FullName ("crash-reports\" + $f.Name) | Out-Null }

    # --- настройки игры и графики ---
    Add-File (Join-Path $game 'options.txt') 'options.txt' | Out-Null
    Add-File (Join-Path $game 'optionsof.txt') 'optionsof.txt' | Out-Null
    $cfgDir = Join-Path $game 'config'
    Add-Glob $cfgDir @('embeddium*', 'sodium*', 'oculus*') 'config'
    Add-File (Join-Path $cfgDir 'forge-client.toml') 'config\forge-client.toml' | Out-Null
    Add-Tree (Join-Path $game 'serverconfig') 'serverconfig'
    $saves = Join-Path $game 'saves'
    if (Test-Path -LiteralPath $saves) {
        Get-ChildItem -LiteralPath $saves -Directory -ErrorAction SilentlyContinue | ForEach-Object {
            $sc = Join-Path $_.FullName 'serverconfig'
            if (Test-Path -LiteralPath $sc) { Add-Tree $sc ("serverconfig-saves\" + $_.Name) }
        }
    }

    # --- шейдеры: только имена + выбранный пак ---
    $sh = New-Object System.Collections.Generic.List[string]
    $shDir = Join-Path $game 'shaderpacks'
    if (Test-Path -LiteralPath $shDir) {
        $items = @(Get-ChildItem -LiteralPath $shDir -Force -ErrorAction SilentlyContinue)
        $sh.Add("Паков в shaderpacks: $($items.Count)") | Out-Null
        foreach ($i in $items) { $sh.Add($(if ($i.PSIsContainer) { "[папка] $($i.Name)" } else { "{0}  ({1} КБ)" -f $i.Name, [int]($i.Length / 1KB) })) | Out-Null }
    } else { $sh.Add('Папки shaderpacks нет.') | Out-Null }
    $oc = Join-Path $cfgDir 'oculus.properties'
    $sel2 = $null
    if (Test-Path -LiteralPath $oc) {
        $props = Read-Cfg $oc
        $sel2 = $props['shaderPack']
        $sh.Add('') | Out-Null
        $sh.Add("Выбранный шейдер (oculus.properties, shaderPack): " + $(if ($sel2) { $sel2 } else { '<не задан / шейдеры выключены>' })) | Out-Null
        if ($props.ContainsKey('enableShaders')) { $sh.Add("enableShaders: " + $props['enableShaders']) | Out-Null }
    } else { $sh.Add('config\oculus.properties не найден — выбранный пак неизвестен.') | Out-Null }
    $sh | Set-Content -LiteralPath (Join-Path $script:Stage 'shaderpacks.txt') -Encoding UTF8
    Note 'OK: shaderpacks.txt'

    # --- Prism ---
    $icfg = @{}
    if ($inst) {
        $src = Join-Path $inst 'instance.cfg'
        if (Add-File $src 'prism\instance.cfg') {
            # на всякий случай вычищаем строки, похожие на секреты
            $dst = Join-Path $script:Stage 'prism\instance.cfg'
            $lines = Get-Content -LiteralPath $dst -Encoding UTF8 | Where-Object { $_ -notmatch $script:Forbidden }
            $lines | Set-Content -LiteralPath $dst -Encoding UTF8
        }
        Add-File (Join-Path $inst 'mmc-pack.json') 'prism\mmc-pack.json' | Out-Null
        $icfg = Read-Cfg $src
    } else { Note 'ПРОПУЩЕНО: instance.cfg и mmc-pack.json (папка игры не из Prism-экземпляра)' }

    # --- пакет (packwiz) ---
    foreach ($d2 in $game, $inst) {
        if (-not $d2) { continue }
        foreach ($n in 'pack.toml', 'index.toml') {
            $p = Join-Path $d2 $n
            if (Test-Path -LiteralPath $p) { Add-File $p ("pack\" + $n) | Out-Null; }
        }
    }

    # --- моды: имя, размер, SHA1 ---
    $modsDir = Join-Path $game 'mods'
    if (Test-Path -LiteralPath $modsDir) {
        $mf = @(Get-ChildItem -LiteralPath $modsDir -File -ErrorAction SilentlyContinue | Sort-Object Name)
        Write-Host "Считаю SHA1 модов ($($mf.Count) шт.), это может занять минуту..."
        $rows = New-Object System.Collections.Generic.List[object]
        foreach ($m in $mf) {
            $h = ''
            try { $h = Get-Sha1 $m.FullName } catch { $h = 'ERR: ' + $_.Exception.Message }
            $rows.Add([pscustomobject]@{ Name = $m.Name; SizeBytes = $m.Length; SHA1 = $h }) | Out-Null
        }
        $rows | Export-Csv -LiteralPath (Join-Path $script:Stage 'mods.csv') -NoTypeInformation -Encoding UTF8
        Note "OK: mods.csv (файлов: $($mf.Count))"
    } else { Note 'ПРОПУЩЕНО (нет папки): mods' }

    # ---------- system.txt ----------
    Write-Host 'Собираю сведения о системе...'
    $sys = New-Object System.Collections.Generic.List[string]
    function S([string]$x) { $sys.Add($x) | Out-Null }
    function Section([string]$t) { S ''; S "=== $t ===" }
    S "Диагностика Minecraft_moded, снято $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    S "Папка игры: $game"

    Section 'ОС'
    try {
        $os = Get-CimInstance Win32_OperatingSystem
        S "$($os.Caption) $($os.Version) build $($os.BuildNumber) ($($os.OSArchitecture))"
        S ("Загружена: {0}; аптайм, ч: {1:N1}" -f $os.LastBootUpTime, ((Get-Date) - $os.LastBootUpTime).TotalHours)
    } catch { S "ОШИБКА: $($_.Exception.Message)"; $os = $null }

    Section 'Процессор'
    try {
        foreach ($c in @(Get-CimInstance Win32_Processor)) {
            S "$($c.Name.Trim()); ядер: $($c.NumberOfCores), потоков: $($c.NumberOfLogicalProcessors), макс. МГц: $($c.MaxClockSpeed), сейчас МГц: $($c.CurrentClockSpeed)"
        }
    } catch { S "ОШИБКА: $($_.Exception.Message)" }

    Section 'Память'
    try {
        if ($os) {
            S ("Всего: {0:N1} ГБ; свободно сейчас: {1:N1} ГБ" -f ($os.TotalVisibleMemorySize / 1MB), ($os.FreePhysicalMemory / 1MB))
        }
        foreach ($pf in @(Get-CimInstance Win32_PageFileUsage -ErrorAction SilentlyContinue)) {
            S "Файл подкачки: $($pf.Name), размер $($pf.AllocatedBaseSize) МБ, занято $($pf.CurrentUsage) МБ"
        }
    } catch { S "ОШИБКА: $($_.Exception.Message)" }

    Section 'Видеокарты'
    try {
        foreach ($g in @(Get-CimInstance Win32_VideoController)) {
            $dd = ''
            if ($g.DriverDate) { $dd = $g.DriverDate.ToString('yyyy-MM-dd') }
            S "$($g.Name); драйвер $($g.DriverVersion) от $dd; VRAM по WMI: $([int]($g.AdapterRAM / 1MB)) МБ (для >4 ГБ занижено); режим $($g.CurrentHorizontalResolution)x$($g.CurrentVerticalResolution)@$($g.CurrentRefreshRate) Гц"
        }
    } catch { S "ОШИБКА: $($_.Exception.Message)" }

    Section 'Диск с игрой'
    try {
        $drive = $game.Substring(0, 1)
        if ($game -match '^[A-Za-z]:') {
            $ld = Get-CimInstance Win32_LogicalDisk -Filter "DeviceID='${drive}:'"
            S ("Диск {0}: свободно {1:N1} ГБ из {2:N1} ГБ" -f $drive, ($ld.FreeSpace / 1GB), ($ld.Size / 1GB))
            try {
                $pd = Get-Partition -DriveLetter $drive -ErrorAction Stop | Get-Disk -ErrorAction Stop | Get-PhysicalDisk -ErrorAction Stop
                S "Тип: $($pd.MediaType), шина $($pd.BusType), модель $($pd.FriendlyName)"
            } catch { S "Тип диска (SSD/HDD) определить не удалось: $($_.Exception.Message)" }
        } else { S "Путь не на обычном диске (сетевой?): $game" }
    } catch { S "ОШИБКА: $($_.Exception.Message)" }

    Section 'Режим питания'
    try { S ((cmd /c 'powercfg /getactivescheme 2>&1') -join ' ') } catch { S "ОШИБКА: $($_.Exception.Message)" }
    try {
        $bat = @(Get-CimInstance Win32_Battery -ErrorAction SilentlyContinue)
        if ($bat.Count -gt 0) { S "Это ноутбук/есть батарея; заряд $($bat[0].EstimatedChargeRemaining)%, статус $($bat[0].BatteryStatus) (2 = от сети)" }
    } catch {}

    Section 'Антивирус (может тормозить загрузку модов)'
    try {
        $av = @(Get-CimInstance -Namespace 'root/SecurityCenter2' -ClassName AntiVirusProduct -ErrorAction Stop)
        foreach ($a in $av) { S $a.displayName }
        if ($av.Count -eq 0) { S 'не найден' }
    } catch { S 'не удалось определить' }

    Section 'Java'
    $glob = @{}
    if ($env:APPDATA) { $glob = Read-Cfg (Join-Path $env:APPDATA 'PrismLauncher\prismlauncher.cfg') }
    $keys = 'JavaPath', 'JavaVersion', 'JavaVendor', 'JavaArchitecture', 'MinMemAlloc', 'MaxMemAlloc', 'JvmArgs', 'OverrideJavaLocation', 'OverrideJavaArgs', 'OverrideMemory', 'WrapperCommand', 'PreLaunchCommand', 'UseDiscreteGpu', 'EnableFeralGamemode'
    S 'instance.cfg:'
    foreach ($k in $keys) { if ($icfg.ContainsKey($k)) { S "  $k = $($icfg[$k])" } }
    if ($glob.Count -gt 0) {
        S 'prismlauncher.cfg (общие настройки Prism, только Java/память):'
        foreach ($k in $keys) { if ($glob.ContainsKey($k)) { S "  $k = $($glob[$k])" } }
    }
    $javaExe = $null
    if ($icfg['JavaPath'] -and (Test-Path -LiteralPath $icfg['JavaPath'])) { $javaExe = $icfg['JavaPath'] }
    elseif ($glob['JavaPath'] -and (Test-Path -LiteralPath $glob['JavaPath'])) { $javaExe = $glob['JavaPath'] }
    else {
        $rp = $running | Where-Object { $_.Path } | Select-Object -First 1
        if ($rp) { $javaExe = $rp.Path; S "Java взята из запущенного процесса: $javaExe" }
        else { $gc = Get-Command java -ErrorAction SilentlyContinue; if ($gc) { $javaExe = $gc.Source; S "Java из PATH: $javaExe" } }
    }
    if ($javaExe) {
        S "java -version ($javaExe):"
        try { foreach ($l in @(cmd /c "`"$javaExe`" -version 2>&1")) { S "  $l" } } catch { S "  ОШИБКА: $($_.Exception.Message)" }
    } else { S 'Путь к Java не определён (Prism использует свою встроенную — укажите её в Настройках экземпляра).' }

    Section 'Java/игра: процессы (без командной строки — в ней токен входа)'
    foreach ($p in $running) {
        try { S ("{0} PID {1}: память {2:N0} МБ, процессорное время {3:N0} с" -f $p.ProcessName, $p.Id, ($p.WorkingSet64 / 1MB), $p.TotalProcessorTime.TotalSeconds) } catch {}
    }
    if ($running.Count -eq 0) { S 'не запущено' }

    Section 'Самые тяжёлые процессы по памяти'
    try {
        Get-Process | Sort-Object WorkingSet64 -Descending | Select-Object -First 8 | ForEach-Object {
            S ("{0,-28} {1,7:N0} МБ" -f $_.ProcessName, ($_.WorkingSet64 / 1MB))
        }
    } catch {}

    $sys | Set-Content -LiteralPath (Join-Path $script:Stage 'system.txt') -Encoding UTF8
    Note 'OK: system.txt'

    # --- dxdiag (по желанию, с таймаутом 60 с) ---
    if ($dxdiag) {
        Write-Host 'Снимаю dxdiag (до 60 секунд)...'
        $dxOut = Join-Path $script:Stage 'dxdiag.txt'
        try {
            $proc = Start-Process -FilePath 'dxdiag.exe' -ArgumentList '/t', "`"$dxOut`"" -PassThru -WindowStyle Hidden
            if (-not $proc.WaitForExit(60000)) {
                try { $proc.Kill() } catch {}
                Note 'ПРОПУЩЕНО: dxdiag не уложился в 60 с'
            } elseif (Test-Path -LiteralPath $dxOut) { Note 'OK: dxdiag.txt' }
            else { Note 'ПРОПУЩЕНО: dxdiag не создал файл' }
        } catch { Note "ОШИБКА: dxdiag — $($_.Exception.Message)" }
    } else { Note 'ПРОПУЩЕНО: dxdiag (отказались)' }

    # ---------- manifest + zip ----------
    $head = @("manifest — что собрано; создан $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), ник/ПК: $nick", '')
    ($head + $script:Manifest) | Set-Content -LiteralPath (Join-Path $script:Stage 'manifest.txt') -Encoding UTF8

    $desktop = [Environment]::GetFolderPath('Desktop')
    if (-not $desktop -or -not (Test-Path -LiteralPath $desktop)) { $desktop = Join-Path $env:USERPROFILE 'Desktop' }
    if (-not (Test-Path -LiteralPath $desktop)) { $desktop = $env:USERPROFILE }
    $zip = Join-Path $desktop ("diag-{0}-{1}.zip" -f $nick, (Get-Date -Format 'yyyyMMdd-HHmm'))
    if (Test-Path -LiteralPath $zip) { Remove-Item -LiteralPath $zip -Force }
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [IO.Compression.ZipFile]::CreateFromDirectory($script:Stage, $zip, [IO.Compression.CompressionLevel]::Optimal, $false)

    Write-Host ''
    Write-Host 'ГОТОВО!' -ForegroundColor Green
    Write-Host "Файл для отправки: $zip" -ForegroundColor Green
    Write-Host ("Размер: {0:N1} МБ. Пропущенное и ошибки — в manifest.txt внутри архива." -f ((Get-Item -LiteralPath $zip).Length / 1MB))
    try { Start-Process explorer.exe -ArgumentList "/select,`"$zip`"" } catch {}
} catch {
    Write-Host ''
    Write-Host "Ошибка: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.ScriptStackTrace
    Write-Host 'Сделайте скриншот этого окна и пришлите.'
} finally {
    if ($script:Stage -and (Test-Path -LiteralPath $script:Stage)) { Remove-Item -LiteralPath $script:Stage -Recurse -Force -ErrorAction SilentlyContinue }
}
Pause-Key
