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

## 4. 日志管理接口

### 4.1 获取日志

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

### 4.2 分页查询日志

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

### 4.3 日志统计

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

### 4.4 删除日志

**接口地址**: `DELETE /api/system/log/{id}`

### 4.5 批量删除日志

**接口地址**: `DELETE /api/system/log/batch`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

### 4.6 导出日志

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

## 5. 系统公告接口

### 5.1 创建公告

**接口地址**: `POST /api/system/announcement`

**请求体**:
```json
{
  "title": "系统维护通知",
  "content": "系统将于本周六凌晨2点进行维护升级",
  "announcementType": "urgent",
  "targetType": "all",
  "targetIds": "",
  "priority": 1
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

### 5.2 获取公告

**接口地址**: `GET /api/system/announcement/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "系统维护通知",
    "content": "系统将于本周六凌晨2点进行维护升级",
    "announcementType": "urgent",
    "targetType": "all",
    "targetIds": "",
    "publisherName": "admin",
    "publishTime": "2025-01-17 10:00:00",
    "status": 1,
    "priority": 1,
    "readCount": 100
  },
  "timestamp": 1705488000000
}
```

### 5.3 更新公告

**接口地址**: `PUT /api/system/announcement/{id}`

**请求体**:
```json
{
  "title": "系统维护通知（更新）",
  "content": "系统将于本周六凌晨2点进行维护升级，预计耗时2小时"
}
```

### 5.4 删除公告

**接口地址**: `DELETE /api/system/announcement/{id}`

### 5.5 分页查询公告

**接口地址**: `GET /api/system/announcement/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| title | String | 否 | 标题(模糊) |
| announcementType | String | 否 | 公告类型 |
| status | Integer | 否 | 状态 |

### 5.6 发布公告

**接口地址**: `POST /api/system/announcement/{id}/publish`

**响应示例**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 5.7 撤回公告

**接口地址**: `POST /api/system/announcement/{id}/revoke`

### 5.8 获取我的公告列表

**接口地址**: `GET /api/system/announcement/my`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "title": "系统维护通知",
      "announcementType": "urgent",
      "publishTime": "2025-01-17 10:00:00",
      "isRead": false
    }
  ],
  "timestamp": 1705488000000
}
```

### 5.9 标记公告已读

**接口地址**: `POST /api/system/announcement/{id}/read`

**响应示例**:
```json
{
  "code": 200,
  "message": "标记成功",
  "data": null,
  "timestamp": 1705488000000
}
```

---

## 6. 在线用户管理接口

### 6.1 分页查询在线用户

**接口地址**: `GET /api/system/online-user/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| username | String | 否 | 用户名(模糊) |
| loginIp | String | 否 | 登录IP |
| status | Integer | 否 | 状态 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "username": "admin",
        "loginIp": "192.168.1.100",
        "loginLocation": "北京市",
        "loginTime": "2025-01-17 09:00:00",
        "lastActiveTime": "2025-01-17 10:30:00",
        "status": 1,
        "userAgent": "Mozilla/5.0"
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

### 6.2 强制下线

**接口地址**: `POST /api/system/online-user/{id}/kickout`

