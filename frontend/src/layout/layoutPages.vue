<template>
  <div class="min-h-screen text-gray-900 font-poppins">
    <!-- Header -->
    <header class="sticky top-0 z-40 bg-gray-800 backdrop-blur border-b">
      <div class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 h-14 flex gap-4">
        <!-- Left: brand -->

        <!-- Desktop nav -->
        <nav class="flex flex-1 items-center justify-center gap-6">
          <RouterLink :to="'/'" class="nav" :class="isActive('/')">Accueil</RouterLink>
          <RouterLink :to="'/stats'" class="nav" :class="isActive('/stats')">Stats</RouterLink>
          <RouterLink :to="'/gestion'" class="nav" :class="isActive('/gestion')"
            >Gestion</RouterLink
          >
        </nav>

        <!-- Burger -->
        <button class="ml-auto p-2 -mr-2" @click="open = !open" aria-label="Menu">☰</button>
      </div>

      <!-- Mobile drawer -->
      <transition name="fade">
        <div v-show="open" class="md:hidden border-t bg-white">
          <nav class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-3 space-y-1">
            <RouterLink
              @click="open = false"
              :to="'/'"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              :class="isActive('/')"
              >Accueil</RouterLink
            >
            <RouterLink
              @click="open = false"
              :to="'/stats'"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              :class="isActive('/stats')"
              >Stats</RouterLink
            >
            <RouterLink
              @click="open = false"
              :to="'/gestion'"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              :class="isActive('/gestion')"
              >Gestion</RouterLink
            >
            <div class="h-px bg-gray-200 my-2"></div>
            <RouterLink
              @click="open = false"
              to="/login"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              >Login</RouterLink
            >
            <RouterLink
              @click="open = false"
              to="/signup"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              >Créer un compte</RouterLink
            >
          </nav>
        </div>
      </transition>
    </header>

    <!-- body ---------------------------------------------------------------------------------------------->
    <main class="min-h-0 px-4 bg-gray-900">
      <div class="min-h-full max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <slot />
      </div>
    </main>

    <!-- Footer ----------------------------------------------------------------------------------------------->
    <footer class="border-t bg-gray-800 text-white">
      <div
        class="max-w-screen-2xl mx-auto h-14 px-4 sm:px-6 lg:px-8 flex items-center justify-between text-sm"
      >
        <span class="font-jetbrains-mono">&copy; {{ new Date().getFullYear() }} — Sneaknik</span>
        <div class="flex items-center gap-4">
          <a href="#" class="hover:underline">À propos</a>
          <a href="mailto:nathantalvasson@gmail.com" class="hover:underline">contact</a>
          <!--faire page de mail moi meme-->
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const open = ref(false)

const isActive = (path) => (route.path === path ? 'text-purple-600 font-semibold' : 'text-white') // les bouton du haut s
</script>

<style></style>
