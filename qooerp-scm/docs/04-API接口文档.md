# QooERP 供应链管理 - API接口文档

> 版本：v1.0
> 创建日期：2026-02-17
> 基础路径：/api/scm

---

## 一、供应商管理接口

### 1.1 创建供应商

POST /api/scm/suppliers

```json
{
  "supplierName": "供应商A",
  "supplierType": "MANUFACTURER",
  "contactPerson": "张三",
  "contactPhone": "13800138000",
  "address": "北京市朝阳区",
  "creditLevel": "A"
}
```

### 1.2 查询供应商

GET /api/scm/suppliers

参数：current, size, supplierName, status

---

## 二、客户管理接口

### 2.1 创建客户

POST /api/scm/customers

```json
{
  "customerName": "客户A",
  "customerType": "ENTERPRISE",
  "customerLevel": "VIP",
  "contactPerson": "李四",
  "contactPhone": "13900139000"
}
```

### 2.2 查询客户

GET /api/scm/customers

---

## 三、物流管理接口

### 3.1 创建物流

POST /api/scm/logistics

```json
{
  "businessType": "SALES",
  "relatedOrderId": 1001,
  "logisticsCompany": "顺丰快递",
  "trackingNumber": "SF1234567890",
  "sender": "张三",
  "receiver": "李四",
  "receiverAddress": "上海市浦东新区",
  "shipmentDate": "2026-02-17"
}
```

### 3.2 物流跟踪

POST /api/scm/logistics/{id}/track

```json
{
  "status": "IN_TRANSIT",
  "location": "上海转运中心"
}
```

### 3.3 查询物流

GET /api/scm/logistics

---

## 四、错误码

| 错误码 | 说明 |
|--------|------|
| 30001 | 供应商已存在 |
| 30002 | 客户已存在 |
| 30003 | 物流单号已存在 |

---

*文档维护：请保持此文档的及时更新*
