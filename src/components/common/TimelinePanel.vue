<script setup>
defineProps({
  list: { type: Array, default: () => [] },
});
</script>

<template>
  <div class="timeline-panel">
    <div v-for="(item, index) in list" :key="`${item.step}-${index}`" class="timeline-item">
      <div class="timeline-dot"></div>
      <div>
        <p class="timeline-title">{{ item.step }}</p>
        <p class="timeline-time">{{ item.time }}</p>
        <p class="timeline-desc">{{ item.desc }}</p>
      </div>
    </div>
  </div>
</template>
