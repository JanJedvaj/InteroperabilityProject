-- Interoperability project — SQL Server one-time setup.
-- Idempotent: safe to re-run. Requires Mixed Mode authentication enabled
-- on the SQL Server instance (Server → Properties → Security →
-- "SQL Server and Windows Authentication mode", then restart the service).

IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = N'BooksDb')
    CREATE DATABASE BooksDb;
GO

USE BooksDb;
GO

IF EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'books_user')
    DROP USER books_user;
GO

USE master;
GO

IF EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'books_user')
    DROP LOGIN books_user;
GO

CREATE LOGIN books_user
    WITH PASSWORD       = 'StrongP@ssw0rd!',
         CHECK_POLICY    = OFF,
         CHECK_EXPIRATION = OFF;
GO

USE BooksDb;
GO

CREATE USER books_user FOR LOGIN books_user;
ALTER ROLE db_owner ADD MEMBER books_user;
GO
