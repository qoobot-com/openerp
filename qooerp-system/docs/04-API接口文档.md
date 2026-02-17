# QooERP System 模块 - API接口文档

## 1. 字典管理接口

### 1.1 创建字典类型

**接口地址**: `POST /api/system/dict`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "dictCode": "common_status",
  "dictName": "通用状态",
  "dictType": "status",
  "description": "通用状态字典",
  "sort": 1,
  "isSystem": false
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1705488000000
}
```

### 1.2 获取字典类型

**接口地址**: `GET /api/system/dict/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 字典ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "dictCode": "common_status",
    "dictName": "通用状态",
    "dictType": "status",
    "description": "通用状态字典",
    "sort": 1,
    "isSystem": false,
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 1.3 更新字典类型

**接口地址**: `PUT /api/system/dict/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 字典ID |

**请求体**:
```json
{
  "dictName": "通用状态(修改)",
  "description": "通用状态字典-已修改",
  "sort": 2
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.4 删除字典类型

**接口地址**: `DELETE /api/system/dict/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 字典ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.5 分页查询字典类型

**接口地址**: `GET /api/system/dict/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页，默认1 |
| size | Long | 否 | 每页大小，默认10 |
| dictCode | String | 否 | 字典编码 |
| dictName | String | 否 | 字典名称(模糊) |
| dictType | String | 否 | 字典类型 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "dictCode": "common_status",
        "dictName": "通用状态",
        "dictType": "status",
        "description": "通用状态字典",
        "sort": 1,
        "isSystem": false
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10,
    "pages": 1
  },
  "timestamp": 1705488000000
}
```

### 1.6 根据编码获取字典类型

**接口地址**: `GET /api/system/dict/code/{code}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | String | 是 | 字典编码 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "dictCode": "common_status",
    "dictName": "通用状态",
    "dictType": "status"
  },
  "timestamp": 1705488000000
}
```

---

## 2. 字典项管理接口

### 2.1 创建字典项

**接口地址**: `POST /api/system/dict-item`

**请求体**:
```json
{
  "dictId": 1,
  "itemValue": "1",
  "itemLabel": "启用",
  "itemColor": "success",
  "sort": 1,
  "isDefault": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1705488000000
}
```

### 2.2 获取字典项

**接口地址**: `GET /api/system/dict-item/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "dictId": 1,
    "itemValue": "1",
    "itemLabel": "启用",
    "itemColor": "success",
    "sort": 1,
    "isDefault": true
  },
  "timestamp": 1705488000000
}
```

### 2.3 更新字典项

**接口地址**: `PUT /api/system/dict-item/{id}`

**请求体**:
```json
{
  "itemLabel": "正常",
  "itemColor": "primary",
  "sort": 2
}
```

### 2.4 删除字典项

**接口地址**: `DELETE /api/system/dict-item/{id}`

### 2.5 分页查询字典项

**接口地址**: `GET /api/system/dict-item/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| dictId | Long | 否 | 字典ID |
| itemLabel | String | 否 | 字典标签(模糊) |

### 2.6 根据字典ID获取字典项

**接口地址**: `GET /api/system/dict-item/dict/{dictId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "dictId": 1,
      "itemValue": "1",
      "itemLabel": "启用",
      "itemColor": "success",
      "sort": 1,
      "isDefault": true
    },
    {
      "id": 2,
      "dictId": 1,
      "itemValue": "0",
      "itemLabel": "禁用",
      "itemColor": "danger",
      "sort": 2,
      "isDefault": false
    }
  ],
  "timestamp": 1705488000000
}
```

---

## 3. 参数配置接口

### 3.1 创建参数

**接口地址**: `POST /api/system/param`

**请求体**:
```json
{
  "paramKey": "system.name",
  "paramValue": "QooERP系统",
  "paramType": "string",
  "paramGroup": "system",
  "description": "系统名称",
  "isSystem": false,
  "isEncrypted": false
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1705488000000
}
```

### 3.2 获取参数

