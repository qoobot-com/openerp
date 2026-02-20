<template>
  <div class="social-media-container">
    <!-- 统计卡片 -->
    <t-row :gutter="16" class="mb-4">
      <t-col :span="6">
        <t-card theme="primary">
          <t-statistic title="总粉丝数" :value="statistics.totalFollowers" :loading="loading">
            <template #prefix><icon-user-group /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="success">
          <t-statistic title="本月新增" :value="statistics.newFollowers" :loading="loading">
            <template #prefix><icon-add-circle /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="warning">
          <t-statistic title="互动总数" :value="statistics.interactions" :loading="loading">
            <template #prefix><icon-chat /></template>
          </t-statistic>
        </t-card>
      </t-col>
      <t-col :span="6">
        <t-card theme="danger">
          <t-statistic title="发布内容" :value="statistics.posts" :loading="loading">
            <template #prefix><icon-file-copy /></template>
          </t-statistic>
        </t-card>
      </t-col>
    </t-row>

    <!-- 平台概览 -->
    <t-card class="mb-4" title="平台概览">
      <t-row :gutter="16">
        <t-col :span="4" v-for="platform in platforms" :key="platform.name">
          <div class="platform-card" :class="platform.type">
            <div class="platform-icon">{{ platform.icon }}</div>
            <div class="platform-info">
              <div class="platform-name">{{ platform.name }}</div>
              <div class="platform-followers">{{ platform.followers }} 粉丝</div>
              <div class="platform-change" :class="platform.change >= 0 ? 'positive' : 'negative'">
                {{ platform.change >= 0 ? '+' : '' }}{{ platform.change }}%
              </div>
            </div>
          </div>
        </t-col>
      </t-row>
    </t-card>

    <!-- 搜索卡片 -->
    <t-card class="mb-4">
      <t-form :data="searchForm" layout="inline" @submit="handleSearch">
        <t-form-item label="内容标题" name="title">
          <t-input v-model="searchForm.title" placeholder="请输入内容标题" clearable />
        </t-form-item>
        <t-form-item label="发布平台" name="platform">
          <t-select v-model="searchForm.platform" placeholder="请选择发布平台" clearable>
            <t-option value="wechat" label="微信公众号" />
            <t-option value="weibo" label="微博" />
            <t-option value="douyin" label="抖音" />
            <t-option value="xiaohongshu" label="小红书" />
            <t-option value="bilibili" label="B站" />
          </t-select>
        </t-form-item>
        <t-form-item label="内容类型" name="type">
          <t-select v-model="searchForm.type" placeholder="请选择内容类型" clearable>
            <t-option value="article" label="文章" />
            <t-option value="video" label="视频" />
            <t-option value="image" label="图文" />
            <t-option value="live" label="直播" />
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
          新建内容
        </t-button>
        <t-button theme="default" @click="handlePublish">
          <template #icon><icon-send /></template>
          批量发布
        </t-button>
      </div>
      <t-button theme="default" variant="outline" @click="handleRefresh">
        <template #icon><icon-refresh /></template>
        刷新
      </t-button>
    </div>

    <!-- 内容列表 -->
    <t-card>
      <t-table
        :data="tableData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @page-change="handlePageChange"
      >
        <template #platform="{ row }">
          <t-tag :theme="getPlatformTheme(row.platform)">{{ getPlatformName(row.platform) }}</t-tag>
        </template>
        <template #type="{ row }">
          <t-tag v-if="row.type === 'article'" theme="primary" variant="light">文章</t-tag>
          <t-tag v-else-if="row.type === 'video'" theme="success" variant="light">视频</t-tag>
          <t-tag v-else-if="row.type === 'image'" theme="warning" variant="light">图文</t-tag>
          <t-tag v-else theme="default" variant="light">直播</t-tag>
        </template>
        <template #status="{ row }">
          <t-tag v-if="row.status === 'draft'" theme="default" variant="light">草稿</t-tag>
          <t-tag v-else-if="row.status === 'scheduled'" theme="warning" variant="light">待发布</t-tag>
          <t-tag v-else-if="row.status === 'published'" theme="success" variant="light">已发布</t-tag>
          <t-tag v-else theme="danger" variant="light">已下架</t-tag>
        </template>
        <template #operation="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleView(row)">查看</t-link>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-link v-if="row.status === 'scheduled'" theme="success" @click="handlePublishNow(row)">立即发布</t-link>
            <t-link v-if="row.status === 'published'" theme="warning" @click="handleUnpublish(row)">下架</t-link>
            <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 新增/编辑内容弹窗 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogTitle"
      width="900px"
      :footer="false"
      @close="handleDialogClose"
    >
      <t-tabs v-model="dialogActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-form :data="formData" ref="formRef" :rules="rules" label-width="120px">
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="内容标题" name="title">
                  <t-input v-model="formData.title" placeholder="请输入内容标题" />
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="发布平台" name="platform">
                  <t-select v-model="formData.platform" placeholder="请选择发布平台">
                    <t-option value="wechat" label="微信公众号" />
                    <t-option value="weibo" label="微博" />
                    <t-option value="douyin" label="抖音" />
                    <t-option value="xiaohongshu" label="小红书" />
                    <t-option value="bilibili" label="B站" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-row :gutter="16">
              <t-col :span="12">
                <t-form-item label="内容类型" name="type">
                  <t-select v-model="formData.type" placeholder="请选择内容类型">
                    <t-option value="article" label="文章" />
                    <t-option value="video" label="视频" />
                    <t-option value="image" label="图文" />
                    <t-option value="live" label="直播" />
                  </t-select>
                </t-form-item>
              </t-col>
              <t-col :span="12">
                <t-form-item label="分类标签" name="tags">
                  <t-select v-model="formData.tags" placeholder="请选择分类标签" multiple>
                    <t-option value="产品介绍" label="产品介绍" />
                    <t-option value="行业资讯" label="行业资讯" />
                    <t-option value="技术分享" label="技术分享" />
                    <t-option value="活动宣传" label="活动宣传" />
                  </t-select>
                </t-form-item>
              </t-col>
            </t-row>
            <t-form-item label="摘要描述" name="summary">
              <t-textarea v-model="formData.summary" placeholder="请输入摘要描述" :maxlength="200" />
            </t-form-item>
            <t-form-item label="封面图片" name="cover">
              <t-upload v-model="formData.cover" theme="image" accept="image/*" :max="1" />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="content" label="内容编辑">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="正文内容" name="content">
              <t-textarea v-model="formData.content" placeholder="请输入正文内容" :maxlength="10000" :autosize="{ minRows: 10 }" />
            </t-form-item>
            <t-form-item label="视频链接" name="videoUrl">
              <t-input v-model="formData.videoUrl" placeholder="请输入视频链接（视频类型）" />
            </t-form-item>
            <t-form-item label="附件" name="attachments">
              <t-upload v-model="formData.attachments" theme="file-input" multiple />
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="schedule" label="发布设置">
          <t-form :data="formData" label-width="120px">
            <t-form-item label="发布方式" name="publishType">
              <t-radio-group v-model="formData.publishType">
                <t-radio value="immediate">立即发布</t-radio>
                <t-radio value="scheduled">定时发布</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item v-if="formData.publishType === 'scheduled'" label="发布时间" name="publishTime">
              <t-date-picker v-model="formData.publishTime" placeholder="请选择发布时间" enable-time-picker />
            </t-form-item>
            <t-form-item label="评论设置" name="allowComment">
              <t-switch v-model="formData.allowComment" label="允许评论" />
            </t-form-item>
            <t-form-item label="互动设置" name="allowInteraction">
              <t-switch v-model="formData.allowInteraction" label="允许互动（点赞、转发）" />
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>
      <div class="dialog-footer">
        <t-button theme="default" @click="dialogVisible = false">取消</t-button>
        <t-button theme="default" @click="handleSaveDraft">保存草稿</t-button>
        <t-button theme="primary" @click="handleSave">{{ formData.publishType === 'immediate' ? '立即发布' : '保存' }}</t-button>
      </div>
    </t-dialog>

    <!-- 内容详情弹窗 -->
    <t-dialog
      v-model:visible="detailVisible"
      header="内容详情"
      width="900px"
      :footer="false"
    >
      <t-tabs v-model="detailActiveTab" theme="card">
        <t-tab-panel value="basic" label="基本信息">
          <t-descriptions :column="2" bordered>
            <t-descriptions-item label="内容标题" :span="2">{{ detailData.title }}</t-descriptions-item>
            <t-descriptions-item label="发布平台">{{ getPlatformName(detailData.platform) }}</t-descriptions-item>
            <t-descriptions-item label="内容类型">{{ getTypeName(detailData.type) }}</t-descriptions-item>
            <t-descriptions-item label="发布状态">
              <t-tag v-if="detailData.status === 'published'" theme="success">已发布</t-tag>
              <t-tag v-else-if="detailData.status === 'scheduled'" theme="warning">待发布</t-tag>
              <t-tag v-else theme="default">草稿</t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="创建时间">{{ detailData.createTime }}</t-descriptions-item>
            <t-descriptions-item label="摘要描述" :span="2">{{ detailData.summary || '-' }}</t-descriptions-item>
          </t-descriptions>
        </t-tab-panel>

        <t-tab-panel value="content" label="内容预览">
          <div class="content-preview">
            <h3>{{ detailData.title }}</h3>
            <div class="preview-content">{{ detailData.content }}</div>
          </div>
        </t-tab-panel>

        <t-tab-panel value="data" label="数据统计">
          <t-row :gutter="16">
            <t-col :span="6">
              <t-statistic title="阅读量" :value="detailData.views || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="点赞数" :value="detailData.likes || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="评论数" :value="detailData.comments || 0" />
            </t-col>
            <t-col :span="6">
              <t-statistic title="转发数" :value="detailData.shares || 0" />
            </t-col>
          </t-row>
          <div class="mt-4">
            <h4>数据趋势</h4>
            <div class="chart-placeholder">
              <t-icon name="chart-line" size="60px" />
              <p>数据趋势图表</p>
            </div>
          </div>
        </t-tab-panel>

        <t-tab-panel value="comments" label="评论管理">
          <t-table :data="detailData.comments || []" :columns="commentColumns" :pagination="false">
            <template #operation="{ row }">
              <t-link theme="primary" @click="handleReplyComment(row)">回复</t-link>
              <t-link theme="danger" @click="handleDeleteComment(row)">删除</t-link>
            </template>
          </t-table>
        </t-tab-panel>
      </t-tabs>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  IconUserGroup, IconAddCircle, IconChat, IconFileCopy,
  IconAdd, IconRefresh, IconSend
} from 'tdesign-icons-vue-next';

