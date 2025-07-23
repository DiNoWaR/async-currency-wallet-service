DO $$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'wallet_data') THEN
      CREATE USER wallet_data WITH PASSWORD 'password';
END IF;
END
$$;

CREATE DATABASE wallet_data OWNER wallet_data;
GRANT ALL PRIVILEGES ON DATABASE wallet_data TO wallet_data;
