-- ================================================
-- QooERP 系统管理数据库初始化脚本
-- 数据库: qooerp_system
-- 版本: 1.2.0
-- 创建日期: 2026-02-17
-- 更新日期: 2026-02-18
-- 说明: 移除定时任务和文件管理相关表，新增系统公告、在线用户、导入导出、审计日志等表
-- ================================================

-- ================================================
-- 1. 创建system_dict字典表
-- ================================================
DROP TABLE IF EXISTS system_dict CASCADE;

CREATE TABLE system_dict (
    id BIGSERIAL PRIMARY KEY,
    dict_name VARCHAR(100) NOT NULL,
    dict_code VARCHAR(100) NOT NULL,
    dict_type VARCHAR(100) NOT NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE UNIQUE INDEX uk_system_dict_code ON system_dict(dict_code);
COMMENT ON TABLE system_dict IS '字典表';
COMMENT ON COLUMN system_dict.id IS '字典ID';
COMMENT ON COLUMN system_dict.dict_name IS '字典名称';
COMMENT ON COLUMN system_dict.dict_code IS '字典编码';
COMMENT ON COLUMN system_dict.dict_type IS '字典类型';
COMMENT ON COLUMN system_dict.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN system_dict.remark IS '备注';

-- ================================================
-- 2. 创建system_dict_item字典项表
-- ================================================
DROP TABLE IF EXISTS system_dict_item CASCADE;

CREATE TABLE system_dict_item (
    id BIGSERIAL PRIMARY KEY,
    dict_id BIGINT NOT NULL,
    item_label VARCHAR(100) NOT NULL,
    item_value VARCHAR(100) NOT NULL,
    item_code VARCHAR(100) NOT NULL,
    sort INTEGER DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500)
);

CREATE INDEX idx_system_dict_id ON system_dict_item(dict_id);
COMMENT ON TABLE system_dict_item IS '字典项表';
COMMENT ON COLUMN system_dict_item.id IS '字典项ID';
COMMENT ON COLUMN system_dict_item.dict_id IS '字典ID';
COMMENT ON COLUMN system_dict_item.item_label IS '字典项标签';
COMMENT ON COLUMN system_dict_item.item_value IS '字典项值';
COMMENT ON COLUMN system_dict_item.item_code IS '字典项编码';
COMMENT ON COLUMN system_dict_item.sort IS '排序号';
COMMENT ON COLUMN system_dict_item.status IS '状态：0-禁用 1-启用';

-- ================================================
-- 3. 创建system_config系统参数表
-- ================================================
DROP TABLE IF EXISTS system_config CASCADE;

CREATE TABLE system_config (
    id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500),
    config_type VARCHAR(20) NOT NULL,
    is_system SMALLINT NOT NULL DEFAULT 0,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE UNIQUE INDEX uk_system_config_key ON system_config(config_key);
COMMENT ON TABLE system_config IS '系统参数表';
COMMENT ON COLUMN system_config.id IS '参数ID';
COMMENT ON COLUMN system_config.config_name IS '参数名称';
COMMENT ON COLUMN system_config.config_key IS '参数键';
COMMENT ON COLUMN system_config.config_value IS '参数值';
COMMENT ON COLUMN system_config.config_type IS '参数类型：string/number/boolean';
COMMENT ON COLUMN system_config.is_system IS '是否系统参数：0-否 1-是';

-- ================================================
-- 4. 创建system_log操作日志表
-- ================================================
DROP TABLE IF EXISTS system_log CASCADE;

CREATE TABLE system_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    module VARCHAR(50),
    operation VARCHAR(50),
    method VARCHAR(200),
    params TEXT,
    ip VARCHAR(50),
    location VARCHAR(100),
    browser VARCHAR(50),
    os VARCHAR(50),
    status SMALLINT NOT NULL DEFAULT 0,
    error_msg VARCHAR(500),
    cost_time INTEGER,
    operate_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_user_id ON system_log(user_id, operate_time);
CREATE INDEX idx_system_operate_time ON system_log(operate_time);
COMMENT ON TABLE system_log IS '操作日志表';
COMMENT ON COLUMN system_log.id IS '日志ID';
COMMENT ON COLUMN system_log.user_id IS '用户ID';
COMMENT ON COLUMN system_log.username IS '用户名';
COMMENT ON COLUMN system_log.module IS '模块名称';
COMMENT ON COLUMN system_log.operation IS '操作类型';
COMMENT ON COLUMN system_log.method IS '方法名称';
COMMENT ON COLUMN system_log.params IS '请求参数';
COMMENT ON COLUMN system_log.ip IS '请求IP';
COMMENT ON COLUMN system_log.status IS '状态：0-失败 1-成功';
COMMENT ON COLUMN system_log.error_msg IS '错误信息';
COMMENT ON COLUMN system_log.cost_time IS '执行时间（毫秒）';
COMMENT ON COLUMN system_log.operate_time IS '操作时间';

