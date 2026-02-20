import { createApp } from 'vue';
import { createPinia } from 'pinia';
import router from './router';
import i18n from './i18n';
import App from './App.vue';
import './assets/styles/index.scss';
import './assets/styles/dark.scss';
import { setupDirectives } from './directives';

// 添加全局错误处理
window.addEventListener('error', (event) => {
  console.error('全局错误:', event.error);
});

window.addEventListener('unhandledrejection', (event) => {
  console.error('未处理的 Promise 拒绝:', event.reason);
});

console.log('正在初始化应用...');

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(i18n);

setupDirectives(app);

app.mount('#app');

console.log('应用已挂载到 #app');
