-- ================================================
-- QooERP 用户管理数据库初始化脚本
-- 数据库: qooerp_user
-- 版本: 1.0.0
-- 创建日期: 2026-02-17
-- ================================================

-- ================================================
-- 1. 创建user_info用户信息表
-- ================================================
DROP TABLE IF EXISTS user_info CASCADE;

CREATE TABLE user_info (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    username VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    real_name VARCHAR(50),
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    gender SMALLINT DEFAULT 0,
    birthday DATE,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50),
    deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_user_username ON user_info(username) WHERE deleted = 0;
CREATE UNIQUE INDEX uk_user_phone ON user_info(phone) WHERE deleted = 0;
CREATE UNIQUE INDEX uk_user_email ON user_info(email) WHERE deleted = 0;

COMMENT ON TABLE user_info IS '用户信息表';
COMMENT ON COLUMN user_info.id IS '用户ID';
COMMENT ON COLUMN user_info.tenant_id IS '租户ID';
COMMENT ON COLUMN user_info.username IS '用户名';
COMMENT ON COLUMN user_info.phone IS '手机号';
COMMENT ON COLUMN user_info.email IS '邮箱';
COMMENT ON COLUMN user_info.real_name IS '真实姓名';
COMMENT ON COLUMN user_info.nickname IS '昵称';
COMMENT ON COLUMN user_info.avatar IS '头像URL';
COMMENT ON COLUMN user_info.gender IS '性别：0-未知 1-男 2-女';
COMMENT ON COLUMN user_info.birthday IS '生日';
COMMENT ON COLUMN user_info.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN user_info.remark IS '备注';
COMMENT ON COLUMN user_info.deleted IS '删除标记：0-未删除 1-已删除';

-- ================================================
-- 2. 创建user_dept用户部门关联表
-- ================================================
DROP TABLE IF EXISTS user_dept CASCADE;

CREATE TABLE user_dept (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    is_primary SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_user_dept ON user_dept(user_id, dept_id);
CREATE INDEX idx_user_id ON user_dept(user_id);

COMMENT ON TABLE user_dept IS '用户部门关联表';
COMMENT ON COLUMN user_dept.id IS '主键ID';
COMMENT ON COLUMN user_dept.user_id IS '用户ID';
COMMENT ON COLUMN user_dept.dept_id IS '部门ID';
COMMENT ON COLUMN user_dept.is_primary IS '是否主部门：0-否 1-是';

-- ================================================
-- 3. 创建user_position用户岗位关联表
-- ================================================
DROP TABLE IF EXISTS user_position CASCADE;

CREATE TABLE user_position (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    position_id BIGINT NOT NULL
);

CREATE UNIQUE INDEX uk_user_position ON user_position(user_id, position_id);
CREATE INDEX idx_user_position_user_id ON user_position(user_id);

COMMENT ON TABLE user_position IS '用户岗位关联表';
COMMENT ON COLUMN user_position.id IS '主键ID';
COMMENT ON COLUMN user_position.user_id IS '用户ID';
COMMENT ON COLUMN user_position.position_id IS '岗位ID';

-- ================================================
-- 4. 初始化数据
-- ================================================

-- 插入默认用户（admin用户）
INSERT INTO user_info (
    tenant_id, username, real_name, nickname, status, create_by, update_by
) VALUES (
    0,
    'admin',
    '系统管理员',
    '超级管理员',
    1,
    'system',
    'system'
);

-- ================================================
-- 脚本结束
-- ================================================
