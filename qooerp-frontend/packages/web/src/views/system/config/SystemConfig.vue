<template>
  <div class="system-config-page">
    <t-card>
      <t-tabs v-model="activeTab" @change="handleTabChange">
        <t-tab-panel value="basic" label="基本配置">
          <t-form ref="basicForm" :data="basicConfig" :rules="basicRules" label-align="right" label-width="150px">
            <t-form-item label="系统名称" name="systemName">
              <t-input v-model="basicConfig.systemName" placeholder="请输入系统名称" />
            </t-form-item>
            <t-form-item label="系统简称" name="systemShortName">
              <t-input v-model="basicConfig.systemShortName" placeholder="请输入系统简称" />
            </t-form-item>
            <t-form-item label="系统Logo" name="systemLogo">
              <t-input v-model="basicConfig.systemLogo" placeholder="请输入系统Logo URL" />
            </t-form-item>
            <t-form-item label="系统描述" name="systemDescription">
              <t-textarea v-model="basicConfig.systemDescription" placeholder="请输入系统描述" :maxlength="500" />
            </t-form-item>
            <t-form-item label="公司名称" name="companyName">
              <t-input v-model="basicConfig.companyName" placeholder="请输入公司名称" />
            </t-form-item>
            <t-form-item label="公司地址" name="companyAddress">
              <t-input v-model="basicConfig.companyAddress" placeholder="请输入公司地址" />
            </t-form-item>
            <t-form-item label="联系电话" name="companyPhone">
              <t-input v-model="basicConfig.companyPhone" placeholder="请输入联系电话" />
            </t-form-item>
            <t-form-item label="联系邮箱" name="companyEmail">
              <t-input v-model="basicConfig.companyEmail" placeholder="请输入联系邮箱" />
            </t-form-item>
            <t-form-item label="备案号" name="icpNumber">
              <t-input v-model="basicConfig.icpNumber" placeholder="请输入备案号" />
            </t-form-item>
            <t-form-item>
              <t-space>
                <t-button theme="primary" :loading="saving" @click="handleSaveBasic">保存配置</t-button>
                <t-button theme="default" @click="handleResetBasic">重置</t-button>
              </t-space>
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="security" label="安全配置">
          <t-form ref="securityForm" :data="securityConfig" label-align="right" label-width="150px">
            <t-form-item label="密码最小长度" name="passwordMinLength">
              <t-input-number v-model="securityConfig.passwordMinLength" :min="6" :max="20" />
            </t-form-item>
            <t-form-item label="密码最大长度" name="passwordMaxLength">
              <t-input-number v-model="securityConfig.passwordMaxLength" :min="6" :max="50" />
            </t-form-item>
            <t-form-item label="密码复杂度" name="passwordComplexity">
              <t-checkbox-group v-model="securityConfig.passwordComplexity">
                <t-checkbox value="uppercase">包含大写字母</t-checkbox>
                <t-checkbox value="lowercase">包含小写字母</t-checkbox>
                <t-checkbox value="number">包含数字</t-checkbox>
                <t-checkbox value="special">包含特殊字符</t-checkbox>
              </t-checkbox-group>
            </t-form-item>
            <t-form-item label="密码有效期" name="passwordExpireDays">
              <t-input-number v-model="securityConfig.passwordExpireDays" :min="0" :max="365" />
              <span style="margin-left: 8px; color: var(--td-text-color-secondary);">0表示永不过期</span>
            </t-form-item>
            <t-form-item label="登录失败锁定" name="loginLockCount">
              <t-input-number v-model="securityConfig.loginLockCount" :min="0" :max="10" />
              <span style="margin-left: 8px; color: var(--td-text-color-secondary);">0表示不锁定</span>
            </t-form-item>
            <t-form-item label="锁定时间(分钟)" name="loginLockMinutes">
              <t-input-number v-model="securityConfig.loginLockMinutes" :min="0" :max="1440" />
            </t-form-item>
            <t-form-item label="Session超时(分钟)" name="sessionTimeout">
              <t-input-number v-model="securityConfig.sessionTimeout" :min="5" :max="1440" />
            </t-form-item>
            <t-form-item label="验证码启用" name="captchaEnabled">
              <t-switch v-model="securityConfig.captchaEnabled" />
            </t-form-item>
            <t-form-item label="验证码类型" name="captchaType">
              <t-radio-group v-model="securityConfig.captchaType">
                <t-radio value="image">图片验证码</t-radio>
                <t-radio value="slide">滑块验证码</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item>
              <t-space>
                <t-button theme="primary" :loading="saving" @click="handleSaveSecurity">保存配置</t-button>
                <t-button theme="default" @click="handleResetSecurity">重置</t-button>
              </t-space>
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="upload" label="上传配置">
          <t-form ref="uploadForm" :data="uploadConfig" label-align="right" label-width="150px">
            <t-form-item label="存储方式" name="storageType">
              <t-radio-group v-model="uploadConfig.storageType">
                <t-radio value="local">本地存储</t-radio>
                <t-radio value="oss">阿里云OSS</t-radio>
                <t-radio value="cos">腾讯云COS</t-radio>
                <t-radio value="s3">AWS S3</t-radio>
              </t-radio-group>
            </t-form-item>
            <t-form-item label="单文件大小限制(MB)" name="maxFileSize">
              <t-input-number v-model="uploadConfig.maxFileSize" :min="1" :max="500" />
            </t-form-item>
            <t-form-item label="允许上传格式" name="allowedExtensions">
              <t-input v-model="uploadConfig.allowedExtensions" placeholder="例如: jpg,jpeg,png,pdf" />
            </t-form-item>
            <t-form-item label="图片压缩" name="imageCompress">
              <t-switch v-model="uploadConfig.imageCompress" />
            </t-form-item>
            <t-form-item label="压缩质量" name="compressQuality">
              <t-slider v-model="uploadConfig.compressQuality" :min="10" :max="100" :marks="{ 10: '10%', 50: '50%', 100: '100%' }" />
            </t-form-item>
            <t-form-item label="上传路径" name="uploadPath">
              <t-input v-model="uploadConfig.uploadPath" placeholder="请输入上传路径" />
            </t-form-item>
            <t-form-item label="访问路径前缀" name="accessUrlPrefix">
              <t-input v-model="uploadConfig.accessUrlPrefix" placeholder="请输入访问路径前缀" />
            </t-form-item>
            <t-form-item>
              <t-space>
                <t-button theme="primary" :loading="saving" @click="handleSaveUpload">保存配置</t-button>
                <t-button theme="default" @click="handleResetUpload">重置</t-button>
              </t-space>
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="email" label="邮件配置">
          <t-form ref="emailForm" :data="emailConfig" label-align="right" label-width="150px">
            <t-form-item label="SMTP服务器" name="smtpHost">
              <t-input v-model="emailConfig.smtpHost" placeholder="请输入SMTP服务器地址" />
            </t-form-item>
            <t-form-item label="SMTP端口" name="smtpPort">
              <t-input-number v-model="emailConfig.smtpPort" :min="1" :max="65535" />
            </t-form-item>
            <t-form-item label="发送者邮箱" name="fromEmail">
              <t-input v-model="emailConfig.fromEmail" placeholder="请输入发送者邮箱" />
            </t-form-item>
            <t-form-item label="发送者名称" name="fromName">
              <t-input v-model="emailConfig.fromName" placeholder="请输入发送者名称" />
            </t-form-item>
            <t-form-item label="用户名" name="username">
              <t-input v-model="emailConfig.username" placeholder="请输入用户名" />
            </t-form-item>
            <t-form-item label="密码" name="password">
              <t-input v-model="emailConfig.password" type="password" placeholder="请输入密码" />
            </t-form-item>
            <t-form-item label="使用SSL" name="useSsl">
              <t-switch v-model="emailConfig.useSsl" />
            </t-form-item>
            <t-form-item label="使用TLS" name="useTls">
              <t-switch v-model="emailConfig.useTls" />
            </t-form-item>
            <t-form-item>
              <t-space>
                <t-button theme="primary" :loading="saving" @click="handleSaveEmail">保存配置</t-button>
                <t-button theme="default" @click="handleResetEmail">重置</t-button>
                <t-button @click="handleTestEmail">测试发送</t-button>
              </t-space>
            </t-form-item>
          </t-form>
        </t-tab-panel>

        <t-tab-panel value="sms" label="短信配置">
          <t-form ref="smsForm" :data="smsConfig" label-align="right" label-width="150px">
            <t-form-item label="服务商" name="provider">
              <t-select v-model="smsConfig.provider" placeholder="请选择服务商">
                <t-option value="aliyun" label="阿里云" />
                <t-option value="tencent" label="腾讯云" />
                <t-option value="qiniu" label="七牛云" />
              </t-select>
            </t-form-item>
            <t-form-item label="Access Key" name="accessKey">
              <t-input v-model="smsConfig.accessKey" placeholder="请输入Access Key" />
            </t-form-item>
            <t-form-item label="Secret Key" name="secretKey">
              <t-input v-model="smsConfig.secretKey" type="password" placeholder="请输入Secret Key" />
            </t-form-item>
            <t-form-item label="签名" name="signName">
              <t-input v-model="smsConfig.signName" placeholder="请输入短信签名" />
            </t-form-item>
            <t-form-item label="验证码模板" name="verifyTemplate">
              <t-input v-model="smsConfig.verifyTemplate" placeholder="请输入验证码模板ID" />
            </t-form-item>
            <t-form-item label="通知模板" name="noticeTemplate">
              <t-input v-model="smsConfig.noticeTemplate" placeholder="请输入通知模板ID" />
            </t-form-item>
            <t-form-item>
              <t-space>
                <t-button theme="primary" :loading="saving" @click="handleSaveSms">保存配置</t-button>
                <t-button theme="default" @click="handleResetSms">重置</t-button>
                <t-button @click="handleTestSms">测试发送</t-button>
              </t-space>
            </t-form-item>
          </t-form>
        </t-tab-panel>
      </t-tabs>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';

