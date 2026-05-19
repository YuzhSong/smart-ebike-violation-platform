<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getAdminViolationDetail } from '../../api/violation';
import AdminLayout from '../../components/layout/AdminLayout.vue';
import PageHeader from '../../components/common/PageHeader.vue';
import StatusTag from '../../components/common/StatusTag.vue';
import TimelinePanel from '../../components/common/TimelinePanel.vue';

const route = useRoute();
const router = useRouter();
const detail = ref(null);
const reviewForm = ref({
  reviewStatus: '待审核',
  punishment: '',
  reviewComment: '',
  processRemark: '',
});

const aiReviewAdvice = computed(() => {
  if (!detail.value) return '待判定';
  return detail.value.manualReviewRequired ? '建议人工复核' : '可直接按规则处理';
});

const relatedUser = computed(() => {
  if (!detail.value) return null;
  return {
    id: detail.value.userId,
    name: detail.value.userName,
    phone: '138-0000-0000',
    vehicleCode: `VEH-${detail.value.userId}`,
  };
});

const deviceInfo = computed(() => {
  if (!detail.value) return null;
  return {
    id: detail.value.deviceId,
    position: detail.value.location,
    type: detail.value.deviceId?.startsWith('EDGE') ? '边缘感知一体机' : '定点抓拍摄像机',
    latestUploadTime: detail.value.time,
  };
});

onMounted(async () => {
  detail.value = await getAdminViolationDetail(route.params.id);
  if (detail.value) {
    // 初始化表单默认值，便于管理员直接调整处理结果
    reviewForm.value.punishment = detail.value.punishment;
  }
});

function actionTip(actionName) {
  if (!detail.value) return;
  window.alert(`事件 ${detail.value.id}：${actionName}（前端交互演示）`);
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
          <p><strong>发生地点：</strong>{{ detail.location }}</p>
          <p><strong>当前处理状态：</strong><StatusTag :status="detail.status" /></p>
          <p><strong>识别置信度：</strong>{{ (detail.confidence * 100).toFixed(1) }}%</p>
        </div>
      </section>

      <section class="panel admin-detail-block" v-if="relatedUser">
        <h3>关联用户信息</h3>
        <div class="info-grid">
          <p><strong>用户编号：</strong>{{ relatedUser.id }}</p>
          <p><strong>用户姓名：</strong>{{ relatedUser.name }}</p>
          <p><strong>联系方式：</strong>{{ relatedUser.phone }}</p>
          <p><strong>绑定车辆/非机动车编号：</strong>{{ relatedUser.vehicleCode }}</p>
        </div>
      </section>

      <section class="panel admin-detail-block" v-if="deviceInfo">
        <h3>设备来源信息</h3>
        <div class="info-grid">
          <p><strong>设备编号：</strong>{{ deviceInfo.id }}</p>
          <p><strong>安装位置：</strong>{{ deviceInfo.position }}</p>
          <p><strong>设备类型：</strong>{{ deviceInfo.type }}</p>
          <p><strong>最近上传时间：</strong>{{ deviceInfo.latestUploadTime }}</p>
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
        <h3>AI识别结果</h3>
        <div class="info-grid">
          <p><strong>识别对象：</strong>{{ detail.aiResult.vehicle }} / {{ detail.aiResult.rider }}</p>
          <p><strong>识别结论：</strong>{{ detail.aiResult.conclusion }}</p>
          <p><strong>置信度：</strong>{{ (detail.aiResult.confidence * 100).toFixed(1) }}%</p>
          <p><strong>是否建议人工复核：</strong>{{ aiReviewAdvice }}</p>
        </div>
      </section>

      <section class="panel admin-detail-block">
        <h3>人工审核与处理区域</h3>
        <div class="admin-form-grid">
          <label>
            审核状态
            <select v-model="reviewForm.reviewStatus">
              <option>待审核</option>
              <option>审核通过</option>
              <option>标记误报</option>
              <option>退回复核</option>
            </select>
          </label>
          <label>
            处罚方式
            <input v-model="reviewForm.punishment" type="text" placeholder="请输入处罚方式" />
          </label>
          <label class="admin-form-full">
            审核意见输入框
            <textarea v-model="reviewForm.reviewComment" rows="3" placeholder="请输入审核意见"></textarea>
          </label>
          <label class="admin-form-full">
            处理备注输入框
            <textarea v-model="reviewForm.processRemark" rows="3" placeholder="请输入处理备注"></textarea>
          </label>
        </div>
      </section>

      <section class="panel admin-detail-block">
        <h3>处理时间线</h3>
        <TimelinePanel :list="detail.processTimeline" />
      </section>

      <section class="panel">
        <div class="admin-action-row">
          <button class="btn-primary" @click="actionTip('审核通过')">审核通过</button>
          <button class="btn-secondary" @click="actionTip('标记误报')">标记误报</button>
          <button class="btn-secondary" @click="actionTip('退回复核')">退回复核</button>
          <button class="btn-secondary" @click="actionTip('修改处罚方式')">修改处罚方式</button>
          <button class="btn-primary" @click="actionTip('保存处理结果')">保存处理结果</button>
          <button class="btn-secondary" @click="router.push('/admin/violations')">返回列表</button>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>
