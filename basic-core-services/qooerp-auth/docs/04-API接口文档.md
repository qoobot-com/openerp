# qooerp-auth 认证服务 - API接口文档

> 模块版本：1.0.0-SNAPSHOT
> 创建日期：20xx-xx-xx
> 文档作者：QooERP团队

---

## 一、接口概述

### 1.1 基础信息

| 项目 | 值 |
|------|-----|
| 服务名称 | qooerp-auth-service |
| 服务端口 | 8081 |
| 上下文路径 | /auth |
| 协议 | HTTP/HTTPS |
| 数据格式 | JSON |

### 1.2 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1739788800000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | Object | 响应数据 |
| timestamp | Long | 时间戳（毫秒） |

### 1.3 响应码说明

| 响应码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 1.4 错误码说明

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| AUTH_001 | 用户不存在 | 登录用户不存在 |
| AUTH_002 | 密码错误 | 登录密码错误 |
| AUTH_003 | 账户已锁定 | 账户已被锁定 |
| AUTH_004 | 验证码错误 | 验证码错误 |
| AUTH_005 | 验证码已过期 | 验证码已过期 |
| AUTH_101 | 令牌无效 | JWT令牌无效 |
| AUTH_102 | 令牌已过期 | JWT令牌已过期 |
| AUTH_103 | 令牌已吊销 | JWT令牌已吊销 |
| AUTH_104 | Refresh Token无效 | Refresh Token无效 |

---

## 二、认证接口

### 2.1 用户登录

#### 接口信息

| 项目 | 值 |
|------|-----|
| 接口地址 | POST /auth/api/auth/login |
| 接口描述 | 用户登录认证 |
| 是否需要认证 | 否 |

#### 请求参数

```yaml
Content-Type: application/json

{
  "username": "string",  # 必填，用户名
  "password": "string", # 必填，密码
  "captcha": "string",  # 可选，验证码
  "captchaKey": "string" # 可选，验证码Key
}
```

#### 响应结果

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "系统管理员",
      "avatar": "http://example.com/avatar.jpg"
    }
  }
}
```

**失败响应 (400)**

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

**失败响应 (AUTH_003)**

```json
{
  "code": 403,
  "message": "账户已锁定，请30分钟后重试",
  "data": {
    "lockTime": "20xx-xx-xxT10:30:00"
  }
}
```

---

### 2.2 用户登出

#### 接口信息

| 项目 | 值 |
|------|-----|
| 接口地址 | POST /auth/api/auth/logout |
| 接口描述 | 用户登出 |
| 是否需要认证 | 是 |

#### 请求头

```yaml
Authorization: Bearer {accessToken}
```

#### 响应结果

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

---

### 2.3 刷新令牌

#### 接口信息

| 项目 | 值 |
|------|-----|
| 接口地址 | POST /auth/api/auth/refresh |
| 接口描述 | 刷新访问令牌 |
| 是否需要认证 | 否 |

#### 请求参数

```yaml
Content-Type: application/json

{
  "refreshToken": "string" # 必填，刷新令牌
}
```

#### 响应结果

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "刷新成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200
  }
}
```

**失败响应 (AUTH_104)**

```json
{
  "code": 401,
  "message": "Refresh Token无效，请重新登录",
  "data": null
}
```

---

### 2.4 验证令牌

#### 接口信息

| 项目 | 值 |
|------|-----|
| 接口地址 | GET /auth/api/auth/validate |
| 接口描述 | 验证令牌有效性 |
| 是否需要认证 | 是 |

#### 请求头

```yaml
Authorization: Bearer {accessToken}
```

