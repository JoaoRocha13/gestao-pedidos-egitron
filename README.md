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
- Spring Scheduler (@Scheduled para relatórios automáticos)

## Requisitos
- JDK 1.8 instalado
- Maven 3.8+ instalado
- SQL Server em execução
- Instância local `localhost\SQLEXPRESS`

---

## Setup do Projeto

### 1) Clonar o repositório
git clone <repo>
cd gestao-pedidos

### 2) Criar a base de dados e tabelas (SSMS)
Executar o script `db/gestaopedidosdb.sql`.  
Isto cria:

- Base de dados `gestaopedidosdb`
- Login `egitron / egitron123`
- Tabelas: `Client`, `Order`, `ErrorLog`, `OrderStatusHistory`

### 3) Configuração da ligação
Editar `src/main/resources/application.properties`:

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

### 4) Arrancar
mvn spring-boot:run

---

## Validação Externa de Clientes
Ao criar/atualizar pedidos (quando altera nome/email), a API valida o nome/email num serviço externo (mock em DEV) e grava o resultado no próprio pedido.

**Configuração**
external.client.validation.enabled=true  
external.client.validation.url=http://localhost:8080/mock-api/clients/verify  
external.client.validation.failStrategy=FAIL_CLOSED

- `enabled`: liga/desliga a validação.
- `url`: URL do serviço externo (em DEV aponta para o mock interno).
- `failStrategy`: comportamento em falha (`FAIL_CLOSED` → bloqueia, `FAIL_OPEN` → permite).

**Migração SQL**
ALTER TABLE [dbo].[Order]  
ADD validated BIT NULL,  
validationReason VARCHAR(255) NULL,  
validationExternalId VARCHAR(64) NULL,  
validatedAt DATETIME NULL;

**Política de Cliente**
- Email único (`UQ_Client_Email`).
- Se existir email igual mas nome diferente → `400 Bad Request`.

---

## Sistema de Relatórios de Erros
- **ErrorLog**: entidade para guardar erros no SQL Server.
- **GlobalExceptionHandler**: captura exceções globais e grava no BD.
- **ErrorLogService**: cria os registos com detalhes (level, source, endpoint, message, stacktrace).
- **ErrorReportService**: gera relatórios em texto simples.
- **EmailService**: envia emails via Mailtrap.
- **ErrorReportOrchestrator**: envia relatórios automáticos com `@Scheduled`.

**Fluxo**: exceções → gravadas em `ErrorLog` → incluídas em relatórios → enviados por email.

**Configuração**
spring.mail.host=sandbox.smtp.mailtrap.io  
spring.mail.port=2525  
spring.mail.username=<user>  
spring.mail.password=<pass>  
spring.mail.from=reports@gestaopedidos.local

error.report.enabled=true  
error.report.to=<teu_email>  
error.report.cron=*/30 * * * * *
error.report.lookbackHours=1  
error.report.maxItems=200


## Evidências (Logs & Relatórios)

**Ficheiros em `/docs`:**
- `error-report-sample.txt` — corpo do e-mail do Mailtrap.
- `error-report-mailtrap-01.png` (opcional).
- `error-log-sample.json` — 1–2 registos do `ErrorLog`.


**Reproduzir (DEV):**
1) `application.properties`  
   `error.report.cron=0 */1 * * * *`  # depois volta a diário (ex.: 0 0 9 * * *)

2) Forçar erros:  
   `GET {{baseUrl}}/api/orders/boom` (Bearer `{{token}}`)

3) Mailtrap → copiar corpo do e-mail para `docs/error-report-sample.txt`.

4) SSMS → exportar 2 registos em JSON (copiar a célula do grid e colar em `docs/error-log-sample.json`):
```sql
SELECT TOP (2)
  errorId AS id,
  CONVERT(varchar(19), occurredAtUtc, 126) AS occurredAt,
  [level], [source], endpoint, [message],
  LEFT(details, 600) + CASE WHEN LEN(details) > 600 THEN ' …(truncated)' ELSE '' END AS details
FROM dbo.ErrorLog
ORDER BY occurredAtUtc DESC
FOR JSON PATH;.
```

