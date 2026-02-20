openapi: 3.0.3
info:
  title: QooERP 监控服务模块 API
  version: 1.1.0
  description: qooerp-monitor 监控服务 API 文档，支持 OpenTelemetry OTLP 协议采集、Prometheus 兼容接口、Web 管理接口

servers:
  - url: http://localhost:8101
    description: 本地开发环境
  - url: https://monitor.qooerp.com
    description: 生产环境

tags:
  - name: OTLP
    description: OpenTelemetry 数据采集接口（标准协议）
  - name: Metrics
    description: 指标采集接口（Prometheus 兼容）
  - name: Service
    description: 监控服务管理接口
  - name: Alert
    description: 告警管理接口
  - name: AlertRule
    description: 告警规则管理接口
  - name: AlertChannel
    description: 告警通知渠道管理接口
  - name: Trace
    description: 链路追踪接口
  - name: Log
    description: 日志查询接口
  - name: Dashboard
    description: 仪表盘接口

paths:
  # ========================================
  # OTLP 数据采集接口
  # ========================================
  /monitor/api/otlp/v1/traces:
    post:
      tags: [OTLP]
      summary: 接收 OpenTelemetry Traces 数据
      description: |
        接收符合 OpenTelemetry OTLP 协议的链路追踪数据。
        支持 gRPC 和 HTTP 两种传输方式。
      requestBody:
        required: true
        content:
          application/x-protobuf:
            schema:
              type: string
              format: binary
              description: OTLP Traces Protocol Buffers 数据
      responses:
        '200':
          description: 成功接收
        '400':
          description: 请求数据格式错误

  /monitor/api/otlp/v1/metrics:
    post:
      tags: [OTLP]
      summary: 接收 OpenTelemetry Metrics 数据
      description: |
        接收符合 OpenTelemetry OTLP 协议的指标数据。
        支持 gRPC 和 HTTP 两种传输方式。
      requestBody:
        required: true
        content:
          application/x-protobuf:
            schema:
              type: string
              format: binary
              description: OTLP Metrics Protocol Buffers 数据
      responses:
        '200':
          description: 成功接收
        '400':
          description: 请求数据格式错误

  /monitor/api/otlp/v1/logs:
    post:
      tags: [OTLP]
      summary: 接收 OpenTelemetry Logs 数据
      description: |
        接收符合 OpenTelemetry OTLP 协议的日志数据。
        支持 gRPC 和 HTTP 两种传输方式。
      requestBody:
        required: true
        content:
          application/x-protobuf:
            schema:
              type: string
              format: binary
              description: OTLP Logs Protocol Buffers 数据
      responses:
        '200':
          description: 成功接收
        '400':
          description: 请求数据格式错误

  # ========================================
  # Metrics 采集接口
  # ========================================
  /monitor/api/metrics/push:
    post:
      tags: [Metrics]
      summary: 主动推送指标数据
      description: |
        微服务主动推送指标数据到监控服务。
        适用于不想使用 OTLP 协议的场景。
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - serviceName
                - metrics
              properties:
                serviceName:
                  type: string
                  description: 服务名称
                  example: qooerp-gateway
                instance:
                  type: string
                  description: 实例标识
                  example: qooerp-gateway:8080
                metrics:
                  type: array
                  description: 指标数据列表
                  items:
                    $ref: '#/components/schemas/MetricData'
      responses:
        '200':
          description: 成功推送
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result'

  /monitor/api/metrics/scrape:
    get:
      tags: [Metrics]
      summary: Prometheus 格式拉取指标
      description: |
        兼容 Prometheus 的 Pull 模式，返回 Prometheus 格式的指标数据。
      parameters:
        - name: serviceName
          in: query
          required: true
          schema:
            type: string
          description: 服务名称
          example: qooerp-gateway
        - name: instance
          in: query
          schema:
            type: string
          description: 实例标识（可选）
      responses:
        '200':
          description: Prometheus 格式指标数据
          content:
            text/plain:
              schema:
                type: string
                example: |
                  jvm_memory_used_bytes{application="qooerp-gateway",env="dev"} 8589934592.0
                  jvm_memory_max_bytes{application="qooerp-gateway",env="dev"} 10737418240.0

  # ========================================
  # 监控服务管理
  # ========================================
  /api/monitor/service/list:
    get:
      tags: [Service]
      summary: 服务列表
      parameters:
        - name: tenantId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/Result'
                  - type: object
                    properties:
                      data:
                        type: array
                        items:
                          $ref: '#/components/schemas/MonitorServiceDTO'

  /api/monitor/service/register:
    post:
      tags: [Service]
      summary: 注册服务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - serviceName
                - serviceAddress
                - servicePort
              properties:
                serviceName:
                  type: string
                serviceAddress:
                  type: string
                servicePort:
                  type: integer
                tenantId:
                  type: integer
      responses:
        '200':
          description: 注册成功

  /api/monitor/service/{id}:
    get:
      tags: [Service]
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
      tags: [Service]
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
              $ref: '#/components/schemas/MonitorServiceUpdateDTO'
      responses:
        '200':
          description: 更新成功
    delete:
      tags: [Service]
      summary: 删除服务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 删除成功

  # ========================================
  # 告警管理
  # ========================================
  /api/monitor/alert/list:
    post:
      tags: [Alert]
      summary: 告警列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                status:
                  type: string
                  enum: [ACTIVE, RECOVERED, CLOSED]
                alertLevel:
                  type: integer
                  minimum: 0
                  maximum: 3
                tenantId:
                  type: integer
                page:
                  type: integer
                  default: 1
                pageSize:
                  type: integer
                  default: 20
      responses:
        '200':
          description: 成功

  /api/monitor/alert/{id}:
    get:
      tags: [Alert]
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

  /api/monitor/alert/handle/{id}:
    post:
      tags: [Alert]
      summary: 处理告警
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
              required:
                - action
              properties:
                action:
                  type: string
                  enum: [CONFIRM, CLOSE, IGNORE]
                remark:
                  type: string
      responses:
        '200':
          description: 处理成功

  /api/monitor/alert/statistics:
    get:
      tags: [Alert]
      summary: 告警统计
      parameters:
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
        - name: tenantId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # ========================================
  # 告警规则管理
  # ========================================
  /api/monitor/alert-rule/create:
    post:
      tags: [AlertRule]
      summary: 创建告警规则
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AlertRuleCreateDTO'
      responses:
        '200':
          description: 创建成功

  /api/monitor/alert-rule/{id}:
    get:
      tags: [AlertRule]
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
      tags: [AlertRule]
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
              $ref: '#/components/schemas/AlertRuleUpdateDTO'
      responses:
        '200':
          description: 更新成功
    delete:
      tags: [AlertRule]
      summary: 删除规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 删除成功

  /api/monitor/alert-rule/list:
    post:
      tags: [AlertRule]
      summary: 规则列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                ruleType:
                  type: string
                enabled:
                  type: boolean
                tenantId:
                  type: integer
                page:
                  type: integer
                pageSize:
                  type: integer
      responses:
        '200':
          description: 成功

  /api/monitor/alert-rule/{id}/enable:
    put:
      tags: [AlertRule]
      summary: 启用规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 启用成功

  /api/monitor/alert-rule/{id}/disable:
    put:
      tags: [AlertRule]
      summary: 禁用规则
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 禁用成功

  # ========================================
  # 告警通知渠道管理
  # ========================================
  /api/monitor/alert-channel/create:
    post:
      tags: [AlertChannel]
      summary: 创建通知渠道
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AlertChannelCreateDTO'
      responses:
        '200':
          description: 创建成功

  /api/monitor/alert-channel/list:
    get:
      tags: [AlertChannel]
      summary: 渠道列表
      parameters:
        - name: tenantId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /api/monitor/alert-channel/{id}:
    get:
      tags: [AlertChannel]
      summary: 渠道详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  # ========================================
  # 链路追踪
  # ========================================
  /api/monitor/trace/{traceId}:
    get:
      tags: [Trace]
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

  /api/monitor/trace/list:
    post:
      tags: [Trace]
      summary: 追踪列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                serviceName:
                  type: string
                spanName:
                  type: string
                minDuration:
                  type: integer
                maxDuration:
                  type: integer
                startTime:
                  type: string
                  format: date-time
                endTime:
                  type: string
                  format: date-time
                page:
                  type: integer
                pageSize:
                  type: integer
      responses:
        '200':
          description: 成功

  # ========================================
  # 日志查询
  # ========================================
  /api/monitor/log/list:
    post:
      tags: [Log]
      summary: 日志列表
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                level:
                  type: string
                  enum: [DEBUG, INFO, WARN, ERROR]
                loggerName:
                  type: string
                traceId:
                  type: string
                startTime:
                  type: string
                  format: date-time
                endTime:
                  type: string
                  format: date-time
                page:
                  type: integer
                pageSize:
                  type: integer
      responses:
        '200':
          description: 成功

  # ========================================
  # 仪表盘
  # ========================================
  /api/monitor/dashboard/overview:
    get:
      tags: [Dashboard]
      summary: 仪表盘概览
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                allOf:
                  - $ref: '#/components/schemas/Result'
                  - type: object
                    properties:
                      data:
                        $ref: '#/components/schemas/DashboardOverviewDTO'

  /api/monitor/dashboard/services-status:
    get:
      tags: [Dashboard]
      summary: 服务状态
      responses:
        '200':
          description: 成功

  /api/monitor/dashboard/realtime-alerts:
    get:
      tags: [Dashboard]
      summary: 实时告警
      parameters:
        - name: limit
          in: query
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 成功

  /api/monitor/dashboard/metrics-trend:
    post:
      tags: [Dashboard]
      summary: 指标趋势
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                metricName:
                  type: string
                timeRange:
                  type: string
                  enum: [1h, 6h, 24h, 7d]
      responses:
        '200':
          description: 成功

