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

\c qooerp_workflow

-- ============================================================================
-- QooERP 工作流引擎模块数据库脚本
-- ============================================================================

-- 创建数据库
CREATE DATABASE qooerp_workflow WITH ENCODING 'UTF8';

-- 流程定义表
DROP TABLE IF EXISTS workflow_definition CASCADE;
CREATE TABLE workflow_definition (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  definition_id VARCHAR(100) NOT NULL,
  definition_name VARCHAR(200) NOT NULL,
  definition_key VARCHAR(100) NOT NULL,
  version INTEGER NOT NULL DEFAULT 1,
  bpmn_content TEXT,
  status SMALLINT NOT NULL DEFAULT 0,
  category VARCHAR(100) DEFAULT NULL,
  description TEXT,
  deploy_time TIMESTAMP DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
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
COMMENT ON COLUMN workflow_definition.status IS '状态(0-草稿,1-已发布,2-已停用)';
COMMENT ON COLUMN workflow_definition.category IS '流程分类';
COMMENT ON COLUMN workflow_definition.description IS '流程描述';
COMMENT ON COLUMN workflow_definition.deploy_time IS '部署时间';

CREATE UNIQUE INDEX uk_workflow_definition_key_version ON workflow_definition(definition_key, version, tenant_id, deleted);
CREATE INDEX idx_workflow_definition_id ON workflow_definition(definition_id);
CREATE INDEX idx_workflow_definition_status ON workflow_definition(status);
CREATE INDEX idx_workflow_definition_category ON workflow_definition(category);
CREATE INDEX idx_workflow_definition_tenant_id ON workflow_definition(tenant_id);

CREATE TRIGGER trg_workflow_definition_update_time
  BEFORE UPDATE ON workflow_definition
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 流程实例表
DROP TABLE IF EXISTS workflow_instance CASCADE;
CREATE TABLE workflow_instance (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  instance_id VARCHAR(100) NOT NULL,
  definition_id VARCHAR(100) NOT NULL,
  instance_name VARCHAR(200) NOT NULL,
  business_key VARCHAR(200) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP DEFAULT NULL,
  start_user_id BIGINT NOT NULL,
  start_user_name VARCHAR(50) NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_instance IS '流程实例表';
COMMENT ON COLUMN workflow_instance.instance_id IS '流程实例ID';
COMMENT ON COLUMN workflow_instance.instance_name IS '流程实例名称';
COMMENT ON COLUMN workflow_instance.business_key IS '业务Key';
COMMENT ON COLUMN workflow_instance.status IS '状态(0-运行中,1-已完成,2-已取消,3-已挂起)';
COMMENT ON COLUMN workflow_instance.start_time IS '开始时间';
COMMENT ON COLUMN workflow_instance.end_time IS '结束时间';

CREATE UNIQUE INDEX uk_workflow_instance_id ON workflow_instance(instance_id, tenant_id, deleted);
CREATE INDEX idx_workflow_instance_definition_id ON workflow_instance(definition_id);
CREATE INDEX idx_workflow_instance_business_key ON workflow_instance(business_key);
CREATE INDEX idx_workflow_instance_status ON workflow_instance(status);
CREATE INDEX idx_workflow_instance_start_time ON workflow_instance(start_time);
CREATE INDEX idx_workflow_instance_tenant_id ON workflow_instance(tenant_id);

CREATE TRIGGER trg_workflow_instance_update_time
  BEFORE UPDATE ON workflow_instance
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 流程任务表
DROP TABLE IF EXISTS workflow_task CASCADE;
CREATE TABLE workflow_task (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  task_id VARCHAR(100) NOT NULL,
  instance_id VARCHAR(100) NOT NULL,
  task_name VARCHAR(200) NOT NULL,
  task_key VARCHAR(100) NOT NULL,
  assignee_id BIGINT DEFAULT NULL,
  assignee_name VARCHAR(50) DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  due_time TIMESTAMP DEFAULT NULL,
  complete_time TIMESTAMP DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_task IS '流程任务表';
COMMENT ON COLUMN workflow_task.task_id IS '任务ID';
COMMENT ON COLUMN workflow_task.task_name IS '任务名称';
COMMENT ON COLUMN workflow_task.task_key IS '任务Key';
COMMENT ON COLUMN workflow_task.status IS '状态(0-待处理,1-已处理,2-已驳回)';

CREATE UNIQUE INDEX uk_workflow_task_id ON workflow_task(task_id, tenant_id, deleted);
CREATE INDEX idx_workflow_task_instance_id ON workflow_task(instance_id);
CREATE INDEX idx_workflow_task_assignee_id ON workflow_task(assignee_id);
CREATE INDEX idx_workflow_task_status ON workflow_task(status);
CREATE INDEX idx_workflow_task_create_time ON workflow_task(create_time);
CREATE INDEX idx_workflow_task_tenant_id ON workflow_task(tenant_id);

-- 流程变量表
DROP TABLE IF EXISTS workflow_variable CASCADE;
CREATE TABLE workflow_variable (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  instance_id VARCHAR(100) NOT NULL,
  task_id VARCHAR(100) DEFAULT NULL,
  variable_name VARCHAR(100) NOT NULL,
  variable_value TEXT,
  variable_type VARCHAR(50) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_variable IS '流程变量表';

CREATE INDEX idx_workflow_variable_instance_id ON workflow_variable(instance_id);
CREATE INDEX idx_workflow_variable_task_id ON workflow_variable(task_id);
CREATE INDEX idx_workflow_variable_variable_name ON workflow_variable(variable_name);
CREATE INDEX idx_workflow_variable_tenant_id ON workflow_variable(tenant_id);

CREATE TRIGGER trg_workflow_variable_update_time
  BEFORE UPDATE ON workflow_variable
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 流程日志表
DROP TABLE IF EXISTS workflow_log CASCADE;
CREATE TABLE workflow_log (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  instance_id VARCHAR(100) NOT NULL,
  task_id VARCHAR(100) DEFAULT NULL,
  action VARCHAR(50) NOT NULL,
  action_user_id BIGINT NOT NULL,
  action_user_name VARCHAR(50) NOT NULL,
  action_comment TEXT,
  action_time TIMESTAMP NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE workflow_log IS '流程日志表';

CREATE INDEX idx_workflow_log_instance_id ON workflow_log(instance_id);
CREATE INDEX idx_workflow_log_task_id ON workflow_log(task_id);
CREATE INDEX idx_workflow_log_action_time ON workflow_log(action_time);
CREATE INDEX idx_workflow_log_tenant_id ON workflow_log(tenant_id);

-- 插入演示数据
INSERT INTO workflow_definition (
  definition_id, definition_name, definition_key, version,
  bpmn_content, status, category, description, tenant_id
) VALUES
('leave-request:1:001', '请假流程', 'leave-request', 1,
 '<?xml version="1.0" encoding="UTF-8"?><definitions>...</definitions>', 1,
 '人事', '员工请假审批流程', 1);

-- 创建用户
CREATE USER qooerp_workflow WITH PASSWORD 'qooerp_workflow_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_workflow;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_workflow;
