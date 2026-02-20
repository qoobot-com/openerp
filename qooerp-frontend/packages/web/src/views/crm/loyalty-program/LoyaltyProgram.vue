<template>
  <div class="loyalty-program-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="会员总数" :value="statistics.totalMembers" :loading="loading">
            <template #prefix><icon-usergroup /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="活跃会员" :value="statistics.activeMembers" :loading="loading">
            <template #prefix><icon-check-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="积分总数" :value="statistics.totalPoints" :loading="loading">
            <template #prefix><icon-star /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="已兑换" :value="statistics.redeemedPoints" :loading="loading">
            <template #prefix><icon-gift /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="会员编号" name="memberCode">
          <t-input v-model="searchForm.memberCode" placeholder="请输入会员编号" clearable />
        </t-form-item>
        <t-form-item label="会员姓名" name="memberName">
          <t-input v-model="searchForm.memberName" placeholder="请输入会员姓名" clearable />
        </t-form-item>
        <t-form-item label="会员等级" name="level">
          <t-select v-model="searchForm.level" placeholder="请选择会员等级" clearable>
            <t-option value="platinum" label="白金会员" />
            <t-option value="gold" label="黄金会员" />
            <t-option value="silver" label="白银会员" />
            <t-option value="bronze" label="青铜会员" />
          </t-select>
        </t-form-item>
        <t-form-item>
          <t-button theme="primary" type="submit">查询</t-button>
          <t-button theme="default" @click="handleReset">重置</t-button>
        </t-form-item>
      </t-form>
    </t-card>

    <!-- 标签页 -->
    <t-card>
      <t-tabs v-model="activeTab" theme="card">
        <!-- 会员列表 -->
        <t-tab-panel value="members" label="会员列表">
          <div class="mb-4 flex justify-between items-center">
            <div>
              <t-button theme="primary" @click="handleAddMember">
                <template #icon><icon-add /></template>
                新增会员
              </t-button>
              <t-button theme="default" @click="handleBatchAdjust">
                <template #icon><icon-edit /></template>
                批量调分
              </t-button>
            </div>
            <t-button theme="default" variant="outline" @click="handleRefresh">
              <template #icon><icon-refresh /></template>
              刷新
            </t-button>
          </div>
          <t-table
            :data="memberData"
            :columns="memberColumns"
            :loading="loading"
            :pagination="pagination"
            @page-change="handlePageChange"
          >
            <template #level="{ row }">
              <t-tag v-if="row.level === 'platinum'" theme="primary">白金会员</t-tag>
              <t-tag v-else-if="row.level === 'gold'" theme="success">黄金会员</t-tag>
              <t-tag v-else-if="row.level === 'silver'" theme="warning">白银会员</t-tag>
              <t-tag v-else theme="default">青铜会员</t-tag>
            </template>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'active'" theme="success">正常</t-tag>
              <t-tag v-else-if="row.status === 'frozen'" theme="danger">冻结</t-tag>
              <t-tag v-else theme="default">停用</t-tag>
            </template>
            <template #operation="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewMember(row)">查看</t-link>
                <t-link theme="primary" @click="handleEditMember(row)">编辑</t-link>
                <t-link theme="primary" @click="handleAdjustPoints(row)">调分</t-link>
                <t-link theme="warning" @click="handleFreezeMember(row)" v-if="row.status === 'active'">冻结</t-link>
                <t-link theme="success" @click="handleUnfreezeMember(row)" v-else>解冻</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 积分管理 -->
        <t-tab-panel value="points" label="积分管理">
          <t-row :gutter="16" class="mb-4">
            <t-col :span="6">
              <t-card title="今日新增积分">
                <t-statistic :value="statistics.todayNewPoints" />
              </t-card>
            </t-col>
            <t-col :span="6">
              <t-card title="今日消费积分">
                <t-statistic :value="statistics.todayUsedPoints" />
              </t-card>
            </t-col>
            <t-col :span="6">
              <t-card title="积分发放数">
                <t-statistic :value="statistics.issuedPoints" />
              </t-card>
            </t-col>
            <t-col :span="6">
              <t-card title="积分兑换数">
                <t-statistic :value="statistics.redeemedPoints" />
              </t-card>
            </t-col>
          </t-row>
          <div class="mb-4 flex justify-between items-center">
            <t-button theme="primary" @click="handleIssuePoints">
              <template #icon><icon-add /></template>
              发放积分
            </t-button>
          </div>
          <t-table
            :data="pointsData"
            :columns="pointsColumns"
            :loading="loading"
            :pagination="pointsPagination"
            @page-change="handlePointsPageChange"
          >
            <template #type="{ row }">
              <t-tag v-if="row.type === 'earn'" theme="success" variant="light">获得</t-tag>
              <t-tag v-else theme="danger" variant="light">消费</t-tag>
            </template>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'completed'" theme="success">已完成</t-tag>
              <t-tag v-else-if="row.status === 'pending'" theme="warning">处理中</t-tag>
              <t-tag v-else theme="danger">已取消</t-tag>
            </template>
          </t-table>
        </t-tab-panel>

        <!-- 会员等级 -->
        <t-tab-panel value="levels" label="会员等级">
          <div class="mb-4 flex justify-between items-center">
            <t-button theme="primary" @click="handleAddLevel">
              <template #icon><icon-add /></template>
              新增等级
            </t-button>
          </div>
          <t-row :gutter="16">
            <t-col :span="6" v-for="level in levels" :key="level.code">
              <t-card class="level-card" :class="level.code">
                <div class="level-header">
                  <div class="level-icon">{{ level.icon }}</div>
                  <div class="level-info">
                    <div class="level-name">{{ level.name }}</div>
                    <div class="level-points">{{ level.minPoints }} - {{ level.maxPoints }} 积分</div>
                  </div>
                </div>
                <t-divider />
                <div class="level-benefits">
                  <div>会员数: {{ level.memberCount }}</div>
                  <div>折扣: {{ level.discount }}%</div>
                  <div>积分倍率: {{ level.pointsMultiple }}x</div>
                </div>
                <div class="level-actions">
                  <t-link theme="primary" @click="handleEditLevel(level)">编辑</t-link>
                  <t-link theme="danger" @click="handleDeleteLevel(level)">删除</t-link>
                </div>
              </t-card>
            </t-col>
          </t-row>
        </t-tab-panel>

        <!-- 兑换商品 -->
        <t-tab-panel value="redeem" label="兑换商品">
          <div class="mb-4 flex justify-between items-center">
            <t-button theme="primary" @click="handleAddProduct">
              <template #icon><icon-add /></template>
              新增商品
            </t-button>
            <t-button theme="default" @click="handleBatchOffline">
              <template #icon><icon-poweroff /></template>
              批量下架
            </t-button>
          </div>
          <t-table
            :data="products"
            :columns="productColumns"
            :loading="loading"
            :pagination="productPagination"
            @page-change="handleProductPageChange"
          >
            <template #image="{ row }">
              <t-avatar shape="square" :image="row.image" size="small" />
            </template>
            <template #stock="{ row }">
              <t-tag v-if="row.stock > 100" theme="success">充足</t-tag>
              <t-tag v-else-if="row.stock > 0" theme="warning">紧张</t-tag>
              <t-tag v-else theme="danger">缺货</t-tag>
            </template>
            <template #status="{ row }">
              <t-tag v-if="row.status === 'online'" theme="success">上架</t-tag>
              <t-tag v-else theme="default">下架</t-tag>
            </template>
            <template #operation="{ row }">
              <t-space>
                <t-link theme="primary" @click="handleViewProduct(row)">查看</t-link>
                <t-link theme="primary" @click="handleEditProduct(row)">编辑</t-link>
                <t-link v-if="row.status === 'online'" theme="warning" @click="handleOfflineProduct(row)">下架</t-link>
                <t-link v-else theme="success" @click="handleOnlineProduct(row)">上架</t-link>
              </t-space>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-card>

    <!-- 发放积分弹窗 -->
    <t-dialog
      v-model:visible="pointsDialogVisible"
      header="发放积分"
      width="600px"
      :footer="false"
    >
      <t-form :data="pointsForm" label-width="120px">
        <t-form-item label="发放类型" name="type">
          <t-radio-group v-model="pointsForm.type">
            <t-radio value="all">全部会员</t-radio>
            <t-radio value="level">指定等级</t-radio>
            <t-radio value="member">指定会员</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item v-if="pointsForm.type === 'level'" label="会员等级" name="level">
          <t-select v-model="pointsForm.level" placeholder="请选择会员等级">
            <t-option value="platinum" label="白金会员" />
            <t-option value="gold" label="黄金会员" />
            <t-option value="silver" label="白银会员" />
          </t-select>
        </t-form-item>
        <t-form-item v-if="pointsForm.type === 'member'" label="会员" name="members">
          <t-select v-model="pointsForm.members" placeholder="请选择会员" multiple filterable>
            <t-option value="1" label="张三" />
            <t-option value="2" label="李四" />
          </t-select>
        </t-form-item>
        <t-form-item label="积分数量" name="points">
          <t-input-number v-model="pointsForm.points" :min="1" />
        </t-form-item>
        <t-form-item label="发放原因" name="reason">
          <t-textarea v-model="pointsForm.reason" placeholder="请输入发放原因" :maxlength="200" />
        </t-form-item>
      </t-form>
      <div class="dialog-footer">
        <t-button theme="default" @click="pointsDialogVisible = false">取消</t-button>
        <t-button theme="primary" @click="handleSavePoints">发放</t-button>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUsergroup, IconCheckCircle, IconStar, IconGift,
  IconAdd, IconEdit, IconRefresh, IconPoweroff
} from 'tdesign-icons-vue-next';