**接口地址**: `GET /api/system/param/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "paramKey": "system.name",
    "paramValue": "QooERP系统",
    "paramType": "string",
    "paramGroup": "system",
    "description": "系统名称",
    "isSystem": false,
    "isEncrypted": false
  },
  "timestamp": 1705488000000
}
```

### 3.3 更新参数

**接口地址**: `PUT /api/system/param/{id}`

**请求体**:
```json
{
  "paramValue": "QooERP企业管理系统",
  "description": "系统名称-已更新"
}
```

### 3.4 删除参数

**接口地址**: `DELETE /api/system/param/{id}`

### 3.5 分页查询参数

**接口地址**: `GET /api/system/param/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| paramKey | String | 否 | 参数键(模糊) |
| paramGroup | String | 否 | 参数分组 |

### 3.6 根据键获取参数值

**接口地址**: `GET /api/system/param/key/{key}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "QooERP系统",
  "timestamp": 1705488000000
}
```

### 3.7 批量更新参数

**接口地址**: `POST /api/system/param/batch`

**请求体**:
```json
{
  "params": [
    {
      "paramKey": "system.name",
      "paramValue": "QooERP企业管理系统"
    },
    {
      "paramKey": "system.version",
      "paramValue": "1.0.0"
    }
  ]
}
```

---

## 4. 文件管理接口

### 4.1 上传文件

**接口地址**: `POST /api/system/file/upload`

**请求头**:
```
Content-Type: multipart/form-data
```

**表单参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 上传的文件 |
| module | String | 否 | 模块名，默认"common" |

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "fileName": "20250117_abc123.jpg",
    "originalName": "avatar.jpg",
    "fileExt": "jpg",
    "fileSize": 102400,
    "mimeType": "image/jpeg",
    "storageType": "local",
    "filePath": "/data/qooerp/files/common/2025/01/17/20250117_abc123.jpg",
    "fileUrl": "/file/common/2025/01/17/20250117_abc123.jpg",
    "md5": "d41d8cd98f00b204e9800998ecf8427e"
  },
  "timestamp": 1705488000000
}
```

### 4.2 获取文件信息

**接口地址**: `GET /api/system/file/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "fileName": "20250117_abc123.jpg",
    "originalName": "avatar.jpg",
    "fileExt": "jpg",
    "fileSize": 102400,
    "mimeType": "image/jpeg",
    "fileUrl": "/file/common/2025/01/17/20250117_abc123.jpg",
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 4.3 删除文件

**接口地址**: `DELETE /api/system/file/{id}`

### 4.4 下载文件

**接口地址**: `GET /api/system/file/{id}/download`

**说明**: 直接返回文件流，响应头包含Content-Disposition

**响应头**:
```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="avatar.jpg"
```

### 4.5 预览文件

**接口地址**: `GET /api/system/file/{id}/preview`

**说明**: 直接返回文件流，用于在线预览

### 4.6 分页查询文件

**接口地址**: `GET /api/system/file/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| fileName | String | 否 | 文件名(模糊) |
| fileExt | String | 否 | 文件扩展名 |
| storageType | String | 否 | 存储类型 |

---

## 5. 日志管理接口

### 5.1 获取日志

