# QooERP 采购管理 - API接口文档

> 版本：v1.0
> 创建日期：2026-02-17
> 基础路径：/api/purchase

---

## 一、订单管理接口

### 1.1 创建采购订单

**POST** `/api/purchase/orders`

**请求体：**
```json
{
  "supplierId": 1001,
  "supplierName": "供应商A",
  "orderDate": "2026-02-17",
  "deliveryDate": "2026-02-20",
  "warehouseId": 2001,
  "warehouseName": "主仓库",
  "remark": "紧急订单",
  "details": [
    {
      "materialId": 3001,
      "materialCode": "MAT001",
      "materialName": "原材料A",
      "specification": "规格A",
      "unit": "件",
      "quantity": 100,
      "unitPrice": 50.00,
      "discountRate": 0.05,
      "remark": ""
    }
  ]
}
```

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": 10001
}
```

### 1.2 更新采购订单

**PUT** `/api/purchase/orders/{id}`

**请求体：**
```json
{
  "id": 10001,
  "deliveryDate": "2026-02-22",
  "warehouseId": 2001,
  "warehouseName": "主仓库",
  "remark": "更新备注",
  "details": [
    {
      "materialId": 3001,
      "materialCode": "MAT001",
      "materialName": "原材料A",
      "specification": "规格A",
      "unit": "件",
      "quantity": 150,
      "unitPrice": 48.00,
      "discountRate": 0.05,
      "remark": ""
    }
  ]
}
```

### 1.3 删除采购订单

**DELETE** `/api/purchase/orders/{id}`

### 1.4 提交审核

**POST** `/api/purchase/orders/{id}/submit`

### 1.5 订单审核

**POST** `/api/purchase/orders/{id}/approve`

### 1.6 驳回订单

**POST** `/api/purchase/orders/{id}/reject`

**请求体：**
```json
{
  "reason": "价格不合理"
}
```

### 1.7 取消订单

**POST** `/api/purchase/orders/{id}/cancel`

### 1.8 完成订单

**POST** `/api/purchase/orders/{id}/complete`

### 1.9 获取订单详情

**GET** `/api/purchase/orders/{id}`

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10001,
    "orderCode": "PO202602170001",
    "supplierId": 1001,
    "supplierName": "供应商A",
    "orderDate": "2026-02-17",
    "deliveryDate": "2026-02-20",
    "orderAmount": 5000.00,
    "discountAmount": 250.00,
    "payableAmount": 4750.00,
    "orderStatus": "APPROVED",
    "orderStatusName": "已审核",
    "purchaserId": 5001,
    "purchaserName": "张三",
    "warehouseId": 2001,
    "warehouseName": "主仓库",
    "remark": "紧急订单",
    "createTime": "2026-02-17T10:30:00",
    "details": [
      {
        "id": 20001,
        "materialId": 3001,
        "materialCode": "MAT001",
        "materialName": "原材料A",
        "specification": "规格A",
        "unit": "件",
        "quantity": 100,
        "unitPrice": 50.00,
        "amount": 5000.00,
        "discountRate": 0.05,
        "discountAmount": 250.00,
        "payableAmount": 4750.00,
        "receivedQuantity": 0,
        "remark": ""
      }
    ]
  }
}
```

### 1.10 分页查询订单

**GET** `/api/purchase/orders`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认10 |
| orderCode | string | 否 | 订单编号 |
| supplierId | long | 否 | 供应商ID |
| orderStatus | string | 否 | 订单状态 |
| startDate | string | 否 | 开始日期(yyyy-MM-dd) |
| endDate | string | 否 | 结束日期(yyyy-MM-dd) |

---

## 二、入库管理接口

### 2.1 创建入库单

**POST** `/api/purchase/receipts`

**请求体：**
```json
{
  "orderId": 10001,
  "receiptDate": "2026-02-18",
  "warehouseId": 2001,
  "warehouseName": "主仓库",
  "remark": "",
  "details": [
    {
      "orderDetailId": 20001,
      "materialId": 3001,
      "materialCode": "MAT001",
      "materialName": "原材料A",
      "specification": "规格A",
      "unit": "件",
      "quantity": 100,
      "unitPrice": 50.00,
      "batchNo": "BT20260218",
      "productionDate": "2026-01-15",
      "expiryDate": "2027-01-15",
      "remark": ""
    }
  ]
}
```

### 2.2 删除入库单

**DELETE** `/api/purchase/receipts/{id}`

### 2.3 审核入库单

**POST** `/api/purchase/receipts/{id}/approve`

### 2.4 取消入库单

**POST** `/api/purchase/receipts/{id}/cancel`

### 2.5 获取入库单详情

