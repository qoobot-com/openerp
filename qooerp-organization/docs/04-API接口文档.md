# QooERP Organization 模块 - API接口文档

## 1. 公司管理接口

### 1.1 创建公司

**接口地址**: `POST /api/organization/company`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求体**:
```json
{
  "companyCode": "QOOERP",
  "companyName": "QooERP科技有限公司",
  "companyNameEn": "QooERP Technology Co., Ltd.",
  "parentId": null,
  "legalPerson": "张三",
  "creditCode": "91110000XXXXXXXXXX",
  "address": "北京市海淀区xxx",
  "phone": "010-12345678",
  "email": "contact@qooerp.com",
  "logo": "/file/logo/qooerp.png",
  "description": "QooERP科技有限公司",
  "sort": 1,
  "status": 1
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

### 1.2 获取公司

**接口地址**: `GET /api/organization/company/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 公司ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "companyCode": "QOOERP",
    "companyName": "QooERP科技有限公司",
    "companyNameEn": "QooERP Technology Co., Ltd.",
    "parentId": null,
    "companyLevel": 1,
    "legalPerson": "张三",
    "creditCode": "91110000XXXXXXXXXX",
    "address": "北京市海淀区xxx",
    "phone": "010-12345678",
    "email": "contact@qooerp.com",
    "logo": "/file/logo/qooerp.png",
    "description": "QooERP科技有限公司",
    "sort": 1,
    "status": 1,
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 1.3 更新公司

**接口地址**: `PUT /api/organization/company/{id}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 公司ID |

**请求体**:
```json
{
  "companyName": "QooERP科技集团有限公司",
  "description": "QooERP科技集团有限公司-已更新",
  "sort": 2
}
```

### 1.4 删除公司

**接口地址**: `DELETE /api/organization/company/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1705488000000
}
```

### 1.5 分页查询公司

**接口地址**: `GET /api/organization/company/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页，默认1 |
| size | Long | 否 | 每页大小，默认10 |
| companyCode | String | 否 | 公司编码(模糊) |
| companyName | String | 否 | 公司名称(模糊) |
| status | Integer | 否 | 状态 |
| parentId | Long | 否 | 父公司ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "companyCode": "QOOERP",
        "companyName": "QooERP科技有限公司",
        "companyLevel": 1,
        "legalPerson": "张三",
        "phone": "010-12345678",
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

### 1.6 获取公司树

**接口地址**: `GET /api/organization/company/tree`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "QooERP科技有限公司",
      "parentId": null,
      "level": 1,
      "type": "company",
      "phone": "010-12345678",
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "上海分公司",
          "parentId": 1,
          "level": 2,
          "type": "company",
          "sort": 1,
          "status": 1,
          "children": [],
          "hasChildren": false
        }
      ],
      "hasChildren": true
    }
  ],
  "timestamp": 1705488000000
}
```

### 1.7 获取子公司

**接口地址**: `GET /api/organization/company/{id}/children`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 父公司ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "companyCode": "SH001",
      "companyName": "上海分公司",
      "parentId": 1,
      "companyLevel": 2,
      "status": 1
    }
  ],
  "timestamp": 1705488000000
}
```

---

## 2. 部门管理接口

### 2.1 创建部门

**接口地址**: `POST /api/organization/department`

**请求体**:
```json
{
  "companyId": 1,
  "deptCode": "RD",
  "deptName": "研发部",
  "deptNameEn": "R&D Department",
  "parentId": null,
  "leaderId": 1,
  "leaderName": "张三",
  "phone": "010-87654321",
  "email": "rd@qooerp.com",
  "address": "北京市海淀区xxx",
  "description": "负责产品研发",
  "sort": 1,
  "status": 1
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

### 2.2 获取部门

**接口地址**: `GET /api/organization/department/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "companyId": 1,
    "deptCode": "RD",
    "deptName": "研发部",
    "deptNameEn": "R&D Department",
    "parentId": null,
    "deptLevel": 1,
    "leaderId": 1,
    "leaderName": "张三",
    "phone": "010-87654321",
    "email": "rd@qooerp.com",
    "address": "北京市海淀区xxx",
    "employeeCount": 50,
    "description": "负责产品研发",
    "sort": 1,
    "status": 1,
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 2.3 更新部门

**接口地址**: `PUT /api/organization/department/{id}`

**请求体**:
```json
{
  "deptName": "技术研发部",
  "leaderId": 2,
  "leaderName": "李四",
  "description": "负责技术研发工作"
}
```

### 2.4 删除部门

**接口地址**: `DELETE /api/organization/department/{id}`

**说明**: 删除部门前需检查是否有子部门和员工，如有则不允许删除。

### 2.5 分页查询部门

