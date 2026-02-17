-- ============================================================================
-- QooERP 客户关系管理模块数据库脚本
-- ============================================================================

CREATE DATABASE IF NOT EXISTS qooerp_crm 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE qooerp_crm;

-- 客户表
CREATE TABLE IF NOT EXISTS crm_customer (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  customer_no VARCHAR(50) NOT NULL COMMENT '客户编号',
  customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
  customer_type TINYINT(4) NOT NULL DEFAULT 1 COMMENT '客户类型(1-企业,2-个人)',
  level TINYINT(4) NOT NULL DEFAULT 3 COMMENT '客户等级(1-A级,2-B级,3-C级)',
  industry VARCHAR(100) DEFAULT NULL COMMENT '所属行业',
  region VARCHAR(100) DEFAULT NULL COMMENT '所在地区',
  contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  contact_phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  contact_email VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
  address VARCHAR(500) DEFAULT NULL COMMENT '地址',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-待审核,1-已审核,2-已停用)',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_customer_no (customer_no, tenant_id, deleted),
  KEY idx_customer_name (customer_name),
  KEY idx_level (level),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- 商机表
CREATE TABLE IF NOT EXISTS crm_opportunity (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  opportunity_no VARCHAR(50) NOT NULL COMMENT '商机编号',
  customer_id BIGINT(20) NOT NULL COMMENT '客户ID',
  customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
  opportunity_name VARCHAR(200) NOT NULL COMMENT '商机名称',
  stage TINYINT(4) NOT NULL DEFAULT 1 COMMENT '商机阶段(1-初步接触,2-需求确认,3-方案报价,4-商务谈判,5-合同签订)',
  amount DECIMAL(18,4) NOT NULL COMMENT '预计金额',
  probability INT NOT NULL COMMENT '成功概率(%)',
  expected_close_date DATE DEFAULT NULL COMMENT '预计成交日期',
  owner_id BIGINT(20) NOT NULL COMMENT '负责人ID',
  owner_name VARCHAR(50) NOT NULL COMMENT '负责人姓名',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-进行中,1-已转化,2-已关闭)',
  description TEXT COMMENT '商机描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_opportunity_no (opportunity_no, tenant_id, deleted),
  KEY idx_customer_id (customer_id),
  KEY idx_owner_id (owner_id),
  KEY idx_stage (stage),
  KEY idx_status (status),
  KEY idx_expected_close_date (expected_close_date),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商机表';

-- 合同表
CREATE TABLE IF NOT EXISTS crm_contract (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  contract_no VARCHAR(50) NOT NULL COMMENT '合同编号',
  opportunity_id BIGINT(20) DEFAULT NULL COMMENT '商机ID',
  customer_id BIGINT(20) NOT NULL COMMENT '客户ID',
  customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
  contract_name VARCHAR(200) NOT NULL COMMENT '合同名称',
  contract_amount DECIMAL(18,4) NOT NULL COMMENT '合同金额',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE NOT NULL COMMENT '结束日期',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已审核,2-执行中,3-已完成)',
  description TEXT COMMENT '合同描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_contract_no (contract_no, tenant_id, deleted),
  KEY idx_opportunity_id (opportunity_id),
  KEY idx_customer_id (customer_id),
  KEY idx_status (status),
  KEY idx_start_date (start_date),
  KEY idx_end_date (end_date),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同表';

-- 服务表
CREATE TABLE IF NOT EXISTS crm_service (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  service_no VARCHAR(50) NOT NULL COMMENT '服务编号',
  customer_id BIGINT(20) NOT NULL COMMENT '客户ID',
  customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
  service_type VARCHAR(50) DEFAULT NULL COMMENT '服务类型',
  service_title VARCHAR(200) NOT NULL COMMENT '服务标题',
  description TEXT COMMENT '服务描述',
  priority TINYINT(4) NOT NULL DEFAULT 2 COMMENT '优先级(1-低,2-中,3-高)',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-待处理,1-处理中,2-已完成)',
  assignee_id BIGINT(20) DEFAULT NULL COMMENT '处理人ID',
  assignee_name VARCHAR(50) DEFAULT NULL COMMENT '处理人姓名',
  satisfaction INT DEFAULT NULL COMMENT '满意度(1-5)',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_service_no (service_no, tenant_id, deleted),
  KEY idx_customer_id (customer_id),
  KEY idx_assignee_id (assignee_id),
  KEY idx_status (status),
  KEY idx_priority (priority),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务表';

-- 跟进记录表
CREATE TABLE IF NOT EXISTS crm_followup (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  followup_no VARCHAR(50) NOT NULL COMMENT '跟进编号',
  customer_id BIGINT(20) NOT NULL COMMENT '客户ID',
  opportunity_id BIGINT(20) DEFAULT NULL COMMENT '商机ID',
  followup_type VARCHAR(50) DEFAULT NULL COMMENT '跟进类型',
  followup_content TEXT COMMENT '跟进内容',
  followup_date DATE NOT NULL COMMENT '跟进日期',
  followup_person_id BIGINT(20) NOT NULL COMMENT '跟进人ID',
  followup_person_name VARCHAR(50) NOT NULL COMMENT '跟进人姓名',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_followup_no (followup_no, tenant_id, deleted),
  KEY idx_customer_id (customer_id),
  KEY idx_opportunity_id (opportunity_id),
  KEY idx_followup_date (followup_date),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跟进记录表';

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
CREATE USER IF NOT EXISTS 'qooerp_crm'@'%' IDENTIFIED BY 'qooerp_crm_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_crm.* TO 'qooerp_crm'@'%';
FLUSH PRIVILEGES;
