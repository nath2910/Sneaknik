<template>
  <div ref="dockEl" class="dock">
    <button
      type="button"
      class="fab"
      @click.stop="toggleDock"
      @pointerdown.stop
      :aria-expanded="dockOpen"
      title="Outils"
    >
      <Plus v-if="!dockOpen" class="fab-icon w-6 h-6" />
      <X v-else class="fab-icon w-6 h-6" />
    </button>

    <Transition name="dock">
      <div v-if="dockOpen" class="dock-panel" @click.stop @pointerdown.stop>
        <button
          type="button"
          class="dock-btn"
          @click="$emit('toggleEdit')"
          title="Mode édition / figé"
        >
          <component :is="editMode ? LockOpen : Lock" class="w-5 h-5" />
          <span>{{ editMode ? 'Mode édition' : 'Mode figé' }}</span>
        </button>

        <div class="dock-sep"></div>

        <button
          type="button"
          class="dock-btn"
          :disabled="!editMode"
          :class="{ disabled: !editMode }"
          @click="$emit('openPalette')"
          title="Ajouter un bloc"
        >
          <PlusSquare class="w-5 h-5" />
          <span>Ajouter</span>
        </button>

        <div class="dock-section">
          <div class="dock-title">Vue</div>

          <div class="dock-row">
            <button type="button" class="btn btn-icon" @click="$emit('zoomOut')" title="Dézoomer">
              <Minus class="w-5 h-5" />
            </button>

            <button
              type="button"
              class="btn btn-pill"
              @click="$emit('resetZoom')"
              title="Reset zoom"
            >
              {{ Math.round(scale * 100) }}%
            </button>

            <button type="button" class="btn btn-icon" @click="$emit('zoomIn')" title="Zoomer">
              <PlusSquare class="w-5 h-5" />
            </button>
          </div>

          <div class="dock-row">
            <button type="button" class="btn btn-icon" @click="$emit('centerView')" title="Centrer">
              <LocateFixed class="w-5 h-5" />
            </button>

            <button
              type="button"
              class="btn btn-icon"
              :disabled="!editMode"
              :class="{ disabled: !editMode }"
              @click="$emit('resetLayout')"
              title="Tout supprimer"
            >
              <RotateCcw class="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { Plus, Minus, PlusSquare, LocateFixed, RotateCcw, X, Lock, LockOpen } from 'lucide-vue-next'

const props = defineProps<{
  editMode: boolean
  scale: number
  paletteOpen?: boolean
}>()

defineEmits<{
  (e: 'toggleEdit'): void
  (e: 'openPalette'): void
  (e: 'zoomIn'): void
  (e: 'zoomOut'): void
  (e: 'resetZoom'): void
  (e: 'centerView'): void
  (e: 'resetLayout'): void
}>()

const dockOpen = ref(false)
const dockEl = ref<HTMLElement | null>(null)

function toggleDock() {
  dockOpen.value = !dockOpen.value
}

watch(
  () => props.paletteOpen,
  (open) => {
    if (open) dockOpen.value = false
  },
)

function onWindowPointerDown(e: PointerEvent) {
  if (!dockOpen.value) return
  const path = (e.composedPath?.() ?? []) as EventTarget[]
  if (dockEl.value && path.includes(dockEl.value)) return
  dockOpen.value = false
}
function onKeyDown(e: KeyboardEvent) {
  if (e.key === 'Escape') dockOpen.value = false
}

onMounted(() => {
  window.addEventListener('pointerdown', onWindowPointerDown)
  window.addEventListener('keydown', onKeyDown)
})
onBeforeUnmount(() => {
  window.removeEventListener('pointerdown', onWindowPointerDown)
  window.removeEventListener('keydown', onKeyDown)
})
</script>

<style scoped>
.dock {
  position: fixed;
  top: 86px;
  right: 18px;
  z-index: 60;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.fab {
  width: 46px;
  height: 46px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(2, 6, 23, 0.4);
  backdrop-filter: blur(12px);
  color: rgba(255, 255, 255, 0.92);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}
.fab-icon,
.fab-icon * {
  pointer-events: none;
}

.dock-panel {
  width: 320px;
  padding: 12px;
  border-radius: 18px;
  background: rgba(2, 6, 23, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}
.dock-btn {
  width: 100%;
  height: 42px;
  padding: 0 12px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.92);
}
.dock-btn span {
  font-weight: 650;
  font-size: 0.95rem;
}
.dock-btn.disabled,
.dock-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.dock-sep {
  height: 1px;
  margin: 12px 0;
  background: rgba(255, 255, 255, 0.08);
}
.dock-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.dock-title {
  font-size: 0.78rem;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.55);
}
.dock-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.btn {
  height: 38px;
  padding: 0 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.92);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn-icon {
  width: 40px;
  padding: 0;
}
.btn-pill {
  width: 84px;
  font-variant-numeric: tabular-nums;
}
.btn.disabled,
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.chip {
  height: 32px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.82rem;
}

/* Anim */
.dock-enter-active,
.dock-leave-active {
  transition:
    opacity 180ms ease,
    transform 200ms ease;
}
.dock-enter-from,
.dock-leave-to {
  opacity: 0;
  transform: translateX(10px) scale(0.98);
}
</style>
