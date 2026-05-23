<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { getAdminViolationList, updateViolationStatus } from '../../api/violation';
import PageHeader from '../../components/common/PageHeader.vue';
import ViolationTable from '../../components/common/ViolationTable.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const router = useRouter();
const rows = ref([]);
const loading = ref(false);
const columns = [
  { key: 'id', label: '事件编号' },
  { key: 'type', label: '违法类型' },
  { key: 'location', label: '发生地点' },
  { key: 'time', label: '发生时间' },
  { key: 'confidenceText', label: '识别置信度' },
  { key: 'status', label: '处理状态' },
  { key: 'userName', label: '关联用户' },
  { key: 'deviceCode', label: '设备编号' },
  { key: 'actions', label: '操作' },
];

async function loadRows() {
  loading.value = true;
  const list = await getAdminViolationList();
  rows.value = list.map((item) => ({
    ...item,
    confidenceText: item.confidence == null ? '-' : `${(item.confidence * 100).toFixed(1)}%`,
  }));
  loading.value = false;
}

onMounted(loadRows);

function handleView(row) {
  router.push(`/admin/violations/${row.id}`);
}

async function handleApprove(row) {
  await updateViolationStatus(row.id, 'CONFIRMED', '审核通过');
  await loadRows();
}

async function handleMisreport(row) {
  await updateViolationStatus(row.id, 'REJECTED', '标记误报');
  await loadRows();
}

function handleChangePunishment(row) {
  router.push(`/admin/violations/${row.id}`);
}
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="违法事件管理" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel">
        <p v-if="loading" class="muted-text">正在加载违法事件...</p>
        <ViolationTable
          v-else
          mode="admin"
          :columns="columns"
          :rows="rows"
          @view="handleView"
          @approve="handleApprove"
          @misreport="handleMisreport"
          @change-punishment="handleChangePunishment"
        />
      </section>
    </div>
  </AdminLayout>
</template>
