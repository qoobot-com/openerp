-- ================================================
-- QooERP 权限服务数据库初始化脚本
-- 数据库: qooerp_permission
-- 版本: 1.0.0
-- 创建日期: 20xx-xx-xx
-- ================================================

-- ================================================
-- 1. 创建数据库（如不存在）
-- ================================================
-- CREATE DATABASE IF NOT EXISTS qooerp_permission
--     WITH ENCODING = 'UTF8'
--     TEMPLATE = template0
--     LC_COLLATE = 'zh_CN.UTF-8'
--     LC_CTYPE = 'zh_CN.UTF-8';

-- ================================================
-- 2. 创建permission_role角色表
-- ================================================
DROP TABLE IF EXISTS permission_role CASCADE;

CREATE TABLE permission_role (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    role_type SMALLINT NOT NULL DEFAULT 2,
    data_scope SMALLINT NOT NULL DEFAULT 1,
    dept_ids VARCHAR(500),
    status SMALLINT NOT NULL DEFAULT 1,
    sort INTEGER NOT NULL DEFAULT 0,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50),
    deleted SMALLINT NOT NULL DEFAULT 0
);

-- 添加表注释
COMMENT ON TABLE permission_role IS '角色表';
COMMENT ON COLUMN permission_role.id IS '角色ID';
COMMENT ON COLUMN permission_role.tenant_id IS '租户ID';
COMMENT ON COLUMN permission_role.role_name IS '角色名称';
COMMENT ON COLUMN permission_role.role_code IS '角色编码';
COMMENT ON COLUMN permission_role.role_type IS '角色类型：1-系统角色 2-业务角色';
COMMENT ON COLUMN permission_role.data_scope IS '数据权限范围：1-全部 2-部门 3-部门及子部门 4-本人 5-自定义';
COMMENT ON COLUMN permission_role.dept_ids IS '自定义部门ID';
COMMENT ON COLUMN permission_role.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN permission_role.sort IS '排序号';
COMMENT ON COLUMN permission_role.remark IS '备注';
COMMENT ON COLUMN permission_role.create_time IS '创建时间';
COMMENT ON COLUMN permission_role.create_by IS '创建人';
COMMENT ON COLUMN permission_role.update_time IS '更新时间';
COMMENT ON COLUMN permission_role.update_by IS '更新人';
COMMENT ON COLUMN permission_role.deleted IS '删除标记：0-未删除 1-已删除';

-- 创建索引
CREATE UNIQUE INDEX uk_permission_role_code ON permission_role(role_code) WHERE deleted = 0;
CREATE INDEX idx_permission_tenant_id ON permission_role(tenant_id, deleted);
CREATE INDEX idx_permission_role_type ON permission_role(role_type, deleted);

-- ================================================
-- 3. 创建permission_menu菜单表
-- ================================================
DROP TABLE IF EXISTS permission_menu CASCADE;

CREATE TABLE permission_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type VARCHAR(1) NOT NULL,
    path VARCHAR(200),
    component VARCHAR(200),
    permission VARCHAR(100),
    icon VARCHAR(100),
    sort INTEGER DEFAULT 0,
    visible SMALLINT NOT NULL DEFAULT 1,
    is_cache SMALLINT NOT NULL DEFAULT 0,
    is_frame SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

-- 添加表注释
COMMENT ON TABLE permission_menu IS '菜单表';
COMMENT ON COLUMN permission_menu.id IS '菜单ID';
COMMENT ON COLUMN permission_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN permission_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN permission_menu.menu_type IS '菜单类型：M-目录 C-菜单 F-按钮';
COMMENT ON COLUMN permission_menu.path IS '路由路径';
COMMENT ON COLUMN permission_menu.component IS '组件路径';
COMMENT ON COLUMN permission_menu.permission IS '权限标识';
COMMENT ON COLUMN permission_menu.icon IS '图标';
COMMENT ON COLUMN permission_menu.sort IS '排序号';
COMMENT ON COLUMN permission_menu.visible IS '是否显示：0-隐藏 1-显示';
COMMENT ON COLUMN permission_menu.is_cache IS '是否缓存：0-不缓存 1-缓存';
COMMENT ON COLUMN permission_menu.is_frame IS '是否外链：0-否 1-是';
COMMENT ON COLUMN permission_menu.remark IS '备注';
COMMENT ON COLUMN permission_menu.create_time IS '创建时间';
COMMENT ON COLUMN permission_menu.create_by IS '创建人';
COMMENT ON COLUMN permission_menu.update_time IS '更新时间';
COMMENT ON COLUMN permission_menu.update_by IS '更新人';

