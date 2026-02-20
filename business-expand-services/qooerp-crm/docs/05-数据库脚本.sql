-- ============================================================================
-- QooERP 客户关系管理模块数据库脚本 (PostgreSQL)
-- ============================================================================

-- 创建数据库
CREATE DATABASE qooerp_crm
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       TEMPLATE = template0;

\c qooerp_crm

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 客户表
CREATE TABLE IF NOT EXISTS crm_customer (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  customer_no VARCHAR(50) NOT NULL,
  customer_name VARCHAR(200) NOT NULL,
  customer_type SMALLINT NOT NULL DEFAULT 1,
  level SMALLINT NOT NULL DEFAULT 3,
  industry VARCHAR(100),
  region VARCHAR(100),
  contact_person VARCHAR(50),
  contact_phone VARCHAR(20),
  contact_email VARCHAR(100),
  address VARCHAR(500),
  status SMALLINT NOT NULL DEFAULT 0,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50),
  update_by VARCHAR(50),
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE crm_customer IS '客户表';
COMMENT ON COLUMN crm_customer.id IS '主键';
COMMENT ON COLUMN crm_customer.customer_no IS '客户编号';
COMMENT ON COLUMN crm_customer.customer_name IS '客户名称';
COMMENT ON COLUMN crm_customer.customer_type IS '客户类型(1-企业,2-个人)';
COMMENT ON COLUMN crm_customer.level IS '客户等级(1-A级,2-B级,3-C级)';
COMMENT ON COLUMN crm_customer.status IS '状态(0-待审核,1-已审核,2-已停用)';

CREATE UNIQUE INDEX uk_customer_no ON crm_customer(customer_no, tenant_id, deleted);
CREATE INDEX idx_customer_name ON crm_customer(customer_name);
CREATE INDEX idx_level ON crm_customer(level);
CREATE INDEX idx_status ON crm_customer(status);
CREATE INDEX idx_tenant_id ON crm_customer(tenant_id);

CREATE TRIGGER trg_crm_customer_update_time
  BEFORE UPDATE ON crm_customer
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 商机表
CREATE TABLE IF NOT EXISTS crm_opportunity (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  opportunity_no VARCHAR(50) NOT NULL,
  customer_id BIGINT NOT NULL,
  customer_name VARCHAR(200) NOT NULL,
  opportunity_name VARCHAR(200) NOT NULL,
  stage SMALLINT NOT NULL DEFAULT 1,
  amount DECIMAL(18,4) NOT NULL,
  probability INTEGER NOT NULL,
  expected_close_date DATE,
  owner_id BIGINT NOT NULL,
  owner_name VARCHAR(50) NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50),
  update_by VARCHAR(50),
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE crm_opportunity IS '商机表';
COMMENT ON COLUMN crm_opportunity.stage IS '商机阶段(1-初步接触,2-需求确认,3-方案报价,4-商务谈判,5-合同签订)';
COMMENT ON COLUMN crm_opportunity.status IS '状态(0-进行中,1-已转化,2-已关闭)';

CREATE UNIQUE INDEX uk_opportunity_no ON crm_opportunity(opportunity_no, tenant_id, deleted);
CREATE INDEX idx_customer_id ON crm_opportunity(customer_id);
CREATE INDEX idx_owner_id ON crm_opportunity(owner_id);
CREATE INDEX idx_stage ON crm_opportunity(stage);
CREATE INDEX idx_status ON crm_opportunity(status);
CREATE INDEX idx_expected_close_date ON crm_opportunity(expected_close_date);
CREATE INDEX idx_tenant_id ON crm_opportunity(tenant_id);

CREATE TRIGGER trg_crm_opportunity_update_time
  BEFORE UPDATE ON crm_opportunity
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 合同表
CREATE TABLE IF NOT EXISTS crm_contract (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  contract_no VARCHAR(50) NOT NULL,
  opportunity_id BIGINT,
  customer_id BIGINT NOT NULL,
  customer_name VARCHAR(200) NOT NULL,
  contract_name VARCHAR(200) NOT NULL,
  contract_amount DECIMAL(18,4) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50),
  update_by VARCHAR(50),
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE crm_contract IS '合同表';
COMMENT ON COLUMN crm_contract.status IS '状态(0-草稿,1-已审核,2-执行中,3-已完成)';

