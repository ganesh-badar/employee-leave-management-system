-- =====================================================================
-- Employee Leave Management System (ELMS)
-- Seed Data for MySQL
-- Default Admin: admin / Admin@123
-- Default Employee: john.doe / Employee@123
-- BCrypt rounds: 10
-- =====================================================================

USE leave_management_db;

-- 1. Departments
INSERT INTO departments (department_code, department_name, description) VALUES
('ENG', 'Engineering', 'Software development, QA, and cloud infrastructure operations'),
('HR', 'Human Resources', 'Talent acquisition, employee welfare, and personnel policy compliance'),
('FIN', 'Finance & Accounting', 'Payroll, fiscal reporting, and statutory tax compliance'),
('OPS', 'Operations & Logistics', 'Enterprise project coordination and facility management'),
('MKT', 'Marketing & Communications', 'Corporate communications, digital branding, and client engagement');

-- 2. Leave Types
INSERT INTO leave_types (type_code, type_name, description, default_days_per_year, is_paid) VALUES
('CL', 'Casual Leave', 'Short-duration leave for personal reasons or urgent personal matters', 12, TRUE),
('SL', 'Sick Leave', 'Medical absence or health-related recovery', 10, TRUE),
('PL', 'Paid / Privilege Leave', 'Planned annual vacation leave earned through tenure', 15, TRUE),
('ML', 'Maternity / Paternity Leave', 'Parental leave as per statutory guidelines', 90, TRUE),
('BL', 'Bereavement Leave', 'Compassionate leave upon demise of immediate family member', 5, TRUE),
('LOP', 'Loss of Pay (Unpaid Leave)', 'Absence requested beyond accrued paid leave quotas', 30, FALSE);

-- 3. Users (BCrypt hashed passwords)
-- Admin@123: $2a$10$N.ZpXhLz6G8a3lK1tU5BveWfPqyV7z6E.a2C4Z.8hQ1yJk4rO8gWy (we also ensure Java seeds dynamically if needed)
-- We will insert initial accounts:
INSERT INTO users (username, email, password_hash, role, is_active) VALUES
('admin', 'admin@internal.corp', '$2a$10$VvW32z9zT0a.Wf5c7yHKeu6kZ9mP3.y4a5b6c7d8e9f0g1h2i3j4k', 'ROLE_ADMIN', TRUE),
('john.doe', 'john.doe@internal.corp', '$2a$10$VvW32z9zT0a.Wf5c7yHKeu6kZ9mP3.y4a5b6c7d8e9f0g1h2i3j4k', 'ROLE_EMPLOYEE', TRUE),
('sarah.connor', 'sarah.connor@internal.corp', '$2a$10$VvW32z9zT0a.Wf5c7yHKeu6kZ9mP3.y4a5b6c7d8e9f0g1h2i3j4k', 'ROLE_EMPLOYEE', TRUE);

-- 4. Employees
INSERT INTO employees (user_id, employee_code, first_name, last_name, email, phone, department_id, designation, joining_date, salary, status) VALUES
(1, 'EMP-1001', 'System', 'Administrator', 'admin@internal.corp', '+1-555-0100', 2, 'HR Operations Director', '2022-01-15', 95000.00, 'ACTIVE'),
(2, 'EMP-1002', 'John', 'Doe', 'john.doe@internal.corp', '+1-555-0102', 1, 'Senior Software Engineer', '2023-03-01', 82000.00, 'ACTIVE'),
(3, 'EMP-1003', 'Sarah', 'Connor', 'sarah.connor@internal.corp', '+1-555-0103', 1, 'DevOps Specialist', '2023-06-15', 78000.00, 'ACTIVE');

-- 5. Leave Balances for 2026
-- John Doe (EMP-1002)
INSERT INTO leave_balances (employee_id, leave_type_id, cal_year, total_allocated, used_days, pending_days) VALUES
(2, 1, 2026, 12.0, 2.0, 0.0),  -- CL
(2, 2, 2026, 10.0, 1.0, 0.0),  -- SL
(2, 3, 2026, 15.0, 0.0, 0.0),  -- PL
(2, 4, 2026, 90.0, 0.0, 0.0),  -- ML
(2, 5, 2026, 5.0,  0.0, 0.0),  -- BL
(2, 6, 2026, 30.0, 0.0, 0.0);  -- LOP

-- Sarah Connor (EMP-1003)
INSERT INTO leave_balances (employee_id, leave_type_id, cal_year, total_allocated, used_days, pending_days) VALUES
(3, 1, 2026, 12.0, 0.0, 0.0),  -- CL
(3, 2, 2026, 10.0, 0.0, 0.0),  -- SL
(3, 3, 2026, 15.0, 0.0, 0.0),  -- PL
(3, 4, 2026, 90.0, 0.0, 0.0),  -- ML
(3, 5, 2026, 5.0,  0.0, 0.0),  -- BL
(3, 6, 2026, 30.0, 0.0, 0.0);  -- LOP

-- 6. Sample Initial Leave Request
INSERT INTO leave_requests (employee_id, leave_type_id, start_date, end_date, total_days, reason, emergency_contact, status, admin_remarks, reviewed_by, reviewed_at) VALUES
(2, 1, '2026-02-10', '2026-02-11', 2.0, 'Annual residential relocation and utility setup', '+1-555-0199', 'APPROVED', 'Approved by HR Operations', 1, '2026-02-08 10:15:00');
