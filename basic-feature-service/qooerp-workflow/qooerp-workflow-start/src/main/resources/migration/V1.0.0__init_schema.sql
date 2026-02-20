-- ============================================================================
-- PostgreSQL 数据库初始化脚本
-- ============================================================================
-- QooERP 工作流引擎模块数据库脚本
-- 版本: v2.0
-- 创建日期: 2026-02-18
-- 数据库: PostgreSQL 15+
-- ============================================================================

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- 流程分类表
-- ============================================================================
DROP TABLE IF EXISTS workflow_category CASCADE;
CREATE TABLE workflow_category (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50) NOT NULL,
  parent_id VARCHAR(50) DEFAULT NULL,
  level INT DEFAULT 1,
  sort INT DEFAULT 0,
  icon VARCHAR(100) DEFAULT NULL,
  description VARCHAR(500) DEFAULT NULL,
  status INT NOT NULL DEFAULT 1,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_category IS '流程分类表';
COMMENT ON COLUMN workflow_category.name IS '分类名称';
COMMENT ON COLUMN workflow_category.code IS '分类编码';
COMMENT ON COLUMN workflow_category.parent_id IS '父分类ID';
COMMENT ON COLUMN workflow_category.icon IS '图标';
COMMENT ON COLUMN workflow_category.sort IS '排序号';
COMMENT ON COLUMN workflow_category.description IS '描述';
COMMENT ON COLUMN workflow_category.status IS '状态(0-禁用,1-启用)';

CREATE UNIQUE INDEX uk_workflow_category_code ON workflow_category(code, tenant_id, deleted);
CREATE INDEX idx_workflow_category_parent_id ON workflow_category(parent_id);
CREATE INDEX idx_workflow_category_tenant_id ON workflow_category(tenant_id);

