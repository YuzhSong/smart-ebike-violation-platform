<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { login } from '../api/auth';

const router = useRouter();
const route = useRoute();
const role = ref('user');
const account = ref('');
const password = ref('');
const loading = ref(false);
const errorMessage = ref('');

async function handleLogin() {
  errorMessage.value = '';
  if (role.value === 'admin') {
    router.push('/admin');
    return;
  }
  if (!account.value.trim() || !password.value) {
    errorMessage.value = '请输入账号和密码';
    return;
  }

  loading.value = true;
  try {
    await login(account.value.trim(), password.value);
    router.push('/user');
  } catch (error) {
    errorMessage.value = error.message || '登录失败';
  } finally {
    loading.value = false;
  }
}

if (route.query.role === 'admin' || route.query.role === 'user') {
  role.value = route.query.role;
}
</script>

<template>
  <div class="login-page">
    <section class="login-left">
      <h1>非机动车智能违法监管与服务平台</h1>
      <p>聚焦城市慢行交通治理，构建“识别-取证-处置-反馈”闭环监管流程。</p>
      <p>通过 AI 视觉识别与多端协同，提高执法效能、服务体验与治理透明度。</p>
    </section>
    <section class="login-right">
      <div class="login-card">
        <h2>账号登录</h2>
        <label>账号</label>
        <input v-model="account" placeholder="请输入账号" />
        <label>密码</label>
        <input v-model="password" type="password" placeholder="请输入密码" />
        <label>身份</label>
        <select v-model="role">
          <option value="user">普通用户</option>
          <option value="admin">管理员</option>
        </select>
        <p v-if="errorMessage" class="form-error">{{ errorMessage }}</p>
        <button class="btn-primary" :disabled="loading" @click="handleLogin">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </div>
    </section>
  </div>
</template>
