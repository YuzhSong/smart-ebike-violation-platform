import { request } from './http';

const AUTH_STORAGE_KEY = 'traffic_platform_auth';

export async function login(account, password) {
  const data = await request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ account, password }),
  });
  const session = {
    userId: data.userId,
    account: data.account,
    username: data.username,
    phone: data.phone,
    tokenType: data.tokenType,
    accessToken: data.accessToken,
  };
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
  return session;
}

export function getAuthSession() {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }
}

export function clearAuthSession() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}
