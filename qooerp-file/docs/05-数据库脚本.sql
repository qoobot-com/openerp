-- QooERP 文件服务模块数据库脚本

CREATE DATABASE IF NOT EXISTS qooerp_file
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE qooerp_file;

-- 文件信息表
CREATE TABLE IF NOT EXISTS file_info (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  file_no VARCHAR(50) NOT NULL COMMENT '文件编号',
  file_name VARCHAR(200) NOT NULL COMMENT '文件名称',
  file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
  file_size BIGINT(20) NOT NULL COMMENT '文件大小（字节）',
  file_type VARCHAR(100) DEFAULT NULL COMMENT '文件类型（MIME）',
  file_extension VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
  file_md5 VARCHAR(64) DEFAULT NULL COMMENT '文件MD5',
  storage_type VARCHAR(20) DEFAULT 'minio' COMMENT '存储类型（minio/oss/local）',
  folder_id BIGINT(20) DEFAULT NULL COMMENT '所属文件夹ID',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（1-正常 2-删除中 3-已删除）',
  download_count INT(11) NOT NULL DEFAULT 0 COMMENT '下载次数',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  creator_id BIGINT(20) NOT NULL COMMENT '创建人ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updater_id BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
  update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_file_no (file_no, tenant_id, deleted),
  KEY idx_folder_id (folder_id),
  KEY idx_file_md5 (file_md5),
  KEY idx_tenant_id (tenant_id),
  KEY idx_creator_id (creator_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 文件夹表
CREATE TABLE IF NOT EXISTS file_folder (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  folder_no VARCHAR(50) NOT NULL COMMENT '文件夹编号',
  folder_name VARCHAR(100) NOT NULL COMMENT '文件夹名称',
  parent_id BIGINT(20) DEFAULT 0 COMMENT '父文件夹ID（0表示根目录）',
  folder_path VARCHAR(500) DEFAULT NULL COMMENT '文件夹路径',
  folder_level INT(11) NOT NULL DEFAULT 1 COMMENT '层级',
  sort INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（1-正常 2-已删除）',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  creator_id BIGINT(20) NOT NULL COMMENT '创建人ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updater_id BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
  update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_folder_no (folder_no, tenant_id, deleted),
  KEY idx_parent_id (parent_id),
  KEY idx_tenant_id (tenant_id),
  KEY idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件夹表';

-- 文件分享表
CREATE TABLE IF NOT EXISTS file_share (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  file_id BIGINT(20) NOT NULL COMMENT '文件ID',
  share_code VARCHAR(50) NOT NULL COMMENT '分享码',
  share_password VARCHAR(100) DEFAULT NULL COMMENT '分享密码（加密）',
  share_name VARCHAR(200) DEFAULT NULL COMMENT '分享标题',
  share_desc VARCHAR(500) DEFAULT NULL COMMENT '分享描述',
  expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
  visit_count INT(11) NOT NULL DEFAULT 0 COMMENT '访问次数',
  download_count INT(11) NOT NULL DEFAULT 0 COMMENT '下载次数',
  status TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（1-有效 2-已取消）',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  creator_id BIGINT(20) NOT NULL COMMENT '创建人ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (id),
  UNIQUE KEY uk_share_code (share_code),
  KEY idx_file_id (file_id),
  KEY idx_tenant_id (tenant_id),
  KEY idx_creator_id (creator_id),
  KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分享表';

-- 文件操作日志表
CREATE TABLE IF NOT EXISTS file_log (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  file_id BIGINT(20) DEFAULT NULL COMMENT '文件ID',
  operation VARCHAR(50) NOT NULL COMMENT '操作类型（upload/download/delete/rename/move/share）',
  operation_desc VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
  operator_id BIGINT(20) NOT NULL COMMENT '操作人ID',
  operator_name VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
  operator_ip VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
  user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
  operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  PRIMARY KEY (id),
  KEY idx_file_id (file_id),
  KEY idx_operator_id (operator_id),
  KEY idx_operation_time (operation_time),
  KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件操作日志表';

-- 存储配额表
CREATE TABLE IF NOT EXISTS file_quota (
  id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  total_quota BIGINT(20) NOT NULL DEFAULT 10737418240 COMMENT '总配额（默认10GB）',
  used_quota BIGINT(20) NOT NULL DEFAULT 0 COMMENT '已使用（字节）',
  file_count INT(11) NOT NULL DEFAULT 0 COMMENT '文件数量',
  folder_count INT(11) NOT NULL DEFAULT 0 COMMENT '文件夹数量',
  last_calculate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后计算时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存储配额表';

-- 创建用户和授权
CREATE USER IF NOT EXISTS 'qooerp_file'@'%' IDENTIFIED BY 'qooerp_file_2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON qooerp_file.* TO 'qooerp_file'@'%';
FLUSH PRIVILEGES;

-- 初始化数据
INSERT INTO file_quota (tenant_id, total_quota, used_quota, file_count, folder_count)
VALUES (1, 107374182400, 0, 0, 0) ON DUPLICATE KEY UPDATE tenant_id=tenant_id;
