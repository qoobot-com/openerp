# qooerp-auth 认证服务文档

> 模块版本：1.0.0-SNAPSHOT
> 创建日期：2026-02-17

---

## 文档目录

| 序号 | 文档名称 | 说明 |
|------|---------|------|
| 1 | [01-业务设计.md](./01-业务设计.md) | 业务流程、业务规则、异常处理 |
| 2 | [02-应用设计.md](./02-应用设计.md) | 应用架构、包结构、核心类设计 |
| 3 | [03-数据设计.md](./03-数据设计.md) | 数据库设计、表结构、索引设计 |
| 4 | [04-API接口文档.md](./04-API接口文档.md) | API接口定义、OpenAPI YAML |
| 5 | [05-数据库脚本.sql](./05-数据库脚本.sql) | 数据库初始化脚本 |

---

## 快速开始

### 1. 初始化数据库

```bash
# 连接到PostgreSQL
psql -U postgres -d qooerp_auth

# 执行初始化脚本
\i docs/05-数据库脚本.sql
```

### 2. 配置应用

编辑 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/qooerp_auth
    username: qooerp
    password: qooerp123
```

### 3. 启动应用

```bash
mvn clean install
cd qooerp-auth-start
mvn spring-boot:run
```

### 4. 测试接口

```bash
# 用户登录
curl -X POST http://localhost:8081/auth/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 模块依赖

```
qooerp-auth
├── qooerp-common       (公共模块)
├── Spring Security     (安全框架)
├── Spring Data Redis   (Redis集成)
└── JWT                 (令牌生成)
```

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.6 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| PostgreSQL | 15+ | 数据库 |
| Redis | 7.x | 缓存 |
| JWT | 0.12.x | 令牌生成 |

---

## 开发进度

| 功能模块 | 子功能项 | 状态 | 完成度 |
|---------|---------|------|--------|
| 用户登录 | 用户名密码登录 | 进行中 | 50% |
| JWT令牌 | 令牌生成 | 已完成 | 100% |
| JWT令牌 | 令牌验证 | 已完成 | 100% |
| JWT令牌 | 令牌刷新 | 已完成 | 100% |
| JWT令牌 | 令牌吊销 | 已完成 | 100% |
| 登录安全 | 登录失败限制 | 进行中 | 30% |

**模块总体进度：5/18 (28%)**

---

## 联系方式

- 文档维护：QooERP开发团队
- 问题反馈：提交Issue
- 技术支持：support@qooerp.com
