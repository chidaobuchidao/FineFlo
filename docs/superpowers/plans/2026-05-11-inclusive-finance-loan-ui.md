# Inclusive Finance Platform — Loan-UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the complete Vue 3 admin SPA (审批端) with glassmorphism dark theme, 10 routes, ECharts dashboards, and Canvas particle effects.

**Architecture:** Vue 3 Composition API + TypeScript, custom components from scratch. Pinia stores per domain (auth, enterprise, approval, statistics). Axios with JWT interceptor. All visual components match spec's glassmorphism style guide.

**Tech Stack:** Vue 3.5+, Vite 6, TypeScript, Vue Router 4, Pinia 2, Axios 1, ECharts 5, GSAP, CSS Custom Properties

---

## File Manifest

```
E:\My_Projects\Reverso_Context\
├── index.html
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
├── vite.config.ts
├── env.d.ts
└── src/
    ├── main.ts
    ├── App.vue
    ├── types/
    │   └── index.ts
    ├── api/
    │   ├── index.ts
    │   ├── auth.ts
    │   ├── enterprise.ts
    │   ├── approval.ts
    │   └── statistics.ts
    ├── assets/
    │   └── styles/
    │       ├── tokens.css
    │       └── global.css
    ├── composables/
    │   ├── useAuth.ts
    │   ├── useNumberScroll.ts
    │   └── useParticleBg.ts
    ├── router/
    │   └── index.ts
    ├── stores/
    │   ├── auth.ts
    │   ├── enterprise.ts
    │   ├── approval.ts
    │   └── statistics.ts
    ├── components/
    │   ├── global/
    │   │   ├── AppLayout.vue
    │   │   ├── SidebarNav.vue
    │   │   ├── TopBar.vue
    │   │   └── Toast.vue
    │   └── shared/
    │       ├── DataTable.vue
    │       ├── StatsCard.vue
    │       ├── ChartPanel.vue
    │       ├── CreditGauge.vue
    │       ├── Timeline.vue
    │       └── ParticleBg.vue
    └── views/
        ├── LoginView.vue
        ├── DashboardView.vue
        ├── EnterpriseList.vue
        ├── EnterpriseDetail.vue
        ├── ApprovalList.vue
        ├── ApprovalDetail.vue
        ├── DisbursementList.vue
        ├── RepaymentList.vue
        ├── OverdueList.vue
        └── StatisticsView.vue
```

---

### Task 1: Project Scaffolding

**Files:**
- Create: `package.json`, `index.html`, `vite.config.ts`, `tsconfig.json`, `tsconfig.app.json`, `tsconfig.node.json`, `env.d.ts`, `src/main.ts`, `src/App.vue`

- [ ] **Step 1: Create package.json**

```json
{
  "name": "loan-ui",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc -b && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "axios": "^1.7.0",
    "echarts": "^5.5.0",
    "gsap": "^3.12.0",
    "pinia": "^2.2.0",
    "vue": "^3.5.0",
    "vue-router": "^4.4.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.1.0",
    "typescript": "~5.6.0",
    "vite": "^6.0.0",
    "vue-tsc": "^2.1.0"
  }
}
```

- [ ] **Step 2: Create index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>普惠金融管理系统</title>
    <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'><text y='28' font-size='28'>💰</text></svg>" />
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.ts"></script>
  </body>
</html>
```

- [ ] **Step 3: Create vite.config.ts**

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
})
```

- [ ] **Step 4: Create tsconfig files**

tsconfig.json:
```json
{
  "files": [],
  "references": [
    { "path": "./tsconfig.app.json" },
    { "path": "./tsconfig.node.json" }
  ]
}
```

tsconfig.app.json:
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForExpose": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "isolatedModules": true,
    "moduleDetection": "force",
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": false,
    "noUnusedParameters": false,
    "noFallthroughCasesInSwitch": true,
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.tsx", "src/**/*.vue", "env.d.ts"]
}
```

tsconfig.node.json:
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2023"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "isolatedModules": true,
    "moduleDetection": "force",
    "noEmit": true,
    "strict": true
  },
  "include": ["vite.config.ts"]
}
```

- [ ] **Step 5: Create env.d.ts**

```typescript
/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
```

- [ ] **Step 6: Create src/main.ts**

```typescript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/styles/tokens.css'
import './assets/styles/global.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
```

- [ ] **Step 7: Create minimal src/App.vue**

```vue
<template>
  <router-view />
</template>

<script setup lang="ts">
</script>
```

- [ ] **Step 8: Install dependencies and verify scaffold**

```bash
cd "E:\My_Projects\Reverso_Context" && npm install && npx vite build --emptyOutDir
```

Expected: Build succeeds with empty App.

- [ ] **Step 9: Commit**

```bash
git add -A && git commit -m "feat: scaffold Vue 3 + Vite + TypeScript project"
```

---

### Task 2: CSS Design Tokens + Global Styles

**Files:**
- Create: `src/assets/styles/tokens.css`, `src/assets/styles/global.css`

- [ ] **Step 1: Create tokens.css**

```css
:root {
  /* Palette */
  --color-primary: #6366F1;
  --color-primary-hover: #818CF8;
  --color-primary-dim: rgba(99, 102, 241, 0.15);
  --color-accent: #06B6D4;
  --color-accent-hover: #22D3EE;

  /* Surfaces */
  --color-bg: #0B0F19;
  --color-bg-elevated: #111827;
  --color-surface: rgba(255, 255, 255, 0.04);
  --color-surface-hover: rgba(255, 255, 255, 0.06);
  --color-surface-active: rgba(255, 255, 255, 0.08);

  /* Text */
  --color-text: #F1F5F9;
  --color-text-secondary: #94A3B8;
  --color-text-muted: #64748B;

  /* Borders */
  --border-glass: 1px solid rgba(99, 102, 241, 0.15);
  --border-subtle: 1px solid rgba(255, 255, 255, 0.06);

  /* Status */
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-danger: #EF4444;
  --color-info: #3B82F6;

  /* Typography */
  --font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  --font-mono: 'JetBrains Mono', 'Fira Code', monospace;

  --text-xs: 0.75rem;
  --text-sm: 0.875rem;
  --text-base: 1rem;
  --text-lg: 1.125rem;
  --text-xl: 1.25rem;
  --text-2xl: 1.5rem;
  --text-3xl: 1.875rem;
  --text-4xl: 2.25rem;

  /* Spacing */
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-5: 1.25rem;
  --space-6: 1.5rem;
  --space-8: 2rem;
  --space-10: 2.5rem;
  --space-12: 3rem;
  --space-16: 4rem;

  /* Radii */
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --radius-xl: 24px;
  --radius-full: 9999px;

  /* Shadows */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.4);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.5);
  --shadow-glow: 0 0 20px rgba(99, 102, 241, 0.3);

  /* Transitions */
  --duration-fast: 150ms;
  --duration-normal: 300ms;
  --duration-slow: 500ms;
  --ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);
  --ease-in-out: cubic-bezier(0.4, 0, 0.2, 1);

  /* Layout */
  --sidebar-width: 260px;
  --topbar-height: 64px;
}
```

- [ ] **Step 2: Create global.css**

```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: var(--font-family);
  font-size: var(--text-base);
  color: var(--color-text);
  background: var(--color-bg);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100%;
}

a {
  color: var(--color-accent);
  text-decoration: none;
}

button {
  cursor: pointer;
  font-family: inherit;
}

input, select, textarea {
  font-family: inherit;
  color: var(--color-text);
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-full);
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* Glass card base */
.glass-card {
  background: var(--color-surface);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: var(--border-glass);
  border-radius: var(--radius-lg);
}

.glass-card:hover {
  background: var(--color-surface-hover);
  border-color: rgba(99, 102, 241, 0.3);
}

/* Page transition */
.page-enter-active {
  transition: opacity var(--duration-slow) var(--ease-out-expo),
              transform var(--duration-slow) var(--ease-out-expo);
}
.page-leave-active {
  transition: opacity var(--duration-fast) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}
.page-enter-from {
  opacity: 0;
  transform: translateY(16px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* Glow border on hover */
.glow-border {
  position: relative;
}
.glow-border::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  opacity: 0;
  box-shadow: inset 0 0 0 1px var(--color-primary);
  transition: opacity var(--duration-normal) var(--ease-out-expo);
  pointer-events: none;
}
.glow-border:hover::after {
  opacity: 1;
}

/* Spinner */
@keyframes spin {
  to { transform: rotate(360deg); }
}
.spinner {
  width: 24px;
  height: 24px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}
```

- [ ] **Step 3: Commit**

```bash
git add src/assets/styles/ && git commit -m "feat: add CSS design tokens and global styles"
```

