openapi: 3.0.3
info:
  title: QooERP 通知服务模块 API
  version: 1.0.0

servers:
  - url: http://localhost:8099/api/notify

paths:
  /email:
    post:
      summary: 发送邮件
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                to:
                  type: string
                subject:
                  type: string
                content:
                  type: string
      responses:
        '200':
          description: 成功

  /sms:
    post:
      summary: 发送短信
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                phone:
                  type: string
                content:
                  type: string
      responses:
        '200':
          description: 成功
