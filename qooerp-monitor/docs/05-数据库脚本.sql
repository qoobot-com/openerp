-- QooERP 监控服务模块数据库脚本 (PostgreSQL + TimescaleDB)

-- 注意: PostgreSQL 需要使用超级用户创建数据库
-- CREATE DATABASE qooerp_monitor
--   WITH OWNER = postgres
--   ENCODING = 'UTF8'
--   LC_COLLATE = 'zh_CN.UTF-8'
--   LC_CTYPE = 'zh_CN.UTF-8'
--   TEMPLATE = template0;

\c qooerp_monitor;

-- ========================================
-- 启用 TimescaleDB 扩展
-- ========================================
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- ========================================
-- TimescaleDB 超表（时序数据）
-- ========================================

-- metrics 超表（指标数据）
CREATE TABLE IF NOT EXISTS metrics (
    time TIMESTAMPTZ NOT NULL,
    metric_name TEXT NOT NULL,
    metric_type TEXT NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    tags JSONB NOT NULL,
    instance TEXT NOT NULL
);

SELECT create_hypertable('metrics', 'time', chunk_time_interval => INTERVAL '1 day');

-- 创建索引
CREATE INDEX idx_metrics_name ON metrics (metric_name, time DESC);
CREATE INDEX idx_metrics_tags ON metrics USING GIN (tags);
CREATE INDEX idx_metrics_instance ON metrics (instance, time DESC);

COMMENT ON TABLE metrics IS '指标数据超表（时序数据）';
COMMENT ON COLUMN metrics.time IS '时间戳（时区感知）';
COMMENT ON COLUMN metrics.metric_name IS '指标名称（如 jvm_memory_used_bytes）';
COMMENT ON COLUMN metrics.metric_type IS '指标类型（gauge/counter/timer/summary）';
COMMENT ON COLUMN metrics.metric_value IS '指标值';
COMMENT ON COLUMN metrics.tags IS '标签（JSONB 格式）';
COMMENT ON COLUMN metrics.instance IS '实例标识（如 qooerp-gateway:8080）';

-- traces 超表（链路追踪数据）
CREATE TABLE IF NOT EXISTS traces (
    time TIMESTAMPTZ NOT NULL,
    trace_id TEXT NOT NULL,
    span_id TEXT NOT NULL,
    parent_span_id TEXT,
    span_name TEXT NOT NULL,
    span_kind TEXT,
    duration_ms BIGINT,
    status_code TEXT,
    tags JSONB NOT NULL,
    instance TEXT NOT NULL
);

SELECT create_hypertable('traces', 'time', chunk_time_interval => INTERVAL '7 days');

-- 创建索引
CREATE INDEX idx_traces_id ON traces (trace_id, time DESC);
CREATE INDEX idx_traces_span ON traces (span_id);
CREATE INDEX idx_traces_name ON traces (span_name, time DESC);
CREATE INDEX idx_traces_tags ON traces USING GIN (tags);

COMMENT ON TABLE traces IS '链路追踪超表（时序数据）';
COMMENT ON COLUMN traces.time IS '时间戳（时区感知）';
COMMENT ON COLUMN traces.trace_id IS '追踪 ID';
COMMENT ON COLUMN traces.span_id IS '跨度 ID';
COMMENT ON COLUMN traces.parent_span_id IS '父跨度 ID';
COMMENT ON COLUMN traces.span_name IS '跨度名称（如 HTTP GET /api/users）';
COMMENT ON COLUMN traces.span_kind IS '跨度类型（如 SERVER/CLIENT/PRODUCER）';
COMMENT ON COLUMN traces.duration_ms IS '持续时间（毫秒）';
COMMENT ON COLUMN traces.status_code IS '状态码（如 OK/ERROR）';
COMMENT ON COLUMN traces.tags IS '标签（JSONB 格式）';
COMMENT ON COLUMN traces.instance IS '实例标识';

