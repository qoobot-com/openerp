# qooerp-permission 权限服务 - API接口文档

> 模块版本：1.0.0-SNAPSHOT
> 创建日期：2026-02-17
> 文档作者：QooERP团队

---

## 一、接口概述

### 1.1 基础信息

| 项目 | 值 |
|------|-----|
| 服务名称 | qooerp-permission-service |
| 基础路径 | /api/permission |
| 协议 | HTTP/HTTPS |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |

### 1.2 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 响应码：200-成功 其他-失败 |
| message | String | 响应消息 |
| data | Object | 响应数据 |
| timestamp | Long | 时间戳 |

### 1.3 通用错误码

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| 200 | 操作成功 | 请求处理成功 |
| 400 | 请求参数错误 | 参数校验失败 |
| 401 | 未登录 | 用户未登录 |
| 403 | 无权限 | 权限不足 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 服务器内部错误 |

---

## 二、角色管理接口

### 2.1 创建角色

**接口地址：** `POST /api/permission/role`

**请求参数：**

```json
{
  "roleName": "销售经理",
  "roleCode": "SALES_MANAGER",
  "roleType": 2,
  "dataScope": 3,
  "deptIds": "1,2,3",
  "status": 1,
  "sort": 1,
  "remark": "销售部门经理角色"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleName | String | 是 | 角色名称 |
| roleCode | String | 是 | 角色编码（唯一） |
| roleType | Integer | 否 | 角色类型：1-系统角色 2-业务角色 |
| dataScope | Integer | 否 | 数据权限范围：1-全部 2-部门 3-部门及子部门 4-本人 5-自定义 |
| deptIds | String | 否 | 自定义部门ID（dataScope=5时使用） |
| status | Integer | 否 | 状态：0-禁用 1-启用 |
| sort | Integer | 否 | 排序号 |
| remark | String | 否 | 备注 |

**响应示例：**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1234567890000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| data | Long | 角色ID |

---

### 2.2 更新角色

**接口地址：** `PUT /api/permission/role/{id}`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 角色ID |

**请求参数：** 同创建角色

**响应示例：**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

### 2.3 删除角色

**接口地址：** `DELETE /api/permission/role/{id}`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 角色ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

### 2.4 查询角色详情

**接口地址：** `GET /api/permission/role/{id}`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 角色ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "roleName": "销售经理",
    "roleCode": "SALES_MANAGER",
    "roleType": 2,
    "dataScope": 3,
    "deptIds": "1,2,3",
    "status": 1,
    "sort": 1,
    "remark": "销售部门经理角色",
    "createTime": "2026-02-17T10:00:00",
    "updateTime": "2026-02-17T10:00:00"
  },
  "timestamp": 1234567890000
}
```

---

### 2.5 分页查询角色

**接口地址：** `GET /api/permission/role/page`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |
| roleName | String | 否 | 角色名称（模糊查询） |
| roleCode | String | 否 | 角色编码（模糊查询） |
| roleType | Integer | 否 | 角色类型 |
| status | Integer | 否 | 状态 |

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "roleName": "销售经理",
        "roleCode": "SALES_MANAGER",
        "roleType": 2,
        "dataScope": 3,
        "status": 1,
        "createTime": "2026-02-17T10:00:00"
      }
    ]
  },
  "timestamp": 1234567890000
}
```

---

### 2.6 分配菜单权限

**接口地址：** `POST /api/permission/role/{roleId}/menus`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | Long | 是 | 角色ID |

**请求参数：**

```json
[1, 2, 3, 4, 5]
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| - | Long[] | 是 | 菜单ID数组 |

**响应示例：**

```json
{
  "code": 200,
  "message": "分配成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

### 2.7 查询角色的菜单ID列表

**接口地址：** `GET /api/permission/role/{roleId}/menus`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | Long | 是 | 角色ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [1, 2, 3, 4, 5],
  "timestamp": 1234567890000
}
```

---

## 三、菜单管理接口

### 3.1 创建菜单

**接口地址：** `POST /api/permission/menu`

**请求参数：**

```json
{
  "parentId": 0,
  "menuName": "角色管理",
  "menuType": "C",
  "path": "/system/role",
  "component": "system/role/index",
  "permission": "system:role:list",
  "icon": "role",
  "sort": 1,
  "visible": 1,
  "isCache": 1,
  "remark": "角色管理菜单"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| parentId | Long | 否 | 父菜单ID |
| menuName | String | 是 | 菜单名称 |
| menuType | String | 是 | 菜单类型：M-目录 C-菜单 F-按钮 |
| path | String | 否 | 路由路径 |
| component | String | 否 | 组件路径 |
| permission | String | 否 | 权限标识 |
| icon | String | 否 | 图标 |
| sort | Integer | 否 | 排序号 |
| visible | Integer | 否 | 是否显示：0-隐藏 1-显示 |
| isCache | Integer | 否 | 是否缓存：0-不缓存 1-缓存 |
| remark | String | 否 | 备注 |

**响应示例：**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1234567890000
}
```

