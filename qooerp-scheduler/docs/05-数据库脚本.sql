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

\c qooerp_scheduler

-- QooERP 任务调度模块数据库脚本

CREATE DATABASE qooerp_scheduler WITH ENCODING 'UTF8';



CREATE TABLE IF NOT EXISTS schedule_job (
  id BIGSERIAL NOT NULL COMMENT '主键',
  job_no VARCHAR(50) NOT NULL COMMENT '任务编号',
  job_name VARCHAR(200) NOT NULL COMMENT '任务名称',
  job_class VARCHAR(500) NOT NULL COMMENT '任务类',
  cron_expression VARCHAR(100) NOT NULL COMMENT 'Cron表达式',
  status SMALLINT NOT NULL DEFAULT 0 COMMENT '状态',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE INDEX uk_job_no (job_no, tenant_id, deleted),
  INDEX idx_tenant_id (tenant_id)
);

CREATE TABLE IF NOT EXISTS schedule_log (
  id BIGSERIAL NOT NULL COMMENT '主键',
  job_id BIGINT NOT NULL COMMENT '任务ID',
  execute_time TIMESTAMP NOT NULL COMMENT '执行时间',
  execute_result TEXT COMMENT '执行结果',
  status SMALLINT NOT NULL COMMENT '状态',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_job_id (job_id),
  INDEX idx_execute_time (execute_time)
);

CREATE USER qooerp_scheduler WITH PASSWORD 'qooerp_scheduler_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_scheduler;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_scheduler;