components:
  schemas:
    # ========================================
    # 通用类型
    # ========================================
    Result:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
          example: 200
        message:
          type: string
          description: 响应消息
          example: success
        data:
          type: object
          description: 响应数据
        timestamp:
          type: integer
          format: int64
          description: 时间戳

    Page:
      type: object
      properties:
        records:
          type: array
          items: {}
        total:
          type: integer
        current:
          type: integer
        pageSize:
          type: integer

    # ========================================
    # Metrics 相关
    # ========================================
    MetricData:
      type: object
      required:
        - metricName
        - metricType
        - metricValue
        - timestamp
      properties:
        metricName:
          type: string
          description: 指标名称
          example: jvm_memory_used_bytes
        metricType:
          type: string
          enum: [gauge, counter, timer, summary]
          description: 指标类型
        metricValue:
          type: number
          format: double
          description: 指标值
          example: 8589934592.0
        tags:
          type: object
          description: 标签
          additionalProperties:
            type: string
          example:
            application: qooerp-gateway
            env: dev
        instance:
          type: string
          description: 实例标识
          example: qooerp-gateway:8080
        timestamp:
          type: integer
          format: int64
          description: 时间戳（毫秒）

    # ========================================
    # Service 相关
    # ========================================
    MonitorServiceDTO:
      type: object
      properties:
        id:
          type: integer
        serviceName:
          type: string
        serviceAddress:
          type: string
        servicePort:
          type: integer
        status:
          type: integer
        healthStatus:
          type: integer
        lastCheckTime:
          type: string
          format: date-time

    MonitorServiceUpdateDTO:
      type: object
      properties:
        serviceAddress:
          type: string
        servicePort:
          type: integer
        status:
          type: integer
        healthStatus:
          type: integer

    # ========================================
    # Alert Rule 相关
    # ========================================
    AlertRuleCreateDTO:
      type: object
      required:
        - ruleName
        - ruleType
        - conditionOperator
        - thresholdValue
        - alertLevel
      properties:
        ruleName:
          type: string
        ruleType:
          type: string
          enum: [service, metric, business]
        metricName:
          type: string
        conditionOperator:
          type: string
          enum: [gt, lt, ge, le, eq]
        thresholdValue:
          type: number
          format: decimal
        duration:
          type: integer
          description: 持续时间（秒）
        alertLevel:
          type: integer
          minimum: 0
          maximum: 3
        notificationChannels:
          type: string
        description:
          type: string

    AlertRuleUpdateDTO:
      type: object
      properties:
        ruleName:
          type: string
        conditionOperator:
          type: string
          enum: [gt, lt, ge, le, eq]
        thresholdValue:
          type: number
          format: decimal
        duration:
          type: integer
        alertLevel:
          type: integer
        notificationChannels:
          type: string
        description:
          type: string

    # ========================================
    # Alert Channel 相关
    # ========================================
    AlertChannelCreateDTO:
      type: object
      required:
        - channelName
        - channelType
      properties:
        channelName:
          type: string
        channelType:
          type: string
          enum: [sms, email, site, webhook]
        channelConfig:
          type: string
          description: JSON 格式配置
        description:
          type: string

    # ========================================
    # Dashboard 相关
    # ========================================
    DashboardOverviewDTO:
      type: object
      properties:
        totalServices:
          type: integer
        healthyServices:
          type: integer
        unhealthyServices:
          type: integer
        activeAlerts:
          type: integer
        recoveredAlerts:
          type: integer
        recentMetricsTrend:
          type: array
          items:
            $ref: '#/components/schemas/MetricsTrendDTO'

    MetricsTrendDTO:
      type: object
      properties:
        metricName:
          type: string
        points:
          type: array
          items:
            $ref: '#/components/schemas/MetricPointDTO'

    MetricPointDTO:
      type: object
      properties:
        timestamp:
          type: integer
          format: int64
        value:
          type: number
          format: double
