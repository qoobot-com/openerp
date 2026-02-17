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

\c qooerp_production

-- ============================================================================
-- QooERP 生产管理模块数据库脚本
-- ============================================================================

-- 创建数据库
CREATE DATABASE qooerp_production WITH ENCODING 'UTF8';

-- ============================================================================
-- 表结构创建
-- ============================================================================

-- 1. 生产计划表
DROP TABLE IF EXISTS production_plan CASCADE;
CREATE TABLE production_plan (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  plan_no VARCHAR(50) NOT NULL,
  plan_name VARCHAR(200) DEFAULT NULL,
  plan_date DATE NOT NULL,
  plan_quantity DECIMAL(18,4) NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  product_id BIGINT NOT NULL,
  product_code VARCHAR(50) NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  delivery_date DATE DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_plan IS '生产计划表';
COMMENT ON COLUMN production_plan.id IS '主键';
COMMENT ON COLUMN production_plan.plan_no IS '计划编号';
COMMENT ON COLUMN production_plan.plan_name IS '计划名称';
COMMENT ON COLUMN production_plan.plan_date IS '计划日期';
COMMENT ON COLUMN production_plan.plan_quantity IS '计划数量';
COMMENT ON COLUMN production_plan.status IS '状态(0-草稿,1-已审核,2-已下达,3-已完成,4-已取消)';
COMMENT ON COLUMN production_plan.product_id IS '产品ID';
COMMENT ON COLUMN production_plan.product_code IS '产品编码';
COMMENT ON COLUMN production_plan.product_name IS '产品名称';
COMMENT ON COLUMN production_plan.delivery_date IS '交货日期';
COMMENT ON COLUMN production_plan.tenant_id IS '租户ID';

CREATE UNIQUE INDEX uk_production_plan_no ON production_plan(plan_no, tenant_id, deleted);
CREATE INDEX idx_production_plan_product_id ON production_plan(product_id);
CREATE INDEX idx_production_plan_plan_date ON production_plan(plan_date);
CREATE INDEX idx_production_plan_status ON production_plan(status);
CREATE INDEX idx_production_plan_tenant_id ON production_plan(tenant_id);

CREATE TRIGGER trg_production_plan_update_time
  BEFORE UPDATE ON production_plan
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 2. 生产订单表
DROP TABLE IF EXISTS production_order CASCADE;
CREATE TABLE production_order (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  order_no VARCHAR(50) NOT NULL,
  plan_id BIGINT DEFAULT NULL,
  plan_no VARCHAR(50) DEFAULT NULL,
  order_date DATE NOT NULL,
  order_quantity DECIMAL(18,4) NOT NULL,
  completed_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 0,
  product_id BIGINT NOT NULL,
  product_code VARCHAR(50) NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  delivery_date DATE DEFAULT NULL,
  workshop_id BIGINT DEFAULT NULL,
  workshop_name VARCHAR(100) DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_order IS '生产订单表';
COMMENT ON COLUMN production_order.id IS '主键';
COMMENT ON COLUMN production_order.order_no IS '订单编号';
COMMENT ON COLUMN production_order.status IS '状态(0-草稿,1-已审核,2-已下达,3-生产中,4-已完成,5-已关闭)';

CREATE UNIQUE INDEX uk_production_order_no ON production_order(order_no, tenant_id, deleted);
CREATE INDEX idx_production_order_plan_id ON production_order(plan_id);
CREATE INDEX idx_production_order_product_id ON production_order(product_id);
CREATE INDEX idx_production_order_order_date ON production_order(order_date);
CREATE INDEX idx_production_order_status ON production_order(status);
CREATE INDEX idx_production_order_tenant_id ON production_order(tenant_id);

CREATE TRIGGER trg_production_order_update_time
  BEFORE UPDATE ON production_order
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 3. 生产领料表
DROP TABLE IF EXISTS production_material CASCADE;
CREATE TABLE production_material (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  material_no VARCHAR(50) NOT NULL,
  order_id BIGINT NOT NULL,
  order_no VARCHAR(50) NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(50) NOT NULL,
  material_name VARCHAR(200) NOT NULL,
  specification VARCHAR(200) DEFAULT NULL,
  unit VARCHAR(20) DEFAULT NULL,
  request_quantity DECIMAL(18,4) NOT NULL,
  issued_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
  returned_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
  warehouse_id BIGINT DEFAULT NULL,
  warehouse_name VARCHAR(100) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  issue_date DATE DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_material IS '生产领料表';

CREATE UNIQUE INDEX uk_production_material_no ON production_material(material_no, tenant_id, deleted);
CREATE INDEX idx_production_material_order_id ON production_material(order_id);
CREATE INDEX idx_production_material_material_id ON production_material(material_id);
CREATE INDEX idx_production_material_issue_date ON production_material(issue_date);
CREATE INDEX idx_production_material_status ON production_material(status);
CREATE INDEX idx_production_material_tenant_id ON production_material(tenant_id);

CREATE TRIGGER trg_production_material_update_time
  BEFORE UPDATE ON production_material
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 4. 生产入库表
DROP TABLE IF EXISTS production_receipt CASCADE;
CREATE TABLE production_receipt (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  receipt_no VARCHAR(50) NOT NULL,
  order_id BIGINT NOT NULL,
  order_no VARCHAR(50) NOT NULL,
  product_id BIGINT NOT NULL,
  product_code VARCHAR(50) NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  receipt_quantity DECIMAL(18,4) NOT NULL,
  qualified_quantity DECIMAL(18,4) NOT NULL,
  unqualified_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
  warehouse_id BIGINT DEFAULT NULL,
  warehouse_name VARCHAR(100) DEFAULT NULL,
  quality_status SMALLINT NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 0,
  receipt_date DATE NOT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_receipt IS '生产入库表';

CREATE UNIQUE INDEX uk_production_receipt_no ON production_receipt(receipt_no, tenant_id, deleted);
CREATE INDEX idx_production_receipt_order_id ON production_receipt(order_id);
CREATE INDEX idx_production_receipt_product_id ON production_receipt(product_id);
CREATE INDEX idx_production_receipt_receipt_date ON production_receipt(receipt_date);
CREATE INDEX idx_production_receipt_status ON production_receipt(status);
CREATE INDEX idx_production_receipt_tenant_id ON production_receipt(tenant_id);

CREATE TRIGGER trg_production_receipt_update_time
  BEFORE UPDATE ON production_receipt
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 5. 生产报工表
DROP TABLE IF EXISTS production_report CASCADE;
CREATE TABLE production_report (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  report_no VARCHAR(50) NOT NULL,
  order_id BIGINT NOT NULL,
  order_no VARCHAR(50) NOT NULL,
  process_id BIGINT DEFAULT NULL,
  process_name VARCHAR(100) DEFAULT NULL,
  work_date DATE NOT NULL,
  work_hours DECIMAL(10,2) NOT NULL,
  output_quantity DECIMAL(18,4) NOT NULL,
  qualified_quantity DECIMAL(18,4) NOT NULL,
  unqualified_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
  worker_id BIGINT DEFAULT NULL,
  worker_name VARCHAR(50) DEFAULT NULL,
  equipment_id BIGINT DEFAULT NULL,
  equipment_name VARCHAR(100) DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_report IS '生产报工表';

CREATE UNIQUE INDEX uk_production_report_no ON production_report(report_no, tenant_id, deleted);
CREATE INDEX idx_production_report_order_id ON production_report(order_id);
CREATE INDEX idx_production_report_work_date ON production_report(work_date);
CREATE INDEX idx_production_report_worker_id ON production_report(worker_id);
CREATE INDEX idx_production_report_tenant_id ON production_report(tenant_id);

CREATE TRIGGER trg_production_report_update_time
  BEFORE UPDATE ON production_report
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 6. 生产质检表
DROP TABLE IF EXISTS production_quality CASCADE;
CREATE TABLE production_quality (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  check_no VARCHAR(50) NOT NULL,
  receipt_id BIGINT DEFAULT NULL,
  order_id BIGINT NOT NULL,
  order_no VARCHAR(50) NOT NULL,
  product_id BIGINT NOT NULL,
  product_code VARCHAR(50) NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  check_date DATE NOT NULL,
  check_quantity DECIMAL(18,4) NOT NULL,
  qualified_quantity DECIMAL(18,4) NOT NULL,
  unqualified_quantity DECIMAL(18,4) NOT NULL DEFAULT 0,
  check_result SMALLINT NOT NULL,
  defect_description VARCHAR(500) DEFAULT NULL,
  checker_id BIGINT DEFAULT NULL,
  checker_name VARCHAR(50) DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_quality IS '生产质检表';

CREATE UNIQUE INDEX uk_production_quality_no ON production_quality(check_no, tenant_id, deleted);
CREATE INDEX idx_production_quality_receipt_id ON production_quality(receipt_id);
CREATE INDEX idx_production_quality_order_id ON production_quality(order_id);
CREATE INDEX idx_production_quality_product_id ON production_quality(product_id);
CREATE INDEX idx_production_quality_check_date ON production_quality(check_date);
CREATE INDEX idx_production_quality_tenant_id ON production_quality(tenant_id);

CREATE TRIGGER trg_production_quality_update_time
  BEFORE UPDATE ON production_quality
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 7. 计划物料表
DROP TABLE IF EXISTS production_plan_material CASCADE;
CREATE TABLE production_plan_material (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  plan_id BIGINT NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(50) NOT NULL,
  material_name VARCHAR(200) NOT NULL,
  specification VARCHAR(200) DEFAULT NULL,
  unit VARCHAR(20) DEFAULT NULL,
  required_quantity DECIMAL(18,4) NOT NULL,
  current_stock DECIMAL(18,4) NOT NULL,
  shortage_quantity DECIMAL(18,4) NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_plan_material IS '计划物料表';

CREATE INDEX idx_production_plan_material_plan_id ON production_plan_material(plan_id);
CREATE INDEX idx_production_plan_material_material_id ON production_plan_material(material_id);
CREATE INDEX idx_production_plan_material_tenant_id ON production_plan_material(tenant_id);

CREATE TRIGGER trg_production_plan_material_update_time
  BEFORE UPDATE ON production_plan_material
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 8. 物料清单表(BOM)
DROP TABLE IF EXISTS production_bom CASCADE;
CREATE TABLE production_bom (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  parent_product_id BIGINT NOT NULL,
  parent_product_code VARCHAR(50) NOT NULL,
  parent_product_name VARCHAR(200) NOT NULL,
  material_id BIGINT NOT NULL,
  material_code VARCHAR(50) NOT NULL,
  material_name VARCHAR(200) NOT NULL,
  specification VARCHAR(200) DEFAULT NULL,
  unit VARCHAR(20) DEFAULT NULL,
  quantity DECIMAL(18,4) NOT NULL,
  material_type SMALLINT NOT NULL,
  is_optional SMALLINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE production_bom IS '物料清单表(BOM)';
COMMENT ON COLUMN production_bom.material_type IS '物料类型(1-原料,2-半成品,3-辅料)';
COMMENT ON COLUMN production_bom.is_optional IS '是否可选(0-否,1-是)';

CREATE INDEX idx_production_bom_parent_product_id ON production_bom(parent_product_id);
CREATE INDEX idx_production_bom_material_id ON production_bom(material_id);
CREATE INDEX idx_production_bom_tenant_id ON production_bom(tenant_id);

CREATE TRIGGER trg_production_bom_update_time
  BEFORE UPDATE ON production_bom
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 插入演示数据
INSERT INTO production_plan (
  plan_no, plan_name, plan_date, plan_quantity, status,
  product_id, product_code, product_name, delivery_date, tenant_id
) VALUES 
('PP20260217001', '2026年2月生产计划A', '2026-02-17', 1000.0000, 1,
 1, 'PROD001', '产品A', '2026-03-01', 1),
('PP20260217002', '2026年2月生产计划B', '2026-02-17', 500.0000, 0,
 2, 'PROD002', '产品B', '2026-03-15', 1);

INSERT INTO production_order (
  order_no, plan_id, plan_no, order_date, order_quantity, status,
  product_id, product_code, product_name, delivery_date, tenant_id
) VALUES 
('PO20260217001', 1, 'PP20260217001', '2026-02-17', 1000.0000, 2,
 1, 'PROD001', '产品A', '2026-03-01', 1);

INSERT INTO production_bom (
  parent_product_id, parent_product_code, parent_product_name,
  material_id, material_code, material_name, specification, unit,
  quantity, material_type, tenant_id
) VALUES 
(1, 'PROD001', '产品A', 101, 'MAT001', '原料A', '规格A', 'kg', 2.0000, 1, 1),
(1, 'PROD001', '产品A', 102, 'MAT002', '原料B', '规格B', 'kg', 1.5000, 1, 1),
(1, 'PROD001', '产品A', 103, 'MAT003', '辅料A', '规格A', 'kg', 0.5000, 3, 1);

-- 生产计划完成情况视图
CREATE OR REPLACE VIEW v_production_plan_summary AS
SELECT 
  pp.id AS plan_id,
  pp.plan_no,
  pp.plan_name,
  pp.plan_date,
  pp.plan_quantity,
  pp.status AS plan_status,
  po.id AS order_id,
  po.order_no,
  po.order_quantity,
  po.completed_quantity,
  ROUND(po.completed_quantity / pp.plan_quantity * 100, 2) AS completion_rate
FROM production_plan pp
LEFT JOIN production_order po ON pp.id = po.plan_id AND po.deleted = 0
WHERE pp.deleted = 0;

-- 复合索引
CREATE INDEX idx_production_order_status_date ON production_order(status, order_date);
CREATE INDEX idx_production_material_order_status ON production_material(order_id, status);
CREATE INDEX idx_production_receipt_order_status ON production_receipt(order_id, status);

-- 创建应用用户
CREATE USER qooerp_production WITH PASSWORD 'qooerp_production_2026';

-- 授予权限
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_production;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_production;
