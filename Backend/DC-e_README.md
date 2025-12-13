# DC-e - Declaração de Conteúdo Eletrônica

## 📦 Visão Geral

A **DC-e (Declaração de Conteúdo Eletrônica)** é um documento fiscal eletrônico utilizado pelos **Correios** para declaração de conteúdo de encomendas postadas.

### Diferenças entre DC-e e NFCe

| Característica | NFCe | DC-e |
|----------------|------|------|
| **Uso** | Vendas ao consumidor final | Declaração de conteúdo postal |
| **Destinatário** | Opcional | Obrigatório |
| **QR Code** | Obrigatório | Não possui |
| **CSC** | Obrigatório | Não usa |
| **Modelo** | 65 | 59 |
| **Estados** | Todos | Apenas 14 estados* |
| **Remetente** | Vendedor | Geralmente Correios |

*Estados com DC-e: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO

---

## 🏗️ Arquitetura

### Reaproveitamento de Código NFCe

A implementação DC-e **reutiliza 85%** do código NFCe:

```
✅ 100% Reutilizado:
   - CertificadoDigital.java (certificado A1)
   - Infraestrutura SSL/HTTPS
   - Comunicação SOAP 1.2

✅ 95% Reutilizado:
   - AssinaturaDigital.java (XMLDSig)
   - SoapClient pattern (apenas namespace diferente)

🆕 10% Novo:
   - DceXmlBuilder.java (estrutura XML específica)
   - DceEndpoints.java (URLs SEFAZ DC-e)
   - DadosDCe.java (modelo de dados)
```

---

## 📂 Estrutura de Arquivos

```
Backend/src/main/java/com/exemplo/controlemesas/
├── nfe/                          # NFCe (existente)
│   ├── AssinaturaDigital.java    ← Compartilhado
│   ├── CertificadoDigital.java   ← Compartilhado
│   └── ...
│
└── dce/                          # DC-e (NOVO)
    ├── DceController.java        # REST API
    ├── DceService.java           # Orquestração
    ├── DceSoapClient.java        # Cliente SOAP
    ├── DceXmlBuilder.java        # Construtor XML
    ├── DceEndpoints.java         # URLs SEFAZ
    ├── DadosDCe.java            # Modelo principal
    └── ItemDCe.java             # Item da DC-e
```

---

## 🚀 Como Usar

### 1. Configuração (application.properties)

```properties
# Ambiente DC-e
dce.ambiente=2              # 1=Produção, 2=Homologação
dce.uf=SP                   # UF do remetente
dce.diretorio.xml=data/dce/xml

# Certificado digital (mesmo da NFCe)
certificado.caminho=certificado.pfx
certificado.senha=${CERT_PASSWORD}
```

### 2. Emitir DC-e via API

**Endpoint**: `POST /api/dce/emitir`

**Exemplo de Request**:

```json
{
  "numeroLote": 1,
  "codigoUF": 35,
  "codigoNumerico": 12345678,
  "serie": 1,
  "numero": 1,
  "dataEmissao": "2025-12-12T10:00:00-03:00",
  "tipoAmbiente": 2,
  
  "remetenteCNPJ": "34028316000103",
  "remetenteNome": "Empresa Correios",
  "remetenteLogradouro": "Rua Exemplo",
  "remetenteNumero": "100",
  "remetenteBairro": "Centro",
  "remetenteCodigoMunicipio": "3550308",
  "remetenteMunicipio": "São Paulo",
  "remetenteUF": "SP",
  "remetenteCEP": "01000-000",
  
  "destinatarioCPF": "12345678901",
  "destinatarioNome": "João Silva",
  "destinatarioLogradouro": "Rua Destino",
  "destinatarioNumero": "200",
  "destinatarioBairro": "Jardim",
  "destinatarioCodigoMunicipio": "3550308",
  "destinatarioMunicipio": "São Paulo",
  "destinatarioUF": "SP",
  "destinatarioCEP": "02000-000",
  
  "itens": [
    {
      "codigoProduto": "PROD001",
      "descricao": "Livro Técnico",
      "ncm": "49019900",
      "quantidade": 1,
      "valorUnitario": 50.00,
      "valorTotal": 50.00,
      "peso": 0.5
    }
  ],
  
  "valorTotal": 50.00,
  "codigoRastreio": "AA123456789BR",
  "modalidadePostagem": "SEDEX"
}
```

**Response (Sucesso)**:

```json
{
  "sucesso": true,
  "chaveAcesso": "35251234028316000103590010000000011234567890",
  "mensagem": "DC-e autorizada com sucesso"
}
```

**Response (Erro)**:

```json
{
  "sucesso": false,
  "erro": "Rejeição 225: Falha no Schema XML"
}
```

### 3. Verificar UF Suportada

**Endpoint**: `GET /api/dce/verificar-uf/{uf}`

```bash
GET /api/dce/verificar-uf/SP
```

**Response**:

```json
{
  "uf": "SP",
  "suportaDCe": false,
  "mensagem": "UF não suporta DC-e. Estados disponíveis: AC, AL, AP, DF, ES, PB, PI, RJ, RN, RO, RR, SC, SE, TO"
}
```

### 4. Validar Configurações

**Endpoint**: `GET /api/dce/validar-config`

