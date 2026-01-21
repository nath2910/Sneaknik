<template>
  <KpiCard
    title="Bénéfice net"
    subtitle="Ventes - tous les frais"
    :accent="accent"
    :loading="loading"
    :error="error"
    :valueText="valueText"
    :deltaPct="deltas?.profit ?? null"
    :deltaText="deltaText"
    hint="sur la période"
    :spark="spark"
  />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeKpi, normalizeSeries } from '@/services/statsAdapters'
import { formatEUR, signFmt } from '@/utils/formatters'
import KpiCard from './_parts/KpiCard.vue'

const props = defineProps({ from: String, to: String, bucket: { type: String, default: 'week' } })
const accent = '#22C55E'

const loading = ref(false)
const error = ref('')
const kpi = ref({ value: 0, deltaPct: null })
const series = ref([])
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const [k, s] = await Promise.all([
      StatsServices.kpi('netProfit', props.from, props.to),
      StatsServices.series('netProfit', props.from, props.to, props.bucket),
    ])
    if (id !== req) return
    kpi.value = normalizeKpi(k.data)
    series.value = normalizeSeries(s.data)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to, props.bucket], load)

const valueText = computed(() => formatEUR(kpi.value.value, { compact: true }))
const deltaText = computed(() => (kpi.value.deltaPct == null ? '' : signFmt(kpi.value.deltaPct)))
const spark = computed(() => series.value.slice(-18).map((p) => p.value))
</script>