CREATE TRIGGER trg_workflow_category_update_time
  BEFORE UPDATE ON workflow_category
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程表单表
-- ============================================================================
DROP TABLE IF EXISTS workflow_form CASCADE;
CREATE TABLE workflow_form (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(100) NOT NULL,
  version INT NOT NULL DEFAULT 1,
  form_type VARCHAR(50) DEFAULT 'single',
  form_config TEXT DEFAULT NULL,
  description TEXT DEFAULT NULL,
  status INT NOT NULL DEFAULT 0,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_form IS '流程表单表';
COMMENT ON COLUMN workflow_form.name IS '表单名称';
COMMENT ON COLUMN workflow_form.code IS '表单编码';
COMMENT ON COLUMN workflow_form.version IS '表单版本';
COMMENT ON COLUMN workflow_form.form_type IS '表单类型(single-单表,master-detail-主子表)';
COMMENT ON COLUMN workflow_form.form_config IS '表单配置(JSON)';
COMMENT ON COLUMN workflow_form.description IS '描述';
COMMENT ON COLUMN workflow_form.status IS '状态(0-草稿,1-已发布,2-已停用)';

CREATE UNIQUE INDEX uk_workflow_form_code ON workflow_form(code, tenant_id, deleted);
CREATE INDEX idx_workflow_form_status ON workflow_form(status);
CREATE INDEX idx_workflow_form_tenant_id ON workflow_form(tenant_id);

CREATE TRIGGER trg_workflow_form_update_time
  BEFORE UPDATE ON workflow_form
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 表单字段表
-- ============================================================================
DROP TABLE IF EXISTS workflow_form_field CASCADE;
CREATE TABLE workflow_form_field (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  form_id VARCHAR(50) NOT NULL,
  field_name VARCHAR(100) NOT NULL,
  field_label VARCHAR(200) NOT NULL,
  field_type VARCHAR(50) DEFAULT 'text',
  field_config TEXT DEFAULT NULL,
  default_value VARCHAR(500) DEFAULT NULL,
  required INT NOT NULL DEFAULT 0,
  readonly INT NOT NULL DEFAULT 0,
  hidden INT NOT NULL DEFAULT 0,
  validation_rules TEXT DEFAULT NULL,
  sort INT NOT NULL DEFAULT 0,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_form_field IS '表单字段表';
COMMENT ON COLUMN workflow_form_field.form_id IS '表单ID';
COMMENT ON COLUMN workflow_form_field.field_name IS '字段名称';
COMMENT ON COLUMN workflow_form_field.field_label IS '字段标题';
COMMENT ON COLUMN workflow_form_field.field_type IS '字段类型';
COMMENT ON COLUMN workflow_form_field.field_config IS '字段配置(JSON)';
COMMENT ON COLUMN workflow_form_field.default_value IS '默认值';
COMMENT ON COLUMN workflow_form_field.required IS '是否必填(0-否,1-是)';
COMMENT ON COLUMN workflow_form_field.readonly IS '是否只读(0-否,1-是)';
COMMENT ON COLUMN workflow_form_field.hidden IS '是否隐藏(0-否,1-是)';
COMMENT ON COLUMN workflow_form_field.validation_rules IS '验证规则(JSON)';
COMMENT ON COLUMN workflow_form_field.sort IS '排序号';

CREATE UNIQUE INDEX uk_workflow_form_field ON workflow_form_field(form_id, field_name, tenant_id, deleted);
CREATE INDEX idx_workflow_form_field_form_id ON workflow_form_field(form_id);
CREATE INDEX idx_workflow_form_field_tenant_id ON workflow_form_field(tenant_id);

CREATE TRIGGER trg_workflow_form_field_update_time
  BEFORE UPDATE ON workflow_form_field
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 表单数据记录表
-- ============================================================================
DROP TABLE IF EXISTS workflow_form_record CASCADE;
CREATE TABLE workflow_form_record (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  form_id VARCHAR(50) NOT NULL,
  business_type VARCHAR(50) DEFAULT NULL,
  business_id VARCHAR(50) DEFAULT NULL,
  form_data TEXT DEFAULT NULL,
  process_instance_id VARCHAR(50) DEFAULT NULL,
  task_id VARCHAR(50) DEFAULT NULL,
  creator_id VARCHAR(50) DEFAULT NULL,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_form_record IS '表单数据记录表';
COMMENT ON COLUMN workflow_form_record.form_id IS '表单ID';
COMMENT ON COLUMN workflow_form_record.business_type IS '业务类型';
COMMENT ON COLUMN workflow_form_record.business_id IS '业务ID';
COMMENT ON COLUMN workflow_form_record.form_data IS '表单数据(JSON)';
COMMENT ON COLUMN workflow_form_record.process_instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_form_record.task_id IS '任务ID';
COMMENT ON COLUMN workflow_form_record.creator_id IS '创建人ID';

CREATE INDEX idx_workflow_form_record_form_id ON workflow_form_record(form_id);
CREATE INDEX idx_workflow_form_record_business ON workflow_form_record(business_type, business_id);
CREATE INDEX idx_workflow_form_record_instance_id ON workflow_form_record(process_instance_id);
CREATE INDEX idx_workflow_form_record_task_id ON workflow_form_record(task_id);
CREATE INDEX idx_workflow_form_record_tenant_id ON workflow_form_record(tenant_id);

CREATE TRIGGER trg_workflow_form_record_update_time
  BEFORE UPDATE ON workflow_form_record
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程模板表
-- ============================================================================
DROP TABLE IF EXISTS workflow_template CASCADE;
CREATE TABLE workflow_template (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(100) NOT NULL,
  process_definition_id VARCHAR(100) DEFAULT NULL,
  process_definition_key VARCHAR(100) DEFAULT NULL,
  version INT NOT NULL DEFAULT 1,
  category_id VARCHAR(50) DEFAULT NULL,
  form_id VARCHAR(50) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  icon VARCHAR(100) DEFAULT NULL,
  is_public INT NOT NULL DEFAULT 0,
  use_count INT NOT NULL DEFAULT 0,
  status INT NOT NULL DEFAULT 0,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_template IS '流程模板表';
COMMENT ON COLUMN workflow_template.name IS '模板名称';
COMMENT ON COLUMN workflow_template.code IS '模板编码';
COMMENT ON COLUMN workflow_template.process_definition_id IS '流程定义ID';
COMMENT ON COLUMN workflow_template.process_definition_key IS '流程定义Key';
COMMENT ON COLUMN workflow_template.version IS '模板版本';
COMMENT ON COLUMN workflow_template.category_id IS '分类ID';
COMMENT ON COLUMN workflow_template.form_id IS '表单ID';
COMMENT ON COLUMN workflow_template.description IS '描述';
COMMENT ON COLUMN workflow_template.icon IS '图标';
COMMENT ON COLUMN workflow_template.is_public IS '是否公开(0-私有,1-公开)';
COMMENT ON COLUMN workflow_template.use_count IS '使用次数';
COMMENT ON COLUMN workflow_template.status IS '状态(0-草稿,1-已发布,2-已停用)';

CREATE UNIQUE INDEX uk_workflow_template_code ON workflow_template(code, tenant_id, deleted);
CREATE INDEX idx_workflow_template_category_id ON workflow_template(category_id);
CREATE INDEX idx_workflow_template_form_id ON workflow_template(form_id);
CREATE INDEX idx_workflow_template_tenant_id ON workflow_template(tenant_id);

CREATE TRIGGER trg_workflow_template_update_time
  BEFORE UPDATE ON workflow_template
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程通知表
-- ============================================================================
DROP TABLE IF EXISTS workflow_notification CASCADE;
CREATE TABLE workflow_notification (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  process_instance_id VARCHAR(50) DEFAULT NULL,
  task_id VARCHAR(50) DEFAULT NULL,
  notification_type VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT DEFAULT NULL,
  receiver_id VARCHAR(50) NOT NULL,
  receiver_name VARCHAR(50) NOT NULL,
  notify_method VARCHAR(50) NOT NULL,
  send_status INT NOT NULL DEFAULT 0,
  send_time TIMESTAMP DEFAULT NULL,
  failure_reason VARCHAR(500) DEFAULT NULL,
  is_read INT NOT NULL DEFAULT 0,
  read_time TIMESTAMP DEFAULT NULL,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_notification IS '流程通知表';
COMMENT ON COLUMN workflow_notification.process_instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_notification.task_id IS '任务ID';
COMMENT ON COLUMN workflow_notification.notification_type IS '通知类型';
COMMENT ON COLUMN workflow_notification.title IS '通知标题';
COMMENT ON COLUMN workflow_notification.content IS '通知内容';
COMMENT ON COLUMN workflow_notification.receiver_id IS '接收人ID';
COMMENT ON COLUMN workflow_notification.receiver_name IS '接收人姓名';
COMMENT ON COLUMN workflow_notification.notify_method IS '通知方式';
COMMENT ON COLUMN workflow_notification.send_status IS '发送状态(0-待发送,1-已发送,2-发送失败)';
COMMENT ON COLUMN workflow_notification.send_time IS '发送时间';
COMMENT ON COLUMN workflow_notification.failure_reason IS '失败原因';
COMMENT ON COLUMN workflow_notification.is_read IS '是否已读(0-未读,1-已读)';
COMMENT ON COLUMN workflow_notification.read_time IS '已读时间';

CREATE INDEX idx_workflow_notification_receiver_id ON workflow_notification(receiver_id);
CREATE INDEX idx_workflow_notification_process_instance_id ON workflow_notification(process_instance_id);
CREATE INDEX idx_workflow_notification_task_id ON workflow_notification(task_id);
CREATE INDEX idx_workflow_notification_notification_type ON workflow_notification(notification_type);
CREATE INDEX idx_workflow_notification_is_read ON workflow_notification(is_read);
CREATE INDEX idx_workflow_notification_send_status ON workflow_notification(send_status);
CREATE INDEX idx_workflow_notification_create_time ON workflow_notification(create_time);
CREATE INDEX idx_workflow_notification_tenant_id ON workflow_notification(tenant_id);

CREATE TRIGGER trg_workflow_notification_update_time
  BEFORE UPDATE ON workflow_notification
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程统计表
-- ============================================================================
DROP TABLE IF EXISTS workflow_counter CASCADE;
CREATE TABLE workflow_counter (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  counter_type VARCHAR(50) NOT NULL,
  counter_key VARCHAR(100) NOT NULL,
  name VARCHAR(200) DEFAULT NULL,
  counter_date VARCHAR(20) NOT NULL,
  start_count INT NOT NULL DEFAULT 0,
  complete_count INT NOT NULL DEFAULT 0,
  cancel_count INT NOT NULL DEFAULT 0,
  suspend_count INT NOT NULL DEFAULT 0,
  avg_duration BIGINT DEFAULT NULL,
  max_duration BIGINT DEFAULT NULL,
  min_duration BIGINT DEFAULT NULL,
  timeout_count INT NOT NULL DEFAULT 0,
  counter_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tenant_id VARCHAR(50) NOT NULL DEFAULT '1',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted INT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_counter IS '流程统计表';
COMMENT ON COLUMN workflow_counter.counter_type IS '统计类型';
COMMENT ON COLUMN workflow_counter.counter_key IS '统计Key';
COMMENT ON COLUMN workflow_counter.name IS '统计名称';
COMMENT ON COLUMN workflow_counter.counter_date IS '统计日期';
COMMENT ON COLUMN workflow_counter.start_count IS '启动次数';
COMMENT ON COLUMN workflow_counter.complete_count IS '完成次数';
COMMENT ON COLUMN workflow_counter.cancel_count IS '取消次数';
COMMENT ON COLUMN workflow_counter.suspend_count IS '挂起次数';
COMMENT ON COLUMN workflow_counter.avg_duration IS '平均耗时(秒)';
COMMENT ON COLUMN workflow_counter.max_duration IS '最大耗时(秒)';
COMMENT ON COLUMN workflow_counter.min_duration IS '最小耗时(秒)';
COMMENT ON COLUMN workflow_counter.timeout_count IS '超时次数';
COMMENT ON COLUMN workflow_counter.counter_time IS '统计时间';

CREATE UNIQUE INDEX uk_workflow_counter ON workflow_counter(counter_key, counter_date, tenant_id, deleted);
CREATE INDEX idx_workflow_counter_counter_type ON workflow_counter(counter_type);
CREATE INDEX idx_workflow_counter_counter_date ON workflow_counter(counter_date);
CREATE INDEX idx_workflow_counter_tenant_id ON workflow_counter(tenant_id);

CREATE TRIGGER trg_workflow_counter_update_time
  BEFORE UPDATE ON workflow_counter
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 插入初始化数据
-- ============================================================================

-- 插入流程分类
INSERT INTO workflow_category (
  id, name, code, parent_id, level, sort, icon, description, status, tenant_id
) VALUES
('1', '人事流程', 'hr', NULL, 1, 1, 'user', '人事相关流程', 1, '1'),
('2', '财务流程', 'finance', NULL, 1, 2, 'money', '财务相关流程', 1, '1'),
('3', '采购流程', 'purchase', NULL, 1, 3, 'shopping', '采购相关流程', 1, '1'),
('4', '销售流程', 'sales', NULL, 1, 4, 'shopping-cart', '销售相关流程', 1, '1'),
('5', '行政流程', 'admin', NULL, 1, 5, 'setting', '行政相关流程', 1, '1'),
('6', '请假申请', 'leave', '1', 2, 1, 'calendar', '请假相关流程', 1, '1'),
('7', '报销申请', 'expense', '2', 2, 1, 'receipt', '报销相关流程', 1, '1');

-- ============================================================================
-- 脚本执行完成
-- ============================================================================
SELECT 'QooERP Workflow Database Initialization Completed!' AS status;
