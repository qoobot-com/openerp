# QooERP Gateway 模块 - API接口文档

## 1. 路由管理接口

### 1.1 添加路由

**接口地址**: `POST /api/gateway/route`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "id": "qooerp-auth",
  "uri": "lb://qooerp-auth",
  "predicates": [
    "Path=/api/auth/**"
  ],
  "filters": [
    "StripPrefix=2"
  ],
  "order": 0
}
```

**参数说明**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 路由ID，唯一标识 |
| uri | String | 是 | 目标URI，支持lb://前缀的负载均衡 |
| predicates | Array | 是 | 断言列表，条件匹配 |
| filters | Array | 否 | 过滤器列表，请求处理 |
| order | Integer | 否 | 路由优先级，越小越优先 |

**响应示例**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.2 查询所有路由

**接口地址**: `GET /api/gateway/route/list`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "qooerp-auth",
      "uri": "lb://qooerp-auth",
      "predicates": [
        "Path=/api/auth/**"
      ],
      "filters": [
        "StripPrefix=2"
      ],
      "order": 0
    },
    {
      "id": "qooerp-user",
      "uri": "lb://qooerp-user",
      "predicates": [
        "Path=/api/user/**"
      ],
      "filters": [
        "StripPrefix=2"
      ],
      "order": 1
    }
  ],
  "timestamp": 1705488000000
}
```

### 1.3 删除路由

**接口地址**: `DELETE /api/gateway/route/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 路由ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.4 更新路由

**接口地址**: `PUT /api/gateway/route/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | String | 是 | 路由ID |

**请求体**:
```json
{
  "uri": "lb://qooerp-auth",
  "predicates": [
    "Path=/api/auth/**"
  ],
  "filters": [
    "StripPrefix=2",
    "AddRequestHeader=X-Request-From,Gateway"
  ],
  "order": 0
}
```

---

## 2. 监控接口

### 2.1 健康检查

**接口地址**: `GET /actuator/health`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "UP",
    "nacosStatus": "UP",
    "timestamp": 1705488000000
  },
  "timestamp": 1705488000000
}
```

**状态说明**:
| 状态 | 说明 |
|------|------|
| UP | 正常 |
| DOWN | 异常 |

### 2.2 查看路由

**接口地址**: `GET /actuator/gateway/routes`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "routes": [
      {
        "route_id": "qooerp-auth",
        "route_definition": {
          "id": "qooerp-auth",
          "uri": "lb://qooerp-auth",
          "predicates": [
            {
              "name": "Path",
              "args": {
                "pattern": "/api/auth/**"
              }
            }
          ],
          "filters": [
            {
              "name": "StripPrefix",
              "args": {
                "parts": 2
              }
            }
          ],
          "order": 0
        },
        "order": 0
      }
    ]
  },
  "timestamp": 1705488000000
}
```

### 2.3 查看全局过滤器

**接口地址**: `GET /actuator/gateway/globalfilters`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "globalfilters": [
      {
        "filter": "org.springframework.cloud.gateway.filter.ForwardRoutingFilter",
        "order": 0
      },
      {
        "filter": "org.springframework.cloud.gateway.filter.NettyRoutingFilter",
        "order": Integer.MAX_VALUE
      },
      {
        "filter": "com.qoobot.qooerp.gateway.filter.AuthFilter",
        "order": -100
      },
      {
        "filter": "com.qoobot.qooerp.gateway.filter.PermissionFilter",
        "order": -99
      },
      {
        "filter": "com.qoobot.qooerp.gateway.filter.LogFilter",
        "order": -90
      }
    ]
  },
  "timestamp": 1705488000000
}
```

### 2.4 查看路由过滤器

**接口地址**: `GET /actuator/gateway/routefilters`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "routefilters": [
      {
        "filter": "StripPrefix",
        "order": 10001,
        "args": {
          "parts": 2
        }
      },
      {
        "filter": "RequestRateLimiter",
        "order": 10002
      }
    ]
  },
  "timestamp": 1705488000000
}
```

### 2.5 刷新路由缓存

**接口地址**: `POST /actuator/gateway/refresh`

