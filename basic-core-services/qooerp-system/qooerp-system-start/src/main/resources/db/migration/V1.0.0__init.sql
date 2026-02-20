-- ================================================
-- QooERP 系统管理数据库初始化脚本
-- 版本: 1.0.0
-- ================================================

-- 1. 创建 system_dict 字典表
CREATE TABLE IF NOT EXISTS system_dict (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_dict_code ON system_dict(dict_code);
COMMENT ON TABLE system_dict IS '字典表';
COMMENT ON COLUMN system_dict.id IS '字典ID';
COMMENT ON COLUMN system_dict.dict_name IS '字典名称';
COMMENT ON COLUMN system_dict.dict_code IS '字典编码';
COMMENT ON COLUMN system_dict.dict_type IS '字典类型';
COMMENT ON COLUMN system_dict.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN system_dict.remark IS '备注';

-- 2. 创建 system_dict_item 字典项表
CREATE TABLE IF NOT EXISTS system_dict_item (
    id BIGSERIAL PRIMARY KEY,
    dict_id BIGINT NOT NULL,
    item_label VARCHAR(100) NOT NULL,
    item_value VARCHAR(100) NOT NULL,
    item_code VARCHAR(100) NOT NULL,
    sort INTEGER DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_system_dict_id ON system_dict_item(dict_id);
COMMENT ON TABLE system_dict_item IS '字典项表';
COMMENT ON COLUMN system_dict_item.id IS '字典项ID';
COMMENT ON COLUMN system_dict_item.dict_id IS '字典ID';
COMMENT ON COLUMN system_dict_item.item_label IS '字典项标签';
COMMENT ON COLUMN system_dict_item.item_value IS '字典项值';
COMMENT ON COLUMN system_dict_item.item_code IS '字典项编码';
COMMENT ON COLUMN system_dict_item.sort IS '排序号';
COMMENT ON COLUMN system_dict_item.status IS '状态：0-禁用 1-启用';

-- 3. 创建 system_config 系统参数表
CREATE TABLE IF NOT EXISTS system_config (
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_config_key ON system_config(config_key);
COMMENT ON TABLE system_config IS '系统参数表';
COMMENT ON COLUMN system_config.id IS '参数ID';
COMMENT ON COLUMN system_config.config_name IS '参数名称';
COMMENT ON COLUMN system_config.config_key IS '参数键';
COMMENT ON COLUMN system_config.config_value IS '参数值';
COMMENT ON COLUMN system_config.config_type IS '参数类型：string/number/boolean';
COMMENT ON COLUMN system_config.is_system IS '是否系统参数：0-否 1-是';

-- 4. 创建 system_log 操作日志表
CREATE TABLE IF NOT EXISTS system_log (
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

CREATE INDEX IF NOT EXISTS idx_system_user_id ON system_log(user_id, operate_time);
CREATE INDEX IF NOT EXISTS idx_system_operate_time ON system_log(operate_time);
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

-- 5. 创建 system_job 定时任务表
CREATE TABLE IF NOT EXISTS system_job (
    id BIGSERIAL PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    job_group VARCHAR(100) NOT NULL,
    job_class VARCHAR(200) NOT NULL,
    cron_expression VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status SMALLINT NOT NULL DEFAULT 0,
    concurrent SMALLINT NOT NULL DEFAULT 1,
    misfire_policy SMALLINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_system_job_group ON system_job(job_group);
COMMENT ON TABLE system_job IS '定时任务表';
COMMENT ON COLUMN system_job.id IS '任务ID';
COMMENT ON COLUMN system_job.job_name IS '任务名称';
COMMENT ON COLUMN system_job.job_group IS '任务组';
COMMENT ON COLUMN system_job.job_class IS '任务类名';
COMMENT ON COLUMN system_job.cron_expression IS 'Cron表达式';
COMMENT ON COLUMN system_job.status IS '状态：0-暂停 1-运行中';
COMMENT ON COLUMN system_job.concurrent IS '是否并发执行：0-否 1-是';
COMMENT ON COLUMN system_job.misfire_policy IS '错过策略：1-立即执行 2-执行一次 3-放弃';

-- 6. 插入初始数据
INSERT INTO system_dict (dict_name, dict_code, dict_type, create_by) VALUES
('用户状态', 'user_status', 'user', 'system'),
('字典状态', 'dict_status', 'system', 'system'),
('任务状态', 'job_status', 'system', 'system'),
('系统状态', 'sys_status', 'system', 'system')
ON CONFLICT DO NOTHING;

INSERT INTO system_dict_item (dict_id, item_label, item_value, item_code, sort) VALUES
((SELECT id FROM system_dict WHERE dict_code = 'user_status'), '正常', '1', 'normal', 1),
((SELECT id FROM system_dict WHERE dict_code = 'user_status'), '禁用', '0', 'disable', 2),
((SELECT id FROM system_dict WHERE dict_code = 'dict_status'), '启用', '1', 'enable', 1),
((SELECT id FROM system_dict WHERE dict_code = 'dict_status'), '禁用', '0', 'disable', 2),
((SELECT id FROM system_dict WHERE dict_code = 'job_status'), '运行中', '1', 'running', 1),
((SELECT id FROM system_dict WHERE dict_code = 'job_status'), '暂停', '0', 'paused', 2),
((SELECT id FROM system_dict WHERE dict_code = 'sys_status'), '正常', '1', 'normal', 1),
((SELECT id FROM system_dict WHERE dict_code = 'sys_status'), '维护', '0', 'maintenance', 2)
ON CONFLICT DO NOTHING;

INSERT INTO system_config (config_name, config_key, config_value, config_type, is_system, create_by) VALUES
('系统名称', 'sys.name', 'QooERP', 'string', 1, 'system'),
('系统版本', 'sys.version', '1.0.0', 'string', 1, 'system'),
('会话超时时间（秒）', 'sys.session.timeout', '7200', 'number', 1, 'system'),
('最大上传文件大小（MB）', 'sys.upload.maxSize', '100', 'number', 1, 'system'),
('允许的文件类型', 'sys.upload.allowedTypes', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'string', 1, 'system'),
('系统维护模式', 'sys.maintenance', 'false', 'boolean', 1, 'system')
ON CONFLICT DO NOTHING;
