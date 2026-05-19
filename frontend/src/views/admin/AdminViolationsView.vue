<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { getAdminViolationList } from '../../api/violation';
import PageHeader from '../../components/common/PageHeader.vue';
import ViolationTable from '../../components/common/ViolationTable.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const router = useRouter();
const rows = ref([]);
const columns = [
  { key: 'id', label: '事件编号' },
  { key: 'type', label: '违法类型' },
  { key: 'location', label: '发生地点' },
  { key: 'time', label: '发生时间' },
  { key: 'confidenceText', label: '识别置信度' },
  { key: 'status', label: '处理状态' },
  { key: 'userName', label: '关联用户' },
  { key: 'deviceId', label: '设备编号' },
  { key: 'actions', label: '操作' },
];

onMounted(async () => {
  const list = await getAdminViolationList();
  rows.value = list.map((item) => ({ ...item, confidenceText: `${(item.confidence * 100).toFixed(1)}%` }));
});

function handleView(row) {
  // 管理端查看详情应进入管理端详情页
  router.push(`/admin/violations/${row.id}`);
}
function handleApprove(row) {
  window.alert(`事件 ${row.id} 已标记为审核通过（前端演示）`);
}
function handleMisreport(row) {
  window.alert(`事件 ${row.id} 已标记为误报（前端演示）`);
}
function handleChangePunishment(row) {
  window.alert(`事件 ${row.id} 进入处罚调整流程（前端演示）`);
}
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="违法事件管理" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel">
        <ViolationTable
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
