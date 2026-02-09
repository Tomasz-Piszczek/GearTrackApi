CREATE TABLE badania_szkolenia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    date DATE NOT NULL,
    category VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OCZEKUJACY',
    organization_id UUID NOT NULL,
    hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);
