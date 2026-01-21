<template>
  <KpiCard
    title="Délai moyen de vente"
    subtitle="Days to Sell"
    :accent="accent"
    :loading="loading"
    :error="error"
    :valueText="valueText"
    :deltaPct="kpi.deltaPct"
    :deltaText="deltaText"
    hint="plus bas = mieux"
    :spark="spark"
  />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeKpi, normalizeSeries } from '@/services/statsAdapters'
import { signFmt } from '@/utils/formatters'
import KpiCard from './_parts/KpiCard.vue'

const props = defineProps({ from: String, to: String, bucket: { type: String, default: 'week' } })
const accent = '#60A5FA'

const loading = ref(false)
const error = ref('')
const kpi = ref({ value: 0, deltaPct: null }) // value = jours
const series = ref([])
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const [k, s] = await Promise.all([
      StatsServices.kpi('avgDaysToSell', props.from, props.to),
      StatsServices.series('avgDaysToSell', props.from, props.to, props.bucket),
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

const valueText = computed(() => `${Number(kpi.value.value ?? 0).toFixed(0)} j`)
const deltaText = computed(() => (kpi.value.deltaPct == null ? '' : signFmt(kpi.value.deltaPct)))
const spark = computed(() => series.value.slice(-18).map((p) => p.value))
</script>
