import { adminDashboardStatsMock } from '../mock/statisticsMock';

export function getAdminDashboardStats() {
  return Promise.resolve(adminDashboardStatsMock);
}

export function getViolationTypeDistribution() {
  return Promise.resolve(adminDashboardStatsMock.typeRanking);
}

export function getViolationTrend7d() {
  return Promise.resolve(adminDashboardStatsMock.trend);
}

export function getLocationRanking() {
  return Promise.resolve(adminDashboardStatsMock.locationRanking);
}

export function getProcessStatusStats() {
  return Promise.resolve(adminDashboardStatsMock.processStatus);
}
