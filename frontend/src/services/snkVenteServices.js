import axios from 'axios'

const SNK_VENTE_API_BASE_URL = 'http://localhost:8080/snkVente'

class SnkVenteServices {
  getSnkVente() {
    return axios.get(SNK_VENTE_API_BASE_URL)
  }

  ajouter() {
    return axios.post(`${SNK_VENTE_API_BASE_URL}/add`, vente)
  }

  rechercher10() {
    return axios.get(`${SNK_VENTE_API_BASE_URL}/recent`)
  }

  totalBenef() {
    return axios.get(`${SNK_VENTE_API_BASE_URL}/total`)
  }
  totalBenefAnnee(year) {
    return axios.get(`${SNK_VENTE_API_BASE_URL}/total`, { params: { year } })
  }
  chiffreAffaire() {
    return axios.get(`${SNK_VENTE_API_BASE_URL}/ca`)
  }

  marque() {
    return axios.get(`${SNK_VENTE_API_BASE_URL}/marque`)
  }
}

export default new SnkVenteServices()

//relation front back via requete
//Couche “API” côté front (Axios).
//Expose getSnkVente() qui fait GET http://localhost:8080/api/snkVente
//Centralise la baseURL, les headers, etc.
