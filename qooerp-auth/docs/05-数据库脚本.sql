-- ================================================
-- QooERP 认证服务数据库初始化脚本
-- 数据库: qooerp_auth
-- 版本: 1.0.0
-- 创建日期: 2026-02-17
-- ================================================

-- ================================================
-- 1. 创建数据库（如不存在）
-- ================================================
-- CREATE DATABASE IF NOT EXISTS qooerp_auth
--     WITH ENCODING = 'UTF8'
--     TEMPLATE = template0
--     LC_COLLATE = 'zh_CN.UTF-8'
--     LC_CTYPE = 'zh_CN.UTF-8';

-- ================================================
-- 2. 创建auth_user用户表
-- ================================================
DROP TABLE IF EXISTS auth_user CASCADE;

CREATE TABLE auth_user (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    real_name VARCHAR(50),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    gender SMALLINT DEFAULT 0,
    birthday DATE,
    status SMALLINT NOT NULL DEFAULT 1,
    lock_time TIMESTAMP,
    failed_count INTEGER NOT NULL DEFAULT 0,
    last_login_time TIMESTAMP,
    last_login_ip VARCHAR(50),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50),
    deleted SMALLINT NOT NULL DEFAULT 0
);

-- 添加表注释
COMMENT ON TABLE auth_user IS '用户表';
COMMENT ON COLUMN auth_user.id IS '用户ID';
COMMENT ON COLUMN auth_user.tenant_id IS '租户ID';
COMMENT ON COLUMN auth_user.username IS '用户名';
COMMENT ON COLUMN auth_user.password IS '密码（BCrypt加密）';
COMMENT ON COLUMN auth_user.phone IS '手机号';
COMMENT ON COLUMN auth_user.email IS '邮箱';
COMMENT ON COLUMN auth_user.real_name IS '真实姓名';
COMMENT ON COLUMN auth_user.nickname IS '昵称';
COMMENT ON COLUMN auth_user.avatar IS '头像URL';
COMMENT ON COLUMN auth_user.gender IS '性别：0-未知 1-男 2-女';
COMMENT ON COLUMN auth_user.birthday IS '生日';
COMMENT ON COLUMN auth_user.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN auth_user.lock_time IS '锁定时间';
COMMENT ON COLUMN auth_user.failed_count IS '登录失败次数';
COMMENT ON COLUMN auth_user.last_login_time IS '最后登录时间';
COMMENT ON COLUMN auth_user.last_login_ip IS '最后登录IP';
COMMENT ON COLUMN auth_user.create_time IS '创建时间';
COMMENT ON COLUMN auth_user.create_by IS '创建人';
COMMENT ON COLUMN auth_user.update_time IS '更新时间';
COMMENT ON COLUMN auth_user.update_by IS '更新人';
COMMENT ON COLUMN auth_user.deleted IS '删除标记：0-未删除 1-已删除';

-- 创建索引
CREATE UNIQUE INDEX uk_auth_username ON auth_user(username) WHERE deleted = 0;
CREATE INDEX idx_auth_phone ON auth_user(phone, deleted) WHERE deleted = 0;
CREATE INDEX idx_auth_email ON auth_user(email, deleted) WHERE deleted = 0;
CREATE INDEX idx_auth_tenant_id ON auth_user(tenant_id, deleted) WHERE deleted = 0;
CREATE INDEX idx_auth_create_time ON auth_user(create_time);
CREATE INDEX idx_auth_status ON auth_user(status, deleted) WHERE deleted = 0;

-- ================================================
-- 3. 创建auth_login_log登录日志表
-- ================================================
DROP TABLE IF EXISTS auth_login_log CASCADE;

