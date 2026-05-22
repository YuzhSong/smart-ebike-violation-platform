<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getAdminViolationDetail, updateViolationStatus } from '../../api/violation';
import AdminLayout from '../../components/layout/AdminLayout.vue';
import PageHeader from '../../components/common/PageHeader.vue';
import StatusTag from '../../components/common/StatusTag.vue';
import TimelinePanel from '../../components/common/TimelinePanel.vue';

const route = useRoute();
const router = useRouter();
const detail = ref(null);
const saving = ref(false);
const reviewForm = ref({
  status: 'PENDING',
  remark: '',
});

async function loadDetail() {
  detail.value = await getAdminViolationDetail(route.params.id);
  if (detail.value) {
    reviewForm.value.status = detail.value.status || 'PENDING';
    reviewForm.value.remark = detail.value.remark || '';
  }
}

onMounted(loadDetail);

async function saveStatus(status = reviewForm.value.status, remark = reviewForm.value.remark) {
  if (!detail.value) return;
  saving.value = true;
  await updateViolationStatus(detail.value.id, status, remark);
  saving.value = false;
  await loadDetail();
}
</script>

<template>
  <AdminLayout>
    <div v-if="detail" class="inner-page">
      <PageHeader title="违法事件详情审核" :links="[{ label: '返回违法事件列表', to: '/admin/violations' }]" />

      <section class="panel admin-detail-block">
        <h3>事件基本信息</h3>
        <div class="info-grid">
          <p><strong>事件编号：</strong>{{ detail.id }}</p>
          <p><strong>违法类型：</strong>{{ detail.type }}</p>
          <p><strong>发生时间：</strong>{{ detail.time }}</p>
          <p><strong>设备编号：</strong>{{ detail.deviceId || '-' }}</p>
          <p><strong>关联用户：</strong>{{ detail.userId || '-' }}</p>
          <p><strong>当前状态：</strong><StatusTag :status="detail.status" /></p>
        </div>
      </section>

      <section class="panel admin-detail-block">
        <h3>现场证据图片</h3>
        <div class="admin-evidence-card">
          <img v-if="detail.imageUrl" :src="detail.imageUrl" alt="违法抓拍证据图" class="admin-evidence-image" />
          <div v-else class="evidence-placeholder">暂无抓拍图片</div>
        </div>
      </section>

      <section class="panel admin-detail-block">
        <h3>识别结果</h3>
        <div class="info-grid">
          <p><strong>识别结论：</strong>{{ detail.aiResult.conclusion }}</p>
          <p><strong>置信度：</strong>{{ detail.confidence == null ? '-' : `${(detail.confidence * 100).toFixed(1)}%` }}</p>
          <p><strong>审核备注：</strong>{{ detail.remark || '-' }}</p>
        </div>
      </section>

      <section class="panel admin-detail-block">
        <h3>人工审核</h3>
        <div class="admin-form-grid">
          <label>
            审核状态
            <select v-model="reviewForm.status">
              <option value="PENDING">待处理</option>
              <option value="REVIEWING">审核中</option>
              <option value="CONFIRMED">已确认违法</option>
              <option value="REJECTED">已驳回/误报</option>
            </select>
          </label>
          <label class="admin-form-full">
            审核备注
            <textarea v-model="reviewForm.remark" rows="3" placeholder="请输入审核备注"></textarea>
          </label>
        </div>
      </section>

      <section class="panel admin-detail-block">
        <h3>处理时间线</h3>
        <TimelinePanel :list="detail.processTimeline" />
      </section>

      <section class="panel">
        <div class="admin-action-row">
          <button class="btn-primary" :disabled="saving" @click="saveStatus('CONFIRMED', reviewForm.remark || '审核通过')">
            审核通过
          </button>
          <button class="btn-secondary" :disabled="saving" @click="saveStatus('REJECTED', reviewForm.remark || '标记误报')">
            标记误报
          </button>
          <button class="btn-secondary" :disabled="saving" @click="saveStatus('REVIEWING', reviewForm.remark || '退回复核')">
            退回复核
          </button>
          <button class="btn-primary" :disabled="saving" @click="saveStatus()">
            {{ saving ? '保存中...' : '保存处理结果' }}
          </button>
          <button class="btn-secondary" @click="router.push('/admin/violations')">返回列表</button>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>
