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

\c qooerp_report

-- ============================================================================
-- QooERP 报表与分析模块数据库脚本
-- ============================================================================

-- 创建数据库
CREATE DATABASE qooerp_report WITH ENCODING 'UTF8';

-- 报表表
DROP TABLE IF EXISTS report CASCADE;
CREATE TABLE report (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  report_no VARCHAR(50) NOT NULL,
  report_name VARCHAR(200) NOT NULL,
  report_type SMALLINT NOT NULL,
  category VARCHAR(100) DEFAULT NULL,
  description TEXT,
  status SMALLINT NOT NULL DEFAULT 0,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by VARCHAR(50) DEFAULT NULL,
  update_by VARCHAR(50) DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report IS '报表表';
COMMENT ON COLUMN report.report_no IS '报表编号';
COMMENT ON COLUMN report.report_name IS '报表名称';
COMMENT ON COLUMN report.report_type IS '报表类型(1-列表,2-图表,3-混合)';
COMMENT ON COLUMN report.category IS '报表分类';
COMMENT ON COLUMN report.description IS '报表描述';
COMMENT ON COLUMN report.status IS '状态(0-草稿,1-已发布)';

CREATE UNIQUE INDEX uk_report_no ON report(report_no, tenant_id, deleted);
CREATE INDEX idx_report_category ON report(category);
CREATE INDEX idx_report_status ON report(status);
CREATE INDEX idx_report_tenant_id ON report(tenant_id);

CREATE TRIGGER trg_report_update_time
  BEFORE UPDATE ON report
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 报表数据源表
DROP TABLE IF EXISTS report_datasource CASCADE;
CREATE TABLE report_datasource (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  report_id BIGINT NOT NULL,
  datasource_name VARCHAR(100) NOT NULL,
  datasource_type VARCHAR(50) NOT NULL,
  connection_config TEXT,
  sql_query TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report_datasource IS '报表数据源表';
COMMENT ON COLUMN report_datasource.datasource_name IS '数据源名称';
COMMENT ON COLUMN report_datasource.datasource_type IS '数据源类型';
COMMENT ON COLUMN report_datasource.connection_config IS '连接配置';
COMMENT ON COLUMN report_datasource.sql_query IS 'SQL查询';

CREATE INDEX idx_report_datasource_report_id ON report_datasource(report_id);
CREATE INDEX idx_report_datasource_tenant_id ON report_datasource(tenant_id);

-- 创建用户
CREATE USER qooerp_report WITH PASSWORD 'qooerp_report_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_report;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_report;