CREATE TABLE auth_login_log (
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

-- 添加表注释
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

-- 创建索引
CREATE INDEX idx_auth_user_id ON auth_login_log(user_id, login_time);
CREATE INDEX idx_auth_tenant_id ON auth_login_log(tenant_id, login_time);
CREATE INDEX idx_auth_login_time ON auth_login_log(login_time);
CREATE INDEX idx_auth_status ON auth_login_log(status, login_time);
CREATE INDEX idx_auth_login_ip ON auth_login_log(login_ip, login_time);

-- ================================================
-- 4. 初始化数据
-- ================================================

-- 插入默认管理员（用户名：admin，密码：admin123）
-- 密码BCrypt加密后的值
INSERT INTO auth_user (
    tenant_id,
    username,
    password,
    real_name,
    nickname,
    status,
    create_by,
    update_by
) VALUES (
    0,
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
    '系统管理员',
    '超级管理员',
    1,
    'system',
    'system'
);

-- ================================================
-- 5. 创建视图（可选）
-- ================================================

-- 用户详情视图（包含最后登录信息）
CREATE OR REPLACE VIEW v_auth_user_detail AS
SELECT
    u.id,
    u.tenant_id,
    u.username,
    u.phone,
    u.email,
    u.real_name,
    u.nickname,
    u.avatar,
    u.gender,
    u.birthday,
    u.status,
    u.last_login_time,
    u.last_login_ip,
    u.failed_count,
    u.lock_time,
    u.create_time,
    u.update_time,
    COALESCE(COUNT(ll.id), 0) AS login_count,
    COALESCE(MAX(ll.login_time), u.last_login_time) AS last_login
FROM auth_user u
LEFT JOIN auth_login_log ll ON u.id = ll.user_id AND ll.status = 1
WHERE u.deleted = 0
GROUP BY u.id;

COMMENT ON VIEW v_auth_user_detail IS '用户详情视图';

-- ================================================
-- 6. 创建函数（可选）
-- ================================================

-- 重置用户登录失败次数
CREATE OR REPLACE FUNCTION auth_reset_failed_count(p_user_id BIGINT)
RETURNS INTEGER AS $$
BEGIN
    UPDATE auth_user
    SET failed_count = 0,
        lock_time = NULL
    WHERE id = p_user_id;

    RETURN 1;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION auth_reset_failed_count IS '重置用户登录失败次数';

-- 检查用户是否被锁定
CREATE OR REPLACE FUNCTION auth_is_user_locked(p_user_id BIGINT)
RETURNS BOOLEAN AS $$
DECLARE
    v_lock_time TIMESTAMP;
BEGIN
    SELECT lock_time INTO v_lock_time
    FROM auth_user
    WHERE id = p_user_id;

    IF v_lock_time IS NOT NULL AND v_lock_time > CURRENT_TIMESTAMP THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION auth_is_user_locked IS '检查用户是否被锁定';

-- ================================================
-- 7. 创建触发器（可选）
-- ================================================

-- 更新时间触发器
CREATE OR REPLACE FUNCTION update_time_trigger()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为auth_user表创建更新时间触发器
DROP TRIGGER IF EXISTS trg_auth_update_time ON auth_user;
CREATE TRIGGER trg_auth_update_time
    BEFORE UPDATE ON auth_user
    FOR EACH ROW
    EXECUTE FUNCTION update_time_trigger();

-- ================================================
-- 8. 创建存储过程（可选）
-- ================================================

-- 清理过期登录日志（保留90天）
CREATE OR REPLACE PROCEDURE auth_clean_expired_login_logs()
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM auth_login_log
    WHERE login_time < CURRENT_TIMESTAMP - INTERVAL '90 days';

    RAISE NOTICE '已清理过期登录日志，影响行数: %', ROW_COUNT;
END;
$$;

COMMENT ON PROCEDURE auth_clean_expired_login_logs IS '清理过期登录日志（保留90天）';

-- ================================================
-- 9. 授权（根据实际环境调整）
-- ================================================

-- 授予只读权限（只读用户）
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;

-- 授予读写权限（应用用户）
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- ================================================
-- 10. 验证数据
-- ================================================

-- 查询用户表数据
SELECT id, username, real_name, status FROM auth_user WHERE deleted = 0;

-- 查询登录日志数据
SELECT id, user_id, username, login_ip, status, login_time
FROM auth_login_log
ORDER BY login_time DESC
LIMIT 10;

-- ================================================
-- 11. 性能优化建议
-- ================================================

-- 定期执行VACUUM和ANALYZE
-- VACUUM ANALYZE auth_user;
-- VACUUM ANALYZE auth_login_log;

-- ================================================
-- 脚本结束
-- ================================================
