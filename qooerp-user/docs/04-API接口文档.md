# QooERP User 模块 - API接口文档

## 1. 用户管理接口

### 1.1 创建用户

**接口地址**: `POST /api/user`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "realName": "张三",
  "email": "zhangsan@qooerp.com",
  "phone": "13800138000",
  "gender": "male",
  "birthday": "1990-01-01",
  "avatar": "/file/avatar/zhangsan.jpg",
  "companyId": 1,
  "deptId": 1,
  "positionId": 1,
  "employeeNo": "EMP001",
  "hireDate": "2023-01-01",
  "status": 1,
  "roleIds": [1, 2],
  "remark": "张三的备注"
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

### 1.2 获取用户

**接口地址**: `GET /api/user/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "realName": "张三",
    "email": "zhangsan@qooerp.com",
    "phone": "13800138000",
    "gender": "male",
    "birthday": "1990-01-01",
    "avatar": "/file/avatar/zhangsan.jpg",
    "companyId": 1,
    "deptId": 1,
    "positionId": 1,
    "employeeNo": "EMP001",
    "hireDate": "2023-01-01",
    "status": 1,
    "deptName": "研发部",
    "positionName": "高级开发工程师",
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 1.3 更新用户

**接口地址**: `PUT /api/user/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |

**请求体**:
```json
{
  "nickname": "张三（更新）",
  "realName": "张三",
  "email": "zhangsan_new@qooerp.com",
  "phone": "13900139000",
  "deptId": 2,
  "positionId": 2,
  "roleIds": [1, 2, 3],
  "remark": "更新后的备注"
}
```

### 1.4 删除用户

**接口地址**: `DELETE /api/user/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.5 分页查询用户

**接口地址**: `GET /api/user/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页，默认1 |
| size | Long | 否 | 每页大小，默认10 |
| username | String | 否 | 用户名(模糊) |
| nickname | String | 否 | 昵称(模糊) |
| realName | String | 否 | 真实姓名(模糊) |
| phone | String | 否 | 手机号(模糊) |
| companyId | Long | 否 | 公司ID |
| deptId | Long | 否 | 部门ID |
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
        "username": "zhangsan",
        "nickname": "张三",
        "realName": "张三",
        "email": "zhangsan@qooerp.com",
        "phone": "13800138000",
        "gender": "male",
        "companyId": 1,
        "deptId": 1,
        "deptName": "研发部",
        "employeeNo": "EMP001",
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

### 1.6 根据用户名获取用户

**接口地址**: `GET /api/user/username/{username}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "realName": "张三",
    "email": "zhangsan@qooerp.com",
    "phone": "13800138000",
    "status": 1
  },
  "timestamp": 1705488000000
}
```

### 1.7 获取用户详情

**接口地址**: `GET /api/user/detail/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "realName": "张三",
    "email": "zhangsan@qooerp.com",
    "phone": "13800138000",
    "gender": "male",
    "birthday": "1990-01-01",
    "avatar": "/file/avatar/zhangsan.jpg",
    "companyId": 1,
    "deptId": 1,
    "positionId": 1,
    "employeeNo": "EMP001",
    "hireDate": "2023-01-01",
    "status": 1,
    "deptName": "研发部",
    "positionName": "高级开发工程师",
    "deptPath": "研发部",
    "roles": [
      {
        "id": 1,
        "roleCode": "admin",
        "roleName": "管理员",
        "description": "系统管理员"
      },
      {
        "id": 2,
        "roleCode": "developer",
        "roleName": "开发人员",
        "description": "开发人员角色"
      }
    ],
    "primaryRole": {
      "id": 1,
      "roleCode": "admin",
      "roleName": "管理员",
      "description": "系统管理员"
    },
    "departments": [
      {
        "id": 1,
        "companyId": 1,
        "deptCode": "RD",
        "deptName": "研发部",
        "deptLevel": 1
      }
    ],
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 1.8 修改密码

**接口地址**: `POST /api/user/{id}/change-password`

**请求体**:
```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.9 重置密码

