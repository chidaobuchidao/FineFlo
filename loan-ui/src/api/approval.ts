import { get, put, post } from './index'
import type { Enterprise, LoanApplication, LoanApproveRequest, Repayment, Overdue, PaginatedData, CreditScore, RiskEvaluation, RiskEvaluateRequest, Disbursement } from '@/types'

export function getEnterprises(params: { page: number; size: number; keyword?: string }) {
  return get<PaginatedData<Enterprise>>('/approval/enterprises', params as Record<string, unknown>)
}

export function getEnterpriseDetail(id: number) {
  return get<Enterprise & { loans: LoanApplication[]; creditScores: CreditScore[] }>(`/approval/enterprises/${id}`)
}

export function getApprovalLoans(params: { page: number; size: number; status?: string }) {
  return get<PaginatedData<LoanApplication>>('/approval/loans', params as Record<string, unknown>)
}

export function approveLoan(id: number, data: LoanApproveRequest) {
  return put<null>(`/approval/loans/${id}/approve`, data)
}

export function getApprovalLoanDetail(id: number) {
  return get<LoanApplication & { enterprise: Enterprise; repayments: Repayment[] }>(`/approval/loans/${id}`)
}

export function getDisbursements(params: { status?: string }) {
  return get<Disbursement[]>('/approval/disbursements', params as Record<string, unknown>)
}

export function grantDisbursement(loanId: number) {
  return put<null>(`/approval/disbursements/${loanId}/grant`)
}

export function getApprovalRepayments(params: { loanId?: number; status?: string }) {
  return get<PaginatedData<Repayment>>('/approval/repayments', params as Record<string, unknown>)
}

export function getApprovalOverdues(params: { page: number; size: number }) {
  return get<PaginatedData<Overdue>>('/approval/overdues', params as Record<string, unknown>)
}

export function getRiskQuery(enterpriseId: number) {
  return get<{ creditScore: CreditScore | null; history: CreditScore[] }>('/approval/risk-query', { enterpriseId })
}

export function evaluateRisk(data: RiskEvaluateRequest) {
  return post<RiskEvaluation>('/approval/risk-evaluate', data)
}
