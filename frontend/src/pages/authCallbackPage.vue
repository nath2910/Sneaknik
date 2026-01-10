<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore.js'

const router = useRouter()
const auth = useAuthStore()

onMounted(() => {
  const hash = window.location.hash || ''
  const params = new URLSearchParams(hash.replace('#', ''))
  const token = params.get('token')

  if (!token) {
    router.replace({ name: 'auth', query: { mode: 'login' } })
    return
  }

  // ✅ met à jour store + storage
  auth.setToken(token)

  // nettoie l'URL (enlève #token=...)
  window.history.replaceState({}, document.title, window.location.pathname)

  router.replace({ name: 'home' })
})
</script>

<template>
  <div style="padding: 24px">Connexion en cours...</div>
</template>