**响应示例**:
```json
{
  "code": 200,
  "message": "强制下线成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 6.3 批量强制下线

**接口地址**: `POST /api/system/online-user/batch-kickout`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

### 6.4 获取在线统计

**接口地址**: `GET /api/system/online-user/statistics`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalOnline": 50,
    "totalTodayLogin": 120,
    "peakOnline": 80,
    "peakTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

---

## 7. 数据导出接口

### 7.1 创建导出任务

**接口地址**: `POST /api/system/export/task`

**请求体**:
```json
{
  "taskName": "用户数据导出",
  "moduleName": "user",
  "exportType": "excel",
  "exportCondition": "{\"deptId\":1,\"status\":1}"
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

### 7.2 分页查询导出任务

**接口地址**: `GET /api/system/export/task/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| taskName | String | 否 | 任务名称(模糊) |
| moduleName | String | 否 | 模块名称 |
| status | Integer | 否 | 状态 |

### 7.3 下载导出文件

**接口地址**: `GET /api/system/export/task/{id}/download`

**说明**: 直接返回文件流

### 7.4 删除导出任务

**接口地址**: `DELETE /api/system/export/task/{id}`

---

## 8. 数据导入接口

### 8.1 创建导入任务

**接口地址**: `POST /api/system/import/task`

**请求头**:
```
Content-Type: multipart/form-data
```

**表单参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 上传的文件 |
| taskName | String | 否 | 任务名称 |
| moduleName | String | 否 | 模块名称 |

**响应示例**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": 1,
  "timestamp": 1705488000000
}
```

### 8.2 分页查询导入任务

**接口地址**: `GET /api/system/import/task/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| taskName | String | 否 | 任务名称(模糊) |
| moduleName | String | 否 | 模块名称 |
| status | Integer | 否 | 状态 |

### 8.3 获取导入错误详情

**接口地址**: `GET /api/system/import/task/{id}/errors`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": 1,
    "taskName": "用户数据导入",
    "totalRecords": 100,
    "successRecords": 95,
    "failureRecords": 5,
    "errors": [
      {
        "rowNumber": 3,
        "fieldName": "mobile",
        "errorMessage": "手机号格式错误",
        "rowData": "{\"username\":\"test\",\"mobile\":\"123\"}"
      }
    ]
  },
  "timestamp": 1705488000000
}
```

### 8.4 下载导入模板

**接口地址**: `GET /api/system/import/template/{moduleName}`

**说明**: 直接返回模板文件流

---

## 9. 审计日志接口

### 9.1 分页查询审计日志

**接口地址**: `GET /api/system/audit-log/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| auditType | String | 否 | 审计类型 |
| moduleName | String | 否 | 模块名称 |
| operationType | String | 否 | 操作类型 |
| targetTable | String | 否 | 目标表名 |
| targetId | String | 否 | 目标ID |
| userId | Long | 否 | 用户ID |
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
        "auditType": "data_change",
        "moduleName": "user",
        "operationType": "update",
        "targetTable": "sys_user",
        "targetId": "1",
        "oldValue": "{\"username\":\"test\",\"status\":0}",
        "newValue": "{\"username\":\"test\",\"status\":1}",
        "changedFields": "[\"status\"]",
        "username": "admin",
        "ip": "192.168.1.100",
        "operateTime": "2025-01-17 10:00:00"
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

### 9.2 查看数据变更详情

**接口地址**: `GET /api/system/audit-log/{id}/detail`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "auditType": "data_change",
    "targetTable": "sys_user",
    "targetId": "1",
    "oldValue": {
      "username": "test",
      "status": 0
    },
    "newValue": {
      "username": "test",
      "status": 1
    },
    "changes": [
      {
        "field": "status",
        "oldValue": "0",
        "newValue": "1"
      }
    ],
    "username": "admin",
    "operateTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 9.3 导出审计日志

**接口地址**: `POST /api/system/audit-log/export`

**请求体**:
```json
{
  "auditType": "data_change",
  "startTime": "2025-01-01 00:00:00",
  "endTime": "2025-01-31 23:59:59"
}
```

**说明**: 直接返回Excel文件流

---

## 10. 错误码说明

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
| 2001 | 公告标题不能为空 |
| 2002 | 公告内容不能为空 |
| 2003 | 已发布公告不可编辑 |
| 3001 | 用户不在线 |
| 3002 | 无权限强制下线 |
| 4001 | 文件格式不支持 |
| 4002 | 文件大小超限 |
| 4003 | 导入数据验证失败 |
| 4004 | 导出记录数超限 |

---

## 11. 版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0.0 | 2025-01-17 | 初始版本 | Auto |
| 2.0.0 | 20xx-xx-xx | 补充系统公告、在线用户、数据导入导出、审计日志等接口 | Auto |
| 2.1.0 | 20xx-xx-xx | 移除文件管理接口 | Auto |
| 2.2.0 | 20xx-xx-xx | 移除定时任务接口 | Auto |
| 2.3.0 | 20xx-xx-xx | 更新模块依赖关系，明确与其他模块为并列微服务，通过Feign客户端调用 | Auto |
