-- QooERP AI智能服务模块数据库脚本

CREATE DATABASE IF NOT EXISTS qooerp_ai 
  DEFAULT CHARACTER SET utf8mb4;

USE qooerp_ai;

CREATE TABLE IF NOT EXISTS ai_model (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
  model_type VARCHAR(50) NOT NULL COMMENT '模型类型',
  version VARCHAR(50) NOT NULL COMMENT '版本',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  KEY idx_model_type (model_type),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型表';

CREATE TABLE IF NOT EXISTS ai_recommendation (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  recommendation_type VARCHAR(50) NOT NULL COMMENT '推荐类型',
  recommendation_data TEXT COMMENT '推荐数据',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_recommendation_type (recommendation_type),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐记录表';

CREATE USER IF NOT EXISTS 'qooerp_ai'@'%' IDENTIFIED BY 'qooerp_ai_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_ai.* TO 'qooerp_ai'@'%';
FLUSH PRIVILEGES;
