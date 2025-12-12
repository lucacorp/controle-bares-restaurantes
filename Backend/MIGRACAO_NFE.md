# 🚀 Migração: ACBrMonitor → Comunicação Direta SEFAZ

## ✅ O que foi feito

A comunicação com a SEFAZ foi migrada de **ACBrMonitor** (socket TCP) para **comunicação direta via HTTPS**.

### Componentes criados:

1. **`CertificadoDigital.java`** - Gerencia certificado A1 (.pfx/.p12)
2. **`AssinaturaDigital.java`** - Assina XML conforme padrão NFe
3. **`SefazSoapClient.java`** - Cliente SOAP para webservices SEFAZ
4. **`SefazEndpoints.java`** - URLs dos webservices por UF
5. **`CertificadoInitializer.java`** - Carrega certificado na inicialização
6. **`NfeService.java`** - Refatorado para comunicação direta

### Dependências adicionadas:

```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcpkix-jdk15on</artifactId>
</dependency>
```

---

## 🔧 Configuração necessária

### 1. Adicione seu certificado digital

Edite `application-dev.properties`:

```properties
# Certificado Digital A1 (PFX/P12)
nfe.certificado.caminho=C:/certificados/certificado.pfx
nfe.certificado.senha=SUA_SENHA_AQUI

# Ambiente SEFAZ
nfe.homologacao=true

# Dados da Empresa
empresa.cnpj=00000000000000
empresa.razaoSocial=Sua Empresa LTDA
empresa.nomeFantasia=Nome Fantasia
empresa.ie=123456789
empresa.uf=SP
```

### 2. Obtenha um certificado

- **Homologação**: Use certificado de teste da SEFAZ
- **Produção**: Certificado A1 válido emitido por AC credenciada

---

## 🎯 Como funciona agora

### Fluxo de emissão:

```
1. Gera XML da NF-e
2. Assina digitalmente com certificado A1
3. Monta lote e envia para SEFAZ via HTTPS
4. Aguarda resposta (síncrono ou consulta recibo)
5. Valida código de status (100 = autorizado)
6. Gera DANFE e salva PDF
7. Persiste no banco
```

### Vantagens vs ACBr:

✅ **Sem dependências externas** - Não precisa do ACBrMonitor rodando  
✅ **Controle total** - Você sabe exatamente o que está sendo enviado  
✅ **Respostas padronizadas** - XML bem definido pela SEFAZ  
✅ **Melhor debugging** - Logs claros e estruturados  
✅ **Performance** - Sem overhead de socket local  
✅ **Portabilidade** - Funciona em qualquer ambiente Java  

---

## 🧪 Testando

### 1. Compile o projeto:

```bash
cd Backend
mvn clean install
```

### 2. Execute a aplicação:

```bash
mvn spring-boot:run
```

### 3. Verifique os logs:

```
✅ Certificado digital carregado com sucesso!
Titular: CN=EMPRESA EXEMPLO...
Válido até: 2026-12-31
```

### 4. Emita uma NF-e:

```bash
curl -X POST http://localhost:8080/api/resumos/1/emitir
```

---

## 📋 Checklist de migração

- [x] Dependências adicionadas ao pom.xml
- [x] Classes de certificado e assinatura criadas
- [x] Cliente SOAP para SEFAZ criado
- [x] NfeService refatorado
- [x] Configurações adicionadas
- [x] Inicializador de certificado criado
- [ ] Certificado A1 configurado
- [ ] Dados da empresa atualizados
- [ ] Teste em homologação realizado
- [ ] SatService também precisa ser migrado (opcional)

---

## 🔍 Troubleshooting

### "Certificado digital não foi carregado"
- Verifique o caminho do arquivo .pfx/.p12
- Confirme a senha do certificado
- Certifique-se que o certificado está válido

### "Erro ao comunicar com SEFAZ"
- Verifique sua conexão com a internet
- Confirme que a URL está correta para sua UF
- Verifique se está usando homologação/produção correto

### "Assinatura inválida"
- Certificado pode estar expirado
- Senha incorreta
- Arquivo corrompido

---

## 📚 Referências

- [Manual de Integração NF-e](http://www.nfe.fazenda.gov.br/portal/principal.aspx)
- [Schemas XML da NF-e](http://www.nfe.fazenda.gov.br/portal/listaConteudo.aspx?tipoConteudo=BMPFMBoln3w=)
- [Ambiente Nacional de Homologação](https://hom.nfe.fazenda.gov.br/)

---

## ⚠️ Importante

- **Nunca commite** a senha do certificado no Git
- Use **variáveis de ambiente** em produção
- Mantenha o certificado em local seguro
- Renove o certificado antes do vencimento
- Teste SEMPRE em homologação primeiro

---

## 🗑️ Código removido

As seguintes funcionalidades do ACBr foram removidas:

- ❌ `enviarComando()` - Socket TCP para ACBrMonitor
- ❌ Parsing de respostas proprietárias `OK:|ERRO:`
- ❌ Timeouts complexos e retry logic
- ❌ Encoding ISO-8859-1 específico
- ❌ Terminador `\r\n.\r\n`
- ❌ Script PowerShell `acbr_raw_test.ps1` (obsoleto)

---

**Autor**: GitHub Copilot  
**Data**: 26/11/2025  
**Versão**: 1.0
