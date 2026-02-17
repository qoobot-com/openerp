-- QooERP 通知服务模块数据库脚本

CREATE DATABASE IF NOT EXISTS qooerp_notify 
  DEFAULT CHARACTER SET utf8mb4;

USE qooerp_notify;

CREATE TABLE IF NOT EXISTS notify_record (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  notify_no VARCHAR(50) NOT NULL COMMENT '通知编号',
  type VARCHAR(50) NOT NULL COMMENT '通知类型',
  receiver VARCHAR(200) NOT NULL COMMENT '接收者',
  content TEXT COMMENT '内容',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_notify_no (notify_no, tenant_id, deleted),
  KEY idx_type (type),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

CREATE TABLE IF NOT EXISTS notify_template (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
  template_name VARCHAR(200) NOT NULL COMMENT '模板名称',
  content TEXT COMMENT '内容',
  type TINYINT(4) NOT NULL COMMENT '类型',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_template_code (template_code, tenant_id, deleted),
  KEY idx_type (type),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知模板表';

CREATE USER IF NOT EXISTS 'qooerp_notify'@'%' IDENTIFIED BY 'qooerp_notify_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_notify.* TO 'qooerp_notify'@'%';
FLUSH PRIVILEGES;