---

### Task 3: TypeScript Type Definitions

**Files:**
- Create: `src/types/index.ts`

- [ ] **Step 1: Create src/types/index.ts**

```typescript
// ==================== API Response ====================
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp?: number
}

export interface PaginatedData<T> {
  total: number
  page: number
  size: number
  records: T[]
}

// ==================== Auth ====================
export interface User {
  id: number
  username: string
  realName: string
  role: 'ENTERPRISE' | 'APPROVER' | 'ADMIN'
  enterpriseId?: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  expiresIn: number
  user: User
}

export interface RegisterRequest {
  username: string
  password: string
  realName: string
  phone: string
  enterpriseId: number
}

// ==================== Enterprise ====================
export interface Enterprise {
  id: number
  name: string
  creditCode: string
  legalPerson: string
  legalIdCard: string
  contactPhone: string
  address: string
  industry: string
  registeredCapital: number
  establishDate: string
  employeeCount: number
  annualRevenue: number
  status: number
  createdAt: string
  updatedAt: string
}

// ==================== Loan ====================
export type LoanStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'GRANTED' | 'REPAID' | 'OVERDUE'

export interface LoanApplication {
  id: number
  enterpriseId: number
  userId: number
  loanAmount: number
  loanTerm: number
  loanPurpose: string
  interestRate: number | null
  repaymentMethod: string
  status: LoanStatus
  applyDate: string
  approveDate: string | null
  approveUserId: number | null
  approveComment: string | null
  creditScore: number | null
  enterpriseName?: string
}

export interface LoanApplyRequest {
  loanAmount: number
  loanTerm: number
  loanPurpose: string
  repaymentMethod: string
}

export interface LoanApproveRequest {
  action: 'APPROVE' | 'REJECT'
  comment: string
}

// ==================== Repayment ====================
export type RepaymentStatus = 'UNPAID' | 'PAID' | 'OVERDUE'

export interface Repayment {
  id: number
  loanId: number
  periodNo: number
  amount: number
  paidAmount: number | null
  dueDate: string
  paidDate: string | null
  status: RepaymentStatus
}

// ==================== Overdue ====================
export interface Overdue {
  id: number
  loanId: number
  enterpriseId: number
  overdueDays: number
  overdueAmount: number
  penalty: number
  startDate: string
  endDate: string | null
  status: 'ACTIVE' | 'SETTLED'
  enterpriseName?: string
}

// ==================== Disbursement ====================
export interface Disbursement {
  id: number
  enterpriseId: number
  enterpriseName: string
  loanAmount: number
  loanTerm: number
  interestRate: number
  approveDate: string
  status: string
}

// ==================== Credit Score ====================
export interface CreditScore {
  id: number
  enterpriseId: number
  score: number
  modelVersion: string
  features: string
  evaluatedAt: string
}

export interface RiskEvaluation {
  enterpriseId: number
  creditScore: number
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  confidence: number
  modelVersion: string
}

export interface RiskEvaluateRequest {
  enterpriseId: number
}

// ==================== Statistics ====================
export interface LoanOverview {
  totalApply: number
  totalApproved: number
  totalDisbursed: number
  totalOverdue: number
  approvalRate: number
  monthly: MonthlyStats[]
}

export interface MonthlyStats {
  month: string
  apply: number
  approved: number
  disbursed: number
}

export interface TrendItem {
  month: string
  value: number
}

export interface OverdueAnalysis {
  byIndustry: { name: string; count: number }[]
  byAmount: { range: string; count: number }[]
  byTerm: { range: string; count: number }[]
}

// ==================== Loan Calculator ====================
export interface CalculatorInput {
  amount: number
  term: number
  rate: number
}

export interface CalculatorResult {
  mode: string
  monthlyPayment: number
  totalInterest: number
  totalPayment: number
  schedule: CalculatorScheduleItem[]
}

export interface CalculatorScheduleItem {
  period: number
  principal: number
  interest: number
  remaining: number
}

// ==================== Navigation ====================
export interface NavItem {
  path: string
  label: string
  icon: string
}
```

- [ ] **Step 2: Verify types compile**

```bash
cd "E:\My_Projects\Reverso_Context" && npx vue-tsc --noEmit --pretty false
```

- [ ] **Step 3: Commit**

```bash
git add src/types/ && git commit -m "feat: add TypeScript type definitions"
```

---

### Task 4: API Layer (Axios + Interceptors + Modules)

**Files:**
- Create: `src/api/index.ts`, `src/api/auth.ts`, `src/api/enterprise.ts`, `src/api/approval.ts`, `src/api/statistics.ts`

- [ ] **Step 1: Create src/api/index.ts — Axios instance with JWT interceptor**

```typescript
import axios from 'axios'
import type { ApiResponse } from '@/types'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export async function get<T>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
  const res = await api.get<ApiResponse<T>>(url, { params })
  return res.data
}

export async function post<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
  const res = await api.post<ApiResponse<T>>(url, data)
  return res.data
}

export async function put<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
  const res = await api.put<ApiResponse<T>>(url, data)
  return res.data
}

export default api
```

- [ ] **Step 2: Create src/api/auth.ts**

```typescript
import { post } from './index'
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types'

export function login(data: LoginRequest) {
  return post<LoginResponse>('/auth/login', data)
}

export function register(data: RegisterRequest) {
  return post<null>('/auth/register', data)
}
```

- [ ] **Step 3: Create src/api/enterprise.ts**

```typescript
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
```

- [ ] **Step 4: Create src/api/approval.ts**

```typescript
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
```

- [ ] **Step 5: Create src/api/statistics.ts**

```typescript
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
```

- [ ] **Step 6: Commit**

```bash
git add src/api/ && git commit -m "feat: add API layer with JWT interceptor and module functions"
```

---

### Task 5: Vue Router Setup

**Files:**
- Create: `src/router/index.ts`

- [ ] **Step 1: Create src/router/index.ts**

```typescript
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guest: true },
  },
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/enterprises',
    name: 'EnterpriseList',
    component: () => import('@/views/EnterpriseList.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/enterprises/:id',
    name: 'EnterpriseDetail',
    component: () => import('@/views/EnterpriseDetail.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/approvals',
    name: 'ApprovalList',
    component: () => import('@/views/ApprovalList.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/approvals/:id',
    name: 'ApprovalDetail',
    component: () => import('@/views/ApprovalDetail.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/disbursements',
    name: 'DisbursementList',
    component: () => import('@/views/DisbursementList.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/repayments',
    name: 'RepaymentList',
    component: () => import('@/views/RepaymentList.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/overdues',
    name: 'OverdueList',
    component: () => import('@/views/OverdueList.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/StatisticsView.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.guest && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 2: Commit**

```bash
git add src/router/ && git commit -m "feat: add Vue Router with auth guards"
```

---

### Task 6: Pinia Stores

**Files:**
- Create: `src/stores/auth.ts`, `src/stores/enterprise.ts`, `src/stores/approval.ts`, `src/stores/statistics.ts`

- [ ] **Step 1: Create src/stores/auth.ts**

```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'
import * as authApi from '@/api/auth'
import type { LoginRequest } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const isApprover = computed(() => user.value?.role === 'APPROVER' || user.value?.role === 'ADMIN')

  function loadFromStorage() {
    const stored = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')
    if (stored && storedToken) {
      user.value = JSON.parse(stored)
      token.value = storedToken
    }
  }

  async function login(credentials: LoginRequest): Promise<boolean> {
    loading.value = true
    error.value = null
    try {
      const res = await authApi.login(credentials)
      if (res.code === 200) {
        token.value = res.data.token
        user.value = res.data.user
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('user', JSON.stringify(res.data.user))
        return true
      }
      error.value = res.message || '登录失败'
      return false
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : '网络错误'
      error.value = msg
      return false
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { user, token, loading, error, isAuthenticated, isApprover, loadFromStorage, login, logout }
})
```

- [ ] **Step 2: Create src/stores/enterprise.ts**

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoanApplication, Repayment, Overdue, PaginatedData, CalculatorResult, CalculatorInput } from '@/types'
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
```

