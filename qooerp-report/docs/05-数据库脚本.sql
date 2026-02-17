-- ============================================================================
-- QooERP 报表与分析模块数据库脚本
-- ============================================================================

CREATE DATABASE IF NOT EXISTS qooerp_report 
  DEFAULT CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE qooerp_report;

-- 报表表
CREATE TABLE IF NOT EXISTS report (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  report_no VARCHAR(50) NOT NULL COMMENT '报表编号',
  report_name VARCHAR(200) NOT NULL COMMENT '报表名称',
  report_type TINYINT(4) NOT NULL COMMENT '报表类型(1-列表,2-图表,3-混合)',
  category VARCHAR(100) DEFAULT NULL COMMENT '报表分类',
  description TEXT COMMENT '报表描述',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0-草稿,1-已发布)',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  update_by VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_report_no (report_no, tenant_id, deleted),
  KEY idx_category (category),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表表';

-- 报表数据源表
CREATE TABLE IF NOT EXISTS report_datasource (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  report_id BIGINT(20) NOT NULL COMMENT '报表ID',
  datasource_name VARCHAR(100) NOT NULL COMMENT '数据源名称',
  datasource_type VARCHAR(50) NOT NULL COMMENT '数据源类型',
  connection_config TEXT COMMENT '连接配置',
  sql_query TEXT COMMENT 'SQL查询',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_report_id (report_id),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表数据源表';

-- 创建用户
CREATE USER IF NOT EXISTS 'qooerp_report'@'%' IDENTIFIED BY 'qooerp_report_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_report.* TO 'qooerp_report'@'%';
FLUSH PRIVILEGES;
