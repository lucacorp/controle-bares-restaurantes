# Script para validar XML da NFCe contra o schema XSD oficial
# Autor: GitHub Copilot
# Data: 2025-12-11

param(
    [Parameter(Mandatory=$false)]
    [string]$XmlPath = "C:\controle-bares-restaurantes\Backend\data\nfe\xml\NFe_35251261134978000130650010000001341186903786.xml",
    
    [Parameter(Mandatory=$false)]
    [string]$SchemaPath = "C:\controle-bares-restaurantes\Backend\scripts\schemas"
)

Write-Host "`n=== VALIDADOR DE XML NFCe 4.0 ===" -ForegroundColor Cyan
Write-Host "XML: $XmlPath" -ForegroundColor Gray
Write-Host "Schemas: $SchemaPath`n" -ForegroundColor Gray

# Verifica se o arquivo XML existe
if (-not (Test-Path $XmlPath)) {
    Write-Host "❌ ERRO: Arquivo XML não encontrado: $XmlPath" -ForegroundColor Red
    exit 1
}

# Cria diretório de schemas se não existir
if (-not (Test-Path $SchemaPath)) {
    Write-Host "📁 Criando diretório de schemas..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $SchemaPath -Force | Out-Null
}

# Lista de schemas necessários para NFCe 4.0
$schemasNecessarios = @{
    "nfe_v4.00.xsd" = "http://www.portalfiscal.inf.br/nfe/xsd/nfe_v4.00.xsd"
    "xmldsig-core-schema_v1.01.xsd" = "http://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd"
    "tiposBasico_v4.00.xsd" = "http://www.portalfiscal.inf.br/nfe/xsd/tiposBasico_v4.00.xsd"
}

# Baixa schemas se não existirem
Write-Host "📥 Verificando schemas..." -ForegroundColor Cyan

$schemasPrincipais = @(
    @{
        Nome = "nfe_v4.00.xsd"
        Url = "https://raw.githubusercontent.com/nfe/nfe/master/wsdl/4.00/schema/nfe_v4.00.xsd"
        Alternativa = "http://www.portalfiscal.inf.br/nfe/xsd/nfe_v4.00.xsd"
    },
    @{
        Nome = "tiposBasico_v4.00.xsd"
        Url = "https://raw.githubusercontent.com/nfe/nfe/master/wsdl/4.00/schema/tiposBasico_v4.00.xsd"
        Alternativa = "http://www.portalfiscal.inf.br/nfe/xsd/tiposBasico_v4.00.xsd"
    },
    @{
        Nome = "xmldsig-core-schema_v1.01.xsd"
        Url = "https://www.w3.org/TR/2002/REC-xmldsig-core-20020212/xmldsig-core-schema.xsd"
        Alternativa = $null
    }
)

foreach ($schema in $schemasPrincipais) {
    $arquivoLocal = Join-Path $SchemaPath $schema.Nome
    
    if (-not (Test-Path $arquivoLocal)) {
        Write-Host "  ⬇️  Baixando $($schema.Nome)..." -ForegroundColor Yellow
        
        try {
            Invoke-WebRequest -Uri $schema.Url -OutFile $arquivoLocal -ErrorAction Stop
            Write-Host "  ✅ $($schema.Nome) baixado com sucesso" -ForegroundColor Green
        }
        catch {
            if ($schema.Alternativa) {
                Write-Host "  ⚠️  Tentando URL alternativa..." -ForegroundColor Yellow
                try {
                    Invoke-WebRequest -Uri $schema.Alternativa -OutFile $arquivoLocal -ErrorAction Stop
                    Write-Host "  ✅ $($schema.Nome) baixado (alternativa)" -ForegroundColor Green
                }
                catch {
                    Write-Host "  ❌ ERRO ao baixar $($schema.Nome): $_" -ForegroundColor Red
                }
            }
            else {
                Write-Host "  ❌ ERRO ao baixar $($schema.Nome): $_" -ForegroundColor Red
            }
        }
    }
    else {
        Write-Host "  ✅ $($schema.Nome) já existe" -ForegroundColor Green
    }
}

Write-Host "`n🔍 Validando XML contra schema XSD..." -ForegroundColor Cyan

