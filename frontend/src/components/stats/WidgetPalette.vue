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
        <div class="fixed inset-0 bg-black/45 backdrop-blur-md" />
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
            <DialogPanel class="glass-panel w-full max-w-3xl rounded-[22px]">
              <div class="glass-header h-11 px-4 flex items-center">
                <DialogTitle class="text-white/90 font-semibold text-[15px]">
                  Ajouter un widget
                </DialogTitle>
                <button
                  class="glass-iconbtn ml-auto h-8 w-8 grid place-items-center rounded-xl"
                  @click="$emit('close')"
                >
                  <span class="close-x" aria-hidden="true"></span>
                </button>
              </div>

              <div class="p-4 grid gap-5">
                <section v-for="group in groups" :key="group.title" class="grid gap-3">
                  <div class="flex items-center gap-2">
                    <span
                      class="group-dot"
                      :style="{
                        background: group.color || '#94a3b8',
                        boxShadow: `0 0 0 3px ${group.glow || 'rgba(148, 163, 184, 0.2)'}`,
                      }"
                    />
                    <div class="text-white/80 text-xs tracking-[0.2em] uppercase">
                      {{ group.title }}
                    </div>
                    <div class="text-white/40 text-xs">({{ group.items.length }})</div>
                  </div>

                  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                    <button
                      v-for="w in group.items"
                      :key="w.type"
                      class="glass-card group rounded-2xl transition p-4 text-left"
                      @click="onWidgetClick(w)"
                    >
                      <div class="flex items-center gap-3">
                        <component :is="w.icon" class="w-6 h-6" :style="{ color: group.color }" />
                        <div class="text-white font-semibold">{{ w.title }}</div>
                      </div>
                      <div class="mt-2 text-sm text-white/60">
                        {{ w.help ?? 'Ajoute ce widget au canvas' }}
                      </div>
                    </button>
                  </div>
                </section>
              </div>

              <div class="px-4 py-3 border-t border-white/10 text-[12px] text-white/55">
                Tip : tu peux deplacer / redimensionner, et le layout se sauvegarde automatiquement.
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>

  <teleport to="body">
    <div v-if="formOpen" class="form-overlay" aria-modal="true" role="dialog">
      <div class="form-backdrop" @click="closeFormPicker"></div>
      <div class="form-panel glass-panel rounded-[22px]" @click.stop>
        <div class="glass-header h-11 px-4 flex items-center">
          <div class="text-white/90 font-semibold text-[15px]">Choisir l'affichage</div>
          <button
            class="glass-iconbtn ml-auto h-8 w-8 grid place-items-center rounded-xl"
            @click.stop="closeFormPicker"
          >
            <span class="close-x" aria-hidden="true"></span>
          </button>
        </div>

        <div class="p-4 grid gap-4">
          <div v-if="pendingWidget" class="text-white/70 text-sm">
            {{ pendingWidget.title }}
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <button
              v-for="form in formList"
              :key="form.key"
              class="form-card text-left"
              @click="selectForm(form.key)"
            >
              <div class="form-icon" :style="{ color: form.color }">
                <component :is="form.icon" class="w-5 h-5" />
              </div>
              <div class="form-body">
                <div class="form-title">{{ form.label }}</div>
                <div class="form-hint">{{ form.hint }}</div>
              </div>
            </button>
          </div>

          <div class="text-[11px] text-white/45">
            Astuce : le camembert est ideal pour 6 categories maximum.
          </div>
        </div>

        <div class="px-4 py-3 border-t border-white/10 text-[12px] text-white/55">
          Choisis une forme lisible et adaptee a la donnee.
        </div>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { computed, ref } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { Hash, BarChart3, PieChart, Target, LineChart } from 'lucide-vue-next'
defineProps({
  open: { type: Boolean, default: false },
  groups: { type: Array, required: true },
})
const emit = defineEmits(['close', 'add'])

