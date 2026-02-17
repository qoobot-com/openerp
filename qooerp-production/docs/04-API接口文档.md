openapi: 3.0.3
info:
  title: QooERP 生产管理模块 API
  version: 1.0.0
  description: QooERP 生产管理模块RESTful API接口文档

servers:
  - url: http://localhost:8092/api/production
    description: 本地开发环境

tags:
  - name: 生产计划
    description: 生产计划管理接口
  - name: 生产订单
    description: 生产订单管理接口
  - name: 生产领料
    description: 生产领料管理接口
  - name: 生产入库
    description: 生产入库管理接口
  - name: 生产报工
    description: 生产报工管理接口
  - name: 生产质检
    description: 生产质检管理接口

paths:
  /plans:
    get:
      tags:
        - 生产计划
      summary: 分页查询生产计划
      operationId: listProductionPlans
      parameters:
        - name: page
          in: query
          required: false
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          required: false
          schema:
            type: integer
            default: 10
        - name: planNo
          in: query
          required: false
          schema:
            type: string
        - name: status
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageProductionPlanDTO'
    post:
      tags:
        - 生产计划
      summary: 创建生产计划
      operationId: createProductionPlan
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductionPlanDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionPlanDTO'

  /plans/{id}:
    get:
      tags:
        - 生产计划
      summary: 获取生产计划详情
      operationId: getProductionPlan
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionPlanDTO'
    put:
      tags:
        - 生产计划
      summary: 更新生产计划
      operationId: updateProductionPlan
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
              $ref: '#/components/schemas/ProductionPlanDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionPlanDTO'
    delete:
      tags:
        - 生产计划
      summary: 删除生产计划
      operationId: deleteProductionPlan
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /plans/{id}/approve:
    post:
      tags:
        - 生产计划
      summary: 审核生产计划
      operationId: approveProductionPlan
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /plans/{id}/issue:
    post:
      tags:
        - 生产计划
      summary: 下达生产计划
      operationId: issueProductionPlan
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /plans/{id}/cancel:
    post:
      tags:
        - 生产计划
      summary: 取消生产计划
      operationId: cancelProductionPlan
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /plans/{id}/materials:
    get:
      tags:
        - 生产计划
      summary: 获取计划物料清单
      operationId: getPlanMaterials
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultListMaterialRequirementDTO'

  /orders:
    get:
      tags:
        - 生产订单
      summary: 分页查询生产订单
      operationId: listProductionOrders
      parameters:
        - name: page
          in: query
          required: false
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          required: false
          schema:
            type: integer
            default: 10
        - name: orderNo
          in: query
          required: false
          schema:
            type: string
        - name: status
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageProductionOrderDTO'
    post:
      tags:
        - 生产订单
      summary: 创建生产订单
      operationId: createProductionOrder
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductionOrderDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionOrderDTO'

  /orders/{id}:
    get:
      tags:
        - 生产订单
      summary: 获取生产订单详情
      operationId: getProductionOrder
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionOrderDTO'
    put:
      tags:
        - 生产订单
      summary: 更新生产订单
      operationId: updateProductionOrder
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
              $ref: '#/components/schemas/ProductionOrderDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionOrderDTO'
    delete:
      tags:
        - 生产订单
      summary: 删除生产订单
      operationId: deleteProductionOrder
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /orders/{id}/approve:
    post:
      tags:
        - 生产订单
      summary: 审核生产订单
      operationId: approveProductionOrder
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /orders/{id}/issue:
    post:
      tags:
        - 生产订单
      summary: 下达生产订单
      operationId: issueProductionOrder
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /orders/{id}/complete:
    post:
      tags:
        - 生产订单
      summary: 完成生产订单
      operationId: completeProductionOrder
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /orders/{id}/close:
    post:
      tags:
        - 生产订单
      summary: 关闭生产订单
      operationId: closeProductionOrder
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /orders/{id}/progress:
    get:
      tags:
        - 生产订单
      summary: 获取订单进度
      operationId: getOrderProgress
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultOrderProgressDTO'

  /materials:
    get:
      tags:
        - 生产领料
      summary: 分页查询生产领料
      operationId: listProductionMaterials
      parameters:
        - name: page
          in: query
          required: false
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          required: false
          schema:
            type: integer
            default: 10
        - name: orderId
          in: query
          required: false
          schema:
            type: integer
        - name: status
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageProductionMaterialDTO'
    post:
      tags:
        - 生产领料
      summary: 创建生产领料
      operationId: createProductionMaterial
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductionMaterialDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionMaterialDTO'

  /materials/{id}/approve:
    post:
      tags:
        - 生产领料
      summary: 审核领料单
      operationId: approveProductionMaterial
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /materials/{id}/issue:
    post:
      tags:
        - 生产领料
      summary: 领料出库
      operationId: issueProductionMaterial
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /receipts:
    get:
      tags:
        - 生产入库
      summary: 分页查询生产入库
      operationId: listProductionReceipts
      parameters:
        - name: page
          in: query
          required: false
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          required: false
          schema:
            type: integer
            default: 10
        - name: orderId
          in: query
          required: false
          schema:
            type: integer
        - name: status
          in: query
          required: false
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageProductionReceiptDTO'
    post:
      tags:
        - 生产入库
      summary: 创建生产入库
      operationId: createProductionReceipt
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductionReceiptDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionReceiptDTO'

  /receipts/{id}/quality:
    post:
      tags:
        - 生产入库
      summary: 质检
      operationId: qualityCheckReceipt
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
              $ref: '#/components/schemas/QualityCheckResultDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /receipts/{id}/approve:
    post:
      tags:
        - 生产入库
      summary: 入库审核
      operationId: approveProductionReceipt
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /receipts/{id}/do:
    post:
      tags:
        - 生产入库
      summary: 执行入库
      operationId: doProductionReceipt
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultVoid'

  /reports:
    get:
      tags:
        - 生产报工
      summary: 分页查询生产报工
      operationId: listProductionReports
      parameters:
        - name: page
          in: query
          required: false
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          required: false
          schema:
            type: integer
            default: 10
        - name: orderId
          in: query
          required: false
          schema:
            type: integer
        - name: workDate
          in: query
          required: false
          schema:
            type: string
            format: date
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageProductionReportDTO'
    post:
      tags:
        - 生产报工
      summary: 创建生产报工
      operationId: createProductionReport
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductionReportDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultProductionReportDTO'