**GET** `/api/purchase/receipts/{id}`

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10001,
    "receiptCode": "PR202602180001",
    "orderId": 10001,
    "orderCode": "PO202602170001",
    "supplierId": 1001,
    "supplierName": "供应商A",
    "receiptDate": "2026-02-18",
    "receiptAmount": 5000.00,
    "receiptStatus": "COMPLETED",
    "receiptStatusName": "已完成",
    "warehouseId": 2001,
    "warehouseName": "主仓库",
    "operatorId": 5001,
    "operatorName": "张三",
    "remark": "",
    "createTime": "2026-02-18T09:00:00",
    "details": [
      {
        "id": 20001,
        "materialId": 3001,
        "materialCode": "MAT001",
        "materialName": "原材料A",
        "specification": "规格A",
        "unit": "件",
        "quantity": 100,
        "unitPrice": 50.00,
        "amount": 5000.00,
        "batchNo": "BT20260218",
        "productionDate": "2026-01-15",
        "expiryDate": "2027-01-15",
        "remark": ""
      }
    ]
  }
}
```

### 2.6 分页查询入库单

**GET** `/api/purchase/receipts`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认10 |
| receiptCode | string | 否 | 入库单编号 |
| orderId | long | 否 | 订单ID |
| supplierId | long | 否 | 供应商ID |
| receiptStatus | string | 否 | 入库状态 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

---

## 三、退货管理接口

### 3.1 创建退货单

**POST** `/api/purchase/returns`

**请求体：**
```json
{
  "orderId": 10001,
  "receiptId": 10001,
  "returnDate": "2026-02-20",
  "reason": "质量问题",
  "warehouseId": 2001,
  "warehouseName": "主仓库",
  "remark": "",
  "details": [
    {
      "materialId": 3001,
      "materialCode": "MAT001",
      "materialName": "原材料A",
      "specification": "规格A",
      "unit": "件",
      "quantity": 10,
      "unitPrice": 50.00,
      "batchNo": "BT20260218",
      "reason": "外观损坏",
      "remark": ""
    }
  ]
}
```

### 3.2 删除退货单

**DELETE** `/api/purchase/returns/{id}`

### 3.3 审核退货单

**POST** `/api/purchase/returns/{id}/approve`

### 3.4 完成退货

**POST** `/api/purchase/returns/{id}/complete`

### 3.5 取消退货单

**POST** `/api/purchase/returns/{id}/cancel`

### 3.6 获取退货单详情

**GET** `/api/purchase/returns/{id}`

**响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10001,
    "returnCode": "PRT202602200001",
    "orderId": 10001,
    "orderCode": "PO202602170001",
    "receiptId": 10001,
    "supplierId": 1001,
    "supplierName": "供应商A",
    "returnDate": "2026-02-20",
    "returnAmount": 500.00,
    "reason": "质量问题",
    "returnStatus": "COMPLETED",
    "returnStatusName": "已完成",
    "warehouseId": 2001,
    "warehouseName": "主仓库",
    "operatorId": 5001,
    "operatorName": "张三",
    "remark": "",
    "createTime": "2026-02-20T10:00:00",
    "details": [
      {
        "id": 20001,
        "materialId": 3001,
        "materialCode": "MAT001",
        "materialName": "原材料A",
        "specification": "规格A",
        "unit": "件",
        "quantity": 10,
        "unitPrice": 50.00,
        "amount": 500.00,
        "batchNo": "BT20260218",
        "reason": "外观损坏",
        "remark": ""
      }
    ]
  }
}
```

### 3.7 分页查询退货单

**GET** `/api/purchase/returns`

**查询参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页条数，默认10 |
| returnCode | string | 否 | 退货单编号 |
| orderId | long | 否 | 订单ID |
| supplierId | long | 否 | 供应商ID |
| returnStatus | string | 否 | 退货状态 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

---

## 四、错误码

| 错误码 | 说明 |
|--------|------|
| 60001 | 订单已存在 |
| 60002 | 供应商不存在 |
| 60003 | 订单已收货 |
| 60004 | 订单状态不正确 |
| 60005 | 订单不存在 |
| 60006 | 入库单不存在 |
| 60007 | 入库单已审核 |
| 60008 | 退货单不存在 |
| 60009 | 退货单已审核 |
| 60010 | 库存不足 |
| 60011 | 订单明细不存在 |
| 60012 | 入库数量超过订单数量 |
| 60013 | 退货数量超过入库数量 |
| 60014 | 物料不存在 |
| 60015 | 仓库不存在 |

---

## 五、状态流转

### 5.1 采购订单状态流转

```
DRAFT(草稿) → PENDING(待审核) → APPROVED(已审核) → RECEIVING(收货中) → COMPLETED(已完成)
                    ↓                    ↓                  ↓
               CANCELLED(已取消)    CANCELLED(已取消)   CANCELLED(已取消)
```

### 5.2 入库单状态流转

```
PENDING(待审核) → COMPLETED(已完成)
      ↓
CANCELLED(已取消)
```

### 5.3 退货单状态流转

```
PENDING(待审核) → APPROVED(已审核) → COMPLETED(已完成)
      ↓                ↓
CANCELLED(已取消)  CANCELLED(已取消)
```

---

*文档维护：请保持此文档的及时更新*