#### 响应结果

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "令牌有效",
  "data": {
    "valid": true,
    "expiresIn": 1800
  }
}
```

**失败响应 (AUTH_102)**

```json
{
  "code": 401,
  "message": "令牌已过期",
  "data": {
    "valid": false,
    "expiresIn": 0
  }
}
```

---

### 2.5 获取当前用户信息

#### 接口信息

| 项目 | 值 |
|------|-----|
| 接口地址 | GET /auth/api/auth/current |
| 接口描述 | 获取当前登录用户信息 |
| 是否需要认证 | 是 |

#### 请求头

```yaml
Authorization: Bearer {accessToken}
```

#### 响应结果

**成功响应 (200)**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "userId": 1,
    "tenantId": 0,
    "username": "admin",
    "realName": "系统管理员",
    "nickname": "超级管理员",
    "avatar": "http://example.com/avatar.jpg",
    "phone": "13800138000",
    "email": "admin@qooerp.com",
    "gender": 1,
    "birthday": "1990-01-01",
    "status": 1,
    "roles": ["admin"],
    "permissions": ["*:*:*"]
  }
}
```

---

## 三、OpenAPI YAML 文档

```yaml
openapi: 3.0.3
info:
  title: QooERP认证服务API
  version: 1.0.0
  description: QooERP认证服务接口文档
servers:
  - url: http://localhost:8081/auth
    description: 本地开发环境

paths:
  /api/auth/login:
    post:
      summary: 用户登录
      description: 用户登录认证，返回JWT令牌
      tags:
        - 认证接口
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - username
                - password
              properties:
                username:
                  type: string
                  description: 用户名
                  example: admin
                password:
                  type: string
                  description: 密码
                  format: password
                  example: admin123
                captcha:
                  type: string
                  description: 验证码
                  example: abc123
                captchaKey:
                  type: string
                  description: 验证码Key
                  example: captcha_key_123
      responses:
        '200':
          description: 登录成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 登录成功
                  data:
                    type: object
                    properties:
                      accessToken:
                        type: string
                        description: 访问令牌
                      refreshToken:
                        type: string
                        description: 刷新令牌
                      tokenType:
                        type: string
                        example: Bearer
                      expiresIn:
                        type: integer
                        description: 过期时间（秒）
                        example: 7200
                      userInfo:
                        $ref: '#/components/schemas/UserInfo'
        '400':
          $ref: '#/components/responses/BadRequest'
        '403':
          $ref: '#/components/responses/Forbidden'

  /api/auth/logout:
    post:
      summary: 用户登出
      description: 用户登出，吊销令牌
      tags:
        - 认证接口
      security:
        - BearerAuth: []
      responses:
        '200':
          description: 登出成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SuccessResponse'
        '401':
          $ref: '#/components/responses/Unauthorized'

  /api/auth/refresh:
    post:
      summary: 刷新令牌
      description: 使用Refresh Token刷新Access Token
      tags:
        - 认证接口
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - refreshToken
              properties:
                refreshToken:
                  type: string
                  description: 刷新令牌
      responses:
        '200':
          description: 刷新成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 刷新成功
                  data:
                    type: object
                    properties:
                      accessToken:
                        type: string
                      refreshToken:
                        type: string
                      tokenType:
                        type: string
                        example: Bearer
                      expiresIn:
                        type: integer
                        example: 7200
        '401':
          $ref: '#/components/responses/Unauthorized'

  /api/auth/validate:
    get:
      summary: 验证令牌
      description: 验证JWT令牌有效性
      tags:
        - 认证接口
      security:
        - BearerAuth: []
      responses:
        '200':
          description: 验证成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 令牌有效
                  data:
                    type: object
                    properties:
                      valid:
                        type: boolean
                        example: true
                      expiresIn:
                        type: integer
                        example: 1800
        '401':
          $ref: '#/components/responses/Unauthorized'

  /api/auth/current:
    get:
      summary: 获取当前用户信息
      description: 获取当前登录用户信息
      tags:
        - 认证接口
      security:
        - BearerAuth: []
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                    example: 200
                  message:
                    type: string
                    example: 查询成功
                  data:
                    $ref: '#/components/schemas/UserInfo'
        '401':
          $ref: '#/components/responses/Unauthorized'

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    UserInfo:
      type: object
      properties:
        userId:
          type: integer
          description: 用户ID
        tenantId:
          type: integer
          description: 租户ID
        username:
          type: string
          description: 用户名
        realName:
          type: string
          description: 真实姓名
        nickname:
          type: string
          description: 昵称
        avatar:
          type: string
          description: 头像URL
        phone:
          type: string
          description: 手机号
        email:
          type: string
          description: 邮箱
        gender:
          type: integer
          description: 性别：0-未知 1-男 2-女
        birthday:
          type: string
          format: date
          description: 生日
        status:
          type: integer
          description: 状态：0-禁用 1-启用
        roles:
          type: array
          items:
            type: string
          description: 角色列表
        permissions:
          type: array
          items:
            type: string
          description: 权限列表

    SuccessResponse:
      type: object
      properties:
        code:
          type: integer
          example: 200
        message:
          type: string
          example: 操作成功
        data:
          type: object
          nullable: true

  responses:
    BadRequest:
      description: 请求参数错误
      content:
        application/json:
          schema:
            type: object
            properties:
              code:
                type: integer
                example: 400
              message:
                type: string
                example: 请求参数错误
              data:
                type: object
                nullable: true

    Unauthorized:
      description: 未认证
      content:
        application/json:
          schema:
            type: object
            properties:
              code:
                type: integer
                example: 401
              message:
                type: string
                example: 未认证
              data:
                type: object
                nullable: true

    Forbidden:
      description: 无权限
      content:
        application/json:
          schema:
            type: object
            properties:
              code:
                type: integer
                example: 403
              message:
                type: string
                example: 无权限
              data:
                type: object
                nullable: true
```

