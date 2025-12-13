# Script de Monitoramento de Testes NFCe
# Autor: GitHub Copilot

$agora = Get-Date

Write-Host "`n╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║          MONITOR DE TESTES NFCe - SEFAZ HOMOLOGAÇÃO             ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host "⏰ Timestamp: $($agora.ToString('yyyy-MM-dd HH:mm:ss'))`n" -ForegroundColor Gray

# Status do Backend
$javaProcess = Get-Process java -ErrorAction SilentlyContinue | Where-Object {
    (Get-NetTCPConnection -OwningProcess $_.Id -ErrorAction SilentlyContinue | Where-Object LocalPort -eq 8080)
}

Write-Host "🖥️  BACKEND STATUS" -ForegroundColor Yellow
if ($javaProcess) {
    Write-Host "   Estado: " -NoNewline
    Write-Host "ONLINE" -ForegroundColor Green
    Write-Host "   PID: $($javaProcess.Id)" -ForegroundColor Gray
    Write-Host "   Porta: 8080" -ForegroundColor Gray
} else {
    Write-Host "   Estado: " -NoNewline
    Write-Host "OFFLINE" -ForegroundColor Red
}

Write-Host ""

# Último XML gerado
$xmlPath = "C:\controle-bares-restaurantes\Backend\data\nfe\xml"

if (Test-Path $xmlPath) {
    $ultimoXML = Get-ChildItem -Path $xmlPath -Filter "NFe_*.xml" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    
    if ($ultimoXML) {
        Write-Host "📄 ÚLTIMO XML GERADO" -ForegroundColor Yellow
        
        # Extrai número NFCe do conteúdo
        $conteudo = Get-Content $ultimoXML.FullName -Raw
        if ($conteudo -match '<nNF>(\d+)</nNF>') {
            $numeroNFe = $matches[1]
        } else {
            $numeroNFe = "?"
        }
        
        # Extrai dhEmi
        $dhEmi = if ($conteudo -match '<dhEmi>(.*?)</dhEmi>') { $matches[1] } else { "N/A" }
        
        # Extrai total
        $total = if ($conteudo -match '<vNF>(.*?)</vNF>') { "R$ $($matches[1])" } else { "N/A" }
        
        Write-Host "   NFCe #$numeroNFe" -ForegroundColor White
        Write-Host "   Arquivo: $($ultimoXML.Name)" -ForegroundColor Gray
        Write-Host "   Gerado em: $($ultimoXML.LastWriteTime.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Gray
        Write-Host "   Total: $total" -ForegroundColor Green
        
        $tempoDecorrido = $agora - $ultimoXML.LastWriteTime
        $minutos = [math]::Floor($tempoDecorrido.TotalMinutes)
        $segundos = $tempoDecorrido.Seconds
        
        Write-Host "   ⏱️  Tempo decorrido: " -NoNewline -ForegroundColor Gray
        
        if ($minutos -lt 5) {
            Write-Host "$minutos min $segundos seg " -NoNewline -ForegroundColor Red
            Write-Host "(⚠️  Cache SEFAZ provável)" -ForegroundColor Yellow
        } elseif ($minutos -lt 10) {
            Write-Host "$minutos min $segundos seg " -NoNewline -ForegroundColor Yellow
            Write-Host "(⏳ Aguardando expiração do cache)" -ForegroundColor Cyan
        } else {
            Write-Host "$minutos min $segundos seg " -NoNewline -ForegroundColor Green
            Write-Host "(✅ Cache provavelmente expirado)" -ForegroundColor Green
        }
        
        Write-Host ""
        Write-Host "─────────────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
        
        # Recomendações
        if ($tempoDecorrido.TotalMinutes -lt 5) {
            Write-Host "💡 RECOMENDAÇÃO: " -NoNewline -ForegroundColor Cyan
            Write-Host "Aguarde mais $([math]::Ceiling(5 - $tempoDecorrido.TotalMinutes)) min para evitar cache" -ForegroundColor Yellow
        } elseif ($tempoDecorrido.TotalMinutes -ge 10) {
            Write-Host "✅ PRONTO PARA TESTAR: " -NoNewline -ForegroundColor Green
            Write-Host "Cache provavelmente expirado. Pode criar uma nova comanda!" -ForegroundColor Cyan
        } else {
            Write-Host "⏳ AGUARDANDO: " -NoNewline -ForegroundColor Yellow
            Write-Host "Mais $([math]::Ceiling(10 - $tempoDecorrido.TotalMinutes)) min recomendado" -ForegroundColor Cyan
        }
    } else {
        Write-Host "📄 ÚLTIMO XML GERADO" -ForegroundColor Yellow
        Write-Host "   Nenhum XML encontrado" -ForegroundColor Red
    }
} else {
    Write-Host "📄 ÚLTIMO XML GERADO" -ForegroundColor Yellow
    Write-Host "   Diretório de XMLs não encontrado" -ForegroundColor Red
}

Write-Host ""
