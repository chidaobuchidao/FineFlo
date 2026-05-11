<template>
  <div class="error-banner" :class="{ dismissable }">
    <span class="error-icon">⚠</span>
    <div class="error-content">
      <p class="error-message">{{ message }}</p>
      <p v-if="detail" class="error-detail">{{ detail }}</p>
    </div>
    <button class="error-retry pressable focus-ring" @click="$emit('retry')">重试</button>
    <button v-if="dismissable" class="error-dismiss pressable" @click="$emit('dismiss')">✕</button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message: string
  detail?: string
  dismissable?: boolean
}>()

defineEmits<{
  retry: []
  dismiss: []
}>()
</script>

<style scoped>
.error-banner {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  border-radius: var(--radius-md);
  background: rgba(239, 68, 68, 0.08);
  border-left: 3px solid var(--color-danger);
}

.error-icon {
  font-size: 18px;
  flex-shrink: 0;
  margin-top: 1px;
}

.error-content {
  flex: 1;
  min-width: 0;
}

.error-message {
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--color-danger);
  margin: 0;
}

.error-detail {
  font-size: var(--text-xs);
  color: var(--color-text-muted);
  margin: var(--space-1) 0 0;
  line-height: 1.5;
}

.error-retry {
  flex-shrink: 0;
  padding: var(--space-1) var(--space-4);
  background: rgba(239, 68, 68, 0.15);
  color: var(--color-danger);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 500;
  transition: all var(--duration-fast) var(--ease-out-expo);
}

.error-retry:hover {
  background: rgba(239, 68, 68, 0.25);
}

.error-dismiss {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: var(--color-text-muted);
  font-size: var(--text-sm);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: color var(--duration-fast) var(--ease-out-expo);
}

.error-dismiss:hover {
  color: var(--color-text);
}
</style>
