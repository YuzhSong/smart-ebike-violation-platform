<script setup>
import { notices, overviewCards } from '../mock/data';
</script>

<template>
  <div class="portal-page">
    <header class="portal-header">
      <h1>非机动车智能违法监管与服务平台</h1>
      <p>面向智慧交管场景的违法识别、证据留存、治理协同与公众服务一体化平台</p>
      <div class="actions">
        <router-link class="btn-primary" to="/login?role=user">用户服务入口</router-link>
        <router-link class="btn-secondary" to="/login?role=admin">管理员入口</router-link>
      </div>
    </header>
    <section class="card-grid">
      <article v-for="card in overviewCards" :key="card.label" class="data-card">
        <p class="label">{{ card.label }}</p>
        <p class="value">{{ card.value }}</p>
        <span class="tag">{{ card.trend }}</span>
      </article>
    </section>
    <section class="panel-row">
      <section class="panel">
        <h3>平台公告</h3>
        <ul>
          <li v-for="item in notices" :key="item.id"><span>{{ item.title }}</span><time>{{ item.date }}</time></li>
        </ul>
      </section>
      <section class="panel">
        <h3>平台定位</h3>
        <p>以边缘设备采集、AI识别与监管业务流程联动为核心，提供用户查询、申诉服务和监管驾驶舱能力。</p>
      </section>
    </section>
  </div>
</template>
