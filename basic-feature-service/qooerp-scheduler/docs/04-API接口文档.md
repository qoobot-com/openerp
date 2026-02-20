openapi: 3.0.3
info:
  title: QooERP 任务调度模块 API
  version: 1.0.0
  description: QooERP任务调度模块RESTful API接口文档

servers:
  - url: http://localhost:8100/api/scheduler
    description: 开发环境
  - url: http://test.qooerp.com/api/scheduler
    description: 测试环境
  - url: http://api.qooerp.com/api/scheduler
    description: 生产环境

tags:
  - name: 任务管理
    description: 定时任务管理相关接口
  - name: 任务日志
    description: 任务日志查询相关接口
  - name: 任务配置
    description: 任务配置管理相关接口
  - name: 任务报警
    description: 任务报警管理相关接口

paths:
  /jobs:
    post:
      tags:
        - 任务管理
      summary: 创建任务
      description: 创建新的定时任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ScheduleJobDTO'
      responses:
        '200':
          description: 创建成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_ScheduleJobDTO'

    get:
      tags:
        - 任务管理
      summary: 分页查询任务
      description: 分页查询定时任务列表
      parameters:
        - name: jobNo
          in: query
          description: 任务编号
          schema:
            type: string
        - name: jobName
          in: query
          description: 任务名称
          schema:
            type: string
        - name: jobType
          in: query
          description: 任务类型
          schema:
            type: string
            enum: [CRON, INTERVAL, ONCE, DEPENDENT]
        - name: status
          in: query
          description: 任务状态
          schema:
            type: string
            enum: [STOPPED, RUNNING, PAUSED, DELETED]
        - name: current
          in: query
          description: 当前页码
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          description: 每页条数
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Page_ScheduleJobDTO'

  /jobs/{id}:
    get:
      tags:
        - 任务管理
      summary: 查询任务
      description: 根据ID查询任务详情
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_ScheduleJobDTO'

    put:
      tags:
        - 任务管理
      summary: 更新任务
      description: 更新定时任务信息
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ScheduleJobDTO'
      responses:
        '200':
          description: 更新成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_ScheduleJobDTO'

    delete:
      tags:
        - 任务管理
      summary: 删除任务
      description: 删除定时任务
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 删除成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /jobs/{id}/start:
    post:
      tags:
        - 任务管理
      summary: 启动任务
      description: 启动已停止的任务
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 启动成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /jobs/{id}/stop:
    post:
      tags:
        - 任务管理
      summary: 停止任务
      description: 停止正在运行的任务
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 停止成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /jobs/{id}/pause:
    post:
      tags:
        - 任务管理
      summary: 暂停任务
      description: 暂停任务调度
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 暂停成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /jobs/{id}/resume:
    post:
      tags:
        - 任务管理
      summary: 恢复任务
      description: 恢复任务调度
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 恢复成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /jobs/execute:
    post:
      tags:
        - 任务管理
      summary: 手动执行任务
      description: 立即执行指定任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/JobExecuteDTO'
      responses:
        '200':
          description: 执行成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /jobs/{id}/logs:
    get:
      tags:
        - 任务管理
      summary: 获取任务日志
      description: 获取指定任务的执行日志
      parameters:
        - name: id
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
        - name: current
          in: query
          description: 当前页码
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          description: 每页条数
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Page_ScheduleLogDTO'

  /logs:
    get:
      tags:
        - 任务日志
      summary: 查询执行日志
      description: 查询任务执行日志
      parameters:
        - name: jobId
          in: query
          required: true
          description: 任务ID
          schema:
            type: integer
        - name: startTime
          in: query
          description: 开始时间
          schema:
            type: string
            format: date-time
        - name: endTime
          in: query
          description: 结束时间
          schema:
            type: string
            format: date-time
        - name: current
          in: query
          description: 当前页码
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          description: 每页条数
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Page_ScheduleLogDTO'

  /logs/recent/{jobId}:
    get:
      tags:
        - 任务日志
      summary: 获取最近日志
      description: 获取任务最近执行的日志
      parameters:
        - name: jobId
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
        - name: limit
          in: query
          description: 返回数量
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_List_ScheduleLogDTO'

  /configs:
    post:
      tags:
        - 任务配置
      summary: 设置任务配置
      description: 设置任务的配置参数
      parameters:
        - name: jobId
          in: query
          required: true
          description: 任务ID
          schema:
            type: integer
        - name: configKey
          in: query
          required: true
          description: 配置键
          schema:
            type: string
        - name: configValue
          in: query
          required: true
          description: 配置值
          schema:
            type: string
        - name: configDesc
          in: query
          description: 配置描述
          schema:
            type: string
      responses:
        '200':
          description: 设置成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

    get:
      tags:
        - 任务配置
      summary: 获取任务配置
      description: 获取任务的所有配置参数
      parameters:
        - name: jobId
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Map_String_String'

  /configs/{jobId}/{configKey}:
    delete:
      tags:
        - 任务配置
      summary: 删除任务配置
      description: 删除任务的指定配置参数
      parameters:
        - name: jobId
          in: path
          required: true
          description: 任务ID
          schema:
            type: integer
        - name: configKey
          in: path
          required: true
          description: 配置键
          schema:
            type: string
      responses:
        '200':
          description: 删除成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

  /notifies:
    get:
      tags:
        - 任务报警
      summary: 查询报警记录
      description: 分页查询报警记录
      parameters:
        - name: jobId
          in: query
          description: 任务ID
          schema:
            type: integer
        - name: status
          in: query
          description: 状态
          schema:
            type: string
            enum: [PENDING, HANDLED, IGNORED]
        - name: current
          in: query
          description: 当前页码
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          description: 每页条数
          schema:
            type: integer
            default: 10
      responses:
        '200':
          description: 查询成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Page_ScheduleNotifyDTO'

  /notifies/{notifyId}/handle:
    post:
      tags:
        - 任务报警
      summary: 处理报警
      description: 处理报警通知
      parameters:
        - name: notifyId
          in: path
          required: true
          description: 报警ID
          schema:
            type: integer
        - name: handler
          in: query
          required: true
          description: 处理人
          schema:
            type: string
        - name: handleRemark
          in: query
          description: 处理备注
          schema:
            type: string
      responses:
        '200':
          description: 处理成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Result_Void'

