-- Inclusive Finance Platform - Database Initialization
-- Creates all tables and inserts seed data

CREATE DATABASE IF NOT EXISTS inclusive_finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inclusive_finance;

-- 1. Enterprise information table
DROP TABLE IF EXISTS credit_score;
DROP TABLE IF EXISTS overdue;
DROP TABLE IF EXISTS repayment;
DROP TABLE IF EXISTS loan_apply;
DROP TABLE IF EXISTS user_info;
DROP TABLE IF EXISTS enterprise;

CREATE TABLE enterprise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT 'Enterprise name',
    credit_code VARCHAR(50) UNIQUE COMMENT 'Unified social credit code',
    legal_person VARCHAR(50) COMMENT 'Legal representative',
    legal_id_card VARCHAR(18) COMMENT 'Legal person ID card',
    contact_phone VARCHAR(20) COMMENT 'Contact phone',
    address VARCHAR(255) COMMENT 'Registered address',
    industry VARCHAR(50) COMMENT 'Industry',
    registered_capital DECIMAL(15,2) COMMENT 'Registered capital (10k CNY)',
    establish_date DATE COMMENT 'Establishment date',
    employee_count INT COMMENT 'Employee count',
    annual_revenue DECIMAL(15,2) COMMENT 'Annual revenue (10k CNY)',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1=active 0=disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT 'Enterprise information';

-- 2. User table
CREATE TABLE user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT COMMENT 'Associated enterprise ID',
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) COMMENT 'Real name',
    phone VARCHAR(20) COMMENT 'Phone number',
    role VARCHAR(20) DEFAULT 'ENTERPRISE' COMMENT 'Role: ENTERPRISE/APPROVER/ADMIN',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) COMMENT 'User information';

-- 3. Loan application table
CREATE TABLE loan_apply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT NOT NULL COMMENT 'Enterprise ID',
    user_id BIGINT NOT NULL COMMENT 'Applicant user ID',
    loan_amount DECIMAL(15,2) NOT NULL COMMENT 'Loan amount',
    loan_term INT NOT NULL COMMENT 'Loan term (months)',
    loan_purpose VARCHAR(255) COMMENT 'Loan purpose',
    interest_rate DECIMAL(5,4) COMMENT 'Annual interest rate',
    repayment_method VARCHAR(20) COMMENT 'Repayment method',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/GRANTED/REPAID/OVERDUE',
    apply_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    approve_date DATETIME COMMENT 'Approval date',
    approve_user_id BIGINT COMMENT 'Approver user ID',
    approve_comment VARCHAR(500) COMMENT 'Approval comment',
    credit_score DECIMAL(5,2) COMMENT 'AI credit score',
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id),
    FOREIGN KEY (user_id) REFERENCES user_info(id)
) COMMENT 'Loan application';

-- 4. Repayment record table
CREATE TABLE repayment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL COMMENT 'Loan application ID',
    period_no INT NOT NULL COMMENT 'Repayment period number',
    amount DECIMAL(15,2) NOT NULL COMMENT 'Amount due',
    paid_amount DECIMAL(15,2) COMMENT 'Amount paid',
    due_date DATE NOT NULL COMMENT 'Due date',
    paid_date DATE COMMENT 'Actual payment date',
    status VARCHAR(20) DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/OVERDUE',
    FOREIGN KEY (loan_id) REFERENCES loan_apply(id)
) COMMENT 'Repayment record';

-- 5. Overdue record table
CREATE TABLE overdue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    enterprise_id BIGINT NOT NULL,
    overdue_days INT NOT NULL COMMENT 'Overdue days',
    overdue_amount DECIMAL(15,2) NOT NULL COMMENT 'Overdue amount',
    penalty DECIMAL(15,2) DEFAULT 0 COMMENT 'Penalty interest',
    start_date DATE NOT NULL,
    end_date DATE COMMENT 'Settlement date',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/SETTLED',
    FOREIGN KEY (loan_id) REFERENCES loan_apply(id),
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) COMMENT 'Overdue record';

