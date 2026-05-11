<template>
  <div class="data-table-wrapper glass-card">
    <div v-if="loading" class="shimmer-skeleton">
      <div v-for="i in 5" :key="i" class="shimmer-row">
        <div
          v-for="j in columns.length"
          :key="j"
          class="shimmer-cell"
          :style="{ width: (Math.random() * 30 + 50) + '%' }"
        />
      </div>
    </div>
    <table v-else class="data-table">
      <thead>
        <tr>
          <th
            v-for="col in columns"
            :key="col.key"
            :style="{ width: col.width }"
            :class="{ sortable: col.sortable, pressable: col.sortable }"
            @click="col.sortable && toggleSort(col.key)"
          >
            <span class="th-content">
              {{ col.label }}
              <span v-if="col.sortable" class="sort-indicator">
                <span v-if="sortKey === col.key && sortDir === 'asc'" class="sort-arrow active">↑</span>
                <span v-else-if="sortKey === col.key && sortDir === 'desc'" class="sort-arrow active">↓</span>
                <span v-else class="sort-arrow">↕</span>
              </span>
            </span>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="data.length === 0">
          <td :colspan="columns.length" class="empty-cell">暂无数据</td>
        </tr>
        <tr
          v-for="(row, rowIdx) in data"
          :key="(row as any).id ?? rowIdx"
          class="data-row glow-border"
          :style="{ animationDelay: (rowIdx * 0.04).toFixed(2) + 's' }"
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
      <button class="pressable focus-ring" :disabled="currentPage <= 1" @click="$emit('pageChange', currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="pressable focus-ring" :disabled="currentPage >= totalPages" @click="$emit('pageChange', currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts" generic="T extends Record<string, unknown>">
import { computed, ref } from 'vue'

export interface Column {
  key: string
  label: string
  width?: string
  sortable?: boolean
}

const props = defineProps<{
  columns: Column[]
  data: T[]
  loading?: boolean
  total?: number
  pageSize?: number
  currentPage?: number
}>()

const emit = defineEmits<{
  rowClick: [row: T]
  pageChange: [page: number]
  sort: [payload: { key: string; direction: 'asc' | 'desc' | null }]
}>()

const totalPages = computed(() => Math.ceil((props.total || 0) / (props.pageSize || 10)))

const sortKey = ref<string | null>(null)
const sortDir = ref<'asc' | 'desc' | null>(null)

function toggleSort(key: string) {
  if (sortKey.value !== key) {
    sortKey.value = key
    sortDir.value = 'asc'
  } else if (sortDir.value === 'asc') {
    sortDir.value = 'desc'
  } else {
    sortKey.value = null
    sortDir.value = null
  }
  emit('sort', { key, direction: sortKey.value === key ? sortDir.value : null })
}

function formatCell(row: T, col: Column): string {
  const val = row[col.key]
  if (val === null || val === undefined) return '-'
  return String(val)
}
</script>

<style scoped>
.data-table-wrapper { overflow: hidden; }

.data-table {
  width: 100%;
  border-collapse: collapse;
}

thead th {
  position: sticky;
  top: 0;
  z-index: 1;
  padding: var(--space-3) var(--space-4);
  text-align: left;
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: var(--border-subtle);
  background: var(--color-bg-elevated);
}

th.sortable {
  cursor: pointer;
  user-select: none;
}

th.sortable:hover {
  color: var(--color-text-secondary);
}

.th-content {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
}

.sort-indicator {
  display: inline-flex;
  font-size: 0.7em;
}

.sort-arrow {
  opacity: 0.3;
  transition: opacity var(--duration-fast);
}

.sort-arrow.active {
  opacity: 1;
  color: var(--color-primary);
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
  animation: rowIn 0.4s var(--spring-standard) both;
}

.data-row:hover {
  background: var(--color-surface-hover);
}

.data-row:hover::after {
  opacity: 1;
  box-shadow: inset 0 0 0 1px rgba(99, 102, 241, 0.5), inset 0 0 20px rgba(99, 102, 241, 0.05);
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

/* Shimmer skeleton */
.shimmer-skeleton {
  overflow: hidden;
}

.shimmer-row {
  display: flex;
  gap: var(--space-4);
  padding: var(--space-3) var(--space-4);
  background: linear-gradient(
    90deg,
    var(--color-surface) 25%,
    var(--color-surface-hover) 50%,
    var(--color-surface) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.shimmer-cell {
  height: 20px;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Row stagger entrance */
@keyframes rowIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
