import { get, post } from './index'
import type { LoanApplication, LoanApplyRequest, Repayment, Overdue, PaginatedData, CalculatorInput, CalculatorResult } from '@/types'

export function getLoans(params: { page: number; size: number; status?: string }) {
  return get<PaginatedData<LoanApplication>>('/enterprise/loans', params as Record<string, unknown>)
}

export function applyLoan(data: LoanApplyRequest) {
  return post<LoanApplication>('/enterprise/loans', data)
}

export function getLoanDetail(id: number) {
  return get<LoanApplication>(`/enterprise/loans/${id}`)
}

export function signLoan(id: number) {
  return post<null>(`/enterprise/loans/${id}/sign`)
}

export function getRepayments(params: { loanId?: number; status?: string }) {
  return get<PaginatedData<Repayment>>('/enterprise/repayments', params as Record<string, unknown>)
}

export function payRepayment(id: number) {
  return post<null>(`/enterprise/repayments/${id}/pay`)
}

export function getOverdues() {
  return get<Overdue[]>('/enterprise/overdue')
}

export function calculateLoan(params: CalculatorInput) {
  return get<CalculatorResult>('/enterprise/calculator', params as Record<string, unknown>)
}
