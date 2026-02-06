// src/services/StatsServices.js
import api from './api'

/**
 * Controller accepts: start/end OR from/to.
 * We send both to be robust.
 */
function dateParams(from, to, asOf) {
  return {
    from,
    to,
    start: from,
    end: to,
    asOf,
  }
}

/**
 * Supports:
 *  - summary(from, to)
 *  - summary({ from, to })
 */
function resolveRange(a, b) {
  if (a && typeof a === 'object') {
    return { from: a.from, to: a.to, asOf: a.asOf }
  }
  return { from: a, to: b, asOf: undefined }
}

function summary(a, b) {
  const { from, to, asOf } = resolveRange(a, b)
  return api.get('/stats/summary', { params: dateParams(from, to, asOf) })
}

function timeseries(a, b, granularityOrOpts = 'day') {
  const { from, to } = resolveRange(a, b)

  let granularity = 'day'
  if (typeof granularityOrOpts === 'string') {
    granularity = granularityOrOpts
  } else if (granularityOrOpts && typeof granularityOrOpts === 'object') {
    granularity = granularityOrOpts.granularity || granularityOrOpts.bucket || 'day'
  }

  return api.get('/stats/timeseries', {
    params: { ...dateParams(from, to), granularity },
  })
}

function brands(a, b) {
  const { from, to } = resolveRange(a, b)
  return api.get('/stats/brands', { params: dateParams(from, to) })
}

function topSales(a, b, limit = 3) {
  const { from, to } = resolveRange(a, b)
  return api.get('/stats/top-sales', { params: { ...dateParams(from, to), limit } })
}

function kpi(metric, a, b) {
  const { from, to } = resolveRange(a, b)
  return api.get(`/stats/kpi/${metric}`, { params: dateParams(from, to) })
}

function series(metric, a, b, granularityOrOpts = 'day') {
  const { from, to } = resolveRange(a, b)

  let granularity = 'day'
  if (typeof granularityOrOpts === 'string') {
    granularity = granularityOrOpts
  } else if (granularityOrOpts && typeof granularityOrOpts === 'object') {
    granularity = granularityOrOpts.granularity || granularityOrOpts.bucket || 'day'
  }

  return api.get(`/stats/series/${metric}`, {
    params: { ...dateParams(from, to), granularity },
  })
}

function breakdown(metric, a, b) {
  const { from, to } = resolveRange(a, b)
  return api.get(`/stats/breakdown/${metric}`, { params: dateParams(from, to) })
}

function rank(metric, a, b, limit = 10) {
  const { from, to } = resolveRange(a, b)
  return api.get(`/stats/rank/${metric}`, { params: { ...dateParams(from, to), limit } })
}

function dateBounds() {
  return api.get('/stats/date-bounds')
}

// Persistance du layout cote backend (prive / multi-appareil).
function getLayout() {
  return api.get('/stats/layout')
}

function saveLayout(layout) {
  return api.put('/stats/layout', { layout })
}

export default {
  summary,
  timeseries,
  brands,
  topSales,
  kpi,
  series,
  breakdown,
  rank,
  dateBounds,
  getLayout,
  saveLayout,
}
