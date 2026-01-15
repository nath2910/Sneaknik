// src/utils/formatters.js
export function fmtEUR(v) {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(
    Number(v ?? 0),
  )
}
export function fmtInt(v) {
  return new Intl.NumberFormat('fr-FR').format(Math.round(Number(v ?? 0)))
}
export function fmtPct01(v01) {
  const v = Number(v01 ?? 0) * 100
  return `${Math.round(v)}%`
}
