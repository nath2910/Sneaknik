<template>
  <div class="h-full flex flex-col">
    <div class="flex items-center justify-between mb-2">
      <p class="text-xs text-white/60">Top ventes (profit)</p>
      <span class="text-xs text-white/40">Top {{ limit }}</span>
    </div>

    <div class="space-y-2 overflow-auto pr-1">
      <div v-for="(it, idx) in items" :key="it.nomItem + idx" class="row">
        <div class="name">
          <span class="rank">#{{ idx + 1 }}</span>
          <span class="txt">{{ it.nomItem }}</span>
        </div>
        <div class="amt">{{ fmtEUR(it.benefice) }}</div>
      </div>

      <div v-if="!items.length" class="text-sm text-white/40">Aucune donnée</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeTopSales } from '@/services/statsAdapters'
import { fmtEUR } from '@/utils/formatters'

const props = defineProps({
  from: String,
  to: String,
  limit: { type: Number, default: 5 },
})

const items = ref([])
let req = 0

async function load() {
  const id = ++req
  const { data } = await StatsServices.topSales(props.from, props.to, props.limit)
  if (id !== req) return
  items.value = normalizeTopSales(data)
}
onMounted(load)
watch(() => [props.from, props.to, props.limit], load)
</script>

<style scoped>
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
}
.name {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.rank {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
}
.txt {
  font-weight: 650;
  color: rgba(255, 255, 255, 0.92);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.amt {
  font-weight: 700;
  color: #22c55e;
}
</style>