-- 创建索引
CREATE INDEX idx_permission_parent_id ON permission_menu(parent_id);
CREATE INDEX idx_permission_menu_type ON permission_menu(menu_type);
CREATE INDEX idx_permission_sort ON permission_menu(parent_id, sort);

-- ================================================
-- 4. 创建permission_role_menu角色菜单关联表
-- ================================================
DROP TABLE IF EXISTS permission_role_menu CASCADE;

CREATE TABLE permission_role_menu (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL
);

-- 添加表注释
COMMENT ON TABLE permission_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN permission_role_menu.id IS '主键ID';
COMMENT ON COLUMN permission_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN permission_role_menu.menu_id IS '菜单ID';

-- 创建唯一索引
CREATE UNIQUE INDEX uk_permission_role_menu ON permission_role_menu(role_id, menu_id);

-- ================================================
-- 5. 创建permission_user_role用户角色关联表
-- ================================================
DROP TABLE IF EXISTS permission_user_role CASCADE;

CREATE TABLE permission_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

-- 添加表注释
COMMENT ON TABLE permission_user_role IS '用户角色关联表';
COMMENT ON COLUMN permission_user_role.id IS '主键ID';
COMMENT ON COLUMN permission_user_role.user_id IS '用户ID';
COMMENT ON COLUMN permission_user_role.role_id IS '角色ID';

-- 创建索引
CREATE UNIQUE INDEX uk_permission_user_role ON permission_user_role(user_id, role_id);
CREATE INDEX idx_permission_user_id ON permission_user_role(user_id);

-- ================================================
-- 6. 初始化数据
-- ================================================

-- 插入默认角色
INSERT INTO permission_role (
    tenant_id, role_name, role_code, role_type, data_scope, status, sort, create_by, update_by
) VALUES
(0, '超级管理员', 'ADMIN', 1, 1, 1, 1, 'system', 'system'),
(0, '普通用户', 'USER', 2, 4, 1, 2, 'system', 'system');

-- 插入默认菜单（一级菜单）
INSERT INTO permission_menu (parent_id, menu_name, menu_type, path, icon, sort, visible, create_by, update_by) VALUES
(0, '系统管理', 'M', '/system', 'system', 1, 1, 'system', 'system'),
(0, '权限管理', 'M', '/permission', 'permission', 2, 1, 'system', 'system'),
(0, '用户管理', 'M', '/user', 'user', 3, 1, 'system', 'system');

-- 插入默认菜单（二级菜单）
INSERT INTO permission_menu (parent_id, menu_name, menu_type, path, permission, icon, sort, visible, create_by, update_by) VALUES
(1, '角色管理', 'C', '/system/role', 'system:role:list', 'role', 1, 1, 'system', 'system'),
(1, '菜单管理', 'C', '/system/menu', 'system:menu:list', 'menu', 2, 1, 'system', 'system'),
(2, '用户管理', 'C', '/permission/user', 'permission:user:list', 'user', 1, 1, 'system', 'system'),
(2, '角色分配', 'C', '/permission/role', 'permission:role:assign', 'role', 2, 1, 'system', 'system');

-- 插入默认按钮权限
INSERT INTO permission_menu (parent_id, menu_name, menu_type, permission, sort, visible, create_by, update_by) VALUES
(4, '新增角色', 'F', 'system:role:create', 1, 0, 'system', 'system'),
(4, '编辑角色', 'F', 'system:role:update', 2, 0, 'system', 'system'),
(4, '删除角色', 'F', 'system:role:delete', 3, 0, 'system', 'system'),
(4, '分配权限', 'F', 'system:role:assign', 4, 0, 'system', 'system'),
(5, '新增菜单', 'F', 'system:menu:create', 1, 0, 'system', 'system'),
(5, '编辑菜单', 'F', 'system:menu:update', 2, 0, 'system', 'system'),
(5, '删除菜单', 'F', 'system:menu:delete', 3, 0, 'system', 'system'),
(6, '分配角色', 'F', 'permission:user:assign', 1, 0, 'system', 'system'),
(6, '移除角色', 'F', 'permission:user:remove', 2, 0, 'system', 'system'),
(7, '查看角色', 'F', 'permission:role:view', 1, 0, 'system', 'system');

