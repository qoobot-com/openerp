-- ============================================================================
-- QooERP 生产管理模块数据库脚本
-- ============================================================================
-- 数据库名称: qooerp_production
-- 字符集: utf8mb4
-- 创建日期: 2026-02-17
-- ============================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS qooerp_production 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE qooerp_production;

-- ============================================================================
-- 表结构创建
-- ============================================================================

-- 1. 生产计划表
CREATE TABLE IF NOT EXISTS production_plan (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  plan_no VARCHAR(50) NOT NULL COMMENT '计划编号',
  plan_name VARCHAR(200) DEFAULT NULL COMMENT '计划名称',
  plan_date DATE NOT NULL COMMENT '计划日期',
  plan_quantity DECIMAL(18,4) NOT NULL COMMENT '计划数量',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已审核,2-已下达,3-已完成,4-已取消)',
  product_id BIGINT(20) NOT NULL COMMENT '产品ID',
  product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
  product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
  delivery_date DATE DEFAULT NULL COMMENT '交货日期',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_plan_no (plan_no, tenant_id, deleted),
  KEY idx_product_id (product_id),
  KEY idx_plan_date (plan_date),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产计划表';

-- 2. 生产订单表
CREATE TABLE IF NOT EXISTS production_order (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
  plan_id BIGINT(20) DEFAULT NULL COMMENT '计划ID',
  plan_no VARCHAR(50) DEFAULT NULL COMMENT '计划编号',
  order_date DATE NOT NULL COMMENT '订单日期',
  order_quantity DECIMAL(18,4) NOT NULL COMMENT '订单数量',
  completed_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '完成数量',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已审核,2-已下达,3-生产中,4-已完成,5-已关闭)',
  product_id BIGINT(20) NOT NULL COMMENT '产品ID',
  product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
  product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
  start_date DATE DEFAULT NULL COMMENT '开始日期',
  end_date DATE DEFAULT NULL COMMENT '完成日期',
  delivery_date DATE DEFAULT NULL COMMENT '交货日期',
  workshop_id BIGINT(20) DEFAULT NULL COMMENT '车间ID',
  workshop_name VARCHAR(100) DEFAULT NULL COMMENT '车间名称',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no, tenant_id, deleted),
  KEY idx_plan_id (plan_id),
  KEY idx_product_id (product_id),
  KEY idx_order_date (order_date),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产订单表';

-- 3. 生产领料表
CREATE TABLE IF NOT EXISTS production_material (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  material_no VARCHAR(50) NOT NULL COMMENT '领料单号',
  order_id BIGINT(20) NOT NULL COMMENT '订单ID',
  order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
  material_id BIGINT(20) NOT NULL COMMENT '物料ID',
  material_code VARCHAR(50) NOT NULL COMMENT '物料编码',
  material_name VARCHAR(200) NOT NULL COMMENT '物料名称',
  specification VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
  unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
  request_quantity DECIMAL(18,4) NOT NULL COMMENT '请领数量',
  issued_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '实发数量',
  returned_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '退回数量',
  warehouse_id BIGINT(20) DEFAULT NULL COMMENT '仓库ID',
  warehouse_name VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已审核,2-已领料)',
  issue_date DATE DEFAULT NULL COMMENT '领料日期',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_material_no (material_no, tenant_id, deleted),
  KEY idx_order_id (order_id),
  KEY idx_material_id (material_id),
  KEY idx_issue_date (issue_date),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产领料表';

-- 4. 生产入库表
CREATE TABLE IF NOT EXISTS production_receipt (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  receipt_no VARCHAR(50) NOT NULL COMMENT '入库单号',
  order_id BIGINT(20) NOT NULL COMMENT '订单ID',
  order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
  product_id BIGINT(20) NOT NULL COMMENT '产品ID',
  product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
  product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
  receipt_quantity DECIMAL(18,4) NOT NULL COMMENT '入库数量',
  qualified_quantity DECIMAL(18,4) NOT NULL COMMENT '合格数量',
  unqualified_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '不合格数量',
  warehouse_id BIGINT(20) DEFAULT NULL COMMENT '仓库ID',
  warehouse_name VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
  quality_status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '质检状态(0-待质检,1-合格,2-不合格)',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已审核,2-已入库)',
  receipt_date DATE NOT NULL COMMENT '入库日期',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_receipt_no (receipt_no, tenant_id, deleted),
  KEY idx_order_id (order_id),
  KEY idx_product_id (product_id),
  KEY idx_receipt_date (receipt_date),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产入库表';

