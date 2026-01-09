<!-- src/components/AjoutPaire.vue -->
<template>
  <div class="fixed inset-0 z-40">
    <!-- overlay -->
    <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click.self="close"></div>

    <!-- modal -->
    <div class="relative z-10 flex items-center justify-center min-h-full p-4">
      <div
        class="w-full max-w-3xl max-h-[90vh] overflow-y-auto rounded-2xl bg-gray-800 border border-gray-700 shadow-2xl"
      >
        <!-- Header -->
        <div class="flex items-start justify-between p-5 border-b border-gray-700">
          <div>
            <h3 class="text-xl font-semibold text-gray-100">Ajouter une vente</h3>
            <p class="text-sm text-gray-400 mt-1">
              Renseigne les infos de l'item pour l'ajouter dans ton suivi.
            </p>
          </div>

          <!-- croix (hover gris cohérent) -->
          <button
            type="button"
            @click="close"
            class="rounded-lg p-2 text-gray-300 transition hover:bg-gray-700/60 hover:text-white focus:outline-none focus:ring-2 focus:ring-purple-500/30"
            aria-label="Fermer"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <!-- Erreur -->
        <div
          v-if="error"
          class="mx-6 mt-4 rounded-lg border border-red-500/40 bg-red-500/10 p-3 text-sm text-red-200"
        >
          ⚠️ {{ error }}
        </div>

        <!-- Succès -->
        <div
          v-if="success"
          class="mx-6 mt-4 rounded-lg border border-emerald-500/40 bg-emerald-500/10 p-3 text-sm text-emerald-200"
        >
          ✅ Vente ajoutée avec succès.
        </div>

        <!-- Formulaire -->
        <form class="p-6 space-y-6" @submit.prevent="creerVente">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <!-- Nom -->
            <div>
              <label for="nomItem" class="block text-sm font-medium text-gray-200 mb-2">
                Nom de l'item
              </label>
              <input
                type="text"
                id="nomItem"
                v-model="form.nomItem"
                placeholder="Dunk low, Bundle 151, Ruinart ..."
                required
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              />
            </div>

            <!-- Catégorie -->
            <div>
              <label for="categorie" class="block text-sm font-medium text-gray-200 mb-2">
                Catégorie
              </label>
              <input
                type="text"
                id="categorie"
                v-model="form.categorie"
                placeholder="Jordan, Hennessy, One Piece ..."
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              />
            </div>

            <!-- Prix retail -->
            <div>
              <label for="prixRetail" class="block text-sm font-medium text-gray-200 mb-2">
                Prix Retail (€)
              </label>
              <input
                type="number"
                id="prixRetail"
                v-model.number="form.prixRetail"
                placeholder="110 €"
                min="0"
                step="0.01"
                required
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              />
            </div>

            <!-- Prix resell -->
            <div>
              <label for="prixResell" class="block text-sm font-medium text-gray-200 mb-2">
                Prix Revente (€)
              </label>
              <input
                type="number"
                id="prixResell"
                v-model.number="form.prixResell"
                placeholder="180 €"
                min="0"
                step="0.01"
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              />
            </div>

            <!-- Date achat -->
            <div>
              <label for="dateAchat" class="block text-sm font-medium text-gray-200 mb-2">
                Date d'achat
              </label>
              <input
                type="date"
                id="dateAchat"
                v-model="form.dateAchat"
                required
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              />
            </div>

            <!-- Date vente -->
            <div>
              <label for="dateVente" class="block text-sm font-medium text-gray-200 mb-2">
                Date de vente
              </label>
              <input
                type="date"
                id="dateVente"
                v-model="form.dateVente"
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              />
              <p class="mt-1 text-xs text-gray-500">Laisse vide si pas vendue.</p>
            </div>

            <!-- Description -->
            <div class="sm:col-span-2">
              <label for="description" class="block text-sm font-medium text-gray-200 mb-2">
                Description
              </label>
              <textarea
                id="description"
                v-model="form.description"
                rows="3"
                placeholder="Etat, taille, notes perso..."
                class="w-full rounded-lg border border-gray-600 bg-gray-900 text-gray-100 px-3 py-2.5 text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500/50 focus:border-purple-500"
              ></textarea>
            </div>
          </div>

          <!-- Footer -->
          <div class="pt-4 border-t border-gray-700 flex items-center justify-end gap-2">
            <button
              type="button"
              @click="close"
              class="px-4 py-2 text-sm rounded-lg border border-gray-600 text-gray-200 hover:bg-gray-700/50 transition disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="loading"
            >
              Annuler
            </button>

            <button
              type="submit"
              :disabled="loading"
              class="px-5 py-2 text-sm rounded-lg text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-emerald-500/40 disabled:opacity-60 disabled:cursor-not-allowed transition whitespace-nowrap inline-flex items-center gap-2"
            >
              <span>{{ loading ? 'Enregistrement...' : 'Enregistrer la vente' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'
import { useAuthStore } from '@/store/authStore'
const authStore = useAuthStore()
const currentUser = authStore.user

const emit = defineEmits(['close', 'added'])

const getToday = () => new Date().toISOString().split('T')[0]

const emptyForm = () => ({
  nomItem: '',
  prixRetail: null,
  prixResell: null,
  dateAchat: getToday(),
  dateVente: null,
  description: '',
  categorie: '',
})

const form = ref(emptyForm())
const loading = ref(false)
const success = ref(false)
const error = ref(null)

const close = () => {
  form.value = emptyForm()
  success.value = false
  error.value = null
  emit('close')
}

const creerVente = async () => {
  loading.value = true
  success.value = false
  error.value = null

  try {
    await SnkVenteServices.ajouter({
      nomItem: form.value.nomItem,
      prixRetail: form.value.prixRetail,
      prixResell: form.value.prixResell,
      dateAchat: form.value.dateAchat,
      dateVente: form.value.dateVente,
      description: form.value.description,
      categorie: form.value.categorie,
      user: currentUser.value ? { id: currentUser.value.id } : null,
      // tu pourras plus tard ajouter userId ici si tu gères l’auth
    })

    success.value = true
    emit('added')

    setTimeout(() => {
      success.value = false
      form.value = emptyForm()
      close()
    }, 1000)
  } catch (err) {
    error.value = err.response?.data?.message || 'Erreur lors de la création de la vente'
    console.error('Erreur:', err)
  } finally {
    loading.value = false
  }
}
</script>
