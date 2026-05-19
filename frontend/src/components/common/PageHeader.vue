<script setup>
defineProps({
  title: { type: String, required: true },
  links: { type: Array, default: () => [] },
});
</script>

<template>
  <header class="inner-header">
    <h2>{{ title }}</h2>
    <nav>
      <router-link v-for="item in links" :key="item.to" :to="item.to">{{ item.label }}</router-link>
    </nav>
  </header>
</template>
