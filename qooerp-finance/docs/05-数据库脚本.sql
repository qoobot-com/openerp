-- ============================================================================
-- QooERP 财务管理 - 数据库初始化脚本 (PostgreSQL)
-- ============================================================================
-- 版本：v1.0
-- 创建日期：2026-02-17

-- 创建数据库
CREATE DATABASE qooerp_finance WITH ENCODING 'UTF8';

\c qooerp_finance

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 1. 科目表
DROP TABLE IF EXISTS finance_account CASCADE;
CREATE TABLE finance_account (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    account_code VARCHAR(32) NOT NULL,
    account_name VARCHAR(128) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    parent_id BIGINT DEFAULT NULL,
    is_leaf SMALLINT NOT NULL DEFAULT 0,
    direction VARCHAR(10) NOT NULL,
    balance_direction VARCHAR(10) NOT NULL,
    is_enabled SMALLINT NOT NULL DEFAULT 1,
    is_cash_account SMALLINT NOT NULL DEFAULT 0,
    is_bank_account SMALLINT NOT NULL DEFAULT 0,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_account IS '科目表';
COMMENT ON COLUMN finance_account.id IS '主键ID';
COMMENT ON COLUMN finance_account.tenant_id IS '租户ID';
COMMENT ON COLUMN finance_account.account_code IS '科目编码';
COMMENT ON COLUMN finance_account.account_name IS '科目名称';
COMMENT ON COLUMN finance_account.account_type IS '科目类型';
COMMENT ON COLUMN finance_account.level IS '科目层级';
COMMENT ON COLUMN finance_account.parent_id IS '父科目ID';
COMMENT ON COLUMN finance_account.is_leaf IS '是否叶子节点';
COMMENT ON COLUMN finance_account.direction IS '借贷方向';
COMMENT ON COLUMN finance_account.balance_direction IS '余额方向';
COMMENT ON COLUMN finance_account.is_enabled IS '是否启用';
COMMENT ON COLUMN finance_account.is_cash_account IS '是否现金科目';
COMMENT ON COLUMN finance_account.is_bank_account IS '是否银行科目';
COMMENT ON COLUMN finance_account.sort_order IS '排序';

CREATE UNIQUE INDEX uk_finance_account_code ON finance_account(tenant_id, account_code);
CREATE INDEX idx_finance_account_parent ON finance_account(parent_id);
CREATE INDEX idx_finance_account_deleted ON finance_account(deleted);
CREATE INDEX idx_finance_account_tenant ON finance_account(tenant_id);

CREATE TRIGGER trg_finance_account_update_time
  BEFORE UPDATE ON finance_account
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 2. 凭证表
DROP TABLE IF EXISTS finance_voucher CASCADE;
CREATE TABLE finance_voucher (
    id BIGSERIAL NOT NULL PRIMARY KEY,
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
    review_time TIMESTAMP DEFAULT NULL,
    posting_status VARCHAR(20) NOT NULL DEFAULT 'UNPOSTED',
    posting_time TIMESTAMP DEFAULT NULL,
    attachment_count INTEGER NOT NULL DEFAULT 0,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_voucher IS '凭证表';
COMMENT ON COLUMN finance_voucher.voucher_code IS '凭证编号';
COMMENT ON COLUMN finance_voucher.voucher_date IS '凭证日期';
COMMENT ON COLUMN finance_voucher.voucher_type IS '凭证类型';
COMMENT ON COLUMN finance_voucher.accounting_period IS '会计期间';
COMMENT ON COLUMN finance_voucher.debit_amount IS '借方金额';
COMMENT ON COLUMN finance_voucher.credit_amount IS '贷方金额';
COMMENT ON COLUMN finance_voucher.summary IS '摘要';
COMMENT ON COLUMN finance_voucher.review_status IS '审核状态';
COMMENT ON COLUMN finance_voucher.posting_status IS '过账状态';

CREATE UNIQUE INDEX uk_finance_voucher_code ON finance_voucher(tenant_id, voucher_code);
CREATE INDEX idx_finance_voucher_date ON finance_voucher(voucher_date);
CREATE INDEX idx_finance_voucher_deleted ON finance_voucher(deleted);
CREATE INDEX idx_finance_voucher_tenant ON finance_voucher(tenant_id);

CREATE TRIGGER trg_finance_voucher_update_time
  BEFORE UPDATE ON finance_voucher
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 3. 凭证明细表
DROP TABLE IF EXISTS finance_voucher_detail CASCADE;
CREATE TABLE finance_voucher_detail (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    voucher_id BIGINT NOT NULL,
    account_code VARCHAR(32) NOT NULL,
    account_name VARCHAR(128) NOT NULL,
    direction VARCHAR(10) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    summary VARCHAR(512),
    auxiliary_type VARCHAR(20),
    auxiliary_id BIGINT DEFAULT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_voucher_detail IS '凭证明细表';

CREATE INDEX idx_finance_voucher_detail_voucher ON finance_voucher_detail(voucher_id);
CREATE INDEX idx_finance_voucher_detail_deleted ON finance_voucher_detail(deleted);
CREATE INDEX idx_finance_voucher_detail_tenant ON finance_voucher_detail(tenant_id);

CREATE TRIGGER trg_finance_voucher_detail_update_time
  BEFORE UPDATE ON finance_voucher_detail
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 4. 应收账款表
DROP TABLE IF EXISTS finance_receivable CASCADE;
CREATE TABLE finance_receivable (
    id BIGSERIAL NOT NULL PRIMARY KEY,
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
    aging_days INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_receivable IS '应收账款表';
COMMENT ON COLUMN finance_receivable.receivable_code IS '应收单号';
COMMENT ON COLUMN finance_receivable.receivable_amount IS '应收金额';
COMMENT ON COLUMN finance_receivable.received_amount IS '已收金额';
COMMENT ON COLUMN finance_receivable.unpaid_amount IS '未收金额';
COMMENT ON COLUMN finance_receivable.status IS '状态';

CREATE UNIQUE INDEX uk_finance_receivable_code ON finance_receivable(tenant_id, receivable_code);
CREATE INDEX idx_finance_receivable_customer ON finance_receivable(customer_id);
CREATE INDEX idx_finance_receivable_status ON finance_receivable(status);
CREATE INDEX idx_finance_receivable_deleted ON finance_receivable(deleted);
CREATE INDEX idx_finance_receivable_tenant ON finance_receivable(tenant_id);

CREATE TRIGGER trg_finance_receivable_update_time
  BEFORE UPDATE ON finance_receivable
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 5. 收款记录表
DROP TABLE IF EXISTS finance_receipt CASCADE;
CREATE TABLE finance_receipt (
    id BIGSERIAL NOT NULL PRIMARY KEY,
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
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_receipt IS '收款记录表';
COMMENT ON COLUMN finance_receipt.receipt_code IS '收款单号';
COMMENT ON COLUMN finance_receipt.receipt_amount IS '收款金额';

CREATE UNIQUE INDEX uk_finance_receipt_code ON finance_receipt(tenant_id, receipt_code);
CREATE INDEX idx_finance_receipt_receivable ON finance_receipt(receivable_id);
CREATE INDEX idx_finance_receipt_deleted ON finance_receipt(deleted);
CREATE INDEX idx_finance_receipt_tenant ON finance_receipt(tenant_id);

CREATE TRIGGER trg_finance_receipt_update_time
  BEFORE UPDATE ON finance_receipt
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 6. 应付账款表
DROP TABLE IF EXISTS finance_payable CASCADE;
CREATE TABLE finance_payable (
    id BIGSERIAL NOT NULL PRIMARY KEY,
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
    aging_days INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_payable IS '应付账款表';

CREATE UNIQUE INDEX uk_finance_payable_code ON finance_payable(tenant_id, payable_code);
CREATE INDEX idx_finance_payable_supplier ON finance_payable(supplier_id);
CREATE INDEX idx_finance_payable_status ON finance_payable(status);
CREATE INDEX idx_finance_payable_deleted ON finance_payable(deleted);
CREATE INDEX idx_finance_payable_tenant ON finance_payable(tenant_id);

CREATE TRIGGER trg_finance_payable_update_time
  BEFORE UPDATE ON finance_payable
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 7. 付款记录表
DROP TABLE IF EXISTS finance_payment CASCADE;
CREATE TABLE finance_payment (
    id BIGSERIAL NOT NULL PRIMARY KEY,
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
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_payment IS '付款记录表';

CREATE UNIQUE INDEX uk_finance_payment_code ON finance_payment(tenant_id, payment_code);
CREATE INDEX idx_finance_payment_payable ON finance_payment(payable_id);
CREATE INDEX idx_finance_payment_deleted ON finance_payment(deleted);
CREATE INDEX idx_finance_payment_tenant ON finance_payment(tenant_id);

CREATE TRIGGER trg_finance_payment_update_time
  BEFORE UPDATE ON finance_payment
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 8. 固定资产表
DROP TABLE IF EXISTS finance_asset CASCADE;
CREATE TABLE finance_asset (
    id BIGSERIAL NOT NULL PRIMARY KEY,
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
    useful_life INTEGER NOT NULL DEFAULT 0,
    depreciation_method VARCHAR(20) NOT NULL DEFAULT 'STRAIGHT_LINE',
    depreciation_status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_asset IS '固定资产表';
COMMENT ON COLUMN finance_asset.asset_code IS '资产编号';
COMMENT ON COLUMN finance_asset.original_value IS '原值';
COMMENT ON COLUMN finance_asset.accumulated_depreciation IS '累计折旧';
COMMENT ON COLUMN finance_asset.net_value IS '净值';

CREATE UNIQUE INDEX uk_finance_asset_code ON finance_asset(tenant_id, asset_code);
CREATE INDEX idx_finance_asset_category ON finance_asset(category_id);
CREATE INDEX idx_finance_asset_deleted ON finance_asset(deleted);
CREATE INDEX idx_finance_asset_tenant ON finance_asset(tenant_id);

CREATE TRIGGER trg_finance_asset_update_time
  BEFORE UPDATE ON finance_asset
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 9. 资产折旧表
DROP TABLE IF EXISTS finance_asset_depreciation CASCADE;
CREATE TABLE finance_asset_depreciation (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    asset_id BIGINT NOT NULL,
    depreciation_period VARCHAR(7) NOT NULL,
    depreciation_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    accumulated_depreciation DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    net_value DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE finance_asset_depreciation IS '资产折旧表';

CREATE UNIQUE INDEX uk_finance_asset_period ON finance_asset_depreciation(tenant_id, asset_id, depreciation_period);
CREATE INDEX idx_finance_asset_depreciation_asset ON finance_asset_depreciation(asset_id);
CREATE INDEX idx_finance_asset_depreciation_deleted ON finance_asset_depreciation(deleted);
CREATE INDEX idx_finance_asset_depreciation_tenant ON finance_asset_depreciation(tenant_id);

CREATE TRIGGER trg_finance_asset_depreciation_update_time
  BEFORE UPDATE ON finance_asset_depreciation
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 创建用户
CREATE USER qooerp_finance WITH PASSWORD 'qooerp_finance_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_finance;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_finance;