---

### 3.2 更新菜单

**接口地址：** `PUT /api/permission/menu/{id}`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 菜单ID |

**请求参数：** 同创建菜单

**响应示例：**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

### 3.3 删除菜单

**接口地址：** `DELETE /api/permission/menu/{id}`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 菜单ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

### 3.4 查询菜单树

**接口地址：** `GET /api/permission/menu/tree`

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "menuName": "系统管理",
      "menuType": "M",
      "path": "/system",
      "icon": "system",
      "sort": 1,
      "visible": 1,
      "children": [
        {
          "id": 11,
          "parentId": 1,
          "menuName": "角色管理",
          "menuType": "C",
          "path": "/system/role",
          "icon": "role",
          "sort": 1,
          "visible": 1,
          "children": []
        }
      ]
    }
  ],
  "timestamp": 1234567890000
}
```

---

### 3.5 查询用户菜单树

**接口地址：** `GET /api/permission/menu/user/tree`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例：** 同查询菜单树

---

## 四、用户角色接口

### 4.1 为用户分配角色

**接口地址：** `POST /api/permission/user/{userId}/roles`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |

**请求参数：**

```json
[1, 2, 3]
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| - | Long[] | 是 | 角色ID数组 |

**响应示例：**

```json
{
  "code": 200,
  "message": "分配成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

### 4.2 查询用户的角色列表

**接口地址：** `GET /api/permission/user/{userId}/roles`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "roleName": "销售经理",
      "roleCode": "SALES_MANAGER",
      "roleType": 2,
      "status": 1
    }
  ],
  "timestamp": 1234567890000
}
```

---

### 4.3 移除用户角色

**接口地址：** `DELETE /api/permission/user/{userId}/roles`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "移除成功",
  "data": null,
  "timestamp": 1234567890000
}
```

---

## 五、权限验证接口

### 5.1 验证用户权限

**接口地址：** `GET /api/permission/check`

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |
| permission | String | 是 | 权限标识 |

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": true,
  "timestamp": 1234567890000
}
```

---

### 5.2 获取用户权限列表

**接口地址：** `GET /api/permission/user/{userId}/permissions`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例：**

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    "system:role:list",
    "system:role:create",
    "system:role:update",
    "system:role:delete",
    "system:menu:list"
  ],
  "timestamp": 1234567890000
}
```

---

## 六、OpenAPI YAML

```yaml
openapi: 3.0.0
info:
  title: QooERP 权限服务 API
  description: QooERP 权限管理服务接口文档
  version: 1.0.0
  contact:
    name: QooERP Team
    email: team@qooerp.com

servers:
  - url: http://localhost:8083/api/permission
    description: 本地开发环境

tags:
  - name: 角色管理
    description: 角色相关接口
  - name: 菜单管理
    description: 菜单相关接口
  - name: 用户角色
    description: 用户角色相关接口
  - name: 权限验证
    description: 权限验证接口