---

## 四、接口测试

### 4.1 Postman集合

导入以下JSON到Postman：

```json
{
  "info": {
    "name": "QooERP认证服务API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "用户登录",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"admin\",\"password\":\"admin123\"}",
          "options": {"raw": {"language": "json"}}
        },
        "url": {
          "raw": "http://localhost:8081/auth/api/auth/login",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["auth", "api", "auth", "login"]
        }
      }
    },
    {
      "name": "用户登出",
      "request": {
        "method": "POST",
        "header": [{"key": "Authorization", "value": "Bearer {{accessToken}}"}],
        "url": {
          "raw": "http://localhost:8081/auth/api/auth/logout",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["auth", "api", "auth", "logout"]
        }
      }
    },
    {
      "name": "刷新令牌",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "raw",
          "raw": "{\"refreshToken\":\"{{refreshToken}}\"}",
          "options": {"raw": {"language": "json"}}
        },
        "url": {
          "raw": "http://localhost:8081/auth/api/auth/refresh",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["auth", "api", "auth", "refresh"]
        }
      }
    },
    {
      "name": "验证令牌",
      "request": {
        "method": "GET",
        "header": [{"key": "Authorization", "value": "Bearer {{accessToken}}"}],
        "url": {
          "raw": "http://localhost:8081/auth/api/auth/validate",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["auth", "api", "auth", "validate"]
        }
      }
    },
    {
      "name": "获取当前用户信息",
      "request": {
        "method": "GET",
        "header": [{"key": "Authorization", "value": "Bearer {{accessToken}}"}],
        "url": {
          "raw": "http://localhost:8081/auth/api/auth/current",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["auth", "api", "auth", "current"]
        }
      }
    }
  ]
}
```

### 4.2 cURL示例

```bash
# 用户登录
curl -X POST http://localhost:8081/auth/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 用户登出
curl -X POST http://localhost:8081/auth/api/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# 刷新令牌
curl -X POST http://localhost:8081/auth/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN"}'

# 验证令牌
curl -X GET http://localhost:8081/auth/api/auth/validate \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# 获取当前用户信息
curl -X GET http://localhost:8081/auth/api/auth/current \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 五、参考资料

- [OpenAPI 3.0规范](https://swagger.io/specification/)
- [JWT最佳实践](https://tools.ietf.org/html/rfc8725)
- [RESTful API设计指南](https://restfulapi.net/)
