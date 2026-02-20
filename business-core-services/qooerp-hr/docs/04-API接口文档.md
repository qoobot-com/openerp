# QooERP 人力资源管理 - API接口文档

> 版本：v1.0
> 创建日期：2026-02-17
> 服务名称：qooerp-hr
> 基础路径：/api/hr

---

## 一、接口概述

### 1.1 接口规范

| 规范项 | 说明 |
|--------|------|
| 协议 | HTTPS |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |
| 认证方式 | Bearer Token (JWT) |
| 分页方式 | Page + Size |
| 时间格式 | ISO 8601 (yyyy-MM-dd'T'HH:mm:ss.SSS'Z') |

### 1.2 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1708147200000
}
```

### 1.3 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": 1708147200000
}
```

---

## 二、员工管理接口

### 2.1 创建员工

**接口描述：** 创建新员工档案

**请求方式：** POST

**请求路径：** /api/hr/employees

**请求头：**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数：**
```json
{
  "employeeName": "张三",
  "gender": "M",
  "birthDate": "1990-01-01",
  "phoneNumber": "13800138000",
  "email": "zhangsan@example.com",
  "idCard": "110101199001011234",
  "organizationId": 1,
  "departmentId": 1,
  "positionId": 1,
  "hireDate": "2023-01-01",
  "employeeType": "FULLTIME",
  "education": "BACHELOR",
  "emergencyContact": "李四",
  "emergencyPhone": "13900139000"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1001,
  "timestamp": 1708147200000
}
```

---

### 2.2 更新员工

**接口描述：** 更新员工信息

**请求方式：** PUT

**请求路径：** /api/hr/employees/{id}

**请求参数：** 同创建员工

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1708147200000
}
```

---

### 2.3 删除员工

**接口描述：** 删除员工（软删除）

**请求方式：** DELETE

**请求路径：** /api/hr/employees/{id}

**响应示例：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1708147200000
}
```

---

### 2.4 获取员工详情

**接口描述：** 获取员工详细信息

**请求方式：** GET

**请求路径：** /api/hr/employees/{id}

**响应示例：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1001,
    "employeeCode": "EMP202601000001",
    "userId": 2001,
    "employeeName": "张三",
    "gender": "M",
    "birthDate": "1990-01-01",
    "phoneNumber": "13800138000",
    "email": "zhangsan@example.com",
    "idCard": "110101199001011234",
    "organizationId": 1,
    "departmentId": 1,
    "departmentName": "技术部",
    "positionId": 1,
    "positionName": "软件工程师",
    "hireDate": "2023-01-01",
    "status": "ACTIVE",
    "employeeType": "FULLTIME",
    "workNumber": "W2026010001",
    "education": "BACHELOR",
    "createTime": "2023-01-01T00:00:00.000Z"
  },
  "timestamp": 1708147200000
}
```

---

### 2.5 查询员工列表

**接口描述：** 分页查询员工列表

**请求方式：** GET

**请求路径：** /api/hr/employees

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页，默认1 |
| size | Integer | 否 | 每页大小，默认10 |
| employeeName | String | 否 | 员工姓名（模糊查询） |
| employeeCode | String | 否 | 员工编号 |
| departmentId | Long | 否 | 部门ID |
| positionId | Long | 否 | 岗位ID |
| status | String | 否 | 员工状态 |
| employeeType | String | 否 | 员工类型 |

**响应示例：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1001,
        "employeeCode": "EMP202601000001",
        "employeeName": "张三",
        "departmentName": "技术部",
        "positionName": "软件工程师",
        "status": "ACTIVE",
        "employeeType": "FULLTIME"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": 1708147200000
}
```

---

### 2.6 员工入职

**接口描述：** 员工入职审批

**请求方式：** POST

**请求路径：** /api/hr/employees/{id}/onboard

**请求参数：**
```json
{
  "approverId": 2001,
  "comment": "同意入职"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "入职成功",
  "data": null,
  "timestamp": 1708147200000
}
```

---

### 2.7 员工离职

**接口描述：** 员工离职申请

**请求方式：** POST

**请求路径：** /api/hr/employees/{id}/terminate

**请求参数：**
```json
{
  "reason": "个人原因",
  "approverId": 2001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "离职申请已提交",
  "data": null,
  "timestamp": 1708147200000
}
```

---

## 三、考勤管理接口

### 3.1 打卡

