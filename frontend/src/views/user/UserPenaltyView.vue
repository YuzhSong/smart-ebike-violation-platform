<script setup>
import PageHeader from '../../components/common/PageHeader.vue';
import UserLayout from '../../components/layout/UserLayout.vue';
</script>

<template>
  <UserLayout>
    <div class="inner-page">
      <PageHeader title="处罚说明" :links="[{ label: '用户首页', to: '/user' }, { label: '返回首页', to: '/' }]" />
      <section class="panel">
        <h3>处罚标准说明</h3>
        <ul>
          <li><span>闯红灯</span><span>罚款 50 元，记违法记录 1 次</span></li>
          <li><span>逆向行驶</span><span>罚款 30 元，记违法记录 1 次</span></li>
          <li><span>占用机动车道</span><span>警告或罚款 20 元</span></li>
          <li><span>未佩戴头盔</span><span>警告并责令整改</span></li>
        </ul>
      </section>
      <section class="panel">
        <h3>处理方式</h3>
        <p class="muted-text">如对处罚内容有异议，可在申诉服务页面提交申诉材料。</p>
      </section>
    </div>
  </UserLayout>
</template>
