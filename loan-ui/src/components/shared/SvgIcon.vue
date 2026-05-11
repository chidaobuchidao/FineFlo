<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    class="svg-icon"
    aria-hidden="true"
  >
    <template v-if="name === 'dashboard'">
      <rect x="3" y="3" width="7" height="7" rx="1" />
      <rect x="14" y="3" width="7" height="7" rx="1" />
      <rect x="3" y="14" width="7" height="7" rx="1" />
      <rect x="14" y="14" width="7" height="7" rx="1" />
    </template>
    <template v-else-if="name === 'enterprise'">
      <rect x="4" y="10" width="16" height="11" rx="1" />
      <path d="M9 21V17H15V21" />
      <path d="M9 10V7C9 5.343 10.343 4 12 4C13.657 4 15 5.343 15 7V10" />
      <line x1="9" y1="13" x2="9" y2="14" />
      <line x1="12" y1="13" x2="12" y2="14" />
      <line x1="15" y1="13" x2="15" y2="14" />
    </template>
    <template v-else-if="name === 'approval'">
      <rect x="4" y="3" width="16" height="18" rx="2" />
      <polyline points="8,12 11,15 16,9" />
    </template>
    <template v-else-if="name === 'disbursement'">
      <circle cx="12" cy="12" r="9" />
      <path d="M12 7V12L15 14" />
      <line x1="8" y1="10" x2="8" y2="10.01" stroke-width="3" stroke-linecap="round" />
    </template>
    <template v-else-if="name === 'repayment'">
      <polyline points="1,4 1,10 7,10" />
      <polyline points="23,20 23,14 17,14" />
      <path d="M20.49 15C18.5 11.5 15.5 9 12 9C9.5 9 7.2 10.2 5.7 12.1" />
      <path d="M3.51 9C5.5 12.5 8.5 15 12 15C14.5 15 16.8 13.8 18.3 11.9" />
    </template>
    <template v-else-if="name === 'overdue'">
      <circle cx="12" cy="12" r="9" />
      <polyline points="12,6 12,12 16,14" />
      <line x1="12" y1="16" x2="12.01" y2="16" stroke-width="3" stroke-linecap="round" />
    </template>
    <template v-else-if="name === 'statistics'">
      <rect x="3" y="14" width="4" height="7" rx="0.5" />
      <rect x="10" y="9" width="4" height="12" rx="0.5" />
      <rect x="17" y="5" width="4" height="16" rx="0.5" />
    </template>
    <template v-else-if="name === 'search'">
      <circle cx="10.5" cy="10.5" r="6.5" />
      <line x1="15.5" y1="15.5" x2="21" y2="21" />
    </template>
    <template v-else-if="name === 'filter'">
      <polygon points="22,3 2,3 10,12.46 10,19 14,21 14,12.46" />
    </template>
    <template v-else-if="name === 'sort'">
      <line x1="8" y1="6" x2="8" y2="18" />
      <polyline points="4,10 8,6 12,10" />
      <line x1="16" y1="18" x2="16" y2="6" />
      <polyline points="20,14 16,18 12,14" />
    </template>
    <template v-else-if="name === 'chevronLeft'">
      <polyline points="15,18 9,12 15,6" />
    </template>
    <template v-else-if="name === 'chevronRight'">
      <polyline points="9,18 15,12 9,6" />
    </template>
    <template v-else-if="name === 'menu'">
      <line x1="3" y1="6" x2="21" y2="6" />
      <line x1="3" y1="12" x2="21" y2="12" />
      <line x1="3" y1="18" x2="21" y2="18" />
    </template>
    <template v-else-if="name === 'close' || name === 'xmark'">
      <line x1="18" y1="6" x2="6" y2="18" />
      <line x1="6" y1="6" x2="18" y2="18" />
    </template>
    <template v-else-if="name === 'expand'">
      <polyline points="15,3 21,3 21,9" />
      <polyline points="9,21 3,21 3,15" />
      <line x1="21" y1="3" x2="14" y2="10" />
      <line x1="3" y1="21" x2="10" y2="14" />
    </template>
    <template v-else-if="name === 'collapse'">
      <polyline points="4,8 10,2 16,8" />
      <polyline points="4,16 10,22 16,16" />
    </template>
    <template v-else-if="name === 'sun'">
      <circle cx="12" cy="12" r="4" />
      <line x1="12" y1="2" x2="12" y2="4" />
      <line x1="12" y1="20" x2="12" y2="22" />
      <line x1="2" y1="12" x2="4" y2="12" />
      <line x1="20" y1="12" x2="22" y2="12" />
      <line x1="4.93" y1="4.93" x2="6.34" y2="6.34" />
      <line x1="17.66" y1="17.66" x2="19.07" y2="19.07" />
      <line x1="4.93" y1="19.07" x2="6.34" y2="17.66" />
      <line x1="17.66" y1="6.34" x2="19.07" y2="4.93" />
    </template>
    <template v-else-if="name === 'moon'">
      <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
    </template>
    <template v-else-if="name === 'monitor'">
      <rect x="2" y="3" width="20" height="14" rx="2" />
      <line x1="8" y1="21" x2="16" y2="21" />
      <line x1="12" y1="17" x2="12" y2="21" />
    </template>
    <template v-else-if="name === 'eye'">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </template>
    <template v-else-if="name === 'eyeOff'">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
      <line x1="1" y1="1" x2="23" y2="23" />
      <path d="M14.12 14.12a3 3 0 1 1-4.24-4.24" />
    </template>
    <template v-else-if="name === 'download'">
      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
      <polyline points="7,10 12,15 17,10" />
      <line x1="12" y1="15" x2="12" y2="3" />
    </template>
    <template v-else-if="name === 'fullscreen'">
      <polyline points="15,3 21,3 21,9" />
      <polyline points="9,21 3,21 3,15" />
      <polyline points="21,3 14,10" />
      <polyline points="3,21 10,14" />
    </template>
    <template v-else-if="name === 'logout'">
      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
      <polyline points="16,17 21,12 16,7" />
      <line x1="21" y1="12" x2="9" y2="12" />
    </template>
    <template v-else-if="name === 'plus'">
      <line x1="12" y1="5" x2="12" y2="19" />
      <line x1="5" y1="12" x2="19" y2="12" />
    </template>
    <template v-else-if="name === 'check'">
      <polyline points="4,12 9,17 20,6" />
    </template>
    <template v-else-if="name === 'warning'">
      <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
      <line x1="12" y1="9" x2="12" y2="13" />
      <line x1="12" y1="17" x2="12.01" y2="17" stroke-width="3" stroke-linecap="round" />
    </template>
    <template v-else-if="name === 'info'">
      <circle cx="12" cy="12" r="9" />
      <line x1="12" y1="8" x2="12.01" y2="8" stroke-width="3" stroke-linecap="round" />
      <polyline points="12,11 12,16" />
    </template>
    <!-- fallback: circle with question mark -->
    <template v-else>
      <circle cx="12" cy="12" r="9" />
      <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
      <line x1="12" y1="17" x2="12.01" y2="17" stroke-width="3" stroke-linecap="round" />
    </template>
  </svg>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  name: string
  size?: number
}>(), {
  size: 20,
})
</script>

<style scoped>
.svg-icon {
  flex-shrink: 0;
  display: block;
}
</style>
