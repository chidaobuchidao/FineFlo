<template>
  <div class="credit-gauge">
    <svg viewBox="0 0 200 120" class="gauge-svg">
      <defs>
        <linearGradient id="gaugeGrad" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stop-color="#EF4444" />
          <stop offset="50%" stop-color="#F59E0B" />
          <stop offset="100%" stop-color="#10B981" />
        </linearGradient>
      </defs>
      <path d="M 30 100 A 70 70 0 0 1 170 100" fill="none" stroke-width="12" stroke-linecap="round" class="gauge-track" />
      <path
        d="M 30 100 A 70 70 0 0 1 170 100"
        fill="none"
        stroke="url(#gaugeGrad)"
        stroke-width="12"
        stroke-linecap="round"
        :stroke-dasharray="arcLength"
        :stroke-dashoffset="arcOffset"
        class="gauge-arc"
      />
      <text x="100" y="85" text-anchor="middle" class="gauge-score">{{ displayScore }}</text>
      <text x="100" y="108" text-anchor="middle" class="gauge-label">{{ riskLabel }}</text>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useNumberScroll } from '@/composables/useNumberScroll'

const props = defineProps<{ score: number }>()

const arcLength = 2 * Math.PI * 70 * 0.5
const { displayValue, animate } = useNumberScroll(1000)

const displayScore = computed(() => displayValue.value.toFixed(1))

const arcOffset = computed(() => arcLength * (1 - displayValue.value / 100))

const riskLabel = computed(() => {
  if (displayValue.value >= 70) return '低风险'
  if (displayValue.value >= 40) return '中风险'
  return '高风险'
})

onMounted(() => animate(props.score))
watch(() => props.score, (v) => animate(v))
</script>

<style scoped>
.credit-gauge { display: flex; justify-content: center; }
.gauge-svg { width: 220px; height: 130px; }
.gauge-track { stroke: var(--color-text-muted); opacity: 0.12; }
.gauge-arc { transition: stroke-dashoffset 1s var(--ease-out-expo); }
.gauge-score { font-size: 28px; font-weight: 700; fill: var(--color-text); }
.gauge-label { font-size: 12px; fill: var(--color-text-secondary); }
</style>
