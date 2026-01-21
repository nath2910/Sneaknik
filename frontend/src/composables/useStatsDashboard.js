import { ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import {
  normalizeSummary,
  normalizeTimeseries,
  normalizeBrands,
  normalizeTopSales,
  prevPeriod,
} from '@/services/statsAdapters'

function deltaPct(curr, prev) {
  const c = Number(curr ?? 0)
  const p = Number(prev ?? 0)
  if (p === 0) return null
  return ((c - p) / Math.abs(p)) * 100
}

export function useStatsDashboard(fromRef, toRef, granularityRef, topSalesLimitRef) {
  const loading = ref(false)
  const error = ref('')

  const summary = ref(normalizeSummary({}))
  const summaryPrev = ref(normalizeSummary({}))
  const timeseries = ref([])
  const brands = ref([])
  const topSales = ref([])

  const deltas = ref({
    ca: null,
    profit: null,
    itemsVendues: null,
    valeurStock: null,
  })

  let req = 0

  async function load() {
    const from = fromRef.value
    const to = toRef.value
    const granularity = granularityRef?.value ?? 'day'
    const limit = topSalesLimitRef?.value ?? 3
    if (!from || !to) return

    const id = ++req
    loading.value = true
    error.value = ''

    try {
      const prev = prevPeriod(from, to)

      const [sNow, sPrev, ts, b, t] = await Promise.all([
        StatsServices.summary(from, to),
        StatsServices.summary(prev.from, prev.to),
        StatsServices.timeseries(from, to, granularity),
        StatsServices.brands(from, to),
        StatsServices.topSales(from, to, limit),
      ])

      if (id !== req) return

      summary.value = normalizeSummary(sNow.data)
      summaryPrev.value = normalizeSummary(sPrev.data)
      timeseries.value = normalizeTimeseries(ts.data)
      brands.value = normalizeBrands(b.data)
      topSales.value = normalizeTopSales(t.data)

      deltas.value = {
        ca: deltaPct(summary.value.ca, summaryPrev.value.ca),
        profit: deltaPct(summary.value.profit, summaryPrev.value.profit),
        itemsVendues: deltaPct(summary.value.itemsVendues, summaryPrev.value.itemsVendues),
        valeurStock: deltaPct(summary.value.valeurStock, summaryPrev.value.valeurStock),
      }
    } catch (e) {
      if (id !== req) return
      error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
    } finally {
      if (id === req) loading.value = false
    }
  }

  watch([fromRef, toRef, granularityRef, topSalesLimitRef], load, { immediate: true })

  return { loading, error, summary, summaryPrev, timeseries, brands, topSales, deltas }
}
