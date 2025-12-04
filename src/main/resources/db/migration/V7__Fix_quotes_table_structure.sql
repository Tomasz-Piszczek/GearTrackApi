-- Drop existing tables
DROP TABLE IF EXISTS quote_production_activities;
DROP TABLE IF EXISTS quote_materials;
DROP TABLE IF EXISTS quotes;

-- Recreate quotes table with proper structure matching BaseEntity
CREATE TABLE quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number VARCHAR(100) NOT NULL,
    contractor_code VARCHAR(50) NOT NULL,
    contractor_name VARCHAR(255) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    min_quantity INTEGER NOT NULL,
    total_quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL
);

-- Recreate quote_materials table with proper structure
CREATE TABLE quote_materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id UUID NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    purchase_price DECIMAL(10,4) NOT NULL,
    margin_percent DECIMAL(5,4) NOT NULL,
    margin_pln DECIMAL(10,4) NOT NULL,
    quantity INTEGER NOT NULL,
    ignore_min_quantity BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL
);

-- Recreate quote_production_activities table with proper structure
CREATE TABLE quote_production_activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id UUID NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    work_time_minutes INTEGER NOT NULL,
    price DECIMAL(10,4) NOT NULL,
    margin_percent DECIMAL(5,4) NOT NULL,
    margin_pln DECIMAL(10,4) NOT NULL,
    ignore_min_quantity BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL
);