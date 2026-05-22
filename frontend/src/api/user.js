const DEMO_USER_ID = Number(import.meta.env.VITE_DEMO_USER_ID || 1);

export async function getCurrentUser() {
  return {
    id: DEMO_USER_ID,
    name: `用户 #${DEMO_USER_ID}`,
    role: 'user',
    region: '-',
  };
}

export async function getUserList() {
  return [];
}