**接口地址**: `GET /api/organization/department/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| companyId | Long | 否 | 公司ID |
| deptCode | String | 否 | 部门编码(模糊) |
| deptName | String | 否 | 部门名称(模糊) |
| status | Integer | 否 | 状态 |
| parentId | Long | 否 | 父部门ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "companyId": 1,
        "deptCode": "RD",
        "deptName": "研发部",
        "deptLevel": 1,
        "leaderName": "张三",
        "employeeCount": 50,
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

### 2.6 获取部门树

**接口地址**: `GET /api/organization/department/tree`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| companyId | Long | 是 | 公司ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "研发部",
      "parentId": null,
      "level": 1,
      "type": "department",
      "companyId": 1,
      "deptId": 1,
      "phone": "010-87654321",
      "leaderName": "张三",
      "employeeCount": 50,
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "前端开发组",
          "parentId": 1,
          "level": 2,
          "type": "department",
          "companyId": 1,
          "deptId": 2,
          "employeeCount": 20,
          "sort": 1,
          "status": 1,
          "children": [],
          "hasChildren": false
        },
        {
          "id": 3,
          "name": "后端开发组",
          "parentId": 1,
          "level": 2,
          "type": "department",
          "companyId": 1,
          "deptId": 3,
          "employeeCount": 30,
          "sort": 2,
          "status": 1,
          "children": [],
          "hasChildren": false
        }
      ],
      "hasChildren": true
    }
  ],
  "timestamp": 1705488000000
}
```

### 2.7 获取子部门

**接口地址**: `GET /api/organization/department/{id}/children`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "companyId": 1,
      "deptCode": "FE",
      "deptName": "前端开发组",
      "parentId": 1,
      "deptLevel": 2,
      "employeeCount": 20,
      "status": 1
    },
    {
      "id": 3,
      "companyId": 1,
      "deptCode": "BE",
      "deptName": "后端开发组",
      "parentId": 1,
      "deptLevel": 2,
      "employeeCount": 30,
      "status": 1
    }
  ],
  "timestamp": 1705488000000
}
```

### 2.8 获取部门路径

**接口地址**: `GET /api/organization/department/{id}/path`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "研发部 / 前端开发组",
  "timestamp": 1705488000000
}
```

### 2.9 移动部门

**接口地址**: `POST /api/organization/department/move`

**请求体**:
```json
{
  "deptId": 3,
  "newParentId": 2
}
```

**说明**: 将部门3移动到部门2下，作为其子部门。

---

## 3. 职位管理接口

### 3.1 创建职位

**接口地址**: `POST /api/organization/position`

