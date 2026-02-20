# QooERP 库存管理 - API接口文档

> 版本：v1.1
> 创建日期：2026-02-17
> 基础路径：/api/inventory

---

## 一、物料管理接口

### 1.1 创建物料

POST /api/inventory/materials

```json
{
  "materialCode": "MAT001",
  "materialName": "商品A",
  "categoryId": 1,
  "specification": "规格A",
  "unit": "件",
  "materialType": "FINISHED",
  "costAttribute": "STANDARD",
  "standardCost": 100.00,
  "safetyStock": 10.00,
  "minStock": 5.00,
  "maxStock": 100.00,
  "leadTime": 7
}
```

### 1.2 查询物料列表

GET /api/inventory/materials/list

参数：
- pageNum: 页码
- pageSize: 每页数量
- materialCode: 物料编码（模糊查询）
- materialName: 物料名称（模糊查询）
- categoryId: 分类ID
- status: 状态

### 1.3 获取物料详情

GET /api/inventory/materials/{id}

### 1.4 更新物料

PUT /api/inventory/materials/{id}

### 1.5 删除物料

DELETE /api/inventory/materials/{id}

### 1.6 获取物料分类树

GET /api/inventory/materials/categories

---

## 二、仓库管理接口

### 2.1 创建仓库

POST /api/inventory/warehouses

```json
{
  "warehouseCode": "WH001",
  "warehouseName": "主仓库",
  "warehouseType": "MAIN",
  "warehouseAddress": "地址",
  "managerId": 1,
  "managerName": "张三",
  "contactPhone": "13800138000"
}
```

### 2.2 查询仓库列表

GET /api/inventory/warehouses/list

### 2.3 获取仓库详情

GET /api/inventory/warehouses/{id}

### 2.4 更新仓库

PUT /api/inventory/warehouses/{id}

### 2.5 删除仓库

DELETE /api/inventory/warehouses/{id}

---

## 三、库存管理接口

### 3.1 查询库存列表

GET /api/inventory/stocks

参数：
- pageNum: 页码
- pageSize: 每页数量
- warehouseId: 仓库ID
- materialCode: 物料编码
- materialName: 物料名称

### 3.2 获取库存详情

GET /api/inventory/stocks/{warehouseId}/materials/{materialId}

### 3.3 库存调整

POST /api/inventory/stocks/{id}/adjust

```json
{
  "adjustQuantity": 10,
  "adjustType": "INCREASE",
  "reason": "盘点调整"
}
```

### 3.4 库存预警

GET /api/inventory/stocks/alerts

返回库存不足、库存积压等预警信息

---

## 四、入库管理接口

### 4.1 创建入库单

POST /api/inventory/inbounds

```json
{
  "inboundType": "PURCHASE",
  "inboundDate": "2026-02-17",
  "warehouseId": 1,
  "supplierId": 1,
  "details": [
    {
      "materialId": 1,
      "quantity": 100,
      "unitPrice": 50.00
    }
  ]
}
```

### 4.2 审核入库单

PUT /api/inventory/inbounds/{id}/approve

```json
{
  "approverId": 1,
  "approveResult": "APPROVED",
  "approveRemark": "审核通过"
}
```

### 4.3 执行入库

PUT /api/inventory/inbounds/{id}/execute

### 4.4 查询入库单列表

GET /api/inventory/inbounds/list

### 4.5 获取入库单详情

GET /api/inventory/inbounds/{id}

### 4.6 删除入库单

DELETE /api/inventory/inbounds/{id}

---

## 五、出库管理接口

### 5.1 创建出库单

POST /api/inventory/outbounds

```json
{
  "outboundType": "SALES",
  "outboundDate": "2026-02-17",
  "warehouseId": 1,
  "customerId": 1,
  "details": [
    {
      "materialId": 1,
      "quantity": 50,
      "unitPrice": 60.00
    }
  ]
}
```

### 5.2 审核出库单

PUT /api/inventory/outbounds/{id}/approve

```json
{
  "approverId": 1,
  "approveResult": "APPROVED",
  "approveRemark": "审核通过"
}
```

### 5.3 执行出库

PUT /api/inventory/outbounds/{id}/execute

### 5.4 查询出库单列表

GET /api/inventory/outbounds/list

### 5.5 获取出库单详情

GET /api/inventory/outbounds/{id}

### 5.6 删除出库单

DELETE /api/inventory/outbounds/{id}

---

## 六、调拨管理接口

### 6.1 创建调拨单

POST /api/inventory/transfers

```json
{
  "transferDate": "2026-02-17",
  "fromWarehouseId": 1,
  "toWarehouseId": 2,
  "details": [
    {
      "materialId": 1,
      "quantity": 20
    }
  ]
}
```

### 6.2 审核调拨单

PUT /api/inventory/transfers/{id}/approve

```json
{
  "approverId": 1,
  "approveResult": "APPROVED"
}
```

### 6.3 执行调拨

PUT /api/inventory/transfers/{id}/execute

### 6.4 查询调拨单列表

GET /api/inventory/transfers/list

### 6.5 获取调拨单详情

GET /api/inventory/transfers/{id}

### 6.6 删除调拨单

DELETE /api/inventory/transfers/{id}

---

## 七、盘点管理接口

### 7.1 创建盘点单

POST /api/inventory/checks

```json
{
  "warehouseId": 1,
  "checkDate": "2026-02-17"
}
```

### 7.2 执行盘点

PUT /api/inventory/checks/{id}/execute

```json
{
  "checkerId": 1,
  "details": [
    {
      "materialId": 1,
      "actualStock": 95.00
    }
  ]
}
```

### 7.3 查询盘点单列表

GET /api/inventory/checks/list

### 7.4 获取盘点单详情

GET /api/inventory/checks/{id}

### 7.5 删除盘点单

DELETE /api/inventory/checks/{id}

---

## 八、库存记录接口

### 8.1 查询库存变动记录

GET /api/inventory/records

参数：
- pageNum: 页码
- pageSize: 每页数量
- warehouseId: 仓库ID
- materialId: 物料ID
- recordType: 记录类型（IN/OUT）
- startTime: 开始时间
- endTime: 结束时间

---

## 九、错误码

| 错误码 | 说明 |
|--------|------|
| 50001 | 物料编码已存在 |
| 50002 | 库存不足 |
| 50003 | 调拨数量不足 |
| 50004 | 仓库编码已存在 |
| 50005 | 物料分类不存在 |
| 50006 | 仓库不存在 |
| 50007 | 入库单不存在 |
| 50008 | 出库单不存在 |
| 50009 | 调拨单不存在 |
| 50010 | 盘点单不存在 |
| 50011 | 物料不存在 |
| 50012 | 已有未完成入库单 |
| 50013 | 已有未完成出库单 |

---

**文档版本**: v1.1
