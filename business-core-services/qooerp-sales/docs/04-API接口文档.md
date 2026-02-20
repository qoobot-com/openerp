# QooERP 销售管理 - API接口文档

> 版本：v1.0
> 创建日期：20xx-xx-xx
> 基础路径：/api/sales

---

## 一、报价管理接口

### 1.1 创建销售报价

POST /api/sales/quotations

```json
{
  "customerName": "客户A",
  "quotationDate": "20xx-xx-xx",
  "validUntil": "2026-03-17",
  "salespersonId": 1001,
  "details": [
    {
      "materialCode": "MAT001",
      "materialName": "商品A",
      "quantity": 100,
      "unitPrice": 50.00
    }
  ]
}
```

### 1.2 审核报价

POST /api/sales/quotations/{id}/approve

### 1.3 报价转订单

POST /api/sales/quotations/{id}/convert

### 1.4 查询报价

GET /api/sales/quotations

---

## 二、合同管理接口

### 2.1 创建销售合同

POST /api/sales/contracts

```json
{
  "customerName": "客户A",
  "contractDate": "20xx-xx-xx",
  "effectiveDate": "20xx-xx-xx",
  "expiryDate": "2027-02-20",
  "details": [...]
}
```

### 2.2 审核合同

POST /api/sales/contracts/{id}/approve

### 2.3 激活合同

POST /api/sales/contracts/{id}/activate

### 2.4 查询合同

GET /api/sales/contracts

---

## 三、订单管理接口

### 3.1 创建销售订单

POST /api/sales/orders

```json
{
  "customerName": "客户A",
  "orderDate": "20xx-xx-xx",
  "deliveryDate": "20xx-xx-xx",
  "details": [
    {
      "materialCode": "MAT001",
      "materialName": "商品A",
      "quantity": 100,
      "unitPrice": 50.00
    }
  ]
}
```

### 3.2 订单审核

POST /api/sales/orders/{id}/approve

### 3.3 订单发货

POST /api/sales/orders/{id}/ship

### 3.4 取消订单

POST /api/sales/orders/{id}/cancel

### 3.5 查询订单

GET /api/sales/orders

---

## 四、发货管理接口

### 4.1 创建发货单

POST /api/sales/deliveries

```json
{
  "orderId": 1001,
  "deliveryDate": "20xx-xx-xx",
  "details": [...]
}
```

### 4.2 确认发货

POST /api/sales/deliveries/{id}/ship

### 4.3 确认签收

POST /api/sales/deliveries/{id}/receive

### 4.4 查询发货单

GET /api/sales/deliveries

---

## 五、退货管理接口

### 5.1 创建退货单

POST /api/sales/returns

```json
{
  "orderId": 1001,
  "reason": "质量问题",
  "details": [...]
}
```

### 5.2 审核退货

POST /api/sales/returns/{id}/approve

### 5.3 处理退货

POST /api/sales/returns/{id}/process

### 5.4 查询退货单

GET /api/sales/returns

---

## 六、收款管理接口

### 6.1 创建收款单

POST /api/sales/receipts

```json
{
  "customerName": "客户A",
  "receiptDate": "20xx-xx-xx",
  "receiptAmount": 5000.00,
  "paymentMethod": "BANK_TRANSFER",
  "details": [
    {
      "orderId": 1001,
      "writeoffAmount": 5000.00
    }
  ]
}
```

### 6.2 确认收款

POST /api/sales/receipts/{id}/confirm

### 6.3 查询收款单

GET /api/sales/receipts

---

## 七、客户信用接口

### 7.1 设置信用额度

POST /api/sales/customer-credit/{customerId}

```json
{
  "creditLimit": 100000.00
}
```

### 7.2 检查信用额度

GET /api/sales/customer-credit/{customerId}/check?amount=50000

### 7.3 查询客户信用

GET /api/sales/customer-credit/{customerId}

---

## 八、报表分析接口

### 8.1 销售统计

GET /api/sales/reports/sales-statistics

### 8.2 销售分析

GET /api/sales/reports/sales-analysis

### 8.3 业绩分析

GET /api/sales/reports/performance-analysis

---

## 九、错误码

| 错误码 | 说明 |
|--------|------|
| 40001 | 报价单已存在 |
| 40002 | 报价单已过期 |
| 40003 | 报价单已转换 |
| 41001 | 合同已存在 |
| 41002 | 合同已激活 |
| 41003 | 合同已终止 |
| 42001 | 订单已存在 |
| 42002 | 订单已发货 |
| 42003 | 订单已完成 |
| 42004 | 订单已取消 |
| 43001 | 库存不足 |
| 43002 | 发货单已发货 |
| 44001 | 退货单已处理 |
| 45001 | 收款单已确认 |
| 46001 | 超出信用额度 |
| 46002 | 信用额度不足 |

---

*文档维护：请保持此文档的及时更新*
