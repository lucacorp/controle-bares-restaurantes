# Script para testar XML diretamente na SEFAZ com análise de erro detalhada

Write-Host "`n=== TESTE DIRETO SEFAZ ===" -ForegroundColor Cyan

# Pegar o XML mais recente
$xmlPath = "C:\controle-bares-restaurantes\Backend\data\nfe\xml"
$ultimoXML = Get-ChildItem -Path $xmlPath -Filter "NFe_*.xml" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $ultimoXML) {
    Write-Host "Nenhum XML encontrado!" -ForegroundColor Red
    exit
}

Write-Host "XML: $($ultimoXML.Name)" -ForegroundColor White
Write-Host "Gerado: $($ultimoXML.LastWriteTime)`n" -ForegroundColor Gray

# Ler conteúdo do XML
$xmlContent = Get-Content $ultimoXML.FullName -Raw -Encoding UTF8

# Verificar elementos críticos
Write-Host "=== VERIFICACAO RAPIDA ===" -ForegroundColor Yellow

if ($xmlContent -match '<infCpl>([^<]+)</infCpl>') {
    Write-Host "infCpl: $($matches[1])" -ForegroundColor Green
} else {
    Write-Host "infCpl: NAO ENCONTRADO!" -ForegroundColor Red
}

if ($xmlContent -match '<dhEmi>([^<]+)</dhEmi>') {
    Write-Host "dhEmi: $($matches[1])" -ForegroundColor White
}

if ($xmlContent -match '<transp><modFrete>([^<]+)</modFrete></transp>') {
    Write-Host "modFrete: $($matches[1])" -ForegroundColor Green
} else {
    Write-Host "modFrete: NAO ENCONTRADO!" -ForegroundColor Red
}

if ($xmlContent -match '<PISOutr>') {
    Write-Host "PIS: PISOutr ✓" -ForegroundColor Green
} else {
    Write-Host "PIS: FORMATO ERRADO!" -ForegroundColor Red
}

if ($xmlContent -match '<COFINSOutr>') {
    Write-Host "COFINS: COFINSOutr ✓" -ForegroundColor Green
} else {
    Write-Host "COFINS: FORMATO ERRADO!" -ForegroundColor Red
}

if ($xmlContent -match '<indIEDest>([0-9])</indIEDest>') {
    Write-Host "indIEDest: $($matches[1]) ✓" -ForegroundColor Green
}

if ($xmlContent -match '<vTroco>([^<]+)</vTroco>') {
    Write-Host "vTroco: $($matches[1]) ✓" -ForegroundColor Green
}

# Mostrar quantidade de campos ICMSTot
$icmsTotMatches = ([regex]::Matches($xmlContent, '<v[A-Z][^>]*>[^<]+</v[A-Z][^>]*>')).Count
Write-Host "Campos ICMSTot: $icmsTotMatches`n" -ForegroundColor White

Write-Host "=== ANALISE POSSIVEL ===" -ForegroundColor Yellow
Write-Host "O erro 225 persiste mesmo com:" -ForegroundColor White
Write-Host "  - XML validado por XSD oficial" -ForegroundColor Gray
Write-Host "  - Timestamp unico no infCpl" -ForegroundColor Gray
Write-Host "  - Cache SEFAZ 2+ minutos atras" -ForegroundColor Gray
Write-Host ""
Write-Host "HIPOTESES:" -ForegroundColor Red
Write-Host "  1. Algo no <enviNFe> wrapper (idLote, indSinc)" -ForegroundColor Yellow
Write-Host "  2. Namespace incorreto" -ForegroundColor Yellow  
Write-Host "  3. Elemento fora de ordem (schema strict)" -ForegroundColor Yellow
Write-Host "  4. Valor invalido em campo especifico" -ForegroundColor Yellow
Write-Host ""

# Extrair e mostrar estrutura de enviNFe
if ($xmlContent -match '<enviNFe[^>]*>') {
    Write-Host "=== WRAPPER enviNFe ===" -ForegroundColor Cyan
    $xmlContent -match '<enviNFe xmlns="([^"]+)" versao="([^"]+)">.*?<idLote>([^<]+)</idLote>.*?<indSinc>([^<]+)</indSinc>' | Out-Null
    Write-Host "xmlns: $($matches[1])" -ForegroundColor White
    Write-Host "versao: $($matches[2])" -ForegroundColor White
    Write-Host "idLote: $($matches[3])" -ForegroundColor White
    Write-Host "indSinc: $($matches[4])`n" -ForegroundColor White
}

Write-Host "=== PROXIMOS PASSOS ===" -ForegroundColor Green
Write-Host "1. Validar sequencia EXATA dos elementos contra schema" -ForegroundColor White
Write-Host "2. Verificar se algum namespace esta missing" -ForegroundColor White
Write-Host "3. Testar com enviNFe minimal (sem campos opcionais)" -ForegroundColor White
Write-Host ""
