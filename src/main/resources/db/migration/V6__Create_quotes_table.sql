CREATE TABLE quotes (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number VARCHAR(100) NOT NULL,
    contractor_code VARCHAR(50) NOT NULL,
    contractor_name VARCHAR(255) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    min_quantity INTEGER NOT NULL,
    total_quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quote_materials (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id UUID REFERENCES quotes(uuid) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    purchase_price DECIMAL(10,4) NOT NULL,
    margin_percent DECIMAL(5,4) NOT NULL,
    margin_pln DECIMAL(10,4) NOT NULL,
    quantity INTEGER NOT NULL,
    ignore_min_quantity BOOLEAN DEFAULT FALSE
);

CREATE TABLE quote_production_activities (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id UUID REFERENCES quotes(uuid) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    work_time_minutes INTEGER NOT NULL,
    price DECIMAL(10,4) NOT NULL,
    margin_percent DECIMAL(5,4) NOT NULL,
    margin_pln DECIMAL(10,4) NOT NULL,
    ignore_min_quantity BOOLEAN DEFAULT FALSE
);