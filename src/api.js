const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export function getAuthHeader() {
  const credentials = localStorage.getItem('medisphereCredentials');
  return credentials ? { Authorization: `Basic ${credentials}` } : {};
}

export async function apiFetch(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...getAuthHeader(), ...options.headers },
  });
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }
  return response.status === 204 ? null : response.json();
}

export function login(username, password) {
  return apiFetch('/api/patients', {
    headers: { Authorization: `Basic ${btoa(`${username}:${password}`)}` },
  }).then(() => {
    localStorage.setItem('medisphereCredentials', btoa(`${username}:${password}`));
    window.dispatchEvent(new Event('medisphere-auth-change'));
    return username;
  });
}

export function logout() {
  localStorage.removeItem('medisphereCredentials');
  window.dispatchEvent(new Event('medisphere-auth-change'));
}