**接口地址**: `GET /api/system/log/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "logType": "operation",
    "module": "用户管理",
    "operation": "创建用户",
    "description": "创建用户test",
    "userId": 1,
    "username": "admin",
    "ip": "192.168.1.100",
    "location": "北京市",
    "userAgent": "Mozilla/5.0",
    "requestMethod": "POST",
    "requestUrl": "/api/user",
    "requestParams": "{\"username\":\"test\"}",
    "costTime": 150,
    "status": 1,
    "errorMessage": null,
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 5.2 分页查询日志

**接口地址**: `GET /api/system/log/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| logType | String | 否 | 日志类型 |
| module | String | 否 | 模块名 |
| userId | Long | 否 | 用户ID |
| username | String | 否 | 用户名 |
| ip | String | 否 | IP地址 |
| status | Integer | 否 | 状态 |
| startTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "logType": "operation",
        "module": "用户管理",
        "operation": "创建用户",
        "username": "admin",
        "ip": "192.168.1.100",
        "costTime": 150,
        "status": 1,
        "createdTime": "2025-01-17 10:00:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10,
    "pages": 1
  },
  "timestamp": 1705488000000
}
```

### 5.3 日志统计

**接口地址**: `GET /api/system/log/statistics`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 否 | 开始日期，格式yyyy-MM-dd |
| endDate | String | 否 | 结束日期，格式yyyy-MM-dd |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalLogs": 1000,
    "operationLogs": 800,
    "loginLogs": 150,
    "systemLogs": 40,
    "errorLogs": 10,
    "successRate": 99.0,
    "avgCostTime": 120,
    "topModules": [
      {
        "module": "用户管理",
        "count": 300
      },
      {
        "module": "订单管理",
        "count": 200
      }
    ]
  },
  "timestamp": 1705488000000
}
```

### 5.4 删除日志

**接口地址**: `DELETE /api/system/log/{id}`

### 5.5 批量删除日志

**接口地址**: `DELETE /api/system/log/batch`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

### 5.6 导出日志

**接口地址**: `POST /api/system/log/export`

**请求体**:
```json
{
  "logType": "operation",
  "startTime": "2025-01-01 00:00:00",
  "endTime": "2025-01-31 23:59:59"
}
```

**说明**: 直接返回Excel文件流

---

## 6. 定时任务接口

### 6.1 创建定时任务

**接口地址**: `POST /api/system/job`

**请求体**:
```json
{
  "jobName": "数据备份任务",
  "jobGroup": "backup",
  "beanName": "dataBackupJob",
  "methodName": "execute",
  "methodParams": "",
  "cronExpression": "0 0 2 * * ?",
  "status": 1,
  "description": "每天凌晨2点执行数据库备份"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1705488000000
}
```

### 6.2 获取定时任务

**接口地址**: `GET /api/system/job/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "jobName": "数据备份任务",
    "jobGroup": "backup",
    "beanName": "dataBackupJob",
    "methodName": "execute",
    "cronExpression": "0 0 2 * * ?",
    "status": 1,
    "description": "每天凌晨2点执行数据库备份",
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 6.3 更新定时任务

**接口地址**: `PUT /api/system/job/{id}`

**请求体**:
```json
{
  "cronExpression": "0 0 3 * * ?",
  "description": "每天凌晨3点执行数据库备份"
}
```

### 6.4 删除定时任务

**接口地址**: `DELETE /api/system/job/{id}`

### 6.5 分页查询定时任务

**接口地址**: `GET /api/system/job/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| jobName | String | 否 | 任务名称(模糊) |
| jobGroup | String | 否 | 任务分组 |
| status | Integer | 否 | 状态 |

### 6.6 启用定时任务

**接口地址**: `POST /api/system/job/{id}/start`

**响应示例**:
```json
{
  "code": 200,
  "message": "启用成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 6.7 停用定时任务

**接口地址**: `POST /api/system/job/{id}/stop`

### 6.8 立即执行定时任务

**接口地址**: `POST /api/system/job/{id}/execute`

**响应示例**:
```json
{
  "code": 200,
  "message": "执行成功",
  "data": {
    "jobId": 1,
    "executeTime": "2025-01-17 10:30:00",
    "costTime": 5000,
    "status": "success"
  },
  "timestamp": 1705488000000
}
```

---

## 7. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |
| 1001 | 字典编码已存在 |
| 1002 | 字典项值已存在 |
| 1003 | 参数键已存在 |
| 1004 | 文件上传失败 |
| 1005 | 文件类型不支持 |
| 1006 | 文件大小超限 |
| 1007 | 定时任务Cron表达式错误 |

---

## 8. 版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0.0 | 2025-01-17 | 初始版本 | Auto |
