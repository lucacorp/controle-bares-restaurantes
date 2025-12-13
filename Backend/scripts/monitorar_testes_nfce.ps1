# Script de Monitoramento de Testes NFCe
# Exibe status do backend, último XML gerado e detecta cache da SEFAZ
# Autor: GitHub Copilot

param(
    [Parameter(Mandatory=$false)]
    [switch]$Continuo = $false,
    
    [Parameter(Mandatory=$false)]
    [int]$IntervaloSegundos = 30
)

function Get-BackendStatus {
    $javaProcess = Get-Process java -ErrorAction SilentlyContinue | 
        Where-Object {(Get-NetTCPConnection -OwningProcess $_.Id -ErrorAction SilentlyContinue | 
        Where-Object LocalPort -eq 8080)}
    
    if ($javaProcess) {
        return @{
            Status = "ONLINE"
            PID = $javaProcess.Id
            Cor = "Green"
        }
    }
    else {
        return @{
            Status = "OFFLINE"
            PID = $null
            Cor = "Red"
        }
    }
}

function Get-UltimoXMLInfo {
    $xmlPath = "C:\controle-bares-restaurantes\Backend\data\nfe\xml"
    
    if (-not (Test-Path $xmlPath)) {
        return $null
    }
    
    $ultimoXML = Get-ChildItem -Path $xmlPath -Filter "NFe_*.xml" | 
        Sort-Object LastWriteTime -Descending | 
        Select-Object -First 1
    
    if ($ultimoXML) {
        # Extrai número da NFCe do nome do arquivo
        if ($ultimoXML.Name -match 'nNF>(\d+)</nNF') {
            $numeroNFe = $matches[1]
        }
        elseif ($ultimoXML.Name -match '0000(\d{3,})\d{10}\.xml') {
            $numeroNFe = [int]$matches[1]
        }
        else {
            $numeroNFe = "?"
        }
        
        $conteudo = Get-Content $ultimoXML.FullName -Raw
        
        # Extrai dhEmi
        $dhEmi = if ($conteudo -match '<dhEmi>(.*?)</dhEmi>') { $matches[1] } else { "N/A" }
        
        # Extrai total
        $total = if ($conteudo -match '<vNF>(.*?)</vNF>') { "R$ $($matches[1])" } else { "N/A" }
        
        return @{
            Arquivo = $ultimoXML.Name
            NumeroNFe = $numeroNFe
            DataGeracao = $ultimoXML.LastWriteTime
            DhEmi = $dhEmi
            Total = $total
            TamanhoKB = [math]::Round($ultimoXML.Length / 1KB, 2)
        }
    }
    
    return $null
}

function Get-UltimoLog {
    $logContent = ""
    
    # Tenta ler os últimos logs do Spring Boot
    try {
        $logFiles = @(
            "C:\controle-bares-restaurantes\Backend\logs\spring-boot-logger.log",
            "C:\controle-bares-restaurantes\Backend\nohup.out"
        )
        
        foreach ($logFile in $logFiles) {
            if (Test-Path $logFile) {
                $logContent = Get-Content $logFile -Tail 50 -ErrorAction SilentlyContinue
                break
            }
        }
    }
    catch {
        # Ignora erros ao ler logs
    }
    
    if ($logContent) {
        # Procura última resposta SEFAZ
        $respostaSefaz = $logContent | Select-String -Pattern "cStat>(\d+)" | Select-Object -Last 1
        $motivoSefaz = $logContent | Select-String -Pattern "xMotivo>(.*?)</xMotivo" | Select-Object -Last 1
        $dhRecbto = $logContent | Select-String -Pattern "dhRecbto>(.*?)</dhRecbto" | Select-Object -Last 1
        
        if ($respostaSefaz) {
            $cStat = $respostaSefaz.Line -replace '.*cStat>(\d+).*', '$1'
            $motivo = if ($motivoSefaz) { 
                $motivoSefaz.Line -replace '.*xMotivo>(.*?)</xMotivo.*', '$1' 
            } else { 
                "N/A" 
            }
            $recebimento = if ($dhRecbto) {
                $dhRecbto.Line -replace '.*dhRecbto>(.*?)</dhRecbto.*', '$1'
            } else {
                "N/A"
            }
            
            return @{
                CStat = $cStat
                Motivo = $motivo
                DhRecbto = $recebimento
            }
        }
    }
    
    return $null
}

