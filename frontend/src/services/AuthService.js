import api from './api'

class AuthService {
  register(payload) {
    // { email, firstName, lastName, password }
    return api.post('/auth/register', payload)
  }

  login(payload) {
    // { email, password }
    return api.post('/auth/login', payload)
  }
  changePassword(payload) {
    return api.post('/auth/change-password', payload)
  }
}

export default new AuthService()
