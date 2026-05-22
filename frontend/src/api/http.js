const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

function buildQuery(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.set(key, value);
    }
  });
  const text = query.toString();
  return text ? `?${text}` : '';
}

export function assetUrl(url) {
  if (!url) return '';
  if (/^https?:\/\//i.test(url)) return url;
  return `${API_BASE_URL}${url.startsWith('/') ? url : `/${url}`}`;
}

export async function request(path, options = {}) {
  const { params, headers, ...fetchOptions } = options;
  const response = await fetch(`${API_BASE_URL}${path}${buildQuery(params)}`, {
    headers: {
      ...(fetchOptions.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
      ...headers,
    },
    ...fetchOptions,
  });

  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json') ? await response.json() : null;

  if (!response.ok) {
    throw new Error(payload?.message || `HTTP ${response.status}`);
  }
  if (payload && typeof payload.code === 'number' && payload.code !== 200) {
    throw new Error(payload.message || `业务错误 ${payload.code}`);
  }
  return payload?.data ?? payload;
}
