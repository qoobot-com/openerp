# QooERP 工作流引擎模块 (qooerp-workflow)

> 基于 Flowable 7.0 的工作流引擎服务，提供完整的流程管理能力

## 模块概述

QooERP 工作流引擎是一个基于 Flowable 7.0 开发的工作流管理服务，为企业提供流程设计、流程执行、任务管理、表单管理、流程监控和消息通知等完整的流程管理能力。

## 模块结构

```
qooerp-workflow/
├── qooerp-workflow-service/      # 服务模块
│   └── src/main/java/com/qoobot/qooerp/workflow/
│       ├── config/              # 配置类
│       ├── constants/           # 常量类
│       ├── controller/          # 控制器
│       ├── dto/                 # 数据传输对象
│       ├── entity/              # 实体类
│       ├── enums/               # 枚举类
│       ├── mapper/              # 数据访问层
│       └── service/             # 服务接口
├── qooerp-workflow-start/       # 启动模块
│   └── src/main/
│       ├── java/com/qoobot/qooerp/workflow/
│       │   └── WorkflowApplication.java
│       └── resources/
│           ├── application.yml
│           └── migration/
│               └── V1.0.0__init_schema.sql
└── docs/                        # 设计文档
    ├── 01-业务设计.md
    ├── 02-应用设计.md
    ├── 03-数据设计.md
    ├── 04-API接口文档.md
    ├── 05-技术设计.md
    ├── 05-数据库脚本.sql
    └── 06-开发进度.md
```

## 核心功能

### 1. 流程定义管理
- 流程设计器集成
- 流程部署和发布
- 流程版本管理
- 流程导入导出
- 流程分类管理

### 2. 流程实例管理
- 流程启动和执行
- 流程挂起和恢复
- 流程撤回和转办
- 流程进度跟踪
- 流程日志查询

### 3. 任务管理
- 待办任务查询
- 已办任务查询
- 任务审批（通过/驳回）
- 任务转派和委派
- 任务加签和减签
- 会签和或签处理

### 4. 表单管理
- 动态表单设计
- 表单字段管理
- 表单数据存储
- 表单权限控制
- 表单验证规则

### 5. 流程监控
- 流程实例监控
- 流程统计分析
- 流程性能分析
- 超时任务监控
- 监控大屏

### 6. 消息通知
- 待办通知
- 审批通知
- 超时提醒
- 抄送通知
- 多渠道通知（系统消息、邮件、短信、微信、钉钉）

## 技术栈

- **框架**: Spring Boot 3.x
- **工作流引擎**: Flowable 7.0
- **数据库**: PostgreSQL 15+
- **ORM**: MyBatis-Plus 3.5.x
- **缓存**: Caffeine + Redis
- **服务注册/发现**: Nacos
- **配置中心**: Nacos Config
- **API文档**: SpringDoc OpenAPI

## 数据库表

| 表名 | 说明 |
|------|------|
| workflow_category | 流程分类表 |
| workflow_form | 流程表单表 |
| workflow_form_field | 表单字段表 |
| workflow_form_record | 表单数据记录表 |
| workflow_template | 流程模板表 |
| workflow_notification | 流程通知表 |
| workflow_counter | 流程统计表 |

## API 接口

### 流程定义管理
- `POST /workflow/definition/deploy` - 部署流程定义
- `GET /workflow/definition/page` - 分页查询流程定义
- `GET /workflow/definition/detail/{id}` - 查询流程定义详情
- `DELETE /workflow/definition/delete/{deploymentId}` - 删除流程定义
- `GET /workflow/definition/diagram/{id}` - 获取流程图

### 流程实例管理
- `POST /workflow/instance/start` - 启动流程实例
- `GET /workflow/instance/page` - 分页查询流程实例
- `GET /workflow/instance/detail/{id}` - 查询流程实例详情
- `POST /workflow/instance/cancel/{id}` - 取消流程实例
- `GET /workflow/instance/diagram/{id}` - 获取流程图

