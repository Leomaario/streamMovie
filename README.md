# SouzaLink Coach - Plataforma de E-Learning

![Dashboard](https://i.imgur.com/Qk2w4gV.png)

## 🚀 Sobre o Projeto

O SouzaLink Coach é uma plataforma de e-learning completa e robusta, desenhada para gestão e distribuição de conteúdo de capacitação interna. A aplicação permite que administradores organizem cursos em catálogos, façam upload de vídeos, e que os utilizadores assistam ao conteúdo, acompanhem o seu progresso e emitam certificados.

Este projeto foi construído do zero como um sistema full-stack moderno, utilizando Java com Spring Boot no backend e React com Vite no frontend.

---

## 🛠️ Tecnologias Utilizadas

O projeto é dividido em duas partes principais:

### **Backend (API RESTful)**
* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.3.1
* **Segurança:** Spring Security com autenticação baseada em Token JWT.
* **Base de Dados:** Spring Data JPA / Hibernate com PostgreSQL.
* **Gestão de Dependências:** Maven
* **Servidor:** Docker para a base de dados PostgreSQL.

### **Frontend (Single Page Application)**
* **Framework:** React 18 com Vite.
* **Roteamento:** React Router DOM.
* **Estilização:** CSS puro com foco em layouts modernos (Flexbox e Grid) e responsividade.
* **UI/UX:** Componentes interativos, design "clean" e animações subtis para uma melhor experiência do utilizador.
* **Ícones:** React Bootstrap Icons.

---

## ✨ Funcionalidades Principais

O sistema possui dois lados: a visão do utilizador comum e a área de gestão do administrador.

### **Para Todos os Utilizadores:**
* **Login Seguro:** Autenticação com utilizador/senha e validação por token JWT.
* **Dashboard Personalizado:** Exibe estatísticas de progresso, cursos em destaque e certificados recentes.
* **Catálogo de Cursos:** Navegação por catálogos que agrupam os vídeos por tema.
* **Player de Vídeo:** Streaming de vídeo seguro e autenticado, com playlist de vídeos do catálogo.
* **Sistema de Progresso:** Funcionalidade de "Marcar como Concluído" para cada vídeo.
* **Página de Certificados:** Galeria para visualizar os certificados conquistados.
* **Design Responsivo:** A experiência adapta-se a qualquer tamanho de tela, do desktop ao telemóvel.

### **Para Administradores:**
* **Painel de Admin:** Dashboard com estatísticas gerais da plataforma (total de cursos, utilizadores) e uma lista dos últimos utilizadores registados.
* **Gestão de Utilizadores:** Uma página dedicada para listar, editar e apagar utilizadores.
* **Gestão de Catálogos:** Interface para criar, editar e apagar catálogos de cursos.
* **Gestão de Cursos (Vídeos):** Ferramentas para fazer upload de novos vídeos (com thumbnail opcional), editar os seus detalhes e apagá-los.
* **Controlo de Acesso Baseado em Permissões (RBAC):** Rotas do backend e links/botões do frontend são protegidos, aparecendo apenas para utilizadores com a permissão correta (`ADMIN`, `LIDER`, `USER`).

---

## ⚙️ Como Executar o Projeto Localmente

Para rodar este projeto na sua máquina, você vai precisar de:
* Java (JDK 17 ou superior)
* Maven 3.8+
* Node.js 18+ (com npm)
* Docker e Docker Compose

### **Passos:**

1.  **Clonar o Repositório:**
    ```sh
    git clone [https://github.com/seu-usuario/seu-repositorio.git](https://github.com/seu-usuario/seu-repositorio.git)
    cd seu-repositorio
    ```

2.  **Configurar o Backend:**
    * Navegue para a pasta `backend`.
    * No ficheiro `application.properties`, ajuste as credenciais do banco de dados se necessário.
    * Rode `mvn clean install` para compilar o projeto.

3.  **Configurar o Frontend:**
    * Navegue para a pasta `frontend`.
    * Rode `npm install` para instalar as dependências.

4.  **Iniciar o Ambiente com Docker:**
    * Na pasta raiz do projeto (onde está o `docker-compose.yml`), rode:
        ```sh
        docker-compose up
        ```
    * Isto irá iniciar o banco de dados PostgreSQL.

5.  **Iniciar os Servidores:**
    * Num terminal, na pasta `backend`, inicie a aplicação Spring: `mvn spring-boot:run` ou execute a classe principal na sua IDE.
    * Noutro terminal, na pasta `frontend`, inicie o servidor de desenvolvimento: `npm run dev`.

6.  **Aceder à Aplicação:**
    * Abra o seu navegador e vá para `http://localhost:5173`.

---

Feito, meu parça! Este `README` dá uma visão geral completa e profissional do projeto que a gente construiu.
