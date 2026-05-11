<template>
  <div class="dashboard">
    <div ref="statsGridRef" class="stats-grid stagger-container">
      <StatsCard
        label="贷款申请"
        :value="overview?.totalApply || 0"
        format="number"
        color="#6366F1"
        glow-color="rgba(99,102,241,0.15)"
        :sparkline="applySparkline"
      />
      <StatsCard
        label="已审批"
        :value="overview?.totalApproved || 0"
        format="number"
        color="#06B6D4"
        glow-color="rgba(6,182,212,0.12)"
      />
      <StatsCard
        label="已放款"
        :value="overview?.totalDisbursed || 0"
        format="number"
        color="#10B981"
        glow-color="rgba(16,185,129,0.12)"
        :sparkline="disbursedSparkline"
      />
      <StatsCard
        label="逾期数"
        :value="overview?.totalOverdue || 0"
        format="number"
        color="#EF4444"
        glow-color="rgba(239,68,68,0.08)"
      />
    </div>

    <ErrorBanner
      v-if="fetchError"
      :message="fetchError"
      dismissable
      @retry="loadAll"
      @dismiss="fetchError = ''"
    />

    <div class="charts-grid">
      <ChartPanel title="放款趋势" :option="disbursementOption" />
      <ChartPanel title="还款趋势" :option="repaymentOption" />
    </div>
    <div class="charts-grid">
      <ChartPanel title="贷款总览" :option="overviewOption" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, nextTick } from 'vue'
import StatsCard from '@/components/shared/StatsCard.vue'
import ChartPanel from '@/components/shared/ChartPanel.vue'
import ErrorBanner from '@/components/shared/ErrorBanner.vue'
import { useStatisticsStore } from '@/stores/statistics'
import type { EChartsOption } from 'echarts'
import gsap from 'gsap'

const stats = useStatisticsStore()
const statsGridRef = ref<HTMLElement | null>(null)
const fetchError = ref('')

async function loadAll() {
  fetchError.value = ''
  try {
    await stats.fetchLoanOverview()
  } catch {
    fetchError.value = '获取贷款总览失败，请重试'
  }
  try {
    await stats.fetchDisbursementTrend()
  } catch {
    // continue
  }
  try {
    await stats.fetchRepaymentTrend()
  } catch {
    // continue
  }
}

onMounted(() => {
  loadAll()

  nextTick(() => {
    gsap.from('.stats-grid > *', {
      y: 24,
      opacity: 0,
      duration: 0.5,
      stagger: 0.06,
      ease: 'power3.out',
    })
  })
})

const overview = computed(() => stats.loanOverview)

const applySparkline = computed(() => {
  return overview.value?.monthly?.map((d) => d.apply) || []
})

const disbursedSparkline = computed(() => {
  return overview.value?.monthly?.map((d) => d.disbursed) || []
})

const darkText = '#94A3B8'
const isDark = computed(() => document.documentElement.getAttribute('data-theme') !== 'light')
const chartBorderColor = computed(() => isDark.value ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.08)')

const disbursementOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: stats.disbursementTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: chartBorderColor.value } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: chartBorderColor.value } } },
  series: [{ data: stats.disbursementTrend.map((d) => d.value), type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, lineStyle: { color: '#06B6D4', width: 2 }, itemStyle: { color: '#06B6D4' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(6,182,212,0.2)' }, { offset: 1, color: 'rgba(6,182,212,0)' }] } } }],
}))

const repaymentOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: stats.repaymentTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: chartBorderColor.value } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: chartBorderColor.value } } },
  series: [{ data: stats.repaymentTrend.map((d) => d.value), type: 'bar', barWidth: '40%', itemStyle: { color: '#6366F1', borderRadius: [4, 4, 0, 0] } }],
}))

const overviewOption = computed<EChartsOption>(() => {
  const months = overview.value?.monthly?.map((d) => d.month) || []
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['申请', '审批', '放款'], textStyle: { color: darkText }, top: 0 },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '40px', containLabel: true },
    xAxis: { type: 'category', data: months, axisLabel: { color: darkText }, axisLine: { lineStyle: { color: chartBorderColor.value } } },
    yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: chartBorderColor.value } } },
    series: [
      { name: '申请', type: 'line', data: overview.value?.monthly?.map((d) => d.apply) || [], smooth: true, lineStyle: { color: '#6366F1' }, itemStyle: { color: '#6366F1' } },
      { name: '审批', type: 'line', data: overview.value?.monthly?.map((d) => d.approved) || [], smooth: true, lineStyle: { color: '#06B6D4' }, itemStyle: { color: '#06B6D4' } },
      { name: '放款', type: 'line', data: overview.value?.monthly?.map((d) => d.disbursed) || [], smooth: true, lineStyle: { color: '#10B981' }, itemStyle: { color: '#10B981' } },
    ],
  }
})
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: var(--space-6); }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-4); }
.charts-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--space-4); }
@media (max-width: 1200px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } .charts-grid { grid-template-columns: 1fr; } }
</style>
