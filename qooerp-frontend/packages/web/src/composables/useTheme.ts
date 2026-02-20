import { ref, watch } from 'vue';
import { useAppStore } from '@/stores/modules/app';

export type Theme = 'light' | 'dark';

/**
 * 主题切换 Hook
 */
export function useTheme() {
  const appStore = useAppStore();
  const theme = ref<Theme>(appStore.theme);

  // 监听主题变化
  watch(
    () => appStore.theme,
    (newTheme) => {
      theme.value = newTheme;
      applyTheme(newTheme);
    },
    { immediate: true }
  );

  /**
   * 应用主题
   */
  const applyTheme = (theme: Theme) => {
    const html = document.documentElement;
    if (theme === 'dark') {
      html.setAttribute('data-theme', 'dark');
    } else {
      html.removeAttribute('data-theme');
    }
  };

  /**
   * 切换主题
   */
  const toggleTheme = () => {
    const newTheme: Theme = theme.value === 'light' ? 'dark' : 'light';
    appStore.setTheme(newTheme);
  };

  /**
   * 设置主题
   */
  const setTheme = (newTheme: Theme) => {
    appStore.setTheme(newTheme);
  };

  return {
    theme,
    toggleTheme,
    setTheme,
  };
}
