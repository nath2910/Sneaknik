<template>
  <section
    class="bg-slate-900/80 rounded-3xl border border-purple-500/70 shadow-xl overflow-hidden"
  >
    <header class="px-8 py-5 border-b border-purple-500/40 bg-slate-950/80">
      <h2 class="text-xl md:text-2xl font-semibold text-white">Dernier items ajoutés</h2>
      <p class="mt-1 text-xs text-gray-400">Les dernier items ajoutées dans ton stock.</p>
    </header>

    <div class="px-4 pb-4 pt-3">
      <div v-if="loading" class="flex items-center justify-center py-10 text-sm text-gray-400">
        Chargement des dernières ventes...
      </div>

      <div
        v-else-if="ventes.length === 0"
        class="flex items-center justify-center py-10 text-sm text-gray-400"
      >
        Aucune paire trouvée pour l’instant.
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
              v-for="vente in ventes"
              :key="vente.id"
              class="border-t border-slate-800/80 hover:bg-slate-800/70 transition"
            >
              <td class="px-4 py-3">
                <p class="font-medium text-gray-100">
                  {{ vente.nomItem || vente.nom_item }}
                </p>
                <p v-if="vente.categorie" class="text-[0.7rem] text-gray-500 uppercase">
                  {{ vente.categorie }}
                </p>
              </td>

              <td class="px-4 py-3 text-right">
                <span class="font-medium">
                  {{ formatEuro(prixRetailOf(vente)) }}
                </span>
              </td>

              <td class="px-4 py-3 text-right">
                <span
                  :class="['font-medium', hasResell(vente) ? 'text-emerald-300' : 'text-gray-500']"
                >
                  {{ hasResell(vente) ? formatEuro(prixResellOf(vente)) : '—' }}
                </span>
              </td>

              <td class="px-4 py-3 text-center">
                <span class="text-xs text-gray-300">
                  {{ formatDate(vente.dateAchat ?? vente.date_achat) }}
                </span>
              </td>

              <td class="px-4 py-3 text-center">
                <span class="text-xs" :class="isVendue(vente) ? 'text-gray-300' : 'text-gray-500'">
                  {{ isVendue(vente) ? formatDate(vente.dateVente ?? vente.date_vente) : '—' }}
                </span>
              </td>

              <td class="px-4 py-3 text-center">
                <span
                  class="inline-flex items-center justify-center rounded-full px-3 py-1 text-[0.7rem] font-medium"
                  :class="
                    isVendue(vente)
                      ? 'bg-emerald-500/10 text-emerald-300 border border-emerald-500/40'
                      : 'bg-slate-800 text-gray-300 border border-slate-700'
                  "
                >
                  {{ isVendue(vente) ? 'Vendue' : 'En stock' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'

const ventes = ref<any[]>([])
const loading = ref(false)

const chargerDernieresVentes = async () => {
  loading.value = true
  try {
    // adapte le nom de la méthode à ton service :
    // soit getSnkVenteRecent(), soit un getSnkVente() + slice(0,10)
    if (typeof (SnkVenteServices as any).getSnkVenteRecent === 'function') {
      const { data } = await (SnkVenteServices as any).getSnkVenteRecent()
      ventes.value = Array.isArray(data) ? data : []
    } else {
      const { data } = await SnkVenteServices.getSnkVente()
      const all = Array.isArray(data) ? data : []
      ventes.value = all.slice(0, 10)
    }
  } catch (e) {
    console.error('Erreur chargement dernières ventes', e)
    ventes.value = []
  } finally {
    loading.value = false
  }
}

onMounted(chargerDernieresVentes)

const isVendue = (v: any) => Boolean(v.dateVente ?? v.date_vente)
const prixRetailOf = (v: any) => Number(v.prixRetail ?? v.prix_retail ?? 0)
const prixResellOf = (v: any) => Number(v.prixResell ?? v.prix_resell ?? 0)
const hasResell = (v: any) => !Number.isNaN(prixResellOf(v)) && prixResellOf(v) > 0

const formatEuro = (value: number) =>
  new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(value || 0)

const formatDate = (value: string | Date | null | undefined) => {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '—'
  return d.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
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
