<script setup>
import { computed } from 'vue';

const props = defineProps({
  status: { type: String, required: true },
});

const label = computed(() => {
  const map = {
    PENDING: '待处理',
    REVIEWING: '审核中',
    CONFIRMED: '已确认',
    REJECTED: '已驳回',
    ONLINE: '在线',
    OFFLINE: '离线',
    MAINTENANCE: '维护中',
    NORMAL: '正常',
    FROZEN: '冻结',
    ACTIVE: '正常',
    DISABLED: '停用',
  };
  return map[props.status] || props.status;
});

const className = computed(() => {
  const map = {
    PENDING: 'status-unprocessed',
    REVIEWING: 'status-appealing',
    CONFIRMED: 'status-processed',
    REJECTED: 'status-rejected',
    ONLINE: 'status-online',
    OFFLINE: 'status-offline',
    MAINTENANCE: 'status-maintenance',
    NORMAL: 'status-processed',
    FROZEN: 'status-rejected',
    ACTIVE: 'status-processed',
    DISABLED: 'status-rejected',
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
  <span class="status-tag" :class="className">{{ label }}</span>
</template>
