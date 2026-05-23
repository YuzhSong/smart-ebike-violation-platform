<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { clearAuthSession } from '../../api/auth';
import { getCurrentUser } from '../../api/user';

const router = useRouter();
const menuItems = [
  { label: '个人中心', to: '/user' },
  { label: '我的违法记录', to: '/user/violations' },
  { label: '处罚说明', to: '/user/penalty' },
  { label: '申诉服务', to: '/user/appeal' },
];

const currentUser = ref({ name: '用户' });

onMounted(async () => {
  const user = await getCurrentUser();
  currentUser.value = user || { name: '用户' };
});

function logout() {
  clearAuthSession();
  router.push('/login?role=user');
}
</script>

<template>
  <div class="user-layout">
    <aside class="user-aside">
      <h2>政务服务</h2>
      <nav class="user-menu">
        <router-link v-for="item in menuItems" :key="item.label + item.to" :to="item.to" class="user-menu-item">
          {{ item.label }}
        </router-link>
      </nav>
    </aside>
    <div class="user-main-shell">
      <header class="user-topbar">
        <strong>用户服务端</strong>
        <div class="user-topbar-right">
          <span>当前用户：{{ currentUser.name || '用户' }}</span>
          <router-link class="btn-secondary" to="/">返回首页</router-link>
          <button class="btn-secondary" @click="logout">退出登录</button>
        </div>
      </header>
      <main class="user-content">
        <slot />
      </main>
    </div>
  </div>
</template>
