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

\c qooerp_project

-- ============================================================================
-- QooERP 项目管理模块数据库脚本
-- ============================================================================

-- 创建数据库
CREATE DATABASE qooerp_project WITH ENCODING 'UTF8';

-- 项目表
DROP TABLE IF EXISTS project CASCADE;
CREATE TABLE project (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  project_no VARCHAR(50) NOT NULL,
  project_name VARCHAR(200) NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  manager_id BIGINT DEFAULT NULL,
  manager_name VARCHAR(50) DEFAULT NULL,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project IS '项目表';
COMMENT ON COLUMN project.project_no IS '项目编号';
COMMENT ON COLUMN project.project_name IS '项目名称';
COMMENT ON COLUMN project.status IS '状态(0-草稿,1-已审批,2-进行中,3-已完成,4-已关闭)';
COMMENT ON COLUMN project.start_date IS '开始日期';
COMMENT ON COLUMN project.end_date IS '结束日期';
COMMENT ON COLUMN project.manager_id IS '项目经理ID';
COMMENT ON COLUMN project.manager_name IS '项目经理姓名';
COMMENT ON COLUMN project.description IS '项目描述';
COMMENT ON COLUMN project.tenant_id IS '租户ID';

CREATE UNIQUE INDEX uk_project_no ON project(project_no, tenant_id, deleted);
CREATE INDEX idx_project_status ON project(status);
CREATE INDEX idx_project_manager_id ON project(manager_id);
CREATE INDEX idx_project_tenant_id ON project(tenant_id);

CREATE TRIGGER trg_project_update_time
  BEFORE UPDATE ON project
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 任务表
DROP TABLE IF EXISTS project_task CASCADE;
CREATE TABLE project_task (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  task_no VARCHAR(50) NOT NULL,
  project_id BIGINT NOT NULL,
  task_name VARCHAR(200) NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  assignee_id BIGINT DEFAULT NULL,
  assignee_name VARCHAR(50) DEFAULT NULL,
  priority SMALLINT NOT NULL DEFAULT 2,
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_task IS '任务表';
COMMENT ON COLUMN project_task.task_no IS '任务编号';
COMMENT ON COLUMN project_task.task_name IS '任务名称';
COMMENT ON COLUMN project_task.status IS '状态(0-待分配,1-进行中,2-已完成)';
COMMENT ON COLUMN project_task.assignee_id IS '负责人ID';
COMMENT ON COLUMN project_task.assignee_name IS '负责人姓名';
COMMENT ON COLUMN project_task.priority IS '优先级(1-低,2-中,3-高)';

CREATE UNIQUE INDEX uk_project_task_no ON project_task(task_no, tenant_id, deleted);
CREATE INDEX idx_project_task_project_id ON project_task(project_id);
CREATE INDEX idx_project_task_assignee_id ON project_task(assignee_id);
CREATE INDEX idx_project_task_status ON project_task(status);
CREATE INDEX idx_project_task_tenant_id ON project_task(tenant_id);

CREATE TRIGGER trg_project_task_update_time
  BEFORE UPDATE ON project_task
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 里程碑表
DROP TABLE IF EXISTS project_milestone CASCADE;
CREATE TABLE project_milestone (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  milestone_no VARCHAR(50) NOT NULL,
  project_id BIGINT NOT NULL,
  milestone_name VARCHAR(200) NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  target_date DATE NOT NULL,
  actual_date DATE DEFAULT NULL,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_milestone IS '里程碑表';
COMMENT ON COLUMN project_milestone.milestone_no IS '里程碑编号';
COMMENT ON COLUMN project_milestone.milestone_name IS '里程碑名称';
COMMENT ON COLUMN project_milestone.status IS '状态(0-未达成,1-已达成)';
COMMENT ON COLUMN project_milestone.target_date IS '目标日期';
COMMENT ON COLUMN project_milestone.actual_date IS '实际日期';

CREATE UNIQUE INDEX uk_project_milestone_no ON project_milestone(milestone_no, tenant_id, deleted);
CREATE INDEX idx_project_milestone_project_id ON project_milestone(project_id);
CREATE INDEX idx_project_milestone_status ON project_milestone(status);
CREATE INDEX idx_project_milestone_tenant_id ON project_milestone(tenant_id);

CREATE TRIGGER trg_project_milestone_update_time
  BEFORE UPDATE ON project_milestone
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 项目成员表
DROP TABLE IF EXISTS project_member CASCADE;
CREATE TABLE project_member (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  project_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(50) DEFAULT NULL,
  role VARCHAR(50) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_member IS '项目成员表';

CREATE INDEX idx_project_member_project_id ON project_member(project_id);
CREATE INDEX idx_project_member_user_id ON project_member(user_id);
CREATE INDEX idx_project_member_tenant_id ON project_member(tenant_id);

-- 任务依赖表
DROP TABLE IF EXISTS project_task_dependency CASCADE;
CREATE TABLE project_task_dependency (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  task_id BIGINT NOT NULL,
  dependency_task_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_task_dependency IS '任务依赖表';

CREATE INDEX idx_project_task_dependency_task_id ON project_task_dependency(task_id);
CREATE INDEX idx_project_task_dependency_dependency_task_id ON project_task_dependency(dependency_task_id);
CREATE INDEX idx_project_task_dependency_tenant_id ON project_task_dependency(tenant_id);

-- 项目评论表
DROP TABLE IF EXISTS project_comment CASCADE;
CREATE TABLE project_comment (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  entity_type VARCHAR(50) NOT NULL,
  entity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(50) DEFAULT NULL,
  content TEXT NOT NULL,
  mentioned_users TEXT DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_comment IS '项目/任务评论表';
COMMENT ON COLUMN project_comment.entity_type IS '实体类型(project/task)';
COMMENT ON COLUMN project_comment.entity_id IS '实体ID';
COMMENT ON COLUMN project_comment.mentioned_users IS '@提及的用户ID列表(逗号分隔)';

CREATE INDEX idx_project_comment_entity ON project_comment(entity_type, entity_id);
CREATE INDEX idx_project_comment_user_id ON project_comment(user_id);
CREATE INDEX idx_project_comment_tenant_id ON project_comment(tenant_id);
CREATE INDEX idx_project_comment_create_time ON project_comment(create_time);

CREATE TRIGGER trg_project_comment_update_time
  BEFORE UPDATE ON project_comment
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 项目附件表
DROP TABLE IF EXISTS project_attachment CASCADE;
CREATE TABLE project_attachment (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  project_id BIGINT NOT NULL,
  task_id BIGINT DEFAULT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_size BIGINT NOT NULL,
  file_type VARCHAR(100) DEFAULT NULL,
  upload_user_id BIGINT NOT NULL,
  upload_user_name VARCHAR(50) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_attachment IS '项目附件表';
COMMENT ON COLUMN project_attachment.file_path IS '文件存储路径';
COMMENT ON COLUMN project_attachment.file_size IS '文件大小(字节)';
COMMENT ON COLUMN project_attachment.file_type IS '文件类型(扩展名)';

CREATE INDEX idx_project_attachment_project_id ON project_attachment(project_id);
CREATE INDEX idx_project_attachment_task_id ON project_attachment(task_id);
CREATE INDEX idx_project_attachment_upload_user_id ON project_attachment(upload_user_id);
CREATE INDEX idx_project_attachment_tenant_id ON project_attachment(tenant_id);

-- 项目风险表
DROP TABLE IF EXISTS project_risk CASCADE;
CREATE TABLE project_risk (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  project_id BIGINT NOT NULL,
  risk_name VARCHAR(200) NOT NULL,
  risk_type VARCHAR(50) DEFAULT NULL,
  probability SMALLINT NOT NULL DEFAULT 1,
  impact SMALLINT NOT NULL DEFAULT 1,
  status SMALLINT NOT NULL DEFAULT 0,
  response TEXT DEFAULT NULL,
  owner_id BIGINT DEFAULT NULL,
  owner_name VARCHAR(50) DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_risk IS '项目风险表';
COMMENT ON COLUMN project_risk.risk_type IS '风险类型(技术风险/进度风险/资源风险等)';
COMMENT ON COLUMN project_risk.probability IS '发生概率(1-低,2-中,3-高)';
COMMENT ON COLUMN project_risk.impact IS '影响程度(1-低,2-中,3-高)';
COMMENT ON COLUMN project_risk.status IS '状态(0-未处理,1-处理中,2-已解决,3-已关闭)';
COMMENT ON COLUMN project_risk.response IS '应对策略';

CREATE INDEX idx_project_risk_project_id ON project_risk(project_id);
CREATE INDEX idx_project_risk_status ON project_risk(status);
CREATE INDEX idx_project_risk_tenant_id ON project_risk(tenant_id);

CREATE TRIGGER trg_project_risk_update_time
  BEFORE UPDATE ON project_risk
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 工时记录表
DROP TABLE IF EXISTS project_time_log CASCADE;
CREATE TABLE project_time_log (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  task_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  user_name VARCHAR(50) DEFAULT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  hours NUMERIC(10,2) NOT NULL,
  description TEXT DEFAULT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE project_time_log IS '工时记录表';
COMMENT ON COLUMN project_time_log.hours IS '工作时长(小时)';

CREATE INDEX idx_project_time_log_task_id ON project_time_log(task_id);
CREATE INDEX idx_project_time_log_user_id ON project_time_log(user_id);
CREATE INDEX idx_project_time_log_tenant_id ON project_time_log(tenant_id);
CREATE INDEX idx_project_time_log_start_time ON project_time_log(start_time);

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
CREATE USER qooerp_project WITH PASSWORD 'qooerp_project_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_project;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_project;
