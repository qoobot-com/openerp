openapi: 3.0.3
info:
  title: QooERP 任务调度模块 API
  version: 1.0.0

servers:
  - url: http://localhost:8100/api/scheduler

paths:
  /jobs:
    post:
      summary: 创建任务
      responses:
        '200':
          description: 成功

  /jobs/{id}/start:
    post:
      summary: 启动任务
      responses:
        '200':
          description: 成功
