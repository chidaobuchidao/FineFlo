<template>
  <header class="topbar">
    <div class="topbar-left">
      <h1 class="page-title">{{ title }}</h1>
    </div>
    <div class="topbar-right">
      <div class="theme-toggle">
        <button
          v-for="opt in themeOptions"
          :key="opt.value"
          class="theme-option"
          :class="{ active: themeStore.mode === opt.value }"
          @click="themeStore.setMode(opt.value)"
          :title="opt.label"
        >
          {{ opt.icon }}
        </button>
      </div>
      <div class="user-info">
        <div class="user-avatar">{{ initials }}</div>
        <div class="user-details">
          <span class="user-name">{{ user?.realName || user?.username }}</span>
          <span class="user-role">{{ roleLabel }}</span>
        </div>
      </div>
      <button class="logout-btn pressable" @click="handleLogout">退出</button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useThemeStore } from '@/stores/theme'

defineProps<{ title: string }>()

const { user, logout } = useAuth()
const themeStore = useThemeStore()

const themeOptions = [
  { value: 'dark' as const, label: '深色', icon: '🌙' },
  { value: 'light' as const, label: '护眼', icon: '☀️' },
  { value: 'system' as const, label: '跟随系统', icon: '💻' },
]

const initials = computed(() => {
  const name = user.value?.realName || user.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const roleLabel = computed(() => {
  const map: Record<string, string> = { ADMIN: '管理员', APPROVER: '审批员', ENTERPRISE: '企业用户' }
  return map[user.value?.role || ''] || '用户'
})

function handleLogout() { logout() }
</script>

<style scoped>
.topbar {
  position: fixed;
  left: var(--sidebar-width);
  right: 0;
  top: 0;
  height: var(--topbar-height);
  background: var(--color-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: var(--border-subtle);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-8);
  z-index: 90;
}

.page-title { font-size: var(--text-xl); font-weight: 600; color: var(--color-text); }

.topbar-right { display: flex; align-items: center; gap: var(--space-6); }

.user-info { display: flex; align-items: center; gap: var(--space-3); }

.user-avatar {
  width: 36px; height: 36px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  display: flex; align-items: center; justify-content: center;
  font-size: var(--text-sm); font-weight: 600; color: white;
}

.user-details { display: flex; flex-direction: column; }
.user-name { font-size: var(--text-sm); font-weight: 500; color: var(--color-text); }
.user-role { font-size: var(--text-xs); color: var(--color-text-muted); }

.logout-btn {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  border: var(--border-glass);
  background: transparent;
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
  transition: all var(--duration-fast) var(--ease-out-expo);
}
.logout-btn:hover { background: rgba(239, 68, 68, 0.1); border-color: rgba(239, 68, 68, 0.3); color: var(--color-danger); }

.theme-toggle {
  display: flex;
  align-items: center;
  background: var(--color-surface);
  border: var(--border-subtle);
  border-radius: var(--radius-full);
  padding: 2px;
  gap: 2px;
}

.theme-option {
  width: 32px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  border-radius: var(--radius-full);
  font-size: 14px;
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-out-expo);
  line-height: 1;
}

.theme-option.active {
  background: var(--color-primary);
}

.theme-option:not(.active):hover {
  background: var(--color-surface-hover);
}
</style>
