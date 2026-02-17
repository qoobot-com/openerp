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

\c qooerp_file

-- 创建数据库
CREATE DATABASE qooerp_file WITH ENCODING 'UTF8';

-- 文件信息表
DROP TABLE IF EXISTS file_info CASCADE;
CREATE TABLE file_info (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  file_no VARCHAR(50) NOT NULL,
  file_name VARCHAR(200) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_size BIGINT NOT NULL,
  file_type VARCHAR(100) DEFAULT NULL,
  file_extension VARCHAR(20) DEFAULT NULL,
  file_md5 VARCHAR(64) DEFAULT NULL,
  storage_type VARCHAR(20) DEFAULT 'minio',
  folder_id BIGINT DEFAULT NULL,
  status SMALLINT NOT NULL DEFAULT 1,
  download_count INTEGER NOT NULL DEFAULT 0,
  tenant_id BIGINT NOT NULL,
  creator_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater_id BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE file_info IS '文件信息表';
COMMENT ON COLUMN file_info.file_no IS '文件编号';
COMMENT ON COLUMN file_info.file_name IS '文件名称';
COMMENT ON COLUMN file_info.file_path IS '文件路径';
COMMENT ON COLUMN file_info.file_size IS '文件大小（字节）';
COMMENT ON COLUMN file_info.file_type IS '文件类型（MIME）';
COMMENT ON COLUMN file_info.file_extension IS '文件扩展名';
COMMENT ON COLUMN file_info.file_md5 IS '文件MD5';
COMMENT ON COLUMN file_info.storage_type IS '存储类型（minio/oss/local）';
COMMENT ON COLUMN file_info.folder_id IS '所属文件夹ID';
COMMENT ON COLUMN file_info.status IS '状态（1-正常 2-删除中 3-已删除）';
COMMENT ON COLUMN file_info.download_count IS '下载次数';

CREATE UNIQUE INDEX uk_file_info_no ON file_info(file_no, tenant_id, deleted);
CREATE INDEX idx_file_info_folder_id ON file_info(folder_id);
CREATE INDEX idx_file_info_md5 ON file_info(file_md5);
CREATE INDEX idx_file_info_tenant_id ON file_info(tenant_id);
CREATE INDEX idx_file_info_creator_id ON file_info(creator_id);
CREATE INDEX idx_file_info_create_time ON file_info(create_time);

CREATE TRIGGER trg_file_info_update_time
  BEFORE UPDATE ON file_info
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 文件夹表
DROP TABLE IF EXISTS file_folder CASCADE;
CREATE TABLE file_folder (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  folder_no VARCHAR(50) NOT NULL,
  folder_name VARCHAR(100) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  folder_path VARCHAR(500) DEFAULT NULL,
  folder_level INTEGER NOT NULL DEFAULT 1,
  sort INTEGER NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  creator_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater_id BIGINT DEFAULT NULL,
  update_time TIMESTAMP DEFAULT NULL,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE file_folder IS '文件夹表';
COMMENT ON COLUMN file_folder.folder_no IS '文件夹编号';
COMMENT ON COLUMN file_folder.folder_name IS '文件夹名称';
COMMENT ON COLUMN file_folder.parent_id IS '父文件夹ID（0表示根目录）';
COMMENT ON COLUMN file_folder.folder_path IS '文件夹路径';
COMMENT ON COLUMN file_folder.folder_level IS '层级';
COMMENT ON COLUMN file_folder.status IS '状态（1-正常 2-已删除）';

CREATE UNIQUE INDEX uk_file_folder_no ON file_folder(folder_no, tenant_id, deleted);
CREATE INDEX idx_file_folder_parent_id ON file_folder(parent_id);
CREATE INDEX idx_file_folder_tenant_id ON file_folder(tenant_id);
CREATE INDEX idx_file_folder_creator_id ON file_folder(creator_id);

CREATE TRIGGER trg_file_folder_update_time
  BEFORE UPDATE ON file_folder
  FOR EACH ROW
  EXECUTE FUNCTION update_modified_time();

-- 文件分享表
DROP TABLE IF EXISTS file_share CASCADE;
CREATE TABLE file_share (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  file_id BIGINT NOT NULL,
  share_code VARCHAR(50) NOT NULL,
  share_password VARCHAR(100) DEFAULT NULL,
  share_name VARCHAR(200) DEFAULT NULL,
  share_desc VARCHAR(500) DEFAULT NULL,
  expire_time TIMESTAMP DEFAULT NULL,
  visit_count INTEGER NOT NULL DEFAULT 0,
  download_count INTEGER NOT NULL DEFAULT 0,
  status SMALLINT NOT NULL DEFAULT 1,
  tenant_id BIGINT NOT NULL,
  creator_id BIGINT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE file_share IS '文件分享表';
COMMENT ON COLUMN file_share.share_code IS '分享码';
COMMENT ON COLUMN file_share.share_password IS '分享密码（加密）';
COMMENT ON COLUMN file_share.share_name IS '分享标题';
COMMENT ON COLUMN file_share.share_desc IS '分享描述';
COMMENT ON COLUMN file_share.expire_time IS '过期时间';
COMMENT ON COLUMN file_share.status IS '状态（1-有效 2-已取消）';

CREATE UNIQUE INDEX uk_file_share_code ON file_share(share_code);
CREATE INDEX idx_file_share_file_id ON file_share(file_id);
CREATE INDEX idx_file_share_tenant_id ON file_share(tenant_id);
CREATE INDEX idx_file_share_creator_id ON file_share(creator_id);
CREATE INDEX idx_file_share_expire_time ON file_share(expire_time);

-- 文件操作日志表
DROP TABLE IF EXISTS file_log CASCADE;
CREATE TABLE file_log (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  file_id BIGINT DEFAULT NULL,
  operation VARCHAR(50) NOT NULL,
  operation_desc VARCHAR(500) DEFAULT NULL,
  operator_id BIGINT NOT NULL,
  operator_name VARCHAR(100) DEFAULT NULL,
  operator_ip VARCHAR(50) DEFAULT NULL,
  user_agent VARCHAR(500) DEFAULT NULL,
  operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tenant_id BIGINT NOT NULL
);

COMMENT ON TABLE file_log IS '文件操作日志表';
COMMENT ON COLUMN file_log.operation IS '操作类型（upload/download/delete/rename/move/share）';

CREATE INDEX idx_file_log_file_id ON file_log(file_id);
CREATE INDEX idx_file_log_operator_id ON file_log(operator_id);
CREATE INDEX idx_file_log_operation_time ON file_log(operation_time);
CREATE INDEX idx_file_log_tenant_id ON file_log(tenant_id);

-- 存储配额表
DROP TABLE IF EXISTS file_quota CASCADE;
CREATE TABLE file_quota (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  total_quota BIGINT NOT NULL DEFAULT 10737418240,
  used_quota BIGINT NOT NULL DEFAULT 0,
  file_count INTEGER NOT NULL DEFAULT 0,
  folder_count INTEGER NOT NULL DEFAULT 0,
  last_calculate_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE file_quota IS '存储配额表';
COMMENT ON COLUMN file_quota.total_quota IS '总配额（默认10GB）';
COMMENT ON COLUMN file_quota.used_quota IS '已使用（字节）';

CREATE UNIQUE INDEX uk_file_quota_tenant_id ON file_quota(tenant_id);

-- 初始化数据
INSERT INTO file_quota (tenant_id, total_quota, used_quota, file_count, folder_count)
VALUES (1, 107374182400, 0, 0, 0)
ON CONFLICT (tenant_id) DO NOTHING;

-- 创建用户和授权
CREATE USER qooerp_file WITH PASSWORD 'qooerp_file_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO qooerp_file;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO qooerp_file;