CREATE UNIQUE INDEX uk_contract_no ON crm_contract(contract_no, tenant_id, deleted);
CREATE INDEX idx_opportunity_id ON crm_contract(opportunity_id);
CREATE INDEX idx_customer_id ON crm_contract(customer_id);
CREATE INDEX idx_status ON crm_contract(status);
CREATE INDEX idx_start_date ON crm_contract(start_date);
CREATE INDEX idx_end_date ON crm_contract(end_date);
CREATE INDEX idx_tenant_id ON crm_contract(tenant_id);

CREATE TRIGGER trg_crm_contract_update_time
  BEFORE UPDATE ON crm_contract
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 服务表
CREATE TABLE IF NOT EXISTS crm_service (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  service_no VARCHAR(50) NOT NULL,
  customer_id BIGINT NOT NULL,
  customer_name VARCHAR(200) NOT NULL,
  service_type VARCHAR(50),
  service_title VARCHAR(200) NOT NULL,
  description TEXT,
  priority SMALLINT NOT NULL DEFAULT 2,
  status SMALLINT NOT NULL DEFAULT 0,
  assignee_id BIGINT,
  assignee_name VARCHAR(50),
  satisfaction INTEGER,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50),
  update_by VARCHAR(50),
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE crm_service IS '服务表';
COMMENT ON COLUMN crm_service.priority IS '优先级(1-低,2-中,3-高)';
COMMENT ON COLUMN crm_service.status IS '状态(0-待处理,1-处理中,2-已完成)';

CREATE UNIQUE INDEX uk_service_no ON crm_service(service_no, tenant_id, deleted);
CREATE INDEX idx_customer_id ON crm_service(customer_id);
CREATE INDEX idx_assignee_id ON crm_service(assignee_id);
CREATE INDEX idx_status ON crm_service(status);
CREATE INDEX idx_priority ON crm_service(priority);
CREATE INDEX idx_tenant_id ON crm_service(tenant_id);

CREATE TRIGGER trg_crm_service_update_time
  BEFORE UPDATE ON crm_service
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 跟进记录表
CREATE TABLE IF NOT EXISTS crm_followup (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  followup_no VARCHAR(50) NOT NULL,
  customer_id BIGINT NOT NULL,
  opportunity_id BIGINT,
  followup_type VARCHAR(50),
  followup_content TEXT,
  followup_date DATE NOT NULL,
  followup_person_id BIGINT NOT NULL,
  followup_person_name VARCHAR(50) NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE crm_followup IS '跟进记录表';

CREATE UNIQUE INDEX uk_followup_no ON crm_followup(followup_no, tenant_id, deleted);
CREATE INDEX idx_customer_id ON crm_followup(customer_id);
CREATE INDEX idx_opportunity_id ON crm_followup(opportunity_id);
CREATE INDEX idx_followup_date ON crm_followup(followup_date);
CREATE INDEX idx_tenant_id ON crm_followup(tenant_id);

-- 插入演示数据
INSERT INTO crm_customer (
  customer_no, customer_name, customer_type, level, industry, region,
  contact_person, contact_phone, contact_email, status, tenant_id
) VALUES 
('CUST20260217001', '某某科技有限公司', 1, 1, '互联网', '华东',
 '张经理', '13800138000', 'zhang@example.com', 1, 1),
('CUST20260217002', '某某贸易公司', 1, 2, '贸易', '华南',
 '李总', '13900139000', 'li@example.com', 1, 1);

INSERT INTO crm_opportunity (
  opportunity_no, customer_id, customer_name, opportunity_name, stage,
  amount, probability, owner_id, owner_name, tenant_id
) VALUES 
('OPP20260217001', 1, '某某科技有限公司', 'ERP系统采购', 3,
 500000.0000, 60, 2, '王销售', 1);

-- 创建用户
CREATE USER qooerp_crm WITH PASSWORD 'qooerp_crm_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_crm;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_crm;
