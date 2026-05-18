<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const router = useRouter();
const route = useRoute();
const role = ref('user');
const account = ref('');
const password = ref('');

function handleLogin() {
  if (role.value === 'admin') router.push('/admin');
  else router.push('/user');
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
        <button class="btn-primary" @click="handleLogin">登录</button>
      </div>
    </section>
  </div>
</template>
