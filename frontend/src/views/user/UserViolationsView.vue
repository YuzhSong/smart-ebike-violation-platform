<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { getCurrentUser } from '../../api/user';
import { getUserViolationList } from '../../api/violation';
import PageHeader from '../../components/common/PageHeader.vue';
import ViolationTable from '../../components/common/ViolationTable.vue';
import UserLayout from '../../components/layout/UserLayout.vue';

const router = useRouter();
const rows = ref([]);
const columns = [
  { key: 'id', label: '违法编号' },
  { key: 'title', label: '违法标题' },
  { key: 'type', label: '违法类型' },
  { key: 'time', label: '发生时间' },
  { key: 'location', label: '发生地点' },
  { key: 'status', label: '处理状态' },
  { key: 'punishment', label: '处罚方式' },
  { key: 'actions', label: '操作' },
];

onMounted(async () => {
  const user = await getCurrentUser();
  rows.value = await getUserViolationList(user.id);
});

function handleView(row) {
  router.push(`/user/violation/${row.id}`);
}

function handleAppeal(row) {
  router.push({ path: '/user/appeal/create', query: { violationId: row.id } });
}
</script>

<template>
  <UserLayout>
    <div class="inner-page">
      <PageHeader title="我的违法记录" :links="[{ label: '用户首页', to: '/user' }, { label: '返回首页', to: '/' }]" />
      <section class="panel">
        <ViolationTable :columns="columns" :rows="rows" @view="handleView" @appeal="handleAppeal" />
      </section>
    </div>
  </UserLayout>
</template>
