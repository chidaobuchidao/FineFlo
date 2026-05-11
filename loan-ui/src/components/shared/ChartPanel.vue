<template>
  <div class="chart-panel glass-card">
    <div class="chart-header">
      <h3 class="chart-title">{{ title }}</h3>
      <div class="chart-actions">
        <slot name="actions" />
        <button
          class="action-btn pressable"
          title="全屏查看"
          @click="toggleFullscreen"
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3 6V3h3M13 6V3h-3M3 10v3h3M13 10v3h-3" />
          </svg>
        </button>
        <button
          class="action-btn pressable"
          title="下载图片"
          @click="downloadChart"
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M8 11V3M5 8l3 3 3-3M3 13h10" />
          </svg>
        </button>
      </div>
    </div>
    <div ref="chartRef" class="chart-body" :style="{ height: height }"></div>
  </div>

  <Teleport to="body">
    <div v-if="isFullscreen" class="chart-fullscreen-overlay" @click.self="toggleFullscreen">
      <div class="chart-fullscreen-panel glass-card">
        <div class="chart-fullscreen-header">
          <h3>{{ title }}</h3>
          <button class="action-btn pressable" title="关闭" @click="toggleFullscreen">
            <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M4 4l10 10M14 4L4 14" />
            </svg>
          </button>
        </div>
        <div ref="fullscreenChartRef" class="chart-fullscreen-body"></div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import '@/assets/styles/echarts-theme'

const props = withDefaults(defineProps<{
  title: string
  option: EChartsOption
  height?: string
}>(), {
  height: '350px',
})

const chartRef = ref<HTMLElement | null>(null)
const fullscreenChartRef = ref<HTMLElement | null>(null)
const isFullscreen = ref(false)

let chart: echarts.ECharts | null = null
let fullscreenChart: echarts.ECharts | null = null

function resolveTheme(): string {
  const mode = document.documentElement.getAttribute('data-theme')
  if (mode === 'light') return 'light-custom'
  if (mode === 'system') {
    if (window.matchMedia('(prefers-color-scheme: light)').matches) return 'light-custom'
    return 'dark-custom'
  }
  return 'dark-custom'
}

function initChart() {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value, resolveTheme())
  chart.setOption({ ...props.option, backgroundColor: 'transparent' })
}

function resize() {
  chart?.resize()
  fullscreenChart?.resize()
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
  if (isFullscreen.value) {
    nextTick(() => {
      if (!fullscreenChartRef.value) return
      fullscreenChart = echarts.init(fullscreenChartRef.value, resolveTheme())
      fullscreenChart.setOption({ ...props.option, backgroundColor: 'transparent' })
    })
  } else {
    fullscreenChart?.dispose()
    fullscreenChart = null
  }
}

function downloadChart() {
  const instance = isFullscreen.value ? fullscreenChart : chart
  if (!instance) return
  const url = instance.getDataURL({ type: 'png', pixelRatio: 2, backgroundColor: 'transparent' })
  const link = document.createElement('a')
  link.href = url
  link.download = `${props.title}-${Date.now()}.png`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape' && isFullscreen.value) {
    toggleFullscreen()
  }
}

onMounted(() => {
  nextTick(initChart)
  window.addEventListener('resize', resize)
  document.addEventListener('keydown', onKeydown)
})

onUnmounted(() => {
  chart?.dispose()
  fullscreenChart?.dispose()
  window.removeEventListener('resize', resize)
  document.removeEventListener('keydown', onKeydown)
})

watch(() => props.option, (opt) => {
  chart?.setOption({ ...opt, backgroundColor: 'transparent' }, true)
  fullscreenChart?.setOption({ ...opt, backgroundColor: 'transparent' }, true)
}, { deep: true })

watch(() => document.documentElement.getAttribute('data-theme'), () => {
  if (chart) {
    chart.dispose()
    chart = echarts.init(chartRef.value!, resolveTheme())
    chart.setOption({ ...props.option, backgroundColor: 'transparent' })
  }
  if (fullscreenChart) {
    fullscreenChart.dispose()
    fullscreenChart = echarts.init(fullscreenChartRef.value!, resolveTheme())
    fullscreenChart.setOption({ ...props.option, backgroundColor: 'transparent' })
  }
})
</script>

<style scoped>
.chart-panel { overflow: hidden; }

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) var(--space-6);
  border-bottom: var(--border-subtle);
}

.chart-title {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--color-text);
}

.chart-actions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: var(--border-subtle);
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out-expo);
}

.action-btn:hover {
  color: var(--color-text);
  background: var(--color-surface-hover);
  border-color: rgba(99, 102, 241, 0.3);
}

.chart-body {
  width: 100%;
}

/* Fullscreen overlay */
.chart-fullscreen-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-8);
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.chart-fullscreen-panel {
  width: 100%;
  max-width: 1400px;
  height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chart-fullscreen-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) var(--space-6);
  border-bottom: var(--border-subtle);
  flex-shrink: 0;
}

.chart-fullscreen-header h3 {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-text);
}

.chart-fullscreen-body {
  flex: 1;
  min-height: 0;
}
</style>
