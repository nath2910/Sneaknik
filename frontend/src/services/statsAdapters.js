// src/services/statsAdapters.js

/* =========================
   Date helpers (avoid UTC shift)
========================= */
export function toYmd(d) {
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

// Parse "YYYY-MM-DD" in local time (not UTC)
export function parseYmdLocal(ymd) {
  const [y, m, d] = String(ymd).split('-').map(Number)
  return new Date(y, (m || 1) - 1, d || 1)
}

export function prevPeriod(from, to) {
  const f = parseYmdLocal(from)
  const t = parseYmdLocal(to)

  const dayMs = 24 * 3600 * 1000
  const days = Math.round((t - f) / dayMs) + 1 // inclusive

  const prevTo = new Date(f.getTime() - dayMs)
  const prevFrom = new Date(prevTo.getTime() - (days - 1) * dayMs)

  return { from: toYmd(prevFrom), to: toYmd(prevTo) }
}

/* =========================
   Adapters for current routes
========================= */
export function normalizeSummary(raw) {
  return {
    ca: Number(raw?.ca ?? 0),
    profit: Number(raw?.profit ?? 0),
    profitMargin: Number(raw?.profitMargin ?? 0), // 0..1 (if backend returns %, divide by 100)
    itemsVendues: Number(raw?.itemsVendues ?? 0),
    itemsEnStock: Number(raw?.itemsEnStock ?? 0),
    valeurStock: Number(raw?.valeurStock ?? 0),

    // Optional fields (if backend adds them later)
    roi: raw?.roi == null ? null : Number(raw.roi), // percent
    totalAchat: raw?.totalAchat == null ? null : Number(raw.totalAchat),
    cashAvailable: raw?.cashAvailable == null ? null : Number(raw.cashAvailable),
    returnRate: raw?.returnRate == null ? null : Number(raw.returnRate), // percent
  }
}

export function normalizeTimeseries(list) {
  const arr = Array.isArray(list) ? list : []
  return arr
    .map((p) => ({
      date: String(p?.date ?? ''),
      ca: Number(p?.ca ?? 0),
      profit: Number(p?.profit ?? 0),
    }))
    .filter((p) => p.date)
}

export function normalizeBrands(list) {
  const arr = Array.isArray(list) ? list : []
  return arr
    .map((p) => ({
      label: String(p?.label ?? ''),
      nb: Number(p?.nb ?? 0),
    }))
    .filter((p) => p.label)
}

export function normalizeTopSales(list) {
  const arr = Array.isArray(list) ? list : []
  return arr
    .map((p) => ({
      nomItem: String(p?.nomItem ?? ''),
      benefice: Number(p?.benefice ?? 0),
    }))
    .filter((p) => p.nomItem)
}

/* =========================
   Generic adapters (for new widgets)
========================= */

// Generic KPI: { value, deltaPct }
export function normalizeKpi(raw) {
  return {
    value: Number(raw?.value ?? 0),
    deltaPct: raw?.deltaPct == null ? null : Number(raw.deltaPct),
  }
}

// Generic series: [{ date, value }]
export function normalizeSeries(list) {
  const arr = Array.isArray(list) ? list : []
  return arr
    .map((p) => ({
      date: String(p?.date ?? ''),
      value: Number(p?.value ?? 0),
    }))
    .filter((p) => p.date)
}

// Ranking: [{ label, value }]
export function normalizeRank(list) {
  const arr = Array.isArray(list) ? list : []
  return arr
    .map((p) => ({
      label: String(p?.label ?? ''),
      value: Number(p?.value ?? 0),
    }))
    .filter((p) => p.label)
}

// Breakdown: [{ label, value }]
export function normalizeBreakdown(list) {
  return normalizeRank(list)
}