**接口描述：** 员工打卡

**请求方式：** POST

**请求路径：** /api/hr/attendance/check-in

**请求参数：**
```json
{
  "checkInType": "CHECK_IN",
  "location": "公司",
  "checkMethod": "APP"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "打卡成功",
  "data": {
    "attendanceId": 3001,
    "checkTime": "2026-02-17T08:55:00.000Z"
  },
  "timestamp": 1708147200000
}
```

---

### 3.2 请假申请

**接口描述：** 提交请假申请

**请求方式：** POST

**请求路径：** /api/hr/attendance/leave

**请求参数：**
```json
{
  "employeeId": 1001,
  "leaveType": "ANNUAL",
  "startDate": "2026-02-20",
  "endDate": "2026-02-22",
  "leaveDays": 3,
  "reason": "年假",
  "approverId": 2001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "请假申请已提交",
  "data": 4001,
  "timestamp": 1708147200000
}
```

---

### 3.3 请假审批

**接口描述：** 审批请假申请

**请求方式：** POST

**请求路径：** /api/hr/attendance/leave/{id}/approve

**请求参数：**
```json
{
  "approved": true,
  "comment": "同意请假",
  "approverId": 2001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "审批成功",
  "data": null,
  "timestamp": 1708147200000
}
```

---

### 3.4 加班申请

**接口描述：** 提交加班申请

**请求方式：** POST

**请求路径：** /api/hr/attendance/overtime

**请求参数：**
```json
{
  "employeeId": 1001,
  "overtimeType": "WEEKDAY",
  "overtimeDate": "2026-02-17",
  "startTime": "2026-02-17T18:00:00.000Z",
  "endTime": "2026-02-17T21:00:00.000Z",
  "reason": "项目紧急",
  "approverId": 2001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "加班申请已提交",
  "data": 5001,
  "timestamp": 1708147200000
}
```

---

### 3.5 加班审批

**接口描述：** 审批加班申请

**请求方式：** POST

**请求路径：** /api/hr/attendance/overtime/{id}/approve

**请求参数：** 同请假审批

---

### 3.6 出差申请

**接口描述：** 提交出差申请

**请求方式：** POST

**请求路径：** /api/hr/attendance/business-trip

**请求参数：**
```json
{
  "employeeId": 1001,
  "destination": "北京",
  "purpose": "客户拜访",
  "startDate": "2026-02-25",
  "endDate": "2026-02-27",
  "tripDays": 3,
  "budgetAmount": 5000.00,
  "approverId": 2001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "出差申请已提交",
  "data": 6001,
  "timestamp": 1708147200000
}
```

---

### 3.7 查询考勤记录

**接口描述：** 查询员工考勤记录

**请求方式：** GET

**请求路径：** /api/hr/attendance/records

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页 |
| size | Integer | 否 | 每页大小 |
| employeeId | Long | 否 | 员工ID |
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| checkInType | String | 否 | 打卡类型 |

---

## 四、薪资管理接口

### 4.1 计算月度薪资

**接口描述：** 计算指定月份的薪资

**请求方式：** POST

**请求路径：** /api/hr/salary/calculate

**请求参数：**
```json
{
  "salaryMonth": "2026-01"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "薪资计算已开始",
  "data": {
    "taskId": "TASK202601001"
  },
  "timestamp": 1708147200000
}
```

---

### 4.2 预览薪资

**接口描述：** 预览员工薪资

**请求方式：** GET

**请求路径：** /api/hr/salary/preview

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| employeeId | Long | 是 | 员工ID |
| salaryMonth | String | 是 | 薪资月份 |

