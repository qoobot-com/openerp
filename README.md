# QooERP

> 适用于大、中、小企业、政府机构的轻量级微服务架构开源ERP系统

## 项目简介

QooERP 是一款基于现代云原生技术构建的大型企业级开源ERP系统，采用DDD领域驱动设计和微服务架构，支持多租户、多语言、多币种，专为**大型企业、政府机构、科技企业**设计，支持**百万级用户规模**，同时兼容中小型企业。

## 核心特性

- **模块化架构**：30+核心模块，按需启用，灵活组合
- **多租户支持**：行隔离多租户架构，支持单实例多组织
- **全球化能力**：100+语言、100+币种、多时区支持
- **高可扩展性**：微服务架构，支持水平扩展至百万级用户
- **云原生部署**：支持Docker一键部署、K8s集群部署、多区域部署
- **企业级安全**：RBAC+ABAC复合权限模型，数据加密，审计日志，合规报告
- **分布式架构**：分库分表、读写分离、分布式事务、分布式缓存
- **高可用保障**：服务熔断、限流降级、多实例负载均衡

## 技术栈

### 前端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.x | 渐进式前端框架 |
| Element Plus | latest | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Pinia | latest | 状态管理 |
| Vite | 5.x | 构建工具 |
| TypeScript | 5.x | 类型安全 |

### 后端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.6 | 微服务框架 |
| Spring Cloud | 2023.0.3 | 微服务治理 |
| Spring Cloud Alibaba | 2023.0.1.2 | 微服务套件 |
| Nacos | 2.x | 服务注册与配置中心 |
| Spring Cloud Gateway | 4.x | API网关 |
| Sentinel | 1.x | 服务熔断限流 |
| OpenFeign | 4.x | 声明式HTTP客户端 |
| Spring Cache | 3.x | 缓存抽象 |
| Caffeine | 3.1.8 | L1本地缓存 |
| Redis | 7.x | L2分布式缓存 |
| Redisson | 3.29.0 | 分布式锁与高级缓存 |
| PostgreSQL | 15+ | 主数据库 |
| ShardingSphere | 5.5.1 | 分库分表中间件 |
| Seata | 2.0.0 | 分布式事务 |
| RocketMQ | 5.x | 消息队列 |
| Flowable | 7.x | 工作流引擎 |

### 开发工具
| 工具 | 说明 |
|------|------|
| Nginx | 1.24+ | 前端静态资源服务器 |
| Docker | 容器化 |
| Kubernetes | 容器编排 |
| Maven | 依赖管理 |
| Flyway | 数据库迁移 |
| Git | 版本控制 |

## 项目结构

```
qooerp/
├── 01-docs/                  # 文档目录
│   ├── 01-开源ERP千功能规划.md
│   ├── 02-QooERP开发模块进度跟踪.md
│   ├── 04-大型开源ERP系统开发准备工作清单.md
│   └── 4-总体架构清单.md
├── qooerp-gateway/          # API网关
├── qooerp-auth/             # 认证服务
├── qooerp-system/           # 系统管理
├── qooerp-finance/          # 财务管理
├── qooerp-hr/               # 人力资源
├── qooerp-scm/              # 供应链管理
├── qooerp-sales/            # 销售管理
├── qooerp-inventory/        # 库存管理
├── qooerp-frontend/         # 前端应用
└── pom.xml                  # Maven父工程
```

## 核心模块

### P0级：核心基础模块（必选）
1. 身份认证与安全
2. 系统管理
3. 全球化支持
4. 数据管理
5. 系统集成
6. 协同办公
7. 任务与工作流
8. 通讯系统

### P1级：核心业务模块（核心业务必须）
1. 财务管理
2. 人力资源管理
3. 供应链管理
4. 销售管理
5. 库存管理

### P2级：扩展业务模块（按需开发）
1. 生产管理
2. 项目管理
3. 客户关系管理（CRM）
4. 服务管理
5. 移动应用

