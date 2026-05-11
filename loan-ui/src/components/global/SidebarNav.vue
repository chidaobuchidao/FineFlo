<template>
  <aside class="sidebar" :class="{ collapsed }">
    <div class="sidebar-brand">
      <div class="brand-icon">
        <SvgIcon name="enterprise" :size="28" />
      </div>
      <transition name="fade-text">
        <span v-if="!collapsed" class="brand-text">普惠金融</span>
      </transition>
    </div>

    <nav class="sidebar-nav">
      <router-link
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        :class="{ active: isActive(item.path) }"
        :title="collapsed ? item.label : undefined"
      >
        <SvgIcon :name="item.icon" :size="20" class="nav-icon-svg" />
        <transition name="fade-text">
          <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
        </transition>
      </router-link>
    </nav>

    <div class="sidebar-footer">
      <button
        class="collapse-toggle pressable"
        @click="toggleCollapsed"
        :title="collapsed ? '展开侧栏' : '收起侧栏'"
      >
        <SvgIcon :name="collapsed ? 'chevronRight' : 'chevronLeft'" :size="18" />
      </button>
      <transition name="fade-text">
        <span v-if="!collapsed" class="version">v1.0.0</span>
      </transition>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import type { NavItem } from '@/types'
import SvgIcon from '@/components/shared/SvgIcon.vue'

const route = useRoute()
const auth = useAuthStore()

const collapsed = ref(false)

function loadCollapsedState() {
  const stored = localStorage.getItem('sidebar-collapsed')
  collapsed.value = stored === 'true'
}

function toggleCollapsed() {
  collapsed.value = !collapsed.value
  localStorage.setItem('sidebar-collapsed', String(collapsed.value))
}

function updateSidebarWidth() {
  const width = collapsed.value ? '72px' : '260px'
  document.documentElement.style.setProperty('--sidebar-width', width)
}

onMounted(() => {
  loadCollapsedState()
  updateSidebarWidth()
})

watch(collapsed, () => {
  updateSidebarWidth()
})

const enterpriseNav: NavItem[] = [
  { path: '/loans', label: '贷款列表', icon: 'search' },
  { path: '/loans/apply', label: '贷款申请', icon: 'plus' },
  { path: '/my-repayments', label: '还款计划', icon: 'repayment' },
  { path: '/my-overdues', label: '逾期记录', icon: 'warning' },
  { path: '/calculator', label: '贷款计算器', icon: 'statistics' },
]

const approverNav: NavItem[] = [
  { path: '/dashboard', label: '仪表盘', icon: 'dashboard' },
  { path: '/enterprises', label: '企业管理', icon: 'enterprise' },
  { path: '/approvals', label: '贷款审批', icon: 'approval' },
  { path: '/disbursements', label: '放款管理', icon: 'disbursement' },
  { path: '/approver-repayments', label: '还款管理', icon: 'repayment' },
  { path: '/approver-overdues', label: '逾期管理', icon: 'overdue' },
  { path: '/statistics', label: '数据统计', icon: 'statistics' },
]

const navItems = computed(() => auth.isEnterprise ? enterpriseNav : approverNav)

function isActive(path: string): boolean {
  if (path === '/dashboard') return route.path === '/dashboard'
  return route.path.startsWith(path)
}
</script>

<style scoped>
/*
  Sidebar is always-dark regardless of theme.
  All colors are hardcoded to dark-theme values so text
  remains readable on the dark background in any theme.
*/
.sidebar {
  --sb-bg: rgba(17, 24, 39, 0.9);
  --sb-border: rgba(99, 102, 241, 0.12);
  --sb-text: #F1F5F9;
  --sb-text-secondary: #94A3B8;
  --sb-text-muted: #64748B;
  --sb-hover-bg: rgba(255, 255, 255, 0.06);
  --sb-active-bg: rgba(99, 102, 241, 0.15);
  --sb-active-text: #818CF8;
  --sb-primary: #818CF8;

  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: var(--sb-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid var(--sb-border);
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition: width 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-6) var(--space-4);
  border-bottom: 1px solid var(--sb-border);
  min-height: 65px;
}

.sidebar.collapsed .sidebar-brand {
  justify-content: center;
  padding: var(--space-6) var(--space-2);
}

.brand-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--sb-primary);
}

.brand-text {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--sb-text);
  letter-spacing: 0.05em;
  white-space: nowrap;
}

.sidebar-nav {
  flex: 1;
  padding: var(--space-4) var(--space-3);
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar.collapsed .sidebar-nav {
  padding: var(--space-4) var(--space-2);
  align-items: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  color: var(--sb-text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--duration-fast) var(--ease-out-expo);
  text-decoration: none;
  white-space: nowrap;
  overflow: hidden;
}

.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: var(--space-3);
  gap: 0;
}

.nav-item:hover {
  background: var(--sb-hover-bg);
  color: var(--sb-text);
}

.nav-item.active {
  background: var(--sb-active-bg);
  color: var(--sb-active-text);
}

.nav-icon-svg {
  flex-shrink: 0;
}

.nav-label {
  overflow: hidden;
  white-space: nowrap;
}

.sidebar-footer {
  padding: var(--space-3) var(--space-4);
  border-top: 1px solid var(--sb-border);
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.sidebar.collapsed .sidebar-footer {
  justify-content: center;
  padding: var(--space-3) var(--space-2);
  flex-direction: column;
  gap: var(--space-2);
}

.collapse-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--sb-border);
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--sb-text-muted);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-out-expo);
  flex-shrink: 0;
}

.collapse-toggle:hover {
  background: var(--sb-hover-bg);
  color: var(--sb-text);
  border-color: var(--sb-primary);
}

.version {
  font-size: var(--text-xs);
  color: var(--sb-text-muted);
  white-space: nowrap;
}

/* Fade transition for text elements */
.fade-text-enter-active {
  transition: opacity 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.fade-text-leave-active {
  transition: opacity 0.15s ease-in;
}
.fade-text-enter-from,
.fade-text-leave-to {
  opacity: 0;
}
</style>
