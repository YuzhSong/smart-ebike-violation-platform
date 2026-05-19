<script setup>
import { computed } from 'vue';

const props = defineProps({
  status: { type: String, required: true },
});

const className = computed(() => {
  const map = {
    未处理: 'status-unprocessed',
    已处理: 'status-processed',
    申诉中: 'status-appealing',
    已驳回: 'status-rejected',
    在线: 'status-online',
    离线: 'status-offline',
    维护中: 'status-maintenance',
    正常: 'status-processed',
    冻结: 'status-rejected',
  };
  return map[props.status] || 'status-default';
});
</script>

<template>
  <span class="status-tag" :class="className">{{ status }}</span>
</template>
