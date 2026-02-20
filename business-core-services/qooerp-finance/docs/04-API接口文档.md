# QooERP 财务管理 - API接口文档

> 版本：v1.0
> 创建日期：20xx-xx-xx
> 基础路径：/api/finance

---

## 一、总账管理接口

### 1.1 创建凭证

**接口描述：** 创建会计凭证

**请求方式：** POST

**请求路径：** /api/finance/vouchers

**请求参数：**
```json
{
  "voucherDate": "20xx-xx-xx",
  "voucherType": "RECEIPT",
  "accountingPeriod": "20xx-xx",
  "summary": "收到客户货款",
  "details": [
    {
      "accountCode": "1001",
      "accountName": "库存现金",
      "direction": "DEBIT",
      "amount": 10000.00,
      "summary": "收到客户货款"
    },
    {
      "accountCode": "1122",
      "accountName": "应收账款",
      "direction": "CREDIT",
      "amount": 10000.00,
      "summary": "收到客户货款",
      "auxiliaryType": "CUSTOMER",
      "auxiliaryId": 1
    }
  ]
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

### 1.2 审核凭证

**接口描述：** 审核会计凭证

**请求方式：** POST

**请求路径：** /api/finance/vouchers/{id}/review

**请求参数：**
```json
{
  "reviewerId": 2001
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "审核成功",
  "data": null,
  "timestamp": 1708147200000
}
```

---

### 1.3 记账凭证

**接口描述：** 记账会计凭证

**请求方式：** POST

**请求路径：** /api/finance/vouchers/{id}/post

**响应示例：**
```json
{
  "code": 200,
  "message": "记账成功",
  "data": null,
  "timestamp": 1708147200000
}
```

---

### 1.4 查询凭证

**接口描述：** 查询凭证列表

**请求方式：** GET

**请求路径：** /api/finance/vouchers

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页 |
| size | Integer | 否 | 每页大小 |
| voucherDate | String | 否 | 凭证日期 |
| voucherType | String | 否 | 凭证类型 |
| accountingPeriod | String | 否 | 会计期间 |
| reviewStatus | String | 否 | 审核状态 |

---

## 二、应收管理接口

### 2.1 创建应收账款

**接口描述：** 创建应收账款

**请求方式：** POST

**请求路径：** /api/finance/receivables

**请求参数：**
```json
{
  "customerId": 1,
  "customerName": "客户A",
  "businessType": "SALES",
  "relatedOrderId": 1001,
  "relatedOrderCode": "SO2026020001",
  "receivableAmount": 50000.00,
  "receivableDate": "20xx-xx-xx",
  "dueDate": "2026-03-17"
}
```

---

### 2.2 收款登记

**接口描述：** 登记收款

**请求方式：** POST

**请求路径：** /api/finance/receipts

**请求参数：**
```json
{
  "receivableId": 1001,
  "customerId": 1,
  "customerName": "客户A",
  "receiptAmount": 50000.00,
  "receiptDate": "20xx-xx-xx",
  "paymentMethod": "BANK_TRANSFER",
  "bankAccount": "6225881234567890"
}
```

---

### 2.3 核销应收

**接口描述：** 核销应收账款

**请求方式：** POST

**请求路径：** /api/finance/receivables/{id}/write-off

**请求参数：**
```json
{
  "receiptId": 2001,
  "amount": 50000.00
}
```

---

### 2.4 应收账龄分析

**接口描述：** 应收账款账龄分析

**请求方式：** GET

**请求路径：** /api/finance/receivables/aging-analysis

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| asOfDate | String | 否 | 截止日期（默认今天） |

---

## 三、应付管理接口

### 3.1 创建应付账款

**接口描述：** 创建应付账款

**请求方式：** POST

**请求路径：** /api/finance/payables

**请求参数：**
```json
{
  "supplierId": 1,
  "supplierName": "供应商A",
  "businessType": "PURCHASE",
  "relatedOrderId": 1001,
  "relatedOrderCode": "PO2026020001",
  "payableAmount": 30000.00,
  "payableDate": "20xx-xx-xx",
  "dueDate": "2026-03-17"
}
```

---

### 3.2 付款登记

**接口描述：** 登记付款

**请求方式：** POST

**请求路径：** /api/finance/payments

**请求参数：**
```json
{
  "payableId": 1001,
  "supplierId": 1,
  "supplierName": "供应商A",
  "paymentAmount": 30000.00,
  "paymentDate": "20xx-xx-xx",
  "paymentMethod": "BANK_TRANSFER",
  "bankAccount": "6225880987654321"
}
```

---

### 3.3 核销应付

**接口描述：** 核销应付账款

**请求方式：** POST

**请求路径：** /api/finance/payables/{id}/write-off

---

### 3.4 应付账龄分析

**接口描述：** 应付账款账龄分析

**请求方式：** GET

**请求路径：** /api/finance/payables/aging-analysis

---

## 四、资产管理接口

### 4.1 创建资产

**接口描述：** 创建固定资产

**请求方式：** POST

**请求路径：** /api/finance/assets

**请求参数：**
```json
{
  "assetName": "办公电脑",
  "categoryId": 1,
  "categoryName": "电子设备",
  "specification": "联想ThinkPad",
  "unit": "台",
  "quantity": 10,
  "originalValue": 50000.00,
  "departmentId": 1,
  "departmentName": "技术部",
  "userId": 1001,
  "userName": "张三",
  "location": "办公室A座",
  "entryDate": "20xx-xx-xx",
  "startDate": "20xx-xx-xx",
  "usefulLife": 3,
  "depreciationMethod": "STRAIGHT_LINE"
}
```

---

### 4.2 资产处置

**接口描述：** 资产处置

**请求方式：** POST

**请求路径：** /api/finance/assets/{id}/disposal

**请求参数：**
```json
{
  "disposalType": "SELL",
  "disposalAmount": 20000.00
}
```

---

### 4.3 月度折旧

**接口描述：** 计算月度折旧

**请求方式：** POST

**请求路径：** /api/finance/assets/depreciation

**请求参数：**
```json
{
  "accountingPeriod": "20xx-xx"
}
```

---

## 五、财务报表接口

### 5.1 资产负债表

**接口描述：** 生成资产负债表

**请求方式：** GET

**请求路径：** /api/finance/reports/balance-sheet

**请求参数：**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| accountingPeriod | String | 是 | 会计期间 |

---

### 5.2 利润表

**接口描述：** 生成利润表

**请求方式：** GET

**请求路径：** /api/finance/reports/income-statement

---

### 5.3 现金流量表

**接口描述：** 生成现金流量表

**请求方式：** GET

**请求路径：** /api/finance/reports/cash-flow-statement

---

### 5.4 科目余额表

**接口描述：** 生成科目余额表

**请求方式：** GET

**请求路径：** /api/finance/reports/account-balance

---

## 六、错误码说明

| 错误码 | 说明 |
|--------|------|
| 20001 | 借贷不平衡 |
| 20002 | 科目不存在 |
| 20003 | 科目不能录入 |
| 20004 | 凭证已审核 |
| 20005 | 凭证已记账 |
| 20006 | 金额不足 |
| 20007 | 已全额核销 |

---

*文档维护：请保持此文档的及时更新，确保接口的准确性*
