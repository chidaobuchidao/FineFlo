<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">贷款申请</h2>
        <p class="page-desc">填写贷款信息，提交后将由AI风控系统进行信用评估</p>
      </div>
    </div>

    <form class="apply-form glass-card" @submit.prevent="handleSubmit">
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label">贷款金额（元）</label>
          <input v-model.number="form.loanAmount" type="number" class="form-input" placeholder="如 500000" required min="10000" @change="calcPreview" />
          <span class="form-hint">1万 ~ 5000万元</span>
        </div>
        <div class="form-group">
          <label class="form-label">贷款期限（月）</label>
          <select v-model.number="form.loanTerm" class="form-input" required @change="calcPreview">
            <option value="">请选择</option>
            <option v-for="m in [3,6,12,24,36,48,60]" :key="m" :value="m">{{ m }} 个月</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">还款方式</label>
          <select v-model="form.repaymentMethod" class="form-input" required @change="calcPreview">
            <option value="EQUAL_INSTALLMENT">等额本息</option>
            <option value="EQUAL_PRINCIPAL">等额本金</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">参考年利率</label>
          <input type="text" class="form-input" value="4.35%" disabled style="opacity:0.5" />
        </div>
        <div class="form-group" style="grid-column: 1 / -1;">
          <label class="form-label">贷款用途</label>
          <textarea v-model="form.loanPurpose" class="form-input form-textarea" placeholder="请详细说明贷款用途，如：扩大生产线、采购原材料等" required maxlength="255" rows="3"></textarea>
        </div>
      </div>

      <!-- Preview -->
      <div v-if="preview" class="preview">
        <h3 class="preview-title">还款预估</h3>
        <div class="preview-row"><span>月还款额</span><span class="preview-highlight">¥{{ formatMoney(preview.monthlyPayment) }}</span></div>
        <div class="preview-row"><span>总利息</span><span>¥{{ formatMoney(preview.totalInterest) }}</span></div>
        <div class="preview-row"><span>总还款额</span><span>¥{{ formatMoney(preview.totalPayment) }}</span></div>
      </div>

      <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary btn-lg" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交申请' }}
        </button>
        <router-link to="/loans" class="btn btn-ghost btn-lg">取消</router-link>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { applyLoan } from '@/api/enterprise'

const router = useRouter()
const form = reactive({ loanAmount: 500000, loanTerm: 12, loanPurpose: '', repaymentMethod: 'EQUAL_INSTALLMENT' })
const errorMsg = ref('')
const submitting = ref(false)
const preview = ref<{ monthlyPayment: number; totalInterest: number; totalPayment: number } | null>(null)

function calcEqualInstallment(principal: number, months: number, rate: number) {
  const r = rate / 12
  const payment = (principal * r * Math.pow(1 + r, months)) / (Math.pow(1 + r, months) - 1)
  return { monthlyPayment: Math.round(payment * 100) / 100, totalInterest: Math.round((payment * months - principal) * 100) / 100, totalPayment: Math.round(payment * months * 100) / 100 }
}

function calcEqualPrincipal(principal: number, months: number, rate: number) {
  const r = rate / 12
  const mp = principal / months
  let totalInterest = 0, remaining = principal
  for (let i = 1; i <= months; i++) {
    totalInterest += remaining * r
    remaining -= mp
  }
  return { monthlyPayment: Math.round((mp + principal * r) * 100) / 100, totalInterest: Math.round(totalInterest * 100) / 100, totalPayment: Math.round((principal + totalInterest) * 100) / 100 }
}

function calcPreview() {
  if (!form.loanAmount || !form.loanTerm || form.loanAmount < 10000) { preview.value = null; return }
  const rate = 0.0435
  preview.value = form.repaymentMethod === 'EQUAL_PRINCIPAL'
    ? calcEqualPrincipal(form.loanAmount, form.loanTerm, rate)
    : calcEqualInstallment(form.loanAmount, form.loanTerm, rate)
}

async function handleSubmit() {
  errorMsg.value = ''
  submitting.value = true
  try {
    await applyLoan({ loanAmount: form.loanAmount, loanTerm: form.loanTerm, loanPurpose: form.loanPurpose, repaymentMethod: form.repaymentMethod })
    router.push('/loans')
  } catch (e: unknown) {
    errorMsg.value = e instanceof Error ? e.message : '提交失败'
  } finally { submitting.value = false }
}

function formatMoney(v: number) { return v?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }

calcPreview()
</script>

<style scoped>
.page { padding: var(--space-8); max-width: 720px; }
.page-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.page-desc { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-muted); margin-bottom: var(--space-8); }
.apply-form { padding: var(--space-8); }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-5); }
.form-label { display: block; font-size: var(--text-sm); font-weight: 500; color: var(--color-text-secondary); margin-bottom: var(--space-2); }
.form-input { width: 100%; padding: var(--space-3) var(--space-4); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); font-size: var(--text-base); outline: none; }
.form-input:focus { border-color: var(--color-primary); }
.form-textarea { resize: vertical; min-height: 80px; }
.form-hint { font-size: var(--text-xs); color: var(--color-text-muted); margin-top: var(--space-1); }
.form-error { color: var(--color-danger); font-size: var(--text-sm); }
.preview { margin-top: var(--space-6); padding: var(--space-5); background: rgba(99,102,241,.06); border: var(--border-glass); border-radius: var(--radius-md); }
.preview-title { font-size: var(--text-base); font-weight: 600; margin-bottom: var(--space-3); }
.preview-row { display: flex; justify-content: space-between; padding: var(--space-2) 0; font-size: var(--text-sm); color: var(--color-text-secondary); }
.preview-highlight { color: var(--color-accent); font-size: var(--text-lg); font-weight: 700; font-family: var(--font-mono); }
.form-actions { display: flex; gap: var(--space-4); margin-top: var(--space-6); }
.btn { display: inline-flex; align-items: center; gap: var(--space-2); padding: var(--space-2) var(--space-5); border-radius: var(--radius-md); font-size: var(--text-sm); font-weight: 500; transition: all var(--duration-fast); text-decoration: none; border: none; cursor: pointer; }
.btn-primary { background: var(--color-primary); color: #fff; }
.btn-ghost { background: transparent; color: var(--color-text-secondary); }
.btn-lg { padding: var(--space-3) var(--space-8); font-size: var(--text-base); }
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
@media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } }
</style>
