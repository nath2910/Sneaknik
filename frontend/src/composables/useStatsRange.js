import { ref } from 'vue'

const toIso = (d) => d.toISOString().slice(0, 10)

export function useStatsRange() {
  const now = new Date()
  const from = ref(toIso(new Date(now.getFullYear(), now.getMonth(), 1)))
  const to = ref(toIso(new Date(now.getFullYear(), now.getMonth() + 1, 0)))

  return { from, to }
}
