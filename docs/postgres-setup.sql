-- Run once as a superuser (e.g. postgres) in DataGrip or psql.
-- Creates the database and user for the Books application.

-- In DataGrip: connect as postgres, run the three statements below one by one.
-- In psql: \i docs/postgres-setup.sql

CREATE DATABASE booksdb;
CREATE USER books_user WITH PASSWORD 'StrongP@ssw0rd!';
GRANT ALL PRIVILEGES ON DATABASE booksdb TO books_user;