const activeTab = ref('basic');
const saving = ref(false);

// 基本配置
const basicConfig = reactive({
  systemName: 'QooERP企业管理系统',
  systemShortName: 'QooERP',
  systemLogo: '/logo.png',
  systemDescription: 'QooERP是一套功能完善的企业资源计划管理系统',
  companyName: 'QooBot科技有限公司',
  companyAddress: '北京市海淀区中关村软件园',
  companyPhone: '400-888-8888',
  companyEmail: 'contact@qoobot.com',
  icpNumber: '京ICP备12345678号',
});

const basicRules = {
  systemName: [{ required: true, message: '请输入系统名称', type: 'error' }],
  systemShortName: [{ required: true, message: '请输入系统简称', type: 'error' }],
  companyName: [{ required: true, message: '请输入公司名称', type: 'error' }],
};

// 安全配置
const securityConfig = reactive({
  passwordMinLength: 8,
  passwordMaxLength: 20,
  passwordComplexity: ['lowercase', 'number'],
  passwordExpireDays: 90,
  loginLockCount: 5,
  loginLockMinutes: 30,
  sessionTimeout: 30,
  captchaEnabled: true,
  captchaType: 'image',
});

// 上传配置
const uploadConfig = reactive({
  storageType: 'local',
  maxFileSize: 10,
  allowedExtensions: 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx',
  imageCompress: true,
  compressQuality: 80,
  uploadPath: '/uploads',
  accessUrlPrefix: '/static',
});

