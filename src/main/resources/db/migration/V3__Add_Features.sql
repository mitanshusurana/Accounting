-- Financial Years
CREATE TABLE IF NOT EXISTS financial_years (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT false
);

-- Units of Measure (UoM)
CREATE TABLE IF NOT EXISTS units_of_measure (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    short_name VARCHAR(50) NOT NULL
);

-- Parties (Suppliers/Customers)
CREATE TABLE IF NOT EXISTS parties (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- CUSTOMER, SUPPLIER, BOTH
    gstin VARCHAR(15),
    address TEXT,
    phone VARCHAR(50),
    email VARCHAR(255)
);

-- Alter Products Table to add barcode, short_code, tax_rate, uom_id
ALTER TABLE products ADD COLUMN IF NOT EXISTS barcode VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS short_code VARCHAR(50);
ALTER TABLE products ADD COLUMN IF NOT EXISTS tax_rate DECIMAL(19, 4) DEFAULT 0.0;
ALTER TABLE products ADD COLUMN IF NOT EXISTS uom_id VARCHAR(255) REFERENCES units_of_measure(id);

-- Purchase Orders
CREATE TABLE IF NOT EXISTS purchase_orders (
    id VARCHAR(255) PRIMARY KEY,
    po_number VARCHAR(255) UNIQUE NOT NULL,
    po_date DATE NOT NULL,
    supplier_id VARCHAR(255) REFERENCES parties(id),
    total_amount DECIMAL(19, 4) DEFAULT 0.0,
    status VARCHAR(50) NOT NULL -- DRAFT, ISSUED, COMPLETED, CANCELLED
);

CREATE TABLE IF NOT EXISTS purchase_order_lines (
    id VARCHAR(255) PRIMARY KEY,
    po_id VARCHAR(255) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    product_id VARCHAR(255) REFERENCES products(product_id),
    quantity DECIMAL(19, 4) NOT NULL,
    unit_price DECIMAL(19, 4) NOT NULL,
    tax_rate DECIMAL(19, 4) DEFAULT 0.0,
    tax_amount DECIMAL(19, 4) DEFAULT 0.0,
    total_amount DECIMAL(19, 4) NOT NULL
);

-- Invoices (Sales & Purchases)
CREATE TABLE IF NOT EXISTS invoices (
    id VARCHAR(255) PRIMARY KEY,
    invoice_number VARCHAR(255) UNIQUE NOT NULL,
    invoice_date DATE NOT NULL,
    party_id VARCHAR(255) REFERENCES parties(id),
    type VARCHAR(50) NOT NULL, -- SALES, PURCHASE
    total_amount DECIMAL(19, 4) DEFAULT 0.0,
    status VARCHAR(50) NOT NULL, -- DRAFT, SUBMITTED, CANCELLED
    linked_po_id VARCHAR(255) REFERENCES purchase_orders(id),
    other_charges DECIMAL(19, 4) DEFAULT 0.0,
    gst_payable DECIMAL(19, 4) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS invoice_lines (
    id VARCHAR(255) PRIMARY KEY,
    invoice_id VARCHAR(255) REFERENCES invoices(id) ON DELETE CASCADE,
    product_id VARCHAR(255) REFERENCES products(product_id),
    quantity DECIMAL(19, 4) NOT NULL,
    unit_price DECIMAL(19, 4) NOT NULL,
    tax_rate DECIMAL(19, 4) DEFAULT 0.0,
    tax_amount DECIMAL(19, 4) DEFAULT 0.0,
    total_amount DECIMAL(19, 4) NOT NULL
);

-- Credit / Debit Notes
CREATE TABLE IF NOT EXISTS credit_debit_notes (
    id VARCHAR(255) PRIMARY KEY,
    note_number VARCHAR(255) UNIQUE NOT NULL,
    note_date DATE NOT NULL,
    invoice_id VARCHAR(255) REFERENCES invoices(id),
    type VARCHAR(50) NOT NULL, -- CREDIT, DEBIT, REPLACEMENT
    amount DECIMAL(19, 4) NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING'
);

-- Default ledgers for Cash and Bank
INSERT INTO accounts (account_id, name, account_type, ltree_path) VALUES
('ASSET.Cash.Main', 'Main Cash A/c', 'ASSET', 'ASSET.Cash.Main'),
('ASSET.Bank.Main', 'Main Bank A/c', 'ASSET', 'ASSET.Bank.Main')
ON CONFLICT (account_id) DO NOTHING;

-- Apply Audit Triggers for MCA mandate to new tables
DROP TRIGGER IF EXISTS audit_invoices_trigger ON invoices;
CREATE TRIGGER audit_invoices_trigger
    AFTER INSERT OR UPDATE OR DELETE ON invoices
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

DROP TRIGGER IF EXISTS audit_purchase_orders_trigger ON purchase_orders;
CREATE TRIGGER audit_purchase_orders_trigger
    AFTER INSERT OR UPDATE OR DELETE ON purchase_orders
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

DROP TRIGGER IF EXISTS audit_credit_debit_notes_trigger ON credit_debit_notes;
CREATE TRIGGER audit_credit_debit_notes_trigger
    AFTER INSERT OR UPDATE OR DELETE ON credit_debit_notes
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();
