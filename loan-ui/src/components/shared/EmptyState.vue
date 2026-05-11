<template>
  <div class="empty-state">
    <div class="empty-icon">
      <SvgIcon :name="icon" :size="48" />
    </div>
    <h3 class="empty-title">{{ title }}</h3>
    <p class="empty-description">{{ description }}</p>
    <button v-if="actionLabel" class="empty-action pressable focus-ring" @click="$emit('action')">
      {{ actionLabel }}
    </button>
  </div>
</template>

<script setup lang="ts">
import SvgIcon from '@/components/shared/SvgIcon.vue'

withDefaults(defineProps<{
  icon?: string
  title: string
  description: string
  actionLabel?: string
}>(), {
  icon: 'info',
})

defineEmits<{
  action: []
}>()
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-16) var(--space-8);
  text-align: center;
}

.empty-icon {
  color: var(--color-text-muted);
  margin-bottom: var(--space-4);
  opacity: 0.6;
}

.empty-title {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-2);
}

.empty-description {
  font-size: var(--text-sm);
  color: var(--color-text-muted);
  max-width: 320px;
  line-height: 1.6;
  margin-bottom: var(--space-6);
}

.empty-action {
  padding: var(--space-2) var(--space-6);
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--duration-fast) var(--ease-out-expo);
}

.empty-action:hover {
  filter: brightness(1.1);
  box-shadow: var(--shadow-glow);
}
</style>
