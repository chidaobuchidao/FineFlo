<template>
  <div class="enterprise-detail" v-if="store.currentEnterprise">
    <button class="back-btn" @click="$router.back()">← 返回</button>
    <div class="detail-header glass-card">
      <h2>{{ store.currentEnterprise.name }}</h2>
      <div class="detail-meta">
        <span>{{ store.currentEnterprise.industry }}</span>
        <span>注册资金 {{ store.currentEnterprise.registeredCapital?.toLocaleString() }} 万</span>
        <span>员工 {{ store.currentEnterprise.employeeCount }} 人</span>
      </div>
    </div>
    <div class="detail-grid">
      <div class="glass-card">
        <h3 class="section-title">企业信息</h3>
        <dl class="info-list">
          <div><dt>法人</dt><dd>{{ store.currentEnterprise.legalPerson }}</dd></div>
          <div><dt>联系电话</dt><dd>{{ store.currentEnterprise.contactPhone }}</dd></div>
          <div><dt>地址</dt><dd>{{ store.currentEnterprise.address }}</dd></div>
          <div><dt>成立日期</dt><dd>{{ store.currentEnterprise.establishDate }}</dd></div>
          <div><dt>年营收</dt><dd>{{ store.currentEnterprise.annualRevenue?.toLocaleString() }} 万</dd></div>
        </dl>
      </div>
      <div class="glass-card">
        <h3 class="section-title">信用评分</h3>
        <CreditGauge :score="latestScore" />
      </div>
    </div>
    <div class="glass-card" style="margin-top: var(--space-6)">
      <h3 class="section-title">历史贷款</h3>
      <DataTable :columns="loanColumns" :data="store.currentEnterprise.loans || []">
        <template #cell-status="{ value }">
          <span class="status-badge" :class="statusClass(value as string)">{{ statusLabel(value as string) }}</span>
        </template>
        <template #cell-loanAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      </DataTable>
    </div>
  </div>
  <div v-else-if="store.loading" class="loading-state"><div class="spinner"></div></div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import CreditGauge from '@/components/shared/CreditGauge.vue'
import { useApprovalStore } from '@/stores/approval'

const route = useRoute()
const store = useApprovalStore()

const latestScore = computed(() => {
  const scores = store.currentEnterprise?.creditScores
  return scores && scores.length > 0 ? scores[scores.length - 1].score : 0
})

const loanColumns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'loanAmount', label: '金额' },
  { key: 'loanTerm', label: '期限(月)' },
  { key: 'status', label: '状态' },
  { key: 'applyDate', label: '申请日期' },
  { key: 'creditScore', label: '信用分' },
]

function statusClass(s: string) {
  const map: Record<string, string> = { APPROVED: 'success', GRANTED: 'info', REJECTED: 'danger', PENDING: 'warning', REPAID: 'success', OVERDUE: 'danger' }
  return map[s] || 'default'
}
function statusLabel(s: string) {
  const map: Record<string, string> = { PENDING: '待审批', APPROVED: '已审批', REJECTED: '已驳回', GRANTED: '已放款', REPAID: '已还清', OVERDUE: '逾期' }
  return map[s] || s
}

onMounted(() => store.fetchEnterpriseDetail(Number(route.params.id)))
</script>

<style scoped>
.back-btn { background: none; border: none; color: var(--color-text-secondary); font-size: var(--text-sm); margin-bottom: var(--space-4); padding: 0; }
.back-btn:hover { color: var(--color-text); }
.detail-header { padding: var(--space-6); margin-bottom: var(--space-6); }
.detail-header h2 { font-size: var(--text-2xl); font-weight: 700; }
.detail-meta { display: flex; gap: var(--space-6); margin-top: var(--space-3); color: var(--color-text-secondary); font-size: var(--text-sm); }
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
.loading-state { display: flex; justify-content: center; padding: 80px; }
</style>
