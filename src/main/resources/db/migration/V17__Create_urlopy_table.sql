CREATE TABLE urlopy (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    note TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    organization_id UUID NOT NULL,
    hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);


