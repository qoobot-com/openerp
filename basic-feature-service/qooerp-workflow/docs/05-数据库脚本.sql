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

-- 创建数据库
CREATE DATABASE IF NOT EXISTS qooerp_workflow WITH ENCODING 'UTF8';

\c qooerp_workflow

-- ============================================================================
-- 流程分类表
-- ============================================================================
DROP TABLE IF EXISTS workflow_category CASCADE;
CREATE TABLE workflow_category (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  category_code VARCHAR(50) NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  icon VARCHAR(100) DEFAULT NULL,
  sort_order INT DEFAULT 0,
  description VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_category IS '流程分类表';
COMMENT ON COLUMN workflow_category.category_name IS '分类名称';
COMMENT ON COLUMN workflow_category.category_code IS '分类编码';
COMMENT ON COLUMN workflow_category.parent_id IS '父分类ID';
COMMENT ON COLUMN workflow_category.icon IS '图标';
COMMENT ON COLUMN workflow_category.sort_order IS '排序号';
COMMENT ON COLUMN workflow_category.description IS '描述';

CREATE UNIQUE INDEX uk_workflow_category_code ON workflow_category(category_code, tenant_id, deleted);
CREATE INDEX idx_workflow_category_parent_id ON workflow_category(parent_id);
CREATE INDEX idx_workflow_category_tenant_id ON workflow_category(tenant_id);

CREATE TRIGGER trg_workflow_category_update_time
  BEFORE UPDATE ON workflow_category
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程定义表
-- ============================================================================
DROP TABLE IF EXISTS workflow_definition CASCADE;
CREATE TABLE workflow_definition (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  definition_id VARCHAR(100) NOT NULL,
  definition_name VARCHAR(200) NOT NULL,
  definition_key VARCHAR(100) NOT NULL,
  version INT NOT NULL DEFAULT 1,
  bpmn_content TEXT DEFAULT NULL,
  svg_content TEXT DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  category_id BIGINT DEFAULT NULL,
  description TEXT DEFAULT NULL,
  deploy_time TIMESTAMP DEFAULT NULL,
  is_system SMALLINT NOT NULL DEFAULT 0,
  allow_withdraw SMALLINT NOT NULL DEFAULT 1,
  withdraw_time_limit INT DEFAULT 24,
  allow_transfer SMALLINT NOT NULL DEFAULT 1,
  allow_delegate SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_definition IS '流程定义表';
COMMENT ON COLUMN workflow_definition.definition_id IS '流程定义ID';
COMMENT ON COLUMN workflow_definition.definition_name IS '流程定义名称';
COMMENT ON COLUMN workflow_definition.definition_key IS '流程定义Key';
COMMENT ON COLUMN workflow_definition.version IS '版本号';
COMMENT ON COLUMN workflow_definition.bpmn_content IS 'BPMN内容';
COMMENT ON COLUMN workflow_definition.svg_content IS 'SVG流程图';
COMMENT ON COLUMN workflow_definition.status IS '状态(0-草稿,1-已发布,2-已停用)';
COMMENT ON COLUMN workflow_definition.category_id IS '分类ID';
COMMENT ON COLUMN workflow_definition.description IS '流程描述';
COMMENT ON COLUMN workflow_definition.deploy_time IS '部署时间';
COMMENT ON COLUMN workflow_definition.is_system IS '是否系统流程(0-否,1-是)';
COMMENT ON COLUMN workflow_definition.allow_withdraw IS '是否允许撤回(0-否,1-是)';
COMMENT ON COLUMN workflow_definition.withdraw_time_limit IS '撤回时间限制(小时)';
COMMENT ON COLUMN workflow_definition.allow_transfer IS '是否允许转办(0-否,1-是)';
COMMENT ON COLUMN workflow_definition.allow_delegate IS '是否允许委派(0-否,1-是)';

CREATE UNIQUE INDEX uk_workflow_definition_key_version ON workflow_definition(definition_key, version, tenant_id, deleted);
CREATE INDEX idx_workflow_definition_id ON workflow_definition(definition_id);
CREATE INDEX idx_workflow_definition_status ON workflow_definition(status);
CREATE INDEX idx_workflow_definition_category_id ON workflow_definition(category_id);
CREATE INDEX idx_workflow_definition_tenant_id ON workflow_definition(tenant_id);

CREATE TRIGGER trg_workflow_definition_update_time
  BEFORE UPDATE ON workflow_definition
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程实例表
-- ============================================================================
DROP TABLE IF EXISTS workflow_instance CASCADE;
CREATE TABLE workflow_instance (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  instance_id VARCHAR(100) NOT NULL,
  definition_id VARCHAR(100) NOT NULL,
  definition_key VARCHAR(100) NOT NULL,
  instance_name VARCHAR(200) NOT NULL,
  business_key VARCHAR(200) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP DEFAULT NULL,
  duration BIGINT DEFAULT NULL,
  start_user_id BIGINT NOT NULL,
  start_user_name VARCHAR(50) NOT NULL,
  start_dept_id BIGINT DEFAULT NULL,
  current_node VARCHAR(100) DEFAULT NULL,
  form_record_id VARCHAR(100) DEFAULT NULL,
  withdraw_status SMALLINT NOT NULL DEFAULT 0,
  withdraw_time TIMESTAMP DEFAULT NULL,
  withdraw_reason VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_instance IS '流程实例表';
COMMENT ON COLUMN workflow_instance.instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_instance.definition_id IS '流程定义ID';
COMMENT ON COLUMN workflow_instance.definition_key IS '流程定义Key';
COMMENT ON COLUMN workflow_instance.instance_name IS '流程实例名称';
COMMENT ON COLUMN workflow_instance.business_key IS '业务Key';
COMMENT ON COLUMN workflow_instance.status IS '状态(0-运行中,1-已完成,2-已取消,3-已挂起)';
COMMENT ON COLUMN workflow_instance.start_time IS '开始时间';
COMMENT ON COLUMN workflow_instance.end_time IS '结束时间';
COMMENT ON COLUMN workflow_instance.duration IS '持续时长(毫秒)';
COMMENT ON COLUMN workflow_instance.start_user_id IS '发起人ID';
COMMENT ON COLUMN workflow_instance.start_user_name IS '发起人姓名';
COMMENT ON COLUMN workflow_instance.start_dept_id IS '发起部门ID';
COMMENT ON COLUMN workflow_instance.current_node IS '当前节点';
COMMENT ON COLUMN workflow_instance.form_record_id IS '表单记录ID';
COMMENT ON COLUMN workflow_instance.withdraw_status IS '撤回状态(0-未撤回,1-已撤回)';
COMMENT ON COLUMN workflow_instance.withdraw_time IS '撤回时间';
COMMENT ON COLUMN workflow_instance.withdraw_reason IS '撤回原因';

CREATE UNIQUE INDEX uk_workflow_instance_id ON workflow_instance(instance_id, tenant_id, deleted);
CREATE INDEX idx_workflow_instance_definition_id ON workflow_instance(definition_id);
CREATE INDEX idx_workflow_instance_definition_key ON workflow_instance(definition_key);
CREATE INDEX idx_workflow_instance_business_key ON workflow_instance(business_key);
CREATE INDEX idx_workflow_instance_status ON workflow_instance(status);
CREATE INDEX idx_workflow_instance_start_time ON workflow_instance(start_time);
CREATE INDEX idx_workflow_instance_start_user_id ON workflow_instance(start_user_id);
CREATE INDEX idx_workflow_instance_tenant_id ON workflow_instance(tenant_id);

CREATE TRIGGER trg_workflow_instance_update_time
  BEFORE UPDATE ON workflow_instance
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程任务表
-- ============================================================================
DROP TABLE IF EXISTS workflow_task CASCADE;
CREATE TABLE workflow_task (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  task_id VARCHAR(100) NOT NULL,
  instance_id VARCHAR(100) NOT NULL,
  definition_id VARCHAR(100) NOT NULL,
  task_name VARCHAR(200) NOT NULL,
  task_key VARCHAR(100) NOT NULL,
  task_type VARCHAR(50) DEFAULT 'userTask',
  assignee_id BIGINT DEFAULT NULL,
  assignee_name VARCHAR(50) DEFAULT NULL,
  assignee_type VARCHAR(20) DEFAULT 'user',
  candidate_users TEXT DEFAULT NULL,
  candidate_groups TEXT DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  priority INT NOT NULL DEFAULT 2,
  due_time TIMESTAMP DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  complete_time TIMESTAMP DEFAULT NULL,
  duration BIGINT DEFAULT NULL,
  comment TEXT DEFAULT NULL,
  is_cc SMALLINT NOT NULL DEFAULT 0,
  parent_task_id VARCHAR(100) DEFAULT NULL,
  form_id BIGINT DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_task IS '流程任务表';
COMMENT ON COLUMN workflow_task.task_id IS '任务ID';
COMMENT ON COLUMN workflow_task.instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_task.definition_id IS '流程定义ID';
COMMENT ON COLUMN workflow_task.task_name IS '任务名称';
COMMENT ON COLUMN workflow_task.task_key IS '任务Key';
COMMENT ON COLUMN workflow_task.task_type IS '任务类型(userTask,serviceTask等)';
COMMENT ON COLUMN workflow_task.assignee_id IS '处理人ID';
COMMENT ON COLUMN workflow_task.assignee_name IS '处理人姓名';
COMMENT ON COLUMN workflow_task.assignee_type IS '分配类型(user,role,dept,expression)';
COMMENT ON COLUMN workflow_task.candidate_users IS '候选人列表(JSON)';
COMMENT ON COLUMN workflow_task.candidate_groups IS '候选组列表(JSON)';
COMMENT ON COLUMN workflow_task.status IS '状态(0-待处理,1-已处理,2-已驳回,3-已撤回)';
COMMENT ON COLUMN workflow_task.priority IS '优先级(1-低,2-中,3-高)';
COMMENT ON COLUMN workflow_task.due_time IS '到期时间';
COMMENT ON COLUMN workflow_task.comment IS '处理意见';
COMMENT ON COLUMN workflow_task.is_cc IS '是否抄送(0-否,1-是)';
COMMENT ON COLUMN workflow_task.parent_task_id IS '父任务ID(加签场景)';
COMMENT ON COLUMN workflow_task.form_id IS '关联表单ID';

CREATE UNIQUE INDEX uk_workflow_task_id ON workflow_task(task_id, tenant_id, deleted);
CREATE INDEX idx_workflow_task_instance_id ON workflow_task(instance_id);
CREATE INDEX idx_workflow_task_assignee_id ON workflow_task(assignee_id);
CREATE INDEX idx_workflow_task_status ON workflow_task(status);
CREATE INDEX idx_workflow_task_create_time ON workflow_task(create_time);
CREATE INDEX idx_workflow_task_due_time ON workflow_task(due_time);
CREATE INDEX idx_workflow_task_tenant_id ON workflow_task(tenant_id);

-- ============================================================================
-- 流程变量表
-- ============================================================================
DROP TABLE IF EXISTS workflow_variable CASCADE;
CREATE TABLE workflow_variable (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  instance_id VARCHAR(100) NOT NULL,
  task_id VARCHAR(100) DEFAULT NULL,
  execution_id VARCHAR(100) DEFAULT NULL,
  variable_name VARCHAR(100) NOT NULL,
  variable_value TEXT DEFAULT NULL,
  variable_type VARCHAR(50) DEFAULT NULL,
  variable_scope VARCHAR(20) DEFAULT 'global',
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_variable IS '流程变量表';
COMMENT ON COLUMN workflow_variable.instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_variable.task_id IS '任务ID(可选)';
COMMENT ON COLUMN workflow_variable.execution_id IS '执行ID';
COMMENT ON COLUMN workflow_variable.variable_name IS '变量名称';
COMMENT ON COLUMN workflow_variable.variable_value IS '变量值';
COMMENT ON COLUMN workflow_variable.variable_type IS '变量类型(string, integer, boolean, date, json等)';
COMMENT ON COLUMN workflow_variable.variable_scope IS '变量范围(global,local,task)';

CREATE INDEX idx_workflow_variable_instance_id ON workflow_variable(instance_id);
CREATE INDEX idx_workflow_variable_task_id ON workflow_variable(task_id);
CREATE INDEX idx_workflow_variable_variable_name ON workflow_variable(variable_name);
CREATE INDEX idx_workflow_variable_tenant_id ON workflow_variable(tenant_id);

CREATE TRIGGER trg_workflow_variable_update_time
  BEFORE UPDATE ON workflow_variable
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- ============================================================================
-- 流程日志表
-- ============================================================================
DROP TABLE IF EXISTS workflow_log CASCADE;
CREATE TABLE workflow_log (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  instance_id VARCHAR(100) NOT NULL,
  task_id VARCHAR(100) DEFAULT NULL,
  node_id VARCHAR(100) DEFAULT NULL,
  node_name VARCHAR(200) DEFAULT NULL,
  action VARCHAR(50) NOT NULL,
  action_user_id BIGINT NOT NULL,
  action_user_name VARCHAR(50) NOT NULL,
  action_comment TEXT DEFAULT NULL,
  action_time TIMESTAMP NOT NULL,
  source_node VARCHAR(100) DEFAULT NULL,
  target_node VARCHAR(100) DEFAULT NULL,
  variables TEXT DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_log IS '流程日志表';
COMMENT ON COLUMN workflow_log.instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_log.task_id IS '任务ID';
COMMENT ON COLUMN workflow_log.node_id IS '节点ID';
COMMENT ON COLUMN workflow_log.node_name IS '节点名称';
COMMENT ON COLUMN workflow_log.action IS '操作类型(start, complete, reject, withdraw, suspend, activate, cancel, transfer, delegate)';
COMMENT ON COLUMN workflow_log.action_user_id IS '操作人ID';
COMMENT ON COLUMN workflow_log.action_user_name IS '操作人姓名';
COMMENT ON COLUMN workflow_log.action_comment IS '操作意见';
COMMENT ON COLUMN workflow_log.action_time IS '操作时间';
COMMENT ON COLUMN workflow_log.source_node IS '源节点';
COMMENT ON COLUMN workflow_log.target_node IS '目标节点';
COMMENT ON COLUMN workflow_log.variables IS '变量数据(JSON)';

CREATE INDEX idx_workflow_log_instance_id ON workflow_log(instance_id);
CREATE INDEX idx_workflow_log_task_id ON workflow_log(task_id);
CREATE INDEX idx_workflow_log_action_time ON workflow_log(action_time);
CREATE INDEX idx_workflow_log_action_user_id ON workflow_log(action_user_id);
CREATE INDEX idx_workflow_log_tenant_id ON workflow_log(tenant_id);

-- ============================================================================
-- 流程表单表
-- ============================================================================
DROP TABLE IF EXISTS workflow_form CASCADE;
CREATE TABLE workflow_form (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  form_key VARCHAR(100) NOT NULL,
  form_name VARCHAR(200) NOT NULL,
  form_type VARCHAR(50) DEFAULT 'dynamic',
  form_schema TEXT DEFAULT NULL,
  form_layout TEXT DEFAULT NULL,
  definition_id VARCHAR(100) DEFAULT NULL,
  node_id VARCHAR(100) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  description TEXT DEFAULT NULL,
  version INT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_form IS '流程表单表';
COMMENT ON COLUMN workflow_form.form_key IS '表单Key（唯一）';
COMMENT ON COLUMN workflow_form.form_name IS '表单名称';
COMMENT ON COLUMN workflow_form.form_type IS '表单类型(dynamic,template)';
COMMENT ON COLUMN workflow_form.form_schema IS '表单结构(JSON Schema)';
COMMENT ON COLUMN workflow_form.form_layout IS '表单布局(JSON)';
COMMENT ON COLUMN workflow_form.definition_id IS '关联流程定义ID';
COMMENT ON COLUMN workflow_form.node_id IS '关联节点ID';
COMMENT ON COLUMN workflow_form.status IS '状态(0-草稿,1-已发布)';
COMMENT ON COLUMN workflow_form.version IS '版本号';

CREATE UNIQUE INDEX uk_workflow_form_key ON workflow_form(form_key, tenant_id, deleted);
CREATE INDEX idx_workflow_form_definition_id ON workflow_form(definition_id);
CREATE INDEX idx_workflow_form_node_id ON workflow_form(node_id);
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
  id BIGSERIAL NOT NULL PRIMARY KEY,
  form_id BIGINT NOT NULL,
  field_key VARCHAR(100) NOT NULL,
  field_name VARCHAR(200) NOT NULL,
  field_type VARCHAR(50) DEFAULT 'input',
  field_options TEXT DEFAULT NULL,
  field_rules TEXT DEFAULT NULL,
  default_value VARCHAR(500) DEFAULT NULL,
  placeholder VARCHAR(200) DEFAULT NULL,
  required SMALLINT NOT NULL DEFAULT 0,
  readonly SMALLINT NOT NULL DEFAULT 0,
  visible SMALLINT NOT NULL DEFAULT 1,
  sort_order INT NOT NULL DEFAULT 0,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_form_field IS '表单字段表';
COMMENT ON COLUMN workflow_form_field.form_id IS '表单ID';
COMMENT ON COLUMN workflow_form_field.field_key IS '字段Key';
COMMENT ON COLUMN workflow_form_field.field_name IS '字段名称';
COMMENT ON COLUMN workflow_form_field.field_type IS '字段类型(input,textarea,select,checkbox,datetime等)';
COMMENT ON COLUMN workflow_form_field.field_options IS '字段选项(JSON)';
COMMENT ON COLUMN workflow_form_field.field_rules IS '字段规则(验证规则等JSON)';
COMMENT ON COLUMN workflow_form_field.default_value IS '默认值';
COMMENT ON COLUMN workflow_form_field.placeholder IS '占位符';
COMMENT ON COLUMN workflow_form_field.required IS '是否必填(0-否,1-是)';
COMMENT ON COLUMN workflow_form_field.readonly IS '是否只读(0-否,1-是)';
COMMENT ON COLUMN workflow_form_field.visible IS '是否可见(0-否,1-是)';
COMMENT ON COLUMN workflow_form_field.sort_order IS '排序号';

CREATE UNIQUE INDEX uk_workflow_form_field ON workflow_form_field(form_id, field_key, tenant_id, deleted);
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
  id VARCHAR(100) NOT NULL PRIMARY KEY,
  record_id VARCHAR(100) NOT NULL,
  form_id BIGINT NOT NULL,
  instance_id VARCHAR(100) DEFAULT NULL,
  task_id VARCHAR(100) DEFAULT NULL,
  form_data TEXT DEFAULT NULL,
  submit_time TIMESTAMP NOT NULL,
  submit_user_id BIGINT NOT NULL,
  submit_user_name VARCHAR(50) NOT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_form_record IS '表单数据记录表';
COMMENT ON COLUMN workflow_form_record.id IS '主键（UUID）';
COMMENT ON COLUMN workflow_form_record.record_id IS '记录ID（唯一）';
COMMENT ON COLUMN workflow_form_record.form_id IS '表单ID';
COMMENT ON COLUMN workflow_form_record.instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_form_record.task_id IS '任务ID';
COMMENT ON COLUMN workflow_form_record.form_data IS '表单数据(JSON)';
COMMENT ON COLUMN workflow_form_record.submit_time IS '提交时间';
COMMENT ON COLUMN workflow_form_record.submit_user_id IS '提交人ID';
COMMENT ON COLUMN workflow_form_record.submit_user_name IS '提交人姓名';

CREATE UNIQUE INDEX uk_workflow_form_record_id ON workflow_form_record(record_id, tenant_id, deleted);
CREATE INDEX idx_workflow_form_record_form_id ON workflow_form_record(form_id);
CREATE INDEX idx_workflow_form_record_instance_id ON workflow_form_record(instance_id);
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
  id BIGSERIAL NOT NULL PRIMARY KEY,
  template_name VARCHAR(200) NOT NULL,
  template_key VARCHAR(100) NOT NULL,
  bpmn_content TEXT DEFAULT NULL,
  svg_content TEXT DEFAULT NULL,
  form_id BIGINT DEFAULT NULL,
  category_id BIGINT DEFAULT NULL,
  description TEXT DEFAULT NULL,
  is_public SMALLINT NOT NULL DEFAULT 0,
  use_count INT NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 0,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_template IS '流程模板表';
COMMENT ON COLUMN workflow_template.template_name IS '模板名称';
COMMENT ON COLUMN workflow_template.template_key IS '模板Key（唯一）';
COMMENT ON COLUMN workflow_template.bpmn_content IS 'BPMN内容';
COMMENT ON COLUMN workflow_template.svg_content IS 'SVG流程图';
COMMENT ON COLUMN workflow_template.form_id IS '关联表单ID';
COMMENT ON COLUMN workflow_template.category_id IS '分类ID';
COMMENT ON COLUMN workflow_template.description IS '描述';
COMMENT ON COLUMN workflow_template.is_public IS '是否公开(0-私有,1-公开)';
COMMENT ON COLUMN workflow_template.use_count IS '使用次数';
COMMENT ON COLUMN workflow_template.status IS '状态(0-草稿,1-已发布)';

CREATE UNIQUE INDEX uk_workflow_template_key ON workflow_template(template_key, tenant_id, deleted);
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
  id BIGSERIAL NOT NULL PRIMARY KEY,
  notification_type VARCHAR(50) NOT NULL,
  target_id VARCHAR(100) NOT NULL,
  target_type VARCHAR(20) NOT NULL,
  receiver_id BIGINT NOT NULL,
  receiver_name VARCHAR(50) NOT NULL,
  notification_method VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT DEFAULT NULL,
  is_read SMALLINT NOT NULL DEFAULT 0,
  read_time TIMESTAMP DEFAULT NULL,
  send_status SMALLINT NOT NULL DEFAULT 0,
  send_time TIMESTAMP DEFAULT NULL,
  error_message VARCHAR(500) DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_notification IS '流程通知表';
COMMENT ON COLUMN workflow_notification.notification_type IS '通知类型(todo,approval,overdue,cc)';
COMMENT ON COLUMN workflow_notification.target_id IS '目标ID(任务ID或实例ID)';
COMMENT ON COLUMN workflow_notification.target_type IS '目标类型(task,instance)';
COMMENT ON COLUMN workflow_notification.receiver_id IS '接收人ID';
COMMENT ON COLUMN workflow_notification.receiver_name IS '接收人姓名';
COMMENT ON COLUMN workflow_notification.notification_method IS '通知方式(email,sms,site-message,wechat)';
COMMENT ON COLUMN workflow_notification.title IS '通知标题';
COMMENT ON COLUMN workflow_notification.content IS '通知内容';
COMMENT ON COLUMN workflow_notification.is_read IS '是否已读(0-未读,1-已读)';
COMMENT ON COLUMN workflow_notification.read_time IS '读取时间';
COMMENT ON COLUMN workflow_notification.send_status IS '发送状态(0-待发送,1-已发送,2-发送失败)';
COMMENT ON COLUMN workflow_notification.send_time IS '发送时间';
COMMENT ON COLUMN workflow_notification.error_message IS '错误信息';

CREATE INDEX idx_workflow_notification_receiver_id ON workflow_notification(receiver_id);
CREATE INDEX idx_workflow_notification_target_id ON workflow_notification(target_id);
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
  id BIGSERIAL NOT NULL PRIMARY KEY,
  counter_key VARCHAR(100) NOT NULL,
  counter_type VARCHAR(50) NOT NULL,
  definition_key VARCHAR(100) DEFAULT NULL,
  counter_date DATE NOT NULL,
  counter_value BIGINT NOT NULL DEFAULT 0,
  extra_data TEXT DEFAULT NULL,
  tenant_id BIGINT NOT NULL DEFAULT 1,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE workflow_counter IS '流程统计表';
COMMENT ON COLUMN workflow_counter.counter_key IS '统计Key';
COMMENT ON COLUMN workflow_counter.counter_type IS '统计类型(instance,task,completion等)';
COMMENT ON COLUMN workflow_counter.definition_key IS '流程定义Key';
COMMENT ON COLUMN workflow_counter.counter_date IS '统计日期';
COMMENT ON COLUMN workflow_counter.counter_value IS '统计值';
COMMENT ON COLUMN workflow_counter.extra_data IS '扩展数据(JSON)';

CREATE UNIQUE INDEX uk_workflow_counter ON workflow_counter(counter_key, counter_date, tenant_id);
CREATE INDEX idx_workflow_counter_counter_type ON workflow_counter(counter_type);
CREATE INDEX idx_workflow_counter_definition_key ON workflow_counter(definition_key);
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
  category_name, category_code, parent_id, icon, sort_order, description, tenant_id
) VALUES
('人事流程', 'hr', NULL, 'user', 1, '人事相关流程', 1),
('财务流程', 'finance', NULL, 'money', 2, '财务相关流程', 1),
('采购流程', 'purchase', NULL, 'shopping', 3, '采购相关流程', 1),
('销售流程', 'sales', NULL, 'shopping-cart', 4, '销售相关流程', 1),
('行政流程', 'admin', NULL, 'setting', 5, '行政相关流程', 1);

-- 插入演示流程定义
INSERT INTO workflow_definition (
  definition_id, definition_name, definition_key, version,
  bpmn_content, svg_content, status, category_id, description, tenant_id
) VALUES
('leave-request:1:001', '请假申请流程', 'leave-request', 1,
 '<?xml version="1.0" encoding="UTF-8"?><definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.flowable.org/processdef"><process id="leave-request" name="请假申请流程" isExecutable="true"><startEvent id="startEvent1" name="开始"></startEvent><userTask id="sid-B6A6F2D3-9F6A-464F-956E-902F6A0F8E0D" name="员工填写申请"></userTask><sequenceFlow id="sid-6E6F6B2D-8F6A-464F-956E-902F6A0F8E0D" sourceRef="startEvent1" targetRef="sid-B6A6F2D3-9F6A-464F-956E-902F6A0F8E0D"></sequenceFlow><endEvent id="sid-D6A6F2D3-9F6A-464F-956E-902F6A0F8E0D" name="结束"></endEvent></process><bpmndi:BPMNDiagram id="BPMNDiagram_leave-request"><bpmndi:BPMNPlane bpmnElement="leave-request" id="BPMNPlane_leave-request"></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>', '', 1,
 1, '员工请假审批流程', 1);

-- 创建用户
CREATE USER IF NOT EXISTS qooerp_workflow WITH PASSWORD 'qooerp_workflow_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_workflow;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_workflow;

-- ============================================================================
-- 脚本执行完成
-- ============================================================================
SELECT 'QooERP Workflow Database Initialization Completed!' AS status;
