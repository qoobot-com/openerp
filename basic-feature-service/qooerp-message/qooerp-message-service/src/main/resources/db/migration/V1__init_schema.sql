-- QooERP 消息服务模块数据库脚本 (PostgreSQL)

-- 消息表
CREATE TABLE message (
  id BIGSERIAL NOT NULL,
  message_no VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  type SMALLINT NOT NULL,
  priority SMALLINT DEFAULT 1,
  status SMALLINT NOT NULL DEFAULT 1,
  sender_id BIGINT,
  sender_name VARCHAR(50),
  channels VARCHAR(100),
  template_id BIGINT,
  send_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

COMMENT ON TABLE message IS '消息表';
COMMENT ON COLUMN message.id IS '主键';
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
COMMENT ON COLUMN message.create_time IS '创建时间';
COMMENT ON COLUMN message.deleted IS '删除标记';

CREATE UNIQUE INDEX uk_message_no ON message(message_no, tenant_id, deleted);
CREATE INDEX idx_tenant_id ON message(tenant_id);
CREATE INDEX idx_type ON message(type);
CREATE INDEX idx_status ON message(status);

-- 消息接收者表
CREATE TABLE message_receiver (
  id BIGSERIAL NOT NULL,
  message_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  receiver_name VARCHAR(50),
  read_status SMALLINT NOT NULL DEFAULT 0,
  read_time TIMESTAMP,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

COMMENT ON TABLE message_receiver IS '消息接收者表';
COMMENT ON COLUMN message_receiver.id IS '主键';
COMMENT ON COLUMN message_receiver.message_id IS '消息ID';
COMMENT ON COLUMN message_receiver.receiver_id IS '接收者ID';
COMMENT ON COLUMN message_receiver.receiver_name IS '接收者名称';
COMMENT ON COLUMN message_receiver.read_status IS '阅读状态（0:未读 1:已读）';
COMMENT ON COLUMN message_receiver.read_time IS '阅读时间';
COMMENT ON COLUMN message_receiver.tenant_id IS '租户ID';
COMMENT ON COLUMN message_receiver.create_time IS '创建时间';

CREATE INDEX idx_message_id ON message_receiver(message_id);
CREATE INDEX idx_receiver_id ON message_receiver(receiver_id);
CREATE INDEX idx_read_status ON message_receiver(read_status);

-- 消息模板表
CREATE TABLE message_template (
  id BIGSERIAL NOT NULL,
  template_no VARCHAR(50) NOT NULL,
  template_name VARCHAR(100) NOT NULL,
  template_type SMALLINT NOT NULL,
  title_template VARCHAR(200),
  content_template TEXT,
  variables VARCHAR(500),
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  create_by BIGINT,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by BIGINT,
  update_time TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

COMMENT ON TABLE message_template IS '消息模板表';
COMMENT ON COLUMN message_template.id IS '主键';
COMMENT ON COLUMN message_template.template_no IS '模板编号';
COMMENT ON COLUMN message_template.template_name IS '模板名称';
COMMENT ON COLUMN message_template.template_type IS '模板类型（1:系统 2:业务 3:通知）';
COMMENT ON COLUMN message_template.title_template IS '标题模板';
COMMENT ON COLUMN message_template.content_template IS '内容模板';
COMMENT ON COLUMN message_template.variables IS '模板变量';
COMMENT ON COLUMN message_template.status IS '状态（0:禁用 1:启用）';
COMMENT ON COLUMN message_template.tenant_id IS '租户ID';
COMMENT ON COLUMN message_template.create_by IS '创建人';
COMMENT ON COLUMN message_template.create_time IS '创建时间';
COMMENT ON COLUMN message_template.update_by IS '更新人';
COMMENT ON COLUMN message_template.update_time IS '更新时间';
COMMENT ON COLUMN message_template.deleted IS '删除标记';

CREATE UNIQUE INDEX uk_template_no ON message_template(template_no, tenant_id, deleted);
CREATE INDEX idx_tenant_id ON message_template(tenant_id);
CREATE INDEX idx_template_type ON message_template(template_type);

-- 消息渠道表
CREATE TABLE message_channel (
  id BIGSERIAL NOT NULL,
  channel_code VARCHAR(20) NOT NULL,
  channel_name VARCHAR(50) NOT NULL,
  channel_type VARCHAR(20) NOT NULL,
  config TEXT,
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);

COMMENT ON TABLE message_channel IS '消息渠道表';
COMMENT ON COLUMN message_channel.id IS '主键';
COMMENT ON COLUMN message_channel.channel_code IS '渠道编码';
COMMENT ON COLUMN message_channel.channel_name IS '渠道名称';
COMMENT ON COLUMN message_channel.channel_type IS '渠道类型';
COMMENT ON COLUMN message_channel.config IS '渠道配置';
COMMENT ON COLUMN message_channel.status IS '状态（0:禁用 1:启用）';
COMMENT ON COLUMN message_channel.tenant_id IS '租户ID';
COMMENT ON COLUMN message_channel.create_time IS '创建时间';
COMMENT ON COLUMN message_channel.update_time IS '更新时间';
COMMENT ON COLUMN message_channel.deleted IS '删除标记';

CREATE UNIQUE INDEX uk_channel_code ON message_channel(channel_code, tenant_id, deleted);
CREATE INDEX idx_tenant_id ON message_channel(tenant_id);

-- 消息统计表
CREATE TABLE message_statistics (
  id BIGSERIAL NOT NULL,
  stat_date DATE NOT NULL,
  stat_type VARCHAR(20) NOT NULL,
  message_type SMALLINT,
  channel_code VARCHAR(20),
  total_count BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

COMMENT ON TABLE message_statistics IS '消息统计表';
COMMENT ON COLUMN message_statistics.id IS '主键';
COMMENT ON COLUMN message_statistics.stat_date IS '统计日期';
COMMENT ON COLUMN message_statistics.stat_type IS '统计类型';
COMMENT ON COLUMN message_statistics.message_type IS '消息类型';
COMMENT ON COLUMN message_statistics.channel_code IS '渠道编码';
COMMENT ON COLUMN message_statistics.total_count IS '总数';
COMMENT ON COLUMN message_statistics.tenant_id IS '租户ID';
COMMENT ON COLUMN message_statistics.create_time IS '创建时间';

CREATE UNIQUE INDEX uk_stat ON message_statistics(stat_date, stat_type, message_type, channel_code, tenant_id);
CREATE INDEX idx_stat_date ON message_statistics(stat_date);
