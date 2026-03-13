-- Extension for hierarchical structure
CREATE EXTENSION IF NOT EXISTS ltree;

-- Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    ltree_path ltree
);

-- Journal Entries Table
CREATE TABLE IF NOT EXISTS journal_entries (
    entry_id VARCHAR(255) PRIMARY KEY,
    transaction_date DATE NOT NULL,
    voucher_type VARCHAR(255),
    narration TEXT
);

-- Postings Table
CREATE TABLE IF NOT EXISTS postings (
    posting_id VARCHAR(255) PRIMARY KEY,
    entry_id VARCHAR(255) REFERENCES journal_entries(entry_id),
    account_id VARCHAR(255) REFERENCES accounts(account_id),
    debit_amount DECIMAL(19, 4) DEFAULT 0.0,
    credit_amount DECIMAL(19, 4) DEFAULT 0.0
);

-- Posting Cost Allocations Table
CREATE TABLE IF NOT EXISTS posting_cost_allocations (
    posting_id VARCHAR(255) REFERENCES postings(posting_id),
    cost_center_id VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL
);

-- Posting Bill Allocations Table
CREATE TABLE IF NOT EXISTS posting_bill_allocations (
    posting_id VARCHAR(255) REFERENCES postings(posting_id),
    invoice_ref VARCHAR(255),
    amount DECIMAL(19, 4),
    type VARCHAR(50)
);

-- Products Table
CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(255) PRIMARY KEY,
    sku VARCHAR(255),
    name VARCHAR(255),
    hsn_code VARCHAR(255),
    default_price DECIMAL(19, 4)
);

-- Godowns Table
CREATE TABLE IF NOT EXISTS godowns (
    godown_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    location VARCHAR(255)
);

-- Stock Movements Table
CREATE TABLE IF NOT EXISTS stock_movements (
    movement_id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255),
    godown_id VARCHAR(255),
    batch_id VARCHAR(255),
    quantity DECIMAL(19, 4),
    available_quantity DECIMAL(19, 4),
    direction VARCHAR(50),
    unit_cost DECIMAL(19, 4),
    movement_date DATE
);

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

-- Audit Log Table for MCA mandate
CREATE TABLE IF NOT EXISTS audit_log (
    record_id SERIAL PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    action VARCHAR(50) NOT NULL,
    old_data JSONB,
    new_data JSONB,
    performed_by VARCHAR(255) DEFAULT CURRENT_USER,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PL/pgSQL Trigger Function for Audit Trail
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO audit_log (table_name, action, old_data, new_data)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD)::jsonb, NULL);
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO audit_log (table_name, action, old_data, new_data)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD)::jsonb, row_to_json(NEW)::jsonb);
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO audit_log (table_name, action, old_data, new_data)
        VALUES (TG_TABLE_NAME, TG_OP, NULL, row_to_json(NEW)::jsonb);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Apply Triggers
DROP TRIGGER IF EXISTS audit_journal_entries_trigger ON journal_entries;
CREATE TRIGGER audit_journal_entries_trigger
    AFTER INSERT OR UPDATE OR DELETE ON journal_entries
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

DROP TRIGGER IF EXISTS audit_postings_trigger ON postings;
CREATE TRIGGER audit_postings_trigger
    AFTER INSERT OR UPDATE OR DELETE ON postings
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- DCL Scripts for Strict Immutability
-- Ensure application user only has INSERT privileges on audit_log
-- Note: In a production environment, 'app_user' must be created
-- by the DBA prior to application startup with a secure password.
GRANT INSERT ON audit_log TO current_user;
REVOKE UPDATE, DELETE, TRUNCATE ON audit_log FROM current_user;
