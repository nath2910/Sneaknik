<template>
  <div class="max-w-md mx-auto p-6 bg-gray-900 shadow-lg">
    <!-- Titre -->
    <h2 class="text-3xl font-bold text-center text-purple-500 mb-4">{{ monthName }} {{ year }}</h2>

    <!-- Head de semaine -->
    <div
      class="grid grid-cols-7 text-center text-gray-500 font-medium border-b border-gray-300/60 pb-2 mb-3"
    >
      <div v-for="d in daysOfWeek" :key="d">{{ d }}</div>
    </div>

    <!-- Grille jours -->
    <div class="grid grid-cols-7 gap-3">
      <!-- Remplissage avant (carrés pâles pour garder la même gueule) -->
      <div
        v-for="n in leadingFills"
        :key="'lead-' + n"
        class="aspect-square rounded-2xl bg-white/60 ring-1 ring-black/5 shadow-sm"
      ></div>

      <!-- Jours du mois -->
      <button
        v-for="day in daysInMonth"
        :key="day"
        type="button"
        class="aspect-square rounded-2xl flex items-center justify-center text-base transition-colors shadow-sm ring-1 ring-black/5"
        :class="
          isToday(day)
            ? 'bg-purple-600 text-white font-semibold shadow-md'
            : 'bg-white text-gray-800 hover:bg-purple-50'
        "
        aria-current="date"
        :aria-label="formatAriaDate(year, month, day)"
      >
        {{ day }}
      </button>

      <!-- Remplissage après (carrés pâles) -->
      <div
        v-for="n in trailingFills"
        :key="'trail-' + n"
        class="aspect-square rounded-2xl bg-white/60 ring-1 ring-black/5 shadow-sm"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Aujourd'hui
const today = new Date()
const year = today.getFullYear()
const month = today.getMonth() // 0=janvier … 11=décembre

// En-têtes L M M J V S D (style FR, lundi d’abord)
const daysOfWeek = ['L', 'M', 'M', 'J', 'V', 'S', 'D']

// Nombre de jours du mois courant
const daysInMonth = new Date(year, month + 1, 0).getDate()

// Jour de la semaine du 1er (JS: 0=dim) -> on convertit pour lundi=0
const rawFirst = new Date(year, month, 1).getDay() // 0..6, 0=dim
const adjustedFirstDay = (rawFirst + 6) % 7 // 0..6, 0=lun, 6=dim

// Remplissages pour garder la même gueule (carrés vides en début/fin)
const leadingFills = adjustedFirstDay
const totalCells = leadingFills + daysInMonth
const trailingFills = (7 - (totalCells % 7)) % 7

// Nom du mois (fr)
const monthName = new Intl.DateTimeFormat('fr-FR', { month: 'long' }).format(
  new Date(year, month, 1),
)

// Met en violet le jour d'aujourd'hui
const isToday = (day) =>
  day === today.getDate() && month === today.getMonth() && year === today.getFullYear()

// Accessibilité / label vocal
const formatAriaDate = (y, m, d) =>
  new Intl.DateTimeFormat('fr-FR', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  }).format(new Date(y, m, d))
</script>
