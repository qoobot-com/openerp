# QooERP 系统管理模块 (qooerp-system)

## 模块概述

QooERP 系统管理模块是QooERP ERP系统的核心基础模块，提供系统级的基础功能服务，包括：

- ✅ 字典管理（System Dict）- 数据字典和字典项管理
- ✅ 系统配置（System Config）- 系统参数配置管理
- ✅ 文件管理（System File）- 文件上传下载管理
- ✅ 操作日志（System Log）- 用户操作日志记录
- ✅ 定时任务（System Job）- Quartz定时任务管理

## 模块结构

```
qooerp-system/
├── qooerp-system-service/          # 服务实现模块
│   ├── src/main/java/com/qoobot/qooerp/system/
│   │   ├── aspect/                   # 切面
│   │   │   └── LogAspect.java       # 操作日志切面
│   │   ├── config/                   # 配置类
│   │   │   ├── AsyncConfig.java     # 异步配置
│   │   │   ├── FileUploadConfig.java # 文件上传配置
│   │   │   ├── MybatisPlusConfig.java # MyBatis Plus配置
│   │   │   ├── QuartzConfig.java    # Quartz配置
│   │   │   ├── RedisConfig.java     # Redis配置
│   │   │   └── SwaggerConfig.java  # Swagger配置
│   │   ├── constants/               # 常量定义
│   │   │   └── SystemConstants.java
│   │   ├── controller/              # 控制器
│   │   │   ├── SystemConfigController.java
│   │   │   ├── SystemDictController.java
│   │   │   ├── SystemDictItemController.java
│   │   │   ├── SystemFileController.java
│   │   │   ├── SystemJobController.java
│   │   │   └── SystemLogController.java
│   │   ├── dto/                     # 数据传输对象
│   │   ├── entity/                  # 实体类
│   │   ├── enums/                   # 枚举类
│   │   │   ├── ConfigTypeEnum.java
│   │   │   ├── DictStatusEnum.java
│   │   │   ├── JobStatusEnum.java
│   │   │   └── LogStatusEnum.java
│   │   ├── exception/               # 异常类
│   │   │   ├── FileUploadException.java
│   │   │   ├── JobExecutionException.java
│   │   │   └── SystemException.java
│   │   ├── handler/                 # 处理器
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── job/                     # 定时任务
│   │   │   └── InvokeMethodJob.java
│   │   ├── listener/                # 监听器
│   │   │   └── ApplicationStartupListener.java
│   │   ├── mapper/                  # 数据访问层
│   │   │   ├── SystemConfigMapper.java
│   │   │   ├── SystemDictItemMapper.java
│   │   │   ├── SystemDictMapper.java
│   │   │   ├── SystemFileMapper.java
│   │   │   ├── SystemJobMapper.java
│   │   │   └── SystemLogMapper.java
│   │   ├── service/                 # 服务层
│   │   │   └── impl/              # 服务实现
│   │   ├── util/                    # 工具类
│   │   │   ├── DictUtils.java      # 字典工具类
│   │   │   └── FileUtils.java      # 文件工具类
│   │   └── vo/                      # 视图对象
│   └── pom.xml
├── qooerp-system-start/             # 启动模块
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-test.yml
│   │   ├── application-prod.yml
│   │   ├── bootstrap.yml
│   │   ├── banner.txt
│   │   ├── logback-spring.xml
│   │   └── db/migration/           # Flyway数据库迁移脚本
│   └── pom.xml
├── docs/                           # 文档目录
│   ├── 01-业务设计.md
│   ├── 02-应用设计.md
│   ├── 03-数据设计.md
│   ├── 04-API接口文档.md
│   ├── 05-技术设计.md
│   └── 05-数据库脚本.sql
└── README.md
```

## 技术栈

### 后端框架
- Spring Boot 3.2.x
- Spring Cloud 2023.x
- MyBatis Plus 3.5.x
- Nacos 2.x（服务发现）

### 数据存储
- PostgreSQL 15+
- Redis 7+

### 任务调度
- Quartz 2.3.x

### 文档工具
- SpringDoc OpenAPI 2.x

### 其他
- Caffeine（本地缓存）
- Redisson（分布式锁）
- EasyExcel（Excel处理）
- Flyway（数据库版本管理）

## 核心功能

### 1. 字典管理

提供数据字典的增删改查功能，支持字典类型和字典项的管理。

**特性：**
- ✅ 字典类型管理
- ✅ 字典项管理
- ✅ 字典缓存
- ✅ 分页查询
- ✅ 字典编码唯一性校验

**使用示例：**
```java
// 获取字典
SystemDictVO dict = dictService.getDictByCode("user_status");

// 获取字典项
List<SystemDictItemVO> items = dictItemService.getItemsByDictId(dictId);

// 使用工具类
String label = DictUtils.getDictLabel("user_status", "1");
Map<String, String> map = DictUtils.getDictItemsMap("user_status");
```

### 2. 系统配置

