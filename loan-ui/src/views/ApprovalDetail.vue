<template>
  <div class="approval-detail" v-if="store.currentLoan">
    <button class="back-btn" @click="$router.back()">← 返回</button>
    <div class="detail-grid">
      <div class="glass-card">
        <h3 class="section-title">贷款信息</h3>
        <dl class="info-list">
          <div><dt>贷款金额</dt><dd>¥{{ store.currentLoan.loanAmount?.toLocaleString() }}</dd></div>
          <div><dt>贷款期限</dt><dd>{{ store.currentLoan.loanTerm }} 月</dd></div>
          <div><dt>贷款用途</dt><dd>{{ store.currentLoan.loanPurpose }}</dd></div>
          <div><dt>年利率</dt><dd>{{ ((store.currentLoan.interestRate || 0) * 100).toFixed(2) }}%</dd></div>
          <div><dt>还款方式</dt><dd>{{ store.currentLoan.repaymentMethod }}</dd></div>
          <div><dt>申请日期</dt><dd>{{ store.currentLoan.applyDate }}</dd></div>
          <div><dt>状态</dt><dd><span class="status-badge" :class="statusClass(store.currentLoan.status)">{{ statusLabel(store.currentLoan.status) }}</span></dd></div>
        </dl>
      </div>
      <div class="glass-card">
        <h3 class="section-title">AI 信用评分</h3>
        <CreditGauge :score="store.currentLoan.creditScore || 0" />
      </div>
    </div>
    <div class="glass-card" style="margin-top: var(--space-6)" v-if="store.currentLoan.status === 'PENDING'">
      <h3 class="section-title">审批操作</h3>
      <div class="approve-form">
        <textarea v-model="comment" class="form-textarea focus-ring" placeholder="审批意见..."></textarea>
        <div class="approve-actions">
          <button class="btn-approve pressable" @click="handleApprove('APPROVE')">✓ 通过</button>
          <button class="btn-reject pressable" @click="handleApprove('REJECT')">✕ 驳回</button>
        </div>
      </div>
    </div>
    <div class="glass-card" style="margin-top: var(--space-6)">
      <h3 class="section-title">还款计划</h3>
      <DataTable :columns="repayColumns" :data="store.currentLoan.repayments || []">
        <template #cell-status="{ value }">
          <span :class="['status-text', (value as string) === 'PAID' ? 'text-success' : (value as string) === 'OVERDUE' ? 'text-danger' : '']">{{ value === 'PAID' ? '已还' : value === 'OVERDUE' ? '逾期' : '未还' }}</span>
        </template>
        <template #cell-amount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
        <template #cell-paidAmount="{ value }">¥{{ (value as number)?.toLocaleString() || '-' }}</template>
      </DataTable>
    </div>
  </div>
  <div v-else-if="store.loading" class="loading-state"><div class="spinner"></div></div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import CreditGauge from '@/components/shared/CreditGauge.vue'
import { useApprovalStore } from '@/stores/approval'

const route = useRoute()
const store = useApprovalStore()
const comment = ref('')

const repayColumns: Column[] = [
  { key: 'periodNo', label: '期数' },
  { key: 'amount', label: '应还' },
  { key: 'paidAmount', label: '实还' },
  { key: 'dueDate', label: '到期日' },
  { key: 'paidDate', label: '还款日' },
  { key: 'status', label: '状态' },
]

async function handleApprove(action: 'APPROVE' | 'REJECT') {
  const ok = await store.approveLoan(store.currentLoan!.id, action, comment.value)
  if (ok) store.fetchApprovalLoanDetail(store.currentLoan!.id)
}

function statusClass(s: string): string {
  const map: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', GRANTED: 'info' }
  return map[s] || ''
}
function statusLabel(s: string): string {
  const map: Record<string, string> = { PENDING: '待审批', APPROVED: '已审批', REJECTED: '已驳回', GRANTED: '已放款', REPAID: '已还清', OVERDUE: '逾期' }
  return map[s] || s
}

onMounted(() => store.fetchApprovalLoanDetail(Number(route.params.id)))
</script>

<style scoped>
.back-btn { background: none; border: none; color: var(--color-text-secondary); font-size: var(--text-sm); margin-bottom: var(--space-4); padding: 0; }
.back-btn:hover { color: var(--color-text); }
.detail-grid { display: grid; grid-template-columns: 1fr 380px; gap: var(--space-6); }
.section-title { font-size: var(--text-base); font-weight: 600; padding: var(--space-4) var(--space-6); border-bottom: var(--border-subtle); }
.info-list { padding: var(--space-4) var(--space-6); }
.info-list > div { display: flex; padding: var(--space-2) 0; }
.info-list dt { width: 100px; color: var(--color-text-muted); font-size: var(--text-sm); }
.info-list dd { color: var(--color-text); font-size: var(--text-sm); }
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
.status-badge.warning { background: rgba(245,158,11,0.15); color: var(--color-warning); }
.status-badge.info { background: rgba(59,130,246,0.15); color: var(--color-info); }
.text-success { color: var(--color-success); }
.text-danger { color: var(--color-danger); }
.approve-form { padding: var(--space-4) var(--space-6); }
.form-textarea { width: 100%; height: 80px; padding: var(--space-3); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); resize: vertical; outline: none; }
.form-textarea:focus { border-color: var(--color-primary); }
.approve-actions { display: flex; gap: var(--space-4); margin-top: var(--space-4); }
.btn-approve, .btn-reject { padding: var(--space-2) var(--space-8); border: none; border-radius: var(--radius-md); font-size: var(--text-sm); font-weight: 600; }
.btn-approve { background: var(--color-success); color: white; }
.btn-reject { background: var(--color-danger); color: white; }
.btn-approve:hover, .btn-reject:hover { filter: brightness(1.1); }
.loading-state { display: flex; justify-content: center; padding: 80px; }
</style>
