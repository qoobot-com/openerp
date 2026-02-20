import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/organization',
    redirect: '/organization/employee',
  },
  {
    path: '/organization/employee',
    name: 'OrganizationEmployee',
    component: () => import('@/views/organization/employee/EmployeeList.vue'),
  },
];

export default routes;
