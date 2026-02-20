import type { Directive, DirectiveBinding } from 'vue';

/**
 * 点击外部指令
 * 使用: v-clickoutside="handleClickOutside"
 */
export const clickoutside: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    if (typeof binding.value !== 'function') {
      console.warn('[v-clickoutside] 需要传入函数');
      return;
    }

    const handleClickOutside = (event: MouseEvent) => {
      if (!el.contains(event.target as Node) && el !== event.target) {
        binding.value(event);
      }
    };

    document.addEventListener('click', handleClickOutside);

    // 存储清理函数
    el._clickoutsideCleanup = () => {
      document.removeEventListener('click', handleClickOutside);
    };
  },
  unmounted(el: HTMLElement) {
    if (el._clickoutsideCleanup) {
      el._clickoutsideCleanup();
    }
  },
};