// 邮件配置
const emailConfig = reactive({
  smtpHost: 'smtp.example.com',
  smtpPort: 587,
  fromEmail: 'noreply@example.com',
  fromName: 'QooERP',
  username: '',
  password: '',
  useSsl: false,
  useTls: true,
});

// 短信配置
const smsConfig = reactive({
  provider: 'aliyun',
  accessKey: '',
  secretKey: '',
  signName: 'QooERP',
  verifyTemplate: '',
  noticeTemplate: '',
});

// 保存配置
const handleSaveBasic = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('基本配置保存成功');
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const handleSaveSecurity = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('安全配置保存成功');
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const handleSaveUpload = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('上传配置保存成功');
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const handleSaveEmail = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('邮件配置保存成功');
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const handleSaveSms = async () => {
  saving.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    MessagePlugin.success('短信配置保存成功');
  } catch (error) {
    MessagePlugin.error('保存失败');
  } finally {
    saving.value = false;
  }
};

// 测试功能
const handleTestEmail = async () => {
  MessagePlugin.success('测试邮件已发送，请查收');
};

const handleTestSms = async () => {
  MessagePlugin.success('测试短信已发送，请查收');
};

// 重置配置
const handleResetBasic = () => {
  MessagePlugin.info('已重置为上次保存的配置');
};

const handleResetSecurity = () => {
  MessagePlugin.info('已重置为上次保存的配置');
};

const handleResetUpload = () => {
  MessagePlugin.info('已重置为上次保存的配置');
};

const handleResetEmail = () => {
  MessagePlugin.info('已重置为上次保存的配置');
};

const handleResetSms = () => {
  MessagePlugin.info('已重置为上次保存的配置');
};

const handleTabChange = (value: string) => {
  console.log('Tab changed:', value);
};
</script>

<style lang="scss" scoped>
.system-config-page {
  :deep(.t-form__item) {
    margin-bottom: 24px;
  }
}
</style>
