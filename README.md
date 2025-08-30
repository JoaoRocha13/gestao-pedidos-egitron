# Gestão de Pedidos - EGITRON

Desafio Técnico Backend (Spring Boot + Java 8 + MSSQL)

## 🚀 Tecnologias
- Java 8 (Zulu)
- Spring Boot 2.7.18
- Maven
- Hibernate / JPA
- SQL Server (MSSQL)
- Postman (testes de API)
- Mailtrap (testes de e-mail)

## 📦 Requisitos
- JDK 1.8 instalado
- Maven 3.8+ instalado
- SQL Server em execução
- Instância local `localhost\SQLEXPRESS`

## ⚙️ Setup do Projeto
1. Clonar o repositório  
   git clone <repo>  
   cd gestao-pedidos

2. Criar a base de dados e tabelas
    - Executar o script `sql/gestaopedidos.sql` no SQL Server Management Studio (SSMS).
    - Isto cria:
        - Base de dados `gestaopedidos`
        - Login `egitron / egitron123`
        - Tabelas: `Client`, `Order`, `OrderStatusHistory`, `ErrorLog`

3. Configuração da ligação no `application.properties`  
   spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=gestaopedidos;encrypt=false;trustServerCertificate=true  
   spring.datasource.username=egitron  
   spring.datasource.password=egitron123  
   spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

   spring.jpa.hibernate.ddl-auto=none  
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect  
   spring.jpa.show-sql=false

4. Compilar e arrancar a aplicação  
   mvn spring-boot:run

5. Endpoints de teste já disponíveis
    - `/health` → responde "OK"
    - (mais endpoints serão adicionados nas próximas fases)

## 📊 Modelo de Dados (E-R)
Entidades principais:
- **Client** → dados do cliente
- **Order** → pedido associado a um cliente
- **OrderStatusHistory** → histórico de estados do pedido
- **ErrorLog** → registo de erros da aplicação

Relações:
- `Client (1) —— (N) Order`
- `Order (1) —— (N) OrderStatusHistory`
- `ErrorLog` isolada (sem FK)



## ✅ Estado Atual
- Projeto Spring Boot configurado
- Script SQL criado e validado no MSSQL
- BD `gestaopedidos` criada com tabelas e constraints
- Entidades JPA (`Client`, `Order`, `OrderStatusHistory`, `ErrorLog`) implementadas
- Repositórios Spring Data JPA criados
- Aplicação arranca sem erros de datasource

## 🔜 Próximos Passos
- Criar controladores REST (`/clients`, `/orders`)
- Implementar endpoints para **criar, listar e consultar pedidos**
- Adicionar validação externa de clientes (mock API)
- Gerir logs de erro via `ErrorLog` + envio de e-mails
- Implementar autenticação OAuth2 (Bearer token)
