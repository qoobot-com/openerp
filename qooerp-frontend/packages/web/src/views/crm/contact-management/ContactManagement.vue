<template>
  <div class="contact-management-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="联系人总数" :value="statistics.total" :loading="loading">
            <template #prefix><icon-user /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="主要联系人" :value="statistics.primary" :loading="loading">
            <template #prefix><icon-star /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="待跟进" :value="statistics.pending" :loading="loading">
            <template #prefix><icon-time /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="default">
          <t-statistic title="客户数" :value="statistics.customers" :loading="loading">
            <template #prefix><icon-shop /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="联系人编号" name="contactCode">
          <t-input v-model="searchForm.contactCode" placeholder="请输入联系人编号" clearable />
        </t-form-item>
        <t-form-item label="联系人姓名" name="name">
          <t-input v-model="searchForm.name" placeholder="请输入联系人姓名" clearable />
        </t-form-item>
        <t-form-item label="客户名称" name="customerName">
          <t-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </t-form-item>
        <t-form-item label="部门" name="department">
          <t-input v-model="searchForm.department" placeholder="请输入部门" clearable />
        </t-form-item>
        <t-form-item label="职位" name="position">
          <t-input v-model="searchForm.position" placeholder="请输入职位" clearable />
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
          新增联系人
        </t-button>
        <t-button theme="default" @click="handleBatchImport">
          <template #icon><icon-upload /></template>
          批量导入
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

    <!-- 联系人列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        :selected-row-keys="selectedRowKeys"
        @select-change="handleSelectChange"
        @page-change="handlePageChange"
      >
        <template #isPrimary="{ row }">
          <t-tag v-if="row.isPrimary" theme="danger" variant="light">主要</t-tag>
          <t-tag v-else theme="default" variant="light">普通</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'active'" theme="success" variant="light">正常</t-tag>
          <t-tag v-else-if="row.status === 'pending'" theme="warning" variant="light">待跟进</t-tag>
          <t-tag v-else theme="danger" variant="light">停用</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link theme="primary" @click="handleRecord(row)">记录</t-link>
            <t-popconfirm content="确定要删除此联系人吗？" @confirm="handleDelete(row)">
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑联系人弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="800px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="activeTab" theme="card">
        <!-- 基本信息 -->
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="联系人编号" name="contactCode">
                  <t-input v-model="formData.contactCode" placeholder="自动生成" disabled />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="所属客户" name="customerId">
                  <t-select v-model="formData.customerId" placeholder="请选择客户" filterable>
                    <t-option v-for="item in customers" :key="item.id" :value="item.id" :label="item.name" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="姓名" name="name">
                  <t-input v-model="formData.name" placeholder="请输入姓名" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="性别" name="gender">
                  <t-radio-group v-model="formData.gender">
                    <t-radio value="男">男</t-radio>
                    <t-radio value="女">女</t-radio>
                  </t-radio-group>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="部门" name="department">
                  <t-input v-model="formData.department" placeholder="请输入部门" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="职位" name="position">
                  <t-input v-model="formData.position" placeholder="请输入职位" />
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="主要联系人" name="isPrimary">
                  <t-switch v-model="formData.isPrimary" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="状态" name="status">
                  <t-select v-model="formData.status" placeholder="请选择状态">
                    <t-option value="active" label="正常" />
                    <t-option value="pending" label="待跟进" />
                    <t-option value="inactive" label="停用" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="备注" name="remark">
              <t-textarea v-model="formData.remark" placeholder="请输入备注" :maxlength="500" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <!-- 联系方式 -->
        <t-tab-panel value="contact" label="联系方式">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="手机号码" name="mobile">
              <t-input v-model="formData.mobile" placeholder="请输入手机号码" />
            </t-form-item>
            <t-form-item label="办公电话" name="officePhone">
              <t-input v-model="formData.officePhone" placeholder="请输入办公电话" />
            </t-form-item>
            <t-form-item label="邮箱" name="email">
              <t-input v-model="formData.email" placeholder="请输入邮箱" />
            </t-form-item>
            <t-form-item label="微信号" name="wechat">
              <t-input v-model="formData.wechat" placeholder="请输入微信号" />
            </t-form-item>
            <t-form-item label="QQ号" name="qq">
              <t-input v-model="formData.qq" placeholder="请输入QQ号" />
            </t-form-item>
            <t-form-item label="地址" name="address">
              <t-input v-model="formData.address" placeholder="请输入地址" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>

      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSave">保存</t-button>
      </div>
    </t-dialog>

    <!-- 联系人详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="联系人详情"
      width="800px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="联系人编号">{{ detailData.contactCode }}</t-descriptions-item>
            <t-descriptions-item label="所属客户">{{ detailData.customerName }}</t-descriptions-item>
            <t-descriptions-item label="姓名">{{ detailData.name }}</t-descriptions-item>
            <t-descriptions-item label="性别">{{ detailData.gender }}</t-descriptions-item>
            <t-descriptions-item label="部门">{{ detailData.department }}</t-descriptions-item>
            <t-descriptions-item label="职位">{{ detailData.position }}</t-descriptions-item>
            <t-descriptions-item label="主要联系人">{{ detailData.isPrimary ? '是' : '否' }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag v-if="detailData.status === 'active'" theme="success">正常</t-tag>
              <t-tag v-else-if="detailData.status === 'pending'" theme="warning">待跟进</t-tag>
              <t-tag v-else theme="danger">停用</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detailData.remark }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="contact" label="联系方式">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="手机号码">{{ detailData.mobile }}</t-descriptions-item>
            <t-descriptions-item label="办公电话">{{ detailData.officePhone }}</t-descriptions-item>
            <t-descriptions-item label="邮箱">{{ detailData.email }}</t-descriptions-item>
            <t-descriptions-item label="微信号">{{ detailData.wechat }}</t-descriptions-item>
            <t-descriptions-item label="QQ号">{{ detailData.qq }}</t-descriptions-item>
            <t-descriptions-item label="地址" :span="2">{{ detailData.address }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="logs" label="操作日志">
          <t-table :data="detailData.logs || []" :columns="logColumns" :pagination="false" />
        </t-tab-panel>
      </t-tabs>
    </t-dialog>

    <!-- 联系记录弹窗 -->
    <t-dialog
      v-model:visible="recordVisible"
      header="添加联系记录"
      width="600px"
      :footer="false"
    >
      <t-form :data="recordForm" label-width="100px">
        <t-form-item label="联系类型" name="type">
          <t-select v-model="recordForm.type" placeholder="请选择联系类型">
            <t-option value="电话" label="电话" />
            <t-option value="拜访" label="拜访" />
            <t-option value="邮件" label="邮件" />
            <t-option value="微信" label="微信" />
            <t-option value="其他" label="其他" />
          </t-select>
        </t-form-item>
        <t-form-item label="联系内容" name="content">
          <t-textarea v-model="recordForm.content" placeholder="请输入联系内容" />
        </t-form-item>
        <t-form-item label="下次联系时间" name="nextTime">
          <t-date-picker v-model="recordForm.nextTime" placeholder="请选择下次联系时间" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="recordVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSaveRecord">保存</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUser, IconStar, IconTime, IconShop,
  IconAdd, IconUpload, IconDownload, IconRefresh
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const recordVisible = ref(false);
const dialogTitle = ref('新增联系人');
const activeTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();
const selectedRowKeys = ref([]);

const statistics = reactive({
  total: 328,
  primary: 156,
  pending: 45,
  customers: 89
});

const searchForm = reactive({
  contactCode: '',
  name: '',
  customerName: '',
  department: '',
  position: ''
});

const formData = reactive({
  id: '',
  contactCode: '',
  customerId: '',
  name: '',
  gender: '男',
  department: '',
  position: '',
  isPrimary: false,
  status: 'active',
  remark: '',
  mobile: '',
  officePhone: '',
  email: '',
  wechat: '',
  qq: '',
  address: ''
});

const recordForm = reactive({
  contactId: '',
  type: '',
  content: '',
  nextTime: ''
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 328
});

const customers = ref([
  { id: 1, name: '上海科技有限公司' },
  { id: 2, name: '北京贸易公司' }
]);

const columns = [
  { colKey: 'contactCode', title: '联系人编号', width: 130 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'customerName', title: '所属客户', width: 150 },
  { colKey: 'department', title: '部门', width: 100 },
  { colKey: 'position', title: '职位', width: 100 },
  { colKey: 'mobile', title: '手机号码', width: 120 },
  { colKey: 'email', title: '邮箱', width: 150 },
  { colKey: 'isPrimary', title: '主要联系人', width: 90 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 120 },
  { colKey: 'operation', title: '操作', width: 180, fixed: 'right' }
];

const logColumns = [
  { colKey: 'operator', title: '操作人', width: 100 },
  { colKey: 'action', title: '操作内容', width: 200 },
  { colKey: 'createTime', title: '操作时间', width: 150 }
];

const rules = {
  name: [{ required: true, message: '请输入姓名', type: 'error' }],
  customerId: [{ required: true, message: '请选择客户', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    contactCode: 'CT202600001',
    customerId: 1,
    customerName: '上海科技有限公司',
    name: '李四',
    gender: '男',
    department: '采购部',
    position: '采购经理',
    mobile: '13800138000',
    officePhone: '021-12345678',
    email: 'li4@example.com',
    wechat: 'li4_wx',
    qq: '12345678',
    address: '上海市浦东新区张江高科技园区',
    isPrimary: true,
    status: 'active',
    remark: '',
    createTime: '2026-01-15 10:30:00'
  },
  {
    id: 2,
    contactCode: 'CT202600002',
    customerId: 2,
    customerName: '北京贸易公司',
    name: '赵六',
    gender: '女',
    department: '财务部',
    position: '财务总监',
    mobile: '13900139000',
    officePhone: '010-87654321',
    email: 'zhao6@example.com',
    wechat: 'zhao6_wx',
    qq: '87654321',
    address: '北京市朝阳区建国路88号',
    isPrimary: true,
    status: 'active',
    remark: '',
    createTime: '2026-01-20 14:20:00'
  }
]);

const handleSearch = () => {
  MessagePlugin.success('查询成功');
};

const handleReset = () => {
  Object.assign(searchForm, {
    contactCode: '',
    name: '',
    customerName: '',
    department: '',
    position: ''
  });
};

const handleAdd = () => {
  dialogTitle.value = '新增联系人';
  dialogVisible.value = true;
  activeTab.value = 'basic';
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑联系人';
  dialogVisible.value = true;
  activeTab.value = 'basic';
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.logs = [
    { operator: '张三', action: '创建联系人', createTime: '2026-01-15 10:30:00' }
  ];
};

const handleRecord = (row: any) => {
  recordForm.contactId = row.id;
  recordVisible.value = true;
};

const handleSaveRecord = () => {
  MessagePlugin.success('联系记录已保存');
  recordVisible.value = false;
};

const handleDelete = (row: any) => {
  MessagePlugin.success('联系人已删除');
};

const handleSave = () => {
  MessagePlugin.success(dialogTitle.value === '新增联系人' ? '联系人已创建' : '联系人已更新');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '',
    contactCode: '',
    customerId: '',
    name: '',
    gender: '男',
    department: '',
    position: '',
    isPrimary: false,
    status: 'active',
    remark: '',
    mobile: '',
    officePhone: '',
    email: '',
    wechat: '',
    qq: '',
    address: ''
  });
};

const handleBatchImport = () => {
  MessagePlugin.info('批量导入功能开发中');
};

const handleExport = () => {
  MessagePlugin.success('报表导出成功');
};

const handleRefresh = () => {
  MessagePlugin.success('数据已刷新');
};

const handleSelectChange = (value: any) => {
  selectedRowKeys.value = value;
};

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

onMounted(() => {
  console.log('ContactManagement mounted');
});
</script>

<style scoped lang="less">
.contact-management-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
