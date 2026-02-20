openapi: 3.0.3
info:
  title: QooERP 客户关系管理模块 API
  version: 1.0.0
  description: QooERP 客户关系管理模块RESTful API接口文档

servers:
  - url: http://localhost:8094/api/crm
    description: 本地开发环境

tags:
  - name: 客户管理
    description: 客户管理接口
  - name: 商机管理
    description: 商机管理接口
  - name: 合同管理
    description: 合同管理接口
  - name: 服务管理
    description: 服务管理接口

paths:
  /customers:
    get:
      tags:
        - 客户管理
      summary: 分页查询客户
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
        - name: customerName
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
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageCrmCustomerDTO'
    post:
      tags:
        - 客户管理
      summary: 创建客户
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CrmCustomerDTO'
      responses:
        '200':
          description: 成功

  /customers/{id}:
    get:
      tags:
        - 客户管理
      summary: 获取客户详情
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
        - 客户管理
      summary: 更新客户
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
              $ref: '#/components/schemas/CrmCustomerDTO'
      responses:
        '200':
          description: 成功

  /customers/{id}/followups:
    post:
      tags:
        - 客户管理
      summary: 添加跟进记录
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
              $ref: '#/components/schemas/CrmFollowupDTO'
      responses:
        '200':
          description: 成功

  /opportunities:
    get:
      tags:
        - 商机管理
      summary: 分页查询商机
      parameters:
        - name: customerId
          in: query
          schema:
            type: integer
        - name: stage
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 商机管理
      summary: 创建商机
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CrmOpportunityDTO'
      responses:
        '200':
          description: 成功

  /opportunities/{id}/convert:
    post:
      tags:
        - 商机管理
      summary: 商机转合同
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /opportunities/{id}/stage:
    put:
      tags:
        - 商机管理
      summary: 更新商机阶段
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
              type: object
              properties:
                stage:
                  type: integer
      responses:
        '200':
          description: 成功

  /contracts:
    get:
      tags:
        - 合同管理
      summary: 分页查询合同
      parameters:
        - name: customerId
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
        - 合同管理
      summary: 创建合同
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CrmContractDTO'
      responses:
        '200':
          description: 成功

  /services:
    get:
      tags:
        - 服务管理
      summary: 分页查询服务
      parameters:
        - name: customerId
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
        - 服务管理
      summary: 创建服务请求
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CrmServiceDTO'
      responses:
        '200':
          description: 成功

components:
  schemas:
    CrmCustomerDTO:
      type: object
      properties:
        id:
          type: integer
        customerNo:
          type: string
        customerName:
          type: string
        customerType:
          type: integer
        level:
          type: integer
        industry:
          type: string
        region:
          type: string
        contactPerson:
          type: string
        contactPhone:
          type: string
        contactEmail:
          type: string
        address:
          type: string
        status:
          type: integer

    PageCrmCustomerDTO:
      type: object
      properties:
        records:
          type: array
          items:
            $ref: '#/components/schemas/CrmCustomerDTO'
        total:
          type: integer
        page:
          type: integer
        size:
          type: integer

    CrmOpportunityDTO:
      type: object
      properties:
        id:
          type: integer
        opportunityNo:
          type: string
        customerId:
          type: integer
        customerName:
          type: string
        opportunityName:
          type: string
        stage:
          type: integer
        amount:
          type: number
        probability:
          type: integer
        expectedCloseDate:
          type: string
          format: date
        ownerId:
          type: integer
        ownerName:
          type: string
        status:
          type: integer

    CrmContractDTO:
      type: object
      properties:
        id:
          type: integer
        contractNo:
          type: string
        opportunityId:
          type: integer
        customerId:
          type: integer
        customerName:
          type: string
        contractName:
          type: string
        contractAmount:
          type: number
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
        status:
          type: integer

    CrmServiceDTO:
      type: object
      properties:
        id:
          type: integer
        serviceNo:
          type: string
        customerId:
          type: integer
        customerName:
          type: string
        serviceType:
          type: string
        serviceTitle:
          type: string
        description:
          type: string
        priority:
          type: integer
        status:
          type: integer
        assigneeId:
          type: integer
        assigneeName:
          type: string

    CrmFollowupDTO:
      type: object
      properties:
        followupNo:
          type: string
        customerId:
          type: integer
        opportunityId:
          type: integer
        followupType:
          type: string
        followupContent:
          type: string
        followupDate:
          type: string
          format: date
        followupPersonId:
          type: integer
        followupPersonName:
          type: string
