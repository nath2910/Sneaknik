<template>
  <aside class="w-full lg:w-[360px] space-y-4">
    <!-- Résumé rapide -->
    <div class="bg-gray-800 border border-gray-700 rounded-3xl shadow-lg p-5 space-y-3">
      <h2 class="text-sm font-semibold text-gray-100">Résumé du mois</h2>
      <p class="text-xs text-gray-400 mb-2">
        Un aperçu rapide de tes performances calculé sur les ventes du mois en cours.
      </p>

      <div class="grid grid-cols-2 gap-3 text-center text-xs">
        <StatBadge label="Bénéfice du mois" :tone="beneficeTone" :value="formattedBenefice" />
        <StatBadge label="C.A. du mois" tone="default" :value="formattedCA" />
        <StatBadge
          label="Items vendues (mois)"
          tone="accent"
          :value="loading ? '...' : nbVendues"
        />
        <StatBadge label="Items en stock" tone="default" :value="loading ? '...' : nbEnStock" />
      </div>

      <p class="mt-2 text-[0.7rem] text-gray-500"></p>
    </div>

    <!-- Actions rapides -->
    <div
      class="bg-gray-800 border border-gray-700 rounded-3xl shadow-lg p-5 space-y-3 [&_button:hover]:bg-purple-800"
    >
      <h3 class="text-sm font-semibold text-gray-100">Actions rapides</h3>
      <p class="text-xs text-gray-400">Accède directement aux principales sections de gestion.</p>

      <div class="flex flex-col gap-2 pt-1">
        <button
          class="w-full px-4 py-2.5 rounded-xl text-sm font-medium bg-gray-900 text-white transition"
          @click="$emit('go-gestion')"
        >
          ➕ Gérer les ventes
        </button>
        <button
          class="w-full px-4 py-2.5 rounded-xl text-sm font-medium bg-gray-900 text-gray-100 border border-gray-700 transition"
          @click="$emit('go-stats')"
        >
          📊 Voir les stats détaillées
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import StatBadge from '@/components/StatBadge.vue'
import { formatEUR } from '@/utils/formatters'

interface Props {
  totalBenefice: number
  totalCA: number
  nbVendues: number
  nbEnStock: number
  loading?: boolean
}

const props = defineProps<Props>()

defineEmits<{
  (e: 'go-gestion'): void
  (e: 'go-stats'): void
}>()

const beneficeTone = computed(() =>
  props.totalBenefice >= 0 ? ('success' as const) : ('danger' as const),
)

const formattedBenefice = computed(() =>
  props.loading ? '...' : formatEUR(props.totalBenefice),
)

const formattedCA = computed(() => (props.loading ? '...' : formatEUR(props.totalCA)))
</script>
