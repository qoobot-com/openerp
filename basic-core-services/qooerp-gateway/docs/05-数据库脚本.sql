-- ================================================
-- QooERP 网关服务数据库初始化脚本
-- 数据库: qooerp_gateway
-- 版本: 1.0.0
-- 创建日期: 20xx-xx-xx
-- ================================================

-- ================================================
-- 1. 创建gateway_route路由表
-- ================================================
DROP TABLE IF EXISTS gateway_route CASCADE;

CREATE TABLE gateway_route (
    id BIGSERIAL PRIMARY KEY,
    route_id VARCHAR(100) NOT NULL,
    route_name VARCHAR(100) NOT NULL,
    uri VARCHAR(500) NOT NULL,
    predicates TEXT,
    filters TEXT,
    order_num INTEGER DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

CREATE UNIQUE INDEX uk_gateway_route_id ON gateway_route(route_id);

COMMENT ON TABLE gateway_route IS '路由表';
COMMENT ON COLUMN gateway_route.id IS '路由ID';
COMMENT ON COLUMN gateway_route.route_id IS '路由ID（唯一）';
COMMENT ON COLUMN gateway_route.route_name IS '路由名称';
COMMENT ON COLUMN gateway_route.uri IS '目标URI';
COMMENT ON COLUMN gateway_route.predicates IS '断言配置（JSON）';
COMMENT ON COLUMN gateway_route.filters IS '过滤器配置（JSON）';
COMMENT ON COLUMN gateway_route.order_num IS '排序号';
COMMENT ON COLUMN gateway_route.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN gateway_route.remark IS '备注';

-- ================================================
-- 2. 创建gateway_filter过滤器表
-- ================================================
DROP TABLE IF EXISTS gateway_filter CASCADE;

CREATE TABLE gateway_filter (
    id BIGSERIAL PRIMARY KEY,
    filter_name VARCHAR(100) NOT NULL,
    filter_class VARCHAR(200) NOT NULL,
    filter_order INTEGER DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(50)
);

COMMENT ON TABLE gateway_filter IS '过滤器表';
COMMENT ON COLUMN gateway_filter.id IS '过滤器ID';
COMMENT ON COLUMN gateway_filter.filter_name IS '过滤器名称';
COMMENT ON COLUMN gateway_filter.filter_class IS '过滤器类名';
COMMENT ON COLUMN gateway_filter.filter_order IS '过滤器顺序';
COMMENT ON COLUMN gateway_filter.status IS '状态：0-禁用 1-启用';
COMMENT ON COLUMN gateway_filter.remark IS '备注';

-- ================================================
-- 3. 创建gateway_blacklist黑名单表
-- ================================================
DROP TABLE IF EXISTS gateway_blacklist CASCADE;

CREATE TABLE gateway_blacklist (
    id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(50) NOT NULL,
    blacklist_type SMALLINT NOT NULL DEFAULT 1,
    expire_time TIMESTAMP,
    reason VARCHAR(500),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50)
);

CREATE INDEX idx_gateway_blacklist_ip ON gateway_blacklist(ip_address);
CREATE INDEX idx_gateway_blacklist_expire ON gateway_blacklist(expire_time);

COMMENT ON TABLE gateway_blacklist IS '黑名单表';
COMMENT ON COLUMN gateway_blacklist.id IS '主键ID';
COMMENT ON COLUMN gateway_blacklist.ip_address IS 'IP地址';
COMMENT ON COLUMN gateway_blacklist.blacklist_type IS '黑名单类型：1-永久 2-临时';
COMMENT ON COLUMN gateway_blacklist.expire_time IS '过期时间';
COMMENT ON COLUMN gateway_blacklist.reason IS '原因';

-- ================================================
-- 4. 初始化数据
-- ================================================

-- 插入默认路由
INSERT INTO gateway_route (route_id, route_name, uri, predicates, order_num, create_by) VALUES
('qooerp-auth', '认证服务', 'lb://qooerp-auth-service', '[{"name":"Path","args":{"pattern":"/api/auth/**"}}]', 1, 'system'),
('qooerp-user', '用户服务', 'lb://qooerp-user-service', '[{"name":"Path","args":{"pattern":"/api/user/**"}}]', 2, 'system'),
('qooerp-permission', '权限服务', 'lb://qooerp-permission-service', '[{"name":"Path","args":{"pattern":"/api/permission/**"}}]', 3, 'system'),
('qooerp-system', '系统管理', 'lb://qooerp-system-service', '[{"name":"Path","args":{"pattern":"/api/system/**"}}]', 4, 'system'),
('qooerp-organization', '组织管理', 'lb://qooerp-organization-service', '[{"name":"Path","args":{"pattern":"/api/organization/**"}}]', 5, 'system');

-- ================================================
-- 脚本结束
-- ================================================
