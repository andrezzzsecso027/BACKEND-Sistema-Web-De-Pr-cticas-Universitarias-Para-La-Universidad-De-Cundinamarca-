# Registra cada 5 s el consumo de CPU/memoria del backend (proceso Java en el puerto 8081)
# y del contenedor de la BD de pruebas, mientras corren las pruebas JMeter.
# Uso: powershell -File monitorear-recursos.ps1 -Salida informes-v2-jwt\recursos.csv
param(
    [string]$Salida = "informes-v2-jwt\recursos.csv",
    [string]$Contenedor = "postgres-udecprac-test",
    [int]$Intervalo = 5
)
$backendPid = (Get-NetTCPConnection -LocalPort 8081 -State Listen | Select-Object -First 1).OwningProcess
$jstat = Join-Path (Split-Path (Get-Process -Id $backendPid).Path) "jstat.exe"
$nucleos = [Environment]::ProcessorCount
"timestamp,backend_cpu_pct,backend_mem_privada_mb,backend_heap_usado_mb,backend_hilos,bd_cpu_pct,bd_ram_mb" | Out-File -Encoding utf8 $Salida
$prev = (Get-Process -Id $backendPid).TotalProcessorTime.TotalSeconds
$prevT = Get-Date
while ($true) {
    Start-Sleep -Seconds $Intervalo
    try { $proc = Get-Process -Id $backendPid -ErrorAction Stop } catch { break }
    $now = Get-Date
    $cpu = ($proc.TotalProcessorTime.TotalSeconds - $prev) / ($now - $prevT).TotalSeconds / $nucleos * 100
    $prev = $proc.TotalProcessorTime.TotalSeconds; $prevT = $now
    $heap = ""
    if (Test-Path $jstat) {
        # jstat respeta la configuracion regional (coma decimal): se normaliza a punto antes de convertir
        $g = ((& $jstat -gc $backendPid | Select-Object -Last 1) -replace ',', '.') -split '\s+' | Where-Object { $_ -ne '' }
        # columnas jstat -gc: S0C S1C S0U S1U EC EU OC OU ...
        $heap = [math]::Round(([double]$g[2] + [double]$g[3] + [double]$g[5] + [double]$g[7]) / 1024, 1)
    }
    $bd = (docker stats --no-stream --format "{{.CPUPerc}};{{.MemUsage}}" $Contenedor) -split ';'
    $bdCpu = $bd[0].TrimEnd('%')
    $m = [regex]::Match($bd[1], '([\d\.]+)(MiB|GiB)')
    $bdRam = if ($m.Groups[2].Value -eq 'GiB') { [double]$m.Groups[1].Value * 1024 } else { $m.Groups[1].Value }
    [string]::Format([cultureinfo]::InvariantCulture, "{0},{1},{2},{3},{4},{5},{6}", $now.ToString("yyyy-MM-dd HH:mm:ss"), [math]::Round($cpu, 1),
        [math]::Round($proc.PrivateMemorySize64 / 1MB, 1), $heap, $proc.Threads.Count, $bdCpu, $bdRam) |
        Out-File -Append -Encoding utf8 $Salida
}
