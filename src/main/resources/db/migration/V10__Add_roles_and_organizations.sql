-- Create organizations table
CREATE TABLE organizations (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    organization_name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);

-- Add role column to users table
ALTER TABLE users ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

-- Add organization_id column to users table  
ALTER TABLE users ADD COLUMN organization_id UUID;

-- Add foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

-- Add organization_id column to all existing tables that extend OrganizationalEntity
ALTER TABLE quotes ADD COLUMN organization_id UUID;
ALTER TABLE tools ADD COLUMN organization_id UUID;
ALTER TABLE machines ADD COLUMN organization_id UUID;
ALTER TABLE employees ADD COLUMN organization_id UUID;
ALTER TABLE employee_tools ADD COLUMN organization_id UUID;
ALTER TABLE payroll_records ADD COLUMN organization_id UUID;
ALTER TABLE machine_inspections ADD COLUMN organization_id UUID;
ALTER TABLE quote_materials ADD COLUMN organization_id UUID;
ALTER TABLE quote_production_activities ADD COLUMN organization_id UUID;

-- Create indexes for better performance
CREATE INDEX idx_users_organization_id ON users (organization_id);
CREATE INDEX idx_quotes_organization_id ON quotes (organization_id);
CREATE INDEX idx_tools_organization_id ON tools (organization_id);
CREATE INDEX idx_machines_organization_id ON machines (organization_id);
CREATE INDEX idx_employees_organization_id ON employees (organization_id);
CREATE INDEX idx_employee_tools_organization_id ON employee_tools (organization_id);
CREATE INDEX idx_payroll_records_organization_id ON payroll_records (organization_id);
CREATE INDEX idx_machine_inspections_organization_id ON machine_inspections (organization_id);
CREATE INDEX idx_quote_materials_organization_id ON quote_materials (organization_id);
CREATE INDEX idx_quote_production_activities_organization_id ON quote_production_activities (organization_id);