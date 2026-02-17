-- 用户表
CREATE TABLE IF NOT EXISTS auth_user (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100),
    status SMALLINT NOT NULL DEFAULT 1,
    account_type SMALLINT DEFAULT 1,
    mfa_enabled SMALLINT DEFAULT 0,
    mfa_secret_key VARCHAR(255),
    last_login_ip VARCHAR(50),
    last_login_time TIMESTAMP,
    login_fail_count INTEGER NOT NULL DEFAULT 0,
    lock_time TIMESTAMP,
    remark TEXT,
    deleted SMALLINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

COMMENT ON TABLE auth_user IS '用户表';
COMMENT ON COLUMN auth_user.id IS '用户ID';
COMMENT ON COLUMN auth_user.tenant_id IS '租户ID';
COMMENT ON COLUMN auth_user.username IS '用户名';
COMMENT ON COLUMN auth_user.password IS '密码（BCrypt加密）';
COMMENT ON COLUMN auth_user.real_name IS '真实姓名';
COMMENT ON COLUMN auth_user.nickname IS '昵称';
COMMENT ON COLUMN auth_user.avatar IS '头像URL';
COMMENT ON COLUMN auth_user.phone IS '手机号';
COMMENT ON COLUMN auth_user.email IS '邮箱';
COMMENT ON COLUMN auth_user.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN auth_user.lock_time IS '锁定时间';
COMMENT ON COLUMN auth_user.login_fail_count IS '登录失败次数';
COMMENT ON COLUMN auth_user.last_login_time IS '最后登录时间';
COMMENT ON COLUMN auth_user.last_login_ip IS '最后登录IP';
COMMENT ON COLUMN auth_user.create_time IS '创建时间';
COMMENT ON COLUMN auth_user.create_by IS '创建人';
COMMENT ON COLUMN auth_user.update_time IS '更新时间';
COMMENT ON COLUMN auth_user.update_by IS '更新人';
COMMENT ON COLUMN auth_user.deleted IS '删除标记：0-未删除 1-已删除';

-- 创建用户表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_auth_username ON auth_user(username) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_auth_phone ON auth_user(phone, deleted) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_auth_email ON auth_user(email, deleted) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_auth_tenant_id ON auth_user(tenant_id, deleted) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_auth_create_time ON auth_user(create_time);
CREATE INDEX IF NOT EXISTS idx_auth_status ON auth_user(status, deleted) WHERE deleted = 0;

-- 登录日志表
CREATE TABLE IF NOT EXISTS auth_login_log (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT,
    username VARCHAR(50),
    login_ip VARCHAR(50),
    login_location VARCHAR(100),
    browser VARCHAR(50),
    os VARCHAR(50),
    device VARCHAR(50),
    status SMALLINT NOT NULL DEFAULT 0,
    message VARCHAR(500),
    login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE auth_login_log IS '登录日志表';
COMMENT ON COLUMN auth_login_log.id IS '日志ID';
COMMENT ON COLUMN auth_login_log.tenant_id IS '租户ID';
COMMENT ON COLUMN auth_login_log.user_id IS '用户ID';
COMMENT ON COLUMN auth_login_log.username IS '用户名';
COMMENT ON COLUMN auth_login_log.login_ip IS '登录IP';
COMMENT ON COLUMN auth_login_log.login_location IS '登录地点';
COMMENT ON COLUMN auth_login_log.browser IS '浏览器类型';
COMMENT ON COLUMN auth_login_log.os IS '操作系统';
COMMENT ON COLUMN auth_login_log.device IS '设备类型';
COMMENT ON COLUMN auth_login_log.status IS '登录状态：0-失败 1-成功';
COMMENT ON COLUMN auth_login_log.message IS '登录消息';
COMMENT ON COLUMN auth_login_log.login_time IS '登录时间';
COMMENT ON COLUMN auth_login_log.create_time IS '创建时间';

-- 创建登录日志表索引
CREATE INDEX IF NOT EXISTS idx_auth_user_id ON auth_login_log(user_id, login_time);
CREATE INDEX IF NOT EXISTS idx_auth_tenant_id ON auth_login_log(tenant_id, login_time);
CREATE INDEX IF NOT EXISTS idx_auth_login_time ON auth_login_log(login_time);
CREATE INDEX IF NOT EXISTS idx_auth_status ON auth_login_log(status, login_time);
CREATE INDEX IF NOT EXISTS idx_auth_login_ip ON auth_login_log(login_ip, login_time);

-- 插入默认管理员用户（密码：admin123）
INSERT INTO auth_user (
    tenant_id, username, password, real_name, nickname, status, account_type, create_by, update_by
)
VALUES (
    0,
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    '系统管理员',
    '超级管理员',
    1,
    0,
    'system',
    'system'
)
ON CONFLICT DO NOTHING;
