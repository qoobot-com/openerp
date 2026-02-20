-- QooERP 通知服务数据库初始化脚本
-- Version: V1__init_schema.sql

-- 创建枚举类型
CREATE TYPE notify_status_enum AS ENUM ('PENDING', 'SENDING', 'SUCCESS', 'FAILED');
CREATE TYPE notify_type_enum AS ENUM ('email', 'sms', 'push', 'internal', 'webhook');
CREATE TYPE template_type_enum AS ENUM ('email', 'sms', 'push', 'internal', 'webhook');

-- 通知记录表
CREATE TABLE notify_record (
  id BIGSERIAL PRIMARY KEY,
  notify_no VARCHAR(50) NOT NULL,
  type notify_type_enum NOT NULL,
  receiver VARCHAR(500) NOT NULL,
  title VARCHAR(500),
  content TEXT,
  template_id BIGINT,
  status notify_status_enum NOT NULL DEFAULT 'PENDING',
  error_msg VARCHAR(1000),
  retry_count INTEGER NOT NULL DEFAULT 0,
  retry_time TIMESTAMP,
  sent_time TIMESTAMP,
  extra_params JSONB,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notify_record_type ON notify_record(type);
CREATE INDEX idx_notify_record_status ON notify_record(status);
CREATE INDEX idx_notify_record_tenant_id ON notify_record(tenant_id);
CREATE INDEX idx_notify_record_create_time ON notify_record(create_time);
CREATE INDEX idx_notify_record_template_id ON notify_record(template_id);

COMMENT ON TABLE notify_record IS '通知记录表';
COMMENT ON COLUMN notify_record.notify_no IS '通知编号';
COMMENT ON COLUMN notify_record.type IS '通知类型';
COMMENT ON COLUMN notify_record.receiver IS '接收者';
COMMENT ON COLUMN notify_record.title IS '标题';
COMMENT ON COLUMN notify_record.content IS '内容';
COMMENT ON COLUMN notify_record.template_id IS '模板ID';
COMMENT ON COLUMN notify_record.status IS '状态';
COMMENT ON COLUMN notify_record.error_msg IS '错误信息';
COMMENT ON COLUMN notify_record.retry_count IS '重试次数';
COMMENT ON COLUMN notify_record.retry_time IS '下次重试时间';
COMMENT ON COLUMN notify_record.sent_time IS '发送时间';
COMMENT ON COLUMN notify_record.extra_params IS '扩展参数';

-- 通知模板表
CREATE TABLE notify_template (
  id BIGSERIAL PRIMARY KEY,
  template_code VARCHAR(50) NOT NULL,
  template_name VARCHAR(200) NOT NULL,
  category VARCHAR(50),
  type template_type_enum NOT NULL,
  subject VARCHAR(500),
  content TEXT NOT NULL,
  variables JSONB,
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notify_template_type ON notify_template(type);
CREATE INDEX idx_notify_template_category ON notify_template(category);
CREATE INDEX idx_notify_template_status ON notify_template(status);
CREATE INDEX idx_notify_template_tenant_id ON notify_template(tenant_id);

COMMENT ON TABLE notify_template IS '通知模板表';
COMMENT ON COLUMN notify_template.template_code IS '模板编码';
COMMENT ON COLUMN notify_template.template_name IS '模板名称';
COMMENT ON COLUMN notify_template.category IS '分类';
COMMENT ON COLUMN notify_template.type IS '类型';
COMMENT ON COLUMN notify_template.subject IS '邮件主题';
COMMENT ON COLUMN notify_template.content IS '内容(支持模板变量)';
COMMENT ON COLUMN notify_template.variables IS '变量定义';
COMMENT ON COLUMN notify_template.status IS '状态(0:禁用,1:启用)';

-- 用户订阅表
CREATE TABLE notify_subscription (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  channel_type VARCHAR(50) NOT NULL,
  receiver VARCHAR(500),
  subscribed SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notify_subscription_user_id ON notify_subscription(user_id);
CREATE INDEX idx_notify_subscription_tenant_id ON notify_subscription(tenant_id);
CREATE INDEX idx_notify_subscription_channel_type ON notify_subscription(channel_type);

COMMENT ON TABLE notify_subscription IS '用户订阅表';
COMMENT ON COLUMN notify_subscription.user_id IS '用户ID';
COMMENT ON COLUMN notify_subscription.channel_type IS '渠道类型';
COMMENT ON COLUMN notify_subscription.receiver IS '接收者';
COMMENT ON COLUMN notify_subscription.subscribed IS '是否订阅(0:否,1:是)';

-- 通知配置表
CREATE TABLE notify_config (
  id BIGSERIAL PRIMARY KEY,
  config_key VARCHAR(100) NOT NULL,
  config_value TEXT,
  description VARCHAR(500),
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notify_config_tenant_id ON notify_config(tenant_id);

COMMENT ON TABLE notify_config IS '通知配置表';
COMMENT ON COLUMN notify_config.config_key IS '配置键';
COMMENT ON COLUMN notify_config.config_value IS '配置值';
COMMENT ON COLUMN notify_config.description IS '说明';

-- 黑名单表
CREATE TABLE notify_blacklist (
  id BIGSERIAL PRIMARY KEY,
  receiver VARCHAR(500) NOT NULL,
  channel_type VARCHAR(50),
  reason VARCHAR(500),
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notify_blacklist_tenant_id ON notify_blacklist(tenant_id);
CREATE INDEX idx_notify_blacklist_channel_type ON notify_blacklist(channel_type);

COMMENT ON TABLE notify_blacklist IS '黑名单表';
COMMENT ON COLUMN notify_blacklist.receiver IS '接收者';
COMMENT ON COLUMN notify_blacklist.channel_type IS '渠道类型(为空表示全部)';
COMMENT ON COLUMN notify_blacklist.reason IS '拉黑原因';

-- 免打扰配置表
CREATE TABLE notify_dnd (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  channel_type VARCHAR(50),
  start_time TIME,
  end_time TIME,
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notify_dnd_user_id ON notify_dnd(user_id);
CREATE INDEX idx_notify_dnd_tenant_id ON notify_dnd(tenant_id);
CREATE INDEX idx_notify_dnd_channel_type ON notify_dnd(channel_type);

COMMENT ON TABLE notify_dnd IS '免打扰配置表';
COMMENT ON COLUMN notify_dnd.user_id IS '用户ID';
COMMENT ON COLUMN notify_dnd.channel_type IS '渠道类型(为空表示全部)';
COMMENT ON COLUMN notify_dnd.start_time IS '开始时间';
COMMENT ON COLUMN notify_dnd.end_time IS '结束时间';
COMMENT ON COLUMN notify_dnd.status IS '状态(0:关闭,1:开启)';

-- 更新时间触发器函数
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为所有表创建更新时间触发器
CREATE TRIGGER trigger_notify_record_update_time
BEFORE UPDATE ON notify_record
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_notify_template_update_time
BEFORE UPDATE ON notify_template
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_notify_subscription_update_time
BEFORE UPDATE ON notify_subscription
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_notify_config_update_time
BEFORE UPDATE ON notify_config
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_notify_blacklist_update_time
BEFORE UPDATE ON notify_blacklist
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_notify_dnd_update_time
BEFORE UPDATE ON notify_dnd
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- 初始化配置数据
INSERT INTO notify_config (config_key, config_value, description, tenant_id) VALUES
('email.enabled', 'true', '邮件通知开关', 0),
('sms.enabled', 'true', '短信通知开关', 0),
('push.enabled', 'true', '推送通知开关', 0),
('internal.enabled', 'true', '站内通知开关', 0),
('webhook.enabled', 'true', 'Webhook通知开关', 0),
('retry.enabled', 'true', '重试开关', 0),
('retry.maxTimes', '3', '最大重试次数', 0),
('retry.interval', '60000', '重试间隔(毫秒)', 0),
('rateLimit.enabled', 'true', '限流开关', 0),
('rateLimit.qps', '100', '每秒请求数', 0);

-- 初始化模板数据
INSERT INTO notify_template (template_code, template_name, category, type, subject, content, status, tenant_id) VALUES
('WELCOME_EMAIL', '欢迎邮件', 'account', 'email', '欢迎加入QooERP', '亲爱的 ${userName}，<br><br>欢迎加入QooERP！', 1, 0),
('VERIFY_CODE_SMS', '验证码短信', 'security', 'sms', NULL, '您的验证码是: ${code}，5分钟内有效。', 1, 0),
('SYSTEM_NOTICE', '系统通知', 'system', 'internal', '${title}', '${content}', 1, 0);
