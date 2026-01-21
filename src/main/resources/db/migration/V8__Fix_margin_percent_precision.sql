-- Fix margin_percent precision in quote_materials and quote_production_activities tables
-- Change from NUMERIC(5,4) to NUMERIC(13,4) to allow values up to 100 million

ALTER TABLE quote_materials 
ALTER COLUMN margin_percent TYPE NUMERIC(13,4);

ALTER TABLE quote_production_activities 
ALTER COLUMN margin_percent TYPE NUMERIC(13,4);