import axios from 'axios'

const baseURL =
  import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

console.log('✅ API baseURL =', baseURL)

const api = axios.create({
  baseURL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

function getToken() {
  return localStorage.getItem('snk_token') || sessionStorage.getItem('snk_token') || ''
}

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    // plus robuste avec AxiosHeaders
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }

  console.log('➡️ REQ', config.method?.toUpperCase(), config.baseURL + config.url, {
    hasToken: !!token,
    authHeader: config.headers?.Authorization,
  })
  return config
})

export default api
