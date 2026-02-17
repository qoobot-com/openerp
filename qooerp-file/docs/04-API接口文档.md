openapi: 3.0.3
info:
  title: QooERP 文件服务模块 API
  version: 1.0.0
  description: 提供文件上传、下载、预览、分享等功能的REST API

servers:
  - url: http://localhost:8097/api/file

paths:
  /upload:
    post:
      summary: 单文件上传
      requestBody:
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                folderId:
                  type: integer
                  description: 文件夹ID
      responses:
        '200':
          description: 上传成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  code:
                    type: integer
                  data:
                    $ref: '#/components/schemas/FileInfoDTO'

  /upload/batch:
    post:
      summary: 批量文件上传
      requestBody:
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                files:
                  type: array
                  items:
                    type: string
                    format: binary
                folderId:
                  type: integer
      responses:
        '200':
          description: 上传成功

  /upload/chunk/init:
    post:
      summary: 初始化分片上传
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                fileName:
                  type: string
                fileSize:
                  type: integer
                fileMd5:
                  type: string
                chunkSize:
                  type: integer
                totalChunks:
                  type: integer
                folderId:
                  type: integer
      responses:
        '200':
          description: 初始化成功

  /upload/chunk:
    post:
      summary: 上传文件分片
      parameters:
        - name: taskId
          in: query
          required: true
          schema:
            type: string
        - name: chunkIndex
          in: query
          required: true
          schema:
            type: integer
      requestBody:
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
      responses:
        '200':
          description: 分片上传成功

  /upload/chunk/complete:
    post:
      summary: 完成分片上传
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                taskId:
                  type: string
                fileName:
                  type: string
                folderId:
                  type: integer
      responses:
        '200':
          description: 合并成功

  /upload/second-pass:
    post:
      summary: 文件秒传
      parameters:
        - name: fileMd5
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 秒传成功

  /download/{id}:
    get:
      summary: 文件下载
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 下载成功
          content:
            application/octet-stream:
              schema:
                type: string
                format: binary

  /download/batch:
    get:
      summary: 批量下载
      parameters:
        - name: ids
          in: query
          required: true
          schema:
            type: string
      responses:
        '200':
          description: 下载成功（ZIP）

  /preview/{id}:
    get:
      summary: 文件预览
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 预览成功

  /preview/{id}/thumbnail:
    get:
      summary: 获取缩略图
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
        - name: width
          in: query
          schema:
            type: integer
        - name: height
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 缩略图

  /list:
    get:
      summary: 文件列表查询
      parameters:
        - name: folderId
          in: query
          schema:
            type: integer
        - name: fileName
          in: query
          schema:
            type: string
        - name: fileType
          in: query
          schema:
            type: string
        - name: pageNum
          in: query
          schema:
            type: integer
            default: 1
        - name: pageSize
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: 查询成功

  /{id}:
    get:
      summary: 获取文件详情
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功

  /{id}/rename:
    put:
      summary: 文件重命名
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/x-www-form-urlencoded:
            schema:
              type: object
              properties:
                newFileName:
                  type: string
      responses:
        '200':
          description: 重命名成功

  /{id}:
    delete:
      summary: 删除文件
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: 删除成功

  /{id}/move:
    put:
      summary: 移动文件
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        content:
          application/x-www-form-urlencoded:
            schema:
              type: object
              properties:
                targetFolderId:
                  type: integer
      responses:
        '200':
          description: 移动成功

  /share/{id}:
    post:
      summary: 创建分享链接
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
              properties:
                sharePassword:
                  type: string
                expireDays:
                  type: integer
      responses:
        '200':
          description: 创建成功

  /share/{code}:
    get:
      summary: 访问分享链接
      parameters:
        - name: code
          in: path
          required: true
          schema:
            type: string
        - name: password
          in: query
          schema:
            type: string
      responses:
        '200':
          description: 访问成功

  /folder:
    post:
      summary: 创建文件夹
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                folderName:
                  type: string
                parentId:
                  type: integer
      responses:
        '200':
          description: 创建成功

  /folder/list:
    get:
      summary: 文件夹列表
      parameters:
        - name: parentId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: 查询成功

  /quota/info:
    get:
      summary: 获取存储配额信息
      responses:
        '200':
          description: 查询成功

  /storage/statistics:
    get:
      summary: 获取存储统计
      responses:
        '200':
          description: 查询成功

components:
  schemas:
    FileInfoDTO:
      type: object
      properties:
        id:
          type: integer
        fileNo:
          type: string
        fileName:
          type: string
        filePath:
          type: string
        fileSize:
          type: integer
        fileType:
          type: string
        fileExtension:
          type: string
        folderId:
          type: integer
        status:
          type: integer
        downloadCount:
          type: integer
        createTime:
          type: string
          format: date-time
