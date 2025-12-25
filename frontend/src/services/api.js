// src/services/api.js
// peermet de créer les requetes en integrant directement dan sleheader le user id creant ainsi un espace dedier a chauq euser

import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  config.headers = config.headers || {}

  const token = localStorage.getItem('snk_token') || sessionStorage.getItem('snk_token')

  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      const hadToken = !!localStorage.getItem('snk_token') || !!sessionStorage.getItem('snk_token')

      if (hadToken) {
        localStorage.removeItem('snk_token')
        localStorage.removeItem('snk_user')
        sessionStorage.removeItem('snk_token')
        sessionStorage.removeItem('snk_user')
        window.location.href = '/auth?mode=login'
      }
    }
    return Promise.reject(err)
  },
)

export default api