const FORM_OPTIONS = {
  number: {
    key: 'number',
    label: 'Chiffre',
    hint: 'KPI simple et direct',
    icon: Hash,
    color: '#38BDF8',
  },
  bars: {
    key: 'bars',
    label: 'Barres',
    hint: 'Comparer des categories',
    icon: BarChart3,
    color: '#F59E0B',
  },
  pie: {
    key: 'pie',
    label: 'Camembert',
    hint: 'Part du total',
    icon: PieChart,
    color: '#A855F7',
  },
  target: {
    key: 'target',
    label: 'Barre objectif',
    hint: 'KPI + seuil',
    icon: Target,
    color: '#22C55E',
  },
  line: {
    key: 'line',
    label: 'Courbe',
    hint: 'Evolution dans le temps',
    icon: LineChart,
    color: '#60A5FA',
  },
  treemap: {
    key: 'treemap',
    label: 'Treemap',
    hint: 'Surface proportionnelle',
    icon: BarChart3,
    color: '#34D399',
  },
  heatmap: {
    key: 'heatmap',
    label: 'Heatmap',
    hint: 'Intensite par categorie',
    icon: Target,
    color: '#F97316',
  },
}

const formOpen = ref(false)
const pendingWidget = ref(null)

const formList = computed(() => {
  const forms = pendingWidget.value?.forms ?? []
  if (!forms.length) return []
  return forms.map(
    (key) =>
      FORM_OPTIONS[key] ?? { key, label: key, hint: '', icon: Hash, color: '#94A3B8' },
  )
})

function closeFormPicker() {
  formOpen.value = false
  pendingWidget.value = null
}

function onWidgetClick(w) {
  if (w?.formPicker === false) {
    emit('add', w.type)
    return
  }
  const forms = Array.isArray(w?.forms) && w.forms.length ? w.forms : ['number']
  pendingWidget.value = { ...w, forms }
  formOpen.value = true
}

function selectForm(key) {
  if (!pendingWidget.value) return
  emit('add', { type: pendingWidget.value.type, view: key })
  closeFormPicker()
}
</script>

<style scoped>
.group-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
}
.glass-panel {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: rgba(8, 10, 16, 0.62);
  backdrop-filter: blur(24px) saturate(140%) contrast(105%);
  box-shadow:
    0 25px 70px rgba(0, 0, 0, 0.65),
    inset 0 1px 0 rgba(255, 255, 255, 0.18);
}
.glass-panel > * {
  position: relative;
  z-index: 1;
}
.glass-panel::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.08), transparent 55%);
  opacity: 0.5;
  pointer-events: none;
}
.glass-panel::after {
  content: '';
  position: absolute;
  inset: -1px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.06), transparent 45%);
  opacity: 0.35;
  pointer-events: none;
}
.glass-header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(10, 12, 18, 0.55);
  backdrop-filter: blur(16px) saturate(140%);
}
.glass-card {
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(10, 12, 18, 0.48);
  backdrop-filter: blur(16px) saturate(130%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.18),
    0 8px 20px rgba(0, 0, 0, 0.25);
}
.glass-card::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.1), transparent 60%);
  pointer-events: none;
}
.glass-iconbtn {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.18);
}
.glass-iconbtn:hover {
  background: rgba(255, 255, 255, 0.14);
}
.glass-iconbtn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.14), transparent 60%);
  opacity: 0.35;
  pointer-events: none;
}
.close-x {
  position: relative;
  width: 12px;
  height: 12px;
  display: inline-block;
}
.close-x::before,
.close-x::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 12px;
  height: 2px;
  background: currentColor;
  transform-origin: center;
}
.close-x::before {
  transform: translate(-50%, -50%) rotate(45deg);
}
.close-x::after {
  transform: translate(-50%, -50%) rotate(-45deg);
}

.form-overlay {
  position: fixed;
  inset: 0;
  z-index: 80;
}
.form-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(6px);
}
.form-panel {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: min(560px, 92vw);
  box-shadow:
    0 30px 80px rgba(0, 0, 0, 0.55),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}
.form-card {
  display: grid;
  grid-template-columns: 36px 1fr;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(12, 16, 24, 0.55);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.08),
    0 8px 18px rgba(0, 0, 0, 0.35);
  transition:
    transform 140ms ease,
    border-color 160ms ease,
    background 160ms ease;
}
.form-card:hover {
  transform: translateY(-1px);
  border-color: rgba(148, 163, 184, 0.35);
  background: rgba(18, 24, 36, 0.7);
}
.form-icon {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
}
.form-title {
  font-weight: 600;
  color: rgba(226, 232, 240, 0.95);
  font-size: 0.95rem;
}
.form-hint {
  font-size: 0.74rem;
  color: rgba(148, 163, 184, 0.7);
}
</style>
