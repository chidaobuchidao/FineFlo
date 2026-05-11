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
  success: '✓', error: '✕', warning: '⚠', info: 'ℹ',
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
  position: fixed; top: 24px; right: 24px; z-index: 9999;
  display: flex; flex-direction: column; gap: 8px;
}
.toast-item {
  display: flex; align-items: center; gap: var(--space-3);
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-md);
  background: rgba(17, 24, 39, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  font-size: var(--text-sm); color: #F1F5F9;
  min-width: 280px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}
.toast-success { border-left: 3px solid var(--color-success); }
.toast-error { border-left: 3px solid var(--color-danger); }
.toast-warning { border-left: 3px solid var(--color-warning); }
.toast-info { border-left: 3px solid var(--color-info); }
.toast-icon { font-size: 16px; }
.toast-enter-active { transition: all 0.4s var(--spring-bouncy); }
.toast-leave-active { transition: all 0.2s ease-in; }
.toast-enter-from { opacity: 0; transform: translateX(40px); }
.toast-leave-to { opacity: 0; transform: translateX(40px); }
</style>
