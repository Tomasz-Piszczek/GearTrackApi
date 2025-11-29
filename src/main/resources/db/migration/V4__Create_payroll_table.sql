CREATE TABLE payroll_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    hourly_rate NUMERIC(10,2) NOT NULL,
    hours_worked NUMERIC(8,2) DEFAULT 0,
    bonus NUMERIC(10,2) DEFAULT 0,
    sick_leave_pay NUMERIC(10,2) DEFAULT 0,
    deductions NUMERIC(10,2) DEFAULT 0,
    bank_transfer NUMERIC(10,2) DEFAULT 0,
    cash_amount NUMERIC(10,2) DEFAULT 0,
    CONSTRAINT fk_payroll_employee_id FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT unique_employee_month_payroll UNIQUE(employee_id, year, month, user_id)
);

CREATE INDEX idx_payroll_records_employee_id ON payroll_records(employee_id);
CREATE INDEX idx_payroll_records_user_id ON payroll_records(user_id);
CREATE INDEX idx_payroll_records_year_month ON payroll_records(year, month);

CREATE TRIGGER update_payroll_records_updated_at BEFORE UPDATE ON payroll_records
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();