-- Add hidden column to all main entity tables for soft delete functionality
-- The hidden column defaults to FALSE and should be NOT NULL

-- Add hidden column to employees table
ALTER TABLE employees 
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to tools table  
ALTER TABLE tools
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to machines table
ALTER TABLE machines
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to machine_inspections table
ALTER TABLE machine_inspections
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to employee_tools table
ALTER TABLE employee_tools
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to quotes table
ALTER TABLE quotes
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to quote_materials table
ALTER TABLE quote_materials
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to quote_production_activities table
ALTER TABLE quote_production_activities
ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Add hidden column to payroll_records table (if it exists)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'payroll_records') THEN
        ALTER TABLE payroll_records ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;
END $$;

-- Create indexes for better performance on hidden column queries
CREATE INDEX idx_employees_hidden ON employees(hidden);
CREATE INDEX idx_tools_hidden ON tools(hidden);
CREATE INDEX idx_machines_hidden ON machines(hidden);
CREATE INDEX idx_machine_inspections_hidden ON machine_inspections(hidden);
CREATE INDEX idx_employee_tools_hidden ON employee_tools(hidden);
CREATE INDEX idx_quotes_hidden ON quotes(hidden);
CREATE INDEX idx_quote_materials_hidden ON quote_materials(hidden);
CREATE INDEX idx_quote_production_activities_hidden ON quote_production_activities(hidden);

-- Create composite indexes for user_id + hidden for better query performance
CREATE INDEX idx_employees_user_id_hidden ON employees(user_id, hidden);
CREATE INDEX idx_tools_user_id_hidden ON tools(user_id, hidden);
CREATE INDEX idx_machines_user_id_hidden ON machines(user_id, hidden);
CREATE INDEX idx_machine_inspections_user_id_hidden ON machine_inspections(user_id, hidden);
CREATE INDEX idx_employee_tools_user_id_hidden ON employee_tools(user_id, hidden);

-- Note: quotes table doesn't have user_id column, so we only add hidden index
-- payroll_records index will be added conditionally if table exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'payroll_records') THEN
        CREATE INDEX idx_payroll_records_hidden ON payroll_records(hidden);
        -- Check if payroll_records has user_id column for composite index
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'payroll_records' AND column_name = 'user_id') THEN
            CREATE INDEX idx_payroll_records_user_id_hidden ON payroll_records(user_id, hidden);
        END IF;
    END IF;
END $$;