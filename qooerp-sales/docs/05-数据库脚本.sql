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

\c qooerp_sales

-- QooERP 销售管理 - 数据库初始化脚本
-- 版本：v1.0
-- 创建日期：2026-02-17

CREATE DATABASE qooerp_sales WITH ENCODING 'UTF8';



-- 销售订单表
CREATE TABLE sales_order (
    id BIGSERIAL NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    order_code VARCHAR(32) NOT NULL,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    order_date DATE NOT NULL,
    delivery_date DATE NOT NULL,
    order_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    payable_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    order_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    order_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    salesperson_id BIGINT DEFAULT NULL,
    salesperson_name VARCHAR(64),
    warehouse_id BIGINT DEFAULT NULL,
    warehouse_name VARCHAR(64),
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_order_code (tenant_id, order_code),
    INDEX idx_customer_id (customer_id),
    INDEX idx_order_status (order_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- 订单明细表
CREATE TABLE sales_order_detail (
    id BIGSERIAL NOT NULL,
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
    shipped_quantity DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_order_id (order_id),
    INDEX idx_material_id (material_id),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- 发货单表
CREATE TABLE sales_delivery (
    id BIGSERIAL NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    delivery_code VARCHAR(32) NOT NULL,
    order_id BIGINT NOT NULL,
    order_code VARCHAR(32),
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    delivery_date DATE NOT NULL,
    delivery_amount DECIMAL(18,2) NOT NULL,
    logistics_id BIGINT DEFAULT NULL,
    delivery_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_delivery_code (tenant_id, delivery_code),
    INDEX idx_order_id (order_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_delivery_status (delivery_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- 发货明细表
CREATE TABLE sales_delivery_detail (
    id BIGSERIAL NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    delivery_id BIGINT NOT NULL,
    order_detail_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(32) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_delivery_id (delivery_id),
    INDEX idx_material_id (material_id),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- 退货单表
CREATE TABLE sales_return (
    id BIGSERIAL NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    return_code VARCHAR(32) NOT NULL,
    order_id BIGINT NOT NULL,
    order_code VARCHAR(32),
    delivery_id BIGINT DEFAULT NULL,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    return_date DATE NOT NULL,
    return_amount DECIMAL(18,2) NOT NULL,
    reason VARCHAR(512),
    return_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    deleted SMALLINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_return_code (tenant_id, return_code),
    INDEX idx_order_id (order_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_return_status (return_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- 退货明细表
CREATE TABLE sales_return_detail (
    id BIGSERIAL NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    return_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    material_code VARCHAR(32) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    remark VARCHAR(512),
    created_by BIGINT DEFAULT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT DEFAULT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_return_id (return_id),
    INDEX idx_material_id (material_id),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);
