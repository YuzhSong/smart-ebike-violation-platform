<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  subText: { type: String, default: '' },
});
</script>

<template>
  <article class="data-card">
    <p class="label">{{ label }}</p>
    <p class="value">{{ value }}</p>
    <p v-if="subText" class="sub-value">{{ subText }}</p>
  </article>
</template>
