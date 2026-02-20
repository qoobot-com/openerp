-- ====================================
-- 低代码开发平台数据库初始化脚本
-- ====================================

-- ====================================
-- 表单管理
-- ====================================
CREATE TABLE IF NOT EXISTS lowcode_form (
    id BIGSERIAL PRIMARY KEY,
    form_name VARCHAR(200) NOT NULL COMMENT '表单名称',
    form_code VARCHAR(100) NOT NULL UNIQUE COMMENT '表单编码',
    form_data JSONB NOT NULL COMMENT '表单配置JSON',
    version INTEGER DEFAULT 1 COMMENT '版本号',
    status SMALLINT DEFAULT 1 COMMENT '状态: 1-草稿 2-发布 3-下架',
    description TEXT COMMENT '表单描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    biz_date INTEGER
);

-- 表单数据表
CREATE TABLE IF NOT EXISTS lowcode_form_data (
    id BIGSERIAL PRIMARY KEY,
    form_id BIGINT NOT NULL COMMENT '表单ID',
    form_code VARCHAR(100) NOT NULL COMMENT '表单编码',
    form_data JSONB NOT NULL COMMENT '表单数据JSON',
    submit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    submitter_id BIGINT COMMENT '提交人ID',
    submitter_name VARCHAR(100) COMMENT '提交人姓名',
    status SMALLINT DEFAULT 1 COMMENT '状态: 1-待审核 2-已审核 3-已驳回',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    biz_date INTEGER
);

-- ====================================
-- 页面管理
-- ====================================
CREATE TABLE IF NOT EXISTS lowcode_page (
    id BIGSERIAL PRIMARY KEY,
    page_name VARCHAR(200) NOT NULL COMMENT '页面名称',
    page_code VARCHAR(100) NOT NULL UNIQUE COMMENT '页面编码',
    page_type VARCHAR(50) NOT NULL COMMENT '页面类型: list-form-detail-chart等',
    page_config JSONB NOT NULL COMMENT '页面配置JSON',
    route_path VARCHAR(200) COMMENT '路由路径',
    version INTEGER DEFAULT 1 COMMENT '版本号',
    status SMALLINT DEFAULT 1 COMMENT '状态: 1-草稿 2-发布 3-下架',
    description TEXT COMMENT '页面描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    biz_date INTEGER
);

-- ====================================
-- 数据源管理
-- ====================================
CREATE TABLE IF NOT EXISTS lowcode_datasource (
    id BIGSERIAL PRIMARY KEY,
    ds_name VARCHAR(100) NOT NULL COMMENT '数据源名称',
    ds_code VARCHAR(100) NOT NULL UNIQUE COMMENT '数据源编码',
    ds_type VARCHAR(50) NOT NULL COMMENT '数据源类型: mysql-postgresql-oracle-sqlserver-api',
    ds_config JSONB NOT NULL COMMENT '数据源配置JSON',
    status SMALLINT DEFAULT 1 COMMENT '状态: 1-启用 2-禁用',
    description TEXT COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    biz_date INTEGER
);

-- ====================================
-- 代码生成记录
-- ====================================
CREATE TABLE IF NOT EXISTS lowcode_code_gen (
    id BIGSERIAL PRIMARY KEY,
    gen_name VARCHAR(200) NOT NULL COMMENT '生成任务名称',
    gen_type VARCHAR(50) NOT NULL COMMENT '生成类型: form-page-module',
    gen_config JSONB NOT NULL COMMENT '生成配置JSON',
    gen_result JSONB COMMENT '生成结果JSON',
    status SMALLINT DEFAULT 1 COMMENT '状态: 1-生成中 2-成功 3-失败',
    error_msg TEXT COMMENT '错误信息',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT,
    del_flag SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    biz_date INTEGER
);

-- ====================================
-- 组件库管理
-- ====================================
CREATE TABLE IF NOT EXISTS lowcode_component (
    id BIGSERIAL PRIMARY KEY,
    component_name VARCHAR(100) NOT NULL COMMENT '组件名称',
    component_code VARCHAR(100) NOT NULL UNIQUE COMMENT '组件编码',
    component_type VARCHAR(50) NOT NULL COMMENT '组件类型: input-select-table等',
    component_config JSONB NOT NULL COMMENT '组件配置JSON',
    icon VARCHAR(100) COMMENT '组件图标',
    status SMALLINT DEFAULT 1 COMMENT '状态: 1-启用 2-禁用',
    sort_order INTEGER DEFAULT 0 COMMENT '排序',
    description TEXT COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    del_flag SMALLINT DEFAULT 0,
    tenant_id BIGINT,
    biz_date INTEGER
);

