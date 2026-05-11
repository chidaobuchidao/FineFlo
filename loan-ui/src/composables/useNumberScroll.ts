import { ref, onUnmounted } from 'vue'

export function useNumberScroll(duration = 1500) {
  const displayValue = ref(0)
  let raf = 0

  function animate(target: number) {
    cancelAnimationFrame(raf)
    const start = displayValue.value
    const startTime = performance.now()

    function tick(now: number) {
      const elapsed = now - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = 1 - Math.pow(1 - progress, 3)
      displayValue.value = start + (target - start) * eased
      if (progress < 1) {
        raf = requestAnimationFrame(tick)
      }
    }

    raf = requestAnimationFrame(tick)
  }

  function formatCurrency(value: number): string {
    return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY', minimumFractionDigits: 0, maximumFractionDigits: 0 })
  }

  function formatNumber(value: number): string {
    return value.toLocaleString('zh-CN')
  }

  function formatPercent(value: number): string {
    return value.toFixed(1) + '%'
  }

  onUnmounted(() => cancelAnimationFrame(raf))

  return { displayValue, animate, formatCurrency, formatNumber, formatPercent }
}
