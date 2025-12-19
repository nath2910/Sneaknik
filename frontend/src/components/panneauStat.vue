<template>
  <!-- Top ventes de l'année -->
  <div class="bg-gray-800 border border-gray-700 rounded-3xl shadow-lg p-5 space-y-3">
    <h3 class="text-sm font-semibold text-gray-100">Top ventes de l’année</h3>
    <p class="text-xs text-gray-400">Les 3 items qui t’ont rapporté le plus cette année.</p>

    <div v-if="loadingTop" class="text-xs text-gray-400 pt-1">Chargement...</div>

    <div v-else-if="!topVentes.length" class="text-xs text-gray-500 pt-1">
      Pas encore de meilleures ventes à afficher.
    </div>

    <ul v-else class="space-y-2 pt-1 text-sm">
      <li
        v-for="(vente, index) in topVentes"
        :key="vente.nomItem"
        class="flex items-center justify-between"
      >
        <div class="flex items-center gap-2">
          <span
            class="inline-flex h-6 w-6 rounded-full bg-purple-600 text-white text-xs items-center justify-center"
          >
            #{{ index + 1 }}
          </span>
          <span class="text-gray-100">{{ vente.nomItem }}</span>
        </div>
        <span class="text-emerald-400 font-semibold"> {{ vente.benefice }} €</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import StatBadge from '@/components/statBadge.vue'
import SnkVenteServices from '@/services/SnkVenteServices' // 🟣 c’était manquant

interface Props {
  totalBenefice: number
  totalCA: number
  nbVendues: number
  nbEnStock: number
  loading?: boolean
}

  defineEmits<{
  (e: 'go-gestion'): void
  (e: 'go-stats'): void
}>()

// creation de l'objet topVente que l'on vas remplir avec l'appelle a l'api
type TopVente = {
  nomItem: string
  benefice: number
}

const topVentes = ref<TopVente[]>([])
const loadingTop = ref(true)

onMounted(async () => {
  try {
    const res = await SnkVenteServices.topVentes()
    topVentes.value = res.data
  } catch (error) {
    console.error('Erreur chargement top ventes', error)
    topVentes.value = []
  } finally {
    loadingTop.value = false
  }
})
</script>
