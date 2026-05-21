import { createRouter, createWebHashHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import UserHomeView from '../views/user/UserHomeView.vue';
import UserViolationsView from '../views/user/UserViolationsView.vue';
import UserViolationDetailView from '../views/user/UserViolationDetailView.vue';
import UserPenaltyView from '../views/user/UserPenaltyView.vue';
import UserAppealView from '../views/user/UserAppealView.vue';
import UserAppealCreateView from '../views/user/UserAppealCreateView.vue';
import AdminDashboardView from '../views/admin/AdminDashboardView.vue';
import AdminViolationsView from '../views/admin/AdminViolationsView.vue';
import AdminViolationDetail from '../views/admin/AdminViolationDetail.vue';
import AdminDevicesView from '../views/admin/AdminDevicesView.vue';
import AdminUsersView from '../views/admin/AdminUsersView.vue';
import AdminStatisticsView from '../views/admin/AdminStatisticsView.vue';
import AdminSettingsView from '../views/admin/AdminSettingsView.vue';

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: LoginView },
  { path: '/user', component: UserHomeView },
  { path: '/user/violations', component: UserViolationsView },
  { path: '/user/violation/:id', component: UserViolationDetailView },
  { path: '/user/penalty', component: UserPenaltyView },
  { path: '/user/appeal', component: UserAppealView },
  { path: '/user/appeal/create', component: UserAppealCreateView },
  { path: '/admin', component: AdminDashboardView },
  { path: '/admin/violations', component: AdminViolationsView },
  { path: '/admin/violations/:id', component: AdminViolationDetail },
  { path: '/admin/devices', component: AdminDevicesView },
  { path: '/admin/users', component: AdminUsersView },
  { path: '/admin/statistics', component: AdminStatisticsView },
  { path: '/admin/settings', component: AdminSettingsView },
];

export default createRouter({
  history: createWebHashHistory(),
  routes,
});
