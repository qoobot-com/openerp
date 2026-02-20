-- ========================================
-- qooerp-scm 供应链管理模块数据库DDL脚本
-- 版本: v1.0
-- 日期: 20xx-xx-xx
-- ========================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS qooerp_scm
  ENCODING 'UTF8'
  LC_COLLATE='zh_CN.UTF-8'
  LC_CTYPE='zh_CN.UTF-8'
  TEMPLATE=template0;

-- ========================================
-- 供应商相关表
-- ========================================

-- 供应商表
CREATE TABLE IF NOT EXISTS scm_supplier (
    id BIGSERIAL PRIMARY KEY,
    supplier_code VARCHAR(50) NOT NULL UNIQUE COMMENT '供应商编码',
    supplier_name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    supplier_type VARCHAR(50) COMMENT '供应商类型（制造商/分销商/零售商）',
    contact_person VARCHAR(100) COMMENT '联系人',
    contact_phone VARCHAR(50) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    address VARCHAR(500) COMMENT '地址',
    city VARCHAR(100) COMMENT '城市',
    province VARCHAR(100) COMMENT '省份',
    country VARCHAR(100) COMMENT '国家',
    postal_code VARCHAR(20) COMMENT '邮政编码',
    tax_number VARCHAR(50) COMMENT '税号',
    bank_name VARCHAR(100) COMMENT '银行名称',
    bank_account VARCHAR(50) COMMENT '银行账号',
    credit_rating VARCHAR(20) COMMENT '信用等级',
    payment_terms INTEGER COMMENT '付款期限（天）',
    status VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态（启用/禁用）',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- 供应商分类表
CREATE TABLE IF NOT EXISTS scm_supplier_category (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    category_name VARCHAR(200) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INTEGER DEFAULT 1 COMMENT '分类层级',
    sort_order INTEGER DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态（启用/禁用）',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- 供应商评估表
CREATE TABLE IF NOT EXISTS scm_supplier_evaluation (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    evaluation_type VARCHAR(50) COMMENT '评估类型（季度/年度/专项）',
    quality_score DECIMAL(5,2) COMMENT '质量得分（0-100）',
    delivery_score DECIMAL(5,2) COMMENT '交期得分（0-100）',
    price_score DECIMAL(5,2) COMMENT '价格得分（0-100）',
    service_score DECIMAL(5,2) COMMENT '服务得分（0-100）',
    total_score DECIMAL(5,2) COMMENT '综合得分',
    evaluation_level VARCHAR(20) COMMENT '评估等级（优秀/良好/合格/不合格）',
    evaluation_date DATE COMMENT '评估日期',
    evaluator VARCHAR(100) COMMENT '评估人',
    evaluation_content TEXT COMMENT '评估内容',
    improvement_plan TEXT COMMENT '改进计划',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- ========================================
-- 客户相关表
-- ========================================

-- 客户表
CREATE TABLE IF NOT EXISTS scm_customer (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编码',
    customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
    customer_type VARCHAR(50) COMMENT '客户类型（个人/企业）',
    contact_person VARCHAR(100) COMMENT '联系人',
    contact_phone VARCHAR(50) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    address VARCHAR(500) COMMENT '地址',
    city VARCHAR(100) COMMENT '城市',
    province VARCHAR(100) COMMENT '省份',
    country VARCHAR(100) COMMENT '国家',
    postal_code VARCHAR(20) COMMENT '邮政编码',
    tax_number VARCHAR(50) COMMENT '税号',
    credit_rating VARCHAR(20) COMMENT '信用等级',
    credit_limit DECIMAL(18,2) DEFAULT 0 COMMENT '信用额度',
    payment_terms INTEGER COMMENT '付款期限（天）',
    status VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态（启用/禁用）',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- 客户分类表
CREATE TABLE IF NOT EXISTS scm_customer_category (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    category_name VARCHAR(200) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    level INTEGER DEFAULT 1 COMMENT '分类层级',
    sort_order INTEGER DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态（启用/禁用）',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- 客户等级表
CREATE TABLE IF NOT EXISTS scm_customer_level (
    id BIGSERIAL PRIMARY KEY,
    level_code VARCHAR(50) NOT NULL UNIQUE COMMENT '等级编码',
    level_name VARCHAR(100) NOT NULL COMMENT '等级名称',
    min_amount DECIMAL(18,2) DEFAULT 0 COMMENT '最低交易金额',
    max_amount DECIMAL(18,2) COMMENT '最高交易金额',
    min_orders INTEGER DEFAULT 0 COMMENT '最低订单数',
    discount_rate DECIMAL(5,4) DEFAULT 1.0000 COMMENT '折扣率',
    priority INTEGER DEFAULT 0 COMMENT '优先级',
    status VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- ========================================
-- 物流相关表
-- ========================================

-- 物流跟踪表
CREATE TABLE IF NOT EXISTS scm_logistics_tracking (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(100) NOT NULL UNIQUE COMMENT '运单号',
    carrier VARCHAR(100) COMMENT '承运商',
    carrier_code VARCHAR(50) COMMENT '承运商编码',
    from_address VARCHAR(500) COMMENT '发货地址',
    to_address VARCHAR(500) COMMENT '收货地址',
    sender VARCHAR(100) COMMENT '发货人',
    receiver VARCHAR(100) COMMENT '收货人',
    receiver_phone VARCHAR(50) COMMENT '收货人电话',
    shipment_time TIMESTAMP COMMENT '发货时间',
    estimated_delivery TIMESTAMP COMMENT '预计送达时间',
    actual_delivery TIMESTAMP COMMENT '实际送达时间',
    status VARCHAR(50) COMMENT '状态（待发货/运输中/已签收/异常）',
    current_location VARCHAR(500) COMMENT '当前位置',
    weight DECIMAL(10,2) COMMENT '重量（kg）',
    volume DECIMAL(10,2) COMMENT '体积（m³）',
    packages INTEGER DEFAULT 1 COMMENT '包裹数量',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- 物流费用表
CREATE TABLE IF NOT EXISTS scm_logistics_cost (
    id BIGSERIAL PRIMARY KEY,
    tracking_id BIGINT COMMENT '物流跟踪ID',
    order_id BIGINT COMMENT '订单ID',
    cost_type VARCHAR(50) COMMENT '费用类型（运费/包装费/保险费/其他）',
    amount DECIMAL(18,2) NOT NULL COMMENT '金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    paid_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '付款状态（未付/已付/部分）',
    paid_amount DECIMAL(18,2) DEFAULT 0 COMMENT '已付金额',
    paid_date DATE COMMENT '付款日期',
    invoice_number VARCHAR(100) COMMENT '发票号码',
    remark TEXT COMMENT '备注',
    tenant_id BIGINT COMMENT '租户ID',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(50) COMMENT '更新人',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INTEGER DEFAULT 0 COMMENT '删除标记'
);

-- ========================================
-- 索引创建
-- ========================================

-- 供应商表索引
CREATE INDEX IF NOT EXISTS idx_supplier_code ON scm_supplier(supplier_code);
CREATE INDEX IF NOT EXISTS idx_supplier_name ON scm_supplier(supplier_name);
CREATE INDEX IF NOT EXISTS idx_supplier_type ON scm_supplier(supplier_type);
CREATE INDEX IF NOT EXISTS idx_supplier_tenant ON scm_supplier(tenant_id);
CREATE INDEX IF NOT EXISTS idx_supplier_status ON scm_supplier(status);

-- 供应商分类表索引
CREATE INDEX IF NOT EXISTS idx_category_parent ON scm_supplier_category(parent_id);
CREATE INDEX IF NOT EXISTS idx_category_code ON scm_supplier_category(category_code);

-- 供应商评估表索引
CREATE INDEX IF NOT EXISTS idx_evaluation_supplier ON scm_supplier_evaluation(supplier_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_date ON scm_supplier_evaluation(evaluation_date);

-- 客户表索引
CREATE INDEX IF NOT EXISTS idx_customer_code ON scm_customer(customer_code);
CREATE INDEX IF NOT EXISTS idx_customer_name ON scm_customer(customer_name);
CREATE INDEX IF NOT EXISTS idx_customer_type ON scm_customer(customer_type);
CREATE INDEX IF NOT EXISTS idx_customer_tenant ON scm_customer(tenant_id);
CREATE INDEX IF NOT EXISTS idx_customer_status ON scm_customer(status);

-- 客户分类表索引
CREATE INDEX IF NOT EXISTS idx_customer_category_parent ON scm_customer_category(parent_id);
CREATE INDEX IF NOT EXISTS idx_customer_category_code ON scm_customer_category(category_code);

-- 客户等级表索引
CREATE INDEX IF NOT EXISTS idx_customer_level_code ON scm_customer_level(level_code);

-- 物流跟踪表索引
CREATE INDEX IF NOT EXISTS idx_tracking_number ON scm_logistics_tracking(tracking_number);
CREATE INDEX IF NOT EXISTS idx_tracking_order ON scm_logistics_tracking(order_id);
CREATE INDEX IF NOT EXISTS idx_tracking_status ON scm_logistics_tracking(status);

-- 物流费用表索引
CREATE INDEX IF NOT EXISTS idx_cost_tracking ON scm_logistics_cost(tracking_id);
CREATE INDEX IF NOT EXISTS idx_cost_order ON scm_logistics_cost(order_id);

-- ========================================
-- 初始化数据
-- ========================================

-- 插入默认客户等级
INSERT INTO scm_customer_level (level_code, level_name, min_amount, max_amount, min_orders, discount_rate, priority, remark)
VALUES
('LEVEL_1', '普通会员', 0, 10000, 0, 1.0000, 1, '普通会员'),
('LEVEL_2', '银卡会员', 10000, 50000, 10, 0.9500, 2, '银卡会员，享受95折优惠'),
('LEVEL_3', '金卡会员', 50000, 200000, 50, 0.9000, 3, '金卡会员，享受90折优惠'),
('LEVEL_4', '白金会员', 200000, 500000, 100, 0.8500, 4, '白金会员，享受85折优惠'),
('LEVEL_5', '钻石会员', 500000, NULL, 200, 0.8000, 5, '钻石会员，享受80折优惠')
ON CONFLICT (level_code) DO NOTHING;

-- ========================================
-- 创建更新时间触发器
-- ========================================

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为各表创建更新时间触发器
CREATE TRIGGER trg_supplier_update_time
BEFORE UPDATE ON scm_supplier
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_supplier_category_update_time
BEFORE UPDATE ON scm_supplier_category
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_supplier_evaluation_update_time
BEFORE UPDATE ON scm_supplier_evaluation
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_customer_update_time
BEFORE UPDATE ON scm_customer
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_customer_category_update_time
BEFORE UPDATE ON scm_customer_category
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_customer_level_update_time
BEFORE UPDATE ON scm_customer_level
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_logistics_tracking_update_time
BEFORE UPDATE ON scm_logistics_tracking
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_logistics_cost_update_time
BEFORE UPDATE ON scm_logistics_cost
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

COMMENT ON DATABASE qooerp_scm IS 'QooERP供应链管理模块数据库';
