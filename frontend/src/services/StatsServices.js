// src/services/StatsServices.js
import api from './api'

export default {
  summary(from, to) {
    return api.get('/stats/summary', { params: { from, to } })
  },

  timeseries(from, to, granularity = 'day') {
    return api.get('/stats/timeseries', { params: { from, to, granularity } })
  },

  brands(from, to) {
    return api.get('/stats/breakdown/brands', { params: { from, to } })
  },

  topSales(from, to, limit = 3) {
    return api.get('/stats/top-sales', { params: { from, to, limit } })
  },
}
