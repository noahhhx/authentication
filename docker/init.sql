-- Create user if not exists
DO
$$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'my_user') THEN
    CREATE USER my_user WITH PASSWORD 'password';
  END IF;
END
$$;

-- Create database if not exists
SELECT 'CREATE DATABASE my_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'my_db')\gexec

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE my_db TO my_user;

-- Connect to the my_db
\c my_db;

-- Grant schema privileges
ALTER DEFAULT PRIVILEGES FOR USER postgres IN SCHEMA public GRANT ALL ON TABLES TO my_user;
GRANT ALL ON SCHEMA public TO my_user;