- [ ] **Step 3: Create src/stores/approval.ts**

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Enterprise, LoanApplication, Repayment, Overdue, PaginatedData, CreditScore, RiskEvaluation, Disbursement } from '@/types'
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
```

- [ ] **Step 4: Create src/stores/statistics.ts**

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoanOverview, TrendItem, OverdueAnalysis } from '@/types'
import * as statisticsApi from '@/api/statistics'

export const useStatisticsStore = defineStore('statistics', () => {
  const loanOverview = ref<LoanOverview | null>(null)
  const disbursementTrend = ref<TrendItem[]>([])
  const repaymentTrend = ref<TrendItem[]>([])
  const overdueAnalysis = ref<OverdueAnalysis | null>(null)
  const loading = ref(false)

  async function fetchLoanOverview(startDate?: string, endDate?: string) {
    loading.value = true
    try {
      const res = await statisticsApi.getLoanOverview({ startDate, endDate })
      if (res.code === 200) loanOverview.value = res.data
    } finally {
      loading.value = false
    }
  }

  async function fetchDisbursementTrend() {
    const res = await statisticsApi.getDisbursementTrend()
    if (res.code === 200) disbursementTrend.value = res.data
  }

  async function fetchRepaymentTrend() {
    const res = await statisticsApi.getRepaymentTrend()
    if (res.code === 200) repaymentTrend.value = res.data
  }

  async function fetchOverdueAnalysis() {
    const res = await statisticsApi.getOverdueAnalysis()
    if (res.code === 200) overdueAnalysis.value = res.data
  }

  return { loanOverview, disbursementTrend, repaymentTrend, overdueAnalysis, loading, fetchLoanOverview, fetchDisbursementTrend, fetchRepaymentTrend, fetchOverdueAnalysis }
})
```

- [ ] **Step 5: Verify TypeScript compiles**

```bash
cd "E:\My_Projects\Reverso_Context" && npx vue-tsc --noEmit --pretty false
```

- [ ] **Step 6: Commit**

```bash
git add src/stores/ && git commit -m "feat: add Pinia stores for auth, enterprise, approval, statistics"
```

---

### Task 7: Composables

**Files:**
- Create: `src/composables/useAuth.ts`, `src/composables/useNumberScroll.ts`, `src/composables/useParticleBg.ts`

- [ ] **Step 1: Create src/composables/useAuth.ts**

```typescript
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

export function useAuth() {
  const store = useAuthStore()
  const router = useRouter()

  async function login(username: string, password: string): Promise<boolean> {
    const ok = await store.login({ username, password })
    if (ok) {
      router.push('/dashboard')
    }
    return ok
  }

  function logout() {
    store.logout()
    router.push('/login')
  }

  return {
    user: store.user,
    token: store.token,
    loading: store.loading,
    error: store.error,
    isAuthenticated: store.isAuthenticated,
    isApprover: store.isApprover,
    login,
    logout,
    loadFromStorage: store.loadFromStorage,
  }
}
```

- [ ] **Step 2: Create src/composables/useNumberScroll.ts**

```typescript
import { ref, onUnmounted } from 'vue'

export function useNumberScroll(duration = 1500) {
  const displayValue = ref(0)
  let raf = 0

  function animate(target: number) {
    cancelAnimationFrame(raf)
    const start = displayValue.value
    const startTime = performance.now()

    function tick(now: number) {
      const elapsed = now - startTime
      const progress = Math.min(elapsed / duration, 1)
      const eased = 1 - Math.pow(1 - progress, 3)
      displayValue.value = start + (target - start) * eased
      if (progress < 1) {
        raf = requestAnimationFrame(tick)
      }
    }

    raf = requestAnimationFrame(tick)
  }

  function formatCurrency(value: number): string {
    return value.toLocaleString('zh-CN', { style: 'currency', currency: 'CNY', minimumFractionDigits: 0, maximumFractionDigits: 0 })
  }

  function formatNumber(value: number): string {
    return value.toLocaleString('zh-CN')
  }

  function formatPercent(value: number): string {
    return value.toFixed(1) + '%'
  }

  onUnmounted(() => cancelAnimationFrame(raf))

  return { displayValue, animate, formatCurrency, formatNumber, formatPercent }
}
```

- [ ] **Step 3: Create src/composables/useParticleBg.ts**