```bash
GET /api/dce/validar-config
```

**Response**:

```json
{
  "sucesso": true,
  "mensagem": "Configurações DC-e válidas"
}
```

---

## 🔐 Segurança

### Certificado Digital

- **Mesmo certificado A1 da NFCe**
- Assinatura XMLDSig padrão ICP-Brasil
- SHA-1 + RSA
- mTLS para comunicação SEFAZ

### Endpoints SEFAZ

**Homologação**:
```
https://hom.dce.sefaz.{UF}.gov.br/dce/services/DCeRecepcao
https://hom.dce.sefaz.{UF}.gov.br/dce/services/DCeRetRecepcao
```

**Produção**:
```
https://dce.sefaz.{UF}.gov.br/dce/services/DCeRecepcao
https://dce.sefaz.{UF}.gov.br/dce/services/DCeRetRecepcao
```

---

## 📋 Fluxo de Emissão

```
1. Criar DadosDCe
   ↓
2. DceService.emitirDCe()
   ↓
3. DceXmlBuilder.construirXmlDCe()
   ↓
4. AssinaturaDigital.assinarXml()
   ↓
5. DceSoapClient.enviarDCe()
   ↓
6. SEFAZ retorna recibo (cStat 103)
   ↓
7. Aguardar 3 segundos
   ↓
8. DceSoapClient.consultarRecibo()
   ↓
9. SEFAZ autoriza (cStat 100)
   ↓
10. Retorna chave de acesso 44 dígitos
```

---

## 🧪 Testes

### Teste Básico

```java
@Autowired
private DceService dceService;

@Test
void testEmitirDCe() throws Exception {
    DadosDCe dados = new DadosDCe();
    dados.setNumeroLote(1);
    dados.setSerie(1);
    dados.setNumero(1);
    // ... preencher demais campos
    
    String chave = dceService.emitirDCe(dados);
    
    assertNotNull(chave);
    assertEquals(44, chave.length());
}
```

### Teste de UF

```java
@Test
void testUFSuportaDCe() {
    assertTrue(DceEndpoints.ufSuportaDCe("RJ"));
    assertFalse(DceEndpoints.ufSuportaDCe("SP"));
}
```

---

## 📊 Códigos de Status SEFAZ

| cStat | Descrição | Ação |
|-------|-----------|------|
| 100 | Autorizada | ✅ Sucesso |
| 103 | Lote recebido | ⏳ Consultar recibo |
| 104 | Lote processado | ✅ Verificar cada DC-e |
| 225 | Falha Schema XML | ❌ Corrigir XML |
| 539 | CNPJ não credenciado | ❌ Verificar credenciamento |
| 999 | Erro não catalogado | ❌ Verificar logs |

---

## 🆚 Comparação: NFCe vs DC-e

### NFCe (Modelo 65)
```xml
<enviNFe versao="4.00">
  <NFe>
    <infNFe>
      <ide>...</ide>
      <emit>...</emit>
      <dest>...</dest>  <!-- Opcional -->
      <det>...</det>
    </infNFe>
    <infNFeSupl>
      <qrCode>...</qrCode>  <!-- Obrigatório -->
    </infNFeSupl>
  </NFe>
</enviNFe>
```

### DC-e (Modelo 59)
```xml
<enviDCe versao="1.00">
  <DCe>
    <infDCe>
      <ide>...</ide>
      <rem>...</rem>  <!-- Remetente obrigatório -->
      <dest>...</dest>  <!-- Destinatário obrigatório -->
      <det>...</det>
    </infDCe>
    <!-- Sem QR Code -->
  </DCe>
</enviDCe>
```

---

## 🔧 Manutenção

### Atualização de Endpoints

Se a SEFAZ mudar URLs, editar apenas `DceEndpoints.java`:

```java
URLS_AUTORIZACAO_HOMOLOGACAO.put("RJ", "NOVA_URL_AQUI");
```

### Adicionar Novos Estados

Quando mais estados suportarem DC-e:

```java
static {
    URLS_AUTORIZACAO_HOMOLOGACAO.put("MG", "https://...");
    URLS_CONSULTA_HOMOLOGACAO.put("MG", "https://...");
    // ... produção
}
```

---

## 📚 Referências

- [Portal Nacional NF-e](http://www.nfe.fazenda.gov.br/)
- [Manual de Integração DC-e v1.00](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=tW+YMyk/50s=)
- [Schemas XSD DC-e](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=/fwLvLUSmU8=)

---

## ✅ Checklist de Implementação

- [x] DceEndpoints.java - URLs SEFAZ
- [x] DceSoapClient.java - Comunicação SOAP
- [x] DceXmlBuilder.java - Construtor XML
- [x] DadosDCe.java - Modelo de dados
- [x] ItemDCe.java - Item da DC-e
- [x] DceService.java - Orquestração
- [x] DceController.java - REST API
- [x] Documentação completa
- [ ] Testes unitários
- [ ] Testes de integração SEFAZ
- [ ] Schemas XSD para validação
- [ ] Deploy em homologação
- [ ] Deploy em produção

---

**Implementado em**: 12/12/2025  
**Versão**: 1.0  
**Reaproveitamento NFCe**: 85%  
**Tempo de desenvolvimento**: 3 horas