### P3级：高级功能模块（增强能力）
1. 报表与数据分析
2. 业务流程管理
3. 低代码平台
4. AI智能助手

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- PostgreSQL 15+
- Redis 7+
- RocketMQ 5.x
- Nacos 2.x
- Maven 3.9+

### 后端启动

```bash
# 克隆项目
git clone https://github.com/qooerp/qooerp.git
cd qooerp

# 启动基础服务（PostgreSQL、Redis、RocketMQ、Nacos）
docker-compose up -d

# 启动后端服务
cd qooerp-auth && mvn spring-boot:run
cd qooerp-system && mvn spring-boot:run
# ... 其他服务
```

### 前端启动

```bash
cd qooerp-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问 http://localhost:3000 查看前端应用。

## 部署方案

### 前端部署
**推荐方案：Nginx独立部署**

**部署架构：**
```
Nginx (静态资源+反向代理)
    ↓
Spring Boot API (后端服务)
    ↓
Redis/MySQL/RocketMQ等中间件
```

**Nginx配置示例：**
```nginx
server {
    listen 80;
    server_name erp.example.com;

    # 前端静态资源
    root /var/www/qooerp/frontend/dist;
    index index.html;

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API代理到后端
    location /api/ {
        proxy_pass http://qooerp-gateway:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # SPA路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

**优势：**
- 高性能，专业静态资源服务器
- 支持CDN加速，适合百万级用户
- 支持多实例负载均衡
- 完美动静分离

### 小微企业部署
- **部署方式**：Docker一键部署
- **并发能力**：100-500用户
- **架构特点**：单体服务 + PostgreSQL + Redis + Nginx前端
- **适用场景**：小规模团队，快速上线

### 中型企业部署
- **部署方式**：微服务集群部署
- **并发能力**：500-5000用户
- **架构特点**：微服务集群 + L1/L2缓存 + 分库分表 + Nginx前端
- **适用场景**：中等规模，需要完整业务模块

### 大型企业部署
- **部署方式**：K8s集群部署
- **并发能力**：5000-50000用户
- **架构特点**：完整微服务 + 分布式缓存 + 分布式事务 + MQ + Nginx集群
- **适用场景**：大规模组织，复杂业务流程

### 集团/科技企业部署
- **部署方式**：K8s多区域部署
- **并发能力**：50000-1000000+用户
- **架构特点**：多机房部署 + 多级缓存 + 异地多活 + CDN加速 + 监控告警
- **适用场景**：跨地域，多组织架构，科技企业，政府机构

## 文档

- [功能规划文档](./01-docs/01-开源ERP千功能规划.md)
- [模块进度跟踪](./01-docs/02-QooERP开发模块进度跟踪.md)
- [开发准备工作清单](01-docs/05-大型开源ERP系统开发准备工作清单.md)
- [总体架构清单](01-docs/04-总体架构清单.md)

## 开发计划

| 阶段 | 里程碑 | 预计完成时间 | 状态 |
|------|--------|-------------|------|
| 第一阶段 | P0核心基础模块完成 | 2026-Q3 | 未开始 |
| 第二阶段 | P1核心业务模块完成 | 2026-Q4 | 未开始 |
| 第三阶段 | P2扩展业务模块完成 | 2027-Q1 | 未开始 |
| 第四阶段 | P3高级功能模块完成 | 2027-Q2 | 未开始 |
| 第五阶段 | 全量测试与优化 | 2027-Q3 | 未开始 |
| 第六阶段 | 正式发布 | 2027-Q4 | 未开始 |

## 贡献指南

欢迎贡献代码、报告问题或提出建议！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 开源协议

本项目采用 [Apache License 2.0](./LICENSE) 开源协议。

## 联系方式

- 官网：https://qooerp.com
- 文档：https://docs.qooerp.com
- 邮箱：contact@qooerp.com

---

**QooERP** - 开源，让企业管理更简单！
