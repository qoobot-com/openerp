-- QooERP SCM 模块数据库初始化脚本
-- 版本：v1.0
-- 创建日期：2026-02-18
-- 数据库：PostgreSQL 15+

-- ==================== 供应商表 ====================
CREATE TABLE scm_supplier (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    supplier_code VARCHAR(32) NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    supplier_type VARCHAR(20) NOT NULL,
    industry VARCHAR(64),
    scale VARCHAR(20),
    contact_person VARCHAR(64),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(128),
    address VARCHAR(256),
    tax_number VARCHAR(64),
    bank_name VARCHAR(64),
    bank_account VARCHAR(64),
    credit_level VARCHAR(10) DEFAULT 'C',
    credit_limit NUMERIC(18,2) DEFAULT 0.00,
    payment_method VARCHAR(20) DEFAULT 'MONTHLY',
    payment_days INT DEFAULT 30,
    evaluation_score INT,
    quality_score INT,
    delivery_score INT,
    service_score INT,
    price_score INT,
    last_evaluation_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_supplier_code UNIQUE (tenant_id, supplier_code)
);

COMMENT ON TABLE scm_supplier IS '供应商表';
COMMENT ON COLUMN scm_supplier.id IS '主键ID';
COMMENT ON COLUMN scm_supplier.tenant_id IS '租户ID';
COMMENT ON COLUMN scm_supplier.supplier_code IS '供应商编码';
COMMENT ON COLUMN scm_supplier.supplier_name IS '供应商名称';
COMMENT ON COLUMN scm_supplier.supplier_type IS '供应商类型：MANUFACTURER-制造商,DISTRIBUTOR-分销商,RETAILER-零售商';
COMMENT ON COLUMN scm_supplier.industry IS '所属行业';
COMMENT ON COLUMN scm_supplier.scale IS '企业规模：LARGE-大型,MEDIUM-中型,SMALL-小型';
COMMENT ON COLUMN scm_supplier.contact_person IS '联系人';
COMMENT ON COLUMN scm_supplier.contact_phone IS '联系电话';
COMMENT ON COLUMN scm_supplier.contact_email IS '联系邮箱';
COMMENT ON COLUMN scm_supplier.address IS '联系地址';
COMMENT ON COLUMN scm_supplier.tax_number IS '税号';
COMMENT ON COLUMN scm_supplier.bank_name IS '开户银行';
COMMENT ON COLUMN scm_supplier.bank_account IS '银行账号';
COMMENT ON COLUMN scm_supplier.credit_level IS '信用等级：A,B,C,D';
COMMENT ON COLUMN scm_supplier.credit_limit IS '信用额度';
COMMENT ON COLUMN scm_supplier.payment_method IS '结算方式：CASH-现金,CREDIT-信用,MONTHLY-月结,QUARTERLY-季结';
COMMENT ON COLUMN scm_supplier.payment_days IS '付款期限（天）';
COMMENT ON COLUMN scm_supplier.evaluation_score IS '评估总分';
COMMENT ON COLUMN scm_supplier.quality_score IS '质量评分';
COMMENT ON COLUMN scm_supplier.delivery_score IS '交付评分';
COMMENT ON COLUMN scm_supplier.service_score IS '服务评分';
COMMENT ON COLUMN scm_supplier.price_score IS '价格评分';
COMMENT ON COLUMN scm_supplier.last_evaluation_time IS '最后评估时间';
COMMENT ON COLUMN scm_supplier.status IS '状态：ACTIVE-启用,INACTIVE-停用,DISABLED-禁用';
COMMENT ON COLUMN scm_supplier.remark IS '备注';
COMMENT ON COLUMN scm_supplier.create_time IS '创建时间';
COMMENT ON COLUMN scm_supplier.update_time IS '更新时间';
COMMENT ON COLUMN scm_supplier.deleted IS '删除标记：0-未删除,1-已删除';
COMMENT ON COLUMN scm_supplier.version IS '版本号';

CREATE INDEX idx_supplier_status ON scm_supplier(status);
CREATE INDEX idx_supplier_deleted ON scm_supplier(deleted);
CREATE INDEX idx_supplier_tenant_id ON scm_supplier(tenant_id);