**接口地址**: `POST /api/user/{id}/reset-password`

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": {
    "password": "123456"
  },
  "timestamp": 1705488000000
}
```

### 1.10 启用用户

**接口地址**: `POST /api/user/{id}/enable`

**响应示例**:
```json
{
  "code": 200,
  "message": "用户已启用",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.11 禁用用户

**接口地址**: `POST /api/user/{id}/disable`

**响应示例**:
```json
{
  "code": 200,
  "message": "用户已禁用",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.12 导入用户

**接口地址**: `POST /api/user/import`

**请求头**:
```
Content-Type: multipart/form-data
```

**表单参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | Excel文件 |

**响应示例**:
```json
{
  "code": 200,
  "message": "导入成功，成功50条，失败0条",
  "data": {
    "successCount": 50,
    "failCount": 0,
    "failRecords": []
  },
  "timestamp": 1705488000000
}
```

### 1.13 导出用户

**接口地址**: `POST /api/user/export`

**请求体**:
```json
{
  "username": "",
  "realName": "",
  "companyId": null,
  "deptId": null,
  "status": null
}
```

**说明**: 直接返回Excel文件流

### 1.14 下载导入模板

**接口地址**: `GET /api/user/template`

**说明**: 直接返回Excel模板文件

---

## 2. 用户角色接口

### 2.1 分配角色

**接口地址**: `POST /api/user/{id}/roles`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |

**请求体**:
```json
{
  "roleIds": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "角色分配成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 2.2 获取用户角色列表

**接口地址**: `GET /api/user/{id}/roles`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "roleCode": "admin",
      "roleName": "管理员",
      "description": "系统管理员",
      "isPrimary": true
    },
    {
      "id": 2,
      "roleCode": "developer",
      "roleName": "开发人员",
      "description": "开发人员角色",
      "isPrimary": false
    }
  ],
  "timestamp": 1705488000000
}
```

### 2.3 删除用户所有角色

**接口地址**: `DELETE /api/user/{id}/roles`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 2.4 删除用户指定角色

**接口地址**: `DELETE /api/user/{id}/roles/{roleId}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |
| roleId | Long | 是 | 角色ID |

---

## 3. 用户部门接口

### 3.1 关联部门

**接口地址**: `POST /api/user/{id}/department`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |

**请求体**:
```json
{
  "deptId": 2
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "部门关联成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 3.2 获取用户部门列表

**接口地址**: `GET /api/user/{id}/departments`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "companyId": 1,
      "deptCode": "RD",
      "deptName": "研发部",
      "deptLevel": 1,
      "isPrimary": true
    }
  ],
  "timestamp": 1705488000000
}
```

### 3.3 删除用户部门关联

**接口地址**: `DELETE /api/user/{id}/department`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705488000000
}
```

---

## 4. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |
| 3001 | 用户名已存在 |
| 3002 | 邮箱已存在 |
| 3003 | 手机号已存在 |
| 3004 | 用户不存在 |
| 3005 | 不能删除超级管理员 |
| 3006 | 旧密码错误 |
| 3007 | 密码强度不足 |
| 3008 | 员工编号已存在 |
| 3009 | 导入文件格式错误 |
| 3010 | 导入数据格式错误 |

---

## 5. 导入导出模板

### 5.1 用户导入模板

Excel表头：

| 用户名 | 密码 | 昵称 | 真实姓名 | 邮箱 | 手机号 | 性别 | 生日 | 所属部门编码 | 职位编码 | 员工编号 | 入职日期 |
|--------|------|------|----------|------|--------|------|------|--------------|----------|----------|----------|
| zhangsan | 123456 | 张三 | 张三 | zhangsan@qooerp.com | 13800138000 | 男 | 1990-01-01 | RD | SENIOR_DEV | EMP001 | 2023-01-01 |

字段说明：
- 用户名：必填，唯一
- 密码：必填
- 真实姓名：必填
- 手机号：必填
- 部门编码：必填，系统已存在的部门编码
- 职位编码：选填

### 5.2 用户导出字段

导出Excel包含字段：
- 用户名、昵称、真实姓名、邮箱、手机号、性别、生日
- 所属公司、所属部门、职位
- 员工编号、入职日期、状态
- 创建时间

---

## 6. 版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0.0 | 2025-01-17 | 初始版本 | Auto |