-- 为超级管理员角色分配所有菜单
INSERT INTO permission_role_menu (role_id, menu_id)
SELECT
    (SELECT id FROM permission_role WHERE role_code = 'ADMIN'),
    id
FROM permission_menu;

-- 为超级管理员分配给admin用户（假设admin用户ID为1）
INSERT INTO permission_user_role (user_id, role_id)
VALUES (1, (SELECT id FROM permission_role WHERE role_code = 'ADMIN'));

-- ================================================
-- 7. 创建视图（可选）
-- ================================================

-- 角色菜单权限视图
CREATE OR REPLACE VIEW v_permission_role_menu AS
SELECT
    r.id AS role_id,
    r.role_name,
    r.role_code,
    m.id AS menu_id,
    m.menu_name,
    m.menu_type,
    m.permission
FROM permission_role r
LEFT JOIN permission_role_menu rm ON r.id = rm.role_id
LEFT JOIN permission_menu m ON rm.menu_id = m.id
WHERE r.deleted = 0;

COMMENT ON VIEW v_permission_role_menu IS '角色菜单权限视图';

-- ================================================
-- 8. 创建函数（可选）
-- ================================================

-- 获取用户的角色列表
CREATE OR REPLACE FUNCTION get_user_roles(p_user_id BIGINT)
RETURNS TABLE(role_id BIGINT, role_code VARCHAR, role_name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.id,
        r.role_code,
        r.role_name
    FROM permission_user_role ur
    INNER JOIN permission_role r ON ur.role_id = r.id
    WHERE ur.user_id = p_user_id AND r.deleted = 0 AND r.status = 1;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION get_user_roles IS '获取用户的角色列表';

-- 获取角色的菜单ID列表
CREATE OR REPLACE FUNCTION get_role_menus(p_role_id BIGINT)
RETURNS TABLE(menu_id BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT DISTINCT menu_id
    FROM permission_role_menu
    WHERE role_id = p_role_id;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION get_role_menus IS '获取角色的菜单ID列表';

-- ================================================
-- 9. 创建触发器（可选）
-- ================================================

-- 更新时间触发器
CREATE OR REPLACE FUNCTION update_time_trigger()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为permission_role表创建更新时间触发器
DROP TRIGGER IF EXISTS trg_permission_role_update_time ON permission_role;
CREATE TRIGGER trg_permission_role_update_time
    BEFORE UPDATE ON permission_role
    FOR EACH ROW
    EXECUTE FUNCTION update_time_trigger();

-- 为permission_menu表创建更新时间触发器
DROP TRIGGER IF EXISTS trg_permission_menu_update_time ON permission_menu;
CREATE TRIGGER trg_permission_menu_update_time
    BEFORE UPDATE ON permission_menu
    FOR EACH ROW
    EXECUTE FUNCTION update_time_trigger();

-- ================================================
-- 10. 创建存储过程（可选）
-- ================================================

-- 批量为用户分配角色
CREATE OR REPLACE PROCEDURE batch_assign_user_roles(p_user_id BIGINT, p_role_ids BIGINT[])
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM permission_user_role WHERE user_id = p_user_id;

    IF p_role_ids IS NOT NULL THEN
        INSERT INTO permission_user_role (user_id, role_id)
        SELECT p_user_id, unnest(p_role_ids);
    END IF;
END;
$$;

COMMENT ON PROCEDURE batch_assign_user_roles IS '批量为用户分配角色';

-- ================================================
-- 11. 授权（根据实际环境调整）
-- ================================================

-- 授予只读权限（只读用户）
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;

-- 授予读写权限（应用用户）
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- ================================================
-- 12. 验证数据
-- ================================================

-- 查询角色数据
SELECT id, role_name, role_code, role_type, status FROM permission_role WHERE deleted = 0;

-- 查询菜单数据
SELECT id, parent_id, menu_name, menu_type, path, icon, sort FROM permission_menu ORDER BY parent_id, sort;

-- 查询用户角色关联
SELECT user_id, role_id FROM permission_user_role;

-- 查询角色菜单关联
SELECT role_id, COUNT(*) as menu_count FROM permission_role_menu GROUP BY role_id;

-- ================================================
-- 13. 性能优化建议
-- ================================================

-- 定期执行VACUUM和ANALYZE
-- VACUUM ANALYZE permission_role;
-- VACUUM ANALYZE permission_menu;
-- VACUUM ANALYZE permission_role_menu;
-- VACUUM ANALYZE permission_user_role;

-- ================================================
-- 脚本结束
-- ================================================
