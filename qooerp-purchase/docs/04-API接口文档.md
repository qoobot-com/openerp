# QooERP 采购管理 - API接口文档

> 版本：v1.0
> 创建日期：2026-02-17
> 基础路径：/api/purchase

---

## 一、订单管理接口

### 1.1 创建采购订单

POST /api/purchase/orders

```json
{
  "supplierName": "供应商A",
  "orderDate": "2026-02-17",
  "deliveryDate": "2026-02-20",
  "details": [
    {
      "materialCode": "MAT001",
      "materialName": "原材料A",
      "quantity": 100,
      "unitPrice": 50.00
    }
  ]
}
```

### 1.2 订单审核

POST /api/purchase/orders/{id}/approve

### 1.3 订单收货

POST /api/purchase/orders/{id}/receive

### 1.4 查询订单

GET /api/purchase/orders

---

## 二、退货管理接口

### 2.1 创建退货单

POST /api/purchase/returns

```json
{
  "orderId": 1001,
  "reason": "质量问题",
  "details": [...]
}
```

---

## 三、错误码

| 错误码 | 说明 |
|--------|------|
| 60001 | 订单已存在 |
| 60002 | 供应商不存在 |
| 60003 | 订单已收货 |

---

*文档维护：请保持此文档的及时更新*
