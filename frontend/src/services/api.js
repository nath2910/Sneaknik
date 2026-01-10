import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

function getToken() {
  return localStorage.getItem('snk_token') || sessionStorage.getItem('snk_token')
}

function clearAuth() {
  localStorage.removeItem('snk_token')
  localStorage.removeItem('snk_user')
  sessionStorage.removeItem('snk_token')
  sessionStorage.removeItem('snk_user')
}

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let redirecting = false

api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err.response?.status

    if (status === 401) {
      const hadToken = !!getToken()
      if (hadToken) {
        clearAuth()
      }

      if (!redirecting && !window.location.pathname.startsWith('/auth')) {
        redirecting = true
        window.location.href = '/auth?mode=login'
      }
    }

    return Promise.reject(err)
  },
)

export default api
