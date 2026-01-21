import Panzoom from '@panzoom/panzoom'
import type { Ref } from 'vue'
import { ref } from 'vue'

type InitOptions = {
  boardWidth: number
  boardHeight: number
  maxScale?: number
  minScale?: number
  contain?: any
  excludeClass?: string
}

export function useCanvasCamera(
  viewportEl: Ref<HTMLElement | null>,
  boardEl: Ref<HTMLElement | null>,
  opts: InitOptions,
) {
  const scale = ref(1)
  let panzoom: any = null
  let ro: ResizeObserver | null = null
  let didReady = false

  const BOARD_W = opts.boardWidth
  const BOARD_H = opts.boardHeight

  function getRects() {
    const vp = viewportEl.value
    const board = boardEl.value
    if (!vp || !board) return null

    const vpRect = vp.getBoundingClientRect()
    const boardRect = board.getBoundingClientRect()
    if (vpRect.width < 50 || vpRect.height < 50) return null
    if (boardRect.width < 50 || boardRect.height < 50) return null

    // ✅ échelle VISUELLE réelle (hyper robuste)
    const s = boardRect.width / BOARD_W
    if (!Number.isFinite(s) || s <= 0) return null

    return { vpRect, boardRect, s }
  }

  function onPanzoomChange() {
    if (!panzoom) return
    scale.value = Number(panzoom.getScale?.() ?? 1)
  }

  function boardPointFromViewport(vx: number, vy: number) {
    const r = getRects()
    if (!r) return { x: BOARD_W / 2, y: BOARD_H / 2 }

    // vx/vy = coords dans le viewport (0..width / 0..height)
    const x = (r.vpRect.left + vx - r.boardRect.left) / r.s
    const y = (r.vpRect.top + vy - r.boardRect.top) / r.s

    return {
      x: Number.isFinite(x) ? x : BOARD_W / 2,
      y: Number.isFinite(y) ? y : BOARD_H / 2,
    }
  }

  function boardPointFromViewportCenter() {
    const r = getRects()
    if (!r) return { x: BOARD_W / 2, y: BOARD_H / 2 }
    return boardPointFromViewport(r.vpRect.width / 2, r.vpRect.height / 2)
  }

  /** centre un point board au centre du viewport (pan RELATIF => zéro drift) */
  function centerOn(boardX: number, boardY: number) {
    if (!panzoom) return
    const r = getRects()
    if (!r) return

    const px = r.boardRect.left + boardX * r.s - r.vpRect.left
    const py = r.boardRect.top + boardY * r.s - r.vpRect.top

    const dx = r.vpRect.width / 2 - px
    const dy = r.vpRect.height / 2 - py

    panzoom.pan(dx, dy, { relative: true, force: true } as any)
  }

  function zoomIn() {
    panzoom?.zoomIn?.()
  }
  function zoomOut() {
    panzoom?.zoomOut?.()
  }
  function resetZoom() {
    panzoom?.reset?.({ force: true } as any)
  }

  function init(onReadyOnce?: () => void) {
    const board = boardEl.value
    const vp = viewportEl.value
    if (!board || !vp) return

    panzoom = Panzoom(board, {
      maxScale: opts.maxScale ?? 3,
      minScale: opts.minScale ?? 0.15,
      contain: opts.contain ?? 'outside',
      excludeClass: opts.excludeClass ?? 'panzoom-exclude',
      canvas: true, // ✅ Lucidchart feel (drag la “feuille”)
      cursor: 'grab',

      handleStartEvent: (e: Event) => {
        e.preventDefault()
        // PAS de e.stopPropagation()
      },
    })

    vp.addEventListener('wheel', panzoom.zoomWithWheel, { passive: false })
    board.addEventListener('panzoomchange', onPanzoomChange as any)

    // reset propre (évite les anciens transforms)
    panzoom.reset?.({ force: true } as any)
    onPanzoomChange()

    // ✅ ready = quand le viewport a une vraie taille
    didReady = false
    ro = new ResizeObserver(() => {
      if (didReady) return
      const r = getRects()
      if (!r) return
      didReady = true

      // double RAF = layout ultra stable
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          onReadyOnce?.()
        })
      })
    })
    ro.observe(vp)
  }

  function destroy() {
    const board = boardEl.value
    const vp = viewportEl.value

    if (ro) {
      ro.disconnect()
      ro = null
    }
    if (vp && panzoom) vp.removeEventListener('wheel', panzoom.zoomWithWheel)
    if (board) board.removeEventListener('panzoomchange', onPanzoomChange as any)

    panzoom = null
  }

  return {
    scale,
    init,
    destroy,
    zoomIn,
    zoomOut,
    resetZoom,
    centerOn,
    boardPointFromViewport,
    boardPointFromViewportCenter,
    getPanzoom: () => panzoom,
  }
}
