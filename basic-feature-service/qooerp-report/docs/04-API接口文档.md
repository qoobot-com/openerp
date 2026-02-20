openapi: 3.0.3
info:
  title: QooERP 报表与分析模块 API
  version: 1.0.0
  description: QooERP 报表与分析模块RESTful API接口文档

servers:
  - url: http://localhost:8096/api/report
    description: 本地开发环境

tags:
  - name: 报表管理
    description: 报表管理接口
  - name: 数据源管理
    description: 报表数据源管理接口
  - name: 报表配置
    description: 报表配置管理接口
  - name: 仪表盘管理
    description: 仪表盘管理接口
  - name: 仪表盘项管理
    description: 仪表盘项管理接口
  - name: 报表订阅
    description: 报表订阅管理接口
  - name: 报表历史
    description: 报表历史管理接口

paths:
  # 报表管理接口
  /reports:
    get:
      tags:
        - 报表管理
      summary: 分页查询报表
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: reportName
          in: query
          schema:
            type: string
        - name: reportType
          in: query
          schema:
            type: integer
        - name: category
          in: query
          schema:
            type: string
        - name: status
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 报表管理
      summary: 创建报表
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ReportDTO'
      responses:
        '200':
          description: 成功

  /reports/{id}:
    get:
      tags:
        - 报表管理
      summary: 获取报表详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    put:
      tags:
        - 报表管理
      summary: 更新报表
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
              $ref: '#/components/schemas/ReportDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 报表管理
      summary: 删除报表
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /reports/{id}/publish:
    post:
      tags:
        - 报表管理
      summary: 发布报表
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /reports/{id}/archive:
    post:
      tags:
        - 报表管理
      summary: 归档报表
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /reports/{id}/data:
    post:
      tags:
        - 报表管理
      summary: 获取报表数据
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
              $ref: '#/components/schemas/ReportQueryDTO'
      responses:
        '200':
          description: 成功

  /reports/{id}/export:
    get:
      tags:
        - 报表管理
      summary: 导出报表
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
        - name: format
          in: query
          required: true
          schema:
            type: string
            enum: [excel, pdf, csv]
      responses:
        '200':
          description: 成功

  # 数据源管理接口
  /datasources:
    get:
      tags:
        - 数据源管理
      summary: 分页查询数据源
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: reportId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 数据源管理
      summary: 创建数据源
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ReportDatasourceDTO'
      responses:
        '200':
          description: 成功

  /datasources/{id}:
    get:
      tags:
        - 数据源管理
      summary: 获取数据源详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    put:
      tags:
        - 数据源管理
      summary: 更新数据源
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
              $ref: '#/components/schemas/ReportDatasourceDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 数据源管理
      summary: 删除数据源
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /datasources/{id}/test:
    post:
      tags:
        - 数据源管理
      summary: 测试数据源连接
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 报表配置接口
  /configs:
    get:
      tags:
        - 报表配置
      summary: 分页查询报表配置
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: reportId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 报表配置
      summary: 创建报表配置
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ReportConfigDTO'
      responses:
        '200':
          description: 成功

  /configs/{id}:
    get:
      tags:
        - 报表配置
      summary: 获取报表配置详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    put:
      tags:
        - 报表配置
      summary: 更新报表配置
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
              $ref: '#/components/schemas/ReportConfigDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 报表配置
      summary: 删除报表配置
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 仪表盘管理接口
  /dashboards:
    get:
      tags:
        - 仪表盘管理
      summary: 分页查询仪表盘
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: dashboardName
          in: query
          schema:
            type: string
        - name: dashboardType
          in: query
          schema:
            type: integer
        - name: status
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 仪表盘管理
      summary: 创建仪表盘
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/DashboardDTO'
      responses:
        '200':
          description: 成功

  /dashboards/{id}:
    get:
      tags:
        - 仪表盘管理
      summary: 获取仪表盘详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    put:
      tags:
        - 仪表盘管理
      summary: 更新仪表盘
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
              $ref: '#/components/schemas/DashboardDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 仪表盘管理
      summary: 删除仪表盘
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /dashboards/{id}/publish:
    post:
      tags:
        - 仪表盘管理
      summary: 发布仪表盘
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 仪表盘项管理接口
  /dashboard-items:
    get:
      tags:
        - 仪表盘项管理
      summary: 分页查询仪表盘项
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: dashboardId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 仪表盘项管理
      summary: 创建仪表盘项
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/DashboardItemDTO'
      responses:
        '200':
          description: 成功

  /dashboard-items/{id}:
    get:
      tags:
        - 仪表盘项管理
      summary: 获取仪表盘项详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    put:
      tags:
        - 仪表盘项管理
      summary: 更新仪表盘项
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
              $ref: '#/components/schemas/DashboardItemDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 仪表盘项管理
      summary: 删除仪表盘项
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /dashboards/{dashboardId}/items:
    get:
      tags:
        - 仪表盘项管理
      summary: 获取仪表盘的所有项
      parameters:
        - name: dashboardId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 报表订阅接口
  /subscriptions:
    get:
      tags:
        - 报表订阅
      summary: 分页查询订阅
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: reportId
          in: query
          schema:
            type: integer
        - name: userId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 报表订阅
      summary: 创建订阅
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ReportSubscriptionDTO'
      responses:
        '200':
          description: 成功

  /subscriptions/{id}:
    get:
      tags:
        - 报表订阅
      summary: 获取订阅详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    put:
      tags:
        - 报表订阅
      summary: 更新订阅
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
              $ref: '#/components/schemas/ReportSubscriptionDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 报表订阅
      summary: 删除订阅
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /subscriptions/{id}/enable:
    post:
      tags:
        - 报表订阅
      summary: 启用订阅
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /subscriptions/{id}/disable:
    post:
      tags:
        - 报表订阅
      summary: 暂停订阅
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 报表历史接口
  /histories:
    get:
      tags:
        - 报表历史
      summary: 分页查询报表历史
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          schema:
            type: integer
            default: 10
        - name: reportId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /histories/{id}:
    get:
      tags:
        - 报表历史
      summary: 获取报表历史详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /reports/{reportId}/snapshots:
    post:
      tags:
        - 报表历史
      summary: 创建报表快照
      parameters:
        - name: reportId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /histories/{id}/restore:
    post:
      tags:
        - 报表历史
      summary: 恢复报表快照
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

