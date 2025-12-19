// src/services/SnkVenteServices.js
import api from './api'

class SnkVenteServices {
  // liste complète de l’utilisateur courant
  getSnkVente() {
    return api.get('/snkVente') // header X-USER-ID ajouté par api.js
  }

  // 10 dernières ventes de l’utilisateur courant
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

  ajouter(vente) {
    // X-USER-ID sera ajouté automatiquement
    return api.post('/snkVente/add', vente)
  }

  supprimer(id) {
    return api.delete(`/snkVente/${id}`)
  }

  topVentes() {
    return api.get('/snkVente/topVentes')
  }
}

export default new SnkVenteServices()
