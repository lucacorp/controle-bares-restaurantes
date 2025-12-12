# Schemas XSD da SEFAZ - Instruções para Download

## ⚠️ Validação Local Desabilitada

A validação local do XML contra schema XSD está **desabilitada** porque os arquivos XSD não estão incluídos no projeto.

## 📥 Como Habilitar Validação Local

### 1. Baixar os Schemas XSD da SEFAZ

Acesse o site oficial da SEFAZ:
```
http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=/fmcTY5E5bzM=
```

Ou diretamente:
```
http://www.nfe.fazenda.gov.br/portal/exibirArquivo.aspx?conteudo=N/njKwlGl4k=
```

### 2. Extrair os Arquivos

Baixe o arquivo **Pacote de Liberação NF-e (PL_009_V4.0)** ou mais recente.

Extraia e localize o arquivo: `nfe_v4.00.xsd`

### 3. Copiar para o Projeto

Crie o diretório:
```
Backend/src/main/resources/schemas/
```

Copie o arquivo `nfe_v4.00.xsd` para esse diretório.

### 4. Recompilar o Projeto

```powershell
cd Backend
mvn clean package -DskipTests
```

### 5. Reiniciar o Backend

Após reiniciar, a validação local estará ativada automaticamente.

## 🔍 Como Usar a Validação

### Opção 1: Validação Automática ao Emitir NFCe

A validação ocorre automaticamente antes de enviar para SEFAZ.
Verifique os logs para mensagens:
- `✅ XML passou na validação local!`
- `❌ XML falhou na validação local!`

### Opção 2: Endpoint REST - Validar Último XML

```bash
curl http://localhost:8080/api/nfe/validar/ultimo
```

### Opção 3: Endpoint REST - Validar Arquivo Específico

```bash
curl "http://localhost:8080/api/nfe/validar?arquivo=data/nfe/xml/NFe_XXX.xml"
```

### Opção 4: POST - Validar XML Direto

```bash
curl -X POST http://localhost:8080/api/nfe/validar \
  -H "Content-Type: application/xml" \
  --data-binary @arquivo.xml
```

## 📋 Arquivos Necessários

Os schemas XSD da NF-e 4.0 incluem múltiplos arquivos interdependentes:

- `nfe_v4.00.xsd` (principal)
- `xmldsig-core-schema_v1.01.xsd`
- `tiposBasico_v4.00.xsd`
- `eventoModalFiscal_v1.00.xsd`
- Entre outros...

**Copie TODOS os arquivos .xsd** para `src/main/resources/schemas/` para garantir que todas as dependências sejam resolvidas.

## ✅ Benefícios da Validação Local

1. **Feedback Imediato**: Detecta erros antes de enviar para SEFAZ
2. **Economia de Tempo**: Evita espera de resposta da SEFAZ para erros óbvios
3. **Desenvolvimento**: Facilita testes e desenvolvimento
4. **Diagnóstico**: Mensagens detalhadas de erro com linha e coluna

## 🎯 Status Atual

**SEM** schemas XSD = Validação local **desabilitada**
**COM** schemas XSD = Validação local **automática**

---

**Nota**: A validação local é **opcional**. O sistema funciona normalmente sem ela, mas é altamente recomendado para desenvolvimento e homologação.
