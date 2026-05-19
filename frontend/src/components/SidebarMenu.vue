<script setup>
defineProps({
  title: { type: String, default: '' },
  items: { type: Array, default: () => [] },
});
</script>

<template>
  <aside class="sidebar">
    <h3>{{ title }}</h3>
    <a v-for="item in items" :key="item.text" :href="item.to" class="side-item">{{ item.text }}</a>
  </aside>
</template>
