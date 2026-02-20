-- QooERP 权限服务数据库初始化脚本
-- 版本: 1.0.0
-- 创建日期: 20xx-xx-xx

-- ========================================
-- 1. 角色表
-- ========================================
CREATE TABLE IF NOT EXISTS permission_role (
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

-- 创建索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_permission_role_code ON permission_role(role_code) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_permission_tenant_id ON permission_role(tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_permission_role_type ON permission_role(role_type, deleted);

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
COMMENT ON COLUMN permission_role.deleted IS '删除标记：0-未删除 1-已删除';

-- ========================================
-- 2. 菜单表
-- ========================================
CREATE TABLE IF NOT EXISTS permission_menu (
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

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_permission_parent_id ON permission_menu(parent_id);
CREATE INDEX IF NOT EXISTS idx_permission_menu_type ON permission_menu(menu_type);
CREATE INDEX IF NOT EXISTS idx_permission_sort ON permission_menu(parent_id, sort);

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

-- ========================================
-- 3. 角色菜单关联表
-- ========================================
CREATE TABLE IF NOT EXISTS permission_role_menu (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL
);

-- 创建唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_permission_role_menu ON permission_role_menu(role_id, menu_id);

-- 添加表注释
COMMENT ON TABLE permission_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN permission_role_menu.id IS '主键ID';
COMMENT ON COLUMN permission_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN permission_role_menu.menu_id IS '菜单ID';

-- ========================================
-- 4. 用户角色关联表
-- ========================================
CREATE TABLE IF NOT EXISTS permission_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

-- 创建索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_permission_user_role ON permission_user_role(user_id, role_id);
CREATE INDEX IF NOT EXISTS idx_permission_user_id ON permission_user_role(user_id);

-- 添加表注释
COMMENT ON TABLE permission_user_role IS '用户角色关联表';
COMMENT ON COLUMN permission_user_role.id IS '主键ID';
COMMENT ON COLUMN permission_user_role.user_id IS '用户ID';
COMMENT ON COLUMN permission_user_role.role_id IS '角色ID';

-- ========================================
-- 5. 初始化数据
-- ========================================
-- 插入默认管理员角色
INSERT INTO permission_role (
    tenant_id, role_name, role_code, role_type, data_scope, status, create_by, update_by
) VALUES (
    0,
    '超级管理员',
    'ADMIN',
    1,
    1,
    1,
    'system',
    'system'
) ON CONFLICT DO NOTHING;

-- 插入默认普通用户角色
INSERT INTO permission_role (
    tenant_id, role_name, role_code, role_type, data_scope, status, create_by, update_by
) VALUES (
    0,
    '普通用户',
    'USER',
    2,
    4,
    1,
    'system',
    'system'
) ON CONFLICT DO NOTHING;

-- 插入默认菜单（一级菜单）
INSERT INTO permission_menu (parent_id, menu_name, menu_type, path, icon, sort, visible, create_by, update_by) VALUES
(0, '系统管理', 'M', '/system', 'system', 1, 1, 'system', 'system'),
(0, '权限管理', 'M', '/permission', 'permission', 2, 1, 'system', 'system'),
(0, '用户管理', 'M', '/user', 'user', 3, 1, 'system', 'system')
ON CONFLICT DO NOTHING;

-- 插入默认菜单（二级菜单）
INSERT INTO permission_menu (parent_id, menu_name, menu_type, path, icon, sort, visible, create_by, update_by) VALUES
(1, '角色管理', 'C', '/system/role', 'role', 1, 1, 'system', 'system'),
(1, '菜单管理', 'C', '/system/menu', 'menu', 2, 1, 'system', 'system'),
(2, '用户管理', 'C', '/permission/user', 'user', 1, 1, 'system', 'system')
ON CONFLICT DO NOTHING;

-- 插入默认菜单（按钮权限）
INSERT INTO permission_menu (parent_id, menu_name, menu_type, path, permission, sort, visible, create_by, update_by) VALUES
(4, '新增角色', 'F', '', 'system:role:add', 1, 1, 'system', 'system'),
(4, '编辑角色', 'F', '', 'system:role:edit', 2, 1, 'system', 'system'),
(4, '删除角色', 'F', '', 'system:role:delete', 3, 1, 'system', 'system'),
(4, '角色授权', 'F', '', 'system:role:auth', 4, 1, 'system', 'system'),
(5, '新增菜单', 'F', '', 'system:menu:add', 1, 1, 'system', 'system'),
(5, '编辑菜单', 'F', '', 'system:menu:edit', 2, 1, 'system', 'system'),
(5, '删除菜单', 'F', '', 'system:menu:delete', 3, 1, 'system', 'system')
ON CONFLICT DO NOTHING;

-- 为超级管理员角色分配所有菜单
INSERT INTO permission_role_menu (role_id, menu_id)
SELECT
    (SELECT id FROM permission_role WHERE role_code = 'ADMIN' LIMIT 1),
    id
FROM permission_menu
WHERE NOT EXISTS (
    SELECT 1 FROM permission_role_menu
    WHERE role_id = (SELECT id FROM permission_role WHERE role_code = 'ADMIN' LIMIT 1)
    AND menu_id = permission_menu.id
);