components:
  schemas:
    Result_Void:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          type: object
          nullable: true

    ScheduleJobDTO:
      type: object
      properties:
        id:
          type: integer
          description: 任务ID
        jobNo:
          type: string
          description: 任务编号
        jobName:
          type: string
          description: 任务名称
        jobType:
          type: string
          enum: [CRON, INTERVAL, ONCE, DEPENDENT]
          description: 任务类型
        jobClass:
          type: string
          description: 任务类全限定名
        cronExpression:
          type: string
          description: Cron表达式
        interval:
          type: integer
          description: 执行间隔(秒)
        executeStrategy:
          type: string
          enum: [NOW, SCHEDULE, RETRY, SKIP]
          description: 执行策略
        retryStrategy:
          type: string
          enum: [NONE, FIXED, EXPONENTIAL, LINEAR]
          description: 重试策略
        retryCount:
          type: integer
          description: 重试次数
        timeout:
          type: integer
          description: 超时时间(秒)
        concurrency:
          type: string
          enum: [SERIAL, PARALLEL]
          description: 并发策略
        dependentJobId:
          type: integer
          description: 依赖任务ID
        status:
          type: string
          enum: [STOPPED, RUNNING, PAUSED, DELETED]
          description: 状态
        nextExecuteTime:
          type: string
          format: date-time
          description: 下次执行时间
        prevExecuteTime:
          type: string
          format: date-time
          description: 上次执行时间
        executeCount:
          type: integer
          description: 执行次数
        successCount:
          type: integer
          description: 成功次数
        failCount:
          type: integer
          description: 失败次数
        config:
          type: object
          additionalProperties:
            type: string
          description: 任务配置

    ScheduleLogDTO:
      type: object
      properties:
        id:
          type: integer
          description: 日志ID
        jobId:
          type: integer
          description: 任务ID
        jobName:
          type: string
          description: 任务名称
        executeTime:
          type: string
          format: date-time
          description: 执行时间
        executeResult:
          type: string
          description: 执行结果
        executeDuration:
          type: integer
          description: 执行时长(毫秒)
        status:
          type: string
          enum: [SUCCESS, FAILURE, RUNNING, TIMEOUT]
          description: 状态
        exceptionInfo:
          type: string
          description: 异常信息

    JobExecuteDTO:
      type: object
      properties:
        jobId:
          type: integer
          description: 任务ID
        params:
          type: object
          additionalProperties:
            type: object
          description: 执行参数

    ScheduleNotifyDTO:
      type: object
      properties:
        id:
          type: integer
          description: 报警ID
        jobId:
          type: integer
          description: 任务ID
        jobName:
          type: string
          description: 任务名称
        notifyType:
          type: string
          enum: [FAILURE, TIMEOUT, RETRY]
          description: 报警类型
        notifyLevel:
          type: string
          enum: [LOW, MEDIUM, HIGH, CRITICAL]
          description: 报警级别
        notifyContent:
          type: string
          description: 报警内容
        notifyTime:
          type: string
          format: date-time
          description: 报警时间
        status:
          type: string
          enum: [PENDING, HANDLED, IGNORED]
          description: 状态
        handler:
          type: string
          description: 处理人
        handleTime:
          type: string
          format: date-time
          description: 处理时间

    Result_ScheduleJobDTO:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          $ref: '#/components/schemas/ScheduleJobDTO'

    Result_Page_ScheduleJobDTO:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          type: object
          properties:
            records:
              type: array
              items:
                $ref: '#/components/schemas/ScheduleJobDTO'
            total:
              type: integer
            current:
              type: integer
            size:
              type: integer

    Result_Page_ScheduleLogDTO:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          type: object
          properties:
            records:
              type: array
              items:
                $ref: '#/components/schemas/ScheduleLogDTO'
            total:
              type: integer
            current:
              type: integer
            size:
              type: integer

    Result_List_ScheduleLogDTO:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          type: array
          items:
            $ref: '#/components/schemas/ScheduleLogDTO'

    Result_Page_ScheduleNotifyDTO:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          type: object
          properties:
            records:
              type: array
              items:
                $ref: '#/components/schemas/ScheduleNotifyDTO'
            total:
              type: integer
            current:
              type: integer
            size:
              type: integer

    Result_Map_String_String:
      type: object
      properties:
        code:
          type: integer
          description: 响应码
        message:
          type: string
          description: 响应消息
        data:
          type: object
          additionalProperties:
            type: string
