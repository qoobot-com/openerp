# QooERP Frontend Architecture

## 设计理念

参考大型开源 ERP 系统（Odoo、ERPNext、Metabase）的最佳实践，结合现代前端技术栈，构建企业级统一前端工程。

## 核心特性

### 1. Monorepo 架构
- 使用 pnpm workspace 管理多包依赖
- 代码复用：shared 包提供共享组件、工具、类型
- 独立构建：每个应用可独立构建和部署
- 统一依赖：统一版本管理，减少依赖冲突

### 2. 多端适配
- **Web**: 主应用，完整功能
- **Desktop**: Electron 桌面应用，离线支持
- **Mobile**: Capacitor 移动应用，原生能力

### 3. 分层设计
```
┌─────────────────────────────────────┐
│         视图层 (Views)                │  页面组件，数据展示与交互
├─────────────────────────────────────┤
│       组件层 (Components)             │  可复用 UI 组件
│   - layout (布局)                     │
│   - common (通用)                      │
│   - business (业务)                    │
├─────────────────────────────────────┤
│    状态管理层 (Pinia Stores)          │  全局状态管理
├─────────────────────────────────────┤
│    逻辑层 (Composables/Hooks)         │  业务逻辑封装
├─────────────────────────────────────┤
│       API 层 (API Services)           │  HTTP 请求封装
└─────────────────────────────────────┘
```

### 4. 技术栈选择理由

| 技术 | 选择理由 |
|------|----------|
| Vue 3 | 渐进式框架，生态完善，性能优异 |
| TypeScript | 类型安全，提升开发效率 |
| TDesign | 腾讯企业级设计语言，适合 ERP 场景 |
| Vite | 快速开发体验，HMR 性能优秀 |
| Pinia | Vue 官方状态管理，轻量高效 |
| Electron | 桌面应用成熟方案 |
| Capacitor | 现代化跨平台移动方案 |
| OpenTelemetry | 标准化监控与链路追踪 |

### 5. 参考的开源系统

| 系统 | 借鉴点 |
|------|--------|
| **Odoo** | 模块化架构、插件系统、多租户 |
| **ERPNext** | 框架化设计、工作流、报表 |
| **Metabase** | 数据可视化、BI 分析 |
| **Camunda** | 工作流引擎、BPMN |
| **Apache Superset** | 仪表盘、数据洞察 |
| **Grafana** | 监控面板、告警 |

## 目录结构说明

### packages/web
Web 端主应用，包含完整的业务功能。

**关键目录**:
- `views/`: 按业务模块组织的页面
- `components/`: 组件库
- `router/`: 路由配置
- `stores/`: Pinia 状态管理
- `api/`: API 接口封装
- `hooks/`: 组合式函数
- `utils/`: 工具函数
- `plugins/`: 插件配置

### packages/desktop
Electron 桌面应用。

**关键目录**:
- `main/`: 主进程代码
- `preload/`: 预加载脚本

### packages/mobile
Capacitor 移动应用。

**关键目录**:
- `src/`: Vue 代码
- `android/`: Android 原生代码
- `ios/`: iOS 原生代码

### shared
共享代码，供所有应用使用。

**关键目录**:
- `components/`: 共享组件
- `composables/`: 共享组合式函数
- `utils/`: 共享工具函数
- `types/`: 共享类型定义
- `constants/`: 共享常量

## 开发规范

### 命名规范
- 组件文件: `PascalCase.vue` (如 `UserProfile.vue`)
- 组合式函数: `use` 前缀 (如 `useAuth.ts`)
- 工具函数: `camelCase` (如 `formatDate.ts`)
- 常量: `UPPER_SNAKE_CASE` (如 `API_BASE_URL`)
- 类型: `PascalCase` (如 `User`)

### Git 提交规范
```
feat: 新功能
fix: 修复 Bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
perf: 性能优化
test: 测试相关
chore: 构建/工具相关
```

## 性能优化

### 1. 代码分割
- 路由懒加载
- 组件懒加载
- Vite 手动 chunk 分割

### 2. 缓存策略
- HTTP 缓存
- LocalStorage 缓存
- Pinia 持久化

### 3. 资源优化
- 图片压缩
- 字体子集化
- Tree Shaking

## 监控与日志

### OpenTelemetry 集成
- 自动插桩：Fetch、XHR
- 手动 Span：用户行为追踪
- 性能指标：FP、FCP、LCP

### 错误监控
- Vue 错误捕获
- 全局错误监听
- 未处理 Promise 拒绝

## 安全实践

### 1. XSS 防护
- Vue 自动转义
- CSP 策略
- 输入验证

### 2. CSRF 防护
- Token 验证
- SameSite Cookie
- Referer 检查

### 3. 敏感数据
- 不在前端存储密码
- 使用 HTTPS
- Token 自动刷新

## 部署方案

### Web 端部署
```bash
pnpm build
# 将 dist 目录部署到 Nginx/Cos/OSS
```

### Desktop 打包
```bash
pnpm build:electron
# 输出到 release 目录
```

### Mobile 打包
```bash
pnpm build:android  # Android
pnpm build:ios      # iOS
```

## 未来规划

1. **低代码平台**: 可视化表单/页面设计器
2. **AI 助手**: 智能客服、数据分析
3. **实时协作**: WebSocket 多人协作
4. **离线支持**: PWA、Service Worker
5. **国际化**: 多语言支持
6. **主题定制**: 用户自定义主题

## 相关文档

- [开发指南](./docs/development.md)
- [部署文档](./docs/deployment.md)
- [API 文档](./docs/api.md)
- [组件文档](./docs/components.md)
