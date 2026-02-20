<template>
  <div class="data-permission-page">
    <!-- 搜索卡片 -->
    <t-card class="search-card">
      <t-form ref="searchForm" :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
        <t-form-item label="角色名称" name="roleName">
          <t-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </t-form-item>
        <t-form-item label="角色编码" name="roleCode">
          <t-input v-model="searchForm.roleCode" placeholder="请输入角色编码" clearable />
        </t-form-item>
        <t-form-item>
          <t-space>
            <t-button theme="primary" type="submit">查询</t-button>
            <t-button theme="default" type="reset">重置</t-button>
          </t-space>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 角色表格 -->
    <t-card class="table-card">
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #status="{ row }">
          <t-tag v-if="row.status === '1'" theme="success" variant="light">启用</t-tag>
          <t-tag v-else theme="danger" variant="light">禁用</t-tag>
        </template>
        <template #dataScope="{ row }">
          <t-tag theme="primary" variant="light">{{ getDataScopeText(row.dataScope) }}</t-tag>
        </template>
        <template #operation="{ row }">
          <t-link theme="primary" @click="handleConfigPermission(row)">配置权限</t-link>
        </template>
      </t-table>
    </t-card>

    <!-- 数据权限配置弹窗 -->
    <t-dialog
      v-model:visible="configDialogVisible"
      header="数据权限配置"
      width="800px"
      :confirm-btn="{
        content: '保存',
        theme: 'primary',
        loading: saving,
      }"
      @confirm="handleSaveConfig"
    >
      <div v-if="currentRole" class="config-content">
        <t-alert theme="info" :message="`正在配置角色「${currentRole.roleName}」的数据权限`" style="margin-bottom: 16px;" />

        <t-form :data="permissionConfig" label-align="right" label-width="120px">
          <t-form-item label="数据权限范围" name="dataScope">
            <t-radio-group v-model="permissionConfig.dataScope" @change="handleDataScopeChange">
              <t-radio value="1">全部数据权限</t-radio>
              <t-radio value="2">自定义数据权限</t-radio>
              <t-radio value="3">本部门数据权限</t-radio>
              <t-radio value="4">本部门及以下数据权限</t-radio>
              <t-radio value="5">仅本人数据权限</t-radio>
            </t-radio-group>
          </t-form-item>

          <!-- 自定义数据权限 - 部门树 -->
          <t-form-item v-if="permissionConfig.dataScope === '2'" label="选择部门" name="deptIds">
            <t-tree
              v-model="permissionConfig.deptIds"
              :data="deptTreeData"
              :checkable="true"
              :default-expanded-all="true"
              hover
            />
          </t-form-item>

          <t-divider>高级配置</t-divider>

          <t-form-item label="角色成员" name="userIds">
            <t-transfer
              v-model="permissionConfig.userIds"
              :data="userData"
              :keys="{ label: 'label', value: 'value' }"
              :target-sort="push"
              search
              :pagination="true"
              :pagination-total="userData.length"
            />
          </t-form-item>

          <t-form-item label="角色部门" name="deptIdsSimple">
            <t-transfer
              v-model="permissionConfig.deptIdsSimple"
              :data="deptData"
              :keys="{ label: 'label', value: 'value' }"
              :target-sort="push"
              search
              :pagination="true"
              :pagination-total="deptData.length"
            />
          </t-form-item>
        </t-form>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

// 搜索表单
const searchForm = reactive({
  roleName: '',
  roleCode: '',
});

// 表格数据
const tableData = ref([]);
const loading = ref(false);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 配置弹窗
const configDialogVisible = ref(false);
const currentRole = ref<any>(null);
const saving = ref(false);

const permissionConfig = reactive({
  roleId: undefined,
  dataScope: '1',
  deptIds: [],
  userIds: [],
  deptIdsSimple: [],
});

// 部门树数据
const deptTreeData = ref([
  {
    value: '1',
    label: 'QooBot科技有限公司',
    children: [
      {
        value: '2',
        label: '技术研发部',
        children: [
          { value: '5', label: '前端组' },
          { value: '6', label: '后端组' },
          { value: '7', label: '测试组' },
        ],
      },
      {
        value: '3',
        label: '市场部',
        children: [
          { value: '8', label: '销售一组' },
          { value: '9', label: '销售二组' },
        ],
      },
      {
        value: '4',
        label: '运营部',
        children: [
          { value: '10', label: '产品组' },
          { value: '11', label: '运营组' },
        ],
      },
    ],
  },
]);

