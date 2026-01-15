import api from './api'

class AuthService {
  async register(payload) {
    // payload = { email, password, firstName, lastName, ... } selon ton RegisterRequest
    const res = await api.post('/auth/register', payload)
    return res.data
  }

  async login(payload) {
    const res = await api.post('/auth/login', payload)

    const headerAuth = res.headers?.authorization || res.headers?.Authorization
    let token = null

    if (headerAuth && headerAuth.startsWith('Bearer ')) {
      token = headerAuth.slice(7)
    }

    token = token || res.data?.token || res.data?.accessToken

    if (!token) {
      console.error('Réponse login:', res.data, res.headers)
      throw new Error('Token manquant dans la réponse /auth/login')
    }

    const user = res.data?.user ?? res.data?.utilisateur ?? null

    localStorage.setItem('snk_token', token)
    if (user) localStorage.setItem('snk_user', JSON.stringify(user))

    return { user, token }
  }

  logout() {
    localStorage.removeItem('snk_token')
    localStorage.removeItem('snk_user')
    sessionStorage.removeItem('snk_token')
    sessionStorage.removeItem('snk_user')
  }

  async changePassword(payload) {
    const res = await api.post('/auth/change-password', payload)
    return res.data
  }
}

export default new AuthService()
