-- Create users table
CREATE TABLE users (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    last_password_reset_at TIMESTAMP NULL,
    reset_password_token TEXT NULL,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0
);

-- Create employees table
CREATE TABLE employees (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    hourly_rate NUMERIC(10,2) NULL
);

-- Create machines table
CREATE TABLE machines (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    name TEXT NOT NULL,
    factory_number TEXT NULL,
    employee_id UUID NULL
);

-- Create machine_inspections table
CREATE TABLE machine_inspections (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    inspection_date DATE NOT NULL,
    performed_by TEXT NOT NULL,
    machine_id UUID NOT NULL
);

-- Create tools table
CREATE TABLE tools (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    name TEXT NOT NULL,
    factory_number TEXT NULL,
    size TEXT NULL,
    quantity INTEGER NULL,
    value NUMERIC(10,2) NULL
);

-- Create employee_tools table (many-to-many relationship)
CREATE TABLE employee_tools (
    uuid UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    tool_id UUID NOT NULL,
    assigned_at DATE NOT NULL,
    quantity INTEGER NOT NULL,
    condition TEXT NOT NULL
);

-- Add foreign key constraints
ALTER TABLE machines ADD CONSTRAINT fk_machines_employee_id 
    FOREIGN KEY (employee_id) REFERENCES employees(uuid);

ALTER TABLE machine_inspections ADD CONSTRAINT fk_machine_inspections_machine_id 
    FOREIGN KEY (machine_id) REFERENCES machines(uuid);

ALTER TABLE employee_tools ADD CONSTRAINT fk_employee_tools_employee_id 
    FOREIGN KEY (employee_id) REFERENCES employees(uuid);

ALTER TABLE employee_tools ADD CONSTRAINT fk_employee_tools_tool_id 
    FOREIGN KEY (tool_id) REFERENCES tools(uuid);

-- Create indexes for better performance
CREATE INDEX idx_machines_employee_id ON machines(employee_id);
CREATE INDEX idx_machine_inspections_machine_id ON machine_inspections(machine_id);
CREATE INDEX idx_employee_tools_employee_id ON employee_tools(employee_id);
CREATE INDEX idx_employee_tools_tool_id ON employee_tools(tool_id);
CREATE INDEX idx_users_email ON users(email);

-- Create indexes for user_id columns for data isolation
CREATE INDEX idx_employees_user_id ON employees(user_id);
CREATE INDEX idx_machines_user_id ON machines(user_id);
CREATE INDEX idx_machine_inspections_user_id ON machine_inspections(user_id);
CREATE INDEX idx_tools_user_id ON tools(user_id);
CREATE INDEX idx_employee_tools_user_id ON employee_tools(user_id);

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update updated_at for all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_employees_updated_at BEFORE UPDATE ON employees
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_machines_updated_at BEFORE UPDATE ON machines
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_machine_inspections_updated_at BEFORE UPDATE ON machine_inspections
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tools_updated_at BEFORE UPDATE ON tools
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_employee_tools_updated_at BEFORE UPDATE ON employee_tools
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();