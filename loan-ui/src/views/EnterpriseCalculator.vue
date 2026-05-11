<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2 class="page-title">贷款计算器</h2>
        <p class="page-desc">预估还款金额，支持等额本息与等额本金两种模式</p>
      </div>
    </div>

    <div class="calc-grid">
      <!-- Input -->
      <div class="glass-card" style="padding: var(--space-6);">
        <h3 class="section-title">贷款参数</h3>
        <div class="form-stack">
          <div class="form-group">
            <label class="form-label">贷款金额（元）</label>
            <input v-model.number="amount" type="number" class="form-input" placeholder="如 500000" min="10000" />
          </div>
          <div class="form-group">
            <label class="form-label">贷款期限（月）</label>
            <select v-model.number="term" class="form-input">
              <option v-for="m in [3,6,12,24,36,48,60]" :key="m" :value="m">{{ m }} 个月</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">年利率（%）</label>
            <input v-model.number="ratePct" type="number" class="form-input" step="0.01" min="0.1" max="24" />
          </div>
          <div class="form-group">
            <label class="form-label">还款方式</label>
            <select v-model="mode" class="form-input" @change="calc">
              <option value="EQUAL_INSTALLMENT">等额本息</option>
              <option value="EQUAL_PRINCIPAL">等额本金</option>
            </select>
          </div>
          <button class="btn btn-primary btn-lg" @click="calc">计 算</button>
        </div>
      </div>

      <!-- Result -->
      <div>
        <div class="glass-card" style="padding: var(--space-6); margin-bottom: var(--space-5);">
          <h3 class="section-title">还款预估</h3>
          <div v-if="result">
            <div class="res-row"><span>月还款额（首期）</span><span class="res-highlight">¥{{ formatMoney(result.monthlyPayment) }}</span></div>
            <div class="res-row"><span>总利息</span><span>¥{{ formatMoney(result.totalInterest) }}</span></div>
            <div class="res-row"><span>总还款额</span><span>¥{{ formatMoney(result.totalPayment) }}</span></div>
          </div>
          <p v-else style="color:var(--color-text-muted);text-align:center;padding:var(--space-8)">输入参数后点击计算</p>
        </div>

        <div v-if="result" class="glass-card" style="padding: var(--space-6); max-height: 420px; overflow: auto;">
          <h3 class="section-title">还款明细表</h3>
          <table class="data-table">
            <thead><tr><th>期数</th><th>本金</th><th>利息</th><th>月供</th><th>剩余</th></tr></thead>
            <tbody>
              <tr v-for="s in schedule" :key="s.period">
                <td>{{ s.period }}</td>
                <td>¥{{ formatMoney(s.principal) }}</td>
                <td>¥{{ formatMoney(s.interest) }}</td>
                <td>¥{{ formatMoney(s.payment) }}</td>
                <td>¥{{ formatMoney(s.remaining) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

const amount = ref(500000)
const term = ref(12)
const ratePct = ref(4.35)
const mode = ref('EQUAL_INSTALLMENT')
const result = ref<{ monthlyPayment: number; totalInterest: number; totalPayment: number } | null>(null)
const schedule = ref<{ period: number; principal: number; interest: number; payment: number; remaining: number }[]>([])

function calc() {
  const rate = ratePct.value / 100
  if (mode.value === 'EQUAL_PRINCIPAL') calcEqualPrincipal(amount.value, term.value, rate)
  else calcEqualInstallment(amount.value, term.value, rate)
}

function calcEqualInstallment(p: number, m: number, r: number) {
  const mr = r / 12
  const payment = (p * mr * Math.pow(1 + mr, m)) / (Math.pow(1 + mr, m) - 1)
  let remaining = p, totalInterest = 0
  const sched: typeof schedule.value = []
  for (let i = 1; i <= m; i++) {
    const interest = Math.round(remaining * mr * 100) / 100
    const principal = Math.round((payment - interest) * 100) / 100
    remaining = Math.round((remaining - principal) * 100) / 100
    if (remaining < 0) remaining = 0
    totalInterest += interest
    sched.push({ period: i, principal, interest, payment: Math.round(payment * 100) / 100, remaining })
  }
  result.value = { monthlyPayment: Math.round(payment * 100) / 100, totalInterest: Math.round(totalInterest * 100) / 100, totalPayment: Math.round((p + totalInterest) * 100) / 100 }
  schedule.value = sched
}

function calcEqualPrincipal(p: number, m: number, r: number) {
  const mr = r / 12, mp = p / m
  let remaining = p, totalInterest = 0
  const sched: typeof schedule.value = []
  for (let i = 1; i <= m; i++) {
    const interest = Math.round(remaining * mr * 100) / 100
    const payment = Math.round((mp + interest) * 100) / 100
    remaining = Math.round((remaining - mp) * 100) / 100
    if (remaining < 0) remaining = 0
    totalInterest += interest
    sched.push({ period: i, principal: Math.round(mp * 100) / 100, interest, payment, remaining })
  }
  result.value = { monthlyPayment: Math.round((mp + p * mr) * 100) / 100, totalInterest: Math.round(totalInterest * 100) / 100, totalPayment: Math.round((p + totalInterest) * 100) / 100 }
  schedule.value = sched
}

function formatMoney(v: number) { return v?.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) }

calc()
</script>

<style scoped>
.page { padding: var(--space-8); }
.page-title { font-size: var(--text-2xl); font-weight: 700; color: var(--color-text); }
.page-desc { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-muted); margin-bottom: var(--space-6); }
.calc-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-5); align-items: start; }
.section-title { font-size: var(--text-base); font-weight: 600; margin-bottom: var(--space-4); padding-bottom: var(--space-3); border-bottom: var(--border-subtle); }
.form-stack { display: flex; flex-direction: column; gap: var(--space-4); }
.form-label { display: block; font-size: var(--text-sm); font-weight: 500; color: var(--color-text-secondary); margin-bottom: var(--space-2); }
.form-input { width: 100%; padding: var(--space-3) var(--space-4); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); font-size: var(--text-base); outline: none; }
.form-input:focus { border-color: var(--color-primary); }
.btn { display: inline-flex; align-items: center; justify-content: center; padding: var(--space-2) var(--space-5); border-radius: var(--radius-md); font-weight: 500; transition: all var(--duration-fast); border: none; cursor: pointer; }
.btn-primary { background: var(--color-primary); color: #fff; }
.btn-lg { padding: var(--space-3); font-size: var(--text-base); width: 100%; }
.res-row { display: flex; justify-content: space-between; padding: var(--space-2) 0; font-size: var(--text-sm); color: var(--color-text-secondary); }
.res-highlight { color: var(--color-accent); font-size: var(--text-xl); font-weight: 700; font-family: var(--font-mono); }
.data-table { width: 100%; border-collapse: collapse; font-size: var(--text-xs); }
.data-table th, .data-table td { padding: var(--space-2) var(--space-3); text-align: right; border-bottom: var(--border-subtle); }
.data-table th { color: var(--color-text-muted); font-weight: 600; text-align: right; }
.data-table tbody tr:hover { background: var(--color-surface-hover); }
@media (max-width: 768px) { .calc-grid { grid-template-columns: 1fr; } }
</style>
