const API_BASE = '/api';

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

function getToken(): string | null {
  return localStorage.getItem('token');
}

export function setToken(token: string | null) {
  if (token) {
    localStorage.setItem('token', token);
  } else {
    localStorage.removeItem('token');
  }
}

export function getStoredUser() {
  const raw = localStorage.getItem('user');
  return raw ? JSON.parse(raw) : null;
}

export function setStoredUser(user: unknown) {
  if (user) {
    localStorage.setItem('user', JSON.stringify(user));
  } else {
    localStorage.removeItem('user');
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 204) {
    return undefined as T;
  }

  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    throw new ApiError(res.status, data.error || 'Request failed');
  }
  return data as T;
}

export const api = {
  register: (body: { email: string; username: string; password: string }) =>
    request('/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body: { email: string; password: string }) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  logout: () => request('/auth/logout', { method: 'POST' }),
  updateProfile: (body: { email: string; username: string; password?: string }) =>
    request('/auth/profile', { method: 'PUT', body: JSON.stringify(body) }),
  getSets: (search?: string) =>
    request(`/sets${search ? `?search=${encodeURIComponent(search)}` : ''}`),
  getMySets: () => request('/sets/mine'),
  getSet: (id: string) => request(`/sets/${id}`),
  createSet: (body: object) => request('/sets', { method: 'POST', body: JSON.stringify(body) }),
  updateSet: (id: string, body: object) =>
    request(`/sets/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteSet: (id: string) => request(`/sets/${id}`, { method: 'DELETE' }),
  addCard: (setId: string, body: object) =>
    request(`/sets/${setId}/cards`, { method: 'POST', body: JSON.stringify(body) }),
  updateCard: (id: string, body: object) =>
    request(`/cards/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  deleteCard: (id: string) => request(`/cards/${id}`, { method: 'DELETE' }),
  startLearning: (setId: string) => request(`/learning/start/${setId}`, { method: 'POST' }),
  recordResult: (body: object) =>
    request('/learning/result', { method: 'POST', body: JSON.stringify(body) }),
  getStatistics: (userId: string) => request(`/statistics/user/${userId}`),
};
