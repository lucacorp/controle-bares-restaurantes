Param(
    [int]$TotalTimeoutMs = 120000
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$xmlDir = 'C:/controle-bares-restaurantes/Backend/data/nfe/xml'
$xmlDirObj = Resolve-Path -Path $xmlDir -ErrorAction SilentlyContinue
if ($xmlDirObj) { $xmlDir = $xmlDirObj.ProviderPath }
$logDir = 'C:/controle-bares-restaurantes/Backend/data/nfe/logs'
$logDirObj = Resolve-Path -Path $logDir -ErrorAction SilentlyContinue
if ($logDirObj) { $logDir = $logDirObj.ProviderPath }
if (-not (Test-Path $logDir)) { New-Item -ItemType Directory -Path $logDir -Force | Out-Null }

# Find latest XML
$latest = Get-ChildItem -Path $xmlDir -Filter '*.xml' -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $latest) {
    Write-Output "No XML files found in: $xmlDir"
    exit 2
}
$xmlPath = $latest.FullName
Write-Output "Using XML: $xmlPath"

$cmdNoTerm = 'NFe.CriarEnviarNFe("' + $xmlPath + '",1,1,1)'
$terminator = "`r`n.`r`n"
$fullCmd = $cmdNoTerm + $terminator

# Encoding: try ISO-8859-1 (Latin1). If not available, fall back to Windows-1252.
try {
    $enc = [System.Text.Encoding]::GetEncoding('iso-8859-1')
} catch {
    $enc = [System.Text.Encoding]::GetEncoding(1252)
}

$acbrHost = '127.0.0.1'
$acbrPort = 3434
 $logFile = Join-Path $logDir 'acbr_raw_test.log'

Function Write-Log($text) {
    $ts = (Get-Date).ToString('yyyy-MM-dd HH:mm:ss.fff')
    $line = "$ts | $text"
    Add-Content -Path $logFile -Value $line -Encoding UTF8
}

Write-Output ("Connecting to {0}:{1} ..." -f $acbrHost, $acbrPort)
Write-Log ("CMD: {0}" -f $cmdNoTerm)

$client = New-Object System.Net.Sockets.TcpClient
try {
    $client.Connect($acbrHost, $acbrPort)
} catch {
    Write-Output ("Failed to connect to {0}:{1} - {2}" -f $acbrHost, $acbrPort, $_)
    Write-Log ("CONNECT-ERROR: {0}" -f $_)
    exit 3
}

$stream = $client.GetStream()
$bytesToSend = $enc.GetBytes($fullCmd)

try {
    $stream.Write($bytesToSend, 0, $bytesToSend.Length)
    $stream.Flush()
    Write-Output "Command sent. Waiting for response (total timeout ${TotalTimeoutMs}ms)..."
    Write-Log "SENT ${bytesToSend.Length} bytes"
} catch {
    Write-Output "Error sending data: $_"
    Write-Log "SEND-ERROR: $_"
    $stream.Close(); $client.Close()
    exit 4
}

# Read loop: per-read short timeout, stop when no data for a short window or total timeout reached
$stream.ReadTimeout = 2000
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
$lastRead = [System.Diagnostics.Stopwatch]::StartNew()
$all = New-Object System.Collections.Generic.List[byte]
$buffer = New-Object byte[] 4096

while ($stopwatch.ElapsedMilliseconds -lt $TotalTimeoutMs -and $lastRead.ElapsedMilliseconds -lt 5000) {
    try {
        $read = $stream.Read($buffer, 0, $buffer.Length)
        if ($read -gt 0) {
            # Copy only the read bytes into a byte[] for AddRange
            $arr = New-Object byte[] $read
            [Array]::Copy($buffer, 0, $arr, 0, $read)
            [void]$all.AddRange($arr)
            $lastRead.Restart()
            # Small immediate continue to try to drain socket
            Start-Sleep -Milliseconds 50
        } else {
            Start-Sleep -Milliseconds 200
        }
    } catch [System.IO.IOException] {
        # Read timeout or other IO exception
        $msg = $_.Exception.Message
        Write-Output "Read exception: $msg"
        Write-Log "READ-EX: $msg"
        Start-Sleep -Milliseconds 200
    }
}

$response = ''
if ($all.Count -gt 0) {
    $response = $enc.GetString($all.ToArray())
    Write-Output "--- Begin raw response ---"
    Write-Output $response
    Write-Output "--- End raw response ---"
    Write-Log "RESPONSE-LEN: $($all.Count)"
    # Save also a cleaned hex/preview line
    $preview = ($response -replace "`r`n", "\\n")
    Write-Log "RESPONSE: $preview"
} else {
    Write-Output "No response received within timeout window."
    Write-Log "NO-RESPONSE"
}

$stream.Close(); $client.Close()
exit 0
