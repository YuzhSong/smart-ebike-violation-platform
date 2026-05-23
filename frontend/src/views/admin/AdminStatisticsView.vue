<script setup>
import { computed, onMounted, ref } from 'vue';
import {
  getLocationRanking,
  getProcessStatusStats,
  getViolationTrend7d,
  getViolationTrend12m,
  getViolationTypeDistribution,
} from '../../api/statistics';
import PageHeader from '../../components/common/PageHeader.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const typeList = ref([]);
const trendList = ref([]);
const monthTrendList = ref([]);
const locationList = ref([]);
const processList = ref([]);

const maxTrend = computed(() => Math.max(...trendList.value.map((i) => i.count), 1));
const maxMonthTrend = computed(() => Math.max(...monthTrendList.value.map((i) => i.count), 1));

onMounted(async () => {
  const [types, trend7d, trend12m, locations, processes] = await Promise.all([
    getViolationTypeDistribution(),
    getViolationTrend7d(),
    getViolationTrend12m(),
    getLocationRanking(),
    getProcessStatusStats(),
  ]);
  typeList.value = types;
  trendList.value = trend7d;
  monthTrendList.value = trend12m;
  locationList.value = locations;
  processList.value = processes;
});
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="统计分析" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel-row stats-top-equal">
        <section class="panel">
          <h3>违法类型分布</h3>
          <ul>
            <li v-for="item in typeList" :key="item.name">
              <span>{{ item.name }}</span><strong>{{ item.count }}</strong>
            </li>
          </ul>
        </section>
        <section class="panel">
          <h3>处理状态统计</h3>
          <ul>
            <li v-for="item in processList" :key="item.name">
              <span>{{ item.name }}</span><strong>{{ item.value }}</strong>
            </li>
          </ul>
        </section>
      </section>
      <section class="panel trend-dual-panel">
        <section class="trend-half">
          <h3>近7天违法数量趋势</h3>
          <div class="trend-chart">
            <div v-for="item in trendList" :key="item.day" class="trend-col">
              <div class="trend-bar" :style="{ height: `${(item.count / maxTrend) * 150}px` }"></div>
              <strong>{{ item.count }}</strong>
              <span class="trend-day">{{ item.day }}</span>
            </div>
          </div>
        </section>
        <section class="trend-half trend-half-split">
          <h3>近12个月违法数量趋势</h3>
          <div class="trend-chart trend-chart-month">
            <div v-for="item in monthTrendList" :key="item.month" class="trend-col">
              <div class="trend-bar trend-bar-month" :style="{ height: `${(item.count / maxMonthTrend) * 150}px` }"></div>
              <strong>{{ item.count }}</strong>
              <span class="trend-day">{{ item.month }}</span>
            </div>
          </div>
        </section>
      </section>
      <section class="panel">
        <h3>地点排行</h3>
        <ul class="rank-list">
          <li v-for="(item, index) in locationList" :key="item.name" class="rank-item">
            <span>{{ index + 1 }}. {{ item.name }}</span><strong>{{ item.count }}</strong>
          </li>
        </ul>
      </section>
    </div>
  </AdminLayout>
</template>