# Cria validador XML
$settings = New-Object System.Xml.XmlReaderSettings
$settings.ValidationType = [System.Xml.ValidationType]::Schema
$settings.ValidationFlags = [System.Xml.Schema.XmlSchemaValidationFlags]::ProcessInlineSchema `
    -bor [System.Xml.Schema.XmlSchemaValidationFlags]::ProcessSchemaLocation `
    -bor [System.Xml.Schema.XmlSchemaValidationFlags]::ReportValidationWarnings

# Adiciona schemas
$schemaSet = New-Object System.Xml.Schema.XmlSchemaSet

$schemaFiles = Get-ChildItem -Path $SchemaPath -Filter "*.xsd"
foreach ($schemaFile in $schemaFiles) {
    try {
        Write-Host "  📄 Carregando schema: $($schemaFile.Name)" -ForegroundColor Gray
        $schemaSet.Add($null, $schemaFile.FullName) | Out-Null
    }
    catch {
        Write-Host "  ⚠️  Aviso ao carregar $($schemaFile.Name): $_" -ForegroundColor Yellow
    }
}

$schemaSet.Compile()
$settings.Schemas = $schemaSet

# Lista para armazenar erros
$erros = @()
$avisos = @()

# Event handler para erros de validação
$validationEventHandler = {
    param($sender, $e)
    
    if ($e.Severity -eq [System.Xml.Schema.XmlSeverityType]::Error) {
        $script:erros += $e.Message
    }
    else {
        $script:avisos += $e.Message
    }
}

$settings.add_ValidationEventHandler($validationEventHandler)

# Valida o XML
try {
    Write-Host "`n📋 Lendo e validando XML..." -ForegroundColor Cyan
    
    $reader = [System.Xml.XmlReader]::Create($XmlPath, $settings)
    
    while ($reader.Read()) {
        # Lê todo o documento para disparar a validação
    }
    
    $reader.Close()
    
    Write-Host "`n" + ("="*70) -ForegroundColor Cyan
    Write-Host "RESULTADO DA VALIDAÇÃO" -ForegroundColor Cyan
    Write-Host ("="*70) -ForegroundColor Cyan
    
    if ($erros.Count -eq 0) {
        Write-Host "✅ XML VÁLIDO - Nenhum erro encontrado!" -ForegroundColor Green
        
        if ($avisos.Count -gt 0) {
            Write-Host "`n⚠️  Avisos encontrados ($($avisos.Count)):" -ForegroundColor Yellow
            for ($i = 0; $i -lt $avisos.Count; $i++) {
                Write-Host "  $($i+1). $($avisos[$i])" -ForegroundColor Yellow
            }
        }
    }
    else {
        Write-Host "❌ XML INVÁLIDO - $($erros.Count) erro(s) encontrado(s):" -ForegroundColor Red
        Write-Host ""
        
        for ($i = 0; $i -lt $erros.Count; $i++) {
            Write-Host "ERRO $($i+1):" -ForegroundColor Red
            Write-Host "$($erros[$i])" -ForegroundColor Yellow
            Write-Host ""
        }
        
        if ($avisos.Count -gt 0) {
            Write-Host "`n⚠️  Avisos encontrados ($($avisos.Count)):" -ForegroundColor Yellow
            for ($i = 0; $i -lt $avisos.Count; $i++) {
                Write-Host "  $($i+1). $($avisos[$i])" -ForegroundColor Yellow
            }
        }
        
        Write-Host "`n💡 DICA: Os erros acima mostram exatamente qual elemento/atributo está" -ForegroundColor Cyan
        Write-Host "causando a rejeição 225 da SEFAZ. Corrija-os em NfeXmlBuilder.java" -ForegroundColor Cyan
        
        exit 1
    }
    
    Write-Host "`n" + ("="*70) -ForegroundColor Cyan
    
    if ($erros.Count -eq 0) {
        Write-Host "`n✅ CONCLUSÃO: O XML está correto segundo o schema NFCe 4.0" -ForegroundColor Green
        Write-Host "Se a SEFAZ ainda retorna erro 225, aguarde a expiração do cache (5+ min)" -ForegroundColor Cyan
        exit 0
    }
}
catch {
    Write-Host "`n❌ ERRO CRÍTICO ao validar XML:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Yellow
    Write-Host "`nStack trace:" -ForegroundColor Gray
    Write-Host $_.Exception.StackTrace -ForegroundColor DarkGray
    exit 1
}
finally {
    if ($reader) {
        $reader.Close()
    }
}