const loading = ref(false);
const dialogVisible = ref(false);
const detailVisible = ref(false);
const dialogTitle = ref('新建内容');
const dialogActiveTab = ref('basic');
const detailActiveTab = ref('basic');
const formRef = ref();

const statistics = reactive({
  totalFollowers: 56800,
  newFollowers: 1234,
  interactions: 8900,
  posts: 234
});

const platforms = [
  { name: '微信公众号', type: 'wechat', icon: '🔊', followers: '2.3万', change: 5.2 },
  { name: '微博', type: 'weibo', icon: '🎵', followers: '1.8万', change: -1.2 },
  { name: '抖音', type: 'douyin', icon: '🎬', followers: '1.1万', change: 12.5 },
  { name: '小红书', type: 'xiaohongshu', icon: '📝', followers: '4800', change: 8.3 }
];

const searchForm = reactive({
  title: '',
  platform: '',
  type: ''
});

const formData = reactive({
  id: '',
  title: '',
  platform: '',
  type: '',
  tags: [],
  summary: '',
  cover: [],
  content: '',
  videoUrl: '',
  attachments: [],
  publishType: 'immediate',
  publishTime: '',
  allowComment: true,
  allowInteraction: true
});

const detailData = reactive<any>({});

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 234
});

const columns = [
  { colKey: 'title', title: '内容标题', width: 200, ellipsis: true },
  { colKey: 'platform', title: '发布平台', width: 120 },
  { colKey: 'type', title: '内容类型', width: 80 },
  { colKey: 'status', title: '状态', width: 100 },
  { colKey: 'views', title: '阅读', width: 80 },
  { colKey: 'likes', title: '点赞', width: 80 },
  { colKey: 'comments', title: '评论', width: 80 },
  { colKey: 'shares', title: '转发', width: 80 },
  { colKey: 'createTime', title: '创建时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 200 }
];

const commentColumns = [
  { colKey: 'user', title: '用户', width: 120 },
  { colKey: 'content', title: '评论内容', width: 300, ellipsis: true },
  { colKey: 'likes', title: '点赞', width: 80 },
  { colKey: 'createTime', title: '评论时间', width: 150 },
  { colKey: 'operation', title: '操作', width: 150 }
];

const rules = {
  title: [{ required: true, message: '请输入内容标题', type: 'error' }],
  platform: [{ required: true, message: '请选择发布平台', type: 'error' }],
  type: [{ required: true, message: '请选择内容类型', type: 'error' }],
  content: [{ required: true, message: '请输入正文内容', type: 'error' }]
};

const tableData = ref([
  {
    id: 1,
    title: 'QooERP企业级ERP系统发布',
    platform: 'wechat',
    type: 'article',
    status: 'published',
    views: 3256,
    likes: 189,
    comments: 45,
    shares: 67,
    createTime: '2026-02-19 10:00:00',
    summary: 'QooERP全新发布，助力企业数字化转型',
    content: 'QooERP是一款功能强大的企业级ERP系统...',
    tags: ['产品介绍'],
    publishTime: '2026-02-19 10:00:00'
  }
]);

const handleSearch = () => { MessagePlugin.success('查询成功'); };

const handleReset = () => {
  Object.assign(searchForm, { title: '', platform: '', type: '' });
};

const handleAdd = () => {
  dialogTitle.value = '新建内容';
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑内容';
  dialogVisible.value = true;
  Object.assign(formData, row);
};

const handleView = (row: any) => {
  detailVisible.value = true;
  detailActiveTab.value = 'basic';
  Object.assign(detailData, row);
  detailData.comments = [
    { user: '用户A', content: '非常期待', likes: 12, createTime: '2026-02-19 10:30:00' }
  ];
};

const handlePublish = () => { MessagePlugin.success('批量发布成功'); };
const handlePublishNow = (row: any) => { MessagePlugin.success('立即发布成功'); };
const handleUnpublish = (row: any) => { MessagePlugin.success('已下架'); };
const handleDelete = (row: any) => { MessagePlugin.success('内容已删除'); };
const handleSaveDraft = () => { MessagePlugin.success('草稿已保存'); };

const handleSave = () => {
  MessagePlugin.success(formData.publishType === 'immediate' ? '内容已发布' : '内容已保存');
  dialogVisible.value = false;
};

const handleDialogClose = () => {
  Object.assign(formData, {
    id: '', title: '', platform: '', type: '', tags: [],
    summary: '', cover: [], content: '', videoUrl: '', attachments: [],
    publishType: 'immediate', publishTime: '', allowComment: true, allowInteraction: true
  });
};

const handleRefresh = () => { MessagePlugin.success('数据已刷新'); };

const handlePageChange = (pageInfo: any) => {
  pagination.current = pageInfo.current;
  pagination.pageSize = pageInfo.pageSize;
};

const handleReplyComment = (row: any) => console.log('Reply comment', row);
const handleDeleteComment = (row: any) => { MessagePlugin.success('评论已删除'); };

const getPlatformTheme = (platform: string) => {
  const themes: Record<string, any> = {
    wechat: 'success', weibo: 'danger', douyin: 'primary',
    xiaohongshu: 'warning', bilibili: 'default'
  };
  return themes[platform] || 'default';
};

const getPlatformName = (platform: string) => {
  const names: Record<string, string> = {
    wechat: '微信公众号', weibo: '微博', douyin: '抖音',
    xiaohongshu: '小红书', bilibili: 'B站'
  };
  return names[platform] || platform;
};

const getTypeName = (type: string) => {
  const names: Record<string, string> = {
    article: '文章', video: '视频', image: '图文', live: '直播'
  };
  return names[type] || type;
};

onMounted(() => { console.log('SocialMedia mounted'); });
</script>

<style scoped lang="less">
.social-media-container { padding: 20px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
.flex { display: flex; }
.justify-between { justify-content: space-between; }
.items-center { align-items: center; }
.platform-card { display: flex; align-items: center; padding: 16px; border-radius: 8px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.platform-card.wechat { background: linear-gradient(135deg, #07c160 0%, #059f4d 100%); }
.platform-card.weibo { background: linear-gradient(135deg, #e6162d 0%, #c20c0c 100%); }
.platform-card.douyin { background: linear-gradient(135deg, #000000 0%, #1a1a1a 100%); }
.platform-card.xiaohongshu { background: linear-gradient(135deg, #ff2442 0%, #e60012 100%); }
.platform-icon { font-size: 40px; margin-right: 16px; }
.platform-info { flex: 1; }
.platform-name { font-size: 16px; font-weight: bold; margin-bottom: 8px; }
.platform-followers { font-size: 14px; opacity: 0.9; }
.platform-change { font-size: 12px; margin-top: 4px; }
.platform-change.positive { color: #52c41a; }
.platform-change.negative { color: #ff4d4f; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
.content-preview { padding: 20px; background: #f5f5f5; border-radius: 4px; }
.content-preview h3 { margin-top: 0; }
.preview-content { white-space: pre-wrap; line-height: 1.6; }
.chart-placeholder { text-align: center; padding: 40px; color: #999; }
</style>