-- 5. 生产报工表
CREATE TABLE IF NOT EXISTS production_report (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  report_no VARCHAR(50) NOT NULL COMMENT '报工单号',
  order_id BIGINT(20) NOT NULL COMMENT '订单ID',
  order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
  process_id BIGINT(20) DEFAULT NULL COMMENT '工序ID',
  process_name VARCHAR(100) DEFAULT NULL COMMENT '工序名称',
  work_date DATE NOT NULL COMMENT '工作日期',
  work_hours DECIMAL(10,2) NOT NULL COMMENT '工作时长(小时)',
  output_quantity DECIMAL(18,4) NOT NULL COMMENT '产出数量',
  qualified_quantity DECIMAL(18,4) NOT NULL COMMENT '合格数量',
  unqualified_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '不合格数量',
  worker_id BIGINT(20) DEFAULT NULL COMMENT '工人ID',
  worker_name VARCHAR(50) DEFAULT NULL COMMENT '工人姓名',
  equipment_id BIGINT(20) DEFAULT NULL COMMENT '设备ID',
  equipment_name VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_report_no (report_no, tenant_id, deleted),
  KEY idx_order_id (order_id),
  KEY idx_work_date (work_date),
  KEY idx_worker_id (worker_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产报工表';

-- 6. 生产质检表
CREATE TABLE IF NOT EXISTS production_quality (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  check_no VARCHAR(50) NOT NULL COMMENT '质检单号',
  receipt_id BIGINT(20) DEFAULT NULL COMMENT '入库单ID',
  order_id BIGINT(20) NOT NULL COMMENT '订单ID',
  order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
  product_id BIGINT(20) NOT NULL COMMENT '产品ID',
  product_code VARCHAR(50) NOT NULL COMMENT '产品编码',
  product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
  check_date DATE NOT NULL COMMENT '质检日期',
  check_quantity DECIMAL(18,4) NOT NULL COMMENT '质检数量',
  qualified_quantity DECIMAL(18,4) NOT NULL COMMENT '合格数量',
  unqualified_quantity DECIMAL(18,4) NOT NULL DEFAULT 0 COMMENT '不合格数量',
  check_result TINYINT(4) NOT NULL COMMENT '质检结果(0-待检,1-合格,2-不合格)',
  defect_description VARCHAR(500) DEFAULT NULL COMMENT '缺陷描述',
  checker_id BIGINT(20) DEFAULT NULL COMMENT '质检人ID',
  checker_name VARCHAR(50) DEFAULT NULL COMMENT '质检人姓名',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_check_no (check_no, tenant_id, deleted),
  KEY idx_receipt_id (receipt_id),
  KEY idx_order_id (order_id),
  KEY idx_product_id (product_id),
  KEY idx_check_date (check_date),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生产质检表';

-- 7. 计划物料表
CREATE TABLE IF NOT EXISTS production_plan_material (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  plan_id BIGINT(20) NOT NULL COMMENT '计划ID',
  material_id BIGINT(20) NOT NULL COMMENT '物料ID',
  material_code VARCHAR(50) NOT NULL COMMENT '物料编码',
  material_name VARCHAR(200) NOT NULL COMMENT '物料名称',
  specification VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
  unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
  required_quantity DECIMAL(18,4) NOT NULL COMMENT '需求数量',
  current_stock DECIMAL(18,4) NOT NULL COMMENT '当前库存',
  shortage_quantity DECIMAL(18,4) NOT NULL COMMENT '短缺数量',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  KEY idx_plan_id (plan_id),
  KEY idx_material_id (material_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='计划物料表';

-- 8. 物料清单表(BOM)
CREATE TABLE IF NOT EXISTS production_bom (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  parent_product_id BIGINT(20) NOT NULL COMMENT '父产品ID',
  parent_product_code VARCHAR(50) NOT NULL COMMENT '父产品编码',
  parent_product_name VARCHAR(200) NOT NULL COMMENT '父产品名称',
  material_id BIGINT(20) NOT NULL COMMENT '物料ID',
  material_code VARCHAR(50) NOT NULL COMMENT '物料编码',
  material_name VARCHAR(200) NOT NULL COMMENT '物料名称',
  specification VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
  unit VARCHAR(20) DEFAULT NULL COMMENT '单位',
  quantity DECIMAL(18,4) NOT NULL COMMENT '用量',
  material_type TINYINT(4) NOT NULL COMMENT '物料类型(1-原料,2-半成品,3-辅料)',
  is_optional TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否可选(0-否,1-是)',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)',
  PRIMARY KEY (id),
  KEY idx_parent_product_id (parent_product_id),
  KEY idx_material_id (material_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料清单表';

-- ============================================================================
-- 初始化数据
-- ============================================================================

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

-- ============================================================================
-- 视图创建
-- ============================================================================

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

-- ============================================================================
-- 存储过程创建
-- ============================================================================

-- 生产进度统计存储过程
DELIMITER $$
CREATE PROCEDURE sp_production_progress_statistics(IN p_order_id BIGINT)
BEGIN
  SELECT 
    po.order_no,
    po.order_quantity,
    COALESCE(pr.qualified_quantity, 0) AS completed_quantity,
    COALESCE(pr.work_hours, 0) AS work_hours,
    CASE 
      WHEN po.order_quantity > 0 THEN 
        ROUND(COALESCE(pr.qualified_quantity, 0) / po.order_quantity * 100, 2)
      ELSE 0 
    END AS progress_percentage
  FROM production_order po
  LEFT JOIN (
    SELECT order_id, 
           SUM(qualified_quantity) AS qualified_quantity, 
           SUM(work_hours) AS work_hours
    FROM production_report 
    WHERE deleted = 0
    GROUP BY order_id
  ) pr ON po.id = pr.order_id
  WHERE po.id = p_order_id AND po.deleted = 0;
END$$
DELIMITER ;

-- ============================================================================
-- 函数创建
-- ============================================================================

-- 获取生产订单可用库存函数
DELIMITER $$
CREATE FUNCTION fn_production_available_stock(p_order_id BIGINT, p_material_id BIGINT) 
RETURNS DECIMAL(18,4)
DETERMINISTIC
READS SQL DATA
BEGIN
  DECLARE v_required_quantity DECIMAL(18,4);
  DECLARE v_issued_quantity DECIMAL(18,4);
  DECLARE v_available_quantity DECIMAL(18,4);
  
  -- 获取订单物料需求数量
  SELECT request_quantity INTO v_required_quantity
  FROM production_material
  WHERE order_id = p_order_id 
    AND material_id = p_material_id 
    AND deleted = 0;
  
  -- 获取已领料数量
  SELECT COALESCE(SUM(issued_quantity), 0) INTO v_issued_quantity
  FROM production_material
  WHERE order_id = p_order_id 
    AND material_id = p_material_id 
    AND deleted = 0;
  
  -- 计算可用库存
  SET v_available_quantity = v_required_quantity - v_issued_quantity;
  
  RETURN v_available_quantity;
END$$
DELIMITER ;

-- ============================================================================
-- 触发器创建
-- ============================================================================

-- 生产订单状态变更触发器
DELIMITER $$
CREATE TRIGGER trg_production_order_status_change
BEFORE UPDATE ON production_order
FOR EACH ROW
BEGIN
  -- 当订单从生产中变更为已完成时
  IF OLD.status = 3 AND NEW.status = 4 THEN
    SET NEW.end_date = CURDATE();
  END IF;
  
  -- 当订单从已审核变更为已下达时
  IF OLD.status = 1 AND NEW.status = 2 THEN
    SET NEW.start_date = CURDATE();
  END IF;
END$$
DELIMITER ;

-- ============================================================================
-- 索引优化
-- ============================================================================

-- 复合索引
CREATE INDEX idx_order_status_date ON production_order(status, order_date);
CREATE INDEX idx_material_order_status ON production_material(order_id, status);
CREATE INDEX idx_receipt_order_status ON production_receipt(order_id, status);

-- ============================================================================
-- 数据库权限
-- ============================================================================

-- 创建应用用户
CREATE USER IF NOT EXISTS 'qooerp_production'@'%' IDENTIFIED BY 'qooerp_production_2026';

-- 授予权限
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_production.* 
  TO 'qooerp_production'@'%';

FLUSH PRIVILEGES;

-- ============================================================================
-- 脚本结束
-- ============================================================================

-- 执行完成后,验证数据库创建
SHOW TABLES;

-- 验证表结构
DESC production_plan;
DESC production_order;
DESC production_material;
DESC production_receipt;
DESC production_report;
DESC production_quality;
DESC production_plan_material;
DESC production_bom;
