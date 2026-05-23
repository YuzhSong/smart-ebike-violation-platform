import { assetUrl, request } from './http';

const STATUS_TEXT = {
  PENDING: '待处理',
  REVIEWING: '审核中',
  CONFIRMED: '已确认',
  REJECTED: '已驳回',
};

function normalizeSummary(item = {}) {
  return {
    ...item,
    title: item.violationType || '-',
    type: item.violationType || '-',
    time: item.eventTime || '-',
    location: item.locationDesc || '-',
    userName: item.username || item.userAccount || (item.userId ? `用户 #${item.userId}` : '-'),
    userAccount: item.userAccount || '-',
    deviceId: item.deviceId || '-',
    deviceCode: item.deviceCode || '-',
    statusText: STATUS_TEXT[item.status] || item.status || '-',
    punishment: punishmentText(item.violationType),
    confidence: item.confidence ?? null,
  };
}

function normalizeDetail(item = {}) {
  const summary = normalizeSummary(item);
  const imageUrl = assetUrl(item.imageUrl);
  return {
    ...summary,
    imageUrl,
    userName: item.username || item.userAccount || (item.userId ? `用户 #${item.userId}` : '-'),
    location: item.locationDesc || '-',
    manualReviewRequired: item.status === 'PENDING' || item.status === 'REVIEWING',
    aiResult: {
      vehicle: '-',
      rider: '-',
      helmet: '-',
      passenger: '-',
      confidence: item.confidence ?? 0,
      conclusion: item.violationType || '-',
    },
    processTimeline: [
      { step: '事件生成', time: item.eventTime || '-', desc: item.violationType || '-' },
      { step: '当前状态', time: '-', desc: STATUS_TEXT[item.status] || item.status || '-' },
      ...(item.remark ? [{ step: '审核备注', time: '-', desc: item.remark }] : []),
    ],
  };
}

function punishmentText(violationType) {
  const map = {
    闯红灯: '罚款 50 元，记违法记录 1 次',
    逆行: '罚款 30 元，记违法记录 1 次',
    逆向行驶: '罚款 30 元，记违法记录 1 次',
    占用机动车道: '警告或罚款 20 元',
    未佩戴头盔: '警告并责令整改',
  };
  return map[violationType] || '按现场审核结果处理';
}

export async function getUserViolationList(userId) {
  const list = await request('/api/user/violations', { params: { userId } });
  return (list || []).map(normalizeSummary);
}

export async function getUserViolationOverview(userId) {
  const list = await getUserViolationList(userId);
  const unprocessed = list.filter((item) => item.status === 'PENDING').length;
  const processed = list.filter((item) => item.status === 'CONFIRMED' || item.status === 'REJECTED').length;
  const appealing = list.filter((item) => item.status === 'REVIEWING').length;
  const latestViolationTime = list.length
    ? list.slice().sort((a, b) => String(b.time).localeCompare(String(a.time)))[0].time
    : '-';
  return { unprocessed, processed, appealing, latestViolationTime };
}

export async function getViolationDetail(id) {
  const detail = await request(`/api/user/violations/${id}`);
  return detail ? normalizeDetail(detail) : null;
}

export async function getAdminViolationDetail(id) {
  const detail = await request(`/api/admin/violations/${id}`);
  return detail ? normalizeDetail(detail) : null;
}

export async function getAdminViolationList(params = {}) {
  const page = await request('/api/admin/violations', {
    params: { page: 1, size: 100, ...params },
  });
  return (page?.records || []).map(normalizeSummary);
}

export async function updateViolationStatus(id, status, remark = '') {
  return request(`/api/admin/violations/${id}/status`, {
    method: 'PUT',
    body: JSON.stringify({ status, remark }),
  });
}

export async function submitViolationAppeal(payload) {
  const remark = [
    payload.reason && `申诉原因：${payload.reason}`,
    payload.detailDesc && `申诉详情：${payload.detailDesc}`,
    payload.evidenceDesc && `补充证据：${payload.evidenceDesc}`,
    payload.contactPhone && `联系电话：${payload.contactPhone}`,
    payload.expectedResult && `期望结果：${payload.expectedResult}`,
  ].filter(Boolean).join('；').slice(0, 255);
  await updateViolationStatus(payload.violationId, 'REVIEWING', remark);
  return {
    id: payload.violationId,
    createdAt: new Date().toISOString(),
    ...payload,
  };
}