-- 6. Credit score table
CREATE TABLE credit_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT NOT NULL,
    score DECIMAL(5,2) NOT NULL COMMENT 'Credit score (0-100)',
    model_version VARCHAR(20) COMMENT 'Model version',
    features TEXT COMMENT 'Feature values (JSON)',
    evaluated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) COMMENT 'Enterprise credit score';

-- ============================================================
-- Seed Data
-- ============================================================

-- Enterprises
INSERT INTO enterprise (id, name, credit_code, legal_person, legal_id_card, contact_phone, address, industry, registered_capital, establish_date, employee_count, annual_revenue) VALUES
(1, '星辉科技有限公司', '91110108MA01ABCD1X', '张三', '110101199001011234', '13800001111', '北京市海淀区中关村科技园A座1001', '信息技术', 1000.00, '2018-03-15', 120, 3000.00),
(2, '绿源生态农业有限公司', '91320583MA01EFGH2Y', '李四', '320501198506152345', '13900002222', '江苏省苏州市工业园区B座2002', '农业', 500.00, '2019-07-20', 80, 1200.00),
(3, '鼎新机械制造股份有限公司', '91440101MA01IJKL3Z', '王五', '440101197803016789', '13700003333', '广东省广州市黄埔区科学城C座3003', '制造业', 2000.00, '2015-01-10', 250, 8000.00),
(4, '悦动文化传媒有限公司', '91330100MA01MNOP4W', '赵六', '330101199208089012', '13600004444', '浙江省杭州市滨江区文创园D座4004', '文化传媒', 300.00, '2021-11-08', 35, 600.00),
(5, '鹏程建筑工程有限公司', '91510100MA01QRST5V', '孙七', '510101198912013456', '13500005555', '四川省成都市高新区天府大道E座5005', '建筑业', 1500.00, '2016-06-25', 180, 5000.00);

-- Users (password is BCrypt hash of '123456')
INSERT INTO user_info (id, enterprise_id, username, password, real_name, phone, role) VALUES
(1, 1, 'enterprise01', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '张三', '13800001111', 'ENTERPRISE'),
(2, 2, 'enterprise02', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '李四', '13900002222', 'ENTERPRISE'),
(3, 3, 'enterprise03', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '王五', '13700003333', 'ENTERPRISE'),
(4, 4, 'enterprise04', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '赵六', '13600004444', 'ENTERPRISE'),
(5, 5, 'enterprise05', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '孙七', '13500005555', 'ENTERPRISE'),
(6, NULL, 'admin', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '管理员', '13000000001', 'ADMIN'),
(7, NULL, 'approver01', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '审批员王', '13000000002', 'APPROVER'),
(8, NULL, 'approver02', '$2b$10$hXzcqdlGRKOUi5ASPCKxc.wUKg.1DXQFzdZYRT0.1UfoVFH.Y/5ca', '审批员李', '13000000003', 'APPROVER');

