-- Update constraints to use organization_id instead of user_id for organizational entities
-- Note: These are tables that extend OrganizationalEntity and should be organization-scoped

-- First, set organization_id to NOT NULL for organizational entities
-- (assuming all existing data should belong to the same organization for now)

-- Make organization_id NOT NULL for all organizational entity tables
ALTER TABLE employees ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE tools ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE machines ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE quotes ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE employee_tools ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE payroll_records ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE machine_inspections ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE quote_materials ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE quote_production_activities ALTER COLUMN organization_id SET NOT NULL;

-- Drop user_id constraints for organizational entities (but keep for users and organizations tables)
ALTER TABLE employees ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE tools ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE machines ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE quotes ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE employee_tools ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE payroll_records ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE machine_inspections ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE quote_materials ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE quote_production_activities ALTER COLUMN user_id DROP NOT NULL;

-- Add foreign key constraints for organization_id
ALTER TABLE employees ADD CONSTRAINT fk_employees_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE tools ADD CONSTRAINT fk_tools_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE machines ADD CONSTRAINT fk_machines_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE quotes ADD CONSTRAINT fk_quotes_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE employee_tools ADD CONSTRAINT fk_employee_tools_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE payroll_records ADD CONSTRAINT fk_payroll_records_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE machine_inspections ADD CONSTRAINT fk_machine_inspections_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE quote_materials ADD CONSTRAINT fk_quote_materials_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE quote_production_activities ADD CONSTRAINT fk_quote_production_activities_organization 
    FOREIGN KEY (organization_id) REFERENCES organizations (id);