openapi: 3.0.3
info:
  title: QooERP 工作流引擎模块 API
  version: 2.0.0
  description: QooERP 工作流引擎模块RESTful API接口文档

servers:
  - url: http://localhost:8095/api/workflow
    description: 本地开发环境

tags:
  - name: 流程定义
    description: 流程定义管理接口
  - name: 流程实例
    description: 流程实例管理接口
  - name: 流程任务
    description: 流程任务管理接口
  - name: 流程表单
    description: 流程表单管理接口
  - name: 流程监控
    description: 流程监控统计接口

paths:
  /definitions:
    get:
      tags:
        - 流程定义
      summary: 分页查询流程定义
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
        - name: status
          in: query
          schema:
            type: integer
            description: 状态(0-草稿,1-已发布,2-已停用)
        - name: categoryId
          in: query
          schema:
            type: integer
            description: 分类ID
        - name: definitionName
          in: query
          schema:
            type: string
            description: 流程名称（模糊查询）
        - name: definitionKey
          in: query
          schema:
            type: string
            description: 流程Key
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PageWorkflowDefinitionDTO'
    post:
      tags:
        - 流程定义
      summary: 创建流程定义
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/WorkflowDefinitionDTO'
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultWorkflowDefinitionDTO'

  /definitions/{id}:
    get:
      tags:
        - 流程定义
      summary: 获取流程定义
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
        - 流程定义
      summary: 更新流程定义
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
              $ref: '#/components/schemas/WorkflowDefinitionDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 流程定义
      summary: 删除流程定义
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /definitions/{id}/deploy:
    post:
      tags:
        - 流程定义
      summary: 部署流程定义
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /definitions/{id}/activate:
    post:
      tags:
        - 流程定义
      summary: 发布流程定义
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /definitions/{id}/suspend:
    post:
      tags:
        - 流程定义
      summary: 停用流程定义
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /definitions/{id}/copy:
    post:
      tags:
        - 流程定义
      summary: 复制流程定义
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
        - name: newKey
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /definitions/import:
    post:
      tags:
        - 流程定义
      summary: 导入流程定义
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                definitionName:
                  type: string
                definitionKey:
                  type: string
                categoryId:
                  type: integer
      responses:
        '200':
          description: 成功

  /definitions/{id}/export:
    get:
      tags:
        - 流程定义
      summary: 导出流程定义
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/DefinitionExportDTO'

  /definitions/{key}/versions:
    get:
      tags:
        - 流程定义
      summary: 获取流程定义版本列表
      parameters:
        - name: key
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances:
    post:
      tags:
        - 流程实例
      summary: 启动流程实例
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/InstanceStartDTO'
      responses:
        '200':
          description: 成功
    get:
      tags:
        - 流程实例
      summary: 分页查询流程实例
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
        - name: status
          in: query
          schema:
            type: integer
            description: 状态(0-运行中,1-已完成,2-已取消,3-已挂起)
        - name: definitionKey
          in: query
          schema:
            type: string
            description: 流程定义Key
        - name: businessKey
          in: query
          schema:
            type: string
            description: 业务Key
        - name: startUserId
          in: query
          schema:
            type: integer
            description: 发起人ID
      responses:
        '200':
          description: 成功

  /instances/{id}:
    get:
      tags:
        - 流程实例
      summary: 获取流程实例
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances/{id}/detail:
    get:
      tags:
        - 流程实例
      summary: 获取流程实例详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances/{id}/cancel:
    post:
      tags:
        - 流程实例
      summary: 取消流程实例
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances/{id}/suspend:
    post:
      tags:
        - 流程实例
      summary: 挂起流程实例
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances/{id}/activate:
    post:
      tags:
        - 流程实例
      summary: 恢复流程实例
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances/{id}/withdraw:
    post:
      tags:
        - 流程实例
      summary: 撤回流程实例
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                reason:
                  type: string
                  description: 撤回原因
      responses:
        '200':
          description: 成功

  /instances/{id}/transfer:
    post:
      tags:
        - 流程实例
      summary: 转办流程实例
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
        - name: targetUserId
          in: query
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /instances/{id}/progress:
    get:
      tags:
        - 流程实例
      summary: 获取流程实例进度
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /instances/{id}/diagram:
    get:
      tags:
        - 流程实例
      summary: 获取流程实例流转图
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/InstanceDiagramDTO'

  /instances/{id}/logs:
    get:
      tags:
        - 流程实例
      summary: 获取流程实例日志
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /tasks/todo:
    get:
      tags:
        - 流程任务
      summary: 获取待办任务
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
        - name: taskName
          in: query
          schema:
            type: string
            description: 任务名称（模糊查询）
        - name: priority
          in: query
          schema:
            type: integer
            description: 优先级(1-低,2-中,3-高)
      responses:
        '200':
          description: 成功

  /tasks/done:
    get:
      tags:
        - 流程任务
      summary: 获取已办任务
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

  /tasks/cc:
    get:
      tags:
        - 流程任务
      summary: 获取抄送任务
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

  /tasks/{id}:
    get:
      tags:
        - 流程任务
      summary: 获取任务详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /tasks/complete:
    post:
      tags:
        - 流程任务
      summary: 完成任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaskCompleteDTO'
      responses:
        '200':
          description: 成功

  /tasks/reject:
    post:
      tags:
        - 流程任务
      summary: 驳回任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaskRejectDTO'
      responses:
        '200':
          description: 成功

  /tasks/{id}/transfer:
    post:
      tags:
        - 流程任务
      summary: 转派任务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
        - name: targetUserId
          in: query
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /tasks/{id}/delegate:
    post:
      tags:
        - 流程任务
      summary: 委派任务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
        - name: targetUserId
          in: query
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /tasks/{id}/withdraw:
    post:
      tags:
        - 流程任务
      summary: 撤回任务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /tasks/add-sign:
    post:
      tags:
        - 流程任务
      summary: 加签任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaskAddSignDTO'
      responses:
        '200':
          description: 成功

  /tasks/remove-sign:
    post:
      tags:
        - 流程任务
      summary: 减签任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaskRemoveSignDTO'
      responses:
        '200':
          description: 成功

  /tasks/{id}/form:
    get:
      tags:
        - 流程任务
      summary: 获取任务表单
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /tasks/form/save:
    post:
      tags:
        - 流程任务
      summary: 保存任务表单
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TaskFormSaveDTO'
      responses:
        '200':
          description: 成功

  /tasks/{id}/history:
    get:
      tags:
        - 流程任务
      summary: 获取任务流转历史
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /forms:
    get:
      tags:
        - 流程表单
      summary: 分页查询表单
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
        - name: formName
          in: query
          schema:
            type: string
            description: 表单名称（模糊查询）
        - name: definitionId
          in: query
          schema:
            type: string
            description: 流程定义ID
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 流程表单
      summary: 创建表单
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/WorkflowFormDTO'
      responses:
        '200':
          description: 成功

  /forms/{id}:
    get:
      tags:
        - 流程表单
      summary: 获取表单
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
        - 流程表单
      summary: 更新表单
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
              $ref: '#/components/schemas/WorkflowFormDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 流程表单
      summary: 删除表单
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /forms/record/save:
    post:
      tags:
        - 流程表单
      summary: 保存表单数据
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/FormRecordSaveDTO'
      responses:
        '200':
          description: 成功

  /forms/record/{id}:
    get:
      tags:
        - 流程表单
      summary: 获取表单数据
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 流程表单
      summary: 删除表单数据
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /monitor/statistics:
    get:
      tags:
        - 流程监控
      summary: 流程统计
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
        - name: definitionKey
          in: query
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /monitor/performance:
    get:
      tags:
        - 流程监控
      summary: 流程性能分析
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
        - name: definitionKey
          in: query
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /monitor/node-durations:
    get:
      tags:
        - 流程监控
      summary: 节点停留时间分析
      parameters:
        - name: definitionKey
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 成功

  /monitor/overdue-tasks:
    get:
      tags:
        - 流程监控
      summary: 超时任务统计
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

  /monitor/completion-rate:
    get:
      tags:
        - 流程监控
      summary: 流程完成率统计
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
      responses:
        '200':
          description: 成功

