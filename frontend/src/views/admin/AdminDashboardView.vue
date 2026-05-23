<script setup>
import { computed, onMounted, ref } from 'vue';
import { getAdminDashboardStats, getViolationTrend12m } from '../../api/statistics';
import PageHeader from '../../components/common/PageHeader.vue';
import DataCard from '../../components/common/DataCard.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const stats = ref(null);
const monthTrendList = ref([]);
const trendMax = computed(() => Math.max(...(stats.value?.trend || []).map((item) => item.count), 1));
const monthTrendMax = computed(() => Math.max(...monthTrendList.value.map((item) => item.count), 1));

onMounted(async () => {
  const [dashboardStats, trend12m] = await Promise.all([
    getAdminDashboardStats(),
    getViolationTrend12m(),
  ]);
  stats.value = dashboardStats;
  monthTrendList.value = trend12m;
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
      <section class="panel-row stats-top-equal">
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
      <section class="panel trend-dual-panel">
        <section class="trend-half">
          <h3>近7天违法数量趋势</h3>
          <div class="trend-chart">
            <div v-for="item in stats.trend" :key="item.day" class="trend-col">
              <div class="trend-bar" :style="{ height: `${(item.count / trendMax) * 140}px` }"></div>
              <div class="trend-day">{{ item.day }}</div>
            </div>
          </div>
        </section>
        <section class="trend-half trend-half-split">
          <h3>近12个月违法数量趋势</h3>
          <div class="trend-chart trend-chart-month">
            <div v-for="item in monthTrendList" :key="item.month" class="trend-col">
              <div class="trend-bar trend-bar-month" :style="{ height: `${(item.count / monthTrendMax) * 140}px` }"></div>
              <div class="trend-day">{{ item.month }}</div>
            </div>
          </div>
        </section>
      </section>
    </div>
  </AdminLayout>
</template>
