-- QooERP 监控服务模块数据库迁移脚本 (PostgreSQL)

-- 监控服务表
CREATE TABLE monitor_service (
  id BIGSERIAL NOT NULL,
  service_name VARCHAR(100) NOT NULL,
  service_address VARCHAR(200) NOT NULL,
  service_port INTEGER NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 1,
  health_status SMALLINT NOT NULL DEFAULT 1,
  last_check_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_service IS '监控服务表';
COMMENT ON COLUMN monitor_service.id IS '主键';
COMMENT ON COLUMN monitor_service.service_name IS '服务名称';
COMMENT ON COLUMN monitor_service.service_address IS '服务地址';
COMMENT ON COLUMN monitor_service.service_port IS '服务端口';
COMMENT ON COLUMN monitor_service.status IS '状态(0:离线 1:在线)';
COMMENT ON COLUMN monitor_service.health_status IS '健康状态(0:异常 1:正常)';
COMMENT ON COLUMN monitor_service.last_check_time IS '最后检查时间';
COMMENT ON COLUMN monitor_service.tenant_id IS '租户ID';
COMMENT ON COLUMN monitor_service.create_time IS '创建时间';
COMMENT ON COLUMN monitor_service.update_time IS '更新时间';
COMMENT ON COLUMN monitor_service.deleted IS '删除标记';

CREATE UNIQUE INDEX uk_service_name ON monitor_service(service_name, tenant_id, deleted);
CREATE INDEX idx_tenant_id ON monitor_service(tenant_id);

-- 告警信息表
CREATE TABLE monitor_alert (
  id BIGSERIAL NOT NULL,
  service_name VARCHAR(100) NOT NULL,
  alert_type VARCHAR(50) NOT NULL,
  alert_level SMALLINT NOT NULL DEFAULT 1,
  alert_content TEXT,
  alert_time TIMESTAMP NOT NULL,
  status SMALLINT NOT NULL DEFAULT 0,
  handler_id BIGINT,
  handle_time TIMESTAMP,
  handle_remark TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_alert IS '告警信息表';
COMMENT ON COLUMN monitor_alert.id IS '主键';
COMMENT ON COLUMN monitor_alert.service_name IS '服务名称';
COMMENT ON COLUMN monitor_alert.alert_type IS '告警类型';
COMMENT ON COLUMN monitor_alert.alert_level IS '告警级别(0:信息 1:警告 2:严重 3:紧急)';
COMMENT ON COLUMN monitor_alert.alert_content IS '告警内容';
COMMENT ON COLUMN monitor_alert.alert_time IS '告警时间';
COMMENT ON COLUMN monitor_alert.status IS '状态(0:待处理 1:已处理 2:已关闭)';
COMMENT ON COLUMN monitor_alert.handler_id IS '处理人ID';
COMMENT ON COLUMN monitor_alert.handle_time IS '处理时间';
COMMENT ON COLUMN monitor_alert.handle_remark IS '处理备注';
COMMENT ON COLUMN monitor_alert.tenant_id IS '租户ID';
COMMENT ON COLUMN monitor_alert.create_time IS '创建时间';
COMMENT ON COLUMN monitor_alert.update_time IS '更新时间';

CREATE INDEX idx_service_name ON monitor_alert(service_name);
CREATE INDEX idx_alert_time ON monitor_alert(alert_time);
CREATE INDEX idx_status ON monitor_alert(status);
CREATE INDEX idx_tenant_id ON monitor_alert(tenant_id);

-- 告警规则表
CREATE TABLE monitor_alert_rule (
  id BIGSERIAL NOT NULL,
  rule_name VARCHAR(100) NOT NULL,
  rule_type VARCHAR(50) NOT NULL,
  metric_name VARCHAR(100),
  condition_operator VARCHAR(10) NOT NULL,
  threshold_value DECIMAL(20,6) NOT NULL,
  alert_level SMALLINT NOT NULL DEFAULT 1,
  enabled SMALLINT NOT NULL DEFAULT 1,
  notification_channels VARCHAR(200),
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_alert_rule IS '告警规则表';
COMMENT ON COLUMN monitor_alert_rule.id IS '主键';
COMMENT ON COLUMN monitor_alert_rule.rule_name IS '规则名称';
COMMENT ON COLUMN monitor_alert_rule.rule_type IS '规则类型(service/metric/business)';
COMMENT ON COLUMN monitor_alert_rule.metric_name IS '指标名称';
COMMENT ON COLUMN monitor_alert_rule.condition_operator IS '条件操作符(gt/lt/ge/le/eq)';
COMMENT ON COLUMN monitor_alert_rule.threshold_value IS '阈值';
COMMENT ON COLUMN monitor_alert_rule.alert_level IS '告警级别(0:信息 1:警告 2:严重 3:紧急)';
COMMENT ON COLUMN monitor_alert_rule.enabled IS '是否启用(0:禁用 1:启用)';
COMMENT ON COLUMN monitor_alert_rule.notification_channels IS '通知渠道(逗号分隔)';
COMMENT ON COLUMN monitor_alert_rule.description IS '规则描述';
COMMENT ON COLUMN monitor_alert_rule.tenant_id IS '租户ID';
COMMENT ON COLUMN monitor_alert_rule.create_time IS '创建时间';
COMMENT ON COLUMN monitor_alert_rule.update_time IS '更新时间';
COMMENT ON COLUMN monitor_alert_rule.deleted IS '删除标记';

CREATE INDEX idx_rule_type ON monitor_alert_rule(rule_type);
CREATE INDEX idx_enabled ON monitor_alert_rule(enabled);
CREATE INDEX idx_tenant_id ON monitor_alert_rule(tenant_id);

-- 告警通知渠道表
CREATE TABLE monitor_alert_channel (
  id BIGSERIAL NOT NULL,
  channel_name VARCHAR(100) NOT NULL,
  channel_type VARCHAR(50) NOT NULL,
  channel_config TEXT,
  enabled SMALLINT NOT NULL DEFAULT 1,
  description TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_alert_channel IS '告警通知渠道表';
COMMENT ON COLUMN monitor_alert_channel.id IS '主键';
COMMENT ON COLUMN monitor_alert_channel.channel_name IS '渠道名称';
COMMENT ON COLUMN monitor_alert_channel.channel_type IS '渠道类型(sms/email/site/webhook)';
COMMENT ON COLUMN monitor_alert_channel.channel_config IS '渠道配置(JSON格式)';
COMMENT ON COLUMN monitor_alert_channel.enabled IS '是否启用(0:禁用 1:启用)';
COMMENT ON COLUMN monitor_alert_channel.description IS '渠道描述';
COMMENT ON COLUMN monitor_alert_channel.tenant_id IS '租户ID';
COMMENT ON COLUMN monitor_alert_channel.create_time IS '创建时间';
COMMENT ON COLUMN monitor_alert_channel.update_time IS '更新时间';
COMMENT ON COLUMN monitor_alert_channel.deleted IS '删除标记';

CREATE INDEX idx_channel_type ON monitor_alert_channel(channel_type);
CREATE INDEX idx_enabled ON monitor_alert_channel(enabled);
CREATE INDEX idx_tenant_id ON monitor_alert_channel(tenant_id);

-- 告警处理记录表
CREATE TABLE monitor_alert_handle (
  id BIGSERIAL NOT NULL,
  alert_id BIGINT NOT NULL,
  handler_id BIGINT NOT NULL,
  handle_action VARCHAR(50) NOT NULL,
  handle_remark TEXT,
  handle_time TIMESTAMP NOT NULL,
  tenant_id BIGINT NOT NULL,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_alert_handle IS '告警处理记录表';
COMMENT ON COLUMN monitor_alert_handle.id IS '主键';
COMMENT ON COLUMN monitor_alert_handle.alert_id IS '告警ID';
COMMENT ON COLUMN monitor_alert_handle.handler_id IS '处理人ID';
COMMENT ON COLUMN monitor_alert_handle.handle_action IS '处理动作(确认/关闭/忽略)';
COMMENT ON COLUMN monitor_alert_handle.handle_remark IS '处理备注';
COMMENT ON COLUMN monitor_alert_handle.handle_time IS '处理时间';
COMMENT ON COLUMN monitor_alert_handle.tenant_id IS '租户ID';

CREATE INDEX idx_alert_id ON monitor_alert_handle(alert_id);
CREATE INDEX idx_handler_id ON monitor_alert_handle(handler_id);
CREATE INDEX idx_handle_time ON monitor_alert_handle(handle_time);
CREATE INDEX idx_tenant_id ON monitor_alert_handle(tenant_id);

-- 监控日志表
CREATE TABLE monitor_log (
  id BIGSERIAL NOT NULL,
  service_name VARCHAR(100) NOT NULL,
  log_type VARCHAR(50) NOT NULL,
  log_level VARCHAR(20) NOT NULL,
  log_content TEXT,
  log_time TIMESTAMP NOT NULL,
  ip_address VARCHAR(50),
  user_id BIGINT,
  tenant_id BIGINT NOT NULL,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_log IS '监控日志表';
COMMENT ON COLUMN monitor_log.id IS '主键';
COMMENT ON COLUMN monitor_log.service_name IS '服务名称';
COMMENT ON COLUMN monitor_log.log_type IS '日志类型(monitor/access/error)';
COMMENT ON COLUMN monitor_log.log_level IS '日志级别(DEBUG/INFO/WARN/ERROR)';
COMMENT ON COLUMN monitor_log.log_content IS '日志内容';
COMMENT ON COLUMN monitor_log.log_time IS '日志时间';
COMMENT ON COLUMN monitor_log.ip_address IS 'IP地址';
COMMENT ON COLUMN monitor_log.user_id IS '用户ID';
COMMENT ON COLUMN monitor_log.tenant_id IS '租户ID';

CREATE INDEX idx_service_name ON monitor_log(service_name);
CREATE INDEX idx_log_type ON monitor_log(log_type);
CREATE INDEX idx_log_time ON monitor_log(log_time);
CREATE INDEX idx_tenant_id ON monitor_log(tenant_id);

-- 性能指标表
CREATE TABLE monitor_metrics (
  id BIGSERIAL NOT NULL,
  service_name VARCHAR(100) NOT NULL,
  metric_type VARCHAR(50) NOT NULL,
  metric_name VARCHAR(100) NOT NULL,
  metric_value DECIMAL(20,6) NOT NULL,
  metric_unit VARCHAR(20),
  collect_time TIMESTAMP NOT NULL,
  tenant_id BIGINT NOT NULL,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_metrics IS '性能指标表';
COMMENT ON COLUMN monitor_metrics.id IS '主键';
COMMENT ON COLUMN monitor_metrics.service_name IS '服务名称';
COMMENT ON COLUMN monitor_metrics.metric_type IS '指标类型(cpu/memory/disk/network/jvm)';
COMMENT ON COLUMN monitor_metrics.metric_name IS '指标名称';
COMMENT ON COLUMN monitor_metrics.metric_value IS '指标值';
COMMENT ON COLUMN monitor_metrics.metric_unit IS '指标单位';
COMMENT ON COLUMN monitor_metrics.collect_time IS '采集时间';
COMMENT ON COLUMN monitor_metrics.tenant_id IS '租户ID';

CREATE INDEX idx_service_name ON monitor_metrics(service_name);
CREATE INDEX idx_metric_type ON monitor_metrics(metric_type);
CREATE INDEX idx_collect_time ON monitor_metrics(collect_time);
CREATE INDEX idx_tenant_id ON monitor_metrics(tenant_id);

-- 链路追踪表
CREATE TABLE monitor_trace (
  id BIGSERIAL NOT NULL,
  trace_id VARCHAR(64) NOT NULL,
  span_id VARCHAR(64) NOT NULL,
  parent_span_id VARCHAR(64),
  service_name VARCHAR(100) NOT NULL,
  operation_name VARCHAR(200) NOT NULL,
  start_time TIMESTAMP NOT NULL,
  duration BIGINT NOT NULL,
  status_code INTEGER NOT NULL,
  tags TEXT,
  logs TEXT,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

COMMENT ON TABLE monitor_trace IS '链路追踪表';
COMMENT ON COLUMN monitor_trace.id IS '主键';
COMMENT ON COLUMN monitor_trace.trace_id IS '追踪ID';
COMMENT ON COLUMN monitor_trace.span_id IS '跨度ID';
COMMENT ON COLUMN monitor_trace.parent_span_id IS '父跨度ID';
COMMENT ON COLUMN monitor_trace.service_name IS '服务名称';
COMMENT ON COLUMN monitor_trace.operation_name IS '操作名称';
COMMENT ON COLUMN monitor_trace.start_time IS '开始时间';
COMMENT ON COLUMN monitor_trace.duration IS '持续时间(毫秒)';
COMMENT ON COLUMN monitor_trace.status_code IS '状态码';
COMMENT ON COLUMN monitor_trace.tags IS '标签(JSON格式)';
COMMENT ON COLUMN monitor_trace.logs IS '日志(JSON格式)';
COMMENT ON COLUMN monitor_trace.tenant_id IS '租户ID';
COMMENT ON COLUMN monitor_trace.create_time IS '创建时间';

CREATE UNIQUE INDEX uk_span_id ON monitor_trace(span_id);
CREATE INDEX idx_trace_id ON monitor_trace(trace_id);
CREATE INDEX idx_service_name ON monitor_trace(service_name);
CREATE INDEX idx_start_time ON monitor_trace(start_time);
CREATE INDEX idx_tenant_id ON monitor_trace(tenant_id);
