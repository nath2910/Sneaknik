<template>
  <div class="canvas-root" :data-edit="editMode ? 'true' : 'false'">
    <!-- Dock -->
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
            @click="toggleEditMode"
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
            @click="paletteOpen = true"
            title="Ajouter un bloc"
          >
            <PlusSquare class="w-5 h-5" />
            <span>Ajouter</span>
          </button>

          <div class="dock-sep"></div>

          <div class="dock-section">
            <div class="dock-title">Période</div>

            <label class="dock-field">
              <span>Du</span>
              <input class="date" type="date" v-model="localFrom" />
            </label>

            <label class="dock-field">
              <span>Au</span>
              <input class="date" type="date" v-model="localTo" />
            </label>

            <div class="dock-row">
              <button type="button" class="chip" @click="preset('month')">Mois</button>
              <button type="button" class="chip" @click="preset('ytd')">YTD</button>
              <button type="button" class="chip" @click="preset('year')">Année</button>
            </div>
          </div>

          <div class="dock-sep"></div>

          <div class="dock-section">
            <div class="dock-title">Vue</div>

            <div class="dock-row">
              <button type="button" class="btn btn-icon" @click="zoomOut" title="Dézoomer">
                <Minus class="w-5 h-5" />
              </button>

              <button type="button" class="btn btn-pill" @click="resetZoom" title="Reset zoom">
                {{ Math.round(scale * 100) }}%
              </button>

              <button type="button" class="btn btn-icon" @click="zoomIn" title="Zoomer">
                <PlusSquare class="w-5 h-5" />
              </button>
            </div>

            <div class="dock-row">
              <button type="button" class="btn btn-icon" @click="centerView" title="Centrer">
                <LocateFixed class="w-5 h-5" />
              </button>

              <button
                type="button"
                class="btn btn-icon"
                :disabled="!editMode"
                :class="{ disabled: !editMode }"
                @click="resetLayout"
                title="Reset layout"
              >
                <RotateCcw class="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Canvas -->
    <div ref="viewportEl" class="viewport">
      <div ref="boardEl" class="board">
        <div
          v-for="w in widgets"
          :key="w.id"
          class="widget panzoom-exclude"
          :data-id="w.id"
          :style="widgetStyle(w)"
        >
          <div class="widget__header">
            <div class="widget__title">
              <span class="dot" />
              <span class="title">{{ w.title }}</span>
            </div>

            <div class="widget__actions" v-if="editMode">
              <button type="button" class="iconbtn" title="Réglages" @click="openSettings(w)">
                <Settings class="w-4 h-4" />
              </button>
              <button type="button" class="iconbtn" title="Supprimer" @click="removeWidget(w.id)">
                <Trash2 class="w-4 h-4" />
              </button>
            </div>
          </div>

          <div class="widget__body">
            <component :is="getComp(w.type)" :from="from" :to="to" v-bind="w.props" />
          </div>

          <div class="widget__resize" v-if="editMode" title="Redimensionner" />
        </div>
      </div>
    </div>

    <!-- Palette -->
    <WidgetPalette
      :open="paletteOpen"
      :widgets="palette"
      @close="paletteOpen = false"
      @add="addWidget"
    />

    <!-- Settings -->
    <WidgetSettingsModal
      :open="settingsOpen"
      :title="settingsTitle"
      :fields="settingsFields"
      :model="settingsModel"
      @close="closeSettings"
      @save="applySettings"
    />
  </div>
</template>
<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch, toRefs, computed } from 'vue'
import Panzoom from '@panzoom/panzoom'
import interact from 'interactjs'
import {
  Plus,
  Minus,
  PlusSquare,
  LocateFixed,
  RotateCcw,
  Trash2,
  X,
  Lock,
  LockOpen,
  Settings,
} from 'lucide-vue-next'

import WidgetPalette from './WidgetPalette.vue'
import WidgetSettingsModal from './WidgetSettingsModal.vue'
import { WIDGET_DEFS, getWidgetDef, newWidget } from './widgetRegistry'

let ro: ResizeObserver | null = null
let didInitialCenter = false

type Widget = {
  id: string
  type: string
  title: string
  x: number
  y: number
  w: number
  h: number
  props?: Record<string, unknown>
  z?: number
}

const props = defineProps({
  from: { type: String, required: true },
  to: { type: String, required: true },
})
const emit = defineEmits(['update:from', 'update:to'])
const { from, to } = toRefs(props)

