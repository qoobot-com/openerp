-- QooERP Inventory Module Database Schema
-- Version: 1.0

-- 物料分类表
CREATE TABLE IF NOT EXISTS inventory_material_category (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    category_code VARCHAR(50) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    level INT DEFAULT 1,
    path VARCHAR(500) DEFAULT '',
    sort_order INT DEFAULT 0,
    category_status VARCHAR(20) DEFAULT 'ENABLED',
    description VARCHAR(500),
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_category_tenant ON inventory_material_category(tenant_id);
CREATE INDEX idx_category_parent ON inventory_material_category(parent_id);

-- 仓库表
CREATE TABLE IF NOT EXISTS inventory_warehouse (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    warehouse_code VARCHAR(50) NOT NULL,
    warehouse_name VARCHAR(100) NOT NULL,
    warehouse_type VARCHAR(20) DEFAULT 'MAIN',
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    address VARCHAR(500),
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    warehouse_status VARCHAR(20) DEFAULT 'ENABLED',
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE UNIQUE INDEX idx_warehouse_code_tenant ON inventory_warehouse(warehouse_code, tenant_id);
CREATE INDEX idx_warehouse_tenant ON inventory_warehouse(tenant_id);

-- 物料表
CREATE TABLE IF NOT EXISTS inventory_material (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    material_spec VARCHAR(100),
    material_unit VARCHAR(20) NOT NULL,
    category_id BIGINT,
    safety_stock NUMERIC(15, 3) DEFAULT 0,
    max_stock NUMERIC(15, 3) DEFAULT 0,
    material_status VARCHAR(20) DEFAULT 'ENABLED',
    barcode VARCHAR(50),
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE UNIQUE INDEX idx_material_code_tenant ON inventory_material(material_code, tenant_id);
CREATE INDEX idx_material_category ON inventory_material(category_id);
CREATE INDEX idx_material_tenant ON inventory_material(tenant_id);

-- 库存表
CREATE TABLE IF NOT EXISTS inventory_stock (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity NUMERIC(15, 3) DEFAULT 0,
    available_quantity NUMERIC(15, 3) DEFAULT 0,
    locked_quantity NUMERIC(15, 3) DEFAULT 0,
    in_transit_quantity NUMERIC(15, 3) DEFAULT 0,
    stock_status VARCHAR(20) DEFAULT 'NORMAL',
    batch_no VARCHAR(50),
    production_date TIMESTAMP,
    expiry_date TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_stock_warehouse ON inventory_stock(warehouse_id);
CREATE INDEX idx_stock_material ON inventory_stock(material_id);
CREATE INDEX idx_stock_batch ON inventory_stock(batch_no);
CREATE UNIQUE INDEX idx_stock_warehouse_material_batch ON inventory_stock(warehouse_id, material_id, batch_no) WHERE deleted = FALSE;

-- 入库单表
CREATE TABLE IF NOT EXISTS inventory_stock_inbound (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    inbound_no VARCHAR(50) NOT NULL,
    inbound_type VARCHAR(20) NOT NULL,
    source_type VARCHAR(20),
    source_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    supplier_id BIGINT,
    inbound_status VARCHAR(20) DEFAULT 'DRAFT',
    total_quantity NUMERIC(15, 3) DEFAULT 0,
    total_amount NUMERIC(15, 2) DEFAULT 0,
    inbound_date TIMESTAMP,
    operator_id BIGINT,
    operator_name VARCHAR(50),
    auditor_id BIGINT,
    auditor_name VARCHAR(50),
    audit_time TIMESTAMP,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE UNIQUE INDEX idx_inbound_no ON inventory_stock_inbound(inbound_no);
CREATE INDEX idx_inbound_warehouse ON inventory_stock_inbound(warehouse_id);
CREATE INDEX idx_inbound_supplier ON inventory_stock_inbound(supplier_id);
CREATE INDEX idx_inbound_tenant ON inventory_stock_inbound(tenant_id);

-- 入库单明细表
CREATE TABLE IF NOT EXISTS inventory_stock_inbound_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    inbound_id BIGINT NOT NULL,
    inbound_no VARCHAR(50) NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    material_spec VARCHAR(100),
    material_unit VARCHAR(20) NOT NULL,
    quantity NUMERIC(15, 3) NOT NULL,
    unit_price NUMERIC(15, 2) DEFAULT 0,
    total_price NUMERIC(15, 2) DEFAULT 0,
    batch_no VARCHAR(50),
    production_date TIMESTAMP,
    expiry_date TIMESTAMP,
    actual_quantity NUMERIC(15, 3),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_inbound_detail_inbound ON inventory_stock_inbound_detail(inbound_id);
CREATE INDEX idx_inbound_detail_material ON inventory_stock_inbound_detail(material_id);

-- 出库单表
CREATE TABLE IF NOT EXISTS inventory_stock_outbound (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    outbound_no VARCHAR(50) NOT NULL,
    outbound_type VARCHAR(20) NOT NULL,
    target_type VARCHAR(20),
    target_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    customer_id BIGINT,
    outbound_status VARCHAR(20) DEFAULT 'DRAFT',
    total_quantity NUMERIC(15, 3) DEFAULT 0,
    total_amount NUMERIC(15, 2) DEFAULT 0,
    outbound_date TIMESTAMP,
    operator_id BIGINT,
    operator_name VARCHAR(50),
    auditor_id BIGINT,
    auditor_name VARCHAR(50),
    audit_time TIMESTAMP,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE UNIQUE INDEX idx_outbound_no ON inventory_stock_outbound(outbound_no);
CREATE INDEX idx_outbound_warehouse ON inventory_stock_outbound(warehouse_id);
CREATE INDEX idx_outbound_customer ON inventory_stock_outbound(customer_id);
CREATE INDEX idx_outbound_tenant ON inventory_stock_outbound(tenant_id);

-- 出库单明细表
CREATE TABLE IF NOT EXISTS inventory_stock_outbound_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    outbound_id BIGINT NOT NULL,
    outbound_no VARCHAR(50) NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    material_spec VARCHAR(100),
    material_unit VARCHAR(20) NOT NULL,
    quantity NUMERIC(15, 3) NOT NULL,
    unit_price NUMERIC(15, 2) DEFAULT 0,
    total_price NUMERIC(15, 2) DEFAULT 0,
    batch_no VARCHAR(50),
    actual_quantity NUMERIC(15, 3),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_outbound_detail_outbound ON inventory_stock_outbound_detail(outbound_id);
CREATE INDEX idx_outbound_detail_material ON inventory_stock_outbound_detail(material_id);

-- 调拨单表
CREATE TABLE IF NOT EXISTS inventory_transfer (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    transfer_no VARCHAR(50) NOT NULL,
    transfer_type VARCHAR(20) DEFAULT 'NORMAL',
    source_warehouse_id BIGINT NOT NULL,
    source_warehouse_name VARCHAR(100),
    target_warehouse_id BIGINT NOT NULL,
    target_warehouse_name VARCHAR(100),
    transfer_status VARCHAR(20) DEFAULT 'DRAFT',
    total_quantity NUMERIC(15, 3) DEFAULT 0,
    transfer_date TIMESTAMP,
    operator_id BIGINT,
    operator_name VARCHAR(50),
    auditor_id BIGINT,
    auditor_name VARCHAR(50),
    audit_time TIMESTAMP,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE UNIQUE INDEX idx_transfer_no ON inventory_transfer(transfer_no);
CREATE INDEX idx_transfer_source ON inventory_transfer(source_warehouse_id);
CREATE INDEX idx_transfer_target ON inventory_transfer(target_warehouse_id);
CREATE INDEX idx_transfer_tenant ON inventory_transfer(tenant_id);

-- 调拨单明细表
CREATE TABLE IF NOT EXISTS inventory_transfer_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    transfer_id BIGINT NOT NULL,
    transfer_no VARCHAR(50) NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    material_spec VARCHAR(100),
    material_unit VARCHAR(20) NOT NULL,
    quantity NUMERIC(15, 3) NOT NULL,
    actual_quantity NUMERIC(15, 3),
    batch_no VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_transfer_detail_transfer ON inventory_transfer_detail(transfer_id);
CREATE INDEX idx_transfer_detail_material ON inventory_transfer_detail(material_id);

-- 盘点单表
CREATE TABLE IF NOT EXISTS inventory_check (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    check_no VARCHAR(50) NOT NULL,
    check_type VARCHAR(20) DEFAULT 'FULL',
    warehouse_id BIGINT NOT NULL,
    warehouse_name VARCHAR(100),
    check_status VARCHAR(20) DEFAULT 'DRAFT',
    check_date TIMESTAMP,
    total_items INT DEFAULT 0,
    diff_items INT DEFAULT 0,
    operator_id BIGINT,
    operator_name VARCHAR(50),
    auditor_id BIGINT,
    auditor_name VARCHAR(50),
    audit_time TIMESTAMP,
    create_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE UNIQUE INDEX idx_check_no ON inventory_check(check_no);
CREATE INDEX idx_check_warehouse ON inventory_check(warehouse_id);
CREATE INDEX idx_check_tenant ON inventory_check(tenant_id);

-- 盘点单明细表
CREATE TABLE IF NOT EXISTS inventory_check_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    check_id BIGINT NOT NULL,
    check_no VARCHAR(50) NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    material_spec VARCHAR(100),
    material_unit VARCHAR(20) NOT NULL,
    system_quantity NUMERIC(15, 3) DEFAULT 0,
    actual_quantity NUMERIC(15, 3),
    diff_quantity NUMERIC(15, 3),
    diff_reason VARCHAR(500),
    batch_no VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_check_detail_check ON inventory_check_detail(check_id);
CREATE INDEX idx_check_detail_material ON inventory_check_detail(material_id);

-- 库存记录表
CREATE TABLE IF NOT EXISTS inventory_stock_record (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    record_type VARCHAR(20) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    business_no VARCHAR(50),
    warehouse_id BIGINT NOT NULL,
    warehouse_name VARCHAR(100),
    material_id BIGINT NOT NULL,
    material_code VARCHAR(50) NOT NULL,
    material_name VARCHAR(100) NOT NULL,
    material_spec VARCHAR(100),
    material_unit VARCHAR(20) NOT NULL,
    before_quantity NUMERIC(15, 3) DEFAULT 0,
    change_quantity NUMERIC(15, 3) NOT NULL,
    after_quantity NUMERIC(15, 3) NOT NULL,
    batch_no VARCHAR(50),
    operator_id BIGINT,
    operator_name VARCHAR(50),
    record_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    remark VARCHAR(500)
);

CREATE INDEX idx_record_tenant ON inventory_stock_record(tenant_id);
CREATE INDEX idx_record_warehouse ON inventory_stock_record(warehouse_id);
CREATE INDEX idx_record_material ON inventory_stock_record(material_id);
CREATE INDEX idx_record_type ON inventory_stock_record(record_type);
CREATE INDEX idx_record_business_no ON inventory_stock_record(business_no);
CREATE INDEX idx_record_time ON inventory_stock_record(record_time);