-- logs 超表（日志数据）
CREATE TABLE IF NOT EXISTS logs (
    time TIMESTAMPTZ NOT NULL,
    level TEXT NOT NULL,
    logger_name TEXT NOT NULL,
    message TEXT,
    trace_id TEXT,
    span_id TEXT,
    thread_name TEXT,
    context JSONB,
    instance TEXT NOT NULL
);

SELECT create_hypertable('logs', 'time', chunk_time_interval => INTERVAL '7 days');

-- 创建索引
CREATE INDEX idx_logs_level ON logs (level, time DESC);
CREATE INDEX idx_logs_trace ON logs (trace_id);
CREATE INDEX idx_logs_logger ON logs (logger_name, time DESC);
CREATE INDEX idx_logs_context ON logs USING GIN (context);

COMMENT ON TABLE logs IS '日志数据超表（时序数据）';
COMMENT ON COLUMN logs.time IS '时间戳（时区感知）';
COMMENT ON COLUMN logs.level IS '日志级别（DEBUG/INFO/WARN/ERROR）';
COMMENT ON COLUMN logs.logger_name IS '日志记录器名称';
COMMENT ON COLUMN logs.message IS '日志消息';
COMMENT ON COLUMN logs.trace_id IS '关联的追踪 ID';
COMMENT ON COLUMN logs.span_id IS '关联的跨度 ID';
COMMENT ON COLUMN logs.thread_name IS '线程名称';
COMMENT ON COLUMN logs.context IS '上下文信息（JSONB 格式）';
COMMENT ON COLUMN logs.instance IS '实例标识';

-- ========================================
-- 连续聚合视图（降采样）
-- ========================================

-- 1 小时聚合（metrics）
CREATE MATERIALIZED VIEW IF NOT EXISTS metrics_1h
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', time) AS time,
    metric_name,
    metric_type,
    tags->>'application' AS application,
    tags->>'env' AS env,
    AVG(metric_value) AS avg_value,
    MAX(metric_value) AS max_value,
    MIN(metric_value) AS min_value,
    COUNT(*) AS sample_count
FROM metrics
GROUP BY time_bucket('1 hour', time), metric_name, metric_type, tags->>'application', tags->>'env';

