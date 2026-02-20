import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/workflow',
    redirect: '/workflow/process',
  },
  {
    path: '/workflow/process',
    name: 'WorkflowProcess',
    component: () => import('@/views/workflow/process/ProcessList.vue'),
  },
];

export default routes;
