# QooERP 销售管理 - API接口文档

> 版本：v1.0
> 创建日期：2026-02-17
> 基础路径：/api/sales

---

## 一、订单管理接口

### 1.1 创建销售订单

POST /api/sales/orders

```json
{
  "customerName": "客户A",
  "orderDate": "2026-02-17",
  "deliveryDate": "2026-02-20",
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

### 1.2 订单审核

POST /api/sales/orders/{id}/approve

### 1.3 订单发货

POST /api/sales/orders/{id}/ship

### 1.4 查询订单

GET /api/sales/orders

---

## 二、退货管理接口

### 2.1 创建退货单

POST /api/sales/returns

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
| 40001 | 订单已存在 |
| 40002 | 库存不足 |
| 40003 | 订单已发货 |

---

*文档维护：请保持此文档的及时更新*
