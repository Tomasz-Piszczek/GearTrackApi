CREATE TABLE payroll_deductions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_record_id UUID NOT NULL,
    category VARCHAR(255) NOT NULL,
    note TEXT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    organization_id UUID NOT NULL,
    hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payroll_record_id) REFERENCES payroll_records(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);