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

paths:
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
