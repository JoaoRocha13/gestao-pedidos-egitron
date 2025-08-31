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
    - Executar o script `db/schema.sql` no SQL Server Management Studio (SSMS).
    - Isto cria:
        - Base de dados `gestaopedidosdb`
        - Login `egitron / egitron123`
        - Tabelas: `Client`, `Order`, `ErrorLog`

3. Configuração da ligação no `application.properties`  
   spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=gestaopedidosdb;encrypt=false;trustServerCertificate=true  
   spring.datasource.username=egitron  
   spring.datasource.password=egitron123  
   spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

   spring.jpa.hibernate.ddl-auto=none  
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect  
   spring.jpa.show-sql=false

4. Compilar e arrancar a aplicação  
   mvn spring-boot:run

5. Endpoints implementados
    - POST /api/orders → criar pedido
    - GET /api/orders → listar pedidos (paginado + filtros)
    - GET /api/orders/{id} → consultar pedido específico
    - PATCH /api/orders/{id} → atualizar parcialmente (estado, cliente, valor)

## 📊 Modelo de Dados (E-R)
Entidades principais:
- **Client** → dados do cliente
- **Order** → pedido associado a um cliente
- **ErrorLog** → registo de erros da aplicação

Relações:
- Client (1) —— (N) Order
- ErrorLog isolada (sem FK)

## ✅ Estado Atual
- Projeto Spring Boot configurado
- Script SQL (`db/schema.sql`) criado e validado no MSSQL
- BD `gestaopedidosdb` criada com tabelas e constraints
- Entidades JPA (`Client`, `Order`, `ErrorLog`) implementadas
- Repositórios Spring Data JPA criados
- DTOs implementados:
    - CreateOrderDTO → criação de pedidos
    - UpdateOrderDTO → atualização parcial de pedidos
    - OrderFilterDTO → filtros de pesquisa (status, datas, search)
    - OrderDTO → resposta limpa para o frontend
- Camada Service (`OrderServiceImpl`) implementada
- Controlador REST (`OrderController`) criado
- Pesquisa avançada com filtros implementada
- Testes de API validados via Postman
- Collection Postman exportada e incluída no repositório (`postman/GestaoPedidos_API.postman_collection.json`)

## 🔜 Próximos Passos
- Adicionar validação externa de clientes (mock API)
- Gerir logs de erro via `ErrorLog` + envio de e-mails
- Implementar autenticação OAuth2 (Bearer token)  
