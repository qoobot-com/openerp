openapi: 3.0.3
info:
  title: QooERP 项目管理模块 API
  version: 1.0.0
  description: QooERP 项目管理模块RESTful API接口文档

servers:
  - url: http://localhost:8093/api/project
    description: 本地开发环境

tags:
  - name: 项目管理
    description: 项目管理接口
  - name: 任务管理
    description: 任务管理接口
  - name: 里程碑
    description: 里程碑管理接口

paths:
  /projects:
    get:
      tags:
        - 项目管理
      summary: 分页查询项目
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
                $ref: '#/components/schemas/PageProjectDTO'
    post:
      tags:
        - 项目管理
      summary: 创建项目
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProjectDTO'
      responses:
        '200':
          description: 成功

  /projects/{id}:
    get:
      tags:
        - 项目管理
      summary: 获取项目详情
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
        - 项目管理
      summary: 更新项目
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
              $ref: '#/components/schemas/ProjectDTO'
      responses:
        '200':
          description: 成功
    delete:
      tags:
        - 项目管理
      summary: 删除项目
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /projects/{id}/approve:
    post:
      tags:
        - 项目管理
      summary: 审批项目
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /projects/{id}/close:
    post:
      tags:
        - 项目管理
      summary: 关闭项目
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

  /tasks:
    get:
      tags:
        - 任务管理
      summary: 分页查询任务
      parameters:
        - name: projectId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 成功
    post:
      tags:
        - 任务管理
      summary: 创建任务
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProjectTaskDTO'
      responses:
        '200':
          description: 成功

  /tasks/{id}/assign:
    post:
      tags:
        - 任务管理
      summary: 分配任务
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
              properties:
                assigneeId:
                  type: integer
      responses:
        '200':
          description: 成功

  /tasks/{id}/complete:
    post:
      tags:
        - 任务管理
      summary: 完成任务
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 成功

components:
  schemas:
    ProjectDTO:
      type: object
      properties:
        id:
          type: integer
        projectNo:
          type: string
        projectName:
          type: string
        status:
          type: integer
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
        managerId:
          type: integer
        managerName:
          type: string
        description:
          type: string

    PageProjectDTO:
      type: object
      properties:
        records:
          type: array
          items:
            $ref: '#/components/schemas/ProjectDTO'
        total:
          type: integer
        page:
          type: integer
        size:
          type: integer

    ProjectTaskDTO:
      type: object
      properties:
        id:
          type: integer
        taskNo:
          type: string
        projectId:
          type: integer
        taskName:
          type: string
        status:
          type: integer
        assigneeId:
          type: integer
        assigneeName:
          type: string
        priority:
          type: integer
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date
