<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getViolationDetail, submitViolationAppeal } from '../../api/violation';
import PageHeader from '../../components/common/PageHeader.vue';
import UserLayout from '../../components/layout/UserLayout.vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const submitting = ref(false);
const detail = ref(null);
const submitResult = ref(null);

const form = reactive({
  reason: '',
  detailDesc: '',
  evidenceDesc: '',
  contactPhone: '',
  expectedResult: '',
  confirm: false,
});

const errors = reactive({
  reason: '',
  detailDesc: '',
  contactPhone: '',
  confirm: '',
});

const violationId = computed(() => route.query.violationId || '');

onMounted(async () => {
  if (!violationId.value) return;
  loading.value = true;
  detail.value = await getViolationDetail(violationId.value);
  loading.value = false;
});

function clearErrors() {
  errors.reason = '';
  errors.detailDesc = '';
  errors.contactPhone = '';
  errors.confirm = '';
}

function validate() {
  clearErrors();
  let valid = true;
  if (!form.reason.trim()) {
    errors.reason = '请选择申诉原因';
    valid = false;
  }
  if (form.detailDesc.trim().length < 10) {
    errors.detailDesc = '申诉详情至少填写 10 个字符';
    valid = false;
  }
  if (!/^1\d{10}$/.test(form.contactPhone.trim())) {
    errors.contactPhone = '请填写 11 位手机号';
    valid = false;
  }
  if (!form.confirm) {
    errors.confirm = '请勾选承诺后再提交';
    valid = false;
  }
  return valid;
}

async function handleSubmit() {
  if (!validate()) return;
  submitting.value = true;
  submitResult.value = await submitViolationAppeal({
    violationId: violationId.value || '',
    reason: form.reason.trim(),
    detailDesc: form.detailDesc.trim(),
    evidenceDesc: form.evidenceDesc.trim(),
    contactPhone: form.contactPhone.trim(),
    expectedResult: form.expectedResult.trim(),
  });
  submitting.value = false;
  window.alert('申诉已提交，等待人工复核（前端演示）');
  router.push('/user/violations');
}
</script>

<template>
  <UserLayout>
    <div class="inner-page">
      <PageHeader
        title="发起申诉"
        :links="[{ label: '我的违法记录', to: '/user/violations' }, { label: '申诉服务说明', to: '/user/appeal' }]"
      />

      <section class="panel" v-if="loading">
        <p class="muted-text">正在加载违法记录信息...</p>
      </section>

      <section class="panel" v-else>
        <h3>申诉对象</h3>
        <div class="appeal-summary-grid">
          <p><strong>违法编号：</strong>{{ detail?.id || violationId || '未指定' }}</p>
          <p><strong>违法类型：</strong>{{ detail?.type || '-' }}</p>
          <p><strong>发生时间：</strong>{{ detail?.time || '-' }}</p>
          <p><strong>发生地点：</strong>{{ detail?.location || '-' }}</p>
        </div>
      </section>

      <section class="panel">
        <h3>申诉信息填写</h3>
        <div class="appeal-form-grid">
          <label>
            申诉原因
            <select v-model="form.reason">
              <option value="">请选择原因</option>
              <option value="非本人车辆">非本人车辆</option>
              <option value="识别结果有误">识别结果有误</option>
              <option value="证据不完整">证据不完整</option>
              <option value="其他原因">其他原因</option>
            </select>
            <span class="form-error" v-if="errors.reason">{{ errors.reason }}</span>
          </label>

          <label>
            联系手机号
            <input v-model="form.contactPhone" type="text" maxlength="11" placeholder="请输入 11 位手机号" />
            <span class="form-error" v-if="errors.contactPhone">{{ errors.contactPhone }}</span>
          </label>

          <label class="appeal-form-full">
            申诉详情
            <textarea v-model="form.detailDesc" rows="4" placeholder="请详细说明申诉事实、时间点和关键依据"></textarea>
            <span class="form-error" v-if="errors.detailDesc">{{ errors.detailDesc }}</span>
          </label>

          <label class="appeal-form-full">
            补充证据说明（可选）
            <textarea v-model="form.evidenceDesc" rows="3" placeholder="可填写图片、视频、目击人等补充说明"></textarea>
          </label>

          <label class="appeal-form-full">
            期望处理结果（可选）
            <input v-model="form.expectedResult" type="text" placeholder="例如：撤销本条违法记录" />
          </label>
        </div>

        <label class="confirm-row">
          <input v-model="form.confirm" type="checkbox" />
          <span>我承诺以上申诉信息真实有效，愿意配合进一步核查。</span>
        </label>
        <p class="form-error" v-if="errors.confirm">{{ errors.confirm }}</p>

        <div class="admin-action-row">
          <button class="btn-secondary" @click="router.push('/user/violations')">取消</button>
          <button class="btn-primary" :disabled="submitting" @click="handleSubmit">
            {{ submitting ? '提交中...' : '提交申诉' }}
          </button>
        </div>
      </section>

      <section class="panel" v-if="submitResult">
        <h3>提交结果</h3>
        <p class="muted-text">申诉单号：{{ submitResult.id }}</p>
      </section>
    </div>
  </UserLayout>
</template>