**请求体**:
```json
{
  "companyId": 1,
  "deptId": 1,
  "positionCode": "SENIOR_DEV",
  "positionName": "高级开发工程师",
  "positionNameEn": "Senior Developer",
  "positionLevel": 3,
  "description": "高级开发工程师职位",
  "responsibilities": "负责核心模块设计和开发",
  "requirements": "5年以上开发经验",
  "sort": 1,
  "status": 1
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

### 3.2 获取职位

**接口地址**: `GET /api/organization/position/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "companyId": 1,
    "deptId": 1,
    "positionCode": "SENIOR_DEV",
    "positionName": "高级开发工程师",
    "positionNameEn": "Senior Developer",
    "positionLevel": 3,
    "description": "高级开发工程师职位",
    "responsibilities": "负责核心模块设计和开发",
    "requirements": "5年以上开发经验",
    "sort": 1,
    "status": 1,
    "createdTime": "2025-01-17 10:00:00"
  },
  "timestamp": 1705488000000
}
```

### 3.3 更新职位

**接口地址**: `PUT /api/organization/position/{id}`

**请求体**:
```json
{
  "positionName": "资深开发工程师",
  "requirements": "8年以上开发经验，有大型项目经验",
  "sort": 2
}
```

### 3.4 删除职位

**接口地址**: `DELETE /api/organization/position/{id}`

**说明**: 删除职位前需检查是否有员工关联，如有则不允许删除。

### 3.5 分页查询职位

**接口地址**: `GET /api/organization/position/list`

**查询参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| current | Long | 否 | 当前页 |
| size | Long | 否 | 每页大小 |
| companyId | Long | 否 | 公司ID |
| deptId | Long | 否 | 部门ID |
| positionCode | String | 否 | 职位编码(模糊) |
| positionName | String | 否 | 职位名称(模糊) |
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
        "companyId": 1,
        "deptId": 1,
        "positionCode": "SENIOR_DEV",
        "positionName": "高级开发工程师",
        "positionLevel": 3,
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

### 3.6 获取部门职位

**接口地址**: `GET /api/organization/position/department/{deptId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "companyId": 1,
      "deptId": 1,
      "positionCode": "SENIOR_DEV",
      "positionName": "高级开发工程师",
      "positionLevel": 3,
      "sort": 1,
      "status": 1
    },
    {
      "id": 2,
      "companyId": 1,
      "deptId": 1,
      "positionCode": "MIDDLE_DEV",
      "positionName": "中级开发工程师",
      "positionLevel": 2,
      "sort": 2,
      "status": 1
    }
  ],
  "timestamp": 1705488000000
}
```

### 3.7 获取公司职位

**接口地址**: `GET /api/organization/position/company/{companyId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "companyId": 1,
      "deptId": 1,
      "positionCode": "SENIOR_DEV",
      "positionName": "高级开发工程师",
      "positionLevel": 3,
      "sort": 1,
      "status": 1
    },
    {
      "id": 2,
      "companyId": 1,
      "deptId": 1,
      "positionCode": "MIDDLE_DEV",
      "positionName": "中级开发工程师",
      "positionLevel": 2,
      "sort": 2,
      "status": 1
    },
    {
      "id": 3,
      "companyId": 1,
      "deptId": 2,
      "positionCode": "PRODUCT_MANAGER",
      "positionName": "产品经理",
      "positionLevel": 3,
      "sort": 3,
      "status": 1
    }
  ],
  "timestamp": 1705488000000
}
```

---

## 4. 组织架构接口

### 4.1 获取完整组织架构树

**接口地址**: `GET /api/organization/tree/{companyId}`

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| companyId | Long | 是 | 公司ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "QooERP科技有限公司",
      "nameEn": "QooERP Technology Co., Ltd.",
      "parentId": null,
      "level": 1,
      "type": "company",
      "companyId": 1,
      "phone": "010-12345678",
      "email": "contact@qooerp.com",
      "sort": 1,
      "status": 1,
      "children": [
        {
          "id": 1,
          "name": "研发部",
          "nameEn": "R&D Department",
          "parentId": null,
          "level": 1,
          "type": "department",
          "companyId": 1,
          "deptId": 1,
          "phone": "010-87654321",
          "leaderName": "张三",
          "employeeCount": 50,
          "sort": 1,
          "status": 1,
          "children": [
            {
              "id": 2,
              "name": "前端开发组",
              "parentId": 1,
              "level": 2,
              "type": "department",
              "companyId": 1,
              "deptId": 2,
              "employeeCount": 20,
              "sort": 1,
              "status": 1,
              "children": [],
              "hasChildren": false
            },
            {
              "id": 3,
              "name": "后端开发组",
              "parentId": 1,
              "level": 2,
              "type": "department",
              "companyId": 1,
              "deptId": 3,
              "employeeCount": 30,
              "sort": 2,
              "status": 1,
              "children": [],
              "hasChildren": false
            }
          ],
          "hasChildren": true
        },
        {
          "id": 2,
          "name": "销售部",
          "parentId": null,
          "level": 1,
          "type": "department",
          "companyId": 1,
          "deptId": 4,
          "phone": "010-11111111",
          "leaderName": "李四",
          "employeeCount": 30,
          "sort": 2,
          "status": 1,
          "children": [],
          "hasChildren": false
        }
      ],
      "hasChildren": true
    }
  ],
  "timestamp": 1705488000000
}
```

### 4.2 获取部门组织树

**接口地址**: `GET /api/organization/tree/department/{deptId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "研发部",
    "nameEn": "R&D Department",
    "parentId": null,
    "level": 1,
    "type": "department",
    "companyId": 1,
    "deptId": 1,
    "phone": "010-87654321",
    "leaderName": "张三",
    "employeeCount": 50,
    "sort": 1,
    "status": 1,
    "children": [
      {
        "id": 2,
        "name": "前端开发组",
        "parentId": 1,
        "level": 2,
        "type": "department",
        "companyId": 1,
        "deptId": 2,
        "employeeCount": 20,
        "sort": 1,
        "status": 1,
        "children": [],
        "hasChildren": false
      },
      {
        "id": 3,
        "name": "后端开发组",
        "parentId": 1,
        "level": 2,
        "type": "department",
        "companyId": 1,
        "deptId": 3,
        "employeeCount": 30,
        "sort": 2,
        "status": 1,
        "children": [],
        "hasChildren": false
      }
    ],
    "hasChildren": true
  },
  "timestamp": 1705488000000
}
```

---

## 5. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |
| 2001 | 公司编码已存在 |
| 2002 | 部门编码已存在 |
| 2003 | 职位编码已存在 |
| 2004 | 部门有子部门，不能删除 |
| 2005 | 部门有员工，不能删除 |
| 2006 | 职位有员工，不能删除 |
| 2007 | 不能将部门移动到其子部门下 |
| 2008 | 公司级别超过限制 |

---

## 6. 版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0.0 | 2025-01-17 | 初始版本 | Auto |