components:
  schemas:
    WorkflowDefinitionDTO:
      type: object
      properties:
        id:
          type: integer
        definitionId:
          type: string
        definitionName:
          type: string
        definitionKey:
          type: string
        version:
          type: integer
        bpmnContent:
          type: string
        svgContent:
          type: string
        status:
          type: integer
        categoryId:
          type: integer
        description:
          type: string
        deployTime:
          type: string
          format: date-time
        isSystem:
          type: integer
        allowWithdraw:
          type: integer
        withdrawTimeLimit:
          type: integer
        allowTransfer:
          type: integer
        allowDelegate:
          type: integer

    PageWorkflowDefinitionDTO:
      type: object
      properties:
        records:
          type: array
          items:
            $ref: '#/components/schemas/WorkflowDefinitionDTO'
        total:
          type: integer
        page:
          type: integer
        size:
          type: integer

    ResultWorkflowDefinitionDTO:
      type: object
      properties:
        code:
          type: integer
        message:
          type: string
        data:
          $ref: '#/components/schemas/WorkflowDefinitionDTO'

    InstanceStartDTO:
      type: object
      required:
        - definitionId
        - instanceName
      properties:
        definitionId:
          type: string
        instanceName:
          type: string
        businessKey:
          type: string
        variables:
          type: object
          additionalProperties: true
        formData:
          type: object
          additionalProperties: true

    WorkflowInstanceDTO:
      type: object
      properties:
        instanceId:
          type: string
        definitionId:
          type: string
        definitionKey:
          type: string
        instanceName:
          type: string
        businessKey:
          type: string
        status:
          type: integer
        startTime:
          type: string
          format: date-time
        endTime:
          type: string
          format: date-time
        duration:
          type: integer
        startUserId:
          type: integer
        startUserName:
          type: string
        currentNode:
          type: string

    TaskCompleteDTO:
      type: object
      required:
        - taskId
      properties:
        taskId:
          type: string
        comment:
          type: string
        variables:
          type: object
          additionalProperties: true
        formData:
          type: object
          additionalProperties: true

    TaskRejectDTO:
      type: object
      required:
        - taskId
        - comment
      properties:
        taskId:
          type: string
        comment:
          type: string
        rejectTo:
          type: string
        formData:
          type: object
          additionalProperties: true

    TaskAddSignDTO:
      type: object
      required:
        - taskId
        - userIds
      properties:
        taskId:
          type: string
        userIds:
          type: array
          items:
            type: integer
        addSignType:
          type: string
          enum: [before, after]
          description: 加签类型(before-前加签,after-后加签)
        comment:
          type: string

    TaskRemoveSignDTO:
      type: object
      required:
        - taskId
        - userIds
      properties:
        taskId:
          type: string
        userIds:
          type: array
          items:
            type: integer
        comment:
          type: string

    TaskFormSaveDTO:
      type: object
      required:
        - taskId
        - formData
      properties:
        taskId:
          type: string
        formData:
          type: object
          additionalProperties: true

    WorkflowFormDTO:
      type: object
      properties:
        id:
          type: integer
        formKey:
          type: string
        formName:
          type: string
        formType:
          type: string
        formSchema:
          type: string
        formLayout:
          type: string
        definitionId:
          type: string
        nodeId:
          type: string
        status:
          type: integer
        description:
          type: string
        version:
          type: integer

    FormRecordSaveDTO:
      type: object
      required:
        - formId
        - formData
      properties:
        formId:
          type: integer
        instanceId:
          type: string
        taskId:
          type: string
        formData:
          type: object
          additionalProperties: true

    DefinitionExportDTO:
      type: object
      properties:
        definitionName:
          type: string
        definitionKey:
          type: string
        bpmnContent:
          type: string
        svgContent:
          type: string
        formData:
          type: object
          additionalProperties: true

    InstanceDiagramDTO:
      type: object
      properties:
        instanceId:
          type: string
        svgContent:
          type: string
        activeNodes:
          type: array
          items:
            type: string
        completedNodes:
          type: array
          items:
            type: string

    ResultVoid:
      type: object
      properties:
        code:
          type: integer
        message:
          type: string
        data:
          type: object
          nullable: true
