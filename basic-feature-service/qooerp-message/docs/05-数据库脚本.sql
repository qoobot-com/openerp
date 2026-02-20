-- QooERP 消息服务模块数据库脚本 (PostgreSQL)

-- 注意: PostgreSQL 需要使用超级用户创建数据库
-- CREATE DATABASE qooerp_message
--   WITH OWNER = postgres
--   ENCODING = 'UTF8'
--   LC_COLLATE = 'zh_CN.UTF-8'
--   LC_CTYPE = 'zh_CN.UTF-8'
--   TEMPLATE = template0;

\c qooerp_message;

-- 消息表
CREATE TABLE IF NOT EXISTS message (
  id BIGSERIAL PRIMARY KEY,
  message_no VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  type SMALLINT NOT NULL COMMENT '类型（1:系统 2:业务 3:通知）',
  priority SMALLINT DEFAULT 1 COMMENT '优先级（1:普通 2:重要 3:紧急）',
  status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态（1:草稿 2:已发送）',
  sender_id BIGINT,
  sender_name VARCHAR(50),
  channels VARCHAR(100),
  template_id BIGINT,
  send_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  CONSTRAINT uk_message_no UNIQUE (message_no, tenant_id, deleted)
);

CREATE INDEX idx_message_tenant_id ON message(tenant_id);
CREATE INDEX idx_message_type ON message(type);
CREATE INDEX idx_message_status ON message(status);

COMMENT ON TABLE message IS '消息表';
COMMENT ON COLUMN message.message_no IS '消息编号';
COMMENT ON COLUMN message.title IS '标题';
COMMENT ON COLUMN message.content IS '内容';
COMMENT ON COLUMN message.type IS '类型（1:系统 2:业务 3:通知）';
COMMENT ON COLUMN message.priority IS '优先级（1:普通 2:重要 3:紧急）';
COMMENT ON COLUMN message.status IS '状态（1:草稿 2:已发送）';
COMMENT ON COLUMN message.sender_id IS '发送者ID';
COMMENT ON COLUMN message.sender_name IS '发送者名称';
COMMENT ON COLUMN message.channels IS '发送渠道';
COMMENT ON COLUMN message.template_id IS '模板ID';
COMMENT ON COLUMN message.send_time IS '发送时间';
COMMENT ON COLUMN message.tenant_id IS '租户ID';

-- 消息接收者表
CREATE TABLE IF NOT EXISTS message_receiver (
  id BIGSERIAL PRIMARY KEY,
  message_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  receiver_name VARCHAR(50),
  read_status SMALLINT NOT NULL DEFAULT 0 COMMENT '阅读状态（0:未读 1:已读）',
  read_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_message_receiver_message_id ON message_receiver(message_id);
CREATE INDEX idx_message_receiver_receiver_id ON message_receiver(receiver_id);
CREATE INDEX idx_message_receiver_read_status ON message_receiver(read_status);

COMMENT ON TABLE message_receiver IS '消息接收者表';
COMMENT ON COLUMN message_receiver.read_status IS '阅读状态（0:未读 1:已读）';
COMMENT ON COLUMN message_receiver.read_time IS '阅读时间';

-- 消息模板表
CREATE TABLE IF NOT EXISTS message_template (
  id BIGSERIAL PRIMARY KEY,
  template_no VARCHAR(50) NOT NULL,
  template_name VARCHAR(100) NOT NULL,
  template_type SMALLINT NOT NULL COMMENT '模板类型（1:系统 2:业务 3:通知）',
  title_template VARCHAR(200),
  content_template TEXT,
  variables VARCHAR(500),
  status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
  tenant_id BIGINT NOT NULL,
  create_by BIGINT,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by BIGINT,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  CONSTRAINT uk_template_no UNIQUE (template_no, tenant_id, deleted)
);

CREATE INDEX idx_message_template_tenant_id ON message_template(tenant_id);
CREATE INDEX idx_message_template_type ON message_template(template_type);

COMMENT ON TABLE message_template IS '消息模板表';
COMMENT ON COLUMN message_template.template_no IS '模板编号';
COMMENT ON COLUMN message_template.template_name IS '模板名称';
COMMENT ON COLUMN message_template.template_type IS '模板类型（1:系统 2:业务 3:通知）';
COMMENT ON COLUMN message_template.title_template IS '标题模板';
COMMENT ON COLUMN message_template.content_template IS '内容模板';
COMMENT ON COLUMN message_template.variables IS '模板变量';
COMMENT ON COLUMN message_template.status IS '状态（0:禁用 1:启用）';
COMMENT ON COLUMN message_template.create_by IS '创建人';
COMMENT ON COLUMN message_template.update_by IS '更新人';

-- 消息渠道表
CREATE TABLE IF NOT EXISTS message_channel (
  id BIGSERIAL PRIMARY KEY,
  channel_code VARCHAR(20) NOT NULL,
  channel_name VARCHAR(50) NOT NULL,
  channel_type VARCHAR(20) NOT NULL,
  config TEXT,
  status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  CONSTRAINT uk_channel_code UNIQUE (channel_code, tenant_id, deleted)
);

CREATE INDEX idx_message_channel_tenant_id ON message_channel(tenant_id);

COMMENT ON TABLE message_channel IS '消息渠道表';
COMMENT ON COLUMN message_channel.channel_code IS '渠道编码';
COMMENT ON COLUMN message_channel.channel_name IS '渠道名称';
COMMENT ON COLUMN message_channel.channel_type IS '渠道类型';
COMMENT ON COLUMN message_channel.config IS '渠道配置';
COMMENT ON COLUMN message_channel.status IS '状态（0:禁用 1:启用）';

-- 消息统计表
CREATE TABLE IF NOT EXISTS message_statistics (
  id BIGSERIAL PRIMARY KEY,
  stat_date DATE NOT NULL,
  stat_type VARCHAR(20) NOT NULL,
  message_type SMALLINT,
  channel_code VARCHAR(20),
  total_count BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_stat UNIQUE (stat_date, stat_type, message_type, channel_code, tenant_id)
);

CREATE INDEX idx_message_statistics_stat_date ON message_statistics(stat_date);

COMMENT ON TABLE message_statistics IS '消息统计表';
COMMENT ON COLUMN message_statistics.stat_date IS '统计日期';
COMMENT ON COLUMN message_statistics.stat_type IS '统计类型';
COMMENT ON COLUMN message_statistics.message_type IS '消息类型';
COMMENT ON COLUMN message_statistics.channel_code IS '渠道编码';
COMMENT ON COLUMN message_statistics.total_count IS '总数';

-- ========== 自动更新时间触发器 ==========
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.update_time = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_message_template_update_time
BEFORE UPDATE ON message_template
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_message_channel_update_time
BEFORE UPDATE ON message_channel
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- ========== 创建用户 ==========
-- 注意: PostgreSQL 需要使用超级用户创建用户
-- CREATE USER qooerp_message WITH PASSWORD 'qooerp_message_2026';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_message;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_message;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO qooerp_message;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO qooerp_message;