**响应示例**:
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": null,
  "timestamp": 1705488000000
}
```

---

## 3. 路由转发说明

### 3.1 认证服务路由

**路径映射**:
```
/api/auth/**  ->  qooerp-auth服务
```

**示例请求**:
```
POST /api/auth/login
请求体: { "username": "admin", "password": "123456" }
转发到: qooerp-auth服务的 POST /login
```

### 3.2 权限服务路由

**路径映射**:
```
/api/permission/**  ->  qooerp-permission服务
```

**示例请求**:
```
GET /api/permission/role/list
转发到: qooerp-permission服务的 GET /role/list
```

### 3.3 用户服务路由

**路径映射**:
```
/api/user/**  ->  qooerp-user服务
```

**示例请求**:
```
GET /api/user/list
转发到: qooerp-user服务的 GET /list
```

### 3.4 系统服务路由

**路径映射**:
```
/api/system/**  ->  qooerp-system服务
```

**示例请求**:
```
GET /api/system/dict/list
转发到: qooerp-system服务的 GET /dict/list
```

### 3.5 组织服务路由

**路径映射**:
```
/api/organization/**  ->  qooerp-organization服务
```

**示例请求**:
```
GET /api/organization/company/list
转发到: qooerp-organization服务的 GET /company/list
```

---

## 4. 请求头说明

### 4.1 认证相关

| 请求头 | 说明 | 示例 |
|--------|------|------|
| Authorization | 认证Token | Bearer eyJhbGciOiJIUzI1NiJ9... |
| X-User-Id | 用户ID（网关注入） | 1 |
| X-Username | 用户名（网关注入） | admin |
| X-User-Name | 真实姓名（网关注入） | 管理员 |

### 4.2 链路追踪

| 请求头 | 说明 | 示例 |
|--------|------|------|
| X-Request-Id | 请求ID | 550e8400-e29b-41d4-a716-446655440000 |
| X-Trace-Id | 链路追踪ID | 550e8400-e29b-41d4-a716-446655440000 |
| X-Span-Id | Span ID | 550e8400-e29b-41d4-a716-446655440000 |

### 4.3 客户端信息

| 请求头 | 说明 | 示例 |
|--------|------|------|
| X-Real-IP | 真实IP | 192.168.1.100 |
| X-Forwarded-For | 转发IP | 192.168.1.100, 192.168.1.1 |
| User-Agent | 用户代理 | Mozilla/5.0... |

---

## 5. 响应体说明

### 5.1 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1705488000000
}
```

### 5.2 错误响应格式

**401 未授权**:
```json
{
  "code": 401,
  "message": "未登录或token已过期",
  "data": null,
  "timestamp": 1705488000000
}
```

**403 权限不足**:
```json
{
  "code": 403,
  "message": "权限不足",
  "data": null,
  "timestamp": 1705488000000
}
```

**429 限流**:
```json
{
  "code": 429,
  "message": "访问过于频繁，请稍后再试",
  "data": null,
  "timestamp": 1705488000000
}
```

**503 服务降级**:
```json
{
  "code": 503,
  "message": "服务暂时不可用，请稍后再试",
  "data": null,
  "timestamp": 1705488000000
}
```

**504 超时**:
```json
{
  "code": 504,
  "message": "请求超时",
  "data": null,
  "timestamp": 1705488000000
}
```

---

## 6. 白名单路径

以下路径不需要认证即可访问：

| 路径 | 说明 |
|------|------|
| /api/auth/login | 登录接口 |
| /api/auth/captcha | 验证码接口 |
| /api/auth/refresh-token | 刷新Token接口 |
| /api/system/public/** | 公共资源接口 |
| /actuator/health | 健康检查接口 |

---

## 7. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 429 | 访问过于频繁（限流） |
| 500 | 系统内部错误 |
| 503 | 服务不可用（降级） |
| 504 | 请求超时 |
| 6001 | 路由不存在 |
| 6002 | 路由添加失败 |
| 6003 | 路由删除失败 |
| 6004 | 服务连接失败 |
| 6005 | 网关配置错误 |

---

## 8. 使用示例

### 8.1 登录示例

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员"
    }
  }
}
```

### 8.2 访问受保护接口示例

```bash
curl -X GET http://localhost:8080/api/user/list \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100
  }
}
```

### 8.3 路由管理示例

```bash
# 查看所有路由
curl -X GET http://localhost:8080/api/gateway/route/list \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# 添加路由
curl -X POST http://localhost:8080/api/gateway/route \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "id": "new-service",
    "uri": "lb://new-service",
    "predicates": ["Path=/api/new/**"],
    "filters": ["StripPrefix=2"]
  }'

# 删除路由
curl -X DELETE http://localhost:8080/api/gateway/route/new-service \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 9. 版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0.0 | 2025-01-17 | 初始版本 | Auto |
