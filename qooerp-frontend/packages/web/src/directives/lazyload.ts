import type { Directive, DirectiveBinding } from 'vue';

/**
 * 懒加载指令
 * 使用: v-lazyload="imageUrl"
 */
export const lazyload: Directive = {
  mounted(el: HTMLImageElement, binding: DirectiveBinding<string>) {
    const imageUrl = binding.value;
    const options = {
      rootMargin: '0px',
      threshold: 0.1,
    };

    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          el.src = imageUrl;
          observer.unobserve(el);
        }
      });
    }, options);

    observer.observe(el);

    // 存储观察器以便清理
    el._lazyloadObserver = observer;
  },
  updated(el: HTMLImageElement, binding: DirectiveBinding<string>) {
    // 如果图片地址变化，重新观察
    if (el._lazyloadObserver) {
      el._lazyloadObserver.disconnect();
    }
    const imageUrl = binding.value;
    const options = {
      rootMargin: '0px',
      threshold: 0.1,
    };
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          el.src = imageUrl;
          observer.unobserve(el);
        }
      });
    }, options);
    observer.observe(el);
    el._lazyloadObserver = observer;
  },
  unmounted(el: HTMLImageElement) {
    if (el._lazyloadObserver) {
      el._lazyloadObserver.disconnect();
    }
  },
};
