# QooERP 供应链管理 - API接口文档

> 版本：v2.0
> 创建日期：2026-02-17
> 更新日期：2026-02-18
> 基础路径：/api/scm

---

## 一、供应商管理接口

### 1.1 创建供应商

**接口路径**: POST /api/scm/suppliers

**请求参数**:
```json
{
  "supplierName": "供应商A",
  "supplierType": "MANUFACTURER",
  "industry": "制造业",
  "scale": "LARGE",
  "contactPerson": "张三",
  "contactPhone": "13800138000",
  "contactEmail": "zhangsan@example.com",
  "address": "北京市朝阳区",
  "taxNumber": "91110000123456789X",
  "bankName": "工商银行",
  "bankAccount": "6222021234567890",
  "creditLevel": "A",
  "creditLimit": 5000000.00,
  "paymentMethod": "MONTHLY",
  "paymentDays": 30,
  "remark": "优质供应商"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": 1
}
```

### 1.2 更新供应商

**接口路径**: PUT /api/scm/suppliers/{id}

**请求参数**:
```json
{
  "supplierName": "供应商A（更新）",
  "contactPerson": "李四",
  "creditLevel": "B"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 1.3 删除供应商

**接口路径**: DELETE /api/scm/suppliers/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 1.4 查询供应商

**接口路径**: GET /api/scm/suppliers/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "supplierCode": "SP20260218001",
    "supplierName": "供应商A",
    "supplierType": "MANUFACTURER",
    "creditLevel": "A",
    "status": "ACTIVE"
  }
}
```

### 1.5 分页查询供应商

**接口路径**: GET /api/scm/suppliers

**请求参数**:
- supplierCode: 供应商编码
- supplierName: 供应商名称
- supplierType: 供应商类型
- creditLevel: 信用等级
- status: 状态
- current: 当前页码
- size: 每页条数

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "current": 1,
    "size": 10,
    "records": [...]
  }
}
```

### 1.6 评估供应商

**接口路径**: POST /api/scm/suppliers/{id}/evaluate

**请求参数**:
```json
{
  "qualityScore": 90,
  "deliveryScore": 85,
  "serviceScore": 88,
  "priceScore": 82,
  "evaluationRemark": "整体表现优秀"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 1.7 获取评估历史

**接口路径**: GET /api/scm/suppliers/{id}/evaluations

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "evaluationDate": "2026-01-01",
      "qualityScore": 90,
      "deliveryScore": 85,
      "serviceScore": 88,
      "priceScore": 82,
      "totalScore": 86,
      "creditLevel": "A"
    }
  ]
}
```

---

## 二、客户管理接口

### 2.1 创建客户

**接口路径**: POST /api/scm/customers

**请求参数**:
```json
{
  "customerName": "客户A",
  "customerType": "ENTERPRISE",
  "industry": "零售业",
  "scale": "MEDIUM",
  "customerLevel": "VIP",
  "contactPerson": "李四",
  "contactPhone": "13900139000",
  "contactEmail": "lisi@example.com",
  "address": "上海市浦东新区",
  "taxNumber": "91310000987654321Y",
  "bankName": "建设银行",
  "bankAccount": "6222020987654321",
  "creditLimit": 10000000.00,
  "paymentMethod": "MONTHLY",
  "paymentDays": 30,
  "remark": "VIP客户"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": 1
}
```

### 2.2 更新客户

**接口路径**: PUT /api/scm/customers/{id}

**请求参数**:
```json
{
  "customerName": "客户A（更新）",
  "contactPerson": "王五",
  "customerLevel": "IMPORTANT"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 2.3 删除客户

**接口路径**: DELETE /api/scm/customers/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 2.4 查询客户

**接口路径**: GET /api/scm/customers/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "customerCode": "CU20260218001",
    "customerName": "客户A",
    "customerType": "ENTERPRISE",
    "customerLevel": "VIP",
    "status": "ACTIVE"
  }
}
```

### 2.5 分页查询客户

**接口路径**: GET /api/scm/customers

**请求参数**:
- customerCode: 客户编码
- customerName: 客户名称
- customerType: 客户类型
- customerLevel: 客户等级
- status: 状态
- current: 当前页码
- size: 每页条数

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "current": 1,
    "size": 10,
    "records": [...]
  }
}
```

### 2.6 更新客户等级

**接口路径**: PUT /api/scm/customers/{id}/level

**请求参数**:
```json
{
  "level": "VIP",
  "changeReason": "年度采购额超过1000万"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 2.7 获取分级历史

**接口路径**: GET /api/scm/customers/{id}/grades

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "gradeDate": "2026-01-01",
      "annualPurchaseAmount": 12000000.00,
      "purchaseCount": 100,
      "averageOrderAmount": 120000.00,
      "returnRate": 2.5,
      "customerLevel": "VIP",
      "changeReason": "年度采购额超过1000万"
    }
  ]
}
```

