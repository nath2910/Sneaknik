<template>
  <WidgetCard
    title="Top ventes"
    subtitle="Plus gros bénéfices"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <div class="space-y-2">
      <div
        v-for="(x, idx) in topSales"
        :key="idx"
        class="flex items-center justify-between gap-3 rounded-xl border border-slate-800 bg-slate-950/30 px-3 py-2"
      >
        <div class="min-w-0">
          <p class="text-xs text-white/90 truncate">{{ x.nomItem }}</p>
          <p class="text-[11px] text-white/45">#{{ idx + 1 }}</p>
        </div>
        <div class="text-sm font-semibold text-emerald-300">
          {{ formatEUR(x.benefice, { compact: true }) }}
        </div>
      </div>
    </div>
  </WidgetCard>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeTopSales } from '@/services/statsAdapters'
import WidgetCard from './_parts/WidgetCard.vue'
import { formatEUR } from '@/utils/formatters'

const props = defineProps({ from: String, to: String, limit: { type: Number, default: 5 } })
const accent = '#22C55E'

const loading = ref(false)
const error = ref('')
const topSales = ref([])
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const { data } = await StatsServices.topSales(props.from, props.to, props.limit)
    if (id !== req) return
    topSales.value = normalizeTopSales(data)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to, props.limit], load)
</script>
