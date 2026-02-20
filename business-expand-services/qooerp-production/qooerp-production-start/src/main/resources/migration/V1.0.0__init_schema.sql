-- ================================================================
-- QooERP 生产管理模块数据库初始化脚本
-- 版本: 1.0.0
-- 创建日期: 2026-02-18
-- ================================================================

-- ================================================================
-- 生产计划表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_plan (
    id BIGSERIAL PRIMARY KEY,
    plan_no VARCHAR(50) NOT NULL UNIQUE COMMENT '计划编号',
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    product_code VARCHAR(100) COMMENT '产品编码',
    plan_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '计划数量',
    completed_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '已完成数量',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    priority INTEGER DEFAULT 0 COMMENT '优先级 0-普通 1-高 2-紧急',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 approved-已批准 in_progress-进行中 completed-已完成 cancelled-已取消',
    mrp_status VARCHAR(20) DEFAULT 'pending' COMMENT 'MRP状态 pending-待计算 calculating-计算中 completed-已完成',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (plan_no, tenant_id)
);

CREATE INDEX idx_production_plan_tenant ON production_plan(tenant_id);
CREATE INDEX idx_production_plan_status ON production_plan(status);
CREATE INDEX idx_production_plan_date ON production_plan(start_date, end_date);
COMMENT ON TABLE production_plan IS '生产计划表';

-- ================================================================
-- 生产订单表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_order (
    id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    plan_id BIGINT COMMENT '生产计划ID',
    plan_no VARCHAR(50) COMMENT '计划编号',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    product_code VARCHAR(100) COMMENT '产品编码',
    order_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '订单数量',
    completed_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '已完成数量',
    qualified_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '合格数量',
    reject_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '不良数量',
    progress_rate DECIMAL(5,2) DEFAULT 0 COMMENT '进度百分比',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    workshop_id BIGINT COMMENT '车间ID',
    workshop_name VARCHAR(200) COMMENT '车间名称',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 pending-待生产 in_production-生产中 completed-已完成 cancelled-已取消',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (order_no, tenant_id)
);

CREATE INDEX idx_production_order_tenant ON production_order(tenant_id);
CREATE INDEX idx_production_order_status ON production_order(status);
CREATE INDEX idx_production_order_plan ON production_order(plan_id);
CREATE INDEX idx_production_order_workshop ON production_order(workshop_id);
COMMENT ON TABLE production_order IS '生产订单表';

-- ================================================================
-- 生产领料表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_material (
    id BIGSERIAL PRIMARY KEY,
    material_no VARCHAR(50) NOT NULL UNIQUE COMMENT '领料单号',
    order_id BIGINT NOT NULL COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    material_id BIGINT NOT NULL COMMENT '物料ID',
    material_name VARCHAR(200) COMMENT '物料名称',
    material_code VARCHAR(100) COMMENT '物料编码',
    required_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '需求数量',
    issued_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '已领数量',
    warehouse_id BIGINT COMMENT '仓库ID',
    warehouse_name VARCHAR(200) COMMENT '仓库名称',
    issue_date DATE COMMENT '领料日期',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 approved-已批准 issued-已领料 completed-已完成',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (material_no, tenant_id)
);

CREATE INDEX idx_production_material_tenant ON production_material(tenant_id);
CREATE INDEX idx_production_material_status ON production_material(status);
CREATE INDEX idx_production_material_order ON production_material(order_id);
COMMENT ON TABLE production_material IS '生产领料表';

-- ================================================================
-- 生产入库表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_receipt (
    id BIGSERIAL PRIMARY KEY,
    receipt_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    order_id BIGINT NOT NULL COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    product_code VARCHAR(100) COMMENT '产品编码',
    receipt_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '入库数量',
    qualified_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '合格数量',
    reject_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '不良数量',
    warehouse_id BIGINT COMMENT '仓库ID',
    warehouse_name VARCHAR(200) COMMENT '仓库名称',
    receipt_date DATE COMMENT '入库日期',
    quality_check_id BIGINT COMMENT '质检单ID',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 approved-已批准 warehoused-已入库 completed-已完成',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (receipt_no, tenant_id)
);

