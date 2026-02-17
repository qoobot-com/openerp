-- QooERP 任务调度模块数据库脚本

CREATE DATABASE IF NOT EXISTS qooerp_scheduler 
  DEFAULT CHARACTER SET utf8mb4;

USE qooerp_scheduler;

CREATE TABLE IF NOT EXISTS schedule_job (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  job_no VARCHAR(50) NOT NULL COMMENT '任务编号',
  job_name VARCHAR(200) NOT NULL COMMENT '任务名称',
  job_class VARCHAR(500) NOT NULL COMMENT '任务类',
  cron_expression VARCHAR(100) NOT NULL COMMENT 'Cron表达式',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_job_no (job_no, tenant_id, deleted),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务表';

CREATE TABLE IF NOT EXISTS schedule_log (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  job_id BIGINT(20) NOT NULL COMMENT '任务ID',
  execute_time DATETIME NOT NULL COMMENT '执行时间',
  execute_result TEXT COMMENT '执行结果',
  status TINYINT(4) NOT NULL COMMENT '状态',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_job_id (job_id),
  KEY idx_execute_time (execute_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志表';

CREATE USER IF NOT EXISTS 'qooerp_scheduler'@'%' IDENTIFIED BY 'qooerp_scheduler_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_scheduler.* TO 'qooerp_scheduler'@'%';
FLUSH PRIVILEGES;
