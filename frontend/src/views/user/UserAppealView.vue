<script setup>
import PageHeader from '../../components/common/PageHeader.vue';
import UserLayout from '../../components/layout/UserLayout.vue';
</script>

<template>
  <UserLayout>
    <div class="inner-page">
      <PageHeader title="申诉服务" :links="[{ label: '我的违法记录', to: '/user/violations' }, { label: '返回首页', to: '/' }]" />
      <section class="panel">
        <h3>申诉提交说明</h3>
        <ul>
          <li><span>申诉入口</span><span>在“我的违法记录”中打开详情后发起申诉</span></li>
          <li><span>材料要求</span><span>请准备时间、地点和辅助证明材料</span></li>
          <li><span>处理时效</span><span>通常 1-3 个工作日完成初审</span></li>
          <li><span>进度查询</span><span>可在“我的违法记录”中查看申诉状态</span></li>
        </ul>
      </section>
    </div>
  </UserLayout>
</template>
