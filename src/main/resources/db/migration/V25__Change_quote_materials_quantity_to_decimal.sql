ALTER TABLE quote_materials ALTER COLUMN quantity TYPE DECIMAL(10,4) USING quantity::DECIMAL(10,4);
