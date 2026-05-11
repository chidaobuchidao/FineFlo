import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoanOverview, TrendItem, OverdueAnalysis } from '@/types'
import * as statisticsApi from '@/api/statistics'

export const useStatisticsStore = defineStore('statistics', () => {
  const loanOverview = ref<LoanOverview | null>(null)
  const disbursementTrend = ref<TrendItem[]>([])
  const repaymentTrend = ref<TrendItem[]>([])
  const overdueAnalysis = ref<OverdueAnalysis | null>(null)
  const loading = ref(false)

  async function fetchLoanOverview(startDate?: string, endDate?: string) {
    loading.value = true
    try {
      const res = await statisticsApi.getLoanOverview({ startDate, endDate })
      if (res.code === 200) loanOverview.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function fetchDisbursementTrend() {
    const res = await statisticsApi.getDisbursementTrend()
    if (res.code === 200) disbursementTrend.value = res.data
  }

  async function fetchRepaymentTrend() {
    const res = await statisticsApi.getRepaymentTrend()
    if (res.code === 200) repaymentTrend.value = res.data
  }

  async function fetchOverdueAnalysis() {
    const res = await statisticsApi.getOverdueAnalysis()
    if (res.code === 200) overdueAnalysis.value = res.data
  }

  return { loanOverview, disbursementTrend, repaymentTrend, overdueAnalysis, loading, fetchLoanOverview, fetchDisbursementTrend, fetchRepaymentTrend, fetchOverdueAnalysis }
})
