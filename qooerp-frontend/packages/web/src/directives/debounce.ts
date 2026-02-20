import type { Directive, DirectiveBinding } from 'vue';

/**
 * 防抖指令
 * 使用: v-debounce:500="handleClick"
 */
export const debounce: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    if (typeof binding.value !== 'function') {
      console.warn('[v-debounce] 需要传入函数');
      return;
    }

    let timer: NodeJS.Timeout | null = null;
    const delay = Number(binding.arg) || 300;

    const handler = (...args: any[]) => {
      if (timer) clearTimeout(timer);
      timer = setTimeout(() => {
        binding.value(...args);
      }, delay);
    };

    el.addEventListener('click', handler);

    // 存储清理函数
    el._debounceCleanup = () => {
      if (timer) clearTimeout(timer);
      el.removeEventListener('click', handler);
    };
  },
  unmounted(el: HTMLElement) {
    if (el._debounceCleanup) {
      el._debounceCleanup();
    }
  },
};