---

## 三、物流管理接口

### 3.1 创建物流

**接口路径**: POST /api/scm/logistics

**请求参数**:
```json
{
  "businessType": "SALES",
  "relatedOrderId": 1001,
  "relatedOrderCode": "SO20260218001",
  "logisticsCompany": "顺丰快递",
  "trackingNumber": "SF1234567890",
  "sender": "张三",
  "senderPhone": "13800138000",
  "senderAddress": "北京市朝阳区",
  "receiver": "李四",
  "receiverPhone": "13900139000",
  "receiverAddress": "上海市浦东新区",
  "shipmentDate": "2026-02-18",
  "estimatedArrivalDate": "2026-02-20",
  "logisticsCost": 50.00
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": 1
}
```

### 3.2 更新物流

**接口路径**: PUT /api/scm/logistics/{id}

**请求参数**:
```json
{
  "logisticsCompany": "中通快递",
  "trackingNumber": "ZT0987654321"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 3.3 物流跟踪

**接口路径**: POST /api/scm/logistics/{id}/track

**请求参数**:
```json
{
  "status": "IN_TRANSIT",
  "location": "上海转运中心",
  "description": "到达上海转运中心"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 3.4 查询物流跟踪

**接口路径**: GET /api/scm/logistics/{id}/tracking

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "trackingTime": "2026-02-18 10:00:00",
      "location": "北京市朝阳区",
      "status": "SHIPPED",
      "description": "已发货"
    },
    {
      "trackingTime": "2026-02-19 08:00:00",
      "location": "上海转运中心",
      "status": "IN_TRANSIT",
      "description": "到达上海转运中心"
    }
  ]
}
```

### 3.5 分页查询物流

**接口路径**: GET /api/scm/logistics

**请求参数**:
- logisticsCode: 物流单号
- businessType: 业务类型
- relatedOrderCode: 关联单据号
- logisticsStatus: 物流状态
- current: 当前页码
- size: 每页条数

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "current": 1,
    "size": 10,
    "records": [...]
  }
}
```

### 3.6 更新物流状态

**接口路径**: PUT /api/scm/logistics/{id}/status

**请求参数**:
```json
{
  "status": "DELIVERED",
  "actualArrivalDate": "2026-02-20"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

---

## 四、供应商报价接口

### 4.1 创建报价单

**接口路径**: POST /api/scm/quotations

**请求参数**:
```json
{
  "supplierId": 1,
  "quotationDate": "2026-02-18",
  "validUntil": "2026-03-18",
  "quotationType": "NORMAL",
  "details": [
    {
      "materialCode": "M001",
      "materialName": "物料A",
      "specification": "规格A",
      "unit": "个",
      "quantity": 100,
      "unitPrice": 10.00,
      "deliveryDays": 7
    }
  ],
  "remark": "报价备注"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": 1
}
```

### 4.2 更新报价单

**接口路径**: PUT /api/scm/quotations/{id}

**请求参数**:
```json
{
  "validUntil": "2026-03-20",
  "remark": "报价备注更新"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 4.3 删除报价单

**接口路径**: DELETE /api/scm/quotations/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 4.4 查询报价单

**接口路径**: GET /api/scm/quotations/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "quotationCode": "QT20260218001",
    "supplierId": 1,
    "supplierName": "供应商A",
    "quotationDate": "2026-02-18",
    "validUntil": "2026-03-18",
    "totalAmount": 1000.00,
    "auditStatus": "PENDING",
    "converted": false,
    "details": [...]
  }
}
```

### 4.5 分页查询报价单

**接口路径**: GET /api/scm/quotations

**请求参数**:
- quotationCode: 报价单号
- supplierId: 供应商ID
- supplierName: 供应商名称
- auditStatus: 审核状态
- converted: 是否转换
- current: 当前页码
- size: 每页条数

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "current": 1,
    "size": 10,
    "records": [...]
  }
}
```

### 4.6 审核报价单

**接口路径**: POST /api/scm/quotations/{id}/approve

**请求参数**:
```json
{
  "auditStatus": "APPROVED",
  "auditRemark": "报价合理，审核通过"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 4.7 报价转采购订单

**接口路径**: POST /api/scm/quotations/{id}/convert

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "purchaseOrderId": 1001,
    "purchaseOrderCode": "PO20260218001"
  }
}
```

### 4.8 报价对比

**接口路径**: POST /api/scm/quotations/compare

