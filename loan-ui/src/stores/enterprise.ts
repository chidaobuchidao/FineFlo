import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoanApplication, Repayment, Overdue, CalculatorResult, CalculatorInput } from '@/types'
import * as enterpriseApi from '@/api/enterprise'

export const useEnterpriseStore = defineStore('enterprise', () => {
  const loans = ref<LoanApplication[]>([])
  const loanTotal = ref(0)
  const currentLoan = ref<LoanApplication | null>(null)
  const repayments = ref<Repayment[]>([])
  const overdues = ref<Overdue[]>([])
  const calculatorResult = ref<CalculatorResult | null>(null)
  const loading = ref(false)

  async function fetchLoans(page = 1, size = 10, status = '') {
    loading.value = true
    try {
      const res = await enterpriseApi.getLoans({ page, size, status })
      if (res.code === 200) {
        loans.value = res.data.records
        loanTotal.value = res.data.total
      }
    } finally {
      loading.value = false
    }
  }

  async function fetchLoanDetail(id: number) {
    loading.value = true
    try {
      const res = await enterpriseApi.getLoanDetail(id)
      if (res.code === 200) currentLoan.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function applyLoan(data: { loanAmount: number; loanTerm: number; loanPurpose: string; repaymentMethod: string }) {
    loading.value = true
    try {
      const res = await enterpriseApi.applyLoan(data)
      return res.code === 200
    } finally {
      loading.value = false
    }
  }

  async function fetchRepayments(loanId?: number, status?: string) {
    const res = await enterpriseApi.getRepayments({ loanId, status })
    if (res.code === 200) repayments.value = res.data.records
  }

  async function fetchOverdues() {
    const res = await enterpriseApi.getOverdues()
    if (res.code === 200) overdues.value = res.data
  }

  async function calculate(data: CalculatorInput) {
    const res = await enterpriseApi.calculateLoan(data)
    if (res.code === 200) calculatorResult.value = res.data
  }

  return { loans, loanTotal, currentLoan, repayments, overdues, calculatorResult, loading, fetchLoans, fetchLoanDetail, applyLoan, fetchRepayments, fetchOverdues, calculate }
})
