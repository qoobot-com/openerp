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

-- ============================================================================
-- 创建数据库
-- ============================================================================
CREATE DATABASE qooerp_report WITH ENCODING 'UTF8';

\c qooerp_report

-- ============================================================================
-- QooERP 报表与分析模块数据库脚本
-- ============================================================================

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
  create_by BIGINT DEFAULT NULL,
  update_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report IS '报表表';
COMMENT ON COLUMN report.report_no IS '报表编号';
COMMENT ON COLUMN report.report_name IS '报表名称';
COMMENT ON COLUMN report.report_type IS '报表类型(1-列表,2-图表,3-混合,4-交叉,5-透视)';
COMMENT ON COLUMN report.category IS '报表分类';
COMMENT ON COLUMN report.description IS '报表描述';
COMMENT ON COLUMN report.status IS '状态(0-草稿,1-已发布,2-已归档)';
COMMENT ON COLUMN report.tenant_id IS '租户ID';
COMMENT ON COLUMN report.deleted IS '逻辑删除标识';

CREATE UNIQUE INDEX uk_report_no ON report(report_no, tenant_id, deleted);
CREATE INDEX idx_report_category ON report(category);
CREATE INDEX idx_report_status ON report(status);
CREATE INDEX idx_report_type ON report(report_type);
CREATE INDEX idx_report_tenant_id ON report(tenant_id);
CREATE INDEX idx_report_create_by ON report(create_by);

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
  datasource_type VARCHAR(20) NOT NULL,
  connection_config TEXT,
  sql_query TEXT,
  cache_config TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  update_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report_datasource IS '报表数据源表';
COMMENT ON COLUMN report_datasource.datasource_name IS '数据源名称';
COMMENT ON COLUMN report_datasource.datasource_type IS '数据源类型(mysql,postgresql,api,elastic)';
COMMENT ON COLUMN report_datasource.connection_config IS '连接配置JSON';
COMMENT ON COLUMN report_datasource.sql_query IS 'SQL查询';
COMMENT ON COLUMN report_datasource.cache_config IS '缓存配置JSON';

CREATE INDEX idx_datasource_report_id ON report_datasource(report_id);
CREATE INDEX idx_datasource_type ON report_datasource(datasource_type);
CREATE INDEX idx_datasource_tenant_id ON report_datasource(tenant_id);
CREATE INDEX idx_datasource_create_by ON report_datasource(create_by);

