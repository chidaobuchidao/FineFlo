import { get } from './index'
import type { LoanOverview, TrendItem, OverdueAnalysis } from '@/types'

export function getLoanOverview(params?: { startDate?: string; endDate?: string }) {
  return get<LoanOverview>('/statistics/loan-overview', params as Record<string, unknown>)
}

export function getDisbursementTrend() {
  return get<TrendItem[]>('/statistics/disbursement-trend')
}

export function getRepaymentTrend() {
  return get<TrendItem[]>('/statistics/repayment-trend')
}

export function getOverdueAnalysis() {
  return get<OverdueAnalysis>('/statistics/overdue-analysis')
}
