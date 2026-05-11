<template>
  <div class="enterprise-list">
    <div class="page-toolbar">
      <div class="search-box">
        <input v-model="keyword" type="text" class="form-input focus-ring" placeholder="搜索企业名称..." @keyup.enter="searchImmediate" />
        <button class="search-btn pressable" @click="searchImmediate">搜索</button>
      </div>
    </div>

    <ErrorBanner
      v-if="error"
      :message="error"
      dismissable
      @retry="searchImmediate"
      @dismiss="error = ''"
    />

    <EmptyState
      v-if="!store.loading && store.enterprises.length === 0 && !error"
      icon="search"
      title="暂无企业数据"
      description="当前没有匹配的企业记录，请调整搜索条件或添加新企业"
    />

    <DataTable
      v-if="!store.loading && (store.enterprises.length > 0 || error)"
      :columns="columns"
      :data="store.enterprises"
      :loading="store.loading"
      :total="store.enterpriseTotal"
      :currentPage="page"
      :pageSize="size"
      @row-click="goDetail"
    >
      <template #cell-status="{ value }">
        <span class="status-badge" :class="value === 1 ? 'active' : 'inactive'">{{ value === 1 ? '正常' : '禁用' }}</span>
      </template>
      <template #cell-registeredCapital="{ value }">{{ (value as number)?.toLocaleString() }} 万</template>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import DataTable from '@/components/shared/DataTable.vue'
import ErrorBanner from '@/components/shared/ErrorBanner.vue'
import EmptyState from '@/components/shared/EmptyState.vue'
import type { Column } from '@/components/shared/DataTable.vue'
import { useApprovalStore } from '@/stores/approval'

const router = useRouter()
const store = useApprovalStore()
const keyword = ref('')
const page = ref(1)
const size = 10
const error = ref('')

const columns: Column[] = [
  { key: 'id', label: 'ID', width: '60px' },
  { key: 'name', label: '企业名称', width: '200px' },
  { key: 'creditCode', label: '信用代码' },
  { key: 'legalPerson', label: '法人' },
  { key: 'industry', label: '行业' },
  { key: 'registeredCapital', label: '注册资本' },
  { key: 'status', label: '状态', width: '80px' },
]

async function doSearch() {
  error.value = ''
  page.value = 1
  try {
    await store.fetchEnterprises(page.value, size, keyword.value)
  } catch {
    error.value = '获取企业列表失败，请重试'
  }
}

function searchImmediate() {
  doSearch()
}

function goDetail(row: Record<string, unknown>) {
  router.push(`/enterprises/${row.id}`)
}

let debounceTimer: ReturnType<typeof setTimeout>
watch(keyword, (val) => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    doSearch()
  }, 300)
})

onMounted(() => store.fetchEnterprises(page.value, size))
</script>

<style scoped>
.page-toolbar { margin-bottom: var(--space-6); }
.search-box { display: flex; gap: var(--space-3); max-width: 400px; }
.form-input { flex: 1; padding: var(--space-2) var(--space-4); background: var(--color-surface); border: var(--border-glass); border-radius: var(--radius-md); color: var(--color-text); font-size: var(--text-sm); outline: none; }
.form-input:focus { border-color: var(--color-primary); }
.search-btn { padding: var(--space-2) var(--space-5); background: var(--color-primary); color: white; border: none; border-radius: var(--radius-md); font-size: var(--text-sm); font-weight: 500; }
.status-badge { padding: 2px 8px; border-radius: var(--radius-full); font-size: var(--text-xs); font-weight: 500; }
.status-badge.active { background: rgba(16, 185, 129, 0.15); color: var(--color-success); }
.status-badge.inactive { background: rgba(239, 68, 68, 0.1); color: var(--color-danger); }
</style>