-- ================================================
-- 5. 创建system_announcement系统公告表
-- ================================================
DROP TABLE IF EXISTS system_announcement CASCADE;

CREATE TABLE system_announcement (
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

CREATE INDEX idx_system_announcement_status ON system_announcement(status, priority);
CREATE INDEX idx_system_announcement_publish_time ON system_announcement(publish_time);
COMMENT ON TABLE system_announcement IS '系统公告表';
COMMENT ON COLUMN system_announcement.id IS '公告ID';
COMMENT ON COLUMN system_announcement.title IS '公告标题';
COMMENT ON COLUMN system_announcement.content IS '公告内容';
COMMENT ON COLUMN system_announcement.announcement_type IS '公告类型：urgent/important/normal';
COMMENT ON COLUMN system_announcement.target_type IS '目标类型：all/department/role';
COMMENT ON COLUMN system_announcement.target_ids IS '目标ID列表';
COMMENT ON COLUMN system_announcement.publisher_id IS '发布人ID';
COMMENT ON COLUMN system_announcement.publisher_name IS '发布人姓名';
COMMENT ON COLUMN system_announcement.publish_time IS '发布时间';
COMMENT ON COLUMN system_announcement.status IS '状态：0-草稿 1-已发布 2-已撤回';
COMMENT ON COLUMN system_announcement.priority IS '优先级';
COMMENT ON COLUMN system_announcement.read_count IS '阅读次数';

-- ================================================
-- 6. 创建system_announcement_read公告阅读记录表
-- ================================================
DROP TABLE IF EXISTS system_announcement_read CASCADE;

CREATE TABLE system_announcement_read (
    id BIGSERIAL PRIMARY KEY,
    announcement_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(50),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_announcement_user ON system_announcement_read(announcement_id, user_id);
CREATE INDEX idx_system_announcement_read_user ON system_announcement_read(user_id);
COMMENT ON TABLE system_announcement_read IS '公告阅读记录表';
COMMENT ON COLUMN system_announcement_read.id IS '记录ID';
COMMENT ON COLUMN system_announcement_read.announcement_id IS '公告ID';
COMMENT ON COLUMN system_announcement_read.user_id IS '用户ID';
COMMENT ON COLUMN system_announcement_read.username IS '用户名';
COMMENT ON COLUMN system_announcement_read.is_read IS '是否已读';
COMMENT ON COLUMN system_announcement_read.read_time IS '阅读时间';

-- ================================================
-- 7. 创建system_online_user在线用户表
-- ================================================
DROP TABLE IF EXISTS system_online_user CASCADE;

CREATE TABLE system_online_user (
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

CREATE UNIQUE INDEX uk_system_online_user_token ON system_online_user(token);
CREATE INDEX idx_system_online_user_user ON system_online_user(user_id);
CREATE INDEX idx_system_online_user_status ON system_online_user(status, last_active_time);
COMMENT ON TABLE system_online_user IS '在线用户表';
COMMENT ON COLUMN system_online_user.id IS '记录ID';
COMMENT ON COLUMN system_online_user.user_id IS '用户ID';
COMMENT ON COLUMN system_online_user.username IS '用户名';
COMMENT ON COLUMN system_online_user.token IS '登录令牌';
COMMENT ON COLUMN system_online_user.login_ip IS '登录IP';
COMMENT ON COLUMN system_online_user.login_location IS '登录地点';
COMMENT ON COLUMN system_online_user.login_time IS '登录时间';
COMMENT ON COLUMN system_online_user.last_active_time IS '最后活跃时间';
COMMENT ON COLUMN system_online_user.status IS '状态：0-已下线 1-在线';
COMMENT ON COLUMN system_online_user.user_agent IS '用户代理';

-- ================================================
-- 8. 创建system_export_task数据导出任务表
-- ================================================
DROP TABLE IF EXISTS system_export_task CASCADE;

CREATE TABLE system_export_task (
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

CREATE INDEX idx_system_export_task_status ON system_export_task(status, create_time);
CREATE INDEX idx_system_export_task_creator ON system_export_task(creator_id);
COMMENT ON TABLE system_export_task IS '数据导出任务表';
COMMENT ON COLUMN system_export_task.id IS '任务ID';
COMMENT ON COLUMN system_export_task.task_name IS '任务名称';
COMMENT ON COLUMN system_export_task.module_name IS '模块名称';
COMMENT ON COLUMN system_export_task.export_type IS '导出类型：excel/csv/pdf';
COMMENT ON COLUMN system_export_task.export_condition IS '导出条件（JSON）';
COMMENT ON COLUMN system_export_task.status IS '状态：0-待执行 1-执行中 2-成功 3-失败';
COMMENT ON COLUMN system_export_task.file_path IS '文件路径';
COMMENT ON COLUMN system_export_task.file_name IS '文件名称';
COMMENT ON COLUMN system_export_task.file_size IS '文件大小（字节）';
COMMENT ON COLUMN system_export_task.total_records IS '总记录数';
COMMENT ON COLUMN system_export_task.creator_id IS '创建人ID';
COMMENT ON COLUMN system_export_task.creator_name IS '创建人姓名';
COMMENT ON COLUMN system_export_task.create_time IS '创建时间';
COMMENT ON COLUMN system_export_task.complete_time IS '完成时间';
COMMENT ON COLUMN system_export_task.error_message IS '错误信息';

-- ================================================
-- 9. 创建system_import_task数据导入任务表
-- ================================================
DROP TABLE IF EXISTS system_import_task CASCADE;

CREATE TABLE system_import_task (
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

CREATE INDEX idx_system_import_task_status ON system_import_task(status, create_time);
CREATE INDEX idx_system_import_task_creator ON system_import_task(creator_id);
COMMENT ON TABLE system_import_task IS '数据导入任务表';
COMMENT ON COLUMN system_import_task.id IS '任务ID';
COMMENT ON COLUMN system_import_task.task_name IS '任务名称';
COMMENT ON COLUMN system_import_task.module_name IS '模块名称';
COMMENT ON COLUMN system_import_task.import_type IS '导入类型：excel/csv';
COMMENT ON COLUMN system_import_task.file_path IS '文件路径';
COMMENT ON COLUMN system_import_task.file_name IS '文件名称';
COMMENT ON COLUMN system_import_task.total_records IS '总记录数';
COMMENT ON COLUMN system_import_task.success_records IS '成功记录数';
COMMENT ON COLUMN system_import_task.failure_records IS '失败记录数';
COMMENT ON COLUMN system_import_task.status IS '状态：0-待执行 1-执行中 2-成功 3-失败';
COMMENT ON COLUMN system_import_task.creator_id IS '创建人ID';
COMMENT ON COLUMN system_import_task.creator_name IS '创建人姓名';
COMMENT ON COLUMN system_import_task.create_time IS '创建时间';
COMMENT ON COLUMN system_import_task.complete_time IS '完成时间';

-- ================================================
-- 10. 创建system_import_error导入错误记录表
-- ================================================
DROP TABLE IF EXISTS system_import_error CASCADE;

CREATE TABLE system_import_error (
    id BIGSERIAL PRIMARY KEY,
    import_task_id BIGINT NOT NULL,
    row_number INTEGER NOT NULL,
    field_name VARCHAR(100),
    error_message VARCHAR(500) NOT NULL,
    row_data TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_import_error_task ON system_import_error(import_task_id);
COMMENT ON TABLE system_import_error IS '导入错误记录表';
COMMENT ON COLUMN system_import_error.id IS '错误ID';
COMMENT ON COLUMN system_import_error.import_task_id IS '导入任务ID';
COMMENT ON COLUMN system_import_error.row_number IS '行号';
COMMENT ON COLUMN system_import_error.field_name IS '字段名';
COMMENT ON COLUMN system_import_error.error_message IS '错误信息';
COMMENT ON COLUMN system_import_error.row_data IS '行数据（JSON）';

-- ================================================
-- 11. 创建system_audit_log审计日志表
-- ================================================
DROP TABLE IF EXISTS system_audit_log CASCADE;

CREATE TABLE system_audit_log (
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

CREATE INDEX idx_system_audit_log_type ON system_audit_log(audit_type, operate_time);
CREATE INDEX idx_system_audit_log_user ON system_audit_log(user_id, operate_time);
CREATE INDEX idx_system_audit_log_target ON system_audit_log(target_table, target_id, operate_time);
COMMENT ON TABLE system_audit_log IS '审计日志表';
COMMENT ON COLUMN system_audit_log.id IS '日志ID';
COMMENT ON COLUMN system_audit_log.audit_type IS '审计类型：data_change/sensitive_operation/login';
COMMENT ON COLUMN system_audit_log.module_name IS '模块名称';
COMMENT ON COLUMN system_audit_log.operation_type IS '操作类型';
COMMENT ON COLUMN system_audit_log.target_table IS '目标表名';
COMMENT ON COLUMN system_audit_log.target_id IS '目标ID';
COMMENT ON COLUMN system_audit_log.old_value IS '变更前值';
COMMENT ON COLUMN system_audit_log.new_value IS '变更后值';
COMMENT ON COLUMN system_audit_log.changed_fields IS '变更字段列表（JSON）';
COMMENT ON COLUMN system_audit_log.user_id IS '用户ID';
COMMENT ON COLUMN system_audit_log.username IS '用户名';
COMMENT ON COLUMN system_audit_log.ip IS 'IP地址';
COMMENT ON COLUMN system_audit_log.user_agent IS '用户代理';
COMMENT ON COLUMN system_audit_log.operate_time IS '操作时间';

-- ================================================
-- 12. 初始化数据
-- ================================================

-- 插入默认字典
INSERT INTO system_dict (dict_name, dict_code, dict_type, create_by) VALUES
('用户状态', 'user_status', 'user', 'system'),
('字典状态', 'dict_status', 'system', 'system'),
('系统状态', 'sys_status', 'system', 'system'),
('公告类型', 'announcement_type', 'system', 'system'),
('公告状态', 'announcement_status', 'system', 'system'),
('导出状态', 'export_status', 'system', 'system'),
('导入状态', 'import_status', 'system', 'system');

-- 插入字典项
INSERT INTO system_dict_item (dict_id, item_label, item_value, item_code, sort) VALUES
-- 用户状态
((SELECT id FROM system_dict WHERE dict_code = 'user_status'), '正常', '1', 'normal', 1),
((SELECT id FROM system_dict WHERE dict_code = 'user_status'), '禁用', '0', 'disable', 2),
-- 字典状态
((SELECT id FROM system_dict WHERE dict_code = 'dict_status'), '启用', '1', 'enable', 1),
((SELECT id FROM system_dict WHERE dict_code = 'dict_status'), '禁用', '0', 'disable', 2),
-- 系统状态
((SELECT id FROM system_dict WHERE dict_code = 'sys_status'), '正常', '1', 'normal', 1),
((SELECT id FROM system_dict WHERE dict_code = 'sys_status'), '维护', '0', 'maintenance', 2),
-- 公告类型
((SELECT id FROM system_dict WHERE dict_code = 'announcement_type'), '紧急', 'urgent', 'urgent', 1),
((SELECT id FROM system_dict WHERE dict_code = 'announcement_type'), '重要', 'important', 'important', 2),
((SELECT id FROM system_dict WHERE dict_code = 'announcement_type'), '普通', 'normal', 'normal', 3),
-- 公告状态
((SELECT id FROM system_dict WHERE dict_code = 'announcement_status'), '草稿', '0', 'draft', 1),
((SELECT id FROM system_dict WHERE dict_code = 'announcement_status'), '已发布', '1', 'published', 2),
((SELECT id FROM system_dict WHERE dict_code = 'announcement_status'), '已撤回', '2', 'revoked', 3),
-- 导出状态
((SELECT id FROM system_dict WHERE dict_code = 'export_status'), '待执行', '0', 'pending', 1),
((SELECT id FROM system_dict WHERE dict_code = 'export_status'), '执行中', '1', 'processing', 2),
((SELECT id FROM system_dict WHERE dict_code = 'export_status'), '成功', '2', 'success', 3),
((SELECT id FROM system_dict WHERE dict_code = 'export_status'), '失败', '3', 'failure', 4),
-- 导入状态
((SELECT id FROM system_dict WHERE dict_code = 'import_status'), '待执行', '0', 'pending', 1),
((SELECT id FROM system_dict WHERE dict_code = 'import_status'), '执行中', '1', 'processing', 2),
((SELECT id FROM system_dict WHERE dict_code = 'import_status'), '成功', '2', 'success', 3),
((SELECT id FROM system_dict WHERE dict_code = 'import_status'), '失败', '3', 'failure', 4);

-- 插入默认系统参数
INSERT INTO system_config (config_name, config_key, config_value, config_type, is_system, create_by) VALUES
('系统名称', 'sys.name', 'QooERP', 'string', 1, 'system'),
('系统版本', 'sys.version', '1.0.0', 'string', 1, 'system'),
('会话超时时间（秒）', 'sys.session.timeout', '7200', 'number', 1, 'system'),
('系统维护模式', 'sys.maintenance', 'false', 'boolean', 1, 'system'),
('日志保留天数', 'sys.log.retentionDays', '90', 'number', 1, 'system'),
('审计日志保留天数', 'sys.auditLog.retentionDays', '365', 'number', 1, 'system'),
('单次导出最大记录数', 'sys.export.maxRecords', '100000', 'number', 1, 'system'),
('单次导入最大记录数', 'sys.import.maxRecords', '50000', 'number', 1, 'system'),
('允许的导入文件类型', 'sys.import.allowedTypes', 'xlsx,csv', 'string', 1, 'system');

-- ================================================
-- 脚本结束
-- ================================================
