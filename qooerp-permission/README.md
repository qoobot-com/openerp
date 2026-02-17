# QooERP 权限服务 (qooerp-permission)

## 模块简介

qooerp-permission 是 QooERP 系统的权限管理服务，基于 RBAC（Role-Based Access Control）模型实现，提供完整的权限管理功能。

## 技术栈

- Spring Boot 3.2.x
- MyBatis-Plus 3.5.x
- PostgreSQL 15+
- Redis 7.x
- Spring Cloud OpenFeign
- Nacos (服务发现与配置中心)
- Flyway (数据库版本管理)
- Swagger (API文档)

## 功能特性

### 1. 角色管理
- 角色的增删改查
- 角色类型区分（系统角色、业务角色）
- 数据权限范围配置（全部、部门、部门及子部门、本人、自定义）
- 角色状态管理（启用/禁用）
- 角色排序

### 2. 菜单管理
- 菜单的增删改查
- 菜单类型区分（目录、菜单、按钮）
- 菜单树结构管理
- 路由路径和组件配置
- 权限标识配置
- 菜单显示状态控制

### 3. 权限验证
- 基于角色的权限验证
- 超级管理员特殊处理
- 菜单权限过滤
- 按钮权限控制

### 4. 数据权限
- 全部数据
- 本部门数据
- 本部门及子部门数据
- 仅本人数据
- 自定义数据范围

### 5. 缓存管理
- Redis 缓存支持
- 菜单树缓存
- 用户权限缓存
- 缓存自动清理

## 项目结构

```
qooerp-permission/
├── qooerp-permission-service/      # 业务逻辑模块
│   ├── src/main/java/com/qoobot/qooerp/permission/
│   │   ├── config/                  # 配置类
│   │   ├── controller/              # 控制器
│   │   ├── dto/                     # 数据传输对象
│   │   ├── entity/                  # 实体类
│   │   ├── enums/                   # 枚举类
│   │   ├── exception/               # 异常类
│   │   ├── mapper/                  # 数据访问层
│   │   ├── service/                 # 服务层
│   │   │   └── impl/               # 服务实现类
│   │   ├── util/                    # 工具类
│   │   └── vo/                      # 视图对象
│   └── pom.xml
├── qooerp-permission-start/          # 启动模块
│   ├── src/main/java/com/qoobot/qooerp/permission/
│   │   └── PermissionApplication.java
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/           # 数据库迁移脚本
└── docs/                            # 文档目录
```

## 快速开始

### 1. 数据库初始化

创建数据库：
```sql
CREATE DATABASE qooerp_permission;
```

启动服务会自动执行 Flyway 迁移脚本创建表结构。

### 2. 配置修改

修改 `application.yml` 中的配置：
- 数据库连接信息
- Redis 连接信息
- Nacos 地址

### 3. 启动服务

```bash
mvn clean package
java -jar qooerp-permission-start/target/qooerp-permission-start-1.0.0-SNAPSHOT.jar
```

### 4. 访问地址

- 服务地址: http://localhost:8083
- Swagger文档: http://localhost:8083/swagger-ui.html

## API 接口

### 角色管理
- `POST /api/permission/role` - 创建角色
- `PUT /api/permission/role` - 更新角色
- `DELETE /api/permission/role/{id}` - 删除角色
- `GET /api/permission/role/{id}` - 查询角色详情
- `GET /api/permission/role/page` - 分页查询角色
- `GET /api/permission/role/list` - 获取所有角色
- `POST /api/permission/role/{roleId}/menus` - 分配菜单给角色
- `GET /api/permission/role/{roleId}/menus` - 获取角色的菜单ID列表

### 菜单管理
- `POST /api/permission/menu` - 创建菜单
- `PUT /api/permission/menu` - 更新菜单
- `DELETE /api/permission/menu/{id}` - 删除菜单
- `GET /api/permission/menu/{id}` - 查询菜单详情
- `GET /api/permission/menu/list` - 获取所有菜单
- `GET /api/permission/menu/tree` - 获取菜单树
- `GET /api/permission/menu/tree/role/{roleId}` - 获取角色的菜单树

### 用户权限管理
- `POST /api/permission/user/roles` - 分配角色给用户
- `DELETE /api/permission/user/{userId}/roles` - 删除用户的所有角色
- `GET /api/permission/user/{userId}/roles` - 获取用户的角色列表
- `GET /api/permission/user/{userId}/menus` - 获取用户的菜单树
- `GET /api/permission/user/{userId}/check` - 检查用户权限

### 权限验证
- `GET /api/permission/check` - 验证用户权限
- `GET /api/permission/permissions` - 获取当前用户权限列表
- `GET /api/permission/menu/tree` - 获取当前用户菜单树
- `POST /api/permission/refresh/{userId}` - 刷新用户权限缓存
- `POST /api/permission/cache/clear` - 清除所有权限缓存

## 数据权限范围说明

| 值 | 名称 | 说明 |
|----|------|------|
| 1 | 全部数据 | 可查看所有数据 |
| 2 | 本部门数据 | 仅查看本部门数据 |
| 3 | 本部门及子部门 | 查看本部门及子部门数据 |
| 4 | 仅本人 | 仅查看自己创建的数据 |
| 5 | 自定义数据 | 自定义部门数据范围 |

## 菜单类型说明

| 值 | 名称 | 说明 |
|----|------|------|
| M | 目录 | 一级菜单，下级菜单的父节点 |
| C | 菜单 | 实际功能页面 |
| F | 按钮 | 页面内的操作按钮 |

## 注意事项

1. 系统角色不能删除
2. 系统角色不能修改角色类型
3. 删除菜单前需先删除子菜单
4. 缓存会自动清理，也可手动调用API清理

## 开发进度

详见 [docs/06-开发进度.md](./docs/06-开发进度.md)

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-02-17 | 初始版本 |

## 许可证

Copyright © 2026 QooERP Team
