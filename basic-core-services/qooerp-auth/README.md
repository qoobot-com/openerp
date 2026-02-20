# qooerp-auth 认证服务模块

## 模块概述

qooerp-auth是QooERP系统的认证授权核心服务，负责用户身份认证、令牌管理等功能。

## 模块结构

```
qooerp-auth/
├── qooerp-auth-service/     # 服务实现层
│   ├── src/main/java/com/qoobot/auth/
│   │   ├── config/         # 配置类
│   │   │   ├── AsyncConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── ScheduleConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/     # 控制器
│   │   │   ├── AuthController.java
│   │   │   └── CaptchaController.java
│   │   ├── dto/            # 数据传输对象
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   ├── TokenResponse.java
│   │   │   ├── SmsCodeRequest.java
│   │   │   └── EmailCodeRequest.java
│   │   ├── entity/         # 实体类
│   │   │   ├── AuthUser.java
│   │   │   └── AuthLoginLog.java
│   │   ├── exception/      # 异常类
│   │   │   ├── AuthException.java
│   │   │   ├── LoginFailedException.java
│   │   │   ├── UserLockedException.java
│   │   │   └── AuthExceptionHandler.java
│   │   ├── mapper/         # 数据访问层
│   │   │   ├── AuthUserMapper.java
│   │   │   └── AuthLoginLogMapper.java
│   │   ├── service/        # 服务层
│   │   │   ├── AuthService.java
│   │   │   ├── TokenService.java
│   │   │   ├── LoginLogService.java
│   │   │   ├── CaptchaService.java
│   │   │   └── impl/
│   │   │       ├── AuthServiceImpl.java
│   │   │       ├── TokenServiceImpl.java
│   │   │       ├── LoginLogServiceImpl.java
│   │   │       └── CaptchaServiceImpl.java
│   │   ├── strategy/       # 策略模式
│   │   │   ├── LoginStrategy.java
│   │   │   ├── LoginStrategyFactory.java
│   │   │   └── impl/
│   │   │       ├── UsernamePasswordLoginStrategy.java
│   │   │       ├── SmsLoginStrategy.java
│   │   │       └── EmailLoginStrategy.java
│   │   ├── task/           # 定时任务
│   │   │   └── LoginLogCleanupTask.java
│   │   ├── util/           # 工具类
│   │   │   └── LoginContextUtil.java
│   │   └── constants/      # 常量类
│   │       ├── AuthConstant.java
│   │       └── TokenConstant.java
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/
│           └── V1.0.0__init_schema.sql
└── qooerp-auth-start/      # 启动模块
    └── src/main/java/com/qoobot/auth/AuthApplication.java
```

## 核心功能

### 1. 用户登录
- 用户名密码登录
- 手机验证码登录（待完善短信服务）
- 邮箱验证码登录（待完善邮件服务）

### 2. JWT令牌管理
- 生成Access Token和Refresh Token
- 令牌验证
- 令牌刷新
- 令牌黑名单管理

### 3. 登录安全
- 登录失败次数限制（默认5次锁定30分钟）
- IP失败次数限制（默认10次/小时）
- 图形验证码验证
- 账户锁定机制

### 4. 登录日志
- 记录登录成功/失败日志
- 记录登录IP、地点、浏览器、操作系统等信息
- 定时清理过期日志（保留90天）

## API接口

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/login | POST | 用户登录 |
| /api/auth/logout | POST | 用户登出 |
| /api/auth/refresh | POST | 刷新令牌 |
| /api/auth/validate | GET | 验证令牌 |
| /api/auth/current | GET | 获取当前用户信息 |

### 验证码接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/captcha/image | GET | 生成图形验证码（字符，直接返回图片） |
| /api/captcha/generate | GET | 生成验证码（支持字符和滑块验证码） |
| /api/captcha/verify | POST | 验证验证码 |
| /api/captcha/sms/send | POST | 发送短信验证码 |
| /api/captcha/email/send | POST | 发送邮箱验证码 |

## 配置说明

### application.yml 主要配置

```yaml
# JWT配置
jwt:
  secret: qooerp-auth-secret-key-2026-very-long-secret
  access-token-expiration: 7200000    # 2小时
  refresh-token-expiration: 604800000  # 7天
  issuer: qooerp-auth

# 认证配置
auth:
  token:
    access-expire: 7200        # 访问令牌过期时间（秒）
    refresh-expire: 604800     # 刷新令牌过期时间（秒）
  login:
    max-fail-count: 5          # 最大失败次数
    lock-time: 30              # 锁定时长（分钟）
    ip-max-failed: 10          # IP最大失败次数
    ip-lock-duration: 3600     # IP锁定时长（秒）
  captcha:
    enabled: true              # 是否启用验证码
    expire: 300               # 验证码过期时间（秒）
```

## 验证码功能

### 字符验证码
- 生成4位随机字符
- 包含字母和数字
- 带干扰线和噪点

### 滑块验证码
- 随机生成滑块缺口位置
- Canvas绘制背景图和滑块图
- 拖拽验证，允许5像素误差
- 验证成功后自动销毁

### 演示页面
访问验证码演示页面：http://localhost:8081/auth/captcha-demo.html

## 数据库表

### auth_user 用户表
主要字段：id, username, password, phone, email, status, lock_time, failed_count等

### auth_login_log 登录日志表
主要字段：id, user_id, username, login_ip, browser, os, status, message等

## 快速开始

### 1. 初始化数据库
执行数据库迁移脚本，Flyway会自动创建表结构并插入默认管理员账号。

### 2. 启动应用
```bash
cd qooerp-auth-start
mvn spring-boot:run
```

### 3. 测试登录
```bash
curl -X POST http://localhost:8081/auth/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 4. 访问API文档
打开浏览器访问：http://localhost:8081/auth/swagger-ui.html

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |

## 技术栈

- Spring Boot 3.3.6
- Spring Security 6.x
- MyBatis-Plus 3.5.5
- PostgreSQL 15+
- Redis 7.x
- JWT 0.12.x
- Flyway

## 扩展性设计

### 1. 登录方式扩展
通过实现`LoginStrategy`接口可以轻松扩展新的登录方式。

### 2. 令牌存储扩展
通过`TokenService`接口可以替换Redis存储为其他存储方式。

### 3. 验证码扩展
通过`CaptchaService`接口可以集成第三方验证码服务。

## 注意事项

1. 生产环境请修改JWT密钥
2. 建议配置HTTPS
3. 建议配置短信和邮件服务
4. 定期清理过期登录日志
5. 监控登录失败次数和异常登录

## 待完善功能

- [ ] MFA多因素认证
- [ ] 第三方登录（微信、钉钉、企业微信）
- [ ] 设备管理与绑定
- [ ] 登录日志的IP地址解析为地理位置
- [ ] 图形验证码增强（滑动验证等）