/* ===== Mode édition/figé ===== */
const EDIT_KEY = 'snk_stats_canvas_edit_v1'
const editMode = ref(
  localStorage.getItem(EDIT_KEY) ? localStorage.getItem(EDIT_KEY) === 'true' : true,
)
function persistEditMode() {
  localStorage.setItem(EDIT_KEY, String(editMode.value))
}
function toggleEditMode() {
  editMode.value = !editMode.value
  persistEditMode()
}

/* ===== Dates ===== */
const localFrom = ref(from.value)
const localTo = ref(to.value)

watch(
  () => [from.value, to.value],
  () => {
    localFrom.value = from.value
    localTo.value = to.value
  },
)

watch(localFrom, (v) => {
  if (!v) return
  emit('update:from', v)
  if (v > localTo.value) emit('update:to', v)
})

watch(localTo, (v) => {
  if (!v) return
  emit('update:to', v)
  if (v < localFrom.value) emit('update:from', v)
})

function preset(kind: 'month' | 'ytd' | 'year') {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  const ymd = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`

  if (kind === 'month') {
    localFrom.value = ymd(new Date(now.getFullYear(), now.getMonth(), 1))
    localTo.value = ymd(new Date(now.getFullYear(), now.getMonth() + 1, 0))
  } else if (kind === 'ytd') {
    localFrom.value = ymd(new Date(now.getFullYear(), 0, 1))
    localTo.value = ymd(now)
  } else {
    localFrom.value = ymd(new Date(now.getFullYear(), 0, 1))
    localTo.value = ymd(new Date(now.getFullYear(), 11, 31))
  }
}

/* ===== Dock ===== */
const dockOpen = ref(false)
const dockEl = ref<HTMLElement | null>(null)

function toggleDock() {
  dockOpen.value = !dockOpen.value
}

function onWindowPointerDown(e: PointerEvent) {
  if (!dockOpen.value) return
  const path = (e.composedPath?.() ?? []) as EventTarget[]
  if (dockEl.value && path.includes(dockEl.value)) return
  dockOpen.value = false
}
function onKeyDown(e: KeyboardEvent) {
  if (e.key === 'Escape') dockOpen.value = false
}

/* ===== Panzoom ===== */
const viewportEl = ref<HTMLElement | null>(null)
const boardEl = ref<HTMLElement | null>(null)
let panzoom: any = null
const scale = ref(1)

const onPanzoomChange = () => {
  if (panzoom) scale.value = panzoom.getScale?.() ?? 1
}

/* ===== Layout ===== */
const GRID = 10
const MIN_W = 320
const MIN_H = 220
const BOARD_W = 9000
const BOARD_H = 6000

const clamp = (n: number, a: number, b: number) => Math.max(a, Math.min(b, n))
const snap = (n: number) => Math.round(n / GRID) * GRID

function clampWidget(w: Widget) {
  w.w = clamp(w.w, MIN_W, BOARD_W)
  w.h = clamp(w.h, MIN_H, BOARD_H)
  w.x = clamp(w.x, 0, BOARD_W - w.w)
  w.y = clamp(w.y, 0, BOARD_H - w.h)
}

function applyWidgetDOM(el: HTMLElement, w: Widget) {
  el.style.width = `${w.w}px`
  el.style.height = `${w.h}px`
  el.style.transform = `translate3d(${w.x}px, ${w.y}px, 0)`
}

/* ===== Widgets ===== */
const STORAGE_KEY = 'snk_stats_canvas_layout_v3'
const paletteOpen = ref(false)

const palette = WIDGET_DEFS.map((w) => ({
  type: w.type,
  title: w.title,
  icon: w.icon,
  help: w.help ?? 'Ajoute ce widget au canvas',
}))

function getComp(type: string) {
  return getWidgetDef(type)?.component
}

function loadLayout(): Widget[] | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as Widget[]) : null
  } catch {
    return null
  }
}

const defaultLayout = (): Widget[] => [
  { ...newWidget('kpis', 180, 160) },
  { ...newWidget('line', 180, 380) },
  { ...newWidget('pie', 980, 380) },
  { ...newWidget('bar', 180, 840) },
  { ...newWidget('top', 820, 840) },
]

const widgets = ref<Widget[]>(loadLayout() ?? defaultLayout())

/* save (debounced) */
let saveTimer: number | null = null
function saveLayoutNow() {
  const minimal = widgets.value.map(({ id, type, title, x, y, w, h, props }) => ({
    id,
    type,
    title,
    x,
    y,
    w,
    h,
    props,
  }))
  localStorage.setItem(STORAGE_KEY, JSON.stringify(minimal))
}
function scheduleSave() {
  if (saveTimer) window.clearTimeout(saveTimer)
  saveTimer = window.setTimeout(() => {
    saveLayoutNow()
    saveTimer = null
  }, 250)
}

function widgetStyle(w: Widget) {
  return {
    width: `${w.w}px`,
    height: `${w.h}px`,
    transform: `translate3d(${w.x}px, ${w.y}px, 0)`,
    zIndex: w.z ?? 1,
  }
}

/* ===== Interact ===== */
const interactables = new Map<string, any>()
let zTop = 10

function attachInteract(w: Widget) {
  if (!editMode.value) return
  const el = boardEl.value?.querySelector(`.widget[data-id="${w.id}"]`) as HTMLElement | null
  if (!el) return

  if (interactables.has(w.id)) {
    interactables.get(w.id).unset()
    interactables.delete(w.id)
  }

  const api = interact(el)
    .draggable({
      allowFrom: '.widget',
      ignoreFrom: '.iconbtn, button, a, input, select, textarea, .widget__resize',
      inertia: true,
      listeners: {
        start() {
          w.z = ++zTop
        },
        move(event) {
          const s = panzoom?.getScale?.() ?? 1
          w.x = snap(w.x + event.dx / s)
          w.y = snap(w.y + event.dy / s)
          clampWidget(w)
          applyWidgetDOM(el, w)
        },
        end() {
          scheduleSave()
        },
      },
    })
    .resizable({
      edges: { right: '.widget__resize', bottom: '.widget__resize' },
      inertia: true,
      listeners: {
        start() {
          w.z = ++zTop
        },
        move(event) {
          const s = panzoom?.getScale?.() ?? 1
          w.w = snap(w.w + event.deltaRect.width / s)
          w.h = snap(w.h + event.deltaRect.height / s)
          clampWidget(w)
          applyWidgetDOM(el, w)
        },
        end() {
          scheduleSave()
        },
      },
    })

  interactables.set(w.id, api)
}

function detachInteract(id: string) {
  const api = interactables.get(id)
  if (api) api.unset()
  interactables.delete(id)
}
function detachAllInteract() {
  for (const id of interactables.keys()) detachInteract(id)
}

watch(editMode, async (enabled) => {
  if (!enabled) {
    detachAllInteract()
    return
  }
  await nextTick()
  widgets.value.forEach((w) => attachInteract(w))
})

/* ===== Panzoom math (stable + anti drift) ===== */
function getTransformStable() {
  // 1) meilleur cas
  if (panzoom?.getTransform) {
    const t = panzoom.getTransform()
    const s = Number(t?.scale ?? panzoom.getScale?.() ?? 1)
    return { x: Number(t?.x ?? 0), y: Number(t?.y ?? 0), s }
  }
  // 2) autre API
  const s = Number(panzoom?.getScale?.() ?? 1)
  if (panzoom?.getPan) {
    const p = panzoom.getPan()
    return { x: Number(p?.x ?? 0), y: Number(p?.y ?? 0), s }
  }
  return { x: 0, y: 0, s }
}

/** pan ABSOLU : on calcule un delta => plus jamais de drift */
function panToAbsolute(targetX: number, targetY: number) {
  if (!panzoom) return
  const cur = getTransformStable()
  const dx = targetX - cur.x
  const dy = targetY - cur.y
  panzoom.pan(dx, dy, { force: true } as unknown) // relatif via delta, stable
}

/** board coords sous un point du viewport */
function boardPointFromViewport(vx: number, vy: number) {
  const vp = viewportEl.value
  if (!vp || !panzoom) return { x: BOARD_W / 2, y: BOARD_H / 2 }

  const rect = vp.getBoundingClientRect()
  if (rect.width < 50 || rect.height < 50) return { x: BOARD_W / 2, y: BOARD_H / 2 }

  const t = getTransformStable()
  const x = (vx - t.x) / t.s
  const y = (vy - t.y) / t.s
  if (!Number.isFinite(x) || !Number.isFinite(y)) return { x: BOARD_W / 2, y: BOARD_H / 2 }
  return { x, y }
}

/** centre de la vue courante (où tu es) */
function boardPointFromViewportCenter() {
  const vp = viewportEl.value
  if (!vp) return { x: BOARD_W / 2, y: BOARD_H / 2 }
  const rect = vp.getBoundingClientRect()
  return boardPointFromViewport(rect.width / 2, rect.height / 2)
}

/** bounds widgets */
function widgetsBounds() {
  const list = widgets.value
  if (!list.length) return { cx: BOARD_W / 2, cy: BOARD_H / 2 }

  let minX = Infinity,
    minY = Infinity,
    maxX = -Infinity,
    maxY = -Infinity

  for (const w of list) {
    minX = Math.min(minX, w.x)
    minY = Math.min(minY, w.y)
    maxX = Math.max(maxX, w.x + w.w)
    maxY = Math.max(maxY, w.y + w.h)
  }
  return { cx: (minX + maxX) / 2, cy: (minY + maxY) / 2 }
}

/** centre un point board au milieu du viewport (ABSOLU) */
function centerOn(boardX: number, boardY: number) {
  const vp = viewportEl.value
  if (!vp || !panzoom) return

  const rect = vp.getBoundingClientRect()
  const t = getTransformStable()

  const targetX = rect.width / 2 - boardX * t.s
  const targetY = rect.height / 2 - boardY * t.s

  panToAbsolute(targetX, targetY)
}

function centerView() {
  const { cx, cy } = widgetsBounds()
  centerOn(cx, cy)
}

/** centre seulement quand viewport est prêt */
function safeInitialCenter() {
  if (didInitialCenter) return
  const vp = viewportEl.value
  if (!vp) return
  const rect = vp.getBoundingClientRect()
  if (rect.width < 50 || rect.height < 50) return
  didInitialCenter = true
  centerView()
}

/* ===== Actions ===== */
function removeWidget(id: string) {
  if (!editMode.value) return
  detachInteract(id)
  widgets.value = widgets.value.filter((w) => w.id !== id)
  scheduleSave()
  // ✅ aucun recentrage, jamais
}

function resetLayout() {
  if (!editMode.value) return
  detachAllInteract()
  widgets.value = defaultLayout()
  nextTick(() => {
    widgets.value.forEach((w) => {
      clampWidget(w)
      attachInteract(w)
    })
    centerView()
    scheduleSave()
  })
}

function addWidget(type: string) {
  if (!editMode.value) return
  paletteOpen.value = false

  // ✅ spawn EXACTEMENT là où tu regardes (centre du viewport actuel)
  const p = boardPointFromViewportCenter()

  // on crée d'abord pour connaître w/h
  const w = newWidget(type, 0, 0) as Widget
  w.x = snap(p.x - w.w / 2)
  w.y = snap(p.y - w.h / 2)

  clampWidget(w)
  widgets.value.push(w)

  nextTick(() => {
    attachInteract(w)
    scheduleSave()
    // ✅ on ne bouge pas la caméra (il pop déjà là où tu es)
  })
}

/* ===== Pan/zoom helpers ===== */
function zoomIn() {
  panzoom?.zoomIn?.()
}
function zoomOut() {
  panzoom?.zoomOut?.()
}
function resetZoom() {
  panzoom?.reset?.({ force: true } as unknown)
}

/* ===== Settings ===== */
const settingsOpen = ref(false)
const settingsWidgetId = ref<string | null>(null)

const settingsWidget = computed(
  () => widgets.value.find((x) => x.id === settingsWidgetId.value) ?? null,
)
const settingsDef = computed(() =>
  settingsWidget.value ? getWidgetDef(settingsWidget.value.type) : null,
)

const settingsTitle = computed(() => settingsWidget.value?.title ?? 'Réglages')
const settingsFields = computed(() => settingsDef.value?.settings ?? [])
const settingsModel = computed(() => ({ ...(settingsWidget.value?.props ?? {}) }))

function openSettings(w: Widget) {
  settingsWidgetId.value = w.id
  settingsOpen.value = true
}
function closeSettings() {
  settingsOpen.value = false
  settingsWidgetId.value = null
}
function applySettings(newModel: Record<string, unknown>) {
  const w = settingsWidget.value
  if (!w) return
  w.props = { ...(w.props ?? {}), ...newModel }
  scheduleSave()
  closeSettings()
}

/* ===== Lifecycle ===== */
onMounted(async () => {
  window.addEventListener('pointerdown', onWindowPointerDown)
  window.addEventListener('keydown', onKeyDown)

  panzoom = Panzoom(boardEl.value!, {
    maxScale: 3,
    minScale: 0.15,
    contain: 'outside',
    excludeClass: 'panzoom-exclude',
  })

  viewportEl.value!.addEventListener('wheel', panzoom.zoomWithWheel, { passive: false })
  boardEl.value!.addEventListener('panzoomchange', onPanzoomChange as any)

  // reset propre (évite les vieux transforms)
  panzoom.reset?.({ force: true } as unknown)

  await nextTick()

  widgets.value.forEach((w) => clampWidget(w))
  if (editMode.value) widgets.value.forEach((w) => attachInteract(w))

  ro = new ResizeObserver(() => safeInitialCenter())
  if (viewportEl.value) ro.observe(viewportEl.value)

  // fallback
  safeInitialCenter()
  onPanzoomChange()
})

onBeforeUnmount(() => {
  window.removeEventListener('pointerdown', onWindowPointerDown)
  window.removeEventListener('keydown', onKeyDown)

  if (viewportEl.value && panzoom)
    viewportEl.value.removeEventListener('wheel', panzoom.zoomWithWheel)
  if (boardEl.value) boardEl.value.removeEventListener('panzoomchange', onPanzoomChange as any)

  detachAllInteract()

  if (ro) {
    ro.disconnect()
    ro = null
  }

  panzoom = null
})
</script>

<style scoped>
.canvas-root {
  --bg: #060a12;
  --text: rgba(255, 255, 255, 0.92);
  --text-soft: rgba(255, 255, 255, 0.8);
  --ring: rgba(139, 92, 246, 0.35);
  --btn: rgba(255, 255, 255, 0.06);
  --btn-hover: rgba(255, 255, 255, 0.1);
  --btn-border: rgba(255, 255, 255, 0.12);

  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: var(--bg);
}

.viewport {
  position: absolute;
  inset: 0;
  overflow: hidden;
  cursor: grab;
}
.viewport:active {
  cursor: grabbing;
}

.board {
  width: 9000px;
  height: 6000px;
  position: relative;
  background:
    radial-gradient(circle at 1px 1px, rgba(170, 200, 255, 0.1) 1px, transparent 1.6px) 0 0 / 28px
      28px,
    radial-gradient(circle at 1px 1px, rgba(255, 255, 255, 0.04) 1px, transparent 1.8px) 0 0 / 8px
      8px,
    linear-gradient(180deg, #050812 0%, #040611 100%);
}

.viewport,
.board {
  touch-action: none;
}

.widget {
  position: absolute;
  border-radius: 22px;
  overflow: hidden;
  background: rgba(17, 24, 39, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.45);
  will-change: transform, width, height;
  contain: layout paint style;
  transition:
    border-color 160ms ease,
    box-shadow 160ms ease;
}
.widget:hover {
  border-color: rgba(255, 255, 255, 0.14);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.55);
}

.widget__header {
  height: 44px;
  display: flex;
  align-items: center;
  padding: 0 10px 0 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(10, 15, 30, 0.88);
}

.widget__title {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  user-select: none;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #8b5cf6;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.15);
}
.title {
  color: var(--text);
  font-weight: 650;
  font-size: 0.9rem;
}

.widget__actions {
  margin-left: auto;
  display: inline-flex;
  gap: 8px;
}
.iconbtn {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text);
  display: grid;
  place-items: center;
  transition:
    background 160ms ease,
    border-color 160ms ease;
}
.iconbtn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.16);
}
.iconbtn:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px var(--ring);
}

.widget__body {
  height: calc(100% - 44px);
  padding: 12px;
}

/* ✅ IMPORTANT : en mode édition, on désactive l’interaction interne
   => drag possible en cliquant PARTOUT (même sur le chart) */
.canvas-root[data-edit='true'] .widget__body {
  pointer-events: none;
}
/* ✅ mais on garde les boutons cliquables */
.canvas-root[data-edit='true'] .iconbtn,
.canvas-root[data-edit='true'] .widget__resize {
  pointer-events: auto;
}

.widget__resize {
  position: absolute;
  right: 10px;
  bottom: 10px;
  width: 14px;
  height: 14px;
  border-right: 2px solid rgba(255, 255, 255, 0.55);
  border-bottom: 2px solid rgba(255, 255, 255, 0.55);
  border-radius: 2px;
  opacity: 0.75;
  cursor: nwse-resize;
  touch-action: none;
}

/* Dock (identique à ton style) */
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
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(11, 18, 32, 0.72);
  backdrop-filter: blur(12px);
  color: rgba(255, 255, 255, 0.92);
  box-shadow: 0 14px 40px rgba(0, 0, 0, 0.35);
}
.fab-icon,
.fab-icon * {
  pointer-events: none;
}

.dock-panel {
  width: 320px;
  padding: 12px;
  border-radius: 18px;
  background: rgba(11, 18, 32, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  box-shadow: 0 18px 55px rgba(0, 0, 0, 0.45);
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
.dock-field {
  display: grid;
  grid-template-columns: 44px 1fr;
  gap: 10px;
  align-items: center;
  color: rgba(255, 255, 255, 0.75);
  font-size: 0.9rem;
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
  border: 1px solid var(--btn-border);
  background: var(--btn);
  color: var(--text);
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

.date {
  height: 36px;
  padding: 0 10px;
  border-radius: 12px;
  border: 1px solid var(--btn-border);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-soft);
  color-scheme: dark;
}
.chip {
  height: 32px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid var(--btn-border);
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-soft);
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
