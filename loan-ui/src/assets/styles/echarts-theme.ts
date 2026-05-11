import * as echarts from 'echarts'

const colorPalette = ['#6366F1', '#06B6D4', '#10B981', '#F59E0B', '#EF4444']

export const darkChartTheme = {
  color: colorPalette,
  backgroundColor: 'transparent',
  textStyle: {
    color: '#94A3B8',
  },
  title: {
    textStyle: { color: '#F1F5F9' },
    subtextStyle: { color: '#64748B' },
  },
  legend: {
    textStyle: { color: '#94A3B8' },
  },
  grid: {
    borderColor: 'rgba(255,255,255,0.06)',
  },
  xAxis: {
    axisLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
    axisTick: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
    axisLabel: { color: '#64748B' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.06)' } },
  },
  yAxis: {
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#64748B' },
    splitLine: { lineStyle: { color: 'rgba(255,255,255,0.06)' } },
  },
  tooltip: {
    backgroundColor: 'rgba(17,24,39,0.92)',
    borderColor: 'rgba(99,102,241,0.2)',
    textStyle: { color: '#F1F5F9' },
  },
}

export const lightChartTheme = {
  color: colorPalette,
  backgroundColor: 'transparent',
  textStyle: {
    color: '#64748B',
  },
  title: {
    textStyle: { color: '#0F172A' },
    subtextStyle: { color: '#94A3B8' },
  },
  legend: {
    textStyle: { color: '#475569' },
  },
  grid: {
    borderColor: 'rgba(0,0,0,0.06)',
  },
  xAxis: {
    axisLine: { lineStyle: { color: 'rgba(0,0,0,0.1)' } },
    axisTick: { lineStyle: { color: 'rgba(0,0,0,0.1)' } },
    axisLabel: { color: '#94A3B8' },
    splitLine: { lineStyle: { color: 'rgba(0,0,0,0.06)' } },
  },
  yAxis: {
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: { color: '#94A3B8' },
    splitLine: { lineStyle: { color: 'rgba(0,0,0,0.06)' } },
  },
  tooltip: {
    backgroundColor: 'rgba(15, 23, 42, 0.92)',
    borderColor: 'rgba(99,102,241,0.2)',
    textStyle: { color: '#F1F5F9' },
  },
}

echarts.registerTheme('dark-custom', darkChartTheme)
echarts.registerTheme('light-custom', lightChartTheme)
