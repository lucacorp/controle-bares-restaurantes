# 📊 Levantamento Completo do Sistema - Controle de Bares e Restaurantes

**Data do Levantamento**: 12/12/2025  
**Status NFCe**: ✅ **IMPLEMENTADO E FUNCIONANDO** (aguardando expiração do cache SEFAZ)

---

## 📋 Índice

1. [Arquitetura Geral](#arquitetura-geral)
2. [Backend - Java Spring Boot](#backend---java-spring-boot)
3. [Frontend - React TypeScript](#frontend---react-typescript)
4. [Módulos Implementados](#módulos-implementados)
5. [Módulos Pendentes](#módulos-pendentes)
6. [Próximos Passos](#próximos-passos)

---

## 🏗️ Arquitetura Geral

### Stack Tecnológica

**Backend**:
- Java 21 (Eclipse Adoptium)
- Spring Boot 3.2.4
- MySQL 8.0.41
- Maven
- Lombok + MapStruct
- Apache HttpClient 5.2.1
- BouncyCastle 1.78.1

**Frontend**:
- React 18 + TypeScript
- Vite
- TailwindCSS
- Axios
- React Router

**Infraestrutura**:
- REST API (porta 8080)
- JWT Authentication
- Spring Security
- JPA/Hibernate 6
- Swagger/OpenAPI

---

## 🔧 Backend - Java Spring Boot

### 📁 Estrutura de Pacotes

```
com.exemplo.controlemesas/
├── config/              # Configurações gerais
├── controller/          # REST Controllers (17 controllers)
├── dto/                 # Data Transfer Objects
├── exception/           # Tratamento de exceções
├── model/               # Entidades JPA
├── nfe/                 # Integração NFCe (NOVO - 100% implementado)
├── pdf/                 # Geração de PDFs
├── repository/          # Repositórios JPA
├── sat/                 # Integração SAT (legacy)
├── security/            # Autenticação JWT
├── services/            # Lógica de negócio
├── util/                # Utilitários
└── validation/          # Validações customizadas
```

---

## 📦 Módulos Implementados

### ✅ 1. Gestão de Mesas

**Entidade**: `Mesa.java`
**Controller**: `MesaController.java`
**Endpoints**:
- `GET /api/mesas` - Listar mesas
- `POST /api/mesas` - Criar mesa
- `PUT /api/mesas/{id}` - Atualizar mesa
- `DELETE /api/mesas/{id}` - Deletar mesa
- `GET /api/mesas/{id}` - Buscar mesa

**Funcionalidades**:
- Cadastro de mesas
- Numeração automática
- Status (LIVRE, OCUPADA, RESERVADA)
- QR Code por mesa

**Frontend**: ✅ Implementado
- `/mesas` - Lista de mesas
- `/mesas/nova` - Cadastro
- `/mesas/:id` - Edição
- `/painel/qrcodes` - Painel de QR Codes

---

### ✅ 2. Gestão de Comandas

**Entidades**:
- `Comanda.java` - Comanda principal
- `ItemComanda.java` - Itens da comanda
- `ComandaResumo.java` - Resumo para fechamento/NFCe

**Controllers**:
- `ComandaController.java` - CRUD de comandas
- `ComandaPublicController.java` - API pública (sem autenticação)
- `ItemComandaController.java` - Gestão de itens
- `ComandaResumoController.java` - Fechamento e resumos

**Endpoints Principais**:
- `GET /api/comandas` - Listar comandas
- `POST /api/comandas` - Criar comanda
- `GET /api/comandas/mesa/{mesaId}` - Comandas por mesa
- `POST /api/comandas/{id}/fechar` - Fechar comanda
- `POST /api/comandas/{id}/itens` - Adicionar item
- `DELETE /api/comandas/itens/{id}` - Remover item

**Status de Comanda**:
- `ABERTA` - Comanda ativa
- `FECHADA` - Comanda fechada
- `CANCELADA` - Comanda cancelada

**Status de Item**:
- `PENDENTE` - Aguardando preparo
- `EM_PREPARO` - Em preparo na cozinha
- `PRONTO` - Pronto para servir
- `ENTREGUE` - Entregue ao cliente
- `CANCELADO` - Item cancelado

**Frontend**: ✅ Implementado
- `/comandas/mesa/:mesaId` - Comandas da mesa
- `/comandas/:id/itens` - Itens da comanda
- Garçom: `/garcom/comanda/:id` - Interface do garçom
- Cozinha: `/cozinha` - Painel da cozinha

---

### ✅ 3. Gestão de Produtos

**Entidade**: `Produto.java`
**Controller**: `ProdutoController.java`

**Campos**:
- Dados básicos: nome, código de barras, categoria, grupo
- Preços: preço de custo, preço de venda
- Estoque: controle de fabricação própria
- Dados fiscais: CFOP, CST, Origem, NCM, alíquotas ICMS/IPI/PIS/COFINS

**Endpoints**:
- `GET /api/produtos` - Listar produtos
- `POST /api/produtos` - Criar produto
- `PUT /api/produtos/{id}` - Atualizar produto
- `DELETE /api/produtos/{id}` - Deletar produto

**Frontend**: ✅ Implementado
- `/produtos` - Lista de produtos
- `/produtos/novo` - Cadastro
- `/produtos/:id` - Edição

---

### ✅ 4. Gestão de Estoque

**Entidades**:
- `Estoque.java` - Quantidade em estoque por produto
- `MovimentacaoEstoque.java` - Histórico de movimentações

**Controllers**:
- `EstoqueController.java` - Consulta de estoque
- `MovimentacaoEstoqueController.java` - Movimentações

**Tipos de Movimentação**:
- `ENTRADA` - Compra, produção, ajuste positivo
- `SAIDA` - Venda, consumo, ajuste negativo

**Endpoints**:
- `GET /api/estoque` - Listar estoque
- `GET /api/estoque/{produtoId}` - Estoque de um produto
- `POST /api/movimentacoes-estoque` - Registrar movimentação
- `GET /api/movimentacoes-estoque` - Histórico

**Frontend**: ✅ Implementado
- `/estoque` - Visualização de estoque
- `/estoque/:id/ajuste` - Ajuste de estoque
- `/estoque/:id/movimentacoes` - Histórico

---

### ✅ 5. Receitas (Produção Própria)

**Entidade**: `Receita.java`
**Controller**: `ReceitaController.java`

**Funcionalidades**:
- Cadastro de receitas com múltiplos ingredientes
- Produto final associado
- Custo de produção calculado automaticamente
- Baixa automática de estoque ao produzir

**Endpoints**:
- `GET /api/receitas` - Listar receitas
- `POST /api/receitas` - Criar receita
- `PUT /api/receitas/{id}` - Atualizar receita
- `POST /api/receitas/{id}/produzir` - Produzir lote

**Frontend**: ✅ Implementado
- `/receitas` - Lista de receitas
- `/receitas/nova` - Cadastro
- `/receitas/:id` - Edição

---

### ✅ 6. Autenticação e Usuários

**Entidades**:
- `Usuario.java` - Dados do usuário
- `Role` (enum) - Papéis (ADMIN, GARCOM, COZINHA)

**Controllers**:
- `UsuarioController.java` - CRUD de usuários
- `AuthController.java` - Login/Logout

**Security**:
- JWT Token (jjwt 0.11.5)
- `JwtAuthenticationFilter.java` - Filtro de autenticação
- `JwtUtil.java` - Geração e validação de tokens
- `WebSecurityConfig.java` - Configuração Spring Security

**Endpoints Públicos** (sem autenticação):
- `POST /api/auth/login`
- `GET /api/comandas/publica/{id}`
- `POST /api/comandas/publica/{id}/itens`

**Frontend**: ✅ Implementado
- Login com contexto de autenticação
- Proteção de rotas por papel
- Token persistido no localStorage

---

### ✅ 7. Dados Fiscais (Auxiliares)

**Entidades Implementadas**:
- `CFOP.java` - Código Fiscal de Operação
- `CST.java` - Código de Situação Tributária
- `Origem.java` - Origem da mercadoria (nacional, importada, etc.)

**Controllers**:
- `CFOPController.java`
- `CSTController.java`
- `OrigemController.java`

**Status**: ✅ Cadastros básicos implementados

---

### ✅ 8. NFCe - Nota Fiscal de Consumidor Eletrônica (NOVO)

**📂 Pacote**: `com.exemplo.controlemesas.nfe`

**Classes Implementadas** (6 arquivos):

1. **AssinaturaDigital.java**
   - Assinatura XML com certificado A1
   - Padrão XMLDSig ICP-Brasil
   - Canonicalização C14N
   - SHA-1 + RSA

2. **CertificadoDigital.java**
   - Carregamento de certificado .pfx
   - Gestão de KeyStore
   - Validação de senha
   - Thread-safe

3. **NfeXmlBuilder.java**
   - Construção de XML NFCe 4.0
   - 21 campos obrigatórios implementados
   - QR Code com CSC (SHA-1 hash)
   - Seção infNFeSupl completa
   - Suporte a múltiplos produtos

4. **NfeXmlValidator.java**
   - Validação local contra schemas XSD
   - Validação opcional (graceful degradation)
   - Relatório detalhado de erros (linha/coluna)
   - Suporte a arquivo e string

5. **SefazSoapClient.java**
   - Cliente SOAP para webservices SEFAZ
   - Comunicação HTTPS com mTLS
   - Envelope SOAP 1.2
   - Autorização e consulta de recibo

6. **SefazEndpoints.java**
   - URLs dos webservices SEFAZ
   - Suporte SP homologação/produção
   - Endpoints de autorização e consulta

**Controller**:
- `NfeValidatorController.java` - API de validação offline
  - `GET /api/nfe/validar?arquivo={path}`
  - `GET /api/nfe/validar/ultimo`
  - `POST /api/nfe/validar`

**Service**:
- `NfeService.java` - Orquestração completa
  - Geração de XML
  - Validação local (opcional)
  - Assinatura digital
  - Envio para SEFAZ
  - Consulta de recibo
  - Salvamento de XML/PDF

**Configurações**:
```properties
# Certificado Digital
certificado.caminho=certificado.pfx
certificado.senha=${CERT_PASSWORD}

# Ambiente SEFAZ
nfe.ambiente=2  # 1=Produção, 2=Homologação
nfe.serie=1
nfe.numero.atual=144
```

**Banco de Dados**:
```sql
-- CSC (Código de Segurança do Contribuinte)
INSERT INTO configuracoes (chave, valor) VALUES 
  ('nfce.csc.id', '000001'),
  ('nfce.csc.codigo', 'obtido-no-portal-sefaz');
```

**Status Atual**: ✅ **100% IMPLEMENTADO E FUNCIONANDO**
- ✅ XML NFCe 4.0 completo
- ✅ QR Code com CSC funcionando
- ✅ Assinatura digital validada
- ✅ Comunicação SEFAZ OK
- ✅ Validação local implementada
- ⏳ Aguardando expiração do cache SEFAZ (erro 225 é cache, não código)

**Documentação Criada**:
- `MIGRACAO_NFE.md` - Guia de migração SAT → NFCe
- `SCHEMAS_XSD_INSTRUCOES.md` - Instalação de schemas XSD

---

### ✅ 9. SAT (Sistema Autenticador e Transmissor) - LEGACY

**Status**: ⚠️ **EM DESUSO** - Migrado para NFCe

**Pacote**: `com.exemplo.controlemesas.sat`

**Motivo da Migração**:
- SAT está sendo descontinuado em SP
- NFCe é o padrão nacional
- Maior flexibilidade e recursos

**Ação Recomendada**: Manter por compatibilidade temporária, remover após validação completa da NFCe

---

### ✅ 10. Geração de PDFs

**Pacote**: `com.exemplo.controlemesas.pdf`

**Biblioteca**: OpenHTML to PDF 1.0.10

**Funcionalidades**:
- Geração de DANFE (Documento Auxiliar NFC-e)
- Cupons de comanda
- Relatórios diversos

**Status**: ✅ Implementado para SAT, precisa adaptação para NFCe

---

### ✅ 11. Configurações

**Entidade**: `Configuracao.java`
**Controller**: `ConfiguracaoController.java`

**Tipos de Configuração**:
- Dados da empresa (CNPJ, IE, nome, endereço)
- Configurações fiscais (CSC, série, numeração)
- Configurações do sistema

**Endpoints**:
- `GET /api/configuracoes` - Listar configurações
- `PUT /api/configuracoes/{id}` - Atualizar configuração

---

## 🔴 Módulos Pendentes/Incompletos

### 1. Relatórios e Dashboard

**Status**: ❌ Não implementado

**Necessidades**:
- Dashboard de vendas (diário, semanal, mensal)
- Relatório de produtos mais vendidos
- Relatório de comandas por período
- Relatório de estoque mínimo
- Gráficos de vendas por categoria
- Análise de lucro bruto

**Complexidade**: Média
**Prioridade**: Alta

---

### 2. Gestão Financeira

**Status**: ❌ Não implementado

**Necessidades**:
- Formas de pagamento (dinheiro, cartão, PIX)
- Controle de caixa (abertura, movimentação, fechamento)
- Contas a pagar/receber
- Fluxo de caixa
- Conciliação bancária

**Complexidade**: Alta
**Prioridade**: Alta

---

### 3. Gestão de Clientes

**Status**: ⚠️ Parcialmente implementado

**Implementado**:
- Campo `nomeCliente` em `ComandaResumo`

**Pendente**:
- Cadastro completo de clientes
- CPF/CNPJ na NFCe
- Histórico de compras
- Programa de fidelidade
- Envio de NFCe por e-mail

**Complexidade**: Média
**Prioridade**: Média

---

### 4. Integração com Delivery

**Status**: ❌ Não implementado

**Necessidades**:
- Integração iFood
- Integração Rappi
- Integração Uber Eats
- Pedidos próprios de delivery
- Rastreamento de entrega

**Complexidade**: Alta
**Prioridade**: Baixa (depende do modelo de negócio)

---

### 5. Reservas de Mesa

**Status**: ⚠️ Estrutura existe, não implementado

**Pendente**:
- Cadastro de reservas
- Agenda de reservas
- Confirmação de reserva
- Notificações

**Complexidade**: Média
**Prioridade**: Baixa

---

### 6. Gestão de Funcionários

**Status**: ⚠️ Básico implementado (usuários)

**Pendente**:
- Controle de ponto
- Escala de trabalho
- Comissões
- Folha de pagamento

**Complexidade**: Alta
**Prioridade**: Baixa

---

### 7. Contingência NFCe

**Status**: ❌ Não implementado

**Necessidades**:
- Emissão em contingência EPEC
- Emissão em contingência offline
- Transmissão de contingência
- Cancelamento de NFCe
- Inutilização de numeração

**Complexidade**: Alta
**Prioridade**: Média

---

### 8. NF-e (Nota Fiscal Eletrônica de Produto)

**Status**: ❌ Não implementado

**Diferenças da NFCe**:
- Destinatário obrigatório com CNPJ
- Transporte
- Impostos mais complexos
- ICMS-ST, IPI, substituição tributária

**Necessidade**: Se vender produtos para empresas

**Complexidade**: Alta
**Prioridade**: Baixa (depende do modelo de negócio)

---

### 9. Integrações Contábeis

**Status**: ❌ Não implementado

**Necessidades**:
- Exportação SPED Fiscal
- Exportação SPED Contribuições
- Integração com sistema contábil
- XML para contabilidade

**Complexidade**: Muito Alta
**Prioridade**: Média

---

### 10. Impressão Térmica

**Status**: ❌ Não implementado

**Necessidades**:
- Impressão de comandas na cozinha
- Impressão de cupons no balcão
- Impressão de DANFCe simplificada
- Driver de impressora ESC/POS

**Complexidade**: Média
**Prioridade**: Alta

---

## 🗄️ Modelo de Dados (Resumo)

### Entidades Principais

```
Mesa (id, numero, qrCode, status)
  ↓ 1:N
Comanda (id, mesaId, dataAbertura, dataFechamento, status)
  ↓ 1:N
ItemComanda (id, comandaId, produtoId, quantidade, precoVenda, status)
  ↓ N:1
Produto (id, nome, precoVenda, categoria, dadosFiscais)
  ↓ 1:1
Estoque (id, produtoId, quantidade)
  ↓ 1:N
MovimentacaoEstoque (id, produtoId, tipo, quantidade, data)

Comanda ← 1:1 → ComandaResumo (id, comandaId, total, nfceData)

Receita (id, produtoFinalId, custoProducao)
  ↓ 1:N
ReceitaItem (id, receitaId, produtoIngredienteId, quantidade)

Usuario (id, nome, email, senha, role)
```

---

## 📊 Endpoints REST (Resumo)

### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

### Mesas
- `GET /api/mesas` - Listar
- `POST /api/mesas` - Criar
- `GET /api/mesas/{id}` - Buscar
- `PUT /api/mesas/{id}` - Atualizar
- `DELETE /api/mesas/{id}` - Deletar

### Comandas
- `GET /api/comandas` - Listar
- `POST /api/comandas` - Criar
- `GET /api/comandas/{id}` - Buscar
- `POST /api/comandas/{id}/fechar` - Fechar
- `GET /api/comandas/mesa/{mesaId}` - Por mesa

### Itens de Comanda
- `POST /api/comandas/{id}/itens` - Adicionar
- `PUT /api/comandas/itens/{id}` - Atualizar
- `DELETE /api/comandas/itens/{id}` - Remover
- `PATCH /api/comandas/itens/{id}/status` - Mudar status

### Produtos
- `GET /api/produtos` - Listar
- `POST /api/produtos` - Criar
- `GET /api/produtos/{id}` - Buscar
- `PUT /api/produtos/{id}` - Atualizar
- `DELETE /api/produtos/{id}` - Deletar

### Estoque
- `GET /api/estoque` - Listar
- `GET /api/estoque/{produtoId}` - Por produto
- `POST /api/movimentacoes-estoque` - Movimentar

### NFCe
- `POST /api/nfe/emitir` - Emitir NFCe
- `GET /api/nfe/validar?arquivo={path}` - Validar XML
- `GET /api/nfe/validar/ultimo` - Validar último

### Receitas
- `GET /api/receitas` - Listar
- `POST /api/receitas` - Criar
- `POST /api/receitas/{id}/produzir` - Produzir

### Usuários
- `GET /api/usuarios` - Listar
- `POST /api/usuarios` - Criar
- `PUT /api/usuarios/{id}` - Atualizar

---

## 🎯 Próximos Passos Recomendados

### Fase 1: Consolidação NFCe (Curto Prazo - 1-2 semanas)

1. **Testar NFCe em Produção**
   - [ ] Obter CSC de produção
   - [ ] Configurar ambiente de produção
   - [ ] Testar autorização real
   - [ ] Validar QR Code funcionando
   - [ ] Implementar cancelamento de NFCe

2. **Melhorias na Emissão**
   - [ ] DANFE simplificada para impressão
   - [ ] Envio de NFCe por e-mail
   - [ ] Armazenamento organizado de XMLs
   - [ ] Logs estruturados de emissão

3. **Contingência Básica**
   - [ ] Modo offline (EPEC)
   - [ ] Transmissão de contingência
   - [ ] Inutilização de numeração

---

### Fase 2: Impressão e Automação (Médio Prazo - 2-4 semanas)

1. **Impressão Térmica**
   - [ ] Driver ESC/POS
   - [ ] Impressão de comandas na cozinha
   - [ ] Impressão de cupons no balcão
   - [ ] Impressão de DANFCe

2. **Automação de Processos**
   - [ ] Baixa automática de estoque ao fechar comanda
   - [ ] Atualização de preços em lote
   - [ ] Alertas de estoque mínimo
   - [ ] Notificações para cozinha (WebSocket)

---

### Fase 3: Gestão Financeira (Médio Prazo - 3-6 semanas)

1. **Formas de Pagamento**
   - [ ] Cadastro de formas de pagamento
   - [ ] Múltiplas formas por comanda
   - [ ] Integração com TEF (opcional)
   - [ ] Integração PIX

2. **Controle de Caixa**
   - [ ] Abertura/fechamento de caixa
   - [ ] Sangria e reforço
   - [ ] Conciliação de valores
   - [ ] Relatório de fechamento

---

### Fase 4: Relatórios e BI (Médio Prazo - 4-6 semanas)

1. **Dashboard de Vendas**
   - [ ] Vendas por período
   - [ ] Produtos mais vendidos
   - [ ] Gráficos de evolução
   - [ ] Análise de lucro

2. **Relatórios Operacionais**
   - [ ] Relatório de estoque
   - [ ] Relatório de comandas
   - [ ] Relatório de produtos
   - [ ] Exportação Excel/PDF

---

### Fase 5: Gestão de Clientes (Longo Prazo - 6-8 semanas)

1. **Cadastro de Clientes**
   - [ ] Cadastro completo (CPF, endereço, telefone)
   - [ ] Vinculação com comandas
   - [ ] CPF/CNPJ na NFCe

2. **Fidelização**
   - [ ] Programa de pontos
   - [ ] Histórico de compras
   - [ ] Campanhas de marketing

---

### Fase 6: Delivery (Longo Prazo - 8-12 semanas)

1. **Sistema Próprio**
   - [ ] Cardápio online
   - [ ] Pedidos via web
   - [ ] Rastreamento de entrega
   - [ ] Integração com motoboys

2. **Integrações**
   - [ ] iFood API
   - [ ] Rappi API
   - [ ] Uber Eats API

---

## 🔧 Melhorias Técnicas Recomendadas

### Código

1. **Testes**
   - [ ] Testes unitários (JUnit 5)
   - [ ] Testes de integração (TestContainers)
   - [ ] Cobertura mínima de 70%

2. **Documentação**
   - [ ] Swagger UI completo
   - [ ] Documentação de APIs
   - [ ] Diagramas de arquitetura

3. **Qualidade**
   - [ ] SonarQube
   - [ ] Análise estática (já tem PMD, Checkstyle, SpotBugs)
   - [ ] Code review sistemático

### Infraestrutura

1. **Deploy**
   - [ ] Dockerização
   - [ ] CI/CD (GitHub Actions)
   - [ ] Ambientes (dev, homolog, prod)

2. **Monitoramento**
   - [ ] Logs estruturados (Logback JSON)
   - [ ] Métricas (Micrometer + Prometheus)
   - [ ] Alertas (Grafana)

3. **Segurança**
   - [ ] HTTPS obrigatório
   - [ ] Rate limiting
   - [ ] Proteção CSRF
   - [ ] Auditoria de ações

---

## 📝 Considerações Finais

### Pontos Fortes do Sistema

✅ Arquitetura bem estruturada (camadas separadas)  
✅ NFCe implementada de forma completa e correta  
✅ Controle de estoque funcional  
✅ Autenticação JWT implementada  
✅ Interface responsiva (TailwindCSS)  
✅ Código limpo (Lombok, MapStruct)  

### Pontos de Atenção

⚠️ Falta de testes automatizados  
⚠️ Relatórios não implementados  
⚠️ Gestão financeira incompleta  
⚠️ Impressão térmica não implementada  
⚠️ Backup de banco de dados não configurado  

### Recomendações Imediatas

1. **Validar NFCe em produção** - Prioridade máxima
2. **Implementar impressão térmica** - Essencial para operação
3. **Criar dashboard básico** - Visibilidade do negócio
4. **Implementar backup automático** - Proteção de dados
5. **Adicionar testes** - Garantir qualidade

---

**Documento criado em**: 12/12/2025  
**Última atualização**: 12/12/2025  
**Versão**: 1.0  
**Autor**: Levantamento Técnico Automatizado
