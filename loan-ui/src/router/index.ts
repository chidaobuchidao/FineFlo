import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/EnterpriseRegister.vue'),
    meta: { guest: true },
  },
  {
    path: '/',
    redirect: '/login',
  },

  // ── Approver routes ──────────────────────────────────────
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/enterprises',
    name: 'EnterpriseList',
    component: () => import('@/views/EnterpriseList.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/enterprises/:id',
    name: 'EnterpriseDetail',
    component: () => import('@/views/EnterpriseDetail.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/approvals',
    name: 'ApprovalList',
    component: () => import('@/views/ApprovalList.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/approvals/:id',
    name: 'ApprovalDetail',
    component: () => import('@/views/ApprovalDetail.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/disbursements',
    name: 'DisbursementList',
    component: () => import('@/views/DisbursementList.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/approver-repayments',
    name: 'RepaymentList',
    component: () => import('@/views/RepaymentList.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/approver-overdues',
    name: 'OverdueList',
    component: () => import('@/views/OverdueList.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/StatisticsView.vue'),
    meta: { requiresAuth: true, role: 'APPROVER' },
  },

  // ── Enterprise routes ────────────────────────────────────
  {
    path: '/loans',
    name: 'MyLoans',
    component: () => import('@/views/EnterpriseLoans.vue'),
    meta: { requiresAuth: true, role: 'ENTERPRISE' },
  },
  {
    path: '/loans/apply',
    name: 'LoanApply',
    component: () => import('@/views/EnterpriseLoanApply.vue'),
    meta: { requiresAuth: true, role: 'ENTERPRISE' },
  },
  {
    path: '/loans/:id',
    name: 'LoanDetail',
    component: () => import('@/views/EnterpriseLoanDetail.vue'),
    meta: { requiresAuth: true, role: 'ENTERPRISE' },
  },
  {
    path: '/my-repayments',
    name: 'MyRepayments',
    component: () => import('@/views/EnterpriseRepayments.vue'),
    meta: { requiresAuth: true, role: 'ENTERPRISE' },
  },
  {
    path: '/calculator',
    name: 'Calculator',
    component: () => import('@/views/EnterpriseCalculator.vue'),
    meta: { requiresAuth: true, role: 'ENTERPRISE' },
  },
  {
    path: '/my-overdues',
    name: 'MyOverdues',
    component: () => import('@/views/EnterpriseOverdue.vue'),
    meta: { requiresAuth: true, role: 'ENTERPRISE' },
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const token = sessionStorage.getItem('token')
  const userStr = sessionStorage.getItem('user')

  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // Role-based guard
  if (to.meta.role && userStr) {
    try {
      const user = JSON.parse(userStr)
      const routeRole = to.meta.role as string
      if (routeRole === 'APPROVER' && user.role === 'ENTERPRISE') {
        next('/loans')
        return
      }
      if (routeRole === 'ENTERPRISE' && (user.role === 'APPROVER' || user.role === 'ADMIN')) {
        next('/dashboard')
        return
      }
    } catch { /* ignore */ }
  }

  // Redirect logged-in user away from login
  if (to.meta.guest && token && userStr) {
    try {
      const user = JSON.parse(userStr)
      next(user.role === 'ENTERPRISE' ? '/loans' : '/dashboard')
      return
    } catch { /* ignore */ }
  }

  next()
})

export default router
