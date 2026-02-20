-- ============================================================================
-- QooERP 采购管理 - PostgreSQL 数据库初始化脚本
-- ============================================================================
-- 版本：v1.0
-- 创建日期：2026-02-18
-- 数据库：PostgreSQL 15+
-- ============================================================================

-- 创建数据库（如不存在）
SELECT 'CREATE DATABASE qooerp_purchase WITH ENCODING = ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'qooerp_purchase');

\c qooerp_purchase

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 采购订单表
-- ============================================================================
CREATE TABLE purchase_order (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    order_code VARCHAR(32) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE NOT NULL,
    order_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    payable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    order_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    purchaser_id BIGINT DEFAULT NULL,
    purchaser_name VARCHAR(64),
    warehouse_id BIGINT DEFAULT NULL,
    warehouse_name VARCHAR(64),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

-- 采购订单表索引
CREATE UNIQUE INDEX uk_order_code ON purchase_order (tenant_id, order_code);
CREATE INDEX idx_po_supplier_id ON purchase_order (supplier_id);
CREATE INDEX idx_po_order_status ON purchase_order (order_status);
CREATE INDEX idx_po_deleted ON purchase_order (deleted);
CREATE INDEX idx_po_tenant_id ON purchase_order (tenant_id);
CREATE INDEX idx_po_order_date ON purchase_order (order_date);

-- 采购订单表注释
COMMENT ON TABLE purchase_order IS '采购订单表';
COMMENT ON COLUMN purchase_order.order_status IS '订单状态: DRAFT-草稿, PENDING-待审核, APPROVED-已审核, RECEIVING-收货中, COMPLETED-已完成, CANCELLED-已取消';

-- ============================================================================
-- 订单明细表
-- ============================================================================
CREATE TABLE purchase_order_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    order_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(32) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    specification VARCHAR(128),
    unit VARCHAR(20),
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    discount_rate DECIMAL(5,2) DEFAULT 0.00,
    discount_amount DECIMAL(18,2) DEFAULT 0.00,
    payable_amount DECIMAL(18,2) NOT NULL,
    received_quantity DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

-- 订单明细表索引
CREATE INDEX idx_pod_order_id ON purchase_order_detail (order_id);
CREATE INDEX idx_pod_material_id ON purchase_order_detail (material_id);
CREATE INDEX idx_pod_deleted ON purchase_order_detail (deleted);
CREATE INDEX idx_pod_tenant_id ON purchase_order_detail (tenant_id);

-- 订单明细表注释
COMMENT ON TABLE purchase_order_detail IS '采购订单明细表';

-- ============================================================================
-- 入库单表
-- ============================================================================
CREATE TABLE purchase_receipt (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    receipt_code VARCHAR(32) NOT NULL,
    order_id BIGINT NOT NULL,
    order_code VARCHAR(32),
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    receipt_date DATE NOT NULL,
    receipt_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    receipt_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    warehouse_id BIGINT DEFAULT NULL,
    warehouse_name VARCHAR(64),
    operator_id BIGINT DEFAULT NULL,
    operator_name VARCHAR(64),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

-- 入库单表索引
CREATE UNIQUE INDEX uk_receipt_code ON purchase_receipt (tenant_id, receipt_code);
CREATE INDEX idx_pr_order_id ON purchase_receipt (order_id);
CREATE INDEX idx_pr_supplier_id ON purchase_receipt (supplier_id);
CREATE INDEX idx_pr_receipt_status ON purchase_receipt (receipt_status);
CREATE INDEX idx_pr_deleted ON purchase_receipt (deleted);
CREATE INDEX idx_pr_tenant_id ON purchase_receipt (tenant_id);
CREATE INDEX idx_pr_receipt_date ON purchase_receipt (receipt_date);

-- 入库单表注释
COMMENT ON TABLE purchase_receipt IS '采购入库单表';
COMMENT ON COLUMN purchase_receipt.receipt_status IS '入库状态: PENDING-待审核, COMPLETED-已完成, CANCELLED-已取消';

-- ============================================================================
-- 入库明细表
-- ============================================================================
CREATE TABLE purchase_receipt_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    receipt_id BIGINT NOT NULL,
    order_detail_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(32) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    specification VARCHAR(128),
    unit VARCHAR(20),
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    batch_no VARCHAR(64),
    production_date DATE,
    expiry_date DATE,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

-- 入库明细表索引
CREATE INDEX idx_prd_receipt_id ON purchase_receipt_detail (receipt_id);
CREATE INDEX idx_prd_material_id ON purchase_receipt_detail (material_id);
CREATE INDEX idx_prd_deleted ON purchase_receipt_detail (deleted);
CREATE INDEX idx_prd_tenant_id ON purchase_receipt_detail (tenant_id);
CREATE INDEX idx_prd_batch_no ON purchase_receipt_detail (batch_no);

-- 入库明细表注释
COMMENT ON TABLE purchase_receipt_detail IS '采购入库明细表';

-- ============================================================================
-- 退货单表
-- ============================================================================
CREATE TABLE purchase_return (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    return_code VARCHAR(32) NOT NULL,
    order_id BIGINT NOT NULL,
    order_code VARCHAR(32),
    receipt_id BIGINT DEFAULT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    return_date DATE NOT NULL,
    return_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    reason VARCHAR(512),
    return_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    warehouse_id BIGINT DEFAULT NULL,
    warehouse_name VARCHAR(64),
    operator_id BIGINT DEFAULT NULL,
    operator_name VARCHAR(64),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0
);

-- 退货单表索引
CREATE UNIQUE INDEX uk_return_code ON purchase_return (tenant_id, return_code);
CREATE INDEX idx_prt_order_id ON purchase_return (order_id);
CREATE INDEX idx_prt_supplier_id ON purchase_return (supplier_id);
CREATE INDEX idx_prt_return_status ON purchase_return (return_status);
CREATE INDEX idx_prt_deleted ON purchase_return (deleted);
CREATE INDEX idx_prt_tenant_id ON purchase_return (tenant_id);
CREATE INDEX idx_prt_return_date ON purchase_return (return_date);

-- 退货单表注释
COMMENT ON TABLE purchase_return IS '采购退货单表';
COMMENT ON COLUMN purchase_return.return_status IS '退货状态: PENDING-待审核, APPROVED-已审核, COMPLETED-已完成, CANCELLED-已取消';

-- ============================================================================
-- 退货明细表
-- ============================================================================
CREATE TABLE purchase_return_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    return_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(32) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    specification VARCHAR(128),
    unit VARCHAR(20),
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    batch_no VARCHAR(64),
    reason VARCHAR(256),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

-- 退货明细表索引
CREATE INDEX idx_prtd_return_id ON purchase_return_detail (return_id);
CREATE INDEX idx_prtd_material_id ON purchase_return_detail (material_id);
CREATE INDEX idx_prtd_deleted ON purchase_return_detail (deleted);
CREATE INDEX idx_prtd_tenant_id ON purchase_return_detail (tenant_id);

-- 退货明细表注释
COMMENT ON TABLE purchase_return_detail IS '采购退货明细表';

-- ============================================================================
-- 创建更新时间触发器
-- ============================================================================
CREATE TRIGGER trg_purchase_order_update_time
    BEFORE UPDATE ON purchase_order
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trg_purchase_order_detail_update_time
    BEFORE UPDATE ON purchase_order_detail
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trg_purchase_receipt_update_time
    BEFORE UPDATE ON purchase_receipt
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trg_purchase_receipt_detail_update_time
    BEFORE UPDATE ON purchase_receipt_detail
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trg_purchase_return_update_time
    BEFORE UPDATE ON purchase_return
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trg_purchase_return_detail_update_time
    BEFORE UPDATE ON purchase_return_detail
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_time();
