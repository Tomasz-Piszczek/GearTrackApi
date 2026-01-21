ALTER TABLE tools DROP COLUMN IF EXISTS user_id;
ALTER TABLE employees DROP COLUMN IF EXISTS user_id;
ALTER TABLE machines DROP COLUMN IF EXISTS user_id;
ALTER TABLE employee_tools DROP COLUMN IF EXISTS user_id;
ALTER TABLE payroll_records DROP COLUMN IF EXISTS user_id;
ALTER TABLE machine_inspections DROP COLUMN IF EXISTS user_id;
ALTER TABLE quote_materials DROP COLUMN IF EXISTS user_id;
ALTER TABLE quote_production_activities DROP COLUMN IF EXISTS user_id;