components:
  schemas:
    ResultVoid:
      type: object
      properties:
        code:
          type: integer
        message:
          type: string
        data:
          type: object
          nullable: true

    ResultProductionPlanDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/ProductionPlanDTO'

    ProductionPlanDTO:
      type: object
      properties:
        id:
          type: integer
        planNo:
          type: string
        planName:
          type: string
        planDate:
          type: string
          format: date
        planQuantity:
          type: number
        status:
          type: integer
        productId:
          type: integer
        productCode:
          type: string
        productName:
          type: string
        deliveryDate:
          type: string
          format: date
        remark:
          type: string
        createTime:
          type: string
          format: date-time

    PageProductionPlanDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: object
              properties:
                records:
                  type: array
                  items:
                    $ref: '#/components/schemas/ProductionPlanDTO'
                total:
                  type: integer
                page:
                  type: integer
                size:
                  type: integer

    ProductionOrderDTO:
      type: object
      properties:
        id:
          type: integer
        orderNo:
          type: string
        planId:
          type: integer
        planNo:
          type: string
        orderDate:
          type: string
          format: date
        orderQuantity:
          type: number
        completedQuantity:
          type: number
        status:
          type: integer
        productId:
          type: integer
        productCode:
          type: string
        productName:
          type: string
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
        deliveryDate:
          type: string
          format: date
        remark:
          type: string
        createTime:
          type: string
          format: date-time

    PageProductionOrderDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: object
              properties:
                records:
                  type: array
                  items:
                    $ref: '#/components/schemas/ProductionOrderDTO'
                total:
                  type: integer
                page:
                  type: integer
                size:
                  type: integer

    ProductionMaterialDTO:
      type: object
      properties:
        id:
          type: integer
        materialNo:
          type: string
        orderId:
          type: integer
        orderNo:
          type: string
        materialId:
          type: integer
        materialCode:
          type: string
        materialName:
          type: string
        specification:
          type: string
        unit:
          type: string
        requestQuantity:
          type: number
        issuedQuantity:
          type: number
        returnedQuantity:
          type: number
        status:
          type: integer
        issueDate:
          type: string
          format: date

    PageProductionMaterialDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: object
              properties:
                records:
                  type: array
                  items:
                    $ref: '#/components/schemas/ProductionMaterialDTO'
                total:
                  type: integer
                page:
                  type: integer
                size:
                  type: integer

    ProductionReceiptDTO:
      type: object
      properties:
        id:
          type: integer
        receiptNo:
          type: string
        orderId:
          type: integer
        orderNo:
          type: string
        productId:
          type: integer
        productCode:
          type: string
        productName:
          type: string
        receiptQuantity:
          type: number
        qualifiedQuantity:
          type: number
        unqualifiedQuantity:
          type: number
        qualityStatus:
          type: integer
        status:
          type: integer
        receiptDate:
          type: string
          format: date

    QualityCheckResultDTO:
      type: object
      properties:
        qualifiedQuantity:
          type: number
        unqualifiedQuantity:
          type: number
        checkResult:
          type: integer
        defectDescription:
          type: string

    PageProductionReceiptDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: object
              properties:
                records:
                  type: array
                  items:
                    $ref: '#/components/schemas/ProductionReceiptDTO'
                total:
                  type: integer
                page:
                  type: integer
                size:
                  type: integer

    ProductionReportDTO:
      type: object
      properties:
        id:
          type: integer
        reportNo:
          type: string
        orderId:
          type: integer
        orderNo:
          type: string
        processId:
          type: integer
        processName:
          type: string
        workDate:
          type: string
          format: date
        workHours:
          type: number
        outputQuantity:
          type: number
        qualifiedQuantity:
          type: number
        unqualifiedQuantity:
          type: number
        workerId:
          type: integer
        workerName:
          type: string

    PageProductionReportDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: object
              properties:
                records:
                  type: array
                  items:
                    $ref: '#/components/schemas/ProductionReportDTO'
                total:
                  type: integer
                page:
                  type: integer
                size:
                  type: integer

    MaterialRequirementDTO:
      type: object
      properties:
        materialId:
          type: integer
        materialCode:
          type: string
        materialName:
          type: string
        requiredQuantity:
          type: number
        currentStock:
          type: number
        shortageQuantity:
          type: number

    OrderProgressDTO:
      type: object
      properties:
        orderId:
          type: integer
        orderNo:
          type: string
        status:
          type: integer
        completedQuantity:
          type: number
        progress:
          type: number
        totalWorkHours:
          type: number
        currentWorkHours:
          type: number

    ResultListMaterialRequirementDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: array
              items:
                $ref: '#/components/schemas/MaterialRequirementDTO'

    ResultOrderProgressDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              $ref: '#/components/schemas/OrderProgressDTO'
