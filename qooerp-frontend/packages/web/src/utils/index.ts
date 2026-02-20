import dayjs from 'dayjs';

/**
 * ==================== 日期处理 ====================
 */

/**
 * 格式化日期
 */
export function formatDate(date: Date | string, format = 'YYYY-MM-DD'): string {
  return dayjs(date).format(format);
}

/**
 * 格式化日期时间
 */
export function formatDateTime(date: Date | string, format = 'YYYY-MM-DD HH:mm:ss'): string {
  return dayjs(date).format(format);
}

/**
 * 获取相对时间
 */
export function formatRelativeTime(date: Date | string): string {
  return dayjs(date).fromNow();
}

/**
 * 获取当前时间戳
 */
export function getTimestamp(): number {
  return Date.now();
}

/**
 * 日期范围格式化
 */
export function formatDateRange(start: Date | string, end: Date | string): string {
  return `${formatDate(start)} ~ ${formatDate(end)}`;
}

/**
 * ==================== 数字处理 ====================
 */

/**
 * 格式化数字（千分位）
 */
export function formatNumber(num: number): string {
  return num.toLocaleString();
}

/**
 * 格式化金额
 */
export function formatMoney(amount: number, currency = '¥', decimals = 2): string {
  return `${currency}${amount.toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`;
}

/**
 * 格式化百分比
 */
export function formatPercent(value: number, decimals = 2): string {
  return `${(value * 100).toFixed(decimals)}%`;
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
}

/**
 * ==================== 字符串处理 ====================
 */

/**
 * 截取字符串
 */
export function truncate(str: string, length = 100, suffix = '...'): string {
  if (str.length <= length) return str;
  return str.slice(0, length) + suffix;
}

/**
 * 生成随机字符串
 */
export function randomString(length = 16): string {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * 生成UUID
 */
export function uuid(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0,
      v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

/**
 * 首字母大写
 */
export function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * 驼峰转短横线
 */
export function camelToKebab(str: string): string {
  return str.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
}

/**
 * 短横线转驼峰
 */
export function kebabToCamel(str: string): string {
  return str.replace(/-([a-z])/g, (_, c) => c.toUpperCase());
}

/**
 * ==================== 数组处理 ====================
 */

/**
 * 数组去重
 */
export function unique<T>(arr: T[]): T[] {
  return Array.from(new Set(arr));
}

/**
 * 数组分组
 */
export function groupBy<T>(arr: T[], key: keyof T): Record<string, T[]> {
  return arr.reduce((result, item) => {
    const groupKey = String(item[key]);
    (result[groupKey] = result[groupKey] || []).push(item);
    return result;
  }, {} as Record<string, T[]>);
}

/**
 * 数组求和
 */
export function sum(arr: number[]): number {
  return arr.reduce((a, b) => a + b, 0);
}

/**
 * 数组求平均值
 */
export function average(arr: number[]): number {
  return sum(arr) / arr.length;
}

/**
 * 数组最大值
 */
export function max(arr: number[]): number {
  return Math.max(...arr);
}

/**
 * 数组最小值
 */
export function min(arr: number[]): number {
  return Math.min(...arr);
}

/**
 * 数组排序
 */
export function sortBy<T>(arr: T[], key: keyof T, order: 'asc' | 'desc' = 'asc'): T[] {
  return [...arr].sort((a, b) => {
    const aVal = a[key];
    const bVal = b[key];
    if (order === 'asc') {
      return aVal > bVal ? 1 : -1;
    } else {
      return aVal < bVal ? 1 : -1;
    }
  });
}

/**
 * 数组分页
 */
export function paginate<T>(arr: T[], page: number, pageSize: number): T[] {
  const start = (page - 1) * pageSize;
  return arr.slice(start, start + pageSize);
}

/**
 * ==================== 对象处理 ====================
 */

/**
 * 深拷贝
 */
export function deepClone<T>(obj: T): T {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj.getTime()) as unknown as T;
  if (obj instanceof Array) return obj.map((item) => deepClone(item)) as unknown as T;
  if (obj instanceof Object) {
    const clonedObj = {} as Record<string, any>;
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key]);
      }
    }
    return clonedObj as T;
  }
  return obj;
}

/**
 * 对象合并
 */
export function deepMerge<T extends object, U extends object>(target: T, source: U): T & U {
  const result = { ...target };
  for (const key in source) {
    if (source.hasOwnProperty(key)) {
      const sourceValue = source[key];
      const targetValue = result[key];
      if (
        typeof sourceValue === 'object' &&
        sourceValue !== null &&
        !Array.isArray(sourceValue)
      ) {
        result[key] = deepMerge(
          targetValue || {},
          sourceValue
        );
      } else {
        result[key] = sourceValue;
      }
    }
  }
  return result as T & U;
}