components:
  schemas:
    ReportDTO:
      type: object
      properties:
        id:
          type: integer
        reportNo:
          type: string
        reportName:
          type: string
        reportType:
          type: integer
        category:
          type: string
        description:
          type: string
        status:
          type: integer

    ReportQueryDTO:
      type: object
      properties:
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
        filters:
          type: object
          additionalProperties: true

    ReportDatasourceDTO:
      type: object
      properties:
        id:
          type: integer
        reportId:
          type: integer
        datasourceName:
          type: string
        datasourceType:
          type: string
        connectionConfig:
          type: object
        sqlQuery:
          type: string
        cacheConfig:
          type: object

    ReportConfigDTO:
      type: object
      properties:
        id:
          type: integer
        reportId:
          type: integer
        configName:
          type: string
        chartType:
          type: string
        chartConfig:
          type: object
        columnConfig:
          type: object
        filterConfig:
          type: object
        sortConfig:
          type: object
        drillConfig:
          type: object

    DashboardDTO:
      type: object
      properties:
        id:
          type: integer
        dashboardNo:
          type: string
        dashboardName:
          type: string
        dashboardType:
          type: integer
        layoutConfig:
          type: object
        description:
          type: string
        status:
          type: integer

    DashboardItemDTO:
      type: object
      properties:
        id:
          type: integer
        dashboardId:
          type: integer
        reportId:
          type: integer
        itemName:
          type: string
        positionConfig:
          type: object
        sizeConfig:
          type: object
        displayConfig:
          type: object
        refreshInterval:
          type: integer
        sortOrder:
          type: integer

    ReportSubscriptionDTO:
      type: object
      properties:
        id:
          type: integer
        reportId:
          type: integer
        userId:
          type: integer
        subscriptionName:
          type: string
        scheduleConfig:
          type: object
        deliveryConfig:
          type: object
        filterConfig:
          type: object
        status:
          type: integer
