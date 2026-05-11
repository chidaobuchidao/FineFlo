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

defineProps<{ items: TimelineItem[] }>()
</script>

<style scoped>
.timeline { padding-left: var(--space-4); }
.timeline-item { position: relative; padding-left: var(--space-8); padding-bottom: var(--space-6); }
.timeline-item:not(.last)::before {
  content: ''; position: absolute; left: 7px; top: 16px; bottom: 0;
  width: 2px; background: var(--color-surface-active);
}
.timeline-dot {
  position: absolute; left: 0; top: 4px; width: 16px; height: 16px;
  border-radius: 50%; border: 2px solid var(--color-text-muted); background: var(--color-bg);
}
.dot-success { border-color: var(--color-success); background: rgba(16, 185, 129, 0.15); }
.dot-warning { border-color: var(--color-warning); background: rgba(245, 158, 11, 0.15); }
.dot-danger { border-color: var(--color-danger); background: rgba(239, 68, 68, 0.15); }
.timeline-title { font-size: var(--text-sm); font-weight: 500; color: var(--color-text); }
.timeline-time { font-size: var(--text-xs); color: var(--color-text-muted); margin-left: var(--space-4); }
.timeline-desc { margin-top: var(--space-1); font-size: var(--text-sm); color: var(--color-text-secondary); }
</style>
