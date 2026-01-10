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
                <th class="px-4 py-3 font-semibold text-gray-300">Nom item</th>
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
                  <p class="font-medium text-gray-100">{{ get(v, 'nomItem') }}</p>
                  <p v-if="get(v, 'categorie')" class="text-[0.7rem] text-gray-500 uppercase">
                    {{ get(v, 'categorie') }}
                  </p>
                </td>

                <td class="px-4 py-3 text-right">
                  <span class="font-medium">{{ euro(num(get(v, 'prixRetail'))) }}</span>
                </td>

                <td class="px-4 py-3 text-right">
                  <span
                    :class="['font-medium', hasResell(v) ? 'text-emerald-300' : 'text-gray-500']"
                  >
                    {{ hasResell(v) ? euro(num(get(v, 'prixResell'))) : '—' }}
                  </span>
                </td>

                <td class="px-4 py-3 text-center">
                  <span class="text-xs text-gray-300">{{ dateFr(get(v, 'dateAchat')) }}</span>
                </td>

                <td class="px-4 py-3 text-center">
                  <span class="text-xs" :class="isVendue(v) ? 'text-gray-300' : 'text-gray-500'">
                    {{ isVendue(v) ? dateFr(get(v, 'dateVente')) : '—' }}
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
    error.value = 'Impossible de charger les derniers items (auth ou API).'
    rows.value = []
  } finally {
    loading.value = false
  }
})

/**
 * Helpers robustes : gère camelCase et snake_case
 */
const get = (obj, key) => {
  if (!obj) return ''
  if (obj[key] != null) return obj[key]
  const snake = key.replace(/[A-Z]/g, (m) => `_${m.toLowerCase()}`)
  return obj[snake] ?? ''
}

const num = (v) => {
  const n = Number(v ?? 0)
  return Number.isFinite(n) ? n : 0
}

const isVendue = (v) => !!get(v, 'dateVente')

const hasResell = (v) => num(get(v, 'prixResell')) > 0

const euro = (value) =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(value || 0)

const dateFr = (value) => {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '—'
  return d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
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