// 用户数据
const userData = computed(() => [
  { value: '1', label: 'admin - 系统管理员' },
  { value: '2', label: 'zhangsan - 张三' },
  { value: '3', label: 'lisi - 李四' },
  { value: '4', label: 'wangwu - 王五' },
  { value: '5', label: 'zhaoliu - 赵六' },
  { value: '6', label: 'sunqi - 孙七' },
  { value: '7', label: 'zhouba - 周八' },
  { value: '8', label: 'wujiu - 吴九' },
]);

// 部门数据
const deptData = computed(() => [
  { value: '2', label: '技术研发部' },
  { value: '3', label: '市场部' },
  { value: '4', label: '运营部' },
  { value: '5', label: '前端组' },
  { value: '6', label: '后端组' },
  { value: '7', label: '测试组' },
  { value: '8', label: '销售一组' },
  { value: '9', label: '销售二组' },
  { value: '10', label: '产品组' },
  { value: '11', label: '运营组' },
]);

// 表格列
const columns = [
  { colKey: 'roleName', title: '角色名称', width: 180 },
  { colKey: 'roleCode', title: '角色编码', width: 150 },
  { colKey: 'dataScope', title: '数据权限范围', width: 180 },
  { colKey: 'deptNames', title: '包含部门', ellipsis: true },
  { colKey: 'userCount', title: '成员数', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '创建时间', width: 180 },
  { colKey: 'operation', title: '操作', width: 120, fixed: 'right' },
];

// 获取数据权限范围文本
const getDataScopeText = (scope: string) => {
  const map: Record<string, string> = {
    '1': '全部数据',
    '2': '自定义数据',
    '3': '本部门数据',
    '4': '本部门及以下',
    '5': '仅本人数据',
  };
  return map[scope] || scope;
};

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const mockData = [
      {
        id: 1,
        roleName: '超级管理员',
        roleCode: 'super_admin',
        dataScope: '1',
        deptNames: '全部部门',
        userCount: 1,
        status: '1',
        createTime: '2026-02-19 10:00:00',
      },
      {
        id: 2,
        roleName: '技术总监',
        roleCode: 'tech_director',
        dataScope: '4',
        deptNames: '技术研发部,前端组,后端组,测试组',
        userCount: 3,
        status: '1',
        createTime: '2026-02-19 10:00:00',
      },
      {
        id: 3,
        roleName: '销售经理',
        roleCode: 'sales_manager',
        dataScope: '2',
        deptNames: '销售一组,销售二组',
        userCount: 5,
        status: '1',
        createTime: '2026-02-19 10:00:00',
      },
      {
        id: 4,
        roleName: '普通员工',
        roleCode: 'employee',
        dataScope: '5',
        deptNames: '',
        userCount: 15,
        status: '1',
        createTime: '2026-02-19 10:00:00',
      },
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
    roleName: '',
    roleCode: '',
  });
  handleSearch();
};

// 配置权限
const handleConfigPermission = (row: any) => {
  currentRole.value = row;
  Object.assign(permissionConfig, {
    roleId: row.id,
    dataScope: row.dataScope,
    deptIds: [],
    userIds: ['1'],
    deptIdsSimple: ['2'],
  });
  configDialogVisible.value = true;
};

// 数据权限范围变化
const handleDataScopeChange = (value: string) => {
  if (value !== '2') {
    permissionConfig.deptIds = [];
  }
};

// 保存配置
const handleSaveConfig = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('保存成功');
    configDialogVisible.value = false;
    loadData();
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
  }
};

// 分页变化
const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
  loadData();
};

loadData();
</script>

<style lang="scss" scoped>
.data-permission-page {
  .search-card,
  .table-card {
    margin-bottom: 16px;
  }

  .config-content {
    :deep(.t-form__item) {
      margin-bottom: 24px;
    }

    :deep(.t-tree) {
      max-height: 300px;
      overflow: auto;
      border: 1px solid var(--td-component-border);
      border-radius: 4px;
      padding: 12px;
    }

    :deep(.t-transfer) {
      width: 100%;
    }
  }
}
</style>
