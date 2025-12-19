<template>
  <div class="overflow-x-auto">
    <table class="min-w-full text-sm text-gray-100">
      <thead class="bg-gray-900 border-b border-gray-700">
        <tr>
          <th
            class="px-4 py-3 text-left font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            id
          </th>
          <th
            class="px-4 py-3 text-left font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            nom de l'item
          </th>
          <th
            class="px-4 py-3 text-left font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            categorie
          </th>
          <th
            class="px-4 py-3 text-right font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            prix_retail
          </th>
          <th
            class="px-4 py-3 text-right font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            prix_resell
          </th>
          <th
            class="px-4 py-3 text-center font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            date achat
          </th>
          <th
            class="px-4 py-3 text-center font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            date vente
          </th>
          <th
            class="px-4 py-3 text-right font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            profit
          </th>
          <th
            class="px-4 py-3 text-center font-semibold text-xs uppercase tracking-wide text-gray-400"
          >
            actions
          </th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="vente in snkVentes"
          :key="vente.id"
          class="border-b border-gray-800 hover:bg-gray-900/70 transition"
        >
          <td class="px-4 py-3 text-xs text-gray-300">
            {{ vente.id }}
          </td>

          <td class="px-4 py-3">
            <div class="flex flex-col">
              <span class="font-medium text-gray-100">
                {{ vente.nomItem || vente.nom_item }}
              </span>
              <span v-if="vente.description" class="text-[11px] text-gray-400 line-clamp-1">
                {{ vente.description }}
              </span>
            </div>
          </td>

          <td class="px-4 py-3">
            <span
              class="inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-medium"
              :class="badgeClass(vente.categorie)"
            >
              {{ vente.categorie || '—' }}
            </span>
          </td>

          <td class="px-4 py-3 text-right">
            {{ formatCurrency(vente.prixRetail ?? vente.prix_retail) }}
          </td>

          <td class="px-4 py-3 text-right">
            {{ formatCurrency(vente.prixResell ?? vente.prix_resell) }}
          </td>

          <td class="px-4 py-3 text-center text-xs text-gray-300">
            {{ formatDate(vente.dateAchat ?? vente.date_achat) }}
          </td>

          <td class="px-4 py-3 text-center text-xs text-gray-300">
            {{
              vente.dateVente || vente.date_vente
                ? formatDate(vente.dateVente ?? vente.date_vente)
                : '—'
            }}
          </td>

          <td class="px-4 py-3 text-right">
            <span
              class="font-semibold"
              :class="profit(vente) >= 0 ? 'text-emerald-400' : 'text-red-400'"
            >
              {{ formatCurrency(profit(vente)) }}
            </span>
          </td>

          <!-- Actions -->
          <td class="px-4 py-3 text-center">
            <button
              type="button"
              class="inline-flex items-center justify-center w-8 h-8 rounded-full bg-gray-800 border border-purple-500/60 text-purple-200 hover:bg-purple-600/20 hover:border-purple-400 transition"
              @click="$emit('edit', vente)"
            >
              <!-- icône crayon -->
              <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15.232 5.232l3.536 3.536M4 20h4.586a1 1 0 00.707-.293l9.414-9.414a2 2 0 000-2.828l-2.172-2.172a2 2 0 00-2.828 0L4 14.586A1 1 0 003.707 15.293L4 20z"
                />
              </svg>
            </button>
          </td>
        </tr>

        <tr v-if="!snkVentes.length">
          <td colspan="9" class="px-4 py-8 text-center text-sm text-gray-400">
            Aucune paire à afficher pour le moment.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
defineProps({
  snkVentes: {
    type: Array,
    required: true,
  },
})

defineEmits(['edit'])

const formatCurrency = (val) => {
  const num = Number(val)
  if (!val || isNaN(num)) return '—'
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: 'EUR',
    maximumFractionDigits: 0,
  }).format(num)
}

const formatDate = (val) => {
  if (!val) return '—'
  return new Date(val).toLocaleDateString('fr-FR')
}

const profit = (vente) => {
  const retail = Number(vente.prixRetail ?? vente.prix_retail ?? 0)
  const resell = Number(vente.prixResell ?? vente.prix_resell ?? 0)
  if (isNaN(retail) || isNaN(resell)) return 0
  return resell - retail
}

const badgeClass = (cat) => {
  if (!cat) return 'bg-gray-700 text-gray-100 border border-gray-500/60'
  const c = cat.toLowerCase()
  if (c.includes('jordan') || c.includes('sneakers') || c.includes('basket')) {
    return 'bg-purple-500/10 text-purple-200 border border-purple-400/60'
  }
  if (c.includes('running')) {
    return 'bg-emerald-500/10 text-emerald-200 border border-emerald-400/60'
  }
  return 'bg-gray-700 text-gray-100 border border-gray-500/60'
}
</script>
