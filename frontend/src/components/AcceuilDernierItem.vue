<template>
  <div class="overflow-hidden rounded-2xl">
    <section
      class="bg-slate-900/80 rounded-3xl border border-purple-500/70 shadow-xl overflow-hidden"
    >
      <header class="px-8 py-5 border-b border-purple-500/40 bg-slate-950/80">
        <h2 class="text-xl md:text-2xl font-semibold text-white">Derniers items ajoutés</h2>
        <p class="mt-1 text-xs text-gray-400">Les 10 derniers items ajoutés dans ton stock.</p>
      </header>

      <div class="px-4 pb-4 pt-3">
        <div v-if="loading" class="flex items-center justify-center py-10 text-sm text-gray-400">
          Chargement...
        </div>

        <div v-else-if="error" class="flex items-center justify-center py-10 text-sm text-red-300">
          {{ error }}
        </div>

        <div
          v-else-if="rows.length === 0"
          class="flex items-center justify-center py-10 text-sm text-gray-400"
        >
          Aucun item pour l’instant.
        </div>

        <div v-else class="overflow-x-auto custom-scrollbar">
          <table class="min-w-full text-sm text-left text-gray-300">
            <thead>
              <tr class="bg-slate-950/70 text-[0.7rem] uppercase tracking-wide">
                <th class="px-4 py-3 font-semibold text-gray-300">Nom de l'item</th>
                <th class="px-4 py-3 font-semibold text-gray-300 text-right">Prix retail</th>
                <th class="px-4 py-3 font-semibold text-gray-300 text-right">Prix resell</th>
                <th class="px-4 py-3 font-semibold text-gray-300 text-center">Date d’achat</th>
                <th class="px-4 py-3 font-semibold text-gray-300 text-center">Date de vente</th>
                <th class="px-4 py-3 font-semibold text-gray-300 text-center">Statut</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="v in rows"
                :key="v.id"
                class="border-t border-slate-800/80 hover:bg-slate-800/70 transition"
              >
                <td class="px-4 py-3">
                  <p class="font-medium text-gray-100">{{ field(v, 'nomItem') }}</p>
                </td>

                <td class="px-4 py-3 text-right">
                  <span class="font-medium">{{ prixRetailText(v) }}</span>
                </td>

                <td class="px-4 py-3 text-right">
                  <span
                    :class="['font-medium', hasResell(v) ? 'text-emerald-300' : 'text-gray-500']"
                  >
                    {{ hasResell(v) ? prixResellText(v) : '--' }}
                  </span>
                </td>

                <td class="px-4 py-3 text-center">
                  <span class="text-xs text-gray-300">{{ dateFr(field(v, 'dateAchat')) }}</span>
                </td>

                <td class="px-4 py-3 text-center">
                  <span class="text-xs" :class="isVendue(v) ? 'text-gray-300' : 'text-gray-500'">
                    {{ isVendue(v) ? dateFr(field(v, 'dateVente')) : '--' }}
                  </span>
                </td>

                <td class="px-4 py-3 text-center">
                  <span
                    class="inline-flex items-center justify-center rounded-full px-3 py-1 text-[0.7rem] font-medium"
                    :class="
                      isVendue(v)
                        ? 'bg-emerald-500/10 text-emerald-300 border border-emerald-500/40'
                        : 'bg-slate-800 text-gray-300 border border-slate-700'
                    "
                  >
                    {{ isVendue(v) ? 'Vendue' : 'En stock' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'
import { formatEUR, formatDateFR } from '@/utils/formatters'
import { getField, hasResell, isVendue, prixResellOf, prixRetailOf } from '@/utils/snkVente'

const rows = ref([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await SnkVenteServices.recent(6)
    rows.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error(e)
    error.value = "Vous n'etes pas connecte."
    rows.value = []
  } finally {
    loading.value = false
  }
})

/**
 * Helpers robustes : gère camelCase et snake_case
 */
const field = (obj, key) => getField(obj, key, '')
const euro = (value) => formatEUR(value)
const dateFr = (value) => formatDateFR(value, { fallback: '--' })
const prixRetailText = (v) => euro(prixRetailOf(v))
const prixResellText = (v) => euro(prixResellOf(v))
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: rgba(148, 163, 184, 0.6);
  border-radius: 9999px;
}
</style>
