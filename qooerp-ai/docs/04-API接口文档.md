openapi: 3.0.3
info:
  title: QooERP AI智能服务模块 API
  version: 1.0.0

servers:
  - url: http://localhost:8102/api/ai

paths:
  /recommend:
    post:
      summary: 智能推荐
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                userId:
                  type: integer
                type:
                  type: string
      responses:
        '200':
          description: 成功

  /predict:
    post:
      summary: 智能预测
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                modelId:
                  type: integer
                inputData:
                  type: object
      responses:
        '200':
          description: 成功

  /chat:
    post:
      summary: 智能问答
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                question:
                  type: string
      responses:
        '200':
          description: 成功
