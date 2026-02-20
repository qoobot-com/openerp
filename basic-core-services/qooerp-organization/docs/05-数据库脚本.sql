-- ================================================
-- QooERP 组织管理数据库初始化脚本
-- 数据库: qooerp_organization
-- 版本: 1.0.0
-- 创建日期: 20xx-xx-xx
-- ================================================

-- ================================================
-- 1. 创建organization_dept部门表
-- ================================================
DROP TABLE IF EXISTS organization_dept CASCADE;

CREATE TABLE organization_dept (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    parent_id BIGINT DEFAULT 0,
    dept_name VARCHAR(100) NOT NULL,
    dept_code VARCHAR(50) NOT NULL,
    leader_id BIGINT,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200),
    sort INTEGER DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE UNIQUE INDEX uk_organization_dept_code ON organization_dept(dept_code);
CREATE INDEX idx_organization_parent_id ON organization_dept(parent_id);

COMMENT ON TABLE organization_dept IS '部门表';
COMMENT ON COLUMN organization_dept.id IS '部门ID';
COMMENT ON COLUMN organization_dept.tenant_id IS '租户ID';
COMMENT ON COLUMN organization_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN organization_dept.dept_name IS '部门名称';
COMMENT ON COLUMN organization_dept.dept_code IS '部门编码';
COMMENT ON COLUMN organization_dept.leader_id IS '负责人ID';
COMMENT ON COLUMN organization_dept.phone IS '联系电话';
COMMENT ON COLUMN organization_dept.email IS '邮箱';
COMMENT ON COLUMN organization_dept.address IS '地址';
COMMENT ON COLUMN organization_dept.sort IS '排序号';
COMMENT ON COLUMN organization_dept.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN organization_dept.remark IS '备注';

-- ================================================
-- 2. 创建organization_position岗位表
-- ================================================
DROP TABLE IF EXISTS organization_position CASCADE;

CREATE TABLE organization_position (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    position_name VARCHAR(100) NOT NULL,
    position_code VARCHAR(50) NOT NULL,
    position_level INTEGER DEFAULT 0,
    dept_id BIGINT,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE UNIQUE INDEX uk_organization_position_code ON organization_position(position_code);
CREATE INDEX idx_organization_dept_id ON organization_position(dept_id);

COMMENT ON TABLE organization_position IS '岗位表';
COMMENT ON COLUMN organization_position.id IS '岗位ID';
COMMENT ON COLUMN organization_position.tenant_id IS '租户ID';
COMMENT ON COLUMN organization_position.position_name IS '岗位名称';
COMMENT ON COLUMN organization_position.position_code IS '岗位编码';
COMMENT ON COLUMN organization_position.position_level IS '岗位级别';
COMMENT ON COLUMN organization_position.dept_id IS '所属部门ID';
COMMENT ON COLUMN organization_position.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN organization_position.remark IS '备注';

-- ================================================
-- 3. 初始化数据
-- ================================================

-- 插入默认部门
INSERT INTO organization_dept (dept_name, dept_code, parent_id, sort, create_by) VALUES
('总公司', 'HEADQUARTERS', 0, 1, 'system'),
('研发部', 'RD', 1, 1, 'system'),
('市场部', 'MARKETING', 1, 2, 'system'),
('销售部', 'SALES', 1, 3, 'system');

-- 插入默认岗位
INSERT INTO organization_position (position_name, position_code, position_level, create_by) VALUES
('总经理', 'CEO', 1, 'system'),
('技术总监', 'CTO', 2, 'system'),
('市场总监', 'CMO', 2, 'system'),
('销售总监', 'CSO', 2, 'system'),
('研发经理', 'RDM', 3, 'system'),
('销售经理', 'SM', 3, 'system');

-- ================================================
-- 脚本结束
-- ================================================