/**
 * 获取对象路径的值
 */
export function get(obj: any, path: string, defaultValue?: any): any {
  const keys = path.split('.');
  let result = obj;
  for (const key of keys) {
    if (result == null) return defaultValue;
    result = result[key];
  }
  return result !== undefined ? result : defaultValue;
}

/**
 * 设置对象路径的值
 */
export function set(obj: any, path: string, value: any): void {
  const keys = path.split('.');
  let current = obj;
  for (let i = 0; i < keys.length - 1; i++) {
    const key = keys[i];
    if (current[key] === undefined) {
      current[key] = {};
    }
    current = current[key];
  }
  current[keys[keys.length - 1]] = value;
}

/**
 * 移除对象的空值
 */
export function omitEmpty<T extends Record<string, any>>(obj: T): Partial<T> {
  const result: Partial<T> = {};
  for (const key in obj) {
    const value = obj[key];
    if (value !== null && value !== undefined && value !== '') {
      result[key] = value;
    }
  }
  return result;
}

/**
 * 对象转查询参数
 */
export function objectToQuery(obj: Record<string, any>): string {
  const params = new URLSearchParams();
  for (const key in obj) {
    const value = obj[key];
    if (value !== null && value !== undefined) {
      params.append(key, String(value));
    }
  }
  return params.toString();
}

/**
 * 查询参数转对象
 */
export function queryToObject(query: string): Record<string, string> {
  const params = new URLSearchParams(query);
  const result: Record<string, string> = {};
  for (const [key, value] of params.entries()) {
    result[key] = value;
  }
  return result;
}

/**
 * ==================== 函数处理 ====================
 */

/**
 * 防抖
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timer: NodeJS.Timeout | null = null;
  return function (this: any, ...args: Parameters<T>) {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      fn.apply(this, args);
    }, delay);
  };
}

/**
 * 节流
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timer: NodeJS.Timeout | null = null;
  let lastTime = 0;
  return function (this: any, ...args: Parameters<T>) {
    const now = Date.now();
    if (now - lastTime >= delay) {
      lastTime = now;
      fn.apply(this, args);
    } else {
      if (timer) clearTimeout(timer);
      timer = setTimeout(() => {
        lastTime = now;
        fn.apply(this, args);
      }, delay - (now - lastTime));
    }
  };
}

/**
 * 函数只执行一次
 */
export function once<T extends (...args: any[]) => any>(fn: T): T {
  let called = false;
  let result: ReturnType<T>;
  return function (this: any, ...args: Parameters<T>): ReturnType<T> {
    if (!called) {
      called = true;
      result = fn.apply(this, args);
    }
    return result;
  } as T;
}

/**
 * ==================== 类型判断 ====================
 */

/**
 * 判断是否为空
 */
export function isEmpty(value: any): boolean {
  if (value === null || value === undefined) return true;
  if (typeof value === 'string') return value.trim() === '';
  if (Array.isArray(value)) return value.length === 0;
  if (typeof value === 'object') return Object.keys(value).length === 0;
  return false;
}

/**
 * 判断是否为数组
 */
export function isArray(value: any): value is any[] {
  return Array.isArray(value);
}

/**
 * 判断是否为对象
 */
export function isObject(value: any): value is Record<string, any> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

/**
 * 判断是否为函数
 */
export function isFunction(value: any): value is Function {
  return typeof value === 'function';
}

/**
 * 判断是否为数字
 */
export function isNumber(value: any): value is number {
  return typeof value === 'number' && !isNaN(value);
}

/**
 * 判断是否为字符串
 */
export function isString(value: any): value is string {
  return typeof value === 'string';
}

/**
 * 判断是否为布尔值
 */
export function isBoolean(value: any): value is boolean {
  return typeof value === 'boolean';
}

/**
 * 判断是否为日期
 */
export function isDate(value: any): value is Date {
  return value instanceof Date;
}

/**
 * 判断是否为邮箱
 */
export function isEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

/**
 * 判断是否为手机号
 */
export function isPhone(value: string): boolean {
  return /^1[3-9]\d{9}$/.test(value);
}

/**
 * 判断是否为URL
 */
export function isUrl(value: string): boolean {
  try {
    new URL(value);
    return true;
  } catch {
    return false;
  }
}

/**
 * ==================== 本地存储 ====================
 */

/**
 * 设置本地存储
 */
export function setStorage(key: string, value: any): void {
  try {
    const serialized = JSON.stringify(value);
    localStorage.setItem(key, serialized);
  } catch (e) {
    console.error('Error setting storage:', e);
  }
}

/**
 * 获取本地存储
 */
