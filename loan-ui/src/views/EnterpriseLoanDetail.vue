<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">贷款详情 #{{ loan?.id }}</h2>
        <p class="page-desc"><router-link to="/loans" class="back-link">← 返回列表</router-link></p>
      </div>
      <span v-if="loan" :class="statusClass(loan.status)">{{ statusLabel(loan.status) }}</span>
    </div>

    <div v-if="loading" class="glass-card skeleton-card"><p style="color:var(--color-text-muted)">加载中...</p></div>

    <div v-else-if="loan" class="detail-grid">
      <!-- Info -->
      <div class="glass-card detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="field"><span class="field-label">贷款金额</span><span class="field-value highlight">¥{{ formatMoney(loan.loanAmount) }}</span></div>
        <div class="field"><span class="field-label">贷款期限</span><span class="field-value">{{ loan.loanTerm }} 个月</span></div>
        <div class="field"><span class="field-label">年利率</span><span class="field-value">{{ ((loan.interestRate ?? 0) * 100).toFixed(2) }}%</span></div>
        <div class="field"><span class="field-label">还款方式</span><span class="field-value">{{ loan.repaymentMethod === 'EQUAL_INSTALLMENT' ? '等额本息' : '等额本金' }}</span></div>
        <div class="field"><span class="field-label">贷款用途</span><span class="field-value">{{ loan.loanPurpose || '—' }}</span></div>
        <div class="field"><span class="field-label">申请日期</span><span class="field-value">{{ formatDate(loan.applyDate) }}</span></div>
        <div v-if="loan.approveComment" class="field"><span class="field-label">审批意见</span><span class="field-value" style="color:var(--color-info)">{{ loan.approveComment }}</span></div>
      </div>

      <!-- Credit Score -->
      <div class="glass-card detail-section">
        <h3 class="section-title">AI 信用评分</h3>
        <div class="gauge-wrap">
          <CreditGauge v-if="loan.creditScore != null" :score="Math.round(loan.creditScore)" :size="160" />
          <p v-else style="color:var(--color-text-muted)">待评估</p>
        </div>
      </div>

      <!-- Sign -->
      <div v-if="loan.status === 'APPROVED'" class="glass-card detail-section" style="grid-column: 1 / -1; text-align: center;">
        <p style="margin-bottom: var(--space-4); color: var(--color-text-secondary);">贷款已审批通过，请确认签约</p>
        <button class="btn btn-primary btn-lg" @click="handleSign">确认签约</button>
      </div>

      <!-- Repayments link -->
      <div class="glass-card detail-section" style="grid-column: 1 / -1;">
        <router-link :to="`/my-repayments?loanId=${loan.id}`" class="link">查看还款计划 →</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLoanDetail, signLoan } from '@/api/enterprise'
import CreditGauge from '@/components/shared/CreditGauge.vue'
import type { LoanApplication } from '@/types'

const route = useRoute()
const router = useRouter()
const loan = ref<LoanApplication | null>(null)
const loading = ref(true)

async function handleSign() {
  if (!loan.value || !confirm('确认签约此贷款合同？')) return
  await signLoan(loan.value.id)
  // Refresh
  const res = await getLoanDetail(loan.value.id)
  loan.value = res.data
}

function statusLabel(s: string) {
  const map: Record<string, string> = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已拒绝', GRANTED: '已放款', REPAID: '已还清', OVERDUE: '已逾期' }
  return map[s] || s
}
function statusClass(s: string) { return `badge badge-${s.toLowerCase()}` }
function formatMoney(v: number) { return v?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }
function formatDate(v: string) { return v ? new Date(v).toLocaleDateString('zh-CN') : '—' }

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  try {
    const res = await getLoanDetail(id)
    loan.value = res.data
  } finally { loading.value = false }
})
</script>

<style scoped>
.page { padding: var(--space-8); max-width: 900px; }
.page-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.page-desc { margin-top: var(--space-1); font-size: var(--text-sm); }
.back-link { color: var(--color-text-muted); text-decoration: none; }
.detail-grid { display: grid; grid-template-columns: 2fr 1fr; gap: var(--space-5); margin-top: var(--space-6); }
.detail-section { padding: var(--space-6); }
.section-title { font-size: var(--text-base); font-weight: 600; margin-bottom: var(--space-4); padding-bottom: var(--space-3); border-bottom: var(--border-subtle); }
.field { display: flex; justify-content: space-between; padding: var(--space-2) 0; font-size: var(--text-sm); }
.field-label { color: var(--color-text-muted); }
.field-value { color: var(--color-text); font-weight: 500; }
.highlight { color: var(--color-accent); font-size: var(--text-lg); font-family: var(--font-mono); }
.gauge-wrap { display: flex; justify-content: center; padding: var(--space-6); }
.link { color: var(--color-primary); text-decoration: none; font-size: var(--text-sm); font-weight: 500; }
.btn { display: inline-flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-md); font-weight: 500; transition: all var(--duration-fast); border: none; cursor: pointer; }
.btn-primary { background: var(--color-primary); color: #fff; }
.btn-lg { padding: var(--space-3) var(--space-8); font-size: var(--text-base); }
.badge { display: inline-block; padding: 2px 10px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.badge-pending { background: rgba(245,158,11,.15); color: var(--color-warning); }
.badge-approved { background: rgba(59,130,246,.15); color: var(--color-info); }
.badge-rejected { background: rgba(239,68,68,.15); color: var(--color-danger); }
.badge-granted { background: rgba(16,185,129,.15); color: var(--color-success); }
.badge-repaid { background: rgba(99,102,241,.15); color: var(--color-primary); }
.badge-overdue { background: rgba(239,68,68,.2); color: var(--color-danger); }
@media (max-width: 700px) { .detail-grid { grid-template-columns: 1fr; } }
</style>
