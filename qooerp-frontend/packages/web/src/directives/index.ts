import type { App } from 'vue';
import { copy } from './copy';
import { longpress } from './longpress';
import { debounce } from './debounce';
import { lazyload } from './lazyload';
import { clickoutside } from './clickoutside';

/**
 * 注册所有自定义指令
 */
export const setupDirectives = (app: App) => {
  app.directive('copy', copy);
  app.directive('longpress', longpress);
  app.directive('debounce', debounce);
  app.directive('lazyload', lazyload);
  app.directive('clickoutside', clickoutside);
};

// 导出单个指令以供单独使用
export { copy, longpress, debounce, lazyload, clickoutside };