**请求参数**:
```json
{
  "materialCodes": ["M001", "M002"],
  "startDate": "2026-02-01",
  "endDate": "2026-02-18"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "materialCode": "M001",
      "materialName": "物料A",
      "quotations": [
        {
          "supplierId": 1,
          "supplierName": "供应商A",
          "unitPrice": 10.00,
          "deliveryDays": 7
        },
        {
          "supplierId": 2,
          "supplierName": "供应商B",
          "unitPrice": 9.50,
          "deliveryDays": 10
        }
      ],
      "bestSupplier": {
        "supplierId": 2,
        "supplierName": "供应商B"
      }
    }
  ]
}
```

---

## 五、供应商对账接口

### 5.1 创建对账单

**接口路径**: POST /api/scm/statements

**请求参数**:
```json
{
  "supplierId": 1,
  "statementType": "MONTHLY",
  "startDate": "2026-01-01",
  "endDate": "2026-01-31",
  "statementDate": "2026-02-01",
  "remark": "1月份对账单"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": 1
}
```

### 5.2 自动生成对账单

**接口路径**: POST /api/scm/statements/auto

**请求参数**:
```json
{
  "supplierId": 1,
  "statementType": "MONTHLY",
  "statementYear": 2026,
  "statementMonth": 1
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "statementId": 1,
    "statementCode": "ST20260201001",
    "totalAmount": 50000.00
  }
}
```

### 5.3 更新对账单

**接口路径**: PUT /api/scm/statements/{id}

**请求参数**:
```json
{
  "freightAmount": 500.00,
  "otherAmount": 100.00,
  "remark": "对账备注更新"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 5.4 删除对账单

**接口路径**: DELETE /api/scm/statements/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 5.5 查询对账单

**接口路径**: GET /api/scm/statements/{id}

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "statementCode": "ST20260201001",
    "supplierId": 1,
    "supplierName": "供应商A",
    "statementType": "MONTHLY",
    "startDate": "2026-01-01",
    "endDate": "2026-01-31",
    "statementDate": "2026-02-01",
    "purchaseAmount": 50000.00,
    "returnAmount": 2000.00,
    "freightAmount": 500.00,
    "otherAmount": 100.00,
    "totalAmount": 48600.00,
    "auditStatus": "PENDING",
    "settlementStatus": "PENDING",
    "details": [...]
  }
}
```

### 5.6 分页查询对账单

**接口路径**: GET /api/scm/statements

**请求参数**:
- statementCode: 对账单号
- supplierId: 供应商ID
- supplierName: 供应商名称
- statementType: 对账类型
- auditStatus: 审核状态
- settlementStatus: 结算状态
- startDate: 对账起始日期
- endDate: 对账结束日期
- current: 当前页码
- size: 每页条数

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "current": 1,
    "size": 10,
    "records": [...]
  }
}
```

### 5.7 审核对账单

**接口路径**: POST /api/scm/statements/{id}/approve

**请求参数**:
```json
{
  "auditStatus": "APPROVED",
  "auditRemark": "对账无误，审核通过"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功"
}
```

### 5.8 对账结算

**接口路径**: POST /api/scm/statements/{id}/settle

**请求参数**:
```json
{
  "settlementMethod": "BANK_TRANSFER",
  "settlementDate": "2026-02-05"
}
```

**响应参数**:
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "payableId": 2001,
    "payableCode": "AP20260205001"
  }
}
```

---

## 六、错误码

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| SCM_001 | 供应商编码已存在 | 指定的供应商编码已存在 |
| SCM_002 | 客户编码已存在 | 指定的客户编码已存在 |
| SCM_003 | 物流单号已存在 | 指定的物流单号已存在 |
| SCM_004 | 供应商不存在 | 指定的供应商不存在 |
| SCM_005 | 客户不存在 | 指定的客户不存在 |
| SCM_006 | 物流不存在 | 指定的物流不存在 |
| SCM_007 | 评估分数无效 | 评估分数必须在0-100之间 |
| SCM_008 | 客户等级无效 | 无效的客户等级 |
| SCM_009 | 报价单号已存在 | 指定的报价单号已存在 |
| SCM_010 | 报价单已审核 | 报价单已审核，不能修改 |
| SCM_011 | 报价单已转换 | 报价单已转采购订单 |
| SCM_012 | 对账单号已存在 | 指定的对账单号已存在 |
| SCM_013 | 对账单已审核 | 对账单已审核，不能修改 |
| SCM_014 | 对账单已结算 | 对账单已结算 |
| SCM_015 | 报价已过期 | 报价已过期，不能转换 |

---

*文档维护：请保持此文档的及时更新*
