# 主页空白问题 - 调试步骤

## 已完成的修复

1. ✅ 添加了 `@types/nprogress` 类型定义
2. ✅ 修复了 `tsconfig.json` 缺少 DOM 库的问题
3. ✅ 创建了登录页面 (`/views/login/Login.vue`)
4. ✅ 创建了 403 错误页面 (`/views/error/403.vue`)
5. ✅ 添加了调试日志到路由守卫和 store
6. ✅ 添加了全局错误处理
7. ✅ 修复了 linter 警告

## 启动步骤

### 1. 安装依赖
```bash
cd d:/05workspaces/qooerp/qooerp-frontend
pnpm install
```

### 2. 启动开发服务器
```bash
cd packages/web
pnpm run dev
```

### 3. 访问应用
- 打开浏览器访问: `http://localhost:3000`

## 测试流程

### 方案 1: 先登录 (推荐)
1. 访问 `http://localhost:3000/login`
2. 输入任意用户名和密码
3. 点击登录按钮
4. 系统会自动跳转到主页

### 方案 2: 直接设置 Token
1. 打开浏览器开发者工具 (F12)
2. 在 Console 中输入:
```javascript
localStorage.setItem('token', 'test-token')
localStorage.setItem('permissions', '["*"]')
```
3. 刷新页面

## 常见问题

### Q: 页面仍然空白
**A**: 检查浏览器控制台:
1. 按 F12 打开开发者工具
2. 查看 Console 标签页的错误信息
3. 查看 Network 标签页的请求状态

### Q: 显示 "403 无权限"
**A**: 这说明路由守卫正常工作,但缺少权限:
1. 清除 LocalStorage
2. 重新登录
3. 或者在 Console 设置权限:
```javascript
localStorage.setItem('permissions', '["*"]')
```

### Q: 显示 "登录页面"
**A**: 这说明需要先登录:
1. 使用方案 1 进行登录
2. 或者在 Console 设置 Token

### Q: API 请求失败
**A**: 后端服务未启动:
1. 启动后端服务 (默认端口 8080)
2. 或修改 `vite.config.ts` 的代理配置

## 调试信息

应用启动时会输出以下日志:
- "正在初始化应用..."
- "应用已挂载到 #app"
- "初始化应用..."
- "恢复主题: ..."
- "恢复语言: ..."
- "应用初始化完成"

路由跳转时会输出:
- "路由跳转: /path ..."

## 下一步

如果以上步骤都无法解决问题,请:
1. 截图控制台的错误信息
2. 截图 Network 请求失败记录
3. 提供 LocalStorage 的内容
