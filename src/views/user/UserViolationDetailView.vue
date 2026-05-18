<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { getViolationDetail } from '../../api/violation';
import PageHeader from '../../components/common/PageHeader.vue';
import StatusTag from '../../components/common/StatusTag.vue';
import EvidenceImageCard from '../../components/common/EvidenceImageCard.vue';
import TimelinePanel from '../../components/common/TimelinePanel.vue';
import UserLayout from '../../components/layout/UserLayout.vue';

const route = useRoute();
const detail = ref(null);

onMounted(async () => {
  detail.value = await getViolationDetail(route.params.id);
});
</script>

<template>
  <UserLayout>
    <div class="inner-page" v-if="detail">
      <PageHeader title="违法详情" :links="[{ label: '返回记录列表', to: '/user/violations' }]" />
      <section class="panel detail-block">
        <h3>违法基本信息</h3>
        <div class="info-grid">
          <p><strong>违法标题：</strong>{{ detail.title }}</p><p><strong>违法类型：</strong>{{ detail.type }}</p>
          <p><strong>发生时间：</strong>{{ detail.time }}</p><p><strong>发生地点：</strong>{{ detail.location }}</p>
          <p><strong>处理状态：</strong><StatusTag :status="detail.status" /></p>
        </div>
      </section>
      <section class="panel detail-block"><h3>现场证据图片</h3><EvidenceImageCard :image-url="detail.imageUrl" /></section>
      <section class="panel detail-block">
        <h3>AI识别结果</h3>
        <div class="info-grid">
          <p><strong>识别对象：</strong>{{ detail.aiResult.vehicle }} / {{ detail.aiResult.rider }}</p>
          <p><strong>头盔识别：</strong>{{ detail.aiResult.helmet }}</p>
          <p><strong>载人情况：</strong>{{ detail.aiResult.passenger }}</p>
          <p><strong>识别置信度：</strong>{{ (detail.aiResult.confidence * 100).toFixed(1) }}%</p>
          <p><strong>识别结论：</strong>{{ detail.aiResult.conclusion }}</p>
        </div>
      </section>
      <section class="panel detail-block"><h3>处理时间线</h3><TimelinePanel :list="detail.processTimeline" /></section>
    </div>
  </UserLayout>
</template>
