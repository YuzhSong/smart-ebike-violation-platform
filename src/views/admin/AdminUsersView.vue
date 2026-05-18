<script setup>
import { onMounted, ref } from 'vue';
import { getUserList } from '../../api/user';
import PageHeader from '../../components/common/PageHeader.vue';
import StatusTag from '../../components/common/StatusTag.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const rows = ref([]);

onMounted(async () => {
  rows.value = await getUserList();
});

function handleAction(action, id) {
  window.alert(`用户 ${id}：${action}（前端演示）`);
}
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="用户信息管理" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>用户编号</th><th>姓名</th><th>联系方式</th><th>绑定车辆/非机动车编号</th>
              <th>违法记录数</th><th>最近违法时间</th><th>用户状态</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.id }}</td><td>{{ row.name }}</td><td>{{ row.phone }}</td><td>{{ row.vehicleNo }}</td>
              <td>{{ row.violationCount }}</td><td>{{ row.latestViolationTime }}</td><td><StatusTag :status="row.status" /></td>
              <td class="table-actions">
                <button class="btn-link" @click="handleAction('查看记录', row.id)">查看记录</button>
                <button class="btn-link" @click="handleAction('编辑信息', row.id)">编辑信息</button>
                <button class="btn-link" @click="handleAction(row.status === '冻结' ? '恢复' : '冻结', row.id)">
                  {{ row.status === '冻结' ? '恢复' : '冻结' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </section>
    </div>
  </AdminLayout>
</template>
