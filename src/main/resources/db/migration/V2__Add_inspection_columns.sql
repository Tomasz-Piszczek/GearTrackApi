-- Add missing columns to machine_inspections table
ALTER TABLE machine_inspections 
ADD COLUMN notes TEXT,
ADD COLUMN status VARCHAR(50) DEFAULT 'COMPLETED';

-- Update existing records to have default status
UPDATE machine_inspections SET status = 'COMPLETED' WHERE status IS NULL;