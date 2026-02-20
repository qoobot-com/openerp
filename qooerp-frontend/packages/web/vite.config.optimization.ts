/**
 * Vite 性能优化配置
 * 这个文件包含各种性能优化的配置选项
 */

import type { PluginOption } from 'vite';

/**
 * 构建优化配置
 */
export const buildOptimizationConfig = {
  // 构建输出目录
  outDir: 'dist',
  // 静态资源目录
  assetsDir: 'assets',
  // 小于此阈值 (kb) 的资源将内联为 base64
  assetsInlineLimit: 4096,
  // CSS 代码分割
  cssCodeSplit: true,
  // 源码映射
  sourcemap: false,
  // 设置最终构建的浏览器兼容目标
  target: 'es2015',
  // 启用 CSS 压缩
  minify: 'terser',
  // terser 压缩选项
  terserOptions: {
    compress: {
      drop_console: true, // 生产环境移除 console
      drop_debugger: true, // 生产环境移除 debugger
      pure_funcs: ['console.log', 'console.info'], // 移除指定的函数
    },
    format: {
      comments: false, // 移除注释
    },
  },
  // chunk 大小警告限制 (kb)
  chunkSizeWarningLimit: 1000,
  // Rollup 打包配置
  rollupOptions: {
    output: {
      // 分包策略
      manualChunks: {
        // Vue 核心库
        'vue-vendor': ['vue', 'vue-router', 'pinia'],
        // UI 组件库
        'tdesign-vue-next': ['tdesign-vue-next', 'tdesign-icons-vue-next'],
        // 工具库
        'utils': ['axios', 'dayjs', 'lodash-es', '@vueuse/core'],
        // 图表库
        'echarts': ['echarts'],
      },
      // 静态资源文件名
      chunkFileNames: 'assets/js/[name]-[hash].js',
      entryFileNames: 'assets/js/[name]-[hash].js',
      assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
    },
  },
};

/**
 * 代码分割配置
 */
export const createCodeSplitPlugin = (): PluginOption => {
  return {
    name: 'code-split-plugin',
    enforce: 'post',
    config: (config) => {
      config.build = config.build || {};
      config.build.rollupOptions = config.build.rollupOptions || {};
      config.build.rollupOptions.output = config.build.rollupOptions.output || {};
      config.build.rollupOptions.output.manualChunks = {
        ...config.build.rollupOptions.output.manualChunks,
        'vendor-vue': ['vue', 'vue-router', 'pinia'],
        'vendor-ui': ['tdesign-vue-next'],
        'vendor-utils': ['axios', 'dayjs', 'lodash-es'],
      };
    },
  };
};

/**
 * Gzip 压缩插件配置
 */
export const compressionConfig = {
  verbose: true,
  disable: false,
  threshold: 10240, // 大于 10kb 的文件才压缩
  algorithm: 'gzip',
  ext: '.gz',
};

/**
 * 图片压缩配置
 */
export const imageOptimizationConfig = {
  // 需要安装 vite-plugin-imagemin
  gifsicle: { optimizationLevel: 7 },
  optipng: { optimizationLevel: 7 },
  mozjpeg: { quality: 80 },
  pngquant: { quality: [0.8, 0.9], speed: 4 },
  svgo: {
    plugins: [
      { name: 'removeViewBox', active: false },
      { name: 'removeEmptyAttrs', active: false },
    ],
  },
};

/**
 * 预构建配置
 */
export const optimizeDepsConfig = {
  include: [
    'vue',
    'vue-router',
    'pinia',
    'axios',
    'tdesign-vue-next',
    'dayjs',
    'lodash-es',
    '@vueuse/core',
    'echarts',
  ],
  exclude: [],
};

/**
 * CDN 外链配置
 * 注意: 使用 CDN 需要确保网络可访问性
 */
export const cdnConfig = {
  // 是否启用 CDN
  enable: false,
  // CDN 基础 URL
  baseUrl: 'https://cdn.jsdelivr.net/npm',
  // CDN 依赖映射
  modules: {
    vue: 'vue@3.4.15/dist/vue.global.prod.js',
    'vue-router': 'vue-router@4.2.5/dist/vue-router.global.prod.js',
    pinia: 'pinia@2.1.7/dist/pinia.iife.prod.js',
    axios: 'axios@1.6.5/dist/axios.min.js',
    dayjs: 'dayjs@1.11.10/dayjs.min.js',
    'lodash-es': 'lodash-es@4.17.21/lodash.min.js',
    echarts: 'echarts@5.4.3/dist/echarts.min.js',
  },
};

/**
 * 路由懒加载配置
 */
export const createLazyLoadRoute = (path: string) => {
  return () => import(/* @vite-ignore */ `@/views/${path}.vue`);
};

/**
 * KeepAlive 缓存配置
 */
export const keepAliveConfig = {
  // 需要缓存的组件名称列表
  include: [] as string[],
  // 不需要缓存的组件名称列表
  exclude: [] as string[],
  // 最大缓存数
  max: 10,
};

/**
 * 虚拟滚动配置
 */
export const virtualScrollConfig = {
  // 每项高度
  itemSize: 40,
  // 缓冲区数量
  buffer: 10,
  // 是否动态高度
  dynamic: false,
};

/**
 * 性能监控配置
 */
export const performanceConfig = {
  // 是否启用性能监控
  enable: true,
  // 采样率 (0-1)
  sampleRate: 0.1,
  // 最大上报条数
  maxReportCount: 50,
  // 上报间隔 (ms)
  reportInterval: 5000,
};

/**
 * 资源预加载配置
 */
export const preloadConfig = {
  // 需要预加载的资源
  resources: [
    { type: 'script', href: '/assets/js/main.js' },
    { type: 'style', href: '/assets/css/main.css' },
  ],
};

/**
 * 资源预连接配置
 */
export const preconnectConfig = {
  // 需要预连接的域名
  domains: [
    'https://cdn.jsdelivr.net',
    'https://api.example.com',
  ],
};

/**
 * 缓存策略配置
 */
export const cacheConfig = {
  // 静态资源缓存时间 (秒)
  staticCacheTime: 31536000, // 1 年
  // HTML 缓存时间 (秒)
  htmlCacheTime: 3600, // 1 小时
  // API 缓存时间 (秒)
  apiCacheTime: 300, // 5 分钟
};

/**
 * PWA 配置
 */
export const pwaConfig = {
  // 是否启用 PWA
  enable: false,
  // Service Worker 文件名
  filename: 'sw.js',
  // 缓存策略
  strategies: 'NetworkFirst',
  // 预缓存资源
  preCacheAssets: [],
  // 运行时缓存
  runtimeCaching: [],
};

/**
 * 默认导出所有配置
 */
export default {
  buildOptimization: buildOptimizationConfig,
  codeSplit: createCodeSplitPlugin,
  compression: compressionConfig,
  imageOptimization: imageOptimizationConfig,
  optimizeDeps: optimizeDepsConfig,
  cdn: cdnConfig,
  lazyLoadRoute: createLazyLoadRoute,
  keepAlive: keepAliveConfig,
  virtualScroll: virtualScrollConfig,
  performance: performanceConfig,
  preload: preloadConfig,
  preconnect: preconnectConfig,
  cache: cacheConfig,
  pwa: pwaConfig,
};