const activeTab = ref('members');
const loading = ref(false);
const pointsDialogVisible = ref(false);

const statistics = reactive({
  totalMembers: 5600,
  activeMembers: 4800,
  totalPoints: 5678000,
  redeemedPoints: 1234000,
  todayNewPoints: 5600,
  todayUsedPoints: 2300,
  issuedPoints: 5678000
});

const searchForm = reactive({
  memberCode: '',
  memberName: '',
  level: ''
});

const pointsForm = reactive({
  type: 'all',
  level: '',
  members: [],
  points: 100,
  reason: ''
});

const pagination = reactive({ current: 1, pageSize: 20, total: 5600 });
const pointsPagination = reactive({ current: 1, pageSize: 20, total: 12345 });
const productPagination = reactive({ current: 1, pageSize: 20, total: 50 });

const memberData = ref([
  { id: 1, memberCode: 'MEM20260001', memberName: '张三', level: 'gold', points: 12500, status: 'active', joinDate: '2025-01-15', lastActive: '2026-02-19' },
  { id: 2, memberCode: 'MEM20260002', memberName: '李四', level: 'silver', points: 6800, status: 'active', joinDate: '2025-02-20', lastActive: '2026-02-18' }
]);

const pointsData = ref([
  { id: 1, memberName: '张三', type: 'earn', points: 500, reason: '购物获得', balance: 12500, status: 'completed', createTime: '2026-02-19 10:00:00' },
  { id: 2, memberName: '李四', type: 'consume', points: -200, reason: '兑换商品', balance: 6800, status: 'completed', createTime: '2026-02-19 09:30:00' }
]);