-- 添加连续聚合策略
SELECT add_continuous_aggregate_policy('metrics_1h',
    start_offset => INTERVAL '2 hours',
    end_offset => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour');

-- 1 天聚合（metrics）
CREATE MATERIALIZED VIEW IF NOT EXISTS metrics_1d
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', time) AS time,
    metric_name,
    metric_type,
    tags->>'application' AS application,
    tags->>'env' AS env,
    AVG(metric_value) AS avg_value,
    MAX(metric_value) AS max_value,
    MIN(metric_value) AS min_value,
    COUNT(*) AS sample_count
FROM metrics
GROUP BY time_bucket('1 day', time), metric_name, metric_type, tags->>'application', tags->>'env';

-- 添加连续聚合策略
SELECT add_continuous_aggregate_policy('metrics_1d',
    start_offset => INTERVAL '2 days',
    end_offset => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 day');

-- ========================================
-- 业务表（告警、服务管理等）
-- ========================================

-- 监控服务表
CREATE TABLE IF NOT EXISTS monitor_service (
  id BIGSERIAL PRIMARY KEY,
  service_name VARCHAR(100) NOT NULL,
  service_address VARCHAR(200) NOT NULL,
  service_port INTEGER NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 1,
  health_status SMALLINT NOT NULL DEFAULT 1,
  last_check_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  CONSTRAINT uk_service_name UNIQUE (service_name, tenant_id, deleted)
);

CREATE INDEX idx_monitor_service_tenant_id ON monitor_service(tenant_id);

COMMENT ON TABLE monitor_service IS '监控服务表';
COMMENT ON COLUMN monitor_service.status IS '状态(0:离线 1:在线)';
COMMENT ON COLUMN monitor_service.health_status IS '健康状态(0:异常 1:正常)';
COMMENT ON COLUMN monitor_service.last_check_time IS '最后检查时间';

-- 告警信息表
CREATE TABLE IF NOT EXISTS monitor_alert (
  id BIGSERIAL PRIMARY KEY,
  rule_id BIGINT NOT NULL,
  rule_name VARCHAR(100) NOT NULL,
  alert_level SMALLINT NOT NULL DEFAULT 1,
  alert_message TEXT NOT NULL,
  triggered_at TIMESTAMPTZ NOT NULL,
  recovered_at TIMESTAMPTZ,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  notification_sent BOOLEAN DEFAULT FALSE,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_monitor_alert_rule_id ON monitor_alert(rule_id);
CREATE INDEX idx_monitor_alert_triggered_at ON monitor_alert(triggered_at DESC);
CREATE INDEX idx_monitor_alert_status ON monitor_alert(status);
CREATE INDEX idx_monitor_alert_tenant_id ON monitor_alert(tenant_id);

COMMENT ON TABLE monitor_alert IS '告警信息表';
COMMENT ON COLUMN monitor_alert.rule_id IS '告警规则ID';
COMMENT ON COLUMN monitor_alert.rule_name IS '规则名称';
COMMENT ON COLUMN monitor_alert.alert_level IS '告警级别(0:信息 1:警告 2:严重 3:紧急)';
COMMENT ON COLUMN monitor_alert.alert_message IS '告警消息';
COMMENT ON COLUMN monitor_alert.triggered_at IS '触发时间';
COMMENT ON COLUMN monitor_alert.recovered_at IS '恢复时间';
COMMENT ON COLUMN monitor_alert.status IS '状态(ACTIVE/RECOVERED/CLOSED)';
COMMENT ON COLUMN monitor_alert.notification_sent IS '是否已发送通知';

-- 告警规则表
CREATE TABLE IF NOT EXISTS monitor_alert_rule (
  id BIGSERIAL PRIMARY KEY,
  rule_name VARCHAR(100) NOT NULL,
  rule_type VARCHAR(50) NOT NULL,
  metric_name VARCHAR(100),
  condition_operator VARCHAR(10) NOT NULL,
  threshold_value DECIMAL(20,6) NOT NULL,
  duration INTEGER,
  alert_level SMALLINT NOT NULL DEFAULT 1,
  enabled SMALLINT NOT NULL DEFAULT 1,
  notification_channels VARCHAR(200),
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_monitor_alert_rule_rule_type ON monitor_alert_rule(rule_type);
CREATE INDEX idx_monitor_alert_rule_enabled ON monitor_alert_rule(enabled);
CREATE INDEX idx_monitor_alert_rule_tenant_id ON monitor_alert_rule(tenant_id);

COMMENT ON TABLE monitor_alert_rule IS '告警规则表';
COMMENT ON COLUMN monitor_alert_rule.rule_type IS '规则类型(service/metric/business)';
COMMENT ON COLUMN monitor_alert_rule.metric_name IS '指标名称';
COMMENT ON COLUMN monitor_alert_rule.condition_operator IS '条件操作符(gt/lt/ge/le/eq)';
COMMENT ON COLUMN monitor_alert_rule.threshold_value IS '阈值';
COMMENT ON COLUMN monitor_alert_rule.duration IS '持续时间（秒）';
COMMENT ON COLUMN monitor_alert_rule.alert_level IS '告警级别(0:信息 1:警告 2:严重 3:紧急)';
COMMENT ON COLUMN monitor_alert_rule.enabled IS '是否启用(0:禁用 1:启用)';
COMMENT ON COLUMN monitor_alert_rule.notification_channels IS '通知渠道(逗号分隔)';

-- 告警通知渠道表
CREATE TABLE IF NOT EXISTS monitor_alert_channel (
  id BIGSERIAL PRIMARY KEY,
  channel_name VARCHAR(100) NOT NULL,
  channel_type VARCHAR(50) NOT NULL,
  channel_config TEXT,
  enabled SMALLINT NOT NULL DEFAULT 1,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_monitor_alert_channel_channel_type ON monitor_alert_channel(channel_type);
CREATE INDEX idx_monitor_alert_channel_enabled ON monitor_alert_channel(enabled);
CREATE INDEX idx_monitor_alert_channel_tenant_id ON monitor_alert_channel(tenant_id);

COMMENT ON TABLE monitor_alert_channel IS '告警通知渠道表';
COMMENT ON COLUMN monitor_alert_channel.channel_type IS '渠道类型(sms/email/site/webhook)';
COMMENT ON COLUMN monitor_alert_channel.channel_config IS '渠道配置(JSON格式)';
COMMENT ON COLUMN monitor_alert_channel.enabled IS '是否启用(0:禁用 1:启用)';

-- 告警处理记录表
CREATE TABLE IF NOT EXISTS monitor_alert_handle (
  id BIGSERIAL PRIMARY KEY,
  alert_id BIGINT NOT NULL,
  handler_id BIGINT NOT NULL,
  handle_action VARCHAR(50) NOT NULL,
  handle_remark TEXT,
  handle_time TIMESTAMPTZ NOT NULL,
  tenant_id BIGINT NOT NULL
);

CREATE INDEX idx_monitor_alert_handle_alert_id ON monitor_alert_handle(alert_id);
CREATE INDEX idx_monitor_alert_handle_handler_id ON monitor_alert_handle(handler_id);
CREATE INDEX idx_monitor_alert_handle_handle_time ON monitor_alert_handle(handle_time DESC);
CREATE INDEX idx_monitor_alert_handle_tenant_id ON monitor_alert_handle(tenant_id);

COMMENT ON TABLE monitor_alert_handle IS '告警处理记录表';
COMMENT ON COLUMN monitor_alert_handle.handle_action IS '处理动作(确认/关闭/忽略)';

-- ========================================
-- 数据保留策略
-- ========================================

-- 保留原始数据 7 天
SELECT add_retention_policy('metrics', INTERVAL '7 days');
SELECT add_retention_policy('traces', INTERVAL '30 days');
SELECT add_retention_policy('logs', INTERVAL '30 days');

-- 保留 1 小时聚合数据 30 天
SELECT add_retention_policy('metrics_1h', INTERVAL '30 days');

-- 保留 1 天聚合数据 1 年
SELECT add_retention_policy('metrics_1d', INTERVAL '365 days');

-- ========================================
-- 初始化数据
-- ========================================

-- 插入默认通知渠道
INSERT INTO monitor_alert_channel (channel_name, channel_type, channel_config, enabled, description, tenant_id)
VALUES
  ('站内信通知', 'site', '{}', 1, '默认站内信通知渠道', 0),
  ('邮件通知', 'email', '{"smtpHost":"localhost","smtpPort":"587"}', 0, '邮件通知渠道', 0);

-- 插入示例告警规则
INSERT INTO monitor_alert_rule (rule_name, rule_type, metric_name, condition_operator, threshold_value, duration, alert_level, enabled, description, tenant_id)
VALUES
  ('JVM内存使用率过高', 'metric', 'jvm_memory_used_bytes', 'gt', 8589934592, 300, 2, 1, 'JVM内存使用超过8GB持续5分钟', 0),
  ('API错误率过高', 'metric', 'http_server_requests_seconds', 'gt', 0.05, 300, 2, 1, 'API错误率超过5%持续5分钟', 0),
  ('服务离线告警', 'service', NULL, 'eq', 0, 60, 3, 1, '服务下线超过1分钟', 0);

-- ========================================
-- 创建视图
-- ========================================

-- 活跃告警视图
CREATE OR REPLACE VIEW active_alerts AS
SELECT
    ma.id,
    ma.rule_id,
    ma.rule_name,
    ma.alert_level,
    ma.alert_message,
    ma.triggered_at,
    ma.status,
    CASE ma.alert_level
        WHEN 0 THEN '信息'
        WHEN 1 THEN '警告'
        WHEN 2 THEN '严重'
        WHEN 3 THEN '紧急'
        ELSE '未知'
    END AS alert_level_text,
    CASE ma.status
        WHEN 'ACTIVE' THEN '活跃'
        WHEN 'RECOVERED' THEN '已恢复'
        WHEN 'CLOSED' THEN '已关闭'
        ELSE '未知'
    END AS status_text
FROM monitor_alert ma
WHERE ma.status = 'ACTIVE'
ORDER BY ma.triggered_at DESC;

-- 服务健康统计视图
CREATE OR REPLACE VIEW service_health_summary AS
SELECT
    ms.service_name,
    ms.status,
    ms.health_status,
    ms.last_check_time,
    COUNT(*) OVER () AS total_services,
    SUM(CASE WHEN ms.status = 1 AND ms.health_status = 1 THEN 1 ELSE 0 END) OVER () AS healthy_services,
    SUM(CASE WHEN ms.status = 0 OR ms.health_status = 0 THEN 1 ELSE 0 END) OVER () AS unhealthy_services
FROM monitor_service ms
WHERE ms.deleted = 0
ORDER BY ms.service_name;

-- 指标趋势统计视图（最近1小时）
CREATE OR REPLACE VIEW metrics_trend_1h AS
SELECT
    time_bucket('1 minute', time) AS time,
    metric_name,
    metric_type,
    AVG(metric_value) AS avg_value,
    MAX(metric_value) AS max_value,
    MIN(metric_value) AS min_value,
    COUNT(*) AS count
FROM metrics
WHERE time > NOW() - INTERVAL '1 hour'
GROUP BY time_bucket('1 minute', time), metric_name, metric_type
ORDER BY time DESC;

-- ========================================
-- 函数
-- ========================================

-- 创建告警函数
CREATE OR REPLACE FUNCTION create_alert(
    p_rule_id BIGINT,
    p_rule_name VARCHAR,
    p_alert_level SMALLINT,
    p_alert_message TEXT,
    p_tenant_id BIGINT
) RETURNS BIGINT AS $$
DECLARE
    v_alert_id BIGINT;
BEGIN
    INSERT INTO monitor_alert (rule_id, rule_name, alert_level, alert_message, triggered_at, tenant_id)
    VALUES (p_rule_id, p_rule_name, p_alert_level, p_alert_message, NOW(), p_tenant_id)
    RETURNING id INTO v_alert_id;

    RETURN v_alert_id;
END;
$$ LANGUAGE plpgsql;

-- 检查服务健康状态函数
CREATE OR REPLACE FUNCTION check_service_health(p_service_name VARCHAR) RETURNS TABLE(status SMALLINT) AS $$
BEGIN
    RETURN QUERY
    SELECT COALESCE(health_status, 0)
    FROM monitor_service
    WHERE service_name = p_service_name AND deleted = 0
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- 获取指标最新值函数
CREATE OR REPLACE FUNCTION get_latest_metric(p_metric_name TEXT, p_instance TEXT) RETURNS DOUBLE PRECISION AS $$
BEGIN
    RETURN (
        SELECT metric_value
        FROM metrics
        WHERE metric_name = p_metric_name AND (p_instance IS NULL OR instance = p_instance)
        ORDER BY time DESC
        LIMIT 1
    );
END;
$$ LANGUAGE plpgsql;

-- ========================================
-- 完成
-- ========================================

-- 验证 TimescaleDB 超表创建结果
SELECT hypertable_name, time_column_name, chunk_time_interval
FROM timescaledb_information.hypertables
WHERE hypertable_schema = 'public';

-- 验证连续聚合视图创建结果
SELECT view_name, materialized_only, job_id
FROM timescaledb_information.continuous_aggregates;

-- 验证数据保留策略
SELECT hypertable_name, retention_period
FROM timescaledb_information.retention_policies;
