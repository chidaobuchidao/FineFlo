import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Enterprise, LoanApplication, Repayment, Overdue, CreditScore, RiskEvaluation, Disbursement } from '@/types'
import * as approvalApi from '@/api/approval'

export const useApprovalStore = defineStore('approval', () => {
  const enterprises = ref<Enterprise[]>([])
  const enterpriseTotal = ref(0)
  const currentEnterprise = ref<(Enterprise & { loans: LoanApplication[]; creditScores: CreditScore[] }) | null>(null)
  const loans = ref<LoanApplication[]>([])
  const loanTotal = ref(0)
  const currentLoan = ref<(LoanApplication & { enterprise: Enterprise; repayments: Repayment[] }) | null>(null)
  const disbursements = ref<Disbursement[]>([])
  const repayments = ref<Repayment[]>([])
  const overdues = ref<Overdue[]>([])
  const overdueTotal = ref(0)
  const creditScores = ref<CreditScore[]>([])
  const currentRisk = ref<RiskEvaluation | null>(null)
  const loading = ref(false)

  async function fetchEnterprises(page = 1, size = 10, keyword = '') {
    loading.value = true
    try {
      const res = await approvalApi.getEnterprises({ page, size, keyword })
      if (res.code === 200) {
        enterprises.value = res.data.records
        enterpriseTotal.value = res.data.total
      }
    } finally {
      loading.value = false
    }
  }

  async function fetchEnterpriseDetail(id: number) {
    loading.value = true
    try {
      const res = await approvalApi.getEnterpriseDetail(id)
      if (res.code === 200) currentEnterprise.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function fetchApprovalLoans(page = 1, size = 10, status = '') {
    loading.value = true
    try {
      const res = await approvalApi.getApprovalLoans({ page, size, status })
      if (res.code === 200) {
        loans.value = res.data.records
        loanTotal.value = res.data.total
      }
    } finally {
      loading.value = false
    }
  }

  async function fetchApprovalLoanDetail(id: number) {
    loading.value = true
    try {
      const res = await approvalApi.getApprovalLoanDetail(id)
      if (res.code === 200) currentLoan.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function approveLoan(id: number, action: 'APPROVE' | 'REJECT', comment: string): Promise<boolean> {
    const res = await approvalApi.approveLoan(id, { action, comment })
    return res.code === 200
  }

  async function fetchDisbursements(status = '') {
    const res = await approvalApi.getDisbursements({ status })
    if (res.code === 200) disbursements.value = res.data
  }

  async function grantDisbursement(loanId: number): Promise<boolean> {
    const res = await approvalApi.grantDisbursement(loanId)
    return res.code === 200
  }

  async function fetchApprovalRepayments(loanId?: number, status?: string) {
    const res = await approvalApi.getApprovalRepayments({ loanId, status })
    if (res.code === 200) repayments.value = res.data.records
  }

  async function fetchApprovalOverdues(page = 1, size = 10) {
    const res = await approvalApi.getApprovalOverdues({ page, size })
    if (res.code === 200) {
      overdues.value = res.data.records
      overdueTotal.value = res.data.total
    }
  }

  async function fetchRiskQuery(enterpriseId: number) {
    const res = await approvalApi.getRiskQuery(enterpriseId)
    if (res.code === 200 && res.data) creditScores.value = res.data.history || []
  }

  async function evaluateRisk(enterpriseId: number) {
    loading.value = true
    try {
      const res = await approvalApi.evaluateRisk({ enterpriseId })
      if (res.code === 200) currentRisk.value = res.data
      return res.code === 200
    } finally {
      loading.value = false
    }
  }

  return { enterprises, enterpriseTotal, currentEnterprise, loans, loanTotal, currentLoan, disbursements, repayments, overdues, overdueTotal, creditScores, currentRisk, loading, fetchEnterprises, fetchEnterpriseDetail, fetchApprovalLoans, fetchApprovalLoanDetail, approveLoan, fetchDisbursements, grantDisbursement, fetchApprovalRepayments, fetchApprovalOverdues, fetchRiskQuery, evaluateRisk }
})
