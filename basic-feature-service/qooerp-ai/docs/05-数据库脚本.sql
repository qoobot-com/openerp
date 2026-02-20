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

\c qooerp_ai

-- QooERP AI智能服务模块数据库脚本

CREATE DATABASE qooerp_ai WITH ENCODING 'UTF8';



CREATE TABLE IF NOT EXISTS ai_model (
  id BIGSERIAL NOT NULL COMMENT '主键',
  model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
  model_type VARCHAR(50) NOT NULL COMMENT '模型类型',
  version VARCHAR(50) NOT NULL COMMENT '版本',
  status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态',
  tenant_id BIGINT NOT NULL COMMENT '租户ID',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  INDEX idx_model_type (model_type),
  INDEX idx_tenant_id (tenant_id)
);

CREATE TABLE IF NOT EXISTS ai_recommendation (
  id BIGSERIAL NOT NULL COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  recommendation_type VARCHAR(50) NOT NULL COMMENT '推荐类型',
  recommendation_data TEXT COMMENT '推荐数据',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_recommendation_type (recommendation_type),
  INDEX idx_create_time (create_time)
);

CREATE USER qooerp_ai WITH PASSWORD 'qooerp_ai_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_ai;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_ai;
