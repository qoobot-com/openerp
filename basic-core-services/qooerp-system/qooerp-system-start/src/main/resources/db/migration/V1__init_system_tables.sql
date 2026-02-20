-- ================================================
-- QooERP System 模块数据库初始化脚本
-- 版本: 1.0.0
-- 创建日期: 2026-02-18
-- ================================================

-- 1. 系统公告表
CREATE TABLE IF NOT EXISTS system_announcement (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    announcement_type VARCHAR(20) NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_ids VARCHAR(1000),
    publisher_id BIGINT,
    publisher_name VARCHAR(50),
    publish_time TIMESTAMP,
    status SMALLINT NOT NULL DEFAULT 0,
    priority INTEGER NOT NULL DEFAULT 0,
    read_count INTEGER NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_system_announcement_status ON system_announcement(status, priority);
CREATE INDEX IF NOT EXISTS idx_system_announcement_publish_time ON system_announcement(publish_time);
COMMENT ON TABLE system_announcement IS '系统公告表';

-- 2. 公告阅读记录表
CREATE TABLE IF NOT EXISTS system_announcement_read (
    id BIGSERIAL PRIMARY KEY,
    announcement_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(50),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_announcement_user ON system_announcement_read(announcement_id, user_id);
CREATE INDEX IF NOT EXISTS idx_system_announcement_read_user ON system_announcement_read(user_id);
COMMENT ON TABLE system_announcement_read IS '公告阅读记录表';

-- 3. 在线用户表
CREATE TABLE IF NOT EXISTS system_online_user (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    token VARCHAR(500) NOT NULL,
    login_ip VARCHAR(50),
    login_location VARCHAR(100),
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_active_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status SMALLINT NOT NULL DEFAULT 1,
    user_agent VARCHAR(500)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_online_user_token ON system_online_user(token);
CREATE INDEX IF NOT EXISTS idx_system_online_user_user ON system_online_user(user_id);
CREATE INDEX IF NOT EXISTS idx_system_online_user_status ON system_online_user(status, last_active_time);
COMMENT ON TABLE system_online_user IS '在线用户表';

-- 4. 数据导出任务表
CREATE TABLE IF NOT EXISTS system_export_task (
    id BIGSERIAL PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    module_name VARCHAR(50) NOT NULL,
    export_type VARCHAR(20) NOT NULL,
    export_condition TEXT,
    status SMALLINT NOT NULL DEFAULT 0,
    file_path VARCHAR(500),
    file_name VARCHAR(200),
    file_size BIGINT,
    total_records INTEGER,
    creator_id BIGINT,
    creator_name VARCHAR(50),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    complete_time TIMESTAMP,
    error_message TEXT
);

CREATE INDEX IF NOT EXISTS idx_system_export_task_status ON system_export_task(status, create_time);
CREATE INDEX IF NOT EXISTS idx_system_export_task_creator ON system_export_task(creator_id);
COMMENT ON TABLE system_export_task IS '数据导出任务表';

-- 5. 数据导入任务表
CREATE TABLE IF NOT EXISTS system_import_task (
    id BIGSERIAL PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    module_name VARCHAR(50) NOT NULL,
    import_type VARCHAR(20) NOT NULL,
    file_path VARCHAR(500),
    file_name VARCHAR(200),
    total_records INTEGER,
    success_records INTEGER DEFAULT 0,
    failure_records INTEGER DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 0,
    creator_id BIGINT,
    creator_name VARCHAR(50),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    complete_time TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_system_import_task_status ON system_import_task(status, create_time);
CREATE INDEX IF NOT EXISTS idx_system_import_task_creator ON system_import_task(creator_id);
COMMENT ON TABLE system_import_task IS '数据导入任务表';

-- 6. 导入错误记录表
CREATE TABLE IF NOT EXISTS system_import_error (
    id BIGSERIAL PRIMARY KEY,
    import_task_id BIGINT NOT NULL,
    row_number INTEGER NOT NULL,
    field_name VARCHAR(100),
    error_message VARCHAR(500) NOT NULL,
    row_data TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_system_import_error_task ON system_import_error(import_task_id);
COMMENT ON TABLE system_import_error IS '导入错误记录表';

-- 7. 审计日志表
CREATE TABLE IF NOT EXISTS system_audit_log (
    id BIGSERIAL PRIMARY KEY,
    audit_type VARCHAR(20) NOT NULL,
    module_name VARCHAR(50),
    operation_type VARCHAR(50),
    target_table VARCHAR(100),
    target_id VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    changed_fields TEXT,
    user_id BIGINT,
    username VARCHAR(50),
    ip VARCHAR(50),
    user_agent VARCHAR(500),
    operate_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_system_audit_log_type ON system_audit_log(audit_type, operate_time);
CREATE INDEX IF NOT EXISTS idx_system_audit_log_user ON system_audit_log(user_id, operate_time);
CREATE INDEX IF NOT EXISTS idx_system_audit_log_target ON system_audit_log(target_table, target_id, operate_time);
COMMENT ON TABLE system_audit_log IS '审计日志表';
