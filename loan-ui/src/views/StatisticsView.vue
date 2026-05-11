<template>
  <div class="statistics-view">
    <div class="stats-grid">
      <StatsCard label="贷款申请" :value="overview?.totalApply || 0" format="number" color="#6366F1" />
      <StatsCard label="审批通过" :value="overview?.totalApproved || 0" format="number" color="#06B6D4" />
      <StatsCard label="已放款" :value="overview?.totalDisbursed || 0" format="number" color="#10B981" />
      <StatsCard label="审批率" :value="overview?.approvalRate || 0" format="percent" color="#8B5CF6" />
      <StatsCard label="逾期数" :value="overview?.totalOverdue || 0" format="number" color="#EF4444" />
    </div>
    <div class="charts-grid">
      <ChartPanel title="放款趋势" :option="disbursementOption" />
      <ChartPanel title="还款趋势" :option="repaymentOption" />
    </div>
    <div class="charts-grid" v-if="store.overdueAnalysis">
      <ChartPanel title="逾期行业分布" :option="overdueIndustryOption" />
      <ChartPanel title="逾期金额分布" :option="overdueAmountOption" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import StatsCard from '@/components/shared/StatsCard.vue'
import ChartPanel from '@/components/shared/ChartPanel.vue'
import { useStatisticsStore } from '@/stores/statistics'
import type { EChartsOption } from 'echarts'

const store = useStatisticsStore()

onMounted(() => {
  store.fetchLoanOverview()
  store.fetchDisbursementTrend()
  store.fetchRepaymentTrend()
  store.fetchOverdueAnalysis()
})

const overview = computed(() => store.loanOverview)
const darkText = '#94A3B8'
const isDark = computed(() => document.documentElement.getAttribute('data-theme') !== 'light')
const chartBorderColor = computed(() => isDark.value ? 'rgba(255,255,255,0.06)' : 'rgba(0,0,0,0.08)')

const disbursementOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: store.disbursementTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: chartBorderColor.value } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: chartBorderColor.value } } },
  series: [{ data: store.disbursementTrend.map((d) => d.value), type: 'line', smooth: true, lineStyle: { color: '#06B6D4', width: 2 }, itemStyle: { color: '#06B6D4' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(6,182,212,0.2)' }, { offset: 1, color: 'rgba(6,182,212,0)' }] } } }],
}))

const repaymentOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: store.repaymentTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: chartBorderColor.value } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: chartBorderColor.value } } },
  series: [{ data: store.repaymentTrend.map((d) => d.value), type: 'bar', barWidth: '40%', itemStyle: { color: '#6366F1', borderRadius: [4, 4, 0, 0] } }],
}))

const overdueIndustryOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item' },
  series: [{ type: 'pie', radius: ['45%', '75%'], center: ['50%', '55%'], data: store.overdueAnalysis?.byIndustry?.map((d) => ({ name: d.name, value: d.count })) || [], label: { color: darkText, fontSize: 12 }, itemStyle: { borderColor: 'var(--color-bg)', borderWidth: 3 } }],
}))

const overdueAmountOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: store.overdueAnalysis?.byAmount?.map((d) => d.range) || [], axisLabel: { color: darkText }, axisLine: { lineStyle: { color: chartBorderColor.value } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: chartBorderColor.value } } },
  series: [{ data: store.overdueAnalysis?.byAmount?.map((d) => d.count) || [], type: 'bar', barWidth: '40%', itemStyle: { color: '#F59E0B', borderRadius: [4, 4, 0, 0] } }],
}))
</script>

<style scoped>
.statistics-view { display: flex; flex-direction: column; gap: var(--space-6); }
.stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: var(--space-4); }
.charts-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--space-4); }
@media (max-width: 1400px) { .stats-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 1200px) { .charts-grid { grid-template-columns: 1fr; } }
</style>
