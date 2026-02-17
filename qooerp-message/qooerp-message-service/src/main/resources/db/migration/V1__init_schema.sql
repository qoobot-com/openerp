-- QooERP 消息服务模块数据库脚本

CREATE DATABASE IF NOT EXISTS qooerp_message
  DEFAULT CHARACTER SET utf8mb4;

USE qooerp_message;

-- 消息表
CREATE TABLE IF NOT EXISTS message (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  message_no VARCHAR(50) NOT NULL COMMENT '消息编号',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT COMMENT '内容',
  type TINYINT(4) NOT NULL COMMENT '类型（1:系统 2:业务 3:通知）',
  priority TINYINT(4) DEFAULT 1 COMMENT '优先级（1:普通 2:重要 3:紧急）',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（1:草稿 2:已发送）',
  sender_id BIGINT(20) COMMENT '发送者ID',
  sender_name VARCHAR(50) COMMENT '发送者名称',
  channels VARCHAR(100) COMMENT '发送渠道',
  template_id BIGINT(20) COMMENT '模板ID',
  send_time DATETIME COMMENT '发送时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_message_no (message_no, tenant_id, deleted),
  KEY idx_tenant_id (tenant_id),
  KEY idx_type (type),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 消息接收者表
CREATE TABLE IF NOT EXISTS message_receiver (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  message_id BIGINT(20) NOT NULL COMMENT '消息ID',
  receiver_id BIGINT(20) NOT NULL COMMENT '接收者ID',
  receiver_name VARCHAR(50) COMMENT '接收者名称',
  read_status TINYINT(4) NOT NULL DEFAULT 0 COMMENT '阅读状态（0:未读 1:已读）',
  read_time DATETIME COMMENT '阅读时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_message_id (message_id),
  KEY idx_receiver_id (receiver_id),
  KEY idx_read_status (read_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息接收者表';

-- 消息模板表
CREATE TABLE IF NOT EXISTS message_template (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  template_no VARCHAR(50) NOT NULL COMMENT '模板编号',
  template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
  template_type TINYINT(4) NOT NULL COMMENT '模板类型（1:系统 2:业务 3:通知）',
  title_template VARCHAR(200) COMMENT '标题模板',
  content_template TEXT COMMENT '内容模板',
  variables VARCHAR(500) COMMENT '模板变量',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_by BIGINT(20) COMMENT '创建人',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by BIGINT(20) COMMENT '更新人',
  update_time DATETIME COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_template_no (template_no, tenant_id, deleted),
  KEY idx_tenant_id (tenant_id),
  KEY idx_template_type (template_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- 消息渠道表
CREATE TABLE IF NOT EXISTS message_channel (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  channel_code VARCHAR(20) NOT NULL COMMENT '渠道编码',
  channel_name VARCHAR(50) NOT NULL COMMENT '渠道名称',
  channel_type VARCHAR(20) NOT NULL COMMENT '渠道类型',
  config TEXT COMMENT '渠道配置',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_channel_code (channel_code, tenant_id, deleted),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息渠道表';

-- 消息统计表
CREATE TABLE IF NOT EXISTS message_statistics (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  stat_date DATE NOT NULL COMMENT '统计日期',
  stat_type VARCHAR(20) NOT NULL COMMENT '统计类型',
  message_type TINYINT(4) COMMENT '消息类型',
  channel_code VARCHAR(20) COMMENT '渠道编码',
  total_count BIGINT(20) NOT NULL COMMENT '总数',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_stat (stat_date, stat_type, message_type, channel_code, tenant_id),
  KEY idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息统计表';
