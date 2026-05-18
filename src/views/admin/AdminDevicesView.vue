<script setup>
import { onMounted, ref } from 'vue';
import { getDeviceList } from '../../api/device';
import PageHeader from '../../components/common/PageHeader.vue';
import StatusTag from '../../components/common/StatusTag.vue';
import AdminLayout from '../../components/layout/AdminLayout.vue';

const rows = ref([]);

onMounted(async () => {
  // 统一通过 api 获取设备数据，便于后期切换真实后端
  rows.value = await getDeviceList();
});

function handleAction(action, id) {
  window.alert(`设备 ${id}：${action}（前端演示）`);
}
</script>

<template>
  <AdminLayout>
    <div class="inner-page">
      <PageHeader title="设备管理" :links="[{ label: '返回控制台', to: '/admin' }]" />
      <section class="panel">
        <table class="data-table">
          <thead>
            <tr>
              <th>设备编号</th><th>安装位置</th><th>设备类型</th><th>在线状态</th>
              <th>最近上传时间</th><th>今日识别任务数</th><th>维护状态</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in rows" :key="row.id">
              <td>{{ row.id }}</td>
              <td>{{ row.location }}</td>
              <td>{{ row.type }}</td>
              <td><StatusTag :status="row.onlineStatus" /></td>
              <td>{{ row.lastUploadTime }}</td>
              <td>{{ row.todayTasks }}</td>
              <td><StatusTag :status="row.maintenanceStatus" /></td>
              <td class="table-actions">
                <button class="btn-link" @click="handleAction('查看详情', row.id)">查看详情</button>
                <button class="btn-link" @click="handleAction('远程配置', row.id)">远程配置</button>
                <button class="btn-link" @click="handleAction('维护记录', row.id)">维护记录</button>
              </td>
            </tr>
          </tbody>
        </table>
      </section>
    </div>
  </AdminLayout>
</template>
