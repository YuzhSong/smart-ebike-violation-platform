<script setup>
import PageHeader from '../../components/common/PageHeader.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="系统设置" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel">
        <h3>平台参数（演示）</h3>
        <p class="muted-text">当前为静态前端原型，后续可接入配置中心、权限控制与审计日志。</p>
      </section>
    </div>
  </AdminLayout>
</template>
