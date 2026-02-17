openapi: 3.0.3
info:
  title: QooERP 监控服务模块 API
  version: 1.0.0

servers:
  - url: http://localhost:8101/api/monitor

paths:
  # 监控服务
  /service/list:
    get:
      summary: 服务列表
      responses:
        '200':
          description: 成功

  /service/register:
    post:
      summary: 注册服务
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  /service/{id}:
    get:
      summary: 服务详情
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
      summary: 更新服务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

    delete:
      summary: 删除服务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 告警管理
  /alert/list:
    post:
      summary: 告警列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  /alert/{id}:
    get:
      summary: 告警详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /alert/handle/{id}:
    post:
      summary: 处理告警
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  /alert/statistics:
    get:
      summary: 告警统计
      responses:
        '200':
          description: 成功

  # 告警规则
  /alert-rule/create:
    post:
      summary: 创建告警规则
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  /alert-rule/{id}:
    get:
      summary: 规则详情
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
      summary: 更新规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

    delete:
      summary: 删除规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /alert-rule/list:
    post:
      summary: 规则列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  /alert-rule/{id}/enable:
    put:
      summary: 启用规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /alert-rule/{id}/disable:
    put:
      summary: 禁用规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # 性能指标
  /metrics/current:
    get:
      summary: 当前指标
      parameters:
        - name: serviceName
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /metrics/history:
    post:
      summary: 历史指标
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  /metrics/summary:
    get:
      summary: 指标汇总
      responses:
        '200':
          description: 成功

  /metrics/trend:
    get:
      summary: 指标趋势
      responses:
        '200':
          description: 成功

  # 链路追踪
  /trace/{traceId}:
    get:
      summary: 追踪详情
      parameters:
        - name: traceId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /trace/list:
    post:
      summary: 追踪列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
      responses:
        '200':
          description: 成功

  # 仪表盘
  /dashboard/overview:
    get:
      summary: 仪表盘概览
      responses:
        '200':
          description: 成功

  /dashboard/services-status:
    get:
      summary: 服务状态
      responses:
        '200':
          description: 成功

  /dashboard/realtime-alerts:
    get:
      summary: 实时告警
      responses:
        '200':
          description: 成功

  /dashboard/metrics-trend:
    get:
      summary: 指标趋势
      responses:
        '200':
          description: 成功
