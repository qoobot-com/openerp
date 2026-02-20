<template>
  <div class="project-knowledge-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="文档总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-file-copy /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="已发布" :value="statistics.published" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="草稿" :value="statistics.draft" :loading="loading">
            <template #prefix><icon-edit /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="总容量" :value="statistics.totalSize" suffix="MB" :loading="loading">
            <template #prefix><icon-folder /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="文档标题" name="title">
          <t-input v-model="searchForm.title" placeholder="请输入文档标题" clearable />
        </t-form-item>
        <t-form-item label="项目名称" name="projectName">
          <t-input v-model="searchForm.projectName" placeholder="请输入项目名称" clearable />
        </t-form-item>
        <t-form-item label="文档分类" name="category">
          <t-select v-model="searchForm.category" placeholder="请选择文档分类" clearable>
            <t-option value="需求文档" label="需求文档" />
            <t-option value="设计文档" label="设计文档" />
            <t-option value="开发文档" label="开发文档" />
            <t-option value="测试文档" label="测试文档" />
            <t-option value="用户手册" label="用户手册" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="draft" label="草稿" />
            <t-option value="published" label="已发布" />
            <t-option value="archived" label="已归档" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <div>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><icon-add /></template>
          新建文档
        </t-button>
        <t-button theme="default" @click="handleUpload">
          <template #icon><icon-upload /></template>
          上传文档
        </t-button>
        <t-button theme="default" @click="handleExport">
          <template #icon><icon-download /></template>
          导出报表
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 文档列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #fileIcon="{ row }">
          <component :is="getFileIcon(row.fileType)" :size="24" class="file-icon" />
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="warning" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'published'" theme="success" variant="light">已发布</t-tag>
          <t-tag v-else theme="default" variant="light">已归档</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="success" @click="handlePublish(row)">发布</t-link>
            <t-link theme="default" @click="handleDownload(row)">下载</t-link>
            <t-popconfirm content="确定要删除此文档吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新建/编辑文档弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="文档编号" name="docCode">
              <t-input v-model="formData.docCode" placeholder="自动生成" disabled />
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="关联项目" name="projectId">
              <t-select v-model="formData.projectId" placeholder="请选择项目">
                <t-option value="1" label="ERP系统开发" />
                <t-option value="2" label="CRM系统开发" />
                <t-option value="3" label="移动端开发" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="文档标题" name="title">
          <t-input v-model="formData.title" placeholder="请输入文档标题" />
        </t-form-item>
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="文档分类" name="category">
              <t-select v-model="formData.category" placeholder="请选择文档分类">
                <t-option value="需求文档" label="需求文档" />
                <t-option value="设计文档" label="设计文档" />
                <t-option value="开发文档" label="开发文档" />
                <t-option value="测试文档" label="测试文档" />
                <t-option value="用户手册" label="用户手册" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item label="状态" name="status">
              <t-select v-model="formData.status" placeholder="请选择状态">
                <t-option value="draft" label="草稿" />
                <t-option value="published" label="已发布" />
                <t-option value="archived" label="已归档" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item label="文档标签" name="tags">
          <t-input v-model="formData.tags" placeholder="请输入标签，用逗号分隔" />
        </t-form-item>
        <t-form-item label="文档内容" name="content">
          <t-textarea v-model="formData.content" placeholder="请输入文档内容" :autosize="{ minRows: 10, maxRows: 20 }" :maxlength="10000" />
        </t-form-item>
        <t-form-item label="附件" name="attachments">
          <t-upload v-model="formData.attachments" theme="file-input" multiple />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
        <t-button theme="primary" @click="handleSavePublish">保存并发布</t-button>
      </div>
    </t-dialog>

    <!-- 文档详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="文档详情"
      width="1000px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="文档编号">{{ detailData.docCode }}</t-descriptions-item>
            <t-descriptions-item label="关联项目">{{ detailData.projectName }}</t-descriptions-item>
            <t-descriptions-item label="文档标题" :span="2">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="文档分类">{{ detailData.category }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="detailData.status === 'draft'" theme="warning">草稿</t-tag>
              <t-tag v-else-if="detailData.status === 'published'" theme="success">已发布</t-tag>
              <t-tag v-else>已归档</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建人">{{ detailData.creator }}</t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="更新人">{{ detailData.updater }}</t-descriptions-item>
            <t-descriptions-item label="更新时间">{{ detailData.updateTime }}</t-descriptions-item>
            <t-descriptions-item label="标签" :span="2">{{ detailData.tags }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="文档内容">
          <div class="knowledge-content">{{ detailData.content }}</div>
        </t-tab-panel>

        <t-tab-panel value="attachments" label="附件">
          <t-table :data="detailData.attachments || []" :columns="attachmentColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="history" label="版本历史">
          <t-table :data="detailData.history || []" :columns="historyColumns" :pagination="false" />
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconFileCopy, IconCheckCircle, IconEdit, IconFolder,
  IconAdd, IconUpload, IconDownload, IconRefresh,
  IconFileDoc, IconFilePdf, IconFileExcel, IconFilePowerpoint, IconFileImage, IconZip
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新建文档');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  total: 156,
  published: 128,
  draft: 18,
  totalSize: 89.5
});

