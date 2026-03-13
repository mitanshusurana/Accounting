-- Pre-populate mandatory system ledgers to avoid FK constraint failures
-- during automated Invoice processing and Tax Generation.

INSERT INTO accounts (account_id, name, account_type, ltree_path) VALUES
('REVENUE.Sales', 'Sales A/c', 'REVENUE', 'REVENUE.Sales'),
('EXPENSE.COGS', 'Cost of Goods Sold', 'EXPENSE', 'EXPENSE.COGS'),
('EXPENSE.Purchases', 'Purchases A/c', 'EXPENSE', 'EXPENSE.Purchases'),
('ASSET.Inventory', 'Closing Stock', 'ASSET', 'ASSET.Inventory'),
('LIABILITY.Tax.GST_Output', 'Output GST (Sales)', 'LIABILITY', 'LIABILITY.Tax.GST_Output'),
('ASSET.Tax.GST_Input', 'Input GST Credit (Purchases)', 'ASSET', 'ASSET.Tax.GST_Input')
ON CONFLICT (account_id) DO NOTHING;
