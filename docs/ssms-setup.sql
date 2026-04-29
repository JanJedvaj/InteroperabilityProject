-- Interoperability project — SQL Server one-time setup.
-- Run this once in SSMS before booting the application for the first time.

CREATE DATABASE BooksDb;
GO

USE BooksDb;
GO

-- Dedicated SQL login used by the application.
-- If your local instance only allows Windows authentication, switch SQL Server
-- to "Mixed Mode" first or change application.yml to use integrated security.
CREATE LOGIN books_user WITH PASSWORD = 'StrongP@ssw0rd!', CHECK_POLICY = OFF;
CREATE USER  books_user FOR LOGIN books_user;
ALTER ROLE db_owner ADD MEMBER books_user;
GO
