<template>
  <div class="h-full flex flex-col">
    <div class="grid grid-cols-2 gap-3">
      <div class="card">
        <div class="lbl">CA</div>
        <div class="val">{{ fmtEUR(s.ca) }}</div>
      </div>
      <div class="card">
        <div class="lbl">Profit</div>
        <div class="val">{{ fmtEUR(s.profit) }}</div>
      </div>

      <div class="card" v-if="showMargin">
        <div class="lbl">Marge</div>
        <div class="val">{{ fmtPct01(s.profitMargin) }}</div>
      </div>
      <div class="card" v-if="showSold">
        <div class="lbl">Vendues</div>
        <div class="val">{{ fmtInt(s.itemsVendues) }}</div>
      </div>

      <div class="card" v-if="showStock">
        <div class="lbl">En stock</div>
        <div class="val">{{ fmtInt(s.itemsEnStock) }}</div>
      </div>
      <div class="card" v-if="showStockValue">
        <div class="lbl">Valeur stock</div>
        <div class="val">{{ fmtEUR(s.valeurStock) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeSummary } from '@/services/statsAdapters'
import { fmtEUR, fmtInt, fmtPct01 } from '@/utils/formatters'

const props = defineProps({
  from: String,
  to: String,
  showMargin: { type: Boolean, default: true },
  showSold: { type: Boolean, default: true },
  showStock: { type: Boolean, default: true },
  showStockValue: { type: Boolean, default: true },
})

const s = ref(normalizeSummary({}))
let req = 0

async function load() {
  const id = ++req
  const { data } = await StatsServices.summary(props.from, props.to)
  if (id !== req) return
  s.value = normalizeSummary(data)
}

onMounted(load)
watch(() => [props.from, props.to], load)
</script>

<style scoped>
.card {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  border-radius: 16px;
  padding: 12px;
}
.lbl {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.55);
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
.val {
  margin-top: 6px;
  font-size: 20px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.92);
}
</style>
