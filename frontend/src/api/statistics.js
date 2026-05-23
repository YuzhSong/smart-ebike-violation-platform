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

function formatDate(date) {
  const y = date.getFullYear();
  const m = `${date.getMonth() + 1}`.padStart(2, '0');
  const d = `${date.getDate()}`.padStart(2, '0');
  return `${y}-${m}-${d}`;
}

export async function getViolationTrend12m() {
  const now = new Date();
  const monthRanges = Array.from({ length: 12 }, (_, idx) => {
    const offset = 11 - idx;
    const firstDay = new Date(now.getFullYear(), now.getMonth() - offset, 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() - offset + 1, 0);
    return {
      label: `${firstDay.getMonth() + 1}月`,
      startDate: formatDate(firstDay),
      endDate: formatDate(lastDay),
    };
  });

  const statsList = await Promise.all(
    monthRanges.map((range) =>
      getStatistics({
        startDate: range.startDate,
        endDate: range.endDate,
      })
    )
  );

  return monthRanges.map((range, index) => ({
    month: range.label,
    count: Number(statsList[index]?.totalViolations || 0),
  }));
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
