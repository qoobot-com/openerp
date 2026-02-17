-- ============================================================================
-- QooERP 工作流引擎模块数据库脚本
-- ============================================================================

CREATE DATABASE IF NOT EXISTS qooerp_workflow 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE qooerp_workflow;

-- 流程定义表
CREATE TABLE IF NOT EXISTS workflow_definition (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  definition_id VARCHAR(100) NOT NULL COMMENT '流程定义ID',
  definition_name VARCHAR(200) NOT NULL COMMENT '流程定义名称',
  definition_key VARCHAR(100) NOT NULL COMMENT '流程定义Key',
  version INT NOT NULL DEFAULT 1 COMMENT '版本号',
  bpmn_content TEXT COMMENT 'BPMN内容',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已发布,2-已停用)',
  category VARCHAR(100) DEFAULT NULL COMMENT '流程分类',
  description TEXT COMMENT '流程描述',
  deploy_time DATETIME DEFAULT NULL COMMENT '部署时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_definition_key_version (definition_key, version, tenant_id, deleted),
  KEY idx_definition_id (definition_id),
  KEY idx_status (status),
  KEY idx_category (category),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程定义表';

-- 流程实例表
CREATE TABLE IF NOT EXISTS workflow_instance (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  instance_id VARCHAR(100) NOT NULL COMMENT '流程实例ID',
  definition_id VARCHAR(100) NOT NULL COMMENT '流程定义ID',
  instance_name VARCHAR(200) NOT NULL COMMENT '流程实例名称',
  business_key VARCHAR(200) DEFAULT NULL COMMENT '业务Key',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-运行中,1-已完成,2-已取消,3-已挂起)',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  end_time DATETIME DEFAULT NULL COMMENT '结束时间',
  start_user_id BIGINT(20) NOT NULL COMMENT '发起人ID',
  start_user_name VARCHAR(50) NOT NULL COMMENT '发起人姓名',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_instance_id (instance_id, tenant_id, deleted),
  KEY idx_definition_id (definition_id),
  KEY idx_business_key (business_key),
  KEY idx_status (status),
  KEY idx_start_time (start_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例表';

-- 流程任务表
CREATE TABLE IF NOT EXISTS workflow_task (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  task_id VARCHAR(100) NOT NULL COMMENT '任务ID',
  instance_id VARCHAR(100) NOT NULL COMMENT '流程实例ID',
  task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
  task_key VARCHAR(100) NOT NULL COMMENT '任务Key',
  assignee_id BIGINT(20) DEFAULT NULL COMMENT '处理人ID',
  assignee_name VARCHAR(50) DEFAULT NULL COMMENT '处理人姓名',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-待处理,1-已处理,2-已驳回)',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  due_time DATETIME DEFAULT NULL COMMENT '到期时间',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_task_id (task_id, tenant_id, deleted),
  KEY idx_instance_id (instance_id),
  KEY idx_assignee_id (assignee_id),
  KEY idx_status (status),
  KEY idx_create_time (create_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程任务表';

-- 流程变量表
CREATE TABLE IF NOT EXISTS workflow_variable (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  instance_id VARCHAR(100) NOT NULL COMMENT '流程实例ID',
  task_id VARCHAR(100) DEFAULT NULL COMMENT '任务ID(可选)',
  variable_name VARCHAR(100) NOT NULL COMMENT '变量名称',
  variable_value TEXT COMMENT '变量值',
  variable_type VARCHAR(50) DEFAULT NULL COMMENT '变量类型',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_instance_id (instance_id),
  KEY idx_task_id (task_id),
  KEY idx_variable_name (variable_name),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程变量表';

-- 流程日志表
CREATE TABLE IF NOT EXISTS workflow_log (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  instance_id VARCHAR(100) NOT NULL COMMENT '流程实例ID',
  task_id VARCHAR(100) DEFAULT NULL COMMENT '任务ID',
  action VARCHAR(50) NOT NULL COMMENT '操作类型',
  action_user_id BIGINT(20) NOT NULL COMMENT '操作人ID',
  action_user_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
  action_comment TEXT COMMENT '操作意见',
  action_time DATETIME NOT NULL COMMENT '操作时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_instance_id (instance_id),
  KEY idx_task_id (task_id),
  KEY idx_action_time (action_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程日志表';

-- 插入演示数据
INSERT INTO workflow_definition (
  definition_id, definition_name, definition_key, version,
  bpmn_content, status, category, description, tenant_id
) VALUES 
('leave-request:1:001', '请假流程', 'leave-request', 1,
 '<?xml version="1.0" encoding="UTF-8"?><definitions>...</definitions>', 1,
 '人事', '员工请假审批流程', 1);

-- 创建用户
CREATE USER IF NOT EXISTS 'qooerp_workflow'@'%' IDENTIFIED BY 'qooerp_workflow_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_workflow.* TO 'qooerp_workflow'@'%';
FLUSH PRIVILEGES;
