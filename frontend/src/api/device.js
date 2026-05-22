import { request } from './http';

export async function getDeviceList(params = {}) {
  const list = await request('/api/admin/devices', { params });
  return (list || []).map((item) => ({
    ...item,
    id: item.deviceCode || item.id,
    rawId: item.id,
    location: item.locationDesc || '-',
    type: '-',
    onlineStatus: item.status || '-',
    lastUploadTime: '-',
    todayTasks: '-',
    maintenanceStatus: '-',
  }));
}
