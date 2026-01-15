// src/services/statsAdapters.js

export function normalizeSummary(raw) {
  return {
    ca: Number(raw?.ca ?? 0),
    profit: Number(raw?.profit ?? 0),
    profitMargin: Number(raw?.profitMargin ?? 0), // 0..1
    itemsVendues: Number(raw?.itemsVendues ?? 0),
    itemsEnStock: Number(raw?.itemsEnStock ?? 0),
    valeurStock: Number(raw?.valeurStock ?? 0),
  }
}

export function normalizeTimeseries(list) {
  const arr = Array.isArray(list) ? list : []
  return arr.map((p) => ({
    date: String(p?.date ?? ''),
    ca: Number(p?.ca ?? 0),
    profit: Number(p?.profit ?? 0),
  }))
}

export function normalizeBrands(list) {
  const arr = Array.isArray(list) ? list : []
  return arr.map((p) => ({
    label: String(p?.label ?? ''),
    nb: Number(p?.nb ?? 0),
  }))
}

export function normalizeTopSales(list) {
  const arr = Array.isArray(list) ? list : []
  return arr.map((p) => ({
    nomItem: String(p?.nomItem ?? ''),
    benefice: Number(p?.benefice ?? 0),
  }))
}

export function toYmd(d) {
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

export function prevPeriod(from, to) {
  const f = new Date(from)
  const t = new Date(to)
  const ms = t.getTime() - f.getTime()
  const prevTo = new Date(f.getTime() - 24 * 3600 * 1000)
  const prevFrom = new Date(prevTo.getTime() - ms)
  return { from: toYmd(prevFrom), to: toYmd(prevTo) }
}