const levels = [
  { code: 'platinum', name: '白金会员', icon: '💎', minPoints: 50000, maxPoints: 999999, memberCount: 156, discount: 20, pointsMultiple: 3 },
  { code: 'gold', name: '黄金会员', icon: '🥇', minPoints: 20000, maxPoints: 49999, memberCount: 520, discount: 15, pointsMultiple: 2 },
  { code: 'silver', name: '白银会员', icon: '🥈', minPoints: 5000, maxPoints: 19999, memberCount: 1680, discount: 10, pointsMultiple: 1.5 },
  { code: 'bronze', name: '青铜会员', icon: '🥉', minPoints: 0, maxPoints: 4999, memberCount: 3244, discount: 5, pointsMultiple: 1 }
];

const products = ref([
  { id: 1, name: '50元优惠券', image: '', points: 5000, stock: 1000, status: 'online', redeemedCount: 234 },
  { id: 2, name: '品牌马克杯', image: '', points: 3000, stock: 50, status: 'online', redeemedCount: 56 }
]);

const memberColumns = [
  { colKey: 'memberCode', title: '会员编号', width: 140 },
  { colKey: 'memberName', title: '会员姓名', width: 100 },
  { colKey: 'level', title: '会员等级', width: 100 },
  { colKey: 'points', title: '积分余额', width: 100 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'joinDate', title: '加入日期', width: 120 },
  { colKey: 'lastActive', title: '最后活跃', width: 120 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const pointsColumns = [
  { colKey: 'memberName', title: '会员姓名', width: 100 },
  { colKey: 'type', title: '类型', width: 80 },
  { colKey: 'points', title: '积分', width: 100 },
  { colKey: 'reason', title: '原因', width: 150 },
  { colKey: 'balance', title: '余额', width: 100 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'createTime', title: '时间', width: 150 }
];

const productColumns = [
  { colKey: 'name', title: '商品名称', width: 150 },
  { colKey: 'image', title: '图片', width: 80 },
  { colKey: 'points', title:所需积分', width: 100 },
  { colKey: 'stock', title: '库存', width: 80 },
  { colKey: 'redeemedCount', title: '已兑换', width: 100 },
  { colKey: 'status', title: '状态', width: 80 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const handleSearch = () => { MessagePlugin.success('查询成功'); };
const handleReset = () => { Object.assign(searchForm, { memberCode: '', memberName: '', level: '' }); };
const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

const handlePageChange = (pageInfo: any) => { pagination.current = pageInfo.current; };
const handlePointsPageChange = (pageInfo: any) => { pointsPagination.current = pageInfo.current; };
const handleProductPageChange = (pageInfo: any) => { productPagination.current = pageInfo.current; };

const handleAddMember = () => MessagePlugin.success('新增会员');
const handleBatchAdjust = () => MessagePlugin.success('批量调分');
const handleViewMember = (row: any) => console.log('View member', row);
const handleEditMember = (row: any) => MessagePlugin.success('编辑会员');
const handleAdjustPoints = (row: any) => MessagePlugin.success('调分');
const handleFreezeMember = (row: any) => MessagePlugin.success('已冻结');
const handleUnfreezeMember = (row: any) => MessagePlugin.success('已解冻');

const handleIssuePoints = () => { pointsDialogVisible.value = true; };
const handleSavePoints = () => { MessagePlugin.success('积分已发放'); pointsDialogVisible.value = false; };

const handleAddLevel = () => MessagePlugin.success('新增等级');
const handleEditLevel = (level: any) => MessagePlugin.success('编辑等级');
const handleDeleteLevel = (level: any) => MessagePlugin.success('删除等级');

const handleAddProduct = () => MessagePlugin.success('新增商品');
const handleBatchOffline = () => MessagePlugin.success('批量下架');
const handleViewProduct = (row: any) => console.log('View product', row);
const handleEditProduct = (row: any) => MessagePlugin.success('编辑商品');
const handleOfflineProduct = (row: any) => MessagePlugin.success('已下架');
const handleOnlineProduct = (row: any) => MessagePlugin.success('已上架');

onMounted(() => { console.log('LoyaltyProgram mounted'); });
</script>

<style scoped lang="less">
.loyalty-program-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.level-card { height: 100%; }
.level-card.platinum { background: linear-gradient(135deg, #e8eaf6 0%, #c5cae9 100%); }
.level-card.gold { background: linear-gradient(135deg, #fff8e1 0%, #ffecb3 100%); }
.level-card.silver { background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%); }
.level-card.bronze { background: linear-gradient(135deg, #efebe9 0%, #d7ccc8 100%); }
.level-header { display: flex; align-items: center; }
.level-icon { font-size: 40px; margin-right: 16px; }
.level-info { flex: 1; }
.level-name { font-size: 18px; font-weight: bold; }
.level-points { font-size: 14px; color: #666; margin-top: 4px; }
.level-benefits { font-size: 14px; color: #666; }
.level-benefits div { margin-bottom: 4px; }
.level-actions { display: flex; justify-content: flex-end; gap: 16px; margin-top: 12px; }
</style>