CREATE INDEX idx_production_receipt_tenant ON production_receipt(tenant_id);
CREATE INDEX idx_production_receipt_status ON production_receipt(status);
CREATE INDEX idx_production_receipt_order ON production_receipt(order_id);
COMMENT ON TABLE production_receipt IS '生产入库表';

-- ================================================================
-- 生产报工表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_report (
    id BIGSERIAL PRIMARY KEY,
    report_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报工单号',
    order_id BIGINT NOT NULL COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(200) COMMENT '工序名称',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    report_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '报工数量',
    qualified_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '合格数量',
    reject_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '不良数量',
    work_time DECIMAL(10,2) COMMENT '工时',
    worker_id BIGINT COMMENT '工人ID',
    worker_name VARCHAR(100) COMMENT '工人姓名',
    equipment_id BIGINT COMMENT '设备ID',
    equipment_name VARCHAR(200) COMMENT '设备名称',
    report_date DATE COMMENT '报工日期',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 approved-已批准 completed-已完成',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (report_no, tenant_id)
);

CREATE INDEX idx_production_report_tenant ON production_report(tenant_id);
CREATE INDEX idx_production_report_status ON production_report(status);
CREATE INDEX idx_production_report_order ON production_report(order_id);
CREATE INDEX idx_production_report_process ON production_report(process_id);
COMMENT ON TABLE production_report IS '生产报工表';

-- ================================================================
-- 生产质检表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_quality (
    id BIGSERIAL PRIMARY KEY,
    quality_no VARCHAR(50) NOT NULL UNIQUE COMMENT '质检单号',
    order_id BIGINT NOT NULL COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    report_id BIGINT COMMENT '报工单ID',
    report_no VARCHAR(50) COMMENT '报工单号',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    product_code VARCHAR(100) COMMENT '产品编码',
    inspection_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '检验数量',
    qualified_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '合格数量',
    reject_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '不良数量',
    qualified_rate DECIMAL(5,2) DEFAULT 0 COMMENT '合格率',
    inspector_id BIGINT COMMENT '质检员ID',
    inspector_name VARCHAR(100) COMMENT '质检员姓名',
    inspection_date DATE COMMENT '检验日期',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 inspecting-检验中 passed-合格 rejected-不合格',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (quality_no, tenant_id)
);

CREATE INDEX idx_production_quality_tenant ON production_quality(tenant_id);
CREATE INDEX idx_production_quality_status ON production_quality(status);
CREATE INDEX idx_production_quality_order ON production_quality(order_id);
CREATE INDEX idx_production_quality_report ON production_quality(report_id);
COMMENT ON TABLE production_quality IS '生产质检表';

-- ================================================================
-- 生产工序表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_process (
    id BIGSERIAL PRIMARY KEY,
    process_code VARCHAR(50) NOT NULL UNIQUE COMMENT '工序编码',
    process_name VARCHAR(200) NOT NULL COMMENT '工序名称',
    product_id BIGINT COMMENT '产品ID',
    process_type VARCHAR(50) COMMENT '工序类型',
    standard_time DECIMAL(10,2) COMMENT '标准工时',
    equipment_id BIGINT COMMENT '设备ID',
    equipment_name VARCHAR(200) COMMENT '设备名称',
    process_order INTEGER DEFAULT 0 COMMENT '工序顺序',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态 active-启用 inactive-停用',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (process_code, tenant_id)
);

CREATE INDEX idx_production_process_tenant ON production_process(tenant_id);
CREATE INDEX idx_production_process_product ON production_process(product_id);
COMMENT ON TABLE production_process IS '生产工序表';

-- ================================================================
-- 生产设备表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_equipment (
    id BIGSERIAL PRIMARY KEY,
    equipment_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编码',
    equipment_name VARCHAR(200) NOT NULL COMMENT '设备名称',
    equipment_type VARCHAR(50) COMMENT '设备类型',
    workshop_id BIGINT COMMENT '车间ID',
    workshop_name VARCHAR(200) COMMENT '车间名称',
    specification VARCHAR(500) COMMENT '规格型号',
    manufacturer VARCHAR(200) COMMENT '制造商',
    purchase_date DATE COMMENT '采购日期',
    status VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '状态 normal-正常 maintenance-维保中 fault-故障 scrapped-报废',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (equipment_code, tenant_id)
);

