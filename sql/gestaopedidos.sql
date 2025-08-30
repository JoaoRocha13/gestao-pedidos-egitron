/* =========================================================
   PROJETO: Sistema de Gestão de Pedidos (EGITRON)
   AMBIENTE: MSSQL
   OBJETIVO: Criar login, BD, utilizador e esquema de dados
   NOTA: Script idempotente (não duplica objetos)
   ========================================================= */

------------------------------------------------------------
-- 1) LOGIN DE SERVIDOR (para a app)
------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = 'egitron')
BEGIN
    CREATE LOGIN [egitron] WITH PASSWORD = 'egitron123',
        CHECK_POLICY = ON, CHECK_EXPIRATION = OFF;
END;
GO

------------------------------------------------------------
-- 2) BASE DE DADOS
------------------------------------------------------------
IF DB_ID('gestaopedidos') IS NULL
BEGIN
    CREATE DATABASE [gestaopedidos]
        COLLATE SQL_Latin1_General_CP1_CI_AS;
END;
GO

------------------------------------------------------------
-- 3) UTILIZADOR NA BD + PERMISSÕES
------------------------------------------------------------
USE [gestaopedidos];
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'egitron')
BEGIN
    CREATE USER [egitron] FOR LOGIN [egitron];
EXEC sp_addrolemember N'db_datareader', N'egitron';
EXEC sp_addrolemember N'db_datawriter', N'egitron';
    -- Opcional: permitir alterar DDL em dev
    -- EXEC sp_addrolemember N'db_ddladmin', N'egitron';
END;
GO

------------------------------------------------------------
-- 4) TABELAS
------------------------------------------------------------

-- CLIENT
IF OBJECT_ID('dbo.Client', 'U') IS NULL
BEGIN
CREATE TABLE dbo.Client (
                            clientId        INT IDENTITY(1,1) CONSTRAINT PK_Client PRIMARY KEY,
                            name            NVARCHAR(120)  NOT NULL,
                            email           NVARCHAR(255)  NOT NULL,
                            phone           NVARCHAR(30)   NULL,
                            nif             NVARCHAR(20)   NULL,
                            isActive        BIT            NOT NULL CONSTRAINT DF_Client_isActive DEFAULT (1),
                            createdAtUtc    DATETIME2(0)   NOT NULL CONSTRAINT DF_Client_createdAtUtc DEFAULT (SYSUTCDATETIME()),
                            updatedAtUtc    DATETIME2(0)   NULL
);
CREATE UNIQUE INDEX UX_Client_email ON dbo.Client(email);
CREATE INDEX IX_Client_isActive ON dbo.Client(isActive);
END;
GO

-- ORDER
IF OBJECT_ID('dbo.[Order]', 'U') IS NULL
BEGIN
CREATE TABLE dbo.[Order] (
                             orderId         INT IDENTITY(1,1) CONSTRAINT PK_Order PRIMARY KEY,
    clientId        INT           NOT NULL,
    referenceCode   NVARCHAR(40)  NOT NULL,
    title           NVARCHAR(200) NOT NULL,
    description     NVARCHAR(MAX) NULL,
    currentStatus   VARCHAR(20)   NOT NULL,
    totalAmount     DECIMAL(12,2) NOT NULL CONSTRAINT DF_Order_totalAmount DEFAULT (0),
    createdAtUtc    DATETIME2(0)  NOT NULL CONSTRAINT DF_Order_createdAtUtc DEFAULT (SYSUTCDATETIME()),
    updatedAtUtc    DATETIME2(0)  NULL,
    CONSTRAINT FK_Order_Client
    FOREIGN KEY (clientId) REFERENCES dbo.Client(clientId)
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_Order_currentStatus
    CHECK (currentStatus IN ('PENDING','IN_PROGRESS','COMPLETED','CANCELLED','ERROR'))
    );
CREATE UNIQUE INDEX UX_Order_referenceCode ON dbo.[Order](referenceCode);
CREATE INDEX IX_Order_clientId ON dbo.[Order](clientId);
CREATE INDEX IX_Order_currentStatus ON dbo.[Order](currentStatus);
END;
GO

-- ORDER STATUS HISTORY
IF OBJECT_ID('dbo.OrderStatusHistory', 'U') IS NULL
BEGIN
CREATE TABLE dbo.OrderStatusHistory (
                                        historyId       INT IDENTITY(1,1) CONSTRAINT PK_OSH PRIMARY KEY,
                                        orderId         INT          NOT NULL,
                                        status          VARCHAR(20)  NOT NULL,
                                        note            NVARCHAR(400) NULL,
                                        changedAtUtc    DATETIME2(0) NOT NULL CONSTRAINT DF_OSH_changedAtUtc DEFAULT (SYSUTCDATETIME()),
                                        changedBy       NVARCHAR(120) NULL,
                                        CONSTRAINT FK_OSH_Order
                                            FOREIGN KEY (orderId) REFERENCES dbo.[Order](orderId)
                                                ON UPDATE NO ACTION ON DELETE CASCADE,
                                        CONSTRAINT CK_OSH_status
                                            CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED','CANCELLED','ERROR'))
);
CREATE INDEX IX_OSH_orderId ON dbo.OrderStatusHistory(orderId);
CREATE INDEX IX_OSH_status ON dbo.OrderStatusHistory(status);
CREATE INDEX IX_OSH_changedAtUtc ON dbo.OrderStatusHistory(changedAtUtc);
END;
GO

-- ERROR LOG
IF OBJECT_ID('dbo.ErrorLog', 'U') IS NULL
BEGIN
CREATE TABLE dbo.ErrorLog (
                              errorId         BIGINT IDENTITY(1,1) CONSTRAINT PK_ErrorLog PRIMARY KEY,
                              occurredAtUtc   DATETIME2(0)  NOT NULL CONSTRAINT DF_ErrorLog_time DEFAULT (SYSUTCDATETIME()),
                              level           VARCHAR(20)   NOT NULL,   -- INFO/WARN/ERROR
                              source          NVARCHAR(120) NULL,       -- Controller/Service
                              endpoint        NVARCHAR(255) NULL,       -- /orders/{id}
                              message         NVARCHAR(400) NOT NULL,
                              details         NVARCHAR(MAX) NULL
);
CREATE INDEX IX_ErrorLog_level_time ON dbo.ErrorLog(level, occurredAtUtc);
END;
GO

------------------------------------------------------------
-- 5) TRIGGER: manter currentStatus coerente
------------------------------------------------------------
IF OBJECT_ID('dbo.trg_OSH_UpdateOrderStatus', 'TR') IS NOT NULL
DROP TRIGGER dbo.trg_OSH_UpdateOrderStatus;
GO
CREATE TRIGGER dbo.trg_OSH_UpdateOrderStatus
    ON dbo.OrderStatusHistory
    AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
UPDATE o
SET o.currentStatus = i.status,
    o.updatedAtUtc  = SYSUTCDATETIME()
    FROM dbo.[Order] o
    JOIN inserted i ON i.orderId = o.orderId;
END;
GO
