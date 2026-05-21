<script setup>
import { onMounted, ref } from 'vue';
import { getCurrentUser } from '../../api/user';
import { getUserViolationList, getUserViolationOverview } from '../../api/violation';
import PageHeader from '../../components/common/PageHeader.vue';
import DataCard from '../../components/common/DataCard.vue';
import StatusTag from '../../components/common/StatusTag.vue';
import UserLayout from '../../components/layout/UserLayout.vue';

const user = ref({});
const overview = ref({ unprocessed: 0, processed: 0, appealing: 0, latestViolationTime: '-' });
const latestList = ref([]);

onMounted(async () => {
  user.value = await getCurrentUser();
  overview.value = await getUserViolationOverview(user.value.id);
  latestList.value = (await getUserViolationList(user.value.id)).slice(0, 5);
});
</script>

<template>
  <UserLayout>
    <div class="inner-page">
      <PageHeader title="用户服务中心" :links="[]" />
      <section class="panel">
        <h3>欢迎您，{{ user.name || '-' }}</h3>
        <p class="muted-text">非机动车违法查询与处理服务</p>
        <div class="card-grid">
          <DataCard label="未处理记录数" :value="overview.unprocessed" />
          <DataCard label="已处理记录数" :value="overview.processed" />
          <DataCard label="申诉中记录数" :value="overview.appealing" />
          <DataCard label="最近一次违法时间" :value="overview.latestViolationTime" />
        </div>
      </section>
      <section class="panel">
        <h3>最近违法记录</h3>
        <table class="data-table">
          <thead><tr><th>违法编号</th><th>违法标题</th><th>违法类型</th><th>发生时间</th><th>处理状态</th></tr></thead>
          <tbody>
            <tr v-for="item in latestList" :key="item.id">
              <td>{{ item.id }}</td><td>{{ item.title }}</td><td>{{ item.type }}</td><td>{{ item.time }}</td><td><StatusTag :status="item.status" /></td>
            </tr>
          </tbody>
        </table>
      </section>
    </div>
  </UserLayout>
</template>