function Show-Status {
    Clear-Host
    
    $agora = Get-Date
    
    Write-Host "`n╔═══════════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║          MONITOR DE TESTES NFCe - SEFAZ HOMOLOGAÇÃO             ║" -ForegroundColor Cyan
    Write-Host "╚═══════════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host "⏰ Timestamp: $($agora.ToString('yyyy-MM-dd HH:mm:ss'))`n" -ForegroundColor Gray
    
    # Status do Backend
    $backend = Get-BackendStatus
    Write-Host "🖥️  BACKEND STATUS" -ForegroundColor Yellow
    Write-Host "   Estado: " -NoNewline
    Write-Host $backend.Status -ForegroundColor $backend.Cor
    if ($backend.PID) {
        Write-Host "   PID: $($backend.PID)" -ForegroundColor Gray
        Write-Host "   Porta: 8080" -ForegroundColor Gray
    }
    
    Write-Host ""
    
    # Último XML gerado
    $xmlInfo = Get-UltimoXMLInfo
    if ($xmlInfo) {
        Write-Host "📄 ÚLTIMO XML GERADO" -ForegroundColor Yellow
        Write-Host "   NFCe #$($xmlInfo.NumeroNFe)" -ForegroundColor White
        Write-Host "   Arquivo: $($xmlInfo.Arquivo)" -ForegroundColor Gray
        Write-Host "   Gerado em: $($xmlInfo.DataGeracao.ToString('yyyy-MM-dd HH:mm:ss'))" -ForegroundColor Gray
        Write-Host "   dhEmi: $($xmlInfo.DhEmi)" -ForegroundColor Gray
        Write-Host "   Total: $($xmlInfo.Total)" -ForegroundColor Green
        Write-Host "   Tamanho: $($xmlInfo.TamanhoKB) KB" -ForegroundColor Gray
        
        $tempoDecorrido = $agora - $xmlInfo.DataGeracao
        $minutos = [math]::Floor($tempoDecorrido.TotalMinutes)
        $segundos = $tempoDecorrido.Seconds
        
        Write-Host "   ⏱️  Tempo decorrido: " -NoNewline -ForegroundColor Gray
        
        if ($minutos -lt 5) {
            Write-Host "$minutos min $segundos seg " -NoNewline -ForegroundColor Red
            Write-Host "(⚠️  Cache SEFAZ provável)" -ForegroundColor Yellow
        }
        elseif ($minutos -lt 10) {
            Write-Host "$minutos min $segundos seg " -NoNewline -ForegroundColor Yellow
            Write-Host "(⏳ Aguardando expiração do cache)" -ForegroundColor Cyan
        }
        else {
            Write-Host "$minutos min $segundos seg " -NoNewline -ForegroundColor Green
            Write-Host "(✅ Cache provavelmente expirado)" -ForegroundColor Green
        }
    }
    else {
        Write-Host "📄 ÚLTIMO XML GERADO" -ForegroundColor Yellow
        Write-Host "   Nenhum XML encontrado" -ForegroundColor Red
    }
    
    Write-Host ""
    
    # Última resposta SEFAZ
    $logInfo = Get-UltimoLog
    if ($logInfo) {
        Write-Host "📡 ÚLTIMA RESPOSTA SEFAZ" -ForegroundColor Yellow
        
        $cStatCor = if ($logInfo.CStat -eq "100") { "Green" } 
                    elseif ($logInfo.CStat -eq "225") { "Red" } 
                    else { "Yellow" }
        
        Write-Host "   cStat: " -NoNewline
        Write-Host $logInfo.CStat -ForegroundColor $cStatCor -NoNewline
        Write-Host " - $($logInfo.Motivo)" -ForegroundColor Gray
        Write-Host "   dhRecbto: $($logInfo.DhRecbto)" -ForegroundColor Gray
        
        # Detecta cache comparando dhRecbto com dhEmi
        if ($xmlInfo -and $logInfo.DhRecbto -ne "N/A") {
            try {
                $recebimentoDate = [DateTime]::Parse($logInfo.DhRecbto.Replace("-03:00", ""))
                $emissaoDate = if ($xmlInfo.DhEmi -match '\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}') {
                    [DateTime]::Parse($xmlInfo.DhEmi.Replace("-03:00", ""))
                } else {
                    $xmlInfo.DataGeracao
                }
                
                $diferencaMinutos = ($emissaoDate - $recebimentoDate).TotalMinutes
                
                Write-Host "   ⚠️  Diferença dhEmi ↔ dhRecbto: " -NoNewline -ForegroundColor Gray
                
                if ($diferencaMinutos -gt 2) {
                    Write-Host "$([math]::Round($diferencaMinutos, 1)) min " -NoNewline -ForegroundColor Red
                    Write-Host "⚠️  CACHE DETECTADO!" -ForegroundColor Red
                }
                elseif ($diferencaMinutos -gt 0.5) {
                    Write-Host "$([math]::Round($diferencaMinutos, 1)) min " -NoNewline -ForegroundColor Yellow
                    Write-Host "⚠️  Possível cache" -ForegroundColor Yellow
                }
                else {
                    Write-Host "$([math]::Round($diferencaMinutos, 1)) min " -NoNewline -ForegroundColor Green
                    Write-Host "✅ Validação fresca" -ForegroundColor Green
                }
            }
            catch {
                # Ignora erros de parse de data
            }
        }
    }
    else {
        Write-Host "📡 ÚLTIMA RESPOSTA SEFAZ" -ForegroundColor Yellow
        Write-Host "   Nenhuma resposta encontrada nos logs" -ForegroundColor Gray
    }
    
    Write-Host ""
    Write-Host "─────────────────────────────────────────────────────────────────────" -ForegroundColor DarkGray
    
    # Recomendações
    if ($xmlInfo) {
        $tempoDecorrido = $agora - $xmlInfo.DataGeracao
        
        if ($tempoDecorrido.TotalMinutes -lt 5) {
            Write-Host "💡 RECOMENDAÇÃO: " -NoNewline -ForegroundColor Cyan
            Write-Host "Aguarde mais $([math]::Ceiling(5 - $tempoDecorrido.TotalMinutes)) min para evitar cache" -ForegroundColor Yellow
        }
        elseif ($tempoDecorrido.TotalMinutes -ge 10) {
            Write-Host "✅ PRONTO PARA TESTAR: " -NoNewline -ForegroundColor Green
            Write-Host "Cache provavelmente expirado. Pode criar uma nova comanda!" -ForegroundColor Cyan
        }
        else {
            Write-Host "⏳ AGUARDANDO: " -NoNewline -ForegroundColor Yellow
            Write-Host "Mais $([math]::Ceiling(10 - $tempoDecorrido.TotalMinutes)) min recomendado" -ForegroundColor Cyan
        }
    }
    
    Write-Host ""
    
    if ($Continuo) {
        Write-Host "⟳ Atualizando em $IntervaloSegundos segundos... (Ctrl+C para sair)" -ForegroundColor DarkGray
    }
}

# Execução
if ($Continuo) {
    while ($true) {
        Show-Status
        Start-Sleep -Seconds $IntervaloSegundos
    }
}
else {
    Show-Status
    Write-Host ""
    Write-Host "💡 Dica: Execute com " -NoNewline -ForegroundColor Gray
    Write-Host "-Continuo" -NoNewline -ForegroundColor Cyan
    Write-Host " para monitoramento em tempo real" -ForegroundColor Gray
    Write-Host "   Exemplo: " -NoNewline -ForegroundColor Gray
    Write-Host ".\scripts\monitorar_testes_nfce.ps1 -Continuo -IntervaloSegundos 30" -ForegroundColor White
    Write-Host ""
}
