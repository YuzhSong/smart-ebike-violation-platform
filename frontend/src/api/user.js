import { getAuthSession } from './auth';
import { request } from './http';

const DEMO_USER_ID = Number(import.meta.env.VITE_DEMO_USER_ID || 1);

export async function getCurrentUser() {
  const session = getAuthSession();
  if (session?.userId) {
    return {
      id: session.userId,
      name: session.username || session.account,
      account: session.account,
      phone: session.phone,
      role: 'user',
      region: '-',
    };
  }
  return {
    id: DEMO_USER_ID,
    name: `用户 #${DEMO_USER_ID}`,
    role: 'user',
    region: '-',
  };
}

export async function getUserList() {
  const list = await request('/api/admin/users');
  return (list || []).map((item) => ({
    id: item.id,
    account: item.account || '-',
    name: item.username || '-',
    phone: item.phone || '-',
    vehicleNo: '-',
    violationCount: item.violationCount ?? 0,
    latestViolationTime: item.latestViolationTime || '-',
    lastLoginAt: item.lastLoginAt || '-',
    status: item.status || '-',
  }));
}
