# 🍽️ Sistema de Controle de Bares/Restaurantes

Projeto em desenvolvimento para controle de operações em bares e restaurantes, com foco em aprendizado e portfólio.

## 🔧 Tecnologias Utilizadas

### Backend (Java + Spring Boot)
- Java 17+
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

### Frontend (React + Vite)
- React 19
- Vite
- TypeScript
- Tailwind CSS

## ✅ Funcionalidades Implementadas

### Backend
- [x] CRUD de Mesas
- [x] CRUD de Produtos com integração de CFOP, CST, Origem e ICMS
- [x] Entidade `Usuario` com autenticação simples (login)
- [x] Endpoints de apoio: CFOP, CST, Origem
- [x] Seed de dados iniciais para tabelas fixas

### Frontend
- [x] Tela de login com validação
- [x] Dashboard com atalhos para funcionalidades
- [x] Listagem de mesas com status visual
- [x] Cadastro e edição de produtos com:
  - Máscara de moeda (preço e custo)
  - Máscara para código de barras (EAN-13)
  - Validação de NCM, IPI e ICMS
  - Combos para CFOP, CST, Origem e ICMS padrão
- [ ] Cadastro e edição de mesas (em desenvolvimento)

## 🚀 Como Rodar o Projeto

### Backend
1. Configure um banco de dados MySQL.
2. Altere o `src/main/resources/application.properties` com os dados de acesso ao banco.
3. Rode o backend com Maven:
   ```bash
   mvn spring-boot:run
   ```

### Frontend
1. Instale as dependências:
   ```bash
   npm install
   ```
2. Inicie a aplicação:
   ```bash
   npm run dev
   ```

> Certifique-se de que o backend esteja rodando em `http://localhost:8080`.

## 📌 Observações
- Projeto em constante evolução: futuras funcionalidades incluirão comandas, relatórios, controle de estoque e fluxo de caixa.
- Desenvolvido com fins de estudo e demonstração de habilidades em full stack.

## 📎 Contato
👤 Luca Alexandre Pantano  
🔗 [LinkedIn](https://www.linkedin.com/in/lucapantano1)