---

## Histórico de Status de Pedido
- **OrderStatusHistory**: guarda cada alteração de estado de um `Order`.

**Endpoint**
GET /api/orders/{id}/history  
Permite consultar todas as transições de estado de um pedido.

---

## Endpoints Implementados
- **POST /api/orders** → criar pedido (validação externa + gravação)
- **GET /api/orders** → listar pedidos (paginado + filtros status/datas)
- **GET /api/orders/{id}** → consultar pedido específico
- **PATCH /api/orders/{id}** → atualizar parcialmente (estado, cliente*, valor)
- **GET /api/orders/{id}/history** → histórico de alterações de estado
- **GET /actuator/health** → estado da API e BD
- **POST /mock-api/clients/verify** → mock de validação externa (DEV)
- **POST /auth/login(token)** → autenticação com JWT/OAuth2
- **GET /auth/me(token)** → info do utilizador autenticado (debug)
- **GET /orders (token)** → listar pedidos autenticados
- **POST /orders (token)** → criar pedido autenticado
- **GET /Boom** → forçar erro 500 (testes)

---

## Modelo de Dados
- **Client** → cliente (email único).
- **Order** → pedido associado a um cliente, com campos extra de validação externa.
- **ErrorLog** → registo de erros da aplicação.
- **OrderStatusHistory** → histórico das mudanças de estado de cada pedido.

---

## Autenticação OAuth2 / JWT (Bearer)

- Configuração: 

- Utilizador de aplicação
app.auth.username=admin
app.auth.password.bcrypt=<hash BCrypt de admin123>

- JWT
app.jwt.secret=egitron-gestao-pedidos-super-secret-key-2025-abc123456789!
app.jwt.expirationMillis=3600000

- Endpoints de Auth
POST /auth/login → devolve JWT
GET /auth/me → devolve utilizador autenticado (debug)


## Fluxo de Autenticação (resumo)
1) Login
Frontend → POST /auth/login
Controller valida credenciais
JwtService gera JWT
Resposta com accessToken

2) Chamada protegida
Frontend → GET /api/orders com Authorization: Bearer <token>
JwtAuthFilter valida token
SecurityConfig deixa passar
OrdersController executa

3) Token inválido/ausente
JwtAuthFilter não autentica
SecurityConfig bloqueia → 401 Unauthorized

---

## Testes (unitários e controller)

1) Unitários (Service): isolam regras de negócio com Mockito (sem Spring Context).

- mvn -Dtest='*ServiceTest' test


2) Controller (slice Web): use @WebMvcTest para testar apenas mapeamentos/validações/status HTTP.
Mocke os serviços com @MockBean e não levante JPA/BD (evita entityManagerFactory).

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = {
DataSourceAutoConfiguration.class,
HibernateJpaAutoConfiguration.class,
JpaRepositoriesAutoConfiguration.class
})


3) Executar:

mvn -Dtest=OrderControllerTest test

--- 

## Estado Atual
- Projeto configurado com Spring Boot + SQL Server.
- Migrações aplicadas (validação em `Order`, histórico de estados).
- Entidades: `Client`, `Order`, `ErrorLog`, `OrderStatusHistory`.
- Serviços: validação externa, logging, relatórios de erros, envio de emails.
- Autenticação JWT concluída (OAuth2 Bearer).
- Endpoints Postman preparados para testar todas as features.


---

## Notas de Clean Code
- Arquitetura em camadas (**Controller → Service → Repository**).
- Separação de responsabilidades clara.
- Configuração centralizada via `application.properties`.
- Tratamento uniforme de erros (400/404/500).
- Integração com serviços externos (mock + real).
- Relatórios automáticos + histórico de estados para auditoria.
- Segurança minimalista e eficaz com JWT.

---

