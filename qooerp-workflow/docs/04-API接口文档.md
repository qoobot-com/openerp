openapi: 3.0.3
info:
  title: QooERP 工作流引擎模块 API
  version: 1.0.0
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
              $ref: '#/components/schemas/WorkflowInstanceStartDTO'
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
        - name: status
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /instances/{id}:
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

  /tasks/todo:
    get:
      tags:
        - 流程任务
      summary: 获取待办任务
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResultListWorkflowTaskDTO'

  /tasks/done:
    get:
      tags:
        - 流程任务
      summary: 获取已办任务
      responses:
        '200':
          description: 成功

  /tasks/{id}/complete:
    post:
      tags:
        - 流程任务
      summary: 完成任务
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
              $ref: '#/components/schemas/TaskCompleteDTO'
      responses:
        '200':
          description: 成功

  /tasks/{id}/reject:
    post:
      tags:
        - 流程任务
      summary: 驳回任务
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
              $ref: '#/components/schemas/TaskRejectDTO'
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
        status:
          type: integer
        category:
          type: string
        description:
          type: string
        deployTime:
          type: string
          format: date-time

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

    WorkflowInstanceStartDTO:
      type: object
      properties:
        definitionId:
          type: string
        businessKey:
          type: string
        instanceName:
          type: string
        variables:
          type: object
          additionalProperties: true

    WorkflowInstanceDTO:
      type: object
      properties:
        instanceId:
          type: string
        definitionId:
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
        startUserId:
          type: integer
        startUserName:
          type: string

    WorkflowTaskDTO:
      type: object
      properties:
        taskId:
          type: string
        instanceId:
          type: string
        taskName:
          type: string
        taskKey:
          type: string
        assigneeId:
          type: integer
        assigneeName:
          type: string
        status:
          type: integer
        createTime:
          type: string
          format: date-time
        dueTime:
          type: string
          format: date-time

    TaskCompleteDTO:
      type: object
      properties:
        comment:
          type: string
        variables:
          type: object
          additionalProperties: true

    TaskRejectDTO:
      type: object
      properties:
        comment:
          type: string
        rejectTo:
          type: string

    ResultListWorkflowTaskDTO:
      allOf:
        - $ref: '#/components/schemas/ResultVoid'
        - type: object
          properties:
            data:
              type: array
              items:
                $ref: '#/components/schemas/WorkflowTaskDTO'

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
