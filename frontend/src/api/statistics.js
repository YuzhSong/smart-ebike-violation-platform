import { getDeviceList } from './device';
import { request } from './http';

const STATUS_TEXT = {
  PENDING: '待处理',
  REVIEWING: '审核中',
  CONFIRMED: '已确认',
  REJECTED: '已驳回',
};

async function getStatistics(params = {}) {
  return request('/api/admin/statistics', { params });
}

function countByStatus(stats, statuses) {
  return (stats.statusDistribution || [])
    .filter((item) => statuses.includes(item.status))
    .reduce((sum, item) => sum + Number(item.count || 0), 0);
}

export async function getAdminDashboardStats() {
  const [stats, devices] = await Promise.all([getStatistics(), getDeviceList()]);
  const online = devices.filter((device) => device.onlineStatus === 'ONLINE').length;
  const deviceOnlineRate = devices.length ? ((online / devices.length) * 100).toFixed(1) : '0.0';
  return {
    todayViolations: stats.todayViolations || 0,
    pendingReview: countByStatus(stats, ['PENDING', 'REVIEWING']),
    deviceOnlineRate,
    processedCount: countByStatus(stats, ['CONFIRMED', 'REJECTED']),
    typeRanking: (stats.typeDistribution || []).map((item) => ({
      name: item.type,
      count: item.count,
    })),
    locationRanking: stats.locationRanking || [],
    trend: stats.trend || [],
  };
}

export async function getViolationTypeDistribution(params = {}) {
  const stats = await getStatistics(params);
  return (stats.typeDistribution || []).map((item) => ({
    name: item.type,
    count: item.count,
  }));
}

export async function getViolationTrend7d() {
  const stats = await getStatistics();
  return stats.trend || [];
}

export async function getLocationRanking() {
  const stats = await getStatistics();
  return stats.locationRanking || [];
}

export async function getProcessStatusStats(params = {}) {
  const stats = await getStatistics(params);
  return (stats.statusDistribution || []).map((item) => ({
    name: STATUS_TEXT[item.status] || item.status,
    value: item.count,
  }));
}