CREATE TRIGGER trg_datasource_update_time
  BEFORE UPDATE ON report_datasource
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 报表配置表
DROP TABLE IF EXISTS report_config CASCADE;
CREATE TABLE report_config (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  report_id BIGINT NOT NULL,
  config_name VARCHAR(100) NOT NULL,
  chart_type VARCHAR(20) DEFAULT NULL,
  chart_config TEXT,
  column_config TEXT,
  filter_config TEXT,
  sort_config TEXT,
  drill_config TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  update_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report_config IS '报表配置表';
COMMENT ON COLUMN report_config.config_name IS '配置名称';
COMMENT ON COLUMN report_config.chart_type IS '图表类型(bar,line,pie,table,scatter,gauge,funnel,radar)';
COMMENT ON COLUMN report_config.chart_config IS '图表配置JSON';
COMMENT ON COLUMN report_config.column_config IS '列配置JSON';
COMMENT ON COLUMN report_config.filter_config IS '过滤条件配置JSON';
COMMENT ON COLUMN report_config.sort_config IS '排序配置JSON';
COMMENT ON COLUMN report_config.drill_config IS '下钻配置JSON';

CREATE INDEX idx_config_report_id ON report_config(report_id);
CREATE INDEX idx_config_tenant_id ON report_config(tenant_id);
CREATE INDEX idx_config_create_by ON report_config(create_by);

CREATE TRIGGER trg_config_update_time
  BEFORE UPDATE ON report_config
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 仪表盘表
DROP TABLE IF EXISTS dashboard CASCADE;
CREATE TABLE dashboard (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  dashboard_no VARCHAR(50) NOT NULL,
  dashboard_name VARCHAR(200) NOT NULL,
  dashboard_type SMALLINT NOT NULL,
  layout_config TEXT,
  description TEXT,
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  update_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE dashboard IS '仪表盘表';
COMMENT ON COLUMN dashboard.dashboard_no IS '仪表盘编号';
COMMENT ON COLUMN dashboard.dashboard_name IS '仪表盘名称';
COMMENT ON COLUMN dashboard.dashboard_type IS '类型(1-个人,2-公共,3-系统)';
COMMENT ON COLUMN dashboard.layout_config IS '布局配置JSON';
COMMENT ON COLUMN dashboard.description IS '描述';
COMMENT ON COLUMN dashboard.status IS '状态(0-禁用,1-启用)';

CREATE UNIQUE INDEX uk_dashboard_no ON dashboard(dashboard_no, tenant_id, deleted);
CREATE INDEX idx_dashboard_type ON dashboard(dashboard_type);
CREATE INDEX idx_dashboard_status ON dashboard(status);
CREATE INDEX idx_dashboard_tenant_id ON dashboard(tenant_id);
CREATE INDEX idx_dashboard_create_by ON dashboard(create_by);

CREATE TRIGGER trg_dashboard_update_time
  BEFORE UPDATE ON dashboard
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 仪表盘项表
DROP TABLE IF EXISTS dashboard_item CASCADE;
CREATE TABLE dashboard_item (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  dashboard_id BIGINT NOT NULL,
  report_id BIGINT NOT NULL,
  item_name VARCHAR(100) NOT NULL,
  position_config TEXT,
  size_config TEXT,
  display_config TEXT,
  refresh_interval INT DEFAULT 300,
  sort_order INT DEFAULT 0,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  update_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE dashboard_item IS '仪表盘项表';
COMMENT ON COLUMN dashboard_item.dashboard_id IS '仪表盘ID';
COMMENT ON COLUMN dashboard_item.report_id IS '报表ID';
COMMENT ON COLUMN dashboard_item.item_name IS '项名称';
COMMENT ON COLUMN dashboard_item.position_config IS '位置配置JSON';
COMMENT ON COLUMN dashboard_item.size_config IS '尺寸配置JSON';
COMMENT ON COLUMN dashboard_item.display_config IS '显示配置JSON';
COMMENT ON COLUMN dashboard_item.refresh_interval IS '刷新间隔(秒)';
COMMENT ON COLUMN dashboard_item.sort_order IS '排序';

CREATE INDEX idx_item_dashboard_id ON dashboard_item(dashboard_id);
CREATE INDEX idx_item_report_id ON dashboard_item(report_id);
CREATE INDEX idx_item_tenant_id ON dashboard_item(tenant_id);
CREATE INDEX idx_item_create_by ON dashboard_item(create_by);

CREATE TRIGGER trg_item_update_time
  BEFORE UPDATE ON dashboard_item
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 报表订阅表
DROP TABLE IF EXISTS report_subscription CASCADE;
CREATE TABLE report_subscription (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  report_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  subscription_name VARCHAR(100) NOT NULL,
  schedule_config TEXT,
  delivery_config TEXT,
  filter_config TEXT,
  status SMALLINT NOT NULL DEFAULT 1,
  last_run_time TIMESTAMP,
  next_run_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  update_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report_subscription IS '报表订阅表';
COMMENT ON COLUMN report_subscription.report_id IS '报表ID';
COMMENT ON COLUMN report_subscription.user_id IS '订阅用户ID';
COMMENT ON COLUMN report_subscription.subscription_name IS '订阅名称';
COMMENT ON COLUMN report_subscription.schedule_config IS '调度配置JSON';
COMMENT ON COLUMN report_subscription.delivery_config IS '投递配置JSON';
COMMENT ON COLUMN report_subscription.filter_config IS '过滤条件JSON';
COMMENT ON COLUMN report_subscription.status IS '状态(0-暂停,1-启用)';
COMMENT ON COLUMN report_subscription.last_run_time IS '最后运行时间';
COMMENT ON COLUMN report_subscription.next_run_time IS '下次运行时间';

CREATE INDEX idx_subscription_report_id ON report_subscription(report_id);
CREATE INDEX idx_subscription_user_id ON report_subscription(user_id);
CREATE INDEX idx_subscription_status ON report_subscription(status);
CREATE INDEX idx_subscription_next_run ON report_subscription(next_run_time);
CREATE INDEX idx_subscription_tenant_id ON report_subscription(tenant_id);
CREATE INDEX idx_subscription_create_by ON report_subscription(create_by);

CREATE TRIGGER trg_subscription_update_time
  BEFORE UPDATE ON report_subscription
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 报表历史表
DROP TABLE IF EXISTS report_history CASCADE;
CREATE TABLE report_history (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  report_id BIGINT NOT NULL,
  snapshot_data TEXT,
  snapshot_config TEXT,
  snapshot_time TIMESTAMP NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE report_history IS '报表历史表';
COMMENT ON COLUMN report_history.report_id IS '报表ID';
COMMENT ON COLUMN report_history.snapshot_data IS '快照数据JSON';
COMMENT ON COLUMN report_history.snapshot_config IS '快照配置JSON';
COMMENT ON COLUMN report_history.snapshot_time IS '快照时间';

CREATE INDEX idx_history_report_id ON report_history(report_id);
CREATE INDEX idx_history_snapshot_time ON report_history(snapshot_time);
CREATE INDEX idx_history_tenant_id ON report_history(tenant_id);
CREATE INDEX idx_history_create_by ON report_history(create_by);

-- ============================================================================
-- 创建用户
-- ============================================================================
CREATE USER qooerp_report WITH PASSWORD 'qooerp_report_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_report;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_report;

-- ============================================================================
-- 初始化数据
-- ============================================================================

-- 插入示例报表分类（可选）
INSERT INTO report (report_no, report_name, report_type, category, description, status, tenant_id, create_by)
VALUES 
  ('RPT202602180001', '采购统计报表', 1, '采购', '采购订单统计报表', 1, 1, 1),
  ('RPT202602180002', '销售趋势图表', 2, '销售', '销售趋势分析图表', 1, 1, 1)
ON CONFLICT DO NOTHING;
