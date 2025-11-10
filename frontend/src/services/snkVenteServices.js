import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // lit la var d'env (dev ou prod)
  headers: { 'Content-Type': 'application/json' },
})

class SnkVenteServices {
  getSnkVente() {
    return api.get('/snkVente')
  }

  ajouter(vente) {
    return api.post('/snkVente/add', vente)
  }

  rechercher10() {
    return api.get('/snkVente/recent')
  }

  totalBenef() {
    return api.get('/snkVente/total')
  }

  totalBenefAnnee(year) {
    return api.get('/snkVente/total', { params: { year } })
  }

  chiffreAffaire() {
    return api.get('/snkVente/ca')
  }

  marque() {
    return api.get('/snkVente/marque')
  }
}

export default new SnkVenteServices()
