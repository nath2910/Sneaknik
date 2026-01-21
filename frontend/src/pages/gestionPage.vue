<template>
  <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
    <DashboardHeader
      subtitle="Backoffice • Stock Sneaknik"
      title="Gestion des ventes"
      description="Ajoute, modifie, recherche, sélectionne et supprime."
    />

    <section class="space-y-6">
      <StockSummaryRow
        :total-paires="totalPaires"
        :nb-en-stock="nbEnStock"
        :nb-vendues="nbVendues"
        :valeur-stock="valeurStock"
      />

      <div class="flex justify-center">
        <div class="w-full md:w-3/4 lg:w-2/3">
          <SearchBarre v-model="searchTerm" />
        </div>
      </div>

      <!-- Tableau -->
      <div class="bg-gray-800 border border-gray-700 rounded-3xl shadow-lg overflow-hidden">
        <!-- Header tableau -->
        <div class="px-6 py-4 flex items-center justify-between border-b border-gray-700/80">
          <!-- Gauche : titre -->
          <div>
            <h2 class="text-lg font-semibold text-gray-100 leading-tight">Liste des items</h2>
            <p class="text-xs text-gray-400">
              {{ filteredVentes.length }} item(s) trouvée(s)
              <span v-if="selectedIds.length"> • {{ selectedIds.length }} sélectionnée(s) </span>
            </p>
          </div>

          <!-- Droite : actions -->
          <div class="flex items-center gap-2">
            <GestionActionsPanel @vente-ajoutee="handleVenteAjoutee" />
            <div class="[&_button:hover]:bg-red-900">
              <button
                type="button"
                class="px-4 py-2 text-xs rounded bg-red-600 text-white disabled:opacity-60 transition whitespace-nowrap"
                @click="openDeleteBulk"
              >
                Supprimer un item
              </button>
            </div>
          </div>
        </div>

        <!-- Liste -->
        <div class="p-4">
          <div class="max-h-[480px] overflow-y-auto pr-2">
            <afficherTout
              :snkVentes="filteredVentes"
              selectable
              v-model="selectedIds"
              @edit="openEditModal"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Edition -->
    <EditVenteModal v-model="showEditModal" :vente="venteToEdit" @saved="handleVenteUpdated" />

    <!-- Delete modal (unique) -->
    <SupprimerModal
      v-if="showDeleteModal"
      :snkVentes="snkVentes"
      :selectedIds="selectedIds"
      :defaultMode="deleteMode"
      @close="showDeleteModal = false"
      @deleted="handleDeleted"
    />
    <div class="[&_button:hover]:bg-gray-800">
      <CsvImportExportWidget :filteredRows="filteredVentes" @imported="reloadVentes" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useAuthStore } from '@/store/authStore'
import SnkVenteServices from '@/services/SnkVenteServices.js'
import DashboardHeader from '@/components/HeaderDePage.vue'
import StockSummaryRow from '@/components/gestion/GestionRésumeStock.vue'
import SearchBarre from '@/components/gestion/GestionSearchBarre.vue'
import GestionActionsPanel from '@/components/gestion/GestionBlocBoutonAddDelete.vue'
import afficherTout from '@/components/gestion/GestionAfficherTout.vue'
import EditVenteModal from '@/components/gestion/GestionModifierItem.vue'
import SupprimerModal from '@/components/gestion/GestionSupprimerModal.vue'
import CsvImportExportWidget from '@/components/gestion/CsvImportExportWidget.vue'
import { isVendue, prixRetailOf } from '@/utils/snkVente'

const snkVentes = ref([])
const searchTerm = ref('')
const selectedIds = ref([])

const showEditModal = ref(false)
const venteToEdit = ref(null)

const showDeleteModal = ref(false)
const deleteMode = ref('name') // 'name' | 'bulk'

const { user } = useAuthStore()
const currentUser = user

const chargerVentes = async () => {
  if (!currentUser.value) {
    snkVentes.value = []
    selectedIds.value = []
    return
  }

  try {
    const { data } = await SnkVenteServices.getSnkVente()
    snkVentes.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Erreur chargement ventes', e)
    snkVentes.value = []
  }
}

onMounted(chargerVentes)

// Stats
const totalPaires = computed(() => snkVentes.value.length)
const nbVendues = computed(() => snkVentes.value.filter(isVendue).length)
const nbEnStock = computed(() => snkVentes.value.filter((v) => !isVendue(v)).length)
const valeurStock = computed(() =>
  snkVentes.value
    .filter((v) => !isVendue(v))
    .reduce((sum, v) => {
      const prix = prixRetailOf(v)
      if (Number.isNaN(prix)) return sum
      return sum + prix
    }, 0),
)

// Recherche
const filteredVentes = computed(() => {
  const term = (searchTerm.value || '').trim().toLowerCase()
  if (!term) return snkVentes.value

  return snkVentes.value.filter((v) => {
    const idStr = String(v.id ?? '')
    const name = String(v.nomItem ?? v.nom_item ?? '').toLowerCase()
    const cat = String(v.categorie ?? '').toLowerCase()
    const desc = String(v.description ?? '').toLowerCase()
    return idStr.includes(term) || name.includes(term) || cat.includes(term) || desc.includes(term)
  })
})

// Sélection logique : si tu filtres, on garde seulement ce qui est visible
watch(filteredVentes, (list) => {
  const visible = new Set(list.map((v) => v.id))
  selectedIds.value = selectedIds.value.filter((id) => visible.has(id))
})

// Ajout
const handleVenteAjoutee = async () => {
  await chargerVentes()
}

// Edition
const openEditModal = (vente) => {
  venteToEdit.value = { ...vente }
  showEditModal.value = true
}

const handleVenteUpdated = (updated) => {
  const index = snkVentes.value.findIndex((v) => v.id === updated.id)
  if (index !== -1) snkVentes.value[index] = updated
}

const openDeleteBulk = () => {
  deleteMode.value = 'bulk'
  showDeleteModal.value = true
}

const handleDeleted = (ids) => {
  const set = new Set(ids)
  snkVentes.value = snkVentes.value.filter((v) => !set.has(v.id))
  selectedIds.value = selectedIds.value.filter((id) => !set.has(id))
}

const reloadVentes = async () => {
  await chargerVentes()
}
</script>
