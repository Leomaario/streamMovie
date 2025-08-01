# E-Learning Platform API

## 🚀 Sobre o Projeto
API desenvolvida para uma plataforma de E-Learning, permitindo gestão de cursos, vídeos e progresso dos alunos. Sistema completo de autenticação e autorização com diferentes níveis de acesso.

## 📋 Funcionalidades Principais

- ✅ Autenticação e Autorização com JWT
- 👤 Gestão de Usuários e Perfis
- 📚 Gerenciamento de Catálogos de Cursos
- 🎥 Sistema de Upload e Gestão de Vídeos
- 📊 Dashboard com Estatísticas
- 📝 Acompanhamento de Progresso do Aluno
- 🔐 Níveis de Acesso (Admin, Moderador, Usuário)

## 🛠️ Tecnologias Utilizadas

### Backend
- **Framework**: Spring Boot (Jakarta EE)
- **Linguagem**: Java 23
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Segurança**: Spring Security + JWT

### Dependências Principais
```xml
<!-- Adicionar no pom.xml -->
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```


## 🏗️ Estrutura do Projeto
```
src/
├── main/
│   ├── java/
│   │   └── com/hrrb/backend/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── security/
│   │       └── BackendApplication.java
│   └── resources/
│       └── application.properties
```


## 🔐 Endpoints da API

### Autenticação
```
POST /api/auth/login - Login de usuário
POST /api/auth/registrar - Registro de novo usuário
GET /api/auth/health - Status do servidor
```


### Dashboard
```
GET /api/dashboard/stats - Estatísticas gerais
GET /api/user-dashboard/data - Dados do dashboard do usuário
```


### Vídeos
```
GET /api/videos - Lista todos os vídeos
POST /api/videos - Adiciona novo vídeo
PUT /api/videos/{id} - Atualiza vídeo
DELETE /api/videos/{id} - Remove vídeo
```


### Catálogos
```
GET /api/catalogos - Lista catálogos
POST /api/catalogos - Cria catálogo
PUT /api/catalogos/{id} - Atualiza catálogo
DELETE /api/catalogos/{id} - Remove catálogo
```


## ⚙️ Configuração do Ambiente

### Variáveis de Ambiente
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://seu-banco-de-dados
SPRING_DATASOURCE_USERNAME=seu-usuario
SPRING_DATASOURCE_PASSWORD=sua-senha
JWT_SECRET=sua-chave-secreta
```


### Application Properties
```properties
spring.application.name=backend
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
jwt.expirationMs=86400000
```


## 🚀 Deploy

### Pré-requisitos
- Java 23
- Maven
- PostgreSQL

### Passos para Deploy
1. Clone o repositório
```shell script
git clone https://seu-repositorio.git
```


2. Configure as variáveis de ambiente

3. Build do projeto
```shell script
mvn clean install
```


4. Execute o projeto
```shell script
java -jar target/backend-0.0.1-SNAPSHOT.jar
```


## 🔒 Segurança

### Níveis de Acesso
- **ADMIN**: Acesso total ao sistema
- **MODERADOR**: Gestão de conteúdo
- **USER**: Acesso aos cursos e progresso

### JWT Configuration
- Token expira em 24 horas
- Refresh token não implementado

## 📝 Logs e Monitoramento
- Logging detalhado para debugging
- Monitoramento de autenticação
- Rastreamento de requisições

## 🤝 Contribuição
1. Faça um Fork do projeto
2. Crie sua Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a Branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença
Este projeto está sob a licença [MIT](LICENSE.md)

## 🎯 Status do Projeto
- [x] Autenticação JWT
- [x] CRUD de Vídeos
- [x] CRUD de Catálogos
- [x] Dashboard
- [ ] Sistema de Notificações
- [ ] Chat em Tempo Real
- [ ] Sistema de Avaliações

## 📞 Contato
- LinkedIn: https://www.linkedin.com/in/leomaario/
- Email: leomariodev@outlook.com

## 🙏 Agradecimentos
- Equipe de desenvolvimento
- Contribuidores
- Comunidade Open Source

