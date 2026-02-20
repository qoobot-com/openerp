import type { Directive, DirectiveBinding } from 'vue';

/**
 * 复制指令
 * 使用: v-copy="text"
 */
export const copy: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string>) {
    el.addEventListener('click', async () => {
      try {
        const text = binding.value;
        await navigator.clipboard.writeText(text);
        // 可以添加提示信息
        console.log('复制成功:', text);
      } catch (err) {
        console.error('复制失败:', err);
      }
    });
  },
};