-- ====================================
-- 创建索引
-- ====================================
CREATE INDEX IF NOT EXISTS idx_lowcode_form_code ON lowcode_form(form_code);
CREATE INDEX IF NOT EXISTS idx_lowcode_form_status ON lowcode_form(status);
CREATE INDEX IF NOT EXISTS idx_lowcode_form_tenant ON lowcode_form(tenant_id);
CREATE INDEX IF NOT EXISTS idx_lowcode_form_data_form ON lowcode_form_data(form_id);
CREATE INDEX IF NOT EXISTS idx_lowcode_form_data_code ON lowcode_form_data(form_code);
CREATE INDEX IF NOT EXISTS idx_lowcode_form_data_tenant ON lowcode_form_data(tenant_id);
CREATE INDEX IF NOT EXISTS idx_lowcode_page_code ON lowcode_page(page_code);
CREATE INDEX IF NOT EXISTS idx_lowcode_page_type ON lowcode_page(page_type);
CREATE INDEX IF NOT EXISTS idx_lowcode_page_tenant ON lowcode_page(tenant_id);
CREATE INDEX IF NOT EXISTS idx_lowcode_datasource_code ON lowcode_datasource(ds_code);
CREATE INDEX IF NOT EXISTS idx_lowcode_datasource_tenant ON lowcode_datasource(tenant_id);
CREATE INDEX IF NOT EXISTS idx_lowcode_component_code ON lowcode_component(component_code);
CREATE INDEX IF NOT EXISTS idx_lowcode_component_type ON lowcode_component(component_type);
CREATE INDEX IF NOT EXISTS idx_lowcode_component_tenant ON lowcode_component(tenant_id);

-- ====================================
-- 初始化数据
-- ====================================

-- 插入默认组件
INSERT INTO lowcode_component (component_name, component_code, component_type, component_config, icon, status, sort_order, description) VALUES
('文本输入', 'input_text', 'input', '{"placeholder": "请输入", "maxLength": 100}', 'input', 1, 1, '单行文本输入组件'),
('文本域', 'textarea', 'input', '{"placeholder": "请输入", "maxLength": 500}', 'align-left', 1, 2, '多行文本输入组件'),
('数字输入', 'input_number', 'input', '{"placeholder": "请输入", "min": 0, "step": 1}', 'hash', 1, 3, '数字输入组件'),
('下拉选择', 'select', 'select', '{"placeholder": "请选择", "options": []}', 'arrow-down', 1, 4, '下拉选择组件'),
('单选框', 'radio', 'select', '{"options": []}', 'check-circle', 1, 5, '单选框组件'),
('复选框', 'checkbox', 'select', '{"options": []}', 'square', 1, 6, '复选框组件'),
('日期选择', 'date_picker', 'date', '{"format": "YYYY-MM-DD"}', 'calendar', 1, 7, '日期选择组件'),
('时间选择', 'time_picker', 'date', '{"format": "HH:mm:ss"}', 'clock', 1, 8, '时间选择组件'),
('日期时间选择', 'datetime_picker', 'date', '{"format": "YYYY-MM-DD HH:mm:ss"}', 'clock-circle', 1, 9, '日期时间选择组件'),
('文件上传', 'upload', 'upload', '{"maxSize": 10, "accept": "*"}', 'upload', 1, 10, '文件上传组件'),
('图片上传', 'image_upload', 'upload', '{"maxSize": 5, "accept": "image/*"}', 'image', 1, 11, '图片上传组件'),
('表格', 'table', 'table', '{"columns": []}', 'table', 1, 12, '表格组件'),
('表单', 'form', 'form', '{"fields": []}', 'form', 1, 13, '表单组件'),
('图表', 'chart', 'chart', '{"type": "line"}', 'line-chart', 1, 14, '图表组件'),
('按钮', 'button', 'action', '{"type": "primary"}', 'button', 1, 15, '按钮组件');
