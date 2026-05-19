<script setup>
import { computed, onMounted, ref } from 'vue';
import { getAdminDashboardStats } from '../../api/statistics';
import PageHeader from '../../components/common/PageHeader.vue';
import DataCard from '../../components/common/DataCard.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const stats = ref(null);
const trendMax = computed(() => Math.max(...(stats.value?.trend || []).map((item) => item.count), 1));

onMounted(async () => {
  stats.value = await getAdminDashboardStats();
});
</script>

<template>
  <AdminLayout>
    <div class="inner-page" v-if="stats">
      <PageHeader title="管理员控制台" :links="[{ label: '进入统计分析', to: '/admin/statistics' }]" />
      <section class="card-grid">
        <DataCard label="今日违法事件数" :value="stats.todayViolations" />
        <DataCard label="待审核事件数" :value="stats.pendingReview" />
        <DataCard label="设备在线率" :value="`${stats.deviceOnlineRate}%`" />
        <DataCard label="已处理事件数" :value="stats.processedCount" />
      </section>
      <section class="panel-row">
        <section class="panel">
          <h3>高发违法类型排行</h3>
          <ul>
            <li v-for="item in stats.typeRanking" :key="item.name"><span>{{ item.name }}</span><strong>{{ item.count }}</strong></li>
          </ul>
        </section>
        <section class="panel">
          <h3>高发地点排行</h3>
          <ul>
            <li v-for="item in stats.locationRanking" :key="item.name"><span>{{ item.name }}</span><strong>{{ item.count }}</strong></li>
          </ul>
        </section>
      </section>
      <section class="panel">
        <h3>近期违法趋势</h3>
        <div class="trend-chart">
          <div v-for="item in stats.trend" :key="item.day" class="trend-col">
            <div class="trend-bar" :style="{ height: `${(item.count / trendMax) * 140}px` }"></div>
            <div class="trend-day">{{ item.day }}</div>
          </div>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>
