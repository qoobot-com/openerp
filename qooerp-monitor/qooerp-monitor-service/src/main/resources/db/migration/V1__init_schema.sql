-- QooERP 监控服务模块数据库迁移脚本

-- 监控服务表
CREATE TABLE IF NOT EXISTS monitor_service (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
  service_address VARCHAR(200) NOT NULL COMMENT '服务地址',
  service_port INT(11) NOT NULL DEFAULT 0 COMMENT '服务端口',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态(0:离线 1:在线)',
  health_status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '健康状态(0:异常 1:正常)',
  last_check_time DATETIME DEFAULT NULL COMMENT '最后检查时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_service_name (service_name, tenant_id, deleted),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控服务表';

-- 告警信息表
CREATE TABLE IF NOT EXISTS monitor_alert (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
  alert_type VARCHAR(50) NOT NULL COMMENT '告警类型',
  alert_level TINYINT(4) NOT NULL DEFAULT 1 COMMENT '告警级别(0:信息 1:警告 2:严重 3:紧急)',
  alert_content TEXT COMMENT '告警内容',
  alert_time DATETIME NOT NULL COMMENT '告警时间',
  status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态(0:待处理 1:已处理 2:已关闭)',
  handler_id BIGINT(20) DEFAULT NULL COMMENT '处理人ID',
  handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
  handle_remark TEXT COMMENT '处理备注',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_service_name (service_name),
  KEY idx_alert_time (alert_time),
  KEY idx_status (status),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警信息表';

-- 告警规则表
CREATE TABLE IF NOT EXISTS monitor_alert_rule (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
  rule_type VARCHAR(50) NOT NULL COMMENT '规则类型(service/metric/business)',
  metric_name VARCHAR(100) DEFAULT NULL COMMENT '指标名称',
  condition_operator VARCHAR(10) NOT NULL COMMENT '条件操作符(gt/lt/ge/le/eq)',
  threshold_value DECIMAL(20,6) NOT NULL COMMENT '阈值',
  alert_level TINYINT(4) NOT NULL DEFAULT 1 COMMENT '告警级别(0:信息 1:警告 2:严重 3:紧急)',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0:禁用 1:启用)',
  notification_channels VARCHAR(200) DEFAULT NULL COMMENT '通知渠道(逗号分隔)',
  description TEXT COMMENT '规则描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_rule_type (rule_type),
  KEY idx_enabled (enabled),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- 告警通知渠道表
CREATE TABLE IF NOT EXISTS monitor_alert_channel (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  channel_name VARCHAR(100) NOT NULL COMMENT '渠道名称',
  channel_type VARCHAR(50) NOT NULL COMMENT '渠道类型(sms/email/site/webhook)',
  channel_config TEXT COMMENT '渠道配置(JSON格式)',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0:禁用 1:启用)',
  description TEXT COMMENT '渠道描述',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_channel_type (channel_type),
  KEY idx_enabled (enabled),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警通知渠道表';

-- 告警处理记录表
CREATE TABLE IF NOT EXISTS monitor_alert_handle (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  alert_id BIGINT(20) NOT NULL COMMENT '告警ID',
  handler_id BIGINT(20) NOT NULL COMMENT '处理人ID',
  handle_action VARCHAR(50) NOT NULL COMMENT '处理动作(确认/关闭/忽略)',
  handle_remark TEXT COMMENT '处理备注',
  handle_time DATETIME NOT NULL COMMENT '处理时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (id),
  KEY idx_alert_id (alert_id),
  KEY idx_handler_id (handler_id),
  KEY idx_handle_time (handle_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警处理记录表';

-- 监控日志表
CREATE TABLE IF NOT EXISTS monitor_log (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
  log_type VARCHAR(50) NOT NULL COMMENT '日志类型(monitor/access/error)',
  log_level VARCHAR(20) NOT NULL COMMENT '日志级别(DEBUG/INFO/WARN/ERROR)',
  log_content TEXT COMMENT '日志内容',
  log_time DATETIME NOT NULL COMMENT '日志时间',
  ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  user_id BIGINT(20) DEFAULT NULL COMMENT '用户ID',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (id),
  KEY idx_service_name (service_name),
  KEY idx_log_type (log_type),
  KEY idx_log_time (log_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监控日志表';

-- 性能指标表
CREATE TABLE IF NOT EXISTS monitor_metrics (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
  metric_type VARCHAR(50) NOT NULL COMMENT '指标类型(cpu/memory/disk/network/jvm)',
  metric_name VARCHAR(100) NOT NULL COMMENT '指标名称',
  metric_value DECIMAL(20,6) NOT NULL COMMENT '指标值',
  metric_unit VARCHAR(20) DEFAULT NULL COMMENT '指标单位',
  collect_time DATETIME NOT NULL COMMENT '采集时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (id),
  KEY idx_service_name (service_name),
  KEY idx_metric_type (metric_type),
  KEY idx_collect_time (collect_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='性能指标表';

-- 链路追踪表
CREATE TABLE IF NOT EXISTS monitor_trace (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  trace_id VARCHAR(64) NOT NULL COMMENT '追踪ID',
  span_id VARCHAR(64) NOT NULL COMMENT '跨度ID',
  parent_span_id VARCHAR(64) DEFAULT NULL COMMENT '父跨度ID',
  service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
  operation_name VARCHAR(200) NOT NULL COMMENT '操作名称',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  duration BIGINT(20) NOT NULL COMMENT '持续时间(毫秒)',
  status_code INT(11) NOT NULL COMMENT '状态码',
  tags TEXT COMMENT '标签(JSON格式)',
  logs TEXT COMMENT '日志(JSON格式)',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_span_id (span_id),
  KEY idx_trace_id (trace_id),
  KEY idx_service_name (service_name),
  KEY idx_start_time (start_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链路追踪表';
