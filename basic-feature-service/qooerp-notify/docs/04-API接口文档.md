openapi: 3.0.3
info:
  title: QooERP 通知服务模块 API
  version: 1.0.0
  description: 统一通知服务，支持邮件、短信、站内、推送等多种通知方式

servers:
  - url: http://localhost:8099/api/notify

paths:
  # ========== 通知发送 ==========
  /email:
    post:
      summary: 发送邮件
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - to
                - subject
                - content
              properties:
                to:
                  type: string
                  description: 收件人(多个用逗号分隔)
                cc:
                  type: string
                  description: 抄送(多个用逗号分隔)
                bcc:
                  type: string
                  description: 密送(多个用逗号分隔)
                subject:
                  type: string
                  description: 邮件主题
                content:
                  type: string
                  description: 邮件内容
                isHtml:
                  type: boolean
                  default: false
                  description: 是否HTML格式
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                  message:
                    type: string
                  data:
                    type: string
                    description: 通知编号

  /sms:
    post:
      summary: 发送短信
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - phone
                - content
              properties:
                phone:
                  type: string
                  description: 手机号(多个用逗号分隔)
                content:
                  type: string
                  description: 短信内容
                sign:
                  type: string
                  description: 短信签名
      responses:
        '200':
          description: 成功

  /internal:
    post:
      summary: 发送站内通知
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - userIds
                - content
              properties:
                userIds:
                  type: string
                  description: 用户ID列表(逗号分隔)
                title:
                  type: string
                  description: 标题
                content:
                  type: string
                  description: 内容
                type:
                  type: string
                  description: 类型(notice/announcement/system)
                link:
                  type: string
                  description: 跳转链接
      responses:
        '200':
          description: 成功

  /push:
    post:
      summary: 发送推送通知
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - deviceIds
                - title
                - content
              properties:
                deviceIds:
                  type: string
                  description: 设备ID列表(逗号分隔)
                title:
                  type: string
                  description: 标题
                content:
                  type: string
                  description: 内容
                badge:
                  type: integer
                  description: 角标数
                sound:
                  type: string
                  description: 提示音
                extras:
                  type: object
                  description: 扩展参数
      responses:
        '200':
          description: 成功

  /webhook:
    post:
      summary: 发送Webhook
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - url
                - data
              properties:
                url:
                  type: string
                  description: Webhook URL
                method:
                  type: string
                  default: POST
                  description: HTTP方法
                headers:
                  type: object
                  description: 请求头
                data:
                  type: object
                  description: 请求数据
      responses:
        '200':
          description: 成功

  /batch:
    post:
      summary: 批量发送
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - type
                - receivers
                - content
              properties:
                type:
                  type: string
                  enum: [email, sms, internal, push, webhook]
                  description: 通知类型
                receivers:
                  type: string
                  description: 接收者列表(逗号分隔)
                title:
                  type: string
                  description: 标题
                content:
                  type: string
                  description: 内容
                extras:
                  type: object
                  description: 扩展参数
      responses:
        '200':
          description: 成功

  /template:
    post:
      summary: 使用模板发送
      tags:
        - 通知发送
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - templateCode
                - receiver
              properties:
                templateCode:
                  type: string
                  description: 模板编码
                receiver:
                  type: string
                  description: 接收者
                variables:
                  type: object
                  description: 模板变量
      responses:
        '200':
          description: 成功

  # ========== 模板管理 ==========
  /template:
    get:
      summary: 分页查询模板
      tags:
        - 模板管理
      parameters:
        - name: current
          in: query
          schema:
            type: integer
        - name: size
          in: query
          schema:
            type: integer
        - name: type
          in: query
          schema:
            type: integer
        - name: category
          in: query
          schema:
            type: string
        - name: keyword
          in: query
          schema:
            type: string
      responses:
        '200':
          description: 成功

    post:
      summary: 创建模板
      tags:
        - 模板管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - templateCode
                - templateName
                - type
                - content
              properties:
                templateCode:
                  type: string
                templateName:
                  type: string
                category:
                  type: string
                type:
                  type: integer
                subject:
                  type: string
                content:
                  type: string
                variables:
                  type: object
      responses:
        '200':
          description: 成功

    put:
      summary: 更新模板
      tags:
        - 模板管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TemplateDTO'
      responses:
        '200':
          description: 成功

  /template/{id}:
    delete:
      summary: 删除模板
      tags:
        - 模板管理
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

    get:
      summary: 获取模板详情
      tags:
        - 模板管理
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /template/preview:
    post:
      summary: 模板预览
      tags:
        - 模板管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - templateId
              properties:
                templateId:
                  type: integer
                variables:
                  type: object
      responses:
        '200':
          description: 成功

  # ========== 记录查询 ==========
  /record/page:
    get:
      summary: 分页查询通知记录
      tags:
        - 记录查询
      parameters:
        - name: current
          in: query
          schema:
            type: integer
        - name: size
          in: query
          schema:
            type: integer
        - name: type
          in: query
          schema:
            type: string
        - name: status
          in: query
          schema:
            type: integer
        - name: receiver
          in: query
          schema:
            type: string
        - name: startTime
          in: query
          schema:
            type: string
            format: date-time
        - name: endTime
          in: query
          schema:
            type: string
            format: date-time
      responses:
        '200':
          description: 成功

  /record/{id}:
    get:
      summary: 获取记录详情
      tags:
        - 记录查询
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /record/statistics:
    get:
      summary: 统计分析
      tags:
        - 记录查询
      parameters:
        - name: startDate
          in: query
          schema:
            type: string
            format: date
        - name: endDate
          in: query
          schema:
            type: string
            format: date
        - name: type
          in: query
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /record/retry/{id}:
    post:
      summary: 重试发送
      tags:
        - 记录查询
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # ========== 订阅管理 ==========
  /subscription:
    post:
      summary: 订阅通知
      tags:
        - 订阅管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - userId
                - channelType
              properties:
                userId:
                  type: integer
                channelType:
                  type: string
                receiver:
                  type: string
      responses:
        '200':
          description: 成功

    delete:
      summary: 取消订阅
      tags:
        - 订阅管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - userId
                - channelType
              properties:
                userId:
                  type: integer
                channelType:
                  type: string
      responses:
        '200':
          description: 成功

  /subscription/user/{userId}:
    get:
      summary: 获取用户订阅列表
      tags:
        - 订阅管理
      parameters:
        - name: userId
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /subscription/dnd:
    post:
      summary: 设置免打扰
      tags:
        - 订阅管理
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - userId
              properties:
                userId:
                  type: integer
                channelType:
                  type: string
                startTime:
                  type: string
                  format: time
                endTime:
                  type: string
                  format: time
                status:
                  type: integer
      responses:
        '200':
          description: 成功

components:
  schemas:
    TemplateDTO:
      type: object
      properties:
        id:
          type: integer
        templateCode:
          type: string
        templateName:
          type: string
        category:
          type: string
        type:
          type: integer
        subject:
          type: string
        content:
          type: string
        variables:
          type: object
        status:
          type: integer
