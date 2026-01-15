  <template>
    <TransitionRoot appear :show="open" as="template">
      <Dialog as="div" class="relative z-50" @close="$emit('close')">
        <TransitionChild
          as="template"
          enter="duration-150 ease-out"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="duration-150 ease-in"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div class="fixed inset-0 bg-black/60 backdrop-blur-sm" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4">
            <TransitionChild
              as="template"
              enter="duration-150 ease-out"
              enterFrom="opacity-0 scale-95"
              enterTo="opacity-100 scale-100"
              leave="duration-150 ease-in"
              leaveFrom="opacity-100 scale-100"
              leaveTo="opacity-0 scale-95"
            >
              <DialogPanel
                class="w-full max-w-3xl rounded-2xl border border-white/10 bg-[#0b1220] shadow-2xl"
              >
                <div class="px-5 py-4 border-b border-white/10 flex items-center">
                  <DialogTitle class="text-white font-semibold text-lg">
                    Ajouter un bloc
                  </DialogTitle>
                  <button class="ml-auto text-white/70 hover:text-white" @click="$emit('close')">
                    ✕
                  </button>
                </div>

                <div class="p-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                  <button
                    v-for="w in widgets"
                    :key="w.type"
                    class="group rounded-2xl border border-white/10 bg-white/5 hover:bg-white/10 transition p-4 text-left"
                    @click="$emit('add', w.type)"
                  >
                    <div class="flex items-center gap-3">
                      <component :is="w.icon" class="w-6 h-6 text-violet-300" />
                      <div class="text-white font-semibold">{{ w.title }}</div>
                    </div>
                    <div class="mt-2 text-sm text-white/60">
                      {{ w.help ?? 'Ajoute ce widget au canvas' }}
                    </div>
                  </button>
                </div>

                <div class="px-5 py-4 border-t border-white/10 text-sm text-white/60">
                  Tip : tu peux déplacer / redimensionner, et le layout se sauvegarde automatiquement.
                </div>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>
  </template>

  <script setup>
  import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
  defineProps({
    open: { type: Boolean, default: false },
    widgets: { type: Array, required: true },
  })
  defineEmits(['close', 'add'])
  </script>