-- Loan applications
INSERT INTO loan_apply (id, enterprise_id, user_id, loan_amount, loan_term, loan_purpose, interest_rate, repayment_method, status, apply_date, approve_date, approve_user_id, approve_comment, credit_score) VALUES
(1, 1, 1, 500000.00, 12, '扩大生产线', 0.0435, 'EQUAL_INSTALLMENT', 'GRANTED', '2026-01-15', '2026-01-18', 7, '企业信用良好，风险可控', 78.50),
(2, 1, 1, 200000.00, 6, '采购原材料', 0.0400, 'EQUAL_PRINCIPAL', 'APPROVED', '2026-03-20', '2026-03-22', 7, '短期周转，经营稳定', 82.00),
(3, 2, 2, 300000.00, 24, '扩建温室大棚', 0.0450, 'EQUAL_INSTALLMENT', 'GRANTED', '2026-02-10', '2026-02-14', 8, '农业扶持项目，优先放款', 75.00),
(4, 2, 2, 150000.00, 12, '购买农机设备', 0.0420, 'EQUAL_PRINCIPAL', 'PENDING', '2026-04-01', NULL, NULL, NULL, NULL),
(5, 3, 3, 1000000.00, 36, '新厂房建设', 0.0480, 'EQUAL_INSTALLMENT', 'GRANTED', '2025-11-05', '2025-11-10', 7, '制造业龙头，长期合作客户', 90.00),
(6, 3, 3, 500000.00, 12, '设备升级改造', 0.0435, 'EQUAL_PRINCIPAL', 'REJECTED', '2026-03-15', '2026-03-18', 8, '近期负债率偏高，暂缓审批', 62.00),
(7, 4, 4, 100000.00, 6, '影视项目启动资金', 0.0500, 'EQUAL_INSTALLMENT', 'APPROVED', '2026-04-10', '2026-04-12', 7, '项目前景良好，同意放款', 71.00),
(8, 4, 4, 80000.00, 3, '设备租赁', 0.0450, 'EQUAL_PRINCIPAL', 'PENDING', '2026-05-01', NULL, NULL, NULL, NULL),
(9, 5, 5, 800000.00, 24, '新项目启动资金', 0.0460, 'EQUAL_INSTALLMENT', 'GRANTED', '2025-12-20', '2025-12-25', 8, '建筑行业优质企业', 85.00),
(10, 5, 5, 300000.00, 12, '材料采购', 0.0435, 'EQUAL_PRINCIPAL', 'PENDING', '2026-04-25', NULL, NULL, NULL, NULL),
(11, 1, 1, 100000.00, 3, '短期周转', 0.0380, 'EQUAL_INSTALLMENT', 'REPAID', '2025-10-01', '2025-10-03', 7, '小额短期，快速审批', 80.00),
(12, 3, 3, 600000.00, 18, '技术改造', 0.0440, 'EQUAL_INSTALLMENT', 'GRANTED', '2026-01-08', '2026-01-12', 7, '', 88.00);

-- Repayment records (for GRANTED loans: 1, 3, 5, 9, 11, 12)
-- Loan 1: 500k, 12mo, 0.0435, EQUAL_INSTALLMENT
INSERT INTO repayment (loan_id, period_no, amount, paid_amount, due_date, paid_date, status) VALUES
(1, 1, 42612.50, 42612.50, '2026-02-15', '2026-02-14', 'PAID'),
(1, 2, 42612.50, 42612.50, '2026-03-15', '2026-03-14', 'PAID'),
(1, 3, 42612.50, 42612.50, '2026-04-15', '2026-04-14', 'PAID'),
(1, 4, 42612.50, NULL, '2026-05-15', NULL, 'UNPAID');

-- Loan 3: 300k, 24mo, 0.045, EQUAL_INSTALLMENT
INSERT INTO repayment (loan_id, period_no, amount, paid_amount, due_date, paid_date, status) VALUES
(3, 1, 15645.00, 15645.00, '2026-03-10', '2026-03-09', 'PAID'),
(3, 2, 15645.00, 15645.00, '2026-04-10', '2026-04-09', 'PAID'),
(3, 3, 15645.00, NULL, '2026-05-10', NULL, 'UNPAID');

-- Loan 5: 1M, 36mo, 0.048, EQUAL_INSTALLMENT
INSERT INTO repayment (loan_id, period_no, amount, paid_amount, due_date, paid_date, status) VALUES
(5, 1, 31336.00, 31336.00, '2025-12-05', '2025-12-04', 'PAID'),
(5, 2, 31336.00, 31336.00, '2026-01-05', '2026-01-04', 'PAID'),
(5, 3, 31336.00, 31336.00, '2026-02-05', '2026-02-04', 'PAID'),
(5, 4, 31336.00, 31336.00, '2026-03-05', '2026-03-04', 'PAID'),
(5, 5, 31336.00, 31336.00, '2026-04-05', '2026-04-04', 'PAID'),
(5, 6, 31336.00, NULL, '2026-05-05', NULL, 'UNPAID');

