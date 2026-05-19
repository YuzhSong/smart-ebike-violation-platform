<script setup>
defineProps({
  imageUrl: { type: String, required: true },
});
</script>

<template>
  <div class="evidence-card">
    <div class="evidence-head">现场抓拍证据</div>
    <img :src="imageUrl" alt="违法证据图片" class="evidence-image" />
  </div>
</template>