export function getStorage<T = any>(key: string, defaultValue?: T): T | undefined {
  try {
    const serialized = localStorage.getItem(key);
    if (serialized === null) return defaultValue;
    return JSON.parse(serialized);
  } catch (e) {
    console.error('Error getting storage:', e);
    return defaultValue;
  }
}

/**
 * 移除本地存储
 */
export function removeStorage(key: string): void {
  try {
    localStorage.removeItem(key);
  } catch (e) {
    console.error('Error removing storage:', e);
  }
}

/**
 * 清空本地存储
 */
export function clearStorage(): void {
  try {
    localStorage.clear();
  } catch (e) {
    console.error('Error clearing storage:', e);
  }
}

/**
 * 设置会话存储
 */
export function setSession(key: string, value: any): void {
  try {
    const serialized = JSON.stringify(value);
    sessionStorage.setItem(key, serialized);
  } catch (e) {
    console.error('Error setting session:', e);
  }
}

/**
 * 获取会话存储
 */
export function getSession<T = any>(key: string, defaultValue?: T): T | undefined {
  try {
    const serialized = sessionStorage.getItem(key);
    if (serialized === null) return defaultValue;
    return JSON.parse(serialized);
  } catch (e) {
    console.error('Error getting session:', e);
    return defaultValue;
  }
}

/**
 * 移除会话存储
 */
export function removeSession(key: string): void {
  try {
    sessionStorage.removeItem(key);
  } catch (e) {
    console.error('Error removing session:', e);
  }
}

/**
 * ==================== 树形数据处理 ====================
 */

export interface TreeNode {
  id: string | number;
  label?: string;
  children?: TreeNode[];
  [key: string]: any;
}

/**
 * 树形转扁平数组
 */
export function treeToList<T extends TreeNode>(tree: T[]): T[] {
  const result: T[] = [];
  const traverse = (nodes: T[]) => {
    nodes.forEach((node) => {
      result.push(node);
      if (node.children && node.children.length > 0) {
        traverse(node.children);
      }
    });
  };
  traverse(tree);
  return result;
}

/**
 * 扁平数组转树形
 */
export function listToTree<T extends TreeNode>(
  list: T[],
  options: { idKey?: string; parentIdKey?: string; parentValue?: any } = {}
): T[] {
  const { idKey = 'id', parentIdKey = 'parentId', parentValue = null } = options;
  const map = new Map<string | number, T>();
  const result: T[] = [];

  // 先创建映射
  list.forEach((item) => {
    map.set(item[idKey], { ...item, children: [] });
  });

  // 构建树
  list.forEach((item) => {
    const node = map.get(item[idKey]);
    const parentId = item[parentIdKey];
    if (parentId === parentValue || parentId === undefined || parentId === null) {
      result.push(node!);
    } else {
      const parent = map.get(parentId);
      if (parent) {
        parent.children!.push(node!);
      }
    }
  });

  return result;
}

/**
 * 查找树节点
 */
export function findTreeNode<T extends TreeNode>(
  tree: T[],
  id: string | number,
  options: { idKey?: string; childrenKey?: string } = {}
): T | undefined {
  const { idKey = 'id', childrenKey = 'children' } = options;
  for (const node of tree) {
    if (node[idKey] === id) return node;
    if (node[childrenKey] && node[childrenKey].length > 0) {
      const found = findTreeNode(node[childrenKey], id, options);
      if (found) return found;
    }
  }
  return undefined;
}

/**
 * 树形节点过滤
 */
export function filterTreeNode<T extends TreeNode>(
  tree: T[],
  predicate: (node: T) => boolean,
  options: { childrenKey?: string } = {}
): T[] {
  const { childrenKey = 'children' } = options;
  const result: T[] = [];

  for (const node of tree) {
    const newNode = { ...node };
    if (node[childrenKey] && node[childrenKey].length > 0) {
      const filteredChildren = filterTreeNode(node[childrenKey], predicate, options);
      newNode[childrenKey] = filteredChildren as any;
    }

    if (predicate(newNode) || (newNode[childrenKey] && newNode[childrenKey].length > 0)) {
      result.push(newNode);
    }
  }

  return result;
}

/**
 * 获取树的所有路径
 */
export function getTreePaths<T extends TreeNode>(
  tree: T[],
  options: { idKey?: string; labelKey?: string; childrenKey?: string } = {}
): string[] {
  const { idKey = 'id', labelKey = 'label', childrenKey = 'children' } = options;
  const paths: string[] = [];

  const traverse = (nodes: T[], path: string[] = []) => {
    nodes.forEach((node) => {
      const currentPath = [...path, node[labelKey] || String(node[idKey])];
      paths.push(currentPath.join(' > '));
      if (node[childrenKey] && node[childrenKey].length > 0) {
        traverse(node[childrenKey], currentPath);
      }
    });
  };

  traverse(tree);
  return paths;
}
