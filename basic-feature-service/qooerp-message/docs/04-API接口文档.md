openapi: 3.0.3
info:
  title: QooERP 消息服务模块 API
  version: 1.0.0

servers:
  - url: http://localhost:8098/api/message

paths:
  /send:
    post:
      summary: 发送消息
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                title:
                  type: string
                content:
                  type: string
                receivers:
                  type: array
                  items:
                    type: integer
      responses:
        '200':
          description: 成功

  /list:
    get:
      summary: 消息列表
      responses:
        '200':
          description: 成功