CREATE INDEX idx_production_equipment_tenant ON production_equipment(tenant_id);
CREATE INDEX idx_production_equipment_workshop ON production_equipment(workshop_id);
CREATE INDEX idx_production_equipment_status ON production_equipment(status);
COMMENT ON TABLE production_equipment IS '生产设备表';

-- ================================================================
-- 生产排程表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_schedule (
    id BIGSERIAL PRIMARY KEY,
    schedule_code VARCHAR(50) NOT NULL UNIQUE COMMENT '排程编码',
    schedule_name VARCHAR(200) NOT NULL COMMENT '排程名称',
    order_id BIGINT COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(200) COMMENT '工序名称',
    equipment_id BIGINT COMMENT '设备ID',
    equipment_name VARCHAR(200) COMMENT '设备名称',
    worker_id BIGINT COMMENT '工人ID',
    worker_name VARCHAR(100) COMMENT '工人姓名',
    planned_start_time TIMESTAMP COMMENT '计划开始时间',
    planned_end_time TIMESTAMP COMMENT '计划结束时间',
    actual_start_time TIMESTAMP COMMENT '实际开始时间',
    actual_end_time TIMESTAMP COMMENT '实际结束时间',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态 pending-待开始 in_progress-进行中 completed-已完成 cancelled-已取消',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (schedule_code, tenant_id)
);

CREATE INDEX idx_production_schedule_tenant ON production_schedule(tenant_id);
CREATE INDEX idx_production_schedule_status ON production_schedule(status);
CREATE INDEX idx_production_schedule_equipment ON production_schedule(equipment_id);
CREATE INDEX idx_production_schedule_time ON production_schedule(planned_start_time, planned_end_time);
COMMENT ON TABLE production_schedule IS '生产排程表';

-- ================================================================
-- 生产报废表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_scrap (
    id BIGSERIAL PRIMARY KEY,
    scrap_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报废单号',
    order_id BIGINT COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    report_id BIGINT COMMENT '报工单ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    product_code VARCHAR(100) COMMENT '产品编码',
    scrap_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '报废数量',
    scrap_reason VARCHAR(500) COMMENT '报废原因',
    scrap_type VARCHAR(50) COMMENT '报废类型',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(200) COMMENT '工序名称',
    scrap_date DATE COMMENT '报废日期',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 approved-已批准 completed-已完成',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (scrap_no, tenant_id)
);

CREATE INDEX idx_production_scrap_tenant ON production_scrap(tenant_id);
CREATE INDEX idx_production_scrap_status ON production_scrap(status);
CREATE INDEX idx_production_scrap_order ON production_scrap(order_id);
COMMENT ON TABLE production_scrap IS '生产报废表';

-- ================================================================
-- 生产返工表
-- ================================================================
CREATE TABLE IF NOT EXISTS production_rework (
    id BIGSERIAL PRIMARY KEY,
    rework_no VARCHAR(50) NOT NULL UNIQUE COMMENT '返工单号',
    order_id BIGINT COMMENT '生产订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    quality_id BIGINT COMMENT '质检单ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) COMMENT '产品名称',
    product_code VARCHAR(100) COMMENT '产品编码',
    rework_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '返工数量',
    rework_reason VARCHAR(500) COMMENT '返工原因',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(200) COMMENT '工序名称',
    rework_date DATE COMMENT '返工日期',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态 draft-草稿 in_progress-进行中 completed-已完成',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    created_by VARCHAR(64) COMMENT '创建人',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(64) COMMENT '更新人',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER NOT NULL DEFAULT 0,
    UNIQUE (rework_no, tenant_id)
);

CREATE INDEX idx_production_rework_tenant ON production_rework(tenant_id);
CREATE INDEX idx_production_rework_status ON production_rework(status);
CREATE INDEX idx_production_rework_order ON production_rework(order_id);
COMMENT ON TABLE production_rework IS '生产返工表';
