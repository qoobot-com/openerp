import type { Directive, DirectiveBinding } from 'vue';

/**
 * 长按指令
 * 使用: v-longpress:500="handleLongPress"
 */
export const longpress: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    if (typeof binding.value !== 'function') {
      console.warn('[v-longpress] 需要传入函数');
      return;
    }

    let timer: NodeJS.Timeout | null = null;
    let startTime = 0;
    const duration = Number(binding.arg) || 500;

    const start = () => {
      startTime = Date.now();
      timer = setTimeout(() => {
        binding.value();
        timer = null;
      }, duration);
    };

    const cancel = () => {
      if (timer) {
        clearTimeout(timer);
        timer = null;
      }
      // 如果按下时间小于 duration，不触发长按
      if (Date.now() - startTime < duration) {
        // 可以在这里添加点击事件的处理
      }
    };

    el.addEventListener('mousedown', start);
    el.addEventListener('touchstart', start);
    el.addEventListener('mouseup', cancel);
    el.addEventListener('mouseleave', cancel);
    el.addEventListener('touchend', cancel);
    el.addEventListener('touchcancel', cancel);

    // 存储清理函数
    el._longpressCleanup = () => {
      el.removeEventListener('mousedown', start);
      el.removeEventListener('touchstart', start);
      el.removeEventListener('mouseup', cancel);
      el.removeEventListener('mouseleave', cancel);
      el.removeEventListener('touchend', cancel);
      el.removeEventListener('touchcancel', cancel);
    };
  },
  unmounted(el: HTMLElement) {
    if (el._longpressCleanup) {
      el._longpressCleanup();
    }
  },
};