-- ==================== 供应商评估表 ====================
CREATE TABLE scm_supplier_evaluation (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    supplier_id BIGINT NOT NULL,
    supplier_code VARCHAR(32) NOT NULL,
    evaluation_date DATE NOT NULL,
    quality_score INT,
    delivery_score INT,
    service_score INT,
    price_score INT,
    total_score INT,
    credit_level VARCHAR(10),
    evaluator VARCHAR(64),
    evaluation_remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_supplier_evaluation IS '供应商评估表';
COMMENT ON COLUMN scm_supplier_evaluation.supplier_id IS '供应商ID';
COMMENT ON COLUMN scm_supplier_evaluation.supplier_code IS '供应商编码';
COMMENT ON COLUMN scm_supplier_evaluation.evaluation_date IS '评估日期';
COMMENT ON COLUMN scm_supplier_evaluation.quality_score IS '质量评分（0-100）';
COMMENT ON COLUMN scm_supplier_evaluation.delivery_score IS '交付评分（0-100）';
COMMENT ON COLUMN scm_supplier_evaluation.service_score IS '服务评分（0-100）';
COMMENT ON COLUMN scm_supplier_evaluation.price_score IS '价格评分（0-100）';
COMMENT ON COLUMN scm_supplier_evaluation.total_score IS '综合评分（0-100）';
COMMENT ON COLUMN scm_supplier_evaluation.credit_level IS '信用等级：A,B,C,D';
COMMENT ON COLUMN scm_supplier_evaluation.evaluator IS '评估人';
COMMENT ON COLUMN scm_supplier_evaluation.evaluation_remark IS '评估意见';

CREATE INDEX idx_supplier_evaluation_supplier_id ON scm_supplier_evaluation(supplier_id);
CREATE INDEX idx_supplier_evaluation_evaluation_date ON scm_supplier_evaluation(evaluation_date);
CREATE INDEX idx_supplier_evaluation_deleted ON scm_supplier_evaluation(deleted);
CREATE INDEX idx_supplier_evaluation_tenant_id ON scm_supplier_evaluation(tenant_id);

-- ==================== 客户表 ====================
CREATE TABLE scm_customer (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    customer_code VARCHAR(32) NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    customer_type VARCHAR(20) NOT NULL,
    industry VARCHAR(64),
    scale VARCHAR(20),
    customer_level VARCHAR(20) DEFAULT 'NORMAL',
    contact_person VARCHAR(64),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(128),
    address VARCHAR(256),
    tax_number VARCHAR(64),
    bank_name VARCHAR(64),
    bank_account VARCHAR(64),
    credit_limit NUMERIC(18,2) DEFAULT 0.00,
    payment_method VARCHAR(20) DEFAULT 'MONTHLY',
    payment_days INT DEFAULT 30,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_customer_code UNIQUE (tenant_id, customer_code)
);

COMMENT ON TABLE scm_customer IS '客户表';
COMMENT ON COLUMN scm_customer.id IS '主键ID';
COMMENT ON COLUMN scm_customer.tenant_id IS '租户ID';
COMMENT ON COLUMN scm_customer.customer_code IS '客户编码';
COMMENT ON COLUMN scm_customer.customer_name IS '客户名称';
COMMENT ON COLUMN scm_customer.customer_type IS '客户类型：ENTERPRISE-企业,INDIVIDUAL-个人,GOVERNMENT-政府';
COMMENT ON COLUMN scm_customer.industry IS '所属行业';
COMMENT ON COLUMN scm_customer.scale IS '企业规模';
COMMENT ON COLUMN scm_customer.customer_level IS '客户等级：VIP-VIP客户,IMPORTANT-重要客户,NORMAL-普通客户,POTENTIAL-潜在客户';
COMMENT ON COLUMN scm_customer.contact_person IS '联系人';
COMMENT ON COLUMN scm_customer.contact_phone IS '联系电话';
COMMENT ON COLUMN scm_customer.contact_email IS '联系邮箱';
COMMENT ON COLUMN scm_customer.address IS '联系地址';
COMMENT ON COLUMN scm_customer.tax_number IS '税号';
COMMENT ON COLUMN scm_customer.bank_name IS '开户银行';
COMMENT ON COLUMN scm_customer.bank_account IS '银行账号';
COMMENT ON COLUMN scm_customer.credit_limit IS '信用额度';
COMMENT ON COLUMN scm_customer.payment_method IS '结算方式';
COMMENT ON COLUMN scm_customer.payment_days IS '付款期限（天）';
COMMENT ON COLUMN scm_customer.status IS '状态：ACTIVE-启用,INACTIVE-停用,DISABLED-禁用';
COMMENT ON COLUMN scm_customer.remark IS '备注';
COMMENT ON COLUMN scm_customer.create_time IS '创建时间';
COMMENT ON COLUMN scm_customer.update_time IS '更新时间';
COMMENT ON COLUMN scm_customer.deleted IS '删除标记：0-未删除,1-已删除';
COMMENT ON COLUMN scm_customer.version IS '版本号';

CREATE INDEX idx_customer_status ON scm_customer(status);
CREATE INDEX idx_customer_deleted ON scm_customer(deleted);
CREATE INDEX idx_customer_tenant_id ON scm_customer(tenant_id);

-- ==================== 客户分级表 ====================
CREATE TABLE scm_customer_grade (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    customer_id BIGINT NOT NULL,
    customer_code VARCHAR(32) NOT NULL,
    grade_date DATE NOT NULL,
    annual_purchase_amount NUMERIC(18,2),
    purchase_count INT,
    average_order_amount NUMERIC(18,2),
    return_rate NUMERIC(5,2),
    customer_level VARCHAR(20),
    change_reason VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_customer_grade IS '客户分级表';
COMMENT ON COLUMN scm_customer_grade.customer_id IS '客户ID';
COMMENT ON COLUMN scm_customer_grade.customer_code IS '客户编码';
COMMENT ON COLUMN scm_customer_grade.grade_date IS '分级日期';
COMMENT ON COLUMN scm_customer_grade.annual_purchase_amount IS '年采购金额';
COMMENT ON COLUMN scm_customer_grade.purchase_count IS '采购频次';
COMMENT ON COLUMN scm_customer_grade.average_order_amount IS '平均客单价';
COMMENT ON COLUMN scm_customer_grade.return_rate IS '退货率（%）';
COMMENT ON COLUMN scm_customer_grade.customer_level IS '客户等级';
COMMENT ON COLUMN scm_customer_grade.change_reason IS '等级变更原因';

CREATE INDEX idx_customer_grade_customer_id ON scm_customer_grade(customer_id);
CREATE INDEX idx_customer_grade_grade_date ON scm_customer_grade(grade_date);
CREATE INDEX idx_customer_grade_deleted ON scm_customer_grade(deleted);
CREATE INDEX idx_customer_grade_tenant_id ON scm_customer_grade(tenant_id);

-- ==================== 物流表 ====================
CREATE TABLE scm_logistics (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    logistics_code VARCHAR(32) NOT NULL,
    business_type VARCHAR(20) NOT NULL,
    related_order_id BIGINT,
    related_order_code VARCHAR(64),
    logistics_company VARCHAR(128),
    tracking_number VARCHAR(64),
    sender VARCHAR(64),
    sender_phone VARCHAR(20),
    sender_address VARCHAR(256),
    receiver VARCHAR(64),
    receiver_phone VARCHAR(20),
    receiver_address VARCHAR(256),
    shipment_date DATE,
    estimated_arrival_date DATE,
    actual_arrival_date DATE,
    logistics_cost NUMERIC(18,2) DEFAULT 0.00,
    logistics_status VARCHAR(20) DEFAULT 'PENDING',
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_logistics_code UNIQUE (tenant_id, logistics_code)
);

COMMENT ON TABLE scm_logistics IS '物流表';
COMMENT ON COLUMN scm_logistics.id IS '主键ID';
COMMENT ON COLUMN scm_logistics.tenant_id IS '租户ID';
COMMENT ON COLUMN scm_logistics.logistics_code IS '物流单号';
COMMENT ON COLUMN scm_logistics.business_type IS '业务类型：PURCHASE-采购,SALES-销售';
COMMENT ON COLUMN scm_logistics.related_order_id IS '关联单据ID';
COMMENT ON COLUMN scm_logistics.related_order_code IS '关联单据号';
COMMENT ON COLUMN scm_logistics.logistics_company IS '物流公司';
COMMENT ON COLUMN scm_logistics.tracking_number IS '快递单号';
COMMENT ON COLUMN scm_logistics.sender IS '发货人';
COMMENT ON COLUMN scm_logistics.sender_phone IS '发货人电话';
COMMENT ON COLUMN scm_logistics.sender_address IS '发货地址';
COMMENT ON COLUMN scm_logistics.receiver IS '收货人';
COMMENT ON COLUMN scm_logistics.receiver_phone IS '收货人电话';
COMMENT ON COLUMN scm_logistics.receiver_address IS '收货地址';
COMMENT ON COLUMN scm_logistics.shipment_date IS '发货日期';
COMMENT ON COLUMN scm_logistics.estimated_arrival_date IS '预计到达日期';
COMMENT ON COLUMN scm_logistics.actual_arrival_date IS '实际到达日期';
COMMENT ON COLUMN scm_logistics.logistics_cost IS '物流费用';
COMMENT ON COLUMN scm_logistics.logistics_status IS '物流状态：PENDING-待发货,SHIPPED-已发货,IN_TRANSIT-运输中,DELIVERED-已送达,CANCELLED-已取消';
COMMENT ON COLUMN scm_logistics.remark IS '备注';
COMMENT ON COLUMN scm_logistics.create_time IS '创建时间';
COMMENT ON COLUMN scm_logistics.update_time IS '更新时间';
COMMENT ON COLUMN scm_logistics.deleted IS '删除标记：0-未删除,1-已删除';
COMMENT ON COLUMN scm_logistics.version IS '版本号';

CREATE INDEX idx_logistics_related_order ON scm_logistics(related_order_id);
CREATE INDEX idx_logistics_status ON scm_logistics(logistics_status);
CREATE INDEX idx_logistics_deleted ON scm_logistics(deleted);
CREATE INDEX idx_logistics_tenant_id ON scm_logistics(tenant_id);

-- ==================== 物流跟踪表 ====================
CREATE TABLE scm_logistics_tracking (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    logistics_id BIGINT NOT NULL,
    logistics_code VARCHAR(32) NOT NULL,
    tracking_time TIMESTAMP NOT NULL,
    location VARCHAR(256),
    status VARCHAR(20) NOT NULL,
    description VARCHAR(512),
    operator VARCHAR(64),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_logistics_tracking IS '物流跟踪表';
COMMENT ON COLUMN scm_logistics_tracking.logistics_id IS '物流ID';
COMMENT ON COLUMN scm_logistics_tracking.logistics_code IS '物流单号';
COMMENT ON COLUMN scm_logistics_tracking.tracking_time IS '跟踪时间';
COMMENT ON COLUMN scm_logistics_tracking.location IS '位置';
COMMENT ON COLUMN scm_logistics_tracking.status IS '状态';
COMMENT ON COLUMN scm_logistics_tracking.description IS '描述';
COMMENT ON COLUMN scm_logistics_tracking.operator IS '操作人';
COMMENT ON COLUMN scm_logistics_tracking.create_time IS '创建时间';
COMMENT ON COLUMN scm_logistics_tracking.update_time IS '更新时间';
COMMENT ON COLUMN scm_logistics_tracking.deleted IS '删除标记：0-未删除,1-已删除';
COMMENT ON COLUMN scm_logistics_tracking.version IS '版本号';

CREATE INDEX idx_logistics_tracking_logistics_id ON scm_logistics_tracking(logistics_id);
CREATE INDEX idx_logistics_tracking_tracking_time ON scm_logistics_tracking(tracking_time);
CREATE INDEX idx_logistics_tracking_deleted ON scm_logistics_tracking(deleted);
CREATE INDEX idx_logistics_tracking_tenant_id ON scm_logistics_tracking(tenant_id);

-- ==================== 供应商报价表 ====================
CREATE TABLE scm_supplier_quotation (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    quotation_code VARCHAR(32) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_code VARCHAR(32) NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    quotation_date DATE NOT NULL,
    valid_until DATE,
    quotation_type VARCHAR(20) DEFAULT 'NORMAL',
    total_amount NUMERIC(18,2) DEFAULT 0.00,
    audit_status VARCHAR(20) DEFAULT 'PENDING',
    auditor VARCHAR(64),
    audit_time TIMESTAMP,
    audit_remark VARCHAR(512),
    converted INT DEFAULT 0,
    purchase_order_id BIGINT,
    purchase_order_code VARCHAR(64),
    convert_time TIMESTAMP,
    attachment_url VARCHAR(512),
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_quotation_code UNIQUE (tenant_id, quotation_code)
);

COMMENT ON TABLE scm_supplier_quotation IS '供应商报价表';
COMMENT ON COLUMN scm_supplier_quotation.quotation_code IS '报价单号';
COMMENT ON COLUMN scm_supplier_quotation.supplier_id IS '供应商ID';
COMMENT ON COLUMN scm_supplier_quotation.quotation_date IS '报价日期';
COMMENT ON COLUMN scm_supplier_quotation.valid_until IS '报价有效期';
COMMENT ON COLUMN scm_supplier_quotation.quotation_type IS '报价类型：NORMAL-正常报价,URGENT-紧急报价';
COMMENT ON COLUMN scm_supplier_quotation.total_amount IS '报价总金额';
COMMENT ON COLUMN scm_supplier_quotation.audit_status IS '审核状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝';
COMMENT ON COLUMN scm_supplier_quotation.auditor IS '审核人';
COMMENT ON COLUMN scm_supplier_quotation.audit_time IS '审核时间';
COMMENT ON COLUMN scm_supplier_quotation.audit_remark IS '审核意见';
COMMENT ON COLUMN scm_supplier_quotation.converted IS '是否转采购订单：0-未转换,1-已转换';
COMMENT ON COLUMN scm_supplier_quotation.purchase_order_id IS '采购订单ID';
COMMENT ON COLUMN scm_supplier_quotation.purchase_order_code IS '采购订单号';
COMMENT ON COLUMN scm_supplier_quotation.convert_time IS '转换时间';
COMMENT ON COLUMN scm_supplier_quotation.attachment_url IS '附件地址';

CREATE INDEX idx_supplier_quotation_supplier_id ON scm_supplier_quotation(supplier_id);
CREATE INDEX idx_supplier_quotation_audit_status ON scm_supplier_quotation(audit_status);
CREATE INDEX idx_supplier_quotation_deleted ON scm_supplier_quotation(deleted);
CREATE INDEX idx_supplier_quotation_tenant_id ON scm_supplier_quotation(tenant_id);

-- ==================== 供应商报价明细表 ====================
CREATE TABLE scm_supplier_quotation_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    quotation_id BIGINT NOT NULL,
    quotation_code VARCHAR(32) NOT NULL,
    material_code VARCHAR(64) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    specification VARCHAR(128),
    unit VARCHAR(32),
    quantity NUMERIC(18,2) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    delivery_days INT,
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_supplier_quotation_detail IS '供应商报价明细表';
COMMENT ON COLUMN scm_supplier_quotation_detail.quotation_id IS '报价单ID';
COMMENT ON COLUMN scm_supplier_quotation_detail.quotation_code IS '报价单号';
COMMENT ON COLUMN scm_supplier_quotation_detail.material_code IS '物料编码';
COMMENT ON COLUMN scm_supplier_quotation_detail.material_name IS '物料名称';
COMMENT ON COLUMN scm_supplier_quotation_detail.specification IS '规格型号';
COMMENT ON COLUMN scm_supplier_quotation_detail.unit IS '单位';
COMMENT ON COLUMN scm_supplier_quotation_detail.quantity IS '数量';
COMMENT ON COLUMN scm_supplier_quotation_detail.unit_price IS '单价';
COMMENT ON COLUMN scm_supplier_quotation_detail.amount IS '金额';
COMMENT ON COLUMN scm_supplier_quotation_detail.delivery_days IS '交货期（天）';

CREATE INDEX idx_supplier_quotation_detail_quotation_id ON scm_supplier_quotation_detail(quotation_id);
CREATE INDEX idx_supplier_quotation_detail_material_code ON scm_supplier_quotation_detail(material_code);
CREATE INDEX idx_supplier_quotation_detail_deleted ON scm_supplier_quotation_detail(deleted);
CREATE INDEX idx_supplier_quotation_detail_tenant_id ON scm_supplier_quotation_detail(tenant_id);

-- ==================== 供应商对账单表 ====================
CREATE TABLE scm_supplier_statement (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    statement_code VARCHAR(32) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_code VARCHAR(32) NOT NULL,
    supplier_name VARCHAR(128) NOT NULL,
    statement_type VARCHAR(20) DEFAULT 'MONTHLY',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    statement_date DATE NOT NULL,
    purchase_amount NUMERIC(18,2) DEFAULT 0.00,
    return_amount NUMERIC(18,2) DEFAULT 0.00,
    freight_amount NUMERIC(18,2) DEFAULT 0.00,
    other_amount NUMERIC(18,2) DEFAULT 0.00,
    total_amount NUMERIC(18,2) DEFAULT 0.00,
    audit_status VARCHAR(20) DEFAULT 'PENDING',
    auditor VARCHAR(64),
    audit_time TIMESTAMP,
    audit_remark VARCHAR(512),
    settlement_status VARCHAR(20) DEFAULT 'PENDING',
    settlement_date DATE,
    settlement_method VARCHAR(20),
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_statement_code UNIQUE (tenant_id, statement_code)
);

COMMENT ON TABLE scm_supplier_statement IS '供应商对账单表';
COMMENT ON COLUMN scm_supplier_statement.statement_code IS '对账单号';
COMMENT ON COLUMN scm_supplier_statement.supplier_id IS '供应商ID';
COMMENT ON COLUMN scm_supplier_statement.statement_type IS '对账类型：MONTHLY-月度,QUARTERLY-季度,YEARLY-年度';
COMMENT ON COLUMN scm_supplier_statement.start_date IS '对账起始日期';
COMMENT ON COLUMN scm_supplier_statement.end_date IS '对账结束日期';
COMMENT ON COLUMN scm_supplier_statement.statement_date IS '对账日期';
COMMENT ON COLUMN scm_supplier_statement.purchase_amount IS '采购总金额';
COMMENT ON COLUMN scm_supplier_statement.return_amount IS '退货总金额';
COMMENT ON COLUMN scm_supplier_statement.freight_amount IS '运费';
COMMENT ON COLUMN scm_supplier_statement.other_amount IS '其他费用';
COMMENT ON COLUMN scm_supplier_statement.total_amount IS '应付总额';
COMMENT ON COLUMN scm_supplier_statement.audit_status IS '审核状态：PENDING-待审核,APPROVED-已通过,REJECTED-已拒绝';
COMMENT ON COLUMN scm_supplier_statement.auditor IS '审核人';
COMMENT ON COLUMN scm_supplier_statement.audit_time IS '审核时间';
COMMENT ON COLUMN scm_supplier_statement.audit_remark IS '审核意见';
COMMENT ON COLUMN scm_supplier_statement.settlement_status IS '结算状态：PENDING-待结算,SETTLED-已结算';
COMMENT ON COLUMN scm_supplier_statement.settlement_date IS '结算日期';
COMMENT ON COLUMN scm_supplier_statement.settlement_method IS '结算方式';

CREATE INDEX idx_supplier_statement_supplier_id ON scm_supplier_statement(supplier_id);
CREATE INDEX idx_supplier_statement_statement_period ON scm_supplier_statement(start_date, end_date);
CREATE INDEX idx_supplier_statement_audit_status ON scm_supplier_statement(audit_status);
CREATE INDEX idx_supplier_statement_deleted ON scm_supplier_statement(deleted);
CREATE INDEX idx_supplier_statement_tenant_id ON scm_supplier_statement(tenant_id);

-- ==================== 供应商对账明细表 ====================
CREATE TABLE scm_supplier_statement_detail (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    statement_id BIGINT NOT NULL,
    statement_code VARCHAR(32) NOT NULL,
    order_type VARCHAR(20) NOT NULL,
    order_id BIGINT NOT NULL,
    order_code VARCHAR(64) NOT NULL,
    material_code VARCHAR(64) NOT NULL,
    material_name VARCHAR(128) NOT NULL,
    quantity NUMERIC(18,2) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    order_date DATE NOT NULL,
    remark VARCHAR(512),
    created_by BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE scm_supplier_statement_detail IS '供应商对账明细表';
COMMENT ON COLUMN scm_supplier_statement_detail.statement_id IS '对账单ID';
COMMENT ON COLUMN scm_supplier_statement_detail.statement_code IS '对账单号';
COMMENT ON COLUMN scm_supplier_statement_detail.order_type IS '单据类型：PURCHASE_ORDER-采购订单,RECEIPT-入库单,RETURN-退货单';
COMMENT ON COLUMN scm_supplier_statement_detail.order_id IS '单据ID';
COMMENT ON COLUMN scm_supplier_statement_detail.order_code IS '单据号';
COMMENT ON COLUMN scm_supplier_statement_detail.material_code IS '物料编码';
COMMENT ON COLUMN scm_supplier_statement_detail.material_name IS '物料名称';
COMMENT ON COLUMN scm_supplier_statement_detail.quantity IS '数量';
COMMENT ON COLUMN scm_supplier_statement_detail.unit_price IS '单价';
COMMENT ON COLUMN scm_supplier_statement_detail.amount IS '金额';
COMMENT ON COLUMN scm_supplier_statement_detail.order_date IS '单据日期';

CREATE INDEX idx_supplier_statement_detail_statement_id ON scm_supplier_statement_detail(statement_id);
CREATE INDEX idx_supplier_statement_detail_order_id ON scm_supplier_statement_detail(order_id);
CREATE INDEX idx_supplier_statement_detail_material_code ON scm_supplier_statement_detail(material_code);
CREATE INDEX idx_supplier_statement_detail_deleted ON scm_supplier_statement_detail(deleted);
CREATE INDEX idx_supplier_statement_detail_tenant_id ON scm_supplier_statement_detail(tenant_id);
