<script setup>
import StatusTag from './StatusTag.vue';

defineProps({
  columns: { type: Array, required: true },
  rows: { type: Array, required: true },
  rowKey: { type: String, default: 'id' },
  mode: { type: String, default: 'user' },
});

const emit = defineEmits(['view', 'appeal', 'approve', 'misreport', 'change-punishment']);
</script>

<template>
  <table class="data-table">
    <thead>
      <tr>
        <th v-for="col in columns" :key="col.key">{{ col.label }}</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="row in rows" :key="row[rowKey]">
        <td v-for="col in columns" :key="col.key">
          <template v-if="col.key === 'status'">
            <StatusTag :status="row[col.key]" />
          </template>
          <template v-else-if="col.key === 'actions'">
            <div class="table-actions">
              <button class="btn-link" @click="emit('view', row)">查看详情</button>
              <button v-if="mode === 'user'" class="btn-link" @click="emit('appeal', row)">发起申诉</button>
              <template v-if="mode === 'admin'">
                <button class="btn-link" @click="emit('approve', row)">审核通过</button>
                <button class="btn-link" @click="emit('misreport', row)">标记误报</button>
                <button class="btn-link" @click="emit('change-punishment', row)">修改处罚</button>
              </template>
            </div>
          </template>
          <template v-else>
            {{ row[col.key] }}
          </template>
        </td>
      </tr>
    </tbody>
  </table>
</template>
