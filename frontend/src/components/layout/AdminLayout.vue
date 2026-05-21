<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';

const now = ref(new Date());
let timer = null;

const menuItems = [
  { label: '数据总览', to: '/admin' },
  { label: '违法事件管理', to: '/admin/violations' },
  { label: '设备管理', to: '/admin/devices' },
  { label: '用户信息管理', to: '/admin/users' },
  { label: '统计分析', to: '/admin/statistics' },
  { label: '系统设置', to: '/admin/settings' },
];

const timeText = computed(() => {
  const date = now.value;
  const p = (n) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${p(date.getMonth() + 1)}-${p(date.getDate())} ${p(date.getHours())}:${p(date.getMinutes())}:${p(date.getSeconds())}`;
});

onMounted(() => {
  timer = setInterval(() => {
    now.value = new Date();
  }, 1000);
});

onBeforeUnmount(() => clearInterval(timer));

function logout() {
  window.alert('已退出登录（前端演示）');
}
</script>

<template>
  <div class="admin-layout">
    <aside class="admin-aside">
      <h1 class="admin-brand">非机动车智能违法监管与服务平台</h1>
      <nav class="admin-menu">
        <router-link v-for="item in menuItems" :key="item.to" :to="item.to" class="admin-menu-item">
          {{ item.label }}
        </router-link>
      </nav>
    </aside>
    <div class="admin-main-shell">
      <header class="admin-topbar">
        <strong>监管后台</strong>
        <div class="admin-topbar-right">
          <span>当前身份：管理员</span>
          <span>{{ timeText }}</span>
          <router-link class="btn-secondary" to="/">返回首页</router-link>
          <button class="btn-secondary" @click="logout">退出登录</button>
        </div>
      </header>
      <main class="admin-content">
        <slot />
      </main>
    </div>
  </div>
</template>