const searchForm = reactive({
  title: '',
  projectName: '',
  category: '',
  status: ''
});

const formData = reactive({
  id: '',
  docCode: '',
  projectId: '',
  title: '',
  category: '',
  status: 'draft',
  tags: '',
  content: '',
  attachments: [],
  remark: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 156
});

const columns = [
  { colKey: 'fileIcon', title: '类型', width: 80 },
  { colKey: 'title', title: '文档标题', width: 250, ellipsis: true },
  { colKey: 'projectName', title: '关联项目', width: 150 },
  { colKey: 'category', title: '文档分类', width: 100 },
  { colKey: 'creator', title: '创建人', width: 100 },
  { colKey: 'fileSize', title: '文件大小', width: 100 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'updateTime', title: '更新时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 200, fixed: 'right' }
];

const attachmentColumns = [
  { colKey: 'fileName', title: '文件名', width: 200 },
  { colKey: 'fileSize', title: '文件大小', width: 100 },
  { colKey: 'uploader', title: '上传人', width: 100 },
  { colKey: 'uploadTime', title: '上传时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 100 }
];

const historyColumns = [
  { colKey: 'version', title: '版本号', width: 100 },
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 150 },
  { colKey: 'operateTime', title: '操作时间', width: 150 },
  { colKey: 'comment', title: '备注', width: 200 }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  projectId: [{ required: true, message: '请选择项目', type: 'error' }],
  title: [{ required: true, message: '请输入文档标题', type: 'error' }],
  category: [{ required: true, message: '请选择文档分类', type: 'error' }],
  content: [{ required: true, message: '请输入文档内容', type: 'error' }]
};

const getFileIcon = (fileType: string) => {
  switch (fileType) {
    case 'doc':
    case 'docx':
      return h(IconFileDoc);
    case 'pdf':
      return h(IconFilePdf);
    case 'xls':
    case 'xlsx':
      return h(IconFileExcel);
    case 'ppt':
    case 'pptx':
      return h(IconFilePowerpoint);
    case 'jpg':
    case 'jpeg':
    case 'png':
    case 'gif':
      return h(IconFileImage);
    case 'zip':
    case 'rar':
      return h(IconZip);
    default:
      return h(IconFileCopy);
  }
};

const tableData = ref([
  {
    id: 1,
    docCode: 'KD202600001',
    projectId: '1',
    projectName: 'ERP系统开发',
    title: 'ERP系统需求规格说明书',
    category: '需求文档',
    status: 'published',
    tags: '需求,ERP,v1.0',
    content: '本文档描述ERP系统的功能需求...',
    attachments: [],
    creator: '张三',
    fileSize: '2.5 MB',
    fileType: 'docx',
    remark: '初版',
    createTime: '2026-01-15 10:00:00',
    updateTime: '2026-02-10 14:30:00',
    updater: '李四'
  },
  {
    id: 2,
    docCode: 'KD202600002',
    projectId: '1',
    projectName: 'ERP系统开发',
    title: 'ERP系统架构设计文档',
    category: '设计文档',
    status: 'published',
    tags: '架构,设计,ERP',
    content: '本文档描述ERP系统的架构设计...',
    attachments: [],
    creator: '李四',
    fileSize: '3.2 MB',
    fileType: 'pdf',
    remark: '',
    createTime: '2026-01-20 09:00:00',
    updateTime: '2026-02-08 16:00:00',
    updater: '李四'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    title: '',
    projectName: '',
    category: '',
    status: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新建文档';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑文档';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.history = [
    { version: 'v1.0', operator: '张三', action: '创建文档', operateTime: '2026-01-15 10:00:00', comment: '初版' },
    { version: 'v1.1', operator: '李四', action: '更新文档', operateTime: '2026-02-10 14:30:00', comment: '补充需求' }
  ];
  detailData.logs = [
    { operator: '张三', action: '创建文档', createTime: '2026-01-15 10:00:00' },
    { operator: '李四', action: '更新文档', createTime: '2026-02-10 14:30:00' }
  ];
};

const handlePublish = (row: any) => {
  MessagePlugin.success('文档已发布');
};

const handleDownload = (row: any) => {
  MessagePlugin.success('文档下载中...');
};

const handleUpload = () => {
  MessagePlugin.info('上传文档功能开发中');
};

const handleDelete = (row: any) => {
  MessagePlugin.success('文档已删除');
};

const handleSaveDraft = () => {
  MessagePlugin.success('草稿已保存');
  dialogVisible.value = false;
};

const handleSavePublish = () => {
  MessagePlugin.success('文档已保存并发布');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    docCode: '',
    projectId: '',
    title: '',
    category: '',
    status: 'draft',
    tags: '',
    content: '',
    attachments: [],
    remark: ''
  });
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

const handleRefresh = () => {
  MessagePlugin.success('数据已刷新');
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('ProjectKnowledge mounted');
});
</script>

<style scoped lang="less">
.project-knowledge-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.file-icon {
  color: #0052d9;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.knowledge-content {
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
  white-space: pre-wrap;
  min-height: 300px;
}
</style>
