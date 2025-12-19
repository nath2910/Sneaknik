<template>
  <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
    <StatBadge label="Total paires" :value="totalPaires" />
    <StatBadge label="En stock" tone="success" :value="nbEnStock" />
    <StatBadge label="Vendues" tone="accent" :value="nbVendues" />
    <StatBadge label="Valeur stock" :value="valeurStock.toFixed(2) + ' €'" />
  </div>
</template>

<script setup lang="ts">
import StatBadge from '@/components/statBadge.vue'

interface Props {
  totalPaires: number
  nbEnStock: number
  nbVendues: number
  valeurStock: number
}

defineProps<Props>()
</script>
