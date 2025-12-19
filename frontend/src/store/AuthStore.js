// src/store/authStore.js
import { ref } from 'vue'

const user = ref(null)

function loadFromLocalStorage() {
  const raw = localStorage.getItem('snk_user')
  if (!raw) {
    user.value = null
    return
  }
  try {
    user.value = JSON.parse(raw)
  } catch (e) {
    console.error(e)
    user.value = null
  }
}

// au chargement du module, on synchro depuis localStorage
loadFromLocalStorage()

function setUser(newUser) {
  user.value = newUser
  if (newUser) {
    localStorage.setItem('snk_user', JSON.stringify(newUser))
  } else {
    localStorage.removeItem('snk_user')
  }
}

export function useAuthStore() {
  return { user, setUser }
}
