<template>
  <KpiCard
    title="Valeur du stock"
    subtitle="Capital immobilisé"
    :accent="accent"
    :loading="loading"
    :error="error"
    :valueText="valueText"
    :deltaPct="deltaPct"
    :deltaText="deltaText"
    hint="au coût d’achat"
  />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeSummary, prevPeriod } from '@/services/statsAdapters'
import { formatEUR, signFmt } from '@/utils/formatters'
import KpiCard from './_parts/KpiCard.vue'

const props = defineProps({ from: String, to: String })
const accent = '#06B6D4'

const loading = ref(false)
const error = ref('')
const summary = ref({ valeurStock: 0 })
const deltaPct = ref(null)
let req = 0

function calcDeltaPct(curr, prev) {
  const c = Number(curr ?? 0)
  const p = Number(prev ?? 0)
  if (!Number.isFinite(p) || p === 0) return null
  return ((c - p) / Math.abs(p)) * 100
}

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const { from: prevFrom, to: prevTo } = prevPeriod(props.from, props.to)
    const [currRes, prevRes] = await Promise.all([
      StatsServices.summary(props.from, props.to),
      StatsServices.summary(prevFrom, prevTo),
    ])
    if (id !== req) return
    const curr = normalizeSummary(currRes.data)
    const prev = normalizeSummary(prevRes.data)
    summary.value = curr
    deltaPct.value = calcDeltaPct(curr.valeurStock, prev.valeurStock)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to], load)

const valueText = computed(() => formatEUR(summary.value.valeurStock, { compact: true }))
const deltaText = computed(() => (deltaPct.value == null ? '' : signFmt(deltaPct.value)))
</script>
