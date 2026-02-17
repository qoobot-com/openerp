-- ============================================================================
-- QooERP 项目管理模块数据库脚本
-- ============================================================================

CREATE DATABASE IF NOT EXISTS qooerp_project 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE qooerp_project;

-- 项目表
CREATE TABLE IF NOT EXISTS project (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  project_no VARCHAR(50) NOT NULL COMMENT '项目编号',
  project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已审批,2-进行中,3-已完成,4-已关闭)',
  start_date DATE DEFAULT NULL COMMENT '开始日期',
  end_date DATE DEFAULT NULL COMMENT '结束日期',
  manager_id BIGINT(20) DEFAULT NULL COMMENT '项目经理ID',
  manager_name VARCHAR(50) DEFAULT NULL COMMENT '项目经理姓名',
  description TEXT COMMENT '项目描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_project_no (project_no, tenant_id, deleted),
  KEY idx_status (status),
  KEY idx_manager_id (manager_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 任务表
CREATE TABLE IF NOT EXISTS project_task (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  task_no VARCHAR(50) NOT NULL COMMENT '任务编号',
  project_id BIGINT(20) NOT NULL COMMENT '项目ID',
  task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-待分配,1-进行中,2-已完成)',
  assignee_id BIGINT(20) DEFAULT NULL COMMENT '负责人ID',
  assignee_name VARCHAR(50) DEFAULT NULL COMMENT '负责人姓名',
  priority TINYINT(4) NOT NULL DEFAULT 2 COMMENT '优先级(1-低,2-中,3-高)',
  start_date DATE DEFAULT NULL COMMENT '开始日期',
  end_date DATE DEFAULT NULL COMMENT '结束日期',
  description TEXT COMMENT '任务描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_task_no (task_no, tenant_id, deleted),
  KEY idx_project_id (project_id),
  KEY idx_assignee_id (assignee_id),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 里程碑表
CREATE TABLE IF NOT EXISTS project_milestone (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  milestone_no VARCHAR(50) NOT NULL COMMENT '里程碑编号',
  project_id BIGINT(20) NOT NULL COMMENT '项目ID',
  milestone_name VARCHAR(200) NOT NULL COMMENT '里程碑名称',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-未达成,1-已达成)',
  target_date DATE NOT NULL COMMENT '目标日期',
  actual_date DATE DEFAULT NULL COMMENT '实际日期',
  description TEXT COMMENT '里程碑描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_milestone_no (milestone_no, tenant_id, deleted),
  KEY idx_project_id (project_id),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='里程碑表';

-- 项目成员表
CREATE TABLE IF NOT EXISTS project_member (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  project_id BIGINT(20) NOT NULL COMMENT '项目ID',
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  user_name VARCHAR(50) DEFAULT NULL COMMENT '用户姓名',
  role VARCHAR(50) DEFAULT NULL COMMENT '角色',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_project_id (project_id),
  KEY idx_user_id (user_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

-- 任务依赖表
CREATE TABLE IF NOT EXISTS project_task_dependency (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  task_id BIGINT(20) NOT NULL COMMENT '任务ID',
  dependency_task_id BIGINT(20) NOT NULL COMMENT '依赖任务ID',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_task_id (task_id),
  KEY idx_dependency_task_id (dependency_task_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务依赖表';

-- 插入演示数据
INSERT INTO project (
  project_no, project_name, status, start_date, end_date, 
  manager_id, manager_name, description, tenant_id
) VALUES 
('PROJ20260217001', 'ERP系统开发项目', 2, '2026-02-01', '2026-06-30',
 1, '张三', '开发企业ERP系统', 1);

INSERT INTO project_task (
  task_no, project_id, task_name, status, assignee_id, assignee_name,
  priority, start_date, end_date, tenant_id
) VALUES 
('TASK20260217001', 1, '需求分析', 2, 2, '李四', 3, '2026-02-01', '2026-02-15', 1),
('TASK20260217002', 1, '系统设计', 2, 3, '王五', 3, '2026-02-16', '2026-03-15', 1);

-- 创建用户
CREATE USER IF NOT EXISTS 'qooerp_project'@'%' IDENTIFIED BY 'qooerp_project_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_project.* TO 'qooerp_project'@'%';
FLUSH PRIVILEGES;
