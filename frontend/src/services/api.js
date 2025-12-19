// src/services/api.js
// peermet de créer les requetes en integrant directement dan sleheader le user id creant ainsi un espace dedier a chauq euser

import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  try {
    const raw = localStorage.getItem('snk_user')
    if (raw) {
      const user = JSON.parse(raw)
      if (user?.id) {
        config.headers['X-USER-ID'] = user.id
      }
    }
  } catch (e) {
    console.error('Erreur lecture user localStorage', e)
  }
  return config
})

export default api
