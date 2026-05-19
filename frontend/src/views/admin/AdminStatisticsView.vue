<script setup>
import { computed, onMounted, ref } from 'vue';
import {
  getLocationRanking,
  getProcessStatusStats,
  getViolationTrend7d,
  getViolationTypeDistribution,
} from '../../api/statistics';
import PageHeader from '../../components/common/PageHeader.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const typeList = ref([]);
const trendList = ref([]);
const locationList = ref([]);
const processList = ref([]);

const maxTrend = computed(() => Math.max(...trendList.value.map((i) => i.count), 1));

onMounted(async () => {
  typeList.value = await getViolationTypeDistribution();
  trendList.value = await getViolationTrend7d();
  locationList.value = await getLocationRanking();
  processList.value = await getProcessStatusStats();
});
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="统计分析" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel-row">
        <section class="panel">
          <h3>违法类型分布</h3>
          <ul class="rank-list">
            <li v-for="item in typeList" :key="item.name" class="rank-item">
              <span>{{ item.name }}</span><strong>{{ item.count }}</strong>
            </li>
          </ul>
        </section>
        <section class="panel">
          <h3>处理状态统计</h3>
          <ul class="rank-list">
            <li v-for="item in processList" :key="item.name" class="rank-item">
              <span>{{ item.name }}</span><strong>{{ item.value }}</strong>
            </li>
          </ul>
        </section>
      </section>
      <section class="panel">
        <h3>最近7天违法数量趋势</h3>
        <div class="trend-chart">
          <div v-for="item in trendList" :key="item.day" class="trend-col">
            <div class="trend-bar" :style="{ height: `${(item.count / maxTrend) * 150}px` }"></div>
            <strong>{{ item.count }}</strong>
            <span class="trend-day">{{ item.day }}</span>
          </div>
        </div>
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
