<template>
  <div class="dict-page">
    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="字典名称" name="dictName">
          <t-input v-model="searchForm.dictName" placeholder="请输入字典名称" clearable />
        </t-form-item>
        <t-form-item label="字典编码" name="dictCode">
          <t-input v-model="searchForm.dictCode" placeholder="请输入字典编码" clearable />
        </t-form-item>
        <t-form-item label="状态" name="status">
          <t-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <t-option value="1" label="启用" />
            <t-option value="0" label="禁用" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 操作栏 -->
    <t-card class="action-card">
      <t-space>
        <t-button theme="primary" @click="handleAdd">
          <template #icon><t-icon name="add" /></template>
          新增字典
        </t-button>
        <t-button theme="danger" :disabled="!selectedRowKeys.length" @click="handleBatchDelete">
          <template #icon><t-icon name="delete" /></template>
          批量删除
        </t-button>
      </t-space>
    </t-card>

    <!-- 字典表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :selected-row-keys="selectedRowKeys"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === '1'" theme="success" variant="light">启用</t-tag>
          <t-tag v-else theme="danger" variant="light">禁用</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleViewData(row)">数据项</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-popconfirm content="确认删除该字典吗？" @confirm="handleDelete(row.id)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 字典编辑弹窗 -->
    <dict-dialog
      v-model:visible="dialogVisible"
      :dict-data="currentDict"
      @success="handleDialogSuccess"
    />

    <!-- 字典数据项弹窗 -->
    <dict-data-dialog
      v-model:visible="dataDialogVisible"
      :dict-id="currentDictId"
      :dict-name="currentDictName"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import DictDialog from './DictDialog.vue';
import DictDataDialog from './DictDataDialog.vue';

// 搜索表单
const searchForm = reactive({
  dictName: '',
  dictCode: '',
  status: '',
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const selectedRowKeys = ref([]);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 弹窗
const dialogVisible = ref(false);
const dataDialogVisible = ref(false);
const currentDict = ref<any>(null);
const currentDictId = ref<number | null>(null);
const currentDictName = ref('');

// 表格列
const columns = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  { colKey: 'dictName', title: '字典名称', width: 200 },
  { colKey: 'dictCode', title: '字典编码', width: 200 },
  { colKey: 'dictType', title: '字典类型', width: 150 },
  { colKey: 'remark', title: '备注', ellipsis: true },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' },
];

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      { id: 1, dictName: '用户性别', dictCode: 'user_gender', dictType: 'string', remark: '用户性别字典', status: '1', createTime: '2026-02-19 10:00:00' },
      { id: 2, dictName: '用户状态', dictCode: 'user_status', dictType: 'number', remark: '用户状态字典', status: '1', createTime: '2026-02-19 10:00:00' },
      { id: 3, dictName: '岗位级别', dictCode: 'position_level', dictType: 'string', remark: '岗位级别字典', status: '1', createTime: '2026-02-19 10:00:00' },
      { id: 4, dictName: '数据权限', dictCode: 'data_scope', dictType: 'string', remark: '数据权限范围', status: '1', createTime: '2026-02-19 10:00:00' },
      { id: 5, dictName: '订单状态', dictCode: 'order_status', dictType: 'string', remark: '订单状态字典', status: '0', createTime: '2026-02-19 10:00:00' },
    ];
    tableData.value = mockData;
    pagination.total = mockData.length;
  } catch (error) {
    MessagePlugin.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  loadData();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    dictName: '',
    dictCode: '',
    status: '',
  });
  handleSearch();
};

// 新增
const handleAdd = () => {
  currentDict.value = null;
  dialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  currentDict.value = { ...row };
  dialogVisible.value = true;
};

// 删除
const handleDelete = async (id: number) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success('删除成功');
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300));
    MessagePlugin.success(`成功删除 ${selectedRowKeys.value.length} 条数据`);
    selectedRowKeys.value = [];
    loadData();
  } catch (error) {
    MessagePlugin.error('删除失败');
  }
};

// 查看数据项
const handleViewData = (row: any) => {
  currentDictId.value = row.id;
  currentDictName.value = row.dictName;
  dataDialogVisible.value = true;
};

// 选择变化
const handleSelectChange = (value: number[]) => {
  selectedRowKeys.value = value;
};

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

// 弹窗成功
const handleDialogSuccess = () => {
  loadData();
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
.dict-page {
  .search-card,
  .action-card,
  .table-card {
    margin-bottom: 16px;
  }
}
</style>
