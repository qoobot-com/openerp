import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/system',
    redirect: '/system/user',
  },
  {
    path: '/system/user',
    name: 'SystemUser',
    component: () => import('@/views/system/user/UserList.vue'),
  },
];

export default routes;
