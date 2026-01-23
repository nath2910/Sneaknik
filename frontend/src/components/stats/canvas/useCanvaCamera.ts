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
  let wheelHandler: ((e: WheelEvent) => void) | null = null
  let touchStartHandler: ((e: TouchEvent) => void) | null = null
  let touchEndHandler: ((e: TouchEvent) => void) | null = null
  let touchZoomOverride = false

  const BOARD_W = opts.boardWidth
  const BOARD_H = opts.boardHeight
  const EXCLUDE_CLASS = opts.excludeClass ?? 'panzoom-exclude'
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

  function findScrollableAncestor(target: HTMLElement | null, stopAt: HTMLElement | null) {
    let el = target
    while (el && el !== stopAt && el !== document.body) {
      const style = window.getComputedStyle(el)
      const overflowY = style.overflowY
      const overflowX = style.overflowX
      const canScrollY =
        (overflowY === 'auto' || overflowY === 'scroll') && el.scrollHeight > el.clientHeight
      const canScrollX =
        (overflowX === 'auto' || overflowX === 'scroll') && el.scrollWidth > el.clientWidth
      if (canScrollY || canScrollX) return el
      el = el.parentElement
    }
    return null
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
    if (!panzoom) return
    const anchor = boardPointFromViewportCenter()
    panzoom.zoomIn?.()
    centerOn(anchor.x, anchor.y)
  }
  function zoomOut() {
    if (!panzoom) return
    const anchor = boardPointFromViewportCenter()
    panzoom.zoomOut?.()
    centerOn(anchor.x, anchor.y)
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
      excludeClass: EXCLUDE_CLASS,
      canvas: true, // ✅ Lucidchart feel (drag la “feuille”)
      cursor: 'grab',

      handleStartEvent: (e: Event) => {
        e.preventDefault()
        // PAS de e.stopPropagation()
      },
    })

    wheelHandler = (e: WheelEvent) => {
      if (!panzoom) return

      const isZoomGesture = e.ctrlKey || e.metaKey
      const isMouseWheel =
        e.deltaMode !== 0 || (Number.isInteger(e.deltaY) && Math.abs(e.deltaY) >= 50)
      if (isZoomGesture || isMouseWheel) {
        e.preventDefault()
        panzoom.zoomWithWheel?.(e)
        return
      }

      const activeExclude = panzoom?.getOptions?.().excludeClass ?? EXCLUDE_CLASS
      const target = e.target as HTMLElement | null
      if (target && activeExclude && target.closest(`.${activeExclude}`)) return
      if (target && target.closest('input, textarea, select, option, [contenteditable="true"]')) {
        return
      }
      if (findScrollableAncestor(target, vp)) return

      e.preventDefault()
      panzoom.pan(e.deltaX, e.deltaY, { relative: true, force: true } as any)
    }

    vp.addEventListener('wheel', wheelHandler, { passive: false })
    board.addEventListener('panzoomchange', onPanzoomChange as any)

    touchStartHandler = (e: TouchEvent) => {
      if (!panzoom) return
      if (e.touches && e.touches.length >= 2 && !touchZoomOverride) {
        panzoom.setOptions?.({ excludeClass: null })
        touchZoomOverride = true
      }
    }

    touchEndHandler = (e: TouchEvent) => {
      if (!panzoom) return
      if (touchZoomOverride && (!e.touches || e.touches.length < 2)) {
        panzoom.setOptions?.({ excludeClass: EXCLUDE_CLASS })
        touchZoomOverride = false
      }
    }

    vp.addEventListener('touchstart', touchStartHandler, { passive: true })
    vp.addEventListener('touchend', touchEndHandler, { passive: true })
    vp.addEventListener('touchcancel', touchEndHandler, { passive: true })

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
    if (vp && wheelHandler) vp.removeEventListener('wheel', wheelHandler)
    if (vp && touchStartHandler) vp.removeEventListener('touchstart', touchStartHandler)
    if (vp && touchEndHandler) {
      vp.removeEventListener('touchend', touchEndHandler)
      vp.removeEventListener('touchcancel', touchEndHandler)
    }
    if (board) board.removeEventListener('panzoomchange', onPanzoomChange as any)

    panzoom = null
    wheelHandler = null
    touchStartHandler = null
    touchEndHandler = null
    touchZoomOverride = false
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