paths:
  /role:
    post:
      tags:
        - 角色管理
      summary: 创建角色
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PermissionRoleRequest'
      responses:
        '200':
          description: 创建成功
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
                    example: 创建成功
                  data:
                    type: integer
                    description: 角色ID

    put:
      tags:
        - 角色管理
      summary: 更新角色
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PermissionRoleRequest'
      responses:
        '200':
          description: 更新成功

  /role/{id}:
    get:
      tags:
        - 角色管理
      summary: 查询角色详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PermissionRoleResponse'

    delete:
      tags:
        - 角色管理
      summary: 删除角色
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 删除成功

  /role/page:
    get:
      tags:
        - 角色管理
      summary: 分页查询角色
      parameters:
        - name: pageNum
          in: query
          required: true
          schema:
            type: integer
        - name: pageSize
          in: query
          required: true
          schema:
            type: integer
        - name: roleName
          in: query
          schema:
            type: string
        - name: roleCode
          in: query
          schema:
            type: string
        - name: roleType
          in: query
          schema:
            type: integer
        - name: status
          in: query
          schema:
            type: integer
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
                  data:
                    $ref: '#/components/schemas/PageResult'

  /role/{roleId}/menus:
    post:
      tags:
        - 角色管理
      summary: 分配菜单权限
      parameters:
        - name: roleId
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: array
              items:
                type: integer
      responses:
        '200':
          description: 分配成功

    get:
      tags:
        - 角色管理
      summary: 查询角色的菜单ID列表
      parameters:
        - name: roleId
          in: path
          required: true
          schema:
            type: integer
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
                  data:
                    type: array
                    items:
                      type: integer

  /menu:
    post:
      tags:
        - 菜单管理
      summary: 创建菜单
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PermissionMenuRequest'
      responses:
        '200':
          description: 创建成功

  /menu/{id}:
    put:
      tags:
        - 菜单管理
      summary: 更新菜单
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/PermissionMenuRequest'
      responses:
        '200':
          description: 更新成功

    delete:
      tags:
        - 菜单管理
      summary: 删除菜单
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 删除成功

  /menu/tree:
    get:
      tags:
        - 菜单管理
      summary: 查询菜单树
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/PermissionTreeVO'

  /menu/user/tree:
    get:
      tags:
        - 菜单管理
      summary: 查询用户菜单树
      parameters:
        - name: userId
          in: query
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功

  /user/{userId}/roles:
    post:
      tags:
        - 用户角色
      summary: 为用户分配角色
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: array
              items:
                type: integer
      responses:
        '200':
          description: 分配成功

    get:
      tags:
        - 用户角色
      summary: 查询用户的角色列表
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功

    delete:
      tags:
        - 用户角色
      summary: 移除用户角色
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 移除成功

  /user/{userId}/permissions:
    get:
      tags:
        - 权限验证
      summary: 获取用户权限列表
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功

components:
  schemas:
    PermissionRoleRequest:
      type: object
      properties:
        roleName:
          type: string
          description: 角色名称
        roleCode:
          type: string
          description: 角色编码
        roleType:
          type: integer
          description: 角色类型
        dataScope:
          type: integer
          description: 数据权限范围
        deptIds:
          type: string
          description: 自定义部门ID
        status:
          type: integer
          description: 状态
        sort:
          type: integer
          description: 排序号
        remark:
          type: string
          description: 备注

    PermissionRoleResponse:
      type: object
      properties:
        id:
          type: integer
        roleName:
          type: string
        roleCode:
          type: string
        roleType:
          type: integer
        dataScope:
          type: integer
        deptIds:
          type: string
        status:
          type: integer
        sort:
          type: integer
        remark:
          type: string
        createTime:
          type: string
          format: date-time
        updateTime:
          type: string
          format: date-time

    PermissionMenuRequest:
      type: object
      properties:
        parentId:
          type: integer
          description: 父菜单ID
        menuName:
          type: string
          description: 菜单名称
        menuType:
          type: string
          description: 菜单类型
        path:
          type: string
          description: 路由路径
        component:
          type: string
          description: 组件路径
        permission:
          type: string
          description: 权限标识
        icon:
          type: string
          description: 图标
        sort:
          type: integer
          description: 排序号
        visible:
          type: integer
          description: 是否显示
        isCache:
          type: integer
          description: 是否缓存
        remark:
          type: string
          description: 备注

    PermissionMenuResponse:
      type: object
      properties:
        id:
          type: integer
        parentId:
          type: integer
        menuName:
          type: string
        menuType:
          type: string
        path:
          type: string
        component:
          type: string
        permission:
          type: string
        icon:
          type: string
        sort:
          type: integer
        visible:
          type: integer
        isCache:
          type: integer
        remark:
          type: string
        createTime:
          type: string
          format: date-time
        updateTime:
          type: string
          format: date-time

    PermissionTreeVO:
      type: object
      properties:
        id:
          type: integer
        parentId:
          type: integer
        menuName:
          type: string
        menuType:
          type: string
        path:
          type: string
        icon:
          type: string
        sort:
          type: integer
        visible:
          type: integer
        children:
          type: array
          items:
            $ref: '#/components/schemas/PermissionTreeVO'

    PageResult:
      type: object
      properties:
        total:
          type: integer
          description: 总记录数
        pages:
          type: integer
          description: 总页数
        current:
          type: integer
          description: 当前页码
        size:
          type: integer
          description: 每页大小
        records:
          type: array
          items:
            $ref: '#/components/schemas/PermissionRoleResponse'
```

---

## 七、参考资料

- [OpenAPI 3.0 规范](https://swagger.io/specification/)
- [RESTful API 设计规范](https://restfulapi.net/)
