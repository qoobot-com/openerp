-- QooERP 财务管理 - 数据库初始化脚本
-- 版本：v1.0
-- 创建日期：2026-02-17

-- 创建数据库
CREATE DATABASE IF NOT EXISTS qooerp_finance 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE qooerp_finance;

-- 1. 科目表
DROP TABLE IF EXISTS finance_account;
CREATE TABLE finance_account (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    account_code VARCHAR(32) NOT NULL,
    account_name VARCHAR(128) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    parent_id BIGINT DEFAULT NULL,
    is_leaf TINYINT NOT NULL DEFAULT 0,
    direction VARCHAR(10) NOT NULL,
    balance_direction VARCHAR(10) NOT NULL,
    is_enabled TINYINT NOT NULL DEFAULT 1,
    is_cash_account TINYINT NOT NULL DEFAULT 0,
    is_bank_account TINYINT NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_code (tenant_id, account_code),
    KEY idx_parent_id (parent_id),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科目表';

-- 2. 凭证表
DROP TABLE IF EXISTS finance_voucher;
CREATE TABLE finance_voucher (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    voucher_code VARCHAR(32) NOT NULL,
    voucher_date DATE NOT NULL,
    voucher_type VARCHAR(20) NOT NULL,
    accounting_period VARCHAR(7) NOT NULL,
    debit_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    credit_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    summary VARCHAR(512),
    creator_id BIGINT DEFAULT NULL,
    reviewer_id BIGINT DEFAULT NULL,
    review_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    review_time DATETIME DEFAULT NULL,
    posting_status VARCHAR(20) NOT NULL DEFAULT 'UNPOSTED',
    posting_time DATETIME DEFAULT NULL,
    attachment_count INT NOT NULL DEFAULT 0,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_voucher_code (tenant_id, voucher_code),
    KEY idx_voucher_date (voucher_date),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='凭证表';

-- 3. 凭证明细表
DROP TABLE IF EXISTS finance_voucher_detail;
CREATE TABLE finance_voucher_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    voucher_id BIGINT NOT NULL,
    account_code VARCHAR(32) NOT NULL,
    account_name VARCHAR(128) NOT NULL,
    direction VARCHAR(10) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    summary VARCHAR(512),
    auxiliary_type VARCHAR(20),
    auxiliary_id BIGINT DEFAULT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_voucher_id (voucher_id),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='凭证明细表';

-- 4. 应收账款表
DROP TABLE IF EXISTS finance_receivable;
CREATE TABLE finance_receivable (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    receivable_code VARCHAR(32) NOT NULL,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    business_type VARCHAR(20) NOT NULL,
    related_order_id BIGINT DEFAULT NULL,
    related_order_code VARCHAR(64),
    receivable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    received_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    unpaid_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    receivable_date DATE NOT NULL,
    due_date DATE NOT NULL,
    aging_days INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_receivable_code (tenant_id, receivable_code),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应收账款表';

-- 5. 收款记录表
DROP TABLE IF EXISTS finance_receipt;
CREATE TABLE finance_receipt (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    receipt_code VARCHAR(32) NOT NULL,
    receivable_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    receipt_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    receipt_date DATE NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    bank_account VARCHAR(64),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_receipt_code (tenant_id, receipt_code),
    KEY idx_receivable_id (receivable_id),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收款记录表';

-- 6. 应付账款表
DROP TABLE IF EXISTS finance_payable;
CREATE TABLE finance_payable (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    payable_code VARCHAR(32) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    business_type VARCHAR(20) NOT NULL,
    related_order_id BIGINT DEFAULT NULL,
    related_order_code VARCHAR(64),
    payable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    unpaid_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    payable_date DATE NOT NULL,
    due_date DATE NOT NULL,
    aging_days INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payable_code (tenant_id, payable_code),
    KEY idx_supplier_id (supplier_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应付账款表';

-- 7. 付款记录表
DROP TABLE IF EXISTS finance_payment;
CREATE TABLE finance_payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    payment_code VARCHAR(32) NOT NULL,
    payable_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    payment_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    bank_account VARCHAR(64),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_code (tenant_id, payment_code),
    KEY idx_payable_id (payable_id),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='付款记录表';

-- 8. 固定资产表
DROP TABLE IF EXISTS finance_asset;
CREATE TABLE finance_asset (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    asset_code VARCHAR(32) NOT NULL,
    asset_name VARCHAR(128) NOT NULL,
    category_id BIGINT NOT NULL,
    category_name VARCHAR(64) NOT NULL,
    specification VARCHAR(128),
    unit VARCHAR(20),
    quantity DECIMAL(10,2) NOT NULL DEFAULT 1.00,
    original_value DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    accumulated_depreciation DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    net_value DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    department_id BIGINT DEFAULT NULL,
    department_name VARCHAR(64),
    user_id BIGINT DEFAULT NULL,
    user_name VARCHAR(64),
    location VARCHAR(128),
    entry_date DATE NOT NULL,
    start_date DATE NOT NULL,
    useful_life INT NOT NULL DEFAULT 0,
    depreciation_method VARCHAR(20) NOT NULL DEFAULT 'STRAIGHT_LINE',
    depreciation_status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_code (tenant_id, asset_code),
    KEY idx_category_id (category_id),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定资产表';

-- 9. 资产折旧表
DROP TABLE IF EXISTS finance_asset_depreciation;
CREATE TABLE finance_asset_depreciation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    asset_id BIGINT NOT NULL,
    depreciation_period VARCHAR(7) NOT NULL,
    depreciation_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    accumulated_depreciation DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    net_value DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    created_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_period (tenant_id, asset_id, depreciation_period),
    KEY idx_asset_id (asset_id),
    KEY idx_deleted (deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产折旧表';