### 任务管理
- `GET /workflow/task/todo/list` - 查询待办任务列表
- `GET /workflow/task/done/list` - 查询已办任务列表
- `POST /workflow/task/approve/{taskId}` - 审批通过
- `POST /workflow/task/reject/{taskId}` - 审批驳回
- `POST /workflow/task/delegate/{taskId}` - 任务转派

### 表单管理
- `POST /workflow/form/create` - 创建表单
- `GET /workflow/form/page` - 分页查询表单
- `GET /workflow/form/field/list/{formId}` - 查询表单字段
- `POST /workflow/form/data/save` - 保存表单数据

### 流程监控
- `GET /workflow/monitor/instance/detail/{id}` - 查询流程实例详情
- `GET /workflow/monitor/progress/{id}` - 获取流程进度
- `GET /workflow/monitor/logs/{id}` - 获取流程日志
- `GET /workflow/monitor/statistics` - 流程统计分析

### 消息通知
- `GET /workflow/notification/list` - 查询用户通知列表
- `POST /workflow/notification/read/{id}` - 标记通知已读
- `GET /workflow/notification/unread/count` - 查询未读通知数量

## 配置说明

### application.yml

```yaml
server:
  port: 8086
  servlet:
    context-path: /workflow

spring:
  application:
    name: qooerp-workflow-service

flowable:
  database-schema-update: true
  db-history-used: true
  history-level: full
  async-executor-activate: true

workflow:
  diagram:
    enabled: true
    font-name: 宋体
  cache:
    enabled: true
  timeout:
    default-task-duration: 86400
  notification:
    enabled: true
  form:
    enabled: true
```

## 开发进度

| 阶段 | 状态 | 完成度 |
|------|------|--------|
| 文档设计 | ✅ 已完成 | 100% |
| 基础设施搭建 | ✅ 已完成 | 100% |
| 核心功能开发 | 📋 未开始 | 0% |
| 表单功能开发 | 📋 未开始 | 0% |
| 高级功能开发 | 📋 未开始 | 0% |
| 通知与监控 | 📋 未开始 | 0% |
| 测试与优化 | 📋 未开始 | 0% |

**总体完成度**: 28.6%

详细进度请查看 [06-开发进度.md](./docs/06-开发进度.md)

## 快速开始

### 1. 数据库初始化

```sql
-- 执行数据库迁移脚本
-- 文件位置: qooerp-workflow-start/src/main/resources/migration/V1.0.0__init_schema.sql
```

### 2. 启动服务

```bash
# 编译项目
mvn clean install

# 启动服务
cd qooerp-workflow-start
mvn spring-boot:run
```

### 3. 访问服务

- 服务地址: http://localhost:8086/workflow
- API文档: http://localhost:8086/workflow/swagger-ui.html

## 依赖说明

主要依赖项已在 `qooerp-workflow-service/pom.xml` 中配置：

- Flowable Spring Boot Starter
- MyBatis-Plus Boot Starter
- PostgreSQL Driver
- Nacos Discovery & Config
- SpringDoc OpenAPI
- Caffeine Cache
- Redis

## 注意事项

1. **ID类型**: 本模块使用 String 类型作为主键ID（UUID），与基础模块 BaseEntity 的 Long 类型不同
2. **多租户**: 支持多租户数据隔离，通过 tenant_id 字段实现
3. **软删除**: 所有业务表支持逻辑删除，通过 deleted 字段标记
4. **审计字段**: 所有表包含 create_time、update_time、create_by、update_by 审计字段

## 版本历史

- **v2.0** (20xx-xx-xx)
  - 完成项目结构搭建
  - 完成基础设施搭建
  - 完成所有设计文档
  - 编译成功，准备开始功能开发

## 联系方式

- 项目地址: https://github.com/qoobot/qooerp
- 文档地址: ./docs/
