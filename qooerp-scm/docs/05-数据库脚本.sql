-- ============================================================================
-- QooERP 供应链管理 - 数据库初始化脚本 (PostgreSQL)
-- ============================================================================
-- 版本：v1.0
-- 创建日期：2026-02-17

-- 创建数据库
CREATE DATABASE qooerp_scm WITH ENCODING 'UTF8';

\c qooerp_scm

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 供应商表
DROP TABLE IF EXISTS scm_supplier CASCADE;
CREATE TABLE scm_supplier (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    supplier_code VARCHAR(32) NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    supplier_type VARCHAR(20) NOT NULL,
    industry VARCHAR(64),
    scale VARCHAR(20),
    contact_person VARCHAR(64),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(128),
    address VARCHAR(256),
    tax_number VARCHAR(64),
    bank_name VARCHAR(64),
    bank_account VARCHAR(64),
    credit_level VARCHAR(10) DEFAULT 'C',
    credit_limit DECIMAL(18,2) DEFAULT 0.00,
    payment_method VARCHAR(20) DEFAULT 'MONTHLY',
    payment_days INTEGER DEFAULT 30,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_supplier IS '供应商表';

CREATE UNIQUE INDEX uk_scm_supplier_code ON scm_supplier(tenant_id, supplier_code);
CREATE INDEX idx_scm_supplier_status ON scm_supplier(status);
CREATE INDEX idx_scm_supplier_deleted ON scm_supplier(deleted);
CREATE INDEX idx_scm_supplier_tenant_id ON scm_supplier(tenant_id);

CREATE TRIGGER trg_scm_supplier_update_time
  BEFORE UPDATE ON scm_supplier
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 客户表
DROP TABLE IF EXISTS scm_customer CASCADE;
CREATE TABLE scm_customer (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    customer_code VARCHAR(32) NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    customer_type VARCHAR(20) NOT NULL,
    industry VARCHAR(64),
    scale VARCHAR(20),
    customer_level VARCHAR(20) DEFAULT 'NORMAL',
    contact_person VARCHAR(64),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(128),
    address VARCHAR(256),
    tax_number VARCHAR(64),
    bank_name VARCHAR(64),
    bank_account VARCHAR(64),
    credit_limit DECIMAL(18,2) DEFAULT 0.00,
    payment_method VARCHAR(20) DEFAULT 'MONTHLY',
    payment_days INTEGER DEFAULT 30,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_customer IS '客户表';

CREATE UNIQUE INDEX uk_scm_customer_code ON scm_customer(tenant_id, customer_code);
CREATE INDEX idx_scm_customer_status ON scm_customer(status);
CREATE INDEX idx_scm_customer_deleted ON scm_customer(deleted);
CREATE INDEX idx_scm_customer_tenant_id ON scm_customer(tenant_id);

CREATE TRIGGER trg_scm_customer_update_time
  BEFORE UPDATE ON scm_customer
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 创建用户
CREATE USER qooerp_scm WITH PASSWORD 'qooerp_scm_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_scm;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_scm;
