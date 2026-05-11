<template>
  <div class="stats-card glass-card pressable">
    <div
      v-if="glowColor || color"
      class="glow-orb"
      :style="{
        background: `radial-gradient(circle, ${glowColor || color} 0%, transparent 70%)`,
      }"
    />
    <div class="stats-label">{{ label }}</div>
    <div
      class="stats-value"
      :style="{
        background: `linear-gradient(135deg, ${color}, ${color}cc)`,
        WebkitBackgroundClip: 'text',
        WebkitTextFillColor: 'transparent',
        backgroundClip: 'text',
      }"
    >
      <span v-if="prefix">{{ prefix }}</span>{{ formattedValue }}
    </div>
    <div
      v-if="change !== undefined"
      class="stats-change"
      :class="change >= 0 ? 'positive' : 'negative'"
    >
      {{ change >= 0 ? '↑' : '↓' }} {{ Math.abs(change) }}%
    </div>
    <div v-if="sparkline && sparkline.length" class="sparkline">
      <div
        v-for="(val, i) in sparkline"
        :key="i"
        class="sparkline-bar"
        :style="sparklineBarStyle(val)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useNumberScroll } from '@/composables/useNumberScroll'

const props = defineProps<{
  label: string
  value: number
  prefix?: string
  format?: 'currency' | 'number' | 'percent'
  color?: string
  change?: number
  glowColor?: string
  sparkline?: number[]
}>()

const { displayValue, animate, formatCurrency, formatNumber, formatPercent } = useNumberScroll()

const formattedValue = computed(() => {
  if (props.format === 'currency') return formatCurrency(displayValue.value)
  if (props.format === 'percent') return formatPercent(displayValue.value)
  return formatNumber(displayValue.value)
})

const sparklineMax = computed(() => {
  if (!props.sparkline || props.sparkline.length === 0) return 1
  return Math.max(...props.sparkline, 1)
})

function sparklineBarStyle(val: number) {
  const heightPct = Math.max((val / sparklineMax.value) * 100, 2)
  return {
    height: heightPct + '%',
    backgroundColor: props.color || 'currentColor',
  }
}

onMounted(() => animate(props.value))
watch(() => props.value, (val) => animate(val))
</script>

<style scoped>
.stats-card {
  padding: var(--space-6);
  cursor: default;
  position: relative;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0.6;
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--color-text-muted);
  margin-bottom: var(--space-2);
  position: relative;
  z-index: 1;
}

.stats-value {
  font-size: var(--text-3xl);
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.02em;
  position: relative;
  z-index: 1;
}

.stats-change {
  margin-top: var(--space-2);
  font-size: var(--text-xs);
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.stats-change.positive { color: var(--color-success); }
.stats-change.negative { color: var(--color-danger); }

.sparkline {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 28px;
  margin-top: var(--space-3);
  position: relative;
  z-index: 1;
}

.sparkline-bar {
  width: 6px;
  min-height: 2px;
  border-radius: 2px;
  opacity: 0.5;
  transition: height var(--duration-normal) var(--ease-out-expo);
}
</style>
