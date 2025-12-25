// src/store/authStore.js
import { ref } from 'vue'

const user = ref(null)
const token = ref(null)

function safeGet(key) {
  try {
    return localStorage.getItem(key)
  } catch {
    return sessionStorage.getItem(key)
  }
}
function safeSet(key, value) {
  try {
    localStorage.setItem(key, value)
  } catch {
    sessionStorage.setItem(key, value)
  }
}
function safeRemove(key) {
  try {
    localStorage.removeItem(key)
  } catch {
    sessionStorage.removeItem(key)
  }
}

function loadFromStorage() {
  try {
    user.value = JSON.parse(safeGet('snk_user') || 'null')
  } catch {
    user.value = null
  }
  token.value = safeGet('snk_token')
}
loadFromStorage()

function setAuth(payload) {
  user.value = payload?.user ?? null
  token.value = payload?.token ?? null

  if (user.value) safeSet('snk_user', JSON.stringify(user.value))
  else safeRemove('snk_user')

  if (token.value) safeSet('snk_token', token.value)
  else safeRemove('snk_token')
}

function logout() {
  setAuth(null)
}

export function useAuthStore() {
  return { user, token, setAuth, logout }
}
