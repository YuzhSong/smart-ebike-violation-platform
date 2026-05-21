import { violationMockList } from '../mock/violationMock';

const appealRecords = [];

export function getUserViolationList(userId) {
  return Promise.resolve(violationMockList.filter((item) => item.userId === userId));
}

export function getUserViolationOverview(userId) {
  // 模拟后端聚合统计结果，后续可替换为 axios 请求
  const list = violationMockList.filter((item) => item.userId === userId);
  const unprocessed = list.filter((item) => item.status === '未处理').length;
  const processed = list.filter((item) => item.status === '已处理').length;
  const appealing = list.filter((item) => item.status === '申诉中').length;
  const latestViolationTime = list.length ? list.slice().sort((a, b) => b.time.localeCompare(a.time))[0].time : '-';
  return Promise.resolve({ unprocessed, processed, appealing, latestViolationTime });
}

export function getViolationDetail(id) {
  return Promise.resolve(violationMockList.find((item) => item.id === id) || null);
}

export function getAdminViolationDetail(id) {
  // 管理端详情当前复用同一份违法事件数据
  return Promise.resolve(violationMockList.find((item) => item.id === id) || null);
}

export function getAdminViolationList() {
  return Promise.resolve(violationMockList);
}

export function submitViolationAppeal(payload) {
  const record = {
    id: `APL-${Date.now()}`,
    createdAt: new Date().toISOString(),
    ...payload,
  };
  appealRecords.push(record);
  return Promise.resolve(record);
}