提供系统参数配置的动态管理，支持配置缓存和热更新。

**特性：**
- ✅ 配置增删改查
- ✅ 配置缓存
- ✅ 配置类型校验
- ✅ 系统配置保护

**使用示例：**
```java
// 获取配置
SystemConfigVO config = configService.getByKey("sys.name");

// 获取配置值
String value = configService.getConfigValue("sys.session.timeout");
```

### 3. 文件管理

提供文件上传、下载、删除功能，支持多种文件类型。

**特性：**
- ✅ 文件上传
- ✅ 文件下载
- ✅ 文件删除
- ✅ 文件类型校验
- ✅ 文件大小限制
- ✅ 日期路径存储

**使用示例：**
```java
// 上传文件
String filePath = fileService.uploadFile(multipartFile, "/upload");

// 下载文件
void downloadFile(String fileId, HttpServletResponse response);

// 删除文件
boolean result = fileService.deleteFile(fileId);
```

### 4. 操作日志

自动记录用户操作日志，包括请求信息、响应信息、执行时间等。

**特性：**
- ✅ 自动记录操作日志
- ✅ 异步保存
- ✅ 请求参数记录
- ✅ 执行时间统计
- ✅ 异常信息记录

**日志字段：**
- 用户ID、用户名
- 模块名称、操作类型
- 请求方法、请求参数
- IP地址、浏览器、操作系统
- 执行状态、执行时间

### 5. 定时任务

基于Quartz的定时任务管理，支持动态添加、暂停、恢复任务。

**特性：**
- ✅ 任务CRUD
- ✅ 任务启停
- ✅ Cron表达式支持
- ✅ 并发控制
- ✅ 错过策略
- ✅ 任务执行日志

**使用示例：**
```java
// 创建任务
SystemJob job = new SystemJob();
job.setJobName("数据备份");
job.setCronExpression("0 0 2 * * ?");
jobService.createJob(job);

// 暂停任务
jobService.pauseJob(jobId);

// 恢复任务
jobService.resumeJob(jobId);

// 立即执行
jobService.triggerJob(jobId);
```

## 数据库

### 数据库初始化

使用Flyway进行数据库版本管理，初始化脚本位于 `qooerp-system-start/src/main/resources/db/migration/` 目录。

**手动初始化：**
```sql
-- 执行 docs/05-数据库脚本.sql
```

### 数据库表

| 表名 | 说明 |
|------|------|
| system_dict | 字典表 |
| system_dict_item | 字典项表 |
| system_config | 系统参数表 |
| system_log | 操作日志表 |
| system_job | 定时任务表 |

## 配置说明

### application.yml

```yaml
spring:
  application:
    name: qooerp-system-service
  datasource:
    url: jdbc:postgresql://localhost:5432/qooerp_system
    username: qooerp
    password: qooerp123
  redis:
    host: localhost
    port: 6379
    database: 0

mybatis-plus:
  type-aliases-package: com.qoobot.qooerp.system.entity
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

logging:
  level:
    com.qoobot.qooerp.system: debug
```

### 文件上传配置

```yaml
file:
  upload:
    path: /upload           # 上传路径
    max-size: 104857600    # 最大文件大小（100MB）
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx
```

## API文档

启动服务后访问 Swagger 文档：

```
http://localhost:8081/swagger-ui.html
```

## 启动说明

### 本地开发

```bash
# 1. 启动PostgreSQL
docker run -d --name qooerp-system-db \
  -e POSTGRES_DB=qooerp_system \
  -e POSTGRES_USER=qooerp \
  -e POSTGRES_PASSWORD=qooerp123 \
  -p 5432:5432 \
  postgres:15

# 2. 启动Redis
docker run -d --name qooerp-redis \
  -p 6379:6379 \
  redis:7-alpine

# 3. 启动Nacos
docker run -d --name qooerp-nacos \
  -p 8848:8848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.2.3

# 4. 启动服务
cd qooerp-system-start
mvn spring-boot:run
```

### Docker部署

```bash
docker build -t qooerp-system:latest .
docker run -d --name qooerp-system \
  -p 8081:8081 \
  qooerp-system:latest
```

## 开发规范

### 包命名规范

所有类包路径：`com.qoobot.qooerp.system.{子包}`

### 常量使用

使用 `SystemConstants` 中定义的常量：

```java
// 缓存名称
SystemConstants.Cache.DICT
SystemConstants.Cache.CONFIG

// 操作类型
SystemConstants.OperationType.CREATE
SystemConstants.OperationType.UPDATE

// 文件上传
SystemConstants.FileUpload.MAX_FILE_SIZE
```

### 异常处理

使用模块自定义异常：

```java
throw new SystemException("错误信息");
throw new FileUploadException("文件上传失败");
throw new JobExecutionException("任务执行失败");
```

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 20xx-xx-xx | 初始版本 |

## 作者

QooERP Team

## 许可证

Copyright © 2026 QooERP Team. All rights reserved.
