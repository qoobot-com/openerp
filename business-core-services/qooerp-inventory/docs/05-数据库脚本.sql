-- ============================================================================
-- PostgreSQL 数据库初始化脚本
-- ============================================================================

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

\c qooerp_inventory

-- QooERP 库存管理 - 数据库初始化脚本
-- 版本：v1.1
-- 创建日期：20xx-xx-xx

CREATE DATABASE qooerp_inventory WITH ENCODING 'UTF8';



-- 仓库表
CREATE TABLE IF NOT EXISTS inventory_warehouse (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  warehouse_code VARCHAR(32) NOT NULL,
  warehouse_name VARCHAR(128) NOT NULL,
  warehouse_type VARCHAR(20) DEFAULT 'MAIN',
  warehouse_address VARCHAR(256),
  manager_id BIGINT,
  manager_name VARCHAR(64),
  contact_phone VARCHAR(20),
  status VARCHAR(20) DEFAULT 'ACTIVE',
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_warehouse_code (tenant_id, warehouse_code),
  INDEX idx_status (status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 物料分类表
CREATE TABLE IF NOT EXISTS inventory_material_category (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  category_code VARCHAR(32) NOT NULL,
  category_name VARCHAR(64) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  category_path VARCHAR(256),
  sort INTEGER DEFAULT 0,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_category_code (tenant_id, category_code),
  INDEX idx_parent_id (parent_id),
  INDEX idx_status (status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 物料表
CREATE TABLE IF NOT EXISTS inventory_material (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  category_id BIGINT DEFAULT NULL,
  category_name VARCHAR(64),
  specification VARCHAR(128),
  unit VARCHAR(20),
  material_type VARCHAR(20) NOT NULL,
  cost_attribute VARCHAR(20) DEFAULT 'STANDARD',
  standard_cost DECIMAL(18,2) DEFAULT 0.00,
  safety_stock DECIMAL(10,2) DEFAULT 0.00,
  min_stock DECIMAL(10,2) DEFAULT 0.00,
  max_stock DECIMAL(10,2) DEFAULT 0.00,
  lead_time INTEGER DEFAULT 0,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  version INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_material_code (tenant_id, material_code),
  INDEX idx_category_id (category_id),
  INDEX idx_status (status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 库存表
CREATE TABLE IF NOT EXISTS inventory_stock (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  warehouse_id BIGINT NOT NULL,
  warehouse_name VARCHAR(64) NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit VARCHAR(20),
  current_stock DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  available_stock DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  locked_stock DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  in_transit_stock DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  standard_cost DECIMAL(18,2) DEFAULT 0.00,
  stock_amount DECIMAL(18,2) DEFAULT 0.00,
  last_inbound_date DATE,
  last_outbound_date DATE,
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_warehouse_material (tenant_id, warehouse_id, material_id),
  INDEX idx_material_id (material_id),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 入库单表
CREATE TABLE IF NOT EXISTS inventory_stock_inbound (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  inbound_code VARCHAR(32) NOT NULL,
  inbound_type VARCHAR(20) NOT NULL,
  inbound_date DATE NOT NULL,
  warehouse_id BIGINT NOT NULL,
  warehouse_name VARCHAR(64),
  supplier_id BIGINT,
  supplier_name VARCHAR(64),
  inbound_status VARCHAR(20) DEFAULT 'PENDING',
  total_quantity DECIMAL(10,2) DEFAULT 0.00,
  total_amount DECIMAL(18,2) DEFAULT 0.00,
  approver_id BIGINT,
  approver_name VARCHAR(64),
  approve_time TIMESTAMP,
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  version INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_inbound_code (tenant_id, inbound_code),
  INDEX idx_warehouse_id (warehouse_id),
  INDEX idx_status (inbound_status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 入库明细表
CREATE TABLE IF NOT EXISTS inventory_stock_inbound_detail (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  inbound_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit VARCHAR(20),
  quantity DECIMAL(10,2) NOT NULL,
  unit_price DECIMAL(18,2),
  amount DECIMAL(18,2),
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  INDEX idx_inbound_id (inbound_id),
  INDEX idx_material_id (material_id),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 出库单表
CREATE TABLE IF NOT EXISTS inventory_stock_outbound (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  outbound_code VARCHAR(32) NOT NULL,
  outbound_type VARCHAR(20) NOT NULL,
  outbound_date DATE NOT NULL,
  warehouse_id BIGINT NOT NULL,
  warehouse_name VARCHAR(64),
  customer_id BIGINT,
  customer_name VARCHAR(64),
  outbound_status VARCHAR(20) DEFAULT 'PENDING',
  total_quantity DECIMAL(10,2) DEFAULT 0.00,
  total_amount DECIMAL(18,2) DEFAULT 0.00,
  approver_id BIGINT,
  approver_name VARCHAR(64),
  approve_time TIMESTAMP,
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  version INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_outbound_code (tenant_id, outbound_code),
  INDEX idx_warehouse_id (warehouse_id),
  INDEX idx_status (outbound_status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 出库明细表
CREATE TABLE IF NOT EXISTS inventory_stock_outbound_detail (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  outbound_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit VARCHAR(20),
  quantity DECIMAL(10,2) NOT NULL,
  unit_price DECIMAL(18,2),
  amount DECIMAL(18,2),
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  INDEX idx_outbound_id (outbound_id),
  INDEX idx_material_id (material_id),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 调拨单表
CREATE TABLE IF NOT EXISTS inventory_transfer (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  transfer_code VARCHAR(32) NOT NULL,
  transfer_date DATE NOT NULL,
  from_warehouse_id BIGINT NOT NULL,
  from_warehouse_name VARCHAR(64),
  to_warehouse_id BIGINT NOT NULL,
  to_warehouse_name VARCHAR(64),
  transfer_status VARCHAR(20) DEFAULT 'PENDING',
  total_quantity DECIMAL(10,2) DEFAULT 0.00,
  approver_id BIGINT,
  approver_name VARCHAR(64),
  approve_time TIMESTAMP,
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  version INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_transfer_code (tenant_id, transfer_code),
  INDEX idx_from_warehouse (from_warehouse_id),
  INDEX idx_to_warehouse (to_warehouse_id),
  INDEX idx_status (transfer_status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 调拨明细表
CREATE TABLE IF NOT EXISTS inventory_transfer_detail (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  transfer_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit VARCHAR(20),
  quantity DECIMAL(10,2) NOT NULL,
  unit_price DECIMAL(18,2),
  amount DECIMAL(18,2),
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  INDEX idx_transfer_id (transfer_id),
  INDEX idx_material_id (material_id),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 盘点单表
CREATE TABLE IF NOT EXISTS inventory_check (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  check_code VARCHAR(32) NOT NULL,
  warehouse_id BIGINT NOT NULL,
  warehouse_name VARCHAR(64),
  check_date DATE NOT NULL,
  check_status VARCHAR(20) DEFAULT 'PENDING',
  checker_id BIGINT,
  checker_name VARCHAR(64),
  check_time TIMESTAMP,
  total_diff_quantity DECIMAL(10,2) DEFAULT 0.00,
  total_diff_amount DECIMAL(18,2) DEFAULT 0.00,
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  version INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE INDEX uk_check_code (tenant_id, check_code),
  INDEX idx_warehouse_id (warehouse_id),
  INDEX idx_status (check_status),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 盘点明细表
CREATE TABLE IF NOT EXISTS inventory_check_detail (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  check_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  specification VARCHAR(128),
  unit VARCHAR(20),
  current_stock DECIMAL(10,2) NOT NULL,
  actual_stock DECIMAL(10,2) NOT NULL,
  diff_quantity DECIMAL(10,2),
  diff_amount DECIMAL(18,2),
  remark VARCHAR(512),
  created_by BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  INDEX idx_check_id (check_id),
  INDEX idx_material_id (material_id),
  INDEX idx_deleted (deleted),
  INDEX idx_tenant_id (tenant_id)
);

-- 库存变动记录表
CREATE TABLE IF NOT EXISTS inventory_stock_record (
  id BIGSERIAL NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 0,
  warehouse_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(32) NOT NULL,
  material_name VARCHAR(128) NOT NULL,
  record_type VARCHAR(20) NOT NULL,
  business_type VARCHAR(20) NOT NULL,
  business_no VARCHAR(32) NOT NULL,
  change_quantity DECIMAL(10,2) NOT NULL,
  before_stock DECIMAL(10,2) NOT NULL,
  after_stock DECIMAL(10,2) NOT NULL,
  unit_price DECIMAL(18,2),
  change_amount DECIMAL(18,2),
  operator_id BIGINT NOT NULL,
  operator_name VARCHAR(64),
  operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(512),
  PRIMARY KEY (id),
  INDEX idx_warehouse_material (warehouse_id, material_id),
  INDEX idx_business_no (business_no),
  INDEX idx_operation_time (operation_time),
  INDEX idx_tenant_id (tenant_id)
);

-- 创建用户和授权
CREATE USER qooerp_inventory WITH PASSWORD 'qooerp_inventory_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_inventory;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_inventory;
