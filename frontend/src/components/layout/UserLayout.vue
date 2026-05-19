<script setup>
const menuItems = [
  { label: '个人中心', to: '/user' },
  { label: '我的违法记录', to: '/user/violations' },
  { label: '处罚说明', to: '/user/penalty' },
  { label: '申诉服务', to: '/user/appeal' },
  { label: '返回首页', to: '/' },
];
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
    <main class="user-content">
      <slot />
    </main>
  </div>
</template>