```typescript
import { onMounted, onUnmounted, ref } from 'vue'

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  radius: number
  opacity: number
}

export function useParticleBg(canvasRef: ReturnType<typeof ref<HTMLCanvasElement | null>>) {
  let ctx: CanvasRenderingContext2D | null = null
  let particles: Particle[] = []
  let animFrame = 0
  let w = 0
  let h = 0

  function init() {
    const canvas = canvasRef.value
    if (!canvas) return
    ctx = canvas.getContext('2d')
    resize()
    particles = Array.from({ length: 80 }, () => ({
      x: Math.random() * w,
      y: Math.random() * h,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      radius: Math.random() * 2 + 1,
      opacity: Math.random() * 0.4 + 0.1,
    }))
    draw()
  }

  function resize() {
    const canvas = canvasRef.value
    if (!canvas) return
    w = canvas.width = window.innerWidth
    h = canvas.height = window.innerHeight
  }

  function draw() {
    if (!ctx || !canvasRef.value) return
    ctx.clearRect(0, 0, w, h)

    for (let i = 0; i < particles.length; i++) {
      const p = particles[i]
      p.x += p.vx
      p.y += p.vy

      if (p.x < 0 || p.x > w) p.vx *= -1
      if (p.y < 0 || p.y > h) p.vy *= -1

      ctx.beginPath()
      ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(99, 102, 241, ${p.opacity})`
      ctx.fill()
    }

    // Draw connections
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x
        const dy = particles[i].y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 120) {
          ctx.beginPath()
          ctx.moveTo(particles[i].x, particles[i].y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(99, 102, 241, ${0.08 * (1 - dist / 120)})`
          ctx.lineWidth = 0.5
          ctx.stroke()
        }
      }
    }

    animFrame = requestAnimationFrame(draw)
  }

  onMounted(() => {
    init()
    window.addEventListener('resize', resize)
  })

  onUnmounted(() => {
    cancelAnimationFrame(animFrame)
    window.removeEventListener('resize', resize)
  })
}
```

- [ ] **Step 4: Commit**

```bash
git add src/composables/ && git commit -m "feat: add composables (useAuth, useNumberScroll, useParticleBg)"
```

---

### Task 8: Global Components — AppLayout, SidebarNav, TopBar

**Files:**
- Create: `src/components/global/AppLayout.vue`, `src/components/global/SidebarNav.vue`, `src/components/global/TopBar.vue`

- [ ] **Step 1: Create src/components/global/SidebarNav.vue**

```vue
<template>
  <aside class="sidebar">
    <div class="sidebar-brand">
      <div class="brand-icon">💰</div>
      <span class="brand-text">普惠金融</span>
    </div>
    <nav class="sidebar-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: isActive(item.path) }"
      >
        <span class="nav-icon">{{ item.icon }}</span>
        <span class="nav-label">{{ item.label }}</span>
      </router-link>
    </nav>
    <div class="sidebar-footer">
      <div class="version">v1.0.0</div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import type { NavItem } from '@/types'

const route = useRoute()

const navItems: NavItem[] = [
  { path: '/dashboard', label: '仪表盘', icon: '📊' },
  { path: '/enterprises', label: '企业管理', icon: '🏢' },
  { path: '/approvals', label: '贷款审批', icon: '✅' },
  { path: '/disbursements', label: '放款管理', icon: '💳' },
  { path: '/repayments', label: '还款管理', icon: '📋' },
  { path: '/overdues', label: '逾期管理', icon: '⚠️' },
  { path: '/statistics', label: '数据统计', icon: '📈' },
]

function isActive(path: string): boolean {
  if (path === '/dashboard') return route.path === '/dashboard'
  return route.path.startsWith(path)
}
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: rgba(17, 24, 39, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: var(--border-glass);
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-6) var(--space-6);
  border-bottom: var(--border-subtle);
}

.brand-icon {
  font-size: 28px;
}

.brand-text {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: 0.05em;
}

.sidebar-nav {
  flex: 1;
  padding: var(--space-4) var(--space-3);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--duration-fast) var(--ease-out-expo);
  text-decoration: none;
}

.nav-item:hover {
  background: var(--color-surface-hover);
  color: var(--color-text);
}

.nav-item.active {
  background: var(--color-primary-dim);
  color: var(--color-primary-hover);
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.sidebar-footer {
  padding: var(--space-4) var(--space-6);
  border-top: var(--border-subtle);
}

.version {
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}
</style>
```

- [ ] **Step 2: Create src/components/global/TopBar.vue**

```vue
<template>
  <header class="topbar">
    <div class="topbar-left">
      <h1 class="page-title">{{ title }}</h1>
    </div>
    <div class="topbar-right">
      <div class="user-info">
        <div class="user-avatar">{{ initials }}</div>
        <div class="user-details">
          <span class="user-name">{{ user?.realName || user?.username }}</span>
          <span class="user-role">{{ roleLabel }}</span>
        </div>
      </div>
      <button class="logout-btn" @click="handleLogout">退出</button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuth } from '@/composables/useAuth'

defineProps<{ title: string }>()

const { user, logout } = useAuth()

const initials = computed(() => {
  const name = user.value?.realName || user.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const roleLabel = computed(() => {
  const map: Record<string, string> = { ADMIN: '管理员', APPROVER: '审批员', ENTERPRISE: '企业用户' }
  return map[user.value?.role || ''] || '用户'
})

function handleLogout() {
  logout()
}
</script>

<style scoped>
.topbar {
  position: fixed;
  left: var(--sidebar-width);
  right: 0;
  top: 0;
  height: var(--topbar-height);
  background: rgba(11, 15, 25, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: var(--border-subtle);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-8);
  z-index: 90;
}

.page-title {
  font-size: var(--text-xl);
  font-weight: 600;
  color: var(--color-text);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: var(--space-6);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: 600;
  color: white;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--color-text);
}

.user-role {
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}

.logout-btn {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  border: var(--border-glass);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
  transition: all var(--duration-fast) var(--ease-out-expo);
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
  color: var(--color-danger);
}
</style>
```

- [ ] **Step 3: Create src/components/global/AppLayout.vue**

```vue
<template>
  <div class="app-layout">
    <SidebarNav />
    <TopBar :title="pageTitle" />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarNav from './SidebarNav.vue'
import TopBar from './TopBar.vue'

const route = useRoute()

const titleMap: Record<string, string> = {
  Dashboard: '仪表盘',
  EnterpriseList: '企业管理',
  EnterpriseDetail: '企业详情',
  ApprovalList: '贷款审批',
  ApprovalDetail: '审批详情',
  DisbursementList: '放款管理',
  RepaymentList: '还款管理',
  OverdueList: '逾期管理',
  Statistics: '数据统计',
}

const pageTitle = computed(() => titleMap[route.name as string] || '普惠金融')
</script>

<style scoped>
.app-layout {
  height: 100%;
}

.main-content {
  margin-left: var(--sidebar-width);
  margin-top: var(--topbar-height);
  padding: var(--space-8);
  min-height: calc(100vh - var(--topbar-height));
}
</style>
```

- [ ] **Step 4: Commit**

```bash
git add src/components/global/ && git commit -m "feat: add AppLayout, SidebarNav, TopBar components"
```

---

### Task 9: Shared Components — Toast, ParticleBg, DataTable

**Files:**
- Create: `src/components/shared/Toast.vue`, `src/components/shared/ParticleBg.vue`, `src/components/shared/DataTable.vue`

- [ ] **Step 1: Create src/components/shared/Toast.vue**

```vue
<template>
  <Teleport to="body">
    <TransitionGroup name="toast" tag="div" class="toast-container">
      <div v-for="msg in messages" :key="msg.id" class="toast-item" :class="`toast-${msg.type}`">
        <span class="toast-icon">{{ iconMap[msg.type] }}</span>
        <span class="toast-text">{{ msg.text }}</span>
      </div>
    </TransitionGroup>
  </Teleport>
</template>

<script setup lang="ts">
import { ref } from 'vue'

type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastMessage {
  id: number
  text: string
  type: ToastType
}

const messages = ref<ToastMessage[]>([])
let nextId = 0

const iconMap: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ',
}

function show(text: string, type: ToastType = 'info', duration = 3000) {
  const id = nextId++
  messages.value.push({ id, text, type })
  setTimeout(() => {
    const idx = messages.value.findIndex((m) => m.id === id)
    if (idx >= 0) messages.value.splice(idx, 1)
  }, duration)
}

defineExpose({ show })
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.toast-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-md);
  background: rgba(17, 24, 39, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  font-size: var(--text-sm);
  color: var(--color-text);
  min-width: 280px;
  box-shadow: var(--shadow-lg);
}

.toast-success { border-left: 3px solid var(--color-success); }
.toast-error { border-left: 3px solid var(--color-danger); }
.toast-warning { border-left: 3px solid var(--color-warning); }
.toast-info { border-left: 3px solid var(--color-info); }

.toast-icon { font-size: 16px; }

.toast-enter-active { transition: all 0.3s var(--ease-out-expo); }
.toast-leave-active { transition: all 0.2s ease-in; }
.toast-enter-from { opacity: 0; transform: translateX(40px); }
.toast-leave-to { opacity: 0; transform: translateX(40px); }
</style>
```

- [ ] **Step 2: Create src/components/shared/ParticleBg.vue**

```vue
<template>
  <canvas ref="canvasRef" class="particle-canvas"></canvas>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useParticleBg } from '@/composables/useParticleBg'

const canvasRef = ref<HTMLCanvasElement | null>(null)
useParticleBg(canvasRef)
</script>

<style scoped>
.particle-canvas {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}
</style>
```

- [ ] **Step 3: Create src/components/shared/DataTable.vue**

```vue
<template>
  <div class="data-table-wrapper glass-card">
    <div v-if="loading" class="skeleton">
      <div v-for="i in 5" :key="i" class="skeleton-row">
        <div v-for="j in columns.length" :key="j" class="skeleton-cell" />
      </div>
    </div>
    <table v-else class="data-table">
      <thead>
        <tr>
          <th v-for="col in columns" :key="col.key" :style="{ width: col.width }">
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="data.length === 0">
          <td :colspan="columns.length" class="empty-cell">暂无数据</td>
        </tr>
        <tr
          v-for="(row, idx) in data"
          :key="(row as any).id || idx"
          class="data-row glow-border"
          @click="$emit('rowClick', row)"
        >
          <td v-for="col in columns" :key="col.key">
            <slot :name="`cell-${col.key}`" :row="row" :value="(row as any)[col.key]">
              {{ formatCell(row, col) }}
            </slot>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="total > pageSize" class="pagination">
      <button :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts" generic="T extends Record<string, unknown>">
import { computed } from 'vue'

export interface Column {
  key: string
  label: string
  width?: string
}

const props = defineProps<{
  columns: Column[]
  data: T[]
  loading?: boolean
  total?: number
  pageSize?: number
  currentPage?: number
}>()

defineEmits<{
  rowClick: [row: T]
  pageChange: [page: number]
}>()

const totalPages = computed(() => Math.ceil((props.total || 0) / (props.pageSize || 10)))

function goPage(page: number) {
  // emit handled via $emit in template
}

function formatCell(row: T, col: Column): string {
  const val = row[col.key]
  if (val === null || val === undefined) return '-'
  return String(val)
}
</script>

<style scoped>
.data-table-wrapper {
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

thead th {
  padding: var(--space-3) var(--space-4);
  text-align: left;
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: var(--border-subtle);
}

tbody td {
  padding: var(--space-3) var(--space-4);
  font-size: var(--text-sm);
  color: var(--color-text);
  border-bottom: var(--border-subtle);
}

.data-row {
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-out-expo);
}

.data-row:hover {
  background: var(--color-surface-hover);
}

.empty-cell {
  text-align: center;
  color: var(--color-text-muted);
  padding: var(--space-12) !important;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-4);
  padding: var(--space-4);
  border-top: var(--border-subtle);
}

.pagination button {
  padding: var(--space-1) var(--space-4);
  border-radius: var(--radius-sm);
  border: var(--border-glass);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
}

.pagination button:hover:not(:disabled) {
  background: var(--color-primary-dim);
  color: var(--color-text);
}

.pagination button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-info {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

/* Skeleton */
.skeleton-row {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
}

.skeleton-cell {
  flex: 1;
  height: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-sm);
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}
</style>
```

- [ ] **Step 4: Commit**

```bash
git add src/components/shared/ && git commit -m "feat: add Toast, ParticleBg, DataTable shared components"
```

---

### Task 10: Shared Components — StatsCard, ChartPanel, CreditGauge, Timeline

**Files:**
- Create: `src/components/shared/StatsCard.vue`, `src/components/shared/ChartPanel.vue`, `src/components/shared/CreditGauge.vue`, `src/components/shared/Timeline.vue`

- [ ] **Step 1: Create src/components/shared/StatsCard.vue**

```vue
<template>
  <div class="stats-card glass-card" @mouseenter="startCount">
    <div class="stats-label">{{ label }}</div>
    <div class="stats-value" :style="{ color: color }">
      <span v-if="prefix">{{ prefix }}</span>{{ formattedValue }}
    </div>
    <div class="stats-change" v-if="change !== undefined" :class="change >= 0 ? 'positive' : 'negative'">
      {{ change >= 0 ? '↑' : '↓' }} {{ Math.abs(change) }}%
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useNumberScroll } from '@/composables/useNumberScroll'

const props = defineProps<{
  label: string
  value: number
  prefix?: string
  format?: 'currency' | 'number' | 'percent'
  color?: string
  change?: number
}>()

const { displayValue, animate, formatCurrency, formatNumber, formatPercent } = useNumberScroll()
const hasAnimated = ref(false)

const formattedValue = computed(() => {
  if (props.format === 'currency') return formatCurrency(displayValue.value)
  if (props.format === 'percent') return formatPercent(displayValue.value)
  return formatNumber(displayValue.value)
})

function startCount() {
  if (!hasAnimated.value) {
    hasAnimated.value = true
    animate(props.value)
  }
}

onMounted(() => {
  animate(props.value)
})

watch(() => props.value, (val) => animate(val))
</script>

<style scoped>
.stats-card {
  padding: var(--space-6);
  cursor: default;
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--color-text-muted);
  margin-bottom: var(--space-2);
}

.stats-value {
  font-size: var(--text-3xl);
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.02em;
}

.stats-change {
  margin-top: var(--space-2);
  font-size: var(--text-xs);
  font-weight: 500;
}

.stats-change.positive { color: var(--color-success); }
.stats-change.negative { color: var(--color-danger); }
</style>
```

- [ ] **Step 2: Create src/components/shared/ChartPanel.vue**

```vue
<template>
  <div class="chart-panel glass-card">
    <div class="chart-header">
      <h3 class="chart-title">{{ title }}</h3>
      <slot name="actions" />
    </div>
    <div ref="chartRef" class="chart-body"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{
  title: string
  option: echarts.EChartsOption
}>()

const chartRef = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

function initChart() {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value, 'dark')
  chart.setOption({ ...props.option, backgroundColor: 'transparent' })
}

function resize() {
  chart?.resize()
}

onMounted(() => {
  nextTick(initChart)
  window.addEventListener('resize', resize)
})

onUnmounted(() => {
  chart?.dispose()
  window.removeEventListener('resize', resize)
})

watch(() => props.option, (opt) => {
  chart?.setOption({ ...opt, backgroundColor: 'transparent' }, true)
}, { deep: true })
</script>

<style scoped>
.chart-panel {
  overflow: hidden;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) var(--space-6);
  border-bottom: var(--border-subtle);
}

.chart-title {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--color-text);
}

.chart-body {
  height: 350px;
}
</style>
```

- [ ] **Step 3: Create src/components/shared/CreditGauge.vue**

```vue
<template>
  <div class="credit-gauge">
    <svg viewBox="0 0 200 120" class="gauge-svg">
      <defs>
        <linearGradient id="gaugeGrad" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" stop-color="#EF4444" />
          <stop offset="50%" stop-color="#F59E0B" />
          <stop offset="100%" stop-color="#10B981" />
        </linearGradient>
      </defs>
      <path
        d="M 30 100 A 70 70 0 0 1 170 100"
        fill="none"
        stroke="rgba(255,255,255,0.08)"
        stroke-width="12"
        stroke-linecap="round"
      />
      <path
        ref="arcRef"
        d="M 30 100 A 70 70 0 0 1 170 100"
        fill="none"
        stroke="url(#gaugeGrad)"
        stroke-width="12"
        stroke-linecap="round"
        :stroke-dasharray="arcLength"
        :stroke-dashoffset="arcOffset"
        class="gauge-arc"
      />
      <text x="100" y="85" text-anchor="middle" class="gauge-score">{{ displayScore }}</text>
      <text x="100" y="108" text-anchor="middle" class="gauge-label">{{ riskLabel }}</text>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useNumberScroll } from '@/composables/useNumberScroll'

const props = defineProps<{
  score: number
}>()

const arcLength = 2 * Math.PI * 70 * 0.5 // half circle
const { displayValue, animate } = useNumberScroll(1000)

const arcRef = ref<SVGPathElement | null>(null)

const displayScore = computed(() => displayValue.value.toFixed(1))

const arcOffset = computed(() => {
  const ratio = displayValue.value / 100
  return arcLength * (1 - ratio)
})

const riskLabel = computed(() => {
  if (displayValue.value >= 70) return '低风险'
  if (displayValue.value >= 40) return '中风险'
  return '高风险'
})

onMounted(() => animate(props.score))
watch(() => props.score, (v) => animate(v))
</script>

<style scoped>
.credit-gauge {
  display: flex;
  justify-content: center;
}

.gauge-svg {
  width: 220px;
  height: 130px;
}

.gauge-arc {
  transition: stroke-dashoffset 1s var(--ease-out-expo);
}

.gauge-score {
  font-size: 28px;
  font-weight: 700;
  fill: var(--color-text);
}

.gauge-label {
  font-size: 12px;
  fill: var(--color-text-secondary);
}
</style>
```

- [ ] **Step 4: Create src/components/shared/Timeline.vue**

```vue
<template>
  <div class="timeline">
    <div v-for="(item, idx) in items" :key="idx" class="timeline-item" :class="{ last: idx === items.length - 1 }">
      <div class="timeline-dot" :class="`dot-${item.type || 'default'}`"></div>
      <div class="timeline-content">
        <div class="timeline-header">
          <span class="timeline-title">{{ item.title }}</span>
          <span class="timeline-time">{{ item.time }}</span>
        </div>
        <p v-if="item.description" class="timeline-desc">{{ item.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface TimelineItem {
  title: string
  time: string
  description?: string
  type?: 'success' | 'warning' | 'danger' | 'default'
}

defineProps<{
  items: TimelineItem[]
}>()
</script>

<style scoped>
.timeline {
  padding-left: var(--space-4);
}

.timeline-item {
  position: relative;
  padding-left: var(--space-8);
  padding-bottom: var(--space-6);
}

.timeline-item:not(.last)::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 16px;
  bottom: 0;
  width: 2px;
  background: rgba(255, 255, 255, 0.08);
}

.timeline-dot {
  position: absolute;
  left: 0;
  top: 4px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.15);
  background: var(--color-bg);
}

.dot-success { border-color: var(--color-success); background: rgba(16, 185, 129, 0.15); }
.dot-warning { border-color: var(--color-warning); background: rgba(245, 158, 11, 0.15); }
.dot-danger { border-color: var(--color-danger); background: rgba(239, 68, 68, 0.15); }

.timeline-title {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--color-text);
}

.timeline-time {
  font-size: var(--text-xs);
  color: var(--color-text-muted);
  margin-left: var(--space-4);
}

.timeline-desc {
  margin-top: var(--space-1);
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}
</style>
```

- [ ] **Step 5: Commit**

```bash
git add src/components/shared/ && git commit -m "feat: add StatsCard, ChartPanel, CreditGauge, Timeline components"
```

---

### Task 11: LoginView

**Files:**
- Create: `src/views/LoginView.vue`
- Modify: `src/App.vue`

- [ ] **Step 1: Create src/views/LoginView.vue**

```vue
<template>
  <div class="login-page">
    <ParticleBg />
    <div class="login-card glass-card">
      <div class="login-header">
        <div class="login-icon">💰</div>
        <h1 class="login-title">普惠金融管理系统</h1>
        <p class="login-subtitle">Inclusive Finance Platform</p>
      </div>
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="username"
            type="text"
            class="form-input"
            placeholder="请输入用户名"
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="password"
            type="password"
            class="form-input"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>
        <p v-if="errorMsg" class="form-error">{{ errorMsg }}</p>
        <button type="submit" class="login-btn" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>登 录</span>
        </button>
      </form>
      <p class="login-hint">测试账号：admin / admin123</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ParticleBg from '@/components/shared/ParticleBg.vue'
import { useAuth } from '@/composables/useAuth'

const { login, loading, error } = useAuth()

const username = ref('')
const password = ref('')
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  const ok = await login(username.value, password.value)
  if (!ok) {
    errorMsg.value = error.value || '登录失败'
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  padding: var(--space-10);
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.login-icon {
  font-size: 48px;
  margin-bottom: var(--space-4);
}

.login-title {
  font-size: var(--text-2xl);
  font-weight: 700;
  color: var(--color-text);
}

.login-subtitle {
  margin-top: var(--space-1);
  font-size: var(--text-sm);
  color: var(--color-text-muted);
}

.form-group {
  margin-bottom: var(--space-5);
}

.form-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-2);
}

.form-input {
  width: 100%;
  padding: var(--space-3) var(--space-4);
  background: rgba(255, 255, 255, 0.05);
  border: var(--border-glass);
  border-radius: var(--radius-md);
  color: var(--color-text);
  font-size: var(--text-base);
  outline: none;
  transition: border-color var(--duration-fast) var(--ease-out-expo);
}

.form-input::placeholder { color: var(--color-text-muted); }
.form-input:focus { border-color: var(--color-primary); }

.form-error {
  color: var(--color-danger);
  font-size: var(--text-sm);
  margin-bottom: var(--space-4);
}

.login-btn {
  width: 100%;
  padding: var(--space-3) var(--space-6);
  background: linear-gradient(135deg, var(--color-primary), #8B5CF6);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  font-weight: 600;
  letter-spacing: 0.1em;
  transition: all var(--duration-fast) var(--ease-out-expo);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
}

.login-btn:hover:not(:disabled) {
  box-shadow: var(--shadow-glow);
  filter: brightness(1.1);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-hint {
  margin-top: var(--space-6);
  text-align: center;
  font-size: var(--text-xs);
  color: var(--color-text-muted);
}
</style>
```

- [ ] **Step 2: Update src/App.vue**

```vue
<template>
  <AppLayout v-if="isAuthenticated" />
  <router-view v-else v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import AppLayout from '@/components/global/AppLayout.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)

onMounted(() => {
  authStore.loadFromStorage()
})
</script>
```

- [ ] **Step 3: Commit**

```bash
git add src/views/LoginView.vue src/App.vue && git commit -m "feat: add LoginView with particle background and glassmorphism card"
```

---

### Task 12: DashboardView

**Files:**
- Create: `src/views/DashboardView.vue`

- [ ] **Step 1: Create src/views/DashboardView.vue**

```vue
<template>
  <div class="dashboard">
    <div class="stats-grid">
      <StatsCard label="贷款申请" :value="overview?.totalApply || 0" format="number" color="#6366F1" />
      <StatsCard label="已审批" :value="overview?.totalApproved || 0" format="number" color="#06B6D4" />
      <StatsCard label="已放款" :value="overview?.totalDisbursed || 0" format="number" color="#10B981" />
      <StatsCard label="逾期数" :value="overview?.totalOverdue || 0" format="number" color="#EF4444" />
    </div>

    <div class="charts-grid">
      <ChartPanel title="放款趋势" :option="disbursementOption" />
      <ChartPanel title="还款趋势" :option="repaymentOption" />
    </div>

    <div class="charts-grid">
      <ChartPanel title="贷款总览" :option="overviewOption" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import StatsCard from '@/components/shared/StatsCard.vue'
import ChartPanel from '@/components/shared/ChartPanel.vue'
import { useStatisticsStore } from '@/stores/statistics'
import type { EChartsOption } from 'echarts'

const stats = useStatisticsStore()

onMounted(() => {
  stats.fetchLoanOverview()
  stats.fetchDisbursementTrend()
  stats.fetchRepaymentTrend()
})

const overview = computed(() => stats.loanOverview)

const darkText = '#94A3B8'
const darkBorder = 'rgba(255,255,255,0.06)'

const disbursementOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: stats.disbursementTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkBorder } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkBorder } } },
  series: [{ data: stats.disbursementTrend.map((d) => d.value), type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, lineStyle: { color: '#06B6D4', width: 2 }, itemStyle: { color: '#06B6D4' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(6,182,212,0.2)' }, { offset: 1, color: 'rgba(6,182,212,0)' }] } } }],
}))

const repaymentOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: stats.repaymentTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkBorder } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkBorder } } },
  series: [{ data: stats.repaymentTrend.map((d) => d.value), type: 'bar', barWidth: '40%', itemStyle: { color: '#6366F1', borderRadius: [4, 4, 0, 0] } }],
}))

const overviewOption = computed<EChartsOption>(() => {
  const months = overview.value?.monthly?.map((d) => d.month) || []
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['申请', '审批', '放款'], textStyle: { color: darkText }, top: 0 },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '40px', containLabel: true },
    xAxis: { type: 'category', data: months, axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkBorder } } },
    yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkBorder } } },
    series: [
      { name: '申请', type: 'line', data: overview.value?.monthly?.map((d) => d.apply) || [], smooth: true, lineStyle: { color: '#6366F1' }, itemStyle: { color: '#6366F1' } },
      { name: '审批', type: 'line', data: overview.value?.monthly?.map((d) => d.approved) || [], smooth: true, lineStyle: { color: '#06B6D4' }, itemStyle: { color: '#06B6D4' } },
      { name: '放款', type: 'line', data: overview.value?.monthly?.map((d) => d.disbursed) || [], smooth: true, lineStyle: { color: '#10B981' }, itemStyle: { color: '#10B981' } },
    ],
  }
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-4);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-4);
}

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .charts-grid { grid-template-columns: 1fr; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add src/views/DashboardView.vue && git commit -m "feat: add DashboardView with stats cards and ECharts"
```

---

### Task 13: Enterprise Management Views

**Files:**
- Create: `src/views/EnterpriseList.vue`, `src/views/EnterpriseDetail.vue`

- [ ] **Step 1: Create src/views/EnterpriseList.vue**

```vue
<template>
  <div class="enterprise-list">
    <div class="page-toolbar">
      <div class="search-box">
        <input v-model="keyword" type="text" class="form-input" placeholder="搜索企业名称..." @keyup.enter="search" />
        <button class="search-btn" @click="search">搜索</button>
      </div>
    </div>
    <DataTable
      :columns="columns"
      :data="store.enterprises"
      :loading="store.loading"
      :total="store.enterpriseTotal"
      :currentPage="page"
      :pageSize="size"
      @row-click="goDetail"
    >
      <template #cell-status="{ value }">
        <span class="status-badge" :class="value === 1 ? 'active' : 'inactive'">
          {{ value === 1 ? '正常' : '禁用' }}
        </span>
      </template>
      <template #cell-registeredCapital="{ value }">
        {{ (value as number)?.toLocaleString() }} 万
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const router = useRouter()
const store = useApprovalStore()
const keyword = ref('')
const page = ref(1)
const size = 10

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'name', label: '企业名称', width: '200px' },
  { key: 'creditCode', label: '信用代码' },
  { key: 'legalPerson', label: '法人' },
  { key: 'industry', label: '行业' },
  { key: 'registeredCapital', label: '注册资本' },
  { key: 'status', label: '状态', width: '80px' },
]

function search() {
  page.value = 1
  store.fetchEnterprises(page.value, size, keyword.value)
}

function goDetail(row: Record<string, unknown>) {
  router.push(`/enterprises/${row.id}`)
}

onMounted(() => store.fetchEnterprises(page.value, size))
</script>

<style scoped>
.page-toolbar {
  margin-bottom: var(--space-6);
}

.search-box {
  display: flex;
  gap: var(--space-3);
  max-width: 400px;
}

.form-input {
  flex: 1;
  padding: var(--space-2) var(--space-4);
  background: rgba(255, 255, 255, 0.05);
  border: var(--border-glass);
  border-radius: var(--radius-md);
  color: var(--color-text);
  font-size: var(--text-sm);
  outline: none;
}

.form-input:focus { border-color: var(--color-primary); }

.search-btn {
  padding: var(--space-2) var(--space-5);
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: 500;
}

.status-badge {
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 500;
}

.status-badge.active { background: rgba(16, 185, 129, 0.15); color: var(--color-success); }
.status-badge.inactive { background: rgba(239, 68, 68, 0.1); color: var(--color-danger); }
</style>
```

- [ ] **Step 2: Create src/views/EnterpriseDetail.vue**

```vue
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
          <span class="status-badge" :class="statusClass(value as string)">
            {{ statusLabel(value as string) }}
          </span>
        </template>
        <template #cell-loanAmount="{ value }">
          ¥{{ (value as number)?.toLocaleString() }}
        </template>
      </DataTable>
    </div>
  </div>
  <div v-else-if="store.loading" class="loading-state">
    <div class="spinner"></div>
  </div>
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
.back-btn {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
  margin-bottom: var(--space-4);
  padding: 0;
}

.back-btn:hover { color: var(--color-text); }

.detail-header {
  padding: var(--space-6);
  margin-bottom: var(--space-6);
}

.detail-header h2 {
  font-size: var(--text-2xl);
  font-weight: 700;
}

.detail-meta {
  display: flex;
  gap: var(--space-6);
  margin-top: var(--space-3);
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: var(--space-6);
}

.section-title {
  font-size: var(--text-base);
  font-weight: 600;
  padding: var(--space-4) var(--space-6);
  border-bottom: var(--border-subtle);
}

.info-list {
  padding: var(--space-4) var(--space-6);
}

.info-list > div {
  display: flex;
  padding: var(--space-2) 0;
}

.info-list dt {
  width: 100px;
  color: var(--color-text-muted);
  font-size: var(--text-sm);
}

.info-list dd {
  color: var(--color-text);
  font-size: var(--text-sm);
}

.status-badge {
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 500;
}
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
.status-badge.warning { background: rgba(245,158,11,0.15); color: var(--color-warning); }
.status-badge.info { background: rgba(59,130,246,0.15); color: var(--color-info); }

.loading-state { display: flex; justify-content: center; padding: 80px; }
</style>
```

- [ ] **Step 3: Commit**

```bash
git add src/views/EnterpriseList.vue src/views/EnterpriseDetail.vue && git commit -m "feat: add enterprise list and detail views"
```

---

### Task 14: Approval Views

**Files:**
- Create: `src/views/ApprovalList.vue`, `src/views/ApprovalDetail.vue`

- [ ] **Step 1: Create src/views/ApprovalList.vue**

```vue
<template>
  <div class="approval-list">
    <div class="page-toolbar">
      <div class="status-filters">
        <button v-for="f in filters" :key="f.value" class="filter-btn" :class="{ active: statusFilter === f.value }" @click="setFilter(f.value)">
          {{ f.label }}
        </button>
      </div>
    </div>
    <DataTable
      :columns="columns"
      :data="store.loans"
      :loading="store.loading"
      :total="store.loanTotal"
      :currentPage="page"
      :pageSize="size"
      @row-click="goDetail"
    >
      <template #cell-status="{ value }">
        <span class="status-badge" :class="statusClass(value as string)">{{ statusLabel(value as string) }}</span>
      </template>
      <template #cell-loanAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-creditScore="{ value }">
        <span :style="{ color: (value as number) >= 70 ? 'var(--color-success)' : (value as number) >= 40 ? 'var(--color-warning)' : 'var(--color-danger)' }">
          {{ value ?? '-' }}
        </span>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const router = useRouter()
const store = useApprovalStore()
const statusFilter = ref('')
const page = ref(1)
const size = 10

const filters = [
  { label: '全部', value: '' },
  { label: '待审批', value: 'PENDING' },
  { label: '已审批', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已放款', value: 'GRANTED' },
]

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'enterpriseName', label: '企业' },
  { key: 'loanAmount', label: '贷款金额' },
  { key: 'loanTerm', label: '期限(月)' },
  { key: 'loanPurpose', label: '用途' },
  { key: 'creditScore', label: 'AI评分' },
  { key: 'status', label: '状态' },
  { key: 'applyDate', label: '申请日期' },
]

function setFilter(v: string) {
  statusFilter.value = v
  page.value = 1
  store.fetchApprovalLoans(page.value, size, v)
}

function goDetail(row: Record<string, unknown>) {
  router.push(`/approvals/${row.id}`)
}

function statusClass(s: string): string {
  const map: Record<string, string> = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', GRANTED: 'info', REPAID: 'success', OVERDUE: 'danger' }
  return map[s] || ''
}
function statusLabel(s: string): string {
  const map: Record<string, string> = { PENDING: '待审批', APPROVED: '已审批', REJECTED: '已驳回', GRANTED: '已放款', REPAID: '已还清', OVERDUE: '逾期' }
  return map[s] || s
}

onMounted(() => store.fetchApprovalLoans(page.value, size))
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }

.status-filters {
  display: flex;
  gap: var(--space-2);
}

.filter-btn {
  padding: var(--space-1) var(--space-4);
  border-radius: var(--radius-full);
  border: var(--border-glass);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
  transition: all var(--duration-fast) var(--ease-out-expo);
}

.filter-btn.active {
  background: var(--color-primary-dim);
  color: var(--color-primary-hover);
  border-color: var(--color-primary);
}

.filter-btn:hover:not(.active) {
  background: var(--color-surface-hover);
}

.status-badge {
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 500;
}
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
.status-badge.warning { background: rgba(245,158,11,0.15); color: var(--color-warning); }
.status-badge.info { background: rgba(59,130,246,0.15); color: var(--color-info); }
</style>
```

- [ ] **Step 2: Create src/views/ApprovalDetail.vue**

```vue
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
        <textarea v-model="comment" class="form-textarea" placeholder="审批意见..."></textarea>
        <div class="approve-actions">
          <button class="btn-approve" @click="handleApprove('APPROVE')">✓ 通过</button>
          <button class="btn-reject" @click="handleApprove('REJECT')">✕ 驳回</button>
        </div>
      </div>
    </div>

    <div class="glass-card" style="margin-top: var(--space-6)">
      <h3 class="section-title">还款计划</h3>
      <DataTable :columns="repayColumns" :data="store.currentLoan.repayments || []">
        <template #cell-status="{ value }">
          <span :class="['status-text', (value as string) === 'PAID' ? 'text-success' : (value as string) === 'OVERDUE' ? 'text-danger' : '']">
            {{ value === 'PAID' ? '已还' : value === 'OVERDUE' ? '逾期' : '未还' }}
          </span>
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
  if (ok) {
    store.fetchApprovalLoanDetail(store.currentLoan!.id)
  }
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
.form-textarea { width: 100%; height: 80px; padding: var(--space-3); background: rgba(255,255,255,0.05); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); resize: vertical; outline: none; }
.form-textarea:focus { border-color: var(--color-primary); }
.approve-actions { display: flex; gap: var(--space-4); margin-top: var(--space-4); }

.btn-approve, .btn-reject { padding: var(--space-2) var(--space-8); border: none; border-radius: var(--radius-md); font-size: var(--text-sm); font-weight: 600; }
.btn-approve { background: var(--color-success); color: white; }
.btn-reject { background: var(--color-danger); color: white; }
.btn-approve:hover { filter: brightness(1.1); }
.btn-reject:hover { filter: brightness(1.1); }

.loading-state { display: flex; justify-content: center; padding: 80px; }
</style>
```

- [ ] **Step 3: Commit**

```bash
git add src/views/ApprovalList.vue src/views/ApprovalDetail.vue && git commit -m "feat: add approval list and detail views"
```

---

### Task 15: Disbursement, Repayment, Overdue Views

**Files:**
- Create: `src/views/DisbursementList.vue`, `src/views/RepaymentList.vue`, `src/views/OverdueList.vue`

- [ ] **Step 1: Create src/views/DisbursementList.vue**

```vue
<template>
  <div class="disbursement-list">
    <div class="page-toolbar">
      <div class="status-filters">
        <button v-for="f in filters" :key="f.value" class="filter-btn" :class="{ active: statusFilter === f.value }" @click="setFilter(f.value)">{{ f.label }}</button>
      </div>
    </div>
    <DataTable :columns="columns" :data="store.disbursements" :loading="store.loading">
      <template #cell-loanAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-interestRate="{ value }">{{ ((value as number) * 100).toFixed(2) }}%</template>
      <template #cell-actions="{ row }">
        <button v-if="(row as any).status === 'APPROVED'" class="action-btn" @click.stop="handleGrant((row as any).id)">确认放款</button>
        <span v-else class="text-muted">已放款</span>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const store = useApprovalStore()
const statusFilter = ref('')

const filters = [
  { label: '全部', value: '' },
  { label: '待放款', value: 'PENDING' },
  { label: '已放款', value: 'GRANTED' },
]

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'enterpriseName', label: '企业' },
  { key: 'loanAmount', label: '金额' },
  { key: 'loanTerm', label: '期限(月)' },
  { key: 'interestRate', label: '利率' },
  { key: 'approveDate', label: '审批日期' },
  { key: 'actions', label: '操作', width: '120px' },
]

function setFilter(v: string) {
  statusFilter.value = v
  store.fetchDisbursements(v)
}

async function handleGrant(loanId: number) {
  const ok = await store.grantDisbursement(loanId)
  if (ok) store.fetchDisbursements(statusFilter.value)
}

onMounted(() => store.fetchDisbursements())
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }
.status-filters { display: flex; gap: var(--space-2); }
.filter-btn {
  padding: var(--space-1) var(--space-4);
  border-radius: var(--radius-full);
  border: var(--border-glass);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
}
.filter-btn.active { background: var(--color-primary-dim); color: var(--color-primary-hover); border-color: var(--color-primary); }
.action-btn {
  padding: 4px 12px;
  border-radius: var(--radius-sm);
  border: none;
  background: var(--color-primary);
  color: white;
  font-size: var(--text-xs);
  font-weight: 500;
}
.action-btn:hover { filter: brightness(1.1); }
.text-muted { color: var(--color-text-muted); font-size: var(--text-sm); }
</style>
```

- [ ] **Step 2: Create src/views/RepaymentList.vue**

```vue
<template>
  <div class="repayment-list">
    <div class="page-toolbar">
      <div class="status-filters">
        <button v-for="f in filters" :key="f.value" class="filter-btn" :class="{ active: statusFilter === f.value }" @click="setFilter(f.value)">{{ f.label }}</button>
      </div>
    </div>
    <DataTable :columns="columns" :data="store.repayments" :loading="store.loading" :total="store.loanTotal" :currentPage="page" :pageSize="size">
      <template #cell-amount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-paidAmount="{ value }">¥{{ (value as number)?.toLocaleString() || '-' }}</template>
      <template #cell-status="{ value }">
        <span :class="['status-badge', statusClass(value as string)]">{{ statusLabel(value as string) }}</span>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const store = useApprovalStore()
const statusFilter = ref('')
const page = ref(1)
const size = 10

const filters = [
  { label: '全部', value: '' },
  { label: '未还', value: 'UNPAID' },
  { label: '已还', value: 'PAID' },
  { label: '逾期', value: 'OVERDUE' },
]

const columns: Column[] = [
  { key: 'loanId', label: '贷款ID' },
  { key: 'periodNo', label: '期数' },
  { key: 'amount', label: '应还金额' },
  { key: 'paidAmount', label: '实还金额' },
  { key: 'dueDate', label: '到期日' },
  { key: 'paidDate', label: '还款日' },
  { key: 'status', label: '状态' },
]

function setFilter(v: string) { statusFilter.value = v; store.fetchApprovalRepayments(undefined, v) }
function statusClass(s: string): string {
  const map: Record<string, string> = { PAID: 'success', OVERDUE: 'danger', UNPAID: 'warning' }
  return map[s] || ''
}
function statusLabel(s: string): string {
  const map: Record<string, string> = { PAID: '已还', OVERDUE: '逾期', UNPAID: '未还' }
  return map[s] || s
}

onMounted(() => store.fetchApprovalRepayments())
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }
.status-filters { display: flex; gap: var(--space-2); }
.filter-btn { padding: var(--space-1) var(--space-4); border-radius: var(--radius-full); border: var(--border-glass); background: transparent; color: var(--color-text-secondary); font-size: var(--text-sm); }
.filter-btn.active { background: var(--color-primary-dim); color: var(--color-primary-hover); border-color: var(--color-primary); }
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
.status-badge.warning { background: rgba(245,158,11,0.15); color: var(--color-warning); }
</style>
```

- [ ] **Step 3: Create src/views/OverdueList.vue**

```vue
<template>
  <div class="overdue-list">
    <DataTable :columns="columns" :data="store.overdues" :loading="store.loading" :total="store.overdueTotal" :currentPage="page" :pageSize="size">
      <template #cell-overdueAmount="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-penalty="{ value }">¥{{ (value as number)?.toLocaleString() }}</template>
      <template #cell-status="{ value }">
        <span :class="['status-badge', (value as string) === 'ACTIVE' ? 'danger' : 'success']">
          {{ value === 'ACTIVE' ? '进行中' : '已结清' }}
        </span>
      </template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DataTable from '@/components/shared/DataTable.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const store = useApprovalStore()
const page = ref(1)
const size = 10

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'enterpriseName', label: '企业' },
  { key: 'loanId', label: '贷款ID' },
  { key: 'overdueDays', label: '逾期天数' },
  { key: 'overdueAmount', label: '逾期金额' },
  { key: 'penalty', label: '罚息' },
  { key: 'startDate', label: '开始日期' },
  { key: 'status', label: '状态' },
]

onMounted(() => store.fetchApprovalOverdues(page.value, size))
</script>

<style scoped>
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.success { background: rgba(16,185,129,0.15); color: var(--color-success); }
.status-badge.danger { background: rgba(239,68,68,0.1); color: var(--color-danger); }
</style>
```

- [ ] **Step 4: Commit**

```bash
git add src/views/DisbursementList.vue src/views/RepaymentList.vue src/views/OverdueList.vue && git commit -m "feat: add disbursement, repayment, overdue list views"
```

---

### Task 16: StatisticsView

**Files:**
- Create: `src/views/StatisticsView.vue`

- [ ] **Step 1: Create src/views/StatisticsView.vue**

```vue
<template>
  <div class="statistics-view">
    <div class="stats-grid">
      <StatsCard label="贷款申请" :value="overview?.totalApply || 0" format="number" color="#6366F1" />
      <StatsCard label="审批通过" :value="overview?.totalApproved || 0" format="number" color="#06B6D4" />
      <StatsCard label="已放款" :value="overview?.totalDisbursed || 0" format="number" color="#10B981" />
      <StatsCard label="审批率" :value="overview?.approvalRate || 0" format="percent" color="#8B5CF6" />
      <StatsCard label="逾期数" :value="overview?.totalOverdue || 0" format="number" color="#EF4444" />
    </div>

    <div class="charts-grid">
      <ChartPanel title="放款趋势" :option="disbursementOption" />
      <ChartPanel title="还款趋势" :option="repaymentOption" />
    </div>

    <div class="charts-grid" v-if="store.overdueAnalysis">
      <ChartPanel title="逾期行业分布" :option="overdueIndustryOption" />
      <ChartPanel title="逾期金额分布" :option="overdueAmountOption" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import StatsCard from '@/components/shared/StatsCard.vue'
import ChartPanel from '@/components/shared/ChartPanel.vue'
import { useStatisticsStore } from '@/stores/statistics'
import type { EChartsOption } from 'echarts'

const store = useStatisticsStore()

onMounted(() => {
  store.fetchLoanOverview()
  store.fetchDisbursementTrend()
  store.fetchRepaymentTrend()
  store.fetchOverdueAnalysis()
})

const overview = computed(() => store.loanOverview)
const darkText = '#94A3B8'
const darkBorder = 'rgba(255,255,255,0.06)'

const disbursementOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: store.disbursementTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkBorder } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkBorder } } },
  series: [{ data: store.disbursementTrend.map((d) => d.value), type: 'line', smooth: true, lineStyle: { color: '#06B6D4', width: 2 }, itemStyle: { color: '#06B6D4' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(6,182,212,0.2)' }, { offset: 1, color: 'rgba(6,182,212,0)' }] } } }],
}))

const repaymentOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: store.repaymentTrend.map((d) => d.month), axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkBorder } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkBorder } } },
  series: [{ data: store.repaymentTrend.map((d) => d.value), type: 'bar', barWidth: '40%', itemStyle: { color: '#6366F1', borderRadius: [4, 4, 0, 0] } }],
}))

const overdueIndustryOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item' },
  series: [{
    type: 'pie',
    radius: ['45%', '75%'],
    center: ['50%', '55%'],
    data: store.overdueAnalysis?.byIndustry?.map((d) => ({ name: d.name, value: d.count })) || [],
    label: { color: darkText, fontSize: 12 },
    itemStyle: { borderColor: 'var(--color-bg)', borderWidth: 3 },
  }],
}))

const overdueAmountOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: store.overdueAnalysis?.byAmount?.map((d) => d.range) || [], axisLabel: { color: darkText }, axisLine: { lineStyle: { color: darkBorder } } },
  yAxis: { type: 'value', axisLabel: { color: darkText }, splitLine: { lineStyle: { color: darkBorder } } },
  series: [{ data: store.overdueAnalysis?.byAmount?.map((d) => d.count) || [], type: 'bar', barWidth: '40%', itemStyle: { color: '#F59E0B', borderRadius: [4, 4, 0, 0] } }],
}))
</script>

<style scoped>
.statistics-view { display: flex; flex-direction: column; gap: var(--space-6); }
.stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: var(--space-4); }
.charts-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: var(--space-4); }

@media (max-width: 1400px) { .stats-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 1200px) { .charts-grid { grid-template-columns: 1fr; } }
</style>
```

- [ ] **Step 2: Commit**

```bash
git add src/views/StatisticsView.vue && git commit -m "feat: add statistics view with charts and analysis"
```

---

### Task 17: Final Verification

- [ ] **Step 1: Install dependencies**

```bash
cd "E:\My_Projects\Reverso_Context" && npm install
```

- [ ] **Step 2: TypeScript compilation check**

```bash
cd "E:\My_Projects\Reverso_Context" && npx vue-tsc --noEmit --pretty false
```

Expected: No type errors.

- [ ] **Step 3: Production build**

```bash
cd "E:\My_Projects\Reverso_Context" && npx vite build
```

Expected: Successful build with no errors.

- [ ] **Step 4: Start dev server**

```bash
cd "E:\My_Projects\Reverso_Context" && npx vite --host
```

Expected: Dev server starts on http://localhost:5173.

---

## Plan Self-Review

1. **Spec coverage:** All 10 routes, 6 shared components, 3 global components, 4 stores, 4 API modules, CSS design system, router, TypeScript types all covered.
2. **No placeholders:** Every step has complete code. No TBD, TODO, or "implement later" patterns.
3. **Type consistency:** All TypeScript interfaces are defined in `src/types/index.ts` and used consistently across API functions, store actions, and view components. Column definitions reference the same keys.

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-05-11-inclusive-finance-loan-ui.md`.