**响应示例：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "employeeId": 1001,
    "employeeName": "张三",
    "salaryMonth": "2026-01",
    "baseSalary": 10000.00,
    "positionSalary": 2000.00,
    "performanceSalary": 1000.00,
    "overtimePay": 500.00,
    "bonus": 0.00,
    "allowance": 500.00,
    "grossSalary": 14000.00,
    "leaveDeduction": 0.00,
    "socialInsurancePersonal": 1400.00,
    "housingFundPersonal": 700.00,
    "incomeTax": 235.00,
    "otherDeduction": 0.00,
    "netSalary": 11665.00
  },
  "timestamp": 1708147200000
}
```

---

### 4.3 确认薪资

**接口描述：** 确认月度薪资

**请求方式：** POST

**请求路径：** /api/hr/salary/confirm

**请求参数：**
```json
{
  "salaryMonth": "2026-01"
}
```

---

### 4.4 发放薪资

**接口描述：** 发放月度薪资

**请求方式：** POST

**请求路径：** /api/hr/salary/pay

**请求参数：**
```json
{
  "salaryMonth": "2026-01",
  "paymentDate": "2026-02-25"
}
```

---

### 4.5 查询薪资记录

**接口描述：** 查询薪资记录

**请求方式：** GET

**请求路径：** /api/hr/salary/records

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页 |
| size | Integer | 否 | 每页大小 |
| employeeId | Long | 否 | 员工ID |
| salaryMonth | String | 否 | 薪资月份 |
| paymentStatus | String | 否 | 发放状态 |

---

## 五、绩效管理接口

### 5.1 创建考核计划

**接口描述：** 创建绩效考核计划

**请求方式：** POST

**请求路径：** /api/hr/performance/plan

**请求参数：**
```json
{
  "planName": "2026年第一季度绩效考核",
  "periodType": "QUARTERLY",
  "year": 2026,
  "quarter": 1,
  "startDate": "2026-01-01",
  "endDate": "2026-03-31",
  "selfAssessmentStart": "2026-04-01",
  "selfAssessmentEnd": "2026-04-07",
  "supervisorAssessmentStart": "2026-04-08",
  "supervisorAssessmentEnd": "2026-04-14"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 7001,
  "timestamp": 1708147200000
}
```

---

### 5.2 发布考核计划

**接口描述：** 发布考核计划

**请求方式：** POST

**请求路径：** /api/hr/performance/plan/{id}/publish

---

### 5.3 自我评估

**接口描述：** 员工自我评估

**请求方式：** POST

**请求路径：** /api/hr/performance/self-assessment

**请求参数：**
```json
{
  "planId": 7001,
  "employeeId": 1001,
  "selfScore": 90.00,
  "comment": "工作完成良好",
  "improvementSuggestion": "进一步提升技术能力"
}
```

---

### 5.4 上级评估

**接口描述：** 上级评估

**请求方式：** POST

**请求路径：** /api/hr/performance/supervisor-assessment

**请求参数：**
```json
{
  "planId": 7001,
  "employeeId": 1001,
  "assessorId": 2001,
  "supervisorScore": 85.00,
  "comment": "工作表现优秀",
  "improvementSuggestion": "加强团队协作"
}
```

---

### 5.5 综合评分

**接口描述：** 计算综合得分

**请求方式：** POST

**请求路径：** /api/hr/performance/calculate-score

**请求参数：**
```json
{
  "planId": 7001,
  "employeeId": 1001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "评分成功",
  "data": {
    "planId": 7001,
    "employeeId": 1001,
    "totalScore": 87.00,
    "grade": "A"
  },
  "timestamp": 1708147200000
}
```

---

### 5.6 查询绩效评估

**接口描述：** 查询绩效评估记录

**请求方式：** GET

**请求路径：** /api/hr/performance/assessments

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页 |
| size | Integer | 否 | 每页大小 |
| planId | Long | 否 | 计划ID |
| employeeId | Long | 否 | 员工ID |
| grade | String | 否 | 绩效等级 |

---

### 5.7 绩效排名

**接口描述：** 获取员工绩效排名

**请求方式：** GET

**请求路径：** /api/hr/performance/rank/{planId}

**响应示例：**
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "rank": 1,
      "employeeId": 1002,
      "employeeName": "李四",
      "totalScore": 95.00,
      "grade": "S"
    },
    {
      "rank": 2,
      "employeeId": 1001,
      "employeeName": "张三",
      "totalScore": 87.00,
      "grade": "A"
    }
  ],
  "timestamp": 1708147200000
}
```

---

## 六、错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 10001 | 员工已存在 |
| 10002 | 员工不存在 |
| 10003 | 员工状态异常 |
| 10004 | 考勤时间异常 |
| 10005 | 薪资已发放 |
| 10006 | 绩效计划已发布 |
| 10007 | 请假天数不足 |
| 10008 | 加班申请已存在 |

---

## 七、附录

### 7.1 相关文档

- 01-业务设计.md
- 02-应用设计.md
- 03-数据设计.md
- 05-技术设计.md
- 05-数据库脚本.sql

---

*文档维护：请保持此文档的及时更新，确保接口的准确性*
