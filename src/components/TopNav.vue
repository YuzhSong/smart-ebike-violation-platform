<script setup>
defineProps({
  items: { type: Array, default: () => [] },
});
</script>

<template>
  <header class="top-nav">
    <div class="brand">非机动车智能违法监管与服务平台</div>
    <nav class="top-nav-links">
      <a v-for="item in items" :key="item.text" :href="item.to">{{ item.text }}</a>
    </nav>
  </header>
</template>