-- Loan 9: 800k, 24mo, 0.046, EQUAL_INSTALLMENT
INSERT INTO repayment (loan_id, period_no, amount, paid_amount, due_date, paid_date, status) VALUES
(9, 1, 37520.00, 37520.00, '2026-01-20', '2026-01-19', 'PAID'),
(9, 2, 37520.00, 37520.00, '2026-02-20', '2026-02-19', 'PAID'),
(9, 3, 37520.00, 37520.00, '2026-03-20', '2026-03-19', 'PAID'),
(9, 4, 37520.00, NULL, '2026-04-20', NULL, 'UNPAID');

-- Loan 11: 100k, 3mo, 0.038, EQUAL_INSTALLMENT (fully repaid)
INSERT INTO repayment (loan_id, period_no, amount, paid_amount, due_date, paid_date, status) VALUES
(11, 1, 33780.00, 33780.00, '2025-11-01', '2025-10-31', 'PAID'),
(11, 2, 33780.00, 33780.00, '2025-12-01', '2025-11-30', 'PAID'),
(11, 3, 33780.00, 33780.00, '2026-01-01', '2025-12-31', 'PAID');

-- Loan 12: 600k, 18mo, 0.044, EQUAL_INSTALLMENT
INSERT INTO repayment (loan_id, period_no, amount, paid_amount, due_date, paid_date, status) VALUES
(12, 1, 35670.00, 35670.00, '2026-02-08', '2026-02-07', 'PAID'),
(12, 2, 35670.00, 35670.00, '2026-03-08', '2026-03-07', 'PAID'),
(12, 3, 35670.00, NULL, '2026-04-08', NULL, 'OVERDUE');

-- Overdue records
INSERT INTO overdue (loan_id, enterprise_id, overdue_days, overdue_amount, penalty, start_date, status) VALUES
(12, 3, 33, 35670.00, 535.05, '2026-04-08', 'ACTIVE');

-- Credit scores
INSERT INTO credit_score (enterprise_id, score, model_version, features, evaluated_at) VALUES
(1, 78.50, 'v1.0', '{"registeredCapital":1000,"employeeCount":120,"annualRevenue":3000,"establishYears":8,"industry":"信息技术","previousLoans":3,"previousOverdues":0,"debtRatio":0.35}', '2026-01-15'),
(1, 82.00, 'v1.0', '{"registeredCapital":1000,"employeeCount":125,"annualRevenue":3200,"establishYears":8,"industry":"信息技术","previousLoans":4,"previousOverdues":0,"debtRatio":0.30}', '2026-03-20'),
(2, 75.00, 'v1.0', '{"registeredCapital":500,"employeeCount":80,"annualRevenue":1200,"establishYears":7,"industry":"农业","previousLoans":1,"previousOverdues":0,"debtRatio":0.25}', '2026-02-10'),
(3, 90.00, 'v1.0', '{"registeredCapital":2000,"employeeCount":250,"annualRevenue":8000,"establishYears":11,"industry":"制造业","previousLoans":5,"previousOverdues":0,"debtRatio":0.40}', '2025-11-05'),
(3, 62.00, 'v1.0', '{"registeredCapital":2000,"employeeCount":250,"annualRevenue":7800,"establishYears":11,"industry":"制造业","previousLoans":6,"previousOverdues":0,"debtRatio":0.55}', '2026-03-15'),
(4, 71.00, 'v1.0', '{"registeredCapital":300,"employeeCount":35,"annualRevenue":600,"establishYears":5,"industry":"文化传媒","previousLoans":0,"previousOverdues":0,"debtRatio":0.20}', '2026-04-10'),
(5, 85.00, 'v1.0', '{"registeredCapital":1500,"employeeCount":180,"annualRevenue":5000,"establishYears":10,"industry":"建筑业","previousLoans":3,"previousOverdues":0,"debtRatio":0.38}', '2025-12-20');
