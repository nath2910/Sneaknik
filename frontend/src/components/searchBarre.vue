<template>
  <div class="w-full">
    <div
      class="group flex items-center gap-3 bg-gray-800/80 border border-gray-700 rounded-full px-5 py-3 shadow-md transition focus-within:border-purple-400 focus-within:ring-2 focus-within:ring-purple-500/40"
    >
      <svg
        class="w-5 h-5 text-gray-400 group-focus-within:text-purple-300 transition"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M21 21l-4.35-4.35M5 11a6 6 0 1112 0 6 6 0 01-12 0z"
        />
      </svg>

      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        type="text"
        placeholder="Rechercher par modèle, catégorie, description..."
        class="flex-1 bg-transparent border-0 focus:outline-none focus:ring-0 text-sm text-gray-100 placeholder:text-gray-500"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

defineEmits(['update:modelValue'])
</script>
