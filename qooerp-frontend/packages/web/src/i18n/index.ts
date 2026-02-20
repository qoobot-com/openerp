import { createI18n } from 'vue-i18n';
import zhCN from './locales/zh-CN';
import enUS from './locales/en-US';
import jaJP from './locales/ja-JP';

export type Language = 'zh-CN' | 'en-US' | 'ja-JP';

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS,
  'ja-JP': jaJP,
};

// 获取默认语言
const getDefaultLocale = (): Language => {
  const stored = localStorage.getItem('locale') as Language;
  if (stored && messages[stored]) {
    return stored;
  }
  const browserLang = navigator.language as Language;
  if (messages[browserLang]) {
    return browserLang;
  }
  return 'zh-CN';
};

const i18n = createI18n({
  legacy: false,
  locale: getDefaultLocale(),
  fallbackLocale: 'zh-CN',
  messages,
});

export default i18n;

/**
 * 设置语言
 */
export const setLocale = (locale: Language) => {
  i18n.global.locale.value = locale;
  localStorage.setItem('locale', locale);
};

/**
 * 获取当前语言
 */
export const getCurrentLocale = () => {
  return i18n.global.locale.value as Language;
};

/**
 * 获取支持的语言列表
 */
export const getSupportedLocales = () => {
  return Object.keys(messages) as Language[];
};
