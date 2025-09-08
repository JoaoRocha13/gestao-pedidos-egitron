# Gestão de Pedidos - EGITRON

Desafio Técnico Backend (Spring Boot + Java 8 + MSSQL)

## Tecnologias
- Java 8 (Zulu)
- Spring Boot 2.7.18
- Maven
- Hibernate / JPA
- SQL Server (MSSQL)
- Postman (testes de API)
- Mailtrap (testes de e-mail)
- Spring Boot Actuator (health/info)
- RestTemplate (consumo de API externa)

## Requisitos
- JDK 1.8 instalado
- Maven 3.8+ instalado
- SQL Server em execução
- Instância local `localhost\SQLEXPRESS`

---

## Setup do Projeto
1) **Clonar o repositório**
```bash
git clone <repo>
cd gestao-pedidos
```

2) **Criar a base de dados e tabelas (SSMS)**
- Executar o script `db/schema.sql`.
- Isto cria:
    - Base de dados `gestaopedidosdb`
    - Login `egitron / egitron123`
    - Tabelas: `Client`, `Order`, `ErrorLog`

3) **Configuração da ligação (`src/main/resources/application.properties`)**
```properties
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=gestaopedidosdb;encrypt=false;trustServerCertificate=true
spring.datasource.username=egitron
spring.datasource.password=egitron123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.show-sql=false

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

4) **Arrancar**
```bash
mvn spring-boot:run
```

---

## Validação Externa de Clientes (**CONCLUÍDO**)
Ao criar/atualizar pedidos (quando altera nome/email), a API valida o **nome/email** num serviço externo (mock em DEV) e **grava o resultado no próprio pedido**.

### Configuração
Adicionar ao `application.properties`:
```properties
# External Client Validation
external.client.validation.enabled=true
external.client.validation.url=http://localhost:8080/mock-api/clients/verify
external.client.validation.failStrategy=FAIL_CLOSED   # ou FAIL_OPEN
```
- `enabled`: liga/desliga a validação.
- `url`: URL do serviço externo (em DEV aponta para o mock interno).
- `failStrategy`:
    - `FAIL_CLOSED` → falha externa bloqueia o pedido (400).
    - `FAIL_OPEN` → falha externa permite seguir, registando “validation skipped …”.



### Migração SQL (campos de validação em `Order`)
Executar **uma vez** após `schema.sql` (ex.: `db/migrations/2025_09_08_add_order_validation.sql`):
```sql
ALTER TABLE [dbo].[Order]
ADD validated BIT NULL,
    validationReason VARCHAR(255) NULL,
    validationExternalId VARCHAR(64) NULL,
    validatedAt DATETIME NULL;
```

> **Recomendado (integridade):** email único em `Client`
```sql
ALTER TABLE [dbo].[Client]
ADD CONSTRAINT UQ_Client_Email UNIQUE (email);
```



### Arquitetura
- `config/HttpClientConfig.java` → cria o **Bean `RestTemplate`** (cliente HTTP único/configurável).
- `service/client/ClientValidationService.java` → **interface** (contrato) da validação externa.
- `service/client/impl/ClientValidationServiceImpl.java` → implementação com `RestTemplate`; lê `enabled/url/failStrategy`; aplica FAIL_OPEN/CLOSED.
- `api/mock/ClientValidationMockController.java` → **mock DEV** `POST /mock-api/clients/verify` (regras simples: formato email, bloqueia `@example.com`, nome ≥ 2).
- `service/impl/OrderServiceImpl.java` → chama validação, **grava resultado no pedido** e aplica política do cliente.
- `model/Order.java` → adicionados campos `validated`, `validationReason`, `validationExternalId`, `validatedAt`.



### Política de Cliente (integridade)
- **Email identifica o cliente**.
- Se já existir cliente com o mesmo email e **nome diferente** → **400 Bad Request** (não renomeamos via `POST /api/orders`).



### Fluxo (resumo)
1. `POST /api/orders` → `OrderServiceImpl.create(dto)`.
2. Chama `ClientValidationService.validate(name,email)`.
3. `ClientValidationServiceImpl` faz `POST` à `external.client.validation.url` (mock em DEV).
4. Resposta `{valid, reason, externalId}`:
    - `valid=false` → **400**.
    - `valid=true` → cria/associa `Client`; em `Order` grava:
        - `validated=true`
        - `validationReason` (ex.: "OK" | "validation skipped …")
        - `validationExternalId`
        - `validatedAt=NOW()`.



### Trocar mock por serviço real
- Produção: **alterar apenas** `external.client.validation.url` para o endpoint real (ex.: `https://api.externa/clients/verify`).
- Não é necessário alterar código.



## Endpoints Implementados
- `POST /api/orders` → criar pedido (validação externa + gravação do resultado)
- `GET /api/orders` → listar pedidos (paginado + filtros status/datas)
- `GET /api/orders/{id}` → consultar pedido específico
- `PATCH /api/orders/{id}` → atualizar parcialmente (estado, cliente*, valor)  
  \* Se mudar nome/email → **revalida externamente** e atualiza campos de validação na `Order`.
- `GET /actuator/health` → estado da API e BD
- **DEV**: `POST /mock-api/clients/verify` → mock da validação externa



## Modelo de Dados (E-R)
- **Client** → dados do cliente (email único recomendado)
- **Order** → pedido associado a um cliente
    - Campos extra (validação externa): `validated`, `validationReason`, `validationExternalId`, `validatedAt`
- **ErrorLog** → registo de erros da aplicação

**Relações**
- Client (1) —— (N) Order
- ErrorLog isolada (sem FK)



## Estado Atual
- Projeto Spring Boot configurado
- Script SQL (`db/schema.sql`) criado e validado no MSSQL
- **Migração aplicada** para campos de validação em `Order`
- Entidades JPA (`Client`, `Order`, `ErrorLog`) implementadas
- Repositórios Spring Data JPA criados
- DTOs: `CreateOrderDTO`, `UpdateOrderDTO`, `OrderFilterDTO`, `OrderDTO`
- Service:
    - `OrderServiceImpl` com validação externa e política (email = chave)
    - `ClientValidationService` + `ClientValidationServiceImpl`
- Controllers: `OrderController`, **Mock DEV** (`ClientValidationMockController`)
- Pesquisa com filtros (status + intervalo de datas)
- Gestão de Erros: `ApiError`, `GlobalExceptionHandler`, `ErrorLog` + `ErrorLogService`
- Health: `/actuator/health`, `/actuator/info`
- Postman: collection em `postman/GestaoPedidos_API.postman_collection.json`



## Notas de Clean Code
- Separação de responsabilidades (Controller → Service → Gateway externo).
- Injeção de dependências (Bean `RestTemplate`) e configuração centralizada.
- Regras de domínio explícitas (email identifica cliente).
- Tratamento uniforme de erros (400/404/500) e registo em `ErrorLog`.
- Comportamento configurável por `properties`.



##  Próximos Passos
- Envio de relatórios de erros por e-mail (Mailtrap)
- Implementar autenticação OAuth2 (Bearer token